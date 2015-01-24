
package cn.zmdx.kaka.fast.locker.settings;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.notification.NotificationPreferences;
import cn.zmdx.kaka.fast.locker.notify.filter.AlphabetScrollerView;
import cn.zmdx.kaka.fast.locker.notify.filter.AlphabetScrollerView.OnEventListener;
import cn.zmdx.kaka.fast.locker.notify.filter.ListCompare;
import cn.zmdx.kaka.fast.locker.notify.filter.NotifyFilterManager;
import cn.zmdx.kaka.fast.locker.notify.filter.NotifyFilterManager.NotifyFilterEntity;
import cn.zmdx.kaka.fast.locker.notify.filter.NotifyFilterUtil;
import cn.zmdx.kaka.fast.locker.widget.material.design.ProgressBarCircularIndeterminate;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;

public class NotifyFilterActivity extends BaseActivity implements OnItemClickListener {

    public final static int TYPE_FILTER = 753;

    public final static int TYPE_SELECT = 357;

    private RelativeLayout mContentLayout;

    private ProgressBarCircularIndeterminate mLoadingView;

    private StickyGridHeadersGridView mNotifyGridView;

    private StickyGridHeadersSimpleArrayAdapter mNotifyFilterAdapter;

    private AlphabetScrollerView mAlphabetView;

    private ArrayList<NotifyFilterEntity> mNotifyDataList = new ArrayList<NotifyFilterEntity>();

    private ArrayList<NotifyFilterEntity> mNotifyInterceptList = new ArrayList<NotifyFilterEntity>();

    private int mCurType;

    private Context mContext;

    private LinearLayout mInterceptLayout;

    private GridView mInterceptGridView;

    private InterceptAdapter mInterceptAdapter;

    private boolean isEditMode = false;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_filter);
        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCurType = getIntent().getIntExtra("type", TYPE_FILTER);
        mPosition = getIntent().getIntExtra("position", -1);
        initView();
        new LoadDataTask().execute();

    }

    private void initView() {
        mContentLayout = (RelativeLayout) findViewById(R.id.notify_content);
        mLoadingView = (ProgressBarCircularIndeterminate) findViewById(R.id.notify_loading);

        mNotifyGridView = (StickyGridHeadersGridView) findViewById(R.id.notify_grid_view);
        mNotifyGridView.setHeadersIgnorePadding(true);
        mNotifyGridView.setAreHeadersSticky(false);

        mAlphabetView = (AlphabetScrollerView) findViewById(R.id.notify_alphabetView);
        mAlphabetView.init(mContentLayout,mNotifyGridView, this);
        mAlphabetView.setOnEventListener(new OnEventListener() {

            @Override
            public void onTouchDown() {
                String mCurrentLetter = mAlphabetView.getCurrentLetter();
                Log.d("syc", "mCurrentLetter=" + mCurrentLetter);
            }
        });

        mInterceptLayout = (LinearLayout) findViewById(R.id.notify_list_layout);

        if (mCurType == TYPE_FILTER) {
            mInterceptLayout.setVisibility(View.VISIBLE);
            mInterceptGridView = (GridView) findViewById(R.id.notify_list_grid_view);
        } else {
            mInterceptLayout.setVisibility(View.GONE);
            getSupportActionBar()
                    .setTitle(getResources().getString(R.string.notify_title_shortcut));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_notify_filter_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu
                .findItem(R.id.action_search));
        searchView.setOnQueryTextListener(mOnQueryTextListener);
        if (mCurType == TYPE_SELECT) {
            menu.findItem(R.id.action_edit).setVisible(false);
        } else {
            menu.findItem(R.id.action_edit).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            isEditMode = !isEditMode;
            mInterceptAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private final SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            if (mNotifyFilterAdapter != null && newText != null) {
                new ListFilter().filter(newText);
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
    };

    private class LoadDataTask extends AsyncTask<Void, Void, ArrayList<NotifyFilterEntity>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setVisibility(true);
        }

        @Override
        protected ArrayList<NotifyFilterEntity> doInBackground(Void... params) {
            Set<String> pkgNameSet = NotificationPreferences.getInstance(mContext)
                    .getInterceptPkgNames();
            if (mCurType == TYPE_FILTER) {
                mNotifyInterceptList = NotifyFilterManager.prepareInterceptList(mContext,
                        pkgNameSet);
                mNotifyDataList = NotifyFilterManager.prepareAppList(mContext, pkgNameSet, true);
            } else {
                mNotifyDataList = NotifyFilterManager.prepareAppList(mContext, pkgNameSet, false);
            }
            return mNotifyDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<NotifyFilterEntity> result) {
            if (!isCancelled()) {
                setVisibility(false);
                setAdapter(result);
            }
            super.onPostExecute(result);
        }
    }

    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String constraintStr = NotifyFilterUtil.getChinesePinyinStr(constraint.toString())
                    .toString().toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<NotifyFilterEntity> filterItems = new ArrayList<NotifyFilterEntity>();

                synchronized (this) {
                    for (NotifyFilterEntity item : mNotifyDataList) {
                        if (item.getNotifyUSName().toLowerCase(Locale.getDefault())
                                .startsWith(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            } else {
                synchronized (this) {
                    result.count = mNotifyDataList.size();
                    result.values = mNotifyDataList;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<NotifyFilterEntity> filtered = (ArrayList<NotifyFilterEntity>) results.values;
            setAdapter(filtered);
        }

    }

    private void setAdapter(ArrayList<NotifyFilterEntity> dataList) {
        ListCompare listCompare = new ListCompare();
        listCompare.initSortEntry(dataList);

        mNotifyFilterAdapter = new StickyGridHeadersSimpleArrayAdapter(this, dataList, listCompare);
        mNotifyGridView.setAdapter(mNotifyFilterAdapter);
        mNotifyGridView.setOnItemClickListener(this);

        if (mCurType == TYPE_FILTER) {
            if (null == mInterceptAdapter) {
                mInterceptAdapter = new InterceptAdapter(mInterceptGridView);
                mInterceptGridView.setAdapter(mInterceptAdapter);
            } else {
                mInterceptAdapter.notifyDataSetChanged();
            }
        }
    }

    private class InterceptAdapter extends BaseAdapter implements OnItemClickListener {
        private ViewHolder viewHolder = null;

        private InterceptAdapter(GridView gridView) {
            gridView.setOnItemClickListener(this);
        }

        @Override
        public int getCount() {
            return mNotifyInterceptList.size();
        }

        @Override
        public NotifyFilterEntity getItem(int position) {
            return mNotifyInterceptList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            private ImageView mNotifyAppIcon;

            private ImageView mNotifySelect;

            private TextView mNotifyAppName;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.activity_notify_filter_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mNotifyAppIcon = (ImageView) convertView
                        .findViewById(R.id.notify_app_icon);
                viewHolder.mNotifyAppName = (TextView) convertView
                        .findViewById(R.id.notify_app_name);
                viewHolder.mNotifySelect = (ImageView) convertView
                        .findViewById(R.id.notify_app_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            NotifyFilterEntity item = mNotifyInterceptList.get(position);
            viewHolder.mNotifyAppIcon.setImageDrawable(item.getNotifyIcon());
            viewHolder.mNotifyAppName.setText(item.getNotifyCHName());

            if (isEditMode) {
                if (position == mNotifyInterceptList.size() - 1) {
                    viewHolder.mNotifySelect.setVisibility(View.GONE);
                } else {
                    viewHolder.mNotifySelect.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.mNotifySelect.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isEditMode) {
                if (position != mNotifyInterceptList.size() - 1) {
                    for (NotifyFilterEntity entity : mNotifyDataList) {
                        if (entity.getPkgName().equals(
                                mNotifyInterceptList.get(position).getPkgName())) {
                            entity.setSelect(false);
                            break;
                        }
                    }
                    removeInterceptPkgName(mNotifyInterceptList.get(position).getPkgName());
                    mNotifyInterceptList.remove(mNotifyInterceptList.get(position));
                    mInterceptAdapter.notifyDataSetChanged();
                    mNotifyFilterAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    private void setVisibility(boolean isLoading) {
        if (isLoading) {
            mContentLayout.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            mContentLayout.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NotifyFilterEntity itme = mNotifyDataList.get(position);
        switch (mCurType) {
            case TYPE_FILTER:
                if (itme.isSelect()) {
                    itme.setSelect(false);
                    NotifyFilterEntity needRemove = new NotifyFilterEntity();
                    for (NotifyFilterEntity entity : mNotifyDataList) {
                        if (entity.getPkgName().equals(itme.getPkgName())) {
                            needRemove = entity;
                            break;
                        }
                    }
                    mNotifyInterceptList.remove(needRemove);
                    removeInterceptPkgName(itme.getPkgName());
                } else {
                    itme.setSelect(true);
                    mNotifyInterceptList.add(mNotifyInterceptList.size() - 1, itme);
                    putInterceptPkgName(itme.getPkgName());
                }
                mNotifyFilterAdapter.notifyDataSetChanged();
                mInterceptAdapter.notifyDataSetChanged();
                break;
            case TYPE_SELECT:
                Intent in = new Intent();
                in.putExtra("position", mPosition);
                in.putExtra("pkgName", itme.getPkgName());
                setResult(RESULT_OK, in);
                onBackPressed();
                break;

            default:
                break;
        }
    }

    private void removeInterceptPkgName(String pkgName) {
        NotificationPreferences.getInstance(mContext).removeInterceptPkgName(pkgName);
    }

    private void putInterceptPkgName(String pkgName) {
        NotificationPreferences.getInstance(mContext).putInterceptPkgName(pkgName);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}

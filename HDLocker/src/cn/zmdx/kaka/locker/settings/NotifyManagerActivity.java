
package cn.zmdx.kaka.locker.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.notification.NotificationPreferences;
import cn.zmdx.kaka.locker.notify.filter.AlphabetScrollerView;
import cn.zmdx.kaka.locker.notify.filter.AlphabetScrollerView.OnEventListener;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterManager;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterManager.NotifyFilterEntity;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterUtil;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.ProgressBarMaterial;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;

public class NotifyManagerActivity extends ActionBarActivity implements OnItemClickListener {

    public final static int TYPE_FILTER = 753;

    public final static int TYPE_SELECT = 357;

    private RelativeLayout mContentLayout;

    private ProgressBarMaterial mLoadingView;

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

    private boolean isSearchMode = false;

    private SearchView mSearchView;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_filter);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg));
        mContext = this;
        mCurType = getIntent().getIntExtra("type", TYPE_FILTER);
        mPosition = getIntent().getIntExtra("position", -1);
        initView();
        new LoadDataTask().execute();

    }

    private void initView() {
        SparseIntArray sparseIntArray = NotifyFilterUtil.initAppSize(this);
        int padding = sparseIntArray.get(NotifyFilterUtil.KEY_GRIDVIEW_PADDING);
        mContentLayout = (RelativeLayout) findViewById(R.id.notify_content);
        mLoadingView = (ProgressBarMaterial) findViewById(R.id.notify_loading);

        mNotifyGridView = (StickyGridHeadersGridView) findViewById(R.id.notify_grid_view);
        mNotifyGridView.setHeadersIgnorePadding(true);
        mNotifyGridView.setAreHeadersSticky(false);
        mNotifyGridView.setPadding(padding, 0, padding, 0);

        mAlphabetView = (AlphabetScrollerView) findViewById(R.id.notify_alphabetView);
        mAlphabetView.init(mContentLayout, mNotifyGridView, this);
        mAlphabetView.setOnEventListener(new OnEventListener() {

            @Override
            public void onTouchDown() {
                // String mCurrentLetter = mAlphabetView.getCurrentLetter();
            }
        });

        mInterceptLayout = (LinearLayout) findViewById(R.id.notify_list_layout);

        if (mCurType == TYPE_FILTER) {
            mInterceptLayout.setVisibility(View.VISIBLE);
            mInterceptGridView = (GridView) findViewById(R.id.notify_list_grid_view);
            int gridViewHeight = sparseIntArray.get(NotifyFilterUtil.KEY_GRIDVIEW_HEIGHT);
            int headPaddingLeft = sparseIntArray.get(NotifyFilterUtil.KEY_HEAD_PADDING_LEFT);
            findViewById(R.id.notify_list_prompt).setPadding(headPaddingLeft, 25, 0, 25);
            LayoutParams params = mInterceptGridView.getLayoutParams();
            params.width = LayoutParams.MATCH_PARENT;
            params.height = gridViewHeight;
            mInterceptGridView.setLayoutParams(params);
            mInterceptGridView.setPadding(padding, 0, padding, 0);
        } else {
            mInterceptLayout.setVisibility(View.GONE);
        }

    }

    // public boolean onCreateOptionsMenu(Menu menu) {
    // MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.activity_notify_filter_menu, menu);
    // mSearchView = (SearchView)
    // MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
    // Class<?> argClass = mSearchView.getClass();
    // // 指定某个私有属性
    // Field ownField;
    // try {
    // ownField = argClass.getDeclaredField("mSearchPlate");
    // // setAccessible 它是用来设置是否有权限访问反射类中的私有属性的，只有设置为true时才可以访问，默认为false
    // ownField.setAccessible(true);
    // View mView = (View) ownField.get(mSearchView);
    // mView.setBackgroundResource(R.drawable.texfield_searchview_holo_light);
    // } catch (NoSuchFieldException e) {
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // e.printStackTrace();
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // }
    //
    // mSearchView.setOnQueryTextListener(mOnQueryTextListener);
    // if (mCurType == TYPE_SELECT) {
    // menu.findItem(R.id.action_edit).setVisible(false);
    // } else {
    // menu.findItem(R.id.action_edit).setVisible(true);
    // }
    // return super.onCreateOptionsMenu(menu);
    // }

    // public boolean onOptionsItemSelected(MenuItem item) {
    // if (item.getItemId() == R.id.action_edit) {
    // isEditMode = !isEditMode;
    // mInterceptAdapter.notifyDataSetChanged();
    // } else if (item.getItemId() == android.R.id.home) {
    // onBackPressed();
    // }
    // return true;
    // }

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
            if (!TextUtils.isEmpty(constraint)) {
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
                isSearchMode = true;
            } else {
                synchronized (this) {
                    result.count = mNotifyDataList.size();
                    result.values = mNotifyDataList;
                }
                isSearchMode = false;
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
        // ListCompare listCompare = new ListCompare();
        // listCompare.initSortEntry(dataList);

        mNotifyFilterAdapter = new StickyGridHeadersSimpleArrayAdapter(this, dataList);
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
            private RelativeLayout mNotifyAppLayout;

            private ImageView mNotifyAppIcon;

            private ImageView mNotifySelect;

            private TypefaceTextView mNotifyAppName;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SparseIntArray sparseIntArray = NotifyFilterUtil.initAppSize(mContext);
            int layoutWidth = sparseIntArray.get(NotifyFilterUtil.KEY_LAYOUT_WIDTH);
            int imageWidth = sparseIntArray.get(NotifyFilterUtil.KEY_IMAGE_WIDTH);
            int imageHeight = sparseIntArray.get(NotifyFilterUtil.KEY_IMAGE_HEIGHT);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.activity_notify_filter_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mNotifyAppLayout = (RelativeLayout) convertView
                        .findViewById(R.id.notify_app_layout);
                viewHolder.mNotifyAppIcon = (ImageView) convertView
                        .findViewById(R.id.notify_app_icon);
                viewHolder.mNotifyAppName = (TypefaceTextView) convertView
                        .findViewById(R.id.notify_app_name);
                viewHolder.mNotifySelect = (ImageView) convertView
                        .findViewById(R.id.notify_app_select);
                viewHolder.mNotifySelect.setPadding(0, 0, (layoutWidth - imageWidth) / 2, 0);
                convertView.setTag(viewHolder);

                LayoutParams layoutParams = viewHolder.mNotifyAppLayout.getLayoutParams();
                layoutParams.width = layoutWidth;
                layoutParams.height = LayoutParams.WRAP_CONTENT;
                viewHolder.mNotifyAppLayout.setLayoutParams(layoutParams);

                LayoutParams params = viewHolder.mNotifyAppIcon.getLayoutParams();
                params.width = imageWidth;
                params.height = imageHeight;
                viewHolder.mNotifyAppIcon.setLayoutParams(params);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mNotifySelect.setImageDrawable(getResources().getDrawable(
                    R.drawable.notify_filter_delete));
            NotifyFilterEntity item = mNotifyInterceptList.get(position);
            viewHolder.mNotifyAppIcon.setImageBitmap(ImageUtils.getResizedBitmap(
                    ImageUtils.drawable2Bitmap(item.getNotifyIcon()), imageWidth, imageHeight));
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

            if (mNotifyInterceptList.size() > 4) {
                int gridViewHeight = sparseIntArray.get(NotifyFilterUtil.KEY_GRIDVIEW_HEIGHT);
                LayoutParams params = mInterceptGridView.getLayoutParams();
                params.width = LayoutParams.MATCH_PARENT;
                params.height = gridViewHeight;
                mInterceptGridView.setLayoutParams(params);
            } else {
                LayoutParams params = mInterceptGridView.getLayoutParams();
                params.width = LayoutParams.MATCH_PARENT;
                params.height = LayoutParams.WRAP_CONTENT;
                mInterceptGridView.setLayoutParams(params);
            }

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isEditMode) {
                if (position != mNotifyInterceptList.size() - 1) {
                    NotifyFilterEntity item = mNotifyInterceptList.get(position);
                    setAppSelectState(item.getPkgName(), false);

                    removeInterceptPkgName(item.getPkgName());
                    mNotifyInterceptList.remove(item);
                    mInterceptAdapter.notifyDataSetChanged();
                    mNotifyFilterAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final NotifyFilterEntity item = mNotifyFilterAdapter.getAdapterData().get(position);
        switch (mCurType) {
            case TYPE_FILTER:
                if (item.isSelect()) {
                    setAppSelectState(item.getPkgName(), false);

                    ArrayList<NotifyFilterEntity> needRemoveList = new ArrayList<NotifyFilterEntity>();
                    for (NotifyFilterEntity entity : mNotifyInterceptList) {
                        if (!TextUtils.isEmpty(entity.getPkgName())) {
                            if (entity.getPkgName().equals(item.getPkgName())) {
                                NotifyFilterEntity needRemove = entity;
                                needRemoveList.add(needRemove);
                            }
                        }
                    }
                    mNotifyInterceptList.removeAll(needRemoveList);
                    removeInterceptPkgName(item.getPkgName());
                } else {
                    setAppSelectState(item.getPkgName(), true);
                    mNotifyInterceptList.add(mNotifyInterceptList.size() - 1, item);
                    putInterceptPkgName(item.getPkgName());
                    setSelection(mNotifyInterceptList.size() - 1);
                }
                mNotifyFilterAdapter.notifyDataSetChanged();
                mInterceptAdapter.notifyDataSetChanged();

                break;
            case TYPE_SELECT:
                Intent in = new Intent();
                in.putExtra("position", mPosition);
                in.putExtra("pkgName", item.getPkgName());
                setResult(RESULT_OK, in);
                onBackPressed();
                break;

            default:
                break;
        }
    }

    private void setSelection(final int postion) {
        HDBThreadUtils.postOnWorkerDelayed(new Runnable() {

            @Override
            public void run() {
                HDBThreadUtils.runOnUi(new Runnable() {

                    @Override
                    public void run() {
                        mInterceptGridView.setSelection(postion);
                    }
                });
            }
        }, 100);
    }

    private void setAppSelectState(String itemPkgName, boolean isSelect) {
        for (NotifyFilterEntity entity : mNotifyFilterAdapter.getAdapterData()) {
            if (entity.getPkgName().equals(itemPkgName)) {
                entity.setSelect(isSelect);
            }
        }

        for (NotifyFilterEntity entity : mNotifyDataList) {
            if (entity.getPkgName().equals(itemPkgName)) {
                entity.setSelect(isSelect);
            }
        }
    }

    private void removeInterceptPkgName(String pkgName) {
        NotificationPreferences.getInstance(mContext).removeInterceptPkgName(pkgName);
    }

    private void putInterceptPkgName(String pkgName) {
        NotificationPreferences.getInstance(mContext).putInterceptPkgName(pkgName);
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
    public void onBackPressed() {
        if (isEditMode) {
            isEditMode = !isEditMode;
            mInterceptAdapter.notifyDataSetChanged();
        } else if (isSearchMode) {
            if (mNotifyFilterAdapter != null) {
                new ListFilter().filter("");
                mSearchView.setIconified(true);
                mSearchView.clearFocus();
            }
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                    R.anim.umeng_fb_slide_out_from_right);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Set<String> pkgNameSet = NotificationPreferences.getInstance(mContext)
                .getInterceptPkgNames();
        String notifyFilterApp = "|";
        for (Iterator iterator = pkgNameSet.iterator(); iterator.hasNext();) {
            notifyFilterApp += (String) iterator.next() + "|";
        }
        // UmengCustomEventManager.statisticalNotifyFilterApps(notifyFilterApp);
    }
}

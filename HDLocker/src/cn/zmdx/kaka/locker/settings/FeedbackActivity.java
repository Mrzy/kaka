
package cn.zmdx.kaka.locker.settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

public class FeedbackActivity extends ActionBarActivity {

    private ListView mListView;

    private FeedbackAgent mAgent;

    private Conversation mComversation;

    private Context mContext;

    private ReplyAdapter adapter;

    private Button sendBtn;

    private EditText inputEdit;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final int VIEW_TYPE_COUNT = 2;

    private final int VIEW_TYPE_USER = 0;

    private final int VIEW_TYPE_DEV = 1;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_feedback_activity);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;

        initView();
        mAgent = new FeedbackAgent(this);
        mComversation = mAgent.getDefaultConversation();
        adapter = new ReplyAdapter();
        mListView.setAdapter(adapter);
        sync();

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.fb_reply_list);
        sendBtn = (Button) findViewById(R.id.fb_send_btn);
        inputEdit = (EditText) findViewById(R.id.fb_send_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fb_reply_refresh);

        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = inputEdit.getText().toString();
                inputEdit.getEditableText().clear();
                if (!TextUtils.isEmpty(content)) {
                    // 将内容添加到会话列表
                    mComversation.addUserReply(content);
                    // 刷新ListView
                    adapter.notifyDataSetChanged();
                    scrollToBottom();
                    // 数据同步
                    sync();
                }
            }
        });

        // 下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });
    }

    // 数据同步
    private void sync() {

        mComversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                // SwipeRefreshLayout停止刷新
                mSwipeRefreshLayout.setRefreshing(false);
                // 刷新ListView
                adapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        if (adapter.getCount() > 0) {
            mListView.smoothScrollToPosition(adapter.getCount());
        }
    }

    // adapter
    class ReplyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mComversation.getReplyList().size();
        }

        @Override
        public Object getItem(int arg0) {
            return mComversation.getReplyList().get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getViewTypeCount() {
            // 两种不同的Item布局
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            // 获取单条回复
            Reply reply = mComversation.getReplyList().get(position);
            if (Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                // 开发者回复Item布局
                return VIEW_TYPE_DEV;
            } else {
                // 用户反馈、回复Item布局
                return VIEW_TYPE_USER;
            }
        }

        @SuppressLint({
                "SimpleDateFormat", "InflateParams"
        })
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            // 获取单条回复
            Reply reply = mComversation.getReplyList().get(position);
            if (convertView == null) {
                // 根据Type的类型来加载不同的Item布局
                if (Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                    // 开发者的回复
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.custom_fb_dev_reply, null);
                } else {
                    // 用户的反馈、回复
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.custom_fb_user_reply, null);
                }

                // 创建ViewHolder并获取各种View
                holder = new ViewHolder();
                holder.replyContent = (TextView) convertView.findViewById(R.id.fb_reply_content);
                holder.replyProgressBar = (ProgressBar) convertView
                        .findViewById(R.id.fb_reply_progressBar);
                holder.replyStateFailed = (ImageView) convertView
                        .findViewById(R.id.fb_reply_state_failed);
                holder.replyData = (TextView) convertView.findViewById(R.id.fb_reply_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 以下是填充数据
            // 设置Reply的内容
            holder.replyContent.setText(reply.content);
            // 在App应用界面，对于开发者的Reply来讲status没有意义
            if (!Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                // 根据Reply的状态来设置replyStateFailed的状态
                if (Reply.STATUS_NOT_SENT.equals(reply.status)) {
                    holder.replyStateFailed.setVisibility(View.VISIBLE);
                } else {
                    holder.replyStateFailed.setVisibility(View.GONE);
                }

                // 根据Reply的状态来设置replyProgressBar的状态
                if (Reply.STATUS_SENDING.equals(reply.status)) {
                    holder.replyProgressBar.setVisibility(View.VISIBLE);
                } else {
                    holder.replyProgressBar.setVisibility(View.GONE);
                }
            }

            // 回复的时间数据，两条Reply之间相差100000ms则展示时间
            if ((position + 1) < mComversation.getReplyList().size()) {
                Reply nextReply = mComversation.getReplyList().get(position + 1);
                if (nextReply.created_at - reply.created_at > 100000) {
                    Date replyTime = new Date(reply.created_at);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    holder.replyData.setText(sdf.format(replyTime));
                    holder.replyData.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView replyContent;

            ProgressBar replyProgressBar;

            ImageView replyStateFailed;

            TextView replyData;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FeedbackActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FeedbackActivity");
        MobclickAgent.onPause(this);
    }
}

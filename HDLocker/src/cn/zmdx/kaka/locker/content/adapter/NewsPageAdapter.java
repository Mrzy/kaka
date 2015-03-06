
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;

public class NewsPageAdapter extends RecyclerView.Adapter<NewsPageAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.news_item_title);
        }
    }

    private List<String> mNews;

    public NewsPageAdapter(List<String> news) {
        mNews = news;
    }

    public String getValueAt(int position) {
        return mNews.get(position);
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mNews.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(mNews.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View view = LayoutInflater.from(vg.getContext()).inflate(R.layout.news_page_item_layout,
                vg, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}

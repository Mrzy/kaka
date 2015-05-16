
package cn.zmdx.kaka.locker.content.channel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.BottomDockUmengEventManager;

public class ChannelBoxManager {

    public static final int CHANNEL_WALLPAPER = 0;

    public static final int CHANNEL_HEADLINES = 1;

    public static final int CHANNEL_GOSSIP = 2;

    public static final int CHANNEL_MICRO = 3;

    public static final int CHANNEL_BEAUTY = 4;

    public static final int CHANNEL_FUNNY = 5;

    // 财经
    public static final int CHANNEL_FINANCE = 6;

    // 体育
    public static final int CHANNEL_SPORTS = 7;

    // 时尚
    public static final int CHANNEL_FASHION = 8;

    // 潮应用
    public static final int CHANNEL_APP = 9;

    // 科技
    public static final int CHANNEL_TECHNOLOGY = 10;

    // 游戏
    public static final int CHANNEL_GAME = 11;

    // 创业
    public static final int CHANNEL_BUSINESS = 12;

    private static ChannelBoxManager INSTANCE;

    private static final String PREFERENCE_NAME = "yuchajjgh";

    private static final String PREFER_KEY_SELECTED_CHANNELS = "pksc";

    private Context mContext;

    private SharedPreferences mPreference;

    private static final String DEFAULT_CHANNEL = "2,1,4,5,7,0";

    private ChannelBoxManager(Context context) {
        mContext = context;
        mPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static ChannelBoxManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ChannelBoxManager(context);
        }
        return INSTANCE;
    }

    public List<ChannelInfo> getAllChannels() {
        List<ChannelInfo> selected = getSelectedChannels();
        final List<ChannelInfo> set = new ArrayList<ChannelInfo>();
        ChannelInfo ci = null;

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_HEADLINES);
        ci.setChannelName(getChannelNameById(CHANNEL_HEADLINES));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_toutiao);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_GOSSIP);
        ci.setChannelName(getChannelNameById(CHANNEL_GOSSIP));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_bagua);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_MICRO);
        ci.setChannelName(getChannelNameById(CHANNEL_MICRO));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_weijingxuan);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_BEAUTY);
        ci.setChannelName(getChannelNameById(CHANNEL_BEAUTY));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_meinv);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_FUNNY);
        ci.setChannelName(getChannelNameById(CHANNEL_FUNNY));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_gaoxiao);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_FINANCE);
        ci.setChannelName(getChannelNameById(CHANNEL_FINANCE));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_caijing);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_SPORTS);
        ci.setChannelName(getChannelNameById(CHANNEL_SPORTS));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_tiyu);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_FASHION);
        ci.setChannelName(getChannelNameById(CHANNEL_FASHION));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_shishang);
        set.add(ci);

        // ci = new ChannelInfo();
        // ci.setChannelId(CHANNEL_APP);
        // ci.setChannelName(getChannelNameById(CHANNEL_APP));
        // ci.setSelected(selected.contains(ci));
        // ci.setChannelImgResId(R.drawable.channel_bg_app);
        // set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_TECHNOLOGY);
        ci.setChannelName(getChannelNameById(CHANNEL_TECHNOLOGY));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_keji);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_GAME);
        ci.setChannelName(getChannelNameById(CHANNEL_GAME));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_youxi);
        set.add(ci);

        ci = new ChannelInfo();
        ci.setChannelId(CHANNEL_BUSINESS);
        ci.setChannelName(getChannelNameById(CHANNEL_BUSINESS));
        ci.setSelected(selected.contains(ci));
        ci.setChannelImgResId(R.drawable.channel_bg_chuangye);
        set.add(ci);
        return set;
    }

    public View createChannelView() {
        return new ChannelBoxView(mContext, this);
    }

    public void saveSelectedChannels(List<ChannelInfo> channels) {
        StringBuffer sb = new StringBuffer();
        for (ChannelInfo ci : channels) {
            sb.append(ci.getChannelId() + ",");
        }
        mPreference.edit().putString(PREFER_KEY_SELECTED_CHANNELS, sb.toString()).commit();
        BottomDockUmengEventManager.statisticalSelectedChannel(channels);
    }

    public List<ChannelInfo> getSelectedChannels() {
        List<ChannelInfo> result = new ArrayList<ChannelInfo>();
        String str = mPreference.getString(PREFER_KEY_SELECTED_CHANNELS, DEFAULT_CHANNEL);
        if (!TextUtils.isEmpty(str)) {
            String[] strs = str.split(",");
            for (String s : strs) {
                try {
                    int id = Integer.parseInt(s);
                    ChannelInfo ci = new ChannelInfo();
                    ci.setChannelId(id);
                    ci.setChannelName(getChannelNameById(id));
                    ci.setSelected(true);
                    result.add(ci);
                } catch (Exception e) {
                    continue;
                }
            }
        }

        // 针对老用户，把壁纸放到最后一位
        if (result.size() > 0
                && result.get(0).getChannelId() == ChannelBoxManager.CHANNEL_WALLPAPER) {
            result.remove(0);
            if (result.get(result.size() - 1).getChannelId() != CHANNEL_WALLPAPER) {
                ChannelInfo ci = new ChannelInfo();
                ci.setChannelId(CHANNEL_WALLPAPER);
                ci.setChannelName(getChannelNameById(CHANNEL_WALLPAPER));
                ci.setSelected(true);
                result.add(ci);
            }
        }
        return result;
    }

    public String getChannelNameById(int id) {
        String result = "";
        switch (id) {
            case CHANNEL_WALLPAPER:
                result = mContext.getString(R.string.pandora_news_classify_wallpaper);
                break;
            case CHANNEL_HEADLINES:
                result = mContext.getString(R.string.pandora_news_classify_headlines);
                break;
            case CHANNEL_GOSSIP:
                result = mContext.getString(R.string.pandora_news_classify_gossip);
                break;
            case CHANNEL_MICRO:
                result = mContext.getString(R.string.pandora_news_classify_micro_choice);
                break;
            case CHANNEL_BEAUTY:
                result = mContext.getString(R.string.pandora_news_classify_beauty);
                break;
            case CHANNEL_FUNNY:
                result = mContext.getString(R.string.pandora_news_classify_funny);
                break;
            case CHANNEL_FINANCE:
                result = mContext.getString(R.string.pandora_news_classify_finance);
                break;
            case CHANNEL_SPORTS:
                result = mContext.getString(R.string.pandora_news_classify_sports);
                break;
            case CHANNEL_FASHION:
                result = mContext.getString(R.string.pandora_news_classify_fashion);
                break;
            case CHANNEL_APP:
                result = mContext.getString(R.string.pandora_news_classify_app);
                break;
            case CHANNEL_TECHNOLOGY:
                result = mContext.getString(R.string.pandora_news_classify_technology);
                break;
            case CHANNEL_GAME:
                result = mContext.getString(R.string.pandora_news_classify_game);
                break;
            case CHANNEL_BUSINESS:
                result = mContext.getString(R.string.pandora_news_classify_bussiness);
                break;
            default:
        }
        return result;
    }

    public List<ChannelInfo> getUnSelectedChannels() {
        List<ChannelInfo> selected = getSelectedChannels();
        List<ChannelInfo> allChannel = getAllChannels();
        List<ChannelInfo> result = new ArrayList<ChannelInfo>();
        for (ChannelInfo ci : selected) {
            if (!allChannel.contains(ci)) {
                result.add(ci);
            }
        }
        return allChannel;
    }
}

package cn.zmdx.kaka.locker.content;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.zmdx.kaka.locker.content.channel.ChannelBoxManager;

public class ChannelPageFactory {

    private static Map<Integer, WeakReference<ChannelPageGenerator>> sPageGenCache = new HashMap<Integer, WeakReference<ChannelPageGenerator>>();

    public static ChannelPageGenerator createPageGenerator(PandoraBoxManager boxManager, int channelId) {
        ChannelPageGenerator cpg = null;
            final WeakReference<ChannelPageGenerator> wr = sPageGenCache.get(channelId);
            if (wr != null && wr.get() != null) {
                cpg = wr.get();
            } else {
                int color = getColorByChannelId(channelId);
                if (channelId == ChannelBoxManager.CHANNEL_WALLPAPER) {
                    cpg = new ChannelPageGenerator(boxManager, channelId, ChannelPageGenerator.NEWS_THEME_WALLPAPER, color);
                } else if (channelId == ChannelBoxManager.CHANNEL_HEADLINES || channelId == ChannelBoxManager.CHANNEL_MICRO || channelId == ChannelBoxManager.CHANNEL_FINANCE) {
                    cpg = new ChannelPageGenerator(boxManager, channelId, ChannelPageGenerator.NEWS_THEME_LIST, color);
                } else {
                    cpg = new ChannelPageGenerator(boxManager, channelId, ChannelPageGenerator.NEWS_THEME_STAGGERED, color);
                }
                sPageGenCache.put(channelId, new WeakReference<ChannelPageGenerator>(cpg));
            }
        return cpg;
    }

    public static int getColorByChannelId(int channelId) {
        return PandoraBoxManager.mTabColors[channelId];
    }

    /**
     * 
     * @param channelId
     * @return ChannelPageGenerator 可能为null
     */
    public static ChannelPageGenerator getPageGenerator(int channelId) {
        ChannelPageGenerator cpg = null;
        final WeakReference<ChannelPageGenerator> wr = sPageGenCache.get(channelId);
        if (wr != null) {
            cpg = wr.get();
        }
        return cpg;
    }
}


package cn.zmdx.kaka.locker.content;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.channel.ChannelBoxManager;

public class ChannelPageFactory {

    private static Map<Integer, WeakReference<ChannelPageGenerator>> sPageGenCache = new HashMap<Integer, WeakReference<ChannelPageGenerator>>();

    private static List<ServerImageData> sNewsHeaderData = new LinkedList<ServerImageData>();

    private static final int NEWS_HEADER_CACHEDDATA_MAX_COUNT = 10;

    public static List<ServerImageData> getNewsHeaderData() {
        return sNewsHeaderData;
    }

    public static void addNewsHeaderData(ServerImageData sid) {
        int size = sNewsHeaderData.size();
        if (size >= NEWS_HEADER_CACHEDDATA_MAX_COUNT) {
            sNewsHeaderData.remove(size - 1);
        }
        sNewsHeaderData.add(0, sid);
    }

    public static ChannelPageGenerator createPageGenerator(PandoraBoxManager boxManager,
            int channelId) {
        ChannelPageGenerator cpg = null;
        final WeakReference<ChannelPageGenerator> wr = sPageGenCache.get(channelId);
        if (wr != null && wr.get() != null) {
            cpg = wr.get();
        } else {
            int color = getColorByChannelId(channelId);
            if (channelId == ChannelBoxManager.CHANNEL_WALLPAPER) {
                cpg = new ChannelPageGenerator(boxManager, channelId,
                        ChannelPageGenerator.NEWS_THEME_WALLPAPER, color);
            } else if (channelId == ChannelBoxManager.CHANNEL_HEADLINES
                    || channelId == ChannelBoxManager.CHANNEL_MICRO
                    || channelId == ChannelBoxManager.CHANNEL_FINANCE) {
                cpg = new ChannelPageGenerator(boxManager, channelId,
                        ChannelPageGenerator.NEWS_THEME_LIST, color);
            } else {
                cpg = new ChannelPageGenerator(boxManager, channelId,
                        ChannelPageGenerator.NEWS_THEME_STAGGERED, color);
            }
            sPageGenCache.put(channelId, new WeakReference<ChannelPageGenerator>(cpg));
        }
        return cpg;
    }

    public static void forceRelease() {
        sPageGenCache.clear();
    }

    public static int getColorByChannelId(int channelId) {
        return PandoraBoxManager.mTabColors[channelId];
    }

    /**
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

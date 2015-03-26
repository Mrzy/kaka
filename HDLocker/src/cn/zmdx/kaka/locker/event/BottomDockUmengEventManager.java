
package cn.zmdx.kaka.locker.event;

import java.util.HashMap;

import cn.zmdx.kaka.locker.HDApplication;

import com.umeng.analytics.MobclickAgent;

public class BottomDockUmengEventManager {

    public static final String EVENT_NEWS_PANEL_EXPANDED = "newsPanelExpanded";// 新闻面板拉开

    public static final String EVENT_NEWS_PANEL_COLLAPSED = "newsPanelCollapsed";// 新闻面板收起

    public static final String EVENT_NEWS_DETAIL_PAGE_BACK_CLICKED = "newsDetailPageBackClicked";// 新闻详情页点击左下角返回键

    public static final String EVENT_NEWS_DETAIL_PAGE_FAVORITE_CLICKED = "newsDetailPageFavoriteClicked";// 点击喜欢按钮

    public static final String EVENT_NEWS_PANEL_BACK_CLICKED = "newsPanelBackClicked";// 点击新闻页右下角圈圈收起新闻面板

    public static final String EVENT_NEWS_DETAIL_PAGE_RIGHT_SLIDE_TO_BACK = "newsDetailPageRightSlideToBack";// 新闻详情页右划返回

    /**
     * 统计新闻面板打开次数
     */
    public static void statisticalNewsPanelExpanded() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                BottomDockUmengEventManager.EVENT_NEWS_PANEL_EXPANDED);
    }

    /**
     * 统计新闻面板收起次数
     */
    public static void statisticalNewsPanelCollapsed() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                BottomDockUmengEventManager.EVENT_NEWS_PANEL_COLLAPSED);
    }

    /**
     * 统计新闻详情页点击左下角返回键次数
     */
    public static void statisticalNewsDetailPageBackClicked() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                BottomDockUmengEventManager.EVENT_NEWS_DETAIL_PAGE_BACK_CLICKED);
    }

    /**
     * 统计新闻详情页点击喜欢按钮次数
     */
    public static void statisticalNewsDetailFavoriteClicked(String newsId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("newsId", newsId);
        MobclickAgent.onEvent(HDApplication.getContext(),
                BottomDockUmengEventManager.EVENT_NEWS_DETAIL_PAGE_FAVORITE_CLICKED, newsId);
    }

    /**
     * 统计点击新闻页右下角圈圈收起新闻面板次数
     */
    public static void statisticalNewsPanelBackClicked() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                BottomDockUmengEventManager.EVENT_NEWS_PANEL_BACK_CLICKED);
    }

    /**
     * 统计新闻详情页右划返回次数
     */
    public static void statisticalNewsDetailPageRightSlideToBack() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                BottomDockUmengEventManager.EVENT_NEWS_DETAIL_PAGE_RIGHT_SLIDE_TO_BACK);
    }

}

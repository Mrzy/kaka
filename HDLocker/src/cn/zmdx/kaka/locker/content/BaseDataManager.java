
package cn.zmdx.kaka.locker.content;

import org.json.JSONObject;

import cn.zmdx.kaka.locker.database.ServerImageDataModel;

public class BaseDataManager {

    public int mId;

    public String mCloudId;

    public String mTitle;

    /**
     * 赞的数量
     */
    public String mTop;

    /**
     * 是否已读标记
     */

    public String mRead;

    /**
     * 采集日期
     */
    public String mCollectTime;

    /**
     * 发布时间
     */
    public String mReleaseTime;

    public String mCollectWebsite;

    public String mDataType;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getCloudId() {
        return mCloudId;
    }

    public void setCloudId(String mCloudId) {
        this.mCloudId = mCloudId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTop() {
        return mTop;
    }

    public void setTop(String mTop) {
        this.mTop = mTop;
    }

    public String getRead() {
        return mRead;
    }

    public void setRead(String read) {
        this.mRead = read;
    }

    public String getCollectTime() {
        return mCollectTime;
    }

    public void setCollectTime(String mCollectTime) {
        this.mCollectTime = mCollectTime;
    }

    public String getReleaseTime() {
        return mReleaseTime;
    }

    public void setReleaseTime(String mReleaseTime) {
        this.mReleaseTime = mReleaseTime;
    }

    public String getCollectWebsite() {
        return mCollectWebsite;
    }

    public void setCollectWebsite(String mCollectWebsite) {
        this.mCollectWebsite = mCollectWebsite;
    }

    public String getDataType() {
        return mDataType;
    }

    public void setDataType(String mDataType) {
        this.mDataType = mDataType;
    }

    public void parseBaseJson(JSONObject jsonObject) {
        this.mCloudId = jsonObject.optString("id");
        mTitle = jsonObject.optString("title");
        mTop = jsonObject.optString("top");
        // mSetp = jsonObject.optString("step");
        mRead = ServerImageDataModel.UN_READ;// 默认为未读
        mCollectTime = jsonObject.optString("collect_time");
        mReleaseTime = jsonObject.optString("release_time");
        mCollectWebsite = jsonObject.optString("collect_website");
        mDataType = jsonObject.optString("data_type");
    }

}

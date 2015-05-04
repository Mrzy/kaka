
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerOnlineWallpaperManager {

    public static ArrayList<ServerOnlineWallpaper> parseJson(JSONObject jsonObj) {
        ArrayList<ServerOnlineWallpaper> sdList = new ArrayList<ServerOnlineWallpaper>();
        String state = jsonObj.optString("state");
        if (state.equals("success")) {
            JSONArray jsonArray = jsonObj.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                ServerOnlineWallpaper serverOnlineWallpaper = new ServerOnlineWallpaper();
                serverOnlineWallpaper.parseBaseJson(jsonObject);
                sdList.add(serverOnlineWallpaper);
            }
        }
        return sdList;
    }

    public static final class ServerOnlineWallpaper {
        private String id;

        private String author;

        private String desc;

        private String thumbURL;

        private String name;

        private Long publishDATE;

        private String imageNAME;

        private String imageURL;

        private String imageEXT;

        private int top;

        /**
         * 数据所在位置
         */
        private int mPosition = -1;

        private boolean isCurItem = false;

        private boolean isNewData = false;

        public void parseBaseJson(JSONObject jsonObject) {
            id = jsonObject.optString("id");
            author = jsonObject.optString("p_author");
            desc = jsonObject.optString("p_desc");
            thumbURL = jsonObject.optString("thumbURL");
            name = jsonObject.optString("p_name");
            publishDATE = jsonObject.optLong("publishDATE", 0);
            imageNAME = jsonObject.optString("imageNAME");
            imageURL = jsonObject.optString("imageURL");
            imageEXT = jsonObject.optString("imageEXT");
            top = jsonObject.optInt("top", 0);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getThumbURL() {
            return thumbURL;
        }

        public void setThumbURL(String thumbURL) {
            this.thumbURL = thumbURL;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getPublishDATE() {
            return publishDATE;
        }

        public void setPublishDATE(long publishDATE) {
            this.publishDATE = publishDATE;
        }

        public String getImageNAME() {
            return imageNAME;
        }

        public void setImageNAME(String imageNAME) {
            this.imageNAME = imageNAME;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getImageEXT() {
            return imageEXT;
        }

        public void setImageEXT(String imageEXT) {
            this.imageEXT = imageEXT;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getPosition() {
            return mPosition;
        }

        public void setPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        public boolean isCurItem() {
            return isCurItem;
        }

        public void setCurItem(boolean isCurItem) {
            this.isCurItem = isCurItem;
        }

        public boolean isNewData() {
            return isNewData;
        }

        public void setNewData(boolean isNewData) {
            this.isNewData = isNewData;
        }

    }

}

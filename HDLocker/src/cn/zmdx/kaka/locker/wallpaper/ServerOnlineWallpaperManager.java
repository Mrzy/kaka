
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.view.View;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;

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

        private int publishDATE;

        private String imageNAME;

        private String imageURL;

        private String imageEXT;

        /**
         * 数据所在位置
         */
        private int mPosition;

        /**
         * 边框View
         */
        private View mSelectView;

        public void parseBaseJson(JSONObject jsonObject) {
            id = jsonObject.optString("id");
            author = jsonObject.optString("author");
            desc = jsonObject.optString("desc");
            thumbURL = jsonObject.optString("thumbURL");
            name = jsonObject.optString("name");
            publishDATE = jsonObject.optInt("publishDATE", 0);
            imageNAME = HDBHashUtils.getStringMD5(jsonObject.optString("imageNAME"));
            imageURL = jsonObject.optString("imageURL");
            imageEXT = jsonObject.optString("imageEXT");
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

        public int getPublishDATE() {
            return publishDATE;
        }

        public void setPublishDATE(int publishDATE) {
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

        public int getPosition() {
            return mPosition;
        }

        public void setPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        public View getSelectView() {
            return mSelectView;
        }

        public void setSelectView(View mSelectView) {
            this.mSelectView = mSelectView;
        }

    }

}


package cn.zmdx.kaka.locker.content;

import android.graphics.Bitmap;
import android.view.View;

public interface IPandoraBox {

    public static final int CATEGORY_PLAIN_TEXT = 0;

    public static final int CATEGORY_SINGLE_IMG = 1;

    public static final int CATEGORY_IMG_TEXT_MIXED = 2;

    public static final int CATEGORY_HTML = 3;

    public static final int CATEGORY_DEFAULT = 4;

    public int getCategory();

    public PandoraData getData();

    public View getContainer();

    public View getRenderedView();

    public static class PandoraData {
        private int mId = -1;

        private String mTitle;

        private String mContent;

        private Bitmap mImage;

        private String from;

        private String mImageUrl;

        private String mDesc;

        private String mTableName;

        public String getmDesc() {
            return mDesc;
        }

        public void setmDesc(String mDesc) {
            this.mDesc = mDesc;
        }

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getmTitle() {
            return mTitle;
        }

        public void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public String getmContent() {
            return mContent;
        }

        public void setmContent(String mContent) {
            this.mContent = mContent;
        }

        public Bitmap getmImage() {
            return mImage;
        }

        public void setmImage(Bitmap mImage) {
            this.mImage = mImage;
        }

        public void setmImageUrl(String imageUrl) {
            mImageUrl = imageUrl;
        }

        public String getmImageUrl() {
            return mImageUrl;
        }

        public void setFromTable(String tableNameServer) {
            mTableName = tableNameServer;
        }

        public String getFromTable() {
            return mTableName;
        }
    }
}

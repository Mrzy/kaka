
package pl.droidsonroids.gif;

import java.io.File;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.meiwen.BuildConfig;

/**
 * An {@link ImageView} which tries treating background and src as
 * {@link GifDrawable}
 * 
 * @author koral--
 */
public class GifImageView extends ImageView {
    static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    private GifDrawable mDrawable = null;

    /**
     * A corresponding superclass constructor wrapper.
     * 
     * @param context
     * @see ImageView#ImageView(Context)
     */
    public GifImageView(Context context) {
        super(context);
    }

    /**
     * Like equivalent from superclass but also try to interpret src and
     * background attributes as {@link GifDrawable}.
     * 
     * @param context
     * @param attrs
     * @see ImageView#ImageView(Context, AttributeSet)
     */
    public GifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        trySetGifDrawable(attrs, getResources());
    }

    /**
     * Like equivalent from superclass but also try to interpret src and
     * background attributes as GIFs.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     * @see ImageView#ImageView(Context, AttributeSet, int)
     */
    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        trySetGifDrawable(attrs, getResources());
    }

    @Override
    public void setImageResource(int resId) {
        setResource(true, resId, getResources());
    }

    @Override
    public void setBackgroundResource(int resId) {
        setResource(false, resId, getResources());
    }

    void trySetGifDrawable(AttributeSet attrs, Resources res) {
        if (attrs != null && res != null && !isInEditMode()) {
            int resId = attrs.getAttributeResourceValue(ANDROID_NS, "src", -1);
            if (resId > 0 && "drawable".equals(res.getResourceTypeName(resId)))
                setResource(true, resId, res);

            resId = attrs.getAttributeResourceValue(ANDROID_NS, "background", -1);
            if (resId > 0 && "drawable".equals(res.getResourceTypeName(resId)))
                setResource(false, resId, res);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public void setBackgroundDrawable(boolean src, File drawableFile) throws IOException {
        if (drawableFile == null || drawableFile.isDirectory()) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("drawableFile must available");
            }
            return;
        }
        mDrawable = new GifDrawable(drawableFile);
        if (src) {
            setImageDrawable(mDrawable);
        } else if (Build.VERSION.SDK_INT >= 16) {
            setBackground(mDrawable);
        } else
            setBackgroundDrawable(mDrawable);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    // new method not available on older API levels
    void setResource(boolean isSrc, int resId, Resources res) {
        try {
            mDrawable = new GifDrawable(res, resId);
            if (isSrc)
                setImageDrawable(mDrawable);
            else if (Build.VERSION.SDK_INT >= 16)
                setBackground(mDrawable);
            else
                setBackgroundDrawable(mDrawable);
            return;
        } catch (Exception ignored) {
            // ignored
        }
        if (isSrc)
            super.setImageResource(resId);
        else
            super.setBackgroundResource(resId);
    }

    /**
     * Sets the content of this GifImageView to the specified Uri. If uri
     * destination is not a GIF then
     * {@link android.widget.ImageView#setImageURI(android.net.Uri)} is called
     * as fallback. For supported URI schemes see:
     * {@link android.content.ContentResolver#openAssetFileDescriptor(android.net.Uri, String)}
     * .
     * 
     * @param uri The Uri of an image
     */
    @Override
    public void setImageURI(Uri uri) {
        if (uri != null)
            try {
                setImageDrawable(new GifDrawable(getContext().getContentResolver(), uri));
                return;
            } catch (IOException ignored) {
                // ignored
            }
        super.setImageURI(uri);
    }

    public void startGif() {
        if (mDrawable != null)
            mDrawable.start();
    }

    public void stopGif() {
        if (mDrawable != null)
            mDrawable.stop();
    }
}

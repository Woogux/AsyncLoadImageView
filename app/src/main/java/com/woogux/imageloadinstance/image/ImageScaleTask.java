package com.woogux.imageloadinstance.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;

/**
 * Created by wgx on 15/6/26.
 * <p/>
 * A simple task to load image asynchronously. Use {@link com.woogux.imageloadinstance.image.ImageScaleTask.OnImageScale#onImageScaled(Bitmap)}
 * to get result bitmap, may be canceled if target imageview has new source or been recycled.
 */
public class ImageScaleTask extends AsyncTask<Object, Void, Bitmap> {

    private Context mContext;
    private OnImageScale mOnImageScale;
    private int mWidth = 0, mHeight = 0, mMode = ImageUtils.RESIZE_MODE_CROP;

    /**
     * @param context Use application context in case of memory leak
     */
    public ImageScaleTask(Context context, int width, int height, int mode,
            OnImageScale onImageScale) {
        mContext = context;
        mOnImageScale = onImageScale;
        mWidth = width;
        mHeight = height;
        mMode = mode;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Object... params) {
        Object param = params[0];
        if (param instanceof Uri) {
            return ImageUtils.decodeBitmapFromUri(mContext, (Uri) param, mWidth, mHeight, mMode);
        } else if (param instanceof Integer) {
            return ImageUtils
                    .decodeBitmapFromResource(mContext.getResources(), (Integer) param, mWidth,
                            mHeight, mMode);
        } else if (param instanceof Bitmap) {
            return ImageUtils.scaleBitmap((Bitmap) param, mWidth, mHeight, mMode);
        } else if (param instanceof File) {
            return ImageUtils.decodeBitmapFromFile((File) param, mWidth, mHeight, mMode);
        } else if (param instanceof String) {
            return ImageUtils
                    .decodeBitmapFromFile(new File((String) param), mWidth, mHeight, mMode);
        } else {
            return null;
        }
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && mOnImageScale != null) {
            mOnImageScale.onImageScaled(bitmap);
        }
    }

    public interface OnImageScale {
        void onImageScaled(Bitmap bitmap);
    }
}

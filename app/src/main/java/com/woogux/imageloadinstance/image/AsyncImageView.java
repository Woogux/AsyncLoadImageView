package com.woogux.imageloadinstance.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.woogux.imageloadinstance.R;

/**
 * Created by wgx on 15/6/26.
 */
public class AsyncImageView extends ImageView {

    private ImageScaleTask mScaleTask;
    private int mEmptyResource = -1;
    private int mMode = ImageUtils.RESIZE_MODE_CROP;

    public AsyncImageView(Context context) {
        super(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.AsyncImageView);
        mEmptyResource = array.getResourceId(0, -1);
        array.recycle();
    }

    public void setEmptyResource(int resId) {
        mEmptyResource = resId;
    }

    /**
     * Must be called after measure.
     */
    public void resolveTargetImageSize() {
        switch (getScaleType()) {
            case CENTER:
            case MATRIX:
            case CENTER_INSIDE:
            default:
                mMode = ImageUtils.RESIZE_MODE_INSIDE;
                break;
            case CENTER_CROP:
            case FIT_END:
            case FIT_CENTER:
            case FIT_START:
            case FIT_XY:
                mMode = ImageUtils.RESIZE_MODE_CROP;
                break;
        }
    }

    @Override
    public void setImageResource(int resId) {
        if (mEmptyResource != -1) {
            super.setImageResource(mEmptyResource);
        }
        if (mScaleTask != null) {
            mScaleTask.cancel(true);
        }
        resolveTargetImageSize();
        mScaleTask = new ImageScaleTask(getContext().getApplicationContext(), getMeasuredWidth(),
                getMeasuredHeight(), mMode, new ImageScaleTask.OnImageScale() {
            @Override
            public void onImageScaled(Bitmap bitmap) {
                AsyncImageView.super.setImageBitmap(bitmap);
            }
        });
        mScaleTask.execute(resId);
    }

    @Override
    public void setImageURI(Uri uri) {
        if (mEmptyResource != -1) {
            super.setImageResource(mEmptyResource);
        }
        if (mScaleTask != null) {
            mScaleTask.cancel(true);
        }
        resolveTargetImageSize();
        mScaleTask = new ImageScaleTask(getContext().getApplicationContext(), getMeasuredWidth(),
                getMeasuredHeight(), mMode, new ImageScaleTask.OnImageScale() {
            @Override
            public void onImageScaled(Bitmap bitmap) {
                AsyncImageView.super.setImageBitmap(bitmap);
            }
        });
        mScaleTask.execute(uri);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (mEmptyResource != -1) {
            super.setImageResource(mEmptyResource);
        }
        if (mScaleTask != null) {
            mScaleTask.cancel(true);
        }
        resolveTargetImageSize();
        mScaleTask = new ImageScaleTask(getContext().getApplicationContext(), getMeasuredWidth(),
                getMeasuredHeight(), mMode, new ImageScaleTask.OnImageScale() {
            @Override
            public void onImageScaled(Bitmap bitmap) {
                AsyncImageView.this.setImageBitmap(bitmap);
            }
        });
        mScaleTask.execute(bm);
    }
}

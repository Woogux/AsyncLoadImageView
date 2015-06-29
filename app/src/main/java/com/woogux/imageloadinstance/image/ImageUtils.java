package com.woogux.imageloadinstance.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by wgx on 15/6/26.
 * <p/>
 * Some image decode functions.
 */
public class ImageUtils {

    public static final int RESIZE_MODE_CROP = 0;
    public static final int RESIZE_MODE_INSIDE = 1;

    public static Bitmap decodeBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight, int mode) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, mode);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeBitmapFromUri(Context context, Uri uri, int reqWidth,
            int reqHeight, int mode) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory
                    .decodeStream(context.getContentResolver().openInputStream(uri), null, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, mode);

            options.inJustDecodeBounds = false;
            return BitmapFactory
                    .decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Target image not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap decodeBitmapFromFile(File file, int reqWidth, int reqHeight, int mode) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, mode);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height, int mode) {
        int sampleSize =
                caculateInSampleSize(bitmap.getWidth(), bitmap.getHeight(), width, height, mode);
        return Bitmap.createBitmap(bitmap, 0, 0, width / sampleSize, height / sampleSize);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight, int mode) {
        // Raw height and width of image
        return caculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight, mode);
    }

    public static int caculateInSampleSize(int bitmapWidth, int bitmapHeight, int reqWidth,
            int reqHeight, int mode) {
        int inSampleSize = 1;

        if (bitmapHeight > reqHeight || bitmapWidth > reqWidth) {

            final int halfHeight = bitmapHeight / 2;
            final int halfWidth = bitmapWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            switch (mode) {
                case RESIZE_MODE_CROP:
                    while ((halfHeight / inSampleSize) > reqHeight
                            || (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                case RESIZE_MODE_INSIDE:
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                default:
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
            }
        }

        return inSampleSize;
    }
}

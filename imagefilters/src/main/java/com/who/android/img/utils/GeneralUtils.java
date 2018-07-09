package com.who.android.img.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

import com.who.android.img.NativeImageProcessor;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * @author Varun on 30/06/15.
 */
public final class GeneralUtils {

    private GeneralUtils() {
    }

    public static Bitmap generateCircularBitmap( final Bitmap input) {

        final int width = input.getWidth();
        final int height = input.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW
        );

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(input, 0, 0, null);
        return outputBitmap;
    }

    public static Mat fromBitmap( final Bitmap bitmap){
        if( null == bitmap || bitmap.isRecycled()) return null;

        final Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        return mat;
    }

}

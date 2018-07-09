package com.who.android.img.effects.subeffects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.who.android.img.NativeImageProcessor;
import com.who.android.img.effects.subeffects.interfaces.PerImageSubEffect;
import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.zomato.photoeffects.BuildConfig;
import com.zomato.photoeffects.R;


/**
 * @author varun
 * Subfilter to add Vignette effect on an image
 */
public final class VignetteSubfilter implements PerImageSubEffect {


    private final boolean isObstruction;

    /**
     * Initialise Vignette subfilter
     *
     */
    public VignetteSubfilter( final boolean isObstruction) {
        this.isObstruction = isObstruction;
    }

    @Override public void process(final int[] pixelData, final int width, final int height ) {

//        Bitmap vignette = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vignette);
//
//        vignette = Bitmap.createScaledBitmap(vignette, inputImage.getWidth(), inputImage.getHeight(), true);
//        final Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setAlpha(mAlpha);
//
//        final Canvas comboImage = new Canvas(inputImage);
//        comboImage.drawBitmap(vignette, 0f, 0f, paint);
        if(isObstruction){
            NativeImageProcessor.vignettify( width, height, pixelData, 400, 500, isObstruction);
        } else {
            NativeImageProcessor.vignettify( width, height, pixelData, 600, 700, isObstruction);

        }
    }

    @Override
    public Tag getTag() {
        return Tag.VIGNETTE;
    }
}
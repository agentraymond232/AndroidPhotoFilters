package com.who.android.img.effects.obstructions;

import android.graphics.Bitmap;
import android.util.Log;

import com.who.android.img.NativeImageProcessor;
import com.who.android.img.effects.subeffects.interfaces.PerImageSubEffect;
import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.effects.subeffects.interfaces.SubEffect;

public final class Cartoonification implements PerImageSubEffect {

    @Override public Tag getTag() {
        return Tag.CARTOONIFY;
    }

    @Override
    public void process( final int[] pixels, final int width, final int height) {
        NativeImageProcessor.cartoonifyImage( width, height, pixels, true);
        Log.e(Cartoonification.class.getName(), "ray232 pixeldata " + pixels.length);
    }
}

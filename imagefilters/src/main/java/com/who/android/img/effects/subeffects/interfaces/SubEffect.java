package com.who.android.img.effects.subeffects.interfaces;

import android.graphics.Bitmap;

/**
 * @author varun on 27/07/15.
 */
@FunctionalInterface public interface SubEffect {

    enum Tag{
        BRIGHTNESS,
        COLOUROVERLAY,
        CONTRAST,
        SATURATION,
        TONECURVE,
        VIGNETTE,
        INVERSION,
        CARTOONIFY
    }

    Tag getTag();

    boolean NATIVE = true;
}

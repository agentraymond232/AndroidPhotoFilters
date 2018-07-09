package com.who.android.img.effects.subeffects.interfaces;

import android.graphics.Bitmap;

public interface PerImageSubEffect extends SubEffect {

    void process ( int[] pixelData, int width, int height);
}

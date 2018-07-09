package com.who.android.img.effects.subeffects.interfaces;

public interface PerPixelSubEffect extends SubEffect {

    void process( int[] pixels, int start, int end);
}

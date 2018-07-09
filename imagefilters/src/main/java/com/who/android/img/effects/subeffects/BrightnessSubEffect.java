package com.who.android.img.effects.subeffects;

import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.NativeImageProcessor;


/**
 * @author varun
 * subfilter used to tweak mBrightness of the Bitmap
 */
public final class BrightnessSubEffect implements PerPixelSubEffect {

    public static int defaultValue(){ return 0;}

    // Value is in integer
    public final int brightness;

    /**
     * Takes brightness of the image
     *
     * @param brightness Integer mBrightness value {value 0 has no effect}
     */
    public BrightnessSubEffect(final int brightness) {
        this.brightness = brightness;
    }

    @Override public void process(final int[] pixels, final int start, final int end) {

        if(NATIVE) {
            NativeImageProcessor.doBrightness(pixels, brightness, start, end);
            return;
        }
        int red, green, blue;

        for (int i = start; i < end; ++i) {
            red = ((pixels[i] >> 16) & 0xFF ) + brightness;
            green = ((pixels[i] >> 8) & 0xFF ) + brightness;
            blue = ((pixels[i]) & 0xFF ) + brightness;

            // validation check
            red = 255 < red ? 255 : 0 > red ? 0 : red;
            green = 255 < green ? 255 : 0 > green ? 0 : green;
            blue = 255 < blue ? 255 : 0 > blue ? 0 : blue;

            pixels[i] = pixels[i] & 0xFF000000 | (red << 16) & 0x00FF0000 | (green << 8) & 0x0000FF00 | blue & 0x000000FF;
        }
    }

    @Override public Tag getTag() {
        return Tag.BRIGHTNESS;
    }
}

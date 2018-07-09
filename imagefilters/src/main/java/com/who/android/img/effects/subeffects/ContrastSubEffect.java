package com.who.android.img.effects.subeffects;

import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.NativeImageProcessor;


/**
 * @author varun
 *         Class to add Contrast Subfilter
 */
public final class ContrastSubEffect implements PerPixelSubEffect {

    public static float defaultValue(){ return 1.0f;}

    // The value is in [1, 3], value 1 has no effect and is the default value
    public final float Contrast;

    /**
     * Initialise mContrast subfilter
     *
     * @param contrast The mContrast value ranges in fraction, value 1 has no effect
     */
    public ContrastSubEffect(final float contrast) {
        Contrast = contrast >= 3 ? 3 : (contrast <= 1 ? 1 : contrast) ;
    }

    @Override public void process(final int[] pixels, final int start, final int end) {
        if(NATIVE) {
            NativeImageProcessor.doContrast(pixels, Contrast, start, end);
            return;
        }

        float red, green, blue;

        for (int i = start; i < end; i++) {
            red = (pixels[i] >> 16) & 0xFF;
            green = (pixels[i] >> 8) & 0xFF;
            blue = (pixels[i]) & 0xFF;

            red = (float) (((((red / 255) - 0.5) * Contrast) + 0.5) * 255);
            green = (float) (((((green / 255) - 0.5) * Contrast) + 0.5) * 255);
            blue = (float) (((((blue / 255) - 0.5) * Contrast) + 0.5) * 255);

            // validation check
            red = (red > 255) ? 255 : ( red < 0 ? 0 : red);
            green = (green > 255) ? 255 : ( green < 0 ? 0 : green);
            blue = (blue > 255) ? 255 : ( blue < 0 ? 0 : blue);

            pixels[i] = pixels[i] & 0xFF000000 | ((int) red << 16) & 0x00FF0000
                    | ((int) green << 8) & 0x0000FF00 | (int) blue & 0x000000FF;
        }
    }

    @Override public Tag getTag() {
        return Tag.CONTRAST;
    }

}

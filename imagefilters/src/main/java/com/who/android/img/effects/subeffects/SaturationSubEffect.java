package com.who.android.img.effects.subeffects;

import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.NativeImageProcessor;


/**
 * @author varun on 28/07/15.
 */
public final class SaturationSubEffect implements PerPixelSubEffect {

    public static float defaultValue(){ return 1.0f;}

    private static int HSLtoRGB_Subfunction( final float temp1, final float temp2, final float temp3) {
        return  1 > temp3 * 6 ? (int) ((temp2 + (temp1 - temp2) * 6 * temp3) * 100) :
                1 > temp3 * 2 ? (int) (temp1 * 100) :
                2 > temp3 * 3 ? (int) ((temp2 + (temp1 - temp2) * (.66666 - temp3) * 6) * 100) :
                (int)(temp2 * 100);
    }


    // The Level value is float [0,3], where Level = 1 has no effect on the image
    private final float Level;

    public SaturationSubEffect(final float level) {

        this.Level =  level >= 3 ? 3 : (level <= 0 ? 0 : level) ;
    }

    @Override public void process(final int[] pixels, final int start, final int end) {

        if(NATIVE) {
            NativeImageProcessor.doSaturation(pixels, Level, start,  end);
            return;
        }
        int r, g, b;
        float temp1, temp2, temp3;
        float rPercent, gPercent, bPercent;
        float maxColor, minColor;
        float l, s, h;
        final float equalityBound = 0.001f;

        for (int i = start; i < end; ++i) {
            r = (pixels[i] >> 16) & 0xFF;
            g = (pixels[i] >> 8) & 0xFF;
            b = (pixels[i]) & 0xFF;

            rPercent = ((float) r) / 255;
            gPercent = ((float) g) / 255;
            bPercent = ((float) b) / 255;

            maxColor = ( rPercent >= gPercent && rPercent >= bPercent ) ? rPercent :
                    ( gPercent >= rPercent && gPercent >= bPercent ) ? gPercent : bPercent;
            minColor = ( rPercent <= gPercent && rPercent <= bPercent ) ? rPercent :
                    ( gPercent <= rPercent && gPercent <= bPercent ) ? gPercent : bPercent;

            l = (maxColor + minColor) / 2;

            if (equalityBound > Math.abs(maxColor - minColor)) { s = 0; h = 0; }
            else {
                s = 0.50 > l ? (maxColor - minColor) / (maxColor + minColor) :
                        (maxColor - minColor) / (2 - maxColor - minColor);

                h = (equalityBound > Math.abs(maxColor - rPercent)) ? (gPercent - bPercent) / (maxColor - minColor) :
                        (equalityBound > Math.abs(maxColor - gPercent)) ? 2 + (bPercent - rPercent) / (maxColor - minColor) :
                                4 + (rPercent - gPercent) / (maxColor - minColor);
            }

            s *= 100 * Level;
            s = (100 < s) ? 100 : 0 > s ? 0 : s;
            s /= 100;
            h *= 60;
            h = (0 > h)? h + 360 : h;
            h /= 360;

            if (0 == s) {
                r = (int) (l * 100);
                g = (int) (l * 100);
                b = (int) (l * 100);
            }
            else {
                temp1 = 0.50 > l ? l * (1 + s) : l + s - (l * s);
                temp2 = 2 * l - temp1;

                for (int j = 0; 3 > j; j++) {
                    switch (j) {
                        case 0: // red
                            temp3 = h + 0.33333f;
                            temp3 = (1 < temp3) ? temp3 - 1 : temp3;
                            r = HSLtoRGB_Subfunction(temp1, temp2, temp3);
                            break;
                        case 1: // green
                            temp3 = h;
                            g = HSLtoRGB_Subfunction(temp1, temp2, temp3);
                            break;
                        case 2: // blue
                            temp3 = h - 0.33333f;
                            temp3 = (0 > temp3) ? temp3 + 1 : temp3;
                            b = HSLtoRGB_Subfunction(temp1, temp2, temp3);
                            break;
                        default: break;
                    }
                }
            }
            r = (int)((((float) r) / 100) * 255);
            g = (int) ((((float) g) / 100) * 255);
            b = (int) ((((float) b) / 100) * 255);

            pixels[i] = pixels[i] & 0xFF000000 | ( r << 16) & 0x00FF0000 | ( g << 8) & 0x0000FF00 |
                    b & 0x000000FF;

        }
    }

    @Override public Tag getTag() {
        return Tag.SATURATION;
    }
}
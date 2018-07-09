package com.who.android.img.effects.subeffects;

import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.NativeImageProcessor;


/**
 * @author varun
 * Subfilter used to overlay bitmap with the color defined
 */
public final class ColorOverlaySubEffect implements PerPixelSubEffect {

    // the color overlay Depth is between 0-255
    private final int Depth;

    // these values are between 0-1
    private final float Red;
    private final float Green;
    private final float Blue;

    /**
     * Initialize Color Overlay Subfilter
     *
     * @param depth Value ranging from 0-255 {Defining intensity of color overlay}
     * @param red   Red value between 0-1
     * @param green Green value between 0-1
     * @param blue  Blue value between 0-1
     */
    public ColorOverlaySubEffect(final int depth, final float red, final float green, final float blue) {
        this.Depth = 255 <= depth ? 255 : (0 >= depth ? 0 : depth);
        this.Red = 1 <= red ? 1 : (0 >= red ? 0 : red);
        this.Blue = 1 <= blue ? 1 : (0 >= blue ? 0 : blue) ;
        this.Green = 1 <= green ? 1 : (0 >= green ? 0 : green);
    }

    @Override public void process(final int[] pixels, final int start, final int end) {

        if(NATIVE ){
            NativeImageProcessor.doColorOverlay(pixels, Depth, Red, Green, Blue, start, end);
            return;
        }

        float R, G, B;

        for (int i = start; i < end; i++) {
            R = (pixels[i] >> 16) & 0xFF;
            G = (pixels[i] >> 8) & 0xFF;
            B = (pixels[i]) & 0xFF;

            // apply intensity level for sepid-toning on each channel
            R += (Depth * Red);
            if (R > 255) { R = 255; }

            G += (Depth * Green);
            if (G > 255) { G = 255; }

            B += (Depth * Blue);
            if (B > 255) { B = 255; }

            pixels[i] = pixels[i] & 0xFF000000 | ((int) R << 16) & 0x00FF0000 | ((int) G << 8) & 0x0000FF00 |
                    (int) B & 0x000000FF;
        }
    }

    @Override public Tag getTag() {
        return Tag.COLOUROVERLAY;
    }
}
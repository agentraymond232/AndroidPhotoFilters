package com.who.android.img.effects.subeffects;

import com.who.android.img.NativeImageProcessor;
import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.effects.subeffects.interfaces.SubEffect;

public final class InversionSubEffect implements PerPixelSubEffect {
    @Override
    public Tag getTag() {
        return Tag.INVERSION;
    }

    @Override public void process(final int[] pixels, final int start, final int end) {

        if(NATIVE) {
            NativeImageProcessor.doInvert(pixels, start, end);
            return;
        }

        for (int i = start; i < end; ++i) {
            int R = 255 - ((pixels[i] >> 16) & 0xFF );
            int G = 255 - ((pixels[i] >> 8) & 0xFF );
            int B = 255 - ((pixels[i]) & 0xFF );

            pixels[i] = pixels[i] & 0xFF000000 | (R & 0x000000FF) << 16 | ( G & 0x000000FF) << 8
                    | B & 0x000000FF;

        }
    }
}

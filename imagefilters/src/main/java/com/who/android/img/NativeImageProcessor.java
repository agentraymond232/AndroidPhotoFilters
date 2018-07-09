package com.who.android.img;


/**
 * @author Varun on 30/06/15.
 */
public final class NativeImageProcessor {
    private NativeImageProcessor() {
    }

    public static native void applyRGBCurve( final int[] pixels, final int[] rgb, final int start,
                                              final int end);

    public static native void applyChannelCurves( final int[] pixels, final int[] r, final int[] g,
                                                   final int[] b, final int start, final int end);

    public static native void doBrightness( final int[] pixels, final int value, final int start,
                                             final int end);

    public static native void doContrast( final int[] pixels, final float value, final int start,
                                           final int end);

    public static native void doColorOverlay( final int[] pixels, final int depth, final float red,
                                               final float green, final float blue, final int start,
                                               final int end);

    public static native void doSaturation( final int[] pixels, float level, final int start,
                                             final int end);

    public static native void doInvert ( final int[] pixels, final int start, final int end);

    public static native void cartoonifyImage( final int width, final int height, final int[] pixels,
                                        final boolean sketchMode);

    public static native void vignettify (final int width, final int height, final int[] pixels,
                                          final int xStdDev, final int yStdDev, final boolean isObstruction);

}

package com.who.android.img.effects.subeffects;

import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.geometry.BezierSpline;
import com.who.android.img.geometry.Point;
import com.who.android.img.NativeImageProcessor;


/**
 * @author varun
 *         Subfilter to tweak mRGB channels of an image
 */
public final class ToneCurveSubEffect implements PerPixelSubEffect {

    // These are knots which contains the plot points
    private final Point[] mRGBKnots;
    private final Point[] mGreenKnots;
    private final Point[] mRedKnots;
    private final Point[] mBlueKnots;
    private final int[] mRGB;
    private final int[] mRed;
    private final int[] mGreen;
    private final int[] mBlue;

    private final int[] R = new int[256];
    private final int[] G = new int[256];
    private final int[] B = new int[256];

    /**
     * Initialise ToneCurveSubfilter (NOTE : Don't pass null knots, pass straight line instead)
     * Knots are the points in 2D taken by tweaking photoshop channels(plane ranging from 0-255)
     *
     * @param rgbKnots   mRGB Knots
     * @param redKnots   Knots in Red Channel
     * @param greenKnots Knots in green Channel
     * @param blueKnots  Knots in Blue channel
     */
    public ToneCurveSubEffect(final Point[] rgbKnots, final Point[] redKnots,
                              final Point[] greenKnots, final Point[] blueKnots) {

        final Point[] straightKnots = new Point[2];
        straightKnots[0] = new Point(0, 0);
        straightKnots[1] = new Point(255, 255);

        mRGBKnots = (null == rgbKnots) ? straightKnots : rgbKnots;
        mRedKnots = (null == redKnots) ? straightKnots : redKnots;
        mGreenKnots = (null == greenKnots) ? straightKnots : greenKnots;
        mBlueKnots = (null == blueKnots) ? straightKnots : blueKnots;

        sortPointsOnXAxis(mRGBKnots);
        sortPointsOnXAxis(mRedKnots);
        sortPointsOnXAxis(mGreenKnots);
        sortPointsOnXAxis(mBlueKnots);

        mRGB = BezierSpline.curveGenerator(mRGBKnots);
        mRed = BezierSpline.curveGenerator(mRedKnots);
        mGreen = BezierSpline.curveGenerator(mGreenKnots);
        mBlue = BezierSpline.curveGenerator(mBlueKnots);
    }

    @Override public void process(final int[] pixels, final int start, final int end) {


        if( null != mRGB)
            applyRGBCurve( pixels, start, end);
        if (!( null == mRed && null == mGreen && null == mBlue))
            applyChannelCurves( pixels, start, end);

    }

    void applyRGBCurve( final int[] pixels, final int start, final int end) {
        if(NATIVE){
            NativeImageProcessor.applyRGBCurve(pixels, mRGB, start, end);
            return;
        }

        for (int i = 0; i < 256; ++i) {
            R[i] = (mRGB[i] << 16) & 0x00FF0000;
            G[i] = (mRGB[i] << 8) & 0x0000FF00;
            B[i] = mRGB[i] & 0x000000FF;
        }

        for (int i = start; i < end; ++i) {
            pixels[i] =
                    0xFF000000 & pixels[i] | R[(pixels[i] >> 16) & 0xFF] | G[(pixels[i] >> 8) & 0xFF] | B[pixels[i] & 0xFF];
        }

    }

    void applyChannelCurves( final int[] pixels, final int start, final int end) {
        if(NATIVE){
            NativeImageProcessor.applyChannelCurves(pixels, mRed, mGreen, mBlue, start, end);
            return;
        }
        int red;
        int green;
        int blue;
        int alpha;

        for (int i = start; i < end; ++i) {
            if ( null != mRed)
                red = ( mRed[(pixels[i] >> 16) & 0xFF] << 16) & 0x00FF0000;
            else
                red = (pixels[i] << 16) & 0x00FF0000;

            if ( null != mGreen)
                green = ( mGreen [(pixels[i] >> 8) & 0xFF] << 8) & 0x0000FF00;
            else
                green = (pixels[i] << 8) & 0x0000FF00;

            if ( null != mBlue )
                blue =  mBlue[pixels[i] & 0xFF] & 0x000000FF;
            else
                blue = pixels[i] & 0x000000FF;

            alpha = pixels[i] & 0xFF000000;

            pixels[i] = alpha | red | green | blue;
        }
    }

    private static void sortPointsOnXAxis(final Point... points) {
        if (null == points) return;


        for (@SuppressWarnings("StandardVariableNames") int s = 1; s < points.length - 1; s++) {
            for (int k = 0; k <= points.length - 2; k++) {
                if (points[k].x > points[k + 1].x) {
                    float temp = points[k].x;
                    points[k].x = points[k + 1].x; //swapping values
                    points[k + 1].x = temp;
                }
            }
        }
    }

    @Override
    public Tag getTag() {
        return Tag.TONECURVE;
    }
}
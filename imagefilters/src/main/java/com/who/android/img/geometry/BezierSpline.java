package com.who.android.img.geometry;


import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.animation.PathInterpolator;

/**
 * @author Varun on 29/06/15.
 */
public final class BezierSpline {
    private BezierSpline() {
    }

    /**
     * Generates Curve {in a plane ranging from 0-255} using the knots provided
     */
    public static int[] curveGenerator(final Point[] knots) {
        if (knots == null) {
            throw new NullPointerException("Knots cannot be null");
        }

        if (2 > knots.length) {
            throw new IllegalArgumentException("Atleast two points are required");
        }

        return Build.VERSION.SDK_INT >= 21 ? getOutputPointsForNewerDevices(knots) :
                getOutputPointsForOlderDevices(knots);
    }

    // This is for lollipop and newer devices
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int[] getOutputPointsForNewerDevices(final Point[] knots) {

        final Point[] controlPoints = calculateControlPoints(knots);
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(knots[0].x / 255.0f, knots[0].y / 255.0f);
        path.moveTo(knots[0].x / 255.0f, knots[0].y / 255.0f);

        for (int index = 1; index < knots.length; index++) {
            path.quadTo(
                    controlPoints[index - 1].x / 255.0f,
                    controlPoints[index - 1].y / 255.0f,
                    knots[index].x / 255.0f,
                    knots[index].y / 255.0f
            );
            path.moveTo(knots[index].x / 255.0f, knots[index].y / 255.0f);
        }

        path.lineTo(1, 1);
        path.moveTo(1, 1);

        final int[] allPoints = new int[256];
        final PathInterpolator pathInterpolator = new PathInterpolator(path);

        for (int x = 0; x < 256; x++) {
            allPoints[x] = (int) (255 * pathInterpolator.getInterpolation( x / 255.0f));
            allPoints[x] = 255 < allPoints[x] ? 255 : 0 > allPoints[x] ? 0 : allPoints[x];
        }

        allPoints[0] = 255 < knots[0].y ? 255 : 0 > knots[0].y ? 0 : (int)knots[0].y ;
        allPoints[255] = 255 < knots[knots.length - 1].y ? 255 : 0 > knots[knots.length - 1].y ? 0 :
                (int)knots[knots.length - 1].y  ;
        return allPoints;
    }


    //This is for devices older than lollipop
    private static int[] getOutputPointsForOlderDevices( final Point[] knots) {
        final Point[] controlPoints = calculateControlPoints(knots);
        final Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(knots[0].x, knots[0].y);
        path.moveTo(knots[0].x, knots[0].y);

        for (int index = 1; index < knots.length; index++) {
            path.quadTo(controlPoints[index - 1].x, controlPoints[index - 1].y, knots[index].x, knots[index].y);
            path.moveTo(knots[index].x, knots[index].y);
        }

        path.lineTo(255, 255);
        path.moveTo(255, 255);

        final int[] allPoints = new int[256];

        final PathMeasure pm = new PathMeasure(path, false);
        for (int i = 0; i < 256; i++) {
            allPoints[i] = -1;
        }

        int x = 0;
        final float[] acoordinates = {0, 0};

        do {
            final float pathLength = pm.getLength();
            for (float i = 0; i <= pathLength; i += 0.08f) {
                pm.getPosTan(i, acoordinates, null);
                if ((int) (acoordinates[0]) > x && x < 256) {
                    allPoints[x] = (int)acoordinates[1];
                    x++;
                }
                if (x > 255) {
                    break;
                }
            }
        } while (pm.nextContour());


        allPoints[0] = 255.0f < knots[0].y ? 255 : 0.0f > knots[0].y ? 0 : allPoints[x] ;
        for (int i = 0; i < 256; i++) {
            if (allPoints[i] == -1) {
                allPoints[i] = allPoints[i - 1];
            }
        }
        allPoints[255] = 255.0f < knots[knots.length - 1].y ? 255 : 0.0f > knots[knots.length - 1].y ?
                0 : allPoints[x] ;
        return allPoints;
    }

    // Calculates the control points for the specified knots
    @NonNull private static Point[] calculateControlPoints( final Point[] knots) {

        final int n = knots.length - 1;
        final Point[] controlPoints = new Point[n];

        if (1 == n) { // Special case: Bezier curve should be a straight line.
            // 3P1 = 2P0 + P3
            controlPoints[0] = new Point((2 * knots[0].x + knots[1].x) / 3, (2 * knots[0].y + knots[1].y) / 3);
            // P2 = 2P1 – P0
            //controlPoints[1][0] = new Point(2*controlPoints[0][0].x - knots[0].x, 2*controlPoints[0][0].y-knots[0].y);
        } else {
            // Calculate first Bezier control points
            // Right hand side vector
            final float[] rhs = new float[n];

            // Set right hand side x values
            for (int i = 1; i < n - 1; ++i) {
                rhs[i] = 4 * knots[i].x + 2 * knots[i + 1].x;
            }
            rhs[0] = knots[0].x + 2 * knots[1].x;
            rhs[n - 1] = (8 * knots[n - 1].x + knots[n].x) / 2.0f;
            // Get first control points x-values
            final float[] x = getFirstControlPoints(rhs);

            // Set right hand side y values
            for (int i = 1; i < n - 1; ++i) {
                rhs[i] = 4 * knots[i].y + 2 * knots[i + 1].y;
            }
            rhs[0] = knots[0].y + 2 * knots[1].y;
            rhs[n - 1] = (8 * knots[n - 1].y + knots[n].y) / 2.0f;
            // Get first control points y-values
            final float[] y = getFirstControlPoints(rhs);
            for (int i = 0; i < n; ++i) {
                controlPoints[i] = new Point(x[i], y[i]);
            }
        }

        return controlPoints;
    }

    @NonNull private static float[] getFirstControlPoints( final float[] rhs) {
        final int n = rhs.length;
        final float[] x = new float[n]; // Solution vector.
        final float[] tmp = new float[n]; // Temp workspace.

        float b = 1.0f;   // Control Point Factor
        x[0] = rhs[0] / b;
        for (int i = 1; i < n; i++) // Decomposition and forward substitution.
        {
            tmp[i] = 1 / b;
            b = (i < n - 1 ? 4.0f : 3.5f) - tmp[i];
            x[i] = (rhs[i] - x[i - 1]) / b;
        }
        for (int i = 1; i < n; i++) {
            x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
        }
        return x;
    }
}
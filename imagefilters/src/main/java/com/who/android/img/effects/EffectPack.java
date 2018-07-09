package com.who.android.img.effects;

import android.content.Context;
import android.support.annotation.NonNull;

import com.who.android.img.effects.obstructions.Cartoonification;
import com.who.android.img.effects.subeffects.InversionSubEffect;
import com.who.android.img.geometry.Point;
import com.zomato.photoeffects.R;
import com.who.android.img.effects.subeffects.BrightnessSubEffect;
import com.who.android.img.effects.subeffects.ColorOverlaySubEffect;
import com.who.android.img.effects.subeffects.ContrastSubEffect;
import com.who.android.img.effects.subeffects.SaturationSubEffect;
import com.who.android.img.effects.subeffects.ToneCurveSubEffect;
import com.who.android.img.effects.subeffects.VignetteSubfilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Originally created by @author Varun on 01/07/15.
 * <p>
 * Added filters by @author Ravi Tamada on 29/11/17.
 * Added multiple filters, the effect names were inspired from
 * various image filters apps
 */
public enum EffectPack {

    ;

    /***
     * the effect pack,
     * @param context
     * @return list of filters
     */
    public static List<Effect> getEnhancementsPack(@NonNull final Context context) {
        final List<Effect> effects = new ArrayList<>(16);
        effects.add(getAweStruckVibeFilter(context));
        effects.add(getClarendon(context));
        effects.add(getOldManFilter(context));
        effects.add(getMarsFilter(context));
        effects.add(getRiseFilter(context));
        effects.add(getAprilFilter(context));
        effects.add(getAmazonFilter(context));
        effects.add(getStarLitFilter(context));
        effects.add(getNightWhisperFilter(context));
        effects.add(getLimeStutterFilter(context));
        effects.add(getHaanFilter(context));
        effects.add(getBlueMessFilter(context));
        effects.add(getAdeleFilter(context));
        effects.add(getCruzFilter(context));
        effects.add(getMetropolis(context));
        effects.add(getAudreyFilter(context));
        return effects;
    }

    public static List<Effect> getObstructionsPack(@NonNull final Context context) {
        final List<Effect> effects = new ArrayList<>(4);
        effects.add(getInversionFilter( context));
        effects.add(getCartoonificationFilter(context));
        effects.add(getVignetterFIlter(context));

        return effects;
    }

    private static Effect getInversionFilter(Context context) {

        return new Effect("Negative", new InversionSubEffect());
    }

    private static Effect getVignetterFIlter(Context context) {

        return new Effect("Vignette", new VignetteSubfilter(true));
    }

    private static Effect getCartoonificationFilter(Context context) {

        return new Effect("Cartoonify", new Cartoonification());
    }


    private static Effect getStarLitFilter(@NonNull final Context context) {
        final Point[] rgbKnots = new Point[8];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(34, 6);
        rgbKnots[2] = new Point(69, 23);
        rgbKnots[3] = new Point(100, 58);
        rgbKnots[4] = new Point(150, 154);
        rgbKnots[5] = new Point(176, 196);
        rgbKnots[6] = new Point(207, 233);
        rgbKnots[7] = new Point(255, 255);
        return new Effect(
                context.getString(R.string.starlit),
                new ToneCurveSubEffect(rgbKnots, null, null, null));
    }

    private static Effect getBlueMessFilter(@NonNull final Context context) {
        final Point[] redKnots = new Point[8];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(86, 34);
        redKnots[2] = new Point(117, 41);
        redKnots[3] = new Point(146, 80);
        redKnots[4] = new Point(170, 151);
        redKnots[5] = new Point(200, 214);
        redKnots[6] = new Point(225, 242);
        redKnots[7] = new Point(255, 255);
        return new Effect(
                context.getString(R.string.bluemess),
                new ToneCurveSubEffect(null, redKnots, null, null),
                new BrightnessSubEffect(30),
                new ContrastSubEffect(1f));
    }

    private static Effect getAweStruckVibeFilter(@NonNull final Context context) {

        final Point[] rgbKnots = new Point[5];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(80, 43);
        rgbKnots[2] = new Point(149, 102);
        rgbKnots[3] = new Point(201, 173);
        rgbKnots[4] = new Point(255, 255);

        final Point[] redKnots = new Point[5];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(125, 147);
        redKnots[2] = new Point(177, 199);
        redKnots[3] = new Point(213, 228);
        redKnots[4] = new Point(255, 255);


        final Point[] greenKnots = new Point[6];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(57, 76);
        greenKnots[2] = new Point(103, 130);
        greenKnots[3] = new Point(167, 192);
        greenKnots[4] = new Point(211, 229);
        greenKnots[5] = new Point(255, 255);


        final Point[] blueKnots = new Point[7];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(38, 62);
        blueKnots[2] = new Point(75, 112);
        blueKnots[3] = new Point(116, 158);
        blueKnots[4] = new Point(171, 204);
        blueKnots[5] = new Point(212, 233);
        blueKnots[6] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.struck),
                new ToneCurveSubEffect(rgbKnots, redKnots, greenKnots, blueKnots));
    }

    private static Effect getLimeStutterFilter(@NonNull final Context context) {

        final Point[] blueKnots = new Point[3];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(165, 114);
        blueKnots[2] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.lime),
                new ToneCurveSubEffect(null, null, null, blueKnots));
    }

    private static Effect getNightWhisperFilter(@NonNull final Context context) {

        final Point[] rgbKnots = new Point[3];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(174, 109);
        rgbKnots[2] = new Point(255, 255);

        final Point[] redKnots = new Point[4];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(70, 114);
        redKnots[2] = new Point(157, 145);
        redKnots[3] = new Point(255, 255);

        final Point[] greenKnots = new Point[3];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(109, 138);
        greenKnots[2] = new Point(255, 255);

        final Point[] blueKnots = new Point[3];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(113, 152);
        blueKnots[2] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.whisper),
                new ContrastSubEffect(1.5f),
                new ToneCurveSubEffect(rgbKnots, redKnots, greenKnots, blueKnots));
    }

    private static Effect getAmazonFilter(@NonNull final Context context) {
        final Point[] blueKnots = new Point[6];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(11, 40);
        blueKnots[2] = new Point(36, 99);
        blueKnots[3] = new Point(86, 151);
        blueKnots[4] = new Point(167, 209);
        blueKnots[5] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.amazon),
                new ContrastSubEffect(1.2f),
                new ToneCurveSubEffect(null, null, null, blueKnots));
    }

    private static Effect getAdeleFilter(@NonNull final Context context) {
        return new Effect(
                context.getString(R.string.adele),
                new SaturationSubEffect(-100f));
    }

    private static Effect getCruzFilter(@NonNull final Context context) {
        return new Effect(
                context.getString(R.string.cruz),
                new SaturationSubEffect(-100f),
                new ContrastSubEffect(1.3f),
                new BrightnessSubEffect(20));
    }

    private static Effect getMetropolis(@NonNull final Context context) {
        return new Effect(
                context.getString(R.string.metropolis),
                new SaturationSubEffect(-1.0f),
                new ContrastSubEffect(1.7f),
                new BrightnessSubEffect(70));
    }

    private static Effect getAudreyFilter(@NonNull final Context context) {

        final Point[] redKnots = new Point[3];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(124, 138);
        redKnots[2] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.audrey),
                new SaturationSubEffect(-100f),
                new ContrastSubEffect(1.3f),
                new BrightnessSubEffect(20),
                new ToneCurveSubEffect(null, redKnots, null, null));
    }

    private static Effect getRiseFilter(@NonNull final Context context) {

        final Point[] blueKnots = new Point[4];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(39, 70);
        blueKnots[2] = new Point(150, 200);
        blueKnots[3] = new Point(255, 255);

        final Point[] redKnots = new Point[4];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(45, 64);
        redKnots[2] = new Point(170, 190);
        redKnots[3] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.rise),
                new ContrastSubEffect(1.9f),
                new BrightnessSubEffect(60),
                new VignetteSubfilter( false),
                new ToneCurveSubEffect(null, redKnots, null, blueKnots));
    }

    private static Effect getMarsFilter(@NonNull final Context context) {
        return new Effect(
                context.getString(R.string.mars),
                new ContrastSubEffect(1.5f),
                new BrightnessSubEffect(10));
    }

    private static Effect getAprilFilter(@NonNull final Context context) {

        final Point[] blueKnots = new Point[4];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(39, 70);
        blueKnots[2] = new Point(150, 200);
        blueKnots[3] = new Point(255, 255);

        final Point[] redKnots = new Point[4];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(45, 64);
        redKnots[2] = new Point(170, 190);
        redKnots[3] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.april),
                new ContrastSubEffect(1.5f),
                new BrightnessSubEffect(5),
                new VignetteSubfilter(false),
                new ToneCurveSubEffect(null, redKnots, null, blueKnots));
    }

    private static Effect getHaanFilter(@NonNull final Context context) {

        final Point[] greenKnots = new Point[3];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(113, 142);
        greenKnots[2] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.haan),
                new ContrastSubEffect(1.3f),
                new BrightnessSubEffect(60),
                new VignetteSubfilter(false),
                new ToneCurveSubEffect(null, null, greenKnots, null));
    }

    private static Effect getOldManFilter(@NonNull final Context context) {

        return new Effect(
                context.getString(R.string.oldman),
                new BrightnessSubEffect(30),
                new SaturationSubEffect(0.8f),
                new ContrastSubEffect(1.3f),
                new VignetteSubfilter(false),
                new ColorOverlaySubEffect(100, .2f, .2f, .1f));
    }

    private static Effect getClarendon(@NonNull final Context context) {

        final Point[] redKnots = new Point[4];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(56, 68);
        redKnots[2] = new Point(196, 206);
        redKnots[3] = new Point(255, 255);


        final Point[] greenKnots = new Point[4];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(46, 77);
        greenKnots[2] = new Point(160, 200);
        greenKnots[3] = new Point(255, 255);


        final Point[] blueKnots = new Point[4];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(33, 86);
        blueKnots[2] = new Point(126, 220);
        blueKnots[3] = new Point(255, 255);

        return new Effect(
                context.getString(R.string.clarendon),
                new ContrastSubEffect(1.5f),
                new BrightnessSubEffect(-10),
                new ToneCurveSubEffect(null, redKnots, greenKnots, blueKnots));
    }
}
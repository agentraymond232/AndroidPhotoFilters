package com.who.android.img.effects;


import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zomato.photoeffects.BuildConfig;
import com.who.android.img.effects.subeffects.interfaces.PerImageSubEffect;
import com.who.android.img.effects.subeffects.interfaces.PerPixelSubEffect;
import com.who.android.img.effects.subeffects.interfaces.SubEffect;

import static com.who.android.img.effects.Effect.ProcessorThread.THREADPOOL;

import com.who.android.img.effects.subeffects.BrightnessSubEffect;
import com.who.android.img.effects.subeffects.ColorOverlaySubEffect;
import com.who.android.img.effects.subeffects.ContrastSubEffect;
import com.who.android.img.effects.subeffects.SaturationSubEffect;
import com.who.android.img.effects.subeffects.ToneCurveSubEffect;
import com.who.android.img.effects.subeffects.VignetteSubfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.who.android.img.effects.Effect.ProcessorThread.POOLSIZE;

/**
 * This Class represents a ImageFilter and includes many subfilters within, we add different subfilters to this class's
 * object and they are then processed in that particular order
 */
public final class Effect {


    public static final String DEFAULTFILTER = "normal";
    /**
     * TODO: Change code such that we write the effected image onto file and only return a URI here.
     * @param effect
     * @param source
     * @param pixels
     * @return
     */
    public static Bitmap prepareThumbnail(final Effect effect, final Bitmap source,
                                          final int[] pixels) {

        if(effect.isDefault()) return source;
        final Bitmap filteredImage;

//        source.getPixels( pixels, 0, source.getWidth(), 0, 0, source.getWidth(),
//                source.getHeight());

        filteredImage = source.copy(Bitmap.Config.ARGB_8888, true);

        Effect.apply(effect, filteredImage, pixels);
//        effect.processFilter( pixels, filteredImage.getWidth(), filteredImage.getHeight());
//        filteredImage = Bitmap.createBitmap(pixels, source.getWidth(), source.getHeight(),
//                Bitmap.Config.ARGB_8888);


        return filteredImage;
    }

    public static void apply(final Effect effect, final Bitmap source,
                             final int[] pixels) {

        if(effect.isDefault()) return;

        source.getPixels( pixels, 0, source.getWidth(), 0, 0, source.getWidth(),
                source.getHeight());
        effect.processFilter(pixels, source.getWidth(), source.getHeight());
        source.setPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
    }

    /**
     * @param effect
     * @return
     */
    public static void applyAddOn(final Effect effect, final int[] pixels) {

        if(effect.isDefault()) return;

        if(effect.needsModifiableImage) throw new IllegalArgumentException("Addon Filters cannot be per image");

        effect.processPerPixelSubfilters(pixels);
    }




    @NonNull public static Effect replaceSubeffect(final Effect getEffect, final SubEffect subEffect){
        final Effect setEffect = (null == getEffect) ?
                new Effect("Add-on", subEffect) :
                getEffect.replaceSubFilter(subEffect);
        return setEffect;
    }

    private static void blockThread( final CountDownLatch latch){

        boolean tasksWereStuck = true;

        try {

            tasksWereStuck = !latch.await(1000, TimeUnit.MILLISECONDS);

        } catch (final InterruptedException e) {

            Log.e(Effect.class.getSimpleName(),
                    "ray232 processing might not have completed "
                            + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {
            if(tasksWereStuck)
                Log.e(Effect.class.getSimpleName(),
                        "ray232 processing incomplete ");
        }
    }

    final List<PerImageSubEffect> ImageSubFilters ;
    final List<PerPixelSubEffect> PixelSubFilters ;

    public final String Name;
    public final boolean needsModifiableImage;

    private Effect(Effect effect) {

        if(null == effect) throw new IllegalArgumentException();
        this.ImageSubFilters = effect.ImageSubFilters;
        this.PixelSubFilters = effect.PixelSubFilters;
        this.Name = effect.Name;
        needsModifiableImage = (null != ImageSubFilters && !this.ImageSubFilters.isEmpty());
    }

    public boolean isDefault(){
        return DEFAULTFILTER.equals(Name);
    }
    public Effect(final String name, final SubEffect... filters) {
        this(name, Arrays.asList(filters));
    }

    private Effect(final String name, final Iterable<SubEffect> subfilters) {

        ImageSubFilters = new ArrayList<>(1);
        PixelSubFilters = new ArrayList<>(2);

        if( null != subfilters) {
            for (final SubEffect subEffect : subfilters) {

                if (subEffect instanceof PerImageSubEffect)
                    ImageSubFilters.add((PerImageSubEffect) subEffect);

                else if (subEffect instanceof PerPixelSubEffect)
                    PixelSubFilters.add((PerPixelSubEffect) subEffect);

            }
        }
        this.Name = name;
        needsModifiableImage =  !this.ImageSubFilters.isEmpty();
    }

    public String getName() {
        return Name;
    }

    private static void replacePerImageSubFilter ( final List<PerImageSubEffect> list,
                                                   final PerImageSubEffect subFilter) {

        final int size = list.size();
        boolean subFilterAbsent = true;

        for ( int i = 0; i < size ; ++i){

            if(list.get(i).getTag().equals(subFilter.getTag())) {
                list.set(i, subFilter);
                subFilterAbsent = false;
            }
        }
        if(subFilterAbsent) list.add( subFilter);
    }

    private static void replacePerPixelSubFilter ( final List<PerPixelSubEffect> list,
                                                   final PerPixelSubEffect subFilter) {

        final int size = list.size();
        boolean subFilterAbsent = true;

        for ( int i = 0; i < size ; ++i){

            if(list.get(i).getTag().equals(subFilter.getTag())) {
                list.set(i, subFilter);
                subFilterAbsent = false;
            }
        }
        if(subFilterAbsent) list.add( subFilter);
    }

    /**
     * Adds a Subfilter to the Main Effect
     * Made package private intentionally so that only additional effects cab be added from the
     * clients.
     * @param subEffect Subfilter like contrast, brightness, tone Curve etc. subfilter
     * @see BrightnessSubEffect
     * @see ColorOverlaySubEffect
     * @see ContrastSubEffect
     * @see ToneCurveSubEffect
     * @see VignetteSubfilter
     * @see SaturationSubEffect
     */
    @NonNull public Effect replaceSubFilter(final SubEffect subEffect) {

        if(null == subEffect) return this;


        if(subEffect instanceof PerImageSubEffect)
            replacePerImageSubFilter( ImageSubFilters, (PerImageSubEffect) subEffect);

        else if (subEffect instanceof PerPixelSubEffect)
            replacePerPixelSubFilter( PixelSubFilters, (PerPixelSubEffect) subEffect);

        return new Effect(this);
    }

    /**
     * Clears all the subfilters from the Parent Effect
     */
    public void clearSubFilters() {
        ImageSubFilters.clear();
        PixelSubFilters.clear();
    }

    /**
     * Removes the subfilter containing Tag from the Parent Effect
     */
    public void removeSubFilterWithTag( final SubEffect.Tag tag) {


        for(final SubEffect filter : ImageSubFilters){
            if(tag.equals(filter.getTag()))
                    ImageSubFilters.remove(filter);
        }

        for(final SubEffect filter : PixelSubFilters){
            if(tag.equals(filter.getTag()))
                PixelSubFilters.remove(filter);
        }

    }

    /**
     * Returns The effect containing Tag
     */
    @Nullable public SubEffect getSubFilterByTag(SubEffect.Tag tag) {
        for (final SubEffect subEffect : ImageSubFilters) {
            if (subEffect.getTag().equals(tag)) {
                return subEffect;
            }
        }

        for (final SubEffect subEffect : PixelSubFilters) {
            if (subEffect.getTag().equals(tag)) {
                return subEffect;
            }
        }
        return null;
    }

    private void processFilter( final int[] pixelData, final int width, final int height){
        if(BuildConfig.DEBUG){

            if(null == pixelData || pixelData.length == 0)
                throw new IllegalArgumentException();

            if(needsModifiableImage && width * height != pixelData.length)
                throw new IllegalArgumentException();
        }

        processPerPixelSubfilters(pixelData);

        for (final PerImageSubEffect subFilter : ImageSubFilters) {
            subFilter.process( pixelData, width, height);
        }

    }

    private void processPerPixelSubfilters( final int[] pixelData){
        final CountDownLatch latch = new CountDownLatch(POOLSIZE);
        final int blockSize = pixelData.length / POOLSIZE;

        for(int i = 0; i < POOLSIZE; ++i){
            final int start = i * blockSize;
            final int end = Math.min((i + 1 ) * blockSize, pixelData.length);
            THREADPOOL.execute(new ProcessorThread(latch, pixelData, start, end, PixelSubFilters));
        }

        blockThread(latch);

    }

    static class ProcessorThread implements Runnable {

        static final int NUMCORES = Runtime.getRuntime().availableProcessors();;
        static final int POOLSIZE =
                (2 >= NUMCORES) ? 2 : (4 >= NUMCORES) ? 4 : 8 >= NUMCORES ? 8 : 16;
        static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();


        final CountDownLatch mLatch;
        final int mStart;
        final int mEnd;
        final Iterable<PerPixelSubEffect> mSubFilters;
        final int[] mPixels;

        ProcessorThread (final CountDownLatch latch, final int[] pixels, final int start,
                         final int end, final Iterable<PerPixelSubEffect> subFilter){

            mSubFilters = subFilter;
            mPixels = pixels;
            mLatch = latch;
            mStart = start;
            mEnd = end; //Math.min((step + 1 ) * mSubFilter.mBlockSize, mSubFilter.mDimensions);
        }

        @Override public void run() {
            for( final PerPixelSubEffect subFilter : mSubFilters)
                subFilter.process(mPixels, mStart, mEnd);
            mLatch.countDown();
        }
    }

}
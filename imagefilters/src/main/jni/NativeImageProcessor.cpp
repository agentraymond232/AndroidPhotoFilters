/* C++ Version */

#include <string.h>
#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include "cartoon.h"

using namespace cv;

extern "C" {

#define INT_BITS 32
#define UCHAR_BITS 8

/*Function to left rotate n by d bits*/
int leftRotate( const int n, const unsigned int d) {
    /* In n<<d, last d bits are 0. To put first d bits of n at
      last, do bitwise or of n<<d with n >>(INT_BITS - d) */
    return ((n << d) & 0xFFFFFF00)|((n >> (INT_BITS - d)) & 0x000000FF);
}

/*Function to right rotate n by d bits*/
int rightRotate(const int n, const unsigned int d) {
    /* In n>>d, first d bits are 0. To put last d bits of at
      first, do bitwise or of n>>d with n <<(INT_BITS - d) */
    return ((n >> d) & 0x00FFFFFF)| ((n << (INT_BITS - d)) & 0xFF000000);
}


static void exportMatToBitmap( const Mat& src, int* const pixels, const unsigned int height,
                        const unsigned int width) {

    const uchar * const pImgData =  src.data;

// loop through rows, columns and channels
    for ( unsigned int row = 0; row < src.rows && row < height; ++row) {
        for ( unsigned int column = 0; column < src.cols && column < width; ++column) {

            const unsigned int numPartsStep = src.channels() * ( src.cols * row + column);
            const unsigned int position = row * src.cols + column;
            const uint8_t blue = pImgData[ src.channels() * ( src.cols * row + column) + 0];
            const uint8_t green = pImgData[ src.channels() * ( src.cols * row + column) + 1];
            const uint8_t red = pImgData[ src.channels() * ( src.cols * row + column) + 2];

            pixels[position] = (pixels[position] & 0xFF000000) | ( (int)red << 16) & 0x00FF0000 |
                               ( (int)green << 8 ) & 0x0000FF00 | (int)blue & 0x000000FF;

        }
    }
}


//TODO 1. Make sure any enhancement/obstruction is not applied twice.
//TODO 2. Vignettify as enhancement, low balckness, Vignettify as obstruction, inversion of previous
static void exportGrayMatToBitmap( const Mat& src, int* const pixels, const unsigned int height,
                                   const unsigned int width, const bool isObstruction) {

    const uchar * const pImgData =  src.data;
    double red, green, blue;


// loop through rows, columns and channels

    /**
     * For now, the logic is that vignette obstruction is just vigenetting and grayscaling.
     * Vignette enhancement is simple vignetting.
     */

    for ( unsigned int row = 0; row < src.rows && row < height; ++row) {
        for ( unsigned int column = 0; column < src.cols && column < width; ++column) {

            const unsigned int position = row * src.cols + column;
            const double scale = (pImgData[/*(numChannels) * */position] )/ 255.0;


            if(isObstruction){
                red = scale * 255;
                green = red;
                blue = red;
            } else {
                red = ((pixels[position] >> 16) & 0xFF) * scale;
                green = ((pixels[position] >> 8) & 0xFF) * scale;
                blue = ((pixels[position]) & 0xFF) * scale;


                //validation check
                red = (red > 255) ? 255 : ( red < 0 ? 0 : red);
                green = (green > 255) ? 255 : ( green < 0 ? 0 : green);
                blue = (blue > 255) ? 255 : ( blue < 0 ? 0 : blue);

            }

//            pixels[position] = pixels[position] | (((int)alpha <<24) & 0xFF000000);
            pixels[position] = (pixels[position] & 0x00000000) | ( (int) red << 16) & 0x00FF0000 |
                               ( (int) green << 8 ) & 0x0000FF00 | (int) blue & 0x000000FF;

        }
    }
}

static unsigned int HSLtoRGB_Subfunction( const float &temp1, const float &temp2, const float &temp3) {
    if ((temp3 * 6) < 1)
        return  (unsigned int) ((temp2 + (temp1 - temp2) * 6 * temp3) * 100);
    else if ((temp3 * 2) < 1)
        return  (unsigned int) (temp1 * 100);
    else if ((temp3 * 3) < 2)
        return  (unsigned int) ((temp2 + (temp1 - temp2) * (.66666 - temp3) * 6) * 100);
    else
        return  (unsigned int) (temp2 * 100);
}

// This function extracts the hue, saturation, and luminance from "color"
// and places these values in h, s, and l respectively.
void saturation( int * const pixels, const float level, const unsigned int start,
                 const unsigned int end) {

    unsigned int r, g, b;
    float temp1, temp2, temp3;
    float rPercent, gPercent, bPercent;
    float maxColor, minColor;
    float l, s, h;

    for (unsigned int i = start; i < end; ++i) {
        r = (unsigned int) (pixels[i] >> 16) & 0xFF;
        g = (unsigned int) (pixels[i] >> 8) & 0xFF;
        b = (unsigned int) (pixels[i]) & 0xFF;

        rPercent = ((float) r) / 255;
        gPercent = ((float) g) / 255;
        bPercent = ((float) b) / 255;

        maxColor = ( rPercent >= gPercent && rPercent >= bPercent ) ? rPercent :
                   ( gPercent >= rPercent && gPercent >= bPercent ) ? gPercent : bPercent;
        minColor = ( rPercent <= gPercent && rPercent <= bPercent ) ? rPercent :
                   ( gPercent <= rPercent && gPercent <= bPercent ) ? gPercent : bPercent;

        l = (maxColor + minColor) / 2;

        if ( maxColor == minColor) { s = 0; h = 0; }
        else {
            s = 0.50 > l ? (maxColor - minColor) / (maxColor + minColor) :
                (maxColor - minColor) / (2 - maxColor - minColor);

            h = ( maxColor == rPercent ) ? (gPercent - bPercent) / (maxColor - minColor) :
                ( maxColor == gPercent ) ? 2 + (bPercent - rPercent) / (maxColor - minColor) :
                4 + (rPercent - gPercent) / (maxColor - minColor);
        }

        s *= 100 * level;
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
        r = (unsigned int) ((((float) r) / 100) * 255);
        g = (unsigned int) ((((float) g) / 100) * 255);
        b = (unsigned int) ((((float) b) / 100) * 255);

        pixels[i] = (pixels[i] & 0xFF000000) | (((int) r << 16) & 0x00FF0000) |
                (((int) g << 8) & 0x0000FF00) | ((int) b & 0x000000FF);

    }
}

static void colorOverlay( int * const pixels, const int depth, const float red, const float green,
                          const float blue, const unsigned int start, const unsigned int end) {

    float R, G, B;

    for ( unsigned int i = start; i < end; ++i) {
        // apply intensity level for sepid-toning on each channel
        R = ((pixels[i] >> 16) & 0xFF) + (depth * red);
        G = ((pixels[i] >> 8) & 0xFF) + (depth * green);
        B = ((pixels[i]) & 0xFF) + (depth * blue);

        R =  (R > 255) ? 255 : R;
        G = (G > 255) ? 255 : G;
        B = (B > 255) ? 255 : B;

        pixels[i] = pixels[i] & 0xFF000000 | ((int) R << 16) & 0x00FF0000 | ((int) G << 8) & 0x0000FF00 |
                    (int) B & 0x000000FF;
    }
}

//TODO Update args to const and optimize code wherever possible.
static void contrast( const unsigned int start, const unsigned int end, int * const pixels,
                      const float value) {

    float red, green, blue;

    for (unsigned int i = start; i < end; ++i) {
        red = (pixels[i] >> 16) & 0xFF;
        green = (pixels[i] >> 8) & 0xFF;
        blue = (pixels[i]) & 0xFF;

        red = (((((red / 255) - 0.5) * value) + 0.5) * 255);
        green = (((((green / 255) - 0.5) * value) + 0.5) * 255);
        blue = (((((blue / 255) - 0.5) * value) + 0.5) * 255);

        // validation check
        red = (red > 255) ? 255 : ( red < 0 ? 0 : red);
        green = (green > 255) ? 255 : ( green < 0 ? 0 : green);
        blue = (blue > 255) ? 255 : ( blue < 0 ? 0 : blue);

        pixels[i] = pixels[i] & 0xFF000000 | ((int) red << 16) & 0x00FF0000
                    | ((int) green << 8) & 0x0000FF00 | (int) blue & 0x000000FF;
    }
}

static void brightness( const unsigned int start, const unsigned int end, int* const pixels,
                        const int value) {

    int red, green, blue;

    for (unsigned int i = start; i < end; ++i) {
        red = ((pixels[i] >> 16) & 0xFF ) + value;
        green = ((pixels[i] >> 8) & 0xFF ) + value;
        blue = ((pixels[i]) & 0xFF ) + value;

        // validation check
        red = 255 < red ? 255 : 0 > red ? 0 : red;
        green = 255 < green ? 255 : 0 > green ? 0 : green;
        blue = 255 < blue ? 255 : 0 > blue ? 0 : blue;

        pixels[i] = pixels[i] & 0xFF000000 | (red << 16) & 0x00FF0000 | (green << 8) & 0x0000FF00
                    | blue & 0x000000FF;
    }
}


static void applyChannelCurves( const unsigned int start, const unsigned int end, int * const pixels,
                                int * const r, int * const g, int * const b) {
    unsigned int red;
    unsigned int green;
    unsigned int blue;
    unsigned int alpha;

    for (unsigned int i = start; i < end; ++i) {
        if ( NULL != r) red = (r[(pixels[i] >> 16) & 0xFF] << 16) & 0x00FF0000;
        else red = (pixels[i] << 16) & 0x00FF0000;

        if ( NULL != g ) green = (g[(pixels[i] >> 8) & 0xFF] << 8) & 0x0000FF00;
        else green = (pixels[i] << 8) & 0x0000FF00;

        if ( NULL != b ) blue = b[pixels[i] & 0xFF] & 0x000000FF;
        else blue = pixels[i] & 0x000000FF;

        alpha = pixels[i] & 0xFF000000;

        pixels[i] = alpha | red | green | blue;
    }
}

static void applyRGBCurve( const unsigned int start, const unsigned int end ,
                           int * const pixels, int * const rgb) {
    unsigned int R[256];
    unsigned int G[256];
    unsigned int B[256];
    for (unsigned short i = 0; i < 256; ++i) {
        R[i] = (rgb[i] << 16) & 0x00FF0000;
        G[i] = (rgb[i] << 8) & 0x0000FF00;
        B[i] = rgb[i] & 0x000000FF;
    }

    for (unsigned int i = start; i < end; ++i) {
        pixels[i] = 0xFF000000 & pixels[i] | R[(pixels[i] >> 16) & 0xFF] | G[(pixels[i] >> 8) & 0xFF]
                    | B[pixels[i] & 0xFF];
    }

}

static inline void invert( int * const pixels, const int start, const int end) {

    for (unsigned int i = start; i < end; ++i) {
        unsigned char R = ((pixels[i] >> 16) & 0xFF );
        unsigned char G = ((pixels[i] >> 8) & 0xFF );
        unsigned char B = ((pixels[i]) & 0xFF );
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;

        pixels[i] = pixels[i] & 0xFF000000 | (R & 0x000000FF) << 16 | ( G & 0x000000FF) << 8
                    | B & 0x000000FF;

    }
}

static inline jint *getPointerArray(JNIEnv *env, jintArray buff) {
    jint *ptrBuff = NULL;
    uint8_t var = false;
    if (buff != NULL)
        ptrBuff = env->GetIntArrayElements(buff, &var);
    return ptrBuff;
}


static inline jintArray jintToJintArray(JNIEnv *env, jint size, jint *arr) {
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, arr);
    return result;
}

static inline void releaseArray(JNIEnv *env, jintArray array1, jint *array2) {
    if (array1 != NULL)
        env->ReleaseIntArrayElements(array1, array2, 0);
}


JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_applyRGBCurve(JNIEnv *env, jobject thiz,
                                                                                jintArray pixels, jintArray rgb,
                                                                                jint start, jint end) {
    jint *pixelsBuff = getPointerArray(env, pixels);
    jint *RGBBuff = getPointerArray(env, rgb);
    applyRGBCurve(start, end, pixelsBuff, RGBBuff);
//    jintArray result = jintToJintArray(env, end -start, pixelsBuff);
    releaseArray(env, pixels, pixelsBuff);
    releaseArray(env, rgb, RGBBuff);
//    return result;
}

JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_applyChannelCurves(JNIEnv *env, jobject thiz,
                                                                                     jintArray pixels, jintArray r,
                                                                                     jintArray g, jintArray b,
                                                                                     jint start, jint end) {
    jint *pixelsBuff = getPointerArray(env, pixels);
    jint *RBuff = getPointerArray(env, r);
    jint *GBuff = getPointerArray(env, g);
    jint *BBuff = getPointerArray(env, b);
    applyChannelCurves( start, end, pixelsBuff, RBuff, GBuff, BBuff);
//    jintArray result = jintToJintArray(env, end - start, pixelsBuff);
    releaseArray(env, pixels, pixelsBuff);
    releaseArray(env, r, RBuff);
    releaseArray(env, g, GBuff);
    releaseArray(env, b, BBuff);
//    return result;
}

JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_doBrightness(JNIEnv *env, jobject thiz,
                                                                               jintArray pixels, jint value, jint start,
                                                                               jint end) {
    jint * pixelsBuff = getPointerArray(env, pixels);
    brightness(start, end , pixelsBuff, value);
    releaseArray(env, pixels, pixelsBuff);
}

JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_doContrast(JNIEnv *env, jobject thiz,
                                                                             jintArray pixels, jfloat value, jint start,
                                                                             jint end) {
    jint *pixelsBuff = getPointerArray(env, pixels);
    contrast(start, end, pixelsBuff, value);
//    jintArray result = jintToJintArray(env, end - start, pixelsBuff);
    releaseArray(env, pixels, pixelsBuff);
//    return result;
}

JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_doColorOverlay(JNIEnv *env, jobject thiz,
                                                                                 jintArray pixels, jint depth,
                                                                                 jfloat red, jfloat green, jfloat blue,
                                                                                 jint start, jint end) {
    jint *pixelsBuff = getPointerArray(env, pixels);
    colorOverlay(pixelsBuff, depth, red, green, blue, start, end);
//    jintArray result = jintToJintArray(env, end - start, pixelsBuff);
    releaseArray(env, pixels, pixelsBuff);
//    return result;
}

JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_doSaturation(JNIEnv *env, jobject thiz,
                                                                               jintArray pixels, jfloat level,

                                                                               jint start, jint end) {
    jint *pixelsBuff = getPointerArray(env, pixels);
    saturation(pixelsBuff, level, start, end);
//    jintArray result = jintToJintArray(env, end - start, pixelsBuff);
    releaseArray(env, pixels, pixelsBuff);
//    return result;
}

JNIEXPORT void
Java_com_who_android_img_NativeImageProcessor_doInvert(JNIEnv *env, jclass type, jintArray pixels,
                                                       jint start, jint end) {
    jint *pixelsBuff = getPointerArray(env, pixels);
    invert(pixelsBuff, start, end);
    // TODO
    releaseArray(env, pixels, pixelsBuff);
}

typedef Point3_<uint8_t> Pixel;


JNIEXPORT void JNICALL
Java_com_who_android_img_NativeImageProcessor_cartoonifyImage(JNIEnv *env, jobject instance,
                                                              jint width, jint height,
                                                              jintArray pixels,
                                                              jboolean sketchMode) {

    jint *pixelsBuff = getPointerArray( env, pixels);

    const unsigned int size = width * height;

    //Converting from ARGB to BGRA
    for(unsigned int i = 0; i < size; ++i){
        pixelsBuff[i] = leftRotate(pixelsBuff[i], UCHAR_BITS);
    }

    Mat bgra(height, width, CV_8UC4, pixelsBuff);
    Mat bgr(height, width, CV_8UC3);
    //Converting from BGRA to BGR
    cvtColor(bgra, bgr, CV_RGBA2BGR);
    Mat displayedFrame(bgra.size(), CV_8UC3);

    cartoonifyImage( bgr, displayedFrame, true);


    for(unsigned int i = 0; i < size; ++i){
        pixelsBuff[i] = rightRotate(pixelsBuff[i], UCHAR_BITS);
    }

    exportMatToBitmap(  displayedFrame  , pixelsBuff, height, width);

    displayedFrame.release();
    bgr.release();
    bgra.release();
    releaseArray(env, pixels, pixelsBuff);

}

JNIEXPORT void JNICALL
Java_com_who_android_img_NativeImageProcessor_vignettify(JNIEnv *env, jclass type, jint width,
                                                         jint height, jintArray pixels,
                                                         const int xStdDev, const int yStdDev,
                                                         const bool isObstruction) {
    jint *pixelsBuff = getPointerArray( env, pixels);

    Mat kernelX = getGaussianKernel(width, xStdDev);
    Mat kerneyY = getGaussianKernel(height, yStdDev);
    Mat kernelXT;

    transpose(kernelX, kernelXT);
    Mat kernel = kerneyY * kernelXT;
    Mat mask;

    normalize(kernel, mask, 0, 1, NORM_MINMAX);

    const unsigned int size = width * height;

    //Converting from ARGB to BGRA
    for(unsigned int i = 0; i < size; ++i){
        pixelsBuff[i] = leftRotate(pixelsBuff[i], UCHAR_BITS);
    }

    Mat bgra(height, width, CV_8UC4, pixelsBuff);
    Mat grayscale, displayedFrame;
    cvtColor(bgra, grayscale, CV_RGBA2GRAY);

    grayscale.convertTo(displayedFrame, CV_64F);
    multiply(mask, displayedFrame, displayedFrame);
    convertScaleAbs(displayedFrame, displayedFrame);

    for(unsigned int i = 0; i < size; ++i){
        pixelsBuff[i] = rightRotate(pixelsBuff[i], UCHAR_BITS);
    }
    exportGrayMatToBitmap( displayedFrame, pixelsBuff, height, width, isObstruction);

    kernelX.release();
    kerneyY.release();
    kernelXT.release();
    mask.release();
    kernel.release();
    displayedFrame.release();
    bgra.release();
    releaseArray(env, pixels, pixelsBuff);
}


}
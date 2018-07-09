LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_ROOT := E:/Portable_Executables/opencv-3.4.1-android-sdk/OpenCV-android-sdk
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCV_ROOT}/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := NativeImageProcessor
LOCAL_SRC_FILES := NativeImageProcessor.cpp cartoon.cpp cartoon.h ImageUtils.h ImageUtils.cpp

LOCAL_CPPFLAGS := -std=c++14
LOCAL_CFLAGS += -O2
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)

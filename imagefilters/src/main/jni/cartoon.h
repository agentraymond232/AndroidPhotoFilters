//
// Created by Raymond232 on 6/15/2018.
//
/*****************************************************************************
*   cartoon.h
*   Create a cartoon-like or painting-like image filter.
******************************************************************************
*   by Shervin Emami, 5th Dec 2012 (shervin.emami@gmail.com)
*   http://www.shervinemami.info/
******************************************************************************
*   Ch1 of the book "Mastering OpenCV with Practical Computer Vision Projects"
*   Copyright Packt Publishing 2012.
*   http://www.packtpub.com/cool-projects-with-opencv/book
*****************************************************************************/
#ifndef WHOUIANDROIDPHONE_CARTOON_H
#define WHOUIANDROIDPHONE_CARTOON_H

#pragma once


#include <stdio.h>
#include <iostream>
#include <vector>

// Include OpenCV's C++ Interface
#include "opencv2/opencv.hpp"


using namespace cv;
using namespace std;



// Convert the given photo into a cartoon-like or painting-like image.
// Set sketchMode to true if you want a line drawing instead of a painting.
// Set alienMode to true if you want alien skin instead of human.
// Set evilMode to true if you want an "evil" character instead of a "good" character.
// Set debugType to 1 to show where skin color is taken from, and 2 to show the skin mask in a new window (for desktop).
void cartoonifyImage(Mat srcColor, Mat dst, const bool sketchMode,
                     const bool alienMode = false, const bool evilMode = false, const int debugType = 1);

void cartoonify( Mat& src);

// Draw an anti-aliased face outline, so the user knows where to put their face.
void drawFaceStickFigure(Mat dst);


void changeFacialSkinColor(Mat smallImgBGR, Mat bigEdges, int debugType);
void removePepperNoise(Mat &mask);
void drawFaceStickFigure(Mat dst);


#endif //WHOUIANDROIDPHONE_CARTOON_H

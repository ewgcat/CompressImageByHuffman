
#ifndef COMPRESS_COMPRESS_H_H
#define COMPRESS_COMPRESS_H_H

#include "lang.h"
#include <stdlib.h>
#include <android/bitmap.h>

#include <setjmp.h>
#include <jpeglib.h>

#define true 1
#define false 0

//#ifdef __cplusplus
//extern "C" {
//#endif
JNIEXPORT jint JNICALL
Java_com_lishuaihua_compress_ImageCompress_nativeCompressBitmap(JNIEnv *env, jclass type,
                                                               jobject bitmap, jint quality,
                                                               jstring dstFile_,
                                                               jboolean optimize);
//
//#ifdef __cplusplus
//}
//#endif
#endif //COMPRESS_COMPRESS_H_H

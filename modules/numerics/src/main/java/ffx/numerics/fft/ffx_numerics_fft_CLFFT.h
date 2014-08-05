/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ffx_numerics_fft_CLFFT */

#ifndef _Included_ffx_numerics_fft_CLFFT
#define _Included_ffx_numerics_fft_CLFFT
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ffx_numerics_fft_CLFFT
 * Method:    do_clfftSetup
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_ffx_numerics_fft_CLFFT_do_1clfftSetup
  (JNIEnv *, jclass);

/*
 * Class:     ffx_numerics_fft_CLFFT
 * Method:    do_clfftCreateDefaultPlan
 * Signature: (JJIIII)J
 */
JNIEXPORT jlong JNICALL Java_ffx_numerics_fft_CLFFT_do_1clfftCreateDefaultPlan
  (JNIEnv *, jclass, jlong, jlong, jint, jint, jint, jint);

/*
 * Class:     ffx_numerics_fft_CLFFT
 * Method:    do_clfftSetPlanPrecision
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_ffx_numerics_fft_CLFFT_do_1clfftSetPlanPrecision
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     ffx_numerics_fft_CLFFT
 * Method:    do_clfftSetLayout
 * Signature: (JII)I
 */
JNIEXPORT jint JNICALL Java_ffx_numerics_fft_CLFFT_do_1clfftSetLayout
  (JNIEnv *, jclass, jlong, jint, jint);

/*
 * Class:     ffx_numerics_fft_CLFFT
 * Method:    do_clfftExecuteTransform
 * Signature: (JIJJJ)I
 */
JNIEXPORT jint JNICALL Java_ffx_numerics_fft_CLFFT_do_1clfftExecuteTransform
  (JNIEnv *, jclass, jlong, jint, jlong, jlong, jlong);

/*
 * Class:     ffx_numerics_fft_CLFFT
 * Method:    do_clfftDestroyPlan
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_ffx_numerics_fft_CLFFT_do_1clfftDestroyPlan
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class benchmark_Benchmark */

#ifndef _Included_benchmark_Benchmark
#define _Included_benchmark_Benchmark
#ifdef __cplusplus
extern "C" {
#endif
#undef benchmark_Benchmark__TIMEOUT_SECONDS
#define benchmark_Benchmark__TIMEOUT_SECONDS 120L
#undef benchmark_Benchmark__TEST_INTEGER_SORT
#define benchmark_Benchmark__TEST_INTEGER_SORT 1L
#undef benchmark_Benchmark__TEST_AES_ENCRYPT
#define benchmark_Benchmark__TEST_AES_ENCRYPT 2L
#undef benchmark_Benchmark__TEST_AES_DECRYPT
#define benchmark_Benchmark__TEST_AES_DECRYPT 3L
#undef benchmark_Benchmark__TEST_SHA_256
#define benchmark_Benchmark__TEST_SHA_256 4L
#undef benchmark_Benchmark__TEST_STRING
#define benchmark_Benchmark__TEST_STRING 5L
#undef benchmark_Benchmark__TEST_STREAM
#define benchmark_Benchmark__TEST_STREAM 6L
#undef benchmark_Benchmark__TEST_MAX_COUNT
#define benchmark_Benchmark__TEST_MAX_COUNT 6L
/*
 * Class:     benchmark_Benchmark
 * Method:    firstConnectN
 * Signature: (Ljava/lang/String;Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_benchmark_Benchmark_firstConnectN
  (JNIEnv *, jclass, jstring, jstring, jint);

/*
 * Class:     benchmark_Benchmark
 * Method:    okayToBeginN
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_benchmark_Benchmark_okayToBeginN
  (JNIEnv *, jclass, jint);

/*
 * Class:     benchmark_Benchmark
 * Method:    reportTestN
 * Signature: (IILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_benchmark_Benchmark_reportTestN
  (JNIEnv *, jclass, jint, jint, jstring);

/*
 * Class:     benchmark_Benchmark
 * Method:    reportCompletionN
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_benchmark_Benchmark_reportCompletionN
  (JNIEnv *, jclass, jint, jfloat);

/*
 * Class:     benchmark_Benchmark
 * Method:    streamN
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_benchmark_Benchmark_streamN
  (JNIEnv *, jclass, jint, jint);

#ifdef __cplusplus
}
#endif
#endif

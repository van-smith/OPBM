/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class opbm_Opbm */

#ifndef _Included_opbm_Opbm
#define _Included_opbm_Opbm
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     opbm_Opbm
 * Method:    sendWindowToForeground
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_opbm_Opbm_sendWindowToForeground
  (JNIEnv *, jclass, jstring);

/*
 * Class:     opbm_Opbm
 * Method:    getHarnessCSVDirectory
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessCSVDirectory
  (JNIEnv *, jclass);

/*
 * Class:     opbm_Opbm
 * Method:    getHarnessXMLDirectory
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_opbm_Opbm_getHarnessXMLDirectory
  (JNIEnv *, jclass);

/*
 * Class:     opbm_Opbm
 * Method:    getScriptCSVDirectory
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_opbm_Opbm_getScriptCSVDirectory
  (JNIEnv *, jclass);

/*
 * Class:     opbm_Opbm
 * Method:    getSettingsDirectory
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_opbm_Opbm_getSettingsDirectory
  (JNIEnv *, jclass);

/*
 * Class:     opbm_Opbm
 * Method:    snapshotProcesses
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_opbm_Opbm_snapshotProcesses
  (JNIEnv *, jclass);

/*
 * Class:     opbm_Opbm
 * Method:    stopProcesses
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_opbm_Opbm_stopProcesses
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif

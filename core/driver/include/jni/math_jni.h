#pragma once
#include <jni.h>
#include "frcsim/math/vector.hpp"
#include "frcsim/math/quaternion.hpp"
#include "frcsim/math/matrix.hpp"

#ifdef __cplusplus
extern "C" {
#endif

// Vector3 JNI
JNIEXPORT jlong JNICALL Java_api_Vector3_nativeCreate(JNIEnv*, jobject, jdouble, jdouble, jdouble);
JNIEXPORT jdouble JNICALL Java_api_Vector3_nativeNorm(JNIEnv*, jobject, jlong);
JNIEXPORT jdouble JNICALL Java_api_Vector3_nativeDot(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Vector3_nativeCross(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Vector3_nativeAdd(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Vector3_nativeSub(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Vector3_nativeScale(JNIEnv*, jobject, jlong, jdouble);
JNIEXPORT jdouble JNICALL Java_api_Vector3_nativeGetX(JNIEnv*, jobject, jlong);
JNIEXPORT jdouble JNICALL Java_api_Vector3_nativeGetY(JNIEnv*, jobject, jlong);
JNIEXPORT jdouble JNICALL Java_api_Vector3_nativeGetZ(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_api_Vector3_nativeDelete(JNIEnv*, jobject, jlong);

// Quaternion JNI
JNIEXPORT jlong JNICALL Java_api_Quaternion_nativeCreate(JNIEnv*, jobject, jdouble, jdouble, jdouble, jdouble);
JNIEXPORT jlong JNICALL Java_api_Quaternion_nativeFromAxisAngle(JNIEnv*, jclass, jlong, jdouble);
JNIEXPORT jlong JNICALL Java_api_Quaternion_nativeMultiply(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Quaternion_nativeRotate(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Quaternion_nativeNormalize(JNIEnv*, jobject, jlong);
JNIEXPORT jlong JNICALL Java_api_Quaternion_nativeConjugate(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_api_Quaternion_nativeDelete(JNIEnv*, jobject, jlong);

// Matrix3 JNI
JNIEXPORT jlong JNICALL Java_api_Matrix3_nativeCreate(JNIEnv*, jobject);
JNIEXPORT jlong JNICALL Java_api_Matrix3_nativeFromQuaternion(JNIEnv*, jclass, jlong);
JNIEXPORT jlong JNICALL Java_api_Matrix3_nativeMultiply(JNIEnv*, jobject, jlong, jlong);
JNIEXPORT jlong JNICALL Java_api_Matrix3_nativeTransform(JNIEnv*, jobject, jlong, jlong); // matrix * vector
JNIEXPORT jlong JNICALL Java_api_Matrix3_nativeTranspose(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_api_Matrix3_nativeDelete(JNIEnv*, jobject, jlong);

// Physics JNI (world/body/ball)
JNIEXPORT jlong JNICALL Java_api_NativePhysics_nativeCreateWorld(JNIEnv*, jclass);
JNIEXPORT void JNICALL Java_api_NativePhysics_nativeDestroyWorld(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_api_NativePhysics_nativeStepWorld(JNIEnv*, jclass, jlong, jdouble);

JNIEXPORT jlong JNICALL Java_api_NativePhysics_nativeCreateBody(JNIEnv*, jclass, jlong, jdouble);
JNIEXPORT void JNICALL Java_api_NativePhysics_nativeSetBodyBoxGeometry(JNIEnv*, jclass, jlong, jdouble, jdouble, jdouble);
JNIEXPORT void JNICALL Java_api_NativePhysics_nativeSetBodySphereGeometry(JNIEnv*, jclass, jlong, jdouble);
JNIEXPORT void JNICALL Java_api_NativePhysics_nativeSetBodyPosition(JNIEnv*, jclass, jlong, jdouble, jdouble, jdouble);

JNIEXPORT jlong JNICALL Java_api_NativePhysics_nativeCreateBall(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_api_NativePhysics_nativeBallShoot(JNIEnv*, jclass, jlong, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble);
JNIEXPORT jdoubleArray JNICALL Java_api_NativePhysics_nativeGetBallState(JNIEnv*, jclass, jlong);
#ifdef __cplusplus
}
#endif

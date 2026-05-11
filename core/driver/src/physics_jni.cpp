#include "jni/math_jni.h"
#include <memory>

#define PTR(T, ptr) reinterpret_cast<T*>(ptr)
#define JPTR(obj) reinterpret_cast<jlong>(obj)

// Physics JNI
JNIEXPORT jlong JNICALL Java_api_NativePhysics_nativeCreateWorld(JNIEnv*, jclass) {
    return JPTR(new frcsim::PhysicsWorld());
}

JNIEXPORT void JNICALL Java_api_NativePhysics_nativeDestroyWorld(JNIEnv*, jclass, jlong worldPtr) {
    delete PTR(frcsim::PhysicsWorld, worldPtr);
}

JNIEXPORT void JNICALL Java_api_NativePhysics_nativeStepWorld(JNIEnv*, jclass, jlong worldPtr, jdouble dt) {
    PTR(frcsim::PhysicsWorld, worldPtr)->config().fixed_dt_s = (dt > 0.0) ? dt : PTR(frcsim::PhysicsWorld, worldPtr)->config().fixed_dt_s;
    PTR(frcsim::PhysicsWorld, worldPtr)->step();
}

JNIEXPORT jlong JNICALL Java_api_NativePhysics_nativeCreateBody(JNIEnv*, jclass, jlong worldPtr, jdouble massKg) {
    return JPTR(&PTR(frcsim::PhysicsWorld, worldPtr)->createBody(massKg));
}

JNIEXPORT void JNICALL Java_api_NativePhysics_nativeSetBodyBoxGeometry(JNIEnv*, jclass, jlong bodyPtr, jdouble dx, jdouble dy, jdouble dz) {
    frcsim::RigidBody::AerodynamicGeometry g;
    g.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kBox;
    g.box_dimensions_m = frcsim::Vector3(dx, dy, dz);
    PTR(frcsim::RigidBody, bodyPtr)->setAerodynamicGeometry(g);
}

JNIEXPORT void JNICALL Java_api_NativePhysics_nativeSetBodySphereGeometry(JNIEnv*, jclass, jlong bodyPtr, jdouble r) {
    frcsim::RigidBody::AerodynamicGeometry g;
    g.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kSphere;
    g.radius_m = r;
    PTR(frcsim::RigidBody, bodyPtr)->setAerodynamicGeometry(g);
}

JNIEXPORT void JNICALL Java_api_NativePhysics_nativeSetBodyPosition(JNIEnv*, jclass, jlong bodyPtr, jdouble x, jdouble y, jdouble z) {
    PTR(frcsim::RigidBody, bodyPtr)->setPosition(x, y, z);
}

JNIEXPORT jlong JNICALL Java_api_NativePhysics_nativeCreateBall(JNIEnv*, jclass, jlong worldPtr) {
    return JPTR(&PTR(frcsim::PhysicsWorld, worldPtr)->createBall());
}

JNIEXPORT void JNICALL Java_api_NativePhysics_nativeBallShoot(JNIEnv*, jclass, jlong ballPtr, jdouble px, jdouble py, jdouble pz, jdouble vx, jdouble vy, jdouble vz) {
    PTR(frcsim::BallPhysicsSim3D, ballPtr)->shoot(frcsim::Vector3(px, py, pz), frcsim::Vector3(vx, vy, vz));
}

JNIEXPORT jdoubleArray JNICALL Java_api_NativePhysics_nativeGetBallState(JNIEnv* env, jclass, jlong ballPtr) {
    const auto& s = PTR(frcsim::BallPhysicsSim3D, ballPtr)->state();
    jdouble out[6];
    out[0] = s.position_m.x; out[1] = s.position_m.y; out[2] = s.position_m.z;
    out[3] = s.velocity_mps.x; out[4] = s.velocity_mps.y; out[5] = s.velocity_mps.z;
    jdoubleArray arr = env->NewDoubleArray(6);
    env->SetDoubleArrayRegion(arr, 0, 6, out);
    return arr;
}

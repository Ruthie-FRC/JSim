package api;

public final class NativePhysics {

    // World
    public static native long nativeCreateWorld();
    public static native void nativeDestroyWorld(long worldPtr);
    public static native void nativeStepWorld(long worldPtr, double dt_s);

    // Body
    public static native long nativeCreateBody(long worldPtr, double massKg);
    public static native void nativeSetBodyBoxGeometry(long bodyPtr, double dx, double dy, double dz);
    public static native void nativeSetBodySphereGeometry(long bodyPtr, double radius);
    public static native void nativeSetBodyPosition(long bodyPtr, double x, double y, double z);

    // Ball
    public static native long nativeCreateBall(long worldPtr);
    public static native void nativeBallShoot(long ballPtr, double px, double py, double pz, double vx, double vy, double vz);
    public static native double[] nativeGetBallState(long ballPtr);

    static {
        System.loadLibrary("frcsim_jni");
    }
}

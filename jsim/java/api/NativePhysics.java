package api;

/**
 * Java JNI bindings for the native physics core (jsimcore).
 *
 * This class exposes thin static wrappers around the native functions
 * implemented in the C++ JNI layer.
 */
public final class NativePhysics {
    private NativePhysics() {}

    static {
        // Ensure native core is loaded when this API is used.
        try {
            System.loadLibrary("jsimcore");
        } catch (UnsatisfiedLinkError e) {
            // Let callers handle missing library errors when invoking native methods.
        }
    }

    // World lifecycle
    private static native long nativeCreateWorld();
    private static native void nativeDestroyWorld(long worldHandle);
    private static native void nativeStepWorld(long worldHandle, double dtSeconds);

    // Body/geometry
    private static native long nativeCreateBody(long worldHandle, double massKg);
    private static native void nativeSetBodyBoxGeometry(long bodyHandle, double x, double y, double z);
    private static native void nativeSetBodySphereGeometry(long bodyHandle, double radius);
    private static native void nativeSetBodyPosition(long bodyHandle, double x, double y, double z);

    // Ball helpers
    private static native long nativeCreateBall(long worldHandle);
    private static native void nativeBallShoot(long ballHandle, double vx, double vy, double vz, double wx, double wy, double wz);
    private static native double[] nativeGetBallState(long ballHandle);

    // Java-friendly wrappers
    public static long createWorld() { return nativeCreateWorld(); }
    public static void destroyWorld(long h) { nativeDestroyWorld(h); }
    public static void stepWorld(long h, double dt) { nativeStepWorld(h, dt); }

    public static long createBody(long worldHandle, double massKg) { return nativeCreateBody(worldHandle, massKg); }
    public static void setBodyBoxGeometry(long bodyHandle, double x, double y, double z) { nativeSetBodyBoxGeometry(bodyHandle, x, y, z); }
    public static void setBodySphereGeometry(long bodyHandle, double radius) { nativeSetBodySphereGeometry(bodyHandle, radius); }
    public static void setBodyPosition(long bodyHandle, double x, double y, double z) { nativeSetBodyPosition(bodyHandle, x, y, z); }

    public static long createBall(long worldHandle) { return nativeCreateBall(worldHandle); }
    public static void ballShoot(long ballHandle, double vx, double vy, double vz, double wx, double wy, double wz) {
        nativeBallShoot(ballHandle, vx, vy, vz, wx, wy, wz);
    }
    public static double[] getBallState(long ballHandle) { return nativeGetBallState(ballHandle); }
}

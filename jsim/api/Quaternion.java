package api;

/**
 * Native Quaternion backed by C++ core (JNI).
 */
public class Quaternion {
    private long nativePtr;

    /**
     * Constructs a native quaternion.
     */
    public Quaternion(double w, double x, double y, double z) {
        nativePtr = nativeCreate(w, x, y, z);
    }

    /**
     * Creates a quaternion from axis and angle.
     */
    public static Quaternion fromAxisAngle(Vector3 axis, double angleRad) {
    long ptr = ((axis != null) ? axis.getNativePtr() : 0);
    long qptr = nativeFromAxisAngle(ptr, angleRad);
    Quaternion q = new Quaternion(0,0,0,0);
    q.nativePtr = qptr;
    return q;
    }

    /**
     * Multiplies this quaternion by another.
     */
    public Quaternion multiply(Quaternion o) {
        long ptr = nativeMultiply(this.nativePtr, o.nativePtr);
        Quaternion q = new Quaternion(0,0,0,0);
        q.nativePtr = ptr;
        return q;
    }

    /**
     * Rotates a vector by this quaternion.
     */
    public Vector3 rotate(Vector3 v) {
    long vptr = (v != null) ? v.getNativePtr() : 0;
    long ptr = nativeRotate(this.nativePtr, vptr);
    return new Vector3(ptr);
    }

    /**
     * Releases native memory.
     */
    public void dispose() {
        nativeDelete(nativePtr);
        nativePtr = 0;
    }

    // JNI native methods
    private native long nativeCreate(double w, double x, double y, double z);
    private static native long nativeFromAxisAngle(long axisPtr, double angle);
    private native long nativeMultiply(long ptrA, long ptrB);
    private native long nativeRotate(long qPtr, long vPtr);
    private native void nativeDelete(long ptr);

    // For internal JNI pointer passing
    long getNativePtr() { return nativePtr; }

    static {
        System.loadLibrary("jsimcore");
    }
}

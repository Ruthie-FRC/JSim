package api;

/**
 * Native Vector3 backed by C++ core (JNI).
 */
public class Vector3 {
    private long nativePtr;

    /**
     * Constructs a native vector.
     * @param x X value
     * @param y Y value
     * @param z Z value
     */
    public Vector3(double x, double y, double z) {
        nativePtr = nativeCreate(x, y, z);
    }

    /**
     * Internal JNI constructor from pointer.
     */
    Vector3(long ptr) {
        this.nativePtr = ptr;
    }

    /**
     * Returns the native pointer (for JNI use).
     */
    long getNativePtr() {
        return nativePtr;
    }

    /**
     * Returns the norm (magnitude) of the vector.
     */
    public double norm() {
        return nativeNorm(nativePtr);
    }

    /**
     * Dot product with another vector.
     */
    public double dot(Vector3 o) {
        long otherPtr = (o != null) ? o.getNativePtr() : 0;
        return nativeDot(nativePtr, otherPtr);
    }

    /**
     * Cross product with another vector.
     */
    public Vector3 cross(Vector3 o) {
        long otherPtr = (o != null) ? o.getNativePtr() : 0;
        long ptr = nativeCross(nativePtr, otherPtr);
        return new Vector3(ptr);
    }

    /**
     * Add another vector.
     */
    public Vector3 add(Vector3 o) {
        long otherPtr = (o != null) ? o.getNativePtr() : 0;
        long ptr = nativeAdd(nativePtr, otherPtr);
        return new Vector3(ptr);
    }

    /**
     * Subtract another vector.
     */
    public Vector3 sub(Vector3 o) {
        long otherPtr = (o != null) ? o.getNativePtr() : 0;
        long ptr = nativeSub(nativePtr, otherPtr);
        return new Vector3(ptr);
    }

    /**
     * Scale by a scalar.
     */
    public Vector3 scale(double s) {
        long ptr = nativeScale(nativePtr, s);
        return new Vector3(ptr);
    }

    /** X/Y/Z accessors */
    public double getX() { return nativeGetX(nativePtr); }
    public double getY() { return nativeGetY(nativePtr); }
    public double getZ() { return nativeGetZ(nativePtr); }

    /**
     * Releases native memory.
     */
    public void dispose() {
        nativeDelete(nativePtr);
        nativePtr = 0;
    }

    private native long nativeCreate(double x, double y, double z);
    private native double nativeNorm(long ptr);
    private native double nativeDot(long ptrA, long ptrB);
    private native long nativeCross(long ptrA, long ptrB);
    private native long nativeAdd(long ptrA, long ptrB);
    private native long nativeSub(long ptrA, long ptrB);
    private native long nativeScale(long ptr, double s);
    private native double nativeGetX(long ptr);
    private native double nativeGetY(long ptr);
    private native double nativeGetZ(long ptr);
    private native void nativeDelete(long ptr);
}

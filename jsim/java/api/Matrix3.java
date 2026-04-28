package api;

/**
 * Native Matrix3 backed by C++ core (JNI).
 */
public class Matrix3 {
    private long nativePtr;

    /**
     * Constructs a native identity matrix.
     */
    public Matrix3() {
        nativePtr = nativeCreate();
    }

    /**
     * Multiplies this matrix by a vector.
     */
    public Vector3 multiply(Vector3 v) {
        long ptr = nativeMultiply(this.nativePtr, v.getNativePtr());
        return new Vector3(ptr);
    }

    /**
     * Releases native memory.
     */
    public void dispose() {
        nativeDelete(nativePtr);
        nativePtr = 0;
    }

    private native long nativeCreate();
    private native long nativeMultiply(long mPtr, long vPtr);
    private native void nativeDelete(long ptr);

    static {
        System.loadLibrary("jsimcore");
    }
}

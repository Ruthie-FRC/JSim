// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

/**
 * Runtime exception type for JSim native operation failures.
 *
 * <p>A well-formed JSimException answers: what happened (operation), how it
 * happened (native return code), and how to fix it (suggestion).
 */
public final class JSimException extends RuntimeException {
    /** Native return code from the failing JNI call. Non-zero indicates an error. */
    private final int rc;

    public JSimException(String operation, int rc, String suggestion) {
        super(buildMessage(operation, rc, suggestion));
        this.rc = rc;
    }

    private static String buildMessage(String operation, int rc, String suggestion) {
        StringBuilder sb = new StringBuilder();
        sb.append(operation);
        sb.append("; native return code=");
        sb.append(rc);
        sb.append(". How it happened: native code returned an error. How to fix: ");
        sb.append(suggestion);
        return sb.toString();
    }

    /**
     * Returns the native return code associated with this failure.
     *
     * @return native return code (non-zero indicates an error)
     */
    public int getRc() {
        return rc;
    }
}

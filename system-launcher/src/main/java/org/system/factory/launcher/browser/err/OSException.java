package org.system.factory.launcher.browser.err;


public class OSException extends java.io.IOException {

    private static final String MSG =
            "The operating system used could not be determined or is currently not supported.";

    public OSException() {
        super(OSException.MSG);
    }

    @Override public String toString() {
        return OSException.MSG;
    }
}

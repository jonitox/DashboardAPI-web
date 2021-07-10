package com.kakao.test1.exception;

public class InvalidK8sContextException extends Exception {
    public InvalidK8sContextException() {
        super();
    }

    public InvalidK8sContextException(String message) {
        super(message);
    }

    public InvalidK8sContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidK8sContextException(Throwable cause) {
        super(cause);
    }

    protected InvalidK8sContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

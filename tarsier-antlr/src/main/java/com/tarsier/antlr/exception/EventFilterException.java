package com.tarsier.antlr.exception;


public class EventFilterException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            errorCode;

    public EventFilterException(Throwable cause) {
        super(cause);
    }

    public EventFilterException(String msg) {
        super(msg);
    }

    public EventFilterException(String msg, Object... strings) {
        super(String.format(msg.replaceAll("\\{\\}", "%s"), strings));
    }

    public EventFilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventFilterException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
package com.mugitek.euskaldc.eventos;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class ConnectionErrorEvent {
    private int errorCode;
    private String errorMessage;

    public ConnectionErrorEvent(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

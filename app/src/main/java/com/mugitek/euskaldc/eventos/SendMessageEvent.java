package com.mugitek.euskaldc.eventos;

import java.nio.charset.Charset;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class SendMessageEvent {
    private String userSid;
    private String message;

    public SendMessageEvent(String userSid, String message) {
        this.userSid = userSid;
        this.message = message;
    }

    public String getUserSid() {
        return userSid;
    }

    public void setUserSid(String userSid) {
        this.userSid = userSid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

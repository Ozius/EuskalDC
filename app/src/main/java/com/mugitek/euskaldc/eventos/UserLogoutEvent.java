package com.mugitek.euskaldc.eventos;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class UserLogoutEvent {
    private String sid;

    public UserLogoutEvent(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}

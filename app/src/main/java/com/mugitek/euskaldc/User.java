package com.mugitek.euskaldc;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class User {
    private String sid;
    private String cid;
    private String nick;
    private String description;

    public User(String sid, String cid, String nick, String description) {
        this.sid = sid;
        this.cid = cid;
        this.nick = nick;
        this.description = description;
    }

    public String getSid() {
        return sid;
    }

    public String getCid() {
        return cid;
    }

    public String getNick() {
        return nick;
    }

    public String getDescription() {
        return description;
    }
}

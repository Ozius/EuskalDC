package com.mugitek.euskaldc.eventos;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class UserLoginEvent {
    private String sid;
    private String cid;
    private String nick;
    private String description;
    private int sockets;
    private long shared;

    public UserLoginEvent(String sid, String cid, String nick, String description) {
        this.sid = sid;
        this.cid = cid;
        this.nick = nick;
        this.description = description;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSockets() {
        return sockets;
    }

    public void setSockets(int sockets) {
        this.sockets = sockets;
    }

    public long getShared() {
        return shared;
    }

    public void setShared(long shared) {
        this.shared = shared;
    }
}

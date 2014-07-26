package com.mugitek.euskaldc.eventos;

/**
 * Created by Arkaitz on 25/07/2014.
 */
public class ConnectEvent {
    private boolean isSsl = true;
    private String server;
    private int puerto;
    private String nick;

    public ConnectEvent(String server, int puerto, String nick) {
        this.server = server;
        this.puerto = puerto;
        this.nick = nick;
    }

    public String getServer() {
        return server;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getNick() {
        return nick;
    }
    public boolean isSsl() {
        return isSsl;
    }
    public void setIsSsl(boolean isSsl) {
        this.isSsl = isSsl;
    }
}

package com.mugitek.euskaldc;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class Mensaje {
    private String user;
    private String mensaje;

    public Mensaje(String user, String mensaje){
        this.user = user;
        this.mensaje = mensaje;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

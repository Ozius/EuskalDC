package com.mugitek.euskaldc;

import android.text.format.Time;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class Mensaje {
    private String user;
    private String mensaje;
    private Time time;

    public Mensaje(String user, String mensaje){
        this.user = user;
        this.mensaje = mensaje;
        this.time = new Time();
        this.time.setToNow();
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

    public String getTime(){
        return time.format("%H:%M:%S");
    }
}

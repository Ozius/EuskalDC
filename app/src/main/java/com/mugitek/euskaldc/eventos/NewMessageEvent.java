package com.mugitek.euskaldc.eventos;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class NewMessageEvent {
    private String mensaje;

    public NewMessageEvent(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

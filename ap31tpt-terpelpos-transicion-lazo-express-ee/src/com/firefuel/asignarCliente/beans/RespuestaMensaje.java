package com.firefuel.asignarCliente.beans;

public class RespuestaMensaje {

    private String mensaje;
    private boolean error;
    private String iconMensaje;//Ruta

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getIconMensaje() {
        return iconMensaje;
    }

    public void setIconMensaje(String iconMensaje) {
        this.iconMensaje = iconMensaje;
    }

}

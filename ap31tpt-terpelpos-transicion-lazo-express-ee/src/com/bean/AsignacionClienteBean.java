package com.bean;

import com.google.gson.JsonObject;
import java.util.concurrent.ConcurrentHashMap;

public class AsignacionClienteBean {

    private static ConcurrentHashMap<String, JsonObject> datosCliente = new ConcurrentHashMap<>();

    public static void agregarInformacionCliente(String clave, JsonObject informacion) {
        datosCliente.put(clave, informacion);
    }

    public static ConcurrentHashMap<String, JsonObject> getDatosCliente() {
        return datosCliente;
    }
    public static JsonObject obtenerInformacionCliente(String clave) {
        return datosCliente.getOrDefault(clave, null);
    }


}

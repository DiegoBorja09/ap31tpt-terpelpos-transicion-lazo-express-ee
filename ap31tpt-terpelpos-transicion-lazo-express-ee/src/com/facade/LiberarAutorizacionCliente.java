/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade;

import com.controllers.NovusConstante;
import com.dao.ClientePropioDao;
import com.firefuel.Main;
import com.google.gson.JsonObject;

/**
 *
 * @author Devitech
 */
public class LiberarAutorizacionCliente {

    static final String IDENTIFICADOR_CUPO = "identificadorCupo";

    public void generarLiberacionPorcancelacion(JsonObject tramaClientePropio, long idPromotor, String odometro) {

        String url = NovusConstante.getServer(NovusConstante.SECURE_CENTRAL_POINT_LIBER_AUTORIZACION_CLIENTES_PROPIOS);
        JsonObject trama = tramaClientePropio;
        trama.addProperty("tipoVenta", 10);
        trama.addProperty("isCredito", true);
        trama.addProperty("vehiculo_odometro", odometro);
        JsonObject data = new JsonObject();
        data.add("data", trama);        
        if (trama.get(IDENTIFICADOR_CUPO) != null && !trama.get(IDENTIFICADOR_CUPO).isJsonNull()) {
            long identificadorCupo = trama.get(IDENTIFICADOR_CUPO).getAsLong();
            data.addProperty(IDENTIFICADOR_CUPO, identificadorCupo);
            data.addProperty("identificadorEstacion", Main.credencial.getEmpresas_id());
            data.addProperty("identificadorResponsable", idPromotor);
            ClientePropioDao clientePropioDao = new ClientePropioDao();
            clientePropioDao.guardarLiberacionClientePropio(data, url, NovusConstante.PUT);
        }

    }
}

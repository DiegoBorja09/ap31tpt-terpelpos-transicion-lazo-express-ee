/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.sap;

import com.controllers.EnvioHttp;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EntradaCombustibleDao.EntradaCombustibleDao;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Devitech
 */
public class EnviarConfirmacionRemisionHO {

    public void enviarConfirmacion(String remision) {
        Runnable enviarConfirmacion = () -> {
            EnvioHttp envioHttp = new EnvioHttp();
            String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_ACTULIZAR_ESTADO_REMISION);
            LocalTime horaActual = LocalTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaFormateada = horaActual.format(formato);
            JsonObject body = new JsonObject();
            body.addProperty("delivery", remision);
            body.addProperty("modification_date", LocalDate.now()+"");
            body.addProperty("modification_hour", horaFormateada);
            JsonObject respuesta = envioHttp.post(url, body);
            if (respuesta != null && NovusConstante.HAY_INTERNET) {
                NovusUtils.printLn("Actulizazicon del estado de la remision en HO actulizado con exito " + respuesta);
            } else {
                EntradaCombustibleDao combustibleDao = new EntradaCombustibleDao();
                combustibleDao.guardarEnvioDeConfirmacion(url, NovusConstante.POST, body.toString());
                NovusUtils.printLn(NovusConstante.HAY_INTERNET ? "error al enviar actulizacion de estado a HO " + respuesta : " Sin internet para enviar actualizaicón a HO");

            }
        };
        CompletableFuture.runAsync(enviarConfirmacion);
    }

}

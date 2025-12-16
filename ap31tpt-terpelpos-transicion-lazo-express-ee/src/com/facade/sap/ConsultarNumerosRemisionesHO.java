/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.sap;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;

/**
 *
 * @author Devitech
 */
public class ConsultarNumerosRemisionesHO {

    public String consultarRemisionHO(String remision) {
        String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_CONSULTAR_REMISION.concat("/".concat(remision)));
        ClientWSAsync clientWSAsync = new ClientWSAsync("Consultar remision", url, NovusConstante.GET, true);
        if (clientWSAsync.esperaRespuesta() != null) {
            System.out.println(clientWSAsync.getResponse().get("status").getAsInt());
            if (clientWSAsync.getResponse().get("status").getAsInt() == 200) {
                return "Espere un momento mientras se sincroniza la información";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
}

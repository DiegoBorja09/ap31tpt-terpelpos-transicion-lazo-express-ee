/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.beans.LoyalityConfig;
import com.firefuel.Main;
import com.google.gson.JsonObject;

/**
 *
 * @author USUARIO
 */
public class LoyaltyConfigMapper implements IMapper<String, LoyalityConfig> {

    @Override
    public LoyalityConfig mapTo(String input) {
        //SOLO NECESITO IDENTFICADO DE PUNTO DE VENTA, POR TIEMPO LO DEJO ASI POR AHORA, CONTINUA AGREGANDO CONFIG DE FIDELIZACION SEGUN NECESITES
        //¡OJO! ESTO ES UNA EMPANDA QUE SE DEBE CORREGIR, debe serializar todos los campos de FIDELIZACION no solo IDENTIFICACION_PUNTO_VENTA
        // HAY DOS IDENTIFICACION_PUNTO_VENTA uno dentro del campo nodo el otro  es el campo IDENTIFICACION_PUNTO_VENTA a mismo nivel que nodo (necesitas el del campo nodo)
        // NO PREGUNTES POR QUE ESTA ASI, NADIE SABE
        LoyalityConfig loyalityConfig = new LoyalityConfig();
        JsonObject json = Main.gson.fromJson(input, JsonObject.class);
        loyalityConfig.setIdentificacionPuntoVenta(json.get("node").getAsJsonObject().get("IDENTIFICACION_PUNTO_VENTA").getAsString());
        return loyalityConfig;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.facade;

import com.dao.EquipoDao;
import com.dao.SetupDao;
import com.google.gson.JsonArray;

/**
 *
 * @author Admin
 */
public class ConfigurationFacade {

    public static JsonArray fetchConfigurationParams(String stringParams) {
        SetupDao sdao = new SetupDao();
        return sdao.searchConfigurationParams(stringParams);

    }

    public static String fetchSalePointIdentificator() {
        EquipoDao dao = new EquipoDao();
        return dao.getIdentificacionPuntoVenta();
    }
}

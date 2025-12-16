/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.infraestructura.mapperJson;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.domain.entities.NotificationPayment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;

/**
 *
 * @author USUARIO
 */
public class GsonMapper implements IMapper<NotificationPayment, JsonObject>{
    private final Gson gson = new Gson();
    
    @Override
    public JsonObject mapTo(NotificationPayment input)  {
       JsonObject jsonObject = gson.toJsonTree(input).getAsJsonObject();
       return  jsonObject;
    }
    
    
    
}

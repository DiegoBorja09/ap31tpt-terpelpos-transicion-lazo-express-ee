/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.domain.entities.NotificationPayment;
import com.google.gson.JsonObject;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author USUARIO
 */
public class ConvertNotificationPaymentToJsonObject implements IUseCase<NotificationPayment, JsonObject>{

     private IMapper<NotificationPayment,JsonObject> mapperToJson;

    public ConvertNotificationPaymentToJsonObject(IMapper<NotificationPayment, JsonObject> mapperToJson) {
        this.mapperToJson = mapperToJson;
    }

    @Override
    public JsonObject execute(NotificationPayment input) {
        //JsonObject OBVIAMENTE NO DEBERIA ESTAR AQUI PERO DADO EL ESTADO DE LA INTERFAZ NO ME QUEDA OPCION debe asumir que es una entiada de dominio
        JsonObject jsonObject = mapperToJson.mapTo(input);
        return jsonObject;
    }
     
     
    
 

}

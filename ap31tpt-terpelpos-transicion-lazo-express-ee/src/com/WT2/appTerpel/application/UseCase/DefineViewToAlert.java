/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.firefuel.InfoViewController;
import com.google.gson.JsonObject;

/**
 *
 * @author USUARIO
 */
public class DefineViewToAlert {

    public void execute(JsonObject jsonObject) {
        
        if (InfoViewController.NotificadorInfoView != null) {
            InfoViewController.NotificadorInfoView.send(jsonObject);
        } else if (InfoViewController.NotificadorVentaView != null) {
            InfoViewController.NotificadorVentaView.send(jsonObject);
        } else if (InfoViewController.NotificadorVentasHistorial != null) {
            InfoViewController.NotificadorVentasHistorial.send(jsonObject);
        }
    }

}

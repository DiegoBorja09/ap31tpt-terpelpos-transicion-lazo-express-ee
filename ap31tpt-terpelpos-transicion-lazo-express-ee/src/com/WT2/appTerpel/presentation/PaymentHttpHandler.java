/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation;

import com.WT2.appTerpel.presentation.controllers.PaymentNotificationController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

/**
 *
 * @author USUARIO
 */
public class PaymentHttpHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        PaymentNotificationController paymentNotificationController = new PaymentNotificationController();
        paymentNotificationController.execute(exchange);
        
    }
    
}

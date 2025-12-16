/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation.Mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.domain.entities.NotificationPayment;
import com.WT2.appTerpel.presentation.dto.NotificationPaymentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class NotificationPaymentMapper implements IMapper<HttpExchange, NotificationPayment> {

    private ObjectMapper objectMapper;

    public NotificationPaymentMapper() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public NotificationPayment mapTo(HttpExchange input) {

        try {
            NotificationPaymentDto notificationPaymentDto = objectMapper.readValue(input.getRequestBody(), NotificationPaymentDto.class);
            notificationPaymentDto.setTipo(4);
            NotificationPayment notificationPayment = new NotificationPayment();
            notificationPayment.setCodigo(notificationPaymentDto.getCodigo());
            notificationPayment.setEstado(notificationPaymentDto.getEstado());
            notificationPayment.setMensaje(notificationPaymentDto.getMensaje());
            notificationPayment.setTitulo(notificationPaymentDto.getTitulo());
            notificationPayment.setTipo(notificationPaymentDto.getTipo());
            String codigoAuth = notificationPaymentDto.getCodigoAutorizacion() != null ? notificationPaymentDto.getCodigoAutorizacion() :"";
            notificationPayment.setCodigoAutorizacion(codigoAuth);

            return notificationPayment;
        } catch (IOException ex) {
            Logger.getLogger(NotificationPaymentMapper.class.getName()).log(Level.SEVERE, null, ex);
            return new NotificationPayment();
        }
    }

}

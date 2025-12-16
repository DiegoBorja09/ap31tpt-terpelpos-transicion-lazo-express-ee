/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.repository;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.entity.HttpRespuesta;
import com.WT2.commons.domain.entity.Request;

/**
 *
 * @author USUARIO
 */
public class HttpsClient implements IHttpClientRepository {

    @Override
    public HttpRespuesta send(String url, Request pagoRequest, Class response) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void initConfig() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

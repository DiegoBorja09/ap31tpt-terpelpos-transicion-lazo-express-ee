/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.WT2.commons.domain.adapters;

import com.WT2.commons.domain.entity.HttpRespuesta;
import com.WT2.commons.domain.entity.Request;

/**
 *
 * @author USUARIO
 */
public interface IHttpClientRepository<T> {

    public HttpRespuesta<T> send(String url, Request pagoRequest, Class<T> response) throws Exception;
    public void initConfig();

}

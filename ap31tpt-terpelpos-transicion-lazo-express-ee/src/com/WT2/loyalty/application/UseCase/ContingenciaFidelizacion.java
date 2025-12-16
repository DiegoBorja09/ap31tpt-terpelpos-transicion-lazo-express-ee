/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.application.UseCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author USUARIO
 */
public class ContingenciaFidelizacion implements IUseCase<Object, Object>{
    
    //SI EL SERVICIO ESTA ARRIBA
    private IHttpClientRepository httpClient;
    private IRepository GuardarFidelizacion;

    public ContingenciaFidelizacion(IHttpClientRepository httpClient, IRepository GuardarFidelizacion) {
        this.httpClient = httpClient;
        this.GuardarFidelizacion = GuardarFidelizacion;
    }
    
    @Override
    public Object execute(Object input) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
   
    
}

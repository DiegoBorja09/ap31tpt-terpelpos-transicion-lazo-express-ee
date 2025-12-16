/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.application.useCases;

import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.WT2.ImpresonesFE.domain.entities.RespuestaFeImprimir;
import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.WT2.commons.infraestructure.repository.HttpClientRepository;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class EnviarFeImprimir implements IUseCase<ParametrosPeticionFePrinter, RespuestaFeImprimir> {

    private IHttpClientRepository<RespuestaFeImprimir> httpClient;

    public EnviarFeImprimir(IHttpClientRepository HttpClientRepository) {
        this.httpClient = HttpClientRepository;
    }

    @Override
    public RespuestaFeImprimir execute(ParametrosPeticionFePrinter input) {
        RespuestaFeImprimir respuestaFePrinter = new RespuestaFeImprimir();
        respuestaFePrinter.setMensaje("NO llego respuesta");
        respuestaFePrinter.setFechaProceso(new Date() + "");
        try {
            Request<PeticionFeImprimir> request = new Request<>();
            request.setBody(input.getPeticionFeImprimir());
            request.setMethod(HttpMethod.POST);
            request.setContent(HttpContentType.JSON);

            respuestaFePrinter = httpClient.send(input.getUrl(), request, RespuestaFeImprimir.class).getResponse();
            
            input.setProcesada(true);

        } catch (Exception ex) {
            Logger.getLogger(EnviarFeImprimir.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return respuestaFePrinter;
    }

}

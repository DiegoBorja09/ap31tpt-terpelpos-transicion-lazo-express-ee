/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.application.useCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.WT2.goPass.domain.entity.request.ConsultarPlacaStatusPumRequestBody;
import com.WT2.goPass.domain.entity.response.ConsultarPlacasResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class ConsultarPlacaStatusPum implements IUseCase<ConsultarPlacaStatusPumRequestBody, ConsultarPlacasResponse> {

    IHttpClientRepository<ConsultarPlacasResponse> httpClient;

    public ConsultarPlacaStatusPum(IHttpClientRepository<ConsultarPlacasResponse> httpClient) {

        this.httpClient = httpClient;
    }

    @Override
    public ConsultarPlacasResponse execute(ConsultarPlacaStatusPumRequestBody input) {

        ConsultarPlacasResponse consultarPlacasResponse = new ConsultarPlacasResponse();

        Request<ConsultarPlacaStatusPumRequestBody> request = new Request<>();

        request.setBody(input);
        request.setContent(HttpContentType.JSON);
        request.setMethod(HttpMethod.POST);

        try {
            consultarPlacasResponse = httpClient.send("http://localhost:7011/api/placaEnCurso", request, ConsultarPlacasResponse.class).getResponse();
            
            
            if (consultarPlacasResponse == null) {
                throw new Exception();
            }
            
            System.out.println("PLACAS RECUPERADAS");
            int numPlaca = 1;
            for(PlacaGopass placaGopass : consultarPlacasResponse.getDatos()){
                System.out.println("PLACA "+numPlaca+": "+placaGopass.getPlaca());
                numPlaca++;
            }
            
        } catch (Exception ex) {
            consultarPlacasResponse = new ConsultarPlacasResponse();
            consultarPlacasResponse.setDatos(new ArrayList<>());
            consultarPlacasResponse.setCodigoEDS("");
            consultarPlacasResponse.setFechaProceso("");
            consultarPlacasResponse.setMensaje("El cliente  fallo al recibir respuesta (mensaje temporal)");
            Logger.getLogger(ConsultarPlacasModuloGoPass.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(ConsultarPlacaStatusPum.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return  consultarPlacasResponse;

    }

}

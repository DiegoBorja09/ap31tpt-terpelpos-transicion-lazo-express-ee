/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.application.UseCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.WT2.loyalty.domain.entities.request.AcumulationLoyaltyRequestBody;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;
import com.WT2.loyalty.domain.valueObject.LoyaltiesEndpoints;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class AcumularPuntosRequestEndpoint implements IUseCase<AcumulationLoyaltyRequestBody, RespuestasAcumulacion> {

    private IHttpClientRepository<RespuestasAcumulacion> httpClient;

    public AcumularPuntosRequestEndpoint(IHttpClientRepository<RespuestasAcumulacion> httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public RespuestasAcumulacion execute(AcumulationLoyaltyRequestBody input) {
        Request<AcumulationLoyaltyRequestBody> request = new Request<>();
        request.setBody(input);
        request.setMethod(HttpMethod.POST);
        request.setContent(HttpContentType.JSON);
        RespuestasAcumulacion respuestasAcumulacion = new RespuestasAcumulacion();
        try {
            respuestasAcumulacion = httpClient.send(LoyaltiesEndpoints.ACUMULARPUNTOS, request, RespuestasAcumulacion.class).getResponse();
            if (respuestasAcumulacion == null) {

                throw new Exception();

            }
        } catch (Exception ex) {

            Logger.getLogger(AcumularPuntosRequestEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            respuestasAcumulacion = new RespuestasAcumulacion();
            respuestasAcumulacion.setError("No hay conexión con el Motor de Fidelización");
            respuestasAcumulacion.setMensajeRespuesta("No hay conexión con el Motor de Fidelización");
            respuestasAcumulacion.setStatus(400);

        }

        return respuestasAcumulacion;

    }
}

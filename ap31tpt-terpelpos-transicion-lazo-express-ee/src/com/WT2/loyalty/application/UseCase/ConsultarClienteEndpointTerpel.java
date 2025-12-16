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
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.valueObject.LoyaltiesEndpoints;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class ConsultarClienteEndpointTerpel implements IUseCase<ConsultClientRequestBody, FoundClient> {

    IHttpClientRepository<FoundClient> httpClient;

    public ConsultarClienteEndpointTerpel(IHttpClientRepository<FoundClient> httpClient) {
        this.httpClient = httpClient;

    }

    @Override
    public FoundClient execute(ConsultClientRequestBody consultClientRequest) {

        FoundClient foundClient = null;

        try {
            Request<ConsultClientRequestBody> request = new Request<>();
            request.setMethod(HttpMethod.GET);
            request.setContent(HttpContentType.JSON);
            request.setBody(consultClientRequest);
            foundClient = httpClient.send(LoyaltiesEndpoints.CONSULTARCLIENTE, request, FoundClient.class).getResponse();
            foundClient.getDatosCliente().getCustomer().setNumeroIdentificacion(request.getBody().getCustomer().getNumeroIdentificacion());
            if (foundClient == null) {

                throw new Exception();

            }
            
        } catch (Exception ex) {
            Logger.getLogger(ConsultarClienteEndpointTerpel.class.getName()).log(Level.SEVERE, null, ex);
            foundClient = new FoundClient();
            foundClient.setAdicional("No hay conexión con el Motor de Fidelización");
            foundClient.setMensaje("No hay conexión con el Motor de Fidelización");
            foundClient.setNombreCliente("CLIENTE");
            foundClient.setStatus(600);
            foundClient.setExisteClient(false);
            foundClient.setDatosCliente(consultClientRequest);

        }

        return foundClient;

    }

}

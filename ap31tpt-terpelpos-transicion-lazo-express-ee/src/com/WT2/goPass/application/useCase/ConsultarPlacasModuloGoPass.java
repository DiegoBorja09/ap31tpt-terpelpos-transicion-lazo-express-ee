package com.WT2.goPass.application.useCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.WT2.goPass.domain.entity.response.ConsultarPlacasResponse;
import com.controllers.NovusConstante;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author USUARIO
 */
public class ConsultarPlacasModuloGoPass implements IUseCase<Integer, ConsultarPlacasResponse> {

    IHttpClientRepository<ConsultarPlacasResponse> httpClient;

    public ConsultarPlacasModuloGoPass(IHttpClientRepository<ConsultarPlacasResponse> httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ConsultarPlacasResponse execute(Integer idVenta) {

        ConsultarPlacasResponse consultarPlacasResponse = new ConsultarPlacasResponse();
        String url = NovusConstante.SECURE_CENTRAL_POINT_BUSCAR_PLACAS_GOPASS + idVenta;
        Request<Void> request = new Request<>();
        request.setMethod(HttpMethod.GET);
        request.setContent(HttpContentType.JSON);

        try {
            consultarPlacasResponse = httpClient.send(url, request, ConsultarPlacasResponse.class).getResponse();
            if (consultarPlacasResponse == null) {
                throw new Exception();
            }

        } catch (Exception ex) {

            consultarPlacasResponse = new ConsultarPlacasResponse();
            consultarPlacasResponse.setDatos(new ArrayList<>());
            consultarPlacasResponse.setCodigoEDS("");
            consultarPlacasResponse.setFechaProceso("");
            consultarPlacasResponse.setMensaje("El cliente  fallo al recibir respuesta (mensaje temporal)");
            Logger.getLogger(ConsultarPlacasModuloGoPass.class.getName()).log(Level.SEVERE, null, ex);

        }

        return consultarPlacasResponse;

    }

}

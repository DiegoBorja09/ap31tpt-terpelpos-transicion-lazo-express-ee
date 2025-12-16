package com.WT2.loyalty.application.UseCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.HttpRespuesta;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.WT2.loyalty.domain.entities.request.RedencionBonoRequest;
import com.WT2.loyalty.domain.entities.response.RespuestaRedencionBono;
import com.WT2.loyalty.domain.valueObject.LoyaltiesEndpoints;
import com.controllers.NovusConstante;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedimirBono implements IUseCase<RedencionBonoRequest, RespuestaRedencionBono> {

    private IHttpClientRepository<RespuestaRedencionBono> httpClient;
    SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);

    public RedimirBono(IHttpClientRepository<RespuestaRedencionBono> httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public RespuestaRedencionBono execute(RedencionBonoRequest input) {
        Request<RedencionBonoRequest> request = new Request<>();
        request.setBody(input);
        request.setMethod(HttpMethod.POST);
        request.setContent(HttpContentType.JSON);
        request.addHeader("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "*/*");
        request.addHeader("dispositivo", "proyectos");
        request.addHeader("fecha", sdfISO.format(new Date()) + "-05");
        request.addHeader("aplicacion", "lazoexpress");
        RespuestaRedencionBono respuestaRedencionBono = null;
        try {
            HttpRespuesta<RespuestaRedencionBono> httpRespuesta = httpClient.send(LoyaltiesEndpoints.REDENCIONBONO, request, RespuestaRedencionBono.class);
            respuestaRedencionBono = httpRespuesta.getResponse();
            
            if (respuestaRedencionBono == null) {
                throw new Exception();
            }else{
                respuestaRedencionBono.setStatus(httpRespuesta.getCodigo());
            }
        } catch (Exception ex) {

            Logger.getLogger(AcumularPuntosRequestEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            respuestaRedencionBono = new RespuestaRedencionBono();
            respuestaRedencionBono.setError("No hay conexión con el Motor de Fidelización");
            respuestaRedencionBono.setMensajeRespuesta("No hay conexión con el Motor de Fidelización");
            respuestaRedencionBono.setStatus(500);

        }

        return respuestaRedencionBono;

    }
}

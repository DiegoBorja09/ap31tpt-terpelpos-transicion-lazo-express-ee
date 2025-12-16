package com.firefuel.controlImpresion.useCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.controllers.NovusConstante;
import com.firefuel.controlImpresion.dto.PeticionImpresion;
import com.firefuel.controlImpresion.dto.RespuestaImpresion;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActualizarMovimiento {

    @SuppressWarnings("unchecked")
    public void actulizarDatosVenta(PeticionImpresion peticion, IHttpClientRepository httpClient) {
        try {
            String url = NovusConstante.SECURE_CENTRAL_POINT_ACTUALIZAR_ATRIBUTOS_VENTA;
            Request<PeticionImpresion> request = new Request<>();
            request.setBody(peticion);
            request.setMethod(HttpMethod.PUT);
            request.setContent(HttpContentType.JSON);

            RespuestaImpresion respuesta = new RespuestaImpresion();
            respuesta.setMensaje("NO llego respuesta");
            respuesta.setFechaProceso(new Date() + "");

            httpClient.send(url, request, RespuestaImpresion.class);
        } catch (Exception ex) {
            Logger.getLogger(EnviarImpresion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

package com.application.useCases.gopass;

import com.application.ports.in.gopass.ConsultarEstadoPagoGoPassPort;
import com.application.ports.out.gopass.GoPassHttpPort;
import com.application.ports.out.gopass.GoPassConfiguracionPort;
import com.bean.TransaccionGopass;
import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultarEstadoPagoGoPassUseCase implements ConsultarEstadoPagoGoPassPort {

    private static final Logger LOGGER = Logger.getLogger(ConsultarEstadoPagoGoPassUseCase.class.getName());

    private final GoPassHttpPort goPassHttpPort;
    private final GoPassConfiguracionPort configuracionPort;

    public ConsultarEstadoPagoGoPassUseCase(GoPassHttpPort goPassHttpPort,
                                           GoPassConfiguracionPort configuracionPort) {
        this.goPassHttpPort = goPassHttpPort;
        this.configuracionPort = configuracionPort;
    }

    @Override
    public ConsultarEstadoResult execute(TransaccionGopass transaccion) {
        LOGGER.info("[HEXAGONAL] Consultando estado de pago GoPass para transacción: " + 
                    transaccion.getIdentificadortransacciongopass());

        if (transaccion == null) {
            return ConsultarEstadoResult.error("La transacción no puede ser nula");
        }

        if (transaccion.getIdentificadortransacciongopass() == 0) {
            return ConsultarEstadoResult.error("ID de transacción GoPass inválido");
        }

        try {
            GoPassConfiguracionPort.ConfiguracionGoPass config = configuracionPort.obtenerConfiguracion();
            
            String refCobro = String.valueOf(transaccion.getIdentificadorventaterpel());
            String establecimiento = config.getCodigoEstablecimiento();
            long idTransaccion = transaccion.getIdentificadortransacciongopass();

            LOGGER.info("Consultando estado - RefCobro: " + refCobro + ", ID: " + idTransaccion);

            JsonObject response = goPassHttpPort.consultarEstadoPago(refCobro, establecimiento, idTransaccion);
            
            String mensaje = extraerMensaje(response);
            
            LOGGER.info("Estado consultado exitosamente: " + mensaje);
            return ConsultarEstadoResult.exitoso(mensaje);

        } catch (GoPassHttpPort.GoPassHttpException e) {
            LOGGER.log(Level.SEVERE, "Error de comunicación con GoPass", e);
            return ConsultarEstadoResult.error(e.getMessage());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado consultando estado de pago", e);
            return ConsultarEstadoResult.error("Error inesperado al consultar el estado");
        }
    }

    private String extraerMensaje(JsonObject response) {
        if (response != null && response.has("mensaje") && !response.get("mensaje").isJsonNull()) {
            return response.get("mensaje").getAsString();
        }
        return "Estado consultado exitosamente";
    }
}


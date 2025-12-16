package com.application.useCases.gopass;

import com.application.ports.in.gopass.ConsultarPlacasGoPassPort;
import com.application.ports.out.gopass.GoPassHttpPort;
import com.application.ports.out.gopass.GoPassConfiguracionPort;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultarPlacasGoPassUseCase implements ConsultarPlacasGoPassPort {

    private static final Logger LOGGER = Logger.getLogger(ConsultarPlacasGoPassUseCase.class.getName());
    
    private final GoPassHttpPort goPassHttpPort;
    private final GoPassConfiguracionPort configuracionPort;

    public ConsultarPlacasGoPassUseCase(GoPassHttpPort goPassHttpPort, 
                                        GoPassConfiguracionPort configuracionPort) {
        this.goPassHttpPort = goPassHttpPort;
        this.configuracionPort = configuracionPort;
    }

    @Override
    public ConsultarPlacasResult execute(Long ventaId) {
        LOGGER.info("[HEXAGONAL] Ejecutando caso de uso: Consultar Placas para venta ID: " + ventaId);

        if (ventaId == null) {
            return ConsultarPlacasResult.error("ID de venta no puede ser nulo");
        }

        try {
            GoPassConfiguracionPort.ConfiguracionGoPass config = configuracionPort.obtenerConfiguracion();
            
            int timeout = calcularTimeoutPlacas(config);
            LOGGER.info("Timeout calculado: " + timeout + "ms");

            ArrayList<PlacaGopass> placas = goPassHttpPort.consultarPlacas(ventaId, timeout);
            
            if (placas.isEmpty()) {
                LOGGER.warning("No se encontraron placas para la venta: " + ventaId);
                return ConsultarPlacasResult.error("No se encontraron placas disponibles");
            }
            
            LOGGER.info("Placas consultadas exitosamente: " + placas.size());
            return ConsultarPlacasResult.exitoso(placas);

        } catch (GoPassHttpPort.GoPassHttpException e) {
            LOGGER.log(Level.SEVERE, "Error de comunicación con GoPass", e);
            return ConsultarPlacasResult.error(e.getMessage());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado consultando placas", e);
            return ConsultarPlacasResult.error("Error inesperado al consultar placas");
        }
    }

    private int calcularTimeoutPlacas(GoPassConfiguracionPort.ConfiguracionGoPass config) {
        int timeout = ((config.getCantidadReintentosToken() * 
            (config.getTiempoMuertoToken() + config.getTiempoReintentosPago())) + 
            (config.getCantidadReintentosConsultaPlaca() * 
            (config.getTiempoMuertoConsultaPlaca() + config.getTiempoReintentosConsultaPlaca())));
        
        return (timeout + 5) * 1000;
    }
}


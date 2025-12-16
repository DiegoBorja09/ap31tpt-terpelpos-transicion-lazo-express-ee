package com.application.useCases.gopass;

import com.application.ports.in.gopass.ProcesarPagoGoPassPort;
import com.application.ports.out.gopass.GoPassPagoPort;
import com.application.ports.out.gopass.GoPassConfiguracionPort;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.bean.VentaGo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesarPagoGoPassUseCase implements ProcesarPagoGoPassPort {

    private static final Logger LOGGER = Logger.getLogger(ProcesarPagoGoPassUseCase.class.getName());
    
    private final GoPassPagoPort pagoPort;
    private final GoPassConfiguracionPort configuracionPort;

    public ProcesarPagoGoPassUseCase(GoPassPagoPort pagoPort,
                                     GoPassConfiguracionPort configuracionPort) {
        this.pagoPort = pagoPort;
        this.configuracionPort = configuracionPort;
    }

    @Override
    public ProcesarPagoResult execute(ProcesarPagoCommand command) {
        LOGGER.info("[HEXAGONAL] Procesando pago GoPass para venta ID: " + command.getVenta().getId());

        if (command.getVenta() == null) {
            return ProcesarPagoResult.error("La venta no puede ser nula");
        }

        if (command.getPlaca() == null) {
            return ProcesarPagoResult.error("La placa no puede ser nula");
        }

        try {
            GoPassConfiguracionPort.ConfiguracionGoPass config = configuracionPort.obtenerConfiguracion();
            long timeout = calcularTimeoutPago(config);
            
            LOGGER.info("Timeout calculado para pago: " + timeout + "ms");

            GoPassPagoPort.ResultadoPago resultado = pagoPort.procesarPago(
                command.getVenta(),
                command.getPlaca(),
                timeout
            );

            if (resultado.isError()) {
                LOGGER.warning("Pago rechazado o con error: " + resultado.getMensaje());
                return ProcesarPagoResult.error(resultado.getMensaje());
            }

            LOGGER.info("Pago procesado exitosamente");
            return ProcesarPagoResult.exitoso(
                resultado.getMensaje(),
                resultado.getEstadoPago(),
                command.getVenta().getId()
            );

        } catch (GoPassPagoPort.GoPassPagoException e) {
            LOGGER.log(Level.SEVERE, "Error de comunicación procesando pago", e);
            return ProcesarPagoResult.error(e.getMessage());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado procesando pago", e);
            return ProcesarPagoResult.error("Error inesperado al procesar el pago");
        }
    }

    private long calcularTimeoutPago(GoPassConfiguracionPort.ConfiguracionGoPass config) {
        int timeout = ((config.getCantidadReintentosToken() * 
            (config.getTiempoMuertoToken() + config.getTiempoReintentosPago())) + 
            (config.getCantidadReintentosPago() * 
            (config.getTiempoReintentosPago() + config.getTiempoReintentosConsultaPlaca())));
        
        return (timeout + 5) * 1000L;
    }
}


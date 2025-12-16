package com.infrastructure.adapters.out.gopass;

import com.application.ports.out.gopass.GoPassPagoPort;
import com.application.services.GoPassPaymentService;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.bean.GopassResponse;
import com.bean.VentaGo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ✅ ARQUITECTURA HEXAGONAL - Adaptador de Salida
 * Adaptador para procesar pagos con GoPass
 * Implementa el puerto GoPassPagoPort
 * Usa el servicio GoPassPaymentService existente
 */
public class GoPassPagoAdapter implements GoPassPagoPort {

    private static final Logger LOGGER = Logger.getLogger(GoPassPagoAdapter.class.getName());
    
    private final GoPassPaymentService paymentService;

    /**
     * Constructor con parámetros de configuración
     */
    public GoPassPagoAdapter(GopassParameters parametrosGopass) {
        this.paymentService = new GoPassPaymentService(parametrosGopass);
    }

    @Override
    public ResultadoPago procesarPago(VentaGo venta, PlacaGopass placa, long timeoutMs) throws GoPassPagoException {
        LOGGER.info("✅ [ADAPTER] Procesando pago GoPass - Venta: " + venta.getId());

        try {
            // Usar el servicio existente
            GopassResponse response = paymentService.procesarPagoGoPass(venta, placa);
            
            if (response == null) {
                throw new GoPassPagoException("Respuesta nula del servicio de pago");
            }
            
            LOGGER.info("✅ Pago procesado - Estado: " + response.getEstadoPago() + 
                       ", Error: " + response.isError());
            
            return new ResultadoPago(
                response.getEstadoPago(),
                response.getMensaje(),
                response.isError()
            );
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error procesando pago", e);
            throw new GoPassPagoException("Error al procesar el pago: " + e.getMessage(), e);
        }
    }
    
    /**
     * Configura el callback para actualizar la UI
     * Esto es necesario por compatibilidad con el código existente
     */
    public void setUICallback(GoPassPaymentService.UICallback callback) {
        this.paymentService.setUICallback(callback);
    }
}


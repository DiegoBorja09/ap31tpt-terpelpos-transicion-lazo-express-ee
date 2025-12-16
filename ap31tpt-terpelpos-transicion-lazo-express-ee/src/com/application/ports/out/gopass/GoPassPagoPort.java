package com.application.ports.out.gopass;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.bean.VentaGo;

public interface GoPassPagoPort {
    
    /**
     * Procesa un pago con GoPass
     * @param venta Venta a pagar
     * @param placa Placa del vehículo
     * @param timeoutMs Timeout en milisegundos
     * @return Resultado del pago
     * @throws GoPassPagoException si hay error procesando el pago
     */
    ResultadoPago procesarPago(VentaGo venta, PlacaGopass placa, long timeoutMs) throws GoPassPagoException;
    
    /**
     * Resultado del procesamiento de pago
     */
    class ResultadoPago {
        private final String estadoPago;
        private final String mensaje;
        private final boolean error;
        
        public ResultadoPago(String estadoPago, String mensaje, boolean error) {
            this.estadoPago = estadoPago;
            this.mensaje = mensaje;
            this.error = error;
        }
        
        public String getEstadoPago() {
            return estadoPago;
        }
        
        public String getMensaje() {
            return mensaje;
        }
        
        public boolean isError() {
            return error;
        }
    }
    
    /**
     * Excepción de procesamiento de pago
     */
    class GoPassPagoException extends Exception {
        public GoPassPagoException(String message) {
            super(message);
        }
        
        public GoPassPagoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


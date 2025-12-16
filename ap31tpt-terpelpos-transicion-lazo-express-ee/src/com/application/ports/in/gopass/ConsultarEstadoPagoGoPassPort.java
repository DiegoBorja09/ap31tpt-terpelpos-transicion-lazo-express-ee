package com.application.ports.in.gopass;

import com.bean.TransaccionGopass;

/**
 * Puerto de entrada: Consultar el estado de un pago GoPass
 */
public interface ConsultarEstadoPagoGoPassPort {
    
    ConsultarEstadoResult execute(TransaccionGopass transaccion);
    
    /**
     * Resultado del caso de uso
     */
    class ConsultarEstadoResult {
        private final boolean exitoso;
        private final String mensaje;

        private ConsultarEstadoResult(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public static ConsultarEstadoResult exitoso(String mensaje) {
            return new ConsultarEstadoResult(true, mensaje);
        }

        public static ConsultarEstadoResult error(String mensaje) {
            return new ConsultarEstadoResult(false, mensaje);
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}


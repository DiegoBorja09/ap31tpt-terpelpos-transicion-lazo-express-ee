package com.application.ports.in.gopass;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.bean.VentaGo;

/**
 * Puerto de entrada: Procesar un pago con GoPass
 */
public interface ProcesarPagoGoPassPort {
    
    ProcesarPagoResult execute(ProcesarPagoCommand command);
    
    /**
     * Command (DTO de entrada)
     */
    class ProcesarPagoCommand {
        private final VentaGo venta;
        private final PlacaGopass placa;

        public ProcesarPagoCommand(VentaGo venta, PlacaGopass placa) {
            this.venta = venta;
            this.placa = placa;
        }

        public VentaGo getVenta() {
            return venta;
        }

        public PlacaGopass getPlaca() {
            return placa;
        }
    }
    
    /**
     * Resultado del caso de uso
     */
    class ProcesarPagoResult {
        private final boolean exitoso;
        private final String mensaje;
        private final String estadoPago;
        private final long idVenta;

        private ProcesarPagoResult(boolean exitoso, String mensaje, String estadoPago, long idVenta) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.estadoPago = estadoPago;
            this.idVenta = idVenta;
        }

        public static ProcesarPagoResult exitoso(String mensaje, String estadoPago, long idVenta) {
            return new ProcesarPagoResult(true, mensaje, estadoPago, idVenta);
        }

        public static ProcesarPagoResult error(String mensaje) {
            return new ProcesarPagoResult(false, mensaje, "ERROR", 0);
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getEstadoPago() {
            return estadoPago;
        }

        public long getIdVenta() {
            return idVenta;
        }
    }
}


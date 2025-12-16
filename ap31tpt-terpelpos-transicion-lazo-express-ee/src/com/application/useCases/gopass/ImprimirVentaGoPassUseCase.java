package com.application.useCases.gopass;

import com.application.ports.out.gopass.ImpresionPort;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImprimirVentaGoPassUseCase {
    
    private static final Logger logger = Logger.getLogger(ImprimirVentaGoPassUseCase.class.getName());
    
    private static final int TIMEOUT_IMPRESION_MS = 5000;
    
    private final ImpresionPort impresionPort;
    
    public ImprimirVentaGoPassUseCase(ImpresionPort impresionPort) {
        this.impresionPort = impresionPort;
    }
    
    public ImprimirVentaResult execute(long ventaId, String tipoDocumento) {
        logger.info("[HEXAGONAL] Ejecutando caso de uso: ImprimirVentaGoPass - Venta ID: " + ventaId);
        
        if (!validarParametros(ventaId, tipoDocumento)) {
            return ImprimirVentaResult.error("Parámetros inválidos para la impresión");
        }
        
        try {
            ImpresionPort.ResultadoImpresion resultado = impresionPort.imprimir(
                ventaId, 
                tipoDocumento, 
                TIMEOUT_IMPRESION_MS
            );
            
            if (resultado.isExitoso()) {
                logger.info("Impresión completada para venta: " + ventaId);
                return ImprimirVentaResult.exitoso("Venta impresa correctamente");
            } else {
                if (resultado.esServicioApagado()) {
                    logger.warning("Servicio de impresión apagado - Venta: " + ventaId);
                    return ImprimirVentaResult.advertencia(
                        "Servicio de impresión no disponible. La venta se procesó correctamente."
                    );
                } else {
                    logger.warning("Error en impresión - Venta: " + ventaId + " - " + resultado.getMensaje());
                    return ImprimirVentaResult.advertencia(resultado.getMensaje());
                }
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error crítico en impresión", e);
            return ImprimirVentaResult.error("Error crítico al imprimir: " + e.getMessage());
        }
    }
    
    private boolean validarParametros(long ventaId, String tipoDocumento) {
        if (ventaId <= 0) {
            logger.warning("ID de venta inválido: " + ventaId);
            return false;
        }
        
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            logger.warning("Tipo de documento inválido: " + tipoDocumento);
            return false;
        }
        
        return true;
    }
    
    public static class ImprimirVentaResult {
        private final boolean exito;
        private final String mensaje;
        private final long timestamp;
        private final boolean esAdvertencia;
        
        private ImprimirVentaResult(boolean exito, String mensaje, boolean esAdvertencia) {
            this.exito = exito;
            this.mensaje = mensaje;
            this.timestamp = System.currentTimeMillis();
            this.esAdvertencia = esAdvertencia;
        }
        
        public static ImprimirVentaResult exitoso(String mensaje) {
            return new ImprimirVentaResult(true, mensaje, false);
        }
        
        public static ImprimirVentaResult advertencia(String mensaje) {
            return new ImprimirVentaResult(false, mensaje, true);
        }
        
        /**
         * Error crítico
         */
        public static ImprimirVentaResult error(String mensaje) {
            return new ImprimirVentaResult(false, mensaje, false);
        }
        
        // Getters
        public boolean isExito() { return exito; }
        public String getMensaje() { return mensaje; }
        public long getTimestamp() { return timestamp; }
        public boolean isAdvertencia() { return esAdvertencia; }
        
        @Override
        public String toString() {
            return String.format("ImprimirVentaResult{exito=%s, mensaje='%s', advertencia=%s, timestamp=%d}", 
                                exito, mensaje, esAdvertencia, timestamp);
        }
    }
}

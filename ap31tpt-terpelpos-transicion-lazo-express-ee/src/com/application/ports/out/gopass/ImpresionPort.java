package com.application.ports.out.gopass;

public interface ImpresionPort {
    
    /**
     * Imprime una venta de forma asíncrona
     * @param ventaId ID de la venta
     * @param tipoDocumento Tipo de documento (factura, ticket, etc)
     * @param timeoutMs Timeout en milisegundos
     * @return Resultado de la impresión
     */
    ResultadoImpresion imprimir(long ventaId, String tipoDocumento, int timeoutMs);
    
    /**
     * Resultado de impresión
     */
    class ResultadoImpresion {
        private final boolean exitoso;
        private final String mensaje;
        private final TipoError tipoError;
        
        private ResultadoImpresion(boolean exitoso, String mensaje, TipoError tipoError) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.tipoError = tipoError;
        }
        
        public static ResultadoImpresion exitoso(String mensaje) {
            return new ResultadoImpresion(true, mensaje, null);
        }
        
        public static ResultadoImpresion error(String mensaje, TipoError tipoError) {
            return new ResultadoImpresion(false, mensaje, tipoError);
        }
        
        public boolean isExitoso() {
            return exitoso;
        }
        
        public String getMensaje() {
            return mensaje;
        }
        
        public TipoError getTipoError() {
            return tipoError;
        }
        
        public boolean esServicioApagado() {
            return tipoError == TipoError.SERVICIO_APAGADO;
        }
    }
    
    /**
     * Tipos de error de impresión
     */
    enum TipoError {
        SERVICIO_APAGADO,       // Connection refused
        TIMEOUT,                // No responde a tiempo
        ERROR_RED,              // Otro error de red
        ERROR_DESCONOCIDO       // Error inesperado
    }
}


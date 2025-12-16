package com.application.ports.out.gopass;

/**
 * Puerto de salida: Configuración de GoPass
 * Define cómo obtener la configuración del sistema
 */
public interface GoPassConfiguracionPort {
    
    /**
     * Obtiene los parámetros de configuración de GoPass
     * @return Parámetros de configuración
     */
    ConfiguracionGoPass obtenerConfiguracion();
    
    /**
     * Datos de configuración de GoPass
     */
    class ConfiguracionGoPass {
        private final int cantidadReintentosToken;
        private final int tiempoMuertoToken;
        private final int tiempoReintentosPago;
        private final int cantidadReintentosConsultaPlaca;
        private final int tiempoMuertoConsultaPlaca;
        private final int tiempoReintentosConsultaPlaca;
        private final int cantidadReintentosPago;
        private final String codigoEstablecimiento;
        
        public ConfiguracionGoPass(int cantidadReintentosToken, int tiempoMuertoToken,
                                  int tiempoReintentosPago, int cantidadReintentosConsultaPlaca,
                                  int tiempoMuertoConsultaPlaca, int tiempoReintentosConsultaPlaca,
                                  int cantidadReintentosPago, String codigoEstablecimiento) {
            this.cantidadReintentosToken = cantidadReintentosToken;
            this.tiempoMuertoToken = tiempoMuertoToken;
            this.tiempoReintentosPago = tiempoReintentosPago;
            this.cantidadReintentosConsultaPlaca = cantidadReintentosConsultaPlaca;
            this.tiempoMuertoConsultaPlaca = tiempoMuertoConsultaPlaca;
            this.tiempoReintentosConsultaPlaca = tiempoReintentosConsultaPlaca;
            this.cantidadReintentosPago = cantidadReintentosPago;
            this.codigoEstablecimiento = codigoEstablecimiento;
        }
        
        // Getters
        public int getCantidadReintentosToken() { return cantidadReintentosToken; }
        public int getTiempoMuertoToken() { return tiempoMuertoToken; }
        public int getTiempoReintentosPago() { return tiempoReintentosPago; }
        public int getCantidadReintentosConsultaPlaca() { return cantidadReintentosConsultaPlaca; }
        public int getTiempoMuertoConsultaPlaca() { return tiempoMuertoConsultaPlaca; }
        public int getTiempoReintentosConsultaPlaca() { return tiempoReintentosConsultaPlaca; }
        public int getCantidadReintentosPago() { return cantidadReintentosPago; }
        public String getCodigoEstablecimiento() { return codigoEstablecimiento; }
    }
}


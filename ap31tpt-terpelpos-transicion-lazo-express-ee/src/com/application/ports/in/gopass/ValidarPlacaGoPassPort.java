package com.application.ports.in.gopass;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;

/**
 * Puerto de entrada: Validar placa de vehículo
 */
public interface ValidarPlacaGoPassPort {
    
    ValidarPlacaResult execute(ValidarPlacaCommand command);
    
    /**
     * Command (DTO de entrada)
     */
    class ValidarPlacaCommand {
        private final PlacaGopass placa;
        private final String digitosIngresados;

        public ValidarPlacaCommand(PlacaGopass placa, String digitosIngresados) {
            this.placa = placa;
            this.digitosIngresados = digitosIngresados;
        }

        public PlacaGopass getPlaca() {
            return placa;
        }

        public String getDigitosIngresados() {
            return digitosIngresados;
        }
    }
    
    /**
     * Resultado del caso de uso
     */
    class ValidarPlacaResult {
        private final boolean valida;
        private final String mensaje;

        private ValidarPlacaResult(boolean valida, String mensaje) {
            this.valida = valida;
            this.mensaje = mensaje;
        }

        public static ValidarPlacaResult valida() {
            return new ValidarPlacaResult(true, "Placa validada correctamente");
        }

        public static ValidarPlacaResult invalida(String mensaje) {
            return new ValidarPlacaResult(false, mensaje);
        }

        public boolean isValida() {
            return valida;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}


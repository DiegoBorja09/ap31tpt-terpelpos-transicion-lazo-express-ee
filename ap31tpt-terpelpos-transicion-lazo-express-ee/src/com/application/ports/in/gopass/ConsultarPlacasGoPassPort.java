package com.application.ports.in.gopass;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import java.util.ArrayList;

/**
 * Puerto de entrada: Define QUÉ puede hacer la aplicación
 * Caso de uso: Consultar placas disponibles para una venta de GoPass
 */
public interface ConsultarPlacasGoPassPort {
    
    /**
     * Consulta las placas disponibles para una venta
     * @param ventaId ID de la venta
     * @return Resultado con las placas o mensaje de error
     */
    ConsultarPlacasResult execute(Long ventaId);
    
    /**
     * Resultado del caso de uso
     */
    class ConsultarPlacasResult {
        private final boolean exitoso;
        private final String mensaje;
        private final ArrayList<PlacaGopass> placas;

        private ConsultarPlacasResult(boolean exitoso, String mensaje, ArrayList<PlacaGopass> placas) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.placas = placas;
        }

        public static ConsultarPlacasResult exitoso(ArrayList<PlacaGopass> placas) {
            return new ConsultarPlacasResult(true, "Placas consultadas exitosamente", placas);
        }

        public static ConsultarPlacasResult error(String mensaje) {
            return new ConsultarPlacasResult(false, mensaje, new ArrayList<>());
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public ArrayList<PlacaGopass> getPlacas() {
            return placas;
        }
    }
}


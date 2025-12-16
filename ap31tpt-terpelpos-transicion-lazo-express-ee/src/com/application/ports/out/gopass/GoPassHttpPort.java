package com.application.ports.out.gopass;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.bean.GopassResponse;
import com.bean.VentaGo;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Puerto de salida: Comunicación HTTP con el servicio GoPass
 * Este puerto define QUÉ necesita la aplicación del mundo exterior (API GoPass)
 * Los adaptadores implementarán esta interfaz
 */
public interface GoPassHttpPort {
    
    /**
     * Consulta las placas disponibles desde la API de GoPass
     * @param ventaId ID de la venta
     * @param timeoutMs Timeout en milisegundos
     * @return Lista de placas o null en caso de error
     * @throws GoPassHttpException si hay error de comunicación
     */
    ArrayList<PlacaGopass> consultarPlacas(Long ventaId, int timeoutMs) throws GoPassHttpException;
    
    /**
     * Procesa un pago con GoPass
     * @param venta Venta a pagar
     * @param placa Placa del vehículo
     * @param timeoutMs Timeout en milisegundos
     * @return Respuesta del pago
     * @throws GoPassHttpException si hay error de comunicación
     */
    GopassResponse procesarPago(VentaGo venta, PlacaGopass placa, long timeoutMs) throws GoPassHttpException;
    
    /**
     * Consulta el estado de un pago
     * @param refCobro Referencia de cobro
     * @param establecimiento Código del establecimiento
     * @param idTransaccion ID de la transacción GoPass
     * @return Respuesta con el estado
     * @throws GoPassHttpException si hay error de comunicación
     */
    JsonObject consultarEstadoPago(String refCobro, String establecimiento, long idTransaccion) throws GoPassHttpException;
    
    /**
     * Excepción de comunicación con GoPass
     */
    class GoPassHttpException extends Exception {
        public GoPassHttpException(String message) {
            super(message);
        }
        
        public GoPassHttpException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


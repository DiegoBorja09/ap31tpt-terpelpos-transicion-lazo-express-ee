package com.controllers.sap;

import com.application.useCases.entradaCombustible.ObtenerHistorialRemisionesUseCase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 🚀 MIGRACIÓN: Controlador para operaciones de historial de remisiones
 * Utiliza casos de uso de Clean Architecture
 * 
 * ARQUITECTURA LIMPIA:
 * - Capa de presentación/controlador
 * - Integra casos de uso con interfaz externa
 * - Manejo de errores HTTP-friendly
 * - Formato de respuesta estándar JSON
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class HistorialRemisionController {
    
    private final ObtenerHistorialRemisionesUseCase obtenerHistorialUseCase;
    
    public HistorialRemisionController() {
        this.obtenerHistorialUseCase = new ObtenerHistorialRemisionesUseCase();
    }
    
    /**
     * 🚀 Obtiene historial de remisiones con límite específico
     * 
     * @param limite número máximo de registros (opcional, default 50)
     * @return JsonObject con respuesta estructurada
     */
    public JsonObject obtenerHistorial(Integer limite) {
        JsonObject respuesta = new JsonObject();
        
        try {
            // 🔍 Usar límite por defecto si no se especifica
            long limiteRegistros = limite != null ? limite : 50L;
            
            // ⚡ Ejecutar caso de uso
            JsonArray historial = obtenerHistorialUseCase.execute(limiteRegistros);
            
            // 🎯 Construir respuesta exitosa
            respuesta.addProperty("exito", true);
            respuesta.addProperty("mensaje", "Historial obtenido exitosamente");
            respuesta.addProperty("total_registros", historial.size());
            respuesta.addProperty("limite_aplicado", limiteRegistros);
            respuesta.add("datos", historial);
            
        } catch (IllegalArgumentException ex) {
            // 🚨 Error de validación
            respuesta.addProperty("exito", false);
            respuesta.addProperty("mensaje", "Error de validación: " + ex.getMessage());
            respuesta.add("datos", new JsonArray());
            
        } catch (Exception ex) {
            // 💥 Error general
            respuesta.addProperty("exito", false);
            respuesta.addProperty("mensaje", "Error interno del servidor: " + ex.getMessage());
            respuesta.add("datos", new JsonArray());
        }
        
        return respuesta;
    }
    
    /**
     * 🎯 Obtiene historial reciente (últimos 100 registros)
     * 
     * @return JsonObject con respuesta estructurada
     */
    public JsonObject obtenerHistorialReciente() {
        return obtenerHistorial(100);
    }
    
    /**
     * 🔍 Obtiene historial con formato simplificado para UI
     * 
     * @param limite número máximo de registros
     * @return JsonArray directo compatible con UI existente
     */
    public JsonArray obtenerHistorialSimple(Integer limite) {
        try {
            long limiteRegistros = limite != null ? limite : 50L;
            return obtenerHistorialUseCase.execute(limiteRegistros);
        } catch (Exception ex) {
            System.err.println("❌ Error en obtenerHistorialSimple: " + ex.getMessage());
            return new JsonArray(); // Retorna array vacío en caso de error
        }
    }
    
    /**
     * 🎯 Método de conveniencia estático para uso directo
     * 
     * @param limite número máximo de registros
     * @return JsonArray con historial de remisiones
     */
    public static JsonArray obtener(Integer limite) {
        HistorialRemisionController controller = new HistorialRemisionController();
        return controller.obtenerHistorialSimple(limite);
    }
} 
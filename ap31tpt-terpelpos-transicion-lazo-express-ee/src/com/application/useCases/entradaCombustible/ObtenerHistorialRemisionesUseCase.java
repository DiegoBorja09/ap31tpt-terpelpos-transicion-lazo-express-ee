package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.google.gson.JsonArray;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EntradaCombustibleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 🚀 MIGRACIÓN: Caso de uso para obtener historial de remisiones
 * Reemplaza el método infoHistorialRemisiones() de EntradaCombustibleDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<Long, JsonArray>
 * - Utiliza EntityManager y Repository pattern
 * - Maneja transacciones y excepciones
 * - Valida parámetros de entrada
 * - Retorna JsonArray compatible con el sistema existente
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ObtenerHistorialRemisionesUseCase implements BaseUseCasesWithParams<Long, JsonArray> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ObtenerHistorialRemisionesUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🚀 Ejecuta la consulta de historial de remisiones con límite específico
     * 
     * @param registros número máximo de registros a retornar (debe ser positivo)
     * @return JsonArray con historial de remisiones ordenado por fecha descendente
     * @throws IllegalArgumentException si registros es nulo o no positivo
     * @throws RuntimeException si ocurre error en base de datos
     */
    @Override
    public JsonArray execute(Long registros) {
        // 🔍 Validación de parámetros de entrada
        if (registros == null || registros <= 0) {
            throw new IllegalArgumentException("El número de registros debe ser positivo y no nulo");
        }
        
        // 🚨 Validación de límite máximo para performance
        if (registros > 10000) {
            throw new IllegalArgumentException("El número de registros no puede ser mayor a 10,000 por razones de performance");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        
        try {
            // 🏗️ Crear repositorio con EntityManager
            EntradaCombustibleRepository repository = new EntradaCombustibleRepository(em);
            
            // ⚡ Ejecutar consulta usando native query
            JsonArray historial = repository.obtenerHistorialRemisiones(registros);
            
            // 📊 Log de resultado para debugging
            System.out.println("🚀 ObtenerHistorialRemisionesUseCase - Historial obtenido: " + 
                             (historial != null ? historial.size() : 0) + " registros (límite: " + registros + ")");
            
            // 🎯 Retornar resultado (nunca null, pero puede estar vacío)
            return historial != null ? historial : new JsonArray();
            
        } catch (Exception ex) {
            // 🚨 Manejo de errores con contexto completo
            String errorMsg = "Error al obtener historial de remisiones con límite: " + registros;
            System.err.println("❌ " + errorMsg + " - " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(errorMsg, ex);
            
        } finally {
            // 🔒 Cerrar EntityManager siempre
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * 🎯 Método de conveniencia estático para uso directo
     * 
     * @param registros número máximo de registros a retornar
     * @return JsonArray con historial de remisiones
     */
    public static JsonArray obtenerHistorial(Long registros) {
        return new ObtenerHistorialRemisionesUseCase().execute(registros);
    }
    
    /**
     * 🔍 Método de conveniencia con límite por defecto (100 registros)
     * 
     * @return JsonArray con los últimos 100 registros de historial
     */
    public static JsonArray obtenerHistorialReciente() {
        return obtenerHistorial(100L);
    }
    
    /**
     * 🎯 Método de conveniencia para interfaz que usa int
     * 
     * @param registros número máximo de registros a retornar como int
     * @return JsonArray con historial de remisiones
     */
    public static JsonArray obtenerHistorial(int registros) {
        return obtenerHistorial((long) registros);
    }
} 
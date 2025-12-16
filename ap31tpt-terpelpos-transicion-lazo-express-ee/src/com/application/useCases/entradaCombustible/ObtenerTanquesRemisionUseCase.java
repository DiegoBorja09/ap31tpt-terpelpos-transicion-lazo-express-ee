package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.bean.BodegaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EntradaCombustibleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 🚀 MIGRACIÓN: Caso de uso para obtener tanques de remisión
 * Reemplaza el método getTanquesRemision() de EntradaCombustibleDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<String, Map<String, ArrayList<BodegaBean>>>
 * - Utiliza EntityManager y Repository pattern
 * - Maneja transacciones y excepciones
 * - Valida parámetros de entrada
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ObtenerTanquesRemisionUseCase implements BaseUseCasesWithParams<String, Map<String, ArrayList<BodegaBean>>> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ObtenerTanquesRemisionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🚀 Ejecuta la consulta de tanques de remisión para un delivery específico
     * 
     * @param delivery número de entrega/delivery de la remisión (no nulo, no vacío)
     * @return Map con tanques agrupados por producto (P-{productoId})
     * @throws IllegalArgumentException si delivery es nulo o vacío
     * @throws RuntimeException si ocurre error en base de datos
     */
    @Override
    public Map<String, ArrayList<BodegaBean>> execute(String delivery) {
        // 🔍 Validación de parámetros de entrada
        if (delivery == null || delivery.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de delivery no puede ser nulo o vacío");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        
        try {
            // 🏗️ Crear repositorio con EntityManager
            EntradaCombustibleRepository repository = new EntradaCombustibleRepository(em);
            
            // ⚡ Ejecutar consulta usando native query
            Map<String, ArrayList<BodegaBean>> tanques = repository.obtenerTanquesRemision(delivery.trim());
            
            // 📊 Log de resultado para debugging
            System.out.println("🚀 ObtenerTanquesRemisionUseCase - Tanques obtenidos para delivery " + delivery + ": " + 
                             (tanques != null ? tanques.size() : 0) + " grupos de productos");
            
            // 🎯 Retornar resultado (nunca null, pero puede estar vacío)
            return tanques != null ? tanques : new HashMap<>();
            
        } catch (Exception ex) {
            // 🚨 Manejo de errores con contexto completo
            String errorMsg = "Error al obtener tanques de remisión para delivery: " + delivery;
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
     * @param delivery número de entrega/delivery de la remisión
     * @return Map con tanques agrupados por producto
     */
    public static Map<String, ArrayList<BodegaBean>> obtenerTanques(String delivery) {
        return new ObtenerTanquesRemisionUseCase().execute(delivery);
    }
} 
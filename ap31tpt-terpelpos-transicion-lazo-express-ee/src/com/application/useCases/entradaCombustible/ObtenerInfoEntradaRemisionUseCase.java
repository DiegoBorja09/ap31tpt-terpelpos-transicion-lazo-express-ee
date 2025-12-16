package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.bean.entradaCombustible.EntradaCombustibleBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EntradaCombustibleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 🚀 MIGRACIÓN: Caso de uso para obtener información de entrada de remisión
 * Reemplaza el método infoEntradaRemision() de EntradaCombustibleDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<String, EntradaCombustibleBean>
 * - Utiliza EntityManager y Repository pattern
 * - Maneja transacciones y excepciones
 * - Valida parámetros de entrada
 * - Retorna EntradaCombustibleBean compatible con el sistema existente
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ObtenerInfoEntradaRemisionUseCase implements BaseUseCasesWithParams<String, EntradaCombustibleBean> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ObtenerInfoEntradaRemisionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🚀 Ejecuta la consulta de información de entrada de remisión para un delivery específico
     * 
     * @param delivery número de entrega/delivery de la remisión (no nulo, no vacío)
     * @return EntradaCombustibleBean con información de la remisión o null si no existe
     * @throws IllegalArgumentException si delivery es nulo o vacío
     * @throws RuntimeException si ocurre error en base de datos
     */
    @Override
    public EntradaCombustibleBean execute(String delivery) {
        // 🔍 Validación de parámetros de entrada
        if (delivery == null || delivery.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de delivery no puede ser nulo o vacío");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        
        try {
            // 🏗️ Crear repositorio con EntityManager
            EntradaCombustibleRepository repository = new EntradaCombustibleRepository(em);
            
            // 🔍 DEBUG: Verificar existencia antes de intentar obtener
            System.out.println("🔍 DEBUG: Verificando existencia de remisión...");


            
            // ⚡ Ejecutar consulta usando native query
            EntradaCombustibleBean remision = repository.obtenerInfoEntradaRemision(delivery.trim());
            
            // 📊 Log de resultado para debugging
            if (remision != null) {
                System.out.println("🚀 ObtenerInfoEntradaRemisionUseCase - Remisión encontrada para delivery " + delivery + 
                                 " - ID: " + remision.getIdRemision());
                System.out.println("🔍 ObtenerInfoEntradaRemisionUseCase - Productos SAP en bean: " + remision.getProductoSAP());
                System.out.println("🔍 ObtenerInfoEntradaRemisionUseCase - Bean completo: " + remision);
            } else {
                System.out.println("🔍 ObtenerInfoEntradaRemisionUseCase - No se encontró remisión para delivery: " + delivery);
            }
            
            // 🎯 Retornar resultado (puede ser null si no se encuentra)
            return remision;
            
        } catch (Exception ex) {
            // 🚨 Manejo de errores con contexto completo
            String errorMsg = "Error al obtener información de entrada de remisión para delivery: " + delivery;
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
     * @return EntradaCombustibleBean con información de la remisión
     */
    public static EntradaCombustibleBean obtenerInfo(String delivery) {
        return new ObtenerInfoEntradaRemisionUseCase().execute(delivery);
    }
    
    /**
     * 🔍 Método de conveniencia para verificar si existe una remisión
     * 
     * @param delivery número de entrega/delivery de la remisión
     * @return true si existe la remisión, false en caso contrario
     */
    public static boolean existeRemision(String delivery) {
        EntradaCombustibleBean remision = obtenerInfo(delivery);
        return remision != null;
    }
} 
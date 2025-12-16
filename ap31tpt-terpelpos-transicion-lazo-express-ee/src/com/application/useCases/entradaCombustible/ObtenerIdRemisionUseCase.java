package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EntradaCombustibleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 🚀 MIGRACIÓN: Caso de uso para obtener ID de remisión por número de delivery
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<String, Long>
 * - Reemplaza EntradaCombustibleDao.obtenerIdRemision()
 * - Mantiene la lógica original: retorna ID de remisión o 0L si no existe
 * - Sigue estructura de FindAllCategoriasKIOSCOUseCase
 * 
 * FUNCIONALIDAD:
 * - Busca remisión SAP por número de delivery
 * - Retorna el ID interno de la remisión
 * - Manejo robusto de errores y logging
 * 
 * USO:
 * ```java
 * ObtenerIdRemisionUseCase useCase = new ObtenerIdRemisionUseCase();
 * Long idRemision = useCase.execute("DELIVERY12345");
 * ```
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 * @since 2024
 */
public class ObtenerIdRemisionUseCase implements BaseUseCasesWithParams<String, Long> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    /**
     * 🏗️ Constructor que inicializa el EntityManagerFactory
     * Utiliza la misma base de datos que otras migraciones
     */
    public ObtenerIdRemisionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🎯 Método principal que ejecuta la lógica del caso de uso
     * 
     * @param numeroRemision número de delivery de la remisión (no puede ser null/vacío)
     * @return Long con el ID de la remisión o 0L si no existe
     * 
     * @throws IllegalArgumentException si numeroRemision es null o vacío
     * @throws RuntimeException si hay errores críticos de base de datos
     */
    @Override
    public Long execute(String numeroRemision) {
        // 🔍 Validación de entrada
        if (numeroRemision == null || numeroRemision.trim().isEmpty()) {
            System.err.println("❌ ObtenerIdRemisionUseCase: Número de remisión no puede ser null o vacío");
            throw new IllegalArgumentException("Número de remisión es requerido");
        }
        
        // 🚀 Logging de inicio
        System.out.println("🔍 ObtenerIdRemisionUseCase: Buscando ID para delivery: " + numeroRemision);
        
        EntityManager em = entityManagerFactory.createEntityManager();
        
        try {
            // 🏛️ Crear repositorio con EntityManager
            EntradaCombustibleRepository repository = new EntradaCombustibleRepository(em);
            
            // 🎯 Ejecutar consulta principal
            Long idRemision = repository.obtenerIdRemision(numeroRemision.trim());
            
            // 📊 Logging de resultado
            if (idRemision != null && idRemision > 0) {
                System.out.println("✅ ObtenerIdRemisionUseCase: Encontrado ID " + idRemision 
                                 + " para delivery: " + numeroRemision);
            } else {
                System.out.println("⚠️ ObtenerIdRemisionUseCase: No se encontró remisión para delivery: " + numeroRemision);
            }
            
            return idRemision != null ? idRemision : 0L;
            
        } catch (Exception ex) {
            // 🚨 Manejo de errores críticos
            System.err.println("❌ ObtenerIdRemisionUseCase: Error crítico al buscar ID de remisión");
            System.err.println("   Delivery: " + numeroRemision);
            System.err.println("   Error: " + ex.getMessage());
            ex.printStackTrace();
            
            // Retorna 0L para mantener compatibilidad con DAO original
            return 0L;
            
        } finally {
            // 🧹 Limpieza de recursos
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * 🎯 Método de conveniencia para verificar si una remisión existe
     * 
     * @param numeroRemision número de delivery a verificar
     * @return true si la remisión existe, false en caso contrario
     */
    public boolean existeRemision(String numeroRemision) {
        try {
            Long idRemision = execute(numeroRemision);
            return idRemision != null && idRemision > 0L;
        } catch (Exception ex) {
            System.err.println("❌ Error al verificar existencia de remisión: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * 🔍 Método de conveniencia que retorna Optional para casos avanzados
     * 
     * @param numeroRemision número de delivery a buscar
     * @return Optional con el ID si existe, empty() si no existe
     */
    public java.util.Optional<Long> buscarIdRemision(String numeroRemision) {
        try {
            Long idRemision = execute(numeroRemision);
            return (idRemision != null && idRemision > 0L) 
                   ? java.util.Optional.of(idRemision) 
                   : java.util.Optional.empty();
        } catch (Exception ex) {
            System.err.println("❌ Error en búsqueda opcional de remisión: " + ex.getMessage());
            return java.util.Optional.empty();
        }
    }
}
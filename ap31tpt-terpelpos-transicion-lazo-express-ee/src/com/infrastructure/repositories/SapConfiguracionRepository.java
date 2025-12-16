package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * 🚀 MIGRACIÓN: Repositorio para operaciones de configuración SAP
 * Reemplaza consultas directas en SapConfiguracionDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseRepositoryInterface
 * - Utiliza consultas nativas desde SqlQueryEnum
 * - Manejo de errores y validaciones
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class SapConfiguracionRepository implements BaseRepositoryInterface<Object> {
    
    private final EntityManager entityManager;
    
    public SapConfiguracionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * 🚀 MIGRACIÓN: Verifica si la empresa es de tipo Masser
     * Equivale al método isMasser() del DAO original
     * 
     * @return boolean true si la empresa es tipo Masser, false en caso contrario
     */
    public boolean isMasser() {
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.IS_MASSER.getQuery()
            );
            
            List<?> resultados = query.getResultList();
            
            // Si hay resultados, significa que encontró una empresa tipo Masser
            return !resultados.isEmpty();
            
        } catch (Exception ex) {
            System.err.println("❌ Error en SapConfiguracionRepository.isMasser(): " + ex.getMessage());
            ex.printStackTrace();
            return false; // Fail-safe: retorna false en caso de error
        }
    }
    
    // 🔧 Implementaciones requeridas por BaseRepositoryInterface
    
    @Override
    public Object save(Object entity) {
        throw new UnsupportedOperationException("Método save no implementado para SapConfiguracionRepository");
    }
    
    @Override
    public Object update(Object entity) {
        throw new UnsupportedOperationException("Método update no implementado para SapConfiguracionRepository");
    }
    
    @Override
    public void delete(Object entity) {
        throw new UnsupportedOperationException("Método delete no implementado para SapConfiguracionRepository");
    }
    
    @Override
    public Optional<Object> findById(Object id) {
        throw new UnsupportedOperationException("Método findById no implementado para SapConfiguracionRepository");
    }
    
    @Override
    public List<Object> findAll() {
        throw new UnsupportedOperationException("Método findAll no implementado para SapConfiguracionRepository");
    }
    
    @Override
    public List<Object> findByQuery(String query, Object... parameters) {
        throw new UnsupportedOperationException("Método findByQuery no implementado para SapConfiguracionRepository");
    }
    
    @Override
    public List<?> findByNativeQuery(String query, Object... parameters) {
        throw new UnsupportedOperationException("Método findByNativeQuery no implementado para SapConfiguracionRepository");
    }
} 
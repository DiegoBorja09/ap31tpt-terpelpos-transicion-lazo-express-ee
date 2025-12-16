package com.application.useCases.sapConfiguracion;

import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SapConfiguracionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 🚀 MIGRACIÓN: Caso de uso para verificar si la empresa es de tipo Masser
 * Reemplaza el método isMasser() de SapConfiguracionDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<Void, Boolean>
 * - Utiliza SapConfiguracionRepository para acceso a datos
 * - Manejo de EntityManager con try-finally
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class IsMasserUseCase implements BaseUseCasesWithParams<Void, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public IsMasserUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🎯 Ejecuta la verificación si la empresa es tipo Masser
     * 
     * @param input Parámetro void (no se usa)
     * @return Boolean true si la empresa es tipo Masser, false en caso contrario
     */
    @Override
    public Boolean execute(Void input) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            SapConfiguracionRepository repository = new SapConfiguracionRepository(em);
            return repository.isMasser();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * 🔧 Método de conveniencia sin parámetros
     * Mantiene la compatibilidad con el método original que no recibía parámetros
     * 
     * @return Boolean true si la empresa es tipo Masser, false en caso contrario
     */
    public Boolean execute() {
        return execute(null);
    }
} 
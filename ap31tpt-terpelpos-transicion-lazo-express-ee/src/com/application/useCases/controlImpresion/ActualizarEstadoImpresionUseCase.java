package com.application.useCases.controlImpresion;

import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ControlImpresionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * 🚀 MIGRACIÓN: Caso de uso para actualizar estado de impresión de movimientos
 * Reemplaza el método actualizarEstadoImpresion() de ControlImpresionDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<Long, Boolean>
 * - Utiliza ControlImpresionRepository para acceso a datos
 * - Manejo de EntityManager con try-finally
 * - Gestión de transacciones para operaciones UPDATE
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ActualizarEstadoImpresionUseCase implements BaseUseCasesWithParams<Long, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ActualizarEstadoImpresionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    public ActualizarEstadoImpresionUseCase(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * 🎯 Ejecuta la actualización del estado de impresión
     * 
     * @param idVenta ID del movimiento/venta a actualizar
     * @return Boolean true si se actualizó exitosamente, false en caso contrario
     */
    @Override
    public Boolean execute(Long idVenta) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            transaction = em.getTransaction();
            transaction.begin();
            
            ControlImpresionRepository repository = new ControlImpresionRepository(em);
            boolean resultado = repository.actualizarEstadoImpresion(idVenta);
            
            transaction.commit();
            return resultado;
            
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
} 
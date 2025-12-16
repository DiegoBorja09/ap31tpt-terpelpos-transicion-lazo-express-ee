package com.application.useCases.controlImpresion;

import com.application.core.AbstractUseCase;
import com.application.core.BaseUseCases;
import com.firefuel.controlImpresion.dto.Venta;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ControlImpresionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.TreeMap;

/**
 * 🚀 MIGRACIÓN: Caso de uso para obtener ventas pendientes de impresión
 * Reemplaza el método ventasPedientesImpresion() de ControlImpresionDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCases<TreeMap<Long, Venta>>
 * - Utiliza ControlImpresionRepository para acceso a datos
 * - Manejo de EntityManager con try-finally
 * - Intervalo fijo de 15 minutos como el DAO original
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ObtenerVentasPendientesImpresionUseCase extends AbstractUseCase<TreeMap<Long, Venta>> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ObtenerVentasPendientesImpresionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    public ObtenerVentasPendientesImpresionUseCase(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * 🎯 Ejecuta la obtención de ventas pendientes de impresión
     * Usa intervalo fijo de 15 minutos como el DAO original
     * 
     * @return TreeMap<Long, Venta> con ventas pendientes indexadas por ID
     */
    @Override
    public TreeMap<Long, Venta> run() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ControlImpresionRepository repository = new ControlImpresionRepository(em);
            return repository.obtenerVentasPendientesImpresion();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
} 
package com.application.useCases.controlImpresion;

import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ControlImpresionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 🚀 MIGRACIÓN: Caso de uso para obtener tiempo de impresión FE
 * Reemplaza el método tiempoImpresionFE() de ControlImpresionDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<String, Integer>
 * - Utiliza ControlImpresionRepository para acceso a datos
 * - Manejo de EntityManager con try-finally
 * - Valor por defecto de 40 segundos como en el DAO original
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ObtenerTiempoImpresionFEUseCase implements BaseUseCasesWithParams<String, Integer> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ObtenerTiempoImpresionFEUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    public ObtenerTiempoImpresionFEUseCase(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * 🎯 Ejecuta la obtención del tiempo de impresión FE por código de parámetro
     * 
     * @param codigoParametro Código del parámetro en wacher_parametros
     * @return Tiempo en segundos, 40 por defecto si no se encuentra el parámetro
     */
    @Override
    public Integer execute(String codigoParametro) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ControlImpresionRepository repository = new ControlImpresionRepository(em);
            return repository.obtenerTiempoImpresionFE(codigoParametro);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
} 
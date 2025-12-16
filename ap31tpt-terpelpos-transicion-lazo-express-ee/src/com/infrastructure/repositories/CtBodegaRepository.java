package com.infrastructure.repositories;

import com.domain.entities.CtBodegaEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones relacionadas con la entidad CtBodegaEntity (tabla ct_bodegas)
 */
public class CtBodegaRepository implements BaseRepositoryInterface<CtBodegaEntity> {

    private final EntityManager entityManager;

    public CtBodegaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtBodegaEntity save(CtBodegaEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving bodega", e);
        }
    }

    @Override
    public CtBodegaEntity update(CtBodegaEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating bodega", e);
        }
    }

    @Override
    public void delete(CtBodegaEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting bodega", e);
        }
    }

    @Override
    public Optional<CtBodegaEntity> findById(Object id) {
        try {
            CtBodegaEntity entity = entityManager.find(CtBodegaEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error finding bodega by id", e);
        }
    }

    @Override
    public List<CtBodegaEntity> findAll() {
        try {
            TypedQuery<CtBodegaEntity> query = entityManager.createQuery("SELECT b FROM CtBodegaEntity b", CtBodegaEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all bodegas", e);
        }
    }

    @Override
    public List<CtBodegaEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtBodegaEntity> query = entityManager.createQuery(jpql, CtBodegaEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing JPQL query", e);
        }
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        try {
            Query query = entityManager.createNativeQuery(sql);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native SQL query", e);
        }
    }

    /**
     * Obtiene el ID de la bodega UREA con tipo 'V' (Venta)
     * Migración JPA del método idBodegaUREA() de RumboDao
     * @return ID de la bodega UREA, o 0L si no se encuentra
     */
    public Long obtenerIdBodegaUrea() {
        try {
            String sql = SqlQueryEnum.OBTENER_ID_BODEGA_UREA.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            List<Object> results = query.getResultList();
            
            if (results.isEmpty()) {
                return 0L;
            }
            
            // Convertir el resultado a Long de forma segura
            Object result = results.get(0);
            if (result instanceof Number) {
                return ((Number) result).longValue();
            }
            
            return 0L;
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ID bodega UREA: " + e.getMessage());
            return 0L;
        }
    }
} 
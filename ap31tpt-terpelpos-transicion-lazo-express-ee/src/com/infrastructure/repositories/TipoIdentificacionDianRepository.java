package com.infrastructure.repositories;

import com.domain.entities.TipoIdentificacionDianEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipoIdentificacionDianRepository implements BaseRepositoryInterface<TipoIdentificacionDianEntity> {

    private final EntityManager entityManager;

    public TipoIdentificacionDianRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TipoIdentificacionDianEntity save(TipoIdentificacionDianEntity entity) {
        try {
            entityManager.getTransaction().begin();
            if (entity.getId() == null) {
                entityManager.persist(entity);
            } else {
                entity = entityManager.merge(entity);
            }
            entityManager.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public TipoIdentificacionDianEntity update(TipoIdentificacionDianEntity entity) {
        return null;
    }

    @Override
    public void delete(TipoIdentificacionDianEntity entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<TipoIdentificacionDianEntity> findById(Object id) {
        return Optional.empty();
    }

    @Override
    public List<TipoIdentificacionDianEntity> findAll() {
        try {
            TypedQuery<TipoIdentificacionDianEntity> query = entityManager.createQuery(
                    "SELECT i FROM IdentificacionDianEntity i", TipoIdentificacionDianEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<TipoIdentificacionDianEntity> findByQuery(String jpql, Object... parameters) {
        return List.of();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        return List.of();
    }

    public List<TipoIdentificacionDianEntity> findAllOrdered() {
        String jpql = "SELECT t FROM TipoIdentificacionDianEntity t " +
                "ORDER BY CASE " +
                "WHEN t.tipoDeIdentificacion = 'Cedula de ciudadania' THEN 0 " +
                "ELSE 1 END, " +
                "t.tipoDeIdentificacion";

        return entityManager.createQuery(jpql, TipoIdentificacionDianEntity.class)
                .getResultList();
    }
}

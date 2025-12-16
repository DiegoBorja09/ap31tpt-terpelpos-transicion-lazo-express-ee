package com.infrastructure.repositories;

import com.domain.entities.LtHorarioEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class LtHorarioRepository implements BaseRepositoryInterface<LtHorarioEntity> {
    private final EntityManager entityManager;

    public LtHorarioRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LtHorarioEntity save(LtHorarioEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving horario", e);
        }
    }

    @Override
    public LtHorarioEntity update(LtHorarioEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating horario", e);
        }
    }

    @Override
    public void delete(LtHorarioEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting horario", e);
        }
    }

    @Override
    public Optional<LtHorarioEntity> findById(Object id) {
        try {
            LtHorarioEntity horario = entityManager.find(LtHorarioEntity.class, id);
            return Optional.ofNullable(horario);
        } catch (Exception e) {
            throw new RuntimeException("Error finding horario by id", e);
        }
    }

    @Override
    public List<LtHorarioEntity> findAll() {
        try {
            TypedQuery<LtHorarioEntity> query = entityManager.createQuery("SELECT h FROM LtHorario h", LtHorarioEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all horarios", e);
        }
    }

    @Override
    public List<LtHorarioEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<LtHorarioEntity> query = entityManager.createQuery(jpql, LtHorarioEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing custom query", e);
        }
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        return List.of();
    }
} 
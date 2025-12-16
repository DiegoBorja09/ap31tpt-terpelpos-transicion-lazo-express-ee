package com.infrastructure.repositories;

import com.domain.entities.CtMovimientosClienteEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CtMovimientosClienteRepository implements BaseRepositoryInterface<CtMovimientosClienteEntity> {
    private final EntityManager entityManager;

    public CtMovimientosClienteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtMovimientosClienteEntity save(CtMovimientosClienteEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving movimiento cliente", e);
        }
    }

    @Override
    public CtMovimientosClienteEntity update(CtMovimientosClienteEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating movimiento cliente", e);
        }
    }

    @Override
    public void delete(CtMovimientosClienteEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting movimiento cliente", e);
        }
    }

    @Override
    public Optional<CtMovimientosClienteEntity> findById(Object id) {
        try {
            CtMovimientosClienteEntity movimientoCliente = entityManager.find(CtMovimientosClienteEntity.class, id);
            return Optional.ofNullable(movimientoCliente);
        } catch (Exception e) {
            throw new RuntimeException("Error finding movimiento cliente by id", e);
        }
    }

    @Override
    public List<CtMovimientosClienteEntity> findAll() {
        try {
            TypedQuery<CtMovimientosClienteEntity> query = entityManager.createQuery("SELECT m FROM CtMovimientosClienteEntity m", CtMovimientosClienteEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all movimientos cliente", e);
        }
    }

    @Override
    public List<CtMovimientosClienteEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtMovimientosClienteEntity> query = entityManager.createQuery(jpql, CtMovimientosClienteEntity.class);
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
        try {
            var query = entityManager.createNativeQuery(sql);
            
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query: " + sql, e);
        }
    }
}

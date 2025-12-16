package com.infrastructure.repositories;

import com.domain.entities.CtMovimientoMedioDePagoEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CtMovimientoMedioDePaogoRepository implements BaseRepositoryInterface<CtMovimientoMedioDePagoEntity> {
    private final EntityManager entityManager;

    public CtMovimientoMedioDePaogoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtMovimientoMedioDePagoEntity save(CtMovimientoMedioDePagoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving movimiento medio de pago", e);
        }
    }

    @Override
    public CtMovimientoMedioDePagoEntity update(CtMovimientoMedioDePagoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating movimiento medio de pago", e);
        }
    }

    @Override
    public void delete(CtMovimientoMedioDePagoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting movimiento medio de pago", e);
        }
    }

    @Override
    public Optional<CtMovimientoMedioDePagoEntity> findById(Object id) {
        try {
            CtMovimientoMedioDePagoEntity medioPago = entityManager.find(CtMovimientoMedioDePagoEntity.class, id);
            return Optional.ofNullable(medioPago);
        } catch (Exception e) {
            throw new RuntimeException("Error finding movimiento medio de pago by id", e);
        }
    }

    @Override
    public List<CtMovimientoMedioDePagoEntity> findAll() {
        try {
            TypedQuery<CtMovimientoMedioDePagoEntity> query = entityManager.createQuery(
                "SELECT m FROM CtMovimientoMedioDePagoEntity m", CtMovimientoMedioDePagoEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all movimientos medios de pago", e);
        }
    }

    @Override
    public List<CtMovimientoMedioDePagoEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtMovimientoMedioDePagoEntity> query = entityManager.createQuery(jpql, CtMovimientoMedioDePagoEntity.class);
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

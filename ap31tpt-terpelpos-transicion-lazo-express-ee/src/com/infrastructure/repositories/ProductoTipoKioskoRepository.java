package com.infrastructure.repositories;

import com.bean.MovimientosDetallesBean;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para manejar las consultas de productos tipo KIOSKO y CANASTILLA
 * usando la función nativa fnc_buscar_productos_market
 */
public class ProductoTipoKioskoRepository implements BaseRepositoryInterface<MovimientosDetallesBean> {
    
    private final EntityManager entityManager;

    public ProductoTipoKioskoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public MovimientosDetallesBean save(MovimientosDetallesBean entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving producto tipo kiosko", e);
        }
    }

    @Override
    public MovimientosDetallesBean update(MovimientosDetallesBean entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating producto tipo kiosko", e);
        }
    }

    @Override
    public void delete(MovimientosDetallesBean entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting producto tipo kiosko", e);
        }
    }

    @Override
    public Optional<MovimientosDetallesBean> findById(Object id) {
        try {
            MovimientosDetallesBean producto = entityManager.find(MovimientosDetallesBean.class, id);
            return Optional.ofNullable(producto);
        } catch (Exception e) {
            throw new RuntimeException("Error finding producto tipo kiosko by id", e);
        }
    }

    @Override
    public List<MovimientosDetallesBean> findAll() {
        try {
            TypedQuery<MovimientosDetallesBean> query = entityManager.createQuery(
                "SELECT m FROM MovimientosDetallesBean m", MovimientosDetallesBean.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all productos tipo kiosko", e);
        }
    }

    @Override
    public List<MovimientosDetallesBean> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<MovimientosDetallesBean> query = entityManager.createQuery(jpql, MovimientosDetallesBean.class);
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
            
            // Asignar parámetros posicionales (?, ?, ?)
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]); // JPA usa índices 1-based
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query: " + sql, e);
        }
    }
} 
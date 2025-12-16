package com.infrastructure.repositories;

import com.domain.entities.CtMovimientoDetalleEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import com.infrastructure.Enums.SqlQueryEnum;

public class CtMovimeintoDetallesRepository implements BaseRepositoryInterface<CtMovimientoDetalleEntity> {

    private final EntityManager entityManager;

    public CtMovimeintoDetallesRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtMovimientoDetalleEntity save(CtMovimientoDetalleEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving movimiento detalle", e);
        }
    }

    @Override
    public CtMovimientoDetalleEntity update(CtMovimientoDetalleEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating movimiento detalle", e);
        }
    }

    @Override
    public void delete(CtMovimientoDetalleEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting movimiento detalle", e);
        }
    }

    @Override
    public Optional<CtMovimientoDetalleEntity> findById(Object id) {
        try {
            CtMovimientoDetalleEntity entity = entityManager.find(CtMovimientoDetalleEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error finding movimiento detalle by id", e);
        }
    }

    @Override
    public List<CtMovimientoDetalleEntity> findAll() {
        try {
            TypedQuery<CtMovimientoDetalleEntity> query = entityManager.createQuery("SELECT m FROM CtMovimientoDetalleEntity m", CtMovimientoDetalleEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all movimiento detalles", e);
        }
    }

    @Override
    public List<CtMovimientoDetalleEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtMovimientoDetalleEntity> query = entityManager.createQuery(jpql, CtMovimientoDetalleEntity.class);
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
            javax.persistence.Query query = entityManager.createNativeQuery(sql);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native SQL query", e);
        }
    }

    public <T> Optional<T> finByMovimientoId(Long idMovimiento, String column, Class<T> returnType) {
        try {
            // Usar SQL nativo en lugar de JPQL para evitar problemas de configuración JPA
            String sql = "SELECT " + column + " FROM ct_movimientos_detalles WHERE movimientos_id = ? ORDER BY id ASC LIMIT 1";
            javax.persistence.Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, idMovimiento);
            
            Object result = query.getResultList().stream().findFirst().orElse(null);
            if (result != null) {
                return Optional.of(returnType.cast(result));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding " + column + " by movimiento id", e);
        }
    }
     /**
     * Obtiene el ID del producto desde un movimiento específico
     * Reemplaza: MovimientosDao.obtenerIdProductoDesdeMovimiento()
     * @param movimientoId ID del movimiento
     * @return productos_id encontrado o 0L si no existe
     */
    public Long obtenerIdProductoDesdeMovimiento(Long movimientoId) {
        try {
            String sql = SqlQueryEnum.OBTENER_ID_PRODUCTO_DESDE_MOVIMIENTO.getQuery();
            Object result = entityManager.createNativeQuery(sql)
                    .setParameter(1, movimientoId)
                    .getSingleResult();
            
            if (result != null) {
                return ((Number) result).longValue();
            }
            return 0L;
        } catch (Exception e) {
            // Si no encuentra registros o hay error, retorna 0L
            return 0L;
        }
    }
}

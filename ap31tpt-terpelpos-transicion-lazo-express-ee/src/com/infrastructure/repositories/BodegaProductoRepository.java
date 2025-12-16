package com.infrastructure.repositories;

import com.domain.entities.BodegaProductoEntity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class BodegaProductoRepository {

    private final EntityManager entityManager;

    public BodegaProductoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Guarda una nueva entidad BodegaProducto
     */
    public BodegaProductoEntity save(BodegaProductoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar BodegaProducto", e);
        }
    }

    /**
     * Busca una BodegaProducto por su ID
     */
    public Optional<BodegaProductoEntity> findById(Long id) {
        try {
            BodegaProductoEntity entity = entityManager.find(BodegaProductoEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar BodegaProducto por ID", e);
        }
    }

    /**
     * Obtiene todos los registros de BodegaProducto
     */
    public List<BodegaProductoEntity> findAll() {
        try {
            TypedQuery<BodegaProductoEntity> query = entityManager.createQuery(
                "SELECT bp FROM BodegaProductoEntity bp", 
                BodegaProductoEntity.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los BodegaProducto", e);
        }
    }

    /**
     * Busca BodegaProducto por ID de producto
     */
    public Optional<BodegaProductoEntity> findByProductoId(Long productoId) {
        try {
            TypedQuery<BodegaProductoEntity> query = entityManager.createQuery(
                "SELECT bp FROM BodegaProductoEntity bp WHERE bp.productosId = :productoId",
                BodegaProductoEntity.class
            );
            query.setParameter("productoId", productoId);
            List<BodegaProductoEntity> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar BodegaProducto por productoId", e);
        }
    }

    /**
     * Actualiza una entidad BodegaProducto existente
     * @param entity entidad a actualizar
     * @return la entidad actualizada
     */
    public BodegaProductoEntity update(BodegaProductoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar BodegaProducto", e);
        }
    }

    /**
     * Busca BodegaProducto por ID de bodega
     * @param bodegaId ID de la bodega a buscar
     * @return lista de entidades encontradas
     */
    public List<BodegaProductoEntity> findByBodegaId(Long bodegaId) {
        try {
            TypedQuery<BodegaProductoEntity> query = entityManager.createQuery(
                "SELECT bp FROM BodegaProductoEntity bp WHERE bp.bodegasId = :bodegaId",
                BodegaProductoEntity.class
            );
            query.setParameter("bodegaId", bodegaId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar BodegaProducto por bodegaId", e);
        }
    }

    /**
     * Elimina una entidad BodegaProducto
     * @param entity entidad a eliminar
     */
    public void delete(BodegaProductoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar BodegaProducto", e);
        }
    }
} 
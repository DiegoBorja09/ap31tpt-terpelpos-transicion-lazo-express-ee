package com.infrastructure.repositories;

import com.domain.entities.ImpuestoEntity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ImpuestoRepository {

    private final EntityManager entityManager;

    public ImpuestoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Guarda un nuevo impuesto
     */
    public ImpuestoEntity save(ImpuestoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar Impuesto", e);
        }
    }

    /**
     * Actualiza un impuesto existente
     */
    public ImpuestoEntity update(ImpuestoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar Impuesto", e);
        }
    }

    /**
     * Busca un impuesto por su ID
     */
    public Optional<ImpuestoEntity> findById(Long id) {
        try {
            ImpuestoEntity entity = entityManager.find(ImpuestoEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar Impuesto por ID", e);
        }
    }

    /**
     * Obtiene todos los impuestos
     */
    public List<ImpuestoEntity> findAll() {
        try {
            TypedQuery<ImpuestoEntity> query = entityManager.createQuery(
                "SELECT i FROM ImpuestoEntity i", 
                ImpuestoEntity.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los Impuestos", e);
        }
    }

    /**
     * Busca impuestos por empresa
     */
    public List<ImpuestoEntity> findByEmpresaId(Long empresaId) {
        try {
            TypedQuery<ImpuestoEntity> query = entityManager.createQuery(
                "SELECT i FROM ImpuestoEntity i WHERE i.empresasId = :empresaId",
                ImpuestoEntity.class
            );
            query.setParameter("empresaId", empresaId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar Impuestos por empresaId", e);
        }
    }

    /**
     * Busca impuestos activos por fecha
     */
    public List<ImpuestoEntity> findActiveByDate(LocalDateTime fecha) {
        try {
            TypedQuery<ImpuestoEntity> query = entityManager.createQuery(
                "SELECT i FROM ImpuestoEntity i " +
                "WHERE i.estado = 'A' " +
                "AND i.fechaInicio <= :fecha " +
                "AND (i.fechaFin IS NULL OR i.fechaFin >= :fecha)",
                ImpuestoEntity.class
            );
            query.setParameter("fecha", fecha);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar Impuestos activos por fecha", e);
        }
    }

    /**
     * Elimina un impuesto
     */
    public void delete(ImpuestoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar Impuesto", e);
        }
    }

    /**
     * Busca impuestos por estado
     */
    public List<ImpuestoEntity> findByEstado(String estado) {
        try {
            TypedQuery<ImpuestoEntity> query = entityManager.createQuery(
                "SELECT i FROM ImpuestoEntity i WHERE i.estado = :estado",
                ImpuestoEntity.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar Impuestos por estado", e);
        }
    }
} 
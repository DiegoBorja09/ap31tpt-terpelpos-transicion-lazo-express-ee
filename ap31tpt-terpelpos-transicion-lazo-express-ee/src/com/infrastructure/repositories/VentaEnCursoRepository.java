package com.infrastructure.repositories;

import com.domain.entities.VentaEnCursoEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class VentaEnCursoRepository implements BaseRepositoryInterface<VentaEnCursoEntity> {

    private final EntityManager entityManager;

    public VentaEnCursoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public VentaEnCursoEntity save(VentaEnCursoEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        return entity;
    }

    @Override
    public VentaEnCursoEntity update(VentaEnCursoEntity entity) {
        entityManager.getTransaction().begin();
        VentaEnCursoEntity merged = entityManager.merge(entity);
        entityManager.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(VentaEnCursoEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<VentaEnCursoEntity> findById(Object id) {
        VentaEnCursoEntity entity = entityManager.find(VentaEnCursoEntity.class, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<VentaEnCursoEntity> findAll() {
        TypedQuery<VentaEnCursoEntity> query = entityManager.createQuery("SELECT v FROM VentaEnCursoEntity v", VentaEnCursoEntity.class);
        return query.getResultList();
    }

    @Override
    public List<VentaEnCursoEntity> findByQuery(String jpql, Object... parameters) {
        TypedQuery<VentaEnCursoEntity> query = entityManager.createQuery(jpql, VentaEnCursoEntity.class);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        Query query = entityManager.createNativeQuery(sql, VentaEnCursoEntity.class);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }

    // 🏗️ Métodos específicos para VentaEnCurso

    /**
     * Crea la tabla lt_ventas_curso si no existe
     * @return true si se ejecutó correctamente
     */
    public boolean crearTablaVentasEnCurso() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.CREAR_TABLA_VENTAS_CURSO.getQuery());
            query.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error creando tabla lt_ventas_curso: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activa una venta en curso para el negocio especificado
     * @param negocio Tipo de negocio (ej: "CAN")
     * @return true si se activó correctamente
     */
    public boolean activarVentaEnCurso(String negocio) {
        try {
            entityManager.getTransaction().begin();
            
            // Primero asegurar que la tabla existe
            crearTablaVentasEnCurso();
            
            Query query = entityManager.createNativeQuery(SqlQueryEnum.ACTIVAR_VENTA_EN_CURSO.getQuery());
            query.setParameter(1, negocio);
            query.executeUpdate();
            
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("⚠️ Error activando venta en curso: " + e.getMessage());
            return false;
        }
    }

    /**
     * Desactiva la venta en curso para el negocio especificado
     * @param negocio Tipo de negocio (ej: "CAN")
     * @return true si se desactivó correctamente
     */
    public boolean desactivarVentaEnCurso(String negocio) {
        try {
            entityManager.getTransaction().begin();
            
            // Primero asegurar que la tabla existe
            crearTablaVentasEnCurso();
            
            Query query = entityManager.createNativeQuery(SqlQueryEnum.DESACTIVAR_VENTA_EN_CURSO.getQuery());
            query.setParameter(1, negocio);
            query.executeUpdate();
            
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("⚠️ Error desactivando venta en curso: " + e.getMessage());
            return false;
        }
    }
} 
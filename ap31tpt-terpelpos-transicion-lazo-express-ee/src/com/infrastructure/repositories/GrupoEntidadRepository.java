package com.infrastructure.repositories;

import com.domain.entities.GrupoEntidadEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad GrupoEntidadEntity
 * Maneja las operaciones de acceso a datos para la tabla grupos_entidad
 */
public class GrupoEntidadRepository implements BaseRepositoryInterface<GrupoEntidadEntity> {

    private final EntityManager entityManager;

    public GrupoEntidadRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public GrupoEntidadEntity save(GrupoEntidadEntity entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar GrupoEntidad", e);
        }
    }

    @Override
    public GrupoEntidadEntity update(GrupoEntidadEntity entity) {
        try {
            entityManager.getTransaction().begin();
            GrupoEntidadEntity updated = entityManager.merge(entity);
            entityManager.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar GrupoEntidad", e);
        }
    }

    @Override
    public void delete(GrupoEntidadEntity entity) {
        try {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(entity)) {
                entity = entityManager.merge(entity);
            }
            entityManager.remove(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar GrupoEntidad", e);
        }
    }

    @Override
    public Optional<GrupoEntidadEntity> findById(Object id) {
        try {
            GrupoEntidadEntity entity = entityManager.find(GrupoEntidadEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar GrupoEntidad por ID", e);
        }
    }

    @Override
    public List<GrupoEntidadEntity> findAll() {
        try {
            String jpql = "SELECT ge FROM GrupoEntidadEntity ge";
            return entityManager.createQuery(jpql, GrupoEntidadEntity.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las GrupoEntidades", e);
        }
    }

    @Override
    public List<GrupoEntidadEntity> findByQuery(String jpql, Object... parameters) {
        try {
            Query query = entityManager.createQuery(jpql, GrupoEntidadEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al ejecutar consulta JPQL", e);
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
            throw new RuntimeException("Error al ejecutar consulta nativa", e);
        }
    }

    /**
     * Busca todas las relaciones grupo-entidad por grupo ID
     */
    public List<GrupoEntidadEntity> findByGrupoId(Long grupoId) {
        try {
            String jpql = "SELECT ge FROM GrupoEntidadEntity ge WHERE ge.grupoId = :grupoId";
            return entityManager.createQuery(jpql, GrupoEntidadEntity.class)
                    .setParameter("grupoId", grupoId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar por grupo ID", e);
        }
    }

    /**
     * Busca todas las relaciones grupo-entidad por entidad ID
     */
    public List<GrupoEntidadEntity> findByEntidadId(Long entidadId) {
        try {
            String jpql = "SELECT ge FROM GrupoEntidadEntity ge WHERE ge.entidadId = :entidadId";
            return entityManager.createQuery(jpql, GrupoEntidadEntity.class)
                    .setParameter("entidadId", entidadId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar por entidad ID", e);
        }
    }

    /**
     * Verifica si existe una relación específica entre grupo y entidad
     */
    public boolean existeRelacion(Long grupoId, Long entidadId) {
        try {
            String jpql = "SELECT COUNT(ge) FROM GrupoEntidadEntity ge WHERE ge.grupoId = :grupoId AND ge.entidadId = :entidadId";
            Long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("grupoId", grupoId)
                    .setParameter("entidadId", entidadId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de relación", e);
        }
    }

    /**
     * Elimina todas las relaciones de un grupo específico
     */
    public void deleteByGrupoId(Long grupoId) {
        try {
            entityManager.getTransaction().begin();
            String jpql = "DELETE FROM GrupoEntidadEntity ge WHERE ge.grupoId = :grupoId";
            entityManager.createQuery(jpql)
                    .setParameter("grupoId", grupoId)
                    .executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar relaciones por grupo ID", e);
        }
    }

    /**
     * Elimina todas las relaciones de una entidad específica
     */
    public void deleteByEntidadId(Long entidadId) {
        try {
            entityManager.getTransaction().begin();
            String jpql = "DELETE FROM GrupoEntidadEntity ge WHERE ge.entidadId = :entidadId";
            entityManager.createQuery(jpql)
                    .setParameter("entidadId", entidadId)
                    .executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar relaciones por entidad ID", e);
        }
    }
} 
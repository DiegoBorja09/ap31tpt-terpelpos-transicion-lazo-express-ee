package com.infrastructure.repositories;

import com.controllers.NovusConstante;
import com.domain.entities.CtWacherParametroEntity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.infrastructure.Enums.SqlQueryEnum;
import javax.persistence.Query;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CtWacherParametroRepository implements BaseRepositoryInterface<CtWacherParametroEntity> {

    private final EntityManager entityManager;

    public CtWacherParametroRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtWacherParametroEntity save(CtWacherParametroEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving wacher parametro", e);
        }
    }

    @Override
    public CtWacherParametroEntity update(CtWacherParametroEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating wacher parametro", e);
        }
    }

    @Override
    public void delete(CtWacherParametroEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting wacher parametro", e);
        }
    }

    @Override
    public Optional<CtWacherParametroEntity> findById(Object id) {
        try {
            CtWacherParametroEntity entity = entityManager.find(CtWacherParametroEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error finding wacher parametro by id", e);
        }
    }

    @Override
    public List<CtWacherParametroEntity> findAll() {
        try {
            TypedQuery<CtWacherParametroEntity> query = entityManager.createQuery("SELECT w FROM CtWacherParametroEntity w", CtWacherParametroEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all wacher parametros", e);
        }
    }

    @Override
    public List<CtWacherParametroEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtWacherParametroEntity> query = entityManager.createQuery(jpql, CtWacherParametroEntity.class);
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


    public Optional<CtWacherParametroEntity> findByParameter(String parameterColumn, String value) {
        try {
            TypedQuery<CtWacherParametroEntity> query = entityManager.createQuery("SELECT w FROM CtWacherParametroEntity w WHERE w." + parameterColumn + " = ?1", CtWacherParametroEntity.class);
            query.setParameter(1, value);
            return query.getResultStream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error finding wacher parametro by parameter", e);
        }
    }

    public String obtenerObligatoriedadFE() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBLIGATORIEDAD_FE.getQuery());
            Object result = query.getSingleResult();
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
    public float obtenerMontoMinimoFE() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.MONTO_MINIMO_FE.getQuery());
            Object result = query.getSingleResult();
            return result != null ? Float.parseFloat(result.toString()) : -1.0f;
        } catch (Exception e) {
            return -1.0f;
        }
    }
    public JsonArray obtenerMotivosAnulacion() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.MOTIVOS_ANULACION_FE.getQuery());
            Object result = query.getSingleResult();
            if (result != null) {
                Gson gson = new Gson();
                return gson.fromJson(result.toString(), JsonArray.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener motivos de anulación: " + e.getMessage());
        }
        return new JsonArray();
    }
    
    public String obtenerCodigoEstacion() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_CODIGO_ESTACION.getQuery());
            Object result = query.getSingleResult();
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            System.err.println("❌ Error al obtener código de estación: " + e.getMessage());
            return null;
        }
    }
    
    public boolean verificarIntegracionUrea() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.VERIFICAR_INTEGRACION_UREA.getQuery());
            query.setParameter(1, NovusConstante.PARAMETER_INTEGRACION_UREA);
            Object result = query.getSingleResult();
            return result != null && "S".equals(result.toString());
        } catch (Exception e) {
            System.err.println("❌ Error al verificar integración UREA: " + e.getMessage());
            return false;
        }
    }
    
}


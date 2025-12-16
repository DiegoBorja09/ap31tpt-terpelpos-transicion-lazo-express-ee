package com.infrastructure.repositories;

import com.domain.entities.MedioPagoEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MedioPagoRepository implements BaseRepositoryInterface<MedioPagoEntity> {
    
    private final EntityManager entityManager;
    private static final Logger LOGGER = Logger.getLogger(MedioPagoRepository.class.getName());

    public MedioPagoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public MedioPagoEntity save(MedioPagoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving medio pago", e);
        }
    }

    @Override
    public MedioPagoEntity update(MedioPagoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating medio pago", e);
        }
    }

    @Override
    public void delete(MedioPagoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting medio pago", e);
        }
    }

    @Override
    public Optional<MedioPagoEntity> findById(Object id) {
        try {
            MedioPagoEntity medioPago = entityManager.find(MedioPagoEntity.class, id);
            return Optional.ofNullable(medioPago);
        } catch (Exception e) {
            throw new RuntimeException("Error finding medio pago by id", e);
        }
    }

    @Override
    public List<MedioPagoEntity> findAll() {
        try {
            TypedQuery<MedioPagoEntity> query = entityManager.createQuery(
                "SELECT m FROM MedioPagoEntity m", 
                MedioPagoEntity.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all medios pago", e);
        }
    }

    @Override
    public List<MedioPagoEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<MedioPagoEntity> query = entityManager.createQuery(jpql, MedioPagoEntity.class);
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
            Query query = entityManager.createNativeQuery(sql);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query: " + sql, e);
        }
    }
    /**
     * Verifica si existe la configuración de GoPass
     * @return true si existe la configuración, false en caso contrario
     */
    public boolean existeGopass() {
        try {
            String sql = SqlQueryEnum.EXISTE_GOPASS.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            LOGGER.log(Level.INFO, "❌ [existeGopass] No se encontró configuración de GoPass");
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ [existeGopass] Error verificando existencia de GoPass", e);
            return false;
        }
    }

    /**
     * Busca un medio de pago por su código externo
     * @param codigoExterno el código externo a buscar
     * @return Optional con el medio de pago si existe
     */
    public Optional<MedioPagoEntity> findByCodigoExterno(String codigoExterno) {
        try {
            TypedQuery<MedioPagoEntity> query = entityManager.createQuery(
                "SELECT m FROM MedioPagoEntity m WHERE m.mpAtributos->>'codigoExterno' = :codigo",
                MedioPagoEntity.class
            );
            query.setParameter("codigo", codigoExterno);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error buscando medio pago por código externo", e);
            return Optional.empty();
        }
    }
} 
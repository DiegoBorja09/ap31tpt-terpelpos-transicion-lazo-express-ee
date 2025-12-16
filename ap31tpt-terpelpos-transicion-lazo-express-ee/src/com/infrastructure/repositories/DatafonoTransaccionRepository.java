package com.infrastructure.repositories;

import com.domain.entities.DatafonosTransaccionEntiity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DatafonoTransaccionRepository implements BaseRepositoryInterface<DatafonosTransaccionEntiity> {
    private final EntityManager entityManager;

    public DatafonoTransaccionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public DatafonosTransaccionEntiity save(DatafonosTransaccionEntiity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving datafono transaccion", e);
        }
    }

    @Override
    public DatafonosTransaccionEntiity update(DatafonosTransaccionEntiity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating datafono transaccion", e);
        }
    }

    @Override
    public void delete(DatafonosTransaccionEntiity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting datafono transaccion", e);
        }
    }

    @Override
    public Optional<DatafonosTransaccionEntiity> findById(Object id) {
        try {
            DatafonosTransaccionEntiity transaccion = entityManager.find(DatafonosTransaccionEntiity.class, id);
            return Optional.ofNullable(transaccion);
        } catch (Exception e) {
            throw new RuntimeException("Error finding datafono transaccion by id", e);
        }
    }

    @Override
    public List<DatafonosTransaccionEntiity> findAll() {
        try {
            TypedQuery<DatafonosTransaccionEntiity> query = entityManager.createQuery("SELECT t FROM DatafonosTransaccionEntiity t", DatafonosTransaccionEntiity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all datafono transacciones", e);
        }
    }

    @Override
    public List<DatafonosTransaccionEntiity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<DatafonosTransaccionEntiity> query = entityManager.createQuery(jpql, DatafonosTransaccionEntiity.class);
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

    /**
     * Busca anulaciones en curso para un movimiento específico
     * @param idMovimiento ID del movimiento a verificar
     * @param idTransaccionOperacion ID de la operación de anulación
     * @param idTransaccionEstado ID del estado pendiente de anulación
     * @return true si hay anulaciones en curso, false en caso contrario
     */
    public boolean hayAnulacionesEncursoEnGeneral(Long idMovimiento, Short idTransaccionOperacion, Short idTransaccionEstado) {
        try {
            List<?> resultado = findByNativeQuery(
                SqlQueryEnum.HAY_ANULACIONES_EN_CURSO_GENERAL.getQuery(),
                idMovimiento, 
                idTransaccionOperacion, 
                idTransaccionEstado
            );
            return !resultado.isEmpty();
        } catch (Exception e) {
            System.err.println("❌ Error en hayAnulacionesEncursoEnGeneral: " + e.getMessage());
            return false;
        }
    }
}

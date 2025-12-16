package com.infrastructure.repositories;

import com.domain.entities.DatafonosEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import com.firefuel.datafonos.EstadosTransaccionVenta;
import com.controllers.NovusUtils;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class DatafonosRepository implements BaseRepositoryInterface<DatafonosEntity> {

    private final EntityManager entityManager;

    public DatafonosRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public DatafonosEntity save(DatafonosEntity entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public DatafonosEntity update(DatafonosEntity entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void delete(DatafonosEntity entity) {
        entityManager.remove(entity);
    }

    @Override
    public Optional<DatafonosEntity> findById(Object id) {
        DatafonosEntity entity = entityManager.find(DatafonosEntity.class, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<DatafonosEntity> findAll() {
        try {
            TypedQuery<DatafonosEntity> query = entityManager.createQuery(
                "SELECT d FROM DatafonosEntity d", DatafonosEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all datafonos", e);
        }
    }

    @Override
    public List<DatafonosEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<DatafonosEntity> query = entityManager.createQuery(jpql, DatafonosEntity.class);
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
        return List.of();
    }

    
    public boolean HasOngoingDatafonSalesUseCase(String codigoTerminal) {
        try {
            String sql = "SELECT CASE WHEN COUNT(t) > 0 THEN 1 ELSE 0 END "
                    + "FROM transacciones t "
                    + "JOIN datafonos d ON t.id_datafono = d.id_datafono "
                    + "WHERE t.id_transaccion_estado IN (1,5) "
                    + "AND d.codigo_terminal = ?";

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, codigoTerminal);
            
            Object result = query.getSingleResult();
            return result instanceof Number && ((Number) result).intValue() == 1;
            
        } catch (Exception e) {
            System.err.println("❌ Error consultando ventas en curso de datafono: " + e.getMessage());
            return false;
        }
    }


 
    
}


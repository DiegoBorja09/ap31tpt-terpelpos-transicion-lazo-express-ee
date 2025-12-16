package com.infrastructure.repositories;

import com.domain.entities.TransmisionEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class TransmisionRepository {

    private final EntityManager entityManager;

    public TransmisionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TransmisionEntity save(TransmisionEntity transmision) {
        try {
            entityManager.persist(transmision);
            return transmision;
        } catch (Exception e) {
            throw new RuntimeException("Error saving transmision", e);
        }
    }

    public Optional<TransmisionEntity> findById(Long id) {
        try {
            TransmisionEntity transmision = entityManager.find(TransmisionEntity.class, id);
            return Optional.ofNullable(transmision);
        } catch (Exception e) {
            throw new RuntimeException("Error finding transmision by id", e);
        }
    }

    public List<TransmisionEntity> findAll() {
        try {
            String jpql = "SELECT t FROM TransmisionEntity t";
            return entityManager.createQuery(jpql, TransmisionEntity.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all transmisiones", e);
        }
    }

    public TransmisionEntity update(TransmisionEntity transmision) {
        try {
            return entityManager.merge(transmision);
        } catch (Exception e) {
            throw new RuntimeException("Error updating transmision", e);
        }
    }

    public void delete(Long id) {
        try {
            TransmisionEntity transmision = entityManager.find(TransmisionEntity.class, id);
            if (transmision != null) {
                entityManager.remove(transmision);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transmision", e);
        }
    }

    public List<TransmisionEntity> findByStatus(Integer status) {
        try {
            String jpql = "SELECT t FROM TransmisionEntity t WHERE t.status = :status";
            return entityManager.createQuery(jpql, TransmisionEntity.class)
                    .setParameter("status", status)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding transmisiones by status", e);
        }
    }

    public List<TransmisionEntity> findBySincronizado(Integer sincronizado) {
        try {
            String jpql = "SELECT t FROM TransmisionEntity t WHERE t.sincronizado = :sincronizado";
            return entityManager.createQuery(jpql, TransmisionEntity.class)
                    .setParameter("sincronizado", sincronizado)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding transmisiones by sincronizado", e);
        }
    }

    /**
     * Método para ejecutar consultas nativas personalizadas
     */
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
     * Inserta una autorización llamando a la función PostgreSQL fnc_insertar_autorizacion
     * @param autorizacion JSON string con la información de autorización
     * @return boolean indicando si la inserción fue exitosa
     */
    public boolean insertarAutorizacion(String autorizacion) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.INSERTAR_AUTORIZACION.getQuery());
            query.setParameter(1, autorizacion);
            
            Object result = query.getSingleResult();
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            // Si el resultado viene como array (lo común con PostgreSQL functions)
            if (result instanceof Object[]) {
                Object[] resultArray = (Object[]) result;
                if (resultArray.length > 0 && resultArray[0] instanceof Boolean) {
                    return (Boolean) resultArray[0];
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error al insertar autorización: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inserta confirmación de venta App llamando a la función PostgreSQL fnc_insert_tbl_autorizaciones_pos
     * NOTA: Actualmente no se usa en el código, pero se mantiene preparado para futuro uso
     * @param autorizacion JSON string con la información de autorización
     * @return boolean indicando si la inserción fue exitosa
     */
    public boolean insertarConfirmacionVentaApp(String autorizacion) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.INSERTAR_CONFIRMACION_VENTA_APP.getQuery());
            query.setParameter(1, autorizacion);
            
            Object result = query.getSingleResult();
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            // Si el resultado viene como array (lo común con PostgreSQL functions)
            if (result instanceof Object[]) {
                Object[] resultArray = (Object[]) result;
                if (resultArray.length > 0 && resultArray[0] instanceof Boolean) {
                    return (Boolean) resultArray[0];
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error al insertar confirmación de venta App: " + e.getMessage());
            return false;
        }
    }
}

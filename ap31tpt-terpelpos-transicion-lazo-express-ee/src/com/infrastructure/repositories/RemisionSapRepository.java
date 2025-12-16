package com.infrastructure.repositories;

import com.domain.entities.RemisionSapEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 🚀 MIGRACIÓN: Repositorio para operaciones con RemisionSapEntity
 * Reemplaza consultas directas en EntradaCombustibleDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseRepositoryInterface
 * - Utiliza consultas nativas desde SqlQueryEnum
 * - Manejo de errores y conversiones de tipos
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class RemisionSapRepository implements BaseRepositoryInterface<RemisionSapEntity> {
    
    private final EntityManager entityManager;
    
    public RemisionSapRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    // ========================================
    // 🔧 IMPLEMENTACIÓN DE BaseRepositoryInterface
    // ========================================
    
    @Override
    public RemisionSapEntity save(RemisionSapEntity entity) {
        try {
            if (entity.getIdRemisionSap() == null) {
                entityManager.persist(entity);
                return entity;
            } else {
                return entityManager.merge(entity);
            }
        } catch (Exception ex) {
            System.err.println("❌ Error al guardar RemisionSapEntity: " + ex.getMessage());
            throw new RuntimeException("Error al guardar remisión SAP", ex);
        }
    }
    
    @Override
    public RemisionSapEntity update(RemisionSapEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception ex) {
            System.err.println("❌ Error al actualizar RemisionSapEntity: " + ex.getMessage());
            throw new RuntimeException("Error al actualizar remisión SAP", ex);
        }
    }
    
    @Override
    public void delete(RemisionSapEntity entity) {
        try {
            if (entityManager.contains(entity)) {
                entityManager.remove(entity);
            } else {
                entityManager.remove(entityManager.merge(entity));
            }
        } catch (Exception ex) {
            System.err.println("❌ Error al eliminar RemisionSapEntity: " + ex.getMessage());
            throw new RuntimeException("Error al eliminar remisión SAP", ex);
        }
    }
    
    @Override
    public Optional<RemisionSapEntity> findById(Object id) {
        try {
            RemisionSapEntity entity = entityManager.find(RemisionSapEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception ex) {
            System.err.println("❌ Error al buscar RemisionSapEntity por ID: " + ex.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public List<RemisionSapEntity> findAll() {
        try {
            TypedQuery<RemisionSapEntity> query = entityManager.createQuery(
                "SELECT r FROM RemisionSapEntity r", RemisionSapEntity.class);
            return query.getResultList();
        } catch (Exception ex) {
            System.err.println("❌ Error al obtener todas las RemisionSapEntity: " + ex.getMessage());
            throw new RuntimeException("Error al obtener todas las remisiones SAP", ex);
        }
    }
    
    @Override
    public List<RemisionSapEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<RemisionSapEntity> query = entityManager.createQuery(jpql, RemisionSapEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception ex) {
            System.err.println("❌ Error en consulta JPQL: " + ex.getMessage());
            throw new RuntimeException("Error en consulta JPQL", ex);
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
        } catch (Exception ex) {
            System.err.println("❌ Error en consulta nativa: " + ex.getMessage());
            throw new RuntimeException("Error en consulta nativa", ex);
        }
    }
    
    // ========================================
    // 🎯 MÉTODOS ESPECÍFICOS DEL DOMINIO
    // ========================================
    
    /**
     * 🚀 MIGRACIÓN: Obtiene información de entrada de remisión por delivery
     * Equivale al método infoEntradaRemision() del DAO original
     * 
     * @param delivery número de entrega/delivery de la remisión
     * @return RemisionSapEntity con información de la remisión o null si no existe
     */
    public RemisionSapEntity obtenerInfoEntradaRemision(String delivery) {
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.INFO_ENTRADA_REMISION.getQuery()
            );
            query.setParameter(1, delivery);
            
            Object[] resultado = (Object[]) query.getSingleResult();
            
            if (resultado != null) {
                return mapearResultadoAEntidad(resultado);
            }
            
        } catch (NoResultException ex) {
            System.out.println("🔍 No se encontró remisión para delivery: " + delivery);
            return null;
            
        } catch (Exception ex) {
            System.err.println("❌ Error en RemisionSapRepository.obtenerInfoEntradaRemision(): " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al obtener información de remisión para delivery: " + delivery, ex);
        }
        
        return null;
    }
    
    /**
     * 🔧 Mapea el resultado de la consulta nativa a RemisionSapEntity
     * 
     * @param resultado array de objetos del resultado de la consulta
     * @return RemisionSapEntity mapeada
     */
    private RemisionSapEntity mapearResultadoAEntidad(Object[] resultado) {
        RemisionSapEntity entidad = new RemisionSapEntity();
        
        try {
            // Mapeo basado en la estructura de sap.tbl_remisiones_sap
            // Manejo seguro del casting para id_remision_sap
            if (resultado[0] != null) {
                if (resultado[0] instanceof BigInteger) {
                    entidad.setIdRemisionSap(((BigInteger) resultado[0]).longValue());
                } else if (resultado[0] instanceof Integer) {
                    entidad.setIdRemisionSap(((Integer) resultado[0]).longValue());
                } else if (resultado[0] instanceof Long) {
                    entidad.setIdRemisionSap((Long) resultado[0]);
                } else if (resultado[0] instanceof Number) {
                    entidad.setIdRemisionSap(((Number) resultado[0]).longValue());
                } else {
                    entidad.setIdRemisionSap(Long.parseLong(resultado[0].toString()));
                }
            } else {
                entidad.setIdRemisionSap(0L);
            }
            
            entidad.setDelivery((String) resultado[1]); // delivery
            entidad.setDocumentDate((String) resultado[2]); // document_date
            entidad.setWayBill((String) resultado[3]); // way_bill
            entidad.setLogisticCenter((String) resultado[4]); // logistic_center
            entidad.setSupplyingCenter((String) resultado[5]); // supplying_center
            entidad.setStatus((String) resultado[6]); // status
            entidad.setModificationDate(resultado[7] != null ? (String) resultado[7] : " "); // modification_date
            entidad.setModificationHour(resultado[8] != null ? (String) resultado[8] : " "); // modification_hour
            entidad.setFrontierLaw((String) resultado[9]); // frontier_law
            
            // Campos adicionales de timestamp
            if (resultado.length > 10 && resultado[10] != null) {
                Date creationDate = (Date) resultado[10];
                entidad.setCreationDate(creationDate.toLocalDate());
            }
            
            if (resultado.length > 11 && resultado[11] != null) {
                Time creationHour = (Time) resultado[11];
                entidad.setCreationHour(creationHour.toLocalTime());
            }
            
            // Manejo seguro del casting para id_estado
            if (resultado.length > 12 && resultado[12] != null) {
                if (resultado[12] instanceof BigInteger) {
                    entidad.setIdEstado(((BigInteger) resultado[12]).intValue());
                } else if (resultado[12] instanceof Integer) {
                    entidad.setIdEstado((Integer) resultado[12]);
                } else if (resultado[12] instanceof Number) {
                    entidad.setIdEstado(((Number) resultado[12]).intValue());
                } else {
                    entidad.setIdEstado(Integer.parseInt(resultado[12].toString()));
                }
            } else {
                entidad.setIdEstado(0);
            }
            
        } catch (Exception ex) {
            System.err.println("❌ Error al mapear resultado a RemisionSapEntity: " + ex.getMessage());
            throw new RuntimeException("Error en mapeo de datos de remisión", ex);
        }
        
        return entidad;
    }
    
    /**
     * 🎯 Método de conveniencia para verificar si existe una remisión
     * 
     * @param delivery número de delivery
     * @return true si existe la remisión, false en caso contrario
     */
    public boolean existeRemision(String delivery) {
        RemisionSapEntity remision = obtenerInfoEntradaRemision(delivery);
        return remision != null;
    }
    
    /**
     * 🔍 Obtiene el ID de remisión por delivery
     * 
     * @param delivery número de delivery
     * @return ID de remisión o null si no existe
     */
    public Long obtenerIdRemisionPorDelivery(String delivery) {
        RemisionSapEntity remision = obtenerInfoEntradaRemision(delivery);
        return remision != null ? remision.getIdRemisionSap() : null;
    }

    
} 
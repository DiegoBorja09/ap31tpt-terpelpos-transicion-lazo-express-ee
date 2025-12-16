package com.infrastructure.repositories;

import com.application.commons.CtTblTranssacionProcesoEnum;
import com.domain.entities.CtTblTransaccionProcesoEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CtTblTransaccionProcesoRepository implements BaseRepositoryInterface<CtTblTransaccionProcesoEntity> {

    private final EntityManager entityManager;

    public CtTblTransaccionProcesoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtTblTransaccionProcesoEntity save(CtTblTransaccionProcesoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving transaccion proceso", e);
        }
    }

    @Override
    public CtTblTransaccionProcesoEntity update(CtTblTransaccionProcesoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating transaccion proceso", e);
        }
    }

    @Override
    public void delete(CtTblTransaccionProcesoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaccion proceso", e);
        }
    }

    @Override
    public Optional<CtTblTransaccionProcesoEntity> findById(Object id) {
        try {
            CtTblTransaccionProcesoEntity entity = entityManager.find(CtTblTransaccionProcesoEntity.class, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error finding transaccion proceso by id", e);
        }
    }

    @Override
    public List<CtTblTransaccionProcesoEntity> findAll() {
        try {
            TypedQuery<CtTblTransaccionProcesoEntity> query = entityManager.createQuery("SELECT t FROM CtTblTransaccionProcesoEntity t", CtTblTransaccionProcesoEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all transaccion procesos", e);
        }
    }

    @Override
    public List<CtTblTransaccionProcesoEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtTblTransaccionProcesoEntity> query = entityManager.createQuery(jpql, CtTblTransaccionProcesoEntity.class);
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

    // =====================================================================
    // MÉTODOS DINÁMICOS USANDO EL ENUM
    // =====================================================================

    /**
     * Busca un valor específico de un campo usando el enum
     * 
     * @param campo El campo a consultar (del enum)
     * @param idMovimiento ID del movimiento
     * @param idIntegracion ID de la integración
     * @param returnType Tipo de retorno esperado
     * @return Optional con el valor encontrado
     */
    public <T> Optional<T> findFieldByMovimientoAndIntegracion(CtTblTranssacionProcesoEnum campo, 
                                                               Long idMovimiento, 
                                                               Long idIntegracion, 
                                                               Class<T> returnType) {
        try {
            String jpql = String.format("SELECT t.%s FROM CtTblTransaccionProcesoEntity t " +
                                      "WHERE t.idMovimiento = :idMovimiento AND t.idIntegracion = :idIntegracion", 
                                      mapEnumToEntityField(campo));
            
            TypedQuery<T> query = entityManager.createQuery(jpql, returnType);
            query.setParameter("idMovimiento", idMovimiento);
            query.setParameter("idIntegracion", idIntegracion);
            
            return query.getResultStream().findFirst();
            
        } catch (Exception e) {
            throw new RuntimeException("Error finding field " + campo.getCampo() + " for movimiento: " + idMovimiento, e);
        }
    }

    /**
     * Busca registros por cualquier campo del enum
     * 
     * @param campo El campo a filtrar
     * @param valor El valor a buscar
     * @return Lista de entidades que coinciden
     */
    public List<CtTblTransaccionProcesoEntity> findByField(CtTblTranssacionProcesoEnum campo, Object valor) {
        try {
            String jpql = String.format("SELECT t FROM CtTblTransaccionProcesoEntity t WHERE t.%s = :valor", 
                                      mapEnumToEntityField(campo));
            
            TypedQuery<CtTblTransaccionProcesoEntity> query = entityManager.createQuery(jpql, CtTblTransaccionProcesoEntity.class);
            query.setParameter("valor", valor);
            
            return query.getResultList();
            
        } catch (Exception e) {
            throw new RuntimeException("Error finding by field " + campo.getCampo(), e);
        }
    }

    /**
     * Busca con múltiples criterios usando el enum
     * 
     * @param criterios Map de campo -> valor para filtrar
     * @return Lista de entidades que coinciden con todos los criterios
     */
    public List<CtTblTransaccionProcesoEntity> findByMultipleFields(java.util.Map<CtTblTranssacionProcesoEnum, Object> criterios) {
        try {
            StringBuilder jpql = new StringBuilder("SELECT t FROM CtTblTransaccionProcesoEntity t WHERE 1=1");
            
            for (CtTblTranssacionProcesoEnum campo : criterios.keySet()) {
                jpql.append(" AND t.").append(mapEnumToEntityField(campo)).append(" = :").append(campo.name());
            }
            
            TypedQuery<CtTblTransaccionProcesoEntity> query = entityManager.createQuery(jpql.toString(), CtTblTransaccionProcesoEntity.class);
            
            // Establecer parámetros
            for (java.util.Map.Entry<CtTblTranssacionProcesoEnum, Object> entry : criterios.entrySet()) {
                query.setParameter(entry.getKey().name(), entry.getValue());
            }
            
            return query.getResultList();
            
        } catch (Exception e) {
            throw new RuntimeException("Error finding by multiple fields", e);
        }
    }

    // =====================================================================
    // MÉTODOS ESPECÍFICOS DE NEGOCIO
    // =====================================================================

    /**
     * Migración de MovimientosDao.isValidEdit() - VERSIÓN DINÁMICA
     * Usa el enum para consultar el campo REAPERTURA
     * 
     * @param idMovimiento ID del movimiento a validar
     * @return true si reapertura < 1 (es válido para editar), false si no
     */
    public boolean isValidEdit(Long idMovimiento) {
        try {
            Optional<Integer> reapertura = findFieldByMovimientoAndIntegracion(
                CtTblTranssacionProcesoEnum.REAPERTURA, 
                idMovimiento, 
                3L, 
                Integer.class
            );
            
            if (reapertura.isEmpty()) {
                System.out.println("@@@@@@@Reapertura@@@@@@@@: NO EXISTE REGISTRO, NO EDITABLE");
                return false;
            }
            boolean result = reapertura.get() < 1;
            System.out.println("@@@@@@@Reapertura@@@@@@@@: " + result);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error validating edit for movimiento: " + idMovimiento, e);
        }
    }

    /**
     * Método opcional para obtener directamente la entidad por movimiento e integración
     * 
     * @param idMovimiento ID del movimiento
     * @param idIntegracion ID de la integración
     * @return Optional con la entidad encontrada
     */
    public Optional<CtTblTransaccionProcesoEntity> findByMovimientoAndIntegracion(Long idMovimiento, Long idIntegracion) {
        try {
            java.util.Map<CtTblTranssacionProcesoEnum, Object> criterios = new java.util.HashMap<>();
            criterios.put(CtTblTranssacionProcesoEnum.ID_MOVIMIENTO, idMovimiento);
            criterios.put(CtTblTranssacionProcesoEnum.ID_INTEGRACION, idIntegracion);
            
            return findByMultipleFields(criterios).stream().findFirst();
            
        } catch (Exception e) {
            throw new RuntimeException("Error finding transaccion proceso by movimiento and integracion", e);
        }
    }

    // =====================================================================
    // MÉTODO HELPER PARA MAPEAR ENUM A CAMPOS DE ENTIDAD
    // =====================================================================

    /**
     * Mapea los nombres de campos del enum a los nombres de propiedades de la entidad JPA
     * 
     * @param campo Enum del campo
     * @return Nombre de la propiedad en la entidad
     */
    private String mapEnumToEntityField(CtTblTranssacionProcesoEnum campo) {
        switch (campo) {
            case ID_TRANSACCION_PROCESO: return "idTransaccionProceso";
            case SINCRONIZADO: return "sincronizado";
            case ID_INTEGRACION: return "idIntegracion";
            case ID_ESTADO_INTEGRACION: return "idEstadoIntegracion";
            case ID_TIPO_TRANSACCION_PROCESO: return "idTipoTransaccionProceso";
            case ID_ESTADO_PROCESO: return "idEstadoProceso";
            case ID_MOVIMIENTO: return "idMovimiento";
            case REINTENTO_CONFIRMACION: return "reintentoConfirmacion";
            case FECHA_CREACION: return "fechaCreacion";
            case ID_TIPO_NEGOCIO: return "idTipoNegocio";
            case REAPERTURA: return "reapertura";
            case IS_FE: return "isFe";
            case DESCRIPCION: return "descripcion";
            case USUARIO_CREACION: return "usuarioCreacion";
            default: throw new IllegalArgumentException("Campo no mapeado: " + campo);
        }
    }
}

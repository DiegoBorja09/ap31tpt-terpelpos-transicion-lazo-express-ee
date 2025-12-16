package com.infrastructure.repositories;


import com.domain.entities.CtMovimientoEntity;
import com.bean.CategoriaBean;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infrastructure.core.BaseRepositoryInterface;
import com.infrastructure.Enums.SqlQueryEnum;
import com.controllers.NovusConstante;
import com.dao.DAOException;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.util.PSQLException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class CtMovimientoRepository implements BaseRepositoryInterface<CtMovimientoEntity> {
    private final EntityManager entityManager;

    public CtMovimientoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtMovimientoEntity save(CtMovimientoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving movimiento", e);
        }
    }

    @Override
    public CtMovimientoEntity update(CtMovimientoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating movimiento", e);
        }
    }

    @Override
    public void delete(CtMovimientoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting movimiento", e);
        }
    }

    @Override
    public Optional<CtMovimientoEntity> findById(Object id) {
        try {
            CtMovimientoEntity movimiento = entityManager.find(CtMovimientoEntity.class, id);
            return Optional.ofNullable(movimiento);
        } catch (Exception e) {
            throw new RuntimeException("Error finding movimiento by id", e);
        }
    }

    @Override
    public List<CtMovimientoEntity> findAll() {
        try {
            TypedQuery<CtMovimientoEntity> query = entityManager.createQuery("SELECT m FROM CtMovimientoEntity m", CtMovimientoEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all movimientos", e);
        }
    }

    @Override
    public List<CtMovimientoEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<CtMovimientoEntity> query = entityManager.createQuery(jpql, CtMovimientoEntity.class);
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
            
            // Asignar parámetros posicionales (?, ?, ?)
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]); // JPA usa índices 1-based
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query: " + sql, e);
        }
    }
    
    /**
     * Actualiza el estado_movimiento de un movimiento específico
     * Reemplaza: destruirVentaDespachadaCombustible del DAO
     * @param idVenta ID del movimiento
     * @param nuevoEstado Nuevo estado a asignar
     * @return true si se actualizó correctamente
     */
    public boolean updateEstadoMovimiento(Long idVenta, String nuevoEstado) {
        try {
            entityManager.getTransaction().begin();
            
            int filasActualizadas = entityManager.createQuery(
                "UPDATE CtMovimientoEntity m SET m.estadoMovimiento = :estado WHERE m.id = :id")
                .setParameter("estado", nuevoEstado)
                .setParameter("id", idVenta)
                .executeUpdate();
            
            entityManager.getTransaction().commit();
            return filasActualizadas > 0;
            
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating estado_movimiento", e);
        }
    }
    public JsonObject findClienteTransmisionById(Long idMovimiento) {
        try {
            // ✅ Asegurar que hay una transacción activa
            boolean wasActive = entityManager.getTransaction().isActive();
            if (!wasActive) {
                entityManager.getTransaction().begin();
            }
            
            String sql = SqlQueryEnum.CONSULTAR_CLIENTE_TRANSMISION.getQuery();
            Object result = entityManager.createNativeQuery(sql)
                    .setParameter(1, idMovimiento)  // ✅ Usar parámetro posicional
                    .getSingleResult();
            
            // ✅ Commit si iniciamos la transacción
            if (!wasActive) {
                entityManager.getTransaction().commit();
            }

            if (result != null) {
                return new JsonParser().parse(result.toString()).getAsJsonObject();
            }
            return new JsonObject();
        } catch (Exception e) {
            // ✅ Rollback si hay error y iniciamos la transacción
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al consultar cliente por transmisión - " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si un movimiento tiene pago GoPass
     * Reemplaza: MovimientosDao.isPagoGopass()
     * @param idMovimiento ID del movimiento a verificar
     * @return true si tiene pago GoPass, false en caso contrario
     */
    public boolean isPagoGopass(Long idMovimiento) {
        try {
            String sql = SqlQueryEnum.IS_PAGO_GOPASS.getQuery();
            Object result = entityManager.createNativeQuery(sql)
                    .setParameter(1, idMovimiento)
                    .getSingleResult();
            
            return result != null;
        } catch (Exception e) {
            // Si no encuentra registros o hay error, retorna false
            return false;
        }
    }
    public long obtenerConsecutivoDesdeMovimiento(long numeroMovimiento) {
        long consecutivo = 0;
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_CONSECUTIVO_DESDE_MOVIMIENTO.getQuery());
            query.setParameter(1, numeroMovimiento);
            Object result = query.getSingleResult();
            if (result != null) {
                consecutivo = ((Number) result).longValue();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo consecutivo desde movimiento: " + e.getMessage());
        }
        return consecutivo;
    }
    
    public boolean consecutivoUsado(String prefijo, long consecutivo) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.CONSECUTIVO_USADO.getQuery());
            query.setParameter(1, consecutivo);
            query.setParameter(2, prefijo);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            System.err.println("❌ Error validando consecutivo usado: " + e.getMessage());
            return false;
        }
    }

   
    /**
     * Actualiza movimientos CT usando la función PostgreSQL fnc_actualizar_ct_movimientos
     * Migración JPA del método actualizarMovimientos() de RumboDao
     * @param data JsonObject con los datos para actualizar movimientos
     * @return true si se ejecutó correctamente
     */
    public boolean actualizarCtMovimientos(JsonObject data) {
        try {
            String sql = SqlQueryEnum.ACTUALIZAR_CT_MOVIMIENTOS.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, data.toString());
            
            // Ejecutar la función - puede retornar datos o simplemente ejecutarse
            query.getResultList();
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error actualizando CT movimientos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado de un movimiento a anulado (017002).
     * Migración JPA del método actualizarEstadoMovimiento() de RumboDao.
     * @param movimientoId ID del movimiento a actualizar
     * @return true si se actualizó correctamente
     */
    public boolean actualizarEstadoMovimiento(long movimientoId) {
        try {
            String sql = SqlQueryEnum.ACTUALIZAR_ESTADO_MOVIMIENTO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, movimientoId);
            
            int filasAfectadas = query.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Error actualizando estado del movimiento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta movimientos CT usando la función PostgreSQL fnc_insertar_ct_movimientos
     * Migración JPA del método insertCTmovimientos() de RumboDao
     * @param jsonMovimientos JsonObject con datos de movimientos
     * @param jsonMovimientosCredito JsonObject con datos de movimientos crédito
     * @param jsonMovimientoDetalles JsonObject con datos de detalles de movimiento
     * @param jsonMovimientoMediosPago JsonObject con datos de medios de pago
     * @param request JsonObject con datos de request
     * @param response JsonObject con datos de response
     * @param identificacionAutorizacion String con identificación de autorización
     * @return true si se insertó correctamente
     */
    public boolean insertarCtMovimientos(JsonObject jsonMovimientos,
                                       JsonObject jsonMovimientosCredito,
                                       JsonObject jsonMovimientoDetalles,
                                       JsonObject jsonMovimientoMediosPago,
                                       JsonObject request,
                                       JsonObject response,
                                       String identificacionAutorizacion) {
        try {
            String sql = SqlQueryEnum.INSERTAR_CT_MOVIMIENTOS.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            
            // Asignar parámetros en orden
            query.setParameter(1, jsonMovimientos.toString());
            query.setParameter(2, jsonMovimientosCredito.toString());
            query.setParameter(3, jsonMovimientoDetalles.toString());
            query.setParameter(4, jsonMovimientoMediosPago.toString());
            query.setParameter(5, request.toString());
            query.setParameter(6, response.toString());
            query.setParameter(7, identificacionAutorizacion);
            
            // Ejecutar y obtener resultado
            Object result = query.getSingleResult();
            return result != null && (Boolean) result;
            
        } catch (Exception e) {
            System.err.println("❌ Error insertando CT movimientos: " + e.getMessage());
            return false;
        }
    }

}


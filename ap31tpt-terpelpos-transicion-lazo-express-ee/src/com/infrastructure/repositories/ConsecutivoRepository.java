package com.infrastructure.repositories;

import com.domain.entities.ConsecutivoEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;
import com.google.gson.JsonObject;
import com.firefuel.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ConsecutivoRepository implements BaseRepositoryInterface<ConsecutivoEntity> {

    private final EntityManager entityManager;

    public ConsecutivoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ConsecutivoEntity save(ConsecutivoEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        return entity;
    }

    @Override
    public ConsecutivoEntity update(ConsecutivoEntity entity) {
        entityManager.getTransaction().begin();
        ConsecutivoEntity merged = entityManager.merge(entity);
        entityManager.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(ConsecutivoEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<ConsecutivoEntity> findById(Object id) {
        ConsecutivoEntity entity = entityManager.find(ConsecutivoEntity.class, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<ConsecutivoEntity> findAll() {
        TypedQuery<ConsecutivoEntity> query = entityManager.createQuery("SELECT c FROM ConsecutivoEntity c", ConsecutivoEntity.class);
        return query.getResultList();
    }

    @Override
    public List<ConsecutivoEntity> findByQuery(String jpql, Object... parameters) {
        TypedQuery<ConsecutivoEntity> query = entityManager.createQuery(jpql, ConsecutivoEntity.class);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        Query query = entityManager.createNativeQuery(sql, ConsecutivoEntity.class);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }

    public boolean actualizarConsecutivo(long id, long consecutivo) {
        try {
            entityManager.getTransaction().begin();
            Query query = entityManager.createNativeQuery(SqlQueryEnum.ACTUALIZAR_CONSECUTIVO.getQuery());
            query.setParameter(1, consecutivo);
            query.setParameter(2, id);
            int updated = query.executeUpdate();
            entityManager.getTransaction().commit();
            return updated == 1;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("❌ Error actualizando consecutivo: " + e.getMessage());
            return false;
        }
    }
    public String obtenerObservaciones(Long id) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.GET_OBSERVACIONES_CONSECUTIVO.getQuery());
            query.setParameter(1, id);
            Object result = query.getSingleResult();
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            System.err.println("⚠️ Error al obtener observaciones: " + e.getMessage());
            return "";
        }
    }

    public JsonObject obtenerAlertasResolucion(int tipoDocumento, String destino) {
        JsonObject info = new JsonObject();
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_ALERTAS_RESOLUCION.getQuery());
            query.setParameter(1, tipoDocumento);
            query.setParameter(2, destino);
            query.setParameter(3, com.firefuel.Main.credencial.getEquipos_id());
            
            // 🔧 FIX: Usar getResultList() en lugar de getSingleResult() para evitar NoResultException
            List<?> resultList = query.getResultList();
            
            if (!resultList.isEmpty()) {
                Object[] result = (Object[]) resultList.get(0);
                if (result != null && result.length >= 4) {
                    // 🔧 FIX: Usar casting seguro como el DAO original
                    info.addProperty("rangoConsecutivos", safeInt(result[0]));
                    info.addProperty("rangoFecha", safeInt(result[1]));
                    info.addProperty("alertaDias", safeInt(result[2]));
                    info.addProperty("alertaConsecutivos", safeInt(result[3]));
                }
            } else {
                System.out.println("ℹ️ No se encontraron registros de consecutivos para tipoDocumento=" + tipoDocumento + " y destino=" + destino);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo alertas de resolución: " + e.getMessage());
            e.printStackTrace(); // Para debug detallado
        }
        return info;
    }

    // Método de utilidad para casting seguro
    private int safeInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 📊 Obtener consecutivo con cálculo de días para getConsecutivo()
     * @param tipoDocumento 31 (FE) o 9 (POS)  
     * @param destino "CAN"
     * @return Lista de Object[] con todos los campos + dias calculados
     */
    public List<Object[]> obtenerConsecutivo(int tipoDocumento, String destino) {
        try {
            java.time.LocalDate todaysDate = java.time.LocalDate.now();
            
            // 🔧 FIX: Usar consulta con fecha literal (igual que DAO original)
            String sql = String.format(
                SqlQueryEnum.OBTENER_CONSECUTIVO_CON_FECHA_LITERAL.getQuery(), 
                todaysDate.toString()
            );
            
            System.out.println("🔍 SQL generado (igual que DAO original): " + sql);
            
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, tipoDocumento);
            query.setParameter(2, destino);
            
            @SuppressWarnings("unchecked")
            List<Object[]> resultList = query.getResultList();
            return resultList;
            
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo consecutivo: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 🔄 Actualizar estado de consecutivo (A/U/V)
     * @param consecutivoId ID del consecutivo
     * @param nuevoEstado 'A' (Activo), 'U' (En Uso), 'V' (Vencido)
     * @return true si se actualizó correctamente
     */
    public boolean actualizarEstadoConsecutivo(long consecutivoId, String nuevoEstado) {
        try {
            entityManager.getTransaction().begin();
            Query query = entityManager.createNativeQuery(SqlQueryEnum.ACTUALIZAR_ESTADO_CONSECUTIVO.getQuery());
            query.setParameter(1, nuevoEstado);
            query.setParameter(2, consecutivoId);
            
            int filasAfectadas = query.executeUpdate();
            entityManager.getTransaction().commit();
            
            System.out.println("✅ Estado consecutivo actualizado: ID=" + consecutivoId + " -> Estado=" + nuevoEstado);
            return filasAfectadas == 1;
            
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("⚠️ Error actualizando estado consecutivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}


package com.infrastructure.repositories;

import com.domain.entities.TransmisionRemisionEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class TransmisionRemisionRepository {

    private final EntityManager entityManager;

    public TransmisionRemisionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JsonArray buscarTransmisionRemision(int sincronizado, String queryExtra) {
        JsonArray data = new JsonArray();
        String sql = SqlQueryEnum.BUSCAR_TRANSMISION_REMISION.getQuery() + " " + queryExtra;

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, sincronizado);  // ✅ Usar parámetro posicional

            List<Object[]> resultados = query.getResultList();

            for (Object[] row : resultados) {
                String requestJson = (String) row[1];
                Long id = ((Number) row[0]).longValue();

                Gson gson = new Gson();
                JsonObject json = gson.fromJson(requestJson, JsonObject.class);
                json.addProperty("id_transmision", id);

                data.add(json);
            }

        } catch (Exception e) {
            System.err.println("Error al consultar transmisiones_remision: " + e.getMessage());
        }

        return data;
    }
    public Long obtenerIdRemisionDesdeMovimiento(long idMovimiento) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_ID_REMISION_DESDE_MOVIMIENTO.getQuery());
            query.setParameter(1, idMovimiento);  // ✅ Usar parámetro posicional

            Object result = query.getSingleResult();
            return result != null ? ((Number) result).longValue() : null;
        } catch (Exception e) {
            System.err.println("Error al obtener ID de remisión: " + e.getMessage());
            return null;
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

}



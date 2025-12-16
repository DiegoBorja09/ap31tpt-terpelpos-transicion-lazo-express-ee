package com.infrastructure.repositories;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ParametrosRepository {

    private final EntityManager entityManager;

    public ParametrosRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public JsonObject obtenerMensajesComprobante() {
        try {
            String sql = SqlQueryEnum.MENSAJES_COMPROBANTE.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            Object result = query.getSingleResult();

            if (result != null) {
                Gson gson = new Gson();
                return gson.fromJson(result.toString(), JsonObject.class);
            }
        } catch (Exception e) {
            System.err.println("Error al consultar mensajes del comprobante: " + e.getMessage());
        }

        return new JsonObject();
    }

}


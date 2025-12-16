package com.infrastructure.repositories;

import com.google.gson.JsonObject;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RemisionRepository {

    private final EntityManager entityManager;

    public RemisionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public float obtenerMontoMinimoRemision() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.MONTO_MINIMO_REMISION.getQuery());
            Object result = query.getSingleResult();
            return result != null ? Float.parseFloat(result.toString()) : -1.0f;
        } catch (Exception e) {
            System.err.println("Error al consultar MONTO_MINIMO_REMISION: " + e.getMessage());
            return -1.0f;
        }
    }

    public JsonObject obtenerObligatoriedadRemision() {
        JsonObject json = new JsonObject();
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBLIGATORIEDAD_REMISION.getQuery());
            Object result = query.getSingleResult();

            float monto = obtenerMontoMinimoRemision();
            json.addProperty("OBLIGATORIO_FE", result != null ? result.toString() : "");
            json.addProperty("monto_minimo", monto);
            json.addProperty("error", monto == -1.0f);
        } catch (Exception e) {
            System.err.println("Error al consultar OBLIGATORIEDAD_REMISION: " + e.getMessage());
            json.addProperty("error", true);
        }

        return json;
    }
}

package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class UnidadRepository {

    private final EntityManager entityManager;

    public UnidadRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String obtenerDescripcionPorId(Long unidadId) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_UNIDAD_DESCRIPCION.getQuery());
            query.setParameter(1, unidadId);  // ✅ Usar parámetro posicional
            Object result = query.getSingleResult();
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            System.err.println("Error al consultar descripción de unidad: " + e.getMessage());
            return "";
        }
    }
}

package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PosRepository {

    private final EntityManager entityManager;

    public PosRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public int obtenerNumeroPos() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.NUMERO_POS.getQuery());
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) {
            System.err.println("Error al obtener el número POS: " + e.getMessage());
            return 0;
        }
    }
}

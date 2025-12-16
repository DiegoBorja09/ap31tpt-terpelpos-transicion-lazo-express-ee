package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.TreeMap;

public class TanquesRepository {
    private final EntityManager entityManager;

    public TanquesRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TreeMap<Integer, String> obtenerTanques() {
        TreeMap<Integer, String> tanques = new TreeMap<>();
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.GET_TANQUES.getQuery());
            List<Object[]> results = query.getResultList();
            for (Object[] row : results) {
                tanques.put(((Number) row[0]).intValue(), (String) row[1]);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener tanques: " + e.getMessage());
        }
        return tanques;
    }
}

// MovimientoRepository.java
package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.TreeMap;

public class TiposMovimientoRepository {

    private final EntityManager entityManager;

    public TiposMovimientoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TreeMap<Integer, String> obtenerTiposMovimiento() {
        TreeMap<Integer, String> resultado = new TreeMap<>();
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.GET_TIPOS_MOVIMIENTO.getQuery());
            List<Object[]> filas = query.getResultList();

            for (Object[] fila : filas) {
                Integer id = ((Number) fila[0]).intValue();
                String descripcion = (String) fila[1];
                resultado.put(id, descripcion);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener tipos de movimiento: " + e.getMessage());
        }
        return resultado;
    }
}

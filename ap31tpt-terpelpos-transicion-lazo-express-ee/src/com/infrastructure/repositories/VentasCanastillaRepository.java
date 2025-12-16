package com.infrastructure.repositories;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import org.postgresql.util.PGobject;

public class VentasCanastillaRepository {

    private final EntityManager entityManager;

    public VentasCanastillaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Busca las ventas de canastilla en un rango de fechas y opcionalmente filtradas por promotor
     * @param fechaInicial Fecha inicial del rango
     * @param fechaFinal Fecha final del rango
     * @param promotor ID del promotor (opcional)
     * @return Lista de resultados de la función de base de datos
     */
    @SuppressWarnings("unchecked")
    public List<PGobject> findVentasCanastilla(String fechaInicial, String fechaFinal, String promotor) {
        String sql = "SELECT * FROM public.fnc_consultar_ventas_canastilla(?, ?, ?) resultado";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, fechaInicial);
        query.setParameter(2, fechaFinal);
        query.setParameter(3, promotor);
        
        List<PGobject> result = (List<PGobject>) query.getResultList();
        return result;
    }
} 
package com.infrastructure.repositories;

import com.bean.TransaccionGopass;
import com.infrastructure.core.BaseRepositoryInterface;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.persistence.NoResultException;

public class GoPassRepository implements BaseRepositoryInterface<TransaccionGopass> {

    private final EntityManager entityManager;
    private static final Logger LOGGER = Logger.getLogger(GoPassRepository.class.getName());

    public GoPassRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Obtiene las transacciones de GoPass según los días especificados
     * @param dias Número de días hacia atrás para obtener las transacciones
     * @return Lista de transacciones GoPass
     */
    public ArrayList<TransaccionGopass> getTransaccionesGoPass(int dias) {
        ArrayList<TransaccionGopass> transacciones = new ArrayList<>();
        try {
            List<Object[]> results = entityManager.createNativeQuery(SqlQueryEnum.GET_TRANSACIONES_GOPASS.getQuery())
                    .setParameter(1, dias)
                    .getResultList();

            LOGGER.info("Número de resultados obtenidos: " + results.size());

            for (Object[] row : results) {
                try {
                    TransaccionGopass e = new TransaccionGopass();
                    
                    // Log detallado de cada columna
                    LOGGER.info("Procesando fila con valores:");
                    for (int i = 0; i < row.length; i++) {
                        LOGGER.info("Columna " + i + ": " + (row[i] != null ? row[i].toString() : "null") + 
                                  " (Tipo: " + (row[i] != null ? row[i].getClass().getName() : "null") + ")");
                    }

                    // Identificador de transacción (viene como String)
                    e.setIdentificadortransacciongopass(row[0] != null ? Long.parseLong(row[0].toString()) : 0L);
                    
                    // Isla (viene como Integer)
                    e.setIsla(row[1] != null ? ((Number) row[1]).intValue() : 0);
                    
                    // Código EDS (viene como String)
                    e.setCodigoeds(row[2] != null ? row[2].toString() : null);
                    
                    // Surtidor y Cara (vienen como Integer)
                    e.setSurtidor(row[3] != null ? ((Number) row[3]).intValue() : 0);
                    e.setCara(row[4] != null ? ((Number) row[4]).intValue() : 0);

                    // Placa (viene como String)
                    e.setPlaca(row[6] != null ? row[6].toString() : null);

                    // ID Venta/Referencia de cobro (viene como BigDecimal)
                    if (row[7] instanceof BigDecimal) {
                        BigDecimal idVenta = (BigDecimal) row[7];
                        e.setIdentificadorventaterpel(idVenta.longValue());
                    }

                    // Estado (viene como String en columna 8)
                    e.setEstado(row[8] != null ? row[8].toString() : null);

                    // Fecha (viene como Timestamp)
                    e.setFecha(row[9] != null ? row[9].toString() : null);

                    // ID Movimiento (viene como BigDecimal)
                    if (row[10] instanceof BigDecimal) {
                        BigDecimal idMovimiento = (BigDecimal) row[10];
                        e.setIdentificadorMovimiento(idMovimiento.longValue());
                    }

                    LOGGER.info("Transacción mapeada exitosamente: " + e.getIdentificadortransacciongopass());
                    transacciones.add(e);

                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error mapeando transacción", ex);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo transacciones", e);
        }
        return transacciones;
    }

    @Override
    public Optional<TransaccionGopass> findById(Object id) {
        return Optional.empty();
    }

    @Override
    public List<TransaccionGopass> findAll() {
        return null;
    }

    @Override
    public TransaccionGopass save(TransaccionGopass entity) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public TransaccionGopass update(TransaccionGopass entity) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void delete(TransaccionGopass entity) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public List<TransaccionGopass> findByQuery(String jpql, Object... parameters) {
        throw new UnsupportedOperationException("Operación no soportada");
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        throw new UnsupportedOperationException("Operación no soportada");
    }
} 
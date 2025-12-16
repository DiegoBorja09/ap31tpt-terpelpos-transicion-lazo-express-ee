package com.application.useCases.movimientosdetalles;

import com.application.core.BaseUseCases;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.infrastructure.repositories.CtMovimeintoDetallesRepository;

/**
 * Caso de uso para obtener el ID del producto desde un movimiento específico
 * Reemplaza: MovimientosDao.obtenerIdProductoDesdeMovimiento()
 * Usado en: VentasHistorialView.buildSales()
 */
public class GetByProductoMovimientoDetalleUseCase implements BaseUseCases<Long> {

    private static final Logger LOGGER = Logger.getLogger(GetByProductoMovimientoDetalleUseCase.class.getName());
    private final EntityManagerFactory entityManagerFactory;
    private final Long movimientoId;

    public GetByProductoMovimientoDetalleUseCase(Long movimientoId) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.movimientoId = movimientoId;
    }

    /**
     * Ejecuta la consulta para obtener el productos_id desde ct_movimientos_detalles
     * @return productos_id encontrado o 0L si no existe
     */
    @Override
    public Long execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            CtMovimeintoDetallesRepository repository = new CtMovimeintoDetallesRepository(entityManager);
            Long productoId = repository.obtenerIdProductoDesdeMovimiento(movimientoId);
            
            LOGGER.log(Level.INFO, "✅ ID producto recuperado desde movimiento_id {0}: {1}", 
                      new Object[]{movimientoId, productoId});
            
            return productoId;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al obtener el ID de producto desde ct_movimientos_detalles para movimiento: " + movimientoId, e);
            return 0L;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Método estático de conveniencia para uso directo
     * @param movimientoId ID del movimiento
     * @return productos_id encontrado o 0L si no existe
     */
    public static Long obtenerProductoId(Long movimientoId) {
        return new GetByProductoMovimientoDetalleUseCase(movimientoId).execute();
    }
}
package com.application.useCases.gopass;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Caso de uso para verificar si un movimiento tiene pago GoPass
 * Reemplaza: MovimientosDao.isPagoGopass()
 * Usado en: VentasHistorialView.buildSales()
 */
public class GetIsGoPassMovimientoUseCase implements BaseUseCases<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(GetIsGoPassMovimientoUseCase.class.getName());
    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;

    public GetIsGoPassMovimientoUseCase(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CtMovimientoRepository repository = new CtMovimientoRepository(entityManager);
            boolean isPagoGopass = repository.isPagoGopass(idMovimiento);
            
            LOGGER.log(Level.INFO, "✅ Verificación GoPass para movimiento {0}: {1}", 
                      new Object[]{idMovimiento, isPagoGopass});
            
            return isPagoGopass;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error verificando pago GoPass para movimiento: " + idMovimiento, e);
            return false; // Valor por defecto en caso de error
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Método estático de conveniencia para verificar si un movimiento tiene pago GoPass
     * @param idMovimiento ID del movimiento a verificar
     * @return true si el movimiento tiene pago GoPass, false en caso contrario
     */
    public static boolean isPagoGopass(Long idMovimiento) {
        GetIsGoPassMovimientoUseCase useCase = new GetIsGoPassMovimientoUseCase(idMovimiento);
        return useCase.execute();
    }
}

package com.application.useCases.datafonos;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.firefuel.datafonos.EstadosTransaccionVenta;
import com.firefuel.datafonos.TransaccionOperacion;
import com.infrastructure.repositories.DatafonoTransaccionRepository;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;

/*
 * Migración JPA del método DatafonosDao.hayAnulacionesEncursoEnGeneral()
 * Verifica si hay anulaciones pendientes para un movimiento específico
 * 
 * Original SQL: 
 * SELECT * FROM datafonos.transacciones t 
 * WHERE t.id_movimiento = ? 
 * AND t.id_transaccion_operacion = ? 
 * AND t.id_transaccion_estado = ?
 */
public class HayAnulacionesPendientesUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;

    public HayAnulacionesPendientesUseCase(Long idMovimiento) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.idMovimiento = idMovimiento;
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Usar el repositorio que ya tiene el método
            DatafonoTransaccionRepository repository = new DatafonoTransaccionRepository(entityManager);
            
            Short operacionAnulacion = (short) TransaccionOperacion.ANULACION.getEstadoTransaccionOperacion();
            Short estadoPendiente = (short) EstadosTransaccionVenta.PENDIENTE_ANULACION.getEstadoVentaTransaccion();
            
            boolean hayAnulaciones = repository.hayAnulacionesEncursoEnGeneral(
                idMovimiento, 
                operacionAnulacion, 
                estadoPendiente
            );
            
            if (hayAnulaciones) {
                System.out.println("🔍 Anulación pendiente encontrada para movimiento: " + idMovimiento);
            }
            
            return hayAnulaciones;

        } catch (Exception e) {
            NovusUtils.printLn("❌ Error verificando anulaciones pendientes para movimiento " + idMovimiento + ": " + e.getMessage());
            return false;
        } finally {
            entityManager.close();
        }
    }
}

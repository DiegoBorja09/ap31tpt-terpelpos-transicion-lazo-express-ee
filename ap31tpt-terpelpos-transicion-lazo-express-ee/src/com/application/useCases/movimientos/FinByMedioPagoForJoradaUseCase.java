package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.repositories.CtMovimientoRepository;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.controllers.NovusUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use Case para validar si se puede editar el medio de pago de un movimiento
 * basándose en el estado del turno/jornada actual.
 * 
 * Migrado desde: MovimientosDao.validarTurnoMedioPago()
 */
public class FinByMedioPagoForJoradaUseCase implements BaseUseCases<Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;
    
    public FinByMedioPagoForJoradaUseCase(Long idMovimiento) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.idMovimiento = idMovimiento;
    }
    
    /**
     * Implementación requerida por BaseUseCases
     * Valida si un movimiento puede tener sus medios de pago editados
     * verificando si está asociado a una jornada activa.
     */
    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            NovusUtils.printLn("🔍 ValidarTurnoMedioPago: Validando movimiento ID " + idMovimiento);
            
            CtMovimientoRepository ctMovimientoRepository = new CtMovimientoRepository(entityManager);
            
            List<?> result = ctMovimientoRepository.findByNativeQuery(
                SqlQueryEnum.VALIDAR_TURNO_MEDIO_PAGO.getQuery(),
                idMovimiento
            );
            
            boolean puedeEditar = !result.isEmpty();
            
            NovusUtils.printLn("📊 Resultado validación: " + (puedeEditar ? "✅ PUEDE editar" : "❌ NO PUEDE editar"));
            
            return puedeEditar;
            
        } catch (Exception e) {
            Logger.getLogger(FinByMedioPagoForJoradaUseCase.class.getName())
                  .log(Level.SEVERE, "❌ Error validando turno medio pago para movimiento: " + idMovimiento, e);
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    /**
     * Método estático de conveniencia para uso directo
     */
    public static boolean validarTurnoMedioPago(Long idMovimiento) {
        return new FinByMedioPagoForJoradaUseCase(idMovimiento).execute();
    }
}

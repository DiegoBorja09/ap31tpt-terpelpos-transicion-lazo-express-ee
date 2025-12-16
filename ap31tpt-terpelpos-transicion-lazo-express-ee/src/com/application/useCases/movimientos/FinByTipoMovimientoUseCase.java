package com.application.useCases.movimientos;

import com.application.commons.CtMovimientosEnum;
import com.application.core.BaseUseCases;
import com.domain.entities.CtMovimientoEntity;
import com.infrastructure.repositories.CtMovimientoRepository;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Caso de uso dinámico para consultar cualquier campo de movimiento por ID
 * Reemplaza: MovimientosDao.consultarTipoMovimiento(), consultarConsecutivoPorId(), etc.
 * Usado en: ImpresionVenta, VentasHistorialView, VentasRepository
 */
public class FinByTipoMovimientoUseCase implements BaseUseCases<String> {
    
    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;
    private final CtMovimientosEnum campo;
    
    // Constructor para usar con BaseUseCases - campo específico
    public FinByTipoMovimientoUseCase(Long idMovimiento, CtMovimientosEnum campo) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.idMovimiento = idMovimiento;
        this.campo = campo;
    }
    
    // Constructor para tipo (backward compatibility)
    public FinByTipoMovimientoUseCase(Long idMovimiento) {
        this(idMovimiento, CtMovimientosEnum.TIPO);
    }
    
    /**
     * Implementación requerida por BaseUseCases
     * Consulta el campo específico del movimiento usando JPA
     */
    @Override
    public String execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            CtMovimientoRepository ctMovimientoRepository = new CtMovimientoRepository(entityManager);
            return ctMovimientoRepository.findById(idMovimiento)
                    .map(this::extractField)
                    .orElse(null);
        } catch (Exception e) {
            Logger.getLogger(FinByTipoMovimientoUseCase.class.getName())
                  .log(Level.SEVERE, "❌ Error consultando campo " + campo.getColumnName() + " por ID: " + idMovimiento, e);
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    /**
     * Extrae el campo específico de la entidad según el enum
     */
    private String extractField(CtMovimientoEntity movimiento) {
        switch (campo) {
            case TIPO:
                return movimiento.getTipo();
            case CONSECUTIVO:
                return movimiento.getConsecutivo();
            case ESTADO_MOVIMIENTO:
                return movimiento.getEstadoMovimiento();
            case PREFIJO:
                return movimiento.getPrefijo();
            default:
                return null;
        }
    }
    
    // 🎯 Métodos de conveniencia estáticos para uso directo
    
    /**
     * Consulta el consecutivo del movimiento
     */
    public static String consultarConsecutivo(long idMovimiento) {
        return new FinByTipoMovimientoUseCase(idMovimiento, CtMovimientosEnum.CONSECUTIVO).execute();
    }
    
    /**
     * Consulta el tipo del movimiento
     */
    public static String consultarTipo(long idMovimiento) {
        return new FinByTipoMovimientoUseCase(idMovimiento, CtMovimientosEnum.TIPO).execute();
    }
    
    /**
     * Consulta el estado del movimiento
     */
    public static String consultarEstado(long idMovimiento) {
        return new FinByTipoMovimientoUseCase(idMovimiento, CtMovimientosEnum.ESTADO_MOVIMIENTO).execute();
    }
    
    /**
     * Consulta el prefijo del movimiento
     */
    public static String consultarPrefijo(long idMovimiento) {
        return new FinByTipoMovimientoUseCase(idMovimiento, CtMovimientosEnum.PREFIJO).execute();
    }
} 
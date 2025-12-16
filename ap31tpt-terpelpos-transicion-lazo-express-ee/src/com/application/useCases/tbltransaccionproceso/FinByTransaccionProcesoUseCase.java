package com.application.useCases.tbltransaccionproceso;

import com.application.commons.CtTblTranssacionProcesoEnum;
import com.application.core.BaseUseCases;
import com.domain.entities.CtTblTransaccionProcesoEntity;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtTblTransaccionProcesoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Caso de uso para operaciones con TransaccionProceso
 * Migración de funcionalidades del MovimientosDao relacionadas con tbl_transaccion_proceso
 */
public class FinByTransaccionProcesoUseCase implements BaseUseCases<Object> {

    private final EntityManagerFactory entityManagerFactory;
    private final OperationType operationType;
    private final Map<String, Object> parameters;

    public enum OperationType {
        FIND_FIELD_BY_MOVIMIENTO_INTEGRACION,
        FIND_BY_FIELD,
        FIND_BY_MULTIPLE_FIELDS,
        FIND_BY_MOVIMIENTO_INTEGRACION,
        IS_VALID_EDIT
    }

    public FinByTransaccionProcesoUseCase(OperationType operationType, Map<String, Object> parameters) {
        this.operationType = operationType;
        this.parameters = parameters;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Object execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CtTblTransaccionProcesoRepository repository = new CtTblTransaccionProcesoRepository(entityManager);
            
            switch (operationType) {
                case FIND_FIELD_BY_MOVIMIENTO_INTEGRACION:
                    return handleFindFieldByMovimientoIntegracion(repository);
                    
                case FIND_BY_FIELD:
                    return handleFindByField(repository);
                    
                case FIND_BY_MULTIPLE_FIELDS:
                    return handleFindByMultipleFields(repository);
                    
                case FIND_BY_MOVIMIENTO_INTEGRACION:
                    return handleFindByMovimientoIntegracion(repository);
                    
                case IS_VALID_EDIT:
                    return handleIsValidEdit(repository);
                    
                default:
                    throw new IllegalArgumentException("Tipo de operación no soportado: " + operationType);
            }
            
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    // ================================================================
    // MÉTODOS ESTÁTICOS PARA FACILITAR EL USO
    // ================================================================

    /**
     * Busca un campo específico por movimiento e integración
     */
    public static <T> Optional<T> findField(CtTblTranssacionProcesoEnum campo, 
                                           Long idMovimiento, 
                                           Long idIntegracion, 
                                           Class<T> returnType) {
        Map<String, Object> params = Map.of(
            "campo", campo,
            "idMovimiento", idMovimiento,
            "idIntegracion", idIntegracion,
            "returnType", returnType
        );
        
        @SuppressWarnings("unchecked")
        Optional<T> result = (Optional<T>) new FinByTransaccionProcesoUseCase(
            OperationType.FIND_FIELD_BY_MOVIMIENTO_INTEGRACION, params).execute();
        
        return result;
    }

    /**
     * Busca registros por un campo específico
     */
    public static List<CtTblTransaccionProcesoEntity> findByField(CtTblTranssacionProcesoEnum campo, Object valor) {
        Map<String, Object> params = Map.of(
            "campo", campo,
            "valor", valor
        );
        
        @SuppressWarnings("unchecked")
        List<CtTblTransaccionProcesoEntity> result = (List<CtTblTransaccionProcesoEntity>) new FinByTransaccionProcesoUseCase(
            OperationType.FIND_BY_FIELD, params).execute();
        
        return result;
    }

    /**
     * Busca registros por múltiples campos
     */
    public static List<CtTblTransaccionProcesoEntity> findByMultipleFields(Map<CtTblTranssacionProcesoEnum, Object> criterios) {
        Map<String, Object> params = Map.of("criterios", criterios);
        
        @SuppressWarnings("unchecked")
        List<CtTblTransaccionProcesoEntity> result = (List<CtTblTransaccionProcesoEntity>) new FinByTransaccionProcesoUseCase(
            OperationType.FIND_BY_MULTIPLE_FIELDS, params).execute();
        
        return result;
    }

    /**
     * Busca entidad por movimiento e integración
     */
    public static Optional<CtTblTransaccionProcesoEntity> findByMovimientoAndIntegracion(Long idMovimiento, Long idIntegracion) {
        Map<String, Object> params = Map.of(
            "idMovimiento", idMovimiento,
            "idIntegracion", idIntegracion
        );
        
        @SuppressWarnings("unchecked")
        Optional<CtTblTransaccionProcesoEntity> result = (Optional<CtTblTransaccionProcesoEntity>) new FinByTransaccionProcesoUseCase(
            OperationType.FIND_BY_MOVIMIENTO_INTEGRACION, params).execute();
        
        return result;
    }

    /**
     * Valida si un movimiento puede ser editado
     * Migración exacta de MovimientosDao.isValidEdit()
     * 
     * @param idMovimiento ID del movimiento a validar
     * @return true si es válido para editar (reapertura < 1), false si no
     */
    public static boolean isValidEdit(Long idMovimiento) {
        Map<String, Object> params = Map.of("idMovimiento", idMovimiento);
        
        return (Boolean) new FinByTransaccionProcesoUseCase(OperationType.IS_VALID_EDIT, params).execute();
    }

    // ================================================================
    // HANDLERS INTERNOS
    // ================================================================

    @SuppressWarnings("unchecked")
    private Object handleFindFieldByMovimientoIntegracion(CtTblTransaccionProcesoRepository repository) {
        CtTblTranssacionProcesoEnum campo = (CtTblTranssacionProcesoEnum) parameters.get("campo");
        Long idMovimiento = (Long) parameters.get("idMovimiento");
        Long idIntegracion = (Long) parameters.get("idIntegracion");
        Class<?> returnType = (Class<?>) parameters.get("returnType");
        
        return repository.findFieldByMovimientoAndIntegracion(campo, idMovimiento, idIntegracion, returnType);
    }

    private Object handleFindByField(CtTblTransaccionProcesoRepository repository) {
        CtTblTranssacionProcesoEnum campo = (CtTblTranssacionProcesoEnum) parameters.get("campo");
        Object valor = parameters.get("valor");
        
        return repository.findByField(campo, valor);
    }

    @SuppressWarnings("unchecked")
    private Object handleFindByMultipleFields(CtTblTransaccionProcesoRepository repository) {
        Map<CtTblTranssacionProcesoEnum, Object> criterios = 
            (Map<CtTblTranssacionProcesoEnum, Object>) parameters.get("criterios");
        
        return repository.findByMultipleFields(criterios);
    }

    private Object handleFindByMovimientoIntegracion(CtTblTransaccionProcesoRepository repository) {
        Long idMovimiento = (Long) parameters.get("idMovimiento");
        Long idIntegracion = (Long) parameters.get("idIntegracion");
        
        return repository.findByMovimientoAndIntegracion(idMovimiento, idIntegracion);
    }

    private Object handleIsValidEdit(CtTblTransaccionProcesoRepository repository) {
        Long idMovimiento = (Long) parameters.get("idMovimiento");
        
        return repository.isValidEdit(idMovimiento);
    }
} 
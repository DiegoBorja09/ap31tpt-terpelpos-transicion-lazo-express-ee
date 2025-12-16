package com.application.useCases.movimeintomediodepago;

import com.application.core.BaseUseCases;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.repositories.CtMovimientoMedioDePaogoRepository;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use Case para buscar medios de pago con datafonos completados de un movimiento
 * Migrado desde: MovimientosDao.buscarmdiosPagosDatafonosCompletados()
 * Usado en: VentasHistorialView.validarIsPagoDatafonoCompleto()
 */
public class FinByMedioDePagoMovimientoUseCase implements BaseUseCases<JsonArray> {
    
    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;
    
    public FinByMedioDePagoMovimientoUseCase(Long idMovimiento) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.idMovimiento = idMovimiento;
    }
    
    /**
     * Implementación requerida por BaseUseCases
     * Consulta los medios de pago del movimiento usando SQL nativo
     */
    @Override
    public JsonArray execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JsonArray mediosPagos = new JsonArray();
        
        try {
            CtMovimientoMedioDePaogoRepository repository = new CtMovimientoMedioDePaogoRepository(entityManager);
            
            List<?> resultados = repository.findByNativeQuery(
                SqlQueryEnum.BUSCAR_MEDIOS_PAGOS_DATAFONOS_COMPLETADOS.getQuery(), 
                idMovimiento
            );
            
            // Convertir resultados de Object[] a JsonArray
            // SQL: SELECT cmmp.id, cmmp.valor_total, cmmp.ing_pago_datafono, cmp.descripcion, cmp.id as id_medio_pago
            for (Object obj : resultados) {
                Object[] row = (Object[]) obj;
                JsonObject objMediosPagos = new JsonObject();
                
                // Mapeo exacto según el orden del SQL y comportamiento original
                objMediosPagos.addProperty("idMovimientosMediosPagos", 
                    row[0] != null ? ((Number) row[0]).longValue() : 0L);              // cmmp.id
                objMediosPagos.addProperty("idMedioPago", 
                    row[4] != null ? ((Number) row[4]).longValue() : 0L);              // cmp.id as id_medio_pago
                objMediosPagos.addProperty("valorTotal", 
                    row[1] != null ? ((Number) row[1]).floatValue() : 0.0f);           // cmmp.valor_total
                objMediosPagos.addProperty("pagoDatafonoAprobado", 
                    row[2] != null ? (Boolean) row[2] : false);                        // cmmp.ing_pago_datafono
                objMediosPagos.addProperty("descripcion", 
                    row[3] != null ? row[3].toString() : "");                          // cmp.descripcion
                
                mediosPagos.add(objMediosPagos);
            }
            
        } catch (Exception e) {
            Logger.getLogger(FinByMedioDePagoMovimientoUseCase.class.getName())
                  .log(Level.SEVERE, "❌ Error consultando medios de pago para movimiento: " + idMovimiento, e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        
        return mediosPagos;
    }
    
    // 🎯 Método de conveniencia estático para uso directo
    
    /**
     * Busca medios de pago con datafonos completados para un movimiento
     */
    public static JsonArray buscarMediosPagosDatafonosCompletados(long idMovimiento) {
        return new FinByMedioDePagoMovimientoUseCase(idMovimiento).execute();
    }
}

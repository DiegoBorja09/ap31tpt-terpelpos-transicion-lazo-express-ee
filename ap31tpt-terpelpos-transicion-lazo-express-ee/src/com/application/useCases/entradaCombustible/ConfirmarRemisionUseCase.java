package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.bean.entradaCombustible.ConfirmarRemisionParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EntradaCombustibleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * 🚀 MIGRACIÓN: Caso de uso para confirmar remisiones actualizando su estado
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<ConfirmarRemisionParams, Boolean>
 * - Reemplaza EntradaCombustibleDao.confirmarRemision()
 * - Gestiona transacciones automáticamente
 * - Sigue estructura de FindAllCategoriasKIOSCOUseCase
 * 
 * FUNCIONALIDAD:
 * - Actualiza estado de remisión en sap.tbl_remisiones_sap
 * - Transacciones seguras con rollback automático
 * - Validación de parámetros críticos
 * - Logging detallado de operaciones
 * 
 * CASOS DE USO:
 * - Finalizar proceso de descargue (estado = 2)
 * - Cambiar estado de remisiones por flujos de negocio
 * - Confirmar recepción completa de combustible
 * 
 * USO:
 * ```java
 * ConfirmarRemisionUseCase useCase = new ConfirmarRemisionUseCase();
 * ConfirmarRemisionParams params = new ConfirmarRemisionParams(123L, 2);
 * boolean exitoso = useCase.execute(params);
 * ```
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 * @since 2024
 */
public class ConfirmarRemisionUseCase implements BaseUseCasesWithParams<ConfirmarRemisionParams, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    /**
     * 🏗️ Constructor que inicializa el EntityManagerFactory
     * Utiliza la misma base de datos que otras migraciones
     */
    public ConfirmarRemisionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🎯 Método principal que ejecuta la lógica del caso de uso
     * 
     * @param params Parámetros de confirmación (no puede ser null)
     * @return Boolean true si se confirmó exitosamente, false en caso contrario
     * 
     * @throws IllegalArgumentException si params es null
     */
    @Override
    public Boolean execute(ConfirmarRemisionParams params) {
        // 🔍 Validación de entrada
        if (params == null) {
            System.err.println("❌ ConfirmarRemisionUseCase: Parámetros no pueden ser null");
            throw new IllegalArgumentException("Parámetros de confirmación son requeridos");
        }
        
        // 🚀 Logging de inicio
        System.out.println("🔄 ConfirmarRemisionUseCase: Iniciando confirmación de remisión");
        System.out.println("   " + params);
        
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            // 🏛️ Iniciar transacción
            transaction = em.getTransaction();
            transaction.begin();
            
            // 🏛️ Crear repositorio con EntityManager
            EntradaCombustibleRepository repository = new EntradaCombustibleRepository(em);
            
            // 🎯 Ejecutar confirmación principal
            boolean exitoso = repository.confirmarRemision(params);
            
            if (exitoso) {
                // ✅ Confirmar transacción
                transaction.commit();
                System.out.println("✅ ConfirmarRemisionUseCase: Remisión confirmada exitosamente");
                System.out.println("   ID: " + params.getIdRemision() + " → Estado: " + params.getEstado());
            } else {
                // ❌ Rollback si no se afectaron registros
                transaction.rollback();
                System.err.println("⚠️ ConfirmarRemisionUseCase: No se pudo confirmar - Rollback realizado");
                System.err.println("   Posibles causas: ID inexistente, estado ya actualizado, etc.");
            }
            
            return exitoso;
            
        } catch (Exception ex) {
            // 🚨 Manejo de errores críticos con rollback
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                    System.err.println("🔄 ConfirmarRemisionUseCase: Rollback realizado por error");
                } catch (Exception rollbackEx) {
                    System.err.println("💥 Error adicional durante rollback: " + rollbackEx.getMessage());
                }
            }
            
            System.err.println("❌ ConfirmarRemisionUseCase: Error crítico al confirmar remisión");
            System.err.println("   Parámetros: " + params);
            System.err.println("   Error: " + ex.getMessage());
            ex.printStackTrace();
            
            // Retorna false para indicar fallo
            return false;
            
        } finally {
            // 🧹 Limpieza de recursos
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * 🎯 Método de conveniencia para finalizar remisión (estado = 2)
     * 
     * @param idRemision ID de la remisión a finalizar
     * @return true si se finalizó exitosamente, false en caso contrario
     */
    public boolean finalizarRemision(Long idRemision) {
        try {
            ConfirmarRemisionParams params = ConfirmarRemisionParams.finalizar(idRemision);
            boolean exitoso = execute(params);
            
            System.out.println(exitoso 
                ? "✅ Remisión " + idRemision + " FINALIZADA exitosamente"
                : "❌ No se pudo finalizar remisión " + idRemision);
            
            return exitoso;
            
        } catch (Exception ex) {
            System.err.println("❌ Error al finalizar remisión: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * 🔄 Método de conveniencia para cambiar estado específico
     * 
     * @param idRemision ID de la remisión
     * @param nuevoEstado Estado a asignar
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean cambiarEstado(Long idRemision, Integer nuevoEstado) {
        try {
            ConfirmarRemisionParams params = new ConfirmarRemisionParams(idRemision, nuevoEstado);
            boolean exitoso = execute(params);
            
            System.out.println(exitoso 
                ? "✅ Estado de remisión " + idRemision + " cambiado a " + nuevoEstado
                : "❌ No se pudo cambiar estado de remisión " + idRemision);
            
            return exitoso;
            
        } catch (Exception ex) {
            System.err.println("❌ Error al cambiar estado: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * 🎯 Método estático de conveniencia para finalización rápida
     * 
     * @param idRemision ID de la remisión a finalizar
     * @return boolean true si se finalizó, false en caso contrario
     */
    public static boolean finalizarRemisionRapido(Long idRemision) {
        ConfirmarRemisionUseCase useCase = new ConfirmarRemisionUseCase();
        return useCase.finalizarRemision(idRemision);
    }
}
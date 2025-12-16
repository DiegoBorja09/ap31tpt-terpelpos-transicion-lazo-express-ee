package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EntradaCombustibleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 🚀 MIGRACIÓN: Caso de uso para validar si una remisión está confirmada
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseUseCasesWithParams<String, Boolean>
 * - Reemplaza EntradaCombustibleDao.validarSiremisionEstaConfirmada()
 * - Valida si remisión tiene estado = 2 (confirmada)
 * - Sigue estructura de FindAllCategoriasKIOSCOUseCase
 * 
 * FUNCIONALIDAD:
 * - Busca remisión SAP por número de delivery con estado = 2
 * - Retorna true si está confirmada, false en caso contrario
 * - Manejo robusto de errores y logging detallado
 * - Principio de "fail-safe": ante error, asume NO confirmada
 * 
 * CASOS DE USO:
 * - Prevenir procesamiento de remisiones ya confirmadas
 * - Validación de estado antes de operaciones críticas
 * - Control de flujo en procesos de descargue
 * 
 * USO:
 * ```java
 * ValidarSiRemisionEstaConfirmadaUseCase useCase = new ValidarSiRemisionEstaConfirmadaUseCase();
 * boolean estaConfirmada = useCase.execute("DELIVERY12345");
 * ```
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 * @since 2024
 */
public class ValidarSiRemisionEstaConfirmadaUseCase implements BaseUseCasesWithParams<String, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    /**
     * 🏗️ Constructor que inicializa el EntityManagerFactory
     * Utiliza la misma base de datos que otras migraciones
     */
    public ValidarSiRemisionEstaConfirmadaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🎯 Método principal que ejecuta la lógica del caso de uso
     * 
     * @param numeroRemision número de delivery de la remisión (no puede ser null/vacío)
     * @return Boolean true si la remisión está confirmada (estado = 2), false en caso contrario
     * 
     * @throws IllegalArgumentException si numeroRemision es null o vacío
     */
    @Override
    public Boolean execute(String numeroRemision) {
        // 🔍 Validación de entrada
        if (numeroRemision == null || numeroRemision.trim().isEmpty()) {
            System.err.println("❌ ValidarSiRemisionEstaConfirmadaUseCase: Número de remisión no puede ser null o vacío");
            throw new IllegalArgumentException("Número de remisión es requerido");
        }
        
        // 🚀 Logging de inicio
        System.out.println("🔍 ValidarSiRemisionEstaConfirmadaUseCase: Validando estado para delivery: " + numeroRemision);
        
        EntityManager em = entityManagerFactory.createEntityManager();
        
        try {
            // 🏛️ Crear repositorio con EntityManager
            EntradaCombustibleRepository repository = new EntradaCombustibleRepository(em);
            
            // 🎯 Ejecutar validación principal
            boolean estaConfirmada = repository.validarSiRemisionEstaConfirmada(numeroRemision.trim());
            
            // 📊 Logging de resultado
            if (estaConfirmada) {
                System.out.println("🚫 ValidarSiRemisionEstaConfirmadaUseCase: Remisión " + numeroRemision 
                                 + " YA ESTÁ CONFIRMADA - Bloquear procesamiento");
            } else {
                System.out.println("✅ ValidarSiRemisionEstaConfirmadaUseCase: Remisión " + numeroRemision 
                                 + " disponible para procesamiento");
            }
            
            return estaConfirmada;
            
        } catch (Exception ex) {
            // 🚨 Manejo de errores críticos con fail-safe
            System.err.println("❌ ValidarSiRemisionEstaConfirmadaUseCase: Error crítico al validar estado");
            System.err.println("   Delivery: " + numeroRemision);
            System.err.println("   Error: " + ex.getMessage());
            ex.printStackTrace();
            
            // 🛡️ FAIL-SAFE: En caso de error, asume NO confirmada para permitir procesamiento
            System.err.println("🛡️ FAIL-SAFE: Asumiendo remisión NO confirmada para permitir procesamiento");
            return false;
            
        } finally {
            // 🧹 Limpieza de recursos
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * 🎯 Método de conveniencia que invierte la lógica para usar en flujos positivos
     * 
     * @param numeroRemision número de delivery a verificar
     * @return true si la remisión puede procesarse (NO está confirmada), false si ya está confirmada
     */
    public boolean puedeProcesamarse(String numeroRemision) {
        try {
            boolean estaConfirmada = execute(numeroRemision);
            boolean puedeProcesamarse = !estaConfirmada;
            
            System.out.println(puedeProcesamarse 
                ? "✅ Remisión " + numeroRemision + " PUEDE procesarse"
                : "🚫 Remisión " + numeroRemision + " NO puede procesarse (ya confirmada)");
            
            return puedeProcesamarse;
            
        } catch (Exception ex) {
            System.err.println("❌ Error al verificar si puede procesarse: " + ex.getMessage());
            // FAIL-SAFE: En caso de error, permite procesamiento
            return true;
        }
    }
    
    /**
     * 🔍 Método de conveniencia que retorna el estado de confirmación como texto
     * 
     * @param numeroRemision número de delivery a verificar
     * @return String descriptivo del estado: "CONFIRMADA", "DISPONIBLE" o "ERROR"
     */
    public String obtenerEstadoTexto(String numeroRemision) {
        try {
            boolean estaConfirmada = execute(numeroRemision);
            return estaConfirmada ? "CONFIRMADA" : "DISPONIBLE";
        } catch (Exception ex) {
            System.err.println("❌ Error al obtener estado de texto: " + ex.getMessage());
            return "ERROR";
        }
    }
    
    /**
     * 🎯 Método estático de conveniencia para uso rápido
     * 
     * @param numeroRemision número de delivery a validar
     * @return boolean true si está confirmada, false en caso contrario
     */
    public static boolean esRemisionConfirmada(String numeroRemision) {
        ValidarSiRemisionEstaConfirmadaUseCase useCase = new ValidarSiRemisionEstaConfirmadaUseCase();
        return useCase.execute(numeroRemision);
    }
} 
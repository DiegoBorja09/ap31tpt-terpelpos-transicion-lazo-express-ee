package com.infrastructure.config;

import com.application.ports.in.gopass.*;
import com.application.ports.out.gopass.*;
import com.application.useCases.gopass.*;
import com.infrastructure.adapters.out.gopass.*;
import com.application.services.GoPassPaymentService;
import com.infrastructure.adapters.out.gopass.ImpresionAdapter;

/**
 * ✅ ARQUITECTURA HEXAGONAL - Configuración de Dependencias
 * Este es el ÚNICO lugar que conoce todas las implementaciones concretas
 * Ensambla los componentes: Puertos → Adaptadores → Casos de Uso
 * 
 * Patrón: Dependency Injection Manual (sin frameworks externos)
 */
public class GoPassDependencyConfig {

    // ═══════════════════════════════════════════════════
    // ADAPTADORES DE SALIDA (Singletons para eficiencia)
    // ═══════════════════════════════════════════════════
    
    private static GoPassHttpPort goPassHttpAdapter;
    private static GoPassConfiguracionPort configuracionAdapter;
    private static GoPassPagoAdapter goPassPagoAdapter;
    
    /**
     * Obtiene (o crea) el adaptador HTTP de GoPass
     */
    public static GoPassHttpPort getGoPassHttpAdapter() {
        if (goPassHttpAdapter == null) {
            goPassHttpAdapter = new GoPassHttpAdapter();
        }
        return goPassHttpAdapter;
    }
    
    /**
     * Obtiene (o crea) el adaptador de configuración
     */
    public static GoPassConfiguracionPort getConfiguracionAdapter() {
        if (configuracionAdapter == null) {
            configuracionAdapter = new GoPassConfiguracionAdapter();
        }
        return configuracionAdapter;
    }
    
    /**
     * Obtiene (o crea) el adaptador de pago
     */
    public static GoPassPagoAdapter getGoPassPagoAdapter() {
        if (goPassPagoAdapter == null) {
            // Obtener parámetros de GoPass
            com.WT2.goPass.domain.entity.beans.GopassParameters params = 
                com.WT2.Containers.Dependency.SingletonMedioPago.ConetextDependecy
                    .getRecuperarParametrosGopass()
                    .execute(null);
            goPassPagoAdapter = new GoPassPagoAdapter(params);
        }
        return goPassPagoAdapter;
    }
    
    /**
     * Obtiene (o crea) el adaptador de impresión
     */
    private static ImpresionAdapter impresionAdapter;
    
    public static ImpresionAdapter getImpresionAdapter() {
        if (impresionAdapter == null) {
            impresionAdapter = new ImpresionAdapter();
        }
        return impresionAdapter;
    }
    
    // ═══════════════════════════════════════════════════
    // CASOS DE USO (Factories)
    // ═══════════════════════════════════════════════════
    
    /**
     * ✅ Crea el caso de uso: Consultar Placas
     * Inyecta las dependencias necesarias (adaptadores)
     */
    public static ConsultarPlacasGoPassPort crearConsultarPlacasUseCase() {
        return new ConsultarPlacasGoPassUseCase(
            getGoPassHttpAdapter(),
            getConfiguracionAdapter()
        );
    }
    
    /**
     * ✅ Crea el caso de uso: Validar Placa
     * Este caso de uso no necesita adaptadores externos
     */
    public static ValidarPlacaGoPassPort crearValidarPlacaUseCase() {
        return new ValidarPlacaGoPassUseCase();
    }
    
    /**
     * ✅ Crea el caso de uso: Procesar Pago
     * Inyecta las dependencias necesarias (adaptadores)
     */
    public static ProcesarPagoGoPassPort crearProcesarPagoUseCase() {
        return new ProcesarPagoGoPassUseCase(
            getGoPassPagoAdapter(),
            getConfiguracionAdapter()
        );
    }
    
    /**
     * ✅ Crea el caso de uso: Consultar Estado de Pago
     * Inyecta las dependencias necesarias (adaptadores)
     */
    public static ConsultarEstadoPagoGoPassPort crearConsultarEstadoPagoUseCase() {
        return new ConsultarEstadoPagoGoPassUseCase(
            getGoPassHttpAdapter(),
            getConfiguracionAdapter()
        );
    }
    
    /**
     * ✅ Crea el caso de uso: Imprimir Venta
     * Inyecta el adaptador de impresión
     */
    public static ImprimirVentaGoPassUseCase crearImprimirVentaUseCase() {
        return new ImprimirVentaGoPassUseCase(
            getImpresionAdapter()
        );
    }
    
    // ═══════════════════════════════════════════════════
    // MÉTODO HELPER: Crear todos los casos de uso
    // ═══════════════════════════════════════════════════
    
    /**
     * ✅ Crea un objeto que contiene todos los casos de uso configurados
     * Útil para inyectar en el controlador UI
     */
    public static GoPassUseCases crearTodosLosCasosDeUso() {
        return new GoPassUseCases(
            crearConsultarPlacasUseCase(),
            crearValidarPlacaUseCase(),
            crearProcesarPagoUseCase(),
            crearConsultarEstadoPagoUseCase()
        );
    }
    
    /**
     * Obtiene el adaptador de pago (necesario para configurar callbacks de UI)
     */
    public static GoPassPagoAdapter getGoPassPagoAdapterForCallbacks() {
        return getGoPassPagoAdapter();
    }
    
    /**
     * ✅ Contenedor de casos de uso (Data Transfer Object)
     * Agrupa todos los casos de uso para facilitar la inyección
     */
    public static class GoPassUseCases {
        public final ConsultarPlacasGoPassPort consultarPlacas;
        public final ValidarPlacaGoPassPort validarPlaca;
        public final ProcesarPagoGoPassPort procesarPago;
        public final ConsultarEstadoPagoGoPassPort consultarEstado;
        
        public GoPassUseCases(ConsultarPlacasGoPassPort consultarPlacas,
                             ValidarPlacaGoPassPort validarPlaca,
                             ProcesarPagoGoPassPort procesarPago,
                             ConsultarEstadoPagoGoPassPort consultarEstado) {
            this.consultarPlacas = consultarPlacas;
            this.validarPlaca = validarPlaca;
            this.procesarPago = procesarPago;
            this.consultarEstado = consultarEstado;
        }
    }
}


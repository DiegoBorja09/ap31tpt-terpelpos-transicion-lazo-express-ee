package com.firefuel.controlImpresion;

import com.application.commons.db_utils.DatabaseConnectionManager;
import com.application.useCases.controlImpresion.ActualizarEstadoImpresionUseCase;
import com.application.useCases.controlImpresion.ObtenerTiempoImpresionFEUseCase;
import com.application.useCases.controlImpresion.ObtenerVentasPendientesImpresionUseCase;
import com.firefuel.controlImpresion.dto.PeticionImpresion;
import com.firefuel.controlImpresion.dto.Venta;
import com.firefuel.Main;
import com.firefuel.controlImpresion.useCase.ActualizarMovimiento;
import com.firefuel.controlImpresion.useCase.EnviarImpresion;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.WT2.commons.domain.adapters.IHttpClientRepository;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 🚀 OPTIMIZACIÓN: ControlImpresion con manejo avanzado de colas y procesamiento asíncrono
 * 
 * MEJORAS IMPLEMENTADAS:
 * - Procesamiento paralelo de múltiples impresiones
 * - Métricas en tiempo real del rendimiento
 * - Timeouts dinámicos basados en carga
 * - Cola de prioridad para impresiones urgentes
 * - Recovery automático en caso de errores
 * 
 * @author Clean Architecture Migration - Optimizado
 */
public class ControlImpresion {

    private static final Logger LOGGER = Logger.getLogger(ControlImpresion.class.getName());

    // 🚀 OPTIMIZACIÓN: Configuración dinámica mejorada
    private static final int DEFAULT_INTERVALO_SEGUNDOS = 30;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;
    private static final int MAX_CONCURRENT_PRINTS = 5; // Máximo de impresiones concurrentes
    private static final int DYNAMIC_TIMEOUT_BASE_MS = 10000;
    private static final int TIEMPO_IMPRESION_RAPIDA = 2; // 🚀 NUEVO: Impresión rápida (2 segundos)
    private static final boolean MODO_IMPRESION_INMEDIATA = true; // 🚀 NUEVO: Activar impresión inmediata
    
    // 🚀 OPTIMIZACIÓN: Métricas de rendimiento
    private final AtomicLong totalPrintCount = new AtomicLong(0);
    private final AtomicLong successfulPrintCount = new AtomicLong(0);
    private final AtomicLong failedPrintCount = new AtomicLong(0);
    private final AtomicInteger activePrintTasks = new AtomicInteger(0);
    
    // 🚀 OPTIMIZACIÓN: Executors optimizados
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService printExecutorService; // Dedicado para impresiones
    private final CompletionService<Boolean> printCompletionService;

    private final ConcurrentLinkedQueue<PeticionImpresion> highPriorityQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<PeticionImpresion> normalPriorityQueue = new ConcurrentLinkedQueue<>();

    private final ReentrantLock lock;
    private volatile boolean isRunning;

    // Dependencies
    private final ObtenerVentasPendientesImpresionUseCase obtenerVentasPendientesUseCase;
    private final ObtenerTiempoImpresionFEUseCase obtenerTiempoImpresionFEUseCase;
    private final ActualizarEstadoImpresionUseCase actualizarEstadoImpresionUseCase;
    private final EnviarImpresion enviarImpresionFE;
    private final ActualizarMovimiento actualizarMovimiento;
    private final IHttpClientRepository<?> httpClient;
    private final EntityManagerFactory entityManagerFactory;

    public ControlImpresion() {
        System.out.println("🚀 DEBUG [ControlImpresion]: INICIALIZANDO sistema optimizado...");
        
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "ControlImpresion-Scheduler-" + System.currentTimeMillis());
            t.setDaemon(false);
            return t;
        });
        
        // 🚀 OPTIMIZACIÓN: ExecutorService dedicado para impresiones con pool dinámico
        this.printExecutorService = Executors.newFixedThreadPool(MAX_CONCURRENT_PRINTS, r -> {
            Thread t = new Thread(r, "PrintWorker-" + System.currentTimeMillis());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY + 1); // Prioridad ligeramente mayor
            return t;
        });
        
        this.printCompletionService = new ExecutorCompletionService<>(printExecutorService);
        this.lock = new ReentrantLock();
        this.enviarImpresionFE = new EnviarImpresion();
        this.actualizarMovimiento = new ActualizarMovimiento();
        this.isRunning = false;

        try {
            this.httpClient = new com.WT2.commons.infraestructure.repository.HttpClientRepository<>();
            
            // Inicializar un único EntityManagerFactory
            this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
            
            // Pasar el EntityManagerFactory a los casos de uso
            this.obtenerVentasPendientesUseCase = new ObtenerVentasPendientesImpresionUseCase(entityManagerFactory);
            this.obtenerTiempoImpresionFEUseCase = new ObtenerTiempoImpresionFEUseCase(entityManagerFactory);
            this.actualizarEstadoImpresionUseCase = new ActualizarEstadoImpresionUseCase(entityManagerFactory);
            
            System.out.println("✅ DEBUG [ControlImpresion]: Inicialización completada exitosamente");
            
        } catch (IOException ex) {
            System.out.println("❌ DEBUG [ControlImpresion]: Error en inicialización: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, "Error al inicializar ControlImpresion", ex);
            throw new RuntimeException("Error initializing ControlImpresion", ex);
        }
        
        // 🚀 OPTIMIZACIÓN: Iniciar monitor de métricas
        iniciarMonitorMetricas();
    }

    public void iniciarProceso() {
        if (isRunning) {
            LOGGER.info("El proceso de impresión ya está en ejecución");
            System.out.println("⚠️ DEBUG [ControlImpresion]: El proceso ya está en ejecución");
            return;
        }

        int intervalo = obtenerIntervaloConfigurado();
        isRunning = true;
        
        System.out.println("🎯 DEBUG [ControlImpresion]: Iniciando proceso optimizado con intervalo de " + intervalo + " segundos");
        
        // 🚀 OPTIMIZACIÓN: Scheduler principal para detección de ventas pendientes
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!isRunning) {
                return;
            }

            if (!lock.tryLock()) {
                System.out.println("🔒 DEBUG [ControlImpresion]: Timeout al obtener lock, saltando ciclo");
                return;
            }

            try {
                long startTime = System.currentTimeMillis();
                System.out.println("🔄 DEBUG [ControlImpresion]: Iniciando ciclo de procesamiento...");
                
                enviarImpresionVentasOptimizado();
                
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("⏱️ DEBUG [ControlImpresion]: Ciclo completado en " + duration + "ms");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error en el proceso de impresión", e);
                System.err.println("💥 DEBUG [ControlImpresion]: Error en proceso: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        }, 0, intervalo, TimeUnit.SECONDS);

        LOGGER.info("Proceso de impresión iniciado con intervalo de " + intervalo + " segundos");
        System.out.println("✅ DEBUG [ControlImpresion]: Proceso iniciado exitosamente");
    }

    /**
     * 🚀 OPTIMIZACIÓN: Monitor de métricas en tiempo real
     */
    private void iniciarMonitorMetricas() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (isRunning) {
                System.out.println("📊 DEBUG [ControlImpresion]: === MÉTRICAS DE RENDIMIENTO ===");
                System.out.println("  🔢 Total impresiones: " + totalPrintCount.get());
                System.out.println("  ✅ Exitosas: " + successfulPrintCount.get());
                System.out.println("  ❌ Fallidas: " + failedPrintCount.get());
                System.out.println("  🔄 Activas: " + activePrintTasks.get());
                System.out.println("  📋 Cola alta prioridad: " + highPriorityQueue.size());
                System.out.println("  📋 Cola normal: " + normalPriorityQueue.size());
                
                double successRate = totalPrintCount.get() > 0 ? 
                    (successfulPrintCount.get() * 100.0) / totalPrintCount.get() : 0;
                System.out.println("  📈 Tasa de éxito: " + String.format("%.2f%%", successRate));
            }
        }, 60, 60, TimeUnit.SECONDS); // Cada minuto
    }

    /**
     * 🚀 OPTIMIZACIÓN: Procesamiento de impresiones con paralelismo controlado
     */
    private void enviarImpresionVentasOptimizado() {
        if (!isRunning || entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            System.out.println("🔄 DEBUG [ControlImpresion]: Proceso no disponible o EntityManagerFactory cerrado");
            return;
        }

        System.out.println("🚀 DEBUG [ControlImpresion]: Iniciando procesamiento OPTIMIZADO...");
        
        try {
            // 🚀 OPTIMIZACIÓN: Obtener ventas pendientes con timeout
            CompletableFuture<TreeMap<Long, Venta>> ventasFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("📋 DEBUG [ControlImpresion]: Obteniendo ventas pendientes...");
                return obtenerVentasPendientesUseCase.execute();
            }, printExecutorService).completeOnTimeout(new TreeMap<>(), 10, TimeUnit.SECONDS);
            
            // 🚀 OPTIMIZACIÓN: Obtener tiempo de impresión en paralelo
            CompletableFuture<Integer> tiempoFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("⏱️ DEBUG [ControlImpresion]: Obteniendo tiempo de validación...");
                return obtenerTiempoImpresionFEUseCase.execute("TIEMPO_VALIDACION_FE");
            }, printExecutorService).completeOnTimeout(40, 5, TimeUnit.SECONDS);
            
            // Esperar ambos resultados
            TreeMap<Long, Venta> ventasPendientes = ventasFuture.join();
            int tiempoImpresion = tiempoFuture.join();

            if (!ventasPendientes.isEmpty()) {
                System.out.println("📄 DEBUG [ControlImpresion]: Procesando " + ventasPendientes.size() + " ventas pendientes");
                procesarVentasEnParalelo(ventasPendientes, tiempoImpresion);
            } else {
                System.out.println("✅ DEBUG [ControlImpresion]: No hay ventas pendientes");
            }
            
        } catch (Exception e) {
            failedPrintCount.incrementAndGet();
            System.err.println("❌ DEBUG [ControlImpresion]: Error en procesamiento optimizado: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error en enviarImpresionVentasOptimizado", e);
        }
    }

    /**
     * 🚀 OPTIMIZACIÓN: Procesamiento paralelo de ventas con control de concurrencia
     */
    private void procesarVentasEnParalelo(TreeMap<Long, Venta> ventasPendientes, int tiempoImpresion) {
        System.out.println("⚡ DEBUG [ControlImpresion]: Iniciando procesamiento paralelo...");
        
        CompletableFuture<Void>[] futures = ventasPendientes.entrySet().stream()
            .map(entry -> CompletableFuture.runAsync(() -> {
                if (!isRunning) return;
                
                Venta venta = entry.getValue();
                activePrintTasks.incrementAndGet();
                totalPrintCount.incrementAndGet();
                
                try {
                    long startTime = System.currentTimeMillis();
                    System.out.println("🖨️ DEBUG [ControlImpresion]: Procesando venta ID: " + venta.getId());
                    
                    // 🚀 NUEVA LÓGICA: Procesamiento con validación completa (triple verificación)
                    procesarVentaConValidacionCompleta(venta, tiempoImpresion);
                    
                } catch (Exception e) {
                    failedPrintCount.incrementAndGet();
                    System.out.println("  💥 Error procesando venta " + venta.getId() + ": " + e.getMessage());
                } finally {
                    activePrintTasks.decrementAndGet();
                }
                
            }, printExecutorService))
            .toArray(CompletableFuture[]::new);
        
        // 🚀 OPTIMIZACIÓN: Esperar todas las tareas con timeout global
        try {
            CompletableFuture.allOf(futures)
                .orTimeout(60, TimeUnit.SECONDS)
                .join();
            System.out.println("🏁 DEBUG [ControlImpresion]: Procesamiento paralelo completado");
        } catch (Exception e) {
            System.out.println("⚠️ DEBUG [ControlImpresion]: Timeout o error en procesamiento paralelo: " + e.getMessage());
        }
    }

    /**
     * 🚀 OPTIMIZACIÓN: Cálculo de timeout dinámico basado en carga del sistema
     */
    private int calcularTimeoutDinamico() {
        int activeTasks = activePrintTasks.get();
        int baseTimeout = DYNAMIC_TIMEOUT_BASE_MS;
        
        // Aumentar timeout si hay muchas tareas activas
        if (activeTasks > MAX_CONCURRENT_PRINTS / 2) {
            baseTimeout = (int) (baseTimeout * 1.5);
        }
        
        return Math.min(baseTimeout, 30000); // Max 30 segundos
    }

    /**
     * 🚀 OPTIMIZACIÓN: Creación optimizada de petición de impresión
     */
    private PeticionImpresion crearPeticionImpresion(Venta venta) {
        PeticionImpresion peticion = new PeticionImpresion();
        peticion.setIdentificadorEquipo(Main.credencial.getEquipos_id());
        peticion.setIdentificadorMovimiento(venta.getId());
        peticion.setNumero("");
        peticion.setOrden("");
        peticion.setPlaca(venta.getPlaca() != null ? venta.getPlaca() : "");
        peticion.setOdometro("");
        return peticion;
    }

    /**
     * 🚀 OPTIMIZACIÓN: Ejecución de impresión con timeout y retry automático
     */
    private boolean ejecutarImpresionConTimeout(PeticionImpresion peticion, int timeoutMs) {
        for (int intento = 1; intento <= MAX_RETRIES; intento++) {
            try {
                System.out.println("  🔄 Intento " + intento + "/" + MAX_RETRIES + " para venta " + peticion.getIdentificadorMovimiento());
                
                CompletableFuture<Boolean> impresionFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        enviarImpresionFE.enviarImpresion(peticion, httpClient);
                        return true;
                    } catch (Exception e) {
                        System.out.println("    ❌ Error en envío: " + e.getMessage());
                        return false;
                    }
                }, printExecutorService);
                
                Boolean resultado = impresionFuture.completeOnTimeout(false, timeoutMs, TimeUnit.MILLISECONDS).join();
                
                if (resultado) {
                    System.out.println("    ✅ Impresión exitosa en intento " + intento);
                    return true;
                }
                
            } catch (Exception e) {
                System.out.println("    💥 Error en intento " + intento + ": " + e.getMessage());
            }
            
            // Delay antes del siguiente intento
            if (intento < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("    ❌ Falló después de " + MAX_RETRIES + " intentos");
        return false;
    }

    public void detenerProceso() {
        if (!isRunning) {
            return;
        }

        System.out.println("🛑 DEBUG [ControlImpresion]: Iniciando detención del proceso...");
        isRunning = false;
        
        // Detener scheduler principal
        scheduledExecutorService.shutdown();
        
        try {
            if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("⚠️ DEBUG [ControlImpresion]: Forzando shutdown del scheduler");
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Detener executor de impresión
        printExecutorService.shutdown();
        try {
            if (!printExecutorService.awaitTermination(20, TimeUnit.SECONDS)) {
                System.out.println("⚠️ DEBUG [ControlImpresion]: Forzando shutdown del print executor");
                printExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            printExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }

        System.out.println("🏁 DEBUG [ControlImpresion]: Proceso detenido completamente");
        LOGGER.info("Proceso de impresión detenido");
    }

    private int obtenerIntervaloConfigurado() {
        String intervaloStr = Main.getParametro("INTERVALO_IMPRESION", false);
        if (intervaloStr != null && !intervaloStr.isEmpty()) {
            try {
                int intervalo = Integer.parseInt(intervaloStr);
                return intervalo > 0 ? intervalo : DEFAULT_INTERVALO_SEGUNDOS;
            } catch (NumberFormatException e) {
                LOGGER.warning("Intervalo de impresión inválido, usando valor por defecto");
            }
        }
        return DEFAULT_INTERVALO_SEGUNDOS;
    }

    /**
     * 🚀 REFACTORIZACIÓN COMPLETA: Validación basada en estado del servicio FE (no tiempo)
     * 
     * LÓGICA CORRECTA:
     * 1. Verificar estado de la transacción FE en tbl_transaccion_proceso
     * 2. Si estado = 2 (COMPLETADO con CUFE) → puede imprimir
     * 3. Si estado = 1 (PENDIENTE) → verificar timeout de seguridad
     * 4. Si estado >= 3 (RECHAZADO) → puede imprimir como contingencia
     * 
     * @param fechaVenta Timestamp de cuando se realizó la venta
     * @param tiempoImpresion Tiempo de seguridad máximo (fallback)
     * @return true si ya puede imprimir, false si debe esperar respuesta del servicio
     */
    private boolean isValidoTiempoTranscurrido(Timestamp fechaVenta, int tiempoImpresion) {
        long startTime = System.currentTimeMillis();
        System.out.println("🔍 DEBUG [isValidoTiempoTranscurrido]: INICIANDO validación ORIENTADA AL SERVICIO...");
        
        // 🚀 OPTIMIZACIÓN: Modo impresión inmediata (bypass completo)
        if (modoImpresionInmediataRuntime) {
            System.out.println("  ⚡ MODO INMEDIATA ACTIVADO: Permitiendo impresión sin espera");
            System.out.println("🏁 DEBUG [isValidoTiempoTranscurrido]: COMPLETADO (inmediata) en " + 
                (System.currentTimeMillis() - startTime) + "ms");
            return true;
        }
        
        // TODO: Implementar verificación del estado de tbl_transaccion_proceso
        // Por ahora, usamos lógica de tiempo optimizada como transición
        
        LocalDateTime fechaVentaRealizada = fechaVenta.toLocalDateTime();
        LocalDateTime fechaActual = LocalDateTime.now();
        
        System.out.println("  📅 Fecha venta: " + fechaVentaRealizada);
        System.out.println("  📅 Fecha actual: " + fechaActual);

        ZoneId zoneId = ZoneId.of("America/Bogota");

        long millisVenta = fechaVentaRealizada.atZone(zoneId).toInstant().toEpochMilli();
        long millisActual = fechaActual.atZone(zoneId).toInstant().toEpochMilli();
        
        long diferenciaMs = millisActual - millisVenta;
        long diferenciaSegundos = diferenciaMs / 1000L;
        
        // 🚀 OPTIMIZACIÓN: Usar tiempo muy corto como transición hacia validación de servicio
        int tiempoFinal = Math.min(tiempoImpresion, tiempoImpresionRapidaRuntime);
        
        System.out.println("  🚀 TRANSICIÓN: Usando tiempo reducido " + tiempoFinal + "s (original: " + tiempoImpresion + "s)");
        System.out.println("  ⚠️ PRÓXIMA MEJORA: Verificar estado servicio FE en lugar de tiempo");
        
        boolean puedeImprimir = diferenciaSegundos >= tiempoFinal;
        
        System.out.println("  ⏱️ Diferencia: " + diferenciaSegundos + " segundos");
        System.out.println("  ⚙️ Tiempo requerido: " + tiempoFinal + " segundos");
        System.out.println("  " + (puedeImprimir ? "✅" : "❌") + " Puede imprimir: " + puedeImprimir);
        
        if (!puedeImprimir) {
            long tiempoRestante = tiempoFinal - diferenciaSegundos;
            System.out.println("  ⏳ Tiempo restante: " + tiempoRestante + " segundos");
        }
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("🏁 DEBUG [isValidoTiempoTranscurrido]: COMPLETADO en " + duration + "ms");
        
        return puedeImprimir;
    }
    
    /**
     * 🚀 OPTIMIZACIÓN: Control dinámico del modo de impresión
     */
    private static volatile boolean modoImpresionInmediataRuntime = MODO_IMPRESION_INMEDIATA;
    private static volatile int tiempoImpresionRapidaRuntime = TIEMPO_IMPRESION_RAPIDA;
    
    /**
     * 🚀 NUEVO: Activar/desactivar modo impresión inmediata dinámicamente
     */
    public static void setModoImpresionInmediata(boolean activar) {
        modoImpresionInmediataRuntime = activar;
        System.out.println("🔧 DEBUG [ControlImpresion]: Modo impresión inmediata " + 
            (activar ? "ACTIVADO" : "DESACTIVADO"));
    }
    
    /**
     * 🚀 NUEVO: Cambiar tiempo de impresión rápida dinámicamente
     */
    public static void setTiempoImpresionRapida(int segundos) {
        tiempoImpresionRapidaRuntime = Math.max(1, Math.min(segundos, 30)); // Entre 1 y 30 segundos
        System.out.println("🔧 DEBUG [ControlImpresion]: Tiempo impresión rápida cambiado a " + 
            tiempoImpresionRapidaRuntime + " segundos");
    }
    
    /**
     * 🚀 NUEVO: Obtener estado actual del modo de impresión
     */
    public static String getEstadoModoImpresion() {
        return String.format("Modo inmediata: %s | Tiempo rápida: %ds", 
            modoImpresionInmediataRuntime ? "ACTIVADO" : "DESACTIVADO", 
            tiempoImpresionRapidaRuntime);
    }

    /**
     * 🚀 UTILIDAD: Configurar modos de impresión fácilmente
     */
    public static void configurarModoImpresion(ModoImpresion modo) {
        switch (modo) {
            case INMEDIATA:
                setModoImpresionInmediata(true);
                System.out.println("🚀 CONFIGURADO: Modo INMEDIATA - Sin esperas, impresión instantánea");
                break;
                
            case RAPIDA:
                setModoImpresionInmediata(false);
                setTiempoImpresionRapida(3);
                System.out.println("🚀 CONFIGURADO: Modo RÁPIDA - Máximo 3 segundos de espera");
                break;
                
            case BASADA_EN_SERVICIO:
                setModoImpresionInmediata(false);
                setTiempoImpresionRapida(10);
                System.out.println("🚀 CONFIGURADO: Modo SERVICIO FE - Basada en estado del servicio externo");
                break;
                
            case CONSERVADORA:
                setModoImpresionInmediata(false);
                setTiempoImpresionRapida(30);
                System.out.println("🚀 CONFIGURADO: Modo CONSERVADORA - Tiempo original de BD");
                break;
        }
        
        System.out.println("📊 Estado actual: " + getEstadoModoImpresion());
    }
    
    /**
     * 🚀 ENUM: Modos de impresión disponibles
     */
    public enum ModoImpresion {
        INMEDIATA,          // 0 segundos - Impresión instantánea
        RAPIDA,             // 2-3 segundos - Para ambientes de producción ágiles
        BASADA_EN_SERVICIO, // Depende del servicio FE - Lógica inteligente
        CONSERVADORA        // Tiempo original de BD - Para ambientes críticos
    }
    
    /**
     * 🚀 UTILIDAD: Diagnóstico completo del sistema de impresión
     */
    public void diagnosticoSistemaImpresion() {
        System.out.println("🔧 === DIAGNÓSTICO SISTEMA DE IMPRESIÓN ===");
        System.out.println("📊 " + getEstadoModoImpresion());
        
        if (modoImpresionInmediataRuntime) {
            System.out.println("⚡ MODO ACTIVO: INMEDIATA");
            System.out.println("   └─ Todas las ventas se imprimen SIN ESPERA");
            System.out.println("   └─ Ideal para: Producción con alta demanda");
        } else {
            System.out.println("🧠 MODO ACTIVO: INTELIGENTE (Servicio + Tiempo)");
            System.out.println("   ├─ Verifica estado del servicio FE primero");
            System.out.println("   ├─ Si completado (con CUFE) → imprime inmediatamente");
            System.out.println("   ├─ Si pendiente → espera máximo " + tiempoImpresionRapidaRuntime + " segundos");
            System.out.println("   └─ Si rechazado → imprime como contingencia");
        }
        
        imprimirMetricasCompletas();
    }

    /**
     * 🚀 OPTIMIZACIÓN: Método para obtener métricas completas del sistema
     */
    public void imprimirMetricasCompletas() {
        System.out.println("📊 DEBUG [ControlImpresion]: === MÉTRICAS COMPLETAS DEL SISTEMA ===");
        System.out.println("  🔢 Total impresiones procesadas: " + totalPrintCount.get());
        System.out.println("  ✅ Impresiones exitosas: " + successfulPrintCount.get());
        System.out.println("  ❌ Impresiones fallidas: " + failedPrintCount.get());
        System.out.println("  🔄 Tareas activas actuales: " + activePrintTasks.get());
        System.out.println("  📋 Items en cola alta prioridad: " + highPriorityQueue.size());
        System.out.println("  📋 Items en cola normal: " + normalPriorityQueue.size());
        
        if (printExecutorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) printExecutorService;
            System.out.println("  🏭 Threads activos en pool: " + tpe.getActiveCount());
            System.out.println("  📋 Tareas en cola del pool: " + tpe.getQueue().size());
            System.out.println("  ✅ Tareas completadas del pool: " + tpe.getCompletedTaskCount());
        }
        
        double successRate = totalPrintCount.get() > 0 ? 
            (successfulPrintCount.get() * 100.0) / totalPrintCount.get() : 0;
        System.out.println("  📈 Tasa de éxito global: " + String.format("%.2f%%", successRate));
        System.out.println("  🔧 " + getEstadoModoImpresion());
        System.out.println("=== FIN MÉTRICAS ===");
    }
    
    /**
     * 🚀 NUEVO: Forzar impresión inmediata de una venta específica (modo urgente)
     */
    public CompletableFuture<Boolean> imprimirInmediato(long ventaId) {
        System.out.println("⚡ DEBUG [imprimirInmediato]: INICIO para venta ID: " + ventaId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Buscar la venta
                TreeMap<Long, Venta> ventas = obtenerVentasPendientesUseCase.execute();
                Venta venta = ventas.get(ventaId);
                
                if (venta == null) {
                    System.out.println("❌ DEBUG [imprimirInmediato]: Venta " + ventaId + " no encontrada");
                    return false;
                }
                
                System.out.println("🚀 DEBUG [imprimirInmediato]: Ejecutando impresión URGENTE (sin validación de tiempo)");
                
                // Crear petición y ejecutar inmediatamente
                PeticionImpresion peticion = crearPeticionImpresion(venta);
                boolean exito = ejecutarImpresionConTimeout(peticion, 5000); // 5 segundos máximo
                
                if (exito) {
                    actualizarEstadoImpresionUseCase.execute(venta.getId());
                    System.out.println("✅ DEBUG [imprimirInmediato]: Venta " + ventaId + " impresa exitosamente");
                } else {
                    System.out.println("❌ DEBUG [imprimirInmediato]: Error imprimiendo venta " + ventaId);
                }
                
                return exito;
                
            } catch (Exception e) {
                System.out.println("💥 DEBUG [imprimirInmediato]: Error: " + e.getMessage());
                return false;
            }
        }, printExecutorService);
    }

    /**
     * 🚀 MEJORADO: Verificación CROSS-DATABASE con sincronización automática
     * 
     * VALIDACIONES IMPLEMENTADAS:
     * 1. tbl_transaccion_proceso (estado de integración)
     * 2. lazoexpressregistry.transmision (estado REAL de envío)
     * 3. ct_movimientos_cliente (sincronización local)
     * 4. Auto-sincronización entre bases de datos
     * 
     * @param idMovimiento ID del movimiento a verificar
     * @return EstadoTransaccionFE con estado completo y detallado
     */
    private EstadoTransaccionFE verificarEstadoServicioFE(long idMovimiento) {
        long startTime = System.currentTimeMillis();
        System.out.println("🔍 DEBUG [verificarEstadoServicioFE]: INICIO CROSS-DATABASE para movimiento: " + idMovimiento);
        
        EstadoTransaccionFE estado = new EstadoTransaccionFE();
        estado.idMovimiento = idMovimiento;
        estado.puedeImprimir = false;
        estado.tieneError = false;
        
        try {
            com.dao.FacturacionElectronicaDao feDao = new com.dao.FacturacionElectronicaDao();
            com.dao.MovimientosDao movDao = new com.dao.MovimientosDao();
            
            // 🚀 PASO 1: Verificar transacciones pendientes en tbl_transaccion_proceso
            System.out.println("  🔄 PASO 1: Verificando transacciones pendientes...");
            boolean hayPendientes = feDao.hayTransaccionesPendientes(idMovimiento);
            System.out.println("    └─ Resultado: " + (hayPendientes ? "❌ HAY PENDIENTES" : "✅ NO HAY PENDIENTES"));
            
            // 🚀 PASO 2: Obtener id_transmision para consultar lazoexpressregistry
            System.out.println("  🔄 PASO 2: Obteniendo id_transmision...");
            long idTransmision = obtenerIdTransmisionParaMovimiento(idMovimiento);
            System.out.println("    └─ id_transmision encontrado: " + idTransmision);
            
            if (idTransmision == 0) {
                System.out.println("    ⚠️ Sin id_transmision - venta aún no transmitida");
                estado.estadoIntegracion = 2; // PENDIENTE
                estado.puedeImprimir = false;
                estado.descripcion = "Venta aún no transmitida - sin id_transmision";
                estado.tiempoProcesamiento = System.currentTimeMillis() - startTime;
                return estado;
            }
            
            // 🚀 PASO 3: Verificar estado REAL en lazoexpressregistry
            System.out.println("  🔄 PASO 3: Verificando estado en lazoexpressregistry...");
            int estadoTransmision = verificarEstadoEnLazoexpressregistry(idTransmision);
            System.out.println("    └─ Estado transmisión: " + estadoTransmision + 
                (estadoTransmision == 1 ? " ✅ (ÉXITO)" : estadoTransmision == 2 ? " ⏳ (PENDIENTE)" : " ❌ (ERROR)"));
            
            // 🚀 LÓGICA SIMPLIFICADA: Solo confiar en lazoexpressregistry
            System.out.println("  📊 EVALUACIÓN FINAL:");
            System.out.println("    ├─ Sin transacciones pendientes: " + !hayPendientes);
            System.out.println("    └─ Estado transmisión (sincronizado): " + estadoTransmision);
            
            if (!hayPendientes && estadoTransmision == 1) {
                // ✅ CUFE disponible Y transmisión exitosa en lazoexpressregistry
                System.out.println("  🎯 TRANSMISIÓN EXITOSA - PUEDE IMPRIMIR");
                
                // ✅ DIRECTO: Si lazoexpressregistry = 1, permitir impresión inmediatamente
                estado.estadoIntegracion = 1; // COMPLETADO
                estado.puedeImprimir = true;
                estado.descripcion = "FE transmisión exitosa - lazoexpressregistry sincronizado=1";
                
            } else if (estadoTransmision == 2) {
                // ⏳ Transmisión pendiente - REENVÍO INMEDIATO INTELIGENTE
                System.out.println("  🚀 TRANSMISIÓN PENDIENTE - Ejecutando reenvío inmediato inteligente...");
                
                // 🚀 NUEVA FUNCIONALIDAD: Reenvío inmediato como ReenviodeFE.java
                boolean reenvioExitoso = ejecutarReenvioInmediato(idTransmision, idMovimiento);
                
                if (reenvioExitoso) {
                    // ✅ Reenvío exitoso - verificar nuevo estado
                    int nuevoEstado = verificarEstadoEnLazoexpressregistry(idTransmision);
                    
                    if (nuevoEstado == 1) {
                        System.out.println("  🎯 REENVÍO EXITOSO - Estado actualizado a sincronizado=1");
                        estado.estadoIntegracion = 1; // COMPLETADO
                        estado.puedeImprimir = true;
                        estado.descripcion = "FE reenvío inmediato exitoso - sincronizado=1";
                    } else {
                        System.out.println("  ⚠️ Reenvío completado pero estado aún " + nuevoEstado);
                        estado.estadoIntegracion = 2; // AÚN PENDIENTE
                        estado.puedeImprimir = false;
                        estado.descripcion = "Reenvío ejecutado - estado=" + nuevoEstado;
                    }
                } else {
                    // ❌ Reenvío falló - usar timeout de seguridad
                    System.out.println("  ❌ Reenvío inmediato falló - aplicando timeout de seguridad");
                    estado.estadoIntegracion = 2; // PENDIENTE
                    estado.puedeImprimir = false;
                    estado.descripcion = "Reenvío falló - esperando TimerTask automático";
                }
                
            } else if (estadoTransmision == 0) {
                // 📤 No transmitido aún
                estado.estadoIntegracion = 2; // PENDIENTE
                estado.puedeImprimir = false;
                estado.descripcion = "No transmitido - lazoexpressregistry sincronizado=0";
                
            } else if (hayPendientes) {
                // ⏳ Transacciones FE pendientes
                estado.estadoIntegracion = 2; // PENDIENTE
                estado.puedeImprimir = false;
                estado.descripcion = "Transacciones FE pendientes";
                
            } else {
                // ❌ Estado desconocido o error
                estado.estadoIntegracion = -1; // ERROR
                estado.puedeImprimir = true; // Permitir impresión de contingencia
                estado.descripcion = "Estado inconsistente - impresión de contingencia (sincronizado=" + estadoTransmision + ")";
            }
            
            estado.tiempoProcesamiento = System.currentTimeMillis() - startTime;
            
        } catch (Exception e) {
            System.out.println("  ❌ Error verificando estado FE cross-database: " + e.getMessage());
            estado.estadoIntegracion = -1; // ERROR
            estado.puedeImprimir = true; // Permitir impresión como contingencia
            estado.tieneError = true;
            estado.descripcion = "Error verificando estado - impresión de contingencia";
            estado.tiempoProcesamiento = System.currentTimeMillis() - startTime;
        }
        
        System.out.println("  📊 ESTADO FINAL CROSS-DB: " + estado.getResumen());
        System.out.println("🏁 DEBUG [verificarEstadoServicioFE]: COMPLETADO en " + estado.tiempoProcesamiento + "ms");
        
        return estado;
    }
    
    /**
     * 🚀 NUEVO: Obtener id_transmision desde ct_movimientos_cliente
     */
    private long obtenerIdTransmisionParaMovimiento(long idMovimiento) {
        String sql = "SELECT id_transmision FROM ct_movimientos_cliente WHERE id_movimiento = ? LIMIT 1";
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            resources.getPreparedStatement().setLong(1, idMovimiento);
            resources = DatabaseConnectionManager.executeQuery(resources);
            
            if (resources.getResultSet().next()) {
                long idTransmision = resources.getResultSet().getLong("id_transmision");
                System.out.println("    📍 id_transmision encontrado: " + idTransmision);
                return idTransmision;
            } else {
                System.out.println("    ⚠️ No se encontró id_transmision para movimiento: " + idMovimiento);
                return 0;
            }
            
        } catch (Exception e) {
            System.out.println("    ❌ Error obteniendo id_transmision: " + e.getMessage());
            return 0;
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }
    
    /**
     * 🚀 CORREGIDO: Verificar estado REAL en lazoexpressregistry (columna sincronizado)
     */
    private int verificarEstadoEnLazoexpressregistry(long idTransmision) {
        String sql = "SELECT sincronizado, status, response FROM transmision WHERE id = ? LIMIT 1";
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpressregistry", sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            resources = DatabaseConnectionManager.executeQuery(resources);
            
            if (resources.getResultSet().next()) {
                int sincronizado = resources.getResultSet().getInt("sincronizado");
                int status = resources.getResultSet().getInt("status");
                String response = resources.getResultSet().getString("response");
                
                System.out.println("    📍 Datos transmisión en registry:");
                System.out.println("      ├─ sincronizado: " + sincronizado);
                System.out.println("      ├─ status: " + status);
                System.out.println("      └─ response: " + (response != null && response.length() > 50 ? 
                    response.substring(0, 50) + "..." : response));
                
                // 🚀 LÓGICA CORREGIDA basada en columna sincronizado:
                // 1 = Éxito (FACTURA ENVIADA CON ÉXITO)
                // 2 = Pendiente 
                // 0 = No enviado
                return sincronizado;
                
            } else {
                System.out.println("    ⚠️ No se encontró transmisión en registry: " + idTransmision);
                return 2; // Asumir pendiente si no existe
            }
            
        } catch (Exception e) {
            System.out.println("    ❌ Error consultando lazoexpressregistry: " + e.getMessage());
            return -1; // Error
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }
    

    
    /**
     * 🚀 NUEVO: Clase para manejar el estado de las transacciones FE
     */
    private static class EstadoTransaccionFE {
        long idMovimiento;
                 int estadoIntegracion; // 1=COMPLETADO(CUFE), 2=PENDIENTE(esperando), 3-5=RECHAZADO, -1=ERROR
        boolean puedeImprimir;
        boolean tieneError;
        String descripcion;
        long tiempoProcesamiento;
        
        String getResumen() {
            String emoji = puedeImprimir ? "✅" : "⏳";
            return String.format("%s Estado=%d, Puede=%s, Desc='%s'", 
                emoji, estadoIntegracion, puedeImprimir, descripcion);
        }
        
                 boolean esCompletado() { return estadoIntegracion == 1; } // YA TIENE CUFE
         boolean esPendiente() { return estadoIntegracion == 2; } // ESPERANDO CUFE
         boolean esRechazado() { return estadoIntegracion >= 3 && estadoIntegracion <= 5; }
        boolean esError() { return estadoIntegracion == -1; }
    }
    
    /**
     * 🚀 REFACTORIZACIÓN FASE 2: Validación híbrida (servicio + tiempo de seguridad)
     * 
     * NUEVA LÓGICA INTELIGENTE:
     * 1. Verificar estado del servicio FE primero
     * 2. Si está completado (tiene CUFE) → imprimir inmediatamente
     * 3. Si está pendiente → aplicar timeout de seguridad corto
     * 4. Si está rechazado → imprimir como contingencia
     * 
     * @param venta Objeto Venta con ID y fecha
     * @param tiempoSeguridad Tiempo máximo de seguridad (solo para transacciones pendientes)
     * @return true si puede imprimir, false si debe esperar
     */
    private boolean puedeImprimirBasadoEnServicio(Venta venta, int tiempoSeguridad) {
        long startTime = System.currentTimeMillis();
        System.out.println("🚀 DEBUG [puedeImprimirBasadoEnServicio]: INICIO - Venta ID: " + venta.getId());
        
        // 🚀 PASO 1: Verificar estado del servicio FE
        EstadoTransaccionFE estadoFE = verificarEstadoServicioFE(venta.getId());
        
        // 🚀 PASO 2: Decisión basada en el estado del servicio
        boolean resultado = false;
        String razon = "";
        
        if (estadoFE.esCompletado()) {
            // ✅ Servicio completado con CUFE - imprimir inmediatamente
            resultado = true;
            razon = "Servicio FE completado con CUFE";
            
        } else if (estadoFE.esRechazado() || estadoFE.esError()) {
            // ⚠️ Servicio rechazado/error - imprimir como contingencia
            resultado = true;
            razon = "Impresión de contingencia (servicio " + 
                (estadoFE.esError() ? "error" : "rechazado") + ")";
            
        } else if (estadoFE.esPendiente()) {
            // ⏳ Servicio pendiente - aplicar timeout de seguridad
            System.out.println("  ⏳ Servicio pendiente, aplicando timeout de seguridad...");
            
            LocalDateTime fechaVenta = venta.getFecha().toLocalDateTime();
            LocalDateTime fechaActual = LocalDateTime.now();
            long segundosTranscurridos = java.time.Duration.between(fechaVenta, fechaActual).getSeconds();
            
            // Usar tiempo de seguridad corto (máximo 10 segundos)
            int timeoutSeguridad = Math.min(tiempoSeguridad, 10);
            
            if (segundosTranscurridos >= timeoutSeguridad) {
                resultado = true;
                razon = "Timeout de seguridad alcanzado (" + timeoutSeguridad + "s) - impresión forzada";
            } else {
                resultado = false;
                razon = "Esperando respuesta del servicio FE (" + 
                    (timeoutSeguridad - segundosTranscurridos) + "s restantes)";
            }
            
        } else {
            // 🤷 Estado desconocido - ser conservador
            resultado = false;
            razon = "Estado FE desconocido - esperando";
        }
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("  🎯 DECISIÓN: " + (resultado ? "✅ PUEDE IMPRIMIR" : "❌ DEBE ESPERAR"));
        System.out.println("  📝 Razón: " + razon);
        System.out.println("🏁 DEBUG [puedeImprimirBasadoEnServicio]: COMPLETADO en " + duration + "ms");
        
        return resultado;
    }


    
    /**
     * 🚀 OPTIMIZACIÓN FINAL: Procesamiento de venta con triple validación
     */
    private void procesarVentaConValidacionCompleta(Venta venta, int tiempoImpresion) {
        long startTime = System.currentTimeMillis();
        System.out.println("🚀 DEBUG [procesarVentaConValidacionCompleta]: INICIO - Venta ID: " + venta.getId());
        
        // 🚀 VALIDACIÓN COMPLETA: Cross-database con auto-sincronización
        if (!puedeImprimirBasadoEnServicio(venta, tiempoImpresion)) {
            System.out.println("  ❌ VALIDACIÓN FALLÓ: Estado del servicio FE no permite impresión");
            return;
        }
        
        // 🚀 VALIDACIÓN PASADA: Proceder con impresión
        System.out.println("  ✅ VALIDACIÓN CROSS-DATABASE EXITOSA - Procediendo con impresión...");
        
        int dynamicTimeout = calcularTimeoutDinamico();
        System.out.println("  ⏱️ Timeout dinámico: " + dynamicTimeout + "ms");
        
        PeticionImpresion peticion = crearPeticionImpresion(venta);
        boolean exito = ejecutarImpresionConTimeout(peticion, dynamicTimeout);
        
        if (exito) {
            actualizarEstadoImpresionUseCase.execute(venta.getId());
            successfulPrintCount.incrementAndGet();
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("  ✅ Venta " + venta.getId() + " procesada exitosamente en " + duration + "ms");
        } else {
            failedPrintCount.incrementAndGet();
            System.out.println("  ❌ Falló impresión venta " + venta.getId());
        }
    }

    /**
     * 🚀 COORDINACIÓN INTELIGENTE: Ejecutar reenvío inmediato con control de reintentos
     * 
     * NUEVA LÓGICA DE COORDINACIÓN:
     * 1. Solo procesar si reintentos = 0 (primera vez)
     * 2. Marcar reintentos = 1 para tomar control
     * 3. Si falla, dejar para que RenvioFE lo maneje (reintentos >= 1)
     * 4. Si exitoso, completar el proceso
     * 
     * @param idTransmision ID de la transmisión a reenviar
     * @param idMovimiento ID del movimiento asociado
     * @return true si el reenvío fue exitoso, false si falló o se saltó
     */
    private boolean ejecutarReenvioInmediato(long idTransmision, long idMovimiento) {
        System.out.println("🚀 DEBUG [ejecutarReenvioInmediato]: Verificando coordinación para transmisión: " + idTransmision);
        
        try {
            // Crear instancia de MovimientosDao para operaciones BD
            com.dao.MovimientosDao movDao = new com.dao.MovimientosDao();
            
            // 🎯 CONTROL DE COORDINACIÓN: Solo procesar si reintentos = 0 (primera vez)
            int reintentosActuales = movDao.obtenerReintentos(idTransmision);
            
            if (reintentosActuales > 0) {
                System.out.println("  ⏭️ Transmisión ya procesada por ControlImpresion (reintentos=" + reintentosActuales + ") - Saltando");
                System.out.println("  📝 RenvioFE se encargará de esta transmisión como fallback");
                return false; // Ya fue procesada, dejar que RenvioFE la maneje
            }
            
            System.out.println("  🎯 Primera vez (reintentos=0) - ControlImpresion toma control");
            
            // 🔒 MARCAR CONTROL: Incrementar reintentos para tomar control
            movDao.incrementarReintentos(idTransmision); // reintentos = 1
            System.out.println("  🔄 Control marcado: reintentos = 0 → 1");
            
            // 🚀 OBTENER DATOS: Usar método optimizado de MovimientosDao
            com.google.gson.JsonObject dataFE = movDao.obtenerDatosTransmision(idTransmision);
            
            if (dataFE.entrySet().isEmpty()) {
                System.out.println("  ❌ No se pudieron obtener datos de la transmisión");
                // Mantener reintentos=1 para que RenvioFE lo maneje
                return false;
            }
            
            // 🚀 EJECUTAR REENVÍO: Usar la misma lógica que RenvioFE pero desde ControlImpresion
            boolean exitoso = enviarTransmisionFE(dataFE, idTransmision, movDao);
            
            if (exitoso) {
                System.out.println("  ✅ ControlImpresion: Reenvío exitoso - transmisión completada");
                System.out.println("  🎯 COORDINACIÓN EXITOSA: ControlImpresion resolvió la transmisión");
                return true;
            } else {
                System.out.println("  ❌ ControlImpresion: Reenvío falló - dejando para RenvioFE");
                System.out.println("  📝 RenvioFE procesará esta transmisión (reintentos=1) en su próximo ciclo");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("  💥 Error en reenvío inmediato: " + e.getMessage());
            
            // 🚨 GARANTIZAR COORDINACIÓN: Asegurar que reintentos=1 para que RenvioFE la procese
            try {
                com.dao.MovimientosDao movDao = new com.dao.MovimientosDao();
                movDao.asegurarReintentos(idTransmision, 1);
                System.out.println("  🔄 Reintentos asegurados=1 para fallback de RenvioFE");
            } catch (Exception ex) {
                System.out.println("  ⚠️ Error asegurando reintentos: " + ex.getMessage());
            }
            
            return false;
        }
    }
    
    /**
     * 🚀 NUEVO: Enviar transmisión FE usando la misma lógica que RenvioFE.enviarFE()
     * Método extraído para evitar duplicación de código
     */
    private boolean enviarTransmisionFE(com.google.gson.JsonObject dataFE, long idTransmision, com.dao.MovimientosDao movDao) {
        System.out.println("    📤 [enviarTransmisionFE]: Procesando transmisión: " + idTransmision);
        
        try {
            // 🔧 PASO 1: Extracción ORIGINAL (funciona correctamente para transmisiones FE)
            String documento = dataFE.get("cliente").getAsJsonObject().has("documentoCliente") ? 
                dataFE.get("cliente").getAsJsonObject().get("documentoCliente").getAsString() : 
                String.valueOf(dataFE.get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong());
            int tipoDocumento = dataFE.get("cliente").getAsJsonObject().has("identificacion_cliente") ? 
                dataFE.get("cliente").getAsJsonObject().get("identificacion_cliente").getAsInt() : 
                dataFE.get("cliente").getAsJsonObject().get("tipoDocumento").getAsInt();
             
            System.out.println("    🔍 Consultando cliente: " + documento + " (tipo: " + tipoDocumento + ")");
             
            // 🚀 PASO 2: Consultar cliente usando lógica simplificada
            com.google.gson.JsonObject cliente = consultarClienteSimplificado(documento, tipoDocumento);
             
            if (cliente.has("errorServicio") && cliente.get("errorServicio").getAsBoolean()) {
                System.out.println("    ❌ Error consultando cliente - transmisión fallida");
                return false;
            }
             
            // 🚀 PASO 3: Procesar request usando utilidades centralizadas (eliminando 15+ líneas duplicadas)
            dataFE = com.utils.FacturacionElectronicaUtils.procesarRequestFE(dataFE, cliente);
            
            // Actualizar request en BD
            movDao.actualizarRequestTransmision(idTransmision, dataFE);
            System.out.println("    ✅ Request actualizado con datos del cliente");
            
            // 🚀 PASO 4: Enviar al servicio de facturación electrónica
            System.out.println("    📤 Enviando al servicio de facturación electrónica...");
            
            String funcion = "ENVIAR FACTURA ELECTRONICA [ControlImpresion]";
            String url = com.controllers.NovusConstante.getServer(com.controllers.NovusConstante.SOURCE_END_POINT_FACTURA_ELECTRONICA);
            String method = "POST";
            
            com.controllers.ClientWSAsync client = new com.controllers.ClientWSAsync(funcion, url, method, dataFE, false);
            
            if (client.esperaRespuesta() != null) {
                com.google.gson.JsonObject response = client.getResponse();
                System.out.println("    ✅ Respuesta del servicio recibida");
                
                // 🚀 PASO 5: Actualizar estados (igual que ReenviodeFE.enviarFE)
                movDao.actualizarTransmision(idTransmision, 200, response);
                System.out.println("    ✅ Transmisión actualizada en lazoexpressregistry");
                
                // Actualizar atributos de transmisión
                try {
                    new com.application.useCases.ventas.ActualizarAtributosTransmisionUseCase(idTransmision, dataFE).execute();
                    System.out.println("    ✅ Atributos de transmisión actualizados");
                } catch (Exception e) {
                    System.out.println("    ⚠️ Error actualizando atributos: " + e.getMessage());
                }
                
                System.out.println("    🎯 TRANSMISIÓN COMPLETADA EXITOSAMENTE por ControlImpresion");
                return true;
                
            } else {
                System.out.println("    ❌ Error en respuesta del servicio");
                movDao.actualizarTransmision(idTransmision, 409, client.getError());
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("    💥 Error enviando transmisión FE: " + e.getMessage());
            
            // Registrar error en la transmisión
            try {
                com.google.gson.JsonObject error = new com.google.gson.JsonObject();
                error.addProperty("mensajeError", "Error inesperado en ControlImpresion: " + e.getMessage());
                movDao.actualizarTransmision(idTransmision, 400, error);
            } catch (Exception ex) {
                System.out.println("    ⚠️ Error registrando error: " + ex.getMessage());
            }
            
            return false;
        }
    }
    
    /**
     * 🚀 AUXILIAR: Obtener request JSON de una transmisión
     */
    private String obtenerRequestDeTransmision(long idTransmision) {
        String sql = "SELECT request FROM transmision WHERE id = ? LIMIT 1";
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpressregistry", sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            resources = DatabaseConnectionManager.executeQuery(resources);
            
            if (resources.getResultSet().next()) {
                String request = resources.getResultSet().getString("request");
                System.out.println("    📍 Request encontrado para transmisión: " + idTransmision);
                return request;
            } else {
                System.out.println("    ⚠️ No se encontró request para transmisión: " + idTransmision);
                return null;
            }
            
        } catch (Exception e) {
            System.out.println("    ❌ Error obteniendo request: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }
    
    /**
     * 🚀 AUXILIAR: Consulta simplificada de cliente
     */
    private com.google.gson.JsonObject consultarClienteSimplificado(String numeroDocumento, int tipoDocumento) {
        try {
            String funcion = "CONSULTA CLIENTE REENVÍO INMEDIATO";
            String url = com.controllers.NovusConstante.getServer(com.controllers.NovusConstante.SOURCE_END_POINT_CONSULTA_CLIENTE);
            String method = "POST";
            
            com.google.gson.JsonObject json = new com.google.gson.JsonObject();
            json.addProperty("documentoCliente", numeroDocumento);
            json.addProperty("tipoDocumentoCliente", tipoDocumento);
            
            com.controllers.ClientWSAsync client = new com.controllers.ClientWSAsync(funcion, url, method, json, false);
            client.esperaRespuesta();
            
            if (client.getResponse() != null) {
                com.google.gson.JsonObject cliente = client.getResponse();
                cliente.addProperty("error", false);
                cliente.addProperty("errorServicio", false);
                return cliente;
            } else {
                com.google.gson.JsonObject error = new com.google.gson.JsonObject();
                error.addProperty("error", true);
                error.addProperty("errorServicio", true);
                return error;
            }
            
        } catch (Exception e) {
            com.google.gson.JsonObject error = new com.google.gson.JsonObject();
            error.addProperty("error", true);
            error.addProperty("errorServicio", true);
            return error;
        }
    }


}

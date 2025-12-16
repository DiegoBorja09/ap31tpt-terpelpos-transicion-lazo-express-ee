/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.presentation.agentes;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.ImpresonesFE.application.service.GestionarFeTirillaImpresion;
import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 🚀 OPTIMIZACIÓN: AgenteDeImpresionFE con ExecutorService dedicado
 * @author USUARIO - Optimizado para mejor rendimiento asíncrono
 */
public class AgenteDeImpresionFE {
    
    private static final Logger LOGGER = Logger.getLogger(AgenteDeImpresionFE.class.getName());
    private CompletableFuture<Void> future;
    
    // 🚀 OPTIMIZACIÓN: ExecutorService dedicado para procesamiento asíncrono
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(), 
        r -> {
            Thread t = new Thread(r, "AgenteImpresionFE-" + System.currentTimeMillis());
            t.setDaemon(true); // Thread daemon para evitar bloqueo en shutdown
            return t;
        }
    );
    
    static {
        // 🚀 OPTIMIZACIÓN: Shutdown hook para limpiar recursos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🔧 DEBUG [AgenteDeImpresionFE]: Ejecutando shutdown hook...");
            EXECUTOR_SERVICE.shutdown();
            try {
                if (!EXECUTOR_SERVICE.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("⚠️ DEBUG [AgenteDeImpresionFE]: Forzando shutdown del ExecutorService");
                    EXECUTOR_SERVICE.shutdownNow();
                }
            } catch (InterruptedException e) {
                EXECUTOR_SERVICE.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }
    
    public boolean execute(ParametrosPeticionFePrinter parametrosPeticionFePrinter){
        long startTime = System.currentTimeMillis();
        System.out.println("🚀 DEBUG [AgenteDeImpresionFE.execute]: INICIO");
        System.out.println("  📋 Parámetro recibido: " + (parametrosPeticionFePrinter != null ? "válido" : "null"));
        
        try {
            // 🚀 OPTIMIZACIÓN: Validación rápida de parámetros
            if (parametrosPeticionFePrinter == null) {
                System.out.println("❌ DEBUG [AgenteDeImpresionFE.execute]: Parámetro nulo, abortando");
                return false;
            }
            
            System.out.println("  📝 Encolando petición...");
            boolean seAgrego = SingletonMedioPago.ConetextDependecy.getEncolarPeticionFeImprimir().execute(parametrosPeticionFePrinter);
            System.out.println("  " + (seAgrego ? "✅" : "❌") + " Petición encolada: " + seAgrego);
            
            GestionarFeTirillaImpresion gestionador = SingletonMedioPago.ConetextDependecy.getGestionarFeTirillaImpresion();
            
            // 🚀 OPTIMIZACIÓN: Verificación optimizada del estado del CompletableFuture
            boolean shouldStartNewTask = (future == null || future.isDone() || future.isCancelled());
            System.out.println("  🔄 ¿Iniciar nueva tarea?: " + shouldStartNewTask);
            
            if (shouldStartNewTask) {
                System.out.println("  🏗️ Creando nueva tarea asíncrona con ExecutorService dedicado...");
                
                // 🚀 OPTIMIZACIÓN: Usar ExecutorService dedicado en lugar del pool común
                future = CompletableFuture.runAsync(gestionador, EXECUTOR_SERVICE)
                    .whenComplete((result, throwable) -> {
                        long duration = System.currentTimeMillis() - startTime;
                        if (throwable != null) {
                            System.out.println("❌ DEBUG [AgenteDeImpresionFE]: Error en ejecución asíncrona: " + throwable.getMessage());
                            LOGGER.log(Level.SEVERE, "Error en procesamiento asíncrono", throwable);
                        } else {
                            System.out.println("✅ DEBUG [AgenteDeImpresionFE]: Tarea completada en " + duration + "ms");
                        }
                    });
                    
                System.out.println("  ⚡ Tarea asíncrona iniciada con ExecutorService dedicado");
            } else {
                System.out.println("  ♻️ Reutilizando tarea existente (optimización)");
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("🏁 DEBUG [AgenteDeImpresionFE.execute]: COMPLETADO en " + totalTime + "ms");
            System.out.println("  📊 Resultado final: " + seAgrego);
            
            return seAgrego;
            
        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            System.out.println("💥 DEBUG [AgenteDeImpresionFE.execute]: ERROR después de " + errorTime + "ms");
            System.out.println("  🔥 Mensaje: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error inesperado en AgenteDeImpresionFE.execute", e);
            return false;
        }
    }
    
    /**
     * 🚀 OPTIMIZACIÓN: Método para obtener métricas del ExecutorService
     */
    public static void logExecutorMetrics() {
        if (EXECUTOR_SERVICE instanceof java.util.concurrent.ThreadPoolExecutor) {
            java.util.concurrent.ThreadPoolExecutor tpe = (java.util.concurrent.ThreadPoolExecutor) EXECUTOR_SERVICE;
            System.out.println("📊 DEBUG [AgenteDeImpresionFE]: Métricas del ExecutorService:");
            System.out.println("  🔢 Threads activos: " + tpe.getActiveCount());
            System.out.println("  📋 Tareas en cola: " + tpe.getQueue().size());
            System.out.println("  ✅ Tareas completadas: " + tpe.getCompletedTaskCount());
        }
    }
}

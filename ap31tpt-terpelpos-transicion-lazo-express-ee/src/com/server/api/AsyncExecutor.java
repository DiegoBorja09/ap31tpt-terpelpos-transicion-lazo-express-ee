package com.server.api;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación del patrón Singleton para el ejecutor asíncrono.
 * Garantiza que solo exista una instancia del ExecutorService en toda la aplicación.
 * Incluye identificadores únicos para cada hilo para facilitar el debugging y logging.
 */
public class AsyncExecutor {
    private static final Logger logger = Logger.getLogger(AsyncExecutor.class.getName());

    private static final int CORE_POOL_SIZE = AsyncExecutorConfig.getInt("executor.corePoolSize", 5);
    private static final int MAX_POOL_SIZE = AsyncExecutorConfig.getInt("executor.maxPoolSize", 10);
    private static final int KEEP_ALIVE_SECONDS = AsyncExecutorConfig.getInt("executor.keepAliveSeconds", 30);
    private static final int QUEUE_CAPACITY = AsyncExecutorConfig.getInt("executor.queueCapacity", 100);

    // Contador atómico para generar identificadores únicos de hilos
    private static final AtomicLong threadIdCounter = new AtomicLong(1);

    // Instancia única del Singleton (volatile para thread-safety)
    private static volatile AsyncExecutor instance;
    
    // ExecutorService encapsulado
    private final ExecutorService executor;
    
    // ScheduledExecutorService para tareas con retardo
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * ThreadFactory personalizado que asigna identificadores únicos a cada hilo
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicLong threadNumber = new AtomicLong(1);
        private final boolean daemon;

        public NamedThreadFactory(String namePrefix, boolean daemon) {
            this.namePrefix = namePrefix;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            t.setDaemon(daemon);
            return t;
        }
    }

    /**
     * Constructor privado para evitar instanciación directa
     */
    private AsyncExecutor() {
        logger.info("[AsyncExecutor] Inicializando AsyncExecutor Singleton");
        
        // Crear ThreadFactory personalizado para el executor principal
        NamedThreadFactory mainThreadFactory = new NamedThreadFactory("AsyncExecutor-Main", false);
        
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                mainThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
        
        // Crear ThreadFactory personalizado para el scheduled executor
        NamedThreadFactory scheduledThreadFactory = new NamedThreadFactory("AsyncExecutor-Scheduled", false);
        
        // Inicializar ScheduledExecutorService para tareas con retardo
        scheduledExecutor = Executors.newScheduledThreadPool(
                Math.max(2, CORE_POOL_SIZE / 2), // Usar la mitad de hilos para tareas programadas
                scheduledThreadFactory
        );
        
        logger.info("[AsyncExecutor] AsyncExecutor Singleton inicializado con éxito");
    }

    /**
     * Obtiene la instancia única del AsyncExecutor (Double-Checked Locking)
     * @return La instancia única del AsyncExecutor
     */
    public static AsyncExecutor getInstance() {
        if (instance == null) {
            synchronized (AsyncExecutor.class) {
                if (instance == null) {
                    instance = new AsyncExecutor();
                }
            }
            logger.info("[AsyncExecutor] Instancia de AsyncExecutor no inicializada, creando nueva instancia ");
        }
        return instance;
    }

    /**
     * Obtiene el ExecutorService interno
     * @return El ExecutorService encapsulado
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Obtiene el ScheduledExecutorService interno
     * @return El ScheduledExecutorService encapsulado
     */
    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    /**
     * Obtiene el identificador del hilo actual
     * @return Identificador del hilo actual en formato "ThreadName-ThreadId"
     */
    public static String getCurrentThreadId() {
        Thread currentThread = Thread.currentThread();
        return currentThread.getName() + "-" + currentThread.getId();
    }

    /**
     * Obtiene información detallada del hilo actual
     * @return Información completa del hilo actual
     */
    public static String getCurrentThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return String.format("Thread[%s-%d, Priority=%d, Daemon=%s, State=%s]", 
                currentThread.getName(), 
                currentThread.getId(),
                currentThread.getPriority(),
                currentThread.isDaemon(),
                currentThread.getState());
    }

    /**
     * Genera un identificador único para una tarea
     * @param taskName Nombre descriptivo de la tarea
     * @return Identificador único de la tarea
     */
    public static String generateTaskId(String taskName) {
        return taskName + "-" + threadIdCounter.getAndIncrement();
    }

    /**
     * Ejecuta una tarea de forma asíncrona
     * @param task La tarea a ejecutar
     */
    public void execute(Runnable task) {
        if (task == null) {
            logger.warning("[AsyncExecutor] Se intentó ejecutar una tarea null");
            return;
        }
        
        String taskId = generateTaskId("Task");
        logger.info("[AsyncExecutor] [" + taskId + "] Programando tarea asíncrona desde hilo: " + getCurrentThreadId());
        
        executor.submit(() -> {
            String threadId = getCurrentThreadId();
            logger.info("[AsyncExecutor] [" + taskId + "] Iniciando ejecución en hilo: " + threadId);
            
            try {
                task.run();
                logger.info("[AsyncExecutor] [" + taskId + "] Tarea completada exitosamente en hilo: " + threadId);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AsyncExecutor] [" + taskId + "] Error en ejecución asincrónica en hilo: " + threadId, e);
            }
        });
    }

    /**
     * Ejecuta una tarea de forma asíncrona con retardo
     * @param task La tarea a ejecutar
     * @param delayMs Retardo en milisegundos antes de ejecutar la tarea
     */
    public void executeWithDelay(Runnable task, long delayMs) {
        if (task == null) {
            logger.warning("[AsyncExecutor] Se intentó ejecutar una tarea null con retardo");
            return;
        }
        
        if (delayMs < 0) {
            logger.warning("[AsyncExecutor] Retardo negativo especificado: " + delayMs + "ms, ejecutando inmediatamente");
            delayMs = 0;
        }
        
        String taskId = generateTaskId("DelayedTask");
        logger.info("[AsyncExecutor] [" + taskId + "] Programando tarea con retardo de " + delayMs + "ms desde hilo: " + getCurrentThreadId());
        
        scheduledExecutor.schedule(() -> {
            String threadId = getCurrentThreadId();
            logger.info("[AsyncExecutor] [" + taskId + "] Iniciando ejecución de tarea con retardo en hilo: " + threadId);
            
            try {
                task.run();
                logger.info("[AsyncExecutor] [" + taskId + "] Tarea con retardo ejecutada exitosamente en hilo: " + threadId);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AsyncExecutor] [" + taskId + "] Error en ejecución asincrónica con retardo en hilo: " + threadId, e);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Ejecuta una tarea de forma asíncrona con retardo y retorna un Future
     * @param task La tarea a ejecutar
     * @param delayMs Retardo en milisegundos antes de ejecutar la tarea
     * @param <T> Tipo de retorno
     * @return ScheduledFuture con el resultado de la tarea
     */
    public <T> ScheduledFuture<T> executeWithDelay(Callable<T> task, long delayMs) {
        if (task == null) {
            logger.warning("[AsyncExecutor] Se intentó ejecutar una tarea Callable null con retardo");
            return null;
        }
        
        if (delayMs < 0) {
            logger.warning("[AsyncExecutor] Retardo negativo especificado: " + delayMs + "ms, ejecutando inmediatamente");
            delayMs = 0;
        }
        
        String taskId = generateTaskId("DelayedCallableTask");
        logger.info("[AsyncExecutor] [" + taskId + "] Programando tarea Callable con retardo de " + delayMs + "ms desde hilo: " + getCurrentThreadId());
        
        return scheduledExecutor.schedule(() -> {
            String threadId = getCurrentThreadId();
            logger.info("[AsyncExecutor] [" + taskId + "] Iniciando ejecución de tarea Callable con retardo en hilo: " + threadId);
            
            try {
                T result = task.call();
                logger.info("[AsyncExecutor] [" + taskId + "] Tarea Callable con retardo ejecutada exitosamente en hilo: " + threadId);
                return result;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AsyncExecutor] [" + taskId + "] Error en ejecución asincrónica Callable con retardo en hilo: " + threadId, e);
                throw e;
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Ejecuta una tarea de forma periódica con intervalo fijo
     * @param task La tarea a ejecutar
     * @param initialDelayMs Retardo inicial en milisegundos
     * @param periodMs Intervalo entre ejecuciones en milisegundos
     * @return ScheduledFuture que puede usarse para cancelar la tarea
     */
    public ScheduledFuture<?> executePeriodically(Runnable task, long initialDelayMs, long periodMs) {
        if (task == null) {
            logger.warning("[AsyncExecutor] Se intentó ejecutar una tarea periódica null");
            return null;
        }
        
        if (initialDelayMs < 0 || periodMs <= 0) {
            logger.warning("[AsyncExecutor] Parámetros inválidos para tarea periódica: initialDelay=" + initialDelayMs + "ms, period=" + periodMs + "ms");
            return null;
        }
        
        String taskId = generateTaskId("PeriodicTask");
        logger.info("[AsyncExecutor] [" + taskId + "] Programando tarea periódica con delay inicial de " + initialDelayMs + "ms y período de " + periodMs + "ms desde hilo: " + getCurrentThreadId());
        
        return scheduledExecutor.scheduleAtFixedRate(() -> {
            String threadId = getCurrentThreadId();
            logger.info("[AsyncExecutor] [" + taskId + "] Ejecutando tarea periódica en hilo: " + threadId);
            
            try {
                task.run();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AsyncExecutor] [" + taskId + "] Error en ejecución de tarea periódica en hilo: " + threadId, e);
            }
        }, initialDelayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Ejecuta una tarea de forma periódica con delay fijo entre ejecuciones
     * @param task La tarea a ejecutar
     * @param initialDelayMs Retardo inicial en milisegundos
     * @param delayMs Delay entre ejecuciones en milisegundos
     * @return ScheduledFuture que puede usarse para cancelar la tarea
     */
    public ScheduledFuture<?> executeWithFixedDelay(Runnable task, long initialDelayMs, long delayMs) {
        if (task == null) {
            logger.warning("[AsyncExecutor] Se intentó ejecutar una tarea con delay fijo null");
            return null;
        }
        
        if (initialDelayMs < 0 || delayMs <= 0) {
            logger.warning("[AsyncExecutor] Parámetros inválidos para tarea con delay fijo: initialDelay=" + initialDelayMs + "ms, delay=" + delayMs + "ms");
            return null;
        }
        
        String taskId = generateTaskId("FixedDelayTask");
        logger.info("[AsyncExecutor] [" + taskId + "] Programando tarea con delay fijo: delay inicial de " + initialDelayMs + "ms y delay entre ejecuciones de " + delayMs + "ms desde hilo: " + getCurrentThreadId());
        
        return scheduledExecutor.scheduleWithFixedDelay(() -> {
            String threadId = getCurrentThreadId();
            logger.info("[AsyncExecutor] [" + taskId + "] Ejecutando tarea con delay fijo en hilo: " + threadId);
            
            try {
                task.run();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AsyncExecutor] [" + taskId + "] Error en ejecución de tarea con delay fijo en hilo: " + threadId, e);
            }
        }, initialDelayMs, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Ejecuta una tarea con retorno de forma asíncrona
     * @param task La tarea a ejecutar
     * @param <T> Tipo de retorno
     * @return Future con el resultado de la tarea
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            logger.warning("[AsyncExecutor] Se intentó ejecutar una tarea Callable null");
            return null;
        }
        
        String taskId = generateTaskId("CallableTask");
        logger.info("[AsyncExecutor] [" + taskId + "] Programando tarea Callable desde hilo: " + getCurrentThreadId());
        
        return executor.submit(() -> {
            String threadId = getCurrentThreadId();
            logger.info("[AsyncExecutor] [" + taskId + "] Iniciando ejecución de tarea Callable en hilo: " + threadId);
            
            try {
                T result = task.call();
                logger.info("[AsyncExecutor] [" + taskId + "] Tarea Callable completada exitosamente en hilo: " + threadId);
                return result;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AsyncExecutor] [" + taskId + "] Error en ejecución asincrónica con retorno en hilo: " + threadId, e);
                throw e;
            }
        });
    }

    /**
     * Ejecuta una tarea de forma asíncrona (método estático de conveniencia)
     * @param task La tarea a ejecutar
     */
    public static void executeAsync(Runnable task) {
        getInstance().execute(task);
    }

    /**
     * Ejecuta una tarea de forma asíncrona con retardo (método estático de conveniencia)
     * @param task La tarea a ejecutar
     * @param delayMs Retardo en milisegundos
     */
    public static void executeAsyncWithDelay(Runnable task, long delayMs) {
        getInstance().executeWithDelay(task, delayMs);
    }

    /**
     * Ejecuta una tarea de forma asíncrona con retardo y retorno (método estático de conveniencia)
     * @param task La tarea a ejecutar
     * @param delayMs Retardo en milisegundos
     * @param <T> Tipo de retorno
     * @return ScheduledFuture con el resultado de la tarea
     */
    public static <T> ScheduledFuture<T> submitAsyncWithDelay(Callable<T> task, long delayMs) {
        return getInstance().executeWithDelay(task, delayMs);
    }

    /**
     * Ejecuta una tarea de forma periódica (método estático de conveniencia)
     * @param task La tarea a ejecutar
     * @param initialDelayMs Retardo inicial en milisegundos
     * @param periodMs Intervalo entre ejecuciones en milisegundos
     * @return ScheduledFuture que puede usarse para cancelar la tarea
     */
    public static ScheduledFuture<?> executeAsyncPeriodically(Runnable task, long initialDelayMs, long periodMs) {
        return getInstance().executePeriodically(task, initialDelayMs, periodMs);
    }

    /**
     * Ejecuta una tarea con delay fijo entre ejecuciones (método estático de conveniencia)
     * @param task La tarea a ejecutar
     * @param initialDelayMs Retardo inicial en milisegundos
     * @param delayMs Delay entre ejecuciones en milisegundos
     * @return ScheduledFuture que puede usarse para cancelar la tarea
     */
    public static ScheduledFuture<?> executeAsyncWithFixedDelay(Runnable task, long initialDelayMs, long delayMs) {
        return getInstance().executeWithFixedDelay(task, initialDelayMs, delayMs);
    }

    /**
     * Ejecuta una tarea con retorno de forma asíncrona (método estático de conveniencia)
     * @param task La tarea a ejecutar
     * @param <T> Tipo de retorno
     * @return Future con el resultado de la tarea
     */
    public static <T> Future<T> submitAsync(Callable<T> task) {
        return getInstance().submit(task);
    }

    /**
     * Cierra el executor de forma ordenada
     */
    public void shutdown() {
        try {
            logger.info("[AsyncExecutor] Iniciando cierre ordenado del AsyncExecutor desde hilo: " + getCurrentThreadId());
            
            // Cerrar executor principal
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warning("[AsyncExecutor] Timeout en cierre ordenado del executor principal, forzando cierre desde hilo: " + getCurrentThreadId());
                executor.shutdownNow();
            }
            
            // Cerrar scheduled executor
            scheduledExecutor.shutdown();
            if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warning("[AsyncExecutor] Timeout en cierre ordenado del scheduled executor, forzando cierre desde hilo: " + getCurrentThreadId());
                scheduledExecutor.shutdownNow();
            }
            
            logger.info("[AsyncExecutor] AsyncExecutor cerrado exitosamente desde hilo: " + getCurrentThreadId());
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "[AsyncExecutor] Error durante el cierre del AsyncExecutor desde hilo: " + getCurrentThreadId(), e);
            executor.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Cierra el executor de forma ordenada (método estático de conveniencia)
     */
    public static void shutdownAsync() {
        getInstance().shutdown();
    }

    /**
     * Verifica si el executor está cerrado
     * @return true si está cerrado, false en caso contrario
     */
    public boolean isShutdown() {
        return executor.isShutdown() && scheduledExecutor.isShutdown();
    }

    /**
     * Verifica si el executor está cerrado (método estático de conveniencia)
     * @return true si está cerrado, false en caso contrario
     */
    public static boolean isAsyncShutdown() {
        return getInstance().isShutdown();
    }

    /**
     * Obtiene estadísticas de los executors
     * @return String con información de estadísticas
     */
    public String getExecutorStats() {
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
            return String.format(
                "Executor Principal - Pool: %d/%d, Cola: %d, Activos: %d, Completadas: %d",
                tpe.getPoolSize(), tpe.getMaximumPoolSize(),
                tpe.getQueue().size(), tpe.getActiveCount(), tpe.getCompletedTaskCount()
            );
        }
        return "[AsyncExecutor] Executor Principal - Estadísticas no disponibles";
    }

    /**
     * Obtiene estadísticas de los executors (método estático de conveniencia)
     * @return String con información de estadísticas
     */
    public static String getAsyncExecutorStats() {
        return getInstance().getExecutorStats();
    }
}
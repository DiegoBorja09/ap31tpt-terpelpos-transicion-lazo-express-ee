package com.services;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeOutsManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger LOGGER = Logger.getLogger(TimeOutsManager.class.getName());

    public void setTimeoutUtilManager(int delayInSeconds, Runnable task) {
        if (task == null || delayInSeconds < 0) return;

        scheduler.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error en tarea programada", e);
            }
        }, delayInSeconds, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        scheduler.shutdown();
    }
}
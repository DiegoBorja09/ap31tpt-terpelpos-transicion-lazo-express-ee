package com.server.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AsyncExecutorConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AsyncExecutorConfig.class.getClassLoader()
                .getResourceAsStream("executor.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            System.err.println("No se pudo cargar executor.properties. Usando valores por defecto.");
        }
    }

    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
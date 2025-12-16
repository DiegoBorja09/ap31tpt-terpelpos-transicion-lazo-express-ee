package com.application.commons.db_utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ejemplo de uso de DatabaseConnectionManager.
 * Muestra cómo refactorizar el código existente para usar la clase centralizadora.
 * 
 * @author Sistema
 */
public class DatabaseConnectionManagerExample {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManagerExample.class.getName());
    
    /**
     * Ejemplo de cómo refactorizar el método searchConfigurationParams del SetupDao.
     * 
     * @param stringParams Parámetros de búsqueda
     * @return JsonArray con los resultados
     */
    public JsonArray searchConfigurationParamsExample(String stringParams) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        JsonArray params = null;
        
        try {
            String sql = "select tipo, valor, codigo from wacher_parametros where codigo in(" + stringParams + ")";
            
            // Usar la clase centralizadora
            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
            
            while (resources.getResultSet().next()) {
                if (params == null) {
                    params = new JsonArray();
                }
                JsonObject object = new JsonObject();
                object.addProperty("tipo", resources.getResultSet().getString("tipo"));
                object.addProperty("valor", resources.getResultSet().getString("valor"));
                object.addProperty("codigo", resources.getResultSet().getString("codigo"));
                params.add(object);
            }
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error en searchConfigurationParamsExample", ex);
        } finally {
            // Cerrar recursos de manera centralizada
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        
        return params;
    }

    /**
     * Ejemplo de cómo manejar múltiples conexiones a diferentes bases de datos.
     * 
     * @param id ID del registro
     * @return JsonObject con datos de ambas bases de datos
     */
    public JsonObject consultarMultiplesBasesExample(long id) {
        JsonObject resultado = new JsonObject();
        
        DatabaseConnectionManager.DatabaseResources resourcesCore = null;
        DatabaseConnectionManager.DatabaseResources resourcesRegistry = null;
        
        try {
            // Consulta en la base de datos core
            String sqlCore = "SELECT * FROM tabla_core WHERE id = ?";
            resourcesCore = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sqlCore);
            resourcesCore.getPreparedStatement().setLong(1, id);
            DatabaseConnectionManager.executeQuery(resourcesCore);
            
            if (resourcesCore.getResultSet().next()) {
                resultado.addProperty("datos_core", resourcesCore.getResultSet().getString("datos"));
            }
            
            // Consulta en la base de datos registry
            String sqlRegistry = "SELECT * FROM tabla_registry WHERE id = ?";
            resourcesRegistry = DatabaseConnectionManager.createDatabaseResources("lazoexpressregistry", sqlRegistry);
            resourcesRegistry.getPreparedStatement().setLong(1, id);
            DatabaseConnectionManager.executeQuery(resourcesRegistry);
            
            if (resourcesRegistry.getResultSet().next()) {
                resultado.addProperty("datos_registry", resourcesRegistry.getResultSet().getString("datos"));
            }
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error en consultarMultiplesBasesExample", ex);
        } finally {
            // Cerrar todos los recursos
            DatabaseConnectionManager.closeDatabaseResources(resourcesCore);
            DatabaseConnectionManager.closeDatabaseResources(resourcesRegistry);
        }
        
        return resultado;
    }
} 
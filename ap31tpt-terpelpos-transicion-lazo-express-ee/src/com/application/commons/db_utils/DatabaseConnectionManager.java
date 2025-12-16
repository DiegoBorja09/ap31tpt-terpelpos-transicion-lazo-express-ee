package com.application.commons.db_utils;

import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase centralizadora para manejar las conexiones de base de datos.
 * Proporciona métodos para crear y cerrar las instancias de Connection, 
 * PreparedStatement y ResultSet de manera centralizada.
 * 
 * @author Sistema
 */
public class DatabaseConnectionManager {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());
    
    /**
     * Clase interna para encapsular las instancias de conexión
     */
    public static class DatabaseResources {
        private Connection connection;
        private PreparedStatement preparedStatement;
        private ResultSet resultSet;
        
        public DatabaseResources(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
            this.connection = connection;
            this.preparedStatement = preparedStatement;
            this.resultSet = resultSet;
        }
        
        public Connection getConnection() {
            return connection;
        }
        
        public PreparedStatement getPreparedStatement() {
            return preparedStatement;
        }
        
        public ResultSet getResultSet() {
            return resultSet;
        }
        
        public void setResultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
        }
    }

    /**
     * Crea las instancias de Connection, PreparedStatement y ResultSet para una consulta.
     *
     * @param databaseName Nombre de la base de datos (ej: "lazoexpresscore", "lazoexpressregistry")
     * @param sqlQuery Consulta SQL a ejecutar
     * @return DatabaseResources con las instancias creadas
     * @throws SQLException Si ocurre un error al crear las instancias
     */
    public static DatabaseResources createDatabaseResources(String databaseName, String sqlQuery) throws SQLException {
        try {
            Connection connection = Main.obtenerConexionAsync(databaseName);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            LOGGER.log(Level.INFO, "Recursos de base de datos creados exitosamente para: {0}", databaseName);

            return new DatabaseResources(connection, preparedStatement, null);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear recursos de base de datos para: " + databaseName, e);
            throw e;
        }
    }

    /**
     * Ejecuta una consulta y crea el ResultSet.
     *
     * @param resources Recursos de base de datos existentes
     * @return DatabaseResources con el ResultSet creado
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public static DatabaseResources executeQuery(DatabaseResources resources) throws SQLException {
        try {
            ResultSet resultSet = resources.getPreparedStatement().executeQuery();
            resources.setResultSet(resultSet);

            LOGGER.log(Level.INFO, "Consulta ejecutada exitosamente");

            return resources;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al ejecutar consulta", e);
            throw e;
        }
    }

    /**
     * Cierra todas las instancias de Connection, PreparedStatement y ResultSet.
     * 
     * @param resources Recursos de base de datos a cerrar
     */
    public static void closeDatabaseResources(DatabaseResources resources) {
        if (resources == null) {
            return;
        }
        
        try {
            if (resources.getResultSet() != null) {
                resources.getResultSet().close();
                LOGGER.log(Level.INFO, "ResultSet cerrado exitosamente");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar ResultSet", e);
        }
        
        try {
            if (resources.getPreparedStatement() != null) {
                resources.getPreparedStatement().close();
                LOGGER.log(Level.INFO, "PreparedStatement cerrado exitosamente");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar PreparedStatement", e);
        }
        
        try {
            if (resources.getConnection() != null) {
                resources.getConnection().close();
                LOGGER.log(Level.INFO, "Connection cerrado exitosamente");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar Connection", e);
        }
    }
    
    /**
     * Método de conveniencia que ejecuta una consulta completa y retorna los recursos.
     * 
     * @param databaseName Nombre de la base de datos
     * @param sqlQuery Consulta SQL
     * @return DatabaseResources con la consulta ejecutada
     * @throws SQLException Si ocurre un error
     */
    public static DatabaseResources executeCompleteQuery(String databaseName, String sqlQuery) throws SQLException {
        DatabaseResources resources = createDatabaseResources(databaseName, sqlQuery);
        return executeQuery(resources);
    }
} 
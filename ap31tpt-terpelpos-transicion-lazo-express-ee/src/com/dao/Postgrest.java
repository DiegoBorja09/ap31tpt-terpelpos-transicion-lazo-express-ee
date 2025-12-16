/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ergarcia
 */
public class Postgrest {

    public String CLASS;
    public String DRIVE;
    public String HOST;
    public String PORT;
    public String DATABASE;
    public String USUARIO;
    public String PASSWORD;

    private Connection conn;
    private Statement stm;

    public Postgrest(String database) {
        CLASS = "org.postgresql.Driver";
        DRIVE = "jdbc:postgresql"; 
        HOST = NovusConstante.HOST_CENTRAL_POINT;
        PORT = NovusConstante.POSGRES_PORT;
        DATABASE = database;
        USUARIO = NovusConstante.POSGRES_USER;
        PASSWORD = NovusConstante.POSGRES_CONTRASENA;
    }

    public Connection conectar() throws DAOException {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DRIVE + "://" + HOST + ":" + PORT + "/" + DATABASE, USUARIO, PASSWORD);
                NovusUtils.printLn("Conectando");
            } else {
//                NovusUtils.printLn("Retornando conexion");
            }
        } catch (Exception ex) {
            conn = null;
            throw new DAOException("ERROR: " + ex.getMessage());

        }
        return conn;
    }

    public boolean conectar(String host, String database, String usuario, String password) throws DAOException {
        boolean conect = false;
        try {
            conn = DriverManager.getConnection(DRIVE + "://" + host + ":" + PORT + "/" + database, usuario, password);
            conect = true;
        } catch (SQLException ex) {
            throw new DAOException("ERROR: " + ex.getMessage());
        }
        return conect;
    }

    public void close() throws DAOException {
        if (stm != null) {
            try {
                stm.close();
            } catch (SQLException e) {
                throw new DAOException("ERROR: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DAOException("ERROR: " + e.getMessage());
            }
        }
    }

    boolean existeDatabase() {
        boolean existeDatabase;
        try {
            conn = DriverManager.getConnection(DRIVE + "://" + HOST + ":" + PORT + "/" + DATABASE, USUARIO, PASSWORD);
            existeDatabase = true;
        } catch (SQLException ex) {
            existeDatabase = false;
        }
        return existeDatabase;
    }

}

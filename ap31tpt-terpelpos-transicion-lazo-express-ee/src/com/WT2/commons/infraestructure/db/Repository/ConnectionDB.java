/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.commons.domain.adapters.IConnectionDB;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class ConnectionDB implements IConnectionDB<Connection>{

    private Connection conectionDB;
    private String dbName;        

    public ConnectionDB( String dbName) {
        this.dbName = dbName;
    }
            
    
    
    
    @Override
    public Connection getDBConection() {  
        try {
            if(conectionDB == null || conectionDB.isClosed()){
                conectionDB = Main.obtenerConexion(dbName);      
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conectionDB;
    }
    
}

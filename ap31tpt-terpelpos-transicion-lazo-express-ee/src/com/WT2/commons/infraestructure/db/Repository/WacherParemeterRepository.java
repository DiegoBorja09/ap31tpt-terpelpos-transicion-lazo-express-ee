/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.entity.WatcherParameter;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class WacherParemeterRepository implements IRepository<String, WatcherParameter> {

    public Connection dbConnect;

    public WacherParemeterRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    public WatcherParameter getData(String params) {
        
        WatcherParameter watcherParameter = new WatcherParameter();
        watcherParameter.setParameter("None");
        watcherParameter.setValor("");

        try {
            String sql = "select * from wacher_parametros where codigo = ? ;";
           if(dbConnect.isClosed()) dbConnect = Main.obtenerConexion("lazoexpresscore");
            PreparedStatement stm = dbConnect.prepareStatement(sql);
            stm.setString(1, params);
            ResultSet re = stm.executeQuery();
            
            while(re.next()){
                watcherParameter.setValor(re.getString("valor"));
                watcherParameter.setParameter(re.getString("codigo"));
                watcherParameter.setId(re.getLong("id"));
            
            }

        } catch (SQLException ex) {
            Logger.getLogger(WacherParemeterRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
         return watcherParameter;
    }

    @Override
    public int updateData(String params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

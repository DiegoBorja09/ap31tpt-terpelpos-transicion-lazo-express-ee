/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.infrastructure.db.repositories;

import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.valueObject.QueryCollectionGoPass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeison
 */
public class GopassParametersRepository implements IRepository<Void, ResultSet> {

    private IConnectionDB<Connection> conection;

    public GopassParametersRepository(IConnectionDB<Connection> conection) {
        this.conection = conection;
    }
    
    
    @Override
    public ResultSet getData(Void params) {
        Connection dbcon = this.conection.getDBConection();
        try  {
            PreparedStatement stm = dbcon.prepareStatement(QueryCollectionGoPass.FETCH_PARAMETERS_GOPASS);
            ResultSet rs = stm.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(GopassParametersRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public int updateData(Void params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
  
    
}

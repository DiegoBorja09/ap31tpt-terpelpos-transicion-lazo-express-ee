/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.infraestructura.Repository;

import com.WT2.appTerpel.domain.valueObject.AppterpelWatcherParametros;
import com.WT2.appTerpel.domain.valueObject.QueryCollection;
import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.infraestructure.db.Repository.ConnectionDB;
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
public class ValidarIntentoAppTerpelRepository implements IRepository<Long, Integer> {

    public IConnectionDB<Connection> dbConnect;

    public ValidarIntentoAppTerpelRepository(IConnectionDB<Connection> dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public Integer getData(Long params) {
        int result = 0;

        try {
            Connection connection = dbConnect.getDBConection();

            PreparedStatement pstm = connection.prepareStatement(QueryCollection.evaluraIntentosAppterpel);
            pstm.setLong(1, params);
            pstm.setString(2, AppterpelWatcherParametros.PERMITIR_REINTENTOS);
            ResultSet rows = pstm.executeQuery();
            while (rows.next()) {

                result = rows.getInt("fnc_validar_intentos_appterpel");

            }
            pstm.close();
        } catch (SQLException ex) {
            Logger.getLogger(ValidarIntentoAppTerpelRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }

    @Override
    public int updateData(Long params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

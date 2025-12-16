/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.WT2.appTerpel.infraestructura.Repository;

import com.WT2.appTerpel.domain.entities.VentasAppterpelBotonesValidador;
import com.WT2.appTerpel.domain.valueObject.AppterpelWatcherParametros;
import com.WT2.appTerpel.domain.valueObject.QueryCollection;
import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Devitech
 */
public class ValidarBotonesVentasAppterpelRepository implements IRepository<Long, VentasAppterpelBotonesValidador> {

    private IConnectionDB<Connection> dbConnect;

    public ValidarBotonesVentasAppterpelRepository(IConnectionDB<Connection> dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public VentasAppterpelBotonesValidador getData(Long params) {

        VentasAppterpelBotonesValidador validador = new VentasAppterpelBotonesValidador();
        validador.setFidelizacion(true);
        validador.setPago(true);
        validador.setProceso(true);

        try {
            Connection connection = dbConnect.getDBConection();

            PreparedStatement pstm = connection.prepareStatement(QueryCollection.validarBotoneVentasAppterpel);
            pstm.setLong(1, params);
            ResultSet rows = pstm.executeQuery();
            while (rows.next()) {

                validador.setPago(rows.getBoolean("pago"));
                validador.setFidelizacion(rows.getBoolean("fideliza"));
                validador.setProceso(rows.getBoolean("proceso"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidarBotonesVentasAppterpelRepository.class.getName()).log(Level.SEVERE, null, ex);

        }

        return validador;

    }

    @Override
    public int updateData(Long params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

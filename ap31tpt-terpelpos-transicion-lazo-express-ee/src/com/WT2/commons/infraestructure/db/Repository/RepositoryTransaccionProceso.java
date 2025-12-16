/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.entity.TransaccionProcesParams;
import com.WT2.commons.domain.entity.TransaccionProceso;
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
        public class RepositoryTransaccionProceso implements IRepository< TransaccionProcesParams, TransaccionProceso> {

    public IConnectionDB<Connection> dbConnect;

    public RepositoryTransaccionProceso(IConnectionDB<Connection> dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public TransaccionProceso getData(TransaccionProcesParams transaccionProcesParams) {
        TransaccionProceso transaccionProcess = new TransaccionProceso();
        transaccionProcess.setIdTrasccion(0);
        transaccionProcess.setIdMov(transaccionProcesParams.getIdMov());
        transaccionProcess.setIdEstadoIntegracion(0);
        transaccionProcess.setIdEstadoProceso(0);

        try {
            String sql = "select * from procesos.tbl_transaccion_proceso tp  where tp.id_integracion = ? and tp.id_movimiento = ? ";
            Connection connection = dbConnect.getDBConection();
            System.out.println("Query sql: " + sql);
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setLong(1, transaccionProcesParams.getIdintegracion());
            stm.setLong(2, transaccionProcesParams.getIdMov());

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                transaccionProcess.setIdTrasccion(rs.getLong("id_transaccion_proceso"));
                transaccionProcess.setIdEstadoIntegracion(rs.getLong("id_estado_integracion"));
                transaccionProcess.setIdEstadoProceso(rs.getLong("id_estado_proceso"));
            }

            System.out.println("id_transaccion_proceso: " + transaccionProcess.getIdTrasccion());
            System.out.println("id_estado_integracion: " + transaccionProcess.getIdEstadoIntegracion());
            System.out.println("id_estado_proceso: " + transaccionProcess.getIdEstadoProceso());
            stm.close();
        } catch (SQLException ex) {
            transaccionProcess = new TransaccionProceso();
            transaccionProcess.setIdTrasccion(0);
            Logger.getLogger(RepositoryTransaccionProceso.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transaccionProcess;
    }

    @Override
    public int updateData(TransaccionProcesParams params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

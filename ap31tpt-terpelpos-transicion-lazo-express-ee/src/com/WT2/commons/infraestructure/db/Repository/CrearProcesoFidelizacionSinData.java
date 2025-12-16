/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.entity.TransaccionProceso;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class CrearProcesoFidelizacionSinData implements IRepository<TransaccionProceso, Void> {

    public Connection dbConnect;

    public CrearProcesoFidelizacionSinData(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public Void getData(TransaccionProceso transaccionProceso) {
        try {
            String sql = "call procesos.insert_procesos_datos(?,?,?,null)";
            if (dbConnect.isClosed()) {
                dbConnect = Main.obtenerConexion("lazoexpresscore");
            }

            PreparedStatement stm = dbConnect.prepareStatement(sql);

            stm.setLong(1, transaccionProceso.getIdTrasccion());
            stm.setLong(2, 3);
            stm.setLong(3, transaccionProceso.getIdMov());

            stm.execute();

        } catch (SQLException ex) {
            Logger.getLogger(CrearSinDatoFidelizacionRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int updateData(TransaccionProceso params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

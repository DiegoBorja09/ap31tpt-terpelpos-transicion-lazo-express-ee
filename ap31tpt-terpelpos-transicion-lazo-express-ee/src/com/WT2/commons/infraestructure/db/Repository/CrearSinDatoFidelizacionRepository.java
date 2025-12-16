/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.loyalty.domain.entities.beans.ProcesosPagosFidelizacionParams;
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
public class CrearSinDatoFidelizacionRepository implements IRepository<ProcesosPagosFidelizacionParams, Void> {

    public Connection dbConnect;

    public CrearSinDatoFidelizacionRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public Void getData(ProcesosPagosFidelizacionParams procesosFidelizacion) {
        try {
            String sql = "select * from procesos.fnc_crear_fidelizacion(?,?,?,?,?,?,?)";
            if (dbConnect.isClosed()) {
                dbConnect = Main.obtenerConexion("lazoexpresscore");
            }

            PreparedStatement stm = dbConnect.prepareStatement(sql);

//            stm.setLong(1, procesosFidelizacion.getIdMov());
//            stm.setInt(2, 3);
//            stm.setInt(3, 3);
//            stm.setInt(4, 4);
//            stm.setInt(5, 1);
//            stm.setString(6, "N");
//            stm.setBoolean(7, true);
            
            stm.setLong(1, procesosFidelizacion.getIdMov());
            stm.setInt(2, procesosFidelizacion.getTipoIdentificador());
            stm.setInt(3, procesosFidelizacion.getIdTipoTransaccionProceso());
            stm.setInt(4, procesosFidelizacion.getIdEstadoProceso());
            stm.setInt(5, procesosFidelizacion.getIdTipoNegocio());
            stm.setString(6, procesosFidelizacion.getFideliza());
            stm.setBoolean(7, procesosFidelizacion.isEditarFidelizacion());

            stm.executeQuery();

        } catch (SQLException ex) {
            Logger.getLogger(CrearSinDatoFidelizacionRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int updateData(ProcesosPagosFidelizacionParams params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

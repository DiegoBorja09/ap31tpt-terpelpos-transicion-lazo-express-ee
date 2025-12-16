/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.fidelizacionDAO;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Devitech
 */
public class FidelizacionDao {

    public String indentficadorPuntoVenta(String negocio) {
        String identificador = null;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select cf.valor from "
                + " parametrizacion.ct_tipo_negocio_origen ctno "
                + " inner join parametrizacion.codigo_fidelizacion cf on "
                + " cf.negocio_id = ctno.id_tipo_negocio_origen "
                + " where ctno.valor = ?";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)){
            pmt.setString(1, negocio);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()){
                identificador = rs.getString("valor");
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo indentficadorPuntoVenta(String negocio) en la clase de fidelizaiconDAO -> paquete -> fidelizacionDAO \n"
                    + e.getMessage());
        }
        return identificador;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Devitech
 */
public class ReporteCierreDiaDao {

    public boolean activarImpresionCierreDia() {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select reporteria_cierres.check_cierre()";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                return rs.getBoolean("check_cierre");
            } else {
                return false;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en metodo public boolean activarImpresionCierreDia() ubicado en " + ReporteCierreDiaDao.class.getName() + " error -> " + e.getMessage());
            return false;
        }
    }

    public String novedades(int year, int month, int day) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from reporteria_cierres.obtener_novedades(?,?,?)";
        String mensaje = "";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, year);
            pmt.setInt(2, month);
            pmt.setInt(3, day);
            ResultSet rs = pmt.executeQuery();
            int contador = 0;
            while (rs.next()) {
                mensaje = mensaje.concat((contador > 0 ? " <br>" : "") + rs.getString("novedad").concat("."));
                contador++;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo String novedades ubicado en " + ReporteCierreDiaDao.class.getName() + " error -> " + e.getMessage());
        }
        return mensaje;
    }
}

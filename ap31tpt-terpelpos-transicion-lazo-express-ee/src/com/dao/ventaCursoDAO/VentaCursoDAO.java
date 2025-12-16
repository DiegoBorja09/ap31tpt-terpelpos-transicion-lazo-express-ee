/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.ventaCursoDAO;

import com.bean.ventaCurso.VentaCursoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author USUARIO
 */
public class VentaCursoDAO {

    public VentaCursoBean atributosVentaCurso(int cara) {
        VentaCursoBean atributosVentaCurso = null;
        String sql = "select\n"
                + "(vc.atributos::json->>'DatosFactura') atributos\n"
                + "from\n"
                + "ventas_curso vc\n"
                + "where\n"
                + "vc.cara = ?\n"
                + "and (vc.atributos::json->>'DatosFactura') is not null; ";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, cara);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                atributosVentaCurso = new VentaCursoBean();
                JsonObject json = Main.gson.fromJson(rs.getString("atributos"), JsonObject.class);
                atributosVentaCurso.setNumeroComprobante(json.get("numero_comprobante").getAsString());
                atributosVentaCurso.setOdometro(json.get("odometro").getAsString());
                atributosVentaCurso.setPlaca(json.get("placa").getAsString());
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo atributosVentaCurso \n"
                    + e.getMessage());
        }
        return atributosVentaCurso;
    }

}

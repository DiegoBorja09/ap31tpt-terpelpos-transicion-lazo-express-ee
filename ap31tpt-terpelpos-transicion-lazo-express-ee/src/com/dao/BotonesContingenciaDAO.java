/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Devitech
 */
public class BotonesContingenciaDAO {

    //refactorizado en un UseCase: package com.application.useCases.persons.NegociosActivosUseCase
    /*
    public JsonObject negociosActivos() {
        JsonObject data = new JsonObject();
        data.addProperty("combustible", combustibles());
        data.addProperty("canastilla", canastillaKiosco("C"));
        data.addProperty("kiosco", canastillaKiosco("K"));
        System.out.println(data);
        return data;
    }

    private boolean combustibles() {
        String sql = "select * from ct_bodegas cb"
                .concat(" inner join ct_bodegas_productos cbp on cb.id = cbp.bodegas_id "
                        + "and cb.atributos::json ->>'estado'= 'A' limit 1");
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (Statement stm = conexion.createStatement()) {
            ResultSet rs = stm.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en la ejecucion de la consulta combutibles" + e.getClass().getName() + " " + e.getMessage());
            return false;
        }
    }

    private boolean canastillaKiosco(String tipoNegocio) {
        String sql = "select *  from bodegas b "
                .concat(" inner join bodegas_productos bp on")
                .concat(" b.id = bp.bodegas_id")
                .concat(" where b.finalidad = ? and b.estado  = 'A' limit 1");
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        try (PreparedStatement ptm = conexion.prepareStatement(sql)) {
            ptm.setString(1, tipoNegocio);
            ResultSet rs = ptm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en la ejecucion de la consulta casntilla" + e.getClass().getName() + " " + e.getMessage());
            return false;
        }
    }
     */
}

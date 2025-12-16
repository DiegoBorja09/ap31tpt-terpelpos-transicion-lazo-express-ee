/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.EntradaCombustibleDao;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;

/**
 *
 * @author Devitech
 */
public class SapConfiguracionDao {

    public boolean isMasser() {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from empresas e \n"
                + "inner join tbl_tipos_empresas tte on e.id_tipo_empresa = tte.id_tipo_empresa \n"
                + "where tte.descripcion ilike '%masser%' limit 1;";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo isMasser() ubicado en la clase " + SapConfiguracionDao.class.getName() + " " + e.getMessage());
            return false;
        }
    }
}

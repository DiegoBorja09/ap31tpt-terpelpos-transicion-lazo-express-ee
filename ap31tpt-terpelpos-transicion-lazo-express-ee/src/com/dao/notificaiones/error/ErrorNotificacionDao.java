/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.notificaiones.error;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author Devitech
 */
public class ErrorNotificacionDao {

    public JsonArray dataNotificacionError() {
        String sql = "select detalle from eventos_reporte.tmp_evento_error e inner join eventos_reporte.reporte_status rs on "
                + " e.fk_status_reporte = rs.id where "
                + " rs.descripcion != 'RESUELTA' "
                + " and (e.codigo_seguimiento is null or e.codigo_seguimiento = '') "
                + " and e.fecha_creacion >= current_date  - interval '5 days' "
                + " order by e.fecha_creacion desc;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        JsonArray data = new JsonArray();
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            Gson gson = new Gson();
            while (rs.next()) {
                data.add(gson.fromJson(rs.getString("detalle"), JsonObject.class));
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo  public JsonArray dataNotificaionError(JsonArray data) en la clase ErrorNotificaionDao paquete com.dao.notificaiones.error -> " + e.getMessage());
        }

        return data;
    }

}

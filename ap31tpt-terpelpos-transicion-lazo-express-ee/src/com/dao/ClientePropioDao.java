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

/**
 *
 * @author Devitech
 */
public class ClientePropioDao {

    public void guardarLiberacionClientePropio(JsonObject trama, String url, String method) {
        Connection connection = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "INSERT INTO public.transmision( "
                + " equipo_id, request, response, sincronizado, fecha_generado, "
                + " fecha_trasmitido, fecha_ultima, url, method, reintentos, status) "
                + " VALUES (?, ?, NULL, 0, now(), "
                + " NULL, NULL, ?, ?, 0, 0);";
        try (PreparedStatement pmt = connection.prepareStatement(sql)) {
            pmt.setInt(1, 1);
            pmt.setString(2, trama.toString());
            pmt.setString(3, url);
            pmt.setString(4, method);
            int respuesta = pmt.executeUpdate();
            if (respuesta >= 1) {
                NovusUtils.printLn("Se ha insertado exitosamente la transmisión de clientes propios ");
            } else {
                NovusUtils.printLn("Error al guardar la transmisión de para clientes propios");
            }
        } catch (Exception e) {
            NovusUtils.printLn("Ha ocurrido un error inesperado en el método public void guardarLiberacionClientePropio(JsonObject trama, String url, String method) " + e.getMessage());
        }
    }
}

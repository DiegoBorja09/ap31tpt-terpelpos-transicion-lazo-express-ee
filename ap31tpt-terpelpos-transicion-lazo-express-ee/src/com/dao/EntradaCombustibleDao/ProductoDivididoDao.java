/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.EntradaCombustibleDao;

import com.bean.entradaCombustible.ProductoDividido;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Devitech
 */
public class ProductoDivididoDao {

    public boolean guardarProductosDivididos(Map<Long, ProductoDividido> productos) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.insertar_producto_dividido(?::jsonb)";
        JsonArray data = new JsonArray();
        productos.forEach((t, u) -> {
            Gson gson = new Gson();
            Type typeObject = new TypeToken<ProductoDividido>() {
            }.getType();
            JsonObject producto = gson.fromJson(gson.toJson(u, typeObject), JsonObject.class);
            data.add(producto);
        });
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, data.toString());
            NovusUtils.printLn(pmt.toString());
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                NovusUtils.printLn("se creado exitosamente el registro");
                return true;
            } else {
                NovusUtils.printLn("no se pudo crear el registro");
                return false;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public void guardarProductosDivididos(TreeMap<Long, ProductoDividido> productos) "
                    + "ubicado en la clase " + ProductoDivididoDao.class.getName() + " " + e.getMessage());
            return false;
        }

    }

    public void actualizarEstadoProductoDividido(int estado, long idRemisionProducto, int idProducto, long idRemisionSap, long idTanque) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.actualizar_estado_remision(?, ?, ?, ?, ?);";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, estado);
            pmt.setLong(2, idRemisionProducto);
            pmt.setInt(3, idProducto);
            pmt.setLong(4, idRemisionSap);
            pmt.setLong(5, idTanque);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean("actualizar_estado_remision")) {
                    NovusUtils.printLn("datos actualizados con exito id producto -> " + idProducto + " id tanque -> " + idTanque + " id remision producto -> " + idRemisionProducto + " estado -> " + estado);
                } else {
                    NovusUtils.printLn("error al generar la actualizacion de los datos id producto -> " + idProducto + " id tanque -> " + idTanque + " id remision producto -> " + idRemisionProducto + " estado -> " + estado);
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocuerrido un error inesperado en el metodo "
                    + "public void actualizarEstadoProductoDividido (int estado, long idRemisionProducto, int idProducto, long idRemisionSap, long idTanque) "
                    + "ubicado en la clase -> " + ProductoDivididoDao.class.getName() + " "
                    + "error -> " + e.getMessage());
        }
    }

    public Map<String, Long> obtenerIdTanqueyIdRemisionProducto(int idProducto, long idRemision) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.obtener_tanque_id_remision_id(?, ?);";
        TreeMap<String, Long> idtanqueIdRemisionProducto = new TreeMap<>();
        idtanqueIdRemisionProducto.put("tanque", 0L);
        idtanqueIdRemisionProducto.put("idRemisionProducto", 0L);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, idProducto);
            pmt.setLong(2, idRemision);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                idtanqueIdRemisionProducto.clear();
                idtanqueIdRemisionProducto.put("tanque", rs.getLong("id_tanque"));
                idtanqueIdRemisionProducto.put("idRemisionProducto", rs.getLong("id_remision_producto"));
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocuerrido un error inesperado en el metodo"
                    + " public Map<String, Long> obtenerIdTanqueyIdRemisionProducto(int idProducto, long idRemision)"
                    + " ubicado en la clase -> " + ProductoDivididoDao.class.getName() + " "
                    + " error -> " + e.getMessage());
        }

        return idtanqueIdRemisionProducto;
    }

    public int cantidadDeProductoDividido(int idProducto, long idRemision, long idRemisionProducto) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.contar_productos_remision(?, ?, ?);";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, idProducto);
            pmt.setLong(2, idRemisionProducto);
            pmt.setLong(3, idRemision);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("contar_productos_remision");
            } else {
                return 0;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocuerrido un error inesperado en el metodo"
                    + " public int cantidadDeProductoDividido(int idProducto, long idRemision, long idRemisionProducto)"
                    + " ubicado en la clase -> " + ProductoDivididoDao.class.getName() + " "
                    + " error -> " + e.getMessage());
            return 0;
        }

    }

}

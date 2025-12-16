package com.firefuel.recuperadorMarketCan.dao;

import com.controllers.NovusConstante;
import com.firefuel.Main;
import com.firefuel.recuperadorMarketCan.dto.Movimiento;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SincronizadorMarketCanDAO {

    /*public Long getJornadaActual() {
        long jornada = 0;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from public.fnc_consultar_jornada_activa() jornada;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                jornada = rs.getLong("jornada");
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return jornada;
    }*/

    public TreeMap<Long, Movimiento> getIdsMovimientosRegistry(long jornada) {
        TreeMap<Long, Movimiento> movimientosRegistry = new TreeMap<>();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = " select * from movimientos m where m.grupo_jornada_id = ?;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, jornada);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Movimiento movimiento = new Movimiento();
                movimiento.setId(rs.getLong("id"));
                movimiento.setOperacion(rs.getInt("operacion"));
                movimiento.setConsecutivo(rs.getLong("consecutivo"));
                movimiento.setConsecutivoPrefijo(rs.getString("consecutivo_prefijo"));
                movimiento.setPersonaId(rs.getLong("persona_id"));
                movimiento.setPersonaNit(rs.getLong("persona_nit"));
                movimiento.setPersonaNombre(rs.getString("persona_nombre"));
                movimientosRegistry.put(movimiento.getId(), movimiento);
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return movimientosRegistry;
    }

    /*public boolean existeMovimientoCore(long idMovimiento) {
        boolean existe = false;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select true existe from ct_movimientos cm where cm.remoto_id = ?";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idMovimiento);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                existe = rs.getBoolean("existe");
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return existe;
    }*/

    /*public JsonObject infoMovimientoTransmision(String prefijo, long consecutivo) {
        JsonObject infoMovimiento = new JsonObject();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "select * from public.fnc_consultar_movimiento_transmision(?,?) respuesta;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, prefijo);
            pst.setLong(2, consecutivo);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                infoMovimiento = Main.gson.fromJson(rs.getString("respuesta"), JsonObject.class);
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return infoMovimiento;
    }*/

    /*public JsonObject infoMovimientoGenerada(long idMovimiento) {
        JsonObject infoGenerada = new JsonObject();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "select * from fnc_obtener_movimiento_transaccion(?) respuesta;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idMovimiento);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                infoGenerada = Main.gson.fromJson(rs.getString("respuesta"), JsonObject.class);
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return infoGenerada;
    }*/

}
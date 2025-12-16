package com.firefuel.controlImpresion.dao;

import com.controllers.NovusConstante;
import com.firefuel.Main;
import com.firefuel.controlImpresion.dto.Venta;
import com.application.commons.db_utils.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlImpresionDao {
    
    public TreeMap<Long, Venta> ventasPedientesImpresion() {
        TreeMap<Long, Venta> ventasPendientesImpresion = new TreeMap<>();
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            String sql = "select * from public.fnc_obtener_ventas_pendientes_impresion('15 minutes'::interval);";
            
            // Usar DatabaseConnectionManager para crear recursos
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSCORE, sql);
            
            // Ejecutar consulta usando DatabaseConnectionManager
            DatabaseConnectionManager.executeQuery(resources);
            
            while (resources.getResultSet().next()) {
                Venta venta = new Venta();
                venta.setId(resources.getResultSet().getLong("id"));
                venta.setFecha(resources.getResultSet().getTimestamp("fecha"));
                venta.setPlaca(resources.getResultSet().getString("placa"));
                ventasPendientesImpresion.put(venta.getId(), venta);
            }
        } catch (SQLException s) {
            Logger.getLogger(ControlImpresionDao.class.getName()).log(Level.SEVERE, null, s);
        } finally {
            // Cerrar recursos usando DatabaseConnectionManager
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return ventasPendientesImpresion;
    }
    
    public int tiempoImpresionFE(String parametro) {
        int tiempo = 40;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select wp.valor from wacher_parametros wp where wp.codigo = ?;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, parametro);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tiempo = rs.getInt("valor");
            }
        } catch (SQLException s) {
            Logger.getLogger(ControlImpresionDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return tiempo;
    }
    
    public void actualizarEstadoImpresion(long idVenta) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "update public.ct_movimientos set pendiente_impresion = false where id = ?;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idVenta);
            pst.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(ControlImpresionDao.class.getName()).log(Level.SEVERE, null, s);
        }
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.firefuel.datafonos.EstadosTransaccionVenta;
import com.firefuel.datafonos.TransaccionOperacion;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

/**
 *
 * @author Devitech
 */
public class DatafonosDao {

    private static final String MENSAJE = "mensaje";
    private static final String CERRAR_TURNO = "cerrarTurno";

    public JsonObject buscarInfoDatafono(long idMovimiento, long idMedioPago, String voucher) {
        JsonObject data = null;
        String sql = "select d.codigo_terminal, a.descripcion, ts.id_promotor, "
                + " ts.id_equipo, d.id_adquiriente, ts.id_transaccion"
                + " from datafonos.transacciones ts "
                + " inner join datafonos.datafonos d on d.id_datafono = ts.id_datafono "
                + " inner join datafonos.adquirientes a on d.id_adquiriente = a.id_adquiriente "
                + " where ts.id_movimiento = ? and ts.id_medio_pago = ?"
                + " and ts.id_transaccion_operacion  = ?"
                + " and ts.id_transaccion_estado = ? ";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            pmt.setLong(2, idMedioPagoVentas(idMovimiento, idMedioPago, voucher));
            pmt.setLong(3, TransaccionOperacion.PAGO.getEstadoTransaccionOperacion());
            pmt.setLong(4, EstadosTransaccionVenta.COMPLETADO.getEstadoVentaTransaccion());
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                data = new JsonObject();
                data.addProperty(NovusConstante.CODIGO_DATAFONO, rs.getString("codigo_terminal"));
                data.addProperty(NovusConstante.PROVEEDOR, rs.getString("descripcion"));
                data.addProperty(NovusConstante.PROMOTOR, rs.getLong("id_promotor") + "");
                data.addProperty(NovusConstante.POS, rs.getLong("id_equipo") + "");
                data.addProperty(NovusConstante.PROVEEDOR_ID, rs.getLong("id_adquiriente"));
                data.addProperty(NovusConstante.TRANSACCION_DATAFONO, rs.getLong("id_transaccion"));
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido en error inisperado en el metodo buscarInfoDatafono(long idMovimiento) "
                    + DatafonosDao.class.getName() + " " + e);
        }
        return data;
    }

    public boolean pagoconfirmadoSinAnular(long idMovimiento, long idMedioPago, String voucher) {
        String sql = "select * from datafonos.transacciones t\n"
                + "where t.id_movimiento = ? "
                + " and t.id_medio_pago = ? "
                + " and t.id_transaccion_estado = ? "
                + " and t.id_transaccion_operacion = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            pmt.setLong(2, idMedioPagoVentas(idMovimiento, idMedioPago, voucher));
            pmt.setInt(3, EstadosTransaccionVenta.COMPLETADO.getEstadoVentaTransaccion());
            pmt.setInt(4, TransaccionOperacion.PAGO.getEstadoTransaccionOperacion());
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido en error inisperado en el metodo pagoconfirmadoSinAnular(long idMovimiento, String numeroRecibo) "
                    + DatafonosDao.class.getName() + " " + e.getMessage());
            return false;
        }
    }

    public boolean hayAnulacionEncurso(long idMovimiento, long idMedioPago, String voucher) {
        String sql = "select * from datafonos.transacciones t "
                + " where t.id_transaccion_padre = ? and id_transaccion_estado  = ?"
                + " and t.id_movimiento = ? and id_transaccion_operacion = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idTransaccionDatafono(idMovimiento, idMedioPago, voucher));
            pmt.setInt(2, EstadosTransaccionVenta.PENDIENTE_ANULACION.getEstadoVentaTransaccion());
            pmt.setLong(3, idMovimiento);
            pmt.setInt(4, TransaccionOperacion.ANULACION.getEstadoTransaccionOperacion());
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido en error inisperado en el metodo hayAnulacionEncurso(long idMovimiento, String numeroRecibo) "
                    + DatafonosDao.class.getName() + " " + e.getMessage());
            return false;
        }
    }

    // public boolean hayAnulacionesEncursoEnGeneral(long idMovimiento) {
    //     String sql = "select * from datafonos.transacciones t "
    //             + "where t.id_movimiento = ? "
    //             + "and t.id_transaccion_operacion = ? "
    //             + "and t.id_transaccion_estado = ?";
    //     Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
    //     try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
    //         pmt.setLong(1, idMovimiento);
    //         pmt.setInt(2, TransaccionOperacion.ANULACION.getEstadoTransaccionOperacion());
    //         pmt.setInt(3, EstadosTransaccionVenta.PENDIENTE_ANULACION.getEstadoVentaTransaccion());
    //         ResultSet rs = pmt.executeQuery();
    //         return rs.next();
    //     } catch (SQLException e) {
    //         NovusUtils.printLn("ha ocurrido un error inisperado en le metodo hayAnulacionesEncursoEnGeneral(long idMovimiento) " + e.getMessage());
    //         return false;
    //     }
    // }

    private long idTransaccionDatafono(long idMovimiento, long idMedioPago, String voucher) {
        String sql = "select id_transaccion  from datafonos.transacciones t"
                + " where t.id_medio_pago = ?"
                + " and t.id_movimiento = ?"
                + " and t.id_transaccion_operacion = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMedioPagoVentas(idMovimiento, idMedioPago, voucher));
            pmt.setLong(2, idMovimiento);
            pmt.setLong(3, TransaccionOperacion.PAGO.getEstadoTransaccionOperacion());
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id_transaccion");
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error el en metodo idTransaccionDatafono(String numeroRecibo) "
                    + DatafonosDao.class.getName() + " " + e.getMessage());
            return 0L;
        }
        return 0L;
    }

    private long idMedioPagoVentas(long idMovimiento, long idMedioPago, String voucher) {
        String sql = "select id from ct_movimientos_medios_pagos cmmp"
                + " where cmmp.ct_movimientos_id = ?"
                + " and cmmp.ct_medios_pagos_id = ?"
                + " and cmmp.numero_comprobante = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            pmt.setLong(2, idMedioPago);
            pmt.setString(3, voucher);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error en la consulta idMedioPago(long idMovimiento, long idMedioPago) " + e.getMessage());
            return 0L;
        }
        return 0L;
    }

    public JsonObject advertenciaDeNotificacionDeCierreTurno() {
        JsonObject data = new JsonObject();
        String sql = "select true as cierre from lt_horarios lh "
                + " where ((extract(EPOCH from ( lh.hora_fin - now()::time )) / 60)) >0"
                + " and  ((extract(EPOCH from (lh.hora_fin - now()::time)) / 60)) <= 5;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                data.addProperty(CERRAR_TURNO, rs.getBoolean("cierre"));
                data.addProperty(MENSAJE, "NO SE PUEDE HACER EL PROCESO DE ANULACION POR QUE EL CIERRE DE TURNO SE ENCUENTRA PROXIMO");
            } else {
                data.addProperty(CERRAR_TURNO, Boolean.FALSE);
                data.addProperty(MENSAJE, "no hay cieres pendientes");
            }
        } catch (SQLException e) {
            data.addProperty(CERRAR_TURNO, Boolean.FALSE);
            data.addProperty(MENSAJE, "ha ocurrrido un error inisperado");
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo de advertenciaDeNotificacionDeCierreTurno() " + e.getMessage());
        }
        return data;
    }

    public String fechaTransccionVentaDatafono(long idMovimiento) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String sql = "select cm.fecha from ct_movimientos cm where cm.id = ?";
        String fecha = "";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                fecha = sdf2.format(rs.getTimestamp("fecha"));
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo de dateFechaTransccionVentaDatafono(long idMovimiento) " + e.getMessage());
        }
        return fecha;
    }

    public float cantidadVenta(long idMovimiento) {
        String sql = "select cantidad  from ct_movimientos_detalles cmd where cmd.movimientos_id = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getFloat("cantidad");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo cantidadVenta(long idMovimiento) " + e.getMessage());
            return 0;
        }
    }

    /*public boolean hayVentasDatafonsEnCurso(String codigoTerminal) {
        String sql = "select * from datafonos.transacciones t where t.id_transaccion_estado in (?, ?)\n"
                + "and t.id_datafono = (select d.id_datafono  from datafonos.datafonos d where d.codigo_terminal = ?);";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, EstadosTransaccionVenta.PENDIENTE.getEstadoVentaTransaccion());
            pmt.setInt(2, EstadosTransaccionVenta.POR_ENVIAR.getEstadoVentaTransaccion());
            pmt.setString(3, codigoTerminal);
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado al momento de consultar las ventas en curso en el metodo hayVentasDatafonsEnCurso()" + e.getMessage());
            return false;
        }
    }*/

    /*public boolean hayVentasEncursoDatafonesStatusPump(JsonObject datafono) {
        String sql = "select * from ventas_curso vc where vc.atributos::json->'datafono'->>'proveedor' = ? and vc.atributos::json->'datafono'->>'codigoDatafono' = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, datafono.get("proveedor").getAsString());
            pmt.setString(2, datafono.get("codigoDatafono").getAsString());
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo hayVentasEncursoDatafonesStatusPump(JsonObject datafono) al consultar la ventas en curso datafono " + e.getMessage());
            return false;
        }
    }*/

}

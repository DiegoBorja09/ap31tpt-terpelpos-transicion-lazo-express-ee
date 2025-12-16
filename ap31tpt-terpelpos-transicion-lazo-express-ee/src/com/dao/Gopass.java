/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.bean.TransaccionGopass;
import com.bean.VentaGo;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.WT2.goPass.infrastructure.db.repositories.GopassParametersRepository;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Since;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

/**
 *
 * @author Devitech
 */
public class Gopass {

    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    public int getDays() throws DAOException {
        int days = Main.getParametroIntCore(NovusConstante.PREFERENCE_LIMITE_REPORTE_GOPASS, false);
        if (days == -1) {
            days = 7;
        }
        return days;
    }

    public ArrayList<TransaccionGopass> getTransaccionesGoPass() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ArrayList<TransaccionGopass> transacciones = new ArrayList<>();
        try {
//            String sql = "select * from transacciones_gopass tg where tg.fecha between (now() - '" + getDays()
//                    + " days'::interval) and now() order by tg.fecha desc";

            String sql = "select * from public.fnc_recuperar_ventas_gopass(" + getDays() + ")";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                TransaccionGopass e = new TransaccionGopass();
                e.setIdentificadortransacciongopass(re.getLong("identificadortransacciongopass"));
                e.setIdentificacionpromotor(re.getString("identificacionpromotor"));
                e.setCodigoeds(re.getString("codigoeds"));
                e.setIsla(re.getInt("isla"));
                e.setSurtidor(re.getInt("surtidor"));
                e.setCara(re.getInt("cara"));
                e.setPlaca(re.getString("placa"));
                e.setTaggopass(null);
                e.setIdentificadorventaterpel(re.getLong("identificadorventaterpel"));
                e.setValor(re.getInt("valor"));
                e.setEstado(re.getString("estado"));
                e.setFecha(re.getString("fecha"));
                e.setIdentificadorMovimiento(re.getLong("idmovimiento"));
                transacciones.add(e);
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());

        }
        return transacciones;
    }

    public ArrayList<VentaGo> getVentas() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ArrayList<VentaGo> ventas = new ArrayList<>();
        try {
            String sql = "select c.id , c.fecha, c.venta_total, c.atributos, c.consecutivo, C.prefijo, C.atributos::json->>'cara' as cara, cmd.cantidad ,\n"
                    + "cmd.precio as precio_producto , p.descripcion, ttp.id_estado_integracion, tep.descripcion \n"
                    + " from (select c.*, row_number() over (partition by c.atributos::json->>'cara' order by c.fecha desc) as rn from ct_movimientos c) c\n"
                    + "left join ct_movimientos_detalles cmd on C.id = cmd.movimientos_id\n"
                    + "left join productos p on cmd.productos_id = p.id\n"
                    + "left join ct_movimientos_medios_pagos cmmp on cmmp.ct_movimientos_id = c.id \n"
                    + "left join ct_medios_pagos cmp on cmp.id = cmmp.ct_medios_pagos_id \n"
                    + "left join (select * from procesos.tbl_transaccion_proceso ttp \n"
                    + "	where ttp.id_integracion  =2 \n"
                    + "	and ttp.id_estado_integracion in (4,5,3)) ttp on ttp.id_movimiento = c.id\n"
                    + "left join procesos.tbl_estados_procesos tep on ttp.id_estado_proceso = tep.id_estado_proceso\n"
                    + "where c.rn <= 6 and C.JORNADAS_ID = (SELECT GRUPO_JORNADA FROM JORNADAS LIMIT 1) and  (C.atributos::json->>'gopass' isnull or  ttp.id_estado_integracion in (4,5,3) )\n"
                    + "and C.fecha between (now() - '60 minutes'::interval) and now()\n"
                    + "and C.atributos::json->>'cara' in (select cara::text from surtidores_detalles group by cara )\n"
                    + "and C.tipo = '017'\n"
                    + "and C.estado_movimiento = '017000'\n"
                    + "and C.atributos::json->>'isCredito'='false'\n"
                    + "and cmp.id = 1 \n"
                    + "order by C.atributos::json->>'cara', C.fecha desc nulls last;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            System.out.println("CONSULTA GOPASS VENTAS: " + sql);
            while (re.next()) {
                VentaGo venta = new VentaGo();
                venta.setId(re.getInt("id"));
                venta.setFecha(re.getString("fecha"));
                venta.setVentaTotal("$ " + df.format(re.getInt("venta_total")));
                JsonObject atributos = new Gson().fromJson(re.getString("atributos"), JsonObject.class);
                if (!atributos.isJsonNull()) {
                    venta.setAtributos(atributos);
                }
                venta.setConsecutivo(re.getInt("consecutivo"));
                venta.setCantidad(re.getDouble("cantidad"));
                venta.setPrecio_producto("$ " + df.format(re.getDouble("precio_producto")));
                venta.setDescription(re.getString("descripcion"));
                venta.setPrefijo(re.getString("prefijo"));
                ventas.add(venta);

            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (JsonSyntaxException s) {
            throw new DAOException("33." + s.getMessage());
        } finally {
            // Cerrar conexion para evitar memory leaks
            if (conexion != null) {
                try {
                    if (!conexion.isClosed()) {
                        conexion.close();
                        //NovusUtils.printLn("CONEXION CERRADA: Gopass.getVentas()");
                    }
                } catch (SQLException e) {
                    Logger.getLogger(Gopass.class.getName()).log(Level.SEVERE, "Error cerrando conexion en getVentas", e);
                }
            }
        }
        return ventas;
    }

    public boolean validarReintentosCobroGopass(long idMovimiento){
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean puedeReintentar = false;
        try {
            String sql = "SELECT \n"
                    + "COALESCE(BOOL_AND(\n"
                    + "        (\n"
                    + "            SELECT \n"
                    + "                COALESCE(reapertura, 0) < ".concat(String.valueOf(NovusConstante.GOPASS_CANTIDAD_MAXIMA_REINTENTOS_PAGOS))
                    + "            FROM procesos.tbl_transaccion_proceso ttp \n"
                    + "            WHERE ttp.id_movimiento = ? AND ttp.id_integracion = 2\n"
                    + "        )\n"
                    + "    ), TRUE) AS reintentos_validos";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idMovimiento);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                puedeReintentar = re.getBoolean("reintentos_validos");
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error en la clase Gopass -> Causa: "+ e.getMessage());
            return puedeReintentar;
        }
        return puedeReintentar;
    }

    public boolean actualizarAtributos(int id, int GopassID, PlacaGopass placa) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean actualizado = false;
        try {
            String sql = "SELECT ATRIBUTOS FROM CT_MOVIMIENTOS WHERE id=" + id + " LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                JsonObject atributos = new Gson().fromJson(re.getString("atributos"), JsonObject.class);
                if (!atributos.isJsonNull()) {
                    atributos.addProperty("gopass", GopassID);
                    atributos.addProperty("vehiculo_placa", placa.getPlaca());
                    atributos.addProperty("personas_nombre", placa.getNombreUsuario());

                    sql = "UPDATE CT_MOVIMIENTOS SET estado='M', atributos=" + "'" + atributos + "'" + "WHERE id= ?";
                    ps = conexion.prepareStatement(sql);
                    ps.setInt(1, id);
                    actualizado = ps.executeUpdate() != 0;
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (JsonSyntaxException s) {
            throw new DAOException("33." + s.getMessage());
        }
        return actualizado;
    }

    public boolean actualizarMediosPago(int id, String gopassID) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean actualizado = false;
        try {
            String sql = "UPDATE ct_movimientos_medios_pagos "
                    + "SET ct_medios_pagos_id=(select id from medios_pagos mp where mp.mp_atributos::json->>'codigoExterno' notnull and mp.mp_atributos::json->>'codigoExterno'='238' limit 1), numero_comprobante=?"
                    + " WHERE ct_movimientos_id=?;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, gopassID);
            ps.setInt(2, id);
            actualizado = ps.executeUpdate() != 0;
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return actualizado;
    }
    
    public boolean existeGopass() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean exist = false;
        try {
            String sql = "select count(*) from medios_pagos mp \n"
                    + "where mp.mp_atributos::json->>'codigoExterno' is not null \n"
                    + "and mp.mp_atributos::json->>'codigoExterno'='238' \n"
                    + "limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            exist = ps.executeQuery().next();
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return exist;
    }

    public boolean isDobleImpresionGopass() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean result = false;
        try {
            try {
                String sql = "SELECT * FROM wacher_parametros WHERE CODIGO = ? AND VALOR = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, "DOBLE_IMPRESION_GOPASS");
                ps.setString(2, "S");
                result = ps.executeQuery().next();
            } catch (PSQLException s) {
                throw new DAOException("31." + s.getMessage());
            } catch (SQLException s) {
                throw new DAOException("32." + s.getMessage());
            } catch (Exception s) {
                throw new DAOException("33." + s.getMessage());
            }
        } catch (Exception e) {
        }
        return result;
    }
}

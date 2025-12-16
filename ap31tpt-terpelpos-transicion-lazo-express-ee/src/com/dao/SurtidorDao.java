/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.domain.entity.TransaccionProcesParams;
import com.bean.PersonaBean;
import com.bean.RecepcionBean;
import com.bean.ReciboExtended;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neo.app.bean.Manguera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

/**
 *
 * @author usuario
 */
public class SurtidorDao {

    private static final int FIDELIZACION = 1;
    private static final int PLACA = 2;
    private static final int FACTURA_ELECTRONICA = 3;
    private static final int REMISION = 4;
    private static final int DATAFONO = 5;

    public JsonArray searchMisfitHoses() throws DAOException {
        JsonArray misfitHoses = null;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "select coalesce((SELECT array_to_json(array_agg(row_to_json(t))) FROM ( \n"
                    + "           SELECT sd.id, sd.manguera, sd.cara,sd.surtidor,\n"
                    + "			p.descripcion as producto, s.factor_inventario,coalesce(sd.acumulado_cantidad,0) as acumulado_cantidad,\n"
                    + "			coalesce(sd.acumulado_cantidad_surt,0) as acumulado_cantidad_surt, p.precio\n"
                    + "			FROM surtidores_detalles AS sd \n"
                    + "			inner join surtidores s on s.surtidor = sd.surtidor\n"
                    + "			inner join productos p on p.id = sd.productos_id\n"
                    + "			WHERE salto_lectura = 'S'\n"
                    + ") t),'[]') as misfit_hoses_array";

            PreparedStatement ps;
            ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                JsonArray data = new Gson().fromJson(re.getString("misfit_hoses_array"), JsonArray.class);
                misfitHoses = data.size() > 0 ? data : null;
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return misfitHoses;
    }

    public void marcadoVentasCombustibles(int manguera) throws DAOException {
        SimpleDateFormat x = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        class Inner {

            void crearTabla() throws SQLException {
                String sql = "CREATE TABLE IF NOT EXISTS public.facturas_suspendidas (\n"
                        + "	manguera int NOT NULL,\n"
                        + "	usado char NOT NULL,\n"
                        + "	fecha varchar NOT NULL,\n"
                        + "	CONSTRAINT facturas_suspendidas_pk PRIMARY KEY (manguera)\n"
                        + ");";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void marcarVentaParaProcesarCanastilla() throws SQLException {
                String sql = "INSERT INTO facturas_suspendidas(manguera, usado, fecha )\n"
                        + "    VALUES (?, 'N', ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, manguera);
                ps.setString(2, x.format(new Date()));
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        try {
            innerCall.crearTabla();
            innerCall.marcarVentaParaProcesarCanastilla();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        }

    }

    public boolean crearPrefacturacion(PersonaBean persona, Manguera manguera, Long medioId) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean result = false;
        try {
            boolean facturaRegistrada = false;
            String sql = "select * from transacciones where surtidor =? and cara =? and usado is null";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, manguera.getSurtidor());
            ps.setInt(2, manguera.getCara());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                facturaRegistrada = true;
            }

            if (!facturaRegistrada) {

                int PROVEEDOR_GUROSOFT = 3;
                sql = "INSERT INTO transacciones\n" + "(" + "codigo, surtidor, cara, grado, proveedores_id, "
                        + "preventa, estado, documento_identificacion_cliente, documento_identificacion_conductor, placa_vehiculo, "
                        + "precio_unidad, porcentaje_descuento_cliente, monto_maximo, cantidad_maxima, usado, "
                        + "fecha_servidor, fecha_creacion, fecha_uso, metodo_pago, medio_autorizacion, "
                        + "serial_dispositivo, conductor_nombre, cliente_nombre, vehiculo_odometro, trama, "
                        + "codigo_tercero, transaccion_sincronizada)\n" + "VALUES(" + "?, ?, ?, ?, ?,"
                        + "true, 'A', ?, null, null," + "null, 0, 0, null, null,"
                        + "now(), now(), null, ?, 'electronica', " + "'FAC. ELECTRONICA', null, ?, null, ?::json,"
                        + "null, 'N');";
                ps = conexion.prepareStatement(sql);

                ps.setString(1, UUID.randomUUID().toString());
                ps.setInt(2, manguera.getSurtidor());
                ps.setInt(3, manguera.getCara());
                ps.setInt(4, manguera.getGrado());
                ps.setInt(5, PROVEEDOR_GUROSOFT);
                ps.setString(6, persona.getIdentificacion());
                if (medioId == null) {
                    ps.setNull(7, Types.NULL);
                } else {
                    ps.setLong(7, medioId);
                }
                ps.setString(8, persona.getNombre());
                ps.setString(9, persona.getAtributos().toString());

                ps.executeUpdate();

                result = true;

            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
        return result;
    }

    public boolean crearAutorizacionPorPlaca(PersonaBean persona, Surtidor manguera, String placa, String odometro,
                                             JsonObject trama, boolean HABILITAR_CONSULTA_SICOM) throws DAOException {
        return crearAutorizacionPorPlaca(persona, manguera, placa, odometro, trama, HABILITAR_CONSULTA_SICOM, null, null);
    }
    
    public boolean crearAutorizacionPorPlaca(PersonaBean persona, Surtidor manguera, String placa, String odometro,
                                             JsonObject trama, boolean HABILITAR_CONSULTA_SICOM, 
                                             String montoManual, String cantidadManual) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        boolean result = false;
        long ventaMaxima = 0l;
        boolean facturaRegistrada = false;
        String placaVehiculo = null;

        try {
            
            String sql = "select * from transacciones where surtidor =? and cara =? and usado is null";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, manguera.getSurtidor());
            ps.setInt(2, manguera.getCara());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                facturaRegistrada = true;
                placaVehiculo = re.getString("placa_vehiculo");
            }

            if (!facturaRegistrada) {

                int PROVEDOR_DEVITECH = 1;
                sql = "INSERT INTO transacciones (codigo, surtidor, cara, grado, proveedores_id, "
                        + "preventa, estado, documento_identificacion_cliente, documento_identificacion_conductor, placa_vehiculo, "
                        + "precio_unidad, porcentaje_descuento_cliente, monto_maximo, cantidad_maxima, usado, "
                        + "fecha_servidor, fecha_creacion, fecha_uso, metodo_pago, medio_autorizacion, "
                        + "serial_dispositivo, conductor_nombre, cliente_nombre, vehiculo_odometro, trama, "
                        + "codigo_tercero, transaccion_sincronizada, promotor_id) "
                        + "VALUES(?, ?, ?, ?, ?," 
                        + "true, 'A', ?, null, ?, " 
                        + "?, 0, ?, ?, null," 
                        + "now(), now(), null, ?, ?, "
                        + "?, null, ?, ?, ?::json,"
                        + "null, 'N', ?);";
                ps = conexion.prepareStatement(sql);
                int MEDIO_PAGO = 1;
                if (HABILITAR_CONSULTA_SICOM) {
                    trama.addProperty("tipoVenta", -1);
                    trama.addProperty("isCredito", false);
                    trama.addProperty("vehiculo_odometro", odometro);
                    trama.addProperty("documentoIdentificacionCliente", "");
                    trama.addProperty("nombreCliente", "");
                    trama.addProperty("medioAutorizacion", "");
                    trama.addProperty("serialDispositivo", "");
                } else {
                    MEDIO_PAGO = 10000;
                    if (trama.has("es_comunidades") && trama.get("es_comunidades").getAsBoolean()) {
                        trama.addProperty("tipoVenta", -1);
                        trama.addProperty("isCredito", false);
                        trama.addProperty("isComunidades", true);
                        trama.addProperty("vehiculo_odometro", odometro);
                    } else {
                        trama.addProperty("tipoVenta", 10);
                        trama.addProperty("isComunidades", false);
                        trama.addProperty("isCredito", true);
                    }

                }

                int saldo = (int) trama.get("saldo").getAsDouble();

                ps.setString(1, UUID.randomUUID().toString());
                ps.setInt(2, manguera.getSurtidor());
                ps.setInt(3, manguera.getCara());
                ps.setInt(4, manguera.getGrado());
                ps.setInt(5, PROVEDOR_DEVITECH);
                ps.setString(6, trama.get("documentoIdentificacionCliente").getAsString());
                ps.setString(7, placa);

                float precioUnidad = manguera.getProductoPrecio();
                ps.setFloat(8, precioUnidad);
                NovusUtils.printLn("[crearAutorizacionPorPlaca] Guardando precio_unidad: " + precioUnidad);

                ventaMaxima = getVentaMaxima(manguera.getSurtidor());
                NovusUtils.printLn("Manguera Surtidor ::::" + manguera.getSurtidor());
                NovusUtils.printLn("Datos ::::" + ventaMaxima);
                NovusUtils.printLn("Surtidor:::" + manguera.toString());

                // NOTA: El montoManual ya no se usa directamente aquí, se convierte a cantidad en VentaPredefinirPlaca
                // Este bloque se mantiene por compatibilidad pero no debería ejecutarse en flujos de clientes propios
                if (montoManual != null && !montoManual.trim().isEmpty()) {
                    try {
                        // Convertir monto a cantidad (galones) para guardar con monto=0 y cantidad=conversión
                        double monto = Double.parseDouble(montoManual.trim());
                        double precioProducto = manguera.getProductoPrecio();
                        
                        if (precioProducto > 0) {
                            // Validar que el monto no exceda el saldo - SI EXCEDE, LANZAR ERROR
                            if ((int) monto > saldo) {
                                String mensajeError = "Monto ingresado (" + (int) monto + ") excede el saldo disponible (" + saldo + ")";
                                NovusUtils.printLn("[crearAutorizacionPorPlaca] ERROR: " + mensajeError);
                                throw new DAOException("MONTO_EXCEDE_SALDO:" + mensajeError);
                            }
                            
                            // Calcular cantidad equivalente (mantener decimales)
                            double cantidad = monto / precioProducto;
                            // Redondear a 4 decimales
                            cantidad = Math.round(cantidad * 10000.0) / 10000.0;
                            
                            // Guardar con monto=0 y cantidad=conversión (con 4 decimales máximo)
                            ps.setInt(9, 0); // monto_maximo (índice ajustado: antes 8, ahora 9)
                            ps.setDouble(10, cantidad); // cantidad_maxima (índice ajustado: antes 9, ahora 10)
                            NovusUtils.printLn("[crearAutorizacionPorPlaca] Monto " + monto + " convertido a " + cantidad + " galones. Guardando MONTO: 0, CANTIDAD: " + cantidad);
                        } else {
                            NovusUtils.printLn("[crearAutorizacionPorPlaca] ERROR: Precio del producto es 0, usando lógica por defecto");
                            if (saldo >= ventaMaxima) {
                                int cantidadMaxima = saldo / (int) manguera.getProductoPrecio();
                                ps.setInt(9, 0);
                                ps.setInt(10, cantidadMaxima);
                            } else {
                                ps.setInt(9, saldo);
                                ps.setInt(10, 0);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Si hay error en la conversión, usar lógica por defecto
                        NovusUtils.printLn("[crearAutorizacionPorPlaca] Error al convertir monto: " + montoManual);
                        if (saldo >= ventaMaxima) {
                            int cantidadMaxima = saldo / (int) manguera.getProductoPrecio();
                            ps.setInt(9, 0);
                            ps.setInt(10, cantidadMaxima);
                        } else {
                            ps.setInt(9, saldo);
                            ps.setInt(10, 0);
                        }
                    }
                } else if (cantidadManual != null && !cantidadManual.trim().isEmpty()) {
                    try {
                        double cantidad = Double.parseDouble(cantidadManual.trim()); // Mantener decimales
                        // Redondear cantidad ingresada a 4 decimales
                        cantidad = Math.round(cantidad * 10000.0) / 10000.0;
                        double precioProducto = manguera.getProductoPrecio();
                        double montoEquivalente = cantidad * precioProducto;
                        
                        double cantidadFinal;
                        if (montoEquivalente > saldo) {
                            cantidadFinal = saldo / precioProducto;
                            // Redondear a 4 decimales
                            cantidadFinal = Math.round(cantidadFinal * 10000.0) / 10000.0;
                            NovusUtils.printLn("[crearAutorizacionPorPlaca] ADVERTENCIA: Cantidad ingresada (" + cantidad + ") genera monto (" + montoEquivalente + ") que excede el saldo disponible (" + saldo + "). Se limitará a: " + cantidadFinal);
                        } else {
                            cantidadFinal = cantidad;
                        }
                        ps.setInt(9, 0); // monto_maximo (índice ajustado: antes 8, ahora 9)
                        ps.setDouble(10, cantidadFinal); // cantidad_maxima (índice ajustado: antes 9, ahora 10)
                        NovusUtils.printLn("[crearAutorizacionPorPlaca] Guardando CANTIDAD: " + cantidadFinal + " (ingresada: " + cantidad + ", monto equivalente: " + montoEquivalente + ", saldo disponible: " + saldo + ")");
                    } catch (NumberFormatException e) {
                        NovusUtils.printLn("[crearAutorizacionPorPlaca] Error al convertir cantidad: " + cantidadManual);
                        if (saldo >= ventaMaxima) {
                            int cantidadMaxima = saldo / (int) manguera.getProductoPrecio();
                            ps.setInt(9, 0);
                            ps.setInt(10, cantidadMaxima);
                        } else {
                            ps.setInt(9, saldo);
                            ps.setInt(10, 0);
                        }
                    }
                } else {
                    if (saldo >= ventaMaxima) {
                        int cantidadMaxima = saldo / (int) manguera.getProductoPrecio();
                        ps.setInt(9, 0); // monto_maximo (índice ajustado: antes 8, ahora 9)
                        ps.setInt(10, cantidadMaxima); // cantidad_maxima (índice ajustado: antes 9, ahora 10)
                    } else {
                        ps.setInt(9, saldo); // monto_maximo (índice ajustado: antes 8, ahora 9)
                        ps.setInt(10, 0); // cantidad_maxima (índice ajustado: antes 9, ahora 10)
                    }
                }
                ps.setInt(11, trama.has("es_comunidades") && trama.get("es_comunidades").getAsBoolean() ? 1 : MEDIO_PAGO); // metodo_pago (índice ajustado: antes 10, ahora 11)
                ps.setString(12, trama.get("medioAutorizacion").getAsString()); // medio_autorizacion (índice ajustado: antes 11, ahora 12)
                ps.setString(13, trama.get("serialDispositivo").getAsString()); // serial_dispositivo (índice ajustado: antes 12, ahora 13)
                ps.setString(14, trama.get("nombreCliente").getAsString()); // cliente_nombre (índice ajustado: antes 13, ahora 14)
                ps.setString(15, odometro); // vehiculo_odometro (índice ajustado: antes 14, ahora 15)
                ps.setString(16, trama.toString()); // trama (índice ajustado: antes 15, ahora 16)
                ps.setLong(17, persona.getId()); // promotor_id (índice ajustado: antes 16, ahora 17)
                ps.executeUpdate();
                result = true;
            }
        } catch (PSQLException s) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException("43." + s.getMessage());
        }
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("facturaRegistrada", facturaRegistrada);
//        resultMap.put("result", result);
//        resultMap.put("placaVehiculo", placaVehiculo);
        return result;
    }

    public Long getVentaMaxima(int surtidor) {
        String codigo = "VENTA_MAXIMA_SURTIDOR_"
                .concat(String.valueOf(surtidor));
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long ventaMaxima = 0l;
        String sql = "select wp.valor "
                + "from wacher_parametros wp where codigo = ?";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ventaMaxima = rs.getLong("valor");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        ventaMaxima = ventaMaxima != 0 ? ventaMaxima : 999999;

        return ventaMaxima;
    }

    public boolean crearAutorizacionTipoVenta(PersonaBean persona, Surtidor manguera, JsonObject atributos, int monto,
                                              int cantidad) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        boolean result = false;
        try {
            String sql = "select * from transacciones where surtidor =? and cara =? and usado is null";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, manguera.getSurtidor());
            ps.setInt(2, manguera.getCara());
            ResultSet re = ps.executeQuery();
            boolean facturaRegistrada = re.next();
            if (!facturaRegistrada) {
                int provedor_devitech = 1;
                sql = "INSERT INTO transacciones (codigo, surtidor, cara, grado, proveedores_id, "
                        + "preventa, estado, documento_identificacion_cliente, documento_identificacion_conductor, placa_vehiculo, "
                        + "precio_unidad, porcentaje_descuento_cliente, monto_maximo, cantidad_maxima, usado, "
                        + "fecha_servidor, fecha_creacion, fecha_uso, metodo_pago, medio_autorizacion, "
                        + "serial_dispositivo, conductor_nombre, cliente_nombre, vehiculo_odometro, trama, "
                        + "codigo_tercero, transaccion_sincronizada, promotor_id) "
                        + "VALUES(?, ?, ?, ?, ?,"
                        + "true, 'A', null, null, null, "
                        + "null, null, ?, ?, null,"
                        + "now(), now(), null, 1, 'especial', "
                        + "null, null, null, null, ?::json,"
                        + "null, 'N', ?);";
                ps = conexion.prepareStatement(sql);

                ps.setString(1, UUID.randomUUID().toString());
                ps.setInt(2, manguera.getSurtidor());
                ps.setInt(3, manguera.getCara());
                ps.setInt(4, manguera.getGrado());
                ps.setInt(5, provedor_devitech);
                ps.setInt(6, monto);
                ps.setInt(7, cantidad);

                ps.setString(8, atributos.toString());
                if (persona != null) {
                    ps.setLong(9, persona.getId());
                } else {
                    ps.setNull(9, Types.NULL);
                }
                ps.executeUpdate();
                result = true;

            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
        return result;
    }

    public int getGradoPorManguera(int manguera) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        int grado = -1;
        String sql = "select grado from surtidores_detalles where manguera = ?";
        PreparedStatement ps;
        try {
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, manguera);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                grado = re.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return grado;
    }

    public int getStatusHose(int manguera) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        int estado = -1;
        try {
            String sql = "select se.tipo from surtidores_detalles sd inner join surtidor_estado se on se.id = sd.estado_publico where manguera = ?";
            PreparedStatement ps;
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, manguera);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                estado = re.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return estado;
    }

    /*public int getTimeoutTotalizadores() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        int timeout = 30000;
        String sql = "select valor from wacher_parametros where codigo = ?";
        PreparedStatement ps;
        try {
            ps = conexion.prepareStatement(sql);
            ps.setString(1, "TIMEOUT_ESPERA_LECTURAS");
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                timeout = Integer.parseInt(re.getString("valor"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return timeout;
    }*/

    /*public JsonArray obtenerInfoSurtidoresEstacion() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonArray infoSurtidoresEstacion = new JsonArray();
        String sql = "select host , isla, equipos_id, COALESCE( atributos::json->>'surtidores' ,'[]') surtidores from surtidores_core";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                JsonObject jsonSurtidor = new JsonObject();
                jsonSurtidor.addProperty("host", re.getString("host"));
                if (re.getLong("equipos_id") == equipoId()) {
                    jsonSurtidor.addProperty("host", "localhost");
                }
                jsonSurtidor.addProperty("isla", re.getLong("isla"));
                JsonArray surtidores = new Gson().fromJson(re.getString("surtidores"), JsonArray.class);
                jsonSurtidor.add("surtidores", surtidores);
                infoSurtidoresEstacion.add(jsonSurtidor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return infoSurtidoresEstacion;
    }*/

    /*public JsonArray obtenerHostSurtidoresEstacion() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        JsonArray infoHostSurtidores = new JsonArray();
        try {
            String sql = "select host , isla, equipos_id from surtidores_core";
            try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
                ResultSet re = ps.executeQuery();
                while (re.next()) {

                    JsonObject jsonSurtidor = new JsonObject();
                    jsonSurtidor.addProperty("host", re.getString("host"));
                    if (re.getLong("equipos_id") == equipoId()) {
                        jsonSurtidor.addProperty("host", "localhost");
                    }
                    jsonSurtidor.addProperty("isla", re.getLong("isla"));
                    infoHostSurtidores.add(jsonSurtidor);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("***************************" + infoHostSurtidores);
        return infoHostSurtidores;
    }*/

    public boolean haySurtidores() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean result = false;
        String sql = "select id from surtidores limit 1";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                result = true;
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return result;
    }

    public long getFamiliaProducto(long id) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long familia = 1;
        try {
            String sql = "select familias from productos where id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                familia = re.getLong(1);
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return familia;
    }

    public long getCodigoProductoViveTerpel(long id) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long codigo = 0;
        NovusUtils.printLn("🔍 Buscando código Salesforce (codigoExterno) para producto ID: " + id);
        try {
            String sql = "SELECT p.p_atributos::json->>'codigoExterno' AS id_Producto " +
                    "FROM productos p WHERE p.id = ? AND p.p_atributos::json->>'codigoExterno' IS NOT NULL LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                String codigoStr = re.getString("id_Producto");
                if (codigoStr != null && !codigoStr.trim().isEmpty()) {
                    try {
                        codigo = Long.parseLong(codigoStr.trim());
                        NovusUtils.printLn("✅ Código Salesforce encontrado: " + codigo);
                    } catch (NumberFormatException nfe) {
                        NovusUtils.printLn("⚠️ El códigoExterno no es un número válido: " + codigoStr);
                    }
                } else {
                    NovusUtils.printLn("⚠️ 'codigoExterno' está vacío para producto ID: " + id);
                }
            } else {
                NovusUtils.printLn("⚠️ No se encontró 'codigoExterno' para el producto ID: " + id);
            }
        } catch (Exception e) {
            NovusUtils.printLn("❌ Error obteniendo código Salesforce para producto ID " + id + ": " + e.getMessage());
        }


        return codigo;
    }

    public long getCodigoSalesForceMP(long id) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long codigo = 0;
        NovusUtils.printLn("📌 Buscando código Salesforce para ID de medio: " + id);

        try {
            String sql = "SELECT mp.mp_atributos::json->>'codigoSalesfore' AS id_salesForce " +
                    "FROM medios_pagos mp " +
                    "WHERE mp.id = ? AND mp.mp_atributos::json->>'codigoSalesfore' IS NOT NULL LIMIT 1";

            NovusUtils.printLn("🧾 SQL Ejecutada: " + sql);

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String codigoStr = rs.getString("id_salesForce");
                NovusUtils.printLn("✅ Código Salesforce encontrado como string: " + codigoStr);

                try {
                    codigo = Long.parseLong(codigoStr);
                    NovusUtils.printLn("🔢 Código Salesforce convertido a long: " + codigo);
                } catch (NumberFormatException ex) {
                    NovusUtils.printLn("⚠️ Error al convertir código Salesforce a long: " + ex.getMessage());
                }

            } else {
                NovusUtils.printLn("⚠️ No se encontró código Salesforce para el medio de pago con ID: " + id);
            }

        } catch (Exception e) {
            NovusUtils.printLn("❌ Error buscando código Salesforce en DB: " + e.getMessage());
        }

        return codigo;
    }




    public void bloqueosSurtidor(TreeMap<Integer, Boolean> bloqueos) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        try {
            for (Map.Entry<Integer, Boolean> bloqueo : bloqueos.entrySet()) {
                String sql = "UPDATE SURTIDORES_DETALLES SET BLOQUEO=?, MOTIVO_BLOQUEO=? WHERE MANGUERA=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                if (!bloqueo.getValue()) {
                    ps.setNull(1, Types.NULL);
                    ps.setNull(2, Types.NULL);
                } else {
                    ps.setString(1, bloqueo.getValue() ? "S" : "N");
                    ps.setString(2, "");
                }
                ps.setLong(3, bloqueo.getKey());

                ps.executeUpdate();
                NovusUtils.printLn(sql + " " + bloqueo.getKey());
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        try {
            for (Map.Entry<Integer, Boolean> bloqueo : bloqueos.entrySet()) {
                String sql = "UPDATE SURTIDORES_DETALLES SET BLOQUEO=?, MOTIVO_BLOQUEO=? WHERE MANGUERA=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                if (!bloqueo.getValue()) {
                    ps.setNull(1, Types.NULL);
                    ps.setNull(2, Types.NULL);
                } else {
                    ps.setString(1, bloqueo.getValue() ? "S" : "N");
                    ps.setString(2, "");
                }
                ps.setLong(3, bloqueo.getKey());

                ps.executeUpdate();
                NovusUtils.printLn(sql + " " + bloqueo.getKey());
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }

    /* METODO NO SE USA NO SE MIGRA 
     public void crearTareaProgramada(ReciboExtended recibo, JsonObject clientJson) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String TAREA_VENTA_FIDELIZADA = "1";
            eliminarTareaCara(Integer.parseInt(recibo.getCara()), TAREA_VENTA_FIDELIZADA);
            String sql = "INSERT INTO public.tareas_pos_ventas\n"
                    + "(cara, manguera, tarea, atributos)\n"
                    + "VALUES(?, ?, ?, ?::json);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(recibo.getCara()));
            ps.setInt(2, Integer.parseInt(recibo.getManguera()));
            ps.setString(3, TAREA_VENTA_FIDELIZADA);
            ps.setString(4, clientJson.toString());

            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/

    //ACTUALIZAR VENTA EN CURSO
    public void updateVentasEncurso(ReciboExtended recibo, JsonObject datos, int tipo) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonObject atributos = new JsonObject();
        int datoAdicioanl = 0;
        String placa = "";
        try {
            String sql = "select * from ventas_curso where cara=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(recibo.getCara()));
            NovusUtils.printLn("esto es la consulta " + ps.toString());
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                if (count > 1) {
                    return;
                }
                if (rs.getString("atributos") != null) {
                    atributos = new Gson().fromJson(rs.getString("atributos"), JsonObject.class);
                }
                datoAdicioanl = rs.getInt("cantidad");
                count++;
            }

            if (atributos.has("DatosFactura")) {
                placa = atributos.get("DatosFactura").getAsJsonObject().get("placa").getAsString();
            }

            switch (tipo) {
                case PLACA:
                    atributos.add("DatosFactura", datos);
                    break;
                case FIDELIZACION:
                    atributos.add("DatosFidelizacion", datos);
                    break;
                case FACTURA_ELECTRONICA:
                    atributos.add("factura_electronica", datos);
                    break;
                case REMISION:

                    datos.addProperty("imprimir", Boolean.TRUE);
                    datos.addProperty("pendiente_impresion", Boolean.FALSE);

                    atributos.add("remision", datos);
                    atributos.addProperty("tipoVenta", 100);
                    break;
                case DATAFONO:
                    atributos.add("datafono", datos);
                    break;
                case NovusConstante.ID_TIPO_VENTA_GOPASS:
                    atributos.add(NovusConstante.PARAMETRO_GOPASS, datos);
                    break;
                case NovusConstante.ID_TIPO_VENTA_APP_TERPEL:
                    atributos.addProperty("isAppTerpel", datos.get("isAppTerpel").getAsBoolean());
                    break;
                default:
            }

            asignarPlaca(atributos, placa);

            if (atributos.has("isAppTerpel") || atributos.has(NovusConstante.PARAMETRO_GOPASS)) {
                aplicarImprimirFalse(atributos,"factura_electronica");
                aplicarImprimirFalse(atributos,"remision");
            }

            atributos.addProperty("statusPump", atributos.has("factura_electronica") && atributos.has("factura_electronica"));
            String updtate = "update ventas_curso set atributos=?::json where cara=? ";
            PreparedStatement pst = conexion.prepareStatement(updtate);
            pst.setString(1, atributos.toString());
            pst.setInt(2, Integer.parseInt(recibo.getCara()));
            NovusUtils.printLn("esto es la consulta " + pst.toString());
            int res = pst.executeUpdate();
            if (res == 1) {
                NovusUtils.printLn(Main.ANSI_GREEN + "Datos actualizados" + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "Error al generar la actualizacion" + Main.ANSI_RESET);
                if (atributos.has("datafono")) {
                    eleminarltVentasCurso(atributos.get("datafono").getAsJsonObject().get("codigoAutorizacion").getAsLong());
                }

            }

        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "error: " + e + Main.ANSI_RESET);
        }

        NovusUtils.printLn(Main.ANSI_BLUE + "Atributos de la venta -> " + atributos + Main.ANSI_RESET);
        NovusUtils.printLn(Main.ANSI_BLUE + "Datos Adicionales -> " + datoAdicioanl + Main.ANSI_RESET);
        NovusUtils.printLn(Main.ANSI_BLUE + "Datos ->" + datos + Main.ANSI_RESET);
    }

    public void aplicarImprimirFalse(JsonObject atributos, String key) {
        if (atributos.has(key)) {
            atributos.get(key).getAsJsonObject().addProperty("pendiente_impresion", Boolean.FALSE);
            atributos.get(key).getAsJsonObject().addProperty("imprimir", Boolean.FALSE);
        }
    }

    public void asignarPlaca(JsonObject atributos, String placa) {
        if (atributos.has("DatosFactura") && !placa.isEmpty()) {
            atributos.get("DatosFactura").getAsJsonObject().addProperty("placa", placa);
        }
    }

    public void eliminarAtributosVenta(int cara) {
        boolean atributosDatafono = validarAtributoDatafonoVentaCurso(cara);
        long codigoAutorizacion = 0l;
        if (atributosDatafono) {
            codigoAutorizacion = getCodigoAutorizacionDatafonoVentaCurso(cara);
            eliminarRegistroLtVentaCurso(codigoAutorizacion);
        }
        String sql = "UPDATE ventas_curso\n"
                + "SET atributos = fnc_utilidad_eliminar_propiedades_json(atributos, 'gopass_v2', 'datafono', 'isAppTerpel')\n"
                + "WHERE cara = ?;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, cara);
            pst.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al eliminar atributos venta fnc_utilidad_eliminar_propiedades_json" + Main.ANSI_RESET);
        }
    }

    public long getCodigoAutorizacionDatafonoVentaCurso(int cara) {
        String sql = "select ((atributos::json->>'datafono')::json->>'codigoAutorizacion')::numeric respuesta from ventas_curso where cara = ?;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, cara);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getLong("respuesta");
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar getCodigoAutorizacionDatafonoVentaCurso" + Main.ANSI_RESET);
        }
        return 0l;
    }

    public void eliminarRegistroLtVentaCurso(long codigoAutorizacion) {
        String sql = "delete from lt_ventas_curso where codigo_autorizacion = ?;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, codigoAutorizacion);
            pst.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al eliminar eliminarRegistroLtVentaCurso" + Main.ANSI_RESET);
        }
    }

    public boolean validarAtributosVentaCursoAppTerpel(int cara) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ResultSet rs = null;
        boolean respuesta = false;
        String sqlAppterpel = "select (atributos::json->>'isAppTerpel') is not null respuesta from ventas_curso where cara = ? ;";
        try ( PreparedStatement pst = conexion.prepareStatement(sqlAppterpel)) {
            pst.setInt(1, cara);
            rs = pst.executeQuery();
            while (rs.next()) {
                respuesta = rs.getBoolean("respuesta");
                return respuesta;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar  getLongIdMedioVenta " + s.getMessage() + Main.ANSI_RESET);

        }
        System.out.println("RESPUESTA APPTERPEL " + respuesta);
        return respuesta;
    }

    public boolean validarAtributosVentaCurso(int cara) {
        String sqlGopass = "select (atributos::json->>'gopass_v2') is not null respuesta from ventas_curso where cara = ? ;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ResultSet rs = null;
        boolean respuesta = false;
        try ( PreparedStatement pst = conexion.prepareStatement(sqlGopass)) {
            pst.setInt(1, cara);
            rs = pst.executeQuery();
            while (rs.next()) {
                respuesta = rs.getBoolean("respuesta");
                return respuesta;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar getLongIdMedioVenta " + s.getMessage() + Main.ANSI_RESET);
        }
        String sqlDatafono = "select (atributos::json->>'datafono') is not null respuesta from ventas_curso where cara = ? ;";
        try ( PreparedStatement pst = conexion.prepareStatement(sqlDatafono)) {
            pst.setInt(1, cara);
            rs = pst.executeQuery();
            while (rs.next()) {
                respuesta = rs.getBoolean("respuesta");
                return respuesta;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar  getLongIdMedioVenta " + s.getMessage() + Main.ANSI_RESET);
        }
        return respuesta;
    }

    public boolean validarAtributoDatafonoVentaCurso(int cara) {
        boolean respuesta = false;
        String sqlDatafono = "select (atributos::json->>'datafono') is not null respuesta from ventas_curso where cara = ? ;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pst = conexion.prepareStatement(sqlDatafono)) {
            pst.setInt(1, cara);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                respuesta = rs.getBoolean("respuesta");
                return respuesta;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar atributos datafonos " + s.getMessage() + Main.ANSI_RESET);
        }
        return respuesta;
    }

    /*public long getIdMedioPagoVentaCurso(int cara) {
        long respuesta = 1l;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select ((vc.atributos::json->>'DatosFactura')::json->>'medio_pago')::numeric respuesta from ventas_curso vc where cara = ?;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, cara);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                respuesta = rs.getLong("respuesta");
                if (respuesta == 0) {
                    respuesta = 1;
                }
                return respuesta;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar getIdMedioPagoVentaCurso " + s.getMessage() + Main.ANSI_RESET);
        }
        return respuesta;
    }*/

    private void eleminarltVentasCurso(long numeroAutorizacion) {
        NovusUtils.printLn("***************entrando para el proceso de autorizacion********************");
        String sql = "delete from lt_ventas_curso where codigo_autorizacion = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try ( PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, numeroAutorizacion);
            int res = pmt.executeUpdate();
            if (res == 1) {
                NovusUtils.printLn(Main.ANSI_GREEN + " registro eliminado -> " + numeroAutorizacion + " metodo eleminarltVentasCurso(long numeroAutorizacion)" + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "error al eliminar los datos " + " eleminarltVentasCurso(long numeroAutorizacion) " + Main.ANSI_RESET);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo -> private void eleminarltVentasCurso(long numeroAutorizacion) en la clase " + SurtidorDao.class.getName() + " " + e.getMessage());
        }
    }

    /*public void actualizarVentaParaImprimir(JsonObject datos) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonObject atributos = new JsonObject();
        try {
            String sql = "select * from ventas_curso where cara=? and manguera =?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(datos.get("cara").getAsString()));
            ps.setInt(2, Integer.parseInt(datos.get("manguera").getAsString()));

            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                atributos = new Gson().fromJson(rs.getString("atributos"), JsonObject.class);
            }
            if (atributos.has("remision")) {
                atributos.get("remision").getAsJsonObject().addProperty("imprimir", true);
            } else {
                atributos.get("factura_electronica").getAsJsonObject().addProperty("imprimir", true);
            }
            String Updtate = "update ventas_curso set atributos=?::json where cara=? and manguera=?";
            PreparedStatement pst = conexion.prepareStatement(Updtate);
            pst.setString(1, atributos.toString());
            pst.setInt(2, Integer.parseInt(datos.get("cara").getAsString()));
            pst.setInt(3, Integer.parseInt(datos.get("manguera").getAsString()));
            int res = pst.executeUpdate();
            if (res == 1) {
                NovusUtils.printLn(Main.ANSI_GREEN + "Datos actualizados" + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "Error al generar la actulizacion" + Main.ANSI_RESET);
            }

        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "error: " + e + Main.ANSI_RESET);
        }
    }

    /* METODO NO SE USA NO SE MIGRA 
    public void eliminarTareaCara(int cara, String tarea) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "DELETE FROM tareas_pos_ventas WHERE CARA=? AND TAREA=?";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, cara);
            ps.setString(2, tarea);

            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/   

    /* METODO NO SE USA NO SE MIGRA 
    public void crearTareaProgramada(Manguera manguera, JsonObject clientJson) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String TAREA_VENTA_CON_PLACA = "2";
            eliminarTareaCara(manguera.getCara(), TAREA_VENTA_CON_PLACA);
            String sql = "INSERT INTO public.tareas_pos_ventas\n"
                    + "(cara, manguera, tarea, atributos)\n"
                    + "VALUES(?, ?, ?, ?::json);";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, manguera.getCara());
            ps.setInt(2, manguera.getId());
            ps.setString(3, TAREA_VENTA_CON_PLACA);
            ps.setString(4, clientJson.toString());

            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/

    /*public void crearPosVentaFE(Manguera manguera, JsonObject clienteJson) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String TAREA_FACTURA_ELECTRONICA = "3";
            String sql = "INSERT INTO public.tareas_pos_ventas\n"
                    + "(cara, manguera, tarea, atributos)\n"
                    + "VALUES(?, ?, ?, ?::json);";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, manguera.getCara());
            ps.setInt(2, manguera.getId());
            ps.setString(3, TAREA_FACTURA_ELECTRONICA);
            ps.setString(4, clienteJson.toString());

            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/

    /*public void actualizarVentaFidelizada(ReciboExtended recibo) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "select atributos from ct_movimientos cm where id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, recibo.getNumero());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                if (re.getString("atributos") != null) {
                    JsonObject json = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                    if (!json.isJsonNull()) {
                        json.addProperty("fidelizada", "S");
                        String sql2 = "update ct_movimientos set atributos=?::json, estado='M' where id = ?";
                        PreparedStatement ps2 = conexion.prepareStatement(sql2);
                        ps2.setString(1, json.toString());
                        ps2.setLong(2, recibo.getNumero());
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/

    /*public void actualizarFidelizacion(Long id) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select atributos from ct_movimientos cm where id = ?";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                if (re.getString("atributos") != null) {
                    JsonObject json = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                    if (!json.isJsonNull()) {
                        json.addProperty("editarFidelizacion", false);
                        String sql2 = "update ct_movimientos set atributos=?::json, estado='M' where id = ?";
                        try ( PreparedStatement ps2 = conexion.prepareStatement(sql2)) {
                            ps2.setString(1, json.toString());
                            ps2.setLong(2, id);
                            ps2.executeUpdate();
                        }
                    }
                }
            }
        } catch (JsonSyntaxException | SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
    }

    /*public boolean fueFidelizada(ReciboExtended recibo) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean fidelizada = false;
        String sql = "select atributos from ct_movimientos cm where id = ?";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, recibo.getNumero());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                if (re.getString("atributos") != null) {
                    JsonObject json = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                    if (!json.isJsonNull() && json.get("fidelizada") != null && !json.get("fidelizada").isJsonNull() && json.get("fidelizada").getAsString().equals("S")) {
                        fidelizada = true;
                    }

                    if (!fidelizada) {
                        TransaccionProcesParams transaccionProcesParams = new TransaccionProcesParams();
                        transaccionProcesParams.setIdMov(recibo.getNumero());
                        transaccionProcesParams.setIdintegracion(3l);

                        fidelizada = SingletonMedioPago.ConetextDependecy.getCheckIsLoyaltyProcessExist().execute(transaccionProcesParams);

                    }
                }
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return fidelizada;
    }*/

    /*public ArrayList<RecepcionBean> getRecepciones() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ArrayList<RecepcionBean> operaciones = new ArrayList<>();
        try {
            String sql = "select rec.*, p.descripcion producto_descripcion\n"
                    + "from recepcion_combustible rec\n"
                    + "inner join productos p on p.id = rec.productos_id";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                RecepcionBean recepcion = new RecepcionBean();
                recepcion.setId(re.getLong("id"));
                recepcion.setPlaca(re.getString("placa"));
                recepcion.setPromotor(re.getLong("promotor_id"));
                recepcion.setDocumento(re.getString("documento"));
                recepcion.setTanqueId(re.getLong("tanques_id"));
                recepcion.setProductoId(re.getLong("productos_id"));
                recepcion.setCantidad(re.getInt("cantidad"));
                recepcion.setAlturaInicial(re.getFloat("altura_inicial"));
                recepcion.setVolumenInicial(re.getFloat("volumen_inicial"));
                recepcion.setAguaInicial(re.getFloat("agua_inicial"));
                recepcion.setFecha(re.getTimestamp("fecha"));
                recepcion.setProductoDescripcion(re.getString("producto_descripcion"));
                operaciones.add(recepcion);
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return operaciones;
    }*/

    /*public RecepcionBean actualizarRecepcionCombustible(RecepcionBean bean) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        try {
            if (bean.getId() == 0) {
                String sql = "INSERT INTO public.recepcion_combustible ("
                        + "promotor_id,documento,placa,tanques_id,productos_id,cantidad,"
                        + "fecha,altura_inicial,volumen_inicial,agua_inicial) "
                        + "VALUES\n"
                        + "(?,?,?,?,?,"
                        + "?,?,?,?,?) RETURNING id";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getPromotor());
                ps.setString(2, bean.getDocumento());
                ps.setString(3, bean.getPlaca());
                ps.setLong(4, bean.getTanqueId());
                ps.setLong(5, bean.getProductoId());
                ps.setInt(6, bean.getCantidad());
                ps.setTimestamp(7, new Timestamp(bean.getFecha().getTime()));
                ps.setFloat(8, bean.getAlturaInicial());
                ps.setDouble(9, bean.getVolumenInicial());
                ps.setFloat(10, bean.getAguaInicial());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    bean.setId(re.getLong("id"));
                }
            } else {
                String sql = "update recepcion_combustible set \n"
                        + "promotor_id=?,documento=?,placa=?,tanques_id=?,productos_id=?,cantidad=?,\n"
                        + "fecha=?,altura_inicial=?,volumen_inicial=?,agua_inicial=?\n"
                        + "where id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getPromotor());
                ps.setString(2, bean.getDocumento());
                ps.setString(3, bean.getPlaca());
                ps.setLong(4, bean.getTanqueId());
                ps.setLong(5, bean.getProductoId());
                ps.setInt(6, bean.getCantidad());
                ps.setTimestamp(7, new Timestamp(bean.getFecha().getTime()));
                ps.setFloat(8, bean.getAlturaInicial());
                ps.setDouble(9, bean.getVolumenInicial());
                ps.setFloat(10, bean.getAguaInicial());
                ps.setLong(11, bean.getId());
                ps.executeUpdate();

            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return bean;
    }*/

    /*public ArrayList<RecepcionBean> borrarRecepcion(RecepcionBean recepcion) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ArrayList<RecepcionBean> operaciones = new ArrayList<>();
        try {
            String sql = "delete from recepcion_combustible where id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, recepcion.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return operaciones;
    }*/

    /*public void setEstadoMovimiento(long id) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "update ct_movimientos set estado = 'M' where id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/

    /*public JsonObject obtenerCapacidadMaxima(long tanque) {
        JsonObject data = new JsonObject();
        String sql = "select * from ct_bodegas where id =?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            PreparedStatement psmt = conexion.prepareStatement(sql);
            psmt.setLong(1, tanque);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                Gson gson = new Gson();
                data = gson.fromJson(rs.getString("atributos"), JsonObject.class);
            }
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }*/

    
    /*
    private long equipoId() {
        String sql = "select id from equipos";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            NovusUtils.printLn("error al consultar el equipo " + e);
        }
        return 0;
    }*/

    /*
    METODO NO SE USA NO SE MIGRA
    public JsonObject getInformacionAutorizacion() {
        JsonObject respuesta = null;
        String sql = "select\n"
                + "row_to_json(t) respuesta\n"
                + "from\n"
                + "(\n"
                + "select\n"
                + "ta.id_autorizacion,\n"
                + "ta.placa_vehiculo,\n"
                + "ta.valor_odometro ,\n"
                + "ta.nombre_cliente,\n"
                + "ta.documento_identificacioncliente,\n"
                + "ta.codigo_familia_producto\n"
                + "from\n"
                + "autorizacion.tbl_autorizaciones ta\n"
                + "inner join autorizacion.tbl_autorizaciones_pos tap on\n"
                + "tap.id_autorizacion = ta.id_autorizacion\n"
                + "where\n"
                + "ta.id_estado in (1, 5)\n"
                + "and tap.id_estado_autorizacion = 1\n"
                + "order by\n"
                + "ta.fecha_autorizacion desc\n"
                + "limit 1) t;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement psmt = conexion.prepareStatement(sql)) {
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                respuesta = new Gson().fromJson(rs.getString("respuesta"), JsonObject.class);
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return respuesta;
    }*/

    /*
    METODO NO SE USA NO SE MIGRA
    public void actualizarAutorizacionAppRumbo(int idAutorizacion) {
        String sql = "update autorizacion.tbl_autorizaciones_pos set id_estado_autorizacion = 3 where id_autorizacion = ?;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement psmt = conexion.prepareStatement(sql)) {
            psmt.setInt(1, idAutorizacion);
            psmt.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
    }*/

    /*public String getCodigoExternoProducto(long cara, long grado) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String codigoExterno = null;
        String sql = "select\n"
                + "(p2.p_atributos->>'codigoExterno') codigo\n"
                + "from\n"
                + "surtidores_detalles sd\n"
                + "inner join productos p2 on\n"
                + "p2.id = sd.productos_id\n"
                + "where\n"
                + "sd.cara = ?\n"
                + "and sd.grado = ?;";
        try ( PreparedStatement psmt = conexion.prepareStatement(sql)) {
            psmt.setLong(1, cara);
            psmt.setLong(2, grado);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                codigoExterno = rs.getString("codigo");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return codigoExterno;
    }*/

    /*public void actualizarNovedadSaltoLectura(JsonObject detailHose) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "UPDATE reporteria_cierres.novedad_salto_lectura\n"
                + "SET  estado='RESUELTO', fecha_actualizacion = now(), sincronizado = 0\n"
                + "WHERE surtidor = ? and manguera = ? and cara = ? and estado = 'PENDIENTE';";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, detailHose.get("surtidor").getAsInt());
            pst.setInt(2, detailHose.get("manguera").getAsInt());
            pst.setInt(3, detailHose.get("cara").getAsInt());
            pst.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn("Error al actualizar Novedad salto de lectura " + s.getMessage());
        }
    }*/

    /*public boolean validarCorreccionSaltoLectura(JsonObject detailHose) {
        boolean corregido = true;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select id from surtidores_detalles sd "
                + "where sd.surtidor = ? "
                + "and sd.manguera = ?  "
                + "and sd.cara = ? "
                + "and salto_lectura = 'S';";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, detailHose.get("surtidor").getAsInt());
            pst.setInt(2, detailHose.get("manguera").getAsInt());
            pst.setInt(3, detailHose.get("cara").getAsInt());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                corregido = false;
            }
        } catch (SQLException s) {
            NovusUtils.printLn("Error al actualizar Novedad salto de lectura " + s.getMessage());
        }
        return corregido;
    }*/

    /*public boolean habilitarBotonesFamiliaAutorizacion(String familia) {
        boolean habilitar = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from public.fnc_consultar_familia_autorizacion(?) resultado;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, familia);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                habilitar = rs.getBoolean("resultado");
            }
        } catch (SQLException s) {
            NovusUtils.printLn("Error habilitarBotonesFamiliaAutorizacion " + s.getMessage());
        }
        return habilitar;
    }*/

    /*public boolean ocultarInputsFacturaVenta(int cara) {
        boolean ocultar = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from fnc_consultar_autorizacion_cara(?) resultado;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, cara);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ocultar = rs.getBoolean("resultado");
            }
        } catch (SQLException s) {
            NovusUtils.printLn("Error cultarInputsFacturaVenta " + s.getMessage());
        }
        return ocultar;
    }*/

}

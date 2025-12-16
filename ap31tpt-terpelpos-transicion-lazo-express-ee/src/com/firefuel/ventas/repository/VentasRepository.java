package com.firefuel.ventas.repository;


import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.firefuel.VentasHistorialView;
import com.firefuel.ventas.dto.VentaDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.application.useCases.movimientos.FinByTipoMovimientoUseCase;
import com.application.commons.db_utils.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentasRepository {

    int GLPFAMILYID = 7;

    public List<VentaDTO> obtenerVentasBasicas(long promotorID, long jornadaID, int paginacion, boolean ventasSinResolver) {
        List<VentaDTO> lista = new ArrayList<>();
        Set<Long> idsAgregados = new HashSet<>();
        String sql = VentasHistorialView.estadoActulizarDatafono ?
                "select * from fnc_consultar_ventas_pendientes(?, ?, ?)" :
                "select * from fnc_consultar_ventas(?, ?, ?)";

        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            // Usar el DatabaseConnectionManager para crear recursos
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            
            if (resources.getConnection() == null || resources.getConnection().isClosed()) {
                Logger.getLogger(VentasRepository.class.getName()).log(Level.SEVERE, "❌ Conexión no disponible o cerrada");
                return lista;
            }

            // Configurar parámetros
            resources.getPreparedStatement().setLong(1, jornadaID);
            resources.getPreparedStatement().setLong(2, promotorID);
            resources.getPreparedStatement().setInt(3, paginacion);

            // Ejecutar consulta usando DatabaseConnectionManager
            DatabaseConnectionManager.executeQuery(resources);
            
            SimpleDateFormat formatoFecha = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
            MovimientosDao movimientosDao = new MovimientosDao();

            while (resources.getResultSet().next()) {
                long idMovimiento = resources.getResultSet().getLong("numero");
                if (idsAgregados.contains(idMovimiento)) {
                    continue;
                }

                VentaDTO venta = new VentaDTO();
                JsonObject atributos = new Gson().fromJson(resources.getResultSet().getString("atributos"), JsonObject.class);

                String prefijo = null;
                String consecutivo = String.valueOf(idMovimiento);

                if (atributos.has("consecutivo") && atributos.get("consecutivo").isJsonObject()) {
                    JsonObject consecutivoObj = atributos.get("consecutivo").getAsJsonObject();
                    if (consecutivoObj.has("prefijo") && !consecutivoObj.get("prefijo").isJsonNull()) {
                        prefijo = consecutivoObj.get("prefijo").getAsString();
                    }
                    if (consecutivoObj.has("consecutivo_actual") && !consecutivoObj.get("consecutivo_actual").isJsonNull()) {
                        consecutivo = consecutivoObj.get("consecutivo_actual").getAsString();
                    }
                }

                // Fallback si no hay prefijo
                if (prefijo == null || prefijo.isBlank()) {
                    String consecutivoBD = FinByTipoMovimientoUseCase.consultarConsecutivo(idMovimiento);
                    if (consecutivoBD != null && !consecutivoBD.isBlank()) {
                        prefijo = consecutivoBD;
                    } else if (esVentaElectronica(atributos)) {
                        prefijo = "SETT-" + idMovimiento;
                    } else {
                        prefijo = "N/A";
                    }
                } else {
                    prefijo = prefijo + "-" + consecutivo;
                }

                venta.setPrefijo(prefijo);
                venta.setNumero(idMovimiento);
                venta.setIdMovimiento(idMovimiento);
                venta.setIdTransmision(resources.getResultSet().getLong("id_transmision"));

                venta.setFecha(formatoFecha.format(formatoFecha.parse(resources.getResultSet().getString("fecha"))));
                venta.setProducto(resources.getResultSet().getString("producto"));
                venta.setOperador(resources.getResultSet().getString("operador"));
                venta.setPromotor(resources.getResultSet().getString("operador"));
                venta.setCara(String.valueOf(resources.getResultSet().getLong("cara")));
                venta.setPlaca(atributos.has("vehiculo_placa") ? atributos.get("vehiculo_placa").getAsString() : "");

               // boolean isGLP = atributos.has("familiaId") && atributos.get("familiaId").getAsInt() == GLPFAMILYID;

                //setPlacaInSale(atributos, VentasHistorialView.estadoActulizarDatafono, isGLP, venta);

                if (ventasSinResolver) {
                    String proceso = resources.getResultSet().getString("proceso");
                    if (proceso == null || proceso.isBlank()) {
                        String estado = resources.getResultSet().getString("descripcion_transaccion_estado_datafono");
                        if ("PENDIENTE".equalsIgnoreCase(estado) || resources.getResultSet().getBoolean("ind_pendiente_asignar_cliente")) {
                            proceso = "FE";
                        } else if (resources.getResultSet().getBoolean("ind_pendiente_resolver_adblue")) {
                            proceso = "UREA";
                        } else if (resources.getResultSet().getString("codigo_autorizacion_datafono") != null) {
                            proceso = "Datafono";
                        } else {
                            proceso = "OTRO";
                        }
                    }
                    venta.setProceso(proceso);
                    venta.setIdTransaccionDatafono(resources.getResultSet().getLong("codigo_autorizacion_datafono"));
                    venta.setEstadoDatafono(resources.getResultSet().getString("descripcion_transaccion_estado_datafono"));
                    venta.setClienteSinAsignar(resources.getResultSet().getBoolean("ind_pendiente_asignar_cliente"));
                }

                double cantidad = resources.getResultSet().getDouble("cantidad");
                String unidad = resources.getResultSet().getString("unidad_medida");
                venta.setCantidad(cantidad);
                venta.setCantidadConUnidad(String.format("%,.3f %s", cantidad, unidad != null ? unidad.toUpperCase() : "GL"));

                long total = resources.getResultSet().getLong("total");
                venta.setTotalRaw(total);
                venta.setTotal("$ " + String.format("%,d", total));
                venta.setAtributosJson(atributos);

                lista.add(venta);
                idsAgregados.add(idMovimiento);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(VentasRepository.class.getName()).log(Level.SEVERE, "❗ Error al consultar ventas", ex);
        } finally {
            // Cerrar recursos usando DatabaseConnectionManager
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }

        return lista;
    }


    // public void setPlacaInSale(JsonObject atributos, boolean isPendingSales, boolean isGLPSale, VentaDTO sale) {
    //     if (!isPendingSales) {
    //         return;
    //     }

    //     if (!isGLPSale) {
    //         return;
    //     }

    //     if (!atributos.has("vehiculo_placa") || !atributos.get("vehiculo_placa").getAsString().isEmpty()) {
    //         return;
    //     }
    //     NovusUtils.printLn("Se realiza una venta de GLP sin placa");
    //     int surtidor = atributos.get("surtidor").getAsInt();
    //     int cara = atributos.get("cara").getAsInt();
    //     int grado = atributos.get("grado").getAsInt();

    //     String placa = getPlacaInTransaccionesTable(surtidor, cara, grado);
    //     sale.setPlaca(placa);
    //     NovusUtils.printLn("La venta queda asociada a la placa: " + placa);
    // }

    // public String getPlacaInTransaccionesTable(int surtidor, int cara, int grado) {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String placa = "";
    //     try {
    //         String sql = "select * from transacciones where surtidor=? and cara =? and grado=? and usado='N' order by fecha_creacion desc limit 1";
    //         PreparedStatement ps = conexion.prepareStatement(sql);
    //         ps.setInt(1, surtidor);
    //         ps.setInt(2, cara);
    //         ps.setInt(3, grado);
    //         ResultSet rs = ps.executeQuery();
    //         if (rs.next()) {
    //             placa = rs.getString("placa_vehiculo");
    //         } else {
    //             NovusUtils.printLn("No se encontró ninguna transacción coincidente.");
    //         }

    //     } catch (Exception e) {
    //         NovusUtils.printLn(Main.ANSI_RED + "error: " + e + Main.ANSI_RESET);
    //     }
    //     return placa;
    // }

    private boolean esVentaElectronica(JsonObject atributos) {
        return atributos != null &&
                atributos.has("proceso") &&
                "FE".equalsIgnoreCase(atributos.get("proceso").getAsString());
    }
}
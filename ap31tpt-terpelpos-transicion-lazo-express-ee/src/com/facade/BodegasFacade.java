/*
 * To change this license header, choose License Headers in Project Properties.,
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.facade;

import com.bean.BodegaBean;
import com.bean.ProductoBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.firefuel.InventariosTanques;
import com.firefuel.Main;
import com.firefuel.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BodegasFacade {

    EquipoDao edao = new EquipoDao();

    public static TreeMap<Integer, JsonObject> fetchTanksCapacity(Set<Integer> tanksId) {
        final TreeMap<Integer, JsonObject> tanksCapacity = new TreeMap<>();
        tanksId.forEach((entry) -> {
            JsonObject response = BodegasFacade.fetchTankCapacity(entry);
            tanksCapacity.put(entry, response);
        });
        return tanksCapacity;
    }

    static JsonObject fetchTankCapacity(int tankId) {
        JsonObject response = null;
        ClientWSAsync ws = new ClientWSAsync("OBTENER AFORO TANQUE",
                NovusConstante.SECURE_CENTRAL_POINT_OBTENER_AFORO + "/" + tankId, NovusConstante.GET, null,
                false, false);
        try {
            response = ws.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

    public static JsonObject printFuelEntryDetails(int fuelEntryId) {
        JsonObject response = null;
        ClientWSAsync ws = new ClientWSAsync("RECEPCION PRINT", NovusConstante.SECURE_CENTRAL_POINT_IMPRIMIR_DETALLE_ENTRADA + "/" + fuelEntryId,
                NovusConstante.GET, null, true, false);
        try {
            response = ws.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

    public static JsonObject printTankInventaryDetails(Set<Integer> selectedTanks) {

        if (selectedTanks != null) {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (Integer tankNumber : selectedTanks) {
                if (index == 0) {
                    sb.append(tankNumber);
                } else {
                    sb.append("," + tankNumber);
                }
                index++;
            }
            if (sb.length() > 0) {
                return fetchPrintTanksInventary(sb.toString());
            }
        }
        return null;
    }

    public static JsonObject fetchPrintTanksInventary(String tanksIdString) {
        JsonObject response = null;
        JsonObject request = new JsonObject();
        request.addProperty("tanques", tanksIdString);
        ClientWSAsync ws = new ClientWSAsync("MEDIDAS TANQUES PRINT", NovusConstante.SECURE_CENTRAL_POINT_IMPRIMIR_DETALLE_INVENTARIO_TANQUE,
                NovusConstante.POST, request, true, false);
        try {
            response = ws.esperaRespuesta();
            
            // Si la respuesta es nula, verificar si hay un error en el cliente
            if (response == null && ws.getError() != null) {
                response = ws.getError();
            }
            
        } catch (Exception ex) {
            NovusUtils.printLn("[ERROR] fetchPrintTanksInventary: " + ex.getMessage());
            // Crear respuesta de error
            response = new JsonObject();
            response.addProperty("codigoError", "500");
            response.addProperty("mensaje", "Error al imprimir inventario de tanques: " + ex.getMessage());
            response.addProperty("error", true);
        }
        return response;
    }

    public static ArrayList<BodegaBean> fetchTanksMeasureTeoric(Long id) {
        ArrayList<BodegaBean> tanksMeasures = null;
        String host = EquipoDao.posPrincipal();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            JsonArray infoTanques = new JsonArray();
            if (host.equals("localhost")) {
                String sql = "select *"
                        .concat(" from fnc_consultar_saldos_productos_x_negocio(i_id_empresa => ?::integer")
                        .concat(" , i_id_negocio => 1::integer)");

                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                JsonArray ArrayRespuestaLocal = new JsonArray();
                Gson gson = new Gson();
                while (rs.next()) {
                    JsonObject respuestaLocal = new JsonObject();
                    respuestaLocal.addProperty("id_bodega", rs.getString("id_bodega"));
                    respuestaLocal.addProperty("nombre_producto", rs.getString("nombre_producto"));
                    respuestaLocal.addProperty("nombre_bodega", rs.getString("nombre_bodega"));
                    respuestaLocal.addProperty("unidad", rs.getString("unidad"));
                    respuestaLocal.addProperty("volumen", rs.getFloat("volumen"));
                    respuestaLocal.addProperty("altura_actual", rs.getFloat("altura_actual"));
                    respuestaLocal.addProperty("id_producto", rs.getInt("id_producto"));
                    respuestaLocal.addProperty("numero_tanque", rs.getInt("numero_tanque"));
                    ArrayRespuestaLocal.add(respuestaLocal);
                }
                tanksMeasures = BodegasFacade.buildConsoleTanksMeasures(null, ArrayRespuestaLocal);
            } else {
                JsonObject data = new JsonObject();
                data.addProperty("id", id);

                String url = "http://"
                        + host
                        + ":8019/api/tanques/informacion";

                ClientWSAsync async = new ClientWSAsync(
                        "CONSULTA TANQUES",
                        url, NovusConstante.POST, data, true, false, 10000);

                async.join();
                JsonObject informacion = async.esperaRespuesta();
                if (informacion != null) {
                    infoTanques = informacion.get("data").getAsJsonArray();
                    tanksMeasures = BodegasFacade.buildConsoleTanksMeasures(null, infoTanques);
                } else {
                    NovusUtils.printLn("Error!");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(BodegasFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            NovusUtils.printLn(e.getMessage());
        }
        if (tanksMeasures == null && InventariosTanques.jLabel1 != null) {
            InventariosTanques.jLabel1.setText("SIN INFORMACION DE INVENTARIO");
            Utils.setTimeout("", () -> {
                InventariosTanques.jLabel1.setText("");
            }, 4000);
        }
        return tanksMeasures;
    }

    public static ArrayList<BodegaBean> fetchConsoleTanksMeasuresVeeder() {
        ArrayList<BodegaBean> tanksMeasures = null;
        JsonObject response = null;
        ClientWSAsync ws = new ClientWSAsync("OBTENER TANQUES VEEDER ROOT", NovusConstante.SECURE_CENTRAL_POINT_OBTENER_MEDIDAS_VEEDER_ROOT, NovusConstante.GET, null, true,
                false);
        try {
            response = ws.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }

        if (response != null) {
            tanksMeasures = BodegasFacade.buildConsoleTanksMeasures(response, null);
        } else {
            if (InventariosTanques.jLabel1 != null) {
                InventariosTanques.jLabel1.setText("NO HAY CONEXION CON LA CONSOLA DE INVENTARIO");
                Utils.setTimeout("", () -> {
                    InventariosTanques.jLabel1.setText("");
                }, 4000);
            }
        }
        return tanksMeasures;
    }

    public static JsonObject sendTanksMeasures(ArrayList<BodegaBean> measuresTanks) {
        JsonObject response = null;
        ClientWSAsync ws = new ClientWSAsync("MEDIDAS TANQUES", NovusConstante.SECURE_CENTRAL_POINT_ENVIAR_MEDIDAS_TANQUES,
                NovusConstante.POST, buildTanksMeasuresRequestObject(measuresTanks), true, false);
        try {
            response = ws.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            response = ws.getError();
        }
        return response;
    }

    public static ArrayList<BodegaBean> fetchTanksMeasures() {
        JsonObject response = null;
        ArrayList<BodegaBean> tanksMeasures = null;
        ClientWSAsync ws = new ClientWSAsync("OBTENER TANQUES", NovusConstante.SECURE_CENTRAL_POINT_OBTENER_TANQUES,
                NovusConstante.GET, null, false, false);
        try {
            response = ws.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }

        if (response != null) {
            tanksMeasures = BodegasFacade.buildTanksMeasures(response);
        }

        return tanksMeasures;
    }

    private static JsonObject buildTanksMeasuresRequestObject(ArrayList<BodegaBean> measuresTanks) {
        JsonObject request = new JsonObject();
        JsonObject entryJson = new JsonObject();
        JsonArray measureArray = new JsonArray();
        entryJson.addProperty("identificadorPromotor", Main.persona != null ? Main.persona.getId() : 0);
        entryJson.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
        entryJson.addProperty("identificacionEquipoLazo", Main.credencial.getEquipos_id());
        entryJson.addProperty("fechaTransaccion", Main.SDFSQL.format(new Date()));
        for (BodegaBean tank : measuresTanks) {
            JsonObject tankJson = new JsonObject();
            tankJson.addProperty("identificadorTanque", tank.getId());
            tankJson.addProperty("identificadorProducto", tank.getProductos().get(0).getId());
            tankJson.addProperty("identificadorFamiliaProducto", tank.getProductos().get(0).getFamiliaId());
            tankJson.addProperty("altura", tank.getAltura_total());
            tankJson.addProperty("agua", tank.getAltura_agua());
            tankJson.addProperty("galones", tank.getGalonTanque());
            measureArray.add(tankJson);
        }
        request.add("entrada", entryJson);
        request.add("medidasTanque", measureArray);

        return request;
    }

    static ArrayList<BodegaBean> buildConsoleTanksMeasures(JsonObject responseTanks, JsonArray data) {
        ArrayList<BodegaBean> tanksMeasures = null;
        if (responseTanks != null) {
            JsonArray tanksMeasuresArray = responseTanks.get("tanques").isJsonArray() ? responseTanks.get("tanques").getAsJsonArray() : new JsonArray();
            for (JsonElement elementTanques : tanksMeasuresArray) {
                if (tanksMeasures == null) {
                    tanksMeasures = new ArrayList<>();
                }
                JsonObject tankObject = elementTanques.getAsJsonObject();
                BodegaBean tank = new BodegaBean();
                tank.setId(tankObject.get("identificadorTanque").getAsLong());
                tank.setDescripcion(tankObject.get("bodegaDescripcion").getAsString());
                tank.setNumeroStand(tankObject.get("numeroTanque").getAsInt());
                tank.setProductos(new ArrayList<ProductoBean>());
                tank.setAltura_agua(tankObject.get("agua").getAsDouble());
                if (tankObject.has("altura") && tankObject.get("altura").isJsonNull()) {
                    tank.setAltura_total(0.0);
                } else if (tankObject.has("altura")) {
                    tank.setAltura_total(tankObject.get("altura").getAsDouble());
                } else {
                    tank.setAltura_total(0.0);
                }
                tank.setGalonTanque(tankObject.get("volumen").getAsFloat());
                tank.setTemperaturaTanque(tankObject.get("temperatura").getAsFloat());

                ProductoBean producto = new ProductoBean();
                producto.setUnidades_medida(tankObject.get("volumenMaximaUnidad") != null && !tankObject.get("volumenMaximaUnidad").isJsonNull() ? tankObject.get("volumenMaximaUnidad").getAsString() : "GL");
                producto.setDescripcion(tankObject.get("productoDescripcion").getAsString());
                producto.setFamiliaId(tankObject.get("identificadorFamiliaProducto").getAsLong());
                producto.setId(tankObject.get("identificadorProducto").getAsLong());
                tank.getProductos().add(producto);
                tanksMeasures.add(tank);
            }
        } else {
            int num = 1;
            for (JsonElement jsonElement : data) {

                if (tanksMeasures == null) {
                    tanksMeasures = new ArrayList<>();
                }
                BodegaBean tank = new BodegaBean();
                JsonObject objarr = jsonElement.getAsJsonObject();
                tank.setId(objarr.get("id_bodega").getAsInt());
                tank.setDescripcion(objarr.get("nombre_bodega").getAsString());
                tank.setNumeroStand(objarr.get("numero_tanque").getAsInt());
                tank.setProductos(new ArrayList<ProductoBean>());
                tank.setAltura_agua(0);
                tank.setAltura_total(0);
                tank.setGalonTanque(objarr.get("volumen").getAsFloat());
                tank.setTemperaturaTanque(0);
                ProductoBean producto = new ProductoBean();
                producto.setUnidades_medida(objarr.get("unidad").getAsString());
                producto.setDescripcion(objarr.get("nombre_producto").getAsString());
                producto.setFamiliaId(objarr.get("id_producto").getAsInt());
                producto.setId(0);
                tank.getProductos().add(producto);
                tanksMeasures.add(tank);
                num++;
            }

        }
        return tanksMeasures;
    }

    static ArrayList<BodegaBean> buildTanksMeasures(JsonObject responseTanks) {
        ArrayList<BodegaBean> tanksMeasures = null;
        JsonArray data = responseTanks.get("data").getAsJsonArray();
        for (JsonElement element : data) {
            if (tanksMeasures == null) {
                tanksMeasures = new ArrayList<>();
            }
            JsonObject tankObject = element.getAsJsonObject();
            BodegaBean tank = new BodegaBean();
            tank.setId(tankObject.get("identificacion_bodega").getAsLong());
            tank.setDescripcion(tankObject.get("bodega_descripcion").getAsString());
            tank.setNumeroStand(tankObject.get("tanque").getAsInt());
            tank.setProductos(new ArrayList<ProductoBean>());
            JsonArray productosArray = tankObject.get("detalles").getAsJsonArray();
            for (JsonElement tanqueElement : productosArray) {
                try {
                    JsonObject productoJson = tanqueElement.getAsJsonObject();
                    ProductoBean producto = new ProductoBean();
                    producto.setFamiliaId(productoJson.get("familias").getAsLong());
                    producto.setId(productoJson.get("productos_id").getAsLong());
                    producto.setDescripcion(productoJson.get("producto").getAsString());
                    producto.setSaldo(productoJson.get("saldo").getAsFloat());
                    producto.setPrecio(productoJson.get("precio").getAsFloat());
                    tank.getProductos().add(producto);
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
            }
            if (productosArray.size() > 0) {
                tanksMeasures.add(tank);
            }
        }

        if (tanksMeasures != null) {
            tanksMeasures.sort((a, b) -> a.getNumeroStand() - b.getNumeroStand());
        }

        return tanksMeasures;
    }
}

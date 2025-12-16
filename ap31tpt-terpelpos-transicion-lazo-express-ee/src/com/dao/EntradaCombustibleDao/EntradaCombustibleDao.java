package com.dao.EntradaCombustibleDao;

import com.bean.BodegaBean;
import com.bean.ProductoBean;
import com.bean.entradaCombustible.EntradaCombustibleBean;
import com.bean.entradaCombustible.EntradaCombustibleHistorialBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.bean.entradaCombustible.ProductoSAP;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EntradaCombustibleDao {

    public Map<String, ArrayList<BodegaBean>> getTanquesRemision(String delivery) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        HashMap<String, ArrayList<BodegaBean>> tanques = new HashMap<>();
        long idRemision = obtenerIdRemision(delivery);
        TreeMap<Integer, Long> productosDivididosExistentes = new TreeMap<>();
        String sql = "select * from public.fnc_consultar_tanques_remision(?);";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, delivery);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                BodegaBean tanque = new BodegaBean();
                boolean existeProductoDividido = !productosDivididosExistentes.containsKey(re.getInt("id_remision_producto"));
                if (validarSihayProductoDividido(re.getInt("producto_id"), re.getInt("id_remision_producto"), idRemision)) {
                    productosDivididosExistentes.put(re.getInt("id_remision_producto"), idRemision);
                    if (!existeProductoDividido) {
                        tanques.putAll(obtenerTanquesProductoDividido(re.getInt("producto_id"), re.getInt("id_remision_producto"), idRemision));
                    }
                } else {
                    tanque.setId(re.getInt("id"));
                    tanque.setDescripcion(re.getString("bodega"));
                    tanque.setNumeroStand(re.getInt("numero"));
                    tanque.setVolumenMaximo(re.getInt("volumen_maximo"));
                    tanque.setFamiliaId(re.getLong("familia"));
                    tanque.setProductos(getProductosTanques(re.getInt("producto_id"), re.getLong("id_remision_producto")));
                    if (tanques.containsKey("P-" + re.getInt("producto_id"))) {
                        tanques.get("P-" + re.getInt("producto_id")).add(tanque);
                    } else {
                        ArrayList<BodegaBean> tanqueBodega = new ArrayList<>();
                        tanqueBodega.add(tanque);
                        tanques.put("P-" + re.getInt("producto_id"), tanqueBodega);
                    }
                }
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public Map<Long, BodegaBean> getTanquesRemision(String delivery) ubicado en ".concat(EntradaCombustibleDao.class.getName()) + " " + ex.getMessage());
        }
        System.out.println(tanques.toString());
        return tanques;
    }

    public ArrayList<ProductoBean> getProductosTanques(int idProducto, long idRemisionProducto) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ArrayList<ProductoBean> tanques = new ArrayList<>();
        String sql = "select  p.id,p.familias,p.descripcion,p.precio,p.unidad_medida_id,u.alias \n"
                + " from productos p \n"
                + " inner join unidades u on u.id = p.unidad_medida_id \n"
                + " where  p.id = ?\n"
                + " order by  p.descripcion asc ";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, idProducto);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                ProductoBean producto = new ProductoBean();
                producto.setId(re.getInt("id"));
                producto.setDescripcion(re.getString("descripcion"));
                producto.setFamiliaId(re.getInt("familias"));
                producto.setPrecio(re.getFloat("precio"));
                producto.setSaldo(0);
                producto.setIdRemisionProducto(idRemisionProducto);
                producto.setUnidades_medida_id(re.getLong("unidad_medida_id"));
                producto.setUnidades_medida(re.getString("alias"));
                tanques.add(producto);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public ArrayList<ProductoBean> getProductosTanques(int idProducto)ubicado en ".concat(EntradaCombustibleDao.class.getName()) + " " + ex.getMessage());
        }
        return tanques;
    }

    public EntradaCombustibleBean infoEntradaRemision(String delivery) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        EntradaCombustibleBean entradaCombustible = null;
        String sql = "select * from sap.tbl_remisiones_sap trs where trs.delivery = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, delivery);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                entradaCombustible = new EntradaCombustibleBean();
                entradaCombustible.setDelivery(re.getString("delivery"));
                entradaCombustible.setIdRemision(re.getLong("id_remision_sap"));
                entradaCombustible.setDocumentDate(re.getString("document_date"));
                entradaCombustible.setWayBill(re.getString("way_bill"));
                entradaCombustible.setLogisticCenter(re.getString("logistic_center"));
                entradaCombustible.setSupplyingCenter(re.getString("supplying_center"));
                entradaCombustible.setStatus(re.getString("status"));
                entradaCombustible.setModificationDate(re.getString("modification_date") != null ? re.getString("modification_date") : " ");
                entradaCombustible.setModificationHour(re.getString("modification_hour") != null ? re.getString("modification_hour") : " ");
                entradaCombustible.setFrontierLaw(re.getString("frontier_law"));
                entradaCombustible.setProductoSAP(obtenerDetalleRemision(re.getLong("id_remision_sap")));
                System.out.println("[DEBUG] Productos SAP cargados en bean: " + entradaCombustible.getProductoSAP());
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public EntradaCombustibleBean infoEntradaRemision(String delivery) ubicado en ".concat(EntradaCombustibleDao.class.getName()) + " " + ex.getMessage());
        }
        System.out.println("[DEBUG] Bean retornado: " + entradaCombustible);
        return entradaCombustible;
    }

    //Este metodo no se esta utilizando
    public Map<String, ProductoSAP> obtenerDetalleRemision(long idRemision) {
        Connection connection = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        TreeMap<String, ProductoSAP> productos = new TreeMap<>();
        String sql = "select trps.*, p.descripcion from sap.tbl_remisiones_productos_sap trps\n"
                + " inner join public.productos p on p.id = trps.id_producto \n"
                + " where trps.id_remision_sap = ? and trps.id_estado = 1";
        try (PreparedStatement pmt = connection.prepareStatement(sql)) {
            pmt.setLong(1, idRemision);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                if (validarSihayProductoDividido(rs.getInt("id_producto"), rs.getLong("id_remision_producto"), idRemision)) {
                    productos.putAll(obtenerProductoDividido(rs.getInt("id_producto"), rs.getLong("id_remision_producto"), idRemision));
                } else {
                    ProductoSAP productoSAP = new ProductoSAP();
                    productoSAP.setProduct(rs.getString("product"));
                    productoSAP.setIdRemisionProducto(rs.getLong("id_remision_producto"));
                    productoSAP.setQuantity(rs.getFloat("quantity"));
                    productoSAP.setUnit(rs.getString("unit"));
                    productoSAP.setSalesCostValue(rs.getFloat("sales_cost_value"));
                    productoSAP.setDescripcion(rs.getString("descripcion"));
                    productoSAP.setProducID(rs.getInt("id_producto"));
                    productoSAP.setClave("P-" + rs.getInt("id_producto"));
                    productos.put("P-" + rs.getInt("id_producto"), productoSAP);
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public List<ProductoSAP> obtenerDetalleRemision (long idRemision) ubicado en ".concat(EntradaCombustibleDao.class.getName()) + " " + e.getMessage());
        }
        return productos;
    }

    public JsonArray infoHistorialRemisiones(long registros) {
        JsonArray info = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        EntradaCombustibleHistorialBean entradaCombustibleHistorial = new EntradaCombustibleHistorialBean();
        String sql = "select\n"
                + "trs.delivery,\n"
                + "p.descripcion product,\n"
                + "trps.quantity,\n"
                + "u.alias unit,\n"
                + "to_char(trs.creation_date, 'DD-MM-YYYY')creation_date,\n"
                + "coalesce (to_char(trs.creation_hour, 'HH24:MI:SS'),'') creation_hour,\n"
                + "coalesce (to_char (trs.modification_date,'DD-MM-YYYY' ),'')modification_date,\n"
                + "coalesce (to_char(trs.modification_hour, 'HH24:MI:SS'),'') modification_hour,\n"
                + "tes.descripcion status\n"
                + "from\n"
                + "sap.tbl_remisiones_sap trs\n"
                + "inner join sap.tbl_remisiones_productos_sap trps on\n"
                + "trs.id_remision_sap = trps.id_remision_sap\n"
                + "inner join productos p on\n"
                + "trps.id_producto = p.id\n"
                + "inner join sap.tbl_estados_sap tes on\n"
                + "trps.id_estado = tes.id_estado\n"
                + "inner join public.unidades u on \n"
                + "p.unidad_medida_id = u.id \n"
                + "order by trs.creation_date desc\n"
                + "fetch first ? rows only;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, registros);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                entradaCombustibleHistorial.setDelivery(re.getString("delivery"));
                entradaCombustibleHistorial.setProduct(re.getString("product"));
                entradaCombustibleHistorial.setQuantity(re.getFloat("quantity"));
                entradaCombustibleHistorial.setUnit(re.getString("unit"));
                entradaCombustibleHistorial.setCreationDate(re.getString("creation_date"));
                entradaCombustibleHistorial.setCreationHour(re.getString("creation_hour"));
                entradaCombustibleHistorial.setModificationDate(re.getString("modification_date"));
                entradaCombustibleHistorial.setModificationHour(re.getString("modification_hour"));
                entradaCombustibleHistorial.setStatus(re.getString("status"));

                JsonObject object = Main.gson.toJsonTree(entradaCombustibleHistorial).getAsJsonObject();
                info.add(object);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public JsonArray infoHistorialRemisiones(long registros) ubicado en ".concat(EntradaCombustibleDao.class.getName()) + " " + ex.getMessage());
        }
        return info;
    }

    public void marcarProductoDescargueComoUsado(Integer producto, Long idRemision, int estado) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "update sap.tbl_remisiones_productos_sap set id_estado = ? where id_producto = ? and id_remision_sap  = ?";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, estado);
            pmt.setInt(2, producto);
            pmt.setLong(3, idRemision);
            int respuesta = pmt.executeUpdate();
            if (respuesta >= 1) {
                NovusUtils.printLn("registros actualizados con exito producto afectado " + producto + " id de la remision " + idRemision + " estado del producto en el descargue " + estado);
            } else {
                NovusUtils.printLn("no se encontró el numero de remision indicada para la actulizacion id -> " + idRemision + " producto de la remision id -> " + producto);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public void marcarProductoDescargueComoUsado(Integer producto, Long idRemision, int estado) ubicado en " + EntradaCombustibleDao.class.getName() + " " + e.getMessage());
        }
    }

    public Long obtenerIdRemision(String numeroRemicion) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select id_remision_sap  from sap.tbl_remisiones_sap trs where trs.delivery = ?";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, numeroRemicion);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id_remision_sap");
            } else {
                return 0l;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public Long obtenerIdRemision(String numeroRemicion) ubicado en la clase " + EntradaCombustibleDao.class.getName() + " " + e.getMessage());
            return 0l;
        }
    }

    public boolean remisionConfirmada(Long idRemision) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from public.fnc_validar_remisiones_completadas(?)";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idRemision);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            } else {
                return false;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public void public boolean remisionConfirmada(Long idRemision) ubicado en " + EntradaCombustibleDao.class.getName() + " " + e.getMessage());
            return false;
        }
    }

    public void confirmarRemision(Long idRemision, int estado) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "UPDATE sap.tbl_remisiones_sap SET id_estado=? WHERE id_remision_sap = ?;";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, estado);
            pmt.setLong(2, idRemision);
            int resultado = pmt.executeUpdate();
            if (resultado >= 1) {
                NovusUtils.printLn("remision confirmada con exito");
            } else {
                NovusUtils.printLn("error al confirmara la remision");
            }

        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public void confirmarRemision(Long idRemision, int estado) ubicado en " + EntradaCombustibleDao.class.getName() + " " + e.getMessage());

        }
    }

    public boolean validarSiremisionEstaConfirmada(String numeroRemicion) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.tbl_remisiones_sap trs where trs.delivery = ? and trs.id_estado = 2";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, numeroRemicion);
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public boolean validarSiremisionEstaConfirmada(String numeroRemicion) ubicado en " + EntradaCombustibleDao.class.getName() + " " + e.getMessage());
            return false;
        }
    }

    public void guardarEnvioDeConfirmacion(String url, String metodo, String body) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "INSERT INTO public.transmision (equipo_id, request, sincronizado, fecha_generado, url, method, reintentos) VALUES( 1, ?, ?, now(),  ?, ?, 0)";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, body);
            pmt.setInt(2, 0);
            pmt.setString(3, url);
            pmt.setString(4, metodo);
            int respuesta = pmt.executeUpdate();
            if (respuesta > 0) {
                NovusUtils.printLn("Transmision guardada con exito");
            } else {
                NovusUtils.printLn("error al guardar la transmision: " + pmt.toString());
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public void guardarEnvioDeConfirmacion (String url, String metodo, String body) ubicado en " + EntradaCombustibleDao.class.getName() + " " + e.getMessage());
        }
    }

    public boolean validarSihayProductoDividido(int idProducto, long idRemisionProducto, long idRemisionSap) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.verificar_existencia_datos(?, ?, ?) as hay_datos";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, idProducto);
            pmt.setLong(2, idRemisionProducto);
            pmt.setLong(3, idRemisionSap);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("hay_datos");
            } else {
                return false;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en "
                    + " el metodo private boolean validarSihayProductoDividido(int idProducto, int idRemisionProducto, int idRemisionSap)"
                    + " unicado en la clase -> " + EntradaCombustibleDao.class.getName() + " error sql -> " + e.getMessage());
            return false;
        }
    }

    public Map<String, ProductoSAP> obtenerProductoDividido(int idProducto, long idRemisionProducto, long idRemisionSap) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.obtener_producto_dividido(?, ?, ?)";
        TreeMap<String, ProductoSAP> productos = new TreeMap<>();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, idProducto);
            pmt.setLong(2, idRemisionProducto);
            pmt.setLong(3, idRemisionSap);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                ProductoSAP productoSAP = new ProductoSAP();
                productoSAP.setProduct(rs.getString("product"));
                productoSAP.setIdRemisionProducto(rs.getLong("id_remision_producto"));
                productoSAP.setQuantity(rs.getFloat("cantidad"));
                productoSAP.setUnit(rs.getString("unit"));
                productoSAP.setSalesCostValue(rs.getFloat("sales_cost_value"));
                productoSAP.setDescripcion(rs.getString("descripcion"));
                productoSAP.setProducID(rs.getInt("id_producto"));
                productoSAP.setClave("T-" + rs.getInt("tanque_id"));
                productos.put("T-" + rs.getInt("tanque_id"), productoSAP);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo "
                    + "private Map<Integer, ProductoSAP> obtenerProductoDividido(int idProducto, long idRemisionProducto, int idRemisionSap) "
                    + " ubicado en la clase ->" + EntradaCombustibleDao.class.getName() + " error ->" + e.getMessage());
        }
        return productos;
    }

    public Map<String, ArrayList<BodegaBean>> obtenerTanquesProductoDividido(int idProducto, int idRemisionProducto, long idRemision) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from sap.obtener_datos_bodega_producto_dividido(?, ?, ?)";
        TreeMap<String, ArrayList<BodegaBean>> tanquesDivididos = new TreeMap<>();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, idProducto);
            pmt.setInt(2, idRemisionProducto);
            pmt.setLong(3, idRemision);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                BodegaBean tanque = new BodegaBean();
                tanque.setId(rs.getInt("id"));
                tanque.setDescripcion(rs.getString("bodega"));
                tanque.setNumeroStand(rs.getInt("numero"));
                tanque.setVolumenMaximo(rs.getInt("volumen_maximo"));
                tanque.setFamiliaId(rs.getLong("familia"));
                tanque.setProductos(getProductosTanques(rs.getInt("producto_id"), idRemisionProducto));
                ArrayList<BodegaBean> bodegas = new ArrayList<>();
                bodegas.add(tanque);
                tanquesDivididos.put("T-" + rs.getInt("id"), bodegas);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo"
                    + " private Map<String, ArrayList<BodegaBean>> obtenerTanquesProductoDividido(int idProducto, int idRemisionProducto, long idRemision)"
                    + " ubicado en la clase -> " + EntradaCombustibleDao.class.getName() + " error -> " + e.getMessage());
        }
        return tanquesDivididos;
    }
}

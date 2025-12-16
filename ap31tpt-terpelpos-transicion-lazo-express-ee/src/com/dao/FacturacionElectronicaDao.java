/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao;

import com.application.commons.db_utils.DatabaseConnectionManager;
import com.application.useCases.consecutivos.GetResolucionesUseCase;
import com.bean.TipoNegocio;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.facade.facturacionelectronica.AtributosDetalleVentaFacturacionELectronica;
import com.facade.facturacionelectronica.DetallesVentaFacturacionElectronica;
import com.facade.facturacionelectronica.ImpresionVentaFacturacionElectronica;
import com.facade.facturacionelectronica.ImpuestosFacturacionElectronica;
import com.facade.facturacionelectronica.MediosPagosFactruracionElectronica;
import com.facade.facturacionelectronica.ObservacionesFacturacionElectronica;
import com.facade.facturacionelectronica.TipoIdentificaion;
import com.facade.facturacionelectronica.VentaFacturacionElectronica;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author Devitech
 */
public class FacturacionElectronicaDao {

    /*public void cargarTiposIdentificaion() {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "   SELECT tipo_de_identificacion, codigo_identificacion, aplica_fidelizacion, caracteres_permitidos, limite_caracteres\n"
                + "FROM facturacion_electronica.identificacion_dian\n"
                + "ORDER BY\n"
                + "  CASE \n"
                + "    WHEN tipo_de_identificacion = 'Cedula de ciudadania' THEN 0\n"
                + "    ELSE 1\n"
                + "  END,\n"
                + "  tipo_de_identificacion;";
        TreeMap<String, TipoIdentificaion> tiposIdentificaion = new TreeMap<>();
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            while (rs.next()) {
                TipoIdentificaion tipoIdentificaion = new TipoIdentificaion();
                tipoIdentificaion.setTipoDocumento(rs.getString("tipo_de_identificacion"));
                tipoIdentificaion.setCodigoTipoDocumento(rs.getLong("codigo_identificacion"));
                tipoIdentificaion.setAplicaFidelizacion(rs.getBoolean("aplica_fidelizacion"));
                tipoIdentificaion.setCaracteresPermitidos(rs.getString("caracteres_permitidos"));
                tipoIdentificaion.setCantidadCaracteres(rs.getInt("limite_caracteres"));
                tiposIdentificaion.put(tipoIdentificaion.getTipoDocumento().toUpperCase(), tipoIdentificaion);
            }
            NovusConstante.setTiposIdentificaion(tiposIdentificaion);
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado al momento de obtener la informacion de los medios de identificacion ubicado en la clase " + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
        }
    }*/

    public ImpresionVentaFacturacionElectronica ventaFeImprimir(long movimientoId) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        String sql = "select * from public.fnc_obtener_venta_fe(?)";
        Gson gson = new Gson();
        ImpresionVentaFacturacionElectronica impresionVentaFacturacionElectronica = new ImpresionVentaFacturacionElectronica();
        try {
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            resources.getPreparedStatement().setLong(1, movimientoId);
            resources = DatabaseConnectionManager.executeQuery(resources);
            if (resources.getResultSet().next()) {
                VentaFacturacionElectronica ventaFacturacionElectronica = new VentaFacturacionElectronica();
                ventaFacturacionElectronica.setBodegas_id(0);
                ventaFacturacionElectronica.setConsecutivo(resources.getResultSet().getInt("consecutivo"));
                ventaFacturacionElectronica.setConsecutivoInicial(0);
                ventaFacturacionElectronica.setConsecutivoActual(resources.getResultSet().getInt("consecutivo"));
                ventaFacturacionElectronica.setConsecutivoFinal(0);
                ventaFacturacionElectronica.setConsecutivo_id(Integer.parseInt(resources.getResultSet().getString("consecutivo_id")));
                ventaFacturacionElectronica.setConsumo_propio(false);
                ventaFacturacionElectronica.setCosto_total(resources.getResultSet().getDouble("costo_total"));
                ventaFacturacionElectronica.setContingencia(false);
                String fecha = resources.getResultSet().getString("fecha");
                ventaFacturacionElectronica.setCreate_date(fecha);
                ventaFacturacionElectronica.setDescuento_total(resources.getResultSet().getDouble("descuento_total"));
                ventaFacturacionElectronica.setEmpresas_id(resources.getResultSet().getLong("empresas_id"));
                ventaFacturacionElectronica.setFecha(fecha);
                ventaFacturacionElectronica.setFechaISO(fecha);
                ventaFacturacionElectronica.setId_venta(movimientoId);
                ventaFacturacionElectronica.setIdentificacionPersona(resources.getResultSet().getString("identificacionpersona"));
                ventaFacturacionElectronica.setImpreso(resources.getResultSet().getString("impreso"));
                ventaFacturacionElectronica.setImpuesto_total(resources.getResultSet().getDouble("impuesto_total"));
                ventaFacturacionElectronica.setMovimiento_estado(resources.getResultSet().getString("estado_movimiento"));
                ventaFacturacionElectronica.setNombresPersona(resources.getResultSet().getString("nombrespersona"));
                ventaFacturacionElectronica.setOperacion(Integer.parseInt(resources.getResultSet().getString("tipo")));
                ventaFacturacionElectronica.setOrigen_id("0");
                ventaFacturacionElectronica.setPersona_id(0);
                ventaFacturacionElectronica.setPersona_nit("0");
                ventaFacturacionElectronica.setPersona_nombre("");
                ventaFacturacionElectronica.setPrefijo(resources.getResultSet().getString("prefijo"));
                ventaFacturacionElectronica.setTercero_codigo_sap(resources.getResultSet().getString("tercero_codigo_sap"));
                ventaFacturacionElectronica.setTercero_correo(resources.getResultSet().getString("tercero_correo"));
                ventaFacturacionElectronica.setTercero_id("0");
                ventaFacturacionElectronica.setTercero_nit(Long.parseLong(resources.getResultSet().getString("tercero_nit")));
                ventaFacturacionElectronica.setTercero_nombre(resources.getResultSet().getString("tercero_nombre"));
                ventaFacturacionElectronica.setTercero_responsabilidad_fiscal(resources.getResultSet().getString("tercero_responsabilidad_fiscal"));
                ventaFacturacionElectronica.setTercero_tipo_documento(Integer.parseInt(resources.getResultSet().getString("tercero_tipo_documento")));
                ventaFacturacionElectronica.setTercero_tipo_persona(resources.getResultSet().getInt("tercero_tipo_persona"));
                ventaFacturacionElectronica.setVenta_total(resources.getResultSet().getDouble("venta_total"));
                ventaFacturacionElectronica.setCajero(resources.getResultSet().getString("cajero"));
                JsonArray detalles = gson.fromJson(resources.getResultSet().getString("detalles"), JsonArray.class);
                ArrayList<DetallesVentaFacturacionElectronica> detallesVentaFacturacionElectronicas = new ArrayList<>();
                for (JsonElement element : detalles) {
                    JsonObject objDetalle = element.getAsJsonObject();
                    DetallesVentaFacturacionElectronica detallesVentaFacturacionElectronica = new DetallesVentaFacturacionElectronica();
                    detallesVentaFacturacionElectronica.setCantidad(objDetalle.get("cantidad").getAsFloat());
                    detallesVentaFacturacionElectronica.setCompuesto("");
                    detallesVentaFacturacionElectronica.setCortesia(false);
                    detallesVentaFacturacionElectronica.setCosto_producto(objDetalle.get("costo_producto").getAsDouble());
                    detallesVentaFacturacionElectronica.setCosto_unidad(0);
                    detallesVentaFacturacionElectronica.setDescuento_id(0);
                    detallesVentaFacturacionElectronica.setDescuento_producto(objDetalle.get("descuento_calculado").getAsDouble());
                    detallesVentaFacturacionElectronica.setPrecio(objDetalle.get("precio").getAsDouble());
                    detallesVentaFacturacionElectronica.setProducto_descripcion(objDetalle.get("descripcion_producto").getAsString());
                    detallesVentaFacturacionElectronica.setProducto_tipo("");
                    detallesVentaFacturacionElectronica.setSubtotal(objDetalle.get("sub_total").getAsDouble());
                    detallesVentaFacturacionElectronica.setBase(objDetalle.get("base").getAsDouble());
                    detallesVentaFacturacionElectronica.setProductos_plu(objDetalle.get("plu").getAsString());
                    detallesVentaFacturacionElectronica.setProductos_id(objDetalle.get("productos_id").getAsLong());
                    detallesVentaFacturacionElectronica.setUnidad(objDetalle.get("descripcion_unidad").getAsString());
                    AtributosDetalleVentaFacturacionELectronica atributosDetalleVentaFacturacionELectronica = new AtributosDetalleVentaFacturacionELectronica();
                    atributosDetalleVentaFacturacionELectronica.setCategoriaDescripcion(objDetalle.get("categoriadescripcion").getAsString());
                    atributosDetalleVentaFacturacionELectronica.setCategoriaId(Long.parseLong(objDetalle.get("categoriaid").getAsString()));
                    atributosDetalleVentaFacturacionELectronica.setTipo(Integer.parseInt(objDetalle.get("tipo").getAsString()));
                    detallesVentaFacturacionElectronica.setAtributos(atributosDetalleVentaFacturacionELectronica);
                    ArrayList<ImpuestosFacturacionElectronica> impuestosFacturacionElectronicaArr = new ArrayList<>();
                    JsonArray impuesto = objDetalle.get("impuestos").getAsJsonArray();
                    for (JsonElement elementImp : impuesto) {
                        JsonObject objImpo = elementImp.getAsJsonObject();
                        ImpuestosFacturacionElectronica impuestosFacturacionElectronica = new ImpuestosFacturacionElectronica();
                        impuestosFacturacionElectronica.setImpuestos_id(objImpo.get("impuestos_id").getAsInt());
                        impuestosFacturacionElectronica.setTipo(objImpo.get("porcentaje_valor").getAsString());
                        impuestosFacturacionElectronica.setValor(objImpo.get("valor").getAsInt());
                        impuestosFacturacionElectronica.setValor_imp(objImpo.get("impuesto_valor").getAsDouble());
                        impuestosFacturacionElectronicaArr.add(impuestosFacturacionElectronica);
                    }
                    detallesVentaFacturacionElectronica.setImpuestos(impuestosFacturacionElectronicaArr);
                    detallesVentaFacturacionElectronicas.add(detallesVentaFacturacionElectronica);
                }

                JsonArray mediosPgos = gson.fromJson(resources.getResultSet().getString("pagos"), JsonArray.class);
                ArrayList<MediosPagosFactruracionElectronica> mediosPagosFactruracionElectronicas = new ArrayList<>();
                for (JsonElement element : mediosPgos) {
                    JsonObject objMedioPago = element.getAsJsonObject();
                    MediosPagosFactruracionElectronica mediosPagosFactruracionElectronica = new MediosPagosFactruracionElectronica();
                    mediosPagosFactruracionElectronica.setCambio(objMedioPago.get("valor_cambio").getAsDouble());
                    mediosPagosFactruracionElectronica.setRecibido(objMedioPago.get("valor_recibido").getAsDouble());
                    mediosPagosFactruracionElectronica.setValor(objMedioPago.get("valor_total").getAsDouble());
                    mediosPagosFactruracionElectronica.setMedios_pagos_id(objMedioPago.get("ct_medios_pagos_id").getAsInt());
                    mediosPagosFactruracionElectronica.setDescripcion(objMedioPago.get("descripcion").getAsString());
                    mediosPagosFactruracionElectronica.setFormaDePago(objMedioPago.get("forma_de_pago").getAsString());
                    mediosPagosFactruracionElectronicas.add(mediosPagosFactruracionElectronica);
                }

                ObservacionesFacturacionElectronica observacionesFacturacionElectronica = new ObservacionesFacturacionElectronica();
                JsonObject objObseravaciones = gson.fromJson(resources.getResultSet().getString("observaciones"), JsonObject.class);
                objObseravaciones = objObseravaciones.get("observaciones").getAsJsonObject();
                if (objObseravaciones.size() > 0) {
                    observacionesFacturacionElectronica.setId(objObseravaciones.get("id").getAsLong());
                    observacionesFacturacionElectronica.setId_estacion(objObseravaciones.get("id_estacion").getAsLong());
                    observacionesFacturacionElectronica.setRetenedor_iva(objObseravaciones.get("retenedor_iva").getAsBoolean());
                    observacionesFacturacionElectronica.setAutorretenedor(objObseravaciones.get("autorretenedor").getAsBoolean());
                    observacionesFacturacionElectronica.setResponsable_iva(objObseravaciones.get("autorretenedor").getAsBoolean());
                    observacionesFacturacionElectronica.setNotas_adicionales(objObseravaciones.get("notas_adicionales").getAsString());
                    observacionesFacturacionElectronica.setGran_contribuyente(objObseravaciones.get("gran_contribuyente").getAsBoolean());
                    observacionesFacturacionElectronica.setPie_pagina_factura_pos(objObseravaciones.get("pie_pagina_factura_pos").getAsString());
                    observacionesFacturacionElectronica.setAutorretenedor_fecha_inicio(objObseravaciones.get("autorretenedor_fecha_inicio").getAsString());
                    observacionesFacturacionElectronica.setGran_contribuyente_fecha_inicio(objObseravaciones.get("gran_contribuyente_fecha_inicio").getAsString());
                    observacionesFacturacionElectronica.setAutorretenedor_numero_autorizacion(objObseravaciones.get("autorretenedor_numero_autorizacion").getAsString());
                    observacionesFacturacionElectronica.setGran_contribuyente_numero_autorizacion(objObseravaciones.get("gran_contribuyente_numero_autorizacion").getAsString());
                }
                impresionVentaFacturacionElectronica.setVentaFacturacionElectronica(ventaFacturacionElectronica);
                impresionVentaFacturacionElectronica.setDetalles(detallesVentaFacturacionElectronicas);
                impresionVentaFacturacionElectronica.setMediosPagos(mediosPagosFactruracionElectronicas);
                impresionVentaFacturacionElectronica.setCliente(gson.fromJson(resources.getResultSet().getString("cliente"), JsonObject.class));
                impresionVentaFacturacionElectronica.setObservacionesFacturacionElectronica(observacionesFacturacionElectronica);
                impresionVentaFacturacionElectronica.setResoluciones(new GetResolucionesUseCase((long) ventaFacturacionElectronica.getConsecutivo_id()).execute());
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado al momento de obtener la informacion de venta ubicado en la clase " + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return impresionVentaFacturacionElectronica;
    }

    /*public String resoluciones(long id) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "select * from consecutivos c  where c.id = ?";
        String resolucion = "";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                resolucion = rs.getString("observaciones");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado ubicado " + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
        }
        return resolucion;
    }*/

    public JsonObject observaciones() {
        Gson gson = new Gson();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "SELECT jsonb_build_object('observaciones', t) as observaciones FROM (\n"
                + " SELECT \n"
                + " opd.id,\n"
                + " opd.id_estacion,\n"
                + " coalesce (opd.autorretenedor, false) as autorretenedor,\n"
                + " coalesce (opd.autorretenedor_numero_autorizacion, '') as autorretenedor_numero_autorizacion,\n"
                + " coalesce (opd.autorretenedor_fecha_inicio::text, '') as autorretenedor_fecha_inicio,\n"
                + " coalesce (opd.responsable_iva, false) as responsable_iva,\n"
                + " coalesce (opd.gran_contribuyente, false) as gran_contribuyente,\n"
                + " coalesce (opd.gran_contribuyente_numero_autorizacion, '') as gran_contribuyente_numero_autorizacion,\n"
                + " coalesce (opd.gran_contribuyente_fecha_inicio::text, '') gran_contribuyente_fecha_inicio,\n"
                + " coalesce (opd.retenedor_iva, false) as retenedor_iva,\n"
                + " coalesce (opd.notas_adicionales, '') as notas_adicionales,\n"
                + " coalesce (opd.pie_pagina_factura_pos, '') as pie_pagina_factura_pos\n"
                + " FROM public.ct_observacion_por_eds opd) t ;";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                JsonObject observaciones = gson.fromJson(rs.getString("observaciones"), JsonObject.class);
                observaciones = observaciones.get("observaciones").getAsJsonObject();
                if (observaciones == null) {
                    observaciones = new JsonObject();
                }
                return observaciones;
            } else {
                return new JsonObject();
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en le metodo  public JsonObject observaciones() ubicado en la clase " + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
            return new JsonObject();
        }
    }

    public String formaDePago(long idMedioPago) {
        Connection conxeion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String formaDepago = "CONTADO";
        String sql = "select coalesce (mp.mp_atributos::json->'forma_pago'->>'descripcion', '') as forma_de_pago "
                + " from medios_pagos mp "
                + " where id = ?";
        try (PreparedStatement pmt = conxeion.prepareStatement(sql)) {
            pmt.setLong(1, idMedioPago);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                formaDepago = rs.getString("forma_de_pago");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public String formaDePago(long idMedioPago) ubicado en la clase "
                    + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
        }
        return formaDepago;
    }

    public String tipoEmpresa() {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select tte.descripcion  from tbl_tipos_empresas tte where tte.id_tipo_empresa = (select e.id_tipo_empresa from empresas e limit 1)";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("descripcion");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en metodo public String tipoEmpresa() ubicado en la clase "
                    + FacturacionElectronicaDao.class.getName() + "error -> " + e.getMessage());
            return "";

        }
        return "";
    }

    public int tipoNegocio(String negocio) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from tbl_tipos_negocios ttn where ttn.valor = ?";
        int idTipoNegocio = 0;
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, negocio);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                idTipoNegocio = rs.getInt("id_tipo_negocio");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en le metodo  public int tipoNegocio (String negocio) en la clase "
                    + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
        }
        return idTipoNegocio;
    }

    public TreeMap<Integer, TipoNegocio> informacionTiposNegocios() {
        Connection conexion
                = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        TreeMap<Integer, TipoNegocio> infoTipoNegocios = new TreeMap<>();
        String sql = "select * from tbl_tipos_negocios ttn ;";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                TipoNegocio tipoNegocio = new TipoNegocio();
                tipoNegocio.setId(rs.getInt("id_tipo_negocio"));
                tipoNegocio.setDescripcion(
                        rs.getString("descripcion"));
                tipoNegocio.setValor(rs.getString("valor"));

                infoTipoNegocios.put(tipoNegocio.getId(), tipoNegocio);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo "
                    + " informacionTiposNegocios en la clase "
                    + FacturacionElectronicaDao.class.getName()
                    + " error -> " + e.getMessage());
        }
        return infoTipoNegocios;
    }

    public boolean isDefaultFe() {
        String sql = "SELECT\n"
                + "    CASE\n"
                + "        WHEN (SELECT valor FROM wacher_parametros WHERE codigo = 'OBLIGATORIO_FE') = 'S' \n"
                + "             AND (SELECT valor FROM wacher_parametros WHERE codigo = 'MONTO_MINIMO_FE') in ('1', '0') \n"
                + "             THEN true\n"
                + "        ELSE false\n"
                + "    END AS resultado;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                return rs.getBoolean("resultado");
            } else {
                return false;
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public boolean isDefaultFe() ubicado en la clase "
                    + FacturacionElectronicaDao.class.getName()
                    + " error -> " + e.getMessage());
            return false;
        }
    }

    public boolean estaPendienteAsignacionCliente(long idMovimiento) {
        String sql = "SELECT EXISTS("
                + " select * from ct_movimientos_cliente cmc"
                + " where cmc.id_movimiento  = ?"
                + " and cmc.sincronizado =3) AS pendiente;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        boolean clientePendiente = false;
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                clientePendiente = rs.getBoolean("pendiente");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido inesperado en el metodo public boolean estaPendienteAsignacionCliente(long idMovimiento) "
                    + " ubicado -> en " + FacturacionElectronicaDao.class.getName()
                    + " error -> " + e.getMessage());
            clientePendiente = false;
        }
        return clientePendiente;
    }

    public void actualizarEstadoImpresion(long idMovimiento) {
        String sql = "update public.ct_movimientos set pendiente_impresion = true where id = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            int resultado = pmt.executeUpdate();
            if (resultado > 0) {
                NovusUtils.printLn("Estado de la impresion actualizados con exito");
            } else {
                NovusUtils.printLn("No se pudo actualizar los datos de la impresion ");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en le metodo -> "
                    + " public void actualizarEstadoImpresion(long idMovimiento) "
                    + "ubicado en la clase -> " + FacturacionElectronicaDao.class.getName());
        }
    }

    public boolean hayTransaccionesPendientes(long idMovimiento) {
        String sql = "select * from procesos.tbl_transaccion_proceso ttp where id_integracion  in (1,2) and id_estado_integracion = 1 and id_movimiento = ?";
        DatabaseConnectionManager.DatabaseResources resources = null;
        try {
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            resources.getPreparedStatement().setLong(1, idMovimiento);
            resources = DatabaseConnectionManager.executeQuery(resources);
            return resources.getResultSet().next();
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public boolean hayTransaccionesPendientes (long idMovimiento) "
                    + "error -> " + e.getMessage()
                    + " clase -> " + FacturacionElectronicaDao.class.getName());

            return false;
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    public boolean ventasFeFinalizadas(long idMovimiento) {
        String sql = "select * from ct_movimientos_cliente cmc where cmc.id_movimiento_cliente = ? and cmc.sincronizado = 4;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public boolean ventasFeFinalizadas(long idMovimiento) "
                    + "error -> " + e.getMessage()
                    + " clase -> " + FacturacionElectronicaDao.class.getName());
            return false;
        }
    }

}

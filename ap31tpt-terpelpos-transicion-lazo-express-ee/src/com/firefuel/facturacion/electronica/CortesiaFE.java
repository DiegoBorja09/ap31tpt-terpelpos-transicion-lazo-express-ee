package com.firefuel.facturacion.electronica;

import com.bean.MovimientosBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
 * @author Devitech
 */
public class CortesiaFE {

    float totalImpuesto;
    float totalImpuestoN;
    float totalVenta;
    double totalDescuentos;
    float subtotal = 0;
    float subtotalImp = 0;
    float ventaNormal = 0;
    MovimientosBean movimiento = new MovimientosBean();

    static final String FECHA_TRANSACCION = "fechaTransaccion";
    static final String DESCUENTO_TOTAL = "descuentoTotal";
    static final String DESCUENTOS = "descuentos";
    static final String IMPUESTO_TOTAL = "impuestoTotal";
    static final String CORTESIA = "cortesia";
    static final String ID_TRANSACCION_VENTA_DETALLE = "idTransaccionVentaDetalle";
    static final String ID_TRANSACCION_DETALLE_VENTA = "idTransaccionDetalleVenta";
    static final String IDENTIFICADOR_PRODUCTO = "identificadorProducto";
    static final String NOMBRE_PRODUCTO = "nombreProducto";
    static final String IDENTIFICACION_PRODUCTO = "identificacionProducto";
    static final String CANTIDAD_VENTA = "cantidadVenta";
    static final String INDENTIFICAION_UNIDAD = "identificadorUnidad";
    static final String COSTO_PRODUCTO = "costoProducto";
    static final String PRECIO_PRODUCTO = "precioProducto";
    static final String IDENTIFICADOR_DESCUENTO = "identificadorDescuento";
    static final String SUBTOTAL_VENTA = "subTotalVenta";
    static final String COSTO = "costo";
    static final String CATEGORIA_ID = "categoriaId";
    static final String CATEGORIA_DESCRIPCION = "categoriaDescripcion";
    static final String ATRIBUTOS = "atributos";
    static final String ISELECTRONICA = "isElectronica";
    static final String IMPUESTOS_APLICADOS = "impuestosAplicados";
    static final String IDENTIFICADOR_IMPPUESTO = "identificadorImpuesto";
    static final String NOMBRE_IMPUESTO = "nombreImpuesto";
    static final String TIPO_IMPUESTO = "tipoImpuesto";
    static final String VALOR_IMPLICADO = "valorImpAplicado";
    static final String VALOR_IMPUESTO_APLICADO = "valorImpuestoAplicado";
    static final String INGREDIENTES_APLICADOS = "ingredientesAplicados";
    static final String PAGOS = "pagos";
    static final String DATOS_FE = "datos_FE";
    static final String VENTA = "venta";
    static final String DETALLES_VENTA = "detallesVenta";
    static final String IMPOCONSUMO = "impoconsumo";
    static final String PRECIO_VENTA_FE_WEB = "precioVentaFeWeb";
    static final String UNIDAD = "unidad";
    float totalCortesias;

    public JsonObject comboCortesia(JsonObject json) {
        JsonObject dataCortesia = json;
        JsonObject ventasObjeto = dataCortesia.get(DATOS_FE).getAsJsonObject().get(VENTA).getAsJsonObject();
        JsonObject venta = new JsonObject();
        venta.addProperty("identificadorNegocio", ventasObjeto.get("identificadorNegocio").getAsString());
        venta.addProperty("identificacionEstacion", ventasObjeto.get("identificacionEstacion").getAsLong());
        venta.addProperty("codigoEstacion", ventasObjeto.get("codigoEstacion").getAsString());
        venta.addProperty("prefijo", ventasObjeto.get("prefijo").getAsString());
        venta.addProperty("consecutivoActual", ventasObjeto.get("consecutivoActual").getAsLong());
        venta.addProperty("consecutivoInicial", ventasObjeto.get("consecutivoInicial").getAsLong());
        venta.addProperty("consecutivoFinal", ventasObjeto.get("consecutivoFinal").getAsLong());
        venta.addProperty("operacion", ventasObjeto.get("operacion").getAsLong());
        venta.addProperty("consecutivo_id", ventasObjeto.get("consecutivo_id").getAsLong());
        venta.addProperty("persona_id", ventasObjeto.get("persona_id").getAsLong());
        venta.addProperty("persona_nit", ventasObjeto.get("persona_nit").getAsString());
        venta.addProperty("nombreEstacion", ventasObjeto.get("nombreEstacion").getAsString());
        venta.addProperty("aliasEstacion", ventasObjeto.get("aliasEstacion").getAsString());
        venta.addProperty("identificadorEstacion", ventasObjeto.get("identificadorEstacion").getAsString());
        venta.addProperty("identificadorTicketVenta", ventasObjeto.get("identificadorTicketVenta").getAsLong());
        venta.addProperty("identificadorTicket", ventasObjeto.get("identificadorTicket").getAsLong());
        venta.addProperty("idTransaccionVenta", ventasObjeto.get("idTransaccionVenta").getAsLong());
        venta.addProperty(FECHA_TRANSACCION, ventasObjeto.get(FECHA_TRANSACCION).getAsString());
        venta.addProperty("identificadorPromotor", ventasObjeto.get("identificadorPromotor").getAsLong());
        venta.addProperty("nombresPromotor", ventasObjeto.get("nombresPromotor").getAsString());
        venta.addProperty("apellidosPromotor", ventasObjeto.get("apellidosPromotor").getAsString());
        venta.addProperty("identificacionPromotor", ventasObjeto.get("identificacionPromotor").getAsString());
        venta.addProperty("identificadorPersona", ventasObjeto.get("identificadorPersona").getAsLong());
        venta.addProperty("identificacionPersona", ventasObjeto.get("identificacionPersona").getAsString());
        venta.addProperty("nombresPersona", ventasObjeto.get("nombresPersona").getAsString());
        venta.addProperty("apellidosPersona", ventasObjeto.get("apellidosPersona").getAsString());
        venta.addProperty("identificadorProveedor", ventasObjeto.get("identificadorProveedor").getAsLong());
        venta.addProperty("identificadorBodega", ventasObjeto.get("identificadorBodega").getAsLong());
        venta.addProperty("nombresBodega", ventasObjeto.get("nombresBodega").getAsString());
        venta.addProperty("codigoBodega", ventasObjeto.get("codigoBodega").getAsString());
        venta.addProperty("costoTotal", ventasObjeto.get("costoTotal").getAsFloat());
        venta.addProperty("ventaTotal", ventasObjeto.get("ventaTotal").getAsFloat());
        venta.addProperty(DESCUENTO_TOTAL, ventasObjeto.get(DESCUENTO_TOTAL).getAsFloat());
        venta.addProperty(IMPUESTO_TOTAL, ventasObjeto.get(IMPUESTO_TOTAL).getAsFloat());
        venta.addProperty("identificadorEquipo", ventasObjeto.get("identificadorEquipo").getAsLong());
        venta.addProperty("impresoTiquete", ventasObjeto.get("impresoTiquete").getAsString());
        venta.addProperty("usoDolar", ventasObjeto.get("usoDolar").getAsFloat());
        venta.addProperty("identificadorJornada", ventasObjeto.get("identificadorJornada").getAsLong());
        venta.addProperty("identificadorOrigen", ventasObjeto.get("identificadorOrigen").getAsLong());

        JsonArray pago = new JsonArray();
        JsonArray ventas = new JsonArray();
        JsonArray detalleVenta = dataCortesia.get(DATOS_FE).getAsJsonObject().get(DETALLES_VENTA).getAsJsonArray();
        for (JsonElement element : detalleVenta) {
            JsonObject ventaObj = new JsonObject();
            JsonObject elementoVenta = element.getAsJsonObject();

            if (elementoVenta.get(CORTESIA).getAsBoolean()) {
                ventaObj.addProperty(ID_TRANSACCION_VENTA_DETALLE, elementoVenta.get(ID_TRANSACCION_VENTA_DETALLE).getAsLong());
                ventaObj.addProperty(ID_TRANSACCION_DETALLE_VENTA, elementoVenta.get(ID_TRANSACCION_DETALLE_VENTA).getAsLong());
                ventaObj.addProperty(IDENTIFICADOR_PRODUCTO, elementoVenta.get(IDENTIFICADOR_PRODUCTO).getAsLong());
                ventaObj.addProperty(NOMBRE_PRODUCTO, elementoVenta.get(NOMBRE_PRODUCTO).getAsString());
                ventaObj.addProperty(IDENTIFICACION_PRODUCTO, elementoVenta.get(IDENTIFICACION_PRODUCTO).getAsString());
                ventaObj.addProperty(FECHA_TRANSACCION, elementoVenta.get(FECHA_TRANSACCION).getAsString());
                ventaObj.addProperty(CANTIDAD_VENTA, elementoVenta.get(CANTIDAD_VENTA).getAsLong());
                ventaObj.addProperty(INDENTIFICAION_UNIDAD, elementoVenta.get(INDENTIFICAION_UNIDAD).getAsLong());
                ventaObj.addProperty(COSTO_PRODUCTO, elementoVenta.get(COSTO_PRODUCTO).getAsFloat());
                ventaObj.addProperty(PRECIO_PRODUCTO, elementoVenta.get(PRECIO_PRODUCTO).getAsFloat());
                ventaObj.addProperty(UNIDAD, elementoVenta.get(UNIDAD).getAsString());
                ventaObj.addProperty(PRECIO_VENTA_FE_WEB, elementoVenta.get(PRECIO_VENTA_FE_WEB).getAsFloat());
                ventaObj.addProperty(IMPOCONSUMO, 0);
                ventaObj.addProperty(IDENTIFICADOR_DESCUENTO, 0);
                ventaObj.addProperty(DESCUENTO_TOTAL, 0);
                ventaObj.addProperty(DESCUENTO_TOTAL, 0);
                ventaObj.addProperty(SUBTOTAL_VENTA, 0);
                ventaObj.addProperty(CORTESIA, elementoVenta.get(CORTESIA).getAsBoolean());
                ventaObj.addProperty("producto_tipo", "P");
                JsonObject atributos = new JsonObject();
                JsonObject objAtributos = elementoVenta.get(ATRIBUTOS).getAsJsonObject();
                atributos.addProperty(CATEGORIA_ID, 1);
                atributos.addProperty(CATEGORIA_DESCRIPCION, "");
                atributos.addProperty("tipo", 1);
                atributos.addProperty(ISELECTRONICA, objAtributos.get(ISELECTRONICA).getAsString());
                ventaObj.add(ATRIBUTOS, atributos);
                JsonArray arrIngreDientes = new JsonArray();
                JsonArray impu = new JsonArray();
                double valor = 0;
                double valorBase = 0;
                for (JsonElement elemt : elementoVenta.get(IMPUESTOS_APLICADOS).getAsJsonArray()) {
                    JsonObject impuesto = elemt.getAsJsonObject();
                    JsonObject detalleImp = new JsonObject();
                    detalleImp.addProperty(IDENTIFICADOR_IMPPUESTO, impuesto.get(IDENTIFICADOR_IMPPUESTO).getAsLong());
                    detalleImp.addProperty(NOMBRE_IMPUESTO, impuesto.get(NOMBRE_IMPUESTO) != null && !impuesto.get(NOMBRE_IMPUESTO).isJsonNull() ? impuesto.get(NOMBRE_IMPUESTO).getAsString() : "");
                    detalleImp.addProperty(TIPO_IMPUESTO, impuesto.get(TIPO_IMPUESTO).getAsString());
                    detalleImp.addProperty(VALOR_IMPLICADO, impuesto.get(VALOR_IMPLICADO).getAsFloat());
                    detalleImp.addProperty(VALOR_IMPUESTO_APLICADO, impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat());
                    if (impuesto.get(TIPO_IMPUESTO).getAsString().equals("%")) {
                        double descuentos = impuesto.get(VALOR_IMPUESTO_APLICADO).getAsDouble();
                        totalDescuentos += descuentos;
                        valor += impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat();
                        valorBase += impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat();
                        totalImpuesto += impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat();
                    }
                    impu.add(detalleImp);
                }
                JsonArray descuntosTotales = new JsonArray();
                JsonObject descunetos = new JsonObject();
                descunetos.addProperty("porcentaje", 100);
                descunetos.addProperty("valor", valor);
                descunetos.addProperty("base", valorBase);
                descunetos.addProperty("descripcion", "IMPUESTO CORTESIA");
                descuntosTotales.add(descunetos);
                ventaObj.add(DESCUENTOS, descuntosTotales);
                totalCortesias += elementoVenta.get(SUBTOTAL_VENTA).getAsFloat();
                JsonArray detallesIngredientes = elementoVenta.get(INGREDIENTES_APLICADOS).getAsJsonArray();
                if (detallesIngredientes.size() > 0) {
                    for (JsonElement detallesIngrediente : detallesIngredientes) {
                        JsonObject ingredientes = detallesIngrediente.getAsJsonObject();
                        arrIngreDientes.add(ingredientes.getAsJsonObject());
                    }
                }
                ventaObj.add(INGREDIENTES_APLICADOS, arrIngreDientes);
                ventaObj.add(IMPUESTOS_APLICADOS, impu);
                double base = elementoVenta.get("base").getAsDouble();
                ventaObj.addProperty("base", base);
            } else {
                ventaObj.addProperty(ID_TRANSACCION_VENTA_DETALLE, elementoVenta.get(ID_TRANSACCION_VENTA_DETALLE).getAsLong());
                ventaObj.addProperty(ID_TRANSACCION_DETALLE_VENTA, elementoVenta.get(ID_TRANSACCION_DETALLE_VENTA).getAsLong());
                ventaObj.addProperty(IDENTIFICADOR_PRODUCTO, elementoVenta.get(IDENTIFICADOR_PRODUCTO).getAsLong());
                ventaObj.addProperty(NOMBRE_PRODUCTO, elementoVenta.get(NOMBRE_PRODUCTO).getAsString());
                ventaObj.addProperty(IDENTIFICACION_PRODUCTO, elementoVenta.get(IDENTIFICACION_PRODUCTO).getAsString());
                ventaObj.addProperty(FECHA_TRANSACCION, elementoVenta.get(FECHA_TRANSACCION).getAsString());
                ventaObj.addProperty(CANTIDAD_VENTA, elementoVenta.get(CANTIDAD_VENTA).getAsLong());
                ventaObj.addProperty(INDENTIFICAION_UNIDAD, elementoVenta.get(INDENTIFICAION_UNIDAD).getAsLong());
                ventaObj.addProperty(COSTO_PRODUCTO, elementoVenta.get(COSTO_PRODUCTO).getAsFloat());
                ventaObj.addProperty(PRECIO_PRODUCTO, elementoVenta.get(PRECIO_PRODUCTO).getAsFloat());
                ventaObj.addProperty(UNIDAD, elementoVenta.get(UNIDAD).getAsString());
                ventaObj.addProperty(IDENTIFICADOR_DESCUENTO, elementoVenta.get(IDENTIFICADOR_DESCUENTO).getAsLong());
                ventaObj.addProperty(DESCUENTO_TOTAL, elementoVenta.get(DESCUENTO_TOTAL).getAsFloat());
                ventaObj.addProperty(SUBTOTAL_VENTA, elementoVenta.get(SUBTOTAL_VENTA).getAsDouble());
                ventaObj.addProperty(PRECIO_VENTA_FE_WEB, elementoVenta.get(PRECIO_VENTA_FE_WEB).getAsFloat());
                ventaObj.addProperty(IMPOCONSUMO, elementoVenta.get(IMPOCONSUMO).getAsDouble());
                totalVenta += elementoVenta.get(SUBTOTAL_VENTA).getAsFloat();
                ventaObj.addProperty(CORTESIA, elementoVenta.get(CORTESIA).getAsBoolean());
                JsonObject atributos = new JsonObject();
                JsonObject objAtributos = elementoVenta.get(ATRIBUTOS).getAsJsonObject();
                atributos.addProperty(CATEGORIA_ID, objAtributos.get(CATEGORIA_ID).getAsLong());
                atributos.addProperty(CATEGORIA_DESCRIPCION, objAtributos.get(CATEGORIA_DESCRIPCION).getAsString());
                atributos.addProperty("tipo", objAtributos.get("tipo").getAsLong());
                atributos.addProperty(ISELECTRONICA, objAtributos.get(ISELECTRONICA).getAsString());
                ventaObj.add(ATRIBUTOS, atributos);

                //ingredientes
                JsonArray ingredientesAplicados = new JsonArray();
                JsonArray detallesIngredientes = elementoVenta.get(INGREDIENTES_APLICADOS).getAsJsonArray();
                if (detallesIngredientes.size() > 0) {
                    for (JsonElement detallesIngrediente : detallesIngredientes) {
                        JsonObject ingredientes = detallesIngrediente.getAsJsonObject();
                        ingredientesAplicados.add(ingredientes.getAsJsonObject());
                    }
                }
                JsonArray imp = elementoVenta.get(IMPUESTOS_APLICADOS).getAsJsonArray();
                JsonArray impuestosAplicados = new JsonArray();
                for (JsonElement jsonElements : imp) {
                    JsonObject impuesto = jsonElements.getAsJsonObject();
                    JsonObject detalleImp = new JsonObject();
                    detalleImp.addProperty(IDENTIFICADOR_IMPPUESTO, impuesto.get(IDENTIFICADOR_IMPPUESTO).getAsLong());
                    detalleImp.addProperty(NOMBRE_IMPUESTO, impuesto.get(NOMBRE_IMPUESTO) != null && !impuesto.get(NOMBRE_IMPUESTO).isJsonNull() ? impuesto.get(NOMBRE_IMPUESTO).getAsString() : "");
                    detalleImp.addProperty(TIPO_IMPUESTO, impuesto.get(TIPO_IMPUESTO).getAsString());
                    detalleImp.addProperty(VALOR_IMPLICADO, impuesto.get(VALOR_IMPLICADO).getAsFloat());
                    detalleImp.addProperty(VALOR_IMPUESTO_APLICADO, impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat());
                    if (impuesto.get(TIPO_IMPUESTO).getAsString().equals("%")) {
                        totalImpuesto += impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat();
                        totalVenta += impuesto.get(VALOR_IMPUESTO_APLICADO).getAsFloat();
                    }
                    impuestosAplicados.add(detalleImp);
                }
                ventaObj.add(INGREDIENTES_APLICADOS, ingredientesAplicados);
                ventaObj.add(IMPUESTOS_APLICADOS, impuestosAplicados);
                double base = elementoVenta.get("base").getAsDouble();
                ventaObj.addProperty("base", base);
            }
            ventas.add(ventaObj);
        }

        JsonArray pagosArr = dataCortesia.get(DATOS_FE).getAsJsonObject().get(PAGOS).getAsJsonArray();
        for (JsonElement jsonElement : pagosArr) {
            JsonObject objPagos = jsonElement.getAsJsonObject();
            JsonObject pagos = new JsonObject();
            pagos.addProperty("descripcionMedio", objPagos.get("descripcionMedioPago").getAsString());
            pagos.addProperty("identificacionMediosPagos", objPagos.get("identificacionMediosPagos").getAsLong());
            pagos.addProperty("recibidoMedioPago", objPagos.get("recibidoMedioPago").getAsFloat());
            pagos.addProperty("totalMedioPago", objPagos.get("totalMedioPago").getAsFloat());
            pagos.addProperty("vueltoMedioPago", objPagos.get("vueltoMedioPago").getAsFloat());
            pagos.addProperty("identificacionComprobante", objPagos.get("identificacionComprobante").getAsString());
            pagos.addProperty("monedaLocal", objPagos.get("monedaLocal").getAsString());
            pagos.addProperty("trm", objPagos.get("trm").getAsLong());
            pago.add(pagos);
        }
        JsonObject data = new JsonObject();
        venta.addProperty(IMPUESTO_TOTAL, totalImpuesto);
        venta.addProperty("ventaTotal", totalVenta);
        venta.addProperty(DESCUENTO_TOTAL, totalDescuentos);

        venta.addProperty("datosAdicionales", true);
        data.add(VENTA, venta);
        data.add(DETALLES_VENTA, ventas);
        data.add(PAGOS, pago);
        JsonObject datosAdicionales = new JsonObject();
        datosAdicionales.add(VENTA, venta);
        datosAdicionales.add(DETALLES_VENTA, ventas);
        datosAdicionales.add(PAGOS, pago);
        data.add("datos_adcionales", datosAdicionales);
        return data;
    }
}

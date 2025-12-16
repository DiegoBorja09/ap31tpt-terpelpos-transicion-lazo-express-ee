package com.neo.print.services;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.firefuel.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintException;

public class AuditoriaPromotor {

    static DecimalFormat dff = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    SimpleDateFormat xsdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);

    JsonObject data = new JsonObject();
    JsonObject info = new JsonObject();

    JsonArray negocio = new JsonArray();

    JsonArray kiosco = new JsonArray();
    JsonArray canastilla = new JsonArray();

    JsonArray mediosPago = new JsonArray();
    JsonArray ventasCombustible = new JsonArray();
    JsonArray ventasComplementarios = new JsonArray();
    JsonArray medios = new JsonArray();

    String promotor = "";
    Date fecha;
    int totalCalibraciones;

    public AuditoriaPromotor(JsonObject json, String promotor, Date fecha) {
        this.data = json;
        this.promotor = promotor;
        this.fecha = fecha;
        init();
    }

    void init() {
        getInfoVentasProductos();
        getVentasCombustible();
        getComplementarios();
        ordenarMediosPago();
        getTotalMediosNegocio(mediosPago);
    }

    public static void addLine(ArrayList<byte[]> lista) {
        int fullPage = 48;
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
        lista.add(TCPPrinterService.TXT_NORMAL);
        lista.add(printChar(fullPage, "-").getBytes());
        lista.add(TCPPrinterService.TXT_FONT_C);
    }

    public static String printChar(int i, String string) {
        String line = "";
        for (int j = 1; j <= i; j++) {
            line += string;
        }
        return line + "\r\n";
    }

    public void addSectionTitleList(ArrayList<byte[]> lista, String title) {
        addLine(lista);
        lista.add(TCPPrinterService.TXT_ALIGN_CT);
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add((title + "\r\n").getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
        addLine(lista);
    }

    public void getInfoVentasProductos() {
        negocio = data.get("data").getAsJsonObject().get("promotor").getAsJsonArray();

        //Se Ordena el Array
        JsonArray jsonArrayOrdenado = getJsonArrayOrdenado(negocio, "producto");

        Map<String, JsonObject> dataKiosco = new TreeMap<>();
        Map<String, JsonObject> dataCanastilla = new TreeMap<>();

        for (JsonElement element : jsonArrayOrdenado) {
            JsonObject dataRespuesta = element.getAsJsonObject();
            switch (dataRespuesta.get("tipo").getAsString()) {
                case "canastilla":
                    JsonObject infoCan = new JsonObject();
                    String keyProductCan = dataRespuesta.get("id").getAsString().concat("-").concat(dataRespuesta.get("precio").getAsFloat() + "");
                    infoCan.addProperty("producto", dataRespuesta.get("producto").getAsString());
                    infoCan.addProperty("codigo", dataRespuesta.get("id").getAsInt());
                    infoCan.addProperty("cantidad", dataRespuesta.get("cantidad").getAsFloat());
                    infoCan.addProperty("pdv", dataRespuesta.get("precio").getAsFloat());
                    infoCan.addProperty("total", dataRespuesta.get("total").getAsFloat());
                    if (dataCanastilla.isEmpty()) {
                        dataCanastilla.put(keyProductCan, infoCan);
                    } else {
                        if (dataCanastilla.get(keyProductCan) != null) {
                            JsonObject infoConsolidada = dataCanastilla.get(keyProductCan);
                            infoConsolidada.addProperty("cantidad", infoConsolidada.get("cantidad").getAsFloat() + infoCan.get("cantidad").getAsFloat());
                            infoConsolidada.addProperty("total", infoConsolidada.get("total").getAsFloat() + infoCan.get("total").getAsFloat());
                            dataCanastilla.remove(keyProductCan);
                            dataCanastilla.put(keyProductCan, infoConsolidada);
                        } else {
                            dataCanastilla.put(keyProductCan, infoCan);
                        }
                    }
                    break;
                case "kiosco":
                    JsonObject infoKco = new JsonObject();
                    String keyProductKco = dataRespuesta.get("id").getAsString().concat("-").concat(dataRespuesta.get("precio").getAsFloat() + "");
                    infoKco.addProperty("producto", dataRespuesta.get("producto").getAsString());
                    infoKco.addProperty("codigo", dataRespuesta.get("id").getAsInt());
                    infoKco.addProperty("cantidad", dataRespuesta.get("cantidad").getAsFloat());
                    infoKco.addProperty("pdv", dataRespuesta.get("precio").getAsFloat());
                    infoKco.addProperty("total", dataRespuesta.get("total").getAsFloat());
                    if (dataKiosco.isEmpty()) {
                        dataKiosco.put(keyProductKco, infoKco);
                    } else {
                        if (dataKiosco.get(keyProductKco) != null) {
                            JsonObject infoConsolidada = dataKiosco.get(keyProductKco);
                            infoConsolidada.addProperty("cantidad", infoConsolidada.get("cantidad").getAsFloat() + infoKco.get("cantidad").getAsFloat());
                            infoConsolidada.addProperty("total", infoConsolidada.get("total").getAsFloat() + infoKco.get("total").getAsFloat());
                            dataKiosco.remove(keyProductKco);
                            dataKiosco.put(keyProductKco, infoConsolidada);
                        } else {
                            dataKiosco.put(keyProductKco, infoKco);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        dataCanastilla.forEach((t, u) -> canastilla.add(u.getAsJsonObject()));
        dataKiosco.forEach((t, u) -> kiosco.add(u.getAsJsonObject()));
    }

    public JsonArray getVentasCombustible() {
        float descuentoTotal = 0f;

        JsonArray ventas = data.get("data").getAsJsonObject().get("promotor").getAsJsonArray();
        float galones;

        //Se Ordena el Array
        JsonArray jsonArrayOrdenado = getJsonArrayOrdenado(ventas, "producto");

        Map<String, JsonObject> dataCombustible = new TreeMap<>();
        for (JsonElement venta : jsonArrayOrdenado) {
            JsonObject ventaInfo = venta.getAsJsonObject();
            if (ventaInfo.get("tipo").getAsString().equals("combustible") || ventaInfo.get("tipo").getAsString().equals("calibracion")) {
                String producto = ventaInfo.get("producto").getAsString();
                JsonObject infoCombustible = new JsonObject();
                galones = ventaInfo.get("cantidad").getAsFloat();
                infoCombustible.addProperty("producto", ventaInfo.get("producto").getAsString());
                infoCombustible.addProperty("precio", ventaInfo.get("precio").getAsFloat());
                infoCombustible.addProperty("galones", galones);
                infoCombustible.addProperty("descuento", ventaInfo.get("descuento_calculado").getAsFloat());
                infoCombustible.addProperty("total", ventaInfo.get("total").getAsFloat());
                String precioDif = ventaInfo.get("precio").getAsFloat() + "";

                float descuentoVenta = ventaInfo.get("descuento_calculado").getAsFloat();
                descuentoTotal += descuentoVenta;

                if (dataCombustible.isEmpty()) {
                    dataCombustible.put(producto.concat("-").concat(precioDif), infoCombustible);
                } else {
                    if (dataCombustible.get(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif)) != null) {
                        JsonObject familias = dataCombustible.get(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif));
                        familias.addProperty("total", familias.get("total").getAsFloat() + infoCombustible.get("total").getAsFloat());
                        familias.addProperty("galones", familias.get("galones").getAsFloat() + infoCombustible.get("galones").getAsFloat());
                        familias.addProperty("descuento", familias.get("descuento").getAsFloat() + infoCombustible.get("descuento").getAsFloat());
                        dataCombustible.remove(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif));
                        dataCombustible.put(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif), familias);
                    } else {
                        dataCombustible.put(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif), infoCombustible);
                    }
                }
            }
        }
        dataCombustible.forEach((t, u) -> ventasCombustible.add(u.getAsJsonObject()));
        data.addProperty("descuentoTotal", descuentoTotal);
        NovusUtils.printLn("Ventas Combustible: " + ventasCombustible);
        return ventasCombustible;
    }

    private JsonArray getComplementarios() {
        JsonArray ventas = data.get("data").getAsJsonObject().get("promotor").getAsJsonArray();
        JsonArray jsonArrayOrdenado = getJsonArrayOrdenado(ventas, "producto");
        float descuentoTotal = 0f;
        Map<String, JsonObject> complementarios = new TreeMap<>();
        float galones;
        for (JsonElement objOrdenado : jsonArrayOrdenado) {
            JsonObject ventaInfo = objOrdenado.getAsJsonObject();
            if (ventaInfo.get("tipo").getAsString().equals("complementarios")) {
                String producto = ventaInfo.get("producto").getAsString();
                JsonObject infoComplementarios = new JsonObject();
                galones = ventaInfo.get("cantidad").getAsFloat();
                infoComplementarios.addProperty("producto", ventaInfo.get("producto").getAsString());
                infoComplementarios.addProperty("pdv", ventaInfo.get("precio").getAsFloat());
                infoComplementarios.addProperty("cantidad", galones);
                infoComplementarios.addProperty("descuento", ventaInfo.get("descuento_calculado").getAsFloat());
                infoComplementarios.addProperty("total", ventaInfo.get("total").getAsFloat());
                String precioDif = ventaInfo.get("precio").getAsFloat() + "";
                float descuentoVenta = ventaInfo.get("descuento_calculado").getAsFloat();
                descuentoTotal += descuentoVenta;
                if (complementarios.isEmpty()) {
                    complementarios.put(producto.concat("-").concat(precioDif), infoComplementarios);
                } else {
                    if (complementarios.get(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif)) != null) {
                        JsonObject venta = complementarios.get(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif));
                        venta.addProperty("total", venta.get("total").getAsFloat() + infoComplementarios.get("total").getAsFloat());
                        venta.addProperty("cantidad", venta.get("cantidad").getAsFloat() + infoComplementarios.get("cantidad").getAsFloat());
                        venta.addProperty("descuento", venta.get("descuento").getAsFloat() + infoComplementarios.get("descuento").getAsFloat());
                        complementarios.remove(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif));
                        complementarios.put(ventaInfo.get("producto").getAsString().concat("-").concat(precioDif), venta);
                    } else {
                        complementarios.put(producto.concat("-").concat(precioDif), infoComplementarios);
                    }
                }
            }
        }
        complementarios.forEach((t, u) -> ventasComplementarios.add(u.getAsJsonObject()));
        data.addProperty("descuentoTotal", descuentoTotal);
        return ventasComplementarios;
    }

    public JsonArray ordenarMediosPago() {
        JsonArray ventas = data.get("data").getAsJsonObject().get("medios_pagos").getAsJsonArray();
        JsonArray ventaCombustible = new JsonArray();
        for (JsonElement jsonElement : ventas) {
            JsonObject dataOrden = jsonElement.getAsJsonObject();
            if (!dataOrden.get("tipo").getAsString().equals("calibracion")) {
                dataOrden.addProperty("medio_pago", dataOrden.get("medio_pago").getAsString());
                dataOrden.addProperty("tipo", dataOrden.get("tipo").getAsString());
                dataOrden.addProperty("total", dataOrden.get("total").getAsFloat());
            } else {
                dataOrden.addProperty("medio_pago", "CALIBRACION");
                dataOrden.addProperty("tipo", "combustible");
            }
            ventaCombustible.add(dataOrden);
        }
        mediosPago = ventaCombustible;

        return mediosPago;
    }

    public JsonArray getTotalMediosNegocio(JsonArray json) {
        JsonArray mediosN = json;

        //Se Ordena el Array
        JsonArray jsonArrayOrdenado = getJsonArrayOrdenado(mediosN, "medio_pago");

        JsonObject mediosNegocio = new JsonObject();

        long totalKcoVentas = 0;
        long totalCanVentas = 0;
        long totalCombVentas = 0;
        long totalComplementariosbVentas = 0;
        String medioPago = "";
        for (JsonElement medio : jsonArrayOrdenado) {
            JsonObject dataMedios = medio.getAsJsonObject();
            if (!mediosNegocio.has(dataMedios.get("medio_pago").getAsString())) {
                JsonObject infoMedios = new JsonObject();
                infoMedios.addProperty("medio", dataMedios.get("medio_pago").getAsString());
                mediosNegocio.add(dataMedios.get("medio_pago").getAsString(), infoMedios);
                medios.add(infoMedios);
            }
            if (!medioPago.equalsIgnoreCase(dataMedios.get("medio_pago").getAsString())) {
                totalKcoVentas = 0;
                totalCanVentas = 0;
                totalCombVentas = 0;
                totalComplementariosbVentas = 0;
                medioPago = dataMedios.get("medio_pago").getAsString();
            }
            if (mediosNegocio.has(dataMedios.get("medio_pago").getAsString())) {
                switch (dataMedios.get("tipo").getAsString()) {
                    case "canastilla":
                        totalCanVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalCanVentas);
                        break;
                    case "kiosco":
                        totalKcoVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalKcoVentas);
                        break;
                    case "combustible":
                        totalCombVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalCombVentas);
                        break;
                    case "complementarios":
                        totalComplementariosbVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalComplementariosbVentas);
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        return medios;
    }

    public void printAuditoriaPromotor() {
        
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║   🚀 ARQUITECTURA HEXAGONAL - ARQUEO PROMOTOR            ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        
        try {
            // 1️⃣ PROCESAR DATOS DEL ARQUEO (Use Case)
            NovusUtils.printLn("📋 Paso 1: Procesando datos del arqueo...");
            com.application.useCases.reportes.ProcesarDatosArqueoUseCase procesarUseCase = 
                new com.application.useCases.reportes.ProcesarDatosArqueoUseCase(data, promotor, fecha, null);
            com.domain.dto.reportes.ArqueoProcesadoDTO arqueoProcessado = procesarUseCase.execute();
            NovusUtils.printLn("✅ Datos procesados correctamente");
            
            // 2️⃣ IMPRIMIR USANDO MICROSERVICIO PYTHON (Use Case)
            NovusUtils.printLn("🖨️  Paso 2: Enviando a microservicio de impresión Python...");
            com.application.useCases.reportes.ImprimirArqueoPromotorUseCase imprimirUseCase = 
                new com.application.useCases.reportes.ImprimirArqueoPromotorUseCase(arqueoProcessado);
            com.bean.ResultBean result = imprimirUseCase.execute();
            
            if (result.isSuccess()) {
                NovusUtils.printLn("✅ Impresión completada exitosamente");
            } else {
                // ❌ ERROR: Servicio Python no disponible O impresora desconfigurada
                // El mensaje ya viene amigable desde Python/Adapter
                String errorMessage = result.getMessage();
                
                // 📋 Logs técnicos detallados
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("ERROR AL IMPRIMIR ARQUEO PROMOTOR");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("Mensaje: " + errorMessage);
                NovusUtils.printLn("Promotor: " + promotor);
                NovusUtils.printLn("Fecha: " + fecha);
                NovusUtils.printLn("");
                NovusUtils.printLn("  Posibles causas:");
                NovusUtils.printLn("   - Servicio Python caído (puerto 8001)");
                NovusUtils.printLn("   - Impresora desconfigurada (IP incorrecta)");
                NovusUtils.printLn("   - Impresora apagada/desconectada");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                
                // Propagar el mensaje amigable que viene de Python
                throw new RuntimeException(errorMessage);
            }
            
        } catch (RuntimeException e) {
            // Re-lanzar RuntimeException (error de servicio Python no disponible)
            throw e;
        } catch (Exception e) {
            // Error inesperado - NO usar método legacy
            // Mostrar error y propagar excepción en lugar de usar fallback legacy
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn(" ERROR INESPERADO en nueva arquitectura");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("Tipo: " + e.getClass().getName());
            NovusUtils.printLn("Mensaje: " + e.getMessage());
            NovusUtils.printLn("");
            NovusUtils.printLn("  No se pudo imprimir el reporte. No se usará método legacy.");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            e.printStackTrace();
            // Propagar la excepción en lugar de usar legacy
            throw new RuntimeException("Error inesperado al imprimir reporte: " + e.getMessage(), e);
        }
    }
    
    /**
     * 🔄 Método legacy mantenido como fallback.
     * TODO: Eliminar cuando microservicio Python esté 100% estable.
     */
    @Deprecated
    private void printLegacyAuditoriaPromotor() {

        ArrayList<byte[]> lista = new ArrayList<>();
        String fechaInicio = xsdf.format(fecha);

        //--------------------------------------------------------------------//
        addSectionTitleList(lista, "VENTAS POR PROMOTOR");
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add(Utils.text_between("FECHA IMPRESION: " + getFecha(new Date()), "\r\n").getBytes());
        lista.add(Utils.text_between("FECHA INICIO: " + fechaInicio, "\r\n").getBytes());
        lista.add(Utils.text_between("PROMOTOR:  " + promotor, "\r\n").getBytes());
        //--------------------------------------------------------------------//

        //--------------------------COMBUSTIBLE-------------------------------//
        JsonArray vCombustible = ventasCombustible;

        if (vCombustible.size() > 0) {

            int vCombt = data.get("data").getAsJsonObject().get("transacciones").getAsJsonObject().get("combustible").getAsInt();
            float totalDescuentos = data.get("descuentoTotal").getAsFloat();

            lista.add(("\n\r").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.text_between("VENTAS COMBUSTIBLE", "\r\n").getBytes());
            lista.add(Utils.text_between("NUMERO DE TRANSACCIONES: " + vCombt, "\r\n").getBytes());
            lista.add(Utils.text_between("TOTAL DESCUENTOS: " + "$ " + dff.format(totalDescuentos), "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            addLine(lista);

            int totalVentaCombustible = 0;

            String[] colsVentasCombustible = {"DESCRIPCION", "GALONES", "PDV", "DCTO", "TOTAL"};

            int[] tamCombustible = {13, 13, 13, 13, 12};
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentasCombustible, tamCombustible).getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            for (JsonElement jsonElement : vCombustible) {

                JsonObject dataVentasCombustible = jsonElement.getAsJsonObject();

                colsVentasCombustible[0] = dataVentasCombustible.get("producto").getAsString() + "";

                colsVentasCombustible[1] = dataVentasCombustible.get("galones").getAsString();

                colsVentasCombustible[2] = "$ " + dff.format(dataVentasCombustible.get("precio").getAsInt());

                colsVentasCombustible[3] = "$ " + dff.format(dataVentasCombustible.get("descuento").getAsInt());

                colsVentasCombustible[4] = "$ " + dff.format(dataVentasCombustible.get("total").getAsInt());

                lista.add(("\n\r").getBytes());
                lista.add(Utils.format_cols(colsVentasCombustible, tamCombustible).getBytes());
                lista.add(("\n\r").getBytes());

                totalVentaCombustible += dataVentasCombustible.get("total").getAsInt();

            }
            colsVentasCombustible[0] = "";

            colsVentasCombustible[1] = "";

            colsVentasCombustible[2] = "";

            colsVentasCombustible[3] = "TOTAL";

            colsVentasCombustible[4] = "$ " + dff.format(totalVentaCombustible);

            lista.add(("\n\r").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentasCombustible, tamCombustible).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
        }
        //---------------------------CANASTILLA-------------------------------//
        JsonArray vCan = canastilla;
        if (vCan.size() > 0) {

            int vCanastilla = data.get("data").getAsJsonObject().get("transacciones").getAsJsonObject().get("canastilla").getAsInt();

            int totalVentaCanastilla = 0;

            lista.add(("\n\r").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.text_between("VENTAS CANASTILLA", "\r\n").getBytes());
            lista.add(Utils.text_between("NUMERO DE TRANSACCIONES: " + vCanastilla, "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            addLine(lista);

            String[] colsVentasCanKco = {"CODIGO", "DESCRIPCION", "CANTIDAD", "PDV", "TOTAL"};

            int[] tamCanKco = {10, 14, 13, 13, 13};
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentasCanKco, tamCanKco).getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            for (JsonElement jsonElement : vCan) {

                JsonObject dataVentasCanastilla = jsonElement.getAsJsonObject();

                String producto = dataVentasCanastilla.get("producto").getAsString();

                colsVentasCanKco[0] = dataVentasCanastilla.get("codigo").getAsInt() + "";

                if (producto.length() > 14) {
                    producto = producto.substring(0, 14);
                }

                colsVentasCanKco[1] = producto;

                colsVentasCanKco[2] = dataVentasCanastilla.get("cantidad").getAsInt() + "";

                colsVentasCanKco[3] = "$ " + dff.format(dataVentasCanastilla.get("pdv").getAsInt());

                colsVentasCanKco[4] = "$ " + dff.format(dataVentasCanastilla.get("total").getAsInt());

                lista.add(("\n\r").getBytes());
                lista.add(Utils.format_cols(colsVentasCanKco, tamCanKco).getBytes());
                lista.add(("\n\r").getBytes());

                totalVentaCanastilla += dataVentasCanastilla.get("total").getAsInt();
            }

            colsVentasCanKco[0] = "";

            colsVentasCanKco[1] = "";

            colsVentasCanKco[2] = "";

            colsVentasCanKco[3] = "TOTAL";

            colsVentasCanKco[4] = "$ " + dff.format(totalVentaCanastilla);

            lista.add(("\n\r").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentasCanKco, tamCanKco).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
        }

        //----------------------------MARKET----------------------------------//
        JsonArray vKco = kiosco;

        if (vKco.size() > 0) {
            int vKiosco = data.get("data").getAsJsonObject().get("transacciones").getAsJsonObject().get("kiosco").getAsInt();
            lista.add(("\n\r").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.text_between("VENTAS MARKET", "\r\n").getBytes());
            lista.add(Utils.text_between("NUMERO DE TRANSACCIONES: " + vKiosco, "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            addLine(lista);

            int totalVentaKiosco = 0;
            String[] colsVentasCanKco = {"CODIGO", "DESCRIPCION", "CANTIDAD", "PDV", "TOTAL"};
            int[] tamCanKco = {10, 14, 13, 13, 13};

            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentasCanKco, tamCanKco).getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            for (JsonElement jsonElement : vKco) {

                JsonObject dataVentasKiosco = jsonElement.getAsJsonObject();

                String producto = dataVentasKiosco.get("producto").getAsString();

                colsVentasCanKco[0] = dataVentasKiosco.get("codigo").getAsInt() + "";
                if (producto.length() > 14) {
                    producto = producto.substring(0, 14);
                }
                colsVentasCanKco[1] = producto;
                colsVentasCanKco[2] = dataVentasKiosco.get("cantidad").getAsInt() + "";

                colsVentasCanKco[3] = "$ " + dff.format(dataVentasKiosco.get("pdv").getAsInt());

                colsVentasCanKco[4] = "$ " + dff.format(dataVentasKiosco.get("total").getAsInt());

                lista.add(("\n\r").getBytes());
                lista.add(Utils.format_cols(colsVentasCanKco, tamCanKco).getBytes());
                lista.add(("\n\r").getBytes());

                totalVentaKiosco += dataVentasKiosco.get("total").getAsInt();

            }

            colsVentasCanKco[0] = "";

            colsVentasCanKco[1] = "";

            colsVentasCanKco[2] = "";

            colsVentasCanKco[3] = "TOTAL";

            colsVentasCanKco[4] = "$ " + dff.format(totalVentaKiosco);

            lista.add(("\n\r").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentasCanKco, tamCanKco).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("\n\r").getBytes());
        }
        //---------------------COMPLEMENTARIOS--------------------------------//
        if (ventasComplementarios.size() > 0) {
            int vComplementarios = data.get("data").getAsJsonObject().get("transacciones").getAsJsonObject().get("complementarios").getAsInt();
            lista.add(("\n\r").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.text_between("VENTAS COMPLEMENTARIOS", "\r\n").getBytes());
            lista.add(Utils.text_between("NUMERO DE TRANSACCIONES: " + vComplementarios, "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            addLine(lista);

            int totalComplementarios = 0;
            String[] colsVentaComplementario = {"CODIGO", "DESCRIPCION", "CANTIDAD", "PDV", "TOTAL"};
            int[] tamComplementarios = {10, 14, 13, 13, 13};
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentaComplementario, tamComplementarios).getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            for (JsonElement jsonElement : ventasComplementarios) {
                JsonObject dataVentasKiosco = jsonElement.getAsJsonObject();
                String producto = dataVentasKiosco.get("producto").getAsString();
                colsVentaComplementario[0] = "";
                if (producto.length() > 14) {
                    producto = producto.substring(0, 14);
                }
                colsVentaComplementario[1] = producto;
                colsVentaComplementario[2] = dataVentasKiosco.get("cantidad").getAsFloat() + " LT";
                colsVentaComplementario[3] = "$ " + dff.format(dataVentasKiosco.get("pdv").getAsInt());
                colsVentaComplementario[4] = "$ " + dff.format(dataVentasKiosco.get("total").getAsInt());
                lista.add(("\n\r").getBytes());
                lista.add(Utils.format_cols(colsVentaComplementario, tamComplementarios).getBytes());
                lista.add(("\n\r").getBytes());

                totalComplementarios += dataVentasKiosco.get("total").getAsInt();

            }

            colsVentaComplementario[0] = "";

            colsVentaComplementario[1] = "";

            colsVentaComplementario[2] = "";

            colsVentaComplementario[3] = "TOTAL";

            colsVentaComplementario[4] = "$ " + dff.format(totalComplementarios);

            lista.add(("\n\r").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.format_cols(colsVentaComplementario, tamComplementarios).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("\n\r").getBytes());
        }
        //--------------------------------------------------------------------//
        //---------------------RESUMEN MEDIOS PAGO----------------------------//
        addSectionTitleList(lista, "RESUMEN MEDIO DE PAGO POR NEGOCIO");

        String[] colsResumenMedios = {"MEDIO", "COMBUSTIBLE", "CANASTILLA", "MARKET", "COMPL."};

        int[] tamRe = {13, 13, 12, 13, 13};
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add(Utils.format_cols(colsResumenMedios, tamRe).getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);
        JsonArray resumenVentas = medios;
        int totalCombustible = 0;
        int totalCanastilla = 0;
        int totalKiosco = 0;
        int complementario = 0;

        for (JsonElement jsonElement : resumenVentas) {

            JsonObject dataMediosPago = jsonElement.getAsJsonObject();
            String medio = dataMediosPago.get("medio").getAsString();
            if (medio.length() > 13) {
                medio = medio.substring(0, 13);
            }
            
            int kioscoV = dataMediosPago.has("kiosco") ? dataMediosPago.get("kiosco").getAsInt() : 0;
            int canastillaV = dataMediosPago.has("canastilla") ? dataMediosPago.get("canastilla").getAsInt() : 0;
            int combustibleV = dataMediosPago.has("combustible") ? dataMediosPago.get("combustible").getAsInt() : 0;
            int complementarioV = dataMediosPago.has("complementarios") ? dataMediosPago.get("complementarios").getAsInt() : 0;

            totalCombustible += combustibleV;
            totalCanastilla += canastillaV;
            totalKiosco += kioscoV;
            complementario += complementarioV;

            colsResumenMedios[0] = medio + "";
            colsResumenMedios[1] = "$ " + dff.format(combustibleV);
            colsResumenMedios[2] = "$ " + dff.format(canastillaV);
            colsResumenMedios[3] = "$ " + dff.format(kioscoV);
            colsResumenMedios[4] = "$ " + dff.format(complementarioV);

            lista.add(("\n\r").getBytes());
            lista.add(Utils.format_cols(colsResumenMedios, tamRe).getBytes());
            lista.add(("\n\r").getBytes());
        }
        //Total
        totalCombustible += totalCalibraciones;
        colsResumenMedios[0] = "TOTAL";
        colsResumenMedios[1] = "$ " + dff.format(totalCombustible);
        colsResumenMedios[2] = "$ " + dff.format(totalCanastilla);
        colsResumenMedios[3] = "$ " + dff.format(totalKiosco);
        colsResumenMedios[4] = "$ " + dff.format(complementario);
        lista.add(("\n\r").getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add(("\n\r").getBytes());
        lista.add(Utils.format_cols(colsResumenMedios, tamRe).getBytes());
        lista.add(("\n\r").getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);

        //-----------------------RESUMEN POR MEDIO DE PAGO -----------------------------------
        addSectionTitleList(lista, "RESUMEN POR MEDIO DE PAGO");

        String[] colsResumenMediosPagos = {"MEDIO", "", "", "", "TOTAL"};

        int[] tamReMedios = {13, 13, 12, 13, 13};
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add(Utils.format_cols(colsResumenMediosPagos, tamReMedios).getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);

        for (JsonElement jsonElement : medios) {

            JsonObject dataMediosPago = jsonElement.getAsJsonObject();

            String medio = dataMediosPago.get("medio").getAsString();

            if (medio.length() > 13) {
                medio = medio.substring(0, 13);
            }

            int kioscoV = dataMediosPago.has("kiosco") ? dataMediosPago.get("kiosco").getAsInt() : 0;
            int canastillaV = dataMediosPago.has("canastilla") ? dataMediosPago.get("canastilla").getAsInt() : 0;
            int combustibleV = dataMediosPago.has("combustible") ? dataMediosPago.get("combustible").getAsInt() : 0;
            int complementarioV = dataMediosPago.has("complementarios") ? dataMediosPago.get("complementarios").getAsInt() : 0;
            int totalMedio = kioscoV + combustibleV + canastillaV + complementarioV;

            colsResumenMediosPagos[0] = medio + "";
            colsResumenMediosPagos[1] = "";
            colsResumenMediosPagos[2] = "";
            colsResumenMediosPagos[3] = "";
            colsResumenMediosPagos[4] = "$ " + dff.format(totalMedio);

            lista.add(("\n\r").getBytes());
            lista.add(Utils.format_cols(colsResumenMediosPagos, tamReMedios).getBytes());
            lista.add(("\n\r").getBytes());
            //-----------------------------------------------------------------------------------------------
            //Calibraciones
            if (data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject().get("cantidad").getAsInt() > 0) {

                totalCalibraciones = data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject().get("total")
                        != null ? data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject().get("total").getAsInt() : 0;

                colsResumenMedios[0] = "CALIBRACION";

                colsResumenMedios[1] = "$ " + dff.format(totalCalibraciones);

                colsResumenMedios[2] = "$ " + dff.format(0);

                colsResumenMedios[3] = "$ " + dff.format(0);

                colsResumenMedios[4] = "$ " + dff.format(0);

                lista.add(("\n\r").getBytes());
                lista.add(Utils.format_cols(colsResumenMedios, tamRe).getBytes());
                lista.add(("\n\r").getBytes());
            }
        }
        //---------------------------------------------------------------------------------------------
        int cantidadSobres = data.get("data").getAsJsonObject().get("sobres").getAsJsonObject().get("cantidad").getAsInt();
        float totalSobres = !data.get("data").getAsJsonObject().get("sobres").getAsJsonObject().get("total").isJsonNull() ? data.get("data").getAsJsonObject().get("sobres").getAsJsonObject().get("total").getAsFloat() : 0f;

        String valorSobres = dff.format(totalSobres);

        addSectionTitleList(lista, "SOBRES CONSIGNADOS ( " + cantidadSobres + " ) TOTAL: $" + valorSobres);

        try {
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (PrintException | IOException ex) {
            Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String getFecha(Date fecha) {
        NovusUtils.printLn(xsdf.format(fecha));
        String sfecha = xsdf.format(fecha);
        sfecha = sfecha.replaceAll("p. m.", "p.m.");
        sfecha = sfecha.replaceAll("a. m.", "a.m.");
        return sfecha;
    }

    public JsonArray getJsonArrayOrdenado(JsonArray json, String key) {
        JsonArray ordenado = new JsonArray();

        List<JsonObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            jsonValues.add((JsonObject) json.get(i));
        }
        Collections.sort(jsonValues, new Comparator<JsonObject>() {
            String keyName = key;

            @Override
            public int compare(JsonObject a, JsonObject b) {
                String valA = "";
                String valB = "";
                try {
                    valA = (String) a.get(keyName).getAsString();
                    valB = (String) b.get(keyName).getAsString();
                } catch (Exception e) {
                    Logger.getLogger(AuditoriaPromotor.class
                            .getName()).log(Level.SEVERE, null, e);
                }
                return valA.compareTo(valB);
            }
        });
        for (int i = 0; i < json.size(); i++) {
            ordenado.add(jsonValues.get(i));
        }
        return ordenado;
    }

}

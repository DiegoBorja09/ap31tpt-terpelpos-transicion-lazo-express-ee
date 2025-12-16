/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.neo.print.services;

import com.bean.EmpresaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.FacturacionElectronicaDao;
import com.dao.MovimientosDao;
import com.facade.facturacionelectronica.DetallesVentaFacturacionElectronica;
import com.facade.facturacionelectronica.ImpresionVentaFacturacionElectronica;
import com.facade.facturacionelectronica.ImpuestosFacturacionElectronica;
import com.firefuel.Main;
import com.firefuel.Utils;
import com.firefuel.facturacion.electronica.ConfiguracionFE;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintException;

/**
 *
 * @author Devitech
 */
public class ReportesFE {

    SimpleDateFormat xsdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat xsdfFecha = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    SimpleDateFormat xsdfFechaFull = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    DecimalFormat dff = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    MovimientosDao mado = new MovimientosDao();
    static final int FULL_PAGE = 48;
    ConfiguracionFE cfe = new ConfiguracionFE();
    long consumidorFinal = 222222222222l;

    public static String printChar(int i, String string) {
        String line = "";
        for (int j = 1; j <= i; j++) {
            line += string;
        }
        return line + "\r\n";
    }

    public static void addLine(ArrayList<byte[]> lista) {
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
        lista.add(TCPPrinterService.TXT_NORMAL);
        lista.add(printChar(FULL_PAGE, "-").getBytes());
        lista.add(TCPPrinterService.TXT_FONT_C);
    }

    public void addSectionTitleList(ArrayList<byte[]> lista, String title) {
        lista.add(TCPPrinterService.TXT_ALIGN_CT);
        lista.add(TCPPrinterService.TXT_NORMAL);
        lista.add((title + "\r\n").getBytes());
        lista.add(TCPPrinterService.TXT_NORMAL);
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
    }

    public JsonObject printCanastilla(JsonObject json) {
        NovusUtils.printLn("::::::::::::::::::");
        JsonArray detalle = new JsonArray();
        NovusUtils.printLn("Json Ventas: " + json);
        NovusUtils.printLn("::::::::::::::::::");
        FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();

        for (JsonElement element : json.get("detalle").getAsJsonArray()) {
            JsonObject objDetalle = element.getAsJsonObject();
            objDetalle.addProperty("unidad", objDetalle.get("unidad_descripcion").getAsString());
            objDetalle.remove("unidad_descripcion");
            detalle.add(objDetalle);
        }
        int idtipoNegocio = facturacionElectronicaDao.tipoNegocio(negocio(json.get("venta").getAsJsonObject().get("operacion").getAsInt()));
        json.get("venta").getAsJsonObject().addProperty("tipo_negocio", idtipoNegocio);
        json.get("venta").getAsJsonObject().addProperty("placa", "");
        json.add("detalle", detalle);
        return enviarImpresion(json);

    }

    public JsonObject printDuplicadoFe(long movimientId) {
        Gson gson = new Gson();
        FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();
        ImpresionVentaFacturacionElectronica impresionVentaFacturacionElectronica = facturacionElectronicaDao.ventaFeImprimir(movimientId);
        impresionVentaFacturacionElectronica.getDetalles().forEach((detalle) -> {
            impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().setTotal_bruto(impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().getTotal_bruto() + detalle.getSubtotal());
            detalle.setBase(detalle.getBase() - obtenerImpoconsumo(detalle.getImpuestos()));
            detalle.getImpuestos().forEach((impuesto) -> {
                if (impuesto.getTipo().equals("%")) {
                    impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().setTotal_base_imponible(impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().getTotal_base_imponible() + (detalle.getBase() * detalle.getCantidad()));
                }
            });
            if (detalle.getImpuestos().isEmpty()) {
                impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().setTotal_base_imponible(impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().getTotal_base_imponible() + 0);
            }
        });
        if ( impresionVentaFacturacionElectronica.getDetalles().isEmpty()){
            impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().setTotal_base_imponible(0);
        }
        impresionVentaFacturacionElectronica.setTipoEmpresa(facturacionElectronicaDao.tipoEmpresa());
        int idTiponegocio = facturacionElectronicaDao.tipoNegocio(negocio(impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().getOperacion()));
        impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().setTipoNegocio(idTiponegocio);
        impresionVentaFacturacionElectronica.getVentaFacturacionElectronica().setPlaca("");
        String json = gson.toJson(impresionVentaFacturacionElectronica);
        NovusUtils.printLn("venta a imprimir " + json + "");
        return enviarImpresion(gson.fromJson(json, JsonObject.class));
    }
    
    public float obtenerImpoconsumo(List<ImpuestosFacturacionElectronica> impuesto) {
        float impoconsumo = 0;
        for (ImpuestosFacturacionElectronica impuestos : impuesto) {
            if (impuestos.getTipo().equals("$")) {
                impoconsumo = impuestos.getValor();
                return impoconsumo;
            }
        }
        return impoconsumo;
    }

    private JsonObject enviarImpresion(JsonObject json) {
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║   🚀 ARQUITECTURA HEXAGONAL - FACTURA ELECTRÓNICA        ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        
        try {
            // 🚀 NUEVA ARQUITECTURA: Usar caso de uso para imprimir
            com.application.useCases.impresion.ImprimirFacturaElectronicaUseCase imprimirUseCase = 
                new com.application.useCases.impresion.ImprimirFacturaElectronicaUseCase(json);
            
            com.bean.ResultBean result = imprimirUseCase.execute();
            
            // Construir respuesta compatible con código existente
            JsonObject response = new JsonObject();
            
            if (result.isSuccess()) {
                NovusUtils.printLn("✅ Impresión FE completada exitosamente vía Python");
                response.addProperty("message", result.getMessage() != null ? 
                    result.getMessage() : "IMPRESIÓN EXITOSA");
                response.addProperty("status", 200);
            } else {
                // ❌ ERROR: Servicio Python no disponible o falló la impresión
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("❌ ERROR: No se pudo imprimir Factura Electrónica");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("Motivo: " + result.getMessage());
                NovusUtils.printLn("");
                NovusUtils.printLn("⚠️  Verifique que el servicio de impresión esté disponible");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                
                response.addProperty("message", result.getMessage() != null ? 
                    result.getMessage() : "No se pudo conectar al servicio de impresión");
                response.addProperty("status", 500);
            }
            
            return response;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ EXCEPCIÓN en enviarImpresion: " + e.getMessage());
            e.printStackTrace();
            
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("message", "ERROR INESPERADO AL IMPRIMIR");
            errorResponse.addProperty("status", 500);
            return errorResponse;
        }
    }

    public String formatoFecha(String fecha, String formato) {
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        String newfecha = "";
        try {
            Date date = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL).parse(fecha);
            newfecha = sdf.format(date);

        } catch (ParseException ex) {
            Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newfecha;
    }

    private String negocio(int operacion) {
        switch (operacion) {
            case 9:
                return "C";
            case 35:
                return "K";
            default:
                return "";
        }
    }

    public JsonObject printCanastillaRemision(JsonObject json) {
        try {

            NovusUtils.printLn("::::::::::::::::::");
            NovusUtils.printLn("Json Ventas: " + json);
            NovusUtils.printLn("::::::::::::::::::");

            EquipoDao dao = new EquipoDao();
            EmpresaBean empresa;
            empresa = dao.findEmpresa(Main.credencial);
            int FULL_PAGE = 48;
            ArrayList<byte[]> lista = new ArrayList<>();
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            String titulo = "REMISION ELECTRONICA ";
            long numeroTicket = mado.numeroRemision();
            lista.add((titulo + " " + numeroTicket + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((empresa.getRazonSocial() + " \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            addLine(lista);

            addSectionTitleList(lista, "NIT: " + empresa.getNit());
            lista.add(("\r\n").getBytes());
            JsonObject cliente = json.get("cliente").getAsJsonObject().get("extraData").getAsJsonObject().get("datosIdentificacionCliente").getAsJsonObject();
            String direccion = empresa.getDireccionPrincipal();
            String telefono = empresa.getTelefonoPrincipal();
            String fecha = json.get("venta").getAsJsonObject().get("fecha").getAsString();
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("" + empresa.getAlias() + "\r\n").getBytes());
            lista.add(("DIR: " + direccion + "\r\n").getBytes());
            lista.add(("TEL: " + telefono + "\r\n").getBytes());
            String newFecha = formatoFecha(fecha, NovusConstante.SIMPLE_FORMAT);
            lista.add(("FECHA: " + newFecha + "\r\n").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("CLIENTE: " + cliente.get("nombreRegistro").getAsString() + "\r\n").getBytes("Cp858"));
            if (json.get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong() != consumidorFinal) {
                String correo = !json.get("cliente").getAsJsonObject().get("correoElectronico").isJsonNull() ? json.get("cliente").getAsJsonObject().get("correoElectronico").getAsString() : "";
                lista.add(("CORREO: " + correo + "\r\n").getBytes());
            } else if (json.get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong() == consumidorFinal) {
                lista.add(("CORREO: " + "N/A" + "\r\n").getBytes());
            }
            addLine(lista);

            lista.add(TCPPrinterService.TXT_BOLD_ON);
            String colsCOM[] = {"PLU", "DESCRIPCION", "CANTIDAD", "PDV", "TOTAL"};
            int tamCOM[] = {5, 18, 10, 15, 15};
            lista.add(Utils.format_cols(colsCOM, tamCOM).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            JsonArray venta = json.get("detalle").getAsJsonArray();
            float totalVenta = 0;
            for (JsonElement elemt : venta) {

                JsonObject objVenta = elemt.getAsJsonObject();

                String plu = objVenta.has("plu") ? objVenta.get("plu").getAsInt() + "" : objVenta.get("productos_plu").getAsInt() + "";

                colsCOM[0] = plu;
                String producto = objVenta.get("producto_descripcion").getAsString();
                if (producto.length() > 16) {
                    producto = producto.substring(0, 16);
                }
                colsCOM[1] = producto;
                colsCOM[2] = objVenta.get("cantidad").getAsInt() + "";

                long precio = objVenta.get("precio") != null ? objVenta.get("precio").getAsLong() : 0l;

                float total = 0;
                total += objVenta.get("subtotal").getAsFloat();
                for (JsonElement elemtp : objVenta.get("impuestos").getAsJsonArray()) {
                    JsonObject imp = elemtp.getAsJsonObject();
                    boolean cortesia = objVenta.get("cortesia").isJsonNull() ? false : objVenta.get("cortesia").getAsBoolean();
                    if (cortesia) {
                        total = 0;
                    } else {
                        total += imp.get("valor_imp").getAsFloat();
                    }
                }
                colsCOM[3] = "$ " + df.format(total / objVenta.get("cantidad").getAsInt()) + "";
                totalVenta += total;
                colsCOM[4] = "$ " + df.format(total);

                if (total != 0) {
                    lista.add(Utils.format_cols(colsCOM, tamCOM).getBytes());
                    lista.add(("\r\n").getBytes());
                }

            }
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.text_between("", "Total").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(Utils.text_between("", "$ " + df.format(totalVenta)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((cfe.mensajeComprobantes()).getBytes());
            lista.add(("\r\n").getBytes());
            if (totalVenta > 0) {
                try {
                    if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                            .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                        TCPPrinterService service = new TCPPrinterService();
                        service.printBytes(lista);
                    } else {
                        QTEPrinterService service = new QTEPrinterService();
                        service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
                    }
                } catch (PrintException pr) {
                    NovusUtils.printLn("error al imprimir -> " + pr);
                } catch (IOException ex) {
                    Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (DAOException | UnsupportedEncodingException ex) {
            Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);

        }
        return json;
    }

    public void reporteDuplicadoRemision(JsonObject json) {

        try {
            EquipoDao dao = new EquipoDao();
            EmpresaBean empresa;
            empresa = dao.findEmpresa(Main.credencial);
            ArrayList<byte[]> lista = new ArrayList<>();
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            String titulo = "REMISION ELECTRONICA ";
            String numeroTicket = json.get("atributos").getAsJsonObject().has("identificadorTicket")
                    ? String.valueOf(json.get("atributos").getAsJsonObject().get("identificadorTicket").getAsLong())
                    : empresa.getCodigo();
            lista.add((titulo + numeroTicket + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((empresa.getRazonSocial() + " \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            addLine(lista);

            addSectionTitleList(lista, "NIT: " + empresa.getNit());

            lista.add(("\r\n").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(("DUPLICADO\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("\r\n").getBytes());
            JsonObject cliente = json.get("atributos").getAsJsonObject().get("cliente").getAsJsonObject().get("extraData").getAsJsonObject().get("datosIdentificacionCliente").getAsJsonObject();
            String direccion = empresa.getDireccionPrincipal();
            String telefono = empresa.getTelefonoPrincipal();
            String fecha = json.get("fecha").getAsString();
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("" + empresa.getAlias() + "\r\n").getBytes());
            lista.add(("DIR: " + direccion + "\r\n").getBytes());
            lista.add(("TEL: " + telefono + "\r\n").getBytes());
            String newFecha = formatoFecha(fecha, NovusConstante.SIMPLE_FORMAT);
            lista.add(("FECHA: " + newFecha + "\r\n").getBytes());
            addLine(lista);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("CLIENTE: " + cliente.get("nombreRegistro").getAsString() + "\r\n").getBytes("Cp858"));
            if (json.get("atributos").getAsJsonObject().get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong() != consumidorFinal) {
                System.out.println("--------------------------------------------");
                System.out.println(json.get("atributos").getAsJsonObject().get("cliente").getAsJsonObject());
                String correo = json.get("atributos").getAsJsonObject().get("cliente").getAsJsonObject().get("correoElectronico").getAsString();
                lista.add(("CORREO: " + correo + "\r\n").getBytes());
            } else if (json.get("atributos").getAsJsonObject()
                    .get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong() == consumidorFinal) {
                lista.add(("CORREO: " + "N/A" + "\r\n").getBytes());
            }
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            String colsCOM[] = {"PLU", "DESCRIPCION", "CANTIDAD", "PDV", "TOTAL"};
            int tamCOM[] = {12, 16, 11, 10, 15};
            lista.add(Utils.format_cols(colsCOM, tamCOM).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            JsonArray venta = json.get("detalles").getAsJsonArray();
            float totalVenta = 0;
            for (JsonElement elemt : venta) {
                JsonObject objVenta = elemt.getAsJsonObject();
                String plu = objVenta.has("plu") ? objVenta.get("plu").getAsInt() + "" : objVenta.get("productos_plu").getAsInt() + "";
                colsCOM[0] = plu;
                String producto = objVenta.get("nombre_producto").getAsString();
                if (producto.length() > 16) {
                    producto = producto.substring(0, 16);
                }
                colsCOM[1] = producto;
                colsCOM[2] = objVenta.get("cantidad").getAsInt() + "";
                long precio = objVenta.get("precio") != null ? objVenta.get("precio").getAsLong() : 0l;
                colsCOM[3] = precio + "";

                float total = 0;
                if (objVenta.get("sub_total").getAsFloat() > 0) {
                    total += objVenta.get("sub_total").getAsFloat();
                    for (JsonElement elemtp : objVenta.get("impuestos").getAsJsonArray()) {
                        JsonObject imp = elemtp.getAsJsonObject();
                        boolean cortesia = objVenta.has("cortesia") ? objVenta.get("cortesia").isJsonNull() ? false : objVenta.get("cortesia").getAsBoolean() : false;
                        if (cortesia) {
                            total = 0;
                        } else {
                            if (imp.get("porcentaje_valor").getAsString().equals("%")) {
                                total += imp.get("impuesto_valor").getAsFloat();
                            }

                        }
                    }
                }
                totalVenta += total;
                colsCOM[4] = "$ " + df.format(total);
                lista.add(Utils.format_cols(colsCOM, tamCOM).getBytes());
                lista.add(("\r\n").getBytes());
            }
            addLine(lista);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(Utils.text_between("", "Total").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(Utils.text_between("", "$ " + df.format(totalVenta)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((cfe.mensajeComprobantes()).getBytes());
            lista.add(("\r\n").getBytes());
            try {
                if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                        .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                    TCPPrinterService service = new TCPPrinterService();
                    service.printBytes(lista);
                } else {
                    QTEPrinterService service = new QTEPrinterService();
                    service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
                }
            } catch (PrintException pr) {

            } catch (IOException ex) {
                Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (DAOException ex) {
            Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ReportesFE.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

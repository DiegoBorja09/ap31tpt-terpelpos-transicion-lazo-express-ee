/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neo.print.services;

import com.application.useCases.consecutivos.GetObservacionesConsecutivoUseCase;
import com.application.useCases.productos.GetCategoriasMovimientoUseCase;
import com.bean.CategoriaBean;
import com.bean.ConsecutivoBean;
import com.bean.DescriptorBean;
import com.bean.EmpresaBean;
import com.bean.ImpuestosBean;
import com.bean.JornadaBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.bean.Recibo;
import com.bean.ReporteJornadaBean;
import com.bean.ResultBean;
import com.bean.Surtidor;
import com.bean.VentasBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.firefuel.Main;
import com.firefuel.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintException;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrinterFacade {

    public static boolean consumoPropio;
    SimpleDateFormat xsdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat xsdfFecha = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    SimpleDateFormat xsdfFechaFull = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    boolean cortecia = false;
    DecimalFormat dff = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    void buildTitle() {

    }

    public void printVentaCombustible(String texto) {
        try {
            ArrayList<byte[]> lista = new ArrayList<>();
            lista.add(texto.getBytes());
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (PrintException ex) {
            NovusUtils.printLn("Ocurrio un error al imprimir");
        } catch (IOException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printCierreDiario(Date fechaConsulta, JsonObject jsonCierre) {

        float cantidad_combustible = 0;
        int i = 0, cantidad = 0;

        TreeMap<Long, MediosPagosBean> mediosPagos = new TreeMap<>();
        int cantidadVentasCombustible = jsonCierre.get("cantidadVentasCombustible").getAsInt();
        int cantidadVentasCanastilla = jsonCierre.get("cantidadVentasCanastilla").getAsInt();
        float totalVentasCombustible = jsonCierre.get("totalVentasCombustible").getAsFloat();
        float totalVentasCanastilla = jsonCierre.get("totalVentasCanastilla").getAsFloat();
        JsonArray jsonArrayVentasCategorizadas = jsonCierre.getAsJsonArray("ventasPorCategoria");
        JsonArray jsonArrayVentasPorFamilia = jsonCierre.getAsJsonArray("ventasPorFamilia");
        JsonArray jsonArrayVentasPorPromotor = jsonCierre.getAsJsonArray("ventasPorPromotor");
        JsonArray jsonArrayMediosPagos = jsonCierre.getAsJsonArray("mediosPagos");
        try {

            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_FONT_D);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("COMPROBANTE INFORME DIARIO\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            EmpresaBean empresa;
            try {
                EquipoDao dao = new EquipoDao();
                empresa = dao.findEmpresa(Main.credencial);
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((empresa.getRazonSocial() + " \r\n").getBytes());
                lista.add((empresa.getCodigo() + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("DIR: " + empresa.getDireccionPrincipal() + "\r\n").getBytes());
                lista.add((empresa.getCiudadDescripcion() + "\r\n").getBytes());
                lista.add(("TEL: " + empresa.getTelefonoPrincipal() + "\r\n").getBytes());
                lista.add(("NIT: " + empresa.getNit() + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
            } catch (DAOException e) {
            }
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_FONT_A);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + getFecha(new Date()), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("FECHA DIA CONSULTA:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(fechaConsulta), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("REPORTE VENTAS DIARIO" + " \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("CANT. VENTAS COMBUSTIBLE:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + cantidadVentasCombustible, 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("CANT. VENTAS CANASTILLA:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + cantidadVentasCanastilla, 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS COMBUSTIBLE:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(
                    (NovusUtils.str_pad("$" + df.format(totalVentasCombustible), 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS CANASTILLA:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("$" + df.format(totalVentasCanastilla), 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());
            if (jsonArrayVentasCategorizadas.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN CATEGORIAS CANASTILLA" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("CATEGORIA", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANT", 7, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("TOTAL", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("IMPUESTO", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementVentasCategorias : jsonArrayVentasCategorizadas) {
                    JsonObject jsonVentaCategoria = elementVentasCategorias.getAsJsonObject();
                    String descripcion = jsonVentaCategoria.get("descripcion").getAsString();
                    int cantidadApariciones = jsonVentaCategoria.get("cantidad").getAsInt();
                    float total = jsonVentaCategoria.get("total").getAsFloat();
                    float totalImpuesto = jsonVentaCategoria.get("totalImpuesto").getAsFloat();
                    lista.add((NovusUtils.str_pad(descripcion, 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadApariciones + "", 7, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add(
                            (NovusUtils.str_pad("$" + df.format(total), 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$" + df.format(totalImpuesto), 13, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
                lista.add(("\n").getBytes());
            }
            if (jsonArrayVentasPorFamilia.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN FAMILIAS COMBUSTIBLE" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("FAMILIA", 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANT", 5, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("VOLUMEN", 9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("TOTAL", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("DESCUENTO", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementVentasFamilia : jsonArrayVentasPorFamilia) {
                    JsonObject jsonVentaFamilia = elementVentasFamilia.getAsJsonObject();
                    String descripcion = jsonVentaFamilia.get("descripcion").getAsString();
                    String volumen = jsonVentaFamilia.get("volumen").getAsString();
                    int cantidadApariciones = jsonVentaFamilia.get("cantidad").getAsInt();
                    float total = jsonVentaFamilia.get("total").getAsFloat();
                    float totalDescuento = jsonVentaFamilia.get("descuento").getAsFloat();
                    lista.add((NovusUtils.str_pad(descripcion, 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadApariciones + "", 5, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add((NovusUtils.str_pad(volumen, 9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add(
                            (NovusUtils.str_pad("$" + df.format(total), 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$" + df.format(totalDescuento), 11, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
                lista.add(("\n").getBytes());
            }
            if (jsonArrayVentasPorPromotor.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN POR PROMOTOR" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("NOMBRE", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANTIDAD VENTAS", 14, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("TOTAL VENTAS", 17, " ", "STR_PAD_BOTH")).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementVentasPromotor : jsonArrayVentasPorPromotor) {
                    JsonObject jsonVentaPromotor = elementVentasPromotor.getAsJsonObject();
                    String nombre = jsonVentaPromotor.get("nombre").getAsString();
                    int cantidadVentas = jsonVentaPromotor.get("cantidad").getAsInt();
                    float total = jsonVentaPromotor.get("total").getAsFloat();
                    lista.add((NovusUtils.str_pad(nombre, 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadVentas + "", 14, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add((NovusUtils.str_pad("$" + df.format(total), 17, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
                lista.add(("\n").getBytes());
            }
            if (jsonArrayMediosPagos.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN MEDIOS PAGOS" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("DESCRIPCION", 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANTIDAD", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("VALOR", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementMediosPagos : jsonArrayMediosPagos) {
                    JsonObject jsonMedioPago = elementMediosPagos.getAsJsonObject();
                    String nombre = jsonMedioPago.get("descripcion").getAsString();
                    int cantidadVentas = jsonMedioPago.get("cantidad").getAsInt();
                    float total = jsonMedioPago.get("total").getAsFloat();
                    lista.add((NovusUtils.str_pad(nombre, 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadVentas + " ", 13, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add(
                            (NovusUtils.str_pad("$" + df.format(total), 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
                lista.add(("\n").getBytes());
            }

            lista.add(("\r\n").getBytes());

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ResultBean printInicioTurnosResponse(MovimientosBean movimiento) {
        int FULL_PAGE = 48;
        ResultBean op = new ResultBean();
        try {

            ArrayList<byte[]> lista = new ArrayList<>();
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_FONT_D);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("INICIO DE JORNADA\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(("INICIO DE JORNADA EXITOSO!\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

            op.setSuccess(true);
            op.setMessage("TICKET IMPRESO CORRECTAMENTE");
        } catch (IOException ex) {
            op.setSuccess(false);
            op.setMessage("ERROR AL IMPRIMIR EL TICKET");
            op.setError(ex.getMessage());
        } catch (PrintException ex) {
            op.setSuccess(true);
            op.setMessage("ERROR AL IMPRIMIR EL TICKET");
            op.setError(ex.getMessage());
        }
        return op;
    }

    public void printReciboKIOSCO(String impresora, Recibo recibo) {
        int FULL_PAGE = 48;

        System.out.println("Printer Recibo: " + recibo);

        try {
            boolean notaCredito = false;
            for (Map.Entry<Long, MediosPagosBean> entry : recibo.getMovimiento().getMediosPagos().entrySet()) {
                MediosPagosBean value = entry.getValue();
                if (value.isCredito()) {
                    notaCredito = true;
                }
            }
            try {
                SetupDao sdao = new SetupDao();
                DescriptorBean descrp = sdao.getDescriptores(recibo.getMovimiento().getEmpresasId());
                recibo.setDescriptores(descrp);
            } catch (DAOException a) {

            }

            ArrayList<byte[]> lista = new ArrayList<>();

            printHr(lista, FULL_PAGE);

            if (recibo.getDescriptores() != null) {
                if (recibo.getDescriptores().getHeader() != null
                        && !recibo.getDescriptores().getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes("Cp858"));
                    lista.add(TCPPrinterService.TXT_FONT_A);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + recibo.getMovimiento().getEmpresa().getNit() + " - ").getBytes("Cp858"));
            lista.add(("TEL: " + recibo.getTelefono() + "\r\n").getBytes());
            lista.add(("DIR: " + recibo.getDireccion() + " - "
                    + recibo.getMovimiento().getEmpresa().getCiudadDescripcion()).getBytes());
            lista.add("\r\n".getBytes());

            lista.add(TCPPrinterService.TXT_FONT_C);

            if (recibo.getMovimiento().getMovmientoEstado().equals("X")) {
                printHr(lista, FULL_PAGE);
                lista.add(TCPPrinterService.TXT_2HEIGHT);
                lista.add(("DOCUMENTO ANULADO\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_NORMAL);
                printHr(lista, FULL_PAGE);
            } else {
                if (recibo.getMovimiento().getImpreso() != null && recibo.getMovimiento().getImpreso().equals("S")) {
                    printHr(lista, FULL_PAGE);
                    lista.add(("DUPLICADO\r\n").getBytes());
                    printHr(lista, FULL_PAGE);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_LT);

            String documento = "TIQUETE SISTEMA P.O.S No.";
            if (notaCredito) {
                documento = "NOTA DE ENTREGA";
            }
            String numero = recibo.getMovimiento().getConsecutivo().getPrefijo()
                    .concat("-" + recibo.getMovimiento().getConsecutivo().getConsecutivo_actual());

            lista.add("\r\n".getBytes());
            lista.add(NovusUtils.text_between(documento, numero).getBytes());
            lista.add("\r\n".getBytes());

            lista.add((NovusUtils.text_between("FECHA EXP:", getFecha(recibo.getMovimiento().getFecha()))).getBytes());
            lista.add("\r\n".getBytes());

            lista.add(NovusUtils.text_between("CLIENTE:", "CONSUMIDOR FINAL").getBytes());
            lista.add("\r\n".getBytes());

            lista.add(NovusUtils.text_between("NIT:", "222222222222").getBytes());
            lista.add("\r\n".getBytes());

            printHr(lista, FULL_PAGE);

            lista.add(TCPPrinterService.TXT_BOLD_ON);

            String cols[] = {"COD", "DESCRIPCION", "UND", "CNT", "V.UNI", "IVA", "BASE", "IMP", "TOTAL"};
            int cols_size[] = {4, 12, 4, 6, 8, 6, 7, 7, 10};

            lista.add(NovusUtils.format_cols(cols, cols_size).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            float impoConsumo = 0;
            MovimientosDao mdao = new MovimientosDao();

            float subtotal = 0;
            float impuestoCortesia = 0;
            float totalCortesia = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : recibo.getMovimiento().getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                JsonObject atributos = value.getAtributos();
                if (atributos != null && atributos.has("cortecia") && atributos.get("cortecia") != null) {
                    cortecia = atributos.get("cortecia").getAsBoolean();
                } else {
                    cortecia = false;
                }
                if (value.getImpuestos() != null) {
                    for (ImpuestosBean impuesto : value.getImpuestos()) {
                        if (impuesto.getPorcentaje_valor().equals(NovusConstante.SIMBOLS_PRICE)) {
                            impoConsumo += impuesto.getValor() * value.getCantidad();
                        } else {
                            if (!cortecia) {
                                subtotal += (value.getPrecio() - impuesto.getCalculado() /value.getCantidadUnidad()) * value.getCantidadUnidad();
                            } else {
                                impuestoCortesia += impuesto.getCalculado();
                            }
                        }
                    }
                }
                int size = 12;
                String descripcion = "";
                if (value.getDescripcion() != null && !value.getDescripcion().isEmpty()) {
                    if (value.getDescripcion().length() < size) {
                        size = value.getDescripcion().length();
                    }
                    descripcion = value.getDescripcion();
                }

                String unidades_medida = "";
                SetupDao sDao = new SetupDao();
                if (value.getProductoId() != 0
                        && sDao.getUnidades(value.getProductoId()).getUnidades_medida() != null) {
                    unidades_medida = sDao.getUnidades(value.getProductoId()).getUnidades_medida().substring(0, 3);
                    if (value.getUnidades_medida() != null && !value.getUnidades_medida().isEmpty()) {
                        if (value.getUnidades_medida().length() > 3) {
                            unidades_medida = value.getUnidades_medida().substring(0, 3);
                        } else {
                            unidades_medida = value.getUnidades_medida();
                        }
                    }
                } else if (value.getUnidades_medida() != null) {
                    unidades_medida = !value.getUnidades_medida().isEmpty() ? value.getUnidades_medida().substring(0, 3)
                            : "";
                }
                lista.add(printProductoKco(size, value.getPlu(), descripcion, unidades_medida,
                        value.getCantidadUnidad(), value.getPrecio(), value.getImpuestos(),
                        value.getPrecio() * value.getCantidadUnidad()).getBytes());
                if (cortecia) {
                    totalCortesia += value.getPrecio() * value.getCantidadUnidad();
                }
            }
            printHr(lista, FULL_PAGE);
            if (!consumoPropio) {
                String descrip[] = {"", "SUBTOTAL", "$" + dff.format(subtotal)};
                int anchos[] = {0, 46, 18};
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "IVA";
                double impuestos = recibo.getMovimiento().getTotalImpuestosSinImpocunsumos();
                descrip[2] = "$" + dff.format(impuestos - impuestoCortesia);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "TOTAL A PAGAR";

                float total = recibo.getMovimiento().getVentaTotal();
                descrip[2] = "$" + dff.format(total - totalCortesia);

                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);

                printHr(lista, FULL_PAGE);
            } else {
                String descrip[] = {"", "SUBTOTAL", "$" + dff.format(recibo.getMovimiento().getImpuestoTotal())};
                int anchos[] = {0, 46, 18};
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "IVA";
                float impuestos = 0;
                descrip[2] = "$" + dff.format(impuestos);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "TOTAL A PAGAR";

                float total = recibo.getMovimiento().getVentaTotal();
                descrip[2] = "$" + dff.format(recibo.getMovimiento().getImpuestoTotal());

                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);

                printHr(lista, FULL_PAGE);
            }

            // boolean activarImpuestoVacio = false;
            //
            // for (Map.Entry<Long, MovimientosDetallesBean> entry :
            // recibo.getMovimiento().getDetalles().entrySet()) {
            // MovimientosDetallesBean value = entry.getValue();
            // if (value.getImpuestos() == null && value.getImpuestos().isEmpty()) {
            // activarImpuestoVacio = true;
            // break;
            // }
            // }
            //
            // if (activarImpuestoVacio) {
            // lista.add(("E. IVA 0% 0 0\r\n").getBytes());
            // }
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            String col[] = {"MEDIOS DE PAGO", "RECIBIDO", "CAMBIO", "TOTAL"};
            int col_size[] = {16, 16, 16, 16};
            lista.add(NovusUtils.format_cols(col, col_size).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            for (Map.Entry<Long, MediosPagosBean> entry : recibo.getMovimiento().getMediosPagos().entrySet()) {
                MediosPagosBean value = entry.getValue();
                String valores[] = new String[4];
                valores[0] = value.getDescripcion();
                valores[1] = "$" + dff.format((value.getRecibido()));
                valores[2] = "$" + dff.format((value.getCambio()));
                if (!consumoPropio) {
                    valores[3] = "$" + dff.format(value.getValor());
                } else {
                    valores[3] = "$" + dff.format(recibo.getMovimiento().getImpuestoTotal());
                }
                lista.add(NovusUtils.format_cols(valores, col_size).getBytes());
                lista.add(("\r\n").getBytes());
            }

            lista.add(("\r\n").getBytes());
            lista.add(("ATENDIDO POR: ").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((recibo.getMovimiento().getPersonaNombre() + "\r\n").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            printHr(lista, FULL_PAGE);

//            if (notaCredito) {
//                lista.add(("RECIBIDO POR:\r\n").getBytes());
//                lista.add(("CC:\r\n\r\n\r\n\r\n").getBytes());
//                printHr(lista, FULL_PAGE);
//                lista.add(TCPPrinterService.TXT_ALIGN_CT);
//                lista.add(("FIRMA\r\n").getBytes());
//                lista.add(TCPPrinterService.TXT_ALIGN_LT);
//                printHr(lista, FULL_PAGE);
//            }
            if (recibo.getMovimiento().getConsecutivo() != null) {
                //lista.add(mdao.getObservaciones(recibo.getMovimiento().getConsecutivo().getId()).getBytes("Cp858"));
                lista.add(
                        new GetObservacionesConsecutivoUseCase(
                                recibo.getMovimiento().getConsecutivo().getId()
                        ).execute().getBytes("Cp858")
                );
                lista.add(("\r\n").getBytes());
                lista.add(("\r\n").getBytes());
                lista.add(createObservaciones().getBytes());
            }

            lista.add(("\r\n").getBytes());
            lista.add((getCopyright()).getBytes());

            for (byte[] bs : lista) {
                NovusUtils.printLn(new String(bs));
            }

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE).equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(null, lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printRecibo(Recibo recibo) {
        int FULL_PAGE = 48;

        try {
            boolean notaCredito = false;
            for (Map.Entry<Long, MediosPagosBean> entry : recibo.getMovimiento().getMediosPagos().entrySet()) {
                MediosPagosBean value = entry.getValue();
                if (value.isCredito()) {
                    notaCredito = true;
                }
            }
            try {
                SetupDao sdao = new SetupDao();
                DescriptorBean descrp = sdao.getDescriptores(recibo.getMovimiento().getEmpresasId());
                recibo.setDescriptores(descrp);

            } catch (DAOException a) {

            }

            ArrayList<byte[]> lista = new ArrayList<>();

            printHr(lista, FULL_PAGE);

            if (recibo.getDescriptores() != null) {
                if (recibo.getDescriptores().getHeader() != null
                        && !recibo.getDescriptores().getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes("Cp858"));
                    lista.add(TCPPrinterService.TXT_FONT_A);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + recibo.getMovimiento().getEmpresa().getNit() + " - ").getBytes("Cp858"));
            lista.add(("TEL: " + recibo.getTelefono() + "\r\n").getBytes());
            lista.add(("DIR: " + recibo.getDireccion() + " - " + recibo.getMovimiento().getEmpresa().getCiudadDescripcion()).getBytes());
            lista.add("\r\n".getBytes());

            lista.add(TCPPrinterService.TXT_FONT_C);

            if (recibo.getMovimiento().getMovmientoEstado().equals("X")) {
                printHr(lista, FULL_PAGE);
                lista.add(TCPPrinterService.TXT_2HEIGHT);
                lista.add(("DOCUMENTO ANULADO\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_NORMAL);
                printHr(lista, FULL_PAGE);
            } else {
                if (recibo.getMovimiento().getImpreso() != null && recibo.getMovimiento().getImpreso().equals("S")) {
                    printHr(lista, FULL_PAGE);
                    lista.add(("DUPLICADO\r\n").getBytes());
                    printHr(lista, FULL_PAGE);
                }
            }

            lista.add(TCPPrinterService.TXT_ALIGN_LT);

            String documento = "TIQUETE SISTEMA P.O.S No.";
            if (notaCredito) {
                documento = "NOTA DE ENTREGA";
            }
            String numero = recibo.getMovimiento().getConsecutivo().getPrefijo().concat("-" + recibo.getMovimiento().getConsecutivo().getConsecutivo_actual());

            lista.add("\r\n".getBytes());
            lista.add(NovusUtils.text_between(documento, numero).getBytes());
            lista.add("\r\n".getBytes());

            lista.add((NovusUtils.text_between("FECHA EXP:", getFecha(recibo.getMovimiento().getFecha()))).getBytes());
            lista.add("\r\n".getBytes());

            lista.add(NovusUtils.text_between("CLIENTE:", "CONSUMIDOR FINAL").getBytes());
            lista.add("\r\n".getBytes());

            lista.add(NovusUtils.text_between("NIT:", "222222222222").getBytes());
            lista.add("\r\n".getBytes());

            printHr(lista, FULL_PAGE);
            lista.add(TCPPrinterService.TXT_BOLD_ON);

            String cols[] = {"COD", "DESCRIPCION", "UND", "CNT", "V.UNI", "IVA", "BASE", "IMP", "TOTAL"};
            int cols_size[] = {4, 12, 4, 4, 8, 4, 10, 8, 10};

            lista.add(NovusUtils.format_cols(cols, cols_size).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            float impoConsumo = 0;
            MovimientosDao mdao = new MovimientosDao();

            float subtotal = 0;
            float totalCortesia = 0;
            float totalImpuestoCortesia = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : recibo.getMovimiento().getDetalles().entrySet()) {

                MovimientosDetallesBean value = entry.getValue();
                JsonObject atributos = value.getAtributos();
                if (atributos != null && atributos.has("cortecia") && atributos.get("cortecia") != null) {
                    cortecia = atributos.get("cortecia").getAsBoolean();
                    value.setPrecio(atributos.get("precio_unitario").getAsFloat());
                } else {
                    cortecia = false;
                }
                if (value.getImpuestos() != null) {
                    for (ImpuestosBean impuesto : value.getImpuestos()) {
                        if (impuesto.getPorcentaje_valor().equals(NovusConstante.SIMBOLS_PRICE)) {
                            impoConsumo += impuesto.getValor() * value.getCantidad();
                        } else if (atributos != null && atributos.has("cortecia") && atributos.get("cortecia").getAsBoolean()) {
                            subtotal += atributos.get("impuesto").getAsFloat();
                            totalImpuestoCortesia += impuesto.getCalculado();
                        } else {
                            subtotal += (value.getPrecio() - impuesto.getCalculado() / Math.round(value.getCantidadUnidad())) * Math.round(value.getCantidadUnidad());
                        }
                    }
                }
                int size = 12;
                String descripcion = "";
                if (value.getDescripcion() != null && !value.getDescripcion().isEmpty()) {
                    if (value.getDescripcion().length() < size) {
                        size = value.getDescripcion().length();
                    }
                    descripcion = value.getDescripcion();
                }

                String unidades_medida = "";
                SetupDao sDao = new SetupDao();

                if (value.getProductoId() != 0 && sDao.getUnidades(value.getProductoId()).getUnidades_medida() != null) {
                    //unidades_medida = sDao.getUnidades(value.getProductoId()).getUnidades_medida().substring(0, 3);
                    if (value.getUnidades_medida() != null && !value.getUnidades_medida().isEmpty()) {
                        if (value.getUnidades_medida().length() > 3) {
                            unidades_medida = value.getUnidades_medida().substring(0, 3);
                        } else {
                            unidades_medida = value.getUnidades_medida();
                        }
                    }
                } else if (value.getUnidades_medida() != null) {
                    //unidades_medida = value.getUnidades_medida();
                    unidades_medida = !value.getUnidades_medida().isEmpty() ? value.getUnidades_medida().substring(0, 3) : "";
                }
                lista.add(printProducto2(size, value.getPlu(), descripcion, unidades_medida, Math.round(value.getCantidadUnidad()), value.getPrecio(), value.getImpuestos(), value.getPrecio() * value.getCantidadUnidad()).getBytes("Cp858"));
                if (cortecia) {
                    totalCortesia += value.getPrecio() * value.getCantidadUnidad();
                }
            }
            printHr(lista, FULL_PAGE);

            if (!consumoPropio) {
                String descrip[] = {"", "SUBTOTAL", "$" + dff.format(subtotal)};
                int anchos[] = {0, 46, 18};
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "IVA";
                float impuestos = recibo.getMovimiento().getImpuestoTotal() - impoConsumo;
                descrip[2] = "$" + dff.format(impuestos - totalImpuestoCortesia);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "TOTAL A PAGAR";

                float total = recibo.getMovimiento().getVentaTotal();
                descrip[2] = "$" + dff.format(total - totalCortesia);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
            } else {
                String descrip[] = {"", "SUBTOTAL", "$" + dff.format(recibo.getMovimiento().getImpuestoTotal() - impoConsumo)};
                int anchos[] = {0, 46, 18};
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());
                descrip[1] = "IVA";
                float impuestos = 0;
                descrip[2] = "$" + dff.format(impuestos);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(("\r\n").getBytes());

                descrip[1] = "TOTAL A PAGAR";

                float total = recibo.getMovimiento().getImpuestoTotal() - impoConsumo;
                descrip[2] = "$" + dff.format(total);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
            }

            printHr(lista, FULL_PAGE);

            // boolean activarImpuestoVacio = false;
            // for (Map.Entry<Long, MovimientosDetallesBean> entry :
            // recibo.getMovimiento().getDetalles().entrySet()) {
            // MovimientosDetallesBean value = entry.getValue();
            // if (value.getImpuestos() == null && value.getImpuestos().isEmpty()) {
            // activarImpuestoVacio = true;
            // }
            // }
            //
            // if (!activarImpuestoVacio) {
            // lista.add(("E. IVA 0% 0 0\r\n").getBytes());
            // }
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            String col[] = {"MEDIOS DE PAGO", "RECIBIDO", "CAMBIO", "TOTAL"};
            int col_size[] = {16, 16, 16, 16};
            lista.add(NovusUtils.format_cols(col, col_size).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            for (Map.Entry<Long, MediosPagosBean> entry : recibo.getMovimiento().getMediosPagos().entrySet()) {
                MediosPagosBean value = entry.getValue();
                String valores[] = new String[4];
                valores[0] = value.getDescripcion();
                valores[1] = "$" + dff.format((value.getRecibido()));
                valores[2] = "$" + dff.format((value.getCambio()));
                 if (!consumoPropio) {
                    valores[3] = "$" + dff.format(value.getValor());
                } else {
                    valores[3] = "$" + dff.format(recibo.getMovimiento().getImpuestoTotal());
                }
                lista.add(NovusUtils.format_cols(valores, col_size).getBytes());
                lista.add(("\r\n").getBytes());
            }
            lista.add(("\r\n").getBytes());
            lista.add(("ATENDIDOS POR: ").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((recibo.getMovimiento().getPersonaNombre() + "\r\n").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            printHr(lista, FULL_PAGE);

//            if (notaCredito) {
//                lista.add(("RECIBIDO POR:\r\n").getBytes());
//                lista.add(("CC:\r\n\r\n\r\n\r\n").getBytes());
//                printHr(lista, FULL_PAGE);
//                lista.add(TCPPrinterService.TXT_ALIGN_CT);
//                lista.add(("FIRMA\r\n").getBytes());
//                lista.add(TCPPrinterService.TXT_ALIGN_LT);
//                printHr(lista, FULL_PAGE);
//            }
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);

            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            if (recibo.getMovimiento().getConsecutivo() != null) {
                //lista.add(mdao.getObservaciones(recibo.getMovimiento().getConsecutivo().getId()).getBytes("Cp858"));
                lista.add(
                        new GetObservacionesConsecutivoUseCase(
                                recibo.getMovimiento().getConsecutivo().getId()
                        ).execute().getBytes("Cp858")
                );
                lista.add(("\r\n").getBytes());
                lista.add(("\r\n").getBytes());
                lista.add(createObservaciones().getBytes());
            }

            lista.add(("\r\n").getBytes());
            lista.add((getCopyright()).getBytes());

            lista.forEach(bs -> {
                NovusUtils.printLn(new String(bs));
            });

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String createObservaciones() {
        MovimientosDao mdao = new MovimientosDao();
        StringBuilder mensaje = new StringBuilder();
        JsonObject observaciones = mdao.getObservacionesFactura();
        if (observaciones != null) {
            if (observaciones.has("autorretenedor") && observaciones.get("autorretenedor").getAsBoolean()) {
                String numeroAutorizacion = observaciones.get("autorretenedor_numero_autorizacion") != null ? observaciones.get("autorretenedor_numero_autorizacion").getAsString() : "";
                String fecha = observaciones.get("autorretenedor_fecha_inicio") != null ? observaciones.get("autorretenedor_fecha_inicio").getAsString() : "";
                mensaje.append("AUTORRETENEDOR  #").append(numeroAutorizacion).append(" ").append(fecha).append("\n");
            }
            if (observaciones.has("responsable_iva") && observaciones.get("responsable_iva").getAsBoolean()) {
                mensaje.append("RESPONSABLE DE IVA \n");
            }
            if (observaciones.has("gran_contribuyente") && observaciones.get("gran_contribuyente").getAsBoolean()) {
                String numeroAutorizacion = observaciones.get("gran_contribuyente_numero_autorizacion") != null ? observaciones.get("gran_contribuyente_numero_autorizacion").getAsString() : "";
                String fecha = observaciones.get("gran_contribuyente_fecha_inicio") != null ? observaciones.get("gran_contribuyente_fecha_inicio").getAsString() : "";
                mensaje.append("GRAN CONTRIBUYENTE #").append(numeroAutorizacion).append(" ").append(fecha).append("\n");
            }
            if (observaciones.has("retenedor_iva") && observaciones.get("retenedor_iva").getAsBoolean()) {
                mensaje.append("AGENTE RETENEDOR DE IVA \n");
            }
            String piePagina = observaciones.has("pie_pagina_factura_pos") ? observaciones.get("pie_pagina_factura_pos").getAsString() : "";
            mensaje.append(piePagina);
        }
        return mensaje.toString();
    }

    public JsonObject getProducto(JsonObject data, long identificadorProducto) {
        JsonObject producto = null;
        JsonArray json = data.get("detallesVenta").getAsJsonArray();
        for (JsonElement jsonElement : json) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (obj.get("identificadorProducto").getAsLong() == identificadorProducto) {
                producto = obj;
                break;
            }
        }
        return producto;
    }

    public float getImpTotalProducto(JsonObject data, long identificadorProducto) {
        float impuestoTotalAplicado = 0;
        JsonObject producto = getProducto(data, identificadorProducto);
        JsonArray impuestosProducto = producto.getAsJsonArray("impuestosAplicados");
        for (JsonElement jsonElement : impuestosProducto) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            impuestoTotalAplicado += jsonObject.get("valorImpuestoAplicado").getAsFloat();
        }
        return impuestoTotalAplicado / getProducto(data, identificadorProducto).get("cantidadVenta").getAsFloat();
    }

    public JsonObject reporteCortesia(JsonObject data, Recibo recibo) {
        int FULL_PAGE = 48;
        ArrayList<byte[]> lista = new ArrayList<>();
        lista.add("\r\n".getBytes());
        try {
            if (recibo.getDescriptores() != null) {
                if (recibo.getDescriptores().getHeader() != null
                        && !recibo.getDescriptores().getHeader().trim().equals("")) {

                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes("Cp858"));
                    lista.add(TCPPrinterService.TXT_FONT_A);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + recibo.getMovimiento().getEmpresa().getNit() + " - ").getBytes("Cp858"));
            lista.add(("TEL: " + recibo.getTelefono() + "\r\n").getBytes());
            lista.add(("DIR: " + recibo.getDireccion() + " - "
                    + recibo.getMovimiento().getEmpresa().getCiudadDescripcion()).getBytes());
            lista.add("\r\n".getBytes());
            String documento = "TIQUETE SISTEMA P.O.S No.";
            String numero = recibo.getMovimiento().getConsecutivo().getPrefijo()
                    .concat("-" + recibo.getMovimiento().getConsecutivo().getConsecutivo_actual());

            lista.add("\r\n".getBytes());
            lista.add(NovusUtils.text_between(documento, numero).getBytes());
            lista.add("\r\n".getBytes());

            lista.add((NovusUtils.text_between("FECHA EXP:", getFecha(recibo.getMovimiento().getFecha()))).getBytes());
            lista.add("\r\n".getBytes());

            lista.add(NovusUtils.text_between("CLIENTE:", "CONSUMIDOR FINAL").getBytes());
            lista.add("\r\n".getBytes());

            lista.add(NovusUtils.text_between("NIT:", "222222222222").getBytes());
            lista.add("\r\n".getBytes());

            printHr(lista, FULL_PAGE);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        String cols[] = {"COD", "DESCRIPCION", "UND", "CNT", "V.UNI", "IVA", "BASE", "IMP", "TOTAL"};
        int cols_size[] = {4, 12, 4, 4, 8, 4, 10, 8, 10};
        lista.add(Utils.format_cols(cols, cols_size).getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);
        JsonArray json = data.get("detallesVenta").getAsJsonArray();
        String tipoImp = "";
        String iva = "";
        String impuestoP = "";
        float totalImpo = 0;
        float totalImpoCortesia = 0;
        float baseProductoItem = 0;
        float subtotal = 0;
        float total = 0;
        float totalVenta;
        float descuento = 0;
        float subTotalItem = 0f;
        for (JsonElement elemnt : json) {
            JsonObject objDetalle = elemnt.getAsJsonObject();
            cols[0] = objDetalle.get("identificacionProducto").getAsString();
            cols[1] = objDetalle.get("nombreProducto").getAsString().substring(0, 11);
            cols[2] = "u";//falta buscar la unidad medida
            cols[3] = objDetalle.get("cantidadVenta").getAsInt() + "";
            float impuesto = 0;
            float precioProducto = objDetalle.get("precioProducto").getAsFloat();
            float precioiTem = precioProducto;
            for (JsonElement imp : objDetalle.get("impuestosAplicados").getAsJsonArray()) {
                JsonObject objImp = imp.getAsJsonObject();
                if (objDetalle.get("cortesia").getAsBoolean()) {
                    if (objImp.get("tipoImpuesto").getAsString().equals("%")) {
                        impuestoP = df.format(objImp.get("valorImpuestoAplicado").getAsFloat());
                        precioiTem += (objImp.get("valorImpuestoAplicado").getAsFloat() / objDetalle.get("cantidadVenta").getAsInt());
                        totalImpoCortesia += objImp.get("valorImpuestoAplicado").getAsFloat();
                        baseProductoItem = precioProducto;
                    }
                } else {
                    if (objImp.get("tipoImpuesto").getAsString().equals("%")) {
                        tipoImp = objImp.get("tipoImpuesto").getAsString();
                        iva = String.valueOf(objImp.get("valorImpAplicado").getAsFloat());
                        impuestoP = df.format(objImp.get("valorImpuestoAplicado").getAsFloat());
                        impuesto = objImp.get("valorImpuestoAplicado").getAsFloat();
                        precioiTem += (objImp.get("valorImpuestoAplicado").getAsFloat() / objDetalle.get("cantidadVenta").getAsInt());
                        totalImpo += objImp.get("valorImpuestoAplicado").getAsFloat();
                        baseProductoItem = precioProducto;
                    }
                    subTotalItem += (precioProducto * objDetalle.get("cantidadVenta").getAsFloat());
                }
                if (iva.length() > 2) {
                    iva = iva.substring(0, 2).replace(".", "") + "" + tipoImp;
                }
            }
            cols[4] = df.format(precioiTem);
            cols[5] = iva;
            cols[6] = df.format(baseProductoItem);
            cols[7] = impuestoP;
            if (objDetalle.get("cortesia").getAsBoolean()) {
                cols[8] = df.format(0);
            } else {
                float totalItem = (baseProductoItem * objDetalle.get("cantidadVenta").getAsFloat()) + impuesto;
                total += totalItem;
                cols[8] = df.format(totalItem);
            }
            lista.add(Utils.format_cols(cols, cols_size).getBytes());
        }
        printHr(lista, FULL_PAGE);
        String descrip[] = {"", "SUBTOTAL", "$" + dff.format(subTotalItem)};
        int anchos[] = {0, 46, 18};
        lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
        lista.add(("\r\n").getBytes());
        descrip[1] = "IVA";
        descrip[2] = "$" + dff.format(totalImpo);
        lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
        lista.add(("\r\n").getBytes());
        descrip[1] = "TOTAL A PAGAR";
        totalVenta = total;
        totalVenta = totalVenta - descuento;
        descrip[2] = "$" + dff.format(totalVenta);
        lista.add(NovusUtils.format_cols(descrip, anchos).getBytes());
        lista.add(("\r\n").getBytes());
        System.out.println(Main.ANSI_PURPLE + NovusUtils.format_cols(descrip, anchos) + Main.ANSI_RESET);
        JsonArray jsonP = data.get("pagos").getAsJsonArray();
        lista.add(("\r\n").getBytes());
        printHr(lista, FULL_PAGE);
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        String ColsP[] = {"MEDIOS DE PAGO", "AUTORIZACION", "CAMBIO", "TOTAL"};
        int ColsPTam[] = {19, 15, 15, 15};
        lista.add(NovusUtils.format_cols(ColsP, ColsPTam).getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);
        for (JsonElement elemP : jsonP) {
            JsonObject objPagos = elemP.getAsJsonObject();
            String medioPago = objPagos.get("descripcionMedio").getAsString();
            ColsP[0] = objPagos.get("descripcionMedio").getAsString();
            ColsP[1] = "";
            ColsP[2] = "$ " + df.format(objPagos.get("vueltoMedioPago").getAsFloat());
            if (medioPago.equals("CORTESIA")) {
                ColsP[3] = "$ " + df.format(totalImpoCortesia);
            } else {
                ColsP[3] = "$ " + df.format(objPagos.get("recibidoMedioPago").getAsFloat());
            }
            lista.add(NovusUtils.format_cols(ColsP, ColsPTam).getBytes());
        }
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
        lista.add(("\r\n").getBytes());
        lista.add(("\r\n").getBytes());
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
        lista.add(("ATENDIDO POR: " + data.get("venta").getAsJsonObject().get("nombresPromotor").getAsString()).getBytes());
        lista.add(("\r\n").getBytes());
        printHr(lista, FULL_PAGE);
        lista.add(TCPPrinterService.TXT_ALIGN_LT);
        MovimientosDao mdao = new MovimientosDao();
        if (recibo.getMovimiento().getConsecutivo() != null) {
            lista.add(
                    new GetObservacionesConsecutivoUseCase(
                            recibo.getMovimiento().getConsecutivo().getId()
                    ).execute().getBytes()
            );
        }
        lista.add(("\r\n").getBytes());
        lista.add((getCopyright()).getBytes());
        try {
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);

            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        data.get("venta").getAsJsonObject().remove("prefijo");
        data.get("venta").getAsJsonObject().addProperty("prefijo", recibo.getMovimiento().getConsecutivo().getPrefijo());
        data.get("venta").getAsJsonObject().remove("consecutivo_id");
        data.get("venta").getAsJsonObject().addProperty("consecutivo_id", recibo.getMovimiento().getConsecutivo().getId());
        data.get("venta").getAsJsonObject().remove("consecutivoActual");
        data.get("venta").getAsJsonObject().addProperty("consecutivoActual", recibo.getMovimiento().getConsecutivo().getConsecutivo_actual());
        data.get("venta").getAsJsonObject().addProperty("idTransaccionVenta", recibo.getMovimiento().getId());

        data.get("venta").getAsJsonObject().remove("descuentoTotal");
        data.get("venta").getAsJsonObject().addProperty("descuentoTotal", descuento);
        data.get("venta").getAsJsonObject().remove("impuestoTotal");
        data.get("venta").getAsJsonObject().addProperty("impuestoTotal", totalImpo);
        data.get("venta").getAsJsonObject().remove("ventaTotal");
        data.get("venta").getAsJsonObject().addProperty("ventaTotal", totalVenta);

        return data;
    }

    private String getCopyright() {
        String copyright = "";
        copyright += new String(TCPPrinterService.TXT_BOLD_ON);
        copyright += new String(TCPPrinterService.TXT_ALIGN_LT);
        copyright += "\r\nFABRICANTE DE SOFTWARE DEVICES AND TECHNOLOGY SAS.\r\n";
        copyright += new String(TCPPrinterService.TXT_BOLD_OFF);
        copyright += "NIT: 900130563-7\n";
        return copyright;
    }

    private String printProductoKco(int size, String plu, String string, String unidad, float cant, float precio,
            ArrayList<ImpuestosBean> impuestos, float total) {
        String line = "";
        String producto = string.length() > size ? string.substring(0, size - 1) : string;
        int anchos[] = {4, 12, 4, 6, 8, 6, 8, 7, 9};

        String info[] = new String[9];
        ImpuestosBean imp = null;
        for (ImpuestosBean impuestosBean : impuestos) {
            if (impuestosBean.getPorcentaje_valor().equals("%")) {
                imp = impuestosBean;
                break;
            }
        }
        info[0] = plu.trim();
        info[1] = producto.trim();
        info[2] = unidad.trim();
        info[3] = cant+"";
        if (!cortecia) {
            info[4] = dff.format(precio);
        }
        if (imp != null) {
            if (!consumoPropio) {
                if (cortecia) {
                    info[4] = dff.format(precio);
                    info[5] = dff.format(imp.getValor()) + "%";
                    info[6] = dff.format(precio - (imp.getCalculado() / cant));
                    info[7] = dff.format(imp.getCalculado());
                    total += 0;
                } else {
                    info[4] = dff.format(precio);
                    info[5] = dff.format(imp.getValor()) + "%";
                    info[6] = dff.format(imp.getBase());
                    info[7] = dff.format(imp.getCalculado());
                }
            } else {
                info[5] = dff.format(0) + "%";
                info[6] = dff.format(imp.getCalculado());
                info[7] = dff.format(0);
            }
        } else {
            info[5] = "0%";
            info[6] = "0";
            info[7] = "0";
        }
        if (!consumoPropio) {
            if (cortecia) {
                info[8] = dff.format(0);
            } else {
                info[8] = dff.format(total);
            }

        } else {
            info[8] = dff.format(imp == null ? 0 : imp.getCalculado());
        }
        line += NovusUtils.format_cols(info, anchos) + "\r\n";
        return line;
    }

    private String printProducto2(int size, String plu, String string, String unidad, float cant, float precio,
            ArrayList<ImpuestosBean> impuestos, float total) {
        String line = "";
        String producto = string.length() > size ? string.substring(0, size - 1) : string;
        int anchos[] = {4, 12, 4, 4, 8, 4, 10, 8, 10};

        String info[] = new String[9];

        ImpuestosBean imp = null;
        for (ImpuestosBean impuestosBean : impuestos) {
            if (impuestosBean.getPorcentaje_valor().equals("%")) {
                NovusUtils.printLn(Main.ANSI_GREEN + impuestosBean.getValor() + Main.ANSI_RESET);
                imp = impuestosBean;
                break;
            }
        }
        info[0] = plu.trim();
        info[1] = producto.trim();
        info[2] = unidad.trim();
        info[3] = dff.format(cant);
        if (!cortecia) {
            info[4] = dff.format(precio);
        }
        if (imp != null) {
            if (!consumoPropio) {
                if (cortecia) {
                    info[4] = dff.format(precio);
                    info[5] = dff.format(imp.getValor()) + "%";
                    info[6] = dff.format(precio - (imp.getCalculado() / cant));
                    info[7] = dff.format(imp.getCalculado());
                    total += 0;
                } else {
                    info[4] = dff.format(precio);
                    info[5] = dff.format(imp.getValor()) + "%";
                    info[6] = dff.format(precio - (imp.getCalculado() / cant));
                    info[7] = dff.format(imp.getCalculado());
                }
            } else {
                info[5] = dff.format(0) + "%";
                info[6] = dff.format(imp.getCalculado());
                info[7] = dff.format(0);
            }
        } else {
            info[5] = "0%";
            info[6] = "0";
            info[7] = "0";
        }
        if (!consumoPropio) {
            if (cortecia) {
                info[8] = dff.format(0);
            } else {
                info[8] = dff.format(total);
            }

        } else {
            info[8] = dff.format(imp == null ? 0 : imp.getCalculado());
        }
        line += NovusUtils.format_cols(info, anchos) + "\r\n";
        return line;
    }

    void printHr(ArrayList<byte[]> lista, int size) {
        lista.add(TCPPrinterService.TXT_FONT_A);
        lista.add(TCPPrinterService.TXT_NORMAL);
        lista.add(NovusUtils.fill("-", size).getBytes());
        lista.add("\r\n".getBytes());
        lista.add(TCPPrinterService.TXT_FONT_C);
    }

    private String calcularPorcentajeIva(float vunt, float valorImp) {
        float porc = (valorImp / vunt) * 100;
        return dff.format(porc) + "%";
    }

    private String printProducto2(int size, String plu, String string, float cant, float precio, float total,
            String imp) {
        String line = "";
        String producto = string.length() > size ? string.substring(0, size) : string;
        line += NovusUtils.str_pad(plu, 3, "0", "STR_PAD_LEFT") + " ";
        line += new String(TCPPrinterService.TXT_BOLD_ON);
        line += NovusUtils.str_pad(producto, size, " ", "STR_PAD_RIGHT") + " ";
        line += new String(TCPPrinterService.TXT_BOLD_OFF);
        line += NovusUtils.str_pad(NovusUtils.fmt(cant), 8, " ", "STR_PAD_RIGHT");
        line += NovusUtils.str_pad("$" + dff.format(precio), 9, " ", "STR_PAD_RIGHT");
        line += new String(TCPPrinterService.TXT_BOLD_ON);
        line += NovusUtils.str_pad("$" + dff.format(total), 14, " ", "STR_PAD_RIGHT");
        line += new String(TCPPrinterService.TXT_BOLD_OFF);
        line += imp + "\r\n";
        return line;
    }

    private String printChar(int i, String string) {
        String line = "";
        for (int j = 1; j <= i; j++) {
            line += string;
        }
        return line + "\r\n";
    }

    public void printInventariosPlus(PersonaBean persona, LinkedHashSet<ProductoBean> productos) {
        try {
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_FONT_D);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("SALDO INVENTARIOS\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(("FECHA:\t" + getFecha(new Date()) + " \r\n").getBytes());
            lista.add(("PROMOTOR:\t" + persona.getNombre() + " " + persona.getApellidos() + " \r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(("\r\n").getBytes());

            for (ProductoBean producto : productos) {
                String descripcion = producto.getDescripcion();
                if (descripcion.length() >= 30) {
                    descripcion = producto.getDescripcion().substring(0, 30);
                }
                descripcion = limpiar(descripcion);
                lista.add((NovusUtils.str_pad(producto.getPlu(), 5, " ", "STR_PAD_LEFT")).getBytes());
                lista.add((" " + NovusUtils.str_pad(descripcion, 33, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("" + producto.getSaldo(), 9, " ", "STR_PAD_LEFT")).getBytes());
                lista.add(("\r\n").getBytes());
            }
            lista.add(("\r\n").getBytes());

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printInventarios(int numeroPOS, ArrayList<MovimientosDetallesBean> productos, String tipo) {
        DescriptorBean descrp = null;
        try {
            SetupDao sdao = new SetupDao();
            descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
        } catch (DAOException a) {
            NovusUtils.printLn(a.getMessage());
        }

        ArrayList<byte[]> lista = new ArrayList<>();
        int FULL_PAGE = 48;

        lista.add(printChar(FULL_PAGE, "-").getBytes());
        if (descrp != null) {
            if (descrp.getHeader() != null
                    && !descrp.getHeader().trim().equals("")) {
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_2HEIGHT);
                lista.add(TCPPrinterService.TXT_FONT_D);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_FONT_A);
                lista.add(TCPPrinterService.TXT_NORMAL);
            }
        }
        lista.add(TCPPrinterService.TXT_ALIGN_CT);
        lista.add(TCPPrinterService.TXT_FONT_A);
        lista.add(TCPPrinterService.TXT_NORMAL);
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);
        lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
        lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
        lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());
        lista.add(printChar(FULL_PAGE, "-").getBytes());
        lista.add(("SALDO PRODUCTOS " + tipo + "\r\n").getBytes());
        lista.add(printChar(FULL_PAGE, "-").getBytes());
        lista.add(("FECHA:\t" + getFecha(new Date()) + " \r\n").getBytes());
        lista.add(("POS:\t" + numeroPOS + " \r\n").getBytes());
        lista.add(printChar(FULL_PAGE, "-").getBytes());
        lista.add(("\r\n").getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_ON);
        lista.add(((NovusUtils.str_pad("PLU", 6, " ", NovusUtils.STR_PAD_RIGHT)) + (NovusUtils.str_pad("DESCRIPCION", 20, " ", NovusUtils.STR_PAD_RIGHT)) + (NovusUtils.str_pad("SALDO", 7, " ", NovusUtils.STR_PAD_RIGHT))).getBytes());
        lista.add(TCPPrinterService.TXT_BOLD_OFF);
        lista.add(("\r\n").getBytes());

        Collections.sort(productos);

        for (MovimientosDetallesBean producto : productos) {
            lista.add((NovusUtils.str_pad(producto.getPlu(), 6, " ", NovusUtils.STR_PAD_RIGHT)
                    + NovusUtils.str_pad(producto.getDescripcion(), 19, " ", NovusUtils.STR_PAD_RIGHT)
                    + NovusUtils.str_pad(" " + producto.getSaldo() + "", 7, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());

        }
        lista.add(printChar(FULL_PAGE, "-").getBytes());
        lista.add(printChar(FULL_PAGE, "-").getBytes());
        try {
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (byte[] bs : lista) {
            NovusUtils.printLn(Main.ANSI_RED + bs + Main.ANSI_RESET);
        }
    }

    public void printArqueoEstadoVentas(String title, PersonaBean persona, ReporteJornadaBean reporte, boolean corte) {
        try {
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_FONT_D);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((title + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);

            if (reporte.getInicio() == null) {
                lista.add(("FECHA:\t" + getFecha(new Date()) + " \r\n").getBytes());
            } else {
                lista.add(("FECHA IMPRESION:\t" + getFecha(new Date()) + " \r\n").getBytes());
                lista.add(("FECHA INICIO   :\t" + getFecha(reporte.getInicio()) + " \r\n").getBytes());
                lista.add(("FECHA FIN      :\t" + getFecha(reporte.getFin()) + " \r\n").getBytes());
            }

            lista.add(("PROMOTOR       :\t" + persona.getNombre() + " " + persona.getApellidos() + " \r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("" + NovusUtils.str_pad("NUMERO DE VENTAS", 38, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + reporte.getNumeroVentas(), 8, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("" + NovusUtils.str_pad("RECIBOS IMPRESOS", 38, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + reporte.getImpresos(), 8, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("" + NovusUtils.str_pad("RECIBOS RE-IMPRESOS", 38, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + reporte.getReimpresos(), 8, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(printChar(FULL_PAGE, "-").getBytes());

            float granTotal = 0;
            if (reporte != null && reporte.getVentas() != null) {
                for (ProductoBean venta : reporte.getVentas()) {
                    lista.add(("" + NovusUtils.str_pad(venta.getDescripcion(), 38, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(("\r\n").getBytes());
                    lista.add(
                            ("" + NovusUtils.str_pad(venta.getCantidad() + " UND ", 38, " ", NovusUtils.STR_PAD_RIGHT))
                                    .getBytes());
                    lista.add((NovusUtils.str_pad("$" + dff.format(venta.getSaldo()), 8, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(("\r\n").getBytes());
                    granTotal += venta.getSaldo();
                }
            }

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_ALIGN_RT);
            lista.add((" TOTAL $" + dff.format(granTotal)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public void printTest() {

        try {

            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_FONT_D);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("CONFIGURACIÃ“N CORRECTA!\r\n").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);

            lista.add(("\r\n").getBytes());

            lista.add(
                    ("Para probar esta impresora, podemos utilizar un PANGRAMA (Una sola oraciÃ³n que tiene todas las letras) por ejemplo podemos usar...\r\n\r\n")
                            .getBytes("Cp858"));

            lista.add((("El niÃ±o exclama de alegrÃ­a viendo al fabuloso periquito comer jugosos kiwis y zanahoria"))
                    .getBytes("Cp858"));
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());
            lista.add((("o en mayusculas...")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(
                    ("El pingÃ¼ino Wenceslao hizo kilÃ³metros bajo exhaustiva lluvia y frÃ­o, aÃ±oraba a su querido cachorro."
                            .toUpperCase()).getBytes("Cp858"));
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("Es extraÃ±o mojar queso en la cerveza \r\ny probar whisky de garrafa.").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((("o en algunos nÃºmeros")).getBytes("Cp858"));
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("89123749123401263418234091273".toUpperCase()).getBytes("Cp858"));
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_ALIGN_RT);
            lista.add(("1234567890 1234567890 1234567890").getBytes("Cp858"));
            lista.add(("*=/+@([{}])").getBytes("Cp858"));
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("\r\n").getBytes());

            lista.add(printChar(FULL_PAGE, "-").getBytes());

            NovusUtils
                    .printLn("PRINTER SERVICE = " + Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE));
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String limpiar(String descripcion) {
        descripcion = descripcion.replaceAll("Ã¡", "\u00e1");
        descripcion = descripcion.replaceAll("Ã©", "\u00e9");
        descripcion = descripcion.replaceAll("Ã­", "\u00ed");
        descripcion = descripcion.replaceAll("Ã³", "\u00f3");
        descripcion = descripcion.replaceAll("Ãº", "\u00fa");

        descripcion = descripcion.replaceAll("Ã�", "\u00c1");
        descripcion = descripcion.replaceAll("Ã‰", "\u00c9");
        descripcion = descripcion.replaceAll("Ã�", "\u00cd");
        descripcion = descripcion.replaceAll("Ã“", "\u00d3");
        descripcion = descripcion.replaceAll("Ãš", "\u00da");
        descripcion = descripcion.replaceAll("Ã‘", "\u00d1");
        descripcion = descripcion.replaceAll("Ã±", "\u00d1");
        return descripcion;
    }

    public void imprimirJornadaInicioPersona(JornadaBean jornada, PersonaBean persona) {
        try {
            DescriptorBean descrp = null;
            try {
                SetupDao sdao = new SetupDao();
                descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            } catch (DAOException a) {
            }
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("INICIO DE JORNADA\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);

            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add(NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());

            lista.add(NovusUtils.str_pad("PROMOTOR:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(persona.getNombre(), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("TURNO:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(
                    (NovusUtils.str_pad(jornada.getGrupoJornada() + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("SALDO BASE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getSaldo()) + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());

            int surtidor = 0;
            int cara = 0;
            for (Surtidor lectura : jornada.getLecturasIniciales()) {
                if (surtidor != lectura.getSurtidor()) {
                    surtidor = lectura.getSurtidor();
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(("LECTURAS INICIALES SURTIDOR " + surtidor + "\r\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }
                if (cara != lectura.getCara()) {
                    cara = lectura.getCara();
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(("\r\nCARA " + cara + "\r\n").getBytes());
                }
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("MANGUERA " + lectura.getManguera() + ": ").getBytes());
                lista.add((lectura.getFamiliaDescripcion().toUpperCase() + "\r\n").getBytes());
                lista.add((lectura.getProductoDescripcion() + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("LI. VENTA:   \t\t" + lectura.getTotalizadorVenta() + "\r\n").getBytes());
                lista.add(("LI. VOLUMEN: \t\t"
                        + Utils.calculeCantidad(lectura.getTotalizadorVolumen(), NovusConstante.FACTOR_INVENTARIO)
                        + "\r\n").getBytes());

            }
            if (!jornada.isPrimerRegistro()) {
                lista.add(("\r\n").getBytes());
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("LA JORNADA FUE ABIERTA POR OTRO PROMOTOR \r\n").getBytes());
            }
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimirJornada(JornadaBean jornada, ArrayList<MovimientosBean> movimientos) {

        ArrayList<byte[]> lista = new ArrayList<>();
        int FULL_PAGE = 48;
        lista.add(("\r\n").getBytes());
        EmpresaBean empresa = null;
        try {
            try {
                EquipoDao dao = new EquipoDao();
                empresa = dao.findEmpresa(Main.credencial);

                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((empresa.getAlias() + " \r\n").getBytes());
                lista.add((empresa.getRazonSocial() + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
            } catch (DAOException e) {
            }

            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add((empresa.getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add(("DIR: " + empresa.getDireccionPrincipal() + "-").getBytes());
            lista.add(("TEL: " + empresa.getTelefonoPrincipal() + "\r\n").getBytes());
            lista.add(("NIT: " + empresa.getNit() + "\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_FONT_A);

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printComprobante(MovimientosBean movimiento) {
        try {

            DescriptorBean descrp = null;
            try {
                SetupDao sdao = new SetupDao();
                descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            } catch (DAOException a) {
            }

            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }

            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("COMPROBANTE INFORME DIARIO\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add((NovusUtils.str_pad("COMPUTADOR   :", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad(InetAddress.getLocalHost().getHostName().toUpperCase(), 33, " ",
                    "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("FECHA. EXP   :", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 33, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("FECHA REP INI:", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(
                    (NovusUtils.str_pad(getFecha(movimiento.getFecha()) + " 00:00:00", 33, " ", "STR_PAD_LEFT"))
                            .getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("FECHA REP FIN:", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(
                    (NovusUtils.str_pad(getFecha(movimiento.getFecha()) + " 23:59:59", 33, " ", "STR_PAD_LEFT"))
                            .getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("TRANSACCIONES:", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + movimiento.getConsecutivo().getConsecutivo_actual(), 33, " ",
                    "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("TOTAL VENTAS :", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("$ " + dff.format(movimiento.getVentaTotal()), 33, " ", "STR_PAD_LEFT"))
                    .getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("VENTAS DE CONTADO\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add((NovusUtils.str_pad("DOC. INICIAL: ", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad(movimiento.getConsecutivo().getPrefijo() + "-"
                    + movimiento.getConsecutivo().getConsecutivo_inicial(), 33, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("DOC. FINAL : ", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad(
                    movimiento.getConsecutivo().getPrefijo() + "-" + movimiento.getConsecutivo().getConsecutivo_final(),
                    33, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            int i = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                Object key = entry.getKey();
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() == 5) {

                    if (i == 0) {
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(printChar(FULL_PAGE, "-").getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add(("VENTAS DE COMBUSTIBLES\r\n").getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(printChar(FULL_PAGE, "-").getBytes());
                    }

                    String imp = value.getDescripcion() == null ? "E. IVA." : value.getDescripcion();
                    lista.add((NovusUtils.str_pad(imp, 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$ " + dff.format(value.getSubtotal()) + "", 33, " ", "STR_PAD_LEFT"))
                            .getBytes());
                    lista.add(("\r\n").getBytes());

                    if (i == 0) {
                        lista.add((NovusUtils.str_pad("DCTOS", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("$ " + dff.format(0) + "", 33, " ", "STR_PAD_LEFT")).getBytes());
                        lista.add(("\r\n").getBytes());
                    }
                    i++;

                }

            }

            float impuestosTotales = 0;
            float subtotal = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                Object key = entry.getKey();
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() == 4) {
                    subtotal += value.getSubtotal();
                    impuestosTotales += value.getCantidad();
                }
            }

            i = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                Object key = entry.getKey();
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() == 4) {

                    if (i == 0) {
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(printChar(FULL_PAGE, "-").getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add(("VENTAS DE CANASTILLA\r\n").getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(printChar(FULL_PAGE, "-").getBytes());

                        String imp = value.getDescripcion() == null ? "E. IVA." : value.getDescripcion();
                        lista.add((NovusUtils.str_pad("E. IVA.", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("$ " + dff.format(subtotal - impuestosTotales) + "", 33, " ",
                                "STR_PAD_LEFT")).getBytes());
                        lista.add(("\r\n").getBytes());

                    }

                    String imp = value.getDescripcion() == null ? "E. IVA." : value.getDescripcion();
                    lista.add((NovusUtils.str_pad(imp, 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$ " + dff.format(value.getCantidad()) + "", 33, " ", "STR_PAD_LEFT"))
                            .getBytes());
                    lista.add(("\r\n").getBytes());

                }
                if (i > 0 && i == (movimiento.getDetalles().size() - 1)) {
                    lista.add((NovusUtils.str_pad("DCTOS", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$ " + dff.format(0) + "", 33, " ", "STR_PAD_LEFT")).getBytes());
                    lista.add(("\r\n").getBytes());
                }
                i++;
            }

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("DISCRIMINACION DE FORMAS DE PAGO\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                Object key = entry.getKey();
                MediosPagosBean value = entry.getValue();

                lista.add((NovusUtils.str_pad(value.getDescripcion(), 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("$ " + dff.format(value.getValor()) + "", 28, " ", "STR_PAD_LEFT"))
                        .getBytes());
                lista.add(("\r\n").getBytes());
            }

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("ANULACIONES\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("INVENTARIO COMPUTADORES\r\n").getBytes());
            lista.add(("SISTEMA P.O.S\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add((NovusUtils.str_pad("CANTIDAD", 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("1", 28, " ", "STR_PAD_LEFT")).getBytes());

            lista.add((NovusUtils.str_pad("NOMBRE/SERIAL:", 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad(InetAddress.getLocalHost().getHostName().toUpperCase(), 28, " ",
                    "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add((NovusUtils.str_pad("UBUCACION:", 10, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad(Main.credencial.getEmpresa().getRazonSocial(), 38, " ", "STR_PAD_LEFT"))
                    .getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("Fabricante Software:\r\n").getBytes());
            lista.add(("DATAGAS S.A.S\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("Nit: 900204620-8\r\n").getBytes());
            lista.add(("DATAPOS EDS v." + Main.VERSION_APP).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            if (descrp != null) {
                String[] footer = descrp.getFooter().split("\r\n");
                for (String text : footer) {
                    lista.add((text + "\r\n").getBytes());
                }
            }
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimirReporteVenta(ArrayList<MovimientosBean> ventas) {

        Collections.sort(ventas);

        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
        int totalVentas = 0;
        int cantidadVentas = 0;
        int cantidadVentasAnuladas = 0;
        for (MovimientosBean venta : ventas) {
            if (venta.getEstado() == 0) {
                totalVentas += venta.getVentaTotal();
                cantidadVentas++;
            } else {
                cantidadVentasAnuladas++;
            }
        }

        ArrayList<byte[]> lista = new ArrayList<>();
        int FULL_PAGE = 48;

        EmpresaBean empresa;
        try {
            EquipoDao dao = new EquipoDao();
            empresa = dao.findEmpresa(Main.credencial);
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_FONT_D);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((empresa.getAlias() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_FONT_C);
            lista.add((empresa.getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(("REPORTE GENERAL\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            lista.add(("" + NovusUtils.str_pad("FECHA DE IMPR.", 36, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + sdf.format(new Date()), 12, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("" + NovusUtils.str_pad("CANTIDAD DE TODAS LAS VENTAS ", 36, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add((NovusUtils.str_pad("" + cantidadVentas, 12, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("" + NovusUtils.str_pad("TOTAL DE TODAS LAS VENTAS ", 36, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(totalVentas), 12, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());

            if (cantidadVentasAnuladas > 0) {
                lista.add(("" + NovusUtils.str_pad("CANTIDAD DE TODAS LAS VENTAS ANULADAS", 38, " ",
                        NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("" + cantidadVentasAnuladas, 10, " ", "STR_PAD_LEFT")).getBytes());
                lista.add(("\r\n").getBytes());

            }
            lista.add(("\r\n").getBytes());
            for (MovimientosBean venta : ventas) {
                if (venta.getEstado() == 0) {
                    String promotor = venta.getPersonaNombre();
                    if (venta.getPersonaNombre().length() > 4) {
                        promotor = venta.getPersonaNombre().substring(0, 4);
                    }

                    lista.add(("" + venta.getConsecutivo().getConsecutivo_actual()).getBytes());
                    lista.add((" - " + sdf.format(venta.getFecha())).getBytes());
                    lista.add((" - " + promotor).getBytes());

                    lista.add((NovusUtils.str_pad("$" + df.format(venta.getVentaTotal()), 12, " ", "STR_PAD_LEFT"))
                            .getBytes());
                    lista.add(("\r\n").getBytes());
                }
            }
            lista.add(("\r\nREPORTE VENTAS ANULADAS\r\n").getBytes());
            for (MovimientosBean venta : ventas) {
                if (venta.getEstado() == 1) {
                    String promotor = venta.getPersonaNombre();
                    if (venta.getPersonaNombre().length() > 4) {
                        promotor = venta.getPersonaNombre().substring(0, 4);
                    }

                    lista.add(("" + venta.getConsecutivo().getConsecutivo_actual()).getBytes());
                    lista.add((" - " + sdf.format(venta.getFecha())).getBytes());
                    lista.add((" - " + promotor).getBytes());

                    lista.add((NovusUtils.str_pad("$" + df.format(venta.getVentaTotal()), 12, " ", "STR_PAD_LEFT"))
                            .getBytes());
                    lista.add(("\r\n").getBytes());
                }
            }
            lista.add(("\r\n").getBytes());

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(("REPORTE POR VENDEDOR\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            TreeMap<Long, ArrayList<MovimientosBean>> ventasxvendedor = new TreeMap<>();

            for (MovimientosBean venta : ventas) {
                if (!ventasxvendedor.containsKey(venta.getPersonaId())) {
                    ventasxvendedor.put(venta.getPersonaId(), new ArrayList<>());
                }
                ventasxvendedor.get(venta.getPersonaId()).add(venta);
            }

            int i = 0;

            for (Map.Entry<Long, ArrayList<MovimientosBean>> entry : ventasxvendedor.entrySet()) {
                ArrayList<MovimientosBean> ventasvendedor = entry.getValue();

                float valor = 0;
                int cantidad = 0;
                int cantidadAnuladas = 0;
                for (MovimientosBean venta : ventasvendedor) {
                    if (venta.getEstado() == 0) {
                        valor += venta.getVentaTotal();
                        cantidad++;
                    } else {
                        cantidadAnuladas++;
                    }
                }

                if (!ventasvendedor.isEmpty()) {
                    if (i != 0) {
                        lista.add(printChar(FULL_PAGE, "-").getBytes());
                    }
                    lista.add(TCPPrinterService.TXT_BOLD_ON);

                    String vendedor = ventasvendedor.get(0).getPersonaNombre();
                    if (vendedor.length() >= 36) {
                        vendedor = vendedor.substring(0, 36);
                    }

                    lista.add((NovusUtils.str_pad(vendedor, 36, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$" + df.format(valor), 12, " ", "STR_PAD_LEFT")).getBytes());
                    lista.add(("\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);

                    lista.add(("" + NovusUtils.str_pad("CANTIDAD DE VENTAS ", 36, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add((NovusUtils.str_pad("" + cantidad, 12, " ", "STR_PAD_LEFT")).getBytes());
                    lista.add(("\r\n").getBytes());

                    if (cantidadAnuladas > 0) {
                        lista.add((""
                                + NovusUtils.str_pad("CANTIDAD DE VENTAS ANULADAS", 36, " ", NovusUtils.STR_PAD_RIGHT))
                                .getBytes());
                        lista.add((NovusUtils.str_pad("" + cantidadAnuladas, 12, " ", "STR_PAD_LEFT")).getBytes());
                        lista.add(("\r\n").getBytes());
                    }

                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(("\r\n").getBytes());
                    lista.add(("**DETALLES**\r\n").getBytes());
                    lista.add(("\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_LT);
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);

                    for (MovimientosBean mv : ventasvendedor) {

                        for (Map.Entry<Long, MovimientosDetallesBean> entry2 : mv.getDetalles().entrySet()) {
                            MovimientosDetallesBean detalle = entry2.getValue();

                            String und = "UND";
                            if (detalle.getTipo() == 5) {
                                und = "GL";
                            }

                            lista.add(TCPPrinterService.TXT_BOLD_ON);
                            lista.add((""
                                    + NovusUtils.str_pad(detalle.getDescripcion(), 38, " ", NovusUtils.STR_PAD_RIGHT))
                                    .getBytes());
                            lista.add(("\r\n").getBytes());
                            lista.add(TCPPrinterService.TXT_BOLD_OFF);
                            lista.add(("CONS: "
                                    + NovusUtils.str_pad(String.valueOf(mv.getConsecutivo().getConsecutivo_actual()),
                                            26, " ", NovusUtils.STR_PAD_RIGHT))
                                    .getBytes());
                            lista.add(("" + NovusUtils.str_pad(NovusUtils.fmt(detalle.getCantidad()) + " " + und + " ",
                                    10, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add((NovusUtils.str_pad("$" + df.format(detalle.getSubtotal()), 12, " ",
                                    "STR_PAD_LEFT")).getBytes());
                            lista.add(("\r\n").getBytes());
                        }

                    }
                }
                i++;
            }

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (DAOException | IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void imprimirArqueoCaja(JornadaBean jornada) {
        int FULL_PAGE = 48;
        ArrayList<byte[]> lista = new ArrayList<>();
        try {
            DescriptorBean descrp;
            SetupDao sdao = new SetupDao();
            descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);

            lista.add(("ARQUEO DE CAJA\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("FECHA INICIO JORNADA:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(jornada.getFechaInicial()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("PROMOTOR:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(jornada.getPersona().getNombre(), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("TURNO:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(
                    (NovusUtils.str_pad(jornada.getGrupoJornada() + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("SALDO BASE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getSaldo()) + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("REPORTE VENDEDOR" + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(NovusUtils.str_pad("NUMERO DE VENTAS:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(
                    (NovusUtils.str_pad(jornada.getCantidadVentasCanastilla() + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
            lista.add(NovusUtils.str_pad("TOTAL DE VENTAS:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getTotalVentasCanastilla()), 23, " ",
                    NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("DISCRIMINACION POR PRODUCTO" + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());

            for (byte[] bs : lista) {
                NovusUtils.printLn(new String(bs));
            }
        } catch (Exception | DAOException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printJornadaActual(JornadaBean jornada) {

        try {
            DescriptorBean descrp = null;
            try {
                SetupDao sdao = new SetupDao();
                descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            } catch (DAOException a) {
            }
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }

            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);

            lista.add(("INFORMACION DEL TURNO\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("FECHA INICIO JORNADA:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(jornada.getFechaInicial()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("PROMOTOR:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(jornada.getPersona().getNombre(), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("TURNO:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(
                    (NovusUtils.str_pad(jornada.getGrupoJornada() + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("SALDO BASE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getSaldo()) + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());

            int surtidor = 0;
            int cara = 0;
            for (Surtidor lectura : jornada.getLecturasIniciales()) {

                if (surtidor != lectura.getSurtidor()) {
                    surtidor = lectura.getSurtidor();
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(("LECTURAS INICIALES SURTIDOR " + surtidor + "\r\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }

                if (cara != lectura.getCara()) {
                    cara = lectura.getCara();
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(("\r\nCARA " + cara + "\r\n").getBytes());
                }

                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("MANGUERA " + lectura.getManguera() + ": ").getBytes());
                lista.add((lectura.getFamiliaDescripcion().toUpperCase() + "\r\n").getBytes());
                lista.add((lectura.getProductoDescripcion() + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("LI. VENTA:   \t\t"
                        + Utils.calculeCantidad(lectura.getTotalizadorVenta(), NovusConstante.FACTOR_INVENTARIO)
                        + "\r\n").getBytes());
                lista.add(("LI. VOLUMEN: \t\t"
                        + Utils.calculeCantidad(lectura.getTotalizadorVolumen(), NovusConstante.FACTOR_INVENTARIO)
                        + "\r\n").getBytes());
            }

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(("REPORTE VENDEDOR" + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(NovusUtils.str_pad("NUMERO DE VENTAS:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(jornada.getCantidadVentas() + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("TOTAL DE VENTAS:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getTotalVentas()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());

            if (!jornada.getMedios().isEmpty()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_FONT_A);
                lista.add(TCPPrinterService.TXT_NORMAL);
                lista.add(("REPORTE MEDIOS DE PAGOS" + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.str_pad("DESCRIPCION:", 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("CANT:", 14, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("TOTAL:", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                for (MediosPagosBean medios : jornada.getMedios()) {
                    lista.add(
                            NovusUtils.str_pad(medios.getDescripcion(), 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad(medios.getCantidad() + "", 14, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                    lista.add(NovusUtils.str_pad("$" + df.format(medios.getValor()), 11, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                }
                lista.add(("\r\n").getBytes());

            }
            if (true) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            }
            for (byte[] bs : lista) {
                NovusUtils.printLn(new String(bs));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonObject detallesVentas(PersonaBean persona) {
        String funcion = "CONSEGUIR TURNOS";
        String url = NovusConstante.SECURE_CENTRAL_POINT_DETALLES_VENTA_PROMOTOR;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");

        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, null, true, false, header);
        client.start();

        JsonObject response = null;

        try {
            client.join();
            response = client.getResponse();
        } catch (InterruptedException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
    }

    public void printReciboDiscriminadoConsecutivo(JsonObject jsonCierre, Date fechaConsulta,
            JsonObject reporteConsecutivo) {
        try {
            DescriptorBean descrp = null;
            try {
                SetupDao sdao = new SetupDao();
                descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            } catch (DAOException a) {
            }
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            int cantidadVentasCombustible = jsonCierre.get("cantidadVentasCombustible").getAsInt();
            int cantidadVentasCanastilla = jsonCierre.get("cantidadVentasCanastilla").getAsInt();
            float totalVentasCombustible = jsonCierre.get("totalVentasCombustible").getAsFloat();
            float totalVentasCanastilla = jsonCierre.get("totalVentasCanastilla").getAsFloat();
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(("DISCRIMINACION POR RESOLUCION \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + getFecha(new Date()), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("FECHA DIA CONSULTA:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(fechaConsulta) + " 00:00:00", 23, " ", "STR_PAD_LEFT"))
                    .getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("REPORTE VENTAS DIARIO" + " \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("CANT. VENTAS COMBUSTIBLE:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + cantidadVentasCombustible, 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("CANT. VENTAS CANASTILLA:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + cantidadVentasCanastilla, 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS COMBUSTIBLE:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(
                    (NovusUtils.str_pad("$" + df.format(totalVentasCombustible), 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS CANASTILLA:", 34, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("$" + df.format(totalVentasCanastilla), 14, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("\r\n").getBytes());

            JsonArray data = reporteConsecutivo.get("data") != null && reporteConsecutivo.get("data").isJsonArray()
                    ? reporteConsecutivo.getAsJsonArray("data")
                    : new JsonArray();
            TreeMap<Long, TreeMap<Long, MovimientosBean>> movimientosResolucionCanastilla = new TreeMap<>();
            TreeMap<Long, TreeMap<Long, VentasBean>> movimientosResolucionCombustible = new TreeMap<>();
            TreeMap<Long, ArrayList<MediosPagosBean>> mediosResolucion = new TreeMap<>();
            TreeMap<Long, ConsecutivoBean> resoluciones = new TreeMap<>();
            boolean hayDetalles = false;
            for (JsonElement element : data) {
                JsonObject jsonReporteConsecutivo = element.getAsJsonObject();
                JsonArray jsonArrayVentasCanastilla = jsonReporteConsecutivo.getAsJsonArray("ventas_canastilla");
                JsonArray jsonArrayVentasCombustible = jsonReporteConsecutivo.getAsJsonArray("ventas");
                TreeMap<Long, MovimientosBean> categorizacion = new TreeMap<>();
                TreeMap<Long, VentasBean> categorizacionCombustible = new TreeMap<>();
                long idResolucion = jsonReporteConsecutivo.get("id").getAsLong();
                for (JsonElement element2 : jsonArrayVentasCanastilla) {
                    JsonObject jsonVentas = element2.getAsJsonObject();
                    JsonArray jsonArrayDetallesVentas = jsonVentas.getAsJsonArray("detalles");
                    for (JsonElement element3 : jsonArrayDetallesVentas) {
                        JsonObject jsonProductos = element3.getAsJsonObject();
                        JsonArray jsonImpuestoArray = jsonProductos.getAsJsonArray("impuestos");
                        float impuestoProducto = 0;
                        for (JsonElement element4 : jsonImpuestoArray) {
                            JsonObject jsonImpuesto = element4.getAsJsonObject();
                            impuestoProducto += jsonImpuesto.get("impuesto_valor").getAsFloat();
                        }
                        hayDetalles = true;
                        float productoPrecio = jsonProductos.get("sub_total").getAsFloat() + impuestoProducto;
                        long categoriaId = jsonProductos.get("categoriaId").getAsLong();
                        String categoriaDesc = jsonProductos.get("categoriaDescripcion").getAsString();
                        MovimientosBean mov = new MovimientosBean();
                        CategoriaBean cat = new CategoriaBean();
                        cat.setId(categoriaId);
                        cat.setGrupo(categoriaDesc);
                        mov.setVentaTotal(productoPrecio);
                        mov.setImpuestoTotal(impuestoProducto);
                        mov.setCantidadTotalProductos(jsonProductos.get("cantidad").getAsLong());
                        mov.setCategoriasMovimiento(cat);
                        if (categorizacion.containsKey(categoriaId)) {
                            MovimientosBean m = categorizacion.get(categoriaId);
                            m.setVentaTotal(m.getVentaTotal() + mov.getVentaTotal());
                            m.setImpuestoTotal(m.getImpuestoTotal() + mov.getImpuestoTotal());
                            m.setCantidadTotalProductos(
                                    m.getCantidadTotalProductos() + mov.getCantidadTotalProductos());
                        } else {
                            categorizacion.put(categoriaId, mov);
                        }
                    }
                }
                for (JsonElement element2 : jsonArrayVentasCombustible) {
                    JsonObject jsonVentas = element2.getAsJsonObject();
                    long idFamilia = jsonVentas.get("familiaId").getAsLong();
                    String familiaDesc = jsonVentas.get("familiaDesc").getAsString();
                    VentasBean venta = new VentasBean();
                    Surtidor sur = new Surtidor();
                    sur.setFamiliaIdentificador(idFamilia);
                    sur.setFamiliaDescripcion(familiaDesc);
                    venta.setSurtidor(sur);
                    venta.setId(jsonVentas.get("id").getAsLong());
                    venta.setCantidadVenta(1);
                    venta.setCantidadCombustible(jsonVentas.get("cantidad_combustible").getAsFloat());
                    venta.setUnidadMedida(jsonVentas.get("unidad_medida").getAsString());
                    venta.setVentaTotal(jsonVentas.get("venta_total").getAsFloat());
                    venta.setImpuestoTotal(jsonVentas.get("impuesto_total").getAsFloat());
                    if (categorizacionCombustible.containsKey(idFamilia)) {
                        VentasBean ven = categorizacionCombustible.get(idFamilia);
                        ven.setVentaTotal(ven.getVentaTotal() + venta.getVentaTotal());
                        ven.setCantidadVenta(ven.getCantidadVenta() + 1);
                        ven.setCantidadCombustible(ven.getCantidadCombustible() + venta.getCantidadCombustible());
                        ven.setImpuestoTotal(ven.getImpuestoTotal() + venta.getImpuestoTotal());
                    } else {
                        categorizacionCombustible.put(idFamilia, venta);
                    }
                    hayDetalles = true;
                }
                JsonArray jsonArrayMediosPagos = jsonReporteConsecutivo.getAsJsonArray("mediosPagos");
                ArrayList<MediosPagosBean> mediosPagos = new ArrayList<>();
                for (JsonElement elementMedios : jsonArrayMediosPagos) {
                    JsonObject jsonMedios = elementMedios.getAsJsonObject();
                    MediosPagosBean medio = new MediosPagosBean();
                    medio.setId(jsonMedios.get("id_medio_de_pago").getAsLong());
                    medio.setDescripcion(jsonMedios.get("nombre_medio_de_pago").getAsString());
                    medio.setValor(jsonMedios.get("valor_total").getAsFloat());
                    medio.setCantidad(jsonMedios.get("cantidad").getAsInt());
                    mediosPagos.add(medio);
                }
                mediosResolucion.put(idResolucion, mediosPagos);
                ConsecutivoBean consecutivo = new ConsecutivoBean();
                consecutivo.setId(idResolucion);
                consecutivo.setPrefijo(jsonReporteConsecutivo.get("prefijo") != null
                        && !jsonReporteConsecutivo.get("prefijo").isJsonNull()
                        ? jsonReporteConsecutivo.get("prefijo").getAsString()
                        : "");
                consecutivo.setConsecutivo_inicial(jsonReporteConsecutivo.get("consecutivo_inicial") != null
                        && !jsonReporteConsecutivo.get("consecutivo_inicial").isJsonNull()
                        ? jsonReporteConsecutivo.get("consecutivo_inicial").getAsLong()
                        : -1);
                consecutivo.setConsecutivo_final(jsonReporteConsecutivo.get("consecutivo_final") != null
                        && !jsonReporteConsecutivo.get("consecutivo_final").isJsonNull()
                        ? jsonReporteConsecutivo.get("consecutivo_final").getAsLong()
                        : -1);
                movimientosResolucionCanastilla.put(idResolucion, categorizacion);
                movimientosResolucionCombustible.put(idResolucion, categorizacionCombustible);
                resoluciones.put(idResolucion, consecutivo);
            }
            if (hayDetalles) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("DETALLES GENERALES \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                for (Map.Entry<Long, TreeMap<Long, MovimientosBean>> entry : movimientosResolucionCanastilla
                        .entrySet()) {
                    Long resolucionId = entry.getKey();
                    float totalVentas = 0;
                    for (Map.Entry<Long, MovimientosBean> x : movimientosResolucionCanastilla.get(resolucionId)
                            .entrySet()) {
                        MovimientosBean mov = x.getValue();
                        totalVentas += mov.getVentaTotal();
                    }
                    for (Map.Entry<Long, VentasBean> x : movimientosResolucionCombustible.get(resolucionId)
                            .entrySet()) {
                        VentasBean mov = x.getValue();
                        totalVentas += mov.getVentaTotal();
                    }
                    if (!movimientosResolucionCanastilla.get(resolucionId).isEmpty()
                            || !movimientosResolucionCombustible.get(resolucionId).isEmpty()) {
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add((NovusUtils.str_pad("CONS. INI: ", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add((NovusUtils.str_pad(
                                resoluciones.get(resolucionId).getPrefijo() + "-"
                                + resoluciones.get(resolucionId).getConsecutivo_inicial(),
                                13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add((NovusUtils.str_pad("CONS. FIN:", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add((NovusUtils.str_pad(
                                resoluciones.get(resolucionId).getPrefijo() + "-"
                                + resoluciones.get(resolucionId).getConsecutivo_final(),
                                13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(("\n").getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add((NovusUtils.str_pad("NRO VENTAS: ", 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add((NovusUtils.str_pad(
                                movimientosResolucionCanastilla.get(resolucionId).size()
                                + movimientosResolucionCombustible.get(resolucionId).size() + "",
                                9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add((NovusUtils.str_pad("TOTAL VENTAS:", 14, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add((NovusUtils.str_pad("$" + df.format(totalVentas), 13, " ", NovusUtils.STR_PAD_RIGHT))
                                .getBytes());
                        lista.add(("\n").getBytes());
                    }
                    if (!movimientosResolucionCanastilla.get(resolucionId).isEmpty()) {
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add((NovusUtils.str_pad("CATEGORIA", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("CANT. ", 7, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("V. TOTAL", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("V. IMPUESTO", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(("\n").getBytes());
                        for (Map.Entry<Long, MovimientosBean> entry2 : movimientosResolucionCanastilla.get(resolucionId)
                                .entrySet()) {
                            Long categoriaId = entry2.getKey();
                            MovimientosBean mov = entry2.getValue();
                            lista.add((NovusUtils.str_pad(mov.getCategoriasMovimiento().getGrupo(), 15, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add((NovusUtils.str_pad(mov.getCantidadTotalProductos() + "", 7, " ", "STR_PAD_BOTH"))
                                    .getBytes());
                            lista.add((NovusUtils.str_pad("$" + df.format(mov.getVentaTotal()), 13, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add((NovusUtils.str_pad("$" + df.format(mov.getImpuestoTotal()), 13, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add(("\n").getBytes());
                        }
                        lista.add(("\n").getBytes());
                    } else if (!movimientosResolucionCombustible.get(resolucionId).isEmpty()) {
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add((NovusUtils.str_pad("FAMILIA", 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("CANT", 5, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("VOLUMEN", 9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("V. TOTAL", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add((NovusUtils.str_pad("V. IMPUESTO", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(("\n").getBytes());
                        for (Map.Entry<Long, VentasBean> entry2 : movimientosResolucionCombustible.get(resolucionId)
                                .entrySet()) {
                            Long categoriaId = entry2.getKey();
                            VentasBean mov = entry2.getValue();
                            lista.add((NovusUtils.str_pad(mov.getSurtidor().getFamiliaDescripcion(), 12, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add((NovusUtils.str_pad(mov.getCantidadVenta() + "", 5, " ", "STR_PAD_BOTH"))
                                    .getBytes());
                            lista.add((NovusUtils.str_pad(mov.getCantidadCombustible() + mov.getUnidadMedida(), 9, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add((NovusUtils.str_pad("$" + df.format(mov.getVentaTotal()), 11, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add((NovusUtils.str_pad("$" + df.format(mov.getImpuestoTotal()), 11, " ",
                                    NovusUtils.STR_PAD_RIGHT)).getBytes());
                            lista.add(("\n").getBytes());
                        }
                        lista.add(("\n").getBytes());
                    }
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((NovusUtils.str_pad("MEDIOS DE PAGO", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add(("\n").getBytes());
                    lista.add((NovusUtils.str_pad("DESCRIPCION", 18, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("CANTIDAD", 15, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add((NovusUtils.str_pad("VALOR", 15, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add(("\n").getBytes());
                    for (MediosPagosBean medio : mediosResolucion.get(resolucionId)) {
                        lista.add((NovusUtils.str_pad(medio.getDescripcion(), 18, " ", NovusUtils.STR_PAD_RIGHT))
                                .getBytes());
                        lista.add((NovusUtils.str_pad(medio.getCantidad() + "", 15, " ", "STR_PAD_BOTH")).getBytes());
                        lista.add((NovusUtils.str_pad("$" + df.format(medio.getValor()), 15, " ", "STR_PAD_BOTH"))
                                .getBytes());
                        lista.add(("\n").getBytes());
                    }
                    lista.add(("\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }
            }

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printReciboDiscriminadoConsecutivoV2(Date fechaConsulta, JsonObject reporteConsecutivo) {
        try {
            DescriptorBean descrp = null;
            try {
                SetupDao sdao = new SetupDao();
                descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            } catch (DAOException a) {
            }
            int cantidadTransacciones = reporteConsecutivo.get("numeroTransacciones").getAsInt();
            float totalVenta = reporteConsecutivo.get("total").getAsFloat();
            JsonArray jsonArrayVentasCategorizadas = reporteConsecutivo.getAsJsonArray("ventasPorCategoria");
            JsonArray jsonArrayVentasPorFamilia = reporteConsecutivo.getAsJsonArray("ventasPorFamilia");
            JsonArray jsonArrayMediosPagos = reporteConsecutivo.getAsJsonArray("mediosPagos");
            String prefijo = reporteConsecutivo.get("prefijo").getAsString();
            int consecutivoInicial = reporteConsecutivo.get("consecutivoInicial").getAsInt();
            int consecutivoFinal = reporteConsecutivo.get("consecutivoFinal").getAsInt();

            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add((Main.credencial.getEmpresa().getCodigo() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add((Main.credencial.getEmpresa().getCiudadDescripcion() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(("DISCRIMINACION POR RESOLUCION - " + prefijo + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + getFecha(new Date()), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("FECHA DIA CONSULTA:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(fechaConsulta), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("MAC EQUIPO:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(Main.credencial.getMac(), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("CONSECUTIVO INICIAL:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(prefijo + "-" + consecutivoInicial, 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("CONSECUTIVO FINAL:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(prefijo + "-" + consecutivoFinal, 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("NRO TRANSACCIONES:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(cantidadTransacciones + "", 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS:", 25, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("$" + df.format(totalVenta), 23, " ", "STR_PAD_LEFT")).getBytes());
            lista.add(("\r\n").getBytes());
            if (jsonArrayVentasCategorizadas.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN CATEGORIAS CANASTILLA" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("CATEGORIA", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANT. ", 7, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("TOTAL", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("IMPUESTO", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementVentasCategorias : jsonArrayVentasCategorizadas) {
                    JsonObject jsonVentaCategoria = elementVentasCategorias.getAsJsonObject();
                    String descripcion = jsonVentaCategoria.get("descripcion").getAsString();
                    int cantidadApariciones = jsonVentaCategoria.get("cantidad").getAsInt();
                    float total = jsonVentaCategoria.get("total").getAsFloat();
                    float totalImpuesto = jsonVentaCategoria.get("totalImpuesto").getAsFloat();
                    lista.add((NovusUtils.str_pad(descripcion, 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadApariciones + "", 7, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add(
                            (NovusUtils.str_pad("$" + df.format(total), 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$" + df.format(totalImpuesto), 13, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
            }
            if (jsonArrayVentasPorFamilia.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN FAMILIAS COMBUSTIBLE" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("FAMILIA", 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANT", 5, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("VOLUMEN", 9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("TOTAL", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("DESCUENTO", 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementVentasFamilia : jsonArrayVentasPorFamilia) {
                    JsonObject jsonVentaFamilia = elementVentasFamilia.getAsJsonObject();
                    String descripcion = jsonVentaFamilia.get("descripcion").getAsString();
                    String volumen = jsonVentaFamilia.get("volumen").getAsString();
                    int cantidadApariciones = jsonVentaFamilia.get("cantidad").getAsInt();
                    float total = jsonVentaFamilia.get("total").getAsFloat();
                    float totalDescuento = jsonVentaFamilia.get("descuento").getAsFloat();
                    lista.add((NovusUtils.str_pad(descripcion, 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadApariciones + "", 5, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add((NovusUtils.str_pad(volumen, 9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add(
                            (NovusUtils.str_pad("$" + df.format(total), 11, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("$" + df.format(totalDescuento), 11, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
            }

            if (jsonArrayMediosPagos.size() > 0) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("RESUMEN MEDIOS PAGOS" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add((NovusUtils.str_pad("DESCRIPCION", 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("CANTIDAD", 13, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add((NovusUtils.str_pad("VALOR", 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("\n").getBytes());
                for (JsonElement elementMediosPagos : jsonArrayMediosPagos) {
                    JsonObject jsonMedioPago = elementMediosPagos.getAsJsonObject();
                    String nombre = jsonMedioPago.get("nombreMedioDePago").getAsString();
                    int cantidadVentas = jsonMedioPago.get("cantidad").getAsInt();
                    float total = jsonMedioPago.get("valorTotal").getAsFloat();
                    lista.add((NovusUtils.str_pad(nombre, 20, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad(cantidadVentas + " ", 13, " ", "STR_PAD_BOTH")).getBytes());
                    lista.add(
                            (NovusUtils.str_pad("$" + df.format(total), 15, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add(("\n").getBytes());
                }
                lista.add(("\n").getBytes());
                lista.add(("\n").getBytes());
            }
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    public ArrayList<MovimientosDetallesBean> obtenerCategoriasProductos(Set<Long> productosIds) {
        MovimientosDao mdao = new MovimientosDao();
        ArrayList<MovimientosDetallesBean> productosConCategorias = null;
        try {
            productosConCategorias = mdao.getCategoriasMovimiento(productosIds);


        } catch (DAOException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productosConCategorias;
    }
     */

    public List<MovimientosDetallesBean> obtenerCategoriasProductos(Set<Long> productosIds) {
        try {
            return new GetCategoriasMovimientoUseCase(productosIds).execute();
        } catch (Exception ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptyList();
        }
    }

    public void printJornadaCierre(JornadaBean jornada, PersonaBean persona, String cierre, boolean detalles,
            boolean logview) {
        float cantidad_combustible = 0;
        int i = 0, cantidad = 0;

        TreeMap<Long, VentasBean> ventasxPromotor = new TreeMap<>();
        TreeMap<Integer, VentasBean> calibraciones = new TreeMap<>();
        TreeMap<Integer, VentasBean> consumoPropio = new TreeMap<>();
        long cantidadVentasAnuladas = 0;
        for (VentasBean venta : jornada.getVentas()) {
            if (venta.isAnulada()) {
                cantidadVentasAnuladas++;
            } else {
                cantidad_combustible += venta.getCantidadCombustible();
            }
        }

        for (VentasBean venta : jornada.getCalibraciones()) {
            if (calibraciones.containsKey(venta.getSurtidor().getManguera())) {
                calibraciones.get(venta.getSurtidor().getManguera()).setVentaTotal(
                        calibraciones.get(venta.getSurtidor().getManguera()).getVentaTotal() + venta.getVentaTotal());
                calibraciones.get(venta.getSurtidor().getManguera())
                        .setCantidadVenta(calibraciones.get(venta.getSurtidor().getManguera()).getCantidadVenta() + 1);
            } else {
                Surtidor sur = new Surtidor();
                calibraciones.put(venta.getSurtidor().getManguera(), new VentasBean());
                calibraciones.get(venta.getSurtidor().getManguera()).setPersonaNombre(venta.getPersonaNombre());
                calibraciones.get(venta.getSurtidor().getManguera()).setVentaTotal(venta.getVentaTotal());
                sur.setManguera(venta.getSurtidor().getManguera());
                calibraciones.get(venta.getSurtidor().getManguera()).setSurtidor(sur);
                calibraciones.get(venta.getSurtidor().getManguera()).setCantidadVenta(1);
            }
        }
        for (VentasBean venta : jornada.getConsumoPropio()) {
            if (consumoPropio.containsKey(venta.getSurtidor().getManguera())) {
                consumoPropio.get(venta.getSurtidor().getManguera()).setVentaTotal(
                        consumoPropio.get(venta.getSurtidor().getManguera()).getVentaTotal() + venta.getVentaTotal());
                consumoPropio.get(venta.getSurtidor().getManguera())
                        .setCantidadVenta(consumoPropio.get(venta.getSurtidor().getManguera()).getCantidadVenta() + 1);
            } else {
                Surtidor sur = new Surtidor();
                consumoPropio.put(venta.getSurtidor().getManguera(), new VentasBean());
                consumoPropio.get(venta.getSurtidor().getManguera()).setPersonaNombre(venta.getPersonaNombre());
                consumoPropio.get(venta.getSurtidor().getManguera()).setVentaTotal(venta.getVentaTotal());
                sur.setManguera(venta.getSurtidor().getManguera());
                consumoPropio.get(venta.getSurtidor().getManguera()).setSurtidor(sur);
                consumoPropio.get(venta.getSurtidor().getManguera()).setCantidadVenta(1);
            }
        }
        if (persona.isPrincipal()) {
            for (VentasBean venta : jornada.getVentas()) {
                if (!venta.isAnulada()) {
                    if (ventasxPromotor.containsKey(venta.getPersonas_id())) {
                        ventasxPromotor.get(venta.getPersonas_id()).setVentaTotal(
                                ventasxPromotor.get(venta.getPersonas_id()).getVentaTotal() + venta.getVentaTotal());
                        ventasxPromotor.get(venta.getPersonas_id())
                                .setCantidadVenta(ventasxPromotor.get(venta.getPersonas_id()).getCantidadVenta() + 1);
                        ventasxPromotor.get(venta.getPersonas_id())
                                .setCantidadCombustible(venta.getCantidadCombustible()
                                        + ventasxPromotor.get(venta.getPersonas_id()).getCantidadCombustible());
                    } else {
                        ventasxPromotor.put(venta.getPersonas_id(), new VentasBean());
                        ventasxPromotor.get(venta.getPersonas_id()).setPersonaNombre(venta.getPersonaNombre());
                        ventasxPromotor.get(venta.getPersonas_id()).setVentaTotal(venta.getVentaTotal());
                        ventasxPromotor.get(venta.getPersonas_id()).setCantidadVenta(1);
                        ventasxPromotor.get(venta.getPersonas_id())
                                .setCantidadCombustible(venta.getCantidadCombustible());
                        ventasxPromotor.get(venta.getPersonas_id()).setUnidadMedida(venta.getUnidadMedida());
                    }
                }
            }
        }
        try {
            DescriptorBean descrp = null;
            try {
                SetupDao sdao = new SetupDao();
                descrp = sdao.getDescriptores(Main.credencial.getEmpresas_id());
            } catch (DAOException a) {
            }
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add((cierre + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("FECHA INICIO JORNADA:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(jornada.getFechaInicial()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("FECHA FIN JORNADA:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(getFecha(jornada.getFechaFinal()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(NovusUtils.str_pad("PROMOTOR:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad(persona.getNombre(), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("TURNO:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(
                    (NovusUtils.str_pad(jornada.getGrupoJornada() + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(NovusUtils.str_pad("SALDO BASE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getSaldo()) + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());

            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(("REPORTE GENERAL VENTAS" + " \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(("" + NovusUtils.str_pad("NUMERO DE VENTAS:", 30, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + jornada.getCantidadVentas(), 18, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("" + NovusUtils.str_pad("NUMERO DE VENTAS CANASTILLA:", 30, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(
                    (NovusUtils.str_pad("" + jornada.getCantidadVentasCanastilla(), 18, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS:", 30, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getTotalVentas()), 18, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("" + NovusUtils.str_pad("TOTAL VENTAS CANASTILLA:", 30, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add((NovusUtils.str_pad("$" + df.format(jornada.getTotalVentasCanastilla()), 18, " ",
                    NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(("" + NovusUtils.str_pad("VENTAS ANULADAS:", 30, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add((NovusUtils.str_pad("" + cantidadVentasAnuladas + " ", 18, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(("\r\n").getBytes());

            if (!ventasxPromotor.isEmpty()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("REPORTE VENTAS PROMOTORES" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.str_pad("DESCRIPCION", 20, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("NO. VENTAS", 14, " ", NovusUtils.STR_PAD_BOTH).getBytes());
                lista.add(NovusUtils.str_pad("TOTAL", 14, " ", NovusUtils.STR_PAD_BOTH).getBytes());
                lista.add(("\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                for (Map.Entry<Long, VentasBean> v : ventasxPromotor.entrySet()) {
                    lista.add(NovusUtils.str_pad((v.getValue().getPersonaNombre() + "").trim().substring(0, (Math.min(20,
                            (v.getValue().getPersonaNombre() + "").trim().length()))),
                            20, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                    lista.add(NovusUtils.str_pad(v.getValue().getCantidadVenta() + "", 14, " ", NovusUtils.STR_PAD_BOTH)
                            .getBytes());
                    lista.add(NovusUtils
                            .str_pad("$" + df.format(v.getValue().getVentaTotal()), 14, " ", NovusUtils.STR_PAD_BOTH)
                            .getBytes());
                }
            }
            if (!jornada.getMedios().isEmpty()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("REPORTE MEDIOS DE PAGO GENERAL" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.str_pad("DESCRIPCION", 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("CANT", 14, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("TOTAL", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                for (MediosPagosBean medios : jornada.getMedios()) {
                    lista.add(
                            NovusUtils.str_pad(medios.getDescripcion(), 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad(medios.getCantidad() + "", 14, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                    lista.add(NovusUtils.str_pad("$" + df.format(medios.getValor()), 11, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                }
                if (jornada.getVentasProductos() != null && !jornada.getVentasProductos().isEmpty()) {
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(("REPORTE VENTAS PRODUCTOS" + " \r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_LT);

                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("DESCRIPCION", 15, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad("NO. VENTAS", 12, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad("CANTIDAD", 10, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad("TOTAL", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(("\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    for (ProductoBean producto : jornada.getVentasProductos()) {
                        lista.add(NovusUtils.str_pad(
                                producto.getDescripcion().substring(0,
                                        Math.min(15, producto.getDescripcion().length())),
                                15, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                        lista.add(NovusUtils.str_pad(Math.round(producto.getCantidadUnidad()) + "", 12, " ",
                                NovusUtils.STR_PAD_BOTH).getBytes());
                        lista.add(NovusUtils.str_pad(producto.getCantidad() + producto.getUnidades_medida(), 10, " ",
                                NovusUtils.STR_PAD_RIGHT).getBytes());
                        lista.add(NovusUtils
                                .str_pad("$" + df.format(producto.getCosto()), 11, " ", NovusUtils.STR_PAD_RIGHT)
                                .getBytes());
                    }
                }
            }
            if (detalles) {
                if (!jornada.getVentas().isEmpty()) {
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(("DETALLES VENTAS COMBUSTIBLE" + " \r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_LT);
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }
                int con = 0;
                for (VentasBean venta : jornada.getVentas()) {
                    if (!venta.isAnulada()) {
                        if (con > 0) {
                            lista.add(printChar(FULL_PAGE, "-").getBytes());
                        }
                        Surtidor surtidor = venta.getSurtidor();
                        lista.add(TCPPrinterService.TXT_BOLD_ON);
                        lista.add(("VENTA NRO" + (venta.getConsecutivoVenta()) + ":\r\n").getBytes());
                        lista.add(TCPPrinterService.TXT_BOLD_OFF);
                        lista.add(NovusUtils.str_pad("FECHA VENTA:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                        lista.add((NovusUtils.str_pad(getFecha(venta.getFechaVenta()), 23, " ",
                                NovusUtils.STR_PAD_RIGHT)).getBytes());
                        lista.add(
                                NovusUtils
                                        .str_pad("S" + surtidor.getSurtidor() + "C" + surtidor.getCara() + "M"
                                                + surtidor.getManguera(), 14, " ", NovusUtils.STR_PAD_RIGHT)
                                        .getBytes());

                        lista.add(NovusUtils.str_pad("CANT: " + venta.getCantidadCombustible() + NovusConstante.MEDIDA,
                                15, " ", NovusUtils.STR_PAD_RIGHT).getBytes());

                        lista.add(NovusUtils
                                .str_pad("PLACA: " + (venta.getPlaca().equals("") ? "NO APLICA" : venta.getPlaca()), 19,
                                        " ", NovusUtils.STR_PAD_RIGHT)
                                .getBytes());
                        lista.add(NovusUtils.str_pad("VENTA TOTAL: ", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                        lista.add((NovusUtils.str_pad("$" + df.format(venta.getVentaTotal()), 23, " ",
                                NovusUtils.STR_PAD_RIGHT)).getBytes());
                    }
                }
            }
            if (!jornada.getCalibraciones().isEmpty()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("CALIBRACIONES" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.str_pad("MANGUERA:", 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("CANT:", 14, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("TOTAL:", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                for (Map.Entry<Integer, VentasBean> v : calibraciones.entrySet()) {
                    lista.add(
                            NovusUtils
                                    .str_pad(
                                            ("MANGUERA " + v.getValue().getSurtidor().getManguera()).trim().substring(0,
                                                    (Math.min(23,
                                                            ("MANGUERA " + v.getValue().getSurtidor().getManguera())
                                                                    .trim().length()))),
                                            23, " ", NovusUtils.STR_PAD_RIGHT)
                                    .getBytes());
                    lista.add(
                            NovusUtils.str_pad(v.getValue().getCantidadVenta() + "", 14, " ", NovusUtils.STR_PAD_RIGHT)
                                    .getBytes());
                    lista.add(NovusUtils
                            .str_pad("$" + df.format(v.getValue().getVentaTotal()), 11, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                }
            }
            if (!jornada.getConsumoPropio().isEmpty()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("CONSUMO PROPIO" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(NovusUtils.str_pad("MANGUERA:", 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("CANT:", 14, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(NovusUtils.str_pad("TOTAL:", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                for (Map.Entry<Integer, VentasBean> v : consumoPropio.entrySet()) {
                    lista.add(
                            NovusUtils
                                    .str_pad(
                                            ("MANGUERA " + v.getValue().getSurtidor().getManguera()).trim().substring(0,
                                                    (Math.min(23,
                                                            ("MANGUERA " + v.getValue().getSurtidor().getManguera())
                                                                    .trim().length()))),
                                            23, " ", NovusUtils.STR_PAD_RIGHT)
                                    .getBytes());
                    lista.add(
                            NovusUtils.str_pad(v.getValue().getCantidadVenta() + "", 14, " ", NovusUtils.STR_PAD_RIGHT)
                                    .getBytes());
                    lista.add(NovusUtils
                            .str_pad("$" + df.format(v.getValue().getVentaTotal()), 11, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                }
            }

            if (persona.isPrincipal() && jornada.getMedidasTanquesIniciales().get("detalles") != null) {

                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("MEDIDAS DE TANQUES" + " \r\n").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());

                JsonArray tanquesInicialesArray = jornada.getMedidasTanquesIniciales().get("detalles") != null
                        && jornada.getMedidasTanquesIniciales().get("detalles").isJsonArray()
                        ? jornada.getMedidasTanquesIniciales().get("detalles").getAsJsonArray()
                        : new JsonArray();
                JsonArray tanquesFinalesArray = jornada.getMedidasTanquesFinales().get("detalles") != null
                        && jornada.getMedidasTanquesFinales().get("detalles").isJsonArray()
                        ? jornada.getMedidasTanquesFinales().get("detalles").getAsJsonArray()
                        : new JsonArray();
                int index = 0;
                for (JsonElement elementTanque : tanquesInicialesArray) {
                    JsonObject jsonTanqueIniciales = elementTanque.getAsJsonObject();
                    JsonObject jsonTanqueFinales = tanquesFinalesArray.get(index).getAsJsonObject();
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(("LECTURAS TANQUE " + jsonTanqueIniciales.get("numero").getAsInt() + ":\r\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(("PRODUCTO: ").getBytes());
                    lista.add((jsonTanqueIniciales.get("productos_descripcion").getAsString() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add(("LEC. AGUA INIC   :   \t\t"
                            + jsonTanqueIniciales.get("atributos").getAsJsonObject().get("agua").getAsFloat() + "\r\n")
                            .getBytes());
                    lista.add(("LEC. AGUA FINAL  :   \t\t"
                            + jsonTanqueFinales.get("atributos").getAsJsonObject().get("agua").getAsFloat() + "\r\n")
                            .getBytes());
                    lista.add(("LEC. TOTAL INIC  :   \t\t" + jsonTanqueIniciales.get("altura").getAsFloat() + "\r\n")
                            .getBytes());
                    lista.add(("LEC. TOTAL FINAL :   \t\t" + jsonTanqueFinales.get("altura").getAsFloat() + "\r\n")
                            .getBytes());
                    lista.add(("DIF LECTURA AGUA :   \t\t"
                            + Math.abs(jsonTanqueFinales.get("atributos").getAsJsonObject().get("agua").getAsFloat()
                                    - jsonTanqueIniciales.get("atributos").getAsJsonObject().get("agua").getAsFloat())
                            + "\r\n").getBytes());
                    lista.add(("DIF LECTURA TOTAL:   \t\t" + Math.abs(jsonTanqueFinales.get("altura").getAsFloat()
                            - jsonTanqueIniciales.get("altura").getAsFloat()) + "\r\n").getBytes());
                    lista.add(("\r\n").getBytes());
                    index++;
                }
            }
            int surtidor = 0;
            int cara = 0;
            long totalDiferenciasLecturasVolumen = 0, totalDiferenciasLecturasVentas = 0;
            for (int j = 0; j < jornada.getLecturasIniciales().size(); j++) {
                if (surtidor == 0) {
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(("LECTURAS CIERRE TURNO" + " \r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_LT);
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }
                Surtidor lecturaInicial = jornada.getLecturasIniciales().get(j);
                Surtidor lecturaFinal = jornada.getLecturasFinales().get(j);

                if (surtidor != lecturaInicial.getSurtidor()) {
                    surtidor = lecturaInicial.getSurtidor();
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(("LECTURAS SURTIDOR " + surtidor + "\r\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }
                if (cara != lecturaInicial.getCara()) {
                    cara = lecturaInicial.getCara();
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(("\r\nCARA " + cara + "\r\n").getBytes());
                }
                lista.add(TCPPrinterService.TXT_BOLD_ON);
                lista.add(("MANGUERA " + lecturaInicial.getManguera() + ": ").getBytes());
                lista.add((lecturaInicial.getFamiliaDescripcion().toUpperCase() + "\r\n").getBytes());
                lista.add((lecturaInicial.getProductoDescripcion() + "\r\n").getBytes());
                lista.add(TCPPrinterService.TXT_BOLD_OFF);
                lista.add(("LEC. VOL INIC   :   \t\t" + Utils.calculeCantidad(lecturaInicial.getTotalizadorVolumen(),
                        NovusConstante.FACTOR_INVENTARIO) + "\r\n").getBytes());
                lista.add(("LEC. VOL FINAL  : \t\t"
                        + Utils.calculeCantidad(lecturaFinal.getTotalizadorVolumen(), NovusConstante.FACTOR_INVENTARIO)
                        + "\r\n").getBytes());
                lista.add(("LEC. VENTA INIC :   \t\t" + lecturaInicial.getTotalizadorVenta() + "\r\n").getBytes());
                lista.add(("LEC. VENTA FINAL: \t\t" + lecturaFinal.getTotalizadorVenta() + "\r\n").getBytes());
                lista.add(("DIF LEC VOL     : \t\t" + Utils.calculeCantidad(
                        (lecturaFinal.getTotalizadorVolumen() - lecturaInicial.getTotalizadorVolumen()),
                        NovusConstante.FACTOR_INVENTARIO) + "\r\n").getBytes());
                lista.add(("DIF LEC VENTAS  : \t\t"
                        + (lecturaFinal.getTotalizadorVenta() - lecturaInicial.getTotalizadorVenta()) + "\r\n")
                        .getBytes());
                totalDiferenciasLecturasVolumen += (lecturaFinal.getTotalizadorVolumen()
                        - lecturaInicial.getTotalizadorVolumen());
                totalDiferenciasLecturasVentas += (lecturaFinal.getTotalizadorVenta()
                        - lecturaInicial.getTotalizadorVenta());
                if ((j + 1) < jornada.getLecturasIniciales().size()) {
                    if (surtidor != jornada.getLecturasIniciales().get(j + 1).getSurtidor()) {
                        lista.add(("\r\n").getBytes());
                        lista.add(printChar(FULL_PAGE, "-").getBytes());
                        lista.add(("TOTAL DIF VOL    : \t\t" + Utils.calculeCantidad(totalDiferenciasLecturasVolumen,
                                NovusConstante.FACTOR_INVENTARIO) + "\r\n").getBytes());
                        lista.add(("TOTAL DIF VENTAS : \t\t"
                                + (totalDiferenciasLecturasVentas) + "\r\n").getBytes());
                        lista.add(("\r\n").getBytes());
                        if (!jornada.getMediosxsurtido().isEmpty()) {
                            lista.add(TCPPrinterService.TXT_BOLD_ON);
                            lista.add(("MEDIOS PAGO X SURTIDOR: " + surtidor + "\r\n").getBytes());
                            lista.add(NovusUtils.str_pad("DESCRIPCION:", 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                            lista.add(NovusUtils.str_pad("CANT:", 14, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                            lista.add(NovusUtils.str_pad("TOTAL:", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                            lista.add(TCPPrinterService.TXT_BOLD_OFF);
                            for (Map.Entry<Long, ArrayList<MediosPagosBean>> m : jornada.getMediosxsurtido()
                                    .entrySet()) {
                                if (m.getKey() == surtidor) {
                                    ArrayList<MediosPagosBean> medios = m.getValue();
                                    for (MediosPagosBean medio : medios) {
                                        lista.add(NovusUtils
                                                .str_pad(medio.getDescripcion(), 23, " ", NovusUtils.STR_PAD_RIGHT)
                                                .getBytes());
                                        lista.add(NovusUtils
                                                .str_pad(medio.getCantidad() + "", 14, " ", NovusUtils.STR_PAD_RIGHT)
                                                .getBytes());
                                        lista.add(NovusUtils.str_pad("$" + df.format(medio.getValor()), 11, " ",
                                                NovusUtils.STR_PAD_RIGHT).getBytes());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    lista.add(("\r\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(("TOTAL DIF VOL    : \t\t"
                            + Utils.calculeCantidad(totalDiferenciasLecturasVolumen, NovusConstante.FACTOR_INVENTARIO)
                            + "\r\n").getBytes());
                    lista.add(("TOTAL DIF VENTAS : \t\t" + (totalDiferenciasLecturasVentas) + "\r\n").getBytes());
                    lista.add(("\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(("MEDIOS PAGO X SURTIDOR: " + surtidor + "\r\n").getBytes());
                    lista.add(NovusUtils.str_pad("DESCRIPCION:", 23, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad("CANT:", 14, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(NovusUtils.str_pad("TOTAL:", 11, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    for (Map.Entry<Long, ArrayList<MediosPagosBean>> m : jornada.getMediosxsurtido().entrySet()) {
                        if (m.getKey() == surtidor) {
                            ArrayList<MediosPagosBean> medios = m.getValue();
                            for (MediosPagosBean medio : medios) {
                                lista.add(NovusUtils.str_pad(medio.getDescripcion(), 23, " ", NovusUtils.STR_PAD_RIGHT)
                                        .getBytes());
                                lista.add(
                                        NovusUtils.str_pad(medio.getCantidad() + "", 14, " ", NovusUtils.STR_PAD_RIGHT)
                                                .getBytes());
                                lista.add(NovusUtils
                                        .str_pad("$" + df.format(medio.getValor()), 11, " ", NovusUtils.STR_PAD_RIGHT)
                                        .getBytes());
                            }
                        }
                    }
                }

            }
            if (detalles) {

                if (!jornada.getVentasCanastilla().isEmpty()) {
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(("DETALLES  VENTAS CANASTILLA" + " \r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_ALIGN_LT);
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }

                int con = 0;
                for (MovimientosBean movimiento : jornada.getVentasCanastilla()) {
                    if (con > 0) {
                        lista.add(printChar(FULL_PAGE, "-").getBytes());
                    }
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("CONSECUTIVO:", 15, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    ConsecutivoBean consecutivo = movimiento.getConsecutivo();
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add(((consecutivo.getPrefijo() + consecutivo.getConsecutivo_actual()) + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("FECHA VENTA:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add((NovusUtils.str_pad(getFecha(movimiento.getFecha()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("CLIENTE:", 8, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add((NovusUtils.str_pad(movimiento.getClienteNombre(), 15, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("NIT:", 5, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add((NovusUtils.str_pad(movimiento.getClienteNit(), 10, " ", NovusUtils.STR_PAD_RIGHT))
                            .getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(("\n").getBytes());
                    lista.add((NovusUtils.str_pad("PLU", 4, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("PRODUCTO", 16, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("CANT", 5, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("VUNT", 9, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add((NovusUtils.str_pad("TOTAL", 12, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                    lista.add(("\n").getBytes());
                    int TAMANO_DESCRIPCION = 15;
                    float impoConsumo = 0;
                    for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                        Long key = entry.getKey();
                        MovimientosDetallesBean value = entry.getValue();
                        String imp = "E";
                        if (value.getImpuestos() != null) {
                            for (ImpuestosBean impuesto : value.getImpuestos()) {
                                if (impuesto.getPorcentaje_valor().equals("%")) {
                                    imp = impuesto.getCodigo();
                                } else {
                                    impoConsumo += impuesto.getValor() * value.getCantidad();
                                }
                            }
                        }
                        String descripcion = value.getDescripcion();
                        if (descripcion.length() >= TAMANO_DESCRIPCION) {
                            descripcion = value.getDescripcion().substring(0, TAMANO_DESCRIPCION - 1);
                        }
                        descripcion = limpiar(descripcion);
                        lista.add(printProducto2(TAMANO_DESCRIPCION, value.getPlu() != null ? value.getPlu() : "",
                                descripcion, value.getCantidad(), value.getPrecio(),
                                value.getPrecio() * value.getCantidadUnidad(), " " + imp).getBytes());
                    }
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("IMPUESTO:", 10, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add(NovusUtils
                            .str_pad("$" + df.format(movimiento.getImpuestoTotal()), 12, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add(NovusUtils.str_pad("VALOR TOTAL:", 12, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                    lista.add(TCPPrinterService.TXT_BOLD_OFF);
                    lista.add(NovusUtils
                            .str_pad("$" + df.format(movimiento.getVentaTotal()), 13, " ", NovusUtils.STR_PAD_RIGHT)
                            .getBytes());
                    lista.add(("\n").getBytes());
                    lista.add(printChar(FULL_PAGE, "-").getBytes());
                }
            }

            LinkedHashSet<MovimientosBean> datos = new LinkedHashSet<>();
            MovimientosDao dao = new MovimientosDao();
            long totalConsignaciones = 0;
            try {
                datos = dao.getSobresRealizados(persona.getId(), jornada.getGrupoJornada());
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
            for (MovimientosBean sob : datos) {
                totalConsignaciones += sob.getVentaTotal();
            }
            if (!datos.isEmpty()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("CONSIGNACION DE SOBRES" + " \r\n").getBytes());

                lista.add(TCPPrinterService.TXT_ALIGN_LT);
                lista.add(printChar(FULL_PAGE, "-").getBytes());

                lista.add(NovusUtils.str_pad("NUMERO DE SOBRES:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add((NovusUtils.str_pad(datos.size() + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
                lista.add(NovusUtils.str_pad("TOTAL CONSIGNACIONES:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
                lista.add((NovusUtils.str_pad("$" + df.format(totalConsignaciones), 23, " ", NovusUtils.STR_PAD_RIGHT))
                        .getBytes());
            }

            if (!persona.isPrincipal()) {
                lista.add(printChar(FULL_PAGE, "-").getBytes());
                lista.add(TCPPrinterService.TXT_ALIGN_CT);
                lista.add(("LA JORNADA FUE ABIERTA POR OTRO PROMOTOR \r\n").getBytes());
            }

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                if (logview) {
                    showViewPrint(lista);
                }
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));

            }
        } catch (IOException | PrintException | DAOException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void showViewPrint(ArrayList<byte[]> list) {
        String mensajePrint = "";
        for (byte[] byteArray : list) {
            mensajePrint += new String(byteArray);
        }
        JPanel panel = new JPanel();
        panel.setVisible(true);
        panel.setBounds(0, 0, 1024, 600);
        JLabel label = new JLabel();
        label.setBounds(0, 0, 1000, 600);
        panel.add(label);
        label.setText(mensajePrint);
    }

    public void printSobres(MovimientosBean sobre, JsonObject jsonReporteSobre, PersonaBean persona) {
        DescriptorBean descrp = null;
        MovimientosDao dao = new MovimientosDao();
        LinkedHashSet<MovimientosBean> datos = new LinkedHashSet<>();

        int cantidadSobres = jsonReporteSobre.get("cantidadSobresAcumulados").getAsInt();
        int consecutivo = jsonReporteSobre.get("consecutivoActual").getAsInt();
        float totalAcumuladoSobre = jsonReporteSobre.get("totalSobresAcumulados").getAsFloat();

        try {
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(("CONSIGNACION DE SOBRES\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("FECHA CONSIGNACION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(sobre.getFecha()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("PROMOTOR:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(persona.getNombre(), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("TURNO:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(
                    (NovusUtils.str_pad(sobre.getGrupoJornadaId() + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("DETALLES DEL SOBRES\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("NUMERO SOBRE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(consecutivo + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("TOTAL SOBRE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("$" + df.format(sobre.getVentaTotal()), 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("CANT. SOBRES ACUMULADOS:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad((cantidadSobres + 1) + "", 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("TOTAL SOBRES ACUMULADOS:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("$" + df.format((totalAcumuladoSobre + sobre.getVentaTotal())), 23, " ",
                    NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());
            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void printSobres(JsonObject jsonReporteSobre, PersonaBean persona) {

        DescriptorBean descrp = null;
        MovimientosDao dao = new MovimientosDao();
        LinkedHashSet<MovimientosBean> datos = new LinkedHashSet<>();

        try {
            ArrayList<byte[]> lista = new ArrayList<>();
            int FULL_PAGE = 48;
            if (descrp != null) {
                if (descrp.getHeader() != null && !descrp.getHeader().trim().equals("")) {
                    lista.add(TCPPrinterService.TXT_ALIGN_CT);
                    lista.add(TCPPrinterService.TXT_2HEIGHT);
                    lista.add(TCPPrinterService.TXT_FONT_D);
                    lista.add(TCPPrinterService.TXT_BOLD_ON);
                    lista.add((Main.credencial.getEmpresa().getAlias() + "\r\n").getBytes());
                    lista.add(TCPPrinterService.TXT_FONT_A);
                    lista.add(TCPPrinterService.TXT_NORMAL);
                }
            }
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_FONT_A);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((Main.credencial.getEmpresa().getRazonSocial() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(("NIT: " + Main.credencial.getEmpresa().getNit() + "\r\n").getBytes());
            lista.add(("DIR: " + Main.credencial.getEmpresa().getDireccionPrincipal() + "\r\n").getBytes());
            lista.add(("TEL: " + Main.credencial.getEmpresa().getTelefonoPrincipal() + "\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(("CONSIGNACION DE SOBRES\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
            SimpleDateFormat sdfISO2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
            lista.add(NovusUtils.str_pad("FECHA IMPRESION:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(new Date()), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("PROMOTOR:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(persona.getNombre(), 23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("TURNO:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(persona.getGrupoJornadaId() + "", 23, " ", NovusUtils.STR_PAD_RIGHT))
                    .getBytes());
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(("DETALLES DE SOBRES \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);
            lista.add(printChar(FULL_PAGE, "-").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("NUMERO SOBRE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("" + jsonReporteSobre.get("numeroSobre").getAsInt(), 23, " ",
                    NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("FECHA SOBRE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad(getFecha(sdfISO2.parse(jsonReporteSobre.get("fechaSobre").getAsString())),
                    23, " ", NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(NovusUtils.str_pad("TOTAL SOBRE:", 25, " ", NovusUtils.STR_PAD_RIGHT).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add((NovusUtils.str_pad("$" + df.format(jsonReporteSobre.get("totalSobre").getAsFloat()), 23, " ",
                    NovusUtils.STR_PAD_RIGHT)).getBytes());
            lista.add(("\r\n").getBytes());

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }
        } catch (IOException | PrintException | ParseException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printFormato(String dato) {

        ArrayList<byte[]> lista = new ArrayList<>();
        lista.add(dato.getBytes());

        try {

            if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                    .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
                TCPPrinterService service = new TCPPrinterService();
                service.printBytes(lista);
            } else {
                QTEPrinterService service = new QTEPrinterService();
                service.printQuote(lista, Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA));
            }

        } catch (IOException | PrintException ex) {
            Logger.getLogger(PrinterFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String getFecha(Date fecha) {
        NovusUtils.printLn(xsdf.format(fecha));
        String sfecha = xsdf.format(fecha);
        sfecha = sfecha.replaceAll("p. m.", "p.m.");
        sfecha = sfecha.replaceAll("a. m.", "a.m.");
        return sfecha;
    }
}

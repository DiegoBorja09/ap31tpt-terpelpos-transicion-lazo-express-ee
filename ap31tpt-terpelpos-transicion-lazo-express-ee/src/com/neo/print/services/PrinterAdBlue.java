/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.neo.print.services;

import com.application.useCases.jornadas.ObtenerJornadaIdUseCase;

import com.bean.EmpresaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.controllers.NumeroLetras;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.firefuel.Utils;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintException;

/**
 *
 * @author Devitech
 */
public class PrinterAdBlue {

    SimpleDateFormat xsdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat xsdfFecha = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    SimpleDateFormat xsdfFechaFull = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    DecimalFormat dff = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    MovimientosDao mado = new MovimientosDao();

    static final int FULL_PAGE = 48;

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

    private String getCopyright() {
        String copyright = "";
        copyright += new String(TCPPrinterService.TXT_BOLD_ON);
        copyright += new String(TCPPrinterService.TXT_ALIGN_LT);
        copyright += "\r\nFABRICANTE DE SOFTWARE DEVICES AND TECHNOLOGY SAS.\r\n";
        copyright += new String(TCPPrinterService.TXT_BOLD_OFF);
        copyright += "NIT: 900130563-7\n";
        return copyright;
    }

    public void printeraDblue(JsonObject data) {
        try {
            EquipoDao dao = new EquipoDao();
            EmpresaBean empresa;
            empresa = dao.findEmpresa(Main.credencial);
            int FULL_PAGE = 48;
            ArrayList<byte[]> lista = new ArrayList<>();
            lista.add(TCPPrinterService.TXT_2HEIGHT);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add(TCPPrinterService.TXT_ALIGN_CT);
            lista.add(TCPPrinterService.TXT_NORMAL);
            lista.add(TCPPrinterService.TXT_BOLD_ON);
            lista.add((empresa.getAlias() + "\r\n").getBytes());
            lista.add((empresa.getRazonSocial() + " \r\n").getBytes());
            lista.add(("NIT: " + empresa.getNit() + "      TEL: " + empresa.getTelefonoPrincipal() + " \r\n").getBytes());
            lista.add((empresa.getDireccionPrincipal() + " \r\n").getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);
            lista.add(TCPPrinterService.TXT_ALIGN_LT);

//            lista.add(Utils.text_between("REMISION NRO:", "10").getBytes());
            lista.add(Utils.text_between("FECHA:", LocalDate.now().toString()).getBytes());
            lista.add(Utils.text_between("TURNO:", new ObtenerJornadaIdUseCase().execute() + "").getBytes());
            lista.add(Utils.text_between("POMOTOR:", data.get("promotor").getAsString()).getBytes());
            lista.add(("ISLA: " + data.get("islas").getAsString()
                    + "        " + "POS: " + data.get("islas").getAsString()
                    + "        " + " CARA: 0        MANGUERA: 0").getBytes());
            addLine(lista);
            lista.add(Utils.text_between("PRODUCTO:", data.get("familiaDesc").getAsString()).getBytes());
            lista.add(Utils.text_between("PRECIO:", data.get("precio").getAsString()).getBytes());
            lista.add(Utils.text_between("CANTIDAD:", data.get("cantidad").getAsFloat() + " LITROS").getBytes());
            lista.add(Utils.text_between("DESCUENTO:", "$ 0").getBytes());
            lista.add(Utils.text_between("TOTAL:", "$ " + data.get("total").getAsFloat()).getBytes());
            addLine(lista);
            NumeroLetras letras = new NumeroLetras();
            lista.add((letras.convertir(data.get("total").getAsFloat()+"", true)).getBytes());
            String identificaion = "IDENTIFICACION:";
            lista.add(Utils.text_between("CLIENTE:", "").getBytes());
            lista.add(Utils.text_between(identificaion, data.get("identificacion").getAsString()).getBytes());
            lista.add(Utils.text_between("SERIAL:", "").getBytes());
            lista.add(Utils.text_between("MEDIO:", "").getBytes());
            lista.add(Utils.text_between("PLACA:", data.get("placa").getAsString()).getBytes());
            lista.add(Utils.text_between("N. INTERNO:", "").getBytes());
            lista.add(Utils.text_between("KILOMETRAJE:", "0").getBytes());

            lista.add(TCPPrinterService.TXT_BOLD_ON);
            String[] cols = {"MEDIOS DE PAGO", "AUTORIZACION", "TOTAL"};
            int[] colsSize = {21, 21, 22};
            lista.add(NovusUtils.format_cols(cols, colsSize).getBytes());
            lista.add(TCPPrinterService.TXT_BOLD_OFF);

            cols[0] = "CREDITOS";
            cols[1] = "RUMBO";
            cols[2] = "$ "+data.get("total").getAsFloat();

            lista.add(Utils.format_cols(cols, colsSize).getBytes());
            lista.add(("\r\n").getBytes());

            lista.add(("FIRMA: _________________________________________________" + " \r\n").getBytes());
            lista.add(("NOMBRE: " + " \r\n").getBytes());

            lista.add(("\r\n").getBytes());
            lista.add((getCopyright()).getBytes());
            for (byte[] bs : lista) {
                NovusUtils.printLn(new String(bs));
            }
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

        } catch (DAOException ex) {
            Logger.getLogger(PrinterAdBlue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

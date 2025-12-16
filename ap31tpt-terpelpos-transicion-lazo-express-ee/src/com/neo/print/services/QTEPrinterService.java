/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neo.print.services;

import com.controllers.NovusUtils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author ASUS-PC
 */
public class QTEPrinterService {

    public final byte[] NULL = { 0x00 }; // Print and line feed
    public final byte[] CTL_LF = { 0x0a }; // Print and line feed
    public static final byte[] CAN_HT = { 0x1b, 0x44, 0x00 }; // Cancel Horizontal Tab
    public static final byte[] TAB_H = { 0x09 }; // Horizontal Tab
    public static final byte[] LINE_SPACE_24 = { 0x1b, 0x33, 24 }; // Set the line spacing at 24
    public static final byte[] LINE_SPACE_30 = { 0x1b, 0x33, 30 }; // Set the line spacing at 30
    // Image
    public static final byte[] SELECT_BIT_IMAGE_MODE = { 0x1B, 0x2A, 33 };
    // Printer hardware
    public static final byte[] HW_INIT = { 0x1b, 0x40 }; // Clear data in buffer and reset modes
    // Cash Drawer
    public static final byte[] CD_KICK_2 = { 0x1b, 0x70, 0x00 }; // Sends a pulse to pin 2 []
    public static final byte[] CD_KICK_5 = { 0x1b, 0x70, 0x01 }; // Sends a pulse to pin 5 []
    // Paper
    public static final byte[] PAPER_FULL_CUT = { 0x1d, 0x56, 0x00 }; // Full cut paper
    public static final byte[] PAPER_PART_CUT = { 0x1d, 0x56, 0x01 }; // Partial cut paper
    // Text format
    public static final byte[] TXT_NORMAL = { 0x1b, 0x21, 0x00 }; // Normal text
    public static final byte[] TXT_2HEIGHT = { 0x1b, 0x21, 0x10 }; // Double height text
    public static final byte[] TXT_2WIDTH = { 0x1b, 0x21, 0x20 }; // Double width text
    public static final byte[] TXT_4SQUARE = { 0x1b, 0x21, 0x30 }; // Quad area text
    public static final byte[] TXT_UNDERL_OFF = { 0x1b, 0x2d, 0x00 }; // Underline font OFF
    public static final byte[] TXT_UNDERL_ON = { 0x1b, 0x2d, 0x01 }; // Underline font 1-dot ON
    public static final byte[] TXT_UNDERL2_ON = { 0x1b, 0x2d, 0x02 }; // Underline font 2-dot ON
    public static final byte[] TXT_BOLD_OFF = { 0x1b, 0x45, 0x00 }; // Bold font OFF
    public static final byte[] TXT_BOLD_ON = { 0x1b, 0x45, 0x01 }; // Bold font ON

    public static final byte[] CHARACTER_SET_SPANISH = { 0x1B, 0x52, 0x12 }; // Print and line feed
    public static final byte[] CHARACTER_SET_SPANISH_I = { 0x1B, 0x52, 0x07 }; // Print and line feed
    public static final byte[] CHARACTER_SET_SPANISH_II = { 0x1B, 0x52, 0x11 }; // Print and line feed

    public static final byte[] TXT_FONT_A = { 0x1b, 0x4d, 0x00 }; // Font type A
    public static final byte[] TXT_FONT_B = { 0x1b, 0x4d, 0x01 }; // Font type B
    public static final byte[] TXT_FONT_C = { 0x1b, 0x4d, 0x02 }; // Font type C
    public static final byte[] TXT_FONT_D = { 0x1b, 0x4d, 0x03 }; // Font type D
    public static final byte[] TXT_FONT_E = { 0x1b, 0x4d, 0x04 }; // Font type E

    public static final byte[] TXT_ALIGN_LT = { 0x1b, 0x61, 0x00 }; // Left justification
    public static final byte[] TXT_ALIGN_CT = { 0x1b, 0x61, 0x01 }; // Centering
    public static final byte[] TXT_ALIGN_RT = { 0x1b, 0x61, 0x02 }; // Right justification
    public static final byte[] LEFT_MARGIN = { 0x1b, 0x6c, 0x08 }; // Left Margin

    private static final byte[] BARCODE_TXT_OFF = { 0x1d, 0x48, 0x00 }; // HRI printBarcode chars OFF
    private static final byte[] BARCODE_TXT_ABV = { 0x1d, 0x48, 0x01 }; // HRI printBarcode chars above
    private static final byte[] BARCODE_TXT_BLW = { 0x1d, 0x48, 0x02 }; // HRI printBarcode chars below
    private static final byte[] BARCODE_TXT_BTH = { 0x1d, 0x48, 0x03 }; // HRI printBarcode chars both above and below
    private static final byte[] BARCODE_FONT_A = { 0x1d, 0x66, 0x00 }; // Font type A for HRI printBarcode chars
    private static final byte[] BARCODE_FONT_B = { 0x1d, 0x66, 0x01 }; // Font type B for HRI printBarcode chars
    private static final byte[] BARCODE_HEIGHT = { 0x1d, 0x68, 0x64 }; // Barcode Height [1-255]
    private static final byte[] BARCODE_WIDTH = { 0x1d, 0x77, 0x03 }; // Barcode Width [2-6]
    private static final byte[] BARCODE_UPC_A = { 0x1d, 0x6b, 0x00 }; // Barcode type UPC-A
    private static final byte[] BARCODE_UPC_E = { 0x1d, 0x6b, 0x01 }; // Barcode type UPC-E
    private static final byte[] BARCODE_EAN13 = { 0x1d, 0x6b, 0x02 }; // Barcode type EAN13
    private static final byte[] BARCODE_EAN8 = { 0x1d, 0x6b, 0x03 }; // Barcode type EAN8
    private static final byte[] BARCODE_CODE39 = { 0x1d, 0x6b, 0x04 }; // Barcode type CODE39
    private static final byte[] BARCODE_ITF = { 0x1d, 0x6b, 0x05 }; // Barcode type ITF
    private static final byte[] BARCODE_NW7 = { 0x1d, 0x6b, 0x06 }; // Barcode type NW7
    // private static final byte[] BARCODE_CODE128 = {0x1d, 0x6b, 73, 5, 123, 67,
    // 12, 34, 56}; // Barcode type 128
    private static final byte[] BARCODE_CODE128_A = { 0x1d, 0x6b, 73 }; // Barcode type 128
    private static final byte[] BARCODE_CODE128_B = { 123, 65 }; // Barcode type 128

    protected void printSpace(BufferedOutputStream osw) {
        for (int i = 0; i < 7; i++) {
            try {
                osw.write("\r\n".getBytes());
            } catch (IOException ex) {
                Logger.getLogger(QTEPrinterService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void barcode(ArrayList<byte[]> lista, String code, String bc, int width, int height, String pos, String font)
            throws Exception {

        lista.add(TXT_ALIGN_CT);
        if (height >= 2 || height <= 6) {
            lista.add(BARCODE_HEIGHT);
        }
        if (width >= 1 || width <= 255) {
            lista.add(BARCODE_WIDTH);
        }
        if (font.equalsIgnoreCase("B")) {
            lista.add(BARCODE_FONT_B);
        } else {
            lista.add(BARCODE_FONT_A);
        }

        if (pos.equalsIgnoreCase("OFF")) {
            lista.add(BARCODE_TXT_OFF);
        } else if (pos.equalsIgnoreCase("BOTH")) {
            lista.add(BARCODE_TXT_BTH);
        } else if (pos.equalsIgnoreCase("ABOVE")) {
            lista.add(BARCODE_TXT_ABV);
        } else {
            lista.add(BARCODE_TXT_BLW);
        }

        switch (bc.toUpperCase()) {
            case "UPC-A":
                lista.add(BARCODE_UPC_A);
                break;
            case "UPC-E":
                lista.add(BARCODE_UPC_E);
                break;
            case "EAN13":
                lista.add(BARCODE_EAN13);
                break;
            case "EAN8":
                lista.add(BARCODE_EAN8);
                break;
            case "CODE39":
                lista.add(BARCODE_CODE39);
                break;
            case "ITF":
                lista.add(BARCODE_ITF);
                break;
            case "NW7":
                lista.add(BARCODE_NW7);
                break;
            case "CODE128":
                lista.add(BARCODE_CODE128_A);

                byte[] LONGITUD = new byte[1];
                LONGITUD[0] = (byte) (code.getBytes().length + BARCODE_CODE128_B.length);
                lista.add(LONGITUD);
                lista.add(BARCODE_CODE128_B);

                // byte[] NUMERO = new byte[]{87,38,37,7};
                byte[] NUMERO = code.getBytes();

                lista.add(NUMERO);
                break;
            default:
                break;

        }

        if (!code.equals("")) {
            // lista.add((code).getBytes());
            lista.add(NULL);
        }

        /*
         * Socket clientSocket = new Socket();
         * clientSocket.connect(new InetSocketAddress(Butter.IP_IMPRESORA,
         * Butter.PORT_IMPRESORA), 5000);
         * try (BufferedOutputStream bus = new
         * BufferedOutputStream(clientSocket.getOutputStream())) {
         * 
         * bus.write(TXT_ALIGN_CT);
         * // Height
         * if (height >= 2 || height <= 6) {
         * bus.write(BARCODE_HEIGHT);
         * } else {
         * NovusUtils.printLn("Incorrect Height");
         * }
         * //Width
         * if (width >= 1 || width <= 255) {
         * bus.write(BARCODE_WIDTH);
         * } else {
         * NovusUtils.printLn("Incorrect Width");
         * }
         * //Font
         * if (font.equalsIgnoreCase("B")) {
         * bus.write(BARCODE_FONT_B);
         * } else {
         * bus.write(BARCODE_FONT_A);
         * }
         * //Position
         * if (pos.equalsIgnoreCase("OFF")) {
         * bus.write(BARCODE_TXT_OFF);
         * } else if (pos.equalsIgnoreCase("BOTH")) {
         * bus.write(BARCODE_TXT_BTH);
         * } else if (pos.equalsIgnoreCase("ABOVE")) {
         * bus.write(BARCODE_TXT_ABV);
         * } else {
         * bus.write(BARCODE_TXT_BLW);
         * }
         * //Type
         * switch (bc.toUpperCase()) {
         * case "UPC-A":
         * bus.write(BARCODE_UPC_A);
         * break;
         * case "UPC-E":
         * bus.write(BARCODE_UPC_E);
         * break;
         * default:
         * case "EAN13":
         * bus.write(BARCODE_EAN13);
         * break;
         * case "EAN8":
         * bus.write(BARCODE_EAN8);
         * break;
         * case "CODE39":
         * bus.write(BARCODE_CODE39);
         * break;
         * case "ITF":
         * bus.write(BARCODE_ITF);
         * break;
         * case "NW7":
         * bus.write(BARCODE_NW7);
         * break;
         * }
         * //Print Code
         * if (!code.equals("")) {
         * bus.write(code.getBytes());
         * } else {
         * NovusUtils.printLn("Incorrect Value");
         * }
         * 
         * bus.flush();
         * bus.close();
         * clientSocket.close();
         * }
         */
    }

    public void printQuote(ArrayList<byte[]> array, String impresora) throws PrintException {
        byte[] cutP = new byte[] { 0x1b, 'i', 1 };
        array.add(cutP);
        if (impresora != null) {
            NovusUtils.printLn("IMPRIMIENDO EN " + impresora);
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            int selectedService = 0;
            int index = 0;
            for (PrintService service : services) {
                NovusUtils.printLn("impresora: " + service.getName());
                if (service.getName().toUpperCase().contains(impresora.toUpperCase())) {
                    selectedService = index;
                    break;
                }
                index++;
            }
            PrintService printService = services[selectedService];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            for (byte[] bs : array) {
                try {
                    out.write(bs);
                } catch (IOException ex) {
                    Logger.getLogger(QTEPrinterService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            byte[] bytes = baos.toByteArray();

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            DocPrintJob docPrintJob = printService.createPrintJob();
            Doc doc = new SimpleDoc(bytes, flavor, null);
            try {
                docPrintJob.print(doc, null);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
    }

}

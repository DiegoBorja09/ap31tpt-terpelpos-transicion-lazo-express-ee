/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neo.print.services;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.escpos.EscPos;
import com.escpos.EscPosConst;
import com.escpos.image.BitonalThreshold;
import com.escpos.image.CoffeeImageImpl;
import com.escpos.image.EscPosImage;
import com.escpos.image.RasterBitImageWrapper;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.print.PrintException;

/**
 *
 * @author ASUS-PC
 */
public class TCPPrinterService {

    public final byte[] CTL_LF = {0x0a}; // Print and line feed
    public static final byte[] CAN_HT = {0x1b, 0x44, 0x00}; // Cancel Horizontal Tab
    public static final byte[] TAB_H = {0x09}; // Horizontal Tab
    public static final byte[] LINE_SPACE_24 = {0x1b, 0x33, 24}; // Set the line spacing at 24
    public static final byte[] LINE_SPACE_30 = {0x1b, 0x33, 30}; // Set the line spacing at 30
    // Image
    public static final byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33};
    // Printer hardware
    public static final byte[] HW_INIT = {0x1b, 0x40}; // Clear data in buffer and reset modes
    // Cash Drawer
    public static final byte[] CD_KICK_2 = {0x1b, 0x70, 0x00}; // Sends a pulse to pin 2 []
    public static final byte[] CD_KICK_5 = {0x1b, 0x70, 0x01}; // Sends a pulse to pin 5 []
    // Paper
    public static final byte[] PAPER_FULL_CUT = {0x1d, 0x56, 0x00}; // Full cut paper
    public static final byte[] PAPER_PART_CUT = {0x1d, 0x56, 0x01}; // Partial cut paper
    // Text format
    public static final byte[] TXT_NORMAL = {0x1b, 0x21, 0x00}; // Normal text
    public static final byte[] TXT_2HEIGHT = {0x1b, 0x21, 0x10}; // Double height text
    public static final byte[] TXT_2WIDTH = {0x1b, 0x21, 0x20}; // Double width text
    public static final byte[] TXT_4SQUARE = {0x1b, 0x21, 0x30}; // Quad area text
    public static final byte[] TXT_UNDERL_OFF = {0x1b, 0x2d, 0x00}; // Underline font OFF
    public static final byte[] TXT_UNDERL_ON = {0x1b, 0x2d, 0x01}; // Underline font 1-dot ON
    public static final byte[] TXT_UNDERL2_ON = {0x1b, 0x2d, 0x02}; // Underline font 2-dot ON
    public static final byte[] TXT_BOLD_OFF = {0x1b, 0x45, 0x00}; // Bold font OFF
    public static final byte[] TXT_BOLD_ON = {0x1b, 0x45, 0x01}; // Bold font ON
    public static final byte[] TXT_FONT_A = {0x1b, 0x4d, 0x00}; // Font type A
    public static final byte[] TXT_FONT_B = {0x1b, 0x4d, 0x01}; // Font type B
    public static final byte[] TXT_FONT_C = {0x1b, 0x4d, 0x02}; // Font type C
    public static final byte[] TXT_FONT_D = {0x1b, 0x4d, 0x03}; // Font type D
    public static final byte[] TXT_FONT_E = {0x1b, 0x4d, 0x04}; // Font type E
    public static final byte[] TXT_ALIGN_LT = {0x1b, 0x61, 0x00}; // Left justification
    public static final byte[] TXT_ALIGN_CT = {0x1b, 0x61, 0x01}; // Centering
    public static final byte[] TXT_ALIGN_RT = {0x1b, 0x61, 0x02}; // Right justification
    public static final byte[] LEFT_MARGIN = {0x1b, 0x6c, 0x08}; // Left Margin

    // Char code table
    public static final byte[] CHARCODE_PC437 = {0x1b, 0x74, 0x00}; // USA Standard Europe
    public static final byte[] CHARCODE_JIS = {0x1b, 0x74, 0x01}; // Japanese Katakana
    public static final byte[] CHARCODE_PC850 = {0x1b, 0x74, 0x02}; // Multilingual
    public static final byte[] CHARCODE_PC860 = {0x1b, 0x74, 0x03}; // Portuguese
    public static final byte[] CHARCODE_PC863 = {0x1b, 0x74, 0x04}; // Canadian-French
    public static final byte[] CHARCODE_PC865 = {0x1b, 0x74, 0x05}; // Nordic
    public static final byte[] CHARCODE_WEU = {0x1b, 0x74, 0x06}; // Simplified Kanji, Hirakana
    public static final byte[] CHARCODE_GREEK = {0x1b, 0x74, 0x07}; // Simplified Kanji
    public static final byte[] CHARCODE_HEBREW = {0x1b, 0x74, 0x08}; // Simplified Kanji
    public static final byte[] CHARCODE_PC1252 = {0x1b, 0x74, 0x10}; // Western European Windows Code Set
    public static final byte[] CHARCODE_PC866 = {0x1b, 0x74, 0x12}; // Cirillic //2
    public static final byte[] CHARCODE_PC852 = {0x1b, 0x74, 0x13}; // Latin 2
    public static final byte[] CHARCODE_PC858 = {0x1b, 0x74, 0x14}; // Euro
    public static final byte[] CHARCODE_THAI42 = {0x1b, 0x74, 0x15}; // Thai character code 42
    public static final byte[] CHARCODE_THAI11 = {0x1b, 0x74, 0x16}; // Thai character code 11
    public static final byte[] CHARCODE_THAI13 = {0x1b, 0x74, 0x17}; // Thai character code 13
    public static final byte[] CHARCODE_THAI14 = {0x1b, 0x74, 0x18}; // Thai character code 14
    public static final byte[] CHARCODE_THAI16 = {0x1b, 0x74, 0x19}; // Thai character code 16
    public static final byte[] CHARCODE_THAI17 = {0x1b, 0x74, 0x1a}; // Thai character code 17
    public static final byte[] CHARCODE_THAI18 = {0x1b, 0x74, 0x1b}; // Thai character code 18

    public static final byte[] CHARCODE_SPANISH = {0x1b, 0x74, 0x1b}; // Thai character code 18

    public static final byte[] ABRIR_CAJON = {0x1b, 0x70, 0x00, 0x19, (byte) 0xFA}; // Thai character code 18

    public static final byte[][] SPECIAL_BYTEARRAY = {CAN_HT, TAB_H, LINE_SPACE_24};

    protected void printBytes(ArrayList<byte[]> array) throws UnknownHostException, IOException, PrintException {
        String impresora = Main.getParametroCore(NovusConstante.PREFERENCE_IP_IMPRESORA, true);
        try {
            byte[] cutP = new byte[]{0x1b, 'i', 1};

            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(impresora, NovusConstante.PORT_IMPRESORA), 5000);
            try (BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream())) {
                try {

                    if (Main.parametros.containsKey(NovusConstante.PREFERENCE_LOGO_IMPRESORA)) {
                        EscPos escpos = new EscPos(bos);
                        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
                        imageWrapper.setJustification(EscPosConst.Justification.Center);
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder()
                                .decode(Main.parametros.get(NovusConstante.PREFERENCE_LOGO_IMPRESORA))));
                        EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(img), new BitonalThreshold());
                        escpos.write(imageWrapper, escposImage);
                    }

                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }

                for (byte[] bs : array) {
                    bos.write(bs);

                }
                printSpace(bos);
                bos.write(cutP);
                bos.flush();
                bos.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error al conectar a la impresosa (" + impresora + ") " + e.getMessage());
        }
    }

    protected void printBytes(String impresora, ArrayList<byte[]> array) throws UnknownHostException, IOException, PrintException {
        try {
            impresora = Main.getParametroCore(NovusConstante.PREFERENCE_IP_IMPRESORA, true);
            byte[] cutP = new byte[]{0x1b, 'i', 1};

            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(impresora, NovusConstante.PORT_IMPRESORA), 5000);
            try (BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream())) {
                try {

                    if (Main.parametros.containsKey(NovusConstante.PREFERENCE_LOGO_IMPRESORA)) {
                        EscPos escpos = new EscPos(bos);
                        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
                        imageWrapper.setJustification(EscPosConst.Justification.Center);
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder()
                                .decode(Main.parametros.get(NovusConstante.PREFERENCE_LOGO_IMPRESORA))));
                        EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(img), new BitonalThreshold());
                        escpos.write(imageWrapper, escposImage);
                    }

                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }

                for (byte[] bs : array) {
                    bos.write(bs);

                }
                printSpace(bos);
                bos.write(cutP);
                bos.flush();
                bos.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error al conectar a la impresora (" + impresora + ") " + e.getMessage());
        }
    }

    protected void printSpace(BufferedOutputStream osw) {
        for (int i = 0; i < 3; i++) {
            try {
                osw.write("\r\n".getBytes());
            } catch (IOException ex) {
                Logger.getLogger(TCPPrinterService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

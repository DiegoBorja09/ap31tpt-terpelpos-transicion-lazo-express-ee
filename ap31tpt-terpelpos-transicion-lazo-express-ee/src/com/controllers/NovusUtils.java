/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controllers;

import com.WT2.commons.domain.valueObject.LetterCase;
import com.bean.MediosPagosBean;
import com.bean.Surtidor;
import com.dao.MovimientosDao;
import com.facade.facturacionelectronica.TipoIdentificaion;
import com.firefuel.KCOViewController;
import com.firefuel.Main;
import com.firefuel.Utils;
import com.firefuel.datafonos.ParametrizacionDatafanos;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author usuario
 */
public class NovusUtils {

    public static boolean circuitBreaker = true;
    public static boolean canPlay = true;

    public static File beep = new File(FileSystems.getDefault().getPath("")
            .toAbsolutePath() + "/audio/beep.wav");

    public static File mangueraDescolgadaAudio = new File(FileSystems.getDefault().getPath("")
            .toAbsolutePath() + "/audio/INICIO_VENTA.wav");

    public static File ventaFidelizadaAudio = new File(FileSystems.getDefault().getPath("")
            .toAbsolutePath() + "/audio/HAS_ACUMULADO_VIVE_TERPEL.wav");

    public static File finVentaAudio = new File(FileSystems.getDefault().getPath("")
            .toAbsolutePath() + "/audio/FIN_DE_VENTA.wav");

    public static Font TerpelFontExtraBold_24 = new java.awt.Font("Terpel Sans ExtraBold", Font.BOLD, 24);
    public static Font TerpelFontExtraBold_32 = new java.awt.Font("Terpel Sans ExtraBold", Font.BOLD, 32);
    public static Font TerpelFontExtraBold_36 = new java.awt.Font("Terpel Sans ExtraBold", Font.BOLD, 36);
    public static Font TerpelFontExtraBold_40 = new java.awt.Font("Terpel Sans ExtraBold", Font.BOLD, 40);

    public static final String FORMAT_FULL_DATE = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String TXT_LEFT = "TXT_LEFT";
    public static final String TXT_CENTER = "TXT_CENTER";
    public static final String TXT_RIGHT = "TXT_RIGHT";
    public static final String TXT_BETWEEN = "TXT_BETWEEN";
    public static final String TXT_UNIFORM = "TXT_UNIFORM";

    public static final String PAD_LEFT = "PAD_LEFT";
    public static final String PAD_RIGHT = "PAD_RIGHT";

    public static final int PAGE_SIZE = 64;
    static final Logger logger = Logger.getLogger(NovusUtils.class.getName());

    public static boolean productionServer() {
        boolean valid = true;
        if (!NovusConstante.HOST_CORE_POINT.contains("nodos")
                || !NovusConstante.HOST_CORE_POINT.contains("ws.sclbox.com")
                || !NovusConstante.HOST_CENTRAL_POINT.contains("fullcopy")
                || !NovusConstante.HOST_CENTRAL_POINT.contains("qas")) {
            valid = false;
        }
        return valid;
    }
// Proceso implementado para matar sincronizacion desde el archivo config.properties para toda la sinctonizacion
public static boolean getCircuitBreakerAllStatus(){
    Properties prop = new Properties();
    try{
        InputStream is = new FileInputStream("C://Devitech//pos/config.properties");
        prop.load(is);
        return Boolean.parseBoolean(""+prop.get("circuteBreakerTodo"));
    }catch(Exception e){
        Logger.getLogger("Error en lectura circuteBreaker" + e);
    }
    return true;
}
// Proceso implemantado para matar sincronizacion desde el archivo config.properties
public static boolean  getCircuitBreakerStatus(){
    Properties prop = new Properties();
    try {
        InputStream is = new FileInputStream("C://Devitech//pos/config.properties"); // lee archivo
        prop.load(is);
        return Boolean.parseBoolean(""+prop.get("circuteBreaker")); //parea la variable el estado
    }catch (Exception e){
        Logger.getLogger("Error en lectura circuteBreaker" + e);
    }
    return true;
}

    public static String align(String str, String align) {
        int length = str.length();
        String line;
        int leftSpaces = PAGE_SIZE - length;
        switch (align) {
            case TXT_CENTER:
                leftSpaces /= 2;
                if (PAGE_SIZE % 2 != 0) {
                    if (length % 2 != 0) {
                        line = fill(" ", leftSpaces).concat(str).concat(fill(" ", leftSpaces));
                    } else {
                        line = fill(" ", leftSpaces).concat(str).concat(fill(" ", leftSpaces + 1));
                    }
                } else {
                    if (length % 2 != 0) {
                        line = fill(" ", leftSpaces).concat(str).concat(fill(" ", leftSpaces + 1));
                    } else {
                        line = fill(" ", leftSpaces).concat(str).concat(fill(" ", leftSpaces));
                    }
                }
                break;
            case TXT_RIGHT:
                line = fill(" ", leftSpaces).concat(str);
                break;
            case TXT_LEFT:
                line = str;
                break;
            default:
                line = "";
                break;
        }
        return line;
    }

    public static String text_between(String str1, String str2) {
        int leftSpaces = PAGE_SIZE - (str1.length() + str2.length());
        return str1.concat(fill(" ", leftSpaces).concat(str2));
    }

    public static String fill(String str, int spaces) {
        if (str.length() <= spaces) {
            return str + fill(str, spaces - 1);
        } else {
            return "";
        }
    }

    public static String format_cols(String[] obj, int anchos[]) {
        String line = "";
        int leftSpace;
        int length;
        for (int i = 0; i < obj.length; i++) {
            length = obj[i].length();
            leftSpace = anchos[i] - length;
            if (i == 0) {
                line += obj[i].concat(fill(" ", leftSpace));
            } else {
                line += fill(" ", leftSpace).concat(obj[i]);
            }
        }
        return line;
    }

    public static void WriteLog(String folder, String filename, String text) {
        BufferedWriter output = null;
        try {
            String folderPrincipal = "logs/" + folder;
            if (folder.length() == 0) {
                folderPrincipal = "logs";
            }
            printLn(text);
            File fileFolder = new File(folderPrincipal);
            if (!fileFolder.exists()) {
                fileFolder.mkdir();
            }

            File file = new File(folderPrincipal + "/" + filename);
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
        } catch (IOException e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void WriteLogAppend(String filename, String text) {
        BufferedWriter output = null;
        try {
            File fileFolder = new File("logs");
            if (!fileFolder.exists()) {
                fileFolder.mkdir();
            }

            File file = new File("logs/" + filename);
            output = new BufferedWriter(new FileWriter(file, true));
            output.write(text);
        } catch (IOException e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void printLn(String line) {
        Calendar ini = Calendar.getInstance();
        String dayOfWeek = ini.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String Day = NovusUtils.getFecha(new Date(), NovusUtils.FORMAT_FULL_DATE);
        WriteLogAppend(dayOfWeek + ".log", "[" + Day + "]" + line + "\r\n");
        System.out.println("[" + Day + "]" + line);

    }

    public static void printLnDialog(String Id, String line) {
        Calendar ini = Calendar.getInstance();
        String dayOfWeek = ini.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String Day = NovusUtils.getFecha(new Date(), NovusUtils.FORMAT_FULL_DATE);
        WriteLogAppend(Id + ".dialog." + dayOfWeek + ".log", "[" + Day + "]" + line + "\r\n");
        System.out.println("[" + Day + "]" + line);
    }

    public static void clearLnDialog(String Id) {
        try {
            Calendar ini = Calendar.getInstance();
            String dayOfWeek = ini.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            File file = new File("logs/" + Id + ".dialog." + dayOfWeek + ".log");
            if (file.exists()) {
                cleanUp(file.getAbsoluteFile().toPath());
            }
        } catch (IOException e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    public static void cleanUp(Path path) throws IOException {
        Files.delete(path);
    }

    public static void setMensaje(String mensaje, JLabel jMensajes) {
        jMensajes.setText(
                NovusUtils.convertMessage(
                        LetterCase.FIRST_UPPER_CASE,
                        mensaje));
        jMensajes.setVisible(mensaje.length() > 0);
        if (mensaje.length() > 36) {
            jMensajes.setFont(new java.awt.Font("Terpel Sans", 1, 30));
        } else {
            jMensajes.setFont(new java.awt.Font("Terpel Sans", 1, 24));
        }
    }

    public static String getFecha(Date fecha, String formato) {
        SimpleDateFormat xsdf = new SimpleDateFormat(formato);
        String sfecha = xsdf.format(fecha);
        sfecha = sfecha.replaceAll("p.Â m.", "p.m");
        sfecha = sfecha.replaceAll("a.Â m.", "a.m");
        return sfecha;
    }

    public static String getFecha(String fecha, String formatoFormato, String formato) {
        SimpleDateFormat xsdf = new SimpleDateFormat(formatoFormato);
        Date sfecha = null;
        try {
            sfecha = xsdf.parse(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getFecha(sfecha, formato);
    }

    public static double secondsToMilliseconds(double seconds) {
        final int MILLISECONDS_FACTOR = 1000;
        return MILLISECONDS_FACTOR * seconds;
    }

    public static void mangueraDescolgadaAudio() {
        try {
            if (canPlay) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(mangueraDescolgadaAudio);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                canPlay = false;
                Utils.setTimeout("NovusUtils.mangueraDescolgadaAudio", () -> {
                    while (clip.isRunning()) {
                        canPlay = false;
                    }
                    canPlay = true;
                }, 0);

            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(NovusUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ventaFidelizadaAudio() {
        try {
            if (canPlay) {

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(ventaFidelizadaAudio);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                canPlay = false;
                Utils.setTimeout("NovusUtils.ventaFidelizadaAudio", () -> {
                    while (clip.isRunning()) {
                        canPlay = false;
                    }
                    canPlay = true;
                }, 0);

            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(NovusUtils.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public static void finVentaAudio() {
        try {
            if (canPlay) {

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(finVentaAudio);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                canPlay = false;
                Utils.setTimeout("NovusUtils.finVentaAudio", () -> {
                    while (clip.isRunning()) {
                        canPlay = false;
                    }
                    canPlay = true;
                }, 0);

            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(NovusUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void beep() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(beep);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void pause(int i) {
        try {
            Thread.sleep(i * 1000);

        } catch (InterruptedException ex) {
            logger.log(Level.WARNING, "Interrupted!", ex);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            NovusUtils.printLn("Interrupted!" + ex.getLocalizedMessage());

        }
    }

    public static void ajusteFuente(JLabel component, int style) {
        String fontStyle;

        if (component instanceof JLabel) {

            JLabel label = (JLabel) component;
            int size = label.getFont().getSize();

            if (style == Font.BOLD) {
                fontStyle = "Bold";
                switch (size) {
                    default:
                        System.err.println("CARGANDO FUENTE:" + "[Terpel Sans " + fontStyle + "] " + size + " -> " + component.getText());
                        label.setFont(new java.awt.Font("Terpel Sans " + fontStyle, Font.BOLD, size));
                        break;
                }

            } else if (style == NovusConstante.EXTRABOLD) {
                fontStyle = "ExtraBold";
                switch (size) {
                    case 24:
                        label.setFont(TerpelFontExtraBold_24);
                        break;
                    case 32:
                        label.setFont(TerpelFontExtraBold_32);
                        break;
                    case 36:
                        label.setFont(TerpelFontExtraBold_36);
                        break;
                    case 40:
                        label.setFont(TerpelFontExtraBold_40);
                        break;
                    default:
                        System.err.println("CARGANDO FUENTE:" + "[Terpel Sans " + fontStyle + "] " + size + " -> " + component.getText());
                        label.setFont(new java.awt.Font("Terpel Sans " + fontStyle, Font.BOLD, size));
                        break;
                }
            }
        }
    }

    public static int getSelectedRowIndex(JTable table) {
        return table.getSelectedRow();
    }

    public static boolean isSelectedRow(JTable table) {
        return (NovusUtils.getSelectedRowIndex(table) > -1);
    }

    public static void ajusteFuente(Component[] components, int style) {

    }

    public static void soloNumeros(KeyEvent e) {
        char caracter = e.getKeyChar();
        if (((caracter < '0')
                || (caracter > '9'))) {
            e.consume();
        }
    }

    public static String STR_PAD_RIGHT = "STR_PAD_RIGHT";
    public static String STR_PAD_LEFT = "STR_PAD_LEFT";
    public static String STR_PAD_BOTH = "STR_PAD_BOTH";

    public static StringBuilder join(String delimiter, Set<Long> set) {
        if (null == delimiter || null == set) {
            throw new NullPointerException();
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Long element : set) {
            if (i > 0) {
                sb.append(delimiter).append(element);
            } else {
                sb.append(element);
            }
            i++;
        }
        return sb;
    }

    public static String str_pad(String input, int length, String pad, String sense) {
        if (input == null) {
            System.out.println("");
        }
        int resto_pad = length - input.length();
        String padded;

        if (resto_pad <= 0) {
            input = input.substring(0, Math.min(input.length(), length));
            return input;
        }

        if (sense.equals(STR_PAD_RIGHT)) {
            padded = input;
            padded += _fill_string(pad, resto_pad);
        } else if (sense.equals(STR_PAD_LEFT)) {
            padded = _fill_string(pad, resto_pad);
            padded += input;
        } else {
            // STR_PAD_BOTH
            int pad_left = (int) Math.ceil(resto_pad / 2);
            int pad_right = resto_pad - pad_left;

            padded = _fill_string(pad, pad_left);
            padded += input;
            padded += _fill_string(pad, pad_right);
        }
        padded = padded.substring(0, Math.min(padded.length(), length));
        System.out.println(padded);
        NovusUtils.printLn(padded);
        return padded;
    }

    protected static String _fill_string(String pad, int resto) {
        boolean first = true;
        String padded = "";

        if (resto >= pad.length()) {
            for (int i = resto; i >= 0; i = i - pad.length()) {
                if (i >= pad.length()) {
                    if (first) {
                        padded = pad;
                    } else {
                        padded += pad;
                    }
                } else {
                    if (first) {
                        padded = pad.substring(0, i);
                    } else {
                        padded += pad.substring(0, i);
                    }
                }
                first = false;
            }
        } else {
            padded = pad.substring(0, resto);
        }
        return padded;
    }

    public static String fmt(double d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%.2f", d).replaceAll("0*$", "");
        }
    }

    public static void soloNumero(KeyEvent evt) {
        char[] p = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        int b = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] == evt.getKeyChar()) {
                b = 1;
            }
        }
        if (b == 0) {
            evt.consume();
        }
    }

    public static JsonArray lecturasToJson(ArrayList<Surtidor> lecturas) {

        JsonArray array = new JsonArray();
        for (Surtidor lectura : lecturas) {
            JsonObject json = new JsonObject();
            json.addProperty("surtidor", lectura.getSurtidor());
            json.addProperty("cara", lectura.getCara());
            json.addProperty("manguera", lectura.getManguera());
            json.addProperty("grado", lectura.getGrado());
            json.addProperty("isla", lectura.getIsla());
            json.addProperty("productoIdentificador", lectura.getProductoIdentificador());
            json.addProperty("productoDescripcion", lectura.getProductoDescripcion());
            json.addProperty("familiaIdentificador", lectura.getFamiliaIdentificador());
            json.addProperty("familiaDescripcion", lectura.getFamiliaDescripcion());
            json.addProperty("acumuladoVolumen", lectura.getTotalizadorVolumen());
            json.addProperty("acumuladoVolumenReal", lectura.getTotalizadorVolumenReal());
            json.addProperty("acumuladoVenta", lectura.getTotalizadorVenta());
            json.addProperty("factor_inventario", lectura.getFactorInventario());
            json.addProperty("factor_volumen_parcial", lectura.getFactorVolumenParcial());
            json.addProperty("factor_importe_parcial", lectura.getFatorImporteParcial());
            json.addProperty("factor_precio", lectura.getFactorPrecio());
            json.addProperty("precio", lectura.getProductoPrecio());
            array.add(json);
        }
        return array;

    }

    public static void llenarComboBox(JComboBox<String> componenteCombo) {
        componenteCombo.removeAllItems();
        String condumidorFinal = "CONSUMIDOR FINAL";
        NovusConstante.getTiposIdentificaion().forEach((key, tiposDocumentos) -> {
            if (!tiposDocumentos.getTipoDocumento().equals(condumidorFinal)) {
                componenteCombo.addItem(tiposDocumentos.getTipoDocumento().toUpperCase());
            }
        });
        componenteCombo.addItem(condumidorFinal);
    }

    public static void llenarComboFidelizacion(JComboBox<String> componenteCombo) {
        componenteCombo.removeAllItems();
        NovusConstante.getTiposIdentificaion().forEach((key, tiposDocumentos) -> {
            if (tiposDocumentos.isAplicaFidelizacion()) {
                componenteCombo.addItem(tiposDocumentos.getTipoDocumento().toUpperCase());
            }
        });
    }

    public static long tipoDeIndentificacion(String tipoDeDocumento) {
        if (NovusConstante.TIPOS_IDENTIFICACION.containsKey(tipoDeDocumento)) {
            return NovusConstante.TIPOS_IDENTIFICACION.get(tipoDeDocumento).getCodigoTipoDocumento();
        } else {
            return 0L;
        }
    }

    public static String tipoDocumento(long clave) {
        String documento = "";
        for (Map.Entry<String, TipoIdentificaion> entry : NovusConstante.TIPOS_IDENTIFICACION.entrySet()) {
            if (!entry.getKey().equals("CONSUMIDOR FINAL") && entry.getValue().getCodigoTipoDocumento() == clave) {
                documento = entry.getValue().getTipoDocumento();
                break;
            }
        }
        return documento;
    }

    public static String obtenerRestriccionCaracteres(String tipoDocumento) {
        if (NovusConstante.TIPOS_IDENTIFICACION.containsKey(tipoDocumento)) {
            return NovusConstante.TIPOS_IDENTIFICACION.get(tipoDocumento).getCaracteresPermitidos();
        } else {
            return "[0-9]";
        }
    }

    public static int obtenerLimiteCaracteres(String tipoDocumento) {
        if (NovusConstante.TIPOS_IDENTIFICACION.containsKey(tipoDocumento)) {
            return NovusConstante.TIPOS_IDENTIFICACION.get(tipoDocumento).getCantidadCaracteres();
        } else {
            return 10;
        }
    }

    public static boolean habilitarPunto(String caracteresHabilitados) {
        return caracteresHabilitados.matches(".*[.].*");
    }

    public static boolean habilitarDosPunto(String caracteresHabilitados) {
        return caracteresHabilitados.matches(".*[:].*");
    }

    public static boolean habilitarTecladoAlfanumerico(String caracteresHabilitados) {
        return !caracteresHabilitados.equals("[0-9]");
    }

    class Audio extends Thread {

        @Override
        public void run() {
            try {
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static float fixed(double value, int decimals) {
        final String format = "#. " + ("##");
        DecimalFormat decimalForm = new DecimalFormat(format);
        System.out.println(value);
        NovusUtils.printLn(Double.toString(value));
        return Float.valueOf(decimalForm.format(value).replaceAll(",", "\\."));
    }

    public static double importeInverso(double factor, double value) {
        return (factor < 0) ? (value * (factor * -1)) : (value / factor);
    }

    public static void setUnsortableTable(JTable table) {
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            ((DefaultRowSorter) table.getRowSorter()).setSortable(i, false);
        }
    }

    public static String encriptacionBase64AES256(String value) {
        final String KEY = "u6LykvD8N3bq0EDkqQoqmm7dtMzgWLZ8";
        final String INIT_VECTOR = "mqWg&B7TNx[LFl*n";
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return new String(Base64.getEncoder().encode(encrypted));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean hayConectividadAInternet() {
        String host = "www.terpel.com";
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            printLn("haciendo a -> " + host);
            boolean hayInternet = inetAddress.isReachable(5000);
            printLn(hayInternet ? "conectado" : "desconectado");
            return hayInternet;
        } catch (IOException ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
            return false;
        }
    }

    public static void limitarCarateres(KeyEvent evt, JTextField campo, int maximo, JLabel label, String argumentos) {
        if (evt.getKeyChar() != evt.VK_ENTER) {
            String a = String.valueOf(evt.getKeyChar());
            String ultimoCaracter = "";
            if (campo.getText().equals("")) {
                char c = evt.getKeyChar();
                if (c == '.') {
                    evt.consume();
                }
            }
            if (campo.getText().length() > 0) {
                ultimoCaracter = campo.getText().substring(campo.getText().length() - 1);
                if (!ultimoCaracter.equals(".")) {
                    ultimoCaracter = "";
                }
            }
            if (evt.getKeyChar() != evt.VK_BACK_SPACE) {
                if (ultimoCaracter.equals(a)) {
                    evt.consume();
                } else {
                    Pattern pat = Pattern.compile(argumentos);
                    String texto = a;
                    Matcher mat = pat.matcher(texto);
                    if (!mat.matches()) {
                        evt.consume();
                        label.setVisible(true);
                        label.setText("caracter no válido");
                        Utils.setTimeout("", () -> {
                            label.setVisible(false);
                        }, 3000);
                    } else {
                        if (campo.getText().length() >= maximo) {
                            evt.consume();
                            label.setVisible(true);
                            label.setText("Maximo " + maximo + " caracteres");
                            Utils.setTimeout("", () -> {
                                label.setVisible(false);
                            }, 3000);
                        }
                    }
                }
            }
        }
    }

    public static boolean validacionesCampos(KeyEvent event, JTextField campo, int maximo, JLabel label, String argumentos, int tamañoLetra, int minimo, Color color) {
        boolean campoValido = Boolean.FALSE;
        char caracter = event.getKeyChar();
        int codigoCaracter = (int) caracter;
        boolean noValido = codigoCaracter == event.VK_ENTER || codigoCaracter == event.VK_BACK_SPACE;
        if (!noValido) {
            Pattern pat = Pattern.compile(argumentos);
            String texto = String.valueOf(event.getKeyChar());
            Matcher mat = pat.matcher(texto);
            if (!mat.matches()) {
                event.consume();
                label.setVisible(true);
                label.setText("Caracter no válido");
                label.setFont(new java.awt.Font("Segoe UI", 0, tamañoLetra));
                label.setForeground(color);
                campoValido = campo.getText().length() >= minimo;
                Utils.setTimeout("", () -> {
                    label.setVisible(false);
                }, 3000);
            } else {
                if (campo.getText().length() >= maximo) {
                    event.consume();
                    label.setVisible(true);
                    label.setText("Máximo " + maximo + " Caracteres");
                    label.setFont(new java.awt.Font("Segoe UI", 0, tamañoLetra));
                    label.setForeground(color);
                    Utils.setTimeout("", () -> {
                        label.setVisible(false);
                    }, 3000);

                } else if (campo.getText().length() < (minimo - 1)) {
                    campoValido = Boolean.FALSE;
                    minimoCaracteres(label, minimo, tamañoLetra, color);
                } else {
                    campoValido = Boolean.TRUE;
                    label.setVisible(false);
                }
            }
        } else {
            if (campo.getText().length() < minimo) {
                campoValido = Boolean.FALSE;
                minimoCaracteres(label, minimo, tamañoLetra, color);
            } else {
                campoValido = Boolean.TRUE;
            }
        }
        return campoValido;
    }

    private static void minimoCaracteres(JLabel label, int minimo, int tamañoLetra, Color color) {
        label.setVisible(true);
        label.setText("Mínimo " + minimo + " Caracteres");
        label.setForeground(color);
        label.setFont(new java.awt.Font("Segoe UI", 0, tamañoLetra));
        Utils.setTimeout("", () -> {
            label.setVisible(false);
        }, 3000);
    }

    public static boolean validarCampos(String argumentos, JTextField field, int maximo, JLabel jNotificacion) {
        boolean seguir = true;
        String usuario = field.getText();
        Pattern pat = Pattern.compile(argumentos);
        Matcher mat = pat.matcher(usuario);
        if (!mat.matches()) {
            jNotificacion.setVisible(true);
            jNotificacion.setText("caracter no valido".toUpperCase());
            if (field.getText().length() >= maximo) {
                jNotificacion.setVisible(true);
                jNotificacion.setText("MAXIMO " + maximo + " CARACTERES");
                Utils.setTimeout("", () -> {
                    jNotificacion.setVisible(false);
                }, 3000);
            } else {
                Utils.setTimeout("", () -> {
                    jNotificacion.setVisible(false);
                }, 3000);
            }
            seguir = false;
        }
        return seguir;
    }

    public static void deshabilitarCopiarPegar(JTextField campo) {
        campo.getInputMap().put(KeyStroke.getKeyStroke("control V"), "null");
        campo.getInputMap().put(KeyStroke.getKeyStroke("control C"), "null");
    }

    public static void setTablePrimaryStyle(JTable table) {
        try {
            DefaultTableCellRenderer textCenter = new DefaultTableCellRenderer();

            table.setFont(new java.awt.Font("Bebas Neue", 0, 20));
            table.setSelectionBackground(new Color(255, 182, 0));
            table.setSelectionForeground(new Color(0, 0, 0));
            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
            headerRenderer.setBackground(new Color(186, 12, 47));
            headerRenderer.setForeground(new Color(255, 255, 255));
            table.setFillsViewportHeight(true);
            headerRenderer.setHorizontalAlignment(JLabel.CENTER);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < table.getModel().getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(table.getModel()) {
                @Override
                public boolean isSortable(int column) {
                    super.isSortable(column);
                    return false;
                }
            };
            table.setRowSorter(rowSorter);

            Component tableParent = table.getParent();

            if (tableParent instanceof JScrollPane) {
                JScrollPane scrollPanel = (JScrollPane) tableParent;
                scrollPanel.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                    @Override
                    protected void configureScrollBarColors() {
                        this.thumbColor = new Color(186, 12, 47);
                    }
                });
                scrollPanel.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
                scrollPanel.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
            }
        } catch (Exception e) {
            System.out.println(e);
            NovusUtils.printLn(e.getMessage());
        }
    }

    public static TreeMap<String, String> headers() {
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "1111");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);
        return header;
    }

    public static void designButtons(JLabel label, Icon icono) {
        label.setIcon(icono);
        label.setForeground(new Color(153, 0, 0));
    }

    public static void debounce(Runnable action, float delay) {
        NovusUtils.printLn("Ejecutando funcion Debounce");
        Timer debounceTimer = new Timer();
        long timeDelay = (long) (delay * 1000);
        NovusUtils.printLn("Delay " + timeDelay);
        debounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                action.run();
                KCOViewController.writing = false;
            }
        }, timeDelay);
    }

    public static JsonObject informacionDatafono(JsonArray datafonos, MovimientosDao mdao) {
        JsonObject data = new JsonObject();
        for (JsonElement objDatafonos : datafonos) {
            JsonObject dataitemDatafono = objDatafonos.getAsJsonObject();
            data.addProperty("proveedor", dataitemDatafono.get("proveedor").getAsString());
            data.addProperty("codigoDatafono", dataitemDatafono.get("codigo_terminal").getAsString());
            data.addProperty("negocio", "COM");
            data.addProperty("codigoAutorizacion", mdao.getCodigoAutorizacion());
            data.addProperty("promotor", Main.persona.getId());
            data.addProperty("proveedorId", dataitemDatafono.get("id_adquiriente").getAsInt());
            data.addProperty("pos", Main.credencial.getEquipos_id() + "");
            data.addProperty("tipoVenta", "UNICO");
        }
        return data;
    }

    public static JsonObject validarParametrizacionDatafono(JsonObject objDatafono, MovimientosDao mdao) {
        JsonObject dataRespuesta = new JsonObject();
        TreeMap<String, String> parametros = mdao.buscarParametrizacion(objDatafono.get("proveedorId").getAsInt());
        for (ParametrizacionDatafanos parametrosDatafonos : ParametrizacionDatafanos.values()) {
            if (parametros.isEmpty()) {
                dataRespuesta = new JsonObject();
                dataRespuesta.addProperty("status", Boolean.FALSE);
                dataRespuesta.addProperty("mensaje", "NO HAY PARAMETROS CONFIGURADOS PARA ESTE DATAFONOS");
                return dataRespuesta;
            } else if (parametros.get(parametrosDatafonos.getValor()) == null) {
                dataRespuesta = new JsonObject();
                dataRespuesta.addProperty("status", Boolean.FALSE);
                dataRespuesta.addProperty("mensaje", "FALTA EL PARAMETRO DE " + parametrosDatafonos.getValor());
                return dataRespuesta;
            } else {
                dataRespuesta = new JsonObject();
                dataRespuesta.addProperty("status", Boolean.TRUE);
                dataRespuesta.addProperty("mensaje", "TODA LA CON FIGURACION DE DATAFONO ESTÁ CORRECTA");
            }
        }
        return dataRespuesta;
    }

    public static String getTipoNegocioComplementario() {
        String tipoNegocio = "KCO";
        if (!Main.TIPO_NEGOCIO.isEmpty()
                && !Main.TIPO_NEGOCIO.equalsIgnoreCase("COMB")
                && !Main.TIPO_NEGOCIO.equalsIgnoreCase(NovusConstante.PARAMETER_CAN)) {
            tipoNegocio = Main.TIPO_NEGOCIO;
        }
        return tipoNegocio;
    }

    public static int idTipoNegocio(String tipoNegocio) {
        int idTipoNegocio = 1;
        switch (tipoNegocio) {
            case NovusConstante.PARAMETER_COMB:
                idTipoNegocio = 1;
                break;
            case NovusConstante.PARAMETER_KCO:
                idTipoNegocio = 2;
                break;
            case NovusConstante.PARAMETER_CAN:
                idTipoNegocio = 3;
                break;
            case NovusConstante.PARAMETER_CDL:
                idTipoNegocio = 4;
                break;
            case NovusConstante.PARAMETER_UREA:
                idTipoNegocio = 5;
                break;
            default:
                return idTipoNegocio;
        }
        return idTipoNegocio;
    }

    public static int tipoIdentificacionPromotor(String descripcion) {
        int tipoIdentificacion = 1;
        switch (descripcion) {
            case NovusConstante.PARAMETER_CEDULA_DE_CIUDADANIA:
                tipoIdentificacion = 1;
                break;
            case NovusConstante.PARAMETER_CEDULA_DE_EXTRANJERIA:
                tipoIdentificacion = 2;
                break;
            default:
                return tipoIdentificacion;
        }
        return tipoIdentificacion;
    }

    public static JsonObject remplazarCaracteresEspecialesClientesFe(JsonObject response) {
        String tipoCadena = "String";
        String tipoObjetoJson = "jsonObject";
        try {
            String nombreComercial = (String) validarPropiedad(response, "nombreComercial", tipoCadena);
            String nombreRazonSocial = (String) validarPropiedad(response, "nombreRazonSocial", tipoCadena);
            String direccion = (String) validarPropiedad(response, "direccion", tipoCadena);
            String correo = (String) validarPropiedad(response, "correoElectronico", tipoCadena);
            response.addProperty("nombreComercial", nombreComercial);
            response.addProperty("nombreRazonSocial", nombreRazonSocial);
            response.addProperty("direccion", direccion);
            response.addProperty("correoElectronico", correo);

            JsonObject extraData = (JsonObject) validarPropiedad(response, "extraData", tipoObjetoJson);
            String correoElectronicoExtrada = (String) validarPropiedad(extraData, "correoElectronico", tipoCadena);
            String nombreComercialExtrada = (String) validarPropiedad(extraData, "nombreComercial", tipoCadena);
            String nombreRegistroExtrada = (String) validarPropiedad(extraData, "nombreRegistro", tipoCadena);

            JsonObject direccionCliente = (JsonObject) validarPropiedad(extraData, "direccionCliente", tipoObjetoJson);
            String direccionClienteExtradata = (String) validarPropiedad(direccionCliente, "direccionLibre", tipoCadena);
            if (extraData == null) {
                extraData = new JsonObject();
            }
            if (direccionCliente == null) {
                direccionCliente = new JsonObject();
            }
            extraData.addProperty("correoElectronico", correoElectronicoExtrada);
            extraData.addProperty("nombreComercial", nombreComercialExtrada);
            extraData.addProperty("nombreRegistro", nombreRegistroExtrada);
            direccionCliente.addProperty("direccionLibre", direccionClienteExtradata);
            extraData.add("direccionCliente", direccionCliente);
            response.add("extraData", extraData);
        } catch (Exception e) {
            printLn("ha ocurrido un error inesperado al reemplazar algunos de los atributos del cliente extraidos");
            return response;
        }
        return response;
    }

    public static Object validarPropiedad(JsonObject data, String propiedad, String tipo) {
        Object respuesta = null;
        String caracteresAReemplazar = "[\"']";
        if (data != null && data.has(propiedad) && data.get(propiedad) != null && !data.get(propiedad).isJsonNull()) {
            switch (tipo) {
                case "String":
                    respuesta = limpiarCaracteresEspeciales(caracteresAReemplazar, data.get(propiedad).getAsString(), "");
                    break;
                case "jsonObject":
                    respuesta = data.get(propiedad).getAsJsonObject();
                    break;
                default:
                    printLn("");
            }
        }
        return respuesta;
    }

    public static String limpiarCaracteresEspeciales(String exprexionRegular, String cadena, String caracterReemplazar) {
        return cadena.replaceAll(exprexionRegular, caracterReemplazar);
    }

    public static void showPanel(JPanel panel, String nombre) {
        CardLayout card = (CardLayout) panel.getLayout();
        card.show(panel, nombre);
    }

    public static String convertMessage(String letterCase, String mensaje) {
        String nuevoMensaje = "";
        if (mensaje != null && !mensaje.isEmpty()) {
            switch (letterCase) {
                case LetterCase.UPPER_CASE:
                    nuevoMensaje = mensaje.toUpperCase();
                    break;
                case LetterCase.LOWER_CASE:
                    nuevoMensaje = mensaje.toLowerCase();
                    break;
                case LetterCase.FIRST_UPPER_CASE:
                    nuevoMensaje = mensaje.substring(0, 1).toUpperCase()
                            + mensaje.substring(1).toLowerCase();
                    break;
                default:
                    nuevoMensaje = mensaje.substring(0, 1).toUpperCase()
                            + mensaje.substring(1).toLowerCase();
                    break;
            }
        }
        return nuevoMensaje;
    }

    public static boolean verificarMedioPago(JsonArray arrayMedios, String medioDescripcion) {
        for (JsonElement arrayMedio : arrayMedios) {
            JsonObject dataMedios = arrayMedio.getAsJsonObject();
            if (dataMedios.get("descripcion").getAsString().equalsIgnoreCase(medioDescripcion)) {
                return true;
            }
        }
        return false;
    }

    public static boolean existeBono(JsonArray bonos, long bonoVoucher) {
        boolean existe = false;
        if (bonos != null) {
            for (JsonElement bono : bonos) {
                JsonObject bonosVenta = bono.getAsJsonObject();
                if (bonoVoucher == bonosVenta.get("AFP").getAsLong()) {
                    existe = true;
                }
            }
        }
        return existe;
    }

    public static boolean isBonoViveTerpel(MediosPagosBean medioSeleccinado) {
        return medioSeleccinado.getAtributos() != null && !medioSeleccinado.getAtributos().isJsonNull()
                && medioSeleccinado.getAtributos().get("bonoTerpel") != null
                && !medioSeleccinado.getAtributos().get("bonoTerpel").isJsonNull()
                && medioSeleccinado.getAtributos().get("bonoTerpel").getAsBoolean();
    }

    public static String getLineaNegocio(String tipoVenta) {
        String lineaNegocio = "";
        switch (tipoVenta) {
            case NovusConstante.PARAMETER_CDL:
                lineaNegocio = "CDL";
                break;
            case "L":
                lineaNegocio = "LU";
            default:
                break;
        }
        return lineaNegocio;
    }

}

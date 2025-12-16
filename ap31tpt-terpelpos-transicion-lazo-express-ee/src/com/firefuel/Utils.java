/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.facade.ConfigurationFacade;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author ASUS-PC
 */
public class Utils {

    public static final int PAGE_SIZE = 64;
    static ArrayList<String> datos = new ArrayList<>();
    private static final String OS = System.getProperty("os.name").toLowerCase();
    static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    public static String getIpLocal() {
        String host = "";
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress(NovusConstante.HOST_END_POINT, 80));
            host = socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            host = "ERROR DE RED";
        }
        return host;
    }

    public static void setTimeout(String origen, Runnable runnable, int delay) {
        NovusUtils.printLn("setTimeout from class [" + origen + "] " + new Date().toString());
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                logger.log(java.util.logging.Level.WARNING, "Interrupted!", e);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static String getSystemDevice() {
        try {
            String OSName = System.getProperty("os.name").toLowerCase();
            if (OSName.contains("windows")) {
                return (getWindowsMotherboardSerialNumber());
            } else if (OSName.contains("mac")) {
                return (getMacMotherBoardSerialNumber());
            } else {
                return (getLinuxMotherBoardSerialNumber());
            }
        } catch (Exception E) {
            System.err.println("System MotherBoard Exp : " + E.getMessage());
            return null;
        }
    }

    public static String jsonArrayToString(JsonArray jsonArray) {
        String newString = "";
        int index = 0;
        for (JsonElement element : jsonArray) {
            String value = element.getAsString();
            newString += "'" + value + "'" + (index < (jsonArray.size() - 1) ? ", " : "");
            index++;
        }
        return newString;
    }

    public static JsonArray fetchConfigurationParams(JsonArray codesArray) {
        return ConfigurationFacade.fetchConfigurationParams(jsonArrayToString(codesArray));
    }

    public static String getSystemStore() {
        try {
            String OSName = System.getProperty("os.name").toLowerCase();
            if (OSName.contains("windows")) {
                return (getWindowsStorage());
            } else if (OSName.contains("mac")) {
                return (getMacStorage());
            } else {
                return (getLinuxStorage());
            }
        } catch (Exception E) {
            System.err.println("System MotherBoard Exp : " + E.getMessage());
            return null;
        }
    }

    public static String getSystemNetwork() {
        try {
            String OSName = System.getProperty("os.name").toLowerCase();
            if (OSName.contains("windows")) {
                return (getWindowsMac());
            } else if (OSName.contains("mac")) {
                return (getMacMac());
            } else {
                return (getLinuxMac());
            }
        } catch (Exception E) {
            System.err.println("System MotherBoard Exp : " + E.getMessage());
            return null;
        }
    }

    public static String getWindowsMotherboardSerialNumber() {
        // wmic diskdrive get serialnumber
        String command = "wmic diskdrive get serialnumber";
        String result;
        String serial = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                if (!result.contains("Serial") && !result.trim().equals("")) {
                    serial = result.trim();
                }
            }
        } catch (IOException ex) {
            System.err.println("Windows Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return "wm" + serial + "er" + serial;
    }

    public static String getLinuxMotherBoardSerialNumber() {
        String command = "cat /proc/cpuinfo";
        String result;
        String serial = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                if (result.contains("Serial")) {
                    serial = result.split(":")[1].trim();
                }
            }
        } catch (IOException ex) {
            System.err.println("Linux Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return serial != null && !serial.equals("") ? serial : getSystemNetwork();
    }

    public static String getMacMotherBoardSerialNumber() {
        String command = "ioreg -l";
        String result;
        String serial = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                if (serial == null && result.contains("IOPlatformSerialNumber")) {
                    serial = result.trim().split("=")[1].trim().replace('\"', ' ').trim();
                    NovusUtils.printLn(serial);
                }
            }
        } catch (IOException ex) {
            System.err.println("Mac Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return serial;
    }

    private static String getMacStorage() {
        String command = "system_profiler SPSerialATADataType";
        String result;
        String serial = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                if (serial == null && result.contains("Serial Number")) {
                    serial = result.trim().split(":")[1].trim().replace('\"', ' ').trim();
                    NovusUtils.printLn(serial);
                }
            }
        } catch (IOException ex) {
            System.err.println("Mac Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return serial;
    }

    private static String getWindowsStorage() {
        // wmic diskdrive get serialnumber
        String command = "wmic diskdrive get serialnumber";
        String result;
        String serial = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                if (!result.contains("Serial") && !result.trim().equals("")) {
                    serial = result.trim();
                }
            }
        } catch (IOException ex) {
            System.err.println("Windows Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return serial;
    }

    private static String getLinuxStorage() {
        // String command = "cat /proc/cpuinfo | tail -1 | cut -c11-100 && cat
        // /sys/block/mmcblk0/device/cid && cat /sys/class/net/eth0/address";
        // String command2[] = {"cat /sys/block/mmcblk0/device/cid", "cat
        // /sys/class/net/eth0/address", "cat /proc/cpuinfo | tail -1 | cut -c11-100"};
        String command = "cat /sys/block/mmcblk0/device/cid";
        String result;
        String serial = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                serial = result.trim();
            }
        } catch (IOException ex) {
            System.err.println("Linux Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return serial;
    }

    private static String getLinuxMac() {
        String command = "cat /sys/class/net/eth0/address";
        String result;
        String mac = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                mac = result.trim();
            }
        } catch (IOException ex) {
            System.err.println("Linux Motherboard Exp : " + ex.getMessage());
            mac = null;
        }
        return mac;
    }

    private static String getWindowsMac() {
        InetAddress ip;
        String macString = null;
        try {
            ip = InetAddress.getLocalHost();
            NovusUtils.printLn("Current IP address : " + ip.getHostAddress());
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            NovusUtils.printLn("Current MAC address : ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            NovusUtils.printLn(sb.toString());
            macString = sb.toString().replaceAll("-", ":").toLowerCase();
        } catch (UnknownHostException | SocketException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return macString;
    }

    private static String getMacMac() {
        String command = "ifconfig";
        String result;
        String serial = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                if (serial == null && result.contains("ether")) {
                    serial = result.trim().split(" ")[1].trim().replace('\"', ' ').trim();
                    NovusUtils.printLn(serial);
                }
            }
        } catch (IOException ex) {
            System.err.println("Mac Motherboard Exp : " + ex.getMessage());
            serial = null;
        }
        return serial;
    }

    public static String getLapsoTiempo(Date start, Date end) {
        long diffInSeconds = (end.getTime() - start.getTime()) / 1000;
        long diff[] = new long[]{0, 0, 0, 0};
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        String time = "";
        if (diff[0] > 1) {
            time += diff[0] + " ds ";
        } else if (diff[0] == 1) {
            time += diff[0] + " d ";
        }

        if (diff[1] > 1) {
            time += diff[1] + " hs ";
        } else if (diff[1] == 1) {
            time += diff[1] + " h ";
        }

        if (diff[2] > 1) {
            time += diff[2] + " mins ";
        } else if (diff[2] == 1) {
            time += diff[2] + " min ";
        }

        if (diff[0] == 0 && diff[1] == 0 && diff[2] == 0) {
            time += diff[3] + " seg ";
        }

        return time;
        /*
         * return (String.format( "%d day%s, %d hour%s, %d minute%s, %d second%s ago",
         * diff[0], diff[0] > 1 ? "s" : "", diff[1], diff[1] > 1 ? "s" : "", diff[2],
         * diff[2] > 1 ? "s" : "", diff[3], diff[3] > 1 ? "s" : ""));
         */
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static float potencia(float base, int exponente) {
        float result = 1;
        int cont = exponente;
        while (cont > 0) {
            result *= base;
            cont--;
        }
        return result;
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

    public static void decimalFomatTextField(JTextField jTextField1) {

        DecimalFormat f = new DecimalFormat("###,###.##");
        if (jTextField1.getText().length() > 2) {
            int value = Integer.parseInt(jTextField1.getText().replace(".", ""));
            jTextField1.setText(String.valueOf(f.format(value)));
        }
        if (jTextField1.getText().length() == 0) {
            jTextField1.setText(String.valueOf(f.format(0.0)));
        }
    }

    /*
     * public static void descargarImagenX(String entidad, long id, long dominio) {
     * if (false) { try { File folder = new File(System.getProperty("user.home") +
     * "/resources/imagenes/" + entidad); folder.mkdirs();
     *
     * File file = new File(System.getProperty("user.home") + "/resources/imagenes/"
     * + entidad + "/" + id + ".png"); FileOutputStream fos = new
     * FileOutputStream(file);
     *
     * Dimension size = new Dimension(100, 100);
     *
     * //URL url = new URL(fullurl); String surl = "https://" +
     * NovusConstante.HOST_IMAGENES_END_POINT + "/neoline/" + dominio + "/" +
     * entidad + "/" + id + ".png"; Main.LOGGER.info("[IMAGEN] " + surl); URL url =
     * new URL(surl); BufferedImage image = ImageIO.read(url); BufferedImage resized
     * = new BufferedImage(size.width, size.height, BufferedImage.SCALE_FAST);
     * Graphics2D g = resized.createGraphics(); g.drawImage(image, 0, 0, size.width,
     * size.height, null); g.dispose();
     *
     * ImageIO.write(resized, "png", file); if (entidad.equals(Butter.CATEGORIA)) {
     * if (Main.iconosCategorias != null) { if (Main.iconosCategorias.get(id) !=
     * null) { Icon icon = new ImageIcon("/home/pi/resources/imagenes/" + entidad +
     * "/" + id + ".png"); Main.iconosCategorias.put(id, icon); } } } else { if
     * (Main.iconos != null) { if (Main.iconos.get(id) != null) { Icon icon = new
     * ImageIcon("/home/pi/resources/imagenes/" + entidad + "/" + id + ".png");
     * Main.iconos.put(id, icon); } } } } catch (Exception e) {
     * System.err.println("ERROR DESCARGANDO LAS IMAGEN"); Main.LOGGER.info(e); } }
     * }
     */
    public static String str_pad(String input, int length, String pad, String sense) {
        if (input == null) {
            // Main.LOGGER.info("");
        }
        int resto_pad = length - input.length();
        String padded;

        if (resto_pad <= 0) {
            return input;
        }

        switch (sense) {
            case "STR_PAD_RIGHT":
                padded = input;
                padded += _fill_string(pad, resto_pad);
                break;
            case "STR_PAD_LEFT":
                padded = _fill_string(pad, resto_pad);
                padded += input;
                break;
            // STR_PAD_BOTH
            default:
                int pad_left = (int) Math.ceil(resto_pad / 2);
                int pad_right = resto_pad - pad_left;
                padded = _fill_string(pad, pad_left);
                padded += input;
                padded += _fill_string(pad, pad_right);
                break;
        }
        return padded;
    }

    public static String fill(String str, int spaces) {
        if (str.length() <= spaces) {
            return str + fill(str, spaces - 1);
        } else {
            return "";
        }
    }

    public static String text_between(String str1, String str2) {
        int leftSpaces = PAGE_SIZE - (str1.length() + str2.length());
        return str1.concat(fill(" ", leftSpaces).concat(str2));
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

    public static Date stringToDate(String asString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
            return sdf.parse(asString);
        } catch (ParseException a) {
            return null;
        }
    }

    public static void syncLog(String log) {
        if (SincronizarView.jlog != null) {
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_TIME_AM);
            SincronizarView.jlog.setText(SincronizarView.jlog.getText().trim() + "\n[" + sdf.format(currentDate) + "] " + log.trim());
        }
    }

    public static Date stringToDateFull(String asString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
            return sdf.parse(asString);
        } catch (ParseException a) {
            return null;
        }
    }

    public static String getTimeFromRtc() {
        String command = "sudo hwclock -r";
        String result;
        String serial = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                NovusUtils.printLn(result.trim());
                serial = result.trim();
            }
        } catch (IOException ex) {
            System.err.println("Linux Motherboard Exp : " + ex.getMessage());
            serial = "";
        }
        return serial.replace('.', 'e');
    }

    public static String setTimeToSystem(String time) {
        String[] cmd = {"/bin/bash", "-c", "sudo date --s \"" + time + "\""};

        String result;
        String serial = "";
        try {
            NovusUtils.printLn("sudo date --s \"" + time + "\"");
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                serial = result.trim();
                NovusUtils.printLn(serial);
            }
        } catch (IOException ex) {
            System.err.println("Linux SET DATE TO SYSTEM Exp : " + ex.getMessage());
            serial = "";
        }
        return serial;
    }

    public static String setTimeToRTC() {
        String[] cmd = {"/bin/bash", "-c", "sudo hwclock -w"};

        String result;
        String serial = "";
        try {
            NovusUtils.printLn("sudo hwclock -w");
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                serial = result.trim();
                NovusUtils.printLn(serial);
            }
        } catch (IOException ex) {
            System.err.println("Linux SET DATE TO RTC Exp : " + ex.getMessage());
            serial = "";
        }
        return serial;
    }

    public static String resetPost() {

        String[] cmd = {"/bin/bash", "-c", "sudo reboot"};

        String result;
        String serial = "";
        try {
            NovusUtils.printLn("REINICIANDO EL POST");
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = stdInput.readLine()) != null) {
                serial = result.trim();
                NovusUtils.printLn(serial);
            }
        } catch (IOException ex) {
            System.err.println("Linux COMMAND REBOOT Exp : " + ex.getMessage());
            serial = "";
        }
        return serial;
    }

    static public void reiniciaTarjetaRed() {
        final Runtime runtime = Runtime.getRuntime();
        Process qq;
        try {
            qq = runtime.exec(new String[]{"/bin/bash", "-c", "sudo ifconfig wlan0 down"});
            qq.waitFor();
            qq = runtime.exec(new String[]{"/bin/bash", "-c", "sudo ifconfig wlan0 up"});
            qq.waitFor();
            // reportStatus(NovusConstante.ALERT_EXECUTE_COMANDO,
            // Butter.ALERT_EXECUTE_COMANDO_RESET_WIFI, "COMANDO APLICADO");
        } catch (InterruptedException e) {
            // reportStatus(Butter.ALERT_EXECUTE_COMANDO,
            // Butter.ALERT_EXECUTE_COMANDO_RESET_WIFI, "COMANDO NO APLICADO -
            // InterruptedException");
            NovusUtils.printLn(e.getMessage());
        } catch (IOException ex) {
            // reportStatus(Butter.ALERT_EXECUTE_COMANDO,
            // Butter.ALERT_EXECUTE_COMANDO_RESET_WIFI, "COMANDO NO APLICADO -
            // IOException");
            // Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            // reportStatus(Butter.ALERT_EXECUTE_COMANDO,
            // Butter.ALERT_EXECUTE_COMANDO_RESET_WIFI, "COMANDO NO APLICADO - Exception");
            // Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * static public void reportStatus(int codigo, String title, String detalle) {
     * try { SimpleDateFormat sdf = new
     * SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
     *
     * JsonArray eventos = new JsonArray();
     *
     * JsonObject json = new JsonObject(); json.addProperty("equipos_id",
     * credencial.getEquipos_id()); json.addProperty("empresas_id",
     * credencial.getEmpresas_id()); json.addProperty("bodegas_id",
     * credencial.getBodegaId()); json.addProperty("fecha", sdf.format(new Date()));
     * json.addProperty("fecha_recibido", sdf.format(new Date())); //
     * json.addProperty("tipos_alertas", NovusConstante.ALERT_TIPO_WARMING);
     * json.addProperty("codigo", codigo); json.addProperty("descripcion", title);
     * json.addProperty("detalles", detalle);
     *
     * eventos.add(json);
     *
     * String url = NovusConstante.SECURE_END_POINT_REPORT_EVENT + "/" +
     * credencial.getReferencia(); ClientWSAsync async = new ClientWSAsync(
     * "REPORTE BASICO DE ESTADO", url, Butter.POST, eventos.toString(),
     * Main.DEBUG_REPORTE_EQUIPO ); async.start(); try { async.join(); JsonObject
     * response = async.getResponse();
     *
     * if (response == null) {
     *
     * response = new JsonObject(); response.addProperty("code",
     * async.getErrorCodigo()); response.addProperty("error",
     * async.getErrorMensaje()); if (Main.DEBUG_REPORTE_EQUIPO) {
     * NovusUtils.printLn(response); } } } catch (InterruptedException A) {
     * System.err.println("InterruptedException REPORTE BASICO DE ESTADO"); } }
     * catch (Exception A) {
     * System.err.println("Exception REPORTE BASICO DE ESTADO"); }
     *
     * }
     */
    public static JsonObject getInfoAnydesk() {
        JsonObject info = new JsonObject();
        try {
            String[] comanods = {"anydesk --get-alias", "anydesk --get-id", "anydesk --get-status"};
            String[] atribute = {"alias", "id", "status"};

            for (int i = 0; i < comanods.length; i++) {
                String[] cmd = {"/bin/bash", "-c", comanods[i]};
                Process pb = Runtime.getRuntime().exec(cmd);
                pb.waitFor();
                String line;
                try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
                    while ((line = input.readLine()) != null) {
                        info.addProperty(atribute[i], line);
                    }
                }
            }
        } catch (IOException | InterruptedException a) {
        }
        return info;
    }

    public static JsonObject getInfoSistema() {
        JsonObject info = new JsonObject();
        try {
            String[] comanods = {"uname -a", "uptime -p"};
            String[] atribute = {"sistema", "uptime"};

            for (int i = 0; i < comanods.length; i++) {
                String[] cmd = {"/bin/bash", "-c", comanods[i]};
                Process pb = Runtime.getRuntime().exec(cmd);
                pb.waitFor();
                String line;
                try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
                    while ((line = input.readLine()) != null) {
                        info.addProperty(atribute[i], line);
                    }
                }
            }
        } catch (IOException | InterruptedException a) {
        }
        return info;
    }

    public static JsonArray getWifis() {
        JsonArray array = new JsonArray();
        try {
            String[] cmd = {"/bin/bash", "-c",
                "sudo cat /etc/wpa_supplicant/wpa_supplicant.conf | grep -E -- 'ssid|psk'"};
            Process pb = Runtime.getRuntime().exec(cmd);
            pb.waitFor();
            String line;
            JsonObject json = null;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
                while ((line = input.readLine()) != null) {
                    if (line.contains("ssid")) {
                        json = new JsonObject();
                        String wifi = line.split("=")[1].substring(1, line.split("=")[1].length() - 1);
                        json.addProperty("ssid", wifi);
                    }
                    if (line.contains("psk")) {
                        if (json != null) {
                            String pass = line.split("=")[1].substring(1, line.split("=")[1].length() - 1);
                            json.addProperty("psk", pass);
                            array.add(json);
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException a) {
        }
        return array;
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
            json.addProperty("acumuladoVenta", lectura.getTotalizadorVenta());
            array.add(json);
        }
        return array;
    }

    public static String fmt(double d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%.2f", d).replaceAll("0*$", "");
        }
    }

    /*
     * public static long calculeCantidad(long cantidad, int factor) { long result;
     * if (factor < 0) { result = cantidad / (factor * -1); } else { result =
     * cantidad * factor; } return result; }
     */
    public static double calculeCantidad(long cantidad, int factor) {
        double result;
        if (factor < 0) {
            result = (double) cantidad / (double) (factor * -1);
        } else {
            result = (double) cantidad * (double) factor;
        }
        return result;
    }

    /*
     * public static void undecorateds(JFrame aThis) {
     *
     * if (!Main.WINDOWS_UNDECORATED) {
     * aThis.setUndecorated(Main.WINDOWS_UNDECORATED); aThis.setBounds(0, 0,
     * Butter.RESOLUCION_WIDHT, Butter.RESOLUCION_HEIGHT + 20);
     * aThis.setLocationRelativeTo(null); }
     *
     * }
     */
}

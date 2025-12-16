/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.componentes.notificaciones.error;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.notificaiones.error.ErrorNotificacionDao;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Devitech
 */
public class NotificacionHistorialerror {

    public static ScheduledExecutorService consultarHistoricoReciente = Executors.newScheduledThreadPool(3);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    );

    public static void notificar() {
        if (NovusConstante.BASE_DE_dATOS_ACTIVA) {
            ErrorNotificacionDao errorNotificacionDao = new ErrorNotificacionDao();
            JsonArray data = errorNotificacionDao.dataNotificacionError();
            int numeroNotificacion = numeroNotificaciones(data);
            Main.info.errorNotificaciones(numeroNotificacion, data);
            InfoViewController.lblNumeroNotificaion.setText(numeroNotificacion >= 10 ? "9+" : numeroNotificacion + "");
            InfoViewController.pnlNumeroNotificaiones.setVisible(numeroNotificacion > 0);
            InfoViewController.lblNumeroNotificaion.setVisible(numeroNotificacion > 0);
            if (InfoViewController.NotificadorNotificacionesError != null) {
                JsonObject dataObj = new JsonObject();
                dataObj.add("data", data);
                InfoViewController.NotificadorNotificacionesError.send(dataObj);
            }
        }

    }

    public static int numeroNotificaciones(JsonArray data) {
        TreeMap<String, String> infoFechas = new TreeMap<>();
        int noticicacion = 0;
        if (NovusConstante.MAPEO_SURTIDOR.size() > 0) {
            data.addAll(NovusConstante.MAPEO_SURTIDOR.getAsJsonArray());
        }

        if (NovusConstante.AUTODIAGNOSTICO_SURTIDOR.size() > 0) {
            data.addAll(NovusConstante.AUTODIAGNOSTICO_SURTIDOR.getAsJsonArray());
        }
        for (JsonElement element : data) {
            JsonObject error = element.getAsJsonObject();
            int indice = indice(error.get("eventDate").getAsString());
            String componente = error.get("eventComponent").getAsString();
            if (!validarExistenciaDeInformacion(infoFechas, indice + "-" + componente)) {
                JsonArray dataError = new JsonArray();
                dataError.add(error);
                noticicacion = noticicacion + 1;
                infoFechas.put(indice + "-" + componente, indice + "-" + componente);
            }
        }

        return noticicacion;
    }

    private static boolean validarExistenciaDeInformacion(TreeMap<String, String> informacionError, String indice) {
        return !informacionError.isEmpty() && informacionError.get(indice) != null;
    }

    private static int indice(String fecha) {
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            LocalDateTime localDate = parseDateTime(fecha);
            Date date = dateFormat.parse(dateFormat2.format(localDate));
            return (int) date.getTime();
        } catch (ParseException ex) {
            Logger.getLogger(RenderizacionError.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }

    }

    private static LocalDateTime parseDateTime(String dateString) {
        for (DateTimeFormatter format : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateString, format);
            } catch (DateTimeParseException ignored) {
                // Ignored exception, try the next formatter
            }
        }
        NovusUtils.printLn("No se pudo analizar la fecha: " + dateString);
        return LocalDateTime.now();
    }

}

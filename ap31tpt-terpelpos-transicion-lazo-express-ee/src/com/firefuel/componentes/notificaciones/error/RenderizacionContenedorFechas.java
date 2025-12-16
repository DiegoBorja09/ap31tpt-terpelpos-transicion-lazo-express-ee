/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.componentes.notificaciones.error;

import com.controllers.NovusUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Devitech
 */
public class RenderizacionContenedorFechas {

    ContenedorFechas contenedorFechas;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm ");
    DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
    RenderizacionError renderizacionError;
    String iconoInformacion = "/com/firefuel/resources/informacion.png";
    String propertisEventDate = "eventDate";
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    );

    public RenderizacionContenedorFechas(ContenedorFechas contenedorFechas, RenderizacionError renderizacionError) {
        this.contenedorFechas = contenedorFechas;
        this.renderizacionError = renderizacionError;
    }

    public void renderizarHoras(JsonArray data) {
        int altura = 0;
        TreeMap<String, JsonArray> infoFechas = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        DateTimeFormatter dateFormatAm = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        for (JsonElement element : data) {
            JsonObject error = element.getAsJsonObject();
            int indice = indice(error.get(propertisEventDate).getAsString());
            String componente = error.get("eventComponent").getAsString();
            if (!validarExistenciaDeInformacion(infoFechas, indice + "-" + componente)) {
                try {
                    FechasItemsHijo fechasItemsHijo = new FechasItemsHijo();
                    JsonArray dataError = new JsonArray();
                    dataError.add(error);
                    LocalDateTime fecha = parseDateTime(error.get(propertisEventDate).getAsString());
                    String fechaObtenida = dateFormatAm.format(fecha);
                    Date date = dateFormat.parse(fechaObtenida);
                    String hora = timeFormat.format(date);
                    fechasItemsHijo.lblFechas.setText(hora);
                    this.contenedorFechas.pnlContenedorFechas.add(fechasItemsHijo);
                    altura = altura + 50;
                    this.contenedorFechas.pnlContenedorFechas.setPreferredSize(new Dimension(340, altura));
                    this.contenedorFechas.setPreferredSize(new Dimension(340, altura > 350 ? 350 : altura));
                    fechasItemsHijo.lblFechas.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent event) {
                            renderizacionError.cargarInformacion(informacion(dataError));
                        }
                    });
                    this.contenedorFechas.pnlContenedorFechas.repaint();
                    this.contenedorFechas.pnlContenedorFechas.revalidate();
                    infoFechas.put(indice + "-" + componente, dataError);
                } catch (ParseException ex) {
                    Logger.getLogger(RenderizacionContenedorFechas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private boolean validarExistenciaDeInformacion(TreeMap<String, JsonArray> informacionError, String indice) {
        return !informacionError.isEmpty() && informacionError.get(indice) != null;
    }

    public void scrollPanelPerosonalizado(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        scroll.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    }

    private JsonArray informacion(JsonArray datosError) {
        JsonArray data = new JsonArray();
        for (JsonElement element : datosError) {
            JsonObject item = new JsonObject();
            JsonObject objError = element.getAsJsonObject();
            int indice = indice(objError.get(propertisEventDate).getAsString());
            boolean resulta = objError.get("eventStatus").getAsString().equals("RESUELTA");
            String componente = objError.get("eventComponent").getAsString();
            String descripcion = "<b>Descripción</b><br/>";
            String eventfullcode = "<b>Código error:</b><br />" + objError.get("eventFullCode").getAsString().replace("__", " ").replace("_", " ") + "<br /><br />";
            item.addProperty("hora", objError.get(propertisEventDate).getAsString());
            item.addProperty("mensaje", "<html>" + eventfullcode + descripcion + objError.get("eventMessage").getAsString().concat(!resulta ? ". <br/><br/> <b>Por favor reporte los siguientes inconvenientes a soporte</b>" : "") + "</html>");
            item.addProperty("tipo", objError.get("eventType").getAsString() + " - " + componente);
            item.addProperty("componente", componente);
            item.addProperty("prioridad", objError.get("eventPriority").getAsString());
            String properti = resulta ? "eventStatus" : "eventType";
            item.addProperty("icono", rutaImagen(objError.get(properti).getAsString()));
            item.addProperty("indice", indice + "-" + componente + "-" + objError.get(properti).getAsString());
            data.add(item);
        }

        return data;
    }

    private LocalDateTime parseDateTime(String dateString) {
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

    private String rutaImagen(String prioridad) {
        String imagen;
        switch (prioridad) {
            case "FALLA":
                imagen = "/com/firefuel/resources/error-notificaion.png";
                break;
            case "ALERTA":
                imagen = iconoInformacion;
                break;
            case "INFORMACION":
                imagen = "/com/firefuel/resources/informacion.png";
                break;
            case "CORRECTO":
                imagen = "/com/firefuel/resources/marca-de-verificacion (3).png";
                break;
            case "RESUELTA":
                imagen = "/com/firefuel/resources/marca-de-verificacion (3).png";
                break;
            default:
                imagen = iconoInformacion;
        }

        return imagen;
    }

    private int indice(String fecha) {
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
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.componentes.notificaciones.error;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.notificaiones.error.ErrorNotificacionDao;
import com.firefuel.NotificacionesErrores;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Devitech
 */
public class RenderizacionError {

    NotificacionesErrores notificacionesErrores;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String urlActivo = "/com/firefuel/resources/arriba-cheuron.png";
    String urlInactivo = "/com/firefuel/resources/flecha.png";
    String fechaObtenidaPropertis = "fechaObtenida";
    TreeMap<String, JsonObject> mapaInformacionError = new TreeMap<>();
    Color activo = new Color(204, 0, 0);
    Color inactivo = new Color(244, 244, 244);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    );

    public RenderizacionError(NotificacionesErrores notificacionesErrores) {
        this.notificacionesErrores = notificacionesErrores;
    }

    public void mostrarNotificaionesError(boolean historicos, JsonArray data) {
        reset();
        informacionPrevia("<html>CARGANDO...</html>");
        Thread tarea = new Thread() {
            @Override
            public void run() {
                JsonArray temp;
                if (historicos) {
                    ErrorNotificacionDao errorNotificacionDao = new ErrorNotificacionDao();
                    JsonArray notificacionesErroresBd = errorNotificacionDao.dataNotificacionError();
                    temp = notificacionesErroresBd;
                } else {
                    temp = data;
                }
                if (NovusConstante.MAPEO_SURTIDOR.size() >0){
                    temp.addAll(NovusConstante.MAPEO_SURTIDOR.getAsJsonArray());
                }
                
                if (NovusConstante.AUTODIAGNOSTICO_SURTIDOR.size() >0){
                    temp.addAll(NovusConstante.AUTODIAGNOSTICO_SURTIDOR.getAsJsonArray());
                }
                renderizarFechas(temp);
            }
        };
        tarea.start();

    }

    public void renderizarFechas(JsonArray notificacionesErrores) {
        cargarInformacion(new JsonArray());
        reset();
        if (notificacionesErrores.size() == 0) {
            informacionPrevia("<html>NO HAY INFORMACIÓN PARA MOSTRAR</html>");
        } else {
            informacionPrevia("<html>CARGANDO...</html>");
            Thread tarea = new Thread() {
                @Override
                public void run() {
                    cargarInformacionFechas(notificacionesErrores);
                }
            };
            tarea.start();
        }

    }

    private void informacionPrevia(String mensaje) {
        JLabel label = new JLabel();
        label.setBounds(0, 235, 350, 100);
        label.setForeground(new Color(0, 0, 0));
        label.setText(mensaje);
        label.setPreferredSize(new Dimension(350, 100));
        label.setFont(new Font("Segoe UI", 1, 25));
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        label.setName("lblSinInformacion");
        this.notificacionesErrores.pnlFechas.add(label);
        this.notificacionesErrores.pnlFechas.repaint();
        this.notificacionesErrores.pnlFechas.revalidate();

    }

    private void cargarInformacionFechas(JsonArray notificacionesErrores) {
        int alturaPanelPorDefecto = 430;
        int altura = 0;
        this.notificacionesErrores.fechasScroll.getVerticalScrollBar().setUnitIncrement(20);
        this.notificacionesErrores.pnlFechas.setPreferredSize(new Dimension(370, alturaPanelPorDefecto));
        TreeMap<Integer, JsonObject> informacionOrganizada = organizarInformacion(notificacionesErrores);
        NavigableMap<Integer, JsonObject> descendingMap = informacionOrganizada.descendingMap();
        reset();
        for (Integer key : descendingMap.keySet()) {
            JsonObject info = informacionOrganizada.get(key);
            FechasPadres fechasPadres = new FechasPadres();
            fechasPadres.lblFecha.setText(info.get(fechaObtenidaPropertis).getAsString());
            ContenedorFechas contenedorFechas = new ContenedorFechas(this, info.get("data").getAsJsonArray());
            fechasPadres.pnlItem.setPreferredSize(new Dimension(335, 38));
            contenedorFechas.setVisible(false);
            this.notificacionesErrores.pnlFechas.add(fechasPadres.pnlItem);
            this.notificacionesErrores.pnlFechas.add(contenedorFechas);
            this.notificacionesErrores.pnlFechas.revalidate();
            this.notificacionesErrores.pnlFechas.repaint();
            fechasPadres.lblFecha.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    contenedorFechas.setVisible(!contenedorFechas.isVisible());
                    if (contenedorFechas.isVisible()) {
                        cambiarImagen(fechasPadres.lblImage, urlActivo);
                        agregarAltura(contenedorFechas.getPreferredSize().height + 10, alturaPanelPorDefecto, 10, true);
                        quitarAltura(obtenerALtura() + 10, 100, false);
                    } else {
                        cambiarImagen(fechasPadres.lblImage, urlInactivo);
                        quitarAltura(contenedorFechas.getPreferredSize().height + 10, 0, true);
                    }
                }
            });
            int alturaTemp = altura + 48;
            altura = alturaTemp;
            agregarAltura(alturaTemp, alturaPanelPorDefecto, 10, false);
        }

        quitarAltura(altura, 002, false);
    }

    private int obtenerALtura() {
        return this.notificacionesErrores.pnlFechas.getPreferredSize().height;
    }

    private void reset() {
        this.notificacionesErrores.pnlFechas.removeAll();
        this.notificacionesErrores.pnlFechas.repaint();
        this.notificacionesErrores.pnlFechas.revalidate();
    }

    public void activarYDesactivar(JPanel panel, JLabel label) {
        resetBotones();
        if (panel.getBackground().equals(activo)) {
            panel.setBackground(inactivo);
            label.setForeground(new Color(51, 51, 51));
        } else {
            panel.setBackground(activo);
            label.setForeground(new Color(255, 255, 255));
        }
    }

    private void resetBotones() {
        this.notificacionesErrores.btnHistorico.setBackground(inactivo);
        this.notificacionesErrores.btnReciente.setBackground(inactivo);
        this.notificacionesErrores.HIstorico.setForeground(new Color(51, 51, 51));
        this.notificacionesErrores.lblReciente.setForeground(new Color(51, 51, 51));
    }

    private TreeMap<Integer, JsonObject> organizarInformacion(JsonArray informacionError) {
        TreeMap<Integer, JsonObject> informacionOrganizado = new TreeMap<>();
        for (JsonElement element : informacionError) {
            JsonObject data = element.getAsJsonObject();
            if (data.size() > 0) {
                LocalDateTime fecha = parseDateTime(data.get("eventDate").getAsString());
                String fechaObtenida = validarFechas(fecha);
                if (!informacionOrganizado.isEmpty() && informacionOrganizado.get(indice(fecha)) != null) {
                    JsonObject info = informacionOrganizado.get(indice(fecha));
                    JsonObject objInfo = new JsonObject();
                    JsonArray error = info.get("data").getAsJsonArray();
                    error.add(data);
                    objInfo.addProperty(fechaObtenidaPropertis, info.get(fechaObtenidaPropertis).getAsString());
                    objInfo.add("data", error);
                    informacionOrganizado.remove(indice(fecha));
                    informacionOrganizado.put(indice(fecha), objInfo);
                } else {
                    JsonArray error = new JsonArray();
                    JsonObject info = new JsonObject();
                    error.add(data);
                    info.addProperty(fechaObtenidaPropertis, fechaObtenida);
                    info.add("data", error);
                    indice(fecha);
                    informacionOrganizado.put(indice(fecha), info);
                }
            }
        }
        return informacionOrganizado;
    }

    private void agregarAltura(int altura, int alturaPorDefecto, int porcentaje, boolean soloUnComponente) {
        if (soloUnComponente) {
            int alturaFianl = this.notificacionesErrores.pnlFechas.getPreferredSize().height + altura;
            this.notificacionesErrores.pnlFechas.setPreferredSize(new Dimension(370, alturaFianl));
        } else {
            if (altura > alturaPorDefecto) {
                int valorAdicional = obtenerPorcentajeAltura(porcentaje);
                int alturaFianl = altura + valorAdicional;
                this.notificacionesErrores.pnlFechas.setPreferredSize(new Dimension(370, alturaFianl));
            }
        }
    }

    private void quitarAltura(int altura, int porcentaje, boolean soloUnComponente) {
        if (soloUnComponente) {
            int alturaFianl = this.notificacionesErrores.pnlFechas.getPreferredSize().height - altura;
            this.notificacionesErrores.pnlFechas.setPreferredSize(new Dimension(370, alturaFianl));
        } else {
            if (this.notificacionesErrores.pnlFechas.getPreferredSize().height > altura) {
                int valorAdicional = obtenerPorcentajeAltura(porcentaje);
                int alturaFianl = altura - valorAdicional;
                this.notificacionesErrores.pnlFechas.setPreferredSize(new Dimension(370, alturaFianl));
            }
        }
    }

    private int obtenerPorcentajeAltura(int porcentaje) {
        return (int) Math.round(this.notificacionesErrores.pnlFechas.getPreferredSize().height * (porcentaje / 100));
    }

    private String validarFechas(LocalDateTime fecha) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        if (fecha.truncatedTo(ChronoUnit.DAYS).isEqual(now)) {
            return "HOY";
        } else if (fecha.truncatedTo(ChronoUnit.DAYS).isEqual(now.minusDays(1))) {
            return "AYER";
        } else {
            return fecha.format(formatter);
        }
    }

    private int indice(LocalDateTime fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(fecha.format(formatter));
            return (int) date.getTime();
        } catch (ParseException ex) {
            Logger.getLogger(RenderizacionError.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }

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

    private void cambiarImagen(JLabel label, String url) {
        label.setIcon(new ImageIcon(getClass().getResource(url)));
    }

    public void cargarInformacion(JsonArray info) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
        if (this.notificacionesErrores.pnlInformacionContenedor.getComponentCount() == 0 && mapaInformacionError.isEmpty()) {
            JLabel label = new JLabel();
            label.setBounds(0, 235, 635, 100);
            label.setForeground(new Color(0, 0, 0));
            label.setText("NO HAY INFORMACIÓN PARA MOSTRAR");
            label.setPreferredSize(new Dimension(365, 100));
            label.setFont(new Font("Segoe UI", 1, 30));
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
            this.notificacionesErrores.pnlInformacionContenedor.add(label);
            this.notificacionesErrores.pnlInformacionContenedor.repaint();
            this.notificacionesErrores.pnlInformacionContenedor.revalidate();
        } else {
            for (JsonElement element : info) {
                JsonObject data = element.getAsJsonObject();
                if (mapaInformacionError.isEmpty()) {
                    this.notificacionesErrores.pnlInformacionContenedor.removeAll();
                }
                try {
                    if (!validarExistenciaDeInformacion(mapaInformacionError, data)) {
                        this.notificacionesErrores.scrollContenedorInformacion.getVerticalScrollBar().setValue(0);
                        InformacionError informacionError = new InformacionError();
                        informacionError.lblTitulo.setText(data.get("tipo").getAsString().toUpperCase());
                        informacionError.lblInformacion.setText(data.get("mensaje").getAsString());
                        cambiarImagen(informacionError.lblIconoInformacionError, data.get("icono").getAsString());
                        String fechaObtenida = dateFormat2.format(parseDateTime(data.get("hora").getAsString()));
                        Date date = dateFormat.parse(fechaObtenida);
                        String timeString = timeFormat.format(date);
                        informacionError.lblHora.setText(timeString);
                        this.notificacionesErrores.pnlInformacionContenedor.add(Box.createVerticalStrut(10), 0);
                        this.notificacionesErrores.pnlInformacionContenedor.add(informacionError.contenedorInformacionError, 0);
                        this.notificacionesErrores.pnlInformacionContenedor.repaint();
                        this.notificacionesErrores.pnlInformacionContenedor.revalidate();
                        mapaInformacionError.put(data.get("indice").getAsString(), data);
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(RenderizacionError.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private boolean validarExistenciaDeInformacion(TreeMap<String, JsonObject> informacionError, JsonObject data) {
        return !informacionError.isEmpty() && informacionError.get(data.get("indice").getAsString()) != null;
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

}

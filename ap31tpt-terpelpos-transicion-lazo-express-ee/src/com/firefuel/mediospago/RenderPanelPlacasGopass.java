package com.firefuel.mediospago;

import com.firefuel.Main;
import com.firefuel.mediospago.dto.ValidacionDataVehiculo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RenderPanelPlacasGopass {

    private PanelItemPlaca panelSeleccionadoAnterior;

    private final Icon placaActivo = new ImageIcon(getClass().getResource("/com/firefuel/resources/fndItemPlaca.png"));
    private final Icon placaSeleccionada = new ImageIcon(getClass().getResource("/com/firefuel/resources/fndPlacaSeleccionada.png"));
    private final Color colorPlacaActivo = new Color(255, 255, 255);
    private final Color colorPlacaSeleccionado = new Color(192, 0, 0);
    private static String valorPlacaSeleccionada = "";
    private static String nombreCliente = "";

    public void renderPlacasGopass(JPanel panelSeleccionPlacas, JsonArray informacionPlacas) {
        PanelPlacasGopass panelPlacas = new PanelPlacasGopass();

        int panelHeight = panelPlacas.getHeight();

        if (informacionPlacas != null) {

            try {

                int j = 0, i = 0;

                int hgap = 10;
                int vgap = 10;

                final int componentHeight = 194;
                final int componentWidth = 70;
                final int offset = 2;
                final int panelWidth = 1033 - offset;
                final int ncols = panelWidth / componentWidth;
                final int availableWidth = panelWidth / ncols;

                PanelPlacasGopass.pnlContainerPlacaGopass.setLayout(new FlowLayout(FlowLayout.LEFT, hgap, vgap));

                for (JsonElement informacionPlaca : informacionPlacas) {
                    JsonObject data = informacionPlaca.getAsJsonObject();
                    ValidacionDataVehiculo validacion = Main.gson.fromJson(data, ValidacionDataVehiculo.class);

                    PanelItemPlaca itemPlaca = new PanelItemPlaca(validacion.getPlaca(), validacion.getNombreUsuario());
                    itemPlaca.setBounds((j * availableWidth + (offset / 2)),
                            ((i * componentHeight) + (offset * (i + 1))), componentWidth, componentHeight);
                    j++;
                    if (j == (ncols)) {
                        j = 0;
                        i++;
                    }

                    itemPlaca.fndItemPlaca.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            cambiarIconoPlacaActivo(itemPlaca);
                            setValorPlacaSeleccionada(itemPlaca.getPlaca());
                            setNombreCliente(itemPlaca.getCliente());
                            activarDesactivar(e, itemPlaca);
                        }
                    });

                    PanelPlacasGopass.pnlContainerPlacaGopass.add(itemPlaca);
                }
                panelHeight = Math.max(panelHeight, ((componentHeight * i) + (offset * (i + 1))));
                panelPlacas.setPreferredSize(new Dimension(1033, panelHeight));
                panelSeleccionPlacas.add("pnlPlacas", panelPlacas);
            } catch (Exception ex) {
                Logger.getLogger(RenderPanelPlacasGopass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void activarDesactivar(java.awt.event.MouseEvent evt, PanelItemPlaca item) {
        JLabel label = (JLabel) evt.getComponent();
        label.setIcon(placaSeleccionada);
        item.lblPlaca.setForeground(colorPlacaSeleccionado);
    }

    public void cambiarIconoPlacaActivo(PanelItemPlaca item) {
        if (panelSeleccionadoAnterior != null && panelSeleccionadoAnterior != item) {
            panelSeleccionadoAnterior.fndItemPlaca.setIcon(placaActivo);
            panelSeleccionadoAnterior.lblPlaca.setForeground(colorPlacaActivo);
        }
        panelSeleccionadoAnterior = item;
    }

    public static String getValorPlacaSeleccionada() {
        return valorPlacaSeleccionada;
    }

    public static void setValorPlacaSeleccionada(String aValorPlacaSeleccionada) {
        valorPlacaSeleccionada = aValorPlacaSeleccionada;
    }

    public static String getNombreCliente() {
        return nombreCliente;
    }

    public static void setNombreCliente(String aNombreCliente) {
        nombreCliente = aNombreCliente;
    }

}
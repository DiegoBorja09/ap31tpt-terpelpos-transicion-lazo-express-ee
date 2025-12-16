package com.firefuel.mediospago;

import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.controllers.NovusConstante;
import com.neo.app.bean.Manguera;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RenderPanelsMediosPago {

    private PanelItemMedioPago panelSeleccionadoAnterior;

    private long idMedioPago = 0;
    private String descripcionMedioPago = "";

    private final Icon panelMedioActivo = new ImageIcon(getClass().getResource("/com/firefuel/resources/panelMedioPago.png"));
    private final Icon panelMedioSeleccionado = new ImageIcon(getClass().getResource("/com/firefuel/resources/panelMedioPagoSeleccionado.png"));

    private String medioActivo = "";
    private String medioSeleccionado = "";

    private final Color colorMedioActivo = new Color(255, 255, 255);
    private final Color colorMedioSeleccionado = new Color(192, 0, 0);

    String imgMedio = "/com/firefuel/resources/datafonoActivo.png";

    int numeroPanel;
    Manguera manguera;
    PanelSeleccionPlacas panelPlacas = new PanelSeleccionPlacas();

    private ArrayList<Long> mediosDeshabilitados = new ArrayList<>();

    public void createPanelsMediosPago(ArrayList<MedioPagoImageBean> mediosPagos, JPanel pnlContenedorMediosPago, Manguera manguera) {
        createPanelsMediosPago(mediosPagos, pnlContenedorMediosPago, manguera, new ArrayList<>());
    }

    public void createPanelsMediosPago(ArrayList<MedioPagoImageBean> mediosPagos, JPanel pnlContenedorMediosPago, Manguera manguera, ArrayList<Long> mediosDeshabilitados) {
        this.manguera = manguera;
        this.mediosDeshabilitados = mediosDeshabilitados;
        numeroPanel = 0;
        final int grupoMedios = 8;
        int inicioIndexMedios = 0;
        int finIndexMedios = Math.min(grupoMedios, mediosPagos.size());

        while (inicioIndexMedios < mediosPagos.size()) {
            ArrayList<MedioPagoImageBean> grupo = new ArrayList<>(mediosPagos.subList(inicioIndexMedios, finIndexMedios));

            numeroPanel++;
            renderPanelMediosPago(grupo, pnlContenedorMediosPago, panelPlacas);

            inicioIndexMedios += grupoMedios;
            finIndexMedios = Math.min(inicioIndexMedios + grupoMedios, mediosPagos.size());
        }

    }

    public void renderPanelMediosPago(ArrayList<MedioPagoImageBean> mediosPagos, JPanel pnlContenedorMediosPago, PanelSeleccionPlacas panelPlacas) {

        PanelFlowMediosPago pnlFlowMediosPago = new PanelFlowMediosPago();

        int panelHeight = pnlFlowMediosPago.getHeight();
        if (mediosPagos != null) {
            int j = 0, i = 0;

            int hgap = 20;
            int vgap = 34;

            final int componentHeight = 228;
            final int componentWidth = 216;
            final int offset = 40;
            final int panelWidth = 1040 - offset;
            final int ncols = panelWidth / componentWidth;
            final int availableWidth = panelWidth / ncols;

            pnlFlowMediosPago.setLayout(new FlowLayout(FlowLayout.CENTER, hgap, vgap));

            for (MedioPagoImageBean medio : mediosPagos) {

                PanelItemMedioPago itemMedioPago = new PanelItemMedioPago(medio.getId(), medio.getDescripcion(),
                        medio.getImagePathUnchecked(), medio.getImagePathChecked());

                itemMedioPago.setBounds((j * availableWidth + (offset / 2)),
                        ((i * componentHeight) + (offset * (i + 1))), componentWidth, componentHeight);
                j++;
                if (j == (ncols)) {
                    j = 0;
                    i++;
                }

                // 🔍 VALIDACIÓN GLP: Deshabilitar medio si está en la lista de deshabilitados
                boolean estaDeshabilitado = mediosDeshabilitados.contains(medio.getId());
                
                if (estaDeshabilitado) {
                    // Deshabilitar visualmente el panel
                    itemMedioPago.setEnabled(false);
                    itemMedioPago.fndMedioPago.setEnabled(false);
                    itemMedioPago.imgMedio.setEnabled(false);
                    itemMedioPago.lblNombreMedioPago.setEnabled(false);
                    itemMedioPago.setOpaque(false);
                    // Cambiar opacidad visual
                    itemMedioPago.fndMedioPago.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/com/firefuel/resources/panelMedioPago.png"))
                        .getImage().getScaledInstance(216, 228, java.awt.Image.SCALE_SMOOTH)));
                }

                itemMedioPago.fndMedioPago.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        // 🔍 VALIDACIÓN GLP: No permitir clic si está deshabilitado
                        if (mediosDeshabilitados.contains(itemMedioPago.getIdMedioPago())) {
                            return; // No hacer nada si está deshabilitado
                        }

                        cambiarIconoMedioActivo(itemMedioPago);

                        setIdMedioPago(itemMedioPago.getIdMedioPago());
                        setDescripcionMedioPago(itemMedioPago.getDescripcionMedioPago());
                        medioActivo = itemMedioPago.getFndMedioActivo();
                        medioSeleccionado = itemMedioPago.getFndMedioSeleccionado();

                        activarDesactivar(e, pnlContenedorMediosPago, itemMedioPago);

                        mostrarPanelPlacas(panelPlacas);
                        cancelarConsultaPlacas(panelPlacas);
                    }
                });

                pnlFlowMediosPago.add(itemMedioPago);
            }
            panelHeight = Math.max(panelHeight, ((componentHeight * i) + (offset * (i + 1))));
            pnlFlowMediosPago.setPreferredSize(new Dimension(1040, panelHeight));
            pnlContenedorMediosPago.add("pnl" + numeroPanel, pnlFlowMediosPago);
        }
    }

    public void cambiarIconoMedioActivo(PanelItemMedioPago item) {
        if (panelSeleccionadoAnterior != null && panelSeleccionadoAnterior != item) {
            panelSeleccionadoAnterior.imgMedio.removeAll();
            panelSeleccionadoAnterior.imgMedio.setIcon(new ImageIcon(medioActivo));
        }
        panelSeleccionadoAnterior = item;
    }

    private void mostrarPanelPlacas(PanelSeleccionPlacas panelPlacas) {
        PanelMediosPago.mostrarPanelPlacas((idMedioPago == NovusConstante.ID_MEDIO_GOPASS), manguera, panelPlacas);
    }

    private void cancelarConsultaPlacas(PanelSeleccionPlacas panelPlacas) {
        if (idMedioPago != NovusConstante.ID_MEDIO_GOPASS) {
            panelPlacas.cancelarConsultaPlacas();
        }
    }

    private void activarDesactivar(java.awt.event.MouseEvent evt, JPanel panelContenedor, PanelItemMedioPago item) {
        resetComponents(panelContenedor);
        JLabel label = (JLabel) evt.getComponent();
        label.setIcon(panelMedioSeleccionado);
        item.lblNombreMedioPago.setForeground(colorMedioSeleccionado);
        item.imgMedio.setIcon(new ImageIcon(medioSeleccionado));
    }

    private void resetComponents(JPanel panelContenedor) {
        for (Component component : panelContenedor.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component componenteLabel : panel.getComponents()) {
                    JPanel itemMedio = (JPanel) componenteLabel;
                    for (Component componenteItem : itemMedio.getComponents()) {
                        JLabel label = (JLabel) componenteItem;
                        resetFondoDescripcion(label);
                    }
                }
            }
        }
    }

    private void resetFondoDescripcion(JLabel label) {
        if (label.getName() != null) {
            if (label.getName().equals("fndMedioPago")) {
                label.setIcon(panelMedioActivo);
            }
            if (label.getName().equals("lblNombreMedio")) {
                label.setForeground(colorMedioActivo);
            }
        }
    }

    public long getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(long aIdMedioPago) {
        idMedioPago = aIdMedioPago;
    }

    public String getDescripcionMedioPago() {
        return descripcionMedioPago;
    }

    public void setDescripcionMedioPago(String aDescripcionMedioPago) {
        descripcionMedioPago = aDescripcionMedioPago;
    }

}

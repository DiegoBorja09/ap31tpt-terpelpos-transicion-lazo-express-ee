/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.application.useCases.facturacion.ObtenerMotivosAnulacionUseCase;
import com.controllers.NovusConstante;
import com.dao.MovimientosDao;
import com.firefuel.ConfirmarAnulacionView;
import com.firefuel.components.panelesPersonalizados.BordesRedondos;
import com.firefuel.components.panelesPersonalizados.PanelRedondo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.utils.enums.TipoAnulacion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Devitech
 */
public class MotivosAnulacion {

    JPanel panel;
    ConfirmarAnulacionView anulacionView;
    boolean combustible = false;

    public MotivosAnulacion(ConfirmarAnulacionView confirmarAnulacionView) {
        this.anulacionView = confirmarAnulacionView;
    }
    
    public MotivosAnulacion(ConfirmarAnulacionView confirmarAnulacionView, boolean combustible) {
        this.anulacionView = confirmarAnulacionView;
        this.combustible = combustible;
    }

    public void cargarMotivosAnulacion(JPanel panel) {
        this.panel = panel;
        MovimientosDao mdao = new MovimientosDao();
        //JsonArray motivos = mdao.motivosAnulacion();
        JsonArray motivos = new ObtenerMotivosAnulacionUseCase().execute();
        panel.setPreferredSize(new Dimension(1220, 490));
        for (JsonElement elemt : motivos) {
            JsonObject btnmotivos = elemt.getAsJsonObject();
            PanelRedondo botonMotivoAnulacion = new PanelRedondo();
            JLabel lblMotivo = new JLabel("<html><center>" + btnmotivos.get("descripcion").getAsString() + "</center></html>");
            int codigo = btnmotivos.get("codigo").getAsInt();
            lblMotivo.setFont(new java.awt.Font("Roboto", 1, 18));
            lblMotivo.setHorizontalTextPosition(SwingConstants.CENTER);
            lblMotivo.setHorizontalAlignment(SwingConstants.CENTER);
            lblMotivo.setVerticalAlignment(SwingConstants.CENTER);
            lblMotivo.setVerticalTextPosition(SwingConstants.CENTER);
            lblMotivo.setForeground(new Color(153, 0, 0));
            botonMotivoAnulacion.setBackground(Color.WHITE);
            botonMotivoAnulacion.setPreferredSize(new Dimension(250, 150));
            botonMotivoAnulacion.setRoundBottomLeft(10);
            botonMotivoAnulacion.setRoundBottomRight(20);
            botonMotivoAnulacion.setRoundTopLeft(20);
            botonMotivoAnulacion.setRoundTopRight(20);
            GroupLayout groupLayout = new GroupLayout(botonMotivoAnulacion);
            botonMotivoAnulacion.setLayout(groupLayout);
            groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addGroup(groupLayout.createSequentialGroup()
                            .addGap(5, 20, 20)
                            .addComponent(lblMotivo, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                            .addContainerGap(20, Short.MAX_VALUE)));

            groupLayout.setVerticalGroup(groupLayout.createParallelGroup()
                    .addGroup(groupLayout.createSequentialGroup()
                            .addGap(5, 20, 20)
                            .addComponent(lblMotivo, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addContainerGap(20, Short.MAX_VALUE)));

            botonMotivoAnulacion.setBorder(new BordesRedondos(new Color(153, 0, 0), 20, 0, 0, 0, 0));
            lblMotivo.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    NovusConstante.TIPO_ANULACION_NOTA_CREDITO = codigo;
                    if (codigo == TipoAnulacion.DEVOLUCION_DE_BIENES_O_PARTES) {
                        anulacionView.jLabel4.setText("SIGUIENTE");
                    } else {
                        anulacionView.jLabel4.setText("ANULAR");
                    }
                    reset();
                    activarDesactivar(botonMotivoAnulacion);
                }
            });

            panel.add(botonMotivoAnulacion);
            panel.revalidate();
            panel.repaint();
        }

    }

    private void activarDesactivar(Component component) {
        PanelRedondo panels = ((PanelRedondo) component);
        panels.setBackground(new Color(153, 0, 0));
        for (Component components : panels.getComponents()) {
            ((JLabel) components).setForeground(Color.WHITE);
        }
    }

    private void reset() {
        for (Component component : panel.getComponents()) {
            PanelRedondo panels = ((PanelRedondo) component);
            panels.setBackground(Color.WHITE);
            for (Component components : panels.getComponents()) {
                ((JLabel) components).setForeground(new Color(153, 0, 0));
            }

        }
    }
}

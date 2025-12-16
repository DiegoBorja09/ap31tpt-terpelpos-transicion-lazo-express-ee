/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.controllers.NovusUtils;
import javax.swing.Icon;

/**
 *
 * @author Devitech
 */
public class ItemPlacaGopass extends javax.swing.JPanel {

    PlacaGopass placa;
    boolean isSelected;
    GoPassMenuController panel;
    private final Icon BG_PLACA = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btPromotor.png"));
    private final Icon BG_PLACA_ELEGIDA = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btPressPromotor.png"));

    public ItemPlacaGopass(PlacaGopass placa, boolean isSelected) {
        initComponents();
        this.placa = placa;
        this.isSelected = isSelected;
        init();
    }
    
    public ItemPlacaGopass(GoPassMenuController parent, PlacaGopass placa, boolean isSelected) {
        initComponents();
        this.placa = placa;
        this.panel = parent;
        this.isSelected = isSelected;
        init();
    }

    private void init() {
        nro_placa.setText(this.placa.getPlaca());
        nombre_usuario.setText(this.placa.getNombreUsuario());
    }

    public void setBgColor() {
        if (isSelected) {
            panel.SetItemPlacaSelected(this);
            jLabel1.setIcon(BG_PLACA_ELEGIDA);
        } else {
            panel.SetItemPlacaSelected(null);
            jLabel1.setIcon(BG_PLACA);
        }
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        setBgColor();
    }

    public PlacaGopass getPlaca() {
        return this.placa;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nro_placa = new javax.swing.JLabel();
        nombre_usuario = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(0, 0));
        setLayout(null);

        nro_placa.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        nro_placa.setForeground(new java.awt.Color(255, 255, 255));
        nro_placa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nro_placa.setText("NRO PLACA");
        add(nro_placa);
        nro_placa.setBounds(20, 10, 180, 80);

        nombre_usuario.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        nombre_usuario.setForeground(new java.awt.Color(255, 255, 255));
        nombre_usuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nombre_usuario.setText("NOMBRE COMPLETO USUARIO");
        add(nombre_usuario);
        nombre_usuario.setBounds(230, 10, 500, 80);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/Recurso 38.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        add(jLabel2);
        jLabel2.setBounds(910, 30, 50, 40);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btPromotor.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        add(jLabel1);
        jLabel1.setBounds(0, 0, 1080, 100);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseReleased
        NovusUtils.beep();
        if (this.panel.ItemPlacaSelected != null) {
            this.panel.ItemPlacaSelected.setSelected(false);
        }
        setSelected(true);
        panel.btnSiguiente.setVisible(true);
    }//GEN-LAST:event_jLabel1MouseReleased

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseReleased
//        seleccionarPlaca();
    }//GEN-LAST:event_jLabel2MouseReleased

//    public void FinalizarSeleccionPlaca() {
//       parent.OverSelectPlaca(this.getPlaca());
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel nombre_usuario;
    private javax.swing.JLabel nro_placa;
    // End of variables declaration//GEN-END:variables

}

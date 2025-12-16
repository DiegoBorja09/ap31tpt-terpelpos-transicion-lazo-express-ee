package com.firefuel;

import com.bean.MedioIdentificacionBean;
import com.controllers.NovusUtils;

public class MedioIdentificacionItem extends javax.swing.JPanel {

    private final MedioIdentificacionBean model;
    RumboView parentRumboView = null;
    RecuperacionVentaView parentRecuperacionView = null;

    public MedioIdentificacionItem(RumboView parentRumboView, MedioIdentificacionBean model) {
        initComponents();
        this.model = model;
        this.parentRumboView = parentRumboView;
        this.init();
    }

    public MedioIdentificacionItem(RecuperacionVentaView parentRumboView, MedioIdentificacionBean model) {
        initComponents();
        this.model = model;
        this.parentRecuperacionView = parentRumboView;
        this.init();
    }

    void handleClick() {
        if (parentRumboView != null) {
            parentRumboView.listenIdentifiersClick(getModel());
        } else {
            parentRecuperacionView.listenIdentifiersClick(getModel());
        }
    }

    void handlePress() {
        NovusUtils.beep();
        try {
            this.background
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btGris.png"))); // NOI18N
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }

    private void init() {
        this.loadComponent();
    }

    void renderIdentifierData() {
        MedioIdentificacionBean mid = this.getModel();
        if (mid != null) {
            this.lbl_identifier_name.setText("<html>" + mid.getDescripcion() + "</html>");
            try {
                this.lbl_identifier_image.setIcon(new javax.swing.ImageIcon(
                        getClass().getResource("/com/firefuel/resources/" + mid.getUrlImagen()))); // NOI18N
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
        }
    }

    void loadComponent() {
        this.renderIdentifierData();
    }

    public MedioIdentificacionBean getModel() {
        return this.model;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_identifier_name = new javax.swing.JLabel();
        lbl_identifier_image = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setOpaque(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        setLayout(null);

        lbl_identifier_name.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lbl_identifier_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_identifier_name.setText("NOMBRE IDENTIFICADOR");
        lbl_identifier_name.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        add(lbl_identifier_name);
        lbl_identifier_name.setBounds(300, 20, 190, 220);
        add(lbl_identifier_image);
        lbl_identifier_image.setBounds(52, 0, 440, 250);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btBlanco.png"))); // NOI18N
        add(background);
        background.setBounds(0, 0, 502, 252);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseReleased
        try {
            this.background
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btBlanco.png"))); // NOI18N
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
    }// GEN-LAST:event_formMouseReleased

    private void formMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseClicked
        this.handleClick();
    }// GEN-LAST:event_formMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMousePressed
        this.handlePress();
    }// GEN-LAST:event_formMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel lbl_identifier_image;
    private javax.swing.JLabel lbl_identifier_name;
    // End of variables declaration//GEN-END:variables
}

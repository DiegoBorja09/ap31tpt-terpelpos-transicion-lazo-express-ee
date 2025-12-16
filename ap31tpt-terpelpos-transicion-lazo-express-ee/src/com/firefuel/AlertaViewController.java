package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

public class AlertaViewController extends javax.swing.JPanel {

    public AlertaViewController() {
        initComponents();
        NovusUtils.ajusteFuente(getComponents(), NovusConstante.EXTRABOLD);
    }

    public void setLabel(String texto) {
        //jLabel2.setText(texto);
    }

    public void setValores(String nombre) {
        // jLabel2.setText(nombre.trim().toUpperCase());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/notifySincro.png"))); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(0, 0, 520, 120);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

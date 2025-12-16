package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

public class EstadoRfidViewController extends javax.swing.JPanel {

    boolean isIbutton;

    public EstadoRfidViewController(boolean isIbutton) {
        initComponents();
        this.isIbutton = isIbutton;
        NovusUtils.ajusteFuente(jLabel2, NovusConstante.EXTRABOLD);
    }

    public void setLabel(String texto) {
        jLabel2.setText(texto);
    }

    public void setValores(String nombre, boolean isButton) {
        if (isButton) {
            jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ibuttonNombre.png")));
        }
        jLabel2.setText(nombre.trim().toUpperCase());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        setOpaque(false);
        setLayout(null);
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 38)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        add(jLabel2);
        jLabel2.setBounds(120, 20, 380, 50);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/rfidNombre.png"))); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(0, 0, 510, 90);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}

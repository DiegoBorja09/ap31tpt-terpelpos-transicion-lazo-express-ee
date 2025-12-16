package com.firefuel.mediospago;

import javax.swing.ImageIcon;

public class PanelItemMedioPago extends javax.swing.JPanel {

    private long idMedioPago;
    private String descripcionMedioPago;
    private String fndMedioSeleccionado;
    private String fndMedioActivo;

    public String getFndMedioActivo() {
        return fndMedioActivo;
    }

    public void setFndMedioActivo(String fndMedioActivo) {
        this.fndMedioActivo = fndMedioActivo;
    }

    public PanelItemMedioPago() {
        initComponents();
    }

    public PanelItemMedioPago(long idMedioPago, String descripcionMedioPago, String fndMedioActivo, String fndMedioSeleccionado) {
        this.idMedioPago = idMedioPago;
        this.descripcionMedioPago = descripcionMedioPago;
        this.fndMedioActivo = fndMedioActivo;
        this.fndMedioSeleccionado = fndMedioSeleccionado;
        initComponents();
        loadComponents();
    }

    public void loadComponents() {
        fndMedioPago.setName("fndMedioPago");
        lblNombreMedioPago.setName("lblNombreMedio");
        imgMedio.setName("imgMedio");
        lblNombreMedioPago.setText(descripcionMedioPago);
        imgMedio.setIcon(new ImageIcon(fndMedioActivo));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imgMedio = new javax.swing.JLabel();
        lblNombreMedioPago = new javax.swing.JLabel();
        fndMedioPago = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(216, 228));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(216, 228));
        setLayout(null);

        imgMedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(imgMedio);
        imgMedio.setBounds(0, 19, 206, 124);

        lblNombreMedioPago.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblNombreMedioPago.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreMedioPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombreMedioPago.setText("NOMBRE");
        add(lblNombreMedioPago);
        lblNombreMedioPago.setBounds(0, 168, 206, 30);

        fndMedioPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndMedioPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/panelMedioPago.png"))); // NOI18N
        fndMedioPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndMedioPago.setMaximumSize(new java.awt.Dimension(216, 228));
        fndMedioPago.setMinimumSize(new java.awt.Dimension(216, 228));
        fndMedioPago.setPreferredSize(new java.awt.Dimension(216, 228));
        add(fndMedioPago);
        fndMedioPago.setBounds(0, 0, 216, 228);

        getAccessibleContext().setAccessibleName("Pruebaaa");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel fndMedioPago;
    public javax.swing.JLabel imgMedio;
    public javax.swing.JLabel lblNombreMedioPago;
    // End of variables declaration//GEN-END:variables

    public long getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(long idMedioPago) {
        this.idMedioPago = idMedioPago;
    }

    public String getDescripcionMedioPago() {
        return descripcionMedioPago;
    }

    public void setDescripcionMedioPago(String descripcionMedioPago) {
        this.descripcionMedioPago = descripcionMedioPago;
    }

    public String getFndMedioSeleccionado() {
        return fndMedioSeleccionado;
    }

    public void setFndMedioSeleccionado(String fndMedioSeleccionado) {
        this.fndMedioSeleccionado = fndMedioSeleccionado;
    }

}

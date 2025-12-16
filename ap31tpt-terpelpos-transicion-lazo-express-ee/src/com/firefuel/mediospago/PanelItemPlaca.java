package com.firefuel.mediospago;

public class PanelItemPlaca extends javax.swing.JPanel {

    private String placa;
    private String cliente;

    public PanelItemPlaca(String placa, String cliente) {
        this.placa = placa;
        this.cliente = cliente;
        initComponents();
        loadComponents();
    }

    public void loadComponents() {
        fndItemPlaca.setName("fndItemPlaca");
        lblPlaca.setName("lblPlaca");
        lblPlaca.setText(placa);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPlaca = new javax.swing.JLabel();
        fndItemPlaca = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(190, 74));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(190, 74));
        setLayout(null);

        lblPlaca.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblPlaca.setForeground(new java.awt.Color(255, 255, 255));
        lblPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPlaca.setText("ABC123");
        lblPlaca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPlaca.setMaximumSize(new java.awt.Dimension(190, 74));
        lblPlaca.setMinimumSize(new java.awt.Dimension(190, 74));
        lblPlaca.setPreferredSize(new java.awt.Dimension(190, 74));
        add(lblPlaca);
        lblPlaca.setBounds(0, 0, 190, 74);

        fndItemPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndItemPlaca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndItemPlaca.png"))); // NOI18N
        fndItemPlaca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(fndItemPlaca);
        fndItemPlaca.setBounds(0, 0, 190, 74);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel fndItemPlaca;
    public javax.swing.JLabel lblPlaca;
    // End of variables declaration//GEN-END:variables

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

}

package com.firefuel;

public class EstadoNotificacionesViewController extends javax.swing.JPanel {

    boolean despachoIniciado = false;

    public void setDespachoIniciado(boolean despachoIniciado) {
        this.despachoIniciado = despachoIniciado;
    }

    public boolean getDespachoIniciado() {
        return this.despachoIniciado;
    }

    public EstadoNotificacionesViewController() {
        initComponents();
        jcara.setFont(new java.awt.Font("Bebas Neue", 0, 36));
        jcliente.setFont(new java.awt.Font("Bebas Neue", 0, 22));
        jplaca.setFont(new java.awt.Font("Bebas Neue", 0, 50));

    }

    public void setLabel(String texto) {
        //jLabel2.setText(texto);
    }

    public void setValores(String placa, String cliente, String cara) {
        jplaca.setText(placa);
        jcliente.setText(cliente);
        jcara.setText(cara);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jplaca = new javax.swing.JLabel();
        jcliente = new javax.swing.JLabel();
        jcara = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jplaca.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jplaca.setForeground(new java.awt.Color(255, 255, 255));
        jplaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(jplaca);
        jplaca.setBounds(30, 10, 340, 60);

        jcliente.setForeground(new java.awt.Color(255, 255, 255));
        jcliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(jcliente);
        jcliente.setBounds(20, 84, 420, 50);

        jcara.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jcara.setForeground(new java.awt.Color(223, 26, 9));
        jcara.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jcara.setText("1");
        add(jcara);
        jcara.setBounds(380, 8, 64, 56);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndCardRumbo.png"))); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(0, 0, 464, 154);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jcara;
    private javax.swing.JLabel jcliente;
    private javax.swing.JLabel jplaca;
    // End of variables declaration//GEN-END:variables
}

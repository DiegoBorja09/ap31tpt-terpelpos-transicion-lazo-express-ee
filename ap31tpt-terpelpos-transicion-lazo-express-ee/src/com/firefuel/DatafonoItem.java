/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.controllers.NovusConstante;

/**
 *
 * @author USUARIO
 */
public class DatafonoItem extends javax.swing.JPanel {

    String estado;
    String serial;
    String plaqueta;
    String proveedor;
    String terminal;
    int numberDatafono;
    int idAdquiriente;

    public void loadComponents() {
        initComponents();
        estado = NovusConstante.DATAFONO_INACTIVO;
        jLabel2.setName("tituloPlaqueta");
        txtDatafono.setText(proveedor);
        txtDatafono.setName("proveedorDatafono");
        String numeroPlaqueta = getUltimosCaracteres(plaqueta, 4);
        txtPlaqueta.setText(numeroPlaqueta + "");
        txtPlaqueta.setName("plaqueta");
        number.setText(numberDatafono + "");
        number.setName("numeroDatafono");
        this.background.setName("fondo");
    }

    public DatafonoItem(int number, int idAquiriente, String serial, String plaqueta, String estado, String proveedor, String terminal) {
        this.numberDatafono = number;
        this.serial = serial;
        this.plaqueta = plaqueta;
        this.estado = estado;
        this.proveedor = proveedor;
        this.terminal = terminal;
        this.idAdquiriente = idAquiriente;
        loadComponents();
    }

    public void setState(String state) {
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoDisabled.png")));
        estado = state;
        switch (state) {
            case NovusConstante.DATAFONO_LOADER:
                break;
            case NovusConstante.DATAFONO_INACTIVO:
                background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoDisabled.png")));
                break;
            case NovusConstante.DATAFONO_ACTIVO:
                background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoInfo.png")));
                break;
            case NovusConstante.DATAFONO_ERROR:
                break;
            default:
                break;
        }
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPlaqueta() {
        return plaqueta;
    }

    public void setPlaqueta(String plaqueta) {
        this.plaqueta = plaqueta;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public int getNumberDatafono() {
        return numberDatafono;
    }

    public void setNumberDatafono(int numberDatafono) {
        this.numberDatafono = numberDatafono;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public int getIdAdquiriente() {
        return idAdquiriente;
    }

    public void setIdAdquiriente(int idAdquiriente) {
        this.idAdquiriente = idAdquiriente;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        number = new javax.swing.JLabel();
        txtDatafono = new javax.swing.JLabel();
        txtPlaqueta = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Plaqueta");
        add(jLabel2);
        jLabel2.setBounds(226, 160, 60, 17);

        number.setFont(new java.awt.Font("Tahoma", 1, 70)); // NOI18N
        number.setForeground(new java.awt.Color(255, 255, 255));
        number.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        number.setText("#");
        number.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(number);
        number.setBounds(30, 10, 90, 110);

        txtDatafono.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txtDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDatafono.setText("Nombre");
        txtDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(txtDatafono);
        txtDatafono.setBounds(206, 120, 160, 30);

        txtPlaqueta.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtPlaqueta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPlaqueta.setText("numero");
        txtPlaqueta.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        add(txtPlaqueta);
        txtPlaqueta.setBounds(292, 156, 80, 22);

        background.setBackground(new java.awt.Color(250, 250, 250));
        background.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoInfo.png"))); // NOI18N
        add(background);
        background.setBounds(0, 0, 378, 192);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel background;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel number;
    private javax.swing.JLabel txtDatafono;
    private javax.swing.JLabel txtPlaqueta;
    // End of variables declaration//GEN-END:variables

    public static String getUltimosCaracteres(String s, int numero) {
        if (s == null || numero > s.length()) {
            return s;
        }
        return s.substring(s.length() - numero);
    }

}

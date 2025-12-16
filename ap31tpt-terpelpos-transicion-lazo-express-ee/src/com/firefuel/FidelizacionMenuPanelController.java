/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.controllers.NovusUtils;
import com.controllers.NovusConstante;

/**
 *
 * @author usuario
 */
public class FidelizacionMenuPanelController extends javax.swing.JPanel {

    InfoViewController base;

    FidelizacionMenuPanelController(InfoViewController base) {
        this.base = base;
        initComponents();
        this.updateUI();
        init();
    }

    void init() {
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel37 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        ventasRetenidas = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setLayout(null);

        jLabel37.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("FIDELIZACIONES RETENIDAS");
        add(jLabel37);
        jLabel37.setBounds(120, 490, 290, 70);

        jLabel42.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setText("VALIDACION BONO");
        add(jLabel42);
        jLabel42.setBounds(120, 390, 280, 70);

        jLabel38.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(186, 12, 47));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("5");
        add(jLabel38);
        jLabel38.setBounds(40, 490, 70, 60);

        jLabel44.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(186, 12, 47));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("4");
        add(jLabel44);
        jLabel44.setBounds(40, 400, 70, 50);

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel39.setEnabled(true
        );
        jLabel39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel39MouseReleased(evt);
            }
        });
        add(jLabel39);
        jLabel39.setBounds(30, 390, 390, 70);

        jLabel30.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("3");
        add(jLabel30);
        jLabel30.setBounds(40, 300, 70, 50);

        ventasRetenidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        ventasRetenidas.setEnabled(true
        );
        ventasRetenidas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ventasRetenidasMouseReleased(evt);
            }
        });
        add(ventasRetenidas);
        ventasRetenidas.setBounds(30, 490, 390, 70);

        jLabel31.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("REDENCION");
        add(jLabel31);
        jLabel31.setBounds(120, 290, 280, 70);

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel32.setEnabled(false);
        jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel32MouseReleased(evt);
            }
        });
        add(jLabel32);
        jLabel32.setBounds(30, 290, 390, 70);

        jLabel27.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("CONSULTA DE CLIENTE");
        add(jLabel27);
        jLabel27.setBounds(120, 110, 300, 70);

        jLabel28.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(186, 12, 47));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("1");
        add(jLabel28);
        jLabel28.setBounds(30, 120, 80, 50);

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel29MouseReleased(evt);
            }
        });
        add(jLabel29);
        jLabel29.setBounds(30, 110, 390, 70);

        jLabel23.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("ACUMULACIÓN");
        add(jLabel23);
        jLabel23.setBounds(120, 200, 280, 70);

        jLabel43.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(186, 12, 47));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("4");
        add(jLabel43);
        jLabel43.setBounds(40, 400, 70, 50);

        jLabel22.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("2");
        add(jLabel22);
        jLabel22.setBounds(40, 210, 70, 45);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel14MouseReleased(evt);
            }
        });
        add(jLabel14);
        jLabel14.setBounds(30, 200, 390, 70);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        add(jLabel1);
        jLabel1.setBounds(1190, 0, 90, 90);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        add(jLabel16);
        jLabel16.setBounds(10, 10, 62, 63);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_fidelizacion.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel13);
        jLabel13.setBounds(680, 180, 390, 430);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel33);
        jLabel33.setBounds(1170, 3, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        add(jLabel36);
        jLabel36.setBounds(10, 710, 100, 80);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("FIDELIZACION");
        add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel40);
        jLabel40.setBounds(90, 10, 10, 68);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel29MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseReleased
        mostrarConsultaCliente();
    }//GEN-LAST:event_jLabel29MouseReleased

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseReleased
        cerrarsubmenu();
    }//GEN-LAST:event_jLabel1MouseReleased

    private void jLabel14MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseReleased
        acumularClientes();
    }//GEN-LAST:event_jLabel14MouseReleased

    private void jLabel39MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel39MouseReleased
        mostrarValidacionVoucher();
    }//GEN-LAST:event_jLabel39MouseReleased

    private void jLabel32MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MouseReleased

    }//GEN-LAST:event_jLabel32MouseReleased

    private void ventasRetenidasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ventasRetenidasMouseReleased
        mostrarFidelizacionesRetenidas();
    }//GEN-LAST:event_ventasRetenidasMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel ventasRetenidas;
    // End of variables declaration//GEN-END:variables

    private void cerrarsubmenu() {
        this.setVisible(false);
        this.base.cambiarPanelHome();
    }

    void mostrarCambioPrecio() {
        NovusUtils.beep();
        TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
        tipoVenta.setView("CAMBIO PRECIO");
        tipoVenta.setVisible(true);
    }

    private void mostrarConsultaCliente() {
        NovusUtils.beep();
        base.recargarPersona();
        FidelizacionCliente cliente = new FidelizacionCliente(base, true);
        cliente.setVisible(true);
    }

    private void acumularClientes() {
        NovusUtils.beep();
        base.recargarPersona();
        FidelizacionVentas cliente = new FidelizacionVentas(base, true);
        cliente.setVisible(true);
    }

    private void mostrarValidacionVoucher() {
        NovusUtils.beep();
        FidelizacionValidacionVoucher voucher = new FidelizacionValidacionVoucher(base, true, true);
        voucher.setVisible(true);
    }

    private void mostrarFidelizacionesRetenidas() {
        NovusUtils.beep();
        FidelizacionesRetenidasView fRetenidasView = new FidelizacionesRetenidasView(base, true);
        fRetenidasView.setVisible(true);
    }

}

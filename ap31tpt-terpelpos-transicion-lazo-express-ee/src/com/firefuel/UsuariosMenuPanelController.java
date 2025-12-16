/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

/**
 *
 * @author usuario
 */
public class UsuariosMenuPanelController extends javax.swing.JPanel {

    InfoViewController base;

    public UsuariosMenuPanelController(InfoViewController base) {
        this.base = base;
        initComponents();
        this.updateUI();
        init();
    }

    private void init() {
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setLayout(null);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("USUARIOS");
        add(jTitle);
        jTitle.setBounds(100, 5, 650, 70);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        add(jLabel1);
        jLabel1.setBounds(1180, 0, 100, 90);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        add(jLabel16);
        jLabel16.setBounds(10, 0, 80, 90);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_usuarios.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel13);
        jLabel13.setBounds(680, 210, 360, 380);

        jLabel19.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("1");
        add(jLabel19);
        jLabel19.setBounds(40, 120, 60, 50);

        jLabel20.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("USUARIOS REGISTRADOS");
        add(jLabel20);
        jLabel20.setBounds(120, 110, 310, 70);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        add(jLabel10);
        jLabel10.setBounds(30, 110, 400, 70);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        add(jLabel26);
        jLabel26.setBounds(10, 710, 100, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel30);
        jLabel30.setBounds(1170, 3, 10, 80);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        cerrarsubmenu();
    }// GEN-LAST:event_jLabel1MouseReleased

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MouseReleased
        mostrarUsuariosRegistrados();
    }// GEN-LAST:event_jLabel10MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jTitle;
    // End of variables declaration//GEN-END:variables

    private void cerrarsubmenu() {
        this.setVisible(false);
        this.base.mostrarOcultarMenu();

        // this.vistaPrincipal.mostrarOcultarMenu();
    }

    private void mostrarUsuariosRegistrados() {
        NovusUtils.beep();
        cerrarsubmenu();
        UsuariosRegistradosViewController reg = new UsuariosRegistradosViewController(base, false);
        reg.setVisible(true);
    }

}

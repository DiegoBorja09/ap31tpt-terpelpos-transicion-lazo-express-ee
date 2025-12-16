/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.firefuel;

import com.bean.Notificador;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import javax.swing.JFrame;

/**
 *
 * @author USUARIO
 */
public class DialogoConfirmacionCierreMensaje extends javax.swing.JDialog {

    boolean loading = false;
    JFrame vistaPrincipal = null;
    boolean autoClose = false;
    int timeout = 0;
    Notificador notificacion = null;

    public DialogoConfirmacionCierreMensaje(JFrame vistaPrincipal, boolean modal, Notificador handler) {
        super(vistaPrincipal, modal);
        initComponents();
        this.vistaPrincipal = vistaPrincipal;
        this.notificacion = handler;
        this.init();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_home = new javax.swing.JLabel();
        txt_closed = new javax.swing.JLabel();
        txtEstacion = new javax.swing.JLabel();
        txtIsla = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 800));
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1280, 800));
        setResizable(false);
        setSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(null);

        btn_home.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        btn_home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_homeMouseReleased(evt);
            }
        });
        getContentPane().add(btn_home);
        btn_home.setBounds(10, 10, 70, 70);

        txt_closed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt_closed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/BtCerrar.png"))); // NOI18N
        txt_closed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_closedMouseReleased(evt);
            }
        });
        getContentPane().add(txt_closed);
        txt_closed.setBounds(1020, 210, 60, 60);

        txtEstacion.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        txtEstacion.setForeground(new java.awt.Color(255, 255, 255));
        txtEstacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtEstacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        txtEstacion.setText("CERRAR ESTACION");
        txtEstacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtEstacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtEstacionMouseReleased(evt);
            }
        });
        getContentPane().add(txtEstacion);
        txtEstacion.setBounds(670, 440, 280, 60);

        txtIsla.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        txtIsla.setForeground(new java.awt.Color(255, 255, 255));
        txtIsla.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtIsla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        txtIsla.setText("CERRAR ISLA");
        txtIsla.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtIsla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtIslaMouseReleased(evt);
            }
        });
        getContentPane().add(txtIsla);
        txtIsla.setBounds(340, 440, 280, 60);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<html><center> </center></html>");
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel3);
        jLabel3.setBounds(280, 230, 710, 190);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndConfirmacion.png"))); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(1024, 457));
        jLabel1.setMinimumSize(new java.awt.Dimension(1024, 457));
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_homeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_homeMouseReleased
        cerrar();
    }//GEN-LAST:event_btn_homeMouseReleased

    private void txt_closedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_closedMouseReleased
        cerrar();
    }//GEN-LAST:event_txt_closedMouseReleased

    private void txtEstacionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEstacionMouseReleased
        sendStatusCierre(true);
    }//GEN-LAST:event_txtEstacionMouseReleased

    private void txtIslaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtIslaMouseReleased
        sendStatusCierre(false);
    }//GEN-LAST:event_txtIslaMouseReleased

    void sendStatusCierre(boolean isCierreEstacion) {
        if (notificacion != null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("cerrarEstacion", isCierreEstacion);
            notificacion.send(obj);
        }
        this.cerrar();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        this.setLocationRelativeTo(null);
    }

    void initializerAutoClose() {
        setTimeout(this.timeout, () -> cerrar());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && autoClose) {
            initializerAutoClose();
        }
        super.setVisible(visible);
    }

    void cerrar() {
        if (this.isVisible()) {
            this.dispose();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_home;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel txtEstacion;
    private javax.swing.JLabel txtIsla;
    private javax.swing.JLabel txt_closed;
    // End of variables declaration//GEN-END:variables

    void setMensajes(String venta, String mensaje) {
        jLabel3.setText("<html><center>" + mensaje + "</center></html>");
    }

    void isLoading(boolean loading) {
        this.loading = loading;
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}

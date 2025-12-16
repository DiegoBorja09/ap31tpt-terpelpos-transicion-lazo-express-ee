/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import teclado.view.common.TecladoExtendido;
import com.neo.print.services.PrinterFacade;
import java.util.regex.Pattern;
import javax.swing.JFrame;


/**
 *
 * @author novus
 */
public class ImpresoraView extends javax.swing.JDialog {

    JFrame parent;
    EquipoDao dao = new EquipoDao();

    public ImpresoraView(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();

    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jLabel1.setText(Main.getParametroCore(NovusConstante.PREFERENCE_IP_IMPRESORA,true));
    
        if (Main.parametros.get(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)
                .equals(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP)) {
            jTextField1.setEnabled(true);
            jTextField1.setText(Main.getParametroCore(NovusConstante.PREFERENCE_IP_IMPRESORA,true));
        } else {
            jTextField1.setEnabled(false);
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        jTextField1.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new TecladoExtendido();
        jTitle = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        background = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1280, 800));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        getContentPane().setLayout(null);

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(186, 12, 47));
        jLabel3.setText("IP ACTUAL:");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel3);
        jLabel3.setBounds(240, 280, 150, 40);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel4.setText("GUARDAR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel4);
        jLabel4.setBounds(910, 250, 200, 60);

        jLabel10.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel10.setText("CANCELAR");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel10);
        jLabel10.setBounds(910, 170, 200, 60);

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("IP ACTUAL:");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(400, 280, 280, 40);

        jTextField1.setBackground(new java.awt.Color(239, 239, 239));
        jTextField1.setFont(new java.awt.Font("Roboto", 1, 36)); // NOI18N
        jTextField1.setBorder(null);
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(230, 201, 460, 47);

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        getContentPane().add(jPanel1);
        jPanel1.setBounds(130, 360, 1024, 330);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("IMPRESORA");
        getContentPane().add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jLabel5);
        jLabel5.setBounds(140, 710, 760, 80);

        back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backMouseReleased(evt);
            }
        });
        getContentPane().add(back);
        back.setBounds(10, 10, 80, 70);

        jLabel2.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        jLabel2.setText("IMPRESORA POR IP");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(220, 150, 320, 30);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel30);
        jLabel30.setBounds(90, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndImpresora.png"))); // NOI18N
        getContentPane().add(background);
        background.setBounds(0, 0, 1280, 800);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion);
        jNotificacion.setBounds(730, 10, 530, 70);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1MouseReleased

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, jTextField1, 15, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jTextField1KeyTyped

    private void backMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMouseReleased
        cancelar();
    }//GEN-LAST:event_backMouseReleased

    private void formMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseClicked
        // Es requerido para evitar que se presionen los botones de abajo
    }// GEN-LAST:event_formMouseClicked

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField1ActionPerformed
        cambiaIP();
    }// GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField1FocusGained
        deshabilitarTeclas(true);
        deshabilitar();
        NovusUtils.deshabilitarCopiarPegar(jTextField1);

    }// GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField1FocusLost
        deshabilitarTeclas(false);
    }// GEN-LAST:event_jTextField1FocusLost

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        NovusUtils.beep();
        if (jLabel4.isEnabled()) {
            jLabel5.setText("Cargando . . .");
            jLabel4.setEnabled(false);
            jLabel10.setEnabled(false);
            setTimeout(3, () -> {
                cambiaIP();
                jLabel4.setEnabled(true);
                jLabel10.setEnabled(true);
                jLabel5.setText("");
            });
        }

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

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MouseReleased
        if (jLabel10.isEnabled()) {
            NovusUtils.beep();
            cancelar();
        }
    }// GEN-LAST:event_jLabel10MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back;
    private javax.swing.JLabel background;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jTitle;
    // End of variables declaration//GEN-END:variables
    private void cancelar() {
        this.dispose();
    }

    private void cambiaIP() {
        String texto = jTextField1.getText();
        if (validate(texto)) {
            Main.parametros.put(NovusConstante.PREFERENCE_IP_IMPRESORA, texto);
            Main.parametros.put(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE, NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP);

            EquipoDao.setParametro(NovusConstante.PREFERENCE_IP_IMPRESORA, texto);
            EquipoDao.setParametro(NovusConstante.PREFERENCE_IP_IMPRESORA, texto, true);
            EquipoDao.setParametro(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE,
                    NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP);
            jLabel1.setText("IMPRESORA POR IP " + texto);
            PrinterFacade pf = new PrinterFacade();
            pf.printTest();
        }
    }

    private static final Pattern PATTERN = Pattern
            .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private void deshabilitarTeclas(boolean activar) {
        TecladoExtendido tec = (TecladoExtendido) jPanel1;
        tec.habilitarAlfanumeric(activar);
        tec.habilitarPunto(activar);
    }

    private void setOpaque(boolean b) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change
        // body of generated methods, choose Tools | Templates.
    }

    private void deshabilitar() {
        TecladoExtendido tec = (TecladoExtendido) jPanel1;
        tec.deshabilitarCaracteresEspeciales(false);
    }

}

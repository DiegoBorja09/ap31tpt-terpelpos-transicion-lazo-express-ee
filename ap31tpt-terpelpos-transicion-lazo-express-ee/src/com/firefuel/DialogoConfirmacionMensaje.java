package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.awt.Frame;
import javax.swing.JDialog;


public class DialogoConfirmacionMensaje extends javax.swing.JDialog {

    boolean loading = false;
    Frame vistaPrincipal = null;
    boolean autoClose = false;
    int timeout = 0;
    Runnable handler = null;
    JDialog dialog;

    public DialogoConfirmacionMensaje(Frame vistaPrincipal, boolean modal, Runnable handler) {
        super(vistaPrincipal, modal);
        initComponents();
        this.vistaPrincipal = vistaPrincipal;
        this.handler = handler;
        this.init();
    }

    public DialogoConfirmacionMensaje(Frame vistaPrincipal, boolean modal, Runnable handler, DialogoConfirmacionMensaje dialog) {
        super(vistaPrincipal, modal);
        initComponents();
        this.vistaPrincipal = vistaPrincipal;
        this.handler = handler;
        this.dialog = dialog;
        this.init();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnRight = new javax.swing.JLabel();
        btnLeft = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setBounds(new java.awt.Rectangle(0, 161, 1280, 639));
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        btnRight.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        btnRight.setForeground(new java.awt.Color(255, 255, 255));
        btnRight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnRight.setText("SI");
        btnRight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnRightMouseReleased(evt);
            }
        });
        getContentPane().add(btnRight);
        btnRight.setBounds(700, 620, 280, 60);

        btnLeft.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        btnLeft.setForeground(new java.awt.Color(255, 255, 255));
        btnLeft.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnLeft.setText("NO");
        btnLeft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnLeftMouseReleased(evt);
            }
        });
        getContentPane().add(btnLeft);
        btnLeft.setBounds(350, 620, 280, 60);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<html><center>SE HA INICIADO JORNADA CORRECTAMENTE</center></html>");
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel3);
        jLabel3.setBounds(280, 210, 730, 330);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_confirmacion_n.png"))); // NOI18N
        background.setMaximumSize(new java.awt.Dimension(1024, 457));
        background.setMinimumSize(new java.awt.Dimension(1024, 457));
        background.setPreferredSize(new java.awt.Dimension(1024, 457));
        getContentPane().add(background);
        background.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRightMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRightMouseReleased
        if (this.btnRight.isEnabled() && this.handler != null) {
            this.cerrar();
            this.handler.run();
        }
    }//GEN-LAST:event_btnRightMouseReleased

    private void btnLeftMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLeftMouseReleased
        NovusUtils.beep();
        if (btnLeft.isEnabled()) {
            cerrar();
        }
    }//GEN-LAST:event_btnLeftMouseReleased

    private void init() {
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
    private javax.swing.JLabel background;
    public javax.swing.JLabel btnLeft;
    public javax.swing.JLabel btnRight;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables

    public void setMensajes(String venta, String mensaje) {
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

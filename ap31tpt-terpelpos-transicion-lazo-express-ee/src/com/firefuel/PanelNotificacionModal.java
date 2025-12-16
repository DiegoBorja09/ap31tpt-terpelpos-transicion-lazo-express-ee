package com.firefuel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class PanelNotificacionModal extends javax.swing.JPanel implements ActionListener {

    private static PanelNotificacionModal instance;


    private String mensaje;
    private String imagen = "/com/firefuel/resources/btOk.png";
    private boolean habilitarBoton;
    private Runnable handler;
    int seconds = 0;
    private int timeout = 5;
    private Timer timer = new Timer(1000, this);

    public PanelNotificacionModal() {
        initComponents();
    }

    public PanelNotificacionModal(String mensaje, String imagen, boolean habilitarBoton) {
        this.mensaje = mensaje;
        this.imagen = imagen;
        this.habilitarBoton = habilitarBoton;
        initComponents();
    }

    public PanelNotificacionModal(String mensaje, String imagen, boolean habilitarBoton, Runnable handler) {
        this.mensaje = mensaje;
        this.imagen = imagen;
        this.habilitarBoton = habilitarBoton;
        this.handler = handler;
        initComponents();

    }

    public static PanelNotificacionModal getInstance() {
        if (instance == null) {
            instance = new PanelNotificacionModal();
        }
        return instance;
    }

    public static PanelNotificacionModal getInstance(String mensaje, String imagen, boolean habilitarBoton) {
        if (instance == null) {
            instance = new PanelNotificacionModal(mensaje, imagen, habilitarBoton);
        }
        return instance;
    }

    public static PanelNotificacionModal getInstance(String mensaje, String imagen, boolean habilitarBoton, Runnable handler) {
        if (instance == null) {
            instance = new PanelNotificacionModal(mensaje, imagen, habilitarBoton, handler);
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jMensaje = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        btnCerrar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jCerrar = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1280, 800));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(227, 227, 232));
        jPanel1.setLayout(null);

        panelRedondo1.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo1.setRoundBottomLeft(30);
        panelRedondo1.setRoundBottomRight(30);
        panelRedondo1.setRoundTopLeft(30);
        panelRedondo1.setRoundTopRight(30);
        panelRedondo1.setLayout(null);

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        jMensaje.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panelRedondo1.add(jMensaje);
        jMensaje.setBounds(0, 190, 940, 170);

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/cheque.png"))); // NOI18N
        jIcono.setToolTipText("");
        panelRedondo1.add(jIcono);
        jIcono.setBounds(0, 50, 940, 140);

        btnCerrar.setBackground(new java.awt.Color(179, 15, 0));
        btnCerrar.setRoundBottomLeft(20);
        btnCerrar.setRoundBottomRight(20);
        btnCerrar.setRoundTopLeft(20);
        btnCerrar.setRoundTopRight(20);
        btnCerrar.setLayout(null);

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(255, 255, 255));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        btnCerrar.add(jCerrar);
        jCerrar.setBounds(0, 0, 270, 70);

        panelRedondo1.add(btnCerrar);
        btnCerrar.setBounds(335, 370, 270, 70);

        jPanel1.add(panelRedondo1);
        panelRedondo1.setBounds(180, 80, 940, 460);

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 1280, 630));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));
    }// </editor-fold>//GEN-END:initComponents

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        cerrar();
    }//GEN-LAST:event_jCerrarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnCerrar;
    public static javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JPanel jPanel1;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    // End of variables declaration//GEN-END:variables

    public void update(String mensaje, String imagen, boolean habilitarBoton, Runnable handler) {
        jMensaje.setText("<html>".concat((mensaje).toUpperCase()).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
        btnCerrar.setVisible(habilitarBoton);
        setHandler(handler);
    }
    
    public void loader(String mensaje, String imagen, boolean habilitarBoton) {
        jMensaje.setText("<html>".concat((mensaje).toUpperCase()).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
        btnCerrar.setVisible(habilitarBoton);
    }
    

    public void mostrar() {
        jMensaje.setText("<html>".concat((mensaje).toUpperCase()).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
        btnCerrar.setVisible(habilitarBoton);
    }


    public void cargar() {
        jMensaje.setText("<html>".concat((mensaje).toUpperCase()).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
        btnCerrar.setVisible(habilitarBoton);
    }

    public void cerrar() {
        seconds = 0;
        timer.stop();
        System.out.println(handler);
        if (handler != null) {
            handler.run();
        }
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isHabilitarBoton() {
        return habilitarBoton;
    }

    public void setHabilitarBoton(boolean habilitarBoton) {
        this.habilitarBoton = habilitarBoton;
    }

    public Runnable getHandler() {
        return handler;
    }

    public void setHandler(Runnable handler) {
        this.handler = handler;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        autoClose();
    }

    public void autoClose() {
        seconds++;
        if (seconds == timeout) {
            cerrar();
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

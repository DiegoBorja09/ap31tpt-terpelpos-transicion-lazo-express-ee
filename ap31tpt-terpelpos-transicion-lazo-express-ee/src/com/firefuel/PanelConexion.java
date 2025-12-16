
package com.firefuel;

import com.controllers.NovusUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Timer;

public class PanelConexion extends javax.swing.JPanel implements ActionListener {

    private static PanelConexion instance;
    private String mensaje;
    private Runnable handler;
    int seconds = 0;
    private int timeout = 5;
    private Timer timer = new Timer(1000, this);
    private int proceso = 0;

    private final Icon alerta = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alert_min.png"));
    private final Icon loader = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loaderSmall.gif"));
    private final Icon confirmacion = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/confirmacionSmall.png"));

    private final int estadoAlerta = 0;
    private final int estadoLoader = 1;
    private final int estadoConfirmacion = 2;

    public PanelConexion() {
        initComponents();
    }

    public PanelConexion(String mensaje) {
        this.mensaje = mensaje;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblIcon = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        lblFnd = new javax.swing.JLabel();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(352, 74));
        setLayout(null);

        lblIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alert_min.png"))); // NOI18N
        add(lblIcon);
        lblIcon.setBounds(17, 8, 58, 58);

        lblMensaje.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(186, 12, 47));
        lblMensaje.setText("MENSAJE");
        add(lblMensaje);
        lblMensaje.setBounds(90, 10, 250, 50);

        lblFnd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/panelConexion.png"))); // NOI18N
        add(lblFnd);
        lblFnd.setBounds(0, 0, 352, 74);
    }// </editor-fold>//GEN-END:initComponents

    static PanelConexion getInstance() {
        if (instance == null) {
            instance = new PanelConexion();
        }
        return instance;
    }

    static PanelConexion getInstance(String mensaje) {
        if (instance == null) {
            instance = new PanelConexion(mensaje);
        }
        return instance;
    }

    public void autoClose() {
        seconds++;
        if (seconds == timeout) {
            cerrar();
        }
    }

    public void cerrar() {
        seconds = 0;
        timer.stop();
        if (handler != null) {
            handler.run();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        autoClose();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void mostrar() {
        lblMensaje.setText("<html><center>".concat((mensaje).toUpperCase()).concat("</center></html>"));
    }

    public void mostrarEstado() {
        switch (proceso) {
            case estadoAlerta:
                lblIcon.setIcon(alerta);
                break;
            case estadoLoader:
                lblIcon.setIcon(loader);
                break;
            case estadoConfirmacion:
                lblIcon.setIcon(confirmacion);
                break;
            default:
                NovusUtils.printLn("Estado no Valido");
                lblIcon.setIcon(alerta);
                break;
        }
        lblMensaje.setText("<html><center>".concat((mensaje).toUpperCase()).concat("</center></html>"));
    }

    public Runnable getHandler() {
        return handler;
    }

    public void setHandler(Runnable handler) {
        this.handler = handler;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblFnd;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblMensaje;
    // End of variables declaration//GEN-END:variables

    public int getProceso() {
        return proceso;
    }

    public void setProceso(int proceso) {
        this.proceso = proceso;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

}

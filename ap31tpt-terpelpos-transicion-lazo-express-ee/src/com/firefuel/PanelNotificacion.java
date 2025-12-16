package com.firefuel;

import com.WT2.loyalty.domain.entities.beans.FoundClient;
import java.awt.Font;
import com.WT2.commons.domain.valueObject.LetterCase;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PanelNotificacion extends javax.swing.JPanel implements ActionListener {

    private static PanelNotificacion instance;
    private static PanelNotificacion appterpelInstance;

    private static final String DEFAULT_ICON_PATH = "/com/firefuel/resources/icono_lg_turno.png";

    private String mensaje;
    private String imagen = "/com/firefuel/resources/btOk.png";
    private boolean habilitarBoton;
    private Runnable handler;
    int seconds = 0;
    private int timeout = 5;
    private VentasHistorialKCOView kiosco;
    private VentasHistorialCanastillaView canastilla;

    private int timerDalay = 1000;
    private Timer timer = new Timer(timerDalay, this);

    public PanelNotificacion() {
        initComponents();
    }

    public PanelNotificacion(String mensaje, String imagen, boolean habilitarBoton) {
        this.mensaje = mensaje;
        this.imagen = imagen;
        this.habilitarBoton = habilitarBoton;
        initComponents();
    }

    public PanelNotificacion(String mensaje, String imagen, boolean habilitarBoton, Runnable handler) {
        this.mensaje = mensaje;
        this.imagen = imagen;
        this.habilitarBoton = habilitarBoton;
        this.handler = handler;
        initComponents();

    }

    public PanelNotificacion(String mensaje, String imagen, boolean habilitarBoton, Runnable handler, VentasHistorialKCOView dialog) {
        this.mensaje = mensaje;
        this.imagen = imagen;
        this.habilitarBoton = habilitarBoton;
        this.handler = handler;
        this.kiosco = dialog;
        initComponents();
    }

    public PanelNotificacion(String mensaje, String imagen, boolean habilitarBoton, Runnable handler, VentasHistorialCanastillaView canstilla) {
        this.mensaje = mensaje;
        this.imagen = imagen;
        this.habilitarBoton = habilitarBoton;
        this.handler = handler;
        this.canastilla = canstilla;
        initComponents();
    }

    public static PanelNotificacion getInstance() {
        if (instance == null) {
            instance = new PanelNotificacion();
        }
        return instance;
    }

    public static PanelNotificacion getInstanceAppterpel() {
        if (appterpelInstance == null) {
            appterpelInstance = new PanelNotificacion();
        }
        return appterpelInstance;
    }

    public static PanelNotificacion getInstance(String mensaje, String imagen, boolean habilitarBoton) {
        if (instance == null) {
            instance = new PanelNotificacion(mensaje, imagen, habilitarBoton);
        }
        return instance;
    }

    public static PanelNotificacion getInstance(String mensaje, String imagen, boolean habilitarBoton, Runnable handler) {
        if (instance == null) {
            instance = new PanelNotificacion(mensaje, imagen, habilitarBoton, handler);
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jIcono = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jCerrar = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1280, 800));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btOk.png"))); // NOI18N
        jIcono.setToolTipText("");
        add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 330, 360));

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        jMensaje.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, 710, 390));

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 3, 3));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-1.png"))); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 10, 290, 70));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));
    }// </editor-fold>//GEN-END:initComponents

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        System.out.println("[PanelNotificacion] CLICK en botón CERRAR detectado");
        cerrar();
    }//GEN-LAST:event_jCerrarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jMensaje;
    // End of variables declaration//GEN-END:variables

    public void update(String mensaje, String imagen, boolean habilitarBoton, Runnable handler, String letterCase) {
        String mensajePanel = setMensajePanel(letterCase, mensaje);
        jMensaje.setText("<html>" + mensajePanel + "</html>");

        // Usa método seguro
        setIconoSeguro(imagen);

        jCerrar.setVisible(habilitarBoton);
        setHandler(handler);
    }



    private void setIconoSeguro(String ruta) {
        if (ruta == null) {
            ruta = DEFAULT_ICON_PATH;
            System.out.println("➡ Ruta nula, usando ícono por defecto: " + ruta);
        }

        System.out.println("🔍 Intentando cargar ícono desde ruta: " + ruta);

        java.net.URL url = getClass().getResource(ruta);
        if (url != null) {
            System.out.println("✅ Recurso encontrado: " + url);
            jIcono.setIcon(new javax.swing.ImageIcon(url));
        } else {
            System.err.println("❌ Recurso NO encontrado: " + ruta);
            jIcono.setIcon(null);
        }

        System.out.println("📌 Working directory: " + System.getProperty("user.dir"));
    }


    public void update(String mensaje, String imagen, boolean habilitarBoton, Runnable handler) {
        String mensajePanel = setMensajePanel(LetterCase.FIRST_UPPER_CASE, mensaje);
        jMensaje.setText("<html>".concat(mensajePanel).concat("</html>"));

        if (imagen != null) {
            java.net.URL url = getClass().getResource(imagen);
            if (url != null) {
                jIcono.setIcon(new javax.swing.ImageIcon(url));
            } else {
                System.err.println("⚠ Recurso no encontrado: " + imagen);
                jIcono.setIcon(null); // o ícono por defecto
            }
        } else {
            System.err.println("⚠ Imagen null, se usará ícono por defecto o ninguno.");
            jIcono.setIcon(null); // o ícono por defecto
        }

        jCerrar.setVisible(habilitarBoton);
        setHandler(handler);
    }


    public void loader(String mensaje, String imagen, boolean habilitarBoton) {
        jMensaje.setText("<html>".concat((mensaje).toUpperCase()).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
    }

    public void mostrar() {
        String mensajePanel = setMensajePanel(LetterCase.FIRST_UPPER_CASE, mensaje);
        jMensaje.setText("<html>".concat(mensajePanel).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
    }

    public void cargar() {
        jMensaje.setText("<html>".concat((mensaje).toUpperCase()).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(habilitarBoton);
    }

    public String setMensajePanel(String letterCase, String mensaje) {
        String nuevoMensaje = "";
        switch (letterCase) {
            case LetterCase.UPPER_CASE:
                nuevoMensaje = mensaje.toUpperCase();
                break;
            case LetterCase.LOWER_CASE:
                nuevoMensaje = mensaje.toLowerCase();
                break;
            case LetterCase.FIRST_UPPER_CASE:
                nuevoMensaje = mensaje.substring(0, 1).toUpperCase()
                        + mensaje.substring(1).toLowerCase();
                break;
            default:
                nuevoMensaje = mensaje.substring(0, 1).toUpperCase()
                        + mensaje.substring(1).toLowerCase();
                break;
        }
        return nuevoMensaje;
    }

   
    public void cerrar() {
        System.out.println("[PanelNotificacion] Método cerrar() ejecutándose...");
        
        seconds = 0;
        timer.stop();
        if (kiosco != null) {
            kiosco.Async(null, 1);
        }
        if (canastilla != null) {
            canastilla.Async(null, 1);
        }
        if (handler != null) {
            System.out.println("[PanelNotificacion] Ejecutando handler...");
            handler.run();
            handler = null;
            System.out.println("[PanelNotificacion] Handler ejecutado");
        } else {
            System.out.println("[PanelNotificacion] WARNING: No hay handler configurado");
        }
        System.out.println("[PanelNotificacion] Método cerrar() completado");
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


    public void setImagen(ImageIcon icono) {
        if (icono != null) {
            jIcono.setIcon(icono);
        } else {
            jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(DEFAULT_ICON_PATH)));
        }
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
        if (seconds >= timeout) {
            cerrar();
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setTimerDalay(int timerDalay) {
        this.timerDalay = timerDalay;
        timer.setInitialDelay(this.timerDalay);
    }

}

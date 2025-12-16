package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class NotificacionesView extends javax.swing.JDialog {
    JFrame parent;

    public NotificacionesView(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        renderizarNotificaciones();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jNotificacionesPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        getContentPane().add(jclock);
        jclock.setBounds(1150, 720, 110, 66);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("NOTIFICACIONES");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(100, 0, 720, 90);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jScrollPane1.setBorder(null);

        jNotificacionesPanel.setBackground(new java.awt.Color(255, 255, 255));
        jNotificacionesPanel.setLayout(null);
        jScrollPane1.setViewportView(jNotificacionesPanel);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(60, 140, 1180, 520);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndNotify.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        cerrar();
    }// GEN-LAST:event_jLabel3MousePressed

    private void cerrar() {
        dispose();
    }

    void renderizarNotificaciones() {
        JsonObject response = obtenerNotificaciones();
        if (response != null) {
            JsonArray jsonNotificacionesArray = response.get("data") != null && response.get("data").isJsonArray() ? response.getAsJsonArray("data") : new JsonArray();
            try {
                this.jNotificacionesPanel.removeAll();
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
            int i = 0;
            for (JsonElement notificacionesElement : jsonNotificacionesArray) {
                NovusUtils.printLn(notificacionesElement.toString());
                // JsonObject jsonNotificacion = notificacionesElement.getAsJsonObject();
                NotifyItemView notifyItem = new NotifyItemView();
                notifyItem.setVisible(true);
                notifyItem.setBounds(0, i * 80, 1166, 73);
                this.jNotificacionesPanel.add(notifyItem);
                i++;
            }
            this.jNotificacionesPanel.validate();
            this.jNotificacionesPanel.repaint();
        }
    }

    JsonObject obtenerNotificaciones() {
        JsonObject response = new JsonObject();
        JsonArray data = new JsonArray();
        data.add(new JsonPrimitive("One"));
        data.add(new JsonPrimitive("two"));
        data.add(new JsonPrimitive("three"));
        data.add(new JsonPrimitive("four"));
        data.add(new JsonPrimitive("five"));
        response.add("data", data);
        return response;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jNotificacionesPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jclock;
    // End of variables declaration//GEN-END:variables

}

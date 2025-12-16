package com.firefuel.mediospago;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.controllers.NovusUtils;
import com.facade.GopassFacade;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neo.app.bean.Manguera;
import java.awt.CardLayout;
import java.util.concurrent.CompletableFuture;

public class PanelSeleccionPlacas extends javax.swing.JPanel {

    static final String PNL_LOADER = "pnlLoader";
    static final String PNL_PLACAS = "pnlPlacas";
    static final String ICONO_LOADER = "/com/firefuel/resources/loader128px.gif";
    static final String ICONO_ERROR = "/com/firefuel/resources/imgError.png";
    JsonArray infoPlacas = new JsonArray();
    JsonObject infoPlacasError;
    private CompletableFuture<Void> futureReference = null;
    public boolean procesando = false;
    private GopassParameters parametrosGopass;
    private int timeoutGopass;
    private int timeoutGopassPlate;

    public PanelSeleccionPlacas() {
        initComponents();
        parametrosGopass = SingletonMedioPago.ConetextDependecy.getRecuperarParametrosGopass().execute(null);
        timeoutGopass = parametrosGopass.getConfiguracionTokenTiempoReintentos() + parametrosGopass.getConfiguracionConsultaPlacaTiempoReintentos() + parametrosGopass.getConfiguracionTokenTiempoMuerto() + parametrosGopass.getConfiguracionConsultaPlacaTiempoMuerto() + 5;
        timeoutGopassPlate = ((parametrosGopass.getConfiguracionTokenCantidadReintentos() * ( parametrosGopass.getConfiguracionTokenTiempoMuerto() + parametrosGopass.getConfiguracionPagoTiempoReintentos()) )  ) + (parametrosGopass.getConfiguracionConsultaPlacaCantidadReintentos() * ( parametrosGopass.getConfiguracionConsultaPlacaTiempoMuerto() + parametrosGopass.getConfiguracionConsultaPlacaTiempoReintentos() ) ); 
        timeoutGopassPlate = (timeoutGopassPlate + 5 )* 1000;
        System.out.println("timeoutGopassPlate: "+ timeoutGopassPlate);

    }

    public void loadComponents(Manguera manguera) {
        if (!procesando) {
            mostrarMensaje(ICONO_LOADER, "Consultando placas");
            futureReference = CompletableFuture.supplyAsync(() -> {
                return consultarPlacas(manguera);
            }).thenAccept(x -> {
                procesarRespuesta((JsonArray) infoPlacas);
            });
            procesando = true;
        } else {
            NovusUtils.printLn("Consulta en Proceso");
        }
    }

    private void procesarRespuesta(JsonArray infoConsulta) {
        procesando = false;
        PanelMediosPago.jPanel1.setVisible(false);
        if (infoConsulta == null || infoConsulta.size() == 0) {

            if (infoPlacasError != null && infoPlacasError.has("mensajeError") && infoPlacasError.get("mensajeError") != null) {
                mostrarMensaje(ICONO_ERROR, infoPlacasError.get("mensajeError").getAsString());
            } else {
                mostrarMensaje(ICONO_ERROR, "Fallo de red - Error de conexión - intente con otro medio de pago.");
            }
        } else {
            NovusUtils.printLn("Info Consultas: " + infoConsulta);
            RenderPanelPlacasGopass renderPlacas = new RenderPanelPlacasGopass();
            renderPlacas.renderPlacasGopass(pnlPlacasEstado, infoPlacas);
            mostrarPanel(PNL_PLACAS);
        }
    }

    private JsonArray consultarPlacas(Manguera manguera) {
        NovusUtils.printLn("Consultando Placas....");
        infoPlacasError = null;
        infoPlacas = new JsonArray();
        System.out.println("timeoutGopassPlate: "+ timeoutGopassPlate);
        JsonObject obj = GopassFacade.consultarPlacasGopass(manguera, timeoutGopassPlate);
        if (obj == null || obj.isJsonNull() || obj.entrySet().isEmpty()) {
            infoPlacasError = new JsonObject();
            infoPlacasError.addProperty("mensajeError", "Fallo de red - Error de conexión - Intente con otro medio de pago");
            return null; 
        }
        
        if (obj.get("error") != null && !obj.get("error").isJsonNull()) {
            JsonObject error = obj.get("error").getAsJsonObject();
            System.out.println("com.firefuel.mediospago.PanelSeleccionPlacas.consultarPlacas()" + error.toString());

            if (error.has("mensajeError") && error.get("mensajeError") != null) {
                infoPlacasError = error;
                return null;
            }
        }

        infoPlacas = obj.get("datos").getAsJsonArray();
        NovusUtils.printLn("infoPlacas  consultarPlacas "+ infoPlacas);
        if (infoPlacas != null && infoPlacas.size() != 0) {
            return infoPlacas;
        }
        return null;
    }

    public void cancelarConsultaPlacas() {
        if (futureReference != null) {
            futureReference.cancel(true);
        }
    }

    private void mostrarMensaje(String imagen, String mensaje) {
        lblIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        lblMensaje.setText("<html><center>" + mensaje + "</center></html>");
        mostrarPanel(PNL_LOADER);
    }

    private void mostrarPanel(String panel) {
        NovusUtils.printLn("[mostrarPanel]" + panel);
        CardLayout cardPanelLayout = (CardLayout) pnlPlacasEstado.getLayout();
        cardPanelLayout.show(pnlPlacasEstado, panel);
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPlacasContainer = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlPlacasEstado = new javax.swing.JPanel();
        pnlLoader = new javax.swing.JPanel();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblIcono = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        pnlPlacas = new javax.swing.JPanel();
        fndPlacas = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();

        setBackground(new java.awt.Color(242, 241, 247));
        setMinimumSize(new java.awt.Dimension(1033, 252));
        setPreferredSize(new java.awt.Dimension(1033, 252));
        setLayout(null);

        pnlPlacasContainer.setBackground(new java.awt.Color(242, 241, 247));
        pnlPlacasContainer.setPreferredSize(new java.awt.Dimension(1033, 250));
        pnlPlacasContainer.setLayout(null);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(152, 153, 157));
        lblTitulo.setText("Seleccione placa");
        pnlPlacasContainer.add(lblTitulo);
        lblTitulo.setBounds(10, 8, 170, 27);

        pnlPlacasEstado.setBackground(new java.awt.Color(242, 241, 247));
        pnlPlacasEstado.setLayout(new java.awt.CardLayout());

        pnlLoader.setBackground(new java.awt.Color(242, 241, 247));

        panelRedondo1.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo1.setRoundBottomLeft(20);
        panelRedondo1.setRoundBottomRight(20);
        panelRedondo1.setRoundTopLeft(20);
        panelRedondo1.setRoundTopRight(20);
        panelRedondo1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panelRedondo1MouseReleased(evt);
            }
        });
        panelRedondo1.setLayout(null);

        lblIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader128px.gif"))); // NOI18N
        lblIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panelRedondo1.add(lblIcono);
        lblIcono.setBounds(110, 36, 115, 112);

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(186, 12, 47));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensaje.setText("Consultando placas");
        panelRedondo1.add(lblMensaje);
        lblMensaje.setBounds(308, 36, 660, 112);

        javax.swing.GroupLayout pnlLoaderLayout = new javax.swing.GroupLayout(pnlLoader);
        pnlLoader.setLayout(pnlLoaderLayout);
        pnlLoaderLayout.setHorizontalGroup(
            pnlLoaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRedondo1, javax.swing.GroupLayout.DEFAULT_SIZE, 1033, Short.MAX_VALUE)
        );
        pnlLoaderLayout.setVerticalGroup(
            pnlLoaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRedondo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlPlacasEstado.add(pnlLoader, "pnlLoader");

        pnlPlacas.setBackground(new java.awt.Color(242, 241, 247));
        pnlPlacas.setLayout(null);

        fndPlacas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlPlacas.add(fndPlacas);
        fndPlacas.setBounds(0, 0, 1033, 197);

        pnlPlacasEstado.add(pnlPlacas, "pnlPlacas");

        pnlPlacasContainer.add(pnlPlacasEstado);
        pnlPlacasEstado.setBounds(0, 42, 1033, 197);
        pnlPlacasContainer.add(fnd);
        fnd.setBounds(0, 0, 1033, 250);

        add(pnlPlacasContainer);
        pnlPlacasContainer.setBounds(0, 0, 1033, 250);
    }// </editor-fold>//GEN-END:initComponents

    private void panelRedondo1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRedondo1MouseReleased
        evt.consume();
    }//GEN-LAST:event_panelRedondo1MouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel fndPlacas;
    private javax.swing.JLabel lblIcono;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblTitulo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    private javax.swing.JPanel pnlLoader;
    private javax.swing.JPanel pnlPlacas;
    private javax.swing.JPanel pnlPlacasContainer;
    private javax.swing.JPanel pnlPlacasEstado;
    // End of variables declaration//GEN-END:variables
}

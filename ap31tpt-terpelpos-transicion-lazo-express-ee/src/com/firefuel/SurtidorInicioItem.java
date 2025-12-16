package com.firefuel;

import com.application.useCases.sutidores.GetTimeoutTotalizadoresUseCase;
import com.bean.Surtidor;
import com.dao.DAOException;
import com.dao.SurtidorDao;
import com.facade.SurtidorFacade;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SurtidorInicioItem extends javax.swing.JPanel {

    final int _SALTO_LECTURA = 0, _ERROR = 1, INDETERMINATE = 2, NORMAL = 3;
    public static final int PUMP_STATE_INDETERMINATE = 1;
    public static final int PUMP_STATE_ERROR = 2;
    public static final int PUMP_STATE_SUCCESS = 3;
    public static final int PUMP_STATE_LOADING = 4;
    public static final int PUMP_STATE_ERROR_DIFF = 5;
    private int state = PUMP_STATE_INDETERMINATE;
    private final Surtidor model;
    private ArrayList<Surtidor> totalizators = null;
    Thread task = null;
    MyCallback NotificacionPrincipal;
    private int timeoutSurtidor = 0;
    private GetTimeoutTotalizadoresUseCase getTimeoutTotalizadoresUseCase;

    public ArrayList<Surtidor> getTotalizators() {
        return totalizators;
    }

    public void setTotalizators(ArrayList<Surtidor> totalizators) {
        this.totalizators = totalizators;
    }

    public SurtidorInicioItem(Surtidor model, MyCallback notificacion) {
        initComponents();
        this.model = model;
        this.NotificacionPrincipal = notificacion;
        this.init();
    }

    void init() {
        this.loadComponent();

    }

    void loadComponent() {
        try {
            getTimeoutTotalizadoresUseCase = new GetTimeoutTotalizadoresUseCase();
            timeoutSurtidor = getTimeoutTotalizadoresUseCase.execute();
            this.pump_number.setText(this.model.getSurtidor() + "");
            this.renderState(this.getState());
        } catch (Exception ex) {
            Logger.getLogger(SurtidorInicioItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getState() {
        return state;
    }

    public Surtidor getModel() {
        return this.model;
    }

    public void setState(int state) {
        this.state = state;
        this.renderState(state);
        if (state == PUMP_STATE_LOADING) {
            setTotalizators(null);
            this.fetchTotalizators();
        } else if (state == PUMP_STATE_INDETERMINATE && this.task != null) {
            try {
                this.task.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SurtidorInicioItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void fetchTotalizators() {
        this.task = new Thread() {
            @Override
            public void run() {
                JsonObject response = SurtidorFacade.fetchTotalizatorsByPump(getModel(), timeoutSurtidor);
                ArrayList<Surtidor> _totalizators = null;
                String error = " ERROR AL OBTENER LECTURAS " + model.getSurtidor();
                boolean salto = false;
                if (response != null) {
                    if (response.get("codigoError") != null) {
                        if (!response.get("codigoError").isJsonNull() && response.get("codigoError").getAsString().equals("40120")) {
                            salto = true;
                        }
                        if (!response.get("mensajeError").isJsonNull()) {
                            System.err.println(response.get("mensajeError").getAsString().toUpperCase());
                            error = "FALLA SURTIDOR : " + model.getSurtidor() + "  " + response.get("mensajeError").getAsString().toUpperCase();
                        }
                    } else {
                        for (JsonElement element : response.getAsJsonArray("data")) {
                            if (_totalizators == null) {
                                _totalizators = new ArrayList<>();
                            }
                            Surtidor pumpData = new Surtidor();
                            JsonObject object = element.getAsJsonObject();
                            pumpData.setSurtidor(object.get("surtidor").getAsInt());
                            pumpData.setCara(object.getAsJsonObject().get("cara").getAsInt());
                            pumpData.setManguera(object.getAsJsonObject().get("manguera").getAsInt());
                            pumpData.setGrado(object.getAsJsonObject().get("grado").getAsInt());
                            pumpData.setIsla(object.getAsJsonObject().get("isla").getAsInt());
                            pumpData.setFamiliaIdentificador(object.getAsJsonObject().get("familiaIdentificador").getAsInt());
                            pumpData.setFamiliaDescripcion(object.getAsJsonObject().get("familiaDescripcion").getAsString());

                            pumpData.setProductoIdentificador(object.getAsJsonObject().get("productoIdentificador").getAsInt());
                            pumpData.setProductoDescripcion(object.getAsJsonObject().get("productoDescripcion").getAsString());
                            pumpData.setProductoPrecio(object.getAsJsonObject().get("precio").getAsFloat());

                            pumpData.setTotalizadorVolumen(object.getAsJsonObject().get("acumuladoVolumen").getAsLong());
                            pumpData.setTotalizadorVolumenReal(object.getAsJsonObject().get("acumuladoVolumenReal").getAsLong());
                            pumpData.setTotalizadorVenta(object.getAsJsonObject().get("acumuladoVenta").getAsLong());
                            pumpData.setFactorInventario(object.getAsJsonObject().get("factor_inventario").getAsInt());
                            pumpData.setFactorVolumenParcial(object.getAsJsonObject().get("factor_volumen_parcial").getAsInt());
                            pumpData.setFatorImporteParcial(object.getAsJsonObject().get("factor_importe_parcial").getAsInt());
                            pumpData.setFactorPrecio(object.getAsJsonObject().get("factor_precio").getAsInt());
                            _totalizators.add(pumpData);
                        }

                    }
                }
                setTotalizators(_totalizators);
                if (salto) {
                    setState(PUMP_STATE_ERROR_DIFF);
                } else {
                    NotificacionPrincipal.sendNotificacion(error);
                    setState(_totalizators != null ? PUMP_STATE_SUCCESS : PUMP_STATE_ERROR);
                    if (_totalizators != null) {
                        NotificacionPrincipal.sendNotificacion(" LECTURAS OBTENIDAS " + model.getSurtidor());
                    } else {
                        NotificacionPrincipal.sendNotificacion(error);
                    }
                }
            }
        };
        this.task.start();
    }

    void changeBackgroundPumpState(int caseId) {
        switch (caseId) {
            case _ERROR:
            case _SALTO_LECTURA:
                this.background.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/surtidorSeleccionadoError.png")));
                break;
            case INDETERMINATE:
                this.background.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/surtidorIndeterminado.png")));
                break;
            case NORMAL:
                this.background.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/surtidorSeleccionado.png")));
                break;
        }
    }

    void renderState(int state) {

        switch (state) {
            case PUMP_STATE_LOADING:
                changeBackgroundPumpState(NORMAL);
                this.sync_pump_state.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loaderTotalizadores.gif")));
                this.sync_pump_state_label.setText("CARGANDO");

                this.sync_pump_state.setFocusable(false);
                break;
            case PUMP_STATE_INDETERMINATE:
                changeBackgroundPumpState(INDETERMINATE);
                this.sync_pump_state.setIcon(null);
                this.sync_pump_state_label.setText("");
                this.sync_pump_state.setFocusable(true);
                break;
            case PUMP_STATE_SUCCESS:
                changeBackgroundPumpState(NORMAL);
                this.sync_pump_state.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/indicadorSurtidorEstadoOk.png")));
                this.sync_pump_state.setFocusable(true);
                this.sync_pump_state_label.setText("LISTO");
                break;
            case PUMP_STATE_ERROR:
                changeBackgroundPumpState(_ERROR);
                this.sync_pump_state.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/indicadorSurtidorEstadoError.png")));
                this.sync_pump_state.setFocusable(true);
                this.sync_pump_state_label.setText("ERROR");
                break;
            case PUMP_STATE_ERROR_DIFF:
                changeBackgroundPumpState(_SALTO_LECTURA);
                this.sync_pump_state.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alerta.png")));
                this.sync_pump_state.setFocusable(true);
                this.sync_pump_state_label.setText("  SALTO");
                break;
        }
    }

    public void toggleSelectedDesign(boolean applySelectedDesign) {
        if (applySelectedDesign && (this.getState() != PUMP_STATE_SUCCESS && this.getState() != PUMP_STATE_LOADING)) {
            setTotalizators(null);
            NotificacionPrincipal.sendNotificacion("OBTENIENDO LECTURAS SURTIDOR " + getModel().getSurtidor() + " ....");
            this.setState(PUMP_STATE_LOADING);
        } else {
            this.setState(PUMP_STATE_INDETERMINATE);
            NotificacionPrincipal.sendNotificacion("");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sync_pump_state_label = new javax.swing.JLabel();
        sync_pump_state = new javax.swing.JLabel();
        pump_number = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        sync_pump_state_label.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        sync_pump_state_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sync_pump_state_label.setText("CARGANDO");
        add(sync_pump_state_label);
        sync_pump_state_label.setBounds(180, 140, 110, 30);

        sync_pump_state.setBackground(new java.awt.Color(0, 0, 0));
        sync_pump_state.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        sync_pump_state.setForeground(new java.awt.Color(255, 255, 255));
        sync_pump_state.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sync_pump_state.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/indicadorSurtidorEstadoOk.png"))); // NOI18N
        sync_pump_state.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                sync_pump_stateMouseReleased(evt);
            }
        });
        add(sync_pump_state);
        sync_pump_state.setBounds(170, 40, 110, 94);

        pump_number.setBackground(new java.awt.Color(102, 255, 0));
        pump_number.setFont(new java.awt.Font("Tahoma", 1, 72)); // NOI18N
        pump_number.setForeground(new java.awt.Color(255, 255, 255));
        pump_number.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pump_number.setText("1");
        add(pump_number);
        pump_number.setBounds(10, 10, 110, 110);

        background.setBackground(new java.awt.Color(0, 0, 0));
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/surtidorIndeterminado.png"))); // NOI18N
        add(background);
        background.setBounds(0, 0, 280, 192);
    }// </editor-fold>//GEN-END:initComponents

    private void sync_pump_stateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sync_pump_stateMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_sync_pump_stateMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel pump_number;
    private javax.swing.JLabel sync_pump_state;
    private javax.swing.JLabel sync_pump_state_label;
    // End of variables declaration//GEN-END:variables

}

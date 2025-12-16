package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.equipos.GetTurnoHorarioUseCase;
import com.bean.BodegaBean;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.facade.SurtidorFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.Timer;

public class InicioSurtidorView extends JDialog {

    InfoViewController parent;
    boolean requiereMedidasTanques = false;
    TreeMap<Integer, SurtidorInicioItem> selectedPumps = new TreeMap<>();
    TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers = new TreeMap<>();
    MyCallback Notificacion;

    public InicioSurtidorView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.init();
    }

    public InicioSurtidorView(InfoViewController parent, boolean modal, boolean requiereMedidasTanques) {
        super(parent, modal);
        initComponents();
        this.requiereMedidasTanques = requiereMedidasTanques;
        this.parent = parent;
        this.init();
    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
    }

    final void init() {
        Main.LECTURAS_DISPONIBLE = false;
        Notificacion = new MyCallback() {

            @Override
            public void sendNotificacion(String mensaje) {
                continue_button.setVisible(totalizadoresValidos());
                label_mensaje_error.setText(mensaje);
            }

            @Override
            public void run(ArrayList<BodegaBean> data) {
                throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods,
                // choose Tools | Templates.
            }

            @Override
            public void run() {
                throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods,
                // choose Tools | Templates.
            }
        };
        loadView();
        loadData();
    }

    ArrayList<Surtidor> fetchPumpsList() {
        return SurtidorFacade.getAllPumps();
    }

    void renderPumps(ArrayList<Surtidor> pumps) {
        int panelHeight = this.pumps_container.getHeight();
        if (pumps != null) {
            int j = 0, i = 0;
            final int componentHeight = 192;
            final int componentWidth = 280;
            final int offset = 40;
            final int panelWidth = 1280 - offset;
            final int ncols = panelWidth / componentWidth;
            final int availableWidth = panelWidth / ncols;
            int n1 = 1;
            int n2 = 1;
            for (Surtidor pump : pumps) {
                SurtidorInicioItem pumpComponent = new SurtidorInicioItem(pump, Notificacion);
                pumpComponent.setBounds((j * availableWidth + (offset / 2)),
                        ((i * componentHeight) + (offset * (i + 1))), componentWidth, componentHeight);
                j++;
                if (j == (ncols)) {
                    j = 0;
                    i++;
                }
                pumpComponent.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        if (pumpComponent.getState() != SurtidorInicioItem.PUMP_STATE_LOADING) {
                            if (selectedPumps.containsKey(pump.getSurtidor())) {
                                selectedPumps.remove(pump.getSurtidor());
                            } else {
                                selectedPumps.put(pump.getSurtidor(), pumpComponent);
                            }
                            pumpComponent.toggleSelectedDesign(selectedPumps.containsKey(pump.getSurtidor()));
                        }
                    }
                });
                this.pumps_container.add(pumpComponent);
                if (n1 > 8) {
                    n2 += 1;
                    n1 = 1;
                }
            }

            panelHeight = Math.max(panelHeight, ((componentHeight * i) + (offset * (i + 1))));
            int altoFinal = panelHeight + (componentHeight / n2) + offset;
            this.pumps_container.setPreferredSize(new Dimension(1280, altoFinal));
        }
    }

    void saveTotalizators() {
        Notificacion.sendNotificacion("GUARDANDO TOTALIZADORES ...");
        if (this.selectedPumps.isEmpty()) {
            mostrarPanelMensaje("DEBE SELECCIONAR AL MENOS 1 SURTIDOR", "/com/firefuel/resources/btBad.png", LetterCase.FIRST_UPPER_CASE);
        } else {
            block:
            {
                for (Map.Entry<Integer, SurtidorInicioItem> entry : this.selectedPumps.entrySet()) {
                    SurtidorInicioItem pumpComponent = entry.getValue();
                    if (pumpComponent.getTotalizators() == null) {
                        mostrarPanelMensaje("NO SE PUDIERON OBTENER TOTALIZADORES DEL SURTIDOR "
                                + pumpComponent.getModel().getSurtidor(), "/com/firefuel/resources/btBad.png", LetterCase.FIRST_UPPER_CASE);
                        selectedTotalizers.clear();
                        break block;
                    } else {
                        selectedTotalizers.put(pumpComponent.getModel().getSurtidor(), pumpComponent.getTotalizators());
                    }
                }
                this.cerrar();
                if (this.requiereMedidasTanques) {
                    LecturasTanquesViewController tanqueView = LecturasTanquesViewController.getInstance(this.parent,
                            true, this.selectedTotalizers);
                    tanqueView.setViewSurtidor(this);
                    tanqueView.setVisible(true);
                } else {
                    InfoViewController.instanciaInicioTurno = TurnosIniciarViewController.getInstance(this.parent, this.selectedTotalizers, true);
                    InfoViewController.instanciaInicioTurno.setViewSurtidor(this);
                    InfoViewController.instanciaInicioTurno.setVisible(true);
                }
            }
        }
    }

    boolean totalizadoresValidos() {
        if (this.selectedPumps.isEmpty()) {
            return false;
        } else {
            boolean status = true;
            for (Map.Entry<Integer, SurtidorInicioItem> entry : this.selectedPumps.entrySet()) {
                SurtidorInicioItem pumpComponent = entry.getValue();
                if (pumpComponent.getTotalizators() == null) {
                    status = false;
                    break;
                }
            }
            return status;
        }
    }

    void loadData() {
        Notificacion.sendNotificacion("Obteniendo surtidores...");
        this.renderPumps(this.fetchPumpsList());
        Notificacion.sendNotificacion("");
        setTimeout(1, () -> {
            try {
                validarLecturaDia();
            } catch (ParseException ex) {
                Logger.getLogger(InicioSurtidorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    int seconds = 0;
    Timer timer;

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

    boolean validarLecturaDia() throws ParseException {
        if (requiereMedidasTanques) {

            EquipoDao dao = new EquipoDao();
            DateFormat sdh = new SimpleDateFormat(NovusConstante.FORMAT_TIME_HH);
            Calendar fechaActual = Calendar.getInstance();
            SimpleDateFormat sdf1 = new SimpleDateFormat(NovusConstante.FORMAT_DATE);

            int horaActual = Integer.parseInt(sdh.format(fechaActual.getTime()));

            String turnoId = dao.getParametroWacher(NovusConstante.PREFERENCE_LECTURA_MEDIDA_TANQUE_TURNO);
            if (turnoId != null && !turnoId.isEmpty()) {

                try {
                    long turno = Long.parseLong(turnoId);
                    GetTurnoHorarioUseCase getTurnoHorarioUseCase = new GetTurnoHorarioUseCase(turno);
                    JsonObject turnoJson = getTurnoHorarioUseCase.execute();
                    if (turnoJson != null) {

                        int hHoraI = Integer.parseInt(turnoJson.get("horaInicio").getAsString().split(":")[0]);
                        int hHoraF = Integer.parseInt(turnoJson.get("horaFin").getAsString().split(":")[0]);

                        NovusUtils.printLn("Hora Inicial : " + hHoraI);
                        NovusUtils.printLn(":____:::::::::::::::::::::::");
                        NovusUtils.printLn("Hora Actual : " + horaActual);
                        NovusUtils.printLn(":____:::::::::::::::::::::::");
                        NovusUtils.printLn("Hora final : " + hHoraF);

                        // 01H >= 22H
                        // HF <= HI -> 22 06 si el ultimo turno 22 - 06 22 -> 05
                        // horaActual = 6;
                        /* se valida si el turno pasa al dia siguiente */
                        if (hHoraF > horaActual && horaActual < hHoraI) {
                            fechaActual.set(Calendar.DAY_OF_MONTH, fechaActual.get(Calendar.DAY_OF_MONTH) - 1);
                        }

                        String existeDia = dao.getParametro("MEDIDA_" + sdf1.format(fechaActual.getTime()));

                        if (existeDia == null) {
                            if (horaActual >= hHoraI && horaActual < hHoraF
                                    || (hHoraF <= hHoraI && horaActual < hHoraF)) {
                                requiereMedidasTanques = true;
                                return true;
                            } else {
                                requiereMedidasTanques = false;
                                NovusUtils.printLn("NO SE VA A SOLICITAR LAS MEDIDAS DE LOS TANQUES");
                            }
                        } else {
                            requiereMedidasTanques = false;
                            NovusUtils.printLn("NO SE VA A SOLICITAR LAS MEDIDAS DE LOS TANQUES POR QUE YA ESTÁ REGISTRADO");
                        }
                    }
                } catch (NumberFormatException e) {
                    NovusUtils.printLn("ERROR AL VERRIFICAR LAS MEDIDAS");
                }
            }
        }
        return false;
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        label_mensaje_error = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        continue_button = new javax.swing.JLabel();
        sub_heading = new javax.swing.JLabel();
        pumps_container_scroll = new javax.swing.JScrollPane();
        pumps_container = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        heading = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        label_mensaje_error.setFont(new java.awt.Font("Tahoma", 0, 26)); // NOI18N
        label_mensaje_error.setForeground(new java.awt.Color(255, 255, 255));
        label_mensaje_error.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_mensaje_error.setText("Consultando Lecturas, por favor espere");
        pnl_principal.add(label_mensaje_error);
        label_mensaje_error.setBounds(144, 724, 770, 60);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(1170, 3, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        continue_button.setFont(new java.awt.Font("Bebas Neue", 1, 24)); // NOI18N
        continue_button.setForeground(new java.awt.Color(255, 255, 255));
        continue_button.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        continue_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        continue_button.setText("CONTINUAR");
        continue_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        continue_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                continue_buttonMouseReleased(evt);
            }
        });
        pnl_principal.add(continue_button);
        continue_button.setBounds(930, 725, 180, 60);

        sub_heading.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        sub_heading.setText("SURTIDORES:");
        pnl_principal.add(sub_heading);
        sub_heading.setBounds(10, 90, 510, 60);

        pumps_container_scroll.setBorder(null);
        pumps_container_scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pumps_container_scroll.setToolTipText("");
        pumps_container_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pumps_container_scroll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pumps_container_scroll.setMinimumSize(new java.awt.Dimension(0, 0));
        pumps_container_scroll.setOpaque(false);
        pumps_container_scroll.setPreferredSize(new java.awt.Dimension(1280, 470));
        pumps_container_scroll.setViewportView(null);

        pumps_container.setBackground(new java.awt.Color(0, 0, 0));
        pumps_container.setForeground(new java.awt.Color(255, 255, 255));
        pumps_container.setOpaque(false);

        javax.swing.GroupLayout pumps_containerLayout = new javax.swing.GroupLayout(pumps_container);
        pumps_container.setLayout(pumps_containerLayout);
        pumps_containerLayout.setHorizontalGroup(
            pumps_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1280, Short.MAX_VALUE)
        );
        pumps_containerLayout.setVerticalGroup(
            pumps_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );

        pumps_container_scroll.setViewportView(pumps_container);
        pumps_container.getAccessibleContext().setAccessibleParent(pumps_container_scroll);

        pnl_principal.add(pumps_container_scroll);
        pumps_container_scroll.setBounds(0, 150, 1280, 550);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(logo);
        logo.setBounds(10, 700, 110, 100);

        heading.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        heading.setForeground(new java.awt.Color(255, 255, 255));
        heading.setText("INICIO TURNO");
        pnl_principal.add(heading);
        heading.setBounds(120, 0, 720, 80);

        back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backMouseReleased(evt);
            }
        });
        pnl_principal.add(back);
        back.setBounds(10, 10, 70, 71);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        background.setMaximumSize(new java.awt.Dimension(1280, 720));
        background.setMinimumSize(new java.awt.Dimension(1280, 720));
        background.setPreferredSize(new java.awt.Dimension(1280, 720));
        pnl_principal.add(background);
        background.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void continue_buttonMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_continue_buttonMouseReleased
        this.saveTotalizators();
    }// GEN-LAST:event_continue_buttonMouseReleased

    private void backMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void cerrar() {
        this.setVisible(false);
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back;
    private javax.swing.JLabel background;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel continue_button;
    private javax.swing.JLabel heading;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel label_mensaje_error;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private javax.swing.JPanel pumps_container;
    private javax.swing.JScrollPane pumps_container_scroll;
    private javax.swing.JLabel sub_heading;
    // End of variables declaration//GEN-END:variables

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(FidelizacionVentas.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono, String letterCase) {

        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(true).setRunnable(runnable)
                .setLetterCase(letterCase).build();
        
        pnl_container.add("card_mensajes", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        Async(runnable, 3);
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    }

}

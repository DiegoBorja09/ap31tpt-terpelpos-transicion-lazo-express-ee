package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.datafonos.HasOngoingSalesInCardReadersPumpStatusUseCase;
import com.application.useCases.datafonos.HasOngoingDatafonSalesUseCase;
import com.bean.AsignacionClienteBean;
import com.bean.BonoViveTerpel;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.Notificador;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.DatafonosDao;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.firefuel.datafonos.ActualizarMediosPagos;
import com.firefuel.datafonos.EnviarPagosDatafonos;
import com.firefuel.datafonos.ParametrizacionDatafanos;
import com.firefuel.datafonos.VentaEnVivoDatafono;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Manguera;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class DatafonosView extends JDialog {

    JFrame parent;
    JDialog parentDialog;
    JsonObject json;
    JsonObject prueba = null;
    Date dateOfToday = new Date();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    String date = format.format(dateOfToday);
    EquipoDao edao = new EquipoDao();
    Runnable cerrar = null;
    Runnable continuar = null;
    Runnable mostarPrincipal = null;
    Runnable runnable = null;
    JsonObject dataResponse = new JsonObject();
    String plaquetaDatafono;
    String adquiriente;
    String codigoTerminal;
    int idAquiriente;
    String serial;
    Runnable cerrarDatafonoParametro;
    boolean pagoMixto = false;
    String tipoVenta = NovusConstante.PAGO_UNICO;
    InfoViewController principal;
    VentaCursoPlaca ventaCursoPlaca;
    ArrayList<MediosPagosBean> mediosPago;
    ArrayList<MediosPagosBean> pagosDatafono;
    MovimientosBean movimiento;
    ArrayList<MediosPagosBean> mediosDePagoSinDatafonos;
    ArrayList<MediosPagosBean> mediosDePagoVenta;
    MovimientosDao mdao = new MovimientosDao();
    ArrayList<Integer> idTransacciones = new ArrayList<>();
    ArrayList<MediosPagosBean> mediosPagosDatafono = null;
    MediosPagosBean medios = new MediosPagosBean();
    JsonObject parametros = new JsonObject();
    Manguera manguera = new Manguera();
    Timer timer = null;
    Timer cerrarVentanaDatafono = null;
    ReciboExtended recibo;
    boolean asignarCliente = false;
    Runnable cerrarTodo = null;
    Notificador notificadorMensaje = null;
    VentaCursoPlaca ventanaCursoPlaca;
    SurtidorDao sdao = new SurtidorDao();

    private final Icon desigButton = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-1.png"));
    private final Icon loaderDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"));
    private final Icon errorDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error enviar pago.png"));
    private final Icon pagoDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/transaccionRecibida.gif"));
    boolean panelSeleccionMedios = false;
    Runnable volverMediosPago;
    Runnable volverPrincipal;
    public HasOngoingSalesInCardReadersPumpStatusUseCase hasOngoingSalesInCardReadersPumpStatusUseCase;

    public DatafonosView() {
    }

    public DatafonosView(JDialog parentDialog, JsonObject json, Runnable cerrar, Runnable continuar) {
        this.parentDialog = parentDialog;
        this.json = json;
        this.cerrar = cerrar;
        this.continuar = continuar;
        initComponents();
        init();
    }

    public DatafonosView(JDialog parentDialog, InfoViewController principal, ArrayList<MediosPagosBean> mediosPagoVenta, MovimientosBean movimiento, Runnable mostarPrincipal, Runnable cerrar, Runnable continuar, ArrayList<MediosPagosBean> mediosDePagoSinDatafonos) {
        this.parentDialog = parentDialog;
        this.cerrar = cerrar;
        this.continuar = continuar;
        this.pagosDatafono = mediosPagoVenta;
        this.movimiento = movimiento;
        this.principal = principal;
        this.mostarPrincipal = mostarPrincipal;
        this.mediosDePagoSinDatafonos = mediosDePagoSinDatafonos;
        initComponents();
        init();
    }

    public DatafonosView(JDialog parentDialog, InfoViewController principal, ArrayList<MediosPagosBean> mediosPagoVenta, MovimientosBean movimiento, Runnable mostarPrincipal, Runnable cerrar, Runnable continuar, ArrayList<MediosPagosBean> mediosDePagoSinDatafonos, ReciboExtended recibo) {
        this.parentDialog = parentDialog;
        this.cerrar = cerrar;
        this.recibo = recibo;
        this.continuar = continuar;
        this.pagosDatafono = mediosPagoVenta;
        this.movimiento = movimiento;
        this.principal = principal;
        this.mostarPrincipal = mostarPrincipal;
        this.mediosDePagoSinDatafonos = mediosDePagoSinDatafonos;

        clonarMedios();
        initComponents();
        init();
    }

    public DatafonosView(InfoViewController principal, ArrayList<MediosPagosBean> mediosPagoVenta, ReciboExtended recibo, MovimientosBean movimiento, Runnable mostarPrincipal, Runnable cerrar, Runnable continuar, ArrayList<MediosPagosBean> mediosDePagoSinDatafonos, boolean asignarCliente, Runnable cerrarTodo) {
        this.cerrar = cerrar;
        this.continuar = continuar;
        this.pagosDatafono = mediosPagoVenta;
        this.recibo = recibo;
        this.movimiento = movimiento;
        this.mostarPrincipal = mostarPrincipal;
        this.principal = principal;
        this.mediosDePagoSinDatafonos = mediosDePagoSinDatafonos;
        this.asignarCliente = asignarCliente;
        this.cerrarTodo = cerrarTodo;
        initComponents();
        init();
    }

    public DatafonosView(InfoViewController info, VentaCursoPlaca ventasPlacaCursoPlaca, Manguera manguera, JsonObject parametros, MediosPagosBean medios, VentaCursoPlaca ventaCursoPlaca) {
        this.principal = info;
        this.medios = medios;
        this.ventaCursoPlaca = ventasPlacaCursoPlaca;
        this.manguera = manguera;
        this.parametros = parametros;
        this.ventanaCursoPlaca = ventaCursoPlaca;
        initComponents();
        init();
    }

    public DatafonosView(InfoViewController info, Manguera manguera, JsonObject parametros, MediosPagosBean medios,
            boolean panelSeleccionMedios, Runnable runnable, Runnable volverPrincipal) {
        this.principal = info;
        this.medios = medios;
        this.manguera = manguera;
        this.parametros = parametros;
        this.panelSeleccionMedios = panelSeleccionMedios;
        this.volverMediosPago = runnable;
        this.volverPrincipal = volverPrincipal;
        initComponents();
        init();
    }

    @SuppressWarnings("unchecked")
    public void clonarMedios() {
        mediosDePagoVenta = (ArrayList<MediosPagosBean>) mediosDePagoSinDatafonos.clone();
    }

    private void init() {
        mediosPago = pagosDatafono;
        tipoVenta();
        loadData();
        NovusUtils.designButtons(btnContinuar, desigButton);
        NovusUtils.designButtons(btnCancelar, desigButton);
        notificadorMensaje = this::recibir;

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });
    }

    public void tipoVenta() {
        if (this.parametros != null && this.parametros.has("ventaEnVivo") && !this.parametros.get("ventaEnVivo").getAsBoolean()) {
            if (pagosDatafono.size() > 1) {
                tipoVenta = NovusConstante.PAGO_MIXTO;
            }
        }

    }

    private void volverPrincipal() {
        if (volverPrincipal != null) {
            volverPrincipal.run();
            volverPrincipal = null;
        }
    }

    void recibir(JsonObject data) {
        mensaje(data);
    }

    void loadData() {
        this.renderDatafonos();
        styleScrollbar();
        habilitarContinuar(false);
    }

    void renderDatafonos() {
        JsonArray datafonosList = solicitarInfo();
        int panelHeight = datafonos_container.getHeight();
        if (datafonosList != null) {
            int number = 1;
            int i = 0;
            int j = 0;
            final int componentHeight = 192;
            final int componentWidth = 378;
            final int offset = 40;
            final int panelWidth = 1280 - offset;
            final int ncols = panelWidth / componentWidth;
            final int availableWidth = panelWidth / ncols;

            for (JsonElement datafono : datafonosList) {
                JsonObject info = datafono.getAsJsonObject();
                DatafonoItem datafonoComponent = new DatafonoItem(number, info.get("id_adquiriente").getAsInt(), info.get("serial").getAsString(),
                        info.get("plaqueta").getAsString(), NovusConstante.DATAFONO_INACTIVO, info.get("proveedor").getAsString(), info.get("codigo_terminal").getAsString());
                datafonoComponent.setBounds((j * availableWidth + (offset / 2)),
                        ((i * componentHeight) + (offset * (i + 1))), componentWidth, componentHeight);
                j++;
                if (j == (ncols)) {
                    j = 0;
                    i++;
                }
                datafonoComponent.background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoDisabled.png")));
                datafonoComponent.background.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        activarDesactivar(evt);
                        plaquetaDatafono = datafonoComponent.getPlaqueta();
                        adquiriente = datafonoComponent.getProveedor();
                        codigoTerminal = datafonoComponent.getTerminal();
                        idAquiriente = datafonoComponent.getIdAdquiriente();
                        serial = datafonoComponent.getSerial();
                    }
                });
                number++;
                datafonos_container.add(datafonoComponent);
            }
            panelHeight = Math.max(panelHeight, ((componentHeight * i) + (offset * (i + 1))));
            datafonos_container.setPreferredSize(new Dimension(1280, panelHeight));
            datafono_container_scroll.setBackground(Color.WHITE);
            JLabel jbackground = new JLabel();
            jbackground.setBounds(0, 0, 1280, 470);
            datafonos_container.add(jbackground);
        }
    }

    private void styleScrollbar() {
        datafono_container_scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        datafono_container_scroll.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        datafono_container_scroll.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    }

    private void activarDesactivar(java.awt.event.MouseEvent evt) {
        reset();
        JLabel label = (JLabel) evt.getComponent();
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoInfo.png")));
        habilitarContinuar(true);
    }

    private void reset() {
        for (Component component : datafonos_container.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component componenteLabel : panel.getComponents()) {
                    JLabel label = (JLabel) componenteLabel;
                    if (label.getText().equals("")) {
                        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/datafonoDisabled.png")));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.hasOngoingSalesInCardReadersPumpStatusUseCase = new HasOngoingSalesInCardReadersPumpStatusUseCase(new JsonObject());
        pnlContainer = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        heading = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JLabel();
        jlTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        datafono_container_scroll = new javax.swing.JScrollPane();
        datafonos_container = new javax.swing.JPanel();
        jclock = ClockViewController.getInstance();
        jLabel30 = new javax.swing.JLabel();
        btnContinuar = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        jTitulo = new javax.swing.JLabel();
        jInfoDatafono = new javax.swing.JLabel();
        jTransacciones = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        fndFondo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jCerrar1 = new javax.swing.JLabel();
        jTituloMen = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jIconoMen = new javax.swing.JLabel();
        fndFondo1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlContainer.setMaximumSize(new java.awt.Dimension(1280, 800));
        pnlContainer.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlContainer.setLayout(new java.awt.CardLayout());

        jPanel2.setLayout(null);

        heading.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        heading.setForeground(new java.awt.Color(255, 255, 255));
        heading.setText("CONFIRMACIÓN DE VENTA");
        jPanel2.add(heading);
        heading.setBounds(120, 2, 720, 80);

        btnCancelar.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btnCancelar.setText("CANCELAR");
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnCancelarMouseReleased(evt);
            }
        });
        jPanel2.add(btnCancelar);
        btnCancelar.setBounds(720, 725, 180, 60);

        jlTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jlTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlTitle.setText("Seleccione el datáfono para realizar el pago");
        jlTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jlTitle);
        jlTitle.setBounds(10, 100, 590, 70);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separator.png"))); // NOI18N
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel3);
        jLabel3.setBounds(18, 168, 535, 5);

        datafono_container_scroll.setBackground(new java.awt.Color(255, 255, 255));
        datafono_container_scroll.setBorder(null);
        datafono_container_scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        datafono_container_scroll.setToolTipText("");
        datafono_container_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        datafono_container_scroll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        datafono_container_scroll.setMinimumSize(new java.awt.Dimension(0, 0));
        datafono_container_scroll.setOpaque(false);
        datafono_container_scroll.setPreferredSize(new java.awt.Dimension(1280, 470));
        datafono_container_scroll.setViewportView(null);

        datafonos_container.setBackground(new java.awt.Color(255, 255, 255));
        datafonos_container.setForeground(new java.awt.Color(255, 255, 255));
        datafonos_container.setLayout(null);
        datafono_container_scroll.setViewportView(datafonos_container);

        jPanel2.add(datafono_container_scroll);
        datafono_container_scroll.setBounds(0, 180, 1280, 470);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setOpaque(false);
        jclock.setLayout(null);
        jPanel2.add(jclock);
        jclock.setBounds(1150, 720, 110, 60);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel30);
        jLabel30.setBounds(1130, 2, 10, 80);

        btnContinuar.setBackground(new java.awt.Color(255, 255, 255));
        btnContinuar.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnContinuar.setForeground(new java.awt.Color(255, 255, 255));
        btnContinuar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnContinuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btnContinuar.setText("ENVIAR");
        btnContinuar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContinuar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnContinuarMouseReleased(evt);
            }
        });
        jPanel2.add(btnContinuar);
        btnContinuar.setBounds(920, 725, 190, 60);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel2.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel1);
        jLabel1.setBounds(0, 0, 84, 82);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jPanel2.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        pnlContainer.add(jPanel2, "pnl_principal");

        jPanel1.setName(""); // NOI18N
        jPanel1.setLayout(null);

        jCerrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-2.png"))); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        jPanel1.add(jCerrar);
        jCerrar.setBounds(990, 18, 249, 53);

        jTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitulo.setForeground(new java.awt.Color(186, 12, 47));
        jTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitulo.setText("TITULO MENSAJE");
        jTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jTitulo);
        jTitulo.setBounds(180, 448, 950, 80);

        jInfoDatafono.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jInfoDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfoDatafono.setText("INFORMACION DATAFONO");
        jInfoDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jInfoDatafono);
        jInfoDatafono.setBounds(280, 530, 740, 28);

        jTransacciones.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTransacciones.setForeground(new java.awt.Color(186, 12, 47));
        jTransacciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTransacciones.setText("TRANSACCION");
        jTransacciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jTransacciones);
        jTransacciones.setBounds(280, 570, 740, 28);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel32);
        jLabel32.setBounds(80, 10, 10, 68);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel33);
        jLabel33.setBounds(120, 710, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel1.add(jLabel35);
        jLabel35.setBounds(10, 710, 100, 80);

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"))); // NOI18N
        jIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jIcono);
        jIcono.setBounds(520, 190, 248, 248);

        fndFondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndMsjDatafonos.png"))); // NOI18N
        fndFondo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo.setRequestFocusEnabled(false);
        jPanel1.add(fndFondo);
        fndFondo.setBounds(0, 0, 1280, 800);

        pnlContainer.add(jPanel1, "mensajesDatafono");

        jPanel3.setLayout(null);

        jCerrar1.setBackground(new java.awt.Color(153, 3, 3));
        jCerrar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar1.setForeground(new java.awt.Color(153, 3, 3));
        jCerrar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-2.png"))); // NOI18N
        jCerrar1.setText("CERRAR");
        jCerrar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrar1MouseClicked(evt);
            }
        });
        jPanel3.add(jCerrar1);
        jCerrar1.setBounds(990, 18, 249, 53);

        jTituloMen.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTituloMen.setForeground(new java.awt.Color(186, 12, 47));
        jTituloMen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTituloMen.setText("TITULO MENSAJE");
        jTituloMen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jTituloMen);
        jTituloMen.setBounds(330, 280, 740, 250);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel36);
        jLabel36.setBounds(80, 10, 10, 68);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel37);
        jLabel37.setBounds(120, 710, 10, 80);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel38);
        jLabel38.setBounds(1130, 710, 10, 80);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel3.add(jLabel39);
        jLabel39.setBounds(10, 710, 100, 80);

        jIconoMen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIconoMen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"))); // NOI18N
        jIconoMen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jIconoMen);
        jIconoMen.setBounds(70, 280, 248, 248);

        fndFondo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fndFondo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo1.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setRequestFocusEnabled(false);
        jPanel3.add(fndFondo1);
        fndFondo1.setBounds(0, 0, 1280, 800);

        pnlContainer.add(jPanel3, "mensajes");

        getContentPane().add(pnlContainer);
        pnlContainer.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 803));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMouseReleased
        if (panelSeleccionMedios) {
            if (volverMediosPago != null) {
                volverMediosPago.run();
                volverMediosPago = null;
            }
        } else {
            close();
        }
    }//GEN-LAST:event_btnCancelarMouseReleased

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseReleased
        if (panelSeleccionMedios) {
            if (volverMediosPago != null) {
                volverMediosPago.run();
                volverMediosPago = null;
            }
        } else {
            close();
        }
    }//GEN-LAST:event_jLabel1MouseReleased

    private void btnContinuarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContinuarMouseReleased
        if (btnContinuar.isEnabled()) {
            validarDatafono();
        }
    }//GEN-LAST:event_btnContinuarMouseReleased

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        cerrar();
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jCerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        if (panelSeleccionMedios) {
            volverPrincipal();
        } else {
            if (this.cerrarDatafonoParametro != null) {
                cerrarDatafonoParametro.run();
                cerrarDatafonoParametro = null;
            } else {
                cerrarVentaEnvivo(this.principal);
            }
        }
    }//GEN-LAST:event_jCerrar1MouseClicked
// GEN-LAST:event_jCheckBox1ActionPerformed

    public void validarDatafono() {
        try {
            JsonArray validacionDatafonos = edao.datafonosInfo();
            if (validacionDatafonos != null) {
                DatafonosDao datafonosDao = new DatafonosDao();
                if (!new HasOngoingDatafonSalesUseCase(codigoTerminal).execute()) {
                    for (JsonElement validacionDatafono : validacionDatafonos) {
                        JsonObject info = validacionDatafono.getAsJsonObject();
                        if (info.get("plaqueta").getAsString().equals(plaquetaDatafono)) {
                            if (asignarCliente) {
                                asignarCliente();
                            } else {
                                if (isPagoBono() && !MedioPagosConfirmarViewController.bonosValidados) {
                                    enviarPagoBonoDatafono();
                                } else if (this.parametros != null && this.parametros.has("ventaEnVivo") && this.parametros.get("ventaEnVivo").getAsBoolean()) {
                                    ventaEnvivoDatafonos();
                                } else {
                                    procesarPago(movimiento);
                                }
                            }
                            return;
                        }
                    }
                } else {
                    JsonObject response = new JsonObject();
                    response.addProperty("mensaje", "EXISTE UNA TRANSACCIÓN EN CURSO EN ESTE DATÁFONO");
                    mensajeErrorDatafono(response);
                }

            } else {
                showMessage("NO SE ENCUENTRA INFORMACION DE DATAFONOS",
                        "/com/firefuel/resources/btBad.png",
                        true, this::close, true,
                        LetterCase.FIRST_UPPER_CASE);
            }
        } catch (DAOException | SQLException ex) {
            Logger.getLogger(DatafonosView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isPagoBono() {
        boolean isBonoTerpel = false;
        if (mediosDePagoVenta != null) {
            for (MediosPagosBean medio : mediosDePagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    isBonoTerpel = true;
                    break;
                }
            }
        }
        return isBonoTerpel;
    }

    public void enviarPago() {
        procesarPago(movimiento);
    }

    public void enviarPagoBonoDatafono() {
        JsonArray bonosArray = new JsonArray();
        JsonArray bonosVenta = mdao.getBonosVenta(this.movimiento.getId());
        if (!MedioPagosConfirmarViewController.bonosValidados) {
            showMessage("REDIMIENDO BONOS, POR FAVOR ESPERE....",
                    "/com/firefuel/resources/loader_fac.gif",
                    false, null,
                    false, LetterCase.FIRST_UPPER_CASE);
            for (MediosPagosBean medio : mediosDePagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    long salesForceIdMp = sdao.getCodigoSalesForceMP(medio.getId());
                    for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                        if (!existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                            JsonObject bonoViveTerpelMP = new JsonObject();
                            bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                            bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                            bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                            bonosArray.add(bonoViveTerpelMP);
                        }
                    }
                }
            }
            ValidacionBonosViveTerpel validacionBonos = new ValidacionBonosViveTerpel();
            setTimeout(1, () -> {
                JsonObject respuestaReclamacion = validacionBonos.ReclamacionBonoViveTerpel(recibo, mediosDePagoVenta, this.movimiento.getId(), bonosArray);
                JsonObject respuesta = validacionBonos.procesamientRespuestaReclamacion(respuestaReclamacion, this.movimiento.getId(), bonosArray);
                procesarRespuestaBonos(respuesta);
            });
        }
    }

    public void procesarRespuestaBonos(JsonObject respuesta) {
        String mensaje = respuesta.get("mensaje").getAsString();
        if (respuesta.get("aprobado").getAsBoolean()) {
            showMessage(mensaje, "/com/firefuel/resources/btOk.png",
                    true, this::enviarPago,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            showMessage(mensaje, "/com/firefuel/resources/btBad.png",
                    true, this::close,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    public boolean existeBono(JsonArray bonos, long bonoVoucher) {
        boolean existe = false;
        if (bonos != null) {
            for (JsonElement bono : bonos) {
                JsonObject bonosVenta = bono.getAsJsonObject();
                if (bonoVoucher == bonosVenta.get("AFP").getAsLong()) {
                    existe = true;
                }
            }
        }
        return existe;
    }

    JsonArray solicitarInfo() {
        JsonArray datafonos = new JsonArray();
        try {
            datafonos = edao.datafonosInfo();
        } catch (DAOException | SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return datafonos;
    }

    private void showMessage(String msj, String ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlContainer.getLayout();
        pnlContainer.add("pnl_ext", panel);
        layout.show(pnlContainer, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        datafonos_container.removeAll();
        datafonos_container.revalidate();
        datafonos_container.repaint();
        reset();
        renderDatafonos();
        datafonos_container.repaint();
        btnContinuar.setEnabled(false);
        CardLayout layout = (CardLayout) pnlContainer.getLayout();
        layout.show(pnlContainer, "pnl_principal");
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnCancelar;
    private javax.swing.JLabel btnContinuar;
    private javax.swing.JScrollPane datafono_container_scroll;
    private javax.swing.JPanel datafonos_container;
    private javax.swing.JLabel fndFondo;
    private javax.swing.JLabel fndFondo1;
    private javax.swing.JLabel heading;
    public static javax.swing.JLabel jCerrar;
    public static javax.swing.JLabel jCerrar1;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jIconoMen;
    private javax.swing.JLabel jInfoDatafono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jTitulo;
    private javax.swing.JLabel jTituloMen;
    private javax.swing.JLabel jTransacciones;
    private ClockViewController jclock;
    private javax.swing.JLabel jlTitle;
    private javax.swing.JPanel pnlContainer;
    // End of variables declaration//GEN-END:variables

    public void close() {
        if (this.parametros != null && this.parametros.has("ventaEnVivo") && this.parametros.get("ventaEnVivo").getAsBoolean()) {
            if (ventanaCursoPlaca != null) {
                this.ventanaCursoPlaca.setVisible(true);
                jclock.stopClock();
                this.dispose();
            }
        } else {
            if (cerrar != null) {
                cerrar.run();
            }
            if (timer != null) {
                timer.stop();
            }
        }

    }

    public void continuar() {
        if (continuar != null) {
            continuar.run();
        }
        if (timer != null) {
            timer.stop();
        }
    }

    public void cerrar() {
        if (runnable != null) {
            runnable.run();
            runnable = null;
        }
        if (timer != null) {
            timer.stop();
        }
        if (cerrarVentanaDatafono != null) {
            cerrarVentanaDatafono.stop();
        }
    }

    public void habilitarContinuar(boolean visible) {
        if (visible) {
            btnContinuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-1.png")));
        } else {
            btnContinuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        }
        btnContinuar.setEnabled(visible);
    }

    private void showPanel(String panel, String msjPrincipal, String informacionDatafono, String transacciones, Icon icono, boolean cerrar, Runnable accion) {
        NovusUtils.showPanel(pnlContainer, panel);
        jTitulo.setText(msjPrincipal);
        jInfoDatafono.setText(informacionDatafono);
        jTransacciones.setText(transacciones);
        jIcono.setIcon(icono);
        jCerrar.setVisible(cerrar);
        runnable = accion;
        if (accion != null) {
            setTimeout(5, accion);
        }

    }

    private void showPanelMensaje(String panel, String msjPrincipal, String informacionDatafono, String transacciones, Icon icono, boolean cerrar, Runnable accion) {
        NovusUtils.showPanel(pnlContainer, panel);
        jTituloMen.setText(msjPrincipal);
        jIconoMen.setIcon(icono);
        jCerrar.setVisible(cerrar);
        runnable = accion;
        if (accion != null) {
            setTimeout(5, accion);
        }
    }

    private void procesarPago(MovimientosBean movimiento) {
        EnviarPagosDatafonos datafonoEnviar = new EnviarPagosDatafonos();
        showPanel("mensajesDatafono",
                "Enviando el pago al datáfono",
                "",
                "",
                loaderDatafono,
                false,
                null);
        ActualizarMediosPagos medios = new ActualizarMediosPagos();
        Thread procceso = new Thread() {
            @Override
            public void run() {
                int contador = 0;
                pagoMixto = pagosDatafono.size() > 1 ? Boolean.TRUE : Boolean.FALSE;
                for (MediosPagosBean mediosPagosBean : pagosDatafono) {
                    float totalTransaccion = mediosPagosBean.getRecibido();
                    if (contador == 0) {
                        dataResponse = datafonoEnviar.enviarVentaDatafonos(totalTransaccion, movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto);
                    } else {
                        NovusUtils.pause(10);
                        dataResponse = datafonoEnviar.enviarVentaDatafonos(totalTransaccion, movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto);
                    }
                    contador++;
                }
                Runnable volver = () -> {
                    if (asignarCliente) {
                        asignarCliente();
                    } else {
                        cerrarVentana();
                        if (cerrar != null) {
                            cerrar.run();
                        }
                        if (mostarPrincipal != null) {
                            mostarPrincipal.run();
                            mostarPrincipal = null;
                            dispose();
                        }
                    }
                };
                Runnable verPrincipal = () -> mostrarMenuPrincipal();
                JsonObject respuestaDatafono = dataResponse;
                if (respuestaDatafono.get("estado").getAsInt() == 200) {
                    showPanel("mensajesDatafono",
                            NovusUtils.convertMessage(
                                    LetterCase.FIRST_UPPER_CASE,
                                    respuestaDatafono.get("mensaje").getAsString()),
                            respuestaDatafono.get("informacionDatafono").getAsString(),
                            respuestaDatafono.get("transacciones").getAsString(),
                            pagoDatafono,
                            respuestaDatafono.get("cerrar").getAsBoolean(),
                            volver);
                    if (!mediosDePagoSinDatafonos.isEmpty()) {
                        medios.actulizarMediosDePagoSinDatafonos(movimiento, mediosDePagoSinDatafonos);
                    }

                } else {
                    showPanel("mensajesDatafono",
                            NovusUtils.convertMessage(
                                    LetterCase.FIRST_UPPER_CASE,
                                    respuestaDatafono.get("mensaje").getAsString()),
                            respuestaDatafono.get("informacionDatafono").getAsString(),
                            respuestaDatafono.get("transacciones").getAsString(),
                            errorDatafono,
                            respuestaDatafono.get("cerrar").getAsBoolean(),
                            verPrincipal);
                }
                volver = null;
            }
        };
        procceso.start();
    }

    public void cerrarVentana() {
        this.dispose();
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (runnable != null) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void asignarCliente() {
        long numeroVenta = recibo.getAtributos().get("idTransmision").getAsLong();
        NovusUtils.printLn("ID TRANSMISION: " + numeroVenta);
        JsonObject datafonosInfo = new JsonObject();
        JsonArray datafonos = new JsonArray();
        JsonObject objDatafonos = new JsonObject();
        objDatafonos.addProperty("id_adquiriente", idAquiriente);
        objDatafonos.addProperty("serial", serial);
        objDatafonos.addProperty("proveedor", adquiriente);
        objDatafonos.addProperty("plaqueta", plaquetaDatafono);
        objDatafonos.addProperty("codigo_terminal", codigoTerminal);
        datafonos.add(objDatafonos);
        datafonosInfo.add("Datafonos", datafonos);
        AsignacionClienteBean.agregarInformacionCliente("Datafonos", datafonosInfo);

        cerrarTodo.run();
    }

    private void ventaEnvivoDatafonos() {
        JsonObject datafono = new JsonObject();
        datafono.addProperty("proveedor", adquiriente);
        datafono.addProperty("codigoDatafono", codigoTerminal);
        datafono.addProperty("negocio", "COM");
        datafono.addProperty("codigoAutorizacion", mdao.getCodigoAutorizacion());
        datafono.addProperty("promotor", Main.persona.getId());
        datafono.addProperty("proveedorId", idAquiriente);
        datafono.addProperty("pos", Main.credencial.getEquipos_id() + "");
        datafono.addProperty("tipoVenta", "UNICO");
        VentaEnVivoDatafono ventaEnVivoDatafonos = new VentaEnVivoDatafono();
        DatafonosDao datafonosDao = new DatafonosDao();
        JsonObject objManguera = this.parametros.get("manguera").getAsJsonObject();
        JsonObject placa = this.parametros.get("placa").getAsJsonObject();
        JsonObject parametros = validarParametrizacionDatafono(datafono);
        if (parametros.get("status").getAsBoolean()) {
            boolean datafonoEnCurso = !new HasOngoingDatafonSalesUseCase(codigoTerminal).execute() && !new HasOngoingSalesInCardReadersPumpStatusUseCase(datafono).execute();


            if (datafonoEnCurso && !sdao.validarAtributoDatafonoVentaCurso(manguera.getCara())) {
                if (this.parametros.get("asignarDatos").getAsBoolean()) {
                    ventaEnVivoDatafonos.datosDatafonosEnCurso(placa, objManguera, datafono, Boolean.FALSE, null);
                    ClienteFacturaElectronica cfa = new ClienteFacturaElectronica(principal, ventaCursoPlaca, this.parametros.get("modal").getAsBoolean(), Boolean.TRUE, placa, objManguera);
                    cfa.RESPUESTA_FACTURA_VENTA_EN_VIVO = placa;
                    cfa.IsVentaEnVivo = this.parametros.get("ventaEnVivo").getAsBoolean();
                    cfa.manguera = this.manguera;
                    this.setVisible(Boolean.FALSE);
                    NovusUtils.printLn("abriendo la vista de facturacio electronica desde -> " + DatafonosView.class.getName());
                    cfa.setVisible(Boolean.TRUE);
                } else {
                    ventaEnVivoDatafonos.datosDatafonosEnCurso(placa, objManguera, datafono, Boolean.TRUE, notificadorMensaje);
                }
            } else {
                JsonObject response = new JsonObject();
                String mensajes = !datafonoEnCurso ? "EXISTE UNA TRANSACCIÓN EN CURSO EN ESTE DATÁFONO" : "EXISTE UNA TRANSACCIÓN EN CURSO CON OTRO DATÁFONO";
                response.addProperty("mensaje", mensajes);
                mensajeErrorDatafono(response);
            }

        } else {
            mensajeErrorDatafono(parametros);
        }

    }

    private void mensaje(JsonObject data) {
        Runnable cerraVista = () -> {
            cerrarVentaEnvivo(this.principal);
        };
        showPanelMensaje("mensajes",
                NovusUtils.convertMessage(
                        LetterCase.FIRST_UPPER_CASE,
                        data.get("mensajeError").getAsString()),
                "",
                "",
                new javax.swing.ImageIcon(getClass().getResource(data.get("icono").getAsString())),
                Boolean.TRUE,
                cerraVista);
    }

    private void mensajeErrorDatafono(JsonObject data) {
        Runnable cerraVista = () -> {
            mostrarMenuPrincipal();
        };
        this.cerrarDatafonoParametro = cerraVista;
        showPanelMensaje("mensajes",
                NovusUtils.convertMessage(
                        LetterCase.FIRST_UPPER_CASE,
                        data.get("mensaje").getAsString()),
                "",
                "",
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btBad.png")),
                Boolean.TRUE,
                cerraVista);
    }

    private void cerrarVentaEnvivo(InfoViewController info) {
        principal.setVisible(Boolean.TRUE);
        this.dispose();
    }

    private JsonObject validarParametrizacionDatafono(JsonObject objDatafono) {
        JsonObject dataRespuesta = new JsonObject();
        TreeMap<String, String> parametros = mdao.buscarParametrizacion(objDatafono.get("proveedorId").getAsInt());
        for (ParametrizacionDatafanos parametrosDatafonos : ParametrizacionDatafanos.values()) {
            if (parametros.isEmpty()) {
                dataRespuesta.addProperty("status", Boolean.FALSE);
                dataRespuesta.addProperty("mensaje", "NO HAY PARAMETROS CONFIGURADOS PARA ESTE DATAFONOS");
                return dataRespuesta;
            } else if (parametros.get(parametrosDatafonos.getValor()) == null) {
                dataRespuesta.addProperty("status", Boolean.FALSE);
                dataRespuesta.addProperty("mensaje", "FALTA EL PARAMETRO DE " + parametrosDatafonos.getValor());
                return dataRespuesta;
            } else {
                dataRespuesta.addProperty("status", Boolean.TRUE);
                dataRespuesta.addProperty("mensaje", "TODA LA CON FIGURACION DE DATAFONO ESTÁ CORRECTA");
            }
        }
        return dataRespuesta;
    }

}

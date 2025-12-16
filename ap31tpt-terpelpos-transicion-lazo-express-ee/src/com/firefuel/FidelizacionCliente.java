/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.beans.ParamsLoyaltySinInternet;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;
import com.WT2.loyalty.presentation.handler.AcomulacionPuntosHandler;
import com.WT2.loyalty.presentation.handler.ConsultarClienteHandler;
import com.application.useCases.sutidores.ActualizarFidelizacionUseCase;
import com.application.useCases.ventas.ExisteFidelizacionUseCase;
import com.application.useCases.ventas.SetReaperturaEnUnoUseCase;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.facade.ConfigurationFacade;
import com.facade.FidelizacionFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import jakarta.persistence.PersistenceException;
import teclado.view.common.TecladoNumerico;

/**
 * @author usuario
 */
public class FidelizacionCliente extends javax.swing.JDialog {

    public static final String COMODIN_TARJETA_LIFEMILES = "%";
    public static final String IDENTIFICADOR_CONSTANTE_TARJETA_LIFEMILES = "LM";
    public static final String SEPARADOR_DATA_TARJETA_LIFEMILES = ";";
    private String salePointIdentificator = null;
    JToggleButton lastToggleSelected = null;
    InfoViewController mainFrame = null;
    int identifierTypeId = 0;
    boolean isOnlySearch = true;
    boolean isSaleInLive = false;
    Thread task = null;
    ReciboExtended saleFacture = null;
    public static final int DOCUMENTO_CEDULA = 1;
    public static final int DOCUMENTO_PASAPORTE = 2;
    public static final int DOCUMENTO_CEDULA_EXTRANJERIA = 3;
    public static final int NUMERO_LIFE_MILES = 4;
    public TreeMap<Integer, String> tiposDocumentos = new TreeMap<>();
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    String cerrar;
    public final String rutaRecurso = "/com/firefuel/resources/";
    private AcomulacionPuntosHandler acomulacionPuntosHandler = new AcomulacionPuntosHandler();
    Runnable ventas = null;

    JsonObject header = new JsonObject();
    JsonObject data = new JsonObject();
    int reintentos = 0;
    Long idVenta = 0L;
    int estadoTransaccion = 0;
    SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
    MovimientosDao mdao = new MovimientosDao();
    SurtidorDao sDao = new SurtidorDao();
    String urlAcumulacion = NovusConstante.SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE;
    String urlValidacion = NovusConstante.SECURE_CENTRAL_POINT_CONSULTA_CLIENTE;
    JDialog ventasSinFidelizar = null;
    boolean datosClientes = false;
    private FoundClient foundClientActual = new FoundClient();
    ActualizarFidelizacionUseCase actualizarFidelizacionUseCase;

    public FidelizacionCliente(InfoViewController mainFrame, boolean modal) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.init();
    }

    public FidelizacionCliente(InfoViewController mainFrame, boolean modal, ReciboExtended saleFacture, boolean isOnlySearch) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.saleFacture = saleFacture;
        this.isOnlySearch = isOnlySearch;
        this.init();
    }

    public FidelizacionCliente(InfoViewController mainFrame, boolean modal, ReciboExtended saleFacture, boolean isOnlySearch, Runnable runnable) {
        super(mainFrame, modal);
        initComponents();
        this.ventas = runnable;
        this.mainFrame = mainFrame;
        this.saleFacture = saleFacture;
        this.isOnlySearch = isOnlySearch;
        this.init();
    }

    public FidelizacionCliente(InfoViewController mainFrame, boolean modal, ReciboExtended saleFacture, boolean isOnlySearch, boolean isSaleInLive) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.isOnlySearch = isOnlySearch;
        this.saleFacture = saleFacture;
        this.isSaleInLive = isSaleInLive;
        this.init();
    }

    public FidelizacionCliente(JDialog dialog, boolean datosClientes, boolean isOnlySearch, long id) {
        super(dialog, true);
        initComponents();
        this.datosClientes = datosClientes;
        this.isOnlySearch = isOnlySearch;
        this.idVenta = id;
        this.init();
    }

    public ReciboExtended getSaleFacture() {
        return this.saleFacture;
    }

    public String fetchSalePointIdentificator() {
        return ConfigurationFacade.fetchSalePointIdentificator();
    }

    public String getSalePointIdentificator() {
        return this.salePointIdentificator;
    }

    public void setSalePointIdentificator(String salePointIdentificator) {
        this.salePointIdentificator = salePointIdentificator;
    }

    public void setIdentifierTypeId(int identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }

    @Override
    public void setVisible(boolean b) {
        if (b && this.getSalePointIdentificator() == null) {
            notificacion("NO SE TIENE EL IDENTIFICADOR DE PUNTO DE VENTA", rutaRecurso.concat("btBad.png"), true, 8000, true);
            cerrar = "";
        } else {
            super.setVisible(b);
        }
    }

    void actualizarVentaCurso() {
        this.jlCara.setText(getSaleFacture().getCara());
        this.jlManguera.setText(getSaleFacture().getManguera());
    }

    void renderSaleDetails() {
        ReciboExtended recibo = this.getSaleFacture();
        String salePrefix = (recibo.getAtributos() != null && recibo.getAtributos().get("consecutivo") != null && !recibo.getAtributos().get("consecutivo").isJsonNull())
                ? recibo.getAtributos().get("consecutivo").isJsonPrimitive() ? recibo.getAtributos().get("consecutivo").getAsString() : recibo.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo").getAsString() + "-"
                + recibo.getAtributos().get("consecutivo").getAsJsonObject().get("consecutivo_actual").getAsString()
                : "";
        String saleNumber = String.valueOf(recibo.getNumero());
        this.sale_consecutive.setText(salePrefix + saleNumber);
        this.sale_face.setText(recibo.getCara());

        this.sale_hose.setText(recibo.getMangueraRobusta());

        this.product_name.setText(recibo.getProducto());
        String medida = "GL";
        if (recibo.getAtributos() != null
                && recibo.getAtributos().has("medida")
                && !recibo.getAtributos().get("medida").isJsonNull()
                && recibo.getAtributos().get("medida").isJsonPrimitive()) {

            String medidaRaw = recibo.getAtributos().get("medida").getAsString();
            if (!"UNDEFINED".equalsIgnoreCase(medidaRaw)) {
                medida = medidaRaw.toUpperCase();
            }
        }
        this.sale_volume.setText(String.format("%.3f", recibo.getCantidadFactor()) + " " + medida);
        idVenta = recibo.getNumero();
    }

    void changeLabelButtonAction() {
        this.btn_action.setText(!this.isOnlySearch ? "ACUMULAR" : "CONSULTAR");
    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        if (!this.isOnlySearch && !this.isSaleInLive) {
            this.renderSaleDetails();
        } else if (this.isSaleInLive) {
            this.actualizarVentaCurso();
        }
        this.changeLabelButtonAction();
        this.toggleSaleDetailPanel(!this.isOnlySearch && !this.isSaleInLive);
        this.toggleSaleInLivePanel(!this.isOnlySearch && this.isSaleInLive);
        this.selectIdentifierType(DOCUMENTO_CEDULA, this.toggle_identifier_type_cc);
    }

    void selectIdentifierType(int identifierTypeIdSelected, JToggleButton toggle) {
        if (this.lastToggleSelected == null || toggle != this.lastToggleSelected) {
            if (toggle != null) {
                this.toggleActivationToggleButtonIdentifiersType(toggle, true);
            }
            if (this.lastToggleSelected != null) {
                this.toggleActivationToggleButtonIdentifiersType(this.lastToggleSelected, false);
            }
            this.lastToggleSelected = toggle;
            this.setIdentifierTypeId(identifierTypeIdSelected);
            this.txt_code_identifier.requestFocus();
        } else {
            this.toggleActivationToggleButtonIdentifiersType(this.lastToggleSelected, false);
            this.lastToggleSelected = null;
            this.setIdentifierTypeId(0);
        }
        this.toggleEnableActionButton(this.isIdentifierEntried() && this.isIdentifierTypeSelected());
    }

    boolean isIdentifierEntried() {
        String identifierTxt = this.txt_code_identifier.getText().trim();
        return !identifierTxt.equals("");
    }

    boolean isIdentifierTypeSelected() {
        return this.identifierTypeId > 0;
    }

    void loadData() {
        this.setSalePointIdentificator(this.fetchSalePointIdentificator());
        this.setIdentifiersTypeCodeWithEnum();
    }

    void toggleSaleDetailPanel(boolean active) {
        this.pnl_sale_detail.setVisible(active);
    }

    void toggleSaleInLivePanel(boolean active) {
        this.pnl_sale_inlive.setVisible(active);
    }

    void setIdentifiersTypeCodeWithEnum() {
        this.tiposDocumentos.clear();
        this.tiposDocumentos.put(DOCUMENTO_CEDULA, "CC");
        this.tiposDocumentos.put(DOCUMENTO_CEDULA_EXTRANJERIA, "CE");
        this.tiposDocumentos.put(NUMERO_LIFE_MILES, "NLM");
        this.tiposDocumentos.put(DOCUMENTO_PASAPORTE, "PAS");
    }

    void init() {
        this.loadData();
        this.loadView();
        jNombreCliente.setVisible(false);
        txt_code_identifier.requestFocus();
    }

    void close() {
        this.setVisible(false);
        dispose();
        if (ventas != null) {
            ventas.run();
        }
    }

    void toggleActivationToggleButtonIdentifiersType(JToggleButton toggle, boolean active) {
        if (active) {
            toggle.setBackground(new Color(153, 0, 0));
            toggle.setForeground(Color.WHITE);
        } else {
            toggle.setBackground(Color.WHITE);
            toggle.setForeground(new Color(153, 0, 0));
        }
    }

    void clearCodeIdentifierField() {
        this.txt_code_identifier.setText("");
    }

    void toggleEnableActionButton(boolean active) {
        this.btn_action.setEnabled(active);
    }

    void adaptFieldForCardReader(boolean adapt) {
        this.txt_code_identifier.setEditable(!adapt);
        if (adapt) {
            this.txt_code_identifier.setText("");
        }
    }

    String getIdentifier() {
        return this.txt_code_identifier.getText().trim();
    }

    private void procesamientRespuestaFidelizacion(FoundClient foundClient) {

        switch (foundClient.getStatus()) {
            case 20000:
                if (this.isOnlySearch) {
                    notificacion(("<html><center>".concat(
                                    foundClient.getNombreCliente())
                                    + "</center> Identificado correctamente</html>"),
                            rutaRecurso.concat("btOk.png"),
                            true, 5000, true);
                    cerrar = "";
                } else {
                    jCerrar.setVisible(false);
                    this.foundClientActual = foundClient;
                    mostrarPanel("confirmacion");
                    jNombreCliente.setText(foundClient.getNombreCliente());
                    jNombreCliente.setVisible(true);
                    jMensajeConfirmacion.setText(" ¿Desea acumular? ");
                }
                break;
            case 99:
            case 600:
                notificacion(NovusUtils.convertMessage(
                                LetterCase.FIRST_UPPER_CASE,
                                foundClient.getMensaje()),
                        rutaRecurso.concat("btBad.png"),
                        true, 5000, true);
                cerrar = "";
                break;
            default:
                notificacion("No hay conexión con el Motor de Fidelización",
                        rutaRecurso.concat("btBad.png"),
                        true, 5000, true);
                cerrar = "";

        }

    }

    void handleClientAcumulationResponse(RespuestasAcumulacion response) {
        if (response != null) {
            NovusUtils.ventaFidelizadaAudio();
            notificacion("Operacion realizada",
                    rutaRecurso.concat("btOk.png"),
                    true, 5000, true);
            cerrar = "";
        } else {
            notificacion("Ocurrio un error en la operacion",
                    rutaRecurso.concat("btBad.png"),
                    true, 5000, true);
            cerrar = "";
        }
    }

    private void actualizarEditarFidelizacion(ReciboExtended local, RespuestasAcumulacion response) {
        if (response != null) {
            mdao.actulizarMOvimientoFidelizacion(local.getNumero(), false);
        }

    }

    private void getValidacionCliente() {
        if (this.datosClientes) {
            // mdao.actualizarFidelizacion(idVenta, this.getIdentifier(), this.tiposDocumentos.get(this.identifierTypeId));
            guardarVentaFidelizacion();
            actualizarFidelizacionUseCase.execute(idVenta);
            notificacion("Datos editados exitosamente",
                    rutaRecurso.concat("btOk.png"),
                    true, 5000, true);
            cerrar = "";
        } else {
            String identifier = this.getIdentifier();
            String identifierTypeCode = this.tiposDocumentos.get(this.identifierTypeId);
            String salePointCode = this.getSalePointIdentificator();
            cargar("Consulta cliente por favor espere...",
                    "/com/firefuel/resources/loader_fac.gif");
            NovusUtils.pause(1);

            ConsultarClienteHandler handler = new ConsultarClienteHandler();

            CompletableFuture.runAsync(() -> {
                HashMap<String, String> params = new HashMap<>();
                params.put("identificacionPuntoVenta", salePointCode);
                params.put("codigoTipoIdentificacion", identifierTypeCode);
                params.put("identifier", identifier);
                FoundClient foundClient = handler.execute(params);
                foundClient.getDatosCliente().getCustomer().setNumeroIdentificacion(identifier);

                System.out.println("Mensaje del servicio " + foundClient.getMensaje());
                System.out.println("El usuario " + foundClient.getNombreCliente());
                System.out.println("Encontrado " + foundClient.isExisteClient());
                cerrarMensaje();
                procesamientRespuestaFidelizacion(foundClient);

            });

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        actualizarFidelizacionUseCase = new ActualizarFidelizacionUseCase();

        tipoDocumento = new javax.swing.ButtonGroup();
        btn_group_identifier_type = new javax.swing.ButtonGroup();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        panelCard = new javax.swing.JPanel();
        cardConsultaCliente = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        toggle_identifier_type_cc = new javax.swing.JToggleButton();
        txt_code_identifier = new javax.swing.JTextField();
        toggle_identifier_type_strangecc = new javax.swing.JToggleButton();
        btn_action = new javax.swing.JLabel();
        pnl_sale_inlive = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jlManguera = new javax.swing.JLabel();
        jlCara = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jbackground_inlive = new javax.swing.JLabel();
        pnl_sale_detail = new javax.swing.JPanel();
        lbl_sale_hose = new javax.swing.JLabel();
        sale_hose = new javax.swing.JLabel();
        lbl_sale_face = new javax.swing.JLabel();
        sale_face = new javax.swing.JLabel();
        sale_consecutive = new javax.swing.JLabel();
        lbl_sale_consecutive = new javax.swing.JLabel();
        lbl_sale_volume = new javax.swing.JLabel();
        sale_volume = new javax.swing.JLabel();
        lbl_product_name = new javax.swing.JLabel();
        product_name = new javax.swing.JLabel();
        jbackground = new javax.swing.JLabel();
        pnl_keyboard = new TecladoNumerico();
        cardConsulta = new javax.swing.JPanel();
        jIcono = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jCerrar = new javax.swing.JLabel();
        confirmacion = new javax.swing.JPanel();
        jMensajeConfirmacion = new javax.swing.JLabel();
        jAceptar = new javax.swing.JLabel();
        jDenegar = new javax.swing.JLabel();
        jNombreCliente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(logo);
        logo.setBounds(10, 700, 110, 100);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion);
        jNotificacion.setBounds(750, 10, 520, 70);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 71);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CONSULTA CLIENTE VIVE TERPEL");
        getContentPane().add(jTitle);
        jTitle.setBounds(120, 15, 650, 60);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(90, 10, 10, 68);

        panelCard.setLayout(new java.awt.CardLayout());

        cardConsultaCliente.setBackground(new java.awt.Color(255, 255, 255));
        cardConsultaCliente.setLayout(null);

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("INGRESE IDENTIFICACION:");
        cardConsultaCliente.add(jLabel1);
        jLabel1.setBounds(60, 40, 590, 40);

        toggle_identifier_type_cc.setBackground(new java.awt.Color(246, 246, 246));
        tipoDocumento.add(toggle_identifier_type_cc);
        toggle_identifier_type_cc.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        toggle_identifier_type_cc.setForeground(new java.awt.Color(153, 0, 0));
        toggle_identifier_type_cc.setText("CEDULA");
        toggle_identifier_type_cc.setBorder(null);
        toggle_identifier_type_cc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggle_identifier_type_ccActionPerformed(evt);
            }
        });
        cardConsultaCliente.add(toggle_identifier_type_cc);
        toggle_identifier_type_cc.setBounds(60, 170, 290, 60);

        txt_code_identifier.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_code_identifier.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_code_identifier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 0, 0), 1, true));
        txt_code_identifier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_code_identifierFocusGained(evt);
            }
        });
        txt_code_identifier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_code_identifierKeyTyped(evt);
            }
        });
        cardConsultaCliente.add(txt_code_identifier);
        txt_code_identifier.setBounds(60, 90, 590, 70);

        toggle_identifier_type_strangecc.setBackground(new java.awt.Color(246, 246, 246));
        tipoDocumento.add(toggle_identifier_type_strangecc);
        toggle_identifier_type_strangecc.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        toggle_identifier_type_strangecc.setForeground(new java.awt.Color(153, 0, 0));
        toggle_identifier_type_strangecc.setText("CEDULA EXTRANJERA");
        toggle_identifier_type_strangecc.setBorder(null);
        toggle_identifier_type_strangecc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggle_identifier_type_strangeccActionPerformed(evt);
            }
        });
        cardConsultaCliente.add(toggle_identifier_type_strangecc);
        toggle_identifier_type_strangecc.setBounds(380, 170, 270, 60);

        btn_action.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        btn_action.setForeground(new java.awt.Color(255, 255, 255));
        btn_action.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_action.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btn_action.setText("CONSULTAR");
        btn_action.setEnabled(false);
        btn_action.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_action.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_actionMouseReleased(evt);
            }
        });
        cardConsultaCliente.add(btn_action);
        btn_action.setBounds(220, 550, 270, 60);

        pnl_sale_inlive.setBackground(new java.awt.Color(255, 255, 255));
        pnl_sale_inlive.setOpaque(false);
        pnl_sale_inlive.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("MANGUERA:");
        pnl_sale_inlive.add(jLabel6);
        jLabel6.setBounds(40, 170, 140, 40);

        jlManguera.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jlManguera.setForeground(new java.awt.Color(153, 0, 0));
        jlManguera.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlManguera.setText("0");
        pnl_sale_inlive.add(jlManguera);
        jlManguera.setBounds(210, 170, 370, 40);

        jlCara.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jlCara.setForeground(new java.awt.Color(153, 0, 0));
        jlCara.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlCara.setText("0");
        pnl_sale_inlive.add(jlCara);
        jlCara.setBounds(210, 90, 370, 50);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("CARA:");
        pnl_sale_inlive.add(jLabel4);
        jLabel4.setBounds(40, 90, 140, 50);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("VENTA EN CURSO");
        pnl_sale_inlive.add(jLabel3);
        jLabel3.setBounds(60, 20, 500, 50);

        jbackground_inlive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndVentaenCurso.png"))); // NOI18N
        pnl_sale_inlive.add(jbackground_inlive);
        jbackground_inlive.setBounds(0, 0, 620, 270);

        cardConsultaCliente.add(pnl_sale_inlive);
        pnl_sale_inlive.setBounds(50, 260, 620, 270);

        pnl_sale_detail.setBackground(new java.awt.Color(255, 255, 255));
        pnl_sale_detail.setOpaque(false);
        pnl_sale_detail.setLayout(null);

        lbl_sale_hose.setBackground(new java.awt.Color(153, 0, 0));
        lbl_sale_hose.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_sale_hose.setForeground(new java.awt.Color(102, 102, 102));
        lbl_sale_hose.setText("MANGUERA:");
        pnl_sale_detail.add(lbl_sale_hose);
        lbl_sale_hose.setBounds(20, 110, 170, 50);

        sale_hose.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        sale_hose.setForeground(new java.awt.Color(153, 0, 0));
        sale_hose.setText("0");
        pnl_sale_detail.add(sale_hose);
        sale_hose.setBounds(210, 110, 350, 50);

        lbl_sale_face.setBackground(new java.awt.Color(153, 0, 0));
        lbl_sale_face.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_sale_face.setForeground(new java.awt.Color(102, 102, 102));
        lbl_sale_face.setText("CARA:");
        pnl_sale_detail.add(lbl_sale_face);
        lbl_sale_face.setBounds(20, 70, 170, 40);

        sale_face.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        sale_face.setForeground(new java.awt.Color(153, 0, 0));
        sale_face.setText("0");
        pnl_sale_detail.add(sale_face);
        sale_face.setBounds(210, 70, 350, 40);

        sale_consecutive.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        sale_consecutive.setForeground(new java.awt.Color(153, 0, 0));
        sale_consecutive.setText("PRE000");
        pnl_sale_detail.add(sale_consecutive);
        sale_consecutive.setBounds(210, 20, 350, 50);

        lbl_sale_consecutive.setBackground(new java.awt.Color(153, 0, 0));
        lbl_sale_consecutive.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_sale_consecutive.setForeground(new java.awt.Color(102, 102, 102));
        lbl_sale_consecutive.setText("VENTA:");
        pnl_sale_detail.add(lbl_sale_consecutive);
        lbl_sale_consecutive.setBounds(20, 20, 170, 50);

        lbl_sale_volume.setBackground(new java.awt.Color(153, 0, 0));
        lbl_sale_volume.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_sale_volume.setForeground(new java.awt.Color(102, 102, 102));
        lbl_sale_volume.setText("CANTIDAD:");
        pnl_sale_detail.add(lbl_sale_volume);
        lbl_sale_volume.setBounds(20, 210, 170, 40);

        sale_volume.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        sale_volume.setForeground(new java.awt.Color(153, 0, 0));
        sale_volume.setText("0GL");
        pnl_sale_detail.add(sale_volume);
        sale_volume.setBounds(210, 210, 350, 40);

        lbl_product_name.setBackground(new java.awt.Color(153, 0, 0));
        lbl_product_name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_product_name.setForeground(new java.awt.Color(102, 102, 102));
        lbl_product_name.setText("PRODUCTO:");
        pnl_sale_detail.add(lbl_product_name);
        lbl_product_name.setBounds(20, 160, 170, 40);

        product_name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        product_name.setForeground(new java.awt.Color(153, 0, 0));
        product_name.setText("PRODUCTO");
        pnl_sale_detail.add(product_name);
        product_name.setBounds(210, 160, 350, 40);

        jbackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoVentas.png"))); // NOI18N
        pnl_sale_detail.add(jbackground);
        jbackground.setBounds(0, 0, 620, 270);

        cardConsultaCliente.add(pnl_sale_detail);
        pnl_sale_detail.setBounds(50, 260, 620, 270);
        cardConsultaCliente.add(pnl_keyboard);
        pnl_keyboard.setBounds(690, 90, 570, 470);

        panelCard.add(cardConsultaCliente, "cardConsultaCliente");

        cardConsulta.setBackground(new java.awt.Color(255, 255, 255));
        cardConsulta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btOk.png"))); // NOI18N
        jIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cardConsulta.add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 350, 270));

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(204, 0, 0));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        cardConsulta.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 160, 870, 210));

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(255, 255, 255));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        cardConsulta.add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 420, 290, 70));

        panelCard.add(cardConsulta, "cardConsulta");

        confirmacion.setBackground(new java.awt.Color(255, 255, 255));
        confirmacion.setName("confirmacion"); // NOI18N
        confirmacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMensajeConfirmacion.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensajeConfirmacion.setForeground(new java.awt.Color(204, 0, 0));
        jMensajeConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensajeConfirmacion.setText("¿Confirmacion de mensaje?");
        confirmacion.add(jMensajeConfirmacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 270, 1280, 130));

        jAceptar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jAceptar.setForeground(new java.awt.Color(255, 255, 255));
        jAceptar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jAceptar.setText("SI");
        jAceptar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAceptar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAceptar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jAceptarMouseClicked(evt);
            }
        });
        confirmacion.add(jAceptar, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 440, 300, 60));

        jDenegar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jDenegar.setForeground(new java.awt.Color(255, 255, 255));
        jDenegar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jDenegar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jDenegar.setText("NO");
        jDenegar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDenegar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jDenegar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jDenegarMouseClicked(evt);
            }
        });
        confirmacion.add(jDenegar, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 440, 300, 60));

        jNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jNombreCliente.setForeground(new java.awt.Color(255, 51, 51));
        jNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jNombreCliente.setText("Nombre Cliente");
        jNombreCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        confirmacion.add(jNombreCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 1280, 70));

        panelCard.add(confirmacion, "confirmacion");

        getContentPane().add(panelCard);
        panelCard.setBounds(0, 90, 1280, 620);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        this.close();
    }//GEN-LAST:event_jLabel13MouseReleased

    private void btn_actionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_actionMouseReleased
        NovusUtils.beep();
        if (this.btn_action.isVisible() && txt_code_identifier.getText().length() > 0 && validarCampo("[0-9]{1,10}", txt_code_identifier, 11, jNotificacion)) {
            btn_action.setVisible(false);
            setTimeout(2, () -> btn_action.setVisible(true));
            if (btn_action.getText().equals("CONSULTAR")) {
                consultaCliente();
            } else {
                fidelizacionOffline();
            }
        }
    }//GEN-LAST:event_btn_actionMouseReleased

    public void consultaCliente() {
        this.getValidacionCliente();
    }

    public void fidelizacionOffline() {
        if (NovusConstante.HAY_INTERNET) {
            this.getValidacionCliente();
        } else {
            if (!existeFidelizacion(idVenta)) {
                notificacion("Venta fidelizada",
                        rutaRecurso.concat("btOk.png"),
                        true, 5000, true);
                cerrar = "";
                guardarVentaFidelizacion();
            } else {
                notificacion("Venta ya fidelizada",
                        rutaRecurso.concat("btBad.png"),
                        true, 5000, true);
                cerrar = "";
            }
        }
    }


    private void toggle_identifier_type_ccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggle_identifier_type_ccActionPerformed
        NovusUtils.beep();
        this.selectIdentifierType(DOCUMENTO_CEDULA, (JToggleButton) (evt.getSource()));
    }//GEN-LAST:event_toggle_identifier_type_ccActionPerformed

    private void toggle_identifier_type_strangeccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggle_identifier_type_strangeccActionPerformed
        NovusUtils.beep();
        this.selectIdentifierType(DOCUMENTO_CEDULA_EXTRANJERIA, (JToggleButton) (evt.getSource()));
    }//GEN-LAST:event_toggle_identifier_type_strangeccActionPerformed

    private void txt_code_identifierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_code_identifierKeyTyped
        validarNumeros(evt);
    }//GEN-LAST:event_txt_code_identifierKeyTyped

    private void txt_code_identifierFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_code_identifierFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_code_identifier);
        txt_code_identifier.requestFocus();
    }//GEN-LAST:event_txt_code_identifierFocusGained

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        NovusUtils.beep();
        if (cerrar.equals("")) {
            mostrarPanel("cardConsultaCliente");
            close();
        } else {
            mostrarPanel(cerrar);
        }
        cerrar = "";
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jAceptarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jAceptarMouseClicked
        confirmacion();
    }//GEN-LAST:event_jAceptarMouseClicked

    private void jDenegarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDenegarMouseClicked
        mostrarPanel("cardConsultaCliente");
    }//GEN-LAST:event_jDenegarMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel btn_action;
    private javax.swing.ButtonGroup btn_group_identifier_type;
    private javax.swing.JPanel cardConsulta;
    private javax.swing.JPanel cardConsultaCliente;
    private javax.swing.JPanel confirmacion;
    private javax.swing.JLabel jAceptar;
    private javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jDenegar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jMensajeConfirmacion;
    private javax.swing.JLabel jNombreCliente;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jbackground;
    private javax.swing.JLabel jbackground_inlive;
    private javax.swing.JLabel jlCara;
    private javax.swing.JLabel jlManguera;
    private javax.swing.JLabel lbl_product_name;
    private javax.swing.JLabel lbl_sale_consecutive;
    private javax.swing.JLabel lbl_sale_face;
    private javax.swing.JLabel lbl_sale_hose;
    private javax.swing.JLabel lbl_sale_volume;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel panelCard;
    private javax.swing.JPanel pnl_keyboard;
    private javax.swing.JPanel pnl_sale_detail;
    private javax.swing.JPanel pnl_sale_inlive;
    private javax.swing.JLabel product_name;
    private javax.swing.JLabel sale_consecutive;
    private javax.swing.JLabel sale_face;
    private javax.swing.JLabel sale_hose;
    private javax.swing.JLabel sale_volume;
    private javax.swing.ButtonGroup tipoDocumento;
    public javax.swing.JToggleButton toggle_identifier_type_cc;
    public javax.swing.JToggleButton toggle_identifier_type_strangecc;
    private javax.swing.JTextField txt_code_identifier;
    // End of variables declaration//GEN-END:variables

    private void validarNumeros(KeyEvent evt) {
        this.toggleEnableActionButton(true);
    }

    private void notificacion(String mensaje, String imagen, boolean cerrar, int time, boolean vistaPrincipal) {
        mostrarPanel("cardConsulta");
        jMensaje.setText(mensaje);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(true);
        int t = time / 1000;
        if (cerrar) {
            setTimeout(t, () -> {
                if (!vistaPrincipal) {
                    mostrarPanel(this.cerrar);
                } else {
                    if (ventas != null) {
                        ventas.run();
                    }
                    close();
                }
                this.cerrar = "";
            });
        }
    }

    private void cargar(String mensaje, String imagen) {
        mostrarPanel("cardConsulta");
        jMensaje.setText(mensaje);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(false);
    }

    private void cerrarMensaje() {
        jMensaje.setText("");
        jIcono.setIcon(null);
        jCerrar.setVisible(false);
    }

    private void cargar(String mensaje, String imagen, boolean cerrar, int time, boolean vistaPrincipal) {
        mostrarPanel("cardConsulta");
        jMensaje.setText(mensaje);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        jCerrar.setVisible(false);
        if (cerrar) {
            setTimeout(time, () -> {
                if (vistaPrincipal) {
                    close();
                }
            });
        }
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
        CardLayout layout = (CardLayout) panelCard.getLayout();
        panelCard.add("pnl_ext", panel);
        layout.show(panelCard, "pnl_ext");
    }

    private void mostrarPanel(String panel) {
        CardLayout card = (CardLayout) panelCard.getLayout();
        card.show(panelCard, panel);

    }

    private void confirmacion() {

        jMensajeConfirmacion.setVisible(true);
        jDenegar.setVisible(true);
        jAceptar.setVisible(true);
        Map<String, Object> paramsAcumularRaw = FidelizacionFacade.buildClientAcumulationRequestObject(saleFacture, foundClientActual.getDatosCliente().getCustomer().getNumeroIdentificacion(), foundClientActual.getDatosCliente().getCustomer().getCodigoTipoIdentificacion(), this.getSalePointIdentificator());

        Runnable runnableFidelizacion = () -> {
            this.acomulacionPuntosHandler.execute(paramsAcumularRaw);
            try {
                //mdao.setReaperturaInOne(this.saleFacture.numero);
                new SetReaperturaEnUnoUseCase(this.saleFacture.numero).execute();
            } catch (PersistenceException ex) {
                Logger.getLogger(FidelizacionCliente.class.getName()).log(Level.SEVERE, null, ex);
            }

        };

        CompletableFuture.runAsync(runnableFidelizacion);
        NovusUtils.ventaFidelizadaAudio();
        close();
    }

    private boolean validarCampo(String argumento, JTextField campo, int maximo, JLabel label) {
        return NovusUtils.validarCampos(argumento, campo, maximo, jNotificacion);
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public JsonObject header() {
        JsonObject content = new JsonObject();
        content.addProperty("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        content.addProperty("Content-Type", "application/json");
        content.addProperty("Accept", "*/*");
        content.addProperty("dispositivo", "proyectos");
        content.addProperty("fecha", sdfISO.format(new Date()) + "-05");
        content.addProperty("aplicacion", "lazoexpress");
        header.add("headers", content);
        return header;
    }

    public void ventaEnCursoOffline() {
        ReciboExtended local = this.getSaleFacture();
        JsonObject jsonData = new JsonObject();
        jsonData.add("datosCliente", FidelizacionFacade.buildSearchClientRequestObject(getIdentifier(), this.tiposDocumentos.get(this.identifierTypeId), this.getSalePointIdentificator()));
        sDao.updateVentasEncurso(local, jsonData, 1);
    }

    private boolean guardarVentaFidelizacion() {
        boolean result = false;
        header();
        if (isSaleInLive) {
            ventaEnCursoOffline();
        } else {

            //  data = FidelizacionFacade.buildClientAcumulationRequestObject(this.saleFacture, getIdentifier(), this.tiposDocumentos.get(this.identifierTypeId), this.getSalePointIdentificator());
            header.add("body", data);
            ParamsLoyaltySinInternet paramsLoyaltySinInternet = new ParamsLoyaltySinInternet();
            paramsLoyaltySinInternet.setIdMovimiento(idVenta);
            IdentificationClient identificationClient = new IdentificationClient();
            paramsLoyaltySinInternet.setDatosCliente(identificationClient);
            identificationClient.setCodigoTipoIdentificacion(this.tiposDocumentos.get(this.identifierTypeId));
            identificationClient.setNumeroIdentificacion(txt_code_identifier.getText().trim());
            result = mdao.guardarProcesoFidelizacionSinInternet(paramsLoyaltySinInternet);
            // mdao.guardarTransmisionFidelizacion(header, urlAcumulacion, urlValidacion, NovusConstante.POST, Main.credencial.getEmpresas_id(), idVenta, estadoTransaccion, reintentos);

        }
        return result;
    }

    boolean existeFidelizacion(Long idVenta) {
        //return mdao.existeFidelizacion(idVenta);
        return new ExisteFidelizacionUseCase(idVenta).execute();
    }

    public JsonObject getRequestVenta(long idventa) {
        return mdao.getRequest(idventa);
    }

}

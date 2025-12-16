/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.consecutivos.ObtenerConsecutivoUseCase;
import com.application.useCases.movimientos.InsertCtMovimientosUseCase;
import com.application.useCases.movimientos.ObtenerPromotoresJornadaUseCase;
import com.application.useCases.movimientos.dto.CtMovimientoJsonObjDto;
import com.bean.MedioIdentificacionBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.dao.RumboDao;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.facade.RumboFacade;
import com.facade.VentasCombustibleFacade;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import teclado.view.common.TecladoExtendidoGray;
import teclado.view.common.TecladoNumericoGray;
import com.application.useCases.productos.ObtenerPrecioUreaUseCase;
import com.application.useCases.productos.ObtenerCodigoExternoUreaUseCase;
import com.application.useCases.bodegas.ObtenerIdBodegaUreaUseCase;
import com.application.useCases.transmisiones.InsertarTransmisionUseCase;
import com.bean.PersonaBean;
import static com.firefuel.InfoViewController.turnosPersonas;
import com.firefuel.utils.ShowMessageSingleton;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.firefuel.Main;



/**
 * @author usuario
 */
public class VentaPlacaView extends javax.swing.JDialog {
    ObtenerPrecioUreaUseCase obtenerPrecioUreaUseCase;
    ObtenerCodigoExternoUreaUseCase obtenerCodigoExternoUreaUseCase;
    ObtenerIdBodegaUreaUseCase obtenerIdBodegaUreaUseCase;

    private InfoViewController controller;
    private javax.swing.JPanel pnlPrincipal;
    static int HOSES_TO_SHOW_BY_SLIDE = 8;
    static int IDENTIFIERS_TO_SHOW_BY_SLIDE = 5;
    long aditionalDataTimeout = 0;
    long saleAuthorizationTimeout = 0;
    long pumpUnlockingTimeout = 0;
    String authorizationIdentifier = null;
    ManguerasItem lastHoseSelected = null;
    boolean stopTimeoutAditionalData = true;
    boolean stopTimeoutSale = true;

    InfoViewController mainFrame = null;
    int index = 0;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    long selectedProductFamilyId = 0;
    float selectedProductPrice = 0;
    boolean aditionalDataIsRequerid = false;

    TreeMap<Long, ManguerasItem> hosesItem = new TreeMap<>();

    static TreeMap<Integer, MedioIdentificacionBean> mediosIdentificadores = new TreeMap<>();
    static final String PNL_HOSES_SELECTOR = "pnl_hoses_selector";
    static final String PNL_CAR_DATA = "pnl_car_data";
    static final String PNL_IDENTIFIERS_SELECTOR = "pnl_identifiers_selector";
    static final String PNL_IDENTIFIER_LISTEN = "pnl_identifier_listen";
    static final String PNL_ADITIONAL_DATA = "pnl_aditional_data";
    static final String PNL_SALE = "pnl_sale";
    static final String PNL_SALE_ADBLUE = "pnlsaleAdBlue";
    static String ULTIMO_PANEL;
    String CERRAR = "";

    static final String SUBPNL_ADICIONAL_DATA_CARD = "pnl_aditional_data_card";
    static final String SUBPNL_ADICIONAL_DATA_PLATE = "pnl_aditional_data_license_plate";
    static final String SUBPNL_ADICIONAL_DATA_INFO = "pnl_aditional_data_info";

    static final int RFID_IDENTIFIER_ID = 3;
    static final int IBUTTON_IDENTIFIER_ID = 2;
    static final int CARD_IDENTIFIER_ID = 1;
    static final int NUMERIC_CODE_IDENTIFIER_ID = 5;
    static final int APP_RUMBO_ID = 6;//App Rumbo

    final int TIPO_INFORMACION_ADICIONAL_PLACA;
    final int TIPO_INFORMACION_ADICIONAL_CARD;
    final int TIPO_INFORMACION_ADICIONAL_INFO;

    ObtenerPromotoresJornadaUseCase obtenerPromotoresJornadaUseCase;
    ObtenerConsecutivoUseCase obtenerConsecutivoUseCase;
    ImageIcon iconInfoRed = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/info_producto_red.png"));
    ImageIcon iconInfoBlue = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/info_producto.png"));

    ImageIcon datosAdicionalesInactivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndDatosAdicionalesInactivo.png"));
    ImageIcon datosInfoInactivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndInformacionAdicionalInactivo.png"));

    public static VentaPlacaView instance = null;
    private boolean isRequestAuthoritation = false;

    Runnable handler = null;
    Runnable runAppRumbo = null;
    boolean ejecutarAccionDeCerrado = true;

    String pn = "";
    private String lastPanelBeforeConfirm = "";
    static boolean APP_RUMBO = false;
    boolean AD_BLUE = false;
    boolean autorizacionPlacaValida = false;
    boolean validacionManguerasSeleccion = false;
    static String panelActualFlujoApp = "";
    ArrayList<String> placas = new ArrayList<>();
    private static VentaPlacaView instance_1;
    boolean isSelectorIdentifiersPanel = false;
    SetupDao sdao = new SetupDao();
    SurtidorDao surtidorDao = new SurtidorDao();
    EquipoDao edao = new EquipoDao();
    RumboDao rdao = new RumboDao();
    boolean familiaAutorizacionApp = true;
    JsonObject responseAppRumbo = null;

    long idEmpresa;
    long idEquipo;

    String hostServer = "";
    JsonObject infoAutorizacionRumbo = new JsonObject();

    private String productoDescripcion = "";
    private String mensajeConfirmacionAdBlue = "";
    private JsonObject infoRequest = new JsonObject();
    private JsonObject responseAdBlue = new JsonObject();

    
    public void clear() {
        index = 0;
        selectedProductFamilyId = 0;
        selectedProductPrice = 0;
        aditionalDataIsRequerid = false;
        aditionalDataTimeout = 0;
        saleAuthorizationTimeout = 0;
        pumpUnlockingTimeout = 0;
        authorizationIdentifier = null;
        lastHoseSelected = null;
        stopTimeoutAditionalData = true;
        stopTimeoutSale = true;
        saleAuthorized = false;
    }

    boolean saleAuthorized = false;

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public String getAuthorizationIdentifier() {
        return authorizationIdentifier;
    }

    public void setAuthorizationIdentifier(String authorizationIdentifier) {
        this.authorizationIdentifier = authorizationIdentifier;
    }

    public long getAditionalDataTimeout() {
        return aditionalDataTimeout;
    }

    public void setAditionalDataTimeout(long aditionalDataTimeout) {
        this.aditionalDataTimeout = aditionalDataTimeout;
    }

    public long getSaleAuthorizationTimeout() {
        return saleAuthorizationTimeout;
    }

    public void setSaleAuthorizationTimeout(long saleAuthorizationTimeout) {
        this.saleAuthorizationTimeout = saleAuthorizationTimeout;
    }

    public long getPumpUnlockingTimeout() {
        return pumpUnlockingTimeout;
    }

    public void setPumpUnlockingTimeout(long pumpUnlockingTimeout) {
        this.pumpUnlockingTimeout = pumpUnlockingTimeout;
    }

    public boolean isSaleAuthorized() {
        return saleAuthorized;
    }

    public void setIsSaleAuthorized(boolean isSaleAuthorized) {
        this.saleAuthorized = isSaleAuthorized;
    }

    String identifierSerial = null;
    TreeMap<Long, Surtidor> informacionMangueras = new TreeMap<>();
    TreeMap<Integer, Integer> optionPumpsCombo = new TreeMap<>();
    TreeMap<Integer, Integer> optionFacesCombo = new TreeMap<>();
    TreeMap<Integer, Integer> optionHosesCombo = new TreeMap<>();

    public TreeMap<Long, Surtidor> getInformacionMangueras() {
        return informacionMangueras;
    }

    public void setInformacionMangueras(TreeMap<Long, Surtidor> informacionMangueras) {
        this.informacionMangueras = informacionMangueras;
    }

    public String getIdentifierSerial() {
        return identifierSerial;
    }

    public void setIdentifierSerial(String identifierSerial) {
        this.identifierSerial = identifierSerial;
    }

    public float getSelectedProductPrice() {
        return selectedProductPrice;
    }

    public void setSelectedProductPrice(float selectedProductPrice) {
        this.selectedProductPrice = selectedProductPrice;
    }

    public long getSelectedProductFamilyId() {
        return selectedProductFamilyId;
    }

    public void setSelectedProductFamilyId(long selectedProductFamilyId) {
        this.selectedProductFamilyId = selectedProductFamilyId;
    }

    public int getSelectedPumpNumber() {
        return selectedPumpNumber;
    }

    public void setSelectedPumpNumber(int selectedPumpNumber) {
        this.selectedPumpNumber = selectedPumpNumber;
    }

    public int getSelectedFaceNumber() {
        return selectedFaceNumber;
    }

    public void setSelectedFaceNumber(int selectedFaceNumber) {
        this.selectedFaceNumber = selectedFaceNumber;
    }

    public int getSelectedHoseNumber() {
        return selectedHoseNumber;
    }

    public void setSelectedHoseNumber(int selectedHoseNumber) {
        this.selectedHoseNumber = selectedHoseNumber;
    }

    int selectedPumpNumber = 0;
    int selectedFaceNumber = 0;
    int selectedHoseNumber = 0;
    long selectedProductId = 0;
    long selectedGrade = -1;

    public long getSelectedGrade() {
        return selectedGrade;
    }

    public void setSelectedGrade(long selectedGrade) {
        this.selectedGrade = selectedGrade;
    }

    public long getSelectedProductId() {
        return selectedProductId;
    }

    public void setSelectedProductId(long selectedProductId) {
        this.selectedProductId = selectedProductId;
    }

    public MedioIdentificacionBean getIdentifierMeanSelected() {
        return this.identifierMeanSelected;
    }

    public void setIdentifierMeanSelected(MedioIdentificacionBean identifierMeanId) {
        this.identifierMeanSelected = identifierMeanId;
    }

    MedioIdentificacionBean identifierMeanSelected = null;
    String odometer = null;
    String cardPass = null;

    public VentaPlacaView(java.awt.Frame mainFrame, boolean modal) {
        super(mainFrame, modal);
        this.controller = controller;
        AD_BLUE = false;
        this.TIPO_INFORMACION_ADICIONAL_CARD = 2;
        this.TIPO_INFORMACION_ADICIONAL_INFO = 3;
        this.TIPO_INFORMACION_ADICIONAL_PLACA = 1;
        initComponents();
        this.mainFrame = (InfoViewController) mainFrame;
        btn_back.setVisible(false);
        VentaPlacaView.instance_1 = this;
        init();
               
    }

    public static VentaPlacaView getInstance() {
        return VentaPlacaView.instance_1;
    }
    
    
    public static VentaPlacaView getInstance(InfoViewController vistaPrincipal, boolean modal) {
        if (VentaPlacaView.instance == null) {
            VentaPlacaView.instance = new VentaPlacaView(vistaPrincipal, modal);
        }
        return VentaPlacaView.instance;
    }

    void cargarMediosIdentificadores() {
        if (mediosIdentificadores != null) {
            mediosIdentificadores.clear();
            mediosIdentificadores = null;
            mediosIdentificadores = new TreeMap<>();
        } else {
            mediosIdentificadores = new TreeMap<>();
        }

        MedioIdentificacionBean rfid = new MedioIdentificacionBean();
        rfid.setId(RFID_IDENTIFIER_ID);
        rfid.setDescripcion("RFID");
        rfid.setNecesarioLector(true);
        rfid.setUrlImagen("RFID.png");
        rfid.setUrlImagen_2x("RFID_2x.png");

        MedioIdentificacionBean ibutton = new MedioIdentificacionBean();
        ibutton.setId(IBUTTON_IDENTIFIER_ID);
        ibutton.setDescripcion("Ibutton");
        ibutton.setNecesarioLector(true);
        ibutton.setUrlImagen("Ibutton.png");
        ibutton.setUrlImagen_2x("Ibutton_2x.png");

        MedioIdentificacionBean card = new MedioIdentificacionBean();
        card.setId(CARD_IDENTIFIER_ID);
        card.setDescripcion("Tarjeta");
        card.setNecesarioLector(false);
        card.setUrlImagen("CARD_R.png");

        MedioIdentificacionBean numericCode = new MedioIdentificacionBean();
        numericCode.setId(NUMERIC_CODE_IDENTIFIER_ID);
        numericCode.setDescripcion("<html><center>Código <br/>Númerico</center></html>");
        numericCode.setNecesarioLector(false);
        numericCode.setUrlImagen("Number.png");

        MedioIdentificacionBean app_rumbo = new MedioIdentificacionBean();
        app_rumbo.setId(APP_RUMBO_ID);
        app_rumbo.setDescripcion("<html><center>App<br/>Rumbo</center></html>");
        app_rumbo.setNecesarioLector(false);
        app_rumbo.setUrlImagen("APP.png");

        if (edao.consultaParametroIntegracion(NovusConstante.PARAMETRO_INTEGRACION_APP_RUMBO)) {
            mediosIdentificadores.put(1, app_rumbo);
        }
        mediosIdentificadores.put(2, ibutton);
        mediosIdentificadores.put(3, rfid);
        mediosIdentificadores.put(4, card);
        mediosIdentificadores.put(5, numericCode);
    }

    

    boolean isAdBlue() {
        return this.getProductoDescripcion()
                .equalsIgnoreCase(NovusConstante.PRODUCTO_UREA);
    }

    void cargaInformacionMangueras() {
        setInformacionMangueras(sdao.getMangueras());
    }

    void cargarInformacionManguerasAppRumbo(int familia) {
        setInformacionMangueras(sdao.getManguerasAppRumbo(familia));
    }

    void loadData() {
        this.cargarMediosIdentificadores();
        this.cargaInformacionMangueras();
    }

    void mensajeConfirmacionAdBlue() {
        mensajeConfirmacionAdBlue = "<html>Recuerde cerrar la venta en la opción <b>Ventas sin resolver</b></html>";
    }

    void renderDurationTimeoutString(long timeout) {
        this.lbl_progress_duration.setText(
                ("<html>EL TIEMPO VENCERÁ... EN <br/><center>" + formatDurationTimeout(timeout) + "</center></html>")
                        .toUpperCase());
    }

    void changeProgressBarValue(int width) {
        int height = this.lbl_progress_percentage.getHeight();
        int x = this.lbl_progress_percentage.getX();
        int y = this.lbl_progress_percentage.getY();
        this.lbl_progress_percentage.setBounds(x, y, width, height);
    }

    void resetProgressBar() {
        this.changeProgressBarValue(0);
        this.renderDurationTimeoutString(0);
    }

    void loadIdentificadorPOS() {
        hostServer = edao.getHostServer();
        idEquipo = Main.credencial.getEquipos_id();
        idEmpresa = Main.credencial.getEmpresas_id();
    }

    

    void setIndex(int index) {
        this.index = index;
    }

    String getPanelName(int index) {
        Component[] childrenPanels = this.pnl_layout_container.getComponents();
        if (index >= 0 && index < childrenPanels.length) {
            JPanel childPanel = (JPanel) childrenPanels[index];
            return childPanel.getName().trim();
        }
        return "";
    }

    

 

    boolean isAValidInteger(String numberString) {
        try {
            Integer.parseInt(numberString);
            return true;

        } catch (NumberFormatException e) {
            log("Error al parsear 'cara' desde la petición del identificador.", e);
            return false;
        }
    }

    
    boolean validationIdentifiersSelector() {
        return this.getIdentifierMeanSelected() != null;
    }
  
    

    void renderValueInput(JTextField input, String value) {
        input.setText(value);
    }

    
    int getHosesCount() {
        return this.getInformacionMangueras().size();
    }

    boolean isManguerasOcupadas() {
        return sdao.getManguerasOcupadas();
    }

    
    
    
    
    float litrosAutorizados(float montoMaximo) {
        float litrosAutorizados = 0f;
        litrosAutorizados = montoMaximo / obtenerPrecioUreaUseCase.execute();
        litrosAutorizados = Math.round(litrosAutorizados * 1000f) / 1000f;
        NovusUtils.printLn("***::->LitrosAutorizados: " + litrosAutorizados);
        return litrosAutorizados;
    }

    boolean requiereInformacionAdicional;
    boolean requierePlacaVehiculo;
    boolean requiereCodigoSeguridad;

   
    void renderTimeoutPanels(JPanel containerPanel, boolean inSale, int position) {
        JPanel progressPanel = this.pnl_timeout_progress;
        containerPanel.add(progressPanel, 0);
        if (inSale) {
            progressPanel.setBounds(400, 390, 535, 195);
        } else {
            switch (position) {
                case 0:
                    progressPanel.setBounds(90, 290, 535, 195);
                    break;
                case 1:
                    progressPanel.setBounds(708, 18, 535, 195);
                    break;
                default:
                    progressPanel.setBounds(90, 290, 535, 195);
            }
        }
        progressPanel.setVisible(true);
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    String formatDurationTimeout(long milliseconds) {
        String durationString;
        float durationInHours = (float) milliseconds / 3300000;
        if (durationInHours < 1) {
            if (milliseconds < 30000) {
                durationString = ((int) truncNumber(durationInHours * 3600)) + " seg";
            } else {
                float minutes = durationInHours * 60;
                float seconds = Math.round((minutes - truncNumber(minutes)) * 60);
                durationString = ((int) Math.floor(minutes)) + "min(s) y " + ((int) seconds) + "seg(s)";
            }
        } else {
            long minutes = Math.round((durationInHours - truncNumber(durationInHours)) * 60);
            durationString = ((int) truncNumber(durationInHours)) + " hora(s) "
                    + (minutes > 0 ? ("y " + ((int) minutes) + " min(s)") : "");
        }
        return durationString;
    }

    double truncNumber(double value) {
        return value - value % 1;
    }

    

    void changeTimeoutProgress(long initialTimeoutMS, long finalTimeoutMS) {
        int progressWidth = this.lbl_progress_back.getWidth();
        int percentage = Math.round(((float) initialTimeoutMS / (float) finalTimeoutMS) * 100);
        float totalProgress = (float) progressWidth / 100;
        this.changeProgressBarValue(Math.round(totalProgress * percentage));
        this.renderDurationTimeoutString(finalTimeoutMS - initialTimeoutMS);
    }

    public void setCardPass(String cardPass) {
        this.cardPass = cardPass;
    }

    void loadIdentifiersSelectorPanel() {
        this.setIdentifierMeanSelected(null);
        //this.toggleSteppersButtons(this.btn_next, false);
        isSelectorIdentifiersPanel = true;
    }

   

    void mostrarPanel(String panel) {
        NovusUtils.printLn("[mostrarPanel]" + panel);
         if (!"pnl_confirmacion".equals(panel)) {
        pn = panel; // << clave para que jDenegar sepa a dónde volver
    }
        CardLayout cardPanelLayout = (CardLayout) this.pnl_layout_container.getLayout();
        cardPanelLayout.show(pnl_layout_container, panel);
    }

    
    int getContainerChildsCount() {
        return this.pnl_layout_container.getComponentCount();
    }

    void taskRunner(Runnable handler) {
        Thread task = new Thread() {
            @Override
            public void run() {
                if (handler != null) {
                    handler.run();
                }
            }
        };
        task.start();
    }

    boolean hasNextChild() {
        return this.index < (this.getContainerChildsCount() - 1);
    }

    void toggleSteppersButtons(JLabel button, boolean visible) {
        button.setVisible(visible);
    }

    void init() {
        mostrarPanel(PNL_IDENTIFIERS_SELECTOR);
                
        this.obtenerCodigoExternoUreaUseCase = new ObtenerCodigoExternoUreaUseCase();
        this.obtenerIdBodegaUreaUseCase = new ObtenerIdBodegaUreaUseCase();
        this.loadData();
        this.loadIdentificadorPOS();
        
        
    }

 
    

    private JsonObject buildJsonLiberacion(JsonObject data) {
        try {
        JsonObject json = new JsonObject();
        json.addProperty("movimientoId", -1);
        json.addProperty("identificadorAprobacion", data.get("identificadorAprobacion").getAsString());
        json.addProperty("identificadorAutorizacionEDS", data.get("identificadorAutorizacionEDS").getAsString());
        return json;
        } catch (Exception e) {
            log("Error al construir la petición de liberación de autorización AdBlue (buildJsonLiberacion).", e);
            return new JsonObject();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tipoDocumento = new javax.swing.ButtonGroup();
        btn_group_identifier_type = new javax.swing.ButtonGroup();
        pnl_timeout_progress = new javax.swing.JPanel();
        lbl_progress_duration = new javax.swing.JLabel();
        lbl_progress_percentage = new javax.swing.JLabel();
        lbl_progress_back = new javax.swing.JLabel();
        background_timeout = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btn_back = new javax.swing.JLabel();
        pnl_layout_container = new javax.swing.JPanel();
        pnl_confirmacion = new javax.swing.JPanel();
        jDenegar = new javax.swing.JLabel();
        jAceptar = new javax.swing.JLabel();
        jMensajeConfirmacion = new javax.swing.JLabel();
        pnl_identifiers_selector = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        title_view = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jbackground = new javax.swing.JLabel();

        pnl_timeout_progress.setName("pnl_timeout_progress"); // NOI18N
        pnl_timeout_progress.setOpaque(false);
        pnl_timeout_progress.setPreferredSize(new java.awt.Dimension(520, 180));
        pnl_timeout_progress.setLayout(null);

        lbl_progress_duration.setBackground(new java.awt.Color(51, 51, 51));
        lbl_progress_duration.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_progress_duration.setForeground(new java.awt.Color(51, 51, 51));
        lbl_progress_duration.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_progress_duration.setText("<html>EL TIEMPO VENCERA EN <br/><center>15 SEGUNDOS</center></html>");
        pnl_timeout_progress.add(lbl_progress_duration);
        lbl_progress_duration.setBounds(20, 10, 350, 110);

        lbl_progress_percentage.setBackground(new java.awt.Color(186, 12, 47));
        lbl_progress_percentage.setOpaque(true);
        pnl_timeout_progress.add(lbl_progress_percentage);
        lbl_progress_percentage.setBounds(20, 120, 0, 40);

        lbl_progress_back.setBackground(new java.awt.Color(239, 239, 239));
        lbl_progress_back.setOpaque(true);
        pnl_timeout_progress.add(lbl_progress_back);
        lbl_progress_back.setBounds(20, 120, 340, 40);

        background_timeout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/cardEsperando.png"))); // NOI18N
        pnl_timeout_progress.add(background_timeout);
        background_timeout.setBounds(0, 0, 535, 195);

        pnl_timeout_progress.getAccessibleContext().setAccessibleName("pnl_timeout_progress");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(100, 0, 10, 80);

        btn_back.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_back.setForeground(new java.awt.Color(223, 26, 9));
        btn_back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_white.png"))); // NOI18N
        btn_back.setText("Anterior");
        btn_back.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_backMousePressed(evt);
            }
        });
        getContentPane().add(btn_back);
        btn_back.setBounds(800, 720, 140, 60);

        pnl_layout_container.setOpaque(false);
        pnl_layout_container.setLayout(new java.awt.CardLayout());

        pnl_confirmacion.setOpaque(false);
        pnl_confirmacion.setLayout(null);

        jDenegar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jDenegar.setForeground(new java.awt.Color(223, 26, 9));
        jDenegar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jDenegar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/bt-danger-white.png"))); // NOI18N
        jDenegar.setText("No");
        jDenegar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDenegar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jDenegar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jDenegarMouseClicked(evt);
            }
        });
        pnl_confirmacion.add(jDenegar);
        jDenegar.setBounds(320, 350, 264, 54);

        jAceptar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jAceptar.setForeground(new java.awt.Color(223, 26, 9));
        jAceptar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/bt-danger-white.png"))); // NOI18N
        jAceptar.setText("Si");
        jAceptar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAceptar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAceptar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jAceptarMouseClicked(evt);
            }
        });
        pnl_confirmacion.add(jAceptar);
        jAceptar.setBounds(730, 350, 264, 54);

        jMensajeConfirmacion.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensajeConfirmacion.setForeground(new java.awt.Color(204, 0, 0));
        jMensajeConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensajeConfirmacion.setText("¿Confirmacion de mensaje?");
        pnl_confirmacion.add(jMensajeConfirmacion);
        jMensajeConfirmacion.setBounds(0, 190, 1280, 100);

        pnl_layout_container.add(pnl_confirmacion, "pnl_confirmacion");

        pnl_identifiers_selector.setName("pnl_identifiers_selector"); // NOI18N
        pnl_identifiers_selector.setOpaque(false);
        pnl_identifiers_selector.setLayout(null);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_clientes_propios.png"))); // NOI18N
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel18MouseReleased(evt);
            }
        });
        pnl_identifiers_selector.add(jLabel18);
        jLabel18.setBounds(290, 50, 270, 500);

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_glp.png"))); // NOI18N
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel19MouseReleased(evt);
            }
        });
        pnl_identifiers_selector.add(jLabel19);
        jLabel19.setBounds(720, 50, 270, 500);

        pnl_layout_container.add(pnl_identifiers_selector, "pnl_identifiers_selector");
        pnl_identifiers_selector.getAccessibleContext().setAccessibleName("pnl_identifiers_selector");

        getContentPane().add(pnl_layout_container);
        pnl_layout_container.setBounds(0, 95, 1280, 600);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 60);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel25);
        jLabel25.setBounds(1115, 710, 10, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(logo);
        logo.setBounds(10, 700, 110, 100);

        title_view.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        title_view.setForeground(new java.awt.Color(255, 255, 255));
        title_view.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        title_view.setText("GESTION DE CLIENTES");
        title_view.setToolTipText("");
        getContentPane().add(title_view);
        title_view.setBounds(140, 10, 820, 60);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion);
        jNotificacion.setBounds(130, 720, 570, 60);

        jbackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jbackground.setText("e.");
        getContentPane().add(jbackground);
        jbackground.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jDenegarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDenegarMouseClicked
    NovusUtils.beep();
    // Vuelve a donde estabas antes de abrir la confirmación
    if (lastPanelBeforeConfirm != null && !lastPanelBeforeConfirm.isEmpty()) {
        mostrarPanel(lastPanelBeforeConfirm);
    } else if (pn != null && !pn.isEmpty()) {
        mostrarPanel(pn);
    } else if (pnlPrincipal != null) {
        NovusUtils.showPanel(pnlPrincipal, "home");
    }
    btn_back.setVisible(false);
   
    }//GEN-LAST:event_jDenegarMouseClicked
    
   
    private void btn_backMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_backMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_backMousePressed
    
    private void jAceptarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jAceptarMouseClicked
       pnl_confirmacion.setVisible(false);
       pnl_identifiers_selector.setVisible(true);
        handler.run();
        handler = null;
       
        
    }//GEN-LAST:event_jAceptarMouseClicked

    private void jLabel18MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseReleased
        // TODO add your handling code here:
        abrirMenuPreautorizar();
    }//GEN-LAST:event_jLabel18MouseReleased

    private void jLabel19MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseReleased
        abrirMenuVentaGlp(); 
    }//GEN-LAST:event_jLabel19MouseReleased

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
        // TODO add your handling code here:
        abrirMenuPreautorizar();
    }//GEN-LAST:event_jLabel18MouseClicked

    private void loadWithoutPumElement() {
        jLabel18.setVisible(false);
    }


    
    //este esta ok estefa 
    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        jMensajeConfirmacion.setText("<html><center>¿DESEAS SALIR DE GESTION CLIENTE?</center></html>");
        btn_back.setVisible(false);
        //btn_next.setVisible(false);
        lastPanelBeforeConfirm = pn; 
        this.mostrarPanel("pnl_confirmacion");
        this.handler = () -> this.close(true);
        
    }
          
    //este esta ok estefa 
    void close(boolean liberar) {
        this.dispose();
        RumboView.instance = null;
        InfoViewController.NotificadorRumbo = null;
        
    }

    void handleKeyPressSerialFields(String value, KeyEvent evt) {
        this.setIdentifierSerial(value);
    }

   

  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background_timeout;
    private javax.swing.JLabel btn_back;
    private javax.swing.ButtonGroup btn_group_identifier_type;
    private javax.swing.JLabel jAceptar;
    private javax.swing.JLabel jDenegar;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jMensajeConfirmacion;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jbackground;
    private javax.swing.JLabel lbl_progress_back;
    private javax.swing.JLabel lbl_progress_duration;
    private javax.swing.JLabel lbl_progress_percentage;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel pnl_confirmacion;
    private javax.swing.JPanel pnl_identifiers_selector;
    private javax.swing.JPanel pnl_layout_container;
    private javax.swing.JPanel pnl_timeout_progress;
    private javax.swing.ButtonGroup tipoDocumento;
    private javax.swing.JLabel title_view;
    // End of variables declaration//GEN-END:variables

      



  



    public void habilitarBotonesBackNext(boolean habilitar) {
        btn_back.setVisible(habilitar);
     
    }



    

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                log("Interrupted.setTimeout", e);
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public String getProductoDescripcion() {
        return productoDescripcion;
    }

    public void setProductoDescripcion(String productoDescripcion) {
        this.productoDescripcion = productoDescripcion;
    }

   

    private void log(String message, Throwable t) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
        String logMessage = "[" + timestamp + "] [RumboView] " + message + " | EXCEPTION: ";
        java.io.StringWriter sw = new java.io.StringWriter();
        t.printStackTrace(new java.io.PrintWriter(sw));
        String exceptionAsString = sw.toString();
        NovusUtils.WriteLogAppend("rumbo.log", logMessage + exceptionAsString + "\n");
    }

    void abrirMenuPreautorizar() {
        NovusUtils.beep();
//        if (Main.persona == null) {
//            mostrarPanelSinTurnos();
//        } else {
//            this.recargarPersona();
//            if (InfoViewController.turnosPersonas.size() > 1) {
//                PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(controller,
//                        NovusConstante.SELECCION_PROMOTOR_AUTORIZACION_VENTA);
//                seleccionpromotor.setVisible(true);
//            } else {
                SeleccionTipoAutorizacionView seleccionView = new SeleccionTipoAutorizacionView(controller, true);
                seleccionView.setVisible(true);
//            }
//        }


        //mostrarSubPanel(new PanelMediosPago());
    }
    
     void abrirMenuVentaGlp() {
        NovusUtils.beep();
//        if (Main.persona == null) {
//            mostrarPanelSinTurnos();
//        } else {
//            this.recargarPersona();
//            if (InfoViewController.turnosPersonas.size() > 1) {
//                PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(controller,
//                        NovusConstante.SELECCION_PROMOTOR_AUTORIZACION_VENTA);
//                seleccionpromotor.setVisible(true);
//            } else {
                VentaPlacaGLP instanciaPlacaGLP = VentaPlacaGLP.getInstance(controller, true);
                instanciaPlacaGLP.setVisible(true);
//            }
//        }


        //mostrarSubPanel(new PanelMediosPago());
    }
    
    
    
      
    public void cambiarPanelHome() {
        NovusUtils.showPanel(pnlPrincipal, "home");
    }
    
      public void recargarPersona() {
        if (Main.CENTRALIZADOR) {
            this.consigueJornadaRemotas();
        }
    }
    public void mostrarPanelSinTurnos() {
        showMessage("NO HAY TURNOS ABIERTOS",
                InfoViewController.icono_sin_turnos,
                true, this::cambiarPanelHome,
                true, LetterCase.FIRST_UPPER_CASE);
    }
    
     public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }
     
     private void consigueJornadaRemotas() {
        obtenerJornadasV2();
        this.renderJournalData();
    }
    void renderJournalData() {
        System.out.println("🔍 Persona Turno Activo(s): " + Main.persona);
        if (Main.persona == null) {
           // this.jLabel6.setText("SIN TURNOS ACTIVOS");
        } else {
            String turnosNombres = "";
            int i = 0;
            for (PersonaBean p : turnosPersonas) {
                turnosNombres += p.getNombre().split(" ")[0];
                if (i < turnosPersonas.size() - 1) {
                    turnosNombres += " - ";
                }
                i++;
            }
         //   this.jLabel6.setText(turnosNombres);
        }
    }
     
    public void showMessage(String msj, ImageIcon ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setImageIcon(ruta)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }
    private void obtenerJornadasV2() {
        turnosPersonas.clear();
        try {
            List<PersonaBean> promotores = obtenerPromotoresJornadaUseCase.execute();
            turnosPersonas = new ArrayList<>(promotores);
            System.out.println("🔍 turnosPersonas: " + turnosPersonas);
            if (turnosPersonas.isEmpty()) {
                Main.persona = null;
            }
        } catch ( Exception e) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, e);
            Main.persona = null;
        }
    }
}

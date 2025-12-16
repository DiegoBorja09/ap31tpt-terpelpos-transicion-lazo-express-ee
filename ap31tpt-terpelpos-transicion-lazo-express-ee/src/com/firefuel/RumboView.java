/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.commons.domain.valueObject.LetterCase;
import com.application.useCases.movimientos.InsertCtMovimientosUseCase;
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

/**
 * @author usuario
 */
public class RumboView extends javax.swing.JDialog {
    ObtenerPrecioUreaUseCase obtenerPrecioUreaUseCase;
    ObtenerCodigoExternoUreaUseCase obtenerCodigoExternoUreaUseCase;
    ObtenerIdBodegaUreaUseCase obtenerIdBodegaUreaUseCase;

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

    ImageIcon iconInfoRed = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/info_producto_red.png"));
    ImageIcon iconInfoBlue = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/info_producto.png"));

    ImageIcon datosAdicionalesInactivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndDatosAdicionalesInactivo.png"));
    ImageIcon datosInfoInactivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndInformacionAdicionalInactivo.png"));

    public static RumboView instance = null;
    private boolean isRequestAuthoritation = false;

    Runnable handler = null;
    Runnable runAppRumbo = null;
    boolean ejecutarAccionDeCerrado = true;

    String pn = "";

    static boolean APP_RUMBO = false;
    boolean AD_BLUE = false;
    boolean autorizacionPlacaValida = false;
    boolean validacionManguerasSeleccion = false;
    static String panelActualFlujoApp = "";
    ArrayList<String> placas = new ArrayList<>();

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
        txt_odometer.setText("");
        txt_serial_card.setText("");
        txt_pass_card.setText("");
        txt_numeric_code_serial.setText("");
        txt_license_plate.setText("");
        txt_pin.setText("");
        txt_license_info1.setText("");
    }

    boolean saleAuthorized = false;

    public String getOdometer() {
        return this.txt_odometer.getText().trim();
    }

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

    public RumboView(java.awt.Frame mainFrame, boolean modal) {
        super(mainFrame, modal);
        AD_BLUE = false;
        this.TIPO_INFORMACION_ADICIONAL_CARD = 2;
        this.TIPO_INFORMACION_ADICIONAL_INFO = 3;
        this.TIPO_INFORMACION_ADICIONAL_PLACA = 1;
        initComponents();
        this.mainFrame = (InfoViewController) mainFrame;
        init();
        if (this.lastHoseSelected == null) {
            btn_next.setVisible(false);
        }
    }

    public static RumboView getInstance(InfoViewController vistaPrincipal, boolean modal) {
        if (RumboView.instance == null) {
            RumboView.instance = new RumboView(vistaPrincipal, modal);
        }
        return RumboView.instance;
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

    public void handleIdentifierRequest(JsonObject identifierRequest) {
        if (!isIsRequestAuthoritation() && !isSaleAuthorized()) {
            String identifierDescription = identifierRequest.get("medio").getAsString();
            int identifierIdRequested = identifierDescription.equals("ibutton") ? IBUTTON_IDENTIFIER_ID
                    : RFID_IDENTIFIER_ID;
            String identifierSerial1 = identifierRequest.get("serial").getAsString();
            int cara = 0;
            try {
                cara = Integer.parseInt(identifierRequest.get("cara") != null && !identifierRequest.get("cara").isJsonNull() ? identifierRequest.get("cara").getAsString() : "0");
            } catch (NumberFormatException e) {
                log("Error al parsear 'cara' desde la petición del identificador.", e);
                NovusUtils.printLn("Error: " + e.getMessage());
            }
            int selectedIdentifierId = this.getIdentifierMeanSelected().getId();
            if (identifierIdRequested == selectedIdentifierId) {

                if (cara == this.getSelectedFaceNumber() || isAdBlue()) {

                    setIsRequestAuthoritation(true);
                    this.setIdentifierSerial(identifierSerial1);
                    if (!isSaleAuthorized()) {
                        this.solicitarAutorizacion();
                    }
                } else {
                    int numerooCara = this.getSelectedFaceNumber();
                    clear();
                    init();
                    clearHoses();
                    notificacion("ERROR : LECTURA ERRADA SE ESPERA CARA ".concat(numerooCara + ""), "/com/firefuel/resources/btBad.png", true, 3000, false);
                    isSelectorIdentifiersPanel = true;
                    CERRAR = PNL_IDENTIFIERS_SELECTOR;
                    habilitarBotonesBackNext(false);
                }
            }
        }
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

    void loadView() {
        jTextField1.setVisible(NovusUtils.productionServer());
        this.resetProgressBar();
        this.handleStepper(0, true);
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

    boolean isValidDataStep(int index) {
        switch (this.getPanelName(index)) {
            case PNL_HOSES_SELECTOR:
                return this.validationHosesSelectorPanel();
            case PNL_CAR_DATA:
                if (!validarCampos("[0-9]{1,10}", txt_odometer, 9, jNotificacion)) {
                    return this.validationCarDataPanel();
                }
            case PNL_IDENTIFIERS_SELECTOR:
                return this.validationIdentifiersSelector();
            case PNL_IDENTIFIER_LISTEN:
                return this.validationIdentifierListen();
            case PNL_ADITIONAL_DATA:
                return this.validationAditionalData();
        }
        return true;
    }

    boolean validationHosesSelectorPanel() {
        if (this.lastHoseSelected == null) {
            notificacion("FAVOR SELECCIONE UNA MANGUERA", "/com/firefuel/resources/btBad.png", true, 2000, false);
            CERRAR = PNL_HOSES_SELECTOR;
            return false;
        }
        return true;
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

    boolean validateOdometer() {
        if (this.getOdometer() == null || this.getOdometer().trim().equals("")) {
            notificacion("DEBE INGRESAR ALGUN VALOR EN ODÓMETRO", "/com/firefuel/resources/btBad.png", true, 4000, false);
            CERRAR = PNL_CAR_DATA;
            return false;
        } else {
            if (!this.isAValidInteger(this.getOdometer().trim())) {
                notificacion("ODÓMETRO DEBE SER UN ENTERO VALIDO", "/com/firefuel/resources/btBad.png", true, 4000, false);
                CERRAR = PNL_CAR_DATA;
                return false;
            } else {
                final long ODOMETER_MAX_THRESHOLD = 9999999999L;
                long odom = Long.parseLong(this.getOdometer().trim());
                if (odom > ODOMETER_MAX_THRESHOLD) {
                    notificacion("ODÓMETRO DEBE SER MENOR A 9999999999", "/com/firefuel/resources/btBad.png", true, 4000, false);
                    CERRAR = PNL_CAR_DATA;
                    return false;
                }
            }

        }

        return true;
    }

    boolean validationCarDataPanel() {
        return this.validateOdometer();
    }

    boolean validationIdentifiersSelector() {
        return this.getIdentifierMeanSelected() != null;
    }

    boolean validationCardIdentifier() {
        if (this.getIdentifierSerial().trim().length() <= 4) {
            notificacion("SERIAL DE LA TARJETA DEBE TENER MAS DE 4 CARACTERES", "/com/firefuel/resources/btBad.png", true, 3000, false);
            this.txt_serial_card.requestFocus();
            CERRAR = PNL_IDENTIFIER_LISTEN;
            return false;
        } else {
            if (this.getCardPass() == null || this.getCardPass().trim().equals("")) {
                notificacion("DEBE INGRESAR CLAVE TARJETA", "/com/firefuel/resources/btBad.png", true, 3000, false);
                this.txt_pass_card.requestFocus();
                CERRAR = PNL_IDENTIFIER_LISTEN;
                return false;
            }
        }
        return true;
    }

    boolean validationNumericCodeIdentifier() {
        if (this.getIdentifierSerial().trim().length() <= 4) {
            notificacion("CÓDIGO DEBE TENER MAS DE 4 CARACTERES", "/com/firefuel/resources/btBad.png", true, 3000, false);
            CERRAR = PNL_IDENTIFIER_LISTEN;
            return false;
        }
        return true;
    }

    boolean validationAditionalDataCard() {
        if (requiereCodigoSeguridad) {
            String pin = new String(txt_pin.getPassword());
            if (pin.isEmpty() || pin.trim().length() == 0) {
                notificacion("PIN DEBE SER INGRESADO", "/com/firefuel/resources/btBad.png", true, 3000, false);
                CERRAR = PNL_ADITIONAL_DATA;
                return false;
            } else {
                if (!this.isAValidInteger(pin.trim())) {
                    notificacion("PIN SOLO DEBER TENER NUMEROS ENTEROS", "/com/firefuel/resources/btBad.png", true, 3000, false);
                    CERRAR = PNL_ADITIONAL_DATA;
                    return false;
                }
            }
        }
        return true;
    }

    boolean validationAditionalDataLicensePlate() {
        if (requierePlacaVehiculo) {
            String licensePlate = txt_license_plate.getText();
            if (licensePlate == null || licensePlate.trim().length() == 0) {
                notificacion("LA INFORMACIÓN ES REQUERIDA", "/com/firefuel/resources/btBad.png", true, 3000, false);
                CERRAR = PNL_ADITIONAL_DATA;
                return false;
            } else {
                if (licensePlate.trim().length() <= 3) {
                    notificacion("SE DEBE TENER MAS DE 3 CARACTERES", "/com/firefuel/resources/btBad.png", true, 3000, false);
                    CERRAR = PNL_ADITIONAL_DATA;
                    return false;
                } else if (licensePlate.trim().length() > 17) {
                    notificacion("SE DEBE TENER MENOS DE 18 CARACTERES", "/com/firefuel/resources/btBad.png", true, 3000, false);
                    CERRAR = PNL_ADITIONAL_DATA;
                    return false;
                }
            }
        }
        return true;
    }

    boolean validationAditionalData() {
        if (this.getIdentifierMeanSelected() != null) {

            switch (this.getIdentifierMeanSelected().getId()) {
                case RumboView.CARD_IDENTIFIER_ID:
                    if (!this.validationAditionalDataCard()) {
                        return false;
                    }
                    break;
                default:
                    if (!this.validationAditionalDataLicensePlate()) {
                        return false;
                    }
            }
            if (this.getAuthorizationIdentifier() == null) {
                notificacion("NO SE TIENE EL IDENTIFICADOR DE AUTORIZACIÓN", "/com/firefuel/resources/btBad.png", true, 3000, false);
                CERRAR = PNL_ADITIONAL_DATA;
                return false;
            }
            if (requiereInformacionAdicional
                    && (txt_license_info1.getText().isEmpty() || txt_license_info1.getText().length() == 0)) {
                notificacion("SE REQUIERE INFORMACIÓN ADICIONAL", "/com/firefuel/resources/btBad.png", true, 3000, false);
                CERRAR = PNL_ADITIONAL_DATA;
                return false;
            }
            this.fetchAditionalData();
        }

        return false;
    }

    JsonObject buildFetchAditionalDataRequest() {
        try {
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();

        if (!txt_license_plate.getText().equals("")) {
            JsonObject request = new JsonObject();
            request.addProperty("tipo", "placa");
            request.addProperty("valor", txt_license_plate.getText());
            arr.add(request);
        }

        String pinText = new String(txt_pin.getPassword());
        if (!pinText.equals("")) {
            JsonObject request = new JsonObject();
            request.addProperty("tipo", "pin");
            request.addProperty("valor", pinText);
            arr.add(request);
        }

        if (!txt_license_info1.getText().equals("")) {
            JsonObject request = new JsonObject();
            request.addProperty("tipo", "adicional");
            request.addProperty("valor", txt_license_info1.getText());
            arr.add(request);
        }

        json.addProperty("identificadorAutorizacionEDS",
                this.getAuthorizationIdentifier() != null ? this.getAuthorizationIdentifier().trim() : "");
        json.add("validar", arr);
        return json;
        } catch (Exception e) {
            log("Error al construir la petición de datos adicionales (buildFetchAditionalDataRequest).", e);
            return new JsonObject();
        }
    }

    void handleAditionalDataResponse(JsonObject response) {

        if (response != null) {
            NovusUtils.printLn("LA RESPUESTA ES DIFERENTE DE NULL >>> VALIDAR: " + response);
            if (response.get("error") == null) {
                this.setIsSaleAuthorized(true);
                nextStep(false);
            } else {
                notificacion("DATO ADICIONAL INVALIDO", "/com/firefuel/resources/btBad.png", true, 3000, false);
                CERRAR = PNL_ADITIONAL_DATA;
            }
        } else {
            notificacion("OCURRIO UN ERROR EN EL ENVIO DE DATOS", "/com/firefuel/resources/btBad.png", true, 3000, false);
            CERRAR = PNL_ADITIONAL_DATA;
        }
    }

    void fetchAditionalData() {
        cargar("ENVIANDO DATOS ADICIONALES", "/com/firefuel/resources/loader_fac.gif");
        setTimeout(1, () -> {
            JsonObject response = RumboFacade.fetchAditionalData(this.buildFetchAditionalDataRequest());
            cerrarMensaje();
            handleAditionalDataResponse(response);
        });
    }

    boolean validationIdentifierListen() {
        if (this.getIdentifierSerial() == null) {
            notificacion("SERIAL NO SE HA PROPORCIONADO", "/com/firefuel/resources/btBad.png", true, 3000, false);
            CERRAR = PNL_IDENTIFIER_LISTEN;
            return false;
        }
        if (this.getIdentifierMeanSelected() != null) {
            switch (this.getIdentifierMeanSelected().getId()) {
                case RumboView.CARD_IDENTIFIER_ID:
                    if (!this.validationCardIdentifier()) {
                        return false;
                    }
                    break;
                case RumboView.NUMERIC_CODE_IDENTIFIER_ID:
                    if (!this.validationNumericCodeIdentifier()) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
            if (!isSaleAuthorized()) {
                this.solicitarAutorizacion();
            }
        }
        return this.isSaleAuthorized();
    }

    void renderValueInput(JTextField input, String value) {
        input.setText(value);
    }

    void renderProductInformation() {
        Surtidor hoseInformationSelected = this.getInformacionMangueras()
                .get(Long.parseLong(this.getSelectedHoseNumber() + ""));
        if (hoseInformationSelected != null) {
            this.lbl_product_name.setText("<html><font color=black>Producto: </font>" + "<font color=red>" + hoseInformationSelected.getProductoDescripcion() + "</font></html>");
            icon_info.setIcon(iconInfoRed);
        } else {
            resetInfoProducto();
        }
    }

    public void listenSale(long hoseInSale, String saleAuthorizationIdentifier) {
        NovusUtils.printLn("MANGUERA= " + hoseInSale + ", AUTORIZACION=" + saleAuthorizationIdentifier + ","
                + "VENTA_AUTORIZADA=" + this.isSaleAuthorized());
        NovusUtils.printLn("MANGUERA= " + this.getSelectedHoseNumber());
        NovusUtils.printLn(Main.ANSI_GREEN + "isSaleAuthorized= " + this.isSaleAuthorized() + ", ");
        NovusUtils.printLn(Main.ANSI_GREEN + "stopTimeoutSale= " + !this.stopTimeoutSale + ", ");
        NovusUtils.printLn(Main.ANSI_GREEN + "stopTimeoutAditionalData= " + this.stopTimeoutAditionalData + ", ");
        NovusUtils.printLn(Main.ANSI_GREEN + "saleAuthorizationIdentifier===" + saleAuthorizationIdentifier);
        NovusUtils.printLn(Main.ANSI_GREEN + "saleAuthorizationIdentifier===" + this.getAuthorizationIdentifier()
                + Main.ANSI_RESET);
        if ((hoseInSale == this.getSelectedHoseNumber())
                && (this.isSaleAuthorized())
                && (!this.stopTimeoutSale)
                && (this.stopTimeoutAditionalData)
                && (saleAuthorizationIdentifier.equals(this.getAuthorizationIdentifier()))) {
            NovusUtils.printLn(Main.ANSI_GREEN + "SE MANDA A DETENER EL TIMEOUT, PORQUE SE RECIBE LA VENTA");
            this.stopTimeoutSale = false;
            cargar("VENTA DETECTADA", "/com/firefuel/resources/loader_fac.gif", true, 2, true);
            ULTIMO_PANEL = "";
        }
    }

    int getHosesCount() {
        return this.getInformacionMangueras().size();
    }

    boolean isManguerasOcupadas() {
        return sdao.getManguerasOcupadas();
    }

    void loadHosesSelectorPanel() {
        this.pnl_hoses_carousel.removeAll();
        isSelectorIdentifiersPanel = false;
        this.setSelectedPumpNumber(0);
        this.lastHoseSelected = null;
        if (!this.getInformacionMangueras().isEmpty()) {
            if (this.getSelectedPumpNumber() == 0) {
                int j = 0, i = 0;
                final int ncols = RumboView.HOSES_TO_SHOW_BY_SLIDE;
                final int carouselContainerHeight = this.pnl_hoses_carousel.getHeight();
                final int carouselContainerWidth = this.pnl_hoses_carousel.getWidth();
                final int availableWidthForComponent = carouselContainerWidth / RumboView.HOSES_TO_SHOW_BY_SLIDE;
                final int componentDimension = 145;
                final int slideItemRemainderVerticalSpace = (carouselContainerHeight - componentDimension) / 2;
                final int slideItemRemainderHorizontalSpace = availableWidthForComponent - componentDimension;
                int index2 = 0;
                for (Map.Entry<Long, Surtidor> hoseInformation : this.getInformacionMangueras().entrySet()) {
                    ManguerasItem hoseItem = new ManguerasItem(this, hoseInformation.getValue());
                    this.pnl_hoses_carousel.add(hoseItem);
                    this.hosesItem.put(hoseInformation.getKey(), hoseItem);
                    hoseItem.setBounds(
                            (index2 * availableWidthForComponent) + ((slideItemRemainderHorizontalSpace / 2)) + 12,
                            ((slideItemRemainderVerticalSpace / 2) * j) + (componentDimension * j) + 20, componentDimension,
                            componentDimension);
                    hoseItem.setEnabled(!hoseInformation.getValue().isBloqueo());
                    hoseItem.setVisible(true);
                    index2++;
                    if (index2 == (ncols)) {
                        j++;
                        index2 = 0;
                    }
                    hoseItem.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseReleased(java.awt.event.MouseEvent evt) {
                            validarHoses(hoseItem);
                        }
                    });
                }
                clearHoses();
            }
        } else {
            if (!familiaAutorizacionApp) {
                jNotificacion.setVisible(true);
                jNotificacion.setText(" ERROR MANGUERAS ");
                setTimeout(2, () -> {
                    jNotificacion.setText("");
                });
            } else if (!isManguerasOcupadas()) {
                notificacion("NO SE ENCONTRO INFORMACIÓN SURTIDORES", "/com/firefuel/resources/btBad.png", true, 3000, false);
                CERRAR = PNL_HOSES_SELECTOR;
            } else {
                notificacion("MANGUERAS OCUPADAS", "/com/firefuel/resources/btBad.png", true, 3000, true);
                CERRAR = "";
            }
        }
    }

    public void validarHoses(ManguerasItem hoseItem) {
        if (hoseItem.getModel().getFamiliaDescripcion().equals("UREA")) {
            if (obtenerIdBodegaUreaUseCase.execute() == 0) {
                notificacion("NO SE ENCONTRÓ INFORMACIÓN DEL TANQUE", "/com/firefuel/resources/btBad.png", true, 3000, true);
                CERRAR = "";
            } else if (obtenerPrecioUreaUseCase.execute() == 0) {
                notificacion("NO SE ENCONTRÓ INFORMACIÓN DEL PRODUCTO", "/com/firefuel/resources/btBad.png", true, 3000, true);
                CERRAR = "";
            } else {
                habilitarBotonesSeleccionManguera(hoseItem);
            }
        } else {
            habilitarBotonesSeleccionManguera(hoseItem);
        }
    }

    void habilitarBotonesSeleccionManguera(ManguerasItem hoseItem) {
        if (!hoseItem.isIsSelected()) {
            btn_next.setVisible(true);
        } else {
            btn_next.setVisible(false);
        }
    }

    void listenIdentifiersClick(MedioIdentificacionBean identifierMeanSelected) {
        this.setIdentifierMeanSelected(identifierMeanSelected);
        if (identifierMeanSelected.getId() == 0) {
            loadInputsIdentifierPanel();
            mostrarPanel("pnl_identifier_listen");
        } else {
            this.nextStep(true);
        }
    }

    void clearHoses() {
        this.lastHoseSelected = null;
        for (Map.Entry<Long, ManguerasItem> entry : hosesItem.entrySet()) {
            ManguerasItem item = entry.getValue();
            item.isSelected = false;
            item.selectPanel(false);
        }
        resetInfoProducto();
    }

    void resetInfoProducto() {
        icon_info.setIcon(iconInfoBlue);
        this.lbl_product_name.setText("<html><font color=blue>Producto: </font>" + "<font color=blue>" + "Seleccione una manguera" + "</font></html>");
    }

    void listenHoseClick(Surtidor hoseInformation) {
        ManguerasItem selectedHosePanel = this.hosesItem.get(Long.parseLong(hoseInformation.getManguera() + ""));
        if (this.lastHoseSelected != null) {
            this.lastHoseSelected.isSelected = false;
            this.lastHoseSelected.selectPanel(false);
        }
        selectedHosePanel.isSelected = !(selectedHosePanel == this.lastHoseSelected);
        this.lastHoseSelected = (selectedHosePanel == this.lastHoseSelected) ? null : selectedHosePanel;
        selectedHosePanel.selectPanel(selectedHosePanel.isSelected);
        if (this.lastHoseSelected != null) {
            Surtidor hoseSelected = this.lastHoseSelected.getModel();
            this.setSelectedFaceNumber(hoseSelected.getCara());
            this.setSelectedHoseNumber(hoseSelected.getManguera());
            this.setSelectedPumpNumber(hoseSelected.getSurtidor());
            this.setSelectedProductId(hoseSelected.getProductoIdentificador());
            this.setSelectedProductFamilyId(hoseSelected.getFamiliaIdentificador());
            this.setSelectedProductPrice(hoseSelected.getProductoPrecio());
            this.setSelectedGrade(hoseSelected.getGrado());
            this.setProductoDescripcion(hoseSelected.getFamiliaDescripcion());
        } else {
            this.setSelectedFaceNumber(0);
            this.setSelectedHoseNumber(0);
            this.setSelectedPumpNumber(0);
            this.setSelectedProductId(0);
            this.setSelectedProductFamilyId(0);
            this.setSelectedProductPrice(0);
            this.setSelectedGrade(-1);
            this.setProductoDescripcion("");
        }
        this.renderProductInformation();
    }

    void loadCarDataPanel() {
        isSelectorIdentifiersPanel = false;
        this.txt_odometer.requestFocus();
        if (this.getOdometer() != null) {
            this.renderValueInput(this.txt_odometer, this.getOdometer());
        }
    }

    void renderIdentifierMeansItem() {
        this.pnl_identifiers.removeAll();
        if (RumboView.mediosIdentificadores != null) {
            int j = 0, i = 0;
            final int ncols = RumboView.IDENTIFIERS_TO_SHOW_BY_SLIDE;
            final int identifiersContainerHeight = this.pnl_identifiers.getHeight();
            final int identifiersContainerWidth = this.pnl_identifiers.getWidth();
            final int availableWidthForComponent = identifiersContainerWidth / ncols;
            final int componentDimension = 385;
            final int slideItemRemainderVerticalSpace = (identifiersContainerHeight - componentDimension) / 2;
            final int slideItemRemainderHorizontalSpace = availableWidthForComponent - componentDimension;
            int index2 = 0;
            for (Map.Entry<Integer, MedioIdentificacionBean> e : RumboView.mediosIdentificadores.entrySet()) {
                item_panel panel = new item_panel(this, e.getValue(), true);
                panel.setBounds(
                        (index2 * availableWidthForComponent) + ((slideItemRemainderHorizontalSpace + 310 / 2)),
                        120 + ((slideItemRemainderVerticalSpace / 2) * j) + (componentDimension * j), 190,
                        componentDimension);
                index2++;
                if (index2 == (ncols)) {
                    j++;
                    index2 = 0;
                }
                this.pnl_identifiers.add(panel);
            }
        }
    }

    JsonObject buildFetchSaleAutorizationRequest() {
        JsonObject request = new JsonObject();
        try {
            String serial = this.getIdentifierSerial();
            serial = serial.replace("ñ", "");
            serial = serial.replace("_", "");
            
            request.addProperty("surtidor", this.getSelectedPumpNumber());
            request.addProperty("cantidad", 0);
            request.addProperty("monto", 0);
            request.addProperty("numeroCara", this.getSelectedFaceNumber());
            request.addProperty("valorOdometro", Long.valueOf(this.getOdometer()));
            request.addProperty("codigoFamiliaProducto", this.getSelectedProductFamilyId());
            request.addProperty("precioVentaUnidad", this.getSelectedProductPrice());
            request.addProperty("codigoSeguridad", this.getCardPass());
            request.addProperty("codigoTipoIdentificador", NovusUtils.tipoIdentificacionPromotor(Main.persona.getTipoIdentificacionDesc()));
            request.addProperty("serialIdentificador", serial);
            request.addProperty("identificadorPromotor", Long.valueOf(Main.persona.getIdentificacion()));
            request.addProperty("idPromotor", Main.persona.getId());
            request.addProperty("medioAutorizacion", this.getIdentifierMeanSelected().getId());
            request.addProperty("identificadorGrado", getSelectedGrade());
            if (isAdBlue()) {
                request.addProperty("codigoProducto", obtenerCodigoExternoUreaUseCase.execute());
            }
            infoRequest = request;
            return request;
        } catch (Exception e) {
            log("Error al construir la petición de autorización de venta (buildFetchSaleAutorizationRequest).", e);
            return new JsonObject(); 
        }
    }

    JsonObject buildJsonInfoVenta(String odometro) {
        try {
        JsonObject infoVenta = new JsonObject();
        infoVenta.addProperty("surtidor", this.getSelectedPumpNumber());
        infoVenta.addProperty("cara", this.getSelectedFaceNumber());
        infoVenta.addProperty("cantidad", 0);
        infoVenta.addProperty("monto", 0);
        infoVenta.addProperty("valorOdometro", odometro);
        infoVenta.addProperty("codigoFamiliaProducto", this.getSelectedProductFamilyId());
        infoVenta.addProperty("precioVentaUnidad", this.getSelectedProductPrice());
        infoVenta.addProperty("codigoSeguridad", this.getCardPass());
        infoVenta.addProperty("codigoTipoIdentificador", 6);
        infoVenta.addProperty("serialIdentificador", "");
        infoVenta.addProperty("identificadorPromotor", Main.persona.getId());
        infoVenta.addProperty("identificadorGrado", getSelectedGrade());
        return infoVenta;
        } catch (Exception e) {
            log("Error al construir la petición de autorización de venta (buildJsonInfoVenta).", e);
            return new JsonObject();
        }
    }

    void solicitarAutorizacion() {
        cargar("SOLICITANDO AUTORIZACIÓN", "/com/firefuel/resources/loader_fac.gif");
        NovusUtils.printLn("INICIANDO LA TAREA PARA CERRAR");
        setTimeout(1, () -> {
            if (!isSaleAuthorized()) {
                JsonObject response = RumboFacade.fetchSaleAuthorization(this.buildFetchSaleAutorizationRequest(), isAdBlue());
                handleSaleAuthorizationResponse(response);
                try {
                    setIsRequestAuthoritation(false);
                } catch (Exception e) {
                    log("Error al parsear 'cara' desde la petición del identificador.", e);
                    NovusUtils.printLn(e.getMessage());
                }
            } else {
                NovusUtils.printLn("******ya fue autorizada");
            }
        });
        NovusUtils.printLn("MOSTRANDO EL DIALOGO");
    }

    void loadSalesPanel(JsonObject data) {
        if (isAdBlue()) {
            loadSalePanelAdBlue(data);
        } else {
            loadSalePanelRumbo();
        }
    }

    void loadSalePanelRumbo() {
        btn_next.setVisible(false);
        btn_back.setVisible(false);
        showPanel(PNL_SALE);
        loadSalePanel();
    }

    void loadSalePanelAdBlue(JsonObject data) {
        responseAdBlue = data;
        AD_BLUE = true;
        btn_back.setVisible(false);
        float cantidadAutorizada = litrosAutorizados(data.get("data").getAsJsonObject().get("montoMaximo").getAsFloat());
        if (cantidadAutorizada > 0f) {
            pn = PNL_SALE_ADBLUE;
            lblLitros.setText(cantidadAutorizada + "");
            lblPlacaAdblue.setText(data.get("data").getAsJsonObject().get("placaVehiculo").getAsString());
            showPanel(PNL_SALE_ADBLUE);
            btn_next.setText("ACEPTAR");
            btn_next.setVisible(true);
        } else {
            notificacion("ERROR CANTIDAD AUTORIZADA", "/com/firefuel/resources/btBad.png", true, 3000, true);
            CERRAR = "";
        }
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

    void handleSaleAuthorizationResponse(JsonObject response) {

        NovusUtils.printLn("RESPUESTA DE LA SOLICITUD 1 >> " + response);
        final String DEFINED_CODE_ERROR_MESSAGE = "40000";
        if (response != null) {
            NovusUtils.printLn("RESPUESTA DE LA SOLICITUD 2 >> " + response.toString());
            if (response.get("error") == null) {

                JsonObject dataJson = response.get("data").getAsJsonObject();
                JsonObject aditionalDataJson = dataJson.get("datosAdicionales").getAsJsonObject();
                JsonObject timeoutsJson = dataJson.get("timeout").getAsJsonObject();

                requiereInformacionAdicional = aditionalDataJson.get("requiereInformacionAdicional") != null
                        && !aditionalDataJson.get("requiereInformacionAdicional").isJsonNull()
                        && aditionalDataJson.get("requiereInformacionAdicional").getAsBoolean();
                requierePlacaVehiculo = aditionalDataJson.get("requierePlacaVehiculo") != null
                        && !aditionalDataJson.get("requierePlacaVehiculo").isJsonNull()
                        && aditionalDataJson.get("requierePlacaVehiculo").getAsBoolean();
                requiereCodigoSeguridad = false;

                if (aditionalDataJson.get("codigoSeguridad") != null
                        && !aditionalDataJson.get("codigoSeguridad").isJsonNull()
                        && !aditionalDataJson.get("codigoSeguridad").getAsString().trim().equals("")) {
                    requiereCodigoSeguridad = true;
                }

                this.setAditionalDataTimeout(timeoutsJson.get("additionalData").getAsLong() * 1000);
                this.setSaleAuthorizationTimeout(timeoutsJson.get("authorization").getAsLong() * 1000);
                this.setPumpUnlockingTimeout(timeoutsJson.get("pumpUnlocking").getAsLong() * 1000);
                this.setAuthorizationIdentifier(dataJson.get("identificadorAutorizacionEDS").getAsString().trim());
                this.jrPlaca.setText(dataJson.get("placaVehiculo").getAsString());

                if ((requiereInformacionAdicional || requierePlacaVehiculo || requiereCodigoSeguridad) && !isAdBlue()) {

                    txt_license_plate.setEnabled(requierePlacaVehiculo);
                    if (requierePlacaVehiculo) {
                        txt_license_plate.requestFocus();
                        txt_license_plate.setBackground(new java.awt.Color(255, 255, 255));
                        txt_license_plate.setForeground(new java.awt.Color(75, 74, 91));
                    } else {
                        txt_license_plate.setBackground(Color.GRAY);
                        labelPlaca.setIcon(datosAdicionalesInactivo);
                    }

                    txt_pin.setEnabled(requiereCodigoSeguridad);
                    if (requiereCodigoSeguridad) {
                        txt_pin.requestFocus();
                        txt_pin.setBackground(new java.awt.Color(255, 255, 255));
                        txt_pin.setForeground(new java.awt.Color(75, 74, 91));
                    } else {
                        txt_pin.setVisible(false);
                        txt_pin.setBackground(Color.GRAY);
                        labelPin.setIcon(datosAdicionalesInactivo);
                    }

                    txt_license_info1.setEnabled(requiereInformacionAdicional);
                    if (requiereInformacionAdicional) {
                        txt_license_info1.requestFocus();
                        txt_license_info1.setBackground(new java.awt.Color(255, 255, 255));
                        txt_license_info1.setForeground(new java.awt.Color(75, 74, 91));
                    } else {
                        txt_license_info1.setBackground(Color.GRAY);
                        jLabel21.setIcon(datosInfoInactivo);
                    }

                    setIndex(index + 1);
                    CERRAR = PNL_ADITIONAL_DATA;
                    showPanel(PNL_ADITIONAL_DATA);
                    pn = PNL_ADITIONAL_DATA;
                    btn_next.setVisible(true);
                    btn_back.setVisible(true);
                    loadAditionalDataPanel();

                } else {
                    this.setIsSaleAuthorized(true);
                    btn_next.setVisible(false);
                    btn_back.setVisible(false);
                    setTimeout(1, () -> {
                        CERRAR = PNL_SALE;
                        ULTIMO_PANEL = CERRAR;
                        setIndex(index + 1);
                        loadSalesPanel(response);
                    });
                }

            } else {
                String codeError = response.get("codigoError").getAsString();
                String messageError = "ERROR GENERAL";
                if (response.get("mensajeError") != null && !response.get("mensajeError").isJsonNull()) {
                    messageError = response.get("mensajeError").getAsString();
                }

                if (response.get("mensaje") != null && !response.get("mensaje").isJsonNull()) {
                    messageError = response.get("mensaje").getAsString();
                }

                if (codeError.equals(DEFINED_CODE_ERROR_MESSAGE)) {
                    notificacion(messageError.toUpperCase(), "/com/firefuel/resources/btBad.png", true, 3000, true);
                    CERRAR = "";
                } else {
                    notificacion("ERROR, VENTA NO PUDO SER AUTORIZADA (" + messageError + ")", "/com/firefuel/resources/btBad.png", true, 3000, true);
                    CERRAR = "";
                }
            }
        } else {
            notificacion("OCURRIO UN ERROR EN LA SOLICITUD", "/com/firefuel/resources/btBad.png", true, 3000, true);
            CERRAR = "";
        }
    }

    void loadReaderIdentifierPanel() {
        CardLayout layout = (CardLayout) this.pnl_identifier_listen.getLayout();
        layout.show(this.pnl_identifier_listen, "pnl_identifier_reader");
        if (this.getIdentifierMeanSelected() != null) {
            try {
                this.lbl_identifier_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/" + this.getIdentifierMeanSelected().getUrlImagen_2x()))); // NOI18N
            } catch (Exception e) {
                log("Error al cargar la imagen del identificador.", e);
                NovusUtils.printLn(e.getMessage());
            }
            this.lbl_identifier_name.setText(this.getIdentifierMeanSelected().getDescripcion());
        }
    }

    void loadInputsIdentifierPanel() {
        CardLayout layoutIdentifierPanel = (CardLayout) this.pnl_identifier_listen.getLayout();
        layoutIdentifierPanel.show(this.pnl_identifier_listen, "pnl_identifier_inputs");
        if (this.getIdentifierMeanSelected() != null) {
            int identifierSelectedId = this.getIdentifierMeanSelected().getId();
            CardLayout layoutIdentifierInputsPanel = (CardLayout) this.pnl_identifier_inputs.getLayout();
            switch (identifierSelectedId) {
                case CARD_IDENTIFIER_ID:
                    this.txt_serial_card.setText("");
                    layoutIdentifierInputsPanel.show(this.pnl_identifier_inputs, "pnl_card_input");
                    this.txt_serial_card.requestFocus();
                    break;
                case NUMERIC_CODE_IDENTIFIER_ID:
                    layoutIdentifierInputsPanel.show(this.pnl_identifier_inputs, "pnl_numeric_code_input");
                    this.txt_numeric_code_serial.setText("");
                    this.txt_numeric_code_serial.requestFocus();
                    break;
                case APP_RUMBO_ID:
                    layoutIdentifierInputsPanel.show(this.pnl_identifier_inputs, "pnl_placa_input");
                    RumboView.txt_placa_input.setText("");
                    RumboView.txt_placa_input.requestFocus();
                    break;
                default:
                    break;
            }
        } else {
            if (APP_RUMBO) {
                habilitarBotonesBackNext(true);
                CardLayout layoutIdentifierInputsPanel = (CardLayout) this.pnl_identifier_inputs.getLayout();
                layoutIdentifierInputsPanel.show(this.pnl_identifier_inputs, "pnl_placa_input");
                RumboView.txt_placa_input.setText("");
                RumboView.txt_placa_input.requestFocus();
            }
        }
    }

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

    void loadAditionalDataPanel() {
        isSelectorIdentifiersPanel = false;
        this.stopTimeoutAditionalData = false;
        if (this.getIdentifierMeanSelected() != null) {
            CardLayout layoutAditionalDataPanel = (CardLayout) this.pnl_aditional_data.getLayout();
            this.resetProgressBar();
            if (!requierePlacaVehiculo && !requiereInformacionAdicional && requiereCodigoSeguridad) {
                txt_pin.setText(new String(txt_pass_card.getPassword()));
                this.stopTimeoutAditionalData = true;
                btn_next.setVisible(false);
                btn_back.setVisible(false);
                this.fetchAditionalData();
            } else if (requierePlacaVehiculo || requiereInformacionAdicional) {
                layoutAditionalDataPanel.show(this.pnl_aditional_data, "pnl_aditional_data_license_plate");
                this.renderTimeoutPanels(this.pnl_aditional_data_license_plate, false, 1);
                this.txt_license_plate.requestFocus();
            }
        }
        this.initTimeoutAditionalData(0, this.getAditionalDataTimeout());
        this.toggleSteppersButtons(this.btn_back, false);
    }

    void changeTimeoutProgress(long initialTimeoutMS, long finalTimeoutMS) {
        int progressWidth = this.lbl_progress_back.getWidth();
        int percentage = Math.round(((float) initialTimeoutMS / (float) finalTimeoutMS) * 100);
        float totalProgress = (float) progressWidth / 100;
        this.changeProgressBarValue(Math.round(totalProgress * percentage));
        this.renderDurationTimeoutString(finalTimeoutMS - initialTimeoutMS);
    }

    void runTimeoutAditionalData(long initialTimeoutMS, long finalTimeoutMS) {
        if (initialTimeoutMS <= finalTimeoutMS) {
            setTimeout(1, () -> initTimeoutAditionalData(initialTimeoutMS + 1000, finalTimeoutMS));
            this.changeTimeoutProgress(initialTimeoutMS, finalTimeoutMS);
        } else {
            this.timeoutExpiration();
        }
    }

    void runTimeoutSale(long initialTimeoutMS, long finalTimeoutMS) {
        if (initialTimeoutMS <= finalTimeoutMS) {
            setTimeout(1, () -> initTimeoutSale(initialTimeoutMS + 1000, finalTimeoutMS));
            this.changeTimeoutProgress(initialTimeoutMS, finalTimeoutMS);
        } else {
            this.timeoutExpiration();
        }
    }

    void initTimeoutAditionalData(long initialTimeoutMS, long finalTimeoutMS) {
        if (this.isVisible() && !(this.stopTimeoutAditionalData)) {
            this.runTimeoutAditionalData(initialTimeoutMS, finalTimeoutMS);
        }
    }

    void initTimeoutSale(long initialTimeoutMS, long finalTimeoutMS) {
        if (this.isVisible() && !(this.stopTimeoutSale)) {
            this.runTimeoutSale(initialTimeoutMS, finalTimeoutMS);
        }
    }

    void timeoutExpiration() {
        this.setIsSaleAuthorized(false);
        this.stopTimeoutAditionalData = true;
        this.stopTimeoutSale = true;
        this.toggleSteppersButtons(this.btn_next, false);
        notificacion("TIEMPO EXPIRADO", "/com/firefuel/resources/btBad.png", true, 2000, true);
        CERRAR = "";
    }

    void loadIdentifierListenPanel() {
        isSelectorIdentifiersPanel = false;
        if (this.getIdentifierMeanSelected() != null) {
            boolean readerIsRequired = this.getIdentifierMeanSelected().isNecesarioLector();
            this.setIdentifierSerial(null);
            if (readerIsRequired) {
                this.loadReaderIdentifierPanel();
                this.toggleSteppersButtons(this.btn_next, false);
            } else {
                this.setCardPass(null);
                this.loadInputsIdentifierPanel();
            }
        }
    }

    public String getCardPass() {
        String pass = new String(this.txt_pass_card.getPassword());
        return cardPass != null ? cardPass : pass;
    }

    public void setCardPass(String cardPass) {
        this.cardPass = cardPass;
    }

    void loadIdentifiersSelectorPanel() {
        this.setIdentifierMeanSelected(null);
        this.renderIdentifierMeansItem();
        this.toggleSteppersButtons(this.btn_next, false);
        isSelectorIdentifiersPanel = true;
    }

    void controlPanelRendered(int index) {
        cargaInformacionMangueras();
        pn = this.getPanelName(index);
        isSelectorIdentifiersPanel = false;
        NovusUtils.printLn("[controlPanelRendered]" + index + " pn " + pn);
        switch (pn) {
            case PNL_HOSES_SELECTOR:
                this.loadHosesSelectorPanel();
                break;
            case PNL_CAR_DATA:
                this.loadCarDataPanel();
                break;
            case PNL_IDENTIFIERS_SELECTOR:
                this.loadIdentifiersSelectorPanel();
                break;
            case PNL_IDENTIFIER_LISTEN:
                this.loadIdentifierListenPanel();
                break;
            case PNL_ADITIONAL_DATA:
                this.loadAditionalDataPanel();
                break;
            case PNL_SALE:
                CERRAR = PNL_SALE;
                showPanel(PNL_SALE);
                this.loadSalePanel();
                btn_next.setVisible(false);
                break;
            default:
                NovusUtils.printLn("[controlPanelRendered]" + index + " DEFAULT FOR PN " + pn);
                break;
        }
    }

    void mostrarPanel(String panel) {
        NovusUtils.printLn("[mostrarPanel]" + panel);
        CardLayout cardPanelLayout = (CardLayout) this.pnl_layout_container.getLayout();
        cardPanelLayout.show(pnl_layout_container, panel);
    }

    void loadSalePanel() {
        NovusUtils.printLn("SALE PANEL******************************************************+");
        isSelectorIdentifiersPanel = false;
        this.resetProgressBar();
        this.renderTimeoutPanels(this.pnl_sale, true, 0);
        this.stopTimeoutSale = false;
        this.initTimeoutSale(0, this.getSaleAuthorizationTimeout());
        this.toggleSteppersButtons(this.btn_back, false);

        jrManguera.setText(getSelectedHoseNumber() + "");
        String producto = lbl_product_name.getText().replace("Producto: ", "");
        jrFamilia.setText(producto);
        btn_next.setVisible(false);
        btn_back.setVisible(false);
    }

    void handleStepper(int index, boolean validate) {
        if (index > this.index && validate && !isValidDataStep(this.index)) {
            return;
        }
        this.stopTimeoutAditionalData = true;
        this.stopTimeoutSale = true;
        CardLayout cardPanelLayout = (CardLayout) this.pnl_layout_container.getLayout();
        if (index > this.index) {
            cardPanelLayout.next(this.pnl_layout_container);
        } else if (index < this.index) {
            cardPanelLayout.previous(this.pnl_layout_container);
        }
        this.setIndex(index);
        this.toggleSteppersButtons(this.btn_back, this.hasPreviousChild());
        this.toggleSteppersButtons(this.btn_next, this.hasNextChild());
        this.controlPanelRendered(this.index);
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

    boolean hasPreviousChild() {
        return this.index > 0;
    }

    boolean hasNextChild() {
        return this.index < (this.getContainerChildsCount() - 1);
    }

    void toggleSteppersButtons(JLabel button, boolean visible) {
        button.setVisible(visible);
    }

    void init() {
        this.obtenerPrecioUreaUseCase = new ObtenerPrecioUreaUseCase();
        this.obtenerCodigoExternoUreaUseCase = new ObtenerCodigoExternoUreaUseCase();
        this.obtenerIdBodegaUreaUseCase = new ObtenerIdBodegaUreaUseCase();
        this.loadData();
        this.loadView();
        this.loadIdentificadorPOS();
        InfoViewController.NotificadorRumbo = (JsonObject data) -> {
            switch (data.get("tipo").getAsInt()) {
                case 1:
                    handleIdentifierRequest(data);
                    break;
                case 2:
                    listenSale(data.get("manguera").getAsInt(), data.get("saleAuthorizationIdentifier").getAsString());
                    break;
                case 3:
                    mostrarMensaje(data);
                    break;
                default:
                    break;
            }
        };
    }

    void close(boolean liberar) {
        this.dispose();
        RumboView.instance = null;
        InfoViewController.NotificadorRumbo = null;
        if (panelActualFlujoApp.equals(PNL_HOSES_SELECTOR) && APP_RUMBO) {
            liberarAutorizacion(1);
        }
        if (liberar) {
            liberarAdblue();
        }
    }

    public void liberarAdblue() {
        if (responseAdBlue.size() > 0) {
            NovusUtils.printLn("Existe Autorizacion");
            JsonObject data = responseAdBlue.get("data").getAsJsonObject();
            RumboFacade.liberacionVentaAdBlue(buildJsonLiberacion(data));
        } else {
            NovusUtils.printLn("No Existe Autorizacion");
            log("No existe autorización AdBlue para liberar o el objeto de respuesta es inválido.");
        }
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
        btn_next = new javax.swing.JLabel();
        pnl_layout_container = new javax.swing.JPanel();
        pnl_identifiers_selector = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        pnl_identifiers = new javax.swing.JPanel();
        pnl_hoses_selector = new javax.swing.JPanel();
        lbl_product_name = new javax.swing.JLabel();
        pnl_hoses_carousel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        icon_info = new javax.swing.JLabel();
        pnl_car_data = new javax.swing.JPanel();
        txt_odometer = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pnl_keyboard = new TecladoNumericoGray();
        background_card_data = new javax.swing.JLabel();
        pnl_identifier_listen = new javax.swing.JPanel();
        pnl_identifier_reader = new javax.swing.JPanel();
        lbl_identifier_name = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbl_identifier_image = new javax.swing.JLabel();
        pnl_identifier_inputs = new javax.swing.JPanel();
        pnl_card_input = new javax.swing.JPanel();
        pnl_keyboard1 = new TecladoNumericoGray();
        txt_pass_card = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_serial_card = new javax.swing.JPasswordField();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        pnl_numeric_code_input = new javax.swing.JPanel();
        txt_numeric_code_serial = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pnl_keyboard2 = new TecladoNumericoGray();
        background_numeric_code = new javax.swing.JLabel();
        pnl_placa_input = new javax.swing.JPanel();
        jPanel3 = new TecladoExtendidoGray();
        txt_placa_input = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        pnl_aditional_data = new javax.swing.JPanel();
        pnl_aditional_data_license_plate = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_pin = new javax.swing.JPasswordField();
        txt_license_plate = new javax.swing.JTextField();
        jPanel2 = new TecladoExtendidoGray();
        jLabel16 = new javax.swing.JLabel();
        txt_license_info1 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        labelPlaca = new javax.swing.JLabel();
        labelPin = new javax.swing.JLabel();
        pnl_sale = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jrManguera = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jrPlaca = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jrFamilia = new javax.swing.JLabel();
        pnlsaleAdBlue = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblLitros = new javax.swing.JLabel();
        jrFamilia1 = new javax.swing.JLabel();
        lblPlacaAdblue = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        iconInfo = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        pnl_confirmacion = new javax.swing.JPanel();
        jDenegar = new javax.swing.JLabel();
        jAceptar = new javax.swing.JLabel();
        jMensajeConfirmacion = new javax.swing.JLabel();
        mensajes = new javax.swing.JPanel();
        jIcono = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jCerrar = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
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

        btn_next.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_next.setForeground(new java.awt.Color(223, 26, 9));
        btn_next.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_white.png"))); // NOI18N
        btn_next.setText("Siguiente");
        btn_next.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_nextMouseReleased(evt);
            }
        });
        getContentPane().add(btn_next);
        btn_next.setBounds(970, 720, 130, 60);

        pnl_layout_container.setOpaque(false);
        pnl_layout_container.setLayout(new java.awt.CardLayout());

        pnl_identifiers_selector.setName("pnl_identifiers_selector"); // NOI18N
        pnl_identifiers_selector.setOpaque(false);
        pnl_identifiers_selector.setLayout(null);

        title.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Medios de autorización");
        title.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_identifiers_selector.add(title);
        title.setBounds(430, 40, 390, 30);

        pnl_identifiers.setOpaque(false);
        pnl_identifiers.setLayout(null);
        pnl_identifiers_selector.add(pnl_identifiers);
        pnl_identifiers.setBounds(20, 0, 1240, 530);

        pnl_layout_container.add(pnl_identifiers_selector, "pnl_identifiers_selector");
        pnl_identifiers_selector.getAccessibleContext().setAccessibleName("pnl_identifiers_selector");

        pnl_hoses_selector.setName("pnl_hoses_selector"); // NOI18N
        pnl_hoses_selector.setOpaque(false);
        pnl_hoses_selector.setLayout(null);

        lbl_product_name.setFont(new java.awt.Font("Terpel Sans", 1, 26)); // NOI18N
        lbl_product_name.setForeground(new java.awt.Color(64, 101, 246));
        lbl_product_name.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_product_name.setText("Seleccione una manguera");
        pnl_hoses_selector.add(lbl_product_name);
        lbl_product_name.setBounds(100, 510, 980, 80);

        pnl_hoses_carousel.setName("pnl_hoses_carousel"); // NOI18N
        pnl_hoses_carousel.setOpaque(false);
        pnl_hoses_carousel.setLayout(null);
        pnl_hoses_selector.add(pnl_hoses_carousel);
        pnl_hoses_carousel.setBounds(20, 50, 1230, 460);
        pnl_hoses_carousel.getAccessibleContext().setAccessibleName("pnl_hoses_carousel");

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Mangueras");
        pnl_hoses_selector.add(jLabel9);
        jLabel9.setBounds(110, 0, 1060, 50);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/mangueras.png"))); // NOI18N
        pnl_hoses_selector.add(jLabel18);
        jLabel18.setBounds(20, 50, 1250, 470);

        icon_info.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/info_producto.png"))); // NOI18N
        icon_info.setToolTipText("");
        pnl_hoses_selector.add(icon_info);
        icon_info.setBounds(50, 530, 43, 40);

        pnl_layout_container.add(pnl_hoses_selector, "pnl_hoses_selector");
        pnl_hoses_selector.getAccessibleContext().setAccessibleName("pnl_hoses_selector");

        pnl_car_data.setName("pnl_car_data"); // NOI18N
        pnl_car_data.setOpaque(false);
        pnl_car_data.setLayout(null);

        txt_odometer.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_odometer.setForeground(new java.awt.Color(75, 74, 91));
        txt_odometer.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_odometer.setBorder(null);
        txt_odometer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_odometerFocusGained(evt);
            }
        });
        txt_odometer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                txt_odometerComponentShown(evt);
            }
        });
        txt_odometer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_odometerActionPerformed(evt);
            }
        });
        txt_odometer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_odometerKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_odometerKeyTyped(evt);
            }
        });
        pnl_car_data.add(txt_odometer);
        txt_odometer.setBounds(100, 160, 500, 60);

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("INGRESE KILOMETRAJE:");
        pnl_car_data.add(jLabel1);
        jLabel1.setBounds(90, 90, 520, 50);
        pnl_car_data.add(pnl_keyboard);
        pnl_keyboard.setBounds(660, 30, 570, 470);

        background_card_data.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndCardData.png"))); // NOI18N
        pnl_car_data.add(background_card_data);
        background_card_data.setBounds(0, 0, 1280, 530);

        pnl_layout_container.add(pnl_car_data, "pnl_car_data");
        pnl_car_data.getAccessibleContext().setAccessibleName("pnl_car_data");

        pnl_identifier_listen.setName("pnl_identifier_listen"); // NOI18N
        pnl_identifier_listen.setOpaque(false);
        pnl_identifier_listen.setLayout(new java.awt.CardLayout());

        pnl_identifier_reader.setName("pnl_identifier_reader"); // NOI18N
        pnl_identifier_reader.setOpaque(false);
        pnl_identifier_reader.setLayout(null);

        lbl_identifier_name.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lbl_identifier_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnl_identifier_reader.add(lbl_identifier_name);
        lbl_identifier_name.setBounds(100, 450, 1080, 130);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("PRESENTE POR FAVOR SU IDENTIFICADOR");
        pnl_identifier_reader.add(jLabel2);
        jLabel2.setBounds(100, 10, 1080, 100);

        lbl_identifier_image.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lbl_identifier_image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_identifier_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ibutton-rumbo.png"))); // NOI18N
        pnl_identifier_reader.add(lbl_identifier_image);
        lbl_identifier_image.setBounds(430, 140, 420, 280);

        pnl_identifier_listen.add(pnl_identifier_reader, "pnl_identifier_reader");
        pnl_identifier_reader.getAccessibleContext().setAccessibleName("pnl_identifier_reader");

        pnl_identifier_inputs.setName("pnl_identifier_inputs"); // NOI18N
        pnl_identifier_inputs.setOpaque(false);
        pnl_identifier_inputs.setLayout(new java.awt.CardLayout());

        pnl_card_input.setName("pnl_card_input"); // NOI18N
        pnl_card_input.setOpaque(false);
        pnl_card_input.setLayout(null);
        pnl_card_input.add(pnl_keyboard1);
        pnl_keyboard1.setBounds(670, 30, 570, 470);

        txt_pass_card.setFont(new java.awt.Font("Arial", 1, 55)); // NOI18N
        txt_pass_card.setForeground(new java.awt.Color(75, 74, 91));
        txt_pass_card.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_pass_card.setBorder(null);
        txt_pass_card.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_pass_cardFocusGained(evt);
            }
        });
        txt_pass_card.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pass_cardKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pass_cardKeyTyped(evt);
            }
        });
        pnl_card_input.add(txt_pass_card);
        txt_pass_card.setBounds(80, 300, 490, 60);

        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("INGRESE CLAVE:");
        pnl_card_input.add(jLabel5);
        jLabel5.setBounds(50, 230, 450, 50);

        jLabel4.setBackground(new java.awt.Color(51, 51, 51));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("PRESENTE LA TARJETA:");
        pnl_card_input.add(jLabel4);
        jLabel4.setBounds(50, 60, 450, 50);

        txt_serial_card.setFont(new java.awt.Font("Arial", 0, 55)); // NOI18N
        txt_serial_card.setForeground(new java.awt.Color(75, 74, 91));
        txt_serial_card.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_serial_card.setBorder(null);
        txt_serial_card.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_serial_cardFocusGained(evt);
            }
        });
        txt_serial_card.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_serial_cardActionPerformed(evt);
            }
        });
        txt_serial_card.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_serial_cardKeyTyped(evt);
            }
        });
        pnl_card_input.add(txt_serial_card);
        txt_serial_card.setBounds(80, 150, 490, 60);

        jLabel15.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(186, 12, 47));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btnLimpiar.png"))); // NOI18N
        jLabel15.setText("LIMPIAR");
        jLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel15MouseReleased(evt);
            }
        });
        pnl_card_input.add(jLabel15);
        jLabel15.setBounds(240, 410, 190, 70);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/labelWhiteBorderRed.png"))); // NOI18N
        jLabel17.setFocusable(false);
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_card_input.add(jLabel17);
        jLabel17.setBounds(50, 290, 550, 80);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/labelWhiteBorderRed.png"))); // NOI18N
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_card_input.add(jLabel20);
        jLabel20.setBounds(50, 140, 550, 80);

        pnl_identifier_inputs.add(pnl_card_input, "pnl_card_input");
        pnl_card_input.getAccessibleContext().setAccessibleName("pnl_card_input");

        pnl_numeric_code_input.setName("pnl_numeric_code_input"); // NOI18N
        pnl_numeric_code_input.setOpaque(false);
        pnl_numeric_code_input.setLayout(null);

        txt_numeric_code_serial.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_numeric_code_serial.setForeground(new java.awt.Color(75, 74, 91));
        txt_numeric_code_serial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_numeric_code_serial.setBorder(null);
        txt_numeric_code_serial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_numeric_code_serialFocusGained(evt);
            }
        });
        txt_numeric_code_serial.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                txt_numeric_code_serialComponentShown(evt);
            }
        });
        txt_numeric_code_serial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_numeric_code_serialActionPerformed(evt);
            }
        });
        txt_numeric_code_serial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_numeric_code_serialKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_numeric_code_serialKeyTyped(evt);
            }
        });
        pnl_numeric_code_input.add(txt_numeric_code_serial);
        txt_numeric_code_serial.setBounds(100, 160, 500, 60);

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("INGRESE CODIGO:");
        pnl_numeric_code_input.add(jLabel6);
        jLabel6.setBounds(90, 90, 450, 50);
        pnl_numeric_code_input.add(pnl_keyboard2);
        pnl_keyboard2.setBounds(680, 30, 570, 470);

        background_numeric_code.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndCardData.png"))); // NOI18N
        background_numeric_code.setName(""); // NOI18N
        pnl_numeric_code_input.add(background_numeric_code);
        background_numeric_code.setBounds(0, 0, 1280, 530);

        pnl_identifier_inputs.add(pnl_numeric_code_input, "pnl_numeric_code_input");
        pnl_numeric_code_input.getAccessibleContext().setAccessibleName("pnl_numeric_code_input");

        pnl_placa_input.setOpaque(false);
        pnl_placa_input.setLayout(null);

        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel3MouseReleased(evt);
            }
        });
        pnl_placa_input.add(jPanel3);
        jPanel3.setBounds(124, 220, 1040, 340);

        txt_placa_input.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_placa_input.setForeground(new java.awt.Color(75, 74, 91));
        txt_placa_input.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_placa_input.setBorder(null);
        txt_placa_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_placa_inputKeyTyped(evt);
            }
        });
        pnl_placa_input.add(txt_placa_input);
        txt_placa_input.setBounds(374, 108, 500, 62);

        jLabel12.setBackground(new java.awt.Color(51, 51, 51));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("INGRESE PLACA:");
        pnl_placa_input.add(jLabel12);
        jLabel12.setBounds(370, 40, 340, 48);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/labelWhiteBorderRed.png"))); // NOI18N
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_placa_input.add(jLabel19);
        jLabel19.setBounds(350, 100, 546, 79);

        pnl_identifier_inputs.add(pnl_placa_input, "pnl_placa_input");

        pnl_identifier_listen.add(pnl_identifier_inputs, "pnl_identifier_inputs");
        pnl_identifier_inputs.getAccessibleContext().setAccessibleName("pnl_identifier_inputs");

        pnl_layout_container.add(pnl_identifier_listen, "pnl_identifier_listen");
        pnl_identifier_listen.getAccessibleContext().setAccessibleName("pnl_identifier_listen");

        pnl_aditional_data.setName("pnl_aditional_data"); // NOI18N
        pnl_aditional_data.setOpaque(false);
        pnl_aditional_data.setLayout(new java.awt.CardLayout());

        pnl_aditional_data_license_plate.setName("pnl_aditional_data_license_plate"); // NOI18N
        pnl_aditional_data_license_plate.setOpaque(false);
        pnl_aditional_data_license_plate.setLayout(null);

        jLabel8.setBackground(new java.awt.Color(51, 51, 51));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("PLACA");
        pnl_aditional_data_license_plate.add(jLabel8);
        jLabel8.setBounds(60, 10, 140, 40);

        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setText("PIN:");
        pnl_aditional_data_license_plate.add(jLabel7);
        jLabel7.setBounds(400, 10, 110, 40);

        txt_pin.setBackground(new java.awt.Color(242, 242, 242));
        txt_pin.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        txt_pin.setForeground(new java.awt.Color(75, 74, 91));
        txt_pin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_pin.setBorder(null);
        txt_pin.setEnabled(false);
        txt_pin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_pinFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_pinFocusLost(evt);
            }
        });
        txt_pin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_pinActionPerformed(evt);
            }
        });
        txt_pin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pinKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pinKeyTyped(evt);
            }
        });
        pnl_aditional_data_license_plate.add(txt_pin);
        txt_pin.setBounds(420, 58, 250, 56);

        txt_license_plate.setBackground(new java.awt.Color(242, 242, 242));
        txt_license_plate.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        txt_license_plate.setForeground(new java.awt.Color(75, 74, 91));
        txt_license_plate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_license_plate.setBorder(null);
        txt_license_plate.setEnabled(false);
        txt_license_plate.setName("txt_license_plate"); // NOI18N
        txt_license_plate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_license_plateFocusGained(evt);
            }
        });
        txt_license_plate.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                txt_license_plateComponentShown(evt);
            }
        });
        txt_license_plate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_license_plateActionPerformed(evt);
            }
        });
        txt_license_plate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_license_plateKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_license_plateKeyTyped(evt);
            }
        });
        pnl_aditional_data_license_plate.add(txt_license_plate);
        txt_license_plate.setBounds(90, 58, 250, 56);
        txt_license_plate.getAccessibleContext().setAccessibleName("txt_license_plate");

        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel2MouseReleased(evt);
            }
        });
        pnl_aditional_data_license_plate.add(jPanel2);
        jPanel2.setBounds(130, 250, 1024, 340);

        jLabel16.setBackground(new java.awt.Color(51, 51, 51));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("<html>INFORMACION </br> ADICIONAL</html>");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_aditional_data_license_plate.add(jLabel16);
        jLabel16.setBounds(60, 150, 230, 70);

        txt_license_info1.setFont(new java.awt.Font("Segoe UI", 1, 52)); // NOI18N
        txt_license_info1.setForeground(new java.awt.Color(75, 74, 91));
        txt_license_info1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_license_info1.setBorder(null);
        txt_license_info1.setEnabled(false);
        txt_license_info1.setName("txt_license_plate"); // NOI18N
        txt_license_info1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_license_info1FocusGained(evt);
            }
        });
        txt_license_info1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                txt_license_info1ComponentShown(evt);
            }
        });
        txt_license_info1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_license_info1ActionPerformed(evt);
            }
        });
        txt_license_info1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_license_info1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_license_info1KeyTyped(evt);
            }
        });
        pnl_aditional_data_license_plate.add(txt_license_info1);
        txt_license_info1.setBounds(280, 156, 340, 58);

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndInformacionAdicional.png"))); // NOI18N
        pnl_aditional_data_license_plate.add(jLabel21);
        jLabel21.setBounds(250, 150, 400, 70);

        labelPlaca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndDatosAdicionales.png"))); // NOI18N
        labelPlaca.setText("jLabel22");
        pnl_aditional_data_license_plate.add(labelPlaca);
        labelPlaca.setBounds(60, 50, 310, 70);

        labelPin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndDatosAdicionales.png"))); // NOI18N
        pnl_aditional_data_license_plate.add(labelPin);
        labelPin.setBounds(390, 50, 310, 70);

        pnl_aditional_data.add(pnl_aditional_data_license_plate, "pnl_aditional_data_license_plate");
        pnl_aditional_data_license_plate.getAccessibleContext().setAccessibleName("pnl_aditional_data_license_plate");

        pnl_layout_container.add(pnl_aditional_data, "pnl_aditional_data");
        pnl_aditional_data.getAccessibleContext().setAccessibleName("pnl_aditional_data");

        pnl_sale.setName("pnl_sale"); // NOI18N
        pnl_sale.setOpaque(false);
        pnl_sale.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 60)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("AUTORIZADO PARA VENDER");
        pnl_sale.add(jLabel3);
        jLabel3.setBounds(80, 40, 1120, 80);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 51, 0), 1, true));
        jPanel1.setLayout(null);

        jrManguera.setFont(new java.awt.Font("Lucida Grande", 1, 72)); // NOI18N
        jrManguera.setForeground(new java.awt.Color(255, 51, 0));
        jrManguera.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jrManguera.setText("00");
        jPanel1.add(jrManguera);
        jrManguera.setBounds(20, 70, 120, 90);

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("PLACA");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(170, 100, 280, 30);

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("MANGUERA");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(20, 40, 120, 30);

        jrPlaca.setFont(new java.awt.Font("Lucida Grande", 1, 36)); // NOI18N
        jrPlaca.setForeground(new java.awt.Color(255, 51, 0));
        jrPlaca.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jrPlaca.setText("ADDDD");
        jPanel1.add(jrPlaca);
        jrPlaca.setBounds(170, 130, 310, 50);

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("PRODUCTO");
        jPanel1.add(jLabel14);
        jLabel14.setBounds(170, 10, 120, 30);

        jrFamilia.setFont(new java.awt.Font("Lucida Grande", 1, 36)); // NOI18N
        jrFamilia.setForeground(new java.awt.Color(255, 51, 0));
        jrFamilia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jrFamilia.setText("GASOLINA CORRIENTE");
        jPanel1.add(jrFamilia);
        jrFamilia.setBounds(170, 40, 850, 50);

        pnl_sale.add(jPanel1);
        jPanel1.setBounds(120, 150, 1040, 200);

        pnl_layout_container.add(pnl_sale, "pnl_sale");
        pnl_sale.getAccessibleContext().setAccessibleName("pnl_sale");

        pnlsaleAdBlue.setName("pnl_sale"); // NOI18N
        pnlsaleAdBlue.setOpaque(false);
        pnlsaleAdBlue.setLayout(null);

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 60)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("AUTORIZADO PARA VENDER");
        pnlsaleAdBlue.add(jLabel22);
        jLabel22.setBounds(80, 40, 1120, 80);

        panelRedondo1.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 7, 0)));
        panelRedondo1.setRoundBottomLeft(10);
        panelRedondo1.setRoundBottomRight(10);
        panelRedondo1.setRoundTopLeft(10);
        panelRedondo1.setRoundTopRight(10);
        panelRedondo1.setLayout(null);

        lblLitros.setFont(new java.awt.Font("Lucida Grande", 1, 72)); // NOI18N
        lblLitros.setForeground(new java.awt.Color(255, 7, 0));
        lblLitros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLitros.setText("10");
        panelRedondo1.add(lblLitros);
        lblLitros.setBounds(80, 80, 190, 90);

        jrFamilia1.setFont(new java.awt.Font("Lucida Grande", 1, 36)); // NOI18N
        jrFamilia1.setForeground(new java.awt.Color(255, 7, 0));
        jrFamilia1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jrFamilia1.setText("UREA");
        panelRedondo1.add(jrFamilia1);
        jrFamilia1.setBounds(470, 50, 180, 50);

        lblPlacaAdblue.setFont(new java.awt.Font("Lucida Grande", 1, 36)); // NOI18N
        lblPlacaAdblue.setForeground(new java.awt.Color(255, 7, 0));
        lblPlacaAdblue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPlacaAdblue.setText("ADD123");
        panelRedondo1.add(lblPlacaAdblue);
        lblPlacaAdblue.setBounds(464, 150, 200, 50);

        jLabel24.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("LITROS AUTORIZADOS");
        panelRedondo1.add(jLabel24);
        jLabel24.setBounds(60, 50, 230, 30);

        jLabel26.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("PRODUCTO");
        panelRedondo1.add(jLabel26);
        jLabel26.setBounds(510, 20, 110, 30);

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("PLACA");
        panelRedondo1.add(jLabel23);
        jLabel23.setBounds(530, 110, 70, 30);

        pnlsaleAdBlue.add(panelRedondo1);
        panelRedondo1.setBounds(246, 140, 784, 230);

        iconInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/info_producto.png"))); // NOI18N
        iconInfo.setToolTipText("");
        pnlsaleAdBlue.add(iconInfo);
        iconInfo.setBounds(260, 440, 30, 40);

        lblMensaje.setFont(new java.awt.Font("Terpel Sans", 0, 22)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(64, 101, 246));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMensaje.setText("Recuerde cerrar la venta en la opción Ventas sin resolver");
        pnlsaleAdBlue.add(lblMensaje);
        lblMensaje.setBounds(310, 420, 670, 80);

        pnl_layout_container.add(pnlsaleAdBlue, "pnlsaleAdBlue");

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

        mensajes.setBackground(new java.awt.Color(255, 255, 255));
        mensajes.setName("mensajes"); // NOI18N
        mensajes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btOk.png"))); // NOI18N
        jIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mensajes.add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 160, 320, 270));

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(204, 0, 0));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("MENSAJE DE EXITO");
        mensajes.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 140, 670, 210));

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(223, 26, 9));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/bt-danger-white.png"))); // NOI18N
        jCerrar.setText("Cerrar");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        mensajes.add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 340, 290, 70));

        pnl_layout_container.add(mensajes, "mensajes");
        mensajes.getAccessibleContext().setAccessibleName("mensajes");

        getContentPane().add(pnl_layout_container);
        pnl_layout_container.setBounds(0, 95, 1280, 600);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(1010, 20, 240, 40);

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
        title_view.setText("RUMBO");
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

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        NovusUtils.beep();
        if (runAppRumbo != null) {
            ejecutarAccionDeCerrado = false;
            runAppRumbo.run();
            runAppRumbo = null;
        } else {
            if (CERRAR.equals("")) {
                close(true);
            } else {
                mostrarPanel(CERRAR);
            }
            closeCase();
        }
        CERRAR = "";
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jPanel3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseReleased

    }//GEN-LAST:event_jPanel3MouseReleased

    private void txt_placa_inputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_placa_inputKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, txt_placa_input, 20, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_placa_inputKeyTyped

    private void jDenegarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDenegarMouseClicked

        switch (pn) {
            case PNL_HOSES_SELECTOR:
                mostrarPanel(PNL_HOSES_SELECTOR);
                break;
            case PNL_CAR_DATA:
                mostrarPanel(PNL_CAR_DATA);
                break;
            case PNL_IDENTIFIERS_SELECTOR:
                isSelectorIdentifiersPanel = true;
                mostrarPanel(PNL_IDENTIFIERS_SELECTOR);
                break;
            case PNL_IDENTIFIER_LISTEN:
                mostrarPanel(PNL_IDENTIFIER_LISTEN);
                break;
            case PNL_ADITIONAL_DATA:
                mostrarPanel(PNL_ADITIONAL_DATA);
                btn_next.setVisible(true);
            case PNL_SALE_ADBLUE:
                mostrarPanel(PNL_SALE_ADBLUE);
                btn_next.setVisible(true);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_jDenegarMouseClicked

    private void jAceptarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jAceptarMouseClicked
        handler.run();
        handler = null;
    }//GEN-LAST:event_jAceptarMouseClicked

    private void txt_odometerKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_odometerKeyTyped
//        String caracteresAceptados = "[0-9]";
//        NovusUtils.limitarCarateres(evt, txt_odometer, 9, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_txt_odometerKeyTyped

    private void txt_license_plateKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_license_plateKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, txt_license_plate, 8, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_txt_license_plateKeyTyped

    private void txt_pinKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_pinKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_pin, 10, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_txt_pinKeyTyped

    private void txt_license_info1KeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_license_info1KeyTyped
        String caracteresAceptados = "[0-9A-Za-z. ]";
        NovusUtils.limitarCarateres(evt, txt_license_info1, 30, jNotificacion, caracteresAceptados);

    }// GEN-LAST:event_txt_license_info1KeyTyped

    private void txt_serial_cardKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_serial_cardKeyTyped
    }// GEN-LAST:event_txt_serial_cardKeyTyped

    private void txt_serial_cardFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_serial_cardFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_serial_card);
    }// GEN-LAST:event_txt_serial_cardFocusGained

    private void txt_pass_cardKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_pass_cardKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_pass_card, 10, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_txt_pass_cardKeyTyped

    private void txt_pass_cardFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_pass_cardFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_pass_card);
    }// GEN-LAST:event_txt_pass_cardFocusGained

    private void txt_numeric_code_serialKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_numeric_code_serialKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_numeric_code_serial, 12, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_txt_numeric_code_serialKeyTyped

    private void txt_numeric_code_serialFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_numeric_code_serialFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_numeric_code_serial);
    }// GEN-LAST:event_txt_numeric_code_serialFocusGained

    private void txt_odometerFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_odometerFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_odometer);
    }// GEN-LAST:event_txt_odometerFocusGained

    private void txt_license_plateFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_license_plateFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_license_plate);
    }// GEN-LAST:event_txt_license_plateFocusGained

    private void txt_license_info1FocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_license_info1FocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_license_info1);
        activarTeclado();
    }// GEN-LAST:event_txt_license_info1FocusGained

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        jMensajeConfirmacion.setText("<html><center>¿DESEAS SALIR DE RUMBO?</center></html>");
        btn_back.setVisible(false);
        btn_next.setVisible(false);
        this.showPanel("pnl_confirmacion");
        this.handler = () -> this.close(true);
    }

    void nextStep(boolean validation) {
        this.handleStepper(this.index + 1, validation);
    }

    void previousStep(boolean validation) {
        this.handleStepper(this.index - 1, validation);
    }

    private void btn_backMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_backMousePressed
        if (this.btn_back.isEnabled()) {
            if (APP_RUMBO) {
                flujoAppAtras();
            } else {
                NovusUtils.beep();
                this.previousStep(true);
            }
        }
    }// GEN-LAST:event_btn_backMousePressed

    void handleControlSlider(int order) {
        CardLayout layout = (CardLayout) this.pnl_hoses_carousel.getLayout();
        if (order >= 0) {
            layout.next(this.pnl_hoses_carousel);
        } else {
            layout.previous(this.pnl_hoses_carousel);
        }
    }

    void handleKeyPressOdomoterField(String keyChar, KeyEvent evt) {
        this.setOdometer(this.txt_odometer.getText().trim());
    }

    void handleKeyPressSerialFields(String value, KeyEvent evt) {
        this.setIdentifierSerial(value);
    }

    void handleKeyPressCardPassField(String keyChar, KeyEvent evt) {
        String pass = new String(this.txt_pass_card.getPassword());
        this.setCardPass(pass);
    }

    private void txt_odometerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_odometerActionPerformed

    }// GEN-LAST:event_txt_odometerActionPerformed

    private void txt_odometerKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_odometerKeyReleased
        String keyChar = evt.getKeyChar() + "";
        this.handleKeyPressOdomoterField(keyChar, evt);
    }// GEN-LAST:event_txt_odometerKeyReleased

    private void txt_odometerComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_txt_odometerComponentShown
        this.txt_odometer.requestFocus();
    }// GEN-LAST:event_txt_odometerComponentShown

    private void txt_numeric_code_serialComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_txt_numeric_code_serialComponentShown
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_numeric_code_serialComponentShown

    private void txt_numeric_code_serialActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_numeric_code_serialActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_numeric_code_serialActionPerformed

    private void txt_numeric_code_serialKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_numeric_code_serialKeyReleased
        this.handleKeyPressSerialFields(this.txt_numeric_code_serial.getText().trim(), evt);
    }// GEN-LAST:event_txt_numeric_code_serialKeyReleased

    private void txt_pass_cardKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_pass_cardKeyReleased
        String keyChar = evt.getKeyChar() + "";
        this.handleKeyPressCardPassField(keyChar, evt);
    }// GEN-LAST:event_txt_pass_cardKeyReleased

    private void txt_pinKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_pinKeyReleased
    }// GEN-LAST:event_txt_pinKeyReleased

    private void txt_license_plateComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_txt_license_plateComponentShown
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_license_plateComponentShown

    private void txt_license_plateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_license_plateActionPerformed
    }

    private void txt_license_plateKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_license_plateKeyReleased
    }

    private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jPanel2MouseReleased
    }

    private void btn_nextMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_nextMouseReleased
        continuarProceso();
    }

    private void txt_serial_cardActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_serial_cardActionPerformed
        limpiarFormato();
    }// GEN-LAST:event_txt_serial_cardActionPerformed

    private void jLabel15MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel15MouseReleased
        limpiarTarjeta();
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField1ActionPerformed
        testIbutton();
    }

    private void txt_license_info1KeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_license_info1KeyReleased
    }

    private void txt_license_info1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_license_info1ActionPerformed
    }

    private void txt_license_info1ComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_txt_license_info1ComponentShown
    }

    private void txt_pinActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_pinActionPerformed
    }

    private void txt_pinFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_pinFocusGained
        desactivarTeclado();
        NovusUtils.deshabilitarCopiarPegar(txt_pin);
    }

    private void txt_pinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_pinFocusLost
        activarTeclado();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background_card_data;
    private javax.swing.JLabel background_numeric_code;
    private javax.swing.JLabel background_timeout;
    private javax.swing.JLabel btn_back;
    private javax.swing.ButtonGroup btn_group_identifier_type;
    private javax.swing.JLabel btn_next;
    private javax.swing.JLabel iconInfo;
    private javax.swing.JLabel icon_info;
    private javax.swing.JLabel jAceptar;
    private javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jDenegar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jMensajeConfirmacion;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jbackground;
    private javax.swing.JLabel jrFamilia;
    private javax.swing.JLabel jrFamilia1;
    private javax.swing.JLabel jrManguera;
    private javax.swing.JLabel jrPlaca;
    private javax.swing.JLabel labelPin;
    private javax.swing.JLabel labelPlaca;
    private javax.swing.JLabel lblLitros;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblPlacaAdblue;
    private javax.swing.JLabel lbl_identifier_image;
    private javax.swing.JLabel lbl_identifier_name;
    private javax.swing.JLabel lbl_product_name;
    private javax.swing.JLabel lbl_progress_back;
    private javax.swing.JLabel lbl_progress_duration;
    private javax.swing.JLabel lbl_progress_percentage;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel mensajes;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    private javax.swing.JPanel pnl_aditional_data;
    private javax.swing.JPanel pnl_aditional_data_license_plate;
    private javax.swing.JPanel pnl_car_data;
    private javax.swing.JPanel pnl_card_input;
    private javax.swing.JPanel pnl_confirmacion;
    private javax.swing.JPanel pnl_hoses_carousel;
    private javax.swing.JPanel pnl_hoses_selector;
    private javax.swing.JPanel pnl_identifier_inputs;
    private javax.swing.JPanel pnl_identifier_listen;
    private javax.swing.JPanel pnl_identifier_reader;
    private javax.swing.JPanel pnl_identifiers;
    private javax.swing.JPanel pnl_identifiers_selector;
    private javax.swing.JPanel pnl_keyboard;
    private javax.swing.JPanel pnl_keyboard1;
    private javax.swing.JPanel pnl_keyboard2;
    private javax.swing.JPanel pnl_layout_container;
    private javax.swing.JPanel pnl_numeric_code_input;
    private javax.swing.JPanel pnl_placa_input;
    private javax.swing.JPanel pnl_sale;
    private javax.swing.JPanel pnl_timeout_progress;
    private javax.swing.JPanel pnlsaleAdBlue;
    private javax.swing.ButtonGroup tipoDocumento;
    private javax.swing.JLabel title;
    private javax.swing.JLabel title_view;
    private javax.swing.JTextField txt_license_info1;
    private javax.swing.JTextField txt_license_plate;
    private javax.swing.JTextField txt_numeric_code_serial;
    private javax.swing.JTextField txt_odometer;
    private javax.swing.JPasswordField txt_pass_card;
    private javax.swing.JPasswordField txt_pin;
    public static javax.swing.JTextField txt_placa_input;
    private javax.swing.JPasswordField txt_serial_card;
    // End of variables declaration//GEN-END:variables

    public void limpiarTarjeta() {
        txt_serial_card.setText("");
        txt_pass_card.setText("");
        txt_serial_card.requestFocus();
    }

    private void showPanel(String panel) {      
        NovusUtils.showPanel(pnl_layout_container, panel);
    }

    private void limpiarFormato() {

        String datos = new String(txt_serial_card.getPassword());
        NovusUtils.printLn(datos);
        char[] data = datos.toCharArray();
        String tarjeta = "";
        boolean valido = false;
        for (char c : data) {
            if (c == 'ñ' || c == 'Ñ' || valido) {
                valido = true;
                if (c != 'ñ' && c != 'Ñ') {
                    try {
                        Integer.parseInt(c + "");
                        tarjeta += c;
                    } catch (NumberFormatException a) {
                        log("Error al parsear 'cara' desde la petición del identificador.", a);
                        valido = false;
                    }
                }
            }
        }
        NovusUtils.printLn(tarjeta);
        txt_serial_card.setText(tarjeta);
        txt_pass_card.requestFocus();
    }

    private void testIbutton() {

        JsonObject identifierRequest = new JsonObject();
        identifierRequest.addProperty("medio", "ibutton");
        identifierRequest.addProperty("serial", jTextField1.getText());

        handleIdentifierRequest(identifierRequest);

    }

    private void desactivarTeclado() {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel2;
        teclado.habilitarAlfanumeric(false);
    }

    private void activarTeclado() {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel2;
        teclado.habilitarAlfanumeric(true);
        teclado.habilitarCaracteresEspeciales(false);
    }

    void closeCase() {
        NovusUtils.printLn("isSelectorIdentifiers: " + isSelectorIdentifiersPanel);
        switch (CERRAR) {
            case PNL_SALE:
                habilitarBotonesBackNext(false);
                break;
            case PNL_HOSES_SELECTOR:
                btn_back.setVisible(false);
                break;
            case PNL_IDENTIFIERS_SELECTOR:
                habilitarBotonesBackNext(false);
                break;
            case "":
                if (isSelectorIdentifiersPanel) {
                    habilitarBotonesBackNext(false);
                } else {
                    habilitarBotonesBackNext(true);
                }
                break;
            default:
                habilitarBotonesBackNext(true);
                break;
        }
    }

    private void continuarProceso() {
        if (this.getIdentifierMeanSelected() != null && this.getIdentifierMeanSelected().getId() == RumboView.CARD_IDENTIFIER_ID) {
            this.setIdentifierSerial(new String(txt_serial_card.getPassword()));
        }
        if (this.btn_next.isEnabled() && !APP_RUMBO) {
            NovusUtils.beep();
            if (AD_BLUE) {
                insertInformacionMovimiento();
            } else {
                this.nextStep(true);
            }
        } else {
            flujoAppSiguiente();
        }
    }

    public void insertInformacionMovimiento() {
        NovusUtils.printLn("insertInformacionMovimiento");
        cargar("Procesando autorización urea", "/com/firefuel/resources/loader_fac.gif");
        Thread consulta = new Thread() {
            @Override
            public void run() {

                String medioAutorizacion = infoRequest.get("medioAutorizacion").getAsString();

                infoRequest.addProperty("codigoSeguridad",
                        responseAdBlue.get("data").getAsJsonObject().get("codigoSeguridad").getAsString());

                float cantidadMaxima = litrosAutorizados(responseAdBlue.get("data").getAsJsonObject().get("montoMaximo").getAsFloat());

                responseAdBlue.get("data").getAsJsonObject().addProperty("cantidadMaxima", cantidadMaxima);

                JsonObject ctMovimientos = VentasCombustibleFacade.buildObjectCtMovimientos(responseAdBlue, infoRequest, false, 0, medioAutorizacion);
                JsonObject ctMovimientosCredito = VentasCombustibleFacade.buildObjectCtMovimientos(responseAdBlue, infoRequest, true, 0, medioAutorizacion);
                JsonObject ctMovimientosDetalles = VentasCombustibleFacade.buildObjectCtMovimientoDetalles();
                JsonObject ctMovimientoMediosPago = VentasCombustibleFacade.buildObjectCtMediosPagos();
                CtMovimientoJsonObjDto ctMovimientoJsonObjDto = new CtMovimientoJsonObjDto(ctMovimientos,
                        ctMovimientosCredito,
                        ctMovimientosDetalles,
                        ctMovimientoMediosPago,
                        infoRequest,
                        responseAdBlue
                );
                boolean respuesta = new InsertCtMovimientosUseCase().execute(ctMovimientoJsonObjDto);
                /*boolean respuesta = Main.rumboDao.
                        insertCTmovimientos(ctMovimientos,
                                ctMovimientosCredito,
                                ctMovimientosDetalles,
                                ctMovimientoMediosPago,
                                infoRequest,
                                responseAdBlue);*/

                cerrarMensaje();
                validarRespuesta(respuesta);
            }
        };
        consulta.start();
    }

    public void validarRespuesta(boolean respuesta) {
        if (respuesta) {
            notificacionPlaca("Autorización aprobada", "/com/firefuel/resources/btOk.png", true, 2, () -> {
                close(false);
            });
        } else {
            Runnable mostarPanelSale = () -> mostrarPanelSale();
            notificacionPlaca("Error al generar autorización", "/com/firefuel/resources/btBad.png", true, 2, mostarPanelSale);
        }
    }

    public void mostrarPanelSale() {
        showPanel(PNL_SALE_ADBLUE);
        btn_next.setText("ACEPTAR");
        btn_next.setVisible(true);
    }

    public void validarCaracteresPlaca() {
        String placa = txt_placa_input.getText().trim();
        jNotificacion.setVisible(true);
        if (placa.length() >= 6) {
            jNotificacion.setVisible(false);
            jNotificacion.setText("");
            validacionAutorizacionPlaca(placa);
        } else {
            jNotificacion.setText("MINIMO 6 CARACTERES");
            setTimeout(3, () -> {
                jNotificacion.setVisible(false);
                jNotificacion.setText("");
            });
        }
    }

    public JsonObject validarPlacaHO(String placa) {
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        NovusUtils.printLn("Validando Placa HO");
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log("Validando Placa HO: " + placa);

        JsonObject response = null;
        boolean debug = true;

        String url = NovusConstante.PROTOCOLO_SSL + hostServer + NovusConstante.SECURE_CENTRAL_POINT_CONSULTAR_PLACA_RUMBO;

        JsonObject request = new JsonObject();
        request.addProperty("placa", placa);
        request.addProperty("empresa_id", idEmpresa);
        request.addProperty("equipo_id", idEquipo);

        ClientWSAsync ws = new ClientWSAsync("CONSULTAR PLACA CLIENTE", url, NovusConstante.POST, request, debug, false);
        try {
            response = ws.esperaRespuesta();
            if (response == null) {
                response = ws.getError();
            }
        } catch (Exception ex) {
            log("Error en ws.esperaRespuesta() para validarPlacaHO", ex);
            NovusUtils.printLn(ex.getMessage());
            log("Error en ws.esperaRespuesta() para validarPlacaHO", ex);
        }
        log("Respuesta de validación de placa HO: " + (response != null ? response.toString() : "null"));
        return response;
    }

    public JsonObject confirmarAutorizacionPlacaHO() {
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        NovusUtils.printLn("Confirmando Autorizacion Placa HO");
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log("Confirmando Autorizacion Placa HO");

        JsonObject response = null;
        boolean debug = true;

        String url = NovusConstante.PROTOCOLO_SSL + hostServer + NovusConstante.SECURE_CENTRAL_POINT_CONFIRMAR_VENTA_APP_RUMBO;

        JsonObject request = new JsonObject();
        request.addProperty("placa", responseAppRumbo.get("data").getAsJsonObject().get("placa_vehiculo").getAsString());
        request.addProperty("empresa_id", idEmpresa);
        request.addProperty("equipo_id", idEquipo);

        ClientWSAsync ws = new ClientWSAsync("CONFIRMANDO AUTORIZACION PLACA CLIENTE", url, NovusConstante.PUT, request, debug, false);
        try {
            response = ws.esperaRespuesta();
            if (response == null) {
                response = ws.getError();
            }
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            log("Error en ws.esperaRespuesta() para confirmarAutorizacionPlacaHO", ex);
        }
        log("Respuesta de confirmación de autorización de placa HO: " + (response != null ? response.toString() : "null"));
        return response;
    }

    public void validacionAutorizacionPlaca(String placa) {
        cargar("VALIDANDO PLACA", "/com/firefuel/resources/loader_fac.gif");
        Thread consulta = new Thread() {
            @Override
            public void run() {
                responseAppRumbo = validarPlacaHO(placa);
                cerrarMensaje();
                validacionPlacaResponse(responseAppRumbo);
            }
        };
        consulta.start();
    }

    public void validacionPlacaResponse(JsonObject response) {
        if (response != null) {
            int status = response.has("status") ? response.get("status").getAsInt() : 404;
            switch (status) {
                case NovusConstante.STATUS_200:
                    mensajeAutorizacionPlaca("AUTORIZACIÓN PLACA VALIDA", response);
                    break;
                case NovusConstante.STATUS_400:
                case NovusConstante.STATUS_404:
                    errorAutorizacionPlaca("<html><center>NO EXISTE INFORMACIÓN ASOCIADA A ESTA IDENTIFICACIÓN, POR FAVOR INTENTE NUEVAMENTE</center></html>", 3);
                    break;
                case NovusConstante.STATUS_409:
                    errorAutorizacionPlaca("AUTORIZACIÓN EN PROCESO", 2);
                    break;
                case NovusConstante.STATUS_500:
                    errorAutorizacionPlaca("NO HAY CONEXIÓN", 1);
                    break;
                default:
                    errorAutorizacionPlaca("NO EXISTE AUTORIZACIÓN", 1);
                    break;
            }
        } else {
            if (InfoViewController.hayInternet) {
                errorAutorizacionPlaca("NO HAY CONEXIÓN", 1);
            } else {
                errorAutorizacionPlaca("NO HAY CONEXIÓN DE INTERNET", 1);
            }
        }
    }

    public void mensajeAutorizacionPlaca(String mensaje, JsonObject response) {
        int codigoFamilia = response.get("data").getAsJsonObject().get("codigo_familia_producto").getAsInt();
        Runnable mostrarHoses = () -> mostrarMangueras(codigoFamilia);
        autorizacionPlacaValida = true;
        notificacionPlaca(mensaje, "/com/firefuel/resources/btOk.png", true, 1, mostrarHoses);
    }

    public void errorAutorizacionPlaca(String mensaje, int time) {
        Runnable ingresarPlaca = () -> mostrarIngresoPlaca();
        notificacionPlaca(mensaje, "/com/firefuel/resources/btBad.png", true, time, ingresarPlaca);
        RumboView.txt_placa_input.requestFocus();
    }

    public void mostrarIngresoPlaca() {
        loadInputsIdentifierPanel();
        mostrarPanel(PNL_IDENTIFIER_LISTEN);
        panelActualFlujoApp = PNL_IDENTIFIER_LISTEN;
        RumboView.txt_placa_input.requestFocus();
    }

    public void mostrarMangueras(int codigoFamilia) {
        habilitarBotonesBackNext(true);
        familiaAutorizacionApp = codigoFamilia > 0;
        cargarInformacionManguerasAppRumbo(codigoFamilia);
        this.loadHosesSelectorPanel();
        mostrarPanel(PNL_HOSES_SELECTOR);
        panelActualFlujoApp = PNL_HOSES_SELECTOR;
    }

    public void flujoAppSiguiente() {
        if (APP_RUMBO && !autorizacionPlacaValida) {
            validarCaracteresPlaca();
        } else if (APP_RUMBO && autorizacionPlacaValida && !validacionManguerasSeleccion) {
            validacionManguerasSeleccion = validationHosesSelectorPanel();
            if (validacionManguerasSeleccion) {
                cargar("SOLICITANDO AUTORIZACIÓN", "/com/firefuel/resources/loader_fac.gif");
                this.setIsSaleAuthorized(true);
                setTimeout(3, () -> {
                    if (crearTransaccion()) {
                        showPanel(PNL_SALE);
                        loadSalePanelApp();
                    } else {
                        notificacion("ERROR AL CREAR LA AUTORIZACION", "/com/firefuel/resources/btBad.png", true, 2000, true);
                        CERRAR = "";
                    }
                });
            }
        }
    }

    public void flujoAppAtras() {
        String panelActual = panelActualFlujoApp;
        switch (panelActual) {
            case PNL_IDENTIFIER_LISTEN:
                habilitarBotonesBackNext(false);
                clearHoses();
                cargarMediosIdentificadores();
                showPanel(PNL_IDENTIFIERS_SELECTOR);
                break;
            case PNL_HOSES_SELECTOR:
                liberarAutorizacion(NovusConstante.ESTADO_DISPONIBLE);
                autorizacionPlacaValida = false;
                clearHoses();
                mostrarIngresoPlaca();
                break;
            default:
                break;
        }
    }

    public void habilitarBotonesBackNext(boolean habilitar) {
        btn_back.setVisible(habilitar);
        btn_next.setVisible(habilitar);
    }

    public void liberarAutorizacion(int estado) {
        if (responseAppRumbo != null) {
            long idAutorizacion = responseAppRumbo.get("data").getAsJsonObject().get("identificadorautorizacioneds").getAsLong();
            liberarAutorizacionPlaca(idAutorizacion, estado);
        }
    }

    public JsonObject liberarAutorizacionPlaca(long idAutorizacion, int estado) {
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        NovusUtils.printLn("Liberando Autorizacion Placa HO");
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");

        log("Liberando Autorizacion Placa HO");

        JsonObject json = new JsonObject();
        JsonObject response = null;
        boolean debug = true;

        String url = NovusConstante.PROTOCOLO_SSL + hostServer + NovusConstante.SECURE_CENTRAL_POINT_LIBERAR_AUTORIZACION_APP_RUMBO + idAutorizacion + "/" + estado;
        ClientWSAsync ws = new ClientWSAsync("LIBERANDO AUTORIZACION PLACA CLIENTE", url, NovusConstante.PUT, json, debug, false);
        try {
            response = ws.esperaRespuesta();
            if (response == null) {
                response = ws.getError();
            }
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            log("Error en ws.esperaRespuesta() para liberarAutorizacionPlaca", ex);
        }
        log("Respuesta de liberar autorización de placa HO: " + (response != null ? response.toString() : "null"));
        return response;
    }

    public boolean crearTransaccion() {
        JsonObject infoApp = responseAppRumbo.get("data").getAsJsonObject();
        JsonObject infoVenta = buildJsonInfoVenta(infoApp.get("valor_odometro").getAsString());
        JsonObject infoTransmision = RumboFacade.buildJsonAuthorizationAppRumbo(infoApp, infoVenta, infoRequestAutorizacionApp());
        infoAutorizacionRumbo = infoTransmision;
        this.setAuthorizationIdentifier(infoTransmision.get("codigo").getAsString());
        return new InsertarTransmisionUseCase(infoTransmision.toString()).execute();
    }

    public JsonObject buildJsonConfirmacion() {
        try {
        JsonObject confirmacion = new JsonObject();
        JsonObject infoAutorizacion = responseAppRumbo.get("data").getAsJsonObject();
        confirmacion.addProperty("id_autorizacion", infoAutorizacion.get("id_autorizacion").getAsLong());
        confirmacion.addProperty("empresa_id", idEmpresa);
        confirmacion.addProperty("equipo_id", idEquipo);
        confirmacion.addProperty("placa", infoAutorizacion.get("placa_vehiculo").getAsString());
        return confirmacion;
        } catch (Exception e) {
            log("Error al construir la petición de confirmación de autorización (buildJsonConfirmacion).", e);
            return new JsonObject();
        }
    }

    JsonObject infoRequestAutorizacionApp() {
        JsonObject info = new JsonObject();
        info.addProperty("empresa_id", idEmpresa);
        info.addProperty("equipo_id", idEquipo);
        return info;
    }

    void loadSalePanelApp() {
        panelActualFlujoApp = PNL_SALE;
        NovusUtils.printLn("SALE PANEL APP RUMBO******************************************************");
        log("Cargando panel de venta para App Rumbo.");
        this.resetProgressBar();
        this.renderTimeoutPanels(this.pnl_sale, true, 0);
        this.stopTimeoutSale = false;
        long timeout = edao.getTimeoutVentaRumbo();
        this.initTimeoutSale(0, timeout);
        this.toggleSteppersButtons(this.btn_back, false);

        jrManguera.setText(getSelectedHoseNumber() + "");
        String producto = lbl_product_name.getText().replace("Producto: ", "");
        jrFamilia.setText(producto);
        btn_next.setVisible(false);
        btn_back.setVisible(false);
        jrPlaca.setText(responseAppRumbo.get("data").getAsJsonObject().get("placa_vehiculo").getAsString());
        RumboFacade.notificarAutorizacionAppRumbo(getSelectedHoseNumber(), infoAutorizacionRumbo, infoRequestAutorizacionApp());
    }

    public boolean isIsRequestAuthoritation() {
        return isRequestAuthoritation;
    }

    public void setIsRequestAuthoritation(boolean isRequestAuthoritation) {
        this.isRequestAuthoritation = isRequestAuthoritation;
    }

    private void notificacionPlaca(String mensaje, String imagen, boolean cerrar, int time, Runnable runnable) {
        mostrarPanel("mensajes");
        jMensaje.setText("<html><center>".concat(mensaje).concat("</center></html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        runAppRumbo = runnable;
        btn_back.setVisible(false);
        btn_next.setVisible(false);
        jCerrar.setVisible(cerrar);
        setTimeoutApp(time, runnable);
    }

    private void notificacion(String mensaje, String imagen, boolean cerrar, int time, boolean vistaPrincipal) {
        mostrarPanel("mensajes");
        jMensaje.setText("<html><center>"
                .concat(NovusUtils.convertMessage(
                        LetterCase.FIRST_UPPER_CASE, 
                        mensaje)
                .concat("</center></html>")));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        btn_back.setVisible(false);
        btn_next.setVisible(false);
        jCerrar.setVisible(true);
        int t = time / 1000;
        if (cerrar) {
            setTimeout(t, () -> {
                closeNotificacion(vistaPrincipal);
                CERRAR = "";
            });
        }
    }

    void closeNotificacion(boolean vistaPrincipal) {
        if (!vistaPrincipal) {
            mostrarPanel(CERRAR);
            if (!CERRAR.equals(PNL_SALE)) {
                switch (CERRAR) {
                    case PNL_HOSES_SELECTOR:
                        btn_back.setVisible(false);
                        break;
                    case PNL_IDENTIFIERS_SELECTOR:
                        habilitarBotonesBackNext(false);
                        break;
                    case "":
                        if (isSelectorIdentifiersPanel) {
                            habilitarBotonesBackNext(false);
                        } else {
                            habilitarBotonesBackNext(true);
                        }
                        break;
                    default:
                        habilitarBotonesBackNext(true);
                        break;
                }
            }
        } else {
            close(true);
        }
    }

    private void cargar(String mensaje, String imagen) {
        mostrarPanel("mensajes");
        jMensaje.setText("<html><center>".concat(mensaje).concat("</center></html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        btn_back.setVisible(false);
        btn_next.setVisible(false);
        jCerrar.setVisible(false);
    }

    private void cerrarMensaje() {
        jMensaje.setText("");
        jIcono.setIcon(null);
        btn_back.setVisible(true);
        btn_next.setVisible(true);
        jCerrar.setVisible(true);
    }

    private void cargar(String mensaje, String imagen, boolean cerrar, int time, boolean vistaprincipal) {
        mostrarPanel("mensajes");
        jMensaje.setText("<html><center>".concat(mensaje).concat("</center></html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        btn_back.setVisible(false);
        btn_next.setVisible(false);
        jCerrar.setVisible(false);
        if (cerrar) {
            setTimeout(time, () -> {
                if (vistaprincipal) {
                    close(true);
                }
            });
        }
    }

    private void mostrarMensaje(JsonObject data) {
        if (ULTIMO_PANEL.equals(PNL_SALE)) {
            CERRAR = PNL_SALE;
        } else {
            CERRAR = this.getPanelName(index);
        }
        NovusUtils.printLn(CERRAR + "*****************************************");
        mostrarPanel("mensajes");
        String mensaje = data.get("mensaje").getAsString();
        String icono = data.get("icono").getAsString();
        jMensaje.setText("<html>".concat(mensaje).concat("</html>"));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(icono)));
        jCerrar.setVisible(true);
        btn_back.setVisible(false);
        btn_next.setVisible(false);
    }

    private boolean validarCampos(String argumentos, JTextField campo, int maximo, JLabel label) {
        return NovusUtils.validarCampos(argumentos, campo, maximo, label);
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

    public void setTimeoutApp(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (ejecutarAccionDeCerrado) {
                    runnable.run();
                }
                ejecutarAccionDeCerrado = true;
            } catch (InterruptedException e) {
                log("Interrupted.setTimeoutApp", e);
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                log("Interrupted.setTimeoutApp", e);

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

    private void log(String message) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
        String logMessage = "[" + timestamp + "] [RumboView] " + message + "\n";
        NovusUtils.WriteLogAppend("rumbo.log", logMessage);
    }

    private void log(String message, Throwable t) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
        String logMessage = "[" + timestamp + "] [RumboView] " + message + " | EXCEPTION: ";
        java.io.StringWriter sw = new java.io.StringWriter();
        t.printStackTrace(new java.io.PrintWriter(sw));
        String exceptionAsString = sw.toString();
        NovusUtils.WriteLogAppend("rumbo.log", logMessage + exceptionAsString + "\n");
    }
}

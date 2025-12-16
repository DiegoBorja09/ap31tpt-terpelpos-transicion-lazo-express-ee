package com.firefuel;

import com.bean.MedioIdentificacionBean;
import com.bean.Notificador;
import com.bean.ReciboExtended;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.facade.ClienteFacade;
import static com.facade.FidelizacionFacade.sdf;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.PrinterFacade;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicProgressBarUI;
import teclado.view.common.TecladoExtendido;
import teclado.view.common.TecladoNumerico;

public class RecuperacionVentaView extends javax.swing.JDialog implements Runnable {

    Thread thread = null;
    long aditionalDataTimeout = 0;
    long saleAuthorizationTimeout = 0;
    long pumpUnlockingTimeout = 0;
    String authorizationIdentifier = null;
    ManguerasItem lastHoseSelected = null;
    boolean stopTimeoutAditionalData = true;
    boolean stopTimeoutSale = true;

    InfoViewController mainFrame = null;
    JDialog mainDialog = null;
    ReciboExtended recibo = null;

    int index = 0;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    long selectedProductFamilyId = 0;
    float selectedProductPrice = 0;
    JsonObject recepcionData = null;

    static TreeMap<Integer, MedioIdentificacionBean> mediosIdentificadores = new TreeMap<>();
    static final String PNL_CAR_DATA = "pnl_car_data";
    static final String PNL_IDENTIFIERS_SELECTOR = "pnl_identifiers_selector";
    static final String PNL_IDENTIFIER_LISTEN = "pnl_identifier_listen";
    static final String PNL_ADITIONAL_DATA = "pnl_aditional_data";
    static final String PNL_SALE = "pnl_sale";

    static final String SUBPNL_ADICIONAL_DATA_CARD = "pnl_aditional_data_card";
    static final String SUBPNL_ADICIONAL_DATA_PLATE = "pnl_aditional_data_license_plate";
    static final String SUBPNL_ADICIONAL_DATA_INFO = "pnl_aditional_data_info";

    static final int RFID_IDENTIFIER_ID = 3;
    static final int IBUTTON_IDENTIFIER_ID = 2;
    static final int CARD_IDENTIFIER_ID = 1;
    static final int NUMERIC_CODE_IDENTIFIER_ID = 5;

    // int TIPO_INFORMACION_ADICIONAL = 0;
    final int TIPO_INFORMACION_ADICIONAL_PLACA = 1;
    final int TIPO_INFORMACION_ADICIONAL_CARD = 2;
    final int TIPO_INFORMACION_ADICIONAL_INFO = 3;
    public static Notificador notificadorRecuperacion = null;

    boolean saleAuthorized = false;
    Runnable handler;
    Runnable recargar;

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

    public static RecuperacionVentaView instance = null;

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

    public RecuperacionVentaView(JFrame mainFrame, boolean modal, ReciboExtended recibo) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = (InfoViewController) mainFrame;
        this.recibo = recibo;
        init();
    }

    public RecuperacionVentaView(JFrame mainFrame, boolean modal, ReciboExtended recibo, Runnable recargar) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = (InfoViewController) mainFrame;
        this.recibo = recibo;
        this.recargar = recargar;
        init();
    }

    public RecuperacionVentaView(JDialog mainDialog, boolean modal, ReciboExtended recibo) {
        super(mainDialog, modal);
        initComponents();
        this.mainDialog = mainDialog;
        this.recibo = recibo;
        init();
    }

    public static RecuperacionVentaView getInstance(InfoViewController vistaPrincipal, boolean modal,
            ReciboExtended recibo) {
        if (RecuperacionVentaView.instance == null) {
            RecuperacionVentaView.instance = new RecuperacionVentaView(vistaPrincipal, modal, recibo);
        }
        return RecuperacionVentaView.instance;
    }

    public static RecuperacionVentaView getInstance(InfoViewController vistaPrincipal, boolean modal,
            ReciboExtended recibo, Runnable recargar) {
        if (RecuperacionVentaView.instance == null) {
            RecuperacionVentaView.instance = new RecuperacionVentaView(vistaPrincipal, modal, recibo, recargar);
        }
        return RecuperacionVentaView.instance;
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
        rfid.setUrlImagen("rfid-rumbo.png");

        MedioIdentificacionBean ibutton = new MedioIdentificacionBean();
        ibutton.setId(IBUTTON_IDENTIFIER_ID);
        ibutton.setDescripcion("IBUTTON");
        ibutton.setNecesarioLector(true);
        ibutton.setUrlImagen("ibutton-rumbo.png");

        MedioIdentificacionBean card = new MedioIdentificacionBean();
        card.setId(CARD_IDENTIFIER_ID);
        card.setDescripcion("TARJETA");
        card.setNecesarioLector(false);
        card.setUrlImagen("card-rumbo.png");

        MedioIdentificacionBean numericCode = new MedioIdentificacionBean();
        numericCode.setId(NUMERIC_CODE_IDENTIFIER_ID);
        numericCode.setDescripcion("CODIGO <br/>NUM.");
        numericCode.setNecesarioLector(false);
        numericCode.setUrlImagen("numeric-code-rumbo.png");

        mediosIdentificadores.put(rfid.getId(), rfid);
        mediosIdentificadores.put(ibutton.getId(), ibutton);
        mediosIdentificadores.put(card.getId(), card);
        mediosIdentificadores.put(numericCode.getId(), numericCode);

    }

    public void handleIdentifierRequest(JsonObject identifierRequest) {
        String identifierDescription = identifierRequest.get("medio").getAsString();
        int identifierIdRequested = identifierDescription.equals("ibutton") ? IBUTTON_IDENTIFIER_ID : RFID_IDENTIFIER_ID;
        String identifierSerial1 = identifierRequest.get("serial").getAsString();
        if (getIdentifierMeanSelected() != null) {
            int selectedIdentifierId = getIdentifierMeanSelected().getId();
            if (identifierIdRequested != selectedIdentifierId) {
                showMessage("IDENTIFICADOR PRESENTADO ES DIFERENTE AL SELECCIONADO", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
            } else {
                this.setIdentifierSerial(identifierSerial1);
                this.solicitarAutorizacion(identifierRequest);
            }
        }
    }

    void cargaInformacionMangueras() {
        SetupDao sdao = new SetupDao();
        setInformacionMangueras(sdao.getMangueras());
    }

    void loadData() {
        this.cargarMediosIdentificadores();
        this.cargaInformacionMangueras();
    }

    void renderDurationTimeoutString(long timeout) {
        this.lbl_progress_duration.setText(
                ("<html>EL TIEMPO VENCERA... EN <br/><center>" + formatDurationTimeout(timeout) + "</center></html>")
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
            case PNL_CAR_DATA:
                return this.validationCarDataPanel();
            case PNL_IDENTIFIERS_SELECTOR:
                return this.validationIdentifiersSelector();
            case PNL_IDENTIFIER_LISTEN:
                return this.validationIdentifierListen();
            case PNL_ADITIONAL_DATA:
                return this.validationAditionalData();
        }
        return true;
    }

    boolean isAValidInteger(String numberString) {
        try {
            Integer.parseInt(numberString);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    boolean validateOdometer() {
        if (this.getOdometer() == null || this.getOdometer().trim().equals("")) {
            showMessage("DEBE INGRESAR ALGUN VALOR EN ODOMETRO", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
            return false;
        } else {
            if (!this.isAValidInteger(this.getOdometer().trim())) {
                showMessage("ODOMETRO DEBE SER UN ENTERO VALIDO", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                return false;
            } else {
                final long ODOMETER_MAX_THRESHOLD = 9999999999L;
                long odom = Long.parseLong(this.getOdometer().trim());
                if (odom > ODOMETER_MAX_THRESHOLD) {
                    showMessage("ODOMETRO DEBE SER MENOR A 9999999999", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
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
            showMessage("SERIAL DE LA TARJETA DEBE TENER MAS DE 4 CARACTERES", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
            return false;
        } else {
            if (this.getCardPass() == null || this.getCardPass().trim().equals("")) {
                showMessage("DEBE INGRESAR CLAVE TARJETA", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                return false;
            }
        }
        return true;
    }

    boolean validationNumericCodeIdentifier() {
        if (this.getIdentifierSerial().trim().length() <= 4) {
            showMessage("CODIGO DEBE TENER MAS DE 4 CARACTERES", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
            return false;
        }
        return true;
    }

    boolean validationAditionalDataCard() {
        if (requiereCodigoSeguridad) {
            String pin = new String(txt_pin.getPassword());
            if (pin.trim().length() == 0) {
                showMessage("PIN DEBE SER INGRESADO", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                return false;
            } else {
                if (!this.isAValidInteger(pin.trim())) {
                    showMessage("PIN SOLO DEBER TENER NUMEROS ENTEROS", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
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
                showMessage("LA INFORMACION ES REQUERIDA", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                return false;
            } else {
                if (licensePlate.trim().length() <= 3) {
                    showMessage("SE DEBE TENER MAS DE 3 CARACTERES", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                    return false;
                } else if (licensePlate.trim().length() > 17) {
                    showMessage("SE DEBE TENER MENOS DE 18 CARACTERES", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                    return false;
                }
            }
        }
        return true;
    }

    boolean validationAditionalData() {
        if (this.getIdentifierMeanSelected() != null) {

            switch (this.getIdentifierMeanSelected().getId()) {
                case RecuperacionVentaView.CARD_IDENTIFIER_ID:
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
                showMessage("NO SE TIENE EL IDENTIFICADOR DE AUTORIZACION", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                return false;
            }
            if (requiereInformacionAdicional && (txt_license_info1.getText().isEmpty() || txt_license_info1.getText().length() == 0)) {
                showMessage("SE REQUIERE INFORMACION ADICIONAL", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                return false;
            }
            this.fetchAditionalData();
        }
        return false;
    }

    JsonObject buildFetchAditionalDataRequest() {
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

        json.addProperty("identificadorAutorizacionEDS", this.getAuthorizationIdentifier() != null ? this.getAuthorizationIdentifier().trim() : "");
        json.add("validar", arr);
        return json;
    }

    void handleAditionalDataResponse(JsonObject response) {

        if (response != null) {
            NovusUtils.printLn("LA RESPUESTA ES DIFERENTE DE NULL >>> VALIDAR: " + response);
            if (response.get("error") == null) {
                this.setIsSaleAuthorized(true);
                this.nextStep(false);
            } else {
                showMessage("SE REQUIERE INFORMACION ADICIONAL", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
                showPanel(PNL_ADITIONAL_DATA);
            }
        } else {
            showMessage("OCURRIO UN ERROR EN EL ENVIO DE DATOS", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
            showPanel(PNL_ADITIONAL_DATA);
        }
    }

    void fetchAditionalData() {
        showMessage("ENVIANDO DATOS ADICIONALES", "/com/firefuel/resources/loader_fac.gif", true, this::mostrarMenuPrincipal, false);
        setTimeout(1, () -> {
            JsonObject resp = ClienteFacade.fetchAditionalData(buildRequest());
            mostrarMenuPrincipal();
            handleAditionalDataResponse(resp);
        });
    }

    boolean validationIdentifierListen() {
        if (this.getIdentifierSerial() == null) {
            showMessage("SERIAL NO SE HA PROPORCIONADO", "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, true);
            return false;
        }
        if (this.getIdentifierMeanSelected() != null) {
            switch (this.getIdentifierMeanSelected().getId()) {
                case RecuperacionVentaView.CARD_IDENTIFIER_ID:
                    if (!this.validationCardIdentifier()) {
                        return false;
                    }
                    break;
                case RecuperacionVentaView.NUMERIC_CODE_IDENTIFIER_ID:
                    if (!this.validationNumericCodeIdentifier()) {
                        return false;
                    }
                    break;
            }
        }
        return this.isSaleAuthorized();
    }

    void renderValueInput(JTextField input, String value) {
        input.setText(value);
    }

    public void listenSale(long hoseInSale, String saleAuthorizationIdentifier) {
        NovusUtils.printLn("MANGUERA= " + hoseInSale + ", AUTORIZACION=" + saleAuthorizationIdentifier + "," + "VENTA_AUTORIZADA=" + this.isSaleAuthorized());
        NovusUtils.printLn("MANGUERA= " + this.getSelectedHoseNumber());
        NovusUtils.printLn(Main.ANSI_GREEN + "isSaleAuthorized= " + this.isSaleAuthorized() + ", ");
        NovusUtils.printLn(Main.ANSI_GREEN + "stopTimeoutSale= " + !this.stopTimeoutSale + ", ");
        NovusUtils.printLn(Main.ANSI_GREEN + "stopTimeoutAditionalData= " + this.stopTimeoutAditionalData + ", ");
        NovusUtils.printLn(Main.ANSI_GREEN + "saleAuthorizationIdentifier===" + saleAuthorizationIdentifier);
        NovusUtils.printLn(Main.ANSI_GREEN + "saleAuthorizationIdentifier===" + this.getAuthorizationIdentifier() + Main.ANSI_RESET);
        if ((hoseInSale == this.getSelectedHoseNumber())
                && (this.isSaleAuthorized())
                && (!this.stopTimeoutSale)
                && (this.stopTimeoutAditionalData)
                && (saleAuthorizationIdentifier.equals(this.getAuthorizationIdentifier()))) {
            NovusUtils.printLn(Main.ANSI_GREEN + "SE MANDA A DETENER EL TIMEOUT, PORQUE SE RECIBE LA VENTA");
            this.stopTimeoutSale = false;
            showMessage("VENTA DETECTADA", "/com/firefuel/resources/loader_fac.gif", true, this::cerrar, false);
            setTimeout(2, () -> {
                mostrarMenuPrincipal();
            });
        }
    }

    int getHosesCount() {
        return this.getInformacionMangueras().size();
    }

    void listenIdentifiersClick(MedioIdentificacionBean identifierMeanSelected) {
        this.setIdentifierMeanSelected(identifierMeanSelected);
        this.nextStep(true);
    }

    void loadCarDataPanel() {
        this.txt_odometer.requestFocus();
        if (this.getOdometer() != null) {
            this.renderValueInput(this.txt_odometer, this.getOdometer());
        }
    }

    void renderIdentifierMeansItem() {
        this.pnl_identifiers.removeAll();
        if (RecuperacionVentaView.mediosIdentificadores != null) {
            int j = 0, i = 0;
            final int componentHeight = 252;
            final int componentWidth = 502;
            final int componentAvailableWidth = 620;
            final int componentAvailableHeight = 265;
            final int remaintSpaceWidth = componentAvailableWidth - componentWidth;
            final int remaintSpaceHeight = componentAvailableHeight - componentHeight;
            final int ncols = 2;
            for (Map.Entry<Integer, MedioIdentificacionBean> e : RecuperacionVentaView.mediosIdentificadores.entrySet()) {
                boolean enabled = false;
                if (e.getValue().getId() == 2) {
                    enabled = true;
                }
                MedioIdentificacionItem panel = new MedioIdentificacionItem(this, e.getValue());
                panel.setBounds((componentWidth * i) + ((remaintSpaceWidth * (i + 1)) / 2), (componentHeight * j) + ((remaintSpaceHeight * (j + 1)) / 2), componentWidth, componentHeight);
                j++;
                if (j == (ncols)) {
                    i++;
                    j = 0;
                }
                this.pnl_identifiers.add(panel);
            }
        }
    }

    JsonObject buildFetchSaleAutorizationRequest() {

        String serial = this.getIdentifierSerial();
        serial = serial.replaceAll("ñ", "");
        serial = serial.replaceAll("_", "");

        JsonObject request = new JsonObject();
        request.addProperty("surtidor", this.getSelectedPumpNumber());
        request.addProperty("cantidad", 0);
        request.addProperty("monto", 0);
        request.addProperty("numeroCara", this.getSelectedFaceNumber());
        request.addProperty("valorOdometro", this.getOdometer());
        request.addProperty("codigoFamiliaProducto", this.getSelectedProductFamilyId());
        request.addProperty("precioVentaUnidad", this.getSelectedProductPrice());
        request.addProperty("codigoSeguridad", this.getCardPass());
        request.addProperty("codigoTipoIdentificador", this.getIdentifierMeanSelected().getId());
        request.addProperty("serialIdentificador", serial);
        request.addProperty("identificadorPromotor", Main.persona.getId());

        return request;
    }

    private JsonObject buildRequest() {
        JsonObject request = new JsonObject();
        request.addProperty("recuperar", "S");
        request.addProperty("identificador", jTextField1.getText());
        request.addProperty("identificadorEstacion", Main.credencial.getEmpresas_id());
        request.addProperty("tipoIdentificador", "IDROOM");
        request.addProperty("fechaConsulta", sdf.format(new Date()));
        request.addProperty("identificadorPromotor", Main.persona.getId());
        request.addProperty("identificadorCara", Integer.parseInt(recibo.getCara()));

        return request;
    }

    void solicitarAutorizacion(JsonObject identifierRequest) {
        PanelNotificacion panelLoader = showMessage("SOLICITANDO AUTORIZACION", "/com/firefuel/resources/loader_fac.gif", false, null, false);
        this.taskRunner(() -> {
            PrinterFacade facade = new PrinterFacade();
            JsonObject request = new JsonObject();
            request.addProperty("recuperar", "S");
            request.addProperty("identificador", identifierRequest.get("serial").getAsString());
            request.addProperty("identificadorEstacion", Main.credencial.getEmpresas_id());
            request.addProperty("tipoIdentificador", "IDROOM");
            request.addProperty("fechaConsulta", sdf.format(new Date()));
            request.addProperty("identificadorPromotor", Main.persona.getId());
            request.addProperty("identificadorCara", Integer.parseInt(recibo.getCara()));
            JsonObject resp = ClienteFacade.consultaValidacionVenta(request);
            boolean comunidades = hayClienteComunidades(resp);
            try {
                if (resp != null && resp.get("data") != null && !resp.get("data").isJsonNull() && !comunidades) {
                    JsonArray familias = resp.get("data").getAsJsonObject().get("familias").getAsJsonArray();
                    String familiaProducto = String.valueOf(surtdao.getFamiliaProducto(recibo.getIdentificacionProducto()));
                    double saldo = resp.get("data").getAsJsonObject().get("saldo").getAsDouble();
                    String tipoCupo = resp.get("data").getAsJsonObject().get("tipoCupo").getAsString();
                    if (saldo >= recibo.getTotal()) {
                        if (containsFamily(familiaProducto, familias)) {
                            isCredito = true;
                            response = resp;

                            recepcionData = resp.get("data").getAsJsonObject();
                            recepcionData.addProperty("medioAutorizacion", "ibutton");
                            recepcionData.addProperty("serialDispositivo", identifierRequest.get("serial").getAsString());
                            jLabel5.setText("<html>" + recepcionData.get("nombreCliente").getAsString() + "<br/>SALDO: $ " + df.format(saldo) + "</html>");
                            panelLoader.cerrar();
                            panelLoader.setVisible(false);
                            pnlPrincipal.getLayout().removeLayoutComponent(panelLoader);
                            showPanel(PNL_SALE);
                        } else {
                            String cliente = resp.get("data").getAsJsonObject().get("nombreCliente").getAsString().toUpperCase();
                            String mensaje = cliente + "\r\nCHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "PRODUCTO NO DISPONIBLE PARA ESTE CLIENTE";
                            lbl_identifier_name.setText("<html><center><b>" + cliente + "</b><br><font color='red'>PRODUCTO NO DISPONIBLE PARA ESTE CLIENTE</center><font></html>");
                            showMessage("<html><center><b>" + cliente + "</b><br><font color='red'>PRODUCTO NO DISPONIBLE PARA ESTE CLIENTE</center><font></html>", "/com/firefuel/resources/btBad.png", true, RecuperacionVentaView.this::cerrar, true);
                            facade.printFormato(mensaje);
                        }
                    } else {
                        String cliente = resp.get("data").getAsJsonObject().get("nombreCliente").getAsString().toUpperCase();
                        String mensaje = cliente + "\r\nCHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + " NO CUENTA CON SALDO DISPONIBLE ";
                        lbl_identifier_name.setText("<html><center><b>" + cliente + "</b><br><font color='red'>NO CUENTA CON SALDO DISPONIBLE $" + df.format(saldo) + " T:" + tipoCupo + "</center><font></html>");
                        showMessage("<html><center><b>" + cliente + "</b><br><font color='red'>NO CUENTA CON SALDO DISPONIBLE $" + df.format(saldo) + " T:" + tipoCupo + "</center><font></html>", "/com/firefuel/resources/btBad.png", true, RecuperacionVentaView.this::cerrar, true);
                        facade.printFormato(mensaje);
                    }
                } else {
                    NovusUtils.beep();
                    if (comunidades) {
                        showMessage("<html><center>ESTA VENTA NO PUEDE SER RECUPERADA COMO CLIENTE CONTADO CON DESCUENTO</center></html>", "/com/firefuel/resources/btBad.png", true, RecuperacionVentaView.this::cerrar, true);
                    } else {
                        mensaje(resp, identifierRequest);
                    }

                }
            } catch (Exception e) {
                NovusUtils.printLn(e + "");
            }
        });
    }

    private void mensaje(JsonObject resp, JsonObject identifierRequest) {
    PrinterFacade facade = new PrinterFacade();
        if (resp != null) {
            if (resp.get("mensajeError") != null && !resp.get("mensajeError").isJsonNull()) {
                lbl_identifier_name.setText("<html><center>" + resp.get("mensajeError").getAsString() + "</center></html>");
                facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + resp.get("mensajeError").getAsString());
                showMessage("<html><center>" + resp.get("mensajeError").getAsString() + "</center></html>", "/com/firefuel/resources/btBad.png", true, RecuperacionVentaView.this::cerrar, true);
            } else {
                lbl_identifier_name.setText("<html><center>REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACION</center></html>");
                showMessage("<html><center>REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACION</center></html>", "/com/firefuel/resources/btBad.png", true, RecuperacionVentaView.this::cerrar, true);
                facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACI�N");
            }
        } else {
            facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "REINTENTE COLOCAR EL CHIP, SIN COMUNICACI�N EN EL SERVER");
            showMessage("<html><center>REINTENTE COLOCAR EL CHIP, SIN COMUNICACI�N EN EL SERVER</center></html>", "/com/firefuel/resources/btBad.png", true, RecuperacionVentaView.this::cerrar, true);
            lbl_identifier_name.setText("<html><center>REINTENTE COLOCAR EL CHIP, SIN COMUNICACI�N EN EL SERVER</center></html>");
        }
    }

    private boolean hayClienteComunidades(JsonObject response) {
        if (response != null) {
            if (response.get("data") != null && !response.get("data").isJsonNull()) {
                response = response.get("data").getAsJsonObject();
                return response.has("es_comunidades") && response.get("es_comunidades").getAsBoolean();
            } else {
                return response.has("es_comunidades") && response.get("es_comunidades").getAsBoolean();
            }
        } else {
            return false;
        }
    }

    private boolean containsFamily(String id_familia, JsonArray arr) {
        boolean result = false;
        for (JsonElement jsonElement : arr) {
            JsonObject obj = (JsonObject) jsonElement;
            if (obj.get("identificador_familia_abajo").getAsString().equals(id_familia)) {
                result = true;
                break;
            }
        }
        return result;
    }

    boolean requiereInformacionAdicional;
    boolean requierePlacaVehiculo;
    boolean requiereCodigoSeguridad;
    SurtidorDao surtdao = new SurtidorDao();

    void handleSaleAuthorizationResponse(JsonObject response) {

        NovusUtils.printLn("RESPUESTA DE LA SOLICITUD 1 >> " + response);
        final String DEFINED_CODE_ERROR_MESSAGE = "40000";
        if (response != null) {
            NovusUtils.printLn("RESPUESTA DE LA SOLICITUD 2 >> " + response.toString());
            if (response.get("error") == null) {

                JsonObject dataJson = response.get("data").getAsJsonObject();
                JsonObject aditionalDataJson = dataJson.get("datosAdicionales").getAsJsonObject();
                JsonObject timeoutsJson = dataJson.get("timeout").getAsJsonObject();

                requiereInformacionAdicional = aditionalDataJson.get("requiereInformacionAdicional") != null && !aditionalDataJson.get("requiereInformacionAdicional").isJsonNull() && aditionalDataJson.get("requiereInformacionAdicional").getAsBoolean();
                requierePlacaVehiculo = aditionalDataJson.get("requierePlacaVehiculo") != null && !aditionalDataJson.get("requierePlacaVehiculo").isJsonNull() && aditionalDataJson.get("requierePlacaVehiculo").getAsBoolean();
                requiereCodigoSeguridad = false;

                if (aditionalDataJson.get("codigoSeguridad") != null && !aditionalDataJson.get("codigoSeguridad").isJsonNull() && !aditionalDataJson.get("codigoSeguridad").getAsString().trim().equals("")) {
                    requiereCodigoSeguridad = true;
                }

                this.setAditionalDataTimeout(timeoutsJson.get("additionalData").getAsLong() * 1000);
                this.setSaleAuthorizationTimeout(timeoutsJson.get("authorization").getAsLong() * 1000);
                this.setPumpUnlockingTimeout(timeoutsJson.get("pumpUnlocking").getAsLong() * 1000);
                this.setAuthorizationIdentifier(dataJson.get("identificadorAutorizacionEDS").getAsString().trim());
                // this.jrPlaca.setText(dataJson.get("placaVehiculo").getAsString());

                if (requiereInformacionAdicional || requierePlacaVehiculo || requiereCodigoSeguridad) {

                    txt_license_plate.setEnabled(requierePlacaVehiculo);
                    if (requierePlacaVehiculo) {
                        txt_license_plate.requestFocus();
                        txt_license_plate.setBackground(new java.awt.Color(186, 12, 47));
                    } else {
                        txt_license_plate.setBackground(Color.GRAY);
                    }

                    txt_pin.setEnabled(requiereCodigoSeguridad);
                    if (requiereCodigoSeguridad) {
                        txt_pin.requestFocus();
                        txt_pin.setBackground(new java.awt.Color(186, 12, 47));
                    } else {
                        jLabel7.setVisible(false);
                        txt_pin.setVisible(false);
                        txt_pin.setBackground(Color.GRAY);
                    }

                    txt_license_info1.setEnabled(requiereInformacionAdicional);
                    if (requiereInformacionAdicional) {
                        txt_license_info1.requestFocus();
                        txt_license_info1.setBackground(new java.awt.Color(186, 12, 47));
                    } else {
                        txt_license_info1.setBackground(Color.GRAY);
                    }

                    this.nextStep(false);
                    showPanel(PNL_ADITIONAL_DATA);

                } else {
                    this.setIsSaleAuthorized(true);
                    this.nextStep(false);
                    setTimeout(1, () -> {
                        this.nextStep(false);
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
                    showMessage(messageError.toUpperCase(), "/com/firefuel/resources/btBad.png", true, this::cerrar, true);
                } else {
                    showMessage("ERROR, VENTA NO PUDO SER AUTORIZADA (" + messageError + ")", "/com/firefuel/resources/btBad.png", true, this::cerrar, true);
                }
            }
        } else {
            showMessage("OCURRIO UN ERROR EN LA SOLICITUD", "/com/firefuel/resources/btBad.png", true, this::cerrar, true);
        }
    }

    void loadReaderIdentifierPanel() {
        CardLayout layout = (CardLayout) this.pnl_identifier_listen.getLayout();
        layout.show(this.pnl_identifier_listen, "pnl_identifier_reader");
        if (this.getIdentifierMeanSelected() != null) {
            try {
                this.lbl_identifier_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/" + this.getIdentifierMeanSelected().getUrlImagen()))); // NOI18N
            } catch (Exception e) {
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
            if (identifierSelectedId == CARD_IDENTIFIER_ID) {
                this.txt_serial_card.setText("");
                layoutIdentifierInputsPanel.show(this.pnl_identifier_inputs, "pnl_card_input");
                this.txt_serial_card.requestFocus();
            } else if (identifierSelectedId == NUMERIC_CODE_IDENTIFIER_ID) {
                layoutIdentifierInputsPanel.show(this.pnl_identifier_inputs, "pnl_numeric_code_input");
                this.txt_numeric_code_serial.setText("");
                this.txt_numeric_code_serial.requestFocus();
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
                    progressPanel.setBounds(720, 15, 535, 195);
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
        float durationInHours = (float) milliseconds / 3600000;
        if (durationInHours < 1) {
            if (milliseconds < 60000) {
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
        this.stopTimeoutAditionalData = false;
        if (this.getIdentifierMeanSelected() != null) {
            CardLayout layoutAditionalDataPanel = (CardLayout) this.pnl_aditional_data.getLayout();
            this.resetProgressBar();

            if (!requierePlacaVehiculo && !requiereInformacionAdicional && requiereCodigoSeguridad) {
                txt_pin.setText(new String(txt_pass_card.getPassword()));
                // continuarProceso();

                this.stopTimeoutAditionalData = true;
                this.setIsSaleAuthorized(true);
                this.nextStep(true);
                loadSalePanel();
                showPanel(PNL_SALE);
                setTimeout(1, () -> {
                    showPanel(PNL_SALE);
                });
            } else if (requierePlacaVehiculo || requiereInformacionAdicional || requiereCodigoSeguridad) {
                layoutAditionalDataPanel.show(this.pnl_aditional_data, "pnl_aditional_data_license_plate");
                this.renderTimeoutPanels(this.pnl_aditional_data_license_plate, false, 1);
                this.txt_license_plate.requestFocus();
            }
            //
            // layoutAditionalDataPanel.show(this.pnl_aditional_data,
            // "pnl_aditional_data_card");
            // this.renderTimeoutPanels(this.pnl_aditional_data_card, false, 0);
            // this.txt_pin.requestFocus();

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
        showMessage("TIEMPO EXPIRADO", "/com/firefuel/resources/btBad.png", true, this::cerrar, true);
    }

    void loadIdentifierListenPanel() {
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
    }

    void controlPanelRendered(int index) {
        switch (this.getPanelName(index)) {
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
                this.loadSalePanel();
                break;
        }
    }

    void loadSalePanel() {
        NovusUtils.printLn("SALE PANEL");
        this.resetProgressBar();
        this.renderTimeoutPanels(this.pnl_sale, true, 0);
        this.stopTimeoutSale = false;
        this.initTimeoutSale(0, this.getSaleAuthorizationTimeout());
        this.toggleSteppersButtons(this.btn_back, false);
    }

    void handleStepper(int index, boolean validate) {
        if (index > this.index && validate) {
            if (!isValidDataStep(this.index)) {
                return;
            }
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

    private void init() {
        jTextField1.setText("");
        this.loadData();
        this.loadView();
        thread = new Thread(this);
        InfoViewController.NotificadorRecuperacion = new Notificador() {
            @Override
            public void send(JsonObject data) {
                handleIdentifierRequest(data);
            }
        };
    }

    @SuppressWarnings("unchecked")
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
        pnlPrincipal = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btn_back = new javax.swing.JLabel();
        btn_next = new javax.swing.JLabel();
        pnl_layout_container = new javax.swing.JPanel();
        pnl_car_data = new javax.swing.JPanel();
        txt_odometer = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pnl_keyboard = new TecladoNumerico();
        background_card_data = new javax.swing.JLabel();
        pnl_identifiers_selector = new javax.swing.JPanel();
        pnl_identifiers = new javax.swing.JPanel();
        pnl_identifier_listen = new javax.swing.JPanel();
        pnl_identifier_reader = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbl_identifier_image = new javax.swing.JLabel();
        lbl_identifier_name = new javax.swing.JLabel();
        pnl_identifier_inputs = new javax.swing.JPanel();
        pnl_card_input = new javax.swing.JPanel();
        pnl_keyboard1 = new TecladoNumerico();
        txt_pass_card = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_serial_card = new javax.swing.JPasswordField();
        jLabel15 = new javax.swing.JLabel();
        pnl_numeric_code_input = new javax.swing.JPanel();
        txt_numeric_code_serial = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pnl_keyboard2 = new TecladoNumerico();
        background_numeric_code = new javax.swing.JLabel();
        pnl_aditional_data = new javax.swing.JPanel();
        pnl_aditional_data_card = new javax.swing.JPanel();
        pnl_keyboard3 = new TecladoNumerico();
        background_numeric_code1 = new javax.swing.JLabel();
        pnl_aditional_data_license_plate = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_pin = new javax.swing.JPasswordField();
        txt_license_plate = new javax.swing.JTextField();
        jPanel2 = new TecladoExtendido();
        jLabel16 = new javax.swing.JLabel();
        txt_license_info1 = new javax.swing.JTextField();
        pnl_sale = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblProducto = new javax.swing.JLabel();
        lblManguera = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblPrecioProducto = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        lblVolumen = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        lblPlaca = new javax.swing.JLabel();
        jprogress = new javax.swing.JProgressBar();
        jTextField1 = new javax.swing.JTextField();
        jNotificacion = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        title_view = new javax.swing.JLabel();
        jbackground = new javax.swing.JLabel();
        pnl_confirmacion = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        NO = new javax.swing.JLabel();
        SI1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();

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

        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));
        jPanel1.setLayout(null);

        btn_back.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_back.setForeground(new java.awt.Color(255, 255, 255));
        btn_back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_back.setText("ANTERIOR");
        btn_back.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_backMousePressed(evt);
            }
        });
        jPanel1.add(btn_back);
        btn_back.setBounds(710, 725, 173, 54);

        btn_next.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_next.setForeground(new java.awt.Color(255, 255, 255));
        btn_next.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_next.setText("SIGUIENTE");
        btn_next.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_nextMouseReleased(evt);
            }
        });
        jPanel1.add(btn_next);
        btn_next.setBounds(920, 725, 173, 54);

        pnl_layout_container.setOpaque(false);
        pnl_layout_container.setLayout(new java.awt.CardLayout());

        pnl_car_data.setName("pnl_car_data"); // NOI18N
        pnl_car_data.setOpaque(false);
        pnl_car_data.setLayout(null);

        txt_odometer.setBackground(new java.awt.Color(186, 12, 47));
        txt_odometer.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_odometer.setForeground(new java.awt.Color(255, 255, 255));
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
        txt_odometer.setBounds(110, 190, 480, 60);

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("INGRESE KILOMETRAJE:");
        pnl_car_data.add(jLabel1);
        jLabel1.setBounds(90, 120, 520, 50);
        pnl_car_data.add(pnl_keyboard);
        pnl_keyboard.setBounds(670, 60, 570, 470);

        background_card_data.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndCardDataRumbo.png"))); // NOI18N
        pnl_car_data.add(background_card_data);
        background_card_data.setBounds(0, 0, 1280, 590);

        pnl_layout_container.add(pnl_car_data, "pnl_car_data");
        pnl_car_data.getAccessibleContext().setAccessibleName("pnl_car_data");

        pnl_identifiers_selector.setName("pnl_identifiers_selector"); // NOI18N
        pnl_identifiers_selector.setOpaque(false);
        pnl_identifiers_selector.setLayout(null);

        pnl_identifiers.setOpaque(false);
        pnl_identifiers.setLayout(null);
        pnl_identifiers_selector.add(pnl_identifiers);
        pnl_identifiers.setBounds(20, 40, 1240, 530);

        pnl_layout_container.add(pnl_identifiers_selector, "pnl_identifiers_selector");
        pnl_identifiers_selector.getAccessibleContext().setAccessibleName("pnl_identifiers_selector");

        pnl_identifier_listen.setName("pnl_identifier_listen"); // NOI18N
        pnl_identifier_listen.setOpaque(false);
        pnl_identifier_listen.setLayout(new java.awt.CardLayout());

        pnl_identifier_reader.setName("pnl_identifier_reader"); // NOI18N
        pnl_identifier_reader.setOpaque(false);
        pnl_identifier_reader.setLayout(null);

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

        lbl_identifier_name.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lbl_identifier_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnl_identifier_reader.add(lbl_identifier_name);
        lbl_identifier_name.setBounds(100, 450, 1080, 130);

        pnl_identifier_listen.add(pnl_identifier_reader, "pnl_identifier_reader");
        pnl_identifier_reader.getAccessibleContext().setAccessibleName("pnl_identifier_reader");

        pnl_identifier_inputs.setName("pnl_identifier_inputs"); // NOI18N
        pnl_identifier_inputs.setOpaque(false);
        pnl_identifier_inputs.setLayout(new java.awt.CardLayout());

        pnl_card_input.setName("pnl_card_input"); // NOI18N
        pnl_card_input.setOpaque(false);
        pnl_card_input.setLayout(null);
        pnl_card_input.add(pnl_keyboard1);
        pnl_keyboard1.setBounds(680, 60, 570, 470);

        txt_pass_card.setBackground(new java.awt.Color(186, 12, 47));
        txt_pass_card.setFont(new java.awt.Font("Arial", 1, 55)); // NOI18N
        txt_pass_card.setForeground(new java.awt.Color(255, 255, 255));
        txt_pass_card.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_pass_card.setBorder(null);
        txt_pass_card.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_pass_cardKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pass_cardKeyTyped(evt);
            }
        });
        pnl_card_input.add(txt_pass_card);
        txt_pass_card.setBounds(40, 290, 300, 70);

        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("INGRESE CLAVE:");
        pnl_card_input.add(jLabel5);
        jLabel5.setBounds(40, 230, 450, 50);

        jLabel4.setBackground(new java.awt.Color(51, 51, 51));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("PRESENTE LA TARJETA:");
        pnl_card_input.add(jLabel4);
        jLabel4.setBounds(40, 60, 450, 50);

        txt_serial_card.setBackground(new java.awt.Color(186, 12, 47));
        txt_serial_card.setFont(new java.awt.Font("Arial", 0, 55)); // NOI18N
        txt_serial_card.setForeground(new java.awt.Color(255, 255, 255));
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
        pnl_card_input.add(txt_serial_card);
        txt_serial_card.setBounds(40, 120, 600, 80);

        jLabel15.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel15.setText("LIMPIAR");
        jLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel15MouseReleased(evt);
            }
        });
        pnl_card_input.add(jLabel15);
        jLabel15.setBounds(50, 380, 180, 70);

        pnl_identifier_inputs.add(pnl_card_input, "pnl_card_input");
        pnl_card_input.getAccessibleContext().setAccessibleName("pnl_card_input");

        pnl_numeric_code_input.setName("pnl_numeric_code_input"); // NOI18N
        pnl_numeric_code_input.setOpaque(false);
        pnl_numeric_code_input.setLayout(null);

        txt_numeric_code_serial.setBackground(new java.awt.Color(186, 12, 47));
        txt_numeric_code_serial.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_numeric_code_serial.setForeground(new java.awt.Color(255, 255, 255));
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
        txt_numeric_code_serial.setBounds(80, 120, 510, 70);

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("INGRESE CODIGO:");
        pnl_numeric_code_input.add(jLabel6);
        jLabel6.setBounds(80, 60, 450, 50);
        pnl_numeric_code_input.add(pnl_keyboard2);
        pnl_keyboard2.setBounds(680, 30, 570, 470);
        pnl_numeric_code_input.add(background_numeric_code);
        background_numeric_code.setBounds(0, 0, 1280, 530);

        pnl_identifier_inputs.add(pnl_numeric_code_input, "pnl_numeric_code_input");
        pnl_numeric_code_input.getAccessibleContext().setAccessibleName("pnl_numeric_code_input");

        pnl_identifier_listen.add(pnl_identifier_inputs, "pnl_identifier_inputs");
        pnl_identifier_inputs.getAccessibleContext().setAccessibleName("pnl_identifier_inputs");

        pnl_layout_container.add(pnl_identifier_listen, "pnl_identifier_listen");
        pnl_identifier_listen.getAccessibleContext().setAccessibleName("pnl_identifier_listen");

        pnl_aditional_data.setName("pnl_aditional_data"); // NOI18N
        pnl_aditional_data.setOpaque(false);
        pnl_aditional_data.setLayout(new java.awt.CardLayout());

        pnl_aditional_data_card.setName("pnl_aditional_data_card"); // NOI18N
        pnl_aditional_data_card.setOpaque(false);
        pnl_aditional_data_card.setPreferredSize(new java.awt.Dimension(1280, 530));
        pnl_aditional_data_card.setLayout(null);
        pnl_aditional_data_card.add(pnl_keyboard3);
        pnl_keyboard3.setBounds(670, 60, 570, 470);
        pnl_aditional_data_card.add(background_numeric_code1);
        background_numeric_code1.setBounds(0, 0, 1280, 530);

        pnl_aditional_data.add(pnl_aditional_data_card, "card2");
        pnl_aditional_data_card.getAccessibleContext().setAccessibleName("pnl_aditional_data_card");

        pnl_aditional_data_license_plate.setName("pnl_aditional_data_license_plate"); // NOI18N
        pnl_aditional_data_license_plate.setOpaque(false);
        pnl_aditional_data_license_plate.setLayout(null);

        jLabel8.setBackground(new java.awt.Color(51, 51, 51));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("PLACA");
        pnl_aditional_data_license_plate.add(jLabel8);
        jLabel8.setBounds(120, 10, 140, 40);

        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setText("PIN:");
        pnl_aditional_data_license_plate.add(jLabel7);
        jLabel7.setBounds(460, 10, 110, 40);

        txt_pin.setBackground(new java.awt.Color(186, 12, 47));
        txt_pin.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_pin.setForeground(new java.awt.Color(255, 255, 255));
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
        txt_pin.setBounds(460, 50, 300, 70);

        txt_license_plate.setBackground(new java.awt.Color(186, 12, 47));
        txt_license_plate.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_license_plate.setForeground(new java.awt.Color(255, 255, 255));
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
        txt_license_plate.setBounds(120, 50, 310, 70);
        txt_license_plate.getAccessibleContext().setAccessibleName("txt_license_plate");

        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel2MouseReleased(evt);
            }
        });
        pnl_aditional_data_license_plate.add(jPanel2);
        jPanel2.setBounds(130, 260, 1024, 336);

        jLabel16.setBackground(new java.awt.Color(51, 51, 51));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("<html>INFORMACION </br> ADICIONAL</html>");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_aditional_data_license_plate.add(jLabel16);
        jLabel16.setBounds(120, 150, 220, 70);

        txt_license_info1.setBackground(new java.awt.Color(186, 12, 47));
        txt_license_info1.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_license_info1.setForeground(new java.awt.Color(255, 255, 255));
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
        txt_license_info1.setBounds(370, 150, 390, 70);

        pnl_aditional_data.add(pnl_aditional_data_license_plate, "pnl_aditional_data_license_plate");
        pnl_aditional_data_license_plate.getAccessibleContext().setAccessibleName("pnl_aditional_data_license_plate");

        pnl_layout_container.add(pnl_aditional_data, "pnl_aditional_data");
        pnl_aditional_data.getAccessibleContext().setAccessibleName("pnl_aditional_data");

        pnl_sale.setName("pnl_sale"); // NOI18N
        pnl_sale.setOpaque(false);
        pnl_sale.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("RECUPERANDO VENTA");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_sale.add(jLabel3);
        jLabel3.setBounds(290, 30, 700, 70);

        lblProducto.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProducto.setText("GASOLINA CORRIENTE 10% OXIGENADA");
        pnl_sale.add(lblProducto);
        lblProducto.setBounds(290, 260, 530, 60);

        lblManguera.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblManguera.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblManguera.setText("0");
        pnl_sale.add(lblManguera);
        lblManguera.setBounds(130, 260, 110, 100);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 40)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/manguera_recuperacion_venta.png"))); // NOI18N
        pnl_sale.add(jLabel17);
        jLabel17.setBounds(130, 260, 110, 100);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(186, 12, 47));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("MANGUERA");
        pnl_sale.add(jLabel18);
        jLabel18.setBounds(130, 210, 110, 30);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("PRODUCTO");
        pnl_sale.add(jLabel19);
        jLabel19.setBounds(290, 210, 530, 30);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(186, 12, 47));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("FECHA");
        pnl_sale.add(jLabel20);
        jLabel20.setBounds(850, 210, 310, 30);

        lblFecha.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblFecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFecha.setText("01-01-2022 00:00");
        pnl_sale.add(lblFecha);
        lblFecha.setBounds(850, 260, 310, 60);

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("PRECIO");
        pnl_sale.add(jLabel22);
        jLabel22.setBounds(110, 420, 200, 30);

        lblPrecioProducto.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblPrecioProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPrecioProducto.setText("$ 20.000");
        pnl_sale.add(lblPrecioProducto);
        lblPrecioProducto.setBounds(110, 470, 200, 60);

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(186, 12, 47));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("VOLUMEN");
        pnl_sale.add(jLabel24);
        jLabel24.setBounds(360, 420, 210, 30);

        lblVolumen.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblVolumen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVolumen.setText("2.0 GL");
        pnl_sale.add(lblVolumen);
        lblVolumen.setBounds(360, 470, 210, 60);

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(186, 12, 47));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("TOTAL");
        pnl_sale.add(jLabel28);
        jLabel28.setBounds(630, 420, 280, 30);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("$ 40.000");
        pnl_sale.add(lblTotal);
        lblTotal.setBounds(630, 470, 280, 60);

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(186, 12, 47));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("PLACA");
        pnl_sale.add(jLabel30);
        jLabel30.setBounds(980, 420, 190, 30);

        lblPlaca.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        lblPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPlaca.setText("ABC123");
        pnl_sale.add(lblPlaca);
        lblPlaca.setBounds(980, 470, 190, 60);

        jprogress.setBackground(new java.awt.Color(186, 12, 47));
        jprogress.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jprogress.setForeground(new java.awt.Color(255, 182, 0));
        jprogress.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jprogress.setMaximumSize(new java.awt.Dimension(560, 40));
        jprogress.setMinimumSize(new java.awt.Dimension(560, 40));
        jprogress.setOpaque(true);
        jprogress.setPreferredSize(new java.awt.Dimension(560, 40));
        pnl_sale.add(jprogress);
        jprogress.setBounds(290, 120, 700, 30);

        pnl_layout_container.add(pnl_sale, "pnl_sale");

        jPanel1.add(pnl_layout_container);
        pnl_layout_container.setBounds(0, 95, 1280, 600);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1);
        jTextField1.setBounds(1010, 20, 240, 40);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jNotificacion);
        jNotificacion.setBounds(130, 720, 530, 70);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel25);
        jLabel25.setBounds(1115, 710, 10, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel1.add(logo);
        logo.setBounds(10, 700, 110, 100);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 71);

        title_view.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        title_view.setForeground(new java.awt.Color(255, 255, 255));
        title_view.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        title_view.setText("RECUPERACION DE VENTA");
        title_view.setToolTipText("");
        jPanel1.add(title_view);
        title_view.setBounds(110, 10, 550, 60);

        jbackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jbackground.setText("e.");
        jPanel1.add(jbackground);
        jbackground.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel1, "pnl_principal");

        pnl_confirmacion.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel9);
        jLabel9.setBounds(307, 250, 690, 180);

        NO.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        NO.setForeground(new java.awt.Color(255, 255, 255));
        NO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        NO.setText("NO");
        NO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NOMouseClicked(evt);
            }
        });
        pnl_confirmacion.add(NO);
        NO.setBounds(330, 480, 264, 54);

        SI1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SI1.setForeground(new java.awt.Color(255, 255, 255));
        SI1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SI1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        SI1.setText("SI");
        SI1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SI1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SI1MouseClicked(evt);
            }
        });
        pnl_confirmacion.add(SI1);
        SI1.setBounds(700, 480, 264, 54);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/card.png"))); // NOI18N
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel11);
        jLabel11.setBounds(250, 210, 800, 360);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        fnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fndMousePressed(evt);
            }
        });
        pnl_confirmacion.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnl_confirmacion, "pnl_confirmacion");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_odometerKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_odometerKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_odometer, 9, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_odometerKeyTyped

    private void txt_serial_cardFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_serial_cardFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_serial_card);
    }//GEN-LAST:event_txt_serial_cardFocusGained

    private void txt_pass_cardKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pass_cardKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_pass_card, 4, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_pass_cardKeyTyped

    private void txt_numeric_code_serialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_numeric_code_serialKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_numeric_code_serial, 15, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_numeric_code_serialKeyTyped

    private void txt_license_plateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_license_plateKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, txt_license_plate, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_license_plateKeyTyped

    private void txt_pinKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pinKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_pin, 6, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_pinKeyTyped

    private void txt_license_info1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_license_info1KeyTyped
        String caracteresAceptados = "[0-9a-zA-Z. ]";
        NovusUtils.limitarCarateres(evt, txt_license_info1, 50, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_license_info1KeyTyped

    private void txt_license_plateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_license_plateFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_license_plate);
    }//GEN-LAST:event_txt_license_plateFocusGained

    private void txt_license_info1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_license_info1FocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_license_info1);
    }//GEN-LAST:event_txt_license_info1FocusGained

    private void txt_odometerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_odometerFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_odometer);
    }//GEN-LAST:event_txt_odometerFocusGained

    private void txt_numeric_code_serialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_numeric_code_serialFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_numeric_code_serial);
    }//GEN-LAST:event_txt_numeric_code_serialFocusGained

    private void NOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NOMouseClicked
        mostrarMenuPrincipal();
    }//GEN-LAST:event_NOMouseClicked

    private void SI1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SI1MouseClicked
        handler.run();
    }//GEN-LAST:event_SI1MouseClicked

    private void fndMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fndMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fndMousePressed

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        showPanelP("pnl_confirmacion");
        jLabel9.setText("<html><center>¿DESEA SALIR? </center></html>");
        handler = () -> {
            this.cerrar();
        };
    }// GEN-LAST:event_jLabel13MouseReleased

    void nextStep(boolean validation) {
        this.handleStepper(this.index + 1, validation);
    }

    void previousStep(boolean validation) {
        this.handleStepper(this.index - 1, validation);
    }

    private void showPanelP(String panel) {        
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

    private void btn_backMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_backMousePressed
        if (this.btn_back.isEnabled()) {
            NovusUtils.beep();
            this.previousStep(true);
        }
    }// GEN-LAST:event_btn_backMousePressed

    // void handleControlSlider(int order) {
    // CardLayout layout = (CardLayout) this.pnl_hoses_carousel.getLayout();
    // if (order >= 0) {
    // layout.next(this.pnl_hoses_carousel);
    // } else {
    // layout.previous(this.pnl_hoses_carousel);
    // }
    // }
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
        continuarProceso();
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
        String pass = new String(this.txt_pin.getPassword());
    }// GEN-LAST:event_txt_pinKeyReleased

    private void txt_license_plateComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_txt_license_plateComponentShown
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_license_plateComponentShown

    private void txt_license_plateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_license_plateActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_license_plateActionPerformed

    private void txt_license_plateKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_license_plateKeyReleased

    }// GEN-LAST:event_txt_license_plateKeyReleased

    private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jPanel2MouseReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_jPanel2MouseReleased

    private void btn_nextMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_nextMouseReleased
        continuarProceso();
    }// GEN-LAST:event_btn_nextMouseReleased

    private void txt_serial_cardActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_serial_cardActionPerformed
        limpiarFormato();
    }// GEN-LAST:event_txt_serial_cardActionPerformed

    private void jLabel15MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel15MouseReleased
        limpiarTarjeta();
    }// GEN-LAST:event_jLabel15MouseReleased

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField1ActionPerformed
        testIbutton();
    }// GEN-LAST:event_jTextField1ActionPerformed

    private void txt_license_info1KeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_license_info1KeyReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_license_info1KeyReleased

    private void txt_license_info1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_license_info1ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_license_info1ActionPerformed

    private void txt_license_info1ComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_txt_license_info1ComponentShown
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_license_info1ComponentShown

    private void txt_pinActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_pinActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txt_pinActionPerformed

    private void txt_pinFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_pinFocusGained
        desactivarTeclado();
        NovusUtils.deshabilitarCopiarPegar(txt_pin);
    }// GEN-LAST:event_txt_pinFocusGained

    private void txt_pinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txt_pinFocusLost
        activarTeclado();
    }// GEN-LAST:event_txt_pinFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NO;
    private javax.swing.JLabel SI1;
    private javax.swing.JLabel background_card_data;
    private javax.swing.JLabel background_numeric_code;
    private javax.swing.JLabel background_numeric_code1;
    private javax.swing.JLabel background_timeout;
    private javax.swing.JLabel btn_back;
    private javax.swing.ButtonGroup btn_group_identifier_type;
    private javax.swing.JLabel btn_next;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jbackground;
    private javax.swing.JProgressBar jprogress;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblManguera;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblPrecioProducto;
    private javax.swing.JLabel lblProducto;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblVolumen;
    private javax.swing.JLabel lbl_identifier_image;
    private javax.swing.JLabel lbl_identifier_name;
    private javax.swing.JLabel lbl_progress_back;
    private javax.swing.JLabel lbl_progress_duration;
    private javax.swing.JLabel lbl_progress_percentage;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnl_aditional_data;
    private javax.swing.JPanel pnl_aditional_data_card;
    private javax.swing.JPanel pnl_aditional_data_license_plate;
    private javax.swing.JPanel pnl_car_data;
    private javax.swing.JPanel pnl_card_input;
    private javax.swing.JPanel pnl_confirmacion;
    private javax.swing.JPanel pnl_identifier_inputs;
    private javax.swing.JPanel pnl_identifier_listen;
    private javax.swing.JPanel pnl_identifier_reader;
    private javax.swing.JPanel pnl_identifiers;
    private javax.swing.JPanel pnl_identifiers_selector;
    private javax.swing.JPanel pnl_keyboard;
    private javax.swing.JPanel pnl_keyboard1;
    private javax.swing.JPanel pnl_keyboard2;
    private javax.swing.JPanel pnl_keyboard3;
    private javax.swing.JPanel pnl_layout_container;
    private javax.swing.JPanel pnl_numeric_code_input;
    private javax.swing.JPanel pnl_sale;
    private javax.swing.JPanel pnl_timeout_progress;
    private javax.swing.ButtonGroup tipoDocumento;
    private javax.swing.JLabel title_view;
    private javax.swing.JTextField txt_license_info1;
    private javax.swing.JTextField txt_license_plate;
    private javax.swing.JTextField txt_numeric_code_serial;
    private javax.swing.JTextField txt_odometer;
    private javax.swing.JPasswordField txt_pass_card;
    private javax.swing.JPasswordField txt_pin;
    private javax.swing.JPasswordField txt_serial_card;
    // End of variables declaration//GEN-END:variables

    public void limpiarTarjeta() {
        txt_serial_card.setText("");
        txt_pass_card.setText("");
        txt_serial_card.requestFocus();
    }

    private void showPanel(String name) {
        pnl_layout_container.requestFocus();
        if (name.equals(PNL_SALE)) {
            cargarDatosVenta();
            thread.start();
        }        
        NovusUtils.showPanel(pnl_layout_container, name);
    }

    private void cargarDatosVenta() {
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
        lblManguera.setText(recibo.getManguera());
        lblProducto.setText(recibo.getProducto());
        lblFecha.setText(sdf.format(recibo.getFecha()));
        lblPrecioProducto.setText("$ " + df.format(recibo.getPrecio()));
        lblVolumen.setText(String.format("%.3f", recibo.getCantidadFactor()) + " "
                + (recibo.getAtributos() != null ? recibo.getAtributos().get("medida").getAsString().toUpperCase()
                : "GL"));
        lblTotal.setText("$ " + df.format(recibo.getTotal()));
        if (!response.get("data").isJsonNull()) {
            String placaVehiculo = response.get("data").getAsJsonObject().get("placaVehiculo").getAsString();
            lblPlaca.setText(placaVehiculo);
        }
        btn_back.setVisible(false);
    }

    boolean isCredito = false;
    JsonObject response = null;

    public synchronized void execute() {
        jprogress.setUI(new MyProgressUI());
        int seconds = 5;
        try {
            while (seconds > 0) {
                Thread.sleep(1000);
                jprogress.setValue(100 / seconds);
                seconds--;
            }
            Thread.sleep(500);
        } catch (InterruptedException e) {
            NovusUtils.printLn(e.getMessage());
            Thread.currentThread().interrupt();
        }
        JsonObject resp = tryRecuperarVenta();
        NovusUtils.printLn(Main.ANSI_RED + resp);
    }

    public JsonObject createJsonRequest() {
        JsonObject json = new JsonObject();
        json.addProperty("id_movimiento", recibo.getNumero());
        json.add("Trama", createJsonTrama());
        json.addProperty("responsable_id", Main.credencial.getId());
        NovusUtils.printLn(Main.ANSI_RED + json + Main.ANSI_RESET);
        return json;
    }

    public JsonObject createJsonTrama() {
        JsonObject jsonTrama = new JsonObject();
        jsonTrama.addProperty("tipoVenta", isCredito ? 10 : null);
        jsonTrama.addProperty("isCredito", isCredito);
        jsonTrama.addProperty("kilometraje", txt_odometer.getText());
        jsonTrama.add("atributos", response.get("data").getAsJsonObject());
        jsonTrama.addProperty("medioAutorizacion", "ibutton");
        jsonTrama.addProperty("serialDispositivo", jTextField1.getText());
        return jsonTrama;
    }

    private JsonObject tryRecuperarVenta() {
        boolean isArray = false;
        boolean isDebug = true;
        String url = NovusConstante.SECURE_CENTRAL_POINT_RECUPERAR_VENTA_CREDITO;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        JsonObject jsonRequest = createJsonRequest();
        ClientWSAsync client = new ClientWSAsync("RECUPERAR VENTA CREDITO", url, NovusConstante.POST, jsonRequest,
                isDebug, isArray, header);
        JsonObject resp = null;
        try {
            resp = client.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        if (resp != null) {
            showMessage("VENTA RECUPERADA CORRECTAMENTE", "/com/firefuel/resources/btOk.png", true, this::cerrar, true);
        } else {
            showMessage("ERROR AL RECUPERAR LA VENTA", "/com/firefuel/resources/btBad.png", true, this::cerrar, true);
        }
        return resp;
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
        TecladoExtendido teclado = (TecladoExtendido) jPanel2;
        teclado.habilitarAlfanumeric(false);
    }

    private void activarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel2;
        teclado.habilitarAlfanumeric(true);
    }

    private void continuarProceso() {
        if (this.getIdentifierMeanSelected() != null) {
            if (this.getIdentifierMeanSelected().getId() == RecuperacionVentaView.CARD_IDENTIFIER_ID) {
                this.setIdentifierSerial(new String(txt_serial_card.getPassword()));
            }
        }
        if (this.btn_next.isEnabled()) {
            NovusUtils.beep();
            this.nextStep(true);
        }
    }

    @Override
    public void run() {
        execute();
    }

    private void cerrar() {
        InfoViewController.NotificadorRecuperacion = null;
        InfoViewController.NotificadorClientePropio = null;
        instance = null;
        if (recargar != null) {
            recargar.run();
        }
        dispose();
    }

    class MyProgressUI extends BasicProgressBarUI {

        Rectangle r = new Rectangle();

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            r = getBox(r);
            g.setColor(progressBar.getForeground());
            g.fillOval(r.x, r.y, r.width, r.height);
        }
    }

    private PanelNotificacion showMessage(String msj, String ruta, boolean habilitar, Runnable runnable, boolean autoclose) {
        PanelNotificacion notificacion = PanelNotificacion.getInstance();
        notificacion.update(msj, ruta, habilitar, runnable);
        notificacion.setTimeout(5);
        if (msj.length() == 0) {
            notificacion.setTimeout(0);
        }
        if (autoclose) {
            notificacion.getTimer().start();
        }
        mostrarSubPanel(notificacion);
        return notificacion;
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

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

}

package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.Notificador;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.facade.ConfigurationFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JToggleButton;
import teclado.view.common.TecladoNumerico;

public class FidelizacionRedencion extends javax.swing.JDialog {

    public static final String COMODIN_TARJETA_LIFEMILES = "%";
    public static final String IDENTIFICADOR_CONSTANTE_TARJETA_LIFEMILES = "LM";
    public static final String SEPARADOR_DATA_TARJETA_LIFEMILES = ";";
    private String salePointIdentificator = null;
    private String identifierCodeTemp = null;
    JToggleButton lastToggleSelected = null;
    InfoViewController mainFrame = null;
    int identifierTypeId = 0;
    boolean entryIsCardReader = false;
    boolean isOnlySearch = true;
    boolean isSaleInLive = false;
    Thread task = null;
    ReciboExtended saleFacture = null;
    public static final int DOCUMENTO_CEDULA = 1;
    public static final int DOCUMENTO_PASAPORTE = 2;
    public static final int DOCUMENTO_CEDULA_EXTRANJERIA = 3;
    public static final int NUMERO_LIFE_MILES = 4;
    int CantidadMillasRed = 0;
    Notificador notify = null;
    public TreeMap<Integer, String> tiposDocumentos = new TreeMap<>();
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    public FidelizacionRedencion(InfoViewController mainFrame, boolean modal) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.init();
    }

    public FidelizacionRedencion(InfoViewController mainFrame, boolean modal, int CantidadMillasRed,
            Notificador notify) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.CantidadMillasRed = CantidadMillasRed;
        this.notify = notify;
        this.init();
    }

    public FidelizacionRedencion(InfoViewController mainFrame, boolean modal, ReciboExtended saleFacture,
            boolean isOnlySearch) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.saleFacture = saleFacture;
        this.isOnlySearch = isOnlySearch;
        this.init();
    }

    public FidelizacionRedencion(InfoViewController mainFrame, boolean modal, ReciboExtended saleFacture,
            boolean isOnlySearch, boolean isSaleInLive) {
        super(mainFrame, modal);
        initComponents();
        this.mainFrame = mainFrame;
        this.isOnlySearch = isOnlySearch;
        this.saleFacture = saleFacture;
        this.isSaleInLive = isSaleInLive;
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
            mostrarPanelMensaje("NO SE TIENE EL IDENTIFICADOR DE PUNTO DE VENTA", 
                    "/com/firefuel/resources/btBad.png",
            LetterCase.FIRST_UPPER_CASE);
            this.close();
        } else {
            super.setVisible(b);
        }
    }

    void actualizarVentaCurso() {

    }

    void renderSaleDetails() {
        ReciboExtended recibo = this.getSaleFacture();
        String salePrefix = (recibo.getAtributos() != null && recibo.getAtributos().get("consecutivo") != null
                && !recibo.getAtributos().get("consecutivo").isJsonNull())
                ? recibo.getAtributos().get("consecutivo").isJsonPrimitive()
                ? recibo.getAtributos().get("consecutivo").getAsString()
                : recibo.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo")
                        .getAsString() + "-"
                + recibo.getAtributos().get("consecutivo").getAsJsonObject()
                        .get("consecutivo_actual").getAsString()
                : "";
        String saleNumber = String.valueOf(recibo.getNumero());

    }

    void changeLabelButtonAction() {

    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        if (!this.isOnlySearch && !this.isSaleInLive) {
            this.renderSaleDetails();
        } else if (this.isSaleInLive) {
            this.actualizarVentaCurso();
        }
        this.jTextValor.setText(this.CantidadMillasRed + "");
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
        if (this.entryIsCardReader) {
            return this.identifierCodeTemp != null && !this.identifierCodeTemp.trim().equals("");
        } else {
            return !identifierTxt.equals("");
        }
    }

    boolean isIdentifierTypeSelected() {
        return this.identifierTypeId > 0;
    }

    void loadData() {
        this.setSalePointIdentificator(this.fetchSalePointIdentificator());
        this.setIdentifiersTypeCodeWithEnum();
    }

    void toggleSaleDetailPanel(boolean active) {

    }

    void toggleSaleInLivePanel(boolean active) {

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
    }

    void close() {
        dispose();
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
        this.identifierCodeTemp = null;
    }

    void toggleEnableActionButton(boolean active) {

    }

    void adaptFieldForCardReader(boolean adapt) {
        this.txt_code_identifier.setEditable(!adapt);
        if (adapt) {
            this.txt_code_identifier.setText("");
        }
    }

    String getIdentifierFromIdentifierTemp() {
        if (this.identifierCodeTemp != null) {
            try {
                String[] identifierPlots = this.identifierCodeTemp.replaceAll("\n", "").replaceAll("\r", "").trim()
                        .split(SEPARADOR_DATA_TARJETA_LIFEMILES);
                String identifierDocumentPlot = identifierPlots[2].trim();
                String documentoIdentificador = identifierDocumentPlot.substring(0,
                        identifierDocumentPlot.indexOf("="));
                return documentoIdentificador;
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    String getIdentifier() {
        return this.entryIsCardReader ? this.getIdentifierFromIdentifierTemp().trim()
                : this.txt_code_identifier.getText().trim();
    }

    void AddPuntosMP() {
        String idCliente = this.txt_code_identifier.getText();
        String pin = new String(this.txtPin.getPassword());
        String Valor = this.jTextValor.getText().trim();
        String tipoId = this.tiposDocumentos.get(this.identifierTypeId);

        if (!idCliente.equals("") && !pin.equals("") && !Valor.equals("")) {
            NovusUtils.printLn("<<<<<<<<<<<<< INFO REDENCION MILLA >>>>>>>>>>>");
            NovusUtils.printLn("ID CLIENTE: " + idCliente);
            NovusUtils.printLn("TIPO ID: " + tipoId);
            NovusUtils.printLn("PIN: " + pin);
            NovusUtils.printLn("VALOR: " + Valor);
            NovusUtils.printLn("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>");
            JsonObject data = new JsonObject();
            data.addProperty("valor", Valor);
            data.addProperty("bono", "");
            data.addProperty("pin", pin);
            data.addProperty("idCliente", idCliente);
            data.addProperty("tipo_id", tipoId);

            if (notify != null) {
                notify.send(data);
            }
            this.dispose();
        } else {
            mostrarPanelMensaje("FALTAN CAMPOS POR COMPLETAR", 
                    "/com/firefuel/resources/btBad.png",
            LetterCase.FIRST_UPPER_CASE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tipoDocumento = new javax.swing.ButtonGroup();
        btn_group_identifier_type = new javax.swing.ButtonGroup();
        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        pnl_keyboard = new TecladoNumerico();
        toggle_identifier_type_cc = new javax.swing.JToggleButton();
        txt_code_identifier = new javax.swing.JTextField();
        toggle_identifier_type_strangecc = new javax.swing.JToggleButton();
        txtPin = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        jTextValor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);
        pnl_principal.add(pnl_keyboard);
        pnl_keyboard.setBounds(670, 150, 570, 470);

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
        pnl_principal.add(toggle_identifier_type_cc);
        toggle_identifier_type_cc.setBounds(60, 250, 290, 60);

        txt_code_identifier.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txt_code_identifier.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_code_identifier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 0, 0), 1, true));
        txt_code_identifier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_code_identifierFocusGained(evt);
            }
        });
        txt_code_identifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_code_identifierActionPerformed(evt);
            }
        });
        txt_code_identifier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_code_identifierKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_code_identifierKeyTyped(evt);
            }
        });
        pnl_principal.add(txt_code_identifier);
        txt_code_identifier.setBounds(60, 170, 590, 70);

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
        pnl_principal.add(toggle_identifier_type_strangecc);
        toggle_identifier_type_strangecc.setBounds(370, 250, 270, 60);

        txtPin.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        txtPin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 0)));
        txtPin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPinFocusGained(evt);
            }
        });
        txtPin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPinActionPerformed(evt);
            }
        });
        txtPin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPinKeyTyped(evt);
            }
        });
        pnl_principal.add(txtPin);
        txtPin.setBounds(60, 362, 590, 70);

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("VALOR A PAGAR ");
        pnl_principal.add(jLabel6);
        jLabel6.setBounds(60, 450, 570, 40);

        jTextValor.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        jTextValor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextValor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 0)));
        jTextValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextValorFocusGained(evt);
            }
        });
        jTextValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextValorKeyTyped(evt);
            }
        });
        pnl_principal.add(jTextValor);
        jTextValor.setBounds(60, 490, 590, 70);

        jLabel3.setBackground(new java.awt.Color(51, 51, 51));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("INGRESE PIN ");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(60, 320, 570, 40);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(1170, 3, 10, 80);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(logo);
        logo.setBounds(10, 700, 110, 100);

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("INGRESE IDENTIFICACION:");
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(60, 130, 570, 40);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 71);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(750, 10, 430, 70);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("REDENCION PUNTOS VIVE TERPEL");
        pnl_principal.add(jTitle);
        jTitle.setBounds(120, 15, 630, 50);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(90, 10, 10, 68);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnl_principal.add(jLabel2);
        jLabel2.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_code_identifierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_code_identifierKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txt_code_identifier, 11, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_code_identifierKeyTyped

    private void txtPinKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPinKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txtPin, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtPinKeyTyped

    private void jTextValorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextValorKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jTextValor, 12, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jTextValorKeyTyped

    private void txt_code_identifierFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_code_identifierFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_code_identifier);
    }//GEN-LAST:event_txt_code_identifierFocusGained

    private void txtPinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPinFocusGained
        NovusUtils.deshabilitarCopiarPegar(txtPin);
    }//GEN-LAST:event_txtPinFocusGained

    private void jTextValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextValorFocusGained
        NovusUtils.deshabilitarCopiarPegar(jTextValor);
    }//GEN-LAST:event_jTextValorFocusGained

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        this.close();
    }// GEN-LAST:event_jLabel13MouseReleased

    private void toggle_identifier_type_ccActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggle_identifier_type_ccActionPerformed
        NovusUtils.beep();
        this.selectIdentifierType(DOCUMENTO_CEDULA, (JToggleButton) (evt.getSource()));
    }// GEN-LAST:event_toggle_identifier_type_ccActionPerformed

    private void toggle_identifier_type_strangeccActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggle_identifier_type_strangeccActionPerformed
        NovusUtils.beep();
        this.selectIdentifierType(DOCUMENTO_CEDULA_EXTRANJERIA, (JToggleButton) (evt.getSource()));
    }// GEN-LAST:event_toggle_identifier_type_strangeccActionPerformed

    private void txt_code_identifierActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_code_identifierActionPerformed
        AddPuntosMP();
    }// GEN-LAST:event_txt_code_identifierActionPerformed

    private void txt_code_identifierKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_code_identifierKeyReleased
    }// GEN-LAST:event_txt_code_identifierKeyReleased

    private void txtPinActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtPinActionPerformed
        AddPuntosMP();
    }// GEN-LAST:event_txtPinActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btn_group_identifier_type;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JTextField jTextValor;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_keyboard;
    private javax.swing.JPanel pnl_principal;
    private javax.swing.ButtonGroup tipoDocumento;
    public javax.swing.JToggleButton toggle_identifier_type_cc;
    public javax.swing.JToggleButton toggle_identifier_type_strangecc;
    private javax.swing.JPasswordField txtPin;
    private javax.swing.JTextField txt_code_identifier;
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

        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

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

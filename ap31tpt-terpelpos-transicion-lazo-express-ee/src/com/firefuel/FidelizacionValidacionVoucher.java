package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.ConsultVoucher;
import com.bean.MediosPagosBean;
import com.bean.Notificador;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.facade.ConfigurationFacade;
import com.facade.FidelizacionFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import teclado.view.common.TecladoExtendidoGray;
import teclado.view.common.TecladoNumerico;

public class FidelizacionValidacionVoucher extends javax.swing.JDialog {

    InfoViewController parent;
    boolean isActived;
    boolean isOnlySearch;
    Notificador notify;
    int sale;
    Long restanteVenta;
    ArrayList<MediosPagosBean> mediosPagos;
    Runnable handler = null;
    public static final String PNL_MENSAJES = "pnl_mensajes";
    public static final String RECURSO_ERROR = "/com/firefuel/resources/btBad.png";

    public FidelizacionValidacionVoucher(InfoViewController parent, boolean modal, boolean isOnlySearch) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.isOnlySearch = isOnlySearch;
        this.init();
    }

    public FidelizacionValidacionVoucher(InfoViewController parent, boolean modal, boolean isOnlySearch, int sale,
            Notificador notify, ArrayList<MediosPagosBean> mediosPagos, long restanteVenta) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.isOnlySearch = isOnlySearch;
        this.sale = sale;
        this.notify = notify;
        this.restanteVenta = restanteVenta;
        this.mediosPagos = mediosPagos;
        this.init();
    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        this.jTextValor.setText(this.sale != 0 ? sale + "" : "");
    }

    private void init() {
        this.loadView();
        habilitarTeclas();
    }

    public void habilitarTeclas() {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) pnlTeclado;
        teclado.habilitarAlfanumeric(true);
        teclado.habilitarCaracteresEspeciales(false);
    }

    void handleKeyPressTxtField(String keyChar, KeyEvent evt) {
        this.toggleEnableActionButton(this.isEmptyField());
    }

    void toggleEnableActionButton(boolean active) {
        this.setIsActived(isActived);
    }

    boolean isEmptyField() {

        String Voucher = this.jTextVoucher.getText().trim();
        String Valor = this.jTextValor.getText().trim();

        return !Voucher.equals("") && !Valor.equals("");
    }

    public void setIsActived(boolean isActived) {
        this.isActived = isActived;
    }

    void close() {
        dispose();
        setVisible(false);
    }

    void consultVoucher() {
        String voucher = jTextVoucher.getText().trim();
        String valor = jTextValor.getText().trim().split("\\.")[0];

        if (voucher.length() < 6) {
            showMessage("VALOR DE CODIGO MINIMO 6 DIGITOS", RECURSO_ERROR,
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
            return;
        }

        String idPuntoVenta = fetchSalePointIdentificator();
        ConsultVoucher consult = new ConsultVoucher();
        consult.setOrigenVenta("EDS");
        consult.setIdentificacionPuntoVenta(idPuntoVenta);
        consult.setCodigoBono(voucher);
        try {
            consult.setValorBono(Long.parseLong(valor));
        } catch (NumberFormatException e) {
            consult.setValorBono(0);
        }

        if (!this.isOnlySearch) {
            String error = "";
            for (MediosPagosBean medio : this.mediosPagos) {
                if (medio.getVoucher() != null && medio.getVoucher().equals(consult.getCodigoBono())) {
                    error += "Codigo de bono (".concat(medio.getVoucher()).concat(") ya esta en uso");
                    break;
                }
            }
            if (error.length() > 0) {
                showMessage(error.toUpperCase(), RECURSO_ERROR,
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
                return;
            }
        }

        showMessage("CONSULTA BONO ESPERE...",
                "/com/firefuel/resources/loader_fac.gif",
                false, this::mostrarMenuPrincipal,
                false, LetterCase.FIRST_UPPER_CASE);
        Thread taskRun = new Thread() {
            @Override
            public void run() {
                JsonObject response = FidelizacionFacade.fetchSearchVoucher(consult);
                mostrarMenuPrincipal();
                handlerVoucherResponse(response, (int) consult.getValorBono(), consult.getCodigoBono());
            }
        };
        taskRun.start();
    }

    private String fetchSalePointIdentificator() {
        return ConfigurationFacade.fetchSalePointIdentificator();
    }

    public void showMessage(String msj, String ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnl_principal.getLayout();
        pnl_principal.add("pnl_ext", panel);
        layout.show(pnl_principal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnl_principal.getLayout();
        layout.show(pnl_principal, "pnl_validacion");
        jTextValor.requestFocus();
    }

    void handlerVoucherResponse(JsonObject response, int montoIngresado, String bono) {
        final int CLIENT_IDENTIFIED_CODE = 20000;

        int statusCode = 0;
        try {
            if (response != null && !response.get("statusCode").isJsonNull()) {
                statusCode = response.get("statusCode").getAsInt();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }

        switch (statusCode) {
            case 200:
                if (response != null) {
                    JsonObject data = new JsonObject();
                    if (response.get("codigoRespuesta") != null) {
                        String responseMessage = response.get("mensajeRespuesta") != null ? response.get("mensajeRespuesta").getAsString().toUpperCase() : "ERROR EN EL PROCESO";
                        int responseCode = response.get("codigoRespuesta").getAsInt();
                        if (responseCode == CLIENT_IDENTIFIED_CODE) {
                            String monto = response.get("monto").getAsString().split("\\.")[0].replace(".00", "");
                            data.addProperty("bono", bono);
                            data.addProperty("valor", (long) montoIngresado);
                            data.addProperty("estado", "valido");
                            if (this.isOnlySearch) {
                                String mensaje = "<html><center>" + responseMessage + "\n<br> Valor: " + monto + "</br></center></html>";
                                showMessage(mensaje, "/com/firefuel/resources/btOk.png",
                                        true, this::mostrarMenuPrincipal,
                                        true, LetterCase.FIRST_UPPER_CASE);
                            } else {
                                if (response.get("monto") != null) {
                                    System.out.println("[this.restanteVenta] " + this.restanteVenta);
                                    if (Long.parseLong(monto) > this.restanteVenta) {
                                        long lostMoney = (long) Long.parseLong(monto) - this.restanteVenta;
                                        jCerrar1.setVisible(isActived);
                                        jMensaje.setText("<html><center>El valor del Bono es\n"
                                                + "superior al valor de la venta,\n el cliente perderá el saldo de "
                                                + "<font color=red>$" + lostMoney + "</font>"
                                                + ",<br/><br/> ¿desea continuar?</center></html>");
                                        showPanel(PNL_MENSAJES);
                                        data.addProperty("valor", this.restanteVenta);
                                        handler = () -> {
                                            if (this.notify != null) {
                                                NovusUtils.printLn("VALOR BONO VIVE TERPEL" + this.restanteVenta);
                                                notify.send(data);
                                                close();
                                            }
                                        };
                                    } else {
                                        if (this.notify != null) {
                                            notify.send(data);
                                        }
                                        close();
                                    }
                                } else {
                                    showMessage("Error al obtener valor del bono",
                                            RECURSO_ERROR, true,
                                            this::mostrarMenuPrincipal, true,
                                            LetterCase.FIRST_UPPER_CASE);
                                }
                            }
                        } else {
                            showMessage(responseMessage, RECURSO_ERROR,
                                    true, this::mostrarMenuPrincipal,
                                    true, LetterCase.FIRST_UPPER_CASE);
                        }
                    } else {
                        String mensaje = response.get("mensajeError").getAsString();
                        showMessage(mensaje, RECURSO_ERROR, true,
                                this::mostrarMenuPrincipal, true,
                                LetterCase.FIRST_UPPER_CASE);
                    }
                } else {
                    showMessage("No hay conexión con el Motor de Fidelización",
                            RECURSO_ERROR, true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                }
                break;
            default:
                showMessage("No hay conexión con el Motor de Fidelización", RECURSO_ERROR,
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
                break;
        }
    }

    void FocusTxtField() {
        jTextVoucher.requestFocus();
    }

    private void showPanel(String panel) {
        NovusUtils.showPanel(pnl_principal, panel);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jclock = new javax.swing.JPanel();
        title_view = new javax.swing.JLabel();
        pnl_principal = new javax.swing.JPanel();
        pnl_validacion = new javax.swing.JPanel();
        jNotificacion_Validacion = new javax.swing.JLabel();
        jTextValor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextVoucher = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        pnlTeclado = new TecladoExtendidoGray(true);
        fnd = new javax.swing.JLabel();
        pnl_mensajes = new javax.swing.JPanel();
        NO = new javax.swing.JLabel();
        SI = new javax.swing.JLabel();
        jCerrar1 = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jclock.setOpaque(false);
        jclock.setLayout(null);

        title_view.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        title_view.setForeground(new java.awt.Color(255, 255, 255));
        title_view.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        title_view.setText("CONSULTA CLIENTE VIVE TERPEL");
        title_view.setToolTipText("");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_principal.setLayout(new java.awt.CardLayout());

        pnl_validacion.setLayout(null);

        jNotificacion_Validacion.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion_Validacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_validacion.add(jNotificacion_Validacion);
        jNotificacion_Validacion.setBounds(130, 720, 1120, 70);

        jTextValor.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        jTextValor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextValor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 0)));
        jTextValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextValorFocusGained(evt);
            }
        });
        jTextValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextValorActionPerformed(evt);
            }
        });
        jTextValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextValorKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextValorKeyTyped(evt);
            }
        });
        pnl_validacion.add(jTextValor);
        jTextValor.setBounds(670, 146, 590, 70);

        jLabel3.setBackground(new java.awt.Color(51, 51, 51));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("INGRESE VALOR DE BONO:");
        pnl_validacion.add(jLabel3);
        jLabel3.setBounds(670, 100, 570, 40);

        jTextVoucher.setFont(new java.awt.Font("Segoe UI", 1, 55)); // NOI18N
        jTextVoucher.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextVoucher.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 0)));
        jTextVoucher.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextVoucherFocusGained(evt);
            }
        });
        jTextVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextVoucherActionPerformed(evt);
            }
        });
        jTextVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextVoucherKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextVoucherKeyTyped(evt);
            }
        });
        pnl_validacion.add(jTextVoucher);
        jTextVoucher.setBounds(40, 146, 590, 70);

        jLabel2.setBackground(new java.awt.Color(51, 51, 51));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("INGRESE CÓDIGO DE BONO:");
        pnl_validacion.add(jLabel2);
        jLabel2.setBounds(40, 100, 570, 40);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        pnl_validacion.add(jLabel13);
        jLabel13.setBounds(20, 16, 48, 49);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_validacion.add(logo);
        logo.setBounds(10, 700, 110, 100);

        jButton2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton2.setText("5.000");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        pnl_validacion.add(jButton2);
        jButton2.setBounds(670, 224, 110, 55);

        jButton5.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton5.setText("30.000");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        pnl_validacion.add(jButton5);
        jButton5.setBounds(1030, 224, 110, 55);

        jButton7.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton7.setText("10.000");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        pnl_validacion.add(jButton7);
        jButton7.setBounds(790, 224, 110, 55);

        jButton8.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton8.setText("20.000");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        pnl_validacion.add(jButton8);
        jButton8.setBounds(910, 224, 110, 55);

        jButton9.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton9.setText("50.000");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        pnl_validacion.add(jButton9);
        jButton9.setBounds(1150, 224, 110, 55);

        jButton10.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton10.setText("100.000");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        pnl_validacion.add(jButton10);
        jButton10.setBounds(670, 290, 110, 55);

        jLabel10.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel10.setText("VALOR EN BILLETES");
        pnl_validacion.add(jLabel10);
        jLabel10.setBounds(440, 224, 190, 50);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_validacion.add(jNotificacion);
        jNotificacion.setBounds(730, 10, 530, 70);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("Consultar Bono Vive Terpel");
        pnl_validacion.add(jTitle);
        jTitle.setBounds(110, 15, 720, 50);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_validacion.add(jLabel27);
        jLabel27.setBounds(90, 10, 10, 68);

        pnlTeclado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlTecladoMouseReleased(evt);
            }
        });
        pnl_validacion.add(pnlTeclado);
        pnlTeclado.setBounds(130, 354, 1024, 340);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnl_validacion.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnl_principal.add(pnl_validacion, "pnl_validacion");

        pnl_mensajes.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_mensajes.setName(""); // NOI18N
        pnl_mensajes.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_mensajes.setLayout(null);

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
        pnl_mensajes.add(NO);
        NO.setBounds(330, 530, 264, 54);

        SI.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SI.setForeground(new java.awt.Color(255, 255, 255));
        SI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        SI.setText("SI");
        SI.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SIMouseClicked(evt);
            }
        });
        pnl_mensajes.add(SI);
        SI.setBounds(700, 530, 264, 54);

        jCerrar1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar1.setForeground(new java.awt.Color(255, 255, 255));
        jCerrar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jCerrar1.setText("CERRAR");
        jCerrar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrar1MouseClicked(evt);
            }
        });
        pnl_mensajes.add(jCerrar1);
        jCerrar1.setBounds(500, 460, 264, 54);

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(204, 0, 0));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("MENSAJE VALIDACION");
        pnl_mensajes.add(jMensaje);
        jMensaje.setBounds(260, 180, 730, 240);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndNotify.png"))); // NOI18N
        pnl_mensajes.add(jLabel4);
        jLabel4.setBounds(0, 0, 1280, 800);

        pnl_principal.add(pnl_mensajes, "pnl_mensajes");

        getContentPane().add(pnl_principal);
        pnl_principal.setBounds(0, 0, 1280, 800);
        pnl_principal.getAccessibleContext().setAccessibleName("");

        setSize(new java.awt.Dimension(1281, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextVoucherKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextVoucherKeyTyped
        String caracteresAceptados = NovusConstante.CARACTERES_ALFANUMERICOS;
        NovusUtils.limitarCarateres(evt, jTextVoucher, 13, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jTextVoucherKeyTyped

    private void jTextValorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextValorKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jTextValor, 12, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jTextValorKeyTyped

    private void jTextVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextVoucherFocusGained
        NovusUtils.deshabilitarCopiarPegar(jTextVoucher);
    }//GEN-LAST:event_jTextVoucherFocusGained

    private void jTextValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextValorFocusGained
        NovusUtils.deshabilitarCopiarPegar(jTextValor);
    }//GEN-LAST:event_jTextValorFocusGained

    private void NOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NOMouseClicked
        close();
    }//GEN-LAST:event_NOMouseClicked

    private void jCerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jCerrar1MouseClicked

    private void SIMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SIMouseClicked
        handler.run();
        this.close();
    }//GEN-LAST:event_SIMouseClicked

    private void pnlTecladoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTecladoMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlTecladoMouseReleased

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        close();
    }// GEN-LAST:event_jLabel13MouseReleased

    private void jTextVoucherKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextVoucherKeyReleased
        String keyChar = evt.getKeyChar() + "";
        this.handleKeyPressTxtField(keyChar, evt);
    }// GEN-LAST:event_jTextVoucherKeyReleased

    private void jTextValorKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextValorKeyReleased
        String keyChar = evt.getKeyChar() + "";
        this.handleKeyPressTxtField(keyChar, evt);
    }// GEN-LAST:event_jTextValorKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        jTextValor.setText("5000");
        FocusTxtField();
    }// GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton5ActionPerformed
        jTextValor.setText("30000");
        FocusTxtField();
    }// GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton7ActionPerformed
        jTextValor.setText("10000");
        FocusTxtField();
    }// GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton8ActionPerformed
        jTextValor.setText("20000");
        FocusTxtField();
    }// GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton9ActionPerformed
        jTextValor.setText("50000");
        FocusTxtField();
    }// GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton10ActionPerformed
        jTextValor.setText("100000");
        FocusTxtField();
    }// GEN-LAST:event_jButton10ActionPerformed

    private void jTextVoucherActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextVoucherActionPerformed
        NovusUtils.printLn("isAtived: " + this.isActived);
        consultVoucher();
        habilitarTeclas();
    }// GEN-LAST:event_jTextVoucherActionPerformed

    private void jTextValorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextValorActionPerformed
        NovusUtils.printLn("isAtived: " + this.isActived);
        consultVoucher();
    }// GEN-LAST:event_jTextValorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NO;
    private javax.swing.JLabel SI;
    private javax.swing.JLabel fnd;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jCerrar1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion_Validacion;
    private javax.swing.JTextField jTextValor;
    private javax.swing.JTextField jTextVoucher;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel jclock;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel pnlTeclado;
    private javax.swing.JPanel pnl_mensajes;
    private javax.swing.JPanel pnl_principal;
    private javax.swing.JPanel pnl_validacion;
    private javax.swing.JLabel title_view;
    // End of variables declaration//GEN-END:variables

}

package com.firefuel;

import com.WT2.appTerpel.domain.context.Concurrents;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.sutidores.GetIdMedioPagoVentaCursoUseCase;
import com.application.useCases.sutidores.GetMovimientoIdDesdeCaraUseCase;
import com.application.useCases.productos.GetProductNameUseCase;
import com.bean.MediosPagosBean;
import com.bean.ReciboExtended;
import com.bean.ventaCurso.VentaCursoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.fidelizacionYfacturaElectronica.VentasCurso;
import com.firefuel.mediospago.PanelMediosPago;
import com.firefuel.mediospago.RenderPanelPlacasGopass;
import com.firefuel.mediospago.RenderPanelsMediosPago;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Manguera;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import teclado.view.common.TecladoExtendidoGray;

/**
 *
 * @author novus
 */
public class VentaCursoPlaca extends javax.swing.JDialog {

    InfoViewController parent;
    Manguera manguera;
    ArrayList<MediosPagosBean> mediosPagos;
    MovimientosDao mdao = new MovimientosDao();
    JsonArray datafonos = new JsonArray();
    boolean isDefault;
    boolean ventaFidelizacion;
    boolean ocultarInputsFactura;
    VentaCursoPlaca ventanaVentaCurso;

    ArrayList<MedioPagoImageBean> listMedios;
    Runnable deshabilitarGestionarVenta;
    Runnable habilitarGestionarVenta;
    String caracteresPermitidos = "[0-9]";
    int cantidadCaracteres = 10;
    GetIdMedioPagoVentaCursoUseCase getIdMedioPagoVentaCursoUseCase;
    final RenderPanelsMediosPago render = new RenderPanelsMediosPago();

    public VentaCursoPlaca(InfoViewController parent, boolean modal,
            Runnable deshabilitarGestionarVenta, Runnable habilitarGestionarVenta,
            String placaComunidades, boolean comunidades, boolean ocultarInputsFactura) {
        super(parent, modal);
        this.parent = parent;
        this.deshabilitarGestionarVenta = deshabilitarGestionarVenta;
        this.habilitarGestionarVenta = habilitarGestionarVenta;
        this.ocultarInputsFactura = ocultarInputsFactura;
        initComponents();
        this.init();
        deshabilitarPlaca(placaComunidades, comunidades);
        ocultarInputsFactura(this.ocultarInputsFactura);
    }

    public VentaCursoPlaca(InfoViewController parent, boolean modal,
            boolean ventaFidelizacion, Runnable habilitarGestionarVenta) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.ventaFidelizacion = ventaFidelizacion;
        this.habilitarGestionarVenta = habilitarGestionarVenta;
        this.init();
        ocultarComponentes(ventaFidelizacion);
    }

    private void ocultarInputsFactura(boolean ocultar) {
        if (ocultar) {
            ocultarComponentes(ocultar);
        }
    }

    private boolean isDatafono(MediosPagosBean medio) {
        return medio.getDescripcion().equalsIgnoreCase("CON DATAFONO");
    }

    private boolean isMedioEspecial(MediosPagosBean medio) {
        return medio.getId() >= 10000;
    }

    void setManguera(Manguera dataManguera) {
        this.manguera = dataManguera;
    }

    void ocultarOpcionMedioPago(boolean visible) {
        panelSeleccionMedioPago.setVisible(visible);
        jLabel3.setVisible(visible);
    }

    private void init() {
        ocultarOpcionMedioPago(false);
        FacturacionElectronica fac = new FacturacionElectronica();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        isDefault = fac.isDefaultFe();
        cargarDatafonos();
        VentasCurso ventasCurso = new VentasCurso();
        ventasCurso.cambiarApariencia(this);
        ventasCurso.cargarTiposDocumentos(this, this.ventaFidelizacion);

    }

    private void cargarDatafonos() {
        try {
            EquipoDao edao = new EquipoDao();
            datafonos = edao.datafonosInfo();
        } catch (DAOException | SQLException ex) {
            Logger.getLogger(VentaCursoPlaca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        jTextField1.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        getIdMedioPagoVentaCursoUseCase = new GetIdMedioPagoVentaCursoUseCase();
        buttonGroup1 = new javax.swing.ButtonGroup();
        comboBox1 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        comboBox2 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        comboBox3 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        comboBox4 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        comboBox5 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        comboBox6 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        pnlPrincipal = new javax.swing.JPanel();
        datosFactura = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new TecladoExtendidoGray();
        jLabel9 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        contenedorDatos = new javax.swing.JPanel();
        pnlPlatayMediosPagos = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        pnlKilometrajeyNumeroComorbante = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        panelSeleccionMedioPago = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnSeleccionMedioPago = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        comboTiposIdentificacion = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(null);

        pnlPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        datosFactura.setBackground(new java.awt.Color(255, 255, 255));
        datosFactura.setMinimumSize(new java.awt.Dimension(1280, 800));
        datosFactura.setPreferredSize(new java.awt.Dimension(1280, 800));
        datosFactura.setLayout(null);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 0));
        datosFactura.add(jLabel5);
        jLabel5.setBounds(140, 720, 330, 60);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        jLabel4.setText("GUARDAR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        datosFactura.add(jLabel4);
        jLabel4.setBounds(920, 700, 200, 100);

        jLabel10.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        jLabel10.setText("CANCELAR");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        datosFactura.add(jLabel10);
        jLabel10.setBounds(470, 700, 200, 100);

        jLabel11.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        jLabel11.setText("CONSULTAR");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        datosFactura.add(jLabel11);
        jLabel11.setBounds(705, 715, 180, 70);

        jPanel1.setOpaque(false);
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        datosFactura.add(jPanel1);
        jPanel1.setBounds(130, 360, 1024, 336);

        jLabel9.setFont(new java.awt.Font("Conthrax", 1, 28)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText(" ");
        datosFactura.add(jLabel9);
        jLabel9.setBounds(460, 0, 810, 90);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        datosFactura.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        datosFactura.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        datosFactura.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        datosFactura.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel12.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("DATOS FACTURA ");
        datosFactura.add(jLabel12);
        jLabel12.setBounds(100, 0, 520, 90);

        txtCliente.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        txtCliente.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtCliente.setDisabledTextColor(new java.awt.Color(161, 140, 140));
        txtCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClienteFocusGained(evt);
            }
        });
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });
        txtCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtClienteKeyTyped(evt);
            }
        });
        datosFactura.add(txtCliente);
        txtCliente.setBounds(140, 120, 460, 70);

        contenedorDatos.setBackground(new java.awt.Color(255, 255, 255));
        contenedorDatos.setLayout(new java.awt.CardLayout());

        pnlPlatayMediosPagos.setBackground(new java.awt.Color(255, 255, 255));
        pnlPlatayMediosPagos.setLayout(null);

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jTextField1.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jTextField1.setMargin(new java.awt.Insets(3, 0, 0, 0));
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextField1MouseReleased(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        pnlPlatayMediosPagos.add(jTextField1);
        jTextField1.setBounds(20, 30, 460, 70);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("INGRESE PLACA");
        pnlPlatayMediosPagos.add(jLabel1);
        jLabel1.setBounds(31, 0, 440, 22);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-siguiente.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        pnlPlatayMediosPagos.add(jLabel13);
        jLabel13.setBounds(1050, 24, 70, 80);

        jLabel14.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel14.setText("KILOMETRAJE");
        pnlPlatayMediosPagos.add(jLabel14);
        jLabel14.setBounds(540, 0, 440, 22);

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jTextField2.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });
        pnlPlatayMediosPagos.add(jTextField2);
        jTextField2.setBounds(530, 30, 460, 70);

        contenedorDatos.add(pnlPlatayMediosPagos, "pnlPlatayMediosPagos");

        pnlKilometrajeyNumeroComorbante.setBackground(new java.awt.Color(255, 255, 255));
        pnlKilometrajeyNumeroComorbante.setLayout(null);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-aras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        pnlKilometrajeyNumeroComorbante.add(jLabel7);
        jLabel7.setBounds(1040, 24, 70, 80);

        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setText("NÚMERO DEL COMPROBANTE");
        pnlKilometrajeyNumeroComorbante.add(jLabel6);
        jLabel6.setBounds(30, 10, 440, 22);

        jTextField3.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jTextField3.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3KeyTyped(evt);
            }
        });
        pnlKilometrajeyNumeroComorbante.add(jTextField3);
        jTextField3.setBounds(20, 40, 530, 70);

        panelSeleccionMedioPago.setBackground(new java.awt.Color(190, 0, 0));
        panelSeleccionMedioPago.setRoundBottomLeft(30);
        panelSeleccionMedioPago.setRoundBottomRight(30);
        panelSeleccionMedioPago.setRoundTopLeft(30);
        panelSeleccionMedioPago.setRoundTopRight(30);
        panelSeleccionMedioPago.setLayout(null);

        btnSeleccionMedioPago.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        btnSeleccionMedioPago.setForeground(new java.awt.Color(255, 255, 255));
        btnSeleccionMedioPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSeleccionMedioPago.setText("Seleccione Medio de Pago");
        btnSeleccionMedioPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSeleccionMedioPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSeleccionMedioPagoMouseReleased(evt);
            }
        });
        panelSeleccionMedioPago.add(btnSeleccionMedioPago);
        btnSeleccionMedioPago.setBounds(1, 0, 460, 70);

        pnlKilometrajeyNumeroComorbante.add(panelSeleccionMedioPago);
        panelSeleccionMedioPago.setBounds(570, 40, 460, 70);

        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("MEDIO DE PAGO");
        pnlKilometrajeyNumeroComorbante.add(jLabel3);
        jLabel3.setBounds(570, 10, 250, 22);

        contenedorDatos.add(pnlKilometrajeyNumeroComorbante, "pnlKilometrajeyNumeroComorbante");

        datosFactura.add(contenedorDatos);
        contenedorDatos.setBounds(120, 230, 1130, 125);

        comboTiposIdentificacion.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        comboTiposIdentificacion.setBorder(null);
        comboTiposIdentificacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTiposIdentificacionActionPerformed(evt);
            }
        });
        datosFactura.add(comboTiposIdentificacion);
        comboTiposIdentificacion.setBounds(620, 120, 540, 70);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("TIPOS DE DOCUMENTO");
        datosFactura.add(jLabel2);
        jLabel2.setBounds(620, 90, 450, 22);

        jLabel15.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel15.setText("NÚMERO IDENTIFICACIÓN CLIENTE");
        datosFactura.add(jLabel15);
        jLabel15.setBounds(150, 90, 450, 28);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        datosFactura.add(jLabel8);
        jLabel8.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(datosFactura, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        VentasCurso ventasCurso = new VentasCurso();
        ventasCurso.cambiarPanel(contenedorDatos, "pnlPlatayMediosPagos");
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        VentasCurso ventasCurso = new VentasCurso();
        ventasCurso.cambiarPanel(contenedorDatos, "pnlKilometrajeyNumeroComorbante");
    }//GEN-LAST:event_jLabel13MouseClicked

    private void txtClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClienteKeyTyped
        NovusUtils.limitarCarateres(evt, txtCliente, cantidadCaracteres, jLabel9, caracteresPermitidos);
    }//GEN-LAST:event_txtClienteKeyTyped

    private void txtClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClienteFocusGained
        NovusUtils.deshabilitarCopiarPegar(txtCliente);
    }//GEN-LAST:event_txtClienteFocusGained

    private void comboTiposIdentificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTiposIdentificacionActionPerformed
        if (comboTiposIdentificacion.getSelectedItem().toString().equals("CONSUMIDOR FINAL")) {
            txtCliente.setText("222222222222");
        } else {
            txtCliente.setText("");
        }
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel1;
        caracteresPermitidos = NovusUtils.obtenerRestriccionCaracteres(comboTiposIdentificacion.getSelectedItem().toString().toUpperCase());
        cantidadCaracteres = NovusUtils.obtenerLimiteCaracteres(comboTiposIdentificacion.getSelectedItem().toString().toUpperCase());
        teclado.habilitarAlfanumeric(NovusUtils.habilitarDosPunto(caracteresPermitidos));
        teclado.habilitarPunto(NovusUtils.habilitarPunto(caracteresPermitidos));
        teclado.habilitarDosPuntos(NovusUtils.habilitarDosPunto(caracteresPermitidos));

    }//GEN-LAST:event_comboTiposIdentificacionActionPerformed

    private void btnSeleccionMedioPagoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSeleccionMedioPagoMouseReleased
        mostrarPanelMediosPago();
    }//GEN-LAST:event_btnSeleccionMedioPagoMouseReleased

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        consultar();
    }//GEN-LAST:event_txtClienteActionPerformed

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
    }//GEN-LAST:event_jLabel4MousePressed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField1KeyTyped
        String caracteresAceptados = "[A-Z0-9a-z]";
        NovusUtils.limitarCarateres(evt, jTextField1, 10, jLabel9, caracteresAceptados);
    }// GEN-LAST:event_jTextField1KeyTyped

    private void desactivarTeclado() {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel1;
        teclado.habilitarCaracteresEspeciales(true);
    }

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jPanel1MouseReleased
        // TODO add your handling code here:
    }

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField2FocusGained
//        jTextField2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
    }// GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField2FocusLost
//        jTextField2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
    }// GEN-LAST:event_jTextField2FocusLost

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField2ActionPerformed

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        if (this.ventaFidelizacion) {
            showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getId(),
                    "/com/firefuel/resources/btOk.png", true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (this.ocultarInputsFactura) {
            showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getId(),
                    "/com/firefuel/resources/btOk.png", true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            guardarDatosPrefactura();
        }
    }// GEN-LAST:event_jLabel4MouseReleased

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField3FocusGained
//        jTextField3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
    }// GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField3FocusLost
//        jTextField3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
    }// GEN-LAST:event_jTextField3FocusLost

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField3ActionPerformed

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        consultar();
    }// GEN-LAST:event_jLabel11MouseReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField2KeyReleased

    }// GEN-LAST:event_jTextField2KeyReleased

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField2KeyTyped
        validarSoloNumeros(evt, jTextField2);
    }// GEN-LAST:event_jTextField2KeyTyped

    private void jTextField3KeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField3KeyTyped
        validarSoloNumeros(evt, jTextField3);
    }// GEN-LAST:event_jTextField3KeyTyped

    private void jTextField1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTextField1MouseReleased
        // this.desactivarTeclado();
    }// GEN-LAST:event_jTextField1MouseReleased

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField1FocusGained
//        jTextField1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
        deshabilitarTeclas(true);
    }

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jTextField1FocusLost
//        jTextField1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
        deshabilitarTeclas(false);
    }

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MouseReleased
        NovusUtils.beep();
        if (this.habilitarGestionarVenta != null) {
            this.habilitarGestionarVenta.run();
        }
        cancelar();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnSeleccionMedioPago;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox1;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox2;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox3;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox4;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox5;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox6;
    public javax.swing.JComboBox<String> comboTiposIdentificacion;
    private javax.swing.JPanel contenedorDatos;
    private javax.swing.JPanel datosFactura;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    public static javax.swing.JTextField jTextField1;
    public static javax.swing.JTextField jTextField2;
    public static javax.swing.JTextField jTextField3;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelSeleccionMedioPago;
    private javax.swing.JPanel pnlKilometrajeyNumeroComorbante;
    private javax.swing.JPanel pnlPlatayMediosPagos;
    private javax.swing.JPanel pnlPrincipal;
    public static javax.swing.JTextField txtCliente;
    // End of variables declaration//GEN-END:variables
    private void cancelar() {
        if (this.parent != null) {
            this.parent.setVisible(true);
            this.dispose();
        } else {
            this.dispose();
        }
    }

    private void desactivarBotonoGestionarVenta() {
        if (deshabilitarGestionarVenta != null) {
            deshabilitarGestionarVenta.run();
            deshabilitarGestionarVenta = null;
        }
    }

    private void mostrarPanelMediosPago() {
        try {
            limpiarSeleccion();
            PanelMediosPago panelMedios = new PanelMediosPago(this::mostrarMenuPrincipal,
                    this::mostrarPanelPrincipalContinuar, datafonos, () -> {
                        cancelar();
                        desactivarBotonoGestionarVenta();
                    }, this.manguera);
            mostrarSubPanel(panelMedios);
        } catch (SQLException ex) {
            Logger.getLogger(VentaCursoPlaca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final Pattern PATTERN = Pattern
            .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private void deshabilitarTeclas(boolean activar) {
        TecladoExtendidoGray tec = (TecladoExtendidoGray) jPanel1;
        tec.habilitarAlfanumeric(activar);
        tec.habilitarPunto(activar);
        tec.habilitarCaracteresEspeciales(false);
    }

    private boolean inFuel(boolean mostrarMensaje) {
        SurtidorDao dao = new SurtidorDao();
        int estadoPump = dao.getStatusHose(manguera.getId());
        if (estadoPump != 3) {
            if (mostrarMensaje) {
                mostrarMensaje("VENTA FINALIZADA");
            }
            setTimeout(3, () -> {
                cancelar();
            });
            return false;
        }
        return true;
    }

    private void guardarDatosPrefactura() {
        VentaCursoBean ventaCursoBean = Main.ventaCursoDAO.atributosVentaCurso(manguera.getCara());
        Pattern pat = Pattern.compile("[A-Z0-9a-z]{3,10}");

        String placa;
        String odometro;
        String comprobante;

        if (ventaCursoBean == null) {
            placa = jTextField1.getText();
            odometro = jTextField2.getText();
            comprobante = jTextField3.getText();
        } else {
            placa = !ventaCursoBean.getPlaca().isEmpty()
                    ? ventaCursoBean.getPlaca() : jTextField1.getText();
            odometro = !ventaCursoBean.getOdometro().isEmpty()
                    ? ventaCursoBean.getOdometro() : jTextField2.getText();
            comprobante = !ventaCursoBean.getNumeroComprobante().isEmpty()
                    ? ventaCursoBean.getNumeroComprobante() : jTextField3.getText();
        }

        Matcher mat = pat.matcher(placa);
        if (!placa.isEmpty() && !mat.matches()) {
            mostrarMensaje("PLACA INVALIDA");
            return;
        }

        jLabel5.setText("");
        SurtidorDao dao = new SurtidorDao();
        if (!inFuel(true)) {
            return;
        }
        MediosPagosBean medio = Concurrents.medioPagoImage.get(idMedioPagoSeleccionado(manguera.getCara()));
        if (medio == null) {
            jLabel5.setText("SELECCIONE UNA MANGUERA DE LA CARA");
        } else {
            try {
                jLabel5.setText("");

                JsonObject json = new JsonObject();
                json.addProperty("medio_pago", medio.getId());
                json.addProperty("numero_comprobante", comprobante);
                json.addProperty("placa", placa);
                json.addProperty("odometro", odometro);
                ReciboExtended recibo = new ReciboExtended();
                recibo.setSurtidor(manguera.getSurtidor() + "");
                recibo.setManguera(manguera.getId() + "");
                recibo.setCara(manguera.getCara() + "");


                JsonObject mang = new JsonObject();
                mang.addProperty("surtidor", this.manguera.getSurtidor());
                mang.addProperty("id", this.manguera.getId());
                mang.addProperty("cara", this.manguera.getCara());

                dao.updateVentasEncurso(recibo, json, 2);
                showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getId(),
                        "/com/firefuel/resources/btOk.png", true,
                        this::cancelar, true,
                        LetterCase.FIRST_UPPER_CASE);

            } catch (Exception ex) {
                Logger.getLogger(VentaCursoPlaca.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private long idMedioPagoSeleccionado(int cara) {
        long idMedio = 1;
        if (render.getIdMedioPago() != 0) {
            idMedio = render.getIdMedioPago();
        } else {
            idMedio = getIdMedioPagoVentaCursoUseCase.execute(cara);
        }
        return idMedio;
    }

    private MediosPagosBean getMediosByString(String mediotexto) {
        MediosPagosBean medio = null;
        for (MediosPagosBean mediosPago : mediosPagos) {
            if (mediotexto.equals(mediosPago.getDescripcion().toUpperCase())) {
                medio = mediosPago;
                break;
            }
        }
        return medio;
    }

    private JsonObject informacionDatafono() {
        JsonObject data = new JsonObject();
        for (JsonElement objDatafonos : datafonos) {
            JsonObject dataitemDatafono = objDatafonos.getAsJsonObject();
            data.addProperty("proveedor", dataitemDatafono.get("proveedor").getAsString());
            data.addProperty("codigoDatafono", dataitemDatafono.get("codigo_terminal").getAsString());
            data.addProperty("negocio", "COM");
            data.addProperty("codigoAutorizacion", mdao.getCodigoAutorizacion());
            data.addProperty("promotor", Main.persona.getId());
            data.addProperty("proveedorId", dataitemDatafono.get("id_adquiriente").getAsInt());
            data.addProperty("pos", Main.credencial.getEquipos_id() + "");
            data.addProperty("tipoVenta", "UNICO");
        }
        return data;
    }

    private void validarSoloNumeros(KeyEvent evt, JTextField campo) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        } else {
            if (campo.getText().length() >= 12) {
                evt.consume();
                mostrarMensaje("MAXIMO 12 CARACTERES");
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        jLabel9.setText(NovusUtils.convertMessage(
                LetterCase.FIRST_UPPER_CASE,
                mensaje));
        jLabel9.setVisible(true);
        setTimeout(3, () -> {
            jLabel9.setText("");
            jLabel9.setVisible(false);
        });
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

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
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

    private void mostrarPanel(String panel) {
        CardLayout cardLayout = (CardLayout) pnlPrincipal.getLayout();
        cardLayout.show(pnlPrincipal, panel);
    }

    private void mostrarPanelPrincipalContinuar() {
        modificarBotonSeleccionMediosPago();
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

    public void modificarBotonSeleccionMediosPago() {
        if (!render.getDescripcionMedioPago().isEmpty()) {
            btnSeleccionMedioPago.setText(render.getDescripcionMedioPago());
        } else {
            btnSeleccionMedioPago.setText("Seleccione Medio de Pago");
        }
    }

    private void cerrar() {
        this.dispose();
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


    private void ocultarComponentes(boolean ventaFidelizacion) {
        contenedorDatos.setVisible(!ventaFidelizacion);
        txtCliente.setBounds(140, 150, 460, 90);
        jLabel15.setBounds(150, 120, 450, 28);
        comboTiposIdentificacion.setBounds(620, 150, 540, 90);
        jLabel2.setBounds(620, 120, 450, 28);
        jPanel1.setBounds(130, 300, 1024, 336);
    }

    public void limpiarSeleccion() {
        render.setIdMedioPago(0);
        render.setDescripcionMedioPago("");
        RenderPanelPlacasGopass.setValorPlacaSeleccionada("");
        modificarBotonSeleccionMediosPago();
    }

    private void consultar() {
        if (txtCliente.getText().isEmpty()) {
            pnlPrincipal.add(datosFactura, "pnl_principal");
            showMessage("INGRESE UN DOCUMENTO",
                    "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            VentasCurso ventasCurso = new VentasCurso();
            ReciboExtended recibo = new ReciboExtended();
            recibo.setSurtidor(manguera.getSurtidor() + "");
            recibo.setManguera(manguera.getId() + "");
            recibo.setCara(manguera.getCara() + "");
            Runnable regresar = () -> ventasCurso.cargarPanelExterno(pnlPrincipal, datosFactura);
            
            // Verificar si es una venta GLP para desactivar fidelización
            boolean esVentaGLP = esVentaGLP();
            
            FidelizacionyFacturacionElectronica fidelizacionyFacturacoinElectronica = new FidelizacionyFacturacionElectronica(recibo, txtCliente.getText(), comboTiposIdentificacion.getSelectedItem().toString(), regresar, this.ventaFidelizacion);
            
            // Si es venta GLP, desactivar la fidelización
            if (esVentaGLP) {
                fidelizacionyFacturacoinElectronica.deshabilitarFidelizacionParaGLP();
            }
            
            ventasCurso.cargarPanelExterno(pnlPrincipal, fidelizacionyFacturacoinElectronica);
        }
    }

    private void deshabilitarPlaca(String placaComunidades, boolean comunidades) {
        if (comunidades) {
            jTextField1.setEnabled(false);
            jTextField1.setText(placaComunidades);
        }
    }

    /**
     * Verifica si la venta actual es de tipo GLP basándose en el producto de la manguera
     * @return true si es GLP, false en caso contrario
     */
    private boolean esVentaGLP() {
        try {
            if (this.manguera != null && this.manguera.getCara() > 0) {
                NovusUtils.printLn("🔍 [GLP] Validando producto para cara VentaCursoPlaca: " + this.manguera.getCara());
                
                // Obtener el ID del producto desde la consulta
                long productoId = new GetMovimientoIdDesdeCaraUseCase().execute(this.manguera.getCara());
                NovusUtils.printLn("🔍 [GLP] Producto ID obtenido VentaCursoPlaca: " + productoId);
                
                if (productoId > 0) {
                    // Obtener la descripción del producto
                    String descripcionProducto = new GetProductNameUseCase(productoId).execute();
                    NovusUtils.printLn("🔍 [GLP] Descripción producto VentaCursoPlaca: '" + descripcionProducto + "'");
                    
                    if (descripcionProducto != null && !descripcionProducto.trim().isEmpty()) {
                        // Verificar si la descripción contiene "GLP" (case insensitive)
                        boolean esGLP = descripcionProducto.toUpperCase().contains("GLP");
                        NovusUtils.printLn("🔍 [GLP] ¿Es producto GLP? VentaCursoPlaca: " + esGLP);
                        return esGLP;
                    } else {
                        NovusUtils.printLn("⚠️ [GLP] Descripción de producto vacía o nula VentaCursoPlaca");
                    }
                } else {
                    NovusUtils.printLn("⚠️ [GLP] ID de producto no válido VentaCursoPlaca: " + productoId);
                }
            } else {
                NovusUtils.printLn("⚠️ [GLP] Manguera o cara no válida VentaCursoPlaca");
            }
            
            NovusUtils.printLn("⚠️ [GLP] No se pudo determinar el tipo de producto - asumiendo NO es GLP VentaCursoPlaca");
            return false;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ [ERROR GLP] Error al verificar tipo de producto VentaCursoPlaca: " + e.getMessage());
            return false;
        }
    }

}

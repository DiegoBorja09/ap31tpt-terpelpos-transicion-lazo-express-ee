/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.Notificador;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.anulacion.AnulacionDao;
import com.facade.anulacion.AnulacionProductos;
import com.firefuel.facturacion.electronica.MotivosAnulacion;
import com.firefuel.facturacion.electronica.NotasCredito;
import com.firefuel.facturacion.electronica.anulacion.AnulacionParcial;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import com.utils.enums.TipoAnulacion;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.awt.Color;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import teclado.view.common.TecladoNumericoGrayDot;

public class ConfirmarAnulacionView extends javax.swing.JDialog {

    Recibo recibo;
    InfoViewController parent;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    final static int ANULACION_VENTA = TipoAnulacion.DEVOLUCION_DE_BIENES_O_PARTES;
    final static int FACTURACION_ELECTRONICA_ANULACION = 2;
    final static int REBAJA_DESCUENTO = 3;
    final static int AJUSTE_PRECIO = 4;
    final static int OTROS = 5;
    public static int DOCUMENTO_VALIDAR;
    long tipoAnulacion = ANULACION_VENTA;
    int CONSECUTIVOS;
    String Prefijo;
    boolean isCombustible;
    JDialog historial;
    public static Notificador notificadorView = null;
    Runnable panelPrincipal;
    AnulacionParcial anulacionParcial;
    Icon botonInactivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"));
    Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"));

    public ConfirmarAnulacionView(InfoViewController parent, boolean modal, Recibo recibo) {
        super(parent, modal);
        this.parent = parent;
        this.recibo = recibo;
        initComponents();
        this.init();

    }

    public ConfirmarAnulacionView(InfoViewController parent, boolean modal, Recibo recibo, int consecutivo, String prefijo, boolean isCombustible, JDialog historial, Runnable panelPrincipal) {
        super(parent, modal);
        this.parent = parent;
        this.recibo = recibo;
        this.CONSECUTIVOS = consecutivo;
        this.Prefijo = prefijo;
        this.historial = historial;
        this.isCombustible = isCombustible;
        this.panelPrincipal = panelPrincipal;
        initComponents();
        MotivosAnulacion motivo = new MotivosAnulacion(this, isCombustible);
        motivo.cargarMotivosAnulacion(jPanel2);
        this.init();

    }

    private void init() {
        anulacionParcial = new AnulacionParcial(this);
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        notificadorView = this::recibir;
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane3.getViewport().setOpaque(false);
        Border border = BorderFactory.createEmptyBorder();
        jScrollPane1.setViewportBorder(border);
        jScrollPane3.setViewportBorder(border);
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(153, 0, 0);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(204, 204, 204));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(204, 204, 204));
        jScrollPane3.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(153, 0, 0);
            }
        });
        jScrollPane3.getVerticalScrollBar().setBackground(new Color(204, 204, 204));
        jScrollPane3.getHorizontalScrollBar().setBackground(new Color(204, 204, 204));
        lblAnularParcial.setEnabled(false);
    }

    void recibir(JsonObject data) {
        if (data.get("autoclose").getAsBoolean()) {
            showMessage(data.get("mensajeError").getAsString(),
                    data.get("icono").getAsString(), 
                    data.get("habilitar").getAsBoolean(), this::cerrar, 
                    data.get("autoclose").getAsBoolean(), LetterCase.FIRST_UPPER_CASE);
        } else {
            showMessage(data.get("mensajeError").getAsString(), 
                    data.get("icono").getAsString(), 
                    false, null, 
                    false,LetterCase.FIRST_UPPER_CASE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        panelPadreAnulacion = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        panelAccionesCambioCantidad = new javax.swing.JPanel();
        pnlDetalleVenta = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel9 = new javax.swing.JLabel();
        pnlEncabezazdo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlContainerProductosDetalle = new javax.swing.JPanel();
        teclado = new javax.swing.JPanel();
        jPanel8 = new TecladoNumericoGrayDot();
        txtCantidad = new javax.swing.JTextField();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel18 = new javax.swing.JLabel();
        pnlDetalleAnulaciones = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel10 = new javax.swing.JLabel();
        pnlEncabezazdo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlContainerProductosAnulacion = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        pnlAcciones = new javax.swing.JPanel();
        pnlAccion1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pnlAccion2 = new javax.swing.JPanel();
        lblAtras = new javax.swing.JLabel();
        lblAnularParcial = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTitle1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel6.setLayout(null);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel6.add(jLabel27);
        jLabel27.setBounds(90, 10, 10, 68);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CONFIRMACIÓN ANULACIÓN VENTA");
        jPanel6.add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back.png"))); // NOI18N
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel6MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel6);
        jLabel6.setBounds(10, 10, 48, 49);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel6.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        panelPadreAnulacion.setBackground(new java.awt.Color(255, 255, 255));
        panelPadreAnulacion.setLayout(new java.awt.CardLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("TIPO ANULACIÓN:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(null);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setAutoscrolls(true);
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));
        jScrollPane2.setViewportView(jPanel2);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 1220, 490));

        panelPadreAnulacion.add(jPanel3, "motivosAnulacion");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(null);

        panelAccionesCambioCantidad.setBackground(new java.awt.Color(255, 255, 255));
        panelAccionesCambioCantidad.setLayout(new java.awt.CardLayout());

        pnlDetalleVenta.setBackground(new java.awt.Color(204, 204, 204));
        pnlDetalleVenta.setForeground(new java.awt.Color(204, 204, 204));
        pnlDetalleVenta.setRoundBottomLeft(20);
        pnlDetalleVenta.setRoundBottomRight(20);
        pnlDetalleVenta.setRoundTopLeft(20);
        pnlDetalleVenta.setRoundTopRight(20);
        pnlDetalleVenta.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Detalle de venta");
        pnlDetalleVenta.add(jLabel9);
        jLabel9.setBounds(0, 0, 600, 50);

        pnlEncabezazdo.setBackground(new java.awt.Color(153, 0, 0));
        pnlEncabezazdo.setRoundBottomLeft(20);
        pnlEncabezazdo.setRoundBottomRight(20);
        pnlEncabezazdo.setRoundTopLeft(20);
        pnlEncabezazdo.setRoundTopRight(20);
        pnlEncabezazdo.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Valor");
        pnlEncabezazdo.add(jLabel3);
        jLabel3.setBounds(339, 0, 180, 60);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Producto");
        pnlEncabezazdo.add(jLabel7);
        jLabel7.setBounds(0, 0, 230, 60);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Cantidad");
        pnlEncabezazdo.add(jLabel8);
        jLabel8.setBounds(230, 0, 110, 60);

        pnlDetalleVenta.add(pnlEncabezazdo);
        pnlEncabezazdo.setBounds(10, 50, 580, 60);

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        pnlContainerProductosDetalle.setBackground(new java.awt.Color(204, 204, 204));
        pnlContainerProductosDetalle.setOpaque(false);
        jScrollPane1.setViewportView(pnlContainerProductosDetalle);

        pnlDetalleVenta.add(jScrollPane1);
        jScrollPane1.setBounds(0, 120, 600, 430);

        panelAccionesCambioCantidad.add(pnlDetalleVenta, "detalleVenta");

        teclado.setBackground(new java.awt.Color(255, 255, 255));
        teclado.setLayout(null);
        teclado.add(jPanel8);
        jPanel8.setBounds(100, 115, 400, 440);

        txtCantidad.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtCantidad.setForeground(new java.awt.Color(51, 51, 51));
        txtCantidad.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActionPerformed(evt);
            }
        });
        teclado.add(txtCantidad);
        txtCantidad.setBounds(100, 40, 400, 60);

        panelRedondo1.setBackground(new java.awt.Color(153, 0, 0));
        panelRedondo1.setRoundBottomLeft(50);
        panelRedondo1.setRoundBottomRight(50);
        panelRedondo1.setRoundTopLeft(50);
        panelRedondo1.setRoundTopRight(50);
        panelRedondo1.setLayout(null);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("X");
        jLabel18.setToolTipText("");
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
        });
        panelRedondo1.add(jLabel18);
        jLabel18.setBounds(0, -1, 50, 50);

        teclado.add(panelRedondo1);
        panelRedondo1.setBounds(520, 0, 50, 50);

        panelAccionesCambioCantidad.add(teclado, "teclado");

        jPanel5.add(panelAccionesCambioCantidad);
        panelAccionesCambioCantidad.setBounds(0, 0, 600, 560);

        pnlDetalleAnulaciones.setBackground(new java.awt.Color(204, 204, 204));
        pnlDetalleAnulaciones.setForeground(new java.awt.Color(204, 204, 204));
        pnlDetalleAnulaciones.setRoundBottomLeft(20);
        pnlDetalleAnulaciones.setRoundBottomRight(20);
        pnlDetalleAnulaciones.setRoundTopLeft(20);
        pnlDetalleAnulaciones.setRoundTopRight(20);
        pnlDetalleAnulaciones.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Anulación Productos");
        pnlDetalleAnulaciones.add(jLabel10);
        jLabel10.setBounds(0, 0, 600, 50);

        pnlEncabezazdo1.setBackground(new java.awt.Color(153, 0, 0));
        pnlEncabezazdo1.setRoundBottomLeft(20);
        pnlEncabezazdo1.setRoundBottomRight(20);
        pnlEncabezazdo1.setRoundTopLeft(20);
        pnlEncabezazdo1.setRoundTopRight(20);
        pnlEncabezazdo1.setLayout(null);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Valor");
        pnlEncabezazdo1.add(jLabel13);
        jLabel13.setBounds(339, 0, 180, 60);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Producto");
        pnlEncabezazdo1.add(jLabel14);
        jLabel14.setBounds(0, 0, 230, 60);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Cantidad");
        pnlEncabezazdo1.add(jLabel15);
        jLabel15.setBounds(230, 0, 110, 60);

        pnlDetalleAnulaciones.add(pnlEncabezazdo1);
        pnlEncabezazdo1.setBounds(10, 50, 580, 60);

        jScrollPane3.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane3.setBorder(null);
        jScrollPane3.setOpaque(false);

        pnlContainerProductosAnulacion.setBackground(new java.awt.Color(204, 204, 204));
        pnlContainerProductosAnulacion.setOpaque(false);
        jScrollPane3.setViewportView(pnlContainerProductosAnulacion);

        pnlDetalleAnulaciones.add(jScrollPane3);
        jScrollPane3.setBounds(0, 120, 600, 430);

        jPanel5.add(pnlDetalleAnulaciones);
        pnlDetalleAnulaciones.setBounds(620, 0, 600, 560);

        panelPadreAnulacion.add(jPanel5, "AnulacionParcial");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/IconoLoader.gif"))); // NOI18N
        jPanel1.add(jLabel16);
        jLabel16.setBounds(390, 80, 410, 210);

        jLabel17.setBackground(new java.awt.Color(51, 51, 51));
        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("CARGANDO");
        jPanel1.add(jLabel17);
        jLabel17.setBounds(360, 330, 470, 80);

        panelPadreAnulacion.add(jPanel1, "cargando");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(null);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader_fac.gif"))); // NOI18N
        jPanel4.add(jLabel19);
        jLabel19.setBounds(190, 180, 340, 210);

        jLabel20.setBackground(new java.awt.Color(51, 51, 51));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(186, 12, 47));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("POR FAVOR, ESPERE UN MOMENTO.");
        jPanel4.add(jLabel20);
        jLabel20.setBounds(550, 255, 640, 80);

        panelPadreAnulacion.add(jPanel4, "cargandoAnulacionTotal");

        jPanel6.add(panelPadreAnulacion);
        panelPadreAnulacion.setBounds(30, 110, 1230, 570);

        pnlAcciones.setOpaque(false);
        pnlAcciones.setLayout(new java.awt.CardLayout());

        pnlAccion1.setOpaque(false);
        pnlAccion1.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        jLabel4.setText("ANULAR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        pnlAccion1.add(jLabel4);
        jLabel4.setBounds(160, 10, 180, 60);

        pnlAcciones.add(pnlAccion1, "accionAnularPrincipal");

        pnlAccion2.setOpaque(false);
        pnlAccion2.setLayout(null);

        lblAtras.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        lblAtras.setForeground(new java.awt.Color(255, 255, 255));
        lblAtras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAtras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        lblAtras.setText("ATRAS");
        lblAtras.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblAtras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAtrasMouseReleased(evt);
            }
        });
        pnlAccion2.add(lblAtras);
        lblAtras.setBounds(10, 10, 170, 60);

        lblAnularParcial.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        lblAnularParcial.setForeground(new java.awt.Color(255, 255, 255));
        lblAnularParcial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAnularParcial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        lblAnularParcial.setText("ANULAR");
        lblAnularParcial.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblAnularParcial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAnularParcialMouseReleased(evt);
            }
        });
        pnlAccion2.add(lblAnularParcial);
        lblAnularParcial.setBounds(190, 10, 170, 60);

        pnlAcciones.add(pnlAccion2, "accionAnularSecundaria");

        jPanel6.add(pnlAcciones);
        pnlAcciones.setBounds(750, 710, 370, 80);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jPanel6.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel6.add(jLabel28);
        jLabel28.setBounds(90, 10, 10, 68);

        jTitle1.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle1.setForeground(new java.awt.Color(255, 255, 255));
        jTitle1.setText("CONFIRMACION ANULACION VENTA");
        jPanel6.add(jTitle1);
        jTitle1.setBounds(120, 15, 720, 50);

        jLabel11.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel11.setText("ANULAR");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel11);
        jLabel11.setBounds(940, 720, 180, 60);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel12MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel12MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel12);
        jLabel12.setBounds(10, 10, 70, 71);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel6.add(jLabel30);
        jLabel30.setBounds(1130, 710, 10, 80);

        jLabel21.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setText("TIPO ANULACION:");
        jPanel6.add(jLabel21);
        jLabel21.setBounds(0, 0, 175, 26);

        jLabel22.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel22.setText("SIGUIENTE");
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel22MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel22);
        jLabel22.setBounds(0, 0, 173, 54);

        jLabel23.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(186, 12, 47));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("<html>REVERTIR INVENTARIO:</html>");
        jPanel6.add(jLabel23);
        jLabel23.setBounds(0, 0, 232, 26);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        jPanel6.add(jLabel2);
        jLabel2.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel6, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel11MouseReleased

    private void jLabel12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MousePressed

    private void jLabel12MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MouseReleased

    private void jLabel22MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel22MouseReleased

    private void lblAtrasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAtrasMouseReleased
        cambiarPanel("motivosAnulacion", panelPadreAnulacion);
        cambiarPanel("accionAnularPrincipal", pnlAcciones);
    }//GEN-LAST:event_lblAtrasMouseReleased

    private void lblAnularParcialMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAnularParcialMouseReleased
        if (lblAnularParcial.isEnabled()) {
            anulacionParcial.anular(recibo.getNumero());
        }

    }//GEN-LAST:event_lblAnularParcialMouseReleased

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
        cambiarPanel("detalleVenta", panelAccionesCambioCantidad);
    }//GEN-LAST:event_jLabel18MouseClicked

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        anulacionParcial.editarCantidad();
        cambiarPanel("detalleVenta", panelAccionesCambioCantidad);
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MousePressed

    }// GEN-LAST:event_jLabel6MousePressed

    private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel6MouseReleased

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        anular();
    }// GEN-LAST:event_jLabel4MouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    public javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jTitle1;
    public javax.swing.JLabel lblAnularParcial;
    public javax.swing.JLabel lblAtras;
    public javax.swing.JPanel panelAccionesCambioCantidad;
    private javax.swing.JPanel panelPadreAnulacion;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    public javax.swing.JPanel pnlAccion1;
    public javax.swing.JPanel pnlAccion2;
    public javax.swing.JPanel pnlAcciones;
    public javax.swing.JPanel pnlContainerProductosAnulacion;
    public javax.swing.JPanel pnlContainerProductosDetalle;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlDetalleAnulaciones;
    public com.firefuel.components.panelesPersonalizados.PanelRedondo pnlDetalleVenta;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlEncabezazdo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlEncabezazdo1;
    private javax.swing.JPanel pnlPrincipal;
    public javax.swing.JPanel teclado;
    public javax.swing.JTextField txtCantidad;
    // End of variables declaration//GEN-END:variables

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

    public void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

    public void cerrar() {
        if (this.historial != null) {
            historial.setVisible(true);
            this.dispose();
            if (panelPrincipal != null) {
                panelPrincipal.run();
            }
        } else {
            this.dispose();
        }
    }

    private void anular() {
        if (NovusConstante.TIPO_ANULACION_NOTA_CREDITO == TipoAnulacion.DEVOLUCION_DE_BIENES_O_PARTES && !isCombustible) {
            cambiarPanel("cargando", panelPadreAnulacion);
            pnlAcciones.setVisible(false);
            Runnable cargarInfo = () -> {
                anulacionParcial.listarProductos(recibo.getNumero());
                cambiarPanel("AnulacionParcial", panelPadreAnulacion);
                cambiarPanel("accionAnularSecundaria", pnlAcciones);
                pnlAcciones.setVisible(true);
            };
            CompletableFuture.runAsync(cargarInfo);
        } else {
            cambiarPanel("cargandoAnulacionTotal", panelPadreAnulacion);
            pnlAcciones.setVisible(false);
            Runnable anularVenta = () -> {
                AnulacionDao anulacionDao = new AnulacionDao();
                ArrayList<AnulacionProductos> productos = (ArrayList<AnulacionProductos>) anulacionDao.obtenerProductos(recibo.getNumero());
                anulacionParcial.listarProductosAnular(productos);
                anulacionParcial.anular(recibo.getNumero());
                pnlAcciones.setVisible(true);
            };
            CompletableFuture.runAsync(anularVenta);
        }
    }

    public void cambiarPanel(String nombrePanel, JPanel panel) {
        CardLayout card = (CardLayout) panel.getLayout();
        card.show(panel, nombrePanel);
    }

}

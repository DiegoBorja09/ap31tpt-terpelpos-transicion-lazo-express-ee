package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.AuditoriaPromotor;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ReporteVentasPromotor extends javax.swing.JDialog {

    InfoViewController base;
    MovimientosDao mdao = new MovimientosDao();
    long id = 0l;
    JsonObject sobres = new JsonObject();
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;

    static DecimalFormat dff = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);

    String promotor = "";
    Date fecha;
    String totalCombustible = "0";
    String totalComplementarios = "0";
    String totalCanastilla = "0";
    String totalKiosco = "0";
    String totalSobres = "0";

    JsonArray medios = new JsonArray();
    
    // Cola de impresión para archivo TXT (mismo patrón que VentasHistorialView)
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;

    public ReporteVentasPromotor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        initTable();
    }

    private void init() {
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);
        setLocationRelativeTo(null);
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        this.renderizarUsuarios();
    }

    private void initTable() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jTable1.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jTable1.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jTable1.setRowSorter(rowSorter);

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pnlInfoPromotor = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        btnAtras = new javax.swing.JLabel();
        jNotificacion1 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pnlContenedorTabla = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pnlTotalVentasPorNegocio = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblCombustible = new javax.swing.JLabel();
        txtTotalCombustible = new javax.swing.JLabel();
        lblCanastilla = new javax.swing.JLabel();
        txtTotalCanastilla = new javax.swing.JLabel();
        lblKiosco = new javax.swing.JLabel();
        txtTotalKiosco = new javax.swing.JLabel();
        lblCalibraciones = new javax.swing.JLabel();
        txtTotalSobres = new javax.swing.JLabel();
        lblSobres = new javax.swing.JLabel();
        txtTotalCalibraciones = new javax.swing.JLabel();
        lblComplementarios = new javax.swing.JLabel();
        txtTotalComplementarios = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtPromotor = new javax.swing.JLabel();
        pnlActualizar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnActualizar = new javax.swing.JLabel();
        pnlImprimirDetallado = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnImprimirDetallado = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("IDENTIFICACION");
        pnl_principal.add(jLabel6);
        jLabel6.setBounds(310, 90, 180, 40);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PROMOTOR");
        pnl_principal.add(jLabel8);
        jLabel8.setBounds(520, 90, 480, 40);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 610));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        pnl_principal.add(jScrollPane1);
        jScrollPane1.setBounds(240, 120, 840, 580);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("SELECCIONE EL PROMOTOR");
        pnl_principal.add(jLabel4);
        jLabel4.setBounds(140, 720, 980, 70);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("REPORTE VENTAS PROMOTOR");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(100, 0, 680, 90);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel7);
        jLabel7.setBounds(20, 20, 48, 49);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel5);
        jLabel5.setBounds(0, 0, 1281, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        pnlInfoPromotor.setLayout(null);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlInfoPromotor.add(logo);
        logo.setBounds(10, 710, 100, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlInfoPromotor.add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        btnAtras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAtras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back.png"))); // NOI18N
        btnAtras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnAtrasMousePressed(evt);
            }
        });
        pnlInfoPromotor.add(btnAtras);
        btnAtras.setBounds(10, 10, 48, 49);

        jNotificacion1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion1.setForeground(new java.awt.Color(255, 255, 255));
        pnlInfoPromotor.add(jNotificacion1);
        jNotificacion1.setBounds(150, 720, 560, 70);

        txtTitulo.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        txtTitulo.setForeground(new java.awt.Color(255, 255, 255));
        txtTitulo.setText("REPORTE VENTAS");
        pnlInfoPromotor.add(txtTitulo);
        txtTitulo.setBounds(100, 0, 720, 90);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlInfoPromotor.add(jLabel32);
        jLabel32.setBounds(80, 10, 10, 68);

        jPanel3.setBackground(new java.awt.Color(220, 220, 220));
        jPanel3.setLayout(null);

        pnlContenedorTabla.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedorTabla.setForeground(new java.awt.Color(255, 255, 255));
        pnlContenedorTabla.setRoundBottomLeft(30);
        pnlContenedorTabla.setRoundBottomRight(30);
        pnlContenedorTabla.setRoundTopLeft(30);
        pnlContenedorTabla.setRoundTopRight(30);
        pnlContenedorTabla.setLayout(null);

        jScrollPane2.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MEDIO", "COMBUSTIBLE", "CANASTILLA", "MARKET", "COMPLEMENTARIO", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(40);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(94);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(94);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(94);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(94);
        }

        pnlContenedorTabla.add(jScrollPane2);
        jScrollPane2.setBounds(10, 20, 860, 420);

        jPanel3.add(pnlContenedorTabla);
        pnlContenedorTabla.setBounds(10, 95, 880, 460);

        pnlTotalVentasPorNegocio.setBackground(new java.awt.Color(255, 255, 255));
        pnlTotalVentasPorNegocio.setRoundBottomLeft(30);
        pnlTotalVentasPorNegocio.setRoundBottomRight(30);
        pnlTotalVentasPorNegocio.setRoundTopLeft(30);
        pnlTotalVentasPorNegocio.setRoundTopRight(30);
        pnlTotalVentasPorNegocio.setLayout(null);

        lblCombustible.setBackground(new java.awt.Color(0, 0, 0));
        lblCombustible.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCombustible.setText("COMBUSTIBLE:");
        pnlTotalVentasPorNegocio.add(lblCombustible);
        lblCombustible.setBounds(10, 100, 120, 40);

        txtTotalCombustible.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        txtTotalCombustible.setForeground(new java.awt.Color(204, 0, 0));
        txtTotalCombustible.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalCombustible.setText("$0");
        pnlTotalVentasPorNegocio.add(txtTotalCombustible);
        txtTotalCombustible.setBounds(180, 100, 170, 40);

        lblCanastilla.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCanastilla.setText("CANASTILLA:");
        pnlTotalVentasPorNegocio.add(lblCanastilla);
        lblCanastilla.setBounds(10, 140, 110, 50);

        txtTotalCanastilla.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        txtTotalCanastilla.setForeground(new java.awt.Color(204, 0, 0));
        txtTotalCanastilla.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalCanastilla.setText("$0");
        pnlTotalVentasPorNegocio.add(txtTotalCanastilla);
        txtTotalCanastilla.setBounds(180, 140, 170, 50);

        lblKiosco.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKiosco.setText("MARKET:");
        pnlTotalVentasPorNegocio.add(lblKiosco);
        lblKiosco.setBounds(10, 190, 90, 50);

        txtTotalKiosco.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        txtTotalKiosco.setForeground(new java.awt.Color(204, 0, 0));
        txtTotalKiosco.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalKiosco.setText("$0");
        pnlTotalVentasPorNegocio.add(txtTotalKiosco);
        txtTotalKiosco.setBounds(180, 190, 170, 50);

        lblCalibraciones.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCalibraciones.setText("TOTAL SOBRES:");
        pnlTotalVentasPorNegocio.add(lblCalibraciones);
        lblCalibraciones.setBounds(10, 290, 120, 50);

        txtTotalSobres.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        txtTotalSobres.setForeground(new java.awt.Color(204, 0, 0));
        txtTotalSobres.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalSobres.setText("$0");
        pnlTotalVentasPorNegocio.add(txtTotalSobres);
        txtTotalSobres.setBounds(180, 290, 170, 50);

        lblSobres.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSobres.setText("TOTAL CALIBRACIONES:");
        pnlTotalVentasPorNegocio.add(lblSobres);
        lblSobres.setBounds(10, 340, 170, 50);

        txtTotalCalibraciones.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        txtTotalCalibraciones.setForeground(new java.awt.Color(204, 0, 0));
        txtTotalCalibraciones.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalCalibraciones.setText("$0");
        pnlTotalVentasPorNegocio.add(txtTotalCalibraciones);
        txtTotalCalibraciones.setBounds(180, 340, 170, 50);

        lblComplementarios.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblComplementarios.setText("COMPLEMENTARIOS");
        pnlTotalVentasPorNegocio.add(lblComplementarios);
        lblComplementarios.setBounds(10, 240, 140, 50);

        txtTotalComplementarios.setFont(new java.awt.Font("Terpel Sans", 0, 18)); // NOI18N
        txtTotalComplementarios.setForeground(new java.awt.Color(204, 0, 0));
        txtTotalComplementarios.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalComplementarios.setText("$0");
        pnlTotalVentasPorNegocio.add(txtTotalComplementarios);
        txtTotalComplementarios.setBounds(180, 240, 170, 50);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 0, 0));
        jLabel11.setText("Total de ventas por negocio");
        pnlTotalVentasPorNegocio.add(jLabel11);
        jLabel11.setBounds(20, 40, 330, 40);

        jPanel3.add(pnlTotalVentasPorNegocio);
        pnlTotalVentasPorNegocio.setBounds(900, 95, 360, 460);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setText("PROMOTOR:");
        jPanel3.add(jLabel1);
        jLabel1.setBounds(30, 20, 150, 60);

        panelRedondo1.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo1.setRoundBottomLeft(30);
        panelRedondo1.setRoundBottomRight(30);
        panelRedondo1.setRoundTopLeft(30);
        panelRedondo1.setRoundTopRight(30);

        txtPromotor.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        txtPromotor.setForeground(new java.awt.Color(51, 51, 51));
        txtPromotor.setText("PROMOTOR");
        txtPromotor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelRedondo1Layout = new javax.swing.GroupLayout(panelRedondo1);
        panelRedondo1.setLayout(panelRedondo1Layout);
        panelRedondo1Layout.setHorizontalGroup(
            panelRedondo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRedondo1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPromotor, javax.swing.GroupLayout.PREFERRED_SIZE, 890, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(174, Short.MAX_VALUE))
        );
        panelRedondo1Layout.setVerticalGroup(
            panelRedondo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRedondo1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtPromotor, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.add(panelRedondo1);
        panelRedondo1.setBounds(190, 15, 1070, 70);

        pnlInfoPromotor.add(jPanel3);
        jPanel3.setBounds(0, 80, 1280, 630);

        pnlActualizar.setBackground(new java.awt.Color(220, 220, 220));
        pnlActualizar.setRoundBottomLeft(15);
        pnlActualizar.setRoundBottomRight(15);
        pnlActualizar.setRoundTopLeft(15);
        pnlActualizar.setRoundTopRight(15);
        pnlActualizar.setLayout(null);

        btnActualizar.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(204, 0, 0));
        btnActualizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnActualizar.setText("ACTUALIZAR");
        btnActualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnActualizarMouseReleased(evt);
            }
        });
        pnlActualizar.add(btnActualizar);
        btnActualizar.setBounds(0, 0, 250, 60);

        pnlInfoPromotor.add(pnlActualizar);
        pnlActualizar.setBounds(720, 730, 250, 60);

        pnlImprimirDetallado.setBackground(new java.awt.Color(220, 220, 220));
        pnlImprimirDetallado.setRoundBottomLeft(15);
        pnlImprimirDetallado.setRoundBottomRight(15);
        pnlImprimirDetallado.setRoundTopLeft(15);
        pnlImprimirDetallado.setRoundTopRight(15);
        pnlImprimirDetallado.setLayout(null);

        btnImprimirDetallado.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btnImprimirDetallado.setForeground(new java.awt.Color(204, 0, 0));
        btnImprimirDetallado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnImprimirDetallado.setText("IMPRIMIR DETALLADO");
        btnImprimirDetallado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirDetallado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnImprimirDetalladoMousePressed(evt);
            }
        });
        pnlImprimirDetallado.add(btnImprimirDetallado);
        btnImprimirDetallado.setBounds(0, 0, 280, 60);

        pnlInfoPromotor.add(pnlImprimirDetallado);
        pnlImprimirDetallado.setBounds(980, 730, 280, 60);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnlInfoPromotor.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnlInfoPromotor, "pnlInfoPromotor");
        pnlInfoPromotor.getAccessibleContext().setAccessibleName("");

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader_fac.gif"))); // NOI18N
        jLabel9.setToolTipText("");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 260, 300, 210));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 0, 0));
        jLabel10.setText("CARGANDO INFORMACION");
        jLabel10.setToolTipText("CARGANDO INFORMACION");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 280, 550, 170));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel2.setText("CARGANDO INFORMACION");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        pnl_container.add(jPanel2, "panel_cargando");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseReleased
        cerrar();
    }//GEN-LAST:event_jLabel7MouseReleased

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel5MousePressed

    private void btnAtrasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAtrasMousePressed
        showPanel("pnl_principal");
    }//GEN-LAST:event_btnAtrasMousePressed

    private void btnImprimirDetalladoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnImprimirDetalladoMousePressed
        NovusUtils.beep();
        imprimirReporte();
    }//GEN-LAST:event_btnImprimirDetalladoMousePressed

    private void btnActualizarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnActualizarMouseReleased
        NovusUtils.beep();
        obtenerVentasPromotor(id);
        actualizarReporte();
    }//GEN-LAST:event_btnActualizarMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnActualizar;
    private javax.swing.JLabel btnAtras;
    private javax.swing.JLabel btnImprimirDetallado;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel35;
    public javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblCalibraciones;
    private javax.swing.JLabel lblCanastilla;
    private javax.swing.JLabel lblCombustible;
    private javax.swing.JLabel lblComplementarios;
    private javax.swing.JLabel lblKiosco;
    private javax.swing.JLabel lblSobres;
    private javax.swing.JLabel logo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlActualizar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlContenedorTabla;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlImprimirDetallado;
    private javax.swing.JPanel pnlInfoPromotor;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlTotalVentasPorNegocio;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private javax.swing.JLabel txtPromotor;
    private javax.swing.JLabel txtTitulo;
    private javax.swing.JLabel txtTotalCalibraciones;
    private javax.swing.JLabel txtTotalCanastilla;
    private javax.swing.JLabel txtTotalCombustible;
    private javax.swing.JLabel txtTotalComplementarios;
    private javax.swing.JLabel txtTotalKiosco;
    private javax.swing.JLabel txtTotalSobres;
    // End of variables declaration//GEN-END:variables

    JsonObject data = null;

    /**
     * 🔄 MIGRADO A SERVICIO PYTHON
     * Obtener datos de ventas del promotor
     * @version 2.0 - Migrado a Python
     */
    private void obtenerVentasPromotor(Long id) {
        showPanel("panel_cargando");
        
        // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║  🐍 SERVICIO PYTHON - ARQUEO PROMOTOR                    ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        NovusUtils.printLn("📋 Obteniendo datos de ventas del promotor");
        NovusUtils.printLn("   - Promotor ID: " + id);
        NovusUtils.printLn("   - Turno: " + Main.persona.getGrupoJornadaId());
        
        String funcion = "OBTENER DATOS ARQUEO PROMOTOR";
        // El servicio de printicket (8001) solo imprime, NO obtiene datos
        // Los datos deben venir del servicio en 8019 (o 8010 como alternativa)
        String url = NovusConstante.SECURE_CENTRAL_POINT_IMPRIMIR_ARQUEO_PROMOTOR;
        JsonObject infoPromotor = new JsonObject();
        // El servicio en 8019 espera promotor_id y turno (no identificadorPromotor/identificadorJornada)
        infoPromotor.addProperty("promotor_id", id);
        infoPromotor.addProperty("turno", Main.persona.getGrupoJornadaId());
        
        NovusUtils.printLn("🌐 URL Servicio: " + url);
        NovusUtils.printLn("📤 Payload: " + infoPromotor.toString());
        
        Thread thread = new Thread() {
            @Override
            public void run() {
                // Usar solo content-type para que ClientWSAsync agregue automáticamente los headers por defecto
                // (identificadorDispositivo, aplicacion, versionapp, versioncode, fecha)
                TreeMap<String, String> headers = new TreeMap<>();
                headers.put("content-type", "application/json");
                
                // Aumentar timeout a 60 segundos - el servicio puede estar procesando datos pesados
                ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, infoPromotor, true, false, headers, 60000);
                JsonObject response = client.esperaRespuesta();
                
                if (response != null) {
                    NovusUtils.printLn("✅ Respuesta del servicio Python recibida");
                    data = response;
                    if (data.get("data").getAsJsonObject().get("promotor").getAsJsonArray().size() > 0) {
                        NovusUtils.printLn("✅ Datos del promotor obtenidos correctamente");
                        actualizarReporte();
                        showPanel("pnlInfoPromotor");
                        // Verificar estado de cola al mostrar el reporte
                        javax.swing.SwingUtilities.invokeLater(() -> verificarEstadoColaAlMostrar());
                    } else {
                        NovusUtils.printLn("⚠️  No se encontraron datos del promotor");
                        Runnable run = () -> cerrar();
                        showMessage("SIN INFORMACION DE ARQUEO", 
                                "/com/firefuel/resources/btBad.png", 
                                true, run, true,
                                LetterCase.FIRST_UPPER_CASE);
                    }
                } else {
                    NovusUtils.printLn("❌ ERROR: Sin respuesta del servicio Python");
                    NovusUtils.printLn("⚠️  El servicio Python puede no estar disponible en puerto 8001");
                    Runnable run = () -> cerrar();
                    showMessage("ERROR AL CONSULTAR INFORMACION - SERVICIO PYTHON NO DISPONIBLE", 
                            "/com/firefuel/resources/btBad.png",
                            true, run, true, 
                            LetterCase.FIRST_UPPER_CASE);
                }
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                // 🚀 MIGRACIÓN A SERVICIO PYTHON - FIN
            }
        };
        thread.start();
    }

    private void renderizarUsuarios() {
        try {
            int i = 0;
            int height = 97;
            int panelHeight;
            for (PersonaBean persona : InfoViewController.turnosPersonas) {
                PromotoresVentas item = new PromotoresVentas(persona);
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        id = persona.getId();
                        fecha = persona.getFechaInicio();
                        promotor = persona.getNombre();
                        obtenerVentasPromotor(id);
                    }
                });
                jPanel1.add(item);
                item.setBounds(10, (i * height) + (5 * (i + 1)), 763, height);
                i++;
                panelHeight = Math.max(jPanel1.getHeight(), ((110 * i) + (1 * (i + 1))));
                jPanel1.setPreferredSize(new Dimension(jPanel1.getHeight(), panelHeight));
            }
        } catch (Exception ex) {
            Logger.getLogger(ReporteVentasPromotor.class.getName()).log(Level.SEVERE, null, ex);
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

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnl_container.getLayout();
        pnl_container.add("pnl_ext", panel);
        layout.show(pnl_container, "pnl_ext");
    }

    private void cerrar() {
        dispose();
    }

    public void getSobres() {
        float totalsobres = !data.get("data").getAsJsonObject().get("sobres").getAsJsonObject().get("total").isJsonNull() ? data.get("data").getAsJsonObject().get("sobres").getAsJsonObject().get("total").getAsFloat() : 0f;
        totalSobres = dff.format(totalsobres);
    }

    public JsonArray ordenarMediosPago() {
        JsonArray ventas = data.get("data").getAsJsonObject().get("medios_pagos").getAsJsonArray();
        JsonArray ventaCombustible = new JsonArray();
        for (JsonElement jsonElement : ventas) {
            JsonObject dataOrden = jsonElement.getAsJsonObject();
            if (!dataOrden.get("tipo").getAsString().equals("calibracion")) {
                dataOrden.addProperty("medio_pago", dataOrden.get("medio_pago").getAsString());
                dataOrden.addProperty("tipo", dataOrden.get("tipo").getAsString());
                dataOrden.addProperty("total", dataOrden.get("total").getAsFloat());
            } else {
                dataOrden.addProperty("medio_pago", "CALIBRACION");
                dataOrden.addProperty("tipo", "combustible");
            }
            ventaCombustible.add(dataOrden);
        }
        return ventaCombustible;
    }

    public JsonArray getTotalMediosNegocio(JsonArray json) {
        JsonArray mediosN = json;
        //Se Ordena el Array
        JsonArray sortedJsonArray = new JsonArray();

        List<JsonObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < mediosN.size(); i++) {
            jsonValues.add((JsonObject) mediosN.get(i));
        }
        Collections.sort(jsonValues, new Comparator<JsonObject>() {
            private static final String KEY_NAME = "medio_pago";

            @Override
            public int compare(JsonObject a, JsonObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME).getAsString();
                    valB = (String) b.get(KEY_NAME).getAsString();
                } catch (Exception e) {
                }
                return valA.compareTo(valB);
            }
        });

        for (int i = 0; i < mediosN.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }

        JsonObject mediosNegocio = new JsonObject();
        JsonArray mediosPagoNegocio = new JsonArray();

        long totalKcoVentas = 0;
        long totalCanVentas = 0;
        long totalCombVentas = 0;
        long totalComplementariosVentas = 0;
        String medioPago = "";

        for (JsonElement medio : sortedJsonArray) {
            JsonObject dataMedios = medio.getAsJsonObject();
            if (!mediosNegocio.has(dataMedios.get("medio_pago").getAsString())) {
                JsonObject infoMedios = new JsonObject();
                infoMedios.addProperty("medio", dataMedios.get("medio_pago").getAsString());
                mediosNegocio.add(dataMedios.get("medio_pago").getAsString(), infoMedios);
                mediosPagoNegocio.add(infoMedios);
            }
            if (!medioPago.equalsIgnoreCase(dataMedios.get("medio_pago").getAsString())) {
                totalKcoVentas = 0;
                totalCanVentas = 0;
                totalCombVentas = 0;
                totalComplementariosVentas = 0;
                medioPago = dataMedios.get("medio_pago").getAsString();
            }
            if (mediosNegocio.has(dataMedios.get("medio_pago").getAsString())) {

                switch (dataMedios.get("tipo").getAsString()) {

                    case "canastilla":
                        totalCanVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalCanVentas);
                        break;
                    case "kiosco":
                        totalKcoVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalKcoVentas);
                        break;
                    case "combustible":
                        totalCombVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalCombVentas);
                        break;
                    case "complementarios":
                        totalComplementariosVentas += dataMedios.get("total").getAsLong();
                        mediosNegocio.get(dataMedios.get("medio_pago").getAsString()).getAsJsonObject().addProperty(dataMedios.get("tipo").getAsString(), totalComplementariosVentas);
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        return mediosPagoNegocio;
    }

    public void getTotalGeneral(JsonArray jsonArray) {
        JsonArray respuestaTotal = jsonArray;

        int totalCombustibleVentas = 0;
        int totalCanastillaVentas = 0;
        int totalKioscoVentas = 0;
        int totalcomplementarios = 0;

        for (JsonElement jsonElement : respuestaTotal) {
            JsonObject dataTotales = jsonElement.getAsJsonObject();

            int kioscoV = dataTotales.has("kiosco") ? dataTotales.get("kiosco").getAsInt() : 0;
            int canastillaV = dataTotales.has("canastilla") ? dataTotales.get("canastilla").getAsInt() : 0;
            int combustibleV = dataTotales.has("combustible") ? dataTotales.get("combustible").getAsInt() : 0;
            int complementarios = dataTotales.has("complementarios") ? dataTotales.get("complementarios").getAsInt() : 0;

            totalCombustibleVentas += combustibleV;
            totalCanastillaVentas += canastillaV;
            totalKioscoVentas += kioscoV;
            totalcomplementarios += complementarios;

        }
        totalCombustible = dff.format(totalCombustibleVentas);
        totalCanastilla = dff.format(totalCanastillaVentas);
        totalComplementarios = dff.format(totalcomplementarios);
        totalKiosco = dff.format(totalKioscoVentas);
    }

    public void getInfo() {
        txtPromotor.setText(promotor);
        txtTotalCombustible.setText("$ " + totalCombustible);
        txtTotalCanastilla.setText("$ " + totalCanastilla);
        txtTotalKiosco.setText("$ " + totalKiosco);
        txtTotalSobres.setText("$ " + totalSobres);
        txtTotalComplementarios.setText("$ " + totalComplementarios);
        if (data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject().get("cantidad").getAsInt() > 0) {
            int totalCalibraciones = data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject().get("total")
                    != null ? data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject().get("total").getAsInt() : 0;
            txtTotalCalibraciones.setText("$ " + dff.format(totalCalibraciones));
        }
    }

    public void actualizarReporte() {
        // Verificar estado de la cola al mostrar el reporte
        verificarEstadoColaAlMostrar();
        getSobres();
        cargarTabla();
        getTotalGeneral(medios);
        getInfo();
    }

    void cargarTabla() {
        JsonArray mediosPago = ordenarMediosPago();
        medios = getTotalMediosNegocio(mediosPago);
        JsonArray dataMedios = medios;
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();

        for (JsonElement dataMedio : dataMedios) {
            JsonObject dataMediosPago = dataMedio.getAsJsonObject();

            String medio = dataMediosPago.get("medio").getAsString();

            if (medio.length() > 13) {
                medio = medio.substring(0, 13);
            }

            int kioscoV = dataMediosPago.has("kiosco") ? dataMediosPago.get("kiosco").getAsInt() : 0;
            int canastillaV = dataMediosPago.has("canastilla") ? dataMediosPago.get("canastilla").getAsInt() : 0;
            int combustibleV = dataMediosPago.has("combustible") ? dataMediosPago.get("combustible").getAsInt() : 0;
            int complementarios = dataMediosPago.has("complementarios") ? dataMediosPago.get("complementarios").getAsInt() : 0;

            int totalMedio = kioscoV + combustibleV + canastillaV + complementarios;

            try {
                defaultModel.addRow(new Object[]{
                    "<html><b>" + medio + "</b></html>",
                    "$ " + dff.format(combustibleV),
                    "$ " + dff.format(canastillaV),
                    "$ " + dff.format(kioscoV),
                    "$ " + dff.format(complementarios),
                    "$ " + dff.format(totalMedio)
                });
            } catch (Exception s) {
                NovusUtils.printLn(s.getMessage());
            }
        }
    }

    public void showPanel(String panel) {        
        NovusUtils.showPanel(pnl_container, panel);
    }

    public void imprimirReporte() {
        NovusUtils.printLn("🖨️ [imprimirReporte] INICIO - Promotor ID: " + id);
        
        // Verificar si el botón está bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("⚠️ Botón de impresión bloqueado - Promotor ID: " + id);
            return;
        }
        
        // Validar que el ID del promotor sea válido
        if (id <= 0) {
            NovusUtils.printLn("❌ ERROR: ID del promotor inválido: " + id);
            showMessage("ERROR: ID de promotor inválido", 
                    "/com/firefuel/resources/btBad.png", 
                    true, null, true, LetterCase.FIRST_UPPER_CASE);
            return;
        }
        
        // Tipo de reporte fijo para este flujo
        String reportType = "REPORTE_VENTA_PROMOTOR";
        long reportId = id; // Usar el ID del promotor como report_id
        
        NovusUtils.printLn("📋 [imprimirReporte] Report ID: " + reportId + ", Report Type: " + reportType);
        
        // Verificar si ya está en cola de impresión
        if (existeEnColaPendiente(reportId, reportType)) {
            NovusUtils.printLn("El registro ya está en cola de impresión - Promotor ID: " + reportId);
            // No desbloquear porque ya está en cola
            return;
        }
        
        // BLOQUEAR INMEDIATAMENTE al hacer click
        bloquearBotonImprimir();
        
        // Guardar en cola antes de imprimir
        NovusUtils.printLn("🔄 [ReporteVentasPromotor] Intentando guardar en cola - ID: " + reportId + ", Tipo: " + reportType);
        guardarRegistroPendiente(reportId, reportType);
        NovusUtils.printLn("✅ [ReporteVentasPromotor] Método guardarRegistroPendiente completado");
        
        // Ejecutar el proceso de impresión en un hilo separado
        // para permitir que la UI se actualice primero
        new Thread(() -> {
            try {
                // Pequeña pausa para asegurar que la UI se actualice
                Thread.sleep(50);
                
                // FLUJO: ReporteVentasPromotor → AuditoriaPromotor → Python
                // Usa arquitectura hexagonal con mensajes amigables
                AuditoriaPromotor auditoria = new AuditoriaPromotor(data, promotor, fecha);
                auditoria.printAuditoriaPromotor();
                
                // Impresión exitosa - eliminar de cola y desbloquear
                eliminarRegistroPendiente(reportId, reportType);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimir();
                    showMessage("REPORTE IMPRESO CORRECTAMENTE",
                            "/com/firefuel/resources/btOk.png",
                            true, this::cerrar,
                            true, LetterCase.FIRST_UPPER_CASE);
                });
                    
            } catch (RuntimeException e) {
                // Error desde Python (servicio caído, impresora desconfigurada, etc.)
                // El mensaje ya viene procesado y amigable desde PrintServiceAdapter
                eliminarRegistroPendiente(reportId, reportType);
                String mensajeErrorTemp = e.getMessage();
                
                // Si el mensaje es null o vacío, usar un mensaje por defecto
                if (mensajeErrorTemp == null || mensajeErrorTemp.isEmpty()) {
                    mensajeErrorTemp = "Error al procesar la impresión. Intente nuevamente";
                }
                
                // El mensaje ya viene procesado desde PrintServiceAdapter.analizarMensajeError()
                // que detecta errores de impresora y devuelve mensajes específicos como:
                // - "Impresora no disponible. Verifique la conexión de la impresora"
                // - "Error de conexión con la impresora. Verifique que la impresora esté encendida y conectada"
                // - "La impresora no responde. Verifique la conexión"
                
                // Crear variable final para usar en el lambda
                final String mensajeError = mensajeErrorTemp;
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimir();
                    showMessage(mensajeError,
                            "/com/firefuel/resources/btBad.png",
                            true, this::cerrar,
                            true, LetterCase.FIRST_UPPER_CASE);
                });
            } catch (Exception e) {
                // Error inesperado
                eliminarRegistroPendiente(reportId, reportType);
                Logger.getLogger(ReporteVentasPromotor.class.getName()).log(Level.SEVERE, null, e);
                final String mensajeErrorFinal = "ERROR INESPERADO AL IMPRIMIR";
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimir();
                    showMessage(mensajeErrorFinal,
                            "/com/firefuel/resources/btBad.png",
                            true, this::cerrar,
                            true, LetterCase.FIRST_UPPER_CASE);
                });
            }
        }).start();
    }
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a "IMPRIMIENDO..."
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true;
        btnImprimirDetallado.setText("IMPRIMIENDO...");
        pnlInfoPromotor.revalidate();
        pnlInfoPromotor.repaint();
        // Forzar actualización inmediata de la UI (sin esperar al final del evento)
        pnlInfoPromotor.paintImmediately(pnlInfoPromotor.getBounds());
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto "IMPRIMIR DETALLADO"
     */
    private void desbloquearBotonImprimir() {
        botonImprimirBloqueado = false;
        btnImprimirDetallado.setText("IMPRIMIR DETALLADO");
        pnlInfoPromotor.revalidate();
        pnlInfoPromotor.repaint();
        // Forzar actualización inmediata de la UI
        pnlInfoPromotor.paintImmediately(pnlInfoPromotor.getBounds());
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     * @param id El ID del registro a buscar
     * @param reportType El tipo de reporte a buscar
     * @return true si existe en la cola, false si no existe
     */
    private synchronized boolean existeEnColaPendiente(long id, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() > 0) {
                    JsonArray registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registros) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.get("id").getAsLong() == id 
                            && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error verificando cola de impresión: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Guarda un registro de impresión pendiente en el archivo TXT
     * @param id El ID del promotor (report_id)
     * @param reportType El tipo de reporte (REPORTE_VENTA_PROMOTOR)
     */
    private synchronized void guardarRegistroPendiente(long id, String reportType) {
        try {
            NovusUtils.printLn("📝 [guardarRegistroPendiente] Iniciando - ID: " + id + ", Tipo: " + reportType);
            
            // Crear carpeta logs si no existe
            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                boolean created = logsFolder.mkdir();
                NovusUtils.printLn("📁 [guardarRegistroPendiente] Carpeta logs creada: " + created);
            }

            // Leer archivo existente o crear nuevo array
            JsonArray registros = new JsonArray();
            File file = new File(PRINT_QUEUE_FILE);
            NovusUtils.printLn("📄 [guardarRegistroPendiente] Ruta archivo: " + file.getAbsolutePath());
            NovusUtils.printLn("📄 [guardarRegistroPendiente] Archivo existe: " + file.exists());
            
            if (file.exists() && file.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("Error leyendo archivo de cola de impresión: " + e.getMessage());
                    registros = new JsonArray();
                }
            }

            // Verificar si el ID ya existe en el array (doble verificación para evitar duplicados)
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    NovusUtils.printLn("Registro ya existe en cola de impresión - ID: " + id + " (no se duplica)");
                    return; // No agregar si ya existe
                }
            }

            // Crear nuevo registro
            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("status", "PENDING");
            nuevoRegistro.addProperty("message", "IMPRIMIENDO...");

            NovusUtils.printLn("📋 [guardarRegistroPendiente] Nuevo registro creado: " + nuevoRegistro.toString());

            // Agregar al array
            registros.add(nuevoRegistro);
            NovusUtils.printLn("📊 [guardarRegistroPendiente] Total registros en array: " + registros.size());

            // Guardar archivo con formato legible
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            String jsonContent = gson.toJson(registros);
            NovusUtils.printLn("💾 [guardarRegistroPendiente] JSON a guardar: " + jsonContent.substring(0, Math.min(200, jsonContent.length())) + "...");
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(jsonContent);
                writer.flush();
                NovusUtils.printLn("✅ [guardarRegistroPendiente] Archivo escrito exitosamente");
            }

            NovusUtils.printLn("✅ Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(ReporteVentasPromotor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Se llama cuando la impresión termina (éxito o error)
     * @param id El ID del promotor a eliminar
     * @param reportType El tipo de reporte
     */
    private synchronized void eliminarRegistroPendiente(long id, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                NovusUtils.printLn("No hay registros en cola de impresión para eliminar");
                return;
            }
            
            // Leer archivo existente
            JsonArray registros;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                if (content.length() > 0) {
                    registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                } else {
                    return;
                }
            }
            
            // Buscar y eliminar el registro
            JsonArray registrosActualizados = new JsonArray();
            boolean encontrado = false;
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    encontrado = true;
                    NovusUtils.printLn("🗑️ Eliminando registro de cola de impresión - ID: " + id + ", Tipo: " + reportType);
                    // No agregar este registro (lo eliminamos)
                } else {
                    registrosActualizados.add(registro);
                }
            }
            
            if (encontrado) {
                // Guardar archivo actualizado
                com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(registrosActualizados));
                }
                NovusUtils.printLn("✅ Registro eliminado de cola de impresión - ID: " + id);
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(ReporteVentasPromotor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Verifica el estado de la cola al mostrar el reporte y bloquea/desbloquea el botón según corresponda
     */
    private void verificarEstadoColaAlMostrar() {
        if (id > 0) {
            String reportType = "REPORTE_VENTA_PROMOTOR";
            if (existeEnColaPendiente(id, reportType)) {
                // Si está en cola, mostrar botón bloqueado con texto IMPRIMIENDO...
                botonImprimirBloqueado = true;
                btnImprimirDetallado.setText("IMPRIMIENDO...");
            } else {
                // Si no está en cola, mostrar botón normal
                botonImprimirBloqueado = false;
                btnImprimirDetallado.setText("IMPRIMIR DETALLADO");
            }
            pnlInfoPromotor.revalidate();
            pnlInfoPromotor.repaint();
        }
    }

}

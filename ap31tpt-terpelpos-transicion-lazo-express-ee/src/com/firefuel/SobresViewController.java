/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import com.bean.MovimientosBean;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import teclado.view.common.TecladoNumerico;

public class SobresViewController extends javax.swing.JDialog {

    InfoViewController vistaPrincipal;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_AM);
    SimpleDateFormat x = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);

    DecimalFormat dff = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    LinkedHashSet<MovimientosBean> datos = new LinkedHashSet<>();
    PersonaBean persona = null;
    DefaultTableCellRenderer textRight;
    JsonObject jsonReporteSobre = null;
    DefaultTableCellRenderer textCenter;
    boolean activoMultisurtidores = false;
    long totalSobres = 0;
    
    // Cola de impresión para archivo TXT
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    private String textoOriginalBoton = "IMPRIMIR";
    private boolean botonGuardarBloqueado = false;
    private String textoOriginalBotonGuardar = "GUARDAR";
    
    // Iconos para los botones
    private final Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"));
    private final Icon botonBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"));
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    void validarMultiTurnos() {
        this.activoMultisurtidores = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_MULTISURTIDORES, false);
    }

    public SobresViewController(JFrame parent, boolean modal) {
        super(parent, modal);
        this.vistaPrincipal = (InfoViewController) vistaPrincipal;
        this.persona = Main.persona;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(reporte.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(nuevo.getComponents(), NovusConstante.EXTRABOLD);

        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jTable1.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        this.validarMultiTurnos();
        recargarPersona();
        this.refrescar("DESDE INIT");
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        reporte = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        nuevo = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new TecladoNumerico();
        jNotificacion_Sobres = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1024, 600));
        getContentPane().setLayout(null);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.CardLayout());

        reporte.setBackground(new java.awt.Color(255, 255, 255));
        reporte.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 32)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(186, 12, 47));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("SOBRES ACTUALES");
        jLabel4.setOpaque(true);
        reporte.add(jLabel4);
        jLabel4.setBounds(0, -5, 630, 70);

        jLabel18.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jLabel18.setText("IMPRIMIR");
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel18MousePressed(evt);
            }
        });
        reporte.add(jLabel18);
        jLabel18.setBounds(330, 470, 180, 60);

        jTable1.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero", "Fecha", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(28);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        reporte.add(jScrollPane1);
        jScrollPane1.setBounds(30, 80, 570, 360);

        jLabel19.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel19.setText("NUEVO");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel19MousePressed(evt);
            }
        });
        reporte.add(jLabel19);
        jLabel19.setBounds(110, 470, 180, 60);

        jPanel3.add(reporte, "reporte");

        nuevo.setBackground(new java.awt.Color(235, 235, 235));
        nuevo.setOpaque(false);
        nuevo.setLayout(null);

        jLabel6.setBackground(new java.awt.Color(186, 12, 47));
        jLabel6.setFont(new java.awt.Font("Roboto", 0, 32)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("NUEVO SOBRE");
        nuevo.add(jLabel6);
        jLabel6.setBounds(40, 0, 420, 60);

        jLabel8.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("$ 0");
        nuevo.add(jLabel8);
        jLabel8.setBounds(130, 340, 340, 80);

        jTextField1.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
        });
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        nuevo.add(jTextField1);
        jTextField1.setBounds(360, 75, 250, 50);

        jTextField2.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField2.setText("0");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
        });
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });
        nuevo.add(jTextField2);
        jTextField2.setBounds(350, 190, 260, 50);

        jLabel9.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("TOTAL DEL SOBRE");
        nuevo.add(jLabel9);
        jLabel9.setBounds(190, 310, 240, 24);

        jLabel10.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel10.setText("VALOR EN BILLETES");
        nuevo.add(jLabel10);
        jLabel10.setBounds(0, 75, 340, 50);

        jLabel13.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel13.setText("VALOR EN MONEDA");
        nuevo.add(jLabel13);
        jLabel13.setBounds(0, 190, 250, 50);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(186, 12, 47));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("1");
        jLabel14.setOpaque(true);
        nuevo.add(jLabel14);
        jLabel14.setBounds(540, 0, 70, 60);

        jLabel20.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jLabel20.setText("GUARDAR");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel20MousePressed(evt);
            }
        });
        nuevo.add(jLabel20);
        jLabel20.setBounds(390, 450, 180, 60);

        jLabel21.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel21.setText("REPORTE");
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel21MousePressed(evt);
            }
        });
        nuevo.add(jLabel21);
        jLabel21.setBounds(60, 450, 180, 60);

        jButton1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton1.setText("1.000.000");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        nuevo.add(jButton1);
        jButton1.setBounds(490, 130, 120, 50);

        jButton2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton2.setText("0");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        nuevo.add(jButton2);
        jButton2.setBounds(0, 130, 90, 50);

        jButton3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton3.setText("100.000");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        nuevo.add(jButton3);
        jButton3.setBounds(190, 130, 100, 50);

        jButton4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton4.setText("200.000");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        nuevo.add(jButton4);
        jButton4.setBounds(290, 130, 100, 50);

        jButton5.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton5.setText("50.000");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        nuevo.add(jButton5);
        jButton5.setBounds(90, 130, 100, 50);

        jButton11.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton11.setText("500.000");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        nuevo.add(jButton11);
        jButton11.setBounds(390, 130, 100, 50);

        jButton6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton6.setText("1.000.000");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        nuevo.add(jButton6);
        jButton6.setBounds(490, 245, 120, 50);

        jButton18.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton18.setText("500.000");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        nuevo.add(jButton18);
        jButton18.setBounds(390, 245, 100, 50);

        jButton7.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton7.setText("200.000");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        nuevo.add(jButton7);
        jButton7.setBounds(290, 245, 100, 50);

        jButton8.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton8.setText("100.000");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        nuevo.add(jButton8);
        jButton8.setBounds(190, 245, 100, 50);

        jButton9.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton9.setText("50.000");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        nuevo.add(jButton9);
        jButton9.setBounds(90, 245, 100, 50);

        jButton10.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jButton10.setText("0");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        nuevo.add(jButton10);
        jButton10.setBounds(0, 245, 90, 50);

        jPanel3.add(nuevo, "nuevo");

        getContentPane().add(jPanel3);
        jPanel3.setBounds(37, 120, 620, 550);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel42);
        jLabel42.setBounds(10, 710, 100, 80);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel43);
        jLabel43.setBounds(120, 710, 10, 80);

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("TURNO NO.");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(1110, 105, 110, 20);

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel3.setText("PROMOTOR");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(730, 105, 210, 20);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(720, 190, 550, 470);

        jNotificacion_Sobres.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion_Sobres.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion_Sobres);
        jNotificacion_Sobres.setBounds(150, 720, 950, 60);

        jLabel2.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("1445 ");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(1110, 130, 150, 40);

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(186, 12, 47));
        jLabel1.setText("PROMOTOR");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(730, 130, 350, 40);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jLabel15.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("REGISTRO DE SOBRES");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(130, 0, 720, 80);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel41);
        jLabel41.setBounds(1130, 710, 10, 80);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel37);
        jLabel37.setBounds(80, 10, 10, 68);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion);
        jNotificacion.setBounds(580, 10, 680, 60);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndSobres.png"))); // NOI18N
        getContentPane().add(jLabel11);
        jLabel11.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        NovusUtils.deshabilitarCopiarPegar(jTextField1);
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        NovusUtils.deshabilitarCopiarPegar(jTextField2);
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseReleased
        select();
    }// GEN-LAST:event_jTable1MouseReleased

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jLabel20MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel20MousePressed
        if (!botonGuardarBloqueado && !jTextField1.getText().isEmpty() && !jTextField2.getText().isEmpty()) {
            // Bloquear botón inmediatamente
            bloquearBotonGuardar();
            
            Thread operacion = new Thread() {
                @Override
                public void run() {
                    try {
                        String valorSobreBilletes = jTextField1.getText();
                        String valorSobresMonedas = jTextField2.getText();
                        refrescar("DESDE INIT");
                        jTextField1.setText(valorSobreBilletes);
                        jTextField2.setText(valorSobresMonedas);
                        boolean hayVentas = hayVentas(persona.getGrupoJornadaId(), persona.getId());
                        if (hayVentas) {
                            guardarRegistro();
                        } else {
                            // Si no hay ventas, desbloquear el botón
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                desbloquearBotonGuardar();
                            });
                        }
                    } catch (Exception e) {
                        NovusUtils.printLn("❌ Error en Thread de guardarRegistro: " + e.getMessage());
                        e.printStackTrace();
                        // Asegurar que el botón se desbloquee en caso de excepción
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            desbloquearBotonGuardar();
                            jNotificacion_Sobres.setText("Error al procesar: " + e.getMessage());
                            jNotificacion_Sobres.setVisible(true);
                            setTimeout(3, () -> {
                                jNotificacion_Sobres.setText("");
                            });
                        });
                    }
                }
            };
            operacion.start();

            jNotificacion_Sobres.setText("Cargando.....");
        }
    }// GEN-LAST:event_jLabel20MousePressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        jTextField1.setText("100000");
        valideText();
    }// GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        jTextField1.setText("1000000");
        valideText();
    }// GEN-LAST:event_jButton1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton11ActionPerformed
        jTextField1.setText("500000");
        valideText();
    }// GEN-LAST:event_jButton11ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        jTextField1.setText("0");
        valideText();
    }// GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton5ActionPerformed
        jTextField1.setText("50000");
        valideText();
    }// GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed
        jTextField1.setText("200000");
        valideText();
    }// GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton6ActionPerformed
        jTextField2.setText("1000000");
        valideText();
    }// GEN-LAST:event_jButton6ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton18ActionPerformed
        jTextField2.setText("500000");
        valideText();
    }// GEN-LAST:event_jButton18ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton7ActionPerformed
        jTextField2.setText("200000");
        valideText();
    }// GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton8ActionPerformed
        jTextField2.setText("100000");
        valideText();
    }// GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton9ActionPerformed
        jTextField2.setText("50000");
        valideText();
    }// GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton10ActionPerformed
        jTextField2.setText("0");
        valideText();
    }// GEN-LAST:event_jButton10ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField1ActionPerformed
        valideText();
    }// GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField1KeyTyped
//        soloNumeros(evt);
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jTextField1, 9, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_jTextField1KeyTyped

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField2KeyTyped
//        soloNumeros(evt);
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jTextField2, 9, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_jTextField2KeyTyped

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField2ActionPerformed
        valideText();
    }// GEN-LAST:event_jTextField2ActionPerformed

    private void jLabel18MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel18MousePressed
        NovusUtils.printLn("🖨️🖨️🖨️ FLUJO 2: BOTÓN IMPRIMIR PRESIONADO - Ejecutando jLabel18MousePressed()");
        selectme();
    }// GEN-LAST:event_jLabel18MousePressed

    private void jLabel19MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel19MousePressed
        verPanelNuevoSobre();
    }// GEN-LAST:event_jLabel19MousePressed

    private void jLabel21MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel21MousePressed
        verPanelReporteSobre();
    }// GEN-LAST:event_jLabel21MousePressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField1KeyPressed
        valideText();
    }// GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField2KeyPressed
        valideText();
    }// GEN-LAST:event_jTextField2KeyPressed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField1KeyReleased
        valideText();
    }// GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField2KeyReleased
        valideText();
    }// GEN-LAST:event_jTextField2KeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    public static javax.swing.JLabel jNotificacion_Sobres;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JPanel nuevo;
    private javax.swing.JPanel reporte;
    // End of variables declaration//GEN-END:variables

    private void verPanelNuevoSobre() {
        NovusUtils.beep();
        Thread proceso = new Thread() {
            @Override
            public void run() {
                refrescar("DESDE INIT");
                CardLayout crr = (CardLayout) (jPanel3.getLayout());
                crr.show(jPanel3, "nuevo");
                if (jTextField1.getText().equals("")) {
                    jTextField1.requestFocus();
                } else if (jTextField2.getText().equals("")) {
                    jTextField2.requestFocus();
                }
                jLabel19.setEnabled(true);
                jNotificacion_Sobres.setText("");
            }
        };
        proceso.start();
        jLabel19.setEnabled(false);
        jNotificacion_Sobres.setText("Cargando.....");
    }

    private void verPanelReporteSobre() {
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "reporte");
    }

    private void valideText() {
        long totalBilletes = 0;
        long totalMonedas = 0;
        long total;
        if (jTextField1.getText().equals("")) {
            jTextField1.requestFocus();
        }
        if (!jTextField1.getText().equals("")) {
            totalBilletes = Long.parseLong(jTextField1.getText());
        } else if (!jTextField2.getText().equals("")) {
            totalMonedas = Long.parseLong(jTextField2.getText());
        }

        if (!jTextField1.getText().equals("") && !jTextField2.getText().equals("")) {
            totalBilletes = Long.parseLong(jTextField1.getText());
            totalMonedas = Long.parseLong(jTextField2.getText());
            total = totalBilletes + totalMonedas;
            jLabel8.setText("$" + (dff.format(total)));
            jLabel20.setIcon(botonActivo);
        } else {
            jLabel20.setIcon(botonBloqueado);
        }
        total = totalBilletes + totalMonedas;
        jLabel8.setText("$" + (dff.format(total)));
    }

    private void soloNumeros(KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
            getToolkit().beep();
            evt.consume();
        }
    }

    private void refrescar(String origen) {
        NovusUtils.printLn(origen);
        NovusUtils.printLn("🔄 INICIANDO REFRESCAR - Llamando a conseguirSobres()");
        try {
            jTextField1.setText("");
            jTextField2.setText("");
            jLabel8.setText("$0");
            totalSobres = 0;
            jTable1.setAutoCreateRowSorter(true);
            JsonObject response = conseguirSobres();
            NovusUtils.printLn("📥 RESPUESTA RECIBIDA DE conseguirSobres(): " + (response != null ? "OK" : "NULL"));
            if (response != null) {
                this.jsonReporteSobre = response.get("data") != null && response.get("data").isJsonObject()
                        ? response.getAsJsonObject("data")
                        : null;
                JsonArray jsonArraySobres = new JsonArray();
                if (this.jsonReporteSobre != null) {
                    String consecutivoSobre = this.jsonReporteSobre.get("consecutivoActual").getAsString();
                    jLabel14.setText(consecutivoSobre);
                    jsonArraySobres = organizarSobres(this.jsonReporteSobre.getAsJsonArray("sobres"));
                    
                    // 🔍 PRUEBA: Verificar qué sobres se están cargando
                    NovusUtils.printLn("🔍 SOBRES CARGADOS EN REFRESCAR:");
                    NovusUtils.printLn("  - Consecutivo actual: " + consecutivoSobre);
                    NovusUtils.printLn("  - Total sobres: " + jsonArraySobres.size());
                    for (int i = 0; i < jsonArraySobres.size(); i++) {
                        JsonObject sobre = jsonArraySobres.get(i).getAsJsonObject();
                        NovusUtils.printLn("  - Sobre " + (i+1) + ": " + sobre.toString());
                    }
                }
                DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
                dm.getDataVector().removeAllElements();
                dm.fireTableDataChanged();
                DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
                int numeroSobre = 0;
                for (JsonElement elementSobre : jsonArraySobres) {
                    JsonObject jsonSobre = elementSobre.getAsJsonObject();
                    numeroSobre++;
                    Date fechaSobre = x.parse(jsonSobre.get("fechaSobre").getAsString());
                    float totalSobre = jsonSobre.get("totalSobre").getAsFloat();
                    defaultModel.addRow(new Object[]{numeroSobre, x.format(fechaSobre), "$" + dff.format(totalSobre)});
                    totalSobres += totalSobre;
                }
                jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
                jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
                jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);

                TableRowSorter<TableModel> sorter = new TableRowSorter<>(dm);
                jTable1.setRowSorter(sorter);

                List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
                sorter.setSortKeys(sortKeys);
                
                // Restaurar botón a estado deshabilitado (gris) después de refrescar
                restaurarBotonDeshabilitado();
            }
        } catch (ParseException ex) {
            Logger.getLogger(SobresViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    private void guardarRegistro() {
        NovusUtils.beep();
        
        // El botón ya está bloqueado desde jLabel20MousePressed(), no necesitamos verificar ni bloquear de nuevo

        Date date = new Date();
        int totalBilletes;
        int totalMonedas;
        totalBilletes = Integer.parseInt(jTextField1.getText());
        totalMonedas = Integer.parseInt(jTextField2.getText());

        int total = totalBilletes + totalMonedas;
        if (total <= 0) {
            // Desbloquear botón si el total es inválido
            javax.swing.SwingUtilities.invokeLater(() -> {
                desbloquearBotonGuardar();
            });
            jNotificacion_Sobres.setText("Total sobre debe ser mayor a 0");
            jNotificacion_Sobres.setVisible(true);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jNotificacion_Sobres);
            });
            return;
        }
        
        // El botón ya está bloqueado desde jLabel20MousePressed(), no necesitamos bloquearlo de nuevo

        MovimientosBean mov = new MovimientosBean();
        mov.setEmpresasId(Main.credencial.getEmpresas_id());
        mov.setOperacionId(NovusConstante.MOVIMIENTO_TIPO_SOBRES);
        mov.setPersonaId(this.persona.getId());
        mov.setPersonaNit(this.persona.getIdentificacion());
        mov.setPersonaNombre(this.persona.getNombre());
        mov.setCreateUser(Main.credencial.getEmpresas_id());
        mov.setGrupoJornadaId(this.persona.getGrupoJornadaId());
        mov.setFecha(date);
        mov.setVentaTotal(total);

        try {
            JsonObject json = new JsonObject();
            json.addProperty("identificadorEmpresa", Main.credencial.getEmpresas_id());
            json.addProperty("identificadorPromotor", this.persona.getId());
            json.addProperty("identificadorGrupoJornada", this.persona.getGrupoJornadaId());
            json.addProperty("total", total);
            json.addProperty("fecha", x.format(date));
            json.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
            JsonObject atributos = new JsonObject();
            atributos.addProperty("numero", jLabel14.getText());
            atributos.addProperty("monedas", totalMonedas);
            atributos.addProperty("billetes", totalBilletes);
            json.add("atributos", atributos);

            NovusUtils.printLn("📋 GUARDANDO SOBRE EN BD: " + NovusConstante.SECURE_CENTRAL_POINT_REGISTRAR_SOBRES);
            NovusUtils.printLn("📦 BODY GUARDAR SOBRE: " + json.toString());
            
            // 🔍 PASO 1: Crear sobre en servicio original
            NovusUtils.printLn("🔍 PASO 1: CREANDO SOBRE EN SERVICIO ORIGINAL");
            ClientWSAsync async = new ClientWSAsync("DATOS BASICOS DE LA EMPRESA", NovusConstante.SECURE_CENTRAL_POINT_REGISTRAR_SOBRES, NovusConstante.POST, json, true, false, null);
            JsonObject response = async.esperaRespuesta();
            
            NovusUtils.printLn("✅ RESPUESTA GUARDAR SOBRE: " + (response != null ? "OK" : "NULL"));
            
            // 🔍 OBTENER ID DEL SOBRE CREADO EN SERVICIO ORIGINAL
            int idSobreOriginal = 0;
            if (response != null) {
                // Intentar obtener el ID del sobre creado desde la respuesta
                if (response.has("numeroSobre")) {
                    idSobreOriginal = response.get("numeroSobre").getAsInt();
                    NovusUtils.printLn("🎯 ID SOBRE ORIGINAL: " + idSobreOriginal);
                } else if (response.has("id")) {
                    idSobreOriginal = response.get("id").getAsInt();
                    NovusUtils.printLn("🎯 ID SOBRE ORIGINAL: " + idSobreOriginal);
                } else {
                    // Si no hay ID en la respuesta, usar el consecutivo actual
                    idSobreOriginal = Integer.parseInt(jLabel14.getText());
                    NovusUtils.printLn("🎯 ID SOBRE ORIGINAL (consecutivo): " + idSobreOriginal);
                }
            } else {
                // Fallback: usar el consecutivo actual
                idSobreOriginal = Integer.parseInt(jLabel14.getText());
                NovusUtils.printLn("🎯 ID SOBRE ORIGINAL (fallback): " + idSobreOriginal);
            }
            
            if (response != null) {
                EquipoDao dao = new EquipoDao();
                String parametro = dao.getParametroCore(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES);
                NovusUtils.printLn("🔧 PARAMETRO_IMPRIMIR_SOBRES: " + parametro);
                
                if ((parametro != null) && (parametro.equals("S"))) {
                    NovusUtils.printLn("🆕 ============================================");
                    NovusUtils.printLn("🆕 FLUJO 1: CREACIÓN DE SOBRE NUEVO");
                    NovusUtils.printLn("🆕 IMPRIMIENDO SOBRE RECIÉN CREADO");
                    NovusUtils.printLn("🆕 ============================================");
                    
                    // 🔍 VERIFICAR SERVICIO DE IMPRESIÓN ANTES DE IMPRIMIR (usando caso de uso con cache)
                    NovusUtils.printLn("🔍 Verificando estado del servicio de impresión...");
                    CheckPrintServiceHealthUseCase.HealthCheckResult healthResult;
                    try {
                        healthResult = checkPrintServiceHealthUseCase.execute(null);
                    } catch (Exception e) {
                        // Si hay excepción al verificar el servicio, asumir que no está disponible
                        NovusUtils.printLn("❌ Excepción al verificar servicio de impresión: " + e.getMessage());
                        e.printStackTrace();
                        final String mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            jNotificacion_Sobres.setText(mensajeError);
                            jNotificacion_Sobres.setVisible(true);
                            jNotificacion_Sobres.setFont(new java.awt.Font("Terpel Sans", 1, 24));
                            setTimeout(4, () -> {
                                jNotificacion_Sobres.setText("");
                                jNotificacion_Sobres.setVisible(false);
                            });
                        });
                        NovusUtils.printLn("⚠️ Sobre creado en BD pero NO se enviará a imprimir por falta de servicio");
                        return;
                    }
                    
                    if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                        // El servicio no está disponible o la impresora no está conectada
                        String mensajeError = healthResult.obtenerMensajeError();
                        if (mensajeError == null || mensajeError.trim().isEmpty()) {
                            mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                        }
                        final String mensajeFinal = mensajeError; // Variable final para usar en lambda
                        NovusUtils.printLn("❌ FLUJO 1: Servicio de impresión no disponible: " + mensajeFinal);
                        
                        // Mostrar mensaje al usuario
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            jNotificacion_Sobres.setText(mensajeFinal);
                            jNotificacion_Sobres.setVisible(true);
                            jNotificacion_Sobres.setFont(new java.awt.Font("Terpel Sans", 1, 24));
                            setTimeout(4, () -> {
                                jNotificacion_Sobres.setText("");
                                jNotificacion_Sobres.setVisible(false);
                            });
                        });
                        
                        NovusUtils.printLn("⚠️ Sobre creado en BD pero NO se enviará a imprimir por falta de servicio");
                    } else {
                        NovusUtils.printLn("✅ Servicio de impresión disponible - Procediendo con impresión");
                        
                        // Cambiar el botón a IMPRIMIENDO... INMEDIATAMENTE
                        cambiarBotonGuardarAImprimiendo();
                        
                        // Construir sobre_data con toda la información del sobre
                        JsonObject sobreData = new JsonObject();
                        sobreData.addProperty("identificador_empresa", Main.credencial.getEmpresas_id());
                        sobreData.addProperty("identificador_promotor", this.persona.getId());
                        sobreData.addProperty("identificacion_promotor", this.persona.getIdentificacion());
                        
                        // Separar nombres y apellidos
                        String nombreCompleto = this.persona.getNombre();
                        String[] partesNombre = nombreCompleto.split(" ", 2);
                        String nombres = partesNombre.length > 0 ? partesNombre[0] : "";
                        String apellidos = partesNombre.length > 1 ? partesNombre[1] : "";
                        
                        sobreData.addProperty("nombres_promotor", nombres);
                        sobreData.addProperty("apellidos_promotor", apellidos);
                        sobreData.addProperty("identificador_equipo", Main.credencial.getEquipos_id());
                        sobreData.addProperty("remoto_id", 0);
                        sobreData.addProperty("total", (double) total);
                        sobreData.addProperty("monedas", (double) totalMonedas);
                        sobreData.addProperty("billetes", (double) totalBilletes);
                        sobreData.addProperty("observaciones", "");
                        
                        // 🔍 PASO 2: Crear sobre en servicio Python con ID sincronizado
                        NovusUtils.printLn("🔍 PASO 2: CREANDO SOBRE EN SERVICIO PYTHON CON ID SINCRONIZADO");
                        NovusUtils.printLn("🔄 SINCRONIZANDO ID: " + idSobreOriginal + " -> Servicio Python");
                        crearSobreConNuevoServicio(this.persona.getGrupoJornadaId(), sobreData, idSobreOriginal);
                        
                        NovusUtils.printLn("✅ ============================================");
                        NovusUtils.printLn("✅ FLUJO 1: SOBRE RECIÉN CREADO - Petición enviada al servicio Python");
                        NovusUtils.printLn("✅ ============================================");
                        
                        // 🔄 SINCRONIZACIÓN: También crear el sobre en el servicio original
                        NovusUtils.printLn("🔄 SINCRONIZANDO: Creando sobre en servicio original para mantener consistencia");
                        try {
                            // El sobre ya se creó en el servicio original arriba (líneas 934-937)
                            // Solo necesitamos asegurar que se refresque la interfaz
                            NovusUtils.printLn("✅ SINCRONIZACIÓN COMPLETA: Sobre creado en ambos servicios");
                        } catch (Exception syncEx) {
                            NovusUtils.printLn("⚠️ ERROR EN SINCRONIZACIÓN: " + syncEx.getMessage());
                        }
                    }
                } else {
                    NovusUtils.printLn("❌ NO SE IMPRIMIRÁ - PARAMETRO_IMPRIMIR_SOBRES no está activo");
                }

                // 🔄 ESPERAR UN MOMENTO PARA QUE EL SERVICIO ORIGINAL PROCESE EL SOBRE
                NovusUtils.printLn("⏳ ESPERANDO 2 SEGUNDOS PARA SINCRONIZACIÓN...");
                try {
                    Thread.sleep(2000); // Esperar 2 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Actualizar UI en el EDT
                javax.swing.SwingUtilities.invokeLater(() -> {
                    this.refrescar("DESDE GUARDAR REGISTRO");
                    this.verPanelReporteSobre();

                    NovusUtils.setMensaje("Se ha guardado su sobre correctamente ", jNotificacion_Sobres);
                    jNotificacion_Sobres.setVisible(true);
                    setTimeout(2, () -> {
                        NovusUtils.setMensaje("", jNotificacion_Sobres);
                    });

                    jTextField1.setText("");
                    jTextField2.setText("");
                    
                    // Desbloquear botón GUARDAR después de completar (vuelve a ROJO activo)
                    desbloquearBotonGuardar();
                });

            } else {
                // Actualizar UI en el EDT
                javax.swing.SwingUtilities.invokeLater(() -> {
                    NovusUtils.setMensaje("Ocurrio un error en el registro de sobres ", jNotificacion_Sobres);
                    jNotificacion_Sobres.setVisible(true);
                    setTimeout(2, () -> {
                        NovusUtils.setMensaje("", jNotificacion_Sobres);
                    });
                    
                    // Desbloquear botón GUARDAR en caso de error
                    desbloquearBotonGuardar();
                });
            }
        } catch (Exception a) {
                NovusUtils.printLn("❌ Error en guardarRegistro: " + a.getMessage());
                a.printStackTrace();
                
                // Desbloquear botón GUARDAR en caso de excepción (en el EDT)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonGuardar();
                    NovusUtils.setMensaje("Error al guardar el sobre: " + a.getMessage(), jNotificacion_Sobres);
                    jNotificacion_Sobres.setVisible(true);
                    setTimeout(3, () -> {
                        NovusUtils.setMensaje("", jNotificacion_Sobres);
                    });
                });
            }
    }

    JsonObject conseguirSobres() {
        NovusUtils.printLn("🚀 EJECUTANDO conseguirSobres() - Iniciando llamada al servicio");
        JsonObject response = null;
        JsonObject comando = new JsonObject();
        String url = this.activoMultisurtidores ? NovusConstante.SECURE_CENTRAL_POINT_REPORTE_SOBRES_CONSOLIDADO : NovusConstante.SECURE_CENTRAL_POINT_REPORTE_SOBRES;
        
        comando.addProperty("identificadorPromotor", Main.persona.getId());
        comando.addProperty("identificadorJornada", Main.persona.getGrupoJornadaId());
        comando.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        
        // LOG: Servicio que se está consumiendo para cargar sobres
        NovusUtils.printLn("📋 CONSUMIENDO SERVICIO PARA CARGAR SOBRES: " + url);
        NovusUtils.printLn("🔧 Multisurtidores activo: " + this.activoMultisurtidores);
        NovusUtils.printLn("📦 BODY CARGAR_SOBRES: " + comando.toString());
        
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync async = new ClientWSAsync("OBTENER SOBRES", url, NovusConstante.POST, comando, true);
        async.setTimeout(5000);
        try {
            response = async.esperaRespuesta();
            NovusUtils.printLn("✅ RESPUESTA CARGAR_SOBRES: " + (response != null ? "OK" : "NULL"));
        } catch (Exception ex) {
            NovusUtils.printLn("❌ ERROR CARGAR_SOBRES: " + ex.getMessage());
        }
        return response;
    }

    // Método para crear un sobre nuevo con el servicio Python
    void crearSobreConNuevoServicio(long identificadorJornada, JsonObject sobreData, int idSobreOriginal) {
        JsonObject comando = new JsonObject();
        
        // Usar tu nuevo servicio de impresión de sobres
        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SHIFTS;
        
        // Construir el body para CREAR_SOBRE con ID sincronizado
        comando.addProperty("shift_type", "CREAR_SOBRE");
        comando.addProperty("identificador_jornada", identificadorJornada);
        comando.addProperty("identificador_sobre", idSobreOriginal); // 🔄 SINCRONIZAR ID
        comando.add("sobre_data", sobreData);
        
        // LOG: Consumiendo servicio para crear sobre
        NovusUtils.printLn("🖨️ CONSUMIENDO SERVICIO PARA CREAR SOBRE: " + url);
        NovusUtils.printLn("🎯 NUEVO SERVICIO PYTHON: " + url);
        NovusUtils.printLn("🔄 ID SINCRONIZADO: " + idSobreOriginal);
        NovusUtils.printLn("📋 BODY CREAR_SOBRE: " + comando.toString());
        
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync async = new ClientWSAsync("CREAR SOBRE", url, NovusConstante.POST, comando, true);
        async.setTimeout(5000);
        try {
            // NO ESPERAR la respuesta del servicio - enviar y continuar
            async.start();
            NovusUtils.printLn("✅ Petición CREAR_SOBRE enviada al servicio Python (sin esperar respuesta) - Sobre: " + idSobreOriginal);
        } catch (Exception ex) {
            NovusUtils.printLn("❌ ERROR al enviar CREAR_SOBRE: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Método para imprimir sobre usando tu nuevo servicio Python
    JsonObject imprimirSobreConNuevoServicio(int numeroSobre, int copias, String shiftType) {
        JsonObject response = null;
        JsonObject comando = new JsonObject();
        
        // Usar tu nuevo servicio de impresión de sobres
        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SHIFTS;
        
        // Construir el body según el formato esperado por tu servicio Python
        comando.addProperty("shift_type", shiftType);
        comando.addProperty("identificador_sobre", numeroSobre);
        comando.addProperty("identificador_jornada", Main.persona.getGrupoJornadaId());
        comando.addProperty("copias", copias);
        
        // LOG: Consumiendo servicio para imprimir sobre
        NovusUtils.printLn("🖨️ CONSUMIENDO SERVICIO PARA IMPRIMIR SOBRE: " + url);
        NovusUtils.printLn("🎯 NUEVO SERVICIO PYTHON: " + url);
        NovusUtils.printLn("📋 BODY " + shiftType + ": " + comando.toString());
        
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync async = new ClientWSAsync("IMPRIMIR SOBRE", url, NovusConstante.POST, comando, true);
        async.setTimeout(5000);
        try {
            // ESPERAR la respuesta del servicio (síncrono) para mostrar mensaje inmediato
            response = async.esperaRespuesta();
            
            if (response != null) {
                NovusUtils.printLn("✅ RESPUESTA " + shiftType + ": " + response.toString());
            } else {
                NovusUtils.printLn("❌ Sin respuesta del servicio - Sobre: " + numeroSobre);
            }
        } catch (Exception ex) {
            NovusUtils.printLn("ERROR " + shiftType + ": " + ex.getMessage());
            ex.printStackTrace();
            // Retornar respuesta de error
            response = new JsonObject();
            response.addProperty("success", false);
            response.addProperty("message", "Error de conexión: " + ex.getMessage());
        }
        return response;
    }

    public void recargarPersona() {
        if (this.persona != null) {
            jLabel1.setText(this.persona.getNombre());
            jLabel2.setText("" + this.persona.getGrupoJornadaId());
        }
    }

    private void cerrar() {
        dispose();
    }

    private void recargarConsecutivo() {
        try {
            MovimientosBean mov = new MovimientosBean();
            mov.setGrupoJornadaId(this.persona.getGrupoJornadaId());
            mov.setPersonaId(this.persona.getId());
            MovimientosDao mdao = new MovimientosDao();

        } catch (Exception ex) {
            Logger.getLogger(SobresViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectme() {
        NovusUtils.printLn("🖨️🖨️🖨️ ============================================");
        NovusUtils.printLn("🖨️🖨️🖨️ FLUJO 2: IMPRESIÓN DE SOBRE SELECCIONADO");
        NovusUtils.printLn("🖨️🖨️🖨️ Botón IMPRIMIR presionado");
        NovusUtils.printLn("🖨️🖨️🖨️ ============================================");
        
        // BLOQUEAR BOTÓN INMEDIATAMENTE - ANTES DE CUALQUIER OTRA OPERACIÓN
        bloquearBotonImprimir();
        
        // Verificar si el botón estaba ya bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("Botón de impresión bloqueado - hay una impresión en proceso");
            // Ya está bloqueado, continuar con el flujo
        }
        
        int r = jTable1.getSelectedRow();
        NovusUtils.printLn("📋 Fila seleccionada: " + r);
        NovusUtils.printLn("📊 jsonReporteSobre es null: " + (this.jsonReporteSobre == null));
        
        if (r > -1 && this.jsonReporteSobre != null) {
            try {
                JsonArray jsonArraySobres = this.jsonReporteSobre.getAsJsonArray("sobres");
                NovusUtils.printLn("📦 Total sobres encontrados: " + jsonArraySobres.size());
                
                // 🔧 CORRECCIÓN: Obtener el número del sobre desde la fila VISUAL seleccionada
                // La tabla está ordenada por fecha, pero necesitamos el sobre real de esa posición
                int filaVisual = jTable1.convertRowIndexToModel(r);
                NovusUtils.printLn("🔍 INFORMACIÓN DE SOBRES:");
                NovusUtils.printLn("  📋 Fila seleccionada visualmente: " + r);
                NovusUtils.printLn("  📋 Fila en el modelo: " + filaVisual);
                
                // Obtener el sobre del array ordenado por fecha
                JsonObject jsonSobreSeleccionado = jsonArraySobres.get(filaVisual).getAsJsonObject();
                int numeroSobre = jsonSobreSeleccionado.get("numeroSobre").getAsInt();
                
                NovusUtils.printLn("🔍 MOSTRANDO CONTEXTO (5 sobres alrededor del seleccionado):");
                int inicio = Math.max(0, filaVisual - 2);
                int fin = Math.min(jsonArraySobres.size(), filaVisual + 3);
                for (int i = inicio; i < fin; i++) {
                    JsonObject tempSobre = jsonArraySobres.get(i).getAsJsonObject();
                    String marcador = (i == filaVisual) ? " ← SELECCIONADO" : "";
                    NovusUtils.printLn("  Fila " + i + ": Sobre #" + tempSobre.get("numeroSobre").getAsString() + " - Total: $" + tempSobre.get("totalSobre").getAsString() + marcador);
                }
                
                NovusUtils.printLn("🎯 SOBRE SELECCIONADO: " + jsonSobreSeleccionado.toString());
                NovusUtils.printLn("🔢 NUMERO SOBRE A IMPRIMIR: " + numeroSobre);
                
                // Verificar si ya está en cola de impresión
                if (existeEnColaPendiente(numeroSobre, "IMPRIMIR_SOBRE")) {
                    NovusUtils.printLn("El registro ya está en cola de impresión - ID: " + numeroSobre);
                    // Ya está bloqueado, no hacer nada más
                    return;
                }
                
                // 🔧 CORRECCIÓN: Usar directamente el sobre seleccionado
                JsonObject jsonSobre = jsonSobreSeleccionado;
                NovusUtils.printLn("✅ FLUJO 2: SOBRE SELECCIONADO ENCONTRADO");
                
                // 🔍 VERIFICAR SERVICIO DE IMPRESIÓN ANTES DE IMPRIMIR (usando caso de uso con cache)
                NovusUtils.printLn("🔍 Verificando estado del servicio de impresión...");
                CheckPrintServiceHealthUseCase.HealthCheckResult healthResult;
                try {
                    healthResult = checkPrintServiceHealthUseCase.execute(null);
                } catch (Exception e) {
                    // Si hay excepción al verificar el servicio, asumir que no está disponible
                    NovusUtils.printLn("❌ Excepción al verificar servicio de impresión: " + e.getMessage());
                    e.printStackTrace();
                    final String mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                    jNotificacion_Sobres.setText(mensajeError);
                    jNotificacion_Sobres.setVisible(true);
                    jNotificacion_Sobres.setFont(new java.awt.Font("Terpel Sans", 1, 24));
                    NovusUtils.printLn("📢 MOSTRANDO MENSAJE EN FOOTER: " + mensajeError);
                    setTimeout(4, () -> {
                        jNotificacion_Sobres.setText("");
                        jNotificacion_Sobres.setVisible(false);
                    });
                    desbloquearBotonImprimir();
                    return;
                }
                
                if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                    // El servicio no está disponible o la impresora no está conectada
                    String mensajeError = healthResult.obtenerMensajeError();
                    if (mensajeError == null || mensajeError.trim().isEmpty()) {
                        mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                    }
                    NovusUtils.printLn("❌ FLUJO 2: Servicio de impresión no disponible: " + mensajeError);
                    
                    // Mostrar mensaje al usuario
                    jNotificacion_Sobres.setText(mensajeError);
                    jNotificacion_Sobres.setVisible(true);
                    jNotificacion_Sobres.setFont(new java.awt.Font("Terpel Sans", 1, 24));
                    NovusUtils.printLn("📢 MOSTRANDO MENSAJE EN FOOTER: " + mensajeError);
                    
                    setTimeout(4, () -> {
                        jNotificacion_Sobres.setText("");
                        jNotificacion_Sobres.setVisible(false);
                    });
                    
                    // Desbloquear botón para permitir reintento
                    desbloquearBotonImprimir();
                    return;
                }
                
                NovusUtils.printLn("✅ Servicio de impresión disponible - Procediendo con impresión");
                NovusUtils.printLn("✅ FLUJO 2: Llamando a imprimirSobreConNuevoServicio()");
                
                try {
                    // Usar tu nuevo servicio Python para imprimir con shift_type "IMPRIMIR_SOBRE"
                    JsonObject response = imprimirSobreConNuevoServicio(numeroSobre, 1, "IMPRIMIR_SOBRE");
                    
                    if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                        NovusUtils.printLn("✅ FLUJO 2: SOBRE SELECCIONADO IMPRESO CON ÉXITO");
                        NovusUtils.setMensaje("SE HA IMPRESO EL SOBRE", jNotificacion_Sobres);
                        jNotificacion_Sobres.setVisible(true);
                        setTimeout(2, () -> {
                            NovusUtils.setMensaje("", jNotificacion_Sobres);
                            dispose();
                        });
                    } else {
                        // El servicio Python respondió pero sin éxito. Mostrar mensaje detallado y mantener la ventana.
                        NovusUtils.printLn("⚠️ FLUJO 2: Servicio Python respondió con éxito = false");
                        String mensajeError = "IMPRESORA NO CONECTADA";
                        if (response != null && response.has("message") && !response.get("message").isJsonNull()) {
                            String mensajeCompleto = response.get("message").getAsString();
                            // Extraer mensaje más corto para el footer
                            if (mensajeCompleto.contains("CONEXIÓN RECHAZADA")) {
                                mensajeError = "IMPRESORA NO CONECTADA - VERIFIQUE EL SERVICIO";
                            } else if (mensajeCompleto.contains("NO DISPONIBLE")) {
                                mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE";
                            } else {
                                mensajeError = mensajeCompleto;
                            }
                        }
                        NovusUtils.printLn("⚠️ Detalle error: " + mensajeError);
                        NovusUtils.printLn("📢 MOSTRANDO MENSAJE EN FOOTER: " + mensajeError);
                        
                        jNotificacion_Sobres.setText(mensajeError);
                        jNotificacion_Sobres.setVisible(true);
                        NovusUtils.printLn("✅ Mensaje mostrado en footer - Visible: " + jNotificacion_Sobres.isVisible());
                        
                        setTimeout(4, () -> {
                            jNotificacion_Sobres.setText("");
                            jNotificacion_Sobres.setVisible(false);
                        });
                        
                        // Desbloquear botón para permitir reintento
                        desbloquearBotonImprimir();
                    }
                } catch (Exception e) {
                    NovusUtils.setMensaje("HAY PROBLEMAS DE CONEXION CON IMPRESORA", jNotificacion_Sobres);
                    jNotificacion_Sobres.setVisible(true);
                    setTimeout(2, () -> {
                        NovusUtils.setMensaje("", jNotificacion_Sobres);
                    });
                    NovusUtils.printLn(e.getMessage());
                    
                    // Desbloquear botón para permitir reintento
                    desbloquearBotonImprimir();
                }
            } catch (Exception e) {
                NovusUtils.printLn("❌ FLUJO 2: ERROR EN selectme(): " + e.getMessage());
                Logger.getLogger(SobresViewController.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            NovusUtils.printLn("❌ FLUJO 2: CONDICIONES NO CUMPLIDAS: r=" + r + ", jsonReporteSobre=" + (this.jsonReporteSobre != null));
        }
    }

    void select() {
        int r = jTable1.getSelectedRow();
        if (r > -1 && this.jsonReporteSobre != null) {
            try {
                JsonArray jsonArraySobres = this.jsonReporteSobre.getAsJsonArray("sobres");
                int filaVisual = jTable1.convertRowIndexToModel(r);
                JsonObject jsonSobreSeleccionado = jsonArraySobres.get(filaVisual).getAsJsonObject();
                int numeroSobre = jsonSobreSeleccionado.get("numeroSobre").getAsInt();
                
                // Verificar si existe en la cola de impresión
                if (existeEnColaPendiente(numeroSobre, "IMPRIMIR_SOBRE")) {
                    // Si existe, mostrar IMPRIMIENDO... y bloquear
                    bloquearBotonImprimir();
                } else {
                    // Si no existe, mostrar IMPRIMIR activo (rojo)
                    desbloquearBotonImprimir();
                }
            } catch (Exception e) {
                // En caso de error, mostrar botón deshabilitado (gris)
                restaurarBotonDeshabilitado();
            }
        } else {
            // Sin selección, mostrar botón deshabilitado (gris)
            restaurarBotonDeshabilitado();
        }
    }
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a IMPRIMIENDO...
     * No usa setEnabled(false) para mantener el texto visible
     */
    private void bloquearBotonImprimir() {
        bloquearBotonImprimirConMensaje("IMPRIMIENDO...");
    }
    
    /**
     * Bloquea el botón de imprimir con un mensaje personalizado
     * Replica el patrón de VentasHistorialView: cambia icono a gris, mueve botón a la izquierda
     * y muestra "IMPRIMIENDO..." en un label separado a la derecha
     */
    private void bloquearBotonImprimirConMensaje(String mensaje) {
        NovusUtils.printLn("🔒 BLOQUEANDO BOTÓN IMPRIMIR - Mensaje: " + mensaje);
        botonImprimirBloqueado = true;
        
        // Cambiar icono a gris y mostrar mensaje en el botón
        jLabel18.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        jLabel18.setText(mensaje);
        jLabel18.setForeground(Color.WHITE);
        jLabel18.setFont(new java.awt.Font("Terpel Sans", java.awt.Font.BOLD, 18));
        
        // Asegurar que el texto esté centrado sobre el icono
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel18.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        
        // Forzar actualización inmediata de la UI (sin esperar al final del evento)
        reporte.revalidate();
        reporte.repaint();
        reporte.paintImmediately(reporte.getBounds());
        
        NovusUtils.printLn("✅ Botón bloqueado - Mensaje: " + mensaje);
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto a IMPRIMIR (rojo - activo)
     * Restaura la posición original del botón y oculta el label de "IMPRIMIENDO..."
     */
    private void desbloquearBotonImprimir() {
        NovusUtils.printLn("🔓 DESBLOQUEANDO BOTÓN IMPRIMIR");
        botonImprimirBloqueado = false;
        
        
        // Restaurar texto e icono original (rojo - activo)
        jLabel18.setText(textoOriginalBoton);
        jLabel18.setForeground(Color.WHITE);
        jLabel18.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        
        // Forzar actualización
        reporte.revalidate();
        reporte.repaint();
        reporte.paintImmediately(reporte.getBounds());
        
        NovusUtils.printLn("✅ Botón desbloqueado - Texto: " + jLabel18.getText());
    }
    
    /**
     * Restaura el botón a su estado deshabilitado (gris) cuando no hay selección
     * o cuando se completa la impresión
     */
    private void restaurarBotonDeshabilitado() {
        botonImprimirBloqueado = false;
        
        
        jLabel18.setText(textoOriginalBoton);
        jLabel18.setForeground(Color.WHITE);
        jLabel18.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        
        reporte.revalidate();
        reporte.repaint();
    }
    
    /**
     * Bloquea el botón GUARDAR y cambia el texto a GUARDANDO...
     */
    private void bloquearBotonGuardar() {
        cambiarBotonGuardarConMensaje("GUARDANDO...");
    }
    
    /**
     * Desbloquea el botón GUARDAR y restaura el texto a GUARDAR (gris deshabilitado)
     */
    private void desbloquearBotonGuardar() {
        botonGuardarBloqueado = false;
        jLabel20.setIcon(botonBloqueado);
        jLabel20.setText(textoOriginalBotonGuardar);
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        // NO usar setEnabled(true) - el componente siempre está habilitado visualmente
        
        // Forzar actualización
        nuevo.revalidate();
        nuevo.repaint();
    }
    
    /**
     * Cambia el botón GUARDAR a estado IMPRIMIENDO...
     * Se usa cuando después de guardar se va a imprimir automáticamente
     */
    private void cambiarBotonGuardarAImprimiendo() {
        cambiarBotonGuardarConMensaje("IMPRIMIENDO...");
    }
    
    /**
     * Cambia el botón GUARDAR con un mensaje personalizado
     */
    private void cambiarBotonGuardarConMensaje(String mensaje) {
        botonGuardarBloqueado = true;
        jLabel20.setIcon(botonBloqueado);
        jLabel20.setText(mensaje);
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Terpel Sans", java.awt.Font.BOLD, 18));
        // NO usar setEnabled(false) - el bloqueo se maneja solo con botonGuardarBloqueado
        
        // Forzar actualización
        nuevo.revalidate();
        nuevo.repaint();
    }
    
    /**
     * Restaura el botón GUARDAR a su estado inicial (gris deshabilitado)
     * Se usa cuando se muestra el panel NUEVO SOBRE
     */
    private void restaurarBotonGuardarInicial() {
        botonGuardarBloqueado = false;
        jLabel20.setIcon(botonBloqueado);
        jLabel20.setText(textoOriginalBotonGuardar);
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
    }

    private boolean hayVentas(long jornadaId, long id) {
        MovimientosDao dao = new MovimientosDao();
        JsonObject data = sumatoriaSobres(dao.hayVentas(jornadaId, id), jornadaId, id);
        int totalBilletes = Integer.parseInt(jTextField1.getText());
        int totalMonedas = Integer.parseInt(jTextField2.getText());
        long total = totalBilletes + totalMonedas + totalSobres;
        if (data.get("hay_ventas").getAsBoolean() && total <= data.get("venta_total").getAsLong()) {
            return true;
        } else if (data.get("hay_ventas").getAsBoolean() && total > data.get("venta_total").getAsLong()) {
            jNotificacion_Sobres.setText("El valor ingresado es superior a lo vendido");
            jNotificacion_Sobres.setVisible(true);
            jNotificacion_Sobres.setFont(new java.awt.Font("Terpel Sans", 1, 28));
            setTimeout(3, () -> {
                jNotificacion_Sobres.setText("");
            });
            return false;
        } else if (!data.get("hay_ventas").getAsBoolean() && data.get("venta_total").getAsLong() == 0) {
            jNotificacion_Sobres.setText("No hay ventas");
            setTimeout(3, () -> {
                jNotificacion_Sobres.setText("");
            });
            return false;
        }
        return false;
    }

    private JsonObject sumatoriaSobres(JsonObject json, long turno, long id) {
        JsonObject data = json;
        long total = 0;
        try {
            NovusUtils.printLn("🔍 sumatoriaSobres: Iniciando búsqueda de ventas para sobres");
            String url = NovusConstante.SECURE_CENTRAL_POINT_SOBRES;
            JsonObject body = new JsonObject();
            body.addProperty("turno", turno);
            body.addProperty("idPromotor", id);
            ClientWSAsync client = new ClientWSAsync("buscar ventas para informacion de sobres", url, NovusConstante.POST, body, true);
            client.setTimeout(5000); // Agregar timeout de 5 segundos
            NovusUtils.printLn("🔍 sumatoriaSobres: Ejecutando esperaRespuesta...");
            client.esperaRespuesta();
            NovusUtils.printLn("🔍 sumatoriaSobres: esperaRespuesta completado");
            
            if (client.getResponse() != null) {
                total = client.getResponse().get("venta_total").getAsLong();
                total += json.get("venta_total").getAsLong();
                if (total > 0 || json.get("venta_total").getAsLong() > 0) {
                    data.addProperty("hay_ventas", true);
                } else {
                    data.addProperty("hay_ventas", false);
                }
                data.addProperty("venta_total", total);
                NovusUtils.printLn("✅ sumatoriaSobres: Total calculado: " + total);
            } else {
                NovusUtils.printLn("⚠️ sumatoriaSobres: No se obtuvo respuesta del servicio, usando datos locales");
                // Si no hay respuesta, usar los datos locales que ya tenemos
                if (json.has("venta_total")) {
                    total = json.get("venta_total").getAsLong();
                    data.addProperty("venta_total", total);
                    data.addProperty("hay_ventas", total > 0);
                }
            }

        } catch (Exception e) {
            NovusUtils.printLn("❌ Error al obtener la informacion en sumatoriaSobres: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, usar los datos locales que ya tenemos
            if (json.has("venta_total")) {
                total = json.get("venta_total").getAsLong();
                data.addProperty("venta_total", total);
                data.addProperty("hay_ventas", total > 0);
            }
        }

        return data;
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

    private JsonArray organizarSobres(JsonArray sobres) {
        NovusUtils.printLn("🔧 EJECUTANDO organizarSobres() - Procesando " + sobres.size() + " sobres");
        TreeMap<Date, JsonObject> ordenarSobres = new TreeMap<>();
        JsonArray data = new JsonArray();
        sobres.forEach(t -> {
            try {
                JsonObject sobre = t.getAsJsonObject();
                String fechaStr = sobre.get("fechaSobre").getAsString();
                ordenarSobres.put(x.parse(fechaStr), sobre);
            } catch (ParseException ex) {
                Logger.getLogger(SobresViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        for (Map.Entry<Date, JsonObject> entry : ordenarSobres.entrySet()){
            data.add(entry.getValue());
        }
        System.out.println("sobres -> "+data);
        return data;
        
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     * @param id El ID del registro a buscar (identificador_sobre o identificador_jornada)
     * @param reportType El tipo de reporte (CREAR_SOBRE, IMPRIMIR_SOBRE)
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
                    JsonArray registros = JsonParser.parseString(content.toString()).getAsJsonArray();
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
     * @param id El ID del sobre o jornada
     * @param reportType El tipo de reporte (CREAR_SOBRE, IMPRIMIR_SOBRE)
     */
    private synchronized void guardarRegistroPendiente(long id, String reportType) {
        try {
            // Crear carpeta logs si no existe
            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdir();
            }

            // Leer archivo existente o crear nuevo array
            JsonArray registros = new JsonArray();
            File file = new File(PRINT_QUEUE_FILE);
            
            if (file.exists() && file.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = JsonParser.parseString(content.toString()).getAsJsonArray();
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

            // Agregar al array
            registros.add(nuevoRegistro);

            // Guardar archivo con formato legible
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registros));
            }

            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(SobresViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Se llama cuando se recibe notificación del servicio o cuando hay error
     * @param id El ID del sobre o jornada a eliminar
     * @param reportType El tipo de reporte (CREAR_SOBRE, IMPRIMIR_SOBRE)
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
                    registros = JsonParser.parseString(content.toString()).getAsJsonArray();
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
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(registrosActualizados));
                }
                NovusUtils.printLn("✅ Registro eliminado de cola de impresión - ID: " + id);
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(SobresViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
}

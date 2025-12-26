/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.bean.MedioIdentificacionBean;
import com.bean.Notificador;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.facade.ClienteFacade;
import com.facade.LiberarAutorizacionCliente;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.PrinterFacade;
import java.awt.CardLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.ScheduledFuture;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import teclado.view.common.TecladoExtendido;

/**
 * @author novus
 */
public class VentaPredefinirPlaca extends javax.swing.JDialog {

    private String productoSeleccionado = "";
    private String productoManguera = "";
    int longlista = 0;
    boolean HABILITAR_CONSULTA_SICOM = false;
    private Timer mensajeTimer; // Timer para limpiar mensaje automáticamente
    InfoViewController parent;
    TreeMap<Integer, Surtidor> mangueras = new TreeMap<>();
    public static VentaPredefinirPlaca instance = null;
     public static VentaPredefinirPlaca instance1 = null;
    JsonObject recepcionData = null;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    Timer timer = null;
    public static VentaPredefinirPlaca ventaPredefinirPlaca = null;
    boolean enVenta_1 = false;
    boolean esFlujoPlaca = false; // Indica si es el flujo de autorización por Placa
    
    // Variables para guardar el estado del footer y restaurarlo después de validaciones
    private String textoFooterAnterior = ""; // Guarda el texto del footer antes de ocultarlo por validaciones
    private boolean saldoOcultoPorValidacion = false; // Indica si el saldo está oculto por una validación
    
     Runnable handler = null;

    public VentaPredefinirPlaca(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        jLabel12.setVisible(false);
        jLabel11.setVisible(false);
        enVenta_1 = false;
        instance1 = this;
        this.init();
        solicitarDatosSurtidor(0);

        jserialtest.setVisible(NovusUtils.productionServer());
        jcara.setVisible(NovusUtils.productionServer());


        //jLabel11.setVisible(HABILITAR_CONSULTA_SICOM);
        //jLabel12.setVisible(HABILITAR_CONSULTA_SICOM);
    }

    
    public static VentaPredefinirPlaca getInstance(InfoViewController vistaPrincipal, boolean modal) {
        if (VentaPredefinirPlaca.instance == null) {
            VentaPredefinirPlaca.instance = new VentaPredefinirPlaca(vistaPrincipal, modal);
        }
        VentaPredefinirPlaca.actualizarDatos();
        return VentaPredefinirPlaca.instance;
    }
    
     public static VentaPredefinirPlaca getInstance1() {
         
         return instance1;
   }
    
    /**
     * Método para inicializar el flujo de autorización por Placa
     * Muestra directamente el panel manual con campos vacíos y muestra el botón CONSULTAR
     * @author Diego Borja Padilla
     */
    public void inicializarFlujoPlaca() {
        esFlujoPlaca = true;
        
        // Limpiar todos los campos
        jPlaca.setText("");
        jKilometraje.setText("");
        jGalonaje.setText("");
        jMonto.setText("");
        jList1.clearSelection();
        jLabel5.setText("");
        
        DefaultListModel<String> listaVacia = new DefaultListModel<>();
        jList1.setModel(listaVacia);
        recepcionData = null;
        
        CardLayout layoutAditionalDataPanel = (CardLayout) this.jPanel2.getLayout();
        layoutAditionalDataPanel.show(jPanel2, "manual");
        
        jPlaca.setEditable(true);
        
        jLabel11.setVisible(false);
        
        jLabelConsultar.setVisible(true);
        
        jLabel10.setBounds(350, 720, 180, 70);  
        jLabelConsultar.setBounds(560, 720, 180, 70);  
        jLabel4.setBounds(770, 720, 180, 70);   
        
        jLabel4.setEnabled(false);
        
        jPlaca.setEditable(true);
        jPlaca.setEnabled(true);
        jKilometraje.setEditable(true);
        jKilometraje.setEnabled(true);
        jMonto.setEditable(true);
        jMonto.setEnabled(true);
        jGalonaje.setEditable(true);
        jGalonaje.setEnabled(true);
        
        // Hacer foco en el campo placa
        jPlaca.requestFocus();
    }

    private static void actualizarDatos() {
        instance.jLabel13.setText(Main.persona.getNombre() + " " + Main.persona.getApellidos());
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

    private void solicitarDatosSurtidor(int cara) {
        SetupDao sdao = new SetupDao();
        mangueras.clear();
        mangueras = sdao.getManguerasProductosAutorizacion();
        NovusUtils.printLn("[solicitarDatosSurtidor]Mangueras: " + mangueras);
    
        
        DefaultListModel<String> demoList = new DefaultListModel<>();
        for (Map.Entry<Integer, Surtidor> entry : mangueras.entrySet()) {
            Surtidor val = entry.getValue();
//            if ((cara == mangueras.get(5) || (cara == 0)) {
                if (recepcionData == null) {
                    String data = "C" + val.getCara() + "M" + val.getManguera() + ": " + val.getFamiliaDescripcion();
                    demoList.addElement(data);
                    NovusUtils.printLn("[solicitarDatosSurtidor]Data: " + data);    
                } else {
                    JsonArray lists = recepcionData.get("familias").getAsJsonArray();
                    for (JsonElement object : lists) {
                        JsonObject familias = object.getAsJsonObject();
                        if (familias.get("identificador_familia_abajo").getAsInt() == val.getFamiliaIdentificador()) {
                            String data = "C" + val.getCara() + "M" + val.getManguera() + ": " + val.getFamiliaDescripcion();
                            if (familias.has("esta_dentro_del_rango")) {
                                val.setEstaDentroDelRango(familias.get("esta_dentro_del_rango").getAsBoolean());
                            }
                            demoList.addElement(data);
                        
                        }
                    }
                }
//            }
        }
        jList1.setModel(demoList);
        
    }

    private void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        InfoViewController.NotificadorClientePropio
                = new Notificador() {
            @Override
            public void send(JsonObject data) {
                recepcionNotificacionExterna(data);
            }
        };
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            jPlaca.requestFocus();
        }
    }

    public MedioIdentificacionBean getIdentifierMeanSelected() {
        return this.identifierMeanSelected;
    }

    public void setIdentifierMeanSelected(MedioIdentificacionBean identifierMeanId) {
        this.identifierMeanSelected = identifierMeanId;
    }

    MedioIdentificacionBean identifierMeanSelected = null;

    @SuppressWarnings("unchecked")
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jcara = new javax.swing.JFormattedTextField();
        jserialtest = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPlaca = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jKilometraje = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabelGalonaje = new javax.swing.JLabel();
        jGalonaje = new javax.swing.JTextField();
        jLabelMonto = new javax.swing.JLabel();
        jMonto = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel1 = new TecladoExtendido();
        jNotificacion = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setLayout(null);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel14MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel14);
        jLabel14.setBounds(10, 9, 70, 71);

        jcara.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jcara.setText("1");
        jcara.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        pnl_principal.add(jcara);
        jcara.setBounds(650, 20, 40, 50);

        jserialtest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jserialtestActionPerformed(evt);
            }
        });
        pnl_principal.add(jserialtest);
        jserialtest.setBounds(700, 20, 300, 50);

        jLabel13.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 0));
        jLabel13.setText("PROMOTOR");
        pnl_principal.add(jLabel13);
        jLabel13.setBounds(100, 30, 720, 50);

        jLabel12.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-warning-small.png"))); // NOI18N
        jLabel12.setText("MANUAL");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel12MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel12);
        jLabel12.setBounds(1030, 10, 180, 70);

        jLabel11.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-warning-small.png"))); // NOI18N
        jLabel11.setText("SICOM");
        jLabel11.setEnabled(false);
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel11);
        jLabel11.setBounds(590, 720, 180, 70);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N - Tamaño uniforme para todos los mensajes del footer
        jLabel5.setForeground(new java.awt.Color(255, 255, 0));
        pnl_principal.add(jLabel5);
        jLabel5.setBounds(10, 700, 570, 100);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel4.setText("GUARDAR");
        jLabel4.setEnabled(false);
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel4);
        jLabel4.setBounds(850, 720, 180, 70); // GUARDAR - posición inicial (ajustada para que quepa bien)

        jLabel10.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel10.setText("CANCELAR");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel10);
        jLabel10.setBounds(650, 720, 180, 70); // CANCELAR - posición inicial (ajustada para que quepa bien)

        // Botón CONSULTAR (visible solo en flujo de Placa)
        jLabelConsultar = new javax.swing.JLabel();
        jLabelConsultar.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabelConsultar.setForeground(new java.awt.Color(255, 255, 255));
        jLabelConsultar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabelConsultar.setText("CONSULTAR");
        jLabelConsultar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelConsultar.setVisible(false);
        jLabelConsultar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelConsultarMouseReleased(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelConsultarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelConsultarMouseExited(evt);
            }
        });
        pnl_principal.add(jLabelConsultar);
        jLabelConsultar.setBounds(590, 720, 180, 70);

        jLabel9.setFont(new java.awt.Font("Conthrax", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("PRE-AUTORIZACIÓN VENTA");
        pnl_principal.add(jLabel9);
        jLabel9.setBounds(100, 10, 730, 24);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.CardLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setOpaque(false);
        jPanel4.setLayout(null);

        jLabel7.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel4.add(jLabel7);
        jLabel7.setBounds(120, 420, 910, 190);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ibutton-rumbo.png"))); // NOI18N
        jLabel6.setText("\n\nPRESENTE CHIP IBUTTON");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel6);
        jLabel6.setBounds(170, 80, 820, 420);

        jPanel2.add(jPanel4, "dispositivo");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 22));
        jLabel1.setText("INGRESE PLACA");
        jPanel3.add(jLabel1);
        jLabel1.setBounds(640, 10, 240, 28);

        jPlaca.setBackground(new java.awt.Color(239, 239, 239));
        jPlaca.setFont(new java.awt.Font("Roboto", 1, 50));
        jPlaca.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        jPlaca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPlacaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPlacaFocusLost(evt);
            }
        });
        jPlaca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPlacaMouseReleased(evt);
            }
        });
        jPlaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlacaActionPerformed(evt);
            }
        });
        jPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPlacaKeyTyped(evt);
            }
        });
        jPanel3.add(jPlaca);
        jPlaca.setBounds(640, 40, 440, 58);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 22)); 
        jLabel2.setText("KILOMETRAJE");
        jPanel3.add(jLabel2);
        jLabel2.setBounds(640, 100, 220, 28); 

        jKilometraje.setBackground(new java.awt.Color(239, 239, 239));
        jKilometraje.setFont(new java.awt.Font("Roboto", 1, 50));
        jKilometraje.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        jKilometraje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jKilometrajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jKilometrajeFocusLost(evt);
            }
        });
        jKilometraje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jKilometrajeActionPerformed(evt);
            }
        });
        jKilometraje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jKilometrajeKeyTyped(evt);
            }
        });
        jPanel3.add(jKilometraje);
        jKilometraje.setBounds(640, 125, 440, 58);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 22));
        jLabel3.setText("SELECCIONE MANGUERA");
        jPanel3.add(jLabel3);
        jLabel3.setBounds(80, 10, 330, 28);

        jScrollPane1.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N

        jList1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
        jList1.setFont(new java.awt.Font("Arial", 0, 24));
        jList1.setSelectionBackground(new java.awt.Color(186, 12, 47));
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList1MouseReleased(evt);
            }
        });
        jList1.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                jList1VetoableChange(evt);
            }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel3.add(jScrollPane1);
        jScrollPane1.setBounds(80, 40, 510, 145);

        // Label y campo GALONAJE (columna izquierda)
        jLabelGalonaje.setFont(new java.awt.Font("Arial", 0, 22));
        jLabelGalonaje.setText("GALONAJE");
        jPanel3.add(jLabelGalonaje);
        jLabelGalonaje.setBounds(80, 185, 240, 28);

        jGalonaje.setBackground(new java.awt.Color(255, 255, 255));
        jGalonaje.setFont(new java.awt.Font("Roboto", 1, 50));
        jGalonaje.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        jGalonaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jGalonajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jGalonajeFocusLost(evt);
            }
        });
        jGalonaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jGalonajeKeyTyped(evt);
            }
        });
        jPanel3.add(jGalonaje);
        jGalonaje.setBounds(80, 210, 510, 58);

        // Label y campo MONTO (columna derecha)
        jLabelMonto.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N
        jLabelMonto.setText("MONTO");
        jPanel3.add(jLabelMonto);
        jLabelMonto.setBounds(640, 185, 240, 28);

        jMonto.setBackground(new java.awt.Color(255, 255, 255));
        jMonto.setFont(new java.awt.Font("Roboto", 1, 50));
        jMonto.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        jMonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jMontoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jMontoFocusLost(evt);
            }
        });
        jMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jMontoKeyTyped(evt);
            }
        });
        jPanel3.add(jMonto);
        jMonto.setBounds(640, 210, 440, 58);

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        jPanel3.add(jPanel1);
        jPanel1.setBounds(70, 280, 1040, 400);

        jPanel2.add(jPanel3, "manual");

        pnl_principal.add(jPanel2);
        jPanel2.setBounds(40, 90, 1190, 610);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N - Tamaño reducido para validaciones
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(10, 720, 560, 60);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnl_principal.add(jLabel8);
        jLabel8.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>                        

  private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
      // TODO add your handling code here:
  }//GEN-LAST:event_jPanel1MouseReleased

  private void jKilometrajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jKilometrajeFocusGained
      jKilometraje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
      NovusUtils.deshabilitarCopiarPegar(jKilometraje);
  }//GEN-LAST:event_jKilometrajeFocusGained

  private void jKilometrajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jKilometrajeFocusLost
      jKilometraje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
  }//GEN-LAST:event_jKilometrajeFocusLost

  private void jKilometrajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jKilometrajeActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_jKilometrajeActionPerformed

  private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseReleased
      // Si el botón está deshabilitado, no hacer nada (no ejecutar ninguna acción)
      if (!jLabel4.isEnabled()) {
          return;
      }
      
      NovusUtils.beep();
      crearAutorizacion();
  }//GEN-LAST:event_jLabel4MouseReleased

 
  
  private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseReleased
    NovusUtils.printLn("[jLabel11MouseReleased] Botón SICOM clickeado con producto: " + productoSeleccionado);
    //Cabiar por el servicio 
    if("GLP".equals(productoSeleccionado)){
      validacionSicom();
    }else{
      NovusUtils.printLn("[jLabel11MouseReleased] Producto no es GLP");
      mostrarPanelMensaje("SICOM SOLO DISPONIBLE PARA GLP", "/com/firefuel/resources/btBad.png", false);
    }
  }//GEN-LAST:event_jLabel11MouseReleased

  private void jPlacaActionPerformed(java.awt.event.ActionEvent evt) {
      // TODO add your handling code here:
  }

  private void jLabel12MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseReleased
//      if (HABILITAR_CONSULTA_SICOM) {
//          cambioManual();
//      }
  }

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseReleased
      NovusUtils.beep();
      liberarAutorizacion();
      cancelar();
  }

  private void jLabelConsultarMouseReleased(java.awt.event.MouseEvent evt) {
      NovusUtils.beep();
      NovusUtils.printLn("[VentaPredefinirPlaca] Botón CONSULTAR clickeado - Flujo de Placa");
      
      String placa = jPlaca.getText().trim();
      String odometro = jKilometraje.getText().trim();
      String monto = jMonto.getText().trim();
      String galonaje = jGalonaje.getText().trim();
      
      if (placa.isEmpty()) {
          jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
          jLabel5.setText("INGRESE LA PLACA");
          jPlaca.requestFocus();
          return;
      }
      
      if (odometro.isEmpty()) {
          jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
          jLabel5.setText("INGRESE EL KILOMETRAJE");
          jKilometraje.requestFocus();
          return;
      }
      
      if (monto.isEmpty() && galonaje.isEmpty()) {
          jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
          jLabel5.setText("INGRESE MONTO O GALONAJE");
          return;
      }
      
      if (!monto.isEmpty() && !galonaje.isEmpty()) {
          jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
          jLabel5.setText("SOLO PUEDE INGRESAR MONTO O GALONAJE, NO AMBOS");
          return;
      }
      

      if (!monto.isEmpty()) {
          // Verificar que no comience con 0 (excepto si es solo "0")
          if (monto.length() > 1 && monto.startsWith("0")) {
              jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
              jLabel5.setText("MONTO INVÁLIDO");
              jMonto.setText("");
              jMonto.requestFocus();
              // Limpiar mensajes de validación
              jNotificacion.setText("");
              jNotificacion.setVisible(false);
              // Limpiar mensaje después de 3 segundos
              Utils.setTimeout("", () -> {
                  String textoActual = jLabel5.getText();
                  if (textoActual != null && textoActual.equals("MONTO INVÁLIDO")) {
                      jLabel5.setText("");
                      jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  }
              }, 3000);
              return;
          }
          
          // Validación: monto debe ser mayor a 0
          try {
              double valorMonto = Double.parseDouble(monto);
              if (valorMonto <= 0) {
                  jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  jLabel5.setText("MONTO INVÁLIDO");
                  jMonto.setText("");
                  jMonto.requestFocus();
                  // Limpiar mensajes de validación
                  jNotificacion.setText("");
                  jNotificacion.setVisible(false);
                  // Limpiar mensaje después de 3 segundos
                  Utils.setTimeout("", () -> {
                      String textoActual = jLabel5.getText();
                      if (textoActual != null && textoActual.equals("MONTO INVÁLIDO")) {
                          jLabel5.setText("");
                          jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                      }
                  }, 3000);
                  return;
              }
          } catch (NumberFormatException e) {
              jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
              jLabel5.setText("ERROR: MONTO INVÁLIDO");
              jMonto.setText("");
              jMonto.requestFocus();
              return;
          }
      }
      
      // Validación: galonaje no puede comenzar con 0
      if (!galonaje.isEmpty()) {
          // Verificar que no comience con 0 (excepto si es solo "0")
          if (galonaje.length() > 1 && galonaje.startsWith("0")) {
              jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
              jLabel5.setText("GALONAJE INVÁLIDO");
              jGalonaje.setText("");
              jGalonaje.requestFocus();
              // Limpiar mensajes de validación
              jNotificacion.setText("");
              jNotificacion.setVisible(false);
              // Limpiar mensaje después de 3 segundos
              Utils.setTimeout("", () -> {
                  String textoActual = jLabel5.getText();
                  if (textoActual != null && textoActual.equals("GALONAJE INVÁLIDO")) {
                      jLabel5.setText("");
                      jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  }
              }, 3000);
              return;
          }
          
          // Validación: galonaje debe ser mayor a 0
          try {
              double valorGalonaje = Double.parseDouble(galonaje);
              if (valorGalonaje <= 0) {
                  jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Tamaño uniforme del footer
                  jLabel5.setText("GALONAJE INVÁLIDO");
                  jGalonaje.setText("");
                  jGalonaje.requestFocus();
                  // Limpiar mensajes de validación
                  jNotificacion.setText("");
                  jNotificacion.setVisible(false);
                  // Limpiar mensaje después de 3 segundos
                  Utils.setTimeout("", () -> {
                      String textoActual = jLabel5.getText();
                      if (textoActual != null && textoActual.equals("GALONAJE INVÁLIDO")) {
                          jLabel5.setText("");
                          jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Restaurar tamaño original
                      }
                  }, 3000);
                  return;
              }
          } catch (NumberFormatException e) {
              jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
              jLabel5.setText("ERROR: GALONAJE INVÁLIDO");
              jGalonaje.setText("");
              jGalonaje.requestFocus();
              return;
          }
      }
      
      mostrarPanelMensaje("CONSULTANDO AUTORIZACIÓN", "/com/firefuel/resources/loader_fac.gif", true);
      
      // Ejecutar consulta en hilo separado
      final String placaFinal = placa;
      final String odometroFinal = odometro;
      final String montoFinal = monto;
      final String galonajeFinal = galonaje;
      
      Runnable consultarCliente = () -> {
          try {
              JsonObject request = new JsonObject();
              
              request.addProperty("kilometros", odometroFinal);
              request.addProperty("identificadorEstacion", Main.credencial.getEmpresas_id());
              request.addProperty("fechaConsulta", sdf.format(new Date()));
              request.addProperty("identificadorPromotor", Main.persona.getId());
              
              request.addProperty("identificador", placaFinal);
              request.addProperty("tipoIdentificador", "PLACA");
              
              if (!montoFinal.isEmpty()) {
                  try {
                      int montoInt = (int) Double.parseDouble(montoFinal);
                      request.addProperty("monto", montoInt);
                      request.add("galonaje", com.google.gson.JsonNull.INSTANCE); 
                      NovusUtils.printLn("[VentaPredefinirPlaca] Enviando MONTO: " + montoInt + ", GALONAJE: null");
                  } catch (NumberFormatException e) {
                      NovusUtils.printLn("[VentaPredefinirPlaca] Error al convertir monto: " + montoFinal);
                      SwingUtilities.invokeLater(() -> {
                          cambiarPanelHome();
                          mostrarPanelMensaje("ERROR: MONTO INVÁLIDO", "/com/firefuel/resources/btBad.png", false);
                      });
                      return;
                  }
              } else if (!galonajeFinal.isEmpty()) {
                  try {
                      int galonajeInt = (int) Double.parseDouble(galonajeFinal);
                      request.add("monto", com.google.gson.JsonNull.INSTANCE); 
                      NovusUtils.printLn("[VentaPredefinirPlaca] Enviando MONTO: null, GALONAJE: " + galonajeInt);
                  } catch (NumberFormatException e) {
                      NovusUtils.printLn("[VentaPredefinirPlaca] Error al convertir galonaje: " + galonajeFinal);
                      SwingUtilities.invokeLater(() -> {
                          cambiarPanelHome();
                          mostrarPanelMensaje("ERROR: GALONAJE INVÁLIDO", "/com/firefuel/resources/btBad.png", false);
                      });
                      return;
                  }
              }
              
              NovusUtils.printLn("[VentaPredefinirPlaca] Request completo enviado:");
              NovusUtils.printLn(Main.ANSI_CYAN + request.toString() + Main.ANSI_RESET);
              
              JsonObject response = ClienteFacade.consultaValidacionVentaPorPlaca(request);
              
              SwingUtilities.invokeLater(() -> {
                  cambiarPanelHome();
                  procesarRespuestaConsultaPlaca(response, placaFinal, odometroFinal, montoFinal, galonajeFinal);
              });
              
          } catch (Exception e) {
              NovusUtils.printLn("[VentaPredefinirPlaca] Error en consulta: " + e.getMessage());
              Logger.getLogger(VentaPredefinirPlaca.class.getName()).log(Level.SEVERE, null, e);
              SwingUtilities.invokeLater(() -> {
                  cambiarPanelHome();
                  mostrarPanelMensaje("ERROR AL CONSULTAR AUTORIZACIÓN", "/com/firefuel/resources/btBad.png", false);
              });
          }
      };
      
      CompletableFuture.runAsync(consultarCliente);
  }

    /**
     * Procesa la respuesta de la consulta por placa
     * El servidor puede enviar información de mangueras disponibles desde las cuales se puede vender
     * @param response Respuesta del servidor
     * @param placa Placa consultada
     * @param odometro Kilometraje ingresado
     * @param monto Monto ingresado (puede estar vacío)
     * @param galonaje Galonaje ingresado (puede estar vacío)
     * @author Diego Borja Padilla
     */
    private void procesarRespuestaConsultaPlaca(JsonObject response, String placa, String odometro, 
                                                String monto, String galonaje) {
        if (response == null) {
            // NO mostrar mensaje de error en el footer, solo en el panel de error
            // jLabel5.setText("ERROR: NO HAY RESPUESTA DEL SERVIDOR");
            mostrarPanelMensaje("ERROR DE COMUNICACIÓN CON EL SERVIDOR", "/com/firefuel/resources/btBad.png", false);
            return;
        }
        
        if (response.has("mensajeError") && !response.get("mensajeError").isJsonNull()) {
            String mensajeError = response.get("mensajeError").getAsString();
            mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false);
            return;
        }
        
        int httpStatus = response.has("httpStatus") && !response.get("httpStatus").isJsonNull() 
            ? response.get("httpStatus").getAsInt() : 0;
        
        boolean isSuccess = (httpStatus == 200) && 
                           response.has("success") && 
                           !response.get("success").isJsonNull() &&
                           response.get("success").getAsBoolean();
        
            if (!isSuccess) {
            String mensajeError;
            
            if (httpStatus != 200) {
                mensajeError = "ERROR: Código HTTP " + httpStatus;
            } else if (response.has("message") && !response.get("message").isJsonNull()) {
                mensajeError = response.get("message").getAsString();
            } else {
                mensajeError = "ERROR EN LA CONSULTA";
            }
            
            // Si data es null, solo mostrar el error y retornar
            if (!response.has("data") || response.get("data").isJsonNull()) {
                NovusUtils.printLn("[VentaPredefinirPlaca] Data es null, mostrando solo mensaje de error en panel (NO en footer)");
                // NO mostrar mensaje de error en el footer, solo en el panel de error
                // jLabel5.setText(mensajeError);
                
                Runnable handlerCerrar = () -> {
                    cambiarPanelHome();
                    jPlaca.setEditable(true);
                    jPlaca.setEnabled(true);
                    jKilometraje.setEditable(true);
                    jKilometraje.setEnabled(true);
                    jMonto.setEditable(true);
                    jMonto.setEnabled(true);
                    jGalonaje.setEditable(true);
                    jGalonaje.setEnabled(true);
                    jLabel4.setEnabled(false);
                    jMonto.setText("");
                    jGalonaje.setText("");
                    jMonto.requestFocus();
                };
                
                mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                return;
            }
            
            // Si data existe y es un array, intentar extraer saldo y nombre cliente
            double saldoDisponible = 0.0;
            String nombreClienteError = "";
            
            if (response.get("data").isJsonArray()) {
                JsonArray dataArray = response.get("data").getAsJsonArray();
                NovusUtils.printLn("[VentaPredefinirPlaca] Procesando dataArray con " + dataArray.size() + " elementos para extraer saldo y nombre cliente");
                
                for (JsonElement element : dataArray) {
                    JsonObject item = element.getAsJsonObject();
                    NovusUtils.printLn("[VentaPredefinirPlaca] Item del array: " + item.toString());
                    
                    if (item.has("saldo") && !item.get("saldo").isJsonNull()) {
                        try {
                            double saldo = 0.0;
                            if (item.get("saldo").isJsonPrimitive()) {
                                if (item.get("saldo").getAsJsonPrimitive().isNumber()) {
                                    saldo = item.get("saldo").getAsDouble();
                                } else if (item.get("saldo").getAsJsonPrimitive().isString()) {
                                    saldo = Double.parseDouble(item.get("saldo").getAsString());
                                }
                            }
                            
                            if (saldo > 0 && (saldoDisponible == 0.0 || saldo > saldoDisponible)) {
                                saldoDisponible = saldo;
                                NovusUtils.printLn("[VentaPredefinirPlaca] Saldo extraído: " + saldoDisponible);
                            }
                        } catch (Exception e) {
                            NovusUtils.printLn("[VentaPredefinirPlaca] Error al obtener saldo del error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    
                    if (nombreClienteError.isEmpty() && item.has("nombreCliente") && !item.get("nombreCliente").isJsonNull()) {
                        nombreClienteError = item.get("nombreCliente").getAsString();
                        NovusUtils.printLn("[VentaPredefinirPlaca] Nombre cliente extraído: " + nombreClienteError);
                    }
                }
            } else {
                NovusUtils.printLn("[VentaPredefinirPlaca] Data no es un array, mostrando solo mensaje de error");
            }

            if (saldoDisponible > 0) {
                String textoFooter;
                if (!nombreClienteError.isEmpty()) {
                    textoFooter = "<html>" + nombreClienteError + "<br/>SALDO: $ " + df.format(saldoDisponible) + "</html>";
                } else {
                    textoFooter = "<html>SALDO: $ " + df.format(saldoDisponible) + "</html>";
                }
                establecerSaldoEnFooter(textoFooter);
                NovusUtils.printLn("[VentaPredefinirPlaca] Saldo numérico: " + saldoDisponible);
            } else {
                NovusUtils.printLn("[VentaPredefinirPlaca] No se encontró saldo disponible, mostrando solo mensaje de error en panel");
            }
            
            final double saldoFinal = saldoDisponible;
            final String nombreClienteFinal = nombreClienteError;
            
            Runnable handlerCerrar = () -> {
                cambiarPanelHome();
                
                jPlaca.setEditable(true);
                jPlaca.setEnabled(true);
                jKilometraje.setEditable(true);
                jKilometraje.setEnabled(true);
                jMonto.setEditable(true);
                jMonto.setEnabled(true);
                jGalonaje.setEditable(true);
                jGalonaje.setEnabled(true);
                
                jLabel4.setEnabled(false);
                
                jMonto.setText("");
                jGalonaje.setText("");
                
                if (saldoFinal > 0) {
                    String textoFooter;
                    if (!nombreClienteFinal.isEmpty()) {
                        textoFooter = "<html>" + nombreClienteFinal + "<br/>SALDO: $ " + df.format(saldoFinal) + "</html>";
                    } else {
                        textoFooter = "<html>SALDO: $ " + df.format(saldoFinal) + "</html>";
                    }
                    establecerSaldoEnFooter(textoFooter);
                    NovusUtils.printLn("[VentaPredefinirPlaca] Saldo reestablecido en footer después de cerrar panel");
                }
                
                jMonto.requestFocus();
            };
            
            mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
            return;
        }
        
        if (httpStatus == 200 && response.has("data") && !response.get("data").isJsonNull() && response.get("data").isJsonArray()) {
            JsonArray dataArray = response.get("data").getAsJsonArray();
            
            if (dataArray.size() == 0) {
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
            jLabel5.setText("NO SE ENCONTRÓ INFORMACIÓN PARA LA PLACA");
                mostrarPanelMensaje("NO SE ENCONTRÓ INFORMACIÓN PARA LA PLACA", "/com/firefuel/resources/btBad.png", false);
                return;
            }
            
            recepcionData = new JsonObject();
            recepcionData.addProperty("medioAutorizacion", "placa");
            recepcionData.addProperty("placaVehiculo", placa);
            
            JsonArray familiasArray = new JsonArray();
            double saldoTotal = 0.0;
            String nombreCliente = "";
            String documentoIdentificacionCliente = "";
            String tipoCupo = "";
            boolean esComunidades = false;
            
            for (JsonElement element : dataArray) {
                JsonObject item = element.getAsJsonObject();
                
                if (item.has("productos_id") && !item.get("productos_id").isJsonNull()) {
                    int productosId = item.get("productos_id").getAsInt();
                    
                    JsonObject familia = new JsonObject();
                    familia.addProperty("identificador_familia_abajo", productosId);
                    if (item.has("description") && !item.get("description").isJsonNull()) {
                        familia.addProperty("description", item.get("description").getAsString());
                    }
                    familiasArray.add(familia);
                    
                    NovusUtils.printLn("[VentaPredefinirPlaca] Producto ID: " + productosId + " agregado como familia");
                }
                
                if (item.has("saldo") && !item.get("saldo").isJsonNull()) {
                    try {
                        double saldo = item.get("saldo").getAsDouble();
                        // 🔍 DEBUG: Log para verificar que se está leyendo el saldo correctamente del item
                        NovusUtils.printLn("🔍 [LECTURA SALDO ITEM] Saldo leído del item: " + saldo);
                        if (saldoTotal == 0.0 || saldo > saldoTotal) {
                            saldoTotal = saldo;
                            NovusUtils.printLn("🔍 [LECTURA SALDO ITEM] saldoTotal actualizado a: " + saldoTotal);
                        }
                    } catch (Exception e) {
                        NovusUtils.printLn("[VentaPredefinirPlaca] Error al obtener saldo: " + e.getMessage());
                    }
                }
                
                if (nombreCliente.isEmpty() && item.has("nombreCliente") && !item.get("nombreCliente").isJsonNull()) {
                    nombreCliente = item.get("nombreCliente").getAsString();
                    NovusUtils.printLn("[VentaPredefinirPlaca] Nombre cliente obtenido: " + nombreCliente);
                }
                
                if (documentoIdentificacionCliente.isEmpty() && item.has("documentoIdentificacionCliente") && !item.get("documentoIdentificacionCliente").isJsonNull()) {
                    documentoIdentificacionCliente = item.get("documentoIdentificacionCliente").getAsString();
                    NovusUtils.printLn("[VentaPredefinirPlaca] Documento cliente obtenido: " + documentoIdentificacionCliente);
                }
                
                if (tipoCupo.isEmpty() && item.has("tipoCupo") && !item.get("tipoCupo").isJsonNull()) {
                    tipoCupo = item.get("tipoCupo").getAsString();
                    NovusUtils.printLn("[VentaPredefinirPlaca] Tipo cupo obtenido: " + tipoCupo);
                }
                
                // Extraer es_comunidades del primer item que lo tenga
                if (!esComunidades && item.has("es_comunidades") && !item.get("es_comunidades").isJsonNull()) {
                    esComunidades = item.get("es_comunidades").getAsBoolean();
                    NovusUtils.printLn("[VentaPredefinirPlaca] es_comunidades obtenido: " + esComunidades);
                }
            }
            
            recepcionData.add("familias", familiasArray);
            
            if (saldoTotal > 0) {
                recepcionData.addProperty("saldo", saldoTotal);
                // 🔍 DEBUG: Log para verificar que el saldo se guardó correctamente
                NovusUtils.printLn("✅ [SALDO GUARDADO] Saldo guardado en recepcionData: " + saldoTotal);
            } else {
                NovusUtils.printLn("⚠️ [SALDO GUARDADO] saldoTotal es 0 o negativo, no se guarda en recepcionData");
            }
            
            if (!documentoIdentificacionCliente.isEmpty()) {
                recepcionData.addProperty("documentoIdentificacionCliente", documentoIdentificacionCliente);
            } else {
                recepcionData.addProperty("documentoIdentificacionCliente", "");
                NovusUtils.printLn("[VentaPredefinirPlaca] ADVERTENCIA: documentoIdentificacionCliente no encontrado en la respuesta");
            }
            
            if (!nombreCliente.isEmpty()) {
                recepcionData.addProperty("nombreCliente", nombreCliente);
            } else {
                recepcionData.addProperty("nombreCliente", "");
                NovusUtils.printLn("[VentaPredefinirPlaca] ADVERTENCIA: nombreCliente no encontrado en la respuesta");
            }
            
            recepcionData.addProperty("serialDispositivo", placa);
            
            if (!tipoCupo.isEmpty()) {
                recepcionData.addProperty("tipoCupo", tipoCupo);
                NovusUtils.printLn("[VentaPredefinirPlaca] Tipo cupo guardado: " + tipoCupo);
            } else {
                recepcionData.addProperty("tipoCupo", "D"); // Por defecto 'D' (Dinero)
                NovusUtils.printLn("[VentaPredefinirPlaca] ADVERTENCIA: tipoCupo no encontrado en la respuesta, usando valor por defecto 'D'");
            }
            
            recepcionData.addProperty("es_comunidades", esComunidades);
            recepcionData.addProperty("clientesPropios", true);
            
            recepcionData.addProperty("identificadorCupo", 0);
            
            recepcionData.addProperty("empresas_id", Main.credencial.getEmpresas_id());
            
            NovusUtils.printLn("[VentaPredefinirPlaca] Se procesaron " + familiasArray.size() + " familias/mangueras disponibles");
            
            solicitarDatosSurtidor(0);
            
            if (jList1.getModel().getSize() == 0) {
                String mensajeError = "NO SE ENCONTRÓ UN PRODUCTO O MANGUERA PARA VENDER A ESA PLACA";
                
                Runnable handlerCerrar = () -> {
                    cambiarPanelHome();
                    
                    recepcionData = null;
                    
                    jLabel5.setText("");
                    
                    jPlaca.setEditable(true);
                    jPlaca.setEnabled(true);
                    jKilometraje.setEditable(true);
                    jKilometraje.setEnabled(true);
                    jMonto.setEditable(true);
                    jMonto.setEnabled(true);
                    jGalonaje.setEditable(true);
                    jGalonaje.setEnabled(true);
                    
                    jLabel4.setEnabled(false);
                    
                    jMonto.setText("");
                    jGalonaje.setText("");
                    
                    jPlaca.requestFocus();
                };
                
                mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                
                NovusUtils.printLn("[VentaPredefinirPlaca] ERROR: No se encontraron mangueras para los productos_id recibidos");
                return;
            }
            
            if (!nombreCliente.isEmpty()) {
                if (saldoTotal > 0) {
                    establecerSaldoEnFooter("<html>" + nombreCliente + "<br/>SALDO: $ " + df.format(saldoTotal) + "</html>");
                } else {
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                    jLabel5.setText("<html>" + nombreCliente + "</html>");
                    // Limpiar mensajes de validación
                    jNotificacion.setText("");
                    jNotificacion.setVisible(false);
                }
            } else if (saldoTotal > 0) {
                establecerSaldoEnFooter("<html>SALDO: $ " + df.format(saldoTotal) + "</html>");
            } else {
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                jLabel5.setText("AUTORIZACIÓN CONSULTADA EXITOSAMENTE");
                // Limpiar mensajes de validación
                jNotificacion.setText("");
                jNotificacion.setVisible(false);
            }
            
            jPlaca.setEditable(false);
            jPlaca.setEnabled(false);
            jKilometraje.setEditable(false);
            jKilometraje.setEnabled(false);
            jMonto.setEditable(false);
            jMonto.setEnabled(false);
            jGalonaje.setEditable(false);
            jGalonaje.setEnabled(false);
            
            jLabel4.setEnabled(true);
            
            NovusUtils.printLn("[VentaPredefinirPlaca] Consulta exitosa para placa: " + placa);
            NovusUtils.printLn("[VentaPredefinirPlaca] Mangueras disponibles: " + jList1.getModel().getSize());
            NovusUtils.printLn("[VentaPredefinirPlaca] Campos bloqueados y botón GUARDAR habilitado");
            
        } else {
            jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
            jLabel5.setText("NO SE ENCONTRÓ INFORMACIÓN PARA LA PLACA");
            mostrarPanelMensaje("NO SE ENCONTRÓ INFORMACIÓN PARA LA PLACA", "/com/firefuel/resources/btBad.png", false);
        }
    }

    /**
     * Procesa la respuesta de la consulta por iButton
     * Similar al flujo de placa pero adaptado para iButton
     * @param response Respuesta del servidor
     * @param identifierRequest Request con información del dispositivo iButton
     * @param kilometros Kilometraje ingresado
     * @param monto Monto ingresado (puede estar vacío)
     * @param galonaje Galonaje ingresado (puede estar vacío)
     * @author Diego Borja Padilla
     */
    private void procesarRespuestaConsultaIButton(JsonObject response, JsonObject identifierRequest, 
                                                   String kilometros, String monto, String galonaje) {
        if (response == null) {
            mostrarPanelMensaje("ERROR DE COMUNICACIÓN CON EL SERVIDOR", "/com/firefuel/resources/btBad.png", false);
            return;
        }
        
        if (response.has("mensajeError") && !response.get("mensajeError").isJsonNull()) {
            String mensajeError = response.get("mensajeError").getAsString();
            // NO mostrar mensaje de error en el footer, solo en el panel de error
            // jLabel5.setText(mensajeError);
            mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false);
            return;
        }
        
        int httpStatus = response.has("httpStatus") && !response.get("httpStatus").isJsonNull() 
            ? response.get("httpStatus").getAsInt() : 0;
        
        boolean isSuccess = (httpStatus == 200) && 
                           response.has("success") && 
                           !response.get("success").isJsonNull() &&
                           response.get("success").getAsBoolean();
        
        if (!isSuccess) {
            String mensajeError;
            
            if (httpStatus != 200) {
                mensajeError = "ERROR: Código HTTP " + httpStatus;
            } else if (response.has("message") && !response.get("message").isJsonNull()) {
                mensajeError = response.get("message").getAsString();
            } else {
                mensajeError = "ERROR EN LA CONSULTA";
            }
            
            // Si data es null, solo mostrar el error y retornar
            if (!response.has("data") || response.get("data").isJsonNull()) {
                NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Data es null, mostrando solo mensaje de error en panel (NO en footer)");
                
                Runnable handlerCerrar = () -> {
                    this.dispose();
                    instance = null;
                    if (parent != null) {
                        SeleccionTipoAutorizacionView seleccionView = new SeleccionTipoAutorizacionView(parent, true);
                        seleccionView.setVisible(true);
                    }
                };
                
                mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                return;
            }
            
            // Si data existe y es un array, intentar extraer saldo y nombre cliente
            double saldoDisponible = 0.0;
            String nombreClienteError = "";
            
            if (response.get("data").isJsonArray()) {
                JsonArray dataArray = response.get("data").getAsJsonArray();
                
                for (JsonElement element : dataArray) {
                    JsonObject item = element.getAsJsonObject();
                    
                    // Obtener saldo disponible
                    if (item.has("saldo") && !item.get("saldo").isJsonNull()) {
                        try {
                            double saldo = 0.0;
                            if (item.get("saldo").isJsonPrimitive()) {
                                if (item.get("saldo").getAsJsonPrimitive().isNumber()) {
                                    saldo = item.get("saldo").getAsDouble();
                                } else if (item.get("saldo").getAsJsonPrimitive().isString()) {
                                    saldo = Double.parseDouble(item.get("saldo").getAsString());
                                }
                            }
                            
                            if (saldo > 0 && (saldoDisponible == 0.0 || saldo > saldoDisponible)) {
                                saldoDisponible = saldo;
                            }
                        } catch (Exception e) {
                            NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Error al obtener saldo del error: " + e.getMessage());
                        }
                    }
                    
                    if (nombreClienteError.isEmpty() && item.has("nombreCliente") && !item.get("nombreCliente").isJsonNull()) {
                        nombreClienteError = item.get("nombreCliente").getAsString();
                    }
                }
            } else {
                NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Data no es un array, mostrando solo mensaje de error");
            }
            
            final double saldoFinal = saldoDisponible;
            final String nombreClienteFinal = nombreClienteError;
            
            if (saldoFinal > 0) {
                String textoFooter;
                if (!nombreClienteFinal.isEmpty()) {
                    textoFooter = "<html>" + nombreClienteFinal + "<br/>SALDO: $ " + df.format(saldoFinal) + "</html>";
                } else {
                    textoFooter = "<html>SALDO: $ " + df.format(saldoFinal) + "</html>";
                }
                jLabel5.setText(textoFooter);
            } else {
            }
            
            Runnable handlerCerrar = () -> {
                this.dispose();
                
                instance = null;
                
                if (parent != null) {
                    SeleccionTipoAutorizacionView seleccionView = new SeleccionTipoAutorizacionView(parent, true);
                    seleccionView.setVisible(true);
                }
            };
            
            mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
            return;
        }
        
        if (httpStatus == 200 && response.has("data") && !response.get("data").isJsonNull() && response.get("data").isJsonArray()) {
            JsonArray dataArray = response.get("data").getAsJsonArray();
            
            if (dataArray.size() == 0) {
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
            jLabel5.setText("NO SE ENCONTRÓ INFORMACIÓN PARA EL DISPOSITIVO");
                mostrarPanelMensaje("NO SE ENCONTRÓ INFORMACIÓN PARA EL DISPOSITIVO", "/com/firefuel/resources/btBad.png", false);
                return;
            }
            
            recepcionData = new JsonObject();
            recepcionData.addProperty("medioAutorizacion", "ibutton");
            
            String serialDispositivo = identifierRequest.get("serial").getAsString();
            recepcionData.addProperty("serialDispositivo", serialDispositivo);
            
            JsonArray familiasArray = new JsonArray();
            double saldoTotal = 0.0;
            String nombreCliente = "";
            String documentoIdentificacionCliente = "";
            String tipoCupo = "";
            String placaVehiculo = "";
            boolean esComunidades = false;
            
            for (JsonElement element : dataArray) {
                JsonObject item = element.getAsJsonObject();
                
                if (item.has("productos_id") && !item.get("productos_id").isJsonNull()) {
                    int productosId = item.get("productos_id").getAsInt();
                    
                    JsonObject familia = new JsonObject();
                    familia.addProperty("identificador_familia_abajo", productosId);
                    if (item.has("description") && !item.get("description").isJsonNull()) {
                        familia.addProperty("description", item.get("description").getAsString());
                    }
                    familiasArray.add(familia);
                    
                    NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Producto ID: " + productosId + " agregado como familia");
                }
                
                if (item.has("saldo") && !item.get("saldo").isJsonNull()) {
                    try {
                        double saldo = item.get("saldo").getAsDouble();
                        if (saldoTotal == 0.0 || saldo > saldoTotal) {
                            saldoTotal = saldo;
                        }
                    } catch (Exception e) {
                        NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Error al obtener saldo: " + e.getMessage());
                    }
                }
                
                if (nombreCliente.isEmpty() && item.has("nombreCliente") && !item.get("nombreCliente").isJsonNull()) {
                    nombreCliente = item.get("nombreCliente").getAsString();
                }
                
                if (documentoIdentificacionCliente.isEmpty() && item.has("documentoIdentificacionCliente") && !item.get("documentoIdentificacionCliente").isJsonNull()) {
                    documentoIdentificacionCliente = item.get("documentoIdentificacionCliente").getAsString();
                }
                
                // Obtener tipoCupo
                if (tipoCupo.isEmpty() && item.has("tipoCupo") && !item.get("tipoCupo").isJsonNull()) {
                    tipoCupo = item.get("tipoCupo").getAsString();
                }
                
                // Obtener placaVehiculo si está disponible
                if (placaVehiculo.isEmpty() && item.has("placa") && !item.get("placa").isJsonNull()) {
                    placaVehiculo = item.get("placa").getAsString();
                }
                
                // Extraer es_comunidades del primer item que lo tenga
                if (!esComunidades && item.has("es_comunidades") && !item.get("es_comunidades").isJsonNull()) {
                    esComunidades = item.get("es_comunidades").getAsBoolean();
                    NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] es_comunidades obtenido: " + esComunidades);
                }
            }
            
            recepcionData.add("familias", familiasArray);
            
            if (saldoTotal > 0) {
                recepcionData.addProperty("saldo", saldoTotal);
            }
            
            if (!documentoIdentificacionCliente.isEmpty()) {
                recepcionData.addProperty("documentoIdentificacionCliente", documentoIdentificacionCliente);
            } else {
                recepcionData.addProperty("documentoIdentificacionCliente", "");
            }
            
            if (!nombreCliente.isEmpty()) {
                recepcionData.addProperty("nombreCliente", nombreCliente);
            } else {
                recepcionData.addProperty("nombreCliente", "");
            }
            
            if (!tipoCupo.isEmpty()) {
                recepcionData.addProperty("tipoCupo", tipoCupo);
            } else {
                recepcionData.addProperty("tipoCupo", "D");
            }
            
            if (!placaVehiculo.isEmpty()) {
                recepcionData.addProperty("placaVehiculo", placaVehiculo);
            }
            
            recepcionData.addProperty("es_comunidades", esComunidades);
            recepcionData.addProperty("clientesPropios", true);
            recepcionData.addProperty("identificadorCupo", 0);
            
            recepcionData.addProperty("empresas_id", Main.credencial.getEmpresas_id());
            
            NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Se procesaron " + familiasArray.size() + " familias/mangueras disponibles");
            
            int cara = identifierRequest.get("cara").getAsInt();
            solicitarDatosSurtidor(cara);
            
            if (jList1.getModel().getSize() == 0) {
                String mensajeError = "NO SE ENCONTRÓ UN PRODUCTO O MANGUERA PARA VENDER CON ESTE DISPOSITIVO";
                
                Runnable handlerCerrar = () -> {
                    cambiarPanelHome();
                    recepcionData = null;
                    jLabel5.setText("");
                    jMonto.setText("");
                    jGalonaje.setText("");
                    jMonto.requestFocus();
                };
                
                mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] ERROR: No se encontraron mangueras para los productos_id recibidos");
                return;
            }
            
            if (!nombreCliente.isEmpty()) {
                if (saldoTotal > 0) {
                    establecerSaldoEnFooter("<html>" + nombreCliente + "<br/>SALDO: $ " + df.format(saldoTotal) + "</html>");
                } else {
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                    jLabel5.setText("<html>" + nombreCliente + "</html>");
                    // Limpiar mensajes de validación
                    jNotificacion.setText("");
                    jNotificacion.setVisible(false);
                }
            } else if (saldoTotal > 0) {
                establecerSaldoEnFooter("<html>SALDO: $ " + df.format(saldoTotal) + "</html>");
            } else {
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                jLabel5.setText("AUTORIZACIÓN CONSULTADA EXITOSAMENTE");
                // Limpiar mensajes de validación
                jNotificacion.setText("");
                jNotificacion.setVisible(false);
            }
            
            CardLayout layoutAditionalDataPanel = (CardLayout) this.jPanel2.getLayout();
            layoutAditionalDataPanel.show(jPanel2, "manual");
            
            if (!placaVehiculo.isEmpty()) {
                jPlaca.setText(placaVehiculo.toUpperCase());
                NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Placa mostrada: " + placaVehiculo);
            }
            
            jPlaca.setEditable(false);
            jPlaca.setEnabled(false);
            jKilometraje.setEditable(true);
            jKilometraje.setEnabled(true);
            jMonto.setEditable(true);
            jMonto.setEnabled(true);
            jGalonaje.setEditable(true);
            jGalonaje.setEnabled(true);
            
            jLabel4.setEnabled(true);
            
            NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Consulta exitosa para dispositivo: " + serialDispositivo);
            NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Mangueras disponibles: " + jList1.getModel().getSize());
            NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Panel cambiado a 'manual' para selección de manguera y entrada de datos");
            
        } else {
            jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
            jLabel5.setText("NO SE ENCONTRÓ INFORMACIÓN PARA EL DISPOSITIVO");
            mostrarPanelMensaje("NO SE ENCONTRÓ INFORMACIÓN PARA EL DISPOSITIVO", "/com/firefuel/resources/btBad.png", false);
        }
    }

  private void jLabelConsultarMouseEntered(java.awt.event.MouseEvent evt) {
      jLabelConsultar.setForeground(new Color(255, 255, 150));
  }//GEN-LAST:event_jLabelConsultarMouseEntered

  private void jLabelConsultarMouseExited(java.awt.event.MouseEvent evt) {
      jLabelConsultar.setForeground(new Color(255, 255, 255));
  }//GEN-LAST:event_jLabelConsultarMouseExited

    private void jPlacaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPlacaFocusGained
      jPlaca.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
      deshabilitarTeclas(true);
      NovusUtils.deshabilitarCopiarPegar(jPlaca);
  }//GEN-LAST:event_jPlacaFocusGained

  private void jPlacaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPlacaFocusLost
      jPlaca.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
      deshabilitarTeclas(false);
  }//GEN-LAST:event_jPlacaFocusLost

  private void jPlacaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPlacaMouseReleased
      if (!jPlaca.isEditable()) {
          jKilometraje.requestFocus();
          deshabilitarTeclas(true);
      }
  }//GEN-LAST:event_jPlacaMouseReleased
  
  //Logica para validar si la cara esta en uso y deshabilitar el boton de SICOM
  private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
    if(evt.getValueIsAdjusting()){
      return;
    }
    if (jList1.getSelectedIndex() >= 0) {
        boolean Usado = false;
        SetupDao sdao = new SetupDao();
        String cara = jList1.getSelectedValue();
        NovusUtils.printLn("[VentaPredefinirPlaca] cara: " + cara);

        //Extrar el numero despues de la letra C
        Pattern pattern = Pattern.compile("C(\\d+)");
        Matcher matcher = pattern.matcher(cara);
        int numeroCara = 0;
        if(matcher.find()){
            numeroCara = Integer.parseInt(matcher.group(1));
            NovusUtils.printLn("[VentaPredefinirPlaca] numeroCara: " + numeroCara);
        }
        
        ArrayList<Integer> carasUsadas = sdao.getCarasUsadas(numeroCara);
        NovusUtils.printLn("[VentaPredefinirPlaca] CarasUsadas: " + carasUsadas);   

        // Verificar si la lista tiene elementos antes de acceder
        if(!carasUsadas.isEmpty() && carasUsadas.get(0) == numeroCara){
            jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
            jLabel5.setText("La cara " + carasUsadas.get(0) + " Tiene una Pre-Autorozación Activa");
            Usado = true;
            // Detener timer previo si existe
            if (mensajeTimer != null && mensajeTimer.isRunning()) {
                mensajeTimer.stop();
            }
            // Crear timer para limpiar mensaje después de 4 segundos
            mensajeTimer = new Timer(4000, e -> {
                jLabel5.setText("");
                ((Timer) e.getSource()).stop();
            });
            mensajeTimer.setRepeats(false);
            mensajeTimer.start();
        }
        else{
            Usado = false;
            // Detener timer si está corriendo
            if (mensajeTimer != null && mensajeTimer.isRunning()) {
                mensajeTimer.stop();
            }
            // Limpiar mensaje inmediatamente cuando pase al else
            jLabel5.setText("");
        }

            

        String[] lista = cara.split(":");
        productoSeleccionado = lista[1].trim().toUpperCase();
        
        NovusUtils.printLn("[VentaPredefinirPlaca] ProductoFamiliaSeleccionada: " + productoSeleccionado);
        NovusUtils.printLn("[VentaPredefinirPlaca] ¿Es GLP?: " + "GLP".equals(productoSeleccionado));
        NovusUtils.printLn("[VentaPredefinirPlaca] HAY_INTERNET: " + NovusConstante.HAY_INTERNET);
        NovusUtils.printLn("[VentaPredefinirPlaca] Cara con autorización: " + (!carasUsadas.isEmpty() ? carasUsadas.get(0) : "ninguna") + " - " + Usado);


        // //Habilitar o deshabilitar el boton de SICOM
        // if("GLP".equals(productoSeleccionado) && HABILITAR_CONSULTA_SICOM && !Usado){
        //     jLabel11.setEnabled(true);
        //     NovusUtils.printLn("✅ HABILITANDO botón - SICOM");
        // } else {
        //     jLabel11.setEnabled(false);
        //     NovusUtils.printLn("❌ DESHABILITANDO botón - SICOM");
        // }
        
        // Lógica de GLP
        if("GLP".equals(productoSeleccionado)) {
            System.out.println("ENTRANDO EN VALIDACIÓN GLP");
            if(NovusConstante.HAY_INTERNET) {
                jLabel4.setEnabled(false);
                NovusUtils.printLn("✅ DESACTIVANDO botón - GLP con internet");
            } else {
                jLabel4.setEnabled(true);
                NovusUtils.printLn("✅ ACTIVANDO botón - GLP sin internet");
            }
        } else if(Usado){
            jLabel4.setEnabled(false);
            NovusUtils.printLn("❌ DESHABILITANDO botón - Guardar - GLP");
        } else {
            jLabel4.setEnabled(true);
            NovusUtils.printLn("✅ ACTIVANDO botón - No es GLP");
        }
        
        // Manejo de foco
        if (jPlaca.getText().isEmpty()) {
            jPlaca.requestFocus();
        } else {
            jKilometraje.requestFocus();
            deshabilitarTeclas(false);
        }
    }
  }//GEN-LAST:event_jList1ValueChanged

    private void jList1VetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
    }

  private void jList1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseReleased
      if (jList1.getSelectedIndex() >= 0) {
          if (jPlaca.getText().isEmpty()) {
              jPlaca.requestFocus();
          } else {
              jKilometraje.requestFocus();
              deshabilitarTeclas(false);
          }
      }
  }//GEN-LAST:event_jList1MouseReleased

  private void jserialtestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jserialtestActionPerformed
      // TODO add your handling code here:
      testing();
  }//GEN-LAST:event_jserialtestActionPerformed

  private void jPlacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPlacaKeyTyped
      String caracteresAceptados = "[0-9a-zA-Z]";
      // Ocultar saldo del footer temporalmente si hay validación de caracteres
      ocultarSaldoPorValidacion();
      NovusUtils.limitarCarateres(evt, jPlaca, 10, jNotificacion, caracteresAceptados);
      // Restaurar saldo después de 3 segundos (tiempo que dura la notificación)
      Utils.setTimeout("", () -> {
          restaurarSaldoDespuesValidacion();
      }, 3000);
  }//GEN-LAST:event_jPlacaKeyTyped

  private void jKilometrajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jKilometrajeKeyTyped
      String caracteresAceptados = "[0-9]";
      // Ocultar saldo del footer temporalmente si hay validación de caracteres
      ocultarSaldoPorValidacion();
      NovusUtils.limitarCarateres(evt, jKilometraje, 8, jNotificacion, caracteresAceptados);
      // Restaurar saldo después de 3 segundos (tiempo que dura la notificación)
      Utils.setTimeout("", () -> {
          restaurarSaldoDespuesValidacion();
      }, 3000);
  }//GEN-LAST:event_jKilometrajeKeyTyped

  private void jGalonajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jGalonajeFocusGained
      jGalonaje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
      NovusUtils.deshabilitarCopiarPegar(jGalonaje);
      // Si monto tiene contenido, limpiarlo (campos mutuamente excluyentes)
      if (!jMonto.getText().trim().isEmpty()) {
          jMonto.setText("");
      }
      // Limpiar mensajes de validación del footer cuando el usuario empieza a escribir
      // Solo si el mensaje es de validación (no saldo)
      String textoActual = jLabel5.getText();
      if (textoActual != null && !textoActual.trim().isEmpty() && !textoActual.contains("SALDO")) {
          jLabel5.setText("");
          jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Restaurar tamaño original
      }
  }//GEN-LAST:event_jGalonajeFocusGained

  private void jGalonajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jGalonajeFocusLost
      jGalonaje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
      
      // Validación: galonaje no puede comenzar con 0
      String galonaje = jGalonaje.getText().trim();
      if (!galonaje.isEmpty()) {
          // Verificar que no comience con 0 (excepto si es solo "0")
          if (galonaje.length() > 1 && galonaje.startsWith("0")) {
              jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
              jLabel5.setText("GALONAJE INVÁLIDO");
              jGalonaje.setText("");
              jGalonaje.requestFocus();
              // Limpiar mensajes de validación
              jNotificacion.setText("");
              jNotificacion.setVisible(false);
              // Limpiar mensaje después de 3 segundos
              Utils.setTimeout("", () -> {
                  String textoActual = jLabel5.getText();
                  if (textoActual != null && textoActual.equals("GALONAJE INVÁLIDO")) {
                      jLabel5.setText("");
                      jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  }
              }, 3000);
              return;
          }
          
          // Validación: galonaje debe ser mayor a 0
          try {
              double valorGalonaje = Double.parseDouble(galonaje);
              if (valorGalonaje <= 0) {
                  jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  jLabel5.setText("GALONAJE INVÁLIDO");
                  jGalonaje.setText("");
                  jGalonaje.requestFocus();
                  // Limpiar mensajes de validación
                  jNotificacion.setText("");
                  jNotificacion.setVisible(false);
                  // Limpiar mensaje después de 3 segundos
                  Utils.setTimeout("", () -> {
                      String textoActual = jLabel5.getText();
                      if (textoActual != null && textoActual.equals("GALONAJE INVÁLIDO")) {
                          jLabel5.setText("");
                          jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Restaurar tamaño original
                      }
                  }, 3000);
                  return;
              }
          } catch (NumberFormatException e) {
              // Si no es un número válido, no hacer nada aquí (ya se validará al guardar)
          }
      }
  }//GEN-LAST:event_jGalonajeFocusLost

  private void jGalonajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jGalonajeKeyTyped
      // Solo números positivos, máximo 3 caracteres
      String caracteresAceptados = "[0-9]";
      // Si monto tiene contenido, limpiarlo antes de permitir escribir en galonaje
      if (!jMonto.getText().trim().isEmpty()) {
          jMonto.setText("");
      }
      // Ocultar saldo del footer temporalmente si hay validación de caracteres
      ocultarSaldoPorValidacion();
      NovusUtils.limitarCarateres(evt, jGalonaje, 3, jNotificacion, caracteresAceptados);
      // Restaurar saldo después de 3 segundos (tiempo que dura la notificación)
      Utils.setTimeout("", () -> {
          restaurarSaldoDespuesValidacion();
      }, 3000);
  }//GEN-LAST:event_jGalonajeKeyTyped

  private void jMontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMontoFocusGained
      jMonto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
      NovusUtils.deshabilitarCopiarPegar(jMonto);
      // Si galonaje tiene contenido, limpiarlo (campos mutuamente excluyentes)
      if (!jGalonaje.getText().trim().isEmpty()) {
          jGalonaje.setText("");
      }
      // Limpiar mensajes de validación del footer cuando el usuario empieza a escribir
      // Solo si el mensaje es de validación (no saldo)
      String textoActual = jLabel5.getText();
      if (textoActual != null && !textoActual.trim().isEmpty() && !textoActual.contains("SALDO")) {
          jLabel5.setText("");
          jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Restaurar tamaño original
      }
  }//GEN-LAST:event_jMontoFocusGained

  private void jMontoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMontoFocusLost
      jMonto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
      
      // Validación: monto no puede comenzar con 0
      String monto = jMonto.getText().trim();
      if (!monto.isEmpty()) {
          // Verificar que no comience con 0 (excepto si es solo "0")
          if (monto.length() > 1 && monto.startsWith("0")) {
              jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
              jLabel5.setText("MONTO INVÁLIDO");
              jMonto.setText("");
              jMonto.requestFocus();
              // Limpiar mensajes de validación
              jNotificacion.setText("");
              jNotificacion.setVisible(false);
              // Limpiar mensaje después de 3 segundos
              Utils.setTimeout("", () -> {
                  String textoActual = jLabel5.getText();
                  if (textoActual != null && textoActual.equals("MONTO INVÁLIDO")) {
                      jLabel5.setText("");
                      jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  }
              }, 3000);
              return;
          }
          
          // Validación: monto debe ser mayor a 0
          try {
              double valorMonto = Double.parseDouble(monto);
              if (valorMonto <= 0) {
                  jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                  jLabel5.setText("MONTO INVÁLIDO");
                  jMonto.setText("");
                  jMonto.requestFocus();
                  // Limpiar mensajes de validación
                  jNotificacion.setText("");
                  jNotificacion.setVisible(false);
                  // Limpiar mensaje después de 3 segundos
                  Utils.setTimeout("", () -> {
                      String textoActual = jLabel5.getText();
                      if (textoActual != null && textoActual.equals("MONTO INVÁLIDO")) {
                          jLabel5.setText("");
                          jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Restaurar tamaño original
                      }
                  }, 3000);
                  return;
              }
          } catch (NumberFormatException e) {
              // Si no es un número válido, no hacer nada aquí (ya se validará al guardar)
          }
      }
  }//GEN-LAST:event_jMontoFocusLost

  private void jMontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jMontoKeyTyped
      // Solo números positivos, máximo 10 caracteres
      String caracteresAceptados = "[0-9]";
      // Si galonaje tiene contenido, limpiarlo antes de permitir escribir en monto
      if (!jGalonaje.getText().trim().isEmpty()) {
          jGalonaje.setText("");
      }
      // Ocultar saldo del footer temporalmente si hay validación de caracteres
      ocultarSaldoPorValidacion();
      NovusUtils.limitarCarateres(evt, jMonto, 10, jNotificacion, caracteresAceptados);
      // Restaurar saldo después de 3 segundos (tiempo que dura la notificación)
      Utils.setTimeout("", () -> {
          restaurarSaldoDespuesValidacion();
      }, 3000);
  }//GEN-LAST:event_jMontoKeyTyped

    private void jLabel14MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseReleased
        liberarAutorizacion();
        cancelar();
    }//GEN-LAST:event_jLabel14MouseReleased

    /**
     * Oculta temporalmente el saldo del footer cuando aparece una validación de caracteres
     */
    private void ocultarSaldoPorValidacion() {
        // Si el footer tiene contenido y no está ya oculto por otra validación, guardarlo
        String textoActual = jLabel5.getText();
        if (textoActual != null && !textoActual.trim().isEmpty() && !saldoOcultoPorValidacion) {
            // Verificar si el texto contiene "SALDO" (indica que hay saldo visible)
            if (textoActual.contains("SALDO")) {
                textoFooterAnterior = textoActual;
                saldoOcultoPorValidacion = true;
                // Limpiar el footer para que solo se vea la notificación de validación
                jLabel5.setText("");
                // Mantener tamaño uniforme del footer
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                NovusUtils.printLn("[VentaPredefinirPlaca] Saldo ocultado por validación de caracteres");
            } else {
                // Si hay un mensaje de validación (no saldo), limpiarlo completamente
                // Esto evita que los mensajes de validación se queden cuando aparece la validación de caracteres
                jLabel5.setText("");
                // Mantener tamaño uniforme del footer
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                NovusUtils.printLn("[VentaPredefinirPlaca] Mensaje de validación del footer limpiado por validación de caracteres");
            }
        }
    }

    /**
     * Restaura el saldo en el footer después de que desaparece la notificación de validación
     */
    private void restaurarSaldoDespuesValidacion() {
        // Si había saldo oculto, restaurarlo
        if (saldoOcultoPorValidacion && textoFooterAnterior != null && !textoFooterAnterior.trim().isEmpty()) {
            jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
            jLabel5.setText(textoFooterAnterior);
            // Restaurar tamaño de fuente original
            // Limpiar mensajes de validación cuando se restaura el saldo
            jNotificacion.setText("");
            jNotificacion.setVisible(false);
            textoFooterAnterior = "";
            saldoOcultoPorValidacion = false;
            NovusUtils.printLn("[VentaPredefinirPlaca] Saldo restaurado después de validación de caracteres - mensajes de validación limpiados");
        }
    }

    /**
     * Establece el saldo en el footer y limpia cualquier mensaje de validación visible
     */
    private void establecerSaldoEnFooter(String textoFooter) {
        jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
        jLabel5.setText(textoFooter);
        // Restaurar tamaño de fuente original
        jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
        // Limpiar mensajes de validación cuando se establece el saldo
        jNotificacion.setText("");
        jNotificacion.setVisible(false);
        NovusUtils.printLn("[VentaPredefinirPlaca] Saldo establecido en footer - mensajes de validación limpiados");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField jGalonaje;
    private javax.swing.JTextField jKilometraje;
    private javax.swing.JTextField jMonto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabelConsultar;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabelGalonaje;
    private javax.swing.JLabel jLabelMonto;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jPlaca;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jcara;
    private javax.swing.JTextField jserialtest;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    // End of variables declaration//GEN-END:variables
  private void cancelar() {
        if (timer != null) {
            timer.stop();
        }
        instance = null;
        InfoViewController.NotificadorClientePropio = null;
        VentaPredefinirPlaca.ventaPredefinirPlaca = null;
        InfoViewController.NotificadorRecuperacion = null;
        this.dispose();
    }

    private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private void deshabilitarTeclas(boolean activar) {
        TecladoExtendido tec = (TecladoExtendido) jPanel1;
        tec.habilitarAlfanumeric(activar);
        tec.habilitarPunto(activar);
    }

    private void validacionSicom() {
        NovusUtils.beep();
        if (jLabel11.isEnabled() && !jPlaca.getText().trim().equals("")) {
            mostrarPanelMensaje("SOLICITANDO AUTORIZACION", "/com/firefuel/resources/loader_fac.gif", false);
            this.taskRunner(() -> {
                try{
                    JsonObject response = ClienteFacade.consultaVehiculoSicom(jPlaca.getText());
                    NovusUtils.printLn("[validacionSicom] response: " + response);
                    cambiarPanelHome();
                    //Verifacion si la respuesta es por timeout o error de conexion
                    if (response == null){
                        NovusUtils.printLn("[SICOM] Timeout o error de conexion");
                        mostrarPanelMensaje("ERROR: TIMEOUT O ERROR DE CONEXION", "/com/firefuel/resources/btBad.png", false);
                        return;
                    }
                    //Verfiicar si tiene el campo httpStatus
                    if(!response.has("httpStatus")){
                        NovusUtils.printLn("[SICOM] No tiene el campo httpStatus");
                        mostrarPanelMensaje("ERROR: No tiene el campo httpStatus", "/com/firefuel/resources/btBad.png", false);
                        return;
                    }
                    int stautusCode = response.get("httpStatus").getAsInt();
                    switch (stautusCode) {
                        case 200:
                        NovusUtils.printLn("[SICOM] Codigo de respuesta 200 - Vehiculo encontrado");
                        crearAutorizacionVentaGLP(response);
                        break;
                        case 404:
                        NovusUtils.printLn("[SICOM] Codigo de respuesta 404 - Vehiculo no encontrado");
                        mostrarPanelMensaje("VEHICULO NO ENCONTRADO EN SICOM GLP", "/com/firefuel/resources/btBad.png", false);
                        break;
                        case 500:
                        NovusUtils.printLn("[SICOM] Codigo de respuesta 500 - Error interno de SICOM");
                        mostrarPanelMensaje("PLACA NO HABILITADA POR SICOM", "/com/firefuel/resources/btBad.png", false);
                        break;
                        default:
                        NovusUtils.printLn("[SICOM] Codigo de respuesta desconocido: " + stautusCode);
                        mostrarPanelMensaje("ERROR AL CONSULTAR VEHICULO EN SICOM GLP - CODIGO: " + stautusCode, "/com/firefuel/resources/btBad.png", false);
                        break;
                    }
                }catch(Exception e){
                    NovusUtils.printLn("❌ [SICOM] Excepción: " + e.getMessage());
                    cambiarPanelHome();
                    mostrarPanelMensaje("ERROR INESPERADO EN CONSULTA SICOM", "/com/firefuel/resources/btBad.png", false);
                }
                
            });
        }
    }

    void crearAutorizacionVentaGLP(JsonObject response) {
        if (response != null) {
            if (response.get("error") == null) {
                if (!response.get("marca").isJsonNull()) {
                    String marcar = response.get("marca").getAsString();
                    String capacidad = "CAPACIDAD MAX: " + response.get("capacidad").getAsString() + "LT";
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                    jLabel5.setText("<html>" + marcar + "<br/>" + capacidad + "</html>");
                    jLabel11.setEnabled(false);
                    jLabel4.setEnabled(true);
                    jPlaca.setEditable(false);
                    jPlaca.setEnabled(false);
                } else {
                    mostrarPanelMensaje(" Vehículo no se encuentra habilitado en SICOM - Venta no Autorizada ...","/com/firefuel/resources/btBad.png", false);
                }
            } else {
                mostrarPanelMensaje("Vehículo no se encuentra habilitado en SICOM - Venta no Autorizada ... ", "/com/firefuel/resources/btBad.png", false);
            }
        } else {
            mostrarPanelMensaje("Vehículo no se encuentra habilitado en SICOM - Venta no Autorizada ... ", "/com/firefuel/resources/btBad.png", false);
        }
    }

    //BTZ736   
    private void crearAutorizacion() {
        NovusUtils.printLn("[VentaPredefinirPlaca] Iniciando creación de autorización");
        
        // Inicializar recepcionData si es null
        if (recepcionData == null) {
            recepcionData = new JsonObject();
        }
        

        if (!recepcionData.has("saldo")) {
            recepcionData.addProperty("saldo", 0.0);
            recepcionData.addProperty("tipoCupo", "L");
        }
        
        // Obtener saldo de forma segura (puede ser String o número)
        double saldoActual = 0.0;
        try {
            if (recepcionData.get("saldo").isJsonPrimitive() && recepcionData.get("saldo").getAsJsonPrimitive().isString()) {
                saldoActual = Double.parseDouble(recepcionData.get("saldo").getAsString());
            } else {
                saldoActual = recepcionData.get("saldo").getAsDouble();
            }
        } catch (Exception e) {
            NovusUtils.printLn("[VentaPredefinirPlaca] Error al obtener saldo: " + e.getMessage());
            saldoActual = 0.0;
        }
        
        String tipoCupo = recepcionData.has("tipoCupo") ? recepcionData.get("tipoCupo").getAsString() : "L";
        
        if (saldoActual > 0 || tipoCupo.equals("L") || hayClienteComunidades(recepcionData)) {
            String placa = jPlaca.getText().trim();
            String odometro = jKilometraje.getText().trim();
            if (!placa.isEmpty() && !odometro.isEmpty()) {
                jLabel5.setText("");
                SurtidorDao dao = new SurtidorDao();

                if (jList1.getSelectedIndex() == -1) {
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 16));
                    jLabel5.setText("SELECCIONE EL \r\nPRODUCTO A VENDER");
                    return; // Salir si no hay selección
                }

                // Validar que getSelectedValue() no sea null
                String selectedValue = jList1.getSelectedValue();
                if (selectedValue == null || selectedValue.isEmpty()) {
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 16));
                    jLabel5.setText("SELECCIONE EL \r\nPRODUCTO A VENDER");
                    return; // Salir si no hay valor seleccionado
                }
                
                String keyFind = selectedValue.split(":")[0];

                Surtidor surtidor = null;
                for (Map.Entry<Integer, Surtidor> entry : mangueras.entrySet()) {
                    Surtidor val = entry.getValue();
                    String value = "C" + val.getCara() + "M" + val.getManguera();
                    if (keyFind.equals(value)) {
                        surtidor = val;
                        productoManguera = surtidor.getProductoDescripcion();
                        break;
                        
                    }
                }

                if (surtidor == null) {
                    jLabel5.setText("SELECCIONE UNA \r\nMANGUERA");
                } else {
                    try {
                        jLabel5.setText("");

                        // Obtener valores de monto y galonaje si fueron digitados
                        String montoManual = jMonto.getText().trim();
                        String cantidadManual = jGalonaje.getText().trim();
                        
                        // Validación para flujo iButton: SI O SI se debe diligenciar monto o galonaje
                        if (!esFlujoPlaca) {
                            if (montoManual.isEmpty() && cantidadManual.isEmpty()) {
                                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                jLabel5.setText("INGRESE MONTO O GALONAJE");
                                return;
                            }
                            
                            if (!montoManual.isEmpty() && !cantidadManual.isEmpty()) {
                                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                jLabel5.setText("SOLO PUEDE INGRESAR MONTO O GALONAJE, NO AMBOS");
                                return;
                            }
                        }
                        
                        // Validación: monto no puede comenzar con 0
                        if (!montoManual.isEmpty()) {
                            // Verificar que no comience con 0 (excepto si es solo "0")
                            if (montoManual.length() > 1 && montoManual.startsWith("0")) {
                                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                jLabel5.setText("MONTO INVÁLIDO");
                                jMonto.setText("");
                                jMonto.requestFocus();
                                // Limpiar mensajes de validación
                                jNotificacion.setText("");
                                jNotificacion.setVisible(false);
                                // Limpiar mensaje después de 3 segundos
                                Utils.setTimeout("", () -> {
                                    String textoActual = jLabel5.getText();
                                    if (textoActual != null && textoActual.equals("MONTO INVÁLIDO")) {
                                        jLabel5.setText("");
                                        jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                    }
                                }, 3000);
                                return;
                            }
                            
                            // Validación: monto debe ser mayor a 0
                            try {
                                double valorMonto = Double.parseDouble(montoManual);
                                if (valorMonto <= 0) {
                                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                    jLabel5.setText("MONTO INVÁLIDO");
                                    jMonto.setText("");
                                    jMonto.requestFocus();
                                    // Limpiar mensajes de validación
                                    jNotificacion.setText("");
                                    jNotificacion.setVisible(false);
                                    // Limpiar mensaje después de 3 segundos
                                    Utils.setTimeout("", () -> {
                                        String textoActual = jLabel5.getText();
                                        if (textoActual != null && textoActual.equals("MONTO INVÁLIDO")) {
                                            jLabel5.setText("");
                                            jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Mantener tamaño uniforme del footer
                                        }
                                    }, 3000);
                                    return;
                                }
                            } catch (NumberFormatException e) {
                                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                jLabel5.setText("ERROR: MONTO INVÁLIDO");
                                jMonto.setText("");
                                jMonto.requestFocus();
                                return;
                            }
                        }
                        
                        // Validación: galonaje no puede comenzar con 0
                        if (!cantidadManual.isEmpty()) {
                            // Verificar que no comience con 0 (excepto si es solo "0")
                            if (cantidadManual.length() > 1 && cantidadManual.startsWith("0")) {
                                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                jLabel5.setText("GALONAJE INVÁLIDO");
                                jGalonaje.setText("");
                                jGalonaje.requestFocus();
                                // Limpiar mensajes de validación
                                jNotificacion.setText("");
                                jNotificacion.setVisible(false);
                                // Limpiar mensaje después de 3 segundos
                                Utils.setTimeout("", () -> {
                                    String textoActual = jLabel5.getText();
                                    if (textoActual != null && textoActual.equals("GALONAJE INVÁLIDOO")) {
                                        jLabel5.setText("");
                                        jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                    }
                                }, 3000);
                                return;
                            }
                            
                            // Validación: galonaje debe ser mayor a 0
                            try {
                                double valorGalonaje = Double.parseDouble(cantidadManual);
                                if (valorGalonaje <= 0) {
                                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                    jLabel5.setText("GALONAJE INVÁLIDO");
                                    jGalonaje.setText("");
                                    jGalonaje.requestFocus();
                                    // Limpiar mensajes de validación
                                    jNotificacion.setText("");
                                    jNotificacion.setVisible(false);
                                    // Limpiar mensaje después de 3 segundos
                                    Utils.setTimeout("", () -> {
                                        String textoActual = jLabel5.getText();
                                        if (textoActual != null && textoActual.equals("GALONAJE INVÁLIDO")) {
                                            jLabel5.setText("");
                                            jLabel5.setFont(new java.awt.Font("Arial", 0, 22)); // Mantener tamaño uniforme del footer
                                        }
                                    }, 3000);
                                    return;
                                }
                            } catch (NumberFormatException e) {
                                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                jLabel5.setText("ERROR: GALONAJE INVÁLIDO");
                                jGalonaje.setText("");
                                jGalonaje.requestFocus();
                                return;
                            }
                        }
                        
                        // Validación específica para flujo iButton: monto no puede exceder saldo
                        if (!esFlujoPlaca && recepcionData != null && recepcionData.has("saldo") && !montoManual.isEmpty()) {
                            try {
                                int monto = (int) Double.parseDouble(montoManual.trim());
                                double saldo;
                                
                                // Manejar saldo como String o número
                                if (recepcionData.get("saldo").isJsonPrimitive() && recepcionData.get("saldo").getAsJsonPrimitive().isString()) {
                                    saldo = Double.parseDouble(recepcionData.get("saldo").getAsString());
                                } else {
                                    saldo = recepcionData.get("saldo").getAsDouble();
                                }
                                
                                if (monto > saldo) {
                                    // Monto excede el saldo disponible - NO permitir autorización
                                    // Mostrar mensaje de error con el saldo disponible
                                    String nombreCliente = recepcionData.has("nombreCliente") && !recepcionData.get("nombreCliente").isJsonNull() 
                                        ? recepcionData.get("nombreCliente").getAsString() : "";
                                    if (!nombreCliente.isEmpty()) {
                                        establecerSaldoEnFooter("<html>" + nombreCliente + "<br/>SALDO: $ " + df.format(saldo) + "</html>");
                                    } else {
                                        jLabel5.setText("MONTO INGRESADO EXCEDE EL SALDO DISPONIBLE");
                                        // Limpiar mensajes de validación
                                        jNotificacion.setText("");
                                        jNotificacion.setVisible(false);
                                    }
                                    // Crear Runnable personalizado para limpiar campos y permitir ingresar nuevamente
                                    Runnable handlerCerrar = () -> {
                                        cambiarPanelHome();
                                        // Limpiar campos de monto y galonaje
                                        jMonto.setText("");
                                        jGalonaje.setText("");
                                        // Poner foco en el campo de monto para ingresar nuevamente
                                        jMonto.requestFocus();
                                    };
                                    mostrarPanelMensaje("MONTO INGRESADO EXCEDE EL SALDO DISPONIBLE", "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                                    return; // No crear la autorización
                                }
                            } catch (NumberFormatException e) {
                                // Error al convertir monto o saldo, continuar con validación normal
                                NovusUtils.printLn("[VentaPredefinirPlaca] Error al validar monto en flujo iButton: " + montoManual + " - " + e.getMessage());
                            } catch (Exception e) {
                                // Cualquier otro error, continuar con validación normal
                                NovusUtils.printLn("[VentaPredefinirPlaca] Error al validar monto en flujo iButton: " + e.getMessage());
                            }
                        }
                        
                        // Validación específica para flujo iButton: galonaje no puede generar monto que exceda saldo
                        if (!esFlujoPlaca && recepcionData != null && recepcionData.has("saldo") && surtidor != null && !cantidadManual.isEmpty()) {
                            try {
                                int cantidad = (int) Double.parseDouble(cantidadManual.trim());
                                double saldo;
                                
                                // Manejar saldo como String o número
                                if (recepcionData.get("saldo").isJsonPrimitive() && recepcionData.get("saldo").getAsJsonPrimitive().isString()) {
                                    saldo = Double.parseDouble(recepcionData.get("saldo").getAsString());
                                } else {
                                    saldo = recepcionData.get("saldo").getAsDouble();
                                }
                                
                                int precioProducto = (int) surtidor.getProductoPrecio();
                                
                                if (precioProducto > 0) {
                                    int montoEquivalente = cantidad * precioProducto;
                                    
                                    if (montoEquivalente > saldo) {
                                        // El monto equivalente excede el saldo disponible - NO permitir autorización
                                        // Mostrar mensaje de error con el saldo disponible
                                        String nombreCliente = recepcionData.has("nombreCliente") && !recepcionData.get("nombreCliente").isJsonNull() 
                                            ? recepcionData.get("nombreCliente").getAsString() : "";
                                        if (!nombreCliente.isEmpty()) {
                                            jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                            jLabel5.setText("<html>" + nombreCliente + "<br/>SALDO: $ " + df.format(saldo) + "</html>");
                                        } else {
                                            jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                                            jLabel5.setText("MONTO INGRESADO EXCEDE EL SALDO DISPONIBLE");
                                        }
                                        // Crear Runnable personalizado para limpiar campos y permitir ingresar nuevamente
                                        Runnable handlerCerrar = () -> {
                                            cambiarPanelHome();
                                            // Limpiar campos de monto y galonaje
                                            jMonto.setText("");
                                            jGalonaje.setText("");
                                            // Poner foco en el campo de monto para ingresar nuevamente
                                            jMonto.requestFocus();
                                        };
                                        mostrarPanelMensaje("MONTO INGRESADO EXCEDE EL SALDO DISPONIBLE", "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                                        return; // No crear la autorización
                                    }
                                }
                            } catch (NumberFormatException e) {
                                // Error al convertir cantidad o saldo, continuar con validación normal
                                NovusUtils.printLn("[VentaPredefinirPlaca] Error al validar cantidad en flujo iButton: " + cantidadManual + " - " + e.getMessage());
                            } catch (Exception e) {
                                // Cualquier otro error, continuar con validación normal
                                NovusUtils.printLn("[VentaPredefinirPlaca] Error al validar cantidad en flujo iButton: " + e.getMessage());
                            }
                        }
                        
                        // CONVERSIÓN DE MONTO A GALONES: Si se ingresó monto, convertirlo a cantidad (galones)
                        String montoParam = null;
                        String cantidadParam = null;
                        
                        if (!montoManual.isEmpty() && surtidor != null) {
                            try {
                                // Convertir monto a galones: cantidad = monto / precio_producto
                                double monto = Double.parseDouble(montoManual.trim());
                                double precioProducto = surtidor.getProductoPrecio();
                                
                                if (precioProducto > 0) {
                                    // Validar que el monto no exceda el saldo disponible (si está disponible)
                                    if (recepcionData != null && recepcionData.has("saldo")) {
                                        // 🔍 DEBUG: Log del contenido completo de recepcionData antes de leer el saldo
                                        NovusUtils.printLn("🔍 [VALIDACIÓN SALDO] recepcionData completo: " + recepcionData.toString());
                                        NovusUtils.printLn("🔍 [VALIDACIÓN SALDO] Tipo de dato del campo 'saldo': " + recepcionData.get("saldo").getClass().getName());
                                        NovusUtils.printLn("🔍 [VALIDACIÓN SALDO] Valor crudo del campo 'saldo': " + recepcionData.get("saldo"));
                                        
                                        try {
                                            double saldo;
                                            if (recepcionData.get("saldo").isJsonPrimitive() && recepcionData.get("saldo").getAsJsonPrimitive().isString()) {
                                                saldo = Double.parseDouble(recepcionData.get("saldo").getAsString());
                                                NovusUtils.printLn("🔍 [VALIDACIÓN SALDO] Saldo leído como String: " + saldo);
                                            } else {
                                                saldo = recepcionData.get("saldo").getAsDouble();
                                                NovusUtils.printLn("🔍 [VALIDACIÓN SALDO] Saldo leído como Double: " + saldo);
                                            }
                                            
                                            // 🔍 DEBUG: Log detallado para identificar el bug
                                            NovusUtils.printLn("🔍 [VALIDACIÓN SALDO] Monto ingresado: " + monto + ", Saldo disponible: " + saldo);
                                            
                                            // ✅ CORRECCIÓN: Comparación directa como double (sin cast a int) para mayor precisión
                                            // El problema era que (int) monto > saldo podía causar problemas de precisión
                                            if (monto > saldo) {
                                                // Monto excede el saldo disponible - mostrar error
                                                String mensajeError = "MONTO INGRESADO EXCEDE EL SALDO DISPONIBLE";
                                                NovusUtils.printLn("❌ [VALIDACIÓN SALDO] ERROR: Monto " + monto + " > Saldo " + saldo);
                                                String nombreCliente = recepcionData.has("nombreCliente") && !recepcionData.get("nombreCliente").isJsonNull() 
                                                    ? recepcionData.get("nombreCliente").getAsString() : "";
                                                
                                                if (!nombreCliente.isEmpty()) {
                                                    establecerSaldoEnFooter("<html>" + nombreCliente + "<br/>SALDO: $ " + df.format(saldo) + "</html>");
                                                } else {
                                                    // NO mostrar mensaje de error en el footer, solo en el panel de error
                                                    // jLabel5.setText(mensajeError);
                                                    // Limpiar mensajes de validación
                                                    jNotificacion.setText("");
                                                    jNotificacion.setVisible(false);
                                                }
                                                
                                                Runnable handlerCerrar = () -> {
                                                    cambiarPanelHome();
                                                    jMonto.setText("");
                                                    jGalonaje.setText("");
                                                    jMonto.requestFocus();
                                                };
                                                
                                                mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                                                return; // No crear autorización
                                            } else {
                                                NovusUtils.printLn("✅ [VALIDACIÓN SALDO] Validación exitosa: Monto " + monto + " <= Saldo " + saldo);
                                            }
                                        } catch (Exception e) {
                                            NovusUtils.printLn("[VentaPredefinirPlaca] Error al validar saldo: " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    } else {
                                        // 🔍 DEBUG: Saldo no disponible
                                        NovusUtils.printLn("⚠️ [VALIDACIÓN SALDO] recepcionData es null o no tiene campo 'saldo'");
                                        if (recepcionData != null) {
                                            NovusUtils.printLn("⚠️ [VALIDACIÓN SALDO] Campos disponibles en recepcionData: " + recepcionData.keySet());
                                        }
                                    }
                                    
                                    // Guardar el monto directamente, sin convertir a galonaje
                                    montoParam = montoManual;
                                    cantidadParam = null; // cantidad = 0
                                    
                                    NovusUtils.printLn("[VentaPredefinirPlaca] Guardando MONTO: " + monto + ", CANTIDAD: 0");
                                } else {
                                    // Si el precio es 0, aún así guardar el monto directamente
                                    montoParam = montoManual;
                                    cantidadParam = null;
                                    NovusUtils.printLn("[VentaPredefinirPlaca] Guardando MONTO: " + monto + ", CANTIDAD: 0 (precio producto = 0)");
                                }
                            } catch (NumberFormatException e) {
                                NovusUtils.printLn("[VentaPredefinirPlaca] Error al convertir monto: " + montoManual + " - " + e.getMessage());
                                // NO mostrar mensaje de error en el footer, solo en el panel de error
                                // jLabel5.setText("ERROR AL CONVERTIR MONTO");
                                mostrarPanelMensaje("ERROR AL CONVERTIR MONTO", "/com/firefuel/resources/btBad.png", false);
                                return;
                            }
                        } else if (!cantidadManual.isEmpty()) {
                            // Si se ingresó cantidad directamente, usar esa cantidad
                            cantidadParam = cantidadManual;
                            montoParam = null;
                        } else {
                            // Si ambos están vacíos, pasar null para usar lógica por defecto
                            montoParam = null;
                            cantidadParam = null;
                        }
                        SetupDao setupDao = new SetupDao();
                        if (setupDao.placaTieneTransaccionActiva(placa)) {
                            mostrarPanelMensaje("NO SE PUEDE CREAR LA AUTORIZACIÓN\nLA PLACA " + placa.toUpperCase() + " TIENE UNA VENTA EN CURSO", "/com/firefuel/resources/btBad.png", false);
                            return;
                        }

                        // ✅ REMOVIDO: HABILITAR_CONSULTA_SICOM ya no se usa en flujo de clientes propios
                        dao.crearAutorizacionPorPlaca(Main.persona, surtidor, placa, odometro, recepcionData, 
                                                     false, montoParam, cantidadParam);
                        
                        
                       enVenta_1 = true;
                        mostrarPanelMensajeFinal(mensajeAutorizacion(surtidor.isEstaDentroDelRango(), hayClienteComunidades(recepcionData)) + surtidor.getCara(), "/com/firefuel/resources/btOk.png", true);
//                        timer = new Timer(5000, e -> cancelar());
                    } catch (DAOException ex) {
                        Logger.getLogger(VentaPredefinirPlaca.class.getName()).log(Level.SEVERE, null, ex);
                        
                        // Verificar si el error es por monto que excede saldo
                        if (ex.getMessage() != null && ex.getMessage().contains("MONTO_EXCEDE_SALDO")) {
                            String mensajeError = "MONTO INGRESADO EXCEDE EL SALDO DISPONIBLE";
                            
                            // Intentar extraer información del saldo si está disponible
                            if (recepcionData != null && recepcionData.has("saldo")) {
                                try {
                                    double saldo;
                                    if (recepcionData.get("saldo").isJsonPrimitive() && recepcionData.get("saldo").getAsJsonPrimitive().isString()) {
                                        saldo = Double.parseDouble(recepcionData.get("saldo").getAsString());
                                    } else {
                                        saldo = recepcionData.get("saldo").getAsDouble();
                                    }
                                    
                                    String nombreCliente = recepcionData.has("nombreCliente") && !recepcionData.get("nombreCliente").isJsonNull() 
                                        ? recepcionData.get("nombreCliente").getAsString() : "";
                                    
                                    if (!nombreCliente.isEmpty()) {
                                        establecerSaldoEnFooter("<html>" + nombreCliente + "<br/>SALDO: $ " + df.format(saldo) + "</html>");
                                    } else {
                                        // NO mostrar mensaje de error en el footer, solo en el panel de error
                                        // jLabel5.setText(mensajeError);
                                        // Limpiar mensajes de validación
                                        jNotificacion.setText("");
                                        jNotificacion.setVisible(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                // NO mostrar mensaje de error en el footer, solo en el panel de error
                                // jLabel5.setText(mensajeError);
                            }
                            
                            // Crear handler para limpiar campos y permitir ingresar nuevamente
                            Runnable handlerCerrar = () -> {
                                cambiarPanelHome();
                                jMonto.setText("");
                                jGalonaje.setText("");
                                jMonto.requestFocus();
                            };
                            
                            mostrarPanelMensaje(mensajeError, "/com/firefuel/resources/btBad.png", false, true, handlerCerrar);
                            return; // Salir sin crear autorización
                        }
                    }
//                    timer.start();
                }
            } else {
                if (placa.isEmpty()) {
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                    jLabel5.setText("Verifique la placa");
                } else {
                    jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                    jLabel5.setText("Requiere kilometraje");
                }
            }
        } else {
            mostrarPanelMensaje("VEHICULO NO CUENTA CON CUPO", "/com/firefuel/resources/btBad.png", false);
        }
    }
    
    private String mensajeAutorizacion(boolean estaDentrodelRango, boolean clienteComunidades) {
        if (clienteComunidades) {
            if (estaDentrodelRango) {
                return "Venta autorizada para clientes comunidades, cara ".toUpperCase();
            } else {
                return "Venta autorizada para clientes comunidades <br> Cliente sin descuento asignado, cara ".toUpperCase();
            }
        } else {
            return "Venta autorizada en la cara ";
        }
    }

    private void cambioManual() {
        NovusUtils.beep();
        CardLayout layoutAditionalDataPanel = (CardLayout) this.jPanel2.getLayout();
        if (jLabel12.getText().equals("MANUAL")) {
            layoutAditionalDataPanel.show(jPanel2, "manual");
            jLabel11.setEnabled(true);
            jLabel12.setText("DISPOSITIVO");
            jLabel4.setEnabled(true);
        } else {
            layoutAditionalDataPanel.show(jPanel2, "dispositivo");
            jLabel12.setText("MANUAL");
            jLabel11.setEnabled(false);
            jLabel4.setEnabled(false);
        }
    }

    public void recepcionNotificacionExterna(JsonObject identifierRequest) {
        jLabel7.setText("CARA:" + identifierRequest.get("cara").getAsString() + " " + identifierRequest.get("serial").getAsString());
//        if (HABILITAR_CONSULTA_SICOM) {
//            this.solicitarAutorizacionVentaGLP(identifierRequest);
//        } else {
            this.solicitarAutorizacionVenta(identifierRequest);
//        }
    }

    void solicitarAutorizacionVenta(JsonObject identifierRequest) {
        mostrarPanelMensaje("SOLICITANDO AUTORIZACION", "/com/firefuel/resources/loader_fac.gif", true);
        Runnable validarCliente = () -> {
            try {
                // Construir el request con la nueva estructura (similar a placa pero con tipoIdentificador: IDROOM)
                JsonObject request = new JsonObject();
                
                // Campos requeridos
                request.addProperty("identificadorEstacion", Main.credencial.getEmpresas_id());
                request.addProperty("fechaConsulta", sdf.format(new Date()));
                request.addProperty("identificadorPromotor", Main.persona.getId());
                
                // Para flujo de iButton: identificador es el serial del dispositivo y tipoIdentificador es "IDROOM"
                String serialDispositivo = identifierRequest.get("serial").getAsString();
                request.addProperty("identificador", serialDispositivo);
                request.addProperty("tipoIdentificador", "IDROOM");
                
                // IMPORTANTE: Al momento de leer el iButton, kilometros, monto y galonaje NO están disponibles
                // Por lo tanto, todos deben ir como null
                request.add("kilometros", com.google.gson.JsonNull.INSTANCE);
                request.add("monto", com.google.gson.JsonNull.INSTANCE);
                request.add("galonaje", com.google.gson.JsonNull.INSTANCE);
                
                NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Enviando KILOMETROS: null, MONTO: null, GALONAJE: null");
                
                // Log del request completo
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("[IBUTTON] Request completo enviado:");
                NovusUtils.printLn(Main.ANSI_CYAN + request.toString() + Main.ANSI_RESET);
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                
                // Usar el mismo endpoint que placa
                JsonObject response = ClienteFacade.consultaValidacionVentaPorPlaca(request);
                
                // Procesar respuesta en el hilo de UI
                // Al momento de leer el iButton, kilometros, monto y galonaje son null
                final String kilometros = null;
                final String monto = null;
                final String galonaje = null;
                
                SwingUtilities.invokeLater(() -> {
                    cambiarPanelHome();
                    procesarRespuestaConsultaIButton(response, identifierRequest, kilometros, monto, galonaje);
                });
                
            } catch (Exception e) {
                NovusUtils.printLn("[VentaPredefinirPlaca] [IBUTTON] Error en consulta: " + e.getMessage());
                Logger.getLogger(VentaPredefinirPlaca.class.getName()).log(Level.SEVERE, null, e);
                SwingUtilities.invokeLater(() -> {
                    cambiarPanelHome();
                    mostrarPanelMensaje("ERROR AL CONSULTAR AUTORIZACIÓN", "/com/firefuel/resources/btBad.png", false);
                });
            }
        };
        CompletableFuture.runAsync(validarCliente);
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

    private void autorizacionClientesComunidades(JsonObject response, JsonObject identifierRequest) {
        if (response != null && response.get("data") != null && !response.get("data").isJsonNull()) {
            procesaRespuestaAutorizacion(response.get("data").getAsJsonObject());
            recepcionData = response.get("data").getAsJsonObject();
            recepcionData.addProperty("medioAutorizacion", "ibutton");
            recepcionData.addProperty("serialDispositivo", identifierRequest.get("serial").getAsString());
            solicitarDatosSurtidor(identifierRequest.get("cara").getAsInt());
            jLabel5.setText("<html>" + recepcionData.get("nombreCliente").getAsString() + "</html>");
        } else {
            validarMensajes(response, identifierRequest);
        }
    }

    private void validarMensajes(JsonObject response, JsonObject identifierRequest) {
        PrinterFacade facade = new PrinterFacade();
        if (response != null) {
            if (response.get("mensajeError") != null && !response.get("mensajeError").isJsonNull()) {
                jLabel7.setText("<html><center>" + response.get("mensajeError").getAsString() + "</center></html>");
                facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + response.get("mensajeError").getAsString());
            } else {
                jLabel7.setText("<html><center>REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACIÓN</center></html>");
                facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACIÓN");
            }
        } else {
            facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "REINTENTE COLOCAR EL CHIP, SIN COMUNICACIÓN EN EL SERVER");
            jLabel7.setText("<html><center>REINTENTE COLOCAR EL CHIP, SIN COMUNICACIÓN EN EL SERVER</center></html>");
        }

    }

    void solicitarAutorizacionVentaGLP(JsonObject identifierRequest) {
        mostrarPanelMensaje("SOLICITANDO AUTORIZACION", "/com/firefuel/resources/loader_fac.gif", true);
        Runnable validarCliente = () -> {
            PrinterFacade facade = new PrinterFacade();
            JsonObject request = new JsonObject();
            request.addProperty("data", identifierRequest.get("serial").getAsString());
            request.addProperty("identificadorCara", identifierRequest.get("cara").getAsInt());
            request.addProperty("identificadorDominio", Main.credencial.getEmpresa().getDominioId());
            request.addProperty("identificadorEmpresa", Main.credencial.getEmpresas_id());
            request.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
            request.addProperty("identificadorNegocio", Main.credencial.getEquipos_id());
            request.addProperty("identificadorPromotor", Main.persona.getId());
            request.addProperty("fechaConsulta", sdf.format(new Date()));

            // Log del request completo para flujo iButton GLP
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("[IBUTTON-GLP] Request completo enviado a consultaValidacionVentaGLP:");
            NovusUtils.printLn(Main.ANSI_CYAN + request.toString() + Main.ANSI_RESET);
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");

            JsonObject response = ClienteFacade.consultaValidacionVentaGLP(request);

            cambiarPanelHome();
            if (response != null && response.get("data") != null && !response.get("data").isJsonNull()) {
                procesaRespuestaAutorizacioGLP(response.get("data").getAsJsonObject());
                recepcionData = response.get("data").getAsJsonObject();
                recepcionData.addProperty("medioAutorizacion", "ibutton");
                recepcionData.addProperty("serialDispositivo", identifierRequest.get("serial").getAsString());
                solicitarDatosSurtidor(identifierRequest.get("cara").getAsInt());
                jLabel5.setFont(new java.awt.Font("Arial", 0, 22));
                jLabel5.setText("<html>" + recepcionData.get("cliente").getAsString() + "</html>");
            } else {
                NovusUtils.beep();

                if (response != null) {
                    if (response.get("mensajeError") != null && !response.get("mensajeError").isJsonNull()) {
                        jLabel7.setText("<html><center>" + response.get("mensajeError").getAsString() + "</center></html>");
                        facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + response.get("mensajeError").getAsString());
                    } else {
                        jLabel7.setText("<html><center>REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACIÓN</center></html>");
                        facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "REINTENTE COLOCAR EL CHIP, VERIFIQUE LA COMUNICACIÓN");
                    }
                } else {
                    facade.printFormato("CHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + "REINTENTE COLOCAR EL CHIP, SIN COMUNICACIÓN EN EL SERVER");
                    jLabel7.setText("<html><center>REINTENTE COLOCAR EL CHIP, SIN COMUNICACIÓN EN EL SERVER</center></html>");
                }
            }
        };
        CompletableFuture.runAsync(validarCliente);
    }

    void procesaRespuestaAutorizacion(JsonObject response) {
        jPlaca.setText(response.get("placaVehiculo").getAsString());
        jLabel4.setEnabled(true);

        jPlaca.setEditable(false);
        CardLayout layoutAditionalDataPanel = (CardLayout) this.jPanel2.getLayout();
        layoutAditionalDataPanel.show(jPanel2, "manual");
        jLabel11.setEnabled(true);
        jLabel12.setText("DISPOSITIVO");
    }

    void procesaRespuestaAutorizacioGLP(JsonObject response) {
        jPlaca.setText(response.get("placa_vehiculo").getAsString());
        jLabel4.setEnabled(true);

        jPlaca.setEditable(false);
        CardLayout layoutAditionalDataPanel = (CardLayout) this.jPanel2.getLayout();
        layoutAditionalDataPanel.show(jPanel2, "manual");
        jLabel11.setEnabled(true);
        jLabel12.setText("DISPOSITIVO");
    }

    private void testing() {
        // glp: 000000172E3C
        // cpropio:  C2000004B0F58201
        JsonObject json = new JsonObject();
        json.addProperty("cara", jcara.getText());
        json.addProperty("serial", jserialtest.getText());

        if (HABILITAR_CONSULTA_SICOM) {
            solicitarAutorizacionVentaGLP(json);
        } else {
            solicitarAutorizacionVenta(json);
        }
    }

    private void Async(Runnable runnable, int tiempo) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(runnable, tiempo, TimeUnit.SECONDS);
              
    }

    private ScheduledFuture<?> Async(Runnable runnable, int tiempo, Runnable alTerminar) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = executor.schedule(() -> {
            runnable.run();
            // Al finalizar el hilo, ejecuta el callback si no es nulo
            if (alTerminar != null) {
                SwingUtilities.invokeLater(alTerminar); // Para actualizar la UI
            }
        }, tiempo, TimeUnit.SECONDS);
        return future;
    }
    public void mostrarPanelMensaje(String mensaje, String icono, boolean hablilitarBoton) {
        mostrarPanelMensaje(mensaje, icono, true, hablilitarBoton);
    }

    public void mostrarPanelMensaje(String mensaje, String icono, boolean autoclose, boolean habilitarBoton) {
        mostrarPanelMensaje(mensaje, icono, autoclose, habilitarBoton, null);
    }
    
    public void mostrarPanelMensaje(String mensaje, String icono, boolean autoclose, boolean habilitarBoton, Runnable customHandler) {
        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

        Runnable runnable = customHandler != null ? customHandler : () -> {
            cambiarPanelHome();
        };

        PanelNotificacion panelMensaje = new PanelNotificacion(mensaje, icono, habilitarBoton, runnable);
        pnl_container.add("card_mensajes", panelMensaje);

        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        panelMensaje.mostrar();
        if (autoclose && !habilitarBoton) {
                  Async(runnable, 5);
        
            }
        }

public void mostrarPanelMensajeFinal(String mensaje, String icono, boolean habilitarboton) {
        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }
        Runnable runnable = this::cancelar;               
        PanelNotificacion panelMensaje = new PanelNotificacion(mensaje, icono, habilitarboton, runnable);
        pnl_container.add("card_mensajes", panelMensaje);
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        panelMensaje.mostrar();
        //Async(runnable, 3);
        Async(() -> {
            // Código que quieres ejecutar después del retraso (tarea principal)
        }, 3, () -> {
        InfoViewController vista = InfoViewController.getInstance();
            if (vista != null) {
                vista.setVisible(true);     // Asegura que sea visible
                vista.setState(JFrame.NORMAL); // Si está minimizada, la restaura (import java.awt.Frame o javax.swing.JFrame)
                vista.toFront();            // La trae al frente
                vista.requestFocus();       // Pone el foco en la ventana       
                cancelar(); 
                VentaPlacaView vpp = VentaPlacaView.getInstance(); 
                // si guardas como singleton/instancia global
                if (vpp != null) vpp.setVisible(false);

                InfoViewController.getInstance().setVisible(true);
                NovusUtils.printLn("LLego tardeeee " );
            }
            this.setVisible(false);

                });
            
    }

 
    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    
    }

    private void liberarAutorizacion() {
        if (recepcionData != null) {
            LiberarAutorizacionCliente liberarAutorizacionCliente = new LiberarAutorizacionCliente();
            String odometro = jKilometraje.getText().trim();
            odometro = odometro.isEmpty() ? "0" : odometro;
            liberarAutorizacionCliente.generarLiberacionPorcancelacion(recepcionData, Main.persona.getId(), odometro);
        }
    }
    
    public boolean CancelaBotones(){
        
        return enVenta_1;
        
    }
    
    public void deshablitar(){
        
        enVenta_1 = false;
        
    }
}

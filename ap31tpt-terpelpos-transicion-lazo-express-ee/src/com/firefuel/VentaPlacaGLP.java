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
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.facade.ClienteFacade;
import com.facade.LiberarAutorizacionCliente;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.PrinterFacade;
import com.server.api.ServerComandoWS;

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
import java.awt.Component;
import static java.lang.Thread.sleep;
import java.util.concurrent.ScheduledFuture;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import teclado.view.common.TecladoExtendido;

/**
 * @author novus
 */
public class VentaPlacaGLP extends javax.swing.JDialog {
    private String productoSeleccionado = "";
    private String productoManguera = "";
    int longlista = 0;
    boolean HABILITAR_CONSULTA_SICOM = false;
    boolean autorizar= false;
    private Timer mensajeTimer; // Timer para limpiar mensaje automáticamente
    InfoViewController parent;
    TreeMap<Integer, Surtidor> mangueras = new TreeMap<>();
    public static VentaPlacaGLP instance = null;
    JsonObject recepcionData = null;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    Timer timer = null;
    public static VentaPlacaGLP ventaPredefinirPlaca = null;
    InfoViewController vistaPrincipal;
    public static VentaPlacaGLP instance1 = null;
    boolean enVenta_1 = false;
    
    
    
    public VentaPlacaGLP(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        jLabel12.setVisible(true);
        CardLayout layoutAditionalDataPanel = (CardLayout) this.jPanel2.getLayout();
        layoutAditionalDataPanel.show(jPanel2, "manual");
        
        jLabel12.setVisible(false);
        jLabel2.setVisible(false);
        jLabel4.setEnabled(false);
        jLabel4.setText("CONSULTAR");
        jLabel11.setVisible(false);
        
        // Deshabilitar campo de placa hasta seleccionar manguera
        jPlaca.setEnabled(false);
        jPlaca.setEditable(false);
        jPlaca.setBackground(new java.awt.Color(200, 200, 200)); // Color gris para indicar deshabilitado
        jLabel5.setText("SELECCIONE UNA MANGUERA PRIMERO"); // Mensaje indicativo
        
        //this.init();
        solicitarDatosSurtidor(0);
        instance1 = this;
        jserialtest.setVisible(NovusUtils.productionServer());
        jcara.setVisible(NovusUtils.productionServer());

        HABILITAR_CONSULTA_SICOM = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_HABILITAR_CONSULTA_SICOM, true);

        //jLabel11.setVisible(HABILITAR_CONSULTA_SICOM);
        //jLabel12.setVisible(HABILITAR_CONSULTA_SICOM);
    }

    public static VentaPlacaGLP getInstance(InfoViewController vistaPrincipal, boolean modal) {
        if (VentaPlacaGLP.instance == null) {
            VentaPlacaGLP.instance = new VentaPlacaGLP(vistaPrincipal, modal);
        }
        VentaPlacaGLP.actualizarDatos();
        return VentaPlacaGLP.instance;
    }
    
     public static VentaPlacaGLP getInstance1() {
         
         return instance1;
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
        mangueras = sdao.getManguerasProductosAutorizacionGLP();
        NovusUtils.printLn("[solicitarDatosSurtidor]Mangueras: " + mangueras);
    
        
        DefaultListModel<String> demoList = new DefaultListModel<>();
        for (Map.Entry<Integer, Surtidor> entry : mangueras.entrySet()) {
            Surtidor val = entry.getValue();
            if ((cara == val.getCara()) || (cara == 0)) {
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
            }
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
            // Solo poner foco en placa si está habilitada (manguera seleccionada)
            if (jPlaca.isEnabled()) {
                jPlaca.requestFocus();
            } else {
                // Si no hay manguera seleccionada, poner foco en la lista de mangueras
                jList1.requestFocus();
            }
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
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        jcara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcaraActionPerformed(evt);
            }
        });
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

        jLabel5.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
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
        jLabel4.setBounds(950, 720, 180, 70);

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
        jLabel10.setBounds(770, 720, 180, 70);

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

        jLabel1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel1.setText("INGRESE PLACA");
        jPanel3.add(jLabel1);
        jLabel1.setBounds(640, 10, 240, 28);

        jPlaca.setBackground(new java.awt.Color(239, 239, 239));
        jPlaca.setFont(new java.awt.Font("Roboto", 1, 70)); // NOI18N
        jPlaca.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPlacaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPlacaKeyTyped(evt);
            }
        });
        jPanel3.add(jPlaca);
        jPlaca.setBounds(640, 40, 440, 90);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel2.setText("KILOMETRAJE");
        jPanel3.add(jLabel2);
        jLabel2.setBounds(640, 130, 220, 28);

        jKilometraje.setEditable(false);
        jKilometraje.setBackground(new java.awt.Color(239, 239, 239));
        jKilometraje.setFont(new java.awt.Font("Roboto", 1, 70)); // NOI18N
        jKilometraje.setText("000");
        jKilometraje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
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
        jKilometraje.setBounds(640, 40, 440, 90);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel3.setText("SELECCIONE MANGUERA");
        jPanel3.add(jLabel3);
        jLabel3.setBounds(80, 10, 330, 28);

        jScrollPane1.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N

        jList1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
        jList1.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
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
        jScrollPane1.setBounds(80, 40, 510, 210);

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        jPanel3.add(jPanel1);
        jPanel1.setBounds(70, 260, 1040, 340);

        jPanel2.add(jPanel3, "manual");

        pnl_principal.add(jPanel2);
        jPanel2.setBounds(40, 90, 1190, 610);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
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
    }// </editor-fold>//GEN-END:initComponents

  private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
      // TODO add your handling code here:
  }//GEN-LAST:event_jPanel1MouseReleased

  private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseReleased
      NovusUtils.beep();
      
      NovusUtils.printLn("[jLabel11MouseReleased] Botón SICOM clickeado con producto: " + productoSeleccionado);
    if("GLP".equals(productoSeleccionado)&& jPlaca.getText()!= ""&& jLabel4.isEnabled()){
      validacionSicom();
    }
      
     //jLabel11MouseReleased(evt);
     
  }//GEN-LAST:event_jLabel4MouseReleased

  private void jPlacaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlacaActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_jPlacaActionPerformed

  private void jLabel12MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseReleased
      if (HABILITAR_CONSULTA_SICOM) {
          cambioManual();
     }
  }//GEN-LAST:event_jLabel12MouseReleased

  private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseReleased
      NovusUtils.beep();
      liberarAutorizacion();
      cancelar();
  }//GEN-LAST:event_jLabel10MouseReleased

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
//      if (!jPlaca.isEditable()) {
//          jKilometraje.requestFocus();
//          deshabilitarTeclas(true);
//      }
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

               // jLabel4.setEnabled(true);
        // //Habilitar o deshabilitar el boton de SICOM
//        // if("GLP".equals(productoSeleccionado) && HABILITAR_CONSULTA_SICOM && !Usado){
//        //     jLabel11.setEnabled(true);
//        //     NovusUtils.printLn("✅ HABILITANDO botón - SICOM");
//        // } else {
//        //     jLabel11.setEnabled(false);
//        //     NovusUtils.printLn("❌ DESHABILITANDO botón - SICOM");
//        // }
        
        // Lógica de GLP no va se cambia por elservicio
//        if("GLP".equals(productoSeleccionado)) {
//            System.out.println("ENTRANDO EN VALIDACIÓN GLP");
//            if(NovusConstante.HAY_INTERNET) {
//                jLabel4.setEnabled(false);
//                NovusUtils.printLn("✅ DESACTIVANDO botón - GLP con internet");
//            } else {
//                jLabel4.setEnabled(true);
//                NovusUtils.printLn("✅ ACTIVANDO botón - GLP sin internet");
//            }
//        } else if(Usado){
//            jLabel4.setEnabled(false);
//            NovusUtils.printLn("❌ DESHABILITANDO botón - Guardar - GLP");
//        } else {
//            jLabel4.setEnabled(true);
//            NovusUtils.printLn("✅ ACTIVANDO botón - No es GLP");
//        }
        
        // Habilitar campo de placa cuando se selecciona una manguera
        jPlaca.setEnabled(true);
        jPlaca.setEditable(true);
        jPlaca.setBackground(new java.awt.Color(239, 239, 239)); // Color normal
        jPlaca.setText(""); // Limpiar el mensaje indicativo
        
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
          // Habilitar campo de placa cuando se selecciona una manguera
          jPlaca.setEnabled(true);
          jPlaca.setEditable(true);
          jPlaca.setBackground(new java.awt.Color(239, 239, 239)); // Color normal
          jPlaca.setText(""); // Limpiar el mensaje indicativo
          
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
      NovusUtils.limitarCarateres(evt, jPlaca, 6, jNotificacion, caracteresAceptados);
  }//GEN-LAST:event_jPlacaKeyTyped

    private void jLabel14MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseReleased
        liberarAutorizacion();
        cancelar();
    }//GEN-LAST:event_jLabel14MouseReleased

    private void jKilometrajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jKilometrajeKeyTyped
        //      String caracteresAceptados = "[0-9]";
        //      NovusUtils.limitarCarateres(evt, jKilometraje, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jKilometrajeKeyTyped

    private void jKilometrajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jKilometrajeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jKilometrajeActionPerformed

    private void jKilometrajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jKilometrajeFocusLost
        //jKilometraje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
    }//GEN-LAST:event_jKilometrajeFocusLost

    private void jKilometrajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jKilometrajeFocusGained
        // jKilometraje.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 5, true));
        // NovusUtils.deshabilitarCopiarPegar(jKilometraje);
    }//GEN-LAST:event_jKilometrajeFocusGained

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseReleased
        //   BOTON SICOM
        //      NovusUtils.printLn("[jLabel11MouseReleased] Botón SICOM clickeado con producto: " + productoSeleccionado);
        //    if("GLP".equals(productoSeleccionado)){
            //      validacionSicom();
            //    }else{
            //      NovusUtils.printLn("[jLabel11MouseReleased] Producto no es GLP");
            //      mostrarPanelMensaje("SICOM SOLO DISPONIBLE PARA GLP", "/com/firefuel/resources/btBad.png", false);
            //    }
    }//GEN-LAST:event_jLabel11MouseReleased

    private void jPlacaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPlacaKeyReleased
                if(jPlaca.getText().length() == 6){
                    jLabel4.setEnabled(true);
                }    // O setVisible(true)    }
                else {        
                    jLabel4.setEnabled(false);
                }  
                // O setVisible(false)

    }//GEN-LAST:event_jPlacaKeyReleased

    private void jcaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcaraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcaraActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField jKilometraje;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
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
        VentaPlacaGLP.ventaPredefinirPlaca = null;
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
        if (!jPlaca.getText().trim().equals("")) {
            mostrarPanelMensaje("SOLICITANDO AUTORIZACIÓN", "/com/firefuel/resources/loader_fac.gif", true);
            this.taskRunner(() -> {
                try{
                    JsonObject response = ClienteFacade.consultaVehiculoSicom(jPlaca.getText().toUpperCase());
                    NovusUtils.printLn("[validacionSicom] response: " + response);
                    cambiarPanelHome();
                    
                    //Verifacion si la respuesta es por timeout o error de conexion
                    if (response == null){
                        NovusUtils.printLn("[SICOM] Timeout o error de conexion");
                        mostrarPanelMensaje("ERROR: TIMEOUT O ERROR DE CONEXION", "/com/firefuel/resources/btBad.png", false);
                        return;
                    }
                    
                    //Verificar si tiene el campo httpStatus
                    if(!response.has("httpStatus")){
                        NovusUtils.printLn("[SICOM] No tiene el campo httpStatus");
                        mostrarPanelMensaje("ERROR: No tiene el campo httpStatus", "/com/firefuel/resources/btBad.png", false);
                        return;
                    }
                    
                    int statusCode = response.get("httpStatus").getAsInt();
                    switch (statusCode) {
                        case 200:
                            NovusUtils.printLn("[SICOM] Codigo de respuesta 200 - Vehiculo encontrado");
                            
                            // Verificar si la placa está habilitada
                            if (response.has("habilitado") && response.get("habilitado").getAsBoolean()) {
                                NovusUtils.printLn("[SICOM] Placa habilitada - Creando autorización");
                                crearAutorizacionVentaGLP(response);
                            } else {
                                NovusUtils.printLn("[SICOM] Placa no habilitada");
                                //mostrarPanelMensaje("PLACA NO HABILITADA POR SICOM", "/com/firefuel/resources/btBad.png", false);
                                mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                            }
                            break;
                            
                        case 404:
                            NovusUtils.printLn("[SICOM] Codigo de respuesta 404 - Vehiculo no encontrado");
                            //mostrarPanelMensaje("VEHICULO NO ENCONTRADO EN SICOM GLP", "/com/firefuel/resources/btBad.png", false);
                            mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                            break;
                            
                        case 500:
                            NovusUtils.printLn("[SICOM] Codigo de respuesta 500 - Error interno de SICOM");
                            mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);

                            break;
                            
                        default:
                            NovusUtils.printLn("[SICOM] Codigo de respuesta desconocido: " + statusCode);
                            //mostrarPanelMensaje("ERROR AL CONSULTAR VEHICULO EN SICOM GLP - CODIGO: " + statusCode, "/com/firefuel/resources/btBad.png", false);
                            mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                            break;
                    }
                }catch(Exception e){
                    NovusUtils.printLn("❌ [SICOM] Excepción: " + e.getMessage());
                    cambiarPanelHome();
                    //mostrarPanelMensaje("ERROR INESPERADO EN CONSULTA SICOM", "/com/firefuel/resources/btBad.png", false);
                    mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                } 
                
            });
        }
    }
 

    void crearAutorizacionVentaGLP(JsonObject response) {
        autorizar = false;
        
        try {
            NovusUtils.printLn("🔄 [AUTORIZACIÓN] Iniciando creación de autorización GLP");
            
            if (response != null) {
                if (response.get("error") == null) {
                    // Verificar si tiene información del vehículo
                    if (response.has("informacion_vehiculo") && !response.get("informacion_vehiculo").isJsonNull()) {
                        JsonObject infoVehiculo = response.get("informacion_vehiculo").getAsJsonObject();
                        
                        if (infoVehiculo.has("marca") && !infoVehiculo.get("marca").isJsonNull()) {
                            String marca = infoVehiculo.get("marca").getAsString();
                            String tipoVehiculo = infoVehiculo.has("tipoVehiculo") ? infoVehiculo.get("tipoVehiculo").getAsString() : "N/A";
                            
                            NovusUtils.printLn("✅ [AUTORIZACIÓN] Marca: " + marca + ", Tipo: " + tipoVehiculo);
                            
                            // Mostrar información del vehículo
                            String infoDisplay = marca + " - " + tipoVehiculo;
                            jLabel5.setText("<html>" + infoDisplay + "</html>");
                            
                            // Habilitar botones y deshabilitar placa
                            jLabel11.setEnabled(false);
                            jLabel4.setEnabled(true);
                            autorizar = true;
                            jPlaca.setEditable(false);
                            jPlaca.setEnabled(false);
                            
                            NovusUtils.printLn("✅ [AUTORIZACIÓN] Creando autorización...");
                            crearAutorizacion_1();
                            
                        } else {
                            NovusUtils.printLn("❌ [AUTORIZACIÓN] No se encontró marca en información del vehículo");
                           // mostrarPanelMensaje("Venta no autorizada por SICOM - Sin información de marca", "/com/firefuel/resources/btBad.png", false);
                            mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                
                        }
                    } else {
                        NovusUtils.printLn("❌ [AUTORIZACIÓN] No se encontró información del vehículo");
                        //mostrarPanelMensaje("Venta no autorizada por SICOM - Sin información del vehículo", "/com/firefuel/resources/btBad.png", false);
                        mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                    }
                } else {
                    NovusUtils.printLn("❌ [AUTORIZACIÓN] Error en respuesta SICOM");
                    //mostrarPanelMensaje("Venta no autorizada por SICOM - Error en respuesta", "/com/firefuel/resources/btBad.png", false);
                    mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                }
            } else {
                NovusUtils.printLn("❌ [AUTORIZACIÓN] Respuesta nula");
                //mostrarPanelMensaje("Venta no autorizada por SICOM - Respuesta nula", "/com/firefuel/resources/btBad.png", false);
                mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
            }
        } catch (Exception e) {
            NovusUtils.printLn("❌ [AUTORIZACIÓN] Excepción: " + e.getMessage());
            e.printStackTrace();
            //mostrarPanelMensaje("ERROR AL PROCESAR AUTORIZACIÓN SICOM", "/com/firefuel/resources/btBad.png", false);
            mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
        }
    }
    private void crearAutorizacion_1() {
        
        recepcionData = new JsonObject();
            recepcionData.addProperty("saldo", "0");
            recepcionData.addProperty("tipoCupo", "L");
        
        String placa = jPlaca.getText().trim();
        String odometro = jKilometraje.getText().trim();
        SurtidorDao dao = new SurtidorDao();
        String keyFind = jList1.getSelectedValue().split(":")[0];
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
                try{
                    
                    if(dao.crearAutorizacionPorPlaca(Main.persona, surtidor, placa, odometro, recepcionData, true))  {   
                         enVenta_1 = true;
//                        mostrarPanelMensajeFinal(mensajeAutorizacion(surtidor.isEstaDentroDelRango(), hayClienteComunidades(recepcionData)) + surtidor.getCara(), "/com/firefuel/resources/btOk.png", true);
                        mostrarPanelMensajeFinal("Venta autorizada en la cara: "+surtidor.getCara()+"<br><b> Placa Consultada: " + jPlaca.getText() + "</b><br></span>", "/com/firefuel/resources/btOk.png", true);
      
                    }else{
                         //mostrarPanelMensajeFinal("Venta no Autorizada" ,"/com/firefuel/resources/btOk.png", true);
                         mostrarPanelMensajeFinal("Venta no autorizada por sicom <br><b>Placa Consultada: " + jPlaca.getText().toUpperCase() + "</b><br></span>", "/com/firefuel/resources/btBad.png", false);
                    }
                }
                catch (DAOException ex) {
                 Logger.getLogger(VentaPlacaGLP.class.getName()).log(Level.SEVERE, null, ex);
                                  
                 }
                    
                        
               // mostrarPanelMensajeFinal(mensajeAutorizacion(surtidor.isEstaDentroDelRango(), hayClienteComunidades(recepcionData)) + surtidor.getCara(), "/com/firefuel/resources/btOk.png", true);
                       
    }
    
    //BTZ736
    private void crearAutorizacion() {
        NovusUtils.printLn("[VentaPredefinirPlaca] Iniciando creación de autorización");
        if (HABILITAR_CONSULTA_SICOM) {
            recepcionData = new JsonObject();
            recepcionData.addProperty("saldo", "0");
            recepcionData.addProperty("tipoCupo", "L");
        }
        if (!recepcionData.has("saldo")) {
            recepcionData.addProperty("saldo", "0");
            recepcionData.addProperty("tipoCupo", "L");
        }
        if (recepcionData.get("saldo").getAsDouble() > 0 || recepcionData.get("tipoCupo").getAsString().equals("L") || hayClienteComunidades(recepcionData)) {
            String placa = jPlaca.getText().trim();
            String odometro = jKilometraje.getText().trim();
            if (!placa.isEmpty() && !odometro.isEmpty()) {
                jLabel5.setText("");
                SurtidorDao dao = new SurtidorDao();

                if (jList1.getSelectedIndex() == -1) {
                    jLabel5.setText("SELECCIONE EL \r\nPRODUCTO A VENDER");
                }

                String keyFind = jList1.getSelectedValue().split(":")[0];

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

                        dao.crearAutorizacionPorPlaca(Main.persona, surtidor, placa, odometro, recepcionData, HABILITAR_CONSULTA_SICOM);
//                         boolean facturaRegistrada = (boolean) result.get("facturaRegistrada");
//                         boolean resultAutorizacion = (boolean) result.get("result");
//                         String placaVehiculo = (String) result.get("placaVehiculo");
//                         NovusUtils.printLn("[VentaPredefinirPlaca] facturaRegistrada: " + facturaRegistrada);
//                         NovusUtils.printLn("[VentaPredefinirPlaca] resultAutorizacion: " + resultAutorizacion);
//                         NovusUtils.printLn("[VentaPredefinirPlaca] placaVehiculo: " + placaVehiculo);
//                         NovusUtils.printLn("[VentaPredefinirPlaca] Producto Manguera: " + productoManguera);
//                         if(facturaRegistrada && productoManguera.equals("GLP")){
//                             mostrarPanelMensaje("YA EXISTE UNA AUTORIZACION PARA LA PLACA : " + placaVehiculo + " EN SICOM", "/com/firefuel/resources/btBad.png", false);
////                         }else{
//                             mostrarPanelMensajeFinal(mensajeAutorizacion(surtidor.isEstaDentroDelRango(), hayClienteComunidades(recepcionData)) + surtidor.getCara(), "/com/firefuel/resources/btOk.png", true);
//                         }
                        mostrarPanelMensajeFinal(mensajeAutorizacion(surtidor.isEstaDentroDelRango(), hayClienteComunidades(recepcionData)) + surtidor.getCara(), "/com/firefuel/resources/btOk.png", true);
                       // timer = new Timer(5000, e -> cancelar());
                    } catch (DAOException ex) {
                        Logger.getLogger(VentaPlacaGLP.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    timer.start();
//                    timer.start();
                }
            } else {
                if (placa.isEmpty()) {
                    jLabel5.setText("Verifique la placa");
                } else {
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
            return "Venta autorizada por SICOM <html><span style='font-size:36px'><br><b>";
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
            this.solicitarAutorizacionVentaGLP(identifierRequest);
//        } else {
//            this.solicitarAutorizacionVenta(identifierRequest);
//        }
    }

    void solicitarAutorizacionVenta(JsonObject identifierRequest) {
        mostrarPanelMensaje("SOLICITANDO AUTORIZACIÓN", "/com/firefuel/resources/loader_fac.gif", true);
        Runnable validarCliente = () -> {
            PrinterFacade facade = new PrinterFacade();
            JsonObject request = new JsonObject();
            request.addProperty("identificador", identifierRequest.get("serial").getAsString());
            request.addProperty("identificadorEstacion", Main.credencial.getEmpresas_id());
            request.addProperty("tipoIdentificador", "IDROOM");
            request.addProperty("fechaConsulta", sdf.format(new Date()));

            request.addProperty("identificadorPromotor", Main.persona.getId());
            request.addProperty("identificadorCara", identifierRequest.get("cara").getAsInt());

            JsonObject response = ClienteFacade.consultaValidacionVenta(request);
            cambiarPanelHome();
            if (hayClienteComunidades(response)) {
                autorizacionClientesComunidades(response, identifierRequest);
            } else {
                if (response != null && response.get("data") != null && !response.get("data").isJsonNull()) {
                    double saldo = response.get("data").getAsJsonObject().get("saldo").getAsDouble();
                    String tipoCupo = response.get("data").getAsJsonObject().get("tipoCupo").getAsString();
                    if (saldo > 0 || tipoCupo.equals("L")) {
                        procesaRespuestaAutorizacion(response.get("data").getAsJsonObject());
                        recepcionData = response.get("data").getAsJsonObject();
                        recepcionData.addProperty("medioAutorizacion", "ibutton");
                        recepcionData.addProperty("serialDispositivo", identifierRequest.get("serial").getAsString());
                        solicitarDatosSurtidor(identifierRequest.get("cara").getAsInt());
                        jLabel5.setText("<html>" + recepcionData.get("nombreCliente").getAsString() + "<br/>SALDO: $ " + df.format(saldo) + "</html>");
                    } else {
                        String cliente = response.get("data").getAsJsonObject().get("placaVehiculo").getAsString().toUpperCase();
                        String mensaje = cliente + "\r\nCHIP: " + identifierRequest.get("serial").getAsString() + "\r\n" + " NO CUENTA CON SALDO EN LA CUENTA ";
                        jLabel7.setText("<html><center><b>" + cliente + "</b><br><font color='red'>NO CUENTA CON SALDO EN LA CUENTA [" + saldo + "] T:" + tipoCupo + "</center><font></html>");
                        facade.printFormato(mensaje);
                    }
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
        mostrarPanelMensaje("SOLICITANDO AUTORIZACIÓN", "/com/firefuel/resources/loader_fac.gif", true);
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

            JsonObject response = ClienteFacade.consultaValidacionVentaGLP(request);

            cambiarPanelHome();
            if (response != null && response.get("data") != null && !response.get("data").isJsonNull()) {
                procesaRespuestaAutorizacioGLP(response.get("data").getAsJsonObject());
                recepcionData = response.get("data").getAsJsonObject();
                recepcionData.addProperty("medioAutorizacion", "ibutton");
                recepcionData.addProperty("serialDispositivo", identifierRequest.get("serial").getAsString());
                solicitarDatosSurtidor(identifierRequest.get("cara").getAsInt());
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

    public void mostrarPanelMensaje(String mensaje, String icono, boolean hablilitarBoton) {
        mostrarPanelMensaje(mensaje, icono, true, hablilitarBoton);
    }

    public void mostrarPanelMensaje(String mensaje, String icono, boolean autoclose, boolean habilitarBoton) {
        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        PanelNotificacion panelMensaje = new PanelNotificacion(mensaje, icono, !autoclose, runnable);
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

//                InfoViewController.getInstance().setVisible(true);
//                NovusUtils.printLn("LLego tardeeee " );
            }
            this.setVisible(false);

                });
            
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
    
    private void cerrar() {
            NovusUtils.beep();
             vistaPrincipal.mostrarSubPanel(new VentaMenuPanelController(vistaPrincipal, parent));
             close(true);
        }

    void close(boolean liberar) {
            this.dispose();
            RumboView.instance = null;
            InfoViewController.NotificadorRumbo = null;

        }
    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    }

    private void liberarAutorizacion() {
        if (recepcionData != null & !HABILITAR_CONSULTA_SICOM) {
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

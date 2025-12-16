package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.persons.EliminarLecturaUseCase;
import com.application.useCases.persons.FindPersonaUseCase;
import com.application.useCases.persons.GetPrincipalTurnoUseCase;
import com.application.useCases.sutidores.GetTimeoutTotalizadoresUseCase;
import com.bean.BodegaBean;
import com.bean.JornadaBean;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.ConsultarUsuario;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.PersonasDao;
import com.dao.SurtidorDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import teclado.view.common.TecladoExtendido;

public class TurnosIniciarViewController extends javax.swing.JDialog {

    public GetPrincipalTurnoUseCase getPrincipalTurnoUseCase;
    public static TurnosIniciarViewController instance = null;
    public static boolean fromRfid = false;
    public InicioSurtidorView sview = null;
    public LecturasTanquesViewController lecturasView = null;
    public static long idPromotorTag = -1;
    public ArrayList<BodegaBean> loadedTanks = null;
    public TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers = null;
    ArrayList<Surtidor> lsurtidores = new ArrayList<>();
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    InfoViewController parent;
    DefaultTableCellRenderer textCenter;
    PersonaBean _persona = null;
    int saldo = 0;
    boolean principal = false;
    PersonaBean personaPrincipal;
    ConsultarUsuario consultaTags;
    public static final String RUTA_RECURSO = "/com/firefuel/resources/";
    PersonasDao pdao = new PersonasDao();
    Runnable handler = null;
    EliminarLecturaUseCase eliminarLecturaUseCase = new EliminarLecturaUseCase();
    FindPersonaUseCase findPersonaUseCase = new FindPersonaUseCase();
    GetTimeoutTotalizadoresUseCase getTimeoutTotalizadoresUseCase = new GetTimeoutTotalizadoresUseCase();

    public TurnosIniciarViewController(InfoViewController vistaPrincipal, boolean modal) {
        super(vistaPrincipal, modal);
        this.parent = vistaPrincipal;
        initComponents();
        this.init();
    }

    public TurnosIniciarViewController(InfoViewController vistaPrincipal, TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers, boolean modal) {
        super(vistaPrincipal, modal);
        this.parent = vistaPrincipal;
        initComponents();
        this.selectedTotalizers = selectedTotalizers;
        Main.LECTURAS_DISPONIBLE = true;
        this.init();
    }

    public TurnosIniciarViewController(InfoViewController vistaPrincipal, TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers, ArrayList<BodegaBean> loadedTanks, boolean modal) {
        super(vistaPrincipal, modal);
        this.parent = vistaPrincipal;
        initComponents();
        this.loadedTanks = loadedTanks;
        this.selectedTotalizers = selectedTotalizers;
        Main.LECTURAS_DISPONIBLE = true;
        this.init();
    }

    public static TurnosIniciarViewController getInstance(InfoViewController vistaPrincipal, boolean modal) {
        if (TurnosIniciarViewController.instance == null) {
            TurnosIniciarViewController.instance = new TurnosIniciarViewController(vistaPrincipal, modal);
        }
        return TurnosIniciarViewController.instance;
    }

    public static TurnosIniciarViewController getInstance(InfoViewController vistaPrincipal, TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers, ArrayList<BodegaBean> loadedTanks, boolean modal) {
        if (TurnosIniciarViewController.instance == null) {
            TurnosIniciarViewController.instance = new TurnosIniciarViewController(vistaPrincipal, selectedTotalizers, loadedTanks, modal);
        }
        return TurnosIniciarViewController.instance;
    }

    public static TurnosIniciarViewController getInstance(InfoViewController vistaPrincipal, TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers, boolean modal) {
        if (TurnosIniciarViewController.instance == null) {
            TurnosIniciarViewController.instance = new TurnosIniciarViewController(vistaPrincipal, selectedTotalizers, modal);
        }
        return TurnosIniciarViewController.instance;
    }

    void setViewTanques(LecturasTanquesViewController lecturasView) {
        this.lecturasView = lecturasView;
    }

    void setViewSurtidor(InicioSurtidorView sview) {
        this.sview = sview;
    }

    void init() {
        //pdao.eliminarLectura();
        establecerAlertaRegistro("S");
		establecerModoRegistro("N");
        eliminarLecturaUseCase.execute();

        consultaTags = new ConsultarUsuario();
        consultaTags.start();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        jsaldo.requestFocus();

        jPanel2.setVisible(false);

        jindicadorLecturas.setText("CONSULTANDO LECTURAS...");

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
        this.jindicadorLecturas.setVisible(false);
        this.mostrarTurnos();
        TurnosIniciarViewController.juser.requestFocus();
        mostrarTeclado();
        activarTeclado();

    }

    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.getPrincipalTurnoUseCase = new GetPrincipalTurnoUseCase();
        jPanel1 = new javax.swing.JPanel();
        pnl_inicio_turno = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jindicadorLecturas = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jBack = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jsaldo = new javax.swing.JTextField();
        juser = new javax.swing.JTextField();
        jpassword = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jPanel2 = new TecladoExtendido();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jPanel1.setLayout(new java.awt.CardLayout());

        pnl_inicio_turno.setLayout(null);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(186, 12, 47));
        jLabel7.setText("SALDO");
        pnl_inicio_turno.add(jLabel7);
        jLabel7.setBounds(370, 140, 160, 30);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("USUARIO");
        pnl_inicio_turno.add(jLabel6);
        jLabel6.setBounds(40, 140, 160, 30);

        jindicadorLecturas.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jindicadorLecturas.setForeground(new java.awt.Color(255, 255, 255));
        jindicadorLecturas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pnl_inicio_turno.add(jindicadorLecturas);
        jindicadorLecturas.setBounds(98, 727, 920, 60);

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setText("CONTRASEÑA");
        pnl_inicio_turno.add(jLabel5);
        jLabel5.setBounds(40, 240, 160, 20);

        jBack.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jBackMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jBackMouseReleased(evt);
            }
        });
        pnl_inicio_turno.add(jBack);
        jBack.setBounds(10, 10, 70, 71);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TURNO ACTIVOS");
        pnl_inicio_turno.add(jLabel4);
        jLabel4.setBounds(670, 120, 580, 60);

        jsaldo.setBackground(new java.awt.Color(186, 12, 47));
        jsaldo.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jsaldo.setForeground(new java.awt.Color(255, 255, 255));
        jsaldo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jsaldo.setBorder(null);
        jsaldo.setCaretColor(new java.awt.Color(255, 255, 0));
        jsaldo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jsaldoFocusGained(evt);
            }
        });
        jsaldo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jsaldoMouseReleased(evt);
            }
        });
        jsaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsaldoActionPerformed(evt);
            }
        });
        jsaldo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jsaldoKeyTyped(evt);
            }
        });
        pnl_inicio_turno.add(jsaldo);
        jsaldo.setBounds(370, 180, 250, 45);

        juser.setBackground(new java.awt.Color(186, 12, 47));
        juser.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        juser.setForeground(new java.awt.Color(255, 255, 255));
        juser.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        juser.setBorder(null);
        juser.setCaretColor(new java.awt.Color(255, 255, 0));
        juser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                juserFocusGained(evt);
            }
        });
        juser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                juserMouseReleased(evt);
            }
        });
        juser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juserActionPerformed(evt);
            }
        });
        juser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                juserKeyTyped(evt);
            }
        });
        pnl_inicio_turno.add(juser);
        juser.setBounds(50, 175, 270, 50);

        jpassword.setBackground(new java.awt.Color(186, 12, 47));
        jpassword.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jpassword.setForeground(new java.awt.Color(255, 255, 255));
        jpassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jpassword.setBorder(null);
        jpassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jpasswordFocusGained(evt);
            }
        });
        jpassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jpasswordMouseReleased(evt);
            }
        });
        jpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jpasswordActionPerformed(evt);
            }
        });
        jpassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jpasswordKeyTyped(evt);
            }
        });
        pnl_inicio_turno.add(jpassword);
        jpassword.setBounds(40, 270, 280, 45);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null}
                },
                new String[]{
                        "PROMOTOR", "IDENTIFICACION", "F. INICIO"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setRowHeight(28);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        pnl_inicio_turno.add(jScrollPane1);
        jScrollPane1.setBounds(660, 180, 600, 195);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("INICIAR TURNO");
        pnl_inicio_turno.add(jTitle);
        jTitle.setBounds(110, 0, 720, 90);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alerta.png"))); // NOI18N
        pnl_inicio_turno.add(jLabel3);
        jLabel3.setBounds(0, 700, 80, 100);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_inicio_turno.add(jLabel27);
        jLabel27.setBounds(80, 10, 10, 68);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_inicio_turno.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_inicio_turno.add(jNotificacion);
        jNotificacion.setBounds(670, 10, 590, 60);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_inicio_turno.add(jLabel28);
        jLabel28.setBounds(80, 713, 10, 80);

        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel2MouseReleased(evt);
            }
        });
        pnl_inicio_turno.add(jPanel2);
        jPanel2.setBounds(100, 380, 1030, 340);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_turnos_iniciar.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_inicio_turno.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        jPanel1.add(pnl_inicio_turno, "card2");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(186, 12, 47));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 100));

        jPanel5.setBackground(new java.awt.Color(186, 12, 47));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 700, 1280, 100));

        jPanel1.add(jPanel3, "card3");

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void juserKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_juserKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, juser, 12, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_juserKeyTyped

    private void jsaldoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jsaldoKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jsaldo, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jsaldoKeyTyped

    private void jpasswordKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpasswordKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jpassword, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jpasswordKeyTyped

    private void juserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_juserFocusGained
        NovusUtils.deshabilitarCopiarPegar(juser);
    }//GEN-LAST:event_juserFocusGained

    private void jsaldoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jsaldoFocusGained
        NovusUtils.deshabilitarCopiarPegar(jsaldo);
    }//GEN-LAST:event_jsaldoFocusGained

    private void jpasswordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jpasswordFocusGained
        NovusUtils.deshabilitarCopiarPegar(jpassword);
    }//GEN-LAST:event_jpasswordFocusGained

    private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jPanel2MouseReleased
    }// GEN-LAST:event_jPanel2MouseReleased

    private void jsaldoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jsaldoMouseReleased
        mostrarTeclado();
        desactivarTeclado();
    }// GEN-LAST:event_jsaldoMouseReleased

    private void jindicadorLecturasMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jindicadorLecturasMouseReleased
    }// GEN-LAST:event_jindicadorLecturasMouseReleased

    private void jBackMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed
    }// GEN-LAST:event_jLabel2MousePressed

    private void jsaldoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jsaldoActionPerformed
        guardar(false);
    }// GEN-LAST:event_jsaldoActionPerformed

    private void jBackMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    private void juserActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_juserActionPerformed
        guardar(false);
    }// GEN-LAST:event_juserActionPerformed

    private void juserMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_juserMouseReleased
        mostrarTeclado();
        activarTeclado();
    }// GEN-LAST:event_juserMouseReleased

    private void jLabel14MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel14MousePressed
    }// GEN-LAST:event_jLabel14MousePressed

    private void jLabel14MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel14MouseReleased

    }// GEN-LAST:event_jLabel14MouseReleased

    private void jpasswordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jpasswordActionPerformed
        guardar(false);
    }// GEN-LAST:event_jpasswordActionPerformed

    private void jpasswordMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jpasswordMouseReleased
        mostrarTeclado();
        desactivarTeclado();
    }// GEN-LAST:event_jpasswordMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jBack;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jindicadorLecturas;
    public static javax.swing.JPasswordField jpassword;
    public static javax.swing.JTextField jsaldo;
    public static javax.swing.JTextField juser;
    private javax.swing.JPanel pnl_inicio_turno;

    // End of variables declaration//GEN-END:variables
    private void cerrar() {
        establecerAlertaRegistro("N");
		establecerModoRegistro("N");
        TurnosIniciarViewController.instance = null;
        InfoViewController.instanciaInicioTurno = null;
        if (this.sview != null) {
            this.sview.dispose();
            this.sview.setVisible(false);
        }

        if (this.lecturasView != null) {
            this.lecturasView.dispose();
            this.lecturasView.setVisible(false);
        }
        this.setVisible(false);
        this.dispose();
        this.parent.recargarPersona();
        consultaTags.detenerProceso();

    }

    private void cerrar(boolean ready) {
        try {
            establecerAlertaRegistro("N");
			establecerModoRegistro("N");
            TurnosIniciarViewController.instance = null;
            InfoViewController.instanciaInicioTurno = null;
            if (consultaTags != null) {
                consultaTags.detenerProceso();
            }

            if (this.sview != null) {
                this.sview.dispose();
                this.sview.setVisible(false);
            }

            if (this.lecturasView != null) {
                this.lecturasView.dispose();
                this.lecturasView.setVisible(false);
            }

            this.setVisible(false);
            this.dispose();

            if (ready) {
                Main.info.cerrarSubmenu();
            }
            this.parent.recargarPersona();

            System.gc();
        } catch (Exception ex) {
            Logger.getLogger(TurnosIniciarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardar(boolean fromRFID) {

        SurtidorDao sDao = new SurtidorDao();
        boolean haySurtidor = sDao.haySurtidores() && !Main.SIN_SURTIDOR;
        if (!haySurtidor) {
            this.jindicadorLecturas.setVisible(false);
        }
        boolean main = true;
        this.parent.recargarPersona();
        if (Main.persona != null) {
            main = false;
        }
        if ((Main.LECTURAS_DISPONIBLE && haySurtidor) || !main || (!haySurtidor)) {
            if (juser.getText().isEmpty()) {
                juser.requestFocus();
            } else if (jpassword.getPassword().length == 0) {
                jpassword.requestFocus();
                this.jindicadorLecturas.setVisible(true);
                this.jindicadorLecturas.setText("INGRESE PIN");
            } else if (jsaldo.getText().isEmpty()) {
                jsaldo.requestFocus();
                this.jindicadorLecturas.setVisible(true);
                this.jindicadorLecturas.setText("INGRESE SALDO");
            } else {
                try {
                    String user = juser.getText().trim();
                    String pass = new String(jpassword.getPassword());
                    int prepwd = Integer.parseInt(pass);
                    int saldotxt = Integer.parseInt(jsaldo.getText().trim());
                   // PersonaBean persona = pdao.findPersona(user, prepwd, fromRFID);
                    PersonaBean persona = findPersonaUseCase.execute(user, prepwd, fromRFID);

                    if (persona != null) {
                        juser.setText(persona.getIdentificacion());
                        jpassword.setText(persona.getPin() + "");
                        //PersonaBean persona_principal = pdao.getPrincipalTurno();
                        PersonaBean persona_principal = getPrincipalTurnoUseCase.execute();
                        if (selectedTotalizers != null && !selectedTotalizers.isEmpty()) {
                            this._persona = persona;
                            this.saldo = saldotxt;
                            this.principal = main;
                            this.personaPrincipal = persona_principal;
                            showMessage("INICIANDO TURNO ....",
                                    "/com/firefuel/resources/loader_fac.gif",
                                    false, null,
                                    LetterCase.FIRST_UPPER_CASE);
                            setTimeout(1, () -> {
                                procesarEnvioTurno();
                            });
                        } else {
                            procesarEnvioTurno(persona, saldotxt, main, persona_principal);
                        }
                    } else {
                        mensajes("ERROR EN LAS CREDENCIALES", "/com/firefuel/resources/btBad.png", true, 5000, false, true);
                    }
                //} catch (DAOException ex) {
                } catch (Exception ex) {
                    NovusUtils.printLn(ex.getMessage());
                }
            }
        } else if (haySurtidor) {
            jindicadorLecturas.setText("ERROR, NO HAY LECTURAS DISPONIBLES");
            mensajes("ERROR, NO HAY LECTURAS DISPONIBLES", "/com/firefuel/resources/btBad.png", true, 5000, false, true);
        } else {
            jindicadorLecturas.setText("ERROR, NO SE PUEDE INICIAR TURNO , INTENTELO MAS TARDE");
            mensajes("NO SE PUEDE INICIAR TURNO , INTENTELO MAS TARDE", "/com/firefuel/resources/btBad.png", true, 5000, false, false);
        }
    }

    public void iniciar(PersonaBean persona) {
        try {
            boolean main = true;
            this.parent.recargarPersona();
            if (Main.persona != null) {
                main = false;
            }
            //PersonaBean persona_principal = pdao.getPrincipalTurno();
            PersonaBean persona_principal = getPrincipalTurnoUseCase.execute();
            if (persona != null) {
                this._persona = persona;
                this.saldo = 0;
                this.principal = main;
                this.personaPrincipal = persona_principal;
                showMessage("INICIANDO TURNO ....",
                        "/com/firefuel/resources/loader_fac.gif", false,
                        null, LetterCase.FIRST_UPPER_CASE);
                setTimeout(1, () -> {
                    procesarEnvioTurno();
                });
            } else {
                jNotificacion.setVisible(true);
                jNotificacion.setText("Error en las credenciales");
                setTimeout(3, () -> {
                    jNotificacion.setText("");
                });
            }

        //} catch (DAOException ex) {
        } catch (Exception ex) {
            Logger.getLogger(TurnosIniciarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarTeclado() {
        jPanel2.setVisible(true);
    }

    private void desactivarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel2;
        teclado.habilitarAlfanumeric(false);
        teclado.habilitarPunto(false);
    }

    private void activarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel2;
        teclado.habilitarAlfanumeric(true);
        teclado.activarTeclasSoloPlaca(false);
        teclado.habilitarPorcentaje(false);
    }

    private void procesarEnvioTurno() {
        procesarEnvioTurno(this._persona, this.saldo, this.principal, this.personaPrincipal);
    }

    private void procesarEnvioTurno(final PersonaBean persona, final int saldo,
                                    final boolean principal, final PersonaBean personaPrincipal) {
        SwingWorker<JsonObject, Void> worker = new SwingWorker<JsonObject, Void>() {
            @Override
            protected JsonObject doInBackground() throws Exception {
                return enviarInicioTurno(persona, saldo, principal);
            }

            @Override
            protected void done() {
                try {
                    JsonObject jsonjornada = get();
                    if (jsonjornada != null
                            && jsonjornada.get("data") != null
                            && !jsonjornada.get("data").isJsonNull()
                            && jsonjornada.get("codigoError") == null) {

                        JornadaBean jornada = new JornadaBean();
                        jornada.setPersonaId(persona.getId());
                        if (principal) {
                            jornada.setLecturasIniciales(selectedTotalizers == null ?
                                    lsurtidores : mapToArrayList(selectedTotalizers));
                        } else {
                            jornada.setLecturasIniciales(new ArrayList<>());
                        }

                        JsonObject data = jsonjornada.get("data").getAsJsonObject();
                        if (data != null) {
                            jornada.setGrupoJornada(data.get("turno").getAsLong());
                            jornada.setSaldo(saldo);
                            jornada.setPrimerRegistro(personaPrincipal == null);
                            jornadaCreadaExito(jornada, persona);
                            TurnosIniciarViewController.fromRfid = false;
                            TurnosIniciarViewController.idPromotorTag = -1;
                            LecturasTanquesViewController.tanques = new ArrayList<>();

                            //pdao.eliminarLectura();
                            eliminarLecturaUseCase.execute();
                        } else {
                            mensajes("HA OCURRIDO UN ERROR TECNICO EN INICIO DE TURNO",
                                    "/com/firefuel/resources/btBad.png", true, 5000, false, true);
                        }
                    } else if (jsonjornada != null && jsonjornada.get("codigoError") != null
                            && jsonjornada.get("mensaje") != null) {
                        mensajes(jsonjornada.get("mensaje").getAsString(),
                                "/com/firefuel/resources/btBad.png", true, 5000, false, true);
                    } else if (jsonjornada != null && jsonjornada.get("codigoError") != null
                            && jsonjornada.get("mensajeError") != null) {
                        mensajes(jsonjornada.get("mensajeError").getAsString().toUpperCase(),
                                "/com/firefuel/resources/btBad.png", true, 5000, false, false);
                        juser.setText("");
                        jpassword.setText("");
                        jsaldo.setText("");
                    } else {
                        mensajes("FALLÓ LA CONEXION CON EL SERVICIO DE TURNOS",
                                "/com/firefuel/resources/btBad.png", true, 5000, false, true);
                        juser.setText("");
                        jpassword.setText("");
                        jsaldo.setText("");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        worker.execute();
    }


    JsonElement getSurtidoresArr(TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers) {
        int[] surtidores = new int[selectedTotalizers.size()];
        int i = 0;
        if (selectedTotalizers != null) {
            for (Map.Entry<Integer, ArrayList<Surtidor>> entry : selectedTotalizers.entrySet()) {
                surtidores[i] = entry.getKey();
                i++;
            }
        }
        JsonElement jsonTree = Main.gson.toJsonTree(surtidores);
        return jsonTree;
    }

    ArrayList<Surtidor> mapToArrayList(TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers) {
        ArrayList<Surtidor> lecturas = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Surtidor>> entry : selectedTotalizers.entrySet()) {
            for (Surtidor pump : entry.getValue()) {
                lecturas.add(pump);
            }
        }
        return lecturas;
    }

    private JsonObject enviarInicioTurno(PersonaBean persona, int saldo, boolean principal) {

        JsonObject response = null;
        JsonObject json = new JsonObject();
        json.addProperty("personas_id", persona.getId());
        json.addProperty("fecha_inicio", Main.SDFSQL.format(new Date()));
        json.addProperty("equipos_id", Main.credencial.getEquipos_id());
        json.addProperty("empresas_id", Main.credencial.getEmpresas_id());
        if (this.selectedTotalizers != null && !Main.SIN_SURTIDOR) {
            json.add("surtidores", getSurtidoresArr(this.selectedTotalizers));
        }

        JsonObject atributos = new JsonObject();
        atributos.addProperty("saldo", saldo);
        json.add("ajustePeriodico", null);
        atributos.add("totalizadoresIniciales", null);
        if (principal && !Main.SIN_SURTIDOR) {
            if (this.loadedTanks != null && !this.loadedTanks.isEmpty()) {
                JsonObject jsonAjustePeriodico = new JsonObject();
                JsonObject jsonEntrada = new JsonObject();
                JsonArray medidasArray = new JsonArray();
                jsonEntrada.addProperty("identificadorPromotor", persona.getId());
                jsonEntrada.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
                jsonEntrada.addProperty("identificacionEquipoLazo", Main.credencial.getEquipos_id());
                jsonEntrada.addProperty("fechaTransaccion", Main.SDFSQL.format(new Date()));
                for (BodegaBean tanque : this.loadedTanks) {
                    JsonObject tanqueJson = new JsonObject();
                    tanqueJson.addProperty("identificadorTanque", tanque.getId());
                    tanqueJson.addProperty("identificadorProducto", tanque.getProductos().get(0).getId());
                    tanqueJson.addProperty("identificadorFamiliaProducto", tanque.getProductos().get(0).getFamiliaId());
                    tanqueJson.addProperty("altura", tanque.getAltura_total());
                    tanqueJson.addProperty("agua", tanque.getAltura_agua());
                    tanqueJson.addProperty("galones", tanque.getGalonTanque());
                    medidasArray.add(tanqueJson);
                }
                jsonAjustePeriodico.add("entrada", jsonEntrada);
                jsonAjustePeriodico.add("medidasTanque", medidasArray);
                json.add("ajustePeriodico", jsonAjustePeriodico);
            }
            atributos.add("totalizadoresIniciales", NovusUtils.lecturasToJson(this.selectedTotalizers == null ? lsurtidores : mapToArrayList(this.selectedTotalizers)));
        }
        json.add("atributos", atributos);
        boolean ENABLE_DEBUG = true;
        int timeout = 30000;
        SurtidorDao sur = new SurtidorDao();
        try {
            int timeoutSurtidor = getTimeoutTotalizadoresUseCase.execute();
            timeout += timeoutSurtidor;
        } catch (Exception ex) {
            Logger.getLogger(TurnosIniciarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_INICIAR;
        ClientWSAsync async = new ClientWSAsync("INICIO JORNADA DE EMPLEADOS", url, NovusConstante.POST, json, ENABLE_DEBUG, timeout);
        try {
            response = async.esperaRespuesta();
            if (response == null) {
                response = async.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
    }

    private void jornadaCreadaExito(JornadaBean jornada, PersonaBean persona) {

        persona.setGrupoJornadaId(jornada.getGrupoJornada());
        
        // Actualizar Main.persona con la persona que acaba de iniciar turno
        Main.persona = persona;
        
        // Recargar la información de personas y actualizar la interfaz
        if (Main.info != null) {
            Main.info.recargarPersona();
        }
        
        // Imprimir inicio de turno usando el nuevo servicio independiente
        try {
            NovusUtils.printLn("========================================");
            NovusUtils.printLn("[INICIO TURNO] Iniciando proceso de impresión");
            NovusUtils.printLn("========================================");
            
            // Convertir long a int para posId (con validación) - debe ser final para lambda
            int posIdTemp = 1; // Default
            if (Main.credencial != null && Main.credencial.getEquipos_id() != null) {
                long equiposId = Main.credencial.getEquipos_id();
                if (equiposId > 0 && equiposId <= Integer.MAX_VALUE) {
                    posIdTemp = (int) equiposId;
                }
                NovusUtils.printLn("[INICIO TURNO] Equipos ID obtenido: " + equiposId + " -> POS ID: " + posIdTemp);
            } else {
                NovusUtils.printLn("[INICIO TURNO] WARNING: Main.credencial es null o no tiene equipos_id, usando POS ID por defecto: " + posIdTemp);
            }
            final int posId = posIdTemp; // Variable final para usar en lambda
            
            // Usar grupo_jornada como identificador_jornada (el servicio Python lo resolverá)
            final long identificadorJornada = jornada.getGrupoJornada();
            final long promotorId = persona.getId();
            
            NovusUtils.printLn("[INICIO TURNO] Parámetros de impresión:");
            NovusUtils.printLn("  - Identificador Jornada (grupo_jornada): " + identificadorJornada);
            NovusUtils.printLn("  - Identificador Promotor: " + promotorId);
            NovusUtils.printLn("  - POS ID: " + posId);
            NovusUtils.printLn("  - Nombre Promotor: " + persona.getNombre() + " " + (persona.getApellidos() != null ? persona.getApellidos() : ""));
            NovusUtils.printLn("  - Fecha Inicio Jornada: " + (jornada.getFechaInicial() != null ? jornada.getFechaInicial().toString() : "N/A"));
            
            // Ejecutar impresión en thread separado para no bloquear la UI
            NovusUtils.printLn("[INICIO TURNO] Creando thread para impresión asíncrona...");
            new Thread(() -> {
                try {
                    NovusUtils.printLn("[INICIO TURNO] Thread iniciado, creando Use Case...");
                    com.application.useCases.shiftReports.PrintInicioTurnoRemoteUseCase useCase = 
                        new com.application.useCases.shiftReports.PrintInicioTurnoRemoteUseCase(
                            identificadorJornada,
                            promotorId,
                            posId
                        );
                    
                    NovusUtils.printLn("[INICIO TURNO] Ejecutando Use Case...");
                    long startTime = System.currentTimeMillis();
                    com.domain.dto.shiftReports.ShiftReportResult result = useCase.execute(null);
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    NovusUtils.printLn("[INICIO TURNO] Use Case completado en " + duration + " ms");
                    
                    if (result.isSuccess()) {
                        NovusUtils.printLn("[INICIO TURNO] ✓ Ticket impreso exitosamente");
                        NovusUtils.printLn("[INICIO TURNO] Mensaje: " + result.getMessage());
                    } else {
                        NovusUtils.printLn("[INICIO TURNO] ✗ Error al imprimir: " + result.getMessage());
                    }
                    NovusUtils.printLn("========================================");
                } catch (Exception e) {
                    NovusUtils.printLn("[INICIO TURNO] ✗ ERROR INESPERADO: " + e.getMessage());
                    NovusUtils.printLn("[INICIO TURNO] Tipo de error: " + e.getClass().getName());
                    e.printStackTrace();
                    NovusUtils.printLn("========================================");
                }
            }).start();
            NovusUtils.printLn("[INICIO TURNO] Thread de impresión iniciado, continuando con flujo normal...");
        } catch (Exception e) {
            NovusUtils.printLn("[INICIO TURNO] ✗ ERROR al programar impresión: " + e.getMessage());
            NovusUtils.printLn("[INICIO TURNO] Tipo de error: " + e.getClass().getName());
            e.printStackTrace();
        }
        
        mensajes("<html><center>SE HA INICIADO JORNADA CORRECTAMENTE</center></html> ", RUTA_RECURSO.concat("btOk.png"), true, 5000, true, false);
        jsaldo.setText("");
        juser.setText("");
        jpassword.setText("");
    }

    private void mostrarTurnos() {
        try {
            Main.info.recargarPersona();
            ArrayList<PersonaBean> personas = new ArrayList<>();
            if (Main.persona != null && InfoViewController.turnosPersonas != null && !InfoViewController.turnosPersonas.isEmpty()) {
                personas = InfoViewController.turnosPersonas;
            }
            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();
            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
            for (PersonaBean list : personas) {
                defaultModel.addRow(new Object[]{list.getNombre(), list.getIdentificacion(),
                        sdf2.format(list.getFechaInicio())});
            }
        } catch (Exception ex) {
            Logger.getLogger(TurnosFinalizarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showMessage(String mensaje, String imagen,
                             boolean habilitar, Runnable runnable, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(imagen).setHabilitar(habilitar).setRunnable(runnable)
                .setLetterCase(letterCase).build();
        mostrarPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    private void mensajes(String mensaje, String imagen, boolean autocerrar, int time, boolean principal, boolean regresar) {
        if (principal) {
            this.handler = () -> {
                cerrar(true);
            };
        } else if (regresar) {
            this.handler = () -> {
                mostrarPanel(pnl_inicio_turno);
            };
        } else {
            this.handler = () -> {
                cerrar();
                juser.requestFocus();
            };
        }

        showMessage(mensaje, imagen, true, handler, LetterCase.FIRST_UPPER_CASE);

        int t = time / 1000;
        if (autocerrar) {
            setTimeout(t, () -> {
                if (autocerrar && principal) {
                    cerrar(true);
                } else if (autocerrar && regresar) {
                    mostrarPanel(pnl_inicio_turno);
                    juser.requestFocus();
                } else {
                    cerrar();
                }
            });

        }
    }

    private void mostrarPanel(JPanel panel) {
        jPanel1.removeAll();
        jPanel1.add(panel);
        jPanel1.revalidate();
        jPanel1.repaint();

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

    private void establecerAlertaRegistro(String valor) {
        EquipoDao.setParametro("registro_tag_sin_turno_silencio", valor, true);
    }

	private void establecerModoRegistro(String valor) {
		EquipoDao.setParametro("registro_tag_modo_registro", valor, true);
	}

}

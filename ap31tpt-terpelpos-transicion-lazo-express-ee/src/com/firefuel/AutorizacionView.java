package com.firefuel;

import com.application.useCases.persons.FindAdminUseCase;
import com.application.useCases.persons.GetAllJornadasUseCase;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.PersonasDao;
import com.dao.SurtidorDao;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import teclado.view.common.TecladoNumerico;

public class AutorizacionView extends javax.swing.JDialog {

    public static AutorizacionView instance = null;
    InfoViewController vistaPrincipal;
    JPanel panel_parent = null;
    JPanel panel_target = null;
    JFrame parent = null;
    JFrame target = null;
    JDialog dialog = null;
    JDialog dialog_target = null;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    ArrayList<Surtidor> lsurtidores = new ArrayList<>();
    DefaultTableCellRenderer textCenter;
    StoreConfirmarViewController store;
    MedioPagosConfirmarViewController medio;
    boolean consumoPropio;
    boolean FE;
    boolean medioPago = false;
    boolean concumoPropioCT = false;
    boolean pedirPromotores = true;
    private Runnable runnable;
    private Runnable salir;
    private boolean ventanaMenuVentamanual = false;
    private boolean activarBotonesContingencia = false;
    private boolean adminDefaultPos = true;

    public AutorizacionView(JFrame parent, JFrame target) {
        this.parent = parent;
        this.target = target;
        this.init();
    }

    public AutorizacionView(JFrame parent, boolean modal, JFrame target, boolean pedirPromotores, InfoViewController vistaPrincipal) {
        this.parent = parent;
        this.target = target;
        this.pedirPromotores = pedirPromotores;
        this.vistaPrincipal = vistaPrincipal;
        this.init();
    }

    public AutorizacionView(JFrame parent, boolean modal, JFrame target, boolean pedirPromotores, InfoViewController vistaPrincipal, boolean ventanaMenuVentamanual, boolean activarBotonesContingencia) {
        this.parent = parent;
        this.target = target;
        this.pedirPromotores = pedirPromotores;
        this.vistaPrincipal = vistaPrincipal;
        this.ventanaMenuVentamanual = ventanaMenuVentamanual;
        this.activarBotonesContingencia = activarBotonesContingencia;
        this.init();
    }

    public AutorizacionView(JFrame parent, JFrame target, boolean consumopropi) {
        this.parent = parent;
        this.target = target;
        this.concumoPropioCT = consumopropi;
        this.init();
    }

    public AutorizacionView(InfoViewController parent, JDialog dialog, boolean modal) {
        super(parent, modal);
        this.dialog_target = dialog;
        this.parent = parent;
        this.init();
    }

    public AutorizacionView(InfoViewController parent, boolean modal, JDialog dialog, boolean consumoPropipo, StoreConfirmarViewController store, boolean FE) {
        super(parent, modal);
        this.dialog_target = dialog;
        this.parent = parent;
        this.consumoPropio = consumoPropipo;
        this.store = store;
        this.FE = FE;
        this.init();
    }

    public AutorizacionView(InfoViewController parent, boolean modal, JDialog dialog, boolean consumoPropipo, MedioPagosConfirmarViewController medio, boolean mediopago) {
        super(parent, modal);
        this.dialog_target = dialog;
        this.parent = parent;
        this.consumoPropio = consumoPropipo;
        this.medio = medio;
        this.medioPago = mediopago;
        this.init();
    }

    public AutorizacionView(JFrame parent, boolean modal, JDialog target) {
        super(parent, modal);
        this.parent = parent;
        this.dialog_target = target;
        this.init();
    }

    public AutorizacionView(JFrame parent, boolean modal, JDialog target, boolean adminDefaultPos) {
        super(parent, modal);
        this.parent = parent;
        this.dialog_target = target;
        this.adminDefaultPos = adminDefaultPos;
        this.init();
    }

    public AutorizacionView(JFrame parent, boolean modal, JDialog target, Runnable runnable) {
        super(parent, modal);
        this.parent = parent;
        this.runnable = runnable;
        this.dialog_target = target;
        this.init();
    }

    public AutorizacionView(JFrame parent, Runnable runnable, boolean modal, Runnable salir) {
        super(parent, modal);
        this.parent = parent;
        this.salir = salir;
        this.runnable = runnable;
        this.init();
    }

    public AutorizacionView(JFrame parent, boolean modal, JPanel target) {
        super(parent, modal);
        this.parent = parent;
        this.panel_target = target;
        this.init();
    }

    public AutorizacionView(JPanel parent, JFrame target) {
        this.panel_parent = parent;
        this.target = target;
        this.init();
    }

    public AutorizacionView(JDialog dialog, boolean modal, JDialog target) {
        this.dialog = dialog;
        this.dialog_target = target;
        this.init();
    }

    private void init() {
        initComponents();

        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jPanel1.setVisible(false);

        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
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

        this.mostrarTurnos();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel28 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        juser = new javax.swing.JTextField();
        jpassword = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new TecladoNumerico()
        ;
        jLabel9 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jNotificacionAutenticacion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(90, 3, 10, 80);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("USUARIO");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(50, 140, 180, 20);

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setText("CONTRASEÑA");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(50, 250, 200, 20);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 10, 70, 71);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TURNO ACTUALES");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(70, 360, 580, 50);

        juser.setBackground(new java.awt.Color(186, 12, 47));
        juser.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
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
        getContentPane().add(juser);
        juser.setBounds(50, 175, 360, 45);

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
        getContentPane().add(jpassword);
        jpassword.setBounds(50, 280, 360, 45);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "PROMOTOR", "IDENTIFICACION", "F. INICIO"
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
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(49, 410, 616, 234);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(700, 170, 550, 470);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icon_lg_credenciales.png"))); // NOI18N
        getContentPane().add(jLabel9);
        jLabel9.setBounds(690, 170, 580, 470);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("VALIDAR ACCESO");
        getContentPane().add(jTitle);
        jTitle.setBounds(120, 15, 550, 50);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(10, 710, 100, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion);
        jNotificacion.setBounds(730, 10, 530, 70);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jNotificacionAutenticacion.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jNotificacionAutenticacion.setForeground(new java.awt.Color(255, 255, 255));
        jNotificacionAutenticacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jNotificacionAutenticacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jNotificacionAutenticacion);
        jNotificacionAutenticacion.setBounds(140, 710, 990, 80);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_autorizacion.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(1060, 120, 75, 22);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void juserKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_juserKeyTyped
        String caracteresAceptados = "[A-Z0-9a-z]";
        NovusUtils.limitarCarateres(evt, juser, 12, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_juserKeyTyped

    private void jpasswordKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpasswordKeyTyped
        String caracteresAceptados = "[A-Z0-9a-z]";
        NovusUtils.limitarCarateres(evt, jpassword, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jpasswordKeyTyped

    private void juserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_juserFocusGained
        NovusUtils.deshabilitarCopiarPegar(juser);
    }//GEN-LAST:event_juserFocusGained

    private void jpasswordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jpasswordFocusGained
        NovusUtils.deshabilitarCopiarPegar(jpassword);
    }//GEN-LAST:event_jpasswordFocusGained

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        guardar(false);
    }// GEN-LAST:event_jButton1ActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed
        NovusConstante.VENTAS_CONTINGENCIA = false;
    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        if (salir != null) {
            salir.run();
            dispose();
        } else {
            cerrar();
        }

    }// GEN-LAST:event_jLabel2MouseReleased

    private void juserActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_juserActionPerformed
        guardar(false);
    }// GEN-LAST:event_juserActionPerformed

    private void juserMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_juserMouseReleased
        mostrarTeclado();
    }// GEN-LAST:event_juserMouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        selectme();
    }// GEN-LAST:event_jTable1MouseClicked

    private void jLabel11MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel11MousePressed

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
    }// GEN-LAST:event_jLabel11MouseReleased

    private void jpasswordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jpasswordActionPerformed
        guardar(false);
    }// GEN-LAST:event_jpasswordActionPerformed

    private void jpasswordMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jpasswordMouseReleased
        mostrarTeclado();
    }// GEN-LAST:event_jpasswordMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacionAutenticacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    public static javax.swing.JPasswordField jpassword;
    public static javax.swing.JTextField juser;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        AutorizacionView.instance = null;
        if (runnable != null) {
            runnable.run();
        }
        dispose();
        if (this.vistaPrincipal != null) {
            this.vistaPrincipal.setVisible(true);
        }
        if (!pedirPromotores) {
            this.vistaPrincipal.abrirMenuVentas();
        }

    }

    private void cerrarVentana() {
        AutorizacionView.instance = null;
        if (runnable != null) {
            runnable.run();
        }
        dispose();
        if (this.vistaPrincipal != null && pedirPromotores) {
            this.vistaPrincipal.setVisible(true);
        }
    }

    private void guardar(boolean fromRFID) {
        SurtidorDao sDao = new SurtidorDao();

        String user = juser.getText();
        String pass = new String(jpassword.getPassword());
        if (user.isEmpty()) {
            juser.requestFocus();
        } else if (pass.isEmpty()) {
            jpassword.requestFocus();
        } else {
            try {
                //PersonasDao pdao = new PersonasDao();
                //int id = pdao.findAdmin(user, pass, adminDefaultPos);
                FindAdminUseCase findAdminUseCase = new FindAdminUseCase();
                int id = findAdminUseCase.execute(user, pass, adminDefaultPos);
                if (id == 5) {
                    Main.ADMIN = id;
                    if (parent != null) {
                        if (target != null) {
                            if (!InfoViewController.turnosPersonas.isEmpty()) {
                                if (InfoViewController.turnosPersonas.size() > 1 && pedirPromotores) {
                                    PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this, NovusConstante.SELECCION_PROMOTOR_FACT_MANUAL);
                                    seleccionpromotor.tipoVentaPos = false;
                                    seleccionpromotor.setVisible(true);
                                } else {
                                    if (!ventanaMenuVentamanual) {
                                        target.setVisible(true);
                                    } else if (activarBotonesContingencia) {
                                        vistaPrincipal.mostrarSubPanel(new VentaManualMenuPanel(vistaPrincipal, false, true));
                                    } else {
                                        vistaPrincipal.mostrarSubPanel(new VentaManualMenuPanel(vistaPrincipal));
                                    }

                                }
                            } else {
                                InfoViewController f = (InfoViewController) parent;
                                f.mostrarPanelSinTurnos();
                            }
                        } else if (dialog_target != null) {
                            if (this.medioPago) {
                                this.medio.ventaGuardada();
                            } else {
                                if (this.consumoPropio) {
                                    if (FE) {
                                        store.enviarFacturaElectronica();
                                    } else {
                                        InfoViewController f = (InfoViewController) parent;
                                        f.mostrarPanelMensaje("ESPERE UN MOMENTO...", "/com/firefuel/resources/loader_fac.gif");
                                        store.ventaGuardada();
                                        store.isConsumoPropio = false;
                                        dialog_target.setVisible(true);
                                        this.setVisible(false);
                                        dispose();
                                    }
                                } else {
                                    if (FE) {
                                        this.setVisible(false);
                                        store.enviarFacturaElectronica();
                                    } else {
                                        this.setVisible(false);
                                        dialog_target.setVisible(true);
                                        this.parent.setVisible(true);
                                    }
                                }
                            }
                        } else {
                            if (parent instanceof InfoViewController) {
                                ((InfoViewController) (parent)).mostrarSubPanel(panel_target);
                            } else {
                                parent.setVisible(true);
                            }
                        }
                    } else if (panel_parent != null) {
                        if (target != null) {
                            if (!InfoViewController.turnosPersonas.isEmpty()) {
                                if (InfoViewController.turnosPersonas.size() > 1) {
                                    PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this,
                                            NovusConstante.SELECCION_PROMOTOR_FACT_MANUAL);
                                    seleccionpromotor.tipoVentaPos = true;
                                    seleccionpromotor.setVisible(true);
                                } else {
                                    target.setVisible(true);
                                }
                            } else {
                                jNotificacionAutenticacion.setText("SIN TURNOS ABIERTOS");
                            }
                        }
                    }
                    cerrarVentana();
                } else {
                    jNotificacionAutenticacion.setText("ESTE USUARIO NO TIENE ACCESO AL MODULO");
                    if (this.consumoPropio) {
                        jNotificacionAutenticacion.setText("ESTE USUARIO NO TIENE ACCESO AL MODULO");
                    }
                    setTimeout(3, () -> {
                        resetLabel(jNotificacionAutenticacion);
                    });
                }

            } catch (Exception ex) {
                NovusUtils.printLn(ex.getMessage());
            }
        }
    }

    public void resetLabel(JLabel jlabel) {
        jlabel.setText("");
    }

    private void mostrarTeclado() {
        jLabel9.setVisible(false);
        jPanel1.setVisible(true);
    }

    private void mostrarTurnos() {
        try {
            Main.info.recargarPersona();
            ArrayList<PersonaBean> personas = new ArrayList<>();
            if (Main.persona != null && InfoViewController.turnosPersonas != null
                    && !InfoViewController.turnosPersonas.isEmpty()) {
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
            Logger.getLogger(TurnosFinalizarViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectme() {
        int r = jTable1.getSelectedRow();
        if (r > -1) {
            String identificacion;
            identificacion = (String) jTable1.getValueAt(r, 1);
            juser.setText(identificacion);
            jpassword.setText("");
        }
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

package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.persons.GetPrincipalTurnoUseCase;
import com.application.useCases.persons.GetTurnoUseCase;
import com.application.useCases.sutidores.GetTimeoutTotalizadoresUseCase;
import com.application.useCases.sutidores.ObtenerInfoSurtidoresEstacionUseCase;
import com.application.useCases.persons.*;
import com.bean.*;
import com.controllers.*;
import com.dao.*;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import teclado.view.common.TecladoNumerico;

public class TurnosFinalizarViewController extends javax.swing.JDialog {

    public GetTurnoUseCase getTurnoUseCase;
    public GetPrincipalTurnoUseCase getPrincipalTurnoUseCase;
    InfoViewController parent;
    public static TurnosFinalizarViewController instance;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    static ArrayList<Surtidor> lsurtidores = new ArrayList<>();
    DefaultTableCellRenderer textCenter;
    public ArrayList<BodegaBean> loadedTanks = null;
    FindPersonaUseCase findPersonaUseCase = new FindPersonaUseCase();
    IsIniciadaAndUltimoUseCase isIniciadaAndUltimoUseCase = new IsIniciadaAndUltimoUseCase();
    private static ObtenerInfoSurtidoresEstacionUseCase obtenerInfoSurtidoresEstacionUseCase = new ObtenerInfoSurtidoresEstacionUseCase();
    private static GetTimeoutTotalizadoresUseCase getTimeoutTotalizadoresUseCase = new GetTimeoutTotalizadoresUseCase();

    public TurnosFinalizarViewController(InfoViewController vistaPrincipal, boolean modal) {
        super(vistaPrincipal, modal);
        this.parent = vistaPrincipal;
        initComponents();
        this.init();

    }

    public static TurnosFinalizarViewController getInstance(InfoViewController vistaPrincipal, boolean modal) {
        if (TurnosFinalizarViewController.instance == null) {
            TurnosFinalizarViewController.instance = new TurnosFinalizarViewController(vistaPrincipal, modal);
        }
        return TurnosFinalizarViewController.instance;
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        this.getTurnoUseCase = new GetTurnoUseCase();
        jPanel1.setVisible(false);
        Main.LECTURAS_DISPONIBLE = false;
        jindicadorLecturas.setText("CONSULTANDO LECTURAS...");

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
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        SurtidorDao sDao = new SurtidorDao();
        boolean haySurtidor = sDao.haySurtidores();
        if (!haySurtidor) {
            TurnosFinalizarViewController.jindicadorLecturas.setVisible(false);
            Main.LECTURAS_DISPONIBLE = true;
        } else {
            TurnosFinalizarViewController.consultandoLecturas();
        }
        this.mostrarTurnos();
        TurnosFinalizarViewController.juser.requestFocus();
        mostrarTeclado();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        this.getTurnoUseCase = new GetTurnoUseCase();
        this.getPrincipalTurnoUseCase = new GetPrincipalTurnoUseCase();
        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
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
        jindicadorLecturas = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(80, 5, 10, 80);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("USUARIO");
        pnl_principal.add(jLabel6);
        jLabel6.setBounds(50, 140, 180, 20);

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setText("CONTRASEÑA");
        pnl_principal.add(jLabel5);
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
        pnl_principal.add(jLabel2);
        jLabel2.setBounds(10, 10, 70, 71);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TURNO ACTIVOS");
        pnl_principal.add(jLabel4);
        jLabel4.setBounds(70, 360, 580, 50);

        juser.setBackground(new java.awt.Color(186, 12, 47));
        juser.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        juser.setForeground(new java.awt.Color(255, 255, 255));
        juser.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        juser.setBorder(null);
        juser.setCaretColor(new java.awt.Color(255, 255, 0));
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
        pnl_principal.add(juser);
        juser.setBounds(50, 175, 360, 45);

        jpassword.setBackground(new java.awt.Color(186, 12, 47));
        jpassword.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jpassword.setForeground(new java.awt.Color(255, 255, 255));
        jpassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jpassword.setBorder(null);
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
        pnl_principal.add(jpassword);
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
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
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

        pnl_principal.add(jScrollPane1);
        jScrollPane1.setBounds(45, 410, 612, 233);
        pnl_principal.add(jPanel1);
        jPanel1.setBounds(700, 170, 550, 470);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icon_lg_credenciales.png"))); // NOI18N
        pnl_principal.add(jLabel9);
        jLabel9.setBounds(690, 170, 580, 470);

        jindicadorLecturas.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jindicadorLecturas.setForeground(new java.awt.Color(255, 255, 255));
        jindicadorLecturas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jindicadorLecturas.setText("ESPERE MIENTRAS SE CONSULTA LAS LECTURAS");
        jindicadorLecturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jindicadorLecturasMouseReleased(evt);
            }
        });
        pnl_principal.add(jindicadorLecturas);
        jindicadorLecturas.setBounds(100, 700, 930, 100);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CIERRE DE JORNADA");
        pnl_principal.add(jTitle);
        jTitle.setBounds(100, 0, 720, 90);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alerta.png"))); // NOI18N
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(0, 700, 80, 100);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(80, 713, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(670, 10, 590, 60);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_turnos_finalizar.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        pnl_principal.add(jButton1);
        jButton1.setBounds(1140, 100, 75, 22);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void juserKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_juserKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, juser, 15, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_juserKeyTyped

    private void jpasswordKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpasswordKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, jpassword, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jpasswordKeyTyped

    private void jindicadorLecturasMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jindicadorLecturasMouseReleased
        consultandoLecturas();
    }// GEN-LAST:event_jindicadorLecturasMouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        guardar(false);
    }// GEN-LAST:event_jButton1ActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed

    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        cerrar();
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
        consultandoLecturas();
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    public static javax.swing.JLabel jindicadorLecturas;
    public static javax.swing.JPasswordField jpassword;
    public static javax.swing.JTextField juser;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        this.parent.recargarPersona();
        TurnosFinalizarViewController.instance = null;
        this.setVisible(false);
        Main.info.limpiarPanelSubmenu();
        this.dispose();
    }

    private void guardar(boolean fromRFID) {

        SurtidorDao sDao = new SurtidorDao();
        boolean haySurtidor = sDao.haySurtidores();
        if ((Main.LECTURAS_DISPONIBLE && haySurtidor) || (!haySurtidor)) {
            String user = juser.getText();
            String prepwd = new String(jpassword.getPassword());
            int password = 0;
            if (!fromRFID) {
                if (!prepwd.trim().equals("")) {
                    password = Integer.parseInt(prepwd);
                }
            } else {
                password = -1;
            }
            if (user.isEmpty()) {
                juser.requestFocus();
            } else if (password == 0) {
                jpassword.requestFocus();
            } else {
                try {
                    PersonasDao pdao = new PersonasDao();
                   // final PersonaBean persona = pdao.findPersona(user, password, fromRFID);
                    final PersonaBean persona = findPersonaUseCase.execute(user, password, fromRFID);
                    if (persona != null) {
                        this.parent.recargarPersona();
                        //PersonaBean perTurno = pdao.getTurno(persona.getId());
                        PersonaBean perTurno = getTurnoUseCase.execute(persona.getId());
                        if (perTurno != null) {
                            juser.setText(persona.getIdentificacion());
                            jpassword.setText(persona.getPin() + "");
                            JornadaBean jornada;
                            //PersonaBean principal = pdao.getPrincipalTurno();
                            GetPrincipalTurnoUseCase getPrincipalTurnoUseCase = new GetPrincipalTurnoUseCase();
                            PersonaBean principal = getPrincipalTurnoUseCase.execute();

                            if (!Main.CENTRALIZADOR) {
                               // jornada = pdao.isIniciadaAndUltimo(persona, Main.credencial);
                                jornada = isIniciadaAndUltimoUseCase.execute(persona, Main.credencial);
                                jornada.setLecturasIniciales(lsurtidores);
                                persona.setPrincipal(principal.getId() == persona.getId());
                                persona.setGrupoJornadaId(principal.getGrupoJornadaId());
                                cierrePersonaJornada(persona, persona.isPrincipal());
                                ImprimirCierreView impri = new ImprimirCierreView(parent, true);
                                impri.promotor = persona;
                                impri.principal = persona.isPrincipal();
                                impri.imprimirCierre(persona.isPrincipal());
                                showDialog(impri);
                            } else {
                                ArrayList<PersonaBean> turnosCerrados = InfoViewController.turnosPersonas;
                                persona.setPrincipal(principal.getId() == persona.getId());
                                boolean cargarVistaTanques = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_MEDIDAS_TANQUES, true);
                                if (persona.isPrincipal()) {
                                    if (cargarVistaTanques) {
                                        LecturasTanquesViewController.getInstance(parent, true, new MyCallback() {
                                            @Override
                                            public void run(ArrayList<BodegaBean> data) {
                                                loadedTanks = data;
                                                cerrarJornada(loadedTanks != null && !loadedTanks.isEmpty(), persona, perTurno, turnosCerrados);
                                            }

                                            @Override
                                            public void run() {

                                            }

                                            @Override
                                            public void sendNotificacion(String mensaje) {
                                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                            }

                                        }).setVisible(true);
                                        return;
                                    }

                                }
                                cerrarJornada(true, persona, perTurno, turnosCerrados);
                            }
                        } else {
                            showMessage("ESTE PROMOTOR NO HA INICIADO TURNO",
                                    "/com/firefuel/resources/btBad.png", 
                                    true, this::mostrarMenuPrincipal,
                                    true, LetterCase.FIRST_UPPER_CASE);
                        }
                    } else {
                        showMessage("ERROR EN CREDENCIALES", 
                                "/com/firefuel/resources/btBad.png", 
                                true, this::mostrarMenuPrincipal,
                                true, LetterCase.FIRST_UPPER_CASE);
                    }
                //} catch (DAOException ex) {
                } catch (Exception ex) {
                    NovusUtils.printLn(ex.getMessage());
                }
            }
        } else {
            TurnosFinalizarViewController.consultandoLecturas();
            showMessage("SE ESTA REALIZANDO CONSULTA TOTALIZADORES", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::mostrarMenuPrincipal, 
                    true, LetterCase.FIRST_UPPER_CASE);
            jindicadorLecturas.setText("ESPERE MIENTRAS SE CONSULTA LAS LECTURAS");
        }
    }

    public void cerrarJornada(boolean tanquesNoRequeridoOvalido, PersonaBean persona, PersonaBean perTurno, ArrayList<PersonaBean> turnosCerrados) {
        if (tanquesNoRequeridoOvalido) {
            JsonObject jsonjornada = ejecutaLoginRemoto(persona, persona.isPrincipal());
            if (jsonjornada != null) {
                persona.setJornadaId(perTurno.getJornadaId());
                persona.setGrupoJornadaId(perTurno.getGrupoJornadaId());
                cierrePersonaJornada(persona, persona.isPrincipal());
                ImprimirCierreView impri = new ImprimirCierreView(parent, true);
                if (persona.isPrincipal()) {
                    for (PersonaBean promotor : turnosCerrados) {
                        promotor.setPrincipal(promotor.getId() == persona.getId());
                        impri.promotor = promotor;
                        impri.principal = promotor.isPrincipal();
                        impri.imprimirCierre(promotor.isPrincipal());
                    }
                } else {
                    impri.promotor = persona;
                    impri.principal = persona.isPrincipal();
                    impri.imprimirCierre(persona.isPrincipal());
                }
                showDialog(impri);
                this.cerrar();
            } else {
                showMessage("ERROR AL FINALIZAR REINTENTE",
                        "/com/firefuel/resources/btBad.png", 
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("NO SE PUEDE CERRAR TURNO PRINCIPAL SIN MEDIDAS DE TANQUES", 
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
        this.parent.recargarPersona();
    }

    private void cierrePersonaJornada(PersonaBean persona, boolean principal) {
        guardarCierre(persona, principal);
        Main.persona = null;
    }

    private void guardarCierre(PersonaBean persona, boolean principal) {
        try {
            FinalizarArchivarUseCase finalizarArchivarUseCase = new FinalizarArchivarUseCase();

           // PersonasDao pdao = new PersonasDao();
            JornadaBean jornada;
            if (principal) {
                for (PersonaBean per : InfoViewController.turnosPersonas) {
                    JornadaBean j = new JornadaBean();
                    j.setPersonaId(per.getId());
                    j.setFechaFinal(new Date());
                    j.setGrupoJornada(per.getGrupoJornadaId());
                   // pdao.finalizarArchivar(j);
                    finalizarArchivarUseCase.execute(j);
                }
            } else {
                jornada = new JornadaBean();
                jornada.setPersonaId(persona.getId());
                jornada.setFechaFinal(new Date());
                jornada.setGrupoJornada(persona.getGrupoJornadaId());
                //pdao.finalizarArchivar(jornada);
                finalizarArchivarUseCase.execute(jornada);
            }
        //} catch (DAOException ex) {
        } catch (Exception ex) {
            Logger.getLogger(TurnosFinalizarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarTeclado() {
        jPanel1.setVisible(true);
        jLabel9.setVisible(false);

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

    private static void consultandoLecturas() {
        Thread tarea = new Thread() {
            @Override
            public void run() {

                try {
                    jindicadorLecturas.setText("SOLICITANDO LECTURAS...");
                    lsurtidores.clear();
                    ControllerSync sysnc = new ControllerSync();
                    SurtidorDao sur = new SurtidorDao();

                    JsonArray infoSurtidoresEstacion = new JsonArray();
                    int timeoutSurtidor = 30000;
                    try {
                        timeoutSurtidor = getTimeoutTotalizadoresUseCase.execute();
                        infoSurtidoresEstacion = obtenerInfoSurtidoresEstacionUseCase.execute();
                    } catch (Exception ex) {
                        Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    boolean success = false;

                    for (JsonElement element : infoSurtidoresEstacion) {
                        JsonObject jsonSurtidor = element.getAsJsonObject();
                        String host = jsonSurtidor.get("host").getAsString();
                        JsonArray jsonArraySurtidores = jsonSurtidor.getAsJsonArray("surtidores");
                        for (JsonElement element2 : jsonArraySurtidores) {
                            long surtidor = element2.getAsJsonPrimitive().getAsLong();
                            JsonObject json = sysnc.lecturasSurtidor(surtidor, host, timeoutSurtidor);
                            if (json != null) {
                                actualizaSurtidores(json);
                                success = true;

                            } else {
                                success = false;
                                jindicadorLecturas.setText("ERROR EN LECTURAS TOTALIZADORES...");
                                break;
                            }
                        }
                    }
                    if (success) {
                        jindicadorLecturas.setText("LECTURAS OK");
                        Main.LECTURAS_DISPONIBLE = true;
                    } else {

                    }
                } catch (Exception e) {
                    Main.LECTURAS_DISPONIBLE = false;
                    jindicadorLecturas.setText("MANGUERAS DEBEN ESTAR COLGADAS PARA FINALIZAR");
                }
            }
        };
        tarea.start();
    }

    public static void actualizaSurtidores(JsonObject response) {
        JsonObject result = response;
        if (result != null) {
            for (JsonElement jelem : result.getAsJsonArray("data")) {

                Surtidor surb = new Surtidor();
                surb.setSurtidor(jelem.getAsJsonObject().get("surtidor").getAsInt());
                surb.setCara(jelem.getAsJsonObject().get("cara").getAsInt());
                surb.setManguera(jelem.getAsJsonObject().get("manguera").getAsInt());
                surb.setGrado(jelem.getAsJsonObject().get("grado").getAsInt());
                surb.setIsla(jelem.getAsJsonObject().get("isla").getAsInt());
                surb.setFamiliaIdentificador(jelem.getAsJsonObject().get("familiaIdentificador").getAsInt());
                surb.setFamiliaDescripcion(jelem.getAsJsonObject().get("familiaDescripcion").getAsString());
                surb.setProductoIdentificador(jelem.getAsJsonObject().get("productoIdentificador").getAsInt());
                surb.setProductoDescripcion(jelem.getAsJsonObject().get("productoDescripcion").getAsString());

                surb.setTotalizadorVolumen(jelem.getAsJsonObject().get("acumuladoVolumenReal").getAsLong());
                surb.setTotalizadorVenta(jelem.getAsJsonObject().get("acumuladoVenta").getAsLong());

                boolean agregar = true;

                for (Surtidor lsurtidore : lsurtidores) {
                    if (lsurtidore.getSurtidor() == surb.getSurtidor() && lsurtidore.getCara() == surb.getCara()
                            && lsurtidore.getManguera() == surb.getManguera()) {
                        agregar = false;
                    }
                }
                if (agregar) {
                    lsurtidores.add(surb);
                }

            }
            int cantidadSurtidores = 0;
            int surtidor = 0;
            for (Surtidor lsurtidore : lsurtidores) {
                if (surtidor != lsurtidore.getSurtidor()) {
                    cantidadSurtidores++;
                    surtidor = lsurtidore.getSurtidor();
                }
            }
        }
    }

    private JsonObject ejecutaLoginRemoto(PersonaBean persona, boolean principal) {
        JsonObject response = null;
        JsonArray jsonArray = new JsonArray();
        for (PersonaBean per : InfoViewController.turnosPersonas) {
            JsonObject persObj = new JsonObject();
            persObj.addProperty("personas_id", per.getId());
            persObj.addProperty("identificadorJornada", per.getGrupoJornadaId());
            persObj.addProperty("fecha_fin", Main.SDFSQL.format(new Date()));
            persObj.addProperty("equipos_id", Main.credencial.getEquipos_id());
            persObj.addProperty("empresas_id", Main.credencial.getEmpresas_id());
            JsonObject atributos = new JsonObject();
            atributos.addProperty("venta_total", 0);
            if (principal && (per.getId() == persona.getId())) {
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
                        tanqueJson.addProperty("identificadorFamiliaProducto",
                                tanque.getProductos().get(0).getFamiliaId());
                        tanqueJson.addProperty("altura", tanque.getAltura_total());
                        tanqueJson.addProperty("agua", tanque.getAltura_agua());
                        tanqueJson.addProperty("galones", tanque.getGalonTanque());
                        medidasArray.add(tanqueJson);
                    }
                    jsonAjustePeriodico.add("entrada", jsonEntrada);
                    jsonAjustePeriodico.add("medidasTanque", medidasArray);
                    persObj.add("ajustePeriodico", jsonAjustePeriodico);
                }
                atributos.add("totalizadoresFinales", NovusUtils.lecturasToJson(lsurtidores));
            }
            persObj.add("atributos", atributos);
            if (!principal && (per.getId() == persona.getId())) {
                jsonArray.add(persObj);
            } else {
                if (principal) {
                    jsonArray.add(persObj);
                }
            }

        }

        boolean ENABLE_DEBUG = true;
        String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_FIN;
        ClientWSAsync async = new ClientWSAsync("FINALIZAR JORNADAS", url, NovusConstante.PUT,
                jsonArray.toString() + "", ENABLE_DEBUG);
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
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
        CardLayout layout = (CardLayout) pnl_container.getLayout();
        pnl_container.add("pnl_ext", panel);
        layout.show(pnl_container, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnl_container.getLayout();
        layout.show(pnl_container, "pnl_principal");
    }

}

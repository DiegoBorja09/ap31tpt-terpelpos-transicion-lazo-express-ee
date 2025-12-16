package com.firefuel;

import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
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

public class SeleccionarPromotorViewController extends javax.swing.JDialog {

    InfoViewController parent;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    JDialog next = null;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    String view = "";
    JPanel panel = null;
    JDialog dialog = null;
    JFrame frame = null;
    public static SeleccionarPromotorViewController instance = null;
    ArrayList<PersonaBean> personas = new ArrayList<>();
    ArrayList<PersonaBean> personasCore;
    public static boolean promotorSeleccionado = false;

    public SeleccionarPromotorViewController(JFrame parent, boolean modal, JPanel panel) {
        super(parent, modal);
        this.parent = (InfoViewController) parent;
        this.panel = panel;
        initComponents();
        this.init();
    }

    public SeleccionarPromotorViewController(JFrame parent, boolean modal, JFrame frame) {
        super(parent, modal);
        this.parent = (InfoViewController) parent;
        this.frame = frame;
        initComponents();
        this.init();
    }

    public static SeleccionarPromotorViewController getInstance(JFrame parent, boolean modal, JPanel panel) {
        if (SeleccionarPromotorViewController.instance == null) {
            SeleccionarPromotorViewController.instance = new SeleccionarPromotorViewController(parent, modal, panel);
        } else {
            SeleccionarPromotorViewController.instance.panel = panel;
            SeleccionarPromotorViewController.instance.dialog = null;
            SeleccionarPromotorViewController.instance.frame = null;
        }
        SeleccionarPromotorViewController.promotorSeleccionado = false;
        return SeleccionarPromotorViewController.instance;
    }

    public static SeleccionarPromotorViewController getInstance(JFrame parent, boolean modal, JFrame frame) {
        if (SeleccionarPromotorViewController.instance == null) {
            SeleccionarPromotorViewController.instance = new SeleccionarPromotorViewController(parent, modal, frame);
        } else {
            SeleccionarPromotorViewController.instance.panel = null;
            SeleccionarPromotorViewController.instance.dialog = null;
            SeleccionarPromotorViewController.instance.frame = frame;
        }
        SeleccionarPromotorViewController.promotorSeleccionado = false;
        return SeleccionarPromotorViewController.instance;
    }

    public static SeleccionarPromotorViewController getInstance(JFrame parent, boolean modal, JDialog dialog) {
        if (SeleccionarPromotorViewController.instance == null) {
            SeleccionarPromotorViewController.instance = new SeleccionarPromotorViewController(parent, modal, dialog);
        } else {
            SeleccionarPromotorViewController.instance.panel = null;
            SeleccionarPromotorViewController.instance.dialog = dialog;
            SeleccionarPromotorViewController.instance.frame = null;
        }
        SeleccionarPromotorViewController.promotorSeleccionado = false;
        return SeleccionarPromotorViewController.instance;
    }

    public SeleccionarPromotorViewController(JFrame parent, boolean modal, JDialog dialog) {
        super(parent, modal);
        this.parent = (InfoViewController) parent;
        this.dialog = dialog;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

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

        mostrarTurnos();
    }

    public void setNext(JDialog next) {
        this.next = next;
    }

    public void setView(String view) {
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jescogerPromotor = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel17);
        jLabel17.setBounds(80, 0, 10, 80);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel18);
        jLabel18.setBounds(140, 710, 10, 80);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        getContentPane().add(jclock);
        jclock.setBounds(20, 720, 110, 60);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jescogerPromotor.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jescogerPromotor.setForeground(new java.awt.Color(255, 255, 255));
        jescogerPromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jescogerPromotor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jescogerPromotor.setText("ESCOGER");
        jescogerPromotor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jescogerPromotor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jescogerPromotorMouseReleased(evt);
            }
        });
        getContentPane().add(jescogerPromotor);
        jescogerPromotor.setBounds(1064, 720, 190, 60);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IDENTIFICACION", "NOMBRE", "FEC. INICIO TURNO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(30);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        }

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(140, 150, 1050, 460);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("ESCOGER PROMOTOR EN TURNO");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(100, 0, 720, 90);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/bg_back.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 85, 1280, 620);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        cerrar(null);
    }// GEN-LAST:event_jLabel3MousePressed

    private void jescogerPromotorMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jescogerPromotorMouseReleased
        select();
    }// GEN-LAST:event_jescogerPromotorMouseReleased

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseReleased
        int r = jTable1.getSelectedRow();
        if (r > -1) {
            jescogerPromotor.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        } else {
            jescogerPromotor.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));

        }
    }// GEN-LAST:event_jTable1MouseReleased

    private void cerrar(Boolean seleccionado) {
        SeleccionarPromotorViewController.promotorSeleccionado = seleccionado == null ? false : seleccionado;
        SeleccionarPromotorViewController.instance = null;
        this.setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel jclock;
    private javax.swing.JLabel jescogerPromotor;
    // End of variables declaration//GEN-END:variables

    private void mostrarTurnos() {
        try {
            Main.info.recargarPersona();
            ArrayList<PersonaBean> personas = new ArrayList<>();
            if (Main.persona != null && InfoViewController.turnosPersonas != null && InfoViewController.turnosPersonas.size() > 0) {
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

    private void select() {
        int r = jTable1.getSelectedRow();
        if (r > -1) {
            Main.persona = this.personas.get(r);
            cerrar(true);
        }
    }

}

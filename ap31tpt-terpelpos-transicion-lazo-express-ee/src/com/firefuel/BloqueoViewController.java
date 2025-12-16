package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.firefuel.utils.ShowMessageSingleton;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class BloqueoViewController extends javax.swing.JDialog {

    InfoViewController parent;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    DefaultTableCellRenderer textLeft;
    ArrayList<PersonaBean> personas;
    ArrayList<PersonaBean> personasCore;
    TreeMap<Long, Surtidor> caras = new TreeMap<>();

    private Runnable runnable;

    public BloqueoViewController(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"SURTIDOR", "CARA", "MANGUERA", "MOTIVO", "BLOQUEAR"}, 0) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Boolean.class;
                } else {
                    return Object.class;
                }
            }
        };
        jTable1.setModel(model);
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
        this.solicitarDatosSurtidor();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jNotificacion = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel2.setLayout(null);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        jPanel2.add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jLabel17.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-xsmall.png"))); // NOI18N
        jLabel17.setText("GUARDAR");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel17MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel17);
        jLabel17.setBounds(1050, 630, 130, 60);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                if(colIndex == 4){
                    return true;
                }
                return false;
            }
        };
        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SURTIDOR", "CARA", "MANGUERA", "MOTIVO", "BLOQUEAR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(30);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
        }

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(90, 140, 1090, 480);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jNotificacion);
        jNotificacion.setBounds(130, 710, 990, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel2.add(jLabel34);
        jLabel34.setBounds(10, 710, 100, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("BLOQUEO/DESBLOQUEO SURTIDOR");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(120, 15, 720, 50);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/bg_back.png"))); // NOI18N
        jPanel2.add(jLabel2);
        jLabel2.setBounds(0, 85, 1280, 618);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel31);
        jLabel31.setBounds(90, 10, 10, 68);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        jPanel2.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel2, "pnl_principal");
        jPanel2.getAccessibleContext().setAccessibleName("");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel17MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel17MouseReleased
        if (jLabel17.isEnabled()) {
            NovusUtils.beep();
            guardarBloqueos();
            showMessage("CAMBIOS APLICADOS EXITOSAMENTE", 
                    "/com/firefuel/resources/btOk.png", 
                    true, this::cerrar, 
                    true, LetterCase.FIRST_UPPER_CASE);
        }

    }// GEN-LAST:event_jLabel17MouseReleased

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel3MousePressed

    private void cerrar() {
        this.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    void guardarBloqueos() {
        TreeMap<Integer, Boolean> bloqueos = new TreeMap<>();
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        int rows = dm.getRowCount();
        int columns = dm.getColumnCount();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (j == columns - 1) {
                    bloqueos.put((Integer) dm.getValueAt(i, 2), (Boolean) dm.getValueAt(i, j));
                }
            }
        }
        SurtidorDao sdao = new SurtidorDao();
        sdao.bloqueosSurtidor(bloqueos);
        jLabel17.setEnabled(false);
    }

    void solicitarDatosSurtidor() {
        SetupDao sdao = new SetupDao();
        caras.clear();
        caras = sdao.getCaras();
        this.renderSurtidores();
    }

    void renderSurtidores() {

        DefaultTableModel dm = (DefaultTableModel) this.jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        jTable1.setAutoCreateRowSorter(true);

        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        this.caras.entrySet().forEach(surdetalle -> {
            try {
                defaultModel.addRow(new Object[]{
                    surdetalle.getValue().getSurtidor(),
                    surdetalle.getValue().getCara(),
                    surdetalle.getValue().getManguera(),
                    surdetalle.getValue().getMotivoBloqueo(),
                    surdetalle.getValue().isBloqueo()
                });
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
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

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

}

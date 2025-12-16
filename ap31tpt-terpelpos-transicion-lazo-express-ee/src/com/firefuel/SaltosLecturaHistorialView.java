package com.firefuel;

import com.bean.SaltosBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import java.awt.Color;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SaltosLecturaHistorialView extends JDialog {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    TreeMap<Long, Float> cache = new TreeMap<>();

    InfoViewController parent;
    ArrayList<SaltosBean> Saltos = new ArrayList<>();

    public SaltosLecturaHistorialView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        init();
    }

    private void init() {
        lbstatus.setText("");
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE));

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

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        this.actualizarVista();
    }


    void actualizarVista() {
        this.renderizarCambiosHistorial();
    }

           private void renderizarCambiosHistorial() {
        
           this.solicitarCambios();
           jTable1.setAutoCreateRowSorter(true);
           DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
           dm.getDataVector().removeAllElements();
           dm.fireTableDataChanged();
           DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
           for (SaltosBean saltos : this.Saltos) {
               
               String prueba="";
               
               switch(saltos.getMotivo()){
                   case 1:
                       prueba = "Falla Tecnica";
                       break;
                   case 2:
                       prueba = "Fuera Sistema";
                       break;
               }

            try {
                defaultModel.addRow(new Object[] { saltos.getId(), sdf.format(saltos.getFecha()) ,
                    /*producto.getMotivo()*/prueba, saltos.getCara(), saltos.getManguera(), saltos.getDescripcion(),
                    saltos.getPersona(), saltos.getSistema_acu_v(), saltos.getSurtidor_acu_v()
                        /* "$" + df.format(producto.getPrecio() + impuestoProducto) */
                });

            } catch (Exception e) {
                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        jTable1.setModel(defaultModel);
           
           
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lbstatus = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        lbstatus.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lbstatus.setForeground(new java.awt.Color(255, 255, 255));
        lbstatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbstatus.setText("CARGANDO HISTORIAL ...");
        getContentPane().add(lbstatus);
        lbstatus.setBounds(470, 730, 650, 50);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "FECHA", "MOTIVO", "CARA", "MANGUERA", "PRODUCTO", "RESPONSABLE", "SISTEMA VOLUMEN", "SURTIDOR VOLUMEN"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(55);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(12);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(12);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
        }

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(30, 110, 1220, 510);

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel1.setText("ACTUALIZAR");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        getContentPane().add(jLabel1);
        jLabel1.setBounds(1060, 630, 190, 60);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("HISTORIAL SALTOS LECTURA");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(100, 0, 720, 90);

        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(390, 420, 200, 16);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(0, 85, 1280, 618);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        getContentPane().add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        jLabel6.setText("CARGANDO ...");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(610, 750, 77, 16);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

// GEN-LAST:event_jCheckBox1ActionPerformed

    void toggleComboPromotor() {
//        jComboPromotor.setEnabled(jCheckBox1.isSelected());
    }

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        NovusUtils.beep();
    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        actualizarVista();
    }// GEN-LAST:event_jLabel1MousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        cerrar();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        seleccionar();
    }

    private void cerrar() {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lbstatus;
    // End of variables declaration//GEN-END:variables

    void seleccionar() {
       
    }
    
    void solicitarCambios(){
        Saltos.clear();
        MovimientosDao mdao = new MovimientosDao();
        
        try{
            
            Saltos = mdao.historialSaltos(12);
            
        }catch(DAOException | SQLException e){
            
            NovusUtils.printLn(e.getMessage());
            
        }
        
    }
}

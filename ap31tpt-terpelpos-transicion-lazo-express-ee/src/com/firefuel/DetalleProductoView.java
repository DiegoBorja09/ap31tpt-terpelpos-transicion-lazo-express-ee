package com.firefuel;

import com.application.useCases.productos.BuscarProductoPorPluKioskoUseCase;
import com.bean.ImpuestosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class DetalleProductoView extends javax.swing.JDialog {

    String plu = "";
    JFrame parent;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    DefaultTableCellRenderer textLeft;
    ArrayList<PersonaBean> personas;
    ArrayList<PersonaBean> personasCore;
    boolean kiosco = false;
    MovimientosDetallesBean producto;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    public DetalleProductoView(JFrame parent, boolean modal, String plu) {
        super(parent, modal);
        this.parent = parent;
        this.plu = plu;
        initComponents();
        this.init();
    }

    public DetalleProductoView(JFrame parent, boolean modal, String plu, boolean kiosco) {
        super(parent, modal);
        this.parent = parent;
        this.plu = plu;
        this.kiosco = kiosco;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textLeft = new DefaultTableCellRenderer();
        textLeft.setHorizontalAlignment(JLabel.LEFT);
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        jTable2.getColumnModel().getColumn(0).setCellRenderer(textLeft);
        jTable2.getColumnModel().getColumn(1).setCellRenderer(textLeft);
        jTable2.getColumnModel().getColumn(2).setCellRenderer(textLeft);

        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.setSelectionBackground(new Color(255, 182, 0));
        jTable2.setSelectionForeground(new Color(0, 0, 0));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable2.setFillsViewportHeight(true);
        // jTable1.setBackground(new Color(255, 255, 255));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jTable2.getModel().getColumnCount(); i++) {
            jTable2.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jTable2.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jTable2.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jTable2.setRowSorter(rowSorter);
        jTable2.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        this.renderizarDatosProducto();
    }

    void renderizarDatosProducto() {
        this.solicitarDatosProducto();
        if (this.producto != null) {
            jplu.setText(this.producto.getPlu());
            jprecio.setText("$" + df.format(this.producto.getPrecio()));
            jproducto.setText(this.producto.getDescripcion());
            jcategoria.setText(producto.getCategoriaDesc());
            jsaldo.setText(Math.round(producto.getSaldo()) + "");
            jTable2.setAutoCreateRowSorter(true);
            DefaultTableModel dm = (DefaultTableModel) jTable2.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();
            DefaultTableModel defaultModel = (DefaultTableModel) jTable2.getModel();
            for (ImpuestosBean impuesto : this.producto.getImpuestos()) {
                defaultModel.addRow(new Object[]{
                    impuesto.getDescripcion(),
                    impuesto.getValor(),
                    "$" + df.format(impuesto.getPorcentaje_valor().equals("%") ? (impuesto.getValor() / 100) * this.producto.getPrecio() : impuesto.getValor())
                });
            }
        }
    }

    float calcularValorBrutoProducto(float precio, ArrayList<ImpuestosBean> impuestos) {
        float precioBruto;
        float totalImpuestoProducto = 0;
        for (ImpuestosBean impuesto : impuestos) {
            if (impuesto.getPorcentaje_valor().equals("%")) {
                totalImpuestoProducto += ((impuesto.getValor() / 100) * precio);
            } else {
                totalImpuestoProducto += impuesto.getValor();
            }
        }
        precioBruto = precio - totalImpuestoProducto;
        return precioBruto;
    }

    void solicitarDatosProducto() {
        MovimientosDao mdao = new MovimientosDao();
        try {
            if (!kiosco) {
                this.producto = mdao.findByPlu(this.plu);
            } else {
                //this.producto = mdao.findByPluKIOSCO(this.plu);
                this.producto = new BuscarProductoPorPluKioskoUseCase(this.plu).execute();
            }
        } catch (DAOException ex) {
            Logger.getLogger(DetalleProductoView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jprecio = new javax.swing.JLabel();
        jcategoria = new javax.swing.JLabel();
        jproducto = new javax.swing.JLabel();
        jplu = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jsaldo = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jScrollPane2.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable2.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IMPUESTO", "%/$", "VALOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setRowHeight(30);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(300);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setPreferredWidth(100);
        }

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(370, 450, 870, 220);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        jLabel2.setText("CATEGORIA");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(60, 230, 200, 50);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setText("PLU");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(60, 110, 200, 40);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("PRECIO");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(60, 290, 200, 50);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(186, 12, 47));
        jLabel7.setText("PRODUCTO");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(60, 170, 200, 50);

        jprecio.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jprecio.setText("$0");
        getContentPane().add(jprecio);
        jprecio.setBounds(300, 290, 550, 40);

        jcategoria.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jcategoria.setText("N/A");
        getContentPane().add(jcategoria);
        jcategoria.setBounds(300, 230, 550, 40);

        jproducto.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jproducto.setText("-------------------");
        getContentPane().add(jproducto);
        jproducto.setBounds(300, 170, 940, 40);

        jplu.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jplu.setText("0");
        getContentPane().add(jplu);
        jplu.setBounds(300, 110, 550, 40);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("STOCK");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(50, 460, 210, 40);

        jsaldo.setFont(new java.awt.Font("Tahoma", 0, 56)); // NOI18N
        jsaldo.setForeground(new java.awt.Color(102, 102, 102));
        jsaldo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsaldo.setText("0");
        getContentPane().add(jsaldo);
        jsaldo.setBounds(70, 520, 180, 130);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("DETALLES PRODUCTO");
        getContentPane().add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel30);
        jLabel30.setBounds(90, 10, 10, 68);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1281, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked

    }//GEN-LAST:event_jTable2MouseClicked

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        cerrar();
    }// GEN-LAST:event_jLabel3MousePressed

    private void cerrar() {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jcategoria;
    private javax.swing.JLabel jplu;
    private javax.swing.JLabel jprecio;
    private javax.swing.JLabel jproducto;
    private javax.swing.JLabel jsaldo;
    // End of variables declaration//GEN-END:variables

}

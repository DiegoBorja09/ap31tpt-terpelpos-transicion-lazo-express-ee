package com.firefuel;

import com.WT2.commons.domain.entity.beans.InformacionFidelizacionRetenida;
import com.WT2.commons.domain.entity.beans.InformacionVentaFidelizacionRetenida;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.event.HierarchyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class FidelizacionesRetenidasView extends javax.swing.JDialog {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;

    MovimientosDao mdao = new MovimientosDao();

    boolean isValid;
    long numero = 0L;
    String fecha = "";
    String cantidad = "";
    Long total = 0L;
    long id = 0L;
    String negocio = "";

    JsonObject request = new JsonObject();
    List<InformacionVentaFidelizacionRetenida> infoVenta;
    List<InformacionFidelizacionRetenida> infoFidelizacion;

    public FidelizacionesRetenidasView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(SwingConstants.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(SwingConstants.CENTER);

        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
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

        renderizarCambios();
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_principal = new javax.swing.JPanel();
        panel_lista = new javax.swing.JPanel();
        btnActualizar = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jclock = ClockViewController.getInstance();
        btnAtras = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        panel_principal.setLayout(new java.awt.CardLayout());

        panel_lista.setLayout(null);

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btnActualizar.setText("ACTUALIZAR");
        btnActualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnActualizarMouseClicked(evt);
            }
        });
        panel_lista.add(btnActualizar);
        btnActualizar.setBounds(1070, 640, 180, 54);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "FECHA", "NEGOCIO", "CANTIDAD", "VALOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(35);
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

        panel_lista.add(jScrollPane2);
        jScrollPane2.setBounds(30, 110, 1220, 510);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        panel_lista.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        panel_lista.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setLayout(null);
        panel_lista.add(jclock);
        jclock.setBounds(1150, 720, 110, 60);

        btnAtras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAtras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        btnAtras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAtrasMouseReleased(evt);
            }
        });
        panel_lista.add(btnAtras);
        btnAtras.setBounds(10, 10, 70, 71);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        panel_lista.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("FIDELIZACIONES RETENIDAS");
        panel_lista.add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        panel_lista.add(jLabel30);
        jLabel30.setBounds(90, 10, 10, 68);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        panel_lista.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        panel_principal.add(panel_lista, "panel_lista");

        getContentPane().add(panel_principal);
        panel_principal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAtrasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAtrasMouseReleased
        cerrar();
    }//GEN-LAST:event_btnAtrasMouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            NovusUtils.beep();
            id = (long) jTable1.getValueAt(r, 0);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnActualizarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnActualizarMouseClicked
        renderizarCambios();
    }//GEN-LAST:event_btnActualizarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnActualizar;
    private javax.swing.JLabel btnAtras;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    private ClockViewController jclock;
    private javax.swing.JPanel panel_lista;
    private javax.swing.JPanel panel_principal;
    // End of variables declaration//GEN-END:variables

    private void renderizarCambios() {
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();

        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        infoFidelizacion = mdao.getInformacionFidelizacion();

        for (InformacionFidelizacionRetenida next : infoFidelizacion) {
            numero = next.getIdMovimiento();
            fecha = next.getFechaTransaccion();
            total = next.getPagoTotal();
            infoVenta = next.getProductos();
            negocio = next.getDescripcionNegocio();

            double cantidadProducto = 0d;

            String cantidadTipo = next.getOrigenVenta().equals("EDS") ? " GL" : "";

            for (InformacionVentaFidelizacionRetenida infoVentaRetenida : infoVenta) {
                cantidadProducto += infoVentaRetenida.getCantidadProducto();
            }

            cantidad = cantidadProducto + cantidadTipo;

            defaultModel.addRow(new Object[]{numero, fecha, negocio, cantidad.replace(".", ","), "$ " + df.format(total)});
        }
    }

    private void cerrar() {
        dispose();
    }

}

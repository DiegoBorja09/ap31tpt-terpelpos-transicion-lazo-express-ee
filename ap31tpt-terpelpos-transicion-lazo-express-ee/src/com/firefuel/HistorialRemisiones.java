package com.firefuel;

import com.application.useCases.entradaCombustible.ObtenerHistorialRemisionesUseCase;
import com.bean.entradaCombustible.EntradaCombustibleHistorialBean;
import com.controllers.NovusUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import com.firefuel.Main;

public class HistorialRemisiones extends javax.swing.JPanel {

    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    Runnable volver;

    ObtenerHistorialRemisionesUseCase useCase;

    public HistorialRemisiones(Runnable volver) {
        this.volver = volver;
        initComponents();
        init();
    }

    public void init() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE));

        jComboMostrar.setBackground(Color.WHITE);
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        jTableHistorialRemisiones.setSelectionBackground(new Color(255, 182, 0));
        jTableHistorialRemisiones.setSelectionForeground(new Color(0, 0, 0));
        jTableHistorialRemisiones.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTableHistorialRemisiones.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTableHistorialRemisiones.getModel().getColumnCount(); i++) {
            jTableHistorialRemisiones.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jTableHistorialRemisiones.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jTableHistorialRemisiones.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jTableHistorialRemisiones.setRowSorter(rowSorter);

        jscrollRegistros.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jscrollRegistros.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jscrollRegistros.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        renderizarHistorial();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel31 = new javax.swing.JLabel();
        btnBack = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        jscrollRegistros = new javax.swing.JScrollPane();
        jTableHistorialRemisiones = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JLabel();
        logoDevitech = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblMostrar = new javax.swing.JLabel();
        jComboMostrar = new javax.swing.JComboBox<>();
        fnd = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setOpaque(false);
        setLayout(null);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        btnBack.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnBackMouseReleased(evt);
            }
        });
        add(btnBack);
        btnBack.setBounds(20, 20, 48, 49);

        lblTitle.setFont(new java.awt.Font("Terpel Sans", 1, 34)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("HISTORIAL REMISIONES");
        add(lblTitle);
        lblTitle.setBounds(100, 0, 720, 90);

        jscrollRegistros.setBorder(null);

        jTableHistorialRemisiones.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTableHistorialRemisiones.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTableHistorialRemisiones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "REMISION", "PRODUCTO", "CANTIDAD", "FECHA REGISTRO", "FECHA ACTUALIZACIÓN", "ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableHistorialRemisiones.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTableHistorialRemisiones.setRowHeight(55);
        jTableHistorialRemisiones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableHistorialRemisiones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableHistorialRemisiones.setShowGrid(true);
        jTableHistorialRemisiones.setShowHorizontalLines(false);
        jTableHistorialRemisiones.setShowVerticalLines(false);
        jTableHistorialRemisiones.getTableHeader().setResizingAllowed(false);
        jTableHistorialRemisiones.getTableHeader().setReorderingAllowed(false);
        jscrollRegistros.setViewportView(jTableHistorialRemisiones);

        add(jscrollRegistros);
        jscrollRegistros.setBounds(30, 110, 1220, 510);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel33);
        jLabel33.setBounds(1180, 3, 10, 80);

        btnActualizar.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-rojo-1.png"))); // NOI18N
        btnActualizar.setText("ACTUALIZAR");
        btnActualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnActualizarMousePressed(evt);
            }
        });
        add(btnActualizar);
        btnActualizar.setBounds(1060, 630, 190, 60);

        logoDevitech.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoDevitech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        add(logoDevitech);
        logoDevitech.setBounds(10, 710, 100, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        lblMostrar.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        lblMostrar.setForeground(new java.awt.Color(153, 0, 0));
        lblMostrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMostrar.setText("MOSTRAR");
        add(lblMostrar);
        lblMostrar.setBounds(770, 640, 130, 40);

        jComboMostrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboMostrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20", "60", "120", "200" }));
        jComboMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboMostrarActionPerformed(evt);
            }
        });
        add(jComboMostrar);
        jComboMostrar.setBounds(920, 634, 110, 52);

        fnd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(fnd);
        fnd.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackMouseReleased
        volver();
    }//GEN-LAST:event_btnBackMouseReleased

    private void btnActualizarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnActualizarMousePressed
        renderizarHistorial();
    }//GEN-LAST:event_btnActualizarMousePressed

    private void jComboMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboMostrarActionPerformed
        NovusUtils.beep();
        setTimeout(1, () -> renderizarHistorial());
    }//GEN-LAST:event_jComboMostrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnActualizar;
    private javax.swing.JLabel btnBack;
    private javax.swing.JLabel fnd;
    private javax.swing.JComboBox<String> jComboMostrar;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JTable jTableHistorialRemisiones;
    private javax.swing.JScrollPane jscrollRegistros;
    private javax.swing.JLabel lblMostrar;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel logoDevitech;
    // End of variables declaration//GEN-END:variables

    public void renderizarHistorial() {
        DefaultTableModel dm = (DefaultTableModel) jTableHistorialRemisiones.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTableHistorialRemisiones.getModel();

        int limiteRegistros = Integer.parseInt(jComboMostrar.getSelectedItem().toString().trim());
        useCase = new ObtenerHistorialRemisionesUseCase();
        JsonArray infoHistorial = useCase.execute((long) limiteRegistros);
        for (JsonElement jsonElement : infoHistorial) {
            EntradaCombustibleHistorialBean entradaHistorial = Main.gson.fromJson(jsonElement, EntradaCombustibleHistorialBean.class);

            String product = entradaHistorial.getProduct()
                    .replace("GASOLINA ", "")
                    .replace("OXIGENADA", "");

            String cantidad = entradaHistorial.getQuantity() + " " + entradaHistorial.getUnit();

            defaultModel.addRow(new Object[]{
                entradaHistorial.getDelivery(),
                product,
                cantidad.trim(),
                entradaHistorial.getCreationDate() + " " + entradaHistorial.getCreationHour(),
                entradaHistorial.getModificationDate() + " " + entradaHistorial.getModificationHour(),
                entradaHistorial.getStatus()
            });
        }
    }

    public void volver() {
        if (volver != null) {
            volver.run();
            volver = null;
        }
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static Border createCustomBorder(Color color) {
        int thickness = 2;
        int radius = 6;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(radius, radius, radius, radius)
        );
    }

}

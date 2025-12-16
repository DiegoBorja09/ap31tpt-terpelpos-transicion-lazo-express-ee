package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.event.HierarchyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class NotificacionLogs extends javax.swing.JDialog {

    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    MovimientosDao mdao = new MovimientosDao();
    JsonArray infoLogs = new JsonArray();
    String fecha = "";
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    TreeMap<Integer, String> tipoNotificacion = new TreeMap<>();
    int idNotificacion = 0;

    public NotificacionLogs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
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

        //Calendar
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        rSDateChooser1.setDatoFecha(ca.getTime());
        rSDateChooser2.setDatoFecha(new Date());

        renderizarCambios();
        getInfo();
//        fecha = sdf.format(ca.getTime());
//        System.out.println("Fecha :"+fecha+">>>>>>>>");
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });
    }

    public void getInfo() {
        tipoNotificacion = mdao.getTipoNotificaciones();
        listarNotificaciones();
    }

    @SuppressWarnings("unchecked")
    public void listarNotificaciones() {
        jComboNotificaciones.removeAllItems();
        tipoNotificacion.put(0, "TODOS");
        for (Map.Entry<Integer, String> entry : tipoNotificacion.entrySet()) {
            String nombreTanque = entry.getValue();
            jComboNotificaciones.addItem(nombreTanque + "");
        }
    }

    public void getIdNotificacion() {
        String nombreMovimiento = jComboNotificaciones.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : tipoNotificacion.entrySet()) {
            if (entry.getValue().equals(nombreMovimiento)) {
                idNotificacion = entry.getKey();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_principal = new javax.swing.JPanel();
        panel_lista = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboNotificaciones = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        rSDateChooser1 = new rojeru_san.componentes.RSDateChooser();
        rSDateChooser2 = new rojeru_san.componentes.RSDateChooser();
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

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setText("TIPO NOTIFICACION:");
        panel_lista.add(jLabel6);
        jLabel6.setBounds(30, 90, 190, 30);

        jComboNotificaciones.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jComboNotificaciones.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0)));
        jComboNotificaciones.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboNotificacionesItemStateChanged(evt);
            }
        });
        jComboNotificaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboNotificacionesActionPerformed(evt);
            }
        });
        panel_lista.add(jComboNotificaciones);
        jComboNotificaciones.setBounds(30, 120, 360, 40);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 0, 0));
        jLabel2.setText("FECHA:");
        panel_lista.add(jLabel2);
        jLabel2.setBounds(420, 90, 150, 30);

        rSDateChooser1.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setEnabled(false);
        rSDateChooser1.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser1.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser1.setFuente(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser1.setPlaceholder("Fecha Inicial");
        panel_lista.add(rSDateChooser1);
        rSDateChooser1.setBounds(420, 120, 210, 40);

        rSDateChooser2.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser2.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser2.setFuente(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser2.setPlaceholder("Fecha Final");
        panel_lista.add(rSDateChooser2);
        rSDateChooser2.setBounds(650, 120, 210, 40);

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
        btnActualizar.setBounds(890, 110, 180, 54);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID NOTIFICACION", "TIPO NOTIFICACION", "LOGGER", "FECHA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(5);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(280);
        }

        panel_lista.add(jScrollPane2);
        jScrollPane2.setBounds(30, 180, 1220, 510);

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
        jTitle.setText("LOGS SINCRONIZACION");
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

    private void btnActualizarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnActualizarMouseClicked
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        NovusUtils.beep();
        rSDateChooser1.getDatoFecha();
        rSDateChooser2.getDatoFecha();
        try {
            renderizarCambios();
        } catch (Exception ex) {
            Logger.getLogger(ReporteInventarioKardex.class.getName()).log(Level.SEVERE, null, ex);
        }
        //mdao.insertLogsNotificaciones(1, 1, "Prueba Insert", fecha);
    }//GEN-LAST:event_btnActualizarMouseClicked

    private void btnAtrasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAtrasMouseReleased
        cerrar();
    }//GEN-LAST:event_btnAtrasMouseReleased

    private void jComboNotificacionesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboNotificacionesItemStateChanged
        getIdNotificacion();
    }//GEN-LAST:event_jComboNotificacionesItemStateChanged

    private void jComboNotificacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboNotificacionesActionPerformed

    }//GEN-LAST:event_jComboNotificacionesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnActualizar;
    private javax.swing.JLabel btnAtras;
    private javax.swing.JComboBox jComboNotificaciones;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    private ClockViewController jclock;
    private javax.swing.JPanel panel_lista;
    private javax.swing.JPanel panel_principal;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser2;
    // End of variables declaration//GEN-END:variables

    private void renderizarCambios() {
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();

        String fechaInicial = sdf.format(rSDateChooser1.getDatoFecha());
        fechaInicial = fechaInicial.concat(" 00:00:00");
        String fechaFinal = sdf.format(rSDateChooser2.getDatoFecha());
        fechaFinal = fechaFinal.concat(" 23:59:59");
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        infoLogs = mdao.getNotificaciones(idNotificacion, fechaInicial, fechaFinal);

        if (infoLogs != null) {
            for (JsonElement infoLog : infoLogs) {
                JsonObject json = infoLog.getAsJsonObject();
                String tNotificacion = json.get("descripcion") != null ? json.get("descripcion").getAsString() : "";
                int idNotif = json.get("id_notificaciones") != null ? json.get("id_notificaciones").getAsInt() : 0;
                String logger = json.get("logger") != null ? json.get("logger").getAsString() : "";
                String fechaRegistro = json.get("fecha") != null ? json.get("fecha").getAsString() : "";

                defaultModel.addRow(new Object[]{idNotif, tNotificacion, logger, fechaRegistro});
            }
        } else {
            NovusUtils.printLn("No Hay Informacion de Logs");
        }

    }

    private void cerrar() {
        dispose();
    }
}

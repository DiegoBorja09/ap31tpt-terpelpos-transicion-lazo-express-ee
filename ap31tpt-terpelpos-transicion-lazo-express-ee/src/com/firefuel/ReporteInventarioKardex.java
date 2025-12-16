package com.firefuel;

import com.application.useCases.movimientos.GetTiposMovimientoUseCase;
import com.application.useCases.tanques.GetTanquesUseCase;
import com.bean.BodegaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import java.util.LinkedList;

public class ReporteInventarioKardex extends javax.swing.JDialog {

    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;

    MovimientosDao mdao = new MovimientosDao();

    EquipoDao edao = new EquipoDao();

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);    

    TreeMap<Integer, String> bodega = new TreeMap<>();
    TreeMap<Integer, String> movimientos = new TreeMap<>();

    String host = edao.posPrincipal();

    JsonArray infoInventarioTeorico = new JsonArray();
    float cantidad;
    float valor;
    float lInicial;
    float lFinal;
    String producto = "";
    String tanque = "";
    String movimiento = "";
    int length = 1000;

    //ID
    int idMovimiento = 0;
    int idTanque = 0;

    public ReporteInventarioKardex(java.awt.Frame parent, boolean modal) throws SQLException {
        super(parent, modal);
        initComponents();
        init();
    }

    private void init() throws SQLException {

        lbstatus.setVisible(false);

        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(SwingConstants.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(SwingConstants.CENTER);

        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N

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
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        rSDateChooser1.setDatoFecha(ca.getTime());
        rSDateChooser2.setDatoFecha(new Date());

        renderizarCambios();
        getInfo();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        rSDateChooser1 = new rojeru_san.componentes.RSDateChooser();
        rSDateChooser2 = new rojeru_san.componentes.RSDateChooser();
        jComboMovimiento = new javax.swing.JComboBox();
        jComboTanques = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jMostrar = new javax.swing.JLabel();
        jComboMostrar = new javax.swing.JComboBox<>();
        lbstatus = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel30 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(null);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 0, 0));
        jLabel2.setText("FECHA:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(590, 100, 150, 30);

        rSDateChooser1.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setEnabled(false);
        rSDateChooser1.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser1.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser1.setFuente(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser1.setPlaceholder("Fecha Inicial");
        getContentPane().add(rSDateChooser1);
        rSDateChooser1.setBounds(590, 130, 210, 40);

        rSDateChooser2.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser2.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser2.setFuente(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        rSDateChooser2.setPlaceholder("Fecha Final");
        getContentPane().add(rSDateChooser2);
        rSDateChooser2.setBounds(820, 130, 210, 40);

        jComboMovimiento.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jComboMovimiento.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0)));
        jComboMovimiento.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboMovimientoItemStateChanged(evt);
            }
        });
        jComboMovimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboMovimientoActionPerformed(evt);
            }
        });
        getContentPane().add(jComboMovimiento);
        jComboMovimiento.setBounds(10, 130, 320, 40);

        jComboTanques.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jComboTanques.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0)));
        jComboTanques.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboTanquesItemStateChanged(evt);
            }
        });
        jComboTanques.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboTanquesActionPerformed(evt);
            }
        });
        getContentPane().add(jComboTanques);
        jComboTanques.setBounds(350, 130, 210, 40);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setText("TIPO MOVIMIENTO:");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(10, 100, 190, 30);

        jMostrar.setFont(new java.awt.Font("Conthrax", 1, 18)); // NOI18N
        jMostrar.setForeground(new java.awt.Color(153, 0, 0));
        jMostrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jMostrar.setText("MOSTRAR:");
        getContentPane().add(jMostrar);
        jMostrar.setBounds(1050, 630, 110, 40);

        jComboMostrar.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jComboMostrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20", "60", "120", "200", "Todos" }));
        jComboMostrar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0)));
        jComboMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboMostrarActionPerformed(evt);
            }
        });
        getContentPane().add(jComboMostrar);
        jComboMostrar.setBounds(1160, 630, 110, 40);

        lbstatus.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lbstatus.setForeground(new java.awt.Color(255, 255, 255));
        lbstatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbstatus.setText("CARGANDO INVENTARIO ...");
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
                "MOVIMIENTO", "FECHA", "TANQUE", "FAMILIA", "PRODUCTO", "CANTIDAD", "VALOR", "LECTURA INICIAL", "LECTURA FINAL"
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(35);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(45);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(25);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(25);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(32);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(32);
        }

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(10, 190, 1260, 430);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel30);
        jLabel30.setBounds(1130, 0, 10, 80);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 0));
        jLabel8.setText("TANQUE:");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(350, 100, 150, 30);

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-xsmallb.png"))); // NOI18N
        jLabel1.setText("ACTUALIZAR");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        getContentPane().add(jLabel1);
        jLabel1.setBounds(1050, 120, 140, 60);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("REPORTE KARDEX");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(100, 0, 720, 90);

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

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

    }//GEN-LAST:event_jTable1MouseClicked

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        NovusUtils.beep();
        rSDateChooser1.getDatoFecha();
        rSDateChooser2.getDatoFecha();
        try {
            renderizarCambios();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteInventarioKardex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jLabel1MousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }//GEN-LAST:event_jLabel7MouseReleased

    private void jComboMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboMostrarActionPerformed
        NovusUtils.beep();
        try {
            renderizarCambios();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteInventarioKardex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboMostrarActionPerformed

    private void jComboMovimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboMovimientoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboMovimientoActionPerformed

    private void jComboTanquesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboTanquesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboTanquesActionPerformed

    private void jComboMovimientoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboMovimientoItemStateChanged
        getIdMovimiento();
    }//GEN-LAST:event_jComboMovimientoItemStateChanged

    private void jComboTanquesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboTanquesItemStateChanged
        getIdTanque();
    }//GEN-LAST:event_jComboTanquesItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboMostrar;
    private javax.swing.JComboBox jComboMovimiento;
    private javax.swing.JComboBox jComboTanques;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jMostrar;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lbstatus;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser2;
    // End of variables declaration//GEN-END:variables

    private void renderizarCambios() throws SQLException {

        try {
            length = Integer.parseInt(jComboMostrar.getSelectedItem().toString().trim());
        } catch (NumberFormatException ne) {
            length = 1000;
        }

        String fechaInicial = sdf.format(rSDateChooser1.getDatoFecha());
        fechaInicial = fechaInicial.concat(" 00:00:00");
        String fechaFinal = sdf.format(rSDateChooser2.getDatoFecha());
        fechaFinal = fechaFinal.concat(" 23:59:59");
        
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();

        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        if (host.equals("localhost")) {
            infoInventarioTeorico = mdao.getInventarioTeorico(idMovimiento, idTanque, fechaInicial, fechaFinal, length);
        } else {
            // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
            NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
            NovusUtils.printLn("║  🐍 SERVICIO PYTHON - REPORTE KARDEX                     ║");
            NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
            NovusUtils.printLn("📋 Consultando reporte de inventario Kardex");
            
            JsonObject data = new JsonObject();
            data.addProperty("idMovimiento", idMovimiento);
            data.addProperty("idTanque", idTanque);
            data.addProperty("fechaInicial", fechaInicial);
            data.addProperty("fechaFinal", fechaFinal);
            data.addProperty("length", length);
            
            NovusUtils.printLn("   - ID Movimiento: " + idMovimiento);
            NovusUtils.printLn("   - ID Tanque: " + idTanque);
            NovusUtils.printLn("   - Fecha Inicial: " + fechaInicial);
            NovusUtils.printLn("   - Fecha Final: " + fechaFinal);

            // Usar servicio Python en lugar del antiguo :8019
            String url = "http://"+ host + ":8019/api/inventario/kardex/reporte";
            
            NovusUtils.printLn("🌐 URL Servicio Python: " + url);
            NovusUtils.printLn("📤 Payload: " + data.toString());
            
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Accept", "application/json");

            ClientWSAsync async = new ClientWSAsync(
                    "CONSULTA INVENTARIO KARDEX - PYTHON",
                    url, NovusConstante.POST, data, true, false, headers, 10000);
            try {
                async.join();
                JsonObject informacion = async.esperaRespuesta();
                if (informacion != null) {
                    NovusUtils.printLn("✅ Respuesta del servicio Python recibida");
                    System.out.println(informacion);
                    infoInventarioTeorico = informacion.get("data").getAsJsonArray();
                } else {
                    NovusUtils.printLn("❌ ERROR: Sin respuesta del servicio Python");
                    NovusUtils.printLn("⚠️  El servicio Python puede no estar disponible en puerto 8001");
                }
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                // 🚀 MIGRACIÓN A SERVICIO PYTHON - FIN

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                NovusUtils.printLn("❌ ERROR en consulta Kardex (Servicio Python): " + e.getMessage());
            }
        }
        for (JsonElement jsonElement : infoInventarioTeorico) {
            JsonObject json = jsonElement.getAsJsonObject();

            cantidad = json.get("cantidad") != null ? json.get("cantidad").getAsFloat() : 0f;
            valor = json.get("valor") != null ? json.get("valor").getAsFloat() : 0f;
            lInicial = json.get("lecturaInicial") != null ? json.get("lecturaInicial").getAsFloat() : 0f;
            lFinal = json.get("lecturaFinal") != null ? json.get("lecturaFinal").getAsFloat() : 0f;

            //Producto
            String product = json.get("producto").getAsString();
            producto = product.replace("GASOLINA ", "").replace("OXIGENADA", "");

            //Tanque
            String tank = json.get("bodega").getAsString();
            tanque = tank.replace("TANQUE", "");

            //Movimiento
            String mov = json.get("movimiento").getAsString();
            movimiento = mov.replace("DE", "");
            defaultModel.addRow(new Object[]{
                movimiento,
                json.get("fecha").getAsString(),
                tanque,
                json.get("familia").getAsString(),
                producto,
                cantidad, valor, lInicial, lFinal
            });
        }
    }

    private void cerrar() {
        dispose();
    }

    public void getInfo() {
        // Obtener tanques usando el nuevo use case
        LinkedList<BodegaBean> tanquesList = new GetTanquesUseCase().execute();
        
        // Convertir LinkedList<BodegaBean> a TreeMap<Integer, String> para mantener compatibilidad
        bodega = new TreeMap<>();
        for (BodegaBean tanque : tanquesList) {
            bodega.put((int) tanque.getId(), tanque.getDescripcion());
        }
        
        //bodega = mdao.getTanques();
        //movimientos = mdao.getTipoMovimiento();
        movimientos = new GetTiposMovimientoUseCase().execute();
        listarTanques();
        listarMovimientos();
    }

    @SuppressWarnings("unchecked")
    public void listarTanques() {
        jComboTanques.removeAllItems();
        bodega.put(0, "TODOS");
        for (Map.Entry<Integer, String> entry : bodega.entrySet()) {
            String nombreTanque = entry.getValue();
            jComboTanques.addItem(nombreTanque + "");
        }
    }

    @SuppressWarnings("unchecked")
    public void listarMovimientos() {
        jComboMovimiento.removeAllItems();
        movimientos.put(0, "TODOS");
        for (Map.Entry<Integer, String> entry : movimientos.entrySet()) {
            String nombreDescripcion = entry.getValue();
            jComboMovimiento.addItem(nombreDescripcion + "");
        }
    }

    public void getIdMovimiento() {
        String nombreMovimiento = jComboMovimiento.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : movimientos.entrySet()) {
            if (entry.getValue().equals(nombreMovimiento)) {
                idMovimiento = entry.getKey();
            }
        }
    }

    public void getIdTanque() {
        String nombreTanque = jComboTanques.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : bodega.entrySet()) {
            if (entry.getValue().equals(nombreTanque)) {
                idTanque = entry.getKey();
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.bean.PersonaBean;
import com.bean.ReciboExtended;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neo.app.bean.MediosPagosBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.neo.app.bean.Recibo;
import com.application.useCases.movimientos.ValidatePaymentMethodShiftUseCase;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.firefuel.utils.ShowMessageSingleton;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class VentasHistoriaFulLView extends JDialog {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    boolean activarAcumular;
    final int CONSULTAR_VENTAS_FECHAS = 1;
    String tipo = "";
    String cedula = "";

    ArrayList<ReciboExtended> lista;
    ReciboExtended reciboFidelizar = null;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;

    InfoViewController parent;
    private final Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"));
    private final Icon botonBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"));
    
    // Cola de impresión
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();
    
    // Panel principal con CardLayout (igual que VentasHistorialView)
    private javax.swing.JPanel pnlPrincipal;

    public VentasHistoriaFulLView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        initComponents();
        init();
    }

    private void Async(Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        if (Main.persona != null) {
            jpromotor.setText("");
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }

        Calendar ca = Calendar.getInstance();
        rSDateChooser1.setDatoFecha(ca.getTime());

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

        NovusUtils.setUnsortableTable(this.jTable1);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jNotificacion_VentaF = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        rSDateChooser1 = new rojeru_san.componentes.RSDateChooser();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jImprimir = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        
        // Crear panel principal con CardLayout (igual que VentasHistorialView)
        pnlPrincipal = new javax.swing.JPanel();
        pnlPrincipal.setLayout(new java.awt.CardLayout());
        pnlPrincipal.setBounds(0, 0, 1280, 800);
        
        // Panel principal que contiene todo el contenido actual
        javax.swing.JPanel pnl_principal = new javax.swing.JPanel();
        pnl_principal.setLayout(null);
        pnl_principal.setBounds(0, 0, 1280, 800);

        jNotificacion_VentaF.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion_VentaF.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion_VentaF);
        jNotificacion_VentaF.setBounds(130, 720, 1120, 70);

        jComboBox1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(204, 0, 51));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        pnl_principal.add(jComboBox1);
        jComboBox1.setBounds(250, 110, 130, 50);

        rSDateChooser1.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setEnabled(false);
        rSDateChooser1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser1.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setPlaceholder("Fecha Inicial");
        pnl_principal.add(rSDateChooser1);
        rSDateChooser1.setBounds(30, 110, 210, 50);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor.setText("PROMOTOR");
        pnl_principal.add(jpromotor);
        jpromotor.setBounds(500, 30, 760, 50);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PREFIJO", "NRO", "FECHA", "PRODUCTO", "PROMOTOR", "CARA", "PLACA", "CANT", "VALOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(190);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(150);
        }

        pnl_principal.add(jScrollPane2);
        jScrollPane2.setBounds(30, 180, 1220, 510);

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel1.setText("ACTUALIZAR");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(410, 100, 190, 70);

        jImprimir.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jImprimir.setForeground(new java.awt.Color(255, 255, 255));
        jImprimir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jImprimir.setText("IMPRIMIR");
        jImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jImprimirMousePressed(evt);
            }
        });
        pnl_principal.add(jImprimir);
        jImprimir.setBounds(610, 100, 180, 70);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CONSULTA VENTAS");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(100, 0, 720, 90);

        jLabel2.setText("jLabel2");
        pnl_principal.add(jLabel2);
        jLabel2.setBounds(390, 420, 200, 16);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pnl_principal.add(jpromotor1);
        jpromotor1.setBounds(800, 10, 460, 20);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnl_principal.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        // Agregar panel principal al CardLayout
        pnlPrincipal.add(pnl_principal, "pnl_principal");
        
        // Agregar pnlPrincipal al contentPane
        getContentPane().setLayout(null);
        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        selecionar();
    }// GEN-LAST:event_jTable1MouseClicked

    private void jImprimirMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jImprimirMousePressed
        NovusUtils.beep();
        selectme();
    }// GEN-LAST:event_jImprimirMousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        refrescar("BOTON REFRESCAR");
    }// GEN-LAST:event_jLabel1MousePressed

    private void cerrar() {

        if (lista != null) {
            lista.clear();
        }
        lista = null;
        reciboFidelizar = null;

        System.gc();
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion_VentaF;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    // End of variables declaration//GEN-END:variables

    public void refrescar(String origen) {

        PersonaBean promotor = null;
        if (promotor != null) {
            Main.persona = promotor;
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("");
        }
        
        Async(() -> {
            solicitarVentas(rSDateChooser1.getDatoFecha(), rSDateChooser1.getDatoFecha(), (String) jComboBox1.getSelectedItem(), promotor, origen);

            try {

                DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
                dm.getDataVector().removeAllElements();
                dm.fireTableDataChanged();

                DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
                int i = 0;
                for (Recibo recibo : lista) {
                    try {
                        defaultModel.addRow(new Object[]{
                            (recibo.getAtributos() != null && recibo.getAtributos().get("consecutivo") != null
                            && !recibo.getAtributos().get("consecutivo").isJsonNull())
                            ? recibo.getAtributos().get("consecutivo").isJsonPrimitive() ? recibo.getAtributos().get("consecutivo").getAsString() : recibo.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo").getAsString() + "-"
                            + recibo.getAtributos().get("consecutivo").getAsJsonObject().get("consecutivo_actual")
                            .getAsString()
                            : "",
                            recibo.getNumero(),
                            sdf2.format(recibo.getFecha()),
                            recibo.getProducto(),
                            recibo.getOperador(),
                            (Integer.parseInt(recibo.getCara()) > 0) ? (recibo.getCara() + "") : "",
                            recibo.getPlaca(),
                            String.format("%.3f", recibo.getCantidadFactor()) + " " + (recibo.getAtributos() != null ? recibo.getAtributos().get("medida").getAsString().toUpperCase() : "GL"),
                            "$ " + df.format(recibo.getTotal())});
                        i++;
                    } catch (NumberFormatException e) {
                        NovusUtils.printLn(e.getMessage());
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(VentasHistoriaFulLView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    void consumoPropio(Recibo recibo) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        JsonObject json = new JsonObject();
        json.addProperty("identificadorMovimiento", recibo.getNumero());
        json.addProperty("identificadorTipoDocumento", 0);
        json.addProperty("devolucionInventario", false);
        json.addProperty("identificadorPromotor", Main.persona.getId());
        json.addProperty("observaciones", "VENTA A CONSUMO PROPIO");
        json.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());

        ClientWSAsync async = new ClientWSAsync("CONSUMO", NovusConstante.SECURE_CENTRAL_POINT_VENTA_CONSUMO_PROPIO,
                NovusConstante.POST, json, true, false, header);
        NovusUtils.printLn(json.toString());
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        if (response == null) {
            NovusUtils.setMensaje("OCURRIO UN ERROR EN EL PROCESO", jNotificacion_VentaF);
            jNotificacion_VentaF.setVisible(true);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jNotificacion_VentaF);
            });
        } else {
            NovusUtils.setMensaje("OCURRIO UN ERROR EN EL PROCESO", jNotificacion_VentaF);
            jNotificacion_VentaF.setVisible(true);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jNotificacion_VentaF);
            });
        }
    }

    private void selectmeAnulacion() {
        try {
            int r = jTable1.getSelectedRow();
            int c = jTable1.getSelectedColumn();
            NovusUtils.printLn(r + "row");
            if (r > -1) {
                long value = (long) jTable1.getValueAt(r, 1);
                if (Main.persona != null) {
                    for (Recibo recibo : lista) {
                        if (value == recibo.getNumero()) {
                            JsonObject response;
                           // response = validarTurnoMedioPago(recibo.getNumero());
                           //   if (new ValidatePaymentMethodShiftUseCase(recibo.getNumero()).execute()) {
                            //ConfirmarAnulacionView confAnulacion = new ConfirmarAnulacionView(parent, true, recibo);
                            //confAnulacion.setVisible(true);
                            //refrescar("ANULACION");
                       // } else {
                           // jNotificacion.setText("NO SE PUEDE ANULAR, EL TURNO ACTUAL NO ES EL MISMO DE LA VENTA");
                            //jNotificacion.setVisible(true);
                            //setTimeout(2, () -> {
                            //    jNotificacion.setText("");
                            //    dispose();
                            //});
                        //}
                       // break;
                           if (new ValidatePaymentMethodShiftUseCase(recibo.getNumero()).execute()) {
                               ConfirmarAnulacionView confAnulacion = new ConfirmarAnulacionView(parent, true, recibo);
                               confAnulacion.setVisible(true);
                               refrescar("ANULACION");
                           } else {
                               jNotificacion.setText("NO SE PUEDE ANULAR, EL TURNO ACTUAL NO ES EL MISMO DE LA VENTA");
                               jNotificacion.setVisible(true);
                               setTimeout(2, () -> {
                                   jNotificacion.setText("");
                                   dispose();
                               });
                           }
                           break;
                        }
                    }
                } else {
                    jNotificacion.setText("DEBE INICIAR TURNO PARA ESTA ACCION");
                    jNotificacion.setVisible(true);
                    setTimeout(2, () -> {
                        jNotificacion.setText("");
                        dispose();
                    });
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VentasHistoriaFulLView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectme() {
        try {
            int r = jTable1.getSelectedRow();
            if (r < 0) {
                return;
            }
            
            long value = (long) jTable1.getValueAt(r, 1);
            
            if (Main.persona != null) {
                // Buscar el recibo seleccionado
                Recibo reciboSeleccionado = null;
                for (Recibo recibo : lista) {
                    if (value == recibo.getNumero()) {
                        reciboSeleccionado = recibo;
                        break;
                    }
                }
                
                if (reciboSeleccionado != null) {
                    final Recibo reciboFinal = reciboSeleccionado;
                    final long valueFinal = value;
                    
                    // Bloquear botón mientras se verifica
                    jImprimir.setText("VERIFICANDO...");
                    jImprimir.setIcon(botonBloqueado);
                    
                    // Ejecutar health check en thread separado
                    new Thread(() -> {
                        try {
                            // Verificar health check del servicio (usando caso de uso con cache)
                            CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
                            
                            if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                                // Servicio no responde o no está saludable - mostrar modal de error
                                String mensaje = healthResult.obtenerMensajeError();
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    jImprimir.setText("IMPRIMIR");
                                    jImprimir.setIcon(botonActivo);
                                });
                                mostrarMensajeError(mensaje);
                                return;
                            }
                            
                            // Servicio OK - Abrir pantalla de impresión
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                jImprimir.setText("IMPRIMIR");
                                jImprimir.setIcon(botonActivo);
                                
                                NovusUtils.printLn(Long.toString(valueFinal));
                                VentasHistorialPlacaViewController hispl = new VentasHistorialPlacaViewController(parent, true);
                                NovusUtils.printLn(reciboFinal.getNumero() + "" + reciboFinal);
                                hispl.setRecibo(reciboFinal);
                                if (!isFE(valueFinal) || !isEspecial(valueFinal)) {
                                    hispl.setVisible(true);
                                } else if (isEspecial(valueFinal)) {
                                    hispl.imprimir("factura");
                                } else {
                                    hispl.imprimir("factura-electronica");
                                }
                                refrescar("SELECTME");
                            });
                            
                        } catch (Exception e) {
                            Logger.getLogger(VentasHistoriaFulLView.class.getName()).log(Level.SEVERE, null, e);
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                jImprimir.setText("IMPRIMIR");
                                jImprimir.setIcon(botonActivo);
                            });
                            mostrarMensajeError("ERROR AL VERIFICAR SERVICIO");
                        }
                    }).start();
                }
            } else {
                jNotificacion.setText("DEBE INICIAR TURNO PARA ESTA ACCION");
                jNotificacion.setVisible(true);
                setTimeout(2, () -> {
                    jNotificacion.setText("");
                    dispose();
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(VentasHistoriaFulLView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void solicitarVentas(Date fechaInial, Date fechaFinal, String hora, PersonaBean persona, String origen) {
        JsonObject comando = new JsonObject();
        if (fechaInial == null) {
            fechaInial = new Date();
        }
        if (fechaFinal == null) {
            fechaFinal = new Date();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
        SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        comando.addProperty("fechaInicio", sdf1.format(fechaInial) + " " + hora + ":00:00");
        comando.addProperty("fechaFin", sdf1.format(fechaFinal) + " " + hora + ":59:59");
        comando.addProperty("inTurno", false);
        if (persona != null) {
            comando.addProperty("identificadorPromotor", persona.getId());
        }

        boolean isArray = false;
        boolean isDebug = false;
        String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_VENTAS_CONSULTA;
        NovusUtils.printLn("______________________________________");
        NovusUtils.printLn("CONSULTANDO A CENTRALIZADOR LAS VENTAS");
        NovusUtils.printLn(url);
        NovusUtils.printLn(comando.toString());

        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(
                "CONSEGUIR VENTAS",
                url,
                NovusConstante.POST,
                comando,
                isDebug, isArray,
                header);
        JsonObject response;
        try {
            client.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        NovusUtils.printLn("______________________________________");
        try {
            response = client.getResponse();
            if (response != null) {
                lista.clear();
                JsonArray array = response.get("data").getAsJsonArray();
                for (JsonElement jsonElement : array) {
                    try {
                        JsonObject json = jsonElement.getAsJsonObject();
                        ReciboExtended rec = new ReciboExtended();

                        rec.setNumero(json.get("numero").getAsLong());
                        rec.setTotal(json.get("total").getAsLong());
                        if (json.get("cantidad") != null && !json.get("cantidad").isJsonNull()) {
                            rec.setCantidadFactor(json.get("cantidad").getAsDouble());
                        }

                        rec.setProducto(json.get("producto").getAsString());

                        rec.setOperador(json.get("operador").getAsString());
                        rec.setIdentificacionPromotor(json.get("identificacionPromotor").getAsString());
                        rec.setIdentificacionProducto(json.get("identificacionProducto").getAsLong());
                        rec.setPrecio(json.get("precio").getAsLong());

                        if (json.get("atributos") != null && !json.get("atributos").isJsonNull()) {
                            rec.setAtributos(json.get("atributos").getAsJsonObject());
                            rec.getAtributos().addProperty("medida", json.get("unidad_medida").getAsString());
                        }
                        if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_odometro").isJsonNull()) {
                            rec.setOdometro(rec.getAtributos().get("vehiculo_odometro").getAsString());
                        } else {
                            rec.setOdometro("");
                        }
                        if (rec.getAtributos() != null && !rec.getAtributos().get("orden").isJsonNull()) {
                            rec.setVoucher(rec.getAtributos().get("orden").getAsString());
                        } else {
                            rec.setVoucher("");
                        }
                        if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_placa").isJsonNull()) {
                            rec.setPlaca(rec.getAtributos().get("vehiculo_placa").getAsString());
                        } else {
                            rec.setPlaca("");
                        }
                        if (rec.getAtributos() != null && rec.getAtributos().get("consecutivo").isJsonNull()) {
                            rec.getAtributos().addProperty("consecutivo", json.get("consecutivo").getAsString());
                        } else {
                            rec.getAtributos().addProperty("is_especial",
                                    !json.get("tipo").getAsString().equals("017"));
                        }

                        JsonArray medios = json.get("medios_pagos").getAsJsonArray();

                        rec.setMediosPagos(new ArrayList<>());
                        for (JsonElement medio : medios) {
                            JsonObject med = medio.getAsJsonObject();

                            MediosPagosBean m = new MediosPagosBean();
                            m.setId(med.get("medio").getAsLong());
                            m.setDescripcion(med.get("medioDescripcion").getAsString());
                            m.setValor(med.get("valorPago").getAsFloat());

                            rec.getMediosPagos().add(m);
                        }

                        rec.setCara(json.get("cara").getAsString());
                        rec.setManguera(json.get("manguera").getAsString());
                        Date date = sdfSQL.parse(json.get("fecha").getAsString());
                        rec.setFecha(date);
                        lista.add(rec);
                    } catch (ParseException a) {
                        NovusUtils.printLn(a.getMessage());
                    }
                }
            } else {

            }
        } catch (Exception ex) {
            Logger.getLogger(VentasHistoriaFulLView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    boolean isFE(long idMovimiento) {
        boolean isFE = false;
        for (Recibo mov : this.lista) {
            if (idMovimiento == mov.getNumero()) {
                if (mov.getAtributos() != null && mov.getAtributos().get("isElectronica") != null
                        && !mov.getAtributos().get("isElectronica").isJsonNull()
                        && mov.getAtributos().get("isElectronica").getAsBoolean()) {
                    isFE = true;
                    break;
                }
            }
        }
        return isFE;
    }

    boolean isEspecial(long idMovimiento) {
        boolean isEspecial = false;
        for (Recibo mov : this.lista) {
            if (idMovimiento == mov.getNumero()) {
                if (mov.getAtributos() != null && mov.getAtributos().get("is_especial") != null
                        && !mov.getAtributos().get("is_especial").isJsonNull()
                        && mov.getAtributos().get("is_especial").getAsBoolean()) {
                    isEspecial = true;
                    break;
                }
            }
        }
        return isEspecial;
    }

    private void selecionar() {
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            NovusUtils.beep();
            long value = (long) jTable1.getValueAt(r, 1);
            for (ReciboExtended recibo : lista) {
                if (recibo.getNumero() == value) {
                    reciboFidelizar = recibo;
                    break;
                }
            }

            // Verificar si existe en cola de impresión
            if (existeEnColaPendiente(value)) {
                // Está en cola - mostrar bloqueado con texto IMPRIMIENDO...
                jImprimir.setText("IMPRIMIENDO...");
                jImprimir.setIcon(botonBloqueado);
            } else {
                // No está en cola - mostrar normal
                jImprimir.setText("IMPRIMIR");
                jImprimir.setIcon(botonActivo);
            }

        } else {
            jImprimir.setText("IMPRIMIR");
            jImprimir.setIcon(botonBloqueado);

        }
    }

   // private JsonObject validarTurnoMedioPago(long idMovimiento) {
    private JsonObject ValidatePaymentMethodShiftUseCase(long idMovimiento) {
        JsonObject response = null;
        JsonObject objMain = new JsonObject();
        objMain.addProperty("identificadorMovimiento", idMovimiento);
        objMain.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        objMain.addProperty("validarTurno", true);
        JsonArray mediosArray = new JsonArray();
        objMain.add("mediosDePagos", mediosArray);
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-Type", "application/json");
        boolean DEBUG = true;
        ClientWSAsync ws = new ClientWSAsync("validarTurnoMedioPago", NovusConstante.SECURE_END_POINT_CAMBIO_MEDIOSPAGOS, NovusConstante.PUT,
                objMain, DEBUG, false, header);
        try {
            ws.start();
            ws.join();
            response = ws.getResponse();
            NovusUtils.printLn(response.toString());
        } catch (InterruptedException ex) {
            NovusUtils.printLn(ex.getMessage());
            Thread.currentThread().interrupt();
        }
        return response;
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
    
    // ============================================
    // MÉTODOS DE VERIFICACIÓN DE SERVICIO
    // ============================================
    
    /**
     * Muestra un mensaje de error usando el mismo enfoque que VentasHistorialView
     * Usa CardLayout en lugar de JDialog para evitar problemas después de navegar entre pantallas
     */
    private void mostrarMensajeError(String mensaje) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // Crear el panel de mensaje usando ShowMessageSingleton (mismo estilo que VentasHistorialView)
                ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder()
                    .setMsj(mensaje)
                    .setRuta("/com/firefuel/resources/btBad.png")
                    .setHabilitar(true)
                    .setRunnable(() -> mostrarMenuPrincipal(true))
                    .setAutoclose(true)
                    .setLetterCase(LetterCase.FIRST_UPPER_CASE)
                    .build();
                
                JPanel panelMensaje = ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes);
                
                // Verificar que el panel se haya creado correctamente
                if (panelMensaje == null) {
                    NovusUtils.printLn("ERROR: Panel de mensaje es null");
                    return;
                }
                
                // Usar mostrarSubPanel (igual que VentasHistorialView)
                mostrarSubPanel(panelMensaje);
                
            } catch (Exception e) {
                NovusUtils.printLn("Error mostrando mensaje de error: " + e.getMessage());
                Logger.getLogger(VentasHistoriaFulLView.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }
    
    /**
     * Muestra un subpanel en el CardLayout (igual que VentasHistorialView)
     */
    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }
    
    /**
     * Muestra el panel principal (igual que VentasHistorialView)
     */
    private void mostrarMenuPrincipal(boolean autoCerrar) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     */
    private synchronized boolean existeEnColaPendiente(long id) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() > 0) {
                    JsonArray registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registros) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.get("id").getAsLong() == id
                            && registro.has("report_type") && registro.get("report_type").getAsString().equals("VENTAS_HISTORICAS")) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error verificando cola de impresión: " + e.getMessage());
        }
        return false;
    }

}

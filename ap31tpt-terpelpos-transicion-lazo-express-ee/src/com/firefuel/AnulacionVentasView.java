/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.application.useCases.facturacion.ObtenerMotivosAnulacionUseCase;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import java.awt.Color;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class AnulacionVentasView extends JDialog {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    JFrame pedido;
    boolean activarAcumular;

    MovimientosDao mdao = new MovimientosDao();

    String tipo = "";
    String cedula = "";
    JsonArray motivos = new JsonArray();
    ArrayList<Recibo> lista;
    ArrayList<String> medidas = new ArrayList<>();
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    static final Logger logger = Logger.getLogger(AnulacionVentasView.class.getName());

    private InfoViewController parent;

    public AnulacionVentasView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        rSDateChooser1.setDatoFecha(ca.getTime());
        rSDateChooser2.setDatoFecha(new Date());

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

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
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

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

        refrescar();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jNotificacion_Venta = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        rSDateChooser1 = new rojeru_san.componentes.RSDateChooser();
        rSDateChooser2 = new rojeru_san.componentes.RSDateChooser();
        jpromotor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setUndecorated(true);
        getContentPane().setLayout(null);

        jNotificacion_Venta.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion_Venta.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion_Venta);
        jNotificacion_Venta.setBounds(140, 720, 980, 70);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel30);
        jLabel30.setBounds(1130, 710, 10, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        rSDateChooser1.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setEnabled(false);
        rSDateChooser1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser1.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setPlaceholder("Fecha Inicial");
        getContentPane().add(rSDateChooser1);
        rSDateChooser1.setBounds(30, 110, 210, 50);

        rSDateChooser2.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser2.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setPlaceholder("Fecha Final");
        getContentPane().add(rSDateChooser2);
        rSDateChooser2.setBounds(270, 110, 210, 50);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        getContentPane().add(jpromotor);
        jpromotor.setBounds(820, 40, 460, 40);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PREFIJO", "NRO", "FECHA", "PROMOTOR", "VALOR", "MOTIVO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(110);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(250);
        }

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(30, 180, 1220, 510);

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
        jLabel1.setBounds(510, 100, 180, 70);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CONSULTA ANULACIONES");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(120, 15, 720, 50);

        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(390, 420, 200, 16);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel7MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        getContentPane().add(jpromotor1);
        jpromotor1.setBounds(820, 10, 460, 20);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        getContentPane().add(jclock);
        jclock.setBounds(1150, 720, 110, 66);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel31);
        jLabel31.setBounds(90, 10, 10, 68);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
        });
        getContentPane().add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel8MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel8MousePressed
        NovusUtils.beep();
    }// GEN-LAST:event_jLabel8MousePressed

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed

    }// GEN-LAST:event_jLabel5MousePressed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        refrescar();
    }// GEN-LAST:event_jLabel1MousePressed

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed

    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel7MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MousePressed

    }// GEN-LAST:event_jLabel7MousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        selecionar();
    }// GEN-LAST:event_jTable1MouseClicked

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MousePressed
    }// GEN-LAST:event_jLabel6MousePressed

    private void cerrar() {
        this.setVisible(false);
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jNotificacion_Venta;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel jclock;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser2;
    // End of variables declaration//GEN-END:variables

    public void refrescar() {
        
        //motivos = mdao.motivosAnulacion();
        motivos = new ObtenerMotivosAnulacionUseCase().execute();

        solicitarVentas(rSDateChooser1.getDatoFecha(), rSDateChooser2.getDatoFecha());
        jTable1.setAutoCreateRowSorter(true);
        try {
            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();

            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
            int i = 0;
            for (Recibo recibo : lista) {
                JsonObject atributos = recibo.getAtributos().get("consecutivo").getAsJsonObject();
                String prefijo = atributos.get("prefijo").getAsString();
                String consecutivo_actual = atributos.has("consecutivo_actual") ? atributos.get("consecutivo_actual").isJsonNull() ? "" : atributos.get("consecutivo_actual").getAsString() : "";

                int tipoMotivo = recibo.getAtributos() != null && !recibo.getAtributos().get("tipoAnulacion").isJsonNull() ? recibo.getAtributos().get("tipoAnulacion").getAsInt() : 0;
                String motivo = getMotivo(tipoMotivo);

                defaultModel.addRow(new Object[]{
                    (recibo.getAtributos() != null && !recibo.getAtributos().get("consecutivo").isJsonNull())
                    ? prefijo.concat(" - ").concat(consecutivo_actual) : "",
                    recibo.getNumero(),
                    sdf2.format(recibo.getFecha()),
                    recibo.getOperador(),
                    "$ " + df.format(recibo.getTotal()),
                    motivo
                });
                i++;
            }
        } catch (Exception s) {
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, s);
        }
    }

    public String getMotivo(int codigo) {
        String tipoMotivo = "";
        if (motivos.size() > 0) {
            for (JsonElement motivo : motivos) {
                JsonObject jsonMotivo = motivo.getAsJsonObject();
                if (jsonMotivo.get("codigo").getAsInt() == codigo) {
                    tipoMotivo = jsonMotivo.get("descripcion").getAsString();
                }
            }
        }
        return tipoMotivo;
    }

    public void solicitarVentas(Date fechaInial, Date fechaFinal) {
        JsonObject comando = new JsonObject();
        if (fechaInial == null) {
            fechaInial = new Date();
        }
        if (fechaFinal == null) {
            fechaFinal = new Date();
        }
        SimpleDateFormat sdff = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
        SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        comando.addProperty("fechaInicio", sdff.format(fechaInial) + " 00:00:00");
        comando.addProperty("fechaFin", sdff.format(fechaFinal) + " 23:00:00");
        boolean isArray = false;
        boolean isDebug = true;
        String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_VENTAS_ANULADAS_CONSULTA;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync("CONSEGUIR VENTAS", url, NovusConstante.POST, comando, isDebug,
                isArray,
                header);
        JsonObject response;
        try {
            client.start();
            client.join();
        } catch (InterruptedException e) {
            NovusUtils.printLn(e.getMessage());
            Thread.currentThread().interrupt();
        }

        try {
            response = client.getResponse();
            if (response != null) {
                lista.clear();
                JsonArray array = response.get("data").getAsJsonArray();
                for (JsonElement jsonElement : array) {
                    try {
                        JsonObject json = jsonElement.getAsJsonObject();
                        Recibo rec = new Recibo();
                        rec.setNumero(json.get("numero").getAsLong());
                        rec.setTotal(json.get("total").getAsLong());
                        if (json.get("cantidad") != null && !json.get("cantidad").isJsonNull()) {
                            rec.setCantidadFactor(json.get("cantidad").getAsDouble());
                        }

                        rec.setOperador(json.get("operador").getAsString());
                        if (json.get("atributos") != null && !json.get("atributos").isJsonNull()) {
                            rec.setAtributos(json.get("atributos").getAsJsonObject());
                        }
                        if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_odometro").isJsonNull()) {
                            rec.setOdometro(rec.getAtributos().get("vehiculo_odometro").getAsString());
                        } else {
                            rec.setOdometro("");
                        }
                        if (rec.getAtributos() != null && !rec.getAtributos().get("voucher").isJsonNull()) {
                            rec.setVoucher(rec.getAtributos().get("voucher").getAsString());
                        } else {
                            rec.setVoucher("");
                        }
                        if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_placa").isJsonNull()) {
                            rec.setPlaca(rec.getAtributos().get("vehiculo_placa").getAsString());
                        } else {
                            rec.setPlaca("");
                        }

                        rec.setCara(json.get("cara").getAsString());
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
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
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

    private void selecionar() {

    }

    class AsyncTask extends Thread {

        VentasHistorialView vista;

        public AsyncTask(VentasHistorialView vista) {
            this.vista = vista;
        }

        void setBloqueoVista(boolean bloquear) {

        }

        void consultarVentasPorFecha() {
        }

        @Override
        public void run() {

        }
    }
}

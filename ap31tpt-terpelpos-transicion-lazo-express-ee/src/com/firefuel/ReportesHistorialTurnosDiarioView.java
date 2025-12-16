package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.JornadaBean;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.ReporteCierreDiaDao;
import com.facade.ReporteFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ReportesHistorialTurnosDiarioView extends JDialog {

    SimpleDateFormat sdfx = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdfDate = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    JFrame pedido;
    boolean activarAcumular;
    boolean WITH_JC = false;

    String tipo = "";
    String cedula = "";

    ArrayList<JornadaBean> lista;
    ArrayList<PersonaBean> listaPersonas;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    PersonaBean persona = new PersonaBean();
    float totalVentas = 0;
    float totalVentasCanastilla = 0;
    float cantidadTotalVentasCanastilla = 0;
    float cantidadTotalVentas = 0;
    JsonObject jsonReporte = null;
    InfoViewController parent;
    
    // Cola de impresión para archivo TXT (mismo patrón que ReporteVentasPromotor)
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    private final Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"));
    private final Icon botonBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"));

    public ReportesHistorialTurnosDiarioView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        listaPersonas = new ArrayList<>();
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
        jTable1.getColumnModel().getColumn(1).setCellRenderer(textCenter);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(textRight);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        jTable1.setFillsViewportHeight(true);
        // jTable1.setBackground(new Color(255, 255, 255));
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

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        rSDateChooser1.setDatoFecha(ca.getTime());

        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO");
        }
        EquipoDao dao = new EquipoDao();
        String value = dao.getParametroWacher("WITH_JAVA_CONTROL");
        if (value != null && !value.isEmpty() && value.equals("S")) {
            WITH_JC = true;
        }
        if (Main.SIN_SURTIDOR) {
            jLabel14.setText("CANTIDAD VENTAS");
        }
        activarBotonimpresion();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        rSDateChooser1 = new rojeru_san.componentes.RSDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jNotificacion = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        rSDateChooser1.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setEnabled(false);
        rSDateChooser1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser1.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setPlaceholder("Fecha Consulta");
        pnl_principal.add(rSDateChooser1);
        rSDateChooser1.setBounds(140, 140, 240, 40);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel6.setText("REPORTE 24H");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel6MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel6);
        jLabel6.setBounds(410, 130, 190, 60);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        pnl_principal.add(jpromotor);
        jpromotor.setBounds(560, 30, 720, 50);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel4.setText("IMPRIMIR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel4);
        jLabel4.setBounds(920, 640, 190, 60);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("REPORTE TURNO DIARIO");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(90, 0, 460, 90);

        jLabel19.setFont(new java.awt.Font("Terpel Sans", 1, 40)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("2020-01-01");
        pnl_principal.add(jLabel19);
        jLabel19.setBounds(210, 270, 340, 60);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel25.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(186, 12, 47));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("FECHA :");
        pnl_principal.add(jLabel25);
        jLabel25.setBounds(240, 220, 270, 50);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MEDIO PAGO", "CANTIDAD", "TOTAL"
            }
        ));
        jTable1.setRowHeight(30);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(1).setMinWidth(100);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(100);
            jTable1.getColumnModel().getColumn(2).setMinWidth(150);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(2).setMaxWidth(150);
        }

        pnl_principal.add(jScrollPane1);
        jScrollPane1.setBounds(60, 360, 630, 290);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        jLabel2.setText("jLabel2");
        pnl_principal.add(jLabel2);
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
        pnl_principal.add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jLabel13.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(186, 12, 47));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("TOTAL VENTAS");
        pnl_principal.add(jLabel13);
        jLabel13.setBounds(830, 170, 350, 70);

        jLabel14.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(186, 12, 47));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("GALONES VENDIDOS");
        pnl_principal.add(jLabel14);
        jLabel14.setBounds(830, 370, 350, 60);

        jLabel20.setFont(new java.awt.Font("Terpel Sans", 1, 48)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("-");
        pnl_principal.add(jLabel20);
        jLabel20.setBounds(800, 250, 410, 110);

        jLabel24.setFont(new java.awt.Font("Terpel Sans", 1, 48)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("-");
        pnl_principal.add(jLabel24);
        jLabel24.setBounds(800, 450, 410, 110);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        pnl_principal.add(jpromotor1);
        jpromotor1.setBounds(560, 0, 720, 30);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndReporteTurnoDiario.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel5MousePressed

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        // Verificar primero si está bloqueado por la variable booleana
        if (botonImprimirBloqueado) {
            return; // No hacer nada si está bloqueado
        }
        if (jLabel4.isEnabled()) {
            NovusUtils.beep();
            obtenerNovedades();
            selectme(false);
        }
    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel7MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MousePressed
    }// GEN-LAST:event_jLabel7MousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MousePressed
        NovusUtils.beep();
        refrescar();
    }// GEN-LAST:event_jLabel6MousePressed

    private void cerrar() {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    // End of variables declaration//GEN-END:variables

    private void refrescar() {
        this.jsonReporte = null;
        Date fechaConsulta = rSDateChooser1.getDatoFecha();
        Date currentDate = new Date();
        if (fechaConsulta.getTime() < currentDate.getTime()) {
            if (WITH_JC) {
                getCierrreDia(fechaConsulta);
            } else {
                solicitarTurnos(fechaConsulta);
            }
            if (this.jsonReporte != null && this.jsonReporte.get("data").isJsonObject()) {
                JsonObject jsonCierre = this.jsonReporte.getAsJsonObject("data");

                cantidadTotalVentas = jsonCierre.get("cantidadVentasCombustible").getAsFloat();
                totalVentas = jsonCierre.get("totalVentasCanastilla").getAsFloat()
                        + jsonCierre.get("totalVentasCombustible").getAsFloat();
                try {
                    DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
                    dm.getDataVector().removeAllElements();
                    dm.fireTableDataChanged();
                    DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
                    JsonArray jsonArrayMediosPagos = jsonCierre.getAsJsonArray("mediosPagos");
                    for (JsonElement elementMediosPagos : jsonArrayMediosPagos) {
                        JsonObject jsonMedioPago = elementMediosPagos.getAsJsonObject();
                        String nombre = jsonMedioPago.get("descripcion").getAsString();
                        int cantidadVentas = jsonMedioPago.get("cantidad").getAsInt();
                        float total = jsonMedioPago.get("total").getAsFloat();
                        defaultModel.addRow(new Object[]{nombre, cantidadVentas, "$ " + df.format(total)});
                    }
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
                if (Main.SIN_SURTIDOR) {
                    jLabel20.setText("$ " + df.format(jsonCierre.get("totalVentasCDL").getAsFloat()));
                    jLabel24.setText(df.format(jsonCierre.get("cantidadVentasCDL").getAsFloat()));
                } else {
                    jLabel20.setText("$ " + df.format(totalVentas));
                    jLabel24.setText(Utils.fmt(cantidadTotalVentas));
                }
                jLabel19.setText(sdfx.format(fechaConsulta));
                jLabel4.setEnabled(true);
                
                // Verificar estado de la cola al mostrar el reporte
                verificarEstadoColaAlMostrar();
            }
        } else {
            jLabel4.setEnabled(false);
            mostrarPanelMensaje("REPORTE NO APLICA PARA ESTA FECHA", 
                    "/com/firefuel/resources/btBad.png", 
                    3, LetterCase.FIRST_UPPER_CASE);
        }
    }
    
    /**
     * Verifica el estado de la cola al mostrar el reporte y bloquea/desbloquea el botón según corresponda
     */
    private void verificarEstadoColaAlMostrar() {
        Date fechaConsulta = rSDateChooser1.getDatoFecha();
        if (fechaConsulta != null) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
            String fechaStr = sdf1.format(fechaConsulta);
            long reportId = fechaStr.hashCode();
            if (reportId < 0) {
                reportId = Math.abs(reportId);
            }
            String reportType = "DIARIO"; // Unificado con el report_type que se envía al microservicio
            
            if (existeEnColaPendiente(reportId, reportType)) {
                // Si está en cola, mostrar botón bloqueado con texto IMPRIMIENDO...
                botonImprimirBloqueado = true;
                jLabel4.setIcon(botonBloqueado);
                jLabel4.setText("IMPRIMIENDO...");
                jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel4.setForeground(new java.awt.Color(255, 255, 255));
                // NO usar setEnabled(false) - el bloqueo se maneja solo con botonImprimirBloqueado
            } else {
                // Si no está en cola, mostrar botón normal
                botonImprimirBloqueado = false;
                jLabel4.setIcon(botonActivo);
                jLabel4.setText("IMPRIMIR");
                jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel4.setForeground(new java.awt.Color(255, 255, 255));
                // NO usar setEnabled(true) - el componente siempre está habilitado visualmente
            }
            pnl_principal.revalidate();
            pnl_principal.repaint();
        }
    }

    JsonObject buildRequestimpresionCierreDiarioConsolidado() {
        JsonObject request = new JsonObject();
        Date fechaConsulta = rSDateChooser1.getDatoFecha();
        SimpleDateFormat sdf1 = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
        request.addProperty("day", sdf1.format(fechaConsulta));
        request.addProperty("report_type", "DIARIO");

        return request;
    }

    // // NOI18N
    private void selectme(boolean detalles) {
        // Verificar solo la variable booleana para el bloqueo de impresión
        // No usar isEnabled() porque no deshabilitamos el componente para mantener el color del texto
        if (botonImprimirBloqueado) {
            return;
        }
        if (!jLabel4.isEnabled()) {
            return;
        }

         NovusUtils.beep();
        
        // Obtener ID único basado en la fecha del reporte
        Date fechaConsulta = rSDateChooser1.getDatoFecha();
        SimpleDateFormat sdf1 = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
        String fechaStr = sdf1.format(fechaConsulta);
        // Convertir fecha a ID único (hash de la fecha)
        long reportIdTemp = fechaStr.hashCode();
        if (reportIdTemp < 0) {
            reportIdTemp = Math.abs(reportIdTemp);
        }
        // Crear variables finales para usar en el lambda
        final long reportId = reportIdTemp;
        final String reportType = "DIARIO"; // Unificado con el report_type que se envía al microservicio
        
        // Verificar si ya está en cola de impresión
        if (existeEnColaPendiente(reportId, reportType)) {
            NovusUtils.printLn("El registro ya está en cola de impresión - ID: " + reportId);
            return;
        }
        
        // BLOQUEAR INMEDIATAMENTE al hacer click
        bloquearBotonImprimir();
        
        // Guardar en cola antes de imprimir
        guardarRegistroPendiente(reportId, reportType);
        
        // Ejecutar el proceso de impresión en un hilo separado
        new Thread(() -> {
            try {
                // Pequeña pausa para asegurar que la UI se actualice
                Thread.sleep(50);
                
                JsonObject response = ReporteFacade
                        .imprimirReporteCierreDiarioConsolidado(this.buildRequestimpresionCierreDiarioConsolidado());
                
                // Eliminar de cola después de imprimir
                eliminarRegistroPendiente(reportId, reportType);
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimir();
                    
                    if (response != null) {
                        boolean success = !response.has("success") || response.get("success").getAsBoolean();
                        boolean tieneErrorCampo = response.has("error") && !response.get("error").isJsonNull();
                        
                        if (success && !tieneErrorCampo) {
                            int tiempo = obtenerNovedades().isEmpty() ? 5 : 10;
                            mostrarPanelMensaje(
                                    "REPORTE IMPRESO CORRECTAMENTE <br>".concat(obtenerNovedades().toUpperCase()),
                                    "/com/firefuel/resources/btOk.png",
                                    tiempo,
                                    LetterCase.FIRST_UPPER_CASE
                            );
                        } else {
                            String mensajeError = "OCURRIÓ UN ERROR EN LA IMPRESIÓN";
                            
                            if (response.has("message") && !response.get("message").isJsonNull()) {
                                mensajeError = response.get("message").getAsString();
                            } else if (tieneErrorCampo) {
                                mensajeError = response.get("error").getAsString();
                            }
                            
                            NovusUtils.printLn("⚠️ Error al imprimir cierre diario: " + mensajeError);
                            mostrarPanelMensaje(
                                    mensajeError,
                                    "/com/firefuel/resources/btBad.png",
                                    5,
                                    LetterCase.FIRST_UPPER_CASE
                            );
                        }
                    } else {
                        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_REPORTS;
                        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
                        NovusUtils.printLn("║  SERVICIO DE IMPRESIÓN PYTHON ESTÁ APAGADO                ║");
                        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
                        NovusUtils.printLn("ERROR: Sin respuesta del microservicio Python");
                        NovusUtils.printLn("URL: " + url);
                        mostrarPanelMensaje(
                                "SERVICIO DE IMPRESIÓN NO DISPONIBLE",
                                "/com/firefuel/resources/btBad.png",
                                5,
                                LetterCase.FIRST_UPPER_CASE
                        );
                    }
                });
            } catch (Exception e) {
                eliminarRegistroPendiente(reportId, reportType);
                Logger.getLogger(ReportesHistorialTurnosDiarioView.class.getName()).log(Level.SEVERE, null, e);
                final String errorMsg = "ERROR AL PROCESAR LA IMPRESIÓN: " + e.getMessage();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimir();
                    mostrarPanelMensaje(
                            errorMsg,
                            "/com/firefuel/resources/btBad.png",
                            5,
                            LetterCase.FIRST_UPPER_CASE
                    );
                });
            }
        }).start();
    }

    public JsonObject solicitarReporteDiscriminadoResolucion() {
        JsonObject comando = new JsonObject();
        Date fechaConsulta = rSDateChooser1.getDatoFecha();
        if (fechaConsulta == null) {
            fechaConsulta = new Date();
        }
        comando.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        comando.addProperty("fechaInicio", sdfx.format(fechaConsulta) + " 00:00:00");
        comando.addProperty("fechaFin", sdfx.format(fechaConsulta) + " 23:59:59");
        String funcion = "CONSEGUIR TURNOS";
        String url = NovusConstante.SECURE_CENTRAL_POINT_FIN_TURNO_RESOLUCION_V2;

        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, comando, true, false, header);

        JsonObject response = null;
        try {
           client.start();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

    public void solicitarTurnos(Date fechaConsulta) {
        JsonObject comando = new JsonObject();
        if (fechaConsulta == null) {
            fechaConsulta = new Date();
        }
        comando.addProperty("fechaInicio", sdf.format(fechaConsulta) + " 00:00:00");
        comando.addProperty("fechaFin", sdf.format(fechaConsulta) + " 23:59:59");
        comando.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());

        String funcion = "CONSEGUIR TURNOS";
        String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_TURNOS_DIARIO;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, comando, true, false, header);
        client.start();
        try {
            client.join();
            this.jsonReporte = client.getResponse();
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    private void getCierrreDia(Date fechaConsulta) {
        if (fechaConsulta == null) {
            fechaConsulta = new Date();
        }
        MovimientosDao dat = new MovimientosDao();
        try {
            this.jsonReporte = dat.findAllCierresDiaReporteria(sdfDate.format(fechaConsulta));
            if (this.jsonReporte.size() == 0) {
                this.jsonReporte = dat.findAllCierresDia(sdfDate.format(fechaConsulta));
            }
            NovusUtils.printLn((this.jsonReporte).toString());
        } catch (DAOException ex) {
            Logger.getLogger(ReportesHistorialTurnosDiarioView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(ReportesHistorialTurnosDiarioView.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono, int tiempo, String letterCase) {

        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(true).setRunnable(runnable)
                .setLetterCase(letterCase).build();
        pnl_container.add("card_mensajes", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));

        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        Async(runnable, tiempo);
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    }

    private void activarBotonimpresion() {
        ReporteCierreDiaDao cierreDiaDao = new ReporteCierreDiaDao();
        jLabel4.setEnabled(!cierreDiaDao.activarImpresionCierreDia());

    }

    private String obtenerNovedades() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(rSDateChooser1.getDatoFecha());
        ReporteCierreDiaDao cierreDiaDao = new ReporteCierreDiaDao();
        return cierreDiaDao.novedades(calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH) + 1, calendario.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a "IMPRIMIENDO..." con fondo gris y texto blanco
     * NO usa setEnabled(false) para evitar que el Look and Feel cambie el color del texto a gris
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true; // Bloqueo funcional con variable booleana
        jLabel4.setIcon(botonBloqueado);
        jLabel4.setText("IMPRIMIENDO...");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setForeground(new java.awt.Color(255, 255, 255)); // Texto blanco - se mantendrá porque no deshabilitamos
        // NO usar setEnabled(false) - el bloqueo se maneja solo con botonImprimirBloqueado
        // Forzar repaint del panel principal también
        pnl_principal.repaint();
        jLabel4.repaint();
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto "IMPRIMIR"
     */
    private void desbloquearBotonImprimir() {
        botonImprimirBloqueado = false; // Desbloqueo funcional con variable booleana
        jLabel4.setIcon(botonActivo);
        jLabel4.setText("IMPRIMIR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        // NO usar setEnabled(true) - el componente siempre está habilitado visualmente
        jLabel4.repaint();
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     * @param id El ID del registro a buscar
     * @param reportType El tipo de reporte a buscar
     * @return true si existe en la cola, false si no existe
     */
    private synchronized boolean existeEnColaPendiente(long id, String reportType) {
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
                    JsonArray registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registros) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.get("id").getAsLong() == id 
                            && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
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
    
    /**
     * Guarda un registro de impresión pendiente en el archivo TXT
     * @param id El ID del reporte (hash de la fecha)
     * @param reportType El tipo de reporte (DIARIO - unificado con el microservicio)
     */
    private synchronized void guardarRegistroPendiente(long id, String reportType) {
        try {
            // Crear carpeta logs si no existe
            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdir();
            }

            // Leer archivo existente o crear nuevo array
            JsonArray registros = new JsonArray();
            File file = new File(PRINT_QUEUE_FILE);
            
            if (file.exists() && file.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("Error leyendo archivo de cola de impresión: " + e.getMessage());
                    registros = new JsonArray();
                }
            }

            // Verificar si el ID ya existe en el array (doble verificación para evitar duplicados)
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    NovusUtils.printLn("Registro ya existe en cola de impresión - ID: " + id + " (no se duplica)");
                    return; // No agregar si ya existe
                }
            }

            // Crear nuevo registro
            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("status", "PENDING");
            nuevoRegistro.addProperty("message", "IMPRIMIENDO...");

            // Agregar al array
            registros.add(nuevoRegistro);

            // Guardar archivo con formato legible
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registros));
            }

            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(ReportesHistorialTurnosDiarioView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Se llama cuando la impresión termina (éxito o error)
     * @param id El ID del reporte a eliminar
     * @param reportType El tipo de reporte
     */
    private synchronized void eliminarRegistroPendiente(long id, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                NovusUtils.printLn("No hay registros en cola de impresión para eliminar");
                return;
            }
            
            // Leer archivo existente
            JsonArray registros;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                if (content.length() > 0) {
                    registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                } else {
                    return;
                }
            }
            
            // Filtrar el registro a eliminar
            JsonArray registrosActualizados = new JsonArray();
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (!(registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType))) {
                    registrosActualizados.add(registro);
                }
            }
            
            // Guardar archivo actualizado
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registrosActualizados));
            }
            
            NovusUtils.printLn("Registro eliminado de cola de impresión - ID: " + id + ", Tipo: " + reportType);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(ReportesHistorialTurnosDiarioView.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}

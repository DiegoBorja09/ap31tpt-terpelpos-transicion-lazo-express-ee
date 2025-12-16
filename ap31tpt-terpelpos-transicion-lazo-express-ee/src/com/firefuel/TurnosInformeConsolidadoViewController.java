package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.sutidores.ObtenerHostSurtidoresEstacionUseCase;
import com.bean.JornadaBean;
import com.bean.MediosPagosBean;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.util.TreeMap;
import com.dao.DAOException;
import com.dao.SurtidorDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TurnosInformeConsolidadoViewController extends javax.swing.JDialog {

    JornadaBean jornada;
    InfoViewController vistaPrincipal;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    SimpleDateFormat sdfAM = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_AM);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    ObtenerHostSurtidoresEstacionUseCase obtenerHostSurtidoresEstacionUseCase = new ObtenerHostSurtidoresEstacionUseCase();
    SurtidorDao sdao = new SurtidorDao();
    JsonArray infoSurtidores = new JsonArray();
    
    // Constantes y variables para manejo de cola de impresión
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public TurnosInformeConsolidadoViewController(InfoViewController vistaPrincipal, boolean modal) throws DAOException {
        super(vistaPrincipal, modal);
        this.vistaPrincipal = vistaPrincipal;
        initComponents();
        this.init();
    }

    public void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

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

        this.cargarInformacion();

        NovusUtils.printLn("info: " + infoSurtidores);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTotalSobres = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        JBLrespuestaError = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel2.setLayout(null);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel2.add(jLabel34);
        jLabel34.setBounds(10, 710, 100, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel36);
        jLabel36.setBounds(1130, 710, 10, 80);

        jLabel11.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("ESTADO:");
        jPanel2.add(jLabel11);
        jLabel11.setBounds(70, 200, 180, 60);

        jTotalSobres.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jTotalSobres.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jTotalSobres.setText("$0");
        jPanel2.add(jTotalSobres);
        jTotalSobres.setBounds(930, 480, 280, 50);

        jLabel21.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("$0");
        jPanel2.add(jLabel21);
        jLabel21.setBounds(880, 270, 330, 50);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.setLabelFor(this);
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        jPanel2.add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jLabel16.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 204, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("ABIERTO");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(290, 200, 350, 60);

        jLabel12.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("TOTAL SOBRES:");
        jPanel2.add(jLabel12);
        jLabel12.setBounds(660, 480, 280, 60);

        jLabel18.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("PROMOTOR");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(290, 140, 900, 50);

        jLabel19.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("2020-01-01 23:53:00 AM");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(280, 270, 360, 50);

        jLabel20.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("$0");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(930, 340, 280, 50);

        jLabel22.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("0");
        jPanel2.add(jLabel22);
        jLabel22.setBounds(990, 210, 220, 50);

        jLabel24.setFont(new java.awt.Font("Terpel Sans", 1, 28)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("0");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(980, 410, 230, 50);

        jLabel25.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("FECHA INICIO:");
        jPanel2.add(jLabel25);
        jLabel25.setBounds(70, 265, 180, 60);

        jLabel27.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel27.setText("IMPRIMIR");
        jLabel27.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel27MousePressed(evt);
            }
        });
        jPanel2.add(jLabel27);
        jLabel27.setBounds(970, 620, 180, 60);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MEDIO PAGO", "CANTIDAD", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(40);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(350);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        }

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(60, 360, 570, 310);

        jLabel26.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel26.setText("ACTUALIZAR");
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel26MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel26MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel26);
        jLabel26.setBounds(740, 620, 180, 60);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("INFORME DE TURNOS CONSOLIDADO");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(100, 0, 720, 90);

        jLabel23.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("PROMOTOR:");
        jPanel2.add(jLabel23);
        jLabel23.setBounds(70, 140, 180, 50);

        jLabel28.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("NUMERO DE TURNO:");
        jPanel2.add(jLabel28);
        jLabel28.setBounds(660, 200, 270, 60);

        jLabel29.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("SALDO:");
        jPanel2.add(jLabel29);
        jLabel29.setBounds(660, 270, 150, 60);

        jLabel30.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("TOTAL VENTAS:");
        jPanel2.add(jLabel30);
        jLabel30.setBounds(660, 340, 230, 60);

        jLabel13.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("NUMERO DE VENTAS:");
        jPanel2.add(jLabel13);
        jLabel13.setBounds(660, 410, 280, 60);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        JBLrespuestaError.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        JBLrespuestaError.setForeground(new java.awt.Color(255, 255, 255));
        JBLrespuestaError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(JBLrespuestaError);
        JBLrespuestaError.setBounds(150, 720, 960, 70);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndInfoTurnos.png"))); // NOI18N
        jPanel2.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel32);
        jLabel32.setBounds(1130, 710, 10, 80);

        pnlPrincipal.add(jPanel2, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        if (jLabel3.isEnabled()) {
            cerrar();
        }
    }// GEN-LAST:event_jLabel3MousePressed

    private void jLabel26MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel26MousePressed
        NovusUtils.beep();
        cargarInformacion();
    }// GEN-LAST:event_jLabel26MousePressed

    private void jLabel27MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel27MousePressed
        NovusUtils.beep();
        imprimirReporte();
    }// GEN-LAST:event_jLabel27MousePressed

    private void jLabel26MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel26MouseReleased

    }// GEN-LAST:event_jLabel26MouseReleased

    private void cerrar() {
        this.setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel JBLrespuestaError;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTotalSobres;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    void cargarInformacion() {
        Runnable salir = ()->{
          cerrar();
          NovusUtils.beep();
        };
        showMessage("CARGANDO INFORMACIÓN TURNO... ", 
                "/com/firefuel/resources/loader_fac.gif", true, salir, 
                false, LetterCase.FIRST_UPPER_CASE);
        AsyncTask asyncTask = new AsyncTask(this);
        asyncTask.start();
    }

    void cargarTabla(ArrayList<MediosPagosBean> mediosPagoJornada) {
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        for (MediosPagosBean medio : mediosPagoJornada) {
            try {
                defaultModel.addRow(new Object[]{
                    medio.getDescripcion(),
                    medio.getCantidad() + "",
                    "$ " + df.format(medio.getValor())
                });
            } catch (Exception s) {
                NovusUtils.printLn(s.getMessage());
            }
        }
    }

    void renderizarInformeTurno(JornadaBean jornada, float totalSobres) {
        this.jornada = jornada;
        cargarTabla(jornada.getMedios());
        jLabel19.setText(sdfAM.format(jornada.getFechaInicial()));
        jLabel18.setText(jornada.getPersona().getNombre().trim());
        jLabel21.setText("$" + df.format(jornada.getSaldo()));
        jLabel22.setText(String.valueOf(jornada.getGrupoJornada()));
        jLabel20.setText(String.valueOf("$ " + df.format(jornada.getTotalVentas())));
        jLabel24.setText(String.valueOf(jornada.getCantidadVentas()));
        jTotalSobres.setText(String.valueOf("$ " + df.format(totalSobres)));

    }
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a IMPRIMIENDO...
     * No usa setEnabled(false) para mantener el texto visible
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true;
        jLabel27.setText("IMPRIMIENDO...");
        jLabel27.setForeground(Color.WHITE);
        // Usar icono gris para indicar que está bloqueado
        jLabel27.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto a IMPRIMIR
     */
    private void desbloquearBotonImprimir() {
        botonImprimirBloqueado = false;
        jLabel27.setText("IMPRIMIR");
        jLabel27.setForeground(Color.WHITE);
        jLabel27.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     * @param id El ID del registro a buscar
     * @return true si existe en la cola, false si no existe
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
                            && registro.has("report_type") && registro.get("report_type").getAsString().equals("REPORTE_CONSOLIDADO_TURNO")) {
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
     * @param id El ID del registro (turno/grupo jornada)
     * @param reportType El tipo de reporte
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
                        registros = JsonParser.parseString(content.toString()).getAsJsonArray();
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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registros));
            }

            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(TurnosInformeConsolidadoViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Se llama cuando el health check falla o la respuesta del servicio no es exitosa
     * @param id El ID del registro a eliminar
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
                    registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                } else {
                    return;
                }
            }
            
            // Buscar y eliminar el registro
            JsonArray registrosActualizados = new JsonArray();
            boolean encontrado = false;
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    encontrado = true;
                    NovusUtils.printLn("🗑️ Eliminando registro de cola de impresión - ID: " + id + ", Tipo: " + reportType);
                    // No agregar este registro (lo eliminamos)
                } else {
                    registrosActualizados.add(registro);
                }
            }
            
            if (encontrado) {
                // Guardar archivo actualizado
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(registrosActualizados));
                }
                NovusUtils.printLn("✅ Registro eliminado de cola de impresión - ID: " + id);
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(TurnosInformeConsolidadoViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    

    private void imprimirReporte() {
        NovusUtils.printLn("[CONSOLIDADO-DETALLE] Botón IMPRIMIR presionado");
        
        // Verificar si el botón está bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("[CONSOLIDADO-DETALLE] Botón de impresión bloqueado - hay una impresión en proceso");
            return;
        }
        
        // Obtener el ID del turno (debe ser final para usar en lambda)
        final long identificadorJornada = (Main.persona != null && Main.persona.getGrupoJornadaId() != 0) 
            ? Main.persona.getGrupoJornadaId() 
            : (jornada != null ? jornada.getGrupoJornada() : 0);
        
        if (identificadorJornada == 0) {
            NovusUtils.printLn("[CONSOLIDADO-DETALLE] ERROR: No se pudo obtener el ID del turno");
            showMessage("ERROR: NO SE PUDO OBTENER EL ID DEL TURNO",
                    "/com/firefuel/resources/btBad.png",
                    true, () -> {},
                    true, LetterCase.FIRST_UPPER_CASE);
            return;
        }
        
        NovusUtils.printLn("[CONSOLIDADO-DETALLE] ID del turno: " + identificadorJornada);
        
        // Verificar si ya está en cola de impresión
        if (existeEnColaPendiente(identificadorJornada)) {
            NovusUtils.printLn("[CONSOLIDADO-DETALLE] El registro ya está en cola de impresión - ID: " + identificadorJornada);
            bloquearBotonImprimir();
            return;
        }
        
        // Bloquear botón y cambiar texto a IMPRIMIENDO...
        bloquearBotonImprimir();
        
        // Ejecutar health check y luego imprimir en un hilo separado
        new Thread(() -> {
            try {
                // 1. Verificar que el servicio de impresión esté activo y saludable (usando caso de uso con cache)
                CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
                
                if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                    // Servicio no responde o no está saludable - NO guardar registro, solo desbloquear y mostrar error
                    final String mensaje = healthResult.obtenerMensajeError();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        desbloquearBotonImprimir();
                        mostrarPanelMensaje(mensaje,
                                "/com/firefuel/resources/btBad.png",
                                LetterCase.FIRST_UPPER_CASE);
                    });
                    return;
                }
                
                // 3. Servicio OK - Guardar registro y proceder con la impresión
                guardarRegistroPendiente(identificadorJornada, "REPORTE_CONSOLIDADO_TURNO");
                
                
                // Usar Print Ticket Service (Python) en lugar de PrinterFacade
                long identificadorPromotor = Main.persona != null ? Main.persona.getId() : 0;
                com.application.useCases.shiftReports.PrintShiftConsolidatedRemoteUseCase useCase = 
                    new com.application.useCases.shiftReports.PrintShiftConsolidatedRemoteUseCase(
                        identificadorJornada,
                        identificadorPromotor
                    );
                
                com.domain.dto.shiftReports.ShiftReportResult result = useCase.execute(null);
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (result.isSuccess()) {
                        // UI: Mostrar mensaje genérico de éxito
                        showMessage("REPORTE IMPRESO CORRECTAMENTE", 
                                "/com/firefuel/resources/btOk.png", 
                                true, this::cerrar, 
                                true, LetterCase.FIRST_UPPER_CASE);
                    } else {
                        // Error en la impresión - eliminar registro de cola
                        eliminarRegistroPendiente(identificadorJornada, "REPORTE_CONSOLIDADO_TURNO");
                        showMessage("OCURRIO UN ERROR EN LA IMPRESION",
                                "/com/firefuel/resources/btBad.png",
                                true, () -> {
                                    desbloquearBotonImprimir();
                                    // No cerrar en caso de error, permitir reintentar
                                },
                                true, LetterCase.FIRST_UPPER_CASE);
                    }
                });
            } catch (Exception e) {
                // Error inesperado - eliminar registro de cola si se guardó
                eliminarRegistroPendiente(identificadorJornada, "REPORTE_CONSOLIDADO_TURNO");
                NovusUtils.printLn("=====================================================");
                NovusUtils.printLn("ERROR INESPERADO en nueva arquitectura");
                NovusUtils.printLn("=====================================================");
                NovusUtils.printLn("Tipo: " + e.getClass().getName());
                NovusUtils.printLn("Mensaje: " + e.getMessage());
                NovusUtils.printLn("=====================================================");
                e.printStackTrace();
                Logger.getLogger(TurnosInformeConsolidadoViewController.class.getName()).log(Level.SEVERE, null, e);
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    showMessage("ERROR INESPERADO AL IMPRIMIR REPORTE: " + e.getMessage(),
                            "/com/firefuel/resources/btBad.png",
                            true, () -> {
                                desbloquearBotonImprimir();
                                // No cerrar en caso de error, permitir reintentar
                            },
                            true, LetterCase.FIRST_UPPER_CASE);
                });
            }
        }).start();
    }

    JsonObject response = null;
    String mensaje = "";

    class AsyncTask extends Thread {

        TurnosInformeConsolidadoViewController vista;

        public AsyncTask(TurnosInformeConsolidadoViewController vista) {
            this.vista = vista;
            try {
                infoSurtidores = obtenerHostSurtidoresEstacionUseCase.execute();
            } catch (Exception ex) {
                Logger.getLogger(TurnosInformeConsolidadoViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        void setBloqueoVista(boolean bloquear) {
            this.vista.jLabel26.setEnabled(!bloquear);
            this.vista.jLabel27.setEnabled(!bloquear);
            this.vista.jLabel3.setEnabled(!bloquear);
        }

        private void consultarConexionHost() {
            JsonObject promotor = new JsonObject();
            if (Main.persona != null) {
                promotor.addProperty("identificadorPromotor", Main.persona.getId());
                boolean status = true;
                for (JsonElement element : infoSurtidores) {

                    JsonObject json = element.getAsJsonObject();
                    String host = json.get("host").getAsString();
                    String isla = json.get("isla").getAsString();

                    NovusUtils.printLn(Main.ANSI_PURPLE
                            + " HOST: " + host
                            + " ISLA: " + isla
                            + Main.ANSI_RESET);

                    String url = "http://"
                            + host
                            + ":8010/api/reportes/informacionTurno";

                    ClientWSAsync async = new ClientWSAsync(
                            "CONSULTA CONEXION ISLA",
                            url, NovusConstante.POST, promotor, true, false, 10000);
                    try {
                        async.join();
                        JsonObject informacionIsla = async.esperaRespuesta();
                        if (informacionIsla == null) {
                            showMessage("Error de comunicación con la isla: ".concat(isla).concat(" genere el reporte una vez se reestablezca la comunicación "),
                                    "/com/firefuel/resources/btBad.png", true, () -> {
                                        cerrar();
                                    }, true, LetterCase.FIRST_UPPER_CASE);
                            status = false;
                            break;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        NovusUtils.printLn(e.getMessage());
                    }
                }
                if (status) {
                    consultarInformeTurno();
                }
            }
        }

        void consultarInformeTurno() {
            long inicioRequest = new Date().getTime();
            JsonObject response = null;
            String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_CONSULTA_CONSOLIDADO;
            JsonObject jpromo = new JsonObject();
            if (Main.persona != null) {
                jpromo.addProperty("identificadorPromotor", Main.persona.getId());
                ClientWSAsync async = new ClientWSAsync("CONSULTA DE INFORME TURNO CONSOLIDADO", url, NovusConstante.POST, jpromo, true, false, 15000);
                try {
                    response = async.esperaRespuesta();
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }

                float totalSobre;
                if (response != null && response.get("data") != null && !response.get("data").isJsonNull()) {
                    try {
                        JsonObject json = response.get("data").getAsJsonObject();
                        PersonaBean persona = new PersonaBean();
                        persona.setNombre(json.get("promotor").getAsString());
                        Date fecha = sdf.parse(json.get("fechaJornada").getAsString());
                        JsonArray array = json.get("lecturasIniciales").getAsJsonArray();
                        ArrayList<Surtidor> lecturasIniciales = new ArrayList<>();

                        for (JsonElement jsonElement : array) {
                            JsonObject jsonw = jsonElement.getAsJsonObject();
                            Surtidor surtidor = new Surtidor();
                            surtidor.setSurtidor(jsonw.get("surtidor").getAsInt());
                            surtidor.setCara(jsonw.get("cara").getAsInt());
                            surtidor.setManguera(jsonw.get("manguera").getAsInt());
                            surtidor.setTotalizadorVenta(jsonw.get("acumuladoVenta").getAsLong());
                            surtidor.setTotalizadorVolumen(jsonw.get("acumuladoVolumen").getAsLong());
                            surtidor.setProductoIdentificador(jsonw.get("productoIdentificador").getAsLong());
                            surtidor.setFamiliaDescripcion(jsonw.get("familiaDescripcion").getAsString());
                            surtidor.setProductoDescripcion(jsonw.get("productoDescripcion").getAsString());
                            lecturasIniciales.add(surtidor);
                        }

                        JsonArray arrayF = json.get("lecturasFinales").getAsJsonArray();
                        ArrayList<Surtidor> lecturasFinales = new ArrayList<>();
                        for (JsonElement jsonElement : arrayF) {
                            JsonObject jsonw = jsonElement.getAsJsonObject();
                            Surtidor surtidor = new Surtidor();
                            surtidor.setSurtidor(jsonw.get("surtidor").getAsInt());
                            surtidor.setCara(jsonw.get("cara").getAsInt());
                            surtidor.setManguera(jsonw.get("manguera").getAsInt());
                            surtidor.setTotalizadorVenta(jsonw.get("acumuladoVenta").getAsLong());
                            surtidor.setTotalizadorVolumen(jsonw.get("acumuladoVolumen").getAsLong());
                            surtidor.setFamiliaDescripcion(jsonw.get("familiaDescripcion").getAsString());
                            surtidor.setProductoIdentificador(jsonw.get("productoIdentificador").getAsLong());
                            surtidor.setProductoDescripcion(jsonw.get("productoDescripcion").getAsString());
                            lecturasFinales.add(surtidor);
                        }

                        JsonObject sobres = json.get("caja_sobres") != null && !json.get("caja_sobres").isJsonNull() ? json.get("caja_sobres").getAsJsonObject() : new JsonObject();
                        totalSobre = sobres.get("total") != null && !sobres.get("total").isJsonNull() ? sobres.get("total").getAsFloat() : 0;
                        JsonArray medios = json.get("medios").getAsJsonArray();
                        ArrayList<MediosPagosBean> mediospagos = new ArrayList<>();

                        for (JsonElement jsonElement : medios) {
                            JsonObject jsonw = jsonElement.getAsJsonObject();
                            MediosPagosBean medio = new MediosPagosBean();
                            medio.setDescripcion(jsonw.get("medios").getAsString());
                            medio.setValor(jsonw.get("total").getAsFloat());
                            medio.setCantidad(jsonw.get("cantidad").getAsInt());
                            mediospagos.add(medio);
                        }

                        JornadaBean jornada = new JornadaBean();
                        jornada.setPersona(persona);
                        jornada.setFechaInicial(fecha);
                        jornada.setLecturasIniciales(lecturasIniciales);
                        jornada.setLecturasFinales(lecturasFinales);
                        jornada.setSaldo(json.get("saldo").getAsFloat());
                        jornada.setCantidadVentas(json.get("numeroVentas").getAsInt());
                        jornada.setGrupoJornada(json.get("turno").getAsLong());
                        jornada.setTotalVentas(json.get("totalVentas").getAsFloat());
                        jornada.setCantidadVentas(json.get("numeroVentas").getAsInt());
                        jornada.setMedios(mediospagos);
                        long finRequest = new Date().getTime();
                        long duracionRequest = finRequest - inicioRequest;
                        while (duracionRequest <= 1000) {
                            finRequest = new Date().getTime();
                            duracionRequest = finRequest - inicioRequest;

                        }
                        vista.renderizarInformeTurno(jornada, totalSobre);
                    } catch (ParseException ex) {
                        Logger.getLogger(TurnosInformeViewController.class.getName()).log(Level.SEVERE, null, ex);
                        this.setBloqueoVista(false);
                    }
                    cambiarPanelHome();
                    jLabel27.setEnabled(true);
                } else {
                    NovusUtils.setMensaje(mensaje, JBLrespuestaError);
                    setBloqueoVista(false);
                    showMessage("NO SE PUDO CONSULTAR EL CONSOLIDADO EN ISLA", "/com/firefuel/resources/btBad.png", true, () -> {
                        cerrar();
                    }, true, LetterCase.FIRST_UPPER_CASE);
                }
            } else {
                showMessage("SIN TURNOS", "/com/firefuel/resources/btBad.png", true, () -> {
                    cerrar();
                }, true, LetterCase.FIRST_UPPER_CASE);
            }
            this.vista.setVisible(true);
        }

        @Override
        public void run() {
            consultarConexionHost();
        }
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
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

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnl_principal");
        pnlPrincipal.revalidate();
        pnlPrincipal.repaint();
    }
    
    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                // Ejecutar en el EDT para asegurar que el cambio de panel se refleje
                javax.swing.SwingUtilities.invokeLater(runnable);
            } catch (InterruptedException e) {
                Logger.getLogger(TurnosInformeConsolidadoViewController.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    public void mostrarPanelMensaje(String mensaje, String icono, String letterCase) {
        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(true).setRunnable(runnable)
                .setLetterCase(letterCase).build();
        pnlPrincipal.add("pnl_ext", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));

        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnl_ext");
        Async(runnable, 3);
    }
}

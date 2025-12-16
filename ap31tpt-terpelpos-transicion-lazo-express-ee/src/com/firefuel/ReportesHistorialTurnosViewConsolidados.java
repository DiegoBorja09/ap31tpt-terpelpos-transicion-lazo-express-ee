package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.JornadaBean;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
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
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ReportesHistorialTurnosViewConsolidados extends JDialog {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    JFrame pedido;
    SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
    boolean activarAcumular;
    String tipo = "";
    String cedula = "";
    ArrayList<JornadaBean> lista;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    PersonaBean persona = new PersonaBean();
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    InfoViewController parent;

    MovimientosDao mdao = new MovimientosDao();
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public ReportesHistorialTurnosViewConsolidados(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        initComponents();
        this.init();

    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.setTablePrimaryStyle(jTable1);

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        rSDateChooser1.setDatoFecha(ca.getTime());
        rSDateChooser2.setDatoFecha(new Date());

        this.parent.recargarPersona();
        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO");
        }
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
        rSDateChooser2 = new rojeru_san.componentes.RSDateChooser();
        jpromotor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jNotificacion = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jactualizar = new javax.swing.JLabel();
        jimprimir = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setLayout(null);

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
        rSDateChooser1.setBounds(50, 110, 210, 50);

        rSDateChooser2.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser2.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setPlaceholder("Fecha Final");
        pnl_principal.add(rSDateChooser2);
        rSDateChooser2.setBounds(280, 110, 210, 50);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        pnl_principal.add(jpromotor);
        jpromotor.setBounds(600, 30, 680, 50);

        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "GRUPO", "FECHA INICIO", "FECHA FIN", "CANTIDAD VENTAS", "TOTAL VENTAS"
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
        jTable1.setShowGrid(true);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(65);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
        }

        pnl_principal.add(jScrollPane2);
        jScrollPane2.setBounds(50, 190, 1190, 470);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jactualizar.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jactualizar.setForeground(new java.awt.Color(255, 255, 255));
        jactualizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jactualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jactualizar.setText("ACTUALIZAR");
        jactualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jactualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jactualizarMousePressed(evt);
            }
        });
        pnl_principal.add(jactualizar);
        jactualizar.setBounds(1060, 110, 190, 60);

        jimprimir.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jimprimir.setForeground(new java.awt.Color(255, 255, 255));
        jimprimir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jimprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jimprimir.setText("IMPRIMIR");
        jimprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jimprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jimprimirMousePressed(evt);
            }
        });
        pnl_principal.add(jimprimir);
        jimprimir.setBounds(870, 110, 190, 60);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("REPORTE CONSOLIDADOS");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(90, 0, 490, 90);

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

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        pnl_principal.add(jpromotor1);
        jpromotor1.setBounds(600, 10, 680, 30);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
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

    private void jLabel8MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel8MousePressed
    }// GEN-LAST:event_jLabel8MousePressed

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed
    }// GEN-LAST:event_jLabel5MousePressed

    private void jactualizarMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        refrescar();
    }// GEN-LAST:event_jLabel1MousePressed

    private void jimprimirMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        NovusUtils.printLn("[CONSOLIDADO] Botón IMPRIMIR presionado");
        selectme(false);
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
        selectme(true);
    }// GEN-LAST:event_jLabel6MousePressed

    private void cerrar() {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jactualizar;
    private javax.swing.JLabel jimprimir;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser2;
    // End of variables declaration//GEN-END:variables

    private void refrescar() {
        solicitarTurnos(rSDateChooser1.getDatoFecha(), rSDateChooser2.getDatoFecha());
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(jTable1.getModel());
        try {
            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();
            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
            int con = 0;
            for (JornadaBean jornada : lista) {
                defaultModel.addRow(new Object[]{
                    ++con,
                    jornada.getGrupoJornada(),
                    sdf.format(jornada.getFechaInicial()),
                    sdf.format(jornada.getFechaFinal()),
                    jornada.getCantidadVentas(),
                    "$ " + df.format(jornada.getTotalVentas())
                });
            }
        } catch (Exception s) {
            Logger.getLogger(ReportesHistorialTurnosViewConsolidados.class.getName()).log(Level.SEVERE, null, s);
        }
    }

    private void selectme(boolean detalles) {
        NovusUtils.printLn("[CONSOLIDADO] selectme llamado - detalles: " + detalles);
        
        // Verificar si el botón está bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("[CONSOLIDADO] Botón de impresión bloqueado - hay una impresión en proceso");
            return;
        }
        
        long index = 0;
        int r = jTable1.getSelectedRow();
        NovusUtils.printLn("[CONSOLIDADO] Fila seleccionada: " + r);
        
        if (r > -1) {
            NovusUtils.beep();
            NovusUtils.printLn("[CONSOLIDADO] Procesando impresión para fila: " + r);
            
            for (JornadaBean jornada : lista) {
                if ((jornada.getGrupoJornada() == (long) jTable1.getValueAt(r, 1))) {
                    NovusUtils.printLn("[CONSOLIDADO] Turno encontrado: " + jornada.getGrupoJornada());
                    
                    // Verificar si ya está en cola de impresión
                    if (existeEnColaPendiente(jornada.getGrupoJornada())) {
                        NovusUtils.printLn("[CONSOLIDADO] El registro ya está en cola de impresión - ID: " + jornada.getGrupoJornada());
                        return;
                    }
                    
                    final long turnoFinal = jornada.getGrupoJornada();
                    
                    NovusUtils.printLn("[CONSOLIDADO] Iniciando proceso de impresión para turno: " + turnoFinal);
                    
                    // Bloquear botón y cambiar texto a IMPRIMIENDO...
                    bloquearBotonImprimir();
                    
                    // Ejecutar health check y luego imprimir en un hilo separado
                    new Thread(() -> {
                        try {
                            // Pequeña pausa para asegurar que la UI se actualice
                            Thread.sleep(50);
                            
                            // 1. Verificar que el servicio de impresión esté activo y saludable (usando caso de uso con cache)
                            CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
                            
                            if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                                // Servicio no responde o no está saludable - eliminar registro de cola y actualizar UI
                                eliminarRegistroPendiente(turnoFinal, "REPORTE_CONSOLIDADO");
                                final String mensaje = healthResult.obtenerMensajeError();
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    NovusUtils.printLn("❌ Servicio de impresión no está disponible: " + mensaje);
                                    desbloquearBotonImprimir();
                                    mostrarPanelMensaje(mensaje,
                                            "/com/firefuel/resources/btBad.png",
                                            "FIRST_UPPER_CASE");
                                });
                                return;
                            }
                            
                            // 3. Servicio OK - Guardar registro y proceder con la impresión
                            guardarRegistroPendiente(turnoFinal, "REPORTE_CONSOLIDADO_TURNO");
                            imprimirConsolidado(turnoFinal);
                            
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                eliminarRegistroPendiente(turnoFinal, "REPORTE_CONSOLIDADO_TURNO");
                                desbloquearBotonImprimir();
                            });
                        } catch (Exception e) {
                            Logger.getLogger(ReportesHistorialTurnosViewConsolidados.class.getName()).log(Level.SEVERE, null, e);
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                eliminarRegistroPendiente(turnoFinal, "REPORTE_CONSOLIDADO_TURNO");
                                desbloquearBotonImprimir();
                            });
                        }
                    }).start();
                    
                    break;
                }
                index++;
            }
            
            // Si no se encontró el turno en la lista después de recorrerla
            if (index >= lista.size()) {
                NovusUtils.printLn("[CONSOLIDADO] ADVERTENCIA: No se encontró el turno en la lista después de buscar");
            }
        } else {
            // Si no hay fila seleccionada y se presionó el botón, mostrar mensaje
            if (!detalles) {
                NovusUtils.printLn("[CONSOLIDADO] ADVERTENCIA: Se presionó IMPRIMIR pero no hay fila seleccionada");
            }
        }
    }
    
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a IMPRIMIENDO...
     * No usa setEnabled(false) para mantener el texto visible
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true;
        jimprimir.setText("IMPRIMIENDO...");
        jimprimir.setForeground(Color.WHITE);
        // Usar icono gris para indicar que está bloqueado
        jimprimir.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto a IMPRIMIR
     */
    private void desbloquearBotonImprimir() {
        botonImprimirBloqueado = false;
        jimprimir.setText("IMPRIMIR");
        jimprimir.setForeground(Color.WHITE);
        jimprimir.setIcon(new javax.swing.ImageIcon(
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
                if (registro.has("id") && registro.get("id").getAsLong() == id) {
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
            Logger.getLogger(ReportesHistorialTurnosViewConsolidados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirConsolidado(long turno) {
        // Ejecutar impresión en thread separado - solo envía el request sin esperar respuesta
        new Thread(() -> {
            try {
                String funcion = "IMPRIMIR CONSOLIDADO";
                String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_REPORTS;

                TreeMap<String, String> header = new TreeMap<>();
                header.put("content-type", "application/json");
                header.put("authorization", "1");
                header.put("uuid", "519a5c11-ae7f-4470-9f67-e212a62ba704");
                header.put("fecha", sdfISO.format(new Date()));
                header.put("aplicacion", "lazoexpress");
                header.put("original", "http://localhost:8010");

                JsonObject comando = new JsonObject();
                comando.addProperty("shift", turno);
                comando.addProperty("report_type", "CONSOLIDADO");
                comando.addProperty("flow_type", "REPORTE_CONSOLIDADO_TURNO");

                // Solo enviar el request sin esperar respuesta
                ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, comando, true, false, header, 5000);
                client.start();
                
                NovusUtils.printLn("Request de impresión enviado - Turno: " + turno + ", Tipo: CONSOLIDADO");
                
            } catch (Exception e) {
                e.printStackTrace();
                NovusUtils.printLn("ERROR al enviar request de impresión: " + e.getMessage());
            }
        }).start();
    }

    public JsonArray solicitarTurnos(Date fechaInial, Date fechaFinal) {
        if (fechaInial == null) {
            fechaInial = new Date();
        }
        if (fechaFinal == null) {
            fechaFinal = new Date();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);

        String fechaInicio = sdf1.format(fechaInial) + " 00:00:00";
        String fechaFin = sdf1.format(fechaFinal) + " 23:59:59";

        JsonArray dataCierre = mdao.getConsolidadoReporteria(fechaInicio, fechaFin);
        NovusUtils.printLn("Fecha Inicio: " + fechaInicio);
        NovusUtils.printLn("Fecha Fin: " + fechaFin);
        NovusUtils.printLn("Info Cierre: " + dataCierre);
        if (dataCierre.size() > 0) {
            listarInformacionCierre(dataCierre);
        } else {
            JsonArray dataCierreDefault = mdao.getConsolidado(fechaInicio, fechaFin);
            if (dataCierreDefault != null) {
                listarInformacionCierre(dataCierreDefault);
            }
        }
        return dataCierre;
    }

    private void listarInformacionCierre(JsonArray data) {
        lista.clear();
        int jx = 0;
        JsonArray array = data;
        for (JsonElement jsonElementPrincipal : array) {
            try {
                JsonObject jsonObject = jsonElementPrincipal.getAsJsonObject();
                JornadaBean j = new JornadaBean();
                j.setGrupoJornada(jsonObject.get("turno").getAsLong());
                j.setFechaInicial(sdf2.parse(jsonObject.get("fecha_inicio").getAsString()));
                j.setFechaFinal(sdf2.parse(jsonObject.get("fecha_fin").getAsString()));
                j.setCantidadVentas(jsonObject.get("numero_ventas").getAsInt());
                j.setTotalVentas(jsonObject.get("total_ventas").getAsFloat());
                lista.add(j);
                jx++;
            } catch (ParseException ex) {
                Logger.getLogger(ReportesHistorialTurnosViewConsolidados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void selecionar() {
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            NovusUtils.beep();
            
            // Obtener el ID de la fila seleccionada
            long idSeleccionado = (long) jTable1.getValueAt(r, 1);
            
            // Verificar si existe en la cola de impresión
            if (existeEnColaPendiente(idSeleccionado)) {
                // Si existe, mostrar IMPRIMIENDO... y bloquear
                bloquearBotonImprimir();
            } else {
                // Si no existe, mostrar IMPRIMIR y desbloquear
                desbloquearBotonImprimir();
            }
        } else {
            // Sin selección, mostrar botón deshabilitado
            NovusUtils.printLn("[CONSOLIDADO] No hay fila seleccionada - no se puede imprimir");
            jimprimir.setText("IMPRIMIR");
            jimprimir.setEnabled(true);
            jimprimir.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        }
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                // Ejecutar en el EDT para asegurar que el cambio de panel se refleje
                javax.swing.SwingUtilities.invokeLater(runnable);
            } catch (InterruptedException e) {
                Logger.getLogger(ReportesHistorialTurnosViewConsolidados.class.getName()).log(Level.SEVERE, null, e);
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
        pnl_container.add("card_mensajes", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));

        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        Async(runnable, 3);
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
        pnl_container.revalidate();
        pnl_container.repaint();
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
            Logger.getLogger(ReportesHistorialTurnosViewConsolidados.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

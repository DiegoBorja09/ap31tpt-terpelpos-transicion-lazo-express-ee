package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.inventario.PrintInventoryRemoteUseCase;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class InventarioProductosViewController extends javax.swing.JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    JFrame parent;
    boolean market = false;
    int numeroPOS = 1;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    DefaultTableCellRenderer textLeft;
    ArrayList<PersonaBean> personas;
    ArrayList<PersonaBean> personasCore;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    TreeMap<Long, Surtidor> caras = new TreeMap<>();
    ArrayList<MovimientosDetallesBean> productosRegistrados = new ArrayList<>();

    // Control de bloqueo de botones (solo en memoria)
    private boolean botonImprimirBloqueado = false;
    private boolean botonImprimirSaldoPositivoBloqueado = false;

    // Labels para mostrar "IMPRIMIENDO..." al lado de los botones
    private javax.swing.JLabel lblImprimiendoTodos = null;
    private javax.swing.JLabel lblImprimiendoSaldoPositivo = null;
    
    // Archivo de cola de impresión
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public InventarioProductosViewController(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    public InventarioProductosViewController(JFrame parent, boolean modal, boolean market) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.market = market;
        this.init();

    }

    private void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

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

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        NovusUtils.setUnsortableTable(this.jTable1);
        
        // Agregar listener para cuando se hace click en la tabla (en cualquier parte)
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Verificar cola de impresión al hacer click en cualquier parte de la tabla
                verificarColaYDesbloquear();
            }
        });
        
        numeroPOS = Main.getParametroIntCore(NovusConstante.PREFERENCE_POSID, false);
        verdetalles.setVisible(false);
        this.actualizarVista();
    }

    void actualizarVista() {
        this.renderizarDatosProductos();
    }

    void renderizarDatosProductos() {
        this.solicitarDatosProductos();
        jTable1.setAutoCreateRowSorter(true);
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        for (MovimientosDetallesBean producto : this.productosRegistrados) {
            try {
                defaultModel.addRow(
                        new Object[]{
                            producto.getPlu(), 
                            producto.getDescripcion(), 
                            producto.getSaldo()
                });
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
        }
        jTable1.setModel(defaultModel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        verdetalles = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        verdetalles.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        verdetalles.setForeground(new java.awt.Color(255, 255, 255));
        verdetalles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        verdetalles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        verdetalles.setText("VER DETALLES");
        verdetalles.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        verdetalles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                verdetallesMousePressed(evt);
            }
        });
        getContentPane().add(verdetalles);
        verdetalles.setBounds(500, 610, 180, 54);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jLabel6.setText("IMP.  SALDO POSITIVO");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel6MousePressed(evt);
            }
        });
        getContentPane().add(jLabel6);
        jLabel6.setBounds(700, 610, 270, 54);

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel5.setText("IMPRIMIR");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel5MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
        });
        getContentPane().add(jLabel5);
        jLabel5.setBounds(1010, 610, 180, 54);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PLU", "DESCRIPCION", "SALDO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setRowHeight(30);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(50, 120, 1140, 470);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("INVENTARIO PRODUCTOS");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(110, 0, 720, 90);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel26);
        jLabel26.setBounds(10, 710, 100, 80);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1281, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel5MouseExited

    private void verdetallesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_verdetallesMousePressed
        selectme();
    }//GEN-LAST:event_verdetallesMousePressed

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed
        if (jLabel5.isVisible() && !botonImprimirBloqueado) {
            NovusUtils.beep();
            imprimirInventarioProductos();
        }
    }// GEN-LAST:event_jLabel5MousePressed

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MousePressed
        if (jLabel6.isVisible() && !botonImprimirSaldoPositivoBloqueado) {
            NovusUtils.beep();
            imprimirInventarioProductosSaldoPositivo();
        }
    }// GEN-LAST:event_jLabel6MousePressed

    void imprimirInventarioProductosSaldoPositivo() {
        // ✅ VERIFICAR SI YA ESTÁ BLOQUEADO (prevenir doble clic)
        if (botonImprimirSaldoPositivoBloqueado) {
            NovusUtils.printLn("⚠️ Botón de impresión saldo positivo ya está bloqueado");
            return;
        }
        
        // Determinar tipo de inventario según el contexto
        String tipoInventario = market 
            ? NovusConstante.INVENTORY_TYPE_MARKET 
            : NovusConstante.INVENTORY_TYPE_CANASTILLA;
        
        // Formato: INVENTARIO_NEGOCIO_SALDO_POSITIVO (ej: INVENTARIO_CANASTILLA_SALDO_POSITIVO)
        String reportType = "INVENTARIO_" + tipoInventario + "_SALDO_POSITIVO";
        
        // Verificar si ya está en cola de impresión
        if (existeEnColaPendiente(reportType)) {
            NovusUtils.printLn("El registro ya está en cola de impresión - Tipo: " + reportType);
            // No desbloquear porque ya está en cola
            return;
        }
        
        // 🔒 BLOQUEAR BOTÓN INMEDIATAMENTE
        bloquearBotonImprimirSaldoPositivo();
        
        // Guardar en cola ANTES de verificar health check
        guardarRegistroPendiente(reportType);
        
        // 🧵 EJECUTAR EN THREAD SEPARADO
        new Thread(() -> {
            try {
                // 🔍 1. VERIFICAR SALUD DEL SERVICIO (usando caso de uso con cache)
                CheckPrintServiceHealthUseCase.HealthCheckResult healthResult;
                try {
                    healthResult = checkPrintServiceHealthUseCase.execute(null);
                } catch (Exception e) {
                    // Si hay excepción al verificar el servicio, asumir que no está disponible
                    NovusUtils.printLn("❌ Excepción al verificar servicio de impresión: " + e.getMessage());
                    e.printStackTrace();
                    eliminarRegistroPendiente(reportType);
                    final String mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        desbloquearBotonImprimirSaldoPositivo();
                        mostrarMensajeError(mensajeError);
                    });
                    return;
                }
                
                if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                    // ❌ Servicio no responde o no está saludable - eliminar registro de cola y desbloquear
                    eliminarRegistroPendiente(reportType);
                    String mensaje = healthResult.obtenerMensajeError();
                    
                    // Validar que el mensaje no sea null o vacío
                    if (mensaje == null || mensaje.trim().isEmpty()) {
                        mensaje = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                    }
                    
                    final String mensajeFinal = mensaje;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        try {
                            NovusUtils.printLn("❌ Servicio de impresión no está disponible: " + mensajeFinal);
                            NovusUtils.printLn("   - Tiene respuesta: " + healthResult.tieneRespuesta());
                            NovusUtils.printLn("   - Es saludable: " + healthResult.esSaludable());
                            desbloquearBotonImprimirSaldoPositivo();
                            mostrarMensajeError(mensajeFinal);
                        } catch (Exception e) {
                            NovusUtils.printLn("❌ Error mostrando mensaje de error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    return;
                }
                
                // ✅ 2. SERVICIO OK - PROCEDER CON IMPRESIÓN (mantener bloqueado hasta notificación)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    PrintInventoryRemoteUseCase.ResultadoImpresion resultado;
                    try {
                        PrintInventoryRemoteUseCase useCase = new PrintInventoryRemoteUseCase(
                            tipoInventario,
                            this.numeroPOS,
                            true,  // Solo saldo positivo
                            1      // 1 copia
                        );
                        
                        resultado = useCase.execute(null);
                        
                        // Mostrar mensaje de éxito cuando se envía correctamente el request
                        if (resultado.esExito()) {
                            NovusUtils.printLn("✅ Mostrando mensaje de éxito: " + resultado.getMensaje());
                            mostrarResultadoImpresion(resultado);
                        } else {
                            NovusUtils.printLn("⚠️ Resultado no exitoso: " + resultado.getMensaje());
                        }
                        
                        // NO desbloquear aquí - se desbloqueará cuando llegue la notificación
                        // El botón permanecerá bloqueado hasta que PaymentNotificationController
                        // elimine el registro de la cola
                        
                    } catch (Exception e) {
                        NovusUtils.printLn("Error imprimiendo inventario (saldo positivo): " + e.getMessage());
                        // Si hay error, eliminar de cola y desbloquear
                        eliminarRegistroPendiente(reportType);
                        desbloquearBotonImprimirSaldoPositivo();
                        resultado = PrintInventoryRemoteUseCase.ResultadoImpresion.error(
                            "No se pudo conectar al servicio de impresión");
                        mostrarResultadoImpresion(resultado);
                    }
                });
                
            } catch (Exception e) {
                Logger.getLogger(InventarioProductosViewController.class.getName())
                    .log(Level.SEVERE, null, e);
                eliminarRegistroPendiente(reportType);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimirSaldoPositivo();
                });
            }
        }).start();
    }

    ArrayList<MovimientosDetallesBean> getProductosConExistencia() {
        ArrayList<MovimientosDetallesBean> productos = new ArrayList<>();
        for (MovimientosDetallesBean producto : productosRegistrados) {
            if (producto.getSaldo() > 0) {
                productos.add(producto);
            }
        }
        return productos;
    }

    void imprimirInventarioProductos() {
        // ✅ VERIFICAR SI YA ESTÁ BLOQUEADO (prevenir doble clic)
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("⚠️ Botón de impresión ya está bloqueado");
            return;
        }
        
        // Determinar tipo de inventario según el contexto
        String tipoInventario = market 
            ? NovusConstante.INVENTORY_TYPE_MARKET 
            : NovusConstante.INVENTORY_TYPE_CANASTILLA;
        
        // Formato: INVENTARIO_NEGOCIO (ej: INVENTARIO_CANASTILLA, INVENTARIO_MARKET) - sin sufijo para impresión completa
        String reportType = "INVENTARIO_" + tipoInventario;
        
        // Verificar si ya está en cola de impresión
        if (existeEnColaPendiente(reportType)) {
            NovusUtils.printLn("El registro ya está en cola de impresión - Tipo: " + reportType);
            // No desbloquear porque ya está en cola
            return;
        }
        
        // 🔒 BLOQUEAR BOTÓN INMEDIATAMENTE
        bloquearBotonImprimirTodos();
        
        // Guardar en cola ANTES de verificar health check
        guardarRegistroPendiente(reportType);
        
        // 🧵 EJECUTAR EN THREAD SEPARADO
        new Thread(() -> {
            try {
                // 🔍 1. VERIFICAR SALUD DEL SERVICIO (usando caso de uso con cache)
                CheckPrintServiceHealthUseCase.HealthCheckResult healthResult;
                try {
                    healthResult = checkPrintServiceHealthUseCase.execute(null);
                } catch (Exception e) {
                    // Si hay excepción al verificar el servicio, asumir que no está disponible
                    NovusUtils.printLn("❌ Excepción al verificar servicio de impresión: " + e.getMessage());
                    e.printStackTrace();
                    eliminarRegistroPendiente(reportType);
                    final String mensajeError = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        desbloquearBotonImprimirTodos();
                        mostrarMensajeError(mensajeError);
                    });
                    return;
                }
                
                if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                    // ❌ Servicio no responde o no está saludable - eliminar registro de cola y desbloquear
                    eliminarRegistroPendiente(reportType);
                    String mensaje = healthResult.obtenerMensajeError();
                    
                    // Validar que el mensaje no sea null o vacío
                    if (mensaje == null || mensaje.trim().isEmpty()) {
                        mensaje = "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
                    }
                    
                    final String mensajeFinal = mensaje;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        try {
                            NovusUtils.printLn("❌ Servicio de impresión no está disponible: " + mensajeFinal);
                            NovusUtils.printLn("   - Tiene respuesta: " + healthResult.tieneRespuesta());
                            NovusUtils.printLn("   - Es saludable: " + healthResult.esSaludable());
                            desbloquearBotonImprimirTodos();
                            mostrarMensajeError(mensajeFinal);
                        } catch (Exception e) {
                            NovusUtils.printLn("❌ Error mostrando mensaje de error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    return;
                }
                
                // ✅ 2. SERVICIO OK - PROCEDER CON IMPRESIÓN (mantener bloqueado hasta notificación)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    PrintInventoryRemoteUseCase.ResultadoImpresion resultado;
                    try {
                        PrintInventoryRemoteUseCase useCase = new PrintInventoryRemoteUseCase(
                            tipoInventario,
                            this.numeroPOS,
                            false, // Imprimir todos los productos
                            1      // 1 copia
                        );
                        
                        resultado = useCase.execute(null);
                        
                        // Mostrar mensaje de éxito cuando se envía correctamente el request
                        if (resultado.esExito()) {
                            NovusUtils.printLn("✅ Mostrando mensaje de éxito: " + resultado.getMensaje());
                            mostrarResultadoImpresion(resultado);
                        } else {
                            NovusUtils.printLn("⚠️ Resultado no exitoso: " + resultado.getMensaje());
                        }
                        
                        // NO desbloquear aquí - se desbloqueará cuando llegue la notificación
                        // El botón permanecerá bloqueado hasta que PaymentNotificationController
                        // elimine el registro de la cola
                        
                    } catch (Exception e) {
                        NovusUtils.printLn("Error imprimiendo inventario: " + e.getMessage());
                        // Si hay error, eliminar de cola y desbloquear
                        eliminarRegistroPendiente(reportType);
                        desbloquearBotonImprimirTodos();
                        resultado = PrintInventoryRemoteUseCase.ResultadoImpresion.error(
                            "No se pudo conectar al servicio de impresión");
                        mostrarResultadoImpresion(resultado);
                    }
                });
                
            } catch (Exception e) {
                Logger.getLogger(InventarioProductosViewController.class.getName())
                    .log(Level.SEVERE, null, e);
                eliminarRegistroPendiente(reportType);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimirTodos();
                });
            }
        }).start();
    }

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel3MousePressed

    private void cerrar() {
        this.setVisible(false);
    }

    /**
     * Verifica si el registro está en cola y desbloquea los botones si no está
     */
    private void verificarColaYDesbloquear() {
        // Determinar tipo de inventario según el contexto
        String tipoInventario = market 
            ? NovusConstante.INVENTORY_TYPE_MARKET 
            : NovusConstante.INVENTORY_TYPE_CANASTILLA;
        
        // Verificar ambos tipos de report_type
        String reportTypeNormal = "INVENTARIO_" + tipoInventario;
        String reportTypeSaldoPositivo = "INVENTARIO_" + tipoInventario + "_SALDO_POSITIVO";
        
        // Verificar si está en cola de impresión
        boolean enColaNormal = existeEnColaPendiente(reportTypeNormal);
        boolean enColaSaldoPositivo = existeEnColaPendiente(reportTypeSaldoPositivo);
        
        // Desbloquear botones si no están en cola
        if (!enColaNormal && botonImprimirBloqueado) {
            desbloquearBotonImprimirTodos();
        }
        if (!enColaSaldoPositivo && botonImprimirSaldoPositivoBloqueado) {
            desbloquearBotonImprimirSaldoPositivo();
        }
        // Si está en cola, mantener bloqueado (no hacer nada)
    }
    
    private void selectme() {
        int row = jTable1.getSelectedRow();
        if (row > -1) {
            NovusUtils.beep();
            
            // Verificar cola de impresión al hacer click
            verificarColaYDesbloquear();
            
            String plu = ((String) jTable1.getValueAt(row, 0)).trim();
            DetalleProductoView producto = new DetalleProductoView(this.parent, true, plu, true);
            producto.setVisible(true);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel verdetalles;
    // End of variables declaration//GEN-END:variables

    void solicitarDatosProductos() {
        productosRegistrados.clear();
        
        try {
            if (!market) {
                try {
                    MovimientosDao mdao = new MovimientosDao();
                    productosRegistrados = mdao.findSummarizedProductsInfo();
                } catch (com.dao.DAOException | java.sql.SQLException e) {
                    System.err.println(" Error accediendo BD para productos no-market: " + e.getMessage());
                }
            } else {
                // USANDO CACHE para productos del market
                System.out.println(" Cargando productos del market con CACHE...");
                com.infrastructure.cache.KioscoCacheServiceLiviano cache = 
                    com.infrastructure.cache.KioscoCacheServiceLiviano.getInstance();
                productosRegistrados = (ArrayList<MovimientosDetallesBean>) 
                    cache.obtenerProductosPopularesConCache(1000); // Cargar hasta 1000 productos
            }
        } catch (Exception e) {
            System.err.println(" Error cargando productos: " + e.getMessage());
            // Fallback a método original si cache falla
            try {
                MovimientosDao mdao = new MovimientosDao();
                if (market) {
                    productosRegistrados = mdao.findSummarizedProductsInfoMarket();
                } else {
                    productosRegistrados = mdao.findSummarizedProductsInfo();
                }
                System.out.println(" Fallback a BD directo completado");
            } catch (com.dao.DAOException | java.sql.SQLException fallbackError) {
                System.err.println(" Error en fallback: " + fallbackError.getMessage());
            }
        }
    }

    /**
     * Muestra el resultado de la impresión utilizando el mismo estilo de mensajes
     * implementado para el historial de ventas.
     * Usa JDialog no modal para no bloquear la pantalla.
     */
    private void mostrarResultadoImpresion(PrintInventoryRemoteUseCase.ResultadoImpresion resultado) {
        try {
            PrintInventoryRemoteUseCase.ResultadoImpresion resultadoSeguro =
                    resultado != null ? resultado
                            : PrintInventoryRemoteUseCase.ResultadoImpresion.error("No se obtuvo respuesta del servicio de impresión");

            boolean exito = resultadoSeguro.esExito();
            String icono = exito ? "/com/firefuel/resources/btOk.png" : "/com/firefuel/resources/btBad.png";
            String mensaje = resultadoSeguro.getMensaje();

            NovusUtils.printLn("📢 Mostrando mensaje de impresión - Éxito: " + exito + ", Mensaje: " + mensaje);

            // Usar JDialog no modal para no bloquear la pantalla
            JDialog dialog = new JDialog(this, false);
            dialog.setUndecorated(true);
            dialog.setModal(false); // No bloquear la pantalla
            dialog.setAlwaysOnTop(true); // Mostrar sobre otras ventanas

            ParametrosMensajes parametrosMensajes = crearParametrosMensaje(mensaje, icono, dialog::dispose);

            JPanel panel = ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes);
            if (panel == null) {
                NovusUtils.printLn("❌ ERROR: Panel de mensaje es null");
                return;
            }
            
            if (panel.getParent() != null) {
                panel.getParent().remove(panel);
            }
            dialog.getContentPane().add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            NovusUtils.printLn("✅ Diálogo de mensaje mostrado correctamente");
        } catch (Exception e) {
            NovusUtils.printLn("❌ Error mostrando mensaje de impresión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ParametrosMensajes crearParametrosMensaje(String mensaje, String icono, Runnable runnable) {
        return new ParametrosMensajesBuilder()
                .setMsj(mensaje)
                .setRuta(icono)
                .setHabilitar(true)
                .setAutoclose(true)
                .setLetterCase(LetterCase.FIRST_UPPER_CASE)
                .setRunnable(runnable)
                .build();
    }

    // ========================================================================
    // MÉTODOS DE BLOQUEO/DESBLOQUEO DE BOTONES
    // ========================================================================

    /**
     * 🔒 Bloquear botón IMPRIMIR (jLabel5) - Muestra "IMPRIMIENDO..." en blanco y botón gris
     */
    private void bloquearBotonImprimirTodos() {
        botonImprimirBloqueado = true;
        
        // Cambiar icono a gris (bt-link-small es el icono gris)
        jLabel5.setIcon(new javax.swing.ImageIcon(
            getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        
        // Cambiar el texto del botón a "IMPRIMIENDO..." en blanco
        jLabel5.setText("IMPRIMIENDO...");
        jLabel5.setForeground(new java.awt.Color(255, 255, 255)); // Color blanco
        
        jLabel5.setVisible(true);
        
        // Forzar actualización inmediata de la UI
        jLabel5.repaint();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /**
     * 🔓 Desbloquear botón IMPRIMIR (jLabel5)
     */
    private void desbloquearBotonImprimirTodos() {
        botonImprimirBloqueado = false;
        
        // Restaurar icono original
        jLabel5.setIcon(new javax.swing.ImageIcon(
            getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        
        // Restaurar texto original
        jLabel5.setText("IMPRIMIR");
        jLabel5.setForeground(new java.awt.Color(255, 255, 255)); // Color blanco
        
        jLabel5.setVisible(true);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /**
     * 🔒 Bloquear botón IMP. SALDO POSITIVO (jLabel6) - Muestra "IMPRIMIENDO..." en blanco y botón gris
     */
    private void bloquearBotonImprimirSaldoPositivo() {
        botonImprimirSaldoPositivoBloqueado = true;
        
        // Cambiar icono a gris (bt-link-normal es el icono gris)
        jLabel6.setIcon(new javax.swing.ImageIcon(
            getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png")));
        
        // Cambiar el texto del botón a "IMPRIMIENDO..." en blanco
        jLabel6.setText("IMPRIMIENDO...");
        jLabel6.setForeground(new java.awt.Color(255, 255, 255)); // Color blanco
        
        jLabel6.setVisible(true);
        
        // Forzar actualización inmediata de la UI
        jLabel6.repaint();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /**
     * 🔓 Desbloquear botón IMP. SALDO POSITIVO (jLabel6)
     */
    private void desbloquearBotonImprimirSaldoPositivo() {
        botonImprimirSaldoPositivoBloqueado = false;
        
        // Restaurar icono original
        jLabel6.setIcon(new javax.swing.ImageIcon(
            getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png")));
        
        // Restaurar texto original
        jLabel6.setText("IMP.  SALDO POSITIVO");
        jLabel6.setForeground(new java.awt.Color(255, 255, 255)); // Color blanco
        
        jLabel6.setVisible(true);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    // ========================================================================
    // MÉTODOS DE VERIFICACIÓN DEL SERVICIO DE IMPRESIÓN
    // ========================================================================


    /**
     * 🚨 Método auxiliar para mostrar mensajes de error
     * Usa JDialog no modal para no bloquear la pantalla.
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarMensajeError(String mensaje) {
        try {
            // Validar mensaje
            if (mensaje == null || mensaje.trim().isEmpty()) {
                mensaje = "ERROR DESCONOCIDO EN SERVICIO DE IMPRESIÓN";
            }
            
            NovusUtils.printLn("📢 Mostrando mensaje de error: " + mensaje);
            
            // Usar JDialog no modal para no bloquear la pantalla
            JDialog dialog = new JDialog(this, false);
            dialog.setUndecorated(true);
            dialog.setModal(false); // No bloquear la pantalla
            dialog.setAlwaysOnTop(true); // Mostrar sobre otras ventanas
            
            ParametrosMensajes parametrosMensajes = crearParametrosMensaje(
                mensaje, 
                "/com/firefuel/resources/btBad.png", 
                dialog::dispose
            );
            
            JPanel panel = ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes);
            if (panel == null) {
                NovusUtils.printLn("❌ ERROR: Panel de mensaje de error es null");
                return;
            }
            
            if (panel.getParent() != null) {
                panel.getParent().remove(panel);
            }
            dialog.getContentPane().add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            NovusUtils.printLn("✅ Diálogo de error mostrado correctamente");
        } catch (Exception e) {
            NovusUtils.printLn("❌ Error mostrando mensaje de error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================================
    // MÉTODOS DE PERSISTENCIA EN ARCHIVO (COLA DE IMPRESIÓN)
    // ========================================================================

    /**
     * Verifica si un report_type existe en la cola de impresión pendiente
     * Para inventario, no usamos ID, solo report_type
     * @param reportType El tipo de reporte (INVENTARIO_CANASTILLA, INVENTARIO_MARKET, etc.)
     * @return true si existe en la cola, false si no existe
     */
    private synchronized boolean existeEnColaPendiente(String reportType) {
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
                        // Para inventario, solo verificamos report_type (no hay id)
                        if (registro.has("report_type") && 
                            registro.get("report_type").getAsString().equals(reportType)) {
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
     * Para inventario, solo se guarda report_type (no hay id)
     * @param reportType El tipo de reporte (INVENTARIO_CANASTILLA, INVENTARIO_MARKET, etc.)
     */
    private synchronized void guardarRegistroPendiente(String reportType) {
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

            // Verificar si el report_type ya existe en el array (doble verificación para evitar duplicados)
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("report_type") && 
                    registro.get("report_type").getAsString().equals(reportType)) {
                    NovusUtils.printLn("Registro ya existe en cola de impresión - Tipo: " + reportType + " (no se duplica)");
                    return; // No agregar si ya existe
                }
            }

            // Crear nuevo registro (sin id, solo report_type)
            JsonObject nuevoRegistro = new JsonObject();
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

            NovusUtils.printLn("Registro guardado en cola de impresión - Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(InventarioProductosViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Para inventario, se elimina por report_type (no hay id)
     * @param reportType El tipo de reporte
     */
    private synchronized void eliminarRegistroPendiente(String reportType) {
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
                // Para inventario, solo verificamos report_type
                if (registro.has("report_type") && 
                    registro.get("report_type").getAsString().equals(reportType)) {
                    encontrado = true;
                    NovusUtils.printLn("🗑️ Eliminando registro de cola de impresión - Tipo: " + reportType);
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
                NovusUtils.printLn("✅ Registro eliminado de cola de impresión - Tipo: " + reportType);
            } else {
                NovusUtils.printLn("⚠️ Registro no encontrado en cola de impresión - Tipo: " + reportType);
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(InventarioProductosViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}

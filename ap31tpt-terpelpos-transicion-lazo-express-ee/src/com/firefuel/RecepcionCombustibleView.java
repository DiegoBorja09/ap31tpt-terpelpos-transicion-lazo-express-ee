package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.sapConfiguracion.IsMasserUseCase;
import com.application.useCases.sutidores.ActualizarRecepcionCombustibleUseCase;
import com.application.useCases.sutidores.BorrarRecepcionUseCase;
import com.application.useCases.sutidores.GetRecepcionesUseCase;
import com.application.useCases.tanques.GetTanquesUseCase;
import com.application.useCases.sutidores.ObtenerCapacidadMaximaUseCase;
import com.bean.BodegaBean;
import com.bean.EntradasBean;
import com.bean.ProductoBean;
import com.bean.RecepcionBean;
import com.bean.entradaCombustible.EntradaCombustibleBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EntradaCombustibleDao.SapConfiguracionDao;
import com.dao.EquipoDao;
import com.dao.SurtidorDao;
import com.facade.sap.DividircantidadCombustible;
import com.facade.sap.RemisionesSAP;
import com.firefuel.components.panelesPersonalizados.BordesRedondos;
import com.firefuel.components.panelesPersonalizados.ComboBox;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.FocusManager;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import teclado.view.common.TecladoExtendidoGray;
import teclado.view.common.TecladoNumerico;
import teclado.view.common.TecladoNumericoGray;
import teclado.view.common.TecladoNumericoGrayConPunto;

public class RecepcionCombustibleView extends JDialog {

    String PANEL_RECEPCION_ACTUALES = "panelActuales";
    String PANEL_RECEPCION_DOCUMENTOS = "panelInformacionGeneral";
    String PANEL_RECEPCION_TANQUES = "panelSeleccionTanques";
    String PANEL_RECEPCION_ENTRADAS = "panelIngresoEntradasIniciales";
    String PANEL_RECEPCION_ENTRADAS_FINAL = "panelIngresoEntradasFinales";
    ObtenerCapacidadMaximaUseCase obtenerCapacidadMaximaUseCase;
    BorrarRecepcionUseCase borrarRecepcionUseCase;
    ActualizarRecepcionCombustibleUseCase actualizarRecepcionCombustibleUseCase;
    GetRecepcionesUseCase getRecepcionesUseCase;
    IsMasserUseCase isMasserUseCase;
    public static float MAXIMO_PERMITIDO_ALTURA;
    SurtidorDao dao = new SurtidorDao();
    private TableRowSorter<TableModel> tableRowSorter;

    EntradasBean entradaCombustible = new EntradasBean();
    LinkedList<BodegaBean> tanques = new LinkedList<>();
    ArrayList<RecepcionBean> recepciones;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    JsonObject jsonVeeder = new JsonObject();
    RemisionesSAP remisionesSAP = new RemisionesSAP(this);
    int solicitudesAveederRoot = 0;
    InfoViewController parent;
    int panelesIndex = -1;
    public TreeMap<Integer, BodegaBean> optionTanksCombo = new TreeMap<>();
    public TreeMap<Integer, ProductoBean> optionProductsCombo = new TreeMap<>();
    private TreeMap<Long, Integer> productosIndice = new TreeMap<>();
    boolean hayVeederRoot = false;
    long promotorId = 0;

    TreeMap<Long, JsonArray> aforos = new TreeMap<>();

    boolean finalizado = false;
    JsonObject jsonRegistro = new JsonObject();
    public RecepcionBean recepcion = null;
    boolean REQUIERE_DENSIDAD = false;
    EntradaCombustibleBean entradaCombustibleBean = null;
    Long numeroDeRemision;
    String delivery;
    public String panelAmostrar = "";
    public Runnable accion = null;

    boolean ejecutarVeeder = true;
    String[] paneles = {
        "panelInformacionGeneral",
        "panelSeleccionTanques",
        "panelIngresoEntradasIniciales",
        "panelIngresoEntradasFinales"
    };
    private boolean masser;
    public boolean manual = false;
    DividircantidadCombustible dividircantidadCombustible = new DividircantidadCombustible(this);
    Color activo = new Color(179, 15, 0);
    Color inactivo = new Color(227, 227, 232);

    public boolean modoRemisionActiva = false;
    public long productoRemisionId = -1;

    public RecepcionCombustibleView(InfoViewController parent, boolean modal, boolean hayVeederRoot) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.hayVeederRoot = hayVeederRoot;
        this.init();
    }

    JsonObject solicitarInformacionConsola(long numeroTanque) {
        JsonObject response = null;
        boolean DEGUG = true;
        ClientWSAsync ws = new ClientWSAsync("OBTENER TANQUES POR NUMERO",
                NovusConstante.SECURE_CENTRAL_POINT_OBTENER_MEDIDAS_VEEDER_ROOT_POR_NUMERO + "/" + numeroTanque,
                NovusConstante.GET,
                null,
                DEGUG,
                false);
        try {
            response = ws.esperaRespuesta();
            NovusUtils.printLn(response.toString());
            jsonVeeder = response;
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        this.solicitudesAveederRoot++;
        if (response == null && this.solicitudesAveederRoot < 2) {
            response = this.solicitarInformacionConsola(numeroTanque);
        } else if (response == null) {
            NovusUtils.setMensaje("ERROR LECTURAS VEEDER ROOT, ENTRADAS DEBE SER MANUAL", jLabel1);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jLabel1);
            });
        }
        return response;
    }

    void sinBorder(JTextField campo) {
        campo.setBorder(new BordesRedondos(new Color(70, 73, 75), 25, 15, 10, 15, 10));
    }

    void conBorder(JTextField campo) {
        campo.setBorder(new BordesRedondos(new Color(204, 0, 0), 25, 15, 10, 15, 10));
    }

    void init() {
        this.masser = isMasserUseCase.execute();
        jcombo_tanque.setUI(ComboBox.createUI(jcombo_tanque));
        jcombo_productos.setUI(ComboBox.createUI(jcombo_productos));
        jComboTanques.setUI(ComboBox.createUI(jComboTanques));
        btnModificar.setVisible(true);
        placheholder(jnro_orden, "NÚMERO DE ORDEN");
        placheholder(jplaca, "PLACA");
        placheholder(jcantidad, "CANTIDAD");
        placheholder(txtCantidad, "CANTIDAD");
//        lblContenedorDatosEntrada.setBorder(new BordesRedondos(new Color(204, 0, 0), 25));
//        jLabel3.setBorder(new BordesRedondos(new Color(204, 0, 0), 25));
        mostrarTecladoAlfa(true);
        mostrarTecladoNum1(true);
        mostrarTecladoNum2(true);
        setUi();
        setIUTable();
        estiloTabla();
        estilosContenedorTanque();
        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
            this.promotorId = Main.persona.getId();
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }
        jLabel14.setVisible(false);
        jLabel36.setVisible(false);
        jLabel43.setVisible(false);
        showPanel(PANEL_RECEPCION_DOCUMENTOS);
        this.cargarTanques();
        if (this.entradaCombustible.getFechaInicio() == null) {
            this.entradaCombustible.setFechaInicio(new Date());
        }

        jnro_orden.requestFocus();

        recepciones = getRecepcionesUseCase.execute();
        if (!recepciones.isEmpty()) {
            actualizarTabla(recepciones);
            showPanel(PANEL_RECEPCION_ACTUALES);
            lblRecepciones.setVisible(true);
            btnRecepciones.setVisible(true);
        } else {
            lblRecepciones.setVisible(false);
            btnRecepciones.setVisible(false);
        }
        lblFinalizar.setVisible(false);
        btnFinalizar.setVisible(false);

        EquipoDao edao = new EquipoDao();
        String requiere_densidad = edao.getParametroCore("recepcion_requiere_densidad");

        if (requiere_densidad != null && requiere_densidad.equals("S")) {
            REQUIERE_DENSIDAD = true;
            jLabel32.setVisible(true);
            jLabel30.setVisible(true);
            jrecibido.setVisible(true);
            jdensidad.setVisible(true);
            lblFinal.setVisible(true);
            btnAlFinal.setVisible(true);
            jLabel24.setVisible(true);
            btnAuto.setVisible(true);

        } else {
            jLabel32.setVisible(false);
            jLabel30.setVisible(false);
            jrecibido.setVisible(false);
            jdensidad.setVisible(false);
            btnAlFinal.setVisible(false);
            lblFinal.setVisible(false);
            jLabel24.setVisible(false);
            btnAuto.setVisible(false);
        }
        ContenedorTanquesDividido.setPreferredSize(new Dimension(570, 210));
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });
    }

    private void setUi() {
        lblCancelar.setForeground(new Color(179, 15, 0));
        btnCancelar.setBorder(new BordesRedondos(new Color(179, 15, 0), 20));
        lblCancelar1.setForeground(new Color(179, 15, 0));
        btnCancelar1.setBorder(new BordesRedondos(new Color(179, 15, 0), 20));
        lblModificar.setForeground(new Color(179, 15, 0));
        btnModificar.setBorder(new BordesRedondos(new Color(179, 15, 0), 20));
        lblCancelarConfirmacion.setForeground(new Color(179, 15, 0));
        btnCancelarConfirmacion.setBorder(new BordesRedondos(new Color(179, 15, 0), 20));
    }

    private void setIUTable() {
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        jInformacionDescargue.getColumnModel().getColumn(0).setPreferredWidth(250);
        jInformacionDescargue.getColumnModel().getColumn(1).setPreferredWidth(280);
        jInformacionDescargue.getColumnModel().getColumn(2).setPreferredWidth(100);
        jInformacionDescargue.getColumnModel().getColumn(3).setPreferredWidth(60);
        jInformacionDescargue.getColumnModel().getColumn(4).setPreferredWidth(200);
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jInformacionDescargue.setSelectionBackground(new Color(255, 182, 0));
        jInformacionDescargue.setSelectionForeground(new Color(0, 0, 0));
        jInformacionDescargue.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        jInformacionDescargue.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jInformacionDescargue.getModel().getColumnCount(); i++) {
            jInformacionDescargue.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jInformacionDescargue.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jInformacionDescargue.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jInformacionDescargue.setRowSorter(rowSorter);

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

    }

    public void actualizarTabla(ArrayList<RecepcionBean> recepciones) {
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        for (RecepcionBean operacion : recepciones) {

            for (BodegaBean tanque : tanques) {
                if (tanque.getId() == operacion.getTanqueId()) {
                    operacion.setTanqueDescripcion(tanque.toString());
                    break;
                }
            }

            for (BodegaBean tanque : tanques) {
                if (tanque.getId() == operacion.getTanqueId()) {
                    operacion.setTanqueDescripcion(tanque.toString());
                    break;
                }
            }

            String producto = "";
            if (operacion.getProductoDescripcion() != null) {
                producto = operacion.getProductoDescripcion().replace("GASOLINA ", "");
            }

            defaultModel.addRow(new Object[]{
                operacion.getId(),
                operacion.getPlaca(),
                operacion.getDocumento(),
                operacion.getTanqueDescripcion(),
                producto,
                "PENDIENTE"
            });
        }
    }

    int indexTrunc(int number, int limit) {
        return Math.max(0, Math.min(number, limit));
    }

    private void cargarTanques() {
        System.out.println("=== DEBUG: cargarTanques() iniciado ===");
        // EquipoDao edao = new EquipoDao();
        GetTanquesUseCase getTanquesUseCase = new GetTanquesUseCase();
        tanques.clear();
        tanques = getTanquesUseCase.execute();
        // tanques = edao.GetTanquesUseCase();
        
        System.out.println("Total de tanques obtenidos: " + tanques.size());
        
        int index = 0;
        if (!tanques.isEmpty()) {
            optionTanksCombo.clear();
            for (BodegaBean tanque : tanques) {
                System.out.println("Tanque " + index + ": ID=" + tanque.getId() + ", Descripción=" + tanque.getDescripcion() + ", Número=" + tanque.getNumeroStand());
                
                ArrayList<ProductoBean> productosTanque = tanque.getProductos();
                if (productosTanque != null) {
                    System.out.println("  - Productos del tanque: " + productosTanque.size());
                    for (ProductoBean producto : productosTanque) {
                        System.out.println("    * Producto: ID=" + producto.getId() + ", Descripción=" + producto.getDescripcion());
                    }
                } else {
                    System.out.println("  - ERROR: Lista de productos del tanque es NULL");
                }
                
                optionTanksCombo.put(index, tanque);
                index++;
            }
            System.out.println("Total de tanques agregados a optionTanksCombo: " + index);
            this.renderTanksSelect();
        } else {
            System.out.println("ERROR: No se obtuvieron tanques del use case");
        }
        System.out.println("=== DEBUG: cargarTanques() finalizado ===");
    }

    public void renderTanksSelect() {
        System.out.println("=== DEBUG: renderTanksSelect() iniciado ===");
        jcombo_tanque.removeAllItems();
        System.out.println("Total de tanques en optionTanksCombo para renderizar: " + optionTanksCombo.size());
        
        for (Map.Entry<Integer, BodegaBean> e : optionTanksCombo.entrySet()) {
            BodegaBean tank = e.getValue();
            System.out.println("Agregando tanque al combo: Índice=" + e.getKey() + ", Tanque=" + tank.toString());
            jcombo_tanque.addItem(tank);
        }
        System.out.println("Total de items en combo de tanques: " + jcombo_tanque.getItemCount());
        System.out.println("=== DEBUG: renderTanksSelect() finalizado ===");
    }

    JsonObject solicitarTanques() {
        JsonObject response = null;
        ClientWSAsync ws = new ClientWSAsync(
                "OBTENER TANQUES",
                NovusConstante.SECURE_CENTRAL_POINT_OBTENER_TANQUES,
                NovusConstante.GET, null, false, false);
        try {
            ws.start();
            ws.join();
            response = ws.getResponse();
            NovusUtils.printLn(response.toString());
        } catch (InterruptedException ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

    void cargarProductos() {
        System.out.println("=== DEBUG: cargarProductos() iniciado ===");
        jcombo_productos.removeAllItems();
        this.optionProductsCombo.clear();
        
        System.out.println("Total de tanques disponibles: " + tanques.size());
        System.out.println("Total de tanques en optionTanksCombo: " + this.optionTanksCombo.size());
        
        // 🔧 NUEVA LÓGICA: Verificar si hay producto SAP activo
        if (this.productoSAPActivo != null) {
            System.out.println("🔍 Producto SAP activo detectado: " + this.productoSAPActivo.getDescripcion());
            System.out.println("🔍 Solo mostrando producto SAP específico de la remisión");
            
            // Solo mostrar el producto SAP activo
            jcombo_productos.addItem(this.productoSAPActivo.getDescripcion().trim().toUpperCase());
            this.optionProductsCombo.put(0, this.productoSAPActivo);
            this.productosIndice.put(this.productoSAPActivo.getId(), 0);
            
            System.out.println("✅ Producto SAP agregado al combo: " + this.productoSAPActivo.getDescripcion());
        } else {
            System.out.println("🔍 No hay producto SAP activo, mostrando todos los productos del tanque");
            
            // Comportamiento original: mostrar todos los productos del tanque
            if (!tanques.isEmpty()) {
                int selectedTankIndex = jcombo_tanque.getSelectedIndex();
                System.out.println("Índice del tanque seleccionado: " + selectedTankIndex);
                
                if (selectedTankIndex > -1) {
                    BodegaBean tanqueSeleccionado = this.optionTanksCombo.get(selectedTankIndex);
                    System.out.println("Tanque seleccionado: " + (tanqueSeleccionado != null ? tanqueSeleccionado.toString() : "NULL"));
                    
                    ArrayList<ProductoBean> productsTankSelected = this.optionTanksCombo.containsKey(selectedTankIndex)
                            ? this.optionTanksCombo.get(selectedTankIndex).getProductos()
                            : null;
                    
                    System.out.println("Productos del tanque seleccionado: " + (productsTankSelected != null ? productsTankSelected.size() : "NULL"));
                    
                    if (productsTankSelected != null) {
                        int index = 0;
                        this.productosIndice.clear();
                        for (ProductoBean product : productsTankSelected) {
                            System.out.println("Agregando producto al combo: ID=" + product.getId() + ", Descripción=" + product.getDescripcion());
                            jcombo_productos.addItem(product.getDescripcion().trim().toUpperCase());
                            this.optionProductsCombo.put(index, product);
                            this.productosIndice.put(product.getId(), index);
                            index++;
                        }
                        System.out.println("Total de productos agregados al combo: " + index);
                    } else {
                        System.out.println("ERROR: productsTankSelected es NULL - No hay productos para el tanque seleccionado");
                    }
                } else {
                    System.out.println("ERROR: No hay tanque seleccionado (selectedTankIndex = -1)");
                }
            } else {
                System.out.println("ERROR: Lista de tanques está vacía");
            }
        }
        System.out.println("=== DEBUG: cargarProductos() finalizado ===");
    }

    private void mostrarTecladoPlaca(boolean mostrar) {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel1;
        teclado.habilitarAlfanumeric(true);
        teclado.activarTeclasSoloPlaca(false);
        teclado.habilitarPorcentaje(false);
    }

    private void mostrarTecladoAlfa(boolean mostrar) {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel1;
        teclado.habilitarAlfanumeric(false);
        teclado.habilitarPunto(false);
    }

    void mostrarTecladoNum1(boolean mostrar) {
        jPanel2.setVisible(mostrar);
    }

    void mostrarTecladoNum2(boolean mostrar) {
        jPanel3.setVisible(mostrar);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        obtenerCapacidadMaximaUseCase = new ObtenerCapacidadMaximaUseCase();
        borrarRecepcionUseCase = new BorrarRecepcionUseCase();
        actualizarRecepcionCombustibleUseCase = new ActualizarRecepcionCombustibleUseCase();
        getRecepcionesUseCase = new GetRecepcionesUseCase();
        isMasserUseCase = new IsMasserUseCase();

        buttonGroup1 = new javax.swing.ButtonGroup();
        jclock = ClockViewController.getInstance();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        panelMenu = new javax.swing.JPanel();
        panelEntradasActuales = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTitle1 = new javax.swing.JLabel();
        btnNueva = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jNuevo = new javax.swing.JLabel();
        btnUsar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jUsar = new javax.swing.JLabel();
        panelInformacionGeneral = new javax.swing.JPanel();
        jPanel1 = new TecladoExtendidoGray();
        jLabelPlaca = new javax.swing.JLabel();
        jplaca = new javax.swing.JTextField();
        jLabelDocumento = new javax.swing.JLabel();
        jnro_orden = new javax.swing.JTextField();
        btnSiguiente = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblSiguiente = new javax.swing.JLabel();
        btnAlFinal = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblFinal = new javax.swing.JLabel();
        btnFinalizar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblFinalizar = new javax.swing.JLabel();
        btnRecepciones = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblRecepciones = new javax.swing.JLabel();
        pnlDetalleRemisionSAP = new javax.swing.JPanel();
        pnlProductos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jScrollPane2 = new javax.swing.JScrollPane();
        jInformacionDescargue = new javax.swing.JTable();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblSeleccionarTodo = new javax.swing.JLabel();
        lblTituloNoDocumentoSAP = new javax.swing.JLabel();
        lblNumeroDocumentoSAP = new javax.swing.JLabel();
        lblTituloFechaDocumento = new javax.swing.JLabel();
        lblFechaDocumento = new javax.swing.JLabel();
        lblTituloGuiaTransporte = new javax.swing.JLabel();
        lblGuiaTransporte = new javax.swing.JLabel();
        lblTituloCentroLogistico = new javax.swing.JLabel();
        lblCentroLogistico = new javax.swing.JLabel();
        lblTituloCentroOrigen = new javax.swing.JLabel();
        lblCentroOrigen = new javax.swing.JLabel();
        btnCancelar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblCancelar = new javax.swing.JLabel();
        btnContinuar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblContinuar = new javax.swing.JLabel();
        btnModificar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblModificar = new javax.swing.JLabel();
        lblTituloRemision = new javax.swing.JLabel();
        pnlDivisionProducto = new javax.swing.JPanel();
        panelRedondo3 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtCantidad = new javax.swing.JTextField();
        jComboTanques = new javax.swing.JComboBox<>();
        btnAgregar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblAgregar = new javax.swing.JLabel();
        ScrollContenedorTanqueDividido = new javax.swing.JScrollPane();
        ContenedorTanquesDividido = new javax.swing.JPanel();
        btnGuardar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblGuardar = new javax.swing.JLabel();
        lblCantidadInformacion = new javax.swing.JLabel();
        lblProductoInformacion = new javax.swing.JLabel();
        lblTituloCantidadTotal = new javax.swing.JLabel();
        lblCantidad = new javax.swing.JLabel();
        lbltitutloProducto = new javax.swing.JLabel();
        lblTanque = new javax.swing.JLabel();
        btnCancelar1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblCancelar1 = new javax.swing.JLabel();
        jPanel6 = new TecladoNumericoGray();
        panelSeleccionTanques = new javax.swing.JPanel();
        jPanel4 = new TecladoNumericoGray();
        jLabel22 = new javax.swing.JLabel();
        jcombo_tanque = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jcombo_productos = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jcantidad = new javax.swing.JTextField();
        btnAuto = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel24 = new javax.swing.JLabel();
        btnAnterior = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel23 = new javax.swing.JLabel();
        btnSiguienteProceso2 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel10 = new javax.swing.JLabel();
        panelIngresoEntradasIniciales = new javax.swing.JPanel();
        jPanel2 = new TecladoNumericoGrayConPunto() ;
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jaltura_inicial = new javax.swing.JTextField();
        jagua_inicial = new javax.swing.JTextField();
        jvolumen_inicial = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        btnIniciar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel15 = new javax.swing.JLabel();
        btnAnterior3 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLAnteriorEntradaInicial = new javax.swing.JLabel();
        panelIngresoEntradasFinales = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jrecibido = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jdensidad = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        JVisualTanque = new javax.swing.JLabel();
        jVisualVolMax = new javax.swing.JLabel();
        jPanel3 = new TecladoNumericoGrayConPunto();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jaltura_final = new javax.swing.JTextField();
        jagua_final = new javax.swing.JTextField();
        jvolumen_final = new javax.swing.JTextField();
        panelRedondo4 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel25 = new javax.swing.JLabel();
        panelRedondo2 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel16 = new javax.swing.JLabel();
        pnlNotificaciones = new javax.swing.JPanel();
        jIcono = new javax.swing.JLabel();
        jtext = new javax.swing.JLabel();
        btnCerrarModal = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel2 = new javax.swing.JLabel();
        pnlConfirmacion = new javax.swing.JPanel();
        panelRedondo5 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel5 = new javax.swing.JLabel();
        btnCancelarConfirmacion = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblCancelarConfirmacion = new javax.swing.JLabel();
        btnAceptarConfirmacion = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblAceptarConfirmacion = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setOpaque(false);
        jclock.setLayout(null);
        getContentPane().add(jclock);
        jclock.setBounds(1150, 710, 110, 80);

        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel5.setLayout(null);

        panelMenu.setBackground(new java.awt.Color(255, 255, 255));
        panelMenu.setName("panelMenu"); // NOI18N
        panelMenu.setOpaque(false);
        panelMenu.setLayout(new java.awt.CardLayout());

        panelEntradasActuales.setBackground(new java.awt.Color(255, 255, 255));
        panelEntradasActuales.setOpaque(false);
        panelEntradasActuales.setLayout(null);

        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ITEM", "PLACA", "DOCUMENTO", "TANQUE", "PRODUCTO", "ESTADO"
            }
        ));
        jTable1.setRowHeight(45);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(100);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(100);
            jTable1.getColumnModel().getColumn(1).setMinWidth(110);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(110);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(110);
            jTable1.getColumnModel().getColumn(2).setMinWidth(240);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(240);
            jTable1.getColumnModel().getColumn(2).setMaxWidth(240);
            jTable1.getColumnModel().getColumn(3).setMinWidth(300);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(3).setMaxWidth(300);
            jTable1.getColumnModel().getColumn(5).setMinWidth(80);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(80);
        }

        panelEntradasActuales.add(jScrollPane1);
        jScrollPane1.setBounds(16, 70, 1250, 450);

        jTitle1.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jTitle1.setForeground(new java.awt.Color(204, 0, 0));
        jTitle1.setText("...EN PROCESO");
        panelEntradasActuales.add(jTitle1);
        jTitle1.setBounds(20, 10, 650, 50);

        btnNueva.setBackground(new java.awt.Color(204, 0, 0));
        btnNueva.setRoundBottomLeft(20);
        btnNueva.setRoundBottomRight(20);
        btnNueva.setRoundTopLeft(20);
        btnNueva.setRoundTopRight(20);
        btnNueva.setLayout(null);

        jNuevo.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jNuevo.setForeground(new java.awt.Color(255, 255, 255));
        jNuevo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jNuevo.setText("NUEVA");
        jNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jNuevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jNuevoMouseReleased(evt);
            }
        });
        btnNueva.add(jNuevo);
        jNuevo.setBounds(0, 0, 250, 60);

        panelEntradasActuales.add(btnNueva);
        btnNueva.setBounds(740, 540, 250, 60);

        btnUsar.setBackground(new java.awt.Color(204, 0, 0));
        btnUsar.setRoundBottomLeft(20);
        btnUsar.setRoundBottomRight(20);
        btnUsar.setRoundTopLeft(20);
        btnUsar.setRoundTopRight(20);
        btnUsar.setLayout(null);

        jUsar.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jUsar.setForeground(new java.awt.Color(255, 255, 255));
        jUsar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jUsar.setText("USAR");
        jUsar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jUsar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jUsarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jUsarMouseReleased(evt);
            }
        });
        btnUsar.add(jUsar);
        jUsar.setBounds(0, 0, 250, 60);

        panelEntradasActuales.add(btnUsar);
        btnUsar.setBounds(1010, 540, 250, 60);

        panelMenu.add(panelEntradasActuales, "panelActuales");
        panelEntradasActuales.getAccessibleContext().setAccessibleName("panelActuales");

        panelInformacionGeneral.setBackground(new java.awt.Color(255, 255, 255));
        panelInformacionGeneral.setName("panelInformacionGeneral"); // NOI18N
        panelInformacionGeneral.setOpaque(false);
        panelInformacionGeneral.setLayout(null);
        panelInformacionGeneral.add(jPanel1);
        jPanel1.setBounds(120, 160, 1024, 336);

        jLabelPlaca.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        jLabelPlaca.setForeground(new java.awt.Color(186, 12, 47));
        jLabelPlaca.setText("PLACA");
        panelInformacionGeneral.add(jLabelPlaca);
        jLabelPlaca.setBounds(640, 10, 120, 50);

        jplaca.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        jplaca.setForeground(new java.awt.Color(0, 0, 0));
        jplaca.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jplaca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jplacaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jplacaFocusLost(evt);
            }
        });
        jplaca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jplacaMouseReleased(evt);
            }
        });
        jplaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jplacaActionPerformed(evt);
            }
        });
        jplaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jplacaKeyTyped(evt);
            }
        });
        panelInformacionGeneral.add(jplaca);
        jplaca.setBounds(640, 60, 470, 70);

        jLabelDocumento.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        jLabelDocumento.setForeground(new java.awt.Color(186, 12, 47));
        jLabelDocumento.setText("NÚMERO DE ORDEN");
        panelInformacionGeneral.add(jLabelDocumento);
        jLabelDocumento.setBounds(140, 10, 250, 50);

        jnro_orden.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        jnro_orden.setForeground(new java.awt.Color(0, 0, 0));
        jnro_orden.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jnro_orden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jnro_ordenFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jnro_ordenFocusLost(evt);
            }
        });
        jnro_orden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jnro_ordenMouseReleased(evt);
            }
        });
        jnro_orden.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jnro_ordenKeyTyped(evt);
            }
        });
        panelInformacionGeneral.add(jnro_orden);
        jnro_orden.setBounds(140, 60, 470, 70);

        btnSiguiente.setBackground(new java.awt.Color(228, 30, 19));
        btnSiguiente.setRoundBottomLeft(20);
        btnSiguiente.setRoundBottomRight(20);
        btnSiguiente.setRoundTopLeft(20);
        btnSiguiente.setRoundTopRight(20);
        btnSiguiente.setLayout(null);

        lblSiguiente.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        lblSiguiente.setForeground(new java.awt.Color(255, 255, 255));
        lblSiguiente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSiguiente.setText("SIGUIENTE");
        lblSiguiente.setToolTipText("");
        lblSiguiente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblSiguiente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblSiguienteMouseReleased(evt);
            }
        });
        btnSiguiente.add(lblSiguiente);
        lblSiguiente.setBounds(0, 0, 180, 60);

        panelInformacionGeneral.add(btnSiguiente);
        btnSiguiente.setBounds(930, 520, 180, 60);

        btnAlFinal.setBackground(new java.awt.Color(228, 30, 19));
        btnAlFinal.setRoundBottomLeft(20);
        btnAlFinal.setRoundBottomRight(20);
        btnAlFinal.setRoundTopLeft(20);
        btnAlFinal.setRoundTopRight(20);
        btnAlFinal.setLayout(null);

        lblFinal.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        lblFinal.setForeground(new java.awt.Color(255, 255, 255));
        lblFinal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFinal.setText("AL FINAL");
        lblFinal.setToolTipText("");
        lblFinal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblFinal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblFinalMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblFinalMouseReleased(evt);
            }
        });
        btnAlFinal.add(lblFinal);
        lblFinal.setBounds(0, 0, 180, 60);

        panelInformacionGeneral.add(btnAlFinal);
        btnAlFinal.setBounds(670, 520, 180, 60);

        btnFinalizar.setBackground(new java.awt.Color(228, 30, 19));
        btnFinalizar.setRoundBottomLeft(20);
        btnFinalizar.setRoundBottomRight(20);
        btnFinalizar.setRoundTopLeft(20);
        btnFinalizar.setRoundTopRight(20);
        btnFinalizar.setLayout(null);

        lblFinalizar.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        lblFinalizar.setForeground(new java.awt.Color(255, 255, 255));
        lblFinalizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFinalizar.setText("FINALIZAR");
        lblFinalizar.setToolTipText("");
        lblFinalizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblFinalizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblFinalizarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblFinalizarMouseReleased(evt);
            }
        });
        btnFinalizar.add(lblFinalizar);
        lblFinalizar.setBounds(0, 0, 180, 60);

        panelInformacionGeneral.add(btnFinalizar);
        btnFinalizar.setBounds(400, 520, 180, 60);

        btnRecepciones.setBackground(new java.awt.Color(228, 30, 19));
        btnRecepciones.setRoundBottomLeft(20);
        btnRecepciones.setRoundBottomRight(20);
        btnRecepciones.setRoundTopLeft(20);
        btnRecepciones.setRoundTopRight(20);
        btnRecepciones.setLayout(null);

        lblRecepciones.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        lblRecepciones.setForeground(new java.awt.Color(255, 255, 255));
        lblRecepciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRecepciones.setText("RECEPCIONES");
        lblRecepciones.setToolTipText("");
        lblRecepciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblRecepciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblRecepcionesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblRecepcionesMouseReleased(evt);
            }
        });
        btnRecepciones.add(lblRecepciones);
        lblRecepciones.setBounds(0, 0, 180, 60);

        panelInformacionGeneral.add(btnRecepciones);
        btnRecepciones.setBounds(130, 520, 180, 60);

        panelMenu.add(panelInformacionGeneral, "panelInformacionGeneral");
        panelInformacionGeneral.getAccessibleContext().setAccessibleName("panelInformacionGeneral");

        pnlDetalleRemisionSAP.setBackground(new java.awt.Color(220, 220, 220));
        pnlDetalleRemisionSAP.setLayout(null);

        pnlProductos.setBackground(new java.awt.Color(255, 255, 255));
        pnlProductos.setRoundBottomLeft(30);
        pnlProductos.setRoundBottomRight(30);
        pnlProductos.setRoundTopLeft(30);
        pnlProductos.setRoundTopRight(30);
        pnlProductos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jInformacionDescargue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TANQUE", "DESCRIPCIÓN", "CANTIDAD", "UNIDAD", "CODIGO PRODUCTO SAP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jInformacionDescargue.setRowHeight(50);
        jInformacionDescargue.setSelectionBackground(new java.awt.Color(255, 255, 0));
        jInformacionDescargue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jInformacionDescargueMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jInformacionDescargue);

        pnlProductos.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 960, 320));

        panelRedondo1.setBackground(new java.awt.Color(179, 15, 0));
        panelRedondo1.setRoundBottomLeft(20);
        panelRedondo1.setRoundBottomRight(20);
        panelRedondo1.setRoundTopLeft(20);
        panelRedondo1.setRoundTopRight(20);
        panelRedondo1.setLayout(null);

        lblSeleccionarTodo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblSeleccionarTodo.setForeground(new java.awt.Color(255, 255, 255));
        lblSeleccionarTodo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSeleccionarTodo.setText("SELECCIONAR TODO");
        lblSeleccionarTodo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSeleccionarTodoMouseClicked(evt);
            }
        });
        panelRedondo1.add(lblSeleccionarTodo);
        lblSeleccionarTodo.setBounds(0, 0, 400, 60);

        pnlProductos.add(panelRedondo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 350, 400, 60));

        lblTituloNoDocumentoSAP.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        lblTituloNoDocumentoSAP.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloNoDocumentoSAP.setText("No. Documento SAP");
        pnlProductos.add(lblTituloNoDocumentoSAP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 230, 40));

        lblNumeroDocumentoSAP.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblNumeroDocumentoSAP.setForeground(new java.awt.Color(51, 51, 51));
        lblNumeroDocumentoSAP.setText("10238343789823");
        pnlProductos.add(lblNumeroDocumentoSAP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 250, 30));

        lblTituloFechaDocumento.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        lblTituloFechaDocumento.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloFechaDocumento.setText("Fecha Documento");
        pnlProductos.add(lblTituloFechaDocumento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 230, 40));

        lblFechaDocumento.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblFechaDocumento.setForeground(new java.awt.Color(51, 51, 51));
        lblFechaDocumento.setText("2023-09-18");
        pnlProductos.add(lblFechaDocumento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 250, 30));

        lblTituloGuiaTransporte.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        lblTituloGuiaTransporte.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloGuiaTransporte.setText("No. Guía Transporte");
        pnlProductos.add(lblTituloGuiaTransporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 230, -1));

        lblGuiaTransporte.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblGuiaTransporte.setForeground(new java.awt.Color(51, 51, 51));
        lblGuiaTransporte.setText("12830192389123");
        pnlProductos.add(lblGuiaTransporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 250, -1));

        lblTituloCentroLogistico.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        lblTituloCentroLogistico.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloCentroLogistico.setText("Centro logístico destino");
        pnlProductos.add(lblTituloCentroLogistico, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 225, 240, 30));

        lblCentroLogistico.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblCentroLogistico.setForeground(new java.awt.Color(51, 51, 51));
        lblCentroLogistico.setText("Devices and technology");
        pnlProductos.add(lblCentroLogistico, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 245, 250, 30));

        lblTituloCentroOrigen.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        lblTituloCentroOrigen.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloCentroOrigen.setText("Centro origen");
        pnlProductos.add(lblTituloCentroOrigen, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 285, 230, 30));

        lblCentroOrigen.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblCentroOrigen.setForeground(new java.awt.Color(51, 51, 51));
        lblCentroOrigen.setText("Devices and technology");
        pnlProductos.add(lblCentroOrigen, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 250, 30));

        btnCancelar.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelar.setRoundBottomLeft(20);
        btnCancelar.setRoundBottomRight(20);
        btnCancelar.setRoundTopLeft(20);
        btnCancelar.setRoundTopRight(20);
        btnCancelar.setLayout(null);

        lblCancelar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblCancelar.setForeground(new java.awt.Color(51, 51, 51));
        lblCancelar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCancelar.setText("CANCELAR");
        lblCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCancelarMouseClicked(evt);
            }
        });
        btnCancelar.add(lblCancelar);
        lblCancelar.setBounds(0, 0, 250, 60);

        pnlProductos.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 425, 250, 60));

        btnContinuar.setBackground(new java.awt.Color(179, 15, 0));
        btnContinuar.setRoundBottomLeft(20);
        btnContinuar.setRoundBottomRight(20);
        btnContinuar.setRoundTopLeft(20);
        btnContinuar.setRoundTopRight(20);
        btnContinuar.setLayout(null);

        lblContinuar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblContinuar.setForeground(new java.awt.Color(255, 255, 255));
        lblContinuar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblContinuar.setText("CONTINUAR");
        lblContinuar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblContinuarMouseClicked(evt);
            }
        });
        btnContinuar.add(lblContinuar);
        lblContinuar.setBounds(0, 0, 250, 60);

        pnlProductos.add(btnContinuar, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 425, 250, 60));

        btnModificar.setBackground(new java.awt.Color(255, 255, 255));
        btnModificar.setRoundBottomLeft(20);
        btnModificar.setRoundBottomRight(20);
        btnModificar.setRoundTopLeft(20);
        btnModificar.setRoundTopRight(20);
        btnModificar.setLayout(null);

        lblModificar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblModificar.setForeground(new java.awt.Color(51, 51, 51));
        lblModificar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModificar.setText("MODIFICAR");
        lblModificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblModificarMouseClicked(evt);
            }
        });
        btnModificar.add(lblModificar);
        lblModificar.setBounds(0, 0, 250, 60);

        pnlProductos.add(btnModificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 425, 250, 60));

        pnlDetalleRemisionSAP.add(pnlProductos);
        pnlProductos.setBounds(30, 80, 1240, 500);

        lblTituloRemision.setBackground(new java.awt.Color(51, 51, 51));
        lblTituloRemision.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTituloRemision.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloRemision.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloRemision.setText("DETALLE DE N° DE ORDEN 10238343789823");
        pnlDetalleRemisionSAP.add(lblTituloRemision);
        lblTituloRemision.setBounds(30, 30, 1230, 40);

        panelMenu.add(pnlDetalleRemisionSAP, "detalleSAP");

        pnlDivisionProducto.setBackground(new java.awt.Color(220, 220, 220));
        pnlDivisionProducto.setLayout(null);

        panelRedondo3.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo3.setRoundBottomLeft(30);
        panelRedondo3.setRoundBottomRight(30);
        panelRedondo3.setRoundTopLeft(30);
        panelRedondo3.setRoundTopRight(30);
        panelRedondo3.setLayout(null);

        txtCantidad.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtCantidad.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadKeyTyped(evt);
            }
        });
        panelRedondo3.add(txtCantidad);
        txtCantidad.setBounds(20, 240, 470, 60);

        jComboTanques.setBackground(new java.awt.Color(255, 255, 255));
        jComboTanques.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jComboTanques.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboTanques.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        panelRedondo3.add(jComboTanques);
        jComboTanques.setBounds(20, 130, 610, 60);

        btnAgregar.setBackground(new java.awt.Color(179, 15, 0));
        btnAgregar.setRoundBottomLeft(30);
        btnAgregar.setRoundBottomRight(30);
        btnAgregar.setRoundTopLeft(30);
        btnAgregar.setRoundTopRight(30);
        btnAgregar.setLayout(null);

        lblAgregar.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblAgregar.setForeground(new java.awt.Color(255, 255, 255));
        lblAgregar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAgregar.setText("+");
        lblAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAgregarMouseClicked(evt);
            }
        });
        btnAgregar.add(lblAgregar);
        lblAgregar.setBounds(0, 0, 130, 60);

        panelRedondo3.add(btnAgregar);
        btnAgregar.setBounds(500, 240, 130, 60);

        ScrollContenedorTanqueDividido.setBorder(null);
        ScrollContenedorTanqueDividido.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        ContenedorTanquesDividido.setBackground(new java.awt.Color(255, 255, 255));
        ContenedorTanquesDividido.setMinimumSize(new java.awt.Dimension(10, 290));
        ScrollContenedorTanqueDividido.setViewportView(ContenedorTanquesDividido);

        panelRedondo3.add(ScrollContenedorTanqueDividido);
        ScrollContenedorTanqueDividido.setBounds(20, 310, 610, 210);

        btnGuardar.setBackground(new java.awt.Color(179, 15, 0));
        btnGuardar.setRoundBottomLeft(30);
        btnGuardar.setRoundBottomRight(30);
        btnGuardar.setRoundTopLeft(30);
        btnGuardar.setRoundTopRight(30);
        btnGuardar.setLayout(null);

        lblGuardar.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblGuardar.setForeground(new java.awt.Color(255, 255, 255));
        lblGuardar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGuardar.setText("GUARDAR");
        lblGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblGuardarMouseClicked(evt);
            }
        });
        btnGuardar.add(lblGuardar);
        lblGuardar.setBounds(0, 0, 270, 60);

        panelRedondo3.add(btnGuardar);
        btnGuardar.setBounds(310, 530, 270, 60);

        lblCantidadInformacion.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblCantidadInformacion.setForeground(new java.awt.Color(51, 51, 51));
        lblCantidadInformacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCantidadInformacion.setText("5000 GL");
        panelRedondo3.add(lblCantidadInformacion);
        lblCantidadInformacion.setBounds(380, 50, 200, 30);

        lblProductoInformacion.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblProductoInformacion.setForeground(new java.awt.Color(51, 51, 51));
        lblProductoInformacion.setText("GASOLINA CORRIENTE AL 7% OXIGENADA");
        panelRedondo3.add(lblProductoInformacion);
        lblProductoInformacion.setBounds(20, 50, 360, 30);

        lblTituloCantidadTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTituloCantidadTotal.setForeground(new java.awt.Color(51, 51, 51));
        lblTituloCantidadTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTituloCantidadTotal.setText("CANTIDAD TOTAL");
        panelRedondo3.add(lblTituloCantidadTotal);
        lblTituloCantidadTotal.setBounds(380, 30, 200, 20);

        lblCantidad.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCantidad.setForeground(new java.awt.Color(51, 51, 51));
        lblCantidad.setText("CANTIDAD");
        panelRedondo3.add(lblCantidad);
        lblCantidad.setBounds(20, 210, 320, 30);

        lbltitutloProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbltitutloProducto.setForeground(new java.awt.Color(51, 51, 51));
        lbltitutloProducto.setText("PRODUCTO");
        panelRedondo3.add(lbltitutloProducto);
        lbltitutloProducto.setBounds(20, 30, 320, 25);

        lblTanque.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTanque.setForeground(new java.awt.Color(51, 51, 51));
        lblTanque.setText("TANQUE");
        panelRedondo3.add(lblTanque);
        lblTanque.setBounds(20, 100, 320, 30);

        btnCancelar1.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelar1.setRoundBottomLeft(30);
        btnCancelar1.setRoundBottomRight(30);
        btnCancelar1.setRoundTopLeft(30);
        btnCancelar1.setRoundTopRight(30);
        btnCancelar1.setLayout(null);

        lblCancelar1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblCancelar1.setForeground(new java.awt.Color(51, 51, 51));
        lblCancelar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCancelar1.setText("CANCELAR");
        lblCancelar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCancelar1MouseClicked(evt);
            }
        });
        btnCancelar1.add(lblCancelar1);
        lblCancelar1.setBounds(0, 0, 280, 60);

        panelRedondo3.add(btnCancelar1);
        btnCancelar1.setBounds(20, 530, 270, 60);

        pnlDivisionProducto.add(panelRedondo3);
        panelRedondo3.setBounds(30, 10, 650, 600);
        pnlDivisionProducto.add(jPanel6);
        jPanel6.setBounds(690, 60, 580, 510);

        panelMenu.add(pnlDivisionProducto, "pnlDivisionProductos");

        panelSeleccionTanques.setBackground(new java.awt.Color(255, 255, 255));
        panelSeleccionTanques.setName("panelSeleccionTanques"); // NOI18N
        panelSeleccionTanques.setOpaque(false);
        panelSeleccionTanques.setLayout(null);
        panelSeleccionTanques.add(jPanel4);
        jPanel4.setBounds(670, 70, 550, 470);

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("CANTIDAD A RECIBIR");
        panelSeleccionTanques.add(jLabel22);
        jLabel22.setBounds(190, 290, 280, 30);

        jcombo_tanque.setBackground(new java.awt.Color(255, 255, 255));
        jcombo_tanque.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jcombo_tanque.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jcombo_tanque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcombo_tanqueActionPerformed(evt);
            }
        });
        panelSeleccionTanques.add(jcombo_tanque);
        jcombo_tanque.setBounds(30, 70, 600, 70);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(186, 12, 47));
        jLabel9.setText("PRODUCTOS:");
        panelSeleccionTanques.add(jLabel9);
        jLabel9.setBounds(30, 150, 210, 30);

        jcombo_productos.setBackground(new java.awt.Color(255, 255, 255));
        jcombo_productos.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jcombo_productos.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        panelSeleccionTanques.add(jcombo_productos);
        jcombo_productos.setBounds(30, 190, 600, 70);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(186, 12, 47));
        jLabel12.setText("TANQUES:");
        panelSeleccionTanques.add(jLabel12);
        jLabel12.setBounds(30, 30, 210, 30);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader.gif"))); // NOI18N
        panelSeleccionTanques.add(jLabel14);
        jLabel14.setBounds(310, 400, 40, 60);

        jcantidad.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jcantidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jcantidad.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jcantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jcantidadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jcantidadFocusLost(evt);
            }
        });
        jcantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcantidadMouseReleased(evt);
            }
        });
        jcantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcantidadActionPerformed(evt);
            }
        });
        jcantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jcantidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jcantidadKeyTyped(evt);
            }
        });
        panelSeleccionTanques.add(jcantidad);
        jcantidad.setBounds(190, 330, 280, 60);

        btnAuto.setBackground(new java.awt.Color(204, 0, 0));
        btnAuto.setRoundBottomLeft(20);
        btnAuto.setRoundBottomRight(20);
        btnAuto.setRoundTopLeft(20);
        btnAuto.setRoundTopRight(20);
        btnAuto.setLayout(null);

        jLabel24.setBackground(new java.awt.Color(204, 0, 0));
        jLabel24.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("AUTO");
        jLabel24.setToolTipText("");
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel24MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel24MouseReleased(evt);
            }
        });
        btnAuto.add(jLabel24);
        jLabel24.setBounds(0, 0, 180, 60);

        panelSeleccionTanques.add(btnAuto);
        btnAuto.setBounds(230, 500, 180, 60);

        btnAnterior.setBackground(new java.awt.Color(204, 0, 0));
        btnAnterior.setRoundBottomLeft(20);
        btnAnterior.setRoundBottomRight(20);
        btnAnterior.setRoundTopLeft(20);
        btnAnterior.setRoundTopRight(20);
        btnAnterior.setLayout(null);

        jLabel23.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("ANTERIOR");
        jLabel23.setToolTipText("");
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel23MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel23MouseReleased(evt);
            }
        });
        btnAnterior.add(jLabel23);
        jLabel23.setBounds(0, 0, 180, 60);

        panelSeleccionTanques.add(btnAnterior);
        btnAnterior.setBounds(40, 500, 180, 60);

        btnSiguienteProceso2.setBackground(new java.awt.Color(204, 0, 0));
        btnSiguienteProceso2.setRoundBottomLeft(20);
        btnSiguienteProceso2.setRoundBottomRight(20);
        btnSiguienteProceso2.setRoundTopLeft(20);
        btnSiguienteProceso2.setRoundTopRight(20);
        btnSiguienteProceso2.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("SIGUIENTE");
        jLabel10.setToolTipText("");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel10MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        btnSiguienteProceso2.add(jLabel10);
        jLabel10.setBounds(0, 0, 180, 60);

        panelSeleccionTanques.add(btnSiguienteProceso2);
        btnSiguienteProceso2.setBounds(420, 500, 180, 60);

        panelMenu.add(panelSeleccionTanques, "panelSeleccionTanques");
        panelSeleccionTanques.getAccessibleContext().setAccessibleName("panelSeleccionTanques");

        panelIngresoEntradasIniciales.setBackground(new java.awt.Color(255, 255, 255));
        panelIngresoEntradasIniciales.setName("panelIngresoEntradasIniciales"); // NOI18N
        panelIngresoEntradasIniciales.setOpaque(false);
        panelIngresoEntradasIniciales.setLayout(null);
        panelIngresoEntradasIniciales.add(jPanel2);
        jPanel2.setBounds(660, 10, 550, 600);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(186, 12, 47));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("ALTURA INICIAL:");
        panelIngresoEntradasIniciales.add(jLabel4);
        jLabel4.setBounds(160, 40, 300, 40);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(186, 12, 47));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("VOLUMEN:");
        panelIngresoEntradasIniciales.add(jLabel11);
        jLabel11.setBounds(160, 160, 300, 30);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(186, 12, 47));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("ALTURA AGUA INICIAL:");
        panelIngresoEntradasIniciales.add(jLabel13);
        jLabel13.setBounds(160, 280, 300, 30);

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader.gif"))); // NOI18N
        panelIngresoEntradasIniciales.add(jLabel36);
        jLabel36.setBounds(300, 400, 50, 60);

        jaltura_inicial.setBackground(new java.awt.Color(255, 255, 255));
        jaltura_inicial.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jaltura_inicial.setForeground(new java.awt.Color(51, 51, 51));
        jaltura_inicial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jaltura_inicial.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jaltura_inicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jaltura_inicialFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jaltura_inicialFocusLost(evt);
            }
        });
        jaltura_inicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jaltura_inicialMouseReleased(evt);
            }
        });
        jaltura_inicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jaltura_inicialActionPerformed(evt);
            }
        });
        jaltura_inicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jaltura_inicialKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jaltura_inicialKeyTyped(evt);
            }
        });
        panelIngresoEntradasIniciales.add(jaltura_inicial);
        jaltura_inicial.setBounds(160, 90, 300, 60);

        jagua_inicial.setBackground(new java.awt.Color(255, 255, 255));
        jagua_inicial.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jagua_inicial.setForeground(new java.awt.Color(51, 51, 51));
        jagua_inicial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jagua_inicial.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jagua_inicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jagua_inicialFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jagua_inicialFocusLost(evt);
            }
        });
        jagua_inicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jagua_inicialMouseReleased(evt);
            }
        });
        jagua_inicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jagua_inicialActionPerformed(evt);
            }
        });
        jagua_inicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jagua_inicialKeyTyped(evt);
            }
        });
        panelIngresoEntradasIniciales.add(jagua_inicial);
        jagua_inicial.setBounds(160, 320, 300, 60);

        jvolumen_inicial.setBackground(new java.awt.Color(255, 255, 255));
        jvolumen_inicial.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jvolumen_inicial.setForeground(new java.awt.Color(255, 255, 255));
        jvolumen_inicial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jvolumen_inicial.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jvolumen_inicial.setFocusable(false);
        jvolumen_inicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jvolumen_inicialFocusGained(evt);
            }
        });
        jvolumen_inicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jvolumen_inicialMouseReleased(evt);
            }
        });
        jvolumen_inicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jvolumen_inicialActionPerformed(evt);
            }
        });
        jvolumen_inicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jvolumen_inicialKeyTyped(evt);
            }
        });
        panelIngresoEntradasIniciales.add(jvolumen_inicial);
        jvolumen_inicial.setBounds(160, 200, 300, 60);

        jLabel40.setBackground(new java.awt.Color(153, 0, 0));
        jLabel40.setFont(new java.awt.Font("Arial", 1, 120)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText(".");
        jLabel40.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel40.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel40MouseReleased(evt);
            }
        });
        panelIngresoEntradasIniciales.add(jLabel40);
        jLabel40.setBounds(870, 510, 120, 80);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/teclado_numerico_bnt_naranja_1.png"))); // NOI18N
        panelIngresoEntradasIniciales.add(jLabel44);
        jLabel44.setBounds(850, 510, 160, 80);

        jLabel39.setFont(new java.awt.Font("Arial", 0, 120)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/teclado_numerico_bg_1.png"))); // NOI18N
        jLabel39.setToolTipText("");
        jLabel39.setAlignmentY(0.0F);
        jLabel39.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel39MouseReleased(evt);
            }
        });
        panelIngresoEntradasIniciales.add(jLabel39);
        jLabel39.setBounds(850, 510, 160, 80);

        btnIniciar.setBackground(new java.awt.Color(204, 0, 0));
        btnIniciar.setRoundBottomLeft(20);
        btnIniciar.setRoundBottomRight(20);
        btnIniciar.setRoundTopLeft(20);
        btnIniciar.setRoundTopRight(20);
        btnIniciar.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("INICIAR");
        jLabel15.setToolTipText("");
        jLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel15MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel15MouseReleased(evt);
            }
        });
        btnIniciar.add(jLabel15);
        jLabel15.setBounds(0, 0, 180, 60);

        panelIngresoEntradasIniciales.add(btnIniciar);
        btnIniciar.setBounds(340, 500, 180, 60);

        btnAnterior3.setBackground(new java.awt.Color(204, 0, 0));
        btnAnterior3.setRoundBottomLeft(20);
        btnAnterior3.setRoundBottomRight(20);
        btnAnterior3.setRoundTopLeft(20);
        btnAnterior3.setRoundTopRight(20);
        btnAnterior3.setLayout(null);

        jLAnteriorEntradaInicial.setBackground(new java.awt.Color(204, 0, 0));
        jLAnteriorEntradaInicial.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLAnteriorEntradaInicial.setForeground(new java.awt.Color(255, 255, 255));
        jLAnteriorEntradaInicial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLAnteriorEntradaInicial.setText("ANTERIOR");
        jLAnteriorEntradaInicial.setToolTipText("");
        jLAnteriorEntradaInicial.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLAnteriorEntradaInicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLAnteriorEntradaInicialMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLAnteriorEntradaInicialMouseReleased(evt);
            }
        });
        btnAnterior3.add(jLAnteriorEntradaInicial);
        jLAnteriorEntradaInicial.setBounds(0, 0, 180, 60);

        panelIngresoEntradasIniciales.add(btnAnterior3);
        btnAnterior3.setBounds(90, 500, 180, 60);

        panelMenu.add(panelIngresoEntradasIniciales, "panelIngresoEntradasIniciales");
        panelIngresoEntradasIniciales.getAccessibleContext().setAccessibleName("panelIngresoEntradasIniciales");

        panelIngresoEntradasFinales.setBackground(new java.awt.Color(255, 255, 255));
        panelIngresoEntradasFinales.setName("panelIngresoEntradasFinales"); // NOI18N
        panelIngresoEntradasFinales.setOpaque(false);
        panelIngresoEntradasFinales.setLayout(null);

        jLabel34.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(153, 0, 0));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("CANTIDAD RECIBIDA");
        panelIngresoEntradasFinales.add(jLabel34);
        jLabel34.setBounds(50, 450, 180, 30);

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(186, 12, 47));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("RECIBIDO");
        panelIngresoEntradasFinales.add(jLabel32);
        jLabel32.setBounds(50, 360, 180, 29);

        jrecibido.setBackground(new java.awt.Color(255, 255, 255));
        jrecibido.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jrecibido.setForeground(new java.awt.Color(51, 51, 51));
        jrecibido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jrecibido.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jrecibido.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jrecibidoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jrecibidoFocusLost(evt);
            }
        });
        jrecibido.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jrecibidoMouseReleased(evt);
            }
        });
        jrecibido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrecibidoActionPerformed(evt);
            }
        });
        jrecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jrecibidoKeyTyped(evt);
            }
        });
        panelIngresoEntradasFinales.add(jrecibido);
        jrecibido.setBounds(50, 390, 180, 60);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(186, 12, 47));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("DENSIDAD");
        panelIngresoEntradasFinales.add(jLabel30);
        jLabel30.setBounds(250, 360, 180, 29);

        jdensidad.setBackground(new java.awt.Color(255, 255, 255));
        jdensidad.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jdensidad.setForeground(new java.awt.Color(51, 51, 51));
        jdensidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jdensidad.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jdensidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jdensidadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jdensidadFocusLost(evt);
            }
        });
        jdensidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jdensidadMouseReleased(evt);
            }
        });
        jdensidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdensidadActionPerformed(evt);
            }
        });
        jdensidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jdensidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jdensidadKeyTyped(evt);
            }
        });
        panelIngresoEntradasFinales.add(jdensidad);
        jdensidad.setBounds(250, 390, 180, 60);

        jLabel26.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("19-19-0000");
        panelIngresoEntradasFinales.add(jLabel26);
        jLabel26.setBounds(470, 420, 150, 20);

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader.gif"))); // NOI18N
        panelIngresoEntradasFinales.add(jLabel43);
        jLabel43.setBounds(530, 450, 40, 60);

        JVisualTanque.setBackground(new java.awt.Color(255, 204, 0));
        JVisualTanque.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        JVisualTanque.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JVisualTanque.setText("volumen");
        JVisualTanque.setToolTipText("");
        JVisualTanque.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        JVisualTanque.setOpaque(true);
        panelIngresoEntradasFinales.add(JVisualTanque);
        JVisualTanque.setBounds(485, 96, 131, 320);

        jVisualVolMax.setBackground(new java.awt.Color(255, 255, 255));
        jVisualVolMax.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        jVisualVolMax.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jVisualVolMax.setText("vol max");
        jVisualVolMax.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jVisualVolMax.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jVisualVolMax.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jVisualVolMax.setOpaque(true);
        jVisualVolMax.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        panelIngresoEntradasFinales.add(jVisualVolMax);
        jVisualVolMax.setBounds(480, 90, 140, 330);
        panelIngresoEntradasFinales.add(jPanel3);
        jPanel3.setBounds(660, 10, 550, 600);

        jLabel35.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelIngresoEntradasFinales.add(jLabel35);
        jLabel35.setBounds(60, 480, 160, 40);

        jLabel37.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelIngresoEntradasFinales.add(jLabel37);
        jLabel37.setBounds(260, 480, 160, 40);

        jLabel38.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(153, 0, 0));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("SEGUN DOCUMENTO");
        panelIngresoEntradasFinales.add(jLabel38);
        jLabel38.setBounds(250, 450, 180, 30);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("ALTURA FINAL:");
        panelIngresoEntradasFinales.add(jLabel6);
        jLabel6.setBounds(80, 60, 330, 30);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(186, 12, 47));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("VOLUMEN FINAL:");
        panelIngresoEntradasFinales.add(jLabel20);
        jLabel20.setBounds(80, 160, 330, 20);

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("AGUA ALTURA");
        panelIngresoEntradasFinales.add(jLabel21);
        jLabel21.setBounds(80, 260, 330, 30);

        jaltura_final.setBackground(new java.awt.Color(255, 255, 255));
        jaltura_final.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jaltura_final.setForeground(new java.awt.Color(51, 51, 51));
        jaltura_final.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jaltura_final.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jaltura_final.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jaltura_finalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jaltura_finalFocusLost(evt);
            }
        });
        jaltura_final.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jaltura_finalMouseReleased(evt);
            }
        });
        jaltura_final.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jaltura_finalActionPerformed(evt);
            }
        });
        jaltura_final.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jaltura_finalKeyTyped(evt);
            }
        });
        panelIngresoEntradasFinales.add(jaltura_final);
        jaltura_final.setBounds(80, 90, 330, 60);

        jagua_final.setBackground(new java.awt.Color(255, 255, 255));
        jagua_final.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jagua_final.setForeground(new java.awt.Color(51, 51, 51));
        jagua_final.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jagua_final.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jagua_final.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jagua_finalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jagua_finalFocusLost(evt);
            }
        });
        jagua_final.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jagua_finalMouseReleased(evt);
            }
        });
        jagua_final.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jagua_finalActionPerformed(evt);
            }
        });
        jagua_final.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jagua_finalKeyTyped(evt);
            }
        });
        panelIngresoEntradasFinales.add(jagua_final);
        jagua_final.setBounds(80, 290, 330, 60);

        jvolumen_final.setBackground(new java.awt.Color(255, 255, 255));
        jvolumen_final.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jvolumen_final.setForeground(new java.awt.Color(51, 51, 51));
        jvolumen_final.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jvolumen_final.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        jvolumen_final.setFocusable(false);
        jvolumen_final.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jvolumen_finalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jvolumen_finalFocusLost(evt);
            }
        });
        jvolumen_final.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jvolumen_finalMouseReleased(evt);
            }
        });
        jvolumen_final.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jvolumen_finalActionPerformed(evt);
            }
        });
        jvolumen_final.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jvolumen_finalKeyTyped(evt);
            }
        });
        panelIngresoEntradasFinales.add(jvolumen_final);
        jvolumen_final.setBounds(80, 190, 330, 60);

        panelRedondo4.setBackground(new java.awt.Color(204, 0, 0));
        panelRedondo4.setRoundBottomLeft(20);
        panelRedondo4.setRoundBottomRight(20);
        panelRedondo4.setRoundTopLeft(20);
        panelRedondo4.setRoundTopRight(20);
        panelRedondo4.setLayout(null);

        jLabel25.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("ANTERIOR");
        jLabel25.setToolTipText("");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel25MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel25MouseReleased(evt);
            }
        });
        panelRedondo4.add(jLabel25);
        jLabel25.setBounds(0, 0, 180, 60);

        panelIngresoEntradasFinales.add(panelRedondo4);
        panelRedondo4.setBounds(50, 530, 180, 60);

        panelRedondo2.setBackground(new java.awt.Color(204, 0, 0));
        panelRedondo2.setRoundBottomLeft(20);
        panelRedondo2.setRoundBottomRight(20);
        panelRedondo2.setRoundTopLeft(20);
        panelRedondo2.setRoundTopRight(20);
        panelRedondo2.setLayout(null);

        jLabel16.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("FINALIZAR");
        jLabel16.setToolTipText("");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel16MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel16MouseReleased(evt);
            }
        });
        panelRedondo2.add(jLabel16);
        jLabel16.setBounds(0, 0, 180, 60);

        panelIngresoEntradasFinales.add(panelRedondo2);
        panelRedondo2.setBounds(250, 530, 180, 60);

        panelMenu.add(panelIngresoEntradasFinales, "panelIngresoEntradasFinales");
        panelIngresoEntradasFinales.getAccessibleContext().setAccessibleName("panelIngresoEntradasFinales");

        pnlNotificaciones.setBackground(new java.awt.Color(255, 255, 255));
        pnlNotificaciones.setLayout(null);
        pnlNotificaciones.add(jIcono);
        jIcono.setBounds(80, 70, 380, 400);

        jtext.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jtext.setForeground(new java.awt.Color(204, 0, 0));
        jtext.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jtext.setText("Texto de prueba");
        pnlNotificaciones.add(jtext);
        jtext.setBounds(460, 70, 720, 400);

        btnCerrarModal.setBackground(new java.awt.Color(204, 0, 0));
        btnCerrarModal.setRoundBottomLeft(20);
        btnCerrarModal.setRoundBottomRight(20);
        btnCerrarModal.setRoundTopLeft(20);
        btnCerrarModal.setRoundTopRight(20);
        btnCerrarModal.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("CERRAR");
        jLabel2.setToolTipText("");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        btnCerrarModal.add(jLabel2);
        jLabel2.setBounds(0, 0, 340, 80);

        pnlNotificaciones.add(btnCerrarModal);
        btnCerrarModal.setBounds(870, 510, 340, 80);

        panelMenu.add(pnlNotificaciones, "mensajeNotificacion");

        pnlConfirmacion.setBackground(new java.awt.Color(227, 227, 232));
        pnlConfirmacion.setLayout(null);

        panelRedondo5.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo5.setRoundBottomLeft(30);
        panelRedondo5.setRoundBottomRight(30);
        panelRedondo5.setRoundTopLeft(30);
        panelRedondo5.setRoundTopRight(30);
        panelRedondo5.setLayout(null);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("¿ESTÁ SEGURO DE ESTA DISTRIBUCIÓN?");
        panelRedondo5.add(jLabel5);
        jLabel5.setBounds(0, 40, 760, 90);

        btnCancelarConfirmacion.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelarConfirmacion.setRoundBottomLeft(30);
        btnCancelarConfirmacion.setRoundBottomRight(30);
        btnCancelarConfirmacion.setRoundTopLeft(30);
        btnCancelarConfirmacion.setRoundTopRight(30);
        btnCancelarConfirmacion.setLayout(null);

        lblCancelarConfirmacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCancelarConfirmacion.setForeground(new java.awt.Color(179, 15, 0));
        lblCancelarConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCancelarConfirmacion.setText("CANCELAR");
        lblCancelarConfirmacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCancelarConfirmacionMouseClicked(evt);
            }
        });
        btnCancelarConfirmacion.add(lblCancelarConfirmacion);
        lblCancelarConfirmacion.setBounds(0, 0, 270, 60);

        panelRedondo5.add(btnCancelarConfirmacion);
        btnCancelarConfirmacion.setBounds(100, 210, 270, 60);

        btnAceptarConfirmacion.setBackground(new java.awt.Color(179, 15, 0));
        btnAceptarConfirmacion.setRoundBottomLeft(30);
        btnAceptarConfirmacion.setRoundBottomRight(30);
        btnAceptarConfirmacion.setRoundTopLeft(30);
        btnAceptarConfirmacion.setRoundTopRight(30);
        btnAceptarConfirmacion.setLayout(null);

        lblAceptarConfirmacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblAceptarConfirmacion.setForeground(new java.awt.Color(255, 255, 255));
        lblAceptarConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAceptarConfirmacion.setText("ACEPTAR");
        lblAceptarConfirmacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAceptarConfirmacionMouseClicked(evt);
            }
        });
        btnAceptarConfirmacion.add(lblAceptarConfirmacion);
        lblAceptarConfirmacion.setBounds(0, 0, 270, 60);

        panelRedondo5.add(btnAceptarConfirmacion);
        btnAceptarConfirmacion.setBounds(400, 210, 270, 60);

        pnlConfirmacion.add(panelRedondo5);
        panelRedondo5.setBounds(260, 160, 760, 300);

        panelMenu.add(pnlConfirmacion, "pnlConfirmacion");

        jPanel5.add(panelMenu);
        panelMenu.setBounds(0, 86, 1280, 615);
        panelMenu.getAccessibleContext().setAccessibleName("panelMenu");

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("RECEPCIÓN DE COMBUSTIBLE");
        jPanel5.add(jTitle);
        jTitle.setBounds(100, 5, 650, 80);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setToolTipText("");
        jPanel5.add(jLabel1);
        jLabel1.setBounds(150, 720, 920, 70);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel5.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        jPanel5.add(jpromotor);
        jpromotor.setBounds(820, 40, 460, 40);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        jPanel5.add(jLabel7);
        jLabel7.setBounds(20, 20, 48, 49);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        jPanel5.add(jpromotor1);
        jpromotor1.setBounds(820, 10, 460, 20);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel5.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel5.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel5.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jPanel5.add(background);
        background.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel5, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jnro_ordenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jnro_ordenKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jnro_orden, 10, jLabel1, caracteresAceptados);
    }//GEN-LAST:event_jnro_ordenKeyTyped

    private void jplacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jplacaKeyTyped
        String caracteresAceptados = "[a-zA-Z0-9]";
        NovusUtils.limitarCarateres(evt, jplaca, 10, jLabel1, caracteresAceptados);
    }//GEN-LAST:event_jplacaKeyTyped

    private void jcantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcantidadKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jcantidad, 8, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }

    }//GEN-LAST:event_jcantidadKeyTyped

    private void jaltura_inicialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jaltura_inicialKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jaltura_inicial, 8, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jaltura_inicialKeyTyped

    private void jaltura_inicialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jaltura_inicialFocusGained
        conBorder(jaltura_inicial);
        NovusUtils.deshabilitarCopiarPegar(jaltura_inicial);
    }//GEN-LAST:event_jaltura_inicialFocusGained

    private void jvolumen_inicialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jvolumen_inicialKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jvolumen_inicial, 8, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jvolumen_inicialKeyTyped

    private void jvolumen_inicialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jvolumen_inicialFocusGained
        NovusUtils.deshabilitarCopiarPegar(jvolumen_inicial);
    }//GEN-LAST:event_jvolumen_inicialFocusGained

    private void jagua_inicialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jagua_inicialKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jagua_inicial, 7, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jagua_inicialKeyTyped

    private void jagua_inicialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jagua_inicialFocusGained
        conBorder(jagua_inicial);
        NovusUtils.deshabilitarCopiarPegar(jagua_inicial);
    }//GEN-LAST:event_jagua_inicialFocusGained

    private void jaltura_finalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jaltura_finalKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jaltura_final, 8, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jaltura_finalKeyTyped

    private void jaltura_finalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jaltura_finalFocusGained
        conBorder(jaltura_final);
        NovusUtils.deshabilitarCopiarPegar(jaltura_final);
    }//GEN-LAST:event_jaltura_finalFocusGained

    private void jvolumen_finalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jvolumen_finalKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jvolumen_final, 7, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jvolumen_finalKeyTyped

    private void jvolumen_finalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jvolumen_finalFocusGained
        conBorder(jvolumen_final);
        NovusUtils.deshabilitarCopiarPegar(jvolumen_final);
    }//GEN-LAST:event_jvolumen_finalFocusGained

    private void jagua_finalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jagua_finalKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jagua_final, 7, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jagua_finalKeyTyped

    private void jagua_finalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jagua_finalFocusGained
        conBorder(jagua_final);
        NovusUtils.deshabilitarCopiarPegar(jagua_final);
    }//GEN-LAST:event_jagua_finalFocusGained

    private void jrecibidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jrecibidoKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jrecibido, 10, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jrecibidoKeyTyped

    private void jrecibidoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jrecibidoFocusGained
        conBorder(jrecibido);
        NovusUtils.deshabilitarCopiarPegar(jrecibido);
    }//GEN-LAST:event_jrecibidoFocusGained

    private void jdensidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jdensidadKeyTyped
        char c = evt.getKeyChar();
        String caracteresAceptados = "[0-9.]";
        if (Character.isDigit(c) || c == '.') {
            NovusUtils.limitarCarateres(evt, jdensidad, 10, jLabel1, caracteresAceptados);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_jdensidadKeyTyped

    private void jdensidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jdensidadFocusGained
        conBorder(jdensidad);
        NovusUtils.deshabilitarCopiarPegar(jdensidad);
    }//GEN-LAST:event_jdensidadFocusGained

    private void jaltura_inicialFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jaltura_inicialFocusLost
        sinBorder(jaltura_inicial);
    }//GEN-LAST:event_jaltura_inicialFocusLost

    private void jagua_inicialFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jagua_inicialFocusLost
        sinBorder(jagua_inicial);
    }//GEN-LAST:event_jagua_inicialFocusLost

    private void lblContinuarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblContinuarMouseClicked
        if (remisionesSAP.validarSeleccion()) {
            remisionesSAP.setEntrarOtravez(false);
            remisionesSAP.devolderEntradaArealizar(entradaCombustibleBean);
            remisionesSAP.continuar(entradaCombustibleBean, "");
        } else {
            remisionesSAP.mensajes("detalleSAP", "Seleccione una opción para continuar", "/com/firefuel/resources/btBad.png");
        }

    }//GEN-LAST:event_lblContinuarMouseClicked

    private void jaltura_finalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jaltura_finalFocusLost
        sinBorder(jaltura_final);
    }//GEN-LAST:event_jaltura_finalFocusLost

    private void jvolumen_finalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jvolumen_finalFocusLost
        sinBorder(jvolumen_final);
    }//GEN-LAST:event_jvolumen_finalFocusLost

    private void jagua_finalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jagua_finalFocusLost
        sinBorder(jagua_final);
    }//GEN-LAST:event_jagua_finalFocusLost

    private void jrecibidoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jrecibidoFocusLost
        sinBorder(jrecibido);
    }//GEN-LAST:event_jrecibidoFocusLost

    private void jdensidadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jdensidadFocusLost
        sinBorder(jdensidad);
    }//GEN-LAST:event_jdensidadFocusLost

    private void lblModificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModificarMouseClicked
        if (!btnModificar.getBackground().equals(inactivo)) {
            dividircantidadCombustible.seleccionProducto(entradaCombustibleBean);
            jTitle.setText("ASIGNACIÓN DE TANQUES");
            btnGuardar.setBackground(inactivo);
        }
    }//GEN-LAST:event_lblModificarMouseClicked

    private void lblSeleccionarTodoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSeleccionarTodoMouseClicked
        remisionesSAP.seleccionarTodo();
    }//GEN-LAST:event_lblSeleccionarTodoMouseClicked

    private void jInformacionDescargueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jInformacionDescargueMouseClicked
        selecciontabla();
    }//GEN-LAST:event_jInformacionDescargueMouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        cerrarMensaje();
        if (this.accion != null) {
            this.accion.run();
            this.accion = null;
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    private void lblCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCancelarMouseClicked
        // LIMPIAR MODO REMISIÓN AL CANCELAR

        System.out.println("[DEBUG] LIMPIANDO modo remisión al cancelar");
        
        showPanel(PANEL_RECEPCION_DOCUMENTOS);
    }//GEN-LAST:event_lblCancelarMouseClicked

    private void lblAgregarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarMouseClicked
        if (btnAgregar.getBackground().equals(activo)) {
            dividircantidadCombustible.agregarCantidadDividida(entradaCombustibleBean, this.promotorId);
        }
    }//GEN-LAST:event_lblAgregarMouseClicked

    private void lblGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGuardarMouseClicked
        if (btnGuardar.getBackground().equals(activo)) {
            showPanel("pnlConfirmacion");
        }
    }//GEN-LAST:event_lblGuardarMouseClicked

    private void lblCancelar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCancelar1MouseClicked
//        showPanel("detalleSAP");
        NovusUtils.showPanel(panelMenu, "detalleSAP");
        dividircantidadCombustible.limpiarTodo();
        jTitle.setText("RECEPCIÓN DE COMBUSTIBLE");
    }//GEN-LAST:event_lblCancelar1MouseClicked

    private void txtCantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyTyped
        String caracteresAceptados = "[0-9.]";
        NovusUtils.validacionesCampos(evt, txtCantidad, 5, jLabel1, caracteresAceptados, 24, 1, Color.WHITE);
        String texto = jLabel1.getText().toUpperCase();
        jLabel1.setText(texto);
    }//GEN-LAST:event_txtCantidadKeyTyped

    private void txtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyReleased
        dividircantidadCombustible.habilitarBoton(txtCantidad.getText());
    }//GEN-LAST:event_txtCantidadKeyReleased

    private void lblAceptarConfirmacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAceptarConfirmacionMouseClicked
        dividircantidadCombustible.guardarDivicion(entradaCombustibleBean.getDelivery());
    }//GEN-LAST:event_lblAceptarConfirmacionMouseClicked

    private void lblCancelarConfirmacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCancelarConfirmacionMouseClicked
        showPanel("pnlDivisionProductos");
    }//GEN-LAST:event_lblCancelarConfirmacionMouseClicked

    private void jplacaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jplacaActionPerformed
        siguiente();
    }// GEN-LAST:event_jplacaActionPerformed

    Matcher getValidator(String stringCompare, String stringRegex) {
        Pattern pat = Pattern.compile(stringRegex);
        Matcher mat = pat.matcher(stringCompare);
        return mat;
    }

    private void lblSiguienteMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jSiguienteMouseReleased
        siguiente();
    }// GEN-LAST:event_jSiguienteMouseReleased

    void seguirEntradaXX(String panel) {
        try {
            jvolumen_inicial.requestFocus();
            showPanel(panel);
            jLabel14.setVisible(false);
            btnAnterior.setBackground(new Color(204, 0, 0));
            jLabel10.setEnabled(true);
            jLabel23.setEnabled(true);
            btnSiguienteProceso2.setBackground(new Color(204, 0, 0));
            entradaCombustible.setProductoDetalle(this.obtenerProductoSeleccionado());
            jTitle.setText("Altura inicial tanque".toUpperCase() + " " + obtenerTanqueSeleccionado().getNumeroStand());
            if (recepcion != null) {
                jvolumen_inicial.setText(String.valueOf(recepcion.getVolumenInicial()));
                jaltura_inicial.setText(String.valueOf(recepcion.getAlturaInicial()));
                jagua_inicial.setText(String.valueOf(recepcion.getAguaInicial()));
            } else {
                if (this.hayVeederRoot) {
                    JsonObject response = solicitarInformacionConsola(obtenerTanqueSeleccionado().getNumeroStand());
                    if (response != null) {
                        entradaCombustible.setLecturasVeederIniciales(response);
                        NovusUtils.printLn("VOLUMEN: " + response.get("volumen").getAsFloat());
                        jvolumen_inicial.setText(response.get("volumen").getAsFloat() + "");
                        jaltura_inicial.setText(response.get("altura").getAsFloat() + "");
                        jagua_inicial.setText(response.get("agua").getAsFloat() + "");
                        bloquearCamposIniciales();
                    } else {
                        jvolumen_inicial.setEditable(false);
                        jvolumen_inicial.setBackground(new Color(153, 153, 153));
                        jvolumen_inicial.setForeground(new Color(0, 0, 0));
                    }
                } else {
                    jvolumen_inicial.setEditable(false);
                    jvolumen_inicial.setBackground(new Color(153, 153, 153));
                    jvolumen_inicial.setForeground(new Color(0, 0, 0));
                }
            }

//            guardarEstadoActual();
        } catch (NullPointerException ex) {
            Logger.getLogger(RecepcionCombustibleView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void bloquearCamposIniciales() {
        jvolumen_inicial.setEditable(false);
        jaltura_inicial.setEditable(false);
        jagua_inicial.setEditable(false);
        jvolumen_inicial.setBackground(new Color(153, 153, 153));
        jvolumen_inicial.setForeground(new Color(0, 0, 0));
        jaltura_inicial.setBackground(new Color(153, 153, 153));
        jaltura_inicial.setForeground(new Color(0, 0, 0));
        jagua_inicial.setBackground(new Color(153, 153, 153));
        jagua_inicial.setForeground(new Color(0, 0, 0));
    }

    void bloquearCamposFinal() {
        jvolumen_final.setEditable(false);
        jaltura_final.setEditable(false);
        jagua_final.setEditable(false);

        jvolumen_final.setBackground(new Color(153, 153, 153));
        jvolumen_final.setForeground(new Color(0, 0, 0));
        jaltura_final.setBackground(new Color(153, 153, 153));
        jaltura_final.setForeground(new Color(0, 0, 0));
        jagua_final.setBackground(new Color(153, 153, 153));
        jagua_final.setForeground(new Color(0, 0, 0));
    }

    private void jLabel10MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel10MousePressed

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MouseReleased
        if (jLabel10.isEnabled()) {
            siguienteIniciar();
        }
    }// GEN-LAST:event_jLabel10MouseReleased

    private void siguienteIniciar() {
        if (jcantidad.getText().trim().equals("")) {
            NovusUtils.setMensaje("DEBE RELLENAR TODOS LOS CAMPOS", jLabel1);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jLabel1);
            });
        } else {
            mostrarTecladoNum1(true);
            jLabel10.setEnabled(false);
            jLabel23.setEnabled(false);
            btnAnterior.setBackground(new Color(229, 230, 232));
            btnSiguienteProceso2.setBackground(new Color(229, 230, 232));
            float cantidad = Float.parseFloat(jcantidad.getText());
            boolean seguir = obtenerCapacidadMaxima((long) cantidad);
            if (seguir) {
                jLabel14.setVisible(true);
                if (recepcion != null) {
                    seguirEntradaXX(PANEL_RECEPCION_ENTRADAS);
                } else {
                    setTimeout(2, () -> seguirEntradaXX(PANEL_RECEPCION_ENTRADAS));
                }
            } else {
                jLabel10.setEnabled(true);
                btnSiguienteProceso2.setBackground(new Color(204, 0, 0));
                btnAnterior.setBackground(new Color(204, 0, 0));
                jLabel23.setEnabled(false);

            }
        }
    }

    public BodegaBean obtenerTanqueSeleccionado() {
        System.out.println("=== DEBUG: obtenerTanqueSeleccionado() ===");
        int selectedTankIndex = jcombo_tanque.getSelectedIndex();
        System.out.println("Índice del tanque seleccionado: " + selectedTankIndex);
        System.out.println("Total de tanques en optionTanksCombo: " + this.optionTanksCombo.size());
        
        if (selectedTankIndex > -1) {
            BodegaBean tank = this.optionTanksCombo.containsKey(selectedTankIndex)
                    ? this.optionTanksCombo.get(selectedTankIndex)
                    : null;
            if (tank != null) {
                System.out.println("Tanque seleccionado: ID=" + tank.getId() + ", Descripción=" + tank.getDescripcion() + ", Número=" + tank.getNumeroStand());
            } else {
                System.out.println("ERROR: Tanque seleccionado es NULL");
            }
            return tank;
        } else {
            System.out.println("ERROR: No hay tanque seleccionado (selectedTankIndex = -1)");
        }
        return null;
    }

    void registrarEntradas() {

    }

    private void jcombo_tanqueActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_tanqueActionPerformed
        System.out.println("=== EVENTO: Cambio en combo de tanques ===");
        System.out.println("Tanque seleccionado en el evento: " + jcombo_tanque.getSelectedItem());
        cargarProductos();
    }// GEN-LAST:event_jcombo_tanqueActionPerformed

    private void jnro_ordenMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jnro_ordenMouseReleased
        mostrarTecladoAlfa(true);
    }// GEN-LAST:event_jnro_ordenMouseReleased

    private void jplacaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jplacaMouseReleased
        mostrarTecladoPlaca(true);
    }// GEN-LAST:event_jplacaMouseReleased

    private void jaltura_inicialMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jaltura_inicialMouseReleased
        mostrarTecladoNum1(true);
    }// GEN-LAST:event_jaltura_inicialMouseReleased

    void cargarVolumenInicial() {

        if (!hayVeederRoot) {
            if (jaltura_inicial.getText().trim().equals("")) {
                jvolumen_inicial.setText("0");

            } else {
                float valor = Float.parseFloat(jaltura_inicial.getText().trim());
                jLabel36.setVisible(true);
                jaltura_inicial.setEnabled(false);
                Thread respuesta = new Thread() {

                    @Override
                    public void run() {

                        jvolumen_inicial.setText(cargarAforo(obtenerTanqueSeleccionado(), valor) + "");
                        jLabel36.setVisible(false);
                        jaltura_inicial.setEnabled(true);
//                         cargarFaseFinalRecepcion();
                    }
                };
                respuesta.start();

            }
        }
    }

    void cargarVolumenFinal() {

        if (!hayVeederRoot) {
            if (jaltura_final.getText().trim().equals("")) {
                jvolumen_final.setText("0");
            } else {
                float valor = Float.parseFloat(jaltura_final.getText().trim());
                jagua_final.setEnabled(false);
                jLabel43.setVisible(true);
                Thread respuestaf = new Thread() {

                    @Override
                    public void run() {
                        jvolumen_final.setText(cargarAforo(obtenerTanqueSeleccionado(), valor) + "");
                        jaltura_inicial.setEnabled(true);
                        jagua_final.setEnabled(true);
                        jLabel43.setVisible(false);
//                        registroEntradas();
                    }
                };
                respuestaf.start();
            }

        }
    }

    // TODO: Se debe tomar estos valores desde la db.
    JsonArray conseguirAforoTanque(BodegaBean tanque) {

        EquipoDao edao = new EquipoDao();
        JsonArray array = edao.getMedidaTanque(tanque);
        return array;

    }

    public float cargarAforo(BodegaBean tanque, float altura) {
        float cargarAforo = 0;
        JsonArray response;
        if (aforos.containsKey(tanque.getId())) {
            response = aforos.get(tanque.getId());
        } else {
            response = this.conseguirAforoTanque(tanque);
            aforos.put(tanque.getId(), response);
        }

        if (response != null) {
            boolean existe = false;

            for (JsonElement element : response) {
                JsonObject jsonAforo = element.getAsJsonObject();
                float alturaAforo = jsonAforo.get("altura_valor").getAsFloat();
                if (alturaAforo == altura) {
                    tanque.setGalonTanque(jsonAforo.get("cantidad_valor").getAsFloat());
                    cargarAforo = jsonAforo.get("cantidad_valor").getAsFloat();
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                cargarAforo = 1;
                float minimo = 0;
                float maximo = 0;
                float minimoCantidad = 0;
                float maximoCantidad = 0;

                for (JsonElement element : response) {
                    JsonObject jsonAforo = element.getAsJsonObject();
                    float alturaF = jsonAforo.get("altura_valor").getAsFloat();
                    if (alturaF > altura) {
                        break;
                    }
                    if (minimo < altura) {
                        minimo = jsonAforo.get("altura_valor").getAsFloat();
                        minimoCantidad = jsonAforo.get("cantidad_valor").getAsLong();
                    }

                }

                for (JsonElement element : response) {
                    JsonObject jsonAforo = element.getAsJsonObject();
                    float alturaF = jsonAforo.get("altura_valor").getAsFloat();
                    if (maximo == 0 && alturaF > altura) {
                        maximo = alturaF;
                        maximoCantidad = jsonAforo.get("cantidad_valor").getAsLong();
                        break;
                    }
                }

                NovusUtils.printLn("min:" + minimo + " " + "cant:" + minimoCantidad);
                NovusUtils.printLn("max:" + maximo + " " + "cant:" + maximoCantidad);

                float diferenciaCantidad = maximoCantidad - minimoCantidad;
                float diferenciaAltura = maximo - minimo;

                float nuevaAforo = diferenciaCantidad / diferenciaAltura;
                float rodamientoAltura = altura - minimo;
                float rodamientoGalone = rodamientoAltura * nuevaAforo;

                cargarAforo = rodamientoGalone + minimoCantidad;

                BigDecimal bigDecimal = new BigDecimal(cargarAforo).setScale(2, RoundingMode.UP);

                cargarAforo = bigDecimal.floatValue();
            }
        }
        return cargarAforo;
    }

    private void jaltura_inicialActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jaltura_inicialActionPerformed
        float altura = Float.parseFloat(jaltura_inicial.getText());
        boolean seguir = obtenerAltura(altura);
        if (seguir) {
            cargarVolumenInicial();
        }

    }// GEN-LAST:event_jaltura_inicialActionPerformed

    private void jagua_inicialMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jagua_inicialMouseReleased
        mostrarTecladoNum1(true);
    }// GEN-LAST:event_jagua_inicialMouseReleased

    private void jagua_inicialActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jagua_inicialActionPerformed
        cargarVolumenInicial();
        if (!jaltura_inicial.getText().trim().equals("")) {
            cargarFaseFinalRecepcion();
        }

    }// GEN-LAST:event_jagua_inicialActionPerformed

    private void jvolumen_inicialMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jvolumen_inicialMouseReleased
        mostrarTecladoNum1(true);
    }// GEN-LAST:event_jvolumen_inicialMouseReleased

    private void jvolumen_inicialActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jvolumen_inicialActionPerformed
        cargarVolumenInicial();
    }// GEN-LAST:event_jvolumen_inicialActionPerformed

    private void jLabel15MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel15MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel15MousePressed

    private void jLabel15MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel15MouseReleased
        if (jLabel15.isEnabled()) {
            cargarFaseFinalRecepcion();
        }

    }// GEN-LAST:event_jLabel15MouseReleased

    private void jLabel16MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel16MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel16MousePressed

    private void jLabel16MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel16MouseReleased
        if (jLabel16.isEnabled() && obtenerAltura(Float.parseFloat(jaltura_final.getText()))) {
            registroEntradas();
        }
    }// GEN-LAST:event_jLabel16MouseReleased

    private void jaltura_finalMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jaltura_finalMouseReleased
        mostrarTecladoNum2(true);
    }// GEN-LAST:event_jaltura_finalMouseReleased

    private void jaltura_finalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jaltura_finalActionPerformed
        float altura = Float.parseFloat(jaltura_final.getText());
        boolean seguir = obtenerAltura(altura);
        if (seguir) {
            cargarVolumenFinal();
        }
    }// GEN-LAST:event_jaltura_finalActionPerformed

    private void jagua_finalMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jagua_finalMouseReleased
        mostrarTecladoNum2(true);
    }// GEN-LAST:event_jagua_finalMouseReleased

    private void jagua_finalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jagua_finalActionPerformed
        cargarVolumenFinal();
        if (obtenerAltura(Float.parseFloat(jaltura_final.getText()))) {
            registroEntradas();
        }
    }// GEN-LAST:event_jagua_finalActionPerformed

    private void jvolumen_finalMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jvolumen_finalMouseReleased
        mostrarTecladoNum2(true);
    }// GEN-LAST:event_jvolumen_finalMouseReleased

    private void jvolumen_finalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jvolumen_finalActionPerformed
        float altura = Float.parseFloat(jvolumen_final.getText());
        boolean seguir = obtenerAltura(altura);
        if (seguir) {
            cargarVolumenFinal();
        }

    }// GEN-LAST:event_jvolumen_finalActionPerformed

    public void cerrar() {
        ejecutarVeeder = false;
        this.entradaCombustible.setFechaInicio(null);
        this.dispose();
    }

    private void jaltura_inicialKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jaltura_inicialKeyReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_jaltura_inicialKeyReleased

    private void jcantidadMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jcantidadMouseReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_jcantidadMouseReleased

    private void jcantidadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcantidadActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jcantidadActionPerformed

    private void jLabel23MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel23MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel23MousePressed

    private void jLabel23MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel23MouseReleased
        if (jLabel23.isEnabled()) {
            // Limpiar producto SAP activo y recargar tanques
            this.productoSAPActivo = null;
            cargarTanques();
            showPanel(PANEL_RECEPCION_DOCUMENTOS);
        }
    }// GEN-LAST:event_jLabel23MouseReleased

    private void jLAnteriorEntradaInicialMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLAnteriorEntradaInicialMousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLAnteriorEntradaInicialMousePressed

    private void jLAnteriorEntradaInicialMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLAnteriorEntradaInicialMouseReleased
        if (jLAnteriorEntradaInicial.isEnabled()) {
            jTitle.setText("RECEPCIÓN DE COMBUSTIBLE");
            showPanel(PANEL_RECEPCION_TANQUES);
            jcantidad.setVisible(true);
        }
    }// GEN-LAST:event_jLAnteriorEntradaInicialMouseReleased

    private void jLabel25MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel25MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel25MousePressed

    private void jLabel25MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel25MouseReleased
        if (jLabel25.isEnabled()) {
            showPanel(PANEL_RECEPCION_ENTRADAS);
        }
    }// GEN-LAST:event_jLabel25MouseReleased

    private void lblFinalMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFinalMousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jFinalMousePressed

    private void lblFinalMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFinalMouseReleased
        continuarSinPlaca();
    }// GEN-LAST:event_jFinalMouseReleased

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        if (jLabel7.isEnabled()) {
            cerrar();
        }
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jnro_ordenFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jnro_ordenFocusGained
        conBorder(jnro_orden);
        NovusUtils.deshabilitarCopiarPegar(jnro_orden);
    }// GEN-LAST:event_jnro_ordenFocusGained

    private void jnro_ordenFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jnro_ordenFocusLost
        sinBorder(jnro_orden);
    }// GEN-LAST:event_jnro_ordenFocusLost

    private void jplacaFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jplacaFocusGained
        conBorder(jplaca);
        deshabilitar();
        NovusUtils.deshabilitarCopiarPegar(jplaca);
    }// GEN-LAST:event_jplacaFocusGained

    private void jplacaFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jplacaFocusLost
        sinBorder(jplaca);
    }// GEN-LAST:event_jplacaFocusLost

    private void jUsarMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jUsarMousePressed
        usarRecepcion();
    }// GEN-LAST:event_jUsarMousePressed

    private void jNuevoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jNuevoMouseReleased
        // LIMPIAR MODO REMISIÓN AL INICIAR NUEVA RECEPCIÓN

        System.out.println("[DEBUG] LIMPIANDO modo remisión al iniciar nueva recepción");
        
        showPanel(PANEL_RECEPCION_DOCUMENTOS);
        jnro_orden.requestFocus();
    }// GEN-LAST:event_jNuevoMouseReleased

    private void jcantidadKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_jcantidadKeyReleased

    private void jUsarMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jUsarMouseReleased
        bloquearCampos();
        usarRecepcion();
    }// GEN-LAST:event_jUsarMouseReleased

    private void lblRecepcionesMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel33MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel33MousePressed

    private void lblRecepcionesMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel33MouseReleased
        showPanel(PANEL_RECEPCION_ACTUALES);
    }// GEN-LAST:event_jLabel33MouseReleased

    private void lblFinalizarMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFinal1MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jFinal1MousePressed

    private void lblFinalizarMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFinal1MouseReleased
        registroEntradas();
        if (jnro_orden.getText().trim().equals("") || jplaca.getText().trim().equals("")) {
            jLabel1.setText("Se requiere documento y placa para finalizar");
            jnro_orden.requestFocus();
        }
    }// GEN-LAST:event_jFinal1MouseReleased

    private void jLabel24MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel24MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel24MousePressed

    private void jLabel24MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel24MouseReleased
        if (jcantidad.getText().trim().equals("")) {
            jcantidad.setText("0");
            mostrarTecladoNum1(true);
            jLabel14.setVisible(true);
            if (recepcion != null) {
                seguirEntradaXX(PANEL_RECEPCION_ENTRADAS);
            } else {
                setTimeout(2, () -> seguirEntradaXX(PANEL_RECEPCION_ENTRADAS));
            }
        }
    }// GEN-LAST:event_jLabel24MouseReleased

    private void jcantidadFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jcantidadFocusGained
        conBorder(jcantidad);
        NovusUtils.deshabilitarCopiarPegar(jcantidad);
    }// GEN-LAST:event_jcantidadFocusGained

    private void jcantidadFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jcantidadFocusLost
        sinBorder(jcantidad);
    }// GEN-LAST:event_jcantidadFocusLost

    private void jdensidadMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jdensidadMouseReleased
        calcula();
    }// GEN-LAST:event_jdensidadMouseReleased

    private void jdensidadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jdensidadActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jdensidadActionPerformed

    private void jrecibidoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jrecibidoMouseReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_jrecibidoMouseReleased

    private void jrecibidoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jrecibidoActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jrecibidoActionPerformed

    private void jdensidadKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jdensidadKeyReleased
        calcula();
    }// GEN-LAST:event_jdensidadKeyReleased

    private void jLabel39MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel39MouseReleased
        presionarPunto();
    }// GEN-LAST:event_jLabel39MouseReleased

    private void jLabel40MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel40MouseReleased
        presionarPunto();
    }// GEN-LAST:event_jLabel40MouseReleased

    private void jLabel41MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel41MouseReleased
        presionarPunto();
    }// GEN-LAST:event_jLabel41MouseReleased

    private void jLabel42MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel42MouseReleased
        presionarPunto();
    }// GEN-LAST:event_jLabel42MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel ContenedorTanquesDividido;
    private javax.swing.JLabel JVisualTanque;
    private javax.swing.JScrollPane ScrollContenedorTanqueDividido;
    private javax.swing.JLabel background;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAceptarConfirmacion;
    public com.firefuel.components.panelesPersonalizados.PanelRedondo btnAgregar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAlFinal;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAnterior;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAnterior3;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAuto;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnCancelar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnCancelar1;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnCancelarConfirmacion;
    public com.firefuel.components.panelesPersonalizados.PanelRedondo btnCerrarModal;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnContinuar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnFinalizar;
    public com.firefuel.components.panelesPersonalizados.PanelRedondo btnGuardar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnIniciar;
    public com.firefuel.components.panelesPersonalizados.PanelRedondo btnModificar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnNueva;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnRecepciones;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnSiguiente;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnSiguienteProceso2;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnUsar;
    private javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JComboBox<String> jComboTanques;
    public javax.swing.JLabel jIcono;
    public javax.swing.JTable jInformacionDescargue;
    private javax.swing.JLabel jLAnteriorEntradaInicial;
    public javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    public javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    public javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDocumento;
    private javax.swing.JLabel jLabelPlaca;
    private javax.swing.JLabel jNuevo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    public javax.swing.JLabel jTitle;
    private javax.swing.JLabel jTitle1;
    private javax.swing.JLabel jUsar;
    private javax.swing.JLabel jVisualVolMax;
    public javax.swing.JTextField jagua_final;
    public javax.swing.JTextField jagua_inicial;
    public javax.swing.JTextField jaltura_final;
    public javax.swing.JTextField jaltura_inicial;
    public javax.swing.JTextField jcantidad;
    private ClockViewController jclock;
    public javax.swing.JComboBox<String> jcombo_productos;
    public javax.swing.JComboBox<BodegaBean> jcombo_tanque;
    public javax.swing.JTextField jdensidad;
    private javax.swing.JTextField jnro_orden;
    private javax.swing.JTextField jplaca;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    public javax.swing.JTextField jrecibido;
    public javax.swing.JLabel jtext;
    public javax.swing.JTextField jvolumen_final;
    public javax.swing.JTextField jvolumen_inicial;
    private javax.swing.JLabel lblAceptarConfirmacion;
    private javax.swing.JLabel lblAgregar;
    private javax.swing.JLabel lblCancelar;
    private javax.swing.JLabel lblCancelar1;
    private javax.swing.JLabel lblCancelarConfirmacion;
    private javax.swing.JLabel lblCantidad;
    public javax.swing.JLabel lblCantidadInformacion;
    public javax.swing.JLabel lblCentroLogistico;
    public javax.swing.JLabel lblCentroOrigen;
    private javax.swing.JLabel lblContinuar;
    public javax.swing.JLabel lblFechaDocumento;
    private javax.swing.JLabel lblFinal;
    private javax.swing.JLabel lblFinalizar;
    private javax.swing.JLabel lblGuardar;
    public javax.swing.JLabel lblGuiaTransporte;
    private javax.swing.JLabel lblModificar;
    public javax.swing.JLabel lblNumeroDocumentoSAP;
    public javax.swing.JLabel lblProductoInformacion;
    private javax.swing.JLabel lblRecepciones;
    private javax.swing.JLabel lblSeleccionarTodo;
    private javax.swing.JLabel lblSiguiente;
    private javax.swing.JLabel lblTanque;
    private javax.swing.JLabel lblTituloCantidadTotal;
    private javax.swing.JLabel lblTituloCentroLogistico;
    private javax.swing.JLabel lblTituloCentroOrigen;
    private javax.swing.JLabel lblTituloFechaDocumento;
    private javax.swing.JLabel lblTituloGuiaTransporte;
    private javax.swing.JLabel lblTituloNoDocumentoSAP;
    public javax.swing.JLabel lblTituloRemision;
    private javax.swing.JLabel lbltitutloProducto;
    private javax.swing.JPanel panelEntradasActuales;
    private javax.swing.JPanel panelInformacionGeneral;
    private javax.swing.JPanel panelIngresoEntradasFinales;
    private javax.swing.JPanel panelIngresoEntradasIniciales;
    private javax.swing.JPanel panelMenu;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo2;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo3;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo4;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo5;
    private javax.swing.JPanel panelSeleccionTanques;
    private javax.swing.JPanel pnlConfirmacion;
    private javax.swing.JPanel pnlDetalleRemisionSAP;
    private javax.swing.JPanel pnlDivisionProducto;
    private javax.swing.JPanel pnlNotificaciones;
    private javax.swing.JPanel pnlPrincipal;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlProductos;
    public javax.swing.JTextField txtCantidad;
    // End of variables declaration//GEN-END:variables

    ProductoBean obtenerProductoSeleccionado() {
        System.out.println("=== DEBUG: obtenerProductoSeleccionado() ===");
        int selectedProductIndex = jcombo_productos.getSelectedIndex();
        System.out.println("Índice del producto seleccionado: " + selectedProductIndex);
        System.out.println("Total de productos en optionProductsCombo: " + this.optionProductsCombo.size());
        
        if (selectedProductIndex > -1) {
            ProductoBean producto = this.optionProductsCombo.get(selectedProductIndex);
            if (producto != null) {
                System.out.println("Producto seleccionado: ID=" + producto.getId() + ", Descripción=" + producto.getDescripcion());
            } else {
                System.out.println("ERROR: Producto seleccionado es NULL");
            }
            return producto;
        } else {
            System.out.println("ERROR: No hay producto seleccionado (selectedProductIndex = -1)");
        }
        return null;
    }

    public long obtenerNumeroTanqueSeleccionado() {
        int selectedTankIndex = jcombo_tanque.getSelectedIndex();
        long numeroTanqueSeleccionado = 0;
        if (selectedTankIndex > -1) {
            BodegaBean tank = this.optionTanksCombo.containsKey(selectedTankIndex)
                    ? this.optionTanksCombo.get(selectedTankIndex)
                    : null;
            if (tank != null) {
                numeroTanqueSeleccionado = tank.getNumeroStand();
            }
        }
        return numeroTanqueSeleccionado;
    }

    void limpiarCampos() {
        /*
         * jcombo_tanque.setEditable(false);
         * jcombo_tanque.setEnabled(false);
         * jcombo_productos.setEnabled(false);
         * jcombo_productos.setEditable(false);
         * jaltura.setText("");
         * jgalones.setText("");
         * jagua.setText("");
         * jLabel17.setText("ENTRADA FINAL");
         * jLabel15.setText("LECTURA FINAL");
         * jLabel10.setText("REGISTRAR ");
         */
    }

    private void continuarSinPlaca() {
        showPanel(PANEL_RECEPCION_TANQUES);
    }

    void registroEntradas() {
        if (jnro_orden.getText().trim().equals("") || jplaca.getText().trim().equals("")) {
            showPanel(PANEL_RECEPCION_DOCUMENTOS);
            lblSiguiente.setVisible(false);
            lblFinal.setVisible(false);
            lblFinalizar.setVisible(true);
            btnFinalizar.setVisible(true);

        } else {
            jLabel1.setText("");
            if (jvolumen_final.getText().trim().equals("") || jaltura_final.getText().trim().equals("")
                    || jagua_final.getText().trim().equals("")) {
                showMessage("DEBE RELLENAR TODOS LOS CAMPOS",
                        "/com/firefuel/resources/btBad.png",
                        true, this::cambiarPanelHome,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else if (!jvolumen_final.getText().trim().equals("")
                    && Float.parseFloat(jvolumen_final.getText().trim()) <= 0) {
                showMessage("VOLUMEN DADO INVALIDO",
                        "/com/firefuel/resources/btBad.png",
                        true, this::cambiarPanelHome,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                double altura = (Double.parseDouble(jaltura_final.getText()) * 100d) / 100d;
                double volumen = (Double.parseDouble(jvolumen_final.getText()) * 100d) / 100d;
                BodegaBean tanque = new BodegaBean();
                tanque.setAltura_agua(Math.round(Float.parseFloat(jagua_final.getText().trim())));
                tanque.setAltura_total(altura);
                tanque.setGalonTanque(Math.round(Float.parseFloat(jvolumen_final.getText().trim())));
                this.entradaCombustible.setTanqueLecturaFinal(tanque);

                showModal("<p style='color:#B30F00; font-size:25px'><center>CARGANDO</center></p><br><center><p style ='color:#161438; font-size:15px'>Realizando entrada espere un momento...</p></center>", "/com/firefuel/resources/IconoLoader.gif", false, null, false);
                int espera = 2;
                /* 2 Segundos de espera */
                setTimeout(espera, () -> {
                    // GUARDAR LA ENTRADA
                    JsonObject response = guardarEntrada();
                    NovusUtils.setMensaje("", jLabel1);
                    
                    if (response != null) {
                        //buscar entrada para confirmar
                        confirmarDescargue();
                        showModal("<p style='color:#048900;'>ENTRADA REALIZADA CORRECTAMENTE</p>", "/com/firefuel/resources/cheque.png", true, this::cerrar, true);
                        panelesIndex = 0;
                        
                        // CORRECCIÓN: Solo borrar recepción si guardado fue exitoso
                        if (recepcion != null) {
                            borrarRecepcionUseCase.execute(recepcion.getId());
                        }
                    } else {
                        //  ERROR: No borrar la recepción para permitir reintento
                        // Mensaje genérico para el usuario (el log tiene detalles técnicos)
                        String mensajeError = "Error de conexión con el servicio de impresión.";
                        showModal(mensajeError, "/com/firefuel/resources/iconoError.png", true, this::cerrar, true);
                    }
                });
                jLabel1.setVisible(true);
            }
        }
    }

    private void confirmarDescargue() {
        if (masser && !manual) {
            if (entradaCombustibleBean == null) {
                entradaCombustibleBean = new EntradaCombustibleBean();
                entradaCombustibleBean.setIdRemision(numeroDeRemision);
                entradaCombustibleBean.setDelivery(delivery);
            }
            this.remisionesSAP.confirmarEntrada(entradaCombustibleBean, (int) obtenerProductoSeleccionado().getId());
        }
    }

    JsonObject guardarEntrada() {
        JsonObject jsonEntradas = new JsonObject();
        jsonEntradas.addProperty("fechaTransaccion", Main.SDFSQL.format(new Date()));
        jsonEntradas.addProperty("fechaInicio", Main.SDFSQL.format(this.entradaCombustible.getFechaInicio()));
        jsonEntradas.addProperty("fechaFin", Main.SDFSQL.format(new Date()));
        jsonEntradas.addProperty("identificacionNegocio", Main.credencial.getEmpresa().getNegocioId());
        jsonEntradas.addProperty("identificadorPromotor", this.promotorId);
        jsonEntradas.addProperty("total",
                Float.parseFloat(jcantidad.getText()) * obtenerProductoSeleccionado().getPrecio());
        jsonEntradas.addProperty("identificacionEquipoLazo", Main.credencial.getEquipos_id());
        jsonEntradas.addProperty("impresoTiquete", "N");
        JsonArray jsonDetalles = new JsonArray();

        JsonObject detalle = new JsonObject();
        detalle.addProperty("identificadorProducto", obtenerProductoSeleccionado().getId());
        detalle.addProperty("identificadorBodega", obtenerTanqueSeleccionado().getId());
        detalle.addProperty("precioProducto", obtenerProductoSeleccionado().getPrecio());
        detalle.addProperty("identificadorUnidad", this.obtenerProductoSeleccionado().getUnidades_medida_id());
        detalle.addProperty("cantidad", jcantidad.getText());
        if (REQUIERE_DENSIDAD) {
            detalle.addProperty("densidad", jdensidad.getText());
            detalle.addProperty("recibido", jrecibido.getText());
            detalle.addProperty("cantidadCalculada", jLabel37.getText());
        }
        detalle.addProperty("subTotal",
                Float.parseFloat(jcantidad.getText()) * obtenerProductoSeleccionado().getPrecio());
        jsonDetalles.add(detalle);

        jsonEntradas.add("detalles", jsonDetalles);
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("fechaTransaccion", Main.SDFSQL.format(new Date()));
        jsonData.addProperty("fechaInicio", Main.SDFSQL.format(new Date()));
        jsonData.addProperty("fechaFin", Main.SDFSQL.format(new Date()));
        jsonData.add("lecturaVeeder", entradaCombustible.getLecturasVeederIniciales());

        JsonObject infoGeneral = new JsonObject();
        infoGeneral.addProperty("PLACA", jplaca.getText().trim());
        infoGeneral.addProperty("DOCUMENTO", jnro_orden.getText().trim());
        jsonData.add("INFORMACION_GENERAL", infoGeneral);

        JsonObject productoSeleccionado = new JsonObject();
        productoSeleccionado.addProperty("identificadorTanque", this.obtenerTanqueSeleccionado().getId());
        productoSeleccionado.addProperty("identificadorProducto", this.obtenerProductoSeleccionado().getId());
        JsonArray array1 = new JsonArray();
        array1.add(productoSeleccionado);
        jsonData.add("PRODUCTOS_SELECIONADOS", array1);

        JsonObject tanqueSeleccionados = new JsonObject();
        tanqueSeleccionados.addProperty("identificacionTanque", obtenerTanqueSeleccionado().getNumeroStand());
        tanqueSeleccionados.addProperty("productoDesc", obtenerProductoSeleccionado().getDescripcion());
        tanqueSeleccionados.addProperty("productoPrecio", obtenerProductoSeleccionado().getPrecio());
        tanqueSeleccionados.addProperty("identificadorTanque", obtenerTanqueSeleccionado().getId());
        tanqueSeleccionados.addProperty("identificadorProducto", obtenerProductoSeleccionado().getId());
        tanqueSeleccionados.addProperty("cantidad", jcantidad.getText());
        JsonArray array2 = new JsonArray();
        array2.add(tanqueSeleccionados);
        jsonData.add("TANQUES_SELECCIONADOS", array2);

        JsonObject medidasInicial = new JsonObject();
        medidasInicial.addProperty("identificacionTanque", this.obtenerTanqueSeleccionado().getId());
        medidasInicial.addProperty("identificadorTanque", this.obtenerTanqueSeleccionado().getDescripcion());
        medidasInicial.addProperty("altura", this.entradaCombustible.getTanqueLecturaInicial().getAltura_total());
        medidasInicial.addProperty("galones", this.entradaCombustible.getTanqueLecturaInicial().getGalonTanque());
        medidasInicial.addProperty("agua", this.entradaCombustible.getTanqueLecturaInicial().getAltura_agua());
        JsonArray array3 = new JsonArray();
        array3.add(medidasInicial);
        jsonData.add("MEDIDAS_INICIALES", array3);

        JsonObject medidasFinales = new JsonObject();
        medidasFinales.addProperty("identificacionTanque", this.obtenerTanqueSeleccionado().getId());
        medidasFinales.addProperty("identificadorTanque", this.obtenerTanqueSeleccionado().getDescripcion());
        medidasFinales.addProperty("altura", this.entradaCombustible.getTanqueLecturaFinal().getAltura_total());
        medidasFinales.addProperty("galones", this.entradaCombustible.getTanqueLecturaFinal().getGalonTanque());
        medidasFinales.addProperty("agua", this.entradaCombustible.getTanqueLecturaFinal().getAltura_agua());
        JsonArray array4 = new JsonArray();
        array4.add(medidasFinales);
        jsonData.add("SOLICITUD_MEDIDAS_FINALES", array4);

        jsonData.add("lecturaVeederFinal", entradaCombustible.getLecturasVeederFinales());
        this.jsonRegistro.add("entrada", jsonEntradas);
        this.jsonRegistro.add("data", jsonData);
        
        NovusUtils.printLn("═══════════════════════════════════════════════════════════");
        NovusUtils.printLn(" GUARDANDO ENTRADA DE COMBUSTIBLE - Backend Node.js");
        NovusUtils.printLn("   URL: " + NovusConstante.SECURE_CENTRAL_POINT_ENTRADA_COMBUSTIBLE);
        NovusUtils.printLn("═══════════════════════════════════════════════════════════");
        
        ClientWSAsync ws = new ClientWSAsync("REGISTRO ENTRADAS",
                NovusConstante.SECURE_CENTRAL_POINT_ENTRADA_COMBUSTIBLE, NovusConstante.POST, this.jsonRegistro,
                false, false);
        ws.setTimeout(30000); // 30 segundos timeout
        
        JsonObject response = null;
        try {
            response = ws.esperaRespuesta();
            int statusCode = ws.getStatus();
            
            // ✅ VALIDACIÓN COMPLETA DE RESPUESTA CON CÓDIGOS HTTP
            if (response == null) {
                //  BACKEND CAÍDO O SIN RESPUESTA
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn(" ERROR HTTP 500: Backend Node.js no responde");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("   Posibles causas:");
                NovusUtils.printLn("   - Servicio Node.js caído (puerto 8010)");
                NovusUtils.printLn("   - Timeout de conexión (>30 segundos)");
                NovusUtils.printLn("   - Error de red");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                return null;
                
            } else if (statusCode >= 500) {
                //  ERROR DEL SERVIDOR (500, 502, 503, etc.)
                NovusUtils.printLn(" ERROR HTTP " + statusCode + ": Error en el servidor backend");
                if (ws.getError() != null) {
                    NovusUtils.printLn("   Detalle: " + ws.getError().toString());
                }
                return null;
                
            } else if (statusCode >= 400 && statusCode < 500) {
                //  ERROR DEL CLIENTE (400, 404, etc.)
                NovusUtils.printLn(" ERROR HTTP " + statusCode + ": Error en la petición");
                if (response.has("mensaje")) {
                    NovusUtils.printLn("   Mensaje: " + response.get("mensaje").getAsString());
                }
                return null;
                
            } else if (statusCode >= 200 && statusCode < 300) {
                //  RESPUESTA EXITOSA - Validar contenido
                if (response.has("codigo")) {
                    int codigoRespuesta = response.get("codigo").getAsInt();
                    if (codigoRespuesta != 0) {
                        // Código de error en respuesta exitosa HTTP
                        String mensaje = response.has("mensaje") ? 
                            response.get("mensaje").getAsString() : "Error desconocido";
                        NovusUtils.printLn("⚠️  Backend retornó código de error: " + codigoRespuesta);
                        NovusUtils.printLn("   Mensaje: " + mensaje);
                        return null;
                    }
                }
                
                NovusUtils.printLn(" Entrada guardada correctamente en backend");
                NovusUtils.printLn("   El backend imprimirá automáticamente en 5 segundos vía Python");
                return response;
                
            } else {
                //  CÓDIGO HTTP DESCONOCIDO
                NovusUtils.printLn(" ERROR: Código HTTP inesperado: " + statusCode);
                return null;
            }
            
        } catch (Exception ex) {
            //  EXCEPCIÓN INESPERADA
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn(" EXCEPCIÓN al comunicarse con backend Node.js");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("   Tipo: " + ex.getClass().getName());
            NovusUtils.printLn("   Mensaje: " + ex.getMessage());
            NovusUtils.printLn("   URL: " + NovusConstante.SECURE_CENTRAL_POINT_ENTRADA_COMBUSTIBLE);
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            ex.printStackTrace();
            return null;
        }
    }

    private void estiloTabla() {
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 34)); // NOI18N

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

        setearTamañoColumn(jTable1.getColumnModel().getColumn(0), 100);
        setearTamañoColumn(jTable1.getColumnModel().getColumn(1), 150);
        setearTamañoColumn(jTable1.getColumnModel().getColumn(2), 150);
        setearTamañoColumn(jTable1.getColumnModel().getColumn(3), 250);
        // setearTamañoColumn(jTable1.getColumnModel().getColumn(4), 200);
        setearTamañoColumn(jTable1.getColumnModel().getColumn(5), 150);

    }

    private void setearTamañoColumn(TableColumn column, int size) {
        column.setPreferredWidth(size);
        column.setMaxWidth(size);
        column.setMinWidth(size);
    }

    public void showPanel(String panel) {
        NovusUtils.showPanel(panelMenu, panel);
        jPanel1.updateUI();
        if (panel.equals(PANEL_RECEPCION_TANQUES)) {
            cargarProductos();
            if (jcombo_productos.getItemCount() > 0) {
                jcombo_productos.setSelectedIndex(0);
            }
            System.out.println("[DEBUG] showPanel: productos en combo: " + jcombo_productos.getItemCount());
        }
    }

    private void usarRecepcion() {
        int r = jTable1.getSelectedRow();
        if (r > -1) {
            try {
                long numeroSobre = (long) jTable1.getValueAt(r, 0);

                numeroDeRemision = remisionesSAP.obtenerIdRemision((String) jTable1.getValueAt(r, 2));
                delivery = (String) jTable1.getValueAt(r, 2);

                for (RecepcionBean recepcione : recepciones) {
                    if (recepcione.getId() == numeroSobre) {
                        recepcion = recepcione;
                        break;
                    }
                }
                if (recepcion != null) {
                    // ACTIVA LA BANDERA Y GUARDA EL PRODUCTO DE LA REMISIÓN
                    modoRemisionActiva = true;
                    productoRemisionId = recepcion.getProductoId();
                    System.out.println("[DEBUG] ACTIVANDO modoRemisionActiva: " + modoRemisionActiva + ", productoRemisionId: " + productoRemisionId);

                    promotorId = recepcion.getPromotor();
                    jnro_orden.setText(String.valueOf(recepcion.getDocumento()));
                    jplaca.setText(String.valueOf(recepcion.getPlaca()));

                    jaltura_inicial.setText(String.valueOf(recepcion.getAlturaInicial()));
                    jvolumen_inicial.setText(String.valueOf(recepcion.getVolumenInicial()));
                    jagua_inicial.setText(String.valueOf(recepcion.getAguaInicial()));
                    entradaCombustible.setFechaInicio(recepcion.getFecha());
                    for (BodegaBean tanque : tanques) {
                        if (tanque.getId() == recepcion.getTanqueId()) {
                            jcombo_tanque.setSelectedItem(tanque);
                            ArrayList<ProductoBean> productsTankSelected = tanque.getProductos();
                            if (productsTankSelected != null) {
                                for (ProductoBean product : productsTankSelected) {
                                    if (recepcion.getProductoId() == product.getId()) {
                                        jcombo_productos.setSelectedIndex(this.productosIndice.get(product.getId()));
                                    }
                                }
                            }
                            break;
                        }
                    }
                    jcantidad.setText(String.valueOf(recepcion.getCantidad()));
                }
                cargarFaseFinalRecepcion();
                showPanel(PANEL_RECEPCION_ENTRADAS_FINAL);

            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
        }
    }

    private void cargarFaseFinalRecepcion() {
        try {

            if (jvolumen_inicial.getText().trim().equals("") || jaltura_inicial.getText().trim().equals("")
                    || jagua_inicial.getText().trim().equals("")) {
                showMessage("DEBE RELLENAR TODOS LOS CAMPOS",
                        "/com/firefuel/resources/btBad.png",
                        true, this::cambiarPanelHome,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else if (!jvolumen_inicial.getText().trim().equals("")
                    && Float.parseFloat(jvolumen_inicial.getText().trim()) <= 0) {
                showMessage("VOLUMEN DADO INVALIDO",
                        "/com/firefuel/resources/btBad.png",
                        true, this::cambiarPanelHome,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                double alturai = (Double.parseDouble(jaltura_inicial.getText()) * 100d) / 100d;
                double volumeni = (Double.parseDouble(jvolumen_inicial.getText()) * 100d) / 100d;
                obtenerTanqueSeleccionado().setAltura_agua(Math.round(Float.parseFloat(jagua_inicial.getText().trim())));
                obtenerTanqueSeleccionado().setAltura_total(alturai);
                obtenerTanqueSeleccionado().setGalonTanque(Math.round(Float.parseFloat(jvolumen_inicial.getText().trim())));
                BodegaBean tanque = new BodegaBean();
                tanque.setAltura_agua(Math.round(Float.parseFloat(jagua_inicial.getText().trim())));
                tanque.setAltura_total(alturai);
                tanque.setGalonTanque(Math.round(Float.parseFloat(jvolumen_inicial.getText().trim())));
                this.entradaCombustible.setTanqueLecturaInicial(tanque);

                NovusUtils.setMensaje("REALIZANDO DESCARGUE, ESPERE", jLabel1);
                jLabel15.setEnabled(false);
                jLAnteriorEntradaInicial.setEnabled(false);
                jLabel23.setEnabled(false);
                btnAnterior.setBackground(new Color(229, 230, 232));
                btnAnterior3.setBackground(new Color(229, 230, 232));
                btnIniciar.setBackground(new Color(229, 230, 232));
                jTitle.setText("Altura final tanque ".toUpperCase() + " " + obtenerTanqueSeleccionado().getNumeroStand());
                if (hayVeederRoot) {
                    setTimeout(5, () -> {
                        if (hayVeederRoot) {
                            JsonObject response = solicitarInformacionConsola(
                                    obtenerTanqueSeleccionado().getNumeroStand());
                            guardarEstadoActual();
                            if (recepcion != null) {
                                actualizarTanque(response);
                            }
                            if (response != null) {
                                entradaCombustible.setLecturasVeederFinales(response);
                                jvolumen_final.setText(response.get("volumen").getAsFloat() + "");
                                jaltura_final.setText(response.get("altura").getAsFloat() + "");
                                jagua_final.setText(response.get("agua").getAsFloat() + "");
                                JVisualTanque.setText(response.get("volumen").getAsDouble() + "");
                                bloquearCamposFinal();
                            } else {
                                jvolumen_final.setEditable(false);
                                jvolumen_final.setBackground(new Color(153, 153, 153));
                                jvolumen_final.setForeground(new Color(0, 0, 0));
                            }
                        } else {
                            if (recepcion != null) {
                                actualizarTanque(null);
                            }

                            jvolumen_final.setEditable(false);
                            jvolumen_final.setBackground(new Color(153, 153, 153));
                            jvolumen_final.setForeground(new Color(0, 0, 0));
                        }

                        if (masser && !manual && entradaCombustibleBean != null) {
                            remisionesSAP.setEntrarOtravez(true);
                            EntradaCombustibleBean combustibleBean = remisionesSAP.eliminarRemisionUsada(entradaCombustibleBean);
                            remisionesSAP.continuar(combustibleBean, "Altura final tanque ".toUpperCase() + " " + obtenerTanqueSeleccionado().getNumeroStand());
                            remisionesSAP.limpiarCampos();
                        } else {
                            NovusUtils.setMensaje("", jLabel1);
                            mostrarTecladoNum2(true);
                            showPanel(PANEL_RECEPCION_ENTRADAS_FINAL);
                            jTitle.setText("Altura final tanque ".toUpperCase() + " " + obtenerTanqueSeleccionado().getNumeroStand());
                        }
                        jLabel15.setEnabled(true);
                        jLAnteriorEntradaInicial.setEnabled(true);
                        jLabel23.setEnabled(true);
                        btnAnterior.setBackground(new Color(204, 0, 0));
                        btnAnterior3.setBackground(new Color(204, 0, 0));
                        btnIniciar.setBackground(new Color(204, 0, 0));
                        NovusUtils.setMensaje("", jLabel1);
                    });

                } else {
                    guardarEstadoActual();
                    if (recepcion != null) {
                        actualizarTanque(null);
                    }
                    mostrarTecladoNum2(true);
                    jLabel15.setEnabled(false);
                    jLAnteriorEntradaInicial.setEnabled(false);
                    jLabel23.setEnabled(false);
                    btnIniciar.setBackground(new Color(229, 230, 232));
                    btnAnterior.setBackground(new Color(229, 230, 232));
                    btnAnterior3.setBackground(new Color(229, 230, 232));
                    if (masser && !manual && entradaCombustibleBean != null) {
                        remisionesSAP.setEntrarOtravez(true);
                        EntradaCombustibleBean combustibleBean = remisionesSAP.eliminarRemisionUsada(entradaCombustibleBean);
                        remisionesSAP.continuar(combustibleBean, "Altura final tanque ".toUpperCase() + " " + obtenerTanqueSeleccionado().getNumeroStand());
                        remisionesSAP.limpiarCampos();
                    } else {
                        showPanel(PANEL_RECEPCION_ENTRADAS_FINAL);
                        jTitle.setText("Altura final tanque ".toUpperCase() + " " + obtenerTanqueSeleccionado().getNumeroStand());
                    }
                    jLabel15.setEnabled(true);
                    jLAnteriorEntradaInicial.setEnabled(true);
                    jLabel23.setEnabled(true);
                    btnAnterior.setBackground(new Color(204, 0, 0));
                    btnAnterior3.setBackground(new Color(204, 0, 0));
                    btnIniciar.setBackground(new Color(204, 0, 0));
                    NovusUtils.setMensaje("", jLabel1);
                }

            }
        } catch (NullPointerException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void guardarEstadoActual() {

        BodegaBean bodega = (BodegaBean) jcombo_tanque.getSelectedItem();
        ProductoBean producto = obtenerProductoSeleccionado();
        if (recepcion == null) {
            recepcion = new RecepcionBean();
            recepcion.setFecha(new Date());
            recepcion.setPromotor(promotorId);
        }
        recepcion.setTanqueId(bodega.getId());
        recepcion.setTanqueDescripcion(bodega.getDescripcion());

        recepcion.setProductoId(producto.getId());
        recepcion.setDocumento(jnro_orden.getText().trim());
        recepcion.setPlaca(jplaca.getText());
        recepcion.setCantidad((int) Float.parseFloat(jcantidad.getText().trim()));
        if (!jaltura_inicial.getText().isEmpty()) {
            recepcion.setAlturaInicial(Float.parseFloat(jaltura_inicial.getText()));
        } else {
            recepcion.setAlturaInicial(0);
        }
        if (!jvolumen_inicial.getText().isEmpty()) {
            recepcion.setVolumenInicial(Double.parseDouble(jvolumen_inicial.getText()));
        } else {
            recepcion.setVolumenInicial(0);
        }
        if (!jagua_inicial.getText().isEmpty()) {
            recepcion.setAguaInicial(Float.parseFloat(jagua_inicial.getText()));
        } else {
            recepcion.setAguaInicial(0);
        }
        NovusUtils.printLn("*****************************************************");
        NovusUtils.printLn("Numero de remision::::::::::::::::::::" + jnro_orden.getText().trim());
        NovusUtils.printLn("Recepcion::::::::::::::::::::: " + recepcion.toString());
        NovusUtils.printLn("*****************************************************");
        recepcion = actualizarRecepcionCombustibleUseCase.execute(recepcion);

    }

    private void actualizarTanque(JsonObject response) {
        int ALTO_COMPONENTE = 340;

        class OneShotTask implements Runnable {

            JsonObject response;

            OneShotTask(JsonObject response) {
                this.response = response;
            }

            @Override
            public void run() {
                try {
                    if (hayVeederRoot) {
                        if (response == null) {
                            response = solicitarInformacionConsola(obtenerTanqueSeleccionado().getNumeroStand());
                        }
                    } else {
                        response = new JsonObject();
                        response.addProperty("volumen", jvolumen_inicial.getText());
                        response.addProperty("altura", jaltura_inicial.getText());
                        response.addProperty("agua", jagua_inicial.getText());
                    }
                    if (response != null) {

                        if (REQUIERE_DENSIDAD) {

                            jLabel21.setVisible(false);
                            jagua_final.setVisible(false);

                            jLabel32.setBounds(40, 240, 180, 29);
                            jLabel30.setBounds(240, 240, 180, 29);
                            jrecibido.setBounds(50, 270, 180, 60);
                            jdensidad.setBounds(250, 270, 180, 60);

                        }

                        BodegaBean tanque = obtenerTanqueSeleccionado();
                        float volMax = tanque.getVolumenMaximo();
                        float volAct = response.get("volumen").getAsFloat();
                        NovusUtils.printLn("actulizando recepcion ::::::::::::::::::: " + recepcion);
                        double volInic = recepcion != null ? recepcion.getVolumenInicial() : Double.parseDouble(jvolumen_inicial.getText());
                        float volPorc = volAct * 100f / volMax;
                        float volPix = ALTO_COMPONENTE * volPorc / 100;

                        jVisualVolMax.setText("vol max:    " + String.valueOf(volMax));

                        int height = Math.round(volPix); // 320
                        JVisualTanque.setBounds(485, (ALTO_COMPONENTE - height) + 75, 131, height);

                        entradaCombustible.setLecturasVeederFinales(response);
                        jvolumen_final.setText(response.get("volumen").getAsFloat() + "");
                        jaltura_final.setText(response.get("altura").getAsFloat() + "");
                        jagua_final.setText(response.get("agua").getAsFloat() + "");

                        jLabel26.setText(getFecha(new Date()));
                        JVisualTanque.setText("Vol :" + Utils.fmt(volAct) + " " + "(" + Utils.fmt(volPorc) + "%)");
                        JVisualTanque.updateUI();

                        if (REQUIERE_DENSIDAD) {
                            jcantidad.setText(String.valueOf(volAct - volInic));
                        } else {
                            jLabel37.setText(jcantidad.getText());
                        }
                        jLabel35.setText(Utils.fmt(volAct - volInic));

                        switch ((int) tanque.getFamiliaId()) {
                            case NovusConstante.CORRIENTE:
                                JVisualTanque.setBackground(NovusConstante.CORRIENTE_COLOR);
                                JVisualTanque.setForeground(Color.white);
                                break;
                            case NovusConstante.EXTRA:
                                JVisualTanque.setBackground(NovusConstante.EXTRA_COLOR);
                                JVisualTanque.setForeground(Color.white);
                                break;
                            case NovusConstante.DIESEL:
                                JVisualTanque.setBackground(NovusConstante.DIESEL_COLOR);
                                break;
                            case NovusConstante.GAS:
                                JVisualTanque.setBackground(NovusConstante.GAS_COLOR);
                                JVisualTanque.setForeground(Color.white);
                                break;
                            case NovusConstante.GLP:
                                JVisualTanque.setBackground(NovusConstante.GLP_COLOR);
                                break;
                            case NovusConstante.ADBLUE:
                                JVisualTanque.setBackground(NovusConstante.ADBLUE_COLOR);
                                break;
                            default:
                                JVisualTanque.setBackground(NovusConstante.CORRIENTE_COLOR);
                        }

                        if (hayVeederRoot) {
                            bloquearCamposFinal();
                        }
                    } else {
                        jvolumen_final.setEditable(false);
                        jvolumen_final.setBackground(new Color(153, 153, 153));
                        jvolumen_final.setForeground(new Color(0, 0, 0));
                    }

                    if (hayVeederRoot && ejecutarVeeder) {
                        actualizarTanque(null);
                    } else {
                        jvolumen_final.setEditable(false);
                        jvolumen_final.setBackground(new Color(153, 153, 153));
                        jvolumen_final.setForeground(new Color(0, 0, 0));

                        jvolumen_final.setText("");
                        jaltura_final.setText("");
                        jagua_final.setText("");

                        jcantidad.setVisible(false);
                        jLabel37.setVisible(false);
                        jLabel35.setVisible(false);

                        jLabel34.setVisible(false);
                        jLabel38.setVisible(false);
                    }

                } catch (NumberFormatException e) {
                    NovusUtils.printLn("ha ocurrido un error Recepcion de combustible" + e.getMessage());
                }
            }
        }
        if (hayVeederRoot) {
            if (response != null) {
                setTimeout(0, new OneShotTask(response));
            } else {
                setTimeout(2, new OneShotTask(response));
            }
        } else {
            new OneShotTask(response).run();
        }

    }

    SimpleDateFormat xsdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);

    private String getFecha(Date fecha) {
        NovusUtils.printLn(xsdf.format(fecha));
        String sfecha = xsdf.format(fecha);
        sfecha = sfecha.replaceAll("p. m.", "p.m.");
        sfecha = sfecha.replaceAll("a. m.", "a.m.");
        return sfecha;
    }

    private void presionarPunto() {
        NovusUtils.beep();
        try {
            JTextField focusOwner = (JTextField) FocusManager.getCurrentManager().getFocusOwner();
            focusOwner.setText(focusOwner.getText() + ".");
        } catch (Exception ex) {
            Logger.getLogger(TecladoNumerico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void calcula() {
        float recibido = Float.parseFloat(jrecibido.getText());
        float densidad = Float.parseFloat(jdensidad.getText());

        jLabel37.setText(Utils.fmt(recibido / densidad));
    }

    private void deshabilitar() {
        TecladoExtendidoGray teclado = (TecladoExtendidoGray) jPanel1;
        teclado.habilitarCaracteresEspeciales(false);
    }

    private boolean obtenerCapacidadMaxima(long capacidad) {
        boolean seguir = true;
        int selectedTankIndex = jcombo_tanque.getSelectedIndex();
        long id = this.optionTanksCombo.get(selectedTankIndex).getId();
        JsonObject json = obtenerCapacidadMaximaUseCase.execute(id);
        long volumenMaximo = json.get("volumen_maximo").getAsLong();
        MAXIMO_PERMITIDO_ALTURA = json.get("altura_maxima").getAsLong();
        if (capacidad > 0) {
            if (capacidad > volumenMaximo) {
                jLabel1.setText("LA CANTIDAD INGRESADA SUPERA LA CAPACIDAD MAXIMA");
                jLabel1.setFont(new java.awt.Font("Arial", 0, 24));
                jLabel1.setVisible(true);
                setTimeout(3, () -> {
                    NovusUtils.setMensaje("", jLabel1);
                });
                seguir = false;
            }
        } else {
            jLabel1.setText("LA CANTIDAD DEBE SER MAYOR A 0");
            jLabel1.setFont(new java.awt.Font("Arial", 0, 24));
            jLabel1.setVisible(true);
            setTimeout(3, () -> {
                NovusUtils.setMensaje("", jLabel1);
            });
            seguir = false;
        }
        return seguir;
    }

    private boolean obtenerAltura(float altura) {
        boolean seguir = true;
        if (MAXIMO_PERMITIDO_ALTURA <= 0) {
            int selectedTankIndex = jcombo_tanque.getSelectedIndex();
            long id = this.optionTanksCombo.get(selectedTankIndex).getId();
            JsonObject json = obtenerCapacidadMaximaUseCase.execute(id);
            MAXIMO_PERMITIDO_ALTURA = json.get("altura_maxima").getAsLong();
        }
        if (altura > 0) {
            if (altura >= MAXIMO_PERMITIDO_ALTURA) {
                jLabel1.setText("LA ALTURA INGRESADA SUPERA LA CAPACIDAD MAXIMA");
                jLabel1.setFont(new java.awt.Font("Arial", 0, 24));
                jLabel1.setVisible(true);
                setTimeout(3, () -> {
                    NovusUtils.setMensaje("", jLabel1);
                });
                seguir = false;
            }
        } else {
            jLabel1.setText("LA CANTIDAD DEBE SER MAYOR A 0");
            jLabel1.setFont(new java.awt.Font("Arial", 0, 24));
            jLabel1.setVisible(true);
            setTimeout(3, () -> {
                NovusUtils.setMensaje("", jLabel1);
            });
            seguir = false;
        }
        return seguir;
    }

    public void showMessage(String msj, String ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    public void showModal(String msj, String ruta, boolean habilitar, Runnable runnable, boolean autoclose) {
        PanelNotificacionModal notificacion = PanelNotificacionModal.getInstance();
        notificacion.update(msj, ruta, habilitar, runnable);
        if (autoclose) {
            notificacion.getTimer().start();
        }
        mostrarSubPanel(notificacion);
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnl_principal");
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

    private void placheholder(JTextField campoDeTexto, String texto) {
        TextPrompt placeholder = new TextPrompt(texto, campoDeTexto);
        placeholder.changeAlpha(0.70f);
        placeholder.setFont(new Font("Terpel Sans", 1, 20));
    }

    private void siguiente() {
        System.out.println("[DEBUG] siguiente");
        if (!validarCampos()) {
            if (masser && !manual) {
                if (!remisionesSAP.getEntrarOtravez()) {
                    entradaCombustibleBean = remisionesSAP.consultarRemision(jnro_orden.getText().trim());
                    //haca catga el producto sap
                    System.out.println("[DEBUG] entrada de combustible: "+ entradaCombustibleBean);
                    remisionesSAP.validarRemision(entradaCombustibleBean);
                } else {
                    showPanel(PANEL_RECEPCION_TANQUES);
                }
            } else {
                entradaCombustible.setPlaca(jplaca.getText().trim());
                entradaCombustible.setNroOrden(jnro_orden.getText().trim());
                mostrarTecladoAlfa(false);
                showPanel(PANEL_RECEPCION_TANQUES);
            }
        } else {
            NovusUtils.setMensaje("DEBE RELLENAR TODOS LOS CAMPOS", jLabel1);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jLabel1);
            });
        }
    }

    private boolean validarCampos() {
        return jplaca.getText().trim().equals("") || jnro_orden.getText().trim().equals("");
    }

    private void cerrarMensaje() {
        showPanel(panelAmostrar);
    }

    private void bloquearCampos() {
        jaltura_inicial.setEnabled(false);
        jagua_inicial.setEnabled(false);
        jcombo_productos.setEnabled(false);
        jcombo_tanque.setEnabled(false);
        jcantidad.setEnabled(false);
        jLabel23.setEnabled(false);
        btnAnterior.setVisible(false);
    }

    private void estilosContenedorTanque() {
        ScrollContenedorTanqueDividido.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        ScrollContenedorTanqueDividido.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        ScrollContenedorTanqueDividido.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    }

    public EntradaCombustibleBean getEntradaCombustible() {
        return entradaCombustibleBean;
    }

    public void setEntradaCombustible(EntradaCombustibleBean entradaCombustibleBean) {
        this.entradaCombustibleBean = entradaCombustibleBean;
    }

    private void selecciontabla() {
        dividircantidadCombustible.habilitarbotonModificar(entradaCombustibleBean);
    }

    public ProductoBean productoSAPActivo = null;

}

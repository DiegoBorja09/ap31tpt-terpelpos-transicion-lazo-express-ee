package com.firefuel;

import com.application.useCases.movimientos.FindAllProductoTipoKioskoUseCase;
import com.application.useCases.productos.BuscarProductoPorCodigoBarraUseCase;
import com.application.useCases.productos.BuscarProductoPorPluKioskoUseCase;
import com.application.useCases.consecutivos.ObtenerAlertasResolucionUseCase;
import com.application.useCases.ventas.VentaEnCursoUseCase;
import com.bean.CategoriaBean;
import com.bean.ConsecutivoBean;
import com.bean.ImpuestosBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.CategoriasDao;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import javax.swing.SwingWorker;
import javax.swing.JComboBox;
import java.util.Objects;
import com.interfaces.BackgroundTask;
import com.application.commons.ResultadoProductosKiosko;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import teclado.view.common.TecladoExtendidoGray;
import teclado.view.common.TecladoNumericoGrayMedium;

public class KCOViewController extends javax.swing.JFrame {

    // ✅ CALLBACK para refrescar productos después de anulación
    private static Runnable onAnulacionExitosaCallback = null;
    
    public static void registrarCallbackAnulacion(Runnable callback) {
        onAnulacionExitosaCallback = callback;
        System.out.println("Callback de anulación registrado en KCOViewController");
    }
    
    public static void notificarAnulacionExitosa() {
        if (onAnulacionExitosaCallback != null) {
            System.out.println(" Ejecutando callback de anulación - Refrescando productos...");
            onAnulacionExitosaCallback.run();
        } else {
            System.out.println(" No hay callback registrado para anulación");
        }
    }

    ObtenerAlertasResolucionUseCase obtenerAlertasResolucionUseCase;
    boolean primeraVenta = true;
    int cantidad = 0;
    int pos = 0;
    long factura = 0l;
    InfoViewController parent = null;
    FacturacionManualKCView dialog;
    ConsecutivoBean newConsecutivo;
    boolean mostrarMenu = false;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    static Color colorProducto = new Color(102, 102, 102);
    MovimientosBean movimiento;
    MovimientosDao mdao = new MovimientosDao();
    FindAllProductoTipoKioskoUseCase findAllProductoTipoKioskoUseCase;
    public static boolean isCombustible = true;
    
    // Variables de paginación
    private int currentPage = 0;
    private int pageSize = 20;
    private long totalRecords = 0;
    private JComboBox<String> pageSizeCombo;
    private JLabel prevButton;
    private JLabel nextButton;
    private JLabel progressButton;
    private JLabel totalRecordsLabel;
    private TreeMap<Long, MovimientosDetallesBean> seleccionado;
    private final TreeMap<Long, ArrayList<MovimientosDetallesBean>> productosCategoria = new TreeMap<>();

    static MovimientosDetallesBean seleccion;
    public ArrayList<MediosPagosBean> medios = new ArrayList<>();
    CategoriaDetallesListView panelPromociones;
    PersonaBean persona;
    ArrayList<CategoriaBean> listaCategorias = null;
    String card = null;
    ArrayList<MovimientosDetallesBean> lista = null;
    boolean manual = false;
    String fecha = "";
    boolean ventanaVenta = false;
    boolean ventanaVentaPrincipal = false;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

    boolean buttonProductosSeleccionado = false;
    boolean buttonCategoriasSeleccionado = false;
    Color colorPanelSeleccionado = new Color(186, 12, 47);
    Color colorPanelInactivo = new Color(214, 217, 223);
    Color colorTextoSeleccionado = new Color(255, 255, 255);
    Color colorTextoInactivo = new Color(186, 12, 47);
    Color colorTextoSelccionadoBold = new Color(102, 102, 102);

    TextPrompt placeholder;
    public static boolean writing = false;
    boolean ingresandoCantidad = false;
    PanelResultadoBusqueda panelBusquedaInstance = PanelResultadoBusqueda.getInstancia(this);
    Icon searchBar = new ImageIcon(getClass().getResource("/com/firefuel/resources/searchbar.png"));
    Icon numberBar = new ImageIcon(getClass().getResource("/com/firefuel/resources/fndCantidadProducto.png"));
    Icon fndTecladoBusqueda = new ImageIcon(getClass().getResource("/com/firefuel/resources/fndTecladoBusqueda.png"));

    public KCOViewController(InfoViewController parent, boolean modal) {
        this.parent = parent;
        this.persona = Main.persona;
        initComponents();
        this.init();
    }

    public KCOViewController(InfoViewController parent, boolean modal, boolean manual, FacturacionManualKCView dialogo, Long factura, String fecha, boolean ventanaVenta, boolean ventanaPrincipal) {
        this.parent = parent;
        this.persona = Main.persona;
        this.dialog = dialogo;
        this.factura = factura;
        this.manual = manual;
        this.fecha = fecha;
        this.ventanaVenta = ventanaVenta;
        this.ventanaVentaPrincipal = ventanaPrincipal;
        initComponents();
        this.init();
    }

    public void init() {
        this.obtenerAlertasResolucionUseCase = new ObtenerAlertasResolucionUseCase(NovusConstante.IS_DEFAULT_FE ? 31 : 35, NovusUtils.getTipoNegocioComplementario());
        findAllProductoTipoKioskoUseCase = new FindAllProductoTipoKioskoUseCase(currentPage, pageSize);
        
        // ✅ REGISTRAR CALLBACK para refrescar cuando se anule una venta
        registrarCallbackAnulacion(() -> {
            System.out.println("Callback activado - Refrescando productos automáticamente...");
            ejecutarSwingWorker(() -> {
                try {
                    getProductsK();
                    System.out.println(" Productos refrescados exitosamente después de anulación");
                } catch (Exception e) {
                    Logger.getLogger(KCOViewController.class.getName()).log(Level.SEVERE, "Error refrescando productos post-anulación", e);
                }
            });
        });
        
        pnlBusquedaProductos.setVisible(false);
        fnd.setIcon(fndTecladoBusqueda);
        placeholder = new TextPrompt("Ingrese el código PLU o el nombre del producto...", jentry_txt);
        pnlTecladoBusqueda.setVisible(false);
        jLabel3.setFont(new java.awt.Font("Roboto", 1, 20));
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(panelteclado.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(pn_teclado.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(pn_menu.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(pn_categorias.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jPanel2.getComponents(), NovusConstante.EXTRABOLD);

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });

        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        jScrollPane3.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });

        jScrollPane3.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane3.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane4.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane4.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane4.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        this.persona = Main.persona;

        limpiarTodos();
        buttonCategoriasSeleccionado = true;
        buttonProductosSeleccionado = false;
        cambioBotonesProductoCategorias();
        mostrarPaneCategorias();
        validarResolucion();
    }

    public void consultarProductos(String busqueda) {
        PanelResultadoBusqueda pnlBusqueda = (PanelResultadoBusqueda) pnlBusquedaProductos;
        pnlBusqueda.consultarProductos(busqueda);
    }

    public void limpiarTodos() {
        movimiento = new MovimientosBean();
        movimiento.setEmpresa(Main.credencial.getEmpresa());
        movimiento.setEmpresasId(Main.credencial.getEmpresas_id());
        movimiento.setPersonaId(persona.getId());
        movimiento.setPersonaNombre(persona.getNombre());
        movimiento.setPersonaApellidos(persona.getApellidos() != null ? persona.getApellidos() : "");
        movimiento.setPersonaNit(persona.getIdentificacion());
        movimiento.setClienteId(2);
        movimiento.setClienteNombre("CLIENTES VARIOS");
        movimiento.setClienteNit("2222222");
        seleccionado = new TreeMap<>();
        jpromotor.setText(persona.getNombre() + " " + (persona.getApellidos() != null ? persona.getApellidos() : ""));
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
        updateConsecutivoXS();
        repaintPanel(true, movimiento);
        resetEntries();
    }

    public void validarResolucion() {
        JsonObject info = obtenerAlertasResolucionUseCase.execute();
        if (info.has("rangoFecha")) {
            int rangoFecha = info.get("rangoFecha").getAsInt();
            int rangoCon = info.get("rangoConsecutivos").getAsInt();
            int alertaDias = info.get("alertaDias").getAsInt();
            int alertConsecutivos = info.get("alertaConsecutivos").getAsInt();

            if (rangoFecha <= alertaDias) {

                if (rangoFecha == alertaDias) {
                    setTimeout(10, () -> {
                        jMensajes.setVisible(false);
                        jMensajes.setText("");
                    });
                    jMensajes.setText("Su resolución vencerá hoy");

                } else {
                    setTimeout(10, () -> {
                        jMensajes.setVisible(false);
                        jMensajes.setText("");
                    });
                    jMensajes.setText("Su resolución vencerá en " + rangoFecha + " Días");
                }
            } else if (rangoCon <= alertConsecutivos) {
                setTimeout(10, () -> {
                    jMensajes.setVisible(false);
                    jMensajes.setText("");
                });
                jMensajes.setText("SU RESOLUCION VENCERÁ POR CONSECUTIVOS");
            }
        }
    }

    public boolean validarCaracteres(KeyEvent evt, String argumentos) {
        boolean caracterValido = false;
        String a = String.valueOf(evt.getKeyChar());
        if (evt.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
            Pattern pat = Pattern.compile(argumentos);
            String texto = a;
            Matcher mat = pat.matcher(texto);
            if (mat.matches()) {
                caracterValido = true;
            }
        }
        return caracterValido;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_container = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jMensajes = new javax.swing.JLabel();
        jfondoMensajes = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        pnlTecladoBusqueda = new javax.swing.JPanel();
        teclado = new TecladoExtendidoGray();
        fnd = new javax.swing.JLabel();
        panelFondo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlBusquedaProductos = PanelResultadoBusqueda.getInstancia(this);
        panelBusqueda = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jentry_txt = new javax.swing.JTextField();
        jSearchBar = new javax.swing.JLabel();
        panelProductos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        panelteclado = new javax.swing.JPanel();
        lbl = new javax.swing.JLabel();
        pnlButtonCategorias = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jCategoria = new javax.swing.JLabel();
        pnlButtonProductos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jProductos = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pn_categorias = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        pn_menu = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        pn_teclado = new javax.swing.JPanel();
        jPanel1 = new TecladoNumericoGrayMedium();
        jcerrar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panelProductosSeleccionados = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        lblSeleccionados = new javax.swing.JLabel();
        pnlButtonQuitarTodos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jQuitarTodos = new javax.swing.JLabel();
        panelResumenVenta = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jsubmenu = new javax.swing.JLabel();
        jSubTotal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jImpuesto = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTotal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        separator1 = new javax.swing.JLabel();
        separator2 = new javax.swing.JLabel();
        jrecibo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jDetalles = new javax.swing.JTextField();
        jpromotor1 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnInventarios = new javax.swing.JLabel();
        btnHistorial = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(null);

        jMensajes.setBackground(new java.awt.Color(255, 255, 255));
        jMensajes.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jMensajes.setForeground(new java.awt.Color(255, 255, 255));
        jMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jMensajes);
        jMensajes.setBounds(80, 722, 420, 50);

        jfondoMensajes.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jfondoMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jfondoMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/notaBorrar.png"))); // NOI18N
        jfondoMensajes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jfondoMensajes);
        jfondoMensajes.setBounds(10, 712, 500, 70);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel11);
        jLabel11.setBounds(10, 10, 70, 71);

        pnlTecladoBusqueda.setBackground(new java.awt.Color(0, 0, 0));
        pnlTecladoBusqueda.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlTecladoBusqueda.setMinimumSize(new java.awt.Dimension(1214, 340));
        pnlTecladoBusqueda.setOpaque(false);
        pnlTecladoBusqueda.setPreferredSize(new java.awt.Dimension(1214, 340));
        pnlTecladoBusqueda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlTecladoBusquedaMouseReleased(evt);
            }
        });
        pnlTecladoBusqueda.setLayout(null);

        teclado.setMinimumSize(new java.awt.Dimension(1030, 343));
        teclado.setOpaque(false);
        teclado.setPreferredSize(new java.awt.Dimension(1030, 343));

        javax.swing.GroupLayout tecladoLayout = new javax.swing.GroupLayout(teclado);
        teclado.setLayout(tecladoLayout);
        tecladoLayout.setHorizontalGroup(
            tecladoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1030, Short.MAX_VALUE)
        );
        tecladoLayout.setVerticalGroup(
            tecladoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 343, Short.MAX_VALUE)
        );

        pnlTecladoBusqueda.add(teclado);
        teclado.setBounds(94, 2, 1030, 343);

        fnd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlTecladoBusqueda.add(fnd);
        fnd.setBounds(-3, -6, 1220, 350);

        jPanel2.add(pnlTecladoBusqueda);
        pnlTecladoBusqueda.setBounds(34, 438, 1214, 340);

        panelFondo.setBackground(new java.awt.Color(241, 240, 245));
        panelFondo.setRoundBottomLeft(14);
        panelFondo.setRoundBottomRight(14);
        panelFondo.setRoundTopLeft(14);
        panelFondo.setRoundTopRight(14);
        panelFondo.setLayout(null);

        pnlBusquedaProductos.setMinimumSize(new java.awt.Dimension(1210, 286));
        pnlBusquedaProductos.setOpaque(false);
        pnlBusquedaProductos.setPreferredSize(new java.awt.Dimension(1210, 286));
        pnlBusquedaProductos.setLayout(null);
        panelFondo.add(pnlBusquedaProductos);
        pnlBusquedaProductos.setBounds(16, 62, 1210, 286);

        panelBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        panelBusqueda.setRoundBottomLeft(20);
        panelBusqueda.setRoundBottomRight(20);
        panelBusqueda.setRoundTopLeft(20);
        panelBusqueda.setRoundTopRight(20);
        panelBusqueda.setLayout(null);

        jentry_txt.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jentry_txt.setForeground(new java.awt.Color(186, 12, 47));
        jentry_txt.setBorder(null);
        jentry_txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jentry_txtFocusGained(evt);
            }
        });
        jentry_txt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jentry_txtMouseClicked(evt);
            }
        });
        jentry_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jentry_txtActionPerformed(evt);
            }
        });
        jentry_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jentry_txtKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jentry_txtKeyTyped(evt);
            }
        });
        panelBusqueda.add(jentry_txt);
        jentry_txt.setBounds(40, 2, 1170, 44);

        jSearchBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jSearchBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/searchbar.png"))); // NOI18N
        jSearchBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panelBusqueda.add(jSearchBar);
        jSearchBar.setBounds(0, 0, 1211, 50);

        panelFondo.add(panelBusqueda);
        panelBusqueda.setBounds(16, 6, 1214, 50);

        panelProductos.setBackground(new java.awt.Color(255, 255, 255));
        panelProductos.setRoundBottomLeft(22);
        panelProductos.setRoundBottomRight(22);
        panelProductos.setRoundTopLeft(22);
        panelProductos.setRoundTopRight(22);
        panelProductos.setLayout(null);

        panelteclado.setMinimumSize(new java.awt.Dimension(512, 600));
        panelteclado.setOpaque(false);
        panelteclado.setLayout(null);

        lbl.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lbl.setForeground(new java.awt.Color(36, 34, 48));
        lbl.setText("Toca para seleccionar");
        panelteclado.add(lbl);
        lbl.setBounds(10, 2, 230, 30);

        jCategoria.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jCategoria.setForeground(new java.awt.Color(186, 12, 47));
        jCategoria.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCategoria.setText("CATEGORIA");
        jCategoria.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCategoriaMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonCategoriasLayout = new javax.swing.GroupLayout(pnlButtonCategorias);
        pnlButtonCategorias.setLayout(pnlButtonCategoriasLayout);
        pnlButtonCategoriasLayout.setHorizontalGroup(
            pnlButtonCategoriasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonCategoriasLayout.createSequentialGroup()
                .addComponent(jCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        pnlButtonCategoriasLayout.setVerticalGroup(
            pnlButtonCategoriasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonCategoriasLayout.createSequentialGroup()
                .addComponent(jCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        panelteclado.add(pnlButtonCategorias);
        pnlButtonCategorias.setBounds(220, 40, 190, 40);

        pnlButtonProductos.setBackground(new java.awt.Color(186, 12, 47));

        jProductos.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jProductos.setForeground(new java.awt.Color(255, 255, 255));
        jProductos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jProductos.setText("PRODUCTOS");
        jProductos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jProductosMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonProductosLayout = new javax.swing.GroupLayout(pnlButtonProductos);
        pnlButtonProductos.setLayout(pnlButtonProductosLayout);
        pnlButtonProductosLayout.setHorizontalGroup(
            pnlButtonProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlButtonProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlButtonProductosLayout.setVerticalGroup(
            pnlButtonProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonProductosLayout.createSequentialGroup()
                .addComponent(jProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        panelteclado.add(pnlButtonProductos);
        pnlButtonProductos.setBounds(16, 40, 190, 40);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.CardLayout());

        pn_categorias.setBackground(new java.awt.Color(255, 255, 255));
        pn_categorias.setOpaque(false);
        pn_categorias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pn_categoriasMouseReleased(evt);
            }
        });
        pn_categorias.setLayout(null);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setOpaque(false);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setMaximumSize(new java.awt.Dimension(600, 490));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel5);

        pn_categorias.add(jScrollPane2);
        jScrollPane2.setBounds(10, 84, 580, 420);

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 488, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(jPanel6);

        pn_categorias.add(jScrollPane3);
        jScrollPane3.setBounds(12, 90, 580, 410);

        jPanel3.add(pn_categorias, "pn_categorias");

        pn_menu.setBackground(new java.awt.Color(255, 255, 255));
        pn_menu.setOpaque(false);
        pn_menu.setLayout(null);

        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel7.setPreferredSize(new java.awt.Dimension(580, 0));
        jPanel7.setLayout(null);
        jScrollPane4.setViewportView(jPanel7);

        pn_menu.add(jScrollPane4);
        jScrollPane4.setBounds(8, 84, 580, 380);

        // Agregar controles de paginación
        String[] pageSizes = {"20", "50", "100"};
        pageSizeCombo = new JComboBox<>(pageSizes);
        pageSizeCombo.addActionListener(e -> {
            ejecutarSwingWorker(() -> {
                int newPageSize = Integer.parseInt((String) Objects.requireNonNull(pageSizeCombo.getSelectedItem()));
                if(newPageSize != pageSize) {
                    currentPage = 0;
                    pageSize = newPageSize;
                    getProductsK();
                }
            });
        });
        pn_menu.add(pageSizeCombo);
        pageSizeCombo.setBounds(20, 480, 60, 30);

        prevButton = new JLabel();
        prevButton.setFont(new java.awt.Font("Arial", 1, 18));
        prevButton.setForeground(new java.awt.Color(255, 255, 255));
        prevButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btnBack.png")));
        prevButton.setText("");
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ejecutarSwingWorker(() -> {
                    if (currentPage > 0) {
                        currentPage--;
                        getProductsK();
                    }
                });
            }
        });
        pn_menu.add(prevButton);
        prevButton.setBounds(475, 480, 60, 40);

        nextButton = new JLabel();
        nextButton.setFont(new java.awt.Font("Arial", 1, 18));
        nextButton.setForeground(new java.awt.Color(255, 255, 255));
        nextButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btnNext.png")));
        nextButton.setText("");
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ejecutarSwingWorker(() -> {
                    if ((long) (currentPage + 1) * pageSize < totalRecords) {
                        currentPage++;
                        getProductsK();
                    }
                });
            }
        });
        pn_menu.add(nextButton);
        nextButton.setBounds(535, 480, 60, 40);

        totalRecordsLabel = new JLabel();
        totalRecordsLabel.setFont(new java.awt.Font("Arial", 1, 12));
        totalRecordsLabel.setForeground(new java.awt.Color(0, 0, 0));
        totalRecordsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        actualizarEtiquetaPaginacion(totalRecords, pageSize, currentPage, totalRecordsLabel);
        totalRecordsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pn_menu.add(totalRecordsLabel);
        totalRecordsLabel.setBounds(370, 480, 120, 40);

        progressButton = new JLabel();
        progressButton.setPreferredSize(new Dimension(35, 30));
        progressButton.setFont(new java.awt.Font("Arial", 1, 18));
        progressButton.setForeground(new java.awt.Color(255, 255, 255));
        progressButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btReintentarsmall.png")));
        progressButton.setText("");
        progressButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pn_menu.add(progressButton);
        progressButton.setBounds(90, 480, 35, 30);

        jPanel3.add(pn_menu, "pn_menu");

        pn_teclado.setBackground(new java.awt.Color(255, 255, 255));
        pn_teclado.setOpaque(false);
        pn_teclado.setLayout(null);

        jPanel1.setPreferredSize(new java.awt.Dimension(440, 325));
        jPanel1.setRequestFocusEnabled(false);
        jPanel1.setLayout(null);
        pn_teclado.add(jPanel1);
        jPanel1.setBounds(14, 36, 550, 470);

        jcerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btCerrarGray.png"))); // NOI18N
        jcerrar.setText("jLabel6");
        jcerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcerrarMouseReleased(evt);
            }
        });
        pn_teclado.add(jcerrar);
        jcerrar.setBounds(550, 0, 50, 50);
        pn_teclado.add(jLabel2);
        jLabel2.setBounds(-6, -5, 630, 650);

        jPanel3.add(pn_teclado, "pn_teclado");

        panelteclado.add(jPanel3);
        jPanel3.setBounds(0, 8, 620, 550);

        panelProductos.add(panelteclado);
        panelteclado.setBounds(3, 0, 614, 600);

        panelFondo.add(panelProductos);
        panelProductos.setBounds(14, 64, 600, 530);

        panelProductosSeleccionados.setBackground(new java.awt.Color(255, 255, 255));
        panelProductosSeleccionados.setRoundBottomLeft(20);
        panelProductosSeleccionados.setRoundBottomRight(20);
        panelProductosSeleccionados.setRoundTopLeft(20);
        panelProductosSeleccionados.setRoundTopRight(20);
        panelProductosSeleccionados.setLayout(null);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(598, 0));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(460, 0));
        jPanel4.setLayout(null);
        jScrollPane1.setViewportView(jPanel4);

        panelProductosSeleccionados.add(jScrollPane1);
        jScrollPane1.setBounds(13, 55, 580, 200);

        lblSeleccionados.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        lblSeleccionados.setForeground(new java.awt.Color(36, 34, 48));
        lblSeleccionados.setText("Productos seleccionados ");
        panelProductosSeleccionados.add(lblSeleccionados);
        lblSeleccionados.setBounds(20, 10, 260, 30);

        pnlButtonQuitarTodos.setRoundBottomLeft(22);
        pnlButtonQuitarTodos.setRoundBottomRight(22);
        pnlButtonQuitarTodos.setRoundTopLeft(22);
        pnlButtonQuitarTodos.setRoundTopRight(22);

        jQuitarTodos.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jQuitarTodos.setForeground(new java.awt.Color(102, 102, 102));
        jQuitarTodos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jQuitarTodos.setText("QUITAR TODOS");
        jQuitarTodos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jQuitarTodos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jQuitarTodosMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonQuitarTodosLayout = new javax.swing.GroupLayout(pnlButtonQuitarTodos);
        pnlButtonQuitarTodos.setLayout(pnlButtonQuitarTodosLayout);
        pnlButtonQuitarTodosLayout.setHorizontalGroup(
            pnlButtonQuitarTodosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlButtonQuitarTodosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jQuitarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlButtonQuitarTodosLayout.setVerticalGroup(
            pnlButtonQuitarTodosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonQuitarTodosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jQuitarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelProductosSeleccionados.add(pnlButtonQuitarTodos);
        pnlButtonQuitarTodos.setBounds(12, 270, 580, 50);

        panelFondo.add(panelProductosSeleccionados);
        panelProductosSeleccionados.setBounds(624, 63, 604, 330);

        panelResumenVenta.setBackground(new java.awt.Color(255, 255, 255));
        panelResumenVenta.setRoundBottomLeft(20);
        panelResumenVenta.setRoundBottomRight(20);
        panelResumenVenta.setRoundTopLeft(20);
        panelResumenVenta.setRoundTopRight(20);
        panelResumenVenta.setLayout(null);

        jsubmenu.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jsubmenu.setForeground(new java.awt.Color(153, 153, 153));
        jsubmenu.setText("Subtotal      ");
        panelResumenVenta.add(jsubmenu);
        jsubmenu.setBounds(28, 46, 270, 40);

        jSubTotal.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jSubTotal.setForeground(new java.awt.Color(153, 153, 153));
        jSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jSubTotal.setText("0");
        panelResumenVenta.add(jSubTotal);
        jSubTotal.setBounds(350, 54, 230, 26);

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Impuestos   ");
        panelResumenVenta.add(jLabel1);
        jLabel1.setBounds(28, 88, 270, 30);

        jImpuesto.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jImpuesto.setForeground(new java.awt.Color(153, 153, 153));
        jImpuesto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jImpuesto.setText("0");
        panelResumenVenta.add(jImpuesto);
        jImpuesto.setBounds(360, 88, 220, 30);

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("TOTAL A PAGAR ");
        panelResumenVenta.add(jLabel5);
        jLabel5.setBounds(28, 122, 300, 50);

        jTotal.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jTotal.setForeground(new java.awt.Color(51, 51, 51));
        jTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jTotal.setText("0");
        panelResumenVenta.add(jTotal);
        jTotal.setBounds(340, 122, 240, 50);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel3.setText("Valor de la venta");
        panelResumenVenta.add(jLabel3);
        jLabel3.setBounds(26, 12, 170, 27);

        separator1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separatorSmall.png"))); // NOI18N
        separator1.setText("jLabel6");
        panelResumenVenta.add(separator1);
        separator1.setBounds(30, 84, 550, 1);

        separator2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separatorSmall.png"))); // NOI18N
        separator2.setText("jLabel6");
        panelResumenVenta.add(separator2);
        separator2.setBounds(30, 124, 550, 1);

        panelFondo.add(panelResumenVenta);
        panelResumenVenta.setBounds(624, 400, 604, 184);

        jPanel2.add(panelFondo);
        panelFondo.setBounds(20, 92, 1242, 604);

        jrecibo.setBackground(new java.awt.Color(255, 255, 255));
        jrecibo.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jrecibo.setForeground(new java.awt.Color(255, 255, 255));
        jrecibo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jrecibo.setText("10000");
        jPanel2.add(jrecibo);
        jrecibo.setBounds(120, 40, 220, 40);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 219, 0));
        jLabel4.setText("VENTA No.");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(120, 10, 220, 30);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        jPanel2.add(jclock);
        jclock.setBounds(1140, 10, 110, 66);

        jDetalles.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jDetalles.setBorder(null);
        jDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDetallesActionPerformed(evt);
            }
        });
        jDetalles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jDetallesKeyReleased(evt);
            }
        });
        jPanel2.add(jDetalles);
        jDetalles.setBounds(1020, 0, 0, 50);

        jpromotor1.setBackground(new java.awt.Color(186, 12, 47));
        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 18)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 219, 0));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor1.setText("PROMOTOR");
        jpromotor1.setOpaque(true);
        jPanel2.add(jpromotor1);
        jpromotor1.setBounds(920, 6, 160, 30);

        jpromotor.setBackground(new java.awt.Color(186, 12, 47));
        jpromotor.setFont(new java.awt.Font("Conthrax", 1, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor.setText("PROMOTOR");
        jpromotor.setOpaque(true);
        jPanel2.add(jpromotor);
        jpromotor.setBounds(610, 30, 480, 50);

        jLabel16.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jLabel16.setText("COMPLETAR VENTA");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel16MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel16);
        jLabel16.setBounds(920, 720, 280, 60);

        jLabel17.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-xsmall.png"))); // NOI18N
        jLabel17.setText("BORRAR");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel17MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel17);
        jLabel17.setBounds(790, 720, 123, 60);

        btnInventarios.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnInventarios.setForeground(new java.awt.Color(255, 255, 255));
        btnInventarios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnInventarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-warning-xsmall.png"))); // NOI18N
        btnInventarios.setText("INVENTARIOS");
        btnInventarios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInventarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnInventariosMouseReleased(evt);
            }
        });
        jPanel2.add(btnInventarios);
        btnInventarios.setBounds(520, 720, 130, 60);

        btnHistorial.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnHistorial.setForeground(new java.awt.Color(255, 255, 255));
        btnHistorial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnHistorial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-warning-xsmall.png"))); // NOI18N
        btnHistorial.setText("HISTORIAL");
        btnHistorial.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHistorial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHistorialMouseReleased(evt);
            }
        });
        jPanel2.add(btnHistorial);
        btnHistorial.setBounds(650, 720, 130, 60);

        jLabel7.setForeground(new java.awt.Color(204, 51, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel7);
        jLabel7.setBounds(0, 0, 1280, 800);
        jPanel2.add(jLabel6);
        jLabel6.setBounds(540, 30, 0, 0);

        pnl_container.add(jPanel2, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jentry_txtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jentry_txtKeyTyped
        if (ingresandoCantidad) {
            validacionCaracteresCantidad(evt);
        } else {
            String caracteresAceptados = "[0-9a-zA-Z\\s]";
            NovusUtils.limitarCarateres(evt, jentry_txt, 200, jLabel6, caracteresAceptados);
            consultaRapidaProductos();
        }
    }//GEN-LAST:event_jentry_txtKeyTyped

    private void jentry_txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jentry_txtFocusGained
        NovusUtils.deshabilitarCopiarPegar(jentry_txt);
    }//GEN-LAST:event_jentry_txtFocusGained

    private void vinetaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_vinetaMouseReleased
    }

    private void jLabel16MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseReleased
        cerrarPanelTecladoConfirmarVenta();
        mostrarPanelTecladoBusqueda(false);
        PanelResultadoBusqueda.limpiarInstancia();
        StoreConfirmarViewController.CANASTILLA = false;
        generarVenta();
    }//GEN-LAST:event_jLabel16MouseReleased

    private void jentry_txtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jentry_txtMouseClicked
        if (!ingresandoCantidad) {
            visiblePaneTecladoBusqueda();
            showMensajes(null, null);
            if (jentry_txt.getText().isEmpty()) {
                panelBusquedaInstance.consultarProductos("");
            }
        }
    }//GEN-LAST:event_jentry_txtMouseClicked

    private void jCategoriaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCategoriaMouseReleased
        mostrarPaneCategorias();
        buttonCategoriasSeleccionado = true;
        buttonProductosSeleccionado = false;
        mostrarPanelTecladoBusqueda(false);
        cambioBotonesProductoCategorias();
    }//GEN-LAST:event_jCategoriaMouseReleased

    private void jLabel17MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseReleased
        mostrarPanelTecladoBusqueda(false);
        borrar();
    }//GEN-LAST:event_jLabel17MouseReleased

    private void btnInventariosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInventariosMouseReleased
        mostrarPanelTecladoBusqueda(false);
        InventarioProductosViewController inv = new InventarioProductosViewController(this, true, true);
        inv.setVisible(true);
    }//GEN-LAST:event_btnInventariosMouseReleased

    private void validacionCaracteresCantidad(KeyEvent evt) {
        char c = evt.getKeyChar();
        String cantidadDigitada = jentry_txt.getText();
        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        } else if (c == '.' && cantidadDigitada.contains(".")) {
            evt.consume();
        } else if (cantidadDigitada.contains(".") && cantidadDigitada.substring(cantidadDigitada.indexOf(".")).length() >= 3) {
            evt.consume();
        }
    }

    private void btnHistorialMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel14MouseReleased
        mostrarPanelTecladoBusqueda(false);
        VentasHistorialKCOView hist = new VentasHistorialKCOView(parent, true);
        hist.setVisible(true);

    }

    private void consultaRapidaProductos() {
        if (!KCOViewController.writing) {
            NovusUtils.printLn("Bloque valido");
            KCOViewController.writing = true;
            NovusUtils.debounce(() -> consultarProductos(jentry_txt.getText()), 1.6f);
        } else {
            NovusUtils.printLn("En Espera");
        }
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnl_container.getLayout();
        pnl_container.add("pnl_ext", panel);
        layout.show(pnl_container, "pnl_ext");
    }

    public void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnl_container.getLayout();
        layout.show(pnl_container, "pnl_principal");
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "home");
    }
    private void jcerrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcerrarMouseReleased
        cerrarTeclado();
        iconosIngresarBusqueda();
    }//GEN-LAST:event_jcerrarMouseReleased

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseReleased
        mostrarPanelTecladoBusqueda(false);
        PanelResultadoBusqueda.limpiarInstancia();
        // Limpiar callback al cerrar
        onAnulacionExitosaCallback = null;
        cerrar();
    }//GEN-LAST:event_jLabel11MouseReleased

    private void jProductosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProductosMouseReleased
        mostrarPaneMenu();
        buttonCategoriasSeleccionado = false;
        buttonProductosSeleccionado = true;
        mostrarPanelTecladoBusqueda(false);
        cambioBotonesProductoCategorias();
    }//GEN-LAST:event_jProductosMouseReleased

    private void jentry_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jentry_txtActionPerformed
        boolean presiono = ((TecladoNumericoGrayMedium) jPanel1).isPresiono();
        if (presiono) {
            ingresarCantidad(jentry_txt.getText());
        } else {
            consultaRapidaProductos();
        }
        ((TecladoNumericoGrayMedium) jPanel1).setPresiono(false);
    }//GEN-LAST:event_jentry_txtActionPerformed

    private void jQuitarTodosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jQuitarTodosMouseReleased
        mostrarPanelTecladoBusqueda(false);
        quitarTodos();
    }//GEN-LAST:event_jQuitarTodosMouseReleased

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseReleased
        mostrarPanelTecladoBusqueda(false);
    }//GEN-LAST:event_jLabel7MouseReleased

    private void pn_categoriasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pn_categoriasMouseReleased
        mostrarPanelTecladoBusqueda(false);
    }//GEN-LAST:event_pn_categoriasMouseReleased

    private void pnlTecladoBusquedaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTecladoBusquedaMouseReleased

    }//GEN-LAST:event_pnlTecladoBusquedaMouseReleased

    private void jentry_txtKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jentry_txtKeyReleased
        onChanged(evt);
    }

    private void breadcumb_categoriaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_breadcumb_categoriaMouseReleased
        mostrarPaneCategorias();
    }

    private void jDetallesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jDetallesActionPerformed
        boolean presiono = ((TecladoNumericoGrayMedium) jPanel1).isPresiono();
        if (presiono) {
            agregarProducto("TECLADO", jDetalles.getText(), null);
        } else {
            agregarProducto("LECTOR", jDetalles.getText(), null);
        }
        ((TecladoNumericoGrayMedium) jPanel1).setPresiono(false);
    }// GEN-LAST:event_jDetallesActionPerformed

    private void jDetallesKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jDetallesKeyReleased
        onChanged(evt);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnHistorial;
    private javax.swing.JLabel btnInventarios;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel jCategoria;
    private javax.swing.JTextField jDetalles;
    private javax.swing.JLabel jImpuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jMensajes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel jProductos;
    private javax.swing.JLabel jQuitarTodos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel jSearchBar;
    private javax.swing.JLabel jSubTotal;
    private javax.swing.JLabel jTotal;
    public javax.swing.JLabel jcerrar;
    private javax.swing.JPanel jclock;
    public static javax.swing.JTextField jentry_txt;
    public static javax.swing.JLabel jfondoMensajes;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JLabel jrecibo;
    private javax.swing.JLabel jsubmenu;
    public javax.swing.JLabel lbl;
    public javax.swing.JLabel lblSeleccionados;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelBusqueda;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelFondo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelProductos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelProductosSeleccionados;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelResumenVenta;
    private javax.swing.JPanel panelteclado;
    private javax.swing.JPanel pn_categorias;
    private javax.swing.JPanel pn_menu;
    private javax.swing.JPanel pn_teclado;
    private javax.swing.JPanel pnlBusquedaProductos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlButtonCategorias;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlButtonProductos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlButtonQuitarTodos;
    private javax.swing.JPanel pnlTecladoBusqueda;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JLabel separator1;
    private javax.swing.JLabel separator2;
    private javax.swing.JPanel teclado;
    // End of variables declaration//GEN-END:variables

    private void cerrarPanelTecladoConfirmarVenta() {
        cerrarTeclado();
        iconosIngresarBusqueda();
    }

    private void cerrar() {
        if (isCanastaVacia()) {
            salir(true);
        }
    }

    public void resetEntries() {
        jentry_txt.setText("");
    }

    boolean isMixto(long tipoProducto) {
        boolean isMixto = false;
        if (tipoProducto == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() != NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                    isMixto = true;
                    break;
                }
            }
        } else {
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                    isMixto = true;
                    break;
                }
            }
        }
        return isMixto;
    }

    MovimientosDetallesBean findByPlu(String codigo) {
        codigo = codigo.trim();
        MovimientosDetallesBean productoConsultado = null;
        for (MovimientosDetallesBean producto : lista) {
            if (producto.getPlu().trim().equals(codigo)) {
                productoConsultado = producto;
                break;
            }
        }
        return productoConsultado;
    }

    MovimientosDetallesBean findByBarCode(String codigo) {
        codigo = codigo.trim();
        MovimientosDetallesBean productoConsultado = null;
        for (MovimientosDetallesBean producto : lista) {
            if (producto.getCodigoBarra().trim().equals(codigo)) {
                productoConsultado = producto;
                break;
            }
        }
        return productoConsultado;
    }

    public void agregarProducto(String origen, String codigo, MovimientosDetallesBean producTemp) {
        jentry_txt.requestFocus();
        jentry_txt.setText("");
        if (!codigo.equals("")) {
            try {
                MovimientosDao dao = new MovimientosDao();
                if (producTemp == null) {
                    if (origen.equals("TECLADO")) {
                        //producTemp = dao.findByPluKIOSCO(codigo);
                        producTemp = new BuscarProductoPorPluKioskoUseCase(codigo).execute();
                    } else {
                        //producTemp = dao.findByBarCodeKIOSCO(codigo);
                        producTemp = new BuscarProductoPorCodigoBarraUseCase(codigo).execute();
                    }
                }
                if (seleccion == null) {
                    if (producTemp != null && producTemp.getEstado() != null && producTemp.getEstado().equals(NovusConstante.ACTIVE)) {
                        if ((producTemp.getCantidadIngredientes() == producTemp.getIngredientes().size())
                                && (producTemp.getCantidadImpuestos() == producTemp.getImpuestos().size())) {
                            jentry_txt.setText("");
                            showMensajes(null, null);

                            MovimientosDetallesBean producto = movimiento.getDetalles().get(producTemp.getId());
                            if (producto == null) {
                                if (isMixto(producTemp.getTipo())) {
                                    showMensajes("Operacion no permitida", "notaBorrar.png");
                                    mostrarPanelTecladoBusqueda(false);
                                } else {
                                    System.out.println(" Usando costo por defecto para producto: " + producTemp.getDescripcion());
                                    agregaProductoAlista(producTemp != null && producTemp.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA ? producTemp.getCantidadUnidad() : 1, producTemp);
                                }
                            } else {
                                if (Main.FACTURACION_NEGATIVO) {
                                    int cantidadIngreado = (int) producto.getCantidadUnidad() + 1;
                                    agregaProductoAlista(cantidadIngreado, producTemp);
                                } else {
                                    if (!producto.isCompuesto()
                                            && producto.getCantidadUnidad() >= producto.getSaldo()) {
                                        showMensajes("Solo puede vender maximo (" + producto.getSaldo() + ")",
                                                "notaBorrar.png");
                                    } else {
                                        agregaProductoAlista(1, producTemp);
                                    }
                                }
                            }
                        } else {
                            if (producTemp.getCantidadIngredientes() != producTemp.getIngredientes().size()) {
                                showMensajes("Descarga incompleta de ingredientes", "notaBorrar.png");

                            } else if (producTemp.getCantidadImpuestos() != producTemp.getImpuestos().size()) {
                                showMensajes("Descarga incompleta de impuestos", "notaBorrar.png");
                            }
                        }
                    } else if (producTemp != null && producTemp.getEstado() != null && producTemp.getEstado().equals(NovusConstante.BLOQUEADO)) {
                        showMensajes("Producto bloqueado", "notaBorrar.png");
                    } else {
                        showMensajes("Producto no encontrado", "notaBorrar.png");
                    }
                    repaintPanel(true, movimiento);
                } else {
                    // No agrega solo cambia la cantidad de la unidad seleccionada
                    if (!codigo.trim().equals("0")) {
                        try {
                            int cant = Integer.parseInt(codigo);
                            if (cant <= 0) {
                                throw new NumberFormatException("Valor no soportado ");
                            }
                            MovimientosDetallesBean producto = movimiento.getDetalles().get(seleccion.getId());

                            if (producto.isCompuesto() || cant <= producto.getSaldo() || Main.FACTURACION_NEGATIVO) {
                                boolean puedeAgregar = true;

                                if (producto.isCompuesto()) {
                                    if (!Main.FACTURACION_NEGATIVO) {
                                        puedeAgregar = validaExistenciaIngredientes(cant, producto);
                                    }
                                }

                                if (puedeAgregar) {
                                    agregaProductoAlista(cant, producto);
                                    repaintPanel(true, movimiento);
                                    jentry_txt.setText("");
                                    showMensajes(null, null);
                                    // jMensajes.setText("");
                                } else {
                                    showMensajes("Uno de los ingredientes sin cantidad solicitada", "notaBorrar.png");
                                }
                            } else {
                                showMensajes("Solo se puede vender un maximo de (" + producto.getSaldo() + ")",
                                        "notaBorrar.png");
                                repaintPanel(true, movimiento);
                                mostrarPanelTecladoBusqueda(false);
                            }
                        } catch (NumberFormatException e) {
                            seleccion = null;
                            jentry_txt.setText("");
                            showMensajes(null, null);
                            repaintPanel(true, movimiento);
                            showMensajes("Operacion no permitida", "notaBorrar.png");
                            mostrarPanelTecladoBusqueda(false);
                        }
                    } else {
                        showMensajes("Operacion no permitida", "notaBorrar.png");
                        mostrarPanelTecladoBusqueda(false);
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(InfoViewController.class
                        .getName()).log(Level.SEVERE, null, ex);
                showMensajes("Ocurrio un error inesperado", "notaBorrar.png");
                mostrarPanelTecladoBusqueda(false);
            }
        } else {
            if (movimiento.getDetalles().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                showMensajes("CANASTA VACIA", "notaBorrar.png");
            } else {
                generarVenta();
            }
        }
        seleccion = null;
    }

    public void ingresarCantidad(String codigo) {
        if (!codigo.trim().equals("0")) {
            try {
                float cant = Float.parseFloat(codigo);
                if (cant <= 0) {
                    throw new NumberFormatException("Valor no soportado ");
                }
                MovimientosDetallesBean producto = movimiento.getDetalles().get(seleccion.getId());
                if (producto.isCompuesto() || cant <= producto.getSaldo() || Main.FACTURACION_NEGATIVO) {
                    boolean puedeAgregar = true;

                    if (producto.isCompuesto()) {
                        if (!Main.FACTURACION_NEGATIVO) {
                            puedeAgregar = validaExistenciaIngredientes(cant, producto);
                        }
                    }
                    if (puedeAgregar) {
                        agregaProductoAlista(cant, producto);
                        repaintPanel(true, movimiento);
                        jentry_txt.setText("");
                        showMensajes(null, null);
                    } else {
                        showMensajes("Uno de los ingredientes sin cantidad solicitada", "notaBorrar.png");
                    }
                } else {
                    showMensajes("Solo se puede vender un maximo de(" + producto.getSaldo() + ")",
                            "notaBorrar.png");
                    repaintPanel(true, movimiento);
                }
            } catch (NumberFormatException e) {
                seleccion = null;
                jentry_txt.setText("");
                showMensajes(null, null);
                repaintPanel(true, movimiento);
                showMensajes("Operacion no permitida", "notaBorrar.png");
                mostrarPanelTecladoBusqueda(false);
            }
        } else {
            showMensajes("Operacion no permitida", "notaBorrar.png");
            mostrarPanelTecladoBusqueda(false);
        }
        cerrarPanelTecladoConfirmarVenta();
    }

    public void generarVenta() {
        if (movimiento.getDetalles().isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            showMensajes("CANASTA VACIA", "notaBorrar.png");
        } else {
            jentry_txt.setText("");
            jMensajes.setText("");
            showMensajes(null, null);
            movimiento.setGrupoJornadaId(persona.getGrupoJornadaId());
            movimiento.setConsecutivo(this.newConsecutivo);
            System.out.println(Main.ANSI_BLUE + "Movimiento Consecutivo: " + movimiento.getConsecutivo() + Main.ANSI_RESET);
            if (manual) {
                try {
                    Date fechaTransaccion = sdf.parse(this.fecha);
                    movimiento.setFecha(fechaTransaccion);
                } catch (ParseException ex) {
                    Logger.getLogger(KCOViewController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                movimiento.setFecha(new Date());
            }
            movimiento.setOperacionId(NovusConstante.MOVIMIENTO_TIPO_KIOSCO);

            MediosPagosBean efectivoInicial = new MediosPagosBean();
            TreeMap<Long, MediosPagosBean> _medios = new TreeMap<>();
            efectivoInicial.setId(1);
            efectivoInicial.setValor(movimiento.getVentaTotal());
            efectivoInicial.setRecibido(movimiento.getVentaTotal());
            efectivoInicial.setDescripcion("EFECTIVO");
            efectivoInicial.setCambio(true);
            efectivoInicial.setCambio(0);
            _medios.put(1L, efectivoInicial);
            movimiento.setMediosPagos(_medios);

            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean detalle = entry.getValue();
                float totalImpuesto = 0;
                for (ImpuestosBean impuesto : detalle.getImpuestos()) {
                    totalImpuesto += impuesto.getCalculado();
                }
                detalle.setTotalImpuestos(totalImpuesto);
                detalle.setSubtotal(detalle.getSubtotal());
            }

            boolean esFactura = false;
            boolean esNuevaVenta = true;
            StoreConfirmarViewController ctd = new StoreConfirmarViewController(this, this.parent, true, movimiento, esNuevaVenta, esFactura, manual, ventanaVenta);
            ctd.setVisible(true);
        }
    }

    private void onChanged(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                && jentry_txt.getText().trim().equals("")) {
            borrar();
        }
    }

    public boolean isCanastaVacia() {
        boolean canastaVacia = true;
        for (Map.Entry<Long, MovimientosDetallesBean> entry : seleccionado.entrySet()) {
            canastaVacia = false;
            MovimientosDetallesBean value = entry.getValue();
            movimiento.getDetalles().remove(value.getId());
        }
        return canastaVacia;
    }

    private void borrar() {
        NovusUtils.printLn("borrar producto");
        boolean canastaVacia = isCanastaVacia();
        if (!canastaVacia) {
            repaintPanel(true, movimiento);
            cantidad--;
            seleccionado.clear();
            seleccion = null;
            showMensajes(null, null);
        }
        resetEntries();
        iconosIngresarBusqueda();
        cerrarTeclado();
    }

    public MovimientosBean repaintPanel(boolean vista, MovimientosBean movimiento) {

        if (vista) {
            AdjustmentListener ajuste = new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    e.getAdjustable().setValue(Integer.MAX_VALUE);
                    jScrollPane1.getVerticalScrollBar().removeAdjustmentListener(this);
                }
            };
            jScrollPane1.getVerticalScrollBar().addAdjustmentListener(ajuste);
            jPanel4.removeAll();
        }
        int cant = 1;
        movimiento.setCostoTotal(0);
        movimiento.setVentaTotal(0);
        movimiento.setImpuestoTotal(0);
        movimiento.setTotalImpuestosSinImpocunsumos(0);
        double totalImpuestoSinImpoconsumo = 0;
        medios = new ArrayList<>();
        if (!movimiento.getDetalles().isEmpty()) {
            float impuestoTotal = 0;
            float subtotales = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean producto = entry.getValue();

                float precioOriginal = producto.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA ? producto.getCosto() : producto.getPrecio() * producto.getCantidadUnidad();
                float precioBase;
                float impuestoProducto = 0;
                float impuestoTotalProducto = 0;
                float impoconsumo = 0;
                float costoTotal;
                if (producto.getImpuestos() != null) {
                    float impuestoCalculado;
                    for (ImpuestosBean impuesto : producto.getImpuestos()) {
                        if (!impuesto.getPorcentaje_valor().equals(NovusConstante.SIMBOLS_PERCENTAGE)) {
                            impuestoCalculado = impuesto.getValor() * producto.getCantidadUnidad();
                            impuestoProducto += impuestoCalculado;
                            impoconsumo += impuestoCalculado;
                            impuesto.setCalculado(impuestoCalculado);
                        }
                    }
                    precioBase = precioOriginal;
                    float preciobaseSinImpo = precioBase - impuestoProducto;
                    impuestoCalculado = 0;
                    impuestoProducto = 0;
                    float base = 0;
                    for (ImpuestosBean impuesto : producto.getImpuestos()) {
                        if (impuesto.getPorcentaje_valor().equals(NovusConstante.SIMBOLS_PERCENTAGE)) {
                            float tarifaIva = (impuesto.getValor() / 100f);
                            base = preciobaseSinImpo / (tarifaIva + 1f);
                            impuestoCalculado = (float) preciobaseSinImpo / ((impuesto.getValor() / 100f) + 1f);
                            impuestoCalculado = impuestoCalculado * (impuesto.getValor() / 100f);
                            impuestoProducto = impuestoCalculado;
                            impuesto.setCalculado(impuestoCalculado);
                            totalImpuestoSinImpoconsumo = totalImpuestoSinImpoconsumo + impuestoCalculado;
                            movimiento.setTotalImpuestosSinImpocunsumos(totalImpuestoSinImpoconsumo);
                            movimiento.setImpuestoU(impuestoProducto);
                            impuesto.setBase(base);
                        }
                    }
                    impuestoTotalProducto = impuestoTotalProducto + impuestoProducto;
                    if (!producto.getImpuestos().isEmpty()) {
                        impuestoTotal = impuestoTotal + impuestoTotalProducto;
                        movimiento.setImpuestoTotal(impuestoCalculado + movimiento.getImpuestoTotal());
                        producto.setSubtotal(precioOriginal - impuestoCalculado);
                    } else {
                        producto.setSubtotal(precioOriginal);
                    }
                    subtotales = subtotales + producto.getSubtotal();
                }
                if (vista) {
                    if (cant > 5) {
                        jPanel4.setPreferredSize(new java.awt.Dimension(jPanel4.getWidth(), jPanel4.getHeight() + 40));
                        jPanel4.setBounds(0, 0, jPanel4.getWidth(), jPanel4.getHeight() + 20);
                    }
                    producto.setItem(cant);

                    PedidoItemView jitem = new PedidoItemView(producto, jScrollPane1, seleccionado, this);
                    jPanel4.add(jitem);
                    jitem.setBounds(0, (cant - 1) * 40, 590, 40);
                    producto.setPanelView(jitem);
                    producto.getPanelView().updateProductoView(producto);
                }
                // Corregido: usar el costo real para productos simples
                if (producto.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                    producto.setCosto(producto.getCosto());
                } else if (producto.isCompuesto()) {
                    producto.setCosto(producto.getProducto_compuesto_costo() * producto.getCantidadUnidad());
                } else {
                    producto.setCosto(producto.getCosto());
                }
                float total = producto.getSubtotal() + impuestoProducto;
                movimiento.setVentaTotal(movimiento.getVentaTotal() + total);
                movimiento.setCostoTotal(producto.getCosto() + movimiento.getCostoTotal());
                if (vista) {
                    jSubTotal.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(movimiento.getVentaTotal() - movimiento.getImpuestoTotal()));
                    jImpuesto.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(movimiento.getImpuestoTotal()));
                    jTotal.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(movimiento.getVentaTotal()));
                }
                cant++;
                if (primeraVenta) {
                    new VentaEnCursoUseCase("A", NovusUtils.getTipoNegocioComplementario()).execute();
                }
                primeraVenta = false;
            }
        } else {
            NovusUtils.printLn("cerrando venta");
            primeraVenta = true;
            new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
            movimiento.setVentaTotal(0);
            if (vista) {
                jImpuesto.setText(NovusConstante.SIMBOLS_PRICE + " 0");
                jSubTotal.setText(NovusConstante.SIMBOLS_PRICE + " 0");
                jTotal.setText(NovusConstante.SIMBOLS_PRICE + " 0");
                jentry_txt.setText("");
            }
        }
        if (vista) {
            jPanel4.validate();
            jPanel4.repaint();
            jScrollPane1.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMaximum());
        }
        cambioBotonQuitarTodos();
        return movimiento;
    }

    void loadPromociones() {
        panelPromociones = new CategoriaDetallesListView(null, this);
        jPanel2.add(panelPromociones, 0);
        panelPromociones.setBounds(0, 0, 1024, 600);
        panelPromociones.setVisible(true);
    }

    void restoreView() {
        hiddenAll();
        jentry_txt.requestFocus();
        jentry_txt.setEditable(true);
        panelteclado.setVisible(true);
    }

    void hiddenAll() {
        if (panelteclado != null) {
            panelteclado.setVisible(false);
        }
        if (panelPromociones != null) {
            panelPromociones.setVisible(false);
        }

    }

    public void listarProductos(long tipo, JPanel panel) {
        int i = 1;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 580;
        int altoComponente = 65;
        int columnas = 1;
        int margenx = 5;
        int margeny = 5;
        panel.removeAll();
        for (MovimientosDetallesBean producto : lista) {

            if (componentesX == columnas) {
                componentesX = 0;
                componentesY++;
            }
            CategoriaProductoListItem jitem = new CategoriaProductoListItem(producto, this);
            panel.add(jitem);
            jitem.setBounds(componentesX * (ancho + margenx), componentesY * (altoComponente + margeny), ancho,
                    altoComponente);
            componentesX++;
            i++;

        }

        int altoInicial = (lista.size() / columnas) + 1;
        int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
        if (altoFinal >= panel.getHeight()) {
            panel.setPreferredSize(new java.awt.Dimension(panel.getWidth(), altoFinal));
            panel.setBounds(0, 0, panel.getWidth(), altoFinal);
        }
        panel.validate();
        panel.repaint();
    }

    public JPanel getContenedor() {
        return jPanel2;
    }

    public void agregarCanastillaAPanel() {
        for (Map.Entry<Long, MovimientosDetallesBean> entry : seleccionado.entrySet()) {
            MovimientosDetallesBean producTemp = entry.getValue();
            agregaProductoAlista(1, producTemp);
        }
    }

    private void agregaProductoAlista(float cantidadSolicitada, MovimientosDetallesBean producTemp) {

        if (producTemp.getBodegasId() == 0) {
            String logMessage = String.format("Producto %d %s no tiene asignada una Bodega en Base de Datos",
                                              producTemp.getId(), 
                                              producTemp.getDescripcion());
            NovusUtils.printLn(logMessage);
            
            showMensajes("Producto no tiene bodega asociada", "notaBorrar.png");
            return;
        }
        
        if (!producTemp.isCompuesto()) {
            if (Main.FACTURACION_NEGATIVO) {
                if (jMensajes.getText().contains("PRECIO")) {
                    if (producTemp.isCombustible()) {
                        producTemp.setSubtotal(0);
                    } else {
                        producTemp.setCantidadUnidad(cantidadSolicitada / producTemp.getPrecio());
                        producTemp.setSubtotal(cantidadSolicitada);

                    }
                } else {
                    producTemp.setCantidadUnidad(cantidadSolicitada);
                    if (producTemp.isCombustible()) {
                        producTemp.setSubtotal(0);
                    } else {
                        producTemp.setSubtotal(producTemp.getPrecio() * producTemp.getCantidadUnidad());
                    }
                }

                cargaProducto(producTemp);
            } else {
                if (producTemp.getCantidadUnidad() > 0) {
                    if (producTemp.getCantidadUnidad() < cantidadSolicitada) {
                        showMensajes("Cantidad solicitada no permitida", "notaBorrar.png");
                    } else {
                        producTemp.setCantidadUnidad(cantidadSolicitada);
                        cargaProducto(producTemp);
                    }
                } else {
                    showMensajes("Sin existencia en bodega", "notaBorrar.png");
                }
            }
        } else {
            boolean existencia = true;
            ProductoBean faltante = null;
            if (!producTemp.getIngredientes().isEmpty()) {
                producTemp.setProducto_compuesto_costo(0);
                for (ProductoBean ingrediente : producTemp.getIngredientes()) {

                    if (ingrediente.isCompuesto()) {
                        existencia = validaExistenciaIngredientes(Integer.parseInt(cantidadSolicitada + ""), ingrediente);
                    } else {
                        if (ingrediente.getCantidadUnidad() < 0
                                || ingrediente.getCantidadUnidad() < (ingrediente.getProducto_compuesto_cantidad()
                                * cantidadSolicitada)) {
                            existencia = false;
                        }
                    }
                    if (!existencia) {
                        if (Main.FACTURACION_NEGATIVO) {
                            existencia = true;
                            producTemp.setProducto_compuesto_costo(producTemp.getProducto_compuesto_costo() + ingrediente.getProducto_compuesto_cantidad());
                        } else {
                            faltante = ingrediente;
                        }
                        break;
                    } else {
                        producTemp.setProducto_compuesto_costo(
                                producTemp.getProducto_compuesto_costo() + ingrediente.getProducto_compuesto_cantidad()
                                * ingrediente.getProducto_compuesto_costo());
                        // producTemp.setCostos(producTemp.getProducto_compuesto_costo());
                    }
                }
            } else {
                existencia = false;
            }
            if (existencia) {
                producTemp.setCantidadUnidad(cantidadSolicitada);
                cargaProducto(producTemp);
            } else {
                if (faltante != null) {
                    showMensajes("Falta ingrediente: " + faltante.getDescripcion(), "notaBorrar.png");
                } else {
                    showMensajes("Producto sin existencia", "notaBorrar.png");
                }
            }
        }
    }

    void cargaProducto(MovimientosDetallesBean producTemp) {
        MovimientosDetallesBean producto = movimiento.getDetalles().get(producTemp.getId());
        if (producto == null) {
            producTemp.setCantidadUnidad(producTemp.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA ? producTemp.getCantidadUnidad() : 1);
        }

        // ✅ SOLUCIÓN: Copia manual en lugar de clone() para evitar pérdida de bodega_id
        MovimientosDetallesBean productoCopia = new MovimientosDetallesBean();

        // Copiar TODOS los campos críticos
        productoCopia.setId(producTemp.getId());
        productoCopia.setProductoId(producTemp.getProductoId());
        productoCopia.setBodegasId(producTemp.getBodegasId());  // ⚠️ CRÍTICO
        productoCopia.setPlu(producTemp.getPlu());
        productoCopia.setDescripcion(producTemp.getDescripcion());
        productoCopia.setPrecio(producTemp.getPrecio());
        productoCopia.setTipo(producTemp.getTipo());
        productoCopia.setCategoriaId(producTemp.getCategoriaId());
        productoCopia.setCategoriaDesc(producTemp.getCategoriaDesc());
        productoCopia.setUnidades_medida_id(producTemp.getUnidades_medida_id());
        productoCopia.setUnidades_medida(producTemp.getUnidades_medida());
        productoCopia.setSaldo(producTemp.getSaldo());
        productoCopia.setCantidadIngredientes(producTemp.getCantidadIngredientes());
        productoCopia.setCantidadImpuestos(producTemp.getCantidadImpuestos());
        productoCopia.setEstado(producTemp.getEstado());
        productoCopia.setCosto(producTemp.getCosto());
        productoCopia.setCodigoBarra(producTemp.getCodigoBarra());
        productoCopia.setProducto_compuesto_costo(producTemp.getProducto_compuesto_costo());
        productoCopia.setCompuesto(producTemp.isCompuesto());
        productoCopia.setCantidadUnidad(producTemp.getCantidadUnidad());
        productoCopia.setSubtotal(producTemp.getSubtotal());
        productoCopia.setImpuestos(producTemp.getImpuestos());
        productoCopia.setIngredientes(producTemp.getIngredientes());

        // Log de debug para verificar
        NovusUtils.printLn("[DEBUG] cargaProducto - PLU: " + productoCopia.getPlu() +
                          ", BodegaId: " + productoCopia.getBodegasId() +
                          ", ProductoId: " + productoCopia.getProductoId());

        movimiento.getDetalles().put(productoCopia.getId(), productoCopia);
    }

    JsonObject solicitarConsecutivos() {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync("CONSEGUIR CONSECUTIVOS", NovusConstante.SECURE_CENTRAL_POINT_CONSECUTIVOS_FACTURAS, NovusConstante.GET, null, true, false, header);
        try {
            response = client.esperaRespuesta();
        } catch (Exception e) {
            Logger.getLogger(KCOViewController.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return response;
    }

    boolean hayConsecutivosCombustible() {
        boolean hayConsecutivosCombustible = false;
        JsonObject response = solicitarConsecutivos();
        if (response != null) {
            System.out.println(response);
            JsonArray data = response.get("data") != null && !response.get("data").isJsonNull() ? response.get("data").getAsJsonArray() : new JsonArray();
            hayConsecutivosCombustible = data.size() > 0;
        }
        return hayConsecutivosCombustible;
    }

    void updateConsecutivoXS() {
        try {
            newConsecutivo = mdao.getConsecutivoMarket(NovusConstante.IS_DEFAULT_FE);
            boolean isCDL = Main.TIPO_NEGOCIO
                    .equals(NovusConstante.PARAMETER_CDL);
            String resolucion = !isCDL ? "KSC" : "CDL";
            if (!manual) {
                if (newConsecutivo != null) {
                    jrecibo.setText(Utils.str_pad(newConsecutivo.getConsecutivo_actual() + "", 6, "0", "STR_PAD_LEFT"));
                } else {
                    ConsecutivosDialogView cDialog = new ConsecutivosDialogView(this, true);
                    cDialog.setVisible(true);
                    this.dispose();
                }
            } else {
                newConsecutivo = mdao.getPrefijo(18, resolucion);
                NovusUtils.printLn("Factura :" + factura);
                jrecibo.setText(Utils.str_pad(newConsecutivo.getConsecutivo_actual_fe() + "", 6, "0", "STR_PAD_LEFT"));
            }
        } catch (DAOException a) {
            Logger.getLogger(KCOViewController.class
                    .getName()).log(Level.SEVERE, null, a);
        }
    }

    public PersonaBean getPersona() {
        return persona;
    }

    public void setPersona(PersonaBean persona) {
        this.persona = persona;
    }

    private boolean validaExistenciaIngredientes(float cant, ProductoBean producto) {
        boolean existencia = true;
        if (!producto.getIngredientes().isEmpty()) {
            for (ProductoBean ingrediente : producto.getIngredientes()) {
                if (ingrediente.getProducto_compuesto_cantidad() < 0) {
                    existencia = false;
                    break;
                }
                float cantidades = ingrediente.getCantidadUnidad();
                float solicitud = cant * ingrediente.getProducto_compuesto_cantidad();
                if (cantidades < solicitud) {
                    existencia = false;
                    break;
                }
            }
        }
        return existencia;
    }

    private void mostrarPaneMenu() {
        pn_categorias.remove(jfondoMensajes);
        pn_teclado.remove(jfondoMensajes);
        pn_menu.add(jfondoMensajes);
        
        // Resetear paginación al mostrar el menú
        currentPage = 0;
        
        ejecutarSwingWorker(() -> {
            try {
                getProductsK();
            } catch (Exception e) {
                Logger.getLogger(KCOViewController.class.getName()).log(Level.SEVERE, "Error al obtener productos", e);
                showMensajes("Error al cargar productos", "notaBorrar.png");
            }
        });
    }

    private boolean productoEstaEnCanasta(long id) {
        LinkedHashMap<Long, MovimientosDetallesBean> productosCanasta = this.movimiento.getDetalles();
        return productosCanasta.containsKey(id);
    }

    private void mostrarPaneCategorias() {
        jScrollPane3.setVisible(false);
        jScrollPane2.setVisible(true);
        CategoriasDao cdao = new CategoriasDao();

        try {
            listaCategorias = cdao.findAllCategoriasKIOSCO();
        } catch (DAOException ex) {
            Logger.getLogger(KCOViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        jScrollPane2.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMinimum());

        listarCategorias();
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "pn_categorias");
        this.card = "pn_categorias";
        pn_menu.remove(jfondoMensajes);
        pn_teclado.remove(jfondoMensajes);
        pn_categorias.add(jfondoMensajes);
    }

    void listarCategorias() {
        int j = 0, k = 0, i = 0;
        if (listaCategorias == null) {
            return;
        }
        for (CategoriaBean categoria : listaCategorias) {
            JLabel buttonCategoria = new JLabel();
            JLabel labelCategoria = new JLabel();
            buttonCategoria.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/opMenu.png")));
            labelCategoria.setText(categoria.getGrupo().toUpperCase());
            labelCategoria.setForeground(new Color(255, 255, 255));
            labelCategoria.setFont(new java.awt.Font("Terpel Sans ExtraBold", Font.BOLD, 20));
            labelCategoria.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            if ((i + 1) % 2 != 0) {
                buttonCategoria.setBounds(30, (j * 50) + 30, 212, 50);
                labelCategoria.setBounds(30, (j * 50) + 30, 212, 50);
                j++;
            } else {
                buttonCategoria.setBounds(30 + 212 + 30, (k * 50) + 30, 212, 50);
                labelCategoria.setBounds(30 + 212 + 30, (k * 50) + 30, 212, 50);
                k++;
            }
            buttonCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    mostrarPanelTecladoBusqueda(false);
                    mostrarCategoria(categoria);
                }
            });
            jPanel5.add(labelCategoria);
            jPanel5.add(buttonCategoria);
            i++;
        }
    }

    void mostrarCategoria(CategoriaBean categoria) {
        jScrollPane3.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMinimum());
        jScrollPane3.setVisible(true);
        jScrollPane2.setVisible(false);
        MovimientosDao dao = new MovimientosDao();
        if (!productosCategoria.containsKey(categoria.getId())) {
            try {
                productosCategoria.put(categoria.getId(), dao.findByCategoriaKIOSCO(categoria));
            } catch (DAOException ex) {
                Logger.getLogger(KCOViewController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        lista = productosCategoria.get(categoria.getId());
        listarProductos(NovusConstante.PRODUCTOS_TIPOS_NORMAL, jPanel6);
    }

    public void cerrarTeclado() {
        showMensajes(null, null);
        opcionesPanelProductos(true);
        isIngresandoCantidad(false);
        if (card.equals("pn_menu")) {
            mostrarPaneMenu();
        } else if (card.equals("pn_categorias")) {
            mostrarPaneCategorias();
        }
        jentry_txt.setText("");
        seleccion = null;
    }

    public void changeEntry(String label, String value) {
        jentry_txt.setText(value);
    }

    public void showMensajes(String texto, String rutaImagenMensaje) {

        jMensajes.setVisible(true);
        jfondoMensajes.setVisible(true);

        NovusUtils.printLn(texto + "     " + rutaImagenMensaje);
        if (texto == null) {
            jfondoMensajes.setVisible(false);
            jMensajes.setText("");
            jfondoMensajes.setIcon(null);
        } else {
            jMensajes.setText(texto);
            jfondoMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/" + rutaImagenMensaje)));
            setTimeout(10, () -> {
                jMensajes.setVisible(false);
                jfondoMensajes.setVisible(false);
                jMensajes.setText("");
            });
        }
    }

    public void isIngresandoCantidad(boolean ingresando) {
        ingresandoCantidad = ingresando;
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

    public void mostrarPaneTeclado(boolean productoGranel) {
        isIngresandoCantidad(true);
        deshabilitarTeclas(productoGranel);
        pn_menu.remove(jfondoMensajes);
        pn_categorias.remove(jfondoMensajes);
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "pn_teclado");
        pn_teclado.remove(jLabel2);
        pn_teclado.add(jfondoMensajes);
        pn_teclado.add(jLabel2);
        jentry_txt.requestFocus();
    }

    public void deshabilitarTeclas(boolean activar) {
        TecladoNumericoGrayMedium tec = (TecladoNumericoGrayMedium) jPanel1;
        tec.habilitarPunto(activar);
    }

    public void visiblePaneTecladoBusqueda() {
        pnlTecladoBusqueda.setVisible(!pnlTecladoBusqueda.isVisible());
        pnlBusquedaProductos.setVisible(!pnlBusquedaProductos.isVisible());
        jentry_txt.requestFocus();
    }

    private void salir(boolean canastaVacia) {
        PedidoDialogCancelarView cancelar = new PedidoDialogCancelarView(this, true);
        cancelar.setVisible(canastaVacia);
        boolean resp = cancelar.respuesta;
        if (resp) {
            new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
            if (parent != null) {
                if (manual) {
                    parent.mostrarSubPanel(new VentaManualMenuPanel(parent, ventanaVentaPrincipal, true));
                } else {
                    parent.showPanel("home");
                }
            } else {
                dialog.dispose();
            }
        }
    }

    public void iconosIngresarCantidad() {
        jSearchBar.setIcon(numberBar);
        placeholder.setText("");
        placeholder = new TextPrompt("Ingrese Cantidad", jentry_txt);
        placeholder.getText();
    }

    public void iconosIngresarBusqueda() {
        jSearchBar.setIcon(searchBar);
        placeholder.setText("");
        placeholder = new TextPrompt("Ingrese el código PLU o el nombre del producto...", jentry_txt);
        placeholder.getText();
    }

    public void mostrarPanelTecladoBusqueda(boolean visible) {
        pnlTecladoBusqueda.setVisible(visible);
        pnlBusquedaProductos.setVisible(visible);
        jentry_txt.requestFocus();
    }

    public void quitarTodos() {
        NovusUtils.printLn("borrar productos");
        jPanel4.setPreferredSize(new java.awt.Dimension(460, 0));
        movimiento.getDetalles().clear();
        repaintPanel(true, movimiento);
        seleccion = null;
        showMensajes(null, null);
    }

    public void cambioBotonQuitarTodos() {
        if (!movimiento.getDetalles().isEmpty()) {
            pnlButtonQuitarTodos.setBackground(colorPanelSeleccionado);
            jQuitarTodos.setForeground(colorTextoSeleccionado);
        } else {
            pnlButtonQuitarTodos.setBackground(colorPanelInactivo);
            jQuitarTodos.setForeground(colorTextoSelccionadoBold);
        }
    }

    public void opcionesPanelProductos(boolean visible) {
        lbl.setVisible(visible);
        pnlButtonProductos.setVisible(visible);
        pnlButtonCategorias.setVisible(visible);
    }

    public void cambioBotonesProductoCategorias() {
        cambioBotonCategorias();
        cambioBotonProducto();
    }

    public void cambioBotonProducto() {
        if (buttonProductosSeleccionado) {
            pnlButtonProductos.setBackground(colorPanelSeleccionado);
            jProductos.setForeground(colorTextoSeleccionado);
        } else {
            pnlButtonProductos.setBackground(colorPanelInactivo);
            jProductos.setForeground(colorTextoInactivo);
        }
    }

    public void cambioBotonCategorias() {
        if (buttonCategoriasSeleccionado) {
            pnlButtonCategorias.setBackground(colorPanelSeleccionado);
            jCategoria.setForeground(colorTextoSeleccionado);
        } else {
            pnlButtonCategorias.setBackground(colorPanelInactivo);
            jCategoria.setForeground(colorTextoInactivo);
        }
    }

    public JPanel getjPanel3() {
        return jPanel3;
    }

    public void ejecutarSwingWorker(BackgroundTask task) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressButton.setIcon(new ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro_small.gif")));
                task.run();
                return null;
            }

            @Override
            protected void done() {
                showPnMenu();
                progressButton.setIcon(new ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btReintentarsmall.png")));
                actualizarEtiquetaPaginacion(totalRecords, pageSize, currentPage, totalRecordsLabel);
            }
        };
        worker.execute();
    }

    private void showPnMenu() {
        if (lista != null && !lista.isEmpty()) {
            if (StoreViewController.isCombustible) {
                listarProductos(NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE, jPanel7);
            } else {
                listarProductos(NovusConstante.PRODUCTOS_TIPOS_NORMAL, jPanel7);
            }
            CardLayout crr = (CardLayout) (jPanel3.getLayout());
            crr.show(jPanel3, "pn_menu");
            this.card = "pn_menu";
            
            // Actualizar estado de los botones de navegación
            prevButton.setEnabled(currentPage > 0);
            nextButton.setEnabled((long) (currentPage + 1) * pageSize < totalRecords);
            
            // Actualizar etiqueta de paginación
            actualizarEtiquetaPaginacion(totalRecords, pageSize, currentPage, totalRecordsLabel);
        }
    }

    private void getProductsK() {
        try {
            FindAllProductoTipoKioskoUseCase useCase = new FindAllProductoTipoKioskoUseCase(currentPage, pageSize);
            ResultadoProductosKiosko result = useCase.executePaginated();
            lista = new ArrayList<>(result.getProductos());
            totalRecords = result.getTotalRegistros();
        } catch (RuntimeException ex) {
            Logger.getLogger(KCOViewController.class.getName()).log(Level.SEVERE, null, ex);
            showMensajes("Error al cargar productos", "notaBorrar.png");
        }
    }

    public void actualizarEtiquetaPaginacion(long totalRecords, int pageSize, int currentPage, JLabel totalRecordsLabel) {
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        int currentPageDisplay = currentPage + 1;
        String mensaje = "Listando " + currentPageDisplay + " de " + totalPages;
        totalRecordsLabel.setText(mensaje);
    }
}

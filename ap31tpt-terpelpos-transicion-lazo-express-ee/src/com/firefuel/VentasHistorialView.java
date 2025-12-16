package com.firefuel;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.VentasAppterpelBotonesValidador;
import com.WT2.appTerpel.domain.valueObject.TransaccionMessageView;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.ventas.ExisteFidelizacionUseCase;
import com.application.useCases.ventas.IsPendienteTransaccionUseCase;
import com.application.useCases.ventas.ValidarTurnoMedioPagoUseCase;
import com.application.useCases.ventas.ValidarVentaPendienteDatafonoUseCase;
import com.bean.AsignacionClienteBean;
import com.bean.MovimientosBean;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.bean.ReciboExtended;
import com.bean.ProductoBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DatafonosDao;
import com.dao.MovimientosDao;
import com.dao.DAOException;
import com.dao.PersonasDao;
import com.dao.SurtidorDao;
import com.facade.RumboFacade;
import com.facade.VentasCombustibleFacade;
import com.firefuel.components.RenderTablaColor;
import com.firefuel.components.SelectRenderRow;
import com.firefuel.datafonos.EnviarPagosDatafonos;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.facturacion.electronica.NotasCredito;
import com.firefuel.facturacion.electronica.VentasSinResolverFE;
import com.firefuel.utils.ShowMessageSingleton;
import com.firefuel.ventas.Service.VentasService;
import com.firefuel.ventas.dto.VentaDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neo.app.bean.MediosPagosBean;
import com.neo.app.bean.Recibo;
import com.neo.print.services.ImpresionVenta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.server.api.AsyncExecutor;
import teclado.view.common.TecladoNumericoGrayDot;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.useCases.movimientos.ValidatePaymentMethodShiftUseCase;
import com.application.useCases.wacherparametros.FindParameterValueUseCase;
import com.application.commons.CtWacherParametrosEnum;
import com.application.useCases.movimientos.FinByTipoMovimientoUseCase;
import com.application.useCases.tbltransaccionproceso.FinByTransaccionProcesoUseCase;
import com.application.useCases.gopass.GetIsGoPassMovimientoUseCase;
import com.application.useCases.jornada.GetJornadasByGroupUseCase;
import com.application.useCases.movimeintomediodepago.FinByMedioDePagoMovimientoUseCase;
import com.application.useCases.movimientos.FinByMedioPagoForJoradaUseCase;
import com.application.useCases.movimientosdetalles.GetByProductoMovimientoDetalleUseCase;
import com.application.useCases.movimientocliente.ObtenerIdTransmisionDesdeMovimientoUseCase;
import com.application.useCases.datafonos.HayAnulacionesPendientesUseCase;
import com.application.useCases.productos.ObtenerPrecioUreaUseCase;
import com.application.useCases.productos.ObtenerCodigoExternoUreaUseCase;
import com.application.useCases.jornadas.ObtenerJornadaIdUseCase;
import com.application.useCases.movimientos.ActualizarCtMovimientosUseCase;
import com.application.useCases.personas.ObtenerNombrePromotorUseCase;
import com.application.useCases.movimientos.ActualizarEstadoMovimientoUseCase;
import com.application.useCases.wacherparametros.ObtenerCodigoEstacionUseCase;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;

public class VentasHistorialView extends JDialog {

    ObtenerPrecioUreaUseCase obtenerPrecioUreaUseCase;
    ObtenerCodigoExternoUreaUseCase obtenerCodigoExternoUreaUseCase;
    ObtenerJornadaIdUseCase obtenerJornadaIdUseCase;
    GetJornadasByGroupUseCase getJornadasByGroupUseCase;
    ObtenerIdTransmisionDesdeMovimientoUseCase obtenerIdTransmisionDesdeMovimientoUseCase;
    ObtenerCodigoEstacionUseCase obtenerCodigoEstacionUseCase;
    private long utlimaConsultaVentas = 0;
    private long getUtlimaConsultaVentasPendientes = 0;
    private static final long INTERVALO_MINIMO_MS = 1000;
    private boolean enPorcesoDeRefresco;
    private Timer debounceTimer;
    MovimientosDao mdao = new MovimientosDao();
    SurtidorDao dao = new SurtidorDao();
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    ImageIcon btnRight = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_right1.png"));
    ImageIcon btnLeft = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_izquierda.png"));
    ImageIcon btnRightPressed = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_right2.png"));
    ImageIcon btnlefttPressed = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_izquierda-press.png"));
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    boolean activarAcumular;
    final int CONSULTAR_VENTAS_FECHAS = 1;
    String tipo = "";
    String cedula = "";
    public JsonObject notaP;
    ArrayList<ReciboExtended> lista;
    ArrayList<ReciboExtended> listaVentasSinResolver = new ArrayList<>();
    ReciboExtended reciboFidelizar = null;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    boolean resetear = false;
    boolean aplicaFE = false;
    InfoViewController parent;
    public int devolucion = 0;
    private final Icon botonActivoIcono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-rojo-1.png"));
    private final Icon botonActivoIconoLarge = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-rojo-large.png"));
    private final Icon botonBloqueadoIcono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"));
    private final Icon botonBloqueadoIconoLarge = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-large.png"));
    private final Icon botonTabsSeleccionado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-tabs-2.png"));
    private final Icon botonTabsNoSeleccionado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-tabs-1.png"));
    private final Icon botonSeleccionado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-seleccionar-small.png"));
    private final Icon botonActivoIconoIconoImpresora = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/impresora-boton.png"));
    private final Icon botonBloqueadoIconoImpresora = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/impresora-boton-gris.png"));
    private Runnable runnable;
    private Runnable handler;
    private Runnable anulacion;
    private Runnable noAceptar;
    static private Runnable refrescar;
    VentasHistorialPlacaViewController ventaPlaca;
    PersonaBean persona = Main.persona;
    PersonasDao pdao = new PersonasDao();
    public static boolean estadoActulizar = false;
    public static boolean estadoActulizarDatafono;
    long idTransacciondatafono;
    long id = 0L;
    boolean fidelizada;
    int validarTipoVenta = 0;
    long tiempoMaximoCliente = 5;
    Runnable irAventas;

    Runnable runnableDatafonos = null;
    Timer timer;
    boolean ejecutarAccionDeCerrado = true;

    private final Icon loaderDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"));
    private final Icon errorDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/venta error.gif"));
    private final Icon okDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/venta check.gif"));
    // ESTOS SON LOS ICONOS DE RECHAZO Y ACEPTACION PARA GOPASS Y APPTERPEL
    private final Icon iconoOk = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/iconoCheck.png"));
    private final Icon iconoError = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/iconoError.png"));

    private final Icon iconoAppTerpelOk = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/AppterlApproved.png"));
    private final Icon iconoAppTerpeError = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/AppterpelRejected.png"));
    private final String PNL_HISTORIAL_VENTAS = "pnlHistorialVentas";
    private final String PNL_GESTIONAR_VENTAS_ADBLUE = "pnlGestionarAdBlue";
    private float precioAdBlue = 0;
    
    // Cola de impresión para archivo TXT
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public VentasHistorialView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        initComponents();
        init();
    }

    public VentasHistorialView(InfoViewController parent, boolean modal, Runnable runnable) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        this.runnable = runnable;
        initComponents();
        init();
    }


    
    private void habilitarFE() {
        FacturacionElectronica fac = new FacturacionElectronica();
        aplicaFE = fac.aplicaFE();
        jFactura.setVisible(aplicaFE);
        jAnular.setVisible(aplicaFE);
        jAsignarDatosSinResolver.setVisible(aplicaFE || fac.remisionActiva());
        jLabel4.setVisible(fac.aplicaFE());
    }

   private void habilitarRemision() {
       FacturacionElectronica fac = new FacturacionElectronica();
       aplicaFE = fac.aplicaFE();
       jFactura.setVisible(findByParameterUseCase.execute() || aplicaFE);
   }

   

    private void botonesBloqueados() {
        botonInactivo(jMedio);
        botonInactivo(jFideliza);
        impresoraInactivo(jImprimir);
        impresoraInactivo(jImprimirSinResolver);
        botonInactivo(jFactura);
        botonInactivo(jAnular);
        botonInactivo(jMedioSinResolver);
        botonInactivo(jAsignarDatosSinResolver);
        botonInactivo(jRecuperacion);
        botonInactivo(btEnviarPagoPendiente);
       ; botonInactivoFinalizar(btnFinalizar);
    }

    public static void setEstadoActulizarDatafono(boolean estado) {
        VentasHistorialView.estadoActulizarDatafono = estado;
    }
        private void init() {
        this.obtenerPrecioUreaUseCase = new ObtenerPrecioUreaUseCase();
        this.obtenerCodigoExternoUreaUseCase = new ObtenerCodigoExternoUreaUseCase();
        this.obtenerJornadaIdUseCase = new ObtenerJornadaIdUseCase();
        this.obtenerIdTransmisionDesdeMovimientoUseCase = new ObtenerIdTransmisionDesdeMovimientoUseCase(0L);
        this.getJornadasByGroupUseCase = new GetJornadasByGroupUseCase();
        this.obtenerCodigoEstacionUseCase = new ObtenerCodigoEstacionUseCase();
        btnHIstorialVentas.setOpaque(false);
        btnHIstorialVentas.setContentAreaFilled(false);
        ventasSinResolver.setOpaque(false);
        ventasSinResolver.setContentAreaFilled(false);
        habilitarFE();
        habilitarRemision();
        botonesBloqueados();
        InfoViewController.NotificadorInfoView = null;
        InfoViewController.NotificadorVentasHistorial = new Notificador() {
            @Override
            public void send(JsonObject data) {
                mostrarPanelMensaje(data.get("mensaje").getAsString(), data, true);
            }
        };

        btnHIstorialVentas.setForeground(new Color(223, 29, 20));
        btnHIstorialVentas.setIcon(botonTabsSeleccionado);
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        cargarTablaHIstorialVentas(cardVentas, "ventasHistorial");
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorial");
        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
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
        renderizarTablaVentasSinResolver(jTable3, jScrollPane4);

        NovusUtils.setUnsortableTable(this.jTable1);
        cargarPromotores();
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        jComboPromotor.addActionListener((java.awt.event.ActionEvent evt) -> {
            Async(() -> {
                refrescarDatos("JCOMBOX " + jComboPromotor.getSelectedIndex());
            });
        });
       this.tiempoMaximoCliente = FindParameterValueUseCase.consultarTiempoMaximoDatosCliente();
        if (Main.persona != null) {
            solicitarVentas(Main.persona, "init", 20); // o el número que uses como límite
        }

        jAsignarDatosSinResolver.setFont(new java.awt.Font("Terpel Sans", 1, 18));
        this.renderizarUsuarios(); //Se llamaba dos veces
        txtCantidadSuministrada.requestFocus();
        precioAdBlue = obtenerPrecioUreaUseCase.execute();
        jAnular.setVisible(false);
        jLabel4.setVisible(false);
        jImprimirSinResolver.setVisible(false);

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });
    }


    // private void init() {
    //     btnHIstorialVentas.setOpaque(false);
    //     btnHIstorialVentas.setContentAreaFilled(false);
    //     ventasSinResolver.setOpaque(false);
    //     ventasSinResolver.setContentAreaFilled(false);
    //     habilitarFE();
    //     habilitarRemision();
    //     botonesBloqueados();
    //     InfoViewController.NotificadorInfoView = null;
    //     InfoViewController.NotificadorVentasHistorial = new Notificador() {
    //         @Override
    //         public void send(JsonObject data) {
    //             mostrarPanelMensaje(data.get("mensaje").getAsString(), data, true);
    //         }
    //     };

    //     btnHIstorialVentas.setForeground(new Color(223, 29, 20));
    //     btnHIstorialVentas.setIcon(botonTabsSeleccionado);
    //     NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
    //     cargarTablaHIstorialVentas(cardVentas, "ventasHistorial");
    //     cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorial");
    //     if (Main.persona != null) {
    //         jpromotor.setText(Main.persona.getNombre());
    //     } else {
    //         jpromotor.setText("NO HAY TURNO ABIERTO");
    //     }

    //     DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
    //     headerRenderer.setBackground(new Color(186, 12, 47));
    //     headerRenderer.setForeground(new Color(255, 255, 255));
    //     jTable1.setFillsViewportHeight(true);
    //     headerRenderer.setHorizontalAlignment(JLabel.CENTER);
    //     for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
    //         jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
    //     }
    //     TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jTable1.getModel()) {
    //         @Override
    //         public boolean isSortable(int column) {
    //             super.isSortable(column);
    //             return false;
    //         }
    //     };
    //     jTable1.setRowSorter(rowSorter);

    //     jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
    //         @Override
    //         protected void configureScrollBarColors() {
    //             this.thumbColor = new Color(186, 12, 47);
    //         }
    //     });
    //     jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
    //     jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    //     renderizarTablaVentasSinResolver(jTable3, jScrollPane4);

    //     NovusUtils.setUnsortableTable(this.jTable1);
    //     cargarPromotores();
    //     jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
    //         @Override
    //         protected void configureScrollBarColors() {
    //             this.thumbColor = new Color(186, 12, 47);
    //         }
    //     });
    //     jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
    //     jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    //     jComboPromotor.addActionListener((java.awt.event.ActionEvent evt) -> {
    //         Async(() -> {
    //             refrescarDatos("JCOMBOX " + jComboPromotor.getSelectedIndex());
    //         });
    //     });


    //     this.tiempoMaximoCliente = mdao.buscarTiempoMaximoDatosCliente();
    //     if (Main.persona != null) {
    //         solicitarVentas(Main.persona, "init", 20); // o el número que uses como límite
    //     }

    //     jAsignarDatosSinResolver.setFont(new java.awt.Font("Terpel Sans", 1, 18));
    //     this.renderizarUsuarios(); //Se llamaba dos veces
    //     txtCantidadSuministrada.requestFocus();
    //     precioAdBlue = Main.rumboDao.precioUREA();
    //     jAnular.setVisible(false);
    //     jLabel4.setVisible(false);
    //     jImprimirSinResolver.setVisible(false);
    // }

    public void mostrarPanelGestionarVentaAdBlue() {
        CardLayout panel = (CardLayout) pnlVentas.getLayout();
        panel.show(pnlVentas, PNL_GESTIONAR_VENTAS_ADBLUE);
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesUreaSinResolver");
        VentasHistorialView.txtCantidadSuministrada.requestFocus();
    }

    public void mostrarPanelHistorialVentas() {
        txtCantidadSuministrada.setText("");
        CardLayout panel = (CardLayout) pnlVentas.getLayout();
        panel.show(pnlVentas, PNL_HISTORIAL_VENTAS);
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasSinResolver");
        VentasHistorialView.txtCantidadSuministrada.requestFocus();
    }

    public void mostartPanelMensajeAppterpel(JsonObject data, boolean autecerrar) {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnlMensajeGopass");
        System.out.println(":::::::::::::::::::: :)" + data);
        lblTitulo.setText(data.get("titulo").getAsString());
        lblMsg.setBounds(530, 360, 430, 70);
        if (data.get("mensaje").getAsString().length() > 20) {
            lblMsg.setBounds(520, 330, 450, 150);
            lblMsg.setFont(new java.awt.Font("Terpel Sans", 1, 18));
        }
        if (data.get("estado").getAsBoolean()) {
            fndIcono.setIcon(iconoAppTerpelOk);
            lblFooter.setText("<html><center>" + TransaccionMessageView.TRANSACCION_APROBADA + "</center></html>");
        } else {
            fndIcono.setIcon(iconoAppTerpeError);
            lblFooter.setText("<html><center>" + TransaccionMessageView.TRANSACCION_RECHAZADA + "</center></html>");
        }
        if (data.get("codigoAutorizacion").getAsString().equals("1815054")) {
            lblMsg.setBounds(530, 290, 430, 200);
            if (data.get("estado").getAsBoolean()) {
                fndIcono.setIcon(iconoOk);
            } else {
                fndIcono.setIcon(iconoError);
            }
            lblFooter.setText("");
        }
        String mensaje = NovusUtils.convertMessage(LetterCase.FIRST_UPPER_CASE,
                data.get("mensaje").getAsString());

        String mensajeFormateado = mensaje;
        // se debe borrar la validación ya que el backend debe entregar el emnsaje correctamente.
        if (mensajeFormateado != null && !mensajeFormateado.isEmpty() && !mensajeFormateado.isBlank() && mensajeFormateado.toLowerCase().startsWith("rechazado")) {
            mensajeFormateado = mensaje.length() > 10 ? mensaje.substring(10) : mensaje ;
        }

        lblMsg.setText("<html><center>" + mensajeFormateado + "</center></html>");

    }

    public void mostrarPanelMensaje(String mensaje, JsonObject data, boolean autecerrar) {

        if (data.get("tipo").getAsInt() == 4) {
            //MENSAJE DE RESULTADO DE APPTERPEL
            mostartPanelMensajeAppterpel(data, autecerrar);
        } else {
            CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
            panel.show(pnlPrincipal, "mensajesDatafono");
            if (data.get("estado").getAsBoolean()) {
                jTitulo.setText("<html><center><b>" + data.get("mensaje").getAsString() + "</b></center></html>");
                jIcono.setIcon(okDatafono);
            } else {
                jTitulo.setText("<html><center><b>" + data.get("mensaje").getAsString() + "</b></center></html>");
                jIcono.setIcon(errorDatafono);
            }
            if (!data.get("codigoAutorizacion").getAsString().isEmpty()) {
                jTransacciones.setText("ID TRANSACCIÓN " + data.get("codigoAutorizacion").getAsString());
            } else {
                jTransacciones.setText("");
            }
        }
        setTimeoutMensajeDatafono(5, () -> mostrarMenuPrincipal(autecerrar));
    }

    private void showPanel(String panel) {
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlPrincipal = new javax.swing.JPanel();
        pnlReporteVentas = new javax.swing.JPanel();
        jclock = ClockViewController.getInstance();
        jpromotor3 = new javax.swing.JLabel();
        jComboMostrar = new javax.swing.JComboBox<>();
        jpromotor4 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        pnlVentas = new javax.swing.JPanel();
        pnlHistorialVentas = new javax.swing.JPanel();
        cardVentas = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnHIstorialVentas = new javax.swing.JToggleButton();
        ventasSinResolver = new javax.swing.JToggleButton();
        pnlGestionarAdBlue = new javax.swing.JPanel();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblMensajePlaca = new javax.swing.JLabel();
        lblPlacaVentaAdBlue = new javax.swing.JLabel();
        lblMensajePrecio = new javax.swing.JLabel();
        lblPrecioUrea = new javax.swing.JLabel();
        lblMensajeLitrosAutorizados = new javax.swing.JLabel();
        lblMensajeTotalVenta = new javax.swing.JLabel();
        lblTotalVenta = new javax.swing.JLabel();
        lblCantidadSuministrada = new javax.swing.JLabel();
        txtCantidadSuministrada = new javax.swing.JTextField();
        lblContenedor = new javax.swing.JLabel();
        jMensajeNotificacion = new javax.swing.JLabel();
        lblLitrosAutorizados = new javax.swing.JLabel();
        pnlTecladoNumerico = new TecladoNumericoGrayDot();
        jPanel1 = new javax.swing.JPanel();
        jComboPromotor = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        accionesDeVentas = new javax.swing.JPanel();
        accionesVentasSinResolver = new javax.swing.JPanel();
        jMedioSinResolver = new javax.swing.JLabel();
        jAsignarDatosSinResolver = new javax.swing.JLabel();
        jImprimirSinResolver = new javax.swing.JLabel();
        btEnviarPagoPendiente = new javax.swing.JLabel();
        btnFinalizar = new javax.swing.JLabel();
        accionesHistorialVentas = new javax.swing.JPanel();
        jMedio = new javax.swing.JLabel();
        jFideliza = new javax.swing.JLabel();
        jImprimir = new javax.swing.JLabel();
        jFactura = new javax.swing.JLabel();
        jRecuperacion = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        accionesHistorialVentasMod2 = new javax.swing.JPanel();
        jAnular = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        accionesUreaSinResolver = new javax.swing.JPanel();
        btnCancelar = new javax.swing.JLabel();
        btnAnularAdBlue = new javax.swing.JLabel();
        btnFinalizarVenta = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jpromotor2 = new javax.swing.JLabel();
        pnlPromotor = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        pnl_confirmacion = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        NO = new javax.swing.JLabel();
        SI1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();
        card_mensaje = new PanelNotificacion();
        pnlMsjDatafonos = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        jTitulo = new javax.swing.JLabel();
        jInfoDatafono = new javax.swing.JLabel();
        jTransacciones = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        fndFondo = new javax.swing.JLabel();
        pnlMensajeGopass = new javax.swing.JPanel();
        jCerrar1 = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblFooter = new javax.swing.JLabel();
        lblMsg = new javax.swing.JLabel();
        fndIcono = new javax.swing.JLabel();
        fndMensaje = new javax.swing.JLabel();
        fndFondo1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        getContentPane().setLayout(null);

        pnlPrincipal.setMaximumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setName(""); // NOI18N
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        pnlReporteVentas.setLayout(null);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setOpaque(false);
        jclock.setLayout(null);
        pnlReporteVentas.add(jclock);
        jclock.setBounds(1150, 720, 110, 60);

        jpromotor3.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor3.setForeground(new java.awt.Color(153, 0, 0));
        jpromotor3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor3.setText("MOSTRAR");
        pnlReporteVentas.add(jpromotor3);
        jpromotor3.setBounds(990, 100, 130, 40);

        jComboMostrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboMostrar.setForeground(new java.awt.Color(51, 51, 51));
        jComboMostrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20", "60", "120", "200", "Todos" }));
        jComboMostrar.setBorder(null);
        jComboMostrar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboMostrarItemStateChanged(evt);
            }
        });
        jComboMostrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jComboMostrarMouseReleased(evt);
            }
        });
        jComboMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboMostrarActionPerformed(evt);
            }
        });
        pnlReporteVentas.add(jComboMostrar);
        jComboMostrar.setBounds(1120, 100, 110, 40);

        jpromotor4.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor4.setForeground(new java.awt.Color(153, 0, 0));
        jpromotor4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor4.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor4);
        jpromotor4.setBounds(40, 110, 150, 40);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlReporteVentas.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlReporteVentas.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor);
        jpromotor.setBounds(500, 30, 760, 50);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CONSULTA VENTAS");
        pnlReporteVentas.add(jLabel3);
        jLabel3.setBounds(100, 0, 720, 90);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        pnlReporteVentas.add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor1.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor1);
        jpromotor1.setBounds(800, 10, 460, 20);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlReporteVentas.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlReporteVentas.add(jLabel29);
        jLabel29.setBounds(1140, 710, 10, 80);

        pnlVentas.setOpaque(false);
        pnlVentas.setLayout(new java.awt.CardLayout());

        pnlHistorialVentas.setOpaque(false);
        pnlHistorialVentas.setLayout(null);

        cardVentas.setBackground(new java.awt.Color(255, 255, 255));
        cardVentas.setLayout(new java.awt.CardLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null}
                },
                new String [] {
                        "PROCESO", "NRO", "COD. AUT", "PREFIJO", "FECHA", "PRODUCTO", "ESTADO", "CARA", "CANT", "VALOR"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setRequestFocusEnabled(false);
        jTable3.setRowHeight(35);
        jTable3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable3.setShowGrid(true);
        jTable3.setShowHorizontalLines(false);
        jTable3.setShowVerticalLines(false);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(36);
            jTable3.getColumnModel().getColumn(1).setPreferredWidth(76);
            jTable3.getColumnModel().getColumn(7).setPreferredWidth(10);
        }

        jPanel7.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1220, -1));

        cardVentas.add(jPanel7, "ventasSinResolver");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
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
        jTable1.setRequestFocusEnabled(false);
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

        jPanel6.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1220, -1));

        cardVentas.add(jPanel6, "ventasHistorial");

        pnlHistorialVentas.add(cardVentas);
        cardVentas.setBounds(0, 70, 1220, 440);

        buttonGroup1.add(btnHIstorialVentas);
        btnHIstorialVentas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHIstorialVentas.setForeground(new java.awt.Color(153, 0, 0));
        btnHIstorialVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-tabs-1.png"))); // NOI18N
        btnHIstorialVentas.setText("HISTORIAL DE VENTAS");
        btnHIstorialVentas.setBorder(null);
        btnHIstorialVentas.setBorderPainted(false);
        btnHIstorialVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHIstorialVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //btnHIstorialVentasActionPerformed(evt);
                debounce(() -> btnHIstorialVentasActionPerformed(evt), 400);
            }
        });
        pnlHistorialVentas.add(btnHIstorialVentas);
        btnHIstorialVentas.setBounds(0, 0, 310, 60);

        buttonGroup1.add(ventasSinResolver);
        ventasSinResolver.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        ventasSinResolver.setForeground(new java.awt.Color(153, 0, 0));
        ventasSinResolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-tabs-1.png"))); // NOI18N
        ventasSinResolver.setText("VENTAS SIN RESOLVER");
        ventasSinResolver.setBorder(null);
        ventasSinResolver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ventasSinResolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //ventasSinResolverActionPerformed(evt);
                debounce(() -> ventasSinResolverActionPerformed(evt), 400);
            }
        });
        pnlHistorialVentas.add(ventasSinResolver);
        ventasSinResolver.setBounds(340, 0, 310, 60);

        pnlVentas.add(pnlHistorialVentas, "pnlHistorialVentas");

        pnlGestionarAdBlue.setOpaque(false);
        pnlGestionarAdBlue.setLayout(null);

        panelRedondo1.setBackground(new java.awt.Color(242, 241, 246));
        panelRedondo1.setRoundBottomLeft(24);
        panelRedondo1.setRoundBottomRight(24);
        panelRedondo1.setRoundTopLeft(24);
        panelRedondo1.setRoundTopRight(24);
        panelRedondo1.setLayout(null);

        lblMensajePlaca.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMensajePlaca.setForeground(new java.awt.Color(73, 73, 81));
        lblMensajePlaca.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMensajePlaca.setText("Placa");
        panelRedondo1.add(lblMensajePlaca);
        lblMensajePlaca.setBounds(30, 30, 80, 32);

        lblPlacaVentaAdBlue.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblPlacaVentaAdBlue.setForeground(new java.awt.Color(228, 22, 18));
        lblPlacaVentaAdBlue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPlacaVentaAdBlue.setText("ABC123");
        panelRedondo1.add(lblPlacaVentaAdBlue);
        lblPlacaVentaAdBlue.setBounds(380, 30, 180, 32);

        lblMensajePrecio.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMensajePrecio.setForeground(new java.awt.Color(73, 73, 81));
        lblMensajePrecio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMensajePrecio.setText("Precio");
        panelRedondo1.add(lblMensajePrecio);
        lblMensajePrecio.setBounds(30, 90, 100, 30);

        lblPrecioUrea.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblPrecioUrea.setForeground(new java.awt.Color(228, 22, 18));
        lblPrecioUrea.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPrecioUrea.setText("$ 9.999");
        panelRedondo1.add(lblPrecioUrea);
        lblPrecioUrea.setBounds(380, 90, 180, 30);

        lblMensajeLitrosAutorizados.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMensajeLitrosAutorizados.setForeground(new java.awt.Color(73, 73, 81));
        lblMensajeLitrosAutorizados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMensajeLitrosAutorizados.setText("Litros autorizados");
        panelRedondo1.add(lblMensajeLitrosAutorizados);
        lblMensajeLitrosAutorizados.setBounds(30, 145, 260, 32);

        lblMensajeTotalVenta.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMensajeTotalVenta.setForeground(new java.awt.Color(73, 73, 81));
        lblMensajeTotalVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMensajeTotalVenta.setText("Total venta");
        panelRedondo1.add(lblMensajeTotalVenta);
        lblMensajeTotalVenta.setBounds(30, 200, 150, 32);

        lblTotalVenta.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotalVenta.setForeground(new java.awt.Color(228, 22, 18));
        lblTotalVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalVenta.setText("$ 0");
        panelRedondo1.add(lblTotalVenta);
        lblTotalVenta.setBounds(380, 200, 300, 30);

        lblCantidadSuministrada.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblCantidadSuministrada.setForeground(new java.awt.Color(73, 73, 81));
        lblCantidadSuministrada.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCantidadSuministrada.setText("Cantidad suministrada urea");
        panelRedondo1.add(lblCantidadSuministrada);
        lblCantidadSuministrada.setBounds(30, 260, 340, 32);

        txtCantidadSuministrada.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        txtCantidadSuministrada.setForeground(new java.awt.Color(65, 65, 81));
        txtCantidadSuministrada.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCantidadSuministrada.setBorder(null);
        txtCantidadSuministrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadSuministradaActionPerformed(evt);
            }
        });
        txtCantidadSuministrada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadSuministradaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadSuministradaKeyTyped(evt);
            }
        });
        panelRedondo1.add(txtCantidadSuministrada);
        txtCantidadSuministrada.setBounds(390, 260, 280, 40);

        lblContenedor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblContenedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/inputBorderRedLarge.png"))); // NOI18N
        panelRedondo1.add(lblContenedor);
        lblContenedor.setBounds(380, 250, 300, 60);

        jMensajeNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jMensajeNotificacion.setForeground(new java.awt.Color(228, 22, 18));
        jMensajeNotificacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelRedondo1.add(jMensajeNotificacion);
        jMensajeNotificacion.setBounds(60, 350, 580, 60);

        lblLitrosAutorizados.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblLitrosAutorizados.setForeground(new java.awt.Color(228, 22, 18));
        lblLitrosAutorizados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        panelRedondo1.add(lblLitrosAutorizados);
        lblLitrosAutorizados.setBounds(380, 145, 180, 30);

        pnlGestionarAdBlue.add(panelRedondo1);
        panelRedondo1.setBounds(50, 32, 700, 436);

        javax.swing.GroupLayout pnlTecladoNumericoLayout = new javax.swing.GroupLayout(pnlTecladoNumerico);
        pnlTecladoNumerico.setLayout(pnlTecladoNumericoLayout);
        pnlTecladoNumericoLayout.setHorizontalGroup(
                pnlTecladoNumericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        pnlTecladoNumericoLayout.setVerticalGroup(
                pnlTecladoNumericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 440, Short.MAX_VALUE)
        );

        pnlGestionarAdBlue.add(pnlTecladoNumerico);
        pnlTecladoNumerico.setBounds(770, 30, 400, 440);

        pnlVentas.add(pnlGestionarAdBlue, "pnlGestionarAdBlue");

        pnlReporteVentas.add(pnlVentas);
        pnlVentas.setBounds(30, 182, 1222, 508);

        jPanel1.setBackground(new java.awt.Color(186, 12, 47));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboPromotor.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jComboPromotor.setForeground(new java.awt.Color(51, 51, 51));
        jComboPromotor.setBorder(null);
        jComboPromotor.setName(""); // NOI18N
        jPanel1.add(jComboPromotor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 410, 60));

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/actualizar.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                debounce(() -> jLabel1MousePressed(evt), 400);     ////Cambio a debounce para evitar multiples llamadas
            }
        });
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 80, 60));

        pnlReporteVentas.add(jPanel1);
        jPanel1.setBounds(190, 100, 490, 60);

        accionesDeVentas.setOpaque(false);
        accionesDeVentas.setLayout(new java.awt.CardLayout());

        accionesVentasSinResolver.setOpaque(false);
        accionesVentasSinResolver.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMedioSinResolver.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jMedioSinResolver.setForeground(new java.awt.Color(102, 102, 102));
        jMedioSinResolver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMedioSinResolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jMedioSinResolver.setText("MEDIO PAGO");
        jMedioSinResolver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMedioSinResolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMedioSinResolverMousePressed(evt);
            }
        });
        accionesVentasSinResolver.add(jMedioSinResolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 22, -1, 60));

        jAsignarDatosSinResolver.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jAsignarDatosSinResolver.setForeground(new java.awt.Color(102, 102, 102));
        jAsignarDatosSinResolver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAsignarDatosSinResolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jAsignarDatosSinResolver.setText("ASIGNAR DATOS");
        jAsignarDatosSinResolver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAsignarDatosSinResolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jAsignarDatosSinResolverMouseReleased(evt);
            }
        });
        accionesVentasSinResolver.add(jAsignarDatosSinResolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 22, -1, 60));

        jImprimirSinResolver.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jImprimirSinResolver.setForeground(new java.awt.Color(255, 255, 255));
        jImprimirSinResolver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jImprimirSinResolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/impresora-boton-gris.png"))); // NOI18N
        jImprimirSinResolver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jImprimirSinResolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jImprimirSinResolverMousePressed(evt);
            }
        });
        accionesVentasSinResolver.add(jImprimirSinResolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 24, 70, 60));

        btEnviarPagoPendiente.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btEnviarPagoPendiente.setForeground(new java.awt.Color(102, 102, 102));
        btEnviarPagoPendiente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btEnviarPagoPendiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        btEnviarPagoPendiente.setText("ENVIAR VENTA");
        btEnviarPagoPendiente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btEnviarPagoPendiente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btEnviarPagoPendienteMouseReleased(evt);
            }
        });
        accionesVentasSinResolver.add(btEnviarPagoPendiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 22, 190, 60));

        btnFinalizar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnFinalizar.setForeground(new java.awt.Color(102, 102, 102));
        btnFinalizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-large.png"))); // NOI18N
        btnFinalizar.setText("FINALIZAR RUMBO UREA");
        btnFinalizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFinalizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnFinalizarMouseReleased(evt);
            }
        });
        accionesVentasSinResolver.add(btnFinalizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 22, 240, 60));

        accionesDeVentas.add(accionesVentasSinResolver, "accionesVentasSinResolver");

        accionesHistorialVentas.setOpaque(false);
        accionesHistorialVentas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMedio.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jMedio.setForeground(new java.awt.Color(102, 102, 102));
        jMedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMedio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jMedio.setText("MEDIO PAGO");
        jMedio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMedio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMedioMousePressed(evt);
            }
        });
        accionesHistorialVentas.add(jMedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 22, 170, 60));

        jFideliza.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jFideliza.setForeground(new java.awt.Color(102, 102, 102));
        jFideliza.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jFideliza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jFideliza.setText("FIDELIZAR");
        jFideliza.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jFideliza.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jFidelizaMousePressed(evt);
            }
        });
        accionesHistorialVentas.add(jFideliza, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 22, -1, 60));

        jImprimir.setBackground(new java.awt.Color(255, 255, 255));
        jImprimir.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jImprimir.setForeground(new java.awt.Color(255, 255, 255));
        jImprimir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/impresora-boton-gris.png"))); // NOI18N
        jImprimir.setToolTipText("");
        jImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jImprimirMousePressed(evt);
            }
        });
        accionesHistorialVentas.add(jImprimir, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 24, 70, 60));

        jFactura.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jFactura.setForeground(new java.awt.Color(102, 102, 102));
        jFactura.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jFactura.setText("F. ELECTRONICA");
        jFactura.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jFacturaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jFacturaMouseReleased(evt);
            }
        });
        accionesHistorialVentas.add(jFactura, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 22, -1, 60));

        jRecuperacion.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jRecuperacion.setForeground(new java.awt.Color(102, 102, 102));
        jRecuperacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRecuperacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jRecuperacion.setText("RECUPERAR");
        jRecuperacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jRecuperacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jRecuperacionMouseReleased(evt);
            }
        });
        accionesHistorialVentas.add(jRecuperacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 22, -1, 60));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_right1.png"))); // NOI18N
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        accionesHistorialVentas.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 30, -1, -1));

        accionesDeVentas.add(accionesHistorialVentas, "accionesVentasHistorial");

        accionesHistorialVentasMod2.setToolTipText("");
        accionesHistorialVentasMod2.setOpaque(false);
        accionesHistorialVentasMod2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jAnular.setBackground(new java.awt.Color(103, 103, 102));
        jAnular.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jAnular.setForeground(new java.awt.Color(102, 102, 102));
        jAnular.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        jAnular.setText("DEVOLUCIÓN");
        jAnular.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAnular.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jAnularMousePressed(evt);
            }
        });
        accionesHistorialVentasMod2.add(jAnular, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 22, -1, 60));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_izquierda.png"))); // NOI18N
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel15MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel15MouseReleased(evt);
            }
        });
        accionesHistorialVentasMod2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 30, -1, -1));

        accionesDeVentas.add(accionesHistorialVentasMod2, "accionesVentasHistorialMod2");

        accionesUreaSinResolver.setOpaque(false);
        accionesUreaSinResolver.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCancelar.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-rojo-1.png"))); // NOI18N
        btnCancelar.setText("CANCELAR");
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnCancelarMousePressed(evt);
            }
        });
        accionesUreaSinResolver.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 22, -1, 60));

        btnAnularAdBlue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAnularAdBlue.setForeground(new java.awt.Color(255, 255, 255));
        btnAnularAdBlue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAnularAdBlue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-rojo-1.png"))); // NOI18N
        btnAnularAdBlue.setText("ANULAR");
        btnAnularAdBlue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnularAdBlue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAnularAdBlueMouseReleased(evt);
            }
        });
        accionesUreaSinResolver.add(btnAnularAdBlue, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 22, -1, 60));

        btnFinalizarVenta.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnFinalizarVenta.setForeground(new java.awt.Color(255, 255, 255));
        btnFinalizarVenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnFinalizarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"))); // NOI18N
        btnFinalizarVenta.setText("GUARDAR");
        btnFinalizarVenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFinalizarVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnFinalizarVentaMouseReleased(evt);
            }
        });
        accionesUreaSinResolver.add(btnFinalizarVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 22, 190, 60));

        accionesDeVentas.add(accionesUreaSinResolver, "accionesUreaSinResolver");

        pnlReporteVentas.add(accionesDeVentas);
        accionesDeVentas.setBounds(130, 700, 1010, 100);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnlReporteVentas.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        jpromotor2.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor2.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor2.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor2);
        jpromotor2.setBounds(800, 10, 460, 20);

        pnlPrincipal.add(pnlReporteVentas, "pnl_principal");

        pnlPromotor.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("IDENTIFICACION");
        pnlPromotor.add(jLabel6);
        jLabel6.setBounds(310, 90, 180, 40);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PROMOTOR");
        pnlPromotor.add(jLabel8);
        jLabel8.setBounds(520, 90, 480, 40);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(800, 610));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 610, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel4);

        pnlPromotor.add(jScrollPane1);
        jScrollPane1.setBounds(240, 120, 840, 580);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("SELECCIONE EL PROMOTOR");
        pnlPromotor.add(jLabel11);
        jLabel11.setBounds(140, 720, 980, 70);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnlPromotor.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlPromotor.add(jLabel30);
        jLabel30.setBounds(10, 710, 100, 80);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel32);
        jLabel32.setBounds(120, 710, 10, 80);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("CONSULTA VENTAS");
        pnlPromotor.add(jLabel12);
        jLabel12.setBounds(100, 0, 680, 90);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        pnlPromotor.add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 71);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel33);
        jLabel33.setBounds(80, 10, 10, 68);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel14MousePressed(evt);
            }
        });
        pnlPromotor.add(jLabel14);
        jLabel14.setBounds(0, 0, 1281, 800);

        pnlPrincipal.add(pnlPromotor, "pnlPromotor");

        pnl_confirmacion.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel9);
        jLabel9.setBounds(307, 250, 690, 180);

        NO.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        NO.setForeground(new java.awt.Color(255, 255, 255));
        NO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        NO.setText("NO");
        NO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NOMouseClicked(evt);
            }
        });
        pnl_confirmacion.add(NO);
        NO.setBounds(470, 480, 170, 56);

        SI1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SI1.setForeground(new java.awt.Color(255, 255, 255));
        SI1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SI1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-desactivado-small.png"))); // NOI18N
        SI1.setText("SI");
        SI1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SI1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SI1MouseClicked(evt);
            }
        });
        pnl_confirmacion.add(SI1);
        SI1.setBounds(680, 480, 163, 56);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/card.png"))); // NOI18N
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel10);
        jLabel10.setBounds(250, 210, 800, 360);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fndMousePressed(evt);
            }
        });
        pnl_confirmacion.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnl_confirmacion, "pnl_confirmacion");
        pnlPrincipal.add(card_mensaje, "card_mensaje");

        pnlMsjDatafonos.setLayout(null);

        jCerrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-2.png"))); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        pnlMsjDatafonos.add(jCerrar);
        jCerrar.setBounds(990, 18, 249, 53);

        jTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitulo.setForeground(new java.awt.Color(186, 12, 47));
        jTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitulo.setText("TITULO MENSAJE");
        jTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jTitulo);
        jTitulo.setBounds(280, 450, 740, 80);

        jInfoDatafono.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jInfoDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfoDatafono.setText("INFORMACION DATAFONO");
        jInfoDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jInfoDatafono);
        jInfoDatafono.setBounds(290, 530, 740, 28);

        jTransacciones.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTransacciones.setForeground(new java.awt.Color(186, 12, 47));
        jTransacciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTransacciones.setText("TRANSACCION");
        jTransacciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jTransacciones);
        jTransacciones.setBounds(290, 570, 740, 28);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel35);
        jLabel35.setBounds(80, 10, 10, 68);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel36);
        jLabel36.setBounds(120, 710, 10, 80);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel37);
        jLabel37.setBounds(1130, 710, 10, 80);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel38);
        jLabel38.setBounds(10, 710, 100, 80);

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"))); // NOI18N
        jIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jIcono);
        jIcono.setBounds(520, 190, 248, 248);

        fndFondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndMsjDatafonos.png"))); // NOI18N
        fndFondo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo.setRequestFocusEnabled(false);
        pnlMsjDatafonos.add(fndFondo);
        fndFondo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlMsjDatafonos, "mensajesDatafono");

        pnlMensajeGopass.setLayout(null);

        jCerrar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar1.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-2.png"))); // NOI18N
        jCerrar1.setText("CERRAR");
        jCerrar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrar1MouseClicked(evt);
            }
        });
        pnlMensajeGopass.add(jCerrar1);
        jCerrar1.setBounds(990, 18, 249, 53);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(186, 12, 47));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("GOPASS");
        pnlMensajeGopass.add(lblTitulo);
        lblTitulo.setBounds(550, 310, 390, 40);

        lblFooter.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblFooter.setForeground(new java.awt.Color(186, 12, 47));
        lblFooter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFooter.setText("PLACA");
        pnlMensajeGopass.add(lblFooter);
        lblFooter.setBounds(530, 430, 430, 40);

        lblMsg.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMsg.setText("RESPUESTA TRANSACCION");
        pnlMensajeGopass.add(lblMsg);
        lblMsg.setBounds(530, 360, 430, 70);

        fndIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/iconoCheck.png"))); // NOI18N
        pnlMensajeGopass.add(fndIcono);
        fndIcono.setBounds(380, 330, 120, 120);

        fndMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndMensaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndPanelMensaje.png"))); // NOI18N
        pnlMensajeGopass.add(fndMensaje);
        fndMensaje.setBounds(116, 220, 1080, 350);

        fndFondo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fndFondo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo1.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setRequestFocusEnabled(false);
        pnlMensajeGopass.add(fndFondo1);
        fndFondo1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlMensajeGopass, "pnlMensajeGopass");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
        pnlPrincipal.getAccessibleContext().setAccessibleName("");

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jFacturaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jFacturaMouseReleased
        if (jFactura.getIcon() != botonBloqueadoIcono) {
            selectFE();
            resetear = true;
        }
    }//GEN-LAST:event_jFacturaMouseReleased
    private void jComboMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboMostrarActionPerformed
        NovusUtils.beep();
        Async(() -> {
            refrescarDatos("BOTON REFRESCAR");
        });
    }//GEN-LAST:event_jComboMostrarActionPerformed

    private void jComboMostrarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboMostrarItemStateChanged

    }//GEN-LAST:event_jComboMostrarItemStateChanged

    private void jComboMostrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboMostrarMouseReleased

    }//GEN-LAST:event_jComboMostrarMouseReleased

    private void NOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NOMouseClicked
        if (noAceptar != null) {
            noAceptar.run();
        } else {
            mostrarMenuPrincipal(true);
        }
    }//GEN-LAST:event_NOMouseClicked

    private void SI1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SI1MouseClicked
        if (anulacion != null) {
            anulacion.run();
        } else {
            handler.run();
            mostrarMenuPrincipal(true);
        }

    }//GEN-LAST:event_SI1MouseClicked

    private void fndMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fndMousePressed

    }//GEN-LAST:event_fndMousePressed

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseReleased
        cerrar();
    }//GEN-LAST:event_jLabel13MouseReleased

    private void jLabel14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel14MousePressed

    private void btnHIstorialVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHIstorialVentasActionPerformed
        toggleActivationToggleButtonIdentifiersType(evt);
        cargarTablaHIstorialVentas(cardVentas, "ventasHistorial");
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorial");
        setEstadoActulizarDatafono(false);
        refrescarDatos("BOTON REFRESCAR");
        renderSales();
    }//GEN-LAST:event_btnHIstorialVentasActionPerformed

    private void ventasSinResolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ventasSinResolverActionPerformed
        toggleActivationToggleButtonIdentifiersType(evt);
        cargarTablaHIstorialVentas(cardVentas, "ventasSinResolver");
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasSinResolver");
        setEstadoActulizarDatafono(true);
        refrescarDatos("BOTON REFRESCAR");
        ventasSinResolver();
    }//GEN-LAST:event_ventasSinResolverActionPerformed

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorial");
    }//GEN-LAST:event_jLabel15MouseClicked

    private void jLabel15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MousePressed
        NovusUtils.beep();
        jLabel15.setIcon(btnlefttPressed);
    }//GEN-LAST:event_jLabel15MousePressed

    private void jLabel15MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseReleased
        jLabel15.setIcon(btnLeft);
    }//GEN-LAST:event_jLabel15MouseReleased

    private void jMedioSinResolverMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMedioSinResolverMousePressed
        if (jMedioSinResolver.getIcon() != botonBloqueadoIcono) {
            cambiarPago(jTable3, listaVentasSinResolver);
        }
    }//GEN-LAST:event_jMedioSinResolverMousePressed

    private void jAsignarDatosSinResolverMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jAsignarDatosSinResolverMouseReleased
        if (jAsignarDatosSinResolver.getIcon() != botonBloqueadoIcono) {

            long millisegundosTranscurridos = System.currentTimeMillis() - reciboFidelizar.getFecha().getTime();
            long tiempoAsignacion = TimeUnit.MINUTES.toMillis(tiempoMaximoCliente);
            if (millisegundosTranscurridos > tiempoAsignacion) {
                Runnable accion = () -> mostrarMenuPrincipal(true);
                showMessage("SE HA COMPLETADO EL TIEMPO MAXIMO PARA ASIGNAR DATOS",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
                refrescarDatos("REFRESCAR VENTAS");
            } else {
                asignarDatos();
            }
        }
    }//GEN-LAST:event_jAsignarDatosSinResolverMouseReleased

    private void jImprimirSinResolverMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jImprimirSinResolverMousePressed
        NovusUtils.beep();
        if (jImprimirSinResolver.getIcon() != botonBloqueadoIconoImpresora) {
            selectme(jTable3, listaVentasSinResolver);
        }

    }//GEN-LAST:event_jImprimirSinResolverMousePressed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        
        seleccionarVentasSinresolver();
        validarIsPagoDatafonoCompleto(reciboFidelizar.getNumero());
        
    }//GEN-LAST:event_jTable3MouseClicked

    private void btEnviarPagoPendienteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btEnviarPagoPendienteMouseReleased
        if (btEnviarPagoPendiente.getIcon() != botonBloqueadoIcono) {
            EnviarPagosDatafonos enviar = new EnviarPagosDatafonos();
            JsonObject respuesta = enviar.actualizarPagoMixto(idTransacciondatafono);
            mostrarRespuesta(respuesta);
        }
    }//GEN-LAST:event_btEnviarPagoPendienteMouseReleased
    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        ejecutarAccionDeCerrado = false;
        closeMensajeDatafono();
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jMedioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMedioMouseReleased

    }//GEN-LAST:event_jMedioMouseReleased

    private void jCerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        closeMensajeDatafono();
    }//GEN-LAST:event_jCerrar1MouseClicked

    private void btnCancelarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMousePressed
        mostrarPanelHistorialVentas();
    }//GEN-LAST:event_btnCancelarMousePressed

    private void btnAnularAdBlueMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAnularAdBlueMouseReleased
        if (btnAnularAdBlue.getIcon() != botonBloqueadoIcono) {
            anularAutorizacionAdBlue();
        }
    }//GEN-LAST:event_btnAnularAdBlueMouseReleased

    private void btnFinalizarVentaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinalizarVentaMouseReleased
        if (btnFinalizarVenta.getIcon() != botonBloqueadoIcono) {
            finalizarVentaAdBlue();
        }
    }//GEN-LAST:event_btnFinalizarVentaMouseReleased

    private void btnFinalizarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinalizarMouseReleased
        if (btnFinalizar.getIcon() != botonBloqueadoIconoLarge) {
            mostrarDatosVentaAdblue(reciboFidelizar);
            mostrarPanelGestionarVentaAdBlue();
        }
    }//GEN-LAST:event_btnFinalizarMouseReleased

    private void txtCantidadSuministradaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadSuministradaKeyTyped
        char c = evt.getKeyChar();
        int maximoCaracteres = 6;
        String cantidad = txtCantidadSuministrada.getText();
        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        } else if (c == '.' && cantidad.contains(".")) {
            evt.consume();
        } else if (cantidad.contains(".") && cantidad.substring(cantidad.indexOf(".")).length() >= 4) {
            evt.consume();
        } else if (cantidad.length() >= maximoCaracteres) {
            jMensajeNotificacion.setVisible(true);
            jMensajeNotificacion.setText("Maximo " + maximoCaracteres + " caracteres");
            evt.consume();
            setTimeout(3, () -> jMensajeNotificacion.setVisible(false));
        }
    }//GEN-LAST:event_txtCantidadSuministradaKeyTyped

    private void txtCantidadSuministradaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadSuministradaKeyReleased
        habilitarFinalizarVenta();
        calcularTotalVenta();
    }//GEN-LAST:event_txtCantidadSuministradaKeyReleased

    private void txtCantidadSuministradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadSuministradaActionPerformed
        boolean presiono = ((TecladoNumericoGrayDot) pnlTecladoNumerico).isPresiono();
        if (presiono && btnFinalizarVenta.getIcon() != botonBloqueadoIcono) {
            finalizarVentaAdBlue();
        }
        ((TecladoNumericoGrayDot) pnlTecladoNumerico).setPresiono(false);
    }//GEN-LAST:event_txtCantidadSuministradaActionPerformed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseReleased

    public void mostrarDatosVentaAdblue(ReciboExtended recibo) {
        float litrosAutorizados = reciboFidelizar.getAtributos()
                .get("extraData").getAsJsonObject()
                .get("response").getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("cantidadMaxima").getAsFloat();
        lblPrecioUrea.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(precioAdBlue));
        lblPlacaVentaAdBlue.setText(recibo.getAtributos()
                .getAsJsonObject().get("vehiculo_placa").getAsString());
        lblLitrosAutorizados.setText(litrosAutorizados + "");
    }

    public void habilitarFinalizarVenta() {
        if (!txtCantidadSuministrada.getText().isEmpty()) {
            botonActivo(btnFinalizarVenta);
        } else {
            botonInactivo(btnFinalizarVenta);
        }
    }

    public void calcularTotalVenta() {
        if (txtCantidadSuministrada.getText().isEmpty()) {
            lblTotalVenta.setText(NovusConstante.SIMBOLS_PRICE + " " + 0);
        } else {
            float cantidadSuministrada = Float.parseFloat(txtCantidadSuministrada.getText());
            float totalVenta = precioAdBlue * cantidadSuministrada;
            lblTotalVenta.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(totalVenta));
        }
    }

    public void finalizarVentaAdBlue() {
        float litrosAutorizados = reciboFidelizar.getAtributos()
                .get("extraData").getAsJsonObject()
                .get("response").getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("cantidadMaxima").getAsFloat();
        float cantidadSuministrada = !txtCantidadSuministrada.getText().isEmpty()
                ? Float.parseFloat(txtCantidadSuministrada.getText())
                : 0;
        if (cantidadSuministrada <= 0) {
            jMensajeNotificacion.setVisible(true);
            jMensajeNotificacion.setText("Valor no valido");
            setTimeout(3, () -> jMensajeNotificacion.setVisible(false));
        } else if (cantidadSuministrada > litrosAutorizados) {
            jMensajeNotificacion.setVisible(true);
            jMensajeNotificacion.setText("Valor supera el maximo Autorizado");
            setTimeout(3, () -> jMensajeNotificacion.setVisible(false));
        } else {
            finalizarAutorizacionAdBlue(cantidadSuministrada);
        }
    }

    public JsonObject builJsonFinalizarVentaAdBlue(float cantidadSuministrada) {

        float precioAdblue = precioAdBlue;

        JsonObject json = new JsonObject();
        JsonObject dataRequest = reciboFidelizar.getAtributos()
                .get("extraData").getAsJsonObject()
                .get("request").getAsJsonObject();

        JsonObject dataResponse = reciboFidelizar.getAtributos()
                .get("extraData").getAsJsonObject()
                .get("response").getAsJsonObject()
                .get("data").getAsJsonObject();

        json.addProperty("movimientoId", reciboFidelizar.getNumero());
        json.addProperty("identificadorAutorizacionEDS", dataResponse.get("identificadorAutorizacionEDS").getAsString());
        json.addProperty("identificadorAprobacion", dataResponse.get("identificadorAprobacion").getAsString());
        json.addProperty("documentoIdentificacionCliente", dataResponse.get("documentoIdentificacionCliente").getAsString());
        json.addProperty("programaCliente", dataResponse.get("programaCliente").getAsString());
        json.addProperty("precioUnidadCliente", precioAdblue);
        json.addProperty("codigoFamiliaProducto", NovusConstante.CODIGO_FAMILIA_PRODUCTO_UREA);
        json.addProperty("precioUnidad", precioAdblue);
        json.addProperty("informacionAdicional", "1");
        json.addProperty("numeroCara", 1);
        json.addProperty("codigoIsla", String.valueOf(Main.credencial.getIsla()));
        json.addProperty("numeroManguera", 1);
        json.addProperty("fechaInicioVenta", VentasCombustibleFacade.obtenerFormatoFechaActual());
        json.addProperty("fechaFinVenta", VentasCombustibleFacade.obtenerFormatoFechaActual());
        json.addProperty("numeroTicketeVenta", generateNumeroTicket());
        json.addProperty("serialIdentificador", dataRequest.get("serialIdentificador").getAsString());
        json.addProperty("cantidadTotal", cantidadSuministrada);
        json.addProperty("costoTotalCliente", (cantidadSuministrada * precioAdblue));
        json.addProperty("costoTotalManguera", (cantidadSuministrada * precioAdblue));
        json.addProperty("valorDescuentoCliente", 0);
        json.addProperty("valorIva", 0);
        json.addProperty("codigoProducto", obtenerCodigoExternoUreaUseCase.execute());
        json.addProperty("identificadorTipoDocumentoPromotor", dataRequest.get("codigoTipoIdentificador").getAsString());
        json.addProperty("documentoIdentificacionPromotor", dataRequest.get("identificadorPromotor").getAsString());
        json.addProperty("identificadorTurno", String.valueOf(obtenerJornadaIdUseCase.execute()));
        json.addProperty("numeroTurno", 1);
        json.addProperty("codigoEstacion", obtenerCodigoEstacionUseCase.execute());
        json.addProperty("nitEstacion", Main.credencial.getEmpresa().getNit());
        json.addProperty("nombreRegional", "REGIONAL");
        json.addProperty("nombreEstacion", Main.credencial.getEmpresa().getAlias());
        json.addProperty("placaVehiculo", dataResponse.get("placaVehiculo").getAsString());
        json.addProperty("valorOdometro", dataResponse.get("valorOdometro").getAsString());
        json.addProperty("lecturaInicial", 1);
        json.addProperty("lecturaFinal", 1);

        return json;
    }

    private String generateNumeroTicket() {
        StringBuilder buildNumeroTicket = new StringBuilder();
        if (String.valueOf(Main.credencial.getIsla()).length() == 1) {
            return buildNumeroTicket.append("0")
                    .append(Main.credencial.getIsla())
                    .append(reciboFidelizar.getNumero()).toString();
        }
        return buildNumeroTicket.append(Main.credencial.getIsla())
                .append(reciboFidelizar.getNumero()).toString();
    }

    public void finalizarAutorizacionAdBlue(float cantidadSuministrada) {
        showMessage("Finalizando venta espere",
                "/com/firefuel/resources/loader_fac.gif",
                false, null,
                false, LetterCase.FIRST_UPPER_CASE);
        setTimeout(2, () -> {
            JsonObject response = RumboFacade.finalizarVentaAdblue(builJsonFinalizarVentaAdBlue(cantidadSuministrada));
            procesarRespuestaFinalizacion(response, cantidadSuministrada);
        });
    }

    public void procesarRespuestaFinalizacion(JsonObject response, float cantidadSuministrada) {
        Runnable accion = () -> {
            botonInactivo(btnFinalizar);
            mostrarMenuPrincipal(true);
            mostrarPanelHistorialVentas();
        };

        if (response != null) {
            if (response.has("codigoError")) {
                showMessage("Error al finalizar la venta",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {

                float total = (cantidadSuministrada * (precioAdBlue));

                JsonObject dataActualizar = new JsonObject();
                dataActualizar.addProperty("movimientoId", reciboFidelizar.getNumero());
                dataActualizar.addProperty("cantidadSuministrada", cantidadSuministrada);
                dataActualizar.addProperty("totalVenta", total);

                new ActualizarCtMovimientosUseCase(dataActualizar).execute();
                imprimirVenta(reciboFidelizar.getNumero());
                showMessage("Venta finalizada",
                        "/com/firefuel/resources/btOk.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("Error al finalizar la venta",
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    public JsonObject buildJsonImpresion(float cantidadSuministrada) {
        JsonObject impresion = new JsonObject();
        JsonObject data = reciboFidelizar.getAtributos();
        impresion.addProperty("promotor", new ObtenerNombrePromotorUseCase(Long.valueOf(reciboFidelizar.getIdentificacionPromotor())).execute());
        impresion.addProperty("islas", Main.credencial.getIsla());
        impresion.addProperty("cara", 1);
        impresion.addProperty("familiaDesc", "UREA");
        impresion.addProperty("precio", precioAdBlue);
        impresion.addProperty("cantidad", cantidadSuministrada);
        impresion.addProperty("total", (cantidadSuministrada * precioAdBlue));
        impresion.addProperty("identificacion", 2);
        impresion.addProperty("medio", "2");
        impresion.addProperty("placa", data.get("vehiculo_placa").getAsString());
        return impresion;
    }

    public void anularAutorizacionAdBlue() {
        showMessage("Anulando autorización espere",
                "/com/firefuel/resources/loader_fac.gif",
                false, null,
                false, LetterCase.FIRST_UPPER_CASE);
        setTimeout(2, () -> {
            JsonObject response = RumboFacade.liberacionVentaAdBlue(buildJsonLiberacion());
            procesarRespuestaAnulacion(response);
        });
    }

    public void procesarRespuestaAnulacion(JsonObject response) {
        Runnable accion = () -> {
            botonInactivo(btnFinalizar);
            mostrarMenuPrincipal(true);
            mostrarPanelHistorialVentas();
        };
        if (response != null) {
            if (response.has("codigoError")) {
                showMessage("Error al realizar la anulación",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                new ActualizarEstadoMovimientoUseCase(reciboFidelizar.getNumero()).execute();
                showMessage("Autorización anulada",
                        "/com/firefuel/resources/btOk.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("Error al finalizar la venta",
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    public void validacionCaracteres(java.awt.event.KeyEvent evt) {
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, txtCantidadSuministrada, 10, jMensajeNotificacion, caracteresAceptados);
    }

    public void closeMensajeDatafono() {
        mostrarMenuPrincipal(false);
        if (timer != null) {
            timer.stop();
        }
    }

    private void jAnularMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jAnularMousePressed
        if (jAnular.getIcon() != botonBloqueadoIcono && !resetear) {
            confirmar("<html><center>¿Desea generar la devolución de esta venta?</center></html>");
            anulacion = () -> {
                if (InfoViewController.turnosPersonas.size() > 1) {
                    showPanel("pnlPromotor");
                } else {
                    selectmeAnulacion();
                }
            };
            noAceptar = () -> {
                showPanel("pnl_principal");
            };
        }
    }

    private void jFidelizaMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFidelizaMousePressed
        if (jFideliza.getIcon() != botonBloqueadoIcono) {
            // 🔍 VALIDACIÓN GLP: Verificar si la venta es GLP
            if (esVentaGLP(reciboFidelizar)) {
                NovusUtils.printLn("🚫 [BOTON FIDELIZAR] Venta GLP detectada - Botón desactivado");
                Runnable accion = () -> mostrarMenuPrincipal(true);
                showMessage("EL BOTÓN FIDELIZAR NO ESTÁ DISPONIBLE PARA VENTAS GLP",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
                return;
            }
            
            //fidelizada = dao.fueFidelizada(reciboFidelizar);
            fidelizada = SingletonMedioPago.ConetextDependecy.getValidarBotonesVentasAppterpel().execute(reciboFidelizar.getNumero()).isFidelizacion();
            if (NovusConstante.HAY_INTERNET) {
                fidelizar(fidelizada);
            } else {
                Date fechaRecibo = reciboFidelizar.getFecha();
                Date fechaActual = new Date();
                long millisegundosTranscurridos = fechaActual.getTime() - fechaRecibo.getTime();
                if (millisegundosTranscurridos > 180000) {
                    Runnable accion = () -> mostrarMenuPrincipal(true);
                    showMessage("NO SE PUEDE FIDELIZAR UNA VENTA HECHA HACE MAS DE 3 MINUTOS",
                            "/com/firefuel/resources/btBad.png",
                            true, accion,
                            true, LetterCase.FIRST_UPPER_CASE);
                    renderSales();
                } else {
                    if (FinByTransaccionProcesoUseCase.isValidEdit(id)) {
                        FidelizacionCliente fidel = new FidelizacionCliente(this, true, true, id);
                        fidel.setVisible(true);
                        setTimeout(1, () -> {
                            renderSales();
                        });
                    } else {
                        fidelizar(fidelizada);
                    }
                }

            }
        }
    }// GEN-LAST:event_jFidelizaMousePressed

    private void fidelizar(boolean fidelizada) {
        if (fidelizada) {
            Date fechaRecibo = reciboFidelizar.getFecha();
            Date fechaActual = new Date();
            long millisegundosTranscurridos = fechaActual.getTime() - fechaRecibo.getTime();
            if (millisegundosTranscurridos > 180000) {
                Runnable accion = () -> mostrarMenuPrincipal(true);
                showMessage("NO SE PUEDE FIDELIZAR UNA VENTA REALIZADA HACE MÁS DE 3 MINUTOS",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
                renderSales();
            } else {
                if (NovusConstante.HAY_INTERNET) {
                    Runnable accion = () -> mostrarMenuPrincipal(true);
                    FidelizacionCliente fidel = new FidelizacionCliente(this.parent, true, reciboFidelizar, false, accion);
                    showDialog(fidel);
                    renderSales();
                } else {
                    FidelizacionCliente fidel = new FidelizacionCliente(this.parent, true, reciboFidelizar, false);
                    fidel.setVisible(true);
                    setTimeout(1, () -> {
                        renderSales();
                    });
                }
            }
        } else {
            Runnable accion = () -> mostrarMenuPrincipal(true);
            showMessage("VENTA FIDELIZADA ANTERIORMENTE",
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    true, LetterCase.FIRST_UPPER_CASE);
            renderSales();
        }
    }

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        Async(() -> {
            refrescarDatos("BOTON REFRESCAR");
        });

    }// GEN-LAST:event_jLabel1MousePressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        seleccionar();
        validarIsPagoDatafonoCompleto(reciboFidelizar.getNumero());

        VentasAppterpelBotonesValidador validador = SingletonMedioPago.ConetextDependecy.getValidarBotonesVentasAppterpel().execute(reciboFidelizar.getNumero());

        if (validador.isPago() && validador.isProceso()) {
            botonInactivo(jRecuperacion);
            botonInactivo(jMedio);

        }

//        if (validador.isFidelizacion()) {
//            botonActivo(jFideliza);
//        } else {
//            botonInactivo(jFideliza);
//
//        }

    }// GEN-LAST:event_jTable1MouseClicked

    private void jImprimirMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jImprimirMousePressed
        NovusUtils.beep();
        if (jImprimir.getIcon() != botonBloqueadoIconoImpresora) {
            selectme(jTable1, lista);
        }
    }// GEN-LAST:event_jImprimirMousePressed

    private void jMedioMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jMedioMousePressed
        if (jMedio.getIcon() != botonBloqueadoIcono) {
            cambiarPago(jTable1, this.lista);
        }
    }// GEN-LAST:event_jMedioMousePressed

    private void jFacturaMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFacturaMousePressed
        NovusUtils.beep();
        if (jFactura.getIcon() != botonBloqueadoIcono) {
            selectFE();
            resetear = true;
        }
    }// GEN-LAST:event_jFacturaMousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jAnularMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jAnularMouseReleased

    }// GEN-LAST:event_jAnularMouseReleased

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        NovusUtils.beep();
        jLabel4.setIcon(btnRightPressed);
    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        jLabel4.setIcon(btnRight);
    }// GEN-LAST:event_jLabel4MouseReleased

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseClicked
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorialMod2");
    }// GEN-LAST:event_jLabel4MouseClicked

    private void jRecuperacionMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jRecuperacionMouseReleased
        if (jRecuperacion.getIcon() != botonBloqueadoIcono) {
            abrirRecuperacionVenta();
            renderSales();
        }
    }// GEN-LAST:event_jRecuperacionMouseReleased

    private void cerrar() {
        if (timer != null) {
            timer.stop();
        }
        timer = null;
        InfoViewController.NotificadorVentasHistorial = null;
        parent.cargarNotificador();
        if (lista != null) {
            lista.clear();
        }
        lista = null;
        reciboFidelizar = null;
        estadoActulizar = false;
        estadoActulizarDatafono = false;

        if (runnable != null) {
            runnable.run();
        }
        parent.mostrarSubPanel(new VentaMenuPanelController(parent, parent));
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NO;
    private javax.swing.JLabel SI1;
    private javax.swing.JPanel accionesDeVentas;
    private javax.swing.JPanel accionesHistorialVentas;
    private javax.swing.JPanel accionesHistorialVentasMod2;
    private javax.swing.JPanel accionesUreaSinResolver;
    private javax.swing.JPanel accionesVentasSinResolver;
    private javax.swing.JLabel btEnviarPagoPendiente;
    private javax.swing.JLabel btnAnularAdBlue;
    private javax.swing.JLabel btnCancelar;
    private javax.swing.JLabel btnFinalizar;
    private javax.swing.JLabel btnFinalizarVenta;
    private javax.swing.JToggleButton btnHIstorialVentas;
    private javax.swing.ButtonGroup buttonGroup1;
    public static javax.swing.JPanel cardVentas;
    private javax.swing.JPanel card_mensaje;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel fndFondo;
    private javax.swing.JLabel fndFondo1;
    private javax.swing.JLabel fndIcono;
    private javax.swing.JLabel fndMensaje;
    private javax.swing.JLabel jAnular;
    private javax.swing.JLabel jAsignarDatosSinResolver;
    public static javax.swing.JLabel jCerrar;
    public static javax.swing.JLabel jCerrar1;
    private javax.swing.JComboBox<String> jComboMostrar;
    private javax.swing.JComboBox<PersonaBean> jComboPromotor;
    private javax.swing.JLabel jFactura;
    private javax.swing.JLabel jFideliza;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jImprimir;
    private javax.swing.JLabel jImprimirSinResolver;
    private javax.swing.JLabel jInfoDatafono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMedio;
    private javax.swing.JLabel jMedioSinResolver;
    private javax.swing.JLabel jMensajeNotificacion;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel jRecuperacion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JLabel jTitulo;
    private javax.swing.JLabel jTransacciones;
    private ClockViewController jclock;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JLabel jpromotor2;
    private javax.swing.JLabel jpromotor3;
    private javax.swing.JLabel jpromotor4;
    private javax.swing.JLabel lblCantidadSuministrada;
    private javax.swing.JLabel lblContenedor;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JLabel lblLitrosAutorizados;
    private javax.swing.JLabel lblMensajeLitrosAutorizados;
    private javax.swing.JLabel lblMensajePlaca;
    private javax.swing.JLabel lblMensajePrecio;
    private javax.swing.JLabel lblMensajeTotalVenta;
    private javax.swing.JLabel lblMsg;
    private javax.swing.JLabel lblPlacaVentaAdBlue;
    private javax.swing.JLabel lblPrecioUrea;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalVenta;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    private javax.swing.JPanel pnlGestionarAdBlue;
    private javax.swing.JPanel pnlHistorialVentas;
    private javax.swing.JPanel pnlMensajeGopass;
    private javax.swing.JPanel pnlMsjDatafonos;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlPromotor;
    private javax.swing.JPanel pnlReporteVentas;
    private javax.swing.JPanel pnlTecladoNumerico;
    private javax.swing.JPanel pnlVentas;
    private javax.swing.JPanel pnl_confirmacion;
    private static javax.swing.JTextField txtCantidadSuministrada;
    private javax.swing.JToggleButton ventasSinResolver;
    // End of variables declaration//GEN-END:variables

    ///AQUI VOY
    public void refrescarDatos(String origen) {
        renderSales();
        if(enPorcesoDeRefresco){
            System.out.println("Refresco en cutso, ingnorando llamada desde: " + origen);
            return;
        }

        enPorcesoDeRefresco = true;

        int limite = 20;
        try {
            if (!jComboMostrar.getSelectedItem().toString().toLowerCase().contains("todos")) {
                limite = Integer.parseInt(jComboMostrar.getSelectedItem().toString().trim());
            }

            PersonaBean person = (PersonaBean) jComboPromotor.getSelectedItem();
            if (person != null) {
                PersonaBean promotor = null;
                if (person.getId() != 0) {
                    promotor = person;
                }
                if (promotor != null) {
                    Main.persona = promotor;
                    jpromotor.setText(Main.persona.getNombre());
                } else {
                    jpromotor.setText("TODOS");
                }
                if (lista != null) {
                    lista.clear();
                }
                jTable1.clearSelection();
                botonesBloqueados();
                long ahora = System.currentTimeMillis();
                if((ahora - utlimaConsultaVentas) > INTERVALO_MINIMO_MS){
                    solicitarVentas(promotor, origen, limite);
                    // <- fuerza la renderización sin depender de la lógica de debounce
                    utlimaConsultaVentas = ahora;
                }else{
                    System.out.println("Evita llamada a solictarVentas por debounce } (" + origen +")");
                }

                if(!estadoActulizarDatafono){
                    renderSales();
                }
                else {
                    if((ahora - getUtlimaConsultaVentasPendientes) > INTERVALO_MINIMO_MS){
                        ventasSinResolver();
                        getUtlimaConsultaVentasPendientes = ahora;
                    }else{
                        System.out.println("Evita llamada a VentasinResolver por debounce ("+ origen+ ")");
                    }
                }
//                if (!estadoActulizarDatafono) {
//                    renderSales();
//                } else {
//                    ventasSinResolver();
//                }
                AsignacionClienteBean.getDatosCliente().clear();
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            enPorcesoDeRefresco = false;
        }
    }

    void toggleActivationToggleButtonIdentifiersType(java.awt.event.ActionEvent evt) {
        reset();
        if (((JToggleButton) evt.getSource()).isSelected()) {
            ((JToggleButton) evt.getSource()).setForeground(new Color(223, 29, 20));
            ((JToggleButton) evt.getSource()).setIcon(botonTabsSeleccionado);
        } else {
            ((JToggleButton) evt.getSource()).setForeground(new Color(167, 25, 22));
            ((JToggleButton) evt.getSource()).setIcon(botonTabsNoSeleccionado);
        }

    }

    private void reset() {
        btnHIstorialVentas.setForeground(new Color(167, 25, 22));
        btnHIstorialVentas.setIcon(botonTabsNoSeleccionado);
        ventasSinResolver.setForeground(new Color(167, 25, 22));
        ventasSinResolver.setIcon(botonTabsNoSeleccionado);
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
        System.out.println(json);
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, e);
        }
        if (response == null) {
            Runnable accion = () -> mostrarMenuPrincipal(true);
            showMessage("OCURRIO UN ERROR EN EL PROCESO ",
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    false, LetterCase.FIRST_UPPER_CASE);
        } else {
            Runnable accion = () -> mostrarMenuPrincipal(true);
            showMessage("VENTA CONVERTIDA A CONSUMO PROPIO EXITOSAMENTE",
                    "/com/firefuel/resources/btOk.png",
                    true, accion,
                    false, LetterCase.FIRST_UPPER_CASE);
            this.setVisible(false);
        }

    }

    private void futureEjecution(Runnable runnable) {
        CompletableFuture.runAsync(runnable);

    }
    //Retorno del cambio de AsyncExecutor a Thread
    private void Async(Runnable runnable) {
        new Thread(() -> {
        try {
            Thread.sleep(100);
            runnable.run();
        } catch (InterruptedException e) {
            Logger.getLogger(VentasHistorialView.class
                    .getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();


    }

    private void Async(Runnable runnable, int seconds) {
        new Thread(() -> {
            try {
                Thread.sleep(1000 * seconds);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(VentasHistorialView.class
                        .getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    //Promotores
    private void selectmeAnulacion(long id) {
        NotasCredito nota = new NotasCredito();
        Runnable panelPrincipal = () -> refrescarDatos("BOTON REFRESCAR");
        showPanel("pnl_principal");
        nota.enviar(notaP, parent, this, true, panelPrincipal, id);
    }

    //Un Promotor
    private void selectmeAnulacion() {
        NotasCredito nota = new NotasCredito();
        Runnable panelPrincipal = () -> refrescarDatos("BOTON REFRESCAR");
        showPanel("pnl_principal");
        nota.enviar(notaP, parent, this, true, panelPrincipal);
    }

    //Se muestran los Promotores con Turnos Iniciados
    private void renderizarUsuarios() {
        try {
            int i = 0;
            int height = 97;
            for (PersonaBean personaT : InfoViewController.turnosPersonas) {
                PromotoresVentas item = new PromotoresVentas(personaT);
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        id = personaT.getId();
                        selectmeAnulacion(id);
                    }
                });
                jPanel4.add(item);
                item.setBounds(10, (i * height) + (5 * (i + 1)), 763, height);
                i++;
            }
        } catch (Exception ex) {
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectme(JTable table, ArrayList<ReciboExtended> lista) {
        try {
            int r = table.getSelectedRow();
            long value = (long) table.getValueAt(r, 1);
            
            // Verificar si el botón está bloqueado
            if (botonImprimirBloqueado) {
                NovusUtils.printLn("Botón de impresión bloqueado - ID: " + value);
                return;
            }
            
            // Tipo de reporte fijo para este flujo
            String reportType = "CONSULTAR_VENTAS";

                        // Verificar si ya está en cola de impresión
            if (existeEnColaPendiente(value, reportType)) {
                            NovusUtils.printLn("El registro ya está en cola de impresión - ID: " + value);
                            return;
                        }
            guardarRegistroPendiente(value, reportType);
            
            // BLOQUEAR INMEDIATAMENTE al hacer click (solo para UI)
            bloquearBotonImprimir();
                        
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
                    
                    // Ejecutar el proceso de verificación e impresión en un hilo separado
                    // para permitir que la UI se actualice primero
                    new Thread(() -> {
                        try {
                            // 1. Verificar que el servicio de impresión esté activo y saludable
                            // Usar caso de uso con cache integrado
                            CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
                            
                            if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                                // Servicio no responde o no está saludable - eliminar registro de cola y actualizar UI
                                eliminarRegistroPendiente(valueFinal, "CONSULTAR_VENTAS");
                                final String mensaje = healthResult.obtenerMensajeError();
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    NovusUtils.printLn("❌ Servicio de impresión no está disponible: " + mensaje);
                                    desbloquearBotonImprimir();
                                    Runnable accion = () -> mostrarMenuPrincipal(true);
                                    showMessage(mensaje,
                                            "/com/firefuel/resources/btBad.png",
                                            true, accion,
                                            true, LetterCase.FIRST_UPPER_CASE);
                                });
                                return;
                            }
                        
                            // 3. Servicio OK - Proceder con la impresión en el EDT
                            javax.swing.SwingUtilities.invokeLater(() -> {
                        refrescar = () -> {
                            refrescarDatos("BOTON REFRESCAR");
                            refrescar = null;
                        };
                        ImpresionVenta ventaPlacaImprimir = new ImpresionVenta();
                                boolean isFE = isFE(valueFinal);
                        ImpresionVenta.ResultadoImpresion resultado;
                                if (!isFE(valueFinal) || !isEspecial(valueFinal)) {
                                    resultado = ventaPlacaImprimir.imprimirVenta(reciboFinal, isFE, "");
                        } else {
                                    resultado = ventaPlacaImprimir.imprimirVenta(reciboFinal, isFE, "factura");
                        }
                        mostrarResultadoImpresion(resultado);
                            });
                            
                        } catch (Exception e) {
                            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, e);
                            eliminarRegistroPendiente(valueFinal, "CONSULTAR_VENTAS");
                            javax.swing.SwingUtilities.invokeLater(() -> desbloquearBotonImprimir());
                        }
                    }).start();
                }
            } else {
                // No hay turno - desbloquear el botón
                desbloquearBotonImprimir();
                Runnable accion = () -> mostrarMenuPrincipal(true);
                showMessage("DEBE INICIAR TURNO PARA ESTA ACCION",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        false, LetterCase.FIRST_UPPER_CASE);
            }
        } catch (Exception ex) {
            // En caso de error, desbloquear el botón
            desbloquearBotonImprimir();
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarResultadoImpresion(ImpresionVenta.ResultadoImpresion resultado) {
        if (resultado == null) {
            showMessage("No se obtuvo respuesta del servicio de impresión",
                    "/com/firefuel/resources/btBad.png",
                    true, () -> {
                    },
                    true, LetterCase.FIRST_UPPER_CASE);
            return;
        }

        boolean exito = resultado.esExito();
        Runnable accion = () -> mostrarMenuPrincipal(true);

        showMessage(resultado.getMensaje(),
                exito ? "/com/firefuel/resources/btOk.png" : "/com/firefuel/resources/btBad.png",
                true, accion,
                true, LetterCase.FIRST_UPPER_CASE);
    }

    public void volverMenu() {
        if (ventaPlaca != null) {
            ventaPlaca.setVisible(false);
            ventaPlaca.dispose();
            pnlPrincipal.getLayout().removeLayoutComponent(ventaPlaca);
            ventaPlaca = null;
        }
        refrescar = null;
        cerrar();
        mostrarMenuPrincipal(true);
    }


    private final VentasService ventasService = new VentasService();
    public void solicitarVentas(PersonaBean persona, String origen, int length) {

        long promotorId = 0;
        if (persona != null && persona.getId() != 0) {
            promotorId = persona.getId();
        }
        long jornada = getJornadasByGroupUseCase.execute();
        List<VentaDTO> ventas = ventasService.obtenerVentasBasicas(promotorId, jornada, length, estadoActulizarDatafono);
        if (ventas != null) {
            // Depuración: Verifica los valores de 'prefijo' y 'consecutivo'
            for (VentaDTO venta : ventas) {
                System.out.println("Venta: " + venta.getPrefijo());  // Verifica que 'prefijo' esté asignado correctamente
            }

            buildSales(ventas);  // Construir las ventas a mostrar

            if (!estadoActulizarDatafono && !ventas.isEmpty()) {
                renderSales();  // Mostrar las ventas en la interfaz
            }
        }
        System.out.println("Obtenidas ventas: " + ventas.size());

    }

    public void buildSales(List<VentaDTO> ventasDTO) {
        if (lista == null) lista = new ArrayList<>();
        lista.clear();
        listaVentasSinResolver.clear();
        SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

        for (VentaDTO dto : ventasDTO) {
            try {
                ReciboExtended rec = new ReciboExtended();

                rec.setNumero(dto.getNumero());
                rec.setFecha(sdfSQL.parse(dto.getFecha()));
                rec.setProducto(dto.getProducto());
                rec.setOperador(dto.getOperador());
                rec.setCara(dto.getCara());
                rec.setPlaca(dto.getPlaca());
                rec.setCantidadFactor(dto.getCantidad());
                rec.setTotal(dto.getTotalRaw());
                rec.setPrefijo(dto.getPrefijo());
                System.out.println("Asignando prefijo visual: " + dto.getPrefijo());

                // ✅ Obtener ID del producto
                long idProducto = GetByProductoMovimientoDetalleUseCase.obtenerProductoId(dto.getNumero());
                rec.setIdentificacionProducto(idProducto);

                // ✅ Atributos JSON
                JsonObject atributos = dto.getAtributosJson();
                // desde vehiculo_odometro
                if (atributos.has("vehiculo_odometro") && !atributos.get("vehiculo_odometro").isJsonNull()) {
                    try {
                        rec.setOdometro(String.valueOf(atributos.get("vehiculo_odometro").toString().matches("\\d+") ? atributos.get("vehiculo_odometro").getAsLong() : 0L));
                    } catch (Exception e) {
                        System.err.println("⚠️ Error parseando vehiculo_odometro: " + atributos.get("vehiculo_odometro"));
                    }
                }

                // desde extraData.response.data.valorOdometro
                if (atributos.has("extraData") && atributos.get("extraData").isJsonObject()) {
                    JsonObject extra = atributos.getAsJsonObject("extraData");
                    if (extra.has("response") && extra.get("response").isJsonObject()) {
                        JsonObject response = extra.getAsJsonObject("response");
                        if (response.has("data") && response.get("data").isJsonObject()) {
                            JsonObject data = response.getAsJsonObject("data");
                            if (data.has("valorOdometro") && !data.get("valorOdometro").isJsonNull()) {
                                try {
                                    rec.setOdometro(String.valueOf(data.get("valorOdometro").getAsLong()));
                                } catch (Exception e) {
                                    System.err.println("⚠️ Error parseando valorOdometro desde extraData.response.data");
                                }
                            }
                        }
                    }
                }


                rec.setAtributos(atributos);

                // ✅ Añadir "proceso" si viene del DTO
                if (rec.getAtributos() != null && dto.getProceso() != null && !dto.getProceso().isBlank()) {
                    rec.getAtributos().addProperty("proceso", dto.getProceso());
                    System.out.println(">>> Venta #" + dto.getNumero() + " atributos con proceso:");
                    System.out.println(rec.getAtributos().toString());
                }

                // ✅ Estado datafono
                if (dto.getEstadoDatafono() != null && !dto.getEstadoDatafono().isBlank()) {
                    rec.getAtributos().addProperty("descripcionTransaccionEstadoDatafono", dto.getEstadoDatafono());
                }

                // ✅ Clasificación por estado
                boolean datafonoConEstado = dto.getEstadoDatafono() != null && !dto.getEstadoDatafono().isBlank();
                if (dto.isClienteSinAsignar() || datafonoConEstado) {
                    listaVentasSinResolver.add(rec);
                } else {
                    lista.add(rec);
                }

            } catch (ParseException ex) {
                Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE,
                        "Error procesando DTO con número: " + (dto != null ? dto.getNumero() : "null"), ex);
            }
        }

        System.out.println("BuildSales - lista: " + lista.size() + ", sinResolver: " + listaVentasSinResolver.size());
    }



    public void renderSales() {
        if (enPorcesoDeRefresco) {
            System.out.println("renderSales en proceso, ignorando llamada.");
            return;
        }
        enPorcesoDeRefresco = true;

        try {
            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();

            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
            Map<Long, VentasSinResolverFE> renderizarClientesSinClientes = new TreeMap<>();
            int i = 0;

            for (Recibo recibo : lista) {
                try {
                    // ✅ Usar el prefijo visual precargado
                    String prefijoVisual = (recibo instanceof ReciboExtended) ? ((ReciboExtended) recibo).getPrefijo() : "N/A";
                    if (prefijoVisual == null || prefijoVisual.isBlank()) {
                        prefijoVisual = "N/A";
                    }

                    // ✅ Medida segura
                    String medida = "GL";
                    JsonObject atributos = recibo.getAtributos();
                    if (atributos != null && atributos.has("medida") && !atributos.get("medida").isJsonNull()) {
                        String medidaValor = atributos.get("medida").getAsString();
                        if (!"UNDEFINED".equalsIgnoreCase(medidaValor)) {
                            medida = medidaValor.toUpperCase();
                        }
                    }

                    String cantidadFormateada = String.format("%.3f", recibo.getCantidadFactor()) + " " + medida;

                    // ✅ Agregar fila
                    defaultModel.addRow(new Object[]{
                            prefijoVisual,
                            recibo.getNumero(),
                            sdf2.format(recibo.getFecha()),
                            recibo.getProducto(),
                            recibo.getOperador(),
                            (recibo.getCara() != null && Integer.parseInt(recibo.getCara()) > 0) ? recibo.getCara() : "",
                            recibo.getPlaca(),
                            cantidadFormateada,
                            "$ " + df.format(recibo.getTotal())
                    });

                    // ✅ Marcar ventas sin resolver para resaltar
                    if (recibo.getNumero() == lista.get(i).getNumero() && lista.get(i).isClienteSinAsignar()) {
                        renderizarClientesSinClientes.put(
                                recibo.getNumero(),
                                new VentasSinResolverFE(recibo.getNumero(), lista.get(i).isClienteSinAsignar())
                        );
                    }
                    i++;

                } catch (Exception e) {
                    Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, "Error renderizando fila: ", e);
                }
            }

            jTable1.setModel(defaultModel);
            jTable1.clearSelection();
            botonesBloqueados();
            jFideliza.setText("FIDELIZAR");

            RenderTablaColor colroTabla = new RenderTablaColor();
            colroTabla.datos(renderizarClientesSinClientes);
            jTable1.setDefaultRenderer(Object.class, colroTabla);

        } catch (Exception ex) {
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, "Error general en renderSales: ", ex);
        } finally {
            enPorcesoDeRefresco = false;
        }
    }

    private void ventasSinResolver() {
        DefaultTableModel dm = (DefaultTableModel) jTable3.getModel();
        jTable3.getColumnModel().getColumn(2).setPreferredWidth(110);
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable3.getModel();

        MovimientosDao dao = new MovimientosDao(); // 👈 para fallback del consecutivo

        for (Recibo recibo : listaVentasSinResolver) {
            String consecutivo = "N/A";
            String prefijo = "";
            String proceso = "";
            String estado = "";
            long numeroDatatafono = 0L;

            JsonObject atributos = recibo.getAtributos();
            boolean esRemision = false;

            if (atributos != null) {
                if (atributos.has("tipoVenta") && !atributos.get("tipoVenta").isJsonNull()) {
                    esRemision = atributos.get("tipoVenta").getAsInt() == 100;
                }

                if (atributos.has("proceso") && !atributos.get("proceso").isJsonNull()) {
                    proceso = atributos.get("proceso").getAsString();

                }

                if (atributos.has("descripcionTransaccionEstadoDatafono") && !atributos.get("descripcionTransaccionEstadoDatafono").isJsonNull()) {
                    estado = atributos.get("descripcionTransaccionEstadoDatafono").getAsString();
                }

                if (atributos.has("idTransaccionDatafono") && !atributos.get("idTransaccionDatafono").isJsonNull()) {
                    numeroDatatafono = atributos.get("idTransaccionDatafono").getAsLong();
                }

                if (atributos.has("consecutivo") && !atributos.get("consecutivo").isJsonNull()) {
                    JsonElement consElem = atributos.get("consecutivo");
                    if (consElem.isJsonObject()) {
                        JsonObject consObj = consElem.getAsJsonObject();
                        if (consObj.has("consecutivo_actual") && !consObj.get("consecutivo_actual").isJsonNull()) {
                            consecutivo = consObj.get("consecutivo_actual").getAsString();
                        }
                        if (consObj.has("prefijo") && !consObj.get("prefijo").isJsonNull()) {
                            prefijo = consObj.get("prefijo").getAsString();
                        }
                    } else if (consElem.isJsonPrimitive()) {
                        consecutivo = consElem.getAsString();
                    }
                }
            }

            // 🛠️ Fallback si consecutivo no se pudo obtener de atributos
            if ((consecutivo == null || consecutivo.equals("N/A") || consecutivo.isBlank())) {
                String consecutivoBD = FinByTipoMovimientoUseCase.consultarConsecutivo(recibo.getNumero());
                if (consecutivoBD != null && !consecutivoBD.isBlank()) {
                    consecutivo = consecutivoBD;
                }
            }

            // 🎯 REGLA FINAL: si es remisión → solo consecutivo, si es FE → prefijo + consecutivo
            if (esRemision || prefijo.isBlank()) {
                prefijo = "";
            } else {
                prefijo = prefijo + "-";
            }

            // Producto y medida
            String producto = recibo.getProducto();
            producto = producto.replace("GASOLINA ", "").replace("OXIGENADA", "");
            String unidadMedida = "GL";
            if (atributos != null && atributos.has("medida") && !atributos.get("medida").isJsonNull()) {
                String medida = atributos.get("medida").getAsString();
                if (!"UNDEFINED".equalsIgnoreCase(medida)) {
                    unidadMedida = medida.toUpperCase();
                }
            }

            defaultModel.addRow(new Object[]{
                    proceso,
                    recibo.getNumero(),
                    numeroDatatafono,
                    prefijo + consecutivo,
                    sdf2.format(recibo.getFecha()),
                    producto,
                    estado,
                    (Integer.parseInt(recibo.getCara()) > 0) ? (recibo.getCara() + "") : "",
                    String.format("%.3f", recibo.getCantidadFactor()) + " " + unidadMedida,
                    "$ " + df.format(recibo.getTotal())
            });
        }

        jTable3.clearSelection();
        SelectRenderRow color1 = new SelectRenderRow();
        color1.setDatos(0, 0, new Color(255, 182, 0), new Color(255, 255, 255));
        jTable3.setDefaultRenderer(Object.class, color1);
    }

    // private void ventasSinResolver() {
    //     DefaultTableModel dm = (DefaultTableModel) jTable3.getModel();
    //     jTable3.getColumnModel().getColumn(2).setPreferredWidth(110);
    //     dm.getDataVector().removeAllElements();
    //     dm.fireTableDataChanged();
    //     DefaultTableModel defaultModel = (DefaultTableModel) jTable3.getModel();

    //     MovimientosDao dao = new MovimientosDao(); // 👈 para fallback del consecutivo

    //     for (Recibo recibo : listaVentasSinResolver) {
    //         String consecutivo = "N/A";
    //         String prefijo = "";
    //         String proceso = "";
    //         String estado = "";
    //         long numeroDatatafono = 0L;

    //         JsonObject atributos = recibo.getAtributos();
    //         boolean esRemision = false;

    //         if (atributos != null) {
    //             if (atributos.has("tipoVenta") && !atributos.get("tipoVenta").isJsonNull()) {
    //                 esRemision = atributos.get("tipoVenta").getAsInt() == 100;
    //             }

    //             if (atributos.has("proceso") && !atributos.get("proceso").isJsonNull()) {
    //                 proceso = atributos.get("proceso").getAsString();

    //             }

    //             if (atributos.has("descripcionTransaccionEstadoDatafono") && !atributos.get("descripcionTransaccionEstadoDatafono").isJsonNull()) {
    //                 estado = atributos.get("descripcionTransaccionEstadoDatafono").getAsString();
    //             }

    //             if (atributos.has("idTransaccionDatafono") && !atributos.get("idTransaccionDatafono").isJsonNull()) {
    //                 numeroDatatafono = atributos.get("idTransaccionDatafono").getAsLong();
    //             }

    //             if (atributos.has("consecutivo") && !atributos.get("consecutivo").isJsonNull()) {
    //                 JsonElement consElem = atributos.get("consecutivo");
    //                 if (consElem.isJsonObject()) {
    //                     JsonObject consObj = consElem.getAsJsonObject();
    //                     if (consObj.has("consecutivo_actual") && !consObj.get("consecutivo_actual").isJsonNull()) {
    //                         consecutivo = consObj.get("consecutivo_actual").getAsString();
    //                     }
    //                     if (consObj.has("prefijo") && !consObj.get("prefijo").isJsonNull()) {
    //                         prefijo = consObj.get("prefijo").getAsString();
    //                     }
    //                 } else if (consElem.isJsonPrimitive()) {
    //                     consecutivo = consElem.getAsString();
    //                 }
    //             }
    //         }

    //         // 🛠️ Fallback si consecutivo no se pudo obtener de atributos
    //         if ((consecutivo == null || consecutivo.equals("N/A") || consecutivo.isBlank())) {
    //             String consecutivoBD = dao.consultarConsecutivoPorId(recibo.getNumero());
    //             if (consecutivoBD != null && !consecutivoBD.isBlank()) {
    //                 consecutivo = consecutivoBD;
    //             }
    //         }

    //         // 🎯 REGLA FINAL: si es remisión → solo consecutivo, si es FE → prefijo + consecutivo
    //         if (esRemision || prefijo.isBlank()) {
    //             prefijo = "";
    //         } else {
    //             prefijo = prefijo + "-";
    //         }

    //         // Producto y medida
    //         String producto = recibo.getProducto();
    //         producto = producto.replace("GASOLINA ", "").replace("OXIGENADA", "");
    //         String unidadMedida = "GL";
    //         if (atributos != null && atributos.has("medida") && !atributos.get("medida").isJsonNull()) {
    //             String medida = atributos.get("medida").getAsString();
    //             if (!"UNDEFINED".equalsIgnoreCase(medida)) {
    //                 unidadMedida = medida.toUpperCase();
    //             }
    //         }

    //         defaultModel.addRow(new Object[]{
    //                 proceso,
    //                 recibo.getNumero(),
    //                 numeroDatatafono,
    //                 prefijo + consecutivo,
    //                 sdf2.format(recibo.getFecha()),
    //                 producto,
    //                 estado,
    //                 (Integer.parseInt(recibo.getCara()) > 0) ? (recibo.getCara() + "") : "",
    //                 String.format("%.3f", recibo.getCantidadFactor()) + " " + unidadMedida,
    //                 "$ " + df.format(recibo.getTotal())
    //         });
    //     }

    //     jTable3.clearSelection();
    //     SelectRenderRow color1 = new SelectRenderRow();
    //     color1.setDatos(0, 0, new Color(255, 182, 0), new Color(255, 255, 255));
    //     jTable3.setDefaultRenderer(Object.class, color1);
    // }




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
        System.out.println("[DEBUG] Buscando si el movimiento " + idMovimiento + " es especial.");
        for (Recibo mov : this.lista) {
            if (idMovimiento == mov.getNumero()) {
                System.out.println("[DEBUG] Movimiento encontrado: " + mov.getNumero());
                JsonObject atributos = mov.getAtributos();
                
                if (atributos != null && atributos.has("tipoVenta")) {
                    long tipoVenta = atributos.get("tipoVenta").getAsLong();
                    System.out.println("[DEBUG] tipoVenta encontrado: " + tipoVenta);
                    if (tipoVenta == 100) {
                        System.out.println("[DEBUG] Movimiento ES especial.");
                        return true;
                    }
                }
                // Si no se encuentra o no es 100, se sale del bucle para este movimiento
                break; 
            }
        }
        
        System.out.println("[DEBUG] Movimiento NO es especial.");
        return false;
    }




    boolean isCalibracion(long idMovimiento) {
        boolean isEspecial = false;
        for (Recibo mov : this.lista) {
            if (idMovimiento == mov.getNumero()) {
                if (mov.getAtributos() != null && (mov.getAtributos().get("is_especial") != null
                        && !mov.getAtributos().get("is_especial").isJsonNull()
                        && mov.getAtributos().get("is_especial").getAsBoolean() || mov.getAtributos().get("tipo") != null && mov.getAtributos().get("tipo").getAsString().equals("014"))) {
                    isEspecial = true;
                    break;
                }
            }
        }
        return isEspecial;
    }

    private void seleccionar() {
        devolucion = 0;
        validarTipoVenta = 0;
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            NovusUtils.beep();
            long value = (long) jTable1.getValueAt(r, 1);
            id = (long) jTable1.getValueAt(r, 1);
            validarVenta(id);

            for (ReciboExtended recibo : lista) {
                if (recibo.getNumero() == value) {
                    reciboFidelizar = recibo;
                    notaP = recibo.getAtributos();
                    
                    // --- DEBUG TEMPORAL ---
                    System.out.println("--- DEBUG BOTÓN F.ELECTRÓNICA ---");
                    System.out.println("Venta seleccionada: " + reciboFidelizar.getNumero());
                    if (notaP.has("isElectronica")) {
                        System.out.println("isElectronica: " + notaP.get("isElectronica").getAsBoolean());
                    } else {
                        System.out.println("isElectronica: ATRIBUTO NO PRESENTE");
                    }
                    if (reciboFidelizar.getAtributos().has("isCredito")) {
                        System.out.println("isCredito: " + reciboFidelizar.getAtributos().get("isCredito").getAsBoolean());
                    } else {
                        System.out.println("isCredito: ATRIBUTO NO PRESENTE");
                    }
                    if (notaP.has("tipoVenta")) {
                        System.out.println("tipoVenta: " + notaP.get("tipoVenta").getAsLong());
                    } else {
                        System.out.println("tipoVenta: ATRIBUTO NO PRESENTE");
                    }
                    System.out.println("isCalibracion: " + isCalibracion(reciboFidelizar.getNumero()));
                    System.out.println("------------------------------------");
                    // --- FIN DEBUG TEMPORAL ---
                    
                    notaP.addProperty("id", value);
                    if (aplicaFE) {
                        jFactura.setVisible(true);
                        if (!notaP.get("isElectronica").getAsBoolean()) {
                            jFactura.setVisible(true);
                            botonActivo(jFactura);
                            // ✅ Solo activar RECUPERAR si no es venta especial
                            if (!notaP.has("tipoVenta") || notaP.get("tipoVenta").getAsLong() != 100) {
                                botonActivo(jRecuperacion);
                            } else {
                                botonInactivo(jRecuperacion);
                            }
                        } else {
                            botonInactivo(jFactura);
                            botonInactivo(jRecuperacion);
                        }
                    } else if (findByParameterUseCase.execute() && notaP.has("tipoVenta") && notaP.get("tipoVenta").getAsLong() != 100 && !notaP.get("isElectronica").getAsBoolean()) {
                        jFactura.setVisible(true);
                        botonActivo(jFactura);
                        // ✅ Ya está validado: tipoVenta != 100 && !isElectronica
                        botonActivo(jRecuperacion);
                    } else {
                        // ✅ CAMBIO: Solo activar RECUPERAR para ventas normales (no electrónicas, no especiales)
                        // Verificar que sea una venta recuperable antes de activar
                        if (!notaP.get("isElectronica").getAsBoolean() && 
                            (!notaP.has("tipoVenta") || notaP.get("tipoVenta").getAsLong() != 100)) {
                            botonActivo(jRecuperacion);
                        } else {
                            botonInactivo(jRecuperacion);
                        }
                        botonInactivo(jFactura);
                    }
                    break;
                }
            }
            
            // Verificar si la venta está en cola de impresión
            String reportType = "CONSULTAR_VENTAS";
            if (existeEnColaPendiente(value, reportType)) {
                // Si está en cola, mostrar botón bloqueado con texto IMPRIMIENDO...
                bloquearBotonImprimir();
            } else {
                // Si no está en cola, mostrar botón normal
                desbloquearBotonImprimir();
                impresoraActivo(jImprimir);
            }
            
            botonActivo(jFideliza);

            boolean credito = reciboFidelizar.getAtributos() != null && 
                              reciboFidelizar.getAtributos().has("isCredito") && 
                              reciboFidelizar.getAtributos().get("isCredito").getAsBoolean();

            long tipoVenta = notaP.get("tipoVenta").getAsLong();

            if (credito) {
                botonInactivo(jFactura);
                botonInactivo(jRecuperacion);
                botonActivo(jFideliza);
                botonInactivo(jMedio);
            } else {
                if (!isFE(value) && !isEspecial(value)) {
                    botonActivo(jMedio);
                } else if (isEspecial(value)) {
                    botonInactivo(jMedio);
                    botonInactivo(jRecuperacion);
                } else {
                    botonInactivo(jMedio);
                }
                if (notaP.has("tipoVenta") && notaP.get("tipoVenta").getAsLong() == 100) {
                    botonInactivo(jMedio);
                }
                if (isFE(value) && !isEspecial(value)) {
                    botonInactivo(jMedio);
                    botonActivo(jFideliza);
                    botonInactivo(jRecuperacion);
                }
            }

            JsonObject atributos = reciboFidelizar.getAtributos();
            boolean esElectronica = atributos.has("isElectronica") && atributos.get("isElectronica").getAsBoolean();
            boolean pendienteAsignacion = atributos.has("pendienteAsignacionCliente") && atributos.get("pendienteAsignacionCliente").getAsBoolean();

            if (esElectronica && !pendienteAsignacion) {
                devolucion = 1;
                botonActivo(jAnular);
            } else {
                botonInactivo(jAnular);
            }
            if (isCalibracion(value)) {
                botonInactivo(jFactura);
            }
            if (atributos.has("pendienteAsignacionCliente") && !atributos.get("pendienteAsignacionCliente").getAsBoolean()) {
                // No hacer nada si ya está asignado
            } else if (atributos.has("extraData") && atributos.get("extraData").isJsonObject()) {
                JsonObject extraData = atributos.get("extraData").getAsJsonObject();
                if (extraData.has("tipoVenta") && !extraData.get("tipoVenta").isJsonNull()) {
                    validarTipoVenta = extraData.get("tipoVenta").getAsInt();
                }
            }

            if (tipoVenta == 100) {
                botonInactivo(jMedio);
                botonInactivo(jAnular);
                botonInactivo(jFactura);
                botonInactivo(jRecuperacion);
            }
            if (reciboFidelizar.getAtributos() != null && 
                reciboFidelizar.getAtributos().has("es_comunidades") && 
                reciboFidelizar.getAtributos().get("es_comunidades").getAsBoolean()) {
                botonInactivo(jRecuperacion);
            }

            if (reciboFidelizar.getFamiliaId() == 7) {
                botonInactivo(jFideliza);
            }

            // 🔍 VALIDACIÓN GLP: Desactivar botón Fidelizar si es venta GLP
            if (esVentaGLP(reciboFidelizar)) {
                NovusUtils.printLn("🚫 [SELECCIONAR] Venta GLP detectada - Botón Fidelizar desactivado");
                botonInactivo(jFideliza);
            }

        } else {
            impresoraInactivo(jImprimir);
            botonInactivo(jMedio);
            botonInactivo(jFactura);
            botonInactivo(jFideliza);
            botonInactivo(jRecuperacion);
        }

    }

    public void botonFidelizacionVentaEspecial(long idMovimiento) {
        if (GetIsGoPassMovimientoUseCase.isPagoGopass(idMovimiento)) {
            botonActivo(jFideliza);
        } else {
            botonInactivo(jFideliza);
        }
    }

    private void seleccionarVentasSinresolver() {
        devolucion = 0;
        validarTipoVenta = 0;
        int r = jTable3.getSelectedRow();

        if (r >= 0) {
            NovusUtils.beep();
            long value = (long) jTable3.getValueAt(r, 1);
            id = value;
            long codigoAutorizacion = (long) jTable3.getValueAt(r, 2);
            System.out.println("este es el codigo de autorizacion " + codigoAutorizacion);

            validarVenta(id);
            ReciboExtended recibofidelizarLocal = null;

            for (ReciboExtended recibo : listaVentasSinResolver) {
                notaP = recibo.getAtributos();

                if (codigoAutorizacion > 0) {
                    if (recibo.getAtributos() != null &&
                            recibo.getAtributos().has("idTransaccionDatafono") &&
                            !recibo.getAtributos().get("idTransaccionDatafono").isJsonNull() &&
                            codigoAutorizacion == recibo.getAtributos().get("idTransaccionDatafono").getAsLong()) {
                        recibofidelizarLocal = recibo;
                        reciboFidelizar = recibo;
                        break;
                    }
                } else {
                    if (recibo.getNumero() == value) {
                        recibofidelizarLocal = recibo;
                        reciboFidelizar = recibo;
                        break;
                    }
                }
            }

            if (recibofidelizarLocal == null) {
                System.err.println("⚠ No se encontró recibo para la venta seleccionada. ID: " + value + ", Cod.Aut: " + codigoAutorizacion);
                return;
            }

            System.out.println("→ validando atributos de recibofidelizarLocal");
            System.out.println("atributos: " + (recibofidelizarLocal.getAtributos() != null ? 
                                               recibofidelizarLocal.getAtributos().toString() : "null"));

            botonInactivoFinalizar(btnFinalizar);
            impresoraActivo(jImprimirSinResolver);

            if (recibofidelizarLocal.getAtributos() != null &&
                    recibofidelizarLocal.getAtributos().has("isElectronica") &&
                    recibofidelizarLocal.getAtributos().get("isElectronica").getAsBoolean() &&
                    recibofidelizarLocal.getAtributos().has("pendienteAsignacionCliente") &&
                    !recibofidelizarLocal.getAtributos().get("pendienteAsignacionCliente").getAsBoolean()) {

                botonInactivo(jAsignarDatosSinResolver);
                botonInactivo(btEnviarPagoPendiente);

            } else {
                if (recibofidelizarLocal.getAtributos() != null &&
                        recibofidelizarLocal.getAtributos().has("proceso") &&
                        !recibofidelizarLocal.getAtributos().get("proceso").getAsString().equals("APP TERPEL")) {
                    botonActivo(jAsignarDatosSinResolver);
                }
                botonInactivo(btEnviarPagoPendiente);
            }

            if (recibofidelizarLocal.getAtributos() == null ||
                    !recibofidelizarLocal.getAtributos().has("isElectronica") ||
                    !recibofidelizarLocal.getAtributos().get("isElectronica").getAsBoolean()) {
                botonActivo(jMedioSinResolver);
            } else {
                botonInactivo(jMedioSinResolver);
            }

            if (recibofidelizarLocal.getAtributos() != null &&
                    recibofidelizarLocal.getAtributos().has("pendienteAsignacionCliente") &&
                    !recibofidelizarLocal.getAtributos().get("pendienteAsignacionCliente").getAsBoolean()) {
                botonInactivo(jAsignarDatosSinResolver);
                botonInactivo(btEnviarPagoPendiente);
            } else {
                if (recibofidelizarLocal.getAtributos() != null &&
                        recibofidelizarLocal.getAtributos().has("proceso") &&
                        !recibofidelizarLocal.getAtributos().get("proceso").getAsString().equals("APP TERPEL")) {
                    botonActivo(jAsignarDatosSinResolver);
                }

                if (recibofidelizarLocal.getAtributos() != null &&
                        recibofidelizarLocal.getAtributos().has("extraData") &&
                        recibofidelizarLocal.getAtributos().get("extraData").isJsonObject()) {
                    JsonObject extra = recibofidelizarLocal.getAtributos().get("extraData").getAsJsonObject();
                    if (extra.has("tipoVenta") && !extra.get("tipoVenta").isJsonNull()) {
                        validarTipoVenta = extra.get("tipoVenta").getAsInt();
                    }
                }
            }

            if (notaP.has("tipoVenta") && !notaP.get("tipoVenta").isJsonNull() && notaP.get("tipoVenta").getAsLong() == 100) {
                botonInactivo(jMedioSinResolver);
                botonInactivo(btEnviarPagoPendiente);
            }

            if (recibofidelizarLocal.getAtributos() != null &&
                    recibofidelizarLocal.getAtributos().has("idTransaccionDatafonoVenta") &&
                    !recibofidelizarLocal.getAtributos().get("idTransaccionDatafonoVenta").isJsonNull()) {

                boolean ventaPendiente = activarVentaPendiente(
                        recibofidelizarLocal.getAtributos().get("idTransaccionDatafonoVenta").getAsLong());

                if (ventaPendiente) {
                    botonActivo(btEnviarPagoPendiente);
                    idTransacciondatafono = recibofidelizarLocal.getAtributos().get("idTransaccionDatafonoVenta").getAsLong();
                    impresoraInactivo(jImprimirSinResolver);
                    botonInactivo(jMedioSinResolver);
                    botonInactivo(jAsignarDatosSinResolver);
                }
            }

            // 🔒 Versión actual solo activa si existe "pendienteAsignacionAdBlue = true"
            if (reciboFidelizar.getAtributos() != null &&
                    reciboFidelizar.getAtributos().has("pendienteAsignacionAdBlue") &&
                    !reciboFidelizar.getAtributos().get("pendienteAsignacionAdBlue").isJsonNull() &&
                    reciboFidelizar.getAtributos().get("pendienteAsignacionAdBlue").getAsBoolean()) {

                botonSeleccionadoFinalizar(btnFinalizar);
                botonInactivo(jMedioSinResolver);
                impresoraInactivo(jImprimirSinResolver);

            // ✅ NUEVA CONDICIÓN adicional basada en "proceso": "UREA"
            } else if (reciboFidelizar.getAtributos() != null &&
                    reciboFidelizar.getAtributos().has("proceso") &&
                    "UREA".equalsIgnoreCase(reciboFidelizar.getAtributos().get("proceso").getAsString())) {

                botonSeleccionadoFinalizar(btnFinalizar);
                botonInactivo(jMedioSinResolver);
                impresoraInactivo(jImprimirSinResolver);

                // ⛔️ Desactivar también "Asignar Datos" si es venta UREA
                botonInactivo(jAsignarDatosSinResolver);

                System.out.println("✔ Activado botón por proceso UREA y desactivado botón ASIGNAR DATOS");
            }


        } else {
            impresoraInactivo(jImprimirSinResolver);
        }
    }


//    private void seleccionarVentasSinresolver() {
//        devolucion = 0;
//        validarTipoVenta = 0;
//        int r = jTable3.getSelectedRow();
//        if (r >= 0) {
//            NovusUtils.beep();
//            long value = (long) jTable3.getValueAt(r, 1);
//            id = (long) jTable3.getValueAt(r, 1);
//            long codigoAutorizacion = (long) jTable3.getValueAt(r, 2);
//            System.out.println("este es el codigo de autorizacion " + codigoAutorizacion);
//            validarVenta(id);
//            ReciboExtended recibofidelizarLocal = null;
//            for (ReciboExtended recibo : listaVentasSinResolver) {
//                notaP = recibo.getAtributos();
//                if (codigoAutorizacion > 0) {
//                    if (recibo.getAtributos().has("idTransaccionDatafono") &&
//                            codigoAutorizacion == recibo.getAtributos().get("idTransaccionDatafono").getAsLong()) {
//                        recibofidelizarLocal = recibo;
//                        reciboFidelizar = recibo;
//                        break;
//                    }
//                } else {
//                    if (recibo.getNumero() == value) {
//                        recibofidelizarLocal = recibo;
//                        reciboFidelizar = recibo;
//                        break;
//                    }
//                }
//            }
////            for (ReciboExtended recibo : listaVentasSinResolver) {
////                notaP = recibo.getAtributos();
////                if (codigoAutorizacion > 0) {
////                    if (codigoAutorizacion == recibo.getAtributos().get("idTransaccionDatafono").getAsLong()) {
////                        recibofidelizarLocal = recibo;
////                        System.out.println("este es el codigo de autorizacion 2 " + codigoAutorizacion);
////                        reciboFidelizar = recibo;
////                        break;
////                    }
////                } else {
////                    if (recibo.getNumero() == value) {
////                        recibofidelizarLocal = recibo;
////                        reciboFidelizar = recibo;
////                        break;
////                    }
////                }
////            }
//            if (recibofidelizarLocal == null) {
//                System.err.println("⚠ No se encontró recibo para la venta seleccionada. ID: " + value + ", Cod.Aut: " + codigoAutorizacion);
//                return; // Evita continuar y lanzar NullPointer
//            }
//
//            botonInactivoFinalizar(btnFinalizar);
//            System.out.println("→ validando atributos de recibofidelizarLocal");
//            JsonObject atributos = recibofidelizarLocal.getAtributos();
//            System.out.println("atributos: " + atributos);
//            impresoraActivo(jImprimirSinResolver);
//            if (recibofidelizarLocal.getAtributos().get("isElectronica").getAsBoolean() && !recibofidelizarLocal.getAtributos().get("pendienteAsignacionCliente").getAsBoolean()) {
//                botonInactivo(jAsignarDatosSinResolver);
//                botonInactivo(btEnviarPagoPendiente);
//            } else {
//                if (!recibofidelizarLocal.getAtributos().get("proceso").getAsString().equals("APP TERPEL")) {
//                    botonActivo(jAsignarDatosSinResolver);
//                }
//                botonInactivo(btEnviarPagoPendiente);
//            }
//            if (!recibofidelizarLocal.getAtributos().get("isElectronica").getAsBoolean()) {
//                botonActivo(jMedioSinResolver);
//                botonInactivo(btEnviarPagoPendiente);
//            } else {
//                botonInactivo(jMedioSinResolver);
//                botonInactivo(btEnviarPagoPendiente);
//            }
//
//            if (!recibofidelizarLocal.getAtributos().get("pendienteAsignacionCliente").getAsBoolean()) {
//                botonInactivo(jAsignarDatosSinResolver);
//                botonInactivo(btEnviarPagoPendiente);
//            } else {
//                if (!recibofidelizarLocal.getAtributos().get("proceso").getAsString().equals("APP TERPEL")) {
//                    botonActivo(jAsignarDatosSinResolver);
//                }
//                if (recibofidelizarLocal.getAtributos().get("extraData") != null && recibofidelizarLocal.getAtributos().get("extraData").getAsJsonObject().has("tipoVenta")) {
//                    validarTipoVenta = !recibofidelizarLocal.getAtributos().get("extraData").getAsJsonObject().get("tipoVenta").isJsonNull() ? recibofidelizarLocal.getAtributos().get("extraData").getAsJsonObject().get("tipoVenta").getAsInt() : 0;
//                }
//            }
//            if (notaP.has("tipoVenta") && notaP.get("tipoVenta").getAsLong() == 100) {
//                botonInactivo(jMedioSinResolver);
//                botonInactivo(btEnviarPagoPendiente);
//            }
//            boolean ventaPendiente = activarVentaPendiente(recibofidelizarLocal.getAtributos().get("idTransaccionDatafonoVenta").getAsLong());
//            if (ventaPendiente) {
//                botonActivo(btEnviarPagoPendiente);
//                idTransacciondatafono = recibofidelizarLocal.getAtributos().get("idTransaccionDatafonoVenta").getAsLong();
//                impresoraInactivo(jImprimirSinResolver);
//                botonInactivo(jMedioSinResolver);
//                botonInactivo(jAsignarDatosSinResolver);
//            }
//            if (reciboFidelizar.getAtributos().get("pendienteAsignacionAdBlue").getAsBoolean()) {
//                botonSeleccionadoFinalizar(btnFinalizar);
//                botonInactivo(jMedioSinResolver);
//                impresoraInactivo(jImprimirSinResolver);
//            }
//        } else {
//            impresoraInactivo(jImprimirSinResolver);
//        }
//
//    }

    private JsonObject buildJsonLiberacion() {
        JsonObject json = new JsonObject();

        JsonObject data = reciboFidelizar.getAtributos()
                .get("extraData").getAsJsonObject()
                .get("response").getAsJsonObject()
                .get("data").getAsJsonObject();

        json.addProperty("movimientoId", reciboFidelizar.getNumero());
        json.addProperty("identificadorAprobacion",
                data.get("identificadorAprobacion").getAsString());
        json.addProperty("identificadorAutorizacionEDS",
                data.get("identificadorAutorizacionEDS").getAsString());
        return json;
    }

    boolean activarVentaPendiente(long idventaDatafono) {
        //boolean ventaEstado = mdao.validarVentaPendienteDatafono(idventaDatafono);
        boolean ventaEstado = new ValidarVentaPendienteDatafonoUseCase(idventaDatafono).execute();
        return ventaEstado;
    }

    boolean existeFidelizacion(Long idVenta) {
        //return mdao.existeFidelizacion(idVenta);
        return new ExisteFidelizacionUseCase(idVenta).execute();
    }

    
    private void validarVenta(long id) {
        if (FinByTransaccionProcesoUseCase.isValidEdit(id) && !NovusConstante.HAY_INTERNET) {
            jFideliza.setText("EDITAR");
        } else {
            jFideliza.setText("FIDELIZAR");
        }
    }

    // private void validarVenta(long id) {
    //     if (mdao.isValidEdit(id) && !NovusConstante.HAY_INTERNET) {
    //         jFideliza.setText("EDITAR");
    //     } else {
    //         jFideliza.setText("FIDELIZAR");
    //     }
    // }

    void cargarPromotores() {

        jComboPromotor.removeAllItems();

        PersonaBean p = new PersonaBean();
        p.setNombre("TODOS");
        p.setId(0);

        jComboPromotor.addItem(p);
        for (PersonaBean persona : InfoViewController.turnosPersonas) {
            jComboPromotor.addItem(persona);
            if (Main.persona.getId() == persona.getId()) {
                jComboPromotor.setSelectedItem(persona);
                Async(() -> {
                    refrescarDatos("PROMOTOR CARGADO");
                });
            }
        }

    }

    private void cambiarPago(JTable table, ArrayList<ReciboExtended> listaData) {
        DatafonosDao datafonosDao = new DatafonosDao();
        int r = table.getSelectedRow();
        this.parent.recargarPersona();
        if (r > -1) {
            MovimientosBean movimiento = new MovimientosBean();
            long value = (long) table.getValueAt(r, 1);
            boolean hayAnulacionEnCurso = new HayAnulacionesPendientesUseCase(value).execute();
            boolean hayAppterpelRechazado = SingletonMedioPago.ConetextDependecy.getValidateIfDeleteAppTerpelPayment().execute(value);
            boolean hayAppterpelAprobado = SingletonMedioPago.ConetextDependecy.getValidateIsAppTerpelPaymentProcessed().execute(value);
            //if (mdao.isPendienteTransaccionMovimiento(value) || hayAnulacionEnCurso) {
            if (new IsPendienteTransaccionUseCase(value).execute() || hayAnulacionEnCurso) {
                Runnable accion = () -> mostrarMenuPrincipal(true);
                String mensaje = hayAnulacionEnCurso ? "ANULACION EN PROCESO, POR FAVOR ESPERE" : "TRANSACCION EN PROCESO, POR FAVOR ESPERE";
                showMessage("<html><center>".concat(mensaje).concat("</center></html>"),
                        "/com/firefuel/resources/alert.gif",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else if (!hayAppterpelAprobado && !hayAppterpelRechazado) {
                Runnable accion = () -> mostrarMenuPrincipal(true);
                String mensaje = hayAnulacionEnCurso ? "ANULACION EN PROCESO, POR FAVOR ESPERE" : "TRANSACCION EN PROCESO, POR FAVOR ESPERE";
                showMessage("<html><center>".concat(mensaje).concat("</center></html>"),
                        "/com/firefuel/resources/alert.gif",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                if (!isFE(value) && !isEspecial(value)) {
                    for (Recibo mov : listaData) {
                        if (value == mov.getNumero()) {
                            movimiento.setId(mov.getNumero());
                            movimiento.setVentaTotal(mov.getTotal());
                            movimiento.setAtributos(mov.getAtributos());
                            break;
                        }
                    }
                }
                boolean response;
                response = FinByMedioPagoForJoradaUseCase.validarTurnoMedioPago(movimiento.getId());
                if (response) {
                    refrescar = () -> {
                        vistaretorno();
                        refrescarDatos("BOTON REFRESCAR");
                        table.revalidate();
                        table.clearSelection();
                        refrescar = null;
                        InfoViewController.NotificadorVentasHistorial = new Notificador() {
                            @Override
                            public void send(JsonObject data) {
                                mostrarPanelMensaje(data.get("mensaje").getAsString(), data, true);
                            }
                        };
                    };
                    Runnable accion = () -> mostrarMenuPrincipal(true);
                    MedioPagosConfirmarViewController ctd = new MedioPagosConfirmarViewController(this.parent, true,
                            movimiento, false, false, reciboFidelizar, accion, refrescar);
                    InfoViewController.NotificadorVentasHistorial = null;
                    showDialog(ctd);
                } else {
                    Runnable accion = () -> mostrarMenuPrincipal(true);
                    showMessage("NO SE PUEDE EDITAR MEDIO PAGO CON TURNO CERRADO",
                            "/com/firefuel/resources/btBad.png",
                            true, accion,
                            false, LetterCase.FIRST_UPPER_CASE);
                }
            }
        }
    }

    private void selectFE() {
        int r = jTable1.getSelectedRow();
        this.parent.recargarPersona();
        if (r > -1) {
            if (Main.persona != null) {
                long value = (long) jTable1.getValueAt(r, 1);
                for (Recibo mov : this.lista) {
                    if (mov != null && value == mov.getNumero()) {
                        // ✅ LÓGICA CORRECTA: Solo convertir si NO es electrónica
                        if (!isFE(value)) {
                            NovusUtils.beep();
                            Runnable volver = () -> {
                                showPanel("pnl_principal");
                                refrescarDatos("BOTON REFRESCAR");
                            };
                            Runnable accion = () -> mostrarMenuPrincipal(true);
                            ClienteFacturaElectronica fac = new ClienteFacturaElectronica(parent, true, accion, volver);
                            fac.setMovimiento(mov);
                            fac.cambiarDato();
                            fac.convertir = true;
                            System.out.println(Main.ANSI_BLUE + notaP + Main.ANSI_RESET);
                            fac.notaP = notaP;
                            NovusUtils.printLn("abriendo la vista de facturacion electronica desde -> " + VentasHistorialView.class.getName());
                            showDialog(fac);
                        }
                        futureEjecution(() -> {
                            refrescarDatos("FACTURACION ELECTRONICA");
                        });
                        break;
                    }
                }
            }
        }
    }

    private void abrirRecuperacionVenta() {
        // ✅ VALIDACIÓN NULL-SAFE: Verificar que reciboFidelizar no sea null
        if (reciboFidelizar == null) {
            System.err.println("⚠️ ERROR: reciboFidelizar es null en abrirRecuperacionVenta()");
            Runnable accion = () -> mostrarMenuPrincipal(true);
            showMessage("ERROR: NO SE HA SELECCIONADO UNA VENTA VÁLIDA",
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    true, LetterCase.FIRST_UPPER_CASE);
            return;
        }
        
        System.out.println(reciboFidelizar.getAtributos());
        renderSales();
        if (!reciboFidelizar.getAtributos().get("recuperada").getAsBoolean()) {
            if (!isPagoGopass()) {
                refrescar = () -> {
                    refrescarDatos("BOTON REFRESCAR");
                };
                RecuperacionVentaView recuperacionView = RecuperacionVentaView.getInstance(parent, true,
                        reciboFidelizar, refrescar);
                Runnable accion = () -> mostrarMenuPrincipal(true);
                AutorizacionView autoView = new AutorizacionView(parent, true, recuperacionView, accion);
                showDialog(autoView);
            } else {
                Runnable accion = () -> mostrarMenuPrincipal(true);
                showMessage("NO SE PUEDE RECUPERAR VENTA DE GOPASS",
                        "/com/firefuel/resources/btBad.png",
                        true, accion,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            Runnable accion = () -> mostrarMenuPrincipal(true);
            showMessage("ESTA VENTA YA HA SIDO RECUPERADA",
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    false, LetterCase.FIRST_UPPER_CASE);
        }
    }

    public boolean isPagoGopass() {
        // ✅ VALIDACIÓN NULL-SAFE: Verificar que reciboFidelizar no sea null
        if (reciboFidelizar == null) {
            System.err.println("⚠️ ERROR: reciboFidelizar es null en isPagoGopass()");
            return false;
        }
        
        // ✅ VALIDACIÓN NULL-SAFE: Verificar que getMediosPagos() no sea null
        ArrayList<MediosPagosBean> mediosPago = reciboFidelizar.getMediosPagos();
        if (mediosPago == null) {
            System.err.println("⚠️ ERROR: mediosPago es null en isPagoGopass()");
            return false;
        }
        
        boolean result = false;
        for (MediosPagosBean medio : mediosPago) {
            if (medio != null && medio.getId() == 20004) {
                result = true;
                break;
            }
        }
        return result;
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

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void mostrarMenuPrincipal(boolean autoCerrar) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        String panel = NovusConstante.ventanaFE ? "pnl_ext" : "pnl_principal";
        layout.show(pnlPrincipal, panel);
        InfoViewController.NotificadorVentasHistorial = new Notificador() {
            @Override
            public void send(JsonObject data) {
                mostrarPanelMensaje(data.get("mensaje").getAsString(), data, autoCerrar);
            }
        };
        if (!NovusConstante.ventanaFE) {
            vistaretorno();
            refrescarDatos("REFRESCAR DATOS");
        }
    }
    //Retorno del cambio de AsyncExecutor a Thread
    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
        try {
            Thread.sleep((long) delay * 1000);
            runnable.run();
        } catch (InterruptedException e) {
            NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
            Thread.currentThread().interrupt();
        }
        }).start();
    }

    public void setTimeoutMensajeDatafono(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (ejecutarAccionDeCerrado) {
                    runnable.run();
                }
                ejecutarAccionDeCerrado = true;
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void confirmar(String text) {
        CardLayout card = (CardLayout) pnlPrincipal.getLayout();
        card.show(pnlPrincipal, "pnl_confirmacion");
        jLabel9.setText(text.toUpperCase());
    }

    public void asignarDatos() {
        try {
            DatafonosDao datafonosDao = new DatafonosDao();
            int r = jTable3.getSelectedRow();
            long value = (long) jTable3.getValueAt(r, 1);
            if (r > -1) {
                MovimientosBean movimiento = new MovimientosBean();
                if (Main.persona != null) {
                    for (Recibo recibo : this.listaVentasSinResolver) {
                        if (value == recibo.getNumero()) {
                            boolean hayAnulacionEnCurso = new HayAnulacionesPendientesUseCase(value).execute();
                            if (hayAnulacionEnCurso) {
                                Runnable accion = () -> mostrarMenuPrincipal(true);
                                showMessage("<html><center>ANULACION EN PROCESO, POR FAVOR ESPERE</center></html>",
                                        "/com/firefuel/resources/alert.gif",
                                        true, accion,
                                        true, LetterCase.FIRST_UPPER_CASE);
                            } else {
                                movimiento.setId(recibo.getNumero());
                                movimiento.setVentaTotal(recibo.getTotal());
                                movimiento.setAtributos(recibo.getAtributos());

                                if (validarTipoVenta == 3) {
                                    long idTransmision = 0;

                                    if (recibo.getAtributos() != null
                                            && recibo.getAtributos().has("idTransmision")
                                            && !recibo.getAtributos().get("idTransmision").isJsonNull()) {
                                        idTransmision = recibo.getAtributos().get("idTransmision").getAsLong();
                                    } else {
                                        System.err.println("⚠️ idTransmision no disponible en atributos del recibo #" + recibo.getNumero());
                                        // Opcional: intentar recuperarlo desde la base de datos con el número de movimiento
                                        obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(recibo.getNumero());
                                        idTransmision = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
                                    }

                                    Runnable accion = () -> mostrarMenuPrincipal(true);
                                    ClienteFacturaElectronica cliente = new ClienteFacturaElectronica(parent, true, idTransmision, true, this, accion, true);
                                    InfoViewController.NotificadorVentasHistorial = null;
                                    NovusUtils.printLn("if (validarTipoVenta == 3) abriendo la vista de facturacio electronica desde -> " + VentasHistorialView.class.getName());
                                    cliente.setVisible(true);
                                } else {
                                    refrescar = () -> {
                                        refrescarDatos("BOTON REFRESCAR");
                                    };
                                    Runnable accion = () -> mostrarMenuPrincipal(true);
                                    AsignarDatosPlacaView asignarDatos = new AsignarDatosPlacaView(parent, recibo, movimiento, reciboFidelizar, accion, refrescar);
                                    InfoViewController.NotificadorVentasHistorial = null;
                                    mostrarSubPanel(asignarDatos);
                                }
                                break;
                            }
                        }
                    }
                } else {
                    Runnable accion = () -> mostrarMenuPrincipal(true);
                    showMessage("DEBE INICIAR TURNO PARA ESTA ACCION",
                            "/com/firefuel/resources/btBad.png",
                            true, accion,
                            false, LetterCase.FIRST_UPPER_CASE);

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VentasHistorialView.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarTablaHIstorialVentas(JPanel panel, String panelVenta) {
        CardLayout cartd = (CardLayout) panel.getLayout();
        cartd.show(panel, panelVenta);
    }

    public void vistaretorno() {
        NovusUtils.printLn("Vista ventas pendientes " + estadoActulizarDatafono);
        if (estadoActulizarDatafono) {
            cargarTablaHIstorialVentas(cardVentas, "ventasSinResolver");
            cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasSinResolver");
            ventasSinResolver.setSelected(true);
            botonesToogle(ventasSinResolver, btnHIstorialVentas);
        } else {
            cargarTablaHIstorialVentas(cardVentas, "ventasHistorial");
            cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorial");
            btnHIstorialVentas.setSelected(true);
            botonesToogle(btnHIstorialVentas, ventasSinResolver);
        }
    }

    private void botonesToogle(JToggleButton butonActivo, JToggleButton butonInActivo) {
        butonActivo.setIcon(botonTabsSeleccionado);
        butonActivo.setForeground(new Color(223, 29, 20));
        butonInActivo.setIcon(botonTabsNoSeleccionado);
        butonInActivo.setForeground(new Color(167, 25, 22));
    }

    public void renderizarTablaVentasSinResolver(JTable tabla, JScrollPane escroll) {
        DefaultTableCellRenderer textRight;
        DefaultTableCellRenderer textCenter;
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        tabla.setSelectionBackground(new Color(255, 182, 0));
        tabla.setSelectionForeground(new Color(0, 0, 0));
        tabla.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        tabla.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getModel().getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            tabla.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tabla.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        tabla.setRowSorter(rowSorter);
        escroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });

        escroll.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        escroll.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        tabla.getTableHeader().setReorderingAllowed(false);
        NovusUtils.setUnsortableTable(tabla);
    }

    private void botonActivo(JLabel label) {
        label.setIcon(botonActivoIcono);
        label.setForeground(new Color(255, 255, 255));
    }

    private void botonInactivo(JLabel label) {
        label.setIcon(botonBloqueadoIcono);
        label.setForeground(new Color(102, 102, 102));
    }

    private void botonInactivoFinalizar(JLabel label) {
        label.setIcon(botonBloqueadoIconoLarge);
        label.setForeground(new Color(102, 102, 102));
    }

    private void botonSeleccionadoFinalizar(JLabel label) {
        label.setIcon(botonActivoIconoLarge);
        label.setForeground(new Color(255, 255, 255));
    }

    private void botonSeleccionado(JLabel label) {
        label.setIcon(botonBloqueadoIcono);
        label.setForeground(new Color(153, 0, 0));
    }

    private void impresoraActivo(JLabel label) {
        label.setIcon(botonActivoIconoIconoImpresora);
        label.setText("");
    }

    private void impresoraInactivo(JLabel label) {
        label.setIcon(botonBloqueadoIconoImpresora);
        label.setText("");
        // Ocultar el label de IMPRIMIENDO si existe
        if (lblImprimiendo != null) {
            lblImprimiendo.setVisible(false);
        }
        // Restaurar posición original del botón si fue movido
        if (label == jImprimir && botonImprimirBloqueado) {
            botonImprimirBloqueado = false;
            accionesHistorialVentas.remove(jImprimir);
            accionesHistorialVentas.add(jImprimir, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 24, 70, 60));
            accionesHistorialVentas.revalidate();
            accionesHistorialVentas.repaint();
        }
    }
    
    // Label para mostrar "IMPRIMIENDO..." fuera del botón
    private javax.swing.JLabel lblImprimiendo = null;
    
    /**
     * Bloquea el botón de imprimir y muestra "IMPRIMIENDO..." a la derecha del botón
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true;
        jImprimir.setIcon(botonBloqueadoIconoImpresora);
        
        // Mover el botón a la izquierda (remover y agregar con nuevas coordenadas)
        accionesHistorialVentas.remove(jImprimir);
        accionesHistorialVentas.add(jImprimir, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 24, 70, 60));
        
        // Crear label de texto si no existe
        if (lblImprimiendo == null) {
            lblImprimiendo = new javax.swing.JLabel("IMPRIMIENDO...");
            lblImprimiendo.setFont(new java.awt.Font("Terpel Sans", 1, 16));
            lblImprimiendo.setForeground(Color.WHITE);
            lblImprimiendo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            // Texto a la DERECHA del botón (botón en x=770, ancho=70, entonces texto en x=842)
            accionesHistorialVentas.add(lblImprimiendo, new org.netbeans.lib.awtextra.AbsoluteConstraints(842, 34, 150, 40));
        }
        // Mostrar el texto
        lblImprimiendo.setVisible(true);
        accionesHistorialVentas.revalidate();
        accionesHistorialVentas.repaint();
        // Forzar actualización inmediata de la UI (sin esperar al final del evento)
        accionesHistorialVentas.paintImmediately(accionesHistorialVentas.getBounds());
    }
    
    /**
     * Desbloquea el botón de imprimir y oculta el texto
     */
    private void desbloquearBotonImprimir() {
        botonImprimirBloqueado = false;
        jImprimir.setIcon(botonActivoIconoIconoImpresora);
        
        // Restaurar el botón a su posición original
        accionesHistorialVentas.remove(jImprimir);
        accionesHistorialVentas.add(jImprimir, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 24, 70, 60));
        
        // Ocultar el label de texto
        if (lblImprimiendo != null) {
            lblImprimiendo.setVisible(false);
        }
        accionesHistorialVentas.revalidate();
        accionesHistorialVentas.repaint();
        // Forzar actualización inmediata de la UI
        accionesHistorialVentas.paintImmediately(accionesHistorialVentas.getBounds());
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
                    JsonArray registros = JsonParser.parseString(content.toString()).getAsJsonArray();
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
     * @param id El ID del movimiento
     * @param reportType El tipo de reporte (VENTA, FACTURA, etc.)
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
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Se llama cuando la respuesta del servicio no es exitosa (no es 200)
     * @param id El ID del movimiento a eliminar
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
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void validarIsPagoDatafonoCompleto(long idMovimiento) {

        JsonArray mediosDepago = FinByMedioDePagoMovimientoUseCase.buscarMediosPagosDatafonosCompletados(idMovimiento);
        int tamañoArray = mediosDepago.size();
        boolean isEfectivoSinValor = Boolean.FALSE;
        int contadorMediosDePagoCondatafono = 0;
        for (JsonElement element : mediosDepago) {
            JsonObject objMedios = element.getAsJsonObject();
            if (objMedios.get("idMedioPago").getAsLong() == 1 && objMedios.get("valorTotal").getAsFloat() == 0) {
                isEfectivoSinValor = Boolean.TRUE;
            }
            if (objMedios.get("pagoDatafonoAprobado").getAsBoolean()) {
                contadorMediosDePagoCondatafono++;
            }
        }
        if (isEfectivoSinValor && contadorMediosDePagoCondatafono >= 1) {
            botonInactivo(jMedio);
        } else {
            boolean credito = reciboFidelizar.getAtributos().get("isCredito").getAsBoolean();
            if (notaP.get("tipoVenta").getAsLong() != 100 && !reciboFidelizar.getAtributos().get("isElectronica").getAsBoolean() && !credito && !isEspecial(reciboFidelizar.getNumero())) {
                botonActivo(jMedio);
            } else {
                botonInactivo(jMedio);
            }

        }
    }

    private void mostrarRespuesta(JsonObject respuesta) {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "mensajesDatafono");
        Runnable runnable = () -> {
            mostrarMenuPrincipal(true);
        };
        jIcono.setIcon(loaderDatafono);
        jTitulo.setText("enviando pago".toUpperCase());
        jInfoDatafono.setText("");
        jTransacciones.setText("");
        Thread response = new Thread() {
            @Override
            public void run() {
                if (respuesta.get("estado").getAsInt() == 200) {
                    jIcono.setIcon(okDatafono);
                    jTitulo.setText(respuesta.get("mensaje").getAsString());
                    jInfoDatafono.setText("");
                    jTransacciones.setText("");
                    setTimeout(5, runnable);
                } else {
                    jIcono.setIcon(errorDatafono);
                    jTitulo.setText(respuesta.get("mensaje").getAsString());
                    jInfoDatafono.setText("");
                    jTransacciones.setText("");
                    setTimeout(5, runnable);
                }

            }
        };
        response.start();
    }
    
    /**
     * 🔄 MIGRADO A SERVICIO PYTHON
     * Impresión de venta desde historial de ventas
     * Verifica primero el estado del servicio antes de imprimir
     * @version 2.2 - Con health check previo
     */
    public void imprimirVenta(long movimientoId) {
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║  🐍 SERVICIO PYTHON - HISTORIAL VENTAS                   ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        NovusUtils.printLn("📋 Imprimiendo venta desde historial");
        NovusUtils.printLn("   - ID Movimiento: " + movimientoId);
        
        // 1. Verificar que el servicio esté activo y saludable
        CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
        
        if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
            // Servicio no responde o no está saludable
            String mensaje = healthResult.obtenerMensajeError();
            NovusUtils.printLn("❌ Servicio de impresión no está disponible: " + mensaje);
            desbloquearBotonImprimir();
            Runnable accion = () -> mostrarMenuPrincipal(true);
            showMessage(mensaje,
                    "/com/firefuel/resources/btBad.png",
                    true, accion,
                    true, LetterCase.FIRST_UPPER_CASE);
            return;
        }
        
        // 3. Servicio activo, saludable e impresora conectada - Proceder con la impresión
        NovusUtils.printLn("✅ Servicio de impresión activo e impresora conectada - Procediendo con impresión");
        
        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json; charset=UTF-8");
        header.put("Accept", "application/json");
        
        JsonObject body = new JsonObject();
        JsonObject data = new JsonObject();
        body.addProperty("identificadorMovimiento", movimientoId);
        body.addProperty("movement_id", movimientoId);
        body.addProperty("flow_type", "CONSULTAR_VENTAS");
        body.addProperty("report_type", "FACTURA");
        body.add("body", data);
        
        NovusUtils.printLn("🌐 URL Servicio Python: " + url);
        NovusUtils.printLn("📤 Payload: " + body.toString());
        
        // Solo enviar el request sin esperar respuesta
        // ClientWSAsync extiende Thread, así que start() ya crea el hilo
        ClientWSAsync client = new ClientWSAsync("IMPRESION VENTA - HISTORIAL", url, NovusConstante.POST, body, true, false, header, 10000);
        client.start();
        
        NovusUtils.printLn("✅ Request de impresión enviado - Movimiento: " + movimientoId);
        NovusUtils.printLn("═══════════════════════════════════════════════════════════");
    }

    private void debounce(Runnable task, int delayMs) {
        if (debounceTimer != null) {
            debounceTimer.stop();
        }
        debounceTimer = new Timer(delayMs, e -> task.run());
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }

    /**
     * Verifica si una venta es de tipo GLP basándose en el producto del movimiento
     * @param recibo ReciboExtended de la venta
     * @return true si es GLP, false en caso contrario
     */
    private boolean esVentaGLP(ReciboExtended recibo) {
        try {
            // Obtener el ID del producto del movimiento
            long idProducto = GetByProductoMovimientoDetalleUseCase.obtenerProductoId(recibo.getNumero());
            
            if (idProducto > 0) {
                // Obtener la información del producto
                MovimientosDao mdao = new MovimientosDao();
                ProductoBean producto = mdao.findProductByIdActive(idProducto);
                
                if (producto != null && producto.getDescripcion() != null) {
                    String descripcionProducto = producto.getDescripcion().toUpperCase();
                    NovusUtils.printLn("🔍 [VALIDACION GLP] Producto de la venta: " + descripcionProducto);
                    
                    // Verificar si la descripción contiene "GLP"
                    return descripcionProducto.contains("GLP");
                }
            }
            
            NovusUtils.printLn("⚠️ [VALIDACION GLP] No se pudo determinar el tipo de producto para la venta: " + recibo.getNumero());
            return false;
            
        } catch (DAOException e) {
            NovusUtils.printLn("❌ [ERROR VALIDACION GLP] Error DAO al verificar tipo de producto: " + e.getMessage());
            return false;
        } catch (Exception e) {
            NovusUtils.printLn("❌ [ERROR VALIDACION GLP] Error general al verificar tipo de producto: " + e.getMessage());
            return false;
        }
    }

}

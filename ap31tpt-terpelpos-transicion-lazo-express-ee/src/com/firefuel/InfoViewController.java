package com.firefuel;

import com.WT2.appTerpel.domain.valueObject.TransaccionMessageView;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.movimientos.ObtenerPromotoresJornadaUseCase;
import com.application.useCases.consecutivos.ObtenerConsecutivoUseCase;
import com.bean.BodegaBean;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.facade.BodegasFacade;
import com.facade.ConfigurationFacade;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.utils.ImageCache;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Timer;
import javax.swing.JLabel;

public class InfoViewController extends javax.swing.JFrame {

    boolean showMenu = true;
    public final EquipoDao equipoDao;
    public final SetupDao setupDao;
    public final FacturacionElectronica facturacionElectronica;
    public static TreeMap<Integer, EstadoNotificacionesViewController> statusPumpNotificaciones = new TreeMap<>();
    public static TreeMap<Long, EstadoRfidViewController> notificacionesRfid = new TreeMap<>();
    public static TreeMap<Integer, EstadoSurtidorViewController> surtidores = new TreeMap<>();
    ObtenerPromotoresJornadaUseCase obtenerPromotoresJornadaUseCase;
    ObtenerConsecutivoUseCase obtenerConsecutivoUseCase;
    private final ScheduledExecutorService schedulerValidarConexion = Executors.newSingleThreadScheduledExecutor();

    public static ArrayList<PersonaBean> turnosPersonas = new ArrayList<>();
    public static boolean surtidorConectado = false;
    public static String ICONO_ACTUAl_RETENIDO = "";
    public static boolean hayInternet = false;
    public static int timeoutInternet = 20000;

    public static ImageIcon corriente;
    public static ImageIcon extra;
    public static ImageIcon diesel;
    public static ImageIcon gas;
    public static ImageIcon glp;

    public static ImageIcon tarjetaStatusPump;

    ///IMAGENES
    public static ImageIcon canastilla;
    public static ImageIcon clubterpel;
    public static ImageIcon icono_rumbo;
    public static ImageIcon icono_clienteMi;
    public static ImageIcon icono_conectado;
    public static ImageIcon icono_no_conectado;
    public static ImageIcon icono_surtidor_no_conectado;
    public static ImageIcon icono_surtidor_conectado;
    public static ImageIcon icono_surtidor_salto;
    public static ImageIcon icono_bloqueo_surtidor;
    public static ImageIcon icono_ventas_retenidas_anim;
    public static ImageIcon icono_venta_ok;
    public static ImageIcon icono_venta_nosincroniza;
    public static ImageIcon icono_btn_izquierda1;
    public static ImageIcon icono_btn_izquierda2;
    public static ImageIcon icono_btn_derecha1;
    public static ImageIcon icono_btn_derecha2;
    public static ImageIcon icono_pos_master;
    public static ImageIcon icono_menu_arriba;
    public static ImageIcon icono_menu_abajo;
    public static ImageIcon icono_sin_turnos;
    public static ImageIcon icono_nostificaciones;
    public static ImageIcon icono_campana_notificaciones;
    public static ImageIcon icono_gif_campana_notificaciones;
    public static ImageIcon icono_gif_datafano_error;
    public static ImageIcon icono_gif_datafono_ok;
    public static ImageIcon icono_appterpel_aprobada;
    public static ImageIcon icono_appterpel_rechazada;
    public static ImageIcon icono_check;
    public static ImageIcon icono_error;

    public static ImageIcon factura;

    public static ImageIcon canastilla2;
    public static ImageIcon clubterpel2;
    public static ImageIcon factura2;

    public static Font bebas_60;
    public static Notificador NotificadorRumbo = null;
    public static Notificador NotificadorInfoView = null;
    public static Notificador NotificadorClientePropio = null;
    public static Notificador NotificadorRecuperacion = null;
    public static Notificador NotificadorVentaView = null;
    public static Notificador NotificadorVentasHistorial = null;
    public static Notificador NotificadorNotificacionesError = null;

    public static TurnosIniciarViewController instanciaInicioTurno = null;
    public static RegistroTagViewController instanciaRegistroTag = null;
    private JsonArray dataNotificacionError = new JsonArray();

    private static InfoViewController instance;
 
    
    static boolean estadoSurtidor;
    int contadorVentaManual = 0;

    public TreeMap<Long, JsonArray> aforos = new TreeMap<>();
    public static ArrayList<BodegaBean> loadedTanks = new ArrayList<>();
    private final Icon errorDatafono = icono_gif_datafano_error;
    private final Icon okDatafono = icono_gif_datafono_ok;

    private final Icon iconoOk = icono_check;
    private final Icon iconoError = icono_error;

    private final Icon iconoAppTerpelOk = icono_appterpel_aprobada;
    private final Icon iconoAppTerpelError = icono_appterpel_rechazada;
    private List<JLabel> mainMenuElementDynamic;
    private int possElementDynamic;
    private int possMementoFirstElement;

    ArrayList<BodegaBean> fetchTanks(boolean activeConsole) throws DAOException {
        Long id = this.equipoDao.findEmpresaId();
        if (activeConsole) {
            return BodegasFacade.fetchConsoleTanksMeasuresVeeder();
        } else {
            return BodegasFacade.fetchTanksMeasureTeoric(id);
        }
    }

    void setLoadedTanks(ArrayList<BodegaBean> loadedTanks) {
        InfoViewController.loadedTanks = loadedTanks;
    }

    private void setDataNotificacionesErrores(JsonArray notificaionesErrores) {
        this.dataNotificacionError = notificaionesErrores;
    }

    ArrayList<BodegaBean> getLoadedTanks() {
        return InfoViewController.loadedTanks;
    }

    public InfoViewController(FacturacionElectronica facturacionElectronica, EquipoDao equipoDao, SetupDao setupDao) {

        this.facturacionElectronica = facturacionElectronica;
        this.equipoDao = equipoDao;
        this.setupDao = setupDao;

        bebas_60 = new java.awt.Font("Bebas Neue", 1, 60);
        this.loadImagesController();
        this.initComponents();
        this.mainMenuElementDynamic = new ArrayList<>();
        InfoViewController.instance = this; // Asignación correcta
        init();
        
    }
    public static InfoViewController getInstance() {
        return InfoViewController.instance;
    }

    public InfoViewController(){
        this(new FacturacionElectronica(), new EquipoDao(), new SetupDao());
    }
    //LoadImagesController
    private void loadImagesController() {
        tarjetaStatusPump = ImageCache.getImage("/com/firefuel/resources/indicador_surtidor_tarjeta.png");

        corriente = ImageCache.getImage("/com/firefuel/resources/indicadorSurtidor_corriente.png");
        extra = ImageCache.getImage("/com/firefuel/resources/indicadorSurtidor_extra.png");
        diesel = ImageCache.getImage("/com/firefuel/resources/indicadorSurtidor_diesel.png");
        gas = ImageCache.getImage("/com/firefuel/resources/indicadorSurtidor_gas.png");
        glp = ImageCache.getImage("/com/firefuel/resources/indicadorSurtidor_glp.png");

        canastilla = ImageCache.getImage("/com/firefuel/resources/btCanastillaSmall.png");
        clubterpel = ImageCache.getImage("/com/firefuel/resources/btLifeMilesSmall.png");
        factura = ImageCache.getImage("/com/firefuel/resources/btFacturaSmall.png");

        canastilla2 = ImageCache.getImage("/com/firefuel/resources/btCanastillaSmall2.png");
        clubterpel2 = ImageCache.getImage("/com/firefuel/resources/btLifeMilesSmall2.png");
        factura2 = ImageCache.getImage("/com/firefuel/resources/btFacturaSmall2.png");

        icono_rumbo = ImageCache.getImage("/com/firefuel/resources/RumboIcon.png");
        icono_clienteMi = ImageCache.getImage("/com/firefuel/resources/placaMI.png");
        icono_conectado = ImageCache.getImage("/com/firefuel/resources/conectado.png");
        icono_no_conectado = ImageCache.getImage("/com/firefuel/resources/noconectado.png");

        icono_surtidor_conectado = ImageCache.getImage("/com/firefuel/resources/surtidoConectado.png");
        icono_surtidor_no_conectado = ImageCache.getImage("/com/firefuel/resources/surtidorNOconectado.png");
        icono_surtidor_salto = ImageCache.getImage("/com/firefuel/resources/surtidorSalto.png");
        icono_bloqueo_surtidor = ImageCache.getImage("/com/firefuel/resources/bloqueo-surtidor.png");
        icono_ventas_retenidas_anim = ImageCache.getImage("/com/firefuel/resources/venta_retenida_animacion.gif");
        icono_venta_ok = ImageCache.getImage("/com/firefuel/resources/ok.png");
        icono_venta_nosincroniza = ImageCache.getImage("/com/firefuel/resources/nosincroniza.png");
        icono_campana_notificaciones = ImageCache.getImage("/com/firefuel/resources/icono de notificaciones.png");
        icono_gif_campana_notificaciones = ImageCache.getImage("/com/firefuel/resources/ALERTA 2.gif");
        icono_check = ImageCache.getImage("/com/firefuel/resources/iconoCheck.png");
        icono_error = ImageCache.getImage("/com/firefuel/resources/iconoError.png");

        icono_btn_izquierda1 = ImageCache.getImage("/com/firefuel/resources/btn_izquierda1.png");
        icono_btn_izquierda2 = ImageCache.getImage("/com/firefuel/resources/btn_izquierda2.png");
        icono_btn_derecha1 = ImageCache.getImage("/com/firefuel/resources/btn_derecha1.png");
        icono_btn_derecha2 = ImageCache.getImage("/com/firefuel/resources/btn_derecha2.png");
        icono_pos_master = ImageCache.getImage("/com/firefuel/resources/posmasterindicador.png");
        icono_menu_arriba = ImageCache.getImage("/com/firefuel/resources/btn_menu_arriba.png");
        icono_menu_abajo = ImageCache.getImage("/com/firefuel/resources/btn_menu_abajo.png");
        icono_sin_turnos = ImageCache.getImage("/com/firefuel/resources/icono_lg_turno.png");
        icono_nostificaciones = ImageCache.getImage("/com/firefuel/resources/terpel pos alarma_Mesa de trabajo 1.png");

        icono_gif_datafano_error = ImageCache.getImage("/com/firefuel/resources/venta error.gif");
        icono_gif_datafono_ok = ImageCache.getImage("/com/firefuel/resources/venta check.gif");
        icono_appterpel_aprobada = ImageCache.getImage("/com/firefuel/resources/AppterlApproved.png");
        icono_appterpel_rechazada = ImageCache.getImage("/com/firefuel/resources/AppterpelRejected.png");
    }

    void loadView() {
        boolean aplicaFE = this.facturacionElectronica.aplicaFE();
        jmod_ventasManuales.setVisible(aplicaFE);
        jNotificaciones.setVisible(false);
        NovusUtils.ajusteFuente(this.jPanel6.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(InfoViewController.jLabel7, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(this.jLabel6, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(this.jLabel19, NovusConstante.EXTRABOLD);
        this.hidePanels();
    }

    void hidePanels() {
        InfoViewController.jnotificacionesRfid.setVisible(false);
        InfoViewController.jnotificaciones.setVisible(false);
        InfoViewController.jSubMenu.setVisible(false);
    }

    JsonArray conseguirAforoTanque(BodegaBean tanque) {
        JsonArray array = this.equipoDao.getMedidaTanque(tanque);
        return array;
    }

    public void limpiarMedidas() {
        if (loadedTanks != null) {
            for (BodegaBean tank : loadedTanks) {
                tank.setGalonTanque(0);
            }
        }
    }

    public void reloadTanks(boolean activeConsole) {
        aforos = null;
        InfoViewController.loadedTanks = new ArrayList<>();
        aforos = new TreeMap<>();
        new Thread(() -> {
            try {
                setLoadedTanks(fetchTanks(activeConsole));
                for (BodegaBean tank : InfoViewController.loadedTanks) {
                    JsonArray response = this.conseguirAforoTanque(tank);
                    aforos.put(tank.getId(), response);
                }
            } catch (Exception e) {
                System.out.println(e);
            } catch (DAOException ex) {
                Logger.getLogger(InfoViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    public void cargarLecturaAforo() {
        aforos = new TreeMap<>();
        InfoViewController.loadedTanks = new ArrayList<>();
        new Thread(() -> {
            try {
                setLoadedTanks(fetchTanks(loadIsActiveConsole()));
                for (BodegaBean tank : loadedTanks) {
                    JsonArray response = this.conseguirAforoTanque(tank);
                    aforos.put(tank.getId(), response);
                }
            } catch (Exception e) {
                System.err.println(e);
            } catch (DAOException ex) {
                Logger.getLogger(InfoViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    JsonArray loadConfigurationParams() {
        JsonArray neededParams = new JsonArray();
        neededParams.add(new JsonPrimitive(NovusConstante.PREFERENCE_VEEDER_ROOT));
        return ConfigurationFacade.fetchConfigurationParams(Utils.jsonArrayToString(neededParams));
    }

    public boolean loadIsActiveConsole() {
        boolean isActiveConsole = false;
        JsonArray configurationParamArray = this.loadConfigurationParams();
        if (configurationParamArray != null) {
            for (JsonElement element : configurationParamArray) {
                JsonObject object = element.getAsJsonObject();
                String paramCode = object.get("codigo").getAsString().trim().toUpperCase();
                String paramValue = object.get("valor").getAsString().trim().toUpperCase();
                if (paramCode.equals(NovusConstante.PREFERENCE_VEEDER_ROOT)) {
                    isActiveConsole = paramValue.equals("S");
                    break;
                }
            }
        }
        return isActiveConsole;
    }

    public void validarConexion() {
        boolean hayConexion = comprobarConexion();

        if (hayConexion != hayInternet) {
            actualizarIconoConexion(hayConexion);
            hayInternet = hayConexion;
            NovusConstante.HAY_INTERNET = hayInternet;
        }

        // Programar la siguiente validación
        new com.services.TimeOutsManager().setTimeoutUtilManager(timeoutInternet / 1000, () -> {
            validarConexion();
        });

        javax.swing.SwingUtilities.invokeLater(() -> Main.info.jInternet.revalidate());
    }

    private boolean comprobarConexion() {
        try {
            URL url = new URL("https://" + NovusConstante.HOST_INTERNET);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(timeoutInternet);
            connection.connect();
            return true;
        } catch (IOException e) {
            Logger.getLogger(InfoViewController.class.getName()).log(Level.WARNING, "Error al validar conexión: " + e.getMessage());
            return false;
        }
    }

    private void actualizarIconoConexion(boolean conectado) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (conectado) {
                Main.info.jInternet.setIcon(icono_conectado);
            } else {
                Main.info.jInternet.setIcon(icono_no_conectado);
            }
        });
    }

    static void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    void abrirMenuPreautorizar() {
        NovusUtils.beep();
        if (Main.persona == null) {
            mostrarPanelSinTurnos();
        } else {
            this.recargarPersona();
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(this,
                        NovusConstante.SELECCION_PROMOTOR_AUTORIZACION_VENTA);
                seleccionpromotor.setVisible(true);
            } else {
                VentaPredefinirPlaca instanciaPlaca = VentaPredefinirPlaca.getInstance(this, true);
                instanciaPlaca.setVisible(true);
            }
        }
        //mostrarSubPanel(new PanelMediosPago());
    }

    //nuevo stefa
    
    void abrirMenuTipoVenta() {
        NovusUtils.beep();
        if (Main.persona == null) {
            mostrarPanelSinTurnos();
        } else {
           // this.recargarPersona();
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(this,
                        NovusConstante.SELECCION_PROMOTOR_TIPO_VENTA);
                seleccionpromotor.setVisible(true);
            } else {
               // VentaPredefinirPlaca instanciaPlaca = VentaPredefinirPlaca.getInstance(this, true);
               // instanciaPlaca.setVisible(true);
              
             VentaPlacaView VentaPlaca = VentaPlacaView.getInstance(this, true);
             VentaPlaca.setVisible(true);
            }
        }
        //mostrarSubPanel(new PanelMediosPago());
    }
    
    
    void abrirMenuRumbo() {

        NovusUtils.beep();
        if (Main.persona == null) {
            mostrarPanelSinTurnos();
        } else {
            this.recargarPersona();
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(this,
                        NovusConstante.SELECCION_PROMOTOR_RUMBO);
                seleccionpromotor.setVisible(true);
            } else {
                RumboView instanciaRumbo = RumboView.getInstance(this, true);
                instanciaRumbo.setVisible(true);
            }
        }
    }

    public static void validarConexionInternet() {
        Main.info.validarConexion();
    }

    //Imagen Surtidor de salto de lecutra
    public  void saldoDeLEctura(boolean salto) {
        if (salto && Main.info != null && Main.info.jSurtidorConectado != null) {
            Main.info.jSurtidorConectado.setIcon(icono_surtidor_salto);

        } else if (Main.info != null && Main.info.jSurtidorConectado != null) {
            this.botonVentaManual();
        }

    }
    // Imagen bloqueo Manguera
    public void bloqueoManguera(boolean bloqueo) {
        if (bloqueo && jSurtidorConectado != null) {
            Main.info.jSurtidorConectado.setIcon(icono_bloqueo_surtidor);
        }
    }

    public  void renderSurtidorConexion(boolean conectado, boolean salto) {
        if (Main.info != null) {
            if (!surtidorConectado && conectado) {
                estadoSurtidor = false;
            } else if (surtidorConectado && !conectado) {
                estadoSurtidor = true;
            }
            NovusConstante.ESTADO_SURTIDOR = estadoSurtidor;
            this.botonVentaManual();
            surtidorConectado = conectado;
        }
    }
    //Ventas retenidas imagenes
    public static void renderVentasRetenidas(int numero, boolean sincronizado) {
        if (Main.info != null) {
            Icon iconoActual = Main.info.jventas_retenidas.getIcon();
            Icon iconoNuevo = null;


            if (numero == 0  || numero == -2) {
                iconoNuevo = icono_venta_ok;
            } else if (sincronizado && numero == -1) {
                iconoNuevo = icono_ventas_retenidas_anim;
                Main.info.jventas_retenidas_numero.setText("");
            } else if (!iconoActual.equals(icono_ventas_retenidas_anim) && !sincronizado) {
                iconoNuevo = icono_venta_nosincroniza;
            }


            if (numero == -1) {
                Main.info.jventas_retenidas_numero.setText("");
            } else {
                Main.info.jventas_retenidas_numero.setText(numero + "");
            }

            if (iconoNuevo != null && !iconoNuevo.equals(iconoActual)) {
                Main.info.jventas_retenidas.setIcon(iconoNuevo);

            }
        }
    }

    public static void ventasPorclientesFE(int facturacionElectronica, int datafono) {
        if (Main.info != null) {
            if (facturacionElectronica > 0 || datafono > 0) {
                Main.info.jNotificaciones.setVisible(true);
                if (facturacionElectronica > 9 || datafono > 9) {
                    Main.info.jNotificaciones.setText("Ventas en proceso F.E 9+, DAT 9+");
                } else {
                    Main.info.jNotificaciones.setText("Ventas en proceso F.E " + facturacionElectronica + ", DAT " + datafono);
                }

            } else {
                Main.info.jNotificaciones.setText("No hay ventas. " + 0);
                Main.info.jNotificaciones.setVisible(false);

            }
        }

    }

    public void errorNotificaciones(int numeroNotificaciones, JsonArray data) {
        if (NovusConstante.BASE_DE_dATOS_ACTIVA) {
            setDataNotificacionesErrores(data);
        } else {
            if (data.size() > 0) {
                setDataNotificacionesErrores(data);
            }
        }
        if (numeroNotificaciones > 0 || !NovusConstante.BASE_DE_dATOS_ACTIVA) {
            Main.info.pnlNumeroNotificaiones.setVisible(numeroNotificaciones > 0);
            Main.info.lblNumeroNotificaion.setVisible(numeroNotificaciones > 0);
            Main.info.lblNumeroNotificaion.setText(numeroNotificaciones >= 10 ? "9+" : numeroNotificaciones + "");
            Main.info.jLabel11.setIcon(icono_gif_campana_notificaciones);
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Main.info.jLabel11.setIcon(icono_campana_notificaciones);
                }
            };
            timer.schedule(task, 3000);
        } else {
            Main.info.jLabel11.setIcon(icono_campana_notificaciones);
        }
    }

    private void botonVentaManual() {
        boolean aplicaFE = this.facturacionElectronica.aplicaFE();
        if (aplicaFE) {
            if (estadoSurtidor || !NovusConstante.HAY_INTERNET) {
                Main.info.jmod_ventasManuales.setVisible(true);
            } else {
                Main.info.jmod_ventasManuales.setVisible(false);
            }
        } else {
            Main.info.jmod_ventasManuales.setVisible(false);
        }
        if (estadoSurtidor) {
            Main.info.jSurtidorConectado.setIcon(icono_surtidor_no_conectado);
        } else {
            Main.info.jSurtidorConectado.setIcon(icono_surtidor_conectado);
        }
    }

    void loadData() {
        JsonArray requeridParams = new JsonArray();
        requeridParams.add(new JsonPrimitive("HOST_LZ"));
        requeridParams.add(new JsonPrimitive("POS_ID"));
        JsonArray configurationParams = Utils.fetchConfigurationParams(requeridParams);
        this.renderInitialData(configurationParams);
        this.recargarPersona();
        if (Main.persona == null) {
            this.preguntarJornadas();
        }
        // Siempre renderizar los datos del turno al cargar
        this.renderJournalData();
    }

    void renderJournalData() {
        System.out.println("🔍 Persona Turno Activo(s): " + Main.persona);
        if (Main.persona == null) {
            this.jLabel6.setText("SIN TURNOS ACTIVOS");
        } else {
            String turnosNombres = "";
            int i = 0;
            for (PersonaBean p : turnosPersonas) {
                turnosNombres += p.getNombre().split(" ")[0];
                if (i < turnosPersonas.size() - 1) {
                    turnosNombres += " - ";
                }
                i++;
            }
            this.jLabel6.setText(turnosNombres);
        }
    }

    void renderInitialData(JsonArray configurationParams) {
        InfoViewController.jLabel7.setText(Main.credencial.getEmpresa().getAlias().toUpperCase());
        this.jLabel14.setText("Versión " + Main.VERSION_CODE);
        if (configurationParams != null) {
            for (JsonElement element : configurationParams) {
                JsonObject param = element.getAsJsonObject();
                String code = param.get("codigo").getAsString().trim();
                String value = param.get("valor").getAsString().trim().toLowerCase();
                if (code.equals("HOST_LZ") && value.equals("localhost")) {
                    this.jNumeroPOS.setIcon(icono_pos_master);
                } else if (code.equals("POS_ID") && !(value.equals(""))) {
                    this.jLabel19.setText(value);
                } else {
                    this.jLabel19.setText("1");
                }
            }
        } else {
            this.jLabel19.setText("1");
        }
    }

    private void loadSideMenuToUp() {

        int size = this.mainMenuElementDynamic.size();
        if (possElementDynamic == 4) {
            if (size % 4 == 0) {
                possElementDynamic = size - 4;
            } else {
                possElementDynamic = size - (size % 4);
            }
        } else {
            possElementDynamic = possMementoFirstElement - 4;
        }
        loadSideMenuToDown();

    }

    private void loadSideMenuToDown() {

        jDynamicPanel.removeAll();
        int maxElements = 0;
        int maxHeight = 0;

        possMementoFirstElement = possElementDynamic;
        while (maxElements < 4 && possElementDynamic < this.mainMenuElementDynamic.size()) {
            JLabel nextElement = mainMenuElementDynamic.get(possElementDynamic);

            nextElement.setBounds(nextElement.getX(), maxHeight, nextElement.getWidth(), nextElement.getHeight());

            if (jDynamicPanel.getComponents().length == 0) {
                maxHeight = nextElement.getHeight() + 4;
            } else {
                maxHeight = maxHeight + nextElement.getHeight() + 4;
            }
            this.jDynamicPanel.add(nextElement);

            maxElements++;
            possElementDynamic++;
        }

        if (possElementDynamic == this.mainMenuElementDynamic.size()) {
            possElementDynamic = 0;
        }
        jDynamicPanel.repaint();

        jPanel2.repaint();
    }

    private void loadWithoutPumElement() {
        this.mainMenuElementDynamic.add(jmod_turno);
        this.mainMenuElementDynamic.add(jmod_kco);
        this.mainMenuElementDynamic.add(jmod_reporte);
        this.mainMenuElementDynamic.add(jmod_configuracion);
        this.mainMenuElementDynamic.add(jmod_usuario);
        jLabel18.setVisible(false);
        jLabel11.setBounds(jSurtidorConectado.getBounds());
        pnlNumeroNotificaiones.setBounds(970, 700, 40, 40);
        jSurtidorConectado.setVisible(false);
        this.jPanel2.removeAll();
        this.jPanel2.add(jDynamicPanel);

    }

    private void init() {
        this.loadView();
        this.loadData();

        if (Main.SIN_SURTIDOR) {
            loadWithoutPumElement();
            loadSideMenuToDown();
        } else {
            this.jPanel2.remove(this.jDynamicPanel);
        }

        pnlNumeroNotificaiones.setVisible(false);
        PanelResultadoBusqueda.limpiarInstancia();
        jPanelConexion.setVisible(false);
        jmod_ventasManuales.setVisible(false);
        this.cargarLecturaAforo();
        MedioPagosConfirmarViewController.bonosValidados = false;
        NotificadorInfoView = (JsonObject data) -> {
            System.out.println("🔥 [NotificadorInfoView] Notificación recibida: " + data.toString());
            if (data.has("disconnected")) {
                boolean conexion = data.get("disconnected") != null ? data.get("disconnected").getAsBoolean() : true;
                int proceso = data.get("proceso") != null ? data.get("proceso").getAsInt() : 0;
                mostrarPanelConexion(data.get("mensaje").getAsString(), data.get("notificacionTimeout").getAsInt(), conexion, proceso);
            } else if (data.get("tipo").getAsInt() == 4) {
                panelMensajesAppTerpel(data);
            } else if (data.get("tipo").getAsInt() == 3) {
                panelMensajesGopass(data);
            } else if (data.get("tipo").getAsInt() == 2) {
                panelMensajesDatafono(data);
            } else if (data.get("tipo").getAsInt() == 1) {
                // Detectar si es notificación GLP (código 25500) para darle más tiempo
                String codigo = data.has("codigo") && !data.get("codigo").isJsonNull() 
                    ? data.get("codigo").getAsString() 
                    : "";
                
                if (codigo.equals("25500")) {
                    // 🔥 Notificación GLP - 10 segundos de duración
                    mostrarPanelMensaje(data.get("mensaje").getAsString(), data.get("icono").getAsString(), 10);
                    //System.out.println("Mostrando Mensje de GLP");
                } else {
                    // Otras notificaciones - 3 segundos por defecto
                    mostrarPanelMensaje(data.get("mensaje").getAsString(), data.get("icono").getAsString());
                }
            }
        };

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                jclock.stopClock();
            }
        });
    }

    public void cargarNotificador() {
        NotificadorInfoView = (JsonObject data) -> {
            if (data.has("disconnected")) {
                boolean conexion = data.get("disconnected") != null ? data.get("disconnected").getAsBoolean() : true;
                int proceso = data.get("proceso") != null ? data.get("proceso").getAsInt() : 0;
                mostrarPanelConexion(data.get("mensaje").getAsString(), data.get("notificacionTimeout").getAsInt(), conexion, proceso);
            } else if (data.get("tipo").getAsInt() == 4) {
                panelMensajesAppTerpel(data);
            } else if (data.get("tipo").getAsInt() == 3) {
                panelMensajesGopass(data);
            } else if (data.get("tipo").getAsInt() == 2) {
                panelMensajesDatafono(data);
            } else if (data.get("tipo").getAsInt() == 1) {
                mostrarPanelMensaje(data.get("mensaje").getAsString(), data.get("icono").getAsString());
            }
        };
    }

    public void panelMensajesDatafono(JsonObject data) {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "mensajesDatafono");
        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        
        if (data.get("mensaje").getAsString().length() > 20) {
            lblRespuestaTransaccion.setFont(new java.awt.Font("Terpel Sans", 1, 18));
            lblRespuestaTransaccion.setBounds(520, 330, 450, 150);
        }
        if (data.get("estado").getAsBoolean()) {
            jTitulo.setText("<html><center><b>" + data.get("mensaje").getAsString() + "</b></center></html>");
            jIcono.setIcon(okDatafono);
        } else {
            jTitulo.setText("<html><center><b>" + data.get("mensaje").getAsString() + "</b></center></html>");
            jIcono.setIcon(errorDatafono);
        }
        if (!data.get("plaqueta").getAsString().isEmpty()) {
            jInfoDatafono.setText("Numero plaqueta" + data.get("plaqueta").getAsString());
        } else {
            jInfoDatafono.setText("");
        }
        if (!data.get("codigoAutorizacion").getAsString().isEmpty()) {
            jTransacciones.setText("Id transacción" + data.get("codigoAutorizacion").getAsString());
        } else {
            jTransacciones.setText("");
        }
        Async(runnable, 10);
    }

    public void panelMensajesAppTerpel(JsonObject data) {

        //AQUI DIBUJA APPTERPEL
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnlMensajeGopass");
        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        lblRespuestaTransaccion.setText("");
        lblRespuestaTransaccion.setFont(new java.awt.Font("Terpel Sans", 1, 18));
        lblRespuestaTransaccion.setBounds(530, 360, 430, 70);

        if (data.get("mensaje").getAsString().length() > 20) {
            lblRespuestaTransaccion.setFont(new java.awt.Font("Terpel Sans", 1, 18));
            lblRespuestaTransaccion.setBounds(520, 330, 450, 150);
        }

        if (data.get("estado").getAsBoolean()) {
            fndIcono.setIcon(iconoAppTerpelOk);
            lblPlaca.setText("<html><center>" + TransaccionMessageView.TRANSACCION_APROBADA + "</center></html>");
        } else {
            fndIcono.setIcon(iconoAppTerpelError);
            lblPlaca.setText("<html><center>" + TransaccionMessageView.TRANSACCION_RECHAZADA + "</center></html>");
        }
        lblTitulo.setText(data.get("titulo").getAsString());
        if (data.get("codigoAutorizacion").getAsString().equals("1815054")) {
            lblRespuestaTransaccion.setBounds(530, 290, 430, 200);
            if (data.get("estado").getAsBoolean()) {
                fndIcono.setIcon(iconoOk);
            } else {
                fndIcono.setIcon(iconoError);
            }
            lblPlaca.setText("");
        }
        String mensaje = NovusUtils.convertMessage(LetterCase.FIRST_UPPER_CASE,
                data.get("mensaje").getAsString());

        String mensajeFormateado = mensaje;
        // se debe borrar la validación ya que el backend debe entregar el emnsaje correctamente.
        if (mensajeFormateado != null && !mensajeFormateado.isEmpty() && !mensajeFormateado.isBlank() && mensajeFormateado.toLowerCase().startsWith("rechazado")) {
            mensajeFormateado = mensaje.length() > 10 ? mensaje.substring(10) : mensaje ;
        }
        lblRespuestaTransaccion.setText("<html><center>" + mensajeFormateado + "</center></html>");

        Async(runnable, 10);
    }

    public void panelMensajesGopass(JsonObject data) {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnlMensajeGopass");
        Runnable runnable = () -> {
            cambiarPanelHome();
        };
        String mensaje;
        lblRespuestaTransaccion.setFont(new java.awt.Font("Terpel Sans", 1, 18));
        lblRespuestaTransaccion.setBounds(530, 360, 430, 70);

        if (data.get("mensaje").getAsString().length() > 20) {
            lblRespuestaTransaccion.setFont(new java.awt.Font("Terpel Sans", 1, 18));
            lblRespuestaTransaccion.setBounds(520, 330, 450, 150);
        }

        if (data.get("estado").getAsBoolean()) {
            fndIcono.setIcon(iconoOk);
            mensaje = "Pago aprobado";
            lblRespuestaTransaccion.setText(mensaje);
        } else {
            fndIcono.setIcon(iconoError);
            mensaje = "Pago rechazado";
            lblRespuestaTransaccion.setText(mensaje);
        }
        if (data.get("placa") != null && !data.get("placa").getAsString().isEmpty()) {
            lblPlaca.setText("Placa " + data.get("placa").getAsString());
        } else {
            lblPlaca.setText("");
        }
        lblTitulo.setText(data.get("titulo").getAsString());
        if (data.get("codigoAutorizacion").getAsString().equals("1815054")) {
            lblRespuestaTransaccion.setBounds(530, 290, 430, 200);
            if (data.get("estado").getAsBoolean()) {
                fndIcono.setIcon(iconoOk);
            } else {
                fndIcono.setIcon(iconoError);
            }
            lblPlaca.setText("");
        }
        Async(runnable, 10);
    }

    public void closeMensajeDatafono() {
        cambiarPanelHome();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        obtenerPromotoresJornadaUseCase = new ObtenerPromotoresJornadaUseCase();
        obtenerConsecutivoUseCase = new ObtenerConsecutivoUseCase(NovusConstante.IS_DEFAULT_FE, "CAN");
        pnlPrincipal = new javax.swing.JPanel();
        home = new javax.swing.JPanel();
        jclock = new ClockViewController();
        jNotificaciones = new javax.swing.JLabel();
        jnotificacionesRfid = new javax.swing.JPanel();
        jnotificaciones = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jmod_venta = new javax.swing.JLabel();
        jmod_kco = new javax.swing.JLabel();
        jmod_rumbo = new javax.swing.JLabel();
        jmod_turno = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jmod_store = new javax.swing.JLabel();
        jmod_configuracion = new javax.swing.JLabel();
        jmod_fidelizacion = new javax.swing.JLabel();
        jmod_surtidor = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jmod_usuario = new javax.swing.JLabel();
        jmod_reporte = new javax.swing.JLabel();
        jmod_gopass1 = new javax.swing.JLabel();
        jmod_ventasManuales = new javax.swing.JLabel();
        jDynamicPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jInternet = new javax.swing.JLabel();
        jSurtidorConectado = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        pnlNumeroNotificaiones = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblNumeroNotificaion = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jNumeroPOS = new javax.swing.JLabel();
        jmensajes = new javax.swing.JLabel();
        jventas_retenidas_numero = new javax.swing.JLabel();
        jventas_retenidas = new javax.swing.JLabel();
        jPanelConexion = PanelConexion.getInstance();
        jnotificaciones1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        card_mensaje = new PanelNotificacion();
        jSubMenu = new javax.swing.JPanel();
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
        lblPlaca = new javax.swing.JLabel();
        lblRespuestaTransaccion = new javax.swing.JLabel();
        fndIcono = new javax.swing.JLabel();
        fndMensaje = new javax.swing.JLabel();
        fndFondo1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setLayout(new java.awt.CardLayout());

        home.setLayout(null);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setLayout(null);
        home.add(jclock);
        jclock.setBounds(1150, 720, 110, 60);

        jNotificaciones.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jNotificaciones.setForeground(new java.awt.Color(255, 255, 255));
        jNotificaciones.setIcon(icono_nostificaciones); // NOI18N
        jNotificaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jNotificacionesMouseReleased(evt);
            }
        });
        home.add(jNotificaciones);
        jNotificaciones.setBounds(845, 15, 316, 50);

        jnotificacionesRfid.setOpaque(false);
        jnotificacionesRfid.setLayout(null);
        home.add(jnotificacionesRfid);
        jnotificacionesRfid.setBounds(140, 705, 510, 90);

        jnotificaciones.setOpaque(false);
        jnotificaciones.setLayout(null);
        jnotificaciones.add(jLabel9);
        jLabel9.setBounds(1030, 0, 110, 80);

        home.add(jnotificaciones);
        jnotificaciones.setBounds(210, 0, 810, 535);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(null);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.CardLayout());

        jPanel3.setOpaque(false);
        jPanel3.setLayout(null);

        jmod_venta.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_venta.setForeground(new java.awt.Color(102, 255, 0));
        jmod_venta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_venta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_ventas.png"))); // NOI18N
        jmod_venta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_venta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_ventaMouseReleased(evt);
            }
        });
        jPanel3.add(jmod_venta);
        jmod_venta.setBounds(30, 140, 130, 130);

        jmod_kco.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_kco.setForeground(new java.awt.Color(102, 255, 0));
        jmod_kco.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_kco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/moduloMarket.png"))); // NOI18N
        jmod_kco.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_kco.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_kcoMouseReleased(evt);
            }
        });
        jPanel3.add(jmod_kco);
        jmod_kco.setBounds(30, 280, 130, 130);

        jmod_rumbo.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_rumbo.setForeground(new java.awt.Color(102, 255, 0));
        jmod_rumbo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_rumbo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_rumbo.png"))); // NOI18N
        jmod_rumbo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_rumbo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_rumboMouseReleased(evt);
            }
        });
        jPanel3.add(jmod_rumbo);
        jmod_rumbo.setBounds(30, 420, 130, 130);

        jmod_turno.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_turno.setForeground(new java.awt.Color(102, 255, 0));
        jmod_turno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_turno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_turnos.png"))); // NOI18N
        jmod_turno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_turno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_turnoMouseReleased(evt);
            }
        });
        jPanel3.add(jmod_turno);
        jmod_turno.setBounds(30, 0, 130, 130);

        jPanel2.add(jPanel3, "card1");

        jPanel4.setOpaque(false);
        jPanel4.setLayout(null);

        jmod_store.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_store.setForeground(new java.awt.Color(102, 255, 0));
        jmod_store.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_store.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_store.png"))); // NOI18N
        jmod_store.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_store.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_storeMouseReleased(evt);
            }
        });
        jPanel4.add(jmod_store);
        jmod_store.setBounds(30, 0, 130, 130);

        jmod_configuracion.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_configuracion.setForeground(new java.awt.Color(102, 255, 0));
        jmod_configuracion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_configuracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_configuracion.png"))); // NOI18N
        jmod_configuracion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_configuracion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jmod_configuracionMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_configuracionMouseReleased(evt);
            }
        });
        jPanel4.add(jmod_configuracion);
        jmod_configuracion.setBounds(30, 140, 130, 130);

        jmod_fidelizacion.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_fidelizacion.setForeground(new java.awt.Color(102, 255, 0));
        jmod_fidelizacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_fidelizacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_fidelizacion.png"))); // NOI18N
        jmod_fidelizacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_fidelizacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_fidelizacionMouseReleased(evt);
            }
        });
        jPanel4.add(jmod_fidelizacion);
        jmod_fidelizacion.setBounds(30, 280, 130, 130);

        jmod_surtidor.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_surtidor.setForeground(new java.awt.Color(102, 255, 0));
        jmod_surtidor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_surtidor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_surtidor.png"))); // NOI18N
        jmod_surtidor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_surtidor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_surtidorMouseReleased(evt);
            }
        });
        jPanel4.add(jmod_surtidor);
        jmod_surtidor.setBounds(30, 420, 130, 130);

        jPanel2.add(jPanel4, "card2");

        jPanel8.setOpaque(false);
        jPanel8.setLayout(null);

        jmod_usuario.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_usuario.setForeground(new java.awt.Color(102, 255, 0));
        jmod_usuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_usuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_usuarios.png"))); // NOI18N
        jmod_usuario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_usuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_usuarioMouseReleased(evt);
            }
        });
        jPanel8.add(jmod_usuario);
        jmod_usuario.setBounds(30, 140, 130, 130);

        jmod_reporte.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jmod_reporte.setForeground(new java.awt.Color(102, 255, 0));
        jmod_reporte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_reporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_reporte.png"))); // NOI18N
        jmod_reporte.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_reporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_reporteMouseReleased(evt);
            }
        });
        jPanel8.add(jmod_reporte);
        jmod_reporte.setBounds(30, 0, 130, 130);

        jmod_gopass1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_gopass1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modulo_gopass.png"))); // NOI18N
        jmod_gopass1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_gopass1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jmod_gopass1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmod_gopass1MouseReleased(evt);
            }
        });
        jPanel8.add(jmod_gopass1);
        jmod_gopass1.setBounds(30, 280, 130, 130);

        jmod_ventasManuales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmod_ventasManuales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ventas-manuales--version-1.0-16.png"))); // NOI18N
        jmod_ventasManuales.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jmod_ventasManuales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jmod_ventasManualesMouseClicked(evt);
            }
        });
        jPanel8.add(jmod_ventasManuales);
        jmod_ventasManuales.setBounds(30, 420, 130, 130);

        jPanel2.add(jPanel8, "card3");

        jDynamicPanel.setOpaque(false);
        jDynamicPanel.setLayout(null);
        jPanel2.add(jDynamicPanel, "card1SS");

        jPanel1.add(jPanel2);
        jPanel2.setBounds(60, 75, 180, 550);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_derecha1.png"))); // NOI18N
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel5MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel5);
        jLabel5.setBounds(112, 635, 90, 58);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_izquierda1.png"))); // NOI18N
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel4);
        jLabel4.setBounds(110, 10, 90, 58);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_menu.png"))); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel1.add(jLabel2);
        jLabel2.setBounds(20, -5, 220, 710);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_menu_arriba.png"))); // NOI18N
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel3MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel3);
        jLabel3.setBounds(2, 290, 60, 110);

        home.add(jPanel1);
        jPanel1.setBounds(1040, 35, 240, 720);

        jPanel6.setLayout(null);

        jInternet.setBackground(new java.awt.Color(186, 12, 47));
        jInternet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/conectado.png"))); // NOI18N
        jInternet.setOpaque(true);
        jPanel6.add(jInternet);
        jInternet.setBounds(840, 710, 70, 80);

        jSurtidorConectado.setBackground(new java.awt.Color(186, 12, 47));
        jSurtidorConectado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/surtidoConectado.png"))); // NOI18N
        jSurtidorConectado.setRequestFocusEnabled(false);
        jPanel6.add(jSurtidorConectado);
        jSurtidorConectado.setBounds(920, 710, 70, 80);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_solicitar_placa.png"))); // NOI18N
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel18MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel18);
        jLabel18.setBounds(10, 140, 160, 140);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(186, 12, 47));
        jLabel14.setText("Version Software 0.0.0");
        jPanel6.add(jLabel14);
        jLabel14.setBounds(20, 120, 580, 20);

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/franjaVersion.png"))); // NOI18N
        jPanel6.add(jLabel13);
        jLabel13.setBounds(0, 120, 370, 23);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 40)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("E.D.S. NUEVO HORIZONTE");
        jPanel6.add(jLabel7);
        jLabel7.setBounds(10, 10, 830, 60);

        jLabel8.setFont(new java.awt.Font("Roboto Mono", 1, 28)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.add(jLabel8);
        jLabel8.setBounds(1130, 10, 100, 50);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel6.add(jLabel17);
        jLabel17.setBounds(1130, 710, 10, 80);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel6.add(jLabel16);
        jLabel16.setBounds(120, 710, 10, 80);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/LOGO-POS-01.png"))); // NOI18N
        jPanel6.add(jLabel15);
        jLabel15.setBounds(10, 710, 100, 80);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("SIN TURNOS");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel6MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel6);
        jLabel6.setBounds(10, 80, 940, 40);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(185, 23, 50));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("0");
        jPanel6.add(jLabel19);
        jLabel19.setBounds(690, 720, 40, 60);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/barraPromotor.png"))); // NOI18N
        jPanel6.add(jLabel10);
        jLabel10.setBounds(0, 80, 1280, 40);

        pnlNumeroNotificaiones.setBackground(new java.awt.Color(204, 204, 204));
        pnlNumeroNotificaiones.setRoundBottomLeft(100);
        pnlNumeroNotificaiones.setRoundBottomRight(100);
        pnlNumeroNotificaiones.setRoundTopLeft(100);
        pnlNumeroNotificaiones.setRoundTopRight(100);
        pnlNumeroNotificaiones.setLayout(null);

        lblNumeroNotificaion.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblNumeroNotificaion.setForeground(new java.awt.Color(153, 0, 0));
        lblNumeroNotificaion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumeroNotificaion.setText("0");
        pnlNumeroNotificaiones.add(lblNumeroNotificaion);
        lblNumeroNotificaion.setBounds(4, 4, 35, 30);

        jPanel6.add(pnlNumeroNotificaiones);
        pnlNumeroNotificaiones.setBounds(1040, 700, 40, 40);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono de notificaciones.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel11);
        jLabel11.setBounds(1000, 710, 78, 80);

        jNumeroPOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/posesclavoindicador.png"))); // NOI18N
        jPanel6.add(jNumeroPOS);
        jNumeroPOS.setBounds(630, 700, 130, 100);

        jmensajes.setFont(new java.awt.Font("Conthrax", 0, 10)); // NOI18N
        jmensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel6.add(jmensajes);
        jmensajes.setBounds(980, 570, 40, 30);

        jventas_retenidas_numero.setBackground(new java.awt.Color(215, 215, 215));
        jventas_retenidas_numero.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jventas_retenidas_numero.setForeground(new java.awt.Color(185, 23, 50));
        jventas_retenidas_numero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jventas_retenidas_numero.setText("0");
        jPanel6.add(jventas_retenidas_numero);
        jventas_retenidas_numero.setBounds(780, 730, 30, 22);

        jventas_retenidas.setBackground(new java.awt.Color(186, 12, 47));
        jventas_retenidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ok.png"))); // NOI18N
        jventas_retenidas.setRequestFocusEnabled(false);
        jPanel6.add(jventas_retenidas);
        jventas_retenidas.setBounds(760, 710, 70, 80);

        jPanelConexion.setOpaque(false);
        jPanel6.add(jPanelConexion);
        jPanelConexion.setBounds(462, 610, 352, 74);

        jnotificaciones1.setOpaque(false);
        jnotificaciones1.setPreferredSize(new java.awt.Dimension(1050, 260));
        jnotificaciones1.setLayout(null);
        jPanel6.add(jnotificaciones1);
        jnotificaciones1.setBounds(40, 550, 1050, 160);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal1.png"))); // NOI18N
        jPanel6.add(jLabel1);
        jLabel1.setBounds(-20, 0, 1320, 800);

        home.add(jPanel6);
        jPanel6.setBounds(0, 0, 1290, 800);

        pnlPrincipal.add(home, "home");
        pnlPrincipal.add(card_mensaje, "card_mensaje");

        jSubMenu.setLayout(null);
        pnlPrincipal.add(jSubMenu, "sub_menu");

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
        jTitulo.setBounds(280, 440, 740, 100);

        jInfoDatafono.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jInfoDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfoDatafono.setText("INFORMACION DATAFONO");
        jInfoDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jInfoDatafono);
        jInfoDatafono.setBounds(280, 540, 740, 28);

        jTransacciones.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTransacciones.setForeground(new java.awt.Color(186, 12, 47));
        jTransacciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTransacciones.setText("TRANSACCION");
        jTransacciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jTransacciones);
        jTransacciones.setBounds(280, 570, 740, 28);

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
        lblTitulo.setBounds(550, 300, 390, 40);

        lblPlaca.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblPlaca.setForeground(new java.awt.Color(186, 12, 47));
        lblPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPlaca.setText("PLACA");
        pnlMensajeGopass.add(lblPlaca);
        lblPlaca.setBounds(530, 440, 430, 40);

        lblRespuestaTransaccion.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblRespuestaTransaccion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRespuestaTransaccion.setText("RESPUESTA TRANSACCION");
        pnlMensajeGopass.add(lblRespuestaTransaccion);
        lblRespuestaTransaccion.setBounds(510, 340, 460, 110);

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

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jNotificacionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jNotificacionesMouseReleased
        abrirHistorialVentas();
    }//GEN-LAST:event_jNotificacionesMouseReleased

    private void jmod_gopass1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmod_gopass1MouseClicked
        abrirMenuGoPass();
    }//GEN-LAST:event_jmod_gopass1MouseClicked

    private void jmod_ventasManualesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmod_ventasManualesMouseClicked
        abrirVentaManual();
    }//GEN-LAST:event_jmod_ventasManualesMouseClicked

    private void jmod_gopass1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmod_gopass1MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jmod_gopass1MouseReleased

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        closeMensajeDatafono();
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jCerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        closeMensajeDatafono();
    }//GEN-LAST:event_jCerrar1MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        NotificacionesErrores notificacionesErrores = new NotificacionesErrores(this, this.dataNotificacionError);
        notificacionesErrores.setVisible(true);
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jmod_fidelizacionMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_fidelizacionMouseReleased
        abrirFidelizacionMenu();
    }// GEN-LAST:event_jmod_fidelizacionMouseReleased

    private void jLabel18MouseReleased(java.awt.event.MouseEvent evt) {
        //abrirMenuPreautorizar();
        //flujo normal 
        abrirMenuTipoVenta();
    }// GEN-LAST:event_jLabel18MouseReleased

    private void jmod_rumboMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_rumboMouseReleased
        abrirMenuRumbo();
    }// GEN-LAST:event_jmod_rumboMouseReleased

    private void jmod_kcoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_kcoMouseReleased
        abrirMenuMarket();
        // PrinterFacade p = new PrinterFacade();
        // p.printComprobanteElectronico();
    }// GEN-LAST:event_jmod_kcoMouseReleased

    private void jmod_storeMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_storeMouseReleased
        abrirStore();
    }// GEN-LAST:event_jmod_storeMouseReleased

    private void jmod_configuracionMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_configuracionMouseReleased
        abrirMenuConfiguracion();
    }// GEN-LAST:event_jmod_configuracionMouseReleased

    private void jmod_surtidorMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_surtidorMouseReleased
        abrirMenuSurtidores();
    }// GEN-LAST:event_jmod_surtidorMouseReleased

    private void jmod_usuarioMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_usuarioMouseReleased
        sincronizarPersonal();
    }// GEN-LAST:event_jmod_usuarioMouseReleased

    private void jmod_gopassMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_gopassMouseReleased
        abrirMenuGoPass();
    }// GEN-LAST:event_jmod_gopassMouseReleased

    private void jmod_configuracionMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_configuracionMousePressed

    }// GEN-LAST:event_jmod_configuracionMousePressed

    private void jmod_gopassMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_gopassMouseClicked
        abrirMenuGoPass();
    }// GEN-LAST:event_jmod_gopassMouseClicked

    boolean hayConsecutivosCombustible() {
        boolean hayConsecutivosCombustible = false;
        JsonObject response = solicitarConsecutivos();
        if (response != null) {
            NovusUtils.printLn(response.toString());
            JsonArray data = response.get("data") != null && !response.get("data").isJsonNull()
                    ? response.get("data").getAsJsonArray()
                    : new JsonArray();
            hayConsecutivosCombustible = data.size() > 0;
        }
        return hayConsecutivosCombustible;
    }

    JsonObject solicitarConsecutivos() {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync("CONSEGUIR CONSECUTIVOS",
                NovusConstante.SECURE_CENTRAL_POINT_CONSECUTIVOS_FACTURAS, NovusConstante.GET, null, true, false,
                header);
        try {
            response = client.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
    }

    public void limpiarPanelSubmenu() {
        InfoViewController.jSubMenu.setVisible(false);
    }

    private void jLabel3MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MouseReleased
        mostrarOcultarMenu();
    }// GEN-LAST:event_jLabel3MouseReleased

    private void jmod_turnoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_turnoMouseReleased
        abrirMenuTurnos();
    }// GEN-LAST:event_jmod_turnoMouseReleased

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed
        jLabel5.setIcon(icono_btn_derecha2);
    }// GEN-LAST:event_jLabel5MousePressed

    private void jmod_reporteMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_reporteMouseReleased
        abrirMenuReportes();
    }// GEN-LAST:event_jmod_reporteMouseReleased

    private void jmod_clienteMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_clienteMouseReleased
        // abrirClientes();
    }// GEN-LAST:event_jmod_clienteMouseReleased

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        jLabel4.setIcon(icono_btn_izquierda2);
    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel5MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MouseReleased
        jLabel5.setIcon(icono_btn_derecha1);
        rotarMenuDerecha();
    }// GEN-LAST:event_jLabel5MouseReleased

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        jLabel4.setIcon(icono_btn_izquierda1);
        rotarMenuIzquierda();
    }// GEN-LAST:event_jLabel4MouseReleased

    private void jmod_ventaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmod_ventaMouseReleased
        abrirMenuVentas();
    }// GEN-LAST:event_jmod_ventaMouseReleased

    private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MouseReleased
        recargarPersona();
    }// GEN-LAST:event_jLabel6MouseReleased

    // <editor-fold defaultstate="collapsed" desc=" Variables declaration - do not
    // modify ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel card_mensaje;
    private javax.swing.JLabel fndFondo;
    private javax.swing.JLabel fndFondo1;
    private javax.swing.JLabel fndIcono;
    private javax.swing.JLabel fndMensaje;
    private javax.swing.JPanel home;
    public static javax.swing.JLabel jCerrar;
    public static javax.swing.JLabel jCerrar1;
    private javax.swing.JPanel jDynamicPanel;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jInfoDatafono;
    private javax.swing.JLabel jInternet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    public static javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificaciones;
    private javax.swing.JLabel jNumeroPOS;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelConexion;
    public static javax.swing.JPanel jSubMenu;
    private javax.swing.JLabel jSurtidorConectado;
    private javax.swing.JLabel jTitulo;
    private javax.swing.JLabel jTransacciones;
    private ClockViewController jclock;
    public static javax.swing.JLabel jmensajes;
    private javax.swing.JLabel jmod_configuracion;
    private javax.swing.JLabel jmod_fidelizacion;
    private javax.swing.JLabel jmod_gopass1;
    private javax.swing.JLabel jmod_kco;
    private javax.swing.JLabel jmod_reporte;
    private javax.swing.JLabel jmod_rumbo;
    private javax.swing.JLabel jmod_store;
    private javax.swing.JLabel jmod_surtidor;
    private javax.swing.JLabel jmod_turno;
    private javax.swing.JLabel jmod_usuario;
    private javax.swing.JLabel jmod_venta;
    private javax.swing.JLabel jmod_ventasManuales;
    public static javax.swing.JPanel jnotificaciones;
    public static javax.swing.JPanel jnotificaciones1;
    public static javax.swing.JPanel jnotificacionesRfid;
    private javax.swing.JLabel jventas_retenidas;
    private javax.swing.JLabel jventas_retenidas_numero;
    public static javax.swing.JLabel lblNumeroNotificaion;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblRespuestaTransaccion;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlMensajeGopass;
    private javax.swing.JPanel pnlMsjDatafonos;
    public static com.firefuel.components.panelesPersonalizados.PanelRedondo pnlNumeroNotificaiones;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    public void preguntarJornadas() {
        setTimeout(10, () -> {
            consigueJornadaRemotas();
            if (Main.persona == null) {
                preguntarJornadas();
            }
        });
    }

    public void mostrarOcultarMenu() {
        NovusUtils.beep();
        showMenu = !showMenu;
        if (showMenu) {
            jLabel3.setIcon(icono_menu_arriba);
            jPanel1.setBounds(1040, 35, 240, 700);

        } else {
            jLabel3.setIcon(icono_menu_abajo);
            jPanel1.setBounds(1040 + 180, 35, 240, 700);
        }
    }

    private void abrirMenuTurnos() {
        NovusUtils.beep();
        mostrarSubPanel(new TurnoMenuPanelController(this));
    }

    public void abrirMenuConfiguracion() {
        NovusUtils.beep();
        ConfiguracionMenuPanelController conf = new ConfiguracionMenuPanelController(this, true);
        AutorizacionView auto = new AutorizacionView(this, true, conf);
        auto.setVisible(true);
    }

    private void abrirStore() {
        NovusUtils.beep();
        if (Main.persona == null) {
            mostrarPanelSinTurnos();
        } else {
            try {
                MovimientosDao mdao = new MovimientosDao();
                // ✅ CORREGIDO: Usar DAO tradicional como en abrirMenuMarket() - consistente y confiable
                if (mdao.getConsecutivo(NovusConstante.IS_DEFAULT_FE) == null || !hayConsecutivosCombustible()) {
                    ConsecutivosDialogView dialog = new ConsecutivosDialogView(this, true);
                    dialog.setVisible(true);
                } else {
                    this.recargarPersona();
                    if (InfoViewController.turnosPersonas.size() > 1) {
                        PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(this,
                                NovusConstante.SELECCION_PROMOTOR_STORE);
                        seleccionpromotor.setVisible(true);

                    } else {
                        StoreViewController store = new StoreViewController(this, true);
                        showParent(store);
                        StoreViewController.jentry_txt.requestFocus();
                    }
                }
            } catch (DAOException a) {
                NovusUtils.printLn(a.getMessage());
            }
        }
    }

    void abrirMenuMarket() {
        NovusUtils.beep();
        if (Main.persona == null) {
            mostrarPanelSinTurnos();
        } else {
            try {
                MovimientosDao mdao = new MovimientosDao();
                // TODO: Migrar getConsecutivoMarket() - método diferente que requiere UseCase específico
                if (mdao.getConsecutivoMarket(NovusConstante.IS_DEFAULT_FE) == null) {
                    ConsecutivosDialogView dialog = new ConsecutivosDialogView(this, true);
                    dialog.setVisible(true);
                } else {
                    this.recargarPersona();
                    if (InfoViewController.turnosPersonas.size() > 1) {
                        PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(this,
                                NovusConstante.SELECCION_PROMOTOR_KCO);
                        seleccionpromotor.setVisible(true);
                    } else {
                        KCOViewController store = new KCOViewController(this, true);
                        showParent(store);
                        KCOViewController.jentry_txt.requestFocus();
                    }
                }
            } catch (DAOException a) {
                NovusUtils.printLn(a.getMessage());
            }
        }
    }

    private void abrirMenuReportes() {
        NovusUtils.beep();
        mostrarSubPanel(new ReporteMenuPanelController(this));
    }

    private void rotarMenuDerecha() {

        NovusUtils.beep();
        if (Main.SIN_SURTIDOR) {
            loadSideMenuToDown();
        } else {

            CardLayout cl = (CardLayout) (jPanel2.getLayout());
            cl.next(jPanel2);
        }
    }

    private void rotarMenuIzquierda() {
        NovusUtils.beep();
        if (Main.SIN_SURTIDOR) {
            loadSideMenuToUp();
        } else {
            CardLayout cl = (CardLayout) (jPanel2.getLayout());
            cl.previous(jPanel2);
        }
    }

    public void abrirMenuVentas() {
        NovusUtils.beep();
        mostrarSubPanel(new VentaMenuPanelController(this, this));
    }

    private void sincronizarPersonal() {
        NovusUtils.beep();
        UsuariosMenuPanelController ump = new UsuariosMenuPanelController(this);
        AutorizacionView auto = new AutorizacionView(this, showMenu, ump);
        auto.setVisible(true);
    }

    public void recargarPersona() {
        if (Main.CENTRALIZADOR) {
            this.consigueJornadaRemotas();
        }
    }

    /**
     * Obtiene las jornadas activas de los promotores usando el caso de uso ObtenerPromotoresJornadaUseCase
     * Limpia la lista de turnos actual y la actualiza con los promotores obtenidos
     * Si no hay jornadas activas, establece Main.persona como null
     */
    private void obtenerJornadasV2() {
        turnosPersonas.clear();
        try {
            List<PersonaBean> promotores = obtenerPromotoresJornadaUseCase.execute();
            turnosPersonas = new ArrayList<>(promotores);
            System.out.println("🔍 turnosPersonas: " + turnosPersonas);
            if (turnosPersonas.isEmpty()) {
                Main.persona = null;
            }
        } catch ( Exception e) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, e);
            Main.persona = null;
        }
    }

    private void consigueJornadaRemotas() {
        obtenerJornadasV2();
        this.renderJournalData();
    }

    private void abrirMenuSurtidores() {
        NovusUtils.beep();
        SurtidorMenuPanelController surtidor = new SurtidorMenuPanelController(this, true);
        surtidor.setVisible(true);
    }

    private void abrirFidelizacionMenu() {
        NovusUtils.beep();
        this.recargarPersona();
        mostrarSubPanel(new FidelizacionMenuPanelController(this));
    }

    private void abrirMenuGoPass() {
        NovusUtils.beep();
        mostrarSubPanel(new GoPassMenuController(this));
    }

    public void abrirFactManual() {
        NovusUtils.beep();
        if (!InfoViewController.turnosPersonas.isEmpty()) {
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this,
                        NovusConstante.SELECCION_PROMOTOR_FACT_MANUAL);
                seleccionpromotor.setVisible(true);
            } else {
                mostrarSubPanel(new VentaManualMenuPanel(this, true));
            }
        }
    }

    public void mostrarPanelSinTurnos() {
        showMessage("NO HAY TURNOS ABIERTOS",
                InfoViewController.icono_sin_turnos,
                true, this::cambiarPanelHome,
                true, LetterCase.FIRST_UPPER_CASE);
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(InfoViewController.class
                        .getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono) {
        mostrarPanelMensaje(mensaje, icono, 3); // Usar 3 segundos por defecto
    }
    
    public void mostrarPanelMensaje(String mensaje, String icono, int tiempoSegundos) {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "card_mensaje");
        PanelNotificacion panelMensaje = (PanelNotificacion) card_mensaje;
        panelMensaje.setHabilitarBoton(true);
        panelMensaje.setMensaje(mensaje);
        Runnable runnable = () -> {
            cambiarPanelHome();
        };
        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btOk.png";
        }
        if (icono.length() == 1) {
            icono = "/com/firefuel/resources/btBad.png";
        }
        panelMensaje.setImagen(icono);
        panelMensaje.setHandler(runnable);
        panelMensaje.mostrar();
        Async(runnable, tiempoSegundos); // ← Tiempo personalizable
    }

    public void mostrarPanelConexion(String mensaje, int time, boolean mostrar, int proceso) {
        NovusUtils.printLn("Proceso Panel Notificacion: " + proceso);
        PanelConexion pc = (PanelConexion) jPanelConexion;
        pc.setMensaje(NovusUtils.convertMessage(
                LetterCase.FIRST_UPPER_CASE,
                mensaje));
        pc.mostrar();
        pc.setProceso(proceso);
        pc.mostrarEstado();
        pc.setHandler(() -> {
            jPanelConexion.setVisible(false);
        });
        if (proceso != 0) {
            pc.setTimeout(time);
            pc.getTimer().start();
        }
        jPanelConexion.setVisible(mostrar);
    }

    public void showMessage(String msj, ImageIcon ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setImageIcon(ruta)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    public void showParent(JFrame frame) {
        JPanel panel = (JPanel) frame.getContentPane();
        mostrarSubPanel(panel);
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    public void cambiarPanelHome() {
        NovusUtils.showPanel(pnlPrincipal, "home");
    }

    public void showPanel(String panel) {
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

    void cerrarSubmenu() {
        jSubMenu.setVisible(false);
        cambiarPanelHome();
        System.gc();
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

    public void abrirVentaManual() {
        NovusUtils.beep();
        if (Main.persona == null) {
            mostrarPanelSinTurnos();
        } else {
            NovusConstante.VENTAS_CONTINGENCIA = !NovusConstante.HAY_INTERNET;
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this,
                        NovusConstante.SELECCION_PROMOTOR_FACT_MANUAL);
                seleccionpromotor.setVisible(true);
            } else {
                this.recargarPersona();
                mostrarSubPanel(new VentaManualMenuPanel(this, true, true));
            }
        }
    }

    private void abrirHistorialVentas() {
        if (Main.persona != null) {
            VentasHistorialView ventas = new VentasHistorialView(this, true);
            ventas.setEstadoActulizarDatafono(Boolean.TRUE);
            ventas.vistaretorno();
            ventas.setVisible(true);
        } else {
            mostrarPanelSinTurnos();
        }
    }

    private void asignarCliente() {
        VentasFE venta = new VentasFE(this);
        if (Main.persona != null) {
            venta.setVisible(true);
        } else {
            mostrarPanelSinTurnos();
        }
    }
}

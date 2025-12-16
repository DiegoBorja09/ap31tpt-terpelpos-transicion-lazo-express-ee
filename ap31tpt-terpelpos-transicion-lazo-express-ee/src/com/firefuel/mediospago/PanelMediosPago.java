package com.firefuel.mediospago;

import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverDataMedioPago;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoImage;
import com.WT2.appTerpel.domain.context.Concurrents;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.WT2.appTerpel.domain.valueObject.TransaccionMessageView;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.commons.domain.adapters.IParametrosMensajesBuilder;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.datafonos.HasOngoingSalesInCardReadersPumpStatusUseCase;
import com.application.useCases.datafonos.HasOngoingDatafonSalesUseCase;
import com.application.useCases.sutidores.OcultarInputsFacturaVentaUseCase;
import com.application.useCases.sutidores.GetMovimientoIdDesdeCaraUseCase;
import com.application.useCases.movimientosdetalles.GetByProductoMovimientoDetalleUseCase;
import com.application.useCases.productos.GetProductNameUseCase;
import com.bean.MediosPagosBean;
import com.bean.ProductoBean;
import com.bean.ReciboExtended;
import com.bean.ventaCurso.VentaCursoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.DatafonosDao;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.firefuel.DatafonosView;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import com.firefuel.PanelNotificacion;
import com.firefuel.VentaCursoPlaca;
import com.firefuel.datafonos.VentaEnVivoDatafono;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neo.app.bean.Manguera;
import com.server.api.ServerComandoWS;
import java.awt.CardLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PanelMediosPago extends javax.swing.JPanel {

    SetupDao setupdao;
    SurtidorDao sdao;
    MovimientosDao mdao;
    EquipoDao edao;
    int primerPanel = 1;
    int numeroPanelActual = 1;
    int numeroPanels;
    private RecoverDataMedioPago recoverData;
    private RecoverMedioPagoImage recoverLista;
    ArrayList<MedioPagoImageBean> listMedio;

    private OcultarInputsFacturaVentaUseCase ocultarInputsFacturaVentaUseCase;

    private static final String PANEL_MENSAJES = "pnlMensaje";
    private static final String PANEL_MEDIOS_PAGOS = "pnlMediosPago";

    final RenderPanelsMediosPago render = new RenderPanelsMediosPago();
    Runnable mostrarPrincipal = null;
    Runnable continuar = null;
    Runnable finalizar = null;
    public HasOngoingSalesInCardReadersPumpStatusUseCase hasOngoingSalesInCardReadersPumpStatusUseCase;

    ArrayList<MedioPagoImageBean> listMedios;
    JsonArray datafonos = new JsonArray();
    Manguera manguera;
    boolean seleccionMedios = false;
    boolean isDefault;

    public PanelMediosPago(Runnable mostrarPrincipal,
            Runnable continuar, JsonArray datafonos, Runnable finalizar, Manguera manguera) throws SQLException {
        this.mostrarPrincipal = mostrarPrincipal;
        this.continuar = continuar;
        this.datafonos = datafonos;
        this.finalizar = finalizar;
        this.manguera = manguera;
        this.setupdao = new SetupDao();
        this.sdao = new SurtidorDao();
        this.mdao = new MovimientosDao();
        this.ocultarInputsFacturaVentaUseCase = new OcultarInputsFacturaVentaUseCase();
        FacturacionElectronica fac = new FacturacionElectronica();
        isDefault = fac.isDefaultFe();
        initComponents();
        SingletonMedioPago.ConetextDependecy.getUpdateImagenReferenciaMedioPago().execute();
        SingletonMedioPago.ConetextDependecy.getRecoverMedio().loadMedioPago();
        loadComponents(SingletonMedioPago.ConetextDependecy.getMedioPago().execute());
    }

    public PanelMediosPago(Manguera manguera, boolean seleccionMedios) throws SQLException {
        this.sdao = new SurtidorDao();
        this.mdao = new MovimientosDao();
        this.setupdao = new SetupDao();
        this.edao = new EquipoDao();
        this.manguera = manguera;
        this.seleccionMedios = seleccionMedios;
        this.ocultarInputsFacturaVentaUseCase = new OcultarInputsFacturaVentaUseCase();
        FacturacionElectronica fac = new FacturacionElectronica();
        isDefault = fac.isDefaultFe();
        initComponents();
        cargarDatafonos(edao);
        SingletonMedioPago.ConetextDependecy.getUpdateImagenReferenciaMedioPago().execute();
        recoverMedios(this.manguera.getCara());
    }

    private void recoverMedios(int cara) throws SQLException {
        if (ocultarInputsFacturaVentaUseCase.execute(cara)) {
            loadComponents(SingletonMedioPago.ConetextDependecy.getMedioPagoWithoutGoPass().execute());
        } else {
            SingletonMedioPago.ConetextDependecy.getRecoverMedio().loadMedioPago();
            loadComponents(SingletonMedioPago.ConetextDependecy.getMedioPago().execute());
        }
    }

    private void cargarDatafonos(EquipoDao edao) {
        try {
            datafonos = edao.datafonosInfo();
        } catch (DAOException | SQLException ex) {
            Logger.getLogger(VentaCursoPlaca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadComponents(ArrayList<MedioPagoImageBean> listMediosPago) throws SQLException {
        lblMensajes.setVisible(false);
        pnl.setVisible(false);
        pnlPlacasGopass.setVisible(false);
        jPanel1.setVisible(false);
        
        // VALIDACIÓN GLP: Filtrar medios de pago para ventas GLP
        boolean isGLP = esVentaGLP();
        ArrayList<MedioPagoImageBean> mediosFiltrados = new ArrayList<>();
        
        if (isGLP) {
            // Filtrar: NO incluir App Terpel ni Gopass en la lista
            for (MedioPagoImageBean medio : listMediosPago) {
                if (medio.getId() == NovusConstante.ID_MEDIO_GOPASS || 
                    medio.getDescripcion().equals(MediosPagosDescription.APPTERPEL)) {
                    NovusUtils.printLn("🚫 [GLP] Medio oculto para GLP: " + medio.getDescripcion() + " (ID=" + medio.getId() + ")");
                    // No agregar a la lista filtrada
                } else {
                    mediosFiltrados.add(medio);
                }
            }
        } else {
            // Si no es GLP, usar todos los medios
            mediosFiltrados = listMediosPago;
        }
        
        render.createPanelsMediosPago(mediosFiltrados, pnlContenedorMediosPago, manguera, new ArrayList<>());

        numeroPanels = render.numeroPanel;
        btnBack.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContenedor = new javax.swing.JPanel();
        pnlMediosPago = new javax.swing.JPanel();
        separator = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        logoDevitech = new javax.swing.JLabel();
        separator2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pnl = new javax.swing.JPanel();
        pnlPlacasGopass = new PanelSeleccionPlacas();
        jLabel1 = new javax.swing.JLabel();
        pnlMedios = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnBack = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblBack = new javax.swing.JLabel();
        btnNext = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblNext = new javax.swing.JLabel();
        pnlContenedorMediosPago = new javax.swing.JPanel();
        pnlContinuar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblContinuar = new javax.swing.JLabel();
        pnlAnterior = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnAnterior = new javax.swing.JLabel();
        lblMensajes = new javax.swing.JLabel();
        fndBackground = new javax.swing.JLabel();
        pnlMensajes = new javax.swing.JPanel();
        separator1 = new javax.swing.JLabel();
        backMensajes = new javax.swing.JLabel();
        jTitle1 = new javax.swing.JLabel();
        logoDevitech1 = new javax.swing.JLabel();
        separator3 = new javax.swing.JLabel();
        btnBackMensajes = new javax.swing.JLabel();
        pnlPlantillaMensaje = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblMensaje = new javax.swing.JLabel();
        pnlBtnNo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnNoConfirmacion = new javax.swing.JLabel();
        pnlBtnSi = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnSiConfirmacion = new javax.swing.JLabel();
        fndMensajes = new javax.swing.JLabel(); 
        this.hasOngoingSalesInCardReadersPumpStatusUseCase = new HasOngoingSalesInCardReadersPumpStatusUseCase(new JsonObject());

        setMaximumSize(new java.awt.Dimension(1280, 800));
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setLayout(null);

        pnlContenedor.setLayout(new java.awt.CardLayout());

        pnlMediosPago.setLayout(null);

        separator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMediosPago.add(separator);
        separator.setBounds(80, 10, 10, 68);

        back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backMouseReleased(evt);
            }
        });
        pnlMediosPago.add(back);
        back.setBounds(0, 0, 84, 84);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 0, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("Medios de pago");
        pnlMediosPago.add(jTitle);
        jTitle.setBounds(108, 0, 710, 84);

        logoDevitech.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoDevitech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlMediosPago.add(logoDevitech);
        logoDevitech.setBounds(10, 710, 100, 80);

        separator2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separator2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMediosPago.add(separator2);
        separator2.setBounds(120, 710, 10, 80);

        jPanel1.setOpaque(false);
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1033, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        pnlMediosPago.add(jPanel1);
        jPanel1.setBounds(128, 130, 1033, 280);

        pnl.setOpaque(false);
        pnl.setLayout(null);

        pnlPlacasGopass.setOpaque(false);
        pnlPlacasGopass.setLayout(null);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlPlacasGopass.add(jLabel1);
        jLabel1.setBounds(0, 0, 1033, 280);

        pnl.add(pnlPlacasGopass);
        pnlPlacasGopass.setBounds(0, 0, 1033, 280);

        pnlMediosPago.add(pnl);
        pnl.setBounds(128, 396, 1033, 280);

        pnlMedios.setBackground(new java.awt.Color(242, 241, 247));
        pnlMedios.setRoundBottomLeft(20);
        pnlMedios.setRoundBottomRight(20);
        pnlMedios.setRoundTopLeft(20);
        pnlMedios.setRoundTopRight(20);
        pnlMedios.setLayout(null);

        btnBack.setBackground(new java.awt.Color(162, 164, 176));
        btnBack.setMaximumSize(new java.awt.Dimension(42, 42));
        btnBack.setMinimumSize(new java.awt.Dimension(42, 42));
        btnBack.setPreferredSize(new java.awt.Dimension(42, 42));
        btnBack.setRoundBottomLeft(10);
        btnBack.setRoundBottomRight(10);
        btnBack.setRoundTopLeft(10);
        btnBack.setRoundTopRight(10);

        lblBack.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndBack.png"))); // NOI18N
        lblBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblBackMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout btnBackLayout = new javax.swing.GroupLayout(btnBack);
        btnBack.setLayout(btnBackLayout);
        btnBackLayout.setHorizontalGroup(
            btnBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBack, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );
        btnBackLayout.setVerticalGroup(
            btnBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBack, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        pnlMedios.add(btnBack);
        btnBack.setBounds(42, 254, 42, 42);

        btnNext.setBackground(new java.awt.Color(162, 164, 176));
        btnNext.setMaximumSize(new java.awt.Dimension(42, 42));
        btnNext.setMinimumSize(new java.awt.Dimension(42, 42));
        btnNext.setRoundBottomLeft(10);
        btnNext.setRoundBottomRight(10);
        btnNext.setRoundTopLeft(10);
        btnNext.setRoundTopRight(10);

        lblNext.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndNext.png"))); // NOI18N
        lblNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblNextMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout btnNextLayout = new javax.swing.GroupLayout(btnNext);
        btnNext.setLayout(btnNextLayout);
        btnNextLayout.setHorizontalGroup(
            btnNextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblNext, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );
        btnNextLayout.setVerticalGroup(
            btnNextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblNext, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        pnlMedios.add(btnNext);
        btnNext.setBounds(1152, 254, 42, 42);

        pnlContenedorMediosPago.setOpaque(false);
        pnlContenedorMediosPago.setLayout(new java.awt.CardLayout());
        pnlMedios.add(pnlContenedorMediosPago);
        pnlContenedorMediosPago.setBounds(100, 0, 1040, 553);

        pnlMediosPago.add(pnlMedios);
        pnlMedios.setBounds(25, 123, 1230, 553);

        pnlContinuar.setBackground(new java.awt.Color(255, 255, 255));
        pnlContinuar.setRoundBottomLeft(8);
        pnlContinuar.setRoundBottomRight(8);
        pnlContinuar.setRoundTopLeft(8);
        pnlContinuar.setRoundTopRight(8);
        pnlContinuar.setLayout(null);

        lblContinuar.setBackground(new java.awt.Color(228, 229, 231));
        lblContinuar.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblContinuar.setForeground(new java.awt.Color(186, 12, 47));
        lblContinuar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblContinuar.setText("CONTINUAR");
        lblContinuar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblContinuar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblContinuarMouseReleased(evt);
            }
        });
        pnlContinuar.add(lblContinuar);
        lblContinuar.setBounds(0, 0, 145, 48);

        pnlMediosPago.add(pnlContinuar);
        pnlContinuar.setBounds(1052, 730, 145, 48);

        pnlAnterior.setBackground(new java.awt.Color(255, 255, 255));
        pnlAnterior.setRoundBottomLeft(8);
        pnlAnterior.setRoundBottomRight(8);
        pnlAnterior.setRoundTopLeft(8);
        pnlAnterior.setRoundTopRight(8);
        pnlAnterior.setLayout(null);

        btnAnterior.setBackground(new java.awt.Color(228, 229, 231));
        btnAnterior.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnAnterior.setForeground(new java.awt.Color(186, 12, 47));
        btnAnterior.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAnterior.setText("ANTERIOR");
        btnAnterior.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAnteriorMouseReleased(evt);
            }
        });
        pnlAnterior.add(btnAnterior);
        btnAnterior.setBounds(0, 0, 145, 48);

        pnlMediosPago.add(pnlAnterior);
        pnlAnterior.setBounds(889, 730, 145, 48);

        lblMensajes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblMensajes.setForeground(new java.awt.Color(186, 38, 57));
        lblMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndAlert.png"))); // NOI18N
        lblMensajes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMediosPago.add(lblMensajes);
        lblMensajes.setBounds(230, 726, 560, 50);

        fndBackground.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fndBackground.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndBackground.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndBackground.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlMediosPago.add(fndBackground);
        fndBackground.setBounds(0, 0, 1280, 800);

        pnlContenedor.add(pnlMediosPago, "pnlMediosPago");

        pnlMensajes.setLayout(null);

        separator1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separator1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMensajes.add(separator1);
        separator1.setBounds(80, 10, 10, 68);

        backMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        backMensajes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backMensajesMouseReleased(evt);
            }
        });
        pnlMensajes.add(backMensajes);
        backMensajes.setBounds(0, 0, 84, 84);

        jTitle1.setFont(new java.awt.Font("Terpel Sans", 0, 36)); // NOI18N
        jTitle1.setForeground(new java.awt.Color(255, 255, 255));
        jTitle1.setText("Medios de pago");
        pnlMensajes.add(jTitle1);
        jTitle1.setBounds(108, 0, 710, 84);

        logoDevitech1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoDevitech1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlMensajes.add(logoDevitech1);
        logoDevitech1.setBounds(10, 710, 100, 80);

        separator3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separator3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMensajes.add(separator3);
        separator3.setBounds(120, 710, 10, 80);

        btnBackMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlMensajes.add(btnBackMensajes);
        btnBackMensajes.setBounds(20, 10, 60, 60);

        pnlPlantillaMensaje.setBackground(new java.awt.Color(242, 241, 247));
        pnlPlantillaMensaje.setRoundBottomLeft(26);
        pnlPlantillaMensaje.setRoundBottomRight(26);
        pnlPlantillaMensaje.setRoundTopLeft(26);
        pnlPlantillaMensaje.setRoundTopRight(26);
        pnlPlantillaMensaje.setLayout(null);

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(194, 0, 2));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensaje.setText("Mensaje");
        lblMensaje.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlPlantillaMensaje.add(lblMensaje);
        lblMensaje.setBounds(90, 2, 630, 160);

        pnlBtnNo.setBackground(new java.awt.Color(255, 255, 255));
        pnlBtnNo.setRoundBottomLeft(20);
        pnlBtnNo.setRoundBottomRight(20);
        pnlBtnNo.setRoundTopLeft(20);
        pnlBtnNo.setRoundTopRight(20);

        btnNoConfirmacion.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnNoConfirmacion.setForeground(new java.awt.Color(194, 0, 2));
        btnNoConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNoConfirmacion.setText("NO");
        btnNoConfirmacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnNoConfirmacionMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlBtnNoLayout = new javax.swing.GroupLayout(pnlBtnNo);
        pnlBtnNo.setLayout(pnlBtnNoLayout);
        pnlBtnNoLayout.setHorizontalGroup(
            pnlBtnNoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnNoConfirmacion, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
        );
        pnlBtnNoLayout.setVerticalGroup(
            pnlBtnNoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnNoConfirmacion, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        );

        pnlPlantillaMensaje.add(pnlBtnNo);
        pnlBtnNo.setBounds(190, 192, 173, 54);

        pnlBtnSi.setBackground(new java.awt.Color(255, 255, 255));
        pnlBtnSi.setRoundBottomLeft(20);
        pnlBtnSi.setRoundBottomRight(20);
        pnlBtnSi.setRoundTopLeft(20);
        pnlBtnSi.setRoundTopRight(20);

        btnSiConfirmacion.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnSiConfirmacion.setForeground(new java.awt.Color(194, 0, 2));
        btnSiConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSiConfirmacion.setText("SI");
        btnSiConfirmacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSiConfirmacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSiConfirmacionMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlBtnSiLayout = new javax.swing.GroupLayout(pnlBtnSi);
        pnlBtnSi.setLayout(pnlBtnSiLayout);
        pnlBtnSiLayout.setHorizontalGroup(
            pnlBtnSiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSiConfirmacion, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
        );
        pnlBtnSiLayout.setVerticalGroup(
            pnlBtnSiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSiConfirmacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        );

        pnlPlantillaMensaje.add(pnlBtnSi);
        pnlBtnSi.setBounds(460, 192, 173, 54);

        pnlMensajes.add(pnlPlantillaMensaje);
        pnlPlantillaMensaje.setBounds(230, 250, 810, 290);

        fndMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fndMensajes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndMensajes.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndMensajes.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndMensajes.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlMensajes.add(fndMensajes);
        fndMensajes.setBounds(0, 0, 1280, 800);

        pnlContenedor.add(pnlMensajes, "pnlMensaje");

        add(pnlContenedor);
        pnlContenedor.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void backMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMouseReleased
        regresar();
    }//GEN-LAST:event_backMouseReleased

    private void lblBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseReleased
        backPanel();
    }//GEN-LAST:event_lblBackMouseReleased

    private void lblNextMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNextMouseReleased
        nextPanel();
    }//GEN-LAST:event_lblNextMouseReleased

    private void lblContinuarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblContinuarMouseReleased
        if (render.getIdMedioPago() == 0) {
            mostrarMensaje("Seleccione un medio de pago", 3);
        } else {
            if (render.getIdMedioPago() == NovusConstante.ID_MEDIO_GOPASS
                    && RenderPanelPlacasGopass.getValorPlacaSeleccionada().isEmpty()) {
                mostrarMensaje("Seleccione una placa", 3);
            } else {
                confirmarSeleccionMedioPago();
            }
        }
    }//GEN-LAST:event_lblContinuarMouseReleased

    private void backMensajesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMensajesMouseReleased
        mostrarPanel(PANEL_MEDIOS_PAGOS);
    }//GEN-LAST:event_backMensajesMouseReleased

    private void btnNoConfirmacionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNoConfirmacionMouseReleased
        mostrarPanel(PANEL_MEDIOS_PAGOS);
    }//GEN-LAST:event_btnNoConfirmacionMouseReleased

    private void btnAnteriorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAnteriorMouseReleased
        regresar();
    }//GEN-LAST:event_btnAnteriorMouseReleased

    private void btnSiConfirmacionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSiConfirmacionMouseReleased
        guardarPlacaGopass();
    }//GEN-LAST:event_btnSiConfirmacionMouseReleased

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        evt.consume();
    }//GEN-LAST:event_jPanel1MouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back;
    private javax.swing.JLabel backMensajes;
    private javax.swing.JLabel btnAnterior;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnBack;
    private javax.swing.JLabel btnBackMensajes;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnNext;
    private javax.swing.JLabel btnNoConfirmacion;
    private javax.swing.JLabel btnSiConfirmacion;
    private javax.swing.JLabel fndBackground;
    private javax.swing.JLabel fndMensajes;
    private javax.swing.JLabel jLabel1;
    public static javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jTitle1;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblContinuar;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblMensajes;
    private javax.swing.JLabel lblNext;
    private javax.swing.JLabel logoDevitech;
    private javax.swing.JLabel logoDevitech1;
    public static javax.swing.JPanel pnl;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlAnterior;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlBtnNo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlBtnSi;
    private javax.swing.JPanel pnlContenedor;
    public static javax.swing.JPanel pnlContenedorMediosPago;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlContinuar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlMedios;
    private javax.swing.JPanel pnlMediosPago;
    private javax.swing.JPanel pnlMensajes;
    public static javax.swing.JPanel pnlPlacasGopass;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlPlantillaMensaje;
    private javax.swing.JLabel separator;
    private javax.swing.JLabel separator1;
    private javax.swing.JLabel separator2;
    private javax.swing.JLabel separator3;
    // End of variables declaration//GEN-END:variables

    private void regresar() {
        if (seleccionMedios) {
            volverPrincipalSeleccionMedios();
        } else {
            volverPrincipal();
        }
    }

    private void volverPrincipalSeleccionMedios() {
        limpiarVariables();
        Main.info.cambiarPanelHome();
    }

    private void volverPrincipal() {
        if (mostrarPrincipal != null) {
            limpiarVariables();
            mostrarPrincipal.run();
            mostrarPrincipal = null;
        }
    }

    private void finalizar() {
        if (finalizar != null) {
            limpiarVariables();
            finalizar.run();
            finalizar = null;
        }
    }

    private void nextPanel() {
        if (numeroPanelActual < numeroPanels) {
            numeroPanelActual++;
            CardLayout layout = (CardLayout) pnlContenedorMediosPago.getLayout();
            layout.show(pnlContenedorMediosPago, "pnl" + numeroPanelActual);
            mostrarBotonesNextBack();
        }
    }

    private void backPanel() {
        if (numeroPanelActual != primerPanel) {
            numeroPanelActual--;
            CardLayout layout = (CardLayout) pnlContenedorMediosPago.getLayout();
            layout.show(pnlContenedorMediosPago, "pnl" + numeroPanelActual);
            mostrarBotonesNextBack();
        }
    }

    private void mostrarBotonesNextBack() {
        if (numeroPanelActual == numeroPanels) {
            btnNext.setVisible(false);
            btnBack.setVisible(true);
            return;
        }
        if (numeroPanelActual == primerPanel) {
            btnBack.setVisible(false);
            btnNext.setVisible(true);
            return;
        }
        btnNext.setVisible(true);
        btnBack.setVisible(true);
    }

    private void guardarPlacaGopass() {
        NovusUtils.printLn("**************");
        NovusUtils.printLn("guardarPlacaGopass");
        JsonObject placa = new JsonObject();
        placa.addProperty("placa", RenderPanelPlacasGopass.getValorPlacaSeleccionada());
        sdao.eliminarAtributosVenta(manguera.getCara());
        sdao.updateVentasEncurso(buildReciboVenta(), buildJsonMedioPagoEfectivo(), NovusConstante.ID_TIPO_VENTA_EFECTIVO);
        sdao.updateVentasEncurso(buildReciboVenta(), placa, NovusConstante.ID_TIPO_VENTA_GOPASS);
        showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getId(),
                "/com/firefuel/resources/btOk.png", 
                true, () -> salir(), 
                true, LetterCase.FIRST_UPPER_CASE);
    }

    private void guardarMedioAppTerpel() {
        NovusUtils.printLn("**************");
        NovusUtils.printLn("guardarMedioAppTerpel");
        JsonObject appTerpel = new JsonObject();
        appTerpel.addProperty("isAppTerpel", Boolean.TRUE);
        sdao.eliminarAtributosVenta(manguera.getCara());
        sdao.updateVentasEncurso(buildReciboVenta(), buildJsonMedioPagoEfectivo(), NovusConstante.ID_TIPO_VENTA_EFECTIVO);
        sdao.updateVentasEncurso(buildReciboVenta(), appTerpel, NovusConstante.ID_TIPO_VENTA_APP_TERPEL);
//        showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getId(),
//                "/com/firefuel/resources/btOk.png", true, () -> regresarPrincipal(), true);
    }

    public void regresarPrincipal() {
        if (seleccionMedios) {
            volverPrincipalSeleccionMedios();
        } else {
            finalizar();
        }
    }

    private void salir() {
        String placa = RenderPanelPlacasGopass.getValorPlacaSeleccionada();
        String cliente = RenderPanelPlacasGopass.getNombreCliente();
        ServerComandoWS.setPlacasCliente(manguera.getCara(), cliente, placa);
        if (seleccionMedios) {
            volverPrincipalSeleccionMedios();
        } else {
            finalizar();
        }
    }

    public ReciboExtended buildReciboVenta() {
        ReciboExtended recibo = new ReciboExtended();
        recibo.setSurtidor(manguera.getSurtidor() + "");
        recibo.setManguera(manguera.getId() + "");
        recibo.setCara(manguera.getCara() + "");
        return recibo;
    }

    public JsonObject buildJsonMedioPagoEfectivo() {
        String comprobante = VentaCursoPlaca.jTextField3 != null ? VentaCursoPlaca.jTextField3.getText() : "";
        String placa = RenderPanelPlacasGopass.getValorPlacaSeleccionada();
        String odometro = VentaCursoPlaca.jTextField2 != null ? VentaCursoPlaca.jTextField2.getText() : "";
        JsonObject json = new JsonObject();
        json.addProperty("medio_pago", NovusConstante.ID_EFECTIVO);
        json.addProperty("numero_comprobante", comprobante);
        json.addProperty("placa", placa);
        json.addProperty("odometro", odometro);
        return json;
    }

    static void mostrarPanelPlacas(boolean visible, Manguera manguera, PanelSeleccionPlacas panelPlacas) {
        if (visible) {
            panelPlacas = (PanelSeleccionPlacas) pnlPlacasGopass;
            panelPlacas.loadComponents(manguera);
            jPanel1.setVisible(true);
        } else {
            jPanel1.setVisible(false);
        }
        pnl.setVisible(visible);
        pnlPlacasGopass.setVisible(visible);
    }

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

    public void mostrarMensaje(String mensaje, int tiempo) {
        setTimeout(tiempo, () -> {
            lblMensajes.setVisible(false);
            lblMensajes.setText("");
        });
        lblMensajes.setVisible(true);
        lblMensajes.setText(mensaje);
    }

    public void mostrarPanel(String panel) {
        CardLayout layout = (CardLayout) pnlContenedor.getLayout();
        layout.show(pnlContenedor, panel);
    }

    public void setMensaje(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    public void confirmarSeleccionMedioPago() {
        if (render.getIdMedioPago() == NovusConstante.ID_MEDIO_GOPASS) {
            if (!inFuel(true)) {
                return;
            }
            mostrarPanelConfirmacionVentaGopass();
        } else {
            if (seleccionMedios) {
                if (render.getDescripcionMedioPago().equalsIgnoreCase("APP TERPEL")) {
                    guardarMedioAppTerpel();
                    showMessageAppterpel(
                            TransaccionMessageView.NOTIFICACION_APPTERPEL_STATUS_PUPM,
                            "/com/firefuel/resources/btOk.png",
                            true, () -> regresarPrincipal(),
                            true, LetterCase.FIRST_UPPER_CASE);

                } else {
                    guardarDatosPrefactura();
                }
            } else {
                if (continuar != null) {
                    ServerComandoWS.eliminarPlacaCliente(manguera.getCara());
                    continuar.run();
                    continuar = null;
                }
            }
        }
    }

    public void mostrarPanelConfirmacionVentaGopass() {
        if (!RenderPanelPlacasGopass.getValorPlacaSeleccionada().isEmpty()) {
            setMensaje("<html><center>¿ Confirma que la placa seleccionada es "
                    + RenderPanelPlacasGopass.getValorPlacaSeleccionada() + " ?</center></html>");
            mostrarPanel(PANEL_MENSAJES);
        }
    }

    public void limpiarVariables() {
        render.setIdMedioPago(0);
        RenderPanelPlacasGopass.setValorPlacaSeleccionada("");
    }

    private long idMedioPagoSeleccionado() {
        long idMedio = 1;
        if (render.getIdMedioPago() != 0) {
            idMedio = render.getIdMedioPago();
        }
        return idMedio;
    }

    private void guardarDatosPrefactura() {
        NovusUtils.printLn("**************");
        NovusUtils.printLn("guardarDatosPrefactura");
        VentaCursoBean ventaCursoBean = Main.ventaCursoDAO.atributosVentaCurso(manguera.getCara());
        ServerComandoWS.eliminarPlacaCliente(manguera.getCara());

        String placa = "";
        String odometro = "";
        String comprobante = "";

        if (ventaCursoBean != null) {
            placa = ventaCursoBean.getPlaca();
            odometro = ventaCursoBean.getOdometro();
            comprobante = ventaCursoBean.getNumeroComprobante();
        }

        if (!inFuel(true)) {
            return;
        }
        MediosPagosBean medio = Concurrents.medioPagoImage.get(idMedioPagoSeleccionado());
        NovusUtils.printLn("**************");
        NovusUtils.printLn("medio: " + medio + " descripcion: " + medio.getDescripcion());
        try {
            JsonObject json = new JsonObject();
            json.addProperty("medio_pago", medio.getId());
            json.addProperty("numero_comprobante", comprobante);
            json.addProperty("placa", placa);
            json.addProperty("odometro", odometro);
            ReciboExtended recibo = new ReciboExtended();
            recibo.setSurtidor(manguera.getSurtidor() + "");
            recibo.setManguera(manguera.getId() + "");
            recibo.setCara(manguera.getCara() + "");

            if (!render.getDescripcionMedioPago().equalsIgnoreCase("CON DATAFONO")) {
                sdao.eliminarAtributosVenta(manguera.getCara());
            }

            JsonObject mang = new JsonObject();
            mang.addProperty("surtidor", manguera.getSurtidor());
            mang.addProperty("id", manguera.getId());
            mang.addProperty("cara", manguera.getCara());
            if (medio.getDescripcion().equalsIgnoreCase("CON DATAFONO")) {
                if (datafonos == null) {
                    showMessage("NO HAY DATAFONOS CONFIGURADOS ", "/com/firefuel/resources/btBad.png", true,
                            () -> mostrarPanel(PANEL_MEDIOS_PAGOS), true, LetterCase.FIRST_UPPER_CASE);
                } else {
                    DatafonosDao datafonosDao = new DatafonosDao();
                    if (datafonos.size() == 1) {
                        VentaEnVivoDatafono venta = new VentaEnVivoDatafono();
                        json.addProperty("medio_pago", 1);
                        JsonObject objDatafono = NovusUtils.informacionDatafono(datafonos, mdao);
                        JsonObject respuestaParametros = NovusUtils.validarParametrizacionDatafono(objDatafono, mdao);
                        if (respuestaParametros.get("status").getAsBoolean()) {
                            
                            if (!new HasOngoingDatafonSalesUseCase(objDatafono.get("codigoDatafono").getAsString()).execute()
                                    && !new HasOngoingSalesInCardReadersPumpStatusUseCase(objDatafono).execute()) {
                                showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getCara(), "/com/firefuel/resources/btOk.png", true, () -> volverPrincipalSeleccionMedios(), true, LetterCase.FIRST_UPPER_CASE);
                                venta.datosDatafonosEnCurso(json, mang, objDatafono, Boolean.TRUE, null);
                            } else {
                                showMessage("EXISTE UNA TRANSACCIÓN EN CURSO EN ESTE DATÁFONO ",
                                        "/com/firefuel/resources/btBad.png",
                                        true, () -> mostrarPanel(PANEL_MEDIOS_PAGOS), true, LetterCase.FIRST_UPPER_CASE);
                            }
                        } else {
                            showMessage(respuestaParametros.get("mensaje").getAsString(),
                                    "/com/firefuel/resources/btBad.png", true, () -> mostrarPanel(PANEL_MEDIOS_PAGOS),
                                    true, LetterCase.FIRST_UPPER_CASE);
                        }
                    } else {
                        JsonObject parametros = new JsonObject();
                        json.addProperty("medio_pago", 1);
                        parametros.add("manguera", mang);
                        parametros.add("placa", json);
                        parametros.addProperty("asignarDatos", Boolean.FALSE);
                        parametros.addProperty("ventaEnVivo", Boolean.TRUE);
                        parametros.addProperty("modal", Boolean.TRUE);

                        DatafonosView datafonosView = new DatafonosView(Main.info, manguera, parametros,
                                medio, true, () -> mostrarPanel(PANEL_MEDIOS_PAGOS),
                                () -> volverPrincipalSeleccionMedios());
                        showDialog(datafonosView);
                    }
                }
            } else {
                sdao.updateVentasEncurso(recibo, json, 2);
                showMessage("SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getId(),
                        "/com/firefuel/resources/btOk.png", true, () -> volverPrincipalSeleccionMedios(), true, LetterCase.FIRST_UPPER_CASE);
            }

        } catch (Exception ex) {
            Logger.getLogger(PanelMediosPago.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showMessage(String msj, String ruta, boolean habilitar,
            Runnable runnable, boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    private void showMessageAppterpel(String msj, String ruta, boolean habilitar,
            Runnable runnable, boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().executeAppterpel(parametrosMensajes));
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlContenedor.getLayout();
        pnlContenedor.add("pnl_ext", panel);
        layout.show(pnlContenedor, "pnl_ext");
        
        Async(() -> {
            // Código que quieres ejecutar después del retraso (tarea principal)
        }, 3, () -> {
                    InfoViewController vista = InfoViewController.getInstance();
                    this.setVisible(false);
 
        });
    }

    private ScheduledFuture<?> Async(Runnable runnable, int tiempo, Runnable alTerminar) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            ScheduledFuture<?> future = executor.schedule(() -> {
                runnable.run();
                // Al finalizar el hilo, ejecuta el callback si no es nulo
                if (alTerminar != null) {
                    SwingUtilities.invokeLater(alTerminar); // Para actualizar la UI
                }
            }, tiempo, TimeUnit.SECONDS);
            return future;
    }
    
    
    
    
    private boolean inFuel(boolean mostrarMensaje) {
        SurtidorDao dao = new SurtidorDao();
        int estadoPump = dao.getStatusHose(manguera.getId());
        if (estadoPump != 3) {
            if (mostrarMensaje) {
                mostrarMensaje("VENTA FINALIZADA", 3);
            }
            setTimeout(3, () -> finalizar());
            return false;
        }
        return true;
    }

    /**
     * Verifica si la venta actual es de tipo GLP basándose en el producto del movimiento
     * @return true si es GLP, false en caso contrario
     */
    private boolean esVentaGLP() {
        try {
            if (this.manguera != null && this.manguera.getCara() > 0) {
                NovusUtils.printLn("🔍 [GLP] Validando producto para cara: " + this.manguera.getCara());
                
                // Obtener el ID del producto desde la consulta
                long productoId = new GetMovimientoIdDesdeCaraUseCase().execute(this.manguera.getCara());
                NovusUtils.printLn("🔍 [GLP] Producto ID obtenido: " + productoId);
                
                if (productoId > 0) {
                    // Obtener la descripción del producto
                    String descripcionProducto = new GetProductNameUseCase(productoId).execute();
                    NovusUtils.printLn("🔍 [GLP] Descripción producto: '" + descripcionProducto + "'");
                    
                    if (descripcionProducto != null && !descripcionProducto.trim().isEmpty()) {
                        // Verificar si la descripción contiene "GLP" (case insensitive)
                        boolean esGLP = descripcionProducto.toUpperCase().contains("GLP");
                        NovusUtils.printLn("🔍 [GLP] ¿Es producto GLP? " + esGLP);
                        return esGLP;
                    } else {
                        NovusUtils.printLn("⚠️ [GLP] Descripción de producto vacía o nula");
                    }
                } else {
                    NovusUtils.printLn("⚠️ [GLP] ID de producto no válido: " + productoId);
                }
            } else {
                NovusUtils.printLn("⚠️ [GLP] Manguera o cara no válida");
            }
            
            NovusUtils.printLn("⚠️ [GLP] No se pudo determinar el tipo de producto - asumiendo NO es GLP");
            return false;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ [ERROR GLP] Error al verificar tipo de producto: " + e.getMessage());
            return false;
        }
    }

}

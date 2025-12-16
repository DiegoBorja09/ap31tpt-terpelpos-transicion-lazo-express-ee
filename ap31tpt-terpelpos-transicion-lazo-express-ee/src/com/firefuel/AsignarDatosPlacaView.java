package com.firefuel;

import com.WT2.appTerpel.domain.valueObject.TransaccionMessageView;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.movimientos.BuscarTransaccionDatafonoCaseUse;
import com.bean.AsignacionClienteBean;
import com.bean.BonoViveTerpel;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.FacturacionElectronicaDao;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;


import com.firefuel.utils.ImageCache;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import java.awt.CardLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.*;

import teclado.view.common.TecladoExtendido;
import com.application.useCases.movimientocliente.ObtenerIdTransmisionDesdeMovimientoUseCase;

public class AsignarDatosPlacaView extends javax.swing.JPanel {
    ObtenerIdTransmisionDesdeMovimientoUseCase obtenerIdTransmisionDesdeMovimientoUseCase = new ObtenerIdTransmisionDesdeMovimientoUseCase(0L);

    SurtidorDao surtidorDao;
    MovimientosBean movimientosBean;
    MovimientosDao movimientosDao;
    InfoViewController parent;
    JsonObject atributosVenta = new JsonObject();
    Runnable cerrar = null;
    ReciboExtended reciboRec;
    DecimalFormat decimalFormat;
    private static ClienteFacturaElectronica viewFe;
    private static MedioPagosConfirmarViewController viewMedios;
    long idTransmision = 0l;
    ArrayList<MediosPagosBean> mediosPagoVenta = new ArrayList<>();
    Runnable refrescar;
    private boolean hayDatafono;
    JsonArray bonosObtenidos = new JsonArray();
    MedioPagosConfirmarViewController medioPagosConfirmarViewController;
    ClienteFacturaElectronica clienteFacturaElectronica;
    ValidacionBonosViveTerpel validadorViveTerpel;
    FacturacionElectronicaDao electronicaDao;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> tareaProgramada;

    public AsignarDatosPlacaView(
            InfoViewController parent,
            Recibo recibo,
            MovimientosBean movimiento,
            ReciboExtended reciboRec,
            Runnable runnable,
            Runnable refrecar) {
        this.parent = parent;
        this.cerrar = runnable;
        this.reciboRec = reciboRec;
        this.refrescar = refrecar;
        this.surtidorDao = new SurtidorDao();
        this.movimientosDao = new MovimientosDao();
        this.movimientosBean = movimiento;
        this.clienteFacturaElectronica = new ClienteFacturaElectronica();
        this.validadorViveTerpel = new ValidacionBonosViveTerpel();
        this.electronicaDao = new FacturacionElectronicaDao();
        this.decimalFormat = new DecimalFormat(NovusConstante.FORMAT_MONEY);
        this.medioPagosConfirmarViewController = new MedioPagosConfirmarViewController(parent, true, this.movimientosBean, false, false, reciboRec, this::continuarProceso, this::mostrarMenuPrincipal, true, this::iniciarProcesoSinDatafono);
        initComponents();
        AsignacionClienteBean.getDatosCliente().clear();
        setRecibo(recibo);
        setViews();
    }

    static void setViewMedios(MedioPagosConfirmarViewController medios) {
        AsignarDatosPlacaView.viewMedios = medios;
    }

    static void setViewFe(ClienteFacturaElectronica view) {
        AsignarDatosPlacaView.viewFe = view;
    }

    private void setViews() {
        reiniciarEstadoMedios();
        setViewMedios(this.medioPagosConfirmarViewController);
        long numeroVenta = obtenerNumeroVentaDesdeRecibo();
        boolean asignarDatos = validarAsignacionDatos();
        ClienteFacturaElectronica cliente = construirClienteFactura(numeroVenta, asignarDatos);
        prepararClienteUI(cliente);
        setViewFe(cliente);
    }

    private void reiniciarEstadoMedios() {
        MedioPagosConfirmarViewController.anulacion = Boolean.FALSE;
    }

    private long obtenerNumeroVentaDesdeRecibo() {
        if (reciboRec.getAtributos() != null
                && reciboRec.getAtributos().has("idTransmision")
                && !reciboRec.getAtributos().get("idTransmision").isJsonNull()) {
            return reciboRec.getAtributos().get("idTransmision").getAsLong();
        } else {
            Logger.getLogger(getClass().getName()).warning("'idTransmision' no encontrado en atributos del recibo.");
            return 0;
        }
    }

    private boolean validarAsignacionDatos() {
        return !new BuscarTransaccionDatafonoCaseUse(this.movimientosBean.getId()).execute();
        
    }

    private ClienteFacturaElectronica construirClienteFactura(long numeroVenta, boolean asignarDatos) {
       /*
        ClienteFacturaElectronica cliente = new ClienteFacturaElectronica(
                parent,
                true,
                numeroVenta,
                true,
                this::mostrarMenuPrincipal,
                this::continuarConfirmarMediosView,
                asignarDatos,
                true,
                true,
                this.movimientosBean,
                mediosPagoVenta
        );
 */

        this.clienteFacturaElectronica =  new ClienteFacturaElectronica(
                parent,
                true,
                numeroVenta,
                true,
                this::mostrarMenuPrincipal,
                this::continuarConfirmarMediosView,
                asignarDatos,
                true,
                true,
                this.movimientosBean,
                mediosPagoVenta
        );
        this.clienteFacturaElectronica.transaccionProceso = this.movimientosDao.buscarAppTerpelRechazadaOnoExiste(this.movimientosBean.getId());
        this.clienteFacturaElectronica.isProcesoRechazada = this.clienteFacturaElectronica.transaccionProceso.esRechazadoOnoExiste();
        this.clienteFacturaElectronica.regresarHIstotial = this::cerrarVenta;
        return this.clienteFacturaElectronica;
    }

    private void prepararClienteUI(ClienteFacturaElectronica cliente) {
        SwingUtilities.invokeLater(() -> {
            cliente.jTextField1.requestFocusInWindow();
        });
    }


    /*
    private void setViews() {

        MedioPagosConfirmarViewController.anulacion = Boolean.FALSE;
        setViewMedios(this.medioPagosConfirmarViewController);

        long numeroVenta = 0;
        if (reciboRec.getAtributos() != null
                && reciboRec.getAtributos().has("idTransmision")
                && !reciboRec.getAtributos().get("idTransmision").isJsonNull()) {

            numeroVenta = reciboRec.getAtributos().get("idTransmision").getAsLong();
        } else {
            System.err.println("⚠ 'idTransmision' no encontrado en reciboRec.getAtributos()");
        }
        boolean asignarDatos = !this.movimientosDao.buscarTransaccionDatafono(this.movimientosBean.getId());

        ClienteFacturaElectronica cliente = new ClienteFacturaElectronica(parent, true, numeroVenta, true, this::mostrarMenuPrincipal, this::continuarConfirmarMediosView, asignarDatos, true, true, this.movimientosBean, mediosPagoVenta);
        cliente.transaccionProceso = this.movimientosDao.buscarAppTerpelRechazadaOnoExiste(this.movimientosBean.getId());
        cliente.isProcesoRechazada = cliente.transaccionProceso.esRechazadoOnoExiste();
        cliente.regresarHIstotial = this::cerrarVenta;
        cliente.jTextField1.requestFocus();
        cliente.jTextField1.requestFocusInWindow();
        setViewFe(cliente);
    } */

    /*
    private void setRecibo(Recibo recibo) {
        boolean isEspecial = false;

        if (recibo.getAtributos() != null && !recibo.getAtributos().isJsonNull()) {
            JsonObject atributos = recibo.getAtributos();

            if (atributos.has("is_especial") && !atributos.get("is_especial").isJsonNull()) {
                isEspecial = atributos.get("is_especial").getAsBoolean();
            }

            if (atributos.has("tipo") && "014".equals(atributos.get("tipo").getAsString())) {
                isEspecial = true;
            }
        }

        datosPlaca(recibo, isEspecial);

        atributosVenta = this.movimientosDao.obtenerAtributosVenta(this.movimientosBean.getId());

        if (this.movimientosBean != null
                && this.movimientosBean.getAtributos() != null
                && this.movimientosBean.getAtributos().has("idTransmision")
                && !this.movimientosBean.getAtributos().get("idTransmision").isJsonNull()) {

            // ✅ Ahora sí asignamos idTransmision antes de usarlo
            idTransmision = this.movimientosBean.getAtributos().get("idTransmision").getAsLong();

            // Estado 4 → Transmisión antes de la vista
            this.movimientosDao.actualizarEstadoTransmision(4, idTransmision);
            this.movimientosDao.actualizarEstadoMovimientosClientes(4, idTransmision);
        } else {
            System.err.println("⚠ 'idTransmision' no disponible en movimiento o nulo");
        }
    }
 */

    private void setRecibo(Recibo recibo) {
        boolean isEspecial = esReciboEspecial(recibo);
        datosPlaca(recibo, isEspecial);

        this.atributosVenta = this.movimientosDao.obtenerAtributosVenta(movimientosBean.getId());

        if (tieneIdTransmisionValido(movimientosBean)) {
            this.idTransmision = this.movimientosBean.getAtributos().get("idTransmision").getAsLong();
            actualizarEstadosTransmision(idTransmision);
        } else {
            Logger.getLogger(getClass().getName()).warning("'idTransmision' no disponible en movimiento o nulo");
        }
    }

    private boolean esReciboEspecial(Recibo recibo) {
        JsonObject atributos = recibo.getAtributos();
        if (atributos == null || atributos.isJsonNull()) return false;

        boolean isEspecial = atributos.has("is_especial") && !atributos.get("is_especial").isJsonNull()
                && atributos.get("is_especial").getAsBoolean();

        if (atributos.has("tipo") && "014".equals(atributos.get("tipo").getAsString())) {
            isEspecial = true;
        }

        return isEspecial;
    }

    private boolean tieneIdTransmisionValido(MovimientosBean movimientos) {
        return movimientos != null &&
                movimientos.getAtributos() != null &&
                movimientos.getAtributos().has("idTransmision") &&
                !movimientos.getAtributos().get("idTransmision").isJsonNull();
    }

    private void actualizarEstadosTransmision(long id) {
        movimientosDao.actualizarEstadoTransmision(4, id);
        movimientosDao.actualizarEstadoMovimientosClientes(4, id);
    }



    /*
    public void datosPlaca(Recibo recibo, boolean isEspecial) {
        // Mostrar el valor total
        jlValor.setText("$ " + this.decimalFormat.format(recibo.getTotal()));

        // Unidad de medida segura
        String unidad = "GL";
        if (recibo.getAtributos() != null
                && recibo.getAtributos().has("medida")
                && !recibo.getAtributos().get("medida").isJsonNull()) {
            String medida = recibo.getAtributos().get("medida").getAsString();
            if (!"UNDEFINED".equalsIgnoreCase(medida)) {
                unidad = medida.toUpperCase();
            }
        }

        // Mostrar cantidad con unidad
        jlCantidad.setText(String.format("%.3f", recibo.getCantidadFactor()) + " " + unidad);

        // Validar y mostrar el número del vehículo
        if (recibo.getAtributos() != null
                && recibo.getAtributos().has("vehiculo_numero")
                && !recibo.getAtributos().get("vehiculo_numero").isJsonNull()) {

            String vehiculoNumero = recibo.getAtributos().get("vehiculo_numero").getAsString();

            if (vehiculoNumero != null && !vehiculoNumero.trim().isEmpty()) {
                jnumero.setText(vehiculoNumero.trim());
                jnumero.setEnabled(false);
            } else {
                System.out.println("⚠ 'vehiculo_numero' está presente pero vacío o espacio en blanco.");
            }
        } else {
            System.out.println("⚠ 'vehiculo_numero' no está disponible o es null.");
        }

        // Logs de depuración
        System.out.println("→ Validando atributos:");
        System.out.println("atributos: " + recibo.getAtributos());

        // Mostrar placa si aplica
        if ((recibo.getPlaca() != null && !recibo.getPlaca().trim().isEmpty()) || isEspecial) {
            jplaca.setText(recibo.getPlaca());
            jplaca.setEnabled(false);

            if (recibo.getOdometro() != null) {
                jkms.setText(recibo.getOdometro());
                jkms.setEnabled(false);
            }

            if (recibo.getVoucher() != null) {
                jorden.setText(recibo.getVoucher());
                jorden.setEnabled(false);
            }
        }
    }

     */

    public void datosPlaca(Recibo recibo, boolean isEspecial) {
        mostrarValorTotal(recibo);
        mostrarCantidadConUnidad(recibo);
        mostrarNumeroVehiculo(recibo);
        mostrarInfoVehiculoSiAplica(recibo, isEspecial);
    }

// --- Métodos privados ---

    private void mostrarValorTotal(Recibo recibo) {
        jlValor.setText("$ " + decimalFormat.format(recibo.getTotal()));
    }

    private void mostrarCantidadConUnidad(Recibo recibo) {
        String unidad = obtenerUnidadDeMedida(recibo);
        jlCantidad.setText(String.format("%.3f", recibo.getCantidadFactor()) + " " + unidad);
    }

    private String obtenerUnidadDeMedida(Recibo recibo) {
        JsonObject atributos = recibo.getAtributos();
        if (atributos != null && atributos.has("medida") && !atributos.get("medida").isJsonNull()) {
            String medida = atributos.get("medida").getAsString();
            if (!"UNDEFINED".equalsIgnoreCase(medida)) {
                return medida.toUpperCase();
            }
        }
        return "GL";
    }

    private void mostrarNumeroVehiculo(Recibo recibo) {
        String vehiculoNumero = getStringAtributo(recibo, "vehiculo_numero");

        if (!vehiculoNumero.isEmpty()) {
            jnumero.setText(vehiculoNumero);
            jnumero.setEnabled(false);
        } else {
            Logger.getLogger(getClass().getName()).info("'vehiculo_numero' no disponible o vacío.");
        }
    }

    private void mostrarInfoVehiculoSiAplica(Recibo recibo, boolean isEspecial) {
        if ((recibo.getPlaca() != null && !recibo.getPlaca().trim().isEmpty()) || isEspecial) {
            jplaca.setText(recibo.getPlaca());
            jplaca.setEnabled(false);

            if (recibo.getOdometro() != null) {
                jkms.setText(recibo.getOdometro());
                jkms.setEnabled(false);
            }

            if (recibo.getVoucher() != null) {
                jorden.setText(recibo.getVoucher());
                jorden.setEnabled(false);
            }
        }
    }

    // --- Utilitario para obtener Strings seguros de atributos JSON ---
    private String getStringAtributo(Recibo recibo, String key) {
        JsonObject atributos = recibo.getAtributos();
        if (atributos != null && atributos.has(key) && !atributos.get(key).isJsonNull()) {
            return atributos.get(key).getAsString().trim();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new javax.swing.JPanel();
        pnlPlaca = new javax.swing.JPanel();
        jNotificacion = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jplaca = new javax.swing.JTextField();
        jkms = new javax.swing.JTextField();
        jorden = new javax.swing.JTextField();
        jnumero = new javax.swing.JTextField();
        jlValor = new javax.swing.JLabel();
        jlCantidad = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jBack = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        btnCancelarPlaca = new javax.swing.JLabel();
        btnContinuarPlaca = new javax.swing.JLabel();
        jPanel1 = new TecladoExtendido();
        jBackground = new javax.swing.JLabel();
        pnlMensaje = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        lblFondo = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1280, 800));
        setLayout(null);

        pnlContainer.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlContainer.setLayout(new java.awt.CardLayout());

        pnlPlaca.setOpaque(false);
        pnlPlaca.setLayout(null);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnlPlaca.add(jNotificacion);
        jNotificacion.setBounds(510, 10, 750, 60);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(186, 12, 47));
        jLabel12.setText("VALOR:");
        pnlPlaca.add(jLabel12);
        jLabel12.setBounds(130, 100, 220, 50);

        jplaca.setBackground(new java.awt.Color(238, 238, 238));
        jplaca.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jplaca.setBorder(null);
        jplaca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jplacaFocusGained(evt);
            }
        });
        jplaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jplacaKeyTyped(evt);
            }
        });
        pnlPlaca.add(jplaca);
        jplaca.setBounds(530, 136, 290, 45);

        jkms.setBackground(new java.awt.Color(238, 238, 238));
        jkms.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jkms.setBorder(null);
        jkms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jkmsKeyTyped(evt);
            }
        });
        pnlPlaca.add(jkms);
        jkms.setBounds(526, 210, 300, 45);

        jorden.setBackground(new java.awt.Color(238, 238, 238));
        jorden.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jorden.setBorder(null);
        jorden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jordenFocusGained(evt);
            }
        });
        jorden.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jordenKeyTyped(evt);
            }
        });
        pnlPlaca.add(jorden);
        jorden.setBounds(522, 278, 300, 45);

        jnumero.setBackground(new java.awt.Color(238, 238, 238));
        jnumero.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jnumero.setBorder(null);
        jnumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jnumeroKeyTyped(evt);
            }
        });
        pnlPlaca.add(jnumero);
        jnumero.setBounds(946, 208, 210, 45);

        jlValor.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jlValor.setForeground(new java.awt.Color(102, 102, 102));
        jlValor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlValor.setText("$ 10.000.000");
        pnlPlaca.add(jlValor);
        jlValor.setBounds(130, 150, 230, 50);

        jlCantidad.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jlCantidad.setForeground(new java.awt.Color(102, 102, 102));
        jlCantidad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlCantidad.setText("0.000 gl");
        pnlPlaca.add(jlCantidad);
        jlCantidad.setBounds(130, 260, 230, 50);

        jLabel9.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(186, 12, 47));
        jLabel9.setText("CANTIDAD:");
        pnlPlaca.add(jLabel9);
        jLabel9.setBounds(130, 210, 220, 50);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(186, 12, 47));
        jLabel4.setText("ORDEN:");
        pnlPlaca.add(jLabel4);
        jLabel4.setBounds(410, 270, 110, 50);

        jLabel11.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(186, 12, 47));
        jLabel11.setText("PLACA:");
        pnlPlaca.add(jLabel11);
        jLabel11.setBounds(410, 130, 110, 50);

        jLabel8.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("NO:");
        pnlPlaca.add(jLabel8);
        jLabel8.setBounds(860, 210, 60, 40);

        jBack.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jBack.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_atras.png"));
        jBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jBackMouseReleased(evt);
            }
        });
        pnlPlaca.add(jBack);
        jBack.setBounds(20, 10, 70, 71);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(186, 12, 47));
        jLabel7.setText("KMS:");
        pnlPlaca.add(jLabel7);
        jLabel7.setBounds(410, 200, 110, 50);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ASIGNAR PLACA");
        pnlPlaca.add(jLabel3);
        jLabel3.setBounds(150, 0, 700, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlPlaca.add(jLabel27);
        jLabel27.setBounds(110, 0, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        pnlPlaca.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlPlaca.add(jLabel29);
        jLabel29.setBounds(110, 710, 10, 80);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlPlaca.add(jLabel30);
        jLabel30.setBounds(1140, 710, 10, 80);

        btnCancelarPlaca.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCancelarPlaca.setForeground(new java.awt.Color(153, 0, 0));
        btnCancelarPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCancelarPlaca.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/boton-blanco-2.png"));
        btnCancelarPlaca.setText("CANCELAR");
        btnCancelarPlaca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarPlaca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnCancelarPlacaMouseReleased(evt);
            }
        });
        pnlPlaca.add(btnCancelarPlaca);
        btnCancelarPlaca.setBounds(570, 720, 270, 60);

        btnContinuarPlaca.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnContinuarPlaca.setForeground(new java.awt.Color(153, 0, 0));
        btnContinuarPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnContinuarPlaca.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/boton-blanco-2.png"));
        btnContinuarPlaca.setText("SIGUIENTE");
        btnContinuarPlaca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContinuarPlaca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnContinuarPlacaMouseReleased(evt);
            }
        });
        pnlPlaca.add(btnContinuarPlaca);
        btnContinuarPlaca.setBounds(860, 720, 270, 60);
        pnlPlaca.add(jPanel1);
        jPanel1.setBounds(100, 358, 1080, 340);

        jBackground.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jBackground.setIcon(ImageCache.getImage("/com/firefuel/resources/fndAsignarPlaca.png"));
        pnlPlaca.add(jBackground);
        jBackground.setBounds(0, 0, 1280, 800);

        pnlContainer.add(pnlPlaca, "pnlPrincipal");

        pnlMensaje.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 3, 3));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/boton-blanco-1.png"));
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        pnlMensaje.add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 10, 290, 70));

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        jMensaje.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMensaje.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, 710, 390));

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(ImageCache.getImage("/com/firefuel/resources/btOk.png"));
        jIcono.setToolTipText("");
        pnlMensaje.add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 330, 360));

        lblFondo.setIcon(ImageCache.getImage("/com/firefuel/resources/fndRumbo.png"));
        pnlMensaje.add(lblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        pnlContainer.add(pnlMensaje, "pnlMensaje");

        add(pnlContainer);
        pnlContainer.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void jBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBackMouseReleased
        cerrar();
    }//GEN-LAST:event_jBackMouseReleased

    private void jplacaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jplacaFocusGained
        desactivarCaratecresEspeciales();
        NovusUtils.deshabilitarCopiarPegar(jplaca);
    }//GEN-LAST:event_jplacaFocusGained

    private void jplacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jplacaKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, jplaca, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jplacaKeyTyped

    private void jkmsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jkmsKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jkms, 12, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jkmsKeyTyped

    private void jordenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jordenFocusGained
        NovusUtils.deshabilitarCopiarPegar(jorden);
    }//GEN-LAST:event_jordenFocusGained

    private void jordenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jordenKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jorden, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jordenKeyTyped

    private void jnumeroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jnumeroKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jnumero, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jnumeroKeyTyped

    private void btnCancelarPlacaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarPlacaMouseReleased
        cerrar();
    }//GEN-LAST:event_btnCancelarPlacaMouseReleased

    private void btnContinuarPlacaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContinuarPlacaMouseReleased
        vistaAsignarClienteView();
    }//GEN-LAST:event_btnContinuarPlacaMouseReleased

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        cerrarMensaje();
    }//GEN-LAST:event_jCerrarMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnCancelarPlaca;
    private javax.swing.JLabel btnContinuarPlaca;
    private javax.swing.JLabel jBack;
    private javax.swing.JLabel jBackground;
    public static javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jkms;
    private javax.swing.JLabel jlCantidad;
    private javax.swing.JLabel jlValor;
    private javax.swing.JTextField jnumero;
    private javax.swing.JTextField jorden;
    public static javax.swing.JTextField jplaca;
    private javax.swing.JLabel lblFondo;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JPanel pnlMensaje;
    private javax.swing.JPanel pnlPlaca;
    // End of variables declaration//GEN-END:variables

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlContainer.getLayout();
        pnlContainer.add("pnl_ext", panel);
        layout.show(pnlContainer, "pnl_ext");
    }

    private void desactivarCaratecresEspeciales() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel1;
        teclado.deshabilitarCaracteresEspeciales(false);
    }

    private void mostrarMenuPrincipal() {
        NovusConstante.ventanaFE = Boolean.FALSE;
        CardLayout layout = (CardLayout) pnlContainer.getLayout();
        layout.show(pnlContainer, "pnlPrincipal");
    }

    private void continuarConfirmarMediosView() {
        NovusConstante.ventanaFE = Boolean.FALSE;
        asignarDatosPlaca(reciboRec.getNumero(), jplaca.getText(), jnumero.getText(), jkms.getText(), jorden.getText());
        showDialog(viewMedios);
    }

    private void devolverDatafonos() {
        showDialog(viewMedios);
    }

    private void continuarProcesoFe() {
        NovusConstante.ventanaFE = Boolean.TRUE;
        viewFe.limpiarComponentes();
        viewFe.cerrarTodo = this::cerrarVenta;
        viewFe.devolverDatafonos = this::devolverDatafonos;
        showDialog(viewFe);
        long transmisionId = getIdTransmisionRobusto();
        if (transmisionId == 0L) {
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimientosBean.getId());
            transmisionId = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
        }

        NovusUtils.printLn("enviando el id de transmision en el proceso con datafono-> " + transmisionId);
        viewFe.enviarPagoAdquiriente(getIdTransmisionRobusto());
    }

    private void continuarProcesoSinDatafono() {
        if (viewMedios.anulacion) {
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimientosBean.getId());
            long transmisionId = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
            NovusUtils.printLn("✅ ID de transmisión obtenido para movimiento " + this.movimientosBean.getId() + ": " + transmisionId);
            viewFe.updateInfoCliente(transmisionId);
            NovusUtils.printLn("enviando el id de transmision en el procesocontinuarProcesoSinDatafono() para anulacion  " + transmisionId);
            //viewFe.updateInfoCliente(transmisionId);
            Runnable ucerrar = () -> {
                cerrarVenta();
            };
            showMessage("Se enviara factura electronica una vez se confirme la anulacion en el datafono".toUpperCase(),
                    "/com/firefuel/resources/btOk.png",
                    true, ucerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            NovusUtils.printLn("****************************");
            NovusUtils.printLn("Continuar Proceso sin Datafono");
            NovusUtils.printLn("****************************");
            //viewMedios.sendMedioPago();
            //long transmisionId = obtenerIdTransmisionSeguro();
            long transmisionId = getIdTransmisionRobusto();
            NovusUtils.printLn("enviando el id de transmision en el proceso sin datafono-> " + transmisionId);
            viewFe.updateInfoCliente(transmisionId);
            viewMedios.mediosPagoVenta.clear();
            if (viewFe.isAppTerpelPendiente) {
                mostarMensaje(TransaccionMessageView.NOTIFICIACION_APPTERPEL, NovusConstante.TIEMPO_MENSAJE_APPTERPEL);
            } else {
                mostarMensaje("Factura electrónica enviada");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void continuarProceso() {
        Gson gson = new Gson();
        bonosObtenidos = new JsonArray();
        ArrayList<MediosPagosBean> mediosDePagoVenta = (ArrayList<MediosPagosBean>) viewMedios.mediosPagoVenta.clone();
        JsonObject info = gson.toJsonTree(AsignacionClienteBean.getDatosCliente()).getAsJsonObject();
        JsonArray medios = info.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();
        JsonArray bonosVenta = this.movimientosDao.getBonosVenta(this.movimientosBean.getId());
        JsonArray bonosArray = new JsonArray();
        boolean isBonoVive = false;
        boolean isDatafono = false;
        setIsDatafono(false);
        for (JsonElement medio : medios) {
            JsonObject medioIt = medio.getAsJsonObject();
            if (medioIt.get("ct_medios_pagos_id").getAsLong() == 20000) {
                isBonoVive = true;
            }
            if (medioIt.get("descripcion").getAsString().toUpperCase().equals("CON DATAFONO")) {
                isDatafono = true;
                setIsDatafono(true);
            }
        }
        viewFe.sethayDatafonos(isDatafono);
        if (isBonoVive && isDatafono && !MedioPagosConfirmarViewController.bonosValidados) {
            showMessage("REDIMIENDO BONOS, POR FAVOR ESPERE....",
                    "/com/firefuel/resources/loader_fac.gif",
                    false, null,
                    false, LetterCase.FIRST_UPPER_CASE);
            for (MediosPagosBean medio : mediosDePagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    long salesForceIdMp = surtidorDao.getCodigoSalesForceMP(medio.getId());
                    for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                        if (!existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                            JsonObject bonoViveTerpelMP = new JsonObject();
                            bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                            bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                            bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                            bonosArray.add(bonoViveTerpelMP);
                        }
                    }
                }
            }
            bonosObtenidos = bonosArray;
            setTimeout(1, () -> {
                boolean isDatafonoVenta = getIsDatafono();
                JsonObject respuestaReclamacion = this.validadorViveTerpel.ReclamacionBonoViveTerpel(reciboRec, mediosDePagoVenta, this.movimientosBean.getId(), bonosObtenidos);
                JsonObject respuesta = this.validadorViveTerpel.procesamientRespuestaReclamacion(respuestaReclamacion, this.movimientosBean.getId(), bonosObtenidos);
                procesarRespuestaBonos(respuesta, isDatafonoVenta);
            });

        } else if (isDatafono) {
            continuarProcesoFe();
        } else {
            continuarProcesoSinDatafono();
        }
    }

    public void iniciarProcesoSinDatafono() {
        Gson gson = new Gson();
        bonosObtenidos = new JsonArray();
        @SuppressWarnings("unchecked")
        ArrayList<MediosPagosBean> mediosDePagoVenta = (ArrayList<MediosPagosBean>) viewMedios.mediosPagoVenta.clone();
        JsonObject info = gson.toJsonTree(AsignacionClienteBean.getDatosCliente()).getAsJsonObject();
        JsonArray medios = info.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();
        JsonArray bonosVenta = this.movimientosDao.getBonosVenta(this.movimientosBean.getId());
        JsonArray bonosArray = new JsonArray();
        boolean isBonoVive = false;
        setIsDatafono(false);
        for (JsonElement medio : medios) {
            JsonObject medioIt = medio.getAsJsonObject();
            if (medioIt.get("ct_medios_pagos_id").getAsLong() == 20000) {
                isBonoVive = true;
            }
        }
        if (isBonoVive && !MedioPagosConfirmarViewController.bonosValidados) {
            showMessage("REDIMIENDO BONOS, POR FAVOR ESPERE....",
                    "/com/firefuel/resources/loader_fac.gif",
                    false, null,
                    false, LetterCase.FIRST_UPPER_CASE);
            for (MediosPagosBean medio : mediosDePagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    long salesForceIdMp = surtidorDao.getCodigoSalesForceMP(medio.getId());
                    for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                        if (!existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                            JsonObject bonoViveTerpelMP = new JsonObject();
                            bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                            bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                            bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                            bonosArray.add(bonoViveTerpelMP);
                        }
                    }
                }
            }
            bonosObtenidos = bonosArray;

            setTimeout(1, () -> {
                JsonObject respuestaReclamacion = this.validadorViveTerpel.ReclamacionBonoViveTerpel(reciboRec, mediosDePagoVenta, this.movimientosBean.getId(), bonosObtenidos);
                JsonObject respuesta =  this.validadorViveTerpel.procesamientRespuestaReclamacion(respuestaReclamacion, this.movimientosBean.getId(), bonosObtenidos);
                procesarRespuestaBonos(respuesta, false);
            });
        } else {
            continuarProcesoSinDatafono();
        }
    }

    public void setIsDatafono(boolean isDatafono) {
        this.hayDatafono = isDatafono;
    }

    public boolean getIsDatafono() {
        return hayDatafono;
    }

    public void procesarRespuestaBonos(JsonObject respuesta, boolean isDatafono) {
        NovusUtils.printLn("procesarRespuestaBonos(JsonObject respuesta, boolean isDatafono)");
        String mensaje = respuesta.get("mensaje").getAsString();
        if (respuesta.get("aprobado").getAsBoolean()) {
            Runnable runnable = () -> {
                if (isDatafono) {
                    continuarProcesoFe();
                } else {
                    continuarProcesoSinDatafono();
                }
            };
            if (!AsignacionClienteBean.getDatosCliente().isEmpty() && AsignacionClienteBean.getDatosCliente().containsKey("VentaCliente")) {
                AsignacionClienteBean.getDatosCliente().get("VentaCliente").getAsJsonObject().get("Cliente").getAsJsonObject().add("Bonos_Vive_Terpel", bonosObtenidos);
            }
            showMessage(mensaje, "/com/firefuel/resources/btOk.png",
                    true, runnable,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            showMessage(mensaje, "/com/firefuel/resources/btBad.png",
                    true, this::vistaAsignarCliente,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void vistaAsignarCliente() {
        showDialog(viewMedios);
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (runnable != null) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public boolean existeBono(JsonArray bonos, long bonoVoucher) {
        boolean existe = false;
        if (bonos != null) {
            for (JsonElement bono : bonos) {
                JsonObject bonosVenta = bono.getAsJsonObject();
                if (bonoVoucher == bonosVenta.get("AFP").getAsLong()) {
                    existe = true;
                }
            }
        }
        return existe;
    }

    private void vistaAsignarClienteView() {
        NovusConstante.ventanaFE = Boolean.TRUE;
        viewFe.mostrarInicio();
        showDialog(viewFe);
    }

    public void cerrar() {
        NovusUtils.printLn("cerrar");
        if (idTransmision == 0L) {
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimientosBean.getId());
            idTransmision = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
        }
        if (cerrar != null) {
            AsignacionClienteBean.getDatosCliente().clear();
            this.movimientosDao.actualizarEstadoTransmision(3, idTransmision);
            this.movimientosDao.actualizarEstadoMovimientosClientes(3, idTransmision);
            cerrar.run();
        }
    }

    public void cerrarVenta() {
        NovusUtils.printLn("cerrarVenta");
        if (cerrar != null) {
            AsignacionClienteBean.getDatosCliente().clear();
            cerrar.run();
        }
    }

    public void cerrarMensaje() {
        NovusUtils.printLn("cerrarVentaMensaje");
        if (cerrar != null) {
            AsignacionClienteBean.getDatosCliente().clear();
            cerrar.run();
            validarSincronizado(idTransmision);
            terminarTareaProgramada();
        }
    }

    public void asignarDatosPlaca(long id, String placa, String numero, String odometro, String orden) {
        JsonObject datosVenta = new JsonObject();

        atributosVenta.addProperty("vehiculo_placa", placa);
        atributosVenta.addProperty("vehiculo_odometro", odometro);
        atributosVenta.addProperty("vehiculo_numero", numero);

        datosVenta.addProperty("identificadorMovimiento", id);
        datosVenta.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        datosVenta.add("Cliente", atributosVenta);

        AsignacionClienteBean.agregarInformacionCliente("VentaCliente", datosVenta);
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

    public void mostarMensaje(String mensaje) {
        jMensaje.setText(mensaje);
        mostrarSubPanel(pnlMensaje);

        tareaProgramada = scheduler.schedule(this::cerrarMensaje, 4, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    private void validarSincronizado(long idTransmision) {
        long idMovimiento = this.movimientosDao.buscarMOvimientoId(idTransmision);
        if (idTransmision == 0L) {
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimientosBean.getId());
            idTransmision = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
        }

        if (!this.electronicaDao.hayTransaccionesPendientes(idMovimiento) && this.electronicaDao.ventasFeFinalizadas(idMovimiento)) {
            NovusUtils.printLn("********************** Cambiando el estado de la sincronizacion a estado 2 ************************************");
            this.movimientosDao.actualizarEstadoTransmision(2, idTransmision);
            this.movimientosDao.actualizarEstadoMovimientosClientes(2, idTransmision);
        }
    }

    public void mostarMensaje(String mensaje, Integer delay) {
        jMensaje.setText(mensaje);
        mostrarSubPanel(pnlMensaje);
        tareaProgramada = scheduler.schedule(this::cerrarMensaje, delay, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    public void terminarTareaProgramada() {
        if (tareaProgramada != null && !tareaProgramada.isDone()) {
            NovusUtils.printLn("Terminado Tarea");
            tareaProgramada.cancel(true);
        }
    }

    private long obtenerIdTransmisionSeguro() {
        if (this.movimientosBean != null
                && this.movimientosBean.getAtributos() != null
                && this.movimientosBean.getAtributos().has("idTransmision")
                && !this.movimientosBean.getAtributos().get("idTransmision").isJsonNull()) {
            return this.movimientosBean.getAtributos().get("idTransmision").getAsLong();
        } else {
            System.err.println("⚠ 'idTransmision' no disponible en movimiento o nulo");
            return 0L;
        }
    }

    private long getIdTransmisionRobusto() {
        long id = obtenerIdTransmisionSeguro();
        if (id == 0L) {
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimientosBean.getId());
            id = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
            NovusUtils.printLn("🔁 getIdTransmisionRobusto(): Fallback obtenido desde DAO: " + id);
        }
        return id;
    }

}

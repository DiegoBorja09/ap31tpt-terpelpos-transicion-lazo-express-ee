package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.BodegaBean;
import com.bean.EntradasBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.facade.BodegasFacade;
import com.facade.SurtidorFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.controllers.ClientWSAsync;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.TreeMap;
import javax.swing.Timer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

public class ReporteRecepcionView extends JDialog {

    InfoViewController mainFrame = null;
    Date initialDate = new Date();
    Date finalDate = new Date();
    TreeMap<Integer, EntradasBean> loadedFuelInputs = new TreeMap<>();
    SimpleDateFormat sdfDateTimeAM = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdfDateISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    SimpleDateFormat sdfBasicDate = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    SimpleDateFormat sdfDateSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);

    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    
    // Constantes y variables para manejo de cola de impresión
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    private Timer timerVerificacionCola = null; // Timer para verificar periódicamente si el registro fue eliminado de la cola
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public ReporteRecepcionView(InfoViewController mainFrame, boolean modal) {
        super(mainFrame, modal);
        this.mainFrame = mainFrame;
        initComponents();
        this.init();
    }

    Date getInitialDate() {
        return this.initialDate;
    }

    Date getFinalDate() {
        return this.finalDate;
    }

    void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    void fetchFuelInputsByDateRange(Date initialDate, Date finalDate) {
        if (initialDate == null) {
            initialDate = new Date();
        }
        if (finalDate == null) {
            finalDate = new Date();
        }
        String formattedInitialDate = this.sdfDateSQL.format(initialDate);
        String formattedFinalDate = this.sdfDateSQL.format(finalDate);

        mostrarPanelMensaje("CONSULTA ENTRADAS ESPERE", 
                "/com/firefuel/resources/loader_fac.gif",
                LetterCase.FIRST_UPPER_CASE);
        PanelNotificacion.jCerrar.setVisible(false);
        setTimeout(1, () -> {
            JsonObject response = SurtidorFacade.fetchFuelInputsByDateRange(formattedInitialDate, formattedFinalDate);
            handleFuelInputsByDateRangeResponse(response);
        });
    }

    void handleFuelInputsByDateRangeResponse(JsonObject response) {
        this.loadedFuelInputs.clear();
        if (response != null) {
            JsonArray jsonArrayData = response.get("data").isJsonArray() ? response.get("data").getAsJsonArray() : null;
            if (jsonArrayData != null) {
                for (JsonElement jsonElement : jsonArrayData) {
                    JsonObject jsonData = jsonElement.getAsJsonObject();
                    if (jsonData != null) {
                        try {
                            JsonObject jsonMovement = jsonData.get("movimiento").getAsJsonObject();
                            JsonObject jsonPerson = jsonData.getAsJsonObject("personas");
                            JsonObject jsonAttributesMovement = jsonMovement.getAsJsonObject("atributos");
                            JsonArray jsonArrayTanks = jsonAttributesMovement.getAsJsonArray("tanques_seleccionados");
                            JsonObject jsonSelectedTanks = jsonArrayTanks.size() > 0
                                    ? jsonArrayTanks.get(0).getAsJsonObject()
                                    : null;
                            int tankId = jsonSelectedTanks.get("identificacionTanque").getAsInt();
                            int fuelInputId = jsonMovement.get("id").getAsInt();
                            float volumeAmount = jsonSelectedTanks.get("cantidad").getAsFloat();
                            String personName = jsonPerson.get("nombre").getAsString();
                            String orderDocument = jsonAttributesMovement.get("documento").getAsString();
                            String productDescription = jsonSelectedTanks.get("productoDesc").getAsString();
                            Date movementDate = this.sdfDateISO.parse(jsonMovement.get("fecha").getAsString());
                            EntradasBean fuelInput = new EntradasBean();
                            ArrayList<ProductoBean> products = new ArrayList<>();
                            ProductoBean productSelected = new ProductoBean();
                            productSelected.setDescripcion(productDescription);
                            products.add(productSelected);
                            BodegaBean tank = new BodegaBean();
                            tank.setNumeroStand(tankId);
                            tank.setProductos(products);
                            tank.setGalonTanque(volumeAmount);
                            fuelInput.setFechaFin(movementDate);
                            PersonaBean person = new PersonaBean();
                            person.setNombre(personName);
                            fuelInput.setNroOrden(orderDocument);
                            fuelInput.setPersona(person);
                            fuelInput.setTanque(tank);
                            this.loadedFuelInputs.put(fuelInputId, fuelInput);
                        } catch (ParseException ex) {
                            Logger.getLogger(ReporteRecepcionView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } else {
            mostrarPanelMensaje("OCURRIO UN ERROR AL CONSULTAR EL REPORTE", 
                    "/com/firefuel/resources/btBad.png",
                    LetterCase.FIRST_UPPER_CASE);
        }
        this.renderFuelInputs();
    }

    void loadData() {
        this.updateView();
    }

    void renderPersonInJourney() {
        if (Main.persona != null) {
            this.jpromotor.setText(Main.persona.getNombre());
        } else {
            this.jpromotor.setText("NO HAY TURNO ABIERTO");
        }
    }

    void renderFuelInputs() {
        this.btn_print_details.setEnabled(false);
        DefaultTableModel dm = (DefaultTableModel) this.fuel_input_table.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) this.fuel_input_table.getModel();
        for (Map.Entry<Integer, EntradasBean> e : this.loadedFuelInputs.entrySet()) {
            EntradasBean fuelEntry = e.getValue();
            BodegaBean tank = fuelEntry.getTanque();
            int tankId = tank.getNumeroStand();
            String orderDocument = fuelEntry.getNroOrden();
            String dateString = this.sdfDateISO.format(fuelEntry.getFechaFin());
            ProductoBean product = tank.getProductos().get(0);
            String productDesc = product.getDescripcion();
            double volumeAmount = tank.getGalonTanque();
            String personName = fuelEntry.getPersona().getNombre();
            try {
                defaultModel.addRow(new Object[]{
                    e.getKey(),
                    tankId,
                    orderDocument,
                    dateString,
                    productDesc,
                    volumeAmount,
                    personName
                });
            } catch (Exception ex) {
                NovusUtils.printLn(ex.getMessage());
            }
        }
    }

    void selectRow() {
        int selectedRow = this.fuel_input_table.getSelectedRow();
        this.btn_print_details.setEnabled(selectedRow > -1);
        
        // Verificar si hay un registro en cola para la entrada seleccionada (igual que en VentasHistorialView)
        if (selectedRow > -1) {
            int fuelEntryId = (int) this.fuel_input_table.getValueAt(selectedRow, 0);
            if (existeEnColaPendiente(fuelEntryId)) {
                // Si está en cola, mostrar botón bloqueado con texto IMPRIMIENDO...
                bloquearBotonImprimir();
            } else {
                // Si no está en cola, mostrar botón normal
                if (!botonImprimirBloqueado) {
                    desbloquearBotonImprimir();
                }
            }
        }
    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.setTablePrimaryStyle(this.fuel_input_table);
        this.initCalendar();
        this.renderPersonInJourney();
    }

    void initCalendar() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
        this.rSDateChooser1.setDatoFecha(ca.getTime());
        this.rSDateChooser2.setDatoFecha(new Date());
    }

    void init() {
        this.loadView();
        this.loadData();
    }

    void updateView() {
        this.setInitialDate(this.rSDateChooser1.getDatoFecha());
        this.setFinalDate(this.rSDateChooser2.getDatoFecha());
        this.fetchFuelInputsByDateRange(this.getInitialDate(), this.getFinalDate());
    }

    void handlePrintRecordDetailRequest(JsonObject response) {
        if (response != null) {
            mostrarPanelMensaje("SE HA IMPRESO CORRECTAMENTE", 
                    "/com/firefuel/resources/btOk.png",
                   LetterCase.FIRST_UPPER_CASE);
        } else {
            mostrarPanelMensaje("OCURRIO UN ERROR EN LA IMPRESION", 
                    "/com/firefuel/resources/btBad.png",
                    LetterCase.FIRST_UPPER_CASE);
        }

    }

    void printRecordDetail() {
        NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Botón IMPRIMIR presionado");
        
        // Verificar si el botón está bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Botón de impresión bloqueado - hay una impresión en proceso");
            return;
        }
        
        int selectedRow = this.fuel_input_table.getSelectedRow();
        if (selectedRow > -1) {
            final int fuelEntryId = (int) this.fuel_input_table.getValueAt(selectedRow, 0);
            NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] ID de entrada: " + fuelEntryId);
            
            // Verificar si ya está en cola de impresión
            if (existeEnColaPendiente(fuelEntryId)) {
                NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] El registro ya está en cola de impresión - ID: " + fuelEntryId);
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
                            NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Servicio de impresión no está disponible: " + mensaje);
                            desbloquearBotonImprimir();
                            mostrarPanelMensaje(mensaje,
                                    "/com/firefuel/resources/btBad.png",
                                    LetterCase.FIRST_UPPER_CASE);
                        });
                        return;
                    }
                    
                    // 3. Servicio OK - Guardar registro y proceder con la impresión
                    // Usar ENTRADA_COMBUSTIBLE para que coincida con el report_type que envía el servicio
                    guardarRegistroPendiente(fuelEntryId, "ENTRADA_COMBUSTIBLE");
                    
                    NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Iniciando impresión para entrada: " + fuelEntryId);
                    
                    // Llamar al facade para imprimir
                    JsonObject response = BodegasFacade.printFuelEntryDetails(fuelEntryId);
                    
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Verificar si la respuesta es exitosa
                        if (response != null) {
                            boolean hasError = response.has("codigoError") 
                                    || response.has("error")
                                    || (response.has("success") && !response.get("success").getAsBoolean());
                            
                            if (!hasError) {
                                // Impresión exitosa - NO desbloquear, esperar notificación
                                // Iniciar timer para verificar periódicamente si el registro fue eliminado de la cola
                                NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Impresión enviada correctamente - iniciando timer de verificación");
                                iniciarTimerVerificacionCola(fuelEntryId);
                                mostrarPanelMensaje("SE HA IMPRESO CORRECTAMENTE", 
                                        "/com/firefuel/resources/btOk.png",
                                        LetterCase.FIRST_UPPER_CASE);
                            } else {
                                // Error en la impresión - eliminar registro de cola
                                eliminarRegistroPendiente(fuelEntryId, "ENTRADA_COMBUSTIBLE");
                                desbloquearBotonImprimir();
                                mostrarPanelMensaje("OCURRIO UN ERROR EN LA IMPRESION",
                                        "/com/firefuel/resources/btBad.png",
                                        LetterCase.FIRST_UPPER_CASE);
                            }
                        } else {
                            // Respuesta nula - eliminar registro de cola
                            eliminarRegistroPendiente(fuelEntryId, "RECEPCION_COMBUSTIBLE");
                            desbloquearBotonImprimir();
                            mostrarPanelMensaje("OCURRIO UN ERROR EN LA IMPRESION",
                                    "/com/firefuel/resources/btBad.png",
                                    LetterCase.FIRST_UPPER_CASE);
                        }
                    });
                } catch (Exception e) {
                    // Error inesperado - eliminar registro de cola si se guardó
                    eliminarRegistroPendiente(fuelEntryId, "ENTRADA_COMBUSTIBLE");
                    NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] ERROR INESPERADO: " + e.getMessage());
                    Logger.getLogger(ReporteRecepcionView.class.getName()).log(Level.SEVERE, null, e);
                    
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        mostrarPanelMensaje("ERROR INESPERADO AL IMPRIMIR: " + e.getMessage(),
                                "/com/firefuel/resources/btBad.png",
                                LetterCase.FIRST_UPPER_CASE);
                        desbloquearBotonImprimir();
                    });
                }
            }).start();
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
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
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        fuel_input_container = new javax.swing.JScrollPane();
        fuel_input_table = new javax.swing.JTable();
        jNotificacion_Recepcion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        btn_print_details = new javax.swing.JLabel();
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
        rSDateChooser1.setPlaceholder("FECHA INICIAL");
        pnl_principal.add(rSDateChooser1);
        rSDateChooser1.setBounds(10, 110, 250, 50);

        rSDateChooser2.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser2.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setPlaceholder("FECHA FINAL");
        pnl_principal.add(rSDateChooser2);
        rSDateChooser2.setBounds(270, 110, 270, 50);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 28)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        pnl_principal.add(jpromotor);
        jpromotor.setBounds(870, 40, 410, 40);

        fuel_input_container.setBorder(null);

        fuel_input_table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        fuel_input_table.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        fuel_input_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "TANQUE", "NRO ORDEN", "FECHA", "PRODUCTO", "CANTIDAD", "PROMOTOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fuel_input_table.setRowHeight(35);
        fuel_input_table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fuel_input_table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fuel_input_table.setShowGrid(true);
        fuel_input_table.setShowHorizontalLines(false);
        fuel_input_table.setShowVerticalLines(false);
        fuel_input_table.getTableHeader().setResizingAllowed(false);
        fuel_input_table.getTableHeader().setReorderingAllowed(false);
        fuel_input_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fuel_input_tableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fuel_input_tableMouseReleased(evt);
            }
        });
        fuel_input_container.setViewportView(fuel_input_table);
        if (fuel_input_table.getColumnModel().getColumnCount() > 0) {
            fuel_input_table.getColumnModel().getColumn(0).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            fuel_input_table.getColumnModel().getColumn(1).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(1).setPreferredWidth(50);
            fuel_input_table.getColumnModel().getColumn(2).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(3).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(3).setPreferredWidth(140);
            fuel_input_table.getColumnModel().getColumn(4).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(4).setPreferredWidth(150);
            fuel_input_table.getColumnModel().getColumn(5).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(6).setResizable(false);
            fuel_input_table.getColumnModel().getColumn(6).setPreferredWidth(150);
        }

        pnl_principal.add(fuel_input_container);
        fuel_input_container.setBounds(10, 180, 1260, 510);

        jNotificacion_Recepcion.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        jNotificacion_Recepcion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion_Recepcion);
        jNotificacion_Recepcion.setBounds(140, 720, 800, 70);

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel1.setText("ACTUALIZAR");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(550, 100, 190, 70);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CONSULTA RECEPCION COMBUSTIBLE");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(100, 0, 790, 80);

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

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 20)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        pnl_principal.add(jpromotor1);
        jpromotor1.setBounds(820, 10, 460, 20);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        pnl_principal.add(jclock);
        jclock.setBounds(1150, 720, 110, 66);

        btn_print_details.setFont(new java.awt.Font("Bebas Neue", 1, 24)); // NOI18N
        btn_print_details.setForeground(new java.awt.Color(255, 255, 255));
        btn_print_details.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_print_details.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_print_details.setText("IMPRIMIR");
        btn_print_details.setEnabled(false);
        btn_print_details.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_print_details.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_print_detailsMouseReleased(evt);
            }
        });
        pnl_principal.add(btn_print_details);
        btn_print_details.setBounds(950, 720, 180, 60);

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

    private void fuel_input_tableMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_fuel_input_tableMouseClicked

    }// GEN-LAST:event_fuel_input_tableMouseClicked

    private void btn_print_detailsMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_print_detailsMouseReleased
        if (this.btn_print_details.isEnabled()) {
            this.printRecordDetail();
        }
    }// GEN-LAST:event_btn_print_detailsMouseReleased

    private void fuel_input_tableMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_fuel_input_tableMouseReleased
        this.selectRow();
    }// GEN-LAST:event_fuel_input_tableMouseReleased

    private void jLabel8MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel8MousePressed
    }// GEN-LAST:event_jLabel8MousePressed

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed

    }// GEN-LAST:event_jLabel5MousePressed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        this.updateView();
    }// GEN-LAST:event_jLabel1MousePressed

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        NovusUtils.beep();
    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel7MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MousePressed

    }// GEN-LAST:event_jLabel7MousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        this.close();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void close() {
        // Detener timer antes de cerrar
        detenerTimerVerificacionCola();
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_print_details;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane fuel_input_container;
    private javax.swing.JTable fuel_input_table;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jNotificacion_Recepcion;
    private javax.swing.JPanel jclock;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser2;
    // End of variables declaration//GEN-END:variables

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(ReporteRecepcionView.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono, String letterCase) {

        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        String msj = String.valueOf(mensaje.charAt(0)).toUpperCase().concat(mensaje.substring(1).toLowerCase());

        
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
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
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a IMPRIMIENDO...
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true;
        btn_print_details.setText("IMPRIMIENDO...");
        btn_print_details.setForeground(Color.WHITE);
        btn_print_details.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto a IMPRIMIR
     */
    private void desbloquearBotonImprimir() {
        botonImprimirBloqueado = false;
        btn_print_details.setText("IMPRIMIR");
        btn_print_details.setForeground(Color.WHITE);
        btn_print_details.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        // Detener el timer de verificación si está activo
        detenerTimerVerificacionCola();
    }
    
    /**
     * Inicia un Timer de Swing que verifica periódicamente si el registro ya no está en la cola
     * y desbloquea el botón automáticamente cuando se recibe la notificación
     * (patrón usado en VentasHistorialView y otros componentes del proyecto)
     */
    private void iniciarTimerVerificacionCola(final int fuelEntryId) {
        // Detener timer anterior si existe
        detenerTimerVerificacionCola();
        
        // Crear timer que verifica cada 1 segundo si el registro ya no está en la cola
        timerVerificacionCola = new Timer(1000, e -> {
            if (!botonImprimirBloqueado) {
                // Si el botón ya no está bloqueado, detener el timer
                detenerTimerVerificacionCola();
                return;
            }
            
            // Verificar si el registro ya no está en la cola
            if (!existeEnColaPendiente(fuelEntryId)) {
                NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Registro eliminado de la cola - desbloqueando botón automáticamente");
                detenerTimerVerificacionCola();
                desbloquearBotonImprimir();
            }
        });
        
        timerVerificacionCola.setRepeats(true);
        timerVerificacionCola.start();
    }
    
    /**
     * Detiene el timer de verificación de cola si está activo
     */
    private void detenerTimerVerificacionCola() {
        if (timerVerificacionCola != null && timerVerificacionCola.isRunning()) {
            timerVerificacionCola.stop();
            timerVerificacionCola = null;
        }
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     */
    private synchronized boolean existeEnColaPendiente(int fuelEntryId) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            if (!file.exists() || file.length() == 0) {
                return false;
            }
            
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
                    return false;
                }
            }
            
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsInt() == fuelEntryId) {
                    if (registro.has("report_type") && registro.get("report_type").getAsString().equals("ENTRADA_COMBUSTIBLE")) {
                        return true;
                    }
                }
            }
            
            return false;
        } catch (Exception ex) {
            Logger.getLogger(ReporteRecepcionView.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Guarda un registro en la cola de impresión pendiente
     */
    private synchronized void guardarRegistroPendiente(int fuelEntryId, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            JsonArray registros = new JsonArray();
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
                }
            }
            
            // Verificar si ya existe un registro con el mismo ID y tipo
            boolean existe = false;
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsInt() == fuelEntryId
                        && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    existe = true;
                    break;
                }
            }
            
            if (!existe) {
                JsonObject nuevoRegistro = new JsonObject();
                nuevoRegistro.addProperty("id", fuelEntryId);
                nuevoRegistro.addProperty("report_type", reportType);
                nuevoRegistro.addProperty("status", "PENDING");
                nuevoRegistro.addProperty("message", "IMPRIMIENDO...");
                registros.add(nuevoRegistro);
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    writer.write(gson.toJson(registros));
                }
                
                NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Registro guardado en cola - ID: " + fuelEntryId);
            }
        } catch (Exception ex) {
            Logger.getLogger(ReporteRecepcionView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión pendiente
     */
    private synchronized void eliminarRegistroPendiente(int fuelEntryId, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            if (!file.exists() || file.length() == 0) {
                return;
            }
            
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
            
            JsonArray nuevosRegistros = new JsonArray();
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsInt() == fuelEntryId
                        && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    // Omitir este registro (eliminarlo)
                    continue;
                }
                nuevosRegistros.add(registro);
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(nuevosRegistros));
            }
            
            NovusUtils.printLn("[RECEPCION-COMBUSTIBLE] Registro eliminado de cola - ID: " + fuelEntryId);
        } catch (Exception ex) {
            Logger.getLogger(ReporteRecepcionView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Verifica el estado del servicio de impresión
     */

}

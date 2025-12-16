package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.BodegaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.facade.BodegasFacade;
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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.Timer;

public class InventariosTanques extends JDialog {

    InfoViewController parent;
    private final int timeoutRequest = 3;
    boolean closedView = true;
    Set<Integer> selectedTanks = new HashSet<>();
    TreeMap<Integer, TanqueInventarioItem> tanksInventaryPanels = new TreeMap<>();
    ArrayList<BodegaBean> loadedTanksInventary = new ArrayList<>();
    boolean existeVeederRoot;
    public static InventariosTanques instance = null;
    EquipoDao edao = new EquipoDao();
    
    // Constantes y variables para manejo de cola de impresión
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    private javax.swing.Timer timerVerificacionCola = null; // Timer para verificar periódicamente si el registro fue eliminado de la cola
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public void listenTankSelection(boolean selected, int tankNumber) {
        if (selected) {
            this.selectedTanks.add(tankNumber);
        } else {
            this.selectedTanks.remove(tankNumber);
        }
        this.toggleEnablePrintButton(!selectedTanks.isEmpty());
        
        // Verificar si hay un registro en cola para los tanques seleccionados (igual que en VentasHistorialView)
        if (!selectedTanks.isEmpty()) {
            long idCola = generarIdCola();
            if (existeEnColaPendiente(idCola)) {
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

    void toggleEnablePrintButton(boolean active) {
        // Solo habilitar si hay tanques seleccionados Y el botón no está bloqueado
        this.btn_print_details.setEnabled(active && !botonImprimirBloqueado);
    }

    public InventariosTanques(InfoViewController parent, boolean modal) throws DAOException {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.init();
    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
    }

    void init() throws DAOException {
        loadView();
        loadData();
    }

    void intervalRealTime() throws DAOException {
        ArrayList<BodegaBean> consoleTanksInventaryLoaded = BodegasFacade.fetchConsoleTanksMeasuresVeeder();
        if (consoleTanksInventaryLoaded != null) {
            boolean tanksAmountDifferent = loadedTanksInventary == null
                    || loadedTanksInventary.size() != consoleTanksInventaryLoaded.size();
            loadedTanksInventary = consoleTanksInventaryLoaded;
            if (tanksAmountDifferent) {
                this.renderTanksInventary(loadedTanksInventary);
            } else {
                this.updateTanksInventary(loadedTanksInventary);
            }
        }
        if (this.closedView == false) {
            setTimeout(timeoutRequest, () -> {
                try {
                    intervalRealTime();
                } catch (DAOException ex) {
                    Logger.getLogger(InventariosTanques.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    void loadData() throws DAOException {
        existeVeederRoot = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_VEEDER_ROOT, true);
        this.closedView = false;
        if (existeVeederRoot) {
            this.loadedTanksInventary = BodegasFacade.fetchConsoleTanksMeasuresVeeder();
            setTimeout(timeoutRequest, () -> {
                try {
                    intervalRealTime();
                } catch (DAOException ex) {
                    Logger.getLogger(InventariosTanques.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            Long id = edao.findEmpresaId();
            this.loadedTanksInventary = BodegasFacade.fetchTanksMeasureTeoric(id);
        }

        this.renderTanksInventary(loadedTanksInventary);
    }

    void updateTanksInventary(ArrayList<BodegaBean> loadedTanksInventary) {
        if (loadedTanksInventary != null) {
            for (BodegaBean tank : loadedTanksInventary) {
                int tankNumber = tank.getNumeroStand();
                if (this.tanksInventaryPanels.containsKey(tankNumber)) {
                    TanqueInventarioItem panel = this.tanksInventaryPanels.get(tankNumber);
                    panel.updateDataModel(tank);
                }
            }
        }
    }

    void renderTanksInventary(ArrayList<BodegaBean> loadedTanksInventary) {
        this.tanks_container.removeAll();
        if (loadedTanksInventary != null) {
            int panelHeight = 590;
            int j = 0;
            int i = 0;
            int altura;
            int variable;
            final int componentHeight = panelHeight / 2;
            final int componentWidth = 1280 / 2;

            /*Se obtiene el numero de filas*/
            double n = (double) loadedTanksInventary.size() / 2;
            int rows = (int) Math.ceil(n);

            /*se obtiene la altura del panel*/
            if (rows <= 2) {
                altura = panelHeight;
            } else {
                double m = (double) (rows + 1) / 2;
                variable = (int) Math.ceil(m);
                altura = panelHeight + (310 * (variable - 1));
            }

            /*se ingresan las medidas obtenidas*/
            tanks_container.setPreferredSize(new Dimension(tanks_container.getWidth(), altura));
            tanks_container.setLayout(new GridLayout(rows, 2));

            final int ncols = 2;
            for (BodegaBean tank : loadedTanksInventary) {
                TanqueInventarioItem panel = new TanqueInventarioItem(this, tank);
                panel.setBounds(componentWidth * i, componentHeight * j, componentWidth, componentHeight);
                j++;
                if (j == (ncols)) {
                    i++;
                    j = 0;
                }
                this.tanksInventaryPanels.put(tank.getNumeroStand(), panel);
                this.tanks_container.add(panel);
            }
        }
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
        // Restaurar el estado habilitado/deshabilitado según si hay tanques seleccionados
        toggleEnablePrintButton(!selectedTanks.isEmpty());
        // Detener el timer de verificación si está activo
        detenerTimerVerificacionCola();
    }
    
    /**
     * Inicia un Timer de Swing que verifica periódicamente si el registro ya no está en la cola
     * y desbloquea el botón automáticamente cuando se recibe la notificación
     * (patrón usado en VentasHistorialView y otros componentes del proyecto)
     */
    private void iniciarTimerVerificacionCola(final long idCola) {
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
            if (!existeEnColaPendiente(idCola)) {
                NovusUtils.printLn("[INVENTARIO-TANQUES] Registro eliminado de la cola - desbloqueando botón automáticamente");
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
     * Genera un ID único para el conjunto de tanques seleccionados
     * Debe coincidir con el algoritmo usado en print-ticket (hash simple de la cadena)
     */
    private long generarIdCola() {
        // Construir la cadena de tanques igual que en BodegasFacade (separados por comas)
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Integer tankNumber : selectedTanks) {
            if (index == 0) {
                sb.append(tankNumber);
            } else {
                sb.append(",").append(tankNumber);
            }
            index++;
        }
        String tanquesStr = sb.toString();
        
        // Usar hashCode de la cadena (igual que el algoritmo simple en print-ticket)
        // Esto genera el mismo valor que el hash simple en Python
        return tanquesStr.hashCode() & 0x7FFFFFFF; // Asegurar que sea positivo
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     * También limpia automáticamente registros viejos con tipos incorrectos
     */
    private synchronized boolean existeEnColaPendiente(long id) {
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
            
            boolean existe = false;
            boolean hayRegistrosViejos = false;
            JsonArray registrosActualizados = new JsonArray();
            
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id) {
                    String reportType = registro.has("report_type") ? registro.get("report_type").getAsString() : "";
                    if ("LECTURAS_TANQUES".equals(reportType)) {
                        existe = true;
                        registrosActualizados.add(registro); // Mantener el correcto
                    } else {
                        // Es un registro viejo con tipo incorrecto - no agregarlo (eliminarlo)
                        hayRegistrosViejos = true;
                        NovusUtils.printLn("[INVENTARIO-TANQUES] Limpiando registro viejo - ID: " + id + ", Tipo viejo: " + reportType);
                    }
                } else {
                    registrosActualizados.add(registro); // Mantener otros registros
                }
            }
            
            // Si se encontraron registros viejos, actualizar el archivo
            if (hayRegistrosViejos) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(registrosActualizados));
                }
                NovusUtils.printLn("[INVENTARIO-TANQUES] Registros viejos eliminados de la cola");
            }
            
            return existe;
        } catch (Exception e) {
            NovusUtils.printLn("Error verificando cola de impresión: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Guarda un registro de impresión pendiente en el archivo TXT
     */
    private synchronized void guardarRegistroPendiente(long id, String reportType) {
        try {
            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdir();
            }

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

            // Verificar si el ID ya existe
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    NovusUtils.printLn("Registro ya existe en cola de impresión - ID: " + id);
                    return;
                }
            }

            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("status", "PENDING");
            nuevoRegistro.addProperty("message", "IMPRIMIENDO...");

            registros.add(nuevoRegistro);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registros));
            }

            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(InventariosTanques.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     */
    private synchronized void eliminarRegistroPendiente(long id, String reportType) {
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
            
            JsonArray registrosActualizados = new JsonArray();
            boolean encontrado = false;
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    encontrado = true;
                    NovusUtils.printLn("Eliminando registro de cola de impresión - ID: " + id + ", Tipo: " + reportType);
                } else {
                    registrosActualizados.add(registro);
                }
            }
            
            if (encontrado) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(registrosActualizados));
                }
                NovusUtils.printLn("Registro eliminado de cola de impresión - ID: " + id);
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(InventariosTanques.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Verifica el estado del servicio de impresión mediante health check
     */

    void printTankInventaryDetails() {
        NovusUtils.printLn("[INVENTARIO-TANQUES] Botón IMPRIMIR presionado");
        
        // Verificar si el botón está bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("[INVENTARIO-TANQUES] Botón de impresión bloqueado - hay una impresión en proceso");
            return;
        }
        
        // Verificar que haya tanques seleccionados
        if (selectedTanks.isEmpty()) {
            NovusUtils.printLn("[INVENTARIO-TANQUES] ERROR: No hay tanques seleccionados");
            mostrarPanelMensaje("ERROR: NO HAY TANQUES SELECCIONADOS",
                    "/com/firefuel/resources/btBad.png",
                    LetterCase.FIRST_UPPER_CASE);
            return;
        }
        
        // Generar ID único para este conjunto de tanques
        final long idCola = generarIdCola();
        NovusUtils.printLn("[INVENTARIO-TANQUES] ID de cola generado: " + idCola + " para tanques: " + selectedTanks.toString());
        
        // Verificar si ya está en cola de impresión (el método también limpia registros viejos automáticamente)
        if (existeEnColaPendiente(idCola)) {
            NovusUtils.printLn("[INVENTARIO-TANQUES] El registro ya está en cola de impresión - ID: " + idCola);
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
                        NovusUtils.printLn("[INVENTARIO-TANQUES] Servicio de impresión no está disponible: " + mensaje);
                        desbloquearBotonImprimir();
                        mostrarPanelMensaje(mensaje,
                                "/com/firefuel/resources/btBad.png",
                                LetterCase.FIRST_UPPER_CASE);
                    });
                    return;
                }
                
                // 3. Servicio OK - Guardar registro y proceder con la impresión
                // Usar LECTURAS_TANQUES para que coincida con el report_type que envía el servicio
                guardarRegistroPendiente(idCola, "LECTURAS_TANQUES");
                
                NovusUtils.printLn("[INVENTARIO-TANQUES] Iniciando impresión para tanques: " + selectedTanks.toString());
                
                // Llamar al facade para imprimir
                JsonObject response = BodegasFacade.printTankInventaryDetails(selectedTanks);
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    // Verificar si la respuesta es exitosa
                    if (response != null) {
                        boolean hasError = response.has("codigoError") 
                                || response.has("error")
                                || (response.has("success") && !response.get("success").getAsBoolean());
                        
                        if (!hasError) {
                            // Impresión exitosa - NO desbloquear, esperar notificación
                            // Iniciar timer para verificar periódicamente si el registro fue eliminado de la cola
                            NovusUtils.printLn("[INVENTARIO-TANQUES] Impresión enviada correctamente - iniciando timer de verificación");
                            iniciarTimerVerificacionCola(idCola);
                            mostrarPanelMensaje("SE HA IMPRESO CORRECTAMENTE", 
                                    "/com/firefuel/resources/btOk.png",
                                    LetterCase.FIRST_UPPER_CASE);
                        } else {
                            // Error en la impresión - eliminar registro de cola y desbloquear
                            eliminarRegistroPendiente(idCola, "LECTURAS_TANQUES");
                            String technicalMessage = "NO PUDO IMPRIMIR EL REPORTE";
                            if (response.has("mensaje")) {
                                technicalMessage = response.get("mensaje").getAsString();
                            } else if (response.has("error")) {
                                technicalMessage = response.get("error").getAsString();
                            }
                            
                            NovusUtils.printLn("=====================================================");
                            NovusUtils.printLn("ERROR AL IMPRIMIR INVENTARIO DE TANQUES");
                            NovusUtils.printLn("=====================================================");
                            NovusUtils.printLn("Mensaje técnico: " + technicalMessage);
                            NovusUtils.printLn("Tanques seleccionados: " + selectedTanks.size());
                            NovusUtils.printLn("IDs Tanques: " + selectedTanks.toString());
                            NovusUtils.printLn("=====================================================");
                            
                            desbloquearBotonImprimir();
                            mostrarPanelMensaje("OCURRIO UN ERROR EN LA IMPRESION", 
                                    "/com/firefuel/resources/btBad.png",
                                    LetterCase.FIRST_UPPER_CASE);
                        }
                    } else {
                        // Respuesta nula - eliminar registro de cola y desbloquear
                        eliminarRegistroPendiente(idCola, "LECTURAS_TANQUES");
                        NovusUtils.printLn("=====================================================");
                        NovusUtils.printLn("ERROR INESPERADO - Respuesta nula del servicio");
                        NovusUtils.printLn("=====================================================");
                        NovusUtils.printLn("Tanques seleccionados: " + selectedTanks.size());
                        NovusUtils.printLn("=====================================================");
                        
                        desbloquearBotonImprimir();
                        mostrarPanelMensaje("OCURRIO UN ERROR EN LA IMPRESION", 
                                "/com/firefuel/resources/btBad.png",
                                LetterCase.FIRST_UPPER_CASE);
                    }
                });
            } catch (Exception e) {
                // Error inesperado - eliminar registro de cola si se guardó
                eliminarRegistroPendiente(idCola, "LECTURAS_TANQUES");
                NovusUtils.printLn("=====================================================");
                NovusUtils.printLn("ERROR INESPERADO en impresión de inventario de tanques");
                NovusUtils.printLn("=====================================================");
                NovusUtils.printLn("Tipo: " + e.getClass().getName());
                NovusUtils.printLn("Mensaje: " + e.getMessage());
                NovusUtils.printLn("=====================================================");
                e.printStackTrace();
                Logger.getLogger(InventariosTanques.class.getName()).log(Level.SEVERE, null, e);
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    desbloquearBotonImprimir();
                    mostrarPanelMensaje("ERROR INESPERADO AL IMPRIMIR REPORTE: " + e.getMessage(),
                            "/com/firefuel/resources/btBad.png",
                            LetterCase.FIRST_UPPER_CASE);
                });
            }
        }).start();
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
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        tanks_container_scroll = new javax.swing.JScrollPane();
        tanks_container = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        heading = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btn_print_details = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(1170, 3, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        tanks_container_scroll.setBorder(null);
        tanks_container_scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tanks_container_scroll.setToolTipText("");
        tanks_container_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        tanks_container_scroll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tanks_container_scroll.setMinimumSize(new java.awt.Dimension(0, 0));
        tanks_container_scroll.setOpaque(false);
        tanks_container_scroll.setPreferredSize(new java.awt.Dimension(1280, 470));
        tanks_container_scroll.setViewportView(null);

        tanks_container.setBackground(new java.awt.Color(255, 255, 255));
        tanks_container.setForeground(new java.awt.Color(255, 255, 255));
        tanks_container.setPreferredSize(new java.awt.Dimension(1000, 1200));
        tanks_container.setLayout(new java.awt.GridLayout(4, 2));
        tanks_container_scroll.setViewportView(tanks_container);
        tanks_container.getAccessibleContext().setAccessibleParent(tanks_container_scroll);

        pnl_principal.add(tanks_container_scroll);
        tanks_container_scroll.setBounds(0, 80, 1280, 630);

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(logo);
        logo.setBounds(10, 700, 110, 100);

        heading.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        heading.setForeground(new java.awt.Color(255, 255, 255));
        heading.setText("INVENTARIOS TANQUES");
        pnl_principal.add(heading);
        heading.setBounds(120, 0, 720, 80);

        back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backMouseReleased(evt);
            }
        });
        pnl_principal.add(back);
        back.setBounds(0, 0, 90, 90);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(140, 720, 730, 70);

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
        btn_print_details.setBounds(930, 725, 180, 60);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndSurtidores.png"))); // NOI18N
        background.setMaximumSize(new java.awt.Dimension(1280, 720));
        background.setMinimumSize(new java.awt.Dimension(1280, 720));
        background.setPreferredSize(new java.awt.Dimension(1280, 720));
        pnl_principal.add(background);
        background.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_print_detailsMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_print_detailsMouseReleased
        if (this.btn_print_details.isEnabled()) {
            this.printTankInventaryDetails();
        }
    }// GEN-LAST:event_btn_print_detailsMouseReleased

    private void backMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void cerrar() {
        this.closedView = true;
        // Detener timer antes de cerrar
        detenerTimerVerificacionCola();
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back;
    private javax.swing.JLabel background;
    private javax.swing.JLabel btn_print_details;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel heading;
    public static javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private javax.swing.JPanel tanks_container;
    private javax.swing.JScrollPane tanks_container_scroll;
    // End of variables declaration//GEN-END:variables

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(InventariosTanques.class.getName()).log(Level.SEVERE, null, e);
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

}

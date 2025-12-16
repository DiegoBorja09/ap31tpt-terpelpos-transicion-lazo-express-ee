package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.sutidores.ObtenerCapacidadMaximaUseCase;
import com.bean.BodegaBean;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.SurtidorDao;
import com.facade.BodegasFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import teclado.view.common.TecladoExtendido;

public class LecturasTanquesViewController extends javax.swing.JDialog {

    boolean independiente = false;
    MyCallback handler = null;
    InfoViewController mainFrame;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    boolean activeConsole;
    public static LecturasTanquesViewController instance = null;
    TreeMap<Integer, JsonObject> COMBO_TANKS_OPTIONS = null;
    int lastIndexSelected = -1;
    TreeMap<Integer, JsonObject> tanksCapacity = null;
    TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers = null;
    public static ArrayList<BodegaBean> tanques = null;
    public boolean aforo = true;
    boolean readOnlyView = false;
    public boolean faltaDato = false;
    int selectedIndexTable = -1;
    private static final String RECURSO_LOADER = "/com/firefuel/resources/loader_fac.gif";
    
    InicioSurtidorView sview = null;
    private Runnable runnable;

    ObtenerCapacidadMaximaUseCase obtenerCapacidadMaximaUseCase = new ObtenerCapacidadMaximaUseCase();
    

    public static LecturasTanquesViewController getInstance(InfoViewController mainFrame, boolean modal) {
        return new LecturasTanquesViewController(mainFrame, modal);
    }

    public static LecturasTanquesViewController getInstance(InfoViewController mainFrame, boolean modal,
            boolean independiente) {
        return new LecturasTanquesViewController(mainFrame, modal, independiente);
    }

    public static LecturasTanquesViewController getInstance(InfoViewController mainFrame, boolean modal,
            TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers) {
        return new LecturasTanquesViewController(mainFrame, modal, selectedTotalizers);
    }

    public static LecturasTanquesViewController getInstance(InfoViewController mainFrame, boolean modal,
            MyCallback handler) {
        return new LecturasTanquesViewController(mainFrame, modal, handler);
    }

    void setViewSurtidor(InicioSurtidorView sview) {
        this.sview = sview;
    }

    LecturasTanquesViewController(InfoViewController mainFrame, boolean modal, MyCallback handler) {
        super(mainFrame, modal);
        this.mainFrame = mainFrame;
        this.handler = handler;
        this.init();
    }

    LecturasTanquesViewController(InfoViewController mainFrame, boolean modal) {
        super(mainFrame, modal);
        this.mainFrame = mainFrame;
        this.init();
    }

    LecturasTanquesViewController(InfoViewController mainFrame, boolean modal, boolean independiente) {
        super(mainFrame, modal);
        this.mainFrame = mainFrame;
        this.independiente = independiente;
        this.init();
    }

    LecturasTanquesViewController(InfoViewController mainFrame, boolean modal,
            TreeMap<Integer, ArrayList<Surtidor>> selectedTotalizers) {
        super(mainFrame, modal);
        this.mainFrame = mainFrame;
        this.selectedTotalizers = selectedTotalizers;
        this.init();
    }

    void setTanksCapacity(TreeMap<Integer, JsonObject> tanksCapacity) {
        this.tanksCapacity = tanksCapacity;
    }

    TreeMap<Integer, JsonObject> getTanksCapacity() {
        return this.tanksCapacity;
    }

    void setActiveConsole(boolean activeConsole) {
        this.activeConsole = activeConsole;
    }

    void toggleAlphabeticKeyboard(boolean enable) {
        TecladoExtendido keyboard = (TecladoExtendido) this.pnl_keyboard;
        keyboard.habilitarAlfanumeric(enable);
    }

    void loadView() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.setTablePrimaryStyle(this.table_measures);
        this.toggleAlphabeticKeyboard(false);
    }

    void loadAsyncInformation() {
        showMessage("CARGANDO INFORMACION DE TANQUES, POR FAVOR ESPERE... ", 
                RECURSO_LOADER, true, this::mostrarMenuPrincipal, 
                false, LetterCase.FIRST_UPPER_CASE);
        AsyncTask asyncTask = new AsyncTask(this);
        asyncTask.start();
        btn_save.setEnabled(false);
    }

    class AsyncTask extends Thread {

        LecturasTanquesViewController view;

        public AsyncTask(LecturasTanquesViewController view) {
            this.view = view;
        }

        void fetchInitialData() {
            this.view.loadData();
        }

        @Override
        public void run() {
            this.fetchInitialData();
        }
    }

    void loadData() {

        this.setActiveConsole(this.mainFrame.loadIsActiveConsole());

        if (this.isActiveConsole() && this.mainFrame.getLoadedTanks() == null) {
            this.setActiveConsole(false);
            showMessage("LAS MEDIDAS DEBEN SER INGRESADAS MANUALMENTE",
                    "/com/firefuel/resources/btBad.png", 
                    true, this::mostrarMenuPrincipal, 
                    true, LetterCase.FIRST_UPPER_CASE);
            mainFrame.reloadTanks(false);
        }
        this.renderTanksNameCombo(this.getTanksComboOptions());
        if (this.isActiveConsole()) {
            this.setReadOnlyView();
            this.renderTanksMeasures();
        } else {
            this.setTanksCapacity(this.fetchTanksCapacity(this.getTanksId()));
        }
        btn_save.setEnabled(true);
        mostrarMenuPrincipal();
        if (mainFrame.getLoadedTanks() == null || mainFrame.getLoadedTanks().isEmpty()) {
            showMessage("ERROR NO HAY INFORMACION DE TANQUES", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::mostrarMenuPrincipal, 
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    void setReadOnlyView() {
        this.readOnlyView = true;
        LecturasTanquesViewController.txt_altura.setEditable(false);
        LecturasTanquesViewController.txt_water.setEditable(false);
        this.combo_tanks.setEnabled(false);
        this.combo_tanks.setEditable(false);
        this.table_measures.setRowSelectionAllowed(false);
        this.btn_add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
    }

    Set<Integer> getTanksId() {
        Set<Integer> tanksId = new HashSet<>();
        ArrayList<BodegaBean> loadedTanks = mainFrame.getLoadedTanks();
        if (loadedTanks != null) {
            for (BodegaBean tank : loadedTanks) {
                tanksId.add(Integer.parseInt(tank.getId() + ""));
            }
        }
        return tanksId;
    }

    TreeMap<Integer, JsonObject> fetchTanksCapacity(Set<Integer> tanksId) {
        return BodegasFacade.fetchTanksCapacity(tanksId);
    }

    void renderTanksMeasures() {
        DefaultTableModel defaultModel = (DefaultTableModel) this.table_measures.getModel();
        defaultModel.getDataVector().removeAllElements();
        defaultModel.fireTableDataChanged();
        ArrayList<BodegaBean> loadedTanks = mainFrame.getLoadedTanks();
        if (loadedTanks != null) {
            for (BodegaBean tank : loadedTanks) {
                if (!tank.getProductos().isEmpty() && (float) tank.getGalonTanque() > 0.0 && (double) tank.getAltura_total() > 0) {
                    BigDecimal bigDecimal = new BigDecimal(tank.getGalonTanque()).setScale(3, RoundingMode.UP);
                    float galones = bigDecimal.floatValue();
                    defaultModel.addRow(new Object[]{tank.getNumeroStand(), tank.getDescripcion(),
                        tank.getProductos() != null && !tank.getProductos().isEmpty()
                        ? tank.getProductos().get(0).getDescripcion()
                        : "",
                        (float) tank.getAltura_total(),
                        tank.getAltura_agua(),
                        galones
                    });
                }
            }
            table_measures.setModel(defaultModel);
        }
    }

    void renderTanksNameCombo(TreeMap<Integer, JsonObject> tanksComboOptions) {
        if (this.COMBO_TANKS_OPTIONS != null) {
            this.combo_tanks.removeAllItems();
            for (Map.Entry<Integer, JsonObject> entry : this.COMBO_TANKS_OPTIONS.entrySet()) {
                JsonObject optionObject = entry.getValue();
                String label = optionObject.get("label").getAsString();
                this.combo_tanks.addItem(label.trim());
            }
        }
    }

    TreeMap<Integer, JsonObject> getTanksComboOptions() {
        ArrayList<BodegaBean> loadedTanks = mainFrame.getLoadedTanks();
        if (loadedTanks != null) {
            this.COMBO_TANKS_OPTIONS = new TreeMap<>();
            int index = 0;
            for (BodegaBean tank : loadedTanks) {
                JsonObject object = new JsonObject();
                object.addProperty("id", tank.getNumeroStand());
                object.addProperty("label", tank.getDescripcion().toUpperCase());
                COMBO_TANKS_OPTIONS.put(index, object);
                index++;
            }
        }
        return this.COMBO_TANKS_OPTIONS;
    }

    boolean isActiveConsole() {
        return this.activeConsole;
    }

    JsonObject getSelectedTank() {
        JsonObject tankObject = null;
        int selectedIndex = Math.max(this.combo_tanks.getSelectedIndex(), 0);
        this.lastIndexSelected = selectedIndex;
        if (this.COMBO_TANKS_OPTIONS != null) {
            tankObject = this.COMBO_TANKS_OPTIONS.get(selectedIndex);
        }
        return tankObject;
    }

    void handleComboTanksChange() {
        int tankId = this.getSelectedTank().get("id").getAsInt();
        if (this.lastIndexSelected != tankId) {
            this.setTextInputValue(txt_altura, "");
            this.setTextInputValue(txt_water, "");
            txtCantidad.setText("");
//            BodegaBean selectedTank = this.getTankById(tankId);
//            if (selectedTank != null) {
//                selectedTank.setAltura_agua(0);
//                selectedTank.setAltura_total(0);
//                selectedTank.setGalonTanque(-1);
//            }
        }
    }

    void init() {
        this.initComponents();
        this.loadView();
        this.loadAsyncInformation();
        this.mainFrame.limpiarMedidas();
    }

    void cerrar() {
        this.setVisible(false);
        this.callHandler(null);
        this.dispose();
        resetFields();
    }

    String getTextInputValue(JTextField field) {
        return field.getText().trim();
    }

    void setTextInputValue(JTextField field, String value) {
        field.setText(value != null ? value.trim() : "");
    }

    void addValuesToTank(BodegaBean selectedTank, double tankInputAlt, double tankInputWater) {
        if (selectedTank != null && tankInputAlt > 0) {
            selectedTank.setAltura_total(tankInputAlt);
            selectedTank.setAltura_agua(tankInputWater);
        }
    }

    BodegaBean getTankById(int id) {
        ArrayList<BodegaBean> tanks = mainFrame.getLoadedTanks();
        BodegaBean selectedTank = null;
        if (tanks != null) {
            for (BodegaBean tank : tanks) {
                if (tank.getNumeroStand() == id) {
                    selectedTank = tank;
                    break;
                }
            }
        }
        return selectedTank;
    }

    void handleAddButton() {
        String altura = this.getTextInputValue(LecturasTanquesViewController.txt_altura);
        String water = this.getTextInputValue(LecturasTanquesViewController.txt_water);
        if (altura.equals("")) {
            showMessage("NO HA INGRESADO ALTURA TOTAL", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::mostrarMenuPrincipal, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (water.equals("")) {
            showMessage("NO HA INGRESADO ALTURA AGUA",
                    "/com/firefuel/resources/btBad.png", 
                    true, this::mostrarMenuPrincipal, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            actualizaValorEnCantidad();
            float tankInputAlt = 0;
            double tankInputWater = 0;
            if (!validNumberDouble(altura)) {
                showMessage("ALTURA TOTAL INGRESADA NO ES UN NUMERO VALIDO",
                        "/com/firefuel/resources/btBad.png", true, 
                        this::mostrarMenuPrincipal, 
                        true, LetterCase.FIRST_UPPER_CASE);
            } else if (!validNumberDouble(water)) {
                showMessage("ALTURA AGUA INGRESADA NO ES UN NUMERO VALIDO",
                        "/com/firefuel/resources/btBad.png", true, 
                        this::mostrarMenuPrincipal, 
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {

                tankInputAlt = Float.parseFloat(altura);
                tankInputWater = Double.parseDouble(water);
                if (tankInputAlt <= 0) {
                    showMessage("ALTURA TOTAL INGRESADA DEBE SER MAYOR A 0", 
                            "/com/firefuel/resources/btBad.png", 
                            true, this::mostrarMenuPrincipal, 
                            true, LetterCase.FIRST_UPPER_CASE);
                } else {
                    int tankId = this.getSelectedTank().get("id").getAsInt();
                    BodegaBean tank = this.getTankById(tankId);

                    if (this.cargarAforo(tank, tankInputAlt) > 0) {
                        this.addValuesToTank(tank, tankInputAlt, tankInputWater);
                        this.renderTanksMeasures();
                        this.resetFields();
                    } else {
                        showMessage("ALTURA TOTAL INGRESADA NO ES UNA MEDIDA VALIDA PARA EL TANQUE",
                                "/com/firefuel/resources/btBad.png", 
                                true, this::mostrarMenuPrincipal, 
                                true, LetterCase.FIRST_UPPER_CASE);
                    }
                }
            }
        }
    }

    void resetFields() {
        this.combo_tanks.setSelectedIndex(0);
        this.setTextInputValue(txt_altura, null);
        this.setTextInputValue(txt_water, null);
        txtCantidad.setText("");
    }

    public float cargarAforo(BodegaBean tanque, float altura) {
        
        //SurtidorDao dao = new SurtidorDao();
        int tankId = this.getSelectedTank().get("id").getAsInt();
        BodegaBean tank = this.getTankById(tankId);
        JsonObject json = obtenerCapacidadMaximaUseCase.execute(tank.getId());
        long alturaMaxima = json.get("altura_maxima").getAsLong();
        long volumenMaximo= json.get("volumen_maximo").getAsLong();
        
        NovusUtils.printLn(" Altura Maxima Tanque  " + alturaMaxima);
        NovusUtils.printLn(" Volumen Maxima Tanque  " + volumenMaximo);
        
        float cargarAforo = 0;
        JsonArray response = null;
        if (mainFrame.aforos.containsKey(tanque.getId())) {
            response = mainFrame.aforos.get(tanque.getId());
        }

        if (response != null) {
            boolean existe = false;

            for (JsonElement element : response) {
                JsonObject jsonAforo = element.getAsJsonObject();
                float alturaAforo = jsonAforo.get("altura_valor").getAsFloat();
                if (alturaAforo == altura) {
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
                float rodamientoAltura = (float) altura - (float) minimo;
                float rodamientoGalone = rodamientoAltura * nuevaAforo;

                cargarAforo = rodamientoGalone + minimoCantidad;

                BigDecimal bigDecimal = new BigDecimal(cargarAforo).setScale(3, RoundingMode.UP);

                cargarAforo = bigDecimal.floatValue();
                                
                if( cargarAforo > volumenMaximo ){
                    NovusUtils.printLn("VOLUMEN SUPERA LA CAPACIDAD MAXIMA DEL TANQUE, VERIFICAR !");
                    jNotificacion.setText("Volumen supera la capacidad maxima del tanque");
                    jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
                    jNotificacion.setVisible(true);
                    setTimeout(5, () -> {
                        jNotificacion.setText("");
                    });
                    cargarAforo = 0f;
                }
                
            }
        }
        tanque.setGalonTanque(cargarAforo);
        return cargarAforo;
    }

    double getTankCapacityAmount(double minMeasure, double maxMeasure, double minCapacityAmount,
            double maxCapacityAmount, double tankInputVolume) {
        double measureMaxMinDiff = maxMeasure - minMeasure;
        double amountMaxMinDiff = maxCapacityAmount - minCapacityAmount;
        double amountDiffDividedBymeasureDiff = amountMaxMinDiff / measureMaxMinDiff;
        double inputVolumeMinMeasureDiff = tankInputVolume - minMeasure;
        double productMeasure = inputVolumeMinMeasureDiff * amountDiffDividedBymeasureDiff;
        return productMeasure + minCapacityAmount;
    }

    boolean validNumberDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    void selectRow(int selectedRow) {
        if (selectedRow > -1) {
            NovusUtils.beep();
            this.btn_remove.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        } else {
            this.btn_remove.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        }
    }

    void selectTankMeasure() {
        final int INDEX_COL_ID = 0;
        if (NovusUtils.isSelectedRow(this.table_measures)) {
            int selectedTankId = (int) this.table_measures
                    .getValueAt(NovusUtils.getSelectedRowIndex(this.table_measures), INDEX_COL_ID);
            BodegaBean selectedTank = this.getTankById(selectedTankId);
            this.loadSelectedDataTank(selectedTank);
            this.selectRow(selectedTankId);
        }
    }

    void loadSelectedDataTank(BodegaBean selectedTank) {
        float altura = (float) (selectedTank.getAltura_total());
        if (selectedTank != null) {
            this.combo_tanks.setSelectedIndex(this.getSelectedTankIndexComboById(selectedTank.getNumeroStand()));
            this.setTextInputValue(this.txt_altura, altura + "");
            this.setTextInputValue(this.txt_water, selectedTank.getAltura_agua() + "");
        }
    }

    int getSelectedTankIndexComboById(int tankId) {
        int selectedIndex = 0;
        if (this.COMBO_TANKS_OPTIONS != null) {
            for (Map.Entry<Integer, JsonObject> entry : this.COMBO_TANKS_OPTIONS.entrySet()) {
                JsonObject optionObject = entry.getValue();
                int id = optionObject.get("id").getAsInt();
                if (id == tankId) {
                    selectedIndex = entry.getKey();
                    break;
                }
            }
        }
        return selectedIndex;
    }

    void removeSelectedDataMeasure(BodegaBean selectedTank) {
        if (selectedTank != null) {
            selectedTank.setAltura_agua(0);
            selectedTank.setAltura_total(0);
            selectedTank.setGalonTanque(-1);
        }
        this.resetFields();
        this.renderTanksMeasures();
    }

    void handleRemoveButton() {
        final int INDEX_COL_ID = 0;
        if (NovusUtils.isSelectedRow(this.table_measures)) {
            int selectedTankId = (int) this.table_measures
                    .getValueAt(NovusUtils.getSelectedRowIndex(this.table_measures), INDEX_COL_ID);
            BodegaBean selectedTank = this.getTankById(selectedTankId);
            this.removeSelectedDataMeasure(selectedTank);
            this.btn_remove.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        }
    }

    void handleTableClick() {
        this.selectTankMeasure();
    }

    boolean validateAllMeasures() {
        ArrayList<BodegaBean> loadedTanks = mainFrame.getLoadedTanks();
        if (loadedTanks != null) {
            for (BodegaBean tank : loadedTanks) {
                if (!tank.getProductos().isEmpty()) {
                    if (tank.getGalonTanque() <= 0) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }

        return !loadedTanks.isEmpty();
    }

    void IncioTurnoView() {
        InfoViewController.instanciaInicioTurno = TurnosIniciarViewController.getInstance(this.mainFrame, this.selectedTotalizers, mainFrame.getLoadedTanks(), true);
        InfoViewController.instanciaInicioTurno.setViewSurtidor(this.sview);
        InfoViewController.instanciaInicioTurno.setViewTanques(this);
        InfoViewController.instanciaInicioTurno.setVisible(true);
    }

    void sendMeasureTanks() {
        JsonObject response = this.fetchSendMeasureTanks();
        if (response != null) {
            String error = response.get("mensaje") != null ? response.get("mensaje").getAsString() : " ";
            if (error.equalsIgnoreCase("unauthorized")) {
                showMessage("HA ALCANZADO EL MAXIMO DE INTENTOS PERMITIDOS",
                        "/com/firefuel/resources/btBad.png", true, this::cerrar, 
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                showMessage("MEDIDAS TANQUES ACTUALIZADOS CORRECTAMENTE",
                        "/com/firefuel/resources/btOk.png", true, this::cerrar, 
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("HA OCURRIDO UN ERROR AL GUARDAR MEDIDAS", 
                    "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    JsonObject fetchSendMeasureTanks() {
        return BodegasFacade.sendTanksMeasures(mainFrame.getLoadedTanks());
    }

    void sendMeasuresTanksData() {
        if (this.handler == null) {
            if (this.isActiveConsole() && mainFrame.getLoadedTanks() != null && mainFrame.getLoadedTanks().size() > 0) {
                if (this.independiente) {
                    sendMeasureTanks();
                } else {
                    this.dispose();
                    IncioTurnoView();
                }
            } else {
                if (mainFrame.getLoadedTanks() != null && mainFrame.getLoadedTanks().size() > 0 && this.validateAllMeasures()) {
                    if (this.independiente) {
                        sendMeasureTanks();
                    } else {
                        this.dispose();
                        IncioTurnoView();
                    }
                } else {
                    showMessage("<html><center>DEBE INGRESARSE MEDIDAS A TODOS LOS TANQUES CARGADOS</center></html>",
                            "/com/firefuel/resources/btBad.png", true, this::mostrarMenuPrincipal, 
                            true, LetterCase.FIRST_UPPER_CASE);
                    return;
                }
            }
        } else {
            if (mainFrame.getLoadedTanks() != null && mainFrame.getLoadedTanks().size() > 0 && this.validateAllMeasures()) {
                this.setVisible(false);
                this.callHandler(mainFrame.getLoadedTanks());
                this.dispose();
            } else {
                showMessage("DEBE INGRESARSE MEDIDAS A TODOS LOS TANQUES CARGADOS", 
                        "/com/firefuel/resources/btBad.png", true, 
                        this::mostrarMenuPrincipal, true, LetterCase.FIRST_UPPER_CASE);
            }
        }
    }

    void callHandler(ArrayList<BodegaBean> loadedTanks) {
        if (this.handler != null) {
            this.handler.run(loadedTanks);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_principal = new javax.swing.JPanel();
        panel_home = new javax.swing.JPanel();
        btn_remove = new javax.swing.JLabel();
        pnl_keyboard = new TecladoExtendido();
        btn_add = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        combo_tanks = new javax.swing.JComboBox<>();
        txt_altura = new javax.swing.JTextField();
        txt_water = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_measures = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        btn_save = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        panel_principal.setLayout(new java.awt.CardLayout());

        panel_home.setLayout(null);

        btn_remove.setFont(new java.awt.Font("Bebas Neue", 1, 24)); // NOI18N
        btn_remove.setForeground(new java.awt.Color(255, 255, 255));
        btn_remove.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_remove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        btn_remove.setText("BORRAR");
        btn_remove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_remove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_removeMouseReleased(evt);
            }
        });
        panel_home.add(btn_remove);
        btn_remove.setBounds(60, 290, 190, 60);

        pnl_keyboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnl_keyboardMouseReleased(evt);
            }
        });
        panel_home.add(pnl_keyboard);
        pnl_keyboard.setBounds(170, 360, 1024, 340);

        btn_add.setFont(new java.awt.Font("Bebas Neue", 1, 24)); // NOI18N
        btn_add.setForeground(new java.awt.Color(255, 255, 255));
        btn_add.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_add.setText("AÑADIR");
        btn_add.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_addMouseReleased(evt);
            }
        });
        panel_home.add(btn_add);
        btn_add.setBounds(310, 290, 190, 60);

        jLabel10.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(186, 12, 47));
        jLabel10.setText("TANQUE:");
        panel_home.add(jLabel10);
        jLabel10.setBounds(20, 105, 130, 40);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        panel_home.add(jLabel2);
        jLabel2.setBounds(20, 0, 70, 80);

        txtCantidad.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        txtCantidad.setForeground(new java.awt.Color(186, 12, 47));
        txtCantidad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtCantidad.setText("0.0");
        panel_home.add(txtCantidad);
        txtCantidad.setBounds(280, 200, 230, 40);

        jLabel11.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(186, 12, 47));
        jLabel11.setText("ALTURA TANQUE:");
        panel_home.add(jLabel11);
        jLabel11.setBounds(20, 160, 250, 40);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(186, 12, 47));
        jLabel12.setText("CANTIDAD COMBUSTIBLE");
        panel_home.add(jLabel12);
        jLabel12.setBounds(20, 200, 250, 40);

        combo_tanks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_tanksActionPerformed(evt);
            }
        });
        panel_home.add(combo_tanks);
        combo_tanks.setBounds(130, 105, 380, 40);

        txt_altura.setBackground(new java.awt.Color(186, 12, 47));
        txt_altura.setFont(new java.awt.Font("Roboto", 1, 26)); // NOI18N
        txt_altura.setForeground(new java.awt.Color(255, 255, 255));
        txt_altura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_altura.setBorder(null);
        txt_altura.setCaretColor(new java.awt.Color(255, 255, 0));
        txt_altura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_alturaFocusGained(evt);
            }
        });
        txt_altura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_alturaActionPerformed(evt);
            }
        });
        txt_altura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_alturaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_alturaKeyTyped(evt);
            }
        });
        panel_home.add(txt_altura);
        txt_altura.setBounds(280, 160, 230, 37);

        txt_water.setBackground(new java.awt.Color(186, 12, 47));
        txt_water.setFont(new java.awt.Font("Roboto", 1, 26)); // NOI18N
        txt_water.setForeground(new java.awt.Color(255, 255, 255));
        txt_water.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_water.setBorder(null);
        txt_water.setCaretColor(new java.awt.Color(255, 255, 0));
        txt_water.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_waterFocusGained(evt);
            }
        });
        txt_water.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_waterKeyTyped(evt);
            }
        });
        panel_home.add(txt_water);
        txt_water.setBounds(280, 240, 230, 37);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("ALTURA AGUA:");
        panel_home.add(jLabel6);
        jLabel6.setBounds(20, 240, 250, 40);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        table_measures.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        table_measures.setFont(new java.awt.Font("Terpel Sans", 1, 14)); // NOI18N
        table_measures.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO.", "TANQUE", "PRODUCTO ", "A. VOLUMEN", "A. AGUA", "CANTIDAD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_measures.setToolTipText("");
        table_measures.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        table_measures.setRowHeight(30);
        table_measures.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_measures.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_measures.getTableHeader().setReorderingAllowed(false);
        table_measures.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                table_measuresMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table_measures);
        if (table_measures.getColumnModel().getColumnCount() > 0) {
            table_measures.getColumnModel().getColumn(0).setResizable(false);
            table_measures.getColumnModel().getColumn(0).setPreferredWidth(25);
            table_measures.getColumnModel().getColumn(1).setResizable(false);
            table_measures.getColumnModel().getColumn(1).setPreferredWidth(150);
            table_measures.getColumnModel().getColumn(2).setResizable(false);
            table_measures.getColumnModel().getColumn(2).setPreferredWidth(150);
            table_measures.getColumnModel().getColumn(3).setResizable(false);
            table_measures.getColumnModel().getColumn(4).setResizable(false);
            table_measures.getColumnModel().getColumn(5).setResizable(false);
        }

        panel_home.add(jScrollPane1);
        jScrollPane1.setBounds(530, 90, 750, 260);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("MEDIDAS TANQUES");
        panel_home.add(jLabel7);
        jLabel7.setBounds(130, 0, 500, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        panel_home.add(jNotificacion);
        jNotificacion.setBounds(560, 10, 700, 70);

        btn_save.setFont(new java.awt.Font("Bebas Neue", 1, 24)); // NOI18N
        btn_save.setForeground(new java.awt.Color(255, 255, 255));
        btn_save.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_save.setText("GUARDAR");
        btn_save.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_saveMouseReleased(evt);
            }
        });
        panel_home.add(btn_save);
        btn_save.setBounds(1070, 720, 180, 60);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setOpaque(true);
        panel_home.add(jLabel3);
        jLabel3.setBounds(20, 160, 495, 190);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_ingreso_lecturas.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        panel_home.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        panel_principal.add(panel_home, "panel_home");

        getContentPane().add(panel_principal);
        panel_principal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_alturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_alturaKeyTyped
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, txt_altura, 6, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txt_alturaKeyTyped

    private void txt_waterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_waterKeyTyped
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, txt_water, 6, jNotificacion, caracteresAceptados);

    }//GEN-LAST:event_txt_waterKeyTyped

    private void txt_alturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_alturaFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_altura);
    }//GEN-LAST:event_txt_alturaFocusGained

    private void txt_waterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_waterFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_water);
    }//GEN-LAST:event_txt_waterFocusGained

    private void pnl_keyboardMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_pnl_keyboardMouseReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_pnl_keyboardMouseReleased

    private void btn_addMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_addMouseReleased
        
        String alturaText = txt_altura.getText();
        
         if (alturaText.trim().isEmpty()) {
            NovusUtils.printLn("El campo de altura está vacío. Por favor, ingrese un valor.");
             
            jNotificacion.setText("La altura debe ser mayor a 0");
            jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
            jNotificacion.setVisible(true);
            setTimeout(3, () -> {
                jNotificacion.setText("");
            });
             
         } else {
            float altura = Float.parseFloat(txt_altura.getText());
            boolean seguir = obtenerLecturas(altura);
            if (!this.readOnlyView && seguir) {
                this.handleAddButton();
            }
            
         }
        
    }// GEN-LAST:event_btn_addMouseReleased

    private void btn_removeMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_removeMouseReleased
        this.handleRemoveButton();
    }// GEN-LAST:event_btn_removeMouseReleased

    private void combo_tanksActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_combo_tanksActionPerformed
        if (this.combo_tanks.isEnabled()) {
            this.handleComboTanksChange();
        }
    }// GEN-LAST:event_combo_tanksActionPerformed

    private void txt_alturaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txt_alturaActionPerformed
        float altura = Float.parseFloat(txt_altura.getText());
        boolean seguir = obtenerLecturas(altura);
        if (seguir) {
            actualizaValorEnCantidad();
        }

    }// GEN-LAST:event_txt_alturaActionPerformed

    private void btn_saveMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_saveMouseReleased        
        if (btn_save.isEnabled()) {
            this.sendMeasuresTanksData();
        }

    }// GEN-LAST:event_btn_saveMouseReleased

    private void table_measuresMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_table_measuresMouseReleased
        if (!this.readOnlyView) {
            this.handleTableClick();
        }
    }// GEN-LAST:event_table_measuresMouseReleased

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    private void txt_alturaKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_alturaKeyReleased
        actualizaValorEnCantidad();
    }// GEN-LAST:event_txt_alturaKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_add;
    private javax.swing.JLabel btn_remove;
    private javax.swing.JLabel btn_save;
    private javax.swing.JComboBox<String> combo_tanks;
    public javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panel_home;
    private javax.swing.JPanel panel_principal;
    private javax.swing.JPanel pnl_keyboard;
    private javax.swing.JTable table_measures;
    private javax.swing.JLabel txtCantidad;
    public static javax.swing.JTextField txt_altura;
    public static javax.swing.JTextField txt_water;
    // End of variables declaration//GEN-END:variables

    private void actualizaValorEnCantidad() {
        if (txt_altura.getText().length() == 0) {
            txtCantidad.setText("VALOR ES: 0.0");
            return;
        }
        new Thread(() -> {
            try {
                int tankId = this.getSelectedTank().get("id").getAsInt();
                BodegaBean tank = this.getTankById(tankId);
                float altura = Float.parseFloat(txt_altura.getText());
                float valor = cargarAforo(tank, altura);
                txtCantidad.setText("VALOR ES: " + valor);
                System.out.println("------------" + valor + "--------------");
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    private boolean obtenerLecturas(float altura) {
        boolean seguir = true;
        //SurtidorDao dao = new SurtidorDao();
        // int tankId = this.getSelectedTank().get("id").getAsInt();

        JsonObject selectedTank = this.getSelectedTank();

        if (selectedTank == null) {
            jNotificacion.setText("No hay tanque seleccionado");
            jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
            jNotificacion.setVisible(true);
            setTimeout(3, () -> {
                jNotificacion.setText("");
            });
            return false;
        }

        int tankId = selectedTank.get("id").getAsInt();
        BodegaBean tank = this.getTankById(tankId);

        if (tank == null) {
            jNotificacion.setText("No se encontró información del tanque");
            jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
            jNotificacion.setVisible(true);
            setTimeout(3, () -> {
                jNotificacion.setText("");
            });
            return false;
        }

        JsonObject json = obtenerCapacidadMaximaUseCase.execute(tank.getId());
       
        if (json == null || json.entrySet().isEmpty()) {
            jNotificacion.setText("No se pudo obtener la capacidad del tanque");
            jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
            jNotificacion.setVisible(true);
            setTimeout(3, () -> {
                jNotificacion.setText("");
            });
            return false;
        }

        long alturaMaxima = json.get("altura_maxima").getAsLong();
        long volumenMaximo = json.get("volumen_maximo").getAsLong();
        
        if (altura > alturaMaxima) {
            tank.setGalonTanque(0);
            jNotificacion.setText("La altura ingresada supera la capacidad maxima");
            jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
            jNotificacion.setVisible(true);
            txt_altura.setText("");
            txtCantidad.setText("");
            setTimeout(3, () -> {
                jNotificacion.setText("");
            });
            seguir = false;
        } else if (altura <= 0) {
            tank.setGalonTanque(0);
            jNotificacion.setText("La altura debe ser mayor a 0");
            jNotificacion.setFont(new java.awt.Font("Arial", 0, 24));
            jNotificacion.setVisible(true);
            setTimeout(3, () -> {
                jNotificacion.setText("");
            });
            seguir = false;
        }
        return seguir;
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
        CardLayout layout = (CardLayout) panel_principal.getLayout();
        panel_principal.add("pnl_ext", panel);
        layout.show(panel_principal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) panel_principal.getLayout();
        layout.show(panel_principal, "panel_home");
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
}

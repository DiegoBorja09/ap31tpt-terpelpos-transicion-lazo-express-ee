package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.dispositivos.DispositivoDto;
import com.application.useCases.dispositivos.EditarDispositivoUseCase;
import com.application.useCases.equipos.EliminarDispositivoUseCase;
import com.application.useCases.equipos.GetDispositivosInfoUseCase;
import com.application.useCases.equipos.IngresarDispositivoUseCase;
import com.application.useCases.equipos.IsValidComUseCase;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.controllers.SetupAsync;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.domain.entities.DispositivoEntity;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.awt.CardLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import teclado.view.common.TecladoExtendido;

public class ParametrizacionesViewController extends javax.swing.JDialog {

    TreeMap<String, String> parametrizaciones = new TreeMap<>();
    InfoViewController parent;
    ParametrizacionesViewController base;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    DefaultTableCellRenderer textRight;
    EditarDispositivoUseCase useCase ;

    DefaultTableCellRenderer textCenter;
    IngresarDispositivoUseCase ingresarDispositivoUseCase;
    TreeMap<Long, DispositivoEntity> Dispositivos = new TreeMap<>();
    EquipoDao dao = new EquipoDao();
    String BOOLEAN = "BOOLEAN";
    String TEXT = "TEXT";
    String Estado = "A";
    String NUMBER = "NUMBER";
    String STRING = "STRING";
    int id;
    int W_JSON = 8;

    HashSet<String> caras = new HashSet<>();

    void solicitarDatosSurtidor() {
        SetupDao sdao = new SetupDao();
        caras = sdao.getOnlyCaras();
        this.CargarCaras();
    }

    public ParametrizacionesViewController(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    void init() {
        inicio();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        cargarParametrizaciones();
        this.solicitarDatosSurtidor();
        this.useCase = new EditarDispositivoUseCase();

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

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        this.actualizarVista();
        jLabel11.setVisible(false);
        txt_cntS.setVisible(false);

    }

    void actualizarVista() {
        this.renderizarCambios();
    }

    boolean existe() {
        boolean existe = false;
        for (Map.Entry<Long, DispositivoEntity> entry : Dispositivos.entrySet()) {
            String conectores = entry.getValue().getConector();

            try {
                if (conectores.equals(jComboConector.getSelectedItem())) {
                    existe = true;
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
        return existe;
    }

    boolean existeTraductor() {

        boolean used = false;

        int n = jTable1.getRowCount();
        String[] existente = new String[n];

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String Traductor = (String) jTable1.getValueAt(i, 6).toString();
            existente[i] = Traductor;
        }

        String traductosW = jRfid5.getText();
        String[] traductorWA = traductosW.split(";");

        if (traductorWA.length <= 0 && existente.length <= 0) {
            used = false;
        } else {
            for (int i = 0; i < traductorWA.length; i++) {
                for (int j = 0; j < existente.length; j++) {
                    if (traductorWA[i].equals(existente[j])) {
                        used = true;
                        break;
                    }
                }
            }
        }
        return used;
    }

    boolean existePuerto() {
        boolean used = false;
        String puertoT = "";
        String puertoU = jIPuertoTCP.getText();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            puertoT = (String) jTable1.getValueAt(i, 3).toString();
            if (puertoU.equals(puertoT)) {
                used = true;
                break;
            }
        }
        return used;
    }

    private void renderizarCambios() {
        this.SolicitarInfo();
        jTable1.setAutoCreateRowSorter(false);
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        for (Map.Entry<Long, DispositivoEntity> entry : Dispositivos.entrySet()) {
            DispositivoEntity dispositivos = entry.getValue();
            try {
                String TipoAutorizacion = "";
                String Traductor = "";
                try {
                    JsonObject Atributos = Main.gson.fromJson(dispositivos.getAtributos(), JsonObject.class);
                    Traductor = Atributos.get("Traductor") != null ? Atributos.get("Traductor").getAsString() : "";
                    TipoAutorizacion = Atributos.get("TipoAutorizacion") != null ? Atributos.get("TipoAutorizacion").getAsString() : "";
                } catch (JsonSyntaxException e) {
                    Logger.getLogger(ParametrizacionesViewController.class.getName()).log(Level.SEVERE, null, e);
                }

                model.addRow(new Object[]{dispositivos.getId(), dispositivos.getTipos(), dispositivos.getInterfaz(),
                    dispositivos.getConector(), dispositivos.getEstado(), TipoAutorizacion, Traductor
                });

            } catch (Exception e) {
                Logger.getLogger(ParametrizacionesViewController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        jTable1.setModel(model);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jComboAuto = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        txt_cntS = new javax.swing.JTextField();
        jventasCheck = new javax.swing.JCheckBox();
        jtanquesCheck = new javax.swing.JCheckBox();
        jimprimirsobres = new javax.swing.JCheckBox();
        jimpresionautoCheck = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txt_id = new javax.swing.JTextField();
        jLagregar = new javax.swing.JLabel();
        jLeditar = new javax.swing.JLabel();
        jLimpiar = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLPuerto = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jComboTipo = new javax.swing.JComboBox<>();
        jLeliminar3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboPR1 = new javax.swing.JComboBox<>();
        jCombo_CarasR = new javax.swing.JComboBox<>();
        btn_guardarR1 = new javax.swing.JLabel();
        jRfid5 = new javax.swing.JTextField();
        jLbdDatos = new javax.swing.JLabel();
        btn_limpiarR1 = new javax.swing.JLabel();
        jIPuertoTCP = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jComboConector = new javax.swing.JComboBox<>();
        jComboInterFaz = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new TecladoExtendido();
        jpromotor = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small2.png"))); // NOI18N
        jButton4.setText("GUARDAR");
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton4MouseReleased(evt);
            }
        });
        pnl_principal.add(jButton4);
        jButton4.setBounds(1110, 20, 130, 40);

        jTabbedPane1.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseReleased(evt);
            }
        });

        jPanel2.setOpaque(false);
        jPanel2.setLayout(null);

        jLabel11.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("DIGITOS SURTIDOR");
        jPanel2.add(jLabel11);
        jLabel11.setBounds(350, 20, 200, 30);

        jComboAuto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "POR JORNADA", "GLOBAL", "POR CARA" }));
        jComboAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboAutoActionPerformed(evt);
            }
        });
        jPanel2.add(jComboAuto);
        jComboAuto.setBounds(40, 50, 200, 40);

        jLabel18.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("TIPO AUTORIZACION RFID");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(40, 20, 240, 30);

        txt_cntS.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_cntS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_cntSMouseReleased(evt);
            }
        });
        txt_cntS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cntSActionPerformed(evt);
            }
        });
        jPanel2.add(txt_cntS);
        txt_cntS.setBounds(350, 50, 60, 40);

        jventasCheck.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jventasCheck.setText("   PLACA OBLIGATORIA IMPRESION VENTAS");
        jventasCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jventasCheckActionPerformed(evt);
            }
        });
        jPanel2.add(jventasCheck);
        jventasCheck.setBounds(40, 140, 440, 40);

        jtanquesCheck.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jtanquesCheck.setText("   INGRESO MEDIDAS DE TANQUES");
        jtanquesCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtanquesCheckActionPerformed(evt);
            }
        });
        jPanel2.add(jtanquesCheck);
        jtanquesCheck.setBounds(500, 140, 420, 40);

        jimprimirsobres.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jimprimirsobres.setText("   IMPRIMIR CONSIGNACION DE SOBRES");
        jimprimirsobres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jimprimirsobresActionPerformed(evt);
            }
        });
        jPanel2.add(jimprimirsobres);
        jimprimirsobres.setBounds(500, 180, 470, 40);

        jimpresionautoCheck.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jimpresionautoCheck.setText("   IMPRESIÓN FACTURA AUTOMATICA");
        jimpresionautoCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jimpresionautoCheckActionPerformed(evt);
            }
        });
        jPanel2.add(jimpresionautoCheck);
        jimpresionautoCheck.setBounds(40, 180, 410, 40);

        jLabel9.setBackground(new java.awt.Color(255, 255, 0));
        jLabel9.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel9.setText("<html>\n<center>\nLa entrega de factura es obligatoria según la resolución 42 del 05 de Mayo del 2020, esta opción se inactiva bajo su responsabilidad y riesgo\n</center>\n</html>\n");
        jLabel9.setOpaque(true);
        jPanel2.add(jLabel9);
        jLabel9.setBounds(30, 230, 460, 100);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("GLOBALES");
        jPanel2.add(jLabel17);
        jLabel17.setBounds(40, 110, 90, 30);

        jTabbedPane1.addTab("PARAMETRIZACION", jPanel2);

        jPanel4.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.setOpaque(false);
        jPanel4.setLayout(null);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "TIPO", "INTERFAZ", "CONECTOR", "ESTADO", "TIPO AUTORIZACION", "TRADUCTOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(35);
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
            jTable1.getColumnModel().getColumn(0).setMinWidth(60);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(60);
            jTable1.getColumnModel().getColumn(4).setMinWidth(70);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(4).setMaxWidth(70);
        }

        jPanel4.add(jScrollPane2);
        jScrollPane2.setBounds(180, 140, 930, 170);

        txt_id.setEditable(false);
        txt_id.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_id.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_id.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_idMouseReleased(evt);
            }
        });
        txt_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idActionPerformed(evt);
            }
        });
        jPanel4.add(txt_id);
        txt_id.setBounds(80, 30, 60, 50);

        jLagregar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLagregar.setForeground(new java.awt.Color(255, 255, 255));
        jLagregar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLagregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-new-small.png"))); // NOI18N
        jLagregar.setText("AGREGAR");
        jLagregar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLagregar.setMaximumSize(new java.awt.Dimension(100, 20));
        jLagregar.setMinimumSize(new java.awt.Dimension(100, 20));
        jLagregar.setPreferredSize(new java.awt.Dimension(100, 20));
        jLagregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLagregarMouseReleased(evt);
            }
        });
        jPanel4.add(jLagregar);
        jLagregar.setBounds(370, 100, 70, 40);

        jLeditar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLeditar.setForeground(new java.awt.Color(255, 255, 255));
        jLeditar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLeditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-new-small.png"))); // NOI18N
        jLeditar.setText("EDITAR");
        jLeditar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLeditar.setMaximumSize(new java.awt.Dimension(100, 20));
        jLeditar.setMinimumSize(new java.awt.Dimension(100, 20));
        jLeditar.setPreferredSize(new java.awt.Dimension(100, 20));
        jLeditar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLeditarMouseReleased(evt);
            }
        });
        jPanel4.add(jLeditar);
        jLeditar.setBounds(510, 100, 70, 40);

        jLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        jLimpiar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-new-small.png"))); // NOI18N
        jLimpiar.setText("LIMPIAR");
        jLimpiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLimpiar.setMaximumSize(new java.awt.Dimension(100, 20));
        jLimpiar.setMinimumSize(new java.awt.Dimension(100, 20));
        jLimpiar.setPreferredSize(new java.awt.Dimension(100, 20));
        jLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLimpiarMouseReleased(evt);
            }
        });
        jPanel4.add(jLimpiar);
        jLimpiar.setBounds(790, 100, 70, 40);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("ID");
        jPanel4.add(jLabel13);
        jLabel13.setBounds(70, 10, 80, 16);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("TIPO");
        jPanel4.add(jLabel14);
        jLabel14.setBounds(190, 10, 110, 16);

        jLPuerto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLPuerto.setForeground(new java.awt.Color(255, 255, 255));
        jLPuerto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLPuerto.setText("PUERTO TCP");
        jPanel4.add(jLPuerto);
        jLPuerto.setBounds(610, 10, 120, 16);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("INTERFAZ");
        jPanel4.add(jLabel16);
        jLabel16.setBounds(320, 10, 120, 16);

        jComboTipo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jComboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IBUTTON", "RFID" }));
        jComboTipo.setSelectedIndex(-1);
        jComboTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboTipoItemStateChanged(evt);
            }
        });
        jComboTipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jComboTipoMouseReleased(evt);
            }
        });
        jPanel4.add(jComboTipo);
        jComboTipo.setBounds(190, 30, 110, 50);

        jLeliminar3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLeliminar3.setForeground(new java.awt.Color(255, 255, 255));
        jLeliminar3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLeliminar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-new-small.png"))); // NOI18N
        jLeliminar3.setText("ELIMINAR");
        jLeliminar3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLeliminar3.setMaximumSize(new java.awt.Dimension(100, 20));
        jLeliminar3.setMinimumSize(new java.awt.Dimension(100, 20));
        jLeliminar3.setPreferredSize(new java.awt.Dimension(100, 20));
        jLeliminar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLeliminar3MouseReleased(evt);
            }
        });
        jPanel4.add(jLeliminar3);
        jLeliminar3.setBounds(650, 100, 70, 40);

        jLabel8.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("TRADUCTOR");
        jPanel4.add(jLabel8);
        jLabel8.setBounds(740, 0, 120, 30);

        jComboPR1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jComboPR1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "P1", "P2", "P3", "P4" }));
        jPanel4.add(jComboPR1);
        jComboPR1.setBounds(740, 30, 50, 50);

        jCombo_CarasR.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jCombo_CarasR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_CarasRActionPerformed(evt);
            }
        });
        jPanel4.add(jCombo_CarasR);
        jCombo_CarasR.setBounds(800, 30, 60, 50);

        btn_guardarR1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_guardarR1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/add.png"))); // NOI18N
        btn_guardarR1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_guardarR1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_guardarR1MouseReleased(evt);
            }
        });
        jPanel4.add(btn_guardarR1);
        btn_guardarR1.setBounds(870, 30, 60, 50);

        jRfid5.setEditable(false);
        jRfid5.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jRfid5.setFocusable(false);
        jRfid5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jRfid5MouseReleased(evt);
            }
        });
        jRfid5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRfid5ActionPerformed(evt);
            }
        });
        jPanel4.add(jRfid5);
        jRfid5.setBounds(930, 30, 250, 50);

        jLbdDatos.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLbdDatos.setForeground(new java.awt.Color(255, 255, 255));
        jLbdDatos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLbdDatos.setText("BD: 1=C5;2=C6;3=C7;4=C8");
        jPanel4.add(jLbdDatos);
        jLbdDatos.setBounds(930, 80, 290, 20);

        btn_limpiarR1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_limpiarR1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/delete.png"))); // NOI18N
        btn_limpiarR1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_limpiarR1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_limpiarR1MouseReleased(evt);
            }
        });
        jPanel4.add(btn_limpiarR1);
        btn_limpiarR1.setBounds(1180, 30, 50, 50);

        jIPuertoTCP.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jIPuertoTCP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jIPuertoTCPFocusGained(evt);
            }
        });
        jIPuertoTCP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jIPuertoTCPMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jIPuertoTCPMouseReleased(evt);
            }
        });
        jIPuertoTCP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jIPuertoTCPActionPerformed(evt);
            }
        });
        jIPuertoTCP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jIPuertoTCPKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jIPuertoTCPKeyTyped(evt);
            }
        });
        jPanel4.add(jIPuertoTCP);
        jIPuertoTCP.setBounds(610, 30, 120, 50);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("CONECTOR");
        jPanel4.add(jLabel19);
        jLabel19.setBounds(480, 10, 120, 16);

        jComboConector.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jComboConector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10" }));
        jComboConector.setSelectedIndex(-1);
        jComboConector.setToolTipText("");
        jComboConector.setFocusable(false);
        jComboConector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jComboConectorMouseReleased(evt);
            }
        });
        jComboConector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboConectorActionPerformed(evt);
            }
        });
        jPanel4.add(jComboConector);
        jComboConector.setBounds(480, 30, 120, 50);

        jComboInterFaz.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jComboInterFaz.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SERIAL", "TCP" }));
        jComboInterFaz.setSelectedIndex(-1);
        jComboInterFaz.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboInterFazItemStateChanged(evt);
            }
        });
        jComboInterFaz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboInterFazActionPerformed(evt);
            }
        });
        jPanel4.add(jComboInterFaz);
        jComboInterFaz.setBounds(320, 30, 120, 50);

        jTabbedPane1.addTab("DISPOSITIVOS", jPanel4);

        pnl_principal.add(jTabbedPane1);
        jTabbedPane1.setBounds(0, 90, 1250, 410);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel2);
        jLabel2.setBounds(10, 10, 70, 71);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("PARAMETRIZACIONES");
        pnl_principal.add(jLabel7);
        jLabel7.setBounds(110, 0, 720, 90);

        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel5MouseReleased(evt);
            }
        });
        pnl_principal.add(jPanel5);
        jPanel5.setBounds(130, 470, 1024, 330);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        pnl_principal.add(jpromotor);
        jpromotor.setBounds(70, 740, 780, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel30);
        jLabel30.setBounds(1130, 710, 10, 80);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        pnl_principal.add(jpromotor1);
        jpromotor1.setBounds(70, 710, 780, 30);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(80, 5, 10, 80);

        jNotificacion.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jNotificacion.setToolTipText("");
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(810, 20, 450, 50);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseReleased

    }//GEN-LAST:event_jPanel5MouseReleased

    private void jComboInterFazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboInterFazActionPerformed

    }//GEN-LAST:event_jComboInterFazActionPerformed

    private void jComboInterFazItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboInterFazItemStateChanged

        String interfazSel = jComboInterFaz.getSelectedItem() != null ? jComboInterFaz.getSelectedItem().toString() : null;
        String tipoSel = jComboTipo.getSelectedItem() != null ? jComboTipo.getSelectedItem().toString() : null;

        if ("SERIAL".equals(interfazSel) && "IBUTTON".equals(tipoSel)) {
            jIPuertoTCP.setVisible(false);
            jLPuerto.setVisible(false);
            jComboConector.setEnabled(true);
        }
        if ("TCP".equals(interfazSel) && "IBUTTON".equals(tipoSel)) {
            jIPuertoTCP.setVisible(true);
            jIPuertoTCP.setEnabled(true);
            jLPuerto.setVisible(true);
            jComboConector.setEnabled(false);
            jComboConector.setSelectedIndex(-1);
        }
        if ("SERIAL".equals(interfazSel) && "RFID".equals(tipoSel)) {
            jIPuertoTCP.setVisible(false);
            jLPuerto.setVisible(false);
            jComboConector.setEnabled(true);
        }
        if ("USB".equals(interfazSel) && "RFID".equals(tipoSel)) {
            jIPuertoTCP.setVisible(false);
            jLPuerto.setVisible(false);
            jComboConector.setEnabled(true);
        }
        if ("SERIAL".equals(interfazSel) && "RFID2".equals(tipoSel)) {
            jIPuertoTCP.setVisible(false);
            jLPuerto.setVisible(false);
            jComboConector.setEnabled(true);
        }
    }//GEN-LAST:event_jComboInterFazItemStateChanged

    private void jComboConectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboConectorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboConectorActionPerformed

    private void jComboConectorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboConectorMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboConectorMouseReleased

    private void jIPuertoTCPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jIPuertoTCPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jIPuertoTCPActionPerformed

    private void jIPuertoTCPMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jIPuertoTCPMouseReleased
        // TODO add your handling code here:
        desactivarTeclado();
        //CTRL
        if (evt.isControlDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_jIPuertoTCPMouseReleased

    private void btn_limpiarR1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_limpiarR1MouseReleased
        limpiarR();
    }//GEN-LAST:event_btn_limpiarR1MouseReleased

    private void jRfid5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRfid5ActionPerformed

    }//GEN-LAST:event_jRfid5ActionPerformed

    private void jRfid5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRfid5MouseReleased

    }//GEN-LAST:event_jRfid5MouseReleased

    private void btn_guardarR1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_guardarR1MouseReleased
        saveTraduccionR();
    }//GEN-LAST:event_btn_guardarR1MouseReleased

    private void jCombo_CarasRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCombo_CarasRActionPerformed

    }//GEN-LAST:event_jCombo_CarasRActionPerformed

    private void jLeliminar3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLeliminar3MouseReleased
        NovusUtils.beep();
        eliminar();
    }//GEN-LAST:event_jLeliminar3MouseReleased

    private void jComboTipoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboTipoMouseReleased

    }//GEN-LAST:event_jComboTipoMouseReleased

    private void jComboTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboTipoItemStateChanged
        stateComboTipoDispositivo();
    }//GEN-LAST:event_jComboTipoItemStateChanged

    private void jLimpiarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLimpiarMouseReleased
        NovusUtils.beep();
        limpiar();
    }//GEN-LAST:event_jLimpiarMouseReleased

    private void jLeditarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLeditarMouseReleased
        NovusUtils.beep();
        editar();
    }//GEN-LAST:event_jLeditarMouseReleased

    private void jLagregarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLagregarMouseReleased
        NovusUtils.beep();
        agregar();
    }//GEN-LAST:event_jLagregarMouseReleased

    private void txt_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idActionPerformed

    }//GEN-LAST:event_txt_idActionPerformed

    private void txt_idMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_idMouseReleased
        activarTeclado();
        desactivarTeclado();
    }//GEN-LAST:event_txt_idMouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int fila = jTable1.getSelectedRow();
        jCombo_CarasR.removeAllItems();
        jLagregar.setVisible(false);

        id = Integer.parseInt((String) jTable1.getValueAt(fila, 0).toString());
        String tipo = (String) jTable1.getValueAt(fila, 1).toString();
        String inter = (String) jTable1.getValueAt(fila, 2).toString();
        String conc = (String) jTable1.getValueAt(fila, 3).toString();
        String trad = (String) jTable1.getValueAt(fila, 6).toString();
        jLbdDatos.setText(trad);

        txt_id.setText("" + id);

        if (tipo.equals("rfidV2") && inter.equals("serial")) {
            txt_id.setText("" + id);
            jComboTipo.setSelectedItem("RFID");
            jComboConector.setSelectedItem(conc);
            jComboInterFaz.setSelectedItem(inter.toUpperCase());
            jComboConector.setEnabled(false);
            jComboTipo.setEnabled(false);
            jComboInterFaz.setEnabled(false);
            jIPuertoTCP.setEnabled(false);
            CargarCaras();
        } else if (tipo.equals("ibutton") && inter.equals("tcp")) {
            txt_id.setText("" + id);
            jComboTipo.setSelectedItem(tipo.toUpperCase());
            jComboInterFaz.setSelectedItem(inter.toUpperCase());
            jComboTipo.setEnabled(false);
            jComboInterFaz.setEnabled(false);
            jIPuertoTCP.setEnabled(false);
            jIPuertoTCP.setText(conc);
            CargarCaras();
        } else if (tipo.equals("ibutton") && inter.equals("serial")) {
            txt_id.setText("" + id);
            jComboTipo.setSelectedItem(tipo.toUpperCase());
            jComboInterFaz.setSelectedItem(inter.toUpperCase());
            jComboConector.setSelectedItem(conc);
            jComboConector.setEnabled(false);
            jComboTipo.setEnabled(false);
            jComboInterFaz.setEnabled(false);
            jIPuertoTCP.setEnabled(false);
            CargarCaras();
        } else if (tipo.equals("rfid") && inter.equals("usb")) {
            txt_id.setText("" + id);
            jComboTipo.setSelectedItem(tipo.toUpperCase());
            jComboInterFaz.setSelectedItem(inter.toUpperCase());
            jComboTipo.setEnabled(false);
            jComboInterFaz.setEnabled(false);
            jComboConector.setSelectedItem(conc);
            jComboConector.setEnabled(false);
            CargarCaras();
        }
        //CargarCaras();
        jRfid5.setText("" + trad);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jimpresionautoCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jimpresionautoCheckActionPerformed
        cambiarEstado(NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA);
    }//GEN-LAST:event_jimpresionautoCheckActionPerformed

    private void jimprimirsobresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jimprimirsobresActionPerformed
        cambiarEstado(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES);
    }//GEN-LAST:event_jimprimirsobresActionPerformed

    private void jtanquesCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtanquesCheckActionPerformed
        cambiarEstado(NovusConstante.PREFERENCE_MEDIDAS_TANQUES);
    }//GEN-LAST:event_jtanquesCheckActionPerformed

    private void jventasCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jventasCheckActionPerformed
        cambiarEstado(NovusConstante.PREFERENCE_PLACA_IMPRESION);
    }//GEN-LAST:event_jventasCheckActionPerformed

    private void jButton4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseReleased
        guardar();
    }//GEN-LAST:event_jButton4MouseReleased

    private void txt_cntSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cntSActionPerformed
        guardar();
    }//GEN-LAST:event_txt_cntSActionPerformed

    private void txt_cntSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_cntSMouseReleased
        mostrarTeclado();
        desactivarTeclado();
    }//GEN-LAST:event_txt_cntSMouseReleased

    private void jComboAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboAutoActionPerformed

    }//GEN-LAST:event_jComboAutoActionPerformed

    private void jTabbedPane1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseReleased

    }//GEN-LAST:event_jTabbedPane1MouseReleased

    private void jIPuertoTCPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jIPuertoTCPKeyTyped

        String caracteresPermitidos = "[0-9]";
        NovusUtils.limitarCarateres(evt, jIPuertoTCP, 4, jNotificacion, caracteresPermitidos);
    }//GEN-LAST:event_jIPuertoTCPKeyTyped

    private void jIPuertoTCPMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jIPuertoTCPMousePressed

    }//GEN-LAST:event_jIPuertoTCPMousePressed

    private void jIPuertoTCPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jIPuertoTCPKeyPressed
        //CTRL
        if (evt.isControlDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_jIPuertoTCPKeyPressed

    private void jIPuertoTCPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jIPuertoTCPFocusGained
        NovusUtils.deshabilitarCopiarPegar(jIPuertoTCP);
    }//GEN-LAST:event_jIPuertoTCPFocusGained

    // GEN-LAST:event_jimpresionautoCheckActionPerformed
    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        NovusUtils.beep();
        guardar();
    }// GEN-LAST:event_jLabel11MouseReleased

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_guardarR1;
    private javax.swing.JLabel btn_limpiarR1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboAuto;
    private javax.swing.JComboBox<String> jComboConector;
    private javax.swing.JComboBox<String> jComboInterFaz;
    private javax.swing.JComboBox<String> jComboPR1;
    private javax.swing.JComboBox<String> jComboTipo;
    private javax.swing.JComboBox<String> jCombo_CarasR;
    private javax.swing.JTextField jIPuertoTCP;
    private javax.swing.JLabel jLPuerto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLagregar;
    private javax.swing.JLabel jLbdDatos;
    private javax.swing.JLabel jLeditar;
    private javax.swing.JLabel jLeliminar3;
    private javax.swing.JLabel jLimpiar;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jRfid5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JCheckBox jimpresionautoCheck;
    private javax.swing.JCheckBox jimprimirsobres;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JCheckBox jtanquesCheck;
    private javax.swing.JCheckBox jventasCheck;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private javax.swing.JTextField txt_cntS;
    private javax.swing.JTextField txt_id;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        dispose();
    }

    private void CargarCaras() {
        jCombo_CarasR.removeAll();
        for (String cara : caras) {
            jCombo_CarasR.addItem("C" + cara);
        }
    }

    ArrayList<String> listaR = new ArrayList<>();

    private void saveTraduccionR() {

        String opcion = "";
        switch (jComboPR1.getSelectedItem().toString()) {
            case "P1":
                opcion = "1";
                break;
            case "P2":
                opcion = "2";
                break;
            case "P3":
                opcion = "3";
                break;
            case "P4":
                opcion = "4";
                break;
        }

        String p = opcion;
        String cara = jCombo_CarasR.getSelectedItem().toString();
        jCombo_CarasR.removeItemAt(jCombo_CarasR.getSelectedIndex());

        listaR.add(p + "=" + cara + ";");

        HashSet<String> hashSet = new HashSet<>(listaR);
        listaR.clear();

        listaR.addAll(hashSet);
        String salida = "";

        for (String string : hashSet) {
            salida = string + salida;
        }

        String traduccion = salida;
        traduccion = traduccion.substring(0, traduccion.length() - 1);
        jRfid5.setText(traduccion);
    }

    int posicion = 0;

//Se Cargan los Datos de la Tabla de Parametros 
    private void inicio() {

        String Traductor = "";
        String conect = "";
        for (Map.Entry<Long, DispositivoEntity> entry : Dispositivos.entrySet()) {
            DispositivoEntity dispositivos = entry.getValue();

            try {
                JsonObject Atributos = Main.gson.fromJson(dispositivos.getAtributos(), JsonObject.class);
                Traductor = Atributos.get("Traductor").getAsString();
                conect = Atributos.get("conector").getAsString();
            } catch (JsonSyntaxException e) {
                Logger.getLogger(ParametrizacionesViewController.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", "");

        JsonObject respuesta = dao.getParametrosInicio(NovusConstante.PARAMENTO_IBUTTON_SERIAL, NovusConstante.PARAMETRO_IBUTTON_PUERTO,
                NovusConstante.PARAMETRO_RFID_V2, NovusConstante.PARAMETRO_RFID_CARA, NovusConstante.PARAMETRO_RFID_V1);

        String ibutton_cara = respuesta.get(NovusConstante.PARAMENTO_IBUTTON_SERIAL).getAsString();
        String ibutton_puerto = respuesta.get(NovusConstante.PARAMETRO_IBUTTON_PUERTO).getAsString();
        String rfid = respuesta.get(NovusConstante.PARAMETRO_RFID_V1).getAsString();
        String rfid2_puerto = respuesta.get(NovusConstante.PARAMETRO_RFID_V2).getAsString();
        String rfid2_cara = respuesta.get(NovusConstante.PARAMETRO_RFID_CARA).getAsString();
        String interfaz = "";
        String tipo = "";
        String com = "";

        if (!ibutton_puerto.equals("0") && !ibutton_puerto.isEmpty()) {
            d_atributos.addProperty("Traductor", ibutton_cara);

            tipo = "ibutton";
            try {
                com = ibutton_puerto.substring(0, ibutton_puerto.length() - 1);
            } catch (Exception ex) {
                System.out.println("Sin Datos");
            }

            if (com.equals("COM")) {
                interfaz = "serial";
            } else {
                interfaz = "tcp";
            }
            ingresarDispositivoUseCase = new IngresarDispositivoUseCase(tipo, ibutton_puerto, interfaz, Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo(tipo, ibutton_puerto, interfaz, Estado, d_atributos);
            DefaultTableModel defaultModel = new DefaultTableModel();
            defaultModel.addRow(new Object[]{"", ibutton_puerto, "",
                ibutton_cara, "", "", ""});

            dao.actualizaParametro(NovusConstante.PARAMETRO_IBUTTON_PUERTO, "0", TEXT, "");
            this.actualizarVista();
        }
        if (!rfid2_puerto.equals("0") && !rfid2_puerto.isEmpty()) {
            d_atributos.addProperty("Traductor", rfid2_cara);
            tipo = "rfidV2";
            interfaz = "serial";

            ingresarDispositivoUseCase = new IngresarDispositivoUseCase(tipo, rfid2_puerto, interfaz, Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo(tipo, rfid2_puerto, interfaz, Estado, d_atributos);
            DefaultTableModel defaultModel = new DefaultTableModel();
            defaultModel.addRow(new Object[]{"", rfid2_puerto, "",
                rfid2_cara, "", "", ""});
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V2, "0", TEXT, "Puerto COM para el RFIDv2, Ninguno dejar en 0 o vacio");
            this.actualizarVista();
        }
        if (!rfid.equals("0") && !rfid.isEmpty()) {
            d_atributos.addProperty("Traductor", "");
            tipo = "rfid";
            interfaz = "usb";

            ingresarDispositivoUseCase = new IngresarDispositivoUseCase(tipo, rfid, interfaz, Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo(tipo, rfid, interfaz, Estado, d_atributos);
            DefaultTableModel defaultModel = new DefaultTableModel();
            defaultModel.addRow(new Object[]{"", rfid, "",
                rfid, "", "", ""});

            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V1, "0", TEXT, "Puerto COM para el RFID versión USB, Ninguno dejar en 0 o vacio");
            this.actualizarVista();
        }
    }

    private void agregar() {

        String puerto = jIPuertoTCP.getText();
        if (jComboTipo.getSelectedIndex() < 0 || jComboInterFaz.getSelectedIndex() < 0) {
            showMessage("COMPLETE LOS CAMPOS", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            if (jComboTipo.getSelectedItem().equals("IBUTTON") && jComboInterFaz.getSelectedItem().equals("TCP")) {
                agregarIbuttonTCP();
            } else if (jComboTipo.getSelectedItem().equals("IBUTTON") && jComboInterFaz.getSelectedItem().equals("SERIAL")) {
                agregarIbuttonSERIAL();
            } else if (jComboTipo.getSelectedItem().equals("RFID") && jComboInterFaz.getSelectedItem().equals("SERIAL")) {
                agregarRfidSerial();
            } else if (jComboTipo.getSelectedItem().equals("RFID") && jComboInterFaz.getSelectedItem().equals("USB")) {
                agregarRfidUSB();
            }
        }
    }

    private void agregarIbuttonTCP() {

        String puerto = jIPuertoTCP.getText();
        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", "");
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (jComboTipo.getSelectedIndex() == -1 || puerto.equals("")) {
            showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (existe() == true) {
            showMessage("CONECTOR YA USADO", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);

        } else if (existePuerto() == true) {
            showMessage("VERIFIQUE EL PUERTO", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            ingresarDispositivoUseCase = new IngresarDispositivoUseCase(jComboTipo.getSelectedItem().toString().toLowerCase(), jIPuertoTCP.getText(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo(jComboTipo.getSelectedItem().toString().toLowerCase(), jIPuertoTCP.getText(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            dao.actualizaParametro(NovusConstante.PARAMENTO_IBUTTON_SERIAL, jRfid5.getText(), TEXT, "Traductor de puertos a cara del surtidor. Formato: 4=C5;2=C6");
            dao.actualizaParametro(NovusConstante.PARAMETRO_IBUTTON_PUERTO, "0", TEXT, "");
            posicion = jComboConector.getSelectedIndex();
            try {
                jCombo_CarasR.removeItemAt(jCombo_CarasR.getSelectedIndex());
            } catch (Exception e) {
                System.err.println(e);
            }
            limpiar();
            this.actualizarVista();
            CargarCaras();
        }
    }

    private void agregarIbuttonSERIAL() {

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", "");
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (jComboTipo.getSelectedIndex() < 0 || jComboConector.getSelectedIndex() < 0) {
            showMessage("DEBE INGRESAR TODOS LOS DATOS",
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (existe()) {
            showMessage("CONECTOR YA USADO", 
                    "/com/firefuel/resources/btBad.png",
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            ingresarDispositivoUseCase = new IngresarDispositivoUseCase(jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo(jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            dao.actualizaParametro(NovusConstante.PARAMENTO_IBUTTON_SERIAL, jRfid5.getText(), TEXT, "Traductor de puertos a cara del surtidor. Formato: 4=C5;2=C6");
            dao.actualizaParametro(NovusConstante.PARAMETRO_IBUTTON_PUERTO, "0", TEXT, "");
            posicion = jComboConector.getSelectedIndex();
            try {
                jCombo_CarasR.removeItemAt(jCombo_CarasR.getSelectedIndex());
            } catch (Exception e) {
                System.err.println(e);
            }
            limpiar();
            this.actualizarVista();
            CargarCaras();
        }
    }

    private void agregarRfidUSB() {

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", jComboAuto.getSelectedItem().toString());
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (jComboTipo.getSelectedIndex() == -1 || jComboConector.getSelectedIndex() == -1) {
            showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (existe() == true) {
            showMessage("CONECTOR YA USADO", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            ingresarDispositivoUseCase = new IngresarDispositivoUseCase(jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo(jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V1, "0", BOOLEAN,
                    "Puerto COM para el RFID, Ninguno dejar en 0 o vacio");
            posicion = jComboConector.getSelectedIndex();
            try {
                jCombo_CarasR.removeItemAt(jCombo_CarasR.getSelectedIndex());
            } catch (Exception e) {
                System.err.println(e);
            }
            limpiar();
            this.actualizarVista();
            CargarCaras();
        }
    }

    private void agregarRfidSerial() {

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", jComboAuto.getSelectedItem().toString());
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (jComboTipo.getSelectedIndex() == -1 || jComboConector.getSelectedIndex() == -1) {
            showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                    "/com/firefuel/resources/btBad.png",
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (existe() == true) {
            showMessage("CONECTOR YA USADO", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            ingresarDispositivoUseCase = new IngresarDispositivoUseCase("rfidV2", jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            ingresarDispositivoUseCase.execute();
            //dao.ingresarDispositivo("rfidV2", jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V2, "0", TEXT,
                    "Puerto COM para el RFIDv2, Ninguno dejar en 0 o vacio");
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_CARA, jRfid5.getText(), TEXT,
                    "Traductor RFID/CARA. Formato esperado: 1=C5;2=C6;3=C7;4=C8");
            posicion = jComboConector.getSelectedIndex();
            jCombo_CarasR.removeAllItems();
            try {
                jCombo_CarasR.removeItemAt(jCombo_CarasR.getSelectedIndex());
            } catch (Exception e) {
                System.err.println(e);
            }
            limpiar();
            this.actualizarVista();
            CargarCaras();
        }

    }

    private void editar() {

        int fila = jTable1.getSelectedRow();
        System.out.println(jTable1.getValueAt(fila, 3));
        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", jComboAuto.getSelectedItem().toString());
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (fila >= 0) {
            if (jComboTipo.getSelectedItem().equals("IBUTTON") && jComboInterFaz.getSelectedItem().equals("TCP")) {
                editarItcp();
            } //Condicion si El dispositivo es un Ibutton y la Interfaz es Serial
            else if (jComboTipo.getSelectedItem().equals("IBUTTON") && jComboInterFaz.getSelectedItem().equals("SERIAL")) {
                editarIserial();
            } //Condicion si El dispositivo es un Rfid2
            else if (jComboTipo.getSelectedItem().equals("RFID") && jComboInterFaz.getSelectedItem().equals("SERIAL")) {
                editarRfid2();
                //Condicion si el dispositivo es un Rfid
            } else if (jComboTipo.getSelectedItem().equals("RFID")) {
                editarRfid();
            }
        } else {
            showMessage("DEBE SELECCIONAR UNA FILA", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void editarRfid() {
        int fila = jTable1.getSelectedRow();

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", jComboAuto.getSelectedItem().toString());
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (fila == -1) {
            showMessage("DEBE SELECCIONAR UNA FILA", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (jComboTipo.getSelectedIndex() == -1 && jComboInterFaz.getSelectedIndex() == -1 && jComboConector.getSelectedIndex() == -1) {
            showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);

        } else {

            DispositivoDto dispositivoDto = new DispositivoDto(Integer.parseInt(txt_id.getText()), jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            useCase.execute(dispositivoDto);

             

           /* dao.editarDispositivo(Integer.parseInt(txt_id.getText()), jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);  */
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V1, jComboConector.getSelectedItem().toString(), TEXT, "PuerTo COM para el RFID versión USB, Ninguno dejar en 0 o vacio");
            this.limpiar();
            this.actualizarVista();
            jComboConector.setEnabled(true);
            jComboConector.setEnabled(true);
            jComboTipo.setEnabled(true);
            jComboInterFaz.setEnabled(true);
        }
    }

    private void editarItcp() {

        int fila = jTable1.getSelectedRow();

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", "");
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (fila >= 0) {
            if (jComboTipo.getSelectedIndex() != -1 && jComboInterFaz.getSelectedIndex() != -1 && !jIPuertoTCP.getText().equals("")) {
                DispositivoDto dispositivoDto = new DispositivoDto(Integer.parseInt(txt_id.getText()), jComboTipo.getSelectedItem().toString().toLowerCase(), jIPuertoTCP.getText(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
                useCase.execute(dispositivoDto);

                /* dao.editarDispositivo(Integer.parseInt(txt_id.getText()), jComboTipo.getSelectedItem().toString().toLowerCase(), jIPuertoTCP.getText(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);*/
                dao.actualizaParametro(NovusConstante.PARAMENTO_IBUTTON_SERIAL, jRfid5.getText(), TEXT, "Traductor de puertos a cara del surtidor. Formato: 4=C5;2=C6");
                dao.actualizaParametro(NovusConstante.PARAMETRO_IBUTTON_PUERTO, "0", TEXT, "");
                this.limpiar();
                this.actualizarVista();
                jComboConector.setEnabled(true);
                jComboConector.setEnabled(true);
                jComboTipo.setEnabled(true);
                jComboInterFaz.setEnabled(true);
            } else {
                showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                        "/com/firefuel/resources/btBad.png", 
                        true, this::cambiarPanelHome, 
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("DEBE SELECCIONAR UNA FILA",
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void editarIserial() {
        int fila = jTable1.getSelectedRow();

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", "");
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (fila >= 0) {
            if (jComboTipo.getSelectedIndex() != -1 && jComboInterFaz.getSelectedIndex() != -1 && jComboConector.getSelectedIndex() != -1) {
                dao.actualizaParametro(NovusConstante.PARAMENTO_IBUTTON_SERIAL, jRfid5.getText(), TEXT, "Traductor de puertos a cara del surtidor. Formato: 4=C5;2=C6");
                dao.actualizaParametro(NovusConstante.PARAMETRO_IBUTTON_PUERTO, "0", TEXT, "");
                
                DispositivoDto dispositivoDto = new DispositivoDto(Integer.parseInt(txt_id.getText()), jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
                useCase.execute(dispositivoDto);
                
                /* dao.editarDispositivo(Integer.parseInt(txt_id.getText()), jComboTipo.getSelectedItem().toString().toLowerCase(), jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);*/
                this.limpiar();
                this.actualizarVista();
                jComboConector.setEnabled(true);
                jComboConector.setEnabled(true);
                jComboTipo.setEnabled(true);
                jComboInterFaz.setEnabled(true);

            } else {
                showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                        "/com/firefuel/resources/btBad.png", 
                        true, this::cambiarPanelHome, 
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("DEBE SELECCIONAR UNA FILA", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void editarRfid2() {
        int fila = jTable1.getSelectedRow();

        JsonObject d_atributos = new JsonObject();
        d_atributos.addProperty("TipoAutorizacion", jComboAuto.getSelectedItem().toString());
        d_atributos.addProperty("Traductor", jRfid5.getText());

        if (fila == -1) {
            showMessage("DEBE SELECCIONAR UNA FILA",
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else if (jComboTipo.getSelectedIndex() == -1 && jComboInterFaz.getSelectedIndex() == -1 && jComboConector.getSelectedIndex() == -1) {
            showMessage("DEBE INGRESAR TODOS LOS DATOS", 
                    "/com/firefuel/resources/btBad.png", 
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
           

            DispositivoDto dispositivoDto = new DispositivoDto(Integer.parseInt(txt_id.getText()), "rfidV2", jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);
            useCase.execute(dispositivoDto);
            /* dao.editarDispositivo(Integer.parseInt(txt_id.getText()), "rfidV2", jComboConector.getSelectedItem().toString(), jComboInterFaz.getSelectedItem().toString().toLowerCase(), Estado, d_atributos);*/
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V2, "0", TEXT,
                    "Puerto COM para el RFIDv2, Ninguno dejar en 0 o vacio");
            dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_CARA, jRfid5.getText(), TEXT,
                    "Traductor RFID/CARA. Formato esperado: 1=C5;2=C6;3=C7;4=C8");
            this.limpiar();
            this.actualizarVista();
            jComboConector.setEnabled(true);
            jComboConector.setEnabled(true);
            jComboTipo.setEnabled(true);
            jComboInterFaz.setEnabled(true);
        }
    }

    private void eliminar() {

        int fila = jTable1.getSelectedRow();

        if (fila == -1) {
            showMessage("DEBE SELECCIONAR UNA FILA",
                    "/com/firefuel/resources/btBad.png",
                    true, this::cambiarPanelHome,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {

            if (jComboInterFaz.getSelectedItem().equals("SERIAL") && jComboTipo.getSelectedItem().equals("RFID2")) {
                eliminarRfid2();
                jComboConector.setEnabled(true);
                jComboConector.setEnabled(true);
                jComboTipo.setEnabled(true);
                jComboInterFaz.setEnabled(true);
            } else if ((jComboInterFaz.getSelectedItem().equals("SERIAL") && jComboTipo.getSelectedItem().equals("IBUTTON"))
                    || (jComboInterFaz.getSelectedItem().equals("TCP") && jComboTipo.getSelectedItem().equals("IBUTTON"))) {

                eliminarIbutton();
                jComboConector.setEnabled(true);
                jComboConector.setEnabled(true);
                jComboTipo.setEnabled(true);
                jComboInterFaz.setEnabled(true);

            } else if ((jComboInterFaz.getSelectedItem().equals("SERIAL") && jComboTipo.getSelectedItem().equals("RFID"))
                    || (jComboInterFaz.getSelectedItem().equals("USB") && jComboTipo.getSelectedItem().equals("RFID"))) {
                eliminarRfid();
                jComboConector.setEnabled(true);
                jComboConector.setEnabled(true);
                jComboTipo.setEnabled(true);
                jComboInterFaz.setEnabled(true);
            } else {
                EliminarDispositivoUseCase eliminarDispositivoUseCase = new EliminarDispositivoUseCase(Integer.parseInt(txt_id.getText().trim()));
                eliminarDispositivoUseCase.execute();
                //dao.eliminarDispositivo(Integer.parseInt(txt_id.getText().trim()));
                this.limpiar();
                this.actualizarVista();
            }
        }
    }

    private void eliminarRfid() {
        String dato = "0";
        EliminarDispositivoUseCase eliminarDispositivoUseCase = new EliminarDispositivoUseCase(Integer.parseInt(txt_id.getText().trim()));
        eliminarDispositivoUseCase.execute();
        //dao.eliminarDispositivo(Integer.parseInt(txt_id.getText().trim()));
        dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V1, dato, BOOLEAN,
                "Puerto COM para el RFID, Ninguno dejar en 0 o vacio");
        this.limpiar();
        this.actualizarVista();
    }

    private void eliminarIbutton() {
        String dato = "0";
        jCombo_CarasR.removeAllItems();
        EliminarDispositivoUseCase eliminarDispositivoUseCase = new EliminarDispositivoUseCase(Integer.parseInt(txt_id.getText().trim()));
        eliminarDispositivoUseCase.execute();
        //dao.eliminarDispositivo(Integer.parseInt(txt_id.getText().trim()));
        dao.actualizaParametro(NovusConstante.PARAMENTO_IBUTTON_SERIAL, dato, TEXT, "Traductor de puertos a cara del surtidor. Formato: 4=C5;2=C6");
        dao.actualizaParametro(NovusConstante.PARAMETRO_IBUTTON_PUERTO, dato, TEXT, "");
        this.limpiar();
        this.actualizarVista();
        CargarCaras();
    }

    private void eliminarRfid2() {

        String dato = "0";
        EliminarDispositivoUseCase eliminarDispositivoUseCase = new EliminarDispositivoUseCase(Integer.parseInt(txt_id.getText().trim()));
        eliminarDispositivoUseCase.execute();
        //dao.eliminarDispositivo(Integer.parseInt(txt_id.getText().trim()));
        dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_V2, dato, TEXT,
                "Puerto COM para el RFIDv2, Ninguno dejar en 0 o vacio");
        dao.actualizaParametro(NovusConstante.PARAMETRO_RFID_CARA, dato, TEXT,
                "Traductor RFID/CARA. Formato esperado: 1=C5;2=C6;3=C7;4=C8");
        this.limpiar();
        this.actualizarVista();
        jComboConector.setEnabled(true);
        jComboConector.setEnabled(false);
        jComboTipo.setEnabled(true);
        jComboInterFaz.setEnabled(true);
    }

    private void limpiar() {
        jLagregar.setVisible(true);
        txt_id.setText("");
        jComboTipo.setSelectedIndex(-1);
        jComboConector.setSelectedIndex(-1);
        jComboInterFaz.setSelectedIndex(-1);
        jIPuertoTCP.setText("");
        listaR.clear();
        jRfid5.setText("");
        jComboConector.setEnabled(true);
        jComboConector.setEnabled(true);
        jComboTipo.setEnabled(true);
        jComboInterFaz.setEnabled(true);
    }

    private boolean validacion() {
        boolean used = false;

        int fila = jTable1.getSelectedRow();
        String traduccion = (String) jTable1.getValueAt(fila, 6).toString();
        String traductosW = jRfid5.getText();
        String traductor_bd = traduccion;

        String[] traductorWA = traductosW.split(";");
        String[] traductor_bdA = traductor_bd.split(";");

        if (traductorWA.length > 0 && traductor_bdA.length > 0) {
            used = false;
        } else {
            for (int i = 0; i < traductorWA.length; i++) {
                for (int j = 0; j < traductor_bdA.length; j++) {
                    if (traductorWA[i].equals(traductor_bdA[j])) {
                        used = true;
                        break;
                    }
                }
            }
        }
        return used;
    }

    boolean existeCOM(String conector) {
        return new IsValidComUseCase(conector).execute();
    }

    private void guardar() {

        String autorizacion;
        int opcion = jComboAuto.getSelectedIndex();
        switch (opcion) {
            case 0:
                autorizacion = "1";
                break;
            case 1:
                autorizacion = "2";
                break;
            case 2:
                autorizacion = "3";
                break;
            default:
                throw new AssertionError();
        }

        dao.actualizaParametro(NovusConstante.PREFERENCE_PLACA_IMPRESION, jventasCheck.isSelected() ? "S" : "N", STRING, "Requerir que sea obligarorio escribir placa para imprimir");
        dao.actualizaParametro(NovusConstante.PREFERENCE_MEDIDAS_TANQUES, jtanquesCheck.isSelected() ? "S" : "N", STRING, "Solicitar lecturas de tanques cuando inician jornada");
        dao.actualizaParametro(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES, jimprimirsobres.isSelected() ? "S" : "N", BOOLEAN, "IMPRIMIR CONSIGNACIONES");
        dao.actualizaParametro(NovusConstante.PARAMETRO_TIPO_AUTO, autorizacion, NUMBER, "METODO_AUTORIZACION_SIN_TAG = 1, METODO_AUTORIZACION_TAG_GLOBAL = 2, METODO_AUTORIZACION_TAG_CARA = 3");

        JsonArray parametrizacionesArray = new JsonArray();
        JsonObject response = null;
        for (Map.Entry<String, String> param : parametrizaciones.entrySet()) {
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("codigo", param.getKey());
            jsonParam.addProperty("valor", param.getValue());
            jsonParam.addProperty("tipo", 2);
            parametrizacionesArray.add(jsonParam);
        }
        ClientWSAsync ws = new ClientWSAsync("GUARDAR PARAMETROS", NovusConstante.SECURE_CENTRAL_POINT_CREAR_PARAMETROS,
                NovusConstante.POST,
                parametrizacionesArray.toString(),
                true);
        try {
            response = ws.esperaRespuesta();
            NovusUtils.printLn(response.toString());
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }

        if (response != null) {
            SetupAsync async = new SetupAsync(NovusConstante.DESCARGAR_MEDIOS_ID);
            showMessage("PARAMETROS MODIFICADOS CORRECTAMENTE",
                    "/com/firefuel/resources/btOk.png",
                    true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
            async.start();
        } else {
            showMessage("HA OCURRIDO UN ERROR AL ACTUALIZAR PARAMETROS",
                    "/com/firefuel/resources/btBad.png",
                    true, this::cambiarPanelHome,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void cargarParametrizaciones() {

        Main.cargarParametros();

        parametrizaciones.put(NovusConstante.PREFERENCE_MEDIDAS_TANQUES, Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_MEDIDAS_TANQUES, false) ? "S" : "N");
        parametrizaciones.put(NovusConstante.PREFERENCE_PLACA_IMPRESION, Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_PLACA_IMPRESION, false) ? "S" : "N");
        parametrizaciones.put(NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA, Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA, false) ? "S" : "N");
        parametrizaciones.put(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES, Main.getParametroCoreBoolean(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES, false) ? "S" : "N");

        try {
            jimpresionautoCheck.setSelected(parametrizaciones.get(NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA).equals("S"));
        } catch (Exception e) {
            jimpresionautoCheck.setSelected(false);
        }

        //Solicitar Impresion Sobres
        String parametro = dao.getParametroCore(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES);
        if (parametro != null) {
            jimprimirsobres.setSelected(parametro.equals("S"));
        } else {
            dao.actualizaParametro(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES, "N", BOOLEAN, "Imprimir consignaciones de sobres");
            jimprimirsobres.setSelected(false);
        }
        //Solicitar Lectura Tanques
        String isTanques = dao.getParametroCore(NovusConstante.PREFERENCE_MEDIDAS_TANQUES);
        if (isTanques.equals("S")) {
            jtanquesCheck.setSelected(true);
        } else {
            jtanquesCheck.setSelected(false);
        }

        String cantidaDigitos = dao.getParametroCore(NovusConstante.PARAMETRO_DIGITOS_SURTIDOR);
        txt_cntS.setText(cantidaDigitos);

        //Solicitar Placa Impresion
        String isPlaca = dao.getParametroCore(NovusConstante.PREFERENCE_PLACA_IMPRESION);
        if (isPlaca.equals("S")) {
            jventasCheck.setSelected(true);
        } else {
            jventasCheck.setSelected(false);
        }

        //Tipo de Autorizacion
        String jornada = dao.getParametroCore(NovusConstante.PARAMETRO_TIPO_AUTO);
        switch (jornada) {
            case "1":
                jComboAuto.setSelectedIndex(0);
                break;
            case "2":
                jComboAuto.setSelectedIndex(1);
                break;
            case "3":
                jComboAuto.setSelectedIndex(2);
                break;
            default:
                throw new AssertionError();
        }
    }

    private void cambiarEstado(String parametro) {
        switch (parametro) {
            case NovusConstante.PREFERENCE_MEDIDAS_TANQUES:
                parametrizaciones.put(NovusConstante.PREFERENCE_MEDIDAS_TANQUES, (jtanquesCheck.isSelected() ? "S" : "N"));
                break;
            case NovusConstante.PREFERENCE_PLACA_IMPRESION:
                parametrizaciones.put(NovusConstante.PREFERENCE_PLACA_IMPRESION, (jventasCheck.isSelected() ? "S" : "N"));
                break;
            case NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA:
                parametrizaciones.put(NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA, (jimpresionautoCheck.isSelected() ? "S" : "N"));
                break;
            case NovusConstante.PARAMETRO_IMPRIMIR_SOBRES:
                parametrizaciones.put(NovusConstante.PARAMETRO_IMPRIMIR_SOBRES, (jimprimirsobres.isSelected() ? "S" : "N"));
                break;
        }
    }

    private void Async(Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    void SolicitarInfo() {
        Dispositivos.clear();
        EquipoDao edao = new EquipoDao();

        try {
            Dispositivos = new GetDispositivosInfoUseCase().execute();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }

    }

    private void mostrarTeclado() {
        jPanel5.setVisible(true);
    }

    private void desactivarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel5;
        teclado.habilitarAlfanumeric(false);
        teclado.habilitarPunto(false);
    }

    private void activarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel5;
        teclado.habilitarAlfanumeric(true);
    }

    private void limpiarR() {
        jCombo_CarasR.removeAllItems();
        listaR.clear();
        jRfid5.setText("");
        CargarCaras();
    }

    //Modifica el Jcombo Interfaz dependiendo del Tipo de Dispositivo
    private void stateComboTipoDispositivo() {

        String jornada = dao.getParametroCore(NovusConstante.PARAMETRO_TIPO_AUTO);
        String[] arr = new String[]{};
        jComboInterFaz.setModel(new javax.swing.DefaultComboBoxModel<>());
        //ibutton
        if (jComboTipo.getSelectedIndex() == 0) {

            arr = new String[]{"", "SERIAL", "TCP"};

            jComboPR1.setEnabled(true);
            jCombo_CarasR.setEnabled(true);
            btn_guardarR1.setVisible(true);
            jRfid5.setEnabled(true);

        } else if (jComboTipo.getSelectedIndex() == 1 && (jornada.equals("1") || jornada.equals("2"))) {

            arr = new String[]{"", "SERIAL", "USB"};
            jComboPR1.setEnabled(false);
            jCombo_CarasR.setEnabled(false);
            btn_guardarR1.setVisible(false);
            jRfid5.setEnabled(false);

        } else if (jComboTipo.getSelectedIndex() == 1) {
            //rfid
            arr = new String[]{"", "SERIAL", "USB"};

        }

        jComboInterFaz.setModel(new javax.swing.DefaultComboBoxModel<>(arr));
    }

    private void showMessage(String msj, String ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar).setRunnable(runnable)
                .setAutoclose(autoclose).setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnl_container.getLayout();
        pnl_container.add("pnl_ext", panel);
        layout.show(pnl_container, "pnl_ext");
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    }

}

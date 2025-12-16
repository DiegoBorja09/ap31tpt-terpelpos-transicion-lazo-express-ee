/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.sutidores.ActualizarNovedadSaltoLecturaUseCase;
import com.application.useCases.sutidores.ValidarCorreccionSaltoLecturaUseCase;
import com.bean.PersonaBean;
import com.bean.SaltosBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.facade.PersonaFacade;
import com.facade.SurtidorFacade;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import teclado.view.common.TecladoExtendido;

/**
 *
 * @author Devitech
 */
public class MenuSaltosLecturas extends javax.swing.JPanel {

    public static final String CORRECCION_SALTOS = "correccion_saltos";
    public static final String MENU = "menu";
    public static final String SALTOS_LECTURAS = "saltos_lecturas";
    public static final String HISTORIAL_SALTOS = "historial_saltos";

    public static final int COMBO_OPTION_FALLA_TECNICA_ID = 1;
    public static final int COMBO_OPTION_VENTA_FUERA_SISTEMA_ID = 2;
    public static final int COMBO_OPTION_AJUSTE_INICIAL_ID = 3;
    public static final int COMBO_OPTION_MANTENIMIENTO_ID = 4;
    public static final String RUTA_IMAGEN = "/com/firefuel/resources/";
    double remainderVolume = 0;
    private Runnable runnable;
    JsonObject detailHose = null;
    TreeMap<Long, JsonObject> misfitHoses = null;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    TreeMap<Long, Float> cache = new TreeMap<>();
    TreeMap<Integer, JsonObject> COMBO_REASON_OPTIONS = null;
    ArrayList<JsonObject> assignations = new ArrayList<JsonObject>();
    TreeMap<Integer, PersonaBean> allPersons = null;
    ArrayList<SaltosBean> Saltos = new ArrayList<>();
    private ValidarCorreccionSaltoLecturaUseCase validarCorreccionSaltoLecturaUseCase;
    private ActualizarNovedadSaltoLecturaUseCase actualizarNovedadSaltoLecturaUseCase;

    public MenuSaltosLecturas() {
        initComponents();
        NovusUtils.setTablePrimaryStyle(this.jTable1);
        NovusUtils.setTablePrimaryStyle(this.table_assignations);
    }

    public MenuSaltosLecturas(Runnable runnable) {
        this.runnable = runnable;
        initComponents();
        NovusUtils.setTablePrimaryStyle(this.jTable1);
    }

    private void addPanel() {
        panel_principal.add(saltos_lecturas, SALTOS_LECTURAS);
        panel_principal.add(correccion_saltos, CORRECCION_SALTOS);
        panel_principal.add(historial_saltos, HISTORIAL_SALTOS);
        panel_principal.add(menu, MENU);
    }

    void loadData() {
        try {
            this.misfitHoses = this.fetchMisfitHoses();
            this.renderMisfitHoses(this.misfitHoses);
        } catch (DAOException ex) {
            NovusUtils.setMensaje("No se pudo obtener información debido a un error", jNotificacion);
            setTimeout(2, () -> {
                NovusUtils.setMensaje("", jNotificacion);
            });

        }
    }

    TreeMap<Long, JsonObject> fetchMisfitHoses() throws DAOException {
        return SurtidorFacade.fetchMisfitHoses();
    }

    void renderMisfitHoses(TreeMap<Long, JsonObject> misfitHoses) {

        DefaultTableModel dm = (DefaultTableModel) this.jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        if (misfitHoses != null) {
            this.jTable1.setAutoCreateRowSorter(true);
            DefaultTableModel defaultModel = (DefaultTableModel) this.jTable1.getModel();
            for (Map.Entry<Long, JsonObject> entry : misfitHoses.entrySet()) {
                JsonObject detailObject = entry.getValue();
                long pump = detailObject.get("surtidor").getAsLong();
                long face = detailObject.get("cara").getAsLong();
                long hose = detailObject.get("manguera").getAsLong();
                String product = detailObject.get("producto").getAsString();
                float diffAmount = detailObject.get("diff_cantidad").getAsFloat();
                try {
                    defaultModel.addRow(new Object[]{
                        pump,
                        face,
                        hose,
                        product.trim().toLowerCase(),
                        diffAmount
                    });
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
            }
        }
    }

    private void renderizarCambiosHistorial() {
        lbstatus.setText("");
        this.SolicitarCambios();
        jTable2.setAutoCreateRowSorter(true);
        DefaultTableModel dm = (DefaultTableModel) jTable2.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable2.getModel();
        for (SaltosBean saltos : this.Saltos) {

            String prueba = "";

            switch (saltos.getMotivo()) {
                case 1:
                    prueba = "Falla Tecnica";
                    break;
                case 2:
                    prueba = "Fuera Sistema";
                    break;
            }

            try {
                defaultModel.addRow(new Object[]{saltos.getId(), sdf.format(saltos.getFecha()),
                    /*producto.getMotivo()*/ prueba, saltos.getCara(), saltos.getManguera(), saltos.getDescripcion(),
                    saltos.getPersona(), saltos.getSistema_acu_v(), saltos.getSurtidor_acu_v()
                /* "$" + df.format(producto.getPrecio() + impuestoProducto) */
                });

            } catch (Exception e) {
                NovusUtils.printLn("Error");
                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        jTable2.setRowHeight(35);
        jTable2.setModel(defaultModel);
        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

    }

    void SolicitarCambios() {
        Saltos.clear();
        MovimientosDao mdao = new MovimientosDao();
        try {
            int limite = Integer.parseInt(jComboMostrar.getSelectedItem().toString().trim());
            Saltos = mdao.historialSaltos(limite);
        } catch (DAOException | SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }

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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.validarCorreccionSaltoLecturaUseCase = new ValidarCorreccionSaltoLecturaUseCase();
        this.actualizarNovedadSaltoLecturaUseCase = new ActualizarNovedadSaltoLecturaUseCase();

        panel_principal = new javax.swing.JPanel();
        menu = new javax.swing.JPanel();
        btn_atras = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabelCorregirSaltos = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabelHistorico = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        saltos_lecturas = new javax.swing.JPanel();
        jNotificacion = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        correccion_saltos = new javax.swing.JPanel();
        pnl_keyboard = new TecladoExtendido();
        combo_razones = new javax.swing.JComboBox<>();
        btn_save = new javax.swing.JLabel();
        pnl_sale_data = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_volumen = new javax.swing.JTextField();
        combo_person = new javax.swing.JComboBox<>();
        btn_assign = new javax.swing.JLabel();
        container_asignations = new javax.swing.JScrollPane();
        table_assignations = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        lbl_product = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbl_hose = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbl_difference = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lbl_remainder = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jNotificacion1 = new javax.swing.JLabel();
        historial_saltos = new javax.swing.JPanel();
        jpromotor3 = new javax.swing.JLabel();
        jComboMostrar = new javax.swing.JComboBox<>();
        lbstatus = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        panel_principal.setLayout(new java.awt.CardLayout());

        menu.setMaximumSize(new java.awt.Dimension(1280, 800));
        menu.setMinimumSize(new java.awt.Dimension(1280, 800));
        menu.setLayout(null);

        btn_atras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_atras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        btn_atras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_atrasMouseReleased(evt);
            }
        });
        menu.add(btn_atras);
        btn_atras.setBounds(20, 10, 80, 70);

        jLabel28.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(186, 12, 47));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("1");
        menu.add(jLabel28);
        jLabel28.setBounds(30, 120, 80, 50);

        jLabel27.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("CORREGIR");
        menu.add(jLabel27);
        jLabel27.setBounds(120, 110, 280, 70);

        jLabelCorregirSaltos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabelCorregirSaltos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelCorregirSaltosMouseClicked(evt);
            }
        });
        menu.add(jLabelCorregirSaltos);
        jLabelCorregirSaltos.setBounds(30, 110, 390, 70);

        jLabel23.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("HISTORICO");
        menu.add(jLabel23);
        jLabel23.setBounds(120, 200, 280, 70);

        jLabel22.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("2");
        menu.add(jLabel22);
        jLabel22.setBounds(40, 210, 70, 45);

        jLabelHistorico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabelHistorico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelHistoricoMouseReleased(evt);
            }
        });
        menu.add(jLabelHistorico);
        jLabelHistorico.setBounds(30, 200, 390, 70);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_surtidor.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menu.add(jLabel13);
        jLabel13.setBounds(710, 200, 360, 410);

        jTitle.setFont(new java.awt.Font("Conthrax", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTitle.setText("SALTOS LECTURA");
        menu.add(jTitle);
        jTitle.setBounds(110, 15, 420, 60);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        menu.add(jLabel33);
        jLabel33.setBounds(1180, 3, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        menu.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        menu.add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        menu.add(jLabel36);
        jLabel36.setBounds(10, 710, 100, 80);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        menu.add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);
        menu.add(jLabel3);
        jLabel3.setBounds(27, 16, 60, 60);

        panel_principal.add(menu, "menu");

        saltos_lecturas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jNotificacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saltos_lecturas.add(jNotificacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 720, 990, 70));

        jLabel18.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel18.setText("ACTUALIZAR");
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel18MouseReleased(evt);
            }
        });
        saltos_lecturas.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 620, 180, 60));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        saltos_lecturas.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel17.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jLabel17.setText("SELECCIONAR");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel17MouseReleased(evt);
            }
        });
        saltos_lecturas.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 620, 180, 60));

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("SALTOS LECTURA");
        saltos_lecturas.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 720, 90));

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SURTIDOR", "CARA", "MANGUERA", "PRODUCTO", "DIF. CANTIDAD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(35);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        saltos_lecturas.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 1110, 490));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        saltos_lecturas.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 710, 10, 80));

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        saltos_lecturas.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 10, 68));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        saltos_lecturas.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panel_principal.add(saltos_lecturas, "saltos_lecturas");

        correccion_saltos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnl_keyboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnl_keyboardMouseReleased(evt);
            }
        });
        correccion_saltos.add(pnl_keyboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(131, 461, 1024, 336));

        combo_razones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FALLA TECNICA", "VENTA FUERA DEL SISTEMA", "AJUSTE INICIAL", "MANTENIMIENTO" }));
        combo_razones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_razonesActionPerformed(evt);
            }
        });
        correccion_saltos.add(combo_razones, new org.netbeans.lib.awtextra.AbsoluteConstraints(791, 53, 360, 30));

        btn_save.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_save.setForeground(new java.awt.Color(255, 255, 255));
        btn_save.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_save.setText("GUARDAR");
        btn_save.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_saveMousePressed(evt);
            }
        });
        correccion_saltos.add(btn_save, new org.netbeans.lib.awtextra.AbsoluteConstraints(421, 181, 180, 60));

        pnl_sale_data.setOpaque(false);
        pnl_sale_data.setLayout(null);

        jLabel8.setText("PROMOTOR:");
        pnl_sale_data.add(jLabel8);
        jLabel8.setBounds(10, 10, 100, 30);

        jLabel6.setText("VOLUMEN:");
        pnl_sale_data.add(jLabel6);
        jLabel6.setBounds(10, 60, 100, 30);

        txt_volumen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_volumenFocusGained(evt);
            }
        });
        txt_volumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_volumenKeyTyped(evt);
            }
        });
        pnl_sale_data.add(txt_volumen);
        txt_volumen.setBounds(140, 55, 360, 30);

        combo_person.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_person.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_personActionPerformed(evt);
            }
        });
        pnl_sale_data.add(combo_person);
        combo_person.setBounds(140, 10, 360, 30);

        btn_assign.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_assign.setForeground(new java.awt.Color(255, 255, 255));
        btn_assign.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_assign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btn_assign.setText("ASIGNAR");
        btn_assign.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_assign.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_assignMousePressed(evt);
            }
        });
        pnl_sale_data.add(btn_assign);
        btn_assign.setBounds(180, 90, 180, 60);

        correccion_saltos.add(pnl_sale_data, new org.netbeans.lib.awtextra.AbsoluteConstraints(651, 91, 530, 150));

        container_asignations.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        table_assignations.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        table_assignations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RESPONSABLE", "VOLUMEN", "TOTAL", "AC VOL INI", "AC VOL FIN", "AC VEN INI", "AC VEN FIN"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_assignations.setRowHeight(35);
        table_assignations.setRowSelectionAllowed(false);
        table_assignations.getTableHeader().setReorderingAllowed(false);
        table_assignations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_assignationsMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                table_assignationsMouseReleased(evt);
            }
        });
        container_asignations.setViewportView(table_assignations);

        correccion_saltos.add(container_asignations, new org.netbeans.lib.awtextra.AbsoluteConstraints(81, 251, 1120, 200));

        jLabel9.setText("PRODUCTO:");
        correccion_saltos.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 51, 90, 40));

        lbl_product.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_product.setText("DIESEL");
        correccion_saltos.add(lbl_product, new org.netbeans.lib.awtextra.AbsoluteConstraints(281, 51, 120, 30));

        jLabel10.setText("MANGUERA:");
        correccion_saltos.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 101, 100, 30));

        lbl_hose.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_hose.setText("1");
        correccion_saltos.add(lbl_hose, new org.netbeans.lib.awtextra.AbsoluteConstraints(251, 91, 150, 50));

        jLabel11.setText("DIFERENCIA:");
        correccion_saltos.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 141, 90, 40));

        lbl_difference.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_difference.setText("2387.56GL");
        correccion_saltos.add(lbl_difference, new org.netbeans.lib.awtextra.AbsoluteConstraints(281, 141, 120, 40));

        jLabel14.setText("RESTANTE:");
        correccion_saltos.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 191, 100, 30));

        lbl_remainder.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_remainder.setText("2387.56GL");
        correccion_saltos.add(lbl_remainder, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 191, 130, 30));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/BtCerrar.png"))); // NOI18N
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel15MouseReleased(evt);
            }
        });
        correccion_saltos.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(1211, 11, 50, 50));

        jLabel16.setText("MOTIVO:");
        correccion_saltos.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(661, 51, 80, 30));

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_fondo_correccion.png"))); // NOI18N
        correccion_saltos.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        jNotificacion1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion1.setForeground(new java.awt.Color(255, 255, 255));
        correccion_saltos.add(jNotificacion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(731, 11, 530, 70));

        panel_principal.add(correccion_saltos, "correccion_saltos");

        historial_saltos.setLayout(null);

        jpromotor3.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor3.setForeground(new java.awt.Color(153, 0, 0));
        jpromotor3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor3.setText("MOSTRAR");
        historial_saltos.add(jpromotor3);
        jpromotor3.setBounds(810, 640, 130, 40);

        jComboMostrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboMostrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20", "60", "120", "200" }));
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
        historial_saltos.add(jComboMostrar);
        jComboMostrar.setBounds(940, 640, 110, 40);

        lbstatus.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lbstatus.setForeground(new java.awt.Color(255, 255, 255));
        lbstatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbstatus.setText("CARGANDO HISTORIAL ...");
        historial_saltos.add(lbstatus);
        lbstatus.setBounds(470, 730, 650, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        historial_saltos.add(jLabel30);
        jLabel30.setBounds(10, 710, 100, 80);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        historial_saltos.add(jLabel32);
        jLabel32.setBounds(120, 710, 10, 80);

        jLabel20.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel20.setText("ACTUALIZAR");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel20MousePressed(evt);
            }
        });
        historial_saltos.add(jLabel20);
        jLabel20.setBounds(1070, 630, 190, 60);

        jLabel21.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("HISTORIAL SALTOS LECTURA");
        historial_saltos.add(jLabel21);
        jLabel21.setBounds(100, 0, 720, 90);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel25MouseReleased(evt);
            }
        });
        historial_saltos.add(jLabel25);
        jLabel25.setBounds(10, 10, 70, 71);

        jTable2 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "FECHA", "MOTIVO", " CARA", "MANGUERA", "PRODUCTO", "RESPONSABLE", "SISTEMA VOLUMEN", "SURTIDOR VOLUMEN"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        historial_saltos.add(jScrollPane2);
        jScrollPane2.setBounds(40, 100, 1210, 520);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        historial_saltos.add(jLabel37);
        jLabel37.setBounds(80, 10, 10, 68);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        historial_saltos.add(jLabel38);
        jLabel38.setBounds(1130, 710, 10, 80);

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        historial_saltos.add(jLabel39);
        jLabel39.setBounds(0, 0, 1281, 801);

        panel_principal.add(historial_saltos, "historial_saltos");

        add(panel_principal, "card5");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_atrasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_atrasMouseReleased
        NovusUtils.beep();
        cerrar();
    }//GEN-LAST:event_btn_atrasMouseReleased

    private void jLabelHistoricoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHistoricoMouseReleased
        renderizarCambiosHistorial();
        NovusUtils.setTablePrimaryStyle(this.jTable2);
        addPanel();
        mostrarPanel(HISTORIAL_SALTOS);
    }//GEN-LAST:event_jLabelHistoricoMouseReleased

    private void jLabel18MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseReleased
        NovusUtils.beep();
        loadData();
    }//GEN-LAST:event_jLabel18MouseReleased

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseReleased
        NovusUtils.beep();
        addPanel();
        mostrarPanel(MENU);
    }//GEN-LAST:event_jLabel4MouseReleased

    private void jLabel17MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseReleased
        seleccionaSaltoLectura();
    }//GEN-LAST:event_jLabel17MouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        selectRow();
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseReleased

    private void pnl_keyboardMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_keyboardMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pnl_keyboardMouseReleased

    private void combo_razonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_razonesActionPerformed
        handleComboReasonChange();
    }//GEN-LAST:event_combo_razonesActionPerformed

    private void btn_saveMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_saveMousePressed
        NovusUtils.beep();
        handleSaveButton();
    }//GEN-LAST:event_btn_saveMousePressed

    private void txt_volumenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_volumenFocusGained
        NovusUtils.deshabilitarCopiarPegar(txt_volumen);
    }//GEN-LAST:event_txt_volumenFocusGained

    private void txt_volumenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_volumenKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_volumenKeyTyped

    private void combo_personActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_personActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_personActionPerformed

    private void btn_assignMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_assignMousePressed
        NovusUtils.beep();
        assignSaleToPerson();
    }//GEN-LAST:event_btn_assignMousePressed

    private void table_assignationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_assignationsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_table_assignationsMouseClicked

    private void table_assignationsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_assignationsMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_table_assignationsMouseReleased

    private void jLabel15MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseReleased
        NovusUtils.beep();
        addPanel();
        mostrarPanel(SALTOS_LECTURAS);
    }//GEN-LAST:event_jLabel15MouseReleased

    private void jLabelCorregirSaltosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCorregirSaltosMouseClicked
        NovusUtils.beep();
        addPanel();
        loadData();
        mostrarPanel(SALTOS_LECTURAS);
    }//GEN-LAST:event_jLabelCorregirSaltosMouseClicked

    private void jLabel20MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MousePressed
        NovusUtils.beep();
        renderizarCambiosHistorial();
    }//GEN-LAST:event_jLabel20MousePressed

    private void jLabel25MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseReleased
        NovusUtils.beep();
        mostrarPanel(MENU);
    }//GEN-LAST:event_jLabel25MouseReleased

    private void jComboMostrarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboMostrarItemStateChanged

    }//GEN-LAST:event_jComboMostrarItemStateChanged

    private void jComboMostrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboMostrarMouseReleased

    }//GEN-LAST:event_jComboMostrarMouseReleased

    private void jComboMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboMostrarActionPerformed
        NovusUtils.beep();
        setTimeout(1, () -> {
            SolicitarCambios();
            renderizarCambiosHistorial();
        });
    }//GEN-LAST:event_jComboMostrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_assign;
    private javax.swing.JLabel btn_atras;
    private javax.swing.JLabel btn_save;
    private javax.swing.JComboBox<String> combo_person;
    private javax.swing.JComboBox<String> combo_razones;
    private javax.swing.JScrollPane container_asignations;
    private javax.swing.JPanel correccion_saltos;
    private javax.swing.JPanel historial_saltos;
    private javax.swing.JComboBox<String> jComboMostrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
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
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCorregirSaltos;
    private javax.swing.JLabel jLabelHistorico;
    public static javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    public javax.swing.JTable jTable2;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jpromotor3;
    private javax.swing.JLabel lbl_difference;
    private javax.swing.JLabel lbl_hose;
    private javax.swing.JLabel lbl_product;
    private javax.swing.JLabel lbl_remainder;
    private javax.swing.JLabel lbstatus;
    private javax.swing.JPanel menu;
    private javax.swing.JPanel panel_principal;
    private javax.swing.JPanel pnl_keyboard;
    private javax.swing.JPanel pnl_sale_data;
    private javax.swing.JPanel saltos_lecturas;
    private javax.swing.JTable table_assignations;
    private javax.swing.JTextField txt_volumen;
    // End of variables declaration//GEN-END:variables

    int getSelectedRowIndex() {
        return this.jTable1.getSelectedRow();
    }

    boolean isSelectedRow() {
        return this.getSelectedRowIndex() > -1;
    }

    void selectRow() {
        if (this.isSelectedRow()) {
            NovusUtils.beep();
            this.jLabel17.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        } else {
            this.jLabel17.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        }
    }

    void seleccionaSaltoLectura() {
        final int INDEX_COL_HOSE = 2;
        if (this.isSelectedRow()) {
            long hoseSelected = (long) this.jTable1.getValueAt(this.getSelectedRowIndex(), INDEX_COL_HOSE);
            JsonObject detailsHoseObject = this.misfitHoses.get(hoseSelected);
            ajustaSaltoLectura(detailsHoseObject, true);
        }
    }

    public void ajustaSaltoLectura(JsonObject detailsHoseObject, boolean open) {
        if (open) {
            addPanel();
            mostrarPanel(CORRECCION_SALTOS);
            loadData(detailsHoseObject);
            NovusUtils.setTablePrimaryStyle(this.table_assignations);
            this.loadData();
        }
    }

    private void mostrarPanel(String panel) {
        CardLayout card = (CardLayout) panel_principal.getLayout();
        card.show(panel_principal, panel);
    }

    /*correcion de salto de lectura*/
    void setModel(JsonObject detailHose) {
        this.detailHose = detailHose;
        double volume = Double.parseDouble(detailHose.get("diff_cantidad").getAsString().toUpperCase().trim());
        this.calculateRemainderVolume(volume);
    }

    double calculateRemainderVolume(double assignedVolume) {
        this.remainderVolume += assignedVolume;
        this.paintRemainderVolume(remainderVolume + "");
        return this.remainderVolume;
    }

    JsonObject getModel() {
        return this.detailHose;
    }

    void loadView() {
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.setTablePrimaryStyle(this.table_assignations);
        this.toggleAlphabeticKeyboard(false);
    }

    void toggleAlphabeticKeyboard(boolean enable) {
        TecladoExtendido keyboard = (TecladoExtendido) this.pnl_keyboard;
        keyboard.habilitarAlfanumeric(enable);
    }

    void loadData(JsonObject detailHose) {
        this.setModel(detailHose);
        this.setReasonComboOptions();
        try {
            this.allPersons = this.fetchAllPersons(true);
        } catch (DAOException ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        this.renderComboAllPersons(this.allPersons);
        this.renderComboOptions();
        this.renderHoseData(this.getModel());
    }

    void setReasonComboOptions() {
        this.COMBO_REASON_OPTIONS = new TreeMap<>();
        this.COMBO_REASON_OPTIONS.put(COMBO_OPTION_FALLA_TECNICA_ID, new Gson().fromJson(
                "{\"id\": " + COMBO_OPTION_FALLA_TECNICA_ID + ", \"label\": \"FALLA TECNICA\"}", JsonObject.class));
        double volume = Double.parseDouble(detailHose.get("diff_cantidad").getAsString().trim());
        if (volume > 0.0 && Main.ADMIN != -1) {
            this.COMBO_REASON_OPTIONS.put(COMBO_OPTION_VENTA_FUERA_SISTEMA_ID,
                    new Gson().fromJson(
                            "{\"id\": " + COMBO_OPTION_VENTA_FUERA_SISTEMA_ID + ", \"label\": \"VENTA FUERA SISTEMA\"}",
                            JsonObject.class));
        }
        this.COMBO_REASON_OPTIONS.put(COMBO_OPTION_AJUSTE_INICIAL_ID, new Gson().fromJson(
                "{\"id\": " + COMBO_OPTION_AJUSTE_INICIAL_ID + ", \"label\": \"AJUSTE INICIAL\"}", JsonObject.class));
        this.COMBO_REASON_OPTIONS.put(COMBO_OPTION_MANTENIMIENTO_ID, new Gson().fromJson(
                "{\"id\": " + COMBO_OPTION_MANTENIMIENTO_ID + ", \"label\": \"MANTENIMIENTO\"}", JsonObject.class));

    }

    double getDifferenceVolumen() {
        return Double.parseDouble(detailHose.get("diff_cantidad").getAsString().toUpperCase().trim());
    }

    double getProductPrice() {
        JsonObject detailHose = this.getModel();
        double productPrice = detailHose.get("precio").getAsDouble();
        return productPrice;
    }

    void renderComboOptions() {
        if (this.COMBO_REASON_OPTIONS != null) {
            this.combo_razones.removeAllItems();
            for (Map.Entry<Integer, JsonObject> entry : this.COMBO_REASON_OPTIONS.entrySet()) {
                if ((this.allPersons == null || this.allPersons.size() == 0)
                        && (entry.getKey() == COMBO_OPTION_VENTA_FUERA_SISTEMA_ID)) {
                    continue;
                }
                JsonObject optionObject = entry.getValue();
                String label = optionObject.get("label").getAsString();
                this.combo_razones.addItem(label.trim().toUpperCase());
            }
        }
    }

    void renderComboAllPersons(TreeMap<Integer, PersonaBean> allPersons) {
        if (allPersons != null) {
            this.combo_person.removeAllItems();
            for (Map.Entry<Integer, PersonaBean> entry : allPersons.entrySet()) {
                PersonaBean person = entry.getValue();
                this.combo_person.addItem(person.getNombre().trim().toUpperCase());
            }
        }
    }

    void renderAssignations(ArrayList<JsonObject> assignations) {// POINT
        this.table_assignations.setAutoCreateRowSorter(true);
        DefaultTableModel dm = (DefaultTableModel) this.table_assignations.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) this.table_assignations.getModel();
        for (JsonObject object : assignations) {
            String personName = object.get("responsable").getAsString().trim().toUpperCase();
            float volume = NovusUtils.fixed(object.get("galones").getAsFloat(), 3);
            float total = NovusUtils.fixed(object.get("total").getAsFloat(), 3);
            float initialAccumulatedVolume = NovusUtils.fixed(object.get("acum_vol_inicial").getAsFloat(), 3);
            float finalAccumulatedVolume = NovusUtils.fixed(object.get("acum_vol_final").getAsFloat(), 3);
            float initialAccumulatedSale = NovusUtils.fixed(object.get("acum_ven_inicial").getAsFloat(), 3);
            float finalAccumulatedSale = NovusUtils.fixed(object.get("acum_ven_final").getAsFloat(), 3);
            try {
                defaultModel.addRow(new Object[]{
                    personName,
                    volume,
                    total,
                    initialAccumulatedVolume,
                    finalAccumulatedVolume,
                    initialAccumulatedSale,
                    finalAccumulatedSale
                });
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
        }
    }

    TreeMap<Integer, PersonaBean> fetchAllPersons(boolean fetchInactives) throws DAOException {
        return PersonaFacade.fetchAllPersons(fetchInactives);
    }

    PersonaBean getSelectedPerson() {
        PersonaBean selectedPerson = null;
        int selectedIndex = this.combo_person.getSelectedIndex() > -1 ? this.combo_person.getSelectedIndex() : 0;
        selectedPerson = this.allPersons.get(selectedIndex);
        return selectedPerson;
    }

    JsonObject getSelectedReason() {
        JsonObject reasonObject = null;
        for (Map.Entry<Integer, JsonObject> entry : this.COMBO_REASON_OPTIONS.entrySet()) {
            if (entry.getValue().get("label").getAsString().equals(this.combo_razones.getSelectedItem())) {
                reasonObject = entry.getValue();
            }
        }
        return reasonObject;
    }

    void renderHoseData(JsonObject detailHose) {
        String product = detailHose.get("producto").getAsString().toUpperCase().trim();
        String hose = detailHose.get("manguera").getAsString().toUpperCase().trim();
        this.lbl_product.setText(product);
        this.lbl_hose.setText(hose);
        this.lbl_difference.setText(this.getDifferenceVolumen() + "");
    }

    void paintRemainderVolume(String remainderVolume) {
        this.lbl_remainder.setText(remainderVolume);
    }

    void handleComboReasonChange() {
        JsonObject SelectedReason = this.getSelectedReason();
        if (SelectedReason == null) {
            return;
        }
        int reasonId = SelectedReason.get("id").getAsInt();
        switch (reasonId) {
            case COMBO_OPTION_VENTA_FUERA_SISTEMA_ID:
                this.togglePanelSaleData(true);
                break;
            default:
                this.togglePanelSaleData(false);
                break;
        }
    }

    void togglePanelSaleData(boolean active) {
        this.toggleEnableComponents(this.pnl_sale_data, active);
        this.container_asignations.setVisible(active);
        this.pnl_keyboard.setVisible(active);
    }

    void toggleEnableComponents(Container container, boolean active) {
        Component[] components = container.getComponents();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                if (component instanceof JPanel) {
                    this.toggleEnableComponents((Container) component, active);
                } else {
                    component.setEnabled(active);
                }
            }
        }
    }

    JsonObject getBuiltAssignation(PersonaBean selectedPerson, double volume) {
        JsonObject assignationObject = new JsonObject();
        double volumeAccumulatedAssigned = this.getDifferenceVolumen() - this.remainderVolume;
        double saleAccumulated = this.getProductPrice() * volumeAccumulatedAssigned;
        float currentAmount = this.detailHose.get("act_cantidad").getAsFloat();
        float currentImport = this.detailHose.get("acumulado_cantidad_surt").getAsFloat();
        double currentSale = this.getProductPrice() * volume;
        assignationObject.addProperty("identificadorResponsable", selectedPerson.getId());
        assignationObject.addProperty("responsable", selectedPerson.getNombre().trim());
        assignationObject.addProperty("galones", volume);
        assignationObject.addProperty("total", currentSale);
        assignationObject.addProperty("acum_vol_inicial", NovusUtils.fixed(volumeAccumulatedAssigned + currentAmount, 3));
        assignationObject.addProperty("acum_vol_final", currentAmount + volume + volumeAccumulatedAssigned);
        assignationObject.addProperty("acum_ven_inicial", saleAccumulated + currentImport);
        assignationObject.addProperty("acum_ven_final", saleAccumulated + currentSale + currentImport);
        return assignationObject;
    }

    void handleAssignations(PersonaBean selectedPerson, double volume) {
        this.assignations.add(this.getBuiltAssignation(selectedPerson, volume));
        this.calculateRemainderVolume(-volume);
        this.resetFields();
        this.renderAssignations(this.assignations);
    }

    void resetFields() {
        this.txt_volumen.setText("");
    }

    void handleSaveButton() {
        JsonObject SelectedReason = this.getSelectedReason();
        if (SelectedReason == null) {
            return;
        }
        int reasonId = SelectedReason.get("id").getAsInt();
        if (reasonId == NovusConstante.COMBO_OPTION_VENTA_FUERA_SISTEMA_ID
                && (this.assignations.isEmpty() || this.remainderVolume > 0)) {
            notificacion("NO SE HA ASIGNADO EL VOLUMEN COMPLETAMENTE", RUTA_IMAGEN.concat("btBad.png"), true, 5000, CORRECCION_SALTOS);
            return;
        }
        try {
            JsonObject response = this.fetchFixMisFitHoses();
            if (response == null) {
                notificacion("HA OCURRIDO UN ERROR AL CORREGIR", RUTA_IMAGEN.concat("btBad.png"), true, 5000, SALTOS_LECTURAS);
                if (validarCorreccionSaltoLecturaUseCase.execute(this.detailHose)) {
                    actualizarNovedadSaltoLecturaUseCase.execute(this.detailHose);
                    this.assignations = new ArrayList<>();
                }
            } else {
                notificacion("SALTO MANGUERA CORREGIDO EXITOSAMENTE", RUTA_IMAGEN.concat("btOk.png"), true, 5000, SALTOS_LECTURAS);
                actualizarNovedadSaltoLecturaUseCase.execute(this.detailHose);
                this.assignations = new ArrayList<>();
            }
        } catch (Exception e) {
            Logger.getLogger(MenuSaltosLecturas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    JsonObject fetchFixMisFitHoses() {
        JsonObject SelectedReason = this.getSelectedReason();
        if (SelectedReason == null) {
            return null;
        }
        int reasonId = SelectedReason.get("id").getAsInt();
        return SurtidorFacade.fetchFixMisFitHoses(this.assignations, this.detailHose, reasonId);
    }

    void assignSaleToPerson() {
        String assignedVolume = this.txt_volumen.getText().trim();
        if (assignedVolume.equals("")) {
            notificacion("DEBE INDICAR VOLUMEN", RUTA_IMAGEN.concat("btBad.png"), true, 5000, CORRECCION_SALTOS);
        } else if (!isNumber(assignedVolume)) {
            notificacion("EL VOLUMEN INGRESADO NO ES UN NUMERO VALIDO", RUTA_IMAGEN.concat("btBad.png"), true, 5000, CORRECCION_SALTOS);
        } else {
            double assignedVolumeValue = Double.parseDouble(assignedVolume);
            if (assignedVolumeValue <= 0) {
                notificacion("VOLUMEN DEBE SER MAYOR A CERO", RUTA_IMAGEN.concat("btBad.png"), true, 5000, CORRECCION_SALTOS);
                return;
            }
            if (assignedVolumeValue > this.getDifferenceVolumen()) {

                notificacion("VOLUMEN INGRESADO EXCEDE LA DIFERENCIA", RUTA_IMAGEN.concat("btBad.png"), true, 5000, CORRECCION_SALTOS);
                return;
            }
            if ((this.remainderVolume - assignedVolumeValue) < 0) {
                notificacion("VOLUMEN INGRESADO EXCEDE EL RESTANTE", RUTA_IMAGEN.concat("btBad.png"), true, 5000, CORRECCION_SALTOS);
                return;
            }
            this.handleAssignations(this.getSelectedPerson(), assignedVolumeValue);
            NovusUtils.printLn("RESTANTE: " + this.remainderVolume);
        }
    }

    boolean isNumber(String stringToValidate) {
        try {
            Double.parseDouble(stringToValidate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void cerrar() {
        this.runnable.run();
    }

    public void notificacion(String mensaje, String imagen, boolean autocerrar, int timeOut, String panel) {
        Runnable cerrar = () -> {
            cargarNotificacion(saltos_lecturas);
            loadData();
            mostrarPanel(panel);
        };

        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(imagen).setHabilitar(true).setRunnable(cerrar)
                .setLetterCase(LetterCase.FIRST_UPPER_CASE).build();
        cargarNotificacion(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));

        if (autocerrar) {
            mostrarPanel(panel);
        }
    }

    private void cargarNotificacion(JPanel panel) {
        panel_principal.removeAll();
        panel_principal.add(panel);
        panel_principal.revalidate();
        panel_principal.repaint();
    }
}

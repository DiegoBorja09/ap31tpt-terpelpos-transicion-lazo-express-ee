package com.firefuel;

import com.application.useCases.persons.GetAllPromotoresUseCase;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import com.bean.ConsecutivoBean;
import com.bean.ImpuestosBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.facturacion.electronica.NotasCredito;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import com.neo.print.services.PrinterFacade;
import com.neo.print.services.ReportesFE;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class VentasHistorialKCOView extends JDialog {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    boolean activarAcumular;
    final int CONSULTAR_VENTAS_FECHAS = 1;
    ArrayList<PersonaBean> promotores = new ArrayList<>();
    ArrayList<MovimientosBean> listaVentasKiosco = new ArrayList<>();
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    TreeMap<Long, Float> cache = new TreeMap<>();
    JsonArray datanota = new JsonArray();

    InfoViewController parent;
    JFrame frame;
    private MovimientosDao sdao = null;
    private boolean isStarted = true;
    public JsonObject respuestaNota = new JsonObject();
    public static int numeroImprimir = 0;
    private final Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"));
    private final Icon botonBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png"));
    Runnable recargar;
    static final Logger logger = Logger.getLogger(VentasHistorialKCOView.class.getName());

    Runnable handler = null;
    DecimalFormat formatoCantidad = new DecimalFormat("#.00");
    
    // Cola de impresión para archivo TXT
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    private String textoOriginalBotonImprimir = "IMPRIMIR";
    
    // Iconos para el botón de imprimir
    private final Icon iconoBotonImprimirActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"));
    private final Icon iconoBotonImprimirBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"));
    
    // Timer para verificar la cola periódicamente
    private javax.swing.Timer timerVerificarCola;
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();

    public VentasHistorialKCOView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        init();
    }

    public VentasHistorialKCOView(InfoViewController parent, boolean modal, JFrame frame) {
        super(parent, modal);
        this.parent = parent;
        this.frame = frame;
        initComponents();
        init();
    }

    private void habilitarFE() {
        FacturacionElectronica fac = new FacturacionElectronica();
        jLabel8.setVisible(fac.aplicaFE());
    }

    private void init() {
        habilitarFE();
        lbstatus.setText("");
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png")));
        jLabel8.setEnabled(false);
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE));
        rSDateChooser1.setDatoFecha(ca.getTime());
        rSDateChooser2.setDatoFecha(new Date());

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        
        // Agregar listener para actualizar estado del botón al seleccionar fila
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonImprimirKCO();
            }
        });
        
        // Iniciar timer para verificar la cola cada 500ms
        timerVerificarCola = new javax.swing.Timer(500, e -> actualizarEstadoBotonImprimirKCO());
        timerVerificarCola.start();

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

        cargarPromotores();
    }

    void cargarPromotores() {
        PersonasDao pdao = new PersonasDao();
        try {
            promotores.clear();
            //promotores = pdao.getAllPromotores();
            GetAllPromotoresUseCase getAllPromotoresUseCase = new GetAllPromotoresUseCase();
            promotores = getAllPromotoresUseCase.execute();
            int i = 0;
            for (PersonaBean promotor : promotores) {
                jComboPromotor.addItem(promotor.getNombre().trim().toUpperCase());
                if (promotor.getId() == Main.persona.getId()) {
                    jComboPromotor.setSelectedIndex(i);
                    jCheckBox1.setSelected(true);
                }
                i++;
            }
            toggleComboPromotor();
            isStarted = false;
            refrescar("cargarPromotores");
        //} catch (DAOException ex) {
        } catch (Exception ex) {
            NovusUtils.printLn("❌ Error al obtener getAllPromotoresUseCase: " + ex.getMessage());
            Logger.getLogger(VentasHistorialCanastillaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        NovusUtils.printLn("PROMOTORES CARGADOS");
        lbstatus.setText("PROMOTORES CARGADOS");
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        lbstatus = new javax.swing.JLabel();
        rSDateChooser1 = new rojeru_san.componentes.RSDateChooser();
        rSDateChooser2 = new rojeru_san.componentes.RSDateChooser();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jComboPromotor = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        pnl_confirmacion = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        NO = new javax.swing.JLabel();
        SI1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        lbstatus.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lbstatus.setForeground(new java.awt.Color(255, 255, 255));
        lbstatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbstatus.setText("CARGANDO VENTAS ...");
        pnl_principal.add(lbstatus);
        lbstatus.setBounds(470, 730, 650, 50);

        rSDateChooser1.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser1.setEnabled(false);
        rSDateChooser1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser1.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser1.setPlaceholder("Fecha Inicial");
        pnl_principal.add(rSDateChooser1);
        rSDateChooser1.setBounds(30, 110, 210, 50);

        rSDateChooser2.setColorBackground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorButtonHover(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorDiaActual(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setColorForeground(new java.awt.Color(186, 12, 47));
        rSDateChooser2.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setFormatoFecha("yyyy-MM-dd");
        rSDateChooser2.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        rSDateChooser2.setPlaceholder("Fecha Final");
        pnl_principal.add(rSDateChooser2);
        rSDateChooser2.setBounds(270, 110, 210, 50);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        pnl_principal.add(jpromotor);
        jpromotor.setBounds(820, 40, 460, 40);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "PREFIJO", "FECHA", "PROMOTOR", "CANT. PRODU", "MEDIO PAGO", "TOTAL IMPUESTO", "VALOR TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(190);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(150);
        }

        pnl_principal.add(jScrollPane2);
        jScrollPane2.setBounds(30, 180, 1220, 510);

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
        jLabel1.setBounds(840, 100, 190, 60);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jLabel4.setText("IMPRIMIR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        pnl_principal.add(jLabel4);
        jLabel4.setBounds(1040, 100, 180, 60);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("HISTORIAL VENTAS MARKET");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(100, 0, 720, 90);

        jLabel2.setText("jLabel2");
        pnl_principal.add(jLabel2);
        jLabel2.setBounds(390, 420, 200, 16);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        pnl_principal.add(jpromotor1);
        jpromotor1.setBounds(820, 10, 460, 20);

        jComboPromotor.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jComboPromotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboPromotorActionPerformed(evt);
            }
        });
        pnl_principal.add(jComboPromotor);
        jComboPromotor.setBounds(500, 120, 320, 50);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jLabel8.setText("ANULAR");
        jLabel8.setToolTipText("");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel8MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel8);
        jLabel8.setBounds(130, 720, 270, 70);

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(186, 12, 47));
        jCheckBox1.setText("POR PROMOTOR");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        pnl_principal.add(jCheckBox1);
        jCheckBox1.setBounds(500, 90, 320, 26);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnl_principal.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        jLabel6.setText("CARGANDO ...");
        pnl_principal.add(jLabel6);
        jLabel6.setBounds(610, 750, 77, 16);

        pnl_container.add(pnl_principal, "pnl_principal");

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
        NO.setBounds(470, 480, 170, 54);

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

        pnl_container.add(pnl_confirmacion, "pnl_confirmacion");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox1ActionPerformed
        if (!isStarted) {
            NovusUtils.beep();
            refrescar("jCheckBox1ActionPerformed");
            toggleComboPromotor();
        }
    }// GEN-LAST:event_jCheckBox1ActionPerformed

    void toggleComboPromotor() {
        jComboPromotor.setEnabled(jCheckBox1.isSelected());
    }

    private void jComboPromotorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboPromotorActionPerformed
        if (!isStarted) {
            refrescar("jComboPromotorActionPerformed");
        }
    }// GEN-LAST:event_jComboPromotorActionPerformed

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
        // ⚠️ VERIFICACIÓN DE BLOQUEO - PRIMERA LÍNEA
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("⚠️ BOTÓN BLOQUEADO - Impresión en proceso. Ignorando clic.");
            NovusUtils.beep();
            return;
        }
        
        NovusUtils.beep();
        JsonObject data = imprimirfacFE(datanota, numeroImprimir);
        boolean imprimirComprobante = data.get("comprobante_fe").getAsBoolean();
        if (imprimirComprobante) {
            imprimirFE(numeroImprimir, data);
        } else {
            selectme();
        }

    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        refrescar("jLabel1MousePressed");
    }// GEN-LAST:event_jLabel1MousePressed

    private void jLabel8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseReleased
        if (jLabel8.isEnabled()) {
            showPanel("pnl_confirmacion");
            jLabel9.setText("<html><center>¿Desea generar la devolución de esta venta?</center></html>");
            handler = () -> {
                enviarNotaCredito();
            };
        }


    }//GEN-LAST:event_jLabel8MouseReleased

    private void NOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NOMouseClicked
        cambiarPanelHome();
    }//GEN-LAST:event_NOMouseClicked

    private void SI1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SI1MouseClicked
        handler.run();
        cambiarPanelHome();
    }//GEN-LAST:event_SI1MouseClicked

    private void fndMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fndMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fndMousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        cerrar();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        seleccionar(evt);
    }

    private void cerrar() {
        NovusUtils.beep();
        this.setVisible(false);
        this.dispose();
    }

    private void showPanel(String panel) {        
       NovusUtils.showPanel(pnl_container, panel);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NO;
    private javax.swing.JLabel SI1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel fnd;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboPromotor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JLabel lbstatus;
    private javax.swing.JPanel pnl_confirmacion;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    private rojeru_san.componentes.RSDateChooser rSDateChooser1;
    private rojeru_san.componentes.RSDateChooser rSDateChooser2;
    // End of variables declaration//GEN-END:variables

    public void refrescar(String accion) {
        NovusUtils.printLn("REFRESCANDO VENTAS ..." + accion);
        Async(() -> {
            lbstatus.setText("Buscando ventas...".toUpperCase());
            solicitarVentas(rSDateChooser1.getDatoFecha(), rSDateChooser2.getDatoFecha());

            jTable1.setAutoCreateRowSorter(true);
            try {
                DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
                dm.getDataVector().removeAllElements();
                dm.fireTableDataChanged();

                DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
                int i = 0;
                for (MovimientosBean movimiento : listaVentasKiosco) {
                    BigDecimal cantidadTotal = BigDecimal.ZERO;
                    for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                        BigDecimal cantidad = BigDecimal.valueOf(entry.getValue().getCantidad());
                        cantidadTotal = cantidadTotal.add(cantidad.setScale(2, RoundingMode.HALF_UP));
                    }
                    String medioPago = "";
                    for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                        medioPago = entry.getValue().getDescripcion().toUpperCase();
                    }

                    try {
                        defaultModel.addRow(new Object[]{
                            movimiento.getId(),
                            movimiento.getConsecutivo().getPrefijo() + "-" + movimiento.getConsecutivo().getConsecutivo_actual(),
                            sdf2.format(movimiento.getFecha()),
                            movimiento.getPersonaNombre(),
                            cantidadTotal,
                            movimiento.getMediosPagos().size() > 1 ? "COMBINADO" : medioPago,
                            "$ " + df.format(movimiento.getImpuestoTotal()),
                            "$ " + df.format(movimiento.getVentaTotal())

                        });
                    } catch (Exception e) {
                        NovusUtils.printLn(e.getMessage());
                    }
                }
                setTimeout(5, () -> {
                    resetLabel(lbstatus);
                });
                lbstatus.setText("");
                // jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            } catch (Exception s) {
                Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, s);
            }
        });
    }

    private void selectme() {
        try {
            // VERIFICAR BLOQUEO ANTES DE OBTENER DATOS - PRIMERA LÍNEA
            if (botonImprimirBloqueado) {
                NovusUtils.printLn("⚠️ BOTÓN BLOQUEADO - Impresión en proceso. Ignorando clic.");
                NovusUtils.beep();
                return;
            }
            
            int r = jTable1.getSelectedRow();
            int c = jTable1.getSelectedColumn();
            if (r > -1) {

                long value = (long) jTable1.getValueAt(r, 0);
                
                // BLOQUEAR INMEDIATAMENTE al hacer click
                bloquearBotonImprimirKCO();
                
                // Tipo de reporte fijo para este flujo (debe coincidir con la notificación)
                String reportType = "CONSULTAR_VENTAS";
                
                // Verificar si ya está en cola de impresión
                if (existeEnColaPendiente(value, reportType)) {
                    NovusUtils.printLn("El registro ya está en cola de impresión - ID: " + value);
                    // No desbloquear porque ya está en cola
                    return;
                }
                guardarRegistroPendiente(value, reportType);
                
                // Buscar el movimiento seleccionado
                MovimientosBean movimientoSeleccionado = null;
                for (MovimientosBean movimiento : listaVentasKiosco) {
                    if (value == movimiento.getId()) {
                        movimientoSeleccionado = movimiento;
                        break;
                    }
                }
                
                if (movimientoSeleccionado != null) {
                    final MovimientosBean movimientoFinal = movimientoSeleccionado;
                    final long valueFinal = value;
                    
                    // Ejecutar el proceso de verificación e impresión en un hilo separado
                    new Thread(() -> {
                        try {
                            // Pequeña pausa para asegurar que la UI se actualice
                            Thread.sleep(50);
                            
                            // 1. Verificar que el servicio de impresión esté activo y saludable (usando caso de uso con cache)
                            CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
                            
                            if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                                // Servicio no responde o no está saludable - eliminar registro de cola y actualizar UI
                                eliminarRegistroPendiente(valueFinal, reportType);
                                final String mensaje = healthResult.obtenerMensajeError();
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    NovusUtils.printLn("❌ Servicio de impresión no está disponible: " + mensaje);
                                    desbloquearBotonImprimirKCO();
                                    mostrarPanelMensaje(mensaje, "/com/firefuel/resources/btBad.png");
                                });
                                return;
                            }
                            
                            // 3. Servicio OK - Enviar a Python de forma asíncrona
                            NovusUtils.printLn("✅ Servicio de impresión activo e impresora conectada - Enviando a Python");
                            
                            String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
                            TreeMap<String, String> headerPython = new TreeMap<>();
                            headerPython.put("Content-type", "application/json; charset=UTF-8");
                            headerPython.put("Accept", "application/json");
                            
                            JsonObject body = new JsonObject();
                            JsonObject data = new JsonObject();
                            body.addProperty("identificadorMovimiento", valueFinal);
                            body.addProperty("movement_id", valueFinal);
                            body.addProperty("flow_type", "CONSULTAR_VENTAS");
                            body.addProperty("report_type", "FACTURA");
                            body.addProperty("database_source", "REGISTRO");  // 🔥 Indicar que debe buscar en BD Registro
                            body.addProperty("tipo_negocio", "KIOSCO");       // 🔥 Tipo de negocio
                            body.add("body", data);
                            
                            NovusUtils.printLn("🌐 URL Servicio Python: " + url);
                            NovusUtils.printLn("📤 Payload: " + body.toString());
                            
                            // Enviar el request sin esperar respuesta (asíncrono)
                            ClientWSAsync client = new ClientWSAsync("IMPRESION KCO", url, NovusConstante.POST, body, true, false, headerPython, 10000);
                            client.start();
                            
                            NovusUtils.printLn("✅ Request de impresión enviado - Movimiento: " + valueFinal);
                            NovusUtils.printLn("🔔 Botón se mantendrá en IMPRIMIENDO... hasta recibir notificación");
                            // NO desbloquear botón - se desbloqueará cuando llegue la notificación
                            
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            javax.swing.SwingUtilities.invokeLater(() -> desbloquearBotonImprimirKCO());
                        } catch (Exception e) {
                            Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, e);
                            javax.swing.SwingUtilities.invokeLater(() -> desbloquearBotonImprimirKCO());
                        }
                    }).start();
                }
            }

        } catch (Exception d) {
            desbloquearBotonImprimirKCO();
            NovusUtils.printLn(d.getMessage());
        }
    }

    public void solicitarVentas(Date fechaInial, Date fechaFinal) {
        if (fechaInial == null) {
            fechaInial = new Date();
        }
        if (fechaFinal == null) {
            fechaFinal = new Date();
        }
        Long identificadorPromotor = null;
        if (jCheckBox1.isSelected() && !promotores.isEmpty()) {
            identificadorPromotor = promotores.get(jComboPromotor.getSelectedIndex()).getId();
        }

        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
        SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        if (sdao == null) {
            sdao = new MovimientosDao();
        }

        JsonObject response = sdao.obtenerVentasKiosco(sdf.format(fechaInial) + " 00:00:00", sdf.format(fechaFinal) + " 23:59:59", identificadorPromotor == null ? "" : identificadorPromotor + "");

        try {
            if (response != null) {
                listaVentasKiosco.clear();
                JsonArray array = response.get("data").getAsJsonArray();
                datanota = response.get("data").getAsJsonArray();
                int i = 1;
                for (JsonElement jsonElement : array) {
                    try {
                        lbstatus.setText("VENTAS " + i + "/" + array.size());
                        JsonObject json = jsonElement.getAsJsonObject();
                        MovimientosBean mov = new MovimientosBean();
                        mov.setId(json.get("id").getAsLong());
                        mov.setFecha(sdfSQL.parse(json.get("fecha").getAsString()));
                        mov.setVentaTotal(json.get("venta_total").getAsFloat());
                        mov.setPersonaId(json.get("responsables_id").getAsLong());
                        mov.setPersonaNombre(json.get("nombres").getAsString());
                        mov.setImpreso(json.get("impreso").getAsString());
                        mov.setClienteId(222222);
                        mov.setClienteNit("CONSUMIDOR FINAL");
                        mov.setEmpresasId(Main.credencial.getEmpresas_id());
                        mov.setClienteNombre("CONSUMIDOR FINAL");
                        mov.setAtributos(json.get("atributos").getAsJsonObject());
                        mov.setGrupoJornadaId(json.get("jornadas_id").getAsLong());
                        mov.setMovmientoEstado("A");
                        if (mov.getAtributos().get("consecutivo") != null && mov.getAtributos().get("consecutivo").isJsonObject()) {
                            JsonObject jsonConsecutivo = mov.getAtributos().getAsJsonObject("consecutivo");
                            ConsecutivoBean conObtenido = new ConsecutivoBean();

                            conObtenido.setId(jsonConsecutivo.get("id").getAsLong());
                            conObtenido.setPrefijo(!jsonConsecutivo.get("prefijo").isJsonNull() ? jsonConsecutivo.get("prefijo").getAsString() : "");
                            conObtenido.setConsecutivo_actual(!json.get("consecutivo_venta").isJsonNull() ? json.get("consecutivo_venta").getAsLong() : 0);
                            conObtenido.setObservaciones("");
                            mov.setConsecutivo(conObtenido);
                        }

                        LinkedHashMap<Long, MovimientosDetallesBean> detalles = new LinkedHashMap<>();

                        float totalImpuestos = 0;
                        float impoConsumo = 0;
                        for (JsonElement jsonElementDetalles : json.getAsJsonArray("detalles")) {

                            JsonObject jsonDetalles = jsonElementDetalles.getAsJsonObject();

                            MovimientosDetallesBean detalleVenta = new MovimientosDetallesBean();
                            JsonObject atributos = jsonDetalles.get("atributos") != null ? jsonDetalles.get("atributos").getAsJsonObject() : new JsonObject();
                            int tipo = atributos.get("tipo") != null && !atributos.get("tipo").isJsonNull() ? atributos.get("tipo").getAsInt() : 0;
                            String medida = jsonDetalles.get("unidad") != null && !jsonDetalles.get("unidad").isJsonNull() ? jsonDetalles.get("unidad").getAsString() : "";
                            detalleVenta.setTipo(tipo);
                            detalleVenta.setUnidades_medida(medida);
                            detalleVenta.setCantidadUnidad(jsonDetalles.get("cantidad").getAsFloat());
                            detalleVenta.setCantidad(jsonDetalles.get("cantidad").getAsFloat());
                            detalleVenta.setPlu(jsonDetalles.get("plu").getAsString());
                            detalleVenta.setCosto(jsonDetalles.get("costo_producto").getAsFloat());
                            detalleVenta.setPrecio(jsonDetalles.get("precio").getAsFloat());
                            detalleVenta.setSubtotal(jsonDetalles.get("sub_total").getAsFloat());
                            if (jsonDetalles.get("atributos").getAsJsonObject().has("cortecia") && !jsonDetalles.get("atributos").getAsJsonObject().get("cortecia").isJsonNull() && jsonDetalles.get("atributos").getAsJsonObject().get("cortecia").getAsBoolean()) {
                                detalleVenta.setId(Long.parseLong(jsonDetalles.get("id_producto").getAsLong() + "" + jsonDetalles.get("plu").getAsString()));
                            } else {
                                detalleVenta.setId(jsonDetalles.get("id_producto").getAsLong());
                            }
                            detalleVenta.setDescripcion(jsonDetalles.get("nombre_producto").getAsString());
                            detalleVenta.setAtributos(atributos);
                            ArrayList<ImpuestosBean> impuestos = new ArrayList<>();
                            for (JsonElement jsonElementImpuestos : jsonDetalles.getAsJsonArray("impuestos")) {
                                JsonObject jsonImpuestos = jsonElementImpuestos.getAsJsonObject();
                                String tipoProcentaje = jsonImpuestos.get("porcentaje_valor").getAsString();
                                if (!tipoProcentaje.equals(NovusConstante.SIMBOLS_PERCENTAGE)) {
                                    impoConsumo += jsonImpuestos.get("impuesto_valor").getAsFloat();
                                }
                            }

                            for (JsonElement jsonElementImpuestos : jsonDetalles.getAsJsonArray("impuestos")) {
                                JsonObject jsonImpuestos = jsonElementImpuestos.getAsJsonObject();
                                String porcenta_valor = jsonImpuestos.get("porcentaje_valor").getAsString();
                                ImpuestosBean impuesto = new ImpuestosBean();
                                impuesto.setId(jsonImpuestos.get("id_impuesto").getAsLong());
                                impuesto.setDescripcion(jsonImpuestos.get("nombre_impuesto").getAsString());
                                impuesto.setCalculado(jsonImpuestos.get("impuesto_valor").getAsFloat());

                                if (!cache.containsKey(jsonImpuestos.get("id_impuesto").getAsLong())) {

                                    try {
                                        impuesto.setValor(jsonImpuestos.get("valor").getAsFloat());
                                        cache.put(jsonImpuestos.get("id_impuesto").getAsLong(), jsonImpuestos.get("valor").getAsFloat());
                                    } catch (Exception ex) {
                                        Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    impuesto.setValor(cache.get(impuesto.getId()));
                                }

                                impuesto.setPorcentaje_valor(jsonImpuestos.get("porcentaje_valor").getAsString());
                                if (porcenta_valor.equals(NovusConstante.SIMBOLS_PERCENTAGE)) {
                                    totalImpuestos += impuesto.getCalculado();
                                }
                                impuestos.add(impuesto);
                            }
                            detalleVenta.setImpuestos(impuestos);
                            detalles.put(detalleVenta.getId(), detalleVenta);
                        }
                        mov.setDetalles(detalles);

                        float recibidoTotal = 0;
                        TreeMap<Long, MediosPagosBean> medios = new TreeMap<>();
                        for (JsonElement jsonElementMedios : json.getAsJsonArray("medios_pagos")) {
                            JsonObject jsonMedio = jsonElementMedios.getAsJsonObject();
                            MediosPagosBean medio = new MediosPagosBean();
                            medio.setId(jsonMedio.get("id_medio_de_pago").getAsLong());
                            medio.setDescripcion(jsonMedio.get("nombre_medio_de_pago").getAsString());
                            medio.setCambio(jsonMedio.get("valor_cambio").getAsFloat());
                            medio.setValor(jsonMedio.get("valor_total").getAsFloat());
                            medio.setRecibido(jsonMedio.get("valor_recibido").getAsFloat());
                            medio.setVoucher(jsonMedio.get("comprobante").getAsString());
                            medio.setComprobante(jsonMedio.get("tiene_comprobante").getAsString().equals("S"));
                            recibidoTotal += medio.getRecibido();
                            medios.put(medio.getId(), medio);
                        }
                        mov.setMediosPagos(medios);
                        mov.setImpuestoTotal(totalImpuestos);
                        mov.setRecibidoTotal(recibidoTotal);
                        listaVentasKiosco.add(mov);
                        i++;
                    } catch (Exception a) {
                        Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, a);
                        NovusUtils.printLn(a.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void seleccionar(MouseEvent evt) {
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            int seleccionar;

            seleccionar = jTable1.rowAtPoint(evt.getPoint());
            String numero = String.valueOf(jTable1.getValueAt(seleccionar, 0));
            int numeroVenta = Integer.parseInt(numero);
            numeroImprimir = numeroVenta;
            generarNotaProducto(datanota, numeroVenta);
            jLabel4.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        } else {
            jLabel4.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
        }
    }

    private void generarNotaProducto(JsonArray datanota, int numero) {
        NotasCredito nota = new NotasCredito();
        respuestaNota = nota.dataproductoNota(datanota, numero, jLabel8);

    }

    private void enviarNotaCredito() {
        NotasCredito nota = new NotasCredito();
        Runnable panelPrincipal = () -> refrescar("jLabel1MousePressed");
        showPanel("pnl_principal");
        JsonObject res = nota.enviar(respuestaNota, parent, this, false, panelPrincipal);
        NovusUtils.printLn(res + "");
    }

    private JsonObject imprimirfacFE(JsonArray datanota, int numero) {
        JsonObject data = new JsonObject();
        FacturacionElectronica fac = new FacturacionElectronica();
        data = fac.imprimirFE(datanota, numero);
        NovusUtils.printLn(data + "");
        return data;
    }

    private void imprimirFE(long movimientoId, JsonObject impresionVenta) {
        FacturacionElectronica facturacionElectronica = new FacturacionElectronica();
        ReportesFE reporte = new ReportesFE();
        if (facturacionElectronica.remisionActiva()) {
            reporte.reporteDuplicadoRemision(impresionVenta);
            mostrarPanelMensaje("COPIA DE VENTA IMPRESA", "/com/firefuel/resources/btOk.png");
        } else {
            mostrarPanelMensaje("ESPERE UN MOMENTO", "/com/firefuel/resources/loader_fac.gif", false);
            Runnable imprimir = () -> {
                JsonObject response = reporte.printDuplicadoFe(movimientoId);
                String icono = response.get("status").getAsInt() == 200 ? "/com/firefuel/resources/btOk.png" : "/com/firefuel/resources/btBad.png";
                mostrarPanelMensaje(response.get("message").getAsString().toUpperCase(), icono);
            };
            CompletableFuture.runAsync(imprimir);
        }

    }

    public void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                if (runnable != null) {
                    Thread.sleep(tiempo * 1000);
                    runnable.run();
                }
            } catch (InterruptedException e) {
                Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono) {

        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        PanelNotificacion panelMensaje = new PanelNotificacion(mensaje, icono, true, runnable, this);
        pnl_container.add("card_mensajes", panelMensaje);

        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        panelMensaje.mostrar();
        Async(runnable, 5);
    }

    public void mostrarPanelMensaje(String mensaje, String icono, boolean hablitarBoton) {
        PanelNotificacion panelMensaje = new PanelNotificacion(mensaje, icono, hablitarBoton, null);
        pnl_container.add("card_mensajes", panelMensaje);

        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        panelMensaje.mostrar();
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    }

    public void resetLabel(JLabel jlabel) {
        jlabel.setText("");
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                logger.log(java.util.logging.Level.WARNING, "Interrupted!", e);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    // ============================================
    // MÉTODOS DE BLOQUEO/DESBLOQUEO DE BOTÓN
    // ============================================
    
    /**
     * Actualiza el estado del botón de imprimir basado en si el registro está en cola
     */
    private void actualizarEstadoBotonImprimirKCO() {
        try {
            int r = jTable1.getSelectedRow();
            if (r >= 0) {
                long value = (long) jTable1.getValueAt(r, 0);
                String reportType = "CONSULTAR_VENTAS";
                
                // Verificar si está en cola de impresión
                if (existeEnColaPendiente(value, reportType)) {
                    // Está en cola - mostrar bloqueado con texto IMPRIMIENDO...
                    jLabel4.setText("IMPRIMIENDO...");
                    jLabel4.setIcon(iconoBotonImprimirBloqueado);
                    botonImprimirBloqueado = true;
                } else {
                    // No está en cola - mostrar normal
                    jLabel4.setText(textoOriginalBotonImprimir);
                    jLabel4.setIcon(iconoBotonImprimirActivo);
                    botonImprimirBloqueado = false;
                }
            } else {
                // Sin selección - mostrar deshabilitado
                jLabel4.setText(textoOriginalBotonImprimir);
                jLabel4.setIcon(iconoBotonImprimirBloqueado);
                botonImprimirBloqueado = false;
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error actualizando estado del botón: " + e.getMessage());
        }
    }
    
    // ============================================
    // MÉTODOS DE BLOQUEO/DESBLOQUEO DE BOTÓN
    // ============================================
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a IMPRIMIENDO...
     */
    private void bloquearBotonImprimirKCO() {
        botonImprimirBloqueado = true;
        jLabel4.setIcon(iconoBotonImprimirBloqueado);
        jLabel4.setText("IMPRIMIENDO...");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        
        // Forzar actualización
        pnl_principal.revalidate();
        pnl_principal.repaint();
    }
    
    /**
     * Desbloquea el botón de imprimir y restaura el texto original
     */
    private void desbloquearBotonImprimirKCO() {
        botonImprimirBloqueado = false;
        jLabel4.setIcon(iconoBotonImprimirActivo);
        jLabel4.setText(textoOriginalBotonImprimir);
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        
        // Forzar actualización
        pnl_principal.revalidate();
        pnl_principal.repaint();
    }
    
    // ============================================
    // MÉTODOS DE COLA DE IMPRESIÓN
    // ============================================
    
    /**
     * Verifica si un registro ya existe en la cola de impresión pendiente
     */
    private boolean existeEnColaPendiente(long id, String reportType) {
        try {
            File queueFile = new File(PRINT_QUEUE_FILE);
            if (!queueFile.exists()) {
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(queueFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() > 0) {
                    JsonArray registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registros) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.has("report_type")) {
                            long registroId = registro.get("id").getAsLong();
                            String registroType = registro.get("report_type").getAsString();
                            if (registroId == id && registroType.equals(reportType)) {
                                return true;
                            }
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
     * Guarda un registro en la cola de impresión pendiente
     */
    private void guardarRegistroPendiente(long id, String reportType) {
        try {
            File queueFile = new File(PRINT_QUEUE_FILE);
            File parentDir = queueFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            JsonArray registros = new JsonArray();
            if (queueFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(queueFile))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("Error leyendo archivo de cola de impresión: " + e.getMessage());
                    registros = new JsonArray();
                }
            }
            
            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("timestamp", System.currentTimeMillis());
            registros.add(nuevoRegistro);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(queueFile))) {
                writer.write(registros.toString());
            }
            
            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión pendiente
     */
    private void eliminarRegistroPendiente(long id, String reportType) {
        try {
            File queueFile = new File(PRINT_QUEUE_FILE);
            if (!queueFile.exists()) {
                return;
            }
            
            JsonArray registros = new JsonArray();
            try (BufferedReader reader = new BufferedReader(new FileReader(queueFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                if (content.length() > 0) {
                    JsonArray registrosOriginales = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registrosOriginales) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.has("report_type")) {
                            long registroId = registro.get("id").getAsLong();
                            String registroType = registro.get("report_type").getAsString();
                            if (!(registroId == id && registroType.equals(reportType))) {
                                registros.add(registro);
                            }
                        }
                    }
                }
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(queueFile))) {
                writer.write(registros.toString());
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            Logger.getLogger(VentasHistorialKCOView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    // ============================================

}

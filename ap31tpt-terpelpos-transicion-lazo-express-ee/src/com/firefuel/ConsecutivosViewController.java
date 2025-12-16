package com.firefuel;

import com.bean.ConsecutivoBean;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ConsecutivosViewController extends javax.swing.JDialog {

    private InfoViewController parent;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    DefaultTableCellRenderer textLeft;
    ArrayList<PersonaBean> personas;
    ArrayList<PersonaBean> personasCore;
    ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();
    static final Logger logger = Logger.getLogger(ConsecutivosViewController.class.getName());

    public ConsecutivosViewController(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel32 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel32);
        jLabel32.setBounds(90, 10, 10, 68);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CONSECUTIVOS");
        getContentPane().add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel30);
        jLabel30.setBounds(1130, 710, 10, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        jScrollPane1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PREFIJOS", "FEC. INI", "FEC. FIN", "CONS. INI", "CONS. ACTUAL", "CONS. FIN", "ESTADO", "TIPO CONS."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(30);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(150);
        }

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 140, 1260, 490);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 85, 1280, 618);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        getContentPane().add(jclock);
        jclock.setBounds(1150, 720, 110, 66);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textLeft = new DefaultTableCellRenderer();
        textLeft.setHorizontalAlignment(JLabel.LEFT);
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        jTable1.getColumnModel().getColumn(0).setCellRenderer(textLeft);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(textLeft);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(textLeft);

        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

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

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        this.renderizarConsecutivos();

    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        NovusUtils.beep();
    }// GEN-LAST:event_jTable1MouseClicked

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        NovusUtils.beep();
        this.cerrar();
    }// GEN-LAST:event_jLabel3MousePressed

    private void cerrar() {
        this.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel jclock;
    // End of variables declaration//GEN-END:variables

    public void renderizarConsecutivos() {

        SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
        jTable1.setAutoCreateRowSorter(true);
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
        JsonObject response = solicitarConsecutivos();

        if (response != null) {
            JsonArray data = response.get("data") != null && !response.get("data").isJsonNull() ? response.get("data").getAsJsonArray() : new JsonArray();
            for (JsonElement jElement : data) {
                try {
                    JsonObject jsonConsecutivo = jElement.getAsJsonObject();
                    ConsecutivoBean consecutivo = new ConsecutivoBean();
                    consecutivo.setId(jsonConsecutivo.get("id").getAsLong());
                    consecutivo.setTipo_documento(jsonConsecutivo.get("tipo_documento").getAsString().equals("031") ? ConsecutivoBean.TIPO_ELECTRONICA : ConsecutivoBean.TIPO_NORMAL);
                    consecutivo.setPrefijo(jsonConsecutivo.get("prefijo").getAsString());
                    consecutivo.setFecha_inicio(sdf2.parse(jsonConsecutivo.get("fecha_inicio").getAsString()));
                    consecutivo.setFecha_fin(sdf2.parse(jsonConsecutivo.get("fecha_fin").getAsString()));
                    consecutivo.setConsecutivo_inicial(jsonConsecutivo.get("consecutivo_inicial").getAsLong());
                    consecutivo.setConsecutivo_final(jsonConsecutivo.get("consecutivo_final").getAsLong());
                    consecutivo.setConsecutivo_actual(jsonConsecutivo.get("consecutivo_actual").getAsLong()-1);
                    consecutivo.setEstado(jsonConsecutivo.get("estado").getAsString());
                    consecutivo.setResolucion(jsonConsecutivo.get("resolucion").getAsString());
                    consecutivos.add(consecutivo);
                } catch (ParseException e) {
                    NovusUtils.printLn(e.getMessage());
                }
            }
            for (ConsecutivoBean consecutivo : consecutivos) {
                try {
                    defaultModel.addRow(new Object[]{
                        consecutivo.getPrefijo(),
                        sdf.format(consecutivo.getFecha_inicio()),
                        sdf.format(consecutivo.getFecha_fin()),
                        consecutivo.getConsecutivo_inicial() + "",
                        consecutivo.getConsecutivo_actual() + "",
                        consecutivo.getConsecutivo_final() + "",
                        consecutivo.getEstado(),
                        consecutivo.getTipo_documento() == ConsecutivoBean.TIPO_ELECTRONICA ? "ELECTRONICA" : "NORMAL"
                    });
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
            }
        }
    }

    JsonObject solicitarConsecutivos() {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync("CONSEGUIR CONSECUTIVOS", NovusConstante.SECURE_CENTRAL_POINT_CONSECUTIVOS_FACTURAS, NovusConstante.GET, null, true, false, header);
        try {
            client.start();
            client.join();
            response = client.getResponse();
        } catch (InterruptedException ex) {
            logger.log(Level.WARNING, "Interrupted!", ex);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            NovusUtils.printLn(ex.getLocalizedMessage());
        }
        return response;
    }
}

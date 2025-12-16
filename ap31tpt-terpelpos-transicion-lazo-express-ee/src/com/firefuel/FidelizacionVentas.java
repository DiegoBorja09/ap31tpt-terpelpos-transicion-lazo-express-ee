/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.tbltransaccionproceso.FinByTransaccionProcesoUseCase;
import com.bean.PersonaBean;
import com.bean.ReciboExtended;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.MediosPagosBean;
import com.neo.app.bean.Recibo;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.HierarchyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import com.application.useCases.jornada.GetJornadasByGroupUseCase;
import com.application.useCases.sutidores.FueFidelizadaUseCase;

public class FidelizacionVentas extends JDialog {

    GetJornadasByGroupUseCase getJornadasByGroupUseCase;
    FueFidelizadaUseCase fueFidelizadaUseCase;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    JFrame pedido;
    boolean activarAcumular;
    final int CONSULTAR_VENTAS_FECHAS = 1;
    String tipo = "";
    String cedula = "";

    ArrayList<ReciboExtended> lista;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;

    InfoViewController parent;
    ReciboExtended reciboFidelizar = null;

    MovimientosDao mdao = new MovimientosDao();

    Long id = 0L;

    public FidelizacionVentas(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        this.getJornadasByGroupUseCase = new GetJornadasByGroupUseCase();

        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(SwingConstants.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(SwingConstants.CENTER);

        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
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
        jComboPromotor.addActionListener((java.awt.event.ActionEvent evt) -> {
            setTimeout(1, () -> {
                refrescarDatos();
            });

        });
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });

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

        fueFidelizadaUseCase = new FueFidelizadaUseCase();

        buttonGroup1 = new javax.swing.ButtonGroup();
        panel_principal = new javax.swing.JPanel();
        panel_lista = new javax.swing.JPanel();
        jpromotor4 = new javax.swing.JLabel();
        jComboPromotor = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jclock = ClockViewController.getInstance();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        panel_principal.setLayout(new java.awt.CardLayout());

        panel_lista.setLayout(null);

        jpromotor4.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor4.setForeground(new java.awt.Color(153, 0, 0));
        jpromotor4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor4.setText("PROMOTOR");
        panel_lista.add(jpromotor4);
        jpromotor4.setBounds(30, 90, 370, 30);

        jComboPromotor.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jComboPromotor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0), new java.awt.Color(153, 0, 0)));
        jComboPromotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboPromotorActionPerformed(evt);
            }
        });
        panel_lista.add(jComboPromotor);
        jComboPromotor.setBounds(30, 120, 370, 50);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 51));
        jLabel4.setText("FACTURA");
        panel_lista.add(jLabel4);
        jLabel4.setBounds(150, 750, 750, 30);

        jLabel8.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("FACTURA");
        panel_lista.add(jLabel8);
        jLabel8.setBounds(150, 720, 460, 30);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        panel_lista.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        panel_lista.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor.setText("PROMOTOR");
        panel_lista.add(jpromotor);
        jpromotor.setBounds(820, 40, 460, 40);

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PREFIJO", "NRO", "FECHA", "PRODUCTO", "PROMOTOR", "CARA", "PLACA", "CANT", "VALOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(190);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(150);
        }

        panel_lista.add(jScrollPane2);
        jScrollPane2.setBounds(30, 180, 1220, 510);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setLayout(null);
        panel_lista.add(jclock);
        jclock.setBounds(1150, 720, 110, 60);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel6.setText("FIDELIZAR");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel6MouseReleased(evt);
            }
        });
        panel_lista.add(jLabel6);
        jLabel6.setBounds(610, 110, 180, 70);

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
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        panel_lista.add(jLabel1);
        jLabel1.setBounds(410, 110, 190, 70);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        panel_lista.add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        panel_lista.add(jpromotor1);
        jpromotor1.setBounds(820, 10, 460, 20);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        panel_lista.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("FIDELIZACION DE VENTAS");
        panel_lista.add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        panel_lista.add(jLabel30);
        jLabel30.setBounds(90, 10, 10, 68);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        panel_lista.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        panel_principal.add(panel_lista, "panel_lista");

        getContentPane().add(panel_principal);
        panel_principal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboPromotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboPromotorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboPromotorActionPerformed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1MousePressed

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MousePressed
        jLabel6.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-warning-smallb.png")));
    }// GEN-LAST:event_jLabel6MousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

     private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            NovusUtils.beep();
            id = (long) jTable1.getValueAt(r, 1);
            if (FinByTransaccionProcesoUseCase.isValidEdit(id)) {
                jLabel6.setText("EDITAR");
            } else {
                jLabel6.setText("FIDELIZAR");
            }
        }
    }// GEN-LAST:event_jTable1MouseClicked
    // private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
    //     int r = jTable1.getSelectedRow();
    //     if (r >= 0) {
    //         NovusUtils.beep();
    //         id = (long) jTable1.getValueAt(r, 1);
    //         if (mdao.isValidEdit(id)) {
    //             jLabel6.setText("EDITAR");
    //         } else {
    //             jLabel6.setText("FIDELIZAR");
    //         }
    //     }
    // }// GEN-LAST:event_jTable1MouseClicked

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        NovusUtils.beep();
        setTimeout(1, () -> {
            refrescarDatos();
        });

    }// GEN-LAST:event_jLabel1MouseReleased

     private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MouseReleased
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        if (NovusConstante.HAY_INTERNET) {
            fidelizar();
        } else {
            if (FinByTransaccionProcesoUseCase.isValidEdit(id)) {
                FidelizacionCliente fidel = new FidelizacionCliente(this, true, true, id);
                fidel.setVisible(true);
                setTimeout(1, () -> {
                    refrescarDatos();
                });
                jTable1.revalidate();
            } else {
                fidelizar();
            }
        }
    }
    // private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MouseReleased
    //     jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
    //     if (NovusConstante.HAY_INTERNET) {
    //         fidelizar();
    //     } else {
    //         if (mdao.isValidEdit(id)) {
    //             FidelizacionCliente fidel = new FidelizacionCliente(this, true, true, id);
    //             fidel.setVisible(true);
    //             setTimeout(1, () -> {
    //                 refrescarDatos();
    //             });
    //             jTable1.revalidate();
    //         } else {
    //             fidelizar();
    //         }
    //     }
    // }

    private void cerrar() {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<PersonaBean> jComboPromotor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitle;
    private ClockViewController jclock;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JLabel jpromotor4;
    private javax.swing.JPanel panel_lista;
    private javax.swing.JPanel panel_principal;
    // End of variables declaration//GEN-END:variables

    void cargarPromotores() {

        jComboPromotor.removeAllItems();

        PersonaBean p = new PersonaBean();
        p.setNombre("TODOS");
        p.setId(0);

        jComboPromotor.addItem(p);
        for (PersonaBean persona : InfoViewController.turnosPersonas) {
            jComboPromotor.addItem(persona);
            if (Main.persona.getId() == persona.getId()) {
                jComboPromotor.setSelectedItem(persona);
                setTimeout(1, () -> refrescarDatos());
            }
        }
    }

    public void refrescarDatos() {
        PersonaBean prom = (PersonaBean) jComboPromotor.getSelectedItem();
        if (prom != null) {
            PersonaBean promotor = null;
            if (prom.getId() != 0) {
                promotor = prom;
            }
            if (promotor != null) {
                Main.persona = promotor;
                jpromotor.setText(Main.persona.getNombre());
            } else {
                jpromotor.setText("TODOS");
            }
            lista.clear();
            solicitarVentasFidelizar(promotor);
            renderSales();
            jLabel6.setText("FIDELIZAR");
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

    public void solicitarVentasFidelizar(PersonaBean persona) {
        long promotorId = 0;
        if (persona != null && persona.getId() != 0) {
            promotorId = persona.getId();
        }
        JsonObject response = mdao.buscarVentasFidelizar(promotorId, getJornadasByGroupUseCase.execute());
        if (response != null) {
            buildSales(response);
        }
    }

    public void renderSales() {
        try {
            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();

            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
            int i = 0;
            for (Recibo recibo : lista) {
                try {
                    defaultModel.addRow(new Object[]{
                        (recibo.getAtributos() != null && recibo.getAtributos().get("consecutivo") != null
                        && !recibo.getAtributos().get("consecutivo").isJsonNull())
                        ? recibo.getAtributos().get("consecutivo").isJsonPrimitive()
                        ? recibo.getAtributos().get("consecutivo").getAsString()
                        : recibo.getAtributos().get("consecutivo").getAsJsonObject()
                        .get("prefijo").getAsString() + "-"
                        + recibo.getAtributos().get("consecutivo").getAsJsonObject()
                        .get("consecutivo_actual")
                        .getAsString()
                        : "",
                        recibo.getNumero(),
                        sdf2.format(recibo.getFecha()),
                        recibo.getProducto(),
                        recibo.getOperador(),
                        (Integer.parseInt(recibo.getCara()) > 0) ? (recibo.getCara() + "") : "",
                        recibo.getPlaca(),
                        String.format("%.3f", recibo.getCantidadFactor()) + " "
                        + (recibo.getAtributos() != null && !recibo.getAtributos().get("medida").getAsString().equals("UNDEFINED")
                        ? recibo.getAtributos().get("medida").getAsString().toUpperCase()
                        : "GL"),
                        "$ " + df.format(recibo.getTotal())});
                    i++;
                } catch (NumberFormatException e) {
                    Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            jTable1.setModel(defaultModel);
            jTable1.clearSelection();
        } catch (Exception ex) {
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buildSales(JsonObject response) {
        lista.clear();
        SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

        JsonArray array = response.get("data").getAsJsonArray();
        for (JsonElement jsonElement : array) {
            try {
                JsonObject json = jsonElement.getAsJsonObject();
                ReciboExtended rec = new ReciboExtended();

                rec.setNumero(json.get("numero").getAsLong());
                rec.setTotal(json.get("total").getAsLong());
                if (json.get("cantidad") != null && !json.get("cantidad").isJsonNull()) {
                    rec.setCantidadFactor(json.get("cantidad").getAsDouble());
                }

                rec.setProducto(json.get("producto").getAsString());

                rec.setOperador(json.get("operador").getAsString());
                rec.setIdentificacionPromotor(json.get("identificacionPromotor").getAsString());
                rec.setIdentificacionProducto(json.get("identificacionProducto").getAsLong());
                rec.setPrecio(json.get("precio").getAsLong());

                if (json.get("atributos") != null && !json.get("atributos").isJsonNull()) {
                    rec.setAtributos(json.get("atributos").getAsJsonObject());
                    rec.getAtributos().addProperty("medida", json.get("unidad_medida").getAsString());

                    rec.setCredito(rec.getAtributos().get("isCredito").getAsBoolean());
                }
                if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_odometro").isJsonNull()) {
                    rec.setOdometro(rec.getAtributos().get("vehiculo_odometro").getAsString());
                } else {
                    rec.setOdometro("");
                }
                if (rec.getAtributos() != null && !rec.getAtributos().get("orden").isJsonNull()) {
                    rec.setVoucher(rec.getAtributos().get("orden").getAsString());
                } else {
                    rec.setVoucher("");
                }
                if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_placa").isJsonNull()) {
                    rec.setPlaca(rec.getAtributos().get("vehiculo_placa").getAsString());
                } else {
                    rec.setPlaca("");
                }
                if (rec.getAtributos() != null && rec.getAtributos().get("consecutivo").isJsonNull()) {
                    rec.getAtributos().addProperty("consecutivo", json.get("consecutivo").getAsString());
                } else {
                    rec.getAtributos().addProperty("is_especial", !json.get("tipo").getAsString().equals("017"));
                }
                rec.getAtributos().addProperty("tipo", json.get("tipo").getAsString());

                JsonArray medios = json.get("medios_pagos").getAsJsonArray();

                rec.setMediosPagos(new ArrayList<>());
                for (JsonElement medio : medios) {
                    JsonObject med = medio.getAsJsonObject();

                    MediosPagosBean m = new MediosPagosBean();
                    m.setId(med.get("medio").getAsLong());
                    m.setDescripcion(med.get("medioDescripcion").getAsString());
                    m.setValor(med.get("valorPago").getAsFloat());

                    rec.getMediosPagos().add(m);
                }

                rec.setCara(json.get("cara").getAsString());
                rec.setManguera(json.get("manguera").getAsString());
                Date date = sdfSQL.parse(json.get("fecha").getAsString());
                rec.setFecha(date);
                lista.add(rec);
            } catch (ParseException ex) {
                Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void fidelizar() {
        selecionar();
        boolean fidelizada = fueFidelizadaUseCase.execute(reciboFidelizar.getNumero());
        if (!fidelizada) {
            Date fechaRecibo = reciboFidelizar.getFecha();
            Date fechaActual = new Date();
            long millisegundosTranscurridos = fechaActual.getTime() - fechaRecibo.getTime();
            if (millisegundosTranscurridos > 180000) {
                mostrarPanelMensaje("NO SE PUEDE FIDELIZAR UNA VENTA HECHA HACE MAS DE 3 MINUTOS",
                        "/com/firefuel/resources/btBad.png", LetterCase.FIRST_UPPER_CASE);
            } else {
                FidelizacionCliente fidel = new FidelizacionCliente(this.parent, true, reciboFidelizar, false);
                fidel.setVisible(true);
                setTimeout(1, () -> {
                    refrescarDatos();
                });
                jTable1.revalidate();
            }
        } else {
            mostrarPanelMensaje("ESTA VENTA YA HA SIDO FIDELIZADA",
                    "/com/firefuel/resources/btBad.png", LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void selecionar() {
        int r = jTable1.getSelectedRow();
        if (r >= 0) {
            NovusUtils.beep();
            long value = (long) jTable1.getValueAt(r, 1);
            for (ReciboExtended recibo : lista) {
                if (recibo.getNumero() == value) {
                    reciboFidelizar = recibo;
                }
            }
        } else {
            reciboFidelizar = null;
            jLabel8.setText("");
            jLabel4.setText("");
        }
    }

    public void mostrarPanelMensaje(String mensaje, String icono, String letterCase) {
        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btOk.png";
        }
        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(true).setRunnable(runnable)
                .setLetterCase(letterCase).build();
        panel_principal.add("card_mensaje_ext", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
        CardLayout panel = (CardLayout) panel_principal.getLayout();
        panel.show(panel_principal, "card_mensaje_ext");
        Async(runnable, 3);
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) panel_principal.getLayout();
        panel.show(panel_principal, "panel_lista");
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep((long) tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(FidelizacionVentas.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    class AsyncTask extends Thread {

        FidelizacionVentas vista;
        int tipoAccion;

        public AsyncTask(FidelizacionVentas vista, int tipoAccion) {
            this.vista = vista;
            this.tipoAccion = tipoAccion;
        }

        void consultarVentasPorFecha(Date fechaInial, Date fechaFinal) {
            SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
            SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
            JsonObject jsonBody = new JsonObject();
            TreeMap<String, String> header = new TreeMap<>();
            if (fechaInial == null) {
                fechaInial = new Date();
            }
            if (fechaFinal == null) {
                fechaFinal = new Date();
            }
            jsonBody.addProperty("fechaInicio", sdf.format(fechaInial) + " 00:00:00");
            jsonBody.addProperty("fechaFin", sdf.format(fechaFinal) + " 23:00:00");
            header.put("Content-type", "application/json");
            ClientWSAsync client = new ClientWSAsync("CONSEGUIR VENTAS", NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_VENTAS_CONSULTA, NovusConstante.POST, jsonBody, true, false,
                    header);
            JsonObject response = null;
            try {
                response = client.esperaRespuesta();
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }
            try {
                if (response != null) {
                    lista.clear();
                    JsonArray array = response.get("data").getAsJsonArray();
                    for (JsonElement jsonElement : array) {
                        try {
                            JsonObject json = jsonElement.getAsJsonObject();
                            ReciboExtended rec = new ReciboExtended();
                            rec.setNumero(json.get("numero").getAsLong());
                            rec.setTotal(json.get("total").getAsLong());
                            if (json.get("cantidad") != null && !json.get("cantidad").isJsonNull()) {
                                rec.setCantidadFactor(json.get("cantidad").getAsDouble());
                            }
                            rec.setProducto(json.get("producto").getAsString());

                            rec.setOperador(json.get("operador").getAsString());
                            rec.setIdentificacionPromotor(json.get("identificacionPromotor").getAsString());
                            rec.setIdentificacionProducto(json.get("identificacionProducto").getAsLong());

                            if (json.get("atributos") != null && !json.get("atributos").isJsonNull()) {
                                rec.setAtributos(json.get("atributos").getAsJsonObject());
                            }
                            if (rec.getAtributos() != null
                                    && !rec.getAtributos().get("vehiculo_odometro").isJsonNull()) {
                                rec.setOdometro(rec.getAtributos().get("vehiculo_odometro").getAsString());
                            } else {
                                rec.setOdometro("");
                            }
                            if (rec.getAtributos() != null && !rec.getAtributos().get("voucher").isJsonNull()) {
                                rec.setVoucher(rec.getAtributos().get("voucher").getAsString());
                            } else {
                                rec.setVoucher("");
                            }
                            if (rec.getAtributos() != null && !rec.getAtributos().get("vehiculo_placa").isJsonNull()) {
                                rec.setPlaca(rec.getAtributos().get("vehiculo_placa").getAsString());
                            } else {
                                rec.setPlaca("");
                            }

                            rec.setCara(json.get("cara").getAsString());
                            rec.setManguera(json.get("manguera").getAsString());
                            Date date = sdfSQL.parse(json.get("fecha").getAsString());
                            rec.setFecha(date);
                            lista.add(rec);
                        } catch (ParseException a) {
                            NovusUtils.printLn(a.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            switch (this.tipoAccion) {
                case CONSULTAR_VENTAS_FECHAS:
                    consultarVentasPorFecha(null, null);
            }
        }
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

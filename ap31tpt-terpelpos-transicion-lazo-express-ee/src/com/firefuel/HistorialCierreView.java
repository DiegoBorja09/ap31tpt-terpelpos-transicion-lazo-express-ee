/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.application.useCases.jornada.GetJornadaIdByPersonaUseCase;
import com.application.useCases.persons.GetAllJornadasHistoryUseCase;
import com.application.useCases.persons.GetAllJornadasUseCase;
import com.bean.PersonaBean;
import com.bean.ReporteJornadaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.neo.print.services.PrinterFacade;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author novus
 */
public class HistorialCierreView extends javax.swing.JFrame {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    InfoViewController pedido;
    boolean activarAcumular;

    String tipo = "";
    String cedula = "";

    ArrayList<PersonaBean> personas;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;

    public HistorialCierreView(InfoViewController pedido) {

        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);

        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        this.pedido = pedido;
        initComponents();
        personas = new ArrayList<>();

        listapersonal();
        mostarTurnos();

    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setAlwaysOnTop(true);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel2.setBackground(new java.awt.Color(255, 51, 51));
        jLabel2.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("    JORNADAS REALIZADAS");
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 120, 990, 40);

        jTable1.setFont(new java.awt.Font("Roboto", 0, 20)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {

                },
                new String[] {
                        "ITEM", "FECHA INICIO", "FECHA FIN", "PROMOTOR", "VENTAS", "TOTAL", "GRUPO"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setRowHeight(35);
        jTable1.setShowGrid(true);
        jTable1.setShowVerticalLines(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(20, 160, 990, 380);

        jLabel1.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel1.setText("ACTUALIZAR");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        getContentPane().add(jLabel1);
        jLabel1.setBounds(330, 60, 170, 50);

        jLabel6.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel6.setText("SALIR");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel6MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel6);
        jLabel6.setBounds(830, 60, 180, 50);

        jLabel9.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel9.setText("IMPRIMIR");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel9MousePressed(evt);
            }
        });
        getContentPane().add(jLabel9);
        jLabel9.setBounds(510, 60, 170, 50);

        jComboBox1.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        getContentPane().add(jComboBox1);
        jComboBox1.setBounds(20, 60, 300, 50);

        jLabel3.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 153));
        jLabel3.setText("PROMOTOR EN TURNO:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(140, 10, 240, 50);

        jLabel5.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
        });
        getContentPane().add(jLabel5);
        jLabel5.setBounds(0, 0, 1030, 600);

        setSize(new java.awt.Dimension(1024, 600));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel5MousePressed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        listapersonal();
    }// GEN-LAST:event_jLabel1MousePressed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseReleased

    }// GEN-LAST:event_jTable1MouseReleased

    private void jLabel9MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel9MousePressed
        imprimirReporteVenta();
    }// GEN-LAST:event_jLabel9MousePressed

    private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MouseReleased
        ocultarme();
    }// GEN-LAST:event_jLabel6MouseReleased

    private void ocultarme() {
        this.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    private void listapersonal() {

        try {
            jComboBox1.removeAll();
            jComboBox1.addItem("TODOS");
            GetAllJornadasUseCase getAllJornadasUseCase = new GetAllJornadasUseCase();
            //PersonasDao dao = new PersonasDao();
            //ArrayList<PersonaBean> personasList = dao.getAllJornadas();
            ArrayList<PersonaBean> personasList = getAllJornadasUseCase.execute();
            for (PersonaBean persona : personasList) {
                jComboBox1.addItem(persona.getNombre() + " " + persona.getApellidos());
            }
            jComboBox1.setSelectedIndex(0);
        } catch (Exception e) {
            Logger.getLogger(HistorialCierreView.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    private void mostarTurnos() {
        try {
            jTable1.setAutoCreateRowSorter(true);

            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();

            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();

            //PersonasDao dao = new PersonasDao();
            //personas = dao.getAllJornadasHistory();
            GetAllJornadasHistoryUseCase getAllJornadasHistoryUseCase = new GetAllJornadasHistoryUseCase();
            personas = getAllJornadasHistoryUseCase.execute();

            for (PersonaBean list : personas) {
                defaultModel.addRow(new Object[] {
                        list.getJornadaId(),
                        sdf.format(list.getFechaInicio()),
                        sdf.format(list.getFechaFin()),
                        list.getNombre(),
                        list.getAtributos().get("cantidad").getAsInt(),
                        "$ " + df.format(list.getAtributos().get("total").getAsFloat()),
                        list.getGrupoJornadaId()
                });
            }

            jTable1.getColumnModel().getColumn(1).setCellRenderer(textCenter);
            jTable1.getColumnModel().getColumn(2).setCellRenderer(textCenter);
            jTable1.getColumnModel().getColumn(5).setCellRenderer(textRight);
        //} catch (DAOException ex) {
        } catch (Exception ex) {
            NovusUtils.printLn("❌ Error al obtener getAllJornadasHistoryUseCase: " + ex.getMessage());
            Logger.getLogger(HistorialCierreView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void imprimirReporteVenta() {

        try {

            int r = jTable1.getSelectedRow();
            int c = jTable1.getSelectedColumn();
            long value = (long) jTable1.getValueAt(r, 0);

            for (PersonaBean persona : personas) {

                if (value == persona.getJornadaId()) {
                    boolean CORTAR = true;
                    MovimientosDao dao = new MovimientosDao();

                    ReporteJornadaBean reporte = new ReporteJornadaBean();
                    if (reporte != null) {
                        try {
                            PrinterFacade pf = new PrinterFacade();
                            pf.printArqueoEstadoVentas("CONSULTA DE CIERRE DE JORNADA", persona, reporte, CORTAR);
                        } catch (Exception a) {
                            NovusUtils.printLn("imprimirReporteVenta > Exception");
                        }
                    }

                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(HistorialCierreView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

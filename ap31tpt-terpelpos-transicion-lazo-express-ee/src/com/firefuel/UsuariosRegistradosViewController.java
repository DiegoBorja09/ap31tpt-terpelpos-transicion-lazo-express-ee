package com.firefuel;

import com.application.useCases.persons.GetAllJornadasHistoryUseCase;
import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.PersonasDao;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class UsuariosRegistradosViewController extends javax.swing.JDialog {

    JFrame parent;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    DefaultTableCellRenderer textLeft;
    ArrayList<PersonaBean> personas;
    ArrayList<PersonaBean> personasCore;

    public UsuariosRegistradosViewController(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        textLeft = new DefaultTableCellRenderer();
        textLeft.setHorizontalAlignment(JLabel.LEFT);
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        personasCore = new ArrayList<>();

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
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        solicitarUsuarios();
        renderizarUsuarios();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel32 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel32.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jLabel32.setText("ASIGNAR IBUTTON");
        jLabel32.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel32MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel32MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel32);
        jLabel32.setBounds(820, 625, 180, 60);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel30.setText("ACTUALIZAR");
        jLabel30.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel30MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel30MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel30);
        jLabel30.setBounds(620, 625, 180, 60);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

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
                "IDENTIFICACION", "NOMBRE", "ESTADO", "MEDIO ASIGNADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(320);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
        }

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(100, 120, 1090, 490);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("USUARIOS REGISTRADOS");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(100, 0, 720, 90);

        jLabel26.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jLabel26.setText("ASIGNAR TAG");
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel26MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel26MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel26);
        jLabel26.setBounds(1010, 625, 180, 60);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 85, 1280, 618);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel26MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel26MousePressed

    private void jLabel26MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseReleased
        selectme();
    }//GEN-LAST:event_jLabel26MouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        select();
    }//GEN-LAST:event_jTable1MouseClicked

    private void jLabel30MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel30MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel30MousePressed

    private void jLabel30MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel30MouseReleased
        solicitarUsuarios();
        renderizarUsuarios();
    }//GEN-LAST:event_jLabel30MouseReleased

    private void jLabel32MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel32MousePressed

    private void jLabel32MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MouseReleased
        selectmeIbutton();
    }//GEN-LAST:event_jLabel32MouseReleased

    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MousePressed
        cerrar();
    }// GEN-LAST:event_jLabel3MousePressed

    private void cerrar() {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    public void solicitarUsuarios() {
        try {
            personasCore.clear();
            //PersonasDao dao = new PersonasDao();
            //personasCore = dao.getAllUsuariosCore();

            GetAllJornadasHistoryUseCase getAllJornadasHistoryUseCase = new GetAllJornadasHistoryUseCase();
            personasCore = getAllJornadasHistoryUseCase.execute();

        //} catch (DAOException ex) {
        } catch (Exception ex) {
            NovusUtils.printLn("❌ Error al obtener getAllJornadasHistoryUseCase: " + ex.getMessage());
            Logger.getLogger(UsuariosRegistradosViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void select() {
        int r = jTable1.getSelectedRow();
        String tag = (String) jTable1.getValueAt(r, 3);
        tag = tag.trim();
        if (r >= 0) {
            jLabel26.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
            jLabel32.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
        } else {
            jLabel26.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
            jLabel32.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
            if (tag.contains("ASIGNADO")) {
                jLabel26.setText("ASIGNAR");
                jLabel32.setText("ASIGNAR");
            } else {
                jLabel26.setText("REASIGNAR");
                jLabel32.setText("REASIGNAR");
            }
        }
    }

    private void selectmeIbutton() {
        int r = jTable1.getSelectedRow();
        NovusUtils.beep();
        if (r > -1) {
            String identificacion = (String) jTable1.getValueAt(r, 0);

            for (PersonaBean persona : personasCore) {
                if (persona.getIdentificacion().trim().equals(identificacion.trim())) {

                    RegistroIButtonViewController viewTag = RegistroIButtonViewController.getInstance((InfoViewController) this.parent, true, this);
                    viewTag.setPersona(persona);
                    viewTag.setVisible(true);

                    break;
                }
            }
        } else {

        }
    }

    private void selectme() {
        int r = jTable1.getSelectedRow();
        NovusUtils.beep();
        if (r > -1) {
            String identificacion = (String) jTable1.getValueAt(r, 0);

            for (PersonaBean persona : personasCore) {
                if (persona.getIdentificacion().trim().equals(identificacion.trim())) {

                    InfoViewController.instanciaRegistroTag = RegistroTagViewController.getInstance((InfoViewController) this.parent, true, this);
                    InfoViewController.instanciaRegistroTag.setPersona(persona);
                    InfoViewController.instanciaRegistroTag.setVisible(true);

                    break;
                }
            }
        }
    }

    public void renderizarUsuarios() {

        TreeMap<String, PersonaBean> promotores = new TreeMap<>();

        jTable1.setAutoCreateRowSorter(true);
        DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();

        for (PersonaBean list : personasCore) {
            if (!promotores.containsKey(list.getIdentificacion())) {
                promotores.put(list.getIdentificacion(), list);
            }
        }
        for (Map.Entry<String, PersonaBean> entry : promotores.entrySet()) {
            Object key = entry.getKey();
            PersonaBean value = entry.getValue();
            if (value.getId() > 3) {
                defaultModel.addRow(new Object[]{
                    value.getIdentificacion(),
                    value.getNombre(),
                    value.getEstado().equals("A") ? "ACTIVO" : "INACTIVO",
                    value.getTag() == null || value.getTag().trim().equals("") ? "NO ASIGNADO" : value.getTag()
                });
            }
        }

    }
}

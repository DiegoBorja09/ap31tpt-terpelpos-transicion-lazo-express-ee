package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import teclado.view.common.TecladoNumerico;

public class TipoVentaViewController extends javax.swing.JDialog {

    public String view = null;
    public long tipoventa = -1;
    public long tipoCantidad = -1;
    public long tipoValor = 1;
    public final int PRESET_TIPO_VALOR = 2;
    public final int PRESET_TIPO_VOLUMEN = 1;

    InfoViewController parent;
    PersonaBean persona;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    TreeMap<Long, Surtidor> caras = new TreeMap<>();

    public TipoVentaViewController(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jcombo_manguera = new javax.swing.JComboBox<>();
        jcombo_producto = new javax.swing.JComboBox<>();
        jcombo_cara = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcantidad = new javax.swing.JTextField();
        jPanel1 = new TecladoNumerico()
        ;
        jcombo_surtidor = new javax.swing.JComboBox<>();
        jNotificacion_tipoVenta = new javax.swing.JLabel();
        jtypeTitle = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel4.setLayout(null);

        buttonGroup1.add(jToggleButton1);
        jToggleButton1.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jToggleButton1.setSelected(true);
        jToggleButton1.setText("VALOR");
        jToggleButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jToggleButton1);
        jToggleButton1.setBounds(160, 490, 140, 50);

        buttonGroup1.add(jToggleButton2);
        jToggleButton2.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jToggleButton2.setText("VOLUMEN");
        jToggleButton2.setBorder(null);
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jToggleButton2);
        jToggleButton2.setBounds(380, 490, 150, 50);

        jLabel13.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(186, 12, 47));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("MANGUERA");
        jPanel4.add(jLabel13);
        jLabel13.setBounds(480, 180, 160, 30);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(186, 12, 47));
        jLabel12.setText("PRODUCTO:");
        jPanel4.add(jLabel12);
        jLabel12.setBounds(50, 310, 160, 30);

        jLabel10.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(186, 12, 47));
        jLabel10.setText("SURTIDOR");
        jPanel4.add(jLabel10);
        jLabel10.setBounds(50, 180, 130, 30);

        jLabel11.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(186, 12, 47));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("CARA");
        jPanel4.add(jLabel11);
        jLabel11.setBounds(290, 180, 90, 30);

        jcombo_manguera.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jcombo_manguera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcombo_mangueraActionPerformed(evt);
            }
        });
        jPanel4.add(jcombo_manguera);
        jcombo_manguera.setBounds(550, 220, 90, 40);

        jcombo_producto.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jcombo_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcombo_productoActionPerformed(evt);
            }
        });
        jPanel4.add(jcombo_producto);
        jcombo_producto.setBounds(50, 350, 590, 50);

        jcombo_cara.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jcombo_cara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcombo_caraActionPerformed(evt);
            }
        });
        jPanel4.add(jcombo_cara);
        jcombo_cara.setBounds(290, 220, 90, 40);

        jLabel9.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(186, 12, 47));
        jLabel9.setText("CANTIDAD:");
        jPanel4.add(jLabel9);
        jLabel9.setBounds(50, 580, 150, 40);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        jPanel4.add(jLabel2);
        jLabel2.setBounds(0, 0, 80, 90);

        jcantidad.setBackground(new java.awt.Color(186, 12, 47));
        jcantidad.setFont(new java.awt.Font("Roboto", 0, 28)); // NOI18N
        jcantidad.setForeground(new java.awt.Color(255, 255, 255));
        jcantidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jcantidad.setBorder(null);
        jcantidad.setCaretColor(new java.awt.Color(255, 255, 0));
        jcantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jcantidadFocusGained(evt);
            }
        });
        jcantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcantidadMouseReleased(evt);
            }
        });
        jcantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcantidadActionPerformed(evt);
            }
        });
        jcantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcantidadKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jcantidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jcantidadKeyTyped(evt);
            }
        });
        jPanel4.add(jcantidad);
        jcantidad.setBounds(220, 570, 420, 60);
        jPanel4.add(jPanel1);
        jPanel1.setBounds(700, 170, 550, 470);

        jcombo_surtidor.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jcombo_surtidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcombo_surtidorActionPerformed(evt);
            }
        });
        jPanel4.add(jcombo_surtidor);
        jcombo_surtidor.setBounds(50, 220, 110, 40);

        jNotificacion_tipoVenta.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion_tipoVenta.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jNotificacion_tipoVenta);
        jNotificacion_tipoVenta.setBounds(100, 720, 1020, 70);

        jtypeTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jtypeTitle.setForeground(new java.awt.Color(255, 255, 255));
        jtypeTitle.setText("{{TIPO VENTA}}");
        jPanel4.add(jtypeTitle);
        jtypeTitle.setBounds(120, 0, 520, 80);

        jLabel8.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setText("FIJAR POR:");
        jPanel4.add(jLabel8);
        jLabel8.setBounds(50, 440, 160, 30);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alerta.png"))); // NOI18N
        jPanel4.add(jLabel3);
        jLabel3.setBounds(0, 700, 80, 100);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel4.add(jLabel28);
        jLabel28.setBounds(80, 713, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel4.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jNotificacion);
        jNotificacion.setBounds(670, 10, 590, 60);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel4.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndVentasPredeterminadas.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel4.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel4, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jcantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jcantidadFocusGained
        NovusUtils.deshabilitarCopiarPegar(jcantidad);
    }//GEN-LAST:event_jcantidadFocusGained

    private void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jPanel1.setVisible(false);

        this.mostrarTeclado();

        this.solicitarDatosSurtidor();

        this.cambiarTipoCantidad(PRESET_TIPO_VALOR);

        jcantidad.requestFocus();

    }

    private void jcombo_mangueraActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_mangueraActionPerformed
        cargarDatosProductos();
    }// GEN-LAST:event_jcombo_mangueraActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton1ActionPerformed
        NovusUtils.beep();
        cambiarTipoCantidad(PRESET_TIPO_VALOR);
        tipoValor = 1;
    }// GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton2ActionPerformed
        NovusUtils.beep();
        cambiarTipoCantidad(PRESET_TIPO_VOLUMEN);
        tipoValor = 2;
    }// GEN-LAST:event_jToggleButton2ActionPerformed

    private void jcantidadKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyPressed
    }// GEN-LAST:event_jcantidadKeyPressed

    private void jcantidadKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyReleased
    }// GEN-LAST:event_jcantidadKeyReleased

    private void jcantidadKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyTyped
//        fix(evt);
        if (tipoValor == 1) {
            String caracteresAceptados = "[0-9.]";
            NovusUtils.limitarCarateres(evt, jcantidad, 7, jNotificacion, caracteresAceptados);
        } else {
            String caracteresAceptados = "[0-9.]";
            NovusUtils.limitarCarateres(evt, jcantidad, 4, jNotificacion, caracteresAceptados);
        }

    }// GEN-LAST:event_jcantidadKeyTyped

    private void jcombo_productoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_productoActionPerformed
        jcantidad.requestFocus();
    }// GEN-LAST:event_jcombo_productoActionPerformed

    private void jcombo_tipoCantidadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_tipoCantidadActionPerformed

    }// GEN-LAST:event_jcombo_tipoCantidadActionPerformed

    private void jcantidadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcantidadActionPerformed
        guardar();
    }// GEN-LAST:event_jcantidadActionPerformed

    private void jcantidadMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jcantidadMouseReleased
        mostrarTeclado();
    }// GEN-LAST:event_jcantidadMouseReleased

    private void jcombo_surtidorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_surtidorActionPerformed
        cargarDatosCaras();
    }// GEN-LAST:event_jcombo_surtidorActionPerformed

    private void jcombo_caraActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_caraActionPerformed
        cargarDatosMangueras();

    }// GEN-LAST:event_jcombo_caraActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed

    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion_tipoVenta;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JToggleButton jToggleButton1;
    public javax.swing.JToggleButton jToggleButton2;
    public static javax.swing.JTextField jcantidad;
    private javax.swing.JComboBox<String> jcombo_cara;
    private javax.swing.JComboBox<String> jcombo_manguera;
    private javax.swing.JComboBox<String> jcombo_producto;
    private javax.swing.JComboBox<String> jcombo_surtidor;
    private javax.swing.JLabel jtypeTitle;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    private void fix(KeyEvent evt) {
        String text = jcantidad.getText().trim();
        if (tipoCantidad == PRESET_TIPO_VALOR) {
            if (text.length() > 7) {
                evt.consume();
            }
        } else {
            if (text.length() > 3) {
                evt.consume();
            }
        }
    }

    public void setView(String view) {
        switch (view) {
            case "VENTAS PREDETERMINADAS":
                this.tipoventa = 1;
                break;
            case "CALIBRACIONES":
                this.tipoventa = 2;
                break;
            case "CONSUMO PROPIO":
                this.tipoventa = 3;
                break;
            case "CAMBIO DE PRECIO":
                this.tipoventa = 4;
                jToggleButton2.setEnabled(true);
                break;
        }
        this.view = view;
        recargarVista();
    }

    private void cerrar() {
        dispose();
    }

    void solicitarDatosSurtidor() {
        SetupDao sdao = new SetupDao();
        caras = sdao.getCaras();
        cargarDatosSurtidores();
    }

    private void mostrarTeclado() {
        jPanel1.setVisible(true);
    }

    private void recargarVista() {
        jtypeTitle.setText(this.view);
    }

    private void cargarDatosSurtidores() {
        jcombo_surtidor.removeAllItems();
        TreeMap<Integer, String> temp = new TreeMap<>();
        for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
            if (!temp.containsKey(surdetalle.getValue().getSurtidor())) {
                jcombo_surtidor.addItem(surdetalle.getValue().getSurtidor() + "");
                temp.put(surdetalle.getValue().getSurtidor(), surdetalle.getValue().getSurtidor() + "");
            }
        }

        NovusUtils.printLn("Surtidor " + temp.entrySet().toString());
        cargarDatosCaras();

    }

    private void cargarDatosCaras() {
        jcombo_cara.removeAllItems();
        if (jcombo_surtidor.getSelectedIndex() > -1) {
            long surtidorSeleccionado = Long.parseLong(jcombo_surtidor.getSelectedItem().toString().trim());
            TreeMap<Integer, String> temp = new TreeMap<>();
            for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
                if (surdetalle.getValue().getSurtidor() == surtidorSeleccionado) {
                    if (!temp.containsKey(surdetalle.getValue().getCara())) {
                        jcombo_cara.addItem(surdetalle.getValue().getCara() + "");
                        temp.put(surdetalle.getValue().getCara(), surdetalle.getValue().getCara() + "");
                    }
                }
            }
            NovusUtils.printLn("caras " + temp.entrySet().toString());
        }

        cargarDatosMangueras();
    }

    private void cargarDatosMangueras() {
        jcombo_manguera.removeAllItems();
        TreeMap<Integer, String> temp = new TreeMap<>();
        if (jcombo_cara.getSelectedIndex() > -1) {
            long surtidorSeleccionado = Long.parseLong(jcombo_surtidor.getSelectedItem().toString().trim());
            long caraSeleccionada = Long.parseLong(jcombo_cara.getSelectedItem().toString().trim());
            for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
                if (surdetalle.getValue().getSurtidor() == surtidorSeleccionado
                        && surdetalle.getValue().getCara() == caraSeleccionada) {
                    if (!temp.containsKey(surdetalle.getValue().getManguera())) {
                        NovusUtils.printLn("MANGUERA CARGADO:" + surdetalle.getValue().getManguera());
                        jcombo_manguera.addItem(surdetalle.getValue().getManguera() + "");
                        temp.put((int) surdetalle.getValue().getManguera(),
                                surdetalle.getValue().getManguera() + "");
                    }
                }
            }
            NovusUtils.printLn("Mangueras " + temp.entrySet().toString());
        }
        cargarDatosProductos();
    }

    private void cargarDatosProductos() {

        jcombo_producto.removeAllItems();
        TreeMap<Long, String> temp = new TreeMap<>();
        if (jcombo_manguera.getSelectedIndex() > -1) {
            long surtidorSeleccionado = Long.parseLong(jcombo_surtidor.getSelectedItem().toString().trim());
            long caraSeleccionada = Long.parseLong(jcombo_cara.getSelectedItem().toString().trim());
            long mangueraSeleccionada = Long.parseLong(jcombo_manguera.getSelectedItem().toString().trim());
            for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
                if (surdetalle.getValue().getSurtidor() == surtidorSeleccionado
                        && surdetalle.getValue().getCara() == caraSeleccionada
                        && surdetalle.getValue().getManguera() == mangueraSeleccionada) {
                    if (!temp.containsKey(surdetalle.getValue().getProductoIdentificador())) {
                        jcombo_producto.addItem(surdetalle.getValue().getProductoDescripcion());
                        temp.put(surdetalle.getValue().getProductoIdentificador(),
                                surdetalle.getValue().getProductoDescripcion());
                    }
                }
            }

        }
        jcantidad.requestFocus();
    }

    private void guardar() {
        String cantidad = jcantidad.getText().trim();

        if (cantidad.equals("") || cantidad.equals("0")) {
            showMessage("DATOS INVALIDOS", "/com/firefuel/resources/btBad.png",
                    true, this::cambiarPanelHome, 
                    true, LetterCase.FIRST_UPPER_CASE);
            jcantidad.requestFocus();
        } else {
            JsonObject response;
            response = guardarTipoVenta();
            if (response != null) {
                showMessage("SE HA REGISTRADO " + this.view,
                        "/com/firefuel/resources/btOk.png", true, this::cerrar, 
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                showMessage("HA OCURRIDO UN ERROR AL REGISTRAR " + this.view, 
                        "/com/firefuel/resources/btBad.png", true, this::cambiarPanelHome,
                        true, LetterCase.FIRST_UPPER_CASE);
            }

        }
    }

    private void cambiarTipoCantidad(long tipo) {
        this.tipoCantidad = tipo;
        if (tipoCantidad == 1) {
            jLabel9.setText("VOLUMEN:");
        } else {
            jLabel9.setText("VALOR:");
        }
        jcantidad.setText("");
        jcantidad.requestFocus();
    }

    private JsonObject guardarTipoVenta() {
        int caraSeleccionada = Integer.parseInt(jcombo_cara.getSelectedItem().toString().trim());
        int surtidorSeleccionado = Integer.parseInt(jcombo_surtidor.getSelectedItem().toString().trim());
        int mangueraSeleccionada = Integer.parseInt(jcombo_manguera.getSelectedItem().toString().trim());
        long familiaId = -1;
        for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
            if (surdetalle.getValue().getSurtidor() == surtidorSeleccionado
                    && surdetalle.getValue().getCara() == caraSeleccionada) {
                String productoSeleccionado = jcombo_producto.getSelectedItem().toString().trim();
                if (productoSeleccionado.equalsIgnoreCase(surdetalle.getValue().getProductoDescripcion().trim())) {
                    familiaId = surdetalle.getValue().getFamiliaIdentificador();
                    break;
                }
            }
        }

        int recibido = Integer.parseInt(jcantidad.getText());
        JsonObject response = null;
        JsonObject json = new JsonObject();
        String url;
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("uuid", NovusConstante.UUID);
        header.put("fecha", sdf.format(new Date()) + "-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadordispositivo", "localhost");
        if (this.tipoventa != 4) {
            try {
                json.addProperty("numeroCara", caraSeleccionada);
                json.addProperty("identificadorPromotor", Main.persona != null ? Main.persona.getId() : 0);
                json.addProperty("identificadorFamiliaProducto", familiaId);
                json.addProperty("monto", this.tipoCantidad == PRESET_TIPO_VALOR ? recibido : 0);
                json.addProperty("volumen", this.tipoCantidad == PRESET_TIPO_VOLUMEN ? recibido : 0);
                json.addProperty("tipoVenta", this.tipoventa);

                SurtidorDao dao = new SurtidorDao();

                Surtidor mang = new Surtidor();
                mang.setSurtidor(surtidorSeleccionado);
                mang.setCara(caraSeleccionada);
                mang.setManguera(mangueraSeleccionada);
                mang.setGrado(dao.getGradoPorManguera(mangueraSeleccionada));

                dao.crearAutorizacionTipoVenta(null, mang, json, json.get("monto").getAsInt(), json.get("volumen").getAsInt());

                response = new JsonObject();
                response.addProperty("success", true);

            } catch (DAOException ex) {
                Logger.getLogger(TipoVentaViewController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            url = "http://localhost:8000/api/multicambioprecio";
            json.addProperty("identificadorProceso", "9480b642-7727-4b83-bf18-a1288b0dac002");
            json.addProperty("listaPrecio", 1);
            json.addProperty("cantdigitos", 6);
            json.addProperty("surtidor", surtidorSeleccionado);
            JsonArray data = new JsonArray();
            JsonArray precioArray = new JsonArray();
            JsonObject jsonData = new JsonObject();
            JsonObject jsonPrecio = new JsonObject();
            jsonPrecio.addProperty("manguera", mangueraSeleccionada);
            jsonPrecio.addProperty("precioUnidad", recibido);
            precioArray.add(jsonPrecio);
            jsonData.addProperty("cara", caraSeleccionada);
            jsonData.add("precios", precioArray);
            data.add(jsonData);
            json.add("data", data);

            NovusUtils.printLn(json.toString());
            ClientWSAsync async = new ClientWSAsync("GUARDAR TIPO VENTA", url,
                    NovusConstante.POST, json, true, false, header);
            try {
                response = async.esperaRespuesta();
            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }

        }

        return response;
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

    private void showMessage(String msj, String ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnl_principal");
    }

}

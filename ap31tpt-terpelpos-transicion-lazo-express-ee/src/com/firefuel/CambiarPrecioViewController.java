package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.SetupDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import teclado.view.common.TecladoNumerico;

public class CambiarPrecioViewController extends javax.swing.JDialog {

    public String view = null;
    public long tipoventa = -1;
    public long tipoCantidad = -1;

    public final int PRESET_TIPO_VALOR = 2;
    public final int PRESET_TIPO_VOLUMEN = 1;

    InfoViewController parent;
    PersonaBean persona;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    TreeMap<Long, Surtidor> caras = new TreeMap<>();
    TreeMap<Long, Surtidor> mangueras = new TreeMap<>();

    public CambiarPrecioViewController(InfoViewController parent, boolean modal) {
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
        jPanel3 = new javax.swing.JPanel();
        jLCargando = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jcombo_producto = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcantidad = new javax.swing.JTextField();
        jPanel1 = new TecladoNumerico()
        ;
        jLValor = new javax.swing.JLabel();
        jtypeTitle = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel3.setLayout(null);

        jLCargando.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLCargando.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jLCargando);
        jLCargando.setBounds(100, 710, 1020, 80);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(186, 12, 47));
        jLabel12.setText("FAMILIA PRODUCTO:");
        jPanel3.add(jLabel12);
        jLabel12.setBounds(50, 240, 260, 30);

        jcombo_producto.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jcombo_producto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcombo_productoItemStateChanged(evt);
            }
        });
        jcombo_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcombo_productoActionPerformed(evt);
            }
        });
        jPanel3.add(jcombo_producto);
        jcombo_producto.setBounds(50, 280, 590, 50);

        jLabel9.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(186, 12, 47));
        jLabel9.setText("VALOR:");
        jPanel3.add(jLabel9);
        jLabel9.setBounds(50, 510, 150, 40);

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
        jPanel3.add(jLabel2);
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
        jPanel3.add(jcantidad);
        jcantidad.setBounds(220, 500, 420, 60);
        jPanel3.add(jPanel1);
        jPanel1.setBounds(700, 170, 550, 470);

        jLValor.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLValor.setText("$ 0");
        jPanel3.add(jLValor);
        jLValor.setBounds(277, 390, 350, 50);

        jtypeTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jtypeTitle.setForeground(new java.awt.Color(255, 255, 255));
        jtypeTitle.setText("CAMBIAR PRECIO");
        jPanel3.add(jtypeTitle);
        jtypeTitle.setBounds(120, 0, 510, 80);

        jLabel8.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setText("VALOR ACTUAL:");
        jPanel3.add(jLabel8);
        jLabel8.setBounds(50, 400, 210, 30);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jNotificacion);
        jNotificacion.setBounds(670, 10, 590, 60);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel4.setText("GUARDAR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        jPanel3.add(jLabel4);
        jLabel4.setBounds(270, 610, 180, 60);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alerta.png"))); // NOI18N
        jPanel3.add(jLabel3);
        jLabel3.setBounds(0, 700, 80, 100);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel28);
        jLabel28.setBounds(80, 713, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        background.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_cambio_precio.png"))); // NOI18N
        background.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(background);
        background.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel3, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jcombo_productoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcombo_productoItemStateChanged
        cargarValor();
    }//GEN-LAST:event_jcombo_productoItemStateChanged

    private void jcantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jcantidadFocusGained
        NovusUtils.deshabilitarCopiarPegar(jcantidad);

    }//GEN-LAST:event_jcantidadFocusGained

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseReleased
        validarIngreso();
    }//GEN-LAST:event_jLabel4MouseReleased

    public void validarIngreso() {
        int valorIngresado = 0;
        try {
            valorIngresado = Integer.parseInt(jcantidad.getText().trim());

        } catch (NumberFormatException ex) {
            Logger.getLogger(CambiarPrecioViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (valorIngresado > 0) {
            guardar();
        } else {
            jNotificacion.setText("VALOR NO VALIDO");
            setTimeout(3, () -> {
                resetLabel(jNotificacion);
            });
            jcantidad.setText("");
            jcantidad.requestFocus();
        }
    }

    private void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jPanel1.setVisible(false);

        this.mostrarTeclado();

        this.solicitarDatosSurtidor();

        this.cambiarTipoCantidad(PRESET_TIPO_VALOR);

        jcantidad.requestFocus();

        jLCargando.setVisible(false);

    }

    private void jcombo_mangueraActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_mangueraActionPerformed
    }// GEN-LAST:event_jcombo_mangueraActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton1ActionPerformed
        NovusUtils.beep();
        cambiarTipoCantidad(PRESET_TIPO_VALOR);
    }// GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton2ActionPerformed
        NovusUtils.beep();
        cambiarTipoCantidad(PRESET_TIPO_VOLUMEN);
    }// GEN-LAST:event_jToggleButton2ActionPerformed

    private void jcantidadKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyPressed

    }// GEN-LAST:event_jcantidadKeyPressed

    private void jcantidadKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyReleased
    }// GEN-LAST:event_jcantidadKeyReleased

    private void jcantidadKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jcantidadKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jcantidad, 6, jNotificacion, caracteresAceptados);

    }// GEN-LAST:event_jcantidadKeyTyped

    private void jcombo_productoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_productoActionPerformed
        jcantidad.requestFocus();
    }// GEN-LAST:event_jcombo_productoActionPerformed

    private void jcombo_tipoCantidadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_tipoCantidadActionPerformed

    }// GEN-LAST:event_jcombo_tipoCantidadActionPerformed

    private void jcantidadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcantidadActionPerformed
        validarIngreso();
    }// GEN-LAST:event_jcantidadActionPerformed

    private void jcantidadMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jcantidadMouseReleased
        mostrarTeclado();
    }// GEN-LAST:event_jcantidadMouseReleased

    private void jcombo_surtidorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_surtidorActionPerformed
        //cargarDatosCaras();
    }// GEN-LAST:event_jcombo_surtidorActionPerformed

    private void jcombo_caraActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jcombo_caraActionPerformed
        //cargarDatosMangueras();

    }// GEN-LAST:event_jcombo_caraActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed

    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLCargando;
    private javax.swing.JLabel jLValor;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    public static javax.swing.JTextField jcantidad;
    private javax.swing.JComboBox<String> jcombo_producto;
    private javax.swing.JLabel jtypeTitle;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        dispose();
    }

    void solicitarDatosSurtidor() {
        SetupDao sdao = new SetupDao();
        caras = sdao.getCarasN();
        mangueras = sdao.getMangueras();
        cargarDatosSurtidores();

    }

    private void mostrarTeclado() {
        jPanel1.setVisible(true);
    }

    private void recargarVista() {
        jtypeTitle.setText(this.view);
    }

    private void cargarDatosSurtidores() {
        jcombo_producto.removeAllItems();
        TreeMap<Integer, String> temp = new TreeMap<>();

        for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {

            jcombo_producto.addItem(surdetalle.getValue().getFamiliaDescripcion() + "");
            temp.put(surdetalle.getValue().getSurtidor(), surdetalle.getValue().getFamiliaDescripcion() + "");
            NovusUtils.printLn(Main.ANSI_RED + "SURTIDOR :" + surdetalle.getValue().getFamiliaDescripcion() + Main.ANSI_RESET);
            NovusUtils.printLn("Surtidor: " + temp.entrySet().toString());
        }
        NovusUtils.printLn("Surtidor: " + temp.entrySet().toString());
    }

    private void cargarValor() {
        int valorO = 0;
        String FamiliaSeleccionado = jcombo_producto.getSelectedItem().toString().trim();
        TreeMap<Integer, String> temp = new TreeMap<>();
        for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
            if (surdetalle.getValue().getFamiliaDescripcion().equals(FamiliaSeleccionado)) {
                valorO = (int) surdetalle.getValue().getProductoPrecio();
            }

        }
        jLValor.setText("$ " + valorO);
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

    public void showMessage(String msj, String ruta,
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

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

    private void guardar() {
        String cantidad = jcantidad.getText().trim();

        if (cantidad.equals("")) {
            jcantidad.requestFocus();
        } else {

            JsonObject response = guardarTipoVenta();
            if (response != null) {
                showMessage("<html><center>CAMBIANDO PRECIO POR FAVOR ESPERE...</center></html>",
                        "/com/firefuel/resources/loader_fac.gif", 
                        false, this::mostrarMenuPrincipal, 
                        false, LetterCase.FIRST_UPPER_CASE);
                setTimeout(3, () -> {
                    showMessage("<html><center>CAMBIOS REALIZADOS</center></html>", 
                            "/com/firefuel/resources/btOk.png",
                            true, this::cerrar, 
                            true, LetterCase.FIRST_UPPER_CASE);
                });
            } else {
                showMessage("<html><center>HA OCURRIDO UN ERROR AL REGISTRAR CAMBIO DE PRECIO</center></html>", 
                        "/com/firefuel/resources/btBad.png", 
                        true, this::mostrarMenuPrincipal, 
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        }
    }

    private JsonObject guardarTipoVenta() {

        long familiaId = -1;

        for (Map.Entry<Long, Surtidor> surdetalle : this.caras.entrySet()) {
            String FamiliaSeleccionado = jcombo_producto.getSelectedItem().toString().trim();

            if (FamiliaSeleccionado.equalsIgnoreCase(surdetalle.getValue().getFamiliaDescripcion().trim())) {
                familiaId = surdetalle.getValue().getFamiliaIdentificador();
                break;
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
        TreeMap<Long, Surtidor> datos;
        SetupDao sdao = new SetupDao();
        datos = sdao.getCaras();

        url = "http://localhost:8000/api/multicambioprecio";

        for (Map.Entry<Long, Surtidor> surtidor : datos.entrySet()) {
            if (surtidor.getValue().getFamiliaDescripcion().equalsIgnoreCase(jcombo_producto.getSelectedItem().toString().trim())) {
                json.addProperty("identificadorProceso", "9480b642-7727-4b83-bf18-a1288b0dac002");
                json.addProperty("listaPrecio", 1);
                json.addProperty("cantdigitos", 6);
                json.addProperty("surtidor", surtidor.getValue().getSurtidor());
                JsonArray data = new JsonArray();
                JsonArray precioArray = new JsonArray();
                JsonObject jsonData = new JsonObject();
                JsonObject jsonPrecio = new JsonObject();
                jsonPrecio.addProperty("manguera", surtidor.getValue().getManguera());
                jsonPrecio.addProperty("precioUnidad", recibido);
                precioArray.add(jsonPrecio);
                jsonData.addProperty("cara", surtidor.getValue().getCara());
                jsonData.add("precios", precioArray);
                data.add(jsonData);
                json.add("data", data);

                ClientWSAsync async = new ClientWSAsync("GUARDAR TIPO VENTA", url, NovusConstante.POST, json, true, false, header);

                try {
                    response = async.esperaRespuesta();
                    JsonObject error;
                    if (response == null) {
                        mostrarMenuPrincipal();
                        error = async.getError();
                        Logger.getLogger(CambiarPrecioViewController.class.getName()).log(Level.SEVERE, null, error);
                    }
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                    Logger.getLogger(CambiarPrecioViewController.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }

        return response;
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
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}

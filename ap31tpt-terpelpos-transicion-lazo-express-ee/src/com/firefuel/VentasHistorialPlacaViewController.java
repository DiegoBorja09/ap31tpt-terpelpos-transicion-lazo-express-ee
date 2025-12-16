
package com.firefuel;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.utils.ImageCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neo.app.bean.Recibo;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.KeyStroke;

import com.services.TimeOutsManager;
import teclado.view.common.TecladoExtendido;

public class VentasHistorialPlacaViewController extends javax.swing.JDialog {

    Recibo recibo;
    boolean fe = false;
    InfoViewController parent;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    boolean placaObligatoria = false;
    boolean facturacionPOS = false;
    KeyEvent evento;
    Runnable runnable;
    Runnable refrescar;
    PeticionFeImprimir peticionFeImprimir;
    ParametrosPeticionFePrinter parametrosPeticionFePrinter;
    TimeOutsManager timeOutsManager;
    
    // Control de impresión
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private boolean botonImprimirBloqueado = false;
    private final Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"));
    private final Icon botonBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"));

    public VentasHistorialPlacaViewController(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
        placaObligatoria = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_PLACA_IMPRESION, true);
        facturacionPOS = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_FACTURACION_POS, false);
        jkms.getInputMap().put(KeyStroke.getKeyStroke("control C"), "null");
    }

    public VentasHistorialPlacaViewController(InfoViewController parent, boolean modal, Runnable runnable) {
        super(parent, modal);
        this.parent = parent;
        this.runnable = runnable;
        initComponents();
        this.init();
        placaObligatoria = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_PLACA_IMPRESION, true);
        facturacionPOS = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_FACTURACION_POS, false);
        jkms.getInputMap().put(KeyStroke.getKeyStroke("control C"), "null");
    }

    public VentasHistorialPlacaViewController(InfoViewController parent, boolean modal, Runnable runnable, Runnable refrescar) {
        super(parent, modal);
        this.parent = parent;
        this.runnable = runnable;
        initComponents();
        this.init();
        placaObligatoria = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_PLACA_IMPRESION, true);
        facturacionPOS = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_FACTURACION_POS, false);
        jkms.getInputMap().put(KeyStroke.getKeyStroke("control C"), "null");
        this.refrescar = refrescar;
    }

    void init() {
        this.peticionFeImprimir = new PeticionFeImprimir();
        this.parametrosPeticionFePrinter = new ParametrosPeticionFePrinter();
        this.timeOutsManager = new TimeOutsManager();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
    }

    void setRecibo(Recibo recibo) {
        this.recibo = recibo;
        boolean is_especial = false;
        if (recibo.getAtributos() != null && !recibo.getAtributos().isJsonNull() && recibo.getAtributos().get("is_especial") != null) {
            is_especial = recibo.getAtributos().get("is_especial").getAsBoolean();
        }
        if (recibo.getAtributos() != null && !recibo.getAtributos().isJsonNull() && recibo.getAtributos().get("tipo") != null && recibo.getAtributos().get("tipo").getAsString().equals("014")) {
            is_especial = true;
        }
        jtitulo.setText("IMPRIMIR VENTAS No. " + recibo.getNumero());
        jLabel10.setText("$ " + df.format(recibo.getTotal()));
        jLabel2.setText(String.format("%.3f", recibo.getCantidadFactor()) + (recibo.getAtributos() != null && !recibo.getAtributos().get("medida").getAsString().equals("UNDEFINED") ? recibo.getAtributos().get("medida").getAsString().toUpperCase() : "GL"));
        if (recibo.getPlaca() != null && !recibo.getPlaca().trim().equals("") || is_especial) {
            jplaca.setText(recibo.getPlaca());
            jplaca.setEnabled(false);
            if (recibo.getOdometro() != null) {
                jkms.setText(recibo.getOdometro());
                jkms.setEnabled(false);
            }
            if (recibo.getVoucher() != null) {
                jorden.setText(recibo.getVoucher());
                jorden.setEnabled(false);
            }
            if (recibo.getAtributos() != null && !recibo.getAtributos().get("vehiculo_numero").isJsonNull()) {
                jnumero.setText(recibo.getAtributos().get("vehiculo_numero").getAsString());
                jnumero.setEnabled(false);
            }
        }
        
        // Verificar si el registro existe en cola de impresión y bloquear botón
        if (existeEnColaPendiente(recibo.getNumero())) {
            NovusUtils.printLn("Registro encontrado en cola de impresión - ID: " + recibo.getNumero());
            bloquearBotonImprimir();
        }
    }

    void isFe(boolean fe) {
        this.fe = fe;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jplaca = new javax.swing.JTextField();
        jnumero = new javax.swing.JTextField();
        jPanel1 = new TecladoExtendido()
        ;
        jLabel3 = new javax.swing.JLabel();
        jorden = new javax.swing.JTextField();
        jkms = new javax.swing.JTextField();
        jNotificacion_Imprimir = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jtitulo = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jplaca.setBackground(new java.awt.Color(238, 238, 238));
        jplaca.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jplaca.setBorder(null);
        jplaca.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jplacaCaretUpdate(evt);
            }
        });
        jplaca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jplacaFocusGained(evt);
            }
        });
        jplaca.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jplacaInputMethodTextChanged(evt);
            }
        });
        jplaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jplacaActionPerformed(evt);
            }
        });
        jplaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jplacaKeyTyped(evt);
            }
        });
        getContentPane().add(jplaca);
        jplaca.setBounds(520, 140, 290, 45);

        jnumero.setBackground(new java.awt.Color(238, 238, 238));
        jnumero.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jnumero.setBorder(null);
        jnumero.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jnumeroFocusGained(evt);
            }
        });
        jnumero.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jnumeroMouseReleased(evt);
            }
        });
        jnumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jnumeroActionPerformed(evt);
            }
        });
        jnumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jnumeroKeyTyped(evt);
            }
        });
        getContentPane().add(jnumero);
        jnumero.setBounds(930, 130, 210, 45);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(100, 360, 1080, 340);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(186, 12, 47));
        jLabel3.setText("ORDEN:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(400, 280, 110, 50);

        jorden.setBackground(new java.awt.Color(238, 238, 238));
        jorden.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jorden.setBorder(null);
        jorden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jordenFocusGained(evt);
            }
        });
        jorden.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jordenKeyTyped(evt);
            }
        });
        getContentPane().add(jorden);
        jorden.setBounds(520, 280, 300, 45);

        jkms.setBackground(new java.awt.Color(238, 238, 238));
        jkms.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jkms.setBorder(null);
        jkms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jkmsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jkmsFocusLost(evt);
            }
        });
        jkms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jkmsKeyTyped(evt);
            }
        });
        getContentPane().add(jkms);
        jkms.setBounds(520, 210, 290, 45);

        jNotificacion_Imprimir.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jNotificacion_Imprimir.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion_Imprimir);
        jNotificacion_Imprimir.setBounds(150, 720, 960, 70);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(186, 12, 47));
        jLabel7.setText("KMS:");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(400, 210, 110, 50);

        jLabel2.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("0.000 gl");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(110, 270, 220, 50);

        jLabel4.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        jLabel4.setText("IMPRIMIR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel4);
        jLabel4.setBounds(930, 270, 190, 70);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_atras.png"));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel6MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel6);
        jLabel6.setBounds(10, 10, 70, 71);

        jLabel8.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("NO:");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(850, 130, 70, 40);

        jLabel9.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(186, 12, 47));
        jLabel9.setText("CANTIDAD:");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(100, 220, 220, 50);

        jLabel10.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("$ 10.000.000");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(110, 160, 230, 50);

        jLabel11.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(186, 12, 47));
        jLabel11.setText("PLACA:");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(400, 140, 110, 50);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(186, 12, 47));
        jLabel12.setText("VALOR:");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(100, 110, 220, 50);

        jtitulo.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jtitulo.setForeground(new java.awt.Color(255, 255, 255));
        jtitulo.setText("IMPRIMIR VENTAS");
        getContentPane().add(jtitulo);
        jtitulo.setBounds(100, 0, 650, 90);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        getContentPane().add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jNotificacion);
        jNotificacion.setBounds(670, 10, 590, 60);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(ImageCache.getImage("/com/firefuel/resources/fndimprimirventa.png"));
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jplacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jplacaKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, jplaca, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jplacaKeyTyped

    private void jkmsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jkmsKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jkms, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jkmsKeyTyped

    private void jordenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jordenKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jorden, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jordenKeyTyped

    private void jnumeroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jnumeroKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jnumero, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jnumeroKeyTyped

    private void jplacaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jplacaFocusGained
        desactivarCaratecresEspeciales();
        NovusUtils.deshabilitarCopiarPegar(jplaca);
    }//GEN-LAST:event_jplacaFocusGained

    private void jordenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jordenFocusGained
        NovusUtils.deshabilitarCopiarPegar(jorden);
    }//GEN-LAST:event_jordenFocusGained

    private void jnumeroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jnumeroFocusGained
        NovusUtils.deshabilitarCopiarPegar(jnumero);
        desactivarCaratecresEspeciales();
    }//GEN-LAST:event_jnumeroFocusGained

    private void jplacaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jplacaCaretUpdate

    }//GEN-LAST:event_jplacaCaretUpdate

    private void jplacaInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jplacaInputMethodTextChanged

    }//GEN-LAST:event_jplacaInputMethodTextChanged

    private void jnumeroMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jnumeroMouseReleased

    }//GEN-LAST:event_jnumeroMouseReleased

    private void jLabel6MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MousePressed

    }// GEN-LAST:event_jLabel6MousePressed

    private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel6MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel6MouseReleased

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        if (jLabel4.isEnabled()) {
            imprimir(facturacionPOS ? "factura" : "venta");
        }
    }// GEN-LAST:event_jLabel4MouseReleased

    private void jplacaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jplacaActionPerformed
    }// GEN-LAST:event_jplacaActionPerformed

    private void jkmsFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jkmsFocusGained
        desactivarTeclado();
        desactivarCaratecresEspeciales();
        NovusUtils.deshabilitarCopiarPegar(jkms);
    }// GEN-LAST:event_jkmsFocusGained

    private void jkmsFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jkmsFocusLost
        activarTeclado();
    }// GEN-LAST:event_jkmsFocusLost

    private void jnumeroActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jnumeroActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jnumeroActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion_Imprimir;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jkms;
    private javax.swing.JTextField jnumero;
    private javax.swing.JTextField jorden;
    public static javax.swing.JTextField jplaca;
    private javax.swing.JLabel jtitulo;
    // End of variables declaration//GEN-END:variables

    public void cerrar() {
        if (runnable != null) {
            runnable.run();
        }
        if (refrescar != null) {
            refrescar.run();
        }
        this.setVisible(false);
        dispose();
    }

    private void printVentaById3(long id, String placa, String numero, String odometro, String orden, String route) {

        JsonObject comando = new JsonObject();

        comando.addProperty("identificadorMovimiento", id);
        comando.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        comando.addProperty("placa", placa);
        comando.addProperty("odometro", odometro);
        comando.addProperty("numero", numero);
        comando.addProperty("orden", orden);

        String funcion = "ACTUALIZAR VENTAS";
        String url = NovusConstante.SECURE_CENTRAL_POINT_ACTUALIZAR_ATRIBUTOS_VENTA;
        String metho = NovusConstante.PUT;
        boolean isArray = false;
        boolean isDebug = true;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(funcion, url, metho, comando, isDebug, isArray, header);
        if (!route.equals("factura-electronica")) {
            try {
                client.start();
                JsonObject x = client.getResponse();
                
                if (x != null) {
                    NovusUtils.printLn("RESPUESTA: " + x.toString());
                } else {
                    NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
                    NovusUtils.printLn("║  SERVICIO DE IMPRESIÓN PYTHON ESTÁ APAGADO                ║");
                    NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
                    NovusUtils.printLn("ERROR: Sin respuesta del microservicio Python");
                    NovusUtils.printLn("URL: " + url);
                    NovusUtils.printLn("");
                    NovusUtils.printLn("Posibles causas:");
                    NovusUtils.printLn("   - Microservicio Python no está levantado (CAUSA MÁS PROBABLE)");
                    NovusUtils.printLn("   - Error de red/conectividad");
                    NovusUtils.printLn("");
                    NovusUtils.printLn("Solución: Verificar que el servicio Python esté corriendo en el puerto 8001");
                }

            } catch (Exception e) {
                NovusUtils.printLn("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        funcion = "IMPRIMIR VENTAS";
        JsonObject json = new JsonObject();
        json.addProperty("movement_id", id);
        json.addProperty("flow_type", "VENTAS_HISTORICAS");
        json.addProperty("report_type", route.toUpperCase());
        JsonObject bodyJson = new JsonObject();
        json.add("body", bodyJson);

        url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        metho = NovusConstante.POST;
        isArray = false;
        isDebug = true;
        header = new TreeMap<>();
        header.put("Content-type", "application/json");
        client = new ClientWSAsync(funcion, url, metho, json, isDebug, isArray, header);

        // El botón ya está bloqueado visualmente por bloquearBotonImprimir()
        try {
            if (this.fe) {
                peticionFeImprimir.setNumero(numero);
                peticionFeImprimir.setOrden(orden);
                peticionFeImprimir.setIdentificadorMovimiento(id);
                peticionFeImprimir.setIdentificadorEquipo(Main.credencial.getEquipos_id());
                peticionFeImprimir.setPlaca(placa);
                peticionFeImprimir.setOdometro(odometro);
                parametrosPeticionFePrinter.setPeticionFeImprimir(peticionFeImprimir);
                url = NovusConstante.SECURE_CENTRAL_POINT_IMPRESION_VENTA + "/" + route;
                parametrosPeticionFePrinter.setUrl(url);
                boolean iniciandoProceso = SingletonMedioPago.ConetextDependecy.getAgenteDeImpresionFE().execute(parametrosPeticionFePrinter);
                if (iniciandoProceso) {
                    jNotificacion_Imprimir.setText("IMPRESIÓN DE VENTA EN PROCESO");
                } else {
                    jNotificacion_Imprimir.setText("Factura electrónica en proceso, por favor espere".toUpperCase());
                }

            } else {
                // Enviar solicitud SIN esperar respuesta
                client.start();
                NovusUtils.printLn("Solicitud de impresión enviada al servicio Python - ID: " + id);
                jNotificacion_Imprimir.setText("IMPRESIÓN EN PROCESO...");
            }

            this.timeOutsManager.setTimeoutUtilManager(2, () -> {
                NovusConstante.ventanaFE = false;
                jNotificacion_Imprimir.setText("");
                this.cerrar();
            });
        } catch (Exception e) {
            jNotificacion_Imprimir.setText("ERROR IMPRESION VENTA");
            NovusUtils.printLn("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        jNotificacion_Imprimir.setVisible(true);
    }

    Matcher getValidator(String stringCompare, String stringRegex) {
        Pattern pat = Pattern.compile(stringRegex);
        Matcher mat = pat.matcher(stringCompare);
        return mat;
    }

    boolean validarPlacaAmarilla(String placa) {
        Matcher matcher = this.getValidator(placa, "[a-zA-Z]{3}[0-9]{3}");
        return matcher.matches();
    }

    boolean validarPlacaRoja(String placa) {
        Matcher matcher = this.getValidator(placa, "[a-zA-Z]{1}[0-9]{4}");
        return matcher.matches();
    }

    boolean validarPlacaAzul(String placa) {
        Matcher matcher = this.getValidator(placa, "[a-zA-Z]{2}[0-9]{4}");
        return matcher.matches();
    }

    boolean validarPlacaVerde(String placa) {
        Matcher matcher = this.getValidator(placa, "[a-zA-Z]{1}[0-9]{5}");
        return matcher.matches();
    }

    boolean validarPlacaAntiguaMoto(String placa) {
        Matcher matcher = this.getValidator(placa, "[a-zA-Z]{3}[0-9]{2}");
        return matcher.matches();
    }

    boolean validFieldsInformation() {
        String placa = jplaca.getText().trim();
        if (placa.length() > 0) {
            if (this.validarPlacaAmarilla(placa)) {
                return true;
            }
            if (this.validarPlacaRoja(placa)) {
                return true;
            }
            if (this.validarPlacaAzul(placa)) {
                return true;
            }
            if (this.validarPlacaVerde(placa)) {
                return true;
            }
            if (this.validarPlacaAntiguaMoto(placa)) {
                return true;
            }
            jNotificacion_Imprimir.setText("LA PLACA INGRESADA NO COINCIDE CON NINGUN FORMATO");
            jNotificacion_Imprimir.setVisible(true);

            this.timeOutsManager.setTimeoutUtilManager(2, () -> {
                jNotificacion_Imprimir.setText("");
                cerrar();
            });
            return false;
        } else {
            return true;
        }
    }

    public void imprimir(String route) {
        // 1. Verificar si el botón está bloqueado
        if (botonImprimirBloqueado) {
            NovusUtils.printLn("Botón de impresión bloqueado - hay una impresión en proceso");
            return;
        }
        
        boolean valid = true;
        if (placaObligatoria && jplaca.getText().trim().equals("")) {
            valid = false;
        }
        if (valid) {
            // 2. Bloquear el botón INMEDIATAMENTE
            bloquearBotonImprimir();
            
            // 3. Verificar si ya existe en cola
            if (existeEnColaPendiente(recibo.getNumero())) {
                NovusUtils.printLn("Registro ya existe en cola de impresión - ID: " + recibo.getNumero());
                return;
            }
            
            // 4. Guardar en cola de impresión
            guardarRegistroPendiente(recibo.getNumero(), "VENTAS_HISTORICAS");
            
            if (recibo.getAtributos() != null && !recibo.getAtributos().isJsonNull() && recibo.getAtributos().get("tipoVenta") != null && recibo.getAtributos().get("tipoVenta").getAsFloat() == 100) {
                route = "remision";
            }
            if (recibo.getAtributos() != null && !recibo.getAtributos().isJsonNull() && recibo.getAtributos().get("is_especial") != null && recibo.getAtributos().get("is_especial").getAsBoolean() && recibo.getAtributos().get("consecutivo") != null && recibo.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo") != null && recibo.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo").getAsString().contains("PROPIO")) {
                route = "consumo-propio";
            }
            if (recibo.getAtributos() != null && !recibo.getAtributos().isJsonNull() && recibo.getAtributos().get("tipo") != null && recibo.getAtributos().get("tipo").getAsString().equals("014")) {
                route = "calibracion";
            }
            NovusUtils.beep();
            // 5. Enviar a imprimir SIN esperar respuesta
            printVentaById3(recibo.getNumero(), jplaca.getText(), jnumero.getText(), jkms.getText(), jorden.getText(), route);
        } else {
            jNotificacion_Imprimir.setText("DEBE ESPECIFICAR LA PLACA");
            jNotificacion_Imprimir.setVisible(true);
            this.timeOutsManager.setTimeoutUtilManager(2, () -> {
                jNotificacion_Imprimir.setText("");
                cerrar();
            });

        }

    }

    private void desactivarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel1;
        teclado.habilitarAlfanumeric(false);
    }

    private void activarTeclado() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel1;
        teclado.habilitarAlfanumeric(true);
    }

    private void desactivarCaratecresEspeciales() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel1;
        teclado.deshabilitarCaracteresEspeciales(false);
    }
    
    // ============================================
    // MÉTODOS DE CONTROL DE IMPRESIÓN
    // ============================================
    
    /**
     * Bloquea el botón de imprimir y cambia el texto a IMPRIMIENDO...
     */
    private void bloquearBotonImprimir() {
        botonImprimirBloqueado = true;
        jLabel4.setText("IMPRIMIENDO...");
        jLabel4.setIcon(botonBloqueado);
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     */
    private synchronized boolean existeEnColaPendiente(long id) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() > 0) {
                    JsonArray registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registros) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.get("id").getAsLong() == id
                            && registro.has("report_type") && registro.get("report_type").getAsString().equals("VENTAS_HISTORICAS")) {
                            return true;
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
     * Guarda un registro de impresión pendiente en el archivo TXT
     */
    private synchronized void guardarRegistroPendiente(long id, String reportType) {
        try {
            // Crear carpeta logs si no existe
            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdir();
            }

            // Leer archivo existente o crear nuevo array
            JsonArray registros = new JsonArray();
            File file = new File(PRINT_QUEUE_FILE);
            
            if (file.exists() && file.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("Error leyendo archivo de cola de impresión: " + e.getMessage());
                    registros = new JsonArray();
                }
            }

            // Verificar si el ID ya existe
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    NovusUtils.printLn("Registro ya existe en cola de impresión - ID: " + id);
                    return;
                }
            }

            // Crear nuevo registro
            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("status", "PENDING");
            nuevoRegistro.addProperty("message", "IMPRIMIENDO...");

            registros.add(nuevoRegistro);

            // Guardar archivo
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registros));
            }

            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(VentasHistorialPlacaViewController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}

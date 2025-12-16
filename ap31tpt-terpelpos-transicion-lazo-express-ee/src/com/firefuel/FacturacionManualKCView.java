package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.ConsecutivoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.firefuel.utils.ShowMessageSingleton;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import teclado.view.common.TecladoNumerico;

public class FacturacionManualKCView extends javax.swing.JDialog {

    String negocio;
    InfoViewController parent;
    String bebasNeue = "Bebas Neue"; //Fuente
    MovimientosDao mdao = new MovimientosDao();
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    long nFactura = 0l;
    boolean ventanaVenta = false;
    boolean ventanaprincipal = false;

    public FacturacionManualKCView(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    public FacturacionManualKCView(JFrame parent, boolean modal, String negocio) {
        super(parent, modal);
        this.negocio = negocio;
        initComponents();
        init();
    }

    public FacturacionManualKCView(JFrame parent, boolean modal, String negocio, InfoViewController info) {
        super(parent, modal);
        this.negocio = negocio;
        this.parent = info;
        initComponents();
        init();
    }

    public FacturacionManualKCView(JFrame parent, boolean modal, String negocio, InfoViewController info, boolean ventanaVenta, boolean ventanaprincipal) {
        super(parent, modal);
        this.negocio = negocio;
        this.parent = info;
        this.ventanaVenta = ventanaVenta;
        this.ventanaprincipal = ventanaprincipal;
        initComponents();
        init();
    }

    public String getNegocio(String tipo) {
        String tipoNegocio = "";
        switch (tipo) {
            case "M":
                tipoNegocio = "MARKET";
                break;
            case "C":
                tipoNegocio = "CANASTILLA";
                break;
            default:
                throw new AssertionError();
        }
        return tipoNegocio;
    }

    private void init() {
        String tipo = getNegocio(negocio);
        jTitle.setText("INGRESO DE FACTURA DE CONTINGENCIA " + tipo);
        addHora();
        txtFecha.setDatoFecha(new Date());
        lblFactura.setFont(new java.awt.Font(bebasNeue, 0, 30));
        lblFecha.setFont(new java.awt.Font(bebasNeue, 0, 30));
        lblHora.setFont(new java.awt.Font(bebasNeue, 0, 30));
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        txtFactura.setEditable(false);
        txtFactura.requestFocus();
        consecutivoValido();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        home = new javax.swing.JPanel();
        jTitle = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        txtFecha = new rojeru_san.componentes.RSDateChooser();
        cmbHoras = new javax.swing.JComboBox<>();
        lblHora = new javax.swing.JLabel();
        lbl = new javax.swing.JLabel();
        cmbMinutos = new javax.swing.JComboBox<>();
        lblFactura = new javax.swing.JLabel();
        txtFactura = new javax.swing.JTextField();
        back = new javax.swing.JLabel();
        tecNumericoPnlPrincipal = new TecladoNumerico()
        ;
        jLabel31 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setMaximumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        home.setLayout(null);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("INGRESO DE FACTURA DE CONTINGENCIA");
        home.add(jTitle);
        jTitle.setBounds(110, 20, 1100, 50);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        home.add(jNotificacion);
        jNotificacion.setBounds(730, 10, 530, 70);

        lblFecha.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(186, 12, 47));
        lblFecha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFecha.setText("FECHA");
        lblFecha.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        home.add(lblFecha);
        lblFecha.setBounds(100, 390, 80, 30);

        txtFecha.setColorBackground(new java.awt.Color(186, 12, 47));
        txtFecha.setColorButtonHover(new java.awt.Color(186, 12, 47));
        txtFecha.setColorDiaActual(new java.awt.Color(186, 12, 47));
        txtFecha.setColorForeground(new java.awt.Color(186, 12, 47));
        txtFecha.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        txtFecha.setFormatoFecha("yyyy-MM-dd");
        txtFecha.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        txtFecha.setPlaceholder("yyyy-mm-dd");
        txtFecha.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFechaMouseClicked(evt);
            }
        });
        home.add(txtFecha);
        txtFecha.setBounds(100, 430, 240, 50);

        cmbHoras.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbHorasActionPerformed(evt);
            }
        });
        home.add(cmbHoras);
        cmbHoras.setBounds(370, 430, 90, 50);

        lblHora.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblHora.setForeground(new java.awt.Color(186, 12, 47));
        lblHora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHora.setText("HORA");
        lblHora.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        home.add(lblHora);
        lblHora.setBounds(370, 390, 200, 30);

        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl.setText(":");
        lbl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        home.add(lbl);
        lbl.setBounds(460, 440, 20, 30);

        cmbMinutos.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbMinutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMinutosActionPerformed(evt);
            }
        });
        home.add(cmbMinutos);
        cmbMinutos.setBounds(480, 430, 90, 50);

        lblFactura.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblFactura.setForeground(new java.awt.Color(186, 12, 47));
        lblFactura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFactura.setText("NRO. FACTURA");
        lblFactura.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        home.add(lblFactura);
        lblFactura.setBounds(110, 310, 160, 50);

        txtFactura.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        txtFactura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFactura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacturaFocusGained(evt);
            }
        });
        txtFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacturaActionPerformed(evt);
            }
        });
        txtFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFacturaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFacturaKeyTyped(evt);
            }
        });
        home.add(txtFactura);
        txtFactura.setBounds(290, 310, 260, 50);

        back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backMouseReleased(evt);
            }
        });
        home.add(back);
        back.setBounds(10, 10, 70, 71);
        home.add(tecNumericoPnlPrincipal);
        tecNumericoPnlPrincipal.setBounds(680, 150, 550, 470);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        home.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        home.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        home.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        home.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndVentaManualKC.png"))); // NOI18N
        home.add(fondo);
        fondo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(home, "home");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void backMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMouseReleased
        cerrar();
    }//GEN-LAST:event_backMouseReleased

    private void txtFacturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacturaFocusGained
        NovusUtils.deshabilitarCopiarPegar(txtFactura);
    }//GEN-LAST:event_txtFacturaFocusGained

    private void txtFacturaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFacturaKeyReleased
        enviarDatos(evt);
    }//GEN-LAST:event_txtFacturaKeyReleased

    private void txtFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFacturaKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txtFactura, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtFacturaKeyTyped

    private void txtFechaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFechaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaMouseClicked

    private void cmbHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbHorasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbHorasActionPerformed

    private void cmbMinutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMinutosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMinutosActionPerformed

    private void txtFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacturaActionPerformed

    }//GEN-LAST:event_txtFacturaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back;
    private javax.swing.JComboBox<String> cmbHoras;
    private javax.swing.JComboBox<String> cmbMinutos;
    private javax.swing.JLabel fondo;
    private javax.swing.JPanel home;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jTitle;
    public javax.swing.JLabel lbl;
    public javax.swing.JLabel lblFactura;
    public javax.swing.JLabel lblFecha;
    public javax.swing.JLabel lblHora;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel tecNumericoPnlPrincipal;
    public javax.swing.JTextField txtFactura;
    private rojeru_san.componentes.RSDateChooser txtFecha;
    // End of variables declaration//GEN-END:variables

    public void cerrar() {
        this.dispose();
    }

    private void addHora() {
        LocalDateTime now = LocalDateTime.now();
        String hora;
        String min;
        for (int i = 0; i < 24; i++) {
            hora = "";
            if (i < 10) {
                hora = "0" + i + hora;
            } else {
                hora = i + hora;
            }
            cmbHoras.addItem(hora);
        }
        cmbHoras.setSelectedIndex(now.getHour());

        for (int i = 0; i < 60; i++) {
            min = "";
            if (i < 10) {
                min = "0" + i + min;
            } else {
                min = i + min;
            }
            cmbMinutos.addItem(min);
        }
        cmbMinutos.setSelectedIndex(now.getMinute());

    }
    String fecha;

    private boolean validarHora() {
        LocalDateTime now = LocalDateTime.now();
        String[] date = sdf.format(txtFecha.getDatoFecha()).split("-");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        int hour = cmbHoras.getSelectedIndex();
        int min = cmbMinutos.getSelectedIndex();
        int seg = now.getSecond();
        LocalDateTime target = LocalDateTime.of(year, month, day, hour, min);
        fecha = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day)
                + " " + (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":" + (seg < 10 ? "0" + seg : seg);
        return target.isBefore(now);
    }

    private boolean consecutivoValido() {
        boolean valido = false;
        try {
            String resolucion;
            boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
            if (this.negocio.equals("M")) {
                if (isCDL) {
                    resolucion = "CDL";
                } else {
                    resolucion = "KSC";
                }
            } else {
                resolucion = "CAN";
            }
            ConsecutivoBean consecutivo = mdao.getPrefijo(18, resolucion);
            if (consecutivo != null) {
                txtFactura.setText(consecutivo.getConsecutivo_actual_fe() + "");
                txtFactura.setEditable(false);
                valido = true;
            } else {
                showMessage("NO HAY RANGO DE FACTURACION EN CONTINGENCIA", 
                        "/com/firefuel/resources/btBad.png",
                        true, this::cerrar, 
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } catch (DAOException ex) {
            Logger.getLogger(FacturacionManualView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valido;
    }

    private void enviarDatos(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!validarHora()) {
                showMessage("LA FECHA ES INVALIDA, POR FAVOR VERIFIQUE.", 
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true,LetterCase.FIRST_UPPER_CASE);
                return;
            }
            enter();
        }
    }

    private void enter() {
        switch (negocio) {
            case "M":
                KCOViewController kco = new KCOViewController(parent, true, true, this, nFactura, fecha, this.ventanaVenta, ventanaprincipal);
                System.out.println(fecha);
                this.showParent(kco);
                this.dispose();
                break;
            case "C":
                StoreViewController store = new StoreViewController(parent, true, true, this, nFactura, fecha, this.ventanaVenta, ventanaprincipal);
                this.showParent(store);
                this.dispose();
                break;
            default:
                throw new AssertionError();
        }
    }

    public void showParent(JFrame frame) {
        JPanel panel = (JPanel) frame.getContentPane();
        this.parent.mostrarSubPanel(panel);
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
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

    public void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "home");
        txtFactura.requestFocus();
    }

    public void showPanel(String panel) {
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

}

package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.firefuel.utils.ShowMessageSingleton;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.Timer;
import teclado.view.common.TecladoNumerico;

public class FormatearViewController extends javax.swing.JDialog {

    InfoViewController parent;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    ArrayList<Surtidor> lsurtidores = new ArrayList<>();
    public static int VENTANA_A_CARGAR;
    public final static int VENTANA_RESTAURARA_EQUIPO = 1;
    public final static int VENTANA_PARAMETRO = 2;

    public FormatearViewController(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }

    public void mostrarPantalla(int pantallaMostrar) {
        VENTANA_A_CARGAR = pantallaMostrar;
    }

    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jPanel1.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jNotificacion = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jpassword = new javax.swing.JPasswordField();
        jPanel1 = new TecladoNumerico()
        ;
        jTitle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel3.setLayout(null);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jNotificacion);
        jNotificacion.setBounds(140, 720, 980, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel3.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel5.setFont(new java.awt.Font("Conthrax", 1, 30)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CLAVE MAESTRA");
        jPanel3.add(jLabel5);
        jLabel5.setBounds(170, 320, 360, 60);

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
        jLabel2.setBounds(10, 10, 70, 71);

        jpassword.setBackground(new java.awt.Color(239, 239, 239));
        jpassword.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jpassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jpassword.setBorder(null);
        jpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jpasswordActionPerformed(evt);
            }
        });
        jpassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jpasswordKeyTyped(evt);
            }
        });
        jPanel3.add(jpassword);
        jpassword.setBounds(100, 410, 500, 50);
        jPanel3.add(jPanel1);
        jPanel1.setBounds(700, 160, 550, 470);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("FORMATEAR EQUIPO");
        jPanel3.add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel30);
        jLabel30.setBounds(90, 10, 10, 68);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndClaveMaestra.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel3, "pnl_principal");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jpasswordKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jpasswordKeyTyped
        int tam = jpassword.getPassword().length;
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) || tam > 8) {
            evt.consume();
        }
    }// GEN-LAST:event_jpasswordKeyTyped

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed

    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    private void jpasswordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jpasswordActionPerformed
        char[] prepwd = jpassword.getPassword();
        int password = 0;
        if (!(prepwd.length == 0)) {
            password = Integer.parseInt(new String(prepwd));
        }
        if (password == 0) {
            jpassword.requestFocus();
        } else {
            if (password == NovusConstante.PASSWORD_RESET || password == NovusConstante.PASSWORD_PARAMETRIZACION) {
                switch (VENTANA_A_CARGAR) {
                    case VENTANA_RESTAURARA_EQUIPO:
                        formatearEquipo();
                        break;
                    default:
                        throw new AssertionError();
                }
            } else {
                setTimeout(2, () -> {
                    NovusUtils.setMensaje("", jNotificacion);
                    cerrar();
                });
            }
        }

    }// GEN-LAST:event_jpasswordActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPasswordField jpassword;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        dispose();
    }

    public void formatearEquipo() {
        showMessage("FORMATEO EQUIPO EN PROCESO",
                "/com/firefuel/resources/loader_fac.gif", false, null, 
                false, LetterCase.FIRST_UPPER_CASE);
        guardar();
    }

    void guardar() {
        EquipoDao dao = new EquipoDao();
        char[] prepwd = jpassword.getPassword();
        int password = NovusConstante.PASSWORD_RESET;

        if (password == 0) {
            jpassword.requestFocus();
        } else {
            if (password == NovusConstante.PASSWORD_RESET) {
                dao.formatearEquipo();
                showMessage("EQUIPO RESETEADO DE FABRICA EXITOSAMENTE", 
                        "/com/firefuel/resources/btOk.png", 
                        true, this::salir,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        }
    }

    private void salir() {
        System.exit(0);
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

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
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

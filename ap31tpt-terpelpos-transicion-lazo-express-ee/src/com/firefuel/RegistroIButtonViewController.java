package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.persons.FindPersonaUseCase;
import com.application.useCases.persons.RegistrarTagUseCase;
import com.application.useCases.sutidores.ObtenerInfoSurtidoresEstacionUseCase;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ControllerSync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.PersonasDao;
import com.dao.SurtidorDao;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import teclado.view.common.TecladoNumerico;

public class RegistroIButtonViewController extends javax.swing.JDialog {

    UsuariosRegistradosViewController userView;
    InfoViewController parent;
    PersonaBean persona;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    ArrayList<Surtidor> lsurtidores = new ArrayList<>();
    public static RegistroIButtonViewController instance = null;
    public static Notificador notificadorIbuton = null;
    private ObtenerInfoSurtidoresEstacionUseCase obtenerInfoSurtidoresEstacionUseCase;

    public RegistroIButtonViewController(InfoViewController parent, boolean modal, UsuariosRegistradosViewController users) {
        super(parent, modal);
        this.parent = parent;
        this.userView = users;
        initComponents();
        this.init();

    }

    public static RegistroIButtonViewController getInstance(InfoViewController parent, boolean modal, UsuariosRegistradosViewController users) {
        if (RegistroIButtonViewController.instance == null) {
            RegistroIButtonViewController.instance = new RegistroIButtonViewController(parent, modal, users);
        } else {
            RegistroIButtonViewController.instance.userView = users;
        }
        return RegistroIButtonViewController.instance;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.obtenerInfoSurtidoresEstacionUseCase = new ObtenerInfoSurtidoresEstacionUseCase();

        pnl_container = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        jNotificacion1 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtag = new javax.swing.JTextField();
        juser = new javax.swing.JTextField();
        jPanel1 = new TecladoNumerico()
        ;
        jLabel7 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_container.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_container.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_container.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        jNotificacion1.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        jNotificacion1.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion1);
        jNotificacion1.setBounds(140, 720, 980, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        pnl_principal.add(jclock);
        jclock.setBounds(1150, 720, 110, 66);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("IBUTTON:");
        pnl_principal.add(jLabel6);
        jLabel6.setBounds(70, 550, 160, 80);

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
        pnl_principal.add(jLabel2);
        jLabel2.setBounds(10, 10, 70, 71);

        jtag.setEditable(false);
        jtag.setBackground(new java.awt.Color(186, 12, 47));
        jtag.setFont(new java.awt.Font("Roboto", 1, 30)); // NOI18N
        jtag.setForeground(new java.awt.Color(255, 255, 255));
        jtag.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jtag.setBorder(null);
        jtag.setCaretColor(new java.awt.Color(255, 255, 0));
        jtag.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtagMouseReleased(evt);
            }
        });
        jtag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtagActionPerformed(evt);
            }
        });
        jtag.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtagKeyTyped(evt);
            }
        });
        pnl_principal.add(jtag);
        jtag.setBounds(260, 560, 330, 60);

        juser.setEditable(false);
        juser.setBackground(new java.awt.Color(186, 12, 47));
        juser.setFont(new java.awt.Font("Roboto", 1, 30)); // NOI18N
        juser.setForeground(new java.awt.Color(255, 255, 255));
        juser.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        juser.setBorder(null);
        juser.setCaretColor(new java.awt.Color(255, 255, 0));
        juser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                juserMouseReleased(evt);
            }
        });
        juser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juserActionPerformed(evt);
            }
        });
        juser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                juserKeyTyped(evt);
            }
        });
        pnl_principal.add(juser);
        juser.setBounds(70, 180, 510, 60);
        pnl_principal.add(jPanel1);
        jPanel1.setBounds(670, 170, 570, 470);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("REGISTRO IBUTTON");
        pnl_principal.add(jLabel7);
        jLabel7.setBounds(100, 0, 750, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnl_principal.add(jNotificacion);
        jNotificacion.setBounds(710, 10, 530, 70);

        jLabel8.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setText("IDENTIFICACION:");
        pnl_principal.add(jLabel8);
        jLabel8.setBounds(50, 110, 240, 60);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnl_principal.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRegIbutton.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_principal.add(jLabel1);
        jLabel1.setBounds(0, 0, 1281, 800);

        pnl_container.add(pnl_principal, "pnl_principal");

        getContentPane().add(pnl_container);
        pnl_container.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    void init() {
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        jPanel1.setVisible(false);

        juser.requestFocus();

        mostrarTeclado();
    }
    private void juserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juserActionPerformed
        guardar();
    }//GEN-LAST:event_juserActionPerformed

    private void juserMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_juserMouseReleased
        mostrarTeclado();
    }//GEN-LAST:event_juserMouseReleased

    private void jtagMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtagMouseReleased
        mostrarTeclado();
    }//GEN-LAST:event_jtagMouseReleased

    private void jtagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtagActionPerformed
        guardar();
    }//GEN-LAST:event_jtagActionPerformed

    private void juserKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_juserKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jtag, 11, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_juserKeyTyped

    private void jtagKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtagKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, jtag, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jtagKeyTyped

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed

    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel2MouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
        // selectme();
    }// GEN-LAST:event_jTable1MouseClicked

    private void jLabelPressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jLabel11MousePressed

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        // consultandoLecturas();
    }// GEN-LAST:event_jLabel11MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jNotificacion;
    public static javax.swing.JLabel jNotificacion1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jclock;
    public static javax.swing.JTextField jtag;
    public static javax.swing.JTextField juser;
    private javax.swing.JPanel pnl_container;
    private javax.swing.JPanel pnl_principal;
    // End of variables declaration//GEN-END:variables

    public void setPersona(PersonaBean persona) {
        this.persona = persona;
        juser.setText(persona.getIdentificacion());
        if (this.persona.getTag() != null && !this.persona.getTag().trim().equals("")) {
            jtag.setText(this.persona.getTag());
        }
    }

    private void cerrar() {
        RegistroIButtonViewController.instance = null;
        this.setVisible(false);
        this.dispose();
    }

    private void mostrarTeclado() {
        jPanel1.setVisible(true);
    }

    public void guardar() {
        String identificacion = juser.getText().trim();
        String tag = jtag.getText().trim();
        if (identificacion.equals("")) {
            juser.requestFocus();
        } else if (tag.equals("")) {
            mostrarPanelMensaje("POR FAVOR ACERQUE EL TAG IBUTTON",
                    "/com/firefuel/resources/btBad.png",
                    LetterCase.FIRST_UPPER_CASE);
        } else {
            long idPersonaRegistrada = RegistroIButtonViewController.guardarIdentificacion(identificacion, tag);
            if (idPersonaRegistrada == 0) {
                mostrarPanelMensaje("PERSONA INEXISTENTE",
                        "/com/firefuel/resources/btBad.png",
                        LetterCase.FIRST_UPPER_CASE);
            } else {
                sendRegistro(idPersonaRegistrada, tag);
                notifyAllDevices(identificacion, tag);
                mostrarPanelMensaje("IBUTTON ASIGNADO CORRECTAMENTE",
                        "/com/firefuel/resources/btOk.png",
                        LetterCase.FIRST_UPPER_CASE);
            }
        }
    }

    public void notifyAllDevices(String identificacion, String tag) {
        SurtidorDao sur = new SurtidorDao();
        JsonArray infoPosEstacion = new JsonArray();
        try {
            infoPosEstacion = obtenerInfoSurtidoresEstacionUseCase.execute();
        } catch (Exception ex) {
            Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        EquipoDao edao = new EquipoDao();
        String url;
        for (JsonElement element : infoPosEstacion) {
            JsonObject jsonPOS = element.getAsJsonObject();
            String host = jsonPOS.get("host").getAsString().trim();
            url = "http://" + host + ":" + NovusConstante.PORT_SERVER_WS_API + "/api/registroIdentificacion";
            if (!(Utils.getIpLocal().trim().equals(host))) {
                try {
                    edao.guardarTransmision(Main.credencial,
                            "{ \"medio\": \"ibutton\",\n"
                            + "    \"identificacion\": \"" + identificacion + "\",\n"
                            + "    \"tag\": \"" + tag + "\"}",
                            url, NovusConstante.POST);
                } catch (DAOException ex) {
                    Logger.getLogger(RegistroIButtonViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static long guardarIdentificacion(String identificacion, String tag) {
        //PersonasDao pdao = new PersonasDao();
        //long idPersona = pdao.registrarTag(identificacion, tag);
        RegistrarTagUseCase registrarTagUseCase = new RegistrarTagUseCase();
        //return idPersona;
        return registrarTagUseCase.execute(identificacion, tag);
    }

    private void sendRegistro(long idPersona, String tag) {
        JsonObject json = new JsonObject();
        json.addProperty("identificadorPersona", idPersona);
        json.addProperty("identificador", tag);
        json.addProperty("origen", 3);
        json.addProperty("identificadorEmpresa", Main.credencial.getEmpresas_id());
        EquipoDao edao = new EquipoDao();
        try {
            edao.guardarTransmision(Main.credencial, json.toString(),
                    NovusConstante.getServer(NovusConstante.SECURE_END_POINT_ASIGNACION_TAG), NovusConstante.POST);
        } catch (DAOException ex) {
            Logger.getLogger(RegistroIButtonViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(RegistroIButtonViewController.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono, String letterCase) {

        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }

        Runnable runnable = () -> {
            cambiarPanelHome();
            cerrar();
        };

        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(true).setRunnable(runnable)
                .setLetterCase(letterCase).build();

        pnl_container.add("card_mensajes", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));

        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        Async(runnable, 3);
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "pnl_principal");
    }

}

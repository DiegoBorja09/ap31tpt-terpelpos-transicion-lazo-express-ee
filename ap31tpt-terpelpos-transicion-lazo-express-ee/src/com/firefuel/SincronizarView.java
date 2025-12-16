/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.controllers.NovusUtils;
import com.controllers.SetupAsync;
import com.controllers.NovusConstante;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Admin
 */
public class SincronizarView extends javax.swing.JDialog {

    SetupAsync sync = null;
    JFrame parent;
    InfoViewController base;
    boolean all = true;
    public static boolean enable = true;

    public SincronizarView(JFrame parent, boolean modal, boolean all) {
        super(parent, modal);
        this.all = all;
        this.parent = parent;
        if (this.all) {
            this.sync = new SetupAsync(null);
        }
        initComponents();
        this.init();
    }

    public SincronizarView(InfoViewController base, boolean modal, boolean all) {
        super(base, modal);
        this.all = all;
        this.base = base;
        if (this.all) {
            this.sync = new SetupAsync(null);
        }
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(this.getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        DefaultCaret caret = (DefaultCaret) jlog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.sincronizacionAutomatica();
    }

    public void sincronizarBodegas() {
        if (enable) {
            NovusUtils.beep();

            NovusUtils.printLn("SINCRONIZACION CONSECUTIVOS");
            this.sync = new SetupAsync(base, NovusConstante.DESCARGAR_BODEGAS_ID);
            this.sync.start();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbutton_sinctodo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jbutton_cerrar1 = new javax.swing.JLabel();
        jloader_categorias = new javax.swing.JLabel();
        jbutton_reintentar_categorias = new javax.swing.JLabel();
        jloader_surtidores = new javax.swing.JLabel();
        jloader_productos = new javax.swing.JLabel();
        jloader_bodega = new javax.swing.JLabel();
        jloader_kardex = new javax.swing.JLabel();
        jloader_medios = new javax.swing.JLabel();
        jloader_personal = new javax.swing.JLabel();
        jloader_empresa = new javax.swing.JLabel();
        jloaderDatafonos = new javax.swing.JLabel();
        jbutton_reintentar_kardex = new javax.swing.JLabel();
        jbuttonDatafono = new javax.swing.JLabel();
        jlabel_productos = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jbutton_reintentar_surtidores = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jbutton_cerrar = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jbuttonAuditoria = new javax.swing.JLabel();
        jbutton_reintentar_empresa = new javax.swing.JLabel();
        jbutton_reintentar_bodegas = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jbuttonSincronizarAforo = new javax.swing.JLabel();
        jbutton_reintentar_productos = new javax.swing.JLabel();
        jbutton_reintentar_medios = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblDatafonos = new javax.swing.JLabel();
        jbutton_reintentar_personas = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlog = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(1024, 600));
        setUndecorated(true);
        setSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(null);

        jbutton_sinctodo.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jbutton_sinctodo.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_sinctodo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_sinctodo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jbutton_sinctodo.setText("SINCRONIZAR TODO");
        jbutton_sinctodo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_sinctodo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_sinctodojLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_sinctodo);
        jbutton_sinctodo.setBounds(180, 720, 284, 60);

        jLabel2.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        jLabel2.setText("KARDEX");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(60, 450, 280, 50);

        jLabel19.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setText("CATEGORIAS");
        getContentPane().add(jLabel19);
        jLabel19.setBounds(60, 210, 200, 50);

        jbutton_cerrar1.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jbutton_cerrar1.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_cerrar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_cerrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jbutton_cerrar1.setText("LIMPIAR");
        jbutton_cerrar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_cerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_cerrar1jLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_cerrar1);
        jbutton_cerrar1.setBounds(870, 20, 190, 60);

        jloader_categorias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_categorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_categorias);
        jloader_categorias.setBounds(358, 206, 40, 60);

        jbutton_reintentar_categorias.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_categorias.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_categorias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_categorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_categorias.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_categorias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_categoriasjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_categorias);
        jbutton_reintentar_categorias.setBounds(470, 210, 110, 50);

        jloader_surtidores.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_surtidores);
        jloader_surtidores.setBounds(358, 386, 40, 60);

        jloader_productos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_productos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_productos);
        jloader_productos.setBounds(358, 326, 40, 60);

        jloader_bodega.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_bodega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_bodega);
        jloader_bodega.setBounds(358, 266, 40, 60);

        jloader_kardex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_kardex);
        jloader_kardex.setBounds(358, 446, 40, 60);

        jloader_medios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_medios);
        jloader_medios.setBounds(358, 566, 40, 60);

        jloader_personal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_personal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_personal);
        jloader_personal.setBounds(358, 506, 40, 60);

        jloader_empresa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloader_empresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        getContentPane().add(jloader_empresa);
        jloader_empresa.setBounds(358, 146, 40, 60);

        jloaderDatafonos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jloaderDatafonos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        jloaderDatafonos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jloaderDatafonos);
        jloaderDatafonos.setBounds(359, 628, 40, 60);

        jbutton_reintentar_kardex.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_kardex.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_kardex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_kardex.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_kardex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_kardexjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_kardex);
        jbutton_reintentar_kardex.setBounds(470, 450, 110, 50);

        jbuttonDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbuttonDatafono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbuttonDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbuttonDatafono.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbuttonDatafonoMouseReleased(evt);
            }
        });
        getContentPane().add(jbuttonDatafono);
        jbuttonDatafono.setBounds(470, 630, 110, 50);

        jlabel_productos.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlabel_productos.setForeground(new java.awt.Color(186, 12, 47));
        jlabel_productos.setText("PRODUCTOS STORE");
        getContentPane().add(jlabel_productos);
        jlabel_productos.setBounds(60, 330, 260, 50);

        jLabel7.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(186, 12, 47));
        jLabel7.setText("SURTIDORES");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(60, 390, 270, 50);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel29);
        jLabel29.setBounds(1130, 710, 10, 80);

        jbutton_reintentar_surtidores.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_surtidores.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_surtidores.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_surtidores.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_surtidores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_surtidoresjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_surtidores);
        jbutton_reintentar_surtidores.setBounds(470, 390, 110, 50);

        jLabel5.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setText("PERSONAL");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(60, 510, 280, 50);

        jbutton_cerrar.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jbutton_cerrar.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_cerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_cerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jbutton_cerrar.setText("CERRAR");
        jbutton_cerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_cerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_cerrar);
        jbutton_cerrar.setBounds(1060, 20, 190, 60);

        jLabel13.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(186, 12, 47));
        jLabel13.setText("DATOS BASICOS");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(60, 160, 200, 30);

        jLabel14.setFont(new java.awt.Font("Conthrax", 1, 36)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("SINCRONIZACION");
        getContentPane().add(jLabel14);
        jLabel14.setBounds(20, 0, 500, 90);

        jbuttonAuditoria.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jbuttonAuditoria.setForeground(new java.awt.Color(255, 255, 255));
        jbuttonAuditoria.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbuttonAuditoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jbuttonAuditoria.setText("AUDITORIA");
        jbuttonAuditoria.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbuttonAuditoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbuttonAuditoriajLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbuttonAuditoria);
        jbuttonAuditoria.setBounds(820, 720, 280, 60);

        jbutton_reintentar_empresa.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_empresa.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_empresa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_empresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_empresa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_empresa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_empresajLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_empresa);
        jbutton_reintentar_empresa.setBounds(470, 150, 110, 50);

        jbutton_reintentar_bodegas.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_bodegas.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_bodegas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_bodegas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_bodegas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_bodegas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_bodegasjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_bodegas);
        jbutton_reintentar_bodegas.setBounds(470, 270, 110, 50);

        jLabel18.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(186, 12, 47));
        jLabel18.setText("BODEGAS");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(60, 270, 270, 50);

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel21);
        jLabel21.setBounds(350, 627, 60, 60);

        jbuttonSincronizarAforo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jbuttonSincronizarAforo.setForeground(new java.awt.Color(255, 255, 255));
        jbuttonSincronizarAforo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbuttonSincronizarAforo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jbuttonSincronizarAforo.setText("SINCRONIZAR AFORO");
        jbuttonSincronizarAforo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbuttonSincronizarAforo.setVerifyInputWhenFocusTarget(false);
        jbuttonSincronizarAforo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbuttonSincronizarAforoMouseReleased(evt);
            }
        });
        getContentPane().add(jbuttonSincronizarAforo);
        jbuttonSincronizarAforo.setBounds(510, 720, 280, 60);

        jbutton_reintentar_productos.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_productos.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_productos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_productos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_productos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_productos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_productosjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_productos);
        jbutton_reintentar_productos.setBounds(470, 330, 110, 50);

        jbutton_reintentar_medios.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_medios.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_medios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_medios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_medios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_mediosjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_medios);
        jbutton_reintentar_medios.setBounds(470, 570, 110, 50);

        jLabel6.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("MEDIOS PAGO");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(60, 570, 280, 50);

        lblDatafonos.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        lblDatafonos.setForeground(new java.awt.Color(186, 12, 47));
        lblDatafonos.setText("DATAFONOS");
        getContentPane().add(lblDatafonos);
        lblDatafonos.setBounds(60, 630, 280, 50);

        jbutton_reintentar_personas.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jbutton_reintentar_personas.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_reintentar_personas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_personas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        jbutton_reintentar_personas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_reintentar_personas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_reintentar_personasjLabel11MouseReleased(evt);
            }
        });
        getContentPane().add(jbutton_reintentar_personas);
        jbutton_reintentar_personas.setBounds(470, 510, 110, 50);

        jlog.setEditable(false);
        jlog.setColumns(20);
        jlog.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jlog.setRows(5);
        jlog.setBorder(null);
        jScrollPane1.setViewportView(jlog);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(670, 160, 560, 510);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(350, 386, 60, 60);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(350, 146, 60, 60);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel8);
        jLabel8.setBounds(350, 206, 60, 60);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("REGISTRO ");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(690, 100, 250, 50);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel9);
        jLabel9.setBounds(350, 266, 60, 60);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel10);
        jLabel10.setBounds(350, 566, 60, 60);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel11);
        jLabel11.setBounds(350, 326, 60, 60);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("ESTADO");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(330, 100, 100, 50);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel12);
        jLabel12.setBounds(350, 446, 60, 60);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndEstadoSincro.png"))); // NOI18N
        getContentPane().add(jLabel15);
        jLabel15.setBounds(350, 506, 60, 60);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("DESCRIPCION");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(60, 100, 250, 50);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndSincronizacion.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbuttonAuditoriajLabel11MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbuttonAuditoriajLabel11MouseReleased
        NotificacionLogs notiLogs = new NotificacionLogs(parent, true);
        notiLogs.setVisible(true);
    }//GEN-LAST:event_jbuttonAuditoriajLabel11MouseReleased

    private void jbuttonSincronizarAforoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbuttonSincronizarAforoMouseReleased
        NovusUtils.beep();
        this.sync = new SetupAsync(base, NovusConstante.SINCRONIZAR_AFORO);
        this.sync.start();

        jbuttonSincronizarAforo.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/botones/bt-link-normal.png")));

        setTimeout(2, () -> {
            jbuttonSincronizarAforo.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                    .getResource("/com/firefuel/resources/botones/bt-danger-normal.png")));
        });
    }//GEN-LAST:event_jbuttonSincronizarAforoMouseReleased

    private void jbuttonDatafonoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbuttonDatafonoMouseReleased
        if (enable) {
            NovusUtils.beep();
            this.sync = new SetupAsync(NovusConstante.SINCRONIZAR_DATAFONOS);
            this.sync.start();
        }
    }//GEN-LAST:event_jbuttonDatafonoMouseReleased

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();
            this.setVisible(false);
            if (this.parent instanceof RegistroEquipoView) {
                this.parent.setVisible(false);
                this.parent.dispose();
               // Main.info = new InfoViewController();
//                Main.info = this;
//                Main.info.setVisible(true);
                this.setVisible(true);
            }
            this.dispose();
        }
    }// GEN-LAST:event_jLabel11MouseReleased

    public static void activarVista(boolean activar) {
        enable = activar;
        jbutton_cerrar.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/botones/bt-" + (activar ? "danger" : "link") + "-small.png")));
        jbutton_sinctodo.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/botones/bt-" + (activar ? "danger" : "link") + "-normal.png")));
        jbutton_reintentar_empresa.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_bodegas.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_productos.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_medios.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_personas.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_surtidores.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_kardex.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbutton_reintentar_categorias.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
        jbuttonDatafono.setIcon(new javax.swing.ImageIcon(SincronizarView.class
                .getResource("/com/firefuel/resources/bt" + (activar ? "Reintentar" : "Inactivo") + ".png")));
    }

    private void jbutton_reintentar_empresajLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_empresajLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();
            this.sync = new SetupAsync(NovusConstante.DESCARGAR_DATOS_BASICOS_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_empresajLabel11MouseReleased

    private void jbutton_reintentar_bodegasjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_bodegasjLabel11MouseReleased
        sincronizarBodegas();
    }// GEN-LAST:event_jbutton_reintentar_bodegasjLabel11MouseReleased

    private void jbutton_reintentar_productosjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_productosjLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();
            this.sync = new SetupAsync(NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_productosjLabel11MouseReleased

    private void jbutton_reintentar_mediosjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_mediosjLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();

            this.sync = new SetupAsync(NovusConstante.DESCARGAR_MEDIOS_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_mediosjLabel11MouseReleased

    private void jbutton_reintentar_personasjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_personasjLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();

            this.sync = new SetupAsync(NovusConstante.DESCARGAR_PERSONAS_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_personasjLabel11MouseReleased

    private void jbutton_reintentar_surtidoresjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_surtidoresjLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();
            this.sync = new SetupAsync(base, NovusConstante.DESCARGAR_SURTIDORES_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_surtidoresjLabel11MouseReleased

    private void jbutton_reintentar_kardexjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_kardexjLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();

            this.sync = new SetupAsync(NovusConstante.DESCARGAR_KARDEX_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_kardexjLabel11MouseReleased

    private void jbutton_reintentar_categoriasjLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_reintentar_categoriasjLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();
            this.sync = new SetupAsync(NovusConstante.DESCARGAR_CATEGORIAS_ID);
            this.sync.start();
        }
    }// GEN-LAST:event_jbutton_reintentar_categoriasjLabel11MouseReleased

    private void jbutton_sinctodojLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_sinctodojLabel11MouseReleased
        if (enable) {
            NovusUtils.beep();
            this.sync = new SetupAsync(null);
            this.all = true;
            sincronizacionAutomatica();
        }
    }// GEN-LAST:event_jbutton_sinctodojLabel11MouseReleased

    private void jbutton_cerrar1jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_cerrar1jLabel11MouseReleased
        jlog.setText("");
    }// GEN-LAST:event_jbutton_cerrar1jLabel11MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JLabel jbuttonAuditoria;
    public static javax.swing.JLabel jbuttonDatafono;
    private javax.swing.JLabel jbuttonSincronizarAforo;
    public static javax.swing.JLabel jbutton_cerrar;
    public static javax.swing.JLabel jbutton_cerrar1;
    public static javax.swing.JLabel jbutton_reintentar_bodegas;
    public static javax.swing.JLabel jbutton_reintentar_categorias;
    public static javax.swing.JLabel jbutton_reintentar_empresa;
    public static javax.swing.JLabel jbutton_reintentar_kardex;
    public static javax.swing.JLabel jbutton_reintentar_medios;
    public static javax.swing.JLabel jbutton_reintentar_personas;
    public static javax.swing.JLabel jbutton_reintentar_productos;
    public static javax.swing.JLabel jbutton_reintentar_surtidores;
    public static javax.swing.JLabel jbutton_sinctodo;
    public static javax.swing.JLabel jlabel_productos;
    public static javax.swing.JLabel jloaderDatafonos;
    public static javax.swing.JLabel jloader_bodega;
    public static javax.swing.JLabel jloader_categorias;
    public static javax.swing.JLabel jloader_empresa;
    public static javax.swing.JLabel jloader_kardex;
    public static javax.swing.JLabel jloader_medios;
    public static javax.swing.JLabel jloader_personal;
    public static javax.swing.JLabel jloader_productos;
    public static javax.swing.JLabel jloader_surtidores;
    public static javax.swing.JTextArea jlog;
    private javax.swing.JLabel lblDatafonos;
    // End of variables declaration//GEN-END:variables

    private void sincronizacionAutomatica() {
        if (this.all) {
            sync.start();
            this.all = false;
        }
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout"
                        + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}

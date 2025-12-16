/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.commons.CtWacherParametrosEnum;
import com.application.commons.DTO.FindByParameterDTO;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.useCases.datafonos.AdvertenciaDeNotificacionDeCierreTurnoUseCase;
import com.application.useCases.wacherparametros.FindWacherDynamicUseCase;
import com.bean.Notificador;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.DatafonosDao;
import com.dao.EquipoDao;
import com.facade.BodegasFacade;
import com.firefuel.utils.ImageCache;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.Timer;
import com.application.useCases.shiftReports.PrintFinTurnoRemoteUseCase;
import com.domain.dto.shiftReports.ShiftReportResult;

/**
 *
 * @author usuario
 */
public class TurnoMenuPanelController extends javax.swing.JPanel {

    public AdvertenciaDeNotificacionDeCierreTurnoUseCase advertenciaDeNotificacionDeCierreTurnoUseCase;
    InfoViewController vistaPrincipal;
    FindWacherDynamicUseCase findWacherDynamicUseCase;
    boolean activoMultisurtidores = false;
    Notificador notificacion = null;
    Timer timer = null;

    void validarMultiTurnos() {
        this.activoMultisurtidores = this.findWacherDynamicUseCase.execute(new FindByParameterDTO(CtWacherParametrosEnum.CODIGO.getColumnName(),NovusConstante.PREFERENCE_MULTISURTIDORES));
        NovusUtils.printLn("com.firefuel.TurnoMenuPanelController.validarMultiTurnos() " + this.activoMultisurtidores);
    }

    public TurnoMenuPanelController(InfoViewController vistaPrincipal) {
        this.vistaPrincipal = vistaPrincipal;
        initComponents();
        this.init();
    }


    void init() {
        bloquearBotonCierre();
        this.findWacherDynamicUseCase = new FindWacherDynamicUseCase();
        NovusUtils.ajusteFuente(this.getComponents(), Font.BOLD);
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jLabel18, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jLabel19, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jLabel21, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jLabel24, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jLabel29, NovusConstante.EXTRABOLD);
        this.validarMultiTurnos();
        jNotificacion_Turno.setVisible(false);
        if(Main.SIN_SURTIDOR){
            jLabel33.setVisible(false);
            jLabel32.setVisible(false);
            jLabel34.setVisible(false);
            jLabel34.setEnabled(false);
            btn_cerrarEstacion.setEnabled(false);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        this.advertenciaDeNotificacionDeCierreTurnoUseCase = new AdvertenciaDeNotificacionDeCierreTurnoUseCase();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jNotificacion_Turno = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        confirmar_cierre = new javax.swing.JPanel();
        jMensaje = new javax.swing.JLabel();
        btn_cerrarEstacion = new javax.swing.JLabel();
        btn_cerrarIsla = new javax.swing.JLabel();
        jCerrar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        mensajes = new javax.swing.JPanel();
        MensajesN = new javax.swing.JLabel();
        iconoN = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setLayout(null);

        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel2.setLayout(null);

        jLabel32.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(186, 12, 47));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("6");
        jPanel2.add(jLabel32);
        jLabel32.setBounds(40, 570, 70, 70);

        jLabel33.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("MEDIDAS TANQUES");
        jPanel2.add(jLabel33);
        jLabel33.setBounds(130, 570, 270, 70);

        jLabel34.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_submenu.png")); 
        jLabel34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel34MousePressed(evt);
            }
        });
        jPanel2.add(jLabel34);
        jLabel34.setBounds(30, 570, 390, 70);

        jLabel29.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(186, 12, 47));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("5");
        jPanel2.add(jLabel29);
        jLabel29.setBounds(40, 470, 70, 70);

        jLabel30.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("CONSOLIDADO TURNO");
        jPanel2.add(jLabel30);
        jLabel30.setBounds(130, 470, 270, 70);

        jLabel31.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_submenu.png"));
        jLabel31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel31MousePressed(evt);
            }
        });
        jPanel2.add(jLabel31);
        jLabel31.setBounds(30, 470, 390, 70);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_home.png"));
        jLabel1.setAlignmentY(0.0F);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel1);
        jLabel1.setBounds(1190, 10, 90, 70);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(ImageCache.getImage("/com/firefuel/resources/icono_triangulo_amarillo.png"));
        jPanel2.add(jLabel16);
        jLabel16.setBounds(0, 5, 90, 80);

        jLabel21.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("3");
        jPanel2.add(jLabel21);
        jLabel21.setBounds(37, 290, 70, 70);

        jLabel22.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("INFORMACION TURNO");
        jPanel2.add(jLabel22);
        jLabel22.setBounds(130, 290, 270, 70);

        jLabel14.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_submenu.png"));
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel14MousePressed(evt);
            }
        });
        jPanel2.add(jLabel14);
        jLabel14.setBounds(30, 290, 390, 70);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(ImageCache.getImage("/com/firefuel/resources/icono_lg_turno.png"));
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel13);
        jLabel13.setBounds(700, 210, 360, 380);

        jLabel19.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("2");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(37, 200, 70, 70);

        jLabel20.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("FINALIZAR TURNO");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(130, 200, 270, 70);

        jLabel10.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_submenu.png"));
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel10MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel10);
        jLabel10.setBounds(30, 200, 390, 70);

        jLabel15.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("INICIAR TURNO");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(130, 110, 270, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        jPanel2.add(jLabel28);
        jLabel28.setBounds(1170, 3, 10, 80);

        jLabel18.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(186, 12, 47));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("1");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(37, 110, 70, 70);

        jLabel9.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_submenu.png"));
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel9MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel9);
        jLabel9.setBounds(30, 110, 390, 70);

        jLabel23.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("SOBRES");
        jPanel2.add(jLabel23);
        jLabel23.setBounds(130, 380, 270, 70);

        jNotificacion_Turno.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion_Turno.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(jNotificacion_Turno);
        jNotificacion_Turno.setBounds(140, 720, 980, 70);

        jLabel24.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(186, 12, 47));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("4");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(40, 380, 70, 70);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTitle.setText("TURNOS");
        jPanel2.add(jTitle);
        jTitle.setBounds(90, 0, 490, 85);

        jLabel17.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_submenu.png"));
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel17MousePressed(evt);
            }
        });
        jPanel2.add(jLabel17);
        jLabel17.setBounds(30, 380, 390, 70);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        jPanel2.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        jPanel2.add(jLabel26);
        jLabel26.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        jPanel2.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel12.setIcon(ImageCache.getImage("/com/firefuel/resources/fnd_submenu_blanco.png"));
        jLabel12.setOpaque(true);
        jPanel2.add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel2, "menu_panel");

        confirmar_cierre.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMensaje.setBackground(new java.awt.Color(186, 12, 47));
        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("¿QUE DESEA CERRAR?");
        confirmar_cierre.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 250, 1280, 160));

        btn_cerrarEstacion.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btn_cerrarEstacion.setForeground(new java.awt.Color(255, 255, 255));
        btn_cerrarEstacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_cerrarEstacion.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-normal.png"));
        btn_cerrarEstacion.setText("CERRAR ESTACION");
        btn_cerrarEstacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_cerrarEstacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_cerrarEstacionMouseClicked(evt);
            }
        });
        confirmar_cierre.add(btn_cerrarEstacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 480, 350, 60));

        btn_cerrarIsla.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btn_cerrarIsla.setForeground(new java.awt.Color(255, 255, 255));
        btn_cerrarIsla.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_cerrarIsla.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-normal.png"));
        btn_cerrarIsla.setText("CERRAR ISLA");
        btn_cerrarIsla.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_cerrarIsla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_cerrarIslaMouseClicked(evt);
            }
        });
        confirmar_cierre.add(btn_cerrarIsla, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 480, 350, 60));

        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(ImageCache.getImage("/com/firefuel/resources/BtCerrar.png"));
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        confirmar_cierre.add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 0, 140, 80));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        jLabel2.setIcon(ImageCache.getImage("/com/firefuel/resources/fndRumbo.png"));
        confirmar_cierre.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        pnlPrincipal.add(confirmar_cierre, "confirmar_cierre");

        mensajes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        MensajesN.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        MensajesN.setForeground(new java.awt.Color(186, 12, 47));
        MensajesN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MensajesN.setText("OK");
        mensajes.add(MensajesN, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 300, 920, 140));

        iconoN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconoN.setIcon(ImageCache.getImage("/com/firefuel/resources/loader_fac.gif"));
        mensajes.add(iconoN, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 250, 260));

        jLabel3.setIcon(ImageCache.getImage("/com/firefuel/resources/fndRumbo.png"));
        mensajes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        pnlPrincipal.add(mensajes, "mensajes");

        add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    void inactivarOpcionConsolidado() {
        if (!this.activoMultisurtidores) {
            this.jLabel29.setForeground(Color.WHITE);
            this.jLabel30.setForeground(Color.WHITE);
            this.jLabel31.setEnabled(this.activoMultisurtidores);
        }

    }

    void bloquearBotonCierre() {
        /*
        * Migracion de la logica de la funcion a un UseCase
        *DatafonosDao datafonosDao = new DatafonosDao();
        *JsonObject data = datafonosDao.advertenciaDeNotificacionDeCierreTurno();
        */
        JsonObject data = advertenciaDeNotificacionDeCierreTurnoUseCase.execute();
        System.out.println("🔍 [CIERRE_TURNO] Data: " + data);
        if(data == null){   
            jLabel10.setEnabled(true);
            jLabel19.setEnabled(true);
            jLabel20.setEnabled(true);
        }
        if (data.get("cerrarTurno").getAsBoolean()) {
            jLabel10.setEnabled(false);
            jLabel19.setEnabled(false);
            jLabel20.setEnabled(false);
        } else {
            jLabel10.setEnabled(true);
            jLabel19.setEnabled(true);
            jLabel20.setEnabled(true);
        }
    }

    private void jLabel31MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel31MousePressed
        if (jLabel31.isEnabled()) {
            try {
                abriVistaInformeTurnoConsolidado();
            } catch (DAOException ex) {
                Logger.getLogger(TurnoMenuPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }// GEN-LAST:event_jLabel31MousePressed

    private void jLabel34MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel34MousePressed
        abrirVistaMedidasTanques();
    }// GEN-LAST:event_jLabel34MousePressed

    private void jLabel17MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel17MousePressed
        abrirVistaSobres();
    }// GEN-LAST:event_jLabel17MousePressed

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel10MouseReleased

    private void btn_cerrarIslaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cerrarIslaMouseClicked
        cerrarTurno(false);
    }//GEN-LAST:event_btn_cerrarIslaMouseClicked

    private void btn_cerrarEstacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cerrarEstacionMouseClicked
        cerrarTurno(true);
    }//GEN-LAST:event_btn_cerrarEstacionMouseClicked

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        mostrarPanel("menu_panel");
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jLabel14MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel14MousePressed
        abriVistaInformeTurno();
    }// GEN-LAST:event_jLabel14MousePressed

    private void jLabel10MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MousePressed
        if (jLabel10.isEnabled()) {
            abrirVistaFinTurno();
        }
    }// GEN-LAST:event_jLabel10MousePressed

    private void jLabel9MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel9MouseReleased
        abrirVistaInicioTurno();
    }// GEN-LAST:event_jLabel9MouseReleased

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        cerrarsubmenu();
    }// GEN-LAST:event_jLabel1MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel MensajesN;
    private javax.swing.JLabel btn_cerrarEstacion;
    private javax.swing.JLabel btn_cerrarIsla;
    private javax.swing.JPanel confirmar_cierre;
    private javax.swing.JLabel iconoN;
    private javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jNotificacion_Turno;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel mensajes;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    void abrirVistaMedidasTanques() {
        try {
            boolean independiente = true;
            EquipoDao edao = new EquipoDao();
            Long id = edao.findEmpresaId();

            setTimeout(1, () -> {
                BodegasFacade.fetchTanksMeasureTeoric(id);
            });

            LecturasTanquesViewController tanqueView = LecturasTanquesViewController.getInstance(this.vistaPrincipal, true,
                    independiente);
            AutorizacionView auto = new AutorizacionView(vistaPrincipal, true, tanqueView);
            auto.setVisible(true);
        } catch (DAOException ex) {
            Logger.getLogger(TurnoMenuPanelController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            NovusUtils.printLn("RECARGANO PERSONA-> EN MODULO DE TURNO");
            this.vistaPrincipal.recargarPersona();
        }
    }

    private void abrirVistaInicioTurno() {
        NovusUtils.beep();
        this.vistaPrincipal.recargarPersona();

        /*
        boolean cargarVistaTanques = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_MEDIDAS_TANQUES, true);
        boolean multisurtidores = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_MULTISURTIDORES, false);
        */
        boolean cargarVistaTanques = findWacherDynamicUseCase.execute(new FindByParameterDTO(CtWacherParametrosEnum.CODIGO.getColumnName(),NovusConstante.PREFERENCE_MEDIDAS_TANQUES));
        boolean multisurtidores = findWacherDynamicUseCase.execute(new FindByParameterDTO(CtWacherParametrosEnum.CODIGO.getColumnName(),NovusConstante.PREFERENCE_MULTISURTIDORES));
        if (!Main.SIN_SURTIDOR && Main.persona == null && multisurtidores) {
            InicioSurtidorView surtidorView = new InicioSurtidorView(this.vistaPrincipal, true, cargarVistaTanques);
            surtidorView.setVisible(true);
        } else {
            if (!Main.SIN_SURTIDOR  && cargarVistaTanques && Main.persona == null) {
                LecturasTanquesViewController tanqueView = LecturasTanquesViewController.getInstance(this.vistaPrincipal,
                        true);
                tanqueView.setVisible(true);
            } else {
                InfoViewController.instanciaInicioTurno = TurnosIniciarViewController.getInstance(this.vistaPrincipal, true);
                InfoViewController.instanciaInicioTurno.setVisible(true);
            }
        }
    }



    private void enviarFinTurno(boolean cierreEstacion) {
        System.out.println("🔍 [CIERRE_TURNO] Iniciando proceso de fin de turno");
        System.out.println("🔍 [CIERRE_TURNO] Cierre estación: " + cierreEstacion);
        
        JsonObject obj = new JsonObject();
        obj.addProperty("cierreEstacion", cierreEstacion);
        obj.addProperty("promotorId", Main.persona.getId());
        obj.addProperty("promotor", Main.persona.getNombre());
        
        System.out.println("🔍 [CIERRE_TURNO] Datos del promotor - ID: " + Main.persona.getId() + ", Nombre: " + Main.persona.getNombre());
        
        boolean ENABLE_DEBUG = true;
        String url = NovusConstante.SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_FIN;
        System.out.println("🔍 [CIERRE_TURNO] Enviando petición a: " + url);
        
        ClientWSAsync async = new ClientWSAsync("FINALIZAR JORNADAS", url, NovusConstante.POST, obj.toString(),
                ENABLE_DEBUG);
        async.esperaRespuesta();
        
        System.out.println("🔍 [CIERRE_TURNO] Status de la respuesta: " + async.getStatus());
        
        if (async.getStatus() == 200) {
            System.out.println("✅ [CIERRE_TURNO] Fin de turno enviado exitosamente");
            cargando("En unos instantes empezará el cierre , por favor espere.", "/com/firefuel/resources/btOk.png");
            
            // Llamar al nuevo servicio independiente de impresión de fin de turno
            try {
                NovusUtils.printLn("========================================");
                NovusUtils.printLn("[FIN TURNO] Iniciando proceso de impresión desde TurnoMenuPanelController");
                NovusUtils.printLn("========================================");
                
                // Obtener datos necesarios para la impresión
                final long identificadorJornada;
                if (Main.persona != null && Main.persona.getGrupoJornadaId() > 0) {
                    identificadorJornada = Main.persona.getGrupoJornadaId();
                } else {
                    NovusUtils.printLn("[FIN TURNO] WARNING: No se pudo obtener grupo_jornada, usando 0");
                    identificadorJornada = 0;
                }
                
                final long promotorId = Main.persona != null ? Main.persona.getId() : 0;
                
                final int posId;
                if (Main.credencial != null && Main.credencial.getEquipos_id() != null) {
                    long equiposId = Main.credencial.getEquipos_id();
                    if (equiposId > 0 && equiposId <= Integer.MAX_VALUE) {
                        posId = (int) equiposId;
                    } else {
                        posId = 1; // Default
                    }
                } else {
                    posId = 1; // Default
                }
                
                NovusUtils.printLn("[FIN TURNO] Parámetros de impresión:");
                NovusUtils.printLn("  - Identificador Jornada (grupo_jornada): " + identificadorJornada);
                NovusUtils.printLn("  - Identificador Promotor: " + promotorId);
                NovusUtils.printLn("  - POS ID: " + posId);
                
                if (identificadorJornada > 0 && promotorId > 0) {
                    // Ejecutar impresión en thread separado para no bloquear la UI
                    new Thread(() -> {
                        try {
                            NovusUtils.printLn("[FIN TURNO] Thread iniciado, creando Use Case...");
                            PrintFinTurnoRemoteUseCase useCase = 
                                new PrintFinTurnoRemoteUseCase(
                                    identificadorJornada,
                                    promotorId,
                                    posId
                                );
                            
                            NovusUtils.printLn("[FIN TURNO] Ejecutando Use Case...");
                            long startTime = System.currentTimeMillis();
                            ShiftReportResult result = useCase.execute(null);
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;
                            
                            NovusUtils.printLn("[FIN TURNO] Use Case completado en " + duration + " ms");
                            
                            if (result.isSuccess()) {
                                NovusUtils.printLn("[FIN TURNO] ✓ Ticket impreso exitosamente");
                                NovusUtils.printLn("[FIN TURNO] Mensaje: " + result.getMessage());
                            } else {
                                NovusUtils.printLn("[FIN TURNO] ✗ Error al imprimir: " + result.getMessage());
                            }
                            NovusUtils.printLn("========================================");
                        } catch (Exception e) {
                            NovusUtils.printLn("[FIN TURNO] ✗ ERROR INESPERADO: " + e.getMessage());
                            e.printStackTrace();
                            NovusUtils.printLn("========================================");
                        }
                    }).start();
                    NovusUtils.printLn("[FIN TURNO] Thread de impresión iniciado");
                } else {
                    NovusUtils.printLn("[FIN TURNO] ✗ ERROR: Datos insuficientes para imprimir (identificadorJornada=" + identificadorJornada + ", promotorId=" + promotorId + ")");
                }
            } catch (Exception e) {
                NovusUtils.printLn("[FIN TURNO] ✗ ERROR al programar impresión: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ [CIERRE_TURNO] Error al enviar fin de turno. Status: " + async.getStatus());
        }
        
        timer = new Timer(2000, (e) -> {
            System.out.println("🔍 [CIERRE_TURNO] Ejecutando timer de cierre");
            closed();
            Main.info.cerrarSubmenu();
        });
        timer.start();
    }

    private void ejecutaLoginRemoto(boolean cierreEstacion) {
        String mensajeCierre = "Cerrando Isla ...";
        System.out.println("🔍 [CIERRE_TURNO] Iniciando login remoto para cierre");
        System.out.println("🔍 [CIERRE_TURNO] Cierre estación: " + cierreEstacion);


        if (cierreEstacion) {
            mensajeCierre = "<html>Enviando notificacion de cierre <br/>a toda la estación <br/>por favor espere ...</html>";
            System.out.println("🔍 [CIERRE_TURNO] Cierre de toda la estación");
        }
        
        cargando(mensajeCierre, "/com/firefuel/resources/loader_fac.gif");
        timer = new Timer(4000, (e) -> {
            System.out.println("🔍 [CIERRE_TURNO] Timer de login remoto completado");
            closed();
            enviarFinTurno(cierreEstacion);
        });
        timer.start();
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }


    private void abriVistaInformeTurnoConsolidado() throws DAOException {
        NovusUtils.beep();
        this.vistaPrincipal.recargarPersona();
        if (Main.persona == null) {
            vistaPrincipal.mostrarPanelSinTurnos();
        } else {
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this.vistaPrincipal, NovusConstante.SELECCION_PROMOTOR_INFORMACION_TURNOS_CONSOLIDADOS);
                seleccionpromotor.setVisible(true);

            } else {
                TurnosInformeConsolidadoViewController turno;
                turno = new TurnosInformeConsolidadoViewController(this.vistaPrincipal, true);
                turno.setVisible(true);
            }
        }
    }

    private void abriVistaInformeTurno() {
        NovusUtils.beep();
        this.vistaPrincipal.recargarPersona();
        if (Main.persona == null) {
            vistaPrincipal.mostrarPanelSinTurnos();
        } else {
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this.vistaPrincipal,
                        NovusConstante.SELECCION_PROMOTOR_INFORMACION_TURNOS);
                seleccionpromotor.setVisible(true);

            } else {
                TurnosInformeViewController turno;
                turno = new TurnosInformeViewController(this.vistaPrincipal, true);
                turno.setVisible(true);
            }
        }
    }

    private void abrirVistaFinTurno() {
        System.out.println("🔍 [CIERRE_TURNO] Abriendo vista de fin de turno");
        NovusUtils.beep();
        boolean multisurtidores = false;
        
        if (Main.parametrosCore.containsKey(NovusConstante.PREFERENCE_MULTISURTIDORES)
                && Main.parametrosCore.get(NovusConstante.PREFERENCE_MULTISURTIDORES).equals("S")) {
            multisurtidores = true;
            System.out.println("🔍 [CIERRE_TURNO] Modo multisurtidores activado");
        }

        this.vistaPrincipal.recargarPersona();
        if (Main.persona == null) {
            System.out.println("❌ [CIERRE_TURNO] No hay persona activa en el turno");
            vistaPrincipal.mostrarPanelSinTurnos();
        } else {
            System.out.println("🔍 [CIERRE_TURNO] Persona activa: " + Main.persona.getNombre());
            if (multisurtidores) {
                System.out.println("🔍 [CIERRE_TURNO] Mostrando panel de confirmación de cierre");
                mostrarPanel("confirmar_cierre");
                // Ejecutar cierre automático después de mostrar el panel
                System.out.println("🔍 [CIERRE_TURNO] Ejecutando cierre automático en modo multisurtidores");
                //enviarFinTurno(false);
            } else {
                System.out.println("🔍 [CIERRE_TURNO] Mostrando vista de finalización de turno");
                TurnosFinalizarViewController turno = TurnosFinalizarViewController.getInstance(vistaPrincipal, true);
                turno.setVisible(true);
            }
        }
    }

    private void abrirVistaSobres() {

        NovusUtils.beep();
        this.vistaPrincipal.recargarPersona();
        if (Main.persona == null) {
            vistaPrincipal.mostrarPanelSinTurnos();
        } else {

            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = new PromotorSeleccionView(this.vistaPrincipal,
                        NovusConstante.SELECCION_PROMOTOR_SOBRES);
                seleccionpromotor.setVisible(true);

            } else {
                SobresViewController sobres = new SobresViewController(this.vistaPrincipal, true);
                sobres.setVisible(true);
            }
        }
    }

    private void cerrarsubmenu() {
        this.setVisible(false);
        this.vistaPrincipal.mostrarOcultarMenu();
    }

    private void cerrarTurno(boolean estacion) {
        ejecutaLoginRemoto(estacion);
    }

    private void mostrarPanel(String Panel) {
        CardLayout card = (CardLayout) pnlPrincipal.getLayout();
        card.show(pnlPrincipal, Panel);
    }

    private void cargando(String mensaje, String imagen) {
        mostrarPanel("mensajes");
        MensajesN.setText(mensaje);
        iconoN.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
    }

    private void closed() {
        if (timer != null) {
            timer.stop();
        }
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

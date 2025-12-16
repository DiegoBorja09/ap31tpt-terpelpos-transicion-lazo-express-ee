/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.persons.IsAdminUseCase;
import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.PersonasDao;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.utils.ShowMessageSingleton;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author usuario
 */
public class VentaMenuPanelController extends javax.swing.JPanel {

    InfoViewController base;
    JFrame parent;
    IsAdminUseCase isAdminUseCase = new IsAdminUseCase();
    //PersonasDao pdao = new PersonasDao();
    boolean internet = false;

    String panelOpcion1 = "pnlOpcion1";
    String panelOpcion2 = "pnlOpcion2";

    public VentaMenuPanelController(InfoViewController base, JFrame parent) {
        this.parent = parent;
        this.base = base;
        initComponents();
        init();

    }

    private void deshabilitarFE() {
        FacturacionElectronica fac = new FacturacionElectronica();
        boolean aplicaFE = fac.aplicaFE();
        lblOpcion4.setEnabled(aplicaFE);
        lblTextOpcion4.setEnabled(aplicaFE);
        btnFactElectronica.setEnabled(aplicaFE);
        lblOpcion8.setEnabled(aplicaFE);
        lblTextOpcion8.setEnabled(aplicaFE);
        btnAnulaciones.setEnabled(aplicaFE);
        btnAnulaciones.setEnabled(aplicaFE);
        lblOpcion7.setEnabled(aplicaFE);
        lblTextOpcion7.setEnabled(aplicaFE);
        btnVentaManual.setEnabled(aplicaFE);
    }

    private void init() {
        deshabilitarFE();
        //desactivarVentaManual();
        NovusUtils.ajusteFuente(this.getComponents(), Font.BOLD);
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(lblOpcion1, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(lblOpcion2, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(lblOpcion3, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(lblOpcion4, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(lblOpcion8, NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(lblOpcion6, NovusConstante.EXTRABOLD);
        this.updateUI();

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        internet = base.hayInternet;
        this.renderizarUsuarios();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlMenuOpciones = new javax.swing.JPanel();
        pnlOpcion1 = new javax.swing.JPanel();
        lblOpcion1 = new javax.swing.JLabel();
        lblTextOpcion1 = new javax.swing.JLabel();
        btnConsultaVentas = new javax.swing.JLabel();
        lblOpcion2 = new javax.swing.JLabel();
        lblTextOpcion2 = new javax.swing.JLabel();
        btnPredeterminar = new javax.swing.JLabel();
        lblOpcion3 = new javax.swing.JLabel();
        lblTextOpcion3 = new javax.swing.JLabel();
        btnConsumoPropio = new javax.swing.JLabel();
        lblOpcion4 = new javax.swing.JLabel();
        lblTextOpcion4 = new javax.swing.JLabel();
        btnFactElectronica = new javax.swing.JLabel();
        lblOpcion5 = new javax.swing.JLabel();
        lblTextOpcion5 = new javax.swing.JLabel();
        btnConsultaVentasAppTerpel = new javax.swing.JLabel();
        lblOpcion6 = new javax.swing.JLabel();
        lblTextOpcion6 = new javax.swing.JLabel();
        btnHistorica = new javax.swing.JLabel();
        lblOpcion7 = new javax.swing.JLabel();
        lblTextOpcion7 = new javax.swing.JLabel();
        btnVentaManual = new javax.swing.JLabel();
        next = new javax.swing.JLabel();
        pnlOpcion2 = new javax.swing.JPanel();
        next1 = new javax.swing.JLabel();
        lblOpcion8 = new javax.swing.JLabel();
        lblTextOpcion8 = new javax.swing.JLabel();
        btnAnulaciones = new javax.swing.JLabel();
        jNotificacion_Venta = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        pnlPromotor = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();

        setLayout(null);

        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));
        jPanel1.setLayout(null);

        pnlMenuOpciones.setOpaque(false);
        pnlMenuOpciones.setPreferredSize(new java.awt.Dimension(460, 610));
        pnlMenuOpciones.setLayout(new java.awt.CardLayout());

        pnlOpcion1.setOpaque(false);
        pnlOpcion1.setLayout(null);

        lblOpcion1.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion1.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion1.setText("1");
        pnlOpcion1.add(lblOpcion1);
        lblOpcion1.setBounds(50, 10, 70, 70);

        lblTextOpcion1.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion1.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion1.setText("CONSULTA VENTAS");
        pnlOpcion1.add(lblTextOpcion1);
        lblTextOpcion1.setBounds(130, 10, 280, 70);

        btnConsultaVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnConsultaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnConsultaVentasMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnConsultaVentas);
        btnConsultaVentas.setBounds(40, 10, 390, 70);

        lblOpcion2.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion2.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion2.setText("2");
        pnlOpcion1.add(lblOpcion2);
        lblOpcion2.setBounds(50, 90, 70, 70);

        lblTextOpcion2.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion2.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion2.setText("PREDETERMINAR");
        pnlOpcion1.add(lblTextOpcion2);
        lblTextOpcion2.setBounds(130, 90, 280, 70);

        btnPredeterminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnPredeterminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnPredeterminarMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnPredeterminar);
        btnPredeterminar.setBounds(40, 90, 390, 70);

        lblOpcion3.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion3.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion3.setText("3");
        pnlOpcion1.add(lblOpcion3);
        lblOpcion3.setBounds(50, 170, 70, 70);

        lblTextOpcion3.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion3.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion3.setText("CONSUMO PROPIO");
        pnlOpcion1.add(lblTextOpcion3);
        lblTextOpcion3.setBounds(130, 170, 280, 70);

        btnConsumoPropio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnConsumoPropio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnConsumoPropioMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnConsumoPropio);
        btnConsumoPropio.setBounds(40, 170, 390, 70);

        lblOpcion4.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion4.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion4.setText("4");
        pnlOpcion1.add(lblOpcion4);
        lblOpcion4.setBounds(50, 250, 70, 70);

        lblTextOpcion4.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion4.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion4.setText("FAC. ELECTRONICA");
        pnlOpcion1.add(lblTextOpcion4);
        lblTextOpcion4.setBounds(130, 250, 280, 70);

        btnFactElectronica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnFactElectronica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnFactElectronicaMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnFactElectronica);
        btnFactElectronica.setBounds(40, 250, 390, 70);

        lblOpcion5.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion5.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion5.setText("5");
        pnlOpcion1.add(lblOpcion5);
        lblOpcion5.setBounds(50, 330, 70, 70);

        lblTextOpcion5.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion5.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion5.setText("VENTAS APP TERPEL");
        pnlOpcion1.add(lblTextOpcion5);
        lblTextOpcion5.setBounds(130, 330, 280, 70);

        btnConsultaVentasAppTerpel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnConsultaVentasAppTerpel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnConsultaVentasAppTerpelMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnConsultaVentasAppTerpel);
        btnConsultaVentasAppTerpel.setBounds(40, 330, 390, 70);

        lblOpcion6.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion6.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion6.setText("6");
        pnlOpcion1.add(lblOpcion6);
        lblOpcion6.setBounds(50, 410, 70, 70);

        lblTextOpcion6.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion6.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion6.setText("HISTORICA");
        pnlOpcion1.add(lblTextOpcion6);
        lblTextOpcion6.setBounds(130, 410, 280, 70);

        btnHistorica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnHistorica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHistoricaMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnHistorica);
        btnHistorica.setBounds(40, 410, 390, 70);

        lblOpcion7.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion7.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion7.setText("7");
        pnlOpcion1.add(lblOpcion7);
        lblOpcion7.setBounds(50, 490, 70, 70);

        lblTextOpcion7.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion7.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion7.setText("VENTA MANUAL");
        pnlOpcion1.add(lblTextOpcion7);
        lblTextOpcion7.setBounds(130, 490, 280, 70);

        btnVentaManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnVentaManual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnVentaManualMouseReleased(evt);
            }
        });
        pnlOpcion1.add(btnVentaManual);
        btnVentaManual.setBounds(40, 490, 390, 70);

        next.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/buttonPage.png"))); // NOI18N
        next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextMouseClicked(evt);
            }
        });
        pnlOpcion1.add(next);
        next.setBounds(150, 568, 162, 34);

        pnlMenuOpciones.add(pnlOpcion1, "pnlOpcion1");

        pnlOpcion2.setOpaque(false);
        pnlOpcion2.setLayout(null);

        next1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        next1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ButtonPageUp.png"))); // NOI18N
        next1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                next1MouseReleased(evt);
            }
        });
        pnlOpcion2.add(next1);
        next1.setBounds(140, 12, 162, 34);

        lblOpcion8.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lblOpcion8.setForeground(new java.awt.Color(186, 12, 47));
        lblOpcion8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpcion8.setText("8");
        pnlOpcion2.add(lblOpcion8);
        lblOpcion8.setBounds(40, 70, 70, 70);

        lblTextOpcion8.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        lblTextOpcion8.setForeground(new java.awt.Color(255, 255, 255));
        lblTextOpcion8.setText("ANULACIONES");
        pnlOpcion2.add(lblTextOpcion8);
        lblTextOpcion8.setBounds(120, 70, 280, 70);

        btnAnulaciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnAnulaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAnulacionesMouseReleased(evt);
            }
        });
        pnlOpcion2.add(btnAnulaciones);
        btnAnulaciones.setBounds(30, 70, 390, 70);

        pnlMenuOpciones.add(pnlOpcion2, "pnlOpcion2");

        jPanel1.add(pnlMenuOpciones);
        pnlMenuOpciones.setBounds(0, 91, 460, 610);

        jNotificacion_Venta.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        jNotificacion_Venta.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jNotificacion_Venta);
        jNotificacion_Venta.setBounds(130, 720, 1120, 70);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("VENTAS");
        jPanel1.add(jTitle);
        jTitle.setBounds(100, 0, 660, 83);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel1);
        jLabel1.setBounds(1180, 0, 100, 90);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        jPanel1.add(jLabel16);
        jLabel16.setBounds(10, 0, 80, 90);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_ventas.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jLabel13);
        jLabel13.setBounds(690, 220, 350, 360);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel27);
        jLabel27.setBounds(110, 710, 10, 80);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel1.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel30);
        jLabel30.setBounds(1170, 3, 10, 80);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel1, "principal");

        pnlPromotor.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("IDENTIFICACION");
        pnlPromotor.add(jLabel6);
        jLabel6.setBounds(310, 90, 180, 40);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PROMOTOR");
        pnlPromotor.add(jLabel8);
        jLabel8.setBounds(520, 90, 480, 40);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(800, 610));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel4);

        pnlPromotor.add(jScrollPane1);
        jScrollPane1.setBounds(240, 120, 840, 580);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("SELECCIONE EL PROMOTOR");
        pnlPromotor.add(jLabel15);
        jLabel15.setBounds(140, 720, 980, 70);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnlPromotor.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlPromotor.add(jLabel40);
        jLabel40.setBounds(10, 710, 100, 80);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel41);
        jLabel41.setBounds(120, 710, 10, 80);

        jLabel18.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("CONSULTA VENTAS");
        pnlPromotor.add(jLabel18);
        jLabel18.setBounds(100, 0, 680, 90);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel29MouseReleased(evt);
            }
        });
        pnlPromotor.add(jLabel29);
        jLabel29.setBounds(10, 10, 70, 71);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel42);
        jLabel42.setBounds(80, 10, 10, 68);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel43);
        jLabel43.setBounds(1130, 710, 10, 80);

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel44.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel44MousePressed(evt);
            }
        });
        pnlPromotor.add(jLabel44);
        jLabel44.setBounds(0, 0, 1281, 800);

        pnlPrincipal.add(pnlPromotor, "pnlPromotor");

        add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel29MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseReleased
        cerrar();
    }//GEN-LAST:event_jLabel29MouseReleased

    private void jLabel44MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel44MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel44MousePressed

    private void btnConsultaVentasAppTerpelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnConsultaVentasAppTerpelMouseReleased

        NovusUtils.beep();
        mostrarHistorialAppTerpel();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnConsultaVentasAppTerpelMouseReleased

    private void nextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextMouseClicked
        mostrarPanelOpciones(panelOpcion2);
    }//GEN-LAST:event_nextMouseClicked

    private void next1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_next1MouseReleased
        mostrarPanelOpciones(panelOpcion1);
    }//GEN-LAST:event_next1MouseReleased

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        cerrarsubmenu();
    }// GEN-LAST:event_jLabel1MouseReleased

    private void btnConsultaVentasMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MouseReleased
        mostrarVentas();
    }// GEN-LAST:event_jLabel10MouseReleased

    private void btnPredeterminarMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        mostrarVentasPredeterminadas();
    }// GEN-LAST:event_jLabel11MouseReleased

    private void btnConsumoPropioMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel14MouseReleased
        mostrarConsumoPropio();
    }// GEN-LAST:event_jLabel14MouseReleased

    private void btnFactElectronicaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel26MouseReleased
        try {
            if (btnFactElectronica.isEnabled()) {
                mostrarFacElectronica();
            }
        } catch (DAOException ex) {
            Logger.getLogger(VentaMenuPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// GEN-LAST:event_jLabel26MouseReleased

    private void btnAnulacionesMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel33MouseReleased
        if (btnAnulaciones.isEnabled()) {
            mostrarAnulacionVentas();
        }

    }// GEN-LAST:event_jLabel33MouseReleased

    private void btnHistoricaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel35MouseReleased
        mostrarVentasHistorica();
    }// GEN-LAST:event_jLabel35MouseReleased

    private void btnVentaManualMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel39MouseReleased
        if (btnVentaManual.isEnabled()) {
            NovusUtils.beep();
            base.recargarPersona();
            if (Main.persona == null) {
                base.mostrarPanelSinTurnos();
            } else {
                if (internet) {
                    NovusConstante.VENTAS_CONTINGENCIA = true;
                    mostrarFacturacionManual();
                } else {
                    NovusConstante.VENTAS_CONTINGENCIA = true;
                    abrirFactManual();
                }
            }
        }
    }// GEN-LAST:event_jLabel39MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnAnulaciones;
    private javax.swing.JLabel btnConsultaVentas;
    private javax.swing.JLabel btnConsultaVentasAppTerpel;
    private javax.swing.JLabel btnConsumoPropio;
    private javax.swing.JLabel btnFactElectronica;
    private javax.swing.JLabel btnHistorica;
    private javax.swing.JLabel btnPredeterminar;
    private javax.swing.JLabel btnVentaManual;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    public javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacion_Venta;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel lblOpcion1;
    private javax.swing.JLabel lblOpcion2;
    private javax.swing.JLabel lblOpcion3;
    private javax.swing.JLabel lblOpcion4;
    private javax.swing.JLabel lblOpcion5;
    private javax.swing.JLabel lblOpcion6;
    private javax.swing.JLabel lblOpcion7;
    private javax.swing.JLabel lblOpcion8;
    private javax.swing.JLabel lblTextOpcion1;
    private javax.swing.JLabel lblTextOpcion2;
    private javax.swing.JLabel lblTextOpcion3;
    private javax.swing.JLabel lblTextOpcion4;
    private javax.swing.JLabel lblTextOpcion5;
    private javax.swing.JLabel lblTextOpcion6;
    private javax.swing.JLabel lblTextOpcion7;
    private javax.swing.JLabel lblTextOpcion8;
    private javax.swing.JLabel next;
    private javax.swing.JLabel next1;
    private javax.swing.JPanel pnlMenuOpciones;
    private javax.swing.JPanel pnlOpcion1;
    private javax.swing.JPanel pnlOpcion2;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlPromotor;
    // End of variables declaration//GEN-END:variables

    private void mostrarPanelOpciones(String panel) {
        CardLayout cardPanelLayout = (CardLayout) this.pnlMenuOpciones.getLayout();
        cardPanelLayout.show(pnlMenuOpciones, panel);
    }

    private void cerrarsubmenu() {
//        this.setVisible(false);
        InfoViewController.NotificadorVentaView = null;
        this.base.cambiarPanelHome();

    }

    private void cerrar() {
        this.showPanel("principal");
    }

    private void mostrarVentas() {
        NovusUtils.beep();
        base.recargarPersona();
        if (Main.persona == null) {
            base.mostrarPanelSinTurnos();
        } else {
            VentasHistorialView hist = new VentasHistorialView(base, true);
            hist.setVisible(true);
        }
    }

    private void mostrarHistorialAppTerpel() {
        base.recargarPersona();
        if (Main.persona == null) {
            base.mostrarPanelSinTurnos();
        } else {
            VentasAppterpellHistorialView hist = new VentasAppterpellHistorialView(base, true);
            hist.setVisible(true);
        }

    }

    private void mostrarVentasPredeterminadas() {
        NovusUtils.beep();
        base.recargarPersona();
        if (Main.persona == null) {
            base.mostrarPanelSinTurnos();
        } else {
            TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
            tipoVenta.setView("VENTAS PREDETERMINADAS");
            tipoVenta.setVisible(true);
        }
    }

    private void mostrarConsumoPropio() {
        NovusUtils.beep();
        base.recargarPersona();
        PersonaBean persona;
        persona = Main.persona;
        if (Main.persona == null) {
            base.mostrarPanelSinTurnos();
        } else {
            if (InfoViewController.turnosPersonas.size() > 1) {
                PromotorSeleccionView seleccionpromotor = PromotorSeleccionView.getInstance(this.base,
                        NovusConstante.SELECCION_PROMOTOR_CONSUMO_PROPIO);
                seleccionpromotor.setVisible(true);
            } else {
                Long id = persona.getId();
                NovusUtils.printLn("Id: " + id + ">>>>>>>>>");
                //NovusUtils.printLn("isAdmin :" + pdao.isAdmin(id));
                NovusUtils.printLn("isAdmin :" + isAdminUseCase.execute(id));
                //if (pdao.isAdmin(id)) {
                if (isAdminUseCase.execute(id)) {
                    TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
                    tipoVenta.setView("CONSUMO PROPIO");
                    tipoVenta.setVisible(true);
                } else {
                    TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
                    tipoVenta.setView("CONSUMO PROPIO");
                    AutorizacionView auto = new AutorizacionView(base, true, tipoVenta);
                    auto.setVisible(true);
                }
            }
        }
    }

    private void mostrarFacturacionManual() {
        NovusUtils.beep();
        base.recargarPersona();
        PersonaBean persona;
        persona = Main.persona;

        Long idPersona = persona.getId();
        boolean promotores = false;
        NovusConstante.ACTIVAR_BOTON_CONTINGENCIA = true;
        FacturacionManualView factManual = new FacturacionManualView(base, true, true);
        AutorizacionView auto = new AutorizacionView(base, true, factManual, promotores, base, true, true);
        showDialog(auto);
    }

    private void mostrarFacElectronica() throws DAOException {
        NovusUtils.beep();
        base.recargarPersona();
        if (Main.persona == null) {
            base.mostrarPanelSinTurnos();
        } else {
            try {
                ClienteFacturaElectronica fac = new ClienteFacturaElectronica(base, true, false, false, true);
                NovusUtils.printLn("abriendo la vista de facturacio electronica desde -> " + VentaMenuPanelController.class.getName());
                fac.setVisible(true);
            } catch (HeadlessException e) {
                NovusUtils.printLn(e + "");
            }
        }
    }

    private void mostrarAnulacionVentas() {
        NovusUtils.beep();
        AnulacionVentasView anulacionView = new AnulacionVentasView(base, true);
        AutorizacionView auto = new AutorizacionView(base, true, anulacionView);
        auto.setVisible(true);
    }

    private void mostrarVentasHistorica() {
        NovusUtils.beep();
        this.base.recargarPersona();
        NovusUtils.printLn(Integer.toString(InfoViewController.turnosPersonas.size()));
        if (InfoViewController.turnosPersonas.size() >= 1) {
            VentasHistoriaFulLView hist = new VentasHistoriaFulLView(base, true);
            hist.setVisible(true);
        }
    }

    private void abrirFactManual() {
        NovusUtils.beep();
        base.mostrarSubPanel(new VentaManualMenuPanel(base, false, true));
    }

    private void desactivarVentaManual() {
        lblOpcion7.setVisible(false);
        lblTextOpcion7.setVisible(false);
        btnVentaManual.setVisible(false);
    }

    //Se muestran los Promotores con Turnos Iniciados
    long id = 0L;

    private void renderizarUsuarios() {
        try {
            int i = 0;
            int height = 97;
            for (PersonaBean personaT : InfoViewController.turnosPersonas) {
                PromotoresVentas item = new PromotoresVentas(personaT);
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {

                        id = personaT.getId();
                        boolean internet = base.hayInternet;
                        boolean promotores = false;

                        NovusUtils.printLn("Id: " + id + ">>");
                       // NovusUtils.printLn("isAdmin :" + pdao.isAdmin(id));
                        NovusUtils.printLn("isAdmin :" + isAdminUseCase.execute(id));
                        NovusUtils.printLn("hay Internet: " + internet);

                        if (internet) {
                            //if (pdao.isAdmin(id)) {
                            if (isAdminUseCase.execute(id)) {
                                FacturacionManualView factManual = new FacturacionManualView(base, true, true);
                                AutorizacionView auto = new AutorizacionView(base, true, factManual, promotores, base);
                                showDialog(auto);
                            } else {
                                showMessage("NO CUENTA CON ACCESO A ESTE MODULO",
                                        "/com/firefuel/resources/btBad.png", 
                                        true, () -> mostrarMenuPrincipal(), 
                                        true, LetterCase.FIRST_UPPER_CASE);
                            }
                        } else {
                            abrirFactManual();
                        }
                    }
                });
                jPanel4.add(item);
                item.setBounds(10, (i * height) + (5 * (i + 1)), 763, height);
                i++;
            }
        } catch (Exception ex) {
            Logger.getLogger(VentasHistorialView.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "principal");
    }

    private void showPanel(String panel) {        
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

}

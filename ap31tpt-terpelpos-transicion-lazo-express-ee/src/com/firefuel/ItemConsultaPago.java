/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.bean.TransaccionGopass;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.domain.entities.GoPassEntity;

import javax.swing.Icon;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Devitech
 */
public class ItemConsultaPago extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(ItemConsultaPago.class.getName());

    TransaccionGopass transaccion;
    GoPassMenuController panel;
    private final Icon ACEPTADO = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/success_gopass_small.png"));
    private final Icon RECHAZADO = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/Estado_Error_Small.png"));
    private final Icon PENDIENTE = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/Estado_Pendiente_Small.png"));
    private final Icon CONSULTAR_ESTADO = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_consultar_estado_pago.png"));

    public ItemConsultaPago(GoPassMenuController panel, TransaccionGopass transaccion) {
        this.transaccion = transaccion;
        this.panel = panel;
        initComponents();
        init();
    }

    public void init() {
        if (transaccion != null) {
            LOGGER.info("Inicializando ItemConsultaPago con transacción: " + transaccion.getIdentificadorventaterpel());
            LOGGER.info("Estado recibido: " + transaccion.getEstado());
            
            jfecha.setText(transaccion.getFecha());
            jref.setText("" + transaccion.getIdentificadorMovimiento());
            jplaca.setText(transaccion.getPlaca());
            
            String estado = transaccion.getEstado();
            LOGGER.info("Evaluando estado para íconos: " + estado);
            
            switch (estado) {
                case "2":
                    LOGGER.info("Estado 2: Mostrando ícono ACEPTADO");
                    jestadopago.setIcon(ACEPTADO);
                    jMovimiento.setVisible(true);
                    break;
                case "3":
                case "5":
                case "4":
                    LOGGER.info("Estado " + estado + ": Mostrando ícono RECHAZADO");
                    jestadopago.setIcon(RECHAZADO);
                    jMovimiento.setIcon(CONSULTAR_ESTADO);
                    break;
                default:
                    LOGGER.info("Estado default: Mostrando ícono PENDIENTE");
                    jestadopago.setIcon(PENDIENTE);
                    jMovimiento.setIcon(CONSULTAR_ESTADO);
                    break;
            }
        } else {
            LOGGER.warning("Se intentó inicializar ItemConsultaPago con transacción null");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jestadopago = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jfecha = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jref = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jplaca = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jMovimiento = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 2, true));
        setPreferredSize(new java.awt.Dimension(1100, 80));
        setLayout(null);

        jestadopago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(jestadopago);
        jestadopago.setBounds(920, 0, 90, 90);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(186, 12, 47));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("FECHA:");
        add(jLabel1);
        jLabel1.setBounds(60, 10, 100, 29);

        jfecha.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jfecha.setForeground(new java.awt.Color(153, 153, 153));
        jfecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jfecha.setText("FECHA");
        add(jfecha);
        jfecha.setBounds(10, 50, 200, 30);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(186, 12, 47));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("REFERENCIA DE COBRO :");
        add(jLabel3);
        jLabel3.setBounds(250, 10, 250, 29);

        jref.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jref.setForeground(new java.awt.Color(153, 153, 153));
        jref.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jref.setText("REFERENCIA");
        add(jref);
        jref.setBounds(280, 50, 170, 30);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("PLACA DEL VEHICULO :");
        add(jLabel5);
        jLabel5.setBounds(570, 10, 250, 29);

        jplaca.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jplaca.setForeground(new java.awt.Color(153, 153, 153));
        jplaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jplaca.setText("PLACA");
        add(jplaca);
        jplaca.setBounds(620, 50, 150, 30);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel8);
        jLabel8.setBounds(1030, 0, 20, 100);

        jMovimiento.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMovimiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/gopass_imprimir.png"))); // NOI18N
        jMovimiento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMovimientoMouseReleased(evt);
            }
        });
        add(jMovimiento);
        jMovimiento.setBounds(1070, 0, 90, 90);
    }// </editor-fold>//GEN-END:initComponents

    private void jMovimientoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jMovimientoMouseReleased
        NovusUtils.beep();
        if (jMovimiento.getIcon() == CONSULTAR_ESTADO) {
            panel.consultarEstadoPago(transaccion);
        } else {
            LOGGER.info("jMovimientoMouseReleased: Reimprimiendo factura.");
            LOGGER.info("jMovimientoMouseReleased: transaccion.getIdentificadorventaterpel() = " + transaccion.getIdentificadorventaterpel());
            LOGGER.info("jMovimientoMouseReleased: transaccion.getIdentificadorMovimiento() = " + transaccion.getIdentificadorMovimiento());
            LOGGER.info("jMovimientoMouseReleased: transaccion.getIdentificadortransacciongopass() = " + transaccion.getIdentificadortransacciongopass());
            long idMovimientoParaImprimir = idMovimiento(transaccion.getIdentificadorMovimiento());
            LOGGER.info("jMovimientoMouseReleased: idMovimiento(transaccion.getIdentificadorMovimiento()) = " + idMovimientoParaImprimir);
            panel.reImprimirPago( idMovimientoParaImprimir, "factura");
        }
    }// GEN-LAST:event_jMovimientoMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jMovimiento;
    private javax.swing.JLabel jestadopago;
    private javax.swing.JLabel jfecha;
    private javax.swing.JLabel jplaca;
    private javax.swing.JLabel jref;
    // End of variables declaration//GEN-END:variables

    public long idMovimiento(long identificadorCompuesto) {
        NovusUtils.printLn("POS_ID: "+Main.parametrosCore.get(NovusConstante.PREFERENCE_POSID));
        LOGGER.info("idMovimiento input: " + identificadorCompuesto);
        if (identificadorCompuesto > 1) {
            String idCompuestoStr = String.valueOf(identificadorCompuesto);
            String posId = Main.parametrosCore.get(NovusConstante.PREFERENCE_POSID);
            LOGGER.info("idMovimiento POS_ID: " + posId);

            if (idCompuestoStr.startsWith(posId)) {
                String idSinPos = idCompuestoStr.substring(posId.length());
                LOGGER.info("idMovimiento string after removing POS_ID: " + idSinPos);
                // Assuming movement ID is 3 digits.
                if (idSinPos.length() >= 3) {
                    String movementIdStr = idSinPos.substring(0, 3);
                    LOGGER.info("idMovimiento extracted movement ID: " + movementIdStr);
                    return Long.valueOf(movementIdStr);
                } else {
                     LOGGER.warning("idMovimiento cannot extract 3-digit movement ID from: " + idSinPos);
                     return Long.valueOf(idSinPos); // fallback
                }
            } else {
                 LOGGER.warning("idMovimiento input does not start with POS_ID. Using old logic.");
                 // Fallback to old logic for the other ID type
                 String idMovimientoString = String.valueOf(identificadorCompuesto).substring(
                    Main.parametrosCore.get(NovusConstante.PREFERENCE_POSID).length());
                 return Long.valueOf(idMovimientoString);
            }
        }
        return 0;
    }

}

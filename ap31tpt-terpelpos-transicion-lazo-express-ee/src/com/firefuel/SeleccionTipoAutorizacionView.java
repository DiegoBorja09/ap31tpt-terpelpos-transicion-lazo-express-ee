
package com.firefuel;

import com.controllers.NovusUtils;
import com.firefuel.components.panelesPersonalizados.BordeRedondoMejorado;
import java.awt.Color;

/**
 * Pantalla de selección de tipo de autorización para consumo propio
 * Permite elegir entre autorización por Chip Ibutton o por Placa
 * 
 * @author Diego Borja Padilla
 */
public class SeleccionTipoAutorizacionView extends javax.swing.JDialog {

    private InfoViewController parent;
    
    public SeleccionTipoAutorizacionView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.init();
    }
    
    private void init() {
        setLocationRelativeTo(null);
        if (Main.persona != null) {
            jLabel_nombrePromotor.setText(Main.persona.getNombre() + " " + Main.persona.getApellidos());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnl_principal = new javax.swing.JPanel();
        jLabel_back = new javax.swing.JLabel();
        jLabel_title = new javax.swing.JLabel();
        jLabel_separador1 = new javax.swing.JLabel();
        jLabel_separador2 = new javax.swing.JLabel();
        jLabel_logo = new javax.swing.JLabel();
        jLabel_instruccion = new javax.swing.JLabel();
        jPanel_ibutton = new javax.swing.JPanel();
        jLabel_iconoIbutton = new javax.swing.JLabel();
        jLabel_textoIbutton = new javax.swing.JLabel();
        jPanel_placa = new javax.swing.JPanel();
        jLabel_iconoPlaca = new javax.swing.JLabel();
        jLabel_textoPlaca = new javax.swing.JLabel();
        jLabel_footer = new javax.swing.JLabel();
        jLabel_header = new javax.swing.JLabel();
        jLabel_background = new javax.swing.JLabel();
        jLabel_nombrePromotor = new javax.swing.JLabel();
        jLabel_cancelar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnl_principal.setOpaque(false);
        pnl_principal.setLayout(null);

        jLabel_back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png")));
        jLabel_back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel_backMouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel_back);
        jLabel_back.setBounds(10, 10, 70, 60);

        jLabel_title.setFont(new java.awt.Font("Terpel Sans", 1, 36));
        jLabel_title.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_title.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_title.setText("PRE-AUTORIZACIÓN VENTA");
        pnl_principal.add(jLabel_title);
        jLabel_title.setBounds(140, 10, 600, 60);
        
        jLabel_nombrePromotor.setFont(new java.awt.Font("Terpel Sans", 1, 36));
        jLabel_nombrePromotor.setForeground(new java.awt.Color(255, 255, 0)); // Amarillo
        jLabel_nombrePromotor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel_nombrePromotor.setText("PROMOTOR");
        pnl_principal.add(jLabel_nombrePromotor);
        jLabel_nombrePromotor.setBounds(750, 10, 500, 60);

        jLabel_separador1.setVisible(false);
        jLabel_separador2.setVisible(false);

        jLabel_logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png")));
        pnl_principal.add(jLabel_logo);
        jLabel_logo.setBounds(10, 710, 110, 80);

        jLabel_instruccion.setFont(new java.awt.Font("Arial", 1, 34));
        jLabel_instruccion.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_instruccion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_instruccion.setText("Seleccione tipo de autorización");
        pnl_principal.add(jLabel_instruccion);
        jLabel_instruccion.setBounds(0, 110, 1280, 50);

        jPanel_ibutton.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_ibutton.setOpaque(true);
        jPanel_ibutton.setBorder(new BordeRedondoMejorado(new java.awt.Color(186, 12, 47), 20, 3.0f));
        jPanel_ibutton.setLayout(null);
        jPanel_ibutton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel_ibuttonMouseReleased(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel_ibuttonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel_ibuttonMouseExited(evt);
            }
        });

        jLabel_iconoIbutton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_iconoIbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/ibutton-rumbo.png")));
        jPanel_ibutton.add(jLabel_iconoIbutton);
        jLabel_iconoIbutton.setBounds(50, 60, 250, 250);

        jLabel_textoIbutton.setFont(new java.awt.Font("Arial", 1, 28));
        jLabel_textoIbutton.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_textoIbutton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_textoIbutton.setText("Chip Ibutton");
        jPanel_ibutton.add(jLabel_textoIbutton);
        jLabel_textoIbutton.setBounds(10, 330, 340, 50);

        pnl_principal.add(jPanel_ibutton);
        jPanel_ibutton.setBounds(250, 200, 360, 420);

        jPanel_placa.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_placa.setOpaque(true);
        jPanel_placa.setBorder(new BordeRedondoMejorado(new java.awt.Color(186, 12, 47), 20, 3.0f));
        jPanel_placa.setLayout(null);
        jPanel_placa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel_placaMouseReleased(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel_placaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel_placaMouseExited(evt);
            }
        });

        jLabel_iconoPlaca.setFont(new java.awt.Font("Arial", 1, 38));
        jLabel_iconoPlaca.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_iconoPlaca.setText("ABC-123");
        jLabel_iconoPlaca.setBackground(new java.awt.Color(255, 255, 255));
        jLabel_iconoPlaca.setOpaque(true);
        jLabel_iconoPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_iconoPlaca.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2),
            javax.swing.BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        jPanel_placa.add(jLabel_iconoPlaca);
        jLabel_iconoPlaca.setBounds(55, 70, 250, 130);

        jLabel_textoPlaca.setFont(new java.awt.Font("Arial", 1, 28));
        jLabel_textoPlaca.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_textoPlaca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_textoPlaca.setText("Placa");
        jPanel_placa.add(jLabel_textoPlaca);
        jLabel_textoPlaca.setBounds(10, 330, 340, 50);

        pnl_principal.add(jPanel_placa);
        jPanel_placa.setBounds(670, 200, 360, 420);

        // Header rojo
        jLabel_header.setBackground(new java.awt.Color(186, 12, 47));
        jLabel_header.setOpaque(true);
        pnl_principal.add(jLabel_header);
        jLabel_header.setBounds(0, 0, 1280, 80);
        
        // Footer rojo
        jLabel_footer.setBackground(new java.awt.Color(186, 12, 47));
        jLabel_footer.setOpaque(true);
        pnl_principal.add(jLabel_footer);
        jLabel_footer.setBounds(0, 710, 1280, 90);
        
        // Botón CANCELAR en el footer (lado derecho)
        jLabel_cancelar.setFont(new java.awt.Font("Segoe UI", 1, 28));
        jLabel_cancelar.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_cancelar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_cancelar.setText("CANCELAR");
        jLabel_cancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_cancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel_cancelarMouseReleased(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel_cancelarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel_cancelarMouseExited(evt);
            }
        });
        pnl_principal.add(jLabel_cancelar);
        jLabel_cancelar.setBounds(1040, 720, 200, 70);
        
        jLabel_background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png")));
        pnl_principal.add(jLabel_background);
        jLabel_background.setBounds(0, 0, 1280, 800);

        getContentPane().add(pnl_principal);
        pnl_principal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }

    private void jLabel_backMouseReleased(java.awt.event.MouseEvent evt) {
        NovusUtils.beep();
        this.dispose();
    }

    private void jPanel_ibuttonMouseReleased(java.awt.event.MouseEvent evt) {
        NovusUtils.beep();
        this.dispose();
        VentaPredefinirPlaca instanciaPlaca = VentaPredefinirPlaca.getInstance(parent, true);
        instanciaPlaca.setVisible(true);
    }

    private void jPanel_placaMouseReleased(java.awt.event.MouseEvent evt) {
        NovusUtils.beep();
        this.dispose();
        VentaPredefinirPlaca instanciaPlaca = VentaPredefinirPlaca.getInstance(parent, true);
        instanciaPlaca.inicializarFlujoPlaca();
        instanciaPlaca.setVisible(true);
    }

    private void jPanel_ibuttonMouseEntered(java.awt.event.MouseEvent evt) {
        jPanel_ibutton.setBackground(new Color(250, 250, 250));
    }

    private void jPanel_ibuttonMouseExited(java.awt.event.MouseEvent evt) {
        jPanel_ibutton.setBackground(new Color(255, 255, 255));
    }

    private void jPanel_placaMouseEntered(java.awt.event.MouseEvent evt) {
        jPanel_placa.setBackground(new Color(250, 250, 250));
    }

    private void jPanel_placaMouseExited(java.awt.event.MouseEvent evt) {
        jPanel_placa.setBackground(new Color(255, 255, 255));
    }

    private void jLabel_cancelarMouseReleased(java.awt.event.MouseEvent evt) {
        NovusUtils.beep();
        this.dispose();
    }

    private void jLabel_cancelarMouseEntered(java.awt.event.MouseEvent evt) {
        jLabel_cancelar.setForeground(new Color(255, 255, 150));
    }

    private void jLabel_cancelarMouseExited(java.awt.event.MouseEvent evt) {
        jLabel_cancelar.setForeground(new Color(255, 255, 255));
    }

    private javax.swing.JLabel jLabel_back;
    private javax.swing.JLabel jLabel_background;
    private javax.swing.JLabel jLabel_cancelar;
    private javax.swing.JLabel jLabel_footer;
    private javax.swing.JLabel jLabel_header;
    private javax.swing.JLabel jLabel_iconoIbutton;
    private javax.swing.JLabel jLabel_iconoPlaca;
    private javax.swing.JLabel jLabel_instruccion;
    private javax.swing.JLabel jLabel_logo;
    private javax.swing.JLabel jLabel_nombrePromotor;
    private javax.swing.JLabel jLabel_separador1;
    private javax.swing.JLabel jLabel_separador2;
    private javax.swing.JLabel jLabel_textoIbutton;
    private javax.swing.JLabel jLabel_textoPlaca;
    private javax.swing.JLabel jLabel_title;
    private javax.swing.JPanel jPanel_ibutton;
    private javax.swing.JPanel jPanel_placa;
    private javax.swing.JPanel pnl_principal;
}


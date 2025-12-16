/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teclado.view.common;

import com.controllers.NovusUtils;
import com.firefuel.Main;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.KeyStroke;

/**
 *
 * @author ASUS-PC
 */
public class TecladoExtendidoWhite extends JPanel {

    private static final long serialVersionUID = 1L;

    Robot robot;
    public static String texto = "";

    ImageIcon botonOn = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"));
    ImageIcon botonOff = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white_off.png"));

    ImageIcon botonBorrar1 = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_extendido_delete_on.png"));
    ImageIcon botonBorrar2 = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_extendido_delete_off.png"));

    ImageIcon botonAceptar1 = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_extendido_ok_on.png"));
    ImageIcon botonAceptar2 = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_extendido_ok_off.png"));

    ImageIcon botonSpace1 = new ImageIcon(getClass().getResource("/teclado/view/resources/teclado_extendido_bsc_white.png"));
    ImageIcon botonSpace2 = new ImageIcon(getClass().getResource("/teclado/view/resources/teclado_extendido_bsc_white_off.png"));

    ImageIcon botonNum1 = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"));
    ImageIcon botonNum2 = new ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white_off.png"));

    ImageIcon botonDel1 = new ImageIcon(getClass().getResource("/teclado/view/resources/teclado_extendido_del_1.png"));
    ImageIcon botonDel2 = new ImageIcon(getClass().getResource("/teclado/view/resources/teclado_extendido_del_2.png"));

    public TecladoExtendidoWhite() {
        initComponents();
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(TecladoExtendidoWhite.class.getName()).log(Level.SEVERE, null, ex);
        }

        NovusUtils.ajusteFuente(this.getComponents(), Font.BOLD);
        deshabilitarCaracteresEspeciales(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlabel1 = new javax.swing.JLabel();
        jlabel2 = new javax.swing.JLabel();
        jlabel3 = new javax.swing.JLabel();
        jlabel6 = new javax.swing.JLabel();
        jlabel5 = new javax.swing.JLabel();
        jlabel4 = new javax.swing.JLabel();
        jlabel7 = new javax.swing.JLabel();
        jlabel8 = new javax.swing.JLabel();
        jlabel9 = new javax.swing.JLabel();
        jlabelAceptar = new javax.swing.JLabel();
        jlabelNINI = new javax.swing.JLabel();
        jlabel0 = new javax.swing.JLabel();
        jlabelPP = new javax.swing.JLabel();
        jlabelYY = new javax.swing.JLabel();
        jlabelQQ = new javax.swing.JLabel();
        jlabelWW = new javax.swing.JLabel();
        jlabelBorrar = new javax.swing.JLabel();
        jlabelEE = new javax.swing.JLabel();
        jlabelOO = new javax.swing.JLabel();
        jlabelII = new javax.swing.JLabel();
        jlabelLL = new javax.swing.JLabel();
        jlabelGUION = new javax.swing.JLabel();
        jlabelMM = new javax.swing.JLabel();
        jlabelTT = new javax.swing.JLabel();
        jlabelRR = new javax.swing.JLabel();
        jlabelGG = new javax.swing.JLabel();
        jlabelJJ = new javax.swing.JLabel();
        jlabelHH = new javax.swing.JLabel();
        jlabelDD = new javax.swing.JLabel();
        jlabelFF = new javax.swing.JLabel();
        jlabelKK = new javax.swing.JLabel();
        jlabelUU = new javax.swing.JLabel();
        jlabelAA = new javax.swing.JLabel();
        jlabelSS = new javax.swing.JLabel();
        jlabelCC = new javax.swing.JLabel();
        jlabelVV = new javax.swing.JLabel();
        jlabelBB = new javax.swing.JLabel();
        jlabelXX = new javax.swing.JLabel();
        jlabelNN = new javax.swing.JLabel();
        jlabelZZ = new javax.swing.JLabel();
        jlabelCOMA = new javax.swing.JLabel();
        jlabelPUNTO = new javax.swing.JLabel();
        jlabelDOSPUNTOS = new javax.swing.JLabel();
        jlabelARROBA = new javax.swing.JLabel();
        jlabelLINEAABAJO = new javax.swing.JLabel();
        jlabelPORCENTAJE = new javax.swing.JLabel();
        jlabelESPACIO = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jlabel1.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel1.setForeground(new java.awt.Color(186, 12, 47));
        jlabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel1.setText("1");
        jlabel1.setToolTipText("");
        jlabel1.setAlignmentY(0.0F);
        jlabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jlabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel1.setName("1"); // NOI18N
        jlabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel1MouseReleased(evt);
            }
        });
        add(jlabel1);
        jlabel1.setBounds(780, 10, 73, 73);

        jlabel2.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel2.setForeground(new java.awt.Color(186, 12, 47));
        jlabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel2.setText("2");
        jlabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel2.setName("2"); // NOI18N
        jlabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel2MouseReleased(evt);
            }
        });
        add(jlabel2);
        jlabel2.setBounds(860, 10, 73, 73);

        jlabel3.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel3.setForeground(new java.awt.Color(186, 12, 47));
        jlabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel3.setText("3");
        jlabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel3.setName("3"); // NOI18N
        jlabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel3MouseReleased(evt);
            }
        });
        add(jlabel3);
        jlabel3.setBounds(940, 10, 73, 73);

        jlabel6.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel6.setForeground(new java.awt.Color(186, 12, 47));
        jlabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel6.setText("6");
        jlabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel6.setName("6"); // NOI18N
        jlabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel6MouseReleased(evt);
            }
        });
        add(jlabel6);
        jlabel6.setBounds(940, 90, 73, 73);

        jlabel5.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel5.setForeground(new java.awt.Color(186, 12, 47));
        jlabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel5.setText("5");
        jlabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel5.setName("5"); // NOI18N
        jlabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel5MouseReleased(evt);
            }
        });
        add(jlabel5);
        jlabel5.setBounds(860, 90, 73, 73);

        jlabel4.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel4.setForeground(new java.awt.Color(186, 12, 47));
        jlabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel4.setText("4");
        jlabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel4.setName("4"); // NOI18N
        jlabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel4MouseReleased(evt);
            }
        });
        add(jlabel4);
        jlabel4.setBounds(780, 90, 73, 73);

        jlabel7.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel7.setForeground(new java.awt.Color(186, 12, 47));
        jlabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel7.setText("7");
        jlabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel7.setName("7"); // NOI18N
        jlabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel7MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel7MouseReleased(evt);
            }
        });
        add(jlabel7);
        jlabel7.setBounds(780, 170, 73, 73);

        jlabel8.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel8.setForeground(new java.awt.Color(186, 12, 47));
        jlabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel8.setText("8");
        jlabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel8.setName("8"); // NOI18N
        jlabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel8MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel8MouseReleased(evt);
            }
        });
        add(jlabel8);
        jlabel8.setBounds(860, 170, 73, 73);

        jlabel9.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel9.setForeground(new java.awt.Color(186, 12, 47));
        jlabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel9.setText("9");
        jlabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel9.setName("9"); // NOI18N
        jlabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel9MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel9MouseReleased(evt);
            }
        });
        add(jlabel9);
        jlabel9.setBounds(940, 170, 73, 73);

        jlabelAceptar.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelAceptar.setForeground(new java.awt.Color(255, 255, 255));
        jlabelAceptar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_extendido_ok_on.png"))); // NOI18N
        jlabelAceptar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelAceptar.setName("A"); // NOI18N
        jlabelAceptar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelAceptarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelAceptarMouseReleased(evt);
            }
        });
        add(jlabelAceptar);
        jlabelAceptar.setBounds(940, 250, 73, 73);

        jlabelNINI.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelNINI.setForeground(new java.awt.Color(186, 12, 47));
        jlabelNINI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelNINI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelNINI.setText("Ñ");
        jlabelNINI.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelNINI.setName("8"); // NOI18N
        jlabelNINI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelNINIMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelNINIMouseReleased(evt);
            }
        });
        add(jlabelNINI);
        jlabelNINI.setBounds(685, 90, 73, 73);

        jlabel0.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabel0.setForeground(new java.awt.Color(186, 12, 47));
        jlabel0.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabel0.setText("0");
        jlabel0.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel0.setName("0"); // NOI18N
        jlabel0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel0MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel0MouseReleased(evt);
            }
        });
        add(jlabel0);
        jlabel0.setBounds(860, 250, 73, 73);

        jlabelPP.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelPP.setForeground(new java.awt.Color(186, 12, 47));
        jlabelPP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelPP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelPP.setText("P");
        jlabelPP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelPP.setName("7"); // NOI18N
        jlabelPP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelPPMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelPPMouseReleased(evt);
            }
        });
        add(jlabelPP);
        jlabelPP.setBounds(685, 10, 73, 73);

        jlabelYY.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelYY.setForeground(new java.awt.Color(186, 12, 47));
        jlabelYY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelYY.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelYY.setText("Y");
        jlabelYY.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelYY.setName("4"); // NOI18N
        jlabelYY.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelYYMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelYYMouseReleased(evt);
            }
        });
        add(jlabelYY);
        jlabelYY.setBounds(385, 10, 73, 73);

        jlabelQQ.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelQQ.setForeground(new java.awt.Color(186, 12, 47));
        jlabelQQ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelQQ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelQQ.setText("Q");
        jlabelQQ.setToolTipText("");
        jlabelQQ.setAlignmentY(0.0F);
        jlabelQQ.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jlabelQQ.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelQQ.setName("1"); // NOI18N
        jlabelQQ.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelQQMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelQQMouseReleased(evt);
            }
        });
        add(jlabelQQ);
        jlabelQQ.setBounds(10, 10, 73, 73);

        jlabelWW.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelWW.setForeground(new java.awt.Color(186, 12, 47));
        jlabelWW.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelWW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelWW.setText("W");
        jlabelWW.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelWW.setName("2"); // NOI18N
        jlabelWW.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelWWMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelWWMouseReleased(evt);
            }
        });
        add(jlabelWW);
        jlabelWW.setBounds(85, 10, 73, 73);

        jlabelBorrar.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelBorrar.setForeground(new java.awt.Color(255, 255, 255));
        jlabelBorrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_extendido_delete_on.png"))); // NOI18N
        jlabelBorrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelBorrar.setName("B"); // NOI18N
        jlabelBorrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelBorrarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelBorrarMouseReleased(evt);
            }
        });
        add(jlabelBorrar);
        jlabelBorrar.setBounds(780, 250, 73, 73);

        jlabelEE.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelEE.setForeground(new java.awt.Color(186, 12, 47));
        jlabelEE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelEE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelEE.setText("E");
        jlabelEE.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelEE.setName("3"); // NOI18N
        jlabelEE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelEEMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelEEMouseReleased(evt);
            }
        });
        add(jlabelEE);
        jlabelEE.setBounds(160, 10, 73, 73);

        jlabelOO.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelOO.setForeground(new java.awt.Color(186, 12, 47));
        jlabelOO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelOO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelOO.setText("O");
        jlabelOO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelOO.setName("6"); // NOI18N
        jlabelOO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelOOMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelOOMouseReleased(evt);
            }
        });
        add(jlabelOO);
        jlabelOO.setBounds(610, 10, 73, 73);

        jlabelII.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelII.setForeground(new java.awt.Color(186, 12, 47));
        jlabelII.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelII.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelII.setText("I");
        jlabelII.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelII.setName("5"); // NOI18N
        jlabelII.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelIIMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelIIMouseReleased(evt);
            }
        });
        add(jlabelII);
        jlabelII.setBounds(535, 10, 73, 73);

        jlabelLL.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelLL.setForeground(new java.awt.Color(186, 12, 47));
        jlabelLL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelLL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelLL.setText("L");
        jlabelLL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelLL.setName("8"); // NOI18N
        jlabelLL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelLLMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelLLMouseReleased(evt);
            }
        });
        add(jlabelLL);
        jlabelLL.setBounds(610, 90, 73, 73);

        jlabelGUION.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelGUION.setForeground(new java.awt.Color(186, 12, 47));
        jlabelGUION.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelGUION.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelGUION.setText("-");
        jlabelGUION.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelGUION.setName("9"); // NOI18N
        jlabelGUION.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelGUIONMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelGUIONMouseReleased(evt);
            }
        });
        add(jlabelGUION);
        jlabelGUION.setBounds(10, 250, 73, 73);

        jlabelMM.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelMM.setForeground(new java.awt.Color(186, 12, 47));
        jlabelMM.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelMM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelMM.setText("M");
        jlabelMM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelMM.setName("9"); // NOI18N
        jlabelMM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelMMMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelMMMouseReleased(evt);
            }
        });
        add(jlabelMM);
        jlabelMM.setBounds(460, 170, 73, 73);

        jlabelTT.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelTT.setForeground(new java.awt.Color(186, 12, 47));
        jlabelTT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelTT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelTT.setText("T");
        jlabelTT.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelTT.setName("3"); // NOI18N
        jlabelTT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelTTMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelTTMouseReleased(evt);
            }
        });
        add(jlabelTT);
        jlabelTT.setBounds(310, 10, 73, 73);

        jlabelRR.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelRR.setForeground(new java.awt.Color(186, 12, 47));
        jlabelRR.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelRR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelRR.setText("R");
        jlabelRR.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelRR.setName("3"); // NOI18N
        jlabelRR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelRRMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelRRMouseReleased(evt);
            }
        });
        add(jlabelRR);
        jlabelRR.setBounds(235, 10, 73, 73);

        jlabelGG.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelGG.setForeground(new java.awt.Color(186, 12, 47));
        jlabelGG.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelGG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelGG.setText("G");
        jlabelGG.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelGG.setName("5"); // NOI18N
        jlabelGG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelGGMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelGGMouseReleased(evt);
            }
        });
        add(jlabelGG);
        jlabelGG.setBounds(310, 90, 73, 73);

        jlabelJJ.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelJJ.setForeground(new java.awt.Color(186, 12, 47));
        jlabelJJ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelJJ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelJJ.setText("J");
        jlabelJJ.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelJJ.setName("5"); // NOI18N
        jlabelJJ.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelJJMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelJJMouseReleased(evt);
            }
        });
        add(jlabelJJ);
        jlabelJJ.setBounds(460, 90, 73, 73);

        jlabelHH.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelHH.setForeground(new java.awt.Color(186, 12, 47));
        jlabelHH.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelHH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelHH.setText("H");
        jlabelHH.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelHH.setName("5"); // NOI18N
        jlabelHH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelHHMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelHHMouseReleased(evt);
            }
        });
        add(jlabelHH);
        jlabelHH.setBounds(385, 90, 73, 73);

        jlabelDD.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelDD.setForeground(new java.awt.Color(186, 12, 47));
        jlabelDD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelDD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelDD.setText("D");
        jlabelDD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelDD.setName("5"); // NOI18N
        jlabelDD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelDDMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelDDMouseReleased(evt);
            }
        });
        add(jlabelDD);
        jlabelDD.setBounds(160, 90, 73, 73);

        jlabelFF.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelFF.setForeground(new java.awt.Color(186, 12, 47));
        jlabelFF.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelFF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelFF.setText("F");
        jlabelFF.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelFF.setName("5"); // NOI18N
        jlabelFF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelFFMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelFFMouseReleased(evt);
            }
        });
        add(jlabelFF);
        jlabelFF.setBounds(235, 90, 73, 73);

        jlabelKK.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelKK.setForeground(new java.awt.Color(186, 12, 47));
        jlabelKK.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelKK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelKK.setText("K");
        jlabelKK.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelKK.setName("5"); // NOI18N
        jlabelKK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelKKMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelKKMouseReleased(evt);
            }
        });
        add(jlabelKK);
        jlabelKK.setBounds(535, 90, 73, 73);

        jlabelUU.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelUU.setForeground(new java.awt.Color(186, 12, 47));
        jlabelUU.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelUU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelUU.setText("U");
        jlabelUU.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelUU.setName("4"); // NOI18N
        jlabelUU.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelUUMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelUUMouseReleased(evt);
            }
        });
        add(jlabelUU);
        jlabelUU.setBounds(460, 10, 73, 73);

        jlabelAA.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelAA.setForeground(new java.awt.Color(186, 12, 47));
        jlabelAA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelAA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelAA.setText("A");
        jlabelAA.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelAA.setName("7"); // NOI18N
        jlabelAA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelAAMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelAAMouseReleased(evt);
            }
        });
        add(jlabelAA);
        jlabelAA.setBounds(10, 90, 73, 73);

        jlabelSS.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelSS.setForeground(new java.awt.Color(186, 12, 47));
        jlabelSS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelSS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelSS.setText("S");
        jlabelSS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelSS.setName("5"); // NOI18N
        jlabelSS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelSSMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelSSMouseReleased(evt);
            }
        });
        add(jlabelSS);
        jlabelSS.setBounds(85, 90, 73, 73);

        jlabelCC.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelCC.setForeground(new java.awt.Color(186, 12, 47));
        jlabelCC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelCC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelCC.setText("C");
        jlabelCC.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelCC.setName("5"); // NOI18N
        jlabelCC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelCCMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelCCMouseReleased(evt);
            }
        });
        add(jlabelCC);
        jlabelCC.setBounds(160, 170, 73, 73);

        jlabelVV.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelVV.setForeground(new java.awt.Color(186, 12, 47));
        jlabelVV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelVV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelVV.setText("V");
        jlabelVV.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelVV.setName("5"); // NOI18N
        jlabelVV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelVVMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelVVMouseReleased(evt);
            }
        });
        add(jlabelVV);
        jlabelVV.setBounds(235, 170, 73, 73);

        jlabelBB.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelBB.setForeground(new java.awt.Color(186, 12, 47));
        jlabelBB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelBB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelBB.setText("B");
        jlabelBB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelBB.setName("5"); // NOI18N
        jlabelBB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelBBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelBBMouseReleased(evt);
            }
        });
        add(jlabelBB);
        jlabelBB.setBounds(310, 170, 73, 73);

        jlabelXX.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelXX.setForeground(new java.awt.Color(186, 12, 47));
        jlabelXX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelXX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelXX.setText("X");
        jlabelXX.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelXX.setName("5"); // NOI18N
        jlabelXX.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelXXMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelXXMouseReleased(evt);
            }
        });
        add(jlabelXX);
        jlabelXX.setBounds(85, 170, 73, 73);

        jlabelNN.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelNN.setForeground(new java.awt.Color(186, 12, 47));
        jlabelNN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelNN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelNN.setText("N");
        jlabelNN.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelNN.setName("5"); // NOI18N
        jlabelNN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelNNMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelNNMouseReleased(evt);
            }
        });
        add(jlabelNN);
        jlabelNN.setBounds(385, 170, 73, 73);

        jlabelZZ.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelZZ.setForeground(new java.awt.Color(186, 12, 47));
        jlabelZZ.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelZZ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelZZ.setText("Z");
        jlabelZZ.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelZZ.setName("5"); // NOI18N
        jlabelZZ.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelZZMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelZZMouseReleased(evt);
            }
        });
        add(jlabelZZ);
        jlabelZZ.setBounds(10, 170, 73, 73);

        jlabelCOMA.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelCOMA.setForeground(new java.awt.Color(186, 12, 47));
        jlabelCOMA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelCOMA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelCOMA.setText(",");
        jlabelCOMA.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelCOMA.setName("9"); // NOI18N
        jlabelCOMA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelCOMAMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelCOMAMouseReleased(evt);
            }
        });
        add(jlabelCOMA);
        jlabelCOMA.setBounds(535, 170, 73, 73);

        jlabelPUNTO.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelPUNTO.setForeground(new java.awt.Color(186, 12, 47));
        jlabelPUNTO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelPUNTO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelPUNTO.setText(".");
        jlabelPUNTO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelPUNTO.setName("9"); // NOI18N
        jlabelPUNTO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelPUNTOMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelPUNTOMouseReleased(evt);
            }
        });
        add(jlabelPUNTO);
        jlabelPUNTO.setBounds(610, 170, 73, 73);

        jlabelDOSPUNTOS.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelDOSPUNTOS.setForeground(new java.awt.Color(186, 12, 47));
        jlabelDOSPUNTOS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelDOSPUNTOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelDOSPUNTOS.setText(":");
        jlabelDOSPUNTOS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelDOSPUNTOS.setName("9"); // NOI18N
        jlabelDOSPUNTOS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelDOSPUNTOSMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelDOSPUNTOSMouseReleased(evt);
            }
        });
        add(jlabelDOSPUNTOS);
        jlabelDOSPUNTOS.setBounds(685, 170, 73, 73);

        jlabelARROBA.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelARROBA.setForeground(new java.awt.Color(186, 12, 47));
        jlabelARROBA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelARROBA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelARROBA.setText("@");
        jlabelARROBA.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelARROBA.setName("9"); // NOI18N
        jlabelARROBA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelARROBAMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelARROBAMouseReleased(evt);
            }
        });
        add(jlabelARROBA);
        jlabelARROBA.setBounds(85, 250, 73, 73);

        jlabelLINEAABAJO.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelLINEAABAJO.setForeground(new java.awt.Color(186, 12, 47));
        jlabelLINEAABAJO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelLINEAABAJO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelLINEAABAJO.setText("_");
        jlabelLINEAABAJO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelLINEAABAJO.setName("9"); // NOI18N
        jlabelLINEAABAJO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelLINEAABAJOMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelLINEAABAJOMouseReleased(evt);
            }
        });
        add(jlabelLINEAABAJO);
        jlabelLINEAABAJO.setBounds(610, 250, 73, 73);

        jlabelPORCENTAJE.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelPORCENTAJE.setForeground(new java.awt.Color(186, 12, 47));
        jlabelPORCENTAJE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelPORCENTAJE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/btn_teclado_extendido_white.png"))); // NOI18N
        jlabelPORCENTAJE.setText("%");
        jlabelPORCENTAJE.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelPORCENTAJE.setName("9"); // NOI18N
        jlabelPORCENTAJE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelPORCENTAJEMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelPORCENTAJEMouseReleased(evt);
            }
        });
        add(jlabelPORCENTAJE);
        jlabelPORCENTAJE.setBounds(685, 250, 73, 73);

        jlabelESPACIO.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jlabelESPACIO.setForeground(new java.awt.Color(186, 12, 47));
        jlabelESPACIO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelESPACIO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/teclado_extendido_bsc_white.png"))); // NOI18N
        jlabelESPACIO.setText("ESPACIO");
        jlabelESPACIO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabelESPACIO.setName("9"); // NOI18N
        jlabelESPACIO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabelESPACIOMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabelESPACIOMouseReleased(evt);
            }
        });
        add(jlabelESPACIO);
        jlabelESPACIO.setBounds(160, 250, 450, 73);

        background.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/teclado_extendido_bg_white.png"))); // NOI18N
        background.setToolTipText("");
        background.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        background.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        background.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        add(background);
        background.setBounds(0, 0, 1030, 340);
    }// </editor-fold>//GEN-END:initComponents

    private void jlabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel1MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel1MousePressed

    private void jlabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel1MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel1MouseReleased

    private void jlabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel2MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel2MousePressed

    private void jlabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel2MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel2MouseReleased

    private void jlabel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel3MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel3MousePressed

    private void jlabel3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel3MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel3MouseReleased

    private void jlabel6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel6MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel6MousePressed

    private void jlabel6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel6MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel6MouseReleased

    private void jlabel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel5MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel5MousePressed

    private void jlabel5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel5MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel5MouseReleased

    private void jlabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel4MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel4MousePressed

    private void jlabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel4MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel4MouseReleased

    private void jlabel7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel7MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel7MousePressed

    private void jlabel7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel7MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel7MouseReleased

    private void jlabel8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel8MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel8MousePressed

    private void jlabel8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel8MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel8MouseReleased

    private void jlabel9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel9MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel9MousePressed

    private void jlabel9MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel9MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel9MouseReleased

    private void jlabelAceptarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelAceptarMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonAceptar2);
    }//GEN-LAST:event_jlabelAceptarMousePressed

    private void jlabelAceptarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelAceptarMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonAceptar1);
        pressReturn();
    }//GEN-LAST:event_jlabelAceptarMouseReleased

    private void jlabel0MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel0MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum2);
    }//GEN-LAST:event_jlabel0MousePressed

    private void jlabel0MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel0MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonNum1);
        getNumber(evt);
    }//GEN-LAST:event_jlabel0MouseReleased

    private void jlabelPPMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelPPMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelPPMousePressed

    private void jlabelPPMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelPPMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelPPMouseReleased

    private void jlabelYYMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelYYMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelYYMousePressed

    private void jlabelYYMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelYYMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelYYMouseReleased

    private void jlabelQQMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelQQMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelQQMousePressed

    private void jlabelQQMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelQQMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelQQMouseReleased

    private void jlabelWWMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelWWMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelWWMousePressed

    private void jlabelWWMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelWWMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelWWMouseReleased

    private void jlabelBorrarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelBorrarMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonBorrar2);
    }//GEN-LAST:event_jlabelBorrarMousePressed

    private void jlabelBorrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelBorrarMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonBorrar1);
        pressClear(evt);
    }//GEN-LAST:event_jlabelBorrarMouseReleased

    private void jlabelEEMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelEEMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelEEMousePressed

    private void jlabelEEMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelEEMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelEEMouseReleased

    private void jlabelOOMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelOOMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelOOMousePressed

    private void jlabelOOMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelOOMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelOOMouseReleased

    private void jlabelIIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelIIMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelIIMousePressed

    private void jlabelIIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelIIMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelIIMouseReleased

    private void jlabelLLMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelLLMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelLLMousePressed

    private void jlabelLLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelLLMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelLLMouseReleased

    private void jlabelMMMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelMMMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelMMMousePressed

    private void jlabelMMMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelMMMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelMMMouseReleased

    private void jlabelTTMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelTTMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelTTMousePressed

    private void jlabelTTMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelTTMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelTTMouseReleased

    private void jlabelRRMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelRRMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelRRMousePressed

    private void jlabelRRMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelRRMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelRRMouseReleased

    private void jlabelGGMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelGGMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelGGMousePressed

    private void jlabelGGMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelGGMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelGGMouseReleased

    private void jlabelJJMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelJJMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelJJMousePressed

    private void jlabelJJMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelJJMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelJJMouseReleased

    private void jlabelHHMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelHHMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelHHMousePressed

    private void jlabelHHMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelHHMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelHHMouseReleased

    private void jlabelDDMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelDDMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelDDMousePressed

    private void jlabelDDMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelDDMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelDDMouseReleased

    private void jlabelFFMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelFFMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelFFMousePressed

    private void jlabelFFMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelFFMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelFFMouseReleased

    private void jlabelKKMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelKKMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelKKMousePressed

    private void jlabelKKMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelKKMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelKKMouseReleased

    private void jlabelUUMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelUUMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelUUMousePressed

    private void jlabelUUMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelUUMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelUUMouseReleased

    private void jlabelAAMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelAAMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelAAMousePressed

    private void jlabelAAMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelAAMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelAAMouseReleased

    private void jlabelSSMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelSSMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelSSMousePressed

    private void jlabelSSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelSSMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelSSMouseReleased

    private void jlabelCCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelCCMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelCCMousePressed

    private void jlabelCCMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelCCMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelCCMouseReleased

    private void jlabelVVMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelVVMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelVVMousePressed

    private void jlabelVVMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelVVMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelVVMouseReleased

    private void jlabelBBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelBBMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelBBMousePressed

    private void jlabelBBMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelBBMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelBBMouseReleased

    private void jlabelXXMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelXXMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelXXMousePressed

    private void jlabelXXMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelXXMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelXXMouseReleased

    private void jlabelNNMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelNNMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelNNMousePressed

    private void jlabelNNMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelNNMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelNNMouseReleased

    private void jlabelZZMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelZZMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelZZMousePressed

    private void jlabelZZMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelZZMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelZZMouseReleased

    private void jlabelESPACIOMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelESPACIOMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonSpace2);
    }//GEN-LAST:event_jlabelESPACIOMousePressed

    private void jlabelESPACIOMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelESPACIOMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonSpace1);
        getNumber(evt);
    }//GEN-LAST:event_jlabelESPACIOMouseReleased

    private void jlabelGUIONMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelGUIONMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelGUIONMousePressed

    private void jlabelGUIONMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelGUIONMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelGUIONMouseReleased

    private void jlabelCOMAMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelCOMAMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelCOMAMousePressed

    private void jlabelCOMAMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelCOMAMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelCOMAMouseReleased

    private void jlabelPUNTOMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelPUNTOMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelPUNTOMousePressed

    private void jlabelPUNTOMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelPUNTOMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelPUNTOMouseReleased

    private void jlabelDOSPUNTOSMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelDOSPUNTOSMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelDOSPUNTOSMousePressed

    private void jlabelDOSPUNTOSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelDOSPUNTOSMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelDOSPUNTOSMouseReleased

    private void jlabelARROBAMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelARROBAMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelARROBAMousePressed

    private void jlabelARROBAMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelARROBAMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelARROBAMouseReleased

    private void jlabelNINIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelNINIMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelNINIMousePressed

    private void jlabelNINIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelNINIMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelNINIMouseReleased

    private void jlabelLINEAABAJOMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelLINEAABAJOMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelLINEAABAJOMousePressed

    private void jlabelLINEAABAJOMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelLINEAABAJOMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelLINEAABAJOMouseReleased

    private void jlabelPORCENTAJEMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelPORCENTAJEMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabelPORCENTAJEMousePressed

    private void jlabelPORCENTAJEMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelPORCENTAJEMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabelPORCENTAJEMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel jlabel0;
    private javax.swing.JLabel jlabel1;
    private javax.swing.JLabel jlabel2;
    private javax.swing.JLabel jlabel3;
    private javax.swing.JLabel jlabel4;
    private javax.swing.JLabel jlabel5;
    private javax.swing.JLabel jlabel6;
    private javax.swing.JLabel jlabel7;
    private javax.swing.JLabel jlabel8;
    private javax.swing.JLabel jlabel9;
    private javax.swing.JLabel jlabelAA;
    private javax.swing.JLabel jlabelARROBA;
    private javax.swing.JLabel jlabelAceptar;
    private javax.swing.JLabel jlabelBB;
    private javax.swing.JLabel jlabelBorrar;
    private javax.swing.JLabel jlabelCC;
    private javax.swing.JLabel jlabelCOMA;
    private javax.swing.JLabel jlabelDD;
    private javax.swing.JLabel jlabelDOSPUNTOS;
    private javax.swing.JLabel jlabelEE;
    private javax.swing.JLabel jlabelESPACIO;
    private javax.swing.JLabel jlabelFF;
    private javax.swing.JLabel jlabelGG;
    private javax.swing.JLabel jlabelGUION;
    private javax.swing.JLabel jlabelHH;
    private javax.swing.JLabel jlabelII;
    private javax.swing.JLabel jlabelJJ;
    private javax.swing.JLabel jlabelKK;
    private javax.swing.JLabel jlabelLINEAABAJO;
    private javax.swing.JLabel jlabelLL;
    private javax.swing.JLabel jlabelMM;
    private javax.swing.JLabel jlabelNINI;
    private javax.swing.JLabel jlabelNN;
    private javax.swing.JLabel jlabelOO;
    private javax.swing.JLabel jlabelPORCENTAJE;
    private javax.swing.JLabel jlabelPP;
    private javax.swing.JLabel jlabelPUNTO;
    private javax.swing.JLabel jlabelQQ;
    private javax.swing.JLabel jlabelRR;
    private javax.swing.JLabel jlabelSS;
    private javax.swing.JLabel jlabelTT;
    private javax.swing.JLabel jlabelUU;
    private javax.swing.JLabel jlabelVV;
    private javax.swing.JLabel jlabelWW;
    private javax.swing.JLabel jlabelXX;
    private javax.swing.JLabel jlabelYY;
    private javax.swing.JLabel jlabelZZ;
    // End of variables declaration//GEN-END:variables

    private void getNumber(MouseEvent evt) {
        NovusUtils.beep();
        JLabel jla = (JLabel) evt.getComponent();

        if (jla.isEnabled()) {

            KeyStroke ks = KeyStroke.getKeyStroke(jla.getText());
            try {
                if (ks == null) {
                    JTextField focusOwner = (JTextField) FocusManager.getCurrentManager().getFocusOwner();
                    int k = 0;
                    switch (((JLabel) evt.getComponent()).getText()) {
                        case ".":
                            k = KeyEvent.VK_PERIOD;
                            break;
                        case "ESPACIO":
                            k = KeyEvent.VK_SPACE;
                            break;
                        case "-":
                            k = KeyEvent.VK_MINUS;
                           break;                            
                        case "_":
                            focusOwner.setText(focusOwner.getText() + "_");
                            return;
                        case "%":
                            focusOwner.setText(focusOwner.getText() + "%");
                            return;
                        case ",":
                            k = KeyEvent.VK_COMMA;
                            break;
                        case ":":
                            focusOwner.setText(focusOwner.getText() + ":");
                            return;
                        case "/":
                            k = KeyEvent.VK_SLASH;
                            break;
                        case "@":
                            focusOwner.setText(focusOwner.getText() + "@");
                            return;
                        case "Ñ":
//                            focusOwner.setText(focusOwner.getText() + "Ñ");
                            k = 209;
//                            return;
                            break;
                        default:
                            break;
                    }
                    robot.keyPress(k);
                    robot.keyRelease(k);
                } else {
                    if (!isNumeric(((char) ks.getKeyCode()) + "")) {
                        robot.keyPress(KeyEvent.VK_SHIFT);
                        robot.keyPress(ks.getKeyCode());
                        robot.keyRelease(ks.getKeyCode());
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                        
                    } else {
                        robot.keyPress(ks.getKeyCode());
                        robot.keyRelease(ks.getKeyCode());
                    }
                }
            } catch (Exception a) {
                System.out.println(Main.ANSI_RED + "Error " + a + Main.ANSI_RESET);
            }
        }
    }

    private void pressReturn() {
        NovusUtils.beep();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private void pressClear(MouseEvent evt) {
        NovusUtils.beep();
        JLabel jla = (JLabel) evt.getComponent();
        if (jla.isEnabled()) {
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void habilitarAlfanumeric(boolean activar) {

        jlabelQQ.setEnabled(activar);
        jlabelWW.setEnabled(activar);
        jlabelEE.setEnabled(activar);
        jlabelRR.setEnabled(activar);
        jlabelTT.setEnabled(activar);
        jlabelYY.setEnabled(activar);
        jlabelUU.setEnabled(activar);
        jlabelII.setEnabled(activar);
        jlabelOO.setEnabled(activar);
        jlabelPP.setEnabled(activar);

        jlabelAA.setEnabled(activar);
        jlabelSS.setEnabled(activar);
        jlabelDD.setEnabled(activar);
        jlabelFF.setEnabled(activar);
        jlabelGG.setEnabled(activar);
        jlabelHH.setEnabled(activar);
        jlabelJJ.setEnabled(activar);
        jlabelKK.setEnabled(activar);
        jlabelLL.setEnabled(activar);
        jlabelNINI.setEnabled(activar);

        jlabelZZ.setEnabled(activar);
        jlabelXX.setEnabled(activar);
        jlabelCC.setEnabled(activar);
        jlabelVV.setEnabled(activar);
        jlabelBB.setEnabled(activar);
        jlabelNN.setEnabled(activar);
        jlabelMM.setEnabled(activar);

        jlabelCOMA.setEnabled(activar);
        jlabelPUNTO.setEnabled(true);
        jlabelESPACIO.setEnabled(activar);
        jlabelPORCENTAJE.setEnabled(activar);
        jlabelGUION.setEnabled(activar);
        jlabelARROBA.setEnabled(activar);
        jlabelDOSPUNTOS.setEnabled(activar);
        jlabelLINEAABAJO.setEnabled(activar);

    }

    public void activarTeclasSoloPlaca(boolean activar) {

        jlabelCOMA.setEnabled(activar);
        jlabelPUNTO.setEnabled(activar);
        jlabelESPACIO.setEnabled(activar);
        jlabelGUION.setEnabled(activar);
        jlabelARROBA.setEnabled(activar);
        jlabelDOSPUNTOS.setEnabled(activar);
        jlabelLINEAABAJO.setEnabled(activar);
    }

    public void habilitarPunto(boolean activar) {
        jlabelPUNTO.setEnabled(activar);
    }
    
    public void deshabilitarCaracteresEspeciales(boolean activar){
        jlabelGUION.setEnabled(activar);
        jlabelARROBA.setEnabled(activar);
        jlabelLINEAABAJO.setEnabled(activar);
        jlabelPORCENTAJE.setEnabled(activar);
        jlabelDOSPUNTOS.setEnabled(activar);
        jlabelCOMA.setEnabled(activar);
    }
    public void habilitarPorcentaje(boolean activar){
        jlabelPORCENTAJE.setEnabled(activar);
    }

}

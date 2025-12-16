/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teclado.view.common;

import com.controllers.NovusUtils;
import com.controllers.NovusConstante;
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
import javax.swing.KeyStroke;

/**
 *
 * @author ASUS-PC
 */
public class TecladoNumericoGrayDot extends JPanel {

    public static String texto = "";

    ImageIcon botonOn = new ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"));
    ImageIcon botonOff = new ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDotPress.png"));

    ImageIcon botonAceptar1 = new ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericOkLarge.png"));
    ImageIcon botonAceptar2 = new ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericOkLargePress.png"));

    ImageIcon botonBorrar1 = new ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericDeleteGraySmall.png"));
    ImageIcon botonBorrar2 = new ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericDeleteGraySmallPress.png"));

    Robot robot;
    private boolean presiono;

    public TecladoNumericoGrayDot() {
        initComponents();
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(TecladoNumericoGrayDot.class.getName()).log(Level.SEVERE, null, ex);
        }
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);
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
        jlabel0 = new javax.swing.JLabel();
        jlabel10 = new javax.swing.JLabel();
        jlabelBorrar = new javax.swing.JLabel();
        jlabelAceptar = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jlabel1.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel1.setForeground(new java.awt.Color(75, 74, 91));
        jlabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel1.setBounds(26, 16, 110, 80);

        jlabel2.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel2.setForeground(new java.awt.Color(75, 74, 91));
        jlabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel2.setBounds(148, 16, 110, 80);

        jlabel3.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel3.setForeground(new java.awt.Color(75, 74, 91));
        jlabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel3.setBounds(268, 16, 110, 80);

        jlabel6.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel6.setForeground(new java.awt.Color(75, 74, 91));
        jlabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel6.setBounds(268, 98, 110, 80);

        jlabel5.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel5.setForeground(new java.awt.Color(75, 74, 91));
        jlabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel5.setBounds(148, 98, 110, 80);

        jlabel4.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel4.setForeground(new java.awt.Color(75, 74, 91));
        jlabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel4.setBounds(26, 96, 110, 80);

        jlabel7.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel7.setForeground(new java.awt.Color(75, 74, 91));
        jlabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel7.setBounds(26, 178, 110, 80);

        jlabel8.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel8.setForeground(new java.awt.Color(75, 74, 91));
        jlabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel8.setBounds(148, 180, 110, 80);

        jlabel9.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel9.setForeground(new java.awt.Color(75, 74, 91));
        jlabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel9.setBounds(268, 180, 110, 80);

        jlabel0.setFont(new java.awt.Font("Conthrax", 1, 46)); // NOI18N
        jlabel0.setForeground(new java.awt.Color(75, 74, 91));
        jlabel0.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
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
        jlabel0.setBounds(26, 260, 110, 80);

        jlabel10.setFont(new java.awt.Font("Conthrax", 1, 60)); // NOI18N
        jlabel10.setForeground(new java.awt.Color(75, 74, 91));
        jlabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericGrayDot.png"))); // NOI18N
        jlabel10.setText(".");
        jlabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlabel10.setName("0"); // NOI18N
        jlabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlabel10MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jlabel10MouseReleased(evt);
            }
        });
        add(jlabel10);
        jlabel10.setBounds(148, 260, 110, 80);

        jlabelBorrar.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelBorrar.setForeground(new java.awt.Color(255, 255, 255));
        jlabelBorrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericDeleteGraySmall.png"))); // NOI18N
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
        jlabelBorrar.setBounds(268, 260, 110, 80);

        jlabelAceptar.setFont(new java.awt.Font("Conthrax", 0, 48)); // NOI18N
        jlabelAceptar.setForeground(new java.awt.Color(255, 255, 255));
        jlabelAceptar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabelAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/keyNumericOkLarge.png"))); // NOI18N
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
        jlabelAceptar.setBounds(27, 340, 350, 80);

        background.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/teclado/view/resources/tecladoNumericGrayDot.png"))); // NOI18N
        background.setToolTipText("");
        add(background);
        background.setBounds(0, 0, 400, 440);
    }// </editor-fold>//GEN-END:initComponents

    private void jlabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel1MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel1MousePressed

    private void jlabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel1MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel1MouseReleased

    private void jlabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel2MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel2MousePressed

    private void jlabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel2MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel2MouseReleased

    private void jlabel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel3MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel3MousePressed

    private void jlabel3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel3MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel3MouseReleased

    private void jlabel6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel6MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel6MousePressed

    private void jlabel6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel6MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel6MouseReleased

    private void jlabel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel5MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel5MousePressed

    private void jlabel5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel5MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel5MouseReleased

    private void jlabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel4MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel4MousePressed

    private void jlabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel4MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel4MouseReleased

    private void jlabel7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel7MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel7MousePressed

    private void jlabel7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel7MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel7MouseReleased

    private void jlabel8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel8MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel8MousePressed

    private void jlabel8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel8MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel8MouseReleased

    private void jlabel9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel9MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel9MousePressed

    private void jlabel9MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel9MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel9MouseReleased

    private void jlabel0MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel0MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel0MousePressed

    private void jlabel0MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel0MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel0MouseReleased

    private void jlabelBorrarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelBorrarMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonBorrar2);
    }//GEN-LAST:event_jlabelBorrarMousePressed

    private void jlabelBorrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelBorrarMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonBorrar1);
        pressClear(evt);
    }//GEN-LAST:event_jlabelBorrarMouseReleased

    private void jlabel10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel10MousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOff);
    }//GEN-LAST:event_jlabel10MousePressed

    private void jlabel10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabel10MouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonOn);
        getNumber(evt);
    }//GEN-LAST:event_jlabel10MouseReleased

    private void jlabelAceptarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelAceptarMousePressed
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonAceptar2);
    }//GEN-LAST:event_jlabelAceptarMousePressed

    private void jlabelAceptarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlabelAceptarMouseReleased
        JLabel buton = (JLabel) evt.getComponent();
        buton.setIcon(botonAceptar1);
        pressReturn();
    }//GEN-LAST:event_jlabelAceptarMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel jlabel0;
    private javax.swing.JLabel jlabel1;
    private javax.swing.JLabel jlabel10;
    private javax.swing.JLabel jlabel2;
    private javax.swing.JLabel jlabel3;
    private javax.swing.JLabel jlabel4;
    private javax.swing.JLabel jlabel5;
    private javax.swing.JLabel jlabel6;
    private javax.swing.JLabel jlabel7;
    private javax.swing.JLabel jlabel8;
    private javax.swing.JLabel jlabel9;
    private javax.swing.JLabel jlabelAceptar;
    private javax.swing.JLabel jlabelBorrar;
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

    private void pressReturn() {
        NovusUtils.beep();
        java.awt.Toolkit.getDefaultToolkit().beep();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        presiono = true;
    }

    private void pressClear(MouseEvent evt) {
        NovusUtils.beep();
        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
    }

    public boolean isPresiono() {
        return presiono;
    }

    public void setPresiono(boolean presiono) {
        this.presiono = presiono;
    }

}

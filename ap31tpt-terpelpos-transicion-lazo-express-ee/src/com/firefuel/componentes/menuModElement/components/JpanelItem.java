/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.componentes.menuModElement.components;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author USUARIO
 */
public class JpanelItem extends JPanel {

    private JLabel labelNumber;
    private JLabel labelTexto;
    private JLabel labelBackGround;
    public JpanelItem(JLabel labelNumber, JLabel labelTexto, JLabel lebelImage) {
        this.labelNumber = labelNumber;
        this.labelTexto = labelTexto;
        this.labelBackGround = lebelImage;
        init();
    }

    private void init() {
        
        this.setBackground(new java.awt.Color(255, 255, 255));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.setLayout(null);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
      //   Random rand = new Random();
       // this.setBackground(new java.awt.Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        this.setBounds(0, 0, 400, 90);
        labelNumber.setFont(new java.awt.Font("Impact", 1, 36));
        labelNumber.setForeground(new java.awt.Color(186, 12, 47));
        labelNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelNumber.setBounds(30, 15, 70, 40);

        labelTexto.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        labelTexto.setForeground(new java.awt.Color(255, 255, 255));
        
        labelTexto.setBounds(110, 15, 290, 60);

        labelBackGround.setBounds(20, 0, this.getWidth(), this.getHeight());
        this.setBounds(0, 0, 400, 90);
        this.add(labelTexto);
        this.add(labelNumber);
        this.add(labelBackGround);
        this.setVisible(true);
    }

    public void setNumerLable(String numberValue) {
        this.labelNumber.setText(numberValue);
    }

    public void setTextLable(String textValue) {
        this.labelTexto.setText(textValue);
    }

    public void setLebelImage(ImageIcon setIcon) {

        this.labelBackGround.setIcon(setIcon);
    }

}

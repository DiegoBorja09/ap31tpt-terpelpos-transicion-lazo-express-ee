/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.components.panelesPersonalizados;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Devitech
 */
public class ScrollBar {
    public void scroll(JScrollPane scroll){
         scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        scroll.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        scroll.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    }
}

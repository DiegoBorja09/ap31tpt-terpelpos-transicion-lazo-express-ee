/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.components.panelesPersonalizados;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Devitech
 */
public class BordesRedondos extends AbstractBorder {

    private Color color = new Color(70,73,75);
    private int radio = 20;
    private int paddingLeft = 10;
    private int paddingTop = 10;
    private int paddingRight = 10;
    private int paddingBottom = 10;

    public BordesRedondos(Color color) {
        this.color = color;
    }
    
    public BordesRedondos(Color color, int radio) {
        this.color = color;
        this.radio = radio;
    }
    
    public BordesRedondos(Color color, int radio, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.color = color;
        this.radio = radio;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
    }

    public BordesRedondos() {

    }

    @Override
    public void paintBorder(
            Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = this.radio;
        int w = width - 1;
        int h = height - 1;

        Area round = new Area(new RoundRectangle2D.Float(0, 0, w, h, r, r));
        Container parent = c.getParent();
        if (parent != null) {
            g2.setColor(parent.getBackground());
            g2.setBackground(parent.getBackground());
            g2.setPaint(parent.getBackground());
            Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
            corner.subtract(round);
            g2.fill(corner);
        }
        g2.setPaint(color);
        g2.draw(round);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(paddingTop, paddingLeft, paddingBottom, paddingRight);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = paddingRight;
        insets.top = insets.bottom = paddingBottom;
        return insets;
    }
}

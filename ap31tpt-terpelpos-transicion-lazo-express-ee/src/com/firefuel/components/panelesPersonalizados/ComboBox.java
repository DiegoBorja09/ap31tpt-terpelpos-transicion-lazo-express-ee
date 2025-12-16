/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.components.panelesPersonalizados;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 *
 * @author Devitech
 */
public class ComboBox extends BasicComboBoxUI {

    private Color red = new Color(153, 3, 3);
    private Color blanco = new Color(255, 255, 255);
    private int roundTopLeft = 0;
    private int roundTopRight = 20;
    private int roundBottomLeft = 0;
    private int roundBottomRight = 20;
    private static int centerxFlecha = 25;
    private static int centerYFlecha = 25;

    public int getRoundTopLeft() {
        return roundTopLeft;
    }

    public void setRoundTopLeft(int roundTopLeft) {
        this.roundTopLeft = roundTopLeft;

    }

    public int getRoundTopRight() {
        return roundTopRight;
    }

    public void setRoundTopRight(int roundTopRight) {
        this.roundTopRight = roundTopRight;

    }

    public int getRoundBottomLeft() {
        return roundBottomLeft;
    }

    public void setRoundBottomLeft(int roundBottomLeft) {
        this.roundBottomLeft = roundBottomLeft;

    }

    public int getRoundBottomRight() {
        return roundBottomRight;
    }

    public void setRoundBottomRight(int roundBottomRight) {
        this.roundBottomRight = roundBottomRight;

    }

    public static ComboBoxUI createUI(JComponent c) {
        return new ComboBox();
    }
    
    public static ComboBoxUI createUI(JComponent c, int x, int y) {
        centerxFlecha = x;
        centerYFlecha = y;
        return new ComboBox();
    }

    @Override
    protected JButton createArrowButton() {
        return new RoundedButton();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        RoundedButton button = new RoundedButton();
        Area area = new Area(button.createRoundTopLeft());

        if (roundTopRight > 0) {
            area.intersect(new Area(button.createRoundTopRight()));
        }
        if (roundBottomLeft > 0) {
            area.intersect(new Area(button.createRoundBottomLeft()));
        }
        if (roundBottomRight > 0) {
            area.intersect(new Area(button.createRoundBottomRight()));
        }
        if (button.getModel().isPressed()) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fill(area);
        Shape oldClip = g2.getClip();
        g2.clip(new RoundRectangle2D.Double(0, 0, (c.getWidth() - 20), (c.getHeight() - 20), 20, 20));
        g2.copyArea(0, 0, c.getWidth() - 1, c.getHeight() - 20, 20, 20);
        super.paint(g2, c);

        g2.setClip(oldClip);
    }

    @Override
    public void paintCurrentValueBackground(Graphics graphics, Rectangle rectangle, boolean hasFocus) {
        graphics.setColor(blanco);
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height - 20);
    }

    /**
     *
     * @return
     */
    @Override
    protected ListCellRenderer<Object> createRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                list.setSelectionBackground(blanco);
                if (isSelected) {
                    setBackground(red);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        };
    }

    private class RoundedButton extends JButton {

        private static final int BUTTON_WIDTH = 10;
        private static final int BUTTON_HEIGHT = 5;
        private static final int ARROW_SIZE = 20;

        public RoundedButton() {
            setOpaque(false);
            setContentAreaFilled(true);

            setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            int centerX = centerxFlecha;
            int centerY = centerYFlecha;

            int[] xPoints = {centerX - ARROW_SIZE / 2, centerX, centerX + 20 / 2};
            int[] yPoints = {centerY - 2, centerY + 10, centerY - 2};
            Path2D path = new Path2D.Double();
            path.moveTo(xPoints[0], yPoints[0]);
            for (int i = 1; i < xPoints.length; i++) {
                path.lineTo(xPoints[i], yPoints[i]);
            }
            g2.draw(path);
        }

        private Shape createRoundTopLeft() {
            int width = getWidth();
            int height = getHeight();
            int roundX = Math.min(width, roundTopLeft);
            int roundY = Math.min(height, roundTopLeft);
            Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
            area.add(new Area(new Rectangle2D.Double((roundX / 2), 0, (width - roundX / 2), height)));
            area.add(new Area(new Rectangle2D.Double(0, (roundY / 2), width, ((height - roundY) / 2))));
            return area;
        }

        private Shape createRoundTopRight() {
            int width = getWidth();
            int height = getHeight();
            int roundX = Math.min(width, roundTopRight);
            int roundY = Math.min(height, roundTopRight);
            Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
            area.add(new Area(new Rectangle2D.Double(0, 0, (width - roundX / 2), height)));
            area.add(new Area(new Rectangle2D.Double(0, (roundY / 2), width, (height - roundY / 2))));
            return area;
        }

        private Shape createRoundBottomLeft() {
            int width = getWidth();
            int height = getHeight();
            int roundX = Math.min(width, roundBottomLeft);
            int roundY = Math.min(height, roundBottomLeft);
            Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
            area.add(new Area(new Rectangle2D.Double((roundX / 2), 0, (width - roundX / 2), height)));
            area.add(new Area(new Rectangle2D.Double(0, 0, width, (height - roundY / 2))));
            return area;
        }

        private Shape createRoundBottomRight() {
            int width = getWidth();
            int height = getHeight();
            int roundX = Math.min(width, roundBottomRight);
            int roundY = Math.min(height, roundBottomRight);
            Area area = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
            area.add(new Area(new Rectangle2D.Double(0, 0, (width - roundX / 2), height)));
            area.add(new Area(new Rectangle2D.Double(0, 0, width, (height - roundY / 2))));
            return area;
        }
    }

    public class RoundBorder extends AbstractBorder {

        Color bgColor = new Color(0, 0, 0, 220);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setColor(bgColor);
            ((Graphics2D) g).drawRoundRect(x, y, width - 1, height - 1, 20, 20);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(20, 20, 20, 20);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = insets.left = insets.bottom = insets.right = 20;
            return insets;
        }
    }

}

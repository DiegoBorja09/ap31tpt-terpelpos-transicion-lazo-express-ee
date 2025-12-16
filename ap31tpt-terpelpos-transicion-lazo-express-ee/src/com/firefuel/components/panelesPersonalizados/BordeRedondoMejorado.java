/*
 * Borde redondeado mejorado con renderizado de alta calidad
 * Sin imperfecciones en las esquinas
 * 
 * @author Diego Borja Padilla
 */
package com.firefuel.components.panelesPersonalizados;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;

/**
 * Borde redondeado mejorado con renderizado de alta calidad
 * @author Diego Borja Padilla
 */
public class BordeRedondoMejorado extends AbstractBorder {

    private Color color;
    private int radio;
    private float grosor;

    public BordeRedondoMejorado(Color color, int radio, float grosor) {
        this.color = color;
        this.radio = radio;
        this.grosor = grosor;
    }

    public BordeRedondoMejorado(Color color, int radio) {
        this(color, radio, 3.0f);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        g2.setStroke(new BasicStroke(
            grosor, 
            BasicStroke.CAP_ROUND, 
            BasicStroke.JOIN_ROUND,
            10.0f,
            null,
            0.0f
        ));
        
        g2.setColor(color);
        
        float offset = grosor / 2.0f;
        float w = width - grosor;
        float h = height - grosor;
        
        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(
            x + offset, 
            y + offset, 
            w, 
            h, 
            radio, 
            radio
        );
        
        g2.draw(rect);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int inset = (int) Math.ceil(grosor);
        return new Insets(inset, inset, inset, inset);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        int inset = (int) Math.ceil(grosor);
        insets.left = insets.top = insets.right = insets.bottom = inset;
        return insets;
    }
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.components;

import com.controllers.NovusUtils;
import com.firefuel.facturacion.electronica.VentasSinResolverFE;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Devitech
 */
public class RenderTablaColor extends DefaultTableCellRenderer {

    private Border borde;
    private Color BackgrounColorSelected;
    private Color colorSelected;
     private Color DefaultBackgrounColor;
    private Color DefaultColor;
    
    public RenderTablaColor() {
        super();
        borde = BorderFactory.createCompoundBorder();
        borde = BorderFactory.createCompoundBorder(borde, BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(37, 34, 27)));
    }

    private Map<Long, VentasSinResolverFE> datosExternos;

    public void datos(Map<Long, VentasSinResolverFE> llenar) {
        this.datosExternos = llenar;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component componente = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        try {
            setHorizontalAlignment(JLabel.RIGHT);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

            table.setFillsViewportHeight(true);
            setHorizontalAlignment(JLabel.CENTER);
            
            table.setBackground(Color.WHITE);
            table.getTableHeader().setBackground(Color.WHITE);
            table.setSelectionBackground(new Color(255, 182, 0));
            table.setSelectionForeground(new Color(0, 0, 0));
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int j = 0; j < table.getColumnCount();j++) {
                table.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
            }

        } catch (Exception e) {
            NovusUtils.printLn(" error en el renderizado de la tabla de ventas de combustibles "+ e.getMessage());
        }

        return componente;
    }

}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.components;

import com.controllers.NovusUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Devitech
 */
public class SelectRenderRow extends DefaultTableCellRenderer {

    private Color BackgrounColorSelected;
    private Color colorSelected;
    private int rowSelected  = 0;
    private int ColumnSelected  = 0;
    
    public SelectRenderRow() {
        super();
    }

   public void setDatos(int r , int c , Color back,Color color){
       rowSelected = r;
       ColumnSelected = c;
       colorSelected = color;
       BackgrounColorSelected = back;
   }
      

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component componente = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        try {
            if(row == rowSelected && ColumnSelected ==  column){
                setBackground(BackgrounColorSelected);
                setForeground(colorSelected);
            }
        } catch (Exception e) {
            NovusUtils.printLn( e.getMessage());
        }

        return componente;
    }

}

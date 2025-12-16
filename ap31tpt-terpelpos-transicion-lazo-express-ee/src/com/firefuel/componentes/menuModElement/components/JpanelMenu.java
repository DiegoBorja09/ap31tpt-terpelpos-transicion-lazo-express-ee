/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.componentes.menuModElement.components;

import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author USUARIO
 */
public class JpanelMenu {

    private JPanel jPanelRightMenu;
    private ArrayList<JpanelItem> listItems;
    private int minItems;

    public JPanel getjPanelRightMenu() {
        return jPanelRightMenu;
    }

    public void setjPanelRightMenu(JPanel jPanelRightMenu) {
        this.jPanelRightMenu = jPanelRightMenu;
    }

    public ArrayList<JpanelItem> getListItems() {
        return listItems;
    }

    public void setListItems(ArrayList<JpanelItem> listItems) {
        this.listItems = listItems;
    }

    public int getMinItems() {
        return minItems;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }
    
    

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.componentes.menuModElement.renderization;

import com.firefuel.componentes.menuModElement.components.JpanelItem;
import com.firefuel.componentes.menuModElement.components.JpanelMenu;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author USUARIO
 */
public class LoadMenu {

    public void load(JpanelMenu jpanelMenu) {

        JPanel parentPanel = jpanelMenu.getjPanelRightMenu();
        ArrayList<JpanelItem> items = jpanelMenu.getListItems();
        parentPanel.removeAll();
        parentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (int i = 0; i < jpanelMenu.getMinItems(); i++) {

            if (i < items.size()) {
                JpanelItem item2Render = items.get(i);
                item2Render.setNumerLable(i+1+"");
                parentPanel.add(item2Render);
            } else {

                parentPanel.add(new JpanelItem(new JLabel(),new JLabel(),new JLabel()));

            }

        }
        
         parentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    }

}

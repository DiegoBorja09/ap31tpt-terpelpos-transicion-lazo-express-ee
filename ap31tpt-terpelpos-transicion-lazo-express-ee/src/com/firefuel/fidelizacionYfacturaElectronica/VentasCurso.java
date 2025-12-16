/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.fidelizacionYfacturaElectronica;

import com.controllers.NovusUtils;
import com.firefuel.TextPrompt;
import com.firefuel.VentaCursoPlaca;
import com.firefuel.components.panelesPersonalizados.BordesRedondos;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author Devitech
 */
public class VentasCurso {
    
    public void cambiarApariencia(VentaCursoPlaca cursoPlaca) {
        BordesRedondos bordesRedondos = new BordesRedondos(new Color(242, 241, 247), 30);
        cursoPlaca.comboTiposIdentificacion.setBackground(Color.WHITE);
        cursoPlaca.comboTiposIdentificacion.setBorder(createCustomBorder(new Color(242, 241, 247)));
        VentaCursoPlaca.txtCliente.setBorder(bordesRedondos);
        VentaCursoPlaca.jTextField1.setBorder(bordesRedondos);
        VentaCursoPlaca.jTextField2.setBorder(bordesRedondos);
        VentaCursoPlaca.jTextField3.setBorder(bordesRedondos);
        textoFalso(VentaCursoPlaca.jTextField1, "Ingrese placa");
        textoFalso(VentaCursoPlaca.jTextField2, "Kilometraje");
        textoFalso(VentaCursoPlaca.jTextField3, "Comprobante");
        textoFalso(VentaCursoPlaca.txtCliente, "Número de documento");
        
    }
    
    private void textoFalso(JTextField field, String texto) {
        TextPrompt placeholder = new TextPrompt(texto, field);
        placeholder.changeAlpha(0.80f);
        placeholder.setFont(new java.awt.Font("Arial", Font.PLAIN, 24));
    }
    
    public void cargarTiposDocumentos(VentaCursoPlaca cursoPlaca, boolean tipos) {
        if (tipos) {
            llenarCampoTipoIdentificacionFidelizacion(cursoPlaca);
        } else {
            llenarCampoTipoIdentificacion(cursoPlaca);
        }
    }
    
    public void llenarCampoTipoIdentificacion(VentaCursoPlaca cursoPlaca) {
        cursoPlaca.comboTiposIdentificacion.removeAllItems();
        NovusUtils.llenarComboBox( cursoPlaca.comboTiposIdentificacion);
    }
    
    public void llenarCampoTipoIdentificacionFidelizacion(VentaCursoPlaca cursoPlaca) {
        cursoPlaca.comboTiposIdentificacion.removeAllItems();
        NovusUtils.llenarComboFidelizacion(cursoPlaca.comboTiposIdentificacion);
    }
    
    public void cambiarPanel(JPanel panel, String nombre) {
        CardLayout layout = (CardLayout) panel.getLayout();
        layout.show(panel, nombre);
    }
    
    public void cargarPanelExterno(JPanel principal, JPanel view) {
        principal.removeAll();
        principal.add(view);
        principal.revalidate();
        principal.repaint();
    }
    
    private static Border createCustomBorder(Color color) {
        int thickness = 2; 
        int radius = 6; 
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(radius, radius, radius, radius)
        );
    }
    
}

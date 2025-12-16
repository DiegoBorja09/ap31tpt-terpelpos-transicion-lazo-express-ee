/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.cerrandoVentana;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;

/**
 *
 * @author Usuario
 */
public class CerrandoVentanasActivas extends TimerTask {

    JDialog dialog = null;
    Timer tareas = new Timer();
    
    public CerrandoVentanasActivas(JDialog dialog, Timer tarea) {
        this.dialog = dialog;
        this.tareas = tarea;
    }

    @Override
    public void run() {
        cerrarVentana();
    }

    public void cerrarVentana() {
        dialog.dispose();
        String estado = dialog.isVisible() ? "avtiva" :"inactiva";
        NovusUtils.printLn("el estado de la ventana es "+ estado);
        dialog.setVisible(false);
        estado = dialog.isVisible() ? "avtiva" :"inactiva";
        NovusUtils.printLn("Estoy ejecutando el cerrado de la venta de emergencia");
        NovusUtils.printLn("el estado de la ventana es "+ estado);
        NovusConstante.cerrarVentanaActivo = false;
        tareas.cancel();
    }
}

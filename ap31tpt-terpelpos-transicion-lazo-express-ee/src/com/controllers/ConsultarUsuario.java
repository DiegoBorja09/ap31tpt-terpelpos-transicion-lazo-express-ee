/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.controllers;

import com.application.useCases.persons.EliminarLecturaUseCase;
import com.application.useCases.persons.ObtenerLecturaTagUseCase;
import com.application.useCases.persons.ObtenerUsuarioUseCase;
import com.bean.PersonaBean;
import com.dao.PersonasDao;
import com.firefuel.TurnosIniciarViewController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Devitech
 */
public class ConsultarUsuario extends Thread {

    private volatile boolean consultar = true;
    private ScheduledExecutorService scheduler;

    @Override
    public void run() {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(() -> {
            if (!consultar) {

                scheduler.shutdown();
                return;
            }

            if (TurnosIniciarViewController.jNotificacion != null &&
                    TurnosIniciarViewController.juser != null) {
                TurnosIniciarViewController.jNotificacion.setText("");
                String user = TurnosIniciarViewController.juser.getText();

                if (user.isEmpty()) {
                    iniciar();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void iniciar() {
        PersonasDao personas = new PersonasDao();
        ObtenerLecturaTagUseCase obtenerLecturaTagUseCase = new ObtenerLecturaTagUseCase();
        // String lectura = personas.obtenerLecturaTag();
         String lectura = obtenerLecturaTagUseCase.execute();

        if (lectura != null) {
            ObtenerUsuarioUseCase obtenerUsuarioUseCase = new ObtenerUsuarioUseCase();
            //PersonaBean personabean = personas.obtenerUsuario(lectura);
            PersonaBean personabean = obtenerUsuarioUseCase.execute(lectura);

            EliminarLecturaUseCase eliminarLecturaUseCase = new EliminarLecturaUseCase();
            //personas.eliminarLectura();
            eliminarLecturaUseCase.execute();
            TurnosIniciarViewController.instance.iniciar(personabean);
        }
    }

    public void detenerProceso() {
        consultar = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

}

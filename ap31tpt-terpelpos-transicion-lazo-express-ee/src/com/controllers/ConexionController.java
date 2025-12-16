package com.controllers;

import com.interfaces.ConexionView;
import com.services.ConexionService;

public class ConexionController {
    private final ConexionService service;
    private final ConexionView view;
    private boolean estadoAnterior = false;

    public ConexionController(ConexionService service, ConexionView view) {
        this.service = service;
        this.view = view;
    }

    public void verificarConexion() {
        boolean conectado = service.tieneConexionInternet();

        if (conectado != estadoAnterior) {
            view.mostrarEstadoConexion(conectado);
        }

        estadoAnterior = conectado;
        view.refrescarIcono();

        NovusConstante.HAY_INTERNET = conectado;
        ///System.out.println("[DEBUG] Internet actualizado: " + conectado);

    }
}

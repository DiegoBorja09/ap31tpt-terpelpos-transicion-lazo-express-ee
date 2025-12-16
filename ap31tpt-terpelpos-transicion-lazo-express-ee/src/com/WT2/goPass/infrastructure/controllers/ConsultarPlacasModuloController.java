/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.infrastructure.controllers;

import com.WT2.goPass.application.useCase.ConsultarPlacasModuloGoPass;
import com.WT2.goPass.domain.entity.response.ConsultarPlacasResponse;

/**
 *
 * @author USUARIO
 */
public class ConsultarPlacasModuloController {

    private ConsultarPlacasModuloGoPass consultarPlacasModuloGoPass;

    public ConsultarPlacasModuloController(ConsultarPlacasModuloGoPass consultarPlacasModuloGoPass) {
        this.consultarPlacasModuloGoPass = consultarPlacasModuloGoPass;
    }

    public ConsultarPlacasResponse execute(Integer idMovimiento) {
        return this.consultarPlacasModuloGoPass.execute(idMovimiento);
    }

}

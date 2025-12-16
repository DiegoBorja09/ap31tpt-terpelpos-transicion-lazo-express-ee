/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.infrastructure.controllers;

import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.goPass.domain.entity.request.ConsultarPlacaStatusPumRequestBody;
import com.WT2.goPass.domain.entity.response.ConsultarPlacasResponse;

/**
 *
 * @author USUARIO
 */
public class ConsultarPlacasStatusPumController {

    private IUseCase<ConsultarPlacaStatusPumRequestBody, ConsultarPlacasResponse> consultarPlacasStatusPum;

    public ConsultarPlacasStatusPumController(IUseCase<ConsultarPlacaStatusPumRequestBody, ConsultarPlacasResponse> consultarPlacasStatusPum) {
        this.consultarPlacasStatusPum = consultarPlacasStatusPum;
    }

    public ConsultarPlacasResponse execute(ConsultarPlacaStatusPumRequestBody input) {
        return this.consultarPlacasStatusPum.execute(input);

    }
}

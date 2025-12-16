/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.infrastructure.services;

import com.WT2.commons.domain.adapters.IService;
import com.WT2.goPass.domain.entity.dto.GopassParametersDto;
import com.WT2.goPass.infrastructure.db.repositories.GopassParametersRepository;
import com.controllers.NovusUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedList;
import java.lang.reflect.Type;

import java.sql.ResultSet;

/**
 *
 * @author Jeison
 */
public class GopassParametersService implements IService<ResultSet, LinkedList<GopassParametersDto>> {

    private GopassParametersRepository gopassrepo;

    public GopassParametersService(GopassParametersRepository gopassrepo) {
        this.gopassrepo = gopassrepo;
    }

    @Override
    public LinkedList<GopassParametersDto> execute(ResultSet input) {
        ResultSet rs = this.gopassrepo.getData(null);
        LinkedList<GopassParametersDto> parametersGopassDto = new LinkedList<>();
        try {
            if (rs.next()) {
                String jsonString = rs.getString("informacion_gopass");
                // Se convierte a Gson el resultado del pl
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                JsonArray dataArray = jsonObject.getAsJsonArray("data");
                Type listType = new TypeToken<LinkedList<GopassParametersDto>>() {
                }.getType();
                parametersGopassDto = gson.fromJson(dataArray, listType);
                for (GopassParametersDto item : parametersGopassDto) {
                    NovusUtils.printLn("Parametrización Gopass: "+item.toString());
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("Excepcion en la clase GopassParametersService. Metodo: execute -> " + e.getMessage());
        }
        return parametersGopassDto;
    }

}

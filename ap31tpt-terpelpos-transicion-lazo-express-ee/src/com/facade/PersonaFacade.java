/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.facade;

import com.application.useCases.persons.FinalizarArchivarUseCase;
import com.application.useCases.persons.SearchAllPersonsUseCase;
import com.bean.PersonaBean;
import com.dao.DAOException;
import com.dao.PersonasDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.TreeMap;

/**
 *
 * @author Admin
 */
public class PersonaFacade {
    static SearchAllPersonsUseCase searchAllPersonsUseCase = new SearchAllPersonsUseCase();

    public static TreeMap<Integer, PersonaBean> fetchAllPersons(boolean fetchInactives) throws DAOException {
        TreeMap<Integer, PersonaBean> allPersons = null;
        //PersonasDao pdao = new PersonasDao();
        //JsonArray allPersonsArray = pdao.searchAllPersons(fetchInactives);
        JsonArray allPersonsArray = searchAllPersonsUseCase.execute(fetchInactives);
        if (allPersonsArray == null) {
            return null;
        } else {
            allPersons = new TreeMap<>();
        }
        int index = 0;
        for (JsonElement element : allPersonsArray) {
            JsonObject personObject = element.getAsJsonObject();
            PersonaBean person = new PersonaBean();
            person.setId(personObject.get("id").getAsLong());
            person.setNombre(personObject.get("nombre").getAsString());
            person.setEstado(personObject.get("estado").getAsString());
            person.setIdentificacion(personObject.get("identificacion").getAsString());
            person.setPerfil(personObject.get("perfil") == null || personObject.get("perfil").isJsonNull() ? 8 : personObject.get("perfil").getAsInt());
            allPersons.put(index, person);
            index++;
        }
        return allPersons;
    }
}

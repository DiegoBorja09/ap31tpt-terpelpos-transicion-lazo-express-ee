/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.domain.valueObject;

/**
 *
 * @author USUARIO
 */
public class QueryCollectionGoPass {
    
    public static String UPDATE_CAR_PLATE_CT_MOV = "update ct_movimientos set atributos = jsonb_set(atributos::jsonb,'{vehiculo_placa}', to_jsonb(?::varchar)) where id = ?";
    public static final String FETCH_PARAMETERS_GOPASS = "select * from parametrizacion.fnc_parametros_gopass() as informacion_gopass";
}

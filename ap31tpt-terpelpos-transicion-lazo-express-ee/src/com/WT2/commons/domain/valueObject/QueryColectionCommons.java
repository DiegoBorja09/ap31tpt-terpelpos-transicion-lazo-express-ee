/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.domain.valueObject;

/**
 *
 * @author USUARIO
 */
public class QueryColectionCommons {

    public static final String RECUPERA_PARAMETROS = "select p.descripcion, tvp.* \n"
            + "from parametrizacion.parametros p \n"
            + "inner join parametrizacion.tbl_valor_parametros tvp on tvp.id_parametro  = p.id_parametro \n"
            + "where p.descripcion ilike ?";
}

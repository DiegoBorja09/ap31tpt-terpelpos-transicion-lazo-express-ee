/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.WT2.commons.domain.adapters;

/**
 *
 * @author USUARIO
 */
public interface IRepository <I,O>{
    
    public O getData(I params);
    public int updateData(I params);
}

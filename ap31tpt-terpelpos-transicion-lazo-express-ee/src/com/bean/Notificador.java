/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bean;

import com.google.gson.JsonObject;

/**
 *
 * @author desarrollador
 */
public interface Notificador { 
    abstract void send(JsonObject data);
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.utils;

/**
 *
 * @author USUARIO
 */
public class ShowMessageSingleton {
    
    private static ShowMessages instance;
    
    public static ShowMessages showMassegesInstance(){
        if(instance == null){
            instance = new ShowMessages();
        }
        return instance;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.Containers.Dependency;

import com.dao.SetupDao;

/**
 *
 * @author USUARIO
 */
public class SingletonMedioPago {
    
    static public DependencyInjection ConetextDependecy = new DependencyInjection(new SetupDao());
    
}

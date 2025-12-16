/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.dao.SetupDao;
import java.sql.SQLException;

/**
 *
 * @author USUARIO
 */
public class UpdateImagenReferenciaMedioPago {
    
    private final SetupDao setup;

    public UpdateImagenReferenciaMedioPago(SetupDao setup) {
        this.setup = setup;
    }
    
    public void execute() throws SQLException{
         setup.actualizarImagenReferenciaMedioPago();
   }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.appTerpel.domain.valueObject.PathFile;
import java.io.File;

/**
 *
 * @author USUARIO
 */
public class CreateImageFolder {
    
    
    public void execute(){
        File fileImage  = new File(PathFile.IMAGEFOLDER);
        
        if(!fileImage.exists()){
            fileImage.mkdir();
        }
     }
    
}

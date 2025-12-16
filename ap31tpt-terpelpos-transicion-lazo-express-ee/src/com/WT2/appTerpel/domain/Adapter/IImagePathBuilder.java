/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.WT2.appTerpel.domain.Adapter;

import com.WT2.appTerpel.domain.valueObject.ImagePath;

/**
 *
 * @author USUARIO
 */
public interface IImagePathBuilder {
        
    public void setFileName(String fileName);
    public ImagePath build();
    public void valuePath(String path); 
  
}

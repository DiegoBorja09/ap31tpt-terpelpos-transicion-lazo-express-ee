/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.appTerpel.domain.Adapter.IImagePathBuilder;
import com.WT2.commons.domain.valueObject.FileExtension;
import com.WT2.appTerpel.domain.valueObject.ImagePath;
import com.WT2.appTerpel.domain.valueObject.PathFile;
import java.io.File;

/**
 *
 * @author USUARIO
 */
public class ImagenPathCheckedBuilder implements IImagePathBuilder {

    private String fileName;
    private String fullPathName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public ImagePath build() {

        ImagePath imagaPath = new ImagePath();
        imagaPath.setSufix(PathFile.CHECKEDSUFIX);
        imagaPath.setFileName(this.fileName);
        valuePath(this.fileName);
        imagaPath.setFullNameString(this.fullPathName);
        return imagaPath;

    }

    public void valuePath(String path) {
        File fullPath = new File(PathFile.IMAGEFOLDER +path+PathFile.CHECKEDSUFIX+FileExtension.PNG);
        
        if( path != null && !path.equals("") && fullPath.exists() ){
            this.fullPathName = fullPath.getPath();
        } else {
            this.fullPathName = PathFile.DEFAULFILECHECKED;
        }

    }
}

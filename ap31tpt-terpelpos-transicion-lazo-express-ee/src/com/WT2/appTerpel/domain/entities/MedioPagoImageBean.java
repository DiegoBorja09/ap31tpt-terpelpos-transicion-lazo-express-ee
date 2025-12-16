
package com.WT2.appTerpel.domain.entities;

import com.WT2.appTerpel.domain.valueObject.ImagePath;
import com.bean.MediosPagosBean;

/**
 *
 * @author USUARIO
 */
public class MedioPagoImageBean extends MediosPagosBean {

    private String imageId;
    private ImagePath imagePathUnchecked;
    private ImagePath imagePathChecked;
    private MedioPagoAtributos attributos;

    public void setAtributo(MedioPagoAtributos attributos) {
        this.attributos = attributos;
    }

    public MedioPagoAtributos getAtributo() {
        return attributos;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImagePathUnchecked() {
        return imagePathUnchecked.getFullNameString();
    }

    public String getImagePathChecked() {
        return imagePathChecked.getFullNameString();
    }

    public void setImagePathUnchecked(ImagePath imagePathUnchecked) {
        this.imagePathUnchecked = imagePathUnchecked;
    }

    public void setImagePathChecked(ImagePath imagePathChecked) {
        this.imagePathChecked = imagePathChecked;
    }
}

package com.WT2.commons.domain.entity;

import javax.swing.*;

public class ParametrosMensajes {

    private String msj;
    private String ruta;
    private boolean habilitar;
    private Runnable runnable;
    private boolean autoclose;
    private String letterCase;
    private ImageIcon imageIcon;

    public String getMsj() {
        return msj;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setMsj(String msj) {
        this.msj = msj;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isHabilitar() {
        return habilitar;
    }

    public void setHabilitar(boolean habilitar) {
        this.habilitar = habilitar;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean isAutoclose() {
        return autoclose;
    }

    public void setAutoclose(boolean autoclose) {
        this.autoclose = autoclose;
    }

    public String getLetterCase() {
        return letterCase;
    }

    public void setLetterCase(String letterCase) {
        this.letterCase = letterCase;
    }

    

}

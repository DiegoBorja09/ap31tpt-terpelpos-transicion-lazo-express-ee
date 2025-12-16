package com.WT2.commons.infraestructure.builders;

import com.WT2.commons.domain.adapters.IParametrosMensajesBuilder;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;

import javax.swing.*;

public class ParametrosMensajesBuilder implements IParametrosMensajesBuilder {

    private String msj;
    private String ruta;
    private boolean habilitar;
    private Runnable runnable;
    private boolean autoclose;
    private String letterCase;
    private ImageIcon imageIcon;

    @Override
    public IParametrosMensajesBuilder setMsj(String msj) {
        this.msj = msj;
        return this;
    }



    @Override
    public ParametrosMensajesBuilder setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        return this;
    }



    @Override
    public IParametrosMensajesBuilder setRuta(String ruta) {
        this.ruta = ruta;
        return this;
    }

    @Override
    public IParametrosMensajesBuilder setHabilitar(Boolean habilitar) {
        this.habilitar = habilitar;
        return this;
    }

    @Override
    public IParametrosMensajesBuilder setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    @Override
    public IParametrosMensajesBuilder setAutoclose(Boolean autoclose) {
        this.autoclose = autoclose;
        return this;
    }

    @Override
    public IParametrosMensajesBuilder setLetterCase(String letterCase) {
        this.letterCase = letterCase;
        return this;
    }



    @Override
    public ParametrosMensajes build() {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajes();
        parametrosMensajes.setMsj(msj);
        parametrosMensajes.setRuta(ruta);
        parametrosMensajes.setHabilitar(habilitar);
        parametrosMensajes.setRunnable(runnable);
        parametrosMensajes.setAutoclose(autoclose);
        parametrosMensajes.setLetterCase(letterCase == null ?  LetterCase.FIRST_UPPER_CASE: letterCase );
        parametrosMensajes.setImageIcon(imageIcon);
        return parametrosMensajes;
    }

}

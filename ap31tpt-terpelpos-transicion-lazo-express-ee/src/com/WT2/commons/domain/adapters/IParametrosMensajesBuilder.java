package com.WT2.commons.domain.adapters;

import com.WT2.commons.domain.entity.ParametrosMensajes;

import javax.swing.*;

public interface IParametrosMensajesBuilder extends IBuilderParameters<ParametrosMensajes>{
    
    
    public IParametrosMensajesBuilder setMsj(String msj);

    public IParametrosMensajesBuilder setRuta(String ruta);

    public IParametrosMensajesBuilder setHabilitar(Boolean habilitar);

    public IParametrosMensajesBuilder setRunnable(Runnable runnable);

    public IParametrosMensajesBuilder setAutoclose(Boolean autoclose);
    
    public IParametrosMensajesBuilder setLetterCase(String letterCase);
    public IParametrosMensajesBuilder setImageIcon(ImageIcon imageIcon);
}

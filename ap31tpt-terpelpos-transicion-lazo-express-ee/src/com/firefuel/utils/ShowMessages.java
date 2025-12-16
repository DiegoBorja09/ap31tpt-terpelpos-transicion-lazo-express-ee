package com.firefuel.utils;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.PanelNotificacion;

public class ShowMessages {

    public PanelNotificacion execute(ParametrosMensajes parametrosMensajes) {
        PanelNotificacion notificacion = PanelNotificacion.getInstance();
        notificacion.update(parametrosMensajes.getMsj(),
                parametrosMensajes.getRuta(),
                parametrosMensajes.isHabilitar(),
                parametrosMensajes.getRunnable(),
                parametrosMensajes.getLetterCase());
        if (parametrosMensajes.isAutoclose()) {
            notificacion.getTimer().start();
        }
        return notificacion;
    }

    public PanelNotificacion executeAppterpel(ParametrosMensajes parametrosMensajes) {

        PanelNotificacion notificacion = PanelNotificacion.getInstanceAppterpel();
        notificacion.setTimerDalay(NovusConstante.TIEMPO_MENSAJE_APPTERPEL * 1000);
        notificacion.setTimeout(0);
        NovusUtils.printLn("El tiempo del mensaje Appterpel esta de "+NovusConstante.TIEMPO_MENSAJE_APPTERPEL );
        notificacion.update(parametrosMensajes.getMsj(),
                parametrosMensajes.getRuta(),
                parametrosMensajes.isHabilitar(),
                parametrosMensajes.getRunnable(),
                parametrosMensajes.getLetterCase());
        if (parametrosMensajes.isAutoclose()) {
            notificacion.getTimer().start();
        }
        return notificacion;

    }

}

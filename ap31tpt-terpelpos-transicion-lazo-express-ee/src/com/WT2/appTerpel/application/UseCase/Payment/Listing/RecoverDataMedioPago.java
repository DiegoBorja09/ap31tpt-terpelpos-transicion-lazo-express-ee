package com.WT2.appTerpel.application.UseCase.Payment.Listing;

import com.WT2.appTerpel.application.UseCase.ImagenPathCheckedBuilder;
import com.WT2.appTerpel.application.UseCase.ImagenPathUncheckedBuilder;
import com.WT2.appTerpel.application.UseCase.ValidateAttributos;
import com.WT2.appTerpel.domain.context.Concurrents;
import com.WT2.appTerpel.domain.entities.MedioPagoAtributos;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.WT2.commons.domain.adapters.IUseCase;
import com.dao.SetupDao;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecoverDataMedioPago implements IUseCase<Void, Void> {

    private final SetupDao setup;
    private final ImagenPathUncheckedBuilder builderImageUnCheck = new ImagenPathUncheckedBuilder();
    private final ImagenPathCheckedBuilder builderImageCheck = new ImagenPathCheckedBuilder();

    public RecoverDataMedioPago(SetupDao setupDao) {
        this.setup = setupDao;
    }

    public void loadMedioPago() throws SQLException {

        ResultSet re = setup.getMediosPagosWithImages(true);

        Concurrents.medioPagoImageList = new CopyOnWriteArrayList<>();

        while (re.next()) {
            ValidateAttributos validateAttributos = new ValidateAttributos();
            MedioPagoImageBean medio = new MedioPagoImageBean();
            medio.setId(re.getLong("id"));
            medio.setCredito(false);
            medio.setDescripcion(re.getString("descripcion"));
            medio.setComprobante(true);
            medio.setMaximo_valor(9999999);
            medio.setCambio(false);
            medio.setMinimo_valor(0);
            MedioPagoAtributos atribuM = Main.gson.fromJson(re.getString("atributos"), MedioPagoAtributos.class);
            medio.setAtributo(atribuM);
            builderImageCheck.setFileName(re.getString("id_medio_pago_recurso_seleccionado"));
            JsonObject atribuOld = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
            medio.setAtributos(atribuOld);
            builderImageUnCheck.setFileName(re.getString("id_medio_pago_recurso"));
            medio.setImagePathChecked(builderImageCheck.build());
            medio.setImagePathUnchecked(builderImageUnCheck.build());
            validateAttributos.execute(medio);
            Concurrents.medioPagoImage.put(medio.getId(), medio);
            Concurrents.medioPagoImageList.add(medio);

        }

    }

    @Override
    public Void execute(Void input) {

        try {
            loadMedioPago();
        } catch (SQLException ex) {
            Logger.getLogger(RecoverDataMedioPago.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

}

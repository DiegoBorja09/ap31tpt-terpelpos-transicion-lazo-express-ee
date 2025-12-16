package com.WT2.loyalty.domain.adapters;

import com.WT2.commons.domain.adapters.IBuilderParameters;
import com.bean.ReciboExtended;
import com.firefuel.FidelizacionyFacturacionElectronica;
import com.firefuel.asignarCliente.beans.InformacionVentaCliente;

public interface IFidelizacionyFacturacionBuilder extends IBuilderParameters<FidelizacionyFacturacionElectronica> {

    public IFidelizacionyFacturacionBuilder setRecibo(ReciboExtended recibo);

    public IFidelizacionyFacturacionBuilder setRegresar(Runnable regresar);

    public IFidelizacionyFacturacionBuilder setEnviar(Runnable enviar);

    public IFidelizacionyFacturacionBuilder setAsignarDatos(Boolean asignarDatos);

    public IFidelizacionyFacturacionBuilder setFidelizacion(Boolean fidelizacion);

    public IFidelizacionyFacturacionBuilder setVentaMarket(Boolean ventaMarket);

    public IFidelizacionyFacturacionBuilder setInformacionVentaCliente(InformacionVentaCliente informacionVentaCliente);

    public IFidelizacionyFacturacionBuilder setNumeroDocumento(String numeroDocumento);

    public IFidelizacionyFacturacionBuilder setTipoDocumento(String tipoDocumento);

}

package com.WT2.loyalty.infrastructure.builders;

import com.WT2.loyalty.domain.adapters.IFidelizacionyFacturacionBuilder;
import com.bean.ReciboExtended;
import com.firefuel.FidelizacionyFacturacionElectronica;
import com.firefuel.asignarCliente.beans.InformacionVentaCliente;

public class FidelizacionyFacturacionBuilder implements IFidelizacionyFacturacionBuilder {

    private ReciboExtended recibo;
    private String numeroDocumento;
    private String tipoDocumento;
    private boolean fidelizacion;
    private Runnable regresar;
    private Runnable enviar;
    private boolean asignarDatos;
    private InformacionVentaCliente informacionVentaCliente;
    private boolean ventaMarket;

    @Override
    public IFidelizacionyFacturacionBuilder setRecibo(ReciboExtended recibo) {
        this.recibo = recibo;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setRegresar(Runnable regresar) {
        this.regresar = regresar;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setEnviar(Runnable enviar) {
        this.enviar = enviar;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setAsignarDatos(Boolean asignarDatos) {
        this.asignarDatos = asignarDatos;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setFidelizacion(Boolean fidelizacion) {
        this.fidelizacion = fidelizacion;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setVentaMarket(Boolean ventaMarket) {
        this.ventaMarket = ventaMarket;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setInformacionVentaCliente(InformacionVentaCliente informacionVentaCliente) {
        this.informacionVentaCliente = informacionVentaCliente;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
        return this;
    }

    @Override
    public IFidelizacionyFacturacionBuilder setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
        return this;
    }

    @Override
    public FidelizacionyFacturacionElectronica build() {
        FidelizacionyFacturacionElectronica fidelizacionyFacturacionElectronica = new FidelizacionyFacturacionElectronica();
        fidelizacionyFacturacionElectronica.setRecibo(recibo);
        fidelizacionyFacturacionElectronica.setEnviar(enviar);
        fidelizacionyFacturacionElectronica.setRegresar(regresar);
        fidelizacionyFacturacionElectronica.setAsignarDatos(asignarDatos);
        fidelizacionyFacturacionElectronica.setFidelizacion(fidelizacion);
        fidelizacionyFacturacionElectronica.setVentaMarket(ventaMarket);
        fidelizacionyFacturacionElectronica.setInformacionVentaCliente(informacionVentaCliente);
        fidelizacionyFacturacionElectronica.setNumeroDocumento(numeroDocumento);
        fidelizacionyFacturacionElectronica.setTipoDocumento(tipoDocumento);
        fidelizacionyFacturacionElectronica.iniciarProceso();
        return fidelizacionyFacturacionElectronica;
    }

}

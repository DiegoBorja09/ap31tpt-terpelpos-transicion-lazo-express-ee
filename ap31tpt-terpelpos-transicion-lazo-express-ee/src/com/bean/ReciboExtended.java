/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

/**
 *
 * @author novusteam
 */
public class ReciboExtended extends com.neo.app.bean.Recibo {

    long id;
    boolean ventaCurso;
    String identificacionPromotor;
    long identificacionProducto;
    boolean credito;
    boolean editar;
    boolean clienteSinAsignar;
    boolean datatafonosPendientes;
    boolean adBluePendiente;
    private long idTransaccionDatafono;
    public int familiaId;
    private String prefijo;
    public long idMovimiento;

    public long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
        public String getMangueraRobusta() {
        if (this.getManguera() != null && !this.getManguera().trim().isEmpty()) {
            return this.getManguera();
        }

        if (this.getAtributos() != null
                && this.getAtributos().has("manguera")
                && !this.getAtributos().get("manguera").isJsonNull()) {
            return this.getAtributos().get("manguera").getAsString();
        }

        return "0";
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public boolean isCredito() {
        return credito;
    }

    public void setCredito(boolean credito) {
        this.credito = credito;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public String getIdentificacionPromotor() {
        return identificacionPromotor;
    }

    public void setIdentificacionPromotor(String identificacionPromotor) {
        this.identificacionPromotor = identificacionPromotor;
    }

    public long getIdentificacionProducto() {
        return identificacionProducto;
    }

    public void setIdentificacionProducto(long identificacionProducto) {
        this.identificacionProducto = identificacionProducto;
    }

    public void setIsVentaCurso(boolean ventaCurso) {
        this.ventaCurso = ventaCurso;
    }

    public boolean isVentaCurso() {
        return ventaCurso;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isClienteSinAsignar() {
        return clienteSinAsignar;
    }

    public void setClienteSinAsignar(boolean clienteSinAsignar) {
        this.clienteSinAsignar = clienteSinAsignar;
    }

    public boolean isDatatafonosPendientes() {
        return datatafonosPendientes;
    }

    public void setDatatafonosPendientes(boolean datatafonosPendientes) {
        this.datatafonosPendientes = datatafonosPendientes;
    }

    public long getIdTransaccionDatafono() {
        return idTransaccionDatafono;
    }

    public void setIdTransaccionDatafono(long idTransaccionDatafono) {
        this.idTransaccionDatafono = idTransaccionDatafono;
    }

    public boolean isAdBluePendiente() {
        return adBluePendiente;
    }

    public void setAdBluePendiente(boolean adBluePendiente) {
        this.adBluePendiente = adBluePendiente;
    }

    public int getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(int familiaId) {
        this.familiaId = familiaId;
    }

    @Override
    public String toString() {
        return "ReciboExtended{" + "id=" + id + ", ventaCurso=" + ventaCurso + ", identificacionPromotor=" + identificacionPromotor + ", identificacionProducto=" + identificacionProducto + ", credito=" + credito + ", editar=" + editar + '}';
    }

}

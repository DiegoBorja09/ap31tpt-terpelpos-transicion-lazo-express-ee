/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 *
 * @author novus
 */
public class MovimientosBean implements Comparable<MovimientosBean> {

    private long id;
    private EmpresaBean empresa;
    private long empresasId;
    private long bodegaId;
    private int operacionId;
    private String operacionDesc;
    private Date fecha;
    private long personaId;
    private String personaNit;
    private String personaNombre;
    private String personaApellidos;
    private CategoriaBean categoriasMovimiento;
    private String unidad_medida;
    private String descuentoCortesia;
    private double totalImpuestosSinImpocunsumos;
    private JsonObject datosFidelizacion;
    private String ventaFidelizada = "N";
    private boolean fidelizar;

    public void setCategoriasMovimiento(CategoriaBean categoriasMovimiento) {
        this.categoriasMovimiento = categoriasMovimiento;
    }
    private long clienteId;
    private String clienteNit;
    private String clienteNombre;
    private long cantidadTotalProductos;

    public long getCantidadTotalProductos() {
        return cantidadTotalProductos;
    }

    public void setCantidadTotalProductos(long cantidadTotalProductos) {
        this.cantidadTotalProductos = cantidadTotalProductos;
    }
    private float costoTotal;
    private float ventaTotal;
    private float subTotal;
    private float impuestoTotal;
    private float impuestoU;
    private float recibidoTotal;
    private float impoConsumoTotal;
    private float descuentoTotal;
    private long origenId;
    private String impreso;
    private long createUser;
    private Date createDate;
    private long updateUser;
    private Date updateDate;
    private long remotoId;
    private int sincronizado;
    private ConsecutivoBean consecutivo;
    private ConsecutivoBean consecutivo_fe;

    public ConsecutivoBean getConsecutivo_fe() {
        return consecutivo_fe;
    }

    public void setConsecutivo_fe(ConsecutivoBean consecutivo_fe) {
        this.consecutivo_fe = consecutivo_fe;
    }
    private boolean success;
    private long grupoJornadaId;
    private BodegaBean bodega;
    private int estado;
    private String movmientoEstado;
    private JsonObject atributos;
    private long precioEspecialId;

    public long getPrecioEspecialId() {
        return precioEspecialId;
    }

    public void setPrecioEspecialId(long precioEspecialId) {
        this.precioEspecialId = precioEspecialId;
    }

    private LinkedHashMap<Long, MovimientosDetallesBean> detalles;
    public TreeMap<Long, MediosPagosBean> mediosPagos;

    public BodegaBean getBodega() {
        return bodega;
    }

    public float getRecibidoTotal() {
        return recibidoTotal;
    }

    public void setRecibidoTotal(float recibidoTotal) {
        this.recibidoTotal = recibidoTotal;
    }

    public void setBodega(BodegaBean bodega) {
        this.bodega = bodega;
    }

    public MovimientosBean() {
        detalles = new LinkedHashMap<>();
    }

    public long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(long empresasId) {
        this.empresasId = empresasId;
    }

    public int getOperacionId() {
        return operacionId;
    }

    public void setOperacionId(int operacionId) {
        this.operacionId = operacionId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ConsecutivoBean getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(ConsecutivoBean consecutivo) {
        this.consecutivo = consecutivo;
    }

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public String getPersonaNit() {
        return personaNit;
    }

    public void setPersonaNit(String personaNit) {
        this.personaNit = personaNit;
    }

    public String getPersonaNombre() {
        return personaNombre;
    }

    public void setPersonaNombre(String personaNombre) {
        this.personaNombre = personaNombre;
    }

    public float getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(float costoTotal) {
        this.costoTotal = costoTotal;
    }

    public float getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(float ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public float getImpuestoTotal() {
        return impuestoTotal;
    }

    public void setImpuestoTotal(float impuestoTotal) {
        this.impuestoTotal = impuestoTotal;
    }

    public float getImpuestoU() {
        return impuestoU;
    }

    public void setImpuestoU(float impuestoU) {
        this.impuestoU = impuestoU;
    }

    public float getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(float descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public String getDescuentoCortesia() {
        return descuentoCortesia;
    }

    public void setDescuentoCortesia(String descuentoCortesia) {
        this.descuentoCortesia = descuentoCortesia;
    }
    
    public long getOrigenId() {
        return origenId;
    }

    public void setOrigenId(long origenId) {
        this.origenId = origenId;
    }

    public String getImpreso() {
        return impreso;
    }

    public void setImpreso(String impreso) {
        this.impreso = impreso;
    }

    public long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(long createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(long updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public long getRemotoId() {
        return remotoId;
    }

    public void setRemotoId(long remotoId) {
        this.remotoId = remotoId;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public LinkedHashMap<Long, MovimientosDetallesBean> getDetalles() {
        return detalles;
    }

    public void setDetalles(LinkedHashMap<Long, MovimientosDetallesBean> detalles) {
        this.detalles = detalles;
    }

    public long getBodegaId() {
        return bodegaId;
    }

    public void setBodegaId(long bodegaId) {
        this.bodegaId = bodegaId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMovmientoEstado() {
        return movmientoEstado;
    }

    public void setMovmientoEstado(String movmientoEstado) {
        this.movmientoEstado = movmientoEstado;
    }

    public long getGrupoJornadaId() {
        return grupoJornadaId;
    }

    public void setGrupoJornadaId(long grupoJornadaId) {
        this.grupoJornadaId = grupoJornadaId;
    }

    public float getImpoConsumoTotal() {
        return impoConsumoTotal;
    }

    public void setImpoConsumoTotal(float impoConsumoTotal) {
        this.impoConsumoTotal = impoConsumoTotal;
    }

    public EmpresaBean getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaBean empresa) {
        this.empresa = empresa;
    }

    public long getClienteId() {
        return clienteId;
    }

    public void setClienteId(long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNit() {
        return clienteNit;
    }

    public void setClienteNit(String clienteNit) {
        this.clienteNit = clienteNit;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getPersonaApellidos() {
        return personaApellidos;
    }

    public void setPersonaApellidos(String personaApellidos) {
        this.personaApellidos = personaApellidos;
    }

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
    }

    public String getOperacionDesc() {
        return operacionDesc;
    }

    public void setOperacionDesc(String operacionDesc) {
        this.operacionDesc = operacionDesc;
    }

    public TreeMap<Long, MediosPagosBean> getMediosPagos() {
        return mediosPagos;
    }

    public void setMediosPagos(TreeMap<Long, MediosPagosBean> mediosPagos) {
        this.mediosPagos = mediosPagos;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }

    public String getUnidad_medida() {
        return unidad_medida;
    }

    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }

    public CategoriaBean getCategoriasMovimiento() {
        return categoriasMovimiento;
    }

    public double getTotalImpuestosSinImpocunsumos() {
        return totalImpuestosSinImpocunsumos;
    }

    public void setTotalImpuestosSinImpocunsumos(double totalImpuestosSinImpocunsumos) {
        this.totalImpuestosSinImpocunsumos = totalImpuestosSinImpocunsumos;
    }

    public JsonObject getDatosFidelizacion() {
        return datosFidelizacion;
    }

    public void setDatosFidelizacion(JsonObject datosFidelizacion) {
        this.datosFidelizacion = datosFidelizacion;
    }

    public String getVentaFidelizada() {
        return ventaFidelizada;
    }

    public void setVentaFidelizada(String ventaFidelizada) {
        this.ventaFidelizada = ventaFidelizada;
    }

     public boolean isFidelizar() {
        return fidelizar;
    }

    public void setFidelizar(boolean fidelizar) {
        this.fidelizar = fidelizar;
    }
    
    
    
    @Override
    public int compareTo(MovimientosBean o) {
        if (this.id < o.getId()) {
            return -1;
        } else if (id == o.getId()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "MovimientosBean{" + "id=" + id + ", empresa=" + empresa + ", empresasId=" + empresasId + ", bodegaId=" + bodegaId + ", operacionId=" + operacionId + ", operacionDesc=" + operacionDesc + ", fecha=" + fecha + ", personaId=" + personaId + ", personaNit=" + personaNit + ", personaNombre=" + personaNombre + ", personaApellidos=" + personaApellidos + ", categoriasMovimiento=" + categoriasMovimiento + ", unidad_medida=" + unidad_medida + ", clienteId=" + clienteId + ", clienteNit=" + clienteNit + ", clienteNombre=" + clienteNombre + ", cantidadTotalProductos=" + cantidadTotalProductos + ", costoTotal=" + costoTotal + ", ventaTotal=" + ventaTotal + ", subTotal=" + subTotal + ", impuestoTotal=" + impuestoTotal + ", recibidoTotal=" + recibidoTotal + ", impoConsumoTotal=" + impoConsumoTotal + ", descuentoTotal=" + descuentoTotal + ", origenId=" + origenId + ", impreso=" + impreso + ", createUser=" + createUser + ", createDate=" + createDate + ", updateUser=" + updateUser + ", updateDate=" + updateDate + ", remotoId=" + remotoId + ", sincronizado=" + sincronizado + ", consecutivo=" + consecutivo + ", success=" + success + ", grupoJornadaId=" + grupoJornadaId + ", bodega=" + bodega + ", estado=" + estado + ", movmientoEstado=" + movmientoEstado + ", atributos=" + atributos + ", detalles=" + detalles + ", mediosPagos=" + mediosPagos + '}';
    }

    
}

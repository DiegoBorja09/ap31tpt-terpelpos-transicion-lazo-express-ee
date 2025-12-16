package com.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ct_movimientos_detalles")
public class CtMovimientoDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "atributos")
    private String atributos;

    @Column(name = "movimientos_id", nullable = false)
    private Long movimientosId;

    @Column(name = "bodegas_id")
    private Long bodegasId;

    @Column(name = "cantidad", nullable = false)
    private BigDecimal cantidad;

    @Column(name = "costo_producto", nullable = false)
    private BigDecimal costoProducto;

    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Column(name = "descuentos_id")
    private Long descuentosId;

    @Column(name = "descuento_calculado")
    private BigDecimal descuentoCalculado;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "dia", nullable = false)
    private Integer dia;

    @Column(name = "remoto_id")
    private Long remotoId;

    @Column(name = "sub_total", nullable = false)
    private BigDecimal subTotal;

    @Column(name = "sub_movimientos_detalles_id")
    private Long subMovimientosDetallesId;

    @Column(name = "unidades_id", nullable = false)
    private Long unidadesId;

    @Column(name = "productos_id")
    private Long productosId;

    @Column(name = "sincronizado", length = 1)
    private String sincronizado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    public Long getMovimientosId() {
        return movimientosId;
    }

    public void setMovimientosId(Long movimientosId) {
        this.movimientosId = movimientosId;
    }

    public Long getBodegasId() {
        return bodegasId;
    }

    public void setBodegasId(Long bodegasId) {
        this.bodegasId = bodegasId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoProducto() {
        return costoProducto;
    }

    public void setCostoProducto(BigDecimal costoProducto) {
        this.costoProducto = costoProducto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Long getDescuentosId() {
        return descuentosId;
    }

    public void setDescuentosId(Long descuentosId) {
        this.descuentosId = descuentosId;
    }

    public BigDecimal getDescuentoCalculado() {
        return descuentoCalculado;
    }

    public void setDescuentoCalculado(BigDecimal descuentoCalculado) {
        this.descuentoCalculado = descuentoCalculado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Long getRemotoId() {
        return remotoId;
    }

    public void setRemotoId(Long remotoId) {
        this.remotoId = remotoId;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Long getSubMovimientosDetallesId() {
        return subMovimientosDetallesId;
    }

    public void setSubMovimientosDetallesId(Long subMovimientosDetallesId) {
        this.subMovimientosDetallesId = subMovimientosDetallesId;
    }

    public Long getUnidadesId() {
        return unidadesId;
    }

    public void setUnidadesId(Long unidadesId) {
        this.unidadesId = unidadesId;
    }

    public Long getProductosId() {
        return productosId;
    }

    public void setProductosId(Long productosId) {
        this.productosId = productosId;
    }

    public String getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(String sincronizado) {
        this.sincronizado = sincronizado;
    }
}

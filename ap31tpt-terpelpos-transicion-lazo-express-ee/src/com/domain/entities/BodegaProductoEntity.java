package com.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bodegas_productos", schema = "public")
public class BodegaProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "productos_id")
    private Long productosId;

    @Column(name = "bodegas_id")
    private Long bodegasId;

    @Column(name = "saldo", precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(name = "cantidad_minima")
    private Integer cantidadMinima;

    @Column(name = "cantidad_maxima")
    private Integer cantidadMaxima;

    @Column(name = "empresas_id")
    private Long empresasId;

    @Column(name = "tiempo_reorden")
    private Integer tiempoReorden;

    @Column(name = "costo")
    private Double costo;

    @Column(name = "unidades_id")
    private Long unidadesId;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductosId() {
        return productosId;
    }

    public void setProductosId(Long productosId) {
        this.productosId = productosId;
    }

    public Long getBodegasId() {
        return bodegasId;
    }

    public void setBodegasId(Long bodegasId) {
        this.bodegasId = bodegasId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(Integer cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public Integer getCantidadMaxima() {
        return cantidadMaxima;
    }

    public void setCantidadMaxima(Integer cantidadMaxima) {
        this.cantidadMaxima = cantidadMaxima;
    }

    public Long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(Long empresasId) {
        this.empresasId = empresasId;
    }

    public Integer getTiempoReorden() {
        return tiempoReorden;
    }

    public void setTiempoReorden(Integer tiempoReorden) {
        this.tiempoReorden = tiempoReorden;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public Long getUnidadesId() {
        return unidadesId;
    }

    public void setUnidadesId(Long unidadesId) {
        this.unidadesId = unidadesId;
    }
} 
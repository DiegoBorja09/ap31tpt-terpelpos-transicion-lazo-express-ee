package com.application.useCases.tanques;

import com.application.useCases.productos.ProductoTanqueDto;
import java.util.List;

public class TanqueDto {
    private Integer id;
    private String descripcion;
    private String codigo;
    private Integer numeroTanque;
    private Integer volumenMaximo;
    private Long familiaId;
    private List<ProductoTanqueDto> productos;

    public TanqueDto() {
    }

    public TanqueDto(Integer id, String descripcion, String codigo ,Integer numeroTanque,
                     Integer volumenMaximo, Long familiaId) {
        this.id = id;
        this.descripcion = descripcion;
        this.numeroTanque = numeroTanque;
        this.volumenMaximo = volumenMaximo;
        this.familiaId = familiaId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getNumeroTanque() {
        return numeroTanque;
    }

    public void setNumeroTanque(Integer numeroTanque) {
        this.numeroTanque = numeroTanque;
    }

    public Integer getVolumenMaximo() {
        return volumenMaximo;
    }

    public void setVolumenMaximo(Integer volumenMaximo) {
        this.volumenMaximo = volumenMaximo;
    }

    public Long getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(Long familiaId) {
        this.familiaId = familiaId;
    }

    public List<ProductoTanqueDto> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoTanqueDto> productos) {
        this.productos = productos;
    }
} 
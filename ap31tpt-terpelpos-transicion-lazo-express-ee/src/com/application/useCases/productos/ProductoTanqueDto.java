package com.application.useCases.productos;

public class ProductoTanqueDto {
    private Integer id;
    private String descripcion;
    private Integer familiaId;
    private Float precio;
    private Integer saldo;
    private Long unidades_medida_id;
    private String unidades_medida;

    public ProductoTanqueDto() {
    }

    public ProductoTanqueDto(Integer id, String descripcion, Integer familiaId, Float precio, 
                           Integer saldo, Long unidades_medida_id, String unidades_medida) {
        this.id = id;
        this.descripcion = descripcion;
        this.familiaId = familiaId;
        this.precio = precio;
        this.saldo = saldo;
        this.unidades_medida_id = unidades_medida_id;
        this.unidades_medida = unidades_medida;
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

    public Integer getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(Integer familiaId) {
        this.familiaId = familiaId;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public Long getUnidades_medida_id() {
        return unidades_medida_id;
    }

    public void setUnidades_medida_id(Long unidades_medida_id) {
        this.unidades_medida_id = unidades_medida_id;
    }

    public String getUnidades_medida() {
        return unidades_medida;
    }

    public void setUnidades_medida(String unidades_medida) {
        this.unidades_medida = unidades_medida;
    }
} 
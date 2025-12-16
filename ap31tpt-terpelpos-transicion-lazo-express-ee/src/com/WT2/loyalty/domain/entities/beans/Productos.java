
package com.WT2.loyalty.domain.entities.beans;


public class Productos {
    
    private String identificacionProducto;
    private double cantidadProducto;
    private long valorUnitarioProducto;
    private String marca;
    private String categoria;
    private String subcategoria;

    public String getIdentificacionProducto() {
        return identificacionProducto;
    }

    public void setIdentificacionProducto(String identificacionProducto) {
        this.identificacionProducto = identificacionProducto;
    }

    public double getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(double cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public long getValorUnitarioProducto() {
        return valorUnitarioProducto;
    }

    public void setValorUnitarioProducto(long valorUnitarioProducto) {
        this.valorUnitarioProducto = valorUnitarioProducto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }
       
    
}

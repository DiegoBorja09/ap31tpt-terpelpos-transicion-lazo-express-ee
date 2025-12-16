/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.domain.entity.dto;

/**
 *
 * @author Jeison
 */
public class GopassParametersDto {
    
    private String valor;
    private String codigo;
    private String valorMinimo;
    private String valorMaximo;
    
    public GopassParametersDto(){
        
    }

    public GopassParametersDto(String valor, String codigo, String valorMinimo, String valorMaximo) {
        this.valor = valor;
        this.codigo = codigo;
        this.valorMinimo = valorMinimo;
        this.valorMaximo = valorMaximo;
    }

    public String getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(String valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(String valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GopassParametersDto{");
        sb.append("valor=").append(valor);
        sb.append(", codigo=").append(codigo);
        sb.append(", valorMinimo=").append(valorMinimo);
        sb.append(", valorMaximo=").append(valorMaximo);
        sb.append('}');
        return sb.toString();
    }
    
    
    
    
         
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.Date;

/**
 *
 * @author ASUS-PC
 */
public class Recibo {

    public long numero;
    public String empresa;
    public String nit;
    public String direccion;
    public String telefono;
    public float cantidad;
    public float precio;
    public float total;
    public String producto;
    public String placa;
    public String surtidor;
    public String cara;
    public String manguera;
    public String isla;
    public String copia;
    public Date fecha;
    public String operador;
    public MovimientosBean movimiento;
    public DescriptorBean descriptores;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCopia() {
        return copia;
    }

    public void setCopia(String copia) {
        this.copia = copia;
    }

    public String getSurtidor() {
        return surtidor;
    }

    public void setSurtidor(String surtidor) {
        this.surtidor = surtidor;
    }

    public String getCara() {
        return cara;
    }

    public void setCara(String cara) {
        this.cara = cara;
    }

    public String getManguera() {
        return manguera;
    }

    public void setManguera(String manguera) {
        this.manguera = manguera;
    }

    public String getIsla() {
        return isla;
    }

    public void setIsla(String isla) {
        this.isla = isla;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public MovimientosBean getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientosBean movimiento) {
        this.movimiento = movimiento;
    }

    public DescriptorBean getDescriptores() {
        return descriptores;
    }

    public void setDescriptores(DescriptorBean descriptores) {
        this.descriptores = descriptores;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }
        
    @Override
    public String toString() {
        return "Recibo{" + "numero=" + numero + ", empresa=" + empresa + ", nit=" + nit + ", direccion=" + direccion + ", telefono=" + telefono + ", cantidad=" + cantidad + ", precio=" + precio + ", total=" + total + ", producto=" + producto + ", placa=" + placa + ", surtidor=" + surtidor + ", cara=" + cara + ", manguera=" + manguera + ", isla=" + isla + ", copia=" + copia + ", fecha=" + fecha + ", operador=" + operador + ", movimiento=" + movimiento + ", descriptores=" + descriptores + '}';
    }       
}

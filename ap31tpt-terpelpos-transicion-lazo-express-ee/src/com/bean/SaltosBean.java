/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

import java.util.Date;

/**
 *
 * @author USUARIO
 */
public class SaltosBean {

    private int id;
    private int motivo;//Motivo Bloqueo
    private String persona;//Persona Responsable
    private String descripcion;//Producto Combustible

    private int surtidor;
    private int cara;
    private int manguera;

    private long sistema_acu_v;//Sistema Volumen Acumulado
    private long surtidor_acu_v;//Surtidor Volumen Acumulado

    private Date fecha;//Fecha Correccion

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMotivo() {
        return motivo;
    }

    public void setMotivo(int motivo) {
        this.motivo = motivo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getSurtidor() {
        return surtidor;
    }

    public void setSurtidor(int surtidor) {
        this.surtidor = surtidor;
    }

    public int getCara() {
        return cara;
    }

    public void setCara(int cara) {
        this.cara = cara;
    }

    public int getManguera() {
        return manguera;
    }

    public void setManguera(int manguera) {
        this.manguera = manguera;
    }

    public long getSistema_acu_v() {
        return sistema_acu_v;
    }

    public void setSistema_acu_v(long sistema_acu_v) {
        this.sistema_acu_v = sistema_acu_v;
    }

    public long getSurtidor_acu_v() {
        return surtidor_acu_v;
    }

    public void setSurtidor_acu_v(long surtidor_acu_v) {
        this.surtidor_acu_v = surtidor_acu_v;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

}

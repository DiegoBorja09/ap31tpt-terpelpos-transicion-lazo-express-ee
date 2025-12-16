/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author desarrollador
 */
public class TurnosBean {

    int estado;
    String fecha;
    int id;
    String turno;
    int equiposId;

    int islas;
    int horariosId;
    String horaInicio;

    String fechaHoraFin;
    JsonObject atributos;
    JsonArray promotores;
    JsonObject consolidados;

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquiposId() {
        return equiposId;
    }

    public void setEquiposId(int equiposId) {
        this.equiposId = equiposId;
    }

    public int getIslas() {
        return islas;
    }

    public void setIslas(int islas) {
        this.islas = islas;
    }

    public int getHorariosId() {
        return horariosId;
    }

    public void setHorariosId(int horariosId) {
        this.horariosId = horariosId;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(String fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

}

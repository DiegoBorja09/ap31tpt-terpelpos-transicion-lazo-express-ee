/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author novus
 */
public class PersonaBean {

    long id;
    long empresaId;
    String empresaRazonSocial;
    String nombre;
    String apellidos;
    String identificacion;
    String genero;
    String direccion;
    String telefono;
    int perfil;
    long tipoIdentificacionId;
    String tipoIdentificacionDesc;
    String estado;
    String sangre;
    long ciudadId;
    boolean cliente;
    String ciudadDesc;
    String clave;
    int pin;
    String correo;
    long grupoJornadaId;
    long jornadaId;
    String tag;
    String responsabilidadFiscal;
    JsonObject atributos;
    Date fecha;
    Date fechaInicio;
    Date fechaFin;
    boolean principal;
    String identificador;
    ArrayList<IdentificadoresBean> identificadores;
    ArrayList<ModulosBean> modulos;
    int idPerfil;

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }
    
    public boolean isCliente() {
        return cliente;
    }

    public void setIsCliente(boolean isCliente) {
        this.cliente = isCliente;
    }

    String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getResponsabilidadFiscal() {
        return responsabilidadFiscal;
    }

    public void setResponsabilidadFiscal(String responsabilidadFiscal) {
        this.responsabilidadFiscal = responsabilidadFiscal;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setIdentificadores(ArrayList<IdentificadoresBean> identificadores) {
        this.identificadores = identificadores;
    }

    public ArrayList<IdentificadoresBean> getIdentificadores() {
        return identificadores;
    }

    public long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(long empresaId) {
        this.empresaId = empresaId;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSangre() {
        return sangre;
    }

    public void setSangre(String sangre) {
        this.sangre = sangre;
    }

    public long getCiudadId() {
        return ciudadId;
    }

    public void setCiudadId(long ciudadId) {
        this.ciudadId = ciudadId;
    }

    public long getTipoIdentificacionId() {
        return tipoIdentificacionId;
    }

    public void setTipoIdentificacionId(long tipoIdentificacionId) {
        this.tipoIdentificacionId = tipoIdentificacionId;
    }

    public String getTipoIdentificacionDesc() {
        return tipoIdentificacionDesc;
    }

    public void setTipoIdentificacionDesc(String tipoIdentificacionDesc) {
        this.tipoIdentificacionDesc = tipoIdentificacionDesc;
    }

    public String getCiudadDesc() {
        return ciudadDesc;
    }

    public void setCiudadDesc(String ciudadDesc) {
        this.ciudadDesc = ciudadDesc;
    }

    public String getEmpresaRazonSocial() {
        return empresaRazonSocial;
    }

    public void setEmpresaRazonSocial(String empresaRazonSocial) {
        this.empresaRazonSocial = empresaRazonSocial;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public ArrayList<ModulosBean> getModulos() {
        return modulos;
    }

    public void setModulos(ArrayList<ModulosBean> modulos) {
        this.modulos = modulos;
    }

    public long getGrupoJornadaId() {
        return grupoJornadaId;
    }

    public void setGrupoJornadaId(long grupoJornadaId) {
        this.grupoJornadaId = grupoJornadaId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
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

    public int getPerfil() {
        return perfil;
    }

    public void setPerfil(int perfil) {
        this.perfil = perfil;
    }

    public long getJornadaId() {
        return jornadaId;
    }

    public void setJornadaId(long jornadaId) {
        this.jornadaId = jornadaId;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
    }

    @Override
    public String toString() {
        return nombre; //To change body of generated methods, choose Tools | Templates.
    }

}

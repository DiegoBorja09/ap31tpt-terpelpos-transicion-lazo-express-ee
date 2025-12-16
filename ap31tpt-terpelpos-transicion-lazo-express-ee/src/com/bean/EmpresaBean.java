/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.ArrayList;

/**
 *
 * @author novus
 */
public class EmpresaBean {

    private long id;
    private long dominioId;
    private long negocioId;
    private String nit;
    private String razonSocial;
    private String localizacion;
    private String estado;
    private ArrayList<PerfilesBean> perfilesEmpresa;
    private long empresasId;

    private long ciudadId;
    private String ciudadDescripcion;
    private String ciudadZonaHoraria;
    private int ciudadIndicador;

    private long provinciaId;
    private String provinciaDescripcion;
    private ArrayList<ContactoBean> contacto = new ArrayList<>();

    private long paisId;
    private String paisDescripcion;
    private String paisMoneda;
    private int paisIndicador;
    private String paisNomenclatura;

    private String urlFotos;
    String direccionPrincipal;
    String telefonoPrincipal;

    long descriptorId;
    String descriptorHeader;
    String descriptorFooter;
    String alias;
    String codigo;
    private String nombreReginal;
    private long idTipoEmpresa;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    private long empresaTipoId;

    public long getEmpresaTipoId() {
        return empresaTipoId;
    }

    public void setEmpresaTipoId(long empresaTipoId) {
        this.empresaTipoId = empresaTipoId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(long empresasId) {
        this.empresasId = empresasId;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public long getCiudadId() {
        return ciudadId;
    }

    public void setCiudadId(long ciudadId) {
        this.ciudadId = ciudadId;
    }

    public String getCiudadDescripcion() {
        return ciudadDescripcion;
    }

    public void setCiudadDescripcion(String ciudadDescripcion) {
        this.ciudadDescripcion = ciudadDescripcion;
    }

    public long getProvinciaId() {
        return provinciaId;
    }

    public void setProvinciaId(long provinciaId) {
        this.provinciaId = provinciaId;
    }

    public String getProvinciaDescripcion() {
        return provinciaDescripcion;
    }

    public void setProvinciaDescripcion(String provinciaDescripcion) {
        this.provinciaDescripcion = provinciaDescripcion;
    }

    public long getPaisId() {
        return paisId;
    }

    public void setPaisId(long paisId) {
        this.paisId = paisId;
    }

    public String getPaisDescripcion() {
        return paisDescripcion;
    }

    public void setPaisDescripcion(String paisDescripcion) {
        this.paisDescripcion = paisDescripcion;
    }

    public String getUrlFotos() {
        return urlFotos;
    }

    public void setUrlFotos(String urlFotos) {
        this.urlFotos = urlFotos;
    }

    public String getCiudadZonaHoraria() {
        return ciudadZonaHoraria;
    }

    public void setCiudadZonaHoraria(String ciudadZonaHoraria) {
        this.ciudadZonaHoraria = ciudadZonaHoraria;
    }

    public int getCiudadIndicador() {
        return ciudadIndicador;
    }

    public void setCiudadIndicador(int ciudadIndicador) {
        this.ciudadIndicador = ciudadIndicador;
    }

    public String getPaisMoneda() {
        return paisMoneda;
    }

    public void setPaisMoneda(String paisMoneda) {
        this.paisMoneda = paisMoneda;
    }

    public int getPaisIndicador() {
        return paisIndicador;
    }

    public void setPaisIndicador(int paisIndicador) {
        this.paisIndicador = paisIndicador;
    }

    public String getPaisNomenclatura() {
        return paisNomenclatura;
    }

    public void setPaisNomenclatura(String paisNomenclatura) {
        this.paisNomenclatura = paisNomenclatura;
    }

    public String getDireccionPrincipal() {
        return direccionPrincipal;
    }

    public void setDireccionPrincipal(String direccionPrincipal) {
        this.direccionPrincipal = direccionPrincipal;
    }

    public String getTelefonoPrincipal() {
        return telefonoPrincipal;
    }

    public void setTelefonoPrincipal(String telefonoPrincipal) {
        this.telefonoPrincipal = telefonoPrincipal;
    }

    public long getDescriptorId() {
        return descriptorId;
    }

    public void setDescriptorId(long descriptorId) {
        this.descriptorId = descriptorId;
    }

    public String getDescriptorHeader() {
        return descriptorHeader;
    }

    public void setDescriptorHeader(String descriptorHeader) {
        this.descriptorHeader = descriptorHeader;
    }

    public String getDescriptorFooter() {
        return descriptorFooter;
    }

    public void setDescriptorFooter(String descriptorFooter) {
        this.descriptorFooter = descriptorFooter;
    }

    public long getDominioId() {
        return dominioId;
    }

    public void setDominioId(long dominioId) {
        this.dominioId = dominioId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(long negocioId) {
        this.negocioId = negocioId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ArrayList<ContactoBean> getContacto() {
        return contacto;
    }

    public void setContacto(ArrayList<ContactoBean> contacto) {
        this.contacto = contacto;
    }

    public void setPerfilesEmpresa(ArrayList<PerfilesBean> arrayList) {
        this.perfilesEmpresa = arrayList;
    }

    public ArrayList<PerfilesBean> getPerfilesEmpresa() {
        return this.perfilesEmpresa;
    }

    public String getNombreReginal() {
        return nombreReginal;
    }

    public void setNombreReginal(String nombreReginal) {
        this.nombreReginal = nombreReginal;
    }

    public long getIdTipoEmpresa() {
        return idTipoEmpresa;
    }

    public void setIdTipoEmpresa(long idTipoEmpresa) {
        this.idTipoEmpresa = idTipoEmpresa;
    }

}

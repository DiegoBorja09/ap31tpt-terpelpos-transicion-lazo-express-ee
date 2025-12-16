package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "personas")
public class CtPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;
    private String clave;
    private String identificacion;
    
    @Column(name = "tipos_identificacion_id")
    private Long tiposIdentificacionId;
    
    private String nombre;
    private String estado;
    
    @Column(name = "correo")
    private String correo;
    
    private String direccion;
    
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    
    @Column(name = "perfiles_id")
    private Long perfilesId;
    
    private String telefono;
    private String celular;
    
    @Column(name = "create_user")
    private String createUser;
    
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    
    @Column(name = "update_user")
    private String updateUser;
    
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    
    @Column(name = "ciudades_id")
    private Long ciudadesId;
    
    private String sangre;
    private String genero;
    
    @Column(name = "empresas_id")
    private Long empresasId;
    
    @Column(name = "sucursales_id")
    private Long sucursalesId;
    
    private Boolean sincronizado;
    private String tag;
    private String pin;

    public CtPerson() {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Long getTiposIdentificacionId() {
        return tiposIdentificacionId;
    }

    public void setTiposIdentificacionId(Long tiposIdentificacionId) {
        this.tiposIdentificacionId = tiposIdentificacionId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getPerfilesId() {
        return perfilesId;
    }

    public void setPerfilesId(Long perfilesId) {
        this.perfilesId = perfilesId;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getCiudadesId() {
        return ciudadesId;
    }

    public void setCiudadesId(Long ciudadesId) {
        this.ciudadesId = ciudadesId;
    }

    public String getSangre() {
        return sangre;
    }

    public void setSangre(String sangre) {
        this.sangre = sangre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(Long empresasId) {
        this.empresasId = empresasId;
    }

    public Long getSucursalesId() {
        return sucursalesId;
    }

    public void setSucursalesId(Long sucursalesId) {
        this.sucursalesId = sucursalesId;
    }

    public Boolean getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(Boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
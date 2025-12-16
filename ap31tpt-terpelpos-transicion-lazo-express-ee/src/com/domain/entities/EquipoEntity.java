package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "equipos")
public class EquipoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresas_id")
    private Long empresasId;

    @Column(name = "equipos_tipos_id")
    private Long equiposTiposId;

    @Column(name = "equipos_protocolos_id")
    private Long equiposProtocolosId;

    @Column(name = "create_user")
    private Integer createUser;

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "update_user")
    private Integer updateUser;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "factor_precio")
    private Integer factorPrecio;

    @Column(name = "factor_importe")
    private Integer factorImporte;

    @Column(name = "factor_inventario")
    private Integer factorInventario;

    @Column(name = "lector_port")
    private Integer lectorPort;

    @Column(name = "impresora_port")
    private Integer impresoraPort;

    @Column(name = "almacenamientos_id")
    private Long almacenamientosId;

    @Column(name = "serial_equipo")
    private String serialEquipo;

    @Column(name = "lector_ip")
    private String lectorIp;

    @Column(name = "estado")
    private String estado;

    @Column(name = "token")
    private String token;

    @Column(name = "password")
    private String password;

    @Column(name = "mac")
    private String mac;

    @Column(name = "ip")
    private String ip;

    @Column(name = "port")
    private String port;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(name = "impresora_ip")
    private String impresoraIp;

    @Column(name = "autorizado")
    private String autorizado;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(Long empresasId) {
        this.empresasId = empresasId;
    }

    public Long getEquiposTiposId() {
        return equiposTiposId;
    }

    public void setEquiposTiposId(Long equiposTiposId) {
        this.equiposTiposId = equiposTiposId;
    }

    public Long getEquiposProtocolosId() {
        return equiposProtocolosId;
    }

    public void setEquiposProtocolosId(Long equiposProtocolosId) {
        this.equiposProtocolosId = equiposProtocolosId;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getFactorPrecio() {
        return factorPrecio;
    }

    public void setFactorPrecio(Integer factorPrecio) {
        this.factorPrecio = factorPrecio;
    }

    public Integer getFactorImporte() {
        return factorImporte;
    }

    public void setFactorImporte(Integer factorImporte) {
        this.factorImporte = factorImporte;
    }

    public Integer getFactorInventario() {
        return factorInventario;
    }

    public void setFactorInventario(Integer factorInventario) {
        this.factorInventario = factorInventario;
    }

    public Integer getLectorPort() {
        return lectorPort;
    }

    public void setLectorPort(Integer lectorPort) {
        this.lectorPort = lectorPort;
    }

    public Integer getImpresoraPort() {
        return impresoraPort;
    }

    public void setImpresoraPort(Integer impresoraPort) {
        this.impresoraPort = impresoraPort;
    }

    public Long getAlmacenamientosId() {
        return almacenamientosId;
    }

    public void setAlmacenamientosId(Long almacenamientosId) {
        this.almacenamientosId = almacenamientosId;
    }

    public String getSerialEquipo() {
        return serialEquipo;
    }

    public void setSerialEquipo(String serialEquipo) {
        this.serialEquipo = serialEquipo;
    }

    public String getLectorIp() {
        return lectorIp;
    }

    public void setLectorIp(String lectorIp) {
        this.lectorIp = lectorIp;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getImpresoraIp() {
        return impresoraIp;
    }

    public void setImpresoraIp(String impresoraIp) {
        this.impresoraIp = impresoraIp;
    }

    public String getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(String autorizado) {
        this.autorizado = autorizado;
    }
}

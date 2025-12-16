package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ct_movimientos_cliente")
public class CtMovimientosClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_cliente")
    private Integer idMovimientoCliente;

    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "id_jornada")
    private Long idJornada;

    @Column(name = "id_transmision")
    private Long idTransmision;

    @Column(name = "fecha_movimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMovimiento;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "dia")
    private Integer dia;

    @Column(name = "sincronizado")
    private Integer sincronizado;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "tipo_documento")
    private Integer tipoDocumento;

    @Column(name = "digito_verificacion")
    private Integer digitoVerificacion;

    @Column(name = "tipo_persona")
    private Integer tipoPersona;

    @Column(name = "fecha_actualizacion_cliente")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacionCliente;

    @Column(name = "tipo_negocio")
    private Long tipoNegocio;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Column(name = "regimen_fiscal", columnDefinition = "text")
    private String regimenFiscal;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "tipo_responsabilidad")
    private String tipoResponsabilidad;

    @Column(name = "codigo_sap")
    private String codigoSap;

    @Column(name = "codigo_pais")
    private String codigoPais;

    @Column(name = "nombre_pais")
    private String nombrePais;

    @Column(name = "codigo_departamento")
    private String codigoDepartamento;

    @Column(name = "nombre_departamento")
    private String nombreDepartamento;

    @Column(name = "codigo_ciudad")
    private String codigoCiudad;

    @Column(name = "nombre_ciudad")
    private String nombreCiudad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "direccion", columnDefinition = "text")
    private String direccion;

    @Column(name = "codigo_pais_fiscal")
    private String codigoPaisFiscal;

    @Column(name = "nombre_pais_fiscal")
    private String nombrePaisFiscal;

    @Column(name = "codigo_departamento_fiscal")
    private String codigoDepartamentoFiscal;

    @Column(name = "nombre_departamento_fiscal")
    private String nombreDepartamentoFiscal;

    @Column(name = "codigo_ciudad_fiscal")
    private String codigoCiudadFiscal;

    @Column(name = "nombre_ciudad_fiscal")
    private String nombreCiudadFiscal;

    @Column(name = "codigo_postal_fiscal")
    private String codigoPostalFiscal;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "codigo_tributario_adquiriente")
    private String codigoTributarioAdquiriente;

    @Column(name = "nombre_tributario_adquiriente")
    private String nombreTributarioAdquiriente;

    @Column(name = "nombre_razon_social", columnDefinition = "text")
    private String nombreRazonSocial;

    // Getters y Setters
    public Integer getIdMovimientoCliente() { return idMovimientoCliente; }
    public void setIdMovimientoCliente(Integer idMovimientoCliente) { this.idMovimientoCliente = idMovimientoCliente; }

    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }

    public Long getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(Long idMovimiento) { this.idMovimiento = idMovimiento; }

    public Long getIdJornada() { return idJornada; }
    public void setIdJornada(Long idJornada) { this.idJornada = idJornada; }

    public Long getIdTransmision() { return idTransmision; }
    public void setIdTransmision(Long idTransmision) { this.idTransmision = idTransmision; }

    public Date getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(Date fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getDia() { return dia; }
    public void setDia(Integer dia) { this.dia = dia; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Integer getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(Integer tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Integer getDigitoVerificacion() { return digitoVerificacion; }
    public void setDigitoVerificacion(Integer digitoVerificacion) { this.digitoVerificacion = digitoVerificacion; }

    public Integer getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(Integer tipoPersona) { this.tipoPersona = tipoPersona; }

    public Date getFechaActualizacionCliente() { return fechaActualizacionCliente; }
    public void setFechaActualizacionCliente(Date fechaActualizacionCliente) { this.fechaActualizacionCliente = fechaActualizacionCliente; }

    public Long getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(Long tipoNegocio) { this.tipoNegocio = tipoNegocio; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getRegimenFiscal() { return regimenFiscal; }
    public void setRegimenFiscal(String regimenFiscal) { this.regimenFiscal = regimenFiscal; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoResponsabilidad() { return tipoResponsabilidad; }
    public void setTipoResponsabilidad(String tipoResponsabilidad) { this.tipoResponsabilidad = tipoResponsabilidad; }

    public String getCodigoSap() { return codigoSap; }
    public void setCodigoSap(String codigoSap) { this.codigoSap = codigoSap; }

    public String getCodigoPais() { return codigoPais; }
    public void setCodigoPais(String codigoPais) { this.codigoPais = codigoPais; }

    public String getNombrePais() { return nombrePais; }
    public void setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }

    public String getCodigoDepartamento() { return codigoDepartamento; }
    public void setCodigoDepartamento(String codigoDepartamento) { this.codigoDepartamento = codigoDepartamento; }

    public String getNombreDepartamento() { return nombreDepartamento; }
    public void setNombreDepartamento(String nombreDepartamento) { this.nombreDepartamento = nombreDepartamento; }

    public String getCodigoCiudad() { return codigoCiudad; }
    public void setCodigoCiudad(String codigoCiudad) { this.codigoCiudad = codigoCiudad; }

    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCodigoPaisFiscal() { return codigoPaisFiscal; }
    public void setCodigoPaisFiscal(String codigoPaisFiscal) { this.codigoPaisFiscal = codigoPaisFiscal; }

    public String getNombrePaisFiscal() { return nombrePaisFiscal; }
    public void setNombrePaisFiscal(String nombrePaisFiscal) { this.nombrePaisFiscal = nombrePaisFiscal; }

    public String getCodigoDepartamentoFiscal() { return codigoDepartamentoFiscal; }
    public void setCodigoDepartamentoFiscal(String codigoDepartamentoFiscal) { this.codigoDepartamentoFiscal = codigoDepartamentoFiscal; }

    public String getNombreDepartamentoFiscal() { return nombreDepartamentoFiscal; }
    public void setNombreDepartamentoFiscal(String nombreDepartamentoFiscal) { this.nombreDepartamentoFiscal = nombreDepartamentoFiscal; }

    public String getCodigoCiudadFiscal() { return codigoCiudadFiscal; }
    public void setCodigoCiudadFiscal(String codigoCiudadFiscal) { this.codigoCiudadFiscal = codigoCiudadFiscal; }

    public String getNombreCiudadFiscal() { return nombreCiudadFiscal; }
    public void setNombreCiudadFiscal(String nombreCiudadFiscal) { this.nombreCiudadFiscal = nombreCiudadFiscal; }

    public String getCodigoPostalFiscal() { return codigoPostalFiscal; }
    public void setCodigoPostalFiscal(String codigoPostalFiscal) { this.codigoPostalFiscal = codigoPostalFiscal; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getCodigoTributarioAdquiriente() { return codigoTributarioAdquiriente; }
    public void setCodigoTributarioAdquiriente(String codigoTributarioAdquiriente) { this.codigoTributarioAdquiriente = codigoTributarioAdquiriente; }

    public String getNombreTributarioAdquiriente() { return nombreTributarioAdquiriente; }
    public void setNombreTributarioAdquiriente(String nombreTributarioAdquiriente) { this.nombreTributarioAdquiriente = nombreTributarioAdquiriente; }

    public String getNombreRazonSocial() { return nombreRazonSocial; }
    public void setNombreRazonSocial(String nombreRazonSocial) { this.nombreRazonSocial = nombreRazonSocial; }
}

package com.domain.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_transaccion_proceso", schema = "procesos")
public class CtTblTransaccionProcesoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion_proceso")
    private int idTransaccionProceso;

    @Column(name = "sincronizado")
    private boolean sincronizado;

    @Column(name = "id_integracion")
    private long idIntegracion;

    @Column(name = "id_estado_integracion")
    private long idEstadoIntegracion;

    @Column(name = "id_tipo_transaccion_proceso")
    private long idTipoTransaccionProceso;

    @Column(name = "id_estado_proceso")
    private long idEstadoProceso;

    @Column(name = "id_movimiento")
    private long idMovimiento;

    @Column(name = "reintento_confirmacion")
    private double reintentoConfirmacion;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "id_tipo_negocio")
    private long idTipoNegocio;

    @Column(name = "reapertura")
    private int reapertura;

    @Column(name = "is_fe")
    private boolean isFe;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public long getIdIntegracion() {
        return idIntegracion;
    }

    public void setIdIntegracion(long idIntegracion) {
        this.idIntegracion = idIntegracion;
    }

    public long getIdEstadoIntegracion() {
        return idEstadoIntegracion;
    }

    public void setIdEstadoIntegracion(long idEstadoIntegracion) {
        this.idEstadoIntegracion = idEstadoIntegracion;
    }

    public long getIdTipoTransaccionProceso() {
        return idTipoTransaccionProceso;
    }

    public void setIdTipoTransaccionProceso(long idTipoTransaccionProceso) {
        this.idTipoTransaccionProceso = idTipoTransaccionProceso;
    }

    public long getIdEstadoProceso() {
        return idEstadoProceso;
    }

    public void setIdEstadoProceso(long idEstadoProceso) {
        this.idEstadoProceso = idEstadoProceso;
    }

    public long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public double getReintentoConfirmacion() {
        return reintentoConfirmacion;
    }

    public void setReintentoConfirmacion(double reintentoConfirmacion) {
        this.reintentoConfirmacion = reintentoConfirmacion;
    }

    public int getIdTransaccionProceso() {
        return idTransaccionProceso;
    }

    public void setIdTransaccionProceso(int idTransaccionProceso) {
        this.idTransaccionProceso = idTransaccionProceso;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public long getIdTipoNegocio() {
        return idTipoNegocio;
    }

    public void setIdTipoNegocio(long idTipoNegocio) {
        this.idTipoNegocio = idTipoNegocio;
    }

    public int getReapertura() {
        return reapertura;
    }

    public void setReapertura(int reapertura) {
        this.reapertura = reapertura;
    }

    public boolean isFe() {
        return isFe;
    }

    public void setFe(boolean fe) {
        isFe = fe;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }
}

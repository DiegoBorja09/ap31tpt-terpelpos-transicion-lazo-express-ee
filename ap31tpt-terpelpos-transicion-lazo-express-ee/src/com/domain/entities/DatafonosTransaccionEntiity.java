package com.domain.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones", schema = "datafonos")
public class DatafonosTransaccionEntiity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_transacciones")
    @SequenceGenerator(name = "sq_transacciones", sequenceName = "datafonos.sq_transacciones")
    @Column(name = "id_transaccion")
    private Long idTransaccion;

    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;

    @Column(name = "id_equipo", nullable = false)
    private Long idEquipo;

    @Column(name = "id_datafono", nullable = false)
    private Long idDatafono;

    @Column(name = "id_promotor")
    private Long idPromotor;

    @Column(name = "id_transaccion_operacion", nullable = false)
    private Short idTransaccionOperacion;

    @Column(name = "codigo_autorizacion", nullable = false)
    private String codigoAutorizacion;

    @Column(name = "valor_transaccion", nullable = false)
    private Double valorTransaccion;

    @Column(name = "id_transaccion_estado", nullable = false)
    private Short idTransaccionEstado;

    @Column(name = "id_transaccion_padre")
    private Long idTransaccionPadre;

    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "ano")
    private Short ano;

    @Column(name = "mes")
    private Short mes;

    @Column(name = "dia")
    private Short dia;

    @Column(name = "sincronizado", nullable = false)
    private Short sincronizado = 1;

    @Column(name = "id_medio_pago")
    private Long idMedioPago;

    public Long getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Long idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Long getIdDatafono() {
        return idDatafono;
    }

    public void setIdDatafono(Long idDatafono) {
        this.idDatafono = idDatafono;
    }

    public Long getIdPromotor() {
        return idPromotor;
    }

    public void setIdPromotor(Long idPromotor) {
        this.idPromotor = idPromotor;
    }

    public Short getIdTransaccionOperacion() {
        return idTransaccionOperacion;
    }

    public void setIdTransaccionOperacion(Short idTransaccionOperacion) {
        this.idTransaccionOperacion = idTransaccionOperacion;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public Double getValorTransaccion() {
        return valorTransaccion;
    }

    public void setValorTransaccion(Double valorTransaccion) {
        this.valorTransaccion = valorTransaccion;
    }

    public Short getIdTransaccionEstado() {
        return idTransaccionEstado;
    }

    public void setIdTransaccionEstado(Short idTransaccionEstado) {
        this.idTransaccionEstado = idTransaccionEstado;
    }

    public Long getIdTransaccionPadre() {
        return idTransaccionPadre;
    }

    public void setIdTransaccionPadre(Long idTransaccionPadre) {
        this.idTransaccionPadre = idTransaccionPadre;
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Short getAno() {
        return ano;
    }

    public void setAno(Short ano) {
        this.ano = ano;
    }

    public Short getMes() {
        return mes;
    }

    public void setMes(Short mes) {
        this.mes = mes;
    }

    public Short getDia() {
        return dia;
    }

    public void setDia(Short dia) {
        this.dia = dia;
    }

    public Short getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(Short sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(Long idMedioPago) {
        this.idMedioPago = idMedioPago;
    }
}

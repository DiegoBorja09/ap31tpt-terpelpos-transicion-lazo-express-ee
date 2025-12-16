package com.domain.entities;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "ct_movimientos")
public class CtMovimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresas_id")
    private Long empresasId;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "estado_movimiento")
    private String estadoMovimiento;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(name = "consecutivo")
    private String consecutivo;

    @Column(name = "responsables_id", insertable = false, updatable = false)
    private Long responsablesId;

    @Column(name = "personas_id")
    private Long personasId;

    @Column(name = "terceros_id")
    private Long tercerosId;

    @Column(name = "costo_total")
    private Double costoTotal;

    @Column(name = "venta_total")
    private Float ventaTotal;

    @Column(name = "impuesto_total")
    private Double impuestoTotal;

    @Column(name = "descuento_total")
    private Double descuentoTotal;

    @Column(name = "sincronizado")
    private String sincronizado;

    @Column(name = "equipos_id")
    private Long equiposId;

    @Column(name = "remoto_id")
    private Long remotoId;

    @Column(name = "atributos", columnDefinition = "jsonb")
    private String atributos;

    @Column(name = "impreso")
    private String impreso;

    @Column(name = "movimientos_id")
    private Long movimientosId;

    @Column(name = "uso_dolar")
    private String usoDolar;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "dia")
    private Integer dia;

    @Column(name = "jornadas_id", insertable = false, updatable = false)
    private Long jornadasId;

    @Column(name = "origen_id")
    private Long origenId;

    @Column(name = "prefijo")
    private String prefijo;

    @Column(name = "tipo_negocio")
    private String tipoNegocio;

    @Column(name = "status_pump")
    private String statusPump;

    @Column(name = "pendiente_impresion")
    private String pendienteImpresion;

    @Column(name = "placa_vehiculo")
    private String placaVehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsables_id")
    private CtPerson responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornadas_id")
    private JornadaEntity jornada;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstadoMovimiento() {
        return estadoMovimiento;
    }

    public void setEstadoMovimiento(String estadoMovimiento) {
        this.estadoMovimiento = estadoMovimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public Long getResponsablesId() {
        return responsablesId;
    }

    public void setResponsablesId(Long responsablesId) {
        this.responsablesId = responsablesId;
    }

    public Long getPersonasId() {
        return personasId;
    }

    public void setPersonasId(Long personasId) {
        this.personasId = personasId;
    }

    public Long getTercerosId() {
        return tercerosId;
    }

    public void setTercerosId(Long tercerosId) {
        this.tercerosId = tercerosId;
    }

    public Double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(Double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public Float getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(Float ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public Double getImpuestoTotal() {
        return impuestoTotal;
    }

    public void setImpuestoTotal(Double impuestoTotal) {
        this.impuestoTotal = impuestoTotal;
    }

    public Double getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(Double descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public String getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(String sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Long getEquiposId() {
        return equiposId;
    }

    public void setEquiposId(Long equiposId) {
        this.equiposId = equiposId;
    }

    public Long getRemotoId() {
        return remotoId;
    }

    public void setRemotoId(Long remotoId) {
        this.remotoId = remotoId;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    public String getImpreso() {
        return impreso;
    }

    public void setImpreso(String impreso) {
        this.impreso = impreso;
    }

    public Long getMovimientosId() {
        return movimientosId;
    }

    public void setMovimientosId(Long movimientosId) {
        this.movimientosId = movimientosId;
    }

    public String getUsoDolar() {
        return usoDolar;
    }

    public void setUsoDolar(String usoDolar) {
        this.usoDolar = usoDolar;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Long getJornadasId() {
        return jornadasId;
    }

    public void setJornadasId(Long jornadasId) {
        this.jornadasId = jornadasId;
    }

    public Long getOrigenId() {
        return origenId;
    }

    public void setOrigenId(Long origenId) {
        this.origenId = origenId;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(String tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    public String getStatusPump() {
        return statusPump;
    }

    public void setStatusPump(String statusPump) {
        this.statusPump = statusPump;
    }

    public String getPendienteImpresion() {
        return pendienteImpresion;
    }

    public void setPendienteImpresion(String pendienteImpresion) {
        this.pendienteImpresion = pendienteImpresion;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public CtPerson getResponsable() {
        return responsable;
    }

    public void setResponsable(CtPerson responsable) {
        this.responsable = responsable;
    }

    public JornadaEntity getJornada() {
        return jornada;
    }

    public void setJornada(JornadaEntity jornada) {
        this.jornada = jornada;
    }
}

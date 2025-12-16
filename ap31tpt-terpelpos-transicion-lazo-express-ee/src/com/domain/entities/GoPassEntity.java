package com.domain.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "transacciones_gopass")
public class GoPassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "identificadortransacciongopass")
    private Integer identificadorTransaccionGopass;

    @Column(name = "identificacionpromotor", nullable = false)
    private String identificacionPromotor;

    @Column(name = "codigoeds", nullable = false) 
    private String codigoEds;

    @Column(name = "isla", nullable = false)
    private Long isla;

    @Column(name = "surtidor", nullable = false)
    private Long surtidor;

    @Column(name = "cara", nullable = false)
    private Long cara;

    @Column(name = "placa", nullable = false)
    private String placa;

    @Column(name = "taggopass")
    private String tagGopass;

    @Column(name = "identificadorventaterpel", nullable = false)
    private Integer identificadorVentaTerpel;

    @Column(name = "valor", nullable = false)
    private Long valor;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha", nullable = false)
    private String fecha;

    @Column(name = "intentos_pago", nullable = false, columnDefinition = "smallint default 1")
    private Short intentosPago;

    @Column(name = "referenciacobro")
    private Long referenciaCobro;

    @Column(name = "identificador_movimiento")
    private Long identificadorMovimiento;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdentificadorTransaccionGopass() {
        return identificadorTransaccionGopass;
    }

    public void setIdentificadorTransaccionGopass(Integer identificadorTransaccionGopass) {
        this.identificadorTransaccionGopass = identificadorTransaccionGopass;
    }

    public String getIdentificacionPromotor() {
        return identificacionPromotor;
    }

    public void setIdentificacionPromotor(String identificacionPromotor) {
        this.identificacionPromotor = identificacionPromotor;
    }

    public String getCodigoEds() {
        return codigoEds;
    }

    public void setCodigoEds(String codigoEds) {
        this.codigoEds = codigoEds;
    }

    public Long getIsla() {
        return isla;
    }

    public void setIsla(Long isla) {
        this.isla = isla;
    }

    public Long getSurtidor() {
        return surtidor;
    }

    public void setSurtidor(Long surtidor) {
        this.surtidor = surtidor;
    }

    public Long getCara() {
        return cara;
    }

    public void setCara(Long cara) {
        this.cara = cara;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTagGopass() {
        return tagGopass;
    }

    public void setTagGopass(String tagGopass) {
        this.tagGopass = tagGopass;
    }

    public Integer getIdentificadorVentaTerpel() {
        return identificadorVentaTerpel;
    }

    public void setIdentificadorVentaTerpel(Integer identificadorVentaTerpel) {
        this.identificadorVentaTerpel = identificadorVentaTerpel;
    }

    public Long getValor() {
        return valor;
    }

    public void setValor(Long valor) {
        this.valor = valor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Short getIntentosPago() {
        return intentosPago;
    }

    public void setIntentosPago(Short intentosPago) {
        this.intentosPago = intentosPago;
    }

    public Long getReferenciaCobro() {
        return referenciaCobro;
    }

    public void setReferenciaCobro(Long referenciaCobro) {
        this.referenciaCobro = referenciaCobro;
    }

    public Long getIdentificadorMovimiento() {
        return identificadorMovimiento;
    }

    public void setIdentificadorMovimiento(Long identificadorMovimiento) {
        this.identificadorMovimiento = identificadorMovimiento;
    }
}

package com.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad JPA para la tabla productos en la base de datos lazoexpresscore
 * Mapea la estructura real de la tabla productos
 */
@Entity
@Table(name = "productos", schema = "public")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "plu")
    private String plu;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "precio2", precision = 10, scale = 2)
    private BigDecimal precio2;

    @Column(name = "unidad_medida_id", insertable = false, updatable = false)
    private Long unidadMedidaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id")
    private UnidadEntity unidadMedida;

    @Column(name = "productos_tipos_id")
    private Long productosTiposId;

    @Column(name = "empresas_id")
    private Long empresasId;

    @Column(name = "familias")
    private Long familias;

    @Column(name = "publico")
    private Long publico;

    @Column(name = "p_atributos", columnDefinition = "json")
    private String pAtributos;

    // Constructores
    public ProductoEntity() {
    }

    public ProductoEntity(Long id, String descripcion, BigDecimal precio) {
        this.id = id;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecio2() {
        return precio2;
    }

    public void setPrecio2(BigDecimal precio2) {
        this.precio2 = precio2;
    }

    public Long getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public void setUnidadMedidaId(Long unidadMedidaId) {
        this.unidadMedidaId = unidadMedidaId;
    }

    public UnidadEntity getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadEntity unidadMedida) {
        this.unidadMedida = unidadMedida;
        this.unidadMedidaId = unidadMedida != null ? unidadMedida.getId() : null;
    }

    public Long getProductosTiposId() {
        return productosTiposId;
    }

    public void setProductosTiposId(Long productosTiposId) {
        this.productosTiposId = productosTiposId;
    }

    public Long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(Long empresasId) {
        this.empresasId = empresasId;
    }

    public Long getFamilias() {
        return familias;
    }

    public void setFamilias(Long familias) {
        this.familias = familias;
    }

    public Long getPublico() {
        return publico;
    }

    public void setPublico(Long publico) {
        this.publico = publico;
    }

    public String getpAtributos() {
        return pAtributos;
    }

    public void setpAtributos(String pAtributos) {
        this.pAtributos = pAtributos;
    }

} 
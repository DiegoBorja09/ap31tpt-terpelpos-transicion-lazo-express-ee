package com.bean.entradaCombustible;

/**
 * 🚀 MIGRACIÓN: DTO para parámetros del caso de uso ConfirmarRemisionUseCase
 * 
 * ARQUITECTURA LIMPIA:
 * - Objeto de transferencia de datos inmutable
 * - Encapsula parámetros del método confirmarRemision
 * - Validaciones incluidas para datos críticos
 * 
 * FUNCIONALIDAD:
 * - Contiene ID de remisión y estado a actualizar
 * - Validaciones de negocio incorporadas
 * - Compatibilidad total con DAO original
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 * @since 2024
 */
public class ConfirmarRemisionParams {
    
    private final Long idRemision;
    private final Integer estado;
    
    /**
     * 🏗️ Constructor con validaciones de negocio
     * 
     * @param idRemision ID de la remisión a confirmar (debe ser > 0)
     * @param estado Estado a asignar (normalmente 2 = FINALIZADA)
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public ConfirmarRemisionParams(Long idRemision, Integer estado) {
        // 🔍 Validaciones de entrada
        if (idRemision == null || idRemision <= 0) {
            throw new IllegalArgumentException("ID de remisión debe ser mayor a 0");
        }
        
        if (estado == null) {
            throw new IllegalArgumentException("Estado no puede ser null");
        }
        
        this.idRemision = idRemision;
        this.estado = estado;
    }
    
    // 🔍 Getters
    public Long getIdRemision() {
        return idRemision;
    }
    
    public Integer getEstado() {
        return estado;
    }
    
    // 🎯 Métodos de utilidad
    @Override
    public String toString() {
        return "ConfirmarRemisionParams{" +
                "idRemision=" + idRemision +
                ", estado=" + estado +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfirmarRemisionParams)) return false;
        ConfirmarRemisionParams that = (ConfirmarRemisionParams) o;
        return idRemision.equals(that.idRemision) && estado.equals(that.estado);
    }
    
    @Override
    public int hashCode() {
        return idRemision.hashCode() * 31 + estado.hashCode();
    }
    
    /**
     * 🔍 Método de conveniencia para verificar si es estado finalizado
     */
    public boolean esFinalizada() {
        return estado != null && estado == 2;
    }
    
    /**
     * 🎯 Factory method para crear parámetros de finalización
     */
    public static ConfirmarRemisionParams finalizar(Long idRemision) {
        return new ConfirmarRemisionParams(idRemision, 2);
    }
}
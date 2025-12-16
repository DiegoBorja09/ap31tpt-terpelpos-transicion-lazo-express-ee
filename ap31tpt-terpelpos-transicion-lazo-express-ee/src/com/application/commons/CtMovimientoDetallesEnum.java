package com.application.commons;

public enum CtMovimientoDetallesEnum {
    
    ID("id"),
    ATRIBUTOS("atributos"),
    MOVIMIENTOS_ID("movimientos_id"),
    BODEGAS_ID("bodegas_id"),
    CANTIDAD("cantidad"),
    COSTO_PRODUCTO("costo_producto"),
    PRECIO("precio"),
    DESCUENTOS_ID("descuentos_id"),
    DESCUENTO_CALCULADO("descuento_calculado"),
    FECHA("fecha"),
    ANO("ano"),
    MES("mes"),
    DIA("dia"),
    REMOTO_ID("remoto_id"),
    SUB_TOTAL("sub_total"),
    SUB_MOVIMIENTOS_DETALLES_ID("sub_movimientos_detalles_id"),
    UNIDADES_ID("unidades_id"),
    PRODUCTOS_ID("productos_id"),
    SINCRONIZADO("sincronizado");
    
    private final String columnName;
    
    CtMovimientoDetallesEnum(String columnName) {
        this.columnName = columnName;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    @Override
    public String toString() {
        return columnName;
    }
}

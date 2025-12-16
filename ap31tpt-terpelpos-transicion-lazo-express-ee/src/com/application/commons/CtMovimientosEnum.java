package com.application.commons;

public enum CtMovimientosEnum {
    ID("id"),
    EMPRESAS_ID("empresas_id"),
    MES("mes"), 
    DIA("dia"),
    JORNADAS_ID("jornadas_id"),
    ORIGEN_ID("origen_id"),
    TIPO_NEGOCIO("tipo_negocio"),
    STATUS_PUMP("status_pump"),
    PENDIENTE_IMPRESION("pendiente_impresion"),
    FECHA("fecha"),
    CONSECUTIVO("consecutivo"),
    RESPONSABLES_ID("responsables_id"),
    PERSONAS_ID("personas_id"),
    TERCEROS_ID("terceros_id"),
    COSTO_TOTAL("costo_total"),
    VENTA_TOTAL("venta_total"),
    IMPUESTO_TOTAL("impuesto_total"),
    DESCUENTO_TOTAL("descuento_total"),
    EQUIPOS_ID("equipos_id"),
    REMOTO_ID("remoto_id"),
    ATRIBUTOS("atributos"),
    MOVIMIENTOS_ID("movimientos_id"),
    USO_DOLAR("uso_dolar"),
    ANO("ano"),
    TIPO("tipo"),
    ESTADO_MOVIMIENTO("estado_movimiento"),
    ESTADO("estado"),
    PREFIJO("prefijo"),
    SINCRONIZADO("sincronizado"),
    IMPRESO("impreso"),
    PLACA_VEHICULO("placa_vehiculo");

    private final String columnName;

    CtMovimientosEnum(String columnName) {
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

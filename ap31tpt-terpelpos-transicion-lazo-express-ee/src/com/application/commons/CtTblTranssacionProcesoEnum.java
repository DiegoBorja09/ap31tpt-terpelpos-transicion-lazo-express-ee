package com.application.commons;

public enum CtTblTranssacionProcesoEnum {
    SINCRONIZADO("sincronizado", "boolean"),
    ID_INTEGRACION("id_integracion", "bigint"),
    ID_ESTADO_INTEGRACION("id_estado_integracion", "bigint"),
    ID_TIPO_TRANSACCION_PROCESO("id_tipo_transaccion_proceso", "bigint"),
    ID_ESTADO_PROCESO("id_estado_proceso", "bigint"),
    ID_MOVIMIENTO("id_movimiento", "bigint"),
    REINTENTO_CONFIRMACION("reintentoconfirmacion", "numeric"),
    ID_TRANSACCION_PROCESO("id_transaccion_proceso", "integer"),
    FECHA_CREACION("fecha_creacion", "timestamp without time zone"),
    ID_TIPO_NEGOCIO("id_tipo_negocio", "bigint"),
    REAPERTURA("reapertura", "integer"),
    IS_FE("isfe", "boolean"),
    DESCRIPCION("descripcion", "character varying"),
    USUARIO_CREACION("usuario_creacion", "character varying");

    private final String campo;
    private final String tipo;

    CtTblTranssacionProcesoEnum(String campo, String tipo) {
        this.campo = campo;
        this.tipo = tipo;
    }

    public String getCampo() {
        return campo;
    }

    public String getTipo() {
        return tipo;
    }
}

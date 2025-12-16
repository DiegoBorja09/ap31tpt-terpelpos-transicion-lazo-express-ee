/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.facturacion.electronica;

/**
 *
 * @author Devitech
 */
public enum TiposDocumentos {
    CEDULA_DE_CIUDADANIA("Cédula de ciudadanía", 13),
    DOCUMENTO_NIT("NIT", 31),
    CONSUMIDOR_FINAL("CONSUMIDOR FINAL", 31),
    DOCUMENTO_TARJ_EXTRANJERIA("Tarjeta de extranjería", 21),
    DOCUMENTO_CEDULA_EXTRANJERIA("Cédula de extranjería", 22),
    DOCUMENTO_PASAPORTE("Pasaporte", 41),
    DOCUMENTO_IDENTIFICACION_EXTRANJERO("Documento de identificacion extranjero", 42),
    DOCUMENTO_I_EXTRANJERO("Documento i. extranjero", 42),
    DOCUMENTO_NIT_DE_OTRO_PAIS("NIT de otro país", 50),
    DOCUMENTO_NUIP("Documento NUIP", 90),
    NUIP("NUIP*", 90),
    DOCUMENTO_REGISTRO_CIVIL("Registro civil", 11),
    DOCUMENTO_TARJETA_IDENTIFICACION("Tarjeta de identificación", 12);

    private int valor = 0;
    private String descripcion = "";

    private TiposDocumentos(String descripcion, int valor) {
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public int getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

}

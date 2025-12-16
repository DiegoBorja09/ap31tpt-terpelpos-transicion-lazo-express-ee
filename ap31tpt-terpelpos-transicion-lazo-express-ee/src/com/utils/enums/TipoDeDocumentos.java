/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utils.enums;
/**
 *
 * @author Devitech
 */
public class TipoDeDocumentos {

    private final String tipoDocumento;
    private static final String DOCUMENTO_CEDULA = "CC";
    private static final String DOCUMENTO_CEDULA_EXTRANJERIA = "CE";

    public TipoDeDocumentos(String documento) {
        this.tipoDocumento = documento;
    }

    public String obtenerTiposDocumentos() {
        String documento;
        switch (this.tipoDocumento) {
            case "CEDULA DE CIUDADANIA":
                documento = DOCUMENTO_CEDULA;
                break;
            case "CEDULA DE EXTRANJERÍA":
                documento = DOCUMENTO_CEDULA_EXTRANJERIA;
                break;
            default:
                documento = "";
        }
        return documento;
    }
}

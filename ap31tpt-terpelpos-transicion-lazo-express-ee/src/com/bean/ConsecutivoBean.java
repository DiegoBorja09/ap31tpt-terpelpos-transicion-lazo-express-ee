/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;
import java.util.Date;

/**
 *
 * @author ASUS-PC
 */
public class ConsecutivoBean {

    long id;
    long id_fe;
    int tipo_documento;
    String prefijo;
    String prefijo_fe;
    Date fecha_inicio;
    Date fecha_fin;
    Date fecha_inicio_fe;
    Date fecha_fin_fe;
    long consecutivo_inicial;
    long consecutivo_actual;
    long consecutivo_final;
    long consecutivo_inicial_fe;
    long consecutivo_actual_fe;
    long consecutivo_final_fe;
    String estado;
    String estado_fe;
    String resolucion;
    String resolucion_fe;
    String observaciones;
    String formato;
    long equipo_id;
    int dias;
    JsonObject atributos;
    long horas;
    long minutos;
    int diasFe;

   
    
    public static final int TIPO_NORMAL = 9;
    public static final int TIPO_ELECTRONICA = 15;

    public String getPrefijo_fe() {
        return prefijo_fe;
    }

    public void setPrefijo_fe(String prefijo_fe) {
        this.prefijo_fe = prefijo_fe;
    }

    public Date getFecha_inicio_fe() {
        return fecha_inicio_fe;
    }

    public void setFecha_inicio_fe(Date fecha_inicio_fe) {
        this.fecha_inicio_fe = fecha_inicio_fe;
    }

    public Date getFecha_fin_fe() {
        return fecha_fin_fe;
    }

    public void setFecha_fin_fe(Date fecha_fin_fe) {
        this.fecha_fin_fe = fecha_fin_fe;
    }

    public String getEstado_fe() {
        return estado_fe;
    }

    public void setEstado_fe(String estado_fe) {
        this.estado_fe = estado_fe;
    }   
    
    public long getId_fe() {
        return id_fe;
    }

    public void setId_fe(long id_fe) {
        this.id_fe = id_fe;
    }

    public long getConsecutivo_inicial_fe() {
        return consecutivo_inicial_fe;
    }

    public void setConsecutivo_inicial_fe(long consecutivo_inicial_fe) {
        this.consecutivo_inicial_fe = consecutivo_inicial_fe;
    }

    public long getConsecutivo_actual_fe() {
        return consecutivo_actual_fe;
    }

    public void setConsecutivo_actual_fe(long consecutivo_actual_fe) {
        this.consecutivo_actual_fe = consecutivo_actual_fe;
    }

    public long getConsecutivo_final_fe() {
        return consecutivo_final_fe;
    }

    public void setConsecutivo_final_fe(long consecutivo_final_fe) {
        this.consecutivo_final_fe = consecutivo_final_fe;
    }

    public String getResolucion_fe() {
        return resolucion_fe;
    }

    public void setResolucion_fe(String resolucion_fe) {
        this.resolucion_fe = resolucion_fe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(int tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public long getConsecutivo_inicial() {
        return consecutivo_inicial;
    }

    public void setConsecutivo_inicial(long consecutivo_inicial) {
        this.consecutivo_inicial = consecutivo_inicial;
    }

    public long getConsecutivo_actual() {
        return consecutivo_actual;
    }

    public void setConsecutivo_actual(long consecutivo_actual) {
        this.consecutivo_actual = consecutivo_actual;
    }

    public long getConsecutivo_final() {
        return consecutivo_final;
    }

    public void setConsecutivo_final(long consecutivo_final) {
        this.consecutivo_final = consecutivo_final;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public long getEquipo_id() {
        return equipo_id;
    }

    public void setEquipo_id(long equipo_id) {
        this.equipo_id = equipo_id;
    }

    public String getFormato() {
        if (formato == null) {
            if (resolucion != null) {
                formato = "RESOLUCION " + resolucion + " \r\n";
            }
            if (prefijo != null) {
                formato += "CONSECUTIVOS " + prefijo + "-" + consecutivo_inicial + " hasta " + prefijo + "-" + consecutivo_final + "\r\n";
                formato += observaciones;
            }
        }
        return formato;
    }
    
     public long getHoras() {
        return horas;
    }

    public void setHoras(long horas) {
        this.horas = horas;
    }

    public long getMinutos() {
        return minutos;
    }

    public void setMinutos(long minutos) {
        this.minutos = minutos;
    }
    

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
    }

    public long getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public int getDiasFe() {
        return diasFe;
    }

    public void setDiasFe(int diasFe) {
        this.diasFe = diasFe;
    }

    @Override
    public String toString() {
        return "ConsecutivoBean{" + "id=" + id + ", id_fe=" + id_fe + ", tipo_documento=" + tipo_documento + ", prefijo=" + prefijo + ", prefijo_fe=" + prefijo_fe + ", fecha_inicio=" + fecha_inicio + ", fecha_fin=" + fecha_fin + ", fecha_inicio_fe=" + fecha_inicio_fe + ", fecha_fin_fe=" + fecha_fin_fe + ", consecutivo_inicial=" + consecutivo_inicial + ", consecutivo_actual=" + consecutivo_actual + ", consecutivo_final=" + consecutivo_final + ", consecutivo_inicial_fe=" + consecutivo_inicial_fe + ", consecutivo_actual_fe=" + consecutivo_actual_fe + ", consecutivo_final_fe=" + consecutivo_final_fe + ", estado=" + estado + ", estado_fe=" + estado_fe + ", resolucion=" + resolucion + ", resolucion_fe=" + resolucion_fe + ", observaciones=" + observaciones + ", formato=" + formato + ", equipo_id=" + equipo_id + ", dias=" + dias + ", atributos=" + atributos + ", horas=" + horas + ", minutos=" + minutos + ", diasFe=" + diasFe + '}';
    }
    
    

}

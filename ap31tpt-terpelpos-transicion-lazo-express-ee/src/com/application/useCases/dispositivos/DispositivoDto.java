package com.application.useCases.dispositivos;

import com.google.gson.JsonObject;

public class DispositivoDto {
    private  Integer id;
    private  String tipos;
    private  String conector;
    private  String interfaz;
    private  String estado;
    private  JsonObject atributos;
  
     public DispositivoDto(Integer id, String tipos, String conector, String interfaz, String estado, JsonObject atributos) {
        this.id = id;
        this.tipos = tipos;
        this.conector = conector;
        this.interfaz = interfaz;
        this.estado = estado;
        this.atributos = atributos;
     }

     public DispositivoDto(){ 
     }

     public Integer getId() {
        return id;
     }
     public String getTipos() {
        return tipos;
     }
     public String getConector() {
        return conector;
     }
     public String getInterfaz() {
        return interfaz;
     }
     public String getEstado() {
        return estado;
     }
     public JsonObject getAtributos() {
        return atributos;
     }

     public void setId(Integer id) {
        this.id = id;
     }
     public void setTipos(String tipos) {
        this.tipos = tipos;
     }
     public void setConector(String conector) {
        this.conector = conector;
     }
     public void setInterfaz(String interfaz) {
        this.interfaz = interfaz;
     }
     public void setEstado(String estado) {
        this.estado = estado;
     }
     public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
     }
}

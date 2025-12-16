/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;


/**
 *
 * @author USUARIO
 */
public class DispositivosBean {
    
    
    private long id;
    private String tipos;
    private String conector;
    private String interfaz;
    private int puerto;
    private String notificar;
    private String icono;
    private String estado;
    private String atributos;
    
  

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTipos() {
        return tipos;
    }

    public void setTipos(String tipos) {
        this.tipos = tipos;
    }

    public String getConector() {
        return conector;
    }

    public void setConector(String conector) {
        this.conector = conector;
    }

    public String getInterfaz() {
        return interfaz;
    }

    public void setInterfaz(String interfaz) {
        this.interfaz = interfaz;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getNotificar() {
        return notificar;
    }

    public void setNotificar(String notificar) {
        this.notificar = notificar;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    @Override
    public String toString() {
        return "DispositivosBean{" + "id=" + id + ", tipos=" + tipos + ", conector=" + conector + ", interfaz=" + interfaz + ", puerto=" + puerto + ", notificar=" + notificar + ", icono=" + icono + ", estado=" + estado + ", atributos=" + atributos + '}';
    }
      
    
}

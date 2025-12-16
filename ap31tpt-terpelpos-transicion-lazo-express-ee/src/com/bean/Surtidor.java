
package com.bean;


public class Surtidor implements Comparable<Surtidor> {

    private String ip;
    private int id;
    private int surtidorId;
    private int port;
    private int surtidor;
    private int cara;
    private int manguera;
    private int grado;
    private int isla;
    private long productoIdentificador;
    private String productoDescripcion;
    private long familiaIdentificador;
    private String familiaDescripcion;
    private boolean bloqueo;
    private String motivoBloqueo;
    private String host;
    private int factorInventario;
    private int factorVolumenParcial;
    private int fatorImporteParcial;
    private int factorPrecio;
    private float productoPrecio;
    private boolean estaDentroDelRango;

    public float getProductoPrecio() {
        return productoPrecio;
    }

    public void setProductoPrecio(float productoPrecio) {
        this.productoPrecio = productoPrecio;
    }

    private long totalizadorVolumen;
    private long totalizadorVolumenReal;
    private long totalizadorVenta;

    public int getSurtidorId() {
        return surtidorId;
    }

    public void setSurtidorId(int surtidorId) {
        this.surtidorId = surtidorId;
    }

    
    
    public int getFactorInventario() {
        return factorInventario;
    }

    public void setFactorInventario(int factorInventario) {
        this.factorInventario = factorInventario;
    }

    public int getFactorVolumenParcial() {
        return factorVolumenParcial;
    }

    public void setFactorVolumenParcial(int factorVolumenParcial) {
        this.factorVolumenParcial = factorVolumenParcial;
    }

    public int getFatorImporteParcial() {
        return fatorImporteParcial;
    }

    public void setFatorImporteParcial(int fatorImporteParcial) {
        this.fatorImporteParcial = fatorImporteParcial;
    }

    public int getFactorPrecio() {
        return factorPrecio;
    }

    public void setFactorPrecio(int factorPrecio) {
        this.factorPrecio = factorPrecio;
    }

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public void setMotivoBloqueo(String motivoBloqueo) {
        this.motivoBloqueo = motivoBloqueo;
    }

    public boolean isBloqueo() {
        return bloqueo;
    }

    public void setBloqueo(boolean bloqueo) {
        this.bloqueo = bloqueo;
    }

    public int getSurtidor() {
        return surtidor;
    }

    public void setSurtidor(int surtidor) {
        this.surtidor = surtidor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCara() {
        return cara;
    }

    public void setCara(int cara) {
        this.cara = cara;
    }

    public int getManguera() {
        return manguera;
    }

    public void setManguera(int manguera) {
        this.manguera = manguera;
    }

    public long getTotalizadorVolumen() {
        return totalizadorVolumen;
    }

    public void setTotalizadorVolumen(long totalizadorVolumen) {
        this.totalizadorVolumen = totalizadorVolumen;
    }

    public long getTotalizadorVenta() {
        return totalizadorVenta;
    }

    public void setTotalizadorVenta(long totalizadorVenta) {
        this.totalizadorVenta = totalizadorVenta;
    }

    public long getProductoIdentificador() {
        return productoIdentificador;
    }

    public void setProductoIdentificador(long productoIdentificador) {
        this.productoIdentificador = productoIdentificador;
    }

    public String getProductoDescripcion() {
        return productoDescripcion;
    }

    public void setProductoDescripcion(String productoDescripcion) {
        this.productoDescripcion = productoDescripcion;
    }

    public long getFamiliaIdentificador() {
        return familiaIdentificador;
    }

    public void setFamiliaIdentificador(long familiaIdentificador) {
        this.familiaIdentificador = familiaIdentificador;
    }

    public String getFamiliaDescripcion() {
        return this.familiaDescripcion;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;

    }

    public void setFamiliaDescripcion(String familiaDescripcion) {
        this.familiaDescripcion = familiaDescripcion;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public int getIsla() {
        return isla;
    }

    public void setIsla(int isla) {
        this.isla = isla;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTotalizadorVolumenReal() {
        return totalizadorVolumenReal;
    }

    public void setTotalizadorVolumenReal(long totalizadorVolumenReal) {
        this.totalizadorVolumenReal = totalizadorVolumenReal;
    }

    public boolean isEstaDentroDelRango() {
        return estaDentroDelRango;
    }

    public void setEstaDentroDelRango(boolean estaDentroDelRango) {
        this.estaDentroDelRango = estaDentroDelRango;
    }
    
    

    @Override
    public int compareTo(Surtidor o) {
        if (manguera < o.manguera) {
            return -1;
        }
        if (manguera > o.manguera) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Surtidor{" + "ip=" + ip + ", id=" + id + ", surtidorId=" + surtidorId + ", port=" + port + ", surtidor=" + surtidor + ", cara=" + cara + ", manguera=" + manguera + ", grado=" + grado + ", isla=" + isla + ", productoIdentificador=" + productoIdentificador + ", productoDescripcion=" + productoDescripcion + ", familiaIdentificador=" + familiaIdentificador + ", familiaDescripcion=" + familiaDescripcion + ", bloqueo=" + bloqueo + ", motivoBloqueo=" + motivoBloqueo + ", host=" + host + ", factorInventario=" + factorInventario + ", factorVolumenParcial=" + factorVolumenParcial + ", fatorImporteParcial=" + fatorImporteParcial + ", factorPrecio=" + factorPrecio + ", productoPrecio=" + productoPrecio + ", totalizadorVolumen=" + totalizadorVolumen + ", totalizadorVolumenReal=" + totalizadorVolumenReal + ", totalizadorVenta=" + totalizadorVenta + '}';
    }

    
    
}

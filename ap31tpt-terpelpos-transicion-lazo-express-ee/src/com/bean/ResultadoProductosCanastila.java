package com.bean;

import java.util.List;

public class ResultadoProductosCanastila {
    private List<MovimientosDetallesBean> productos;
    private long totalRegistros;

    public ResultadoProductosCanastila(List<MovimientosDetallesBean> productos, long totalRegistros) {
        this.productos = productos;
        this.totalRegistros = totalRegistros;
    }

    public List<MovimientosDetallesBean> getProductos() {
        return productos;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

}

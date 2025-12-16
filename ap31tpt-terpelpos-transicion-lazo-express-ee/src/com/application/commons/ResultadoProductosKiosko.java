package com.application.commons;

import com.bean.MovimientosDetallesBean;
import java.util.List;

public class ResultadoProductosKiosko {
    private List<MovimientosDetallesBean> productos;
    private long totalRegistros;

    public ResultadoProductosKiosko(List<MovimientosDetallesBean> productos, long totalRegistros) {
        this.productos = productos;
        this.totalRegistros = totalRegistros;
    }

    public List<MovimientosDetallesBean> getProductos() {
        return productos;
    }

    public void setProductos(List<MovimientosDetallesBean> productos) {
        this.productos = productos;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }
}

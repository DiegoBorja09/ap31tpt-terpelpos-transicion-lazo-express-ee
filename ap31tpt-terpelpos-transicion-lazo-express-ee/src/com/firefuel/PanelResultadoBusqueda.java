package com.firefuel;

import com.application.useCases.movimientos.FindAllProductoTipoKioskoUseCase;
import com.application.useCases.productos.BuscarProductosTipoKioskoUseCase;
import com.bean.MovimientosDetallesBean;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;


public class PanelResultadoBusqueda extends javax.swing.JPanel {

    KCOViewController pedidoKCO;
    MovimientosDetallesBean producto;
    ArrayList<MovimientosDetallesBean> lista;
    MovimientosDao mdao = new MovimientosDao();
    FindAllProductoTipoKioskoUseCase findAllProductoTipoKioskoUseCase;
    static PanelResultadoBusqueda panelResultadoBusqueda;

    public PanelResultadoBusqueda() {
        initComponents();
        jscrollStyle();
    }

    public PanelResultadoBusqueda(KCOViewController pedidoKCO) {
        this.pedidoKCO = pedidoKCO;
        initComponents();
        this.init();
    }

    public void jscrollStyle() {
        jscrollProductos.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jscrollProductos.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jscrollProductos.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
    }

    @SuppressWarnings("unchecked")
    public void init() {
        try {
            findAllProductoTipoKioskoUseCase = new FindAllProductoTipoKioskoUseCase();
            List<MovimientosDetallesBean> listaProductosTipoKCO = findAllProductoTipoKioskoUseCase.execute();
            //ArrayList<MovimientosDetallesBean> listaProductos = mdao.findAllProductoTipoKIOSCO(null);
            lista = new ArrayList<>(listaProductosTipoKCO);
            //lista = (ArrayList<MovimientosDetallesBean>) listaProductos.clone();
            listarProductos(pnlResultadoBusqueda, lista);
            jscrollStyle();
        } catch (RuntimeException  ex) {
            Logger.getLogger(PanelResultadoBusqueda.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PanelResultadoBusqueda getInstancia(KCOViewController pedidoKCO) {
        if (panelResultadoBusqueda == null) {
            panelResultadoBusqueda = new PanelResultadoBusqueda(pedidoKCO);
        }
        return panelResultadoBusqueda;
    }

    public static void limpiarInstancia() {
        if (panelResultadoBusqueda != null) {
            panelResultadoBusqueda = null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jscrollProductos = new javax.swing.JScrollPane();
        pnlResultadoBusqueda = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1212, 286));
        setMinimumSize(new java.awt.Dimension(1212, 286));
        setPreferredSize(new java.awt.Dimension(1212, 286));
        setLayout(null);

        jscrollProductos.setBorder(null);
        jscrollProductos.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jscrollProductos.setMaximumSize(new java.awt.Dimension(1212, 286));
        jscrollProductos.setMinimumSize(new java.awt.Dimension(1212, 286));
        jscrollProductos.setPreferredSize(new java.awt.Dimension(1212, 286));

        pnlResultadoBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        pnlResultadoBusqueda.setMaximumSize(new java.awt.Dimension(1212, 800));
        pnlResultadoBusqueda.setMinimumSize(new java.awt.Dimension(1212, 800));

        javax.swing.GroupLayout pnlResultadoBusquedaLayout = new javax.swing.GroupLayout(pnlResultadoBusqueda);
        pnlResultadoBusqueda.setLayout(pnlResultadoBusquedaLayout);
        pnlResultadoBusquedaLayout.setHorizontalGroup(
            pnlResultadoBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1212, Short.MAX_VALUE)
        );
        pnlResultadoBusquedaLayout.setVerticalGroup(
            pnlResultadoBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );

        jscrollProductos.setViewportView(pnlResultadoBusqueda);

        add(jscrollProductos);
        jscrollProductos.setBounds(0, 0, 1210, 286);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jscrollProductos;
    private javax.swing.JPanel pnlResultadoBusqueda;
    // End of variables declaration//GEN-END:variables

    public void listarProductos(JPanel panel, ArrayList<MovimientosDetallesBean> listaProductos) {
        int i = 1;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 1212;
        int altoComponente = 54;
        int columnas = 1;
        int margenx = 5;
        int margeny = 5;
        panel.removeAll();
        if (listaProductos != null) {
            for (MovimientosDetallesBean productos : listaProductos) {
                if (componentesX == columnas) {
                    componentesX = 0;
                    componentesY++;
                }
                PanelProducto jitem = new PanelProducto(productos, pedidoKCO);
                panel.add(jitem);
                jitem.setBounds(componentesX * (ancho + margenx), componentesY * (altoComponente + margeny), ancho,
                        altoComponente);
                componentesX++;
                i++;

            }
            int altoInicial = (listaProductos.size() / columnas) + 1;
            int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
            if (altoFinal >= panel.getHeight()) {
                panel.setPreferredSize(new java.awt.Dimension(panel.getWidth(), altoFinal));
                panel.setBounds(0, 0, panel.getWidth(), altoFinal);
            }
            panel.validate();
            panel.repaint();
        }
    }

    public void consultarProductos(String busqueda) {
        try {
            ArrayList<MovimientosDetallesBean> listaBusqueda;
            NovusUtils.printLn("🔍 BÚSQUEDA: '" + busqueda + "'");
            if (busqueda.isEmpty()) {
                NovusUtils.printLn("Consultando Todos los Productos");
                findAllProductoTipoKioskoUseCase = new FindAllProductoTipoKioskoUseCase();
                List<MovimientosDetallesBean> listaProductos = findAllProductoTipoKioskoUseCase.execute();
                listaBusqueda = new ArrayList<>(listaProductos);

                // listaBusqueda = mdao.findAllProductoTipoKIOSCO(null);
            } else {
                NovusUtils.printLn("Consultando Busqueda Producto");
                //listaBusqueda = mdao.busquedaProductoTipoKIOSCO(busqueda);
                listaBusqueda = new BuscarProductosTipoKioskoUseCase(busqueda).execute();
            }
            NovusUtils.printLn("🔍 PRODUCTOS ENCONTRADOS: " + listaBusqueda.size());
            listarProductos(pnlResultadoBusqueda, listaBusqueda);
        } catch ( RuntimeException ex) {
            NovusUtils.printLn("Errorrrrrr " + ex.getMessage());
            Logger.getLogger(PanelResultadoBusqueda.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarProductos() {
        listarProductos(pnlResultadoBusqueda, lista);
    }

}

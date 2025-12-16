package com.firefuel;

import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverDataMedioPago;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoImage;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.application.useCases.sutidores.OcultarInputsFacturaVentaUseCase;
import com.bean.ReciboExtended;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import static com.firefuel.VentaPredefinirPlaca.ventaPredefinirPlaca;
import com.firefuel.mediospago.PanelMediosPago;
import com.google.gson.JsonArray;
import com.neo.app.bean.Manguera;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class EstadoSurtidorViewController extends javax.swing.JPanel {

    
    private static EstadoSurtidorViewController instance;
 
    boolean ventaMarcada = false;
    public boolean ventaAFidelizar = false;
    public boolean ventaComunidades = false;
    public boolean ocultarComponentes = false;
    public String placa = "";
    public boolean ocularComponentes = false;
    boolean ventaAFacturar = false;
    Manguera surtidor;
    int manguera = 0;
    int cara = 0;
    boolean enVenta_1 = false;
    boolean enVenta = false;
    boolean enVenta_2 = false;
    boolean despachoIniciado = false;
    boolean cache = true;
    boolean isCredito = false;
    MovimientosDao mdao = new MovimientosDao();
    JsonArray datafonos = new JsonArray();
    EquipoDao edao;
    
    private RecoverDataMedioPago recoverData;
    private SetupDao setupDao;
    private RecoverMedioPagoImage recoverLista;
    private OcultarInputsFacturaVentaUseCase ocultarInputsFacturaVentaUseCase;

    ArrayList<MedioPagoImageBean> listMedios;
    boolean gestionaVenta = false;

    public Icon icono_fidelizar = new ImageIcon(getClass().getResource("/com/firefuel/resources/btFacturaSmall.png"));

    public void setDespachoIniciado(boolean despachoIniciado) {
        this.despachoIniciado = despachoIniciado;
    }

    public boolean getDespachoIniciado() {
        return this.despachoIniciado;
    }

    public boolean isIsCredito() {
        return isCredito;
    }

    public int getManguera() {
        return manguera;
    }

    public void setManguera(int manguera) {
        this.manguera = manguera;
    }

    public int getCara() {
        return cara;
    }

    public void setCara(int cara) {
        this.cara = cara;
    }

    public void setEnVenta(boolean enVenta) {
        
        this.enVenta = enVenta;
        if (!enVenta) {
            this.desactivarBotones();
        } else {
            habilitarBotones();
        }
    }

    public void habilitarBotones() {
        
          if( enVenta_1 == false){
        btnMediosPago.setEnabled(true);
        lblbtnMediosPago.setForeground(new Color(166, 4, 14));
          }
        if (!gestionaVenta) {
//            btnGestionarVenta.setEnabled(true);
//            lblbtnGestionarVenta.setForeground(new Color(166, 4, 14));
            
             if(enVenta_1== false){
            btnGestionarVenta.setEnabled(true);
            lblbtnGestionarVenta.setForeground(new Color(166, 4, 14));
        }
        }
    }
    
     public void deshabilitarBotones() {
         if(enVenta_2 == false){
            btnMediosPago.setEnabled(false);
            lblbtnMediosPago.setForeground(new Color(166, 4, 14));
         }
        if (gestionaVenta ) {
            if(enVenta_2 == false){
                btnGestionarVenta.setEnabled(false);
                lblbtnGestionarVenta.setForeground(new Color(166, 4, 14));
            }
        }
    }
    

    public EstadoSurtidorViewController(int cara, int manguera, boolean enVenta) {
        initComponents();
        lblbtnMediosPago.setText("<html><center>Medios de pago</center></html>");
        lblbtnGestionarVenta.setText("<html><center>Gestionar venta</center></html>");
        this.manguera = manguera;
        this.cara = cara;
        this.enVenta_1 = false;
        this.enVenta_2 = false;
        
        if(VentaPredefinirPlaca.getInstance1() != null){
            enVenta_1 = VentaPredefinirPlaca.getInstance1().CancelaBotones();
            VentaPredefinirPlaca.getInstance1().deshablitar();
        }
        this.setEnVenta(enVenta); 
        
//        if(VentaPredefinirPlaca.getInstance1().CancelaBotones()){
//            enVenta = false;
//             NovusUtils.printLn("no entra  :");
//         this.setEnVenta(enVenta);    
//        }else{
//             NovusUtils.printLn("no entra al if  :" + cara);
//            this.setEnVenta(enVenta);  
//        }
        
        this.edao = new EquipoDao();
        NovusUtils.printLn("cara: " + cara + " manguera: " + manguera);
        jimporte.setFont(InfoViewController.bebas_60);
        jcantidad.setFont(InfoViewController.bebas_60);
        jprecio.setFont(InfoViewController.bebas_60);
        instance = this;
 
    }
    
      public static EstadoSurtidorViewController getInstance() {
        
        return EstadoSurtidorViewController.instance;
   }

    private void cargarMediosPago() {
        this.setupDao = new SetupDao();
        this.recoverData = new RecoverDataMedioPago(this.setupDao);
        this.recoverLista = new RecoverMedioPagoImage();
        listMedios = recoverLista.execute();
    }

    private void cargarDatafonos(EquipoDao edao) {
        try {
            datafonos = edao.datafonosInfo();
        } catch (DAOException | SQLException ex) {
            Logger.getLogger(VentaCursoPlaca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setLabel(String texto) {
        jLabel2.setText(texto);
    }

    public void setValores(String producto, String familia, String unidad, String importe, String cantidad,
            String precio) {
        jproducto.setText(producto.trim().toUpperCase());
        try {
            switch (familia.trim().toLowerCase()) {
                case "corriente":
                    jmanguera_foto.setIcon(InfoViewController.corriente);
                    break;
                case "diesel":
                    jmanguera_foto.setIcon(InfoViewController.diesel);
                    break;
                case "extra":
                    jmanguera_foto.setIcon(InfoViewController.extra);
                    break;
                case "gas":
                    jmanguera_foto.setIcon(InfoViewController.gas);
                    break;
                case "glp":
                    jmanguera_foto.setIcon(InfoViewController.glp);
                    break;
                default:
                    jmanguera_foto.setIcon(InfoViewController.corriente);
            }

        } catch (Exception e) {
            jmanguera_foto.setIcon(InfoViewController.corriente);
        }
        jcantidadunidad.setText("CANTIDAD (" + unidad.trim().toUpperCase() + ")");
        jimporte.setText("$ " + importe);
        jcantidad.setText(cantidad);
        jprecio.setText("$ " + precio);
    }
    
    

    public void desactivarBotones() {
        this.desactivarBotonMediosPago();
        this.desactivarBotonFactura();
    }

    void desactivarBotonFactura() {
        if(enVenta_2 == false){
            this.btnGestionarVenta.setEnabled(false);
            this.lblbtnGestionarVenta.setForeground(Color.WHITE);
        }
    }

    void desactivarBotonMediosPago() {
         if(enVenta_2 == false){
            btnMediosPago.setEnabled(false);
            lblbtnMediosPago.setForeground(Color.WHITE);
         }
    }

    private boolean inFuel() {
        SurtidorDao dao = new SurtidorDao();
        int estadoPump = dao.getStatusHose(manguera);
        return estadoPump == 3;
    }

    void marcarLifeMiles() {
        this.ventaAFidelizar = true;
        ReciboExtended saleFacture = new ReciboExtended();
        saleFacture.setCara(cara + "");
        saleFacture.setManguera(manguera + "");
        saleFacture.setIsVentaCurso(true);

        NovusUtils.printLn("Cara :" + cara);
        this.gestionaVenta = true;
        boolean isValida = mdao.validarVentaEnCurso(cara);

        if (isValida) {
            this.desactivarBotonFactura();
            VentaCursoPlaca cursoPlaca = new VentaCursoPlaca(Main.info, true, true, this::habilitarBotonGestinonarFactura);
            Manguera dataManguera = new Manguera();
            dataManguera.setCara(cara);
            dataManguera.setId(manguera);
            cursoPlaca.setManguera(dataManguera);
            cursoPlaca.setVisible(true);
        }

    }

    void marcarStore() {
        if (inFuel()) {
            this.ventaMarcada = true;
            this.desactivarBotonMediosPago();
            SurtidorDao sdao = new SurtidorDao();
            try {
                sdao.marcadoVentasCombustibles(manguera);
            } catch (DAOException ex) {
                Logger.getLogger(EstadoSurtidorViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.ocultarInputsFacturaVentaUseCase = new OcultarInputsFacturaVentaUseCase();

        lblbtnGestionarVenta = new javax.swing.JLabel();
        btnGestionarVenta = new javax.swing.JLabel();
        lblbtnMediosPago = new javax.swing.JLabel();
        btnMediosPago = new javax.swing.JLabel();
        jmanguera_foto = new javax.swing.JLabel();
        jprecio2 = new javax.swing.JLabel();
        jimporte = new javax.swing.JLabel();
        jcantidad = new javax.swing.JLabel();
        jcantidadunidad = new javax.swing.JLabel();
        jprecio1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jprecio = new javax.swing.JLabel();
        jproducto = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        lblbtnGestionarVenta.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblbtnGestionarVenta.setForeground(new java.awt.Color(166, 4, 14));
        lblbtnGestionarVenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblbtnGestionarVenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(lblbtnGestionarVenta);
        lblbtnGestionarVenta.setBounds(20, 80, 90, 60);

        btnGestionarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btnGestionarVenta.png"))); // NOI18N
        btnGestionarVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnGestionarVentaMouseReleased(evt);
            }
        });
        add(btnGestionarVenta);
        btnGestionarVenta.setBounds(10, 70, 186, 80);

        lblbtnMediosPago.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblbtnMediosPago.setForeground(new java.awt.Color(166, 4, 14));
        lblbtnMediosPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblbtnMediosPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(lblbtnMediosPago);
        lblbtnMediosPago.setBounds(210, 80, 90, 60);

        btnMediosPago.setForeground(new java.awt.Color(255, 204, 51));
        btnMediosPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnMediosPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btnMediosPago.png"))); // NOI18N
        btnMediosPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnMediosPagoMouseReleased(evt);
            }
        });
        add(btnMediosPago);
        btnMediosPago.setBounds(200, 70, 186, 80);

        jmanguera_foto.setBackground(new java.awt.Color(252, 252, 252));
        jmanguera_foto.setFont(new java.awt.Font("Digital-7 Mono", 1, 24)); // NOI18N
        jmanguera_foto.setForeground(new java.awt.Color(255, 255, 255));
        jmanguera_foto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmanguera_foto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/indicadorSurtidor_corriente.png"))); // NOI18N
        jmanguera_foto.setOpaque(true);
        add(jmanguera_foto);
        jmanguera_foto.setBounds(10, 200, 100, 150);

        jprecio2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jprecio2.setForeground(new java.awt.Color(255, 204, 51));
        jprecio2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jprecio2.setText("IMPORTE");
        add(jprecio2);
        jprecio2.setBounds(160, 160, 200, 30);

        jimporte.setBackground(new java.awt.Color(204, 0, 0));
        jimporte.setFont(new java.awt.Font("Digital-7 Mono", 1, 42)); // NOI18N
        jimporte.setForeground(new java.awt.Color(255, 255, 255));
        jimporte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jimporte.setText("$ 0.00");
        jimporte.setOpaque(true);
        add(jimporte);
        jimporte.setBounds(130, 200, 260, 60);

        jcantidad.setBackground(new java.awt.Color(204, 0, 0));
        jcantidad.setFont(new java.awt.Font("Digital-7 Mono", 1, 42)); // NOI18N
        jcantidad.setForeground(new java.awt.Color(255, 255, 255));
        jcantidad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jcantidad.setText("0.0");
        jcantidad.setOpaque(true);
        add(jcantidad);
        jcantidad.setBounds(130, 320, 260, 60);

        jcantidadunidad.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jcantidadunidad.setForeground(new java.awt.Color(255, 204, 51));
        jcantidadunidad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jcantidadunidad.setText("CANTIDAD");
        add(jcantidadunidad);
        jcantidadunidad.setBounds(140, 270, 240, 40);

        jprecio1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jprecio1.setForeground(new java.awt.Color(255, 204, 51));
        jprecio1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jprecio1.setText("PRECIO");
        add(jprecio1);
        jprecio1.setBounds(160, 390, 200, 40);

        jLabel2.setFont(new java.awt.Font("Terpel Sans", 1, 72)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 0, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("1");
        add(jLabel2);
        jLabel2.setBounds(20, 370, 90, 130);

        jprecio.setBackground(new java.awt.Color(204, 0, 0));
        jprecio.setFont(new java.awt.Font("Digital-7 Mono", 1, 42)); // NOI18N
        jprecio.setForeground(new java.awt.Color(255, 255, 255));
        jprecio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jprecio.setText("$ 0.00");
        jprecio.setOpaque(true);
        add(jprecio);
        jprecio.setBounds(140, 440, 240, 60);

        jproducto.setBackground(new java.awt.Color(255, 255, 0));
        jproducto.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jproducto.setForeground(new java.awt.Color(255, 255, 255));
        jproducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jproducto.setText("FAMILIA");
        add(jproducto);
        jproducto.setBounds(0, 0, 390, 60);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        if(cache){
            jLabel3.setIcon(InfoViewController.tarjetaStatusPump);
        }else{
            jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/indicador_surtidor_tarjeta.png"))); // NOI18N
        }
        add(jLabel3);
        jLabel3.setBounds(0, 0, 405, 520);
    }// </editor-fold>//GEN-END:initComponents

    private void btnMediosPagoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMediosPagoMouseReleased
        if (btnMediosPago.isEnabled()) {
            mostrarPanelMediosPago();
        }
    }//GEN-LAST:event_btnMediosPagoMouseReleased

    private void mostrarPanelMediosPago() {
        try {
            Manguera dataManguera = new Manguera();
            dataManguera.setCara(cara);
            dataManguera.setId(manguera);
            PanelMediosPago panelMedios = new PanelMediosPago(dataManguera, Boolean.TRUE);
            
            Main.info.mostrarSubPanel(panelMedios);
        } catch (SQLException ex) {
            Logger.getLogger(VentaCursoPlaca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void btnGestionarVentaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        if (inFuel()) {
            if (this.btnGestionarVenta.isEnabled()) {
                if (ventaAFidelizar) {
                    marcarLifeMiles();
                } else {
                    cargarVentaCursoPlaca();
                }
            }
        } else {
            if(enVenta_2== false){
            this.btnGestionarVenta.setEnabled(false);
            this.lblbtnGestionarVenta.setForeground(Color.WHITE);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel btnGestionarVenta;
    public javax.swing.JLabel btnMediosPago;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jcantidad;
    private javax.swing.JLabel jcantidadunidad;
    private javax.swing.JLabel jimporte;
    private javax.swing.JLabel jmanguera_foto;
    private javax.swing.JLabel jprecio;
    private javax.swing.JLabel jprecio1;
    private javax.swing.JLabel jprecio2;
    private javax.swing.JLabel jproducto;
    public javax.swing.JLabel lblbtnGestionarVenta;
    public javax.swing.JLabel lblbtnMediosPago;
    // End of variables declaration//GEN-END:variables

    private void cargarVentaCursoPlaca() {
        this.ventaAFacturar = true;
        Manguera dataManguera = new Manguera();
        dataManguera.setCara(cara);
        dataManguera.setId(manguera);
        this.desactivarBotonFactura();
        this.gestionaVenta = true;
        this.ocultarComponentes = this.ocultarInputsFacturaVentaUseCase.execute(cara);
        VentaCursoPlaca ventanaCurso = new VentaCursoPlaca(Main.info, true, 
                this::desactivarBotonFactura, 
                this::habilitarBotonGestinonarFactura, 
                placa, ventaComunidades, 
                this.ocultarComponentes);
        ventanaCurso.setManguera(dataManguera);
        ventanaCurso.setVisible(true);
    }

    public void habilitarBotonGestinonarFactura() {
        
         if(enVenta_1== false){
            btnGestionarVenta.setEnabled(true);
            lblbtnGestionarVenta.setForeground(new Color(166, 4, 14));
        }
//        btnGestionarVenta.setEnabled(true);
//        lblbtnGestionarVenta.setForeground(new Color(166, 4, 14));
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

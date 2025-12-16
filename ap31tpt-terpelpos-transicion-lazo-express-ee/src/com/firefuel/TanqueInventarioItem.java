package com.firefuel;

import com.bean.BodegaBean;
import com.bean.ProductoBean;
import java.awt.Color;
import java.util.ArrayList;

public class TanqueInventarioItem extends javax.swing.JPanel {

    InventariosTanques parentView = null;
    private BodegaBean model;
    Thread task = null;
    boolean isSelected = false;

    public TanqueInventarioItem(InventariosTanques parentView, BodegaBean model) {
        initComponents();
        this.model = model;
        this.parentView = parentView;
        init();
    }

    private void init() {
        this.loadComponent();
    }

    void updateDataModel(BodegaBean tank) {
        this.model = tank;
        this.loadComponent();
    }

    void loadComponent() {
        BodegaBean tank = this.getModel();
        this.renderTankInformationData(tank);
        this.renderTankInventaryInformation(tank);
    }

    void renderTankInventaryInformation(BodegaBean tank) {
        if (tank != null) {
            float altura = (float) tank.getAltura_total();
            this.jtotal_water.setText(tank.getAltura_agua() + "");
            ArrayList< ProductoBean> products = tank.getProductos();
            ProductoBean product = !products.isEmpty() ? products.get(0) : null;
            String productUnit = product != null ? (product.getUnidades_medida() != null ? product.getUnidades_medida() : "LITROS") : "LT";
            this.jvolume.setText(tank.getGalonTanque() + " " + productUnit);
            this.jtemperature.setText(tank.getTemperaturaTanque() + "");
        }
    }

    void renderTankInformationData(BodegaBean tank) {
        if (tank != null) {
            this.lbl_tank_number.setText(tank.getNumeroStand() + "");
            this.lbl_tank_description.setText(tank.getDescripcion());
            ArrayList< ProductoBean> products = tank.getProductos();
            String productName = !products.isEmpty() ? products.get(0).getDescripcion().toUpperCase() : "";
            this.jproduct_name.setText(productName);
        }
    }

    public BodegaBean getModel() {
        return this.model;
    }

    void handlePanelSelection() {
        this.toggleSelectedDesign();
        this.parentView.listenTankSelection(this.isSelected, this.getModel().getNumeroStand());
    }

    public void toggleSelectedDesign() {
        this.isSelected = !this.isSelected;
        this.changeStyleBackground(this.isSelected);
    }

    void changeLabelsForeground(Color foregroundColor) {
        this.lbl_tank_description.setForeground(foregroundColor);
        this.lbl_product_name.setForeground(foregroundColor);
        this.lbl_temperature.setForeground(foregroundColor);
        this.lbl_total_water.setForeground(foregroundColor);
        this.lbl_volume.setForeground(foregroundColor);
    }

    void changeStyleBackground(boolean isSelected) {
        if (isSelected) {
            this.jbackground.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/cardTanqueSeleccionada.png")));
            this.lbl_tank_number.setForeground(new Color(186, 12, 47));
            this.changeLabelsForeground(Color.white);
        } else {
            this.lbl_tank_number.setForeground(Color.white);
            this.jbackground.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/cardTanqueNoSeleccionada.png")));
            this.changeLabelsForeground(Color.black);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_tank_number = new javax.swing.JLabel();
        lbl_tank_description = new javax.swing.JLabel();
        lbl_temperature = new javax.swing.JLabel();
        jtemperature = new javax.swing.JLabel();
        lbl_volume = new javax.swing.JLabel();
        jvolume = new javax.swing.JLabel();
        lbl_total_water = new javax.swing.JLabel();
        jtotal_water = new javax.swing.JLabel();
        lbl_product_name = new javax.swing.JLabel();
        jproduct_name = new javax.swing.JLabel();
        jbackground = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        setLayout(null);

        lbl_tank_number.setBackground(new java.awt.Color(186, 12, 47));
        lbl_tank_number.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lbl_tank_number.setForeground(new java.awt.Color(255, 255, 255));
        lbl_tank_number.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_tank_number.setText("1");
        add(lbl_tank_number);
        lbl_tank_number.setBounds(10, 20, 60, 40);

        lbl_tank_description.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lbl_tank_description.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_tank_description.setText("TANQUE DE PRUEBA");
        add(lbl_tank_description);
        lbl_tank_description.setBounds(90, 0, 520, 70);

        lbl_temperature.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_temperature.setText("TEMPERATURA:");
        add(lbl_temperature);
        lbl_temperature.setBounds(20, 220, 160, 40);

        jtemperature.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jtemperature.setText("TEMPERATURA");
        add(jtemperature);
        jtemperature.setBounds(190, 220, 430, 40);

        lbl_volume.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_volume.setText("VOLUMEN:");
        add(lbl_volume);
        lbl_volume.setBounds(20, 120, 160, 40);

        jvolume.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jvolume.setText("VOLUMEN");
        add(jvolume);
        jvolume.setBounds(190, 120, 430, 40);

        lbl_total_water.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_total_water.setText("ALTURA AGUA:");
        add(lbl_total_water);
        lbl_total_water.setBounds(20, 170, 160, 40);

        jtotal_water.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jtotal_water.setText("ALTURA AGUA");
        add(jtotal_water);
        jtotal_water.setBounds(190, 170, 430, 40);

        lbl_product_name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_product_name.setText("PRODUCTO:");
        add(lbl_product_name);
        lbl_product_name.setBounds(20, 80, 160, 40);

        jproduct_name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jproduct_name.setText("PRODUCTO");
        add(jproduct_name);
        jproduct_name.setBounds(190, 80, 430, 30);

        jbackground.setBackground(new java.awt.Color(255, 255, 255));
        jbackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/cardTanqueNoSeleccionada.png"))); // NOI18N
        add(jbackground);
        jbackground.setBounds(0, 0, 640, 290);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        handlePanelSelection();
    }//GEN-LAST:event_formMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jbackground;
    private javax.swing.JLabel jproduct_name;
    private javax.swing.JLabel jtemperature;
    private javax.swing.JLabel jtotal_water;
    private javax.swing.JLabel jvolume;
    private javax.swing.JLabel lbl_product_name;
    private javax.swing.JLabel lbl_tank_description;
    private javax.swing.JLabel lbl_tank_number;
    private javax.swing.JLabel lbl_temperature;
    private javax.swing.JLabel lbl_total_water;
    private javax.swing.JLabel lbl_volume;
    // End of variables declaration//GEN-END:variables
}

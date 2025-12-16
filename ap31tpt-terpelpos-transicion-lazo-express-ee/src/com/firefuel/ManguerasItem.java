package com.firefuel;

import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

public class ManguerasItem extends javax.swing.JPanel {

    private final Surtidor model;
    RumboView parentView = null;
    RecuperacionVentaView parentView1 = null;

    boolean isSelected = false;

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ManguerasItem(RumboView parentView, Surtidor model) {
        initComponents();
        this.model = model;
        this.parentView = parentView;
        this.init();
    }

    public ManguerasItem(RecuperacionVentaView parentView, Surtidor model) {
        initComponents();
        this.model = model;
        this.parentView1 = parentView;
        this.init();
    }

    void handleClick() {
        NovusUtils.beep();
        if (parentView != null) {
            this.parentView.listenHoseClick(this.getModel());
        }
        try {
            this.selectPanel(isSelected);
        } catch (Exception e) {
            NovusUtils.printLn("Mangueras Items Error: " + e.getMessage());
            this.selectPanel(isSelected);
        }
    }

    void selectPanel(boolean selected) {
        if (selected) {
            this.background.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/selected.png")));
        } else {
            this.background.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/activo.png")));
        }
    }

    private void init() {
        this.loadComponent();
    }

    void renderIdentifierData() {
        Surtidor surtidorModel = this.getModel();
        if (surtidorModel != null && isAdBlue(surtidorModel)) {
            this.lbl_hose_number.setFont(new java.awt.Font("Segoe UI Bold", 0, 30));
            this.lbl_hose_number.setText("UREA");
        } else {
            this.lbl_hose_number.setText(surtidorModel.getManguera() + "");
        }
    }

    boolean isAdBlue(Surtidor surtidorModel) {
        return surtidorModel.getProductoDescripcion()
                .equalsIgnoreCase(NovusConstante.PRODUCTO_UREA);
    }

    void loadComponent() {
        this.renderIdentifierData();
    }

    public Surtidor getModel() {
        return this.model;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_hose_number = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setOpaque(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        setLayout(null);

        lbl_hose_number.setBackground(new java.awt.Color(51, 51, 51));
        lbl_hose_number.setFont(new java.awt.Font("Segoe UI", 1, 46)); // NOI18N
        lbl_hose_number.setForeground(new java.awt.Color(223, 26, 9));
        lbl_hose_number.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_hose_number.setText("0");
        add(lbl_hose_number);
        lbl_hose_number.setBounds(0, 0, 145, 145);

        background.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/activo.png"))); // NOI18N
        add(background);
        background.setBounds(0, 0, 145, 145);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseReleased

    }// GEN-LAST:event_formMouseReleased

    private void formMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseClicked
        this.handleClick();
    }// GEN-LAST:event_formMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMousePressed
        // this.background.setIcon(
        // new
        // javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btTouch.png")));
    }// GEN-LAST:event_formMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel lbl_hose_number;
    // End of variables declaration//GEN-END:variables
}

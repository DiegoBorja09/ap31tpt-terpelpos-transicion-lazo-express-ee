/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao;

import com.bean.Notificador;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.SincronizarView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;

/**
 *
 * @author ASUS
 */
public class ListenToNotifyMessage extends Thread implements Notificador {

    String[] tasks = {"sincronizacion_ui"};

    @Override
    public void run() {
        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost("localhost");
        dataSource.setPort(5432);
        dataSource.setDatabaseName("lazoexpresscore");
        dataSource.setUser("pilotico");
        dataSource.setPassword("$2y$12$UWpxiZi3UaF7ZyKeySCpB.5Z5FfRtAAkgYuQz.m4qnLUFR7CmTOu");
        dataSource.setApplicationName("listenToNotifyMessage");

        PGNotificationListener pl = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                if(NovusUtils.getCircuitBreakerStatus()){
                try {
                    // System.out.println("[listenToNotifyMessage]" + payload);
                    JsonObject data = new Gson().fromJson(payload, JsonObject.class);
                    send(data);
                } catch (Exception e) {
                    Logger.getLogger(ListenToNotifyMessage.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
     };

        try (
                 PGConnection connection = (PGConnection) dataSource.getConnection()) {

            for (String task : tasks) {
                try ( Statement statement = connection.createStatement()) {
                    statement.execute("LISTEN " + task + ";");
                }
            }

            connection.addNotificationListener(pl);
            while (true) {
                Thread.sleep(100);
            }
//            
        } catch (Exception e) {
            System.err.println("[listenToNotifyMessage]" + e);
        }
    }

    @Override
    public void send(JsonObject data) {
        System.out.println("[listenToNotifyMessage]" + data.toString());
        int tipoNotificacion = data.get("tipo").getAsInt();

        switch (tipoNotificacion) {
            case NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID:
                sendNotificationStatus(data.get("estado").getAsString(), data.get("mensaje").getAsString(), SincronizarView.jloader_productos, SincronizarView.jbutton_reintentar_productos);
                break;
            case NovusConstante.DESCARGAR_SURTIDORES_ID:
            case NovusConstante.DESCARGAR_BODEGAS_ID:
                sendNotificationStatus(data.get("estado").getAsString(), data.get("mensaje").getAsString(), SincronizarView.jloader_surtidores, SincronizarView.jbutton_reintentar_surtidores);
                sendNotificationStatus(data.get("estado").getAsString(), "", SincronizarView.jloader_bodega, SincronizarView.jbutton_reintentar_bodegas);
                break;
            case NovusConstante.DESCARGAR_DATOS_BASICOS_ID:
                sendNotificationStatus(data.get("estado").getAsString(), data.get("mensaje").getAsString(), SincronizarView.jloader_empresa, SincronizarView.jbutton_reintentar_empresa);
                break;
            case NovusConstante.DESCARGAR_CATEGORIAS_ID:
                sendNotificationStatus(data.get("estado").getAsString(), data.get("mensaje").getAsString(), SincronizarView.jloader_categorias, SincronizarView.jbutton_reintentar_categorias);
                break;
        }
    }
    private void sendNotificationStatus(String status, String mensaje, JLabel labelCheck, JLabel labelSync) {
        if (!mensaje.isEmpty()) {
            addMenssaje(mensaje);
        }
        String iconCheck = "chulito_off.png";
        String iconSync = "btReintentar.png";
        switch (status) {
            case "Encurso":
                iconSync = "btSincro.gif";
                break;
            case "Inicio":
                iconSync = "btSincro.gif";
                iconCheck = "chulito_off.png";
                break;
            case "Error":
                iconCheck = "error.png";
                break;
            default:
                iconCheck = "chulito_on.png";
                System.out.println("[listenToNotifyMessage]" + mensaje);
                break;
        }
        if (labelSync != null) {
            labelSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/" + iconSync)));
        }
        if (labelCheck != null) {
            labelCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/" + iconCheck)));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ListenToNotifyMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addMenssaje(String log) {
        if (SincronizarView.jlog != null) {
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_TIME_AM);
            SincronizarView.jlog.setText(SincronizarView.jlog.getText().trim() + "\n[" + sdf.format(currentDate) + "] " + log.trim());
        }
    }
}

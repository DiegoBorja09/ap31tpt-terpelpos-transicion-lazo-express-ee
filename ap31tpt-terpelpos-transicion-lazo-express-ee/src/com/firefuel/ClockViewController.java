package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockViewController extends JPanel implements AutoCloseable {

    private static ClockViewController instance;

    private final JLabel jfecha = new JLabel();
    private final JLabel jhora = new JLabel();
    private final SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_HUMAN);
    private final Timer timer;

    public ClockViewController() {
        setOpaque(false);
        setLayout(null);
        initUI();

        // Inicializar Timer de Swing
        timer = new Timer(1000, e -> {
            Date ahora = new Date();
            try {
                String[] partes = sdf.format(ahora).split(" ");
                jfecha.setText(partes[0]);
                jhora.setText(partes[1]);
            } catch (Exception ex) {
                System.err.println("❌ Error actualizando reloj: " + ex.getMessage());
            }
        });
        timer.start();
    }

    public static synchronized ClockViewController getInstance() {
        if (instance == null) {
            NovusUtils.printLn("🕒 Creando instancia única de ClockViewController");
            instance = new ClockViewController();
        }
        instance.restartClock();
        return instance;
    }

    private void initUI() {
        jfecha.setFont(new java.awt.Font("Digital-7 Mono", 0, 20));
        jfecha.setForeground(new java.awt.Color(255, 255, 255));
        jfecha.setHorizontalAlignment(SwingConstants.CENTER);
        jfecha.setBounds(0, 0, 110, 40);
        add(jfecha);

        jhora.setFont(new java.awt.Font("Digital-7 Mono", 0, 26));
        jhora.setForeground(new java.awt.Color(255, 219, 0));
        jhora.setHorizontalAlignment(SwingConstants.CENTER);
        jhora.setBounds(0, 30, 110, 40);
        add(jhora);
    }

    public synchronized void startClock() {
        if (!timer.isRunning()) {
            timer.start();
            NovusUtils.printLn("🟢 Clock iniciado.");
        } else {
            NovusUtils.printLn("⚠️ Clock ya estaba corriendo.");
        }
    }

    public synchronized void restartClock() {
        NovusUtils.printLn("🔁 Reiniciando el reloj...");
        stopClock();
        startClock();
    }

    public synchronized void stopClock() {
        if (timer.isRunning()) {
            timer.stop();
            NovusUtils.printLn("🛑 Clock detenido.");
        }
    }

    @Override
    public void close() {
        System.out.println("🔒 Cerrando ClockViewController...");
        stopClock();
    }
}
package com.services;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class ConexionService {

    private final String host;
    private final int timeoutMs;

    public ConexionService(String host, int timeoutMs) {
        this.host = host;
        this.timeoutMs = timeoutMs;
    }

    public boolean tieneConexionInternet() {
        try {
            URL url = new URL("https://" + host);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(timeoutMs);
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}


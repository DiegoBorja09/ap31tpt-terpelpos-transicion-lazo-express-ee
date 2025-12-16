package com.controllers;

import com.firefuel.Main;
import com.firefuel.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

public class ClientWSAsync extends Thread {

    static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static String KEY_DEVITECH = "Bearer -----BEGIN RSA PRIVATE KEY-----MIIEpAIBAAKCAQEAiskm5R0382UD7tEXgTVQHYAYucbYy/beKUndz7Ku2PVUvrKvull35Ql+LpZKah1E7+46M31pxs61Xc4L+fIHn3y0ywwz3uHP/g71vtD6LFy4fTZLirZKfA6WsipUaRoPC6Zye0itE2pvuEXo5uvZdGq+rkwGqQL4SRKRwh0ZX4Xhmtq00NyThPqD7DN+9ZbSkDShm3HkAhhAYPqfvcvPGCiNTEPuEG9Y3cXivkdCBlKhAeTGrvoFkNwKCdd5ZWbLv6LAxuHecu6XHhVMId4NGxCH2XKh2AUv6eJ/gjwkfIbrdO7FdmCe6QC8vWOH9PxXHrWEbFDpCvSHWIvR4qKyZQIDAQABAoIBAQCHXz+uGbr6kVyttIv9vzffHpR/mULcaHc4xNE0B3FfNKWtwPOBjEVTRdgrrvL04Ineknt4v+rOPdBQqGusKHVhDq32pHdv/sj3YjY4IvTzEpntoGk86yRqL3y0Wm+tePqV/YwLTs9rcdV5Y8+SdxjL4lcOAiA4+SmfdRpxwhp+vc+tknfgcs+tSbbDHyDhgQwnsUCDPs5CLgTU9/gqTlUPEezyXZLaWSCttu1cuPUZwRED9Hs2NjXSj1O2HTHAg3Z3TApcnFkG4hVDUBFJFJAFtwpAzrWoSZVZP4s/S4YdkC3hVVIM6bo52jRr0Ci3w3UTuINFuXpNX/O7CYyvvF4BAoGBANpAcpVyMgbutx6g+5Qak49WRfZOaxBH/BwbYuRBYG2PkkANsSoRWRrDUlZY+vGkIXhHp4F4N/7SjFHrpQ/1KvYoN81/A3kZ4c5L1cR7PtTnXAUXNqGlhNALj9jMbCzlhaBFHl87Mz8SQoBCiB/zBYiMYjdGwoqGm8/ESQM8zLWBAoGBAKLKLl565on5uvKUfiPCee1RlBDvGnO4+LM6OF0z0gUSUxhMFklv5zYcT6Vi6f3R6QgPpkG5GNLscNxoHrOIz0Zz496P1ZAgA8DXpCMsKsRD1vu5n9seKienvkqXRP2axK5PEupu4wCuVomiXnlHvLYjY5esT6CouWLOzvbuC1blAoGAPrRXd2Joxx8ck3sy7Jk6HetujFZ5YiMcZsLjharW1oNyRF7qsKhtTkghxtcnufcq+pCzqnnstJSvZfXq5YvNvQ1PAwZj7A4olwmosBussKSMBpZlxsl0QAWiXWpWBgwneSWClV+/2HYZjxoOXAeJZnLW4QS+behAqc++HmUAd4ECgYEAgDdCIkQmhBHfvuRaHYw1QEf6mQPaD79mkrOOZUpFZp0yOXbkLt8meqX9zUOFDNdh9WluB2HkPWzgz5hqZfmhV9o7ZbZf/O5aRm8R5moJHSBZmVZwo8K0bRtfc5yFSEG4G5pIScEgpg6qNilew6NO7R4eeP3MkbuSmFJPDIodAEkCgYA4TZ4h7EgBTAG0+INsTLcVyaLozaiPt9z4XUUbKbYPGaULABoLqBixFiPJgbt2hkf3DNMT42/f744Qxj/tlwzIUx60giiF9iH4obTgJUQlRX4ZW3R7n9FHq+TV/a/eiLYhvcU873zJ7tc+RIoCwsET9HzoJilA/ueNldooEnTvcw==-----END RSA PRIVATE KEY-----";

    public static String GET = "GET";
    public static String POST = "POST";
    public static String PUT = "PUT";
    private int timeout = 30000; // cambio a 65000
    private final String funcion;
    TreeMap<String, String> header = null;
    private String publicKey;
    private final String url;
    private final String method;
    private final String request;
    private JsonObject response;
    private boolean DEBUG;
    private boolean isArray = false;
    private int status = 0;
    private int errorCodigo;
    private String errorMensaje;
    private JsonObject error;

    public static final String NOTIFICAR_LOGINVIEW = "notificar_loginview";

    Gson gson = new GsonBuilder()
            .setDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO)
            .setPrettyPrinting().create();

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);

    /**
     *
     * @param funcion
     * @param url
     * @param method
     * @param json
     * @param DEBUG
     * @param publicKey
     */
    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG, String publicKey) {

        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.request = json.toString();
        this.publicKey = publicKey;
        this.DEBUG = DEBUG;

    }

    public ClientWSAsync(String funcion, String url, String method, boolean DEBUG) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.request = null;
        this.DEBUG = DEBUG;
    }

    public ClientWSAsync(String funcion, String url, String method, boolean DEBUG, TreeMap<String, String> header) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.request = null;
        this.DEBUG = DEBUG;
        this.header = header;
    }

    /**
     *
     * @param funcion
     * @param url
     * @param method
     * @param json
     * @param DEBUG
     */
    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        if (json != null) {
            this.request = json.toString();
        } else {
            this.request = null;
        }
        this.DEBUG = DEBUG;
    }

    /**
     *
     * @param funcion
     * @param url
     * @param method
     * @param json
     * @param DEBUG
     */
    public ClientWSAsync(String funcion, String url, String method, String json, boolean DEBUG) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.request = json;
        this.DEBUG = DEBUG;
    }

    /**
     *
     * @param funcion
     * @param url
     * @param method
     * @param json
     * @param DEBUG
     * @param isArray
     */
    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG, boolean isArray) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        if (json != null) {
            this.request = json.toString();
        } else {
            this.request = null;
        }
        this.DEBUG = DEBUG;
        this.isArray = isArray;
    }

    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG, int timeout) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.timeout = timeout;
        if (json != null) {
            this.request = json.toString();
        } else {
            this.request = null;
        }
        this.DEBUG = DEBUG;
    }

    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG, boolean isArray,
            int timeout) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.timeout = timeout;
        if (json != null) {
            this.request = json.toString();
        } else {
            this.request = null;
        }
        this.DEBUG = DEBUG;
        this.isArray = isArray;
    }

    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG, boolean isArray,
            TreeMap<String, String> header, int timeout) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        this.timeout = timeout;
        if (json != null) {
            this.request = json.toString();
        } else {
            this.request = null;
        }
        this.DEBUG = DEBUG;
        this.isArray = isArray;
        this.header = header;
    }

    public ClientWSAsync(String funcion, String url, String method, JsonObject json, boolean DEBUG, boolean isArray, TreeMap<String, String> header) {
        this.funcion = funcion;
        this.url = url;
        this.method = method;
        if (json != null) {
            this.request = json.toString();
        } else {
            this.request = null;
        }
        this.DEBUG = DEBUG;
        this.isArray = isArray;
        this.header = header;
    }

    @Override
    public void run() {
        try {
            response = execute();
        } catch (WSException ex) {
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonObject execute() throws WSException {
        JsonObject respuesta = null;
        StringBuilder stb;
        try {
            if (url.contains("https")) {
                URL curl = new URL(url);
                HttpsURLConnection conn = (HttpsURLConnection) curl.openConnection();

                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod(method);
                conn.setConnectTimeout(this.timeout);
                conn.setReadTimeout(this.timeout);

                conn.setHostnameVerifier((String hostname, SSLSession session) -> true);

                if (header == null) {
                    header = new TreeMap<>();
                    header.put("content-Type", "application/json");
                    if (Main.credencial != null && Main.credencial.getToken() != null) {
                        header.put("authorization", "Bearer " + Main.credencial.getToken());
                        header.put("password", Main.credencial.getPassword());
                    }
                    header.put("identificadorDispositivo", Main.credencial.getIdentificadorEquipo());
                    header.put("aplicacion", Main.APLICACION_CODE);
                    header.put("versionApp", Main.VERSION_APP);
                    header.put("versionCode", Main.VERSION_CODE + "");
                    header.put("fecha", sdf.format(new Date()));
                    if (publicKey != null) {
                        header.put("key", publicKey);
                    }
                }

                header.entrySet().forEach((entry) -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    conn.setRequestProperty(key, value);
                });

                if (!method.equals(GET)) {
                    if (DEBUG) {
                        NovusUtils.printLn("TX [TASK] " + funcion);
                        NovusUtils.printLn("TX [POST] " + url);
                        NovusUtils.printLn("TX [HEAD] \r\n");

                        for (Map.Entry<String, String> entry : header.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            NovusUtils.printLn(key + ": " + value);
                            NovusUtils.printLn(key + ": " + value);
                        }
                        NovusUtils.printLn("TX [RESQ] " + request);
                    }
                    conn.connect();
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                    wr.write(request);
                    wr.flush();
                } else {
                    if (DEBUG) {
                        NovusUtils.printLn("TX [TASK] " + funcion);
                        NovusUtils.printLn("TX [GET ] " + url);
                    }
                }

                status = conn.getResponseCode();
                String result = conn.getResponseMessage();
                BufferedReader br;
                String line;

                if (status != 0) {
                    switch (status) {
                        case 200:
                        case 201:
                            InputStreamReader ir = new InputStreamReader(conn.getInputStream(), "UTF-8");
                            br = new BufferedReader(ir);

                            stb = new StringBuilder();
                            while ((line = br.readLine()) != null) {
                                stb.append(line);
                            }
                            if (DEBUG) {
                                NovusUtils.printLn("RX [(" + status + "): " + result + "]: \r\n");
                                JsonElement elemento = new Gson().fromJson(stb.toString(), JsonElement.class);
                                NovusUtils.printLn("\r\n" + new GsonBuilder().setPrettyPrinting().create().toJson(elemento));
                            }
                            if (isArray) {
                                JsonArray array = gson.fromJson(stb.toString(), JsonArray.class);
                                respuesta = new JsonObject();
                                respuesta.addProperty("sucess", true);
                                respuesta.add("data", array);
                            } else {
                                try {
                                    respuesta = gson.fromJson(stb.toString(), JsonObject.class);
                                } catch (JsonSyntaxException e) {
                                    respuesta = new JsonObject();
                                    respuesta.addProperty("response", stb.toString());
                                }
                            }
                            break;
                        default:
                            InputStreamReader err = new InputStreamReader(conn.getErrorStream(), "UTF-8");
                            br = new BufferedReader(err);
                            stb = new StringBuilder();
                            while ((line = br.readLine()) != null) {
                                stb.append(line);
                            }

                            NovusUtils.printLn("ERROR RX [(" + status + "): " + stb.toString());
                            try {
                                error = gson.fromJson(stb.toString(), JsonObject.class);
                                error.addProperty("error", true);
                            } catch (JsonSyntaxException e) {
                                error = null;
                            }
                            break;
                    }
                }
            } else {
                URL curl = new URL(url);
                HttpURLConnection conn;
                conn = (HttpURLConnection) curl.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod(method);
                conn.setConnectTimeout(this.timeout);
                conn.setReadTimeout(this.timeout);

                if (header == null) {
                    header = new TreeMap<>();
                    header.put("content-Type", "application/json");
                    if (Main.credencial != null && Main.credencial.getToken() != null) {
                        header.put("Authorization", "Bearer " + Main.credencial.getToken());
                        header.put("password", Main.credencial.getPassword());
                    }
                    header.put("identificadorDispositivo", Main.credencial.getIdentificadorEquipo());
                    header.put("aplicacion", Main.APLICACION_CODE);
                    header.put("versionapp", Main.VERSION_APP);
                    header.put("versioncode", Main.VERSION_CODE + "");
                    header.put("fecha", sdf.format(new Date()));
                    if (publicKey != null) {
                        header.put("key", publicKey);
                    }
                }

                header.entrySet().forEach((entry) -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    conn.setRequestProperty(key, value);
                });
                if (!method.equals(GET)) {
                    if (DEBUG) {
                        NovusUtils.printLn("TX [TASK] " + funcion);
                        NovusUtils.printLn("TX [POST] " + url);
                        NovusUtils.printLn("TX [HEAD] ");
                        for (Map.Entry<String, String> entry : header.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            NovusUtils.printLn(key + ": " + value);
                        }
                        NovusUtils.printLn("TX [RESQ] " + request);

                    }
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                    wr.write(request);
                    wr.flush();
                } else {
                    if (DEBUG) {
                        NovusUtils.printLn("TX [TASK] " + funcion);
                        NovusUtils.printLn("TX [GET ] " + url);
                    }
                }

                status = conn.getResponseCode();
                String result = conn.getResponseMessage();
                BufferedReader br;
                String line;

                if (status != 0) {
                    switch (status) {
                        case 200:
                        case 201:
                            InputStreamReader ir = new InputStreamReader(conn.getInputStream(), "UTF-8");
                            br = new BufferedReader(ir);
                            stb = new StringBuilder();
                            while ((line = br.readLine()) != null) {
                                stb.append(line);
                            }

                            if (DEBUG) {
                                NovusUtils.printLn("RX [(" + status + "): " + result + "]: " + stb.toString());
                            }

                            if (isArray) {
                                JsonArray array = gson.fromJson(stb.toString(), JsonArray.class);
                                respuesta = new JsonObject();
                                respuesta.addProperty("sucess", true);
                                respuesta.add("data", array);
                            } else {
                                try {
                                    respuesta = gson.fromJson(stb.toString(), JsonObject.class);
                                } catch (JsonSyntaxException e) {
                                    respuesta = new JsonObject();
                                    respuesta.addProperty("response", stb.toString());
                                }
                            }

                            break;
                        case 503:
                            // Servicio no disponible - leer respuesta del health check (impresora no conectada)
                            java.io.InputStream serviceUnavailableStream = conn.getErrorStream();
                            if (serviceUnavailableStream != null) {
                                InputStreamReader suReader = new InputStreamReader(serviceUnavailableStream, "UTF-8");
                                br = new BufferedReader(suReader);
                                stb = new StringBuilder();
                                while ((line = br.readLine()) != null) {
                                    stb.append(line);
                                }
                                
                                if (DEBUG) {
                                    NovusUtils.printLn("RX [(" + status + "): " + result + "]: " + stb.toString());
                                }
                                
                                try {
                                    // Parsear como respuesta normal para que se pueda procesar el estado de la impresora
                                    respuesta = gson.fromJson(stb.toString(), JsonObject.class);
                                } catch (JsonSyntaxException e) {
                                    respuesta = new JsonObject();
                                    respuesta.addProperty("status", "unhealthy");
                                    respuesta.addProperty("message", stb.toString());
                                }
                            } else {
                                respuesta = new JsonObject();
                                respuesta.addProperty("status", "unhealthy");
                                respuesta.addProperty("message", "Servicio no disponible");
                            }
                            break;
                        default:
                            // Validar que getErrorStream() no sea null antes de leer
                            java.io.InputStream errorStream = conn.getErrorStream();
                            if (errorStream != null) {
                                InputStreamReader err = new InputStreamReader(errorStream, "UTF-8");
                            br = new BufferedReader(err);
                            stb = new StringBuilder();
                            while ((line = br.readLine()) != null) {
                                stb.append(line);
                            }
                            NovusUtils.printLn("ERROR RX [(" + status + "): " + stb.toString());

                            try {
                                error = gson.fromJson(stb.toString(), JsonObject.class);
                                error.addProperty("error", true);
                            } catch (JsonSyntaxException e) {
                                error = null;
                                }
                            } else {
                                // ErrorStream es null - crear objeto de error con información disponible
                                NovusUtils.printLn("ERROR RX [(" + status + "): " + result + "] - Sin body de error");
                                error = new JsonObject();
                                error.addProperty("error", true);
                                error.addProperty("status", status);
                                error.addProperty("message", result != null ? result : "Error HTTP " + status);
                            }

                            break;
                    }
                }
            }
        } catch (MalformedURLException mlfexception) {
            NovusUtils.printLn("ERROR: MalformedURLException ClientWS -> " + mlfexception.getMessage());
        } catch (SocketTimeoutException ti) {
            throw new WSException("ERROR: SocketTimeout in " + url);
        } catch (IOException mlfexception) {
            NovusUtils.printLn("ERROR: IOException ClientWS -> " + mlfexception.getMessage());
        } catch (JsonSyntaxException exception) {
            NovusUtils.printLn("ERROR: JsonSyntaxException ClientWS -> " + exception.getMessage());
        } catch (KeyManagementException | NoSuchAlgorithmException exception) {
            NovusUtils.printLn("ERROR: Exception ClientWS ->  " + exception.getMessage());
        }
        
   
        
        setResponse(respuesta);
        return respuesta;
    }

    public JsonObject getResponse() {
        return response;
    }

    public void setResponse(JsonObject response) {
        this.response = response;
    }

    public int getErrorCodigo() {
        return errorCodigo;
    }

    public void setErrorCodigo(int errorCodigo) {
        this.errorCodigo = errorCodigo;
    }

    public String getErrorMensaje() {
        return errorMensaje;
    }

    public void setErrorMensaje(String errorMensaje) {
        this.errorMensaje = errorMensaje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JsonObject getError() {
        return error;
    }

    public void setError(JsonObject error) {
        this.error = error;
    }

    public JsonObject esperaRespuesta() {
        JsonObject res = null;
        start();
        try {
            join();
            res = response;
        } catch (InterruptedException ex) {
            logger.log(Level.WARNING, "Interrupted!", ex);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getLocalizedMessage());
        }
        return res;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

}

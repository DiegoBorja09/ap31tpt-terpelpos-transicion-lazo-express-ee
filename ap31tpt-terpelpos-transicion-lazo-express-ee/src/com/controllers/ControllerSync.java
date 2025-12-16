/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controllers;

import com.bean.IdentificadoresBean;
import com.bean.ModulosBean;
import com.bean.PersonaBean;
import com.bean.TransmisionBean;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.SetupDao;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerSync extends Thread {

    boolean DEBUG = false;
    final int PAUSA = 1;
    long TIEMPO = 0;
    long FULL_TIEMPO = 0;
    int TIEMPO_MAXIMO_PARA_SINCRONIZAR = 30;
    boolean TIENE_DATOS_PARA_SINCRONIZAR = false;

    long ULTIMO_REPORTE;
    static final Logger logger = Logger.getLogger(ControllerSync.class.getName());

    EquipoDao edao = new EquipoDao();

    @Override
    public void run() {
        TIENE_DATOS_PARA_SINCRONIZAR = true;
        ULTIMO_REPORTE = 0;
        while (true) {
            try {
                ArrayList<TransmisionBean> lista = edao.getTransmisiones();
                TIENE_DATOS_PARA_SINCRONIZAR = !lista.isEmpty();

                if (TIENE_DATOS_PARA_SINCRONIZAR || TIEMPO >= TIEMPO_MAXIMO_PARA_SINCRONIZAR) {
                    if (TIENE_DATOS_PARA_SINCRONIZAR || Main.SYNC) {
                        if (DEBUG) {
                            NovusUtils.printLn(getName() + "INICIANDO SINCRONIZANDO POR DEMANDA");
                        }
                        sincronizar(lista);
                    } else {
                        if (TIEMPO >= TIEMPO_MAXIMO_PARA_SINCRONIZAR) {
                            if (DEBUG) {
                                NovusUtils.printLn(getName() + "INICIANDO SINCRONIZANDO POR TIEMPO");
                            }
                            sincronizar(lista);
                        }
                    }
                    if (DEBUG) {
                        NovusUtils.printLn(getName() + "FIN DE LA SINCRONIZANDO");
                    }
                    Main.SYNC = false;
                    TIEMPO = 0;
                }
                pause(1);
            } catch (Exception a) {
                NovusUtils.printLn(getName() + " ControllerSync.Exception ERROR SYNC " + a.getMessage());
                pause(1);
            } catch (DAOException ex) {
                NovusUtils.printLn(getName() + " ControllerSync.DAOException ERROR SYNC " + ex.getMessage());
                Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, ex);
            }
            pause(30);
            TIEMPO++;
        }
    }

    public void sincronizar(ArrayList<TransmisionBean> lista) {
        NovusUtils.printLn(getName() + "CANTIDAD DE PETICIONES :" + lista.size());
        try {
            for (TransmisionBean txBean : lista) {
                String url = txBean.getUrl();
                ClientWSAsync async = new ClientWSAsync("SINCRONIZADION DE DATOS", url, txBean.getMethod(),
                        txBean.getRequest(), Main.DEBUG_REPORTE_EQUIPO);
                async.start();
                try {
                    async.join();
                    int status = async.getStatus();
                    JsonObject response = async.getResponse();
                    if (txBean.getFechaTrasmitido() == null) {
                        txBean.setFechaTrasmitido(new Date());
                    }
                    switch (status) {
                        case 200:
                        case 201:
                            edao.updateTransmision(txBean.getId(), response, 1, txBean.getFechaTrasmitido(),
                                    txBean.getReintentos() + 1, status);
                            break;
                        case 409:
                            JsonObject error = async.getError();
                            edao.updateTransmision(txBean.getId(), error, 1, txBean.getFechaTrasmitido(),
                                    txBean.getReintentos() + 1, status);
                            break;
                        default:
                            response = new JsonObject();
                            response.addProperty("code", async.getErrorCodigo());
                            response.addProperty("error", async.getErrorMensaje());
                            edao.updateTransmision(txBean.getId(), response, 0, txBean.getFechaTrasmitido(),
                                    txBean.getReintentos() + 1, status);
                            break;
                    }
                } catch (InterruptedException e) {
                    NovusUtils.printLn(getName() + " (ControllerSync) Interrupted! ERROR SYNC " + e.getMessage());
                    logger.log(Level.WARNING, "Interrupted!", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                    Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, e);
                }
            }

        } catch (Exception | DAOException e) {
            Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(getName() + " ERROR SYNC " + e.getMessage());
        }
    }

    public void pause(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            NovusUtils.printLn(getName() + " (ControllerSync).pause Interrupted! ERROR SYNC " + e.getMessage());
            logger.log(Level.WARNING, "Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public JsonObject lecturasSurtidor(long surtidor, String host, int timeout) throws Exception {
        JsonObject request = new JsonObject();
        JsonObject response;
        request.addProperty("surtidor", surtidor);
        request.addProperty("turno", 0);

        boolean ENABLE_DEBUG = true;
        boolean IS_ARRAY = false;
        String url = "http://" + host + ":"+Main.puertoTotalizadores+"/api/aperturaTurno";

        TreeMap<String, String> header = new TreeMap<>();
        header.put("Authorization",
                "Bearer -----BEGIN RSA PRIVATE KEY-----MIIEpAIBAAKCAQEAiskm5R0382UD7tEXgTVQHYAYucbYy/beKUndz7Ku2PVUvrKvull35Ql+LpZKah1E7+46M31pxs61Xc4L+fIHn3y0ywwz3uHP/g71vtD6LFy4fTZLirZKfA6WsipUaRoPC6Zye0itE2pvuEXo5uvZdGq+rkwGqQL4SRKRwh0ZX4Xhmtq00NyThPqD7DN+9ZbSkDShm3HkAhhAYPqfvcvPGCiNTEPuEG9Y3cXivkdCBlKhAeTGrvoFkNwKCdd5ZWbLv6LAxuHecu6XHhVMId4NGxCH2XKh2AUv6eJ/gjwkfIbrdO7FdmCe6QC8vWOH9PxXHrWEbFDpCvSHWIvR4qKyZQIDAQABAoIBAQCHXz+uGbr6kVyttIv9vzffHpR/mULcaHc4xNE0B3FfNKWtwPOBjEVTRdgrrvL04Ineknt4v+rOPdBQqGusKHVhDq32pHdv/sj3YjY4IvTzEpntoGk86yRqL3y0Wm+tePqV/YwLTs9rcdV5Y8+SdxjL4lcOAiA4+SmfdRpxwhp+vc+tknfgcs+tSbbDHyDhgQwnsUCDPs5CLgTU9/gqTlUPEezyXZLaWSCttu1cuPUZwRED9Hs2NjXSj1O2HTHAg3Z3TApcnFkG4hVDUBFJFJAFtwpAzrWoSZVZP4s/S4YdkC3hVVIM6bo52jRr0Ci3w3UTuINFuXpNX/O7CYyvvF4BAoGBANpAcpVyMgbutx6g+5Qak49WRfZOaxBH/BwbYuRBYG2PkkANsSoRWRrDUlZY+vGkIXhHp4F4N/7SjFHrpQ/1KvYoN81/A3kZ4c5L1cR7PtTnXAUXNqGlhNALj9jMbCzlhaBFHl87Mz8SQoBCiB/zBYiMYjdGwoqGm8/ESQM8zLWBAoGBAKLKLl565on5uvKUfiPCee1RlBDvGnO4+LM6OF0z0gUSUxhMFklv5zYcT6Vi6f3R6QgPpkG5GNLscNxoHrOIz0Zz496P1ZAgA8DXpCMsKsRD1vu5n9seKienvkqXRP2axK5PEupu4wCuVomiXnlHvLYjY5esT6CouWLOzvbuC1blAoGAPrRXd2Joxx8ck3sy7Jk6HetujFZ5YiMcZsLjharW1oNyRF7qsKhtTkghxtcnufcq+pCzqnnstJSvZfXq5YvNvQ1PAwZj7A4olwmosBussKSMBpZlxsl0QAWiXWpWBgwneSWClV+/2HYZjxoOXAeJZnLW4QS+behAqc++HmUAd4ECgYEAgDdCIkQmhBHfvuRaHYw1QEf6mQPaD79mkrOOZUpFZp0yOXbkLt8meqX9zUOFDNdh9WluB2HkPWzgz5hqZfmhV9o7ZbZf/O5aRm8R5moJHSBZmVZwo8K0bRtfc5yFSEG4G5pIScEgpg6qNilew6NO7R4eeP3MkbuSmFJPDIodAEkCgYA4TZ4h7EgBTAG0+INsTLcVyaLozaiPt9z4XUUbKbYPGaULABoLqBixFiPJgbt2hkf3DNMT42/f744Qxj/tlwzIUx60giiF9iH4obTgJUQlRX4ZW3R7n9FHq+TV/a/eiLYhvcU873zJ7tc+RIoCwsET9HzoJilA/ueNldooEnTvcw==-----END RSA PRIVATE KEY-----");
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "12345");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);

        ClientWSAsync async = new ClientWSAsync("DESCARGA DE EMPLEADOS", url, NovusConstante.POST, request,
                ENABLE_DEBUG, IS_ARRAY, header);
        async.setTimeout(timeout);
        response = async.esperaRespuesta();
        if (response == null) {
            int statusCode = async.getStatus();
            if (statusCode == 406) {
                throw new Exception("Surtidor ocupado");
            } else if (async.getError() != null) {
                response = async.getError();
            }
        }

        return response;

    }

    public void descargaPersonal() {
        JsonObject json = new JsonObject();
        boolean ENABLE_DEBUG = true;
        String url = NovusConstante
                .getServer(NovusConstante.SECURE_END_POINT_PERSONAL + "/" + Main.credencial.getEmpresas_id());
        ClientWSAsync async = new ClientWSAsync("DESCARGA DE EMPLEADOS", url, NovusConstante.GET, json, ENABLE_DEBUG);

        async.start();
        try {
            async.join();
            JsonObject response = async.getResponse();

            JsonArray array = response.getAsJsonArray("data");
            int size = array.size();
            if (InfoViewController.jmensajes != null) {
                InfoViewController.jmensajes
                        .setText("<html><font color=yellow>PERSONAL DESCARGADOS 0/" + size + "</font></html>");
            }

            SetupDao pdao = new SetupDao();
            int insert = 0;

            if (size > 0) {
                try {
                    pdao.limpiaEmpleados();
                    pdao.limpiaEmpleadosRemotos();
                } catch (DAOException a) {
                }
            }
            // sudo date --set "2020-09-14 13:37"
            boolean error = false;

            for (int i = 0; i < size; i++) {
                if (InfoViewController.jmensajes != null) {
                    InfoViewController.jmensajes.setText(
                            "<html><font color=yellow>PERSONAL DESCARGADOS " + insert + "/" + size + "</font></html>");
                }

                try {
                    JsonObject jper = array.get(i).getAsJsonObject();

                    PersonaBean personal = new PersonaBean();
                    personal.setId(jper.get("id").getAsLong());

                    personal.setTipoIdentificacionId(
                            jper.get("tipo_identificacion").getAsJsonObject().get("id").getAsLong());

                    JsonObject jsonid = jper.get("tipo_identificacion").getAsJsonObject().get("descripcion")
                            .getAsJsonObject();

                    personal.setTipoIdentificacionDesc(jsonid.get("label").getAsString());
                    personal.setIdentificacion(jper.get("identificacion").getAsString());
                    personal.setNombre(jper.get("nombres").getAsString());
                    personal.setApellidos(jper.get("apellidos").getAsString());
                    personal.setEstado(jper.get("estado").getAsString());
                    if (!jper.get("pin").isJsonNull()) {
                        personal.setPin(jper.get("pin").getAsInt());
                        personal.setClave(jper.get("clave").getAsString());
                    }

                    if (!jper.get("modulos").isJsonNull()) {
                        JsonArray arrat = jper.get("modulos").getAsJsonArray();
                        personal.setModulos(new ArrayList<>());
                        for (JsonElement obj : arrat) {
                            ModulosBean mod = new ModulosBean();
                            mod.setId(obj.getAsJsonObject().get("id").getAsInt());
                            mod.setDescripcion(obj.getAsJsonObject().get("descripcion").getAsString());
                            mod.setModuloId(obj.getAsJsonObject().get("sub_modulos_id").getAsLong());
                            mod.setAcciones(obj.getAsJsonObject().get("acciones").getAsString());
                            mod.setEstado(obj.getAsJsonObject().get("estado").getAsString());

                            JsonObject atributos = new JsonObject();
                            atributos.addProperty("posicion", obj.getAsJsonObject().get("posicion").getAsString());
                            atributos.addProperty("color", obj.getAsJsonObject().get("color").getAsString());
                            atributos.addProperty("url", obj.getAsJsonObject().get("url").getAsString());

                            mod.setAtributos(atributos);

                            personal.getModulos().add(mod);
                        }
                    }
                    if (!jper.get("identificadores").isJsonNull()) {
                        JsonArray arrat = jper.get("identificadores").getAsJsonArray();
                        personal.setIdentificadores(new ArrayList<>());
                        for (JsonElement obj : arrat) {
                            IdentificadoresBean ident = new IdentificadoresBean();
                            ident.setId(obj.getAsJsonObject().get("id").getAsInt());
                            ident.setIdentificador(obj.getAsJsonObject().get("identificador").getAsString());
                            ident.setEstado(obj.getAsJsonObject().get("estado").getAsString());
                            personal.getIdentificadores().add(ident);
                        }
                    }

                    personal.setEmpresaId(jper.get("empresa").getAsJsonObject().get("id").getAsLong());
                    personal.setEmpresaRazonSocial(
                            jper.get("empresa").getAsJsonObject().get("razon_social").getAsString());

                    personal.setCiudadId(jper.get("ciudad").getAsJsonObject().get("id").getAsLong());
                    personal.setCiudadDesc(jper.get("ciudad").getAsJsonObject().get("descripcion").getAsString());

                    pdao.procesarEmpleadoRegistry(personal, Main.credencial);
                    pdao.procesarEmpleadoRemoto(personal, Main.credencial);
                    insert++;
                    error = false;
                } catch (DAOException | Exception ex) {
                    NovusUtils.printLn(getName() + ex.getMessage());
                    if (InfoViewController.jmensajes != null) {
                        InfoViewController.jmensajes.setText("<html><font color='#FF00000'>PERSONAL DESCARGADOS "
                                + insert + "/" + size + "</font></html>");

                    }
                    error = true;

                }

            }
            if (InfoViewController.jmensajes != null && !error) {
                InfoViewController.jmensajes.setText(
                        "<html><font color='#00FF00'>PERSONAL FINALIZADO " + insert + "/" + size + "</font></html>");
            }

        } catch (InterruptedException e) {
            Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, e);
            Thread.currentThread().interrupt();

        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controllers;

import com.application.useCases.sutidores.SetEstadoMovimientoUseCase;
import com.dao.SurtidorDao;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.TreeMap;

public class ControllerSyncTransmisiones extends Thread {

    private SetEstadoMovimientoUseCase setEstadoMovimientoUseCase;

    @Override
    public void run() {
        while (true) {
            try {
                setEstadoMovimientoUseCase = new SetEstadoMovimientoUseCase();
                String url = "https://ws.sclbox.com:7008/api/movimiento/movimientos-incompletos/"
                        + Main.credencial.getEmpresas_id();

                TreeMap<String, String> header = new TreeMap<>();
                header.put("authorization", "12246655");
                header.put("aplicacion", "LAZO_EXPRESS_MANAGER");
                header.put("uuid", "530d4dc7-4ebb-4bce-a661-a6d218a657ef");
                header.put("fecha", "2020-08-29T11:43:24-05:00");
                header.put("identificadordispositivo", "localhost");
                header.put("versionapp", "1");
                header.put("versioncode", "1");
                header.put("user-agent", "Java");
                header.put("key",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6Ik1JSUNXd0lCQUFLQmdGOTliWGgreFNnanZLNkpjdTRvTS8xcUg1dWZkbzFTR3NBLzk5S2VuRS9aVHczTTJQZlVJUisvUjZmblcwMENWeDl1RlJERXk2UnNCYWQ2UVBISDVQazFYbjJ6N2ZleFRhUFVrckhmSUtBbUV2NURBN2JVZHhWR3ZGZlRQd1NkSE9Ncit1OFJzek00NnBrVGxZYWRDYUxoSWxOL0FxMEZ4WGxMY1M0QlQ5dVBBZ01CQUFFQ2dZQlhsMkpYYVZISXBCOXJxc0UySE1LejdpallROG9uait6aDlNdUs4OXRHazN1a21RZGRMY1RFcmwwNGMweVlzMURmajhiYS91b2VCTWlhKzlXZTgxSGc5aEhieFlrR3FxVmliL1BpbXJEZ1kwdmV1YTJpZVpJaERGN05tNHBVOVVjM002ckdDd3RVZFBNby9QT0Q1WVpNSzJTa1VLOUJHNFZ4UDZhV2FXT01BUUpCQUxISTF1dmZ2c1BwVnF3RmFNZXpjcDd2QWhwU2N5aTk5WFJoUWsrT2VQbmFvTlk2N2RxVGMrYm5RbFd6N2gzN2c0bjZIWEVnaWphME0wTzZ5aGZaaUlFQ1FRQ0pnQlExY1Z1Y0FTQmFud1lXUEgvVmk3WTZsK2FFUlR1UEczUmg0TCt4eWJFNnRZV1BTT2NwUEdUQ2tEalFPb1dzQkFRRlA4TkZtQ2tSY0gzNVF0d1BBa0JTeEswQ2JVMVR5aHRXeE1IdVR5WHF4bDBWUWhOcEFWNzN5cHZqaS9IWWFLZCtkVHJ0Q3I3bjNmeHRCL0t3dExxZGp2c01BdGsxOUpqMVRFM3Y0bytCQWtBRXZ1enpvMFA4TUhYRGF2VzAwc1crd3lHNUNBRW0wNVRvclpQb3hPOFkzZE5HS1huQXBrMkVPTmRwZDd6ZVF3YldGeFZrY1B6MmEvZ082QWtSRDJPMUFrRUFvR3dSSlhkc3VtcFExWTB1SXc1M0wxYWltRnhRSW4yZzIwb1kwRTZHM1duOFZ3VlJ1WGlqS3lHejhHbTNBSDl2cjRZd1dwRlFkN05UeERySUl2VDNjdz09IiwiaWF0IjoxNTk0MDQ5NDQwfQ.QkZV9B7DyzMW52m1XIzxIwOTgfF7NqAkXLfrY6MPJ3g");

                boolean DEBUG = false;
                ClientWSAsync async = new ClientWSAsync("SINCRONIZADION DE DATOS", url, "GET", DEBUG, header);
                try {
                    JsonObject json = async.esperaRespuesta();
                    if (json != null) {
                        JsonArray array = json.get("data").getAsJsonArray();
                        SurtidorDao dao = new SurtidorDao();
                        for (JsonElement jVentas : array) {
                            JsonObject jVenta = jVentas.getAsJsonObject();

                            if (Main.credencial.getEquipos_id() == jVenta.get("identificadorEquipo").getAsLong()) {
                                try {
                                    setEstadoMovimientoUseCase.execute(jVenta.get("identificadorRemoto").getAsLong());
                                    NovusUtils.printLn("MOVIMIENTO PENDIENTE POR RECTIFICAR MOVIMIENTO_ID = "
                                            + jVenta.get("identificadorRemoto").getAsLong());
                                    
                                } catch (Exception a) {
                                    NovusUtils.printLn(a.getMessage());
                                }
                            }
                        }
                    }
                } catch (Exception a) {
                    NovusUtils.printLn(a.getMessage());
                }

            } catch (Exception a) {
                NovusUtils.printLn(a.getMessage());
            }
            pause(300);
        }
    }

    public void pause(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (Exception s) {
        }
    }

    public JsonObject lecturasSurtidor(long surtidor, String host) throws Exception {
        JsonObject request = new JsonObject();
        JsonObject response = null;
        request.addProperty("surtidor", surtidor);
        request.addProperty("turno", 0);

        boolean ENABLE_DEBUG = true;
        boolean IS_ARRAY = true;
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
        response = async.esperaRespuesta();
        if (response == null) {
            int statusCode = async.getStatus();
            if (statusCode == 406) {
                throw new Exception("Surtidor ocupado");
            }
        }

        return response;

    }
}

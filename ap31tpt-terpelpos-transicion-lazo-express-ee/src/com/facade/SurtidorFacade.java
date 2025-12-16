package com.facade;

import com.application.useCases.sutidores.ObtenerInfoSurtidoresEstacionUseCase;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.ControllerSync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.SurtidorDao;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SurtidorFacade {

    private static ObtenerInfoSurtidoresEstacionUseCase obtenerInfoSurtidoresEstacionUseCase = new ObtenerInfoSurtidoresEstacionUseCase();

    public static JsonObject fetchTotalizatorsByPump(Surtidor pump, int timeout) {
        ControllerSync sysnc = new ControllerSync();
        JsonObject response = null;
        try {
            response = sysnc.lecturasSurtidor(pump.getSurtidor(), pump.getHost(), timeout);
        } catch (Exception ex) {
            Logger.getLogger(SurtidorFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response;
    }

    public static ArrayList<Surtidor> getAllPumps() {
        TreeMap<Integer, Surtidor> pumps = null;
        SurtidorDao surtidorDao = new SurtidorDao();
        JsonArray stationPumpsArray = new JsonArray();
        try {
            stationPumpsArray = obtenerInfoSurtidoresEstacionUseCase.execute();
        } catch (Exception ex) {
            Logger.getLogger(ControllerSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (JsonElement element : stationPumpsArray) {
            JsonObject object = element.getAsJsonObject();
            String host = object.get("host").getAsString();
            JsonArray pumpArray = object.getAsJsonArray("surtidores") != null
                    && object.getAsJsonArray("surtidores").isJsonArray() ? object.getAsJsonArray("surtidores")
                            : new JsonArray();
            for (JsonElement elementPump : pumpArray) {
                if (pumps == null) {
                    pumps = new TreeMap<>();
                }
                int pumpNumber = elementPump.getAsJsonPrimitive().getAsInt();
                Surtidor pump = new Surtidor();
                pump.setSurtidor(pumpNumber);
                pump.setHost(host.trim());
                pumps.put(pumpNumber, pump);
            }
        }

        ArrayList<Surtidor> orderedPumps = null;
        if (pumps != null) {
            orderedPumps = new ArrayList<Surtidor>();
            for (Map.Entry<Integer, Surtidor> entry : pumps.entrySet()) {
                Surtidor value = entry.getValue();
                orderedPumps.add(value);
            }
        }

        return orderedPumps;
    }

    public boolean corregirSaltoLectura(long surtidorDetalleId) {
        boolean success = false;
        JsonObject jsonRequest = new JsonObject();
        JsonObject jsonResponse = null;
        jsonRequest.addProperty("tipo", 3);
        jsonRequest.addProperty("subtipo", 1);
        JsonObject jsonPaquete = new JsonObject();
        jsonPaquete.addProperty("configuracionId", surtidorDetalleId);
        jsonRequest.add("paquete", jsonPaquete);
        ClientWSAsync request = new ClientWSAsync("MANGUERAS  DESCUADRADAS",
                NovusConstante.SECURE_CORE_POINT_CORRECCION_SALTOS, NovusConstante.POST, jsonRequest, true, false);
        try {
            jsonResponse = request.esperaRespuesta();
        } catch (Exception ex) {
        } finally {
            if (jsonResponse != null) {
                success = true;
            }
        }

        return success;
    }

    public static JsonObject fetchFixMisFitHoses(ArrayList<JsonObject> assignations, JsonObject detailHose,
            int reasonId) {
        JsonObject response = null;
        ClientWSAsync request = new ClientWSAsync("CORRECCION SALTOS LECTURAS",
                NovusConstante.SECURE_CENTRAL_POINT_CORREGIR_SALTOS_LECTURAS, NovusConstante.POST,
                buidFixMisFitHosesRequestObject(assignations, detailHose, reasonId), true, false);
        try {
            response = request.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

    public static JsonObject buidFixMisFitHosesRequestObject(ArrayList<JsonObject> assignations, JsonObject detailHose,
            int reasonId) {
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("identificadorSalto", detailHose.get("id").getAsInt());
        requestObject.addProperty("motivo", reasonId);
        requestObject.addProperty("responsable", Main.persona != null ? Main.persona.getId() : 1);
        requestObject.add("correccionSaltos",
                reasonId == NovusConstante.COMBO_OPTION_VENTA_FUERA_SISTEMA_ID
                        ? new Gson().toJsonTree(assignations).getAsJsonArray()
                        : new JsonArray());
        return requestObject;
    }

    public static TreeMap<Long, JsonObject> fetchMisfitHoses() throws DAOException {
        SurtidorDao sdao = new SurtidorDao();
        JsonArray misfitHoses = sdao.searchMisfitHoses();
        TreeMap<Long, JsonObject> misfitHosesConverted = null;
        if (misfitHoses == null) {
            return null;
        } else {
            misfitHosesConverted = new TreeMap<>();
        }

        for (JsonElement element : misfitHoses) {
            JsonObject object = element.getAsJsonObject();
            long hose = object.get("manguera").getAsLong();
            double factor = object.get("factor_inventario").getAsDouble();
            double amountAcc = object.get("acumulado_cantidad").getAsDouble();
            double pumpAmountAcc = object.get("acumulado_cantidad_surt").getAsDouble();
            float amountHist = NovusUtils.fixed(NovusUtils.importeInverso(factor, amountAcc), 2);
            float currentAmount = NovusUtils.fixed(NovusUtils.importeInverso(factor, pumpAmountAcc), 2);
            float diffAmount = NovusUtils.fixed(NovusUtils.importeInverso(factor, (pumpAmountAcc - amountAcc)), 2);

            JsonObject objectConverted = object;
            objectConverted.addProperty("hist_cantidad", amountHist);
            objectConverted.addProperty("act_cantidad", currentAmount);
            objectConverted.addProperty("diff_cantidad", diffAmount);

            misfitHosesConverted.put(hose, objectConverted);

        }
        return misfitHosesConverted;
    }

    public static JsonObject fetchFuelInputsByDateRange(String formattedInitialDate, String formattedFinalDate) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync clientWS = new ClientWSAsync("CONSEGUIR REPORTE RECEPCION",
                NovusConstante.SECURE_CETNRAL_POINT_REPORTE_RECEPCION_COMBUSTIBLE,
                NovusConstante.POST,
                buildFuelInputsByDateRangeRequestObject(formattedInitialDate, formattedFinalDate),
                true, false, header);
        try {
            return clientWS.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
    }

    public static JsonObject buildFuelInputsByDateRangeRequestObject(String formattedInitialDate,
            String formattedFinalDate) {
        JsonObject request = new JsonObject();
        request.addProperty("fecha_inicial", formattedInitialDate);
        request.addProperty("fecha_final", formattedFinalDate);
        return request;
    }
}

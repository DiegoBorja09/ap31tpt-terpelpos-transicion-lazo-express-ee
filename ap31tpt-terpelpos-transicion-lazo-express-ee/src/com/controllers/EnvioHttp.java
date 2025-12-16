/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 *
 * @author Devitech
 */
public class EnvioHttp {
    
    
    public JsonObject put(String url) {
        Gson gson = new Gson();
        try {
            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                return  gson.fromJson(respuesta.body(), JsonObject.class);
            } else {
                throw new IOException("Respuesta inesperada del servidor: " + respuesta.statusCode());
            }
        } catch (IOException | InterruptedException ex) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
        return null;
    }
    
    public JsonObject post(String url, JsonObject body) {
        Gson gson = new Gson();
        try {
            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                return  gson.fromJson(respuesta.body(), JsonObject.class);
            } else {
                throw new IOException("Respuesta inesperada del servidor: " + respuesta.statusCode());
            }
        } catch (IOException | InterruptedException ex) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
        return null;
    }
    
    
}

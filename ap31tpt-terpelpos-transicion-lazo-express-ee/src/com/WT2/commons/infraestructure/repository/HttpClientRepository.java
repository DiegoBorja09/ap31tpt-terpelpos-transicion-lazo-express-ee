/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.repository;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.entity.HttpRespuesta;
import com.WT2.commons.domain.entity.Request;
import com.controllers.NovusUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefuel.Main;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.TreeMap;

/**
 *
 * @author USUARIO
 */
public final class HttpClientRepository<T> implements IHttpClientRepository<T> {

    private final ObjectMapper objectMapper;
    private HttpClient httpClient;

    public HttpClientRepository() throws MalformedURLException, IOException {

        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public HttpRespuesta<T> send(String url, Request request, Class<T> responsePrototype) throws JsonProcessingException, URISyntaxException, IOException, InterruptedException {
        byte[] requestBody = new byte[0];

        if (request.getBody() != null) {
            requestBody = objectMapper.writeValueAsBytes(request.getBody());
        }
        NovusUtils.printLn("Request send to  @@@@@ " + url);
        NovusUtils.printLn(objectMapper.writeValueAsString(request.getBody()));

        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder().uri(new URI(url)).header("Content-Type", request.getContent());

        if (requestBody.length > 0) {
            httpBuilder.method(request.getMethod(), HttpRequest.BodyPublishers.ofByteArray(requestBody));
        } else {
            httpBuilder.method(request.getMethod(), HttpRequest.BodyPublishers.noBody());
        }

        TreeMap<String, String> headers = request.getHeaders();
        if (headers != null) {

            for (String llave : headers.keySet()) {
                httpBuilder.header(llave, headers.get(llave));
            }
        }

        HttpRequest httpRequest = httpBuilder.build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println(Main.ANSI_YELLOW + " Servicio " + url + " responde con estado: " + response.statusCode() + Main.ANSI_RESET);

        T respontMapped = objectMapper.readValue(response.body(), responsePrototype);
        HttpRespuesta<T> respuesta = new HttpRespuesta<>();
        respuesta.setCodigo(response.statusCode());
        respuesta.setResponse(respontMapped);
        
        return respuesta;

    }

    @Override
    public void initConfig() {

    }

}

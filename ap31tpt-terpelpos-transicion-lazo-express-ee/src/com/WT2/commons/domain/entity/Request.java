/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.domain.entity;

import java.util.TreeMap;

/**
 *
 * @author USUARIO
 */
public class Request<T> {

    private String method;
    private T body;
    private String content;
    private TreeMap<String, String> headers;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TreeMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(TreeMap<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String llave, String arg) {
        if (this.headers == null) {
            this.headers = new TreeMap<>();
        }
        this.headers.put(llave, arg);
    }
}

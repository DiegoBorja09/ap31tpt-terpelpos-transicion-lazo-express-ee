package com.facade.fidelizacion;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DatePosUtils {
    public static String obtenerFechaActual() {
        ZonedDateTime nowColombia = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return nowColombia.format(formatter); // ✅ Formato UTC, pero hora local
    }



    public static void main(String[] args) {
        System.out.println(obtenerFechaActual());
    }

}

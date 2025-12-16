package com.firefuel.utils;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static final Map<String, ImageIcon> cache = new HashMap<>();

    public static ImageIcon getImage(String path) {
        URL url = ImageCache.class.getResource(path);
        if (url == null) {
            System.err.println("⚠ Imagen no encontrada en ruta: " + path);
            return null;
        }
        return cache.computeIfAbsent(path, p -> new ImageIcon(ImageCache.class.getResource(p)));
    }

}

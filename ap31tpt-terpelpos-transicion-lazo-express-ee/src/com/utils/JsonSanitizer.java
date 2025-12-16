package com.utils;

/**
 * Utility to sanitize JSON strings by escaping ASCII control characters that are
 * invalid in PostgreSQL json input (e.g., 0x00-0x1F except standard escapes).
 */
public final class JsonSanitizer {

    private JsonSanitizer() {}

    /**
     * Escapes characters with code points 0x00..0x1F that are not standard JSON escapes.
     */
    public static String escapeInvalidControlChars(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        StringBuilder sb = new StringBuilder(json.length());
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            // Allow common JSON escape candidates to pass through (they should already be escaped in a proper JSON string)
            if (c >= 0x00 && c <= 0x1F) {
                switch (c) {
                    case '\\':
                    case '"':
                    case '\n':
                    case '\r':
                    case '\t':
                    case '\b':
                    case '\f':
                        sb.append(c);
                        break;
                    default:
                        String hex = Integer.toHexString(c);
                        if (hex.length() == 1) hex = "000" + hex;
                        else if (hex.length() == 2) hex = "00" + hex;
                        else if (hex.length() == 3) hex = "0" + hex;
                        sb.append("\\u").append(hex);
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}




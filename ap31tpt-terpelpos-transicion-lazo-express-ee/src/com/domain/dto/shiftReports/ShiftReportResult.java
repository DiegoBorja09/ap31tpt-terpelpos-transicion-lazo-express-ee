package com.domain.dto.shiftReports;

/**
 * DTO para encapsular el resultado de la impresión de reportes de turno.
 * 
 * Permite retornar tanto el estado de éxito/fallo como el mensaje 
 * específico del error (ej: mensaje de print-ticket sobre impresora).
 * 
 * @version 1.0
 */
public class ShiftReportResult {
    private final boolean success;
    private final String message;
    
    public ShiftReportResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Constructor para resultado exitoso con mensaje por defecto
     */
    public static ShiftReportResult success() {
        return new ShiftReportResult(true, "Reporte impreso correctamente");
    }
    
    /**
     * Constructor para resultado exitoso con mensaje personalizado
     */
    public static ShiftReportResult success(String message) {
        return new ShiftReportResult(true, message);
    }
    
    /**
     * Constructor para resultado fallido
     */
    public static ShiftReportResult failure(String errorMessage) {
        return new ShiftReportResult(false, errorMessage);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
}


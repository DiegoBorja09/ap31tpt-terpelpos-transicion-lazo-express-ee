package com.application.useCases.gopass;

import com.application.ports.in.gopass.ValidarPlacaGoPassPort;

import java.util.logging.Logger;

public class ValidarPlacaGoPassUseCase implements ValidarPlacaGoPassPort {

    private static final Logger LOGGER = Logger.getLogger(ValidarPlacaGoPassUseCase.class.getName());

    @Override
    public ValidarPlacaResult execute(ValidarPlacaCommand command) {
        LOGGER.info("[HEXAGONAL] Validando placa: " + command.getPlaca().getPlaca());

        // Validar entrada
        if (command.getPlaca() == null) {
            return ValidarPlacaResult.invalida("La placa no puede ser nula");
        }

        if (command.getDigitosIngresados() == null || command.getDigitosIngresados().trim().isEmpty()) {
            return ValidarPlacaResult.invalida("Debe ingresar los 3 dígitos de la placa");
        }

        String digitosIngresados = command.getDigitosIngresados().trim();

        if (digitosIngresados.length() != 3 && digitosIngresados.length() != 6) {
            return ValidarPlacaResult.invalida("Debe ingresar 3 o 6 dígitos de la placa");
        }

        // Validar coincidencia
        String placaCompleta = command.getPlaca().getPlaca();
        
        if (placaCompleta == null || placaCompleta.isEmpty()) {
            return ValidarPlacaResult.invalida("La placa no tiene un valor válido");
        }

        // Si ingresó 3 dígitos, validar que coincidan con los últimos 3
        // Si ingresó 6 dígitos (placa completa), validar que coincida toda
        boolean esValida;
        
        if (digitosIngresados.length() == 3) {
            // Validar últimos 3 dígitos
            esValida = placaCompleta.toUpperCase().endsWith(digitosIngresados.toUpperCase());
            
            if (!esValida) {
                LOGGER.warning("Los últimos 3 dígitos no coinciden. Placa: " + placaCompleta + ", Ingresados: " + digitosIngresados);
                return ValidarPlacaResult.invalida("Los dígitos ingresados no coinciden con la placa");
            }
        } else {
            // Validar placa completa
            esValida = placaCompleta.trim().equalsIgnoreCase(digitosIngresados.trim());
            
            if (!esValida) {
                LOGGER.warning("La placa completa no coincide. Esperada: " + placaCompleta + ", Ingresada: " + digitosIngresados);
                return ValidarPlacaResult.invalida("La placa ingresada no coincide");
            }
        }

        LOGGER.info("Placa validada exitosamente");
        return ValidarPlacaResult.valida();
    }
}


package com.application.useCases.gopass;

import com.application.ports.in.gopass.ValidarPlacaGoPassPort;
import com.application.ports.in.gopass.ValidarPlacaGoPassPort.ValidarPlacaCommand;
import com.application.ports.in.gopass.ValidarPlacaGoPassPort.ValidarPlacaResult;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;

/**
 * ✅ EJEMPLO DE TEST UNITARIO
 * Test del caso de uso ValidarPlacaGoPassUseCase
 * NO necesita mocks porque no tiene dependencias externas (lógica pura)
 */
public class ValidarPlacaGoPassUseCaseTest {
    
    private ValidarPlacaGoPassPort useCase;
    
    public void setUp() {
        // ✅ No necesita mocks, es lógica pura
        useCase = new ValidarPlacaGoPassUseCase();
    }
    
    public void deberiaValidarPlacaConTresDigitosCorrectamente() {
        // Given
        setUp();
        PlacaGopass placa = new PlacaGopass();
        placa.setPlaca("ABC123");
        
        ValidarPlacaCommand command = new ValidarPlacaCommand(placa, "123");
        
        // When
        ValidarPlacaResult resultado = useCase.execute(command);
        
        // Then
        assert resultado.isValida() : "La placa debería ser válida";
        assert "Placa validada correctamente".equals(resultado.getMensaje());
        
        System.out.println("✅ Test 1 pasó: Validación con 3 dígitos");
    }
    
    public void deberiaRechazarPlacaConDigitosIncorrectos() {
        // Given
        setUp();
        PlacaGopass placa = new PlacaGopass();
        placa.setPlaca("ABC123");
        
        ValidarPlacaCommand command = new ValidarPlacaCommand(placa, "456"); // ← Incorrectos
        
        // When
        ValidarPlacaResult resultado = useCase.execute(command);
        
        // Then
        assert !resultado.isValida() : "La placa NO debería ser válida";
        assert resultado.getMensaje().contains("no coinciden");
        
        System.out.println("✅ Test 2 pasó: Rechazo de dígitos incorrectos");
    }
    
    public void deberiaValidarPlacaCompletaCorrectamente() {
        // Given
        setUp();
        PlacaGopass placa = new PlacaGopass();
        placa.setPlaca("ABC123");
        
        ValidarPlacaCommand command = new ValidarPlacaCommand(placa, "ABC123");
        
        // When
        ValidarPlacaResult resultado = useCase.execute(command);
        
        // Then
        assert resultado.isValida() : "La placa completa debería ser válida";
        
        System.out.println("✅ Test 3 pasó: Validación con placa completa");
    }
    
    public void deberiaRechazarPlacaNula() {
        // Given
        setUp();
        ValidarPlacaCommand command = new ValidarPlacaCommand(null, "123");
        
        // When
        ValidarPlacaResult resultado = useCase.execute(command);
        
        // Then
        assert !resultado.isValida() : "No debería aceptar placa nula";
        assert resultado.getMensaje().contains("no puede ser nula");
        
        System.out.println("✅ Test 4 pasó: Rechazo de placa nula");
    }
    
    public void deberiaRechazarDigitosVacios() {
        // Given
        setUp();
        PlacaGopass placa = new PlacaGopass();
        placa.setPlaca("ABC123");
        
        ValidarPlacaCommand command = new ValidarPlacaCommand(placa, "");
        
        // When
        ValidarPlacaResult resultado = useCase.execute(command);
        
        // Then
        assert !resultado.isValida() : "No debería aceptar dígitos vacíos";
        assert resultado.getMensaje().contains("Debe ingresar");
        
        System.out.println("✅ Test 5 pasó: Rechazo de dígitos vacíos");
    }
    
    public void deberiaSerCaseInsensitive() {
        // Given
        setUp();
        PlacaGopass placa = new PlacaGopass();
        placa.setPlaca("ABC123");
        
        // Minúsculas
        ValidarPlacaCommand command = new ValidarPlacaCommand(placa, "abc123");
        
        // When
        ValidarPlacaResult resultado = useCase.execute(command);
        
        // Then
        assert resultado.isValida() : "Debería ignorar mayúsculas/minúsculas";
        
        System.out.println("✅ Test 6 pasó: Case insensitive");
    }
    
    /**
     * Ejecuta todos los tests
     */
    public static void main(String[] args) {
        ValidarPlacaGoPassUseCaseTest test = new ValidarPlacaGoPassUseCaseTest();
        
        System.out.println("🧪 Ejecutando tests de ValidarPlacaGoPassUseCase...\n");
        
        try {
            test.deberiaValidarPlacaConTresDigitosCorrectamente();
            test.deberiaRechazarPlacaConDigitosIncorrectos();
            test.deberiaValidarPlacaCompletaCorrectamente();
            test.deberiaRechazarPlacaNula();
            test.deberiaRechazarDigitosVacios();
            test.deberiaSerCaseInsensitive();
            
            System.out.println("\n🎉 Todos los tests pasaron exitosamente!");
            
        } catch (AssertionError e) {
            System.err.println("\n❌ Test falló: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


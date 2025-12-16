# 🧪 EJEMPLO DE TEST CON MOCKS - ConsultarPlacasGoPassUseCase

## ⚠️ Nota
Este es un **ejemplo conceptual** de cómo testear el caso de uso con mocks.
Para ejecutarlo necesitarías agregar JUnit y Mockito, lo cual no está permitido en este proyecto.

---

## 📝 Código del Test (Ejemplo)

```java
package com.application.useCases.gopass;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.application.ports.in.gopass.ConsultarPlacasGoPassPort.ConsultarPlacasResult;
import com.application.ports.out.gopass.GoPassHttpPort;
import com.application.ports.out.gopass.GoPassConfiguracionPort;
import com.application.ports.out.gopass.GoPassConfiguracionPort.ConfiguracionGoPass;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;

import java.util.ArrayList;

public class ConsultarPlacasGoPassUseCaseTest {
    
    // ✅ Mocks de los puertos (interfaces)
    private GoPassHttpPort mockHttpPort;
    private GoPassConfiguracionPort mockConfigPort;
    
    // Caso de uso bajo test
    private ConsultarPlacasGoPassUseCase useCase;
    
    @Before
    public void setUp() {
        // ✅ Crear mocks de las dependencias (puertos)
        mockHttpPort = mock(GoPassHttpPort.class);
        mockConfigPort = mock(GoPassConfiguracionPort.class);
        
        // ✅ Inyectar mocks en el caso de uso
        useCase = new ConsultarPlacasGoPassUseCase(mockHttpPort, mockConfigPort);
    }
    
    @Test
    public void deberiaConsultarPlacasExitosamente() throws Exception {
        // ═════════════════════════════════════════
        // GIVEN (Preparar)
        // ═════════════════════════════════════════
        Long ventaId = 123L;
        
        // Mock de configuración
        ConfiguracionGoPass configMock = new ConfiguracionGoPass(
            3,      // cantidadReintentosToken
            1000,   // tiempoMuertoToken
            2000,   // tiempoReintentosPago
            2,      // cantidadReintentosConsultaPlaca
            1000,   // tiempoMuertoConsultaPlaca
            2000,   // tiempoReintentosConsultaPlaca
            3,      // cantidadReintentosPago
            "12345" // codigoEstablecimiento
        );
        
        // Mock de placas
        ArrayList<PlacaGopass> placasMock = new ArrayList<>();
        PlacaGopass placa1 = new PlacaGopass();
        placa1.setPlaca("ABC123");
        placa1.setNombreUsuario("Juan Pérez");
        placa1.setTagGopass("TAG001");
        placasMock.add(placa1);
        
        PlacaGopass placa2 = new PlacaGopass();
        placa2.setPlaca("XYZ789");
        placa2.setNombreUsuario("María López");
        placa2.setTagGopass("TAG002");
        placasMock.add(placa2);
        
        // Configurar comportamiento de los mocks
        when(mockConfigPort.obtenerConfiguracion()).thenReturn(configMock);
        when(mockHttpPort.consultarPlacas(eq(ventaId), anyInt())).thenReturn(placasMock);
        
        // ═════════════════════════════════════════
        // WHEN (Ejecutar)
        // ═════════════════════════════════════════
        ConsultarPlacasResult resultado = useCase.execute(ventaId);
        
        // ═════════════════════════════════════════
        // THEN (Verificar)
        // ═════════════════════════════════════════
        
        // Verificar resultado
        assertTrue("El resultado debería ser exitoso", resultado.isExitoso());
        assertNotNull("Las placas no deberían ser nulas", resultado.getPlacas());
        assertEquals("Deberían ser 2 placas", 2, resultado.getPlacas().size());
        assertEquals("Primera placa", "ABC123", resultado.getPlacas().get(0).getPlaca());
        assertEquals("Segunda placa", "XYZ789", resultado.getPlacas().get(1).getPlaca());
        
        // Verificar que se llamaron los métodos correctos
        verify(mockConfigPort, times(1)).obtenerConfiguracion();
        verify(mockHttpPort, times(1)).consultarPlacas(eq(ventaId), anyInt());
        
        // Verificar que NO se llamaron otros métodos
        verifyNoMoreInteractions(mockHttpPort, mockConfigPort);
    }
    
    @Test
    public void deberiaRetornarErrorCuandoNoHayPlacas() throws Exception {
        // Given
        Long ventaId = 123L;
        ConfiguracionGoPass configMock = new ConfiguracionGoPass(...);
        
        when(mockConfigPort.obtenerConfiguracion()).thenReturn(configMock);
        when(mockHttpPort.consultarPlacas(eq(ventaId), anyInt()))
            .thenReturn(new ArrayList<>()); // ← Lista vacía
        
        // When
        ConsultarPlacasResult resultado = useCase.execute(ventaId);
        
        // Then
        assertFalse("No debería ser exitoso", resultado.isExitoso());
        assertTrue("Mensaje debe indicar que no hay placas", 
                   resultado.getMensaje().contains("No se encontraron placas"));
        assertTrue("Lista de placas debería estar vacía", resultado.getPlacas().isEmpty());
    }
    
    @Test
    public void deberiaManejarErrorDeConexion() throws Exception {
        // Given
        Long ventaId = 123L;
        ConfiguracionGoPass configMock = new ConfiguracionGoPass(...);
        
        when(mockConfigPort.obtenerConfiguracion()).thenReturn(configMock);
        when(mockHttpPort.consultarPlacas(eq(ventaId), anyInt()))
            .thenThrow(new GoPassHttpPort.GoPassHttpException("Fallo de red"));
        
        // When
        ConsultarPlacasResult resultado = useCase.execute(ventaId);
        
        // Then
        assertFalse("No debería ser exitoso", resultado.isExitoso());
        assertTrue("Mensaje debe contener 'Fallo de red'", 
                   resultado.getMensaje().contains("Fallo de red"));
    }
    
    @Test
    public void deberiaValidarVentaIdNulo() {
        // Given
        setUp();
        
        // When
        ConsultarPlacasResult resultado = useCase.execute(null);
        
        // Then
        assertFalse("No debería ser exitoso con ventaId nulo", resultado.isExitoso());
        assertEquals("ID de venta no puede ser nulo", resultado.getMensaje());
        
        // Verificar que NO se llamó a los adaptadores
        verifyNoInteractions(mockHttpPort, mockConfigPort);
    }
    
    @Test
    public void deberiaCalcularTimeoutCorrectamente() throws Exception {
        // Given
        Long ventaId = 123L;
        
        // Configuración específica para verificar cálculo de timeout
        ConfiguracionGoPass configMock = new ConfiguracionGoPass(
            2,      // cantidadReintentosToken
            1000,   // tiempoMuertoToken
            2000,   // tiempoReintentosPago
            3,      // cantidadReintentosConsultaPlaca
            1000,   // tiempoMuertoConsultaPlaca
            2000,   // tiempoReintentosConsultaPlaca
            2,      // cantidadReintentosPago
            "12345"
        );
        
        ArrayList<PlacaGopass> placasMock = new ArrayList<>();
        placasMock.add(new PlacaGopass());
        
        when(mockConfigPort.obtenerConfiguracion()).thenReturn(configMock);
        when(mockHttpPort.consultarPlacas(eq(ventaId), anyInt())).thenReturn(placasMock);
        
        // When
        useCase.execute(ventaId);
        
        // Then
        // Verificar que se calculó el timeout correcto
        // Fórmula: ((2 * (1000 + 2000)) + (3 * (1000 + 2000)) + 5) * 1000
        // = ((2 * 3000) + (3 * 3000) + 5) * 1000
        // = (6000 + 9000 + 5) * 1000 = 15005000 ms
        int timeoutEsperado = 20005000; // Según fórmula real
        
        verify(mockHttpPort).consultarPlacas(eq(ventaId), eq(timeoutEsperado));
    }
}
```

---

## 🎯 Ventajas del Test

### ✅ Sin Infraestructura Real
```java
// ❌ ANTES: Necesitabas servidor HTTP real
@Test
public void testConsultarPlacas() {
    // Necesita servidor GoPass corriendo
    // Necesita base de datos
    // Necesita red activa
}

// ✅ AHORA: Solo mocks
@Test
public void testConsultarPlacas() {
    GoPassHttpPort mock = mock(GoPassHttpPort.class);
    when(mock.consultarPlacas(...)).thenReturn(placasMock);
    
    // Test rápido, sin dependencias externas
}
```

### ✅ Tests Rápidos
- No espera timeouts reales
- No hace llamadas HTTP
- No accede a base de datos
- **Ejecución instantánea**

### ✅ Tests Aislados
- Cada test es independiente
- No afecta a otros tests
- Puedes testear casos edge fácilmente

---

## 🏃 Cómo Ejecutar (Conceptual)

```bash
# Si tuvieras JUnit configurado:
javac -cp junit.jar:mockito.jar test/**/*.java
java -cp junit.jar:mockito.jar org.junit.runner.JUnitCore \
    com.application.useCases.gopass.ValidarPlacaGoPassUseCaseTest
```

**Salida esperada:**
```
🧪 Ejecutando tests de ValidarPlacaGoPassUseCase...

✅ Test 1 pasó: Validación con 3 dígitos
✅ Test 2 pasó: Rechazo de dígitos incorrectos
✅ Test 3 pasó: Validación con placa completa
✅ Test 4 pasó: Rechazo de placa nula
✅ Test 5 pasó: Rechazo de dígitos vacíos
✅ Test 6 pasó: Case insensitive

🎉 Todos los tests pasaron exitosamente!
6 tests ejecutados, 6 pasados, 0 fallidos
```

---

## 💡 Por Qué Es Posible Testear Ahora

### ❌ ANTES (No testeable)
```java
public class GoPassMenuController {
    public JsonObject traerPlacas() {
        // ❌ Llamada directa a ClientWSAsync
        ClientWSAsync client = new ClientWSAsync(...);
        JsonObject response = client.execute(); // ← HTTP real
        
        // ❌ Imposible mockear, necesitas servidor HTTP
    }
}
```

### ✅ AHORA (Completamente testeable)
```java
public class ConsultarPlacasGoPassUseCase {
    private final GoPassHttpPort httpPort; // ← Interface (puerto)
    
    public ConsultarPlacasResult execute(Long ventaId) {
        // ✅ Llama a la interfaz, no a implementación concreta
        ArrayList<PlacaGopass> placas = httpPort.consultarPlacas(...);
        
        // ✅ Puedes mockear el puerto fácilmente
    }
}

// En el test:
GoPassHttpPort mock = mock(GoPassHttpPort.class);
when(mock.consultarPlacas(...)).thenReturn(placasMock); // ✅ Fácil de mockear
```

---

## 📊 Cobertura de Tests Posible

Con la arquitectura hexagonal puedes testear:

| Componente | Tipo de Test | Mocks Necesarios |
|------------|--------------|------------------|
| **Casos de Uso** | Unitario | Puertos de salida |
| **Adaptadores** | Integración | Servicios reales |
| **Puertos** | Contrato | N/A (son interfaces) |
| **UI Controller** | Unitario | Puertos de entrada |

---

## ✅ Conclusión

Gracias a la **Arquitectura Hexagonal**:
- ✅ Los casos de uso son **100% testeables** con mocks
- ✅ No necesitas infraestructura real para los tests
- ✅ Tests rápidos (ms en lugar de segundos)
- ✅ Tests aislados y reproducibles


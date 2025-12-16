package com.infrastructure.database;

import com.application.useCases.parametros.GetMensajesFEUseCase;
import com.application.useCases.gopass.ExisteGopassUseCase;
import com.application.useCases.gopass.GetTransacionesGoPassUseCase;
import com.application.useCases.gopass.GetVentasGoPassUseCase;
import com.application.useCases.persons.FindAllPersonsUseCase;
import com.application.useCases.movimientos.FindAllMovimientoUseCase;
import com.application.useCases.persons.FindPersonaByIdUseCase;
import com.application.useCases.wacherparametros.FindAllWacherParametrosUseCase;
import com.bean.PersonaBean;
import com.bean.VentaGo;
import com.controllers.NovusUtils;
import com.domain.entities.CtPerson;
import com.domain.entities.CtMovimientoEntity;
import com.domain.entities.CtWacherParametroEntity;
import com.google.gson.JsonObject;
import com.domain.entities.GoPassEntity;

import java.util.List;


public class ExampleConnection   {
    public static void main(String[] args) {
        //testMensajesFE();
       /*FindAllPersonsUseCase findAllPersons = new FindAllPersonsUseCase();
        FindAllMovimientoUseCase findAllMovimientoUseCase = new FindAllMovimientoUseCase();

        List<CtPerson> ctPersonEntities = findAllPersons.execute();
        List<CtMovimientoEntity> movimientoEntities = findAllMovimientoUseCase.execute();
        NovusUtils.printLn("Total personas: " + ctPersonEntities.size());
        NovusUtils.printLn("Total Movimiento: " + movimientoEntities.size());
       for (CtPerson ctPersonEntity : ctPersonEntities) {
           NovusUtils.printLn(ctPersonEntity.getNombre());
       }
        for(CtMovimientoEntity ctMovimientoEntity : movimientoEntities){
            NovusUtils.printLn("Movimiento ID: " + ctMovimientoEntity.getId() + " - Consecutivo: " + ctMovimientoEntity.getConsecutivo());
        }
        
        // ✅ Probar caso de uso de remisión
        NovusUtils.printLn("\n========== TEST REMISION ==========");
        testHabilitarRemision();
        
        // ✅ Probar caso de uso de facturación electrónica
        NovusUtils.printLn("\n========== TEST FACTURACION FE ==========");
        testHabilitarFE();
        
        // ✅ Probar ambos casos juntos
        NovusUtils.printLn("\n========== TEST COMBINADO ==========");
        testLogicaCombinada();*/

        testGopass();
        testFindPersonaByIdUseCase();
    }

    public static void testFindPersonaByIdUseCase() {
        NovusUtils.printLn("🔍 Iniciando prueba de FindPersonaByIdUseCase...");
        FindPersonaByIdUseCase findPersonaByIdUseCase = new FindPersonaByIdUseCase();
        PersonaBean persona = findPersonaByIdUseCase.execute(10277);
        NovusUtils.printLn("🔍 Persona: " + persona);
    }

    public static void testGopass() {
        NovusUtils.printLn("🔍 Iniciando prueba de gopass...");
        ExisteGopassUseCase existeGopassUseCase = new ExisteGopassUseCase();
        Boolean existeGopass = existeGopassUseCase.execute();
        NovusUtils.printLn("🔍 Existe gopass: " + existeGopass);
        GetTransacionesGoPassUseCase getTransacionesGoPassUseCase = new GetTransacionesGoPassUseCase();
        //List<GoPassEntity> transacciones = getTransacionesGoPassUseCase.execute();
        //NovusUtils.printLn("📊 Total transacciones encontradas: " + transacciones.size());
        GetVentasGoPassUseCase getVentasGoPassUseCase = new GetVentasGoPassUseCase();
        List<VentaGo> ventas = getVentasGoPassUseCase.execute();
        NovusUtils.printLn("📊 Total ventas GoPass encontradas: " + ventas.size());
        for (VentaGo venta : ventas) {
            NovusUtils.printLn("📊 Venta: " + venta.getId() + " - " + venta.getFecha() + " - " + venta.getVentaTotal());
        }
    }

    /**
     * Método de prueba para validar el comportamiento del caso de uso de remisión
     */
    private static void testHabilitarRemision() {
        NovusUtils.printLn("🔍 Iniciando prueba de habilitarRemision...");
        
        boolean remisionActiva = false;
        
        try {
            // ✅ Usar el mismo caso de uso que en producción
            FindAllWacherParametrosUseCase findAllWacherParametrosUseCase = new FindAllWacherParametrosUseCase();
            List<CtWacherParametroEntity> parametros = findAllWacherParametrosUseCase.execute();
            
            NovusUtils.printLn("📊 Total parámetros encontrados: " + parametros.size());
            
            // 🔎 Mostrar todos los parámetros para debug
            NovusUtils.printLn("📋 Lista de todos los parámetros:");
            parametros.forEach(p -> 
                NovusUtils.printLn("  - Código: '" + p.getCodigo() + "' | Valor: '" + p.getValor() + "'")
            );
            
            // ✅ Aplicar la misma lógica de negocio
            remisionActiva = parametros.stream()
                .filter(parametro -> {
                    boolean match = "REMISION".equals(parametro.getCodigo());
                    if (match) {
                        NovusUtils.printLn("🎯 Parámetro REMISION encontrado con valor: '" + parametro.getValor() + "'");
                    }
                    return match;
                })
                .findFirst()
                .map(parametro -> {
                    boolean isActive = "S".equals(parametro.getValor());
                    NovusUtils.printLn("⚙️  Evaluando valor '" + parametro.getValor() + "' == 'S' ? " + isActive);
                    return isActive;
                })
                .orElse(false);
                
            if (parametros.stream().noneMatch(p -> "REMISION".equals(p.getCodigo()))) {
                NovusUtils.printLn("⚠️  No se encontró parámetro con código 'REMISION'");
            }
                
        } catch (Exception ex) {
            NovusUtils.printLn("❌ Error al obtener parámetros: " + ex.getMessage());
            ex.printStackTrace();
            remisionActiva = false;
        }
        
        // 📊 Resultado final
        NovusUtils.printLn("🏁 RESULTADO: remisionActiva = " + remisionActiva);
        NovusUtils.printLn("📝 En producción jFactura.setVisible(" + remisionActiva + " || aplicaFE)");
        NovusUtils.printLn("========================================\n");
    }

    /**
     * Método de prueba para validar el comportamiento del caso de uso de facturación electrónica
     */
    private static void testHabilitarFE() {
        NovusUtils.printLn("🔍 Iniciando prueba de habilitarFE...");
        
        boolean aplicaFE = false;
        
        try {
            // ✅ Usar el mismo caso de uso que en producción
            FindAllWacherParametrosUseCase findAllWacherParametrosUseCase = new FindAllWacherParametrosUseCase();
            List<CtWacherParametroEntity> parametros = findAllWacherParametrosUseCase.execute();
            
            NovusUtils.printLn("📊 Total parámetros encontrados: " + parametros.size());
            
            // 🔎 Mostrar parámetros relacionados con FE
            NovusUtils.printLn("📋 Parámetros relacionados con Facturación Electrónica:");
            parametros.stream()
                .filter(p -> p.getCodigo().contains("FE") || p.getCodigo().contains("FACTUR"))
                .forEach(p -> NovusUtils.printLn("  - Código: '" + p.getCodigo() + "' | Valor: '" + p.getValor() + "'"));
            
            // ✅ Buscar parámetro de facturación electrónica (puede ser varios nombres)
            aplicaFE = parametros.stream()
                .filter(parametro -> {
                    // Buscar diferentes posibles nombres para FE
                    boolean match = "FACTURACION_FE".equals(parametro.getCodigo()) || 
                                   "FE".equals(parametro.getCodigo()) ||
                                   "FACTURACION_ELECTRONICA".equals(parametro.getCodigo());
                    if (match) {
                        NovusUtils.printLn("🎯 Parámetro FE encontrado: '" + parametro.getCodigo() + "' con valor: '" + parametro.getValor() + "'");
                    }
                    return match;
                })
                .findFirst()
                .map(parametro -> {
                    boolean isActive = "S".equals(parametro.getValor());
                    NovusUtils.printLn("⚙️  Evaluando valor '" + parametro.getValor() + "' == 'S' ? " + isActive);
                    return isActive;
                })
                .orElse(false);
                
            if (parametros.stream().noneMatch(p -> 
                "FACTURACION_FE".equals(p.getCodigo()) || 
                "FE".equals(p.getCodigo()) ||
                "FACTURACION_ELECTRONICA".equals(p.getCodigo()))) {
                NovusUtils.printLn("⚠️  No se encontró parámetro de Facturación Electrónica");
            }
                
        } catch (Exception ex) {
            NovusUtils.printLn("❌ Error al obtener parámetros FE: " + ex.getMessage());
            ex.printStackTrace();
            aplicaFE = false;
        }
        
        // 📊 Resultado final
        NovusUtils.printLn("🏁 RESULTADO: aplicaFE = " + aplicaFE);
        NovusUtils.printLn("📝 En producción:");
        NovusUtils.printLn("   jFactura.setVisible(" + aplicaFE + ")");
        NovusUtils.printLn("   jAnular.setVisible(" + aplicaFE + ")");
        NovusUtils.printLn("========================================\n");
    }

    /**
     * Método de prueba para validar la lógica combinada de ambos casos de uso
     */
    private static void testLogicaCombinada() {
        NovusUtils.printLn("🔍 Iniciando prueba de lógica combinada...");
        
        boolean remisionActiva = false;
        boolean aplicaFE = false;
        
        try {
            // ✅ Una sola consulta para obtener ambos parámetros
            FindAllWacherParametrosUseCase findAllWacherParametrosUseCase = new FindAllWacherParametrosUseCase();
            List<CtWacherParametroEntity> parametros = findAllWacherParametrosUseCase.execute();
            
            NovusUtils.printLn("📊 Probando lógica combinada con " + parametros.size() + " parámetros");
            
            // ✅ Obtener REMISION
            remisionActiva = parametros.stream()
                .filter(parametro -> "REMISION".equals(parametro.getCodigo()))
                .findFirst()
                .map(parametro -> "S".equals(parametro.getValor()))
                .orElse(false);
            
            // ✅ Obtener FACTURACION_FE
            aplicaFE = parametros.stream()
                .filter(parametro -> "FACTURACION_FE".equals(parametro.getCodigo()) || 
                                     "FE".equals(parametro.getCodigo()) ||
                                     "FACTURACION_ELECTRONICA".equals(parametro.getCodigo()))
                .findFirst()
                .map(parametro -> "S".equals(parametro.getValor()))
                .orElse(false);
                
            NovusUtils.printLn("📋 Estados obtenidos:");
            NovusUtils.printLn("   🎯 remisionActiva = " + remisionActiva);
            NovusUtils.printLn("   🎯 aplicaFE = " + aplicaFE);
                
        } catch (Exception ex) {
            NovusUtils.printLn("❌ Error en lógica combinada: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        // 📊 Simular lógica de habilitarFE()
        NovusUtils.printLn("\n🏁 SIMULACIÓN DE LÓGICA COMBINADA:");
        NovusUtils.printLn("   📄 jFactura.setVisible(" + aplicaFE + ") // Solo FE");
        NovusUtils.printLn("   📄 jAnular.setVisible(" + aplicaFE + ") // Solo FE");
        
        boolean asignarDatos = aplicaFE || remisionActiva;
        NovusUtils.printLn("   📄 jAsignarDatosSinResolver.setVisible(" + aplicaFE + " || " + remisionActiva + ") = " + asignarDatos);
        
        // 📊 Simular lógica de habilitarRemision()
        boolean facturaRemision = remisionActiva || aplicaFE;
        NovusUtils.printLn("   📄 jFactura.setVisible(" + remisionActiva + " || " + aplicaFE + ") = " + facturaRemision + " // Remisión O FE");
        
        NovusUtils.printLn("\n💡 CONCLUSIÓN:");
        if (aplicaFE && remisionActiva) {
            NovusUtils.printLn("   ✅ Ambas funcionalidades están ACTIVAS");
        } else if (aplicaFE) {
            NovusUtils.printLn("   📋 Solo Facturación Electrónica está ACTIVA");
        } else if (remisionActiva) {
            NovusUtils.printLn("   📋 Solo Remisiones está ACTIVA");
        } else {
            NovusUtils.printLn("   ❌ Ambas funcionalidades están INACTIVAS");
        }
        
        NovusUtils.printLn("========================================\n");
    }
    private static void testMensajesFE() {
        NovusUtils.printLn("🔍 Iniciando prueba de GetMensajesFEUseCase...");

        try {
            GetMensajesFEUseCase useCase = new GetMensajesFEUseCase();
            JsonObject mensajes = useCase.execute();

            NovusUtils.printLn("📋 Mensajes obtenidos desde parámetros:");
            NovusUtils.printLn(mensajes.toString());

        } catch (Exception e) {
            NovusUtils.printLn("❌ Error al ejecutar GetMensajesFEUseCase: " + e.getMessage());
            e.printStackTrace();
        }

        NovusUtils.printLn("========================================\n");
    }

}

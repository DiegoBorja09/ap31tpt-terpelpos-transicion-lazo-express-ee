package com.firefuel;

import com.application.useCases.consecutivos.ObtenerConsecutivoUseCase;
import com.bean.ConsecutivoBean;
import com.bean.ImpuestosBean;
import com.bean.JornadaBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.bean.Surtidor;
import com.bean.VentasBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.PrinterFacade;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author robert
 */
public class ImprimirCierreView extends javax.swing.JDialog {

    boolean respuesta;
    PersonaBean promotor;
    ArrayList<PersonaBean> personasEnJornada;
    ArrayList<JornadaBean> lista;
    public boolean principal;
    InfoViewController parent;
    long jornada;
    ObtenerConsecutivoUseCase obtenerConsecutivoUseCase;

    public ImprimirCierreView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        this.lista = new ArrayList<>();
        this.parent = parent;
        this.obtenerConsecutivoUseCase = new ObtenerConsecutivoUseCase(false, "CAN");
        initComponents();
    }

    public void setPersonaJornada(PersonaBean promotor, ArrayList<PersonaBean> personasEnJornada, long jornada) {
        this.promotor = promotor;
        this.personasEnJornada = personasEnJornada;
        this.jornada = jornada;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel2.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel2.setText("REIMPRIMIR");
        jLabel2.setToolTipText("");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel2);
        jLabel2.setBounds(300, 255, 200, 50);

        jLabel4.setFont(new java.awt.Font("Juicebox", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel4.setText("CERRAR");
        jLabel4.setToolTipText("");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        getContentPane().add(jLabel4);
        jLabel4.setBounds(100, 250, 190, 60);

        jLabel3.setFont(new java.awt.Font("Juicebox", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(186, 12, 47));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<html> <center> ¿Desea remprimir  el <br/>cierre actual? </center> </html>");
        jLabel3.setToolTipText("");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabel3);
        jLabel3.setBounds(100, 50, 390, 170);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jPanel1.setOpaque(false);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(90, 40, 410, 190);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(186, 12, 47), 8));
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 580, 360);

        setSize(new java.awt.Dimension(585, 358));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MousePressed
        // jLabel2.setIcon(new
        // javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/btn_success2.png")));
        // // NOI18N
    }// GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel2MouseReleased
        // jLabel2.setIcon(new
        // javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/btn_success.png")));
        // // NOI18N
        imprimirCierre(principal);

    }// GEN-LAST:event_jLabel2MouseReleased

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed
    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased
        cierre(false);
    }// GEN-LAST:event_jLabel4MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    public void imprimirCierre(boolean principal) {
        try {
            NovusUtils.printLn("========================================");
            NovusUtils.printLn("[FIN TURNO] Iniciando proceso de impresión");
            NovusUtils.printLn("========================================");
            NovusUtils.printLn("[FIN TURNO] Solicitando datos del turno (principal=" + principal + ")...");
            
            JornadaBean turnoCerrado = solicitarTurno(principal);
            if (turnoCerrado != null) {
                NovusUtils.printLn("[FIN TURNO] ✓ Datos del turno obtenidos exitosamente");
                
                // Usar el nuevo servicio independiente de fin de turno
                // Convertir long a int para posId (con validación) - debe ser final para lambda
                int posIdTemp = 1; // Default
                if (Main.credencial != null && Main.credencial.getEquipos_id() != null) {
                    long equiposId = Main.credencial.getEquipos_id();
                    if (equiposId > 0 && equiposId <= Integer.MAX_VALUE) {
                        posIdTemp = (int) equiposId;
                    }
                    NovusUtils.printLn("[FIN TURNO] Equipos ID obtenido: " + equiposId + " -> POS ID: " + posIdTemp);
                } else {
                    NovusUtils.printLn("[FIN TURNO] WARNING: Main.credencial es null o no tiene equipos_id, usando POS ID por defecto: " + posIdTemp);
                }
                final int posId = posIdTemp; // Variable final para usar en lambda
                
                // Usar grupo_jornada como identificador_jornada (el servicio Python lo resolverá)
                final long identificadorJornada = turnoCerrado.getGrupoJornada();
                final long promotorId = promotor.getId();
                
                NovusUtils.printLn("[FIN TURNO] Parámetros de impresión:");
                NovusUtils.printLn("  - Identificador Jornada (grupo_jornada): " + identificadorJornada);
                NovusUtils.printLn("  - Identificador Promotor: " + promotorId);
                NovusUtils.printLn("  - POS ID: " + posId);
                NovusUtils.printLn("  - Nombre Promotor: " + (promotor != null ? promotor.getNombre() + " " + (promotor.getApellidos() != null ? promotor.getApellidos() : "") : "N/A"));
                NovusUtils.printLn("  - Fecha Inicio: " + (turnoCerrado.getFechaInicial() != null ? turnoCerrado.getFechaInicial().toString() : "N/A"));
                NovusUtils.printLn("  - Fecha Fin: " + (turnoCerrado.getFechaFinal() != null ? turnoCerrado.getFechaFinal().toString() : "N/A"));
                
                // Ejecutar impresión en thread separado para no bloquear la UI
                NovusUtils.printLn("[FIN TURNO] Creando thread para impresión asíncrona...");
                new Thread(() -> {
                    try {
                        NovusUtils.printLn("[FIN TURNO] Thread iniciado, creando Use Case...");
                        com.application.useCases.shiftReports.PrintFinTurnoRemoteUseCase useCase = 
                            new com.application.useCases.shiftReports.PrintFinTurnoRemoteUseCase(
                                identificadorJornada,
                                promotorId,
                                posId
                            );
                        
                        NovusUtils.printLn("[FIN TURNO] Ejecutando Use Case...");
                        long startTime = System.currentTimeMillis();
                        com.domain.dto.shiftReports.ShiftReportResult result = useCase.execute(null);
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        
                        NovusUtils.printLn("[FIN TURNO] Use Case completado en " + duration + " ms");
                        
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            if (result.isSuccess()) {
                                NovusUtils.printLn("[FIN TURNO] ✓ Ticket impreso exitosamente");
                                NovusUtils.printLn("[FIN TURNO] Mensaje: " + result.getMessage());
                                NovusUtils.printLn("========================================");
                                dispose();
                            } else {
                                NovusUtils.printLn("[FIN TURNO] ✗ Error al imprimir: " + result.getMessage());
                                NovusUtils.printLn("========================================");
                                // No cerrar en caso de error, permitir reintentar
                            }
                        });
                    } catch (Exception e) {
                        NovusUtils.printLn("[FIN TURNO] ✗ ERROR INESPERADO: " + e.getMessage());
                        NovusUtils.printLn("[FIN TURNO] Tipo de error: " + e.getClass().getName());
                        e.printStackTrace();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            NovusUtils.printLn("========================================");
                            dispose();
                        });
                    }
                }).start();
                NovusUtils.printLn("[FIN TURNO] Thread de impresión iniciado, continuando con flujo normal...");

            } else {
                NovusUtils.printLn("[FIN TURNO] ✗ ERROR: No se pudo obtener datos del turno");
                NovusUtils.printLn("[FIN TURNO] turnoCerrado es NULL - Backend no devolvió datos");
                System.err.println("turnoCerrado es NULL - Backend no devolvió datos");
                dispose();
            }
        } catch (Exception ex) {
            NovusUtils.printLn("[FIN TURNO] ✗ ERROR EXCEPCIÓN: " + ex.getMessage());
            NovusUtils.printLn("[FIN TURNO] Tipo de error: " + ex.getClass().getName());
            Logger.getLogger(ImprimirCierreView.class.getName()).log(Level.SEVERE, null, ex);
            dispose();
        }
    }

    public void imprimirCierre(boolean principal, boolean logview) {
        try {
            NovusUtils.printLn("========================================");
            NovusUtils.printLn("[FIN TURNO] Iniciando proceso de impresión (logview=" + logview + ")");
            NovusUtils.printLn("========================================");
            NovusUtils.printLn("[FIN TURNO] Solicitando datos del turno (principal=" + principal + ")...");
            
            JornadaBean turnoCerrado = solicitarTurno(principal);
            if (turnoCerrado != null) {
                NovusUtils.printLn("[FIN TURNO] ✓ Datos del turno obtenidos exitosamente");
                
                // Usar el nuevo servicio independiente de fin de turno
                // Convertir long a int para posId (con validación) - debe ser final para lambda
                int posIdTemp = 1; // Default
                if (Main.credencial != null && Main.credencial.getEquipos_id() != null) {
                    long equiposId = Main.credencial.getEquipos_id();
                    if (equiposId > 0 && equiposId <= Integer.MAX_VALUE) {
                        posIdTemp = (int) equiposId;
                    }
                    NovusUtils.printLn("[FIN TURNO] Equipos ID obtenido: " + equiposId + " -> POS ID: " + posIdTemp);
                } else {
                    NovusUtils.printLn("[FIN TURNO] WARNING: Main.credencial es null o no tiene equipos_id, usando POS ID por defecto: " + posIdTemp);
                }
                final int posId = posIdTemp; // Variable final para usar en lambda
                
                // Usar grupo_jornada como identificador_jornada (el servicio Python lo resolverá)
                final long identificadorJornada = turnoCerrado.getGrupoJornada();
                final long promotorId = promotor.getId();
                
                NovusUtils.printLn("[FIN TURNO] Parámetros de impresión:");
                NovusUtils.printLn("  - Identificador Jornada (grupo_jornada): " + identificadorJornada);
                NovusUtils.printLn("  - Identificador Promotor: " + promotorId);
                NovusUtils.printLn("  - POS ID: " + posId);
                NovusUtils.printLn("  - Nombre Promotor: " + (promotor != null ? promotor.getNombre() + " " + (promotor.getApellidos() != null ? promotor.getApellidos() : "") : "N/A"));
                NovusUtils.printLn("  - Fecha Inicio: " + (turnoCerrado.getFechaInicial() != null ? turnoCerrado.getFechaInicial().toString() : "N/A"));
                NovusUtils.printLn("  - Fecha Fin: " + (turnoCerrado.getFechaFinal() != null ? turnoCerrado.getFechaFinal().toString() : "N/A"));
                
                // Ejecutar impresión en thread separado para no bloquear la UI
                NovusUtils.printLn("[FIN TURNO] Creando thread para impresión asíncrona...");
                new Thread(() -> {
                    try {
                        NovusUtils.printLn("[FIN TURNO] Thread iniciado, creando Use Case...");
                        com.application.useCases.shiftReports.PrintFinTurnoRemoteUseCase useCase = 
                            new com.application.useCases.shiftReports.PrintFinTurnoRemoteUseCase(
                                identificadorJornada,
                                promotorId,
                                posId
                            );
                        
                        NovusUtils.printLn("[FIN TURNO] Ejecutando Use Case...");
                        long startTime = System.currentTimeMillis();
                        com.domain.dto.shiftReports.ShiftReportResult result = useCase.execute(null);
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        
                        NovusUtils.printLn("[FIN TURNO] Use Case completado en " + duration + " ms");
                        
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            if (result.isSuccess()) {
                                NovusUtils.printLn("[FIN TURNO] ✓ Ticket impreso exitosamente");
                                NovusUtils.printLn("[FIN TURNO] Mensaje: " + result.getMessage());
                                NovusUtils.printLn("========================================");
                                dispose();
                            } else {
                                NovusUtils.printLn("[FIN TURNO] ✗ Error al imprimir: " + result.getMessage());
                                NovusUtils.printLn("========================================");
                                // No cerrar en caso de error, permitir reintentar
                            }
                        });
                    } catch (Exception e) {
                        NovusUtils.printLn("[FIN TURNO] ✗ ERROR INESPERADO: " + e.getMessage());
                        NovusUtils.printLn("[FIN TURNO] Tipo de error: " + e.getClass().getName());
                        e.printStackTrace();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            NovusUtils.printLn("========================================");
                            dispose();
                        });
                    }
                }).start();
                NovusUtils.printLn("[FIN TURNO] Thread de impresión iniciado, continuando con flujo normal...");

            } else {
                NovusUtils.printLn("[FIN TURNO] ✗ ERROR: No se pudo obtener datos del turno");
                NovusUtils.printLn("[FIN TURNO] turnoCerrado es NULL - Backend no devolvió datos");
                dispose();
            }
        } catch (Exception ex) {
            NovusUtils.printLn("[FIN TURNO] ✗ ERROR EXCEPCIÓN: " + ex.getMessage());
            NovusUtils.printLn("[FIN TURNO] Tipo de error: " + ex.getClass().getName());
            Logger.getLogger(ImprimirCierreView.class.getName()).log(Level.SEVERE, null, ex);
            dispose();
        }
    }

    public JsonObject solicitarReporteDiscriminadoResolucion() {
        JsonObject comando = new JsonObject();
        comando.addProperty("identificadorGrupoJornada", promotor.getGrupoJornadaId());
        comando.addProperty("identificadorPromotor", promotor.getId());
        String funcion = "CONSEGUIR TURNOS";
        String url = NovusConstante.SECURE_CENTRAL_POINT_FIN_TURNO_RESOLUCION;

        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, comando, true, false, header);

        JsonObject response = null;
        try {
            response = client.esperaRespuesta();
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

    public JornadaBean solicitarTurno(boolean principal) {
        JsonObject comando = new JsonObject();
        SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        comando.addProperty("identificadorGrupoJornada", promotor.getGrupoJornadaId());
        comando.addProperty("identificadorPromotor", promotor.getId());
        comando.addProperty("principalTurno", (principal) ? 1 : 0);
        boolean isArray = true;
        boolean isDebug = true;
        JornadaBean jornadaBean = null;
        String funcion = "CONSEGUIR TURNOS";
        String url = NovusConstante.SECURE_CENTRAL_POINT_DETALLES_VENTA_PROMOTOR;

        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, comando, isDebug, false, header);

        JsonObject response;
        try {
            client.start();
            client.join();
            response = client.getResponse();
            if (response != null && response.get("data") != null && !response.get("data").isJsonNull()) {
                JsonObject jsonobject = response.get("data").getAsJsonObject();
                jornadaBean = new JornadaBean();
                PersonaBean persona = new PersonaBean();
                jornadaBean.setFechaImpresion(new Date());
                jornadaBean.setPersonaId(jsonobject.get("identificadorPromotor").getAsLong());
                persona.setNombre(jsonobject.get("promotor").getAsString());
                persona.setIdentificacion(jsonobject.get("identificacion").getAsString());
                jornadaBean.setFechaInicial(sdf2.parse(jsonobject.get("fecha_inicio").getAsString()));
                jornadaBean.setFechaFinal(sdf2.parse(jsonobject.get("fecha_fin").getAsString()));
                jornadaBean.setGrupoJornada(jsonobject.get("turno").getAsLong());
                persona.setGrupoJornadaId(jsonobject.get("turno").getAsLong());
                jornadaBean.setCantidadVentas(jsonobject.get("numero_ventas").getAsInt());
                jornadaBean.setSaldo(jsonobject.get("saldo").getAsFloat());
                jornadaBean.setTotalVentas(jsonobject.get("total_ventas").getAsFloat());
                jornadaBean.setTotalVentasCanastilla(jsonobject.get("total_ventas_canastilla").getAsFloat());
                jornadaBean.setCantidadVentasCanastilla(jsonobject.get("numero_ventas_canastilla").getAsInt());
                jornadaBean.setPersona(persona);
                JsonArray arrayLecturasIniciales = jsonobject.get("lecturasiniciales").getAsJsonArray();
                for (JsonElement jsonElementLecturasIniciales : arrayLecturasIniciales) {
                    JsonObject jsonLecturasIniciales = jsonElementLecturasIniciales.getAsJsonObject();
                    Surtidor surtidor = new Surtidor();
                    surtidor.setSurtidor(jsonLecturasIniciales.get("surtidor").getAsInt());
                    surtidor.setCara(jsonLecturasIniciales.get("cara").getAsInt());
                    surtidor.setManguera(jsonLecturasIniciales.get("manguera").getAsInt());
                    surtidor.setGrado(jsonLecturasIniciales.get("grado").getAsInt());
                    surtidor.setIsla(jsonLecturasIniciales.get("isla").getAsInt());
                    surtidor.setProductoIdentificador(jsonLecturasIniciales.get("productoIdentificador").getAsInt());
                    surtidor.setProductoDescripcion(jsonLecturasIniciales.get("productoDescripcion").getAsString());
                    surtidor.setFamiliaIdentificador(jsonLecturasIniciales.get("familiaIdentificador").getAsInt());
                    surtidor.setFamiliaDescripcion(jsonLecturasIniciales.get("familiaDescripcion").getAsString());
                    surtidor.setTotalizadorVolumen(jsonLecturasIniciales.get("acumuladoVolumen").getAsLong());
                    surtidor.setTotalizadorVenta(jsonLecturasIniciales.get("acumuladoVenta").getAsLong());
                    jornadaBean.getLecturasIniciales().add(surtidor);
                }
                JsonArray arrayLecturasFinales = jsonobject.get("lecturasfinales").getAsJsonArray();
                for (JsonElement jsonElementLecturasIniciales : arrayLecturasFinales) {
                    JsonObject jsonLecturasFinales = jsonElementLecturasIniciales.getAsJsonObject();
                    Surtidor surtidor = new Surtidor();
                    surtidor.setSurtidor(jsonLecturasFinales.get("surtidor").getAsInt());
                    surtidor.setCara(jsonLecturasFinales.get("cara").getAsInt());
                    surtidor.setManguera(jsonLecturasFinales.get("manguera").getAsInt());
                    surtidor.setGrado(jsonLecturasFinales.get("grado").getAsInt());
                    surtidor.setIsla(jsonLecturasFinales.get("isla").getAsInt());
                    surtidor.setProductoIdentificador(jsonLecturasFinales.get("productoIdentificador").getAsInt());
                    surtidor.setProductoDescripcion(jsonLecturasFinales.get("productoDescripcion").getAsString());
                    surtidor.setFamiliaIdentificador(jsonLecturasFinales.get("familiaIdentificador").getAsInt());
                    surtidor.setFamiliaDescripcion(jsonLecturasFinales.get("familiaDescripcion").getAsString());
                    surtidor.setTotalizadorVolumen(jsonLecturasFinales.get("acumuladoVolumen").getAsLong());
                    surtidor.setTotalizadorVenta(jsonLecturasFinales.get("acumuladoVenta").getAsLong());
                    jornadaBean.getLecturasFinales().add(surtidor);
                }
                ArrayList<MediosPagosBean> mediospagos = new ArrayList<>();
                JsonArray medios = jsonobject.get("medios").getAsJsonArray();
                for (JsonElement jsonElementMedios : medios) {
                    JsonObject jsonMedios = jsonElementMedios.getAsJsonObject();
                    MediosPagosBean medio = new MediosPagosBean();
                    medio.setDescripcion(jsonMedios.get("medios").getAsString());
                    medio.setValor(jsonMedios.get("total").getAsLong());
                    medio.setCantidad(jsonMedios.get("cantidad").getAsInt());
                    mediospagos.add(medio);
                }

                ArrayList<VentasBean> ventas = new ArrayList<>();
                JsonArray ventasArray = jsonobject.get("ventas").getAsJsonArray();
                for (JsonElement jsonElementVentas : ventasArray) {
                    JsonObject jsonVentas = jsonElementVentas.getAsJsonObject();
                    VentasBean venta = new VentasBean();
                    venta.setFechaVenta(sdf2.parse(jsonVentas.get("fecha").getAsString()));
                    venta.setConsecutivoVenta(jsonVentas.get("consecutivo_venta").getAsLong());
                    venta.setVentaTotal(jsonVentas.get("venta_total").getAsLong());
                    venta.setPlaca(jsonVentas.get("placa").getAsString());
                    venta.setRecaudo(jsonVentas.get("recaudo").getAsFloat());
                    venta.setAnulada(jsonVentas.get("estado_movimiento").getAsString()
                            .equals(NovusConstante.VENTA_ANULADA_CODIGO));
                    venta.setCantidadCombustible(jsonVentas.get("cantidad_combustible").getAsFloat());
                    venta.setUnidadMedida(jsonVentas.get("unidad_medida").getAsString());
                    if (jsonVentas.get("responsables_id") != null && !jsonVentas.get("responsables_id").isJsonNull()) {
                        venta.setPersonas_id(jsonVentas.get("responsables_id").getAsLong());
                        venta.setPersonaNombre(jsonVentas.get("nombres").getAsString());
                    }
                    Surtidor surtidor = new Surtidor();
                    surtidor.setSurtidor(jsonVentas.get("surtidor").getAsInt());
                    surtidor.setCara(jsonVentas.get("cara").getAsInt());
                    surtidor.setManguera(jsonVentas.get("manguera").getAsInt());
                    venta.setSurtidor(surtidor);
                    ventas.add(venta);
                }
                ArrayList<MovimientosBean> ventasCanastilla = new ArrayList<>();
                JsonArray ventasCanastillaArray = jsonobject.get("ventas_canastilla").getAsJsonArray();
                for (JsonElement jsonElement : ventasCanastillaArray) {
                    try {
                        JsonObject json = jsonElement.getAsJsonObject();
                        MovimientosBean mov = new MovimientosBean();
                        mov.setId(json.get("id").getAsLong());
                        mov.setFecha(sdf2.parse(json.get("fecha").getAsString()));
                        mov.setVentaTotal(json.get("venta_total").getAsFloat());
                        mov.setPersonaId(json.get("responsables_id").getAsLong());
                        mov.setPersonaNombre(json.get("nombres").getAsString());
                        if (json.get("impreso") != null) {
                            mov.setImpreso(json.get("impreso").getAsString());
                        } else {
                            mov.setImpreso("N");
                        }
                        mov.setClienteId(json.get("cliente_id").getAsLong());
                        mov.setClienteNit(json.get("cliente_nit").getAsString());
                        mov.setEmpresasId(Main.credencial.getEmpresas_id());
                        mov.setClienteNombre(json.get("cliente_nombre").getAsString());
                        if (json.get("atributos") != null) {
                            mov.setAtributos(json.get("atributos").getAsJsonObject());
                        }
                        mov.setGrupoJornadaId(jsonobject.get("turno").getAsLong());
                        mov.setMovmientoEstado("A");
                        if (mov.getAtributos() != null && mov.getAtributos().get("consecutivo") != null && mov.getAtributos().get("consecutivo").isJsonObject()) {
                            JsonObject jsonConsecutivo = mov.getAtributos().getAsJsonObject("consecutivo");
                            ConsecutivoBean conObtenido = null;
                            try {
                                conObtenido = obtenerConsecutivoUseCase.execute();
                            } catch (Exception ex) {
                                Logger.getLogger(ImprimirCierreView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (conObtenido != null) {
                                conObtenido.setId(jsonConsecutivo.get("id").getAsLong());
                                conObtenido.setPrefijo(jsonConsecutivo.get("prefijo").getAsString());
                                conObtenido.setConsecutivo_actual(json.get("consecutivo_venta").getAsLong());
                            }
                            mov.setConsecutivo(conObtenido);
                        }
                        LinkedHashMap<Long, MovimientosDetallesBean> detalles = new LinkedHashMap<>();
                        float totalImpuestos = 0;
                        float impoConsumo = 0;
                        for (JsonElement jsonElementDetalles : json.getAsJsonArray("detalles")) {
                            JsonObject jsonDetalles = jsonElementDetalles.getAsJsonObject();
                            MovimientosDetallesBean detalleVenta = new MovimientosDetallesBean();
                            detalleVenta.setCantidadUnidad(jsonDetalles.get("cantidad").getAsInt());
                            detalleVenta.setCantidad(jsonDetalles.get("cantidad").getAsLong());
                            detalleVenta.setPlu(jsonDetalles.get("plu").getAsString());
                            detalleVenta.setCosto(jsonDetalles.get("costo_producto").getAsFloat());
                            detalleVenta.setPrecio(jsonDetalles.get("precio").getAsFloat());
                            detalleVenta.setSubtotal(jsonDetalles.get("sub_total").getAsFloat());
                            detalleVenta.setId(jsonDetalles.get("id_producto").getAsLong());
                            detalleVenta.setDescripcion(jsonDetalles.get("nombre_producto").getAsString());
                            detalleVenta.setBodegasId(jsonDetalles.get("bodega_id").getAsLong());
                            ArrayList<ImpuestosBean> impuestos = new ArrayList<>();

                            for (JsonElement jsonElementImpuestos : jsonDetalles.getAsJsonArray("impuestos")) {
                                JsonObject jsonImpuestos = jsonElementImpuestos.getAsJsonObject();
                                ImpuestosBean impuesto = new ImpuestosBean();
                                impuesto.setId(jsonImpuestos.get("id_impuesto").getAsLong());
                                impuesto.setDescripcion(jsonImpuestos.get("nombre_impuesto").getAsString());
                                impuesto.setValor(jsonImpuestos.get("impuesto_valor").getAsFloat());
                                impuesto.setPorcentaje_valor(jsonImpuestos.get("porcentaje_valor").getAsString());
                                impuesto.setCodigo("");
                                impuesto.setCalculado(impuesto.getValor());
                                totalImpuestos += impuesto.getValor();
                                impuestos.add(impuesto);
                            }
                            detalleVenta.setImpuestos(impuestos);
                            detalles.put(detalleVenta.getId(), detalleVenta);
                        }
                        mov.setDetalles(detalles);
                        float recibidoTotal = 0;
                        TreeMap<Long, MediosPagosBean> mediosCanastilla = new TreeMap<>();
                        for (JsonElement jsonElementMedios : json.getAsJsonArray("medios_pagos")) {
                            JsonObject jsonMedio = jsonElementMedios.getAsJsonObject();
                            MediosPagosBean medio = new MediosPagosBean();
                            medio.setId(jsonMedio.get("id_medio_de_pago").getAsLong());
                            medio.setDescripcion(jsonMedio.get("nombre_medio_de_pago").getAsString());
                            medio.setCambio(jsonMedio.get("valor_cambio").getAsFloat());
                            medio.setValor(jsonMedio.get("valor_total").getAsFloat());
                            medio.setRecibido(jsonMedio.get("valor_recibido").getAsFloat());
                            medio.setVoucher(jsonMedio.get("comprobante").getAsString());
                            medio.setComprobante(jsonMedio.get("tiene_comprobante").getAsString().equals("S"));
                            recibidoTotal += medio.getRecibido();
                            mediosCanastilla.put(medio.getId(), medio);
                        }
                        mov.setMediosPagos(mediosCanastilla);
                        mov.setImpuestoTotal(totalImpuestos);
                        mov.setRecibidoTotal(recibidoTotal);
                        ventasCanastilla.add(mov);
                    } catch (ParseException a) {
                        NovusUtils.printLn(a.getMessage());
                    }
                }
                NovusUtils.printLn(ventasCanastilla.toString());

                JsonArray ajustePeriodicoArray = jsonobject.get("ajuste_periodico") == null
                        || jsonobject.get("ajuste_periodico").isJsonNull() ? new JsonArray()
                        : jsonobject.get("ajuste_periodico").getAsJsonArray();

                ArrayList<VentasBean> calibraciones = new ArrayList<>();
                JsonArray calibracionesArray = jsonobject.get("calibraciones").getAsJsonArray();
                for (JsonElement jsonElementCalibraciones : calibracionesArray) {
                    JsonObject jsonCalibraciones = jsonElementCalibraciones.getAsJsonObject();
                    VentasBean venta = new VentasBean();
                    venta.setFechaVenta(sdf2.parse(jsonCalibraciones.get("fecha").getAsString()));
                    venta.setConsecutivoVenta(jsonCalibraciones.get("consecutivo_venta").getAsLong());
                    venta.setVentaTotal(jsonCalibraciones.get("venta_total").getAsLong());
                    venta.setPlaca(jsonCalibraciones.get("placa").getAsString());
                    venta.setCantidadCombustible(jsonCalibraciones.get("cantidad_combustible").getAsFloat());
                    Surtidor surtidor = new Surtidor();
                    surtidor.setSurtidor(jsonCalibraciones.get("surtidor").getAsInt());
                    surtidor.setCara(jsonCalibraciones.get("cara").getAsInt());
                    if (jsonCalibraciones.get("responsables_id") != null
                            && !jsonCalibraciones.get("responsables_id").isJsonNull()) {
                        venta.setPersonas_id(jsonCalibraciones.get("responsables_id").getAsLong());
                        venta.setPersonaNombre(jsonCalibraciones.get("nombres").getAsString());
                    }
                    surtidor.setManguera(jsonCalibraciones.get("manguera").getAsInt());
                    venta.setSurtidor(surtidor);
                    calibraciones.add(venta);
                }

                ArrayList<VentasBean> consumoPropio = new ArrayList<>();
                JsonArray consumoPropioArray = jsonobject.get("consumo_propio").getAsJsonArray();
                for (JsonElement jsonElementComsumo : consumoPropioArray) {
                    JsonObject jsonConsumo = jsonElementComsumo.getAsJsonObject();
                    VentasBean venta = new VentasBean();
                    venta.setFechaVenta(sdf2.parse(jsonConsumo.get("fecha").getAsString()));
                    venta.setConsecutivoVenta(jsonConsumo.get("consecutivo_venta").getAsLong());
                    venta.setVentaTotal(jsonConsumo.get("venta_total").getAsLong());
                    venta.setPlaca(jsonConsumo.get("placa").getAsString());
                    venta.setCantidadCombustible(jsonConsumo.get("cantidad_combustible").getAsFloat());
                    Surtidor surtidor = new Surtidor();
                    surtidor.setSurtidor(jsonConsumo.get("surtidor").getAsInt());
                    surtidor.setCara(jsonConsumo.get("cara").getAsInt());
                    if (jsonConsumo.get("responsables_id") != null
                            && !jsonConsumo.get("responsables_id").isJsonNull()) {
                        venta.setPersonas_id(jsonConsumo.get("responsables_id").getAsLong());
                        venta.setPersonaNombre(jsonConsumo.get("nombres").getAsString());
                    }
                    surtidor.setManguera(jsonConsumo.get("manguera").getAsInt());
                    venta.setSurtidor(surtidor);
                    consumoPropio.add(venta);
                }
                TreeMap<Long, ArrayList<MediosPagosBean>> mediosxsurtidor = new TreeMap<>();
                JsonArray mediosSurtidor = jsonobject.get("medios_surtidor").getAsJsonArray();
                for (JsonElement jsonElement4 : mediosSurtidor) {
                    JsonObject jsonMediosSurtidor = jsonElement4.getAsJsonObject();
                    MediosPagosBean medio = new MediosPagosBean();
                    medio.setDescripcion(jsonMediosSurtidor.get("medios").getAsString());
                    medio.setValor(jsonMediosSurtidor.get("total").getAsLong());
                    medio.setCantidad(jsonMediosSurtidor.get("cantidad").getAsInt());
                    ArrayList<MediosPagosBean> mediosLista;
                    if (mediosxsurtidor.containsKey(jsonMediosSurtidor.get("surtidor").getAsLong())) {
                        mediosLista = mediosxsurtidor.get(jsonMediosSurtidor.get("surtidor").getAsLong());
                    } else {
                        mediosLista = new ArrayList<>();
                    }
                    mediosLista.add(medio);
                    mediosxsurtidor.put(jsonMediosSurtidor.get("surtidor").getAsLong(), mediosLista);
                }
                JsonArray productosArray = jsonobject.get("productos") == null
                        || jsonobject.get("productos").isJsonNull() ? new JsonArray()
                        : jsonobject.get("productos").getAsJsonArray();
                ArrayList<ProductoBean> productos = new ArrayList<>();
                for (JsonElement jsonElementProductos : productosArray) {
                    JsonObject jsonProductos = jsonElementProductos.getAsJsonObject();
                    ProductoBean producto = new ProductoBean();
                    producto.setId(jsonProductos.get("id").getAsLong());
                    producto.setDescripcion(jsonProductos.get("descripcion").getAsString());
                    producto.setCosto(jsonProductos.get("total").getAsFloat());
                    producto.setCantidad(jsonProductos.get("cantidad_combustible").getAsFloat());
                    producto.setUnidades_medida(jsonProductos.get("unidad_medida").getAsString());
                    producto.setCantidadUnidad(jsonProductos.get("cantidad").getAsFloat());
                    NovusUtils.printLn("CANTIDAD COMBUSTIBLE" + producto.getCantidad());
                    productos.add(producto);
                }
                jornadaBean.setVentasCanastilla(ventasCanastilla);
                jornadaBean.setMedidasTanquesIniciales(
                        ajustePeriodicoArray.size() > 0 && ajustePeriodicoArray.get(0).getAsJsonObject() != null
                        && !ajustePeriodicoArray.get(0).isJsonNull()
                        ? ajustePeriodicoArray.get(0).getAsJsonObject()
                        : new JsonObject());
                jornadaBean.setMedidasTanquesFinales(
                        ajustePeriodicoArray.size() > 1 && ajustePeriodicoArray.get(1).getAsJsonObject() != null
                        && !ajustePeriodicoArray.get(1).isJsonNull()
                        ? ajustePeriodicoArray.get(1).getAsJsonObject()
                        : new JsonObject());
                jornadaBean.setCalibraciones(calibraciones);
                jornadaBean.setConsumoPropio(consumoPropio);
                jornadaBean.setVentas(ventas);
                jornadaBean.setVentasProductos(productos);
                jornadaBean.setMediosxsurtido(mediosxsurtidor);
                jornadaBean.setMedios(mediospagos);

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientWSAsync.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        } catch (ParseException ex) {
            Logger.getLogger(ImprimirCierreView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jornadaBean;
    }

    private void cierre(boolean b) {
        respuesta = b;
        this.dispose();
    }
}

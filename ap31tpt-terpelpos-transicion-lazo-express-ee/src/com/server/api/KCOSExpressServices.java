/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.api;

import com.application.useCases.persons.FindPersonaByIdUseCase;
import com.application.useCases.persons.RegistrarTagUseCase;
import com.application.useCases.productos.BuscarProductoPorPluKioskoUseCase;
import com.bean.ConsecutivoBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.bean.Recibo;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.facade.PedidoFacade;
import com.firefuel.KCOViewController;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import com.neo.print.services.PrinterFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.application.useCases.jornada.GetJornadaIdByPersonaUseCase;

/**
 *
 * @author novusteam
 */
public class KCOSExpressServices {

    public void generarVenta(PersonaBean persona, ArrayList<ProductoBean> products) {

        try {
            GetJornadaIdByPersonaUseCase getJornadaIdUseCase = new GetJornadaIdByPersonaUseCase();
            MovimientosDao mdao = new MovimientosDao();
            MovimientosBean movimiento = new MovimientosBean();
            movimiento.setDetalles(new LinkedHashMap<>());
            for (ProductoBean product : products) {
                try {
                    //MovimientosDetallesBean producTemp = mdao.findByPluKIOSCO(product.getPlu());
                    MovimientosDetallesBean producTemp = new BuscarProductoPorPluKioskoUseCase(product.getPlu()).execute();
                    if (producTemp != null) {
                        movimiento.getDetalles().put(producTemp.getId(), producTemp);
                        agregaProductoAlista(movimiento, product.getCantidad(), producTemp);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(KCOSExpressServices.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            KCOViewController vista = new KCOViewController(null, false);
            vista.repaintPanel(false, movimiento);
            ConsecutivoBean newConsecutivo;

            newConsecutivo = mdao.getConsecutivoMarket(false);

            FindPersonaByIdUseCase findPersonaByIdUseCase = new FindPersonaByIdUseCase();


            //PersonasDao pdao = new PersonasDao();
            //persona = pdao.findPersonaById(persona.getId());
            persona = findPersonaByIdUseCase.execute(persona.getId());
            long grupoJornada = getJornadaIdUseCase.execute(persona.getId());
            movimiento.setGrupoJornadaId(grupoJornada);

            MediosPagosBean efectivoInicial = new MediosPagosBean();
            TreeMap<Long, MediosPagosBean> medios = new TreeMap<>();
            efectivoInicial.setId(1);
            efectivoInicial.setValor(movimiento.getVentaTotal());
            efectivoInicial.setRecibido(movimiento.getVentaTotal());
            efectivoInicial.setDescripcion("EFECTIVO");
            efectivoInicial.setCambio(true);
            efectivoInicial.setCambio(0);
            medios.put(1L, efectivoInicial);
            movimiento.setMediosPagos(medios);

            movimiento.setEmpresa(Main.credencial.getEmpresa());
            movimiento.setEmpresasId(Main.credencial.getEmpresas_id());
            movimiento.setPersonaId(persona.getId());
            movimiento.setPersonaNombre(persona.getNombre());
            movimiento.setPersonaApellidos(persona.getApellidos() != null ? persona.getApellidos() : "");
            movimiento.setPersonaNit(persona.getIdentificacion());
            movimiento.setClienteId(2);
            movimiento.setClienteNombre("CLIENTES VARIOS");
            movimiento.setClienteNit("2222222");

            movimiento.setConsecutivo(newConsecutivo);
            movimiento.setFecha(new Date());
            movimiento.setOperacionId(NovusConstante.MOVIMIENTO_TIPO_KIOSCO);

            Recibo rec = new Recibo();
            rec.setPlaca("");
            rec.setEmpresa(movimiento.getEmpresa().getRazonSocial());
            rec.setDireccion(movimiento.getEmpresa().getDireccionPrincipal());
            rec.setTelefono(movimiento.getEmpresa().getTelefonoPrincipal());
            movimiento.setMovmientoEstado("A");
            rec.setMovimiento(movimiento);

            boolean isCombustible = false;
            if (movimiento.getDetalles() != null) {
                for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                    MovimientosDetallesBean value = entry.getValue();
                    if (value.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                        isCombustible = true;
                        break;
                    }
                }
            }

            if (!isCombustible) {
                EquipoDao dao = new EquipoDao();

                String impresora = dao.getParametrosImpresoraKCO();

                MovimientosBean movimientoRealizado = null;
                movimientoRealizado = mdao.createKIOSCO(movimiento, Main.credencial,false);

                if (movimientoRealizado != null) {
                    movimientoRealizado.setMovmientoEstado("A");
                    rec.setMovimiento(movimientoRealizado);
                }
                JsonObject response = null;
                PedidoFacade pedidoF = new PedidoFacade();
                if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_KIOSCO) {
                    response = pedidoF.sendVentaKIOSCO(movimientoRealizado, true, false);
                } else {
                    response = pedidoF.sendVenta(movimientoRealizado, false);
                }

                PrinterFacade pf = new PrinterFacade();
                pf.printReciboKIOSCO(impresora, rec);
            }

        } catch (DAOException ex) {
            Logger.getLogger(KCOSExpressServices.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Natalia Aurora
    public static void agregaProductoAlista(MovimientosBean movimiento, float cantidadSolicitada,
            MovimientosDetallesBean producTemp) {
        if (!producTemp.isCompuesto()) {
            if (Main.FACTURACION_NEGATIVO) {
                producTemp.setCantidadUnidad(cantidadSolicitada);
                if (producTemp.isCombustible()) {
                    producTemp.setSubtotal(0);
                } else {
                    // CORREGIR
                    producTemp.setSubtotal(producTemp.getPrecio() * producTemp.getCantidadUnidad());
                }

            } else {
                if (producTemp.getCantidadUnidad() > 0) {
                    if (producTemp.getCantidadUnidad() < cantidadSolicitada) {
                        NovusUtils.printLn("CANTIDAD SOLICITADA NO PERMITIDA");
                    } else {
                        producTemp.setCantidadUnidad(cantidadSolicitada);
                    }
                } else {
                    NovusUtils.printLn("SIN EXISTENCIA EN BODEGA");
                }
            }
        } else {
            boolean existencia = true;
            ProductoBean faltante = null;
            if (!producTemp.getIngredientes().isEmpty()) {
                producTemp.setProducto_compuesto_costo(0);
                for (ProductoBean ingrediente : producTemp.getIngredientes()) {

                    if (ingrediente.isCompuesto()) {
                        // existencia = validaExistenciaIngredientes(Integer.parseInt(cantidadSolicitada
                        // + ""), ingrediente);
                    } else {
                        if (ingrediente.getCantidadUnidad() < 0
                                || ingrediente.getCantidadUnidad() < (ingrediente.getProducto_compuesto_cantidad()
                                        * cantidadSolicitada)) {
                            if (Main.FACTURACION_NEGATIVO) {
                                existencia = true;
                            } else {
                                existencia = false;
                            }

                        }
                    }
                    if (!existencia) {
                        faltante = ingrediente;
                        break;
                    } else {
                        // TODO: Esta pendiente
                        producTemp.setProducto_compuesto_costo(
                                producTemp.getProducto_compuesto_costo() + ingrediente.getProducto_compuesto_cantidad()
                                        * ingrediente.getProducto_compuesto_costo());
                        // producTemp.setCostos(producTemp.getProducto_compuesto_costo());
                    }
                }
            } else {
                existencia = false;
            }
            if (existencia) {
                producTemp.setCantidadUnidad(cantidadSolicitada);
            } else {
                if (faltante != null) {
                    NovusUtils.printLn("FALTA INGREDIENTE: " + faltante.getDescripcion());
                } else {
                    NovusUtils.printLn("PRODUCTO SIN EXISTENCIA");
                }
            }
        }
    }

}

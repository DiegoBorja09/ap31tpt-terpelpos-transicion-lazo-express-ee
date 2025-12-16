package com.firefuel.ventas.Service;

import com.firefuel.ventas.dto.VentaDTO;
import com.firefuel.ventas.repository.VentasRepository;

import java.util.List;

public class VentasService {
    private final VentasRepository repository = new VentasRepository();


    public List<VentaDTO> obtenerVentasBasicas(long promotorID, long jornadaID, int paginacion, boolean sinResolver){

        return repository.obtenerVentasBasicas(promotorID, jornadaID, paginacion, sinResolver);
    }
}

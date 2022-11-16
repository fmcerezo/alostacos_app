package alostacos.juegos.atletismo.alostacos.controlador.eventos;

import java.util.EventListener;

/**
 * Created by Francisco Cerezo on 20/05/2016.
 */
public interface EntrenamientoRealizadoEventListener extends EventListener {
    void EntrenamientoRealizado(EntrenamientoRealizadoEventObject args);
}
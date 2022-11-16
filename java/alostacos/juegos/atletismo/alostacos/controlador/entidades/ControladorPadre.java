package alostacos.juegos.atletismo.alostacos.controlador.entidades;

import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;
import alostacos.juegos.atletismo.alostacos.modelo.Comunicaciones;

/**
 * Created by Francisco Cerezo on 14/05/2016.
 */
public class ControladorPadre {
    protected Comunicaciones com;
    protected VistaCache vistaCache;

    public ControladorPadre() {
        this.com = Comunicaciones.getInstance();
        this.vistaCache = VistaCache.getInstance();
    }
}

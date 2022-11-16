package alostacos.juegos.atletismo.alostacos.controlador.eventos;

import java.util.EventObject;

import alostacos.juegos.atletismo.alostacos.modelo.DatosAtletaJSON;

/**
 * Created by Francisco Cerezo on 20/05/2016.
 */
public class DatosAtletaRecuperadosEventObject extends EventObject {
    private DatosAtletaJSON datosAtleta;

    public DatosAtletaRecuperadosEventObject(Object source, DatosAtletaJSON datosAtleta) {
        super (source);
        this.datosAtleta = datosAtleta;
    }

    public DatosAtletaJSON getDatosAtleta() { return datosAtleta; }
}
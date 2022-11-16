package alostacos.juegos.atletismo.alostacos.controlador.entidades;

import java.util.List;

import alostacos.juegos.atletismo.alostacos.controlador.excepciones.NoAutorizadoException;

/**
 * Created by Francisco Cerezo on 06/05/2016.
 */
public class Entrenamiento extends ControladorPadre {
    public List<String> getFactoresEntrenamiento(String tipoEntrenamiento) {
        return super.com.getFactoresEntrenamiento(tipoEntrenamiento);
    }

    public List<List<String>> getTiposEntrenamiento() {
        return super.com.getTiposEntrenamiento();
    }

    public boolean loadFactoresEntrenamiento(String tipoEntrenamiento) throws NoAutorizadoException {
        return super.com.loadFactoresEntrenamiento(tipoEntrenamiento);
    }

    public boolean loadTiposEntrenamiento() {
        return super.com.loadTiposEntrenamiento();
    }
}

package alostacos.juegos.atletismo.alostacos.controlador;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Francisco Cerezo on 16/07/2016.
 */
public class BasicState implements Serializable {
    public HashMap<String, List<InfoFactor>> factoresEntrenamiento;
    public List<List<String>> tiposEntrenamiento;
    public String hash;
    public boolean logado;
    public int idxViewPage, numeroNiveles;


    @Override
    public String toString() {
        return "BasicState{" +
                "factoresEntrenamiento=" + factoresEntrenamiento +
                ", tiposEntrenamiento=" + tiposEntrenamiento +
                ", hash='" + hash + '\'' +
                ", logado=" + logado +
                ", idxViewPage=" + idxViewPage +
                ", numeroNiveles=" + numeroNiveles +
                '}';
    }
}

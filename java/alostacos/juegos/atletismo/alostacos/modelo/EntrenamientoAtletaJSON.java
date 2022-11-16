package alostacos.juegos.atletismo.alostacos.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francisco Cerezo on 23/05/2016.
 */
public class EntrenamientoAtletaJSON extends RespuestaJSON {
    public List<ValoresEntrenamiento> valoresEntrenamiento = new ArrayList<ValoresEntrenamiento>();

    public class ValoresEntrenamiento {
        public int nivel;
        public float porcentajeNivel;
    }
}
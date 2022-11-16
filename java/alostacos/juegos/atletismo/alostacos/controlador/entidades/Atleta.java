package alostacos.juegos.atletismo.alostacos.controlador.entidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alostacos.juegos.atletismo.alostacos.controlador.InfoFactor;
import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;
import alostacos.juegos.atletismo.alostacos.controlador.excepciones.NoAutorizadoException;
import alostacos.juegos.atletismo.alostacos.modelo.DatosAtletaJSON;
import alostacos.juegos.atletismo.alostacos.modelo.EntrenamientoAtletaJSON;

/**
 * Created by Francisco Cerezo on 23/05/2016.
 */
public class Atleta extends ControladorPadre {
    public boolean entrenar(HashMap<String, List<InfoFactor>> factoresEntrenamiento) {
        boolean returnValue;

        try {
            Iterator it = factoresEntrenamiento.entrySet().iterator();

            returnValue = true;

            while (it.hasNext()) {
                int sumatorio = 0;
                Map.Entry e = (Map.Entry) it.next();
                String tipoEntrenamiento = e.getKey().toString();

                List<InfoFactor> listaInfoFactores = (List<InfoFactor>) e.getValue();
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (InfoFactor iFactor : listaInfoFactores) {
                    sumatorio += iFactor.getEnergiaAsignada();
                    sb.append(String.format("%d,", iFactor.getEnergiaAsignada()));
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(']');

                //Si de tipo de entrenamiento no se han asignado puntos no se hace petición web.
                if(sumatorio > 0)
                    returnValue &= this.com.entrenar(tipoEntrenamiento, sb.toString());
            }
        }
        catch(NoAutorizadoException e) {
            super.vistaCache.setSinAutorizacion();
            returnValue = false;
        }
        catch(Exception e) {
            returnValue = false;
        }

        return returnValue;
    }

    public DatosAtletaJSON getEnergia() {
        try {
            return super.com.getEnergiaAtleta();
        }
        catch(NoAutorizadoException e) {
            super.vistaCache.setSinAutorizacion();
        }

        return null;
    }

    public List<EntrenamientoAtletaJSON.ValoresEntrenamiento> getEntrenamiento(String tipoEntrenamiento) throws NoAutorizadoException {
        return super.com.getEntrenamiento(tipoEntrenamiento);
    }
}

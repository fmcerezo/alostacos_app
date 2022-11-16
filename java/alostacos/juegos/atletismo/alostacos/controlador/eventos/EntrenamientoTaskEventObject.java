package alostacos.juegos.atletismo.alostacos.controlador.eventos;

import java.util.EventObject;
import java.util.List;

import alostacos.juegos.atletismo.alostacos.modelo.EntrenamientoAtletaJSON.ValoresEntrenamiento;

/**
 * Created by Francisco Cerezo on 20/05/2016.
 */
public class EntrenamientoTaskEventObject extends EventObject {
    private int sectionNumber;
    private String tipoEntrenamiento;
    private List<ValoresEntrenamiento> valoresEntrenamiento;
    private List<String> factores;

    public EntrenamientoTaskEventObject (Object source, int sectionNumber, String tipoEntrenamiento, List<String> factores, List<ValoresEntrenamiento> valoresEntrenamiento) {
        super (source);
        this.sectionNumber = sectionNumber;
        this.tipoEntrenamiento = tipoEntrenamiento;
        this.factores = factores;
        this.valoresEntrenamiento = valoresEntrenamiento;
    }

    public List<String> getFactores () { return this.factores; }

    public List<ValoresEntrenamiento> getListadoValoresEntrenamiento() { return this.valoresEntrenamiento; }

    public int getSectionNumber () { return this.sectionNumber; }

    public String getTipoEntrenamiento() {
        return tipoEntrenamiento;
    }
}
package alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Atleta;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Entrenamiento;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoTaskEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoTaskEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.excepciones.NoAutorizadoException;
import alostacos.juegos.atletismo.alostacos.modelo.EntrenamientoAtletaJSON.ValoresEntrenamiento;

/**
 * Created by Francisco Cerezo on 19/05/2016.
 */
public class EntrenamientoTask extends AsyncTask<Integer, Void, Boolean> implements EntrenamientoTaskEventListener {
    // Aqui se almacenaran todos los manejadores (delegados) de evento
    private ArrayList listeners;
    private Atleta atleta;
    private Entrenamiento entrenamiento;
    private List<ValoresEntrenamiento> valoresEntrenamiento;
    private int sectionNumber;
    private List<String> factores;

    public EntrenamientoTask() {
        this.atleta = new Atleta();
        this.entrenamiento = new Entrenamiento();
        this.factores = Arrays.asList();
        this.listeners = new ArrayList();
    }

    public void addFactoresEventListener(EntrenamientoTaskEventListener listener) { listeners.add(listener); }
    public void FactoresObtenidos(EntrenamientoTaskEventObject args) {}

    @Override
    protected Boolean doInBackground(Integer... params) {
        boolean returnValue;
        List<String> tipoEntrenamiento;

        try {
            this.sectionNumber = params[0];
            tipoEntrenamiento = this.entrenamiento.getTiposEntrenamiento().get(this.sectionNumber);

            returnValue = this.entrenamiento.loadFactoresEntrenamiento(tipoEntrenamiento.get(0));
            if(returnValue)
                this.factores = this.entrenamiento.getFactoresEntrenamiento(tipoEntrenamiento.get(0));

            this.valoresEntrenamiento = this.atleta.getEntrenamiento(tipoEntrenamiento.get(0));
        }
        catch(Exception e) {
            returnValue = false;
        }

        return returnValue;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        List<String> listaFactores;
        ListIterator li = listeners.listIterator();

        if(success)
            listaFactores = this.factores;
        else
            listaFactores = null;

        while (li.hasNext()) {
            EntrenamientoTaskEventListener listener = (EntrenamientoTaskEventListener)li.next();
            EntrenamientoTaskEventObject entrTaskEvObj = new EntrenamientoTaskEventObject(this, this.sectionNumber, this.entrenamiento.getTiposEntrenamiento().get(this.sectionNumber).get(0), listaFactores, this.valoresEntrenamiento);
            listener.FactoresObtenidos(entrTaskEvObj);
        }
    }
}
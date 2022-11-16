package alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import alostacos.juegos.atletismo.alostacos.controlador.InfoFactor;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Atleta;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.DatosAtletaRecuperadosEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.DatosAtletaRecuperadosEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoRealizadoEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoRealizadoEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.excepciones.NoAutorizadoException;
import alostacos.juegos.atletismo.alostacos.modelo.DatosAtletaJSON;

/**
 * Created by Francisco Cerezo on 19/05/2016.
 */
public class AtletaTask extends AsyncTask<Object, Void, Boolean> implements DatosAtletaRecuperadosEventListener, EntrenamientoRealizadoEventListener {
    // Aqui se almacenaran todos los manejadores (delegados) de evento
    private ArrayList listenersEnergiaRecuperada, listenersEntrenamientoRealizado;
    private Atleta atleta;
    private TareaAtleta tareaAtleta;
    private DatosAtletaJSON datosAtleta;

    public AtletaTask() {
        this.atleta = new Atleta();
        this.listenersEnergiaRecuperada = new ArrayList();
        this.listenersEntrenamientoRealizado = new ArrayList();
    }

    //region Gestión Eventos
    public void addEnergiaRecuperadaEventListener(DatosAtletaRecuperadosEventListener listener) { listenersEnergiaRecuperada.add(listener); }
    public void DatosAtletaRecuperados(DatosAtletaRecuperadosEventObject args) {}
    public void addEntrenamientoRealizadoEventListener(EntrenamientoRealizadoEventListener listener) { listenersEntrenamientoRealizado.add(listener); }
    public void EntrenamientoRealizado(EntrenamientoRealizadoEventObject args) {}
    //endregion

    @Override
    protected Boolean doInBackground(Object... params) {
        boolean returnValue;

        this.tareaAtleta = (TareaAtleta) params[0];

        if(this.tareaAtleta == TareaAtleta.GetEnergia ) {
            try {
                this.datosAtleta = this.atleta.getEnergia();
                returnValue = true;
            }
            catch (Exception e) {
                returnValue = false;
            }
        }
        else {
            try {
                HashMap<String, List<InfoFactor>> factoresEntrenamiento = (HashMap) params[1];

                returnValue = this.atleta.entrenar(factoresEntrenamiento);
            }
            catch (Exception e) {
                returnValue = false;
            }
        }

        return returnValue;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        ListIterator li;

        if(this.tareaAtleta == TareaAtleta.GetEnergia ) {
            li = this.listenersEnergiaRecuperada.listIterator();

            //TODO: Habrá que indicar con mayor corrección esta incidencia.
            if (!success) {
                this.datosAtleta = new DatosAtletaJSON();
                this.datosAtleta.nombre = "Sin conexión";
            }

            while (li.hasNext()) {
                DatosAtletaRecuperadosEventListener listener = (DatosAtletaRecuperadosEventListener) li.next();
                DatosAtletaRecuperadosEventObject energiaRecuperadaTaskEvObj = new DatosAtletaRecuperadosEventObject(this, this.datosAtleta);
                listener.DatosAtletaRecuperados(energiaRecuperadaTaskEvObj);
            }
        }
        else {
            li = this.listenersEntrenamientoRealizado.listIterator();

            while (li.hasNext()) {
                EntrenamientoRealizadoEventListener listener = (EntrenamientoRealizadoEventListener) li.next();
                EntrenamientoRealizadoEventObject entrenamientoRealizadoTaskEvObj = new EntrenamientoRealizadoEventObject(this);
                listener.EntrenamientoRealizado(entrenamientoRealizadoTaskEvObj);
            }
        }
    }

    public enum TareaAtleta {
        GetEnergia, Entrenar
    }
}
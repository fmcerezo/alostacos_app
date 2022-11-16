package alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.ListIterator;

import alostacos.juegos.atletismo.alostacos.controlador.entidades.Configuracion;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Entrenamiento;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Usuario;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.LoginUsuarioEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.LoginUsuarioEventObject;

/**
 * Created by Francisco Cerezo on 19/05/2016.
 */
public class UsuarioTask extends AsyncTask<String, Void, UsuarioTask.ResultadoLoginUsuario> implements LoginUsuarioEventListener {
    // Aqui se almacenaran todos los manejadores (delegados) de evento
    private ArrayList listeners;
    private Usuario usuario;

    public UsuarioTask() {
        this.usuario = new Usuario();
        this.listeners = new ArrayList();
    }

    //region Gestión Eventos
    public void addLoginUsuarioEventListener(LoginUsuarioEventListener listener) { this.listeners.add(listener); }
    public void LoginUsuario(LoginUsuarioEventObject args) {}
    //endregion

    @Override
    protected ResultadoLoginUsuario doInBackground(String... params) {
        ResultadoLoginUsuario returnValue;
        String user, pwd;

        try {
            user = params[0];
            pwd = params[1];

            try {
                //TODO: Hayq que establecer control de versión, si no coincide hay que obligar a actualizar.
                if(this.usuario.login(user, pwd)) {
                    if(new Entrenamiento().loadTiposEntrenamiento() && Configuracion.getInstance().loadConfiguracion())
                        returnValue = ResultadoLoginUsuario.Correcto;
                    else
                        returnValue = ResultadoLoginUsuario.ErrorComunicaciones;
                }
                else
                    returnValue = ResultadoLoginUsuario.Incorrecto;
            } catch (Exception e) {
                returnValue = ResultadoLoginUsuario.ErrorComunicaciones;
            }
        }
        catch(Exception e) {
            returnValue = ResultadoLoginUsuario.Incorrecto;
        }

        return returnValue;
    }

    @Override
    protected void onPostExecute(ResultadoLoginUsuario success) {
        this.LanzaEventosLoginUsuario(success);
    }

    @Override
    protected void onCancelled() {
        this.LanzaEventosLoginUsuario(ResultadoLoginUsuario.ErrorComunicaciones);
    }

    private void LanzaEventosLoginUsuario(ResultadoLoginUsuario success) {
        ListIterator li = this.listeners.listIterator();

        while (li.hasNext()) {
            LoginUsuarioEventListener listener = (LoginUsuarioEventListener) li.next();
            LoginUsuarioEventObject loginUsuarioTaskEvObj = new LoginUsuarioEventObject(this, success);
            listener.LoginUsuario(loginUsuarioTaskEvObj);
        }
    }

    public enum ResultadoLoginUsuario {
        Correcto, Incorrecto, ErrorComunicaciones
    }
}
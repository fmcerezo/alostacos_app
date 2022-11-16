package alostacos.juegos.atletismo.alostacos.controlador.eventos;

import java.util.EventObject;

import alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas.UsuarioTask;

/**
 * Created by Francisco Cerezo on 20/05/2016.
 */
public class LoginUsuarioEventObject extends EventObject {
    private UsuarioTask.ResultadoLoginUsuario resultadoLoginUsuario;

    public LoginUsuarioEventObject(Object source, UsuarioTask.ResultadoLoginUsuario resultadoLoginUsuario) {
        super (source);
        this.resultadoLoginUsuario = resultadoLoginUsuario;
    }

    public UsuarioTask.ResultadoLoginUsuario getResultadoLoginUsuario() {
        return this.resultadoLoginUsuario;
    }
}
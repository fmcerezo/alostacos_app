package alostacos.juegos.atletismo.alostacos.controlador.eventos;

import java.util.EventListener;

/**
 * Created by Francisco Cerezo on 20/05/2016.
 */
public interface LoginUsuarioEventListener extends EventListener {
    void LoginUsuario(LoginUsuarioEventObject args);
}
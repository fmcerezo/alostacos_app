package alostacos.juegos.atletismo.alostacos.controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import alostacos.juegos.atletismo.alostacos.controlador.entidades.Configuracion;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Entrenamiento;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.CierreEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.CierreEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.DatosAtletaRecuperadosEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.DatosAtletaRecuperadosEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoComenzadoEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoComenzadoEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoRealizadoEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoRealizadoEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoTaskEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoTaskEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EstadoBotonesModificadoEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EstadoBotonesModificadoEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.LoginUsuarioEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.LoginUsuarioEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.VistaCacheRefrescadaEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.VistaCacheRefrescadaEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.excepciones.NoAutorizadoException;
import alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas.AtletaTask;
import alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas.EntrenamientoTask;
import alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas.UsuarioTask;
import alostacos.juegos.atletismo.alostacos.modelo.Comunicaciones;

/**
 * Created by Francisco Cerezo on 26/05/2016.
 */
public class VistaCache implements EstadoBotonesModificadoEventListener, VistaCacheRefrescadaEventListener, LoginUsuarioEventListener {
    //region Gestión Singleton
    private static VistaCache ourInstance = new VistaCache();
    private VistaCache() {
        this.initialize();
    }
    public void initialize() {
        this.idxViewPage = 0;
        this.logado = false;
        this.listenersCierreAplicacion = new ArrayList();
        this.listenersCacheRefrescada = new ArrayList();
        this.listenersEnergiaRecuperada = new ArrayList();
        this.listenersEstadoBotonesModificado = new ArrayList();
        this.listenersEntrenamientoComenzado = new ArrayList();
        this.listenersLoginUsuario = new ArrayList();
    }
    public static VistaCache getInstance() { return ourInstance; }
    //endregion

    // Aqui se almacenarán todos los manejadores (delegados) de evento
    private ArrayList listenersCierreAplicacion, listenersEnergiaRecuperada, listenersEntrenamientoComenzado, listenersCacheRefrescada, listenersEstadoBotonesModificado, listenersLoginUsuario;
    private HashMap<String, List<InfoFactor>> factoresEntrenamiento;
    private List<List<String>> tiposEntrenamiento;
    private String nombreAtleta;
    private boolean logado;
    private int energiaAsignada, energiaAtleta, idxViewPage, nivelAtleta, tiposRecuperados;

    //region Gestión Eventos
    public void addCierreAplicacionEventListener(CierreEventListener listener) { this.listenersCierreAplicacion.add(listener); }
    public void addDatosAtletaRecuperadosEventListener(DatosAtletaRecuperadosEventListener listener) { listenersEnergiaRecuperada.add(listener); }
    public void addEstadoBotonesModificadoEventListener(EstadoBotonesModificadoEventListener listener) { listenersEstadoBotonesModificado.add(listener); }
    public void addEntrenamientoComenzadoEventListener(EntrenamientoComenzadoEventListener listener) { listenersEntrenamientoComenzado.add(listener); }
    public void addVistaCacheRefrescadaEventListener(VistaCacheRefrescadaEventListener listener) { listenersCacheRefrescada.add(listener); }
    public void addLoginUsuarioEventListener(LoginUsuarioEventListener listener) { this.listenersLoginUsuario.add(listener); }
    public void EstadoBotonesModificado(EstadoBotonesModificadoEventObject args, boolean botonMasEnabled) {}
    public void LoginUsuario(LoginUsuarioEventObject args) {}
    public void VistaCacheRefrescada(VistaCacheRefrescadaEventObject args) {}
    //endregion

    public void actualizar() {
        if(this.logado) {
            this.energiaAtleta = this.energiaAsignada = this.tiposRecuperados = 0;
            this.factoresEntrenamiento = new HashMap<String, List<InfoFactor>>(this.tiposEntrenamiento.size());

            this.actualizarDatosEntrenamiento();
            this.actualizarEnergiaAtleta(true);
        }
        else
            this.notificarCierre();
    }

    public void actualizarEnergia() {
        this.actualizarEnergiaAtleta(false);
    }

    public void entrenar(int idxViewPage) {
        AtletaTask atletaTask;

        this.idxViewPage = idxViewPage;

        try {
            atletaTask = new AtletaTask();
            atletaTask.addEntrenamientoRealizadoEventListener(new EntrenamientoRealizadoEventListener() {
                @Override
                public void EntrenamientoRealizado(EntrenamientoRealizadoEventObject args) {
                    actualizar();
                }
            });

            ListIterator li = this.listenersEntrenamientoComenzado.listIterator();

            while (li.hasNext()) {
                    EntrenamientoComenzadoEventListener listener = (EntrenamientoComenzadoEventListener) li.next();
                    EntrenamientoComenzadoEventObject evObj = new EntrenamientoComenzadoEventObject(this);
                    listener.EntrenamientoComenzado(evObj);
            }
            atletaTask.execute(AtletaTask.TareaAtleta.Entrenar, this.factoresEntrenamiento);
        }
        finally {
            atletaTask = null;
        }
    }

    public BasicState getBasicState() {
        BasicState basicState = Comunicaciones.getInstance().getBasicState();

        basicState.logado = this.logado;
        basicState.idxViewPage = this.idxViewPage;
        basicState.numeroNiveles = Configuracion.getInstance().getNumeroNiveles();
        basicState.factoresEntrenamiento = this.factoresEntrenamiento;

        return basicState;
    }

    public int getEnergiaDisponible() { return this.getEnergiaAtleta() - this.energiaAsignada; }

    public List<InfoFactor> getFactoresEntrenamiento(int idxTipoEntrenamiento) {
        String nombreTipoEntrenamiento;

        nombreTipoEntrenamiento = this.tiposEntrenamiento.get(idxTipoEntrenamiento).get(0);

        return this.factoresEntrenamiento.get(nombreTipoEntrenamiento);
    }

    public int getIdxViewPage() { return idxViewPage; }

    public String getNombreAtleta() { return nombreAtleta; }

    public int getNivelAtleta() { return nivelAtleta; }

    public List<List<String>> getTiposEntrenamiento() {
        return tiposEntrenamiento;
    }

    public void login(String user, String password) {
        UsuarioTask usuarioTask;

        try {
            usuarioTask = new UsuarioTask();
            usuarioTask.addLoginUsuarioEventListener(new LoginUsuarioEventListener() {
                @Override
                public void LoginUsuario(LoginUsuarioEventObject args) {
                    tiposEntrenamiento = new Entrenamiento().getTiposEntrenamiento();
                    //Indico que el login ha sido correcto.
                    logado = args.getResultadoLoginUsuario() == UsuarioTask.ResultadoLoginUsuario.Correcto;
                    ListIterator li = listenersLoginUsuario.listIterator();

                    while (li.hasNext()) {
                        LoginUsuarioEventListener listener = (LoginUsuarioEventListener)li.next();
                        LoginUsuarioEventObject evObj = new LoginUsuarioEventObject(this, args.getResultadoLoginUsuario());
                        listener.LoginUsuario(evObj);
                    }

                }
            });
            usuarioTask.execute(new String[] {user, password});
        }
        finally {
            usuarioTask = null;
        }
    }

    public int modificarEnergiaAsignada(boolean incrementar, int idxTipoEntrenamiento, int idxFactor) {
        String tipoEntrenamiento;

        tipoEntrenamiento = this.tiposEntrenamiento.get(idxTipoEntrenamiento).get(0);

        if(incrementar && this.energiaAsignada < this.energiaAtleta) {
            this.energiaAsignada++;
            this.factoresEntrenamiento.get(tipoEntrenamiento).get(idxFactor).incrementarEnergiaAsignada();
            if(this.getEnergiaDisponible() < 1)
                this.avisarEstadoBotones(false);
        }
        else if(!incrementar && this.factoresEntrenamiento.get(tipoEntrenamiento).get(idxFactor).getEnergiaAsignada() > 0) {
            this.energiaAsignada--;
            this.factoresEntrenamiento.get(tipoEntrenamiento).get(idxFactor).decrementarEnergiaAsignada();
            if(this.getEnergiaDisponible() == 1)
                this.avisarEstadoBotones(true);
        }

        return this.factoresEntrenamiento.get(tipoEntrenamiento).get(idxFactor).getEnergiaAsignada();
    }

    public void notificarCierre() {
        ListIterator li = this.listenersCierreAplicacion.listIterator();

        while (li.hasNext()) {
            CierreEventListener listener = (CierreEventListener) li.next();
            CierreEventObject evObj = new CierreEventObject(this);
            listener.CierreAplicacion(evObj);
        }
    }

    public boolean isEnergiaAsignada() { return this.energiaAsignada > 0; }

    public boolean isEnergiaEnMaximo() { return this.nivelAtleta + 9 <= this.energiaAtleta; }

    public boolean isLogado() {
        return this.logado;
    }

    public void setBasicState(BasicState basicState) {
        this.logado = basicState.logado;
        this.idxViewPage = basicState.idxViewPage;
        this.factoresEntrenamiento = basicState.factoresEntrenamiento;
        this.tiposEntrenamiento = basicState.tiposEntrenamiento;
        Configuracion.getInstance().setNumeroNiveles(basicState.numeroNiveles);
        Comunicaciones.getInstance().setBasicState(basicState);
    }

    public void setSinAutorizacion() { this.logado = false; }

    public String toStringComAPI() {
        return Comunicaciones.getInstance().toStringComAPI() + " - VistaCache factoresEntrenamiento: " + this.factoresEntrenamiento.toString();
    }


    //region Métodos privados
    private void actualizarDatosEntrenamiento() {
        final boolean noAutorizado = false;

        for (int x = 0; !noAutorizado && x < this.tiposEntrenamiento.size(); x++) {
            EntrenamientoTask enTask;

            try {
                final List<InfoFactor> infoFactores = new ArrayList<InfoFactor>();
                final HashMap<String, List<InfoFactor>> aux = this.factoresEntrenamiento;

                enTask = new EntrenamientoTask();
                enTask.addFactoresEventListener(new EntrenamientoTaskEventListener() {
                    @Override
                    public void FactoresObtenidos(EntrenamientoTaskEventObject args) {
                        if (args.getFactores() != null) {
                            List<String> factores = args.getFactores();

                            for (int x = 0; x < factores.size(); x++)
                                infoFactores.add(new InfoFactor(factores.get(x), args.getListadoValoresEntrenamiento().get(x).nivel, args.getListadoValoresEntrenamiento().get(x).porcentajeNivel));
                        }

                        aux.put(args.getTipoEntrenamiento(), infoFactores);
                        avisarInfoRecuperada();
                    }
                }
                );

                enTask.execute(x);
            }
            finally {
                enTask = null;
            }
        }
    }

    private void actualizarEnergiaAtleta(final boolean avisarInfoRecuperada) {
        AtletaTask atletaTask;

        try {
            atletaTask = new AtletaTask();
            atletaTask.addEnergiaRecuperadaEventListener(new DatosAtletaRecuperadosEventListener() {
                @Override
                public void DatosAtletaRecuperados(DatosAtletaRecuperadosEventObject args) {
                    if(logado) {
                        energiaAtleta = args.getDatosAtleta().energia;
                        nivelAtleta = args.getDatosAtleta().nivel;
                        nombreAtleta = args.getDatosAtleta().nombre;

                        // true avisa de actualización completa, false solo de actualización de la energia del atleta.
                        if (avisarInfoRecuperada)
                            avisarInfoRecuperada();
                        else {
                            ListIterator li = listenersEnergiaRecuperada.listIterator();

                            while (li.hasNext()) {
                                DatosAtletaRecuperadosEventListener listener = (DatosAtletaRecuperadosEventListener) li.next();
                                DatosAtletaRecuperadosEventObject energiaRecuperadaTaskEvObj = new DatosAtletaRecuperadosEventObject(this, args.getDatosAtleta());
                                listener.DatosAtletaRecuperados(energiaRecuperadaTaskEvObj);
                            }
                        }
                    }
                    else
                        notificarCierre();
                }
            });

            atletaTask.execute(AtletaTask.TareaAtleta.GetEnergia);
        }
        finally {
            atletaTask = null;
        }
    }

    private void avisarEstadoBotones(boolean botonMasEnabled) {
        ListIterator li = this.listenersEstadoBotonesModificado.listIterator();

        while (li.hasNext()) {
            EstadoBotonesModificadoEventListener listener = (EstadoBotonesModificadoEventListener)li.next();
            EstadoBotonesModificadoEventObject evObj = new EstadoBotonesModificadoEventObject(this);
            listener.EstadoBotonesModificado(evObj, botonMasEnabled);
        }
    }

    private void avisarInfoRecuperada() {
        this.tiposRecuperados++;
        // Tienen que estar recuperados los tipos de entrenamiento más la energia disponible del atleta.
        if(this.tiposEntrenamiento.size() + 1 == this.tiposRecuperados) {
            ListIterator li = this.listenersCacheRefrescada.listIterator();

            while (li.hasNext()) {
                VistaCacheRefrescadaEventListener listener = (VistaCacheRefrescadaEventListener)li.next();
                VistaCacheRefrescadaEventObject evObj = new VistaCacheRefrescadaEventObject(this);
                listener.VistaCacheRefrescada(evObj);
            }
        }
    }

    private int getEnergiaAtleta() {
        if( this.energiaAtleta == 0)
            this.avisarEstadoBotones(false);
        return this.energiaAtleta;
    }

    @Override
    public String toString() {
        return "VistaCache{" +
                "listenersEnergiaRecuperada=" + listenersEnergiaRecuperada.size() +
                ", listenersEntrenamientoComenzado=" + listenersEntrenamientoComenzado.size() +
                ", listenersCacheRefrescada=" + listenersCacheRefrescada.size() +
                ", listenersEstadoBotonesModificado=" + listenersEstadoBotonesModificado.size() +
                ", listenersLoginUsuario=" + listenersLoginUsuario.size() +
                ", factoresEntrenamiento=" + factoresEntrenamiento +
                ", tiposEntrenamiento=" + tiposEntrenamiento +
                ", nombreAtleta='" + nombreAtleta + '\'' +
                ", energiaAsignada=" + energiaAsignada +
                ", energiaAtleta=" + energiaAtleta +
                ", idxViewPage=" + idxViewPage +
                ", nivelAtleta=" + nivelAtleta +
                ", tiposRecuperados=" + tiposRecuperados +
                '}';
    }
    //endregion Métodos privados
}
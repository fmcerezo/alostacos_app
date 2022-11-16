package alostacos.juegos.atletismo.alostacos.controlador.entidades;

import alostacos.juegos.atletismo.alostacos.modelo.ConfiguracionJSON;

/**
 * Created by Francisco Cerezo on 19/06/2016.
 */
public class Configuracion extends ControladorPadre {
    private int numeroNiveles;

    //region Gestión Singleton
    private static Configuracion ourInstance = new Configuracion();
    private Configuracion() {}
    public static Configuracion getInstance() { return ourInstance; }
    //endregion

    public int getNumeroNiveles() { return this.numeroNiveles; }

    public boolean loadConfiguracion() {
        ConfiguracionJSON configuracionJSON;
        boolean loaded;

        loaded = false;
        try {
            configuracionJSON = super.com.getConfiguracion();

            if (configuracionJSON != null && configuracionJSON.estado == 1) {
                this.numeroNiveles = configuracionJSON.numeroNiveles;
                loaded = true;
            }
        }
        catch (Exception e){}

        return loaded;
    }

    public void setNumeroNiveles(int numeroNiveles) { this.numeroNiveles = numeroNiveles; }

    @Override
    public String toString() {
        return "Configuracion{" +
                "numeroNiveles=" + numeroNiveles +
                '}';
    }
}

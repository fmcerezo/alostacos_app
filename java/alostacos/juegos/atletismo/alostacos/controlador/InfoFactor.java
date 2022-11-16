package alostacos.juegos.atletismo.alostacos.controlador;

import java.io.Serializable;

import alostacos.juegos.atletismo.alostacos.controlador.entidades.Configuracion;

/**
 * Created by Francisco Cerezo on 26/05/2016.
 */
public class InfoFactor implements Serializable {
    private boolean nivelATope;
    private String nombre;
    private int energiaAsignada, nivel, numeroNiveles;
    private float porcentajeNivel;

    public InfoFactor(String nombre, int nivel, float porcentajeNivel) {
        this.nombre = nombre;
        this.energiaAsignada = 0;
        this.nivel = nivel;
        this.porcentajeNivel = porcentajeNivel;

        //Controlamos si este factor de entrenamiento ya está a tope.
        this.numeroNiveles = Configuracion.getInstance().getNumeroNiveles();
        this.nivelATope = this.numeroNiveles == this.nivel && this.porcentajeNivel == 100;
    }

    public void decrementarEnergiaAsignada() {
        this.energiaAsignada--;
    }

    public int getEnergiaAsignada() {
        return energiaAsignada;
    }

    public int getNivel() {
        return nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPorcentajeNivel() {
        float fValue = this.porcentajeNivel;
        int value = (int) (fValue * 100);
        fValue = value / 100f;

        return String.format("%.2f", fValue);
    }

    public void incrementarEnergiaAsignada() {

        this.energiaAsignada++;
    }

    public boolean isNivelATope() { return nivelATope; }

    @Override
    public String toString() {
        return "InfoFactor{" +
                "nivelATope=" + nivelATope +
                ", nombre='" + nombre + '\'' +
                ", energiaAsignada=" + energiaAsignada +
                ", nivel=" + nivel +
                ", numeroNiveles=" + numeroNiveles +
                ", porcentajeNivel=" + porcentajeNivel +
                '}';
    }
}

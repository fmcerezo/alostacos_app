package alostacos.juegos.atletismo.alostacos.vista.fragments;

/**
 * Created by Francisco Cerezo on 16/06/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import alostacos.juegos.atletismo.alostacos.R;
import alostacos.juegos.atletismo.alostacos.controlador.BasicState;
import alostacos.juegos.atletismo.alostacos.controlador.InfoFactor;
import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.DatosAtletaRecuperadosEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.DatosAtletaRecuperadosEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EstadoBotonesModificadoEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EstadoBotonesModificadoEventObject;
import alostacos.juegos.atletismo.alostacos.vista.EntrenamientoActivity;

// A placeholder fragment containing a simple view.
public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "id_frame";
    private final String BASIC_STATE_FRAGMENT = "BASIC_STATE_FRAGMENT";

    // Returns a new instance of this fragment for the given section number.
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber - 1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;

        try {
            rootView = inflater.inflate(R.layout.fragment_entrenamiento, container, false);
            VistaCache vistaCache = VistaCache.getInstance();

            if(savedInstanceState != null) {
                BasicState bs = (BasicState) savedInstanceState.getSerializable(BASIC_STATE_FRAGMENT);
                vistaCache.setBasicState(bs);
            }

            LinearLayout ver = (LinearLayout) rootView.findViewById(R.id.conVer);
            for (LinearLayout l : this.pintarParteDinamica(rootView, inflater, vistaCache))
                ver.addView(l);

            this.pintarParteEstatica(rootView, vistaCache);
        }
        catch (Exception e) {
            EntrenamientoActivity.muestraExcepcion(this.getActivity(), "onCreateView PlaceholderFragment", e);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable(BASIC_STATE_FRAGMENT, VistaCache.getInstance().getBasicState());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    private LinearLayout[] pintarParteDinamica(View rootView, LayoutInflater inflater, final VistaCache vistaCache) {
        int numero = getArguments().getInt(ARG_SECTION_NUMBER);
        List<InfoFactor> factores = vistaCache.getFactoresEntrenamiento(numero);
        if(factores == null)
            EntrenamientoActivity.muestraExcepcion(this.getContext(), "pintarParteDinamica", new Exception("numero: " + numero));
        LinearLayout l[] = new LinearLayout[factores.size()];

        for ( int x = 0; x < l.length; x++ ) {
            final int idxFactor = x;

            l[x] = (LinearLayout) inflater.inflate(R.layout.factor_entrenamiento, null);
            TextView lblFactor = (TextView) l[x].findViewById(R.id.lblFactor);
            lblFactor.setText(factores.get(x).getNombre());
            TextView lblNivel = (TextView) l[x].findViewById(R.id.lblNivel);

            String descripcionFactor = getResources().getString(R.string.descripcion_factor);
            descripcionFactor = String.format(descripcionFactor, factores.get(x).getNivel(), factores.get(x).getPorcentajeNivel());
            lblNivel.setText(descripcionFactor);

            this.pintarBotones(l[x], vistaCache, idxFactor, rootView, factores.get(x));
        }

        return l;
    }

    private void pintarBotones(LinearLayout l, final VistaCache vistaCache, final int idxFactor, View rootView, InfoFactor factor) {
        final int numero = getArguments().getInt(ARG_SECTION_NUMBER);
        final Button btnMas = (Button) l.findViewById(R.id.btnMas);
        final Button btnMenos = (Button) l.findViewById(R.id.btnMenos);
        final TextView lblPuntos = (TextView) l.findViewById(R.id.lblPuntos);
        final TextView txtEnergia = (TextView) rootView.findViewById(R.id.txtEnergia);
        final Button btnEntrenar = (Button) rootView.findViewById(R.id.btnEntrenar);

        int energiaAsignada = factor.getEnergiaAsignada();
        lblPuntos.setText(String.format("%d", energiaAsignada));

        if(factor.isNivelATope()) {
            btnMas.setEnabled(false);
            // Que sea clicable o no determinará si está temporalmente deshabilitado (falta de energia)
            // o está definitivamente deshabilitado (se ha alcanzado el tope).
            btnMas.setClickable(false);
        }
        else
            btnMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    lblPuntos.setText(String.format("%d", vistaCache.modificarEnergiaAsignada(true, numero, idxFactor)));
                    txtEnergia.setText(String.format("%d", vistaCache.getEnergiaDisponible()));
                    btnMenos.setEnabled(true);
                    btnEntrenar.setEnabled(true);
                }
            });

        btnMenos.setEnabled(energiaAsignada > 0);
        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int puntosAsignados;

                puntosAsignados = vistaCache.modificarEnergiaAsignada(false, numero, idxFactor);
                lblPuntos.setText(String.format("%d", puntosAsignados));
                txtEnergia.setText(String.format("%d", vistaCache.getEnergiaDisponible()));

                btnMenos.setEnabled(puntosAsignados > 0);
                btnEntrenar.setEnabled(vistaCache.isEnergiaAsignada());
            }
        });

        vistaCache.addEstadoBotonesModificadoEventListener(new EstadoBotonesModificadoEventListener() {
            @Override
            public void EstadoBotonesModificado(EstadoBotonesModificadoEventObject args, boolean botonMasEnabled) {
                btnMas.setEnabled(botonMasEnabled);
            }
        });
    }

    private void pintarParteEstatica(final View rootView, final VistaCache vistaCache) {
        final int numero = getArguments().getInt(ARG_SECTION_NUMBER);
        TextView txtTipoEntrenamiento = (TextView) rootView.findViewById(R.id.txtTipoEntrenamiento);
        txtTipoEntrenamiento.setText(vistaCache.getTiposEntrenamiento().get(numero).get(1));

        final Button btnEntrenar = (Button) rootView.findViewById(R.id.btnEntrenar);
        btnEntrenar.setEnabled(vistaCache.isEnergiaAsignada());
        btnEntrenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vistaCache.entrenar(numero);
            }
        });

        final TextView txtEnergia = (TextView) rootView.findViewById(R.id.txtEnergia);
        txtEnergia.setText(String.format("%d", vistaCache.getEnergiaDisponible()));
        final TextView txtNombre = (TextView) rootView.findViewById(R.id.txtNombre);
        txtNombre.setText(vistaCache.getNombreAtleta());
        final TextView txtNivel = (TextView) rootView.findViewById(R.id.txtNivel);
        txtNivel.setText(String.format(getResources().getString(R.string.nivel_atleta), vistaCache.getNivelAtleta()));

        vistaCache.addDatosAtletaRecuperadosEventListener(new DatosAtletaRecuperadosEventListener() {
            @Override
            public void DatosAtletaRecuperados(DatosAtletaRecuperadosEventObject args) {
                try {
                    int e = vistaCache.getEnergiaDisponible();
                    txtEnergia.setText(String.format("%d", e));
                    txtNombre.setText(args.getDatosAtleta().nombre);
                    txtNivel.setText(String.format(getResources().getString(R.string.nivel_atleta), args.getDatosAtleta().nivel));

                    if (e > 0) {
                        btnEntrenar.setEnabled(true);
                        LinearLayout ver = (LinearLayout) rootView.findViewById(R.id.conVer);

                        for (int x = 0; x < ver.getChildCount(); x++) {
                            LinearLayout conFactor = (LinearLayout) ver.getChildAt(x);
                            Button btnMas = (Button) conFactor.findViewById(R.id.btnMas);
                            if (btnMas.isClickable())
                                btnMas.setEnabled(true);
                        }
                    }

                    btnEntrenar.setEnabled(vistaCache.isEnergiaAsignada());
                } catch (Exception e) {
                    EntrenamientoActivity.muestraExcepcion(getContext(), "PlaceholderFragment DatosAtletaRecuperados(DatosAtletaRecuperadosEventObject args)", e);
                }
            }
        });
    }
}
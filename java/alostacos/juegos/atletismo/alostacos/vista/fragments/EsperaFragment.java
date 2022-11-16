package alostacos.juegos.atletismo.alostacos.vista.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import alostacos.juegos.atletismo.alostacos.R;
import alostacos.juegos.atletismo.alostacos.vista.EntrenamientoActivity;

/**
 * Created by Francisco Cerezo on 16/06/2016.
 */
public class EsperaFragment extends Fragment {
    private ProgressBar mProgressView;

    public static EsperaFragment newInstance() { return new EsperaFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot;

        try {
            viewRoot = inflater.inflate(R.layout.fragment_espera, container, false);
            this.mProgressView = (ProgressBar) viewRoot.findViewById(R.id.progress_bar);
        }
        catch (Exception e) {
            viewRoot = null;
            EntrenamientoActivity.muestraExcepcion(this.getActivity(), "onCreateView EsperaFragment", e);
        }

        return viewRoot;
    }
}
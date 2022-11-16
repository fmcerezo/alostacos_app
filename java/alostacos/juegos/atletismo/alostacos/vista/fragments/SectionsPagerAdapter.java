package alostacos.juegos.atletismo.alostacos.vista.fragments;

/**
 * Created by Francisco Cerezo on 16/06/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.Date;
import java.util.Random;

import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private VistaCache vistaCache;
    private boolean bEspera;
    private long baseId;

    public SectionsPagerAdapter(FragmentManager fm, boolean espera) {
        super(fm);
        this.bEspera = espera;
        this.vistaCache = VistaCache.getInstance();
        //Esto es un parche para que el adaptador sea capaz de actualizar los fragments cuando hay más de 1 origen de datos.
        //El ItemId devuelto debe ser aleatorio para que siempre sea distinto.
        baseId = new Random(new Date().getTime()).nextInt() * (espera ? 1:-1);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(this.bEspera)
            return EsperaFragment.newInstance();
        else
            return PlaceholderFragment.newInstance(position + 1);
    }

    //this is called when notifyDataSetChanged() is called
    //Esto es un parche para que el adaptador sea capaz de actualizar los fragments cuando hay más de 1 origen de datos.
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

    //Esto es un parche para que el adaptador sea capaz de actualizar los fragments cuando hay más de 1 origen de datos.
    @Override
    public long getItemId(int position) {
        // give an ID different from position when position has been changed
        return baseId + position;
    }

    @Override
    public int getCount() {
        int count;

        if (this.bEspera)
            count = 1;
        else
            count = this.vistaCache.getTiposEntrenamiento().size();

        return count;
    }
}

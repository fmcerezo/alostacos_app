package alostacos.juegos.atletismo.alostacos.vista;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import alostacos.juegos.atletismo.alostacos.R;
import alostacos.juegos.atletismo.alostacos.controlador.BasicState;
import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;
import alostacos.juegos.atletismo.alostacos.controlador.entidades.Configuracion;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.CierreEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.CierreEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoComenzadoEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.EntrenamientoComenzadoEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.VistaCacheRefrescadaEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.VistaCacheRefrescadaEventObject;
import alostacos.juegos.atletismo.alostacos.vista.fragments.SectionsPagerAdapter;

public class EntrenamientoActivity extends AppCompatActivity {
    private final int interval = 3 * 60 * 1000 + 2000; // 3 minutos y 2 segundos
    private final String BASIC_STATE = "BASIC_STATE";

    private Handler handler;
    private Runnable runnable;
    private boolean bOnCreateRealizado;

    //TODO: Hay que gestionar que no entrene si ha consumido los puntos en la web.
    //TODO: Tendré que hacer que Comunicaciones.entrenar no devuelva true a pelo.
    //TODO: Tendré que ver como coño solucionar el landscape, pero ya con calma. Que los botones de entrenamiento se ensanchen al poner en apaisado.
    // El fallo con el paso a landcape/portrait esta relacionado con los listeners de vistaCache, que dejan de existir en el activity pero siguen existiendo en vistaCache.

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter, mSectionWaiting;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final VistaCache vistaCache;

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_entrenamiento);

            vistaCache = VistaCache.getInstance();

            if(savedInstanceState != null) {
                BasicState bs = (BasicState) savedInstanceState.getSerializable(BASIC_STATE);
                vistaCache.setBasicState(bs);
            }

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setLogo(R.drawable.tacos);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), false);
            mSectionWaiting = new SectionsPagerAdapter(getSupportFragmentManager(), true);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    int energia = VistaCache.getInstance().getEnergiaDisponible();

                    for (int idx = 0; idx < mViewPager.getChildCount(); idx++) {
                        View view = mViewPager.getChildAt(idx);
                        TextView txtEnergia = (TextView) view.findViewById(R.id.txtEnergia);
                        txtEnergia.setText(String.format("%d", energia));

                        Button btnEntrenar = (Button) view.findViewById(R.id.btnEntrenar);
                        btnEntrenar.setEnabled(VistaCache.getInstance().isEnergiaAsignada());
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            // Este evento solo debe ejecutarse si se ha detectado que la autorización del jugador ya no es válida.
            vistaCache.addCierreAplicacionEventListener(new CierreEventListener() {
                @Override
                public void CierreAplicacion(CierreEventObject args) {
                    //Hay que asegurarse de que solo se lanza una vez.
                    vistaCache.initialize();
                    if(handler != null)
                        handler.removeCallbacksAndMessages(null);
                    Intent in = new Intent(EntrenamientoActivity.this, LoginActivity.class);
                    EntrenamientoActivity.this.startActivity(in);
                    finish();
                }
            });

            vistaCache.addVistaCacheRefrescadaEventListener(new VistaCacheRefrescadaEventListener() {
                @Override
                public void VistaCacheRefrescada(VistaCacheRefrescadaEventObject args) {
                    try {
                        mViewPager.setAdapter(mSectionsPagerAdapter);
                        mViewPager.setCurrentItem(vistaCache.getIdxViewPage());
                    }
                    catch (Exception e) {
                        muestraExcepcion("VistaCacheRefrescada(VistaCacheRefrescadaEventObject args)", e);
                    }
                }
            });
            vistaCache.addEntrenamientoComenzadoEventListener(new EntrenamientoComenzadoEventListener() {
                @Override
                public void EntrenamientoComenzado(EntrenamientoComenzadoEventObject args) {
                    try {
                        mViewPager.setAdapter(mSectionWaiting);
                    }
                    catch (Exception e) {
                        muestraExcepcion("EntrenamientoComenzado(EntrenamientoComenzadoEventObject args)", e);
                    }
                }
            });
            this.mViewPager.setAdapter(mSectionWaiting);
            vistaCache.actualizar();
            this.bOnCreateRealizado = true;
        }
        catch (Exception e) {
            this.muestraExcepcion("onCreate", e);
        }
        finally {
            try {
                this.handler = new Handler();
                this.runnable = new Runnable() {
                    public void run() {
                        try {
                            //Actualizamos automáticamente la energia disponible del atleta.
                            if (!VistaCache.getInstance().isEnergiaEnMaximo())
                                VistaCache.getInstance().actualizarEnergia();
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(this, interval);
                        } catch (Exception e) {
                            muestraExcepcion("run()", e);
                        }
                    }
                };

                this.handler.removeCallbacksAndMessages(null);
                this.handler.postDelayed(this.runnable, this.interval);
            }
            catch (Exception e) {
                this.muestraExcepcion("onCreate finally", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onPause() {
        try {
            super.onPause();
            //Si pausamos la app anulamos la llamada pendiente a consultar energia.
            this.handler.removeCallbacksAndMessages(null);
        }
        catch (Exception e) {
            this.muestraExcepcion("onPause", e);
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            // Si acamos de crear la activity ya hemos recabado datos, no tiene sentido lanzar el handler y duplicamos peticiones http.
            if(!this.bOnCreateRealizado) {
                //Cuando se reinicia la app de inmediato se recupera la energia.
                this.handler.removeCallbacksAndMessages(null);
                this.handler.postDelayed(this.runnable, 100);
            }
            else
                this.bOnCreateRealizado = false;
        }
        catch (Exception e) {
            this.muestraExcepcion("onResume", e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable(BASIC_STATE, VistaCache.getInstance().getBasicState());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_entrenamiento, menu);
            return true;
        }
        catch (Exception e) {
            this.muestraExcepcion("onCreateOptionsMenu", e);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        try {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.alostacos.com"));
                this.startActivity(intent);
                return true;
            }
        }
        catch (Exception e) {
            this.muestraExcepcion("onOptionsItemSelected", e);
        }

        return super.onOptionsItemSelected(item);
    }


    public static void muestraExcepcion(Context clase, String nombreMetodo, Exception e) {
        StringBuilder sb = new StringBuilder(clase.getClass().getSimpleName());
        sb.append(" - ");
        sb.append(nombreMetodo);
        sb.append(System.getProperty("line.separator"));
        sb.append(" - ");
        sb.append(e.getMessage());
        sb.append(System.getProperty("line.separator"));
        sb.append(" - ");
        sb.append(e.getLocalizedMessage());
        sb.append(System.getProperty("line.separator"));
        sb.append(" - ");
        sb.append(e.toString());
        sb.append(System.getProperty("line.separator"));
        sb.append(" - ");
        /*if(e.getStackTrace().length > 1) {
            sb.append(e.getStackTrace()[0].toString());
            sb.append(System.getProperty("line.separator"));
        }
        else*/ if(e.getStackTrace().length > 0) {
            for (StackTraceElement t : e.getStackTrace()) {
                sb.append(t.toString());
                sb.append(System.getProperty("line.separator"));
            }
        }
        sb.append(" - ");
        sb.append(VistaCache.getInstance().toStringComAPI());
        sb.append(" - ");
        sb.append(Configuracion.getInstance().toString());
        new AlertDialog.Builder(clase)
                .setTitle("Excepción")
                .setMessage(sb.toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void muestraExcepcion(String nombreMetodo, Exception e) {
        EntrenamientoActivity.muestraExcepcion(this, nombreMetodo, e);
    }
}
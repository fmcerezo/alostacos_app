package alostacos.juegos.atletismo.alostacos.vista;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import alostacos.juegos.atletismo.alostacos.R;
import alostacos.juegos.atletismo.alostacos.controlador.BasicState;
import alostacos.juegos.atletismo.alostacos.controlador.VistaCache;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.LoginUsuarioEventListener;
import alostacos.juegos.atletismo.alostacos.controlador.eventos.LoginUsuarioEventObject;
import alostacos.juegos.atletismo.alostacos.controlador.tareasAsincronas.UsuarioTask;
import alostacos.juegos.atletismo.alostacos.modelo.AlostacosSQLiteHelper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private VistaCache vistaCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            this.vistaCache = VistaCache.getInstance();

            //Si ya está logado pasamos a la activity de entrenamiento.
            if (!this.vistaCache.isLogado()) {
                setContentView(R.layout.activity_login);

                final AlostacosSQLiteHelper db = new AlostacosSQLiteHelper(this);

                this.vistaCache.addLoginUsuarioEventListener(new LoginUsuarioEventListener() {
                    @Override
                    public void LoginUsuario(LoginUsuarioEventObject args) {
                        if (args.getResultadoLoginUsuario() == UsuarioTask.ResultadoLoginUsuario.Correcto) {
                            db.InsertarDatosLoginUsuario(mEmailView.getText().toString(), mPasswordView.getText().toString());
                            lanzaActivityEntrenamiento();
                        } else {
                            showProgress(false);
                            if (args.getResultadoLoginUsuario() == UsuarioTask.ResultadoLoginUsuario.Incorrecto)
                                mPasswordView.setError(getText(R.string.prompt_bad_auth));
                            else
                                mPasswordView.setError(getText(R.string.prompt_error));
                            mPasswordView.requestFocus();
                        }
                    }
                });

                // Set up the login form.
                mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
                mEmailView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, db.GetDatosLoginUsuario()));
                //TODO: Antes de publicar la app debo quitar el user y pwd de user1 que esta puesto a fuego.
                //mEmailView.setText("fran800m@yahoo.com");
                mPasswordView = (EditText) findViewById(R.id.password);
                //mPasswordView.setText("Gorrinete1");
                mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                        if (id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

                Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
                mEmailSignInButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });

                mLoginFormView = findViewById(R.id.login_form);
                mProgressView = findViewById(R.id.login_progress);
            } else
                this.lanzaActivityEntrenamiento();
        }
        catch (Exception e) {
            EntrenamientoActivity.muestraExcepcion(this, "onCreate", e);
        }
    }

    private void lanzaActivityEntrenamiento() {
        Intent in = new Intent(LoginActivity.this, EntrenamientoActivity.class);
        LoginActivity.this.startActivity(in);
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        try {
            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            boolean cancel = false;

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                mPasswordView.requestFocus();
                cancel = true;
            }
            // Check for a valid email address.
            else if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                mEmailView.requestFocus();
                cancel = true;
            }

            if (!cancel) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                this.vistaCache.login(email, password);
            }
        }
        catch (Exception e) {
            EntrenamientoActivity.muestraExcepcion(this, "attemptLogin", e);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        try {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
        catch (Exception e) {
            EntrenamientoActivity.muestraExcepcion(this, "showProgress", e);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}
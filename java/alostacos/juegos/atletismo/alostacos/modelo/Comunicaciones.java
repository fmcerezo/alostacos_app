package alostacos.juegos.atletismo.alostacos.modelo;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import alostacos.juegos.atletismo.alostacos.controlador.BasicState;
import alostacos.juegos.atletismo.alostacos.controlador.excepciones.NoAutorizadoException;

/**
 * Created by Francisco Cerezo on 05/05/2016.
 */
public class Comunicaciones {
    private final int NO_AUTORIZADO = 5;

    private static Comunicaciones ourInstance = new Comunicaciones();
    private ComAPI comAPI;
    private Gson gson;


    private Comunicaciones() {
        this.comAPI = new ComAPI();
        this.gson = new Gson();
    }

    public static Comunicaciones getInstance() {
        return ourInstance;
    }

    public boolean entrenar(String tipoEntrenamiento, String energiaAsignada) throws NoAutorizadoException {
        String respuesta, url;

        url = String.format("%s%s%s", ComAPI.URL, ComAPI.Entidades.Atletas.putEntrenar, tipoEntrenamiento);
        respuesta = this.getHttpResponse(url, "PUT", energiaAsignada);

        return true;
    }

    public BasicState getBasicState() {
        BasicState basicState = new BasicState();

        basicState.hash = this.comAPI.getClaveAPI();
        basicState.tiposEntrenamiento = this.comAPI.getTiposEntrenamiento();

        return basicState;
    }

    public DatosAtletaJSON getEnergiaAtleta() throws NoAutorizadoException {
        DatosAtletaJSON energiaAtletaJ;
        String respuesta, url;

        url = String.format("%s%s", ComAPI.URL, ComAPI.Entidades.Atletas.getEnergia);
        respuesta = this.getHttpResponse(url, "GET");
        energiaAtletaJ = this.gson.fromJson(respuesta, DatosAtletaJSON.class);

        return energiaAtletaJ;
    }

    public List<EntrenamientoAtletaJSON.ValoresEntrenamiento> getEntrenamiento(String tipoEntrenamiento) throws NoAutorizadoException {
        EntrenamientoAtletaJSON valoresEntrenamientoJ;
        String respuesta, url;

        url = String.format("%s%s%s", ComAPI.URL, ComAPI.Entidades.Atletas.getEntrenamiento, tipoEntrenamiento);
        respuesta = this.getHttpResponse(url, "GET");
        valoresEntrenamientoJ = this.gson.fromJson(respuesta, EntrenamientoAtletaJSON.class);

        return valoresEntrenamientoJ.valoresEntrenamiento;
    }

    public List<String> getFactoresEntrenamiento(String tipoEntrenamiento) {
        return this.comAPI.getFactoresEntrenamiento(tipoEntrenamiento);
    }

    public List<List<String>> getTiposEntrenamiento() {
        return this.comAPI.getTiposEntrenamiento();
    }

    public ConfiguracionJSON getConfiguracion() throws NoAutorizadoException {
        ConfiguracionJSON configuracionJ;
        String respuesta, url;

        url = String.format("%s%s", ComAPI.URL, ComAPI.Entidades.Configuraciones.getConfiguraciones);
        respuesta = this.getHttpResponse(url, "GET");
        configuracionJ = this.gson.fromJson(respuesta, ConfiguracionJSON.class);

        return configuracionJ;
    }

    public boolean loadFactoresEntrenamiento(String tipoEntrenamiento) throws NoAutorizadoException {
        boolean returnValue;
        TiposEntrenamientoJSON factotesEntrenamientoJ;
        String respuesta, url;

        url = String.format("%s%s%s", ComAPI.URL, ComAPI.Entidades.Entrenamientos.getEntrenamientos, tipoEntrenamiento);
        respuesta = this.getHttpResponse(url, "GET");
        factotesEntrenamientoJ = this.gson.fromJson(respuesta, TiposEntrenamientoJSON.class);
        this.comAPI.setFactoresEntrenamiento(tipoEntrenamiento, factotesEntrenamientoJ.factores);

        return true;
    }

    public boolean loadTiposEntrenamiento() {
        boolean returnValue;
        EntrenamientosJSON entrenamientosJ;
        String respuesta, url;

        try {
            url = String.format("%s%s", ComAPI.URL, ComAPI.Entidades.Entrenamientos.getEntrenamientos);
            respuesta = this.getHttpResponse(url, "GET");
            entrenamientosJ = this.gson.fromJson(respuesta, EntrenamientosJSON.class);
            this.comAPI.setTiposEntrenamiento(entrenamientosJ.tipos);

            returnValue = entrenamientosJ.estado == 1;
        }
        catch (Exception e) {
            returnValue = false;
        }

        return returnValue;
    }

    public boolean login(String user, String pwd) {
        LoginJSON loginJ;
        boolean logado;
        String body, respuesta, url;

        try {
            body = String.format("{\"login\":\"%s\",\"pwd\":\"%s\"}", user, pwd);
            url = String.format("%s%s", ComAPI.URL, ComAPI.Entidades.Usuarios.postUsuariosLogin);
            respuesta = this.getHttpResponse(url, "POST", body);
            loginJ = this.gson.fromJson(respuesta, LoginJSON.class);
            logado = loginJ.estado == 1 && loginJ.hash != null;
            if (logado)
                this.comAPI.setClaveAPI(loginJ.hash);
        } catch (NoAutorizadoException e) {
            //En teoría esta excepción no es posible en el login.
            logado = false;
        }

        return logado;
    }

    public void setBasicState(BasicState basicState) {
        this.comAPI.setClaveAPI(basicState.hash);
        this.comAPI.setTiposEntrenamiento(basicState.tiposEntrenamiento);
    }

    public String toStringComAPI() {
        return this.comAPI.toString();
    }


    private String getHttpResponse(String url, String method) throws NoAutorizadoException {
        return this.getHttpResponse(url, method, "");
    }

    private String getHttpResponse(String url, String method, String body) throws NoAutorizadoException {
        BufferedReader reader;
        HttpURLConnection urlConnection;
        InputStream inputStream;
        OutputStream os;
        RespuestaJSON comprobacion;
        String line, returnValue;
        StringBuilder stringBuilder;
        URL urlObject;
        byte[] outputInBytes;

        urlConnection = null;

        try {
            if (!this.comAPI.getClaveAPI().isEmpty())
                url = String.format("%1$s&hash=%2$s", url, this.comAPI.getClaveAPI());

            urlObject = new URL(url);

            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);

            /* Ha habido que dejar autenticación básica porque los inútiles del servidor PHP no tienen activado los módulos Apache.
            if (!this.comAPI.getClaveAPI().isEmpty())
                urlConnection.setRequestProperty("Authorization", String.format("Basic %1$s", this.comAPI.getClaveAPI()));
            */

            if (body != null && !body.trim().isEmpty()) {
                outputInBytes = body.getBytes("UTF-8");
                os = urlConnection.getOutputStream();
                os.write(outputInBytes);
                os.close();
            }

            try {
                inputStream = urlConnection.getInputStream();
            } catch (FileNotFoundException e) {
                inputStream = urlConnection.getErrorStream();
            } catch (IOException e) {
                /* Modifico aquí control de código de login incorrecto (No autorizado HTTP 401) para no volver a tocar el servidor PHP,
                   que de todas formas el servidor no soporta autenticación, así de geniales son. */
                if (urlConnection.getResponseCode() == 401)
                    inputStream = urlConnection.getErrorStream();
                else
                    throw e;
            }

            stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            inputStream.close();
            reader.close();
            returnValue = stringBuilder.toString();
            comprobacion = this.gson.fromJson(returnValue, RespuestaJSON.class);
            if (comprobacion.estado == NO_AUTORIZADO)
                throw new NoAutorizadoException();
        } catch (NoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            returnValue = e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return returnValue;
    }


    private class ComAPI {
        public static final String URL = "http://api.alostacos.com/";

        private HashMap<String, List<String>> factoresEntrenamiento;
        private List<List<String>> tiposEntrenamiento;
        private String claveAPI;


        public ComAPI() {
            this.claveAPI = "";
            this.factoresEntrenamiento = new HashMap<String, List<String>>(0);
            this.tiposEntrenamiento = Arrays.asList();
        }


        public String getClaveAPI() {
            return this.claveAPI;
        }

        public List<String> getFactoresEntrenamiento(String tipoEntrenamiento) {
            return this.factoresEntrenamiento.get(tipoEntrenamiento);
        }

        public List<List<String>> getTiposEntrenamiento() { return this.tiposEntrenamiento; }

        public void setClaveAPI(String clave) {
            this.claveAPI = clave;
        }

        public void setFactoresEntrenamiento(String tipo, List<String> factores) {
            this.factoresEntrenamiento.put(tipo, factores);
        }

        public void setTiposEntrenamiento(List<List<String>> vTiposEntrenamiento) { this.tiposEntrenamiento = vTiposEntrenamiento; }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            String newLine = System.getProperty("line.separator");

            result.append( this.getClass().getName() );
            result.append( " Object {" );
            result.append(newLine);

            //determine fields declared in this class only (no fields of superclass)
            Field[] fields = this.getClass().getDeclaredFields();

            //print field names paired with their values
            for ( Field field : fields  ) {
                result.append("  ");
                try {
                    result.append( field.getName() );
                    result.append(": ");
                    //requires access to private field:
                    result.append( field.get(this) );
                } catch ( IllegalAccessException ex ) {
                    System.out.println(ex);
                }
                result.append(newLine);
            }
            result.append("}");

            return result.toString();
        }


        public class Entidades {
            public class Atletas {
                public static final String getEnergia = "atletas/energia";
                public static final String getEntrenamiento = "atletas/entrenamiento/";
                public static final String putEntrenar = "atletas/entrenar/"; //Ejemplo body: [1,0,0,0,0]
            }
            public class Configuraciones {
                public static final String getConfiguraciones = "configuraciones/";
            }
            public class Entrenamientos {
                public static final String getEntrenamientos = "entrenamientos/";
            }
            public class Usuarios {
                public static final String postUsuariosLogin = "usuarios/login"; //Ejemplo body: {"login":"user","pwd":"123456"}
            }
        }
    }
}
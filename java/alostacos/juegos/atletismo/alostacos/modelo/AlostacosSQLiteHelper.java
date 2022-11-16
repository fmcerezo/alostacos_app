package alostacos.juegos.atletismo.alostacos.modelo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francisco Cerezo on 30/06/2016.
 */
public class AlostacosSQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private String sql;

    public AlostacosSQLiteHelper(Context contexto)
    {
        //TODO: Agregar numero de version a AndroidManifest.xml y usarlo.
        super(contexto, "AlostacosDB", null, 1);
        this.database = this.getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db)
    {
        this.sql = "CREATE TABLE DatosLoginUsuario ";
        this.sql += "(IdUsuario INTEGER PRIMARY KEY, Login TEXT, Password TEXT, UNIQUE(Login) ON CONFLICT REPLACE);";
        db.execSQL(this.sql);

        this.sql = "CREATE TABLE Excepciones ";
        this.sql += "(IdExcepcion INTEGER PRIMARY KEY, Texto TEXT);";
        db.execSQL(this.sql);
    }

    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva)
    {
        db.execSQL("DROP TABLE IF EXISTS DatosLoginUsuario;");
        db.execSQL("DROP TABLE IF EXISTS Excepciones;");
        this.onCreate(db);
    }

    public List<String> GetDatosLoginUsuario()
    {
        List<String> lista = new ArrayList<>();

        try {
            this.sql = "select IdUsuario, Login, Password from DatosLoginUsuario order by Login ASC;";
            Cursor rs = this.database.rawQuery(this.sql, null);

            if (rs.moveToFirst()) {
                do {
                    lista.add(rs.getString(1));
                } while (rs.moveToNext());
            }

            rs.close();
        }
        catch (Exception e) {
            lista.add("Problema al recuperar logins.");
        }

        return lista;
    }

    public List<String> GetExcepciones()
    {
        List<String> lista = new ArrayList<>();

        try {
            this.sql = "select Texto from Excepciones order by IdExcepcion ASC;";
            Cursor rs = this.database.rawQuery(this.sql, null);

            if (rs.moveToFirst()) {
                do {
                    lista.add(rs.getString(0));
                } while (rs.moveToNext());
            }

            rs.close();
        }
        catch (Exception e) {
            lista.add("Problema al recuperar excepciones.");
        }

        return lista;
    }

    public void InsertarDatosLoginUsuario(String login, String password)
    {
        String j;
        try {
            String sql;

            sql = "INSERT INTO DatosLoginUsuario (Login, Password) VALUES ('%s', '%s');";
            sql = String.format(sql, login, password);

            this.database.execSQL(sql);
        }
        catch (Exception e) {
            j = e.getMessage();
        }
    }

    public void InsertarExcepcion(String excepcion)
    {
        String j;
        try {
            String sql;

            sql = "INSERT INTO Excepciones (Texto) VALUES ('%s');";
            sql = String.format(sql, excepcion);

            this.database.execSQL(sql);
        }
        catch (Exception e) {
            j = e.getMessage();
        }
    }
}
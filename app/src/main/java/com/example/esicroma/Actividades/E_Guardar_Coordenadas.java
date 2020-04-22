package com.example.esicroma.Actividades;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esicroma.API.ClienteRetrofit;
import com.example.esicroma.Modelo.Lista_Lotes;
import com.example.esicroma.Modelo.Post_Localizacion;
import com.example.esicroma.Modelo.UserLote;
import com.example.esicroma.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class E_Guardar_Coordenadas extends AppCompatActivity {

    TextView Latitud, Longitud;
    Button Guardar;
    LocationManager ubicacion;

    Spinner Lotes_Spinner, Medidas;
    List<UserLote> lista_lotes;

    Double Latitud_D, Longitud_D;
    Integer cm;

    Post_Localizacion post_localizacion = new Post_Localizacion();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_guardar_localizacion);
        getSupportActionBar().setTitle("Guardar Localizacion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Permiso_De_Localizacion();

        Guardar = findViewById(R.id.obtener);
        Lotes_Spinner = findViewById(R.id.Lotes_Spinner);
        Medidas = findViewById(R.id.medidas);

        nuevo_lista();
        lista_de_medidas();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Obtener_Coordenadas();
                Guardar_Informacion();
            }
        });

    }

    public void Permiso_De_Localizacion() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(E_Guardar_Coordenadas.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(E_Guardar_Coordenadas.this, "GPS permission allws us to Acces GPS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(E_Guardar_Coordenadas.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Obtener_Coordenadas() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
        }

        ubicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (ubicacion != null){
            Latitud_D = loc.getLatitude();
            Longitud_D = loc.getLongitude();
            post_localizacion.setLatitud(Latitud_D.toString());
            post_localizacion.setLongitud(Longitud_D.toString());
            Toast.makeText(E_Guardar_Coordenadas.this, "Ubicación Guardada con Exito. Latitud: " + Latitud_D + " Longitud: " + Longitud_D, Toast.LENGTH_LONG).show();
        }
    }

    public void nuevo_lista(){
        Call<List<UserLote>> call = ClienteRetrofit.getInstance().getApi().Informacion_Lotes(A_IniciarSesion.Datos_De_Usuario.getId());
        call.enqueue(new Callback<List<UserLote>>() {
            @Override
            public void onResponse(Call<List<UserLote>> call, Response<List<UserLote>> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(E_Guardar_Coordenadas.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                lista_lotes = response.body();

                ArrayAdapter<UserLote> adapter = new ArrayAdapter<UserLote>(E_Guardar_Coordenadas.this, android.R.layout.simple_spinner_item, lista_lotes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Lotes_Spinner.setAdapter(adapter);

                Lotes_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UserLote userLote = (UserLote) parent.getSelectedItem();
                        post_localizacion.setLote_id(userLote.getId());
                        //displayUserData(userLote);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<UserLote>> call, Throwable t) {
                Toast.makeText(E_Guardar_Coordenadas.this, "Error al Obtener la Lista", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void displayUserData(UserLote userLote){
        String Lote_Info = "Nombre: " + userLote.getNombre() + "\nUso de suelo: " + userLote.getUsoSuelo();
        Toast.makeText(this, Lote_Info, Toast.LENGTH_LONG).show();
    }

    public void lista_de_medidas(){
        Integer[] centimetros = {15, 25, 50, 100};

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(E_Guardar_Coordenadas.this, android.R.layout.simple_spinner_item, centimetros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Medidas.setAdapter(adapter);

        Medidas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cm = (Integer) parent.getSelectedItem();
                post_localizacion.setProfundidad(cm);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void Guardar_Informacion(){
        post_localizacion.setUser_id(A_IniciarSesion.Datos_De_Usuario.getId());

        Call<Post_Localizacion> call = ClienteRetrofit.getInstance().getApi().Datos_De_Muestas(post_localizacion.getLatitud(), post_localizacion.getLongitud(), post_localizacion.getLote_id(), post_localizacion.getProfundidad(), post_localizacion.getUser_id());
        call.enqueue(new Callback<Post_Localizacion>() {
            @Override
            public void onResponse(Call<Post_Localizacion> call, Response<Post_Localizacion> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(E_Guardar_Coordenadas.this, "Error al guardar los datos", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(E_Guardar_Coordenadas.this, "Se a Guardado Exitosamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Post_Localizacion> call, Throwable t) {
                Toast.makeText(E_Guardar_Coordenadas.this, "Error en la conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*public void Info_Lotes(){
        Call<Lista_Lotes> call = ClienteRetrofit.getInstance().getApi().Obtener_Lotes(A_IniciarSesion.Datos_De_Usuario.getId());
        call.enqueue(new Callback<Lista_Lotes>() {
            @Override
            public void onResponse(Call<Lista_Lotes> call, Response<Lista_Lotes> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(E_Guardar_Coordenadas.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                lista_lotes = response.body().getUserLotes();

                for (UserLote l : lista_lotes){
                    System.out.println("Nombre: " + l.getNombre());
                }

                ArrayAdapter<UserLote> adapter = new ArrayAdapter<UserLote>(E_Guardar_Coordenadas.this, android.R.layout.simple_spinner_item, lista_lotes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Lotes_Spinner.setAdapter(adapter);

                Lotes_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UserLote userLote = (UserLote) parent.getSelectedItem();
                        displayUserData(userLote);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



                String[] Nombre_De_Los_Lotes = new String[lista_lotes.size()];
                int posicion = 0;

                for (UserLote l : lista_lotes){
                    Nombre_De_Los_Lotes[posicion] = l.getNombre();
                    posicion++;
                }

                for (int x = 0; x < Nombre_De_Los_Lotes.length; x++){
                    System.out.println("Nombre: " + Nombre_De_Los_Lotes[x]);
                }

            }

            @Override
            public void onFailure(Call<Lista_Lotes> call, Throwable t) {
                System.out.println("XNXX " + t.getMessage());
                Toast.makeText(E_Guardar_Coordenadas.this, "" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //return lista_lotes;
    }*/


}

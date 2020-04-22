package com.example.esicroma.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.esicroma.API.ClienteRetrofit;
import com.example.esicroma.Modelo.A_Iniciar_Sesion_Response;
import com.example.esicroma.Modelo.Localizacion;
import com.example.esicroma.Modelo.User;
import com.example.esicroma.R;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class A_IniciarSesion extends AppCompatActivity {

    EditText Correo, Contraseña;
    Button   iniciar_sesion;
    public static User Datos_De_Usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_login);

        EnableRuntimePermissionToAccessCamera();

        Correo = (EditText) findViewById(R.id.Correo);
        Contraseña = (EditText) findViewById(R.id.pass);
        iniciar_sesion = (Button) findViewById(R.id.entrar);

        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iniciar_Sesion();
            }
        });
    }

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(A_IniciarSesion.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(A_IniciarSesion.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        }else {

            ActivityCompat.requestPermissions(A_IniciarSesion.this,new String[]{Manifest.permission.CAMERA}, 1);

        }
    }


    /*private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        latitud.setText("Localización agregada");
        direccion.setText("");
    }*/

    private boolean Validar_Email(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void Ir_A_La_Pantalla_de_Perfil(){
        Intent Pantalla_De_Perfil  = new Intent(this, B_Perfil.class);
        startActivity(Pantalla_De_Perfil);
    }

    private void Iniciar_Sesion(){
        String correo = Correo.getText().toString();
        String contraseña = Contraseña.getText().toString();

        if (correo.isEmpty()){
            Toast.makeText(this, "El campo del correo esta vacio", Toast.LENGTH_LONG).show();
        }
        if (contraseña.isEmpty()){
            Toast.makeText(this, "El campo del contraseña esta vacio", Toast.LENGTH_LONG).show();
        }
        if (!Validar_Email(correo)){
            Toast.makeText(this, "Correo invalido", Toast.LENGTH_LONG).show();
        }
        else if (!contraseña.isEmpty()){
            Call<A_Iniciar_Sesion_Response> call = ClienteRetrofit.getInstance().getApi().Datos_Login(correo, contraseña);
            call.enqueue(new Callback<A_Iniciar_Sesion_Response>() {
                @Override
                public void onResponse(Call<A_Iniciar_Sesion_Response> call, Response<A_Iniciar_Sesion_Response> response) {
                    if (!response.isSuccessful()){
                        if (response.code() == 403)
                            Toast.makeText(A_IniciarSesion.this, "Credenciales Incorrectas", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(A_IniciarSesion.this, "" + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Datos_De_Usuario = response.body().getUser();
                    Toast.makeText(A_IniciarSesion.this, "Sesion Iniciada", Toast.LENGTH_LONG).show();
                    Ir_A_La_Pantalla_de_Perfil();



                }

                @Override
                public void onFailure(Call<A_Iniciar_Sesion_Response> call, Throwable t) {
                    //Toast.makeText(A_IniciarSesion.this, "" + t.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(A_IniciarSesion.this, "Error en la conexión", Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}

package com.example.esicroma.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.esicroma.API.ClienteRetrofit;
import com.example.esicroma.Modelo.User;
import com.example.esicroma.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class B_Perfil extends AppCompatActivity{

    private TextView Nombre_Completo;
    private Button Crear_Muestra;
    private Button Guardar_Lozalizacion;
    User Datos_De_Usuario = A_IniciarSesion.Datos_De_Usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b__perfil);

        Nombre_Completo = findViewById(R.id.Nombre_Completo);
        Nombre_Completo.setText(Datos_De_Usuario.getNombre() + " " + Datos_De_Usuario.getApe_p() + " " + Datos_De_Usuario.getApe_m());

        Crear_Muestra = findViewById(R.id.Crear_Muestra);
        Crear_Muestra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ir__A_La_Pantalla_De_Crear_Muestra();
            }
        });

        Guardar_Lozalizacion = findViewById(R.id.Guardar_Localizacion);
        Guardar_Lozalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ir_A_La_Pantalla_De_Guardar_Localizacion();
            }
        });
        //getUsuarios();
    }

    public void Ir__A_La_Pantalla_De_Crear_Muestra(){
        Intent Pantalla_De_Crear_Muestra = new Intent(this, C_Crear_Muestra.class);
        startActivity(Pantalla_De_Crear_Muestra);
    }

    public void  Ir_A_La_Pantalla_De_Guardar_Localizacion(){
        Intent Pantalla_De_Guardar_Localizacion = new Intent(this, E_Guardar_Coordenadas.class);
        startActivity(Pantalla_De_Guardar_Localizacion);
    }

    /*private void getUsuarios(){
        Call<List<User>> call = ClienteRetrofit.getInstance().getApi().getUsuario();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(!response.isSuccessful()){
                    usaurios.setText("Codigo: " + response.code());
                    return;
                }

                List<User> lista_User = response.body();

                for (User user : lista_User){
                    String content = "";
                    content += "Nombre: " + user.getNombre() + lista_User.size();
                    usaurios.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                usaurios.setText(t.getMessage());
            }
        });
    }*/
}

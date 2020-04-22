package com.example.esicroma.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esicroma.API.ClienteRetrofit;
import com.example.esicroma.Modelo.Carga_De_Datos_IA;
import com.example.esicroma.Modelo.Cromann;
import com.example.esicroma.R;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class D_Analisis_Croma extends AppCompatActivity {

    ImageView croma_img;
    TextView Oxigeno, Mat_Organica, Minerales, Nitrogeno, Rompimiento, Mat_Viva, Act_Biologia, Nitro_Organico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d__analisis__croma);

        getSupportActionBar().setTitle("Resultado del Croma");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Oxigeno = findViewById(R.id.Oxigeno);
        Mat_Organica = findViewById(R.id.Mat_Organica);
        Minerales = findViewById(R.id.Minerales);
        Nitrogeno = findViewById(R.id.Nitrogeno);
        Rompimiento = findViewById(R.id.Rompimiento);
        Mat_Viva = findViewById(R.id.Mat_Viva);
        Act_Biologia = findViewById(R.id.Act_Biologica);
        Nitro_Organico = findViewById(R.id.Nitro_Organico);

        croma_img = findViewById(R.id.croma_img);

        croma_img.setImageBitmap(C_Crear_Muestra.bitmap);

        subir_Imagen();
        Cambio_de_Colores();


    }

    public void Cambio_de_Colores (){
        String resultado = C_Crear_Muestra.respuesta;
        System.out.println("Analisis" + resultado.charAt(7));
        int[] i = {7,10,13,16,19,22,25,28};
        Boolean[] bool = {false,false,false,false,false,false,false,false};

        if (resultado.charAt(7) == '1'){
            Oxigeno.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndOxg(true);
        }
        if (resultado.charAt(10) == '1'){
            Mat_Organica.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndMatOrg(true);
        }
        if (resultado.charAt(13) == '1'){
            Minerales.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndTransSist(true);
        }
        if (resultado.charAt(16) == '1'){
            Nitrogeno.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndNElem(true);
        }
        if (resultado.charAt(19) == '1'){
            Rompimiento.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndRomp(true);
        }
        if (resultado.charAt(22) == '1'){
            Mat_Viva.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndMatViva(true);
        }
        if (resultado.charAt(25) == '1'){
            Act_Biologia.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndBio(true);
        }
        if (resultado.charAt(28) == '1'){
            Nitro_Organico.setBackgroundResource(R.drawable.encender);
            C_Crear_Muestra.crm.getCromann().get(0).setIndProN(true);
        }

        for (int x=0; x<i.length; x++){
            if (resultado.charAt(i[x]) == '1')
                bool[x] = true;
        }

        Actu_CRM(C_Crear_Muestra.crm.getCromann().get(0).getId(),  C_Crear_Muestra.crm.getCromann().get(0));
        System.out.println("Materia " + C_Crear_Muestra.crm.getCromann().get(0).getIndMatOrg());
        // C_Crear_Muestra.crm.getCromann().get(0).getId()
        //Actualizar_Cromann(C_Crear_Muestra.crm.getCromann().get(0).getId(), bool[0], bool[1], bool[2], bool[3], bool[4], bool[5], bool[6], bool[7]);

    }

    public void Actu_CRM(int id, Cromann cromann){
        Call<Carga_De_Datos_IA> call = ClienteRetrofit.getInstance().getApi().ia(C_Crear_Muestra.crm.getCromann().get(0).getId(), C_Crear_Muestra.crm.getCromann().get(0));
        call.enqueue(new Callback<Carga_De_Datos_IA>() {
            @Override
            public void onResponse(Call<Carga_De_Datos_IA> call, Response<Carga_De_Datos_IA> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(D_Analisis_Croma.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                System.out.println("INTELIGENCIA " + response.body().getMensaje());
            }

            @Override
            public void onFailure(Call<Carga_De_Datos_IA> call, Throwable t) {
                System.out.println("Medico " + t.getMessage());
            }
        });
    }

    public void subir_Imagen(){
        RequestBody fileReqBody = RequestBody.create(MultipartBody.FORM, "imagen");
        RequestBody filep = RequestBody.create(MediaType.parse(getContentResolver().getType(C_Crear_Muestra.photoURI)), C_Crear_Muestra.photoFile);

        MultipartBody.Part file = MultipartBody.Part.createFormData("img", C_Crear_Muestra.photoFile.getName(), filep);


        Call<ResponseBody> call = ClienteRetrofit.getInstance().getApi().cargar_Imagen(C_Crear_Muestra.crm.getCromann().get(0).getId(), file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(D_Analisis_Croma.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                System.out.println("Perro " + response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void Actualizar_Cromann(int id, Boolean indOxg, Boolean indMatOrg, Boolean indTransSist, Boolean indNElem, Boolean indRomp, Boolean indMatViva, Boolean indBio, Boolean indProN){
        Call<ResponseBody> call = ClienteRetrofit.getInstance().getApi().IA(id, indOxg, indMatOrg, indTransSist, indNElem, indRomp, indMatViva, indBio, indProN);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(D_Analisis_Croma.this, "" + response.code() + " Error de datos " + response.message(), Toast.LENGTH_LONG).show();
                    return;
                }

                String h = response.body().toString();
                System.out.println("INTELIGENCIA "  + h);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}

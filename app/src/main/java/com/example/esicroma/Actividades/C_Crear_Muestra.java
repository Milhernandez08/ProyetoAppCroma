package com.example.esicroma.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;

import com.example.esicroma.API.ClienteRetrofit;
import com.example.esicroma.Modelo.Localizacion;
import com.example.esicroma.Modelo.Post_Localizacion;
import com.example.esicroma.Modelo.Respuesta_Croma;
import com.example.esicroma.Modelo.Respuesta_IA;
import com.example.esicroma.Modelo.Respuesta_Muestra;
import com.example.esicroma.Modelo.Respuesta_Municipio;
import com.example.esicroma.Modelo.UserLote;
import com.example.esicroma.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class C_Crear_Muestra extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    Spinner spinner;
    Spinner cromas_Spinner;

    String pathImage;
    Button btnTakeFoto, btnUploadFoto;
    ImageView imageView;

    public static Bitmap bitmap;
    public static String respuesta;
    public static UserLote userLote;
    public static Respuesta_Croma crm;


    public static Uri photoURI;
    public static File photoFile;

    List<UserLote> Lista_De_Lotes;
    List<Respuesta_Croma> cromann;
    Respuesta_Municipio Region;



    TextView Nombre_Lote, id_Muestra, Latitud_CC, Longitud_CC, Profundidad_CC;
    TextView Pais, Estado, Municipio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c__crear__muestra);

        spinner = findViewById(R.id.Localizaciones);
        cromas_Spinner = findViewById(R.id.Lista_Muestras);

        getSupportActionBar().setTitle("Crear Nueva Muestra");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Lista_De_Lugares();

        /************ CAMARA ***************/
        imageView = findViewById(R.id.foto_img);
        btnTakeFoto = findViewById(R.id.Camara);
        btnUploadFoto = findViewById(R.id.Analizar);

        btnTakeFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent();
            }
        });

        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFile();
                //Ir_A_La_Pantalla_De_Resultados();
            }
        });

        /*********** DATOS EN TEXTVIEW ***********/
        Nombre_Lote = findViewById(R.id.Nombre_Lote);
        id_Muestra = findViewById(R.id.id_Muestra);
        Latitud_CC = findViewById(R.id.Latitud_CC);
        Longitud_CC = findViewById(R.id.Longitud_CC);
        Profundidad_CC = findViewById(R.id.Profundidad_CC);

        /****** REGION ******/
        Pais = findViewById(R.id.Pais);
        Estado = findViewById(R.id.Estado);
        Municipio = findViewById(R.id.Municipio);
    }

    public void Regresar_A_La_Pantalla_De_Perfil(){
        Intent Pantalla_De_Perfil = new Intent(this, B_Perfil.class);
        startActivity(Pantalla_De_Perfil);
    }

    public void Ir_A_La_Pantalla_De_Resultados(){
        Intent Pantalla_De_Resultados = new Intent(this, D_Analisis_Croma.class);
        startActivity(Pantalla_De_Resultados);
    }



    public void takePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null){
                photoURI = FileProvider.getUriForFile(this, "com.example.esicroma", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                btnUploadFoto.setEnabled(true);
            }
        }
    }

    public File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        pathImage = imageFile.getAbsolutePath();
        Log.e("ruta imagen: ", pathImage);
        return imageFile;
    }

    public void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, 1);
    }

    public void postFile(){

        System.out.println("file: " + photoFile);

        RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);

        MultipartBody.Part requestImage = MultipartBody.Part.createFormData("ourfile", photoFile.getName(), fileReqBody);
        System.out.println("file imagen " + requestImage);

        Call<ResponseBody> call = ClienteRetrofit.getInstance2().getApi().upload(requestImage);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.code());

                if (!response.isSuccessful()){
                    Toast.makeText(C_Crear_Muestra.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                //System.out.println("Repuesta: " + response.body().source());


                try{
                    respuesta = response.body().source() + "";
                    if (response.isSuccessful()){
                        Ir_A_La_Pantalla_De_Resultados();
                    }
                }
                catch (Exception e){

                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(C_Crear_Muestra.this, "" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){

            Uri uri = Uri.parse(pathImage);
            Log.e("photoUri on activiti.->" , uri+"");

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                Log.e("bitmap on activity.->" , bitmap+"");
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void Lista_De_Lugares(){
        Call<List<UserLote>> call = ClienteRetrofit.getInstance().getApi().Informacion_Lotes(A_IniciarSesion.Datos_De_Usuario.getId());
        call.enqueue(new Callback<List<UserLote>>() {
            @Override
            public void onResponse(Call<List<UserLote>> call, Response<List<UserLote>> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(C_Crear_Muestra.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Lista_De_Lotes = response.body();

                ArrayAdapter<UserLote> adapter = new ArrayAdapter<UserLote>(C_Crear_Muestra.this, android.R.layout.simple_spinner_item, Lista_De_Lotes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        userLote = (UserLote) parent.getSelectedItem();
                        Informacion_Muestra(userLote.getId());
                        displayUserData(userLote);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<UserLote>> call, Throwable t) {

            }
        });
    }

    public void Datos_Region(){
        Call<Respuesta_Municipio> call = ClienteRetrofit.getInstance().getApi().Obtener_Region(A_IniciarSesion.Datos_De_Usuario.getMunicipio_id());
        call.enqueue(new Callback<Respuesta_Municipio>() {
            @Override
            public void onResponse(Call<Respuesta_Municipio> call, Response<Respuesta_Municipio> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(C_Crear_Muestra.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Region = response.body();

                Pais.setText("");
                Pais.setText(Region.getPais().getNombre());

                Estado.setText("");
                Estado.setText(Region.getMunicipioestado().getNombre());

                Municipio.setText("");
                Municipio.setText(Region.getMunicipio().getNombre());
            }

            @Override
            public void onFailure(Call<Respuesta_Municipio> call, Throwable t) {
                Toast.makeText(C_Crear_Muestra.this, "Error al Cargar los Datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Informacion_Muestra(int id){
        Call<List<Respuesta_Croma>> call = ClienteRetrofit.getInstance().getApi().Datos_Croma(id);
        call.enqueue(new Callback<List<Respuesta_Croma>>() {
            @Override
            public void onResponse(Call<List<Respuesta_Croma>> call, Response<List<Respuesta_Croma>> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(C_Crear_Muestra.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                cromann = response.body();

                ArrayAdapter<Respuesta_Croma> adapter = new ArrayAdapter<Respuesta_Croma>(C_Crear_Muestra.this, android.R.layout.simple_spinner_item, cromann);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cromas_Spinner.setAdapter(adapter);

                cromas_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        crm = (Respuesta_Croma) parent.getSelectedItem();

                        id_Muestra.setText("");
                        id_Muestra.setText("" + crm.getCromann().get(0).getMuestraId());

                        Datos_de_la_Muestra(crm.getCromann().get(0).getId());

                        Profundidad_CC.setText("");
                        Profundidad_CC.setText(crm.getProfundidad());

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<List<Respuesta_Croma>> call, Throwable t) {
                Toast.makeText(C_Crear_Muestra.this, "Error al cargar la información", Toast.LENGTH_LONG).show();
                System.out.println("GRAVE: " + t.getMessage());
            }
        });
    }

    public void Datos_de_la_Muestra(int id){
        Call<Respuesta_Muestra> call = ClienteRetrofit.getInstance().getApi().informacion_Croma(id);
        call.enqueue(new Callback<Respuesta_Muestra>() {
            @Override
            public void onResponse(Call<Respuesta_Muestra> call, Response<Respuesta_Muestra> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(C_Crear_Muestra.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Respuesta_Muestra res = response.body();
                Latitud_CC.setText("");
                Latitud_CC.setText(FormatoDecimal(res.getMuestra().getLocalizacion().getLatitud()));

                Longitud_CC.setText("");
                Longitud_CC.setText(FormatoDecimal(res.getMuestra().getLocalizacion().getLongitud()));

            }

            @Override
            public void onFailure(Call<Respuesta_Muestra> call, Throwable t) {
                Toast.makeText(C_Crear_Muestra.this, "Error al cargar la información", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String FormatoDecimal(String num){
        String formato = "";
        //String num2 = String.valueOf(num);
        int numero_De_Decimales = 6;
        int x = 0;

        for(int i=0; i<num.length(); i++){

            if (num.charAt(i) == '.')
                x = 1;

            formato = formato + num.charAt(i);

            if (x == 1)
                numero_De_Decimales--;

            if (numero_De_Decimales == 0)
                break;
        }

        return formato;
    }

    public void displayUserData(UserLote userLote){
        Nombre_Lote.setText("");
        Nombre_Lote.setText(userLote.getNombre());

        /*Latitud_CC.setText("");
        Latitud_CC.setText(userLote.getLocalizacion().getLatitud());

        Longitud_CC.setText("");
        Longitud_CC.setText(userLote.getLocalizacion().getLongitud());*/

        Datos_Region();
    }
}

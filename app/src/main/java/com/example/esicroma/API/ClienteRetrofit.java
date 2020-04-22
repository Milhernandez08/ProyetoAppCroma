package com.example.esicroma.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    private static final String BASE_URL = "https://morning-stream-44468.herokuapp.com/api/v2/"; //https://pure-wildwood-42123.herokuapp.com/api/v2/ http://192.168.43.221
    private static final String BASE_URL2 = "http://3.85.85.67:5000/"; //   http://3.85.85.67:5000/
    private static ClienteRetrofit mInstancia;
    private Retrofit retrofit;

    private ClienteRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private ClienteRetrofit(String BASE_URL2){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized ClienteRetrofit getInstance2(){
        mInstancia = new ClienteRetrofit(BASE_URL2);
        return mInstancia;
    }

    public static synchronized ClienteRetrofit getInstance(){
        mInstancia = new ClienteRetrofit();
        return mInstancia;
    }

    public API getApi(){
        return retrofit.create(API.class);
    }
}

package com.example.esicroma.Modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Imagen {
    @SerializedName("cromann")
    @Expose
    private Cromann cromann;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;
}

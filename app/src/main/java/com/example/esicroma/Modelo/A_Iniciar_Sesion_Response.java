package com.example.esicroma.Modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class A_Iniciar_Sesion_Response {

    @SerializedName("token")
    @Expose
    Token token;
    @SerializedName("user")
    @Expose
    User user;
    @SerializedName("status")
    @Expose
    int status;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*@Override
    public String toString() {
        return "A_Iniciar_Sesion_Response{" +
                "user=" + user +
                ", token=" + token +
                ", status=" + status +
                '}';
    }*/
}

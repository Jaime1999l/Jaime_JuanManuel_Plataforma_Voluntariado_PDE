package com.example.jaime_plataforma_voluntariado_pde.repos.convert;

import androidx.room.TypeConverter;

import com.example.jaime_plataforma_voluntariado_pde.model.Ubicacion;
import com.google.gson.Gson;


public class UbicacionConverter {

    private static final Gson gson = new Gson();

    @TypeConverter
    public String fromUbicacion(Ubicacion ubicacion) {
        if (ubicacion == null) return null;
        return gson.toJson(ubicacion); // Convertimos la ubicación a JSON
    }

    @TypeConverter
    public Ubicacion toUbicacion(String data) {
        if (data == null || data.isEmpty()) return null;
        return gson.fromJson(data, Ubicacion.class); // Convertimos el JSON a un objeto Ubicacion
    }
}

package com.example.jaime_plataforma_voluntariado_pde.repos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;

@Database(entities = {ActividadVoluntariado.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ActividadVoluntariadoDao actividadVoluntariadoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "voluntariado_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

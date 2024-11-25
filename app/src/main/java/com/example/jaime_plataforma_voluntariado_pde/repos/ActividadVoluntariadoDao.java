package com.example.jaime_plataforma_voluntariado_pde.repos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;

import java.util.List;

@Dao
public interface ActividadVoluntariadoDao {
    @Insert
    void insertarActividad(ActividadVoluntariado actividad);

    @Query("SELECT * FROM actividades")
    List<ActividadVoluntariado> obtenerTodasLasActividades();

    @Insert
    void insertActividad(ActividadVoluntariado actividad);

    @Query("DELETE FROM actividades")
    void deleteAllActividades();
}


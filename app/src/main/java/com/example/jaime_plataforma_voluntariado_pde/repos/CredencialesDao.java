package com.example.jaime_plataforma_voluntariado_pde.repos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.jaime_plataforma_voluntariado_pde.model.Credenciales;

@Dao
public interface CredencialesDao {
    @Insert
    void insertarCredenciales(Credenciales credenciales);

    @Query("SELECT * FROM credenciales WHERE id = :id LIMIT 1")
    Credenciales obtenerCredencialesPorId(int id);
}

package com.example.jaime_plataforma_voluntariado_pde.repos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.jaime_plataforma_voluntariado_pde.model.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insertarUsuario(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    Usuario obtenerUsuarioPorCorreo(String correo);

    @Query("SELECT * FROM usuarios")
    List<Usuario> obtenerTodosLosUsuarios();
}

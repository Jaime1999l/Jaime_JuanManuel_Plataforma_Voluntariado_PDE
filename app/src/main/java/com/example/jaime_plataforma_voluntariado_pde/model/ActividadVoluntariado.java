package com.example.jaime_plataforma_voluntariado_pde.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.jaime_plataforma_voluntariado_pde.repos.convert.ListStringConverter;
import com.example.jaime_plataforma_voluntariado_pde.repos.convert.UbicacionConverter;

import java.util.List;

@Entity(tableName = "actividades")
@TypeConverters({UbicacionConverter.class, ListStringConverter.class})
public class ActividadVoluntariado {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String firebaseId;
    private String titulo;
    private String descripcion;
    private String estado;
    private int cupoTotal;
    private int cupoDisponible;

    @TypeConverters(UbicacionConverter.class)
    private Ubicacion ubicacion;

    @TypeConverters(ListStringConverter.class)
    private List<String> usuariosInscritos;

    // Constructor completo
    public ActividadVoluntariado(String firebaseId, String titulo, String descripcion, Ubicacion ubicacion, int cupoTotal) {
        this.firebaseId = firebaseId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.cupoTotal = cupoTotal;
        this.cupoDisponible = cupoTotal;
        this.estado = "activa";
    }

    // Constructor vacío requerido por Room
    public ActividadVoluntariado() {
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCupoTotal() {
        return cupoTotal;
    }

    public void setCupoTotal(int cupoTotal) {
        this.cupoTotal = cupoTotal;
    }

    public int getCupoDisponible() {
        return cupoDisponible;
    }

    public void setCupoDisponible(int cupoDisponible) {
        this.cupoDisponible = cupoDisponible;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<String> getUsuariosInscritos() {
        return usuariosInscritos;
    }

    public void setUsuariosInscritos(List<String> usuariosInscritos) {
        this.usuariosInscritos = usuariosInscritos;
    }
}

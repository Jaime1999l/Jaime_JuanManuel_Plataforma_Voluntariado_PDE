package com.example.jaime_plataforma_voluntariado_pde.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;
import com.example.jaime_plataforma_voluntariado_pde.model.Ubicacion;
import com.example.jaime_plataforma_voluntariado_pde.repos.AppDatabase;
import com.example.jaime_plataforma_voluntariado_pde.repos.FirebaseHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.UUID;

public class CreateActivity extends AppCompatActivity {

    private EditText etTitulo, etDescripcion, etCupo, etDireccion;
    private Button btnCrearActividad;
    private FirebaseHandler firebaseHandler;
    private AppDatabase appDatabase;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private double currentLat = 0.0;
    private double currentLong = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Inicializar campos
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCupo = findViewById(R.id.etCupo);
        etDireccion = findViewById(R.id.etDireccion);
        btnCrearActividad = findViewById(R.id.btnCrearActividad);

        // Inicializar Firebase y Room
        firebaseHandler = new FirebaseHandler();
        appDatabase = AppDatabase.getInstance(this);

        // Inicializar el cliente de ubicación
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();

        btnCrearActividad.setOnClickListener(v -> crearActividad());
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtenerUbicacionActual();
        }
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                } else {
                    Toast.makeText(CreateActivity.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void crearActividad() {
        // Validar campos
        if (TextUtils.isEmpty(etTitulo.getText()) || TextUtils.isEmpty(etDescripcion.getText()) ||
                TextUtils.isEmpty(etCupo.getText()) || TextUtils.isEmpty(etDireccion.getText())) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear datos de la actividad
            String id = UUID.randomUUID().toString();
            String titulo = etTitulo.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            int cupo = Integer.parseInt(etCupo.getText().toString().trim());
            String direccion = etDireccion.getText().toString().trim();

            // Generar ubicación aleatoria en un radio de 1000 metros
            double[] randomLocation = generarUbicacionAleatoria(currentLat, currentLong, 1000);
            double latitud = randomLocation[0];
            double longitud = randomLocation[1];

            Ubicacion ubicacion = new Ubicacion(latitud, longitud, direccion);
            ActividadVoluntariado actividad = new ActividadVoluntariado(id, titulo, descripcion, ubicacion, cupo);

            // Guardar en Firebase
            guardarActividadEnFirebase(actividad);

            // Guardar en Room
            guardarActividadEnRoom(actividad);

            // Finalizar la actividad con un mensaje de éxito
            Toast.makeText(this, "Actividad creada exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear actividad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private double[] generarUbicacionAleatoria(double baseLat, double baseLong, int radioEnMetros) {
        double radioTerrestre = 6378100; // Radio de la Tierra en metros

        double desplazamientoLatitud = (Math.random() * 2 - 1) * (radioEnMetros / radioTerrestre) * (180 / Math.PI);
        double desplazamientoLongitud = (Math.random() * 2 - 1) * (radioEnMetros / (radioTerrestre * Math.cos(Math.toRadians(baseLat)))) * (180 / Math.PI);

        double nuevaLatitud = baseLat + desplazamientoLatitud;
        double nuevaLongitud = baseLong + desplazamientoLongitud;

        return new double[]{nuevaLatitud, nuevaLongitud};
    }

    private void guardarActividadEnFirebase(ActividadVoluntariado actividad) {
        try {
            firebaseHandler.guardarActividad(actividad); // Utiliza tu método en FirebaseHandler
            Toast.makeText(this, "Guardado en Firebase exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al interactuar con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarActividadEnRoom(ActividadVoluntariado actividad) {
        new Thread(() -> {
            try {
                appDatabase.actividadVoluntariadoDao().insertActividad(actividad);
                runOnUiThread(() -> Toast.makeText(this, "Guardado en Room exitosamente", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar en Room: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.jaime_plataforma_voluntariado_pde;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.jaime_plataforma_voluntariado_pde.ui.fragments.ActivitiesFragment;
import com.example.jaime_plataforma_voluntariado_pde.ui.fragments.MenuFragment;
import com.example.jaime_plataforma_voluntariado_pde.ui.activities.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class PantallaPrincipalActivity extends AppCompatActivity {

    private Button btnLogout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location userLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnLogout = findViewById(R.id.btnLogout);

        // Configurar el botón de cerrar sesión
        btnLogout.setOnClickListener(v -> cerrarSesion());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            // Cargar el fragmento inicial (lista de actividades)
            cargarFragment(new ActivitiesFragment());

            // Cargar el fragmento del menú
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerMenu, new MenuFragment())
                    .commit();

        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método para cargar un fragmento en el contenedor principal.
     *
     * @param fragment Fragmento a cargar
     */
    private void cargarFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerContent, fragment)
                    .commit();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar el fragmento: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método para cerrar la sesión del usuario.
     */
    private void cerrarSesion() {
        try {
            // Limpiar preferencias del usuario
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Mostrar mensaje de confirmación
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

            // Redirigir al LoginActivity
            Intent intent = new Intent(PantallaPrincipalActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cerrar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtenerUbicacionUsuario();
        }
    }

    private void obtenerUbicacionUsuario() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = location;
                actualizarActividades();
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarActividades() {
        ActivitiesFragment fragment = (ActivitiesFragment) getSupportFragmentManager().findFragmentById(R.id.containerContent);
        if (fragment != null && fragment.isVisible()) {
            fragment.actualizarActividadesCercanas(userLocation);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionUsuario();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

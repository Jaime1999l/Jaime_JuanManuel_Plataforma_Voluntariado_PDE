package com.example.jaime_plataforma_voluntariado_pde;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.jaime_plataforma_voluntariado_pde.ui.fragments.ActivitiesFragment;
import com.example.jaime_plataforma_voluntariado_pde.ui.fragments.MenuFragment;
import com.example.jaime_plataforma_voluntariado_pde.ui.activities.LoginActivity;

public class PantallaPrincipalActivity extends AppCompatActivity {

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnLogout = findViewById(R.id.btnLogout);

        // Configurar el botón de cerrar sesión
        btnLogout.setOnClickListener(v -> cerrarSesion());

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
}

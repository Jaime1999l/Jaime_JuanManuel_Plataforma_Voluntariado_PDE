package com.example.jaime_plataforma_voluntariado_pde.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;
import com.example.jaime_plataforma_voluntariado_pde.model.Ubicacion;
import com.example.jaime_plataforma_voluntariado_pde.repos.AppDatabase;
import com.example.jaime_plataforma_voluntariado_pde.repos.FirebaseHandler;

import java.util.UUID;

public class CreateActivity extends AppCompatActivity {

    private EditText etTitulo, etDescripcion, etCupo, etLatitud, etLongitud, etDireccion;
    private Button btnCrearActividad;
    private FirebaseHandler firebaseHandler;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Inicializar campos
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCupo = findViewById(R.id.etCupo);
        etLatitud = findViewById(R.id.etLatitud);
        etLongitud = findViewById(R.id.etLongitud);
        etDireccion = findViewById(R.id.etDireccion);
        btnCrearActividad = findViewById(R.id.btnCrearActividad);

        // Inicializar Firebase y Room
        firebaseHandler = new FirebaseHandler();
        appDatabase = AppDatabase.getInstance(this);

        btnCrearActividad.setOnClickListener(v -> crearActividad());
    }

    private void crearActividad() {
        // Validar campos
        if (TextUtils.isEmpty(etTitulo.getText()) || TextUtils.isEmpty(etDescripcion.getText()) ||
                TextUtils.isEmpty(etCupo.getText()) || TextUtils.isEmpty(etLatitud.getText()) ||
                TextUtils.isEmpty(etLongitud.getText()) || TextUtils.isEmpty(etDireccion.getText())) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear datos de la actividad
            String id = UUID.randomUUID().toString();
            String titulo = etTitulo.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            int cupo = Integer.parseInt(etCupo.getText().toString().trim());
            double latitud = Double.parseDouble(etLatitud.getText().toString().trim());
            double longitud = Double.parseDouble(etLongitud.getText().toString().trim());
            String direccion = etDireccion.getText().toString().trim();

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
}

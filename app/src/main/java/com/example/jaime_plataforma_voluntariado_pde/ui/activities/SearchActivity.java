package com.example.jaime_plataforma_voluntariado_pde.ui.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;
import com.example.jaime_plataforma_voluntariado_pde.repos.AppDatabase;
import com.example.jaime_plataforma_voluntariado_pde.repos.ActividadVoluntariadoDao;
import com.example.jaime_plataforma_voluntariado_pde.ui.adapters.ActivitiesAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rvResults;
    private TextView tvNoResults;
    private ActivitiesAdapter adapter;
    private List<ActividadVoluntariado> allActivities;
    private ActividadVoluntariadoDao actividadDao;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Inicializar componentes
        etSearch = findViewById(R.id.etSearch);
        rvResults = findViewById(R.id.rvResults);
        tvNoResults = findViewById(R.id.tvNoResults); // Mensaje para "sin resultados"
        rvResults.setLayoutManager(new LinearLayoutManager(this));

        allActivities = new ArrayList<>();
        adapter = new ActivitiesAdapter(this, new ArrayList<>());
        rvResults.setAdapter(adapter);

        // Inicializar DAO y Firebase Firestore
        actividadDao = AppDatabase.getInstance(this).actividadVoluntariadoDao();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Configurar listener en tiempo real con Firebase
        listenToActivities();

        // Configurar búsqueda en tiempo real
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterActivities(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Escucha en tiempo real los cambios en la colección de actividades de Firebase
     */
    private void listenToActivities() {
        firebaseFirestore.collection("actividades")
                .addSnapshotListener((QuerySnapshot value, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        showErrorToast("Error al escuchar actividades: " + error.getMessage());
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<ActividadVoluntariado> updatedActivities = value.toObjects(ActividadVoluntariado.class);

                        allActivities.clear();
                        allActivities.addAll(updatedActivities);

                        runOnUiThread(() -> {
                            adapter.updateList(new ArrayList<>(allActivities));
                            tvNoResults.setVisibility(View.GONE); // Ocultar mensaje de "sin resultados"
                            rvResults.setVisibility(View.VISIBLE); // Mostrar RecyclerView
                        });
                    } else {
                        runOnUiThread(() -> {
                            allActivities.clear();
                            adapter.updateList(new ArrayList<>());
                            tvNoResults.setVisibility(View.VISIBLE); // Mostrar mensaje
                            rvResults.setVisibility(View.GONE); // Ocultar RecyclerView
                        });
                    }
                });
    }

    /**
     * Filtra actividades según la consulta de búsqueda
     * @param query texto ingresado por el usuario
     */
    private void filterActivities(String query) {
        new Thread(() -> {
            List<ActividadVoluntariado> filteredActivities = new ArrayList<>();

            if (!query.isEmpty()) {
                String cleanedQuery = query.replaceAll("\\s+", "").toLowerCase();

                for (ActividadVoluntariado activity : allActivities) {
                    String cleanedTitle = activity.getTitulo() != null ? activity.getTitulo().replaceAll("\\s+", "").toLowerCase() : "";
                    String cleanedDescription = activity.getDescripcion() != null ? activity.getDescripcion().replaceAll("\\s+", "").toLowerCase() : "";

                    if (cleanedTitle.contains(cleanedQuery) || cleanedDescription.contains(cleanedQuery)) {
                        filteredActivities.add(activity);
                    }
                }
            } else {
                filteredActivities.addAll(allActivities);
            }

            runOnUiThread(() -> {
                if (filteredActivities.isEmpty()) {
                    adapter.updateList(new ArrayList<>());
                    tvNoResults.setVisibility(View.VISIBLE); // Mostrar mensaje
                    rvResults.setVisibility(View.GONE); // Ocultar RecyclerView
                } else {
                    adapter.updateList(filteredActivities);
                    tvNoResults.setVisibility(View.GONE); // Ocultar mensaje
                    rvResults.setVisibility(View.VISIBLE); // Mostrar RecyclerView
                }
            });
        }).start();
    }

    private void showErrorToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}

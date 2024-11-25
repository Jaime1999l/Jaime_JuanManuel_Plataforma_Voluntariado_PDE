package com.example.jaime_plataforma_voluntariado_pde.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;
import com.example.jaime_plataforma_voluntariado_pde.repos.FirebaseHandler;
import com.example.jaime_plataforma_voluntariado_pde.ui.adapters.ActivitiesAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesFragment extends Fragment {

    private RecyclerView rvActivities;
    private ActivitiesAdapter adapter;
    private List<ActividadVoluntariado> actividades;
    private FirebaseHandler firebaseHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        rvActivities = view.findViewById(R.id.rvActivities);
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        actividades = new ArrayList<>();
        adapter = new ActivitiesAdapter(requireContext(), actividades);
        rvActivities.setAdapter(adapter);

        firebaseHandler = new FirebaseHandler();
        cargarActividades();

        return view;
    }

    private void cargarActividades() {
        firebaseHandler.obtenerActividades(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                actividades.clear();
                actividades.addAll(task.getResult().toObjects(ActividadVoluntariado.class));
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


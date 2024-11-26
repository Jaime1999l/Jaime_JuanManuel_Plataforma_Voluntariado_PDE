package com.example.jaime_plataforma_voluntariado_pde.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;
import com.example.jaime_plataforma_voluntariado_pde.repos.FirebaseHandler;
import com.example.jaime_plataforma_voluntariado_pde.ui.adapters.ActivitiesAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivitiesFragment extends Fragment {

    private RecyclerView rvActivities;
    private ActivitiesAdapter adapter;
    private List<ActividadVoluntariado> actividades;
    private FirebaseHandler firebaseHandler;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        requestLocationPermission();

        return view;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            cargarActividades();
        }
    }

    private void cargarActividades() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    obtenerActividadesCercanas(location);
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void obtenerActividadesCercanas(Location userLocation) {
        firebaseHandler.obtenerActividades(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                actividades.clear();
                List<ActividadVoluntariado> todasActividades = task.getResult().toObjects(ActividadVoluntariado.class);

                // Calcular la distancia entre el usuario y cada actividad
                for (ActividadVoluntariado actividad : todasActividades) {
                    if (actividad.getUbicacion() != null) {
                        Location actividadLocation = new Location("");
                        actividadLocation.setLatitude(actividad.getUbicacion().getLatitud());
                        actividadLocation.setLongitude(actividad.getUbicacion().getLongitud());

                        float distancia = userLocation.distanceTo(actividadLocation); // Distancia en metros
                        actividad.getUbicacion().setDireccion(String.format("%.2f km", distancia / 1000)); // Formatear a kilómetros
                    }
                }

                // Ordenar actividades por distancia
                Collections.sort(todasActividades, Comparator.comparingDouble(a -> {
                    Location actividadLocation = new Location("");
                    actividadLocation.setLatitude(a.getUbicacion().getLatitud());
                    actividadLocation.setLongitude(a.getUbicacion().getLongitud());
                    return userLocation.distanceTo(actividadLocation);
                }));

                // Tomar las 3 actividades más cercanas
                actividades.addAll(todasActividades.subList(0, Math.min(3, todasActividades.size())));

                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarActividades();
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void actualizarActividadesCercanas(Location userLocation) {
        if (userLocation == null) {
            Toast.makeText(getContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseHandler.obtenerActividades(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                actividades.clear();
                List<ActividadVoluntariado> todasActividades = task.getResult().toObjects(ActividadVoluntariado.class);

                // Calcular distancias y ordenar
                for (ActividadVoluntariado actividad : todasActividades) {
                    if (actividad.getUbicacion() != null) {
                        Location actividadLocation = new Location("");
                        actividadLocation.setLatitude(actividad.getUbicacion().getLatitud());
                        actividadLocation.setLongitude(actividad.getUbicacion().getLongitud());

                        float distancia = userLocation.distanceTo(actividadLocation);
                        actividad.getUbicacion().setDireccion(String.format("%.2f km", distancia / 1000));
                    }
                }

                // Ordenar actividades por proximidad
                todasActividades.sort(Comparator.comparingDouble(a -> {
                    Location actividadLocation = new Location("");
                    actividadLocation.setLatitude(a.getUbicacion().getLatitud());
                    actividadLocation.setLongitude(a.getUbicacion().getLongitud());
                    return userLocation.distanceTo(actividadLocation);
                }));

                // Actualizar con las más cercanas
                actividades.addAll(todasActividades.subList(0, Math.min(3, todasActividades.size())));
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

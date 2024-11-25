package com.example.jaime_plataforma_voluntariado_pde.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.ui.activities.CreateActivity;
import com.example.jaime_plataforma_voluntariado_pde.ui.activities.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuFragment extends Fragment {

    private ImageButton btnSearch, btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        btnSearch = view.findViewById(R.id.btnSearch);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnSearch.setOnClickListener(v -> navigateToSearch());
        btnAdd.setOnClickListener(v -> navigateToAdd());

        // Verificar el rol del usuario actual
        verificarRolUsuario();

        return view;
    }

    private void verificarRolUsuario() {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .whereEqualTo("correo", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String rol = queryDocumentSnapshots.getDocuments().get(0).getString("rol");

                        // Si el usuario no es administrador, ocultar el botón de agregar actividad
                        if (!"Administrador".equalsIgnoreCase(rol)) {
                            btnAdd.setVisibility(View.GONE);
                        }
                    } else {
                        // Si el usuario no se encuentra, ocultar el botón por defecto
                        btnAdd.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    // En caso de error, también ocultar el botón como medida de seguridad
                    btnAdd.setVisibility(View.GONE);
                });
    }

    private void navigateToSearch() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    private void navigateToAdd() {
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        startActivity(intent);
    }
}

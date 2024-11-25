package com.example.jaime_plataforma_voluntariado_pde.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jaime_plataforma_voluntariado_pde.R;
import com.example.jaime_plataforma_voluntariado_pde.model.ActividadVoluntariado;
import com.example.jaime_plataforma_voluntariado_pde.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder> {

    private List<ActividadVoluntariado> actividades;
    private Context context;


    public ActivitiesAdapter(Context context, List<ActividadVoluntariado> actividades){
        this.context = context;
        this.actividades = actividades;
    }

    public void updateList(List<ActividadVoluntariado> newActivities) {
        actividades.clear();
        actividades.addAll(newActivities);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActividadVoluntariado actividad = actividades.get(position);
        holder.tvTitulo.setText(actividad.getTitulo());
        holder.tvDescripcion.setText(actividad.getDescripcion());
        holder.tvCupoDisponible.setText("Cupo Disponible: " + actividad.getCupoDisponible());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String currentUserEmail = auth.getCurrentUser().getEmail();

            // Obtener el estado inicial de inscripción
            boolean isUserEnrolled = actividad.getUsuariosInscritos() != null &&
                    actividad.getUsuariosInscritos().contains(currentUserEmail);

            // Configurar la imagen de la estrella
            holder.ivStar.setImageResource(isUserEnrolled ? R.drawable.ic_star_filled : R.drawable.ic_star_border);

            // Configurar el clic de la estrella
            holder.ivStar.setOnClickListener(v -> {
                // Comprobar nuevamente si el usuario está inscrito antes de ejecutar la acción
                FirebaseFirestore.getInstance().collection("actividades")
                        .document(actividad.getFirebaseId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                List<String> usuariosInscritos = (List<String>) documentSnapshot.get("usuariosInscritos");
                                if (usuariosInscritos == null) usuariosInscritos = new ArrayList<>();

                                boolean isCurrentlyEnrolled = usuariosInscritos.contains(currentUserEmail);

                                if (isCurrentlyEnrolled) {
                                    desinscribirUsuario(actividad, currentUserEmail, holder);
                                } else if (actividad.getCupoDisponible() <= 0) {
                                    Toast.makeText(holder.itemView.getContext(), "No hay cupos disponibles para esta actividad.", Toast.LENGTH_SHORT).show();
                                } else {
                                    inscribirUsuario(actividad, currentUserEmail, holder);
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Error al verificar actividad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.ivStar.setImageResource(R.drawable.ic_star_border);
            holder.ivStar.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Debe iniciar sesión para inscribirse", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void inscribirUsuario(ActividadVoluntariado actividad, String userEmail, ActivityViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String actividadId = actividad.getFirebaseId();

        db.collection("actividades").document(actividadId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> usuariosInscritos = (List<String>) documentSnapshot.get("usuariosInscritos");
                        if (usuariosInscritos == null) usuariosInscritos = new ArrayList<>();

                        if (!usuariosInscritos.contains(userEmail)) {
                            usuariosInscritos.add(userEmail);
                            actividad.setCupoDisponible(actividad.getCupoDisponible() - 1);

                            List<String> finalUsuariosInscritos = usuariosInscritos;
                            db.collection("actividades").document(actividadId)
                                    .update(
                                            "usuariosInscritos", usuariosInscritos,
                                            "cupoDisponible", actividad.getCupoDisponible()
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        actividad.setUsuariosInscritos(finalUsuariosInscritos); // Actualiza localmente
                                        holder.ivStar.setImageResource(R.drawable.ic_star_filled);
                                        Toast.makeText(holder.itemView.getContext(), "Inscripción exitosa.", Toast.LENGTH_SHORT).show();
                                        actualizarActividadesUsuario(db, actividad, userEmail, true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(holder.itemView.getContext(), "Error al inscribirse: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(), "Error al cargar la actividad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void desinscribirUsuario(ActividadVoluntariado actividad, String userEmail, ActivityViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String actividadId = actividad.getFirebaseId();

        db.collection("actividades").document(actividadId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> usuariosInscritos = (List<String>) documentSnapshot.get("usuariosInscritos");
                        if (usuariosInscritos != null && usuariosInscritos.contains(userEmail)) {
                            usuariosInscritos.remove(userEmail);
                            actividad.setCupoDisponible(actividad.getCupoDisponible() + 1);

                            db.collection("actividades").document(actividadId)
                                    .update(
                                            "usuariosInscritos", usuariosInscritos,
                                            "cupoDisponible", actividad.getCupoDisponible()
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        actividad.setUsuariosInscritos(usuariosInscritos); // Actualiza localmente
                                        holder.ivStar.setImageResource(R.drawable.ic_star_border);
                                        Toast.makeText(holder.itemView.getContext(), "Desinscripción exitosa.", Toast.LENGTH_SHORT).show();
                                        actualizarActividadesUsuario(db, actividad, userEmail, false);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(holder.itemView.getContext(), "Error al desinscribirse: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "El usuario no está inscrito en esta actividad.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(), "Error al cargar la actividad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarActividadesUsuario(FirebaseFirestore db, ActividadVoluntariado actividad, String userEmail, boolean isAdding) {
        db.collection("usuarios").whereEqualTo("correo", userEmail).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Usuario usuario = queryDocumentSnapshots.getDocuments().get(0).toObject(Usuario.class);

                        if (usuario.getActividadesInscritas() == null) {
                            usuario.setActividadesInscritas(new ArrayList<>());
                        }

                        if (isAdding) {
                            usuario.getActividadesInscritas().add(actividad);
                        } else {
                            usuario.getActividadesInscritas().removeIf(a -> a.getFirebaseId().equals(actividad.getFirebaseId()));
                        }

                        db.collection("usuarios").document(userId)
                                .update("actividadesInscritas", usuario.getActividadesInscritas())
                                .addOnSuccessListener(aVoid -> {
                                    String message = isAdding
                                            ? "Actividad añadida a tus actividades."
                                            : "Actividad eliminada de tus actividades.";
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al actualizar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al cargar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvDescripcion;
        TextView tvCupoDisponible;
        ImageView ivStar;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvCupoDisponible = itemView.findViewById(R.id.tvCupoDisponible);
            ivStar = itemView.findViewById(R.id.ivStar);
        }
    }
}

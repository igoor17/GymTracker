package es.eduardo.gymtracker.profile;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.eduardo.gymtracker.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Código de solicitud para seleccionar una imagen
    private Uri imageUri; // URI de la imagen seleccionada

    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    // UI
    Button saveButton;

    // Info Usuario
    EditText profileNameEdit;
    EditText profileAgeEdit;
    EditText profileHeightEdit;
    EditText profileWeightEdit;
    ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        profileNameEdit = view.findViewById(R.id.profile_name_edit);
        profileAgeEdit = view.findViewById(R.id.profile_age_edit);
        profileHeightEdit = view.findViewById(R.id.profile_height_edit);
        profileWeightEdit = view.findViewById(R.id.profile_weight_edit);
        profileImage = view.findViewById(R.id.profile_image);

        // Obtén la información del usuario y establece los valores en los EditText

        db.collection("users").document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        getActivity().runOnUiThread(() -> {
                            profileNameEdit.setText(documentSnapshot.getString("name"));
                            profileAgeEdit.setText(documentSnapshot.getString("age"));
                            profileHeightEdit.setText(documentSnapshot.getString("height"));
                            profileWeightEdit.setText(documentSnapshot.getString("weight"));

                            // Cargar la imagen del usuario en el ImageView
                            String imageUrl = documentSnapshot.getString("imageUrl");
                            if (imageUrl != null) {
                                Glide.with(getActivity())
                                        .load(imageUrl)
                                        .into(profileImage);
                            }
                        });
                    }
                });


        ImageButton editImageButton = view.findViewById(R.id.edit_image_button);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Actualizar el ImageView con la nueva imagen seleccionada
            Glide.with(this)
                    .load(imageUri)
                    .into(profileImage);
        }
    }

    private void saveChanges() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child("images/" + user.getUid() + ".jpg");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = fileReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                updateProfile(uri.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error uploading image.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updateProfile(null);
        }
    }

    private void updateProfile(String imageUrl) {
        double height = Double.parseDouble(profileHeightEdit.getText().toString());
        double weight = Double.parseDouble(profileWeightEdit.getText().toString());

        // Convertir altura a metros
        double heightInM = height / 100;

        // Calcular IMC
        double imc = weight / Math.pow(heightInM, 2);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", profileNameEdit.getText().toString());
        userMap.put("age", profileAgeEdit.getText().toString());
        userMap.put("height", profileHeightEdit.getText().toString());
        userMap.put("weight", profileWeightEdit.getText().toString());
        userMap.put("imageUrl", imageUrl);
        userMap.put("bmi", imc);

        db.collection("users").document(user.getEmail())
                .update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (isAdded()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        // Volver al fragmento de perfil
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, new ProfileFragment());
                        transaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (isAdded()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.error_update_profile), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }
}
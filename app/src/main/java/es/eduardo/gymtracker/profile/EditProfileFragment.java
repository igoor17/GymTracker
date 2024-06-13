package es.eduardo.gymtracker.profile;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import es.eduardo.gymtracker.R;

/**
 * Fragment for editing user profile information.
 */
public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for selecting an image
    private Uri imageUri; // URI of the selected image

    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    // UI elements
    Button saveButton;
    EditText profileNameEdit;
    EditText profileAgeEdit;
    EditText profileHeightEdit;
    EditText profileWeightEdit;
    ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Firebase instances
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Initialize UI elements
        profileNameEdit = view.findViewById(R.id.profile_name_edit);
        profileAgeEdit = view.findViewById(R.id.profile_age_edit);
        profileHeightEdit = view.findViewById(R.id.profile_height_edit);
        profileWeightEdit = view.findViewById(R.id.profile_weight_edit);
        profileImage = view.findViewById(R.id.profile_image);

        // Load user information and set values in EditText fields
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

                            // Load user image into ImageView using Glide
                            String imageUrl = documentSnapshot.getString("imageUrl");
                            if (imageUrl != null) {
                                Glide.with(getActivity())
                                        .load(imageUrl)
                                        .into(profileImage);
                            }
                        });
                    }
                });

        // Set click listener for choosing an image from gallery
        ImageButton editImageButton = view.findViewById(R.id.edit_image_button);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set click listener for saving changes button
        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        return view;
    }

    /**
     * Opens the file chooser to select an image from gallery.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result from the file chooser to set the selected image to ImageView.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The Intent data of the result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Update ImageView with the selected image using Glide
            Glide.with(this)
                    .load(imageUri)
                    .into(profileImage);
        }
    }

    /**
     * Saves changes made to the user profile, including image upload if a new image is selected.
     */
    private void saveChanges() {
        if (imageUri != null) {
            // Reference to store image in Firebase Storage
            StorageReference fileReference = storageRef.child("images/" + user.getUid() + ".jpg");

            try {
                // Convert selected image to Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                // Compress Bitmap to JPEG with quality 100
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Upload image data to Firebase Storage
                UploadTask uploadTask = fileReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get download URL of uploaded image
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Update profile with new image URL
                                updateProfile(uri.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle image upload failure
                        Toast.makeText(getActivity(), "Error uploading image.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Update profile without changing the image
            updateProfile(null);
        }
    }

    /**
     * Updates user profile details in Firestore.
     *
     * @param imageUrl The URL of the new profile image (null if image was not changed).
     */
    private void updateProfile(String imageUrl) {
        // Convert height and weight EditText values to double
        double height = Double.parseDouble(profileHeightEdit.getText().toString());
        double weight = Double.parseDouble(profileWeightEdit.getText().toString());

        // Convert height to meters for BMI calculation
        double heightInMeters = height / 100;

        // Calculate BMI
        double bmi = weight / (heightInMeters * heightInMeters);

        // Create map with updated user details
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", profileNameEdit.getText().toString());
        userMap.put("age", profileAgeEdit.getText().toString());
        userMap.put("height", profileHeightEdit.getText().toString());
        userMap.put("weight", profileWeightEdit.getText().toString());
        userMap.put("imageUrl", imageUrl); // Update imageUrl if a new image is selected
        userMap.put("bmi", bmi); // Update BMI

        // Update user document in Firestore
        db.collection("users").document(user.getEmail())
                .update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show toast message indicating successful profile update
                        if (isAdded()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        // Navigate back to the profile fragment
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, new ProfileFragment());
                        transaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show toast message indicating profile update failure
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

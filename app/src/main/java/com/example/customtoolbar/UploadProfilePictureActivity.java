package com.example.customtoolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UploadProfilePictureActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    private static final String TAG = "UploadProfilePictureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        Button buttonUploadPicChoose = findViewById(R.id.btn_upload_choose_pic);
        Button buttonUploadPic = findViewById(R.id.btn_upload_pic);
        progressBar = findViewById(R.id.progressBar);
        imageViewUploadPic = findViewById(R.id.imageview_profile_dp);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Upload Profile Picture");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
        // Instead of using firebaseUser.getPhotoUrl()
        assert firebaseUser != null;
        Uri uri = firebaseUser.getPhotoUrl();

        // Use the updated photo URL from UserProfileChangeRequest
        if (firebaseUser.getPhotoUrl() != null) {
            uri = firebaseUser.getPhotoUrl();
        }
        Picasso.get().load(uri).into(imageViewUploadPic);

        //Choosing image to upload
        buttonUploadPicChoose.setOnClickListener(v -> openFileChooser());

        //Upload chosen image
        buttonUploadPic.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            UploadPic();
        });

    }

    private void UploadPic() {
        if (uriImage != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                StorageReference fileReference = storageReference.child(currentUser.getUid() + "/displaypics." + getFileExtension(uriImage));
                fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {
                    // Debugging statement
                    Log.d(TAG, "Upload successful");

                    fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Debugging statement
                                Log.d(TAG, "Download URL: " + uri.toString());

                                // Update user profile with photo URL
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();
                                currentUser.updateProfile(profileUpdates);

                                // Pass data to MainActivity
                                Intent intent = new Intent(UploadProfilePictureActivity.this, MainActivity.class);
                                intent.putExtra("uploadedImageUri", uri);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Debugging statement
                                Log.e(TAG, "Failed to get download URL", e);

                                Toast.makeText(UploadProfilePictureActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            });
                })
                        .addOnFailureListener(e -> {
                            // Debugging statement
                            Log.e(TAG, "Upload failed", e);

                            Toast.makeText(UploadProfilePictureActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(UploadProfilePictureActivity.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UploadProfilePictureActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    //Obtain File Extension of the image
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            //Refresh action
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(UploadProfilePictureActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UploadProfilePictureActivity.this, Login.class);

            //Clear stack to prevent user coming back to Home Fragment/MainActivity after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UploadProfilePictureActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
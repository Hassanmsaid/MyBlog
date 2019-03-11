package com.example.blog.Gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText fNameET, lNameET, emailET, passwordET, confirmPasswordET;
    private Button createAccBtn;
    private ImageView profileImage;
    private Uri resultUri;
    private DatabaseReference dbReference;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        fNameET = findViewById(R.id.create_first_ET);
        lNameET = findViewById(R.id.create_last_ET);
        emailET = findViewById(R.id.create_email_ET);
        passwordET = findViewById(R.id.create_password_ET);
        confirmPasswordET = findViewById(R.id.create_confirm_password_ET);

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

        createAccBtn = findViewById(R.id.create_acc_btn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference().child("Users");
        auth = FirebaseAuth.getInstance();
    }

    private void createNewAccount() {
        final String fname, lname, email, pw, confirmpw;
        fname = fNameET.getText().toString();
        lname = lNameET.getText().toString();
        email = emailET.getText().toString();
        pw = passwordET.getText().toString();
        confirmpw = confirmPasswordET.getText().toString();

        //check all inputs
        if (!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pw) && pw.equals(confirmpw)) {
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            auth.createUserWithEmailAndPassword(email, pw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null) {
                        StorageReference imagePath = storageReference.child(resultUri.getLastPathSegment());
                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userID = auth.getCurrentUser().getUid();
                                DatabaseReference newUser = dbReference.child(userID);
                                newUser.child("firstName").setValue(fname);
                                newUser.child("lastName").setValue(lname);
                                newUser.child("image").setValue(resultUri.toString());

                                progressDialog.dismiss();
                                Toast.makeText(CreateAccountActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CreateAccountActivity.this, PostsActivity.class));
                                finish();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccountActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageURI = data.getData();
            CropImage.activity(imageURI)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profileImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

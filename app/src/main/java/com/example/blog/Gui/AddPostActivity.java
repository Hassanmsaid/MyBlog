package com.example.blog.Gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blog.Gui.Posts.PostsActivity;
import com.example.blog.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    private ImageView postImage;
    private EditText postTitleET, postTextET;
    private Button postBtn;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private String downloadImageUrl;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
//        toolbar = findViewById(R.id.add_post_toolbar);
//        setSupportActionBar(toolbar);

        postImage = findViewById(R.id.add_post_img);
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

        postTitleET = findViewById(R.id.add_post_title_ET);
        postTextET = findViewById(R.id.add_post_text_ET);

        postBtn = findViewById(R.id.post_btn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            postImage.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if (auth != null && user != null) {
                    startActivity(new Intent(AddPostActivity.this, AddPostActivity.class));
//                    finish();
                }
                break;
            case R.id.action_signout:
                if (auth != null && user != null) {
                    auth.signOut();
                    startActivity(new Intent(AddPostActivity.this, LoginActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startPosting() {
        progressDialog.setMessage("Posting to blog...");
        progressDialog.show();

        final String titleValue = postTitleET.getText().toString().trim();
        final String textValue = postTextET.getText().toString().trim();

        if (!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(textValue) && imageUri != null) {
            //start uploading
            final StorageReference filepath = storageReference.child("Posts Images").child(imageUri.getLastPathSegment());
            final UploadTask uploadTask = filepath.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(AddPostActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageUrl = filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl = task.getResult().toString();
                                DatabaseReference newPost = databaseReference.push();
                                Map<String, String> dataToSave = new HashMap<>();
                                dataToSave.put("blogTitle", titleValue);
                                dataToSave.put("blogText", textValue);
                                dataToSave.put("blogImage", downloadImageUrl);
                                dataToSave.put("timeStamp", String.valueOf(System.currentTimeMillis()));
                                dataToSave.put("userID", user.getUid());
                                newPost.setValue(dataToSave);
                                progressDialog.dismiss();
                                Toast.makeText(AddPostActivity.this, "Post added successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddPostActivity.this, PostsActivity.class));
                            }
                        }
                    });
                }
            });
        }
    }
}

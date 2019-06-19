package com.example.blog.Gui.CreateAccount;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

class CreateAccPresenter {

    private ICreateAccView view;
    private Context context;

    CreateAccPresenter(ICreateAccView view, Context context) {
        this.view = view;
        this.context = context;
    }

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference = firebaseDatabase.getReference().child("Users");
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    void createAccEmailPassword(String email, String pw, final String fname, final String lname, final Uri resultUri) {
        view.createAccLoading();

        auth.createUserWithEmailAndPassword(email, pw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (authResult != null) {
                    StorageReference imagePath = storageReference.child(String.valueOf(System.currentTimeMillis()));
                    imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String userID = auth.getCurrentUser().getUid();
                            DatabaseReference newUser = dbReference.child(userID);
                            newUser.child("firstName").setValue(fname);
                            newUser.child("lastName").setValue(lname);
                            newUser.child("image").setValue(resultUri.toString());

                            view.createAccSuccess();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.createAccFail();
            }
        });
    }
}

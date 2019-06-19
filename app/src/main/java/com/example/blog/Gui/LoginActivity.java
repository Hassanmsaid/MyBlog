package com.example.blog.Gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blog.Gui.CreateAccount.CreateAccountActivity;
import com.example.blog.Gui.Posts.PostsActivity;
import com.example.blog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListner;
    private FirebaseUser user;

    private EditText emailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.email_ET);
        passwordET = findViewById(R.id.password_ET);

        //Handle login button
        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pw;
                email = emailET.getText().toString();
                pw = passwordET.getText().toString();
                if (!email.equals("") && !pw.equals("")) {
                    login(email, pw);
                }
            }
        });


        //commit test

        //Handle create account button
        Button createAccBtn = findViewById(R.id.create_account_btn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                finish();
            }
        });

        //Check auth state
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        authListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //toast("NOT signed in");
                } else {
                    //toast("Signed in");
                    startActivity(new Intent(LoginActivity.this, PostsActivity.class));
                    finish();
                }
            }
        };
    }

    //Login
    private void login(String email, String pw) {
        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    toast("Login Successful!");
                    startActivity(new Intent(LoginActivity.this, PostsActivity.class));
                    finish();
                } else {
                    toast("Failed! Check email & pw then try again.");
                }
            }
        });
    }

    //Toast function
    public void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //commit 2

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                auth.signOut();
                break;
            case R.id.action_add:
                startActivity(new Intent(LoginActivity.this, AddPostActivity.class));
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListner != null)
            auth.removeAuthStateListener(authListner);
    }
}

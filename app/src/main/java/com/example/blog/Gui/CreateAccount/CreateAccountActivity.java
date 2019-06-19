package com.example.blog.Gui.CreateAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blog.Gui.Posts.PostsActivity;
import com.example.blog.R;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class CreateAccountActivity extends AppCompatActivity implements ICreateAccView {

    private EditText fNameET, lNameET, emailET, passwordET, confirmPasswordET;
    private Button createAccBtn;
    private ImageView profileImage;
    private Uri resultUri;
    private ProgressDialog progressDialog;
    private CreateAccPresenter presenter;

    Uri destUri, finalUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        fNameET = findViewById(R.id.create_first_ET);
        lNameET = findViewById(R.id.create_last_ET);
        emailET = findViewById(R.id.create_email_ET);
        passwordET = findViewById(R.id.create_password_ET);
        confirmPasswordET = findViewById(R.id.create_confirm_password_ET);

        progressDialog = new ProgressDialog(this);
        presenter = new CreateAccPresenter(this, this);

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(CreateAccountActivity.this);
            }
        });

        createAccBtn = findViewById(R.id.create_acc_btn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
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
            presenter.createAccEmailPassword(email, pw, fname, lname, finalUri);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Second crop
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                Uri imageURI = data.getData();
                Uri destUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(imageURI, destUri).asSquare().start(this);
                finalUri = Crop.getOutput(data);
                profileImage.setImageURI(finalUri);
            } else if (requestCode == Crop.REQUEST_CROP) {
                finalUri = Crop.getOutput(data);
                profileImage.setImageURI(Crop.getOutput(data));
            }
        }
    }

    @Override
    public void createAccSuccess() {
        progressDialog.dismiss();
        Toast.makeText(CreateAccountActivity.this, "Account created", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CreateAccountActivity.this, PostsActivity.class));
        finish();
    }

    @Override
    public void createAccFail() {
        progressDialog.dismiss();
        Toast.makeText(CreateAccountActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void createAccLoading() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
    }
}

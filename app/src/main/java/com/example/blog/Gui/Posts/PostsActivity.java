package com.example.blog.Gui.Posts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.blog.BlogAdapter;
import com.example.blog.Gui.AddPostActivity;
import com.example.blog.Gui.LoginActivity;
import com.example.blog.Model.Blog;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostsActivity extends AppCompatActivity implements IPostsView {

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private BlogAdapter adapter;
    private List<Blog> blogList;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    PostsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        init();
        presenter.retrievePosts(databaseReference, blogList);
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.keepSynced(true);

        toolbar = findViewById(R.id.posts_toolbar);
        toolbar.setTitle("Posts");
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);

        blogList = new ArrayList<>();
        recyclerView = findViewById(R.id.posts_RV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlogAdapter(PostsActivity.this, blogList);
        presenter = new PostsPresenter(this, this);
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
                    startActivity(new Intent(PostsActivity.this, AddPostActivity.class));
                }
                break;
            case R.id.action_signout:
                if (auth != null && user != null) {
                    auth.signOut();
                    startActivity(new Intent(PostsActivity.this, LoginActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void loadingPosts() {
        progressDialog.setMessage("Retrieving posts...");
        progressDialog.show();
    }

    @Override
    public void displayPosts() {
        Collections.reverse(blogList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}


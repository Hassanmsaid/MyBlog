package com.example.blog.Gui.Posts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.blog.BlogAdapter;
import com.example.blog.Model.Blog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Collections;
import java.util.List;

public class PostsPresenter {
    IPostsView view;
    Context context;

    public PostsPresenter(IPostsView view, Context context) {
        this.view = view;
        this.context = context;
    }

    void retrievePosts(DatabaseReference databaseReference, final List<Blog> blogList){

        view.loadingPosts();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.add(blog);
                view.displayPosts();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

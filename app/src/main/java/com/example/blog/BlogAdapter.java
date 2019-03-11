package com.example.blog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blog.Model.Blog;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Blog blog = blogList.get(i);

        viewHolder.postTitle.setText(blog.getBlogTitle());
        viewHolder.postText.setText(blog.getBlogText());

        //TODO: fix date
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimeStamp())).getTime());
        viewHolder.timeStamp.setText(formattedDate);

        String imageURL = blog.getBlogImage();
        Picasso.get().load(imageURL).into(viewHolder.postImage);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView postTitle, postText, timeStamp, userID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postTitle= itemView.findViewById(R.id.post_title);
            postText = itemView.findViewById(R.id.post_text);
            timeStamp = itemView.findViewById(R.id.post_time);
            userID = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}

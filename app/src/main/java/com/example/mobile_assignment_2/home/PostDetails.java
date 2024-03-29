package com.example.mobile_assignment_2.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_assignment_2.Comment;
import com.example.mobile_assignment_2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author: Weiran Zou
 * @description: Post Details screen displaying the details of post tapped on "Explore" tab
 * on Home screen. The user can like, cancel like, collect, cancel collect and comment on posts.
 */
public class PostDetails extends AppCompatActivity {
    TextView titleView;
    TextView descripView;
    TextView authorView;
    ImageView profileView;
    LinearLayout commentsLinearLayout;
    RecyclerView imagesRecyclerView;
    Button likeBtn;
    Button collectBtn;
    Button commentBtn;
    Button sendBtn;
    private TextInputEditText commentTextField;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<String> likes = new ArrayList<>();
    ArrayList<String> collects = new ArrayList<>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String description = intent.getStringExtra("description");
        String uid = intent.getStringExtra("uid");
        ArrayList<String> imageURLs = intent.getStringArrayListExtra("imageURLs");
        ArrayList<Comment> commentsList = new ArrayList<>();
        String pid = intent.getStringExtra("pid");
        final int[] num_likes = {0};
        final int[] num_collects = {0};
        final int[] num_comments = {0};
        likeBtn = findViewById(R.id.like);
        collectBtn = findViewById(R.id.collect);
        commentBtn = findViewById(R.id.comment);
        sendBtn = findViewById(R.id.sendCommentButton);
        commentTextField = findViewById(R.id.textFieldComment);
        profileView = findViewById(R.id.profile_image);
        // Get profile photo url of author of post
        firebaseDatabase.getReference().child("Users").child(uid).child("imageUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error in fetching data", task.getException());
                } else {
                    String profileImageUrl = task.getResult().getValue(String.class);
                    // Download image from URL and set to imageView
                    Picasso.with(getApplicationContext()).load(profileImageUrl).fit().centerCrop().into(profileView);
                }
            }
        });
        // Get the current post and listen for changes
        firebaseDatabase.getReference().child("Posts").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get and display num of likes, collects and comments and comments
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (dataSnapshot.getKey().equals("likes")) {
                        num_likes[0] = dataSnapshot.getValue(int.class);
                    }
                    if (dataSnapshot.getKey().equals("collects")) {
                        num_collects[0] = dataSnapshot.getValue(int.class);
                    }
                    if (dataSnapshot.getKey().equals("numComments")) {
                        num_comments[0] = dataSnapshot.getValue(int.class);
                    }
                    if (dataSnapshot.getKey().equals("comments")) {
                        commentsList.clear();
                        commentsLinearLayout.removeAllViews();
                        for (DataSnapshot commentDataSnapshot : dataSnapshot.getChildren()) {
                            Comment comment = commentDataSnapshot.getValue(Comment.class);
                            commentsList.add(comment);

                        }
                        for (Comment c : commentsList) {
                            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comment, null);
                            TextView authorView = view.findViewById(R.id.author_name);
                            TextView commentView = view.findViewById(R.id.comment_text);
                            ImageView profileView = view.findViewById(R.id.profile_image);
                            String profileImageUrl = c.getAuthorProfileUrl();
                            // Download image from URL and set to imageView
                            Picasso.with(getApplicationContext()).load(profileImageUrl).fit().centerCrop().into(profileView);
                            authorView.setText(c.getAuthor());
                            commentView.setText(c.getCommentMessage());
                            commentsLinearLayout.addView(view, 0);
                        }
                    }

                }

                likeBtn.setText(String.valueOf(num_likes[0]));
                collectBtn.setText(String.valueOf(num_collects[0]));
                commentBtn.setText(String.valueOf(num_comments[0]));

                DatabaseReference usersRef = firebaseDatabase.getReference("Users");
                // Get post ids of posts liked by current user and listen for changes
                usersRef.child(currentUser.getUid()).child("likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        likes.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String likedPid = (String) dataSnapshot.getValue();
                            likes.add(likedPid);
                        }
                        Log.d("likes", String.valueOf(likes.size()));
                        // if user likes, set like btn to filled heart, otherwise, to unfilled heart
                        if (likes.contains(pid)) {
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_solid, 0, 0, 0);
                        } else {
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                        }
                        // Get post ids of posts collected by current user and listen for changes
                        usersRef.child(currentUser.getUid()).child("collects").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                collects.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String collectPid = (String) dataSnapshot.getValue();
                                    collects.add(collectPid);
                                }

                                // if user collects, set collect btn to filled star, otherwise, to unfilled star
                                if (collects.contains(pid)) {
                                    collectBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_solid, 0, 0, 0);
                                } else {
                                    collectBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.collect, 0, 0, 0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        titleView = findViewById(R.id.post_title);
        descripView = findViewById(R.id.post_description);
        authorView = findViewById(R.id.author_name);
        titleView.setText(title);
        descripView.setText(description);
        authorView.setText(author);
        commentsLinearLayout = findViewById(R.id.commentsLinearLayout);
        imagesRecyclerView = findViewById(R.id.recyclerView);
        imagesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager imagesLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerView.setLayoutManager(imagesLinearLayoutManager);
        ImagesAdapter imagesAdapter = new ImagesAdapter(imageURLs, this, R.layout.post_details_image_view);
        imagesRecyclerView.setAdapter(imagesAdapter);

        // Handle click event on like button
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If this post hasn't been liked by user
                if (!likes.contains(pid)) {
                    // increment num of likes for this post by 1
                    firebaseDatabase.getReference("Posts").child(pid).child("likes").setValue(num_likes[0] + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // push to user's likes
                            DatabaseReference userLikesRef = firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("likes").push();
                            Log.d("likes", userLikesRef.getKey());
                            userLikesRef.setValue(pid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(getApplicationContext(), "You liked this post", Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });
                    // If this post has been liked by user
                } else {
                    // decrement num of likes for this post by 1
                    firebaseDatabase.getReference("Posts").child(pid).child("likes").setValue(num_likes[0] - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Remove this post from posts that user likes
                            DatabaseReference likeRef = firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("likes");
                            Query query = likeRef.orderByValue().equalTo(pid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                    Toast.makeText(getApplicationContext(), "You cancelled like", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });
                }

            }
        });

        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                // If this post hasn't been collected by user
                if (!collects.contains(pid)) {
                    // increment num of collects for this post by 1
                    num_collects[0] += 1;
                    firebaseDatabase.getReference("Posts").child(pid).child("collects").setValue(num_collects[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // push to user's collects
                            DatabaseReference userLikesRef = firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("collects").push();
                            userLikesRef.setValue(pid);
                            Toast.makeText(getApplicationContext(), "You collected this post", Toast.LENGTH_LONG).show();

                        }
                    });
                    // If this post has been collected by user
                } else {
                    // decrement num of collects for this post by 1
                    num_collects[0] -= 1;
                    firebaseDatabase.getReference("Posts").child(pid).child("collects").setValue(num_collects[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Remove this post from posts that user collects
                            DatabaseReference collectRef = firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("collects");
                            Query query = collectRef.orderByValue().equalTo(pid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Toast.makeText(getApplicationContext(), "You cancelled collect", Toast.LENGTH_LONG).show();

                        }

                    });
                }

            }
        });
        // Handle click event on comment button
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsLinearLayout.getParent().requestChildFocus(commentsLinearLayout, commentsLinearLayout);
            }
        });
        // Handle click event on send button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = commentTextField.getText().toString();
                if (!commentText.isEmpty()) {
                    // Get current user's name
                    firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error in fetching data", task.getException());
                                // push comments to this post
                            } else {
                                String authorName = task.getResult().getValue(String.class);
                                firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("imageUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        String profileImageUrl = task.getResult().getValue(String.class);
                                        Comment comment = new Comment(authorName, commentText, profileImageUrl);
                                        DatabaseReference commentRef = firebaseDatabase.getReference().child("Posts").child(pid).child("comments").push();
                                        commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firebaseDatabase.getReference().child("Posts").child(pid).child("numComments").setValue(num_comments[0] + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getApplicationContext(), "Comment Added successfully", Toast.LENGTH_LONG).show();
                                                        commentTextField.setText("");
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });


                            }
                        }
                    });


                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
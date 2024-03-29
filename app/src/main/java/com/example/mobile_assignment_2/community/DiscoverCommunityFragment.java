package com.example.mobile_assignment_2.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobile_assignment_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author: Wentao Xie
 * @description: Lists the information about all community.
 * Click join button to join and redirect to community Detail page.
 * Click img in each community, pop up the information about this community, including name, description and name.
 * Click plus button redirect to create community page.
 * Click My Community redirect to My Community page.
 */
public class DiscoverCommunityFragment extends Fragment {
    private ArrayList<Communitypost> posts = new ArrayList<>();
    private RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscoverCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverCommunityFragment newInstance(String param1, String param2) {
        DiscoverCommunityFragment fragment = new DiscoverCommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);


        ArrayList<String> communityPosts = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Get a reference to users
        DatabaseReference commRef = firebaseDatabase.getReference("Community");

        commRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference postsRef = firebaseDatabase.getReference("Community");
                postsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Communitypost post = dataSnapshot.getValue(Communitypost.class);
                            posts.add(post);
                        }

                        recyclerView = view.findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        CustomAdapter customAdapter = new CustomAdapter(posts);
                        customAdapter.setClickListener(new commPostItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Communitypost post = posts.get(position);
                                Intent i = new Intent(getActivity(), CommunityDetail.class);
                                i.putExtra("cid", post.getCid());
                                i.putExtra("communityName", post.getCommName());
                                i.putExtra("imageURLs", post.getImageUrls());
                                startActivity(i);
                            }
                        });
                        recyclerView.setAdapter(customAdapter);


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


        return view;
    }


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<Communitypost> posts = new ArrayList<Communitypost>();
        private commPostItemClickListener communityPostItemClickListener;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleView;
            ImageView imgView;
            ImageButton ib;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Communitypost post = posts.get(getAdapterPosition());
                        String postName = post.getCommName();
                        String postDesc = post.getComDescription();
                        String postIngUrl = post.getImageUrls();

                        Bundle bundle = new Bundle();
                        bundle.putString("postName", postName);
                        bundle.putString("postDesc", postDesc);
                        bundle.putString("postIngUrl", postIngUrl);
                        BottomSheet bottomSheet = new BottomSheet();
                        bottomSheet.setArguments(bundle);
                        bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");


                    }
                });
                titleView = view.findViewById(R.id.communityName);
                imgView = view.findViewById(R.id.communityImg);
                ib = view.findViewById(R.id.join_community_button);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("join", "yes");
                        Communitypost post = posts.get(getAdapterPosition());
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                        mDatabase.child("Users").child(currentUser.getUid()).child("commLst").child(posts.get(getAdapterPosition()).getCid()).setValue("true");
                        Intent i = new Intent(getActivity(), CommunityDetail.class);
                        i.putExtra("cid", post.getCid());
                        i.putExtra("communityName", post.getCommName());
                        Log.d("msgonclick", "this is an on click!!!!!!!!!!!!!!!");
                        i.putExtra("imageURLs", post.getImageUrls());
                        startActivity(i);
                    }
                });
            }

        }


        public CustomAdapter(ArrayList<Communitypost> posts) {
            this.posts = posts;

        }

        public void setClickListener(commPostItemClickListener communityPostItemClickListener) {
            this.communityPostItemClickListener = communityPostItemClickListener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.community_joinin_posts, viewGroup, false);

            return new CustomAdapter.ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, final int position) {

            viewHolder.titleView.setText(posts.get(position).getCommName());
            String imageUrl = posts.get(position).getImageUrls();

            // Download image from URL and set to imageView
            Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(viewHolder.imgView);


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return posts.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }
}
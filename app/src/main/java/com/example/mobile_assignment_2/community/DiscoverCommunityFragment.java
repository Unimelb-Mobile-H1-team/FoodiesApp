package com.example.mobile_assignment_2.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobile_assignment_2.Post;
import com.example.mobile_assignment_2.R;
import com.example.mobile_assignment_2.home.PostDetails;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverCommunityFragment extends Fragment implements commPostItemClickListener{
    private ArrayList<Communitypost> posts = new ArrayList<>();
    private RecyclerView recyclerView;

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

        posts.add(new Communitypost(R.drawable.food, "commName1"));
        posts.add(new Communitypost(R.drawable.food2, "commName2"));
        posts.add(new Communitypost(R.drawable.food, "commName3"));
        posts.add(new Communitypost(R.drawable.food2, "commName4"));

        // Inflate the layout for this fragment

        recyclerView =  view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(posts);
        customAdapter.setClickListener(this);
        recyclerView.setAdapter(customAdapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {
        Communitypost post = posts.get(position);
        Intent i = new Intent(getActivity(), CommunityDetail.class);
//        i.putExtra("title", post.getCommName());
//        i.putExtra("description", post.getDescription());
//        i.putExtra("author", post.getAuthor());
//        Log.i("hello", post.getTitle());
//        Log.i("hello", post.getDescription());
        startActivity(i);
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<Communitypost> posts = new ArrayList<Communitypost>();
        private commPostItemClickListener communityPostItemClickListener;
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView titleView;
            ImageView imgView;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                view.setOnClickListener(this);
                titleView =   view.findViewById(R.id.communityName);
                imgView =  view.findViewById(R.id.communityImg);
            }

            @Override
            public void onClick(View view) {
                if(communityPostItemClickListener != null) {
                    communityPostItemClickListener.onClick(view, getAbsoluteAdapterPosition());
                }
            }

        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param posts String[] containing the data to populate views to be used
         * by RecyclerView.
         */
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

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            //Post post = (Post) posts.get(position);
            viewHolder.titleView.setText(posts.get(position).getCommName());

            viewHolder.imgView.setBackgroundResource(posts.get(position).getCommImage());

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
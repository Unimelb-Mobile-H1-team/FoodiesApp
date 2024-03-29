package com.example.mobile_assignment_2.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_assignment_2.R;
import com.example.mobile_assignment_2.message.ChatWindowActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

/**
 * @author: Yao-Wen Chang
 * @description: This file connects the ChatFragment and ChatListData, when clicking the button of list
 * data, the chat window will show up, and user can chat with their friends
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private ChatListData[] chatListData;

    public ChatListAdapter(ChatListData[] _chatListData) {
        this.chatListData = _chatListData;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View chatListItem = layoutInflater.inflate(R.layout.chat_list_item, parent, false);
        ChatListAdapter.ViewHolder viewHolder = new ChatListAdapter.ViewHolder(chatListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final ChatListData chatListItem = chatListData[position];
        holder.textViewUsername.setText(chatListData[position].getUsername());
        holder.textViewLastMsg.setText(chatListData[position].getLastMsg());
        new DownloadImageFromInternet((ImageView) holder.imageViewAvatar).execute(chatListData[position].getImgURL());
        //Log.d("new message is coming1", chatListData[position]);

        if(chatListData[position].getHasRead().equals("false")){
            Log.d("new message is coming2", chatListData[position].getHasRead());
            holder.newMessageCircle.setVisibility(View.VISIBLE);
        }
        else{
            holder.newMessageCircle.setVisibility(View.INVISIBLE);
        }


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.newMessageCircle.getDrawable().clearColorFilter();

                FriendListAdapter.username = "";
                FriendListAdapter.userID = "";
                // Redirect to user page
                Toast.makeText(view.getContext(),"click on item: "+chatListItem.getUsername(),Toast.LENGTH_LONG).show();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser fuser = firebaseAuth.getCurrentUser();
                // Redirect to user page
                FriendListAdapter.username = chatListItem.getUsername();
                FriendListAdapter.userID  = chatListData[position].getFriendID();
                FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("friends").child(FriendListAdapter.userID).child("hasRead").setValue("true");
                holder.newMessageCircle.setVisibility(view.INVISIBLE);
                Toast.makeText(view.getContext(), "click on item: " + chatListItem.getUsername(), Toast.LENGTH_LONG).show();

                // Redirect to user page
                FriendListAdapter.username = chatListItem.getUsername();
                FriendListAdapter.userID = chatListData[position].getFriendID();
                Context context = view.getContext();
                Intent intent = new Intent(context, ChatWindowActivity.class);
                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return chatListData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewAvatar;
        public TextView textViewUsername, textViewLastMsg;
        public RelativeLayout relativeLayout;
        public ImageView newMessageCircle;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageViewAvatar = (ImageView) itemView.findViewById(R.id.imageViewAvatar);
            this.textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            this.textViewLastMsg = (TextView) itemView.findViewById(R.id.textViewLastMsg);
            this.newMessageCircle = (ImageView) itemView.findViewById(R.id.newMessageCircle);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(imageView.getContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}


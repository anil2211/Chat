package com.example.anil.chat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anil3 on 17-04-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{

    private List<Messages>userMessageList;
     private FirebaseAuth mAuth;
     private DatabaseReference userDatabaseReference;

    public MessageAdapter(List<Messages>userMessageList)
    {
        this.userMessageList=userMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_of_user,parent,false);

      mAuth=FirebaseAuth.getInstance();


        return  new
                MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position)
    {

        String message_sender_id=mAuth.getCurrentUser().getUid();
        Messages messages=userMessageList.get(position);


       String fromUserId=messages.getFrom();
        String frommessagetype=messages.getType();

        userDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String userName=dataSnapshot.child("user_name").getValue().toString();
                String userImage=dataSnapshot.child("user_thumb_image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        if (frommessagetype.equals("text"))

        {

            holder.messagePicture.setVisibility(View.INVISIBLE);

            if(fromUserId.equals(message_sender_id))
            {

                holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setGravity(Gravity.RIGHT);
            }

            else
            {
                holder.messageText.setBackgroundResource(R.drawable.msg_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setGravity(Gravity.LEFT);

            }


            holder.messageText.setText(messages.getMessage());

        }
else
        {

            holder.messageText.setVisibility(View.INVISIBLE);
           // holder.messageText.setPadding(0,0,0,0);
            Picasso.with(holder.userProfileImage.getContext()).load(messages.getMessage()).placeholder(R.drawable.facebookavatar).into(holder.messagePicture);

        }





    }

    @Override
    public int getItemCount()
    {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText;
        public CircleImageView userProfileImage;
       public ImageView messagePicture;

        public MessageViewHolder(View view)
        {
            super(view);
            messageText=view.findViewById(R.id.msg_text);
            messagePicture=view.findViewById(R.id.message_image_view);
            userProfileImage=view.findViewById(R.id.msg_profile_img);

        }

    }

}

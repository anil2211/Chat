package com.example.anil.chat;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String msgReceiverId;
    private String getMsgReceiverName;
    private Toolbar chatToolbar;
    private TextView userNameTitle;
    private TextView userLastSeen;
    private CircleImageView userChatProfileImage;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth=FirebaseAuth.getInstance();
       // msgSenderId=mAuth.getCurrentUser().getUid();


        msgReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        getMsgReceiverName=getIntent().getExtras().get("user_name").toString();

        chatToolbar=findViewById(R.id.chat_bar);
        setSupportActionBar(chatToolbar);

        //for chat custom bar on chat menu
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//back button
        actionBar.setDisplayShowCustomEnabled(true);//for back button above

        //we need layout inflator
        LayoutInflater layoutInflater=(LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //to set view for this
        View action_bar_view= layoutInflater.inflate(R.layout.chat_custom_bar,null);
        //connect action bar to the action view
        actionBar.setCustomView(action_bar_view);

        //find view by ID
        userNameTitle=findViewById(R.id.custom_profile_name);
        userLastSeen=findViewById(R.id.custom_last_seen);
        userChatProfileImage=findViewById(R.id.custom_profile_image);

        userNameTitle.setText(getMsgReceiverName);

        rootRef.child("Users").child(msgReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final  String online=dataSnapshot.child("online").getValue().toString();
                //reteive image
                final String userThumb= dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(ChatActivity.this).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.facebookavatar)
                        .into(userChatProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ChatActivity.this).load(userThumb).placeholder(R.drawable.facebookavatar).into(userChatProfileImage);


                            }
                        });



                //for status online retriving
                if (online.equals("true"))
                {
                    userLastSeen.setText("online");
                }
                else
                {
                    userLastSeen.setText(online);
                    //LastSeenTime getTime=new LastSeenTime();
                    //long last_seen=Long.parseLong(online);
                    //String lastSeenDisplayTime=getTime.getTimeAgo(last_seen,getApplicationContext());
                    //userLastSeen.setText(lastSeenDisplayTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}

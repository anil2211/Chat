package com.example.anil.chat;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button sendFriendrequestButton;
    private Button declineFriendRequest;
    private TextView profileName;
    private TextView profileStatus;
    private ImageView profileImage;
    private DatabaseReference UserReference;//for firebase refence

    //for friend request
    private String CURRENT_STATE;//variable
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;
    private DatabaseReference friendsReferrence;//for accepting the request in to database
    private DatabaseReference notificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        FriendRequestReference.keepSynced(true);

        notificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");

        notificationRef.keepSynced(true);

        //for online user
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        friendsReferrence = FirebaseDatabase.getInstance().getReference().child("Friends");

        //firebase reference offlne
        friendsReferrence.keepSynced(true);

        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        //Toast.makeText(ProfileActivity.this, visit_user_id, Toast.LENGTH_SHORT).show();

        //find view by Id
        sendFriendrequestButton = findViewById(R.id.profile_visit_send_btn);
        declineFriendRequest = findViewById(R.id.profile_visit_decline_btn);
        profileName = findViewById(R.id.profile_visit_user);
        profileStatus = findViewById(R.id.profile_visit_status);
        profileImage = findViewById(R.id.profile_visit_image);


        CURRENT_STATE = "not_friends";


        UserReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //to retrive data from firebase
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.facebookavatar).into(profileImage);

                FriendRequestReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // if (dataSnapshot.exists())
                        //{
                        if (dataSnapshot.hasChild(receiver_user_id)) {
                            String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                            if (req_type.equals("sent")) {
                                CURRENT_STATE = "request_sent";
                                sendFriendrequestButton.setText("Cancel Friend Request");

                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                declineFriendRequest.setEnabled(false);


                            }
                            else if (req_type.equals("received")) {
                                CURRENT_STATE = "request_received";
                                sendFriendrequestButton.setText("Accept Friend Request");

                                declineFriendRequest.setVisibility(View.VISIBLE);
                                declineFriendRequest.setEnabled(true);

                                declineFriendRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        declineFriendRequest();
                                    }
                                });

                            }
                        }
                        //  }
                        else
                        {
                            friendsReferrence.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                        CURRENT_STATE = "friends";
                                        sendFriendrequestButton.setText("Unfriend This Person");

                                        declineFriendRequest.setVisibility(View.INVISIBLE);
                                        declineFriendRequest.setEnabled(false);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//for decline button
        declineFriendRequest.setVisibility(View.INVISIBLE);
        declineFriendRequest.setEnabled(false);




        if (!sender_user_id.equals(receiver_user_id)) {
            sendFriendrequestButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    sendFriendrequestButton.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")) {
                        sendFriendrequestToPerson();
                    }

                    if (CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }

                    //for accept request
                    if (CURRENT_STATE.equals("request_received")) {
                        AcceptFriendRequest();
                    }

                    //for unfriend
                    if (CURRENT_STATE.equals("friends")) {
                        UnfriendsFriend();
                    }

                }


            });
        } else {
            declineFriendRequest.setVisibility(View.INVISIBLE);
            sendFriendrequestButton.setVisibility(View.INVISIBLE);

        }
    }



    private void declineFriendRequest()
    {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendFriendrequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                sendFriendrequestButton.setText("send Friend Request");
                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                declineFriendRequest.setEnabled(false);

                            }
                        }
                    });
                }

            }
        });
    }


    private void UnfriendsFriend() {
        friendsReferrence.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    friendsReferrence.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendFriendrequestButton.setEnabled(true);
                                //for again send requ
                                CURRENT_STATE = "not_friends";
                                sendFriendrequestButton.setText("Send Friend Request");

                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                declineFriendRequest.setEnabled(false);


                            }
                        }
                    });
                }
            }
        });

    }

    //for accept friend request
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void AcceptFriendRequest()

    {
        //on which date they become friend
        android.icu.util.Calendar callFordate = android.icu.util.Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentdate.format(callFordate);

        friendsReferrence.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendsReferrence.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                sendFriendrequestButton.setEnabled(true);
                                                                CURRENT_STATE = "friends";
                                                                sendFriendrequestButton.setText("UnFriend this Person");

                                                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                                                declineFriendRequest.setEnabled(false);
                                                            }
                                                        }
                                                    });
                                                }

                                            }
                                        });
                                    }
                                });

                    }
                });
    }

    //if error so change sender and receiver placee
    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendFriendrequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                sendFriendrequestButton.setText("send Friend Request");
                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                declineFriendRequest.setEnabled(false);

                            }
                        }
                    });
                }

            }
        });
    }

    private void sendFriendrequestToPerson() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).child("request_type")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {

                                        HashMap<String , String> notificationsData=new HashMap<String,String>();
                                        notificationsData.put("from",sender_user_id);
                                        notificationsData.put("type","request");

                                        notificationRef.child(receiver_user_id).push().setValue(notificationsData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    sendFriendrequestButton.setEnabled(true);
                                                    CURRENT_STATE = "request_sent";
                                                    sendFriendrequestButton.setText("cancel Friend Request");

                                                    declineFriendRequest.setVisibility(View.INVISIBLE);
                                                    declineFriendRequest.setEnabled(false);
                                                }
                                            }
                                        });



                                    }
                                }
                            });
                        }
                    }
                });
    }

}


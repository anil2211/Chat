package com.example.anil.chat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendsList;
    private DatabaseReference friendsReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;

    String online_user_id;
    private View myMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        //for recycler view
        myFriendsList = myMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friendsReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);

        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return myMainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                        (
                                Friends.class,
                                //retrive all user
                                R.layout.all_users_display_layout,
                                FriendsViewHolder.class,
                                friendsReference

                        ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                        viewHolder.setDate(model.getDate());
                        final String list_user_id = getRef(position).getKey();
                        userReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("user_name").getValue().toString();
                                String thumbImage = dataSnapshot.child("user_image").getValue().toString();

                                //for online image
                                if (dataSnapshot.hasChild("online")) {
                                    String online_status =dataSnapshot.child("online").getValue().toString();
                                    viewHolder.setUserOnline(online_status);
                                }

                                viewHolder.setUserName(userName);
                                viewHolder.setThumbImage(thumbImage, getContext());

                                //for chat screen from friend list dialog box pop out

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CharSequence option[] = new CharSequence[]
                                                {
                                                        userName + "'s Profile",
                                                        "send Message"
                                                        //we can give as many option on this area
                                                };

                                        //dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Select Options");

                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int position) {
                                                if (position == 0)

                                                {
                                                    Intent profileintent = new Intent(getContext(), ProfileActivity.class);
                                                    profileintent.putExtra("visit_user_id", list_user_id);
                                                    startActivity(profileintent);

                                                }
                                                if (position == 1)

                                                {
                                                    //for validation if user not logged in sice long time so  it will not crashed
                                                    if (dataSnapshot.child("online").exists())
                                                    {
                                                        Intent chatintent = new Intent(getContext(), ChatActivity.class);
                                                        chatintent.putExtra("visit_user_id", list_user_id);
                                                        chatintent.putExtra("user_name", userName);
                                                        startActivity(chatintent);
                                                    } else   //for last seen
                                                    {
                                                        userReference.child(list_user_id).child("online")
                                                                .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid)
                                                            {
                                                                Intent chatintent = new Intent(getContext(), ChatActivity.class);
                                                                chatintent.putExtra("visit_user_id", list_user_id);
                                                                chatintent.putExtra("user_name", userName);
                                                                startActivity(chatintent);

                                                            }
                                                        });

                                                    }

                                                }


                                            }
                                        });


                                        builder.show();


                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
        myFriendsList.setAdapter(firebaseRecyclerAdapter);
    }

    //static class

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        //contructor
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView sinceFriendsData = mView.findViewById(R.id.all_users_status);
            sinceFriendsData.setText(date);
        }

        public void setUserName(String userName) {
            TextView userNameDisplay = mView.findViewById(R.id.all_user_username);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String thumbImage, final Context ctx) {

            final CircleImageView image = mView.findViewById(R.id.all_user_image);


            //  Picasso.with(ctx).load(user_image).placeholder(R.drawable.facebookavatar).into(image);
            //offline image
            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.facebookavatar)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.facebookavatar).into(image);


                        }
                    });




        }

        public void setUserOnline(String online_status) {
            ImageView onlineStatusView = mView.findViewById(R.id.online_status);
            if (online_status.equals("true")) {
                onlineStatusView.setVisibility(View.VISIBLE);
            } else {
                onlineStatusView.setVisibility(View.INVISIBLE);

            }
        }
    }

}

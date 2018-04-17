package com.example.anil.chat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by anil3 on 12-04-2018.
 */

public class LetsRattleOffline extends Application {


    //for who is online user

    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currenrUser;


    @Override
    public void onCreate()

    {

        super.onCreate();
        //string type of variable offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //for image piccaso to load image offline
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        mAuth=FirebaseAuth.getInstance();
        //if user is online
        currenrUser=mAuth.getCurrentUser();
        if(currenrUser !=null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid();
            //to check online user is not or it is online
            userReference=FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    userReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    // userReference.child("online").setValue(true);//this showing after minimise the online user also



                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }

    }
}
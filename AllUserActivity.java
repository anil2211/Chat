package com.example.anil.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private RecyclerView allUserList;
    private DatabaseReference dbr;  //all data base reference user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);


        //for users page toolbar only

        mtoolbar = findViewById(R.id.all_users_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //for back button


        //for recyclerview
        allUserList = (RecyclerView) findViewById(R.id.all_user_list);
        //for all user layout display
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));
        // dbr= FirebaseDatabase.getInstance().getReference().child("users");
        dbr = FirebaseDatabase.getInstance().getReference().child("Users");
        dbr.keepSynced(true);//for ofline



    }

    @Override
    protected void onStart() {
        super.onStart();
        //firebase recycler adapter to retrieve firebase databse to database  use library also
        //it need to parameter one module one is view holder so first create that class java class alluser
        //second parameter after the on start method public  view holder
        FirebaseRecyclerAdapter<Allusers, AllUsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Allusers, AllUsersViewHolder>
                (
                        //four parameter
                        Allusers.class,
                        R.layout.all_users_display_layout, //this is created for the display user layout
                        AllUsersViewHolder.class,
                        dbr

                ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, Allusers model, final int position) {

                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_thumb_image(getApplicationContext(), model.getUser_thumb_image());

                //for accept and reject request
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent intent = new Intent(AllUserActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);//all data of on which we click
                        startActivity(intent);
                    }
                });
/*


                //for accept and reject request
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent intent = new Intent(AllUserActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);//all data of on which we click
                        startActivity(intent);
                    }
                });
*/

            }
        };

//fire base adapter
        allUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mview = itemView;//firebase adapter

        }

        public void setUser_name(String user_name) {
           TextView name = mview.findViewById(R.id.all_user_username);
           name.setText(user_name);
        }

        public void setUser_status(String user_status) {
            TextView status= mview.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }
       /* public void setUser_image(String user_image)
        {
            TextView image =(TextView) mview.findViewById(all_users_status);//fron textview
            image.setText(user_image);
        }

*/

        public void setUser_thumb_image(final Context ctx, final String user_thumb_image)
        {
            final CircleImageView thumb_image = mview.findViewById(R.id.all_user_image);


             //Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.facebookavatar).into(thumb_image);
            //offline image
            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.facebookavatar)
                    .into(thumb_image, new Callback() {
                @Override
                public void onSuccess()
                {

                }

                @Override
                public void onError()
                {
                    Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.facebookavatar).into(thumb_image);


                }
            });


        }

    }
}


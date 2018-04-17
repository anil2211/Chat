package com.example.anil.chat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    //for validation
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar ;//for back toolbar


    private ViewPager myViewPager;
    private TabLayout mytabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

    FirebaseUser currentUser;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String online_user_id = mAuth.getCurrentUser().getUid();
            userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);


        }
        //for tabs menu
        myViewPager=findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter=new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        mytabLayout = findViewById(R.id.main_tabs);
        // mytabLayout.setupWithViewPager(myviewPager);
        mytabLayout.setupWithViewPager(myViewPager);


        //for toolbar
        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("LetsRattleOn");
    }

    //for user validation to check wether it is sign in or not
    @Override
    protected void onStart()
    {
        super.onStart();
        currentUser = mAuth.getCurrentUser();//unique id for current user
        if (currentUser == null)     //if user not login
        {

           /* Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//user will not go to main activity
            startActivity(intent);
            finish();*///to end activity

            LogOutUser();

        }
        else if(currentUser != null)
        {
            userReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null) {

            userReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void LogOutUser()
    {

        Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//user will not go to main activity
        startActivity(intent);
        finish();

    }


    //menu option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);//fro menu directory
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //for log out
        if (item.getItemId() == R.id.main_logoutBtn) {
            if(currentUser != null)
            {
                userReference.child("online").setValue(ServerValue.TIMESTAMP);
            }
            mAuth.signOut();   //logout from
            LogOutUser();
        }
        // for acount setting
        if (item.getItemId() == R.id.main_Account_settingBtn) {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        }

        if (item.getItemId() == R.id.main_all_users_btn) {
            Intent intent = new Intent(MainActivity.this, AllUserActivity.class);
            startActivity(intent);
        }

        return true;
    }

}

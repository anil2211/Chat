package com.example.anil.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisteredActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbr;
    private Toolbar mtoolbar;
    private ProgressDialog loadingBar;
    private EditText registerUserName; //for authentication
    private EditText registerUserEmail;
    private EditText registerPassword;
    private Button createAccountBtn ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);


   //for store in database
        mAuth=FirebaseAuth.getInstance();

        //for toolbar  and for back button change in manifest
       /* mtoolbar=findViewById(R.id.registerd_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*///for button back
        // for casting
            registerUserName=findViewById(R.id.register_name);
            registerUserEmail=findViewById(R.id.register_email);
            registerPassword=findViewById(R.id.register_password);
            createAccountBtn=findViewById(R.id.create_account_btn);
        loadingBar=new ProgressDialog(this);//for loading bar

//for button
        createAccountBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               //to get the data from user
                String name=registerUserName.getText().toString();
                String email=registerUserEmail.getText().toString();
                String password=registerPassword.getText().toString();

                //create method to call
                RegisterAccount(name,email,password);
            }
        });
    }

    //method for user register
    private void RegisterAccount(final String name, String email, String password)
    {
        //for empty validation
       if (TextUtils.isEmpty(name))
       {
           Toast.makeText(RegisteredActivity.this, "Please write your name", Toast.LENGTH_LONG).show();
       }
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisteredActivity.this, "Please write your email", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisteredActivity.this, "Please write your password", Toast.LENGTH_LONG).show();
        }

        else
        {
            //for loading bar
            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("Please wait,while we are creating your account");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {

                    if (task.isSuccessful())
                    {
                     //if succesful to send main activity
                    // to store database
                        String current_user_Id = mAuth.getCurrentUser().getUid();
                        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);
                        dbr.child("user_name").setValue(name);
                        dbr.child("user_status").setValue("Hey Guys..! Lets RattleOn");
                        dbr.child("user_image").setValue("facebookavatar");
                        dbr.child("user_thumb_image").setValue("facebookavatar").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Intent mainIntent = new Intent(RegisteredActivity.this, MainActivity.class);
                                    //not come back again account
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            }
                        });
                    }
                    else
                    {

                        Toast.makeText(RegisteredActivity.this, "Something went wrong,try again", Toast.LENGTH_SHORT).show();
                    }
                  loadingBar.dismiss();
                }
            });
        }
    }
}

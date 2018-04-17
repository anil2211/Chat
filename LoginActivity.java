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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity

{

    //for toolbar
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private Button login_button;
    private EditText login_email;
    private EditText login_password;
    private ProgressDialog loadinBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //for toolbar  and for back button change in manifest
       /* mtoolbar=findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*///for button back

        mAuth = FirebaseAuth.getInstance();

        login_button = findViewById(R.id.login_button);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        loadinBar = new ProgressDialog(this);


        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();

                LoginUserAccount(email, password);
            }
        });
    }


    private void LoginUserAccount(String email, String password)
    {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this, " Please fill your Email", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this, " Please fill your password", Toast.LENGTH_SHORT).show();
        }


        loadinBar.setTitle("Login Account");
        loadinBar.setMessage("please wait,we are login");
        loadinBar.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    //for token  notification
                   // String online_user_id=mAuth.getCurrentUser().getUid();
                    //String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    /*userReference.child(online_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });*/

                       Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                }
                else
                    {
                    Toast.makeText(LoginActivity.this, "Wrong Email or Password, Please check your Email and Password", Toast.LENGTH_SHORT).show();
                    }
                loadinBar.dismiss();
            }
        });
    }
}


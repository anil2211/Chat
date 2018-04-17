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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    //for back button
    private Toolbar mtoolbar;
    //for button
    private Button SaveStatusButton;
    private EditText statusInput;
    //for loading bar
    private ProgressDialog loadingBar;
    //for store in database and also fetch root
    private DatabaseReference changeStatusRef;
    //current user
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        //for online user
        String user_id = mAuth.getCurrentUser().getUid();
        //reference to database for online user
        changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        //title and back button
        mtoolbar = findViewById(R.id.status_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SaveStatusButton = findViewById(R.id.save_status_button);
        statusInput = findViewById(R.id.status_input);
        //for loading bar
        loadingBar = new ProgressDialog(this);

        //for old status
        String old_status=getIntent().getExtras().get("user_status").toString();
        statusInput.setText(old_status);


        SaveStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_status = statusInput.getText().toString();
                //create method for change status
                ChangeProfileStatus(new_status);
            }

            private void ChangeProfileStatus(String new_status) {
                if (TextUtils.isEmpty(new_status)) {
                    Toast.makeText(StatusActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("change Profile Status");
                    loadingBar.setMessage("Please wait,upadting your status...");
                    loadingBar.show();
                    //for updating the status
                    changeStatusRef.child("user_status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Intent intent = new Intent(StatusActivity.this, SettingActivity.class);
                                startActivity(intent);
                                Toast.makeText(StatusActivity.this, "Profile status updated succesfully", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(StatusActivity.this, "Error Occured ", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });


    }
}

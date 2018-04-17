package com.example.anil.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity
{

    //for button
    private Button need_button;
    private Button already_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        //cast for button
        need_button=findViewById(R.id.need_button);
        already_button=findViewById(R.id.already_button);

        //for sending to account by sign or login

        need_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             //for send to new activity
                Intent intent = new Intent(StartPageActivity.this,RegisteredActivity.class);
                startActivity(intent);
            }
        });

        //for login button
        already_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


    }
}

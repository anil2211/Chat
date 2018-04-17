package com.example.anil.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {
    private CircleImageView settingImage;
    private TextView setingName;
    private TextView settingStatus;
    private Button changeImage;
    private Button changeStatus;
    private Toolbar mtoolbar;

    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    Bitmap thumb_bitmap = null;
    private StorageReference thumbImageRef;
    private ProgressDialog loadingbar;
    private final static int Gallery_Pick = 1;

    //to store image in the databse firebase
    private StorageReference storeProfileImage;
//for image compression
   /* Bitmap thumb_bitmap=null;
private StorageReference thumbImageRef;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        mtoolbar=findViewById(R.id.setting_page_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for button back

        //for database reference
        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();
        //for unique id

        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        dbr.keepSynced(true);

        //for image store reference
        //also root firebase storeage
        storeProfileImage = FirebaseStorage.getInstance().getReference().child("Profile Image");

        thumbImageRef =FirebaseStorage.getInstance().getReference().child("Thumb_Images");
        settingImage = findViewById(R.id.profile_image);
        setingName = findViewById(R.id.setting_user_name);
        settingStatus = findViewById(R.id.setting_user_status);
        changeImage = findViewById(R.id.setting_image_button);
        changeStatus = findViewById(R.id.setting_status_button);
        loadingbar = new ProgressDialog(this);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get user data     "for online user id which usesame in database"
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                //to display  name and status on account
                setingName.setText(name);
                settingStatus.setText(status);

                if (!image.equals("facebookavatar"))
                {
                    //  Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.facebookavatar).into(settingImage);

                    Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.facebookavatar).into(settingImage, new Callback() {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.facebookavatar).into(settingImage);

                        }
                    });

                }

              //  Picasso.with(SettingActivity.this).load(image).into(settingImage);
                //to set dafault  image for all user
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //for the crop the image in profile page

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);


            }
        });
        // change of status
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //for storing previou status
                String old_status = settingStatus.getText().toString();

                Intent statusIntent = new Intent(SettingActivity.this, StatusActivity.class);
                //for previous status
                statusIntent.putExtra("user_status", old_status);
                startActivity(statusIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            //for crop only
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        //for pic store in firebase and profile

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingbar.setTitle("Updating profile picture");
                loadingbar.setMessage("plzzz! wait we are updating your profile");
                loadingbar.show();
                Uri resultUri = result.getUri();


                File thumb_filePathUri=new File(resultUri.getPath());

                //for actual image tht take time to load in profile due to large
                //for this 1st use library

                //              File thumb_filePathUri=new File(resultUri.getPath());

                //for storing with user id in firebase data
                String user_id = mAuth.getCurrentUser().getUid();

                //for compression of image
                try
                {

                   thumb_bitmap =new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                 final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                StorageReference filePath = storeProfileImage.child(user_id + ".jpg");

                final StorageReference thumb_filePath= thumbImageRef.child(user_id + ".jpg");
                //create reference to store the into the firebase
                //          final StorageReference thumb_filePath=thumbImageRef.child(user_id + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {


                        if (task.isSuccessful()) {
                            Toast.makeText(SettingActivity.this, "saving your profile Picture.", Toast.LENGTH_LONG).show();
//get image

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            //for thumImage
                          UploadTask uploadTask= thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                                    if(task.isSuccessful())
                                    {
                                        Map update_user_data= new HashMap();
                                        update_user_data.put("user_image", downloadUrl);
                                        update_user_data.put("user_thumb_image",thumb_downloadUrl);
                                        dbr.updateChildren(update_user_data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {

                                                Toast.makeText(SettingActivity.this, "Picture Updated Successfully", Toast.LENGTH_SHORT).show();

                                                loadingbar.dismiss();
                                            }
                                        });
                                    }
                                }
                            });




                            //                  UploadTask uploadTask=thumb_filePath.putBytes(thumb_byte);

                        } else {
                            Toast.makeText(SettingActivity.this, "Error occured while uploading profile picture", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}


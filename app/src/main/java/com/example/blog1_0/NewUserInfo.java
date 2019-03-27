package com.example.blog1_0;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class NewUserInfo extends AppCompatActivity  {
    EditText profileInfo,setupName;
    ImageView setupImage;
    Button setupBtn;
    CheckBox salesmanCheck;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private ProgressBar setupProgress;
    private String user_id;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);
        profileInfo =findViewById(R.id.profileInfo);
        setupImage =findViewById(R.id.setup_image);
        setupName=findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        salesmanCheck =findViewById(R.id.salesmanCheck);
        setupProgress=findViewById(R.id.setup_progress);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //videos 6
        setupBtn.setEnabled(false);
        setupProgress.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().exists())
                    {
                        String name =task.getResult().getString("name");
                        String image =task.getResult().getString("image");
                        String privilege =task.getResult().getString("privilege");
                        setupName.setText(name);
                        if(privilege.equals("Salesman"))
                            salesmanCheck.setChecked(true);
                        profileInfo.append("Email: " + user.getEmail());
                        RequestOptions placeholderRequest =new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_profile_image);
                        mainImageURI =Uri.parse(image);
                        Glide.with(NewUserInfo.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                    else
                    {
                        Toast.makeText(NewUserInfo.this, "Please fill out your information", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    String error =task.getException().getMessage();
                    Toast.makeText(NewUserInfo.this, "FireStore Get Data Error :"+error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });


        firebaseFirestore.collection("Users/"+user_id+"/Following").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    Map<String,Object> followMap=new HashMap<>();
                    followMap.put("timestamp", FieldValue.serverTimestamp());
                    followMap.put("name",firebaseAuth.getCurrentUser().getDisplayName());
                    firebaseFirestore.collection("Users/"+user_id+"/Following").document(firebaseAuth.getCurrentUser().getUid()).set(followMap);
                }
            }
        });
    }



    //videos 5
    public void saveAccountBtn(View view) {
        final String user_name = setupName.getText().toString();
        if(!TextUtils.isEmpty(user_name) && mainImageURI !=null){
            setupProgress.setVisibility(View.VISIBLE);
            if(isChanged) {
                final StorageReference image_path = storageReference.child("profile_image").child(user_id + ".jpg");
                Task<Uri> urlTask = image_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(NewUserInfo.this, "Error :" + error, Toast.LENGTH_LONG).show();
                        }
                        // Continue with the task to get the download URL
                        return image_path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            storeFirestore(task, user_name);

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(NewUserInfo.this, "Image Error :" + error, Toast.LENGTH_SHORT).show();
                        }
                        setupProgress.setVisibility(View.INVISIBLE);
                    }
                });
            }else {
                storeFirestore(null,user_name);
            }
        }else {
            storeFirestore(null,user_name);
        }
    }
    private void storeFirestore(final Task<Uri> task, String user_name){
        Uri downloadUri ;
        if(task!=null) {
             downloadUri = task.getResult();
        }else {
                downloadUri=mainImageURI;
        }
        //videos 6
//       remmembre thes if have any error change(Replace) Object to String
        Map<String ,Object > userMap=new HashMap<>();
        userMap.put("name",user_name);
        userMap.put("image",downloadUri.toString());
        userMap.put("user_id",user_id);
        if(salesmanCheck.isChecked())
            userMap.put("privilege","Salesman");
        else
            userMap.put("privilege","Customer");
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewUserInfo.this, "User information are Updated ", Toast.LENGTH_LONG).show();

                    //to divided between salesman and Customer
                    if(salesmanCheck.isChecked()){
                    Intent intent =new Intent(NewUserInfo.this,MainActivity.class);
                    startActivity(intent);
                    finish();}else {
                        Intent intent =new Intent(NewUserInfo.this,Customer.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    String error =task.getException().getMessage();
                    Toast.makeText(NewUserInfo.this, "FireStore Image Error :"+error, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        firebaseFirestore.collection("Users/"+user_id).document("Following").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (!task.getResult().exists()){
//                    firebaseFirestore.collection("Users/"+user_id).document("Following").set(user_id);
//                }
//            }
//        });
    }

    public void clickProfileImage(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(NewUserInfo.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(NewUserInfo.this, "Permission Denied", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(NewUserInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {

                BringImagePicker();

            }

        } else {

            BringImagePicker();

        }
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(NewUserInfo.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(NewUserInfo.this, "Crop Image Error :"+error, Toast.LENGTH_SHORT).show();

            }
        }

    }

}


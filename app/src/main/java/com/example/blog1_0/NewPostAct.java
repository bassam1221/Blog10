package com.example.blog1_0;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostAct extends AppCompatActivity {
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;
    private Uri postImageUri ;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar newPostToolbar;
    private FirebaseAuth firebaseAuth;
    private String currenrt_user_id;
    private Spinner newPostOfferType;
    private EditText newPostPriceText,NewPostfromtext,NewPostTotext,newPostName;
    private TextView newPostToLabile,newPostFromLabile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        newPostProgress=findViewById(R.id.new_post_progress);
        newPostDesc =findViewById(R.id.new_post_desc);
        newPostImage= findViewById(R.id.new_post_image);
        firebaseAuth =FirebaseAuth.getInstance();
        currenrt_user_id= firebaseAuth.getCurrentUser().getUid();
        newPostOfferType=findViewById(R.id.newPostOfferType);
        newPostName=findViewById(R.id.newPostItemName);
        newPostPriceText=findViewById(R.id.new_post_price);
        newPostToLabile =findViewById(R.id.new_post_to);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore =FirebaseFirestore.getInstance();
        setSupportActionBar(newPostToolbar);


    }

    public void newPostImageClick(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setAspectRatio(1, 1)
                .start(NewPostAct.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri= result.getUri();
                newPostImage.setImageURI(postImageUri);
                newPostImage.setPadding(5,5,5,5);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void postOfferbtn(View view)
    {
        final String desc =newPostDesc.getText().toString();
        final String itemName=newPostName.getText().toString();
        final String price =newPostPriceText.getText().toString();
        final String toDate=newPostToLabile.getText().toString();
        final Double number=Double.parseDouble(price);

        Date d = new Date();
        final CharSequence s  = DateFormat.format("dd/MM/yyyy\"", d.getTime());

        Log.d("DATE tast ",s.toString());
        if (!TextUtils.isEmpty(desc) && postImageUri!=null && !TextUtils.isEmpty(itemName) &&!TextUtils.isEmpty(price)){
            Log.d("postImage",desc);
            newPostProgress.setVisibility(View.VISIBLE);
            String randomName = FieldValue.serverTimestamp().toString();
            final StorageReference filePath =storageReference.child("post_image").child(randomName+"jpg");
            Task<Uri> urlTask = filePath.putFile(postImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        String error = task.getException().getMessage();
                        Toast.makeText(NewPostAct.this, "Error :" + error, Toast.LENGTH_LONG).show();
                    }
                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d("postImage","successful");
                        currenrt_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Uri downloadUri =task.getResult();
                        Map<String,Object> postMap=new HashMap<>();
                        postMap.put("imageUrl",downloadUri.toString());
                        postMap.put("itemName",itemName);
                        postMap.put("price",number);
                        postMap.put("desc",desc);
                        postMap.put("dateTo", toDate);
                        postMap.put("user_id",currenrt_user_id);
                        postMap.put("timestamp",s.toString());
                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(NewPostAct.this, "post was add ", Toast.LENGTH_LONG).show();
                                    finish();
                                }else {

                                }
                                newPostProgress.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else {
                        newPostProgress.setVisibility(View.INVISIBLE);
                        String error = task.getException().getMessage();
                        Toast.makeText(NewPostAct.this, "Image Error :" + error, Toast.LENGTH_SHORT).show();
                    }
                    newPostProgress.setVisibility(View.INVISIBLE);
                }
            });

        }else {
            Toast.makeText(this, "Fill all fields ", Toast.LENGTH_LONG).show();
        }


    }
}

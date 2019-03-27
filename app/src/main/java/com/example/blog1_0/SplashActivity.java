package com.example.blog1_0;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private static final String TAG = "EmailPasswordActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            Intent intent;
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    user_id = firebaseAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if (task.getResult().exists())
                                {

                                    String privilege =task.getResult().getString("privilege");
                                    if(privilege.equals("Salesman"))
                                    {
                                        intent =new Intent(SplashActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        intent =new Intent(SplashActivity.this,Customer.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                                else
                                {
                                    intent =new Intent(SplashActivity.this,NewUserInfo.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                            else
                            {
                                String error =task.getException().getMessage();
                                Toast.makeText(SplashActivity.this, "FireStore Get Data Error :"+error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    intent =new Intent(SplashActivity.this,login.class);
                    startActivity(intent);
                    finish();

                }



            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



}

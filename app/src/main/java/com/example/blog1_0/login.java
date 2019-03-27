package com.example.blog1_0;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.URL;

public class login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private ProgressDialog mProgressDialog;

    EditText loginEmailText, loginPassText;
    Button loginBtn, loginRegBtn;
    Intent intent;
    private ProgressBar loginProgress;
    SignInButton signin_google_button;
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        loginEmailText = findViewById(R.id.reg_email);
        loginPassText = findViewById(R.id.reg_confirm_pass);
        loginBtn = findViewById(R.id.login_btn);
        loginRegBtn = findViewById(R.id.login_reg_btn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(this);
        loginRegBtn.setOnClickListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
//                updateUI(user);
            }
        };
        //google ^^^
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMain();

       }

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
//                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent intent;
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Toast.makeText(login.this, "con't signIN", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(login.this, "Sign In succssfully", Toast.LENGTH_LONG).show();
//                    intent =new Intent(login.this,NewUserInfo.class);
//                    startActivity(intent);
                    sendToMain();


                }
                hideProgressDialog();
            }
        });
    }


    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_LONG).show();
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    public void loginWithEmail(){
        String loginEmail = loginEmailText.getText().toString();
        String loginPass = loginPassText.getText().toString();

        if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
            loginProgress.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendToMain();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(login.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                        loginPassText.setText("");
                    }
                    loginProgress.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.login_btn:
                loginWithEmail();
                break;
            case R.id.login_reg_btn:
                    createNewAccount();
                    break;
        }
    }

    private void sendToMain() {
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
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
                            intent =new Intent(login.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            intent =new Intent(login.this,Customer.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                    else
                    {
                        intent =new Intent(login.this,NewUserInfo.class);
                        startActivity(intent);
                        finish();

                    }
                }
                else
                {
                    String error =task.getException().getMessage();
                    Toast.makeText(login.this, "FireStore Get Data Error :"+error, Toast.LENGTH_LONG).show();
                }
            }
        });
//        //fixed problem hear
//        Intent mainIntent = new Intent(login.this,SplashActivity.class);
//        startActivity(mainIntent);
//        finish();

    }
   public void createNewAccount(){
       Intent regIntent = new Intent(login.this, RegisterActivity.class);
       startActivity(regIntent);
    }
}
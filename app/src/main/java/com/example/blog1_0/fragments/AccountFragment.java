package com.example.blog1_0.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blog1_0.MainActivity;
import com.example.blog1_0.NewUserInfo;
import com.example.blog1_0.R;
import com.example.blog1_0.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String user_id;
    private Uri mainImageURI = null;


    ImageView imageView;
    TextView username;
    TextView userEmail;
    TextView userPrivilege;
    Button SignoutBtn,editeProfileBtn;



    public AccountFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_account,container,false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();
        imageView = view.findViewById(R.id.image_profilePic);
        username = view.findViewById(R.id.show_username);
        userEmail=view.findViewById(R.id.show_email);
        userPrivilege =view.findViewById(R.id.show_privelge);
        SignoutBtn=view.findViewById(R.id.signout_button);
        editeProfileBtn=view.findViewById(R.id.edit_button);
        SignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent Intent = new Intent(getActivity(), login.class);
                startActivity(Intent);
                getActivity().finish();
            }
        });

        editeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getActivity(), NewUserInfo.class);
                startActivity(Intent);

            }
        });


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String privilege = task.getResult().getString("privilege");
                        String email=firebaseUser.getEmail();
                        username.setText(name);
                        userEmail.setText(email);
                        userPrivilege.setText(privilege);
                        RequestOptions placeholderRequest =new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_profile_image);
                        mainImageURI = Uri.parse(image);
                        Glide.with(getActivity()).setDefaultRequestOptions(placeholderRequest).load(image).into(imageView);

                    }
                }
            }
        });






        // Inflate the layout for this fragment
        return view;
            }




    }


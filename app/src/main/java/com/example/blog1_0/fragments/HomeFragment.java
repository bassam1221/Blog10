package com.example.blog1_0.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blog1_0.adapters.BlogRecyclerListAdapter;
import com.example.blog1_0.R;
import com.example.blog1_0.login;
import com.example.blog1_0.models.BlogPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView listBlogView;
    private List<BlogPost> blogList;
    private BlogRecyclerListAdapter blogRecyclerListAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_home,container,false);
        blogList = new ArrayList<>();
        listBlogView=view.findViewById(R.id.list_blog_view);
        blogRecyclerListAdapter =new BlogRecyclerListAdapter(blogList);
        swipeRefreshLayout=view.findViewById(R.id.swipeRef);
        listBlogView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listBlogView.setAdapter(blogRecyclerListAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser()!=null) {
            //Here Order .
            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("price", Query.Direction.ASCENDING);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (queryDocumentSnapshots != null) {

                        for (final DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogPostID =doc.getDocument().getId();
                                final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                                String ids = (String) doc.getDocument().get("user_id");
                                firebaseFirestore.collection("Users/"+firebaseAuth.getCurrentUser().getUid()+"/Following").document(ids).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            if(task.getResult().exists()){
                                                blogList.add(blogPost);
                                                blogRecyclerListAdapter.notifyDataSetChanged();

                                            }
                                            else {
                                                Log.d("Follow postt Exception","hahah");
                                            }
                                        }
                                        else {
                                            Log.d("Follow postt Exception","hahaha");                                    }
                                    }
                                });

                            }

                        }
                    }
                }
            });

        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = getActivity().getIntent();
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

}

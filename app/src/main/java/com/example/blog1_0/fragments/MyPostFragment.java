package com.example.blog1_0.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blog1_0.R;
import com.example.blog1_0.adapters.BlogRecyclerListAdapter;
import com.example.blog1_0.models.BlogPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
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
public class MyPostFragment extends Fragment {
    private RecyclerView listBlogView;
    private List<BlogPost> blogList;
    private BlogRecyclerListAdapter blogRecyclerListAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public MyPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_my_post,container,false);
        blogList = new ArrayList<>();
        listBlogView=view.findViewById(R.id.list_blog_view);
        blogRecyclerListAdapter =new BlogRecyclerListAdapter(blogList);
        listBlogView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listBlogView.setAdapter(blogRecyclerListAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser()!=null) {
            //Here Order .
            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.ASCENDING);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (queryDocumentSnapshots != null) {

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostID =doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                                String ids = (String) doc.getDocument().get("user_id");
                                //if stetment to check on post from same user or outher.
                                 if (firebaseAuth.getCurrentUser().getUid().toString().equals(ids))
                                blogList.add(blogPost);
                                blogRecyclerListAdapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });
        }
        return view;
    }

}

package com.example.blog1_0.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blog1_0.R;
import com.example.blog1_0.adapters.FollowRecyclerListAdapter;
import com.example.blog1_0.models.FollowMarket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class FollowFragment extends Fragment {
    private RecyclerView listfollowView;
    private List<FollowMarket> followList;
    private FollowRecyclerListAdapter followRecyclerListAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public FollowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_follow,container,false);
        followList =new ArrayList<>();
        listfollowView= view.findViewById(R.id.list_follow_view);
        followRecyclerListAdapter = new FollowRecyclerListAdapter(followList);
        listfollowView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listfollowView.setAdapter(followRecyclerListAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser()!=null) {
            final Query firstQuery = firebaseFirestore.collection("Users").orderBy("name", Query.Direction.ASCENDING);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots !=null){
                        for(DocumentChange doc :queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType()==DocumentChange.Type.ADDED){

                                FollowMarket follow = doc.getDocument().toObject(FollowMarket.class);
                                String ids = (String) doc.getDocument().get("user_id");
                                Log.d("ids---->",ids);
                                Log.d("user--->",firebaseAuth.getCurrentUser().getUid());
                                if (!ids.equals(firebaseAuth.getCurrentUser().getUid())) {
                                    followList.add(follow);
                                    followRecyclerListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });
        }
        return view;
    }

}

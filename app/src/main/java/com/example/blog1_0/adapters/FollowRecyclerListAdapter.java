package com.example.blog1_0.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blog1_0.R;
import com.example.blog1_0.models.BlogPost;
import com.example.blog1_0.models.FollowMarket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowRecyclerListAdapter extends RecyclerView.Adapter<FollowRecyclerListAdapter.ViewHolder> {

    public List<FollowMarket> follow_list;
    public Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public FollowRecyclerListAdapter(List<FollowMarket> blog_list) {

        this.follow_list = blog_list;

    }

    @NonNull
    @Override
    public FollowRecyclerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_marcket_follow, viewGroup, false);
        context=viewGroup.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth =FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowRecyclerListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        final String currentUser=firebaseAuth.getCurrentUser().getUid();
        String image_url=follow_list.get(i).getImage();
        viewHolder.setBlogImage(image_url);
        final String market_name=follow_list.get(i).getName();
        viewHolder.setName(market_name);
        final String following =follow_list.get(i).getUser_id();

        //follow
        firebaseFirestore.collection("Users/"+currentUser+"/Following").document(following).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null){
                    if(documentSnapshot.exists()){

                        viewHolder.follow_btn.setBackground(context.getDrawable(R.drawable.follow_btn_after));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            viewHolder.follow_btn.setTextColor(context.getColor(R.color.whiteTransparentHalf));
                        }
                    }else {


                      viewHolder.follow_btn.setBackground(context.getDrawable(R.drawable.follow_btn_beffor));

                    }
                }}
        });

        viewHolder.follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itsmy = firebaseFirestore.collection("Users/"+currentUser+"/Following").document(following).getId();
                firebaseFirestore.collection("Users/"+currentUser+"/Following").document(following).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String,Object> followMap=new HashMap<>();
                            followMap.put("timestamp", FieldValue.serverTimestamp());
                            followMap.put("name",market_name);
                            firebaseFirestore.collection("Users/"+currentUser+"/Following").document(following).set(followMap);
                        }else {

                            firebaseFirestore.collection("Users/"+currentUser+"/Following").document(following).delete();

                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return follow_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private  View mView;
        private CircleImageView followImage;
        private TextView marketname;
        private Button follow_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            follow_btn=mView.findViewById(R.id.follow_market_button);
        }

        public void setBlogImage(String downladUri){

            followImage= mView.findViewById(R.id.follow_market_image);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(downladUri).into(followImage); }
        public void setName(String market_name) {
            marketname=mView.findViewById(R.id.follow_market_name);
            marketname.setText(market_name); }
    }
}

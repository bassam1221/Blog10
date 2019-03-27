package com.example.blog1_0.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blog1_0.R;
import com.example.blog1_0.models.BlogPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerListAdapter extends RecyclerView.Adapter<BlogRecyclerListAdapter.ViewHolder> {

    public List<BlogPost>blog_list;
    public Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public BlogRecyclerListAdapter (List<BlogPost> blog_list)
    {

        this.blog_list=blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_liste_item, viewGroup, false);
        context=viewGroup.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth =FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);

        final String blogPostID=blog_list.get(i).BlogPostID;
        final String currentUserID=firebaseAuth.getCurrentUser().getUid();


        String desc_data=blog_list.get(i).getDesc();
        viewHolder.setDescText(desc_data);

        String image_url=blog_list.get(i).getImageUrl();
        viewHolder.setBlogImage(image_url);

        String name_data=blog_list.get(i).getName();
        viewHolder.setNameText(name_data);

        Double items_price =blog_list.get(i).getPrice();
        viewHolder.setBlogPrice(items_price);

        String blog_time =blog_list.get(i).getTimestamp();
        viewHolder.setTime(blog_time);

        String blod_date_to=blog_list.get(i).getDateTo();
        viewHolder.setdateTO(blod_date_to);


        String user_id=blog_list.get(i).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName =  task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    viewHolder.setUserData(userName, userImage);
                }else {

                }
            }
        });
        // Get Like count
        firebaseFirestore.collection("Posts/"+blogPostID+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots!=null){
                if(!queryDocumentSnapshots.isEmpty()){
                    int count =queryDocumentSnapshots.size();
                    viewHolder.updatelikeCount(count);

                }else {
                    viewHolder.updatelikeCount(0);

                }
            }
            }
        });

        // Get Like
        firebaseFirestore.collection("Posts/"+blogPostID+"/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null){
                if(documentSnapshot.exists()){

                    viewHolder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.red_like));
                }else {

                    viewHolder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.post_like_image));

                }
            }}
        });
        // Like Here
        viewHolder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+blogPostID+"/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String,Object>likeMap=new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/"+blogPostID+"/Likes").document(currentUserID).set(likeMap);
                        }else{
                            firebaseFirestore.collection("Posts/"+blogPostID+"/Likes").document(currentUserID).delete();
                        }
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView blogUserImage;
        private  View mView;
        private ImageView blogImageView;
        private TextView blogUserName,nameView,blogDate,blogPrice,blogDateTo,descView;
        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            blogLikeBtn=mView.findViewById(R.id.blog_like_btn);

        }

        public void  setDescText(String descText){
            descView =mView.findViewById(R.id.blog_desc);
            descView.setText("Description:\n"+descText);

        }
        public void setBlogImage(String downladUri){

            blogImageView= mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downladUri).into(blogImageView);

        }
        public  void setNameText(String nameText){
            nameView=mView.findViewById(R.id.blog_item_name);
            nameView.setText("name: "+nameText);

        }
        public void setBlogPrice(Double price){
            blogPrice=mView.findViewById(R.id.blog_price);
            blogPrice.setText("price: "+price.toString());
        }
        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date_from);
            blogDate.setText("from: "+date);

        }

        public void setdateTO (String blog_dateTo) {
            blogDateTo=mView.findViewById(R.id.plog_date_to);
            blogDateTo.setText("to: "+blog_dateTo);
        }

        public void setUserData(String userName, String userImage) {
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(userName);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(blogUserImage);
        }
        public void updatelikeCount(int count ){
            blogLikeCount =mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count +"Likes");

        }
    }
}

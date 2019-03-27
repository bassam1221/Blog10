package com.example.blog1_0;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.blog1_0.fragments.AccountFragment;
import com.example.blog1_0.fragments.HomeFragment;
import com.example.blog1_0.fragments.MyPostFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {



    private final Fragment[] PAGES = new Fragment[] {
            new HomeFragment(),
            new MyPostFragment(),
            new AccountFragment()


    };
    private final String[] PAGE_TITLES = new String[] {
            "Home",
            "My posts",
            "Account"


    };





    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
//    private HomeFragment homeFragment;
//    private AccountFragment accountFragment;
//    private FollowFragment mapFragment;
    FragmentManager fragmentManager;

    private ViewPager mViewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();

if(mAuth.getCurrentUser()!=null) {

    mViewPager = findViewById(R.id.mviewpager);
    fragmentManager = getSupportFragmentManager();
    mViewPager.setAdapter(new MyPagerAdapter(fragmentManager));
    tabLayout = findViewById(R.id.tablayout);
    tabLayout.setupWithViewPager(mViewPager);
    tabLayout.getTabAt(0).setIcon(R.mipmap.home_image);
    tabLayout.getTabAt(2).setIcon(R.mipmap.post_user_image);
    tabLayout.getTabAt(1).setIcon(R.mipmap.fragment_myposts);

}
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser==null){
            sendTOLogin();
        }else {
            user_id =mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if (!task.getResult().exists()){
                            Intent intent=new Intent(MainActivity.this,NewUserInfo.class);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.action_logout_btn:
//                logOut();
//                return true;
//
//            case R.id.action_setting_btn:
//
//                Intent settingsIntent = new Intent(MainActivity.this, NewUserInfo.class);
//                startActivity(settingsIntent);
//                return true;
//
//
//            default:
//                return false;
//
//
//        }
//
//    }

    private void logOut() {
        mAuth.signOut();
        Intent Intent = new Intent(MainActivity.this, login.class);
        startActivity(Intent);
        finish();
    }

    public void addPostClickBtn(View view) {
        Intent intent =new Intent(MainActivity.this,NewPostAct.class);
        startActivity(intent);
    }
    private  void  sendTOLogin(){
        Intent intent =new Intent(MainActivity.this,login.class);
        startActivity(intent);
        finish();
    }





    public class MyPagerAdapter extends FragmentPagerAdapter {



        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return PAGES[position];
        }

        @Override
        public int getCount() {
            return PAGES.length;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }
    }

}

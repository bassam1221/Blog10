package com.example.blog1_0;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.example.blog1_0.adapters.BlogRecyclerListAdapter;
import com.example.blog1_0.fragments.AccountFragment;
import com.example.blog1_0.fragments.HomeFragment;
import com.example.blog1_0.fragments.FollowFragment;
import com.example.blog1_0.models.BlogPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Customer extends AppCompatActivity {


    private final Fragment[] PAGES = new Fragment[] {
            new HomeFragment(),
            new FollowFragment(),
            new AccountFragment()

    };
    private final String[] PAGE_TITLES = new String[] {
            "Home",
            "Customer",
            "Account"


    };

//    private Toolbar customerToolbar;
    private FirebaseAuth mAuth;
//    //
    private RecyclerView listBlogView;
    private List<BlogPost> blogList;
    private FirebaseFirestore firebaseFirestore;
    private BlogRecyclerListAdapter blogRecyclerListAdapter;
//    //
    private ViewPager mViewPager;
    FragmentManager fragmentManager;
    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        mAuth = FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser()!=null) {

            mViewPager = findViewById(R.id.mviewpager);
            fragmentManager = getSupportFragmentManager();
            mViewPager.setAdapter(new MyPagerAdapter(fragmentManager));
            tabLayout = findViewById(R.id.tablayout);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.getTabAt(0).setIcon(R.mipmap.home_image);
            tabLayout.getTabAt(1).setIcon(R.mipmap.fragment_myposts);
            tabLayout.getTabAt(2).setIcon(R.mipmap.post_user_image);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

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

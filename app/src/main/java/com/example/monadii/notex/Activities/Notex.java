package com.example.monadii.notex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Adapters.ViewPager_Adapter;
import com.example.monadii.notex.Fragment.HomeFragment;
import com.example.monadii.notex.Fragment.MessageFragment;
import com.example.monadii.notex.Fragment.SearchFragment;
import com.example.monadii.notex.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Notex extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    NavigationView navigationView;
    private TabLayout tabLayout;
    private FloatingActionButton Add;
    private ViewPager viewPager;
    private ViewPager_Adapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notex);

        //FireBase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Main Layout
        tabLayout =  findViewById(R.id.tabLayout);
        viewPager =  findViewById(R.id.view_pager);
        adapter = new ViewPager_Adapter(getSupportFragmentManager());
        Add = findViewById(R.id.Add_but);

        //Add Fragment
        adapter.ADDFRAGMENT(new HomeFragment());
        adapter.ADDFRAGMENT(new SearchFragment());
        adapter.ADDFRAGMENT(new MessageFragment());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_mail);

        //Navigation View
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


        Add = findViewById(R.id.Add_but);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add();
            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(getApplicationContext(),Profile.class);
                startActivity(profile);
            }
        });

         UpdateNavHeadder();

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void Add(){
        Intent intent = new Intent(this,Add.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile ) {
            Intent prof = new Intent(getApplicationContext(),Profile.class);
            startActivity(prof);
        }else if (id == R.id.nav_setting ){
            Intent setting = new Intent(getApplicationContext(), Settings.class);
            startActivity(setting);
        }else if ( id == R.id.nav_logout ) {
            FirebaseAuth.getInstance().signOut();
            Intent login = new Intent(this, Login.class);
            startActivity(login);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Fire Base & Navigation Header
    private void UpdateNavHeadder (){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView navUserPic = headerView.findViewById(R.id.nav_user_pic);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);

        navUserName.setText(currentUser.getDisplayName());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPic);
    }

    private void checkUserStatus(){
        if( currentUser != null ){

            mUID = currentUser.getUid();
           /* SharedPreferences sp= getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();*/

        }else{
            startActivity(new Intent(this, Login.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        UpdateNavHeadder();
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

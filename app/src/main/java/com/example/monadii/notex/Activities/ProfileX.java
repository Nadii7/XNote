package com.example.monadii.notex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Adapters.Home_Adapter;
import com.example.monadii.notex.Models.Post;
import com.example.monadii.notex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileX extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    RecyclerView postRecyclerView;
    List<Post> postList;
    String uid;
    private ImageButton Exit , sendMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_x);

        firebaseAuth = FirebaseAuth.getInstance();

        postList =new ArrayList<>();
        postRecyclerView = findViewById(R.id.ProfileX_rec);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        sendMessage = findViewById(R.id.send_Xmessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Message.class);
                intent.putExtra("hisUid",uid);
                startActivity(intent);
            }
        });

        Exit = findViewById(R.id.exit_profileX);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkUserStatus();
        Update();
        loadPost();
    }

    private void Update (){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseUserReference = firebaseDatabase.getReference("Users");

        final TextView userName = findViewById(R.id.profileX_user_name);
        final TextView userBio = findViewById(R.id.profileX_user_bio);
        final ImageView userPic = findViewById(R.id.profileX_user_pic);
        final ImageView coverPic = findViewById(R.id.BackGroundX);

        Query query = databaseUserReference.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String bio = "" + ds.child("bio").getValue();
                    String avatar = "" + ds.child("avatar").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    userName.setText(name);
                    userBio.setText(bio);
                    try {
                        Glide.with(getApplicationContext()).load(avatar).into(userPic);
                    } catch (Exception e) {
                        Toast.makeText(ProfileX.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    try {
                        Glide.with(getApplicationContext()).load(cover).into(coverPic);
                    } catch (Exception e) {
                        Toast.makeText(ProfileX.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPost(){
        LinearLayoutManager layoutManager = (new LinearLayoutManager(this));
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    postList.add(post);
                    Home_Adapter postAdapter = new Home_Adapter(ProfileX.this, postList);
                    postRecyclerView.setAdapter(postAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if( user != null ){

        }else{
            startActivity(new Intent(this, Login.class));
            finish();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

package com.example.monadii.notex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Profile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<Post> noteList;
    private RecyclerView recyclerView;
    private Button Edit_Profile ;
    private ImageButton Exit, sendMessage;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        noteList =new ArrayList<>();
        recyclerView = findViewById(R.id.Profile_rec);
        LinearLayoutManager layoutManager = (new LinearLayoutManager(this));
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        final TextView userName = findViewById(R.id.profile_user_name);
        final TextView userBio = findViewById(R.id.profile_user_bio);

        Edit_Profile = findViewById(R.id.Edit_but);
        Edit_Profile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           Intent intent = new Intent(Profile.this,Edit.class);
           intent.putExtra("name",userName.getText());
           intent.putExtra("bio",userBio.getText());
           startActivity(intent);
           finish();
           }
       });

        Exit = findViewById(R.id.exit_profile);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sendMessage = findViewById(R.id.sendMessage_but);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Message.class);
                intent.putExtra("hisUid",uid);
                startActivity(intent);
            }
        });

        LIST_POST();

    }

    //Header
    private void Update (){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseUserReference = firebaseDatabase.getReference("Users");

        final TextView userName = findViewById(R.id.profile_user_name);
        final TextView userBio = findViewById(R.id.profile_user_bio);
        final ImageView userPic = findViewById(R.id.profile_user_pic);
        final ImageView coverPic = findViewById(R.id.BackGround);

        Query query = databaseUserReference.orderByChild("email").equalTo(currentUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                String name = ""+ ds.child("name").getValue();
                String bio = ""+ ds.child("bio").getValue();
                String avatar = ""+ ds.child("avatar").getValue();
                String cover = ""+ ds.child("cover").getValue();

                userName.setText(name);
                userBio.setText(bio);
                try {
                    Glide.with(getApplicationContext()).load(avatar).into(userPic);
                }catch (Exception e){
                    Toast.makeText(Profile.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

                    try {
                        Glide.with(getApplicationContext()).load(cover).into(coverPic);
                    }catch (Exception e){
                        Toast.makeText(Profile.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void LIST_POST(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Posts");

        Query query = databaseReference.orderByChild("uId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot postsnap : dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    noteList.add(post);
                    Home_Adapter postAdapter = new Home_Adapter(Profile.this, noteList);
                    recyclerView.setAdapter(postAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        Update();
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

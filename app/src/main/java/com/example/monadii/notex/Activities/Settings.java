package com.example.monadii.notex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ConstraintLayout signout , profile_Con, nightmood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Button profile = findViewById(R.id.setting_Profile);
        profile_Con = findViewById(R.id.profilecon);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoProfile();
            }
        });
        profile_Con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoProfile();
            }
        });

        signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent login = new Intent(getApplicationContext(), Login.class);
                startActivity(login);
                finish();
            }
        });
        nightmood = findViewById(R.id.night_mood);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Soon",Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton back = findViewById(R.id.back_but);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void GoProfile() {
        Intent intent = new Intent(this,Profile.class);
        startActivity(intent);
    }

    //Fire Base & Navigation Header
    private void Update (){
        TextView UserName = findViewById(R.id.setting_user_name);
        TextView UserMail = findViewById(R.id.setting_user_mail);
        ImageView UserPic = findViewById(R.id.setting_user_pic);

        UserName.setText(currentUser.getDisplayName());
        UserMail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPic);
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

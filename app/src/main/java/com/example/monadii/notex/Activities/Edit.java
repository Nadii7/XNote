package com.example.monadii.notex.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Edit extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageButton close ;
    private EditText edit_name , edit_bio ;
    private ImageView edit_cover , edit_avatar ;
    private Button  save  ;
    Uri PickedImgUri_Avatar;
    Uri PickedImgUri_Cover;
    static int PReqCode = 5 ;
    static int REQUESCODE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        edit_name = findViewById(R.id.update_name);
        edit_bio = findViewById(R.id.update_bio);
        edit_cover = findViewById(R.id.cover_pic);
        edit_avatar = findViewById(R.id.avatar_pic);

        edit_name.setText( getIntent().getStringExtra("name"));
        edit_bio.setText( getIntent().getStringExtra("bio"));
        Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(edit_avatar);
        Glide.with(getApplicationContext()).load(databaseReference.getDatabase().getReference("cover")).into(edit_cover);

        save = findViewById(R.id.Save_Edit);
        close =findViewById(R.id.Exit_Edit);

        edit_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUESCODE = 15 ;
                checkAndRequestForPermission();
            }
        });

        edit_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUESCODE = 5 ;
                checkAndRequestForPermission();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Close();
            }
        });

    }

    private void UpdateProfile() {
        final String name = edit_name.getText().toString().trim();
        final String bio = edit_bio.getText().toString().trim();

        HashMap <String , Object> result  = new HashMap<>();

        result.put( "name" , name );
        result.put( "bio" , bio );
        if (PickedImgUri_Cover !=null){
        result.put( "cover" , ""+PickedImgUri_Cover );
        }

        if (PickedImgUri_Avatar !=null){
        result.put( "avatar" , ""+PickedImgUri_Avatar );
        }

        databaseReference.child(currentUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final FirebaseUser  user =FirebaseAuth.getInstance().getCurrentUser();
                if ( PickedImgUri_Avatar != null ) {
                    UserProfileChangeRequest changeProfilePic = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(PickedImgUri_Avatar).build();

                    user.updateProfile(changeProfilePic).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Profile Pic Updated Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    });

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    Query query = ref.orderByChild("uId").equalTo(user.getUid());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String child = ds.getKey();
                                dataSnapshot.getRef().child(child).child("uAvatar").setValue(""+PickedImgUri_Avatar);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                String child = ds.getKey();
                                if (dataSnapshot.child(child).hasChild("Comments")){
                                    String child1 = ""+dataSnapshot.child(child).getKey();
                                    Query query1 =FirebaseDatabase.getInstance().getReference("Posta").child(child1).child("Comments").orderByChild("uId").equalTo(user.getUid());
                                    query1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       for (DataSnapshot ds: dataSnapshot.getChildren()){
                                           String child = ds.getKey();
                                           dataSnapshot.getRef().child(child).child("uAvatar").setValue(""+PickedImgUri_Avatar);
                                       }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                if (!name.equals("")){
                    UserProfileChangeRequest changeProfileName = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).setPhotoUri(PickedImgUri_Avatar).build();

                    user.updateProfile(changeProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Profile Name Updated Successfully...",Toast.LENGTH_SHORT).show();
                        }
                    });
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    Query query = ref.orderByChild("uId").equalTo(user.getUid());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String child = ds.getKey();
                                dataSnapshot.getRef().child(child).child("uName").setValue(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                String child = ds.getKey();
                                if (dataSnapshot.child(child).hasChild("Comments")){
                                    String child1 = ""+dataSnapshot.child(child).getKey();
                                    Query query1 =FirebaseDatabase.getInstance().getReference("Posta").child(child1).child("Comments").orderByChild("uId").equalTo(user.getUid());
                                    query1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String child = ds.getKey();
                                                dataSnapshot.getRef().child(child).child("uName").setValue(name);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                Close();
            }
        });



    }

    private void Close(){
        Intent intent = new Intent(Edit.this,Profile.class);
        startActivity(intent);
        finish();
    }

    private void OpenGallery() {
        // Todo : Open Gallery And Wait User To Pick a pic
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, REQUESCODE );
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Edit.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Edit.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Edit.this, "PLEASE ACCEPT THE REQUIRED PERMISION", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Edit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            OpenGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            //The User Successfully Picked an image and we need to save its Preference to URI Variable
           if (REQUESCODE == 5){
               PickedImgUri_Avatar = data.getData();
               edit_avatar.setImageURI(PickedImgUri_Avatar);
           }else if (REQUESCODE == 15 ) {
               PickedImgUri_Cover = data.getData();
               edit_cover.setImageURI(PickedImgUri_Cover);
           }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Profile.class);
        startActivity(intent);
    }
}

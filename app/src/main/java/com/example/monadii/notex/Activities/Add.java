package com.example.monadii.notex.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class Add extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userDbRef;
    private Button add, close;
    ImageButton load_img;
    private TextView note;
    private ImageView note_pic;
    private ProgressBar progressBar;
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    private Uri PickedImgUri;
    String name, mail, uid, avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        note_pic = findViewById(R.id.Add_note_pic);
        note = findViewById(R.id.Add_note);
        progressBar = findViewById(R.id.Add_par);
        progressBar.setVisibility(View.INVISIBLE);
        close = findViewById(R.id.Add_close);
        load_img = findViewById(R.id.load_img);
        add = findViewById(R.id.Add_but);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(mail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    mail = "" + ds.child("email").getValue();
                    avatar = "" + ds.child("avatar").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                final String noteTxt = note.getText().toString();

                if ( noteTxt.isEmpty()  ) {
                    ShowMessage("Can't Post Empty Note...");
                    progressBar.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.VISIBLE);
                    return;
                }if (PickedImgUri == null){
                    uploadData(noteTxt, "No Image");
                }else{
                    uploadData(noteTxt, PickedImgUri.toString() );
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exit();
            }
        });

        load_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();
            }
        });


    }


    private void uploadData(final String note , final String uri){
        final String timsStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName= "Posts/" +"post_" +timsStamp;
        final String like = "0";
        if (!uri.equals("No Image")){
            StorageReference ref = FirebaseStorage.getInstance().getReference("Posts").child(filePathAndName);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    String downloadUri =uriTask.getResult().toString();

                    if (uriTask.isSuccessful()){
                        HashMap<Object,String> hashMap = new HashMap<>();
                        hashMap.put("uId",uid);
                        hashMap.put("uName",name);
                        hashMap.put("uMail",mail);
                        hashMap.put("uAvatar",avatar);
                        hashMap.put("pId",timsStamp);
                        hashMap.put("pNote",note);
                        hashMap.put("pImage",downloadUri);
                        hashMap.put("pTime",timsStamp);
                        hashMap.put("pLikes",like);
                        hashMap.put("pComments",like);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        ref.child(timsStamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                      ShowMessage("Note Posted Successfully");
                                        Exit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowMessage(e.getMessage());
                                add.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ShowMessage(e.getMessage());
                    add.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            HashMap<Object,String> hashMap = new HashMap<>();
            hashMap.put("uId",uid);
            hashMap.put("uName",name);
            hashMap.put("uMail",mail);
            hashMap.put("uAvatar",avatar);
            hashMap.put("pId",timsStamp);
            hashMap.put("pNote",note);
            hashMap.put("pImage","No Image");
            hashMap.put("pTime",timsStamp);
            hashMap.put("pLikes",like);
            hashMap.put("pComments",like);


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timsStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ShowMessage("Note Posted Successfully");
                            Exit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ShowMessage(e.getMessage());
                            add.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

        }
    }

    //Fire Base & Header
    private void Update() {
        ImageView UserPic = findViewById(R.id.A_user_pic);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPic);
    }

    private void Exit() {
        Intent intent = new Intent(getApplicationContext(), Notex.class);
        startActivity(intent);
        finish();
    }

    private void ShowMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void OpenGallery() {
        // Todo : Open Gallery And Wait User To Pick a pic
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Add.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Add.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Add.this, "PLEASE ACCEPT THE REQUIRED PERMISION", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Add.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            OpenGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            //The User Successfully Picked an image and we need to save its Preference to URI Variable
            PickedImgUri = data.getData();
            note_pic.setImageURI(PickedImgUri);
        }
    }


    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user != null ){
            mail = user.getEmail();
            uid = user.getUid();
            name = user.getDisplayName();
            avatar =user.getPhotoUrl().toString();

        }else{
            startActivity(new Intent(this, Login.class));
            finish();
        }

    }


    @Override
    protected void onStart() {
        checkUserStatus();
        Update();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

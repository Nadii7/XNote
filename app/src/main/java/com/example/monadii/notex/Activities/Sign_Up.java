package com.example.monadii.notex.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.monadii.notex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Sign_Up extends AppCompatActivity {
    ImageView user_pic;
    DatabaseReference mDatabase;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri PickedImgUri;
    private EditText user_name, user_mail, user_pass;
    private ProgressBar progressBar;
    private Button register ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        user_name = findViewById(R.id.sign_name);
        user_mail = findViewById(R.id.sign_mail);
        user_pass = findViewById(R.id.sign_password);
        progressBar = findViewById(R.id.sign_bar);
        register = findViewById(R.id.signup);
        progressBar.setVisibility(View.INVISIBLE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_Up();
            }
        });

        user_pic = findViewById(R.id.sign_pic);
        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();

                } else {
                    OpenGallery();
                }
            }
        });


    }


    public void sign_Up() {
        register.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        final String name = user_name.getText().toString();
        final String mail = user_mail.getText().toString();
        final String password = user_pass.getText().toString();


        if (name.isEmpty() || mail.isEmpty() || password.isEmpty() ) {
            ShowMessage("Please Verify all fields");
            register.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            mAuth.createUserWithEmailAndPassword(mail , password )
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's informationUpdateUserInfo( name , PickedImgUri , mAuth.getCurrentUser());

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                UpdateUserInfo(name,PickedImgUri,mAuth.getCurrentUser());
                                String uid=mAuth.getCurrentUser().getUid();
                                HashMap<Object,String> hashMap  = new HashMap<>();
                                hashMap.put("uid",uid);
                                hashMap.put("email",mail);
                                hashMap.put("name",name);
                                hashMap.put("avatar",""+PickedImgUri);
                                hashMap.put("bio","");
                                hashMap.put("cover","");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference=database.getReference("Users");
                                reference.child(uid).setValue(hashMap);
                            }

                                UpdateUI();

                            } else {
                                // If sign in fails, display a message to the user.
                                ShowMessage("Account creation failed"+task.getException().getMessage());
                                register.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });

        }
    }

    private void UpdateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference Storage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = Storage.child(PickedImgUri.getLastPathSegment());
        imageFilePath.putFile(PickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
           imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {
                   UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                           .setDisplayName(name)
                           .setPhotoUri(uri)
                           .build();
                   currentUser.updateProfile(profileUpdate)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                  ShowMessage("Register Complete");
                                  UpdateUI();
                              }
                               }
                           });
               }
           });
            }
        });
    }

    private void UpdateUI() {
        Intent intent = new Intent(this, Notex.class);
        startActivity(intent);
        finish();
    }

    private void OpenGallery() {
        // Todo : Open Gallery And Wait User To Pick a pic
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Sign_Up.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Sign_Up.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Sign_Up.this, "PLEASE ACCEPT THE REQUIRED PERMISION", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Sign_Up.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
            user_pic.setImageURI(PickedImgUri);
        }

    }


    // Message
    private void ShowMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

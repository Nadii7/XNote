package com.example.monadii.notex.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Adapters.Chat_Adapter;
import com.example.monadii.notex.Models.Chat;
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
import java.util.HashMap;
import java.util.List;

public class Message extends AppCompatActivity {
    private ImageButton back , info , add_pic , send ;
    private ImageView receiver_avatar , message_Pic;
    private TextView receiver_name ;
    private EditText message_txt;
    private RecyclerView message_rec;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    String mUid , hUid ;
    String hisImage;
    ValueEventListener seenListener;
    DatabaseReference useRefForSeen;
    List<Chat> chatList;
    Chat_Adapter chat_adapter;

    private static final int PReqCode = 22;
    private static final int REQUESCODE = 22;
    private Uri PickedImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        back = findViewById(R.id.back);
        info =findViewById(R.id.info);
        add_pic = findViewById(R.id.add_pic);
        send = findViewById(R.id.send_message);
        receiver_avatar =findViewById(R.id.User_R_pic);
        receiver_name = findViewById(R.id.User_R_name);
        message_txt = findViewById(R.id.message_txt);
        message_Pic = findViewById(R.id.message_pic);
        message_rec = findViewById(R.id.Message_View_rec);
        message_rec.setLayoutManager(new LinearLayoutManager(this));


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        hUid =  intent.getStringExtra("hisUid");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mUid = currentUser.getUid();

        Query userQuery = mReference.orderByChild("uid").equalTo(hUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String name ="" + snapshot.child("name").getValue();
                    hisImage ="" + snapshot.child("avatar").getValue();
                    receiver_name .setText(name);
                    Glide.with(Message.this).load(hisImage).into(receiver_avatar);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String message = message_txt.getText().toString().trim();
               if (TextUtils.isEmpty(message) && PickedImgUri == null ){
                   Toast.makeText(Message.this,"Can't send Empty Message...",Toast.LENGTH_SHORT).show();
               }else{
                   sendMessage(message);
               }
               //empty message_txt
               message_txt.setText("");
               message_Pic.setImageURI(null);
               PickedImgUri = null;
               message_Pic.getLayoutParams().height = 0 ;
               message_Pic.requestLayout();

           }
       });
       add_pic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               checkAndRequestForPermission();
           }
       });

       info.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), ProfileX.class);
               intent.putExtra("uid",hUid);
               startActivity(intent);
           }
       });

       readMessage();
       checkMessageStatus();
    }

    private void checkMessageStatus() {
        useRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = useRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           for (DataSnapshot snapshot : dataSnapshot.getChildren()){
               Chat chat = snapshot.getValue(Chat.class);
               if (chat.getReceiver().equals(mUid) && chat.getSender().equals(hUid)){
                   HashMap < String , Object > hasSeenHashMap = new HashMap<>();
                   hasSeenHashMap.put("status",true);
                   snapshot.getRef().updateChildren(hasSeenHashMap);
               }
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if ( chat.getReceiver().equals(mUid) && chat.getSender().equals(hUid)  ||
                         chat.getReceiver().equals(hUid) && chat.getSender().equals(mUid)  ){
                        chatList.add(chat);
                    }
                    chat_adapter = new Chat_Adapter(Message.this,chatList, hisImage);
                    chat_adapter.notifyDataSetChanged();
                    message_rec.setAdapter(chat_adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String message ) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap <String , Object > hashMap = new HashMap<>();
        hashMap.put("sender", mUid);
        hashMap.put("receiver",hUid);
        if (!message.isEmpty() ){
            hashMap.put("message", message);
        }else{
            hashMap.put("message", "No Text");
        }
        hashMap.put("timestamp",timestamp);
        hashMap.put("status",false);
        if(PickedImgUri != null){ hashMap.put("messagePic", PickedImgUri.toString() ); }
        else {hashMap.put("messagePic", "No Image");}
        databaseReference.child("Chats").push().setValue(hashMap);

       /* String msg = message;
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("User").child(mUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify){
                    sendNotification(hUid,user.getName(),message);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkUserStatus(){
        FirebaseUser muser = mAuth.getCurrentUser();
        if ( muser != null ){

        }else{
            startActivity(new Intent(this, Login.class));
            finish();
        }

    }

    private void OpenGallery() {
        // Todo : Open Gallery And Wait User To Pick a pic
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, REQUESCODE );
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Message.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Message.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Message.this, "PLEASE ACCEPT THE REQUIRED PERMISION", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Message.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
            message_Pic.setImageURI(PickedImgUri);

            message_Pic.getLayoutParams().height = 900 ;
            message_Pic.requestLayout();


        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        useRefForSeen.removeEventListener(seenListener);
    }
}

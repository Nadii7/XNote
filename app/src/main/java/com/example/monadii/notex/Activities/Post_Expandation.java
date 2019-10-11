package com.example.monadii.notex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Adapters.Comment_Adapter;
import com.example.monadii.notex.Models.Comment;
import com.example.monadii.notex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Post_Expandation extends AppCompatActivity {
    String hisUid, myUid, myAvatar, myName, postId, pLikes, pComment, hisAvatar, hisName , pImage;

    private ImageView post_user_pic , post_pic , current_user_pic ;
    private TextView user_name , user_mail , post_txt , post_date , likes_count , reply_count ;
    private EditText reply_txt ;
    private Button reply , close ;
    private ImageButton plike_but , Pmore_but;
    boolean mProcessComment= false;
    boolean mProcessLike= false;
    private FirebaseUser currentUser;

    RecyclerView recyclerView;
    Comment_Adapter commentAdapter;
    List<Comment> listComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postId= getIntent().getStringExtra("postId");

        post_user_pic = findViewById(R.id.Post_user_pic);
        post_pic = findViewById(R.id.P_img);
        current_user_pic = findViewById(R.id.U_pic);
        user_name = findViewById(R.id.Post_user_name);
        user_mail = findViewById(R.id.Post_user_mail);
        post_txt = findViewById(R.id.P_txt);
        post_date = findViewById(R.id.P_date);
        likes_count =findViewById(R.id.plike_num);
        reply_count = findViewById(R.id.pcomment_num);
        plike_but = findViewById(R.id.plike_but);
        Pmore_but = findViewById(R.id.pmore_but);
        reply_txt = findViewById(R.id.reply_txt);
        reply = findViewById(R.id.reply_but);
        close = findViewById(R.id.close);
        recyclerView = findViewById(R.id.comment_rec);
        listComment=new ArrayList<>();

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              postComment();

            }
        });


        post_user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserInfo();
            }
        });

        plike_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              likePost();
            }
        });

        Pmore_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOption();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exit();
            }
        });

        loadPostDetails();
        Reply();
        setLikes();

    }

    private void shareNote() {
        Intent intent = new Intent(Intent.ACTION_SEND);


    }

    private void showMoreOption() {
        PopupMenu popupMenu = new PopupMenu(this, Pmore_but, Gravity.END);
        if (hisUid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");

        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0){
                    deletePost();
                }
                return false;
            }
        });
    }

    private void deletePost() {
        if(pImage .equals("No Image")){
            deleteWithoutImage(postId);
        }else{
            deleteWithImage(postId, pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                                ShowMessage("Deleted Successfully..");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteWithoutImage(String pId) {
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                ShowMessage("Deleted Successfully..");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setLikes() {
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postId).hasChild(myUid)) {
                        plike_but.setImageResource(R.drawable.ic_liked);
                    }
                else {
                        plike_but.setImageResource(R.drawable.ic_like);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        mProcessLike = true ;
        // get Id of Post Clicked
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
         likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessLike) {
                    if (dataSnapshot.child(postId).hasChild(myUid)) {
                        //Already Liked So we will remove like
                        postRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes )- 1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;
                    } else {
                        postRef.child(postId).child("pLikes").setValue("" +  (Integer.parseInt(pLikes ) + 1));
                        likesRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        String comment = reply_txt.getText().toString().trim();
        if (TextUtils.isEmpty(comment)){
            ShowMessage("Can't post Empty Comment...");
        }else{
            String timeStamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("cId",timeStamp);
            hashMap.put("comment",comment);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("uId",myUid);
            hashMap.put("uAvatar",myAvatar);
            hashMap.put("uName",myName);
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ShowMessage("Comment Added Successfully..");
                            updateCommentCount();
                            reply_txt.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ShowMessage(""+e.getMessage());
                }
            });
        }
    }

    private void updateCommentCount() {
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if (mProcessComment){
              String comments = ""+dataSnapshot.child("pComments").getValue();
              int newCommentBal = Integer.parseInt(comments)+1;
              ref.child("pComments").setValue(""+newCommentBal);
              mProcessComment = false;
          }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {
        Intent intent = new Intent(this,ProfileX.class);
        intent.putExtra("uid",myUid);
        startActivity(intent);
    }

    private void loadPostDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    // Get Data
                    hisUid = ""+ds.child("uId").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    String umail = ""+ds.child("uMail").getValue();
                    hisAvatar = ""+ds.child("uAvatar").getValue();
                    String pid = ""+ds.child("PId").getValue();
                    String pnote = ""+ds.child("pNote").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    String ptimeStamp = ""+ds.child("pTime").getValue();
                    String pTime = timestampToString(ptimeStamp);
                    pLikes = ""+ds.child("pLikes").getValue();
                    pComment = ""+ds.child("pComments").getValue();


                    // Set Data
                    user_name.setText(hisName);
                    user_mail.setText(umail);
                    try { Glide.with(getApplicationContext()).load(hisAvatar).into(post_user_pic);}catch (Exception e){}
                    post_txt.setText(pnote);
                    post_date.setText(pTime);
                    likes_count.setText(pLikes+" Likes");
                    reply_count.setText(pComment+" Comments");

                    if (pImage .equals("No Image")) {
                        post_pic.setVisibility(View.GONE);
                    } else{
                        try {
                            Glide.with(getApplicationContext()).load(pImage).into(post_pic);}catch (Exception e){} }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Reply() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            listComment.clear();
            for (DataSnapshot ds :dataSnapshot.getChildren()) {
            Comment comment = ds.getValue(Comment.class);
            listComment.add(comment);

            commentAdapter = new Comment_Adapter(getApplicationContext(),listComment ,myUid , postId );
            recyclerView.setAdapter(commentAdapter);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void ShowMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void Update() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        try{ Glide.with(this).load(currentUser.getPhotoUrl()).into(current_user_pic);}catch (Exception e){}
    }

    private void Exit() {
        Intent intent = new Intent(getApplicationContext(), Notex.class);
        startActivity(intent);
        finish();
    }

    private String timestampToString (String time){
         Calendar calendar = Calendar.getInstance(Locale.getDefault());
         calendar.setTimeInMillis(Long.parseLong(time));
         String date = DateFormat.format("dd-MM-yyyy, hh:mm:aa",calendar).toString();
         return date;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkUserStatus(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if( currentUser != null ){
            myUid = currentUser.getUid();
            myName = currentUser.getDisplayName();
            myAvatar =currentUser.getPhotoUrl().toString();
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


}

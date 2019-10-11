package com.example.monadii.notex.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Activities.Post_Expandation;
import com.example.monadii.notex.Activities.ProfileX;
import com.example.monadii.notex.Models.Post;
import com.example.monadii.notex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.HomeViewHolder> {
    private Context H_context;
    private List<Post> H_post;

    private String myUid;
    private DatabaseReference likesRef;
    private DatabaseReference postRef;
    private boolean mProcessLike = false;
    public Home_Adapter(Context h_context, List<Post> posts) {
        H_context = h_context;
        this.H_post = posts;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }
    // < ON Create >
    @NonNull
    @Override
    public Home_Adapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater h_inflater = LayoutInflater.from(H_context);
        view = h_inflater.inflate(R.layout.note, parent, false);
        return new HomeViewHolder(view);
    }
    // < Size >
    @Override
    public int getItemCount() {
        return H_post.size();
    }
    // Set & Get Values
    @Override
    public void onBindViewHolder(@NonNull final Home_Adapter.HomeViewHolder holder, final int position) {
       // Get Data
        final String uId = H_post.get(position).getuId();
        final String uName = H_post.get(position).getuName();
        String uMail = H_post.get(position).getuMail();
        String uAvatar = H_post.get(position).getuAvatar();
        final String pId = H_post.get(position).getpId();
        String pNote = H_post.get(position).getpNote();
        final String pImage = H_post.get(position).getpImage();
        String pTime = timestampToString(H_post.get(position).getpTime());
        String pLikes = H_post.get(position).getpLikes();
        String pComments = H_post.get(position).getpComments();
        // Set Data
        holder.user_name.setText(uName);
        holder.note.setText(pNote);
        holder.date.setText(pTime);
        holder.likesCount.setText(pLikes+" likes");
        holder.commentCount.setText(pComments+" Comments");

        // Set Likes For Each Post
        setLikes(holder, pId);

        // User Avatar
        try { Glide.with(H_context).load(uAvatar).into(holder.user_pic); }catch (Exception e){}
        // Post Image
        if (pImage.equals("No Image")) {
            holder.note_pic.setVisibility(View.GONE);
        } else{
            try { Glide.with(H_context).load(pImage).into(holder.note_pic);}catch (Exception e){}
            holder.note_pic.setVisibility(View.VISIBLE);
        }

        // MoreButton Action
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOption(holder.more, uId, myUid, pId, pImage );
            }
        });

        // LikeButton Action
        holder.like_but.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View view) {
                final int pLikes = Integer.parseInt(H_post.get(position).getpLikes());
                mProcessLike = true ;

                // get Id of Post Clicked
              final String postId =H_post.get(position).getpId();
              likesRef.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (mProcessLike) {
                         if (dataSnapshot.child(postId).hasChild(myUid)) {
                             //Already Liked So we will remove like
                             postRef.child(postId).child("pLikes").setValue("" + (pLikes - 1));
                             likesRef.child(postId).child(myUid).removeValue();
                             mProcessLike = false;
                         } else {
                             postRef.child(postId).child("pLikes").setValue("" + (pLikes + 1));
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
        });

        // CommentButton Action
        holder.comment_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(H_context,Post_Expandation.class);
                intent.putExtra("postId",pId);
                H_context.startActivity(intent);
            }
        });


        holder.user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(H_context, ProfileX.class);
               intent.putExtra("uid",uId);
               H_context.startActivity(intent);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(H_context,Post_Expandation.class);
                intent.putExtra("postId",pId);
                H_context.startActivity(intent);
            }
        });

    }

    private void setLikes(final HomeViewHolder holder, final String postKey) {

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot.child(postKey).hasChild(myUid)){
               holder.like_but.setImageResource(R.drawable.ic_liked);
           }else{
               holder.like_but.setImageResource(R.drawable.ic_like);
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOption(ImageButton more, String uId, String myUid, final String pId, final String pImage) {
        PopupMenu popupMenu = new PopupMenu(H_context, more, Gravity.END);
        if (uId.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0){
                    deletePost(pId, pImage);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deletePost(String pId, String pImage) {
        if(pImage.equals("No Image")){
            deleteWithoutImage(pId);
        }else{
            deleteWithImage(pId, pImage);
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
                                Toast.makeText(H_context,"Deleted Successfully..",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(H_context,"Deleted Successfully..",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // < View Holder >
    public class HomeViewHolder extends RecyclerView.ViewHolder {
      private ImageView user_pic, note_pic;
      private TextView user_name, date, note, location, likesCount, commentCount;
      private ImageButton like_but, comment_but, share, more;

      private ConstraintLayout note_cons;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            user_pic = itemView.findViewById(R.id.N_user_pic);
            note_pic = itemView.findViewById(R.id.N_pic);
            like_but = itemView.findViewById(R.id.like_but);
            comment_but = itemView.findViewById(R.id.comment_but);
            more = itemView.findViewById(R.id.more_but);
            user_name =itemView.findViewById(R.id.N_user_name);
            date = itemView.findViewById(R.id.N_date);
            note = itemView.findViewById(R.id.N_txt);
            likesCount = itemView.findViewById(R.id.like_num);
            commentCount = itemView.findViewById(R.id.comment_num );
        }
    }

    private String timestampToString (String time){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String date = DateFormat.format("dd-MM-yyyy, hh:mm:aa",calendar).toString();
        return date;
    }


}

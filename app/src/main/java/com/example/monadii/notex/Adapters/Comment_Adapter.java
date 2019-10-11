package com.example.monadii.notex.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Models.Comment;
import com.example.monadii.notex.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.CommentViewHolder> {
    private Context C_context;
    private List<Comment> C_comment;
    String myUid, postId ;

    public Comment_Adapter(Context c_context, List<Comment> c_comment, String myUid, String postId) {
        C_context = c_context;
        C_comment = c_comment;
        this.myUid = myUid;
        this.postId =postId;
    }

    // < ON Create >
    @NonNull
    @Override
    public Comment_Adapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater C_inflater = LayoutInflater.from(C_context);
        view = C_inflater.inflate(R.layout.comment,parent,false);
        return new CommentViewHolder(view);
    }
    // < Size >
    @Override
    public int getItemCount() {
        return C_comment.size();
    }
    // Set & Get Values
    @Override
    public void onBindViewHolder(@NonNull Comment_Adapter.CommentViewHolder holder, int position) {
        final String uid = C_comment.get(position).getuId();
        final String name = C_comment.get(position).getuName();
        String mail = C_comment.get(position).getuMail();
        String avatar = C_comment.get(position).getuAvatar();
        final String cid = C_comment.get(position).getcId();
        String comment = C_comment.get(position).getComment();
        String cTime = timestampToString(C_comment.get(position).getTimestamp());

        holder.comment.setText(comment);
        holder.user_name.setText(name);
        if (cTime!= null){
        holder.date.setText(cTime);}
        try { Glide.with(C_context).load(avatar).into(holder.user_pic); }catch (Exception e){}

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (myUid.equals(uid)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure you wanna delete this?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteComment(cid);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }else {
                    Toast.makeText(C_context,"Can't remove others comments",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void deleteComment(String cid) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.child("Comments").child(cid).removeValue();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comments = ""+dataSnapshot.child("pComments").getValue();
                int newCommentBal = Integer.parseInt(comments)-1;
                ref.child("pComments").setValue(""+newCommentBal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // < View Holder >
    public class CommentViewHolder extends RecyclerView.ViewHolder {
      private ImageView user_pic ;
      private TextView user_name,comment , date;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            user_pic = itemView.findViewById(R.id.C_user_pic);
            user_name =itemView.findViewById(R.id.C_user_name);
            comment = itemView.findViewById(R.id.C_txt);
            date = itemView.findViewById(R.id.C_date);
         }
    }


    private String timestampToString (String time){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String date = DateFormat.format("dd-MM-yyyy, hh:mm:aa",calendar).toString();
        return date;
    }



}

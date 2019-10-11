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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.ChatViewHolder> {
    private static final int Msg_Type_Right = 0 ;
    private static final int Msg_Type_Left = 1 ;
    private Context C_context;
    private List<Chat> m_chat;
    private String imageUrl;
    private FirebaseUser fUser;

    public Chat_Adapter(Context c_context, List<Chat> m_chat, String imageUrl) {
        C_context = c_context;
        this.m_chat = m_chat;
        this.imageUrl = imageUrl;
    }

    // < ON Create >
    @NonNull
    @Override
    public Chat_Adapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Msg_Type_Right){
        LayoutInflater M_inflater = LayoutInflater.from(C_context);
        view = M_inflater.inflate(R.layout.sender,parent,false);
        return new ChatViewHolder(view);
        }else{
            LayoutInflater M_inflater = LayoutInflater.from(C_context);
            view = M_inflater.inflate(R.layout.receiver,parent,false);
            return new ChatViewHolder(view);
        }
    }
    // < Size >
    @Override
    public int getItemCount() {
        return m_chat.size();
    }
    // Set & Get Values
    @Override
    public void onBindViewHolder(@NonNull Chat_Adapter.ChatViewHolder holder, final int position) {
        String message = m_chat.get(position).getMessage();
        String pic = m_chat.get(position).getMessagePic();

        holder.messageTv.setText(message);
        holder.timeTv.setText(timestampToString(m_chat.get(position).getTimestamp()));
        if (!pic.isEmpty()) { try { Glide.with(C_context).load(pic).into(holder.messageIv); } catch (Exception e){} }

        try { Glide.with(C_context).load(imageUrl).into(holder.avatarIv); } catch (Exception e){}

        if (position == m_chat.size()-1) {
            if (m_chat.get(position).isStatus()) {
                holder.statusTv.setText("Seen");
                holder.statusIconIv.setImageResource(R.drawable.ic_deleverd);
            } else {
                holder.statusTv.setText("Delivered");
                holder.statusIconIv.setImageResource(R.drawable.ic_sent);
            }
        }else{
            holder.statusTv.setVisibility(View.GONE);
            holder.statusIconIv.setVisibility(View.GONE);
        }

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(C_context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you wanna delete message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteMessage(position);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();

                return true;
            }
        });
    }

    private void DeleteMessage(int position) {
        final String mUid =FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp = m_chat.get(position).getTimestamp();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbReference.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()) {
                    if(snapshot.child("sender").getValue().equals(mUid)) {

                        //IF U Wanna Delete it
                        // snapshot.getRef().removeValue();
                        //If U Wanna Replace it with delete msg
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "this message was deleted");
                        hashMap.put("messagePic", "");
                        snapshot.getRef().updateChildren(hashMap);
                        Toast.makeText(C_context,"Deleted",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(C_context,"U can delete only your message...",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    // < View Holder >
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv ,messageIv , statusIconIv;
        private TextView messageTv,timeTv ,statusTv;
        private ConstraintLayout layout;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.Avatar);
            messageTv = itemView.findViewById(R.id.MessageTv);
            messageIv = itemView.findViewById(R.id.MessageIv);
            timeTv= itemView.findViewById(R.id.MessageTime);
            statusTv = itemView.findViewById(R.id.MessageStatus);
            statusIconIv =  itemView.findViewById(R.id.MessageStatusIcon);
            layout =itemView.findViewById(R.id.message_lay);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (m_chat.get(position).getSender().equals(fUser.getUid())){
            return Msg_Type_Right;
        }else{
            return Msg_Type_Left;
        }
    }

    private String timestampToString (String time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yy, hh:mm aa",calendar).toString();
        return datetime;
    }

}
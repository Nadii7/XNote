package com.example.monadii.notex.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Models.Post;
import com.example.monadii.notex.R;

import java.util.List;

public class Profile_Adapter extends RecyclerView.Adapter<Profile_Adapter.ProfileViewHolder> {
    private Context P_context;
    private List<Post> P_notes;

    public Profile_Adapter(Context p_context, List<Post> p_notes) {
        P_context = p_context;
        P_notes = p_notes;
    }

    @NonNull
    @Override
    public Profile_Adapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater p_inflater = LayoutInflater.from(P_context);
        view = p_inflater.inflate(R.layout.note,parent,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Profile_Adapter.ProfileViewHolder holder, int position) {
        Glide.with(P_context).load(P_notes.get(position).getuAvatar()).into(holder.user_pic);
        Glide.with(P_context).load(P_notes.get(position).getpImage()).into(holder.note_pic);
        holder.user_name.setText(P_notes.get(position).getuName());
        holder.date.setText( P_notes.get(position).getpTime());
        holder.note.setText(P_notes.get(position).getpNote());
    }

    @Override
    public int getItemCount() {
        return P_notes.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_pic , note_pic;
        private TextView user_name,date,note;
        private ConstraintLayout note_cons;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            user_pic = itemView.findViewById(R.id.N_user_pic);
            note_pic = itemView.findViewById(R.id.N_pic);
            user_name =itemView.findViewById(R.id.N_user_name);
            date = itemView.findViewById(R.id.N_date);
            note = itemView.findViewById(R.id.N_txt);

        }
    }

}

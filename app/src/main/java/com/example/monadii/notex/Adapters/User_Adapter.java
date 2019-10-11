package com.example.monadii.notex.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Activities.ProfileX;
import com.example.monadii.notex.Models.User;
import com.example.monadii.notex.R;

import java.util.List;

public class User_Adapter extends RecyclerView.Adapter<User_Adapter.UserViewHolder> {
    private Context S_context;
    private List<User> users;

    public User_Adapter(Context s_context, List<User> users) {
        S_context = s_context;
        this.users = users;
    }

    @NonNull
    @Override
    public User_Adapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater h_inflater = LayoutInflater.from(S_context);
        view = h_inflater.inflate(R.layout.user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final User_Adapter.UserViewHolder holder, final int position) {
        holder.user_name.setText(users.get(position).getName());
        Glide.with(S_context).load(users.get(position).getAvatar()).into(holder.user_pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(S_context, ProfileX.class);
                intent.putExtra("uid",users.get(position).getUid());
                S_context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_pic;
        private TextView user_name;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            user_pic = itemView.findViewById(R.id.S_user_pic);
            user_name = itemView.findViewById(R.id.S_user_name);
        }
    }
}

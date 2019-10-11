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
import com.example.monadii.notex.Activities.Message;
import com.example.monadii.notex.Models.User;
import com.example.monadii.notex.R;

import java.util.List;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ResultViewHolder> {
    private Context R_context;
    private List<User> users;

    public Search_Adapter(Context r_context, List<User> users) {
        R_context = r_context;
        this.users = users;
    }

    @NonNull
    @Override
    public Search_Adapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater r_inflater = LayoutInflater.from(R_context);
        view = r_inflater.inflate(R.layout.result,parent,false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Search_Adapter.ResultViewHolder holder, final int position) {
        holder.R_user_name.setText(users.get(position).getName());
        holder.R_user_mail.setText(users.get(position).getEmail());
        Glide.with(R_context).load(users.get(position).getAvatar()).into(holder.R_user_pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(R_context, Message.class);
                intent.putExtra("hisUid",users.get(position).getUid());
                R_context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        private ImageView R_user_pic;
        private TextView R_user_name,R_user_mail;
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            R_user_pic = itemView.findViewById(R.id.R_user_pic);
            R_user_name = itemView.findViewById(R.id.R_user_name);
            R_user_mail = itemView.findViewById(R.id.R_user_mail);
        }
    }
}

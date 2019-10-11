package com.example.monadii.notex.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.monadii.notex.Activities.Settings;
import com.example.monadii.notex.Adapters.Search_Adapter;
import com.example.monadii.notex.Models.User;
import com.example.monadii.notex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    private RecyclerView user_rec;
    private List<User> userList;
    private Search_Adapter user_adapter;
    private Button setting;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View message = inflater.inflate(R.layout.fragment_message, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        userList = new ArrayList<>();
        user_rec = message.findViewById(R.id.Message_rec);
        user_rec.setLayoutManager(new LinearLayoutManager(this.getContext()));


        //Setting Activity
        setting = message.findViewById(R.id.M_setting_but) ;
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getContext(), Settings.class);
                startActivity(intent);
            }
        });

        getAllUsers();

        return message;
    }


    private void getAllUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                        userList.add(user);

                    user_adapter = new Search_Adapter(getActivity(),userList);
                    user_rec.setAdapter(user_adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Fire Base & Navigation Header
    private void Update (){
        try {
            ImageView UserPic = getView().findViewById(R.id.M_H_user_pic);
            Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPic);
        }catch (Exception e){}
    }

    @Override
    public void onStart() {
        Update();
        super.onStart();
    }
}

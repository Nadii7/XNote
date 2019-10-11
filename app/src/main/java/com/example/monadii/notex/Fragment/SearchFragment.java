package com.example.monadii.notex.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monadii.notex.Activities.Settings;
import com.example.monadii.notex.Adapters.Home_Adapter;
import com.example.monadii.notex.Adapters.User_Adapter;
import com.example.monadii.notex.Models.Post;
import com.example.monadii.notex.Models.User;
import com.example.monadii.notex.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private Button setting;
    private SearchView searchView ;
    private RecyclerView user_rec;
    private RecyclerView post_rec;
    private List<User> userList;
    private List<Post> postList;
    private User_Adapter search_adapter;
    private Home_Adapter postAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View search = inflater.inflate(R.layout.fragment_search, container, false);

        userList = new ArrayList<>();
        postList = new ArrayList<>();
        post_rec = search.findViewById(R.id.SPost_res);
        user_rec = search.findViewById(R.id.User_rec);
        final TextView user = search.findViewById(R.id.textView11);
        final TextView post = search.findViewById(R.id.textView15);

        LinearLayoutManager layoutManager = (new LinearLayoutManager(getActivity()));
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        post_rec.setLayoutManager(layoutManager);
        user_rec.setLayoutManager(new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL,false));

        searchView=search.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchUsers(s);
                user.setVisibility(View.VISIBLE);
                post.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUsers(s);
                    user.setVisibility(View.VISIBLE);
                    post.setVisibility(View.VISIBLE);
                }else {
                    postList.clear();
                    userList.clear();
                    user.setVisibility(View.INVISIBLE);
                    post.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        setting = search.findViewById(R.id.set_but) ;
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getContext(), Settings.class);
                startActivity(intent);
            }
        });


        return search;
    }


    private void searchUsers(final String query ) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (user.getName().toLowerCase().contains(query.toLowerCase()) || user.getEmail().toLowerCase().contains(query.toLowerCase())){
                        userList.add(user);
                    }

                    search_adapter = new User_Adapter(getActivity(),userList);
                    search_adapter.notifyDataSetChanged();
                    user_rec.setAdapter(search_adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Post post = ds.getValue(Post.class);
                        if (post.getpNote().toLowerCase().contains(query.toLowerCase())
                                || post.getuName().toLowerCase().contains(query.toLowerCase())
                                || post.getuMail().toLowerCase().contains(query.toLowerCase()))
                        {
                            postList.add(post);

                        }

                        postAdapter = new Home_Adapter(getActivity(),postList);
                        postAdapter.notifyDataSetChanged();
                        post_rec.setAdapter(postAdapter);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }





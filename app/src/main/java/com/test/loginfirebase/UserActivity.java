package com.test.loginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.loginfirebase.databinding.ActivityUserBinding;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity implements UserListener{
    private ActivityUserBinding binding;
    private  PreferenceManager preferenceManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }
    private void setListeners()
    {
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void getUsers()
    {
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                loading(false);
                String currentUserId=preferenceManager.getString(Constants.KEY_USER_ID);
                if (task.isSuccessful() && task.getResult()!=null)
                {
                    List<User> users=new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                        if (currentUserId.equals(queryDocumentSnapshot.getId()))
                        {
                            continue;
                        }
                        com.test.loginfirebase.User user=new com.test.loginfirebase.User();
                        user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME);
                        user.email=queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                        user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                        user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                        user.id=queryDocumentSnapshot.getId();
                        users.add(user);
                    }
                    if (users.size()>0)
                    {
                        UsersAdapter usersAdapter=new UsersAdapter(users,UserActivity.this);
                        binding.userRecyclerView.setAdapter(usersAdapter);
                        binding.userRecyclerView.setVisibility(View.VISIBLE);
                    }else
                    {
                        showErrorMessage();
                    }
                }else
                {
                    showErrorMessage();
                }
            }
        });
    }
    private void showErrorMessage()
    {

        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private  void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            binding.progessBar.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.progessBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}

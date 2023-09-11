package com.test.loginfirebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.loginfirebase.databinding.LoginBinding;
import com.test.loginfirebase.databinding.SignupBinding;

import java.util.HashMap;
//import com.test.loginfirebase.databinding.ActivityMainBinding;

public class login extends AppCompatActivity {
    LoginBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager=new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding=LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    public void setListeners()
    {
        binding.textCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),signup.class));
            }
        });
        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidSignInDetails())
                {
                    signIn();
                }
            }
        });
        
    }
    private void signIn()
    {
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString()).whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0)
                {
                    DocumentSnapshot  documentSnapshot=task.getResult().getDocuments().get(0);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                    preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                    preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else
                {
                    loading(false);
                    showToast("unable to sign in");
                }
            }
        });
    }
    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progessBar.setVisibility(View.VISIBLE);
        }else
        {
            binding.progessBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private  void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private Boolean isValidSignInDetails()
    {
        if (binding.inputEmail.getText().toString().trim().isEmpty())
        {
            showToast("Enter Email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches())
        {
            showToast("Enter Valid Email");
            return  false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty())
        {
            showToast("Enter Password");
            return false;
        }else
        {
            return true;
        }
    }
}

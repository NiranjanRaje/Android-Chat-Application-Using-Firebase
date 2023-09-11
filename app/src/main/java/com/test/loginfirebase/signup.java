package com.test.loginfirebase;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.test.loginfirebase.databinding.LoginBinding;
import com.test.loginfirebase.databinding.SignupBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class signup extends AppCompatActivity {
    SignupBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=SignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        setListeners();
    }
    public void setListeners()
    {
        binding.textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidSignUpDetails())
                {
                    signUp();
                }
            }
        });
        binding.layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                picKImage.launch(intent);
            }
        });
    }
    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void signUp()
    {
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String,Object> user=new HashMap<>();
        user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                loading(false);
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading(false);
                showToast(e.getMessage());
            }
        });

    }
    private String encodedImage(Bitmap bitmap)
    {
        int previewWidth=150;
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> picKImage= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()==RESULT_OK)
            {
                if (result.getData()!=null)
                {
                    Uri imageUri =result.getData().getData();
                    try {
                        InputStream inputStream=getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodedImage=encodedImage(bitmap);
                    }catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    });
    private Boolean isValidSignUpDetails()
    {
        if (encodedImage==null)
        {
            showToast("Select Profile Image");
            return false;
        }else if (binding.inputName.getText().toString().trim().isEmpty())
        {
            showToast("Enter Name");
            return false;
        }else  if (binding.inputEmail.getText().toString().trim().isEmpty())
        {
            showToast("Enter Email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText()).matches())
        {
            showToast("Enter Valid Image");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty())
        {
            showToast("Enter Password");
            return false;
        }else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty())
        {
            showToast("Confirm Your Password");
            return false;
        }else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString()))
        {
            showToast("Password & Confirm Password must be same");
            return false;
        }else
        {
            return true;
        }
    }
    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            binding.buttonSignup.setVisibility(View.INVISIBLE);
            binding.progessBar.setVisibility(View.VISIBLE);
        }else
        {
            binding.progessBar.setVisibility(View.INVISIBLE);
            binding.buttonSignup.setVisibility(View.VISIBLE);
        }
    }
}

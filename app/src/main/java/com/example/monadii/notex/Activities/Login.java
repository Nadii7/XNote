package com.example.monadii.notex.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monadii.notex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText user_mail,user_pass;
    private ProgressBar progressBar;
    private Button login , reset_Password ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user_mail = findViewById(R.id.login_mail);
        user_pass = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_bar);

        Button sigup = findViewById(R.id.sign);
        sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                final String mail = user_mail.getText().toString();
                final String password = user_pass.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    ShowMessage("Please Verify all fields");
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Signin(mail , password);
                }

            }
        });

        reset_Password= findViewById(R.id.passReset);
        reset_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordReset();
            }
        });


    }

    private void PasswordReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password For..");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText email = new EditText(this);
        if(!user_mail.getText().toString().isEmpty()) {
            email.setText(user_mail.getText());
        }else{
            email.setHint("your-mail@example.com");
        }
        linearLayout.addView(email);
        linearLayout.setPadding(100,50,50,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = email.getText().toString().trim();
                Reset(mail);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void Reset(String mail) {
        mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ShowMessage("Email Sent");
                }else{
                    ShowMessage("Failed....");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ShowMessage(""+e.getMessage());
            }
        });
    }

    private void ShowMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    public void SignUp(){
        Intent intent = new Intent(this, Sign_Up.class);
        startActivity(intent);
    }

    public void Signin(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if (task.isSuccessful()) {
               progressBar.setVisibility(View.INVISIBLE);
               login.setVisibility(View.VISIBLE);
               UpdateUi();

           }else{
               ShowMessage(task.getException().getMessage());
               progressBar.setVisibility(View.INVISIBLE);
               login.setVisibility(View.VISIBLE);
           }
            }
        });

    }

    private void UpdateUi() {

        Intent intent = new Intent(this,Notex.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user !=null){
            UpdateUi();
        }
    }
}


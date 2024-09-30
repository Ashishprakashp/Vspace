package com.example.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Button signupbtn = (Button) findViewById(R.id.signupbtn);
        Button loginbtn = (Button) findViewById(R.id.loginbtn);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = ((EditText)findViewById(R.id.uname)).getText().toString();
                String pw1 = ((EditText)findViewById(R.id.pw1)).getText().toString();
                String pw2 = ((EditText)findViewById(R.id.pw2)).getText().toString();
                if(!pw1.equals(pw2)){
                    Toast.makeText(Signup.this, "Re-enter the same password", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(uname,pw1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Signup.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Signup.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,MainActivity.class));
            }
        });

    }
}

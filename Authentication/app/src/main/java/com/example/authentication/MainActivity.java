package com.example.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginbtn = (Button) findViewById(R.id.loginbtn);
        Button signupbtn = (Button) findViewById(R.id.signupbtn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = ((EditText)findViewById(R.id.uname)).getText().toString();
                String pw = ((EditText)findViewById(R.id.pw)).getText().toString();
                mAuth.signInWithEmailAndPassword(uname,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(MainActivity.this,Dashboard.class));
                        }else{
                            Toast.makeText(MainActivity.this,"Invalid credentials!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Signup.class));
            }
        });
    }
}
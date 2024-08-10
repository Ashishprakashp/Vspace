package com.example.v_space;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StudentSignup extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Handler handler = new Handler();
    private Runnable checkVerificationRunnable;
    Student student;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_signup);
        Button signupbtn = findViewById(R.id.button_signup);
        Button loginbtn = findViewById(R.id.button_login);

        // Handle the incoming deep link


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rollno = ((EditText)findViewById(R.id.student_rollno)).getText().toString();
                String mobile = ((EditText)findViewById(R.id.mobile)).getText().toString();
                String mail = ((EditText)findViewById(R.id.mail)).getText().toString();
                String pw1 = ((EditText)findViewById(R.id.password1)).getText().toString();
                String pw2 = ((EditText)findViewById(R.id.password2)).getText().toString();

                if (rollno.isEmpty()) {
                    Toast.makeText(StudentSignup.this, "Enter Rollno ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mobile.isEmpty()) {
                    Toast.makeText(StudentSignup.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mail.isEmpty()) {
                    Toast.makeText(StudentSignup.this, "Enter mail id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pw1.equals(pw2)) {
                    Toast.makeText(StudentSignup.this, "Re-enter same password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pw1.isEmpty()) {
                    Toast.makeText(StudentSignup.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    student = new Student(rollno, mail, hashPassword(pw1), mobile);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                mAuth.createUserWithEmailAndPassword(mail, pw1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(StudentSignup.this, "Verification email sent. Please check your email.", Toast.LENGTH_SHORT).show();
                                            // Start checking email verification status
                                            startCheckingEmailVerification();
                                        } else {
                                            Toast.makeText(StudentSignup.this, "Failed to send verification email: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(StudentSignup.this, "Signup Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentSignup.this, StudentLogin.class));
            }
        });
    }
    public String hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for(byte b:hash){
            sb.append(String.format("%02x",b));
        }
        return sb.toString();
    }
    private void startCheckingEmailVerification() {
        final int[] limit = {0};
        checkVerificationRunnable = new Runnable() {
            @Override
            public void run() {
                if(limit[0] <=60000){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (user.isEmailVerified()) {
                                        Toast.makeText(StudentSignup.this, "Email verified", Toast.LENGTH_SHORT).show();
                                        goToHomePage();
                                    } else {
                                        // Continue checking if email is not verified
                                        handler.postDelayed(checkVerificationRunnable, 5000); // Check every 5 seconds
                                    }
                                } else {
                                    Toast.makeText(StudentSignup.this, "Error checking email verification: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        limit[0] +=5000;
                    }
                }else{
                    Toast.makeText(StudentSignup.this, "Verification time limit exceeded", Toast.LENGTH_SHORT).show();
                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(StudentSignup.this, "User deleted", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(StudentSignup.this, "Error deleting user: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        };

        handler.post(checkVerificationRunnable); // Start checking
    }
    private void goToHomePage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("students").child(student.getRollno()).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(StudentSignup.this, StudentDashboard.class);
                    startActivity(intent);
                    finish(); // Optionally close the current activity
                }else{
                    Toast.makeText(StudentSignup.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
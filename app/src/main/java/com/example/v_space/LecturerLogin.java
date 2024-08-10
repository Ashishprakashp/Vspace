package com.example.v_space;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class LecturerLogin extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturer_login);
        Button signupbtn = findViewById(R.id.button_signup);
        Button loginbtn = findViewById(R.id.button_login);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LecturerLogin.this,LecturerSignup.class));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = ((EditText)findViewById(R.id.mail_et)).getText().toString();
                String pw = ((EditText)findViewById(R.id.pw_et)).getText().toString();
                if(mail.isEmpty() || pw.isEmpty()){
                    Toast.makeText(LecturerLogin.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("lecturers");
                    myRef.orderByChild("mail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                // Iterate through the snapshot's children
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    Lecturer lecturer = childSnapshot.getValue(Lecturer.class);
                                    if (lecturer != null) {
                                        // Sign in with Firebase Authentication
                                        mAuth.signInWithEmailAndPassword(mail, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){
                                                    String l_id = lecturer.getL_Id(); // Get l_id from the Lecturer object
                                                    Toast.makeText(LecturerLogin.this, "Lecturer id: " + l_id, Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(LecturerLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                    UserSession.getInstance().setUserEmail(mail);
                                                    UserSession.getInstance().setL_id(l_id);
                                                    startActivity(new Intent(LecturerLogin.this, LecturerDashboard.class));
                                                } else {
                                                    Toast.makeText(LecturerLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        break; // Exit the loop after processing the first match
                                    }
                                }
                            } else {
                                Toast.makeText(LecturerLogin.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors here
                        }
                    });
                }
            }
        });

    }
}

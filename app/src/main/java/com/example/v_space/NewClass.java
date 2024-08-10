package com.example.v_space;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewClass extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_class_creation);
        Button createbtn = findViewById(R.id.create_btn);
        Button cancelbtn = findViewById(R.id.cancel_btn);

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String classCode = ((EditText) findViewById(R.id.classcode_et)).getText().toString();
                String degree = ((EditText) findViewById(R.id.degree_et)).getText().toString();
                String yearOfStudy = ((EditText) findViewById(R.id.year_et)).getText().toString();
                String section = ((EditText) findViewById(R.id.section_et)).getText().toString();
                Department department=(Department) getIntent().getSerializableExtra("department");
                String departmentCode=department.getDepartmentCode();
                ParticularClass newClass = new ParticularClass(classCode, degree, yearOfStudy, section, departmentCode);
                DatabaseReference myRef = database.getReference("classes");
                myRef.child(classCode).setValue(newClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            DatabaseReference myRef = database.getReference("schedules");
                            List<String> days = new ArrayList<>(Collections.nCopies(8, ""));
                            List<String> hours = new ArrayList<>(Collections.nCopies(13, ""));
                            List<List<String>> subjectCodes = new ArrayList<>();
                            List<List<String>> subjectNames = new ArrayList<>();
                            List<List<String>> lecturerNames = new ArrayList<>();
                            List<List<String>> roomNumbers = new ArrayList<>();

// Initialize the 2D lists
                            for (int i = 0; i < 8; i++) {
                                subjectCodes.add(new ArrayList<>(Collections.nCopies(13, "")));
                                subjectNames.add(new ArrayList<>(Collections.nCopies(13, "")));
                                lecturerNames.add(new ArrayList<>(Collections.nCopies(13, "")));
                                roomNumbers.add(new ArrayList<>(Collections.nCopies(13, "")));
                            }

// Create the Schedule object
                            Schedule schedule = new Schedule(days, hours, subjectCodes, subjectNames, lecturerNames, roomNumbers,
                                    newClass.getDepartmentCode(), newClass.getClassCode());
                            myRef.child(newClass.getClassCode()).setValue(schedule).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(NewClass.this, "Class clicked: " + newClass.getClassCode(), Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(NewClass.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(NewClass.this, "Failed to create class", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)findViewById(R.id.classcode_et)).setText("");
                ((EditText)findViewById(R.id.degree_et)).setText("");
                ((EditText)findViewById(R.id.year_et)).setText("");
                ((EditText)findViewById(R.id.section_et)).setText("");

            }
        });
    }
}

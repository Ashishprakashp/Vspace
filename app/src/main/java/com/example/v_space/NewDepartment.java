package com.example.v_space;

import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewDepartment extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_department);

        String email = UserSession.getInstance().getUserEmail();
        Toast.makeText(NewDepartment.this, email, Toast.LENGTH_SHORT).show();

        DatabaseReference myRef = database.getReference("departments");
        DatabaseReference myRef2 = database.getReference("lecturers");

        Button createbtn = findViewById(R.id.create_dept_btn);
        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String collegeCode = ((EditText) findViewById(R.id.collegecode_et)).getText().toString();
                String collegeName = ((EditText) findViewById(R.id.collegename_et)).getText().toString();
                String departmentCode = ((EditText) findViewById(R.id.deptcode_et)).getText().toString();
                String departmentName = ((EditText) findViewById(R.id.deptname_et)).getText().toString();
                String hodName = ((EditText) findViewById(R.id.hodname_et)).getText().toString();

                Department department = new Department(collegeCode, collegeName, departmentCode, departmentName, hodName);
                myRef.child(departmentCode).setValue(department);

                myRef2.orderByChild("mail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot lecturerSnapshot : dataSnapshot.getChildren()) {
                                String lecturerKey = lecturerSnapshot.getKey();

                                // Update the department code for this lecturer
                                myRef2.child(lecturerKey).child("dcode").setValue(departmentCode);
                                Toast.makeText(NewDepartment.this, "Department code updated", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(NewDepartment.this, "Lecturer not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                        Toast.makeText(NewDepartment.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(NewDepartment.this, "Department created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewDepartment.this, LecturerDashboard.class));
            }
        });
    }
}

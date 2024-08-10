package com.example.v_space;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NewClassFragment extends Fragment {
    private static final String TAG = "NewClassFragment";
    private FirebaseDatabase database;
    private DatabaseReference classesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_class_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();

        Department department = null;
        if (getArguments() != null) {
            department = (Department) getArguments().getSerializable("department");
            if (department != null) {
                Toast.makeText(getContext(), "Department Code: " + department.getDepartmentCode() + "\nDepartment Name: " + department.getDepartmentName(), Toast.LENGTH_SHORT).show();
                fetchClasses(department.getDepartmentCode());
            } else {
                Toast.makeText(getContext(), "Department data is missing", Toast.LENGTH_SHORT).show();
            }
        }

        Button createbtn = view.findViewById(R.id.create_dept_btn);
        Department finalDepartment = department;
        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewClass.class);
                if (finalDepartment != null) {
                    intent.putExtra("department", finalDepartment);
                }
                startActivity(intent);
            }
        });
    }

    private void fetchClasses(String departmentCode) {
        classesRef = database.getReference("classes");
        classesRef.orderByChild("departmentCode").equalTo(departmentCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            displayClasses(dataSnapshot);
                        } else {
                            Toast.makeText(getContext(), "No classes found for this department", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to retrieve classes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayClasses(DataSnapshot dataSnapshot) {
        View rootView = getView();
        if (rootView != null) {
            LinearLayout classContainer = rootView.findViewById(R.id.fragment_container); // Ensure this ID matches your layout
            if (classContainer != null) {
                classContainer.removeAllViews(); // Clear any existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ParticularClass classItem = snapshot.getValue(ParticularClass.class);
                    if (classItem != null) {
                        Log.d(TAG, "Class item retrieved: " + classItem.getClassCode());

                        LinearLayout classLayout = new LinearLayout(getContext());
                        classLayout.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        150
                                )
                        );
                        classLayout.setOrientation(LinearLayout.VERTICAL);
                        classLayout.setBackgroundResource(R.drawable.create_class_box);
                        classLayout.setPadding(15, 15, 15, 15);

                        TextView classTextView = new TextView(getContext());
                        classTextView.setText(classItem.getYearOfStudy() + " " + classItem.getDegree() + " " + classItem.getSection());
                        classTextView.setPadding(16, 16, 16, 16);
                        classTextView.setTextSize(16); // Optional: Add a background drawable
                        classLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("class", classItem);
                                Intent intent = new Intent(getActivity(), Scheduler.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                Toast.makeText(getContext(), "Class clicked: " + classItem.getClassCode(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        classLayout.addView(classTextView);
                        classContainer.addView(classLayout);
                    } else {
                        Log.d(TAG, "Class item is null");
                    }
                }
            } else {
                Log.d(TAG, "Class container is null");
            }
        } else {
            Log.d(TAG, "Root view is null");
        }
    }

}
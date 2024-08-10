package com.example.v_space;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class LecturerDashboardFragment extends Fragment {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG = "LecturerDashboardFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lecturer_dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String L_id = UserSession.getInstance().getL_id();

        if (L_id == null || L_id.isEmpty()) {
            Toast.makeText(getContext(), "Lecturer ID is not set", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Lecturer ID is null or empty");
            return;
        }

        DatabaseReference lecturerRef = database.getReference("lecturers").child(L_id);
        lecturerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Lecturer lecturer = dataSnapshot.getValue(Lecturer.class);
                    if (lecturer != null) {
                        String collegeCode = lecturer.getCollege_code();
                        if (collegeCode != null && !collegeCode.isEmpty()) {
                            fetchDepartments(collegeCode);
                        } else {
                            Toast.makeText(getContext(), "College code is not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Lecturer not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve lecturer data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDepartments(String collegeCode) {
        DatabaseReference departmentRef = database.getReference("departments");
        departmentRef.orderByChild("collegeCode").equalTo(collegeCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Department department = snapshot.getValue(Department.class);
                                if (department != null) {
                                    displayDepartmentInfo(department);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "No departments found for this college code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to retrieve departments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayDepartmentInfo(Department department) {
        if (department == null) {
            Toast.makeText(getContext(), "Department data is null", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Department Info: " + department.getDepartmentCode() + " - " + department.getDepartmentName());

        // Find the LinearLayout inside the ScrollView
        LinearLayout departmentContainer = getView().findViewById(R.id.departmentContainer);
        if (departmentContainer == null) {
            Log.e(TAG, "LinearLayout is null");
            return;
        }

        LinearLayout newLayout = new LinearLayout(getContext());
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newLayout.setOrientation(LinearLayout.VERTICAL);
        newLayout.setBackgroundResource(R.drawable.create_class_box);
        newLayout.setPadding(15, 15, 15, 15);

        LinearLayout newLayout2 = new LinearLayout(getContext());
        newLayout2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newLayout2.setOrientation(LinearLayout.HORIZONTAL);
        newLayout2.setPadding(15, 15, 15, 15);

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv.setText(department.getDepartmentCode() + " : " + department.getDepartmentName());
        tv.setPadding(0, 0, 0, 20);
        tv.setTextSize(16);

        Button button = new Button(getContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        button.setText("Open");
        button.setBackgroundResource(R.drawable.create_class_btn);
        button.setPadding(10, 10, 10, 10);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("department", department);
                Fragment newClassFragment = new NewClassFragment();
                newClassFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, newClassFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button button2 = new Button(getContext());
        button2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        button2.setText("Register");
        button2.setBackgroundResource(R.drawable.create_class_btn);
        button2.setPadding(10, 10, 10, 10);

        DatabaseReference lecturerDcodeRef = database.getReference("lecturers").child(UserSession.getInstance().getL_id()).child("dcode");

        // Retrieve the current dcode value
        lecturerDcodeRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String currentDcode = (String) task.getResult().getValue();

                    // Check if the lecturer is already registered
                    boolean isAlreadyRegistered = currentDcode != null && currentDcode.equals(department.getDepartmentCode());

                    if (isAlreadyRegistered) {
                        button2.setEnabled(false);
                        button2.setText("Registered");
                    } else {
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (currentDcode != null && !currentDcode.isEmpty()) {
                                    // Display an alert dialog for confirmation
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Confirmation")
                                            .setMessage("You are already registered to department " + currentDcode + ". Do you want to change it?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Update the dcode with the new department
                                                    lecturerDcodeRef.setValue(department.getDepartmentCode())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Registered to " + department.getDepartmentName(), Toast.LENGTH_SHORT).show();
                                                                        reloadFragment();
                                                                    } else {
                                                                        Toast.makeText(getContext(), "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                } else {
                                    // Register the lecturer to the department
                                    lecturerDcodeRef.setValue(department.getDepartmentCode())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Registered to " + department.getDepartmentName(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve registration info: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        newLayout2.addView(button);
        newLayout2.addView(button2);
        newLayout.addView(tv);
        newLayout.addView(newLayout2);
        departmentContainer.addView(newLayout);  // Add new department layout to the container
    }

    private void reloadFragment() {
        Fragment fragment = new LecturerDashboardFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

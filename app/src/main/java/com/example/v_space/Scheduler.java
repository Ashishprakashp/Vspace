package com.example.v_space;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler extends AppCompatActivity {

    private Spinner subjectSpinner, lecturerSpinner;
    private DatabaseReference databaseRef;
    private TableLayout tableLayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ParticularClass particularClass;
    Schedule schedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler);

        subjectSpinner = findViewById(R.id.subject_list_spinner);
        lecturerSpinner = findViewById(R.id.lecturer_list_spinner);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseRef.child("subjects");
        particularClass = (ParticularClass) getIntent().getSerializableExtra("class");
        loadSubjects();
        loadLecturers();

        Button addSubjectButton = findViewById(R.id.add_subject_btn);
        addSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subjectName = ((EditText) findViewById(R.id.subject_name_et)).getText().toString();
                String subjectCode = ((EditText) findViewById(R.id.subject_code_et)).getText().toString();
                String lecturerName = ((Spinner) findViewById(R.id.lecturer_list_spinner)).getSelectedItem().toString();
                if(subjectName.equals("No lecturers available")){
                    Toast.makeText(Scheduler.this, "No lecturer selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (subjectName.isEmpty() || subjectCode.isEmpty()) {
                    Toast.makeText(Scheduler.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Subject subject = new Subject(subjectName, subjectCode, lecturerName);
                myRef.child(subjectCode).setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Scheduler.this, "Subject added", Toast.LENGTH_SHORT).show();
                            loadSubjects(); // Refresh the subjects spinner
                        } else {
                            Toast.makeText(Scheduler.this, "Failed to add subject", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        tableLayout = findViewById(R.id.table_layout);
        populateTimetable();

    }
    private void loadSubjects() {
        DatabaseReference subjectsRef = databaseRef.child("subjects");
        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjectList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String subjectName = snapshot.child("subjectName").getValue(String.class);
                        if (subjectName != null) {
                            subjectList.add(subjectName);
                        }
                    }
                }
                if (subjectList.isEmpty()) {
                    subjectList.add("No subjects available");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, subjectList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Scheduler.this, "Failed to load subjects.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLecturers() {
        // Retrieve the department code from the Intent

        String dcode = particularClass.getDepartmentCode(); // Get department code from the class

        // Reference to the lecturers node in Firebase
        DatabaseReference lecturersRef = databaseRef.child("lecturers");

        // Query to filter lecturers by department code
        lecturersRef.orderByChild("dcode").equalTo(dcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> lecturerList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the lecturer's name from each snapshot
                        String lecturerName = snapshot.child("mail").getValue(String.class); // You may want to use `mail` or another field for the name
                        if (lecturerName != null) {
                            lecturerList.add(lecturerName);
                            // Debug log
                            Log.d("Scheduler", "Lecturer found: " + lecturerName);
                        }
                    }
                } else {
                    // Debug log
                    Log.d("Scheduler", "No lecturers found for department code: " + dcode);
                }

                if (lecturerList.isEmpty()) {
                    lecturerList.add("No lecturers available");
                }

                // Set up the adapter for the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, lecturerList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lecturerSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Scheduler.this, "Failed to load lecturers.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("ResourceAsColor")
    private void populateTimetable() {
        tableLayout.removeAllViews();

        // Initialize your Firebase database reference
        DatabaseReference myRef = database.getReference("schedules").child(particularClass.getClassCode());

        // Add a value event listener to read data from Firebase
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    schedule = snapshot.getValue(Schedule.class);
                    if (schedule != null) {
                        List<List<String>> subjectCodes = schedule.getSubject_code();
                        List<List<String>> subjectNames = schedule.getSubject_name();
                        List<List<String>> lecturerNames = schedule.getLecturer_name();
                        List<List<String>> roomNumbers = schedule.getRoom_number();

                        // Assume there are 7 rows (days) and 12 columns (hours) as in the initial array
                        String[][] timetableData = new String[7][13];

                        // Fill in the first row with headers
                        timetableData[0] = new String[]{"Day/Hour", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

                        // Populate the timetable with data from the database
                        for (int row = 1; row < 7; row++) { // Assuming 6 days
                            timetableData[row][0] = getDayName(row); // Populate the day names (e.g., Monday, Tuesday)
                            for (int col = 1; col < 13; col++) { // Assuming 12 hours
                                String subjectCode = subjectCodes.get(row).get(col);
                                String subjectName = subjectNames.get(row).get(col);
                                String lecturerName = lecturerNames.get(row).get(col);
                                String roomNumber = roomNumbers.get(row).get(col);

                                timetableData[row][col] = subjectName.isEmpty() ? "N/A" : subjectName; // Fill with subject names or "N/A"
                            }
                        }

                        // Now populate the table with the `timetableData` array
                        populateTableWithTimetableData(timetableData);
                    }
                } else {
                    Toast.makeText(Scheduler.this, "No schedule data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Scheduler.this, "Failed to load timetable: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper function to populate the table layout with the timetable data
    private void populateTableWithTimetableData(String[][] timetableData) {
        for (int row = 0; row < timetableData.length; row++) {
            TableRow tableRow = new TableRow(this);

            for (int col = 0; col < timetableData[row].length; col++) {
                TextView cellView = (TextView) getLayoutInflater().inflate(R.layout.timetable_cell, tableRow, false);
                cellView.setText(timetableData[row][col]);
                cellView.setTag(new int[]{row, col}); // Store row and column as an array

                // Apply colors based on position
                if (row == 0 && col == 0) {
                    cellView.setBackgroundColor(ContextCompat.getColor(this, R.color.blue)); // First cell in the first row
                } else if (row == 0) {
                    cellView.setBackgroundColor(ContextCompat.getColor(this, R.color.white)); // Rest of the cells in the first row
                } else if (col == 0) {
                    cellView.setBackgroundColor(ContextCompat.getColor(this, R.color.white)); // Rest of the cells in the first column
                } else {
                    cellView.setBackgroundColor(ContextCompat.getColor(this, R.color.grey)); // All other cells
                }

                cellView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleCellClick(v, particularClass);
                    }
                });

                tableRow.addView(cellView);
            }

            tableLayout.addView(tableRow);
        }
    }

    // Helper function to convert row index to day name
    private String getDayName(int index) {
        switch (index) {
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            default: return "";
        }
    }



    private void handleCellClick(View view, ParticularClass particularClass) {
        TextView cell = (TextView) view;
        int[] position = (int[]) cell.getTag(); // Retrieve the row and column from the tag
        int row = position[0];
        int col = position[1];

        String cellText = cell.getText().toString();
        String cellId = "cell_" + row + "_" + col;

        // Display a toast showing which cell was clicked
        Toast.makeText(this, "Clicked: " + cellText + " at row " + row + ", column " + col, Toast.LENGTH_SHORT).show();

        // Initialize your Firebase database reference
        DatabaseReference myRef = database.getReference("schedules").child(particularClass.getClassCode());

        // Add a value event listener to read data from Firebase
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    schedule = snapshot.getValue(Schedule.class);
                    if (schedule != null) {
                        List<List<String>> subjectCodes = schedule.getSubject_code();
                        List<List<String>> subjectNames = schedule.getSubject_name();
                        List<List<String>> lecturerNames = schedule.getLecturer_name();
                        List<List<String>> roomNumbers = schedule.getRoom_number();

                        String subjectCode = subjectCodes.get(row).get(col);
                        String subjectName = subjectNames.get(row).get(col);
                        String lecturerName = lecturerNames.get(row).get(col);
                        String roomNumber = roomNumbers.get(row).get(col);

                        if (subjectCode.isEmpty() && subjectName.isEmpty() && lecturerName.isEmpty() && roomNumber.isEmpty()) {
                            showDataInputDialog(myRef, row, col, subjectCodes, subjectNames, lecturerNames, roomNumbers);
                        } else {
                            showDataDisplayDialog(subjectCode, subjectName, lecturerName, roomNumber);
                        }
                    } else {
                        Toast.makeText(Scheduler.this, "No data available for this cell.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Scheduler.this, "Selected cell: " + cellId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Scheduler.this, "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDataInputDialog(DatabaseReference myRef, int row, int col, List<List<String>> subjectCodes, List<List<String>> subjectNames, List<List<String>> lecturerNames, List<List<String>> roomNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Scheduler.this);
        builder.setTitle("Enter Data");

        View dialogView = getLayoutInflater().inflate(R.layout.data_input_dialog, null);
        Spinner subjectNameSpinner = dialogView.findViewById(R.id.subject_name_spinner);
        Spinner subjectCodeSpinner = dialogView.findViewById(R.id.subject_code_spinner);
        Spinner lecturerNameSpinner = dialogView.findViewById(R.id.lecturer_name_spinner); // Changed from EditText to Spinner
        EditText roomNumberInput = dialogView.findViewById(R.id.room_number_input);

        // Load the lecturers into the lecturerNameSpinner
        loadLecturersForDialog(lecturerNameSpinner);

        // Assume you have a list of subjects from your database
        List<String> subjectNamesList = new ArrayList<>();
        Map<String, String> subjectNameToCodeMap = new HashMap<>();

        // Fetch subjects from Firebase (assuming it's already done before this point)
        DatabaseReference subjectsRef = database.getReference("subjects");
        subjectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    String subjectName = subjectSnapshot.child("subjectName").getValue(String.class);
                    String subjectCode = subjectSnapshot.child("subjectCode").getValue(String.class);

                    subjectNamesList.add(subjectName);
                    subjectNameToCodeMap.put(subjectName, subjectCode);
                }

                // Set up the subject name spinner
                ArrayAdapter<String> subjectNameAdapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, subjectNamesList);
                subjectNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectNameSpinner.setAdapter(subjectNameAdapter);

                // Update the subject code spinner based on subject name selection
                subjectNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedSubjectName = subjectNamesList.get(position);
                        String correspondingSubjectCode = subjectNameToCodeMap.get(selectedSubjectName);

                        // Set the subject code in the spinner
                        List<String> subjectCodeList = Collections.singletonList(correspondingSubjectCode);
                        ArrayAdapter<String> subjectCodeAdapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, subjectCodeList);
                        subjectCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        subjectCodeSpinner.setAdapter(subjectCodeAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle case where no subject is selected
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Scheduler.this, "Failed to load subjects.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedSubjectName = (String) subjectNameSpinner.getSelectedItem();
                String selectedSubjectCode = (String) subjectCodeSpinner.getSelectedItem();
                String lecturerName = lecturerNameSpinner.getSelectedItem().toString(); // Get lecturer name from spinner
                String roomNumber = roomNumberInput.getText().toString();

                subjectCodes.get(row).set(col, selectedSubjectCode);
                subjectNames.get(row).set(col, selectedSubjectName);
                lecturerNames.get(row).set(col, lecturerName);
                roomNumbers.get(row).set(col, roomNumber);

                Schedule updatedSchedule = new Schedule(schedule.getDay(), schedule.getHour(), subjectCodes, subjectNames, lecturerNames, roomNumbers, schedule.getDepartment_code(), schedule.getClass_code());
                myRef.setValue(updatedSchedule).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            populateTimetable();
                            Toast.makeText(Scheduler.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Scheduler.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadLecturersForDialog(Spinner lecturerNameSpinner) {
        String dcode = particularClass.getDepartmentCode(); // Get department code from the class

        DatabaseReference lecturersRef = databaseRef.child("lecturers");

        lecturersRef.orderByChild("dcode").equalTo(dcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> lecturerList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String lecturerName = snapshot.child("l_Id").getValue(String.class); // Adjust field name as needed
                        if (lecturerName != null) {
                            lecturerList.add(lecturerName);
                        }
                    }
                }

                if (lecturerList.isEmpty()) {
                    lecturerList.add("No lecturers available");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, lecturerList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lecturerNameSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Scheduler.this, "Failed to load lecturers.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadSubjectsForDialog(Spinner subjectNameSpinner, Spinner subjectCodeSpinner) {
        DatabaseReference subjectsRef = databaseRef.child("subjects");
        subjectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjectNames = new ArrayList<>();
                List<String> subjectCodes = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String subjectName = snapshot.child("subjectName").getValue(String.class);
                    String subjectCode = snapshot.child("subjectCode").getValue(String.class);

                    if (subjectName != null && subjectCode != null) {
                        subjectNames.add(subjectName);
                        subjectCodes.add(subjectCode);
                    }
                }

                ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, subjectNames);
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectNameSpinner.setAdapter(nameAdapter);

                ArrayAdapter<String> codeAdapter = new ArrayAdapter<>(Scheduler.this, android.R.layout.simple_spinner_item, subjectCodes);
                codeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectCodeSpinner.setAdapter(codeAdapter);

                // Set listener to update code spinner when subject name is selected
                subjectNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        subjectCodeSpinner.setSelection(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Scheduler.this, "Failed to load subjects.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDataDisplayDialog(String subjectCode, String subjectName, String lecturerName, String roomNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Scheduler.this);
        builder.setTitle("Subject Details");

        String message = "Subject Code: " + subjectCode + "\n" +
                "Subject Name: " + subjectName + "\n" +
                "Lecturer Name: " + lecturerName + "\n" +
                "Room Number: " + roomNumber;

        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

}

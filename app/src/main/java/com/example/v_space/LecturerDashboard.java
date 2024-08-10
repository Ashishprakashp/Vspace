package com.example.v_space;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LecturerDashboard extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturer_dashboard);
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null && data.getQueryParameter("dcode") != null) {
            String departmentCode = data.getQueryParameter("dcode");

            // Retrieve lecturer ID from user session
            String L_id = UserSession.getInstance().getL_id();

            // Update lecturer's department code in Firebase
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("lecturers");
            myRef.child(L_id).child("dcode").setValue(departmentCode)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Department code updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update department code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        // Load the default fragment (LecturerDashboardFragment)
        loadFragment(new LecturerDashboardFragment());

        ImageButton menubtn = findViewById(R.id.menu_btn);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int id = item.getItemId();

                if (id == R.id.item1) {
                    fragment = new LecturerDashboardFragment();
                } else if (id == R.id.item2) {
                    fragment = new NewDepartmentFragment();
                } else if (id == R.id.item3) {
                    fragment = new NewClassFragment();
                } else if (id == R.id.item4) {
                    //fragment = new NewSpaceFragment();
                } else if (id == R.id.item5) {
                    //fragment = new ProfileFragment();
                } else {
                    Toast.makeText(LecturerDashboard.this, "Invalid item selected", Toast.LENGTH_SHORT).show();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // Replace the fragment container with the selected fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}

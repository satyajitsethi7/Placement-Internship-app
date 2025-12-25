package com.example.placementproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginFragment extends Fragment {

    private EditText email, password;
    private AppCompatButton loginbtn;
    private TextView sign_up, sign_in_google, forgotpass;
    private ImageView pwicon;
    private RadioButton student, admin;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginbtn = view.findViewById(R.id.registerbtn);
//        sign_in_google = view.findViewById(R.id.signingoogle);
        sign_up = view.findViewById(R.id.sign_up);
        student = view.findViewById(R.id.studentbtn);
        admin = view.findViewById(R.id.adminbtn);
        pwicon = view.findViewById(R.id.passicon);
        forgotpass = view.findViewById(R.id.forgotpass);

        forgotpass.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgotpass, null);
            EditText emailBox = dialogView.findViewById(R.id.editTextTextEmailAddress);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            
            dialogView.findViewById(R.id.btnreset).setOnClickListener(v1 -> {
                String userEmail = emailBox.getText().toString().trim();
                if (userEmail.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Check your email to reset password", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            
            dialogView.findViewById(R.id.backtologin).setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            dialog.show();
        });

        pwicon.setOnClickListener(v -> togglePasswordVisibilty());
        
        student.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                student.setTextColor(getResources().getColor(R.color.white));
            } else {
                student.setTextColor(getResources().getColor(R.color.sign_in));
            }
        });

        admin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                admin.setTextColor(getResources().getColor(R.color.white));
            } else {
                admin.setTextColor(getResources().getColor(R.color.sign_in));
            }
        });

        loginbtn.setOnClickListener(v -> loginUser());

        sign_up.setOnClickListener(v -> {
            if (getActivity() instanceof LoginAndSignup) {
                ((LoginAndSignup) getActivity()).switchFragment(new SignupFragment());
            }
        });
        
        return view;
    }

    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String selectedRole = student.isChecked() ? "student" : "admin";

        if (userEmail.isEmpty() || userPass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserRole(mAuth.getCurrentUser().getUid(), selectedRole);
                        } else {
                            Toast.makeText(getActivity(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRole(String uid, String selectedRole) {
        DatabaseReference userRef = mDatabase.getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String roleFromDB = snapshot.child("role").getValue(String.class);
                    if (roleFromDB != null && roleFromDB.equalsIgnoreCase(selectedRole)) {
                        
                        SharedPreferences pref = requireActivity().getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("flag", false); // Means logged in
                        editor.putString("role", roleFromDB);
                        editor.apply();

                        Intent intent;
                        if (roleFromDB.equalsIgnoreCase("admin")) {
                            intent = new Intent(getActivity(), AdminActivity.class);
                        } else {
                            intent = new Intent(getActivity(), MainActivity.class);
                        }
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        mAuth.signOut();
                        Toast.makeText(getActivity(), "Access denied: Account is not " + selectedRole, Toast.LENGTH_LONG).show();
                    }
                } else {
                    mAuth.signOut();
                    Toast.makeText(getActivity(), "User record not found in database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibilty() {
        if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            pwicon.setImageResource(R.drawable.show_pwicon);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pwicon.setImageResource(R.drawable.hide_pw_icon);
        }
        password.setSelection(password.getText().length());
    }
}

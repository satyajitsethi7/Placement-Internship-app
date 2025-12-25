package com.example.placementproject;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private EditText email, password, fullname, mobnumber;
    private AppCompatButton registerbtn;
    private TextView signin;
    private ImageView pwicon;
    private RadioButton studentBtn, adminBtn;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        fullname = view.findViewById(R.id.fullname);
        registerbtn = view.findViewById(R.id.registerbtn);
        mobnumber = view.findViewById(R.id.mobnumber);
        signin = view.findViewById(R.id.sign_in);
        pwicon = view.findViewById(R.id.passicon);
        studentBtn = view.findViewById(R.id.studentbtn);
        adminBtn = view.findViewById(R.id.adminbtn);

        pwicon.setOnClickListener(v -> togglePasswordVisibilty());

        registerbtn.setOnClickListener(v -> signupUser());

        signin.setOnClickListener(v -> {
            if (getActivity() instanceof LoginAndSignup) {
                ((LoginAndSignup) getActivity()).switchFragment(new LoginFragment());
            }
        });

        studentBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                studentBtn.setTextColor(getResources().getColor(R.color.white));
            } else {
                studentBtn.setTextColor(getResources().getColor(R.color.sign_in));
            }
        });

        adminBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adminBtn.setTextColor(getResources().getColor(R.color.white));
            } else {
                adminBtn.setTextColor(getResources().getColor(R.color.sign_in));
            }
        });

        return view;
    }

    private void signupUser() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String name = fullname.getText().toString().trim();
        String mobile = mobnumber.getText().toString().trim();
        String role = studentBtn.isChecked() ? "student" : "admin";

        if (userEmail.isEmpty() || userPass.isEmpty() || name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPass.length() < 6) {
            Toast.makeText(getActivity(), "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = mDatabase.getReference("Users").child(userId);

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullname", name);
                            userData.put("email", userEmail);
                            userData.put("mobile", mobile);
                            userData.put("role", role);

                            userRef.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        if (getActivity() instanceof LoginAndSignup) {
                                            ((LoginAndSignup) getActivity()).switchFragment(new LoginFragment());
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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

package com.example.placementproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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

import java.util.concurrent.atomic.AtomicReference;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private EditText email,password;
    private AppCompatButton loginbtn;
    private TextView sign_up,sign_in_google,forgotpass;
    private TextView txtHeading;
    private ImageView pwicon;

    private RadioButton student,admin;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_login, container, false);
        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        loginbtn=view.findViewById(R.id.registerbtn);
        sign_in_google=view.findViewById(R.id.signingoogle);
        sign_up=view.findViewById(R.id.sign_up);
        student=view.findViewById(R.id.studentbtn);
        admin=view.findViewById(R.id.adminbtn);
        pwicon=view.findViewById(R.id.passicon);
        forgotpass=view.findViewById(R.id.forgotpass);

        forgotpass.setOnClickListener(v -> {
            //forgot password code here
            AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
            View dialogView=getLayoutInflater().inflate(R.layout.dialog_forgotpass, null);
            EditText emailBox=dialogView.findViewById(R.id.editTextTextEmailAddress);

            builder.setView(dialogView);
            AlertDialog dialog= builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialogView.findViewById(R.id.btnreset).setOnClickListener(v1 -> {
                String userEmail=email.getText().toString();
                //reset password code here
            });
            dialogView.findViewById(R.id.backtologin).setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            dialog.show();
        });
        pwicon.setOnClickListener(v -> togglePasswordVisibilty());
        student.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                student.setTextColor(getResources().getColor(R.color.white));
            }
            else
            {
                student.setTextColor(getResources().getColor(R.color.sign_in));
            }
        });

        admin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                admin.setTextColor(getResources().getColor(R.color.white));
            }
            else
            {
                admin.setTextColor(getResources().getColor(R.color.sign_in));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof LoginAndSignup) {
                    ((LoginAndSignup) getActivity()).switchFragment(new SignupFragment());
                }
            }
        });
        return view;
    }
    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add Firebase authentication or database logic here
        Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();
        SharedPreferences pref = requireActivity().getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("flag", false);
        editor.apply();

        if (student.isChecked()) {
            Intent iMain = new Intent(getActivity(), MainActivity.class); // Student dashboard
            startActivity(iMain);
        } else {
            Intent iAdmin = new Intent(getActivity(), AdminActivity.class); // Admin dashboard
            startActivity(iAdmin);
        }
//        Intent iMain = new Intent(getActivity(), MainActivity.class);
//        startActivity(iMain);
        requireActivity().finish();

    }
    private void togglePasswordVisibilty()
    {
        if(password.getInputType()==(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD))
        {
            password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            pwicon.setImageResource(R.drawable.show_pwicon);
        }
        else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pwicon.setImageResource(R.drawable.hide_pw_icon);
        }
        password.setSelection(password.getText().length());
    }
}
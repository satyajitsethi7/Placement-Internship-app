package com.example.placementproject;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private EditText email,password,fullname,mobnumber;
    private AppCompatButton registerbtn;
    private TextView signin;
    private ImageView pwicon;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        View view= inflater.inflate(R.layout.fragment_signup, container, false);

        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        fullname=view.findViewById(R.id.fullname);
        registerbtn=view.findViewById(R.id.registerbtn);
        mobnumber=view.findViewById(R.id.mobnumber);
        signin=view.findViewById(R.id.sign_in);
        pwicon=view.findViewById(R.id.passicon);

        pwicon.setOnClickListener(v -> togglePasswordVisibilty());

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof LoginAndSignup) {
                    ((LoginAndSignup) getActivity()).switchFragment(new LoginFragment());
                }
            }
        });
        return view;
    }
    private void signupUser() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //in this otp will sent to his/her email code here

        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        View dialogview=getLayoutInflater().inflate(R.layout.dialog_otp, null);
        EditText otptext1=dialogview.findViewById(R.id.otptext1);
        EditText otptext2=dialogview.findViewById(R.id.otptext2);
        EditText otptext3=dialogview.findViewById(R.id.otptext3);
        EditText otptext4=dialogview.findViewById(R.id.otptext4);

        //separate inner class for textwatcher
        class otptextwatcher implements TextWatcher{

            private EditText currentView;
            private EditText nextView;

            otptextwatcher(EditText currentView,EditText nextView)
            {
                this.currentView=currentView;
                this.nextView=nextView;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==1&&nextView!=null)
                {
                    nextView.requestFocus();
                } else if (s.length()==0) {
                    currentView.setSelection(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }
        otptext1.addTextChangedListener(new otptextwatcher(otptext1, otptext2));
        otptext2.addTextChangedListener(new otptextwatcher(otptext2, otptext3));
        otptext3.addTextChangedListener(new otptextwatcher(otptext3, otptext4));
        otptext4.addTextChangedListener(new otptextwatcher(otptext4, null));

        builder.setView(dialogview);
        AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialogview.findViewById(R.id.otpverifybtn).setOnClickListener(v -> {
            String otp1 = otptext1.getText().toString().trim();
            String otp2 = otptext2.getText().toString().trim();
            String otp3 = otptext3.getText().toString().trim();
            String otp4 = otptext4.getText().toString().trim();

            // Validate OTP
            if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter the complete OTP", Toast.LENGTH_SHORT).show();
            } else {
                String fullOTP = otp1 + otp2 + otp3 + otp4;
                // Proceed with OTP verification logic here
                Toast.makeText(getActivity(), "OTP Entered: " + fullOTP, Toast.LENGTH_SHORT).show();

                // Dismiss only when OTP is complete
                dialog.dismiss();

            }
            if (getActivity() instanceof LoginAndSignup) {
                ((LoginAndSignup) getActivity()).switchFragment(new LoginFragment());
            }
            //verify otp code here
        });

        dialogview.findViewById(R.id.resendotp).setOnClickListener(v -> {
            //another otp send code here
        });
        dialog.show();

        // Add Firebase authentication or database logic here

        //if otp match which we given to user then show signup successful
        //Toast.makeText(getActivity(), "Signup Successful!", Toast.LENGTH_SHORT).show();
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
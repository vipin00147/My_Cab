package com.example.fragmenttravel;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import com.example.fragmenttravel.BroadcastReceiver.OTP_Receiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.aabhasjindal.otptextview.OtpTextView;

public class EnterOtp extends Fragment {


    private MaterialButton back, verify;
    public OtpTextView otp;
    private TextView textView;
    private String generatedOtp, verificationId;


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_otp, container, false);
        back = view.findViewById(R.id.back);
        otp = view.findViewById(R.id.otp_view);
        textView = view.findViewById(R.id.textView2);
        verify = view.findViewById(R.id.verify);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            textView.setText(bundle.getString("mobile"));
            verificationId = bundle.getString("verificationId");
            if(otp.getOTP() != null && otp.getOTP().length() == 6){
                verifyCode(otp.getOTP());
            }
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out);
                LoginWithPhone fragment = new LoginWithPhone();
                transaction.replace(R.id.mainContainer, fragment);
                transaction.commit();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp.getOTP();
                if(code != null){
                    verifyCode(code);
                }
            }
        });

        requestSmsPermission();
        new OTP_Receiver().setOtp(otp);

        return view;

    }

    private void requestSmsPermission() {

        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this.getContext(), permission);

        if(grant != PackageManager.PERMISSION_GRANTED) {

            String[] permissionList = new String[1];
            permissionList[0] = permission;

            ActivityCompat.requestPermissions(this.getActivity(), permissionList, 1);
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        //inside this method we are checking if the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if the code is correct and the task is succesful we are sending our user to new activity.

                            /*Intent i =new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();*/

                        } else {
                            //if the code is not correct then we are displaying an error message to the user.
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void verifyCode(String code) {
        SweetAlertDialog dialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(true);
        dialog.show();
        try {
            //below line is used for getting getting credentials from our verification id and code.
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            //after getting credential we are calling sign in method.
            //signInWithCredential(credential);

        }
        catch (Exception e){
            dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            dialog.setTitleText("Oops...").setContentText(e.getMessage())
                    .show();
        }
    }
}
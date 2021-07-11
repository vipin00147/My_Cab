package com.example.fragmenttravel.LoginWithGoogle;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fragmenttravel.BroadcastReceiver.OTP_Receiver;
import com.example.fragmenttravel.HomeFragment;
import com.example.fragmenttravel.LoginWithPhone.LoginWithPhone;
import com.example.fragmenttravel.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.aabhasjindal.otptextview.OtpTextView;


public class EnterOtp extends Fragment {

    private MaterialButton back, verify;
    public OtpTextView otp;
    private TextView textView;
    private String  verificationId;


    private FirebaseAuth mAuth;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_otp2, container, false);

        mAuth = FirebaseAuth.getInstance();

        back = view.findViewById(R.id.back);
        otp = view.findViewById(R.id.google_otp_view);
        textView = view.findViewById(R.id.phoneNumber);
        verify = view.findViewById(R.id.googleVerify);

        bundle = getArguments();

        if (bundle != null) {
            textView.setText(bundle.getString("phone"));
            verificationId = bundle.getString("verificationId");
            if(otp.getOTP() != null && otp.getOTP().length() == 6){
                verifyCode(otp.getOTP());
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out);
                PhoneNumberInput fragment = new PhoneNumberInput();
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

    private void signInWithCredential() {
        Bundle b1 = getArguments();

        Log.d("firebaseAuthWithGoogle", "signInWithCredential:success");
        FirebaseUser user = mAuth.getCurrentUser();

        Bundle b2 = new Bundle();
        b2.putString("email",user.getEmail());
        b2.putString("name",user.getDisplayName());
        b2.putString("phone",bundle.getString("phone"));

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(b2);
        transaction.replace(R.id.mainContainer, fragment);
        transaction.commit();

    }

    private void firebaseAuthWithGoogle(String idToken) {
    }

    private void verifyCode(String code) {
        SweetAlertDialog dialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(true);
        dialog.show();
        try {
            //below line is used for getting getting credentials from our verification id and code.
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            //after getting credential we are calling sign in method.
            signInWithCredential();
            dialog.dismiss();

        }
        catch (Exception e){
            dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            dialog.setTitleText("Oops...").setContentText(e.getMessage())
                    .dismiss();
            Log.d("missingerror",e.getStackTrace().toString());
        }
    }
}
package com.example.fragmenttravel.Controller.LoginWithGoogle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.fragmenttravel.Controller.BroadcastReceiver.OTP_Receiver;
import com.example.fragmenttravel.Model.OTPModel;
import com.example.fragmenttravel.R;
import com.example.fragmenttravel.View.Welcome_;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private SweetAlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_otp2, container, false);

        dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

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
                dialog.show();
                String code = otp.getOTP();
                if(code != null){
                    verifyCode(code);
                }
                else {
                    dialog.dismiss();
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

        OTPModel.setOTPFilled(1);

        SharedPreferences save = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = save.edit();
        ed.putString("phone", b2.getString("phone"));
        ed.putString("name", b2.getString("name"));
        ed.putString("email", b2.getString("email"));
        ed.commit();

        Intent intent = new Intent(getActivity(), Welcome_.class);
        intent.putExtras(b2);
        startActivity(intent);

        dialog.dismiss();

    }

    private void firebaseAuthWithGoogle(String idToken) {
    }

    private void verifyCode(String code) {
        try {
            //below line is used for getting getting credentials from our verification id and code.
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            //after getting credential we are calling sign in method.
            signInWithCredential();

        }
        catch (Exception e){
            dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            dialog.setTitleText("Oops...").setContentText(e.getMessage())
                    .dismiss();
            Log.d("missingerror",e.getStackTrace().toString());
        }
    }
}
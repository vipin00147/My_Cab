package com.example.fragmenttravel;


import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.Manifest;

import com.example.fragmenttravel.BroadcastReceiver.OTP_Receiver;
import com.google.android.material.button.MaterialButton;
import in.aabhasjindal.otptextview.OtpTextView;

public class EnterOtp extends Fragment {


    private MaterialButton back;
    private OtpTextView otpnumber;
    private TextView textView;



    private Bundle bundle = this.getArguments();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_otp, container, false);
        back = view.findViewById(R.id.back);
        otpnumber = view.findViewById(R.id.otp_view);
        textView = view.findViewById(R.id.textView2);



        if (bundle != null) {
            String phoneNumber = bundle.getString("mobile");
            textView.setText(textView.getText()+phoneNumber);
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

        requestsmspermission();

        new OTP_Receiver().setEditText(otpnumber);

        return view;
    }
    private void requestsmspermission() {
        String smspermission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this.getContext(), smspermission);
        //check if read SMS permission is granted or not
        if(grant!= PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0]=smspermission;
            ActivityCompat.requestPermissions(this.getActivity(), permission_list,1);
        }
    }

}
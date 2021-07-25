package com.example.fragmenttravel.Controller.LoginWithGoogle;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fragmenttravel.R;
import com.example.fragmenttravel.View.SplashScreen;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

public class PhoneNumberInput extends Fragment {

    private TextView errorMessage;
    private EditText phoneNumber;
    private CountryCodePicker ccp;
    private MaterialButton next, backButton;

    String otp;
    private String verificationId;

    private static final int CREDENTIAL_PICKER_REQUEST =120 ;

    private SweetAlertDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_number_input, container, false);

        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);

        getPhone();

        backButton = view.findViewById(R.id.back);
        ccp = view.findViewById(R.id.ccp);
        phoneNumber = view.findViewById(R.id.phone_number_edt);
        errorMessage = view.findViewById(R.id.textinput_error);
        next = view.findViewById(R.id.phoneContinue);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                SplashScreen fragment = new SplashScreen();
                transaction.replace(R.id.mainContainer, fragment);
                transaction.commit();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().trim().length() == 10)
                    sendVerificationCode(ccp.getSelectedCountryCodeWithPlus()+phoneNumber.getText().toString());
                else
                    Toast.makeText(getActivity(), "Invalid Number", Toast.LENGTH_SHORT).show();
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() >= 1){
                    next.setClickable(true);
                    errorMessage.setVisibility(view.INVISIBLE);
                    next.setBackgroundColor(next.getContext().getResources().getColor(R.color.black));
                } else {
                    next.setClickable(false);
                    errorMessage.setVisibility(view.VISIBLE);
                    next.setBackgroundColor(next.getContext().getResources().getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 10) {
                    progressDialog.show();
                }
            }
        });

        return view;
    }

    private void getPhone() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(getActivity()).getHintPickerIntent(hintRequest);
        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {
            new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE).dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            phoneNumber.setText(credentials.getId().substring(3));

            sendVerificationCode(ccp.getSelectedCountryCodeWithPlus()+phoneNumber.getText().toString());

        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            Toast.makeText(getActivity(), "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }


    private void sendVerificationCode(String number) {
        //this method is used for getting OTP on user phone number.

        PhoneAuthProvider.getInstance().verifyPhoneNumber(number,
                60
                , TimeUnit.SECONDS,
                this.getActivity()
                ,mCallBack);
    }


    //callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            //initializing our callbacks for on verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //below method is used when OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //when we recieve the OTP it contains a unique id wich we are storing in our string which we have already created.
            verificationId = s;

            Bundle b1 = getArguments();

            Bundle bundle = new Bundle();
            bundle.putString("phone",phoneNumber.getText().toString());
            bundle.putString("otp",otp);
            bundle.putString("email",b1.getString("email"));
            bundle.putString("name",b1.getString("name"));
            bundle.putString("verificationId",verificationId);
            bundle.putString("idToken", b1.getString("idToken"));

            progressDialog.dismiss();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out);
            EnterOtp fragment = new EnterOtp();
            fragment.setArguments(bundle);
            transaction.replace(R.id.mainContainer, fragment);
            transaction.commit();
        }

        //this method is called when user recieve OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //below line is used for getting OTP code which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();
            //checking if the code is null or not.
            if (code != null) {
                //if the code is not null then we are setting that code to our OTP edittext field.
                otp = code;
            }
        }

        //thid method is called when firebase doesnot sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            //displaying error message with firebase exception.
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
            dialog.setTitleText("Oops...").setContentText(e.getMessage());
            dialog.setCancelable(true);
            dialog.show();
        }
    };

}
package com.example.fragmenttravel.BroadcastReceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import in.aabhasjindal.otptextview.OtpTextView;

public class OTP_Receiver extends BroadcastReceiver {
    public static OtpTextView otp;
    String extractedOtp = "";

    public void setOtp(OtpTextView otp) {
        OTP_Receiver.otp = otp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for(SmsMessage msg : messages) {
                String message  = msg.getMessageBody();
                extractedOtp = message.substring(0, 6);
                otp.setOTP(extractedOtp);
            }
        }
    }
}
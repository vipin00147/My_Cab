package com.example.fragmenttravel.Controller.LoginWithFacebook;

public class OTPModel {

    public static int OTPFilled = 0;

    public static int getOTPFilled() {
        return OTPFilled;
    }

    public static void setOTPFilled(int OTPFilled) {
        com.example.fragmenttravel.Model.OTPModel.OTPFilled = OTPFilled;
    }

}

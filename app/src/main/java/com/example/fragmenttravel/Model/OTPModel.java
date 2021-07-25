package com.example.fragmenttravel.Model;

public class OTPModel {

    public static int OTPFilled = 0;
    public static int PERMISSION_ALLOWED = 0;

    public static int getOTPFilled() {
        return OTPFilled;
    }

    public static int getPermissionAllowed() {
        return PERMISSION_ALLOWED;
    }

    public static void setPermissionAllowed(int permissionAllowed) {
        PERMISSION_ALLOWED = permissionAllowed;
    }

    public static void setOTPFilled(int OTPFilled) {
        OTPModel.OTPFilled = OTPFilled;
    }
}

package com.example.fragmenttravel.LoginWithGoogle;

import com.google.android.gms.tasks.Task;

public class GoogleSignInAccount {

    public String IdToken;
    public Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task;

    public GoogleSignInAccount(String idToken) {
        IdToken = idToken;
    }

    public GoogleSignInAccount() {

    }

    public Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> getTask() {
        return task;
    }

    public void setTask(Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task) {
        this.task = task;
    }

    public String getIdToken() {
        return IdToken;
    }

    public void setIdToken(String idToken) {
        IdToken = idToken;
    }
}

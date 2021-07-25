package com.example.fragmenttravel.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fragmenttravel.Model.OTPModel;
import com.example.fragmenttravel.R;
import com.google.android.material.button.MaterialButton;

public class Welcome extends Fragment {

    private MaterialButton allow;
    private Bundle b1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        b1 = new Bundle();

        SharedPreferences sh = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);

        String phone = sh.getString("phone", "");
        String name =  sh.getString("name", "");
        String email =  sh.getString("email", "");

        try {
            allow = view.findViewById(R.id.allowpermission);
             b1 = getArguments();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        if(b1 == null){
            b1 = new Bundle();
            b1.putString("phone",phone);
            b1.putString("name",name);
            b1.putString("email",email);
        }

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTPModel.setPermissionAllowed(1);
                Intent intent = new Intent(getActivity(), Home.class);
                            intent.putExtras(b1);
                            startActivity(intent);

            }
        });

        return  view;
    }
}
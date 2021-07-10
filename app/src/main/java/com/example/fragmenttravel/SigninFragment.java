package com.example.fragmenttravel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SigninFragment extends Fragment {
    Button create_a_new_account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        view.findViewById(R.id.createAnewAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.slide_in, R.anim.fade_out);
                SignupFragment fragment = new SignupFragment();
                transaction.replace(R.id.mainContainer, fragment);
                transaction.commit();
            }
        });

        return view;
    }
}
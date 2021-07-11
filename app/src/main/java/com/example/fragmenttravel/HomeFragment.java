package com.example.fragmenttravel;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    TextView email, name, phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);

        Bundle bundle = getArguments();
        if(bundle != null){
            email.setText(bundle.getString("email"));
            name.setText(bundle.getString("name"));
            phone.setText(bundle.getString("phone"));
        }
        return view;
    }

}
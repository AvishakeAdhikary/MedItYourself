package com.example.med_it_yourself;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {


    private Button mLoginButton;
    private EditText mLoginUserEmail,mLoginPassword;
    private TextView mLoginSwitchText;
    private SwitchMaterial mLoginSwitch;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.activity_login, container, false);
        mLoginButton = (Button) rootView.findViewById(R.id.login);
        mLoginUserEmail = (EditText) rootView.findViewById(R.id.username_useredit);
        mLoginPassword = (EditText) rootView.findViewById(R.id.password_login);
        mLoginSwitch = (SwitchMaterial) rootView.findViewById(R.id.login_toggle);
        mLoginSwitchText = (TextView) rootView.findViewById(R.id.switchtext_login);
        mAuth = FirebaseAuth.getInstance();

        mLoginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    mLoginSwitchText.setText(R.string.provider);
                }
                else
                {
                    mLoginSwitchText.setText(R.string.user);
                }
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    if (mLoginSwitch.isChecked() == false)
                    {
                        Intent i = new Intent(getActivity(), UserActionPicker.class);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(getActivity(), ProviderActionPicker.class);
                        startActivity(i);
                    }
                    return;
                }
            }
        };

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = mLoginUserEmail.getText().toString().trim();
                String password = mLoginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(user_email)) {
                    mLoginUserEmail.setError("Username/Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mLoginPassword.setError("Password is required");
                    return;
                }

                if(password.length()<6)
                {
                    mLoginPassword.setError("Password must be more than 5 characters");
                    return;
                }

                if (mLoginSwitch.isChecked() == false)
                {
                    mAuth.signInWithEmailAndPassword(user_email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getActivity(), "Login error as Customer", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(user_email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getActivity(), "Login error as Provider", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
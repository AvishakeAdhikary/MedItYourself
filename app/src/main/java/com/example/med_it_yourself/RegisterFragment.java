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

public class RegisterFragment extends Fragment {

    private Button mRegisterButton;
    private EditText mRegisterEmail,mRegisterUsername,mRegisterPassword;
    private TextView mRegisterSwitchText;
    private SwitchMaterial mRegisterSwitch;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_register, container, false);
        mRegisterButton = (Button) rootView.findViewById(R.id.register);
        mRegisterEmail = (EditText) rootView.findViewById(R.id.register_email);
        mRegisterPassword = (EditText) rootView.findViewById(R.id.register_password);
        mRegisterSwitch = (SwitchMaterial) rootView.findViewById(R.id.register_toggle);
        mRegisterSwitchText = (TextView) rootView.findViewById(R.id.register_switchtext);
        mAuth = FirebaseAuth.getInstance();

        mRegisterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    mRegisterSwitchText.setText(R.string.provider);
                }
                else
                {
                    mRegisterSwitchText.setText(R.string.user);
                }
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    Toast.makeText(getContext(), "You can now login to your account", Toast.LENGTH_LONG).show();
                }
            }
        };

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mRegisterEmail.getText().toString().trim();
                String password = mRegisterPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mRegisterEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mRegisterPassword.setError("Password is required");
                    return;
                }

                if(password.length()<6)
                {
                    mRegisterPassword.setError("Password must be more than 5 characters");
                    return;
                }

                if(mRegisterSwitch.isChecked()==false)
                {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(getActivity(), "Register error as customers", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                current_user_db.setValue(true);
                            }
                        }
                    });
                    Toast.makeText(getContext(), "You are now registered as a customer", Toast.LENGTH_LONG).show();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(getActivity(), "Register error as providers", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Providers").child(user_id);
                                current_user_db.setValue(true);
                            }
                        }
                    });
                    Toast.makeText(getContext(), "You are now registered as a provider", Toast.LENGTH_LONG).show();
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
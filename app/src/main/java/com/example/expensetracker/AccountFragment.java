package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private Button buttonInitiatePasswordReset;
    private Button buttonResetPassword;
    private Button buttonInitiateUsernameChange;
    private Button buttonChangeUsername;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        Button buttonAccountInfo = view.findViewById(R.id.buttonAccountInfo);
        TextView emailInfo = view.findViewById(R.id.emailInfo);
        TextView usernameInfo = view.findViewById(R.id.usernameInfo);
        EditText newUsernameInput = view.findViewById(R.id.newUsernameInput);
        EditText confirmNewUsernameInput = view.findViewById(R.id.confirmNewUsernameInput);
        EditText oldPasswordInput = view.findViewById(R.id.oldPasswordInput);
        EditText newPasswordInput = view.findViewById(R.id.newPasswordInput);
        EditText confirmNewPasswordInput = view.findViewById(R.id.confirmNewPasswordInput);
        buttonInitiateUsernameChange = view.findViewById(R.id.buttonInitiateUsernameChange);
        buttonChangeUsername = view.findViewById(R.id.buttonUsernameChange);
        buttonInitiatePasswordReset = view.findViewById(R.id.buttonInitiatePasswordReset);
        buttonResetPassword = view.findViewById(R.id.buttonResetPassword);
        buttonAccountInfo.setOnClickListener(v -> {
            emailInfo.setText("Email: " + user.getEmail());
            emailInfo.setVisibility(View.VISIBLE);
            usernameInfo.setText("Username: " + user.getDisplayName());
            usernameInfo.setVisibility(View.VISIBLE);
            buttonInitiateUsernameChange.setVisibility(View.VISIBLE);
            buttonInitiateUsernameChange.setOnClickListener(v2 -> {
                newUsernameInput.setVisibility(View.VISIBLE);
                confirmNewUsernameInput.setVisibility(View.VISIBLE);
                buttonChangeUsername.setVisibility(View.VISIBLE);
                buttonInitiateUsernameChange.setVisibility(View.GONE);
                buttonChangeUsername.setOnClickListener(v22 -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(newUsernameInput.getText().toString().equals(confirmNewUsernameInput.getText().toString())) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newUsernameInput.getText().toString()).build();
                        assert user != null;
                        user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // Display a message indicating that the display name has been set
                                Toast.makeText(getActivity(), "Username changed successfully!", Toast.LENGTH_SHORT).show();
                                newUsernameInput.setVisibility(View.GONE);
                                confirmNewUsernameInput.setVisibility(View.GONE);
                                buttonChangeUsername.setVisibility(View.GONE);
                                buttonInitiateUsernameChange.setVisibility(View.VISIBLE);
                                usernameInfo.setText("Username: " + user.getDisplayName());
                            } else {
                                // Display a message if setting the display name fails
                                Toast.makeText(getActivity(), "Failed to change username!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(getActivity(), "Usernames must match!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            buttonInitiatePasswordReset.setVisibility(View.VISIBLE);
            buttonInitiatePasswordReset.setOnClickListener(v1 -> {
                oldPasswordInput.setVisibility(View.VISIBLE);
                newPasswordInput.setVisibility(View.VISIBLE);
                confirmNewPasswordInput.setVisibility(View.VISIBLE);
                buttonResetPassword.setVisibility(View.VISIBLE);
                buttonResetPassword.setOnClickListener(v11 -> mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), oldPasswordInput.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (newPasswordInput.getText().toString().equals(confirmNewPasswordInput.getText().toString()) && newPasswordInput.getText().toString().length() >= 6 && !newPasswordInput.getText().toString().equals(oldPasswordInput.getText().toString())) {
                            user.updatePassword(newPasswordInput.getText().toString());
                            mAuth.signOut();
                            Toast.makeText(getActivity(), "Updating Password Successful! Please Login Using New Password!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(requireActivity(), ActivityLogin.class));
                        }
                        else if (newPasswordInput.getText().toString().equals(oldPasswordInput.getText().toString())) {
                            Toast.makeText(getActivity(), "Updating Password Failed! Please ensure you choose a new password different from your old one!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Updating Password Failed! Please ensure the new passwords match and are of valid length!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Updating Password Failed! Please check credentials.", Toast.LENGTH_LONG).show();
                    }
                }));
            });
        });
        // Inflate the layout for this fragment
        return view;
    }

}
package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    private Button buttonInitiateUsernameChange;
    private RelativeLayout relativelayout;
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
        relativelayout = view.findViewById(R.id.relativelayout);
        buttonInitiateUsernameChange = view.findViewById(R.id.buttonInitiateUsernameChange);
        buttonInitiatePasswordReset = view.findViewById(R.id.buttonInitiatePasswordReset);
        buttonAccountInfo.setOnClickListener(v -> {
            emailInfo.setText("Email: " + user.getEmail());
            emailInfo.setVisibility(View.VISIBLE);
            usernameInfo.setText("Username: " + user.getDisplayName());
            usernameInfo.setVisibility(View.VISIBLE);
            buttonInitiateUsernameChange.setVisibility(View.VISIBLE);
            relativelayout.setVisibility(View.VISIBLE);
            buttonInitiateUsernameChange.setOnClickListener(v2 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = getLayoutInflater().inflate(R.layout.change_username_dialog, null);
                EditText confirmUsernameEditText = dialogView.findViewById(R.id.confirmUsernameEditText);
                EditText newUsernameEditText = dialogView.findViewById(R.id.newUsernameEditText);
                builder.setView(dialogView);
                builder.setTitle("Change Username");

                builder.setPositiveButton("Update", null);
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(dialogInterface -> {
                    Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    updateButton.setOnClickListener(v1 -> {
                        String newUsername = newUsernameEditText.getText().toString();
                        String confirmUsername = confirmUsernameEditText.getText().toString();

                        if (newUsername.isEmpty()) {
                            newUsernameEditText.setError("This field cannot be empty");
                            newUsernameEditText.requestFocus();
                            return;
                        }
                        if (confirmUsername.isEmpty()) {
                            confirmUsernameEditText.setError("This field cannot be empty");
                            confirmUsernameEditText.requestFocus();
                            return;
                        }

                        if (!newUsername.equals(confirmUsername)) {
                            Toast.makeText(getActivity(), "Usernames must match!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newUsername).build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Username changed successfully!", Toast.LENGTH_SHORT).show();
                                    usernameInfo.setText("Username: " + user.getDisplayName());
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to change username!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                });

                dialog.show();
            });

            buttonInitiatePasswordReset.setVisibility(View.VISIBLE);
            buttonInitiatePasswordReset.setOnClickListener(v1 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = getLayoutInflater().inflate(R.layout.change_password_dialog, null);
                EditText oldPasswordEditText = dialogView.findViewById(R.id.oldPasswordEditText);
                EditText newPasswordEditText = dialogView.findViewById(R.id.newPasswordEditText);
                EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPasswordEditText);
                builder.setView(dialogView);
                builder.setTitle("Change Password");

                builder.setPositiveButton("Update", null);
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(dialogInterface -> {
                    Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    updateButton.setOnClickListener(view1 -> {
                        //boolean isValid = true;

                        if (oldPasswordEditText.getText().toString().trim().isEmpty()) {
                            oldPasswordEditText.setError("This field cannot be empty");
                            oldPasswordEditText.requestFocus();
                            return;
                        }

                        if (newPasswordEditText.getText().toString().trim().isEmpty()) {
                            newPasswordEditText.setError("This field cannot be empty");
                            newPasswordEditText.requestFocus();
                            return;
                        }

                        if (confirmPasswordEditText.getText().toString().trim().isEmpty()) {
                            confirmPasswordEditText.setError("This field cannot be empty");
                            confirmPasswordEditText.requestFocus();
                            return;
                        }

                        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), oldPasswordEditText.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString()) && newPasswordEditText.getText().toString().length() >= 6 && !newPasswordEditText.getText().toString().equals(oldPasswordEditText.getText().toString())) {
                                    user.updatePassword(newPasswordEditText.getText().toString());
                                    mAuth.signOut();
                                    Toast.makeText(getActivity(), "Updating Password Successful! Please Login Using New Password!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(requireActivity(), ActivityLogin.class));
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Ensure your new password is different, matches confirmation, and is at least 6 characters long.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Failed to authenticate. Please check your current password.", Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                });

                dialog.show();
            });
        });
        // Inflate the layout for this fragment
        return view;
    }
}
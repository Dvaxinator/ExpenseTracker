package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private EditText userEmail;
    private EditText userPassword;
    private EditText forgottenPassword;
    private Button logInBtn;
    private Button signUpBtn;
    private Button forgotPasswordBtn;
    private Button initiateForgottenPasswordBtn;
    private Button returnBtn;
    private FirebaseAuth mAuth;

    // Goes to the UserMenu activity if the user is already logged in and their email is verified
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            startActivity(new Intent(ActivityLogin.this, UserMenu.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        logInBtn = findViewById(R.id.buttonLogin);
        userEmail = findViewById(R.id.editTextUsername);
        userPassword = findViewById(R.id.editTextPassword);
        forgottenPassword = findViewById(R.id.emailForForgottenPassword);
        initiateForgottenPasswordBtn = findViewById(R.id.initiateForgottenPassword);
        signUpBtn = findViewById(R.id.buttonSignUp);
        forgotPasswordBtn = findViewById(R.id.buttonForgotPassword);
        returnBtn = findViewById(R.id.buttonReturn);
        mAuth = FirebaseAuth.getInstance();
        forgotPasswordBtn.setOnClickListener(view -> forgotPassword());
        initiateForgottenPasswordBtn.setOnClickListener(view -> initiateForgottenPassword());
        logInBtn.setOnClickListener(view -> userLogin());
        signUpBtn.setOnClickListener(view -> launchRegistration());
        returnBtn.setOnClickListener(view -> returnToLogin());
    }

    public void returnToLogin() {
        logInBtn.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(View.VISIBLE);
        userEmail.setVisibility(View.VISIBLE);
        userPassword.setVisibility(View.VISIBLE);
        forgotPasswordBtn.setVisibility(View.VISIBLE);
        returnBtn.setVisibility(View.GONE);
        forgottenPassword.setVisibility(View.GONE);
        initiateForgottenPasswordBtn.setVisibility(View.GONE);
        startActivity(new Intent(ActivityLogin.this, ActivityLogin.class));
    }
    public void forgotPassword() {
        logInBtn.setVisibility(View.GONE);
        signUpBtn.setVisibility(View.GONE);
        userEmail.setVisibility(View.GONE);
        userPassword.setVisibility(View.GONE);
        forgotPasswordBtn.setVisibility(View.GONE);
        returnBtn.setVisibility(View.VISIBLE);
        forgottenPassword.setVisibility(View.VISIBLE);
        initiateForgottenPasswordBtn.setVisibility(View.VISIBLE);
    }
    public void initiateForgottenPassword() {
        String email = forgottenPassword.getText().toString(); // Get the user's email or prompt the user to enter their email

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                logInBtn.setVisibility(View.VISIBLE);
                signUpBtn.setVisibility(View.VISIBLE);
                userEmail.setVisibility(View.VISIBLE);
                userPassword.setVisibility(View.VISIBLE);
                forgotPasswordBtn.setVisibility(View.VISIBLE);
                returnBtn.setVisibility(View.GONE);
                forgottenPassword.setVisibility(View.GONE);
                initiateForgottenPasswordBtn.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchRegistration() {
        Intent registerIntent = new Intent(ActivityLogin.this, SignUpActivity.class);
        startActivity(registerIntent);
    }

    private void userLogin() {

        /*/
        ensuring correct information is added for log in
         */
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (email.isEmpty()) {
            userEmail.setError("Please enter an e-mail address!");
            userEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter a valid e-mail address!");
            userEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            userPassword.setError("Please enter a password!");
            userPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            userPassword.setError("Minimum password length is 6 characters!");
            userPassword.requestFocus();
            return;
        }
        if (email == null || password == null) {
            System.out.println("FLAG");
            return;
        }
        /*/
        validating user login task using fire base authenticator
         */

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    startActivity(new Intent(ActivityLogin.this, UserMenu.class));
                    finish();
                } else {
                    Toast.makeText(ActivityLogin.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ActivityLogin.this, "Login Failed!, Please check credentials.", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_login);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
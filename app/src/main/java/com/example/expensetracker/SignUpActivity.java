package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextUsername;
    private Button buttonSignUp;
    private Button buttonLogIn;

    // firebase instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonSignUp = findViewById(R.id.buttonSignUpPage);
        buttonLogIn = findViewById(R.id.buttonLogIn);

        buttonSignUp.setOnClickListener(view -> signUp());
        buttonLogIn.setOnClickListener(view -> goToLogin());
    }

    private void goToLogin() {
        Intent loginPageIntent = new Intent(SignUpActivity.this, ActivityLogin.class);
        startActivity(loginPageIntent);
        finish();
    }

    private void signUp() {
        // Retrieve user input
        String firstName = editTextFirstName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password constraints
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Continue with sign-up process
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, display a message to the user
                Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();

                // Get the current authenticated user
                FirebaseUser user = mAuth.getCurrentUser();

                // Create a new Intent to start UserMenuActivity
                Intent intent = new Intent(SignUpActivity.this, UserMenu.class);

                // Pass the user's email as an extra
                assert user != null;
                intent.putExtra("userEmail", user.getEmail());

                // You could pass other user details here as needed

                // Start the UserMenuActivity
                startActivity(intent);

                // Optionally, if you want to finish the SignUpActivity
                finish();

            } else {
                // If sign in fails, display a message to the user
                Toast.makeText(SignUpActivity.this, "Sign-up failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
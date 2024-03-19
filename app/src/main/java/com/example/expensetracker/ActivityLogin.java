package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
        super.onCreate(savedInstanceState);
        com.example.expensetracker.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button logInBtn = findViewById(R.id.buttonLogin);
        userEmail = findViewById(R.id.editTextUsername);
        userPassword = findViewById(R.id.editTextPassword);
        Button signUpBtn = findViewById(R.id.buttonSignUp);
        mAuth = FirebaseAuth.getInstance();
        logInBtn.setOnClickListener(view -> userLogin());
        signUpBtn.setOnClickListener(view -> launchRegistration());
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
            userEmail.setError("Please enter e-mail");
            userEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter valid e-mail");
            userEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            userPassword.setError("Please enter password");
            userPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            userPassword.setError("minimum password length is 6 characters");
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
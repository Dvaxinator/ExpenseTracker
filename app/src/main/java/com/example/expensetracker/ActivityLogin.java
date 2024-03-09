package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.databinding.ActivityLoginBinding;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginBinding binding;
    private Button LogInBtn;
    private Button SignUpBtn;
    private EditText userEmail;
    private EditText userPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LogInBtn = findViewById(R.id.buttonLogin);
        userEmail = findViewById(R.id.editTextUsername);
        userPassword = findViewById(R.id.editTextPassword);
        SignUpBtn = findViewById(R.id.buttonSignUp);
        mAuth = FirebaseAuth.getInstance();
        LogInBtn.setOnClickListener(view -> userLogin());
        SignUpBtn.setOnClickListener(view -> launchRegistration());
    }

    private void launchRegistration(){
        Intent registerIntent = new Intent(ActivityLogin.this, SignUpActivity.class);
        startActivity(registerIntent);
    }

    private void userLogin() {

        /*/
        ensuring correct information is added for log in
         */
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if(email.isEmpty()){
            userEmail.setError("Please enter e-mail");
            userEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("Please enter valid e-mail");
            userEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            userPassword.setError("Please enter password");
            userPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            userPassword.setError("minimum password length is 6 characters");
            userPassword.requestFocus();
            return;
        }
        if(email == null || password == null){
            System.out.println("FLAG");
            return;
        }
        /*/
        validating user login task using fire base authenticator
         */
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // redirect to App Home Page!! (poll part!)      // creating empty activity for now!!
                    startActivity(new Intent(ActivityLogin.this, UserMenu.class));
                }else {
                    Toast.makeText(ActivityLogin.this, "Login Failed!, Please check credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_login);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
package com.nsoft.nchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.nsoft.nchat.databinding.ActivitySignInBinding;

import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MyMethodsClass myMethodsClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize here -------------------------------------------------------------------------
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        // -----------------------------------------------------------------------------------------
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());                                  // my code -------------
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        // my code starts here ---------------------------------------------------------------------
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        myMethodsClass = new MyMethodsClass(getApplicationContext());

        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_colour));
        }


        binding.signUpTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignupActivity.class));
        });

        binding.signInButton.setOnClickListener(v -> {
            String emailString = binding.emailEditText.getText().toString().trim();
            String passwordString = binding.passwordEditText.getText().toString().trim();

            if (emailString.isEmpty() || passwordString.isEmpty()) {
                Toast.makeText(this, "Please fill all boxes!", Toast.LENGTH_SHORT).show();
            } else {
                myMethodsClass.loginPostRequest(emailString, passwordString, new MyMethodsClass.ResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        boolean status = Boolean.parseBoolean(jsonObject.optString("status", "false"));
                        if (status) {
                            String user_id = jsonObject.optString("user_id", "Null");

                            Toast.makeText(SignInActivity.this, user_id, Toast.LENGTH_SHORT).show();
                            editor.putBoolean("is_logged_in", true).apply();
                            editor.putString("user_id", user_id).apply();

                            startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(SignInActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }   // on create ends here ---------------------------------------------------------------------


}   // main class ends here ------------------------------------------------------------------------
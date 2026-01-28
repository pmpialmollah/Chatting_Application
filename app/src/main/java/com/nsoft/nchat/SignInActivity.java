package com.nsoft.nchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());                                  // my code -------------
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // my code starts here ---------------------------------------------------------------------
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        myMethodsClass = new MyMethodsClass(getApplicationContext());

        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn){
            startActivity(new Intent(this, ChatActivity.class));
            finish();
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
                        String response = jsonObject.optString("response", "Null");

                        Toast.makeText(SignInActivity.this, response, Toast.LENGTH_SHORT).show();
                        if (status){
                            editor.putBoolean("is_logged_in", true).apply();
                            startActivity(new Intent(SignInActivity.this, ChatActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(SignInActivity.this, response, Toast.LENGTH_SHORT).show();
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
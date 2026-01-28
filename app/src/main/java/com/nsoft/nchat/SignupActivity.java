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
import com.nsoft.nchat.databinding.ActivitySignupBinding;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private MyMethodsClass myMethodsClass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());       // my code -------------

        setContentView(binding.getRoot());                                  // my code -------------
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // my code starts here ---------------------------------------------------------------------
        myMethodsClass = new MyMethodsClass(getApplicationContext());
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        binding.signInTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
        });

        binding.signUpButton.setOnClickListener(v -> {
            String name = binding.nameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String userId = myMethodsClass.generateUniqueId(name);
                myMethodsClass.accountCreatePostRequest(name, email, password, userId, new MyMethodsClass.ResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        if (jsonObject != null) {
                            boolean status = Boolean.parseBoolean(jsonObject.optString("status", "false"));
                            String response = jsonObject.optString("response");

                            Toast.makeText(SignupActivity.this, response, Toast.LENGTH_SHORT).show();

                            if (status) {
                                editor.putString("user_id", userId).apply();
                                startActivity(new Intent(SignupActivity.this, ChatActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(SignupActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }   // on create ends here ---------------------------------------------------------------------

}   // main class ends here ------------------------------------------------------------------------
package com.nsoft.nchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nsoft.nchat.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize here -------------------------------------------------------------------------
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        Boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
            finish();
            return;
        }

        binding.joinButton.setOnClickListener(v -> {
            String emailString = binding.emailEditText.getText().toString().trim();
            String nameString = binding.nameEditText.getText().toString().trim();

            if (emailString.isEmpty() || nameString.isEmpty()) {
                Toast.makeText(this, "Please fill all boxes!", Toast.LENGTH_SHORT).show();
            } else {
//                editor.putBoolean("isLoggedIn", true).apply();

//                startActivity(new Intent(MainActivity.this, ChatActivity.class));
//                finish();

                String userId = generateUniqueId(nameString);

                jsonObjectPostRequest(nameString, emailString, userId, new ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        boolean status = jsonObject.optBoolean("status");
                        String response = jsonObject.optString("response");

                        if (status && !response.isEmpty()) {
                            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                            editor.putBoolean("isLoggedIn", true).apply();
                            startActivity(new Intent(MainActivity.this, ChatActivity.class));
                            finish();
                        }
                    }
                    @Override
                    public void onError(String error) {
                        if (error != null && !error.isEmpty()) {
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }   // on create ends here ---------------------------------------------------------------------

    private String generateUniqueId(String name) {
        String[] nameList = name.split(" ");
        String firstName = nameList[0];
        Random random = new Random();
        String result = firstName.toLowerCase() + "_" + (random.nextInt(9000) + 1000);
        return result;
    }

    public interface ApiCallback {
        void onSuccess(JSONObject jsonObject);

        void onError(String error);
    }

    private void jsonObjectPostRequest(String name, String email, String userId, ApiCallback callback) {
        String url = "https://backend.nsoftcompany.xyz/add";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    callback.onSuccess(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError("Status code: " + volleyError.networkResponse.statusCode);

                Log.d("TAG", "onErrorResponse: " + volleyError.toString());
            }
        })
//        {
//            @Override
//            public Map<String, String> getHeaders(){
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        }
        ;

        requestQueue.add(jsonObjectRequest);
    }

}   // main class ends here ------------------------------------------------------------------------
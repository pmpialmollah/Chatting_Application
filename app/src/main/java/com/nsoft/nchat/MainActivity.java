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

import com.nsoft.nchat.databinding.ActivityMainBinding;

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
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String name = sharedPreferences.getString("name", "");

        if (!name.isEmpty()) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
            finish();
        }

        binding.joinButton.setOnClickListener(v -> {
            String nameString = binding.nameEditText.getText().toString().trim();
            if (nameString.isEmpty()) {
                Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show();
            } else {
                editor.putString("name", nameString);
                editor.commit();

                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                finish();
            }
        });

    }   // on create ends here ---------------------------------------------------------------------

}   // main class ends here ------------------------------------------------------------------------
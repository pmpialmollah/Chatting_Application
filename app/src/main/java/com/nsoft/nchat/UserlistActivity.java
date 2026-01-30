package com.nsoft.nchat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nsoft.nchat.databinding.ActivityUserlistBinding;

public class UserlistActivity extends AppCompatActivity {
    private ActivityUserlistBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityUserlistBinding.inflate(getLayoutInflater());         // my code ---------
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        // my code starts here ---------------------------------------------------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_colour));
        }

    }   // my code ---------------------------------------------------------------------------------

}   // my code -------------------------------------------------------------------------------------
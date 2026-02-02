package com.nsoft.nchat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nsoft.nchat.databinding.ActivityDashboardBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private MyMethodsClass myMethodsClass;
    private String userId;
    private List<String> receiverList;

    private String TAG = "MyFirebaseMsgService";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());        // my code ---------
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());                                      // my code ---------
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        // my code starts here ---------------------------------------------------------------------
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        myMethodsClass = new MyMethodsClass(getApplicationContext());
        receiverList = new ArrayList<>();

        userId = sharedPreferences.getString("user_id", "null");

        askNotificationPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_colour));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DashboardFragment()).commit();

        binding.navBar.setOnNavigationItemSelectedListener(menuItem -> {
            int menuId = menuItem.getItemId();
            if (menuId == R.id.navHome){
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DashboardFragment()).commit();
                return  true;
            } else if (menuId == R.id.navUsers) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UsersFragment()).commit();
                return true;
            } else if (menuId == R.id.navTools) {
                Toast.makeText(this, "Nav tools", Toast.LENGTH_SHORT).show();
                return true;
            }
            else {
                Toast.makeText(this, "No item", Toast.LENGTH_SHORT).show();
                return  false;
            }
        });

    }   // on create ends here ---------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();

    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}   // main class ends here ------------------------------------------------------------------------
package com.smartshanta.smartshanta.login;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.smartshanta.smartshanta.MainActivity;
import com.smartshanta.smartshanta.R;

public class LoginActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private final static int SPLASH_DISPLAY_LENGTH = 2000;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginFragment = new LoginFragment();

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
        fragmentTransaction.commit();
        // New Handler to start the MainActivity and close this SplashScreen after some seconds.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                        /* Create an Intent that will start the MainActivity. */
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                //To prevent the user from using the back button to go back to the Login activity
                // you have to finish() the activity after starting a new one.
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}

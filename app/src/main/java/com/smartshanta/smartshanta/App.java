package com.smartshanta.smartshanta;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.smartshanta.smartshanta.util.Constants;

/**
 * Created by OMAR on 25/01/2017.
 */
public class App extends MultiDexApplication {

    // create or get shared preferences unique file:
    private SharedPreferences sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();

        // shared pref
        sharedPref = getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        // create keys for loginName and loginPassword, first check if they created already,:
        SharedPreferences.Editor editor = sharedPref.edit(); // request editing shared pref file

        if (sharedPref.getString("userID", "null").equals("null")) // create new key
            editor.putString("userID", "null");
        editor.apply(); // save

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
